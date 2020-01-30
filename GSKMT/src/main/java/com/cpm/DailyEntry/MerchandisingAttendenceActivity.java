package com.cpm.DailyEntry;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.cpm.Constants.CommonFunctions;
import com.cpm.Constants.CommonString;
import com.cpm.database.GSKMTDatabase;
import com.cpm.delegates.StoreBean;
import com.cpm.gsk_mt.MainMenuActivity;
import com.cpm.message.AlertMessage;
import com.cpm.upload.Base64;
import com.cpm.xmlGetterSetter.NonWorkingAttendenceGetterSetter;
import com.crashlytics.android.Crashlytics;
import com.example.gsk_mtt.R;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Calendar;

public class MerchandisingAttendenceActivity extends Activity
        implements View.OnClickListener, AdapterView.OnItemSelectedListener {
    Button attendenc_BTN;
    Spinner supAtten_spinner;
    ImageView imgcam;
    String reasonname = "", reasonid = "", entry_allow = "";
    protected String _pathforcheck = "";
    protected String _path;
    GSKMTDatabase db;
    private SharedPreferences preferences = null;
    private SharedPreferences.Editor editor = null;
    String username, date, image_str = "";
    ArrayList<StoreBean> storelist = new ArrayList<>();
    ArrayList<NonWorkingAttendenceGetterSetter> nonworkingAttendenceList = new ArrayList<>();
    private ArrayAdapter<CharSequence> reason_adapter;
    ProgressDialog loading;
    Context context;
    Object result = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_merchandising_attendence);
        attendenc_BTN = findViewById(R.id.attendenc_BTN);
        supAtten_spinner = findViewById(R.id.supAtten_spinner);
        imgcam = findViewById(R.id.imgcam);
        context = this;
        preferences = PreferenceManager.getDefaultSharedPreferences(this);
        editor = preferences.edit();
        username = preferences.getString(CommonString.KEY_USERNAME, null);
        date = preferences.getString(CommonString.KEY_DATE, null);

        db = new GSKMTDatabase(this);
        db.open();
        storelist = db.getStoreData(date);
        nonworkingAttendenceList = db.getNonWoATTENDENCEData();
        if (nonworkingAttendenceList.size() > 0) {
            reason_adapter = new ArrayAdapter<>(this, R.layout.spinner_text_view);
            reason_adapter.add("-Select Reason-");
            for (int i = 0; i < nonworkingAttendenceList.size(); i++) {
                reason_adapter.add(nonworkingAttendenceList.get(i).getREASON().get(0));
            }
            supAtten_spinner.setAdapter(reason_adapter);
            reason_adapter.setDropDownViewResource(R.layout.spinner_text_view);
            supAtten_spinner.setOnItemSelectedListener(this);
        }
        attendenc_BTN.setOnClickListener(this);
        imgcam.setOnClickListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Create a Folder for Images
        File file = new File(Environment.getExternalStorageDirectory(), "MT_GSK_Images");
        if (!file.isDirectory()) {
            file.mkdir();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.imgcam:
                _pathforcheck = username + "_ATTE_IMG_" + date.replace("/", "") + "_" + getCurrentTime().replace(":", "") + ".jpg";
                _path = CommonString.FILE_PATH + _pathforcheck;
                CommonFunctions.startAnncaCameraActivity(context, _path);
                break;

            case R.id.attendenc_BTN:
                if (checkNetIsAvailable()) {
                    if (validate()) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(context)
                                .setTitle("Parinaam").setMessage("Do you want to upload Merchandiser Attendance Data");
                        builder.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                if (entry_allow.equals("1")) {
                                    new UploadAttendenceMerchandising(context, username, reasonid, date, image_str, CommonString.KEY_P).execute();
                                } else {
                                    new UploadAttendenceMerchandising(context, username, reasonid, date, image_str, CommonString.KEY_U).execute();
                                }
                                dialogInterface.dismiss();
                            }
                        });
                        builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.dismiss();
                            }
                        });
                        builder.show();
                    }
                } else {
                    Toast.makeText(MerchandisingAttendenceActivity.this, "No internet availeble ! Please check your connection", Toast.LENGTH_SHORT).show();
                }
                break;
        }

    }

    public boolean checkNetIsAvailable() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null && activeNetwork.isConnectedOrConnecting();
        return isConnected;
    }

    private void ToastMSG(String strM) {
        Toast.makeText(MerchandisingAttendenceActivity.this, strM, Toast.LENGTH_LONG).show();
    }

    private boolean validate() {
        boolean status = true;
        if (supAtten_spinner.getSelectedItem().toString().equalsIgnoreCase("-Select Reason-")) {
            ToastMSG("Please Select Reason Dropdown");
            status = false;
        }
        if (status && !entry_allow.equals("") && entry_allow.equals("1")) {
            if (image_str.equals("")) {
                ToastMSG("Please Capture Image");
                status = false;
            }
        }
        return status;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.i("MakeMachine", "resultCode: " + resultCode);
        switch (resultCode) {
            case 0:
                Log.i("MakeMachine", "User cancelled");
                break;
            case -1:
                try {
                    if (_pathforcheck != null && !_pathforcheck.equals("")) {
                        if (new File(CommonString.FILE_PATH + _pathforcheck).exists()) {
                            String metadata = CommonFunctions.setMetadataAtImagesforattendence("Attendance Image", username);
                            CommonFunctions.addMetadataAndTimeStampToImage(context, _path, metadata, date);
                            imgcam.setBackgroundResource(R.drawable.camera_tick_ico);
                            image_str = _pathforcheck;
                        }
                    }
                } catch (Exception e) {
                    Crashlytics.logException(e);
                    e.printStackTrace();
                }
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }


    protected void startCameraActivity() {
        try {
            Log.i("MakeMachine", "startCameraActivity()");
            File file = new File(_path);
            Uri outputFileUri = Uri.fromFile(file);
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, outputFileUri);
            startActivityForResult(intent, 0);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public String getCurrentTime() {
        Calendar m_cal = Calendar.getInstance();
        String hour_str = String.valueOf(m_cal.get(Calendar.HOUR_OF_DAY));
        hour_str = "00" + hour_str;
        hour_str = hour_str.substring(hour_str.length() - 2, hour_str.length());
        String minute_str = String.valueOf(m_cal.get(Calendar.MINUTE));
        minute_str = "00" + minute_str;
        minute_str = minute_str.substring(minute_str.length() - 2, minute_str.length());
        String second_str = String.valueOf(m_cal.get(Calendar.SECOND));
        second_str = "00" + second_str;
        second_str = second_str.substring(second_str.length() - 2, second_str.length());
        String intime = hour_str + ":" + minute_str + ":" + second_str;

        return intime;
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
        switch (adapterView.getId()) {
            case R.id.supAtten_spinner:
                if (position != 0 && nonworkingAttendenceList.size() > 0) {
                    reasonname = nonworkingAttendenceList.get(position - 1).getREASON().get(0);
                    reasonid = nonworkingAttendenceList.get(position - 1).getREASON_ID().get(0);
                    entry_allow = nonworkingAttendenceList.get(position - 1).getENTRY_ALLOW().get(0);
                    if (entry_allow.equals("1")) {
                        imgcam.setVisibility(View.VISIBLE);
                    } else {
                        imgcam.setVisibility(View.GONE);
                    }
                } else {
                    reasonname = "";
                    reasonid = "";
                    entry_allow = "";
                    imgcam.setVisibility(View.GONE);
                }
                break;
        }

    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {
    }

    private class UploadAttendenceMerchandising extends AsyncTask<String, String, String> {
        Context context;
        String user_NM, reason_cd, visit_date, att_image, staus;

        UploadAttendenceMerchandising(Context context, String user_NM, String reason_cd, String visit_date, String att_image, String staus) {
            this.context = context;
            this.user_NM = user_NM;
            this.reason_cd = reason_cd;
            this.visit_date = visit_date;
            this.att_image = att_image;
            this.staus = staus;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            loading = ProgressDialog.show(context, "Uploading Attendance Data", "Please wait...", false, false);
        }

        @Override
        protected String doInBackground(String... strings) {
            try {
                String onXML = "", sos_xml = "";
                String final_xml = "";
                onXML = "[USER_DATA][REASON_ID]"
                        + reason_cd
                        + "[/REASON_ID]"
                        + "[USER_ID]"
                        + user_NM
                        + "[/USER_ID]"
                        + "[ATT_DATE]"
                        + visit_date
                        + "[/ATT_DATE]"
                        + "[IMAGE_URL]"
                        + att_image
                        + "[/IMAGE_URL]"
                        + "[/USER_DATA]";
                final_xml = final_xml + onXML;
                sos_xml = "[DATA]" + final_xml + "[/DATA]";
                SoapObject request1 = new SoapObject(CommonString.NAMESPACE, CommonString.MEHTOD_MERCHANDISOR_ATTENDENCE);
                request1.addProperty("onXML", sos_xml);
                SoapSerializationEnvelope envelope1 = new SoapSerializationEnvelope(SoapEnvelope.VER11);
                envelope1.dotNet = true;
                envelope1.setOutputSoapObject(request1);
                HttpTransportSE androidHttpTransport1 = new HttpTransportSE(CommonString.URL);
                androidHttpTransport1.call(CommonString.SOAP_ACTION + CommonString.MEHTOD_MERCHANDISOR_ATTENDENCE, envelope1);
                Object result1 = (Object) envelope1.getResponse();
                if (result1.toString().equalsIgnoreCase(CommonString.KEY_SUCCESS)) {
                    if (entry_allow.equals("1")) {
                        db.open();
                        editor.putString(CommonString.KEY_ATTENDENCE_STATUS, "1");
                        editor.apply();
                        db.insertAttendenceData(user_NM, visit_date, staus, att_image, reasonname, reason_cd, entry_allow);
                        if (!image_str.equals("")) {
                            if (new File(Environment.getExternalStorageDirectory() + "/MT_GSK_Images/" + att_image).exists()) {
                                result = UploadPOSMImage(att_image, "SupAttendanceImages");
                                if (result.toString().equalsIgnoreCase(CommonString.KEY_FAILURE)) {
                                    return CommonString.METHOD_Upload_StoreDeviationImage;
                                }
                            }
                            return result.toString();
                        }
                    } else {
                        editor.putString(CommonString.KEY_ATTENDENCE_STATUS, "2");
                        editor.apply();
                        return result1.toString();
                    }


                } else {
                    return CommonString.KEY_FAILURE + "," + result1.toString();
                }
            } catch (MalformedURLException e) {
                runOnUiThread(new Runnable() {
                    public void run() {
                        ShowAlert2(AlertMessage.MESSAGE_SOCKETEXCEPTION);
                    }
                });
            } catch (final IOException e) {
                Crashlytics.logException(e);
                runOnUiThread(new Runnable() {
                    public void run() {
                        ShowAlert2(e.toString());
                    }
                });
            } catch (Exception e) {
                runOnUiThread(new Runnable() {
                    public void run() {
                        ShowAlert2(AlertMessage.MESSAGE_EXCEPTION);
                    }
                });
            }
            return "";
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            loading.dismiss();
            if (s.equalsIgnoreCase(CommonString.KEY_SUCCESS)) {
                if (entry_allow.equals("1")) {
                    Intent intent = new Intent(MerchandisingAttendenceActivity.this, CopyOfStorelistActivity.class);
                    startActivity(intent);
                    MerchandisingAttendenceActivity.this.finish();
                } else {
                    Intent intent = new Intent(MerchandisingAttendenceActivity.this, MainMenuActivity.class);
                    startActivity(intent);
                    MerchandisingAttendenceActivity.this.finish();
                }
                Toast.makeText(getApplicationContext(), "Attendance Uploaded Successfullly.", Toast.LENGTH_SHORT).show();
            } else if (s.equalsIgnoreCase(CommonString.METHOD_Upload_StoreDeviationImage)) {
                Toast.makeText(getApplicationContext(), "Image Not Uploaded Successfully. Please try again", Toast.LENGTH_SHORT).show();
            } else if (!s.equalsIgnoreCase(CommonString.KEY_SUCCESS)) {
                Toast.makeText(getApplicationContext(), "Error In Uploading Attendance", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public void ShowAlert2(String str) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Error");
        builder.setMessage(str).setCancelable(false)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();
                    }
                });
        AlertDialog alert = builder.create();
        alert.show();

    }

    public String UploadPOSMImage(String path, String folder) throws Exception {
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
            width_tmp /= 2;
            height_tmp /= 2;
            scale *= 2;
        }
        // Decode with inSampleSize
        BitmapFactory.Options o2 = new BitmapFactory.Options();
        o2.inSampleSize = scale;
        Bitmap bitmap = BitmapFactory.decodeFile(Environment.getExternalStorageDirectory() + "/MT_GSK_Images/" + path, o2);
        ByteArrayOutputStream bao = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 90, bao);
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
            return CommonString.KEY_SUCCESS;
        } else {
            return CommonString.KEY_FAILURE;
        }
    }
}

