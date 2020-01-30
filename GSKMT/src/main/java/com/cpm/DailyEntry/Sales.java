package com.cpm.DailyEntry;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import com.cpm.Constants.CommonString;

import com.cpm.DailyEntry.BeforeStock.ViewHolder;
import com.cpm.database.GSKMTDatabase;
import com.cpm.delegates.CoverageBean;
import com.cpm.delegates.SkuBean;
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
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;

import android.text.InputType;
import android.util.Log;
import android.view.Gravity;
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

public class Sales extends Activity implements OnClickListener {

    private SharedPreferences preferences;
    private SharedPreferences.Editor editor = null;
    ListView lv;
    Button save;
    ImageView cam1, cam2, cam3, cam4;
    public static ArrayList<SkuBean> sku_brand_list = new ArrayList<SkuBean>();
    GSKMTDatabase db;
    String store_id, category_id, process_id, intime, username, app_version, region_id, store_type_id, state_id, key_id, class_id;
    protected static String _pathforcheck = "";
    public String image1 = "", image2 = "", image3 = "", _path, date, image4 = "", imgDate;
    private static String str, path;
    String img1 = "", reason_id = "0", remark = "";
    String _Currentdate;
    boolean _taken = true;
    public static ArrayList<String> allthreeDates = new ArrayList<String>();
    public static ArrayList<SkuBean> sos_target_list = new ArrayList<SkuBean>();
    ArrayList<CoverageBean> coveragelist = new ArrayList<CoverageBean>();
    static int row_pos = 10000;
    AlertDialog alert;
    static int currentVersion = 1;
    TextView sos_target;
    private Keyboard mKeyboard;
    private CustomKeyboardView mKeyboardView;
    LinearLayout imglayout;
    boolean update = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.sales);

        lv = (ListView) findViewById(R.id.ListView01);


        preferences = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        store_id = preferences.getString(CommonString.KEY_STORE_ID, null);
        store_type_id = preferences.getString(CommonString.storetype_id, null);
        category_id = preferences.getString(CommonString.KEY_CATEGORY_ID, null);
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
        //		imglayout = (LinearLayout)findViewById(R.id.imageLayout);
        save = (Button) findViewById(R.id.savebtn);



        imgDate = date.replace("/", "-");
        db = new GSKMTDatabase(Sales.this);
        db.open();
        coveragelist = db.getCoverageData(date, store_id, process_id);
        sku_brand_list = db.getSalesStockData(store_id, category_id, process_id);
        if (sku_brand_list.size() == 0) {
            sku_brand_list = db.getBrandSkuListForSales(category_id, store_id, process_id, state_id, store_type_id,key_id,class_id);
        } else {
            save.setText("Update");
            update = true;
        }
        if (sku_brand_list.size() > 0) {
            System.out.println("" + sku_brand_list.size());
            lv.setAdapter(new MyAdaptor(this));
        }


        save.setOnClickListener(this);
        currentVersion = android.os.Build.VERSION.SDK_INT;
        if (isTablet(getApplicationContext())) {
            mKeyboard = new Keyboard(this, R.xml.keyboard);
            //			Toast.makeText(BeforeStock.this, "Hi ! i am tablet", Toast.LENGTH_LONG).show();

        } else {
            mKeyboard = new Keyboard(this, R.xml.keyboard1);
            //			Toast.makeText(BeforeStock.this, "Hi ! i am phone", Toast.LENGTH_LONG).show();
        }


        mKeyboardView = (CustomKeyboardView) findViewById(R.id.keyboard_view);
        mKeyboardView.setKeyboard(mKeyboard);
        mKeyboardView
                .setOnKeyboardActionListener(new BasicOnKeyboardActionListener(
                        this));

        lv.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScroll(AbsListView view, int firstVisibleItem,
                                 int visibleItemCount, int totalItemCount) {

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

        //		super.onBackPressed();

        if (mKeyboardView.getVisibility() == View.VISIBLE) {
            mKeyboardView.setVisibility(View.INVISIBLE);
			/*mKeyboardView.requestFocusFromTouch();*/
        } else {

            AlertDialog.Builder builder = new AlertDialog.Builder(Sales.this);
            builder.setMessage("Are you sure you want to quit ?").setCancelable(false).setPositiveButton("Yes",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {

                            alert.getButton(
                                    AlertDialog.BUTTON_POSITIVE)
                                    .setEnabled(false);


                            Intent in = new Intent(Sales.this, DailyEntryMainMenu.class);
                            startActivity(in);
                            Sales.this.finish();


                        }
                    }).setNegativeButton("No",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.cancel();
                        }
                    });
            alert = builder.create();
            alert.show();
        }
    }


    public String getCurrentTime() {

        Calendar m_cal = Calendar.getInstance();

        String intime = m_cal.get(Calendar.HOUR_OF_DAY) + ":"
                + m_cal.get(Calendar.MINUTE) + ":" + m_cal.get(Calendar.SECOND);

        return intime;

    }


    static class ViewHolder {


        TextView text, brand_name;

        LinearLayout l1;
        LinearLayout l2;
        EditText last_one, last_two, last_three;
        EditText stockQuantity, faceup;

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
                convertView = mInflater.inflate(R.layout.sales_row, null);
                holder = new ViewHolder();

                holder.brand_name = (TextView) convertView
                        .findViewById(R.id.brand);

                holder.text = (TextView) convertView
                        .findViewById(R.id.mainpage_rememberme);
                holder.faceup = (EditText) convertView
                        .findViewById(R.id.editText2);
                holder.l1 = (LinearLayout) convertView
                        .findViewById(R.id.mainpage_header);
                holder.l2 = (LinearLayout) convertView
                        .findViewById(R.id.mainpage_header2);


                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }


            if (currentVersion >= 11) {

                holder.faceup.setTextIsSelectable(true);
                holder.faceup.setRawInputType(InputType.TYPE_CLASS_TEXT);
            } else {

                holder.faceup.setInputType(0);
            }


            holder.faceup.setOnFocusChangeListener(new OnFocusChangeListener() {
                public void onFocusChange(View v, boolean hasFocus) {

                    if(hasFocus){
                        showKeyboardWithAnimation();
                    }
                    if (!hasFocus) {

                        hide();
                        final int position = v.getId();
                        final EditText Caption = (EditText) v;
                        String value1 = Caption.getText().toString();

                        if (value1.equals("")) {

                            sku_brand_list.get(position).setSales_qty("");
                            lv.invalidateViews();

                        } else {

                            sku_brand_list.get(position).setSales_qty(value1);
                            lv.invalidateViews();
                        }

                    }
                }
            });


            if (position == 0) {
                holder.brand_name.setText(sku_brand_list.get(position).getBrand());
                holder.text.setText(sku_brand_list.get(position).getSku_name());

            } else {

                if (sku_brand_list.get(position - 1).getBrand()
                        .equalsIgnoreCase(sku_brand_list.get(position).getBrand())) {
                    holder.brand_name.setText("");
                    holder.text.setText(sku_brand_list.get(position).getSku_name());

                } else {

                    holder.brand_name.setText(sku_brand_list.get(position).getBrand());
                    holder.text.setText(sku_brand_list.get(position).getSku_name());

                }

            }


            //					holder.stockQuantity.setText(sku_brand_list.get(position).getBefore_Stock());
            holder.faceup.setText(sku_brand_list.get(position).getSales_qty());


            if (position == row_pos - 1) {

                holder.l1.setBackgroundColor(Color.RED);
                holder.l2.setBackgroundColor(Color.RED);
            }


            holder.faceup.setId(position);


            return convertView;
        }

    }


    private void showKeyboardWithAnimation() {

        if (mKeyboardView.getVisibility() == View.GONE) {
            Animation animation = AnimationUtils
                    .loadAnimation(Sales.this,
                            R.anim.slide_in_bottom);
            mKeyboardView.showWithAnimation(animation);
        } else if (mKeyboardView.getVisibility() == View.INVISIBLE) {
            mKeyboardView.setVisibility(View.VISIBLE);
        }
    }


    public void ShowToast(String message) {

        LayoutInflater inflater = getLayoutInflater();
        View layout = inflater.inflate(R.layout.toastview,
                (ViewGroup) findViewById(R.id.toast_layout_root));

        ImageView image = (ImageView) layout.findViewById(R.id.image);
        image.setImageResource(R.drawable.ic_launcher);
        TextView text = (TextView) layout.findViewById(R.id.text);
        text.setText(message);

        Toast toast = new Toast(getApplicationContext());
        toast.setGravity(Gravity.CENTER_HORIZONTAL, 0, 0);
        toast.setDuration(Toast.LENGTH_LONG);
        toast.setView(layout);
        toast.show();

    }

    public boolean validate_allvalues() {
        boolean result = true;

        for (int i = 0; i < sku_brand_list.size(); i++) {

            if (sku_brand_list.get(i).getSales_qty().equals("")) {

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


                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setMessage("Are you sure you want to save").setCancelable(false).setPositiveButton("Yes",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {

                                if (update) {

                                    for (int i = 0; i < sku_brand_list.size(); i++) {
                                        db.updateSalesData(store_id, category_id, process_id, sku_brand_list.get(i).getSales_qty(),
                                                sku_brand_list.get(i).getSku_id());

                                        Intent in = new Intent(Sales.this, DailyEntryMainMenu.class);
                                        startActivity(in);
                                        Sales.this.finish();

                                    }


                                } else {
                                    db.InsertSalesData(store_id, sku_brand_list, username, category_id, process_id);

                                    Intent in = new Intent(Sales.this, DailyEntryMainMenu.class);
                                    startActivity(in);
                                    Sales.this.finish();
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
                Toast.makeText(Sales.this, "Please fill all data", Toast.LENGTH_LONG).show();
            }
        }


    }


    public static boolean isTablet(Context context) {
        return (context.getResources().getConfiguration().screenLayout
                & Configuration.SCREENLAYOUT_SIZE_MASK)
                >= Configuration.SCREENLAYOUT_SIZE_LARGE;
    }

    public void hide() {
        mKeyboardView.setVisibility(View.INVISIBLE);
		/*	// mKeyboardView.clearFocus();
		mKeyboardView.requestFocusFromTouch();*/

    }
}
