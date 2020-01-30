package com.cpm.DailyEntry;

import java.io.File;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Calendar;

import com.cpm.Constants.CommonFunctions;
import com.cpm.Constants.CommonString;

import com.cpm.database.GSKMTDatabase;
import com.cpm.delegates.CoverageBean;
import com.cpm.delegates.ReasonModel;
import com.cpm.delegates.StoreBean;

import com.crashlytics.android.Crashlytics;
import com.example.gsk_mtt.R;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.provider.MediaStore;


import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;
import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

public class NonWorkingActivity extends Activity implements OnItemSelectedListener, OnClickListener {

    Spinner reason, sub_reason;
    String reasonname, reasonid, image, entry, intime, reasonval, sub_reason_id = "";
    ArrayList<ReasonModel> reasonData = new ArrayList<ReasonModel>();
    ArrayList<ReasonModel> subreasonData = new ArrayList<>();
    ArrayList<StoreBean> storelist = new ArrayList<>();
    private ArrayAdapter<CharSequence> reason_adapter;
    private ArrayAdapter<CharSequence> sub_reason_adapter;
    Button save;
    EditText remarks;
    protected String _pathforcheck = "";
    protected String _path;
    GSKMTDatabase db;
    AlertDialog alert;
    ImageView img;
    SharedPreferences preferences;
    String process_id, username, app_version, date, store_id, lat = "0.0", longi = "0.0", meetingstatus, storename;
    TextView percentage, message;
    private String image1 = "";
    String str, errormesg;
    Data data;
    ProgressBar pb;
    Dialog dialog;
    boolean upload_status = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.non_working);

        reason = (Spinner) findViewById(R.id.reason_spinner);
        sub_reason = (Spinner) findViewById(R.id.sub_reason);
        save = (Button) findViewById(R.id.save);
        remarks = (EditText) findViewById(R.id.remark);
        img = (ImageView) findViewById(R.id.image);
        preferences = PreferenceManager.getDefaultSharedPreferences(this);
        process_id = preferences.getString(CommonString.KEY_PROCESS_ID, null);
        intime = getCurrentTime();
        username = preferences.getString(CommonString.KEY_USERNAME, null);
        app_version = preferences.getString(CommonString.KEY_VERSION, null);
        meetingstatus = preferences.getString("EmpMeetingStatus", null);
        date = preferences.getString(CommonString.KEY_DATE, null);
        store_id = preferences.getString(CommonString.KEY_STORE_ID, null);
        storename = preferences.getString(CommonString.KEY_STORE_NAME, "");
        if ((new File(Environment.getExternalStorageDirectory() + "/MT_GSK_Images/")).exists()) {
            Log.i("directory is created", "directory is created");
        } else {
            (new File(Environment.getExternalStorageDirectory() + "/MT_GSK_Images/")).mkdir();
        }
        str = Environment.getExternalStorageDirectory() + "/MT_GSK_Images/";
        db = new GSKMTDatabase(NonWorkingActivity.this);
        db.open();
        storelist = db.getStoreData(date);
        if (storelist.size() > 0) {
            for (int i = 0; i < storelist.size(); i++) {
                if (storelist.get(i).getUPLOAD_STATUS().equalsIgnoreCase(CommonString.KEY_D) ||
                        storelist.get(i).getUPLOAD_STATUS().equalsIgnoreCase(CommonString.KEY_U) ||
                        storelist.get(i).getUPLOAD_STATUS().equalsIgnoreCase(CommonString.KEY_P)) {
                    upload_status = true;
                    break;

                }
            }
        }

        reasonData = db.getNonWorkingReason(upload_status);
        for (int i = 0; i < reasonData.size(); i++) {
            if (reasonData.get(i).getReason().equalsIgnoreCase("pjp deviation")) {
                reasonData.remove(i);
            }
            if (!meetingstatus.equalsIgnoreCase("N")) {
                if (reasonData.get(i).getReason().equalsIgnoreCase("Meeting")) {
                    reasonData.remove(i);
                }
            }
        }

        reason_adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item);
        sub_reason_adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item);
        reason_adapter.add("Select Reason");
        sub_reason_adapter.add("Select Sub-Reason");
        for (int i = 0; i < reasonData.size(); i++) {
            reason_adapter.add(reasonData.get(i).getReason());
        }

        reason.setAdapter(reason_adapter);
        reason_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sub_reason_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        reason.setOnItemSelectedListener(this);
        sub_reason.setOnItemSelectedListener(this);
        img.setOnClickListener(this);


        save.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                if (check_condition()) {
                    if (check_condition1()) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(NonWorkingActivity.this);
                        builder.setMessage("Are you sure you want to save and upload").setCancelable(false).setPositiveButton(android.R.string.yes,
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        try {
                                            // db.updateStoreStatusOnLeave(visit_storeid, date, CommonString.KEY_CHECK_IN, visit_process_id);
                                            ///////changes by jeevan for holidays
                                            if (reasonid.equals("2") || reasonid.equals("7")) {
                                                CoverageBean data = new CoverageBean();
                                                data.setStoreId(store_id);
                                                data.setImage(image1);
                                                data.setLatitude(lat);
                                                data.setLongitude(longi);
                                                data.setVisitDate(date);
                                                data.setInTime(intime);
                                                data.setOutTime(getCurrentTime());
                                                data.setReasonid(reasonid);
                                                data.setRemark(remarks.getText().toString().replaceAll("[&^<>{}'$]", " "));
                                                data.setUserId(username);
                                                data.setApp_version(app_version);
                                                data.setProcess_id(process_id);
                                                data.setImage_allow("");
                                                data.setSub_reasonId(sub_reason_id);
                                                data.setStatus(CommonString.STORE_STATUS_LEAVE);
                                                data.setCHECKOUT_IMG(image1);
                                                // db.InsertCoverageInLeaveCase(data, storelist);
                                                new UploadNonworkingData(NonWorkingActivity.this, data).execute();
                                            } else {
                                                ArrayList<CoverageBean> coverageBeanlist = new ArrayList<CoverageBean>();
                                                coverageBeanlist = db.getCoverageData(date, null, process_id);
                                                if (coverageBeanlist.size() > 0) {
                                                    for (int i = 0; i < coverageBeanlist.size(); i++) {
                                                        if (coverageBeanlist.get(i).getReasonid().equals("2") ||
                                                                coverageBeanlist.get(i).getReasonid().equals("7")) {
                                                            db.deleteCoverageFOR();
                                                        }
                                                    }
                                                }

                                                CoverageBean data = new CoverageBean();
                                                data.setImage(image1);
                                                data.setStoreId(store_id);
                                                data.setLatitude(lat);
                                                data.setLongitude(longi);
                                                data.setVisitDate(date);
                                                data.setInTime(intime);
                                                data.setOutTime(getCurrentTime());
                                                data.setReasonid(reasonid);
                                                data.setRemark(remarks.getText().toString().replaceAll("[&^<>{}'$]", " "));
                                                data.setUserId(username);
                                                data.setApp_version(app_version);
                                                data.setProcess_id(process_id);
                                                data.setImage_allow("");
                                                data.setSub_reasonId(sub_reason_id);
                                                data.setStatus(CommonString.STORE_STATUS_LEAVE);
                                                data.setCHECKOUT_IMG(image1);
                                                new UploadNonworkingData(NonWorkingActivity.this, data).execute();
                                            }
                                        } catch (Exception e) {
                                            Crashlytics.logException(e);
                                            e.printStackTrace();
                                        }
                                    }
                                }).setNegativeButton(android.R.string.cancel,
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        dialog.cancel();
                                    }
                                });
                        AlertDialog alert = builder.create();
                        alert.show();
                    } else {
                        Toast.makeText(NonWorkingActivity.this, "Please select a reason ", Toast.LENGTH_LONG).show();
                    }
                } else {
                    Toast.makeText(NonWorkingActivity.this, "Please take a photo", Toast.LENGTH_LONG).show();
                }

            }
        });

    }

    public boolean check_condition() {
        boolean result = true;
        if (reasonid.equals("13") || reasonid.equals("14")) {
            if (image1.equals("")) {
                result = false;
            }
        }
        return result;

    }

    public boolean check_condition1() {
        boolean result = true;
        if (reasonid.equals("")) {
            result = false;
        }
        return result;

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent in = new Intent(NonWorkingActivity.this, CopyOfStorelistActivity.class);
        startActivity(in);
        NonWorkingActivity.this.finish();
    }

    @Override
    public void onItemSelected(AdapterView<?> arg0, View arg1, int position,
                               long arg3) {
        switch (arg0.getId()) {
            case R.id.reason_spinner:
                if (position != 0 && reasonData.size() > 0) {
                    reasonname = reasonData.get(position - 1).getReason();
                    reasonval = reasonData.get(position - 1).getReasonValid();
                    reasonid = reasonData.get(position - 1).getReasonid();
                    subreasonData = db.getSubReasonNonWorking(reasonid);
                    if (subreasonData.size() > 0) {
                        sub_reason_adapter.clear();
                        for (int i = 0; i < subreasonData.size(); i++) {
                            sub_reason_adapter.add(subreasonData.get(i).getSub_reason());

                        }
                        sub_reason_adapter.notifyDataSetChanged();
                        sub_reason.setAdapter(sub_reason_adapter);

                        sub_reason.setOnItemSelectedListener(new OnItemSelectedListener() {

                            @Override
                            public void onItemSelected(AdapterView<?> parent,
                                                       View view, int position, long id) {
                                sub_reason_id = subreasonData.get(position).getSub_reasonId();
                            }

                            @Override
                            public void onNothingSelected(AdapterView<?> parent) {
                            }
                        });

                    } else {
                        sub_reason_adapter.clear();
                        sub_reason_id = "";
                    }

                } else {
                    reasonname = "";
                    reasonid = "";
                    reasonval = "";

                }

                if (reasonid.equals("13") || reasonid.equals("14")) {
                    img.setVisibility(View.VISIBLE);
                    img.setEnabled(true);
                    image1 = "";

                } else {
                    img.setVisibility(View.INVISIBLE);
                    img.setEnabled(false);
                    image1 = "";
                }

                break;
        }

    }

    public String getCurrentTime() {
        Calendar m_cal = Calendar.getInstance();
        String intime = m_cal.get(Calendar.HOUR_OF_DAY) + ":" + m_cal.get(Calendar.MINUTE) + ":" + m_cal.get(Calendar.SECOND);
        return intime;
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
    }

    @Override
    public void onClick(View v) {
        String tempDate = date.replace("/", "-");
        if (v.getId() == R.id.image) {
            _pathforcheck = store_id + "NonWorking" + username + tempDate + "image1" + ".jpg";
            _path = Environment.getExternalStorageDirectory() + "/MT_GSK_Images/" + _pathforcheck;
            Log.i("MakeMachine", "ButtonClickHandler.onClick()");
            CommonFunctions.startAnncaCameraActivity(NonWorkingActivity.this, _path);
        }
    }

    protected void startCameraActivity() {
        try {
            Log.i("MakeMachine", "startCameraActivity()");
            File file = new File(_path);
            Uri outputFileUri = Uri.fromFile(file);
            Intent intent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, outputFileUri);
            startActivityForResult(intent, 0);
        } catch (Exception e) {
            e.printStackTrace();
        }
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
                        if (new File(str + _pathforcheck).exists()) {
                            String metadata = CommonFunctions.setMetadataAtImages(storename, store_id, "NONWORKING IMAGE", username);
                            CommonFunctions.addMetadataAndTimeStampToImage(NonWorkingActivity.this, _path, metadata, date);
                            img.setBackgroundResource(R.drawable.camera_tick_ico);
                            image1 = _pathforcheck;
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

    private class Data {
        String name;
        int value;
    }

    private class UploadNonworkingData extends AsyncTask<Void, Data, String> {
        CoverageBean coverageBean;
        Context context;

        UploadNonworkingData(Context context, CoverageBean coverageBean) {
            this.context = context;
            this.coverageBean = coverageBean;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog = new Dialog(context);
            dialog.setContentView(R.layout.custom);
            dialog.setTitle("Uploading NonWorking information...");
            dialog.setCancelable(false);
            pb = (ProgressBar) dialog.findViewById(R.id.progressBar1);
            percentage = (TextView) dialog.findViewById(R.id.percentage);
            message = (TextView) dialog.findViewById(R.id.message);
            final TextView tv_title = dialog.findViewById(R.id.tv_title);
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    tv_title.setText("Uploading NonWorking info");
                }
            });
            dialog.show();
        }

        @Override
        protected void onProgressUpdate(Data... values) {
            pb.setProgress(values[0].value);
            percentage.setText(values[0].value + "%");
            message.setText(values[0].name);
        }

        @Override
        protected String doInBackground(Void... strings) {
            try {

                data = new Data();
                data.value = 20;
                data.name = "Data Uploading";
                publishProgress(data);
                ///////////////////////for coverage upload...............Service...........
                if (reasonid != null && reasonid.equals("2") || reasonid != null && reasonid.equals("7")) {
                    String onXMLCOV = "[DATA][USER_DATA][STORE_ID]"
                            + coverageBean.getStoreId()
                            + "[/STORE_ID]"
                            + "[VISIT_DATE]"
                            + coverageBean.getVisitDate()
                            + "[/VISIT_DATE]"
                            + "[LATITUDE]"
                            + coverageBean.getLatitude()
                            + "[/LATITUDE]"
                            + "[STORE_IMAGE]"
                            + coverageBean.getImage()
                            + "[/STORE_IMAGE]"
                            + "[LONGITUDE]"
                            + coverageBean.getLongitude()
                            + "[/LONGITUDE]"
                            + "[REASON_REMARK]"
                            + coverageBean.getRemark()
                            + "[/REASON_REMARK][REASON_ID]"
                            + coverageBean.getReasonid()
                            + "[/REASON_ID][SUB_REASON_ID]"
                            + "0"
                            + "[/SUB_REASON_ID][APP_VERSION]" + app_version
                            + "[/APP_VERSION] [IMAGE_ALLOW]" + "0"
                            + "[/IMAGE_ALLOW]" + "[USER_ID]" + username
                            + "[/USER_ID]" + "[PROCESS_ID]" + process_id
                            + "[/PROCESS_ID]"
                            + "[CHECKOUT_IMAGE]"
                            + coverageBean.getImage()
                            + "[/CHECKOUT_IMAGE]"
                            + "[/USER_DATA][/DATA]";

                    SoapObject request = new SoapObject(CommonString.NAMESPACE, CommonString.METHOD_UPLOAD_COVERAGE_NONWORKING_DATA);
                    request.addProperty("onXML", onXMLCOV);
                    SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
                    envelope.dotNet = true;
                    envelope.setOutputSoapObject(request);
                    HttpTransportSE androidHttpTransport = new HttpTransportSE(CommonString.URL);
                    androidHttpTransport.call(CommonString.SOAP_ACTION_METHOD_UPLOAD_COVERAGE_NONWORKING_DATA, envelope);
                    Object result = (Object) envelope.getResponse();
                    if (result.toString().contains(CommonString.KEY_SUCCESS)) {
                        SharedPreferences.Editor editor = preferences.edit();
                        editor.putString(CommonString.KEY_STOREVISITED, "none");
                        editor.putString(CommonString.KEY_STOREVISITED_STATUS, "");
                        editor.putString(CommonString.KEY_STORE_IN_TIME, "");
                        editor.commit();
                        db.open();
                        db.updateStoreStatusOnLeaveOrHoliday(storelist, date, CommonString.KEY_U);
                        data.value = 100;
                        data.name = "NonWorking data uploaded";
                        publishProgress(data);
                        errormesg = CommonString.KEY_SUCCESS;
                    } else {
                        if (!result.toString().equalsIgnoreCase(CommonString.KEY_SUCCESS)) {
                            errormesg = CommonString.METHOD_UPLOAD_COVERAGE_NONWORKING_DATA + "," + result.toString().trim();
                        }
                    }
                } else {
                    String onXMLCOV = "[DATA][USER_DATA][STORE_ID]"
                            + coverageBean.getStoreId()
                            + "[/STORE_ID]"
                            + "[VISIT_DATE]"
                            + coverageBean.getVisitDate()
                            + "[/VISIT_DATE]"
                            + "[LATITUDE]"
                            + coverageBean.getLatitude()
                            + "[/LATITUDE]"
                            + "[STORE_IMAGE]"
                            + coverageBean.getImage()
                            + "[/STORE_IMAGE]"
                            + "[LONGITUDE]"
                            + coverageBean.getLongitude()
                            + "[/LONGITUDE]"
                            + "[IN_TIME]"
                            + intime
                            + "[/IN_TIME][OUT_TIME]"
                            + getCurrentTime()
                            + "[/OUT_TIME][UPLOAD_STATUS]L"
                            + "[/UPLOAD_STATUS][CREATED_BY]" + username
                            + "[/CREATED_BY][REASON_REMARK]"
                            + coverageBean.getRemark()
                            + "[/REASON_REMARK][REASON_ID]"
                            + coverageBean.getReasonid()
                            + "[/REASON_ID][SUB_REASON_ID]"
                            + "0"
                            + "[/SUB_REASON_ID][APP_VERSION]" + app_version
                            + "[/APP_VERSION] [IMAGE_ALLOW]" + "0"
                            + "[/IMAGE_ALLOW]" + "[USER_ID]" + username
                            + "[/USER_ID]" + "[PROCESS_ID]" + process_id
                            + "[/PROCESS_ID]"
                            + "[CHECKOUT_IMAGE]"
                            + coverageBean.getImage()
                            + "[/CHECKOUT_IMAGE]"
                            + "[/USER_DATA][/DATA]";

                    SoapObject request = new SoapObject(CommonString.NAMESPACE, CommonString.METHOD_UPLOAD_DR_STORE_COVERAGE_LOC);
                    request.addProperty("onXML", onXMLCOV);
                    SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
                    envelope.dotNet = true;
                    envelope.setOutputSoapObject(request);
                    HttpTransportSE androidHttpTransport = new HttpTransportSE(CommonString.URL);
                    androidHttpTransport.call(CommonString.SOAP_ACTION_UPLOAD_DR_STORE_COVERAGE, envelope);
                    Object result = (Object) envelope.getResponse();
                    if (result.toString().contains(CommonString.KEY_SUCCESS)) {
                        SharedPreferences.Editor editor = preferences.edit();
                        editor.putString(CommonString.KEY_STOREVISITED, "none");
                        editor.putString(CommonString.KEY_STOREVISITED_STATUS, "");
                        editor.putString(CommonString.KEY_STORE_IN_TIME, "");
                        editor.commit();
                        db.open();
                        db.InsertCoverage(coverageBean, store_id, date, process_id);
                        db.updateStoreStatusOnLeave(coverageBean.getStoreId(), coverageBean.getVisitDate(), CommonString.STORE_STATUS_LEAVE,
                                coverageBean.getProcess_id());
                        data.value = 100;
                        data.name = "NonWorking data uploaded";
                        publishProgress(data);
                        errormesg = CommonString.KEY_SUCCESS;
                    } else {
                        if (!result.toString().equalsIgnoreCase(CommonString.KEY_SUCCESS)) {
                            errormesg = CommonString.METHOD_UPLOAD_DR_STORE_COVERAGE_LOC + "," + result.toString().trim();
                        }
                    }
                }

                return errormesg;
            } catch (Exception e) {
                Crashlytics.logException(e);
                errormesg = e.toString();
                return errormesg;
            }
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            dialog.dismiss();
            if (s.equalsIgnoreCase(CommonString.KEY_SUCCESS)) {
                Intent intent = new Intent(getApplicationContext(), CopyOfStorelistActivity.class);
                startActivity(intent);
                NonWorkingActivity.this.finish();
            } else {
                Toast.makeText(getApplicationContext(), errormesg + " Please Try Again", Toast.LENGTH_LONG).show();
            }
        }
    }
}
