package com.cpm.DailyEntry;

import java.io.File;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.cpm.Constants.CommonFunctions;
import com.cpm.Constants.CommonString;
import com.cpm.database.GSKMTDatabase;
import com.cpm.delegates.SkuBean;
import com.example.gsk_mtt.R;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.text.InputFilter;
import android.text.InputType;
import android.text.Spanned;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.View.OnFocusChangeListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AbsListView.OnScrollListener;

public class ShareofShelfActivity extends Activity implements OnClickListener {
    private SharedPreferences preferences;
    private SharedPreferences.Editor editor = null;
    ListView lv;
    Button save;
    public ArrayList<SkuBean> subCategoryList = new ArrayList<SkuBean>();
    Boolean update = false, filled_any = false, isDialogOpen = true;
    GSKMTDatabase db;
    public String store_id, category_id, process_id, intime, username, app_version, region_id, store_type_id, storename, cat_name;
    String paked_key;
    protected static String _pathforcheck = "";
    public String image1 = "", _path, date, imgDate, str, state_id, key_id, class_id;
    static int currentVersion = 1, row_pos = 10000, _pos = -1;
    boolean saveFlag = true;
    MyAdaptor sosAdapter;
    Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sos_activity);
        context = this;
        lv = (ListView) findViewById(R.id.ListView01);
        preferences = PreferenceManager.getDefaultSharedPreferences(context);
        store_id = preferences.getString(CommonString.KEY_STORE_ID, null);
        category_id = preferences.getString(CommonString.KEY_CATEGORY_ID, null);
        process_id = preferences.getString(CommonString.KEY_PROCESS_ID, null);
        date = preferences.getString(CommonString.KEY_DATE, null);
        intime = preferences.getString(CommonString.KEY_IN_TIME, null);
        username = preferences.getString(CommonString.KEY_USERNAME, null);
        app_version = preferences.getString(CommonString.KEY_VERSION, null);
        storename = preferences.getString(CommonString.KEY_STORE_NAME, null);
        ///change by jeevan RAna
        state_id = preferences.getString(CommonString.KEY_STATE_ID, null);
        region_id = preferences.getString(CommonString.region_id, null);
        store_type_id = preferences.getString(CommonString.storetype_id, null);
        key_id = preferences.getString(CommonString.KEY_ID, null);
        class_id = preferences.getString(CommonString.KEY_CLASS_ID, null);
        paked_key = preferences.getString(CommonString.KEY_PACKED_KEY, null);
        cat_name = preferences.getString(CommonString.KEY_CATEGORY_NAME, "");
        TextView textView1 = (TextView) findViewById(R.id.textView1);
        textView1.setText("SOS - " + cat_name);
        currentVersion = android.os.Build.VERSION.SDK_INT;
        save = (Button) findViewById(R.id.savebtn);
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
        subCategoryList = db.getsos_facing(store_id, category_id, process_id);
        if (subCategoryList.size() == 0) {
            db.open();
            subCategoryList = db.getsubcategorySOS(store_id, process_id, region_id, store_type_id, key_id, category_id);
        } else {
            save.setText("Update");
            update = true;
        }

        if (subCategoryList.size() > 0) {
            sosAdapter = new MyAdaptor(context);
            lv.setAdapter(sosAdapter);
        }

        lv.setOnScrollListener(new OnScrollListener() {
            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

            }

            @Override
            public void onScrollStateChanged(AbsListView arg0, int scrollState) {
                sosAdapter.notifyDataSetChanged();
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
        if (filled_any) {
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setMessage("Are you sure you want to quit ?").setCancelable(false).setPositiveButton("YES",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            Intent in = new Intent(context, DailyEntryMainMenu.class);
                            startActivity(in);
                            ShareofShelfActivity.this.finish();
                        }
                    }).setNegativeButton("NO",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.cancel();
                        }
                    });
            AlertDialog alert = builder.create();
            alert.show();
        } else {
            Intent in = new Intent(context, DailyEntryMainMenu.class);
            startActivity(in);
            ShareofShelfActivity.this.finish();

        }
    }


    class ViewHolder {
        ImageView sos_img;
        TextView txt_brands, txt_ownbrands, txt_target;
        EditText eyelavel_faceup, noneyelavel_faceup, eyelavel_faceupown, noneyelavel_faceupown;
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
            return subCategoryList.size();
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
                convertView = mInflater.inflate(R.layout.item_sos, null);
                holder = new ViewHolder();
                holder.sos_img = (ImageView) convertView.findViewById(R.id.sos_img);
                holder.txt_target = (TextView) convertView.findViewById(R.id.txt_target);
                holder.txt_brands = (TextView) convertView.findViewById(R.id.txt_brands);
                holder.eyelavel_faceup = (EditText) convertView.findViewById(R.id.eyelavel_faceup);
                holder.noneyelavel_faceup = (EditText) convertView.findViewById(R.id.noneyelavel_faceup);
                ////for own category sub
                holder.txt_ownbrands = (TextView) convertView.findViewById(R.id.txt_ownbrands);
                holder.eyelavel_faceupown = (EditText) convertView.findViewById(R.id.eyelavel_faceupown);
                holder.noneyelavel_faceupown = (EditText) convertView.findViewById(R.id.noneyelavel_faceupown);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }


            final SkuBean object = subCategoryList.get(position);

            holder.txt_brands.setText(object.getSubcategory() + " - Category");
            holder.txt_brands.setId(position);
            holder.txt_ownbrands.setText(getString(R.string.hul) + " " + object.getSubcategory());
            holder.txt_ownbrands.setId(position);


            if (object.getLINEAR_MEASUREMENT().equals("1")) {
                holder.eyelavel_faceup.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
                holder.eyelavel_faceup.setFilters(new InputFilter[]{new DecimalDigitsInputFilter(6, 1)});
                holder.eyelavel_faceup.setId(position);

                holder.noneyelavel_faceup.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
                holder.noneyelavel_faceup.setFilters(new InputFilter[]{new DecimalDigitsInputFilter(6, 1)});
                holder.noneyelavel_faceup.setId(position);


                holder.eyelavel_faceupown.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
                holder.eyelavel_faceupown.setFilters(new InputFilter[]{new DecimalDigitsInputFilter(6, 1)});
                holder.eyelavel_faceupown.setId(position);

                holder.noneyelavel_faceupown.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
                holder.noneyelavel_faceupown.setFilters(new InputFilter[]{new DecimalDigitsInputFilter(6, 1)});
                holder.noneyelavel_faceupown.setId(position);


            } else {
                holder.eyelavel_faceup.setInputType(InputType.TYPE_CLASS_NUMBER);
                holder.eyelavel_faceup.setFilters(new InputFilter[]{new InputFilter.LengthFilter(6)});
                holder.eyelavel_faceup.setId(position);

                holder.noneyelavel_faceup.setInputType(InputType.TYPE_CLASS_NUMBER);
                holder.noneyelavel_faceup.setFilters(new InputFilter[]{new InputFilter.LengthFilter(6)});
                holder.noneyelavel_faceup.setId(position);


                holder.eyelavel_faceupown.setInputType(InputType.TYPE_CLASS_NUMBER);
                holder.eyelavel_faceupown.setFilters(new InputFilter[]{new InputFilter.LengthFilter(6)});
                holder.eyelavel_faceupown.setId(position);

                holder.noneyelavel_faceupown.setInputType(InputType.TYPE_CLASS_NUMBER);
                holder.noneyelavel_faceupown.setFilters(new InputFilter[]{new InputFilter.LengthFilter(6)});
                holder.noneyelavel_faceupown.setId(position);
            }


            holder.eyelavel_faceup.setOnFocusChangeListener(new OnFocusChangeListener() {
                @Override
                public void onFocusChange(View view, boolean hasFocus) {
                    if (!hasFocus) {
                        filled_any = true;
                        final int position = view.getId();
                        final EditText Caption = (EditText) view;
                        String value1 = Caption.getText().toString().replaceFirst("^0+(?!$)", "");
                        if (value1.equals("")) {
                            object.setCategoryEyelevel("");
                            holder.eyelavel_faceup.setText("");
                            holder.eyelavel_faceup.setId(position);

                        } else {
                            String final_value = CommonFunctions.return_decimal_value(value1);
                            object.setCategoryEyelevel(final_value);
                            holder.eyelavel_faceup.setText(final_value);
                            holder.eyelavel_faceup.setId(position);
                        }
                    }
                }
            });


            holder.noneyelavel_faceup.setOnFocusChangeListener(new OnFocusChangeListener() {
                @Override
                public void onFocusChange(View view, boolean hasFocus) {
                    if (!hasFocus) {
                        filled_any = true;
                        final int position = view.getId();
                        final EditText Caption = (EditText) view;
                        String value1 = Caption.getText().toString().replaceFirst("^0+(?!$)", "");
                        if (value1.equals("")) {
                            object.setCategoryNonEyelevel("");
                            holder.noneyelavel_faceup.setText("");
                            holder.noneyelavel_faceup.setId(position);

                        } else {
                            String final_value = CommonFunctions.return_decimal_value(value1);
                            object.setCategoryNonEyelevel(final_value);
                            holder.noneyelavel_faceup.setText(final_value);
                            holder.noneyelavel_faceup.setId(position);
                        }
                    }
                }
            });


            holder.eyelavel_faceupown.setOnFocusChangeListener(new OnFocusChangeListener() {
                @Override
                public void onFocusChange(View view, boolean hasFocus) {
                    if (!hasFocus) {
                        filled_any = true;
                        final int position = view.getId();
                        final EditText Caption = (EditText) view;
                        String value1 = Caption.getText().toString().replaceFirst("^0+(?!$)", "");
                        if (object.getCategoryEyelevel().equals("")) {
                            if (isDialogOpen) {
                                isDialogOpen = !isDialogOpen;
                                AlertDialog.Builder builder = new AlertDialog.Builder(mcontext);
                                String msg_str = "First Fill The " + object.getSubcategory() + "- " + object.getCategory() + " 'EyeLevel'";
                                builder.setMessage(msg_str)
                                        .setCancelable(false).setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        dialog.dismiss();
                                        isDialogOpen = !isDialogOpen;
                                        object.setEyelevel("");
                                        holder.eyelavel_faceupown.setText("");
                                        holder.eyelavel_faceupown.setId(position);
                                    }
                                });
                                AlertDialog alert = builder.create();
                                alert.show();
                            }
                        } else {
                            String final_value = "";
                            if (!value1.equals("")) {

                                final_value = CommonFunctions.return_decimal_value(value1);
                                double stock_total_eyelevelfacup = Double.parseDouble(CommonFunctions.return_decimal_value(object.getCategoryEyelevel()));
                                double calculated_value = Double.parseDouble(final_value);
                                if (calculated_value > stock_total_eyelevelfacup) {
                                    if (isDialogOpen) {
                                        isDialogOpen = !isDialogOpen;
                                        AlertDialog.Builder builder = new AlertDialog.Builder(mcontext);
                                        String msg_str = "HUL " + object.getSubcategory() + "- " + object.getCategory() +
                                                " 'EyeLevel' cannot be greather than of " + object.getSubcategory() + "- " + object.getCategory() + "'EyeLevel'";
                                        builder.setMessage(msg_str).setCancelable(false)
                                                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                                    public void onClick(DialogInterface dialog, int id) {
                                                        dialog.dismiss();
                                                        isDialogOpen = !isDialogOpen;
                                                        object.setEyelevel("");
                                                        holder.eyelavel_faceupown.setText("");
                                                        holder.eyelavel_faceupown.setId(position);
                                                    }
                                                });
                                        AlertDialog alert = builder.create();
                                        alert.show();
                                    }
                                } else {
                                    object.setEyelevel(final_value);
                                    call_ach(holder, object, position);

                                }
                            } else {
                                object.setEyelevel("");
                            }
                        }
                    }
                }
            });


            holder.noneyelavel_faceupown.setOnFocusChangeListener(new OnFocusChangeListener() {
                @Override
                public void onFocusChange(View view, boolean hasFocus) {
                    if (!hasFocus) {
                        filled_any = true;
                        final int position = view.getId();
                        final EditText Caption = (EditText) view;
                        String value1 = Caption.getText().toString().replaceFirst("^0+(?!$)", "");
                        if (object.getCategoryNonEyelevel().equals("")) {
                            if (isDialogOpen) {
                                isDialogOpen = !isDialogOpen;
                                AlertDialog.Builder builder = new AlertDialog.Builder(mcontext);
                                String msg_str = "First Fill The " + object.getSubcategory() + "- " + object.getCategory() + " 'Non EyeLevel'";
                                builder.setMessage(msg_str)
                                        .setCancelable(false).setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        dialog.dismiss();
                                        isDialogOpen = !isDialogOpen;
                                        object.setNoneyelevel("");
                                        holder.noneyelavel_faceupown.setText("");
                                        holder.noneyelavel_faceupown.setId(position);
                                    }
                                });
                                AlertDialog alert = builder.create();
                                alert.show();
                            }
                        } else {
                            String final_value = "";
                            if (!value1.equals("")) {
                                final_value = CommonFunctions.return_decimal_value(value1);
                                double stock_total_eyelevelfacup = Double.parseDouble(CommonFunctions.return_decimal_value(object.getCategoryNonEyelevel()));
                                double calculated_value = Double.parseDouble(final_value);
                                if (calculated_value > stock_total_eyelevelfacup) {
                                    if (isDialogOpen) {
                                        isDialogOpen = !isDialogOpen;
                                        AlertDialog.Builder builder = new AlertDialog.Builder(mcontext);
                                        String msg_str = "HUL " + object.getSubcategory() + "- " + object.getCategory() +
                                                " 'Non EyeLevel' cannot be greather than of " + object.getSubcategory() + "- " + object.getCategory() + "'Non EyeLevel'";
                                        builder.setMessage(msg_str).setCancelable(false)
                                                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                                    public void onClick(DialogInterface dialog, int id) {
                                                        dialog.dismiss();
                                                        isDialogOpen = !isDialogOpen;
                                                        object.setNoneyelevel("");
                                                        holder.noneyelavel_faceupown.setText("");
                                                        holder.noneyelavel_faceupown.setId(position);
                                                    }
                                                });
                                        AlertDialog alert = builder.create();
                                        alert.show();
                                    }
                                } else {
                                    object.setNoneyelevel(final_value);
                                    call_ach(holder, object, position);
                                }
                            } else {
                                object.setNoneyelevel("");
                            }
                        }
                    }
                }
            });


            holder.sos_img.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {
                    _pos = position;
                    filled_any = true;
                    _pathforcheck = store_id + "-" + category_id + "_SOSImage" + imgDate + ".jpg";
                    _path = str + _pathforcheck;
                    CommonFunctions.startAnncaCameraActivity(context, _path);
                }
            });

            if (!image1.equals("")) {
                if (_pos == position) {
                    object.setImage1(image1);
                    image1 = "";
                    _pos = -1;
                }
            }

            if (object.getImage1().equals("")) {
                holder.sos_img.setImageResource(R.drawable.camera_ico);
                holder.sos_img.setId(position);
            } else {
                holder.sos_img.setImageResource(R.drawable.camera_tick_ico);
                holder.sos_img.setId(position);
            }

            call_ach(holder, object, position);
            holder.eyelavel_faceup.setText(subCategoryList.get(position).getCategoryEyelevel());
            holder.eyelavel_faceup.setId(position);

            holder.noneyelavel_faceup.setText(subCategoryList.get(position).getCategoryNonEyelevel());
            holder.noneyelavel_faceup.setId(position);

            holder.eyelavel_faceupown.setText(subCategoryList.get(position).getEyelevel());
            holder.eyelavel_faceupown.setId(position);

            holder.noneyelavel_faceupown.setText(subCategoryList.get(position).getNoneyelevel());
            holder.noneyelavel_faceupown.setId(position);

            if (!saveFlag) {
                if (subCategoryList.get(position).getCategoryEyelevel().equals("")) {
                    holder.eyelavel_faceup.setHint("EMPTY");
                    holder.eyelavel_faceup.setHintTextColor(Color.RED);
                }

                if (subCategoryList.get(position).getCategoryNonEyelevel().equals("")) {
                    holder.noneyelavel_faceup.setHint("EMPTY");
                    holder.noneyelavel_faceup.setHintTextColor(Color.RED);
                }

                if (subCategoryList.get(position).getEyelevel().equals("")) {
                    holder.eyelavel_faceupown.setHint("EMPTY");
                    holder.eyelavel_faceupown.setHintTextColor(Color.RED);
                }

                if (subCategoryList.get(position).getNoneyelevel().equals("")) {
                    holder.noneyelavel_faceupown.setHint("EMPTY");
                    holder.noneyelavel_faceupown.setHintTextColor(Color.RED);
                }
            }

            return convertView;
        }

    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.savebtn) {
            lv.clearFocus();
            lv.invalidateViews();
            row_pos = 10000;
            if (validate_values()) {
                AlertDialog.Builder builder = new AlertDialog.Builder(context).setTitle(R.string.parinamm);
                builder.setMessage("Are you sure you want to save ?").setCancelable(false).setPositiveButton("Yes",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                db.open();
                                db.InsertSOSData(store_id, subCategoryList, username, category_id, process_id, date);
                                Intent in = new Intent(context, DailyEntryMainMenu.class);
                                startActivity(in);
                                ShareofShelfActivity.this.finish();
                            }
                        }).setNegativeButton("No",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });
                AlertDialog alert = builder.create();

                alert.show();
            }
        }


    }


    public class DecimalDigitsInputFilter implements InputFilter {
        Pattern mPattern;

        public DecimalDigitsInputFilter(int digitsBeforeZero, int digitsAfterZero) {
            mPattern = Pattern.compile("[0-9]{0," + (digitsBeforeZero - 1) + "}+((\\.[0-9]{0," + (digitsAfterZero - 1) + "})?)||(\\.)?");
        }

        @Override
        public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
            Matcher matcher = mPattern.matcher(dest);
            if (!matcher.matches())
                return "";
            return null;
        }

    }


    public boolean validate_values() {
        saveFlag = true;
        for (int i = 0; i < subCategoryList.size(); i++) {
            if (subCategoryList.get(i).getImage1().equals("")) {
                String msg_str = "First Click The " + subCategoryList.get(i).getSubcategory() + "- " + subCategoryList.get(i).getCategory() + " 'Image'";
                show_toast(msg_str);
                saveFlag = false;
                break;
            } else if (subCategoryList.get(i).getCategoryEyelevel().equals("")) {
                String msg_str = "First Fill The " + subCategoryList.get(i).getSubcategory() + "- " + subCategoryList.get(i).getCategory() + " 'EyeLevel'";
                show_toast(msg_str);
                saveFlag = false;
                break;
            } else if (subCategoryList.get(i).getEyelevel().equals("")) {
                String msg_str = "First Fill The HUL " + subCategoryList.get(i).getSubcategory() + "- " + subCategoryList.get(i).getCategory() + " 'EyeLevel'";
                show_toast(msg_str);
                saveFlag = false;
                break;
            } else if (Double.parseDouble(CommonFunctions.return_decimal_value(subCategoryList.get(i).getEyelevel())) > Double.parseDouble(CommonFunctions.return_decimal_value(subCategoryList.get(i).getCategoryEyelevel()))) {
                String msg_str = "HUL " + subCategoryList.get(i).getSubcategory() + "- " + subCategoryList.get(i).getCategory() +
                        " 'EyeLevel' cannot be greather than of " + subCategoryList.get(i).getSubcategory() + "- " + subCategoryList.get(i).getCategory() + "'EyeLevel'";
                show_toast(msg_str);
                saveFlag = false;
                break;
            } else if (subCategoryList.get(i).getCategoryNonEyelevel().equals("")) {
                String msg_str = "First Fill The " + subCategoryList.get(i).getSubcategory() + "- " + subCategoryList.get(i).getCategory() + " 'Non EyeLevel'";
                show_toast(msg_str);
                saveFlag = false;
                break;
            } else if (subCategoryList.get(i).getNoneyelevel().equals("")) {
                String msg_str = "First Fill The HUL " + subCategoryList.get(i).getSubcategory() + "- " + subCategoryList.get(i).getCategory() + " 'Non EyeLevel'";
                show_toast(msg_str);
                saveFlag = false;
                break;
            } else if (Double.parseDouble(CommonFunctions.return_decimal_value(subCategoryList.get(i).getNoneyelevel())) > Double.parseDouble(CommonFunctions.return_decimal_value(subCategoryList.get(i).getCategoryNonEyelevel()))) {
                String msg_str = "HUL " + subCategoryList.get(i).getSubcategory() + "- " + subCategoryList.get(i).getCategory() +
                        " 'Non EyeLevel' cannot be greather than of " + subCategoryList.get(i).getSubcategory() + "- " + subCategoryList.get(i).getCategory()
                        + "'Non EyeLevel'";
                show_toast(msg_str);
                saveFlag = false;
                break;
            }
        }

        return saveFlag;

    }

    private void show_toast(String str) {
        Toast.makeText(context, str, Toast.LENGTH_LONG).show();
    }

    private String calculate_sos_ach(SkuBean sosObj) {
        String sos_ach = "0";
        try {
            if (sosObj != null) {
                if (sosObj.getCategoryEyelevel().equals("") && sosObj.getCategoryNonEyelevel().equals("") && sosObj.getEyelevel().equals("") && sosObj.getNoneyelevel().equals("")) {
                } else {
                    double CC = Double.parseDouble(CommonFunctions.return_decimal_value(sosObj.getEyelevel()));
                    double DD = Double.parseDouble(CommonFunctions.return_decimal_value(sosObj.getNoneyelevel()));
                    double AA = Double.parseDouble(CommonFunctions.return_decimal_value(sosObj.getCategoryEyelevel()));
                    double BB = Double.parseDouble(CommonFunctions.return_decimal_value(sosObj.getCategoryNonEyelevel()));
                    double sos = ((CC + DD) / (AA + BB)) * 100;
                    int i = (int) sos;
                    sos_ach = "" + i;
                    sosAdapter.notifyDataSetChanged();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return sos_ach;
    }

    private void call_ach(ViewHolder holder, SkuBean object, int position) {
        object.setAchieved_sosper(calculate_sos_ach(object));
        holder.txt_target.setText("SOS Target : " + object.getSos_target() + "% Achieved : " + object.getAchieved_sosper() + "%");
        holder.txt_target.setId(position);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            View v = getCurrentFocus();
            if (v instanceof EditText) {
                Rect outRect = new Rect();
                v.getGlobalVisibleRect(outRect);
                if (!outRect.contains((int) event.getRawX(), (int) event.getRawY())) {
                    Log.d("focus", "touchevent");
                    v.clearFocus();
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                }
            }
        }
        return super.dispatchTouchEvent(event);
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
                            String metadata = CommonFunctions.setMetadataAtImages(storename, store_id, "SOS IMAGE", username);
                            CommonFunctions.addMetadataAndTimeStampToImage(context, _path, metadata, date);
                            //Set Clicked image to Imageview
                            image1 = _pathforcheck;
                            _pathforcheck = "";
                            sosAdapter.notifyDataSetChanged();
                            break;
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
        }
    }

}
