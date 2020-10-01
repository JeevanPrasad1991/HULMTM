package com.cpm.DailyEntry;

import java.io.IOException;
import java.net.MalformedURLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import com.cpm.Constants.CommonString;


import com.cpm.database.GSKMTDatabase;
import com.cpm.delegates.CoverageBean;
import com.cpm.delegates.PromotionBean;
import com.cpm.delegates.SkuBean;
import com.cpm.delegates.StoreBean;
import com.cpm.delegates.TOTBean;
import com.cpm.gsk_mt.LoginActivity;
import com.cpm.gsk_mt.MainMenuActivity;
import com.cpm.message.AlertMessage;
import com.crashlytics.android.Crashlytics;
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

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

public class CopyOfStorelistActivity extends Activity {
    ArrayList<StoreBean> storelist = new ArrayList<StoreBean>();
    private SharedPreferences preferences;
    private SharedPreferences.Editor editor = null;
    boolean leavestaus = false, holidaystatus = false;
    ArrayList<SkuBean> category_list = new ArrayList<SkuBean>();
    ArrayList<SkuBean> afterStockData = new ArrayList<SkuBean>();
    ArrayList<SkuBean> additionalData = new ArrayList<SkuBean>();
    ArrayList<SkuBean> salesData = new ArrayList<SkuBean>();
    ArrayList<TOTBean> totMappingData = new ArrayList<TOTBean>();
    public static ArrayList<TOTBean> TOTdata = new ArrayList<TOTBean>();
    public ArrayList<TOTBean> TOTInsertdata = new ArrayList<TOTBean>();
    ArrayList<TOTBean> aftertotData = new ArrayList<TOTBean>();
    private ArrayList<CoverageBean> reasonlist = new ArrayList<CoverageBean>();
    ArrayList<PromotionBean> mappingPromotion = new ArrayList<PromotionBean>();
    ArrayList<PromotionBean> mappingPromotion1 = new ArrayList<PromotionBean>();
    String visit_process_id, key_id;
    StoreBean storestatus = new StoreBean();
    ProgressDialog loading;
    String visit_status;
    GSKMTDatabase db;
    String date, user_id;
    ListView lv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.storename);
        lv = (ListView) findViewById(R.id.list);
        preferences = PreferenceManager.getDefaultSharedPreferences(this);
        db = new GSKMTDatabase(CopyOfStorelistActivity.this);
        db.open();
        date = preferences.getString(CommonString.KEY_DATE, null);
        visit_status = preferences.getString(CommonString.KEY_STOREVISITED_STATUS, "");
        visit_process_id = preferences.getString(CommonString.KEY_PROCESS_ID, "");
        key_id = preferences.getString(CommonString.KEY_ID, null);
        user_id = preferences.getString(CommonString.KEY_USERNAME, "");
        fillData();
        db.open();
        storelist = db.getStoreData(date);
        if (storelist.size() > 0) {
            lv.setAdapter(new MyAdaptor());
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent in = new Intent(CopyOfStorelistActivity.this, MainMenuActivity.class);
        startActivity(in);
    }

    @Override
    protected void onResume() {
        super.onResume();
        DateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");
        Date date_device = new Date();
        if (!dateFormat.format(date_device).equalsIgnoreCase(date)) {
            AlertDialog.Builder builder = new AlertDialog.Builder(CopyOfStorelistActivity.this).setTitle("Alert Dialog");
            builder.setMessage("Your Device date does not match login Date. You will be logged out now");
            builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    startActivity(new Intent(CopyOfStorelistActivity.this, LoginActivity.class));
                    CopyOfStorelistActivity.this.finish();
                    dialog.dismiss();
                }
            });
            builder.show();
        }
    }


    private void fillData() {
        ArrayList<CoverageBean> coverageBeanlist = new ArrayList<CoverageBean>();
        coverageBeanlist = db.getCoverageData(date, null, null);
        for (int i = 0; i < coverageBeanlist.size(); i++) {
            boolean before_tot = false, after_tot = false, flagTOT = false, Promo = false, competitionpromotionflag = false, sales_flag = false;
            boolean flagCheckout = false;
            ///change by jeevan RAna
            db.open();
            storestatus = db.getStoreStatus(coverageBeanlist.get(i).getStoreId(), coverageBeanlist.get(i).getProcess_id());
            if (storestatus.getCHECKOUT_STATUS() != null && storestatus.getCHECKOUT_STATUS().equalsIgnoreCase(CommonString.STORE_STATUS_LEAVE) ||
                    storestatus.getUPLOAD_STATUS() != null && storestatus.getUPLOAD_STATUS().equalsIgnoreCase(
                            CommonString.STORE_STATUS_LEAVE) || storestatus.getCHECKOUT_STATUS() != null && storestatus.getCHECKOUT_STATUS().equalsIgnoreCase(CommonString.KEY_C)) {
            } else {
                db.open();
                category_list = db.getCategoryList(coverageBeanlist.get(i).getProcess_id(), storestatus.getSTORE_ID());
                if (category_list.size() > 0) {
                    for (int j = 0; j < category_list.size(); j++) {
                        db.open();
                        afterStockData = db.getAfterStockData(coverageBeanlist.get(i).getStoreId(), category_list.get(j).getCategory_id(),
                                coverageBeanlist.get(i).getProcess_id());
                        db.open();
                        additionalData = db.getProductEntryDetail(coverageBeanlist.get(i).getStoreId(), category_list.get(j).getCategory_id(),
                                coverageBeanlist.get(i).getProcess_id());
                        db.open();
                        totMappingData = db.getTOTData(coverageBeanlist.get(i).getStoreId(), coverageBeanlist.get(i).getProcess_id(),
                                category_list.get(j).getCategory_id());
                        db.open();
                        salesData = db.getSalesStockData(coverageBeanlist.get(i).getStoreId(), category_list.get(j).getCategory_id(),
                                coverageBeanlist.get(i).getProcess_id());
                        db.open();
                        mappingPromotion = db.getPromoComplianceData(key_id, coverageBeanlist.get(i).getProcess_id(), category_list.get(j).getCategory_id());
                        if (mappingPromotion.size() > 0) {
                            db.open();
                            mappingPromotion1 = db.getInsertedPromoCompliance(coverageBeanlist.get(i).getStoreId(),
                                    category_list.get(j).getCategory_id(), coverageBeanlist.get(i).getProcess_id());
                            if (mappingPromotion1.size() > 0) {
                                Promo = true;
                            } else {
                                Promo = false;
                            }
                        } else {
                            Promo = true;
                        }

                        db.open();
                        StoreBean storeSalesStatus = db.getStoreStatus(coverageBeanlist.get(i).getStoreId(), coverageBeanlist.get(i).getProcess_id());

                        if (storeSalesStatus != null && storeSalesStatus.getSale_enableFlag() != null && !storeSalesStatus.getSale_enableFlag().equals("")
                                && storeSalesStatus.getSale_enableFlag().equals("1")&& category_list.get(j).getShowsalesflag()!=null
                                &&category_list.get(j).getShowsalesflag().equals("1")) {
                           if (db.getBrandSkuListForSales(category_list.get(j).getCategory_id(),
                                   coverageBeanlist.get(i).getStoreId()
                                   , coverageBeanlist.get(i).getProcess_id()).size() > 0){
                               if (salesData.size() > 0) {
                                   sales_flag = true;
                               } else {
                                   sales_flag = false;
                               }
                           }else {
                               sales_flag = true;
                           }

                        } else {
                            sales_flag = true;
                        }

                        db.open();
                        TOTdata = db.getTOTData(coverageBeanlist.get(i).getStoreId(),
                                coverageBeanlist.get(i).getProcess_id(),
                                category_list.get(j).getCategory_id());
                        if (TOTdata.size() > 0) {
                            db.open();
                            TOTInsertdata = db.getInsertedAfterTOTData(coverageBeanlist.get(i).getStoreId(),
                                    category_list.get(j).getCategory_id(), coverageBeanlist.get(i).getProcess_id());
                            if (TOTInsertdata.size() > 0) {
                                flagTOT = true;
                            } else {
                                flagTOT = false;
                            }
                        } else {
                            flagTOT = true;
                        }

                        if (totMappingData.size() > 0) {
                            db.open();
                            aftertotData = db.getAfterTOTData(coverageBeanlist.get(i).getStoreId(), category_list.get(j).getCategory_id(),
                                    coverageBeanlist.get(i).getProcess_id());
                            if (aftertotData.size() > 0) {
                                before_tot = true;
                                after_tot = true;
                            }
                        } else {
                            before_tot = true;
                            after_tot = true;
                        }

                        if (category_list.get(j).getCategory_id().equals("1") || category_list.get(j).getCategory_id().equals("3")) {
                            if (storestatus != null && storestatus.getCOMP_ENABLE() != null && storestatus.getCOMP_ENABLE().equalsIgnoreCase("Y")) {
                                db.open();
                                if (db.getcomptitiondataforpromotion(category_list.get(j).getCategory_id()).size() > 0) {
                                    db.open();
                                    if (db.getcompetitionPromotionfromDatabase(coverageBeanlist.get(i).getStoreId(),
                                            category_list.get(j).getCategory_id(), coverageBeanlist.get(i).getProcess_id()).size() > 0) {
                                        competitionpromotionflag = true;
                                    } else {
                                        competitionpromotionflag = false;
                                    }
                                } else {
                                    competitionpromotionflag = true;
                                }
                            } else {
                                competitionpromotionflag = true;
                            }
                        } else {
                            competitionpromotionflag = true;
                        }

                        if (coverageBeanlist.get(i).getProcess_id().equals("2")) {
                            if (before_tot == true && after_tot == true && afterStockData.size() > 0 && additionalData.size() > 0 && Promo
                                    && flagTOT && db.getEnteredCompetitionDetail(coverageBeanlist.get(i).getStoreId(),
                                    category_list.get(j).getCategory_id(), coverageBeanlist.get(i).getProcess_id()).size() > 0
                                    && competitionpromotionflag == true && sales_flag) {
                                flagCheckout = true;
                            } else {
                                flagCheckout = false;
                                break;
                            }
                        } else {
                            if (before_tot == true && after_tot == true && afterStockData.size() > 0
                                    && additionalData.size() > 0 && Promo && flagTOT
                                    && db.getEnteredCompetitionDetail(coverageBeanlist.get(i).getStoreId(),
                                    category_list.get(j).getCategory_id(), coverageBeanlist.get(i).getProcess_id()).size() > 0
                                    && competitionpromotionflag == true && sales_flag) {
                                flagCheckout = true;
                            } else {
                                flagCheckout = false;
                                break;
                            }
                        }
                    }
                }


                if (flagCheckout) {
                    db.open();
                    db.updateStoreStatusOnLeave(coverageBeanlist.get(i).getStoreId(), date, CommonString.KEY_VALID, visit_process_id);
                    db.open();
                    db.updateCoverageStatus(coverageBeanlist.get(i).getStoreId(), CommonString.KEY_VALID, visit_process_id);
                }
            }
        }
    }


    private class ViewHolder {
        TextView storename, storeaddress;
        ImageView imgtick;
        Button checkout;
        Button checkin_cancel;
        RelativeLayout l1;
    }

    public String getCurrentTime() {
        Calendar m_cal = Calendar.getInstance();
        SimpleDateFormat formatter = new SimpleDateFormat("HH:mm:ss");
        String intime = formatter.format(m_cal.getTime());
        return intime;
    }

    private void showToast(String message) {
        Toast.makeText(getBaseContext(), message, Toast.LENGTH_LONG).show();
    }


    public boolean CheckNetAvailability() {
        boolean connected = false;
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE)
                .getState() == NetworkInfo.State.CONNECTED || connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED) {
            connected = true;
        }
        return connected;
    }

    private class MyAdaptor extends BaseAdapter {
        @Override
        public int getCount() {
            return storelist.size();
        }

        @Override
        public Object getItem(int position) {
            return position;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            ViewHolder holder = null;
            if (convertView == null) {
                holder = new ViewHolder();
                LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = inflater.inflate(R.layout.storeviewlist, null);
                holder.storename = (TextView) convertView.findViewById(R.id.storelistviewxml_storename);
                holder.storeaddress = (TextView) convertView.findViewById(R.id.storelistviewxml_storeaddress);
                holder.imgtick = (ImageView) convertView.findViewById(R.id.storelistviewxml_storeico);
                holder.checkout = (Button) convertView.findViewById(R.id.chkout);
                holder.checkin_cancel = (Button) convertView.findViewById(R.id.chkin);
                holder.l1 = (RelativeLayout) convertView.findViewById(R.id.storenamelistview_layout);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            holder.storename.setText(storelist.get(position).getSTORE() + " Store Id - " + storelist.get(position).getSTORE_ID());
            holder.storeaddress.setText(storelist.get(position).getCITY());

            holder.l1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    try {
                        DateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");
                        Date date_device = new Date();
                        if (dateFormat.format(date_device).equalsIgnoreCase(date)) {
                            StoreBean sb = storelist.get(position);
                            if (sb.getUPLOAD_STATUS().equalsIgnoreCase(CommonString.KEY_U)) {
                                showToast(AlertMessage.MESSAGE_UPLOAD);
                            } else if (sb.getUPLOAD_STATUS().equals(CommonString.KEY_D)) {
                                showToast(AlertMessage.MESSAGE_DATA_UPLOAD);
                            } else if (sb.getUPLOAD_STATUS().equals(CommonString.KEY_P)) {
                                showToast(AlertMessage.MESSAGE_PARTIAL_UPLOAD);
                            } else if (sb.getCHECKOUT_STATUS().equals(CommonString.KEY_C)) {
                                showToast(AlertMessage.MESSAGE_CHECKOUT_UPLOAD);
                            } else {
                                boolean entry_flag = true;
                                for (int j = 0; j < storelist.size(); j++) {
                                    if (!storelist.get(j).getCHECKOUT_STATUS().equalsIgnoreCase(CommonString.KEY_C) &&
                                            storelist.get(j).getUPLOAD_STATUS().equalsIgnoreCase(CommonString.KEY_CHECK_IN) ||
                                            !storelist.get(j).getCHECKOUT_STATUS().equalsIgnoreCase(CommonString.KEY_C) &&
                                                    storelist.get(j).getUPLOAD_STATUS().equalsIgnoreCase(CommonString.KEY_VALID)) {

                                        if (sb.getSTORE() != storelist.get(j).getSTORE()) {
                                            entry_flag = false;
                                            break;
                                        } else {
                                            break;

                                        }
                                    }
                                }

                                if (!entry_flag) {
                                    showToast(AlertMessage.title_store_list_checkout_current);
                                } else {
                                    editor = preferences.edit();
                                    editor.putString(CommonString.KEY_STORE_ID, sb.getSTORE_ID());
                                    editor.putString(CommonString.KEY_STORE_NAME, sb.getSTORE());
                                    editor.putString(CommonString.KEY_VISIT_DATE, sb.getVISIT_DATE());
                                    editor.putString(CommonString.storetype_id, sb.getStoreType_id());
                                    editor.putString(CommonString.region_id, sb.getREGION_ID());
                                    editor.putString(CommonString.KEY_PROCESS_ID, sb.getPROCESS_ID());
                                    editor.putString(CommonString.KEY_ID, sb.getKey_id());
                                    editor.putString(CommonString.KEY_PACKED_KEY, sb.getPkdKey());
                                    editor.putString(CommonString.KEY_STATE_ID, sb.getSTATE_ID());
                                    editor.putString(CommonString.KEY_CLASS_ID, sb.getCLASS_ID());
                                    editor.putString(CommonString.KEY_COMPETITION_PROMOTION, sb.getCOMP_ENABLE());
                                    editor.putString(CommonString.KEY_SALEENABLE_FLAG, sb.getSale_enableFlag());
                                    editor.commit();
                                    if (sb.getPROCESS_ID().equals("3")) {
                                        Intent intent = new Intent(getBaseContext(), StoreWisePerformance.class);
                                        startActivity(intent);
                                        CopyOfStorelistActivity.this.finish();
                                    } else {
                                        Intent intent = new Intent(getBaseContext(), StoreVisitedActivity.class);
                                        startActivity(intent);
                                        CopyOfStorelistActivity.this.finish();
                                    }
                                }
                            }
                        } else {
                            AlertDialog.Builder builder = new AlertDialog.Builder(CopyOfStorelistActivity.this).setTitle("Alert Dialog");
                            builder.setMessage("Your Device date does not match login Date. You will be logged out now");
                            builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    startActivity(new Intent(CopyOfStorelistActivity.this, LoginActivity.class));
                                    CopyOfStorelistActivity.this.finish();
                                    dialog.dismiss();
                                }
                            });
                            builder.show();
                        }
                    } catch (Exception e) {
                        Crashlytics.logException(e);
                        e.printStackTrace();
                    }

                }
            });
            holder.checkin_cancel.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View arg0) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(CopyOfStorelistActivity.this);
                    builder.setMessage(
                            "Are you sure you want to cancel checkin?")
                            .setCancelable(false)
                            .setPositiveButton(
                                    "OK",
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(
                                                DialogInterface dialog,
                                                int id) {
                                            try {
                                                editor = preferences.edit();
                                                editor.putString(CommonString.KEY_STOREVISITED, "none");
                                                editor.putString(CommonString.KEY_STOREVISITED_STATUS, "");
                                                editor.putString(CommonString.KEY_STORE_IN_TIME, "");
                                                editor.putString(CommonString.KEY_LATITUDE, "");
                                                editor.putString(CommonString.KEY_LONGITUDE, "");
                                                editor.commit();
                                                new deletecoveragedata(CopyOfStorelistActivity.this, user_id, storelist.get(position).getSTORE_ID(), date,
                                                        "N", storelist.get(position).getPROCESS_ID()).execute();
                                                storelist.get(position).setUPLOAD_STATUS("N");
                                            } catch (Exception e) {
                                                e.printStackTrace();
                                            }

                                        }
                                    })
                            .setNegativeButton("Cancel",
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int id) {
                                            dialog.cancel();
                                        }
                                    });
                    AlertDialog alert = builder.create();
                    alert.show();

                }
            });
            holder.checkout.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(
                            CopyOfStorelistActivity.this);
                    builder.setMessage("Are you sure you want to checkout")
                            .setCancelable(false)
                            .setPositiveButton("OK",
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(
                                                DialogInterface dialog, int id) {
                                            if (CheckNetAvailability()) {
                                                String outTime = getCurrentTime();
                                                editor = preferences.edit();
                                                editor.putString(CommonString.KEY_STORE_ID, storelist.get(position).getSTORE_ID());
                                                editor.putString(CommonString.KEY_STORE_NAME, storelist.get(position).getSTORE());
                                                editor.putString(CommonString.KEY_OUT_TIME, outTime);
                                                editor.commit();
                                                Intent i = new Intent(CopyOfStorelistActivity.this, CheckOutStoreActivity.class);
                                                startActivity(i);
                                            } else {
                                                Toast.makeText(CopyOfStorelistActivity.this, "No internet availeble ! Please check your connection", Toast.LENGTH_SHORT).show();
                                            }
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

                }
            });

            boolean checkoutdisableflag = false;

            if (storelist.get(position).getUPLOAD_STATUS().equalsIgnoreCase(CommonString.KEY_P) &&
                    storelist.get(position).getCHECKOUT_STATUS().equals(CommonString.KEY_C)) {
                holder.checkout.setBackgroundResource(R.drawable.tick_c);
                holder.checkout.setEnabled(false);
                holder.checkout.setVisibility(View.VISIBLE);
                holder.checkin_cancel.setVisibility(View.INVISIBLE);
                holder.checkout.setVisibility(View.INVISIBLE);
                holder.imgtick.setBackgroundResource(R.drawable.tick_p);
                holder.imgtick.setVisibility(View.VISIBLE);
                checkoutdisableflag = true;
                ///////////change by jeeevannnnnnnnnnnn
            } else if (storelist.get(position).getUPLOAD_STATUS().equalsIgnoreCase(CommonString.KEY_D) &&
                    storelist.get(position).getCHECKOUT_STATUS().equals(CommonString.KEY_C)) {
                holder.checkout.setBackgroundResource(R.drawable.tick_c);
                holder.checkout.setEnabled(false);
                holder.checkout.setVisibility(View.VISIBLE);
                holder.checkin_cancel.setVisibility(View.INVISIBLE);
                holder.checkout.setVisibility(View.INVISIBLE);
                holder.imgtick.setBackgroundResource(R.drawable.tick_d);
                holder.imgtick.setVisibility(View.VISIBLE);
                checkoutdisableflag = true;
                ///////////change by jeeevannnnnnnnnnnn
            } else if (storelist.get(position).getUPLOAD_STATUS().equalsIgnoreCase(CommonString.KEY_U) &&
                    storelist.get(position).getCHECKOUT_STATUS().equals(CommonString.KEY_C)) {
                holder.checkout.setBackgroundResource(R.drawable.tick_c);
                holder.checkout.setEnabled(false);
                holder.checkout.setVisibility(View.VISIBLE);
                holder.checkin_cancel.setVisibility(View.INVISIBLE);
                holder.checkout.setVisibility(View.INVISIBLE);
                holder.imgtick.setBackgroundResource(R.drawable.tick_u);
                holder.imgtick.setVisibility(View.VISIBLE);
                checkoutdisableflag = true;
                ///////////change by jeeevannnnnnnnnnnn

            } else if (!checkoutdisableflag && storelist.get(position).getCHECKOUT_STATUS().equals(CommonString.KEY_C)) {
                holder.checkout.setBackgroundResource(R.drawable.tick_c);
                holder.checkout.setEnabled(false);
                holder.checkout.setVisibility(View.VISIBLE);
                holder.checkin_cancel.setVisibility(View.INVISIBLE);
                ///////////change by jeeevannnnnnnnnnnn
            } else if ((storelist.get(position).getUPLOAD_STATUS().equals(CommonString.KEY_VALID))) {
                holder.checkout.setBackgroundResource(R.drawable.checkout);
                holder.checkout.setVisibility(View.VISIBLE);
                holder.checkout.setEnabled(true);
                holder.checkin_cancel.setVisibility(View.INVISIBLE);

            } else if (storelist.get(position).getUPLOAD_STATUS().equalsIgnoreCase(CommonString.KEY_CHECK_IN)) {
                holder.checkout.setVisibility(View.INVISIBLE);
                holder.checkin_cancel.setVisibility(View.VISIBLE);
                holder.checkin_cancel.setEnabled(true);
            } else {
                holder.checkin_cancel.setEnabled(false);
                holder.checkin_cancel.setVisibility(View.INVISIBLE);
                holder.checkout.setEnabled(false);
                holder.checkout.setVisibility(View.INVISIBLE);
                holder.imgtick.setBackgroundResource(R.drawable.store);
                holder.imgtick.setVisibility(View.VISIBLE);
            }

            if (storelist.get(position).getUPLOAD_STATUS().equalsIgnoreCase(CommonString.STORE_STATUS_LEAVE)) {
                leavestaus = false;
                holidaystatus = false;
                for (int i = 0; i < storelist.size(); i++) {
                    db.open();
                    reasonlist = db.getCoverageData(date, storelist.get(position).getSTORE_ID(), storelist.get(position).getPROCESS_ID());
                    if (reasonlist.size() > 0) {
                        if (reasonlist.get(0).getReasonid().equals("2")) {
                            leavestaus = true;
                            break;
                        } else if (reasonlist.get(0).getReasonid().equals("7")) {
                            holidaystatus = true;
                            break;
                        } else if (reasonlist.get(0).getReasonid().equals("3")) {
                            holder.imgtick.setBackgroundResource(R.drawable.m);

                        } else if (reasonlist.get(0).getReasonid().equals("13") || reasonlist.get(0).getReasonid().equals("14")) {
                            holder.imgtick.setBackgroundResource(R.drawable.o);
                        } else if (reasonlist.get(0).getReasonid().equals("12")) {
                            holder.imgtick.setBackgroundResource(R.drawable.s);
                        }
                    } else {
                        holder.imgtick.setBackgroundResource(R.drawable.store);
                        holder.imgtick.setId(position);
                    }

                }
            } else {
                holder.imgtick.setBackgroundResource(R.drawable.store);
            }


            if (leavestaus && (!storelist.get(position).getUPLOAD_STATUS().equals(CommonString.KEY_U))) {
                holder.imgtick.setBackgroundResource(R.drawable.tickl);
            } else if (holidaystatus == true && (!storelist.get(position).getUPLOAD_STATUS().equalsIgnoreCase(CommonString.KEY_U))) {
                holder.imgtick.setBackgroundResource(R.drawable.h);
            }
            if (storelist.get(position).getUPLOAD_STATUS().equalsIgnoreCase(CommonString.KEY_U)) {
                holder.imgtick.setBackgroundResource(R.drawable.tick_u);
                holder.checkout.setVisibility(View.INVISIBLE);
            } else if (storelist.get(position).getUPLOAD_STATUS().equalsIgnoreCase(CommonString.KEY_D)) {
                holder.imgtick.setVisibility(View.VISIBLE);
                holder.checkout.setVisibility(View.INVISIBLE);
                holder.imgtick.setBackgroundResource(R.drawable.tick_d);
            } else if (storelist.get(position).getUPLOAD_STATUS().equalsIgnoreCase(CommonString.KEY_P)) {
                holder.imgtick.setBackgroundResource(R.drawable.tick_p);
                holder.imgtick.setVisibility(View.VISIBLE);
                holder.checkout.setVisibility(View.INVISIBLE);
            }
            return convertView;
        }

    }

    private class deletecoveragedata extends AsyncTask<String, String, String> {
        Context context;
        String user_id, store_cd, visit_date, status, PROCESS_ID;

        deletecoveragedata(Context context, String user_id, String store_cd, String visit_date, String status, String PROCESS_ID) {
            this.context = context;
            this.user_id = user_id;
            this.store_cd = store_cd;
            this.visit_date = visit_date;
            this.status = status;
            this.PROCESS_ID = PROCESS_ID;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            loading = ProgressDialog.show(context, "Deleting Coverage Data", "Please wait...",
                    false, false);

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
                SoapObject request = new SoapObject(CommonString.NAMESPACE, CommonString.MEHTOD_DELETE_COVERAGE);
                request.addProperty("onXML", sos_xml);
                SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
                envelope.dotNet = true;
                envelope.setOutputSoapObject(request);
                HttpTransportSE androidHttpTransport1 = new HttpTransportSE(CommonString.URL);
                androidHttpTransport1.call(CommonString.SOAP_ACTION + CommonString.MEHTOD_DELETE_COVERAGE, envelope);
                Object result1 = (Object) envelope.getResponse();
                if (result1.toString().equalsIgnoreCase(CommonString.KEY_SUCCESS)) {
                    db.open();
                    db.updateStoreStatusOnLeave(store_cd, visit_date, status, PROCESS_ID);
                    db.open();
                    db.deleteCoverage(store_cd);
                    db.open();
                    db.deleteAllTables(store_cd, PROCESS_ID);
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
            lv.invalidateViews();
            if (s.equalsIgnoreCase(CommonString.KEY_SUCCESS)) {
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

}
