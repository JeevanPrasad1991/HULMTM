package com.cpm.DailyEntry;

import java.util.ArrayList;

import com.cpm.Constants.CommonString;
import com.cpm.database.GSKMTDatabase;
import com.cpm.delegates.PromotionBean;
import com.cpm.delegates.SkuBean;
import com.cpm.delegates.TOTBean;
import com.example.gsk_mtt.R;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceManager;

import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

public class DailyEntryMainMenu extends Activity {

    public Button after_stock,
            after_tot, before_add, after_add, addtional_info_before, comptitionfor_promotion,
            promo_compliance, comp, backroom_stock, sales;
    private SharedPreferences preferences;
    private SharedPreferences.Editor editor = null;
    String store_id, category_id, date, process_id, key_id, cat_name, COMPETITION_PROMOTION_flag;
    TextView category_name;
    GSKMTDatabase db;
    ArrayList<SkuBean> beforeAddtionalData, afterStockData, sos_target_list, entered_comp_data, backRoomStockData;
    ArrayList<TOTBean> afterTOTData, totstockdata;
    ArrayList<TOTBean> mappingDataTOTCategoryWise;
    ArrayList<SkuBean> salesData;
    ArrayList<PromotionBean> mappingPromotion, promotionData;
    String sos_after = "", afterStockQuantity = "";
    TextView after_sos;
    public static ArrayList<TOTBean> TOTdata = new ArrayList<TOTBean>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dailyentry);
        after_stock = (Button) findViewById(R.id.after_stock);
        after_tot = (Button) findViewById(R.id.after_tot);
        addtional_info_before = (Button) findViewById(R.id.before_additional);
        promo_compliance = (Button) findViewById(R.id.promotional_data);
        category_name = (TextView) findViewById(R.id.cat_name);
        comp = (Button) findViewById(R.id.competitionTracking);
        sales = (Button) findViewById(R.id.salesTracking);
        backroom_stock = (Button) findViewById(R.id.backroom_stock);
        after_sos = (TextView) findViewById(R.id.sos_after);
        ///new Add 11/september
        comptitionfor_promotion = (Button) findViewById(R.id.comptitionfor_promotion);
        preferences = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        date = preferences.getString(CommonString.KEY_DATE, null);
        store_id = preferences.getString(CommonString.KEY_STORE_ID, null);
        category_id = preferences.getString(CommonString.KEY_CATEGORY_ID, null);
        cat_name = preferences.getString(CommonString.KEY_CATEGORY_NAME, null);
        process_id = preferences.getString(CommonString.KEY_PROCESS_ID, null);
        COMPETITION_PROMOTION_flag = preferences.getString(CommonString.KEY_COMPETITION_PROMOTION, null);
        key_id = preferences.getString(CommonString.KEY_ID, null);
        beforeAddtionalData = new ArrayList<SkuBean>();
        afterTOTData = new ArrayList<TOTBean>();
        totstockdata = new ArrayList<TOTBean>();
        mappingPromotion = new ArrayList<PromotionBean>();
        sos_target_list = new ArrayList<SkuBean>();
        entered_comp_data = new ArrayList<SkuBean>();
        salesData = new ArrayList<SkuBean>();
        mappingDataTOTCategoryWise = new ArrayList<TOTBean>();
        db = new GSKMTDatabase(DailyEntryMainMenu.this);
        db.open();
        category_name.setText(cat_name);
        afterStockData = db.getAfterStockData(store_id, category_id, process_id);
        backRoomStockData = db.getBackRoomStock(store_id, category_id, process_id);
        salesData = db.getSalesStockData(store_id, category_id, process_id);
        sales.setVisibility(View.GONE);
        sales.setEnabled(false);

        if (salesData.size() > 0) {
            sales.setBackgroundResource(R.drawable.sales_tick);
        } else {
            sales.setBackgroundResource(R.drawable.sales);
        }

        for (int i = 0; i < afterStockData.size(); i++) {
            afterStockQuantity = afterStockData.get(i).getAfter_Stock();
        }

        beforeAddtionalData = db.getProductEntryDetail(store_id, category_id, process_id);
        afterTOTData = db.getAfterTOTData(store_id, category_id, process_id);
        promotionData = db.getInsertedPromoCompliances(store_id, category_id, process_id);
        entered_comp_data = db.getEnteredCompetitionDetail(store_id, category_id, process_id);
        sos_target_list = db.getSOSTarget(store_id, category_id, process_id);

        if (backRoomStockData.size() > 0) {
            backroom_stock.setBackgroundResource(R.drawable.backroom_tick);

        } else {
            backroom_stock.setBackgroundResource(R.drawable.backroom);
        }

        mappingDataTOTCategoryWise = db.getTOTData(store_id, process_id, category_id);
        mappingPromotion = db.getPromoComplianceData(key_id, process_id, category_id);

        if (!afterStockQuantity.equals("")) {
            after_stock.setBackgroundResource(R.drawable.tick_stock_after_ico);
            sos_after = db.getAFTERSOS(store_id, category_id, process_id);
            if (category_id.equals("2") || category_id.equals("6")) {
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
                if (sos_target_list.size() > 0) {
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
                }
            }
        }

        if (!afterStockQuantity.equals("")) {
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
                if (db.getcomptitiondataforpromotion(category_id).size() > 0) {
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


        promo_compliance.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent in = new Intent(getBaseContext(), PromoCompliance.class);
                startActivity(in);
                DailyEntryMainMenu.this.finish();

            }
        });

        sales.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent in = new Intent(getBaseContext(), Sales.class);
                startActivity(in);
                DailyEntryMainMenu.this.finish();

            }
        });
        addtional_info_before.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent in = new Intent(getBaseContext(), BeforeAdditionalDisplay.class);
                startActivity(in);
                DailyEntryMainMenu.this.finish();

            }
        });

        after_stock.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent in = new Intent(getBaseContext(), AfterStockActivity.class);
                startActivity(in);
                DailyEntryMainMenu.this.finish();

            }
        });

        after_tot.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent in = new Intent(getBaseContext(), AfterTOT.class);
                startActivity(in);
                DailyEntryMainMenu.this.finish();

            }
        });

        comp.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent in = new Intent(getBaseContext(), CompetitionTracking.class);
                startActivity(in);
                DailyEntryMainMenu.this.finish();

            }
        });

        backroom_stock.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent in = new Intent(getBaseContext(), StockwareHouse.class);
                startActivity(in);
                DailyEntryMainMenu.this.finish();
            }
        });


        ///new add 11 september competition promotion
        comptitionfor_promotion.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent in = new Intent(getBaseContext(), CompetitionPromotionActivity.class);
                startActivity(in);
                DailyEntryMainMenu.this.finish();
            }
        });

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent in = new Intent(DailyEntryMainMenu.this, CopyOfStorevisitedYesMenu.class);
        startActivity(in);
        DailyEntryMainMenu.this.finish();

    }

}
