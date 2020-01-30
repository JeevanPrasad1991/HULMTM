package com.cpm.DailyEntry;

import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.net.MalformedURLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;
import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;

import android.provider.MediaStore;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.cpm.Constants.CommonFunctions;
import com.cpm.Constants.CommonString;

import com.cpm.database.GSKMTDatabase;
import com.cpm.message.AlertMessage;
import com.cpm.xmlGetterSetter.FailureGetterSetter;
import com.cpm.xmlHandler.FailureXMLHandler;
import com.crashlytics.android.Crashlytics;
import com.example.gsk_mtt.R;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;

public class CheckOutStoreActivity extends Activity implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, View.OnClickListener {
    private String username, visit_date, store_id, store_intime, process_id, storename;
    protected String _pathforcheck = "", _path, str, image1 = "", currLatitude = "0.0", currLongitude = "0.0";
    private FailureGetterSetter failureGetterSetter = null;
    private SharedPreferences preferences = null;
    private TextView percentage, message, storename_checkout;
    private GSKMTDatabase db;
    ImageView checkout_image;
    private Dialog dialog;
    private ProgressBar pb;
    Button checkout_save;
    private Data data;
    GoogleApiClient mGoogleApiClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.checkout_activity);
        checkout_image = (ImageView) findViewById(R.id.checkout_image);
        checkout_save = (Button) findViewById(R.id.checkout_save);
        storename_checkout = findViewById(R.id.storename_checkout);
        preferences = PreferenceManager.getDefaultSharedPreferences(this);
        username = preferences.getString(CommonString.KEY_USERNAME, "");
        visit_date = preferences.getString(CommonString.KEY_DATE, null);
        store_id = preferences.getString(CommonString.KEY_STORE_ID, "");
        store_intime = preferences.getString(CommonString.KEY_IN_TIME, "");
        process_id = preferences.getString(CommonString.KEY_PROCESS_ID, "");
        storename = preferences.getString(CommonString.KEY_STORE_NAME, null);
        db = new GSKMTDatabase(this);
        db.open();
        storename_checkout.setText("Store - " + storename);
        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
        }
        if (Build.VERSION.SDK_INT >= 23 && ContextCompat.checkSelfPermission(getApplicationContext(),
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(getApplicationContext(),
                Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            return;
        }
        checkout_save.setOnClickListener(this);
        checkout_image.setOnClickListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        db.open();
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
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.checkout_image:
                _pathforcheck = store_id + "_CheckoutIMG_" + visit_date.replace("/", "") + "_"
                        + getCurrentTime().replace(":", "") + ".jpg";
                _path = CommonString.FILE_PATH + _pathforcheck;
                CommonFunctions.startAnncaCameraActivity(CheckOutStoreActivity.this, _path);

                break;
            case R.id.checkout_save:
                if (!image1.equals("")) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(this).setTitle("Parinaam").
                            setMessage("Do you want to save checkout data");
                    builder.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            if (CheckNetAvailability()) {
                                new BackgroundTask(CheckOutStoreActivity.this).execute();
                            } else {
                                Toast.makeText(getBaseContext(), "No internet Connection ! Please connect to network", Toast.LENGTH_LONG).show();
                            }
                        }
                    });
                    builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.dismiss();
                        }
                    });
                    builder.show();
                } else {
                    Toast.makeText(getBaseContext(), "Please click checkout image", Toast.LENGTH_LONG).show();
                }
        }
    }

    private class BackgroundTask extends AsyncTask<Void, Data, String> {
        private Context context;

        BackgroundTask(Context context) {
            this.context = context;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog = new Dialog(context);
            dialog.setContentView(R.layout.custom);
            dialog.setTitle("Uploading Checkout Data");
            dialog.setCancelable(false);
            dialog.show();
            pb = (ProgressBar) dialog.findViewById(R.id.progressBar1);
            percentage = (TextView) dialog.findViewById(R.id.percentage);
            message = (TextView) dialog.findViewById(R.id.message);
            final TextView tv_title = dialog.findViewById(R.id.tv_title);
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    tv_title.setText("Uploading Checkout Data");
                }
            });
        }

        @Override
        protected String doInBackground(Void... params) {

            try {
                data = new Data();
                data.value = 20;
                data.name = "Checkout Data Uploading";
                publishProgress(data);
                SAXParserFactory saxPF = SAXParserFactory.newInstance();
                SAXParser saxP = saxPF.newSAXParser();
                XMLReader xmlR = saxP.getXMLReader();
                String onXML = "[DATA][USER_DATA][USER_ID]" + username
                        + "[/USER_ID]" + "[LATITUDE]" + currLatitude
                        + "[/LATITUDE][LONGITUDE]" + currLongitude
                        + "[/LONGITUDE] [CHECKOUT_DATE]" + visit_date
                        + "[/CHECKOUT_DATE][CHECK_OUTTIME]" + getCurrentTime()
                        + "[/CHECK_OUTTIME][CHECK_INTIME]" + store_intime
                        + "[/CHECK_INTIME][CREATED_BY]" + username
                        + "[/CREATED_BY][STORE_ID]" + store_id
                        + "[/STORE_ID][PROCESS_ID]" + process_id
                        + "[/PROCESS_ID] [/USER_DATA][/DATA]";

                SoapObject request = new SoapObject(CommonString.NAMESPACE, CommonString.METHOD_Checkout_StatusNew);
                request.addProperty("onXML", onXML);
                SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
                envelope.dotNet = true;
                envelope.setOutputSoapObject(request);
                data.value = 40;
                data.name = "Checkout from store";
                publishProgress(data);
                HttpTransportSE androidHttpTransport = new HttpTransportSE(CommonString.URL);
                androidHttpTransport.call(CommonString.SOAP_ACTION_Checkout_StatusNew, envelope);
                Object result = (Object) envelope.getResponse();
                data.value = 100;
                data.name = "Checkout Done";
                publishProgress(data);
                if (result.toString().equalsIgnoreCase(CommonString.KEY_SUCCESS)) {
                    SharedPreferences.Editor editor = preferences.edit();
                    editor.putString(CommonString.KEY_STOREVISITED, "");
                    editor.putString(CommonString.KEY_STOREVISITED_STATUS, "");
                    editor.putString(CommonString.KEY_STORE_IN_TIME, "");
                    editor.putString(CommonString.KEY_OUT_TIME, "");
                    editor.putString(CommonString.KEY_LATITUDE, "");
                    editor.putString(CommonString.KEY_LONGITUDE, "");
                    editor.commit();
                    db.open();
                    db.updateStoreStatusOnCheckout(store_id, visit_date, CommonString.KEY_C, process_id);
                    db.updateCoverageCheckoutIMAGE(store_id, image1, process_id);
                    db.updateCoverageStatus(store_id, CommonString.KEY_C, process_id);
                    db.updateOutTime(store_id, getCurrentTime());
                } else {
                    if (result.toString().equalsIgnoreCase(CommonString.KEY_FALSE)) {
                        return CommonString.METHOD_Checkout_StatusNew;
                    }

                    // for failure
                    FailureXMLHandler failureXMLHandler = new FailureXMLHandler();
                    xmlR.setContentHandler(failureXMLHandler);
                    InputSource is = new InputSource();
                    is.setCharacterStream(new StringReader(result.toString()));
                    xmlR.parse(is);
                    failureGetterSetter = failureXMLHandler.getFailureGetterSetter();
                    if (failureGetterSetter.getStatus().equalsIgnoreCase(CommonString.KEY_FAILURE)) {
                        return CommonString.METHOD_Checkout_StatusNew + "," + failureGetterSetter.getErrorMsg();
                    }
                }
                return CommonString.KEY_SUCCESS;

            } catch (MalformedURLException e) {
                Crashlytics.logException(e);
                final AlertMessage message = new AlertMessage(
                        CheckOutStoreActivity.this,
                        AlertMessage.MESSAGE_EXCEPTION, "checkout", e);
                runOnUiThread(new Runnable() {

                    @Override
                    public void run() {
                        message.showMessage();
                    }
                });
            } catch (IOException e) {
                final AlertMessage message = new AlertMessage(
                        CheckOutStoreActivity.this,
                        AlertMessage.MESSAGE_SOCKETEXCEPTION,
                        "socket_checkout_store", e);
                runOnUiThread(new Runnable() {

                    @Override
                    public void run() {
                        message.showMessage();

                    }
                });
            } catch (Exception e) {
                Crashlytics.logException(e);
                final AlertMessage message = new AlertMessage(
                        CheckOutStoreActivity.this,
                        AlertMessage.MESSAGE_EXCEPTION, "acra_checkout", e);
                runOnUiThread(new Runnable() {

                    @Override
                    public void run() {
                        message.showMessage();
                    }
                });
            }

            return "";
        }

        @Override
        protected void onProgressUpdate(Data... values) {
            pb.setProgress(values[0].value);
            percentage.setText(values[0].value + "%");
            message.setText(values[0].name);

        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            dialog.dismiss();
            if (result.equals(CommonString.KEY_SUCCESS)) {
                AlertMessage message = new AlertMessage(CheckOutStoreActivity.this, AlertMessage.MESSAGE_CHECKOUT,
                        "checkout", null);
                message.showMessage();
            } else if (!result.equals("")) {
                AlertMessage message = new AlertMessage(CheckOutStoreActivity.this,
                        AlertMessage.MESSAGE_ERROR + result, "checkoutfail", null);
                message.showMessage();
            }

        }

    }

    class Data {
        int value;
        String name;
    }

    public String getCurrentTime() {
        Calendar m_cal = Calendar.getInstance();
        SimpleDateFormat formatter = new SimpleDateFormat("HH:mm:ss");
        String intime = formatter.format(m_cal.getTime());
        return intime;

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
                        if (new File(CommonString.FILE_PATH + _pathforcheck).exists()) {
                            String metadata = CommonFunctions.setMetadataAtImages(storename, store_id, "CHECHOUT IMAGE", username);
                            CommonFunctions.addMetadataAndTimeStampToImage(CheckOutStoreActivity.this, _path, metadata, visit_date);
                            image1 = _pathforcheck;
                            checkout_image.setBackgroundResource(R.drawable.camera_tick_ico);
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
            currLatitude = String.valueOf(mLastLocation.getLatitude());
            currLongitude = String.valueOf(mLastLocation.getLongitude());
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
