package com.cpm.DailyEntry;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.cpm.Constants.CommonFunctions;
import com.cpm.Constants.CommonString;
import com.cpm.database.GSKMTDatabase;
import com.cpm.delegates.StoreBean;
import com.cpm.message.AlertMessage;
import com.example.gsk_mtt.R;


public class StoreProfileActivity extends Activity implements View.OnClickListener {
    private SharedPreferences preferences;
    private String store_id, process_id, username, date;
    TextView txt_store_name;
    Button save;
    EditText edt_storeId, edt_store_name, edt_store_address, edt_store_locality, edt_store_pincode, edt_city;
    StoreBean store_profileInfo = null;
    Context context;
    GSKMTDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_store_profile);
        context = this;
        db = new GSKMTDatabase(context);
        db.open();

        store_profileInfo = new StoreBean();
        save = (Button) findViewById(R.id.save);
        txt_store_name = (TextView) findViewById(R.id.txt_store_name);
        edt_storeId = (EditText) findViewById(R.id.edt_storeId);
        edt_store_name = (EditText) findViewById(R.id.edt_store_name);
        edt_store_address = (EditText) findViewById(R.id.edt_store_address);
        edt_store_locality = (EditText) findViewById(R.id.edt_store_locality);
        edt_store_pincode = (EditText) findViewById(R.id.edt_store_pincode);
        edt_city = (EditText) findViewById(R.id.edt_city);
        preferences = PreferenceManager.getDefaultSharedPreferences(context);
        store_id = preferences.getString(CommonString.KEY_STORE_ID, "");
        process_id = preferences.getString(CommonString.KEY_PROCESS_ID, null);
        username = preferences.getString(CommonString.KEY_USERNAME, null);
        date = preferences.getString(CommonString.KEY_DATE, null);
        save.setOnClickListener(this);
        validate_data();

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.save:
                if (check_condition()) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(context).setTitle(R.string.parinamm);
                    builder.setMessage("Are you sure you want to save ?").setCancelable(false).setPositiveButton("Yes",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    StoreBean storeBean = new StoreBean();
                                    storeBean.setSTORE_ID(edt_storeId.getText().toString());
                                    storeBean.setSTORE(CommonFunctions.removed_special_char(edt_store_name));
                                    storeBean.setAddress(CommonFunctions.removed_special_char(edt_store_address));
                                    storeBean.setLocality(CommonFunctions.removed_special_char(edt_store_locality));
                                    storeBean.setPinCode((edt_store_pincode.getText().toString()));
                                    storeBean.setCITY(edt_city.getText().toString());
                                    storeBean.setPROCESS_ID(process_id);
                                    storeBean.setVISIT_DATE(date);
                                    db.open();
                                    long l = db.insert_store_information_data(storeBean, username);
                                    if (l > 0) {
                                        Intent in = new Intent(context, CopyOfStorevisitedYesMenu.class);
                                        startActivity(in);
                                        StoreProfileActivity.this.finish();
                                    } else {
                                        AlertMessage.Show_toast_msg(context, "Error in Saving Data");
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
                }

                break;
        }

    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent in = new Intent(context, CopyOfStorelistActivity.class);
        startActivity(in);
        StoreProfileActivity.this.finish();
    }


    private boolean check_condition() {
        boolean check_flag = true;
        try {

            if (edt_store_name.getText().toString().isEmpty()) {
                AlertMessage.Show_toast_msg(context, "Please Enter Store Name");
                check_flag = false;
            } else if (edt_store_address.getText().toString().isEmpty()) {
                AlertMessage.Show_toast_msg(context, "Please Enter Store Address");
                check_flag = false;
            } else if (edt_store_locality.getText().toString().isEmpty()) {
                AlertMessage.Show_toast_msg(context, "Please Enter Locality");
                check_flag = false;
            } else if (edt_store_pincode.getText().toString().isEmpty()) {
                AlertMessage.Show_toast_msg(context, "Please Enter Pin Code");
                check_flag = false;
            } else if (edt_store_pincode.getText().toString().length() < 6) {
                AlertMessage.Show_toast_msg(context, "Please Enter Correct Pin Code");
                check_flag = false;
            } else {
                check_flag = true;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return check_flag;
    }

    private void validate_data() {
        db.open();
        store_profileInfo = db.getstore_profile_info_forupload(store_id, date, process_id);
        if (store_profileInfo != null && store_profileInfo.getSTORE_ID() != null && !store_profileInfo.getSTORE_ID().equals("")) {
            save.setText("Update");
        } else {
            db.open();
            store_profileInfo = db.getStoreStatus(store_id, process_id);
        }
        if (store_profileInfo != null) {
            edt_storeId.setText(store_profileInfo.getSTORE_ID());
            edt_store_name.setText(store_profileInfo.getSTORE());
            edt_store_address.setText(store_profileInfo.getAddress());
            edt_store_locality.setText(store_profileInfo.getLocality());
            edt_store_pincode.setText(store_profileInfo.getPinCode());
            edt_city.setText(store_profileInfo.getCITY());
        }
    }
}
