package com.cpm.gsk_mt;

import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import com.cpm.Constants.CommonString;

import com.cpm.autoupdate.AutoupdateActivity;
import com.cpm.database.GSKMTDatabase;
import com.cpm.message.AlertMessage;
import com.cpm.xmlGetterSetter.AttendenceStatusGetterSetter;
import com.cpm.xmlGetterSetter.FailureGetterSetter;
import com.cpm.xmlGetterSetter.LoginGetterSetter;
import com.cpm.xmlGetterSetter.NoticeBoardGetterSetter;
import com.cpm.xmlHandler.XMLHandlers;
import com.example.gsk_mtt.R;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.crashlytics.FirebaseCrashlytics;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.provider.Settings;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class LoginActivity extends Activity implements OnClickListener, LocationListener {
    private EditText mUsername, mPassword;
    private Button mLogin;
    private String username, password, p_username, p_password;
    private double latitude = 0.0, longitude = 0.0;
    private int versionCode;
    private boolean isChecked;
    private LocationManager locmanager = null;
    private SharedPreferences preferences = null;
    private SharedPreferences.Editor editor = null;
    private Intent intent = null;
    GSKMTDatabase database;
    static int counter = 1;
    // TextView login_version;
    String app_ver;
    int eventType;
    LoginGetterSetter lgs = null;
    AttendenceStatusGetterSetter attendenceStatus = null;
    NoticeBoardGetterSetter noticeboardgsetter = null;
    private FirebaseAnalytics mFirebaseAnalytics;
    private final static int PERMISSION_ALL = 99;
    TextView version_text;
    Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);
        context = this;
        ContentResolver.setMasterSyncAutomatically(false);
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);
        mUsername = (EditText) findViewById(R.id.login_usertextbox);
        mPassword = (EditText) findViewById(R.id.login_locktextbox);
        version_text = (TextView) findViewById(R.id.version_text);
      //  mUsername.setText("testmer");
        //mPassword.setText("cpm123");
        // mUsername.setText("sanjay.alkar");
        //mPassword.setText("cpm@5678");
        mLogin = (Button) findViewById(R.id.login_loginbtn);
        preferences = PreferenceManager.getDefaultSharedPreferences(context);
        editor = preferences.edit();
        p_username = preferences.getString(CommonString.KEY_USERNAME, null);
        p_password = preferences.getString(CommonString.KEY_PASSWORD, null);
        isChecked = preferences.getBoolean(CommonString.KEY_REMEMBER, false);
        try {
            app_ver = getPackageManager().getPackageInfo(getPackageName(), 0).versionName;
        } catch (NameNotFoundException e) {
            e.printStackTrace();
        }

        version_text.setText("(MT) Version - " + app_ver);
        database = new GSKMTDatabase(context);
        database.open();
        if (!isChecked) {
        } else {
            mUsername.setText(p_username);
            mPassword.setText(p_password);
        }
        mLogin.setOnClickListener(this);
        locmanager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        boolean enabled = locmanager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        // Check if enabled and if not send user to the GSP settings
        // Better solution would be to display a dialog and suggesting to
        // go to the settings
        if (!enabled) {
            AlertDialog.Builder alertDialog = new AlertDialog.Builder(context);
            // Setting Dialog Title
            alertDialog.setTitle("GPS IS DISABLED...");
            // Setting Dialog Message
            alertDialog.setMessage("Click ok to enable GPS.");
            // Setting Positive "Yes" Button
            alertDialog.setPositiveButton("YES", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                    startActivity(intent);
                }
            });
            // Setting Negative "NO" Button
            alertDialog.setNegativeButton("NO", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    // Write your code here to invoke NO event
                    dialog.cancel();
                }
            });
            // Showing Alert Message
            alertDialog.show();
        }

        checkAndRequestPermissions();

    }

    @Override
    protected void onResume() {
        super.onResume();
        // Create a Folder for Images
        File sdCard = Environment.getExternalStorageDirectory();
        File dir = new File(sdCard.getAbsolutePath() + "/" + CommonString.FOLDER_NAME_IMAGE);
        if (!dir.exists()) {
            dir.mkdirs();
        }

        checkAndRequestPermissions();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onClick(View v) {
        // TODO Auto-generated method stub
        username = mUsername.getText().toString().trim();
        password = mPassword.getText().toString().trim();
        switch (v.getId()) {
            case R.id.login_loginbtn:
                if (username.length() == 0) {
                    showToast("Please enter username");
                } else if (password.length() == 0) {
                    showToast("Please enter password");
                } else {
                    p_username = preferences.getString(CommonString.KEY_USERNAME, null);
                    p_password = preferences.getString(CommonString.KEY_PASSWORD, null);
                    // If no preferences are stored
                    if (p_username == null && p_password == null) {
                        if (CheckNetAvailability()) {
                            new AuthenticateTask().execute();
                        } else {
                            showToast("No Network and first login");
                        }
                    }
                    // If preferences are stored
                    else {
                        if (username.equalsIgnoreCase(p_username)) {
                            if (CheckNetAvailability()) {
                                new AuthenticateTask().execute();
                            } else if (password.equals(p_password)) {
                                intent = new Intent(context, NoticeBoardActivity.class);
                                startActivity(intent);
                                this.finish();
                                showToast("No Network and offline login");
                            } else {
                                showToast("Incorrect Password");
                            }
                        } else {
                            showToast("Incorrect Username");
                        }
                    }
                }
                break;

        }
    }

    private class AuthenticateTask extends AsyncTask<Void, Void, String> {
        private ProgressDialog dialog = null;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog = new ProgressDialog(LoginActivity.this);
            dialog.setTitle("Login");
            dialog.setMessage("Authenticating....");
            dialog.setCancelable(false);
            dialog.show();
        }


        @Override
        protected String doInBackground(Void... params) {
            try {
                versionCode = getPackageManager().getPackageInfo(getPackageName(), 0).versionCode;
                String userauth_xml = "[DATA]" + "[USER_DATA][USER_ID]"
                        + username + "[/USER_ID]" + "[PASSWORD]" + password
                        + "[/PASSWORD]" + "[IN_TIME]" + getCurrentTime()
                        + "[/IN_TIME]" + "[LATITUDE]" + latitude
                        + "[/LATITUDE]" + "[LONGITUDE]" + longitude
                        + "[/LONGITUDE]" + "[APP_VERSION]" + app_ver
                        + "[/APP_VERSION]" + "[ATT_MODE]OnLine[/ATT_MODE]"
                        + "[NETWORK_STATUS]" + "LoginStatus"
                        + "[/NETWORK_STATUS]" + "[/USER_DATA][/DATA]";

                SoapObject request = new SoapObject(CommonString.NAMESPACE, CommonString.METHOD_LOGIN);
                request.addProperty("onXML", userauth_xml);
                SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
                envelope.dotNet = true;
                envelope.setOutputSoapObject(request);
                HttpTransportSE androidHttpTransport = new HttpTransportSE(CommonString.URL);

                androidHttpTransport.call(CommonString.SOAP_ACTION_LOGIN, envelope);
                Object result = (Object) envelope.getResponse();

                if (result.toString().equalsIgnoreCase(CommonString.KEY_FAILURE)) {
                    final AlertMessage message = new AlertMessage((Activity) context, AlertMessage.MESSAGE_FAILURE, "login", null);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            message.showMessage();
                        }
                    });

                } else if (result.toString().equalsIgnoreCase(CommonString.KEY_FALSE)) {
                    final AlertMessage message = new AlertMessage((Activity) context, AlertMessage.MESSAGE_FALSE, "login", null);
                    runOnUiThread(new Runnable() {

                        @Override
                        public void run() {
                            message.showMessage();
                        }
                    });
                } else if (result.toString().equalsIgnoreCase(CommonString.KEY_CHANGED)) {
                    final AlertMessage message = new AlertMessage((Activity) context, AlertMessage.MESSAGE_CHANGED, "login", null);
                    runOnUiThread(new Runnable() {

                        @Override
                        public void run() {
                            message.showMessage();
                        }
                    });

                } else {
                    XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
                    factory.setNamespaceAware(true);
                    XmlPullParser xpp = factory.newPullParser();
                    xpp.setInput(new StringReader(result.toString()));
                    xpp.next();
                    eventType = xpp.getEventType();
                    FailureGetterSetter failureGetterSetter = XMLHandlers.failureXMLHandler(xpp, eventType);
                    if (failureGetterSetter.getStatus().equalsIgnoreCase(CommonString.KEY_FAILURE)) {
                        final AlertMessage message = new AlertMessage(
                                (Activity) context, CommonString.METHOD_LOGIN + failureGetterSetter.getErrorMsg(), "login", null);
                        runOnUiThread(new Runnable() {

                            @Override
                            public void run() {
                                // TODO Auto-generated method stub
                                message.showMessage();
                            }
                        });
                    } else {
                        try {
                            // For String source
                            xpp.setInput(new StringReader(result.toString()));
                            xpp.next();
                            eventType = xpp.getEventType();
                            lgs = XMLHandlers.loginXMLHandler(xpp, eventType);

                        } catch (XmlPullParserException e) {
                            e.printStackTrace();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                        request = new SoapObject(CommonString.NAMESPACE, CommonString.METHOD_NAME_UNIVERSAL_DOWNLOAD);
                        request.addProperty("UserName", username);
                        request.addProperty("Type", "ATTENDANCE_STATUS");
                        envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
                        envelope.dotNet = true;
                        envelope.setOutputSoapObject(request);
                        androidHttpTransport = new HttpTransportSE(CommonString.URL);
                        androidHttpTransport.call(CommonString.SOAP_ACTION_UNIVERSAL, envelope);
                        Object attendeceresult = (Object) envelope.getResponse();
                        if (result.toString() != null) {
                            xpp.setInput(new StringReader(attendeceresult.toString()));
                            xpp.next();
                            eventType = xpp.getEventType();
                            attendenceStatus = XMLHandlers.AttendenceStatusXMLHandler(xpp, eventType);
                        }


                        request = new SoapObject(CommonString.NAMESPACE, CommonString.METHOD_NAME_UNIVERSAL_DOWNLOAD);
                        request.addProperty("UserName", username);
                        request.addProperty("Type", "NOTICE_BOARD");
                        envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
                        envelope.dotNet = true;
                        envelope.setOutputSoapObject(request);
                        androidHttpTransport = new HttpTransportSE(CommonString.URL);
                        androidHttpTransport.call(CommonString.SOAP_ACTION_UNIVERSAL, envelope);
                        attendeceresult = (Object) envelope.getResponse();
                        if (attendeceresult.toString() != null) {
                            xpp.setInput(new StringReader(attendeceresult.toString()));
                            xpp.next();
                            eventType = xpp.getEventType();
                            noticeboardgsetter = XMLHandlers.noticeBoardXmlHandler(xpp, eventType);
                        }


                        // PUT IN PREFERENCES

                        editor.putString(CommonString.KEY_USERNAME, username);
                        editor.putString(CommonString.KEY_PASSWORD, password);
                        editor.putString(CommonString.KEY_VERSION, lgs.getAPP_VERSION());
                        editor.putString(CommonString.KEY_PATH, lgs.getAPP_PATH());
                        editor.putString(CommonString.KEY_DATE, lgs.getCURRENTDATE());
                        editor.putString(CommonString.KEY_ATTENDENCE_STATUS, attendenceStatus.getStaus());

                        editor.putString(CommonString.KEY_SERVER_NOTICE_BOARD_URL, noticeboardgsetter.getNoticeBoard());
                        editor.putString(CommonString.KEY_SERVER_QUIZ_URL, noticeboardgsetter.getQuizUrl());
                        editor.apply();

                        Bundle bundle = new Bundle();
                        bundle.putString(FirebaseAnalytics.Param.ITEM_ID, username.toLowerCase());
                        bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, CommonString.KEY_LOGIN_DATA);
                        bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, "Data");
                        mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);
                        FirebaseCrashlytics.getInstance().setUserId(username.toLowerCase());
                        return CommonString.KEY_SUCCESS;
                    }
                }

                return "";

            } catch (MalformedURLException e) {
                FirebaseCrashlytics.getInstance().recordException(e);
                final AlertMessage message = new AlertMessage((Activity) context, AlertMessage.MESSAGE_EXCEPTION, "acra_login", e);
                runOnUiThread(new Runnable() {

                    @Override
                    public void run() {
                        message.showMessage();
                    }
                });
            } catch (IOException e) {
                final AlertMessage message = new AlertMessage(
                        (Activity) context,
                        AlertMessage.MESSAGE_SOCKETEXCEPTION, "socket_login", e);
                counter++;
                runOnUiThread(new Runnable() {

                    @Override
                    public void run() {
                        // TODO Auto-generated method stub
                        if (counter < 3) {
                            new AuthenticateTask().execute();
                        } else {
                            message.showMessage();
                            counter = 1;
                        }
                    }
                });
            } catch (Exception e) {
                final AlertMessage message = new AlertMessage(
                        (Activity) context,
                        AlertMessage.MESSAGE_SOCKETEXCEPTION, "socket_login", e);
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
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            if (result != null && !result.equals("") && result.equalsIgnoreCase(CommonString.KEY_SUCCESS)) {
                if (preferences.getString(CommonString.KEY_VERSION, "").equals(Integer.toString(versionCode))) {
                    intent = new Intent(getBaseContext(), NoticeBoardActivity.class);
                    startActivity(intent);
                    finish();
                } else {
                    intent = new Intent(getBaseContext(), AutoupdateActivity.class);
                    intent.putExtra(CommonString.KEY_PATH, preferences.getString(CommonString.KEY_PATH, ""));
                    startActivity(intent);
                    finish();
                }
            }
            dialog.dismiss();
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

    private void showToast(String message) {
        Toast.makeText(getBaseContext(), message, Toast.LENGTH_LONG).show();
    }

    // for location
    @Override
    public void onLocationChanged(Location location) {

        latitude = location.getLatitude();
        longitude = location.getLongitude();
    }

    @Override
    public void onProviderDisabled(String arg0) {


    }

    @Override
    public void onProviderEnabled(String arg0) {


    }

    @Override
    public void onStatusChanged(String arg0, int arg1, Bundle arg2) {
    }

    public void onButtonClick(View v) {
        Intent startMain = new Intent(Intent.ACTION_MAIN);
        startMain.addCategory(Intent.CATEGORY_HOME);
        startMain.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(startMain);
    }

    public String getCurrentTime() {

        Calendar m_cal = Calendar.getInstance();

        String intime = m_cal.get(Calendar.HOUR_OF_DAY) + ":"
                + m_cal.get(Calendar.MINUTE) + ":" + m_cal.get(Calendar.SECOND);
        return intime;

    }

    @Override
    public void onBackPressed() {
        Intent startMain = new Intent(Intent.ACTION_MAIN);
        startMain.addCategory(Intent.CATEGORY_HOME);
        startMain.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(startMain);

    }

    private boolean checkAndRequestPermissions() {
        int permissionwrite_storage = ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        int CAMERA = ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA);
        int ACCESS_NETWORK_STATE = ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_NETWORK_STATE);
        int ACCESS_COARSE_LOCATION = ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION);
        int locationPermission = ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION);
        int READ_EXTERNAL_STORAGE = ContextCompat.checkSelfPermission(context, Manifest.permission.READ_EXTERNAL_STORAGE);
        List<String> listPermissionsNeeded = new ArrayList<>();

        if (permissionwrite_storage != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }
        if (CAMERA != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.CAMERA);
        }

        if (ACCESS_NETWORK_STATE != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.ACCESS_NETWORK_STATE);
        }
        if (ACCESS_COARSE_LOCATION != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.ACCESS_COARSE_LOCATION);
        }
        if (locationPermission != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.ACCESS_FINE_LOCATION);
        }

        if (READ_EXTERNAL_STORAGE != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.READ_EXTERNAL_STORAGE);
        }


        if (!listPermissionsNeeded.isEmpty()) {
            ActivityCompat.requestPermissions((Activity) context, listPermissionsNeeded.toArray(new String[listPermissionsNeeded.size()]), PERMISSION_ALL);
            return false;
        }
        return true;
    }

    private void showDialogOK(String message, DialogInterface.OnClickListener okListener) {
        new AlertDialog.Builder(this).setMessage(message).setPositiveButton("OK", okListener).setNegativeButton("Cancel", okListener).create().show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        Log.d("", "Permission callback called-------");
        switch (requestCode) {
            case PERMISSION_ALL: {
                Map<String, Integer> perms = new HashMap<>();
                // Initialize the map with both permissions
                perms.put(Manifest.permission.WRITE_EXTERNAL_STORAGE, PackageManager.PERMISSION_GRANTED);
                perms.put(Manifest.permission.CAMERA, PackageManager.PERMISSION_GRANTED);
                perms.put(Manifest.permission.ACCESS_NETWORK_STATE, PackageManager.PERMISSION_GRANTED);
                perms.put(Manifest.permission.ACCESS_COARSE_LOCATION, PackageManager.PERMISSION_GRANTED);
                perms.put(Manifest.permission.ACCESS_FINE_LOCATION, PackageManager.PERMISSION_GRANTED);
                perms.put(Manifest.permission.READ_EXTERNAL_STORAGE, PackageManager.PERMISSION_GRANTED);
                // Fill with actual results from user
                if (grantResults.length > 0) {
                    for (int i = 0; i < permissions.length; i++)
                        perms.put(permissions[i], grantResults[i]);
                    // Check for both permissions
                    if (perms.get(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
                            && perms.get(Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED
                            && perms.get(Manifest.permission.ACCESS_NETWORK_STATE) == PackageManager.PERMISSION_GRANTED
                            && perms.get(Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED
                            && perms.get(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                            && perms.get(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {


                        File sdCard = Environment.getExternalStorageDirectory();
                        File dir = new File(sdCard.getAbsolutePath() + "/" + CommonString.FOLDER_NAME_IMAGE);
                        if (!dir.exists()) {
                            dir.mkdirs();
                        }

                        Log.d("", "sms & location services permission granted");
                        // process the normal flow
                        //else any one or both the permissions are not granted
                    } else {
                        Log.d("", "Some permissions are not granted ask again ");
                        //permission is denied (this is the first time, when "never ask again" is not checked) so ask again explaining the usage of permission
//                        // shouldShowRequestPermissionRationale will return true
                        //show the dialog or snackbar saying its necessary and try again otherwise proceed with setup.
                        if (ActivityCompat.shouldShowRequestPermissionRationale((Activity) context, Manifest.permission.WRITE_EXTERNAL_STORAGE) ||
                                ActivityCompat.shouldShowRequestPermissionRationale((Activity) context, Manifest.permission.CAMERA) ||
                                ActivityCompat.shouldShowRequestPermissionRationale((Activity) context, Manifest.permission.ACCESS_NETWORK_STATE) ||
                                ActivityCompat.shouldShowRequestPermissionRationale((Activity) context, Manifest.permission.ACCESS_COARSE_LOCATION) ||
                                ActivityCompat.shouldShowRequestPermissionRationale((Activity) context, Manifest.permission.ACCESS_FINE_LOCATION) ||
                                ActivityCompat.shouldShowRequestPermissionRationale((Activity) context, Manifest.permission.READ_EXTERNAL_STORAGE)) {
                            showDialogOK("Location,File,Call and Camera Services Permission required for this app",
                                    new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            switch (which) {
                                                case DialogInterface.BUTTON_POSITIVE:
                                                    checkAndRequestPermissions();
                                                    break;
                                                case DialogInterface.BUTTON_NEGATIVE:
                                                    // proceed with logic by disabling the related features or quit the app.
                                                    Intent startMain = new Intent(Intent.ACTION_MAIN);
                                                    startMain.addCategory(Intent.CATEGORY_HOME);
                                                    startMain.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                                    startActivity(startMain);
                                                    break;
                                            }
                                        }
                                    });
                        } else {
                            Toast.makeText(context, "Go to settings and enable permissions", Toast.LENGTH_LONG).show();
                        }
                    }
                }
            }
        }

    }
}
