package com.cpm.upload;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.net.MalformedURLException;
import java.util.ArrayList;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;
import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;

import com.cpm.Constants.CommonString;

import com.cpm.database.GSKMTDatabase;
import com.cpm.delegates.CoverageBean;
import com.cpm.delegates.GeotaggingBeans;
import com.cpm.delegates.PromotionBean;
import com.cpm.delegates.ShelfVisibilityBean;
import com.cpm.delegates.SkuBean;
import com.cpm.delegates.StoreBean;
import com.cpm.delegates.TOTBean;
import com.cpm.message.AlertMessage;
import com.cpm.xmlGetterSetter.FailureGetterSetter;
import com.cpm.xmlHandler.FailureXMLHandler;

import com.crashlytics.android.Crashlytics;
import com.example.gsk_mtt.R;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;

import android.view.WindowManager;
import android.widget.ProgressBar;
import android.widget.TextView;

public class UploadImageActivity extends Activity {
    private Dialog dialog;
    private ProgressBar pb;
    private TextView percentage, message;
    private String visit_date;
    private SharedPreferences preferences;
    private GSKMTDatabase database;
    private int factor, k;
    String result, username, cate_id;
    String storename = "";
    String errormsg = "";
    static int counter = 1;
    private ArrayList<CoverageBean> coverageBeanlist = new ArrayList<CoverageBean>();
    ArrayList<SkuBean> stockimages = new ArrayList<SkuBean>();
    ArrayList<TOTBean> beforetotData = new ArrayList<TOTBean>();
    ArrayList<TOTBean> afterTOTData = new ArrayList<TOTBean>();
    ArrayList<SkuBean> beforeaddtionalData = new ArrayList<SkuBean>();
    ArrayList<SkuBean> afterAddaitionalData = new ArrayList<SkuBean>();
    ArrayList<SkuBean> compData = new ArrayList<SkuBean>();
    ArrayList<GeotaggingBeans> geotaglist = new ArrayList<GeotaggingBeans>();
    ArrayList<ShelfVisibilityBean> shelfdata = new ArrayList<ShelfVisibilityBean>();
    StoreBean storestatus = new StoreBean();
    ArrayList<PromotionBean> promotionData = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.upload_option);
        preferences = PreferenceManager.getDefaultSharedPreferences(this);
        visit_date = preferences.getString(CommonString.KEY_DATE, null);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        username = preferences.getString(CommonString.KEY_USERNAME, null);
        cate_id = preferences.getString(CommonString.KEY_CATEGORY_ID, null);
        database = new GSKMTDatabase(this);
        database.open();
        new UploadTask(this).execute();

    }

    private class UploadTask extends AsyncTask<Void, Void, String> {
        private Context context;

        UploadTask(Context context) {
            this.context = context;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog = new Dialog(context);
            dialog.setContentView(R.layout.custom);
            dialog.setTitle("Uploading Images");
            dialog.setCancelable(false);
            dialog.show();
            pb = (ProgressBar) dialog.findViewById(R.id.progressBar1);
            percentage = (TextView) dialog.findViewById(R.id.percentage);
            message = (TextView) dialog.findViewById(R.id.message);
            final TextView tv_title = dialog.findViewById(R.id.tv_title);
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    tv_title.setText("Uploading Images");
                }
            });
        }

        @Override
        protected String doInBackground(Void... params) {
            // TODO Auto-generated method stub
            try {
                database.open();
                coverageBeanlist = database.getCoverageData(visit_date, null, null);
                geotaglist = database.getGeotaggingData(CommonString.KEY_D);
                if (coverageBeanlist.size() > 0 || geotaglist.size() > 0) {
                    if (coverageBeanlist.size() == 1 && geotaglist.size() == 0) {
                        factor = 50;
                    } else {
                        factor = 100 / (coverageBeanlist.size() + geotaglist.size());
                    }
                }

                for (int i = 0; i < coverageBeanlist.size(); i++) {
                    if (coverageBeanlist.get(i).getStatus().equalsIgnoreCase(CommonString.KEY_D)) {
                        database.open();
                        storestatus = database.getStoreStatus(coverageBeanlist.get(i).getStoreId(), coverageBeanlist.get(i).getProcess_id());
                        if (storestatus.getUPLOAD_STATUS().equalsIgnoreCase(CommonString.STORE_STATUS_LEAVE) ||
                                storestatus.getCHECKOUT_STATUS().equalsIgnoreCase(CommonString.STORE_STATUS_LEAVE)
                                || storestatus.getCHECKOUT_STATUS().equalsIgnoreCase(CommonString.KEY_C)
                                || storestatus.getUPLOAD_STATUS().equalsIgnoreCase(CommonString.KEY_D)
                                || storestatus.getCHECKOUT_STATUS().equalsIgnoreCase(CommonString.KEY_D)) {

                            runOnUiThread(new Runnable() {

                                public void run() {
                                    k = k + factor;
                                    pb.setProgress(k);
                                    percentage.setText(k + "%");
                                    message.setText(storename + " Images");
                                }
                            });

                            if (coverageBeanlist.get(i).getImage() != null && !coverageBeanlist.get(i).getImage().equals("")) {
                                if (new File(Environment.getExternalStorageDirectory() + "/MT_GSK_Images/" +
                                        coverageBeanlist.get(i).getImage()).exists()) {
                                    result = UploadPOSMImage(coverageBeanlist.get(i).getImage(), "CoverageImage");
                                    if (result.equalsIgnoreCase(CommonString.KEY_FAILURE)) {
                                        return CommonString.METHOD_Get_DR_POSM_IMAGES + "," + errormsg;
                                    }
                                    runOnUiThread(new Runnable() {

                                        public void run() {
                                            message.setText("Coverage Image Uploaded");
                                        }
                                    });
                                }
                            }
                            if (coverageBeanlist.get(i).getCHECKOUT_IMG() != null && !coverageBeanlist.get(i).getCHECKOUT_IMG().equals("")) {
                                if (new File(Environment.getExternalStorageDirectory() + "/MT_GSK_Images/" +
                                        coverageBeanlist.get(i).getCHECKOUT_IMG()).exists()) {
                                    result = UploadPOSMImage(coverageBeanlist.get(i).getCHECKOUT_IMG(), "CoverageImage");
                                    if (result.equalsIgnoreCase(CommonString.KEY_FAILURE)) {
                                        return CommonString.METHOD_Get_DR_POSM_IMAGES + "," + errormsg;
                                    }
                                    runOnUiThread(new Runnable() {
                                        public void run() {
                                            message.setText("Coverage Checkout Image Uploaded");
                                        }
                                    });
                                }
                            }
                            database.open();
                            // Stock Images
                            stockimages = database.getStockImagesForUpload(coverageBeanlist.get(i).getStoreId(), username, coverageBeanlist.get(i).getProcess_id());
                            for (int j = 0; j < stockimages.size(); j++) {
                                if (stockimages.get(j).getImage5() != null && !stockimages.get(j).getImage5().equals("")) {

                                    if (new File(Environment.getExternalStorageDirectory() + "/MT_GSK_Images/"
                                            + stockimages.get(j).getImage5()).exists()) {

                                        result = UploadPOSMImage(stockimages.get(j).getImage5(), "StockImage");

                                        if (result.equalsIgnoreCase(CommonString.KEY_FAILURE)) {
                                            return CommonString.METHOD_UPLOAD_PRIMARY_WINDOW_IMAGES + "," + errormsg;
                                        }

                                        runOnUiThread(new Runnable() {

                                            public void run() {
                                                // TODO Auto-generated
                                                // method
                                                // stub
                                                message.setText("Stock images Uploaded");
                                            }
                                        });
                                    }
                                }


                                if (stockimages.get(j).getImage6() != null && !stockimages.get(j).getImage6().equals("")) {
                                    if (new File(Environment.getExternalStorageDirectory() + "/MT_GSK_Images/" + stockimages.get(j).getImage6()).exists()) {
                                        result = UploadPOSMImage(stockimages.get(j).getImage6(), "StockImage");
                                        if (result.equalsIgnoreCase(CommonString.KEY_FAILURE)) {
                                            return CommonString.METHOD_UPLOAD_PRIMARY_WINDOW_IMAGES + "," + errormsg;
                                        }

                                        runOnUiThread(new Runnable() {

                                            public void run() {
                                                // TODO Auto-generated
                                                // method
                                                // stub

                                                message.setText("Stock images Uploaded");
                                            }
                                        });
                                    }
                                }


                                if (stockimages.get(j).getImage7() != null && !stockimages.get(j).getImage7().equals("")) {
                                    if (new File(Environment.getExternalStorageDirectory() + "/MT_GSK_Images/" + stockimages.get(j).getImage7()).exists()) {
                                        result = UploadPOSMImage(stockimages.get(j).getImage7(), "StockImage");
                                        if (result.equalsIgnoreCase(CommonString.KEY_FAILURE)) {

                                            return CommonString.METHOD_UPLOAD_PRIMARY_WINDOW_IMAGES
                                                    + "," + errormsg;
                                        }

                                        runOnUiThread(new Runnable() {

                                            public void run() {
                                                // TODO Auto-generated
                                                // method
                                                // stub

                                                message.setText("Stock images Uploaded");
                                            }
                                        });
                                    }
                                }


                                if (stockimages.get(j).getImage8() != null && !stockimages.get(j).getImage8().equals("")) {
                                    if (new File(Environment.getExternalStorageDirectory() + "/MT_GSK_Images/" + stockimages.get(j).getImage8()).exists()) {

                                        result = UploadPOSMImage(stockimages.get(j).getImage8(), "StockImage");
                                        if (result.equalsIgnoreCase(CommonString.KEY_FAILURE)) {
                                            return CommonString.METHOD_UPLOAD_PRIMARY_WINDOW_IMAGES + "," + errormsg;
                                        }

                                        runOnUiThread(new Runnable() {

                                            public void run() {
                                                // TODO Auto-generated
                                                // method
                                                // stub

                                                message.setText("Stock images Uploaded");
                                            }
                                        });
                                    }
                                }

                            }


                            // Stock Shelf Images
                            database.open();
                            shelfdata = database.getInsertedShelfFacingStockDataForUpload(coverageBeanlist.get(i).getStoreId(),
                                    coverageBeanlist.get(i).getProcess_id());


                            for (int j = 0; j < shelfdata.size(); j++) {
                                if (shelfdata.get(j).getImage() != null && !shelfdata.get(j).getImage().equals("")) {

                                    if (new File(Environment.getExternalStorageDirectory() + "/MT_GSK_Images/"
                                            + shelfdata.get(j).getImage()).exists()) {

                                        result = UploadPOSMImage(shelfdata.get(j).getImage(), "ShelfImages");

                                        if (result.equalsIgnoreCase(CommonString.KEY_FAILURE)) {

                                            return CommonString.METHOD_UPLOAD_PRIMARY_WINDOW_IMAGES
                                                    + "," + errormsg;
                                        }

                                        runOnUiThread(new Runnable() {

                                            public void run() {
                                                // TODO Auto-generated
                                                // method
                                                // stub
                                                message.setText("Shelf image images Uploaded");
                                            }
                                        });
                                    }
                                }


                            }

                            //Upload stock - Primary Category - Brand Image

                            ArrayList<SkuBean> sku_brand_list_second = new ArrayList<SkuBean>();

                            database.open();
                            sku_brand_list_second = database.getInsertedDisplayListAfterStockUpload(coverageBeanlist
                                    .get(i).getStoreId(), coverageBeanlist.get(i).getProcess_id());

                            for (int j = 0; j < sku_brand_list_second.size(); j++) {


                                if (sku_brand_list_second.get(j).getBrand_img() != null
                                        && !sku_brand_list_second.get(j).getBrand_img()
                                        .equals("")) {

                                    if (new File(
                                            Environment.getExternalStorageDirectory() + "/MT_GSK_Images/"

                                                    + sku_brand_list_second.get(j).getBrand_img())
                                            .exists()) {

                                        result = UploadPOSMImage(

                                                sku_brand_list_second.get(j).getBrand_img(), "PrimaryCatImages");

                                        if (result
                                                .equalsIgnoreCase(CommonString.KEY_FAILURE)) {

                                            return CommonString.METHOD_UPLOAD_PRIMARY_WINDOW_IMAGES
                                                    + "," + errormsg;
                                        }

                                        runOnUiThread(new Runnable() {

                                            public void run() {
                                                // TODO Auto-generated
                                                // method
                                                // stub

                                                message.setText("Shelf image images Uploaded");
                                            }
                                        });
                                    }
                                }


                            }

                            //End

                            //Promotion POP Images
                            database.open();
                            promotionData = database
                                    .getInsertedPromoCompliance(coverageBeanlist
                                            .get(i).getStoreId(), coverageBeanlist.get(i).getProcess_id());

                            for (int j = 0; j < promotionData.size(); j++) {


                                if (promotionData.get(j).getPop_img() != null && !promotionData.get(j).getPop_img().equals("")) {
                                    if (new File(CommonString.FILE_PATH + promotionData.get(j).getPop_img()).exists()) {
                                        result = UploadPOSMImage(promotionData.get(j).getPop_img(), "PromotionImages");
                                        if (result
                                                .equalsIgnoreCase(CommonString.KEY_FAILURE)) {

                                            return CommonString.METHOD_Upload_StoreDeviationImage
                                                    + "," + errormsg;
                                        }

                                        runOnUiThread(new Runnable() {

                                            public void run() {


                                                message.setText("POP Images Uploaded");
                                            }
                                        });
                                    }
                                }

                            }

                            //TOT Images

                            database.open();
                            beforetotData = database.getBeforeTOTDataForUpload(coverageBeanlist.get(i).getStoreId(),
                                    coverageBeanlist.get(i).getProcess_id());

                            for (int j = 0; j < beforetotData.size(); j++) {

                            }

                            database.open();
                            afterTOTData = database.getAfterTOTDataForUpload(coverageBeanlist.get(i).getStoreId(), coverageBeanlist.get(i).getProcess_id());

                            for (int j = 0; j < afterTOTData.size(); j++) {


                                if (afterTOTData.get(j).getImage1() != null
                                        && !afterTOTData.get(j).getImage1()
                                        .equals("")) {

                                    if (new File(
                                            Environment.getExternalStorageDirectory() + "/MT_GSK_Images/"

                                                    + afterTOTData.get(j).getImage1())
                                            .exists()) {

                                        result = UploadPOSMImage(afterTOTData.get(j).getImage1(), "TOTImage");
                                        if (result
                                                .equalsIgnoreCase(CommonString.KEY_FAILURE)) {

                                            return CommonString.METHOD_Upload_StoreDeviationImage
                                                    + "," + errormsg;
                                        }

                                        runOnUiThread(new Runnable() {

                                            public void run() {


                                                message.setText("After TOT image1 Uploaded");
                                            }
                                        });
                                    }
                                }


                                if (afterTOTData.get(j).getImage2() != null
                                        && !afterTOTData.get(j).getImage2()
                                        .equals("")) {

                                    if (new File(
                                            Environment.getExternalStorageDirectory() + "/MT_GSK_Images/"

                                                    + afterTOTData.get(j).getImage2())
                                            .exists()) {

                                        result = UploadPOSMImage(afterTOTData.get(j).getImage2(), "TOTImage");
                                        if (result
                                                .equalsIgnoreCase(CommonString.KEY_FAILURE)) {

                                            return CommonString.METHOD_Upload_StoreDeviationImage
                                                    + "," + errormsg;
                                        }

                                        runOnUiThread(new Runnable() {

                                            public void run() {


                                                message.setText("After TOT image 2 Uploaded");
                                            }
                                        });
                                    }
                                }


                                if (afterTOTData.get(j).getImage3() != null && !afterTOTData.get(j).getImage3().equals("")) {
                                    if (new File(Environment.getExternalStorageDirectory() + "/MT_GSK_Images/" + afterTOTData.get(j).getImage3()).exists()) {
                                        result = UploadPOSMImage(afterTOTData.get(j).getImage3(), "TOTImage");
                                        if (result.equalsIgnoreCase(CommonString.KEY_FAILURE)) {
                                            return CommonString.METHOD_Upload_StoreDeviationImage + "," + errormsg;
                                        }

                                        runOnUiThread(new Runnable() {

                                            public void run() {
                                                message.setText("After TOT image 3 Uploaded");
                                            }
                                        });
                                    }
                                }

                            }

                            database.open();
                            beforeaddtionalData = database.getProductEntryDetailForUpload(coverageBeanlist.get(i).getStoreId(), coverageBeanlist.get(i).getProcess_id());
                            for (int j = 0; j < beforeaddtionalData.size(); j++) {


                                if (beforeaddtionalData.get(j).getAdditional_image() != null
                                        && !beforeaddtionalData.get(j).getAdditional_image()
                                        .equals("")) {

                                    if (new File(
                                            Environment.getExternalStorageDirectory() + "/MT_GSK_Images/"

                                                    + beforeaddtionalData.get(j).getAdditional_image())
                                            .exists()) {

                                        result = UploadPOSMImage(beforeaddtionalData.get(j).getAdditional_image(), "AdditionalDisplayIamge");
                                        if (result
                                                .equalsIgnoreCase(CommonString.KEY_FAILURE)) {

                                            return CommonString.METHOD_Upload_StoreDeviationImage
                                                    + "," + errormsg;
                                        }

                                        runOnUiThread(new Runnable() {

                                            public void run() {


                                                message.setText("Additional Display Images Uploaded");
                                            }
                                        });
                                    }
                                }


                            }

                            database.open();
                            compData = database.getEnteredCompetitionDetailForUploading(coverageBeanlist.get(i).getStoreId(), coverageBeanlist.get(i).getProcess_id());
                            for (int j = 0; j < compData.size(); j++) {
                                if (compData.get(j).getAdditional_image() != null
                                        && !compData.get(j).getAdditional_image()
                                        .equals("")) {

                                    if (new File(
                                            Environment.getExternalStorageDirectory() + "/MT_GSK_Images/" + compData.get(j).getAdditional_image())
                                            .exists()) {
                                        result = UploadPOSMImage(compData.get(j).getAdditional_image(), "CompetitionImages");
                                        if (result
                                                .equalsIgnoreCase(CommonString.KEY_FAILURE)) {

                                            return CommonString.METHOD_Upload_StoreDeviationImage
                                                    + "," + errormsg;
                                        }
                                        runOnUiThread(new Runnable() {

                                            public void run() {
                                                message.setText("Competition Images Uploaded");
                                            }
                                        });
                                    }
                                }
                            }
                            database.open();
                            afterAddaitionalData = database.getAfterProductEntryDetail(coverageBeanlist.get(i).getStoreId());
                            for (int j = 0; j < afterAddaitionalData.size(); j++) {
                            }

                        }

                        // SET COVERAGE STATUS
                        String statusxml = "[DATA][USER_DATA][STORE_ID]"
                                + coverageBeanlist.get(i).getStoreId()
                                + "[/STORE_ID][CREATED_BY]" + username
                                + "[/CREATED_BY][VISIT_DATE]"
                                + coverageBeanlist.get(i).getVisitDate()
                                + "[/VISIT_DATE][STATUS]"
                                + CommonString.KEY_U
                                + "[/STATUS][/USER_DATA][/DATA]";

                        SoapObject request = new SoapObject(CommonString.NAMESPACE, CommonString.METHOD_SET_COVERAGE_STATUS);
                        request.addProperty("onXML", statusxml);
                        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
                        envelope.dotNet = true;
                        envelope.setOutputSoapObject(request);
                        HttpTransportSE androidHttpTransport = new HttpTransportSE(CommonString.URL);
                        androidHttpTransport.call(CommonString.SOAP_ACTION_SET_COVERAGE_STATUS, envelope);
                        Object result = (Object) envelope.getResponse();
                        if (result.toString().equalsIgnoreCase(CommonString.KEY_SUCCESS)) {
                            database.open();
                            database.deleteAllTables(coverageBeanlist.get(i).getStoreId(), coverageBeanlist.get(i).getProcess_id());
                            database.updateStoreStatusAfterImageUpload(coverageBeanlist.get(i).getStoreId(),
                                    coverageBeanlist.get(i).getVisitDate(), CommonString.KEY_U, coverageBeanlist.get(i).getProcess_id());
                        }
                        if (!result.toString().equalsIgnoreCase(CommonString.KEY_SUCCESS)) {
                        }

                    }
                }

                //for geo tag

                for (int i = 0; i < geotaglist.size(); i++) {
                    runOnUiThread(new Runnable() {

                        public void run() {
                            // TODO Auto-generated method stub
                            k = k + factor;
                            pb.setProgress(k);
                            percentage.setText(k + "%");
                            message.setText("Uploading Geotag Images...");
                        }
                    });
                    if (geotaglist.get(i).getUrl1() != null && !geotaglist.get(i).getUrl1().equals("")) {
                        if (new File(Environment.getExternalStorageDirectory() + "/MT_GSK_Images/" + geotaglist.get(i).getUrl1()).exists()) {
                            result = UploadGeoImage(geotaglist.get(i).getUrl1(), "GeoTag");
                            if (result.toString().equalsIgnoreCase(CommonString.KEY_FALSE)) {
                                return CommonString.METHOD_Get_DR_STORE_IMAGES_GEO;
                            } else if (result.equalsIgnoreCase(CommonString.KEY_FAILURE)) {
                                return CommonString.METHOD_Get_DR_STORE_IMAGES_GEO + "," + errormsg;
                            }

                            runOnUiThread(new Runnable() {

                                public void run() {
                                    // TODO Auto-generated method stub

                                    message.setText("Image1 Upload");
                                }
                            });
                        }
                    }

                }

                return CommonString.KEY_SUCCESS;
            } catch (MalformedURLException e) {
                Crashlytics.logException(e);
                final AlertMessage message = new AlertMessage(
                        UploadImageActivity.this,
                        AlertMessage.MESSAGE_EXCEPTION, "download", e);
                runOnUiThread(new Runnable() {

                    @Override
                    public void run() {
                        // TODO Auto-generated method stub
                        message.showMessage();
                    }
                });

            } catch (IOException e) {
                final AlertMessage message = new AlertMessage(
                        UploadImageActivity.this,
                        AlertMessage.MESSAGE_SOCKETEXCEPTION,
                        "socket_uploadimage", e);
                counter++;
                runOnUiThread(new Runnable() {

                    @Override
                    public void run() {
                        // TODO Auto-generated method stub
                        message.showMessage();
                    }
                });
            } catch (Exception e) {
                Crashlytics.logException(e);
                final AlertMessage message = new AlertMessage(
                        UploadImageActivity.this,
                        AlertMessage.MESSAGE_EXCEPTION, "download", e);
                runOnUiThread(new Runnable() {

                    @Override
                    public void run() {
                        // TODO Auto-generated method stub
                        message.showMessage();
                    }
                });
            }

            return "";
        }


        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            dialog.dismiss();
            if (result.equals(CommonString.KEY_SUCCESS)) {
                for (int i = 0; i < geotaglist.size(); i++) {
                    database.open();
                    database.updateGeoTagData(geotaglist.get(i).getStoreid(), CommonString.KEY_U);
                    database.updateDataStatus(geotaglist.get(i).getStoreid(), CommonString.KEY_U);
                    database.deleteGeoTagData(geotaglist.get(i).getStoreid());
                }
                database.deleteAllTables();
                AlertMessage message = new AlertMessage(UploadImageActivity.this, AlertMessage.MESSAGE_UPLOAD_IMAGE, "success", null);
                message.showMessage();
            } else if (!result.equals("")) {
                AlertMessage message = new AlertMessage(
                        UploadImageActivity.this, result, "success", null);
                message.showMessage();
            }
        }

    }

    public String UploadPOSMImage(String path, String folder) throws Exception {
        errormsg = "";
        BitmapFactory.Options o = new BitmapFactory.Options();
        o.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(Environment.getExternalStorageDirectory() + "/MT_GSK_Images/" + path, o);
        // The new size we want to scale to
        final int REQUIRED_SIZE = 1024;
        // Find the correct scale value. It should be the power of 2.
        int width_tmp = o.outWidth, height_tmp = o.outHeight;
        int scale = 1;
        while (true) {
            if (width_tmp < REQUIRED_SIZE && height_tmp < REQUIRED_SIZE)
                break;
            width_tmp /= 2.1;
            height_tmp /= 2.1;
            scale *= 2.1;
        }
        // Decode with inSampleSize
        BitmapFactory.Options o2 = new BitmapFactory.Options();
        o2.inSampleSize = scale;
        Bitmap bitmap = BitmapFactory.decodeFile(Environment.getExternalStorageDirectory() + "/MT_GSK_Images/" + path, o2);
        ByteArrayOutputStream bao = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bao);
        byte[] ba = bao.toByteArray();
        String ba1 = Base64.encodeBytes(ba);

        SoapObject request = new SoapObject(CommonString.NAMESPACE, CommonString.METHOD_Get_DR_POSM_IMAGES);

        request.addProperty("img", ba1);
        request.addProperty("name", path);
        request.addProperty("FolderName", folder);

        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
        envelope.dotNet = true;
        envelope.setOutputSoapObject(request);
        HttpTransportSE androidHttpTransport = new HttpTransportSE(CommonString.URL);
        androidHttpTransport.call(CommonString.SOAP_ACTION_Get_DR_POSM_IMAGES, envelope);
        Object result = (Object) envelope.getResponse();
        if (result.toString().equalsIgnoreCase(CommonString.KEY_SUCCESS)) {
            new File(Environment.getExternalStorageDirectory() + "/MT_GSK_Images/" + path).delete();
        } else {
            return CommonString.KEY_FAILURE;
        }
        return "";
    }

    public String UploadGeoImage(String path, String folder) throws Exception {
        errormsg = "";
        BitmapFactory.Options o = new BitmapFactory.Options();
        o.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(Environment.getExternalStorageDirectory() + "/MT_GSK_Images/" + path, o);

        // The new size we want to scale to
        final int REQUIRED_SIZE = 1024;

        // Find the correct scale value. It should be the power of 2.
        int width_tmp = o.outWidth, height_tmp = o.outHeight;
        int scale = 1;

        while (true) {
            if (width_tmp < REQUIRED_SIZE && height_tmp < REQUIRED_SIZE)
                break;
            width_tmp /= 2.1;
            height_tmp /= 2.1;
            scale *= 2.1;
        }

        // Decode with inSampleSize
        BitmapFactory.Options o2 = new BitmapFactory.Options();
        o2.inSampleSize = scale;
        Bitmap bitmap = BitmapFactory.decodeFile(Environment.getExternalStorageDirectory() + "/MT_GSK_Images/" + path, o2);

        ByteArrayOutputStream bao = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bao);
        byte[] ba = bao.toByteArray();
        String ba1 = Base64.encodeBytes(ba);

        SoapObject request = new SoapObject(CommonString.NAMESPACE,
                CommonString.METHOD_Get_DR_POSM_IMAGES);

        request.addProperty("img", ba1);
        request.addProperty("name", path);
        request.addProperty("FolderName", folder);

        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
                SoapEnvelope.VER11);
        envelope.dotNet = true;
        envelope.setOutputSoapObject(request);

        HttpTransportSE androidHttpTransport = new HttpTransportSE(
                CommonString.URL);

        androidHttpTransport.call(
                CommonString.SOAP_ACTION_Get_DR_POSM_IMAGES, envelope);
        Object result = (Object) envelope.getResponse();

        if (result.toString().equalsIgnoreCase(CommonString.KEY_SUCCESS)) {
            new File(Environment.getExternalStorageDirectory() + "/MT_GSK_Images/" + path).delete();
        } else {
            return CommonString.KEY_FAILURE;
        }

        return "";
    }

}
