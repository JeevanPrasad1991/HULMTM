package com.cpm.DailyEntry;

import java.io.File;
import java.util.ArrayList;

import com.cpm.Constants.CommonString;
import com.cpm.DailyEntry.AfterStockActivity.ViewHolder;
import com.cpm.database.GSKMTDatabase;
import com.cpm.delegates.CoverageBean;
import com.cpm.delegates.ShelfVisibilityBean;
import com.cpm.delegates.SkuBean;
import com.cpm.delegates.TOTBean;
import com.example.gsk_mtt.R;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Color;
import android.inputmethodservice.Keyboard;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.View.OnFocusChangeListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AbsListView.OnScrollListener;

public class StockwareHouse extends Activity implements OnClickListener {
    private SharedPreferences preferences;
    private SharedPreferences.Editor editor = null;
    public ArrayList<TOTBean> question_list = new ArrayList<TOTBean>();
    ListView lv, lv2;
    Button save, save2, shelf_visibility, cancel;
    ImageView cam1, cam2, cam3, cam4;
    public static ArrayList<SkuBean> sku_brand_list = new ArrayList<SkuBean>();
    private CustomKeyboardView mKeyboardView;
    boolean check = true;
    LinearLayout imglayout;
    Boolean update = false;
    GSKMTDatabase db;
    public String store_id, category_id, process_id, intime, username, app_version, region_id, store_type_id;
    String paked_key;

    protected static String _pathforcheck = "";
    protected static String _pathforcheck1 = "";
    public String image1 = "", image2 = "", image3 = "", _path, date, imgDate, image4 = "", str, state_id, key_id, class_id;
    static int row_pos = 10000;
    static int currentVersion = 1;
    private Keyboard mKeyboard;
    boolean saveFlag = true;
    MyAdaptor stockwarehouse_adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.stock_warehouse);
        lv = (ListView) findViewById(R.id.ListView01);
        preferences = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        store_id = preferences.getString(CommonString.KEY_STORE_ID, null);
        category_id = preferences.getString(CommonString.KEY_CATEGORY_ID, null);
        process_id = preferences.getString(CommonString.KEY_PROCESS_ID, null);
        date = preferences.getString(CommonString.KEY_DATE, null);
        intime = preferences.getString(CommonString.KEY_IN_TIME, null);
        username = preferences.getString(CommonString.KEY_USERNAME, null);
        app_version = preferences.getString(CommonString.KEY_VERSION, null);
        region_id = preferences.getString(CommonString.region_id, null);
        store_type_id = preferences.getString(CommonString.storetype_id, null);

        ///change by jeevan RAna
        state_id = preferences.getString(CommonString.KEY_STATE_ID, null);
        key_id = preferences.getString(CommonString.KEY_ID, null);
        class_id = preferences.getString(CommonString.KEY_CLASS_ID, null);
        paked_key = preferences.getString(CommonString.KEY_PACKED_KEY, null);
        currentVersion = android.os.Build.VERSION.SDK_INT;
        if (isTablet(getApplicationContext())) {
            mKeyboard = new Keyboard(this, R.xml.keyboard);
        } else {
            mKeyboard = new Keyboard(this, R.xml.keyboard1);

        }


        mKeyboardView = (CustomKeyboardView) findViewById(R.id.keyboard_view);
        mKeyboardView.setKeyboard(mKeyboard);
        mKeyboardView.setOnKeyboardActionListener(new BasicOnKeyboardActionListener(this));

