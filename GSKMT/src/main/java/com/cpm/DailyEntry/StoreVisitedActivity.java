package com.cpm.DailyEntry;


import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Calendar;

import com.cpm.Constants.CommonString;

import com.cpm.database.GSKMTDatabase;
import com.cpm.delegates.CoverageBean;
import com.cpm.delegates.StoreBean;
import com.cpm.message.AlertMessage;
import com.example.gsk_mtt.R;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.Toast;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

public class StoreVisitedActivity extends Activity {
    private SharedPreferences preferences;
    String date, store_id, store_intime, process_id, user_id;
    GSKMTDatabase db;
    private int _mid;
    ArrayList<CoverageBean> storestatus = new ArrayList<CoverageBean>();
    ArrayList<CoverageBean> coverageBeanlist = new ArrayList<>();
    ProgressDialog loading;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.storevisited);
        RadioButton yes = (RadioButton) findViewById(R.id.yes);
        RadioButton no = (RadioButton) findViewById(R.id.no);
        preferences = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        date = preferences.getString(CommonString.KEY_DATE, null);
        store_id = preferences.getString(CommonString.KEY_STORE_ID, null);
        store_intime = preferences.getString(CommonString.KEY_STORE_IN_TIME, "");
        process_id = preferences.getString(CommonString.KEY_PROCESS_ID, "");
        user_id = preferences.getString(CommonString.KEY_USERNAME, "");
        db = new GSKMTDatabase(StoreVisitedActivity.this);
        db.open();
        coverageBeanlist = db.getCoverageData(date, null, null);
        storestatus = db.getCoverageData(date, store_id, process_id);

        //////click on rediobutton...............

        if (storestatus.size() > 0 && storestatus.get(0).getStatus().equalsIgnoreCase(CommonString.KEY_CHECK_IN)
                || storestatus.size() > 0 && storestatus.get(0).getStatus().equalsIgnoreCase(CommonString.KEY_VALID)) {
            yes.setChecked(true);
        } else if (storestatus.size() > 0 && storestatus.get(0).getStatus().equalsIgnoreCase(CommonString.STORE_STATUS_LEAVE)) {
            no.setChecked(true);
        }
        yes.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (getMid() != 0) {
                    if (storestatus.size() > 0 && storestatus.get(0).getStatus().equalsIgnoreCase(CommonString.STORE_STATUS_LEAVE)) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(StoreVisitedActivity.this);
                        builder.setMessage("Your leave data will be deleted.")
                                .setCancelable(false)
                                .setPositiveButton("OK",
                                        new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int id) {
                                                if (CheckNetAvailability()) {
                                                    SharedPreferences.Editor editor = preferences.edit();
                                                    editor.putString(CommonString.KEY_IN_TIME, getCurrentTime());
                                                    editor.putString(CommonString.KEY_STOREVISITED, store_id);
                                                    editor.putString(CommonString.KEY_STOREVISITED_STATUS, "yes");
                                                    editor.commit();
                                                    UpdateData(user_id, store_id, date, "I");
                                                } else {
                                                    showToast("No Internet Available");
                                                }

                                            }
                                        })
                                .setNegativeButton("Back",
                                        new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog,
                                                                int id) {
                                                dialog.cancel();
                                                Intent i = new Intent(StoreVisitedActivity.this, CopyOfStorelistActivity.class);
                                                startActivity(i);
                                                StoreVisitedActivity.this.finish();
                                            }
                                        });

                        AlertDialog alert = builder.create();
                        alert.show();

                    } else {
                        if (checkcoverageStatus()) {
                            SharedPreferences.Editor editor = preferences.edit();
                            editor.putString(CommonString.KEY_IN_TIME, getCurrentTime());
                            editor.putString(CommonString.KEY_STOREVISITED, store_id);
                            editor.putString(CommonString.KEY_STOREVISITED_STATUS, "yes");
                            editor.commit();
                            Intent i = new Intent(StoreVisitedActivity.this, StoreInformationActivity.class);
                            startActivity(i);
                            StoreVisitedActivity.this.finish();
                        } else {
                            Intent in = new Intent(StoreVisitedActivity.this, CopyOfStorevisitedYesMenu.class);
                            startActivity(in);
                            StoreVisitedActivity.this.finish();
                        }
                    }
                } else {
                    SharedPreferences.Editor editor = preferences.edit();
                    editor.putString(CommonString.KEY_STORE_IN_TIME, getCurrentTime());
                    editor.putString(CommonString.KEY_STOREVISITED, store_id);
                    editor.putString(CommonString.KEY_STOREVISITED_STATUS, "yes");
                    editor.commit();
                    Intent i = new Intent(StoreVisitedActivity.this, StoreInformationActivity.class);
                    startActivity(i);
                    StoreVisitedActivity.this.finish();
                }
            }
        });


        no.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (getMid() != 0) {
                    if (storestatus.size() > 0 && storestatus.get(0).getStatus().equalsIgnoreCase(CommonString.STORE_STATUS_LEAVE)) {
                        SharedPreferences.Editor editor = preferences.edit();
                        editor.putString(CommonString.KEY_STORE_IN_TIME, "");
                        editor.putString(CommonString.KEY_STOREVISITED, "");
                        editor.putString(CommonString.KEY_STOREVISITED_STATUS, "");
                        editor.putString(CommonString.KEY_LATITUDE, "");
                        editor.putString(CommonString.KEY_LONGITUDE, "");
                        editor.commit();
                        Intent in = new Intent(StoreVisitedActivity.this, NonWorkingActivity.class);
                        startActivity(in);
                        StoreVisitedActivity.this.finish();
                    } else {
                        AlertDialog.Builder builder = new AlertDialog.Builder(StoreVisitedActivity.this);
                        builder.setMessage("Your all data will be deleted.")
                                .setCancelable(false)
                                .setPositiveButton("OK",
                                        new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog,
                                                                int id) {
                                                if (CheckNetAvailability()) {
                                                    SharedPreferences.Editor editor = preferences.edit();
                                                    editor.putString(CommonString.KEY_IN_TIME, "");
                                                    editor.putString(CommonString.KEY_STOREVISITED, "");
                                                    editor.putString(CommonString.KEY_STOREVISITED_STATUS, "");
                                                    editor.putString(CommonString.KEY_LATITUDE, "");
                                                    editor.putString(CommonString.KEY_LONGITUDE, "");
                                                    editor.commit();
                                                    UpdateData(user_id, store_id, date, "N");
                                                } else {
                                                    showToast("No Internet Available");
                                                }
                                            }
                                        }).setNegativeButton("Back",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog,
                                                        int id) {
                                        dialog.cancel();
                                        Intent i = new Intent(StoreVisitedActivity.this, CopyOfStorelistActivity.class);
                                        startActivity(i);
                                        StoreVisitedActivity.this.finish();
                                    }
                                });
                        AlertDialog alert = builder.create();
                        alert.show();
                    }
                } else {
                    SharedPreferences.Editor editor = preferences.edit();
                    editor.putString(CommonString.KEY_STORE_IN_TIME, "");
                    editor.putString(CommonString.KEY_STOREVISITED, "");
                    editor.putString(CommonString.KEY_STOREVISITED_STATUS, "");
                    editor.putString(CommonString.KEY_LATITUDE, "");
                    editor.putString(CommonString.KEY_LONGITUDE, "");
                    editor.commit();
                    db.updateStoreStatusOnLeave(store_id, date, CommonString.KEY_N, process_id);
                    Intent in = new Intent(StoreVisitedActivity.this, NonWorkingActivity.class);
                    startActivity(in);
                    StoreVisitedActivity.this.finish();
                }
            }
        });

    }


    private void showToast(String message) {
        Toast.makeText(getBaseContext(), message, Toast.LENGTH_LONG).show();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent in = new Intent(StoreVisitedActivity.this, CopyOfStorelistActivity.class);
        startActivity(in);
        StoreVisitedActivity.this.finish();
    }

    public String getCurrentTime() {
        Calendar m_cal = Calendar.getInstance();
        String intime = m_cal.get(Calendar.HOUR_OF_DAY) + ":" + m_cal.get(Calendar.MINUTE) + ":" + m_cal.get(Calendar.SECOND);
        return intime;
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

    public long getMid() {
        int mid = 0;
        mid = db.CheckMid(date, store_id, process_id);
        _mid = mid;
        return mid;
    }


    public void UpdateData(final String user_id, final String store_id, final String visit_date, final String status) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                new deletecoveragedata(StoreVisitedActivity.this, user_id, store_id, visit_date, status).execute();
            }
        });
    }

    private class deletecoveragedata extends AsyncTask<String, String, String> {
        Context context;
        String user_id, store_cd, visit_date, status;

        deletecoveragedata(Context context, String user_id, String store_cd, String visit_date, String status) {
            this.context = context;
            this.user_id = user_id;
            this.store_cd = store_cd;
            this.visit_date = visit_date;
            this.status = status;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            loading = ProgressDialog.show(context, "Deleting Coverage Data", "Please wait...", false, false);
        }

        @Override
        protected String doInBackground(String... strings) {
            try {
                String onXML = "", sos_xml = "";
                String final_xml = "";
                onXML = "[COVERAGE_STATUS][STORE_ID]"
                        + store_cd
                        + "[/STORE_ID]"
                        + "[VISIT_DATE]"
                        + visit_date
                        + "[/VISIT_DATE]"
                        + "[USERID]"
                        + user_id
                        + "[/USERID]"
                        + "[/COVERAGE_STATUS]";
                final_xml = final_xml + onXML;
                sos_xml = "[DATA]" + final_xml + "[/DATA]";
                SoapObject request1 = new SoapObject(CommonString.NAMESPACE, CommonString.MEHTOD_DELETE_COVERAGE);
                request1.addProperty("onXML", sos_xml);
                SoapSerializationEnvelope envelope1 = new SoapSerializationEnvelope(SoapEnvelope.VER11);
                envelope1.dotNet = true;
                envelope1.setOutputSoapObject(request1);
                HttpTransportSE androidHttpTransport1 = new HttpTransportSE(CommonString.URL);
                androidHttpTransport1.call(CommonString.SOAP_ACTION + CommonString.MEHTOD_DELETE_COVERAGE, envelope1);
                Object result1 = (Object) envelope1.getResponse();
                if (result1.toString().equalsIgnoreCase(CommonString.KEY_SUCCESS)) {
                    db.open();
                    db.deleteAllTables(store_id, process_id);
                    db.updateStoreStatusOnLeave(store_id, date, CommonString.KEY_N, process_id);
                    return CommonString.KEY_SUCCESS;
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
                if (status.equalsIgnoreCase("I")) {
                    Intent intent = new Intent(StoreVisitedActivity.this, StoreInformationActivity.class);
                    startActivity(intent);
                    StoreVisitedActivity.this.finish();
                } else {
                    Intent intent = new Intent(StoreVisitedActivity.this, NonWorkingActivity.class);
                    startActivity(intent);
                    StoreVisitedActivity.this.finish();
                }
                Toast.makeText(getApplicationContext(), "Coverage deleted Successfully", Toast.LENGTH_SHORT).show();
            } else if (s.equalsIgnoreCase(CommonString.KEY_FAILURE)) {
                Toast.makeText(getApplicationContext(), "Error in deleted coverage", Toast.LENGTH_SHORT).show();
            } else if (s.equals("")) {
                Toast.makeText(getApplicationContext(), "Coverage not deleted", Toast.LENGTH_SHORT).show();
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

    private boolean checkcoverageStatus() {
        boolean status = true;
        if (coverageBeanlist.size() > 0) {
            for (int i = 0; i < coverageBeanlist.size(); i++) {
                if (store_id.equals(coverageBeanlist.get(i).getStoreId())) {
                    status = false;
                    break;
                }
            }
        }
        return status;
    }

}
