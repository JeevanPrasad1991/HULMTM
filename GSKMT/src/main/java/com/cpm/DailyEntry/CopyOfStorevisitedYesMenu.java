package com.cpm.DailyEntry;


import java.util.ArrayList;

import com.cpm.Constants.CommonString;
import com.cpm.database.GSKMTDatabase;
import com.cpm.delegates.PromotionBean;
import com.cpm.delegates.SkuBean;
import com.cpm.delegates.TOTBean;
import com.cpm.xmlGetterSetter.SKUGetterSetter;
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

public class CopyOfStorevisitedYesMenu extends Activity {
    private SharedPreferences preferences;
    private SharedPreferences.Editor editor = null;
    GSKMTDatabase db;
    String date, process_id, store_id, state_id, key_id, class_id, COMPETITION_PROMOTION_flag;
    ArrayList<SkuBean> category_list;
    ListView lv;
    GridView gv;
    ArrayList<SkuBean> afterStockData = new ArrayList<SkuBean>();
    ArrayList<SkuBean> salesData = new ArrayList<SkuBean>();
    ArrayList<TOTBean> aftertotData = new ArrayList<TOTBean>();
    ArrayList<SkuBean> additionaldata = new ArrayList<SkuBean>();
    ArrayList<TOTBean> totMappingdata = new ArrayList<TOTBean>();
    ArrayList<PromotionBean> promotionlist = new ArrayList<PromotionBean>();
    ArrayList<SkuBean> comptitionTrackingList = new ArrayList<SkuBean>();
    ArrayList<PromotionBean> mappingPromotion = new ArrayList<PromotionBean>();
    public static ArrayList<TOTBean> TOTdata = new ArrayList<TOTBean>();
    public static ArrayList<TOTBean> TOTInsertdata = new ArrayList<TOTBean>();
    boolean cat_HFD = false, cat_wellness = false, cat_oral = false, flagOUTS = false;
    boolean before_tot = true, after_tot = true;
    boolean flagpromo = true;
    boolean flagTOT = true;
    boolean competition_promoFLAG = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.category_name);
        gv = (GridView) findViewById(R.id.gridView1);
        preferences = PreferenceManager.getDefaultSharedPreferences(this);
        db = new GSKMTDatabase(CopyOfStorevisitedYesMenu.this);
        db.open();
        date = preferences.getString(CommonString.KEY_DATE, null);
        process_id = preferences.getString(CommonString.KEY_PROCESS_ID, null);
        store_id = preferences.getString(CommonString.KEY_STORE_ID, null);
        ///change by jeevan RAna
        COMPETITION_PROMOTION_flag = preferences.getString(CommonString.KEY_COMPETITION_PROMOTION, null);
        state_id = preferences.getString(CommonString.KEY_STATE_ID, null);
        key_id = preferences.getString(CommonString.KEY_ID, null);
        class_id = preferences.getString(CommonString.KEY_CLASS_ID, null);
        category_list = db.getCategoryList(process_id, key_id, state_id, class_id);
        key_id = preferences.getString(CommonString.KEY_ID, null);


        for (int i = 0; i < category_list.size(); i++) {
            ///comptiitionTacking
            comptitionTrackingList = db.getEnteredCompetitionDetail(store_id, category_list.get(i).getCategory_id(), process_id);
            afterStockData = db.getAfterStockData(store_id, category_list.get(i).getCategory_id(), process_id);
            additionaldata = db.getProductEntryDetail(store_id, category_list.get(i).getCategory_id(), process_id);
            mappingPromotion = db.getPromoComplianceData(key_id, process_id, category_list.get(i).getCategory_id());


///for cometition promotion flagwise
            if (category_list.get(i).getCategory_id().equals("1") || category_list.get(i).getCategory_id().equals("3")) {
                if (COMPETITION_PROMOTION_flag != null && COMPETITION_PROMOTION_flag.equalsIgnoreCase("Y")) {
                    if (db.getcomptitiondataforpromotion(category_list.get(i).getCategory_id()).size() > 0) {
                        if (db.getcompetitionPromotionfromDatabase(store_id, category_list.get(i).getCategory_id(), process_id).size() > 0) {
                            competition_promoFLAG = true;
                        } else {
                            competition_promoFLAG = false;
                        }
                    } else {
                        competition_promoFLAG = true;
                    }
                } else {
                    competition_promoFLAG = true;
                }
            } else {
                competition_promoFLAG = true;
            }


            ///for promotion condition
            if (mappingPromotion.size() > 0) {
                promotionlist = db.getInsertedPromoCompliance(store_id, category_list.get(i).getCategory_id(), process_id);
                if (promotionlist.size() > 0) {
                    flagpromo = true;
                } else {
                    flagpromo = false;
                }
            } else {
                flagpromo = true;
            }


            //fot tot condition
            TOTdata = db.getTOTData(store_id, process_id, category_list.get(i).getCategory_id());
            if (TOTdata.size() > 0) {
                TOTInsertdata = db.getInsertedAfterTOTData(store_id, category_list.get(i).getCategory_id(), process_id);
                if (TOTInsertdata.size() > 0) {
                    flagTOT = true;
                } else {
                    flagTOT = false;
                }
            } else {
                flagTOT = true;
            }


            totMappingdata = db.getTOTData(store_id, process_id, category_list.get(i).getCategory_id());
            salesData = db.getSalesStockData(store_id, category_list.get(i).getCategory_id(), process_id);
            if (totMappingdata.size() > 0) {
                aftertotData = db.getAfterTOTData(store_id, category_list.get(i).getCategory_id(), process_id);
                if (aftertotData.size() > 0) {
                    before_tot = true;
                    after_tot = true;
                }
            } else {
                before_tot = true;
                after_tot = true;
            }

            if (process_id.equals("2")) {
                if (totMappingdata.size() > 0) {
                    if (afterStockData.size() > 0 && additionaldata.size() > 0 && flagpromo && flagTOT && comptitionTrackingList.size() > 0 && competition_promoFLAG) {
                        if (category_list.get(i).getCategory_id().equals("1"))
                            cat_HFD = true;
                        if (category_list.get(i).getCategory_id().equals("2"))
                            cat_wellness = true;
                        if (category_list.get(i).getCategory_id().equals("3"))
                            cat_oral = true;
                        if (category_list.get(i).getCategory_id().equals("6"))
                            flagOUTS = true;
                    } else {
                        if (category_list.get(i).getCategory_id().equals("1"))
                            cat_HFD = false;
                        if (category_list.get(i).getCategory_id().equals("2"))
                            cat_wellness = false;
                        if (category_list.get(i).getCategory_id().equals("3"))
                            cat_oral = false;
                        if (category_list.get(i).getCategory_id().equals("6"))
                            flagOUTS = false;
                    }
                } else {

                    if (afterStockData.size() > 0 && additionaldata.size() > 0 && flagpromo && flagTOT && comptitionTrackingList.size() > 0 && competition_promoFLAG) {
                        if (category_list.get(i).getCategory_id().equals("1"))
                            cat_HFD = true;
                        if (category_list.get(i).getCategory_id().equals("2"))
                            cat_wellness = true;
                        if (category_list.get(i).getCategory_id().equals("3"))
                            cat_oral = true;
                        if (category_list.get(i).getCategory_id().equals("6"))
                            flagOUTS = true;
                    } else {
                        if (category_list.get(i).getCategory_id().equals("1"))
                            cat_HFD = false;
                        if (category_list.get(i).getCategory_id().equals("2"))
                            cat_wellness = false;
                        if (category_list.get(i).getCategory_id().equals("3"))
                            cat_oral = false;
                        if (category_list.get(i).getCategory_id().equals("6"))
                            flagOUTS = false;
                    }

                }

            } else {
                if (totMappingdata.size() > 0) {
                    if (afterStockData.size() > 0 && additionaldata.size() > 0 && flagpromo && flagTOT && comptitionTrackingList.size() > 0 && competition_promoFLAG) {
                        if (category_list.get(i).getCategory_id().equals("1"))
                            cat_HFD = true;
                        if (category_list.get(i).getCategory_id().equals("2"))
                            cat_wellness = true;
                        if (category_list.get(i).getCategory_id().equals("3"))
                            cat_oral = true;
                        if (category_list.get(i).getCategory_id().equals("6"))
                            flagOUTS = true;
                    }
                } else {
                    if (afterStockData.size() > 0 && additionaldata.size() > 0 && flagpromo && flagTOT && comptitionTrackingList.size() > 0 && competition_promoFLAG) {
                        if (category_list.get(i).getCategory_id().equals("1"))
                            cat_HFD = true;
                        if (category_list.get(i).getCategory_id().equals("2"))
                            cat_wellness = true;
                        if (category_list.get(i).getCategory_id().equals("3"))
                            cat_oral = true;
                        if (category_list.get(i).getCategory_id().equals("6"))
                            flagOUTS = true;
                    }
                }
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
        Intent in = new Intent(CopyOfStorevisitedYesMenu.this, CopyOfStorelistActivity.class);
        startActivity(in);
        CopyOfStorevisitedYesMenu.this.finish();

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

            if (category_list.get(position).getCategory_id().equals("2")) {
                if (cat_wellness) {
                    holder.img.setImageResource(R.drawable.wellness_tick);
                } else {
                    holder.img.setImageResource(R.drawable.wellness_ico);
                }
            }


            if (category_list.get(position).getCategory().equalsIgnoreCase("HFD")) {
                if (cat_HFD) {
                    holder.img.setImageResource(R.drawable.hfd_tick);
                } else {
                    holder.img.setImageResource(R.drawable.hfd_ico);
                }
            }


            if (category_list.get(position).getCategory_id().equals("6") || category_list.get(position).getCategory().equalsIgnoreCase("Oats")) {
                if (flagOUTS) {
                    holder.img.setImageResource(R.drawable.oats_done);
                } else {
                    holder.img.setImageResource(R.drawable.oats);
                }
            }

            if (category_list.get(position).getCategory_id().equals("3") ||
                    category_list.get(position).getCategory().equalsIgnoreCase("Oral Care")) {
                if (cat_oral) {
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
                    if (process_id.equals("3")) {
                        Intent in = new Intent(getBaseContext(), CategoryWisePerformance.class);
                        startActivity(in);
                        CopyOfStorevisitedYesMenu.this.finish();
                    } else {
                        Intent in = new Intent(getBaseContext(), DailyEntryMainMenu.class);
                        startActivity(in);
                        CopyOfStorevisitedYesMenu.this.finish();
                    }
                }
            });

            return convertView;
        }

    }

}
