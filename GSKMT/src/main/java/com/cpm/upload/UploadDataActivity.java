package com.cpm.upload;

import java.io.StringReader;
import java.util.ArrayList;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;
import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.view.WindowManager;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.cpm.Constants.CommonString;
import com.cpm.DailyEntry.DailyEntryMainMenu;


import com.cpm.database.GSKMTDatabase;

import com.cpm.delegates.CoverageBean;

import com.cpm.delegates.SkuBean;

import com.cpm.delegates.GeotaggingBeans;
import com.cpm.delegates.PromotionBean;
import com.cpm.delegates.ShelfVisibilityBean;
import com.cpm.delegates.StoreBean;
import com.cpm.delegates.TOTBean;

import com.cpm.message.AlertMessage;
import com.cpm.xmlGetterSetter.FailureGetterSetter;
import com.cpm.xmlGetterSetter.SKUGetterSetter;
import com.cpm.xmlHandler.FailureXMLHandler;
import com.crashlytics.android.Crashlytics;
import com.example.gsk_mtt.R;

public class UploadDataActivity extends Activity {
    private Dialog dialog;
    private ProgressBar pb;
    private TextView percentage, message;
    String app_ver, cate_id;
    private String visit_date, username;
    private SharedPreferences preferences;
    private GSKMTDatabase database;
    private int factor, k;
    String datacheck = "";
    String[] words;
    String validity;
    int mid;
    boolean leavetrue = false;
    ArrayList<CoverageBean> coverageBeanlist = new ArrayList<CoverageBean>();
    ArrayList<SkuBean> beforeStockData = new ArrayList<SkuBean>();
    ArrayList<SkuBean> stockImages = new ArrayList<SkuBean>();
    ArrayList<SkuBean> backRoomStockData = new ArrayList<SkuBean>();
    ArrayList<SkuBean> salesStockData = new ArrayList<SkuBean>();
    ArrayList<TOTBean> afterTOTData = new ArrayList<TOTBean>();
    ArrayList<TOTBean> complianceData = new ArrayList<TOTBean>();
    ArrayList<SkuBean> beforeaddtionalData = new ArrayList<SkuBean>();
    ArrayList<SkuBean> competitionTrackingData = new ArrayList<SkuBean>();
    ArrayList<SkuBean> afterAddaitionalData = new ArrayList<SkuBean>();
    ArrayList<PromotionBean> promotionData = new ArrayList<>();
    ArrayList<ShelfVisibilityBean> shelfData = new ArrayList<ShelfVisibilityBean>();
    ArrayList<TOTBean> stockTotdata = new ArrayList<TOTBean>();
    private FailureGetterSetter failureGetterSetter = null;
    StoreBean storestatus = new StoreBean();
    ArrayList<GeotaggingBeans> geotaglist = new ArrayList<GeotaggingBeans>();
    ArrayList<SKUGetterSetter> competitionpromotionList = new ArrayList<>();
    String sub_reason = "0";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.upload_option);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        preferences = PreferenceManager.getDefaultSharedPreferences(this);
        visit_date = preferences.getString(CommonString.KEY_DATE, null);
        username = preferences.getString(CommonString.KEY_USERNAME, null);
        cate_id = preferences.getString(CommonString.KEY_CATEGORY_ID, null);
        app_ver = preferences.getString(CommonString.KEY_VERSION, "");
        database = new GSKMTDatabase(this);
        database.open();
        try {
            new UploadTask(this).execute();
        } catch (Exception e) {
            e.printStackTrace();
            Crashlytics.logException(e);
        }
    }

    @Override
    public void onBackPressed() {
        Intent i = new Intent(this, DailyEntryMainMenu.class);
        startActivity(i);
        UploadDataActivity.this.finish();
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
            dialog.setTitle("Uploading Data");
            dialog.setCancelable(false);
            dialog.show();
            pb = (ProgressBar) dialog.findViewById(R.id.progressBar1);
            percentage = (TextView) dialog.findViewById(R.id.percentage);
            message = (TextView) dialog.findViewById(R.id.message);
            final TextView tv_title = dialog.findViewById(R.id.tv_title);
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    tv_title.setText("Uploading Data");
                }
            });
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            dialog.dismiss();
            if (result.equals(CommonString.KEY_SUCCESS)) {
                AlertMessage message = new AlertMessage(UploadDataActivity.this,
                        AlertMessage.MESSAGE_UPLOAD_DATA, "success", null);
                message.showMessage();
            } else if (!result.equals("")) {
                AlertMessage message = new AlertMessage(UploadDataActivity.this, AlertMessage.MESSAGE_ERROR
                        + result, "success", null);
                message.showMessage();
            } else {
                AlertMessage message = new AlertMessage(UploadDataActivity.this, AlertMessage.MESSAGE_SOCKETEXCEPTION + result, "success", null);
                message.showMessage();
            }

        }

        @Override
        protected String doInBackground(Void... params) {
            try {
                database.open();
                coverageBeanlist = database.getCoverageData(visit_date, null, null);
                geotaglist = database.getGeotaggingData("Y");
                if (coverageBeanlist.size() > 0 || geotaglist.size() == 0) {
                    if (coverageBeanlist.size() == 1 && geotaglist.size() == 0) {
                        factor = 50;
                    } else {
                        factor = 100 / (coverageBeanlist.size() + geotaglist.size());
                    }
                }
                SAXParserFactory saxPF = SAXParserFactory.newInstance();
                SAXParser saxP = saxPF.newSAXParser();
                XMLReader xmlR = saxP.getXMLReader();
                for (int i = 0; i < coverageBeanlist.size(); i++) {
                    database.open();
                    storestatus = database.getStoreStatus(coverageBeanlist.get(i).getStoreId(), coverageBeanlist.get(i).getProcess_id());
                    if (coverageBeanlist.get(i).getSub_reasonId().equals("")) {
                        sub_reason = "0";
                    } else {
                        sub_reason = coverageBeanlist.get(i).getSub_reasonId();
                    }

                    if (storestatus.getUPLOAD_STATUS().equalsIgnoreCase(CommonString.STORE_STATUS_LEAVE) || storestatus.getCHECKOUT_STATUS().equalsIgnoreCase(CommonString.KEY_C)) {
                        if (!coverageBeanlist.get(i).getStatus().equalsIgnoreCase("D")) {
                            String onXML = "[DATA][USER_DATA][STORE_ID]"
                                    + coverageBeanlist.get(i).getStoreId()
                                    + "[/STORE_ID]"
                                    + "[VISIT_DATE]"
                                    + coverageBeanlist.get(i).getVisitDate()
                                    + "[/VISIT_DATE]"
                                    + "[LATITUDE]"
                                    + coverageBeanlist.get(i).getLatitude()
                                    + "[/LATITUDE]"
                                    + "[STORE_IMAGE]"
                                    + coverageBeanlist.get(i).getImage()
                                    + "[/STORE_IMAGE]"
                                    + "[LONGITUDE]"
                                    + coverageBeanlist.get(i).getLongitude()
                                    + "[/LONGITUDE]"
                                    + "[IN_TIME]"
                                    + coverageBeanlist.get(i).getInTime()
                                    + "[/IN_TIME][OUT_TIME]"
                                    + coverageBeanlist.get(i).getOutTime()
                                    + "[/OUT_TIME][UPLOAD_STATUS]P"
                                    + "[/UPLOAD_STATUS][CREATED_BY]" + username
                                    + "[/CREATED_BY][REASON_REMARK]"
                                    + "0"
                                    + "[/REASON_REMARK][REASON_ID]"
                                    + coverageBeanlist.get(i).getReasonid()
                                    + "[/REASON_ID][SUB_REASON_ID]"
                                    + sub_reason
                                    + "[/SUB_REASON_ID][APP_VERSION]" + app_ver
                                    + "[/APP_VERSION] [IMAGE_ALLOW]" + "0"
                                    + "[/IMAGE_ALLOW]" + "[USER_ID]" + username
                                    + "[/USER_ID]" + "[PROCESS_ID]" + coverageBeanlist.get(i).getProcess_id()
                                    + "[/PROCESS_ID]"
                                    + "[CHECKOUT_IMAGE]"
                                    + coverageBeanlist.get(i).getCHECKOUT_IMG()
                                    + "[/CHECKOUT_IMAGE]"
                                    + "[/USER_DATA][/DATA]";


                            SoapObject request = new SoapObject(CommonString.NAMESPACE, CommonString.METHOD_UPLOAD_DR_STORE_COVERAGE_LOC);
                            request.addProperty("onXML", onXML);
                            SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
                            envelope.dotNet = true;
                            envelope.setOutputSoapObject(request);
                            HttpTransportSE androidHttpTransport = new HttpTransportSE(CommonString.URL);
                            androidHttpTransport.call(CommonString.SOAP_ACTION_UPLOAD_DR_STORE_COVERAGE, envelope);
                            Object result = (Object) envelope.getResponse();
                            datacheck = result.toString();
                            words = datacheck.split("\\;");
                            validity = (words[0]);
                            if (validity.equalsIgnoreCase(CommonString.KEY_SUCCESS)) {
                                database.open();
                                database.updateCoverageStatus(coverageBeanlist.get(i).getStoreId(), CommonString.KEY_P, coverageBeanlist.get(i).getProcess_id());
                                database.updateStoreStatusOnLeave(coverageBeanlist.get(i).getStoreId(), visit_date, CommonString.KEY_P, coverageBeanlist.get(i).getProcess_id());
                            } else {
                                if (result.toString().equalsIgnoreCase(CommonString.KEY_FALSE)) {
                                    return CommonString.METHOD_UPLOAD_DR_STORE_COVERAGE;
                                }
                                // for failure
                                FailureXMLHandler failureXMLHandler = new FailureXMLHandler();
                                xmlR.setContentHandler(failureXMLHandler);
                                InputSource is = new InputSource();
                                is.setCharacterStream(new StringReader(result.toString()));
                                xmlR.parse(is);
                                failureGetterSetter = failureXMLHandler.getFailureGetterSetter();
                                if (failureGetterSetter.getStatus().equalsIgnoreCase(CommonString.KEY_FAILURE)) {
                                    return CommonString.METHOD_UPLOAD_DR_STORE_COVERAGE + "," + failureGetterSetter.getErrorMsg();
                                }
                            }
                            mid = Integer.parseInt((words[1]));

                            runOnUiThread(new Runnable() {

                                public void run() {
                                    k = k + factor;
                                    pb.setProgress(k);
                                    percentage.setText(k + "%");
                                    message.setText(" Data is Uploading");
                                }
                            });

                            if (!(coverageBeanlist.get(i).getReasonid().equals("") || coverageBeanlist.get(i).getReasonid().equalsIgnoreCase("0"))) {
                            } else {
                                database.open();
                                //  backroom Stock data
                                backRoomStockData = database.getBackRoomStockDataForUpload(coverageBeanlist.get(i).getStoreId(),
                                        coverageBeanlist.get(i).getProcess_id());
                                String final_data_xml = "";
                                onXML = "";
                                if (backRoomStockData.size() > 0) {
                                    for (int j = 0; j < backRoomStockData.size(); j++) {
                                        onXML = "[USER_DATA][MID]"
                                                + mid
                                                + "[/MID][CREATED_BY]"
                                                + username
                                                + "[/CREATED_BY][SKU_CD]"
                                                + backRoomStockData.get(j).getSku_id()
                                                + "[/SKU_CD]"
                                                + "[BACKROOM_STOCK_QTY]"
                                                + backRoomStockData.get(j).getBackroomStockValue()
                                                + "[/BACKROOM_STOCK_QTY]"
                                                + "[/USER_DATA]";

                                        final_data_xml = final_data_xml + onXML;

                                    }
                                    final_data_xml = "[DATA]" + final_data_xml + "[/DATA]";
                                    request = new SoapObject(CommonString.NAMESPACE, CommonString.METHOD_UPLOAD_STOCK_XML_DATA);
                                    request.addProperty("MID", mid);
                                    request.addProperty("KEYS", "BACKROOM_STOCK_XML");
                                    request.addProperty("USERNAME", username);
                                    request.addProperty("XMLDATA", final_data_xml);

                                    envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
                                    envelope.dotNet = true;
                                    envelope.setOutputSoapObject(request);
                                    androidHttpTransport = new HttpTransportSE(CommonString.URL);
                                    androidHttpTransport.call(CommonString.SOAP_ACTION_UPLOAD_ASSET_XMLDATA, envelope);
                                    result = (Object) envelope.getResponse();
                                    if (!result.toString().equalsIgnoreCase(CommonString.KEY_SUCCESS)) {
                                        if (result.toString().equalsIgnoreCase(CommonString.KEY_FALSE)) {
                                            return CommonString.METHOD_UPLOAD_ASSET;
                                        }
                                        FailureXMLHandler failureXMLHandler = new FailureXMLHandler();
                                        xmlR.setContentHandler(failureXMLHandler);
                                        InputSource is = new InputSource();
                                        is.setCharacterStream(new StringReader(result.toString()));
                                        xmlR.parse(is);
                                        failureGetterSetter = failureXMLHandler.getFailureGetterSetter();
                                        if (failureGetterSetter.getStatus().equalsIgnoreCase(CommonString.KEY_FAILURE)) {
                                            return CommonString.METHOD_UPLOAD_ASSET + "," + failureGetterSetter.getErrorMsg();
                                        }
                                    }
                                }
                                runOnUiThread(new Runnable() {

                                    public void run() {
                                        message.setText("Stock Data Uploaded");
                                    }
                                });


                                //  Sales data
                                database.open();
                                salesStockData = database.getsalesStockDataForUpload(coverageBeanlist.get(i).getStoreId(), coverageBeanlist.get(i).getProcess_id());
                                String final_data_xmll = "";
                                onXML = "";
                                if (salesStockData.size() > 0) {
                                    for (int j = 0; j < salesStockData.size(); j++) {
                                        onXML = "[USER_DATA][MID]"
                                                + mid
                                                + "[/MID][CREATED_BY]"
                                                + username
                                                + "[/CREATED_BY][SKU_CD]"
                                                + salesStockData.get(j).getSku_id()
                                                + "[/SKU_CD]"
                                                + "[SALES_QTY]"
                                                + salesStockData.get(j).getSales_qty()
                                                + "[/SALES_QTY]"
                                                + "[/USER_DATA]";

                                        final_data_xmll = final_data_xmll + onXML;

                                    }

                                    final_data_xmll = "[DATA]" + final_data_xmll + "[/DATA]";
                                    request = new SoapObject(CommonString.NAMESPACE, CommonString.METHOD_UPLOAD_STOCK_XML_DATA);
                                    request.addProperty("MID", mid);
                                    request.addProperty("KEYS", "SALES_XML");
                                    request.addProperty("USERNAME", username);
                                    request.addProperty("XMLDATA", final_data_xmll);
                                    envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
                                    envelope.dotNet = true;
                                    envelope.setOutputSoapObject(request);
                                    androidHttpTransport = new HttpTransportSE(CommonString.URL);
                                    androidHttpTransport.call(CommonString.SOAP_ACTION_UPLOAD_ASSET_XMLDATA, envelope);
                                    result = (Object) envelope.getResponse();
                                    if (!result.toString().equalsIgnoreCase(CommonString.KEY_SUCCESS)) {
                                        if (result.toString().equalsIgnoreCase(CommonString.KEY_FALSE)) {
                                            return CommonString.METHOD_UPLOAD_ASSET;
                                        }
                                        FailureXMLHandler failureXMLHandler = new FailureXMLHandler();
                                        xmlR.setContentHandler(failureXMLHandler);
                                        InputSource is = new InputSource();
                                        is.setCharacterStream(new StringReader(result.toString()));
                                        xmlR.parse(is);
                                        failureGetterSetter = failureXMLHandler.getFailureGetterSetter();
                                        if (failureGetterSetter.getStatus().equalsIgnoreCase(CommonString.KEY_FAILURE)) {
                                            return CommonString.METHOD_UPLOAD_ASSET + "," + failureGetterSetter.getErrorMsg();
                                        }
                                    }

                                }
                                runOnUiThread(new Runnable() {

                                    public void run() {
                                        message.setText("Sales Data Uploaded");
                                    }
                                });

                                database.open();
                                // Stock
                                beforeStockData = database.getStockDataForUpload(coverageBeanlist.get(i).getStoreId(), coverageBeanlist.get(i).getProcess_id());
                                String final_xml = "";
                                onXML = "";
                                if (!(beforeStockData.size() == 0)) {
                                    for (int j = 0; j < beforeStockData.size(); j++) {
                                        String stock, faceup;
                                        if (beforeStockData.get(j).getBefore_Stock() == null) {
                                            stock = "0";
                                        } else {
                                            stock = beforeStockData.get(j).getBefore_Stock();
                                        }
                                        onXML = "[USER_DATA][MID]"
                                                + mid
                                                + "[/MID][CREATED_BY]"
                                                + username
                                                + "[/CREATED_BY][SKU_CD]"
                                                + beforeStockData.get(j).getSku_id()
                                                + "[/SKU_CD]"
                                                + "[AFTER_FACEUP]"
                                                + beforeStockData.get(j).getAfter_faceup()
                                                + "[/AFTER_FACEUP][AFTER_STOCK_QTY]"
                                                + beforeStockData.get(j).getAfter_Stock()
                                                + "[/AFTER_STOCK_QTY][MORETHAN_SIX]"
                                                + beforeStockData.get(j).getALAST_THREE()
                                                + "[/MORETHAN_SIX][LASTTO_SIX]"
                                                + beforeStockData.get(j).getATHREE_TO_SIX()
                                                + "[/LASTTO_SIX][ABOVE]"
                                                + beforeStockData.get(j).getAMORE_SIX()
                                                + "[/ABOVE][CATEGORY_ID]"
                                                + beforeStockData.get(j).getCategory_id()
                                                + "[/CATEGORY_ID][/USER_DATA]";

                                        final_xml = final_xml + onXML;
                                    }


                                    final_xml = "[DATA]" + final_xml + "[/DATA]";
                                    request = new SoapObject(CommonString.NAMESPACE, CommonString.METHOD_UPLOAD_STOCK_XML_DATA);
                                    request.addProperty("MID", mid);
                                    request.addProperty("KEYS", "STOCK_NEW_XML");
                                    request.addProperty("USERNAME", username);
                                    request.addProperty("XMLDATA", final_xml);
                                    envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
                                    envelope.dotNet = true;
                                    envelope.setOutputSoapObject(request);
                                    androidHttpTransport = new HttpTransportSE(CommonString.URL);
                                    androidHttpTransport.call(CommonString.SOAP_ACTION_UPLOAD_ASSET_XMLDATA, envelope);
                                    result = (Object) envelope.getResponse();
                                    if (!result.toString().equalsIgnoreCase(CommonString.KEY_SUCCESS)) {
                                        if (result.toString().equalsIgnoreCase(CommonString.KEY_FALSE)) {
                                            return CommonString.METHOD_UPLOAD_ASSET;
                                        }

                                        FailureXMLHandler failureXMLHandler = new FailureXMLHandler();
                                        xmlR.setContentHandler(failureXMLHandler);
                                        InputSource is = new InputSource();
                                        is.setCharacterStream(new StringReader(result.toString()));
                                        xmlR.parse(is);
                                        failureGetterSetter = failureXMLHandler.getFailureGetterSetter();
                                        if (failureGetterSetter.getStatus().equalsIgnoreCase(CommonString.KEY_FAILURE)) {
                                            return CommonString.METHOD_UPLOAD_ASSET + "," + failureGetterSetter.getErrorMsg();
                                        }
                                    }

                                }
                                runOnUiThread(new Runnable() {

                                    public void run() {
                                        message.setText("Stock Data Uploaded");
                                    }
                                });


                                /////changesssssss
                                database.open();
                                //Shelf Visibility Data uploading
                                // shelfData = database.getInsertedShelfDataForUpload(coverageBeanlist.get(i).getStoreId(), coverageBeanlist.get(i).getProcess_id());
                                shelfData = database.getInsertedShelfFacingStockDataForUpload(coverageBeanlist.get(i).getStoreId(), coverageBeanlist.get(i).getProcess_id());
                                String shelf_xml = "";
                                onXML = "";
                                if (shelfData.size() > 0) {
                                    for (int j = 0; j < shelfData.size(); j++) {
                                        onXML = "[USER_DATA][MID]"
                                                + mid
                                                + "[/MID][CREATED_BY]"
                                                + username
                                                + "[/CREATED_BY][CATEGORY_ID]"
                                                + shelfData.get(j).getCate_id()
                                                + "[/CATEGORY_ID]"
                                                + "[PROCESS_ID]"
                                                + shelfData.get(j).getProcess_id()
                                                + "[/PROCESS_ID][STORE_TYPEID]"
                                                + shelfData.get(j).getStore_typeid()
                                                + "[/STORE_TYPEID]"
                                                + "[BRAND_ID]"
                                                + shelfData.get(j).getBrand_id()
                                                + "[/BRAND_ID][FACING_TARGET]"
                                                + shelfData.get(j).getFacing_Target()
                                                + "[/FACING_TARGET][YESORNO]"
                                                + shelfData.get(j).getAnswer()
                                                + "[/YESORNO][IMAGE_URL]"
                                                + shelfData.get(j).getImage()
                                                + "[/IMAGE_URL][/USER_DATA]";


                                        shelf_xml = shelf_xml + onXML;

                                    }
                                    shelf_xml = "[DATA]" + shelf_xml + "[/DATA]";
                                    request = new SoapObject(CommonString.NAMESPACE, CommonString.METHOD_UPLOAD_STOCK_XML_DATA);
                                    request.addProperty("MID", mid);
                                    request.addProperty("KEYS", "SHELF_VISIBILITY_FACING_XML");
                                    request.addProperty("USERNAME", username);
                                    request.addProperty("XMLDATA", shelf_xml);
                                    envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
                                    envelope.dotNet = true;
                                    envelope.setOutputSoapObject(request);
                                    androidHttpTransport = new HttpTransportSE(CommonString.URL);
                                    androidHttpTransport.call(CommonString.SOAP_ACTION_UPLOAD_ASSET_XMLDATA, envelope);
                                    result = (Object) envelope.getResponse();
                                    if (!result.toString().equalsIgnoreCase(CommonString.KEY_SUCCESS)) {
                                        if (result.toString().equalsIgnoreCase(CommonString.KEY_FALSE)) {
                                            return CommonString.METHOD_UPLOAD_ASSET;
                                        }

                                        FailureXMLHandler failureXMLHandler = new FailureXMLHandler();
                                        xmlR.setContentHandler(failureXMLHandler);
                                        InputSource is = new InputSource();
                                        is.setCharacterStream(new StringReader(result.toString()));
                                        xmlR.parse(is);

                                        failureGetterSetter = failureXMLHandler
                                                .getFailureGetterSetter();

                                        if (failureGetterSetter
                                                .getStatus()
                                                .equalsIgnoreCase(
                                                        CommonString.KEY_FAILURE)) {
                                            return CommonString.METHOD_UPLOAD_ASSET
                                                    + ","
                                                    + failureGetterSetter
                                                    .getErrorMsg();
                                        }
                                    }

                                }
                                runOnUiThread(new Runnable() {

                                    public void run() {
                                        message.setText("Shelf Data Uploaded");
                                    }
                                });

                                database.open();
                                stockImages = database.getStockImagesForUpload(coverageBeanlist.get(i).getStoreId(), username, coverageBeanlist.get(i).getProcess_id());
                                String stock_xml = "";
                                onXML = "";
                                if (stockImages.size() > 0) {
                                    for (int j = 0; j < stockImages.size(); j++) {
                                        onXML = "[USER_DATA][MID]"
                                                + mid
                                                + "[/MID][CREATED_BY]"
                                                + username
                                                + "[/CREATED_BY][BEFORE_IMAGE1]"
                                                + stockImages.get(j).getImage1()
                                                + "[/BEFORE_IMAGE1]"
                                                + "[BEFORE_IMAGE2]"
                                                + stockImages.get(j).getImage2()
                                                + "[/BEFORE_IMAGE2][BEFORE_IMAGE3]"
                                                + stockImages.get(j).getImage3()
                                                + "[/BEFORE_IMAGE3][BEFORE_IMAGE4]"
                                                + stockImages.get(j).getImage4()
                                                + "[/BEFORE_IMAGE4][AFTER_IMAGE1]"
                                                + stockImages.get(j).getImage5()
                                                + "[/AFTER_IMAGE1][AFTER_IMAGE2]"
                                                + stockImages.get(j).getImage6()
                                                + "[/AFTER_IMAGE2][AFTER_IMAGE3]"
                                                + stockImages.get(j).getImage7()
                                                + "[/AFTER_IMAGE3][AFTER_IMAGE4]"
                                                + stockImages.get(j).getImage8()
                                                + "[/AFTER_IMAGE4][CATEGORY_ID]"
                                                + stockImages.get(j).getCategory_id()
                                                + "[/CATEGORY_ID][/USER_DATA]";

                                        stock_xml = stock_xml + onXML;

                                    }


                                    stock_xml = "[DATA]" + stock_xml + "[/DATA]";
                                    request = new SoapObject(CommonString.NAMESPACE, CommonString.METHOD_UPLOAD_STOCK_XML_DATA);
                                    request.addProperty("MID", mid);
                                    request.addProperty("KEYS", "STOCK_IMAGES");
                                    request.addProperty("USERNAME", username);

                                    request.addProperty("XMLDATA", stock_xml);
                                    envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
                                    envelope.dotNet = true;
                                    envelope.setOutputSoapObject(request);
                                    androidHttpTransport = new HttpTransportSE(CommonString.URL);
                                    androidHttpTransport.call(CommonString.SOAP_ACTION_UPLOAD_ASSET_XMLDATA, envelope);
                                    result = (Object) envelope.getResponse();
                                    if (!result.toString().equalsIgnoreCase(CommonString.KEY_SUCCESS)) {
                                        if (result.toString().equalsIgnoreCase(CommonString.KEY_FALSE)) {
                                            return CommonString.METHOD_UPLOAD_ASSET;
                                        }

                                        FailureXMLHandler failureXMLHandler = new FailureXMLHandler();
                                        xmlR.setContentHandler(failureXMLHandler);
                                        InputSource is = new InputSource();
                                        is.setCharacterStream(new StringReader(result.toString()));
                                        xmlR.parse(is);
                                        failureGetterSetter = failureXMLHandler.getFailureGetterSetter();
                                        if (failureGetterSetter.getStatus().equalsIgnoreCase(CommonString.KEY_FAILURE)) {
                                            return CommonString.METHOD_UPLOAD_ASSET + "," + failureGetterSetter.getErrorMsg();
                                        }
                                    }

                                }
                                runOnUiThread(new Runnable() {

                                    public void run() {
                                        message.setText("Stock Images Data Uploaded");
                                    }
                                });


                                // Before TOT Data
                                database.open();
                                afterTOTData = database.getAfterTOTDataForUpload(coverageBeanlist.get(i).getStoreId(), coverageBeanlist.get(i).getProcess_id());
                                String before_tot_xml = "";
                                onXML = "";
                                if (afterTOTData.size() > 0) {
                                    for (int j = 0; j < afterTOTData.size(); j++) {

                                        onXML = "[USER_DATA][MID]"
                                                + mid
                                                + "[/MID][CREATED_BY]"
                                                + username
                                                + "[/CREATED_BY][DISPLAY_ID]"
                                                + afterTOTData.get(j).getDisplay_id()
                                                + "[/DISPLAY_ID]"

                                                + "[CATEGORY_ID]"
                                                + afterTOTData.get(j).getCategory_id()
                                                + "[/CATEGORY_ID]"

                                                + "[AFTER_QUANTITY]"
                                                + afterTOTData.get(j).getAFTER_QTY()
                                                + "[/AFTER_QUANTITY][AFTER_TARGET_QUANTITY]"
                                                + afterTOTData.get(j).getTrg_quantity()
                                                + "[/AFTER_TARGET_QUANTITY][AFTER_STOCK_COUNT]"
                                                + afterTOTData.get(j).getStock_count()
                                                + "[/AFTER_STOCK_COUNT][AFTER_IMAGE1]"
                                                + afterTOTData.get(j).getImage1()
                                                + "[/AFTER_IMAGE1][AFTER_IMAGE2]"
                                                + afterTOTData.get(j).getImage2()
                                                + "[/AFTER_IMAGE2] [AFTER_IMAGE3]"
                                                + afterTOTData.get(j).getImage3()
                                                + "[/AFTER_IMAGE3][UNIQUE_KEY_ID]"
                                                + afterTOTData.get(j).getUnique_id()
                                                + "[/UNIQUE_KEY_ID][BRAND_ID]" + afterTOTData.get(j).getBrand_id() +
                                                "[/BRAND_ID][TYPE]"
                                                + afterTOTData.get(j).getType()
                                                + "[/TYPE][/USER_DATA]";

                                        before_tot_xml = before_tot_xml + onXML;

                                    }


                                    before_tot_xml = "[DATA]" + before_tot_xml + "[/DATA]";


                                    request = new SoapObject(CommonString.NAMESPACE, CommonString.METHOD_UPLOAD_STOCK_XML_DATA);
                                    request.addProperty("MID", mid);
                                    request.addProperty("KEYS", "TOT_NEW_XML");
                                    request.addProperty("USERNAME", username);
                                    request.addProperty("XMLDATA", before_tot_xml);
                                    envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
                                    envelope.dotNet = true;
                                    envelope.setOutputSoapObject(request);
                                    androidHttpTransport = new HttpTransportSE(CommonString.URL);
                                    androidHttpTransport.call(CommonString.SOAP_ACTION_UPLOAD_ASSET_XMLDATA, envelope);
                                    result = (Object) envelope.getResponse();
                                    if (!result.toString().equalsIgnoreCase(CommonString.KEY_SUCCESS)) {
                                        if (result.toString().equalsIgnoreCase(CommonString.KEY_FALSE)) {
                                            return CommonString.METHOD_UPLOAD_ASSET;
                                        }
                                        FailureXMLHandler failureXMLHandler = new FailureXMLHandler();
                                        xmlR.setContentHandler(failureXMLHandler);
                                        InputSource is = new InputSource();
                                        is.setCharacterStream(new StringReader(result.toString()));
                                        xmlR.parse(is);
                                        failureGetterSetter = failureXMLHandler.getFailureGetterSetter();
                                        if (failureGetterSetter.getStatus().equalsIgnoreCase(CommonString.KEY_FAILURE)) {
                                            return CommonString.METHOD_UPLOAD_ASSET + "," + failureGetterSetter.getErrorMsg();
                                        }
                                    }

                                }
                                runOnUiThread(new Runnable() {

                                    public void run() {
                                        message.setText("TOT Data Uploaded");
                                    }
                                });


                                //totStockdata
                                database.open();
                                stockTotdata = database.getTOTStockEntryDetailForUpload(coverageBeanlist.get(i).getStoreId(), coverageBeanlist.get(i).getProcess_id());
                                String stock_tot_xml = "";
                                onXML = "";
                                if (stockTotdata.size() > 0) {
                                    for (int j = 0; j < stockTotdata.size(); j++) {
                                        onXML = "[USER_DATA][MID]"
                                                + mid
                                                + "[/MID][CREATED_BY]"
                                                + username
                                                + "[/CREATED_BY][DISPLAY_ID]"
                                                + stockTotdata.get(j).getDisplay_id()
                                                + "[/DISPLAY_ID]"
                                                + "[BRAND_ID]"
                                                + stockTotdata.get(j).getBrand_id()
                                                + "[/BRAND_ID][SKU_ID]"
                                                + stockTotdata.get(j).getSku_id()
                                                + "[/SKU_ID][UNIQUE_ID]"
                                                + stockTotdata.get(j).getUnique_id()
                                                + "[/UNIQUE_ID][CATEGORY_ID]"
                                                + stockTotdata.get(j).getCategory_id()
                                                + "[/CATEGORY_ID][QUANTITY]" + stockTotdata.get(j).getQuantity()
                                                + "[/QUANTITY][/USER_DATA]";

                                        stock_tot_xml = stock_tot_xml + onXML;

                                    }


                                    stock_tot_xml = "[DATA]" + stock_tot_xml + "[/DATA]";

                                    request = new SoapObject(CommonString.NAMESPACE, CommonString.METHOD_UPLOAD_STOCK_XML_DATA);
                                    request.addProperty("MID", mid);
                                    request.addProperty("KEYS", "TOT_STOCK_XML");
                                    request.addProperty("USERNAME", username);
                                    request.addProperty("XMLDATA", stock_tot_xml);

                                    envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
                                    envelope.dotNet = true;
                                    envelope.setOutputSoapObject(request);
                                    androidHttpTransport = new HttpTransportSE(CommonString.URL);
                                    androidHttpTransport.call(CommonString.SOAP_ACTION_UPLOAD_ASSET_XMLDATA, envelope);
                                    result = (Object) envelope.getResponse();
                                    if (!result.toString().equalsIgnoreCase(CommonString.KEY_SUCCESS)) {
                                        if (result.toString().equalsIgnoreCase(CommonString.KEY_FALSE)) {
                                            return CommonString.METHOD_UPLOAD_ASSET;
                                        }

                                        FailureXMLHandler failureXMLHandler = new FailureXMLHandler();
                                        xmlR.setContentHandler(failureXMLHandler);

                                        InputSource is = new InputSource();
                                        is.setCharacterStream(new StringReader(result.toString()));
                                        xmlR.parse(is);
                                        failureGetterSetter = failureXMLHandler.getFailureGetterSetter();
                                        if (failureGetterSetter.getStatus().equalsIgnoreCase(CommonString.KEY_FAILURE)) {
                                            return CommonString.METHOD_UPLOAD_ASSET + "," + failureGetterSetter.getErrorMsg();
                                        }
                                    }

                                }
                                runOnUiThread(new Runnable() {

                                    public void run() {
                                        message.setText("TOT Stock Data Uploaded");
                                    }
                                });

                                // Promotion Data
                                database.open();
                                promotionData = database.getInsertedPromoCompliance(coverageBeanlist.get(i).getStoreId(), coverageBeanlist.get(i).getProcess_id());
                                String after_tot_xml = "";
                                onXML = "";
                                if (promotionData.size() > 0) {
                                    for (int j = 0; j < promotionData.size(); j++) {
                                        onXML = "[USER_DATA][MID]"
                                                + mid
                                                + "[/MID][CREATED_BY]"
                                                + username
                                                + "[/CREATED_BY][SKU_ID]"
                                                + promotionData.get(j).getSku_id()
                                                + "[/SKU_ID]"
                                                + "[STOCK]"
                                                + promotionData.get(j).getStock()
                                                + "[/STOCK][POP]"
                                                + promotionData.get(j).getPop()
                                                + "[/POP][RUNNING_IN_SYSTEM]"
                                                + promotionData.get(j).getRunning()
                                                + "[/RUNNING_IN_SYSTEM][RUNNING_IN_SYSTEM_CHILD]"
                                                + promotionData.get(j).getRunning_child_toggle()
                                                + "[/RUNNING_IN_SYSTEM_CHILD][RUNNING_IN_SYSTEM_CHILD_PRICE]"
                                                + promotionData.get(j).getRunning_child_price()
                                                + "[/RUNNING_IN_SYSTEM_CHILD_PRICE][PROMOTION]"
                                                + promotionData.get(j).getPromotion()
                                                + "[/PROMOTION][CATEGORY_ID]"
                                                + promotionData.get(j).getCategory_id()
                                                + "[/CATEGORY_ID][PROCESS_ID]"
                                                + promotionData.get(j).getProcess_id()
                                                + "[/PROCESS_ID][POP_IMAGE]"
                                                + promotionData.get(j).getPop_img()
                                                + "[/POP_IMAGE]  [ID]"
                                                + promotionData.get(j).getSpecial_id()
                                                + "[/ID] "
                                                + "[/USER_DATA]";

                                        after_tot_xml = after_tot_xml + onXML;

                                    }


                                    after_tot_xml = "[DATA]" + after_tot_xml + "[/DATA]";


                                    request = new SoapObject(CommonString.NAMESPACE, CommonString.METHOD_UPLOAD_STOCK_XML_DATA);

                                    request.addProperty("MID", mid);
                                    request.addProperty("KEYS", "PROMOTION_DATA_NEW");
                                    request.addProperty("USERNAME", username);
                                    request.addProperty("XMLDATA", after_tot_xml);

                                    envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
                                    envelope.dotNet = true;
                                    envelope.setOutputSoapObject(request);

                                    androidHttpTransport = new HttpTransportSE(CommonString.URL);
                                    androidHttpTransport.call(CommonString.SOAP_ACTION_UPLOAD_ASSET_XMLDATA, envelope);
                                    result = (Object) envelope.getResponse();
                                    if (!result.toString().equalsIgnoreCase(CommonString.KEY_SUCCESS)) {
                                        if (result.toString().equalsIgnoreCase(CommonString.KEY_FALSE)) {
                                            return CommonString.METHOD_UPLOAD_ASSET;
                                        }
                                        FailureXMLHandler failureXMLHandler = new FailureXMLHandler();
                                        xmlR.setContentHandler(failureXMLHandler);
                                        InputSource is = new InputSource();
                                        is.setCharacterStream(new StringReader(result.toString()));
                                        xmlR.parse(is);

                                        failureGetterSetter = failureXMLHandler.getFailureGetterSetter();
                                        if (failureGetterSetter.getStatus().equalsIgnoreCase(CommonString.KEY_FAILURE)) {
                                            return CommonString.METHOD_UPLOAD_ASSET + "," + failureGetterSetter.getErrorMsg();
                                        }
                                    }

                                }

                                runOnUiThread(new Runnable() {

                                    public void run() {
                                        message.setText("PROMOTION Data Uploaded");
                                    }
                                });

                                database.open();
                                complianceData = database.getAfterComplianceData(coverageBeanlist.get(i).getStoreId(), coverageBeanlist.get(i).getProcess_id());
                                String question_xml = "";
                                onXML = "";
                                if (complianceData.size() > 0) {
                                    for (int j = 0; j < complianceData.size(); j++) {
                                        onXML = "[USER_DATA][MID]"
                                                + mid
                                                + "[/MID][CREATED_BY]"
                                                + username
                                                + "[/CREATED_BY][DISPLAY_ID]"
                                                + complianceData.get(j).getDisplay_id()
                                                + "[/DISPLAY_ID]"
                                                + "[QUESTION_ID]"
                                                + complianceData.get(j).getQuestion_id()
                                                + "[/QUESTION_ID][ANSWER]"
                                                + complianceData.get(j).getAnswer()
                                                + "[/ANSWER][UNIQUE_ID]"
                                                + complianceData.get(j).getUnique_id()
                                                + "[/UNIQUE_ID][CATEGORY_ID]"
                                                + complianceData.get(j).getCategory_id()
                                                + "[/CATEGORY_ID][/USER_DATA]";

                                        question_xml = question_xml + onXML;
                                    }


                                    question_xml = "[DATA]" + question_xml + "[/DATA]";
                                    request = new SoapObject(CommonString.NAMESPACE, CommonString.METHOD_UPLOAD_STOCK_XML_DATA);
                                    request.addProperty("MID", mid);
                                    request.addProperty("KEYS", "QUESTION_XML");
                                    request.addProperty("USERNAME", username);
                                    request.addProperty("XMLDATA", question_xml);
                                    envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
                                    envelope.dotNet = true;
                                    envelope.setOutputSoapObject(request);
                                    androidHttpTransport = new HttpTransportSE(CommonString.URL);
                                    androidHttpTransport.call(CommonString.SOAP_ACTION_UPLOAD_ASSET_XMLDATA, envelope);
                                    result = (Object) envelope.getResponse();
                                    if (!result.toString().equalsIgnoreCase(CommonString.KEY_SUCCESS)) {
                                        if (result.toString().equalsIgnoreCase(CommonString.KEY_FALSE)) {
                                            return CommonString.METHOD_UPLOAD_ASSET;
                                        }
                                        FailureXMLHandler failureXMLHandler = new FailureXMLHandler();
                                        xmlR.setContentHandler(failureXMLHandler);
                                        InputSource is = new InputSource();
                                        is.setCharacterStream(new StringReader(result.toString()));
                                        xmlR.parse(is);
                                        failureGetterSetter = failureXMLHandler.getFailureGetterSetter();
                                        if (failureGetterSetter.getStatus().equalsIgnoreCase(CommonString.KEY_FAILURE)) {
                                            return CommonString.METHOD_UPLOAD_ASSET + "," + failureGetterSetter.getErrorMsg();
                                        }
                                    }

                                }
                                runOnUiThread(new Runnable() {

                                    public void run() {
                                        message.setText("Compliance Data Uploaded");
                                    }
                                });


                                ArrayList<SkuBean> sku_brand_list_second = new ArrayList<SkuBean>();
                                database.open();
                                sku_brand_list_second = database.getInsertedDisplayListAfterStockUpload(coverageBeanlist
                                        .get(i).getStoreId(), coverageBeanlist.get(i).getProcess_id());
                                String question_xml_stockAdditional = "";
                                onXML = "";
                                if (sku_brand_list_second.size() > 0) {
                                    for (int j = 0; j < sku_brand_list_second.size(); j++) {

                                        onXML = "[USER_DATA][MID]"
                                                + mid
                                                + "[/MID][CREATED_BY]"
                                                + username
                                                + "[/CREATED_BY][DISPLAY_ID]"
                                                + sku_brand_list_second.get(j).getDisplay_id()
                                                + "[/DISPLAY_ID]"
                                                + "[UNIQUE_ID]"
                                                + sku_brand_list_second.get(j).getUID()
                                                + "[/UNIQUE_ID][CATEGORY_ID]"
                                                + sku_brand_list_second.get(j).getCategory_id()
                                                + "[/CATEGORY_ID][BRAND_ID]" + sku_brand_list_second.get(j).getBrand_id() +
                                                "[/BRAND_ID][IS_EXIST]" + sku_brand_list_second.get(j).getExist() +
                                                "[/IS_EXIST][BRAND_IMG]" + sku_brand_list_second.get(j).getBrand_img() +
                                                "[/BRAND_IMG]" +
                                                "[/USER_DATA]";

                                        question_xml_stockAdditional = question_xml_stockAdditional + onXML;

                                    }
                                    question_xml_stockAdditional = "[DATA]" + question_xml_stockAdditional + "[/DATA]";
                                    request = new SoapObject(CommonString.NAMESPACE, CommonString.METHOD_UPLOAD_STOCK_XML_DATA);
                                    request.addProperty("MID", mid);
                                    request.addProperty("KEYS", "PRIMARY_WINDOW_DISPLAY_WITH_IMAGE");
                                    request.addProperty("USERNAME", username);
                                    request.addProperty("XMLDATA", question_xml_stockAdditional);
                                    envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
                                    envelope.dotNet = true;
                                    envelope.setOutputSoapObject(request);

                                    androidHttpTransport = new HttpTransportSE(CommonString.URL);
                                    androidHttpTransport.call(CommonString.SOAP_ACTION_UPLOAD_ASSET_XMLDATA, envelope);
                                    result = (Object) envelope.getResponse();
                                    if (!result.toString().equalsIgnoreCase(CommonString.KEY_SUCCESS)) {

                                        if (result.toString().equalsIgnoreCase(CommonString.KEY_FALSE)) {
                                            return CommonString.METHOD_UPLOAD_ASSET;
                                        }
                                        FailureXMLHandler failureXMLHandler = new FailureXMLHandler();
                                        xmlR.setContentHandler(failureXMLHandler);

                                        InputSource is = new InputSource();
                                        is.setCharacterStream(new StringReader(result.toString()));
                                        xmlR.parse(is);
                                        failureGetterSetter = failureXMLHandler.getFailureGetterSetter();

                                        if (failureGetterSetter.getStatus().equalsIgnoreCase(CommonString.KEY_FAILURE)) {
                                            return CommonString.METHOD_UPLOAD_ASSET + "," + failureGetterSetter.getErrorMsg();
                                        }
                                    }
                                }
                                runOnUiThread(new Runnable() {

                                    public void run() {
                                        message.setText("Primary Window Data Uploaded");
                                    }
                                });

                                database.open();
                                complianceData = database
                                        .getAfterComplianceDataAfterStock(coverageBeanlist
                                                .get(i).getStoreId(), coverageBeanlist.get(i).getProcess_id());

                                String question_xml_stockAfter = "";
                                onXML = "";
                                if (complianceData.size() > 0) {
                                    for (int j = 0; j < complianceData.size(); j++) {
                                        onXML = "[USER_DATA][MID]"
                                                + mid
                                                + "[/MID][CREATED_BY]"
                                                + username
                                                + "[/CREATED_BY][DISPLAY_ID]"
                                                + complianceData.get(j).getDisplay_id()
                                                + "[/DISPLAY_ID]"
                                                + "[QUESTION_ID]"
                                                + complianceData.get(j).getQuestion_id()
                                                + "[/QUESTION_ID][ANSWER]"
                                                + complianceData.get(j).getAnswer()
                                                + "[/ANSWER][UNIQUE_ID]"
                                                + complianceData.get(j).getUnique_id()
                                                + "[/UNIQUE_ID][CATEGORY_ID]"
                                                + complianceData.get(j).getCategory_id()
                                                + "[/CATEGORY_ID][/USER_DATA]";

                                        question_xml_stockAfter = question_xml_stockAfter + onXML;

                                    }


                                    question_xml_stockAfter = "[DATA]" + question_xml_stockAfter
                                            + "[/DATA]";


                                    request = new SoapObject(
                                            CommonString.NAMESPACE,
                                            CommonString.METHOD_UPLOAD_STOCK_XML_DATA);

                                    request.addProperty("MID", mid);
                                    request.addProperty("KEYS", "PRIMARY_WINDOW_GAP");
                                    request.addProperty("USERNAME", username);

                                    request.addProperty("XMLDATA", question_xml_stockAfter);

                                    envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
                                    envelope.dotNet = true;
                                    envelope.setOutputSoapObject(request);
                                    androidHttpTransport = new HttpTransportSE(CommonString.URL);


                                    androidHttpTransport.call(CommonString.SOAP_ACTION_UPLOAD_ASSET_XMLDATA, envelope);
                                    result = (Object) envelope.getResponse();
                                    if (!result.toString().equalsIgnoreCase(CommonString.KEY_SUCCESS)) {
                                        if (result.toString().equalsIgnoreCase(CommonString.KEY_FALSE)) {
                                            return CommonString.METHOD_UPLOAD_ASSET;
                                        }
                                        FailureXMLHandler failureXMLHandler = new FailureXMLHandler();
                                        xmlR.setContentHandler(failureXMLHandler);
                                        InputSource is = new InputSource();
                                        is.setCharacterStream(new StringReader(result.toString()));
                                        xmlR.parse(is);
                                        failureGetterSetter = failureXMLHandler.getFailureGetterSetter();

                                        if (failureGetterSetter.getStatus().equalsIgnoreCase(CommonString.KEY_FAILURE)) {
                                            return CommonString.METHOD_UPLOAD_ASSET + "," + failureGetterSetter.getErrorMsg();
                                        }
                                    }

                                }
                                runOnUiThread(new Runnable() {

                                    public void run() {
                                        message.setText("Sku Data Uploaded");
                                    }
                                });

                                // Before Additional  Data
                                database.open();
                                beforeaddtionalData = database.getProductEntryDetailForUpload(coverageBeanlist.get(i).getStoreId(), coverageBeanlist.get(i).getProcess_id());
                                String before_add_xml = "";
                                onXML = "";
                                if (beforeaddtionalData.size() > 0) {
                                    for (int j = 0; j < beforeaddtionalData.size(); j++) {
                                        onXML = "[USER_DATA][MID]"
                                                + mid
                                                + "[/MID][CREATED_BY]"
                                                + username
                                                + "[/CREATED_BY][BRAND_ID]"
                                                + beforeaddtionalData.get(j).getBrand_id()
                                                + "[/BRAND_ID]"
                                                + "[DISPLAY_ID]"
                                                + beforeaddtionalData.get(j).getDisplay_id()
                                                + "[/DISPLAY_ID][QUANTITY]"
                                                + beforeaddtionalData.get(j).getQuantity()
                                                + "[/QUANTITY][IMAGE]"
                                                + beforeaddtionalData.get(j).getAdditional_image()
                                                + "[/IMAGE][CATEGORY_ID]"
                                                + beforeaddtionalData.get(j).getCategory_id()
                                                + "[/CATEGORY_ID][YESORNO]"
                                                + beforeaddtionalData.get(j).getYesorno()
                                                + "[/YESORNO][/USER_DATA]";

                                        before_add_xml = before_add_xml + onXML;

                                    }

                                    before_add_xml = "[DATA]" + before_add_xml + "[/DATA]";
                                    request = new SoapObject(CommonString.NAMESPACE, CommonString.METHOD_UPLOAD_STOCK_XML_DATA);
                                    request.addProperty("MID", mid);
                                    request.addProperty("KEYS", "ADDITIONAL_XML");
                                    request.addProperty("USERNAME", username);
                                    request.addProperty("XMLDATA", before_add_xml);
                                    envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
                                    envelope.dotNet = true;
                                    envelope.setOutputSoapObject(request);
                                    androidHttpTransport = new HttpTransportSE(CommonString.URL);
                                    androidHttpTransport.call(CommonString.SOAP_ACTION_UPLOAD_ASSET_XMLDATA, envelope);
                                    result = (Object) envelope.getResponse();
                                    if (!result.toString().equalsIgnoreCase(CommonString.KEY_SUCCESS)) {
                                        if (result.toString().equalsIgnoreCase(CommonString.KEY_FALSE)) {
                                            return CommonString.METHOD_UPLOAD_ASSET;
                                        }
                                        FailureXMLHandler failureXMLHandler = new FailureXMLHandler();
                                        xmlR.setContentHandler(failureXMLHandler);
                                        InputSource is = new InputSource();
                                        is.setCharacterStream(new StringReader(result.toString()));
                                        xmlR.parse(is);
                                        failureGetterSetter = failureXMLHandler.getFailureGetterSetter();
                                        if (failureGetterSetter.getStatus().equalsIgnoreCase(CommonString.KEY_FAILURE)) {
                                            return CommonString.METHOD_UPLOAD_ASSET + "," + failureGetterSetter.getErrorMsg();
                                        }
                                    }

                                }
                                runOnUiThread(new Runnable() {

                                    public void run() {
                                        message.setText("Additional Data Uploaded");
                                    }
                                });

                                database.open();
                                // Uploading Competition Tracking data
                                competitionTrackingData = database.getEnteredCompetitionDetailForUploading(coverageBeanlist.get(i).getStoreId(), coverageBeanlist.get(i).getProcess_id());
                                String comp_xml = "", comIsExist = "";
                                onXML = "";
                                if (competitionTrackingData.size() > 0) {
                                    for (int j = 0; j < competitionTrackingData.size(); j++) {
                                        if (competitionTrackingData.get(j).isCompTExist() == true) {
                                            comIsExist = "1";
                                        } else {
                                            comIsExist = "0";
                                        }

                                        onXML = "[USER_DATA][MID]"
                                                + mid
                                                + "[/MID][CREATED_BY]"
                                                + username
                                                + "[/CREATED_BY][BRAND_ID]"
                                                + competitionTrackingData.get(j).getBrand_id()
                                                + "[/BRAND_ID]"
                                                + "[DISPLAY_ID]"
                                                + competitionTrackingData.get(j).getDisplay_id()
                                                + "[/DISPLAY_ID][QUANTITY]"
                                                + competitionTrackingData.get(j).getQuantity()
                                                + "[/QUANTITY][IMAGE]"
                                                + competitionTrackingData.get(j).getAdditional_image()
                                                + "[/IMAGE]"

                                                + "[CATEGORY_ID]"
                                                + competitionTrackingData.get(j).getCategory_id()
                                                + "[/CATEGORY_ID]"

                                                + "[COM_EXIST]"
                                                + comIsExist
                                                + "[/COM_EXIST]"

                                                + "[/USER_DATA]";

                                        comp_xml = comp_xml + onXML;
                                    }

                                    comp_xml = "[DATA]" + comp_xml + "[/DATA]";

                                    request = new SoapObject(CommonString.NAMESPACE, CommonString.METHOD_UPLOAD_STOCK_XML_DATA);
                                    request.addProperty("MID", mid);
                                    request.addProperty("KEYS", "COMPETITION_TRACKING_NEW_XML");
                                    request.addProperty("USERNAME", username);
                                    request.addProperty("XMLDATA", comp_xml);
                                    envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
                                    envelope.dotNet = true;
                                    envelope.setOutputSoapObject(request);
                                    androidHttpTransport = new HttpTransportSE(CommonString.URL);
                                    androidHttpTransport.call(CommonString.SOAP_ACTION_UPLOAD_ASSET_XMLDATA, envelope);
                                    result = (Object) envelope.getResponse();
                                    if (!result.toString().equalsIgnoreCase(CommonString.KEY_SUCCESS)) {
                                        if (result.toString().equalsIgnoreCase(CommonString.KEY_FALSE)) {
                                            return CommonString.METHOD_UPLOAD_ASSET;
                                        }
                                        FailureXMLHandler failureXMLHandler = new FailureXMLHandler();
                                        xmlR.setContentHandler(failureXMLHandler);
                                        InputSource is = new InputSource();
                                        is.setCharacterStream(new StringReader(result.toString()));
                                        xmlR.parse(is);
                                        failureGetterSetter = failureXMLHandler.getFailureGetterSetter();
                                        if (failureGetterSetter.getStatus().equalsIgnoreCase(CommonString.KEY_FAILURE)) {
                                            return CommonString.METHOD_UPLOAD_ASSET + "," + failureGetterSetter.getErrorMsg();
                                        }
                                    }
                                }
                                runOnUiThread(new Runnable() {

                                    public void run() {
                                        message.setText("Competition Data Uploaded");
                                    }
                                });


                                // AFTER Additional  Data
                                database.open();
                                afterAddaitionalData = database.getAfterProductEntryDetail(coverageBeanlist.get(i).getStoreId());
                                String after_add_xml = "";
                                onXML = "";
                                if (!(afterAddaitionalData.size() == 0)) {
                                    for (int j = 0; j < afterAddaitionalData.size(); j++) {
                                        onXML = "[USER_DATA][MID]"
                                                + mid
                                                + "[/MID][CREATED_BY]"
                                                + username
                                                + "[/CREATED_BY][BRAND_ID]"
                                                + afterAddaitionalData.get(j).getBrand_id()
                                                + "[/BRAND_ID]"
                                                + "[DISPLAY_ID]"
                                                + afterAddaitionalData.get(j).getDisplay_id()
                                                + "[/DISPLAY_ID][QUANTITY]"
                                                + afterAddaitionalData.get(j).getQuantity()
                                                + "[/QUANTITY][IMAGE]"
                                                + afterAddaitionalData.get(j).getAdditional_image()
                                                + "[/IMAGE]"
                                                + "[/USER_DATA]";

                                        after_add_xml = after_add_xml + onXML;
                                    }

                                    after_add_xml = "[DATA]" + after_add_xml + "[/DATA]";
                                    request = new SoapObject(CommonString.NAMESPACE, CommonString.METHOD_UPLOAD_STOCK_XML_DATA);
                                    request.addProperty("MID", mid);
                                    request.addProperty("KEYS", "AFTER_ADDITIONAL_INFO");
                                    request.addProperty("USERNAME", username);
                                    request.addProperty("XMLDATA", after_add_xml);
                                    envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
                                    envelope.dotNet = true;
                                    envelope.setOutputSoapObject(request);
                                    androidHttpTransport = new HttpTransportSE(CommonString.URL);
                                    androidHttpTransport.call(CommonString.SOAP_ACTION_UPLOAD_ASSET_XMLDATA, envelope);
                                    result = (Object) envelope.getResponse();
                                    if (!result.toString().equalsIgnoreCase(CommonString.KEY_SUCCESS)) {
                                        if (result.toString().equalsIgnoreCase(CommonString.KEY_FALSE)) {
                                            return CommonString.METHOD_UPLOAD_ASSET;
                                        }

                                        FailureXMLHandler failureXMLHandler = new FailureXMLHandler();
                                        xmlR.setContentHandler(failureXMLHandler);
                                        InputSource is = new InputSource();
                                        is.setCharacterStream(new StringReader(result.toString()));
                                        xmlR.parse(is);
                                        failureGetterSetter = failureXMLHandler.getFailureGetterSetter();
                                        if (failureGetterSetter.getStatus().equalsIgnoreCase(CommonString.KEY_FAILURE)) {
                                            return CommonString.METHOD_UPLOAD_ASSET + "," + failureGetterSetter.getErrorMsg();
                                        }
                                    }

                                }
                                runOnUiThread(new Runnable() {

                                    public void run() {
                                        message.setText("Sku Data Uploaded");
                                    }
                                });


                                // AFTER Additional  Data
                                database.open();
                                competitionpromotionList = database.getcompetitionPromotionfromDatabase(coverageBeanlist.get(i).getStoreId(), "", coverageBeanlist.get(i).getProcess_id());
                                after_add_xml = "";
                                onXML = "";
                                if (competitionpromotionList.size() > 0) {
                                    for (int j = 0; j < competitionpromotionList.size(); j++) {
                                        onXML = "[USER_DATA][MID]"
                                                + mid
                                                + "[/MID][CREATED_BY]"
                                                + username
                                                + "[/CREATED_BY]"

                                                + "[CATEGORY_ID]"
                                                + competitionpromotionList.get(j).getCategory_id().get(0)
                                                + "[/CATEGORY_ID]"
                                                + "[COMPANY_ID]"
                                                + competitionpromotionList.get(j).getCompany_id().get(0)
                                                + "[/COMPANY_ID]"
                                                + "[BRAND_ID]"
                                                + competitionpromotionList.get(j).getBrand_id().get(0)
                                                + "[/BRAND_ID]"
                                                + "[SKU_ID]"
                                                + competitionpromotionList.get(j).getSku_id().get(0)
                                                + "[/SKU_ID]"
                                                + "[PRICEOFF_SPIN]"
                                                + competitionpromotionList.get(j).getPriceOFFtoggleValue()
                                                + "[/PRICEOFF_SPIN]"
                                                + "[PRICEOFF_QTY]"
                                                + competitionpromotionList.get(j).getPriceOFF_edtRS()
                                                + "[/PRICEOFF_QTY]"

                                                + "[SKU_MRP]"
                                                + competitionpromotionList.get(j).getMRP_sku().get(0)
                                                + "[/SKU_MRP]"

                                                + "[/USER_DATA]";

                                        after_add_xml = after_add_xml + onXML;
                                    }


                                    after_add_xml = "[DATA]" + after_add_xml + "[/DATA]";
                                    request = new SoapObject(CommonString.NAMESPACE, CommonString.METHOD_UPLOAD_STOCK_XML_DATA);

                                    request.addProperty("MID", mid);
                                    request.addProperty("KEYS", "COMP_PROMOTION_TRACKING_DATA");
                                    request.addProperty("USERNAME", username);
                                    request.addProperty("XMLDATA", after_add_xml);
                                    envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
                                    envelope.dotNet = true;
                                    envelope.setOutputSoapObject(request);
                                    androidHttpTransport = new HttpTransportSE(CommonString.URL);
                                    androidHttpTransport.call(CommonString.SOAP_ACTION_UPLOAD_ASSET_XMLDATA, envelope);
                                    result = (Object) envelope.getResponse();
                                    if (!result.toString().equalsIgnoreCase(CommonString.KEY_SUCCESS)) {
                                        if (result.toString().equalsIgnoreCase(CommonString.KEY_FALSE)) {
                                            return CommonString.METHOD_UPLOAD_ASSET;
                                        }

                                        FailureXMLHandler failureXMLHandler = new FailureXMLHandler();
                                        xmlR.setContentHandler(failureXMLHandler);
                                        InputSource is = new InputSource();
                                        is.setCharacterStream(new StringReader(result.toString()));
                                        xmlR.parse(is);
                                        failureGetterSetter = failureXMLHandler.getFailureGetterSetter();
                                        if (failureGetterSetter.getStatus().equalsIgnoreCase(CommonString.KEY_FAILURE)) {
                                            return CommonString.METHOD_UPLOAD_ASSET + "," + failureGetterSetter.getErrorMsg();
                                        }
                                    }
                                }
                                runOnUiThread(new Runnable() {

                                    public void run() {
                                        message.setText("Sku Data Uploaded");
                                    }
                                });
                            }
                            database.open();
                            database.updateCoverageStatus(coverageBeanlist.get(i).getStoreId(), CommonString.KEY_D, coverageBeanlist.get(i).getProcess_id());
                            database.updateStoreStatusOnLeave(coverageBeanlist.get(i).getStoreId(), visit_date, CommonString.KEY_D, coverageBeanlist.get(i).getProcess_id());
                            String statusxml = "";
                            if (coverageBeanlist.get(i).getReasonid().equals("2") ||
                                    coverageBeanlist.get(i).getReasonid().equals("7")) {
                                statusxml = "[DATA][USER_DATA][STORE_ID]"
                                        + coverageBeanlist.get(i).getStoreId()
                                        + "[/STORE_ID][CREATED_BY]" + username
                                        + "[/CREATED_BY][VISIT_DATE]"
                                        + coverageBeanlist.get(i).getVisitDate()
                                        + "[/VISIT_DATE][STATUS]"
                                        + "U"
                                        + "[/STATUS][/USER_DATA][/DATA]";

                                leavetrue = true;
                            } else {
                                statusxml = "[DATA][USER_DATA][STORE_ID]"
                                        + coverageBeanlist.get(i).getStoreId()
                                        + "[/STORE_ID][CREATED_BY]" + username
                                        + "[/CREATED_BY][VISIT_DATE]"
                                        + coverageBeanlist.get(i).getVisitDate()
                                        + "[/VISIT_DATE][STATUS]"
                                        + CommonString.KEY_D
                                        + "[/STATUS][/USER_DATA][/DATA]";
                            }

                            // SET COVERAGE STATUS
                            request = new SoapObject(CommonString.NAMESPACE, CommonString.METHOD_SET_COVERAGE_STATUS);
                            request.addProperty("onXML", statusxml);
                            envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
                            envelope.dotNet = true;
                            envelope.setOutputSoapObject(request);
                            androidHttpTransport = new HttpTransportSE(CommonString.URL);
                            androidHttpTransport.call(CommonString.SOAP_ACTION_SET_COVERAGE_STATUS, envelope);
                            result = (Object) envelope.getResponse();
                            if (result.toString().equalsIgnoreCase(CommonString.KEY_SUCCESS)) {
                                if (leavetrue) {
                                    database.updateCoverageStatus(coverageBeanlist.get(i).getStoreId(), CommonString.KEY_U, coverageBeanlist.get(i).getProcess_id());
                                    database.updateStoreStatusOnLeave(coverageBeanlist.get(i).getStoreId(), visit_date, CommonString.KEY_U, coverageBeanlist.get(i).getProcess_id());
                                }
                            } else {
                                if (result.toString().equalsIgnoreCase(CommonString.KEY_FALSE)) {
                                }
                            }
                        }
                    }
                }

                String geo_xml = "";
                ArrayList<String> geotemplist = new ArrayList<String>();
                if (geotaglist.size() > 0) {
                    for (int i = 0; i < geotaglist.size(); i++) {
                        runOnUiThread(new Runnable() {

                            public void run() {
                                // TODO Auto-generated method stub
                                k = k + factor;
                                pb.setProgress(k);
                                percentage.setText(k + "%");
                                message.setText("Uploading Geotag Data...");
                            }
                        });

                        String onXML = "[GeoTag_DATA][STORE_ID]"
                                + geotaglist.get(i).getStoreid()
                                + "[/STORE_ID]"
                                + "[LATTITUDE]"
                                + geotaglist.get(i).getLatitude()
                                + "[/LATTITUDE]"
                                + "[LONGITUDE]"
                                + geotaglist.get(i).getLongitude()
                                + "[/LONGITUDE]"
                                + "[FRONT_IMAGE]"
                                + geotaglist.get(i).getUrl1()
                                + "[/FRONT_IMAGE]"

                                + "[CREATED_BY]" + username
                                + "[/CREATED_BY][/GeoTag_DATA]";

                        geo_xml = geo_xml + onXML;

                        geotemplist.add(geotaglist.get(i).getStoreid());

                    }

                    geo_xml = "[DATA]" + geo_xml + "[/DATA]";
                    SoapObject request = new SoapObject(CommonString.NAMESPACE, CommonString.METHOD_UPLOAD_STOCK_XML_DATA);
                    request.addProperty("MID", mid);
                    request.addProperty("KEYS", "GEOTAG_NEW_DATA");
                    request.addProperty("USERNAME", username);

                    request.addProperty("XMLDATA", geo_xml);
                    SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
                    envelope.dotNet = true;
                    envelope.setOutputSoapObject(request);
                    HttpTransportSE androidHttpTransport = new HttpTransportSE(CommonString.URL);
                    androidHttpTransport.call(CommonString.SOAP_ACTION_UPLOAD_ASSET_XMLDATA, envelope);
                    Object result = (Object) envelope.getResponse();
                    if (result.toString().equalsIgnoreCase(CommonString.KEY_SUCCESS)) {
                        database.open();
                        for (int i = 0; i < geotemplist.size(); i++) {
                            database.updateGeoTagData(geotemplist.get(i).toString(), CommonString.KEY_D);
                            database.updateDataStatus(geotemplist.get(i).toString(), CommonString.KEY_D);
                        }
                    } else {
                        if (result.toString().equalsIgnoreCase(CommonString.KEY_FALSE)) {
                            return CommonString.METHOD_UPLOAD_ASSET;
                        } else if (!result.toString().equalsIgnoreCase(CommonString.KEY_SUCCESS)) {
                            return CommonString.METHOD_UPLOAD_ASSET;
                        }
                    }
                }

                return CommonString.KEY_SUCCESS;

            } catch (Exception ex) {
                Crashlytics.logException(ex);
                ex.printStackTrace();
            }
            return "";
        }
    }
}
