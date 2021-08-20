package com.cpm.DailyEntry;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.cpm.Constants.CommonString;
import com.cpm.database.GSKMTDatabase;
import com.cpm.delegates.SkuBean;
import com.example.gsk_mtt.R;

import java.util.ArrayList;
import java.util.Calendar;

public class SkuListedActivity extends Activity implements OnClickListener {
    private SharedPreferences preferences;
    private SharedPreferences.Editor editor = null;
    ListView lv;
    Button save;
    TextView header_name;
    public ArrayList<SkuBean> listedsku = new ArrayList<SkuBean>();
    GSKMTDatabase db;
    String store_id, process_id, intime, username, app_version,
            region_id, store_type_id, state_id, key_id, class_id, date;
    static int row_pos = 10000;
    AlertDialog alert;
    static int currentVersion = 1;
    boolean update = false, check_click = false;
    Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sales);
        context = this;
        header_name = (TextView) findViewById(R.id.textView1);
        lv = (ListView) findViewById(R.id.ListView01);
        preferences = PreferenceManager.getDefaultSharedPreferences(context);
        store_id = preferences.getString(CommonString.KEY_STORE_ID, null);
        store_type_id = preferences.getString(CommonString.storetype_id, null);
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
        header_name.setText("SKU Listed - " + date);
        db = new GSKMTDatabase(context);
        db.open();
        listedsku = db.getinsertedlistedSKU(store_id, process_id);
        if (listedsku.size() == 0) {
            listedsku = db.getSkuListed();
        } else {
            save.setText("Update");
            update = true;
        }

        if (listedsku.size() > 0) {
            lv.setAdapter(new MyAdaptor(context));
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
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setMessage("Are you sure you want to quit ?").setCancelable(false).setPositiveButton("Yes",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            alert.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(false);
                            Intent in = new Intent(context, CopyOfStorevisitedYesMenu.class);
                            startActivity(in);
                            SkuListedActivity.this.finish();


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
            Intent in = new Intent(context, CopyOfStorevisitedYesMenu.class);
            startActivity(in);
            SkuListedActivity.this.finish();
        }
    }

    public String getCurrentTime() {
        Calendar m_cal = Calendar.getInstance();
        String intime = m_cal.get(Calendar.HOUR_OF_DAY) + ":" + m_cal.get(Calendar.MINUTE) + ":" + m_cal.get(Calendar.SECOND);
        return intime;

    }

    class ViewHolder {
        Spinner spin_sku_availeb;
        TextView text, brand_name;
        LinearLayout l1;

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
            return listedsku.size();
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
            ViewHolder holder;
            if (convertView == null) {
                convertView = mInflater.inflate(R.layout.listed_sku, null);
                holder = new ViewHolder();
                holder.brand_name = (TextView) convertView.findViewById(R.id.brand);
                holder.text = (TextView) convertView.findViewById(R.id.mainpage_rememberme);
                holder.spin_sku_availeb = (Spinner) convertView.findViewById(R.id.spin_sku_availeb);
                holder.l1 = (LinearLayout) convertView.findViewById(R.id.mainpage_header);
                convertView.setTag(holder);

            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            if (position == 0) {
                holder.brand_name.setText(listedsku.get(position).getBrand());
                holder.brand_name.setId(position);
                holder.text.setText(listedsku.get(position).getSku_name());
                holder.text.setId(position);

            } else {
                if (listedsku.get(position - 1).getBrand().equalsIgnoreCase(listedsku.get(position).getBrand())) {
                    holder.brand_name.setText("");
                    holder.brand_name.setId(position);
                    holder.text.setText(listedsku.get(position).getSku_name());
                    holder.text.setId(position);
                } else {
                    holder.brand_name.setText(listedsku.get(position).getBrand());
                    holder.brand_name.setId(position);
                    holder.text.setText(listedsku.get(position).getSku_name());
                    holder.text.setId(position);
                }
            }

            CustomAdapter customAdapter = new CustomAdapter(mcontext, listedsku.get(position).getReasons());
            holder.spin_sku_availeb.setAdapter(customAdapter);

            holder.spin_sku_availeb.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> adapterView, View view, int itemPos, long l) {
                    if (itemPos != 0) {
                        listedsku.get(position).setReason_name(listedsku.get(position).getReasons().get(itemPos).getReason_name());
                    } else {
                        listedsku.get(position).setReason_name("");
                    }
                }

                @Override
                public void onNothingSelected(AdapterView<?> adapterView) {

                }
            });

            if (!listedsku.get(position).getReason_name().equals("")) {
                for (int i = 0; i < listedsku.get(position).getReasons().size(); i++) {
                    if (listedsku.get(position).getReason_name().equalsIgnoreCase(listedsku.get(position).getReasons().get(i).getReason_name())) {
                        holder.spin_sku_availeb.setSelection(i);
                        break;
                    }
                }
            }

            if (position == row_pos - 1) {
                holder.l1.setBackgroundColor(Color.RED);
                holder.l1.setId(position);
            }


            return convertView;
        }

    }

    public boolean validate_allvalues() {
        boolean result = true;
        for (int i = 0; i < listedsku.size(); i++) {
            if (listedsku.get(i).getReason_name().equals("")) {
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
                AlertDialog.Builder builder = new AlertDialog.Builder(context).setTitle(R.string.parinamm);
                builder.setMessage("Are you sure you want to save ?").setCancelable(false).
                        setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                if (update) {
                                    for (int i = 0; i < listedsku.size(); i++) {
                                        db.open();
                                        db.updateListedSku(store_id, process_id, listedsku.get(i).getReason_name(), listedsku.get(i).getSku_id());
                                    }

                                    Intent in = new Intent(context, CopyOfStorevisitedYesMenu.class);
                                    startActivity(in);
                                    SkuListedActivity.this.finish();
                                } else {
                                    db.open();
                                    db.InsertAllListedSku(store_id, listedsku, process_id);
                                    Intent in = new Intent(context, CopyOfStorevisitedYesMenu.class);
                                    startActivity(in);
                                    SkuListedActivity.this.finish();
                                }
                            }
                        }).setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
                AlertDialog alert = builder.create();
                alert.show();
            } else {
                Toast.makeText(context, "Please Select 'SKU' Listed", Toast.LENGTH_LONG).show();
            }
        }

    }

    private class CustomAdapter extends BaseAdapter {
        Context context;
        ArrayList<SkuBean> reasonData;

        public CustomAdapter(Context context, ArrayList<SkuBean> reasonData) {
            this.context = context;
            this.reasonData = reasonData;
        }

        @Override
        public int getCount() {
            return reasonData.size();
        }

        @Override
        public Object getItem(int i) {
            return null;
        }

        @Override
        public long getItemId(int i) {
            return 0;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            view = LayoutInflater.from(context).inflate(R.layout.custom_spinner_item, null);
            TextView names = (TextView) view.findViewById(R.id.tv_text);
            names.setText(reasonData.get(i).getReason_name());
            return view;
        }
    }

}