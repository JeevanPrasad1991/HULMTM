package com.cpm.DailyEntry;

import java.util.ArrayList;

import com.cpm.Constants.CommonString;
import com.cpm.DailyEntry.BeforeAdditionalDisplay.MyAdaptor;
import com.cpm.DailyEntry.BeforeAdditionalDisplay.MyAdaptor.ViewHolder;
import com.cpm.database.GSKMTDatabase;
import com.cpm.delegates.SkuBean;
import com.cpm.delegates.TOTBean;
import com.example.gsk_mtt.R;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.Toast;

public class TOTstock_Activity extends Activity implements OnItemSelectedListener {
    Spinner brand, sku;
    EditText quantity;
    Button save_btn;
    public ListView listview;
    private SharedPreferences preferences;
    String store_id, category_id, process_id, date, intime, username, app_version, brand_name = "", brand_id = "", sku_name = "", sku_id = "", unique_id, display_id;
    GSKMTDatabase db;
    MyAdaptor adapterData;
    ArrayList<SkuBean> brand_list = new ArrayList<SkuBean>();
    ArrayList<SkuBean> sku_list = new ArrayList<SkuBean>();
    ArrayList<TOTBean> list = new ArrayList<TOTBean>();
    private ArrayAdapter<CharSequence> brandAdaptor, skuAdaptor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tot_stock);
        brand = (Spinner) findViewById(R.id.brand_name);
        sku = (Spinner) findViewById(R.id.sku_name);
        quantity = (EditText) findViewById(R.id.qty_bought);
        save_btn = (Button) findViewById(R.id.add_btn);
        listview = (ListView) findViewById(R.id.lv);
        preferences = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        store_id = preferences.getString(CommonString.KEY_STORE_ID, null);
        category_id = preferences.getString(CommonString.KEY_CATEGORY_ID, null);
        process_id = preferences.getString(CommonString.KEY_PROCESS_ID, null);
        date = preferences.getString(CommonString.KEY_DATE, null);
        intime = preferences.getString(CommonString.KEY_IN_TIME, null);
        username = preferences.getString(CommonString.KEY_USERNAME, null);
        app_version = preferences.getString(CommonString.KEY_VERSION, null);
        db = new GSKMTDatabase(TOTstock_Activity.this);
        db.open();

        unique_id = getIntent().getStringExtra("UniqueId");
        display_id = getIntent().getStringExtra("DisplayId");
        db.open();
        brand_list = db.getBrandList(category_id);
        brandAdaptor = new ArrayAdapter<CharSequence>(TOTstock_Activity.this, android.R.layout.simple_spinner_item);
        skuAdaptor = new ArrayAdapter<CharSequence>(TOTstock_Activity.this, android.R.layout.simple_spinner_item);
        brandAdaptor.add("Select Brand");
        skuAdaptor.add("Select Sku");
        for (int i = 0; i < brand_list.size(); i++) {
            brandAdaptor.add(brand_list.get(i).getBrand());
        }
        brand.setAdapter(brandAdaptor);
        brandAdaptor.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        skuAdaptor.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        brand.setOnItemSelectedListener(this);
        sku.setOnItemSelectedListener(this);

        db.open();
        list = db.getTOTStockEntryDetail(store_id, category_id, process_id, display_id, unique_id);

        if (list.size() > 0) {
            adapterData = new MyAdaptor(TOTstock_Activity.this, list);
            listview.setAdapter(adapterData);
            listview.invalidateViews();
        }


        save_btn.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {

                String quan = quantity.getText().toString();
                if (!quan.equalsIgnoreCase("")) {
                    if (validatedata()) {
                        TOTBean ab = new TOTBean();
                        ab.setQuantity(quan);
                        ab.setBrand(brand_name);
                        ab.setBrand_id(brand_id);
                        ab.setDisplay_id(display_id);
                        ab.setStore_id(store_id);
                        ab.setUnique_id(unique_id);
                        ab.setSku_id(sku_id);
                        ab.setSku_name(sku_name);
                        ab.setProcess_id(process_id);
                        ab.setQuantity(quan);
                        ab.setCategory_id(category_id);
                        db.InsertStockTot(ab);
                        brand.setSelection(0);
                        sku.setSelection(0);
                        quantity.setText("");
                        db.open();
                        list = db.getTOTStockEntryDetail(store_id, category_id, process_id, display_id, unique_id);
                        adapterData = new MyAdaptor(TOTstock_Activity.this, list);
                        listview.setAdapter(adapterData);
                        listview.invalidateViews();
                    } else {
                        Toast.makeText(getBaseContext(), "Please Select the Brand & Sku", Toast.LENGTH_LONG).show();
                    }

                } else {

                    Toast.makeText(getBaseContext(), "Please fill the quantity ", Toast.LENGTH_LONG).show();

                }
            }

        });


    }


    public class MyAdaptor extends BaseAdapter {

        private LayoutInflater mInflater;
        private Context mcontext;
        private ArrayList<TOTBean> list;

        public MyAdaptor(Activity activity, ArrayList<TOTBean> list1) {

            mInflater = LayoutInflater.from(getBaseContext());
            mcontext = activity;
            list = list1;
        }

        @Override
        public int getCount() {

            return list.size();
        }

        @Override
        public Object getItem(int position) {

            return position;
        }

        @Override
        public long getItemId(int position) {

            return position;
        }

        class ViewHolder {
            TextView brand, qty_bought, display;
            Button save, delete;

        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {

            final ViewHolder holder;

            if (convertView == null) {

                convertView = mInflater
                        .inflate(R.layout.tot_stock_list, null);
                holder = new ViewHolder();

                holder.brand = (TextView) convertView.findViewById(R.id.brand_name);

                holder.display = (TextView) convertView.findViewById(R.id.display_name);
                holder.qty_bought = (TextView) convertView.findViewById(R.id.qty_bought);


                holder.delete = (Button) convertView.findViewById(R.id.delete_btn);

                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            holder.delete.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {

                    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                            TOTstock_Activity.this);

                    // set title
                    alertDialogBuilder.setTitle("Do You Want To Delete?");

                    // set dialog message
                    alertDialogBuilder
                            .setMessage("Click Yes To Delete!")
                            .setCancelable(false)
                            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    // if this button is clicked, close
                                    // current activity

                                    db.deleteTOTStockEntry(list.get(position).getKEY_ID());

                                    adapterData.notifyDataSetChanged();

                                    list = db.getTOTStockEntryDetail(store_id, category_id, process_id, display_id, unique_id);
                                    listview.setAdapter(new MyAdaptor(TOTstock_Activity.this, list));
                                    listview.invalidateViews();

                                }
                            })
                            .setNegativeButton("No", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    // if this button is clicked, just close
                                    // the dialog box and do nothing
                                    dialog.cancel();
                                }
                            });

                    // create alert dialog
                    AlertDialog alertDialog = alertDialogBuilder.create();

                    // show it
                    alertDialog.show();

                }
            });


            holder.brand.setText(list.get(position).getBrand()
                    .toString());
            holder.display.setText(list.get(position).getSku_name().toString());

            holder.qty_bought.setText(list.get(position).getQuantity());


            return convertView;
        }
    }


    @Override
    public void onBackPressed() {

        super.onBackPressed();

        Intent in = new Intent(TOTstock_Activity.this, AfterTOT.class);
        startActivity(in);
        TOTstock_Activity.this.finish();
    }


    public boolean validatedata() {
        boolean result = false;
        if (brand_name != null && !brand_id.equalsIgnoreCase("") && sku_name != null && !sku_id.equalsIgnoreCase("")) {
            result = true;
        }

        return result;

    }


    @Override
    public void onItemSelected(AdapterView<?> arg0, View arg1, int position,
                               long arg3) {
        switch (arg0.getId()) {

            case R.id.brand_name:

                if (position != 0 && brand_list.size() > 0) {
                    brand_name = brand_list.get(position - 1).getBrand();

                    brand_id = brand_list.get(position - 1).getBrand_id();

//					sku_list = db.getSKUList(category_id, brand_id);

                    if (sku_list.size() > 0) {
                        skuAdaptor.clear();
                        for (int i = 0; i < sku_list.size(); i++) {
                            skuAdaptor.add(sku_list.get(i).getSku_name());

                        }
                        skuAdaptor.notifyDataSetChanged();
                        sku.setAdapter(skuAdaptor);

                        sku.setOnItemSelectedListener(new OnItemSelectedListener() {

                            @Override
                            public void onItemSelected(AdapterView<?> parent,
                                                       View view, int position, long id) {
                                sku_id = sku_list.get(position).getSku_id();
                                sku_name = sku_list.get(position).getSku_name();


                            }

                            @Override
                            public void onNothingSelected(AdapterView<?> parent) {


                            }
                        });

                    } else {
                        skuAdaptor.clear();
                        sku_id = "";


                    }

                } else {
                    brand_name = "";
                    brand_id = "";
                    sku_name = "";


                }


                break;
        }
    }


    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        // TODO Auto-generated method stub

    }

}


	

