package com.cpm.DailyEntry;

import java.io.File;
import java.io.StringReader;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;
import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;

import com.cpm.Constants.CommonFunctions;
import com.cpm.Constants.CommonString;
import com.cpm.database.GSKMTDatabase;
import com.cpm.delegates.CoverageBean;
import com.cpm.message.AlertMessage;
import com.cpm.xmlGetterSetter.FailureGetterSetter;
import com.cpm.xmlHandler.FailureXMLHandler;
import com.crashlytics.android.Crashlytics;
import com.example.gsk_mtt.R;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.location.Location;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.provider.MediaStore;

import android.provider.Settings;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class StoreInformationActivity extends Activity implements
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {
    String date, imgDate, store_id, _path, img1 = "", picStatus = "", process_id, reason_id = "0", remark = "", storename,
            intime, username, app_version, lat = "0.0", longi = "0.0", str, _pathforcheck = "", image1 = "";
    private FailureGetterSetter failureGetterSetter = null;
    ArrayList<CoverageBean> coveragelist = new ArrayList<>();
    private SharedPreferences.Editor editor = null;
    private SharedPreferences preferences;
    private LocationManager locmanager = null;
    boolean enabled;
    ArrayAdapter<String> dataAdapter;
    GoogleApiClient mGoogleApiClient;
    ProgressDialog loading;
    ImageView img;
    Spinner spin;
    Button save;
    GSKMTDatabase db;
    int pos = 0;
    TextView store_name;
    boolean flag = false;
    Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.store_info);
        context = this;
        img = (ImageView) findViewById(R.id.store_image);
        spin = (Spinner) findViewById(R.id.spinner1);
        save = (Button) findViewById(R.id.save_btn);
        store_name = (TextView) findViewById(R.id.storename);
        str = Environment.getExternalStorageDirectory() + "/MT_GSK_Images/";
        preferences = PreferenceManager.getDefaultSharedPreferences(context);
        process_id = preferences.getString(CommonString.KEY_PROCESS_ID, null);
        username = preferences.getString(CommonString.KEY_USERNAME, null);
        app_version = preferences.getString(CommonString.KEY_VERSION, null);
        storename = preferences.getString(CommonString.KEY_STORE_NAME, null);
        date = preferences.getString(CommonString.KEY_DATE, null);
        store_id = preferences.getString(CommonString.KEY_STORE_ID, null);
        imgDate = date.replace("/", "-");
        store_name.setText("Store - " + storename);
        db = new GSKMTDatabase(context);
        db.open();
        img.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                _pathforcheck = store_id + "storeImage" + imgDate + ".jpg";
                _path = str + _pathforcheck;
                CommonFunctions.startAnncaCameraActivity(context, _path);
                // startCameraActivity();
            }
        });
        List<String> list = new ArrayList<String>();
        list.add("Select");
        list.add("Allowed");
        list.add("NOT Allowed");
        dataAdapter = new ArrayAdapter<String>(context,
                android.R.layout.simple_spinner_item, list);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spin.setAdapter(dataAdapter);
        coveragelist = db.getCoverageData(date, store_id, process_id);
        if (coveragelist.size() > 0) {
            String image = coveragelist.get(0).getImage();
            String status = coveragelist.get(0).getImage_allow();
            if (!image.equalsIgnoreCase("")) {
                img.setBackgroundResource(R.drawable.camera_tick_ico);
                image1 = image;
            } else {
                img.setBackgroundResource(R.drawable.camera_ico);
            }
            if (status.equalsIgnoreCase("Allowed")) {
                pos = dataAdapter.getPosition("Allowed");
                spin.setSelection(pos);
            } else if (status.equalsIgnoreCase("NOT Allowed")) {
                pos = dataAdapter.getPosition("NOT Allowed");
                spin.setSelection(pos);
            }
        }
        spin.setOnItemSelectedListener(new OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position != 0) {
                    picStatus = spin.getSelectedItem().toString();
                    System.out.println(" ...." + picStatus);
                } else {
                    picStatus = "";
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });


        save.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                if (CheckNetAvailability()) {
                    if (!picStatus.equalsIgnoreCase("Select") && !image1.equalsIgnoreCase("")) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(context);
                        builder.setMessage("Are you sure you want to save").setCancelable(false).setPositiveButton("Yes",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        ArrayList<CoverageBean> coverageBeanlist = new ArrayList<CoverageBean>();
                                        coverageBeanlist = db.getCoverageData(date, store_id, process_id);
                                        if (coverageBeanlist.size() > 0) {
                                            String covintime = coverageBeanlist.get(0).getInTime();
                                            if (covintime.equalsIgnoreCase("")) {
                                                intime = getCurrentTime();
                                            } else {
                                                intime = covintime;
                                            }
                                        } else {
                                            intime = getCurrentTime();
                                        }
                                        runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                new InstantUpload(StoreInformationActivity.this, "error").execute();
                                            }
                                        });
                                        editor = preferences.edit();
                                        editor.putString(CommonString.KEY_LATITUDE, lat);
                                        editor.putString(CommonString.KEY_LONGITUDE, longi);
                                        editor.putString(CommonString.KEY_IN_TIME, intime);
                                        editor.commit();
                                    }
                                }).setNegativeButton("No",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        dialog.cancel();
                                    }
                                });
                        AlertDialog alert = builder.create();
                        alert.show();
                    } else {
                        Toast.makeText(getBaseContext(), "Please click image", Toast.LENGTH_LONG).show();
                    }
                } else {
                    Toast.makeText(getBaseContext(), "No internet Connection ! Please connect to network", Toast.LENGTH_LONG).show();
                }
            }
        });

        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
        }
        if (Build.VERSION.SDK_INT >= 23 &&
                ContextCompat.checkSelfPermission(getApplicationContext(),
                        Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(getApplicationContext(),
                        Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        locmanager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        enabled = locmanager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        // Check if enabled and if not send user to the GSP settings
        // Better solution would be to display a dialog and suggesting to
        // go to the settings
        if (!enabled) {
            AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
            // Setting Dialog Title
            alertDialog.setTitle("GPS IS DISABLED...");
            // Setting Dialog Message
            alertDialog.setMessage("Click ok to enable GPS.");
            // Setting Positive "Yes" Button
            alertDialog.setPositiveButton("YES",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                            startActivity(intent);
                            dialog.dismiss();
                        }
                    });
            // Setting Negative "NO" Button
            alertDialog.setNegativeButton("NO",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            // Write your code here to invoke NO event
                            dialog.cancel();
                        }
                    });
            // Showing Alert Message
            alertDialog.show();
        }
    }


    public boolean CheckNetAvailability() {

        boolean connected = false;
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE)
                .getState() == NetworkInfo.State.CONNECTED
                || connectivityManager.getNetworkInfo(
                ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED) {
            // we are connected to a network
            connected = true;
        }

        return connected;

    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent in = new Intent(StoreInformationActivity.this, CopyOfStorelistActivity.class);
        startActivity(in);
        StoreInformationActivity.this.finish();
    }


    public class InstantUpload extends AsyncTask<Void, String, String> {
        private Context context;
        String errormesg;

        InstantUpload(Context context, String string) {
            this.context = context;
            this.errormesg = string;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            loading = ProgressDialog.show(context, "Uploading store check in info", "Please wait...", false, false);
        }

        @Override
        protected String doInBackground(Void... params) {
            try {

                SAXParserFactory saxPF = SAXParserFactory.newInstance();
                SAXParser saxP = saxPF.newSAXParser();
                XMLReader xmlR = saxP.getXMLReader();
                String onXML = "[DATA][USER_DATA][USER_ID]" + username
                        + "[/USER_ID]" + " [VISIT_DATE]" + date
                        + "[/VISIT_DATE][IN_TIME]" + intime
                        + "[/IN_TIME][STORE_ID]" + store_id
                        + "[/STORE_ID] [PROCESS_ID]" + process_id + " [/PROCESS_ID][/USER_DATA][/DATA]";

                SoapObject request = new SoapObject(CommonString.NAMESPACE, CommonString.METHOD_STORE_VISIT);
                request.addProperty("onXML", onXML);
                SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
                envelope.dotNet = true;
                envelope.setOutputSoapObject(request);
                HttpTransportSE androidHttpTransport = new HttpTransportSE(CommonString.URL);
                androidHttpTransport.call(CommonString.SOAP_ACTION_STORE_VISIT, envelope);
                Object result = (Object) envelope.getResponse();
                if (result.toString().equalsIgnoreCase(CommonString.KEY_SUCCESS)) {
                    ///////////////////////for coverage upload...............Service...........
                    String onXMLCOV = "[DATA][USER_DATA][STORE_ID]"
                            + store_id
                            + "[/STORE_ID]"
                            + "[VISIT_DATE]"
                            + date
                            + "[/VISIT_DATE]"
                            + "[LATITUDE]"
                            + lat
                            + "[/LATITUDE]"
                            + "[STORE_IMAGE]"
                            + image1
                            + "[/STORE_IMAGE]"
                            + "[LONGITUDE]"
                            + longi
                            + "[/LONGITUDE]"
                            + "[IN_TIME]"
                            + getCurrentTime()
                            + "[/IN_TIME][OUT_TIME]"
                            + ""
                            + "[/OUT_TIME][UPLOAD_STATUS]I"
                            + "[/UPLOAD_STATUS][CREATED_BY]" + username
                            + "[/CREATED_BY][REASON_REMARK]"
                            + "0"
                            + "[/REASON_REMARK][REASON_ID]"
                            + reason_id
                            + "[/REASON_ID][SUB_REASON_ID]"
                            + "0"
                            + "[/SUB_REASON_ID][APP_VERSION]" + app_version
                            + "[/APP_VERSION] [IMAGE_ALLOW]" + "0"
                            + "[/IMAGE_ALLOW]" + "[USER_ID]" + username
                            + "[/USER_ID]" + "[PROCESS_ID]" + process_id
                            + "[/PROCESS_ID]"
                            + "[CHECKOUT_IMAGE]"
                            + ""
                            + "[/CHECKOUT_IMAGE]"
                            + "[/USER_DATA][/DATA]";

                    request = new SoapObject(CommonString.NAMESPACE, CommonString.METHOD_UPLOAD_DR_STORE_COVERAGE_LOC);
                    request.addProperty("onXML", onXMLCOV);
                    envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
                    envelope.dotNet = true;
                    envelope.setOutputSoapObject(request);
                    androidHttpTransport = new HttpTransportSE(CommonString.URL);
                    androidHttpTransport.call(CommonString.SOAP_ACTION_UPLOAD_DR_STORE_COVERAGE, envelope);
                    result = (Object) envelope.getResponse();
                    if (result.toString().contains(CommonString.KEY_SUCCESS)) {
                        errormesg = CommonString.KEY_SUCCESS;
                    } else {
                        errormesg = CommonString.METHOD_UPLOAD_DR_STORE_COVERAGE_LOC + "," + result.toString();
                    }
                } else {
                    if (result.toString().equalsIgnoreCase(CommonString.KEY_FALSE)) {
                        errormesg = CommonString.METHOD_STORE_VISIT;
                    }

                    // for failure
                    FailureXMLHandler failureXMLHandler = new FailureXMLHandler();
                    xmlR.setContentHandler(failureXMLHandler);
                    InputSource is = new InputSource();
                    is.setCharacterStream(new StringReader(result.toString()));
                    xmlR.parse(is);
                    failureGetterSetter = failureXMLHandler.getFailureGetterSetter();
                    if (failureGetterSetter.getStatus().equalsIgnoreCase(CommonString.KEY_FAILURE)) {
                        errormesg = CommonString.METHOD_STORE_VISIT + "," + failureGetterSetter.getErrorMsg();
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
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            loading.dismiss();
            if (result.equals(CommonString.KEY_SUCCESS)) {
                CoverageBean data = new CoverageBean();
                db.open();
                data.setImage(image1);
                data.setStoreId(store_id);
                data.setLatitude(lat);
                data.setLongitude(longi);
                data.setVisitDate(date);
                data.setInTime(getCurrentTime());
                data.setOutTime("");
                data.setReasonid(reason_id);
                data.setRemark(remark);
                data.setUserId(username);
                data.setApp_version(app_version);
                data.setProcess_id(process_id);
                data.setImage_allow(picStatus);
                data.setStatus(CommonString.KEY_CHECK_IN);
                data.setCHECKOUT_IMG("");
                db.open();
                db.InsertCoverage(data, store_id, date, process_id);
                db.open();
                db.updateStoreStatusOnLeave(store_id, date, CommonString.KEY_CHECK_IN, process_id);
                Intent in = new Intent(StoreInformationActivity.this, CopyOfStorevisitedYesMenu.class);
                startActivity(in);
                StoreInformationActivity.this.finish();
                Toast.makeText(getApplicationContext(), "Intime Successfully Uploaded", Toast.LENGTH_LONG).show();
            } else {
                if (!flag) {
                    final AlertMessage message = new AlertMessage(StoreInformationActivity.this, AlertMessage.MESSAGE_EXCEPTION + result, "acra_checkout", result, "");
                    message.showMessage();
                    flag = true;
                } else {
                    flag = false;
                }
            }
        }
    }

    public String getCurrentTime() {
        Calendar m_cal = Calendar.getInstance();
        SimpleDateFormat formatter = new SimpleDateFormat("HH:mm:ss");
        String intime = formatter.format(m_cal.getTime());
        return intime;
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
                        if (new File((str + _pathforcheck).trim()).exists()) {
                            String metadata = CommonFunctions.setMetadataAtImages(storename, store_id, "STORE IMAGE", username);
                            CommonFunctions.addMetadataAndTimeStampToImage(context, _path, metadata, date);
                            //Set Clicked image to Imageview
                            img.setBackgroundResource(R.drawable.camera_tick_ico);
                            img1 = _pathforcheck;
                            image1 = img1;
                            _pathforcheck = "";
                            break;
                        }
                    }
                } catch (Exception e) {
                    Crashlytics.logException(e);
                    e.printStackTrace();
                }
                break;
        }
    }

    @Override
    public void onConnected(Bundle bundle) {
        Location mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        if (mLastLocation != null) {
            lat = String.valueOf(mLastLocation.getLatitude());
            longi = String.valueOf(mLastLocation.getLongitude());
        }
    }

    @Override
    public void onConnectionSuspended(int i) {
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
    }

    protected void onStart() {
        mGoogleApiClient.connect();
        super.onStart();
    }


}
