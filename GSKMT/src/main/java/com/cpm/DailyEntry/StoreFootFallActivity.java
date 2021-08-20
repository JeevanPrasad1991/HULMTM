package com.cpm.DailyEntry;

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
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.WindowManager;
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

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.cpm.Constants.CommonFunctions;
import com.cpm.Constants.CommonString;
import com.cpm.database.GSKMTDatabase;
import com.cpm.delegates.SkuBean;
import com.cpm.xmlGetterSetter.MAPPING_ALLSKU_ASSORTMENT;
import com.example.gsk_mtt.R;

import java.io.File;
import java.util.ArrayList;


public class StoreFootFallActivity extends Activity implements OnClickListener {
    Context context;
    Button save_btn;
    GSKMTDatabase db;
    private SharedPreferences preferences;
    MAPPING_ALLSKU_ASSORTMENT footfallobject = null;
    EditText edt_DailyStoreFootfall, edt_ShopperContact, edt_sales_conversion;
    public String category_id, process_id, date, store_id, store_type_id, username, app_version, storename, cat_name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_storefootfall);
        context = this;
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        edt_DailyStoreFootfall = (EditText) findViewById(R.id.edt_DailyStoreFootfall);
        edt_ShopperContact = (EditText) findViewById(R.id.edt_ShopperContact);
        edt_sales_conversion = (EditText) findViewById(R.id.edt_sales_conversion);
        save_btn = (Button) findViewById(R.id.savebtn);
        save_btn.setOnClickListener(this);
        preferences = PreferenceManager.getDefaultSharedPreferences(context);
        store_id = preferences.getString(CommonString.KEY_STORE_ID, null);
        category_id = preferences.getString(CommonString.KEY_CATEGORY_ID, null);
        process_id = preferences.getString(CommonString.KEY_PROCESS_ID, null);
        date = preferences.getString(CommonString.KEY_DATE, null);
        username = preferences.getString(CommonString.KEY_USERNAME, null);
        app_version = preferences.getString(CommonString.KEY_VERSION, null);
        store_type_id = preferences.getString(CommonString.storetype_id, null);
        storename = preferences.getString(CommonString.KEY_STORE_NAME, "");
        cat_name = preferences.getString(CommonString.KEY_CATEGORY_NAME, "");
        db = new GSKMTDatabase(context);
        db.open();
        footfallobject = db.getinserted_storeFootfall(store_id, category_id, process_id);
        if (footfallobject != null && footfallobject.getDaily_storeFootfall() != null && !footfallobject.getDaily_storeFootfall().equals("")) {
            save_btn.setText("Update");
            set_data(footfallobject);
        }
    }


    private void set_data(MAPPING_ALLSKU_ASSORTMENT footfallobject) {
        try {

            edt_DailyStoreFootfall.setText(footfallobject.getDaily_storeFootfall());
            edt_ShopperContact.setText(footfallobject.getShoperContact());
            edt_sales_conversion.setText(footfallobject.getSales_conversion());

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onBackPressed() {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage(CommonString.ONBACK_ALERT_MESSAGE).setCancelable(false).setPositiveButton("OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(
                            DialogInterface dialog, int id) {
                        Intent in = new Intent(context, DailyEntryMainMenu.class);
                        startActivity(in);
                        StoreFootFallActivity.this.finish();

                    }
                }).setNegativeButton("Cancel",
                new DialogInterface.OnClickListener() {
                    public void onClick(
                            DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
        AlertDialog alert = builder.create();
        alert.show();

    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.savebtn:
                if (validate()) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(context).setTitle("Parinaam").setMessage("Are you sure You want to save the data ?")
                            .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    footfallobject = new MAPPING_ALLSKU_ASSORTMENT();
                                    footfallobject.setDaily_storeFootfall(edt_DailyStoreFootfall.getText().toString());
                                    footfallobject.setShoperContact(edt_ShopperContact.getText().toString());
                                    footfallobject.setSales_conversion(edt_sales_conversion.getText().toString());
                                    db.open();
                                    db.insertstorefootfall(footfallobject, store_id, category_id, process_id);
                                    Intent in = new Intent(context, DailyEntryMainMenu.class);
                                    startActivity(in);
                                    TOAST("Data has been saved");
                                    StoreFootFallActivity.this.finish();
                                }
                            }).setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    dialogInterface.dismiss();
                                }
                            });

                    builder.show();

                }
                break;
        }
    }


    private boolean validate() {
        boolean status = true;
        if (edt_DailyStoreFootfall.getText().toString().isEmpty()) {
            TOAST("Please Enter Daily Store Footfall");
            status = false;
        } else if (edt_ShopperContact.getText().toString().isEmpty()) {
            TOAST("Please Enter Shopper Contact");
            status = false;
        } else if (Integer.parseInt(edt_ShopperContact.getText().toString()) > Integer.parseInt(edt_DailyStoreFootfall.getText().toString())) {
            TOAST("Shopper Contact Should be Less Than Or Equal to Daily Store Footfall");
            edt_ShopperContact.setHint("Shopper Contact");
            edt_ShopperContact.setText("");
            status = false;
        } else if (edt_sales_conversion.getText().toString().isEmpty()) {
            TOAST("Please Enter Sale - Conversion");
            status = false;
        }

        return status;
    }

    private void TOAST(String msg) {
        Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_LONG).show();
    }

}