//		sos = (TextView)findViewById(R.id.textBtn);
        save = (Button) findViewById(R.id.savebtn);

        cam1 = (ImageView) findViewById(R.id.cam1);
        cam2 = (ImageView) findViewById(R.id.cam2);
        cam3 = (ImageView) findViewById(R.id.cam3);
        cam4 = (ImageView) findViewById(R.id.cam4);

        imglayout = (LinearLayout) findViewById(R.id.imglayout);


        db = new GSKMTDatabase(StockwareHouse.this);
        db.open();

        if ((new File(Environment.getExternalStorageDirectory() + "/MT_GSK_Images/")).exists()) {
            Log.i("directory is created", "directory is created");
        } else {
            (new File(Environment.getExternalStorageDirectory() + "/MT_GSK_Images/")).mkdir();
        }

        str = Environment.getExternalStorageDirectory() + "/MT_GSK_Images/";
        imgDate = date.replace("/", "-");
        db.open();
        sku_brand_list = db.getBackRoomStock(store_id, category_id, process_id);
        if (sku_brand_list.size() == 0) {
            db.open();
            sku_brand_list = db.getBrandSkuListBackroom(category_id, store_id, process_id);
        } else {
            save.setText("Update");
            update = true;
        }


        if (sku_brand_list.size() > 0) {
            stockwarehouse_adapter = new MyAdaptor(this);
            lv.setAdapter(stockwarehouse_adapter);
        }

        lv.setOnScrollListener(new OnScrollListener() {
            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

            }

            @Override
            public void onScrollStateChanged(AbsListView arg0, int scrollState) {
                stockwarehouse_adapter.notifyDataSetChanged();
                lv.clearFocus();
                if (SCROLL_STATE_TOUCH_SCROLL == scrollState) {
                    View currentFocus = getCurrentFocus();
                    if (currentFocus != null) {
                        currentFocus.clearFocus();
                    }
                }
            }

        });

        save.setOnClickListener(this);
    }

    @Override
    public void onBackPressed() {

        if (mKeyboardView.getVisibility() == View.VISIBLE) {
            mKeyboardView.setVisibility(View.INVISIBLE);
            /*mKeyboardView.requestFocusFromTouch();*/
        } else {

            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("Are you sure you want to quit ?").setCancelable(false).setPositiveButton("YES",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {

                            Intent in = new Intent(StockwareHouse.this, DailyEntryMainMenu.class);
                            startActivity(in);
                            StockwareHouse.this.finish();

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
            ViewHolder holder;

            if (convertView == null) {
                convertView = mInflater.inflate(R.layout.stock_warehouse_list, null);
                holder = new ViewHolder();
                holder.brand_name = (TextView) convertView.findViewById(R.id.brand);
                holder.text = (TextView) convertView.findViewById(R.id.mainpage_rememberme);
                holder.stockQuantity = (EditText) convertView.findViewById(R.id.editText2);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
           /* if (currentVersion >= 11) {
                holder.stockQuantity.setTextIsSelectable(true);
                holder.stockQuantity.setRawInputType(InputType.TYPE_CLASS_TEXT);
            } else {
                holder.stockQuantity.setInputType(0);
            }*/
            if (position == 0) {
                holder.brand_name.setText(sku_brand_list.get(position).getBrand());
                holder.text.setText(sku_brand_list.get(position).getSku_name());
            } else {

                if (sku_brand_list.get(position - 1).getBrand().equalsIgnoreCase(sku_brand_list.get(position).getBrand())) {
                    holder.brand_name.setText("");
                    holder.text.setText(sku_brand_list.get(position).getSku_name());
                } else {
                    holder.brand_name.setText(sku_brand_list.get(position).getBrand());
                    holder.text.setText(sku_brand_list.get(position).getSku_name());

                }
            }

            holder.stockQuantity.setOnFocusChangeListener(new OnFocusChangeListener() {
                public void onFocusChange(View v, boolean hasFocus) {
                    if (!hasFocus) {
                        final int position = v.getId();
                        final EditText Caption = (EditText) v;
                        String value1 = Caption.getText().toString();
                        if (value1.equals("")) {
                            sku_brand_list.get(position).setBackRoomStock("");
                        } else {
                            sku_brand_list.get(position).setBackRoomStock(value1);
                        }
                    }
                }
            });


            if (!saveFlag) {
                if (sku_brand_list.get(position).getBackRoomStock().equals("")) {
                    holder.stockQuantity.setHint("EMPTY");
                    holder.stockQuantity.setHintTextColor(Color.RED);
                }
            }

            holder.stockQuantity.setText(sku_brand_list.get(position).getBackRoomStock());
            holder.stockQuantity.setId(position);
            return convertView;
        }

    }


    private void showKeyboardWithAnimation() {
        if (mKeyboardView.getVisibility() == View.GONE) {
            Animation animation = AnimationUtils.loadAnimation(StockwareHouse.this, R.anim.slide_in_bottom);
            mKeyboardView.showWithAnimation(animation);
        } else if (mKeyboardView.getVisibility() == View.INVISIBLE) {
            mKeyboardView.setVisibility(View.VISIBLE);
        }
    }

    public static boolean isTablet(Context context) {
        return (context.getResources().getConfiguration().screenLayout
                & Configuration.SCREENLAYOUT_SIZE_MASK)
                >= Configuration.SCREENLAYOUT_SIZE_LARGE;
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.savebtn) {
            lv.clearFocus();
            lv.invalidateViews();
            row_pos = 10000;
            if (validate_values()) {
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setMessage("Are you sure you want to save").setCancelable(false).setPositiveButton("Yes",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                if (update) {
                                    for (int i = 0; i < sku_brand_list.size(); i++) {
                                        db.open();
                                        db.updateBackStoreData(store_id, category_id, process_id, sku_brand_list.get(i).getBackRoomStock(), sku_brand_list.get(i).getSku_id());
                                        Intent in = new Intent(StockwareHouse.this, DailyEntryMainMenu.class);
                                        startActivity(in);
                                        StockwareHouse.this.finish();

                                    }

                                } else {
                                    db.open();
                                    db.InsertStockwareHouseData(store_id, sku_brand_list, username, category_id, process_id);
                                    Intent in = new Intent(StockwareHouse.this, DailyEntryMainMenu.class);
                                    startActivity(in);
                                    StockwareHouse.this.finish();
                                }

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

                Toast.makeText(getApplicationContext(), "Please fill all data", Toast.LENGTH_LONG).show();

            }
        }


    }

    public boolean validate_values() {
        saveFlag = true;
        for (int i = 0; i < sku_brand_list.size(); i++) {
            if (sku_brand_list.get(i).getBackRoomStock().equals("")) {
                saveFlag = false;
                break;
            }


        }

        return saveFlag;

    }

    public void hide() {
        mKeyboardView.setVisibility(View.INVISIBLE);
        /*	// mKeyboardView.clearFocus();
        mKeyboardView.requestFocusFromTouch();*/

    }
}
