package com.cpm.DailyEntry;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

import com.cpm.Constants.CommonFunctions;
import com.cpm.Constants.CommonString;
import com.cpm.DailyEntry.AfterTOT.ViewHolder;
import com.cpm.database.GSKMTDatabase;
import com.cpm.delegates.PromotionBean;
import com.crashlytics.android.Crashlytics;
import com.example.gsk_mtt.R;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.support.design.widget.Snackbar;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import static com.cpm.DailyEntry.BeforeTOT.imgDate;

public class PromoCompliance extends Activity {
    ListView lv;
    Button save;
    GSKMTDatabase db;
    ArrayList<PromotionBean> promotionlist;
    SharedPreferences preferences;
    String store_id, category_id, process_id, date, intime, username, app_version, region_id, key_id, state_id, storename,cat_name;
    boolean update = false;
    MyAdaptor adapter;
    String _pathforcheck, _path, str = CommonString.FILE_PATH, img = "";
    int child_pos = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.promotion);
        lv = (ListView) findViewById(R.id.list);
        save = (Button) findViewById(R.id.save);
        promotionlist = new ArrayList<>();

        db = new GSKMTDatabase(PromoCompliance.this);
        db.open();
        preferences = PreferenceManager.getDefaultSharedPreferences(PromoCompliance.this);
        store_id = preferences.getString(CommonString.KEY_STORE_ID, null);
        category_id = preferences.getString(CommonString.KEY_CATEGORY_ID, null);
        process_id = preferences.getString(CommonString.KEY_PROCESS_ID, null);
        date = preferences.getString(CommonString.KEY_DATE, null);
        intime = preferences.getString(CommonString.KEY_IN_TIME, null);
        username = preferences.getString(CommonString.KEY_USERNAME, null);
        app_version = preferences.getString(CommonString.KEY_VERSION, null);
        region_id = preferences.getString(CommonString.region_id, null);
        state_id = preferences.getString(CommonString.KEY_STATE_ID, null);
        key_id = preferences.getString(CommonString.KEY_ID, null);
        storename = preferences.getString(CommonString.KEY_STORE_NAME, "");
        cat_name = preferences.getString(CommonString.KEY_CATEGORY_NAME, "");
        promotionlist = db.getInsertedPromoCompliance(store_id, category_id, process_id);
        if (promotionlist.size() == 0) {
            promotionlist = db.getPromoComplianceData2(key_id, process_id, category_id, state_id);
        } else {
            update = true;
            save.setText("Update");
        }

        if (promotionlist.size() > 0) {
            adapter = new MyAdaptor(PromoCompliance.this);
            lv.setAdapter(adapter);
        }


        lv.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                lv.clearFocus();
                lv.invalidateViews();
                if (SCROLL_STATE_TOUCH_SCROLL == scrollState) {
                    View currentFocus = getCurrentFocus();
                    if (currentFocus != null) {
                        currentFocus.clearFocus();
                    }
                }
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

            }
        });

        save.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                lv.clearFocus();
                lv.invalidateViews();
                if (isValid()) {
                    if (isCount()) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(
                                PromoCompliance.this);
                        builder.setMessage("Do you want to save the data ")
                                .setCancelable(false)
                                .setPositiveButton("OK",
                                        new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog,
                                                                int id) {

                                                if (update) {

                                                    for (int i = 0; i < promotionlist.size(); i++) {
                                                        db.updatePromotionData(store_id, category_id, process_id, promotionlist.get(i).getSpecial_id(), promotionlist.get(i).getSku_id(),
                                                                promotionlist.get(i).getStock(), promotionlist.get(i).getPop(), promotionlist.get(i).getRunning(), promotionlist.get(i).getRunning_child_toggle(),
                                                                promotionlist.get(i).getPop_img(), promotionlist.get(i).getRunning_child_price());

                                                    }

                                                    Intent i = new Intent(
                                                            getApplicationContext(),
                                                            DailyEntryMainMenu.class);
                                                    startActivity(i);
                                                    PromoCompliance.this.finish();
                                                } else {
                                                    db.InsertPromotionData(promotionlist, store_id
                                                            , category_id, process_id);


                                                    Intent i = new Intent(
                                                            getApplicationContext(),
                                                            DailyEntryMainMenu.class);
                                                    startActivity(i);
                                                    PromoCompliance.this.finish();
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
                    } else {
                        Toast.makeText(getApplicationContext(), "Please fill actual price", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(getApplicationContext(), R.string.click_image_alert_str, Toast.LENGTH_SHORT).show();
                }
            }
        });


    }

    @Override
    public void onBackPressed() {

        super.onBackPressed();

        Intent in = new Intent(PromoCompliance.this, DailyEntryMainMenu.class);
        startActivity(in);
        PromoCompliance.this.finish();
    }


    public class MyAdaptor extends BaseAdapter {

        LayoutInflater mInflater;
        private Context mcontext;

        public MyAdaptor(Context context) {

            mInflater = LayoutInflater.from(context);
            mcontext = context;
        }

        @Override
        public int getCount() {

            return promotionlist.size();
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
                convertView = mInflater.inflate(R.layout.promotionviewlist, null);
                holder = new ViewHolder();
                holder.sku_name = (TextView) convertView.findViewById(R.id.sku_name);
                holder.stock = (ToggleButton) convertView.findViewById(R.id.stock);
                holder.pop = (ToggleButton) convertView.findViewById(R.id.pop);
                holder.running = (ToggleButton) convertView.findViewById(R.id.running);
                holder.promo_name = (TextView) convertView.findViewById(R.id.Promotion_name);
                holder.img_cam_pop = (ImageView) convertView.findViewById(R.id.img_cam_pop);
                holder.linearLayout = (LinearLayout) convertView.findViewById(R.id.running_childlayout);
                holder.running_child_toggle = (ToggleButton) convertView.findViewById(R.id.running_childtoggel);
                holder.running_child_price = (EditText) convertView.findViewById(R.id.running_child_price);

                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            holder.img_cam_pop.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {
                    _pathforcheck = store_id + "POP" + process_id + username + getCurrentTime().replace(":", "") + category_id +
                            promotionlist.get(position).getSku_id() + ".jpg";
                    _path = str + _pathforcheck;
                    child_pos = position;
                    CommonFunctions.startAnncaCameraActivity(mcontext, _path);
                }
            });


            holder.stock.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {
                    final int position = v.getId();
                    final ToggleButton Caption = (ToggleButton) v;
                    String value1 = Caption.getText().toString();

                    if (value1.equals("")) {
                        promotionlist.get(position).setStock("");
                    } else {

                        promotionlist.get(position).setStock(value1);

                    }

                }
            });


            holder.pop.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {
                    final int position = v.getId();
                    final ToggleButton Caption = (ToggleButton) v;
                    String value1 = Caption.getText().toString();

                    if (value1.equals("")) {

                        promotionlist.get(position).setPop("");

                    } else {

                        promotionlist.get(position).setPop(value1);

                    }
                    lv.clearFocus();
                    adapter.notifyDataSetChanged();
                }
            });

            holder.running_child_price.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View v, boolean hasFocus) {
                    if (!hasFocus) {
                        String val = holder.running_child_price.getText().toString().replaceAll("[&^<>{}'$]", "").replaceFirst("^0+(?!$)", "");
                        if (val.equals("")) {
                            promotionlist.get(position).setRunning_child_price("");

                        } else {
                            promotionlist.get(position).setRunning_child_price(val);
                        }
                    }
                }
            });

            holder.running.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {


                    final int position = v.getId();
                    final ToggleButton Caption = (ToggleButton) v;
                    String value1 = Caption.getText().toString();

                    if (value1.equals("")) {

                        promotionlist.get(position).setRunning("");

                    } else {

                        promotionlist.get(position).setRunning(value1);

                    }

                    lv.clearFocus();
                    adapter.notifyDataSetChanged();

                }
            });


            if (promotionlist.get(position).getStock().equalsIgnoreCase("NO")) {
                holder.stock.setChecked(false);
            } else {
                holder.stock.setChecked(true);
            }


            //POP cam image visible on toggle checked
            if (promotionlist.get(position).getPop().equalsIgnoreCase("NO")) {
                holder.pop.setChecked(false);
                holder.img_cam_pop.setVisibility(View.GONE);
                String img_path = promotionlist.get(position).getPop_img();

                if (!img_path.equals("")) {
                    if (new File(str + img_path).exists()) {
                        new File(str + img_path).delete();
                    }
                }

                promotionlist.get(position).setPop_img("");

            } else {
                holder.pop.setChecked(true);
                holder.img_cam_pop.setVisibility(View.VISIBLE);
                if (!img.equals("") && child_pos == position) {
                    promotionlist.get(position).setPop_img(img);
                    img = "";
                    child_pos = -1;
                }
            }

            if (promotionlist.get(position).getPop_img().equals("")) {
                holder.img_cam_pop.setBackgroundResource(R.drawable.camera_ico);
            } else {
                holder.img_cam_pop.setBackgroundResource(R.drawable.camera_tick_ico);
            }

            // check running status
            if (promotionlist.get(position).getRunning().equalsIgnoreCase("NO")) {
                holder.running.setChecked(false);
                holder.linearLayout.setVisibility(View.GONE);
                promotionlist.get(position).setRunning_child_price("");
                promotionlist.get(position).setRunning_child_toggle("NO");

            } else {
                holder.running.setChecked(true);
                holder.linearLayout.setVisibility(View.VISIBLE);


                holder.running_child_toggle.setOnClickListener(new OnClickListener() {

                    @Override
                    public void onClick(View v) {

                        final int position = v.getId();
                        final ToggleButton Caption = (ToggleButton) v;
                        String value1 = Caption.getText().toString();

                        if (value1.equals("")) {

                            promotionlist.get(position).setRunning_child_toggle("");

                        } else {

                            promotionlist.get(position).setRunning_child_toggle(value1);

                        }
                        lv.clearFocus();
                        adapter.notifyDataSetChanged();

                    }
                });
            }


            // Handling Running child Toggle

            if (promotionlist.get(position).getRunning_child_toggle().equalsIgnoreCase("NO")) {
                holder.running_child_toggle.setChecked(false);
                holder.running_child_price.setVisibility(View.VISIBLE);
                holder.running_child_price.setText(promotionlist.get(position).getRunning_child_price() + "");

            } else {
                holder.running_child_toggle.setChecked(true);
                holder.running_child_price.setVisibility(View.GONE);
                promotionlist.get(position).setRunning_child_price("");

            }


            holder.sku_name.setText(promotionlist.get(position).getSku_name());
            holder.promo_name.setText(promotionlist.get(position).getPromotion());
            holder.stock.setId(position);
            holder.pop.setId(position);
            holder.running.setId(position);
            holder.running_child_toggle.setId(position);


            return convertView;
        }

    }

    static class ViewHolder {

        ToggleButton stock, pop, running, running_child_toggle;
        TextView sku_name, promo_name;
        ImageView img_cam_pop;
        EditText running_child_price;
        LinearLayout linearLayout;


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
                if (_pathforcheck != null && !_pathforcheck.equals("")) {
                    try {
                        if (new File(str + _pathforcheck).exists()) {
                            String metadata = CommonFunctions.setMetadataAtImagesforcategory(storename, store_id, "PROMOTION IMAGE", username,cat_name);
                            Bitmap bmp = CommonFunctions.addMetadataAndTimeStampToImage(PromoCompliance.this, _path, metadata, date);
                            img = _pathforcheck;
                            lv.invalidateViews();
                            _pathforcheck = "";
                            break;
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        Crashlytics.logException(e);
                    }

                }

                break;
        }
    }

    public String getCurrentTime() {
        Calendar m_cal = Calendar.getInstance();
        SimpleDateFormat formatter = new SimpleDateFormat("HH:mm:ss:mmm");
        String cdate = formatter.format(m_cal.getTime());
        return cdate;
    }

    public boolean isValid() {
        boolean flag = true;
        for (int i = 0; i < promotionlist.size(); i++) {
            if (promotionlist.get(i).getPop().equalsIgnoreCase("YES")) {
                if (promotionlist.get(i).getPop_img().equals("")) {
                    flag = false;
                    break;
                }
            }
        }
        return flag;
    }

    public boolean isCount() {
        boolean flagCount = true;
        for (int i = 0; i < promotionlist.size(); i++) {
            if (promotionlist.get(i).getRunning().equalsIgnoreCase("YES") && promotionlist.get(i).getRunning_child_toggle().equalsIgnoreCase("NO")) {
                if (promotionlist.get(i).getRunning_child_price().equals("")) {
                    flagCount = false;
                    break;
                } else {
                    flagCount = true;
                }
            }

        }
        return flagCount;
    }
}
