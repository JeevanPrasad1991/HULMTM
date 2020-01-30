package com.cpm.DailyEntry;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import com.cpm.Constants.CommonString;

import com.cpm.DailyEntry.AfterStockActivity.ViewHolder;
import com.cpm.database.GSKMTDatabase;
import com.cpm.delegates.CoverageBean;
import com.cpm.delegates.SkuBean;
import com.crashlytics.android.Crashlytics;
import com.example.gsk_mtt.R;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.inputmethodservice.Keyboard;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.provider.MediaStore;

import android.text.InputType;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class BeforeStock extends Activity implements OnClickListener {

    private SharedPreferences preferences;
    private SharedPreferences.Editor editor = null;
    ListView lv;
    Button save;
    ImageView cam1, cam2, cam3, cam4;
    public static ArrayList<SkuBean> sku_brand_list = new ArrayList<SkuBean>();
    GSKMTDatabase db;
    String store_id, category_id, process_id, intime, username, app_version, region_id, store_type_id;
    protected static String _pathforcheck = "";
    public String image1 = "", image2 = "", image3 = "", _path, date, image4 = "", imgDate, state_id, key_id, class_id;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.before_stock);

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

        imglayout = (LinearLayout) findViewById(R.id.imageLayout);
        save = (Button) findViewById(R.id.savebtn);
        cam1 = (ImageView) findViewById(R.id.cam1);
        cam2 = (ImageView) findViewById(R.id.cam2);
        cam3 = (ImageView) findViewById(R.id.cam3);
        cam4 = (ImageView) findViewById(R.id.cam4);
        sos_target = (TextView) findViewById(R.id.sos_target);
        imgDate = date.replace("/", "-");

        db = new GSKMTDatabase(BeforeStock.this);
        db.open();

        if ((new File(Environment.getExternalStorageDirectory() + "/MT_GSK_Images/")).exists()) {
            Log.i("directory is created", "directory is created");
        } else {
            (new File(Environment.getExternalStorageDirectory() + "/MT_GSK_Images/")).mkdir();
        }
        str = Environment.getExternalStorageDirectory() + "/MT_GSK_Images/";
        sos_target_list = db.getSOSTarget(store_id, category_id, process_id);
        coveragelist = db.getCoverageData(date, store_id, process_id);
        if (coveragelist.get(0).getImage_allow().equalsIgnoreCase("NOT Allowed")) {
            cam1.setEnabled(false);
            cam2.setEnabled(false);
            cam3.setEnabled(false);
        } else {
            cam1.setEnabled(true);
            cam2.setEnabled(true);
            cam3.setEnabled(true);
        }


        sku_brand_list = db.getBrandSkuList(category_id, store_id, process_id, state_id, store_type_id, key_id, class_id);

        if (sku_brand_list.size() > 0) {

            System.out.println("" + sku_brand_list.size());

            lv.setAdapter(new MyAdaptor(this));
        }

        allthreeDates = getpackedDate();
        save.setOnClickListener(this);
        currentVersion = android.os.Build.VERSION.SDK_INT;
        if (isTablet(getApplicationContext())) {
            mKeyboard = new Keyboard(this, R.xml.keyboard);

        } else {
            mKeyboard = new Keyboard(this, R.xml.keyboard1);

        }


        mKeyboardView = (CustomKeyboardView) findViewById(R.id.keyboard_view);
        mKeyboardView.setKeyboard(mKeyboard);
        mKeyboardView
                .setOnKeyboardActionListener(new BasicOnKeyboardActionListener(
                        this));

        if (sos_target_list.size() > 0) {
            sos_target.setText(sos_target_list.get(0).getSos_target());
        } else {
            sos_target.setText("0");
        }
    }

    @Override
    public void onBackPressed() {

//		super.onBackPressed();

        AlertDialog.Builder builder = new AlertDialog.Builder(BeforeStock.this);
        builder.setMessage("Are you sure you want to quit ?").setCancelable(false).setPositiveButton("Yes",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                        alert.getButton(
                                AlertDialog.BUTTON_POSITIVE)
                                .setEnabled(false);


                        Intent in = new Intent(BeforeStock.this, DailyEntryMainMenu.class);
                        startActivity(in);
                        BeforeStock.this.finish();


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


    public String getCurrentTime() {

        Calendar m_cal = Calendar.getInstance();

        String intime = m_cal.get(Calendar.HOUR_OF_DAY) + ":"
                + m_cal.get(Calendar.MINUTE) + ":" + m_cal.get(Calendar.SECOND);

        return intime;

    }


    public void onButtonClick(View v) {
        switch (v.getId()) {

            case R.id.cam1:
                _pathforcheck = store_id + "_1_BS" + process_id + username + imgDate + category_id + process_id + ".jpg";
                _path = str + _pathforcheck;
                startCameraActivity();
                break;
            case R.id.cam2:
                _pathforcheck = store_id + "_2_BS" + process_id + username + imgDate + category_id + process_id + ".jpg";
                _path = str + _pathforcheck;
                startCameraActivity();
                break;
            case R.id.cam3:
                _pathforcheck = store_id + "_3_BS" + process_id + username + imgDate + category_id + process_id + ".jpg";
                _path = str + _pathforcheck;
                startCameraActivity();
                break;


            case R.id.cam4:
                _pathforcheck = store_id + "_4_BS" + process_id + username + imgDate + category_id + process_id + ".jpg";
                _path = str + _pathforcheck;
                startCameraActivity();
                break;

        }
    }

    protected void startCameraActivity() {

        try {
            Log.i("MakeMachine", "startCameraActivity()");
            File file = new File(_path);
            Uri outputFileUri = Uri.fromFile(file);

            Intent intent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, outputFileUri);

            startActivityForResult(intent, 0);
        } catch (Exception e) {
            e.printStackTrace();
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
                try {
                    if (_pathforcheck != null && !_pathforcheck.equals("")) {
                        if (new File((str + _pathforcheck).trim()).exists()) {

                            img1 = _pathforcheck;
                            String value = img1.substring(img1.indexOf("_") + 1, img1.lastIndexOf("_"));
                            if (value != null) {
                                if (value.equalsIgnoreCase("1")) {


    //                                                                                       stockdata.get(0).setCamImage1(img1.replace("_", ""));
                                    image1 = img1;
                                    cam1.setBackgroundResource(R.drawable.camera_tick_ico);
                                    imglayout.setBackgroundColor(Color.WHITE);
                                } else if (value.equalsIgnoreCase("2")) {
    //                                                                                          stockdata.get(0).setCamImage2(img1.replace("_", ""));
                                    image2 = img1;
                                    cam2.setBackgroundResource(R.drawable.camera_tick_ico);
                                    imglayout.setBackgroundColor(Color.WHITE);
                                } else if (value.equalsIgnoreCase("3")) {
    //                                                                            stockdata.get(0).setCamImage2(img1.replace("_", ""));
                                    image3 = img1;
                                    cam3.setBackgroundResource(R.drawable.camera_tick_ico);
                                    imglayout.setBackgroundColor(Color.WHITE);
                                } else {
    //                                                                                          stockdata.get(0).setCamImage3(img1.replace("_", ""));
                                    image4 = img1;
                                    cam4.setBackgroundResource(R.drawable.camera_tick_ico);
                                    imglayout.setBackgroundColor(Color.WHITE);
                                }
                            }
                            //                            audit_lv.invalidateViews();
                            _pathforcheck = "";
                            break;

                        }
                    }
                } catch (Exception e) {
                    Crashlytics.logException(e);
                    e.printStackTrace();
                }
                break;
        }
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
                convertView = mInflater.inflate(R.layout.sku_row, null);
                holder = new ViewHolder();

                holder.brand_name = (TextView) convertView
                        .findViewById(R.id.brand);

                holder.text = (TextView) convertView
                        .findViewById(R.id.mainpage_rememberme);
                holder.stockQuantity = (EditText) convertView
                        .findViewById(R.id.editText2);
                holder.faceup = (EditText) convertView
                        .findViewById(R.id.editText1);
                holder.l1 = (LinearLayout) convertView
                        .findViewById(R.id.mainpage_header);
                holder.l2 = (LinearLayout) convertView
                        .findViewById(R.id.mainpage_header2);

                holder.last_one = (EditText) convertView
                        .findViewById(R.id.last_one);

                holder.last_two = (EditText) convertView
                        .findViewById(R.id.last_two);

                holder.last_three = (EditText) convertView
                        .findViewById(R.id.last_three);

                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }


            if (currentVersion >= 11) {
                holder.stockQuantity.setTextIsSelectable(true);
                holder.stockQuantity.setRawInputType(InputType.TYPE_CLASS_TEXT);


                holder.faceup.setTextIsSelectable(true);
                holder.faceup.setRawInputType(InputType.TYPE_CLASS_TEXT);
            } else {
                holder.stockQuantity.setInputType(0);
                holder.faceup.setInputType(0);
            }


            holder.last_one.setHint(allthreeDates.get(0));
            holder.last_two.setHint(allthreeDates.get(1));
            holder.last_three.setHint("< " + allthreeDates.get(2).substring(0, 6));


            holder.stockQuantity.setOnFocusChangeListener(new OnFocusChangeListener() {
                public void onFocusChange(View v, boolean hasFocus) {

                    showKeyboardWithAnimation();

                    if (!hasFocus) {
                        final int position = v.getId();
                        final EditText Caption = (EditText) v;
                        String value1 = Caption.getText().toString();

                        if (value1.equals("")) {

                            sku_brand_list.get(position).setBefore_Stock("");
                            lv.invalidateViews();

                        } else {

                            sku_brand_list.get(position).setBefore_Stock(value1);
                            lv.invalidateViews();
                        }

                    }
                }
            });

            holder.faceup.setOnFocusChangeListener(new OnFocusChangeListener() {
                public void onFocusChange(View v, boolean hasFocus) {

                    showKeyboardWithAnimation();

                    if (!hasFocus) {
                        final int position = v.getId();
                        final EditText Caption = (EditText) v;
                        String value1 = Caption.getText().toString();

                        if (value1.equals("")) {

                            sku_brand_list.get(position).setBefore_faceup("");
                            lv.invalidateViews();

                        } else {

                            sku_brand_list.get(position).setBefore_faceup(value1);
                            lv.invalidateViews();
                        }

                    }
                }
            });


            holder.last_one.setOnFocusChangeListener(new OnFocusChangeListener() {
                public void onFocusChange(View v, boolean hasFocus) {

                    if (!hasFocus) {
                        final int position = v.getId();
                        final EditText Caption = (EditText) v;
                        String value1 = Caption.getText().toString();
                        if (value1.equals("")) {
                            sku_brand_list.get(position).setBLAST_THREE("");
                        } else {

                            sku_brand_list.get(position).setBLAST_THREE(value1);

                        }

                    }
                }
            });

            holder.last_two.setOnFocusChangeListener(new OnFocusChangeListener() {
                public void onFocusChange(View v, boolean hasFocus) {
                    if (!hasFocus) {
                        final int position = v.getId();
                        final EditText Caption = (EditText) v;
                        String value1 = Caption.getText().toString();
                        if (value1.equals("")) {
                            sku_brand_list.get(position).setBTHREE_TO_SIX("");
                        } else {
                            sku_brand_list.get(position).setBTHREE_TO_SIX(value1);

                        }

                    }
                }
            });

            holder.last_three
                    .setOnFocusChangeListener(new OnFocusChangeListener() {
                        public void onFocusChange(View v, boolean hasFocus) {
                            if (!hasFocus) {
                                final int position = v.getId();
                                final EditText Caption = (EditText) v;
                                String value1 = Caption.getText().toString();
                                if (value1.equals("")) {
                                    sku_brand_list.get(position).setBMORE_SIX("");
                                } else {
                                    sku_brand_list.get(position).setBMORE_SIX(value1);
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


            holder.stockQuantity.setText(sku_brand_list.get(position).getBefore_Stock());
            holder.faceup.setText(sku_brand_list.get(position).getBefore_faceup());

            holder.last_one.setText(sku_brand_list.get(position).getBLAST_THREE());
            holder.last_two.setText(sku_brand_list.get(position).getBTHREE_TO_SIX());
            holder.last_three.setText(sku_brand_list.get(position).getBLAST_THREE());


            if (!sku_brand_list.get(position).getCompany_id().equalsIgnoreCase("1")) {

                sku_brand_list.get(position).setBefore_Stock("0");
                sku_brand_list.get(position).setBLAST_THREE("0");
                sku_brand_list.get(position).setBMORE_SIX("0");
                sku_brand_list.get(position).setBTHREE_TO_SIX("0");

                holder.l1.setBackgroundColor(Color.YELLOW);
                holder.l2.setVisibility(View.GONE);
                holder.stockQuantity.setVisibility(View.INVISIBLE);


                holder.faceup.setEnabled(true);

            } else {

					/*	holder.stockQuantity.setVisibility(View.VISIBLE);
                        holder.l1.setBackgroundColor(Color.WHITE);
	                    holder.l2.setVisibility(View.VISIBLE);
	                    holder.l2.setBackgroundColor(Color.WHITE);*/

                if (sku_brand_list.get(position).getBefore_Stock().equalsIgnoreCase("0")) {

                    holder.stockQuantity.setVisibility(View.VISIBLE);
                    holder.l1.setBackgroundColor(Color.WHITE);
                    holder.l2.setVisibility(View.VISIBLE);
                    holder.l2.setBackgroundColor(Color.WHITE);

                    sku_brand_list.get(position).setBefore_faceup("0");
                            /*	 sku_brand_list.get(position).setALAST_THREE("0");
			                	 sku_brand_list.get(position).setATHREE_TO_SIX("0");
			                	 sku_brand_list.get(position).setAMORE_SIX("0")*/
                    ;

                    holder.faceup.setText("0");
                    holder.faceup.setEnabled(false);
			                	/* holder.last_one.setText("0");
			                	 holder.last_one.setEnabled(false);
			                	 holder.last_two.setText("0");
			                	 holder.last_two.setEnabled(false);
			                	 holder.last_three.setText("0");
			                	 holder.last_three.setEnabled(false);*/


                } else {

                    holder.stockQuantity.setVisibility(View.VISIBLE);
                    holder.l1.setBackgroundColor(Color.WHITE);
                    holder.l2.setVisibility(View.VISIBLE);
                    holder.l2.setBackgroundColor(Color.WHITE);
                    holder.faceup.setEnabled(true);
			                	/*holder.last_one.setEnabled(true);
			                	holder.last_two.setEnabled(true);
			                	holder.last_three.setEnabled(true);
			                	 */

                }


            }


            if (position == row_pos - 1) {

                holder.l1.setBackgroundColor(Color.RED);
                holder.l2.setBackgroundColor(Color.RED);
            }

            holder.stockQuantity.setId(position);
            holder.faceup.setId(position);

            holder.last_one.setId(position);
            holder.last_two.setId(position);
            holder.last_three.setId(position);


            return convertView;
        }

    }


    private void showKeyboardWithAnimation() {

        if (mKeyboardView.getVisibility() == View.GONE) {
            Animation animation = AnimationUtils
                    .loadAnimation(BeforeStock.this,
                            R.anim.slide_in_bottom);
            mKeyboardView.showWithAnimation(animation);
        } else if (mKeyboardView.getVisibility() == View.INVISIBLE) {
            mKeyboardView.setVisibility(View.VISIBLE);
        }
    }


    public ArrayList<String> getpackedDate() {

        ArrayList<String> threeDates = new ArrayList<String>();

        SimpleDateFormat df = new SimpleDateFormat("MMM-yy");
        //String formattedDate = df.format(c.getTime());
        String firstto_3date = "";
        String thrrdto_sixdate = "";
        String sixtonine = "";


        Calendar cal = Calendar.getInstance();
        String formattedDatecurrent = df.format(cal.getTime());
        firstto_3date = formattedDatecurrent;
        // substract 7 days

        cal.set(Calendar.DAY_OF_MONTH, -1);

        // convert to date
        Date myDate = cal.getTime();

        String formattedDate2 = df.format(cal.getTime());

        cal.set(Calendar.DAY_OF_MONTH, -1);

        // convert to date

        String formattedDate3 = df.format(cal.getTime());

        firstto_3date += " " + formattedDate3;
        threeDates.add(firstto_3date);

        cal.set(Calendar.DAY_OF_MONTH, -1);

        // convert to date
        String formattedDate4 = df.format(cal.getTime());

        thrrdto_sixdate = formattedDate4;
        cal.set(Calendar.DAY_OF_MONTH, -1);

        // convert to date
        String formattedDate5 = df.format(cal.getTime());
        cal.set(Calendar.DAY_OF_MONTH, -1);

        // convert to date
        String formattedDate6 = df.format(cal.getTime());
        thrrdto_sixdate += " " + formattedDate6;
        threeDates.add(thrrdto_sixdate);

        cal.set(Calendar.DAY_OF_MONTH, -1);

        // convert to date
        String formattedDate7 = df.format(cal.getTime());
        sixtonine = formattedDate7;

        cal.set(Calendar.DAY_OF_MONTH, -1);

        // convert to date
        String formattedDate8 = df.format(cal.getTime());
        cal.set(Calendar.DAY_OF_MONTH, -1);

        // convert to date
        String formattedDate9 = df.format(cal.getTime());
        sixtonine += " " + formattedDate9;
        threeDates.add(sixtonine);

        // convert to date
        return threeDates;
    }


    public boolean validate_values() {
        boolean result = true;

        for (int i = 0; i < sku_brand_list.size(); i++) {

            if (sku_brand_list.get(i).getBefore_Stock().equals("")) {

                if (!sku_brand_list.get(i).getBefore_faceup().equals("")) {
                    result = false;
                    break;
                }

            }

            if (sku_brand_list.get(i).getBefore_faceup().equals("")) {

                if (!sku_brand_list.get(i).getBefore_Stock().equals("")) {
                    result = false;
                    break;
                }

            }

        }

        return result;

    }


    public boolean condition() {
        boolean result = true;

			  /*if (!sku_brand_list.get(position).getCompany_id().equalsIgnoreCase("1")) {
             	 
             	 sku_brand_list.get(position).setBefore_Stock("0");
             	 sku_brand_list.get(position).setBLAST_THREE("0");
             	 sku_brand_list.get(position).setBMORE_SIX("0");
             	 sku_brand_list.get(position).setBTHREE_TO_SIX("0");
             	 
             	 holder.l1.setBackgroundColor(Color.BLUE);
                  holder.l2.setVisibility(View.GONE);
                  holder.stockQuantity.setVisibility(View.INVISIBLE);

				} else {
					
					holder.stockQuantity.setVisibility(View.VISIBLE);
                 holder.l1.setBackgroundColor(Color.WHITE);
                 holder.l2.setVisibility(View.VISIBLE);
                 holder.l2.setBackgroundColor(Color.WHITE);

				}
			*/

        for (int i = 0; i < sku_brand_list.size(); i++) {

            if (sku_brand_list.get(i).getCompany_id().equalsIgnoreCase("1")) {
                if (!sku_brand_list.get(i).getBefore_Stock().equalsIgnoreCase("")
                        && !sku_brand_list.get(i).getBefore_faceup().equalsIgnoreCase("")) {
                    if (Integer.parseInt(sku_brand_list.get(i).getBefore_faceup()) > (Integer
                            .parseInt(sku_brand_list.get(i).getBefore_Stock()))) {
                        row_pos = i + 1;
                        lv.invalidateViews();
                        result = false;
                        break;
                    }
                }
            } else {
                result = true;
            }


        }

        return result;
    }


    public boolean condition_lastmonth_stock() {
        boolean result = true;

        for (int i = 0; i < sku_brand_list.size(); i++) {
            if (!sku_brand_list.get(i).getBLAST_THREE().equalsIgnoreCase("")
                    && !sku_brand_list.get(i).getBMORE_SIX().equalsIgnoreCase("")
                    && !sku_brand_list.get(i).getBTHREE_TO_SIX().equalsIgnoreCase("")) {
                if ((Integer.parseInt(sku_brand_list.get(i).getBLAST_THREE())
                        + Integer.parseInt(sku_brand_list.get(i).getBMORE_SIX()) + Integer
                        .parseInt(sku_brand_list.get(i).getBTHREE_TO_SIX())) != (Integer
                        .parseInt(sku_brand_list.get(i).getBefore_Stock()))) {
                    row_pos = i + 1;
                    result = false;
                    break;
                }
            }

        }

        return result;
    }


    public boolean validate_allvalues_laststock() {
        boolean result = true;

        for (int i = 0; i < sku_brand_list.size(); i++) {

            if (sku_brand_list.get(i).getBLAST_THREE().equals("")
                    || sku_brand_list.get(i).getBMORE_SIX().equals("")
                    || sku_brand_list.get(i).getBTHREE_TO_SIX().equals("")) {
                row_pos = i + 1;
                result = false;
                break;
            }

        }

        return result;

    }

    public boolean validate_primaryWindows() {
        boolean result = true;

        if (!coveragelist.get(0).getImage_allow().equalsIgnoreCase("NOT Allowed")) {

            if (image1.equalsIgnoreCase("") && image2.equalsIgnoreCase("") && image3.equalsIgnoreCase("") && image4.equalsIgnoreCase("")) {
                result = false;
                imglayout.setBackgroundColor(Color.RED);
            } else {
                imglayout.setBackgroundColor(Color.WHITE);
            }


        }
        return result;

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

            if (sku_brand_list.get(i).getBefore_Stock().equals("")
                    || sku_brand_list.get(i).getBefore_faceup().equals("")
                    ) {

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

            /*
            * if (check_condition()) { Toast.makeText(getApplicationContext(),
            * AlertMessage.MESSAGE_INVALID_DATA, Toast.LENGTH_SHORT) .show(); }
            * else {
            */

            if (validate_allvalues()) {
                if (validate_values()) {
                    if (condition()) {
//                if (condition_lastmonth_stock()) {

                        if (validate_primaryWindows()) {

                            AlertDialog.Builder builder = new AlertDialog.Builder(this);
                            builder.setMessage("Are you sure you want to save").setCancelable(false).setPositiveButton("Yes",
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int id) {


//                        		db.InsertCoverage(store_id, date, intime, getCurrentTime(),
//										reason_id, remark,username,app_version,process_id);

                                            db.InsertBeforeStockImagese(store_id, category_id, image1, image2, image3, image4
                                                    , username, process_id);

                                            db.InsertBeforerStockData(store_id, sku_brand_list, username, category_id, process_id);


                                            Intent in = new Intent(BeforeStock.this, DailyEntryMainMenu.class);
                                            startActivity(in);
                                            BeforeStock.this.finish();
                        	 
                        	 
                        	 
                        	 

                          /*  database.open();
                          if (check) {
                             database.UpdateSkuData( getMid(),store_id, stockdata);
                              database.UpdatePrimaryWindowImages(store_id,image1,image2,image3);
                                                                                                                                                                               
                          	} else {
                            database.InsertAssetData( getMid(),store_id, stockdata);
                                                                                                                                                                                            
                            database.InsertPrimaryWindowImages(store_id,image1,image2,image3);
                          	}

                            Intent DailyEntryMenu = new Intent(SkuDisplay.this, DailyentryMenuActivity.class);
                             startActivity(DailyEntryMenu);*/

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


                            Toast.makeText(getBaseContext(), "Please click image! "
                                    , Toast.LENGTH_LONG).show();
                        }

//            	  }


                    } else {

                        Toast.makeText(getBaseContext(), "Incorrect Data ! " + row_pos, Toast.LENGTH_LONG).show();

                    }
                } else {
                    Toast.makeText(getBaseContext(), "Incorrect data ! ", Toast.LENGTH_LONG).show();
                }
            } else {
                Toast.makeText(getBaseContext(), "Enter Data In All Fields",
                        Toast.LENGTH_LONG).show();
            }

        }

    }

    public static boolean isTablet(Context context) {
        return (context.getResources().getConfiguration().screenLayout
                & Configuration.SCREENLAYOUT_SIZE_MASK)
                >= Configuration.SCREENLAYOUT_SIZE_LARGE;
    }

}
