
package com.cpm.geotagging;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;
import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Point;
import android.graphics.drawable.BitmapDrawable;
import android.hardware.Camera;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.cpm.Constants.CommonString;


import com.cpm.database.GSKMTDatabase;
import com.cpm.delegates.GeotaggingBeans;
import com.cpm.delegates.SkuBean;
import com.cpm.message.AlertMessage;
import com.cpm.upload.Base64;
import com.cpm.upload.ImageUploadActivity;
import com.cpm.upload.UploadDataActivity;
import com.cpm.xmlGetterSetter.FailureGetterSetter;
import com.cpm.xmlHandler.FailureXMLHandler;
import com.example.gsk_mtt.R;


import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;

import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

public class LocationActivity extends FragmentActivity  implements OnMapReadyCallback,GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, com.google.android.gms.location.LocationListener {

    private static final String TAG = "LocationActivity";
    protected static final String PHOTO_TAKEN = "photo_taken";
    LocationManager locationManager;
    Geocoder geocoder;
    TextView locationText;
    protected Button _button;
    protected FloatingActionButton _buttonsave;
    public Camera camera;
    private Dialog dialog;
    String result;
    private ProgressBar pb;
    private TextView percentage, message;
    protected ImageView _image;
    protected TextView _field;
    Button imagebtn;

    protected boolean _taken;
    ImageView capture_1;
    ImageView capture_2;
    ImageView capture_3;
    public String text;
    public View view;
    Location location;
    GeotaggingBeans data = new GeotaggingBeans();
    private int factor, k;

    private FailureGetterSetter failureGetterSetter = null;
    protected String diskpath = "";
    protected String _path;
    protected String _path1;
    protected String _path2;

    protected String _pathforcheck = "";
    protected String _path1forcheck = "";
    protected String _path2forcheck = "";

    String storename;
    String storeid, username;

    SharedPreferences preferences;

    String storeaddress = "";
    String storelatitude = "";
    String storelongitude = "";
    protected int resultCode;
    public BitmapDrawable bitmapDrawable;
    int abc;
    int abd;
    int abe;
    TextView Stroename;
    static Editor e1, e2, e3;
    double lat;

    private Bitmap mBubbleIcon, mShadowIcon;
    ProgressBar progress;
    String errormsg = "";
    private Timer timer;
    ArrayList<GeotaggingBeans> geotaglist = new ArrayList<GeotaggingBeans>();
    private final static int PLAY_SERVICES_RESOLUTION_REQUEST = 1000;
    private static int UPDATE_INTERVAL = 1000; // 10 sec
    private static int FATEST_INTERVAL = 500; // 5 sec
    private static int DISPLACEMENT = 10; // 10 meters
    Location mLastLocation;
    private GoogleApiClient mGoogleApiClient;
    LocationRequest mLocationRequest;
    Marker currLocationMarker;
    LatLng latLng;
    private GoogleMap  mMap;
    double latitude =0.0;
    double longitude =0.0;

