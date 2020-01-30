package com.cpm.geotagging;

import com.cpm.database.GSKMTDatabase;
import com.cpm.xmlGetterSetter.FailureGetterSetter;

import android.app.Activity;
import android.app.Dialog;
import android.content.SharedPreferences;
import android.os.Bundle;

import android.widget.ProgressBar;
import android.widget.TextView;

public class GeotagImageUpload extends Activity {
	
	
	private Dialog dialog;
	private ProgressBar pb;
	private TextView percentage, message;
	private String visit_date;
	private SharedPreferences preferences;
	private GSKMTDatabase database;
	private int factor, k;
	private FailureGetterSetter failureGetterSetter = null;

	String result, username, cate_id;
	String datacheck = "";
	String[] words;
	String validity, storename="", devationStoresName;
	String mid = "";
	String errormsg = "";
	static int counter = 1;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

}
