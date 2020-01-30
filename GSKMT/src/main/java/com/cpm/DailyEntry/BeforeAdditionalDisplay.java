package com.cpm.DailyEntry;

import java.io.File;
import java.util.ArrayList;

import com.cpm.Constants.CommonFunctions;
import com.cpm.Constants.CommonString;


import com.cpm.database.GSKMTDatabase;


import com.cpm.delegates.SkuBean;
import com.crashlytics.android.Crashlytics;
import com.example.gsk_mtt.R;

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
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

public class BeforeAdditionalDisplay extends Activity implements OnClickListener, AdapterView.OnItemSelectedListener {

    public RecyclerView additional_Recycler;
    Spinner brand, display;
    EditText quantity;
    Button add, save_btn;
    public ArrayList<SkuBean> brand_list = new ArrayList<SkuBean>();
    public ArrayList<SkuBean> display_list = new ArrayList<SkuBean>();
    private ArrayAdapter<CharSequence> brandAdaptor, displayAdaptor;
    GSKMTDatabase db;
    String brand_name, brand_id, display_name, display_id, store_id, store_type_id, image_url;
    private SharedPreferences preferences;
    MyAdaptor adapterData;
    Button show;
    ImageView cam;
    String img1 = "";
    ArrayList<SkuBean> list = new ArrayList<SkuBean>();
    private static String str, path;
    protected static String _pathforcheck = "";
    public String category_id, process_id, date, intime, username, app_version, imgDate, _path, image1 = "", storename,cat_name;
    ToggleButton toggle;
    String s;
    Button refImage;
    String reference_image_path = Environment.getExternalStorageDirectory() + "/GSKMT Planogram Images/";

