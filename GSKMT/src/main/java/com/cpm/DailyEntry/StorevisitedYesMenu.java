package com.cpm.DailyEntry;


import java.util.ArrayList;

import com.cpm.Constants.CommonString;
import com.cpm.database.GSKMTDatabase;
import com.cpm.delegates.PromotionBean;
import com.cpm.delegates.SkuBean;
import com.cpm.delegates.TOTBean;
import com.example.gsk_mtt.R;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

public class StorevisitedYesMenu extends Activity {
    private SharedPreferences preferences;
    private SharedPreferences.Editor editor = null;
    GSKMTDatabase db;
    String date, process_id, store_id, state_id, key_id, class_id;
    ArrayList<SkuBean> category_list;
    ListView lv;
    GridView gv;
    ArrayList<SkuBean> beforeStockDataHFD = new ArrayList<SkuBean>();
    ArrayList<SkuBean> beforeStockDataWellness = new ArrayList<SkuBean>();
    ArrayList<SkuBean> beforeStockDataOralcare = new ArrayList<SkuBean>();
    ArrayList<SkuBean> afterStockDataHFD = new ArrayList<SkuBean>();
    ArrayList<SkuBean> afterStockDataWellness = new ArrayList<SkuBean>();
    ArrayList<SkuBean> afterStockDataOral = new ArrayList<SkuBean>();
    ArrayList<TOTBean> afterTOTDataHFD = new ArrayList<TOTBean>();
    ArrayList<TOTBean> afterTOTDataWellness = new ArrayList<TOTBean>();
    ArrayList<TOTBean> afterTOTDataOral = new ArrayList<TOTBean>();
    ArrayList<SkuBean> beforeaddtionalDataHFD = new ArrayList<SkuBean>();
    ArrayList<SkuBean> beforeaddtionalDataWellness = new ArrayList<SkuBean>();
    ArrayList<SkuBean> beforeaddtionalDataOral = new ArrayList<SkuBean>();
    ArrayList<PromotionBean> mappingPromotion3 = new ArrayList<PromotionBean>();
    ArrayList<PromotionBean> mappingPromotion1 = new ArrayList<PromotionBean>();
    ArrayList<PromotionBean> mappingPromotion2 = new ArrayList<PromotionBean>();
    ArrayList<TOTBean> totMappingDataHFD = new ArrayList<TOTBean>();
    ArrayList<TOTBean> totMappingDataWellness = new ArrayList<TOTBean>();
    ArrayList<TOTBean> totMappingDataOral = new ArrayList<TOTBean>();
    boolean before_stock = false, after_stock = false, before_totHFD = false, before_totWellness = false,
            before_totOral = false, after_totHFD = false, after_totWellness = false, after_totOral = false,
            before_addtional = true, cat_HFD = false, cat_wellness = false, cat_oral = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.category_name);
        gv = (GridView) findViewById(R.id.gridView1);
        preferences = PreferenceManager.getDefaultSharedPreferences(this);
        db = new GSKMTDatabase(StorevisitedYesMenu.this);
        db.open();
        date = preferences.getString(CommonString.KEY_DATE, null);
        process_id = preferences.getString(CommonString.KEY_PROCESS_ID, null);
        store_id = preferences.getString(CommonString.KEY_STORE_ID, null);
        ///change by jeevan RAna
        state_id = preferences.getString(CommonString.KEY_STATE_ID, null);
        key_id = preferences.getString(CommonString.KEY_ID, null);
        class_id = preferences.getString(CommonString.KEY_CLASS_ID, null);
        category_list = db.getCategoryList(process_id, key_id, state_id, class_id);
        beforeStockDataHFD = db.getBeforeStockData(store_id, "1", process_id);
        afterStockDataHFD = db.getAfterStockData(store_id, "1", process_id);
        beforeaddtionalDataHFD = db.getProductEntryDetail(store_id, "1", process_id);
        mappingPromotion1 = db.getInsertedPromoCompliance(store_id, "1", process_id);
        mappingPromotion2 = db.getInsertedPromoCompliance(store_id, "2", process_id);
        mappingPromotion3 = db.getInsertedPromoCompliance(store_id, "3", process_id);
        totMappingDataHFD = db.getTOTData(store_id, process_id, "1");
        if (totMappingDataHFD.size() > 0) {
            afterTOTDataHFD = db.getAfterTOTData(store_id, "1", process_id);
            if (afterTOTDataHFD.size() > 0) {
                before_totHFD = true;
                after_totHFD = true;
            }
        } else {
            before_totHFD = true;
            after_totHFD = true;
        }

        if (totMappingDataHFD.size() != 0) {
            if (beforeStockDataHFD.size() > 0 && afterStockDataHFD.size() > 0 && afterTOTDataHFD.size() > 0 &&
                    beforeaddtionalDataHFD.size() > 0 && mappingPromotion1.size() > 0) {

                cat_HFD = true;

            }
        } else {

            if (beforeStockDataHFD.size() > 0 && afterStockDataHFD.size() > 0 &&
                    beforeaddtionalDataHFD.size() > 0 && mappingPromotion1.size() > 0) {
                cat_HFD = true;
            }

        }


        beforeStockDataWellness = db.getBeforeStockData(store_id, "2", process_id);
        afterStockDataWellness = db.getAfterStockData(store_id, "2", process_id);
        beforeaddtionalDataWellness = db.getProductEntryDetail(store_id, "2", process_id);


        totMappingDataWellness = db.getTOTData(store_id, process_id, "2");

        if (totMappingDataWellness.size() > 0) {
            afterTOTDataWellness = db.getAfterTOTData(store_id, "2", process_id);

            if (afterTOTDataWellness.size() > 0) {
                before_totWellness = true;
                after_totWellness = true;
            }
        } else {
            before_totWellness = true;
            after_totWellness = true;
        }

        if (totMappingDataWellness.size() != 0) {
            if (beforeStockDataWellness.size() > 0 && afterStockDataWellness.size() > 0 &&
                    afterTOTDataWellness.size() > 0 &&
                    beforeaddtionalDataWellness.size() > 0 && mappingPromotion2.size() > 0) {

                cat_wellness = true;

            }
        } else {

            if (beforeStockDataWellness.size() > 0 && afterStockDataWellness.size() > 0 &&
                    beforeaddtionalDataWellness.size() > 0 && mappingPromotion2.size() > 0) {
                cat_wellness = true;
            }


        }


        beforeStockDataOralcare = db.getBeforeStockData(store_id, "3", process_id);
        afterStockDataOral = db.getAfterStockData(store_id, "3", process_id);
        beforeaddtionalDataOral = db.getProductEntryDetail(store_id, "3", process_id);
        totMappingDataOral = db.getTOTData(store_id, process_id, "3");
        if (totMappingDataOral.size() > 0) {
            afterTOTDataOral = db.getAfterTOTData(store_id, "3", process_id);
            if (afterTOTDataOral.size() > 0) {
                before_totOral = true;
                after_totOral = true;
            }
        } else {
            before_totOral = true;
            after_totOral = true;
        }

        if (totMappingDataOral.size() != 0) {
            if (beforeStockDataOralcare.size() > 0 && afterStockDataOral.size() > 0 && afterTOTDataOral.size() > 0 &&
                    beforeaddtionalDataOral.size() > 0 && mappingPromotion3.size() > 0) {
                cat_oral = true;
            }
        } else {
            if (beforeStockDataOralcare.size() > 0 && afterStockDataOral.size() > 0 && beforeaddtionalDataOral.size() > 0 && mappingPromotion3.size() > 0) {
                cat_oral = true;
            }
        }
        if (category_list.size() > 0) {
            System.out.println("" + category_list.size());
            gv.setAdapter(new MyAdaptor());
        }
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent in = new Intent(StorevisitedYesMenu.this, CopyOfStorelistActivity.class);
        startActivity(in);
        StorevisitedYesMenu.this.finish();
    }

    private class ViewHolder {
        TextView CategoryName;
        ImageView img;


    }

    private class MyAdaptor extends BaseAdapter {

        @Override
        public int getCount() {

            return category_list.size();
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
            if (holder == null) {
                holder = new ViewHolder();
                LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = inflater.inflate(R.layout.category_grid_view, null);

                holder.CategoryName = (TextView) convertView.findViewById(R.id.name);

                holder.img = (ImageView) convertView.findViewById(R.id.img);

            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            holder.CategoryName.setText(category_list.get(position).getCategory());
            if (category_list.get(position).getCategory_id().equalsIgnoreCase("2")) {
                if (cat_wellness == true) {
                    holder.img.setImageResource(R.drawable.wellness_tick);
                } else {
                    holder.img.setImageResource(R.drawable.wellness_ico);
                }
            } else if (category_list.get(position).getCategory().equalsIgnoreCase("HFD")) {
                if (cat_HFD == true) {
                    holder.img.setImageResource(R.drawable.hfd_tick);
                } else {
                    holder.img.setImageResource(R.drawable.hfd_ico);
                }
            } else {
                if (cat_oral == true) {
                    holder.img.setImageResource(R.drawable.ohc_tick);
                } else {
                    holder.img.setImageResource(R.drawable.ohc_ico);
                }
            }


            convertView.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {
                    editor = preferences.edit();
                    editor.putString(CommonString.KEY_CATEGORY_ID, category_list.get(position).getCategory_id());
                    editor.putString(CommonString.KEY_CATEGORY_NAME, category_list.get(position).getCategory());
                    editor.commit();
                    Intent in = new Intent(getBaseContext(), DailyEntryMainMenu.class);
                    startActivity(in);
                    StorevisitedYesMenu.this.finish();
                }
            });

            return convertView;
        }

    }

}
