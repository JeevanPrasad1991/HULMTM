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
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.provider.MediaStore;

import android.support.design.widget.Snackbar;
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
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ToggleButton;

public class CompetitionTracking extends Activity implements OnClickListener, AdapterView.OnItemSelectedListener {
    String store_id, category_id, process_id, date, intime, username, _pathforcheck = "", img1 = "", image1 = "", str,
            app_version, store_type_id, imgDate, _path, brand_name, brand_id, display_name, display_id,storename,cat_name;
    private ArrayAdapter<CharSequence> brandAdaptor, displayAdaptor;
    public ArrayList<SkuBean> display_list = new ArrayList<>();
    ArrayList<SkuBean> brand_list = new ArrayList<>();
    ArrayList<SkuBean> list = new ArrayList<>();
    SharedPreferences preferences;
    GSKMTDatabase db;
    Spinner competition, display;
    public RecyclerView competitionPRecycler;
    EditText quantity;
    Button add_btn, save_btn;
    ImageView cam;
    MyAdaptor adapterData;

    /////Rl
    LinearLayout RL_headerS, rl_allDATA, savebtnLayout;
    ToggleButton COMP_checkbox;
    boolean addflag = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.competition_trackingwith);
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        display = (Spinner) findViewById(R.id.display_name);
        competition = (Spinner) findViewById(R.id.competition);
        quantity = (EditText) findViewById(R.id.qty_bought);
        add_btn = (Button) findViewById(R.id.add_btn);
        cam = (ImageView) findViewById(R.id.camera);
        competitionPRecycler = (RecyclerView) findViewById(R.id.competitionPRecycler);

        ///new changessssssssss
        COMP_checkbox = (ToggleButton) findViewById(R.id.COMP_checkbox);
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

        db = new GSKMTDatabase(CompetitionTracking.this);
        db.open();
        str = Environment.getExternalStorageDirectory() + "/MT_GSK_Images/";

        brand_list = db.getCompetitionBrandList(category_id);
        display_list = db.getDisplayListForComp();

        brandAdaptor = new ArrayAdapter<CharSequence>(CompetitionTracking.this, R.layout.spinner_custom_item);
        brandAdaptor.setDropDownViewResource(R.layout.spinner_custom_item);
        brandAdaptor.add("-Select Competition-");

        for (int i = 0; i < brand_list.size(); i++) {
            brandAdaptor.add(brand_list.get(i).getBrand());
        }

        competition.setAdapter(brandAdaptor);

        displayAdaptor = new ArrayAdapter<CharSequence>(CompetitionTracking.this, R.layout.spinner_custom_item);
        displayAdaptor.setDropDownViewResource(R.layout.spinner_custom_item);

        displayAdaptor.add("-Select Display-");
        for (int i = 0; i < display_list.size(); i++) {
            displayAdaptor.add(display_list.get(i).getDisplay());
        }


        display.setAdapter(displayAdaptor);


        ///for checkbox

        COMP_checkbox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (COMP_checkbox.isChecked()) {
                    if (list.size() > 0) {
                        if (list.get(0).isCompTExist() == false) {
                            list.clear();
                        }
                    }
                    RL_headerS.setVisibility(View.VISIBLE);
                    rl_allDATA.setVisibility(View.VISIBLE);
                    savebtnLayout.setVisibility(View.VISIBLE);
                    competitionPRecycler.setVisibility(View.VISIBLE);
                    COMP_checkbox.setChecked(true);
                } else {
                    AlertDialog.Builder builder = new AlertDialog.Builder(CompetitionTracking.this);
                    builder.setMessage("Are you sure you want to close the window")
                            .setTitle("Parinaam")
                            .setCancelable(false)
                            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    list.clear();
                                    COMP_checkbox.setChecked(false);
                                    RL_headerS.setVisibility(View.GONE);
                                    rl_allDATA.setVisibility(View.GONE);
                                    savebtnLayout.setVisibility(View.GONE);
                                    competitionPRecycler.setVisibility(View.GONE);

                                }

                            }).setNegativeButton("No", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.cancel();
                            COMP_checkbox.setChecked(true);
                        }
                    });

                    AlertDialog alert = builder.create();
                    alert.show();
                }
            }
        });

        competition.setOnItemSelectedListener(this);
        display.setOnItemSelectedListener(this);

        add_btn.setOnClickListener(this);
        save_btn.setOnClickListener(this);
        cam.setOnClickListener(this);
        setCompetitorPOIData();

    }


    public void setCompetitorPOIData() {
        list = db.getEnteredCompetitionDetail(store_id, category_id, process_id);
        if (list.size() > 0) {
            if (list.get(0).isCompTExist() == true) {
                COMP_checkbox.setChecked(true);
                competitionPRecycler.setAdapter(new MyAdaptor(this, list));
                competitionPRecycler.setLayoutManager(new LinearLayoutManager(this));
                competitionPRecycler.setVisibility(View.VISIBLE);
                RL_headerS.setVisibility(View.VISIBLE);
                rl_allDATA.setVisibility(View.VISIBLE);
                savebtnLayout.setVisibility(View.VISIBLE);

            } else {
                RL_headerS.setVisibility(View.GONE);
                rl_allDATA.setVisibility(View.GONE);
                savebtnLayout.setVisibility(View.GONE);
                competitionPRecycler.setVisibility(View.GONE);
                COMP_checkbox.setChecked(false);
            }
        }
    }


    public boolean validatedata() {
        boolean result = true;
        try {
            if (list.size() > 0) {
                for (int k = 0; k < list.size(); k++) {
                    if (list.get(k).getBrand_id().equals(brand_id) && list.get(k).getDisplay_id().equals(display_id)
                            && list.get(k).getCategory_id().equals(category_id) && list.get(k).getProcess_id().equals(process_id)) {
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
                            String metadata = CommonFunctions.setMetadataAtImagesforcategory(storename, store_id, "COMPETITION ASSET IMAGE", username,cat_name);
                            CommonFunctions.addMetadataAndTimeStampToImage(CompetitionTracking.this, _path, metadata, date);
                            img1 = _pathforcheck;
                            String value = img1.substring(img1.indexOf("_") + 1, img1.lastIndexOf("_"));
                            if (value != null) {
                                if (value.equals("1")) {
                                    image1 = img1;
                                    cam.setImageResource(R.drawable.camera_tick_ico);
                                }
                            }
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
                                Intent in = new Intent(CompetitionTracking.this, DailyEntryMainMenu.class);
                                startActivity(in);
                                CompetitionTracking.this.finish();

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

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.add_btn:
                hideSoftKeyboard();
                if (validate()) {
                    if (validatedata()) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(this);
                        builder.setTitle("Parinaam").setMessage("Do you want to add data");
                        builder.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                addflag = true;
                                SkuBean ab = new SkuBean();
                                ab.setQuantity(quantity.getText().toString().replaceFirst("().<0>?", ""));
                                ab.setBrand(brand_name);
                                ab.setBrand_id(brand_id);
                                ab.setDisplay(display_name);
                                ab.setDisplay_id(display_id);
                                ab.setAdditional_image(image1);
                                ab.setCategory_id(category_id);
                                ab.setProcess_id(process_id);
                                ab.setCompTExist(true);
                                list.add(ab);
                                adapterData = new MyAdaptor(CompetitionTracking.this, list);
                                competitionPRecycler.setAdapter(adapterData);
                                competitionPRecycler.setLayoutManager(new LinearLayoutManager(CompetitionTracking.this));
                                cam.setImageResource(R.drawable.camera_ico);
                                competition.setSelection(0);
                                display.setSelection(0);
                                quantity.setText("");
                                image1 = "";

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
                        TOAST("Already Exist please take another.");
                    }
                }

                break;
            case R.id.save_btn:
                if (!COMP_checkbox.isChecked()) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(this);
                    builder.setTitle("Parinaam").setMessage("Do you want to save data");
                    builder.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            list.clear();
                            SkuBean ab = new SkuBean();
                            ab.setQuantity("");
                            ab.setBrand("");
                            ab.setBrand_id("");
                            ab.setDisplay("");
                            ab.setDisplay_id("");
                            ab.setAdditional_image("");
                            ab.setCompTExist(false);
                            list.add(ab);
                            db.open();
                            db.InsertCompetitionInfo(list, store_id, category_id, process_id);
                            TOAST("Data has been saved");
                            Intent in = new Intent(CompetitionTracking.this, DailyEntryMainMenu.class);
                            startActivity(in);
                            CompetitionTracking.this.finish();
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
                            db.InsertCompetitionInfo(list, store_id, category_id, process_id);
                            TOAST("Data has been saved");
                            Intent in = new Intent(CompetitionTracking.this, DailyEntryMainMenu.class);
                            startActivity(in);
                            CompetitionTracking.this.finish();
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
                CommonFunctions.startAnncaCameraActivity(CompetitionTracking.this, _path);
                break;
        }

    }


    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
        if (adapterView.getId() == R.id.competition) {
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
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = mInflater.inflate(R.layout.addtional_list, parent, false);
            ViewHolder holder = new ViewHolder(view);
            return holder;
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, final int position) {
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
                                                    competitionPRecycler.setAdapter(adapter);
                                                    competitionPRecycler.setLayoutManager(new LinearLayoutManager(mcontext));
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
                                                db.deleteCompetitionEntry(list.get(position).getKey_id());
                                                list.remove(position);
                                                notifyDataSetChanged();
                                                if (list.size() > 0) {
                                                    MyAdaptor adapter = new MyAdaptor(mcontext, list);
                                                    competitionPRecycler.setAdapter(adapter);
                                                    competitionPRecycler.setLayoutManager(new LinearLayoutManager(mcontext));
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
        if (competition.getSelectedItemId() == 0) {
            TOAST("Please Select Competition Dropdown .");
            status = false;
        }
        if (status && display.getSelectedItemId() == 0) {
            TOAST("Please Select Display Dropdown .");
            status = false;
        }
        if (status && quantity.getText().toString().equals("")) {
            TOAST("Please Fill Quantity .");
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