    /////Rl
    LinearLayout RL_headerS, rl_allDATA, savebtnLayout;
    boolean addflag = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.addtional_before_display);
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        additional_Recycler = (RecyclerView) findViewById(R.id.additional_Recycler);
        cam = (ImageView) findViewById(R.id.camera);
        brand = (Spinner) findViewById(R.id.brand_name);
        display = (Spinner) findViewById(R.id.display_name);
        quantity = (EditText) findViewById(R.id.qty_bought);
        toggle = (ToggleButton) findViewById(R.id.toggle);
        add = (Button) findViewById(R.id.add_btn);
        refImage = (Button) findViewById(R.id.refimage);

        ///new changessssssssss
        RL_headerS = (LinearLayout) findViewById(R.id.RL_headerS);
        rl_allDATA = (LinearLayout) findViewById(R.id.rl_allDATA);

        savebtnLayout = (LinearLayout) findViewById(R.id.savebtnLayout);
        save_btn = (Button) findViewById(R.id.save_btn);

        preferences = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        store_id = preferences.getString(CommonString.KEY_STORE_ID, null);
        category_id = preferences.getString(CommonString.KEY_CATEGORY_ID, null);
        process_id = preferences.getString(CommonString.KEY_PROCESS_ID, null);
        date = preferences.getString(CommonString.KEY_DATE, null);
        intime = preferences.getString(CommonString.KEY_IN_TIME, null);
        username = preferences.getString(CommonString.KEY_USERNAME, null);
        app_version = preferences.getString(CommonString.KEY_VERSION, null);
        store_type_id = preferences.getString(CommonString.storetype_id, null);
        storename = preferences.getString(CommonString.KEY_STORE_NAME, "");
        cat_name = preferences.getString(CommonString.KEY_CATEGORY_NAME, "");
        imgDate = date.replace("/", "-");
        db = new GSKMTDatabase(BeforeAdditionalDisplay.this);
        db.open();

        toggle.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                s = toggle.getText().toString();
                if (s.equalsIgnoreCase("YES")) {
                    if (list.size() > 0 && list.get(0).getYesorno().equalsIgnoreCase("NO")) {
                        db.deleteProductEntryData(store_id, process_id, category_id);
                        list.clear();
                    }
                    RL_headerS.setVisibility(View.VISIBLE);
                    rl_allDATA.setVisibility(View.VISIBLE);
                    savebtnLayout.setVisibility(View.VISIBLE);
                    additional_Recycler.setVisibility(View.VISIBLE);
                    toggle.setChecked(true);
                } else {
                    AlertDialog.Builder builder = new AlertDialog.Builder(BeforeAdditionalDisplay.this);
                    builder.setMessage("Are you sure you want to close the window")
                            .setTitle("Parinaam")
                            .setCancelable(false)
                            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    list.clear();
                                    toggle.setChecked(false);
                                    RL_headerS.setVisibility(View.GONE);
                                    rl_allDATA.setVisibility(View.GONE);
                                    savebtnLayout.setVisibility(View.GONE);
                                    additional_Recycler.setVisibility(View.GONE);

                                }

                            }).setNegativeButton("No", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.cancel();
                            toggle.setChecked(true);
                        }
                    });

                    AlertDialog alert = builder.create();
                    alert.show();
                }
            }
        });


        str = Environment.getExternalStorageDirectory() + "/MT_GSK_Images/";
        brand_list = db.getBrandList(category_id);
        brandAdaptor = new ArrayAdapter<CharSequence>(BeforeAdditionalDisplay.this, R.layout.spinner_custom_item);
        brandAdaptor.setDropDownViewResource(R.layout.spinner_custom_item);
        brandAdaptor.add("-Select Brand-");
        for (int i = 0; i < brand_list.size(); i++) {
            brandAdaptor.add(brand_list.get(i).getBrand());
        }

        brand.setAdapter(brandAdaptor);

        display_list = db.getDisplayList(category_id, store_id, store_type_id, process_id);
        displayAdaptor = new ArrayAdapter<CharSequence>(BeforeAdditionalDisplay.this, R.layout.spinner_custom_item);
        displayAdaptor.setDropDownViewResource(R.layout.spinner_custom_item);
        displayAdaptor.add("-Select Display-");
        for (int i = 0; i < display_list.size(); i++) {
            displayAdaptor.add(display_list.get(i).getDisplay());
        }

        display.setAdapter(displayAdaptor);
        cam.setOnClickListener(this);
        add.setOnClickListener(this);
        refImage.setOnClickListener(this);
        save_btn.setOnClickListener(this);
        brand.setOnItemSelectedListener(this);
        display.setOnItemSelectedListener(this);
        setadditionalDisplayData();
    }


    public void setadditionalDisplayData() {
        list = db.getProductEntryDetail(store_id, category_id, process_id);
        if (list.size() > 0) {
            if (list.get(0).getYesorno().equals("YES")) {
                toggle.setChecked(true);
                additional_Recycler.setAdapter(new MyAdaptor(this, list));
                additional_Recycler.setLayoutManager(new LinearLayoutManager(this));
                additional_Recycler.setVisibility(View.VISIBLE);
                RL_headerS.setVisibility(View.VISIBLE);
                rl_allDATA.setVisibility(View.VISIBLE);
                savebtnLayout.setVisibility(View.VISIBLE);

            } else {
                RL_headerS.setVisibility(View.GONE);
                rl_allDATA.setVisibility(View.GONE);
                savebtnLayout.setVisibility(View.GONE);
                additional_Recycler.setVisibility(View.GONE);
                toggle.setChecked(false);
            }
        }
    }

    @Override
    public void onBackPressed() {
        AlertDialog.Builder builder = new AlertDialog.Builder(
                this);
        builder.setMessage(CommonString.ONBACK_ALERT_MESSAGE)
                .setCancelable(false)
                .setPositiveButton("OK",
                        new DialogInterface.OnClickListener() {
                            public void onClick(
                                    DialogInterface dialog, int id) {
                                Intent in = new Intent(BeforeAdditionalDisplay.this, DailyEntryMainMenu.class);
                                startActivity(in);
                                BeforeAdditionalDisplay.this.finish();

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
                        if (new File((str + _pathforcheck).trim()).exists()) {
                            img1 = _pathforcheck;
                            String value = img1.substring(img1.indexOf("_") + 1, img1.lastIndexOf("_"));
                            if (value != null) {
                                if (value.equals("1")) {
                                    String metadata = CommonFunctions.setMetadataAtImagesforcategory(storename, store_id, "ADDITIONAL DISPLAY IMAGE", username,cat_name);
                                    Bitmap bmp = CommonFunctions.addMetadataAndTimeStampToImage(BeforeAdditionalDisplay.this, _path, metadata, date);
                                    image1 = img1;
                                    cam.setImageResource(R.drawable.camera_tick_ico);
                                }
                            }
                            _pathforcheck = "";
                            break;
                        }
                    } catch (Exception e) {
                        Crashlytics.logException(e);
                        e.printStackTrace();
                    }
                }

                break;
        }
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.add_btn:
                hideSoftKeyboard();
                if (validate()) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(this);
                    builder.setTitle("Parinaam").setMessage("Do you want to add data");
                    builder.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            addflag = true;
                            SkuBean ab = new SkuBean();
                            ab.setQuantity(quantity.getText().toString());
                            ab.setBrand(brand_name);
                            ab.setBrand_id(brand_id);
                            ab.setDisplay(display_name);
                            ab.setDisplay_id(display_id);
                            ab.setAdditional_image(image1);
                            ab.setYesorno("YES");
                            list.add(ab);
                            adapterData = new MyAdaptor(BeforeAdditionalDisplay.this, list);
                            additional_Recycler.setAdapter(adapterData);
                            additional_Recycler.setLayoutManager(new LinearLayoutManager(BeforeAdditionalDisplay.this));
                            quantity.setText("1");
                            brand.setSelection(0);
                            display.setSelection(0);
                            image1 = "";
                            cam.setImageResource(R.drawable.camera_ico);

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
                break;

            case R.id.save_btn:
                if (!toggle.isChecked()) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(this);
                    builder.setTitle("Parinaam").setMessage("Do you want to save data");
                    builder.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            list.clear();
                            SkuBean ab = new SkuBean();
                            ab.setQuantity("0");
                            ab.setBrand("");
                            ab.setBrand_id("");
                            ab.setDisplay("");
                            ab.setDisplay_id("");
                            ab.setAdditional_image("");
                            ab.setYesorno("NO");
                            list.add(ab);
                            db.open();
                            db.InsertAdditionalInfo(list, store_id, category_id, process_id);
                            TOAST("Data has been saved");
                            Intent in = new Intent(BeforeAdditionalDisplay.this, DailyEntryMainMenu.class);
                            startActivity(in);
                            BeforeAdditionalDisplay.this.finish();
                        }
                    });
                    builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.dismiss();

                        }
                    });
                    builder.show();
                } else if (list.size() > 0 && addflag) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(this);
                    builder.setTitle("Parinaam").setMessage("Do you want to save data");
                    builder.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            db.open();
                            db.InsertAdditionalInfo(list, store_id, category_id, process_id);
                            TOAST("Data has been saved");
                            Intent in = new Intent(BeforeAdditionalDisplay.this, DailyEntryMainMenu.class);
                            startActivity(in);
                            BeforeAdditionalDisplay.this.finish();
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
            case R.id.camera:
                _pathforcheck = store_id + "_1_BA" + imgDate + brand_id + display_id + ".jpg";
                _path = str + _pathforcheck;
                CommonFunctions.startAnncaCameraActivity(BeforeAdditionalDisplay.this, _path);
                break;
            case R.id.refimage:
                if (!display_id.equals("")) {
                    Bitmap bitmap = BitmapFactory.decodeFile(reference_image_path + image_url);
                    if (bitmap != null) {
                        final Dialog dialog = new Dialog(BeforeAdditionalDisplay.this);
                        dialog.setContentView(R.layout.popup);
                        ImageView refimage = (ImageView) dialog.findViewById(R.id.displayimage);
                        dialog.setTitle("Reference Image");
                        bitmap = BitmapFactory.decodeFile(reference_image_path + image_url);
                        Drawable mDrawable = new BitmapDrawable(getResources(), bitmap);
                        refimage.setImageDrawable(mDrawable);
                        dialog.show();
                    } else {
                        Toast.makeText(BeforeAdditionalDisplay.this, "Reference Image not available", Toast.LENGTH_LONG).show();
                    }
                } else {
                    Toast.makeText(BeforeAdditionalDisplay.this, "Please Select Display", Toast.LENGTH_LONG).show();
                }
                break;
        }
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
        if (adapterView.getId() == R.id.brand_name) {
            if (position != 0) {
                brand_name = brand_list.get(position - 1).getBrand();
                brand_id = brand_list.get(position - 1).getBrand_id();
            } else {
                brand_name = "";
                brand_id = "";
            }
        } else if (adapterView.getId() == R.id.display_name) {
            if (position != 0) {
                display_name = display_list.get(position - 1).getDisplay();
                display_id = display_list.get(position - 1).getDisplay_id();
                image_url = display_list.get(position - 1).getImage_url();
            } else {
                display_name = "";
                display_id = "";
            }

        }

    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }

    public class MyAdaptor extends RecyclerView.Adapter<MyAdaptor.ViewHolder> {
        private LayoutInflater mInflater;
        private Context mcontext;
        private ArrayList<SkuBean> list;

        public MyAdaptor(Context context, ArrayList<SkuBean> list1) {
            mInflater = LayoutInflater.from(context);
            mcontext = context;
            list = list1;
        }

        @Override
        public MyAdaptor.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = mInflater.inflate(R.layout.addtional_list, parent, false);
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
                                                    MyAdaptor adapter = new MyAdaptor(mcontext, list);
                                                    additional_Recycler.setAdapter(adapter);
                                                    additional_Recycler.setLayoutManager(new LinearLayoutManager(mcontext));
                                                    adapter.notifyDataSetChanged();
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
                        builder.setMessage("Are you sure you want to Delete")
                                .setCancelable(false)
                                .setPositiveButton("Yes",
                                        new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int id) {
                                                db.deleteProductEntry(list.get(position).getKey_id());
                                                list.remove(position);
                                                notifyDataSetChanged();
                                                if (list.size() > 0) {
                                                    MyAdaptor adapter = new MyAdaptor(mcontext, list);
                                                    additional_Recycler.setAdapter(adapter);
                                                    additional_Recycler.setLayoutManager(new LinearLayoutManager(mcontext));
                                                    adapter.notifyDataSetChanged();
                                                }
                                                notifyDataSetChanged();
                                            }
                                        })
                                .setNegativeButton("No",
                                        new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int id) {
                                                dialog.cancel();
                                            }
                                        });
                        AlertDialog alert = builder.create();
                        alert.show();
                    }


                }
            });


            holder.brand.setText(list.get(position).getBrand().toString());
            holder.display.setText(list.get(position).getDisplay().toString());
            holder.qty_bought.setText(list.get(position).getQuantity());
            holder.brand.setId(position);
            holder.display.setId(position);
            holder.qty_bought.setId(position);
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
            TextView brand, qty_bought, display;
            ImageView delete;

            public ViewHolder(View convertView) {
                super(convertView);
                brand = (TextView) convertView.findViewById(R.id.brand_name);
                display = (TextView) convertView.findViewById(R.id.display_name);
                qty_bought = (TextView) convertView.findViewById(R.id.qty_bought);
                delete = (ImageView) convertView.findViewById(R.id.dlt_img);
            }
        }
    }

    private boolean validate() {
        boolean status = true;
        if (brand.getSelectedItemId() == 0) {
            TOAST("Please Select Brand Dropdown .");
            status = false;
        }
        if (status && display.getSelectedItemId() == 0) {
            TOAST("Please Select Display Dropdown .");
            status = false;
        }

        if (status && image1.equals("")) {
            TOAST("Please Capture Image .");
            status = false;
        }

        return status;
    }

    private void TOAST(String msg) {
        Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_LONG).show();
    }

}
