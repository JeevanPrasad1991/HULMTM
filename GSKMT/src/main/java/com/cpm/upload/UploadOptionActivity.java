package com.cpm.upload;

import java.util.ArrayList;
import java.util.Iterator;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.cpm.Constants.CommonString;
import com.cpm.database.GSKMTDatabase;
import com.cpm.delegates.CoverageBean;
import com.cpm.delegates.StoreBean;
import com.cpm.gsk_mt.MainMenuActivity;
import com.cpm.message.AlertMessage;
import com.crashlytics.android.Crashlytics;
import com.example.gsk_mtt.R;

public class UploadOptionActivity extends Activity {
    private SharedPreferences preferences;
    private static GSKMTDatabase database;
    ArrayList<CoverageBean> cdata = new ArrayList<CoverageBean>();
    StoreBean storestatus = new StoreBean();
    Button upload_data, upload_image;
    private Intent intent;
    private String date;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.upload_option);
        upload_data = findViewById(R.id.upload_data);
        upload_image = findViewById(R.id.upload_image);
        preferences = PreferenceManager.getDefaultSharedPreferences(this);
        date = preferences.getString(CommonString.KEY_DATE, null);
        database = new GSKMTDatabase(this);
        database.open();
    }


    public void onButtonClick(View v) {
        switch (v.getId()) {
            case R.id.upload_data:
                database.open();
                cdata = database.getCoverageData(date, null, null);
                if (cdata.size() == 0) {
                    Toast.makeText(getBaseContext(), AlertMessage.MESSAGE_NO_DATA, Toast.LENGTH_LONG).show();
                } else {
                    if (CheckNetAvailability()) {
                        if ((validate_data())) {
                            Intent i = new Intent(UploadOptionActivity.this, UploadDataActivity.class);
                            startActivity(i);
                            UploadOptionActivity.this.finish();
                        } else {
                            Toast.makeText(getBaseContext(), AlertMessage.MESSAGE_NO_DATA, Toast.LENGTH_LONG).show();
                        }
                    } else {
                        showToast("No Network Available");
                    }
                }

                break;
            case R.id.upload_image:
                database.open();
                cdata = database.getCoverageData(date, null, null);
                if (cdata.size() == 0) {
                    Toast.makeText(getBaseContext(), AlertMessage.MESSAGE_NO_IMAGE, Toast.LENGTH_LONG).show();
                } else {
                    if (CheckNetAvailability()) {
                        if (validate()) {
                            intent = new Intent(this, UploadImageActivity.class);
                            startActivity(intent);
                            finish();
                        } else {
                            Toast.makeText(getBaseContext(), AlertMessage.MESSAGE_DATA_FIRST, Toast.LENGTH_LONG).show();
                        }
                    } else {
                        showToast("No Network Available");
                    }
                }
                break;
        }
    }

    private void showToast(String message) {
        Toast.makeText(getBaseContext(), message, Toast.LENGTH_LONG).show();
    }

    public boolean validate_data() {
        boolean result = false;
        database.open();
        cdata = database.getCoverageData(date, null, null);
        try {
            if (cdata.size() > 0) {
                for (int i = 0; i < cdata.size(); i++) {
                    storestatus = database.getStoreStatus(cdata.get(i).getStoreId(), cdata.get(i).getProcess_id());
                    if (!storestatus.getUPLOAD_STATUS().equalsIgnoreCase(CommonString.KEY_D)) {
                        if ((storestatus.getCHECKOUT_STATUS().equalsIgnoreCase(
                                CommonString.KEY_C) || storestatus.getUPLOAD_STATUS().equalsIgnoreCase(CommonString.KEY_P) ||
                                storestatus.getUPLOAD_STATUS().equalsIgnoreCase(CommonString.STORE_STATUS_LEAVE) ||
                                storestatus.getCHECKOUT_STATUS().equalsIgnoreCase(CommonString.STORE_STATUS_LEAVE))) {
                            result = true;
                            break;

                        }
                    }
                }

            }
        } catch (Exception e) {
            e.printStackTrace();
            Crashlytics.logException(e);
        }
        return result;
    }

    public boolean validate() {
        boolean result = false;
        database.open();
        cdata = database.getCoverageData(date, null, null);
        try {
            if (cdata.size() > 0) {
                for (int i = 0; i < cdata.size(); i++) {
                    if (cdata.get(i).getStatus().equalsIgnoreCase(CommonString.KEY_D)) {
                        result = true;
                        break;
                    }
                }
            }
        } catch (Exception e) {
            Crashlytics.logException(e);
            e.printStackTrace();
        }
        return result;
    }

    @Override
    public void onBackPressed() {
        Intent i = new Intent(this, MainMenuActivity.class);
        startActivity(i);
        UploadOptionActivity.this.finish();
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


}