    // boolean flag to toggle periodic location updates
    private boolean mRequestingLocationUpdates = false;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.gps_location);
        _buttonsave = (FloatingActionButton) findViewById(R.id.geotag_sumbitbtn);
        capture_1 = (ImageView) findViewById(R.id.geotag_frontcamera);
        capture_2 = (ImageView) findViewById(R.id.geotag_insidecamera1);
        capture_3 = (ImageView) findViewById(R.id.geotag_insidecamera2);
        Stroename = (TextView) findViewById(R.id.geotag_store_orginalname);
       // progress = (ProgressBar) findViewById(R.id.progressBar1);

       /* map.setBuiltInZoomControls(true);
        map.setSatellite(true);*/

        Intent intent = getIntent();
        storename = intent.getStringExtra("StoreName");
        storeid = intent.getStringExtra("Storeid");
        storeaddress = intent.getStringExtra("Storeaddress");
        storelatitude = intent.getStringExtra("storelatitude");
        storelongitude = intent.getStringExtra("storelongitude");
        Stroename.setText(storename);

       /* mapController = map.getController();
        mapController.setZoom(15);*/
        preferences = PreferenceManager.getDefaultSharedPreferences(this);
        username = preferences.getString(CommonString.KEY_USERNAME, null);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.mapview);
        mapFragment.getMapAsync(this);

      /*  locationManager = (LocationManager) this.getSystemService(LOCATION_SERVICE);
        geocoder = new Geocoder(this);
        Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
*/

      /*  if (checkPlayServices()) {

            // Building the GoogleApi client
            buildGoogleApiClient();
        }

*/

        if (checkPlayServices()) {

            // Building the GoogleApi client
            buildGoogleApiClient();

            createLocationRequest();
        }

        /*_buttonsave.setOnClickListener(new OnClickListener() {

            public void onClick(View v) {
                // TODO Auto-generated method stub

                if (!(storelatitude.equals("0")) && !(storelongitude.equals("0"))) {
                    lat = Double.parseDouble(storelatitude);
                    longitude = Double.parseDouble(storelongitude);

                } else {
                    lat = data.getLatitude();
                    longitude = data.getLongitude();

                }
                if (ImageUploadActivity.CheckGeotagImage(_pathforcheck) && ImageUploadActivity.CheckGeotagImage(_path1forcheck)
                        && ImageUploadActivity.CheckGeotagImage(_path2forcheck)) {

                    String status = "Y";
                    GSKMTDatabase data = new GSKMTDatabase(getApplicationContext());
                    data.open();
                    data.updateGeoTagStatus(storeid, status, lat, longitude);

                    data.InsertStoregeotagging(storeid, lat, longitude, _pathforcheck, _path1forcheck, _path2forcheck, status);
                    data.close();

                    // display toast
                    AlertDialog.Builder builder = new AlertDialog.Builder(v
                            .getContext());

                    builder.setMessage("Data is saved successfully.")
                            .setCancelable(false)
                            .setPositiveButton("OK",
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(
                                                DialogInterface dialog, int id) {

                                            new GeoTagUpload(LocationActivity.this).execute();
	                                                Intent intent = new Intent(LocationActivity.this, GeoTagging.class);
											        startActivity(intent);
											LocationActivity.this.finish();


										}
									});
					AlertDialog alert = builder.create();
					alert.show();

				} else if (!(data.getLatitude() == 0) && !(data.getLongitude() == 0)) {

					AlertDialog.Builder builder = new AlertDialog.Builder(LocationActivity.this);
					builder.setMessage(
							"Do you want to save geo location without store images.")
							.setCancelable(false)
							.setPositiveButton("OK",
									new DialogInterface.OnClickListener() {
										public void onClick(
												DialogInterface dialog, int id) {

											SavePartialData(lat, longitude);

										}
									})
							.setNegativeButton("Cancel",
									new DialogInterface.OnClickListener() {
										public void onClick(
												DialogInterface dialog, int id) {
											dialog.cancel();
										}
									});
					AlertDialog alert = builder.create();

					alert.show();

				} else if (!(storelatitude.equals("0"))
						&& !(storelongitude.equals("0"))) {

					if (ImageUploadActivity.CheckGeotagImage(_pathforcheck)
							&& ImageUploadActivity
									.CheckGeotagImage(_path1forcheck)
							&& ImageUploadActivity
									.CheckGeotagImage(_path2forcheck)) {

						String status = "Y";
						GSKMTDatabase data = new GSKMTDatabase(
								getApplicationContext());
						data.open();
						data.updateGeoTagStatus(storeid, status, lat, longitude);
						data.InsertStoregeotagging(storeid, lat, longitude,
								_pathforcheck, _path1forcheck, _path2forcheck,
								status);
						data.close();

						AlertDialog.Builder builder = new AlertDialog.Builder(v
								.getContext());

						builder.setMessage("Data is saved sucessfully.")
								.setCancelable(false)
								.setPositiveButton("OK",
										new DialogInterface.OnClickListener() {
											public void onClick(
													DialogInterface dialog,
													int id) {

												new GeoTagUpload(LocationActivity.this).execute();

                                                        Intent intent = new Intent(LocationActivity.this, GeoTagging.class);
												         startActivity(intent);

												LocationActivity.this.finish();


											}
										});
						AlertDialog alert = builder.create();
						alert.show();

					} else {

						Toast.makeText(getBaseContext(),
								"Please Capture all store images",
								Toast.LENGTH_LONG).show();

					}
				} else {

					AlertDialog.Builder builder = new AlertDialog.Builder(v
							.getContext());

					builder.setMessage("Wait for geo location")
							.setCancelable(false)
							.setPositiveButton("OK",
									new DialogInterface.OnClickListener() {
										public void onClick(
												DialogInterface dialog, int id) {
											dialog.cancel();
										}
									});
					AlertDialog alert = builder.create();
					alert.show();

				}

			}
		});*/

        capture_1.setOnClickListener(new ButtonClickHandler());
        capture_2.setOnClickListener(new ButtonClickHandler());
        capture_3.setOnClickListener(new ButtonClickHandler());

    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);

        if (ContextCompat.checkSelfPermission(this,
                android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) ==
                PackageManager.PERMISSION_GRANTED) {
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

/*
        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);

        if (mLastLocation != null) {
            //place marker at current position
            //mGoogleMap.clear();
            latLng = new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude());
            MarkerOptions markerOptions = new MarkerOptions();
            markerOptions.position(latLng);

            markerOptions.title("Current Position");
            markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_MAGENTA));
            currLocationMarker = mMap.addMarker(markerOptions);
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15));

        }

        LocationRequest mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(5000); //5 seconds
        mLocationRequest.setFastestInterval(3000); //3 seconds
        mLocationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
        //mLocationRequest.setSmallestDisplacement(0.1F); //1/10 meter

        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);


        try {


            int latiti;
            int longi;
            if (!(storelatitude.equals("0")) && !(storelongitude.equals("0"))) {
                latiti = (int) (Double.parseDouble(storelatitude) * 1000000);
                longi = (int) (Double.parseDouble(storelongitude) * 1000000);

            }

            else {
                data.setLatitude((mLastLocation.getLatitude()));
                data.setLongitude((mLastLocation.getLongitude()));

                latiti = (int) (mLastLocation.getLatitude() * 1000000);
                longi = (int) (mLastLocation.getLongitude() * 1000000);





            }



        }
        catch (Exception e) {
            Log.e("LocateMe", "Could not get Geocoder data", e);
        }
*/


    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        mGoogleApiClient = new GoogleApiClient.Builder(this).addConnectionCallbacks(this).addOnConnectionFailedListener(this).addApi(LocationServices.API).build();


    }
    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this).
                addConnectionCallbacks(this).
                addOnConnectionFailedListener(this).
                addApi(LocationServices.API).build();
    }

    @Override
    public void onLocationChanged(Location location) {
        mLastLocation = location;
    }
    @Override
    protected void onStop() {
        super.onStop();
        if (mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        stopLocationUpdates();
    }
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;



        mMap.setMyLocationEnabled(true);
        mMap.getUiSettings().setCompassEnabled(true);
        mMap.getUiSettings().setZoomControlsEnabled(true);
        mMap.getMaxZoomLevel();
        mMap.getMinZoomLevel();
        mMap.getUiSettings();
        mMap.animateCamera(CameraUpdateFactory.zoomIn());
        mMap.animateCamera(CameraUpdateFactory.zoomOut());
        mMap.animateCamera(CameraUpdateFactory.zoomTo(20));

    }

    private boolean checkPlayServices() {
        int resultCode = GooglePlayServicesUtil
                .isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
                GooglePlayServicesUtil.getErrorDialog(resultCode, this,
                        PLAY_SERVICES_RESOLUTION_REQUEST).show();
            } else {
                Toast.makeText(getApplicationContext(),
                        "This device is not supported.", Toast.LENGTH_LONG)
                        .show();
                finish();
            }
            return false;
        }
        return true;
    }
    protected void createLocationRequest() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(UPDATE_INTERVAL);
        mLocationRequest.setFastestInterval(FATEST_INTERVAL);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setSmallestDisplacement(DISPLACEMENT);
    }

    @Override
    protected void onResume() {
        super.onResume();

        checkPlayServices();

        // Resuming the periodic location updates
        if (mGoogleApiClient.isConnected() && mRequestingLocationUpdates) {
            startLocationUpdates();
        }
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
        File file = new File(diskpath);
        Uri outputFileUri = Uri.fromFile(file);

        Intent intent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, outputFileUri);
        startActivityForResult(intent, 0);

    }

    public boolean isNetworkOnline() {
        boolean status = false;
        try {
            ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo netInfo = cm.getNetworkInfo(0);
            if (netInfo != null
                    && netInfo.getState() == NetworkInfo.State.CONNECTED) {
                status = false;
            } else {
                netInfo = cm.getNetworkInfo(1);
                if (netInfo != null
                        && netInfo.getState() == NetworkInfo.State.CONNECTED)
                    status = false;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
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
                onPhotoTaken();

                if (ImageUploadActivity.CheckGeotagImage(_pathforcheck)) {

                    capture_1.setImageResource(R.drawable.camera_done);

                }
                if (ImageUploadActivity.CheckGeotagImage(_path1forcheck)) {

                    capture_2.setImageResource(R.drawable.camera_done);

                }
                if (ImageUploadActivity.CheckGeotagImage(_path2forcheck)) {

                    capture_3.setImageResource(R.drawable.camera_done);

                }

                break;
        }
    }

    protected void onPhotoTaken() {

        Log.i("MakeMachine", "onPhotoTaken");
        _taken = true;

        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inSampleSize = 8;
        Bitmap bitmap = BitmapFactory.decodeFile(diskpath, options);

    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        Log.i("MakeMachine", "onRestoreInstanceState()");
        if (savedInstanceState.getBoolean(this.PHOTO_TAKEN)) {
            onPhotoTaken();
            if (ImageUploadActivity.CheckGeotagImage(_pathforcheck)) {

                capture_1.setImageResource(R.drawable.camera_done);

            }
            if (ImageUploadActivity.CheckGeotagImage(_path1forcheck)) {

                capture_2.setImageResource(R.drawable.camera_done);

            }
            if (ImageUploadActivity.CheckGeotagImage(_path2forcheck)) {

                capture_3.setImageResource(R.drawable.camera_done);

            }

        }

        if (ImageUploadActivity.CheckGeotagImage(_pathforcheck)) {

            capture_1.setImageResource(R.drawable.camera_done);

        }
        if (ImageUploadActivity.CheckGeotagImage(_path1forcheck)) {

            capture_2.setImageResource(R.drawable.camera_done);

        }
        if (ImageUploadActivity.CheckGeotagImage(_path2forcheck)) {

            capture_3.setImageResource(R.drawable.camera_done);

        }

    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putBoolean(this.PHOTO_TAKEN, _taken);
    }

    public class ButtonClickHandler implements View.OnClickListener {
        LocationActivity loc = new LocationActivity();

        public void onClick(View view) {

            if (!(storelatitude.equals("0")) && !(storelongitude.equals("0"))) {

                if (view.getId() == R.id.geotag_frontcamera) {

                    diskpath = Environment.getExternalStorageDirectory()
                            + "/MT_GSK_Images/" + storeid + "front.jpg";
                    _path = storeid + "front.jpg";

                    Log.i("MakeMachine", "ButtonClickHandler.onClick()");

                    abc = 03;

                    startCameraActivity();

                }
                if (view.getId() == R.id.geotag_insidecamera1) {
                    Log.i("MakeMachine", "ButtonClickHandler.onClick()");
                    diskpath = Environment.getExternalStorageDirectory()
                            + "/MT_GSK_Images/" + storeid + "inside.jpg";
                    _path1 = storeid + "inside.jpg";

                    abd = 01;

                    startCameraActivity();

                }
                if (view.getId() == R.id.geotag_insidecamera2) {

                    Log.i("MakeMachine", "ButtonClickHandler.onClick()");
                    diskpath = Environment.getExternalStorageDirectory()
                            + "/MT_GSK_Images/" + storeid + "inside1.jpg";
                    _path2 = storeid + "inside1.jpg";

                    abe = 02;

                    startCameraActivity();

                }

            } else if (data.getLatitude() == 0 && data.getLongitude() == 0) {
                AlertDialog.Builder builder = new AlertDialog.Builder(
                        view.getContext());

                builder.setMessage("Wait For Geo Location")
                        .setCancelable(false)
                        .setPositiveButton("OK",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog,
                                                        int id) {
                                        dialog.cancel();
                                    }
                                });
                AlertDialog alert = builder.create();
                alert.show();

            }

            else {
                if (view.getId() == R.id.geotag_frontcamera) {

                    diskpath = Environment.getExternalStorageDirectory()
                            + "/MT_GSK_Images/" + storeid + "front.jpg";
                    _path = storeid + "front.jpg";

                    Log.i("MakeMachine", "ButtonClickHandler.onClick()");

                    abc = 03;

                    startCameraActivity();

                }
                if (view.getId() == R.id.geotag_insidecamera1) {
                    Log.i("MakeMachine", "ButtonClickHandler.onClick()");
                    diskpath = Environment.getExternalStorageDirectory()
                            + "/MT_GSK_Images/" + storeid + "inside.jpg";
                    _path1 = storeid + "inside.jpg";

                    abd = 01;

                    startCameraActivity();

                }
                if (view.getId() == R.id.geotag_insidecamera2) {

                    Log.i("MakeMachine", "ButtonClickHandler.onClick()");
                    diskpath = Environment.getExternalStorageDirectory()
                            + "/MT_GSK_Images/" + storeid + "inside1.jpg";
                    _path2 = storeid + "inside1.jpg";

                    abe = 02;

                    startCameraActivity();

                }

            }

        }
    }
    public void SavePartialData(Double lat, Double longitude) {

        String status = "P";
        GSKMTDatabase data = new GSKMTDatabase(getApplicationContext());
        data.open();
        data.updateGeoTagStatus(storeid, status, lat, longitude);
        data.InsertStoregeotagging(storeid, lat, longitude, _pathforcheck,
                _path1forcheck, _path2forcheck, status);
        data.close();
        Intent intent = new Intent(LocationActivity.this, GeoTagging.class);

        startActivity(intent);

        LocationActivity.this.finish();

    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        // Handle the back button
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            // Ask the user if they want to quit

            Intent intent = new Intent(LocationActivity.this, GeoTagging.class);

            startActivity(intent);
            LocationActivity.this.finish();

            return true;
        }

        return super.onKeyDown(keyCode, event);
    }

    public void ShowToast(String message) {

        Toast.makeText(getBaseContext(), message, Toast.LENGTH_LONG).show();
    }

    class GeoData {
        int value;
        String name;
    }

    public class GeoTagUpload extends AsyncTask<Void, Void, String>{

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
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            dialog.dismiss();

            if (result.equals(CommonString.KEY_SUCCESS)) {
                AlertMessage message = new AlertMessage(
                        LocationActivity.this,
                        AlertMessage.MESSAGE_UPLOAD_DATA, "success", null);
                message.showMessage();

                new GeoTagImageUpload(LocationActivity.this).execute();


Intent intent = new Intent(
						LocationActivity.this,
						GeoTagging.class);

				startActivity(intent);

				LocationActivity.this.finish()
;

			} else if (!result.equals("")) {
				AlertMessage message = new AlertMessage(
						LocationActivity.this, AlertMessage.MESSAGE_ERROR
								+ result, "success", null);
				message.showMessage();
			}

		}



		@Override
		protected String doInBackground(Void... params) {
			try {

				GSKMTDatabase db = new GSKMTDatabase(LocationActivity.this);
				db.open();


				geotaglist = db.getGeotaggingData("Y");

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



						String onXML = "[USER_DATA][STORE_ID]"
								+ Integer.parseInt(geotaglist.get(i).getStoreid())
								+ "[/STORE_ID][LATTITUDE]"
								+ Double.toString(geotaglist.get(i).getLatitude())
								+ "[/LATTITUDE][LONGITUDE]"
								+ Double.toString(geotaglist.get(i).getLongitude())
								+ "[/LONGITUDE][IMAGE_URL1]"
								+ geotaglist.get(i).getUrl1() + "[/IMAGE_URL1]"
								+ "[IMAGE_URL2]" + geotaglist.get(i).getUrl2()
								+ "[/IMAGE_URL2][IMAGE_URL3]"
								+ geotaglist.get(i).getUrl3()
								+ "[/IMAGE_URL3][CREATED_BY]" + username
								+ "[/CREATED_BY][/USER_DATA]";

						geo_xml = geo_xml + onXML;

						geotemplist.add(geotaglist.get(i).getStoreid());

					}

					geo_xml = "[DATA]" + geo_xml
							+ "[/DATA]";



						SoapObject request = new SoapObject(CommonString.NAMESPACE,
								CommonString.METHOD_UPLOAD_STOCK_XML_DATA);
						request.addProperty("MID", "0");
						request.addProperty("KEYS", "GEOTAG_DATA");
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


							for(int i =0 ; i < geotemplist.size(); i++){
								//db.updateGeoTagData(geotemplist.get(i).toString());
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
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            dialog.dismiss();

            if (result.equals(CommonString.KEY_SUCCESS)) {
                AlertMessage message = new AlertMessage(
                        LocationActivity.this,
                        AlertMessage.MESSAGE_UPLOAD_IMAGE, "success", null);
                message.showMessage();



                Intent intent = new Intent(
                        LocationActivity.this,
                        GeoTagging.class);

                startActivity(intent);

                LocationActivity.this.finish();

            } else if (!result.equals("")) {
                AlertMessage message = new AlertMessage(
                        LocationActivity.this, AlertMessage.MESSAGE_ERROR
                        + result, "success", null);
                message.showMessage();
            }

        }


        @Override
        protected String doInBackground(Void... params) {
            try {

                GSKMTDatabase db = new GSKMTDatabase(LocationActivity.this);
                db.open();


                geotaglist = db.getGeotaggingData("D");

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

                                if (result.toString().equalsIgnoreCase(
                                        CommonString.KEY_FALSE)) {

                                    return CommonString.METHOD_Get_DR_STORE_IMAGES_GEO;
                                } else if (result
                                        .equalsIgnoreCase(CommonString.KEY_FAILURE)) {

                                    return CommonString.METHOD_Get_DR_STORE_IMAGES_GEO
                                            + "," + errormsg;
                                }

                                runOnUiThread(new Runnable() {

                                    public void run() {
                                        // TODO Auto-generated method stub

                                        message.setText("Image1 Upload");
                                    }
                                });
                            }
                        }
                        if (geotaglist.get(i).getUrl2() != null
                                && !geotaglist.get(i).getUrl2()
                                .equalsIgnoreCase("")) {

                            if (new File(Environment.getExternalStorageDirectory()+"/MT_GSK_Images/"
                                    + geotaglist.get(i).getUrl2()).exists()) {

                                result = UploadGeoImage(geotaglist.get(i).getUrl2(), "GeoTag");

                                if (result.toString().equalsIgnoreCase(
                                        CommonString.KEY_FALSE)) {

                                    return CommonString.METHOD_Get_DR_STORE_IMAGES_GEO;
                                } else if (result
                                        .equalsIgnoreCase(CommonString.KEY_FAILURE)) {

                                    return CommonString.METHOD_Get_DR_STORE_IMAGES_GEO
                                            + "," + errormsg;
                                }

                                runOnUiThread(new Runnable() {

                                    public void run() {
                                        // TODO Auto-generated method stub

                                        message.setText("Image2 Upload");
                                    }
                                });
                            }
                        }
                        if (geotaglist.get(i).getUrl3() != null
                                && !geotaglist.get(i).getUrl3()
                                .equalsIgnoreCase("")) {

                            if (new File(Environment.getExternalStorageDirectory()+"/MT_GSK_Images/"
                                    + geotaglist.get(i).getUrl3()).exists()) {

                                result = UploadGeoImage(geotaglist.get(i).getUrl3(), "GeoTag");

                                if (result.toString().equalsIgnoreCase(
                                        CommonString.KEY_FALSE)) {

                                    return CommonString.METHOD_Get_DR_STORE_IMAGES_GEO;
                                } else if (result
                                        .equalsIgnoreCase(CommonString.KEY_FAILURE)) {

                                    return CommonString.METHOD_Get_DR_STORE_IMAGES_GEO
                                            + "," + errormsg;
                                }

                                runOnUiThread(new Runnable() {

                                    public void run() {

                                        message.setText("Image3 Upload");
                                    }
                                });
                            }
                        }

//						for(int i =0 ; i < geotaglist.size(); i++){

//						}

                        db.open();
                        db.updateGeoTagDataInMain(geotaglist.get(i)
                                .getStoreid());

                        db.deleteGeoTagData(geotaglist.get(i).getStoreid());

                    }


                }



                return CommonString.KEY_SUCCESS;

            }

            catch (Exception ex) {
                ex.printStackTrace();
            }
            return "";

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

