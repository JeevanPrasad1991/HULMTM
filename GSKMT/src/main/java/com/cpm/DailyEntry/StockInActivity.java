package com.cpm.DailyEntry;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.cpm.Constants.CommonString;
import com.cpm.database.GSKMTDatabase;
import com.cpm.delegates.CoverageBean;
import com.cpm.delegates.SkuBean;
import com.example.gsk_mtt.R;

import java.util.ArrayList;

public class StockInActivity extends Activity implements View.OnClickListener {
    private SharedPreferences preferences;
    private SharedPreferences.Editor editor = null;
    GSKMTDatabase db;
    public String date, state_id, key_id, class_id, storename, cat_name, store_id, category_id, process_id, intime, username, app_version, store_type_id;
    ArrayList<CoverageBean> coveragelist = new ArrayList<CoverageBean>();
    boolean isDialogOpen = true, check = true, update = false;
    ArrayList<SkuBean> sku_brand_list = new ArrayList<>();
    MyAdaptor adaptorfor_stock;
    ListView list_view_for_stock;
    TextView sos_target;
    Button save;
    Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sku_stockinward);
        context = this;
        save = (Button) findViewById(R.id.savebtn);
        sos_target = (TextView) findViewById(R.id.sos_target);
        list_view_for_stock = (ListView) findViewById(R.id.ListView01);
        save.setOnClickListener(this);

        preferences = PreferenceManager.getDefaultSharedPreferences(context);
        store_id = preferences.getString(CommonString.KEY_STORE_ID, null);
        category_id = preferences.getString(CommonString.KEY_CATEGORY_ID, null);
        process_id = preferences.getString(CommonString.KEY_PROCESS_ID, null);
        date = preferences.getString(CommonString.KEY_DATE, null);
        intime = preferences.getString(CommonString.KEY_IN_TIME, null);
        username = preferences.getString(CommonString.KEY_USERNAME, null);
        app_version = preferences.getString(CommonString.KEY_VERSION, null);
        store_type_id = preferences.getString(CommonString.storetype_id, null);
        ///change by jeevan RAna
        state_id = preferences.getString(CommonString.KEY_STATE_ID, null);
        key_id = preferences.getString(CommonString.KEY_ID, null);
        class_id = preferences.getString(CommonString.KEY_CLASS_ID, null);
        storename = preferences.getString(CommonString.KEY_STORE_NAME, "");
        cat_name = preferences.getString(CommonString.KEY_CATEGORY_NAME, "");
        db = new GSKMTDatabase(context);
        db.open();
        sos_target.setText("Stock/Inward - " + cat_name);
        coveragelist = db.getCoverageData(date, store_id, process_id);
        db.open();
        sku_brand_list = db.getstockininserteddata(store_id, category_id, process_id);
        if (sku_brand_list.size() == 0) {
            db.open();
            sku_brand_list = db.getBrandSkuListforstockin(category_id, store_id, process_id);
        } else {
            save.setText("Update");
            update = true;
        }

        if (sku_brand_list.size() > 0) {
            adaptorfor_stock = new MyAdaptor(context);
            list_view_for_stock.setAdapter(adaptorfor_stock);
        }

        list_view_for_stock.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

            }

            @Override
            public void onScrollStateChanged(AbsListView arg0, int scrollState) {
                list_view_for_stock.clearFocus();
                adaptorfor_stock.notifyDataSetChanged();
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
        if (isDialogOpen) {
            Intent in = new Intent(context, DailyEntryMainMenu.class);
            startActivity(in);
            StockInActivity.this.finish();
        } else {
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setMessage("Are you sure you want to quit ?").setCancelable(false).setPositiveButton("YES",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            Intent in = new Intent(context, DailyEntryMainMenu.class);
                            startActivity(in);
                            StockInActivity.this.finish();
                        }
                    }).setNegativeButton("NO",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.cancel();
                        }
                    });
            AlertDialog alert = builder.create();

            alert.show();
        }

    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.savebtn) {
            list_view_for_stock.clearFocus();
            list_view_for_stock.invalidateViews();
            if (checkfilled_stockin()) {
                AlertDialog.Builder builder = new AlertDialog.Builder(context).setTitle(R.string.parinamm);
                builder.setMessage("Are you sure you want to save ?").setCancelable(false).setPositiveButton("Yes",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                db.open();
                                db.InsertStockInwardData(store_id, date, sku_brand_list, username, category_id, cat_name, process_id);
                                db.open();
                                Intent in = new Intent(context, DailyEntryMainMenu.class);
                                startActivity(in);
                                StockInActivity.this.finish();
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
                Toast.makeText(context, "Please fill all 'Stock-In' data.", Toast.LENGTH_LONG).show();
            }
        }
    }

    private boolean checkfilled_stockin() {
        check = true;
        try {
            if (sku_brand_list.size() > 0) {
                for (int k = 0; k < sku_brand_list.size(); k++) {
                    if (sku_brand_list.get(k).getAfter_Stock().equals("")) {
                        check = false;
                        break;
                    }
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return check;
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
            final ViewHolder holder;

            if (convertView == null) {
                convertView = mInflater.inflate(R.layout.sku_row_stockin, null);
                holder = new ViewHolder();
                holder.brand_name = (TextView) convertView.findViewById(R.id.brand);
                holder.text = (TextView) convertView.findViewById(R.id.mainpage_rememberme);
                holder.txt_stock = (TextView) convertView.findViewById(R.id.txt_stock);
                holder.edt_stock = (EditText) convertView.findViewById(R.id.edt_stock);
                convertView.setTag(holder);

            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            if (position == 0) {
                holder.brand_name.setBackgroundResource(R.drawable.list_selector);
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
                    holder.brand_name.setBackgroundResource(R.drawable.list_selector);
                    holder.brand_name.setText(sku_brand_list.get(position).getBrand());
                    holder.brand_name.setId(position);
                    holder.text.setText(sku_brand_list.get(position).getSku_name());
                    holder.text.setId(position);
                }
            }

            holder.edt_stock.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                public void onFocusChange(View v, boolean hasFocus) {
                    if (!hasFocus) {
                        isDialogOpen = false;
                        final int position = v.getId();
                        final EditText Caption = (EditText) v;
                        String value1 = Caption.getText().toString().replaceFirst("^0+(?!$)", "");
                        if (value1.equals("")) {
                            sku_brand_list.get(position).setAfter_Stock("");
                        } else {
                            sku_brand_list.get(position).setAfter_Stock(value1);
                        }
                    }
                }
            });

            if (!check) {
                if (holder.edt_stock.getText().toString().equals("")) {
                    holder.edt_stock.setHint("EMPTY");
                    holder.edt_stock.setHintTextColor(Color.RED);
                }
            }

            holder.edt_stock.setText(sku_brand_list.get(position).getAfter_Stock());
            holder.edt_stock.setId(position);

            return convertView;
        }
    }

    static class ViewHolder {
        TextView text, brand_name, txt_stock;
        EditText edt_stock;

    }

}
