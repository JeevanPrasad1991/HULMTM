package com.cpm.DailyEntry;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import com.cpm.Constants.CommonString;

import com.cpm.DailyEntry.BeforeStock.ViewHolder;
import com.cpm.database.GSKMTDatabase;
import com.cpm.delegates.CoverageBean;
import com.cpm.delegates.SkuBean;
import com.example.gsk_mtt.R;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Color;
import android.inputmethodservice.Keyboard;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;

import android.text.InputType;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.View.OnFocusChangeListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class Sales extends Activity implements OnClickListener {
    private SharedPreferences preferences;
    private SharedPreferences.Editor editor = null;
    ListView lv;
    Button save;
    public ArrayList<SkuBean> sku_brand_list = new ArrayList<SkuBean>();
    GSKMTDatabase db;
    String store_id, category_id, process_id, intime, username, app_version,
            region_id, store_type_id, state_id, key_id, class_id, date;
    ArrayList<CoverageBean> coveragelist = new ArrayList<CoverageBean>();
    static int row_pos = 10000;
    AlertDialog alert;
    static int currentVersion = 1;
    boolean update = false, check_click = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sales);
        lv = (ListView) findViewById(R.id.ListView01);
        preferences = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        store_id = preferences.getString(CommonString.KEY_STORE_ID, null);
        store_type_id = preferences.getString(CommonString.storetype_id, null);
        category_id = preferences.getString(CommonString.KEY_CATEGORY_ID, null);
        process_id = preferences.getString(CommonString.KEY_PROCESS_ID, null);
        date = preferences.getString(CommonString.KEY_DATE, null);
        intime = preferences.getString(CommonString.KEY_IN_TIME, null);
        username = preferences.getString(CommonString.KEY_USERNAME, null);
        app_version = preferences.getString(CommonString.KEY_VERSION, null);
        region_id = preferences.getString(CommonString.region_id, null);
        ///change by jeevan RAna
        state_id = preferences.getString(CommonString.KEY_STATE_ID, null);
        key_id = preferences.getString(CommonString.KEY_ID, null);
        class_id = preferences.getString(CommonString.KEY_CLASS_ID, null);
        save = (Button) findViewById(R.id.savebtn);
        db = new GSKMTDatabase(Sales.this);
        db.open();
        coveragelist = db.getCoverageData(date, store_id, process_id);
        db.open();
        sku_brand_list = db.getSalesStockData(store_id, category_id, process_id);
        if (sku_brand_list.size() == 0) {
            sku_brand_list = db.getBrandSkuListForSales(category_id, store_id, process_id);
        } else {
            save.setText("Update");
            update = true;
        }
        if (sku_brand_list.size() > 0) {
            System.out.println("" + sku_brand_list.size());
            lv.setAdapter(new MyAdaptor(this));
        }


        save.setOnClickListener(this);
        currentVersion = android.os.Build.VERSION.SDK_INT;
        lv.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

            }

            @Override
            public void onScrollStateChanged(AbsListView arg0, int scrollState) {
                lv.clearFocus();
                if (SCROLL_STATE_TOUCH_SCROLL == scrollState) {
                    View currentFocus = getCurrentFocus();
                    if (currentFocus != null) {
                        currentFocus.clearFocus();
                    }
                }
            }

        });

    }


    @Override
    public void onBackPressed() {
        if (check_click) {
            AlertDialog.Builder builder = new AlertDialog.Builder(Sales.this);
            builder.setMessage("Are you sure you want to quit ?").setCancelable(false).setPositiveButton("Yes",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            alert.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(false);
                            Intent in = new Intent(Sales.this, DailyEntryMainMenu.class);
                            startActivity(in);
                            Sales.this.finish();


                        }
                    }).setNegativeButton("No",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.cancel();
                        }
                    });
            alert = builder.create();
            alert.show();
        } else {
            Intent in = new Intent(Sales.this, DailyEntryMainMenu.class);
            startActivity(in);
            Sales.this.finish();
        }
    }


    public String getCurrentTime() {
        Calendar m_cal = Calendar.getInstance();
        String intime = m_cal.get(Calendar.HOUR_OF_DAY) + ":" + m_cal.get(Calendar.MINUTE) + ":" + m_cal.get(Calendar.SECOND);
        return intime;

    }


    static class ViewHolder {
        TextView text, brand_name;
        LinearLayout l1;
        LinearLayout l2;
        EditText faceup;

    }


    private class MyAdaptor extends BaseAdapter {
        LayoutInflater mInflater;
        private Context mcontext;

        public MyAdaptor(Context context) {
            mInflater = LayoutInflater.from(context);
            mcontext = context;

        }

        @Override
        public int getCount() {
            return sku_brand_list.size();
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
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder;
            if (convertView == null) {
                convertView = mInflater.inflate(R.layout.sales_row, null);
                holder = new ViewHolder();
                holder.brand_name = (TextView) convertView.findViewById(R.id.brand);
                holder.text = (TextView) convertView.findViewById(R.id.mainpage_rememberme);
                holder.faceup = (EditText) convertView.findViewById(R.id.editText2);
                holder.l1 = (LinearLayout) convertView.findViewById(R.id.mainpage_header);
                holder.l2 = (LinearLayout) convertView.findViewById(R.id.mainpage_header2);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            holder.faceup.setOnFocusChangeListener(new OnFocusChangeListener() {
                public void onFocusChange(View v, boolean hasFocus) {
                    if (!hasFocus) {
                        final int position = v.getId();
                        final EditText Caption = (EditText) v;
                        String value1 = Caption.getText().toString();
                        if (value1.equals("")) {
                            sku_brand_list.get(position).setSales_qty("");
                        } else {
                            sku_brand_list.get(position).setSales_qty(value1);
                        }
                    }
                }
            });


            if (position == 0) {
                holder.brand_name.setText(sku_brand_list.get(position).getBrand());
                holder.brand_name.setId(position);
                holder.text.setText(sku_brand_list.get(position).getSku_name());
                holder.text.setId(position);

            } else {
                if (sku_brand_list.get(position - 1).getBrand().equalsIgnoreCase(sku_brand_list.get(position).getBrand())) {
                    holder.brand_name.setText("");
                    holder.brand_name.setId(position);
                    holder.text.setText(sku_brand_list.get(position).getSku_name());
                    holder.text.setId(position);
                } else {
                    holder.brand_name.setText(sku_brand_list.get(position).getBrand());
                    holder.brand_name.setId(position);
                    holder.text.setText(sku_brand_list.get(position).getSku_name());
                    holder.text.setId(position);
                }
            }

            holder.faceup.setText(sku_brand_list.get(position).getSales_qty());
            holder.faceup.setId(position);

            if (position == row_pos - 1) {
                holder.l1.setBackgroundColor(Color.RED);
                holder.l1.setId(position);
                holder.l2.setBackgroundColor(Color.RED);
                holder.l2.setId(position);
            }


            return convertView;
        }

    }


    public boolean validate_allvalues() {
        boolean result = true;
        for (int i = 0; i < sku_brand_list.size(); i++) {
            if (sku_brand_list.get(i).getSales_qty().equals("")) {
                result = false;
                break;
            }
        }

        return result;
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.savebtn) {
            lv.clearFocus();
            row_pos = 10000;
            if (validate_allvalues()) {
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setMessage("Are you sure you want to save").setCancelable(false).setPositiveButton("Yes",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                if (update) {
                                    for (int i = 0; i < sku_brand_list.size(); i++) {
                                        db.open();
                                        db.updateSalesData(store_id, category_id, process_id, sku_brand_list.get(i).getSales_qty(), sku_brand_list.get(i).getSku_id());
                                        Intent in = new Intent(Sales.this, DailyEntryMainMenu.class);
                                        startActivity(in);
                                        Sales.this.finish();
                                    }
                                } else {
                                    db.open();
                                    db.InsertSalesData(store_id, sku_brand_list, username, category_id, process_id);
                                    Intent in = new Intent(Sales.this, DailyEntryMainMenu.class);
                                    startActivity(in);
                                    Sales.this.finish();
                                }


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
                Toast.makeText(Sales.this, "Please fill all data", Toast.LENGTH_LONG).show();
            }
        }

    }
}