if (result.toString().equalsIgnoreCase(CommonString.KEY_FALSE)) {
			return CommonString.KEY_FALSE;
		}

		SAXParserFactory saxPF = SAXParserFactory.newInstance();
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
		}

	} else {
		return CommonString.KEY_FAILURE;
	}

	return "";
}


}

/** Called when the activity is first created. *//*





		// old code for gps

		*/
/*
		 * locationManager = (LocationManager) this
		 * .getSystemService(LOCATION_SERVICE); geocoder = new Geocoder(this);
		 * location = locationManager
		 * .getLastKnownLocation(LocationManager.GPS_PROVIDER);
		 *//*


		// new code
		locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
		locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0,
				0, this);


		Stroename.setText(storename);

		if (!(storelatitude.equals("0")) && !(storelongitude.equals("0"))) {

			int latiti = (int) (Double.parseDouble(storelatitude) * 1000000);
			int longi = (int) (Double.parseDouble(storelongitude) * 1000000);

			point = new GeoPoint(latiti, longi);
			mapController.setCenter(point);
			MapOverlay mapOverlay = new MapOverlay();
			List<Overlay> listOfOverlays = map.getOverlays();
			listOfOverlays.clear();
			listOfOverlays.add(mapOverlay);

		}
		else
		{startService();}


		Stroename.setText(storename);

		_pathforcheck = storeid + "front.jpg";
		_path1forcheck = storeid + "inside.jpg";
		_path2forcheck = storeid + "inside1.jpg";





	}




	private void startService() {

	Handler handler = new Handler();
	handler.postDelayed(new Runnable() {

		@Override
		public void run() {
			// TODO Auto-generated method stub



			// TODO Auto-generated method stub

			Intent intent = new Intent(
					"android.location.GPS_ENABLED_CHANGE");
			intent.putExtra("enabled", true);
			//sendBroadcast(intent);

			String lat = String.valueOf(data.getLatitude());
			String lon = String.valueOf(data.getLongitude());

			if (!lat.equals("0.0") && !lon.equals("0.0")) {

			} else {
				openNetwork();

			}



		}
	}, 60000);


	}



	public void openNetwork() {

		if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
			Intent intent = new Intent(
					"android.location.GPS_ENABLED_CHANGE");
			intent.putExtra("enabled", false);
		//	sendBroadcast(intent);
		}

		locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
		locationManager.requestLocationUpdates(
				LocationManager.NETWORK_PROVIDER, 0, 0, this);



	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		locationManager.removeUpdates(this);
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
				1000, 10, this);
		if (ImageUploadActivity.CheckGeotagImage(_pathforcheck)) {

			capture_1.setImageResource(R.drawable.camera_done);

		}
		if (ImageUploadActivity.CheckGeotagImage(_path1forcheck)) {

			capture_2.setImageResource(R.drawable.camera_done);

		}
		if (ImageUploadActivity.CheckGeotagImage(_path2forcheck)) {

			capture_3.setImageResource(R.drawable.camera_done);

		}
	}

	@Override
	protected boolean isRouteDisplayed() {
		// TODO Auto-generated method stub
		return false;
	}

	public void onLocationChanged(Location location) {
		// TODO Auto-generated method stub

		try {

			// Convert latitude and longitude into int that the GeoPoint
			// constructor can understand
			int latiti;
			int longi;
			if (!(storelatitude.equals("0")) && !(storelongitude.equals("0"))) {
				latiti = (int) (Double.parseDouble(storelatitude) * 1000000);
				longi = (int) (Double.parseDouble(storelongitude) * 1000000);

			} else {
				data.setLatitude((location.getLatitude()));
				data.setLongitude((location.getLongitude()));

				latiti = (int) (location.getLatitude() * 1000000);
				longi = (int) (location.getLongitude() * 1000000);

			}

			point = new GeoPoint(latiti, longi);
			mapController.animateTo(point);
			MapOverlay mapOverlay = new MapOverlay();
			List<Overlay> listOfOverlays = map.getOverlays();
			listOfOverlays.clear();
			listOfOverlays.add(mapOverlay);

		} catch (Exception e) {
			Log.e("LocateMe", "Could not get Geocoder data", e);
		}
	}

	public void onProviderDisabled(String provider) {
		// TODO Auto-generated method stub

	}

	public void onProviderEnabled(String provider) {
		// TODO Auto-generated method stub

	}

	public void gobacktogeoselection() {
		Intent intent = new Intent(LocationActivity.this, GeoTagging.class);

		startActivity(intent);
		this.finish();

	}

	public void onStatusChanged(String provider, int status, Bundle extras) {
		// TODO Auto-generated method stub

	}





















}
*/
