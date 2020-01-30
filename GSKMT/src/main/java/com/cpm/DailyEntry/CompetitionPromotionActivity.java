package com.cpm.DailyEntry;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.cpm.Constants.CommonString;
import com.cpm.database.GSKMTDatabase;


import com.cpm.xmlGetterSetter.SKUGetterSetter;
import com.crashlytics.android.Crashlytics;
import com.example.gsk_mtt.R;

import java.io.File;
import java.util.ArrayList;

public class CompetitionPromotionActivity extends Activity implements AdapterView.OnItemSelectedListener, View.OnClickListener {
    String store_id, category_id, process_id, date, intime, username, _pathforcheck = "", img1 = "", str,
            app_version, store_type_id, imgDate, _path, competition_name = "", competition_id = "", brand_name = "",
            brand_id = "", sku_name = "", sku_id = "", price = "0", priceOFFSpinValue = "", segment_Id = "", segment = "";
    private ArrayAdapter<CharSequence> competitionAdapter, skuAdaptor, brandAdapter, segmentAdapter;
    ArrayList<SKUGetterSetter> competitionList = new ArrayList<>();
    ArrayList<SKUGetterSetter> compltCompetitionList = new ArrayList<>();
    ArrayList<SKUGetterSetter> brand_list, sku_Listwithprice, competi_segmentList = new ArrayList<>();
    SharedPreferences preferences;
    GSKMTDatabase db;
    Spinner competitionpcat, comp_sku, comp_brand, comptoggle_priceoff,/*new changes*/
            comp_skuSegment;
    //  ToggleButton COMP_checkbox;
    LinearLayout RLoffprice, rl_allDATA, rl_MRP, rl_promotion, rl_brandLayout,
            rl_skuLayout, RL_headerS, addbtnLayout /*new changes*/, rl_skusegmentLayout;
    TextView edt_comp_promotion, comp_skuname, compmrp_qty;
    private EditText edt_comp_priceoff;
    RecyclerView competitionPRecycler;
    MyAdaptor compromoAdapter;
    Button add_btn, save_btn;
    // Array of choices
    String priceoff_spinValue[] = {" Select Price OFF ", "YES", "NO"};
    boolean addflag = false;
    Context context;
    NestedScrollView scroll_view;
    //ImageView camera;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_competition_promotion);
        uiallids();
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        setcompetitionpromotionDats();
    }

    void uiallids() {
        competitionpcat = findViewById(R.id.competitionpcat);
        comp_sku = findViewById(R.id.comp_sku);
        comp_brand = findViewById(R.id.comp_brand);
        RL_headerS = findViewById(R.id.RL_headerS);
        addbtnLayout = findViewById(R.id.addbtnLayout);
        scroll_view = findViewById(R.id.scroll_view);

        competitionPRecycler = findViewById(R.id.competitionPRecycler);
        comptoggle_priceoff = findViewById(R.id.comptoggle_priceoff);

        // COMP_checkbox = findViewById(R.id.COMP_checkbox);

        RLoffprice = findViewById(R.id.RLoffprice);
        rl_allDATA = findViewById(R.id.rl_allDATA);
        rl_promotion = findViewById(R.id.rl_promotion);
        rl_brandLayout = findViewById(R.id.rl_brandLayout);
        rl_skuLayout = findViewById(R.id.rl_skuLayout);

        edt_comp_priceoff = findViewById(R.id.edt_comp_priceoff);
        edt_comp_promotion = findViewById(R.id.edt_comp_promotion);

        comp_skuname = findViewById(R.id.comp_skuname);
        compmrp_qty = findViewById(R.id.compmrp_qty);

        rl_MRP = findViewById(R.id.rl_MRP);

        add_btn = findViewById(R.id.add_btn);
        save_btn = findViewById(R.id.save_btn);
        // camera = findViewById(R.id.camera);

        //camera.setOnClickListener(this);
        save_btn.setOnClickListener(this);
        add_btn.setOnClickListener(this);
        //COMP_checkbox.setOnClickListener(this);

        /*new changes 18.09.2018*/
        comp_skuSegment = (Spinner) findViewById(R.id.comp_skuSegment);
        rl_skusegmentLayout = (LinearLayout) findViewById(R.id.rl_skusegmentLayout);

        context = this;
        preferences = PreferenceManager.getDefaultSharedPreferences(context);
        store_id = preferences.getString(CommonString.KEY_STORE_ID, null);
        category_id = preferences.getString(CommonString.KEY_CATEGORY_ID, null);
        process_id = preferences.getString(CommonString.KEY_PROCESS_ID, null);
        date = preferences.getString(CommonString.KEY_DATE, null);
        intime = preferences.getString(CommonString.KEY_IN_TIME, null);
        username = preferences.getString(CommonString.KEY_USERNAME, null);
        app_version = preferences.getString(CommonString.KEY_VERSION, null);
        store_type_id = preferences.getString(CommonString.storetype_id, null);
        imgDate = date.replace("/", "-");
        db = new GSKMTDatabase(context);
        db.open();
        str = Environment.getExternalStorageDirectory() + "/MT_GSK_Images/";

        competitionList = db.getcomptitiondataforpromotion(category_id);
        competitionAdapter = new ArrayAdapter<CharSequence>(context, R.layout.spinner_custom_item);
        competitionAdapter.setDropDownViewResource(R.layout.spinner_custom_item);
        competitionAdapter.add("-Select Competition-");

        for (int i = 0; i < competitionList.size(); i++) {
            competitionAdapter.add(competitionList.get(i).getCompany().get(0));
        }
        competitionpcat.setAdapter(competitionAdapter);
        competitionpcat.setOnItemSelectedListener(this);


        ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(context, R.layout.spinner_custom_item, priceoff_spinValue); //selected item will look like a spinner set from XML
        spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        comptoggle_priceoff.setAdapter(spinnerArrayAdapter);
        comptoggle_priceoff.setOnItemSelectedListener(this);


        edt_comp_priceoff.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                if (!hasFocus) {
                    EditText Caption = (EditText) view;
                    String value = Caption.getText().toString().replaceFirst("^0+(?!$)", "");
                    if (!value.equals("")) {
                        try {
                            int totalscore = Integer.parseInt(price);
                            int priceoff = Integer.parseInt(value.toString());
                            int percentage = 0;
                            if (priceoff > totalscore) {
                                final AlertDialog.Builder builder = new AlertDialog.Builder(context).setMessage("Discount value cannot be greater than 'MRP'");
                                builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        setHideSoftKeyboard(edt_comp_priceoff);
                                        rl_promotion.setVisibility(View.VISIBLE);
                                        edt_comp_promotion.setText("0 % ");
                                        edt_comp_priceoff.setText("0");
                                        hideSoftKeyboard();
                                        dialog.dismiss();
                                    }
                                });
                                builder.show();

                            } else {
                                if (totalscore != 0) {
                                    percentage = (Integer) ((priceoff * 100) / totalscore);
                                }
                                rl_promotion.setVisibility(View.VISIBLE);
                                edt_comp_promotion.setText(String.valueOf(percentage) + " % ");
                                edt_comp_priceoff.setText(value.toString());
                            }
                        } catch (NumberFormatException e) {
                            Crashlytics.logException(e);
                            e.printStackTrace();
                        }
                    } else {
                        rl_promotion.setVisibility(View.GONE);
                    }
                }
            }
        });


        edt_comp_priceoff.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    //Clear focus here from edittext
                    edt_comp_priceoff.clearFocus();
                }
                return false;
            }
        });
    }


    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        switch (parent.getId()) {
            case R.id.competitionpcat:
                if (position != 0) {
                    competition_id = competitionList.get(position - 1).getCompany_id().get(0);
                    competition_name = competitionList.get(position - 1).getCompany().get(0);

                    brand_list = db.getbrandListByCompanyId(competition_id, category_id);
                    if (brand_list.size() > 0) {
                        rl_brandLayout.setVisibility(View.VISIBLE);

                        brandAdapter = new ArrayAdapter<CharSequence>(context, R.layout.spinner_custom_item);
                        brandAdapter.setDropDownViewResource(R.layout.spinner_custom_item);
                        brandAdapter.add("-Select Brand-");
                        for (int i = 0; i < brand_list.size(); i++) {
                            brandAdapter.add(brand_list.get(i).getBrand().get(0));
                        }
                        comp_brand.setAdapter(brandAdapter);
                        comp_brand.setOnItemSelectedListener(this);
                    } else {
                        brand_list.clear();
                        sku_Listwithprice.clear();
                        rl_brandLayout.setVisibility(View.GONE);
                        rl_skuLayout.setVisibility(View.GONE);
                        rl_MRP.setVisibility(View.GONE);
                    }

                } else {
                    competition_id = "";
                    competition_name = "";
                }

                break;

            case R.id.comp_brand:
                if (position != 0) {
                    brand_name = brand_list.get(position - 1).getBrand().get(0);
                    brand_id = brand_list.get(position - 1).getBrand_id().get(0);
                    competi_segmentList = db.getSegmentListByBrandId(brand_id, category_id);
                    if (competi_segmentList.size() > 0) {
                        rl_skusegmentLayout.setVisibility(View.VISIBLE);
                        segmentAdapter = new ArrayAdapter<CharSequence>(context, R.layout.spinner_custom_item);
                        segmentAdapter.setDropDownViewResource(R.layout.spinner_custom_item);
                        segmentAdapter.add("-Select Segment-");
                        for (int i = 0; i < competi_segmentList.size(); i++) {
                            segmentAdapter.add(competi_segmentList.get(i).getComp_segment());
                        }
                        comp_skuSegment.setAdapter(segmentAdapter);
                        comp_skuSegment.setOnItemSelectedListener(this);
                    } else {
                        competi_segmentList.clear();
                        rl_skusegmentLayout.setVisibility(View.GONE);
                        rl_skuLayout.setVisibility(View.GONE);
                        rl_MRP.setVisibility(View.GONE);
                    }

                } else {
                    brand_id = "";
                    brand_name = "";
                }
                break;

            case R.id.comp_skuSegment:
                if (position != 0) {
                    segment_Id = competi_segmentList.get(position - 1).getComp_segment_Id();
                    segment = competi_segmentList.get(position - 1).getComp_segment();
                    sku_Listwithprice = db.getSkuListByBrandId(brand_id, category_id, segment_Id);
                    if (sku_Listwithprice.size() > 0) {
                        rl_skuLayout.setVisibility(View.VISIBLE);
                        skuAdaptor = new ArrayAdapter<CharSequence>(context, R.layout.spinner_custom_item);
                        skuAdaptor.setDropDownViewResource(R.layout.spinner_custom_item);
                        skuAdaptor.add("-Select Sku-");
                        for (int i = 0; i < sku_Listwithprice.size(); i++) {
                            skuAdaptor.add(sku_Listwithprice.get(i).getSku_name().get(0));
                        }

                        comp_sku.setAdapter(skuAdaptor);
                        comp_sku.setOnItemSelectedListener(this);
                    } else {
                        sku_Listwithprice.clear();
                        rl_skuLayout.setVisibility(View.GONE);
                        rl_MRP.setVisibility(View.GONE);
                    }
                } else {
                    segment_Id = "";
                    segment = "";
                }
                break;
            case R.id.comp_sku:
                if (position != 0) {
                    sku_id = sku_Listwithprice.get(position - 1).getSku_id().get(0);
                    sku_name = sku_Listwithprice.get(position - 1).getSku_name().get(0);
                    price = sku_Listwithprice.get(position - 1).getMRP_sku().get(0);
                    comp_skuname.setText("   MRP  ");
                    compmrp_qty.setText(price);
                    rl_MRP.setVisibility(View.VISIBLE);
                } else {
                    sku_id = "";
                    sku_name = "";
                    price = "0";
                    rl_MRP.setVisibility(View.GONE);
                    comp_skuname.setText("");
                    compmrp_qty.setText("");
                }

                break;

            case R.id.comptoggle_priceoff:
                if (position != 0) {
                    priceOFFSpinValue = comptoggle_priceoff.getSelectedItem().toString();
                    if (comptoggle_priceoff.getSelectedItemId() == 1) {
                        RLoffprice.setVisibility(View.VISIBLE);
                        rl_promotion.setVisibility(View.VISIBLE);
                    } else {
                        RLoffprice.setVisibility(View.GONE);
                        rl_promotion.setVisibility(View.GONE);
                        edt_comp_priceoff.setText("");
                        edt_comp_promotion.setText("");
                    }
                } else {
                    priceOFFSpinValue = "";
                }
                break;
        }

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.add_btn:
                hideSoftKeyboard();
                edt_comp_priceoff.clearFocus();
                if (validate()) {
                    if (validatedata()) {
                        int edttxt_value = 0;
                        int price_int = 0;
                        price_int = Integer.parseInt(price);
                        if (!edt_comp_priceoff.getText().toString().equals("")) {
                            edttxt_value = Integer.parseInt(edt_comp_priceoff.getText().toString());
                        }
                        if (edttxt_value <= price_int) {
                            AlertDialog.Builder builder = new AlertDialog.Builder(this);
                            builder.setTitle("Parinaam").setMessage("Do you want to add data");
                            builder.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    addflag = true;
                                    SKUGetterSetter ab = new SKUGetterSetter();
                                    // ab.setCompIsExist(true);
                                    ab.setCompany_id(competition_id);
                                    ab.setCompany(competition_name);
                                    ab.setBrand(brand_name);
                                    ab.setBrand_id(brand_id);
                                    ab.setComp_segment(segment);
                                    ab.setComp_segment_Id(segment_Id);
                                    ab.setSku_name(sku_name);
                                    ab.setSku_id(sku_id);
                                    ab.setMRP_sku(price);
                                    if (!priceOFFSpinValue.equals("") && priceOFFSpinValue.equalsIgnoreCase("YES")) {
                                        ab.setPriceOFFtoggleValue(priceOFFSpinValue);
                                        ab.setPriceOFF_edtRS(edt_comp_priceoff.getText().toString().replaceFirst("().<0>?", ""));
                                        ab.setPromotionpercentValue(edt_comp_promotion.getText().toString());
                                    } else {
                                        ab.setPriceOFFtoggleValue("");
                                        ab.setPriceOFF_edtRS("0");
                                        ab.setPromotionpercentValue("0 %");
                                    }

                                    ab.setComp_img("");
                                    ab.setCategory_id(category_id);
                                    ab.setProcess_Id(process_id);
                                    ab.setStore_Id(store_id);

                                    compltCompetitionList.add(ab);
                                    compromoAdapter = new MyAdaptor(context, compltCompetitionList);
                                    competitionPRecycler.setAdapter(compromoAdapter);
                                    competitionPRecycler.setLayoutManager(new LinearLayoutManager(context));
                                    // camera.setImageResource(R.drawable.camera_ico);
                                    edt_comp_priceoff.setText("");
                                    edt_comp_promotion.setText("");
                                    competitionpcat.setSelection(0);
                                    comp_sku.setSelection(0);
                                    comp_brand.setSelection(0);
                                    comptoggle_priceoff.setSelection(0);
                                    comp_skuSegment.setSelection(0);

                                    rl_skusegmentLayout.setVisibility(View.GONE);
                                    rl_promotion.setVisibility(View.GONE);
                                    RLoffprice.setVisibility(View.GONE);
                                    rl_MRP.setVisibility(View.GONE);
                                    rl_brandLayout.setVisibility(View.GONE);
                                    rl_skuLayout.setVisibility(View.GONE);
                                    price = "0";
                                    img1 = "";

                                }
                            });
                            builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    dialogInterface.dismiss();
                                }
                            });
                            builder.show();
                        }

                    } else {
                        TOAST("Already Exist please take another.");
                    }
                }

                break;
            case R.id.save_btn:
                if (compltCompetitionList.size() > 0 && addflag) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(this);
                    builder.setTitle("Parinaam").setMessage("Do you want to save data");
                    builder.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            db.open();
                            db.InsertCompetitionPromotionListData(compltCompetitionList);
                            TOAST("Data has been saved");
                            Intent in = new Intent(context, DailyEntryMainMenu.class);
                            startActivity(in);
                            CompetitionPromotionActivity.this.finish();
                        }
                    });
                    builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.dismiss();

                        }
                    });
                    builder.show();
                } else {
                    TOAST("Please add first");
                }
                break;

        }

    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.i("MakeMachine", "resultCode: " + resultCode);
        switch (resultCode) {
            case 0:
                Log.i("MakeMachine", "User cancelled");
                break;
            case -1:
                if (_pathforcheck != null && !_pathforcheck.equals("")) {
                    if (new File((str + _pathforcheck).trim()).exists()) {
                        img1 = _pathforcheck;
                        _pathforcheck = "";
                    }
                }
                break;
        }
    }

    @Override
    public void onBackPressed() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(CommonString.ONBACK_ALERT_MESSAGE)
                .setCancelable(false)
                .setPositiveButton("OK",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                Intent in = new Intent(context, DailyEntryMainMenu.class);
                                startActivity(in);
                                CompetitionPromotionActivity.this.finish();
                            }
                        })
                .setNegativeButton("Cancel",
                        new DialogInterface.OnClickListener() {
                            public void onClick(
                                    DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });
        AlertDialog alert = builder.create();
        alert.show();

    }

    public void hideSoftKeyboard() {
        try {
            InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void TOAST(String msg) {
        Toast.makeText(context, msg, Toast.LENGTH_LONG).show();
    }


    public class MyAdaptor extends RecyclerView.Adapter<MyAdaptor.ViewHolder> {
        private LayoutInflater mInflater;
        private Context mcontext;
        private ArrayList<SKUGetterSetter> list;

        public MyAdaptor(Context context, ArrayList<SKUGetterSetter> list1) {
            mInflater = LayoutInflater.from(context);
            mcontext = context;
            list = list1;
        }

        @Override
        public MyAdaptor.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = mInflater.inflate(R.layout.competitionpromotion_entry, parent, false);
            MyAdaptor.ViewHolder holder = new MyAdaptor.ViewHolder(view);
            return holder;
        }

        @Override
        public void onBindViewHolder(MyAdaptor.ViewHolder holder, final int position) {
            holder.delete.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    if (list.get(position).getKey_id() == null) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(mcontext);
                        builder.setMessage("Are you sure you want to Delete")
                                .setCancelable(false)
                                .setPositiveButton("Yes",
                                        new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int id) {
                                                list.remove(position);
                                                notifyDataSetChanged();
                                                if (list.size() > 0) {
                                                    compromoAdapter = new MyAdaptor(mcontext, list);
                                                    competitionPRecycler.setAdapter(compromoAdapter);
                                                    competitionPRecycler.setLayoutManager(new LinearLayoutManager(mcontext));
                                                    compromoAdapter.notifyDataSetChanged();
                                                }
                                                notifyDataSetChanged();
                                            }
                                        })
                                .setNegativeButton("No",
                                        new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog,
                                                                int id) {
                                                dialog.cancel();
                                            }
                                        });
                        AlertDialog alert = builder.create();
                        alert.show();
                    } else {
                        AlertDialog.Builder builder = new AlertDialog.Builder(mcontext);
                        builder.setMessage("Are you sure you want to Delete").setCancelable(false).setPositiveButton("Yes",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        db.deleteCompetitionPromotion(list.get(position).getKey_id());
                                        list.remove(position);
                                        notifyDataSetChanged();
                                        if (list.size() > 0) {
                                            compromoAdapter = new MyAdaptor(mcontext, list);
                                            competitionPRecycler.setAdapter(compromoAdapter);
                                            competitionPRecycler.setLayoutManager(new LinearLayoutManager(mcontext));
                                            compromoAdapter.notifyDataSetChanged();
                                        }

                                        notifyDataSetChanged();
                                    }
                                })
                                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        dialog.cancel();
                                    }
                                });
                        AlertDialog alert = builder.create();
                        alert.show();
                    }
                }
            });


            holder.comp_name.setText(list.get(position).getCompany().get(0).toString());
            holder.sku_name.setText(list.get(position).getSku_name().get(0).toString());
            holder.priceOFF.setText(list.get(position).getPriceOFF_edtRS().toString());
            holder.promotion_nm.setText(list.get(position).getPromotionpercentValue());

            holder.comp_name.setId(position);
            holder.sku_name.setId(position);
            holder.priceOFF.setId(position);
            holder.promotion_nm.setId(position);
            holder.delete.setId(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }


        @Override
        public int getItemCount() {
            return list.size();
        }


        class ViewHolder extends RecyclerView.ViewHolder {
            TextView comp_name, sku_name, priceOFF, promotion_nm;
            ImageView delete;

            public ViewHolder(View convertView) {
                super(convertView);
                comp_name = (TextView) convertView.findViewById(R.id.comp_name);
                sku_name = (TextView) convertView.findViewById(R.id.sku_name);
                priceOFF = (TextView) convertView.findViewById(R.id.priceOFF);
                promotion_nm = (TextView) convertView.findViewById(R.id.promotion_nm);
                delete = (ImageView) convertView.findViewById(R.id.dlt_img);
            }

        }
    }


    public void setcompetitionpromotionDats() {
        compltCompetitionList = db.getcompetitionPromotionfromDatabase(store_id, category_id, process_id);
        if (compltCompetitionList.size() > 0) {
            competitionPRecycler.setAdapter(new MyAdaptor(context, compltCompetitionList));
            competitionPRecycler.setLayoutManager(new LinearLayoutManager(this));
            competitionPRecycler.setVisibility(View.VISIBLE);
            RL_headerS.setVisibility(View.VISIBLE);
            rl_allDATA.setVisibility(View.VISIBLE);
            addbtnLayout.setVisibility(View.VISIBLE);
        }
    }


    private boolean validate() {
        boolean status = true;
        if (competitionpcat.getSelectedItemId() == 0) {
            TOAST("Please Select Competition Dropdown .");
            status = false;
        }

        if (status && comp_brand.getSelectedItemId() == 0) {
            TOAST("Please Select Brand Dropdown .");
            status = false;
        }
        if (status && comp_sku.getSelectedItemId() == 0) {
            TOAST("Please Select Sku Dropdown .");
            status = false;
        }

        if (status && comptoggle_priceoff.getSelectedItemId() == 1 && edt_comp_priceoff.getText().toString().isEmpty()) {
            TOAST("Please Fill Price OFF Value .");
            status = false;
        }

        return status;
    }

    public boolean validatedata() {
        boolean result = true;
        try {
            if (compltCompetitionList.size() > 0) {
                for (int k = 0; k < compltCompetitionList.size(); k++) {
                    if (compltCompetitionList.get(k).getBrand_id().get(0).equals(brand_id) && compltCompetitionList.get(k).getCompany_id().get(0).equals(competition_id)
                            && compltCompetitionList.get(k).getSku_id().get(0).equals(sku_id) && compltCompetitionList.get(k).getStore_Id().equals(store_id)
                            && compltCompetitionList.get(k).getCategory_id().get(0).equals(category_id) && compltCompetitionList.get(k).getProcess_Id().equals(process_id)) {
                        result = false;
                        break;
                    }
                }
            }
        } catch (Exception e) {
            Crashlytics.logException(e);
            e.printStackTrace();
        }

        return result;

    }

    private void setHideSoftKeyboard(EditText editText) {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(editText.getWindowToken(), 0);
    }
}
