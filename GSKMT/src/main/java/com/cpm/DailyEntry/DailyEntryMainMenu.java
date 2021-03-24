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
import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceManager;

import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class DailyEntryMainMenu extends Activity {
    public Button after_stock, stockinward_btn,
            after_tot, addtional_info_before, comptitionfor_promotion,
            promo_compliance, comp, backroom_stock, sales, subcat_sos_facing;
    private SharedPreferences preferences;
    private SharedPreferences.Editor editor = null;
    String store_id, category_id, date, process_id, key_id, region_id, store_type_id, cat_name, COMPETITION_PROMOTION_flag, saleenable_flag = "", categorywise_salesflag = "";
    TextView category_name;
    ImageView salstrackingsupportimg;
    GSKMTDatabase db;
    ArrayList<SkuBean> beforeAddtionalData, afterStockData, sos_target_list, entered_comp_data, backRoomStockData;
    ArrayList<TOTBean> afterTOTData, totstockdata;
    ArrayList<TOTBean> mappingDataTOTCategoryWise;
    ArrayList<SkuBean> salesData;
    ArrayList<PromotionBean> mappingPromotion, promotionData;
    String sos_after = "", afterStockQuantity = "";
    TextView after_sos;
    public static ArrayList<TOTBean> TOTdata = new ArrayList<TOTBean>();
    Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dailyentry);
        context = this;
        after_stock = (Button) findViewById(R.id.after_stock);
        stockinward_btn = (Button) findViewById(R.id.stockinward_btn);
        subcat_sos_facing = (Button) findViewById(R.id.subcat_sos_facing);
        after_tot = (Button) findViewById(R.id.after_tot);
        addtional_info_before = (Button) findViewById(R.id.before_additional);
        promo_compliance = (Button) findViewById(R.id.promotional_data);
        category_name = (TextView) findViewById(R.id.cat_name);
        comp = (Button) findViewById(R.id.competitionTracking);
        sales = (Button) findViewById(R.id.salesTracking);
        salstrackingsupportimg = (ImageView) findViewById(R.id.salstrackingsupportimg);
        backroom_stock = (Button) findViewById(R.id.backroom_stock);
        after_sos = (TextView) findViewById(R.id.sos_after);
        ///new Add 11/september
        comptitionfor_promotion = (Button) findViewById(R.id.comptitionfor_promotion);
        preferences = PreferenceManager.getDefaultSharedPreferences(context);
        date = preferences.getString(CommonString.KEY_DATE, null);
        store_id = preferences.getString(CommonString.KEY_STORE_ID, null);
        category_id = preferences.getString(CommonString.KEY_CATEGORY_ID, null);
        cat_name = preferences.getString(CommonString.KEY_CATEGORY_NAME, null);
        process_id = preferences.getString(CommonString.KEY_PROCESS_ID, null);
        COMPETITION_PROMOTION_flag = preferences.getString(CommonString.KEY_COMPETITION_PROMOTION, null);
        saleenable_flag = preferences.getString(CommonString.KEY_SALEENABLE_FLAG, "");
        categorywise_salesflag = preferences.getString(CommonString.KEY_CATEGORY_WISESALE_FLAG, "");
        key_id = preferences.getString(CommonString.KEY_ID, null);
        region_id = preferences.getString(CommonString.region_id, null);
        store_type_id = preferences.getString(CommonString.storetype_id, null);
        beforeAddtionalData = new ArrayList<SkuBean>();
        afterTOTData = new ArrayList<TOTBean>();
        totstockdata = new ArrayList<TOTBean>();
        mappingPromotion = new ArrayList<PromotionBean>();
        sos_target_list = new ArrayList<SkuBean>();
        entered_comp_data = new ArrayList<SkuBean>();
        salesData = new ArrayList<SkuBean>();
        mappingDataTOTCategoryWise = new ArrayList<TOTBean>();
        db = new GSKMTDatabase(context);
        db.open();
        category_name.setText(cat_name);
        db.open();
        afterStockData = db.getAfterStockData(store_id, category_id, process_id);
        db.open();
        backRoomStockData = db.getBackRoomStock(store_id, category_id, process_id);
        db.open();
        salesData = db.getSalesStockData(store_id, category_id, process_id);
        if (saleenable_flag != null && !saleenable_flag.equals("") && saleenable_flag.equals("1") && categorywise_salesflag != null
                && !categorywise_salesflag.equals("") && categorywise_salesflag.equals("1")) {
            sales.setVisibility(View.VISIBLE);
            sales.setEnabled(true);
            salstrackingsupportimg.setVisibility(View.VISIBLE);
            salstrackingsupportimg.setEnabled(true);
        } else {
            sales.setVisibility(View.GONE);
            sales.setEnabled(false);
            sales.setBackgroundResource(R.drawable.sales_grey);
            salstrackingsupportimg.setVisibility(View.GONE);
            salstrackingsupportimg.setEnabled(false);
        }

        if (db.getBrandSkuListForSales(category_id, store_id, process_id).size() > 0) {
            if (salesData.size() > 0) {
                sales.setBackgroundResource(R.drawable.sales_tick);
                sales.setEnabled(true);
            } else {
                sales.setBackgroundResource(R.drawable.sales);
                sales.setEnabled(true);
            }
        } else {
            sales.setBackgroundResource(R.drawable.sales_grey);
            sales.setEnabled(false);
        }


        for (int i = 0; i < afterStockData.size(); i++) {
            afterStockQuantity = afterStockData.get(i).getAfter_Stock();
        }
        db.open();
        beforeAddtionalData = db.getProductEntryDetail(store_id, category_id, process_id);
        db.open();
        afterTOTData = db.getAfterTOTData(store_id, category_id, process_id);
        db.open();
        promotionData = db.getInsertedPromoCompliances(store_id, category_id, process_id);
        db.open();
        entered_comp_data = db.getEnteredCompetitionDetail(store_id, category_id, process_id);
        db.open();
        sos_target_list = db.getSOSTarget(store_id, category_id, process_id);

        if (backRoomStockData.size() > 0) {
            backroom_stock.setBackgroundResource(R.drawable.backroom_tick);

        } else {
            backroom_stock.setBackgroundResource(R.drawable.backroom);
        }

        db.open();
        mappingDataTOTCategoryWise = db.getTOTData(store_id, process_id, category_id);
        db.open();
        mappingPromotion = db.getPromoComplianceData(key_id, process_id, category_id);
        if (!afterStockQuantity.equals("")) {
            after_stock.setBackgroundResource(R.drawable.tick_stock_after_ico);
            sos_after = db.getAFTERSOS(store_id, category_id, process_id);
            if (category_id.equals("2")) {
                after_sos.setVisibility(View.VISIBLE);
                if (sos_target_list.size() > 0) {
                    if (sos_after == null) {
                        after_sos.setText("SOS : 0");
                    } else {
                        if (!sos_after.equals("")) {
                            float b = Float.parseFloat(sos_after);
                            int s_after = (int) Math.round(b);
                            if (s_after == 100) {
                                after_sos.setText("SOS: 100");
                                after_sos.setTextColor(Color.GREEN);
                            } else {
                                after_sos.setText("SOS: 0");
                                after_sos.setTextColor(Color.RED);
                            }
                        } else {
                            after_sos.setText("SOS : 0");
                        }
                    }
                } else {
                    after_sos.setText("SOS : 0");
                }
            } else {
                after_sos.setVisibility(View.INVISIBLE);
                /*if (sos_target_list.size() > 0) {
                    if (sos_after == null) {
                        after_sos.setText("SOS : 0");
                    } else {
                        if (!sos_after.equals("")) {
                            float b = Float.parseFloat(sos_after);
                            int s_after = (int) Math.round(b);
                            String s_target = sos_target_list.get(0).getSos_target();
                            int target = Integer.parseInt(s_target);
                            if (s_after < target) {
                                after_sos.setText("SOS: " + s_after);
                                after_sos.setTextColor(Color.RED);
                            } else {
                                after_sos.setText("SOS: " + s_after);
                                after_sos.setTextColor(Color.GREEN);
                            }
                        } else {
                            after_sos.setText("SOS : 0");
                        }
                    }
                } else {
                    after_sos.setText("SOS : 0");
                }*/
            }
        }

        if (!afterStockQuantity.equals("")) {
            db.open();
            TOTdata = db.getTOTData(store_id, process_id, category_id);
            if (TOTdata.size() > 0) {
                after_tot.setEnabled(true);
                after_tot.setBackgroundResource(R.drawable.tot_after_ico);
            } else {
                after_tot.setBackgroundResource(R.drawable.disabled_tot_after_ico);
            }
        }


        if (!afterStockQuantity.equals("") && afterTOTData.size() > 0) {
            after_tot.setBackgroundResource(R.drawable.tick_tot_after_ico);
        }


        if (mappingDataTOTCategoryWise.size() > 0) {
            if (beforeAddtionalData.size() > 0) {
                addtional_info_before.setBackgroundResource(R.drawable.additional_display_tick);
            }
        } else {
            if (beforeAddtionalData.size() > 0) {
                addtional_info_before.setBackgroundResource(R.drawable.additional_display_tick);
            }
        }

        if (mappingPromotion.size() > 0) {
            promo_compliance.setEnabled(true);
            promo_compliance.setBackgroundResource(R.drawable.promo_compliance);
            if (promotionData.size() > 0) {
                promo_compliance.setBackgroundResource(R.drawable.promo_compliance_tick);
            } else {
                promo_compliance.setBackgroundResource(R.drawable.promo_compliance);
            }

        } else {
            promo_compliance.setEnabled(false);
            promo_compliance.setBackgroundResource(R.drawable.disabled_promo_compliance);
        }

        if (entered_comp_data.size() > 0) {
            comp.setBackgroundResource(R.drawable.competition_asset_done);
        } else {
            comp.setBackgroundResource(R.drawable.competition_asset);
        }


        if (category_id != null && category_id.equals("1") || category_id != null && category_id.equals("3")) {
            if (COMPETITION_PROMOTION_flag != null && COMPETITION_PROMOTION_flag.equalsIgnoreCase("Y")) {
                comptitionfor_promotion.setEnabled(true);
                comptitionfor_promotion.setVisibility(View.VISIBLE);
                db.open();
                if (db.getcomptitiondataforpromotion(category_id).size() > 0) {
                    db.open();
                    if (db.getcompetitionPromotionfromDatabase(store_id, category_id, process_id).size() > 0) {
                        comptitionfor_promotion.setBackgroundResource(R.drawable.competition_promotion_done);
                    } else {
                        comptitionfor_promotion.setBackgroundResource(R.drawable.competition_promotion);
                    }
                } else {
                    comptitionfor_promotion.setBackgroundResource(R.drawable.competition_promotion_disable);
                }
            } else {
                comptitionfor_promotion.setEnabled(false);
                comptitionfor_promotion.setBackgroundResource(R.drawable.competition_promotion_disable);
                comptitionfor_promotion.setVisibility(View.VISIBLE);
            }
        } else {
            comptitionfor_promotion.setEnabled(false);
            comptitionfor_promotion.setVisibility(View.GONE);
        }

        if (category_id != null && !category_id.equals("2") && afterStockData.size() > 0 &&
                db.getsubcategorySOS(store_id, process_id, region_id, store_type_id, key_id, category_id).size() > 0) {
            subcat_sos_facing.setEnabled(true);
            subcat_sos_facing.setBackgroundResource(R.drawable.sos);
            ////for stock Inward
            db.open();
            if (db.getsos_facing(store_id, category_id, process_id).size() > 0) {
                subcat_sos_facing.setEnabled(true);
                subcat_sos_facing.setBackgroundResource(R.drawable.sos_tick);
            } else {
                subcat_sos_facing.setEnabled(true);
                subcat_sos_facing.setBackgroundResource(R.drawable.sos);
            }
        } else {
            subcat_sos_facing.setEnabled(false);
            subcat_sos_facing.setBackgroundResource(R.drawable.sos_gray);
        }


        if (afterStockData.size() > 0) {
            stockinward_btn.setEnabled(true);
            stockinward_btn.setBackgroundResource(R.drawable.stock_inward);

            ////for Stock Inwarddd
            db.open();
            if (db.getstockininserteddata(store_id, category_id, process_id).size() > 0) {
                stockinward_btn.setEnabled(true);
                stockinward_btn.setBackgroundResource(R.drawable.stock_inward_tick);
            } else {
                stockinward_btn.setEnabled(true);
                stockinward_btn.setBackgroundResource(R.drawable.stock_inward);
            }


        } else {
            stockinward_btn.setEnabled(false);
            stockinward_btn.setBackgroundResource(R.drawable.stock_inward_gray);

        }
        
        promo_compliance.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent in = new Intent(context, PromoCompliance.class);
                startActivity(in);
                DailyEntryMainMenu.this.finish();

            }
        });

        sales.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent in = new Intent(context, Sales.class);
                startActivity(in);
                DailyEntryMainMenu.this.finish();

            }
        });
        addtional_info_before.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent in = new Intent(context, BeforeAdditionalDisplay.class);
                startActivity(in);
                DailyEntryMainMenu.this.finish();

            }
        });

        after_stock.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent in = new Intent(context, AfterStockActivity.class);
                startActivity(in);
                DailyEntryMainMenu.this.finish();
            }
        });

        after_tot.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent in = new Intent(context, AfterTOT.class);
                startActivity(in);
                DailyEntryMainMenu.this.finish();

            }
        });

        comp.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent in = new Intent(context, CompetitionTracking.class);
                startActivity(in);
                DailyEntryMainMenu.this.finish();

            }
        });

        backroom_stock.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent in = new Intent(context, StockwareHouse.class);
                startActivity(in);
                DailyEntryMainMenu.this.finish();
            }
        });


        ///new add 11 september competition promotion
        comptitionfor_promotion.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent in = new Intent(context, CompetitionPromotionActivity.class);
                startActivity(in);
                DailyEntryMainMenu.this.finish();
            }
        });

        subcat_sos_facing.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent in = new Intent(context, ShareofShelfActivity.class);
                startActivity(in);
                DailyEntryMainMenu.this.finish();
            }
        });

        stockinward_btn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent in = new Intent(context, StockInActivity.class);
                startActivity(in);
                DailyEntryMainMenu.this.finish();
            }
        });

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent in = new Intent(context, CopyOfStorevisitedYesMenu.class);
        startActivity(in);
        DailyEntryMainMenu.this.finish();

    }

}
