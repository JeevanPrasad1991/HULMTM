package com.cpm.geotagging;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.cpm.Constants.CommonString;
import com.cpm.DailyEntry.AfterTOT;
import com.cpm.database.GSKMTDatabase;
import com.cpm.delegates.GeotaggingBeans;
import com.cpm.message.AlertMessage;
import com.cpm.upload.Base64;
import com.cpm.xmlGetterSetter.FailureGetterSetter;
import com.cpm.xmlHandler.FailureXMLHandler;
import com.example.gsk_mtt.R;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;
import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.StringReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

public class GeoTagActivity extends AppCompatActivity implements
        OnMapReadyCallback,GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, LocationListener {
    private GoogleMap mMap;
    double latitude =0.0;
    double longitude =0.0;
    LocationManager locationManager;
    Geocoder geocoder;
    // LogCat tag
    private static final String TAG = GeoTagActivity.class.getSimpleName();
    private Dialog dialog;
    private final static int PLAY_SERVICES_RESOLUTION_REQUEST = 1000;
    String storeid,str,status;
    private Location mLastLocation;
    private LocationManager locmanager = null;
    // Google client to interact with Google API
    private GoogleApiClient mGoogleApiClient;
    boolean enabled;
    // boolean flag to toggle periodic location updates
    private boolean mRequestingLocationUpdates = false;
    private LocationRequest mLocationRequest;
    private TextView percentage, message;
    // Location updates intervals in sec
    private static int UPDATE_INTERVAL = 500; // 5 sec
    private static int FATEST_INTERVAL = 100; // 1 sec
    private static int DISPLACEMENT = 5; // 10 meters
    String  username;
    private FailureGetterSetter failureGetterSetter = null;
    SupportMapFragment mapFragment;
    GSKMTDatabase db;
    FloatingActionButton fab,fabcarmabtn;
    File file;
    SharedPreferences preferences;
    ArrayList<GeotaggingBeans> geotaglist = new ArrayList<GeotaggingBeans>();
    private ProgressBar pb;
    private int factor, k;
    String errormsg = "";
    String result;
    protected String diskpath = "",_path,_pathforcheck,img_str="";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_geo_tag);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        preferences = PreferenceManager.getDefaultSharedPreferences(this);
        username = preferences.getString(CommonString.KEY_USERNAME, null);
         mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        db = new GSKMTDatabase(GeoTagActivity.this);
        db.open();
        fab = (FloatingActionButton) findViewById(R.id.fab);
        fabcarmabtn = (FloatingActionButton) findViewById(R.id.camrabtn);
        storeid = getIntent().getStringExtra("Storeid");
        str = CommonString.FILE_PATH;
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!img_str.equals("")){
                    status="Y";
                    db.updateStatus(storeid,status, latitude, longitude);
                    db.InsertSTOREgeotag(storeid, latitude, longitude, img_str,status);
                    img_str="";
                    new GeoTagUpload(GeoTagActivity.this).execute();
                }
                else {
                    Snackbar.make(view, "Please Take Image Before Save", Snackbar.LENGTH_LONG).setAction("Action", null).show();

                }

            }
        });

        fabcarmabtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                _pathforcheck = storeid + "Store" + "Image" +  getCurrentTime().replace(":","")+".jpg";
                _path = CommonString.FILE_PATH + _pathforcheck;
                startCameraActivity();



            }
        });


        locationManager = (LocationManager) this
                .getSystemService(LOCATION_SERVICE);
        geocoder = new Geocoder(this);
        Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);


        if (checkPlayServices()) {
            // Building the GoogleApi client
            buildGoogleApiClient();
        }

        if (checkPlayServices()) {

            // Building the GoogleApi client
            buildGoogleApiClient();

            createLocationRequest();
        }


        locmanager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        enabled = locmanager.isProviderEnabled(LocationManager.GPS_PROVIDER);


        if (!enabled) {

            AlertDialog.Builder alertDialog = new AlertDialog.Builder(
                    GeoTagActivity.this);

            // Setting Dialog Title
            alertDialog.setTitle("GPS IS DISABLED...");

            // Setting Dialog Message
            alertDialog.setMessage("Click ok to enable GPS.");

            // Setting Positive "Yes" Button
            alertDialog.setPositiveButton("YES",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {

                            Intent intent = new Intent(
                                    Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                            startActivity(intent);
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

    @Override
    public void onConnected(Bundle bundle) {

        mLastLocation = LocationServices.FusedLocationApi
                .getLastLocation(mGoogleApiClient);

        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            if (mLastLocation != null) {
                latitude = mLastLocation.getLatitude();
                longitude = mLastLocation.getLongitude();

                mMap.setMyLocationEnabled(true);

                // Add a marker of latest location and move the camera
                LatLng latLng = new LatLng(latitude, longitude);
                mMap.addMarker(new MarkerOptions().position(latLng));
                mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
                mMap.animateCamera(CameraUpdateFactory.zoomTo(15));
            }
        }


        // if (mRequestingLocationUpdates) {
        startLocationUpdates();
        // }

        // startLocationUpdates();
    }

    @Override
    public void onConnectionSuspended(int i) {
        mGoogleApiClient.connect();
    }

    @Override
    public void onLocationChanged (Location location){

    }

    @Override
    public void onConnectionFailed (ConnectionResult connectionResult) {
        Log.i(TAG, "Connection failed: ConnectionResult.getErrorCode() = "
                + connectionResult.getErrorCode());
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
    }

    protected void onStart() {
        super.onStart();
        if (mGoogleApiClient != null) {
            mGoogleApiClient.connect();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

       /* checkPlayServices();

        // Resuming the periodic location updates
        if (mGoogleApiClient.isConnected() && mRequestingLocationUpdates) {
            startLocationUpdates();
        }*/
    }


    @Override
    protected void onPause() {
        super.onPause();
        stopLocationUpdates();
    }


    /**
     * Creating google api client object
     * */
    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API).build();
    }

    /**
     * Creating location request object
     * */
    protected void createLocationRequest() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(UPDATE_INTERVAL);
        mLocationRequest.setFastestInterval(FATEST_INTERVAL);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setSmallestDisplacement(DISPLACEMENT);
    }

    /**
     * Method to verify google play services on the device
     * */
    private boolean checkPlayServices() {
        int resultCode = GooglePlayServicesUtil
                .isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
                GooglePlayServicesUtil.getErrorDialog(resultCode, this,
                        PLAY_SERVICES_RESOLUTION_REQUEST).show();
            } else {
                Toast.makeText(getApplicationContext(), "This device is not supported.", Toast.LENGTH_LONG)
                        .show();
                finish();
            }
            return false;
        }
        return true;
    }

    /**
     * Starting the location updates
     * */
    protected void startLocationUpdates() {


        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);

        }

    }

    /**
     * Stopping location updates
     */
    protected void stopLocationUpdates() {
        LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
    }


    protected void startCameraActivity() {
        Log.i("MakeMachine", "startCameraActivity()");
        file = new File(_path);
        Uri outputFileUri = Uri.fromFile(file);

        Intent intent = new Intent(
                MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, outputFileUri);

        startActivityForResult(intent, 0);

    }


    public String getCurrentTime() {

        Calendar m_cal = Calendar.getInstance();

        SimpleDateFormat formatter = new SimpleDateFormat("HH:mm:ss:mmm");
        String cdate = formatter.format(m_cal.getTime());

       /* String intime = m_cal.get(Calendar.HOUR_OF_DAY) + ":"
                + m_cal.get(Calendar.MINUTE) + ":" + m_cal.get(Calendar.SECOND);*/

        return cdate;

    }


    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        Log.i("MakeMachine", "resultCode: " + resultCode);
        switch (resultCode) {
            case 0:
                Log.i("MakeMachine", "User cancelled");
                break;

            case -1:

                if (_pathforcheck != null && !_pathforcheck.equals("")) {
                    if (new File(str + _pathforcheck).exists()) {

                      //  fabcarmabtn.setBackgroundResource(R.drawable.camera_icon_done);

                        fabcarmabtn.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.camera_icon_done));

                       // fabcarmabtn.setBackgroundColor(Color.parseColor("#FF0066"));

                        fabcarmabtn.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#FF0066")));

                        img_str = _pathforcheck;
                        _pathforcheck = "";

                    }
                }

                break;
        }


        super.onActivityResult(requestCode, resultCode, data);
    }



    public class GeoTagUpload extends AsyncTask<Void, Void, String> {

        private Context context;

        GeoTagUpload(Context context) {
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
        }

        @Override
        protected String doInBackground(Void... params) {
            try {

                GSKMTDatabase db = new GSKMTDatabase(GeoTagActivity.this);
                db.open();

                geotaglist = db.getinsertGeotaggingData("Y");

                // uploading Geotag

                SAXParserFactory saxPF = SAXParserFactory.newInstance();
                SAXParser saxP = saxPF.newSAXParser();
                XMLReader xmlR = saxP.getXMLReader();


                String geo_xml = "";
                ArrayList<String> geotemplist = new ArrayList<String>();
                if (geotaglist.size()>0) {

                    for(int i = 0 ; i<geotaglist.size();i++){


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
                                /*+ "[IMAGE_URL2]" + geotaglist.get(i).getUrl2()
                                + "[/IMAGE_URL2][IMAGE_URL3]"
                                + geotaglist.get(i).getUrl3()
                                + "[/IMAGE_URL3]" */

                                + "[CREATED_BY]" + username
                                + "[/CREATED_BY][/GeoTag_DATA]";

                        geo_xml = geo_xml + onXML;

                        geotemplist.add(geotaglist.get(i).getStoreid());

                    }

                    geo_xml = "[DATA]" + geo_xml
                            + "[/DATA]";

                    SoapObject request = new SoapObject(CommonString.NAMESPACE,
                            CommonString.METHOD_UPLOAD_STOCK_XML_DATA);
                    request.addProperty("MID", "0");
                    request.addProperty("KEYS", "GEOTAG_NEW_DATA");
                    request.addProperty("USERNAME", username );

                    request.addProperty("XMLDATA", geo_xml);

                    SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
                            SoapEnvelope.VER11);
                    envelope.dotNet = true;
                    envelope.setOutputSoapObject(request);

                    HttpTransportSE androidHttpTransport = new HttpTransportSE(
                            CommonString.URL);

                    androidHttpTransport.call(
                            CommonString.SOAP_ACTION_UPLOAD_ASSET_XMLDATA, envelope);
                    Object result = (Object) envelope.getResponse();

                    if (result.toString().equalsIgnoreCase(
                            CommonString.KEY_SUCCESS)) {
                    String statusD="D";

                        for(int i =0 ; i < geotemplist.size(); i++){
                            db.updateGeoTagData(geotemplist.get(i).toString(),statusD);

                            db.updateDataStatus(geotemplist.get(i).toString(),statusD);



                        }

                    } else {

                        if (result.toString().equalsIgnoreCase(
                                CommonString.KEY_FALSE)) {
                            return CommonString.METHOD_UPLOAD_ASSET;
                        }

                        // for failure
                        FailureXMLHandler failureXMLHandler = new FailureXMLHandler();
                        xmlR.setContentHandler(failureXMLHandler);

                        InputSource is = new InputSource();
                        is.setCharacterStream(new StringReader(result
                                .toString()));
                        xmlR.parse(is);

                        failureGetterSetter = failureXMLHandler
                                .getFailureGetterSetter();

                        if (failureGetterSetter.getStatus().equalsIgnoreCase(
                                CommonString.KEY_FAILURE)) {
                            return CommonString.METHOD_UPLOAD_ASSET + ","
                                    + failureGetterSetter.getErrorMsg();

                        } else {

                        }
                    }
                }


                return CommonString.KEY_SUCCESS;

            }

            catch (Exception ex) {
                ex.printStackTrace();
            }
            return "";

        }


        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            dialog.dismiss();

            if (result.equals(CommonString.KEY_SUCCESS)) {
                new GeoTagActivity.GeoTagImageUpload(GeoTagActivity.this).execute();
            } else if (!result.equals("")) {
                AlertMessage message = new AlertMessage(
                        GeoTagActivity.this, AlertMessage.MESSAGE_ERROR
                        + result, "success", null);
                message.showMessage();
            }

        }

    }



    public class GeoTagImageUpload extends AsyncTask<Void, Void, String>{

        private Context context;

        GeoTagImageUpload(Context context) {
            this.context = context;
        }

        @Override
        protected void onPreExecute() {

            super.onPreExecute();

            dialog = new Dialog(context);
            dialog.setContentView(R.layout.custom);
            dialog.setTitle("Uploading Geotag Images");
            dialog.setCancelable(false);
            dialog.show();
            pb = (ProgressBar) dialog.findViewById(R.id.progressBar1);
            percentage = (TextView) dialog.findViewById(R.id.percentage);
            message = (TextView) dialog.findViewById(R.id.message);
        }




        @Override
        protected String doInBackground(Void... params) {
            try {

                GSKMTDatabase db = new GSKMTDatabase(GeoTagActivity.this);
                db.open();


                geotaglist = db.getinsertGeotaggingData("D");

                // Uploading Geotag

                SAXParserFactory saxPF = SAXParserFactory.newInstance();
                SAXParser saxP = saxPF.newSAXParser();
                XMLReader xmlR = saxP.getXMLReader();




                if (geotaglist.size()>0) {

                    for(int i = 0 ; i<geotaglist.size();i++){

                        runOnUiThread(new Runnable() {

                            public void run() {
                                // TODO Auto-generated method stub
                                k = k + factor;
                                pb.setProgress(k);
                                percentage.setText(k + "%");
                                message.setText("Uploading Geotag Images...");
                            }
                        });

                        if (geotaglist.get(i).getUrl1() != null
                                && !geotaglist.get(i).getUrl1()
                                .equalsIgnoreCase("")) {

                            if (new File(Environment.getExternalStorageDirectory()+"/MT_GSK_Images/"
                                    + geotaglist.get(i).getUrl1()).exists()) {
                                result = UploadGeoImage(geotaglist.get(i).getUrl1(), "GeoTag");


                                if (!result.toString().equalsIgnoreCase(CommonString.KEY_SUCCESS)) {

                                    return "GeoTag";
                                }


                                if (result.toString().equalsIgnoreCase(CommonString.KEY_FALSE)) {

                                    return CommonString.METHOD_Get_DR_STORE_IMAGES_GEO;
                                }
                                else if (result
                                        .equalsIgnoreCase(CommonString.KEY_FAILURE)) {

                                    return CommonString.METHOD_Get_DR_STORE_IMAGES_GEO
                                            + "," + errormsg;
                                }



                               /* runOnUiThread(new Runnable() {

                                    public void run() {
                                        // TODO Auto-generated method stub

                                        message.setText("Image1 Upload");
                                    }
                                });*/
                            }
                        }



//						for(int i =0 ; i < geotaglist.size(); i++){

//						}



                    }


                }


                return CommonString.KEY_SUCCESS;

            }

            catch (Exception ex) {
                ex.printStackTrace();
            }

            return "";

        }


        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            dialog.dismiss();

            if (result.equalsIgnoreCase(CommonString.KEY_SUCCESS)) {
               /* AlertMessage message = new AlertMessage(
                        GeoTagActivity.this,
                        AlertMessage.MESSAGE_UPLOAD_IMAGE, "success", null);
                message.showMessage();*/

                Toast.makeText(getApplicationContext(),"GeoTag Uploaded ",Toast.LENGTH_LONG).show();

                String Statustag="U";
                db.open();

                for(int i =0 ; i < geotaglist.size(); i++){


                    db.updateGeoTagData(geotaglist.get(i).getStoreid(),Statustag);

                    db.updateDataStatus(geotaglist.get(i).getStoreid(),Statustag);

                    // db.updateGeoTagDataInMain(geotaglist.get(i).getStoreid());

                    db.deleteGeoTagData(geotaglist.get(i).getStoreid());


                }



                Intent intent = new Intent(
                        GeoTagActivity.this,
                        GeoTagging.class);

                startActivity(intent);

                GeoTagActivity.this.finish();

            }

            else if (!result.equals("")) {
                AlertMessage message = new AlertMessage(
                        GeoTagActivity.this, AlertMessage.MESSAGE_ERROR
                        + result, "success", null);
                message.showMessage();
            }

        }

    }

    public String UploadGeoImage(String path, String folder) throws Exception {

        errormsg = "";
        BitmapFactory.Options o = new BitmapFactory.Options();
        o.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(Environment.getExternalStorageDirectory()+"/MT_GSK_Images/" + path, o);

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
        Bitmap bitmap = BitmapFactory.decodeFile(Environment.getExternalStorageDirectory()+"/MT_GSK_Images/" + path, o2);

        ByteArrayOutputStream bao = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 90, bao);
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

            new File(Environment.getExternalStorageDirectory()+"/MT_GSK_Images/" + path).delete();



            /*SAXParserFactory saxPF = SAXParserFactory.newInstance();
            SAXParser saxP = saxPF.newSAXParser();
            XMLReader xmlR = saxP.getXMLReader();

            // for failure
            FailureXMLHandler failureXMLHandler = new FailureXMLHandler();
            xmlR.setContentHandler(failureXMLHandler);

            InputSource is = new InputSource();
            is.setCharacterStream(new StringReader(result.toString()));
            xmlR.parse(is);

            failureGetterSetter = failureXMLHandler
                    .getFailureGetterSetter();

            if (failureGetterSetter.getStatus().equalsIgnoreCase(
                    CommonString.KEY_FAILURE)) {
                errormsg = failureGetterSetter.getErrorMsg();
                return CommonString.KEY_FAILURE;
            }*/

        }

        else if (result.toString().equalsIgnoreCase(CommonString.KEY_FALSE)) {
            return CommonString.KEY_FALSE;
        }
        else {
            return CommonString.KEY_FAILURE;
        }

        return result.toString() ;
    }



}
