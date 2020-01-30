package com.cpm.DailyEntry;

import java.util.ArrayList;

import com.cpm.Constants.CommonString;

import com.cpm.database.GSKMTDatabase;
import com.cpm.delegates.PerformanceBean;
import com.example.gsk_mtt.R;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceManager;

import android.support.design.widget.FloatingActionButton;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class StoreWisePerformance extends Activity {
    private SharedPreferences preferences;
    String date, store_id, store_intime, process_id;
    GSKMTDatabase database;
    ListView lv;
    ArrayList<PerformanceBean> per_list;
    FloatingActionButton ok;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.store_performance);
        lv = (ListView) findViewById(R.id.listviewperformance);
        ok = (FloatingActionButton) findViewById(R.id.ok);
        preferences = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        date = preferences.getString(CommonString.KEY_DATE, null);
        store_id = preferences.getString(CommonString.KEY_STORE_ID, null);
        store_intime = preferences.getString(CommonString.KEY_STORE_IN_TIME, "");
        process_id = preferences.getString(CommonString.KEY_PROCESS_ID, "");
        database = new GSKMTDatabase(StoreWisePerformance.this);
        database.open();
        per_list = new ArrayList<>();
        per_list = database.getStoreWisePerformanceData(store_id, process_id);
        if (per_list.size() > 0) {
            lv.setAdapter(new MyAdaptor());
        }

        ok.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent in = new Intent(StoreWisePerformance.this, StoreVisitedActivity.class);
                startActivity(in);
                StoreWisePerformance.this.finish();

            }
        });

    }


    private class ViewHolder {
        TextView period, sos_score, stock_score, asset_stock, promo_score, pss;
    }

    private class MyAdaptor extends BaseAdapter {
        public MyAdaptor() {
            super();
            // TODO Auto-generated constructor stub
        }

        @Override
        public int getCount() {
            // TODO Auto-generated method stub
            return per_list.size();
        }

        @Override
        public Object getItem(int position) {
            // TODO Auto-generated method stub
            return position;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder = null;
            if (convertView == null) {
                holder = new ViewHolder();
                LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = inflater.inflate(R.layout.item_storewiseperformace_list, null);
                holder.period = (TextView) convertView.findViewById(R.id.period);
                holder.sos_score = (TextView) convertView.findViewById(R.id.sos_score);
                holder.stock_score = (TextView) convertView.findViewById(R.id.stock_score);
                holder.asset_stock = (TextView) convertView.findViewById(R.id.asset_stock);
                holder.promo_score = (TextView) convertView.findViewById(R.id.promo_score);
                holder.pss = (TextView) convertView.findViewById(R.id.pss);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            holder.period.setText(per_list.get(position).getPeriod());

            if (per_list.get(position).getSOS_SCORE() == null) {
                holder.sos_score.setText("0.0");
            } else {
                holder.sos_score.setText(per_list.get(position).getSOS_SCORE());
            }


            if (per_list.get(position).getSTOCK_SCORE() == null) {
                holder.stock_score.setText("0.0");
            } else {
                holder.stock_score.setText(per_list.get(position).getSTOCK_SCORE());
            }

            if (per_list.get(position).getASSET_SCORE() == null) {
                holder.asset_stock.setText("0.0");
            } else {
                holder.asset_stock.setText(per_list.get(position).getASSET_SCORE());
            }

            if (per_list.get(position).getPROMO_SCORE() == null) {
                holder.promo_score.setText("0.0");
            } else {
                holder.promo_score.setText(per_list.get(position).getPROMO_SCORE());
            }


            if (per_list.get(position).getPss_avg() == null) {
                holder.pss.setText("0.0");
            } else {
                holder.pss.setText(per_list.get(position).getPss_avg());
            }

            int value1 = ragAnalysis(per_list.get(position).getPss_avg());
            holder.pss.setBackgroundColor(value1);

            return convertView;
        }

    }


    private int ragAnalysis(String string) {
        int color;
        int c = (int) Math.round(Double.parseDouble(string));
        if (c > 70) {
            color = Color.GREEN;
        } else if (c > 39 && c < 71) {
            color = Color.parseColor("#FFBF00");
        } else {
            color = Color.RED;
        }

        return color;

    }

}
