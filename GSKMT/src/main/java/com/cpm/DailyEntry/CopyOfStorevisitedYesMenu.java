package com.cpm.DailyEntry;


import java.util.ArrayList;

import com.cpm.Constants.CommonString;
import com.cpm.database.GSKMTDatabase;
import com.cpm.delegates.PromotionBean;
import com.cpm.delegates.SkuBean;
import com.cpm.delegates.TOTBean;
import com.cpm.xmlGetterSetter.MAPPING_ALLSKU_ASSORTMENT;
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
    String date, process_id, store_id, state_id, key_id, class_id, COMPETITION_PROMOTION_flag, saleenable_flag = "", region_id, store_type_id;
    ArrayList<SkuBean> category_list;
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
    boolean competition_promoFLAG = false, sales_flag = false;
    String sku_listed = "";

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
        region_id = preferences.getString(CommonString.region_id, null);
        store_type_id = preferences.getString(CommonString.storetype_id, null);
        key_id = preferences.getString(CommonString.KEY_ID, null);
        COMPETITION_PROMOTION_flag = preferences.getString(CommonString.KEY_COMPETITION_PROMOTION, null);
        saleenable_flag = preferences.getString(CommonString.KEY_SALEENABLE_FLAG, "");
        state_id = preferences.getString(CommonString.KEY_STATE_ID, null);
        key_id = preferences.getString(CommonString.KEY_ID, null);
        class_id = preferences.getString(CommonString.KEY_CLASS_ID, null);
        key_id = preferences.getString(CommonString.KEY_ID, null);
        sku_listed = preferences.getString(CommonString.KEY_SKU_LISTED, "");
        db.open();
        category_list = db.getCategoryList(process_id, store_id);
        if (category_list.size() > 0) {
            SkuBean sb = new SkuBean();
            sb.setCategory("SKU Listed");
            sb.setCategory_id("-1");
            sb.setShowsalesflag("");
            category_list.add(sb);
        }

        call_category();
        if (category_list.size() > 0) {
            System.out.println("" + category_list.size());
            gv.setAdapter(new MyAdaptor());
        }
    }

    protected void call_category() {
        if (category_list.size() > 0) {
            for (int i = 0; i < category_list.size(); i++) {
                if (!category_list.get(i).getCategory_id().equalsIgnoreCase("-1") && !category_list.get(i).getCategory().equalsIgnoreCase("Sku Listed")) {
                    ///comptiitionTacking
                    db.open();
                    comptitionTrackingList = db.getEnteredCompetitionDetail(store_id, category_list.get(i).getCategory_id(), process_id);
                    db.open();
                    afterStockData = db.getAfterStockData(store_id, category_list.get(i).getCategory_id(), process_id);
                    db.open();
                    additionaldata = db.getProductEntryDetail(store_id, category_list.get(i).getCategory_id(), process_id);
                    db.open();
                    mappingPromotion = db.getPromoComplianceData(key_id, process_id, category_list.get(i).getCategory_id());
                    db.open();
                    salesData = db.getSalesStockData(store_id, category_list.get(i).getCategory_id(), process_id);
                    if (saleenable_flag != null && !saleenable_flag.equals("") && saleenable_flag.equals("1") && category_list.get(i).getShowsalesflag() != null
                            && category_list.get(i).getShowsalesflag().equals("1") && category_list.get(i).getShowsalesflag() != null
                            && category_list.get(i).getShowsalesflag().equals("1")) {
                        if (db.getBrandSkuListForSales(category_list.get(i).getCategory_id(), store_id, process_id).size() > 0) {
                            if (salesData.size() > 0) {
                                sales_flag = true;
                            } else {
                                sales_flag = false;
                            }
                        } else {
                            sales_flag = true;
                        }
                    } else {
                        sales_flag = true;
                    }

///for cometition promotion flagwise
                    if (category_list.get(i).getCategory_id().equals("1") || category_list.get(i).getCategory_id().equals("3")) {
                        if (COMPETITION_PROMOTION_flag != null && COMPETITION_PROMOTION_flag.equalsIgnoreCase("Y")) {
                            db.open();
                            if (db.getcomptitiondataforpromotion(category_list.get(i).getCategory_id()).size() > 0) {
                                db.open();
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
                        db.open();
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
                    db.open();
                    TOTdata = db.getTOTData(store_id, process_id, category_list.get(i).getCategory_id());
                    if (TOTdata.size() > 0) {
                        db.open();
                        TOTInsertdata = db.getInsertedAfterTOTData(store_id, category_list.get(i).getCategory_id(), process_id);
                        if (TOTInsertdata.size() > 0) {
                            flagTOT = true;
                        } else {
                            flagTOT = false;
                        }
                    } else {
                        flagTOT = true;
                    }

                    db.open();
                    totMappingdata = db.getTOTData(store_id, process_id, category_list.get(i).getCategory_id());
                    if (totMappingdata.size() > 0) {
                        db.open();
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
                            if (afterStockData.size() > 0 && additionaldata.size() > 0 && flagpromo && flagTOT && comptitionTrackingList.size() > 0 && competition_promoFLAG && sales_flag) {
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
                            if (afterStockData.size() > 0 && additionaldata.size() > 0 && flagpromo && flagTOT && comptitionTrackingList.size() > 0 && competition_promoFLAG && sales_flag) {
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
                            if (afterStockData.size() > 0 && additionaldata.size() > 0 && flagpromo && flagTOT && comptitionTrackingList.size() > 0
                                    && competition_promoFLAG && sales_flag) {
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
                            if (afterStockData.size() > 0 && additionaldata.size() > 0 && flagpromo && flagTOT && comptitionTrackingList.size() > 0
                                    && competition_promoFLAG && sales_flag) {
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

            }
            ////
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
            holder.CategoryName.setVisibility(View.GONE);
            holder.CategoryName.setId(position);

            if (category_list.get(position).getCategory_id().equals("-1")) {
                db.open();
                if (sku_listed != null && !sku_listed.equals("") && sku_listed.equals("1") && db.getSkuListed().size() > 0) {
                    ///for SKU Listed
                    db.open();
                    if (db.getinsertedlistedSKU(store_id, process_id).size() > 0) {
                        holder.img.setImageResource(R.drawable.sku_listed_tick);
                        holder.img.setId(position);
                    } else {
                        holder.img.setImageResource(R.drawable.sku_listed);
                        holder.img.setId(position);
                    }
                } else {
                    holder.img.setImageResource(R.drawable.sku_listed_gray);
                    holder.img.setId(position);
                }
            } else if (category_list.get(position).getCategory_id().equals("2")) {
                if (cat_wellness && checkstockinward(category_list.get(position).getCategory_id())
                        && check_filled_customer_conversion(category_list.get(position).getCategory_id())) {
                    holder.img.setImageResource(R.drawable.wellness_tick);
                    holder.img.setId(position);
                } else {
                    holder.img.setImageResource(R.drawable.wellness_ico);
                    holder.img.setId(position);
                }
            } else if (category_list.get(position).getCategory().equalsIgnoreCase("HFD")) {
                if (cat_HFD && checkcatwise_sosfacing(category_list.get(position).getCategory_id())
                        && checkstockinward(category_list.get(position).getCategory_id())
                        && check_filled_customer_conversion(category_list.get(position).getCategory_id())) {
                    holder.img.setImageResource(R.drawable.hfd_tick);
                    holder.img.setId(position);
                } else {
                    holder.img.setImageResource(R.drawable.hfd_ico);
                    holder.img.setId(position);
                }
            } else if (category_list.get(position).getCategory_id().equals("6") || category_list.get(position).getCategory().equalsIgnoreCase("Oats")) {
                if (flagOUTS && checkcatwise_sosfacing(category_list.get(position).getCategory_id())
                        && checkstockinward(category_list.get(position).getCategory_id())
                        && check_filled_customer_conversion(category_list.get(position).getCategory_id())) {
                    holder.img.setImageResource(R.drawable.oats_done);
                    holder.img.setId(position);
                } else {
                    holder.img.setImageResource(R.drawable.oats);
                    holder.img.setId(position);
                }
            } else if (category_list.get(position).getCategory_id().equals("3") || category_list.get(position).getCategory().equalsIgnoreCase("Oral Care")) {
                if (cat_oral && checkcatwise_sosfacing(category_list.get(position).getCategory_id())
                        && checkstockinward(category_list.get(position).getCategory_id())
                        && check_filled_customer_conversion(category_list.get(position).getCategory_id())) {
                    holder.img.setImageResource(R.drawable.ohc_tick);
                    holder.img.setId(position);
                } else {
                    holder.img.setImageResource(R.drawable.ohc_ico);
                    holder.img.setId(position);
                }
            }


            convertView.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!category_list.get(position).getCategory_id().equals("-1")) {
                        editor = preferences.edit();
                        editor.putString(CommonString.KEY_CATEGORY_ID, category_list.get(position).getCategory_id());
                        editor.putString(CommonString.KEY_CATEGORY_NAME, category_list.get(position).getCategory());
                        editor.putString(CommonString.KEY_CATEGORY_WISESALE_FLAG, category_list.get(position).getShowsalesflag());
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
                    } else if (category_list.get(position).getCategory_id().equals("-1") &&
                            category_list.get(position).getCategory().equalsIgnoreCase("Sku Listed") &&
                            sku_listed != null && !sku_listed.equals("") &&
                            sku_listed.equals("1") && db.getSkuListed().size() > 0) {
                        Intent in = new Intent(getBaseContext(), SkuListedActivity.class);
                        startActivity(in);
                        CopyOfStorevisitedYesMenu.this.finish();
                    }
                }
            });

            return convertView;
        }

    }

    private boolean checkcatwise_sosfacing(String category_id) {
        boolean flag = true;
        try {
            if (category_id != null && !category_id.equals("2") && db.getsubcategorySOS(store_id, process_id, region_id, store_type_id, key_id, category_id).size() > 0) {
                db.open();
                if (db.getsos_facing(store_id, category_id, process_id).size() > 0) {
                    flag = true;
                } else {
                    flag = false;
                }
            } else {
                flag = true;
            }


        } catch (Exception e) {
            e.printStackTrace();
        }
        return flag;
    }


    private boolean checkstockinward(String category_id) {
        boolean flag = true;
        try {
            db.open();
            if (db.getBrandSkuListforstockin(category_id, store_id, process_id).size() > 0) {
                db.open();
                if (db.getstockininserteddata(store_id, category_id, process_id).size() > 0) {
                    flag = true;
                } else {
                    flag = false;
                }
            } else {
                flag = true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return flag;
    }

    private boolean check_filled_customer_conversion(String categorYId) {
        boolean flag = true;
        try {
            //For Customer Connect-----&*
            if (!process_id.equals("3")) {
                db.open();
                MAPPING_ALLSKU_ASSORTMENT footfallobject = db.getinserted_storeFootfall(store_id, categorYId, process_id);
                if (footfallobject != null && footfallobject.getDaily_storeFootfall() != null && !footfallobject.getDaily_storeFootfall().equals("")) {
                    flag = true;
                } else {
                    flag = false;
                }
            } else {
                flag = true;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return flag;
    }

}
