package com.cpm.DailyEntry;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;

import com.cpm.Constants.CommonFunctions;
import com.cpm.Constants.CommonString;
import com.cpm.database.GSKMTDatabase;
import com.cpm.delegates.SkuBean;
import com.cpm.delegates.TOTBean;
import com.cpm.message.AlertMessage;
import com.example.gsk_mtt.R;
import com.google.firebase.crashlytics.FirebaseCrashlytics;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.inputmethodservice.Keyboard;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.provider.MediaStore;

import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

public class AfterTOT extends Activity {
    private SharedPreferences preferences;
    static int mposition = -1;
    public static String store_id, category_id, process_id, date, intime, username, app_version, imgDate, storetype_id, region_id, state_id, key_id, class_id;
    public static String img1 = "", img2 = "", img3 = "";
    public String _path = "", reason_id = "0", remark = "", storename, cat_name;
    private static String str;
    String brand_name = "", brand_id = "", sku_name = "", sku_id = "";
    int row_pos;
    Spinner brand, sku;
    EditText quantity;
    MyAdaptorStock adapterData;
    ArrayList<SkuBean> brand_list = new ArrayList<SkuBean>();
    ArrayList<SkuBean> sku_list = new ArrayList<SkuBean>();
    ArrayList<TOTBean> list = new ArrayList<TOTBean>();
    public ListView listview;
    Button save_btnn;
    private ArrayAdapter<CharSequence> brandAdaptor, skuAdaptor;
    protected static String _pathforcheck = "";
    protected static String _pathforcheck2 = "";
    protected static String _pathforcheck3 = "";
    Button save_btn;
    ListView lv;
    GSKMTDatabase db;
    public static ArrayList<TOTBean> data = new ArrayList<TOTBean>();
    public ArrayList<TOTBean> question_list = new ArrayList<TOTBean>();
    public ArrayList<TOTBean> stocklist = new ArrayList<TOTBean>();
    Boolean update = false;
    Boolean update1 = false;
    Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.after_tot);
        context = this;
        save_btn = (Button) findViewById(R.id.save);
        lv = (ListView) findViewById(R.id.list);
        preferences = PreferenceManager.getDefaultSharedPreferences(context);
        store_id = preferences.getString(CommonString.KEY_STORE_ID, null);
        category_id = preferences.getString(CommonString.KEY_CATEGORY_ID, null);
        process_id = preferences.getString(CommonString.KEY_PROCESS_ID, null);
        date = preferences.getString(CommonString.KEY_DATE, null);
        intime = preferences.getString(CommonString.KEY_IN_TIME, null);
        username = preferences.getString(CommonString.KEY_USERNAME, null);
        app_version = preferences.getString(CommonString.KEY_VERSION, null);
        storetype_id = preferences.getString(CommonString.storetype_id, null);
        region_id = preferences.getString(CommonString.region_id, null);
        ///change by jeevan RAna
        state_id = preferences.getString(CommonString.KEY_STATE_ID, null);
        key_id = preferences.getString(CommonString.KEY_ID, null);
        class_id = preferences.getString(CommonString.KEY_CLASS_ID, null);
        storename = preferences.getString(CommonString.KEY_STORE_NAME, "");
        cat_name = preferences.getString(CommonString.KEY_CATEGORY_NAME, "");

        db = new GSKMTDatabase(context);
        db.open();
        if ((new File(Environment.getExternalStorageDirectory() + "/MT_GSK_Images/")).exists()) {
            Log.i("directory is created", "directory is created");
        } else {
            (new File(Environment.getExternalStorageDirectory() + "/MT_GSK_Images/")).mkdir();
        }
        str = Environment.getExternalStorageDirectory() + "/MT_GSK_Images/";
        imgDate = date.replace("/", "-");
        db.open();
        data = db.getInsertedAfterTOTData(store_id, category_id, process_id); //need to add_btn the process id
        if (data.size() == 0) {
            data = db.getTOTData(store_id, process_id, category_id);
            for (int i2 = 0; i2 < data.size(); i2++) {

                data.get(i2).setCamera1("NO");
                data.get(i2).setCamera2("NO");
                data.get(i2).setCamera3("NO");

                data.get(i2).setImage1("");
                data.get(i2).setImage2("");
                data.get(i2).setImage3("");

            }
        } else {
            update = true;
            save_btn.setText("update");
            for (int i2 = 0; i2 < data.size(); i2++) {
                if (!data.get(i2).getImage1().equals("")) {
                    data.get(i2).setCamera1("YES");
                    data.get(i2).setImage1(data.get(i2).getImage1());
                } else {
                    data.get(i2).setCamera1("NO");
                    data.get(i2).setImage1("");
                }
                if (!data.get(i2).getImage2().equals("")) {
                    data.get(i2).setCamera2("YES");
                    data.get(i2).setImage2(data.get(i2).getImage2());
                } else {
                    data.get(i2).setCamera2("NO");
                    data.get(i2).setImage2("");
                }


                if (!data.get(i2).getImage3().equals("")) {

                    data.get(i2).setCamera3("YES");
                    data.get(i2).setImage3(data.get(i2).getImage3());
                } else {
                    data.get(i2).setCamera3("NO");
                    data.get(i2).setImage3("");
                }

            }
        }


        if (data.size() > 0) {
            lv.setAdapter(new MyAdaptor(context));
            System.out.println("" + data.size());
        }

        save_btn.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                lv.clearFocus();
                lv.invalidateViews();
                if (!check_condition()) {
                    Toast.makeText(context, AlertMessage.MESSAGE_INVALID_DATA, Toast.LENGTH_SHORT).show();
                } else {
                    if (!check_conditionForImages()) {
                        Toast.makeText(context, AlertMessage.MESSAGE_IMAGE, Toast.LENGTH_SHORT).show();
                    } else {
                        if (!check_conditionForQuestions()) {
                            Toast.makeText(context, AlertMessage.MESSAGE_GAPS, Toast.LENGTH_SHORT).show();
                        } else {

                            if (!check_conditionForSTOCK()) {
                                Toast.makeText(context, AlertMessage.MESSAGE_TOT_STOCK, Toast.LENGTH_SHORT).show();
                            } else {
                                AlertDialog.Builder builder = new AlertDialog.Builder(context).setTitle(R.string.parinamm);
                                builder.setMessage("Do you want to save the data ?")
                                        .setCancelable(false)
                                        .setPositiveButton("OK",
                                                new DialogInterface.OnClickListener() {
                                                    public void onClick(DialogInterface dialog,
                                                                        int id) {
                                                        if (update) {
                                                            for (int i = 0; i < data.size(); i++) {
                                                                db.open();
                                                                db.updateAfterTOTData(store_id, data.get(i).getBrand_id(), data.get(i).getDisplay_id(),
                                                                        data.get(i).getAFTER_QTY(), data.get(i).getStock_count(), data.get(i).getImage1(),
                                                                        data.get(i).getImage2(), data.get(i).getImage3(), process_id);

                                                            }

                                                            Intent i = new Intent(context, DailyEntryMainMenu.class);
                                                            startActivity(i);
                                                            AfterTOT.this.finish();
                                                        } else {
                                                            db.open();
                                                            db.InsertAfterTOTData(store_id, data, category_id, process_id);
                                                            Intent i = new Intent(context, DailyEntryMainMenu.class);
                                                            startActivity(i);
                                                            AfterTOT.this.finish();
                                                        }

                                                    }
                                                })
                                        .setNegativeButton("Cancel",
                                                new DialogInterface.OnClickListener() {
                                                    public void onClick(DialogInterface dialog,
                                                                        int id) {
                                                        dialog.cancel();
                                                    }
                                                });
                                AlertDialog alert = builder.create();
                                alert.show();
                            }
                        }
                    }
                }


            }
        });


        lv.setOnScrollListener(new AbsListView.OnScrollListener() {
            private int mLastFirstVisibleItem;

            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                lv.clearFocus();

            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                lv.invalidate();
                if (mLastFirstVisibleItem < firstVisibleItem) {

                }
                if (mLastFirstVisibleItem > firstVisibleItem) {

                }
                mLastFirstVisibleItem = firstVisibleItem;

            }
        });


    }


    @Override
    public void onBackPressed() {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage("Are you sure you want to quit ?").setCancelable(false).setPositiveButton("Yes",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                        Intent in = new Intent(context, DailyEntryMainMenu.class);
                        startActivity(in);
                        AfterTOT.this.finish();

                    }
                }).setNegativeButton("No",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
        AlertDialog alert = builder.create();

        alert.show();
//			}
    }

    static class ViewHolder {
        TextView brand_name, display_name, target_quantity, question_text;
        ImageView image1, image2, image3;
        EditText actual_quanity, stock_count;
        Button question_lv, refImage, stock_btn;
        ToggleButton answer;

    }


    public String getCurrentTime() {

        Calendar m_cal = Calendar.getInstance();

        String intime = m_cal.get(Calendar.HOUR_OF_DAY) + ":"
                + m_cal.get(Calendar.MINUTE) + ":" + m_cal.get(Calendar.SECOND);

        return intime;

    }

    public boolean check_condition() {
        boolean result = true;
        for (int i = 0; i < data.size(); i++) {
            if (!data.get(i).getAFTER_QTY().equals("")) {
                if (Integer.parseInt(data.get(i).getAFTER_QTY()) > Integer.parseInt(data.get(i).getTrg_quantity())) {
                    result = false;
                    break;
                } else {
                    result = true;
                }
            } else {
                result = false;
                break;
            }
        }

        return result;

    }


    public boolean check_conditionForQuestions() {
        boolean result = true;

        for (int i = 0; i < data.size(); i++) {
            db.open();
            question_list = db.getInsertedTOTQuestionsData(store_id, data.get(i).getDisplay_id(), category_id, data.get(i).getUnique_id(), process_id);
            if (question_list.size() == 0) {
                result = false;
                break;

            }

        }

        return result;

    }


    public boolean check_conditionForSTOCK() {
        boolean result = true;
        for (int i = 0; i < data.size(); i++) {
            db.open();
            stocklist = db.getTOTStockEntryDetail(store_id, category_id, process_id, data.get(i).getDisplay_id(),
                    data.get(i).getUnique_id());
            if (!data.get(i).getAFTER_QTY().equals("0")) {
                if (stocklist.size() == 0) {
                    result = false;
                    break;
                }
            } else if (data.get(i).getAFTER_QTY().equals("0")) {
                if (stocklist.size() == 0) {
                    result = true;

                }

            }


        }

        return result;

    }


    public boolean check_conditionForImages() {
        boolean result = true;
        for (int i = 0; i < data.size(); i++) {
            if (data.get(i).getAFTER_QTY().equals("0")) {
                if (data.get(i).getImage1().equals("") && data.get(i).getImage2().equals("")
                        && data.get(i).getImage3().equals("")) {
                    result = true;
                }
            } else if (!data.get(i).getAFTER_QTY().equals("0")) {
                if (data.get(i).getCamera1().equalsIgnoreCase("NO") ||
                        data.get(i).getCamera2().equalsIgnoreCase("NO")
                        || data.get(i).getCamera3().equalsIgnoreCase("NO") ||
                        data.get(i).getCamera1().equalsIgnoreCase("NA") ||
                        data.get(i).getCamera2().equalsIgnoreCase("NA") ||
                        data.get(i).getCamera3().equalsIgnoreCase("NA")) {
                    result = false;
                    break;

                }
            }
        }

        return result;


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
            return data.size();
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
            final ViewHolder holder;


            if (convertView == null) {
                convertView = mInflater.inflate(R.layout.after_tot_viewlist, null);
                holder = new ViewHolder();
                holder.brand_name = (TextView) convertView.findViewById(R.id.brand_name);
                holder.display_name = (TextView) convertView.findViewById(R.id.display_name);
                holder.target_quantity = (TextView) convertView.findViewById(R.id.trgt_quantity);
                holder.actual_quanity = (EditText) convertView.findViewById(R.id.actual_quantity);
                holder.question_lv = (Button) convertView.findViewById(R.id.questions_listview);
                holder.refImage = (Button) convertView.findViewById(R.id.refimage);
                holder.stock_btn = (Button) convertView.findViewById(R.id.stock_fill);
                holder.image1 = (ImageView) convertView.findViewById(R.id.cam1);
                holder.image2 = (ImageView) convertView.findViewById(R.id.cam2);
                holder.image3 = (ImageView) convertView.findViewById(R.id.cam3);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            holder.actual_quanity.setOnFocusChangeListener(new OnFocusChangeListener() {
                public void onFocusChange(View v, boolean hasFocus) {
                    if (!hasFocus) {
                        final int position = v.getId();
                        final EditText Caption = (EditText) v;
                        String value1 = Caption.getText().toString();
                        if (value1.equals("")) {
                            data.get(position).setAFTER_QTY("");
                        } else {
                            data.get(position).setAFTER_QTY(value1);
                        }
                    }
                }
            });


            holder.refImage.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {
                    if (data.get(position).getImage_url() != null && !data.get(position).getImage_url().equals("NA")) {
                        if (new File(CommonString.FILE_PATH_PLANOGRAM + data.get(position).getImage_url()).exists()) {
                            final Dialog dialog = new Dialog(AfterTOT.this);
                            dialog.setContentView(R.layout.popup);
                            ImageView refimage = (ImageView) dialog.findViewById(R.id.displayimage);
                            dialog.setTitle("Reference Image");
                            if (data.get(position).getImage_url() != null && !data.get(position).getImage_url().equals("NA")) {
                                Bitmap bitmap = BitmapFactory.decodeFile(CommonString.FILE_PATH_PLANOGRAM + data.get(position).getImage_url());
                                Drawable mDrawable = new BitmapDrawable(getResources(), bitmap);
                                refimage.setImageDrawable(mDrawable);
                                dialog.show();
                            }
                        } else {
                            Toast.makeText(AfterTOT.this, "Refrence image not availeble", Toast.LENGTH_LONG).show();
                        }
                    } else {
                        Toast.makeText(AfterTOT.this, "Refrence image not availeble", Toast.LENGTH_LONG).show();
                    }


                }
            });


            if (data.get(position).getAFTER_QTY().equals("")) {
                holder.actual_quanity.setText("");

            } else {
                holder.actual_quanity.setText(data.get(position).getAFTER_QTY());
            }

            if (data.get(position).getAFTER_QTY().equals("0")) {
                ArrayList<TOTBean> totStocklist = new ArrayList<TOTBean>();
                db.open();
                totStocklist = db.getTOTStockEntryDetail(store_id, category_id, process_id, data.get(position).getDisplay_id(),
                        data.get(position).getUnique_id());
                if (totStocklist.size() > 0) {
                    db.open();
                    db.deleteTOTStockEntry(store_id, category_id, process_id, data.get(position).getDisplay_id(),
                            data.get(position).getUnique_id());
                }

                holder.stock_btn.setEnabled(false);
                holder.image1.setImageResource(R.drawable.camera_disabled);
                holder.image2.setImageResource(R.drawable.camera_disabled);
                holder.image3.setImageResource(R.drawable.camera_disabled);
                data.get(position).setImage1("");
                data.get(position).setImage2("");
                data.get(position).setImage3("");
                data.get(position).setCamera1("NA");
                data.get(position).setCamera2("NA");
                data.get(position).setCamera3("NA");

                holder.image1.setEnabled(false);
                holder.image2.setEnabled(false);
                holder.image3.setEnabled(false);

            } else if (data.get(position).getAFTER_QTY().equals("") || !data.get(position).getAFTER_QTY().equals("0")) {
                holder.image1.setImageResource(R.drawable.camera_ico);
                holder.image2.setImageResource(R.drawable.camera_ico);
                holder.image3.setImageResource(R.drawable.camera_ico);
                holder.image1.setEnabled(true);
                holder.image2.setEnabled(true);
                holder.image3.setEnabled(true);
                holder.stock_btn.setEnabled(true);

            } else {
                holder.image1.setImageResource(R.drawable.camera_ico);
                holder.image2.setImageResource(R.drawable.camera_ico);
                holder.image3.setImageResource(R.drawable.camera_ico);
                holder.image1.setEnabled(true);
                holder.image2.setEnabled(true);
                holder.image3.setEnabled(true);
            }


            holder.image1.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {
                    mposition = position;
                    _pathforcheck = store_id + "_" + process_id + username + imgDate + "left" +
                            data.get(position).getBrand_id() + data.get(position).getDisplay_id() + ".jpg";
                    _path = str + _pathforcheck;
                    CommonFunctions.startAnncaCameraActivity(mcontext, _path);

                }
            });

            holder.image2.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {
                    mposition = position;
                    _pathforcheck2 = store_id + "_" + process_id + username + imgDate + "front" +
                            data.get(position).getBrand_id() + data.get(position).getDisplay_id()
                            + ".jpg";
                    _path = str + _pathforcheck2;
                    CommonFunctions.startAnncaCameraActivity(mcontext, _path);

                }
            });


            holder.image3.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {
                    mposition = position;
                    _pathforcheck3 = store_id + "_" + process_id + username + imgDate + "right" +
                            data.get(position).getBrand_id() + data.get(position).getDisplay_id() + ".jpg";
                    _path = str + _pathforcheck3;
                    CommonFunctions.startAnncaCameraActivity(mcontext, _path);

                }
            });


            if (!img1.equals("")) {
                if (position == mposition) {
                    data.get(position).setCamera1("YES");
                    data.get(position).setImage1(img1);
                    img1 = "";

                }
            }


            if (!img2.equals("")) {
                if (position == mposition) {
                    data.get(position).setCamera2("YES");
                    data.get(position).setImage2(img2);
                    img2 = "";

                }
            }


            if (!img3.equals("")) {
                if (position == mposition) {
                    data.get(position).setCamera3("YES");
                    data.get(position).setImage3(img3);
                    img3 = "";

                }
            }

            if (data.get(position).getCamera1().equalsIgnoreCase("NO")) {
                holder.image1.setImageResource(R.drawable.camera_ico);
            } else if (data.get(position).getCamera1().equalsIgnoreCase("YES")) {
                holder.image1.setImageResource(R.drawable.camera_tick_ico);
            }


            if (data.get(position).getCamera2().equalsIgnoreCase("NO")) {
                holder.image2.setImageResource(R.drawable.camera_ico);
            } else if (data.get(position).getCamera2().equalsIgnoreCase("YES")) {
                holder.image2.setImageResource(R.drawable.camera_tick_ico);
            }


            if (data.get(position).getCamera3().equalsIgnoreCase("NO")) {
                holder.image3.setImageResource(R.drawable.camera_ico);
            } else if (data.get(position).getCamera3().equalsIgnoreCase("YES")) {
                holder.image3.setImageResource(R.drawable.camera_tick_ico);
            }

            if (position == 0) {
                holder.brand_name.setText(data.get(position).getBrand());
                holder.display_name.setText(data.get(position).getDisplay() + "(" + data.get(position).getType() + ")");
                holder.target_quantity.setText(data.get(position).getTrg_quantity());
            } else {

                if (data.get(position - 1).getDisplay()
                        .equalsIgnoreCase(data.get(position).getDisplay())) {
                    holder.brand_name.setText(data.get(position).getBrand());
                    holder.display_name.setText(data.get(position).getDisplay() + "(" + data.get(position).getType() + ")");
                    holder.target_quantity.setText(data.get(position).getTrg_quantity());
                } else {
                    holder.brand_name.setText(data.get(position).getBrand());
                    holder.display_name.setText(data.get(position).getDisplay() + "(" + data.get(position).getType() + ")");
                    holder.target_quantity.setText(data.get(position).getTrg_quantity());
                }
            }


            holder.actual_quanity.setId(position);
            holder.display_name.setId(position);
            holder.image1.setId(position);
            holder.image2.setId(position);
            holder.image3.setId(position);
            holder.question_lv.setId(position);
            holder.stock_btn.setId(position);

            holder.stock_btn.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {
                    final int position1 = v.getId();
                    final Dialog stockdialog = new Dialog(mcontext);
                    stockdialog.setContentView(R.layout.totstockdialog);
                    stockdialog.setTitle("Stock Display");
                    brand = (Spinner) stockdialog.findViewById(R.id.brand_namespinner);
                    sku = (Spinner) stockdialog.findViewById(R.id.sku_nameSpinner);
                    quantity = (EditText) stockdialog.findViewById(R.id.qty_bought);
                    save_btnn = (Button) stockdialog.findViewById(R.id.add_btn);
                    listview = (ListView) stockdialog.findViewById(R.id.lv);
                    brand_list = db.gettotBrandList(category_id, process_id, store_id);
                    brandAdaptor = new ArrayAdapter<CharSequence>(mcontext, android.R.layout.simple_spinner_item);
                    brandAdaptor.add("- Select -");

                    skuAdaptor = new ArrayAdapter<CharSequence>(mcontext, android.R.layout.simple_spinner_item);
                    skuAdaptor.add("- Select -");
                    skuAdaptor.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

                    for (int i = 0; i < brand_list.size(); i++) {
                        brandAdaptor.add(brand_list.get(i).getBrand());
                    }
                    brand.setAdapter(brandAdaptor);
                    brandAdaptor.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

                    brand.setOnItemSelectedListener(new OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> adapterView, View view, int pos, long l) {
                            if (pos != 0 && brand_list.size() > 0) {
                                brand_name = brand_list.get(pos - 1).getBrand();
                                brand_id = brand_list.get(pos - 1).getBrand_id();
                                sku_list = db.getSKUList(category_id, brand_id, process_id, store_id);
                                if (sku_list.size() > 0) {
                                    skuAdaptor = new ArrayAdapter<CharSequence>(mcontext, android.R.layout.simple_spinner_item);
                                    skuAdaptor.add("- Select -");
                                    for (int i = 0; i < sku_list.size(); i++) {
                                        skuAdaptor.add(sku_list.get(i).getSku_name());
                                    }

                                    sku.setAdapter(skuAdaptor);
                                    skuAdaptor.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                                }
                            } else {
                                brand_name = "";
                                brand_id = "";
                                sku_name = "";
                                sku_id = "";
                            }
                        }

                        @Override
                        public void onNothingSelected(AdapterView<?> adapterView) {

                        }
                    });

                    sku.setOnItemSelectedListener(new OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> adapterView, View view, int pos, long l) {
                            if (pos != 0) {
                                sku_id = sku_list.get(pos - 1).getSku_id();
                                sku_name = sku_list.get(pos - 1).getSku_name();
                            } else {
                                sku_name = "";
                                sku_id = "";
                            }
                        }

                        @Override
                        public void onNothingSelected(AdapterView<?> adapterView) {

                        }
                    });
                    db.open();
                    list = db.getTOTStockEntryDetail(store_id, category_id, process_id, data.get(position).getDisplay_id(), data.get(position).getUnique_id());
                    if (list.size() > 0) {
                        adapterData = new MyAdaptorStock(AfterTOT.this, list);
                        listview.setAdapter(adapterData);
                        listview.invalidateViews();
                    }

                    save_btnn.setOnClickListener(new OnClickListener() {

                        @Override
                        public void onClick(View v) {
                            lv.clearFocus();
                            lv.invalidate();
                            String quan = quantity.getText().toString();
                            if (!quan.equalsIgnoreCase("")) {
                                if (validatedata()) {
                                    if (validateduplicateEntry(data.get(position).getDisplay_id(), data.get(position).getUnique_id())) {
                                        TOTBean ab = new TOTBean();
                                        ab.setQuantity(quan);
                                        ab.setBrand(brand_name);
                                        ab.setBrand_id(brand_id);
                                        ab.setDisplay_id(data.get(position).getDisplay_id());
                                        ab.setStore_id(store_id);
                                        ab.setUnique_id(data.get(position).getUnique_id());
                                        ab.setSku_id(sku_id);
                                        ab.setSku_name(sku_name);
                                        ab.setProcess_id(process_id);
                                        ab.setQuantity(quan);
                                        ab.setCategory_id(category_id);
                                        db.InsertStockTot(ab);
                                        brand.setSelection(0);
                                        sku.setSelection(0);
                                        quantity.setText("");
                                        db.open();
                                        list = db.getTOTStockEntryDetail(store_id, category_id, process_id,
                                                data.get(position).getDisplay_id(), data.get(position).getUnique_id());
                                        adapterData = new MyAdaptorStock(AfterTOT.this, list);
                                        listview.setAdapter(adapterData);
                                        listview.invalidateViews();
                                    } else {
                                        Toast.makeText(getBaseContext(), "Duplicate Entry !", Toast.LENGTH_LONG).show();
                                    }
                                } else {
                                    Toast.makeText(getBaseContext(), "Please Select the Brand & Sku", Toast.LENGTH_LONG).show();
                                }

                            } else {

                                Toast.makeText(getBaseContext(), "Please fill the quantity ", Toast.LENGTH_LONG).show();

                            }

                        }
                    });
                    stockdialog.show();
                }
            });


            holder.question_lv.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {
                    final int position1 = v.getId();
                    final Dialog dialog = new Dialog(AfterTOT.this, R.style.theme_sms_receive_dialog);
                    dialog.setContentView(R.layout.dialog);
                    dialog.setTitle("Gap Reasons");
                    ListView listofquestion = (ListView) dialog.findViewById(R.id.list_q);
                    Button save_q = (Button) dialog.findViewById(R.id.save_q);
                    db.open();
                    question_list = db.getInsertedTOTQuestionsData(store_id,
                            data.get(position1).getDisplay_id(), category_id, data.get(position1).getUnique_id(), process_id);

                    if (question_list.size() == 0) {
                        update1 = false;
                        db.open();
                        question_list = db.getQuestionsData(data.get(position1).getDisplay_id());
                        listofquestion.setAdapter(new questionAdaptor(AfterTOT.this));
                    } else {

                        update1 = true;
                        save_q.setText("Update");

                        if (question_list.size() > 0) {

                            listofquestion.setAdapter(new questionAdaptor(AfterTOT.this));

                        }

                    }

                    save_q.setOnClickListener(new OnClickListener() {

                        @Override
                        public void onClick(View v) {
                            if (update1) {
                                for (int i = 0; i < question_list.size(); i++) {
                                    db.open();
                                    db.updateGapsData(store_id, category_id, question_list.get(i).getDisplay_id(),
                                            question_list.get(i).getQuestion_id(), question_list.get(i).getAnswer(),
                                            data.get(position1).getUnique_id(), process_id);
                                    dialog.dismiss();
                                }
                            } else {
                                db.open();
                                db.InsertQuestionData(question_list, data.get(position1).getDisplay_id(), store_id
                                        , category_id, data.get(position1).getUnique_id(), process_id);
                                dialog.dismiss();
                            }


                        }
                    });

                    dialog.show();


                }
            });


            return convertView;
        }

    }


    private class questionAdaptor extends BaseAdapter {

        LayoutInflater mInflater;
        private Context mcontext;


        public questionAdaptor(Context context) {

            mInflater = LayoutInflater.from(context);
            mcontext = context;

        }


        @Override
        public int getCount() {

            return question_list.size();
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
            ViewHolder holder;
            if (convertView == null) {

                convertView = mInflater.inflate(R.layout.question_view_list,
                        null);
                holder = new ViewHolder();
                holder.question_text = (TextView) convertView
                        .findViewById(R.id.question);

                holder.answer = (ToggleButton) convertView
                        .findViewById(R.id.answer);


                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            holder.question_text.setText(question_list.get(position).getQuestion());


            holder.answer.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {


                    final int position = v.getId();
                    final ToggleButton Caption = (ToggleButton) v;
                    String value1 = Caption.getText().toString();

                    if (value1.equals("")) {

                        question_list.get(position).setAnswer("");

                    } else {

                        question_list.get(position).setAnswer(value1);


                    }

                }
            });


            if (question_list.get(position).getAnswer().equalsIgnoreCase("NO")) {
                holder.answer.setChecked(false);
            } else {
                holder.answer.setChecked(true);
            }
            holder.answer.setId(position);
            holder.question_text.setId(position);

            return convertView;
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
                        if (new File(str + _pathforcheck).exists()) {
                            String metadata = CommonFunctions.setMetadataAtImagesforcategory(storename, store_id, "TOT LEFT IMAGE", username, cat_name);
                            CommonFunctions.addMetadataAndTimeStampToImage(AfterTOT.this, _path, metadata, date);
                            img1 = _pathforcheck;
                            lv.invalidateViews();
                            _pathforcheck = "";
                            break;

                        }
                    }

                    if (_pathforcheck2 != null && !_pathforcheck2.equals("")) {
                        if (new File(str + _pathforcheck2).exists()) {
                            String metadata = CommonFunctions.setMetadataAtImagesforcategory(storename, store_id, "TOT FRONT IMAGE", username, cat_name);
                            CommonFunctions.addMetadataAndTimeStampToImage(AfterTOT.this, _path, metadata, date);
                            img2 = _pathforcheck2;
                            lv.invalidateViews();
                            _pathforcheck2 = "";
                            break;

                        }
                    }

                    if (_pathforcheck3 != null && !_pathforcheck3.equals("")) {
                        if (new File(str + _pathforcheck3).exists()) {
                            String metadata = CommonFunctions.setMetadataAtImagesforcategory(storename, store_id, "TOT RIGHT IMAGE", username, cat_name);
                            CommonFunctions.addMetadataAndTimeStampToImage(AfterTOT.this, _path, metadata, date);
                            img3 = _pathforcheck3;
                            lv.invalidateViews();
                            _pathforcheck3 = "";
                            break;

                        }
                    }
                } catch (Exception e) {
                    FirebaseCrashlytics.getInstance().recordException(e);
                    e.printStackTrace();
                }
                break;
        }
    }

    public class MyAdaptorStock extends BaseAdapter {
        private LayoutInflater mInflater;
        private Context mcontext;
        private ArrayList<TOTBean> list;

        public MyAdaptorStock(Activity activity, ArrayList<TOTBean> list1) {

            mInflater = LayoutInflater.from(getBaseContext());
            mcontext = activity;
            list = list1;
        }

        @Override
        public int getCount() {

            return list.size();
        }

        @Override
        public Object getItem(int position1) {

            return position1;
        }

        @Override
        public long getItemId(int position1) {

            return position1;
        }

        class ViewHolder {
            TextView brand, qty_bought, display;
            Button save, delete;

        }

        @Override
        public View getView(final int position1, View convertView, ViewGroup parent) {

            final ViewHolder holder;

            if (convertView == null) {

                convertView = mInflater.inflate(R.layout.tot_stock_list, null);
                holder = new ViewHolder();
                holder.brand = (TextView) convertView.findViewById(R.id.brand_name);
                holder.display = (TextView) convertView.findViewById(R.id.display_name);
                holder.qty_bought = (TextView) convertView.findViewById(R.id.qty_bought);
                holder.delete = (Button) convertView.findViewById(R.id.delete_btn);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            holder.delete.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {

                    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                            AfterTOT.this);

                    // set title
                    alertDialogBuilder.setTitle("Do You Want To Delete?");

                    // set dialog message
                    alertDialogBuilder
                            .setMessage("Click Yes To Delete!")
                            .setCancelable(false)
                            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    // if this button is clicked, close
                                    // current activity
                                    db.open();
                                    db.deleteTOTStockEntry(list.get(position1).getKEY_ID());

                                    adapterData.notifyDataSetChanged();
                                    db.open();
                                    list = db.getTOTStockEntryDetail(store_id, category_id, process_id,
                                            list.get(position1).getDisplay_id(), list.get(position1).getUnique_id());
                                    listview.setAdapter(new MyAdaptorStock(AfterTOT.this, list));
                                    listview.invalidateViews();

                                }
                            })
                            .setNegativeButton("No", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    // if this button is clicked, just close
                                    // the dialog box and do nothing
                                    dialog.cancel();
                                }
                            });

                    // create alert dialog
                    AlertDialog alertDialog = alertDialogBuilder.create();

                    // show it
                    alertDialog.show();

                }
            });


            holder.brand.setText(list.get(position1).getBrand()
                    .toString());
            holder.display.setText(list.get(position1).getSku_name().toString());

            holder.qty_bought.setText(list.get(position1).getQuantity());


            holder.brand.setId(position1);
            holder.display.setId(position1);
            holder.qty_bought.setId(position1);
            holder.delete.setId(position1);

            return convertView;
        }
    }

    public boolean validatedata() {
        boolean result = false;
        if (brand_name != null && !brand_id.equals("") && sku_name != null && !sku_id.equals("")) {
            result = true;
        }
        return result;

    }


    public boolean validateduplicateEntry(String display_id, String unique_id) {
        boolean result = true;
        db.open();
        list = db.getTOTStockEntryDetailFORVAlidation(store_id, category_id, process_id,
                display_id, unique_id, brand_id, sku_id);

        if (list.size() > 0) {
            result = false;
        }
        return result;
    }


}
