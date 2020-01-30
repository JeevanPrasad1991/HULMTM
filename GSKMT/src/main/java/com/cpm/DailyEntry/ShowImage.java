package com.cpm.DailyEntry;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.widget.ImageView;

import com.cpm.Constants.CommonString;
import com.example.gsk_mtt.R;

public class ShowImage extends Activity {
	public String imagepath = "";
	private ImageView viewimage;
	private SharedPreferences preferences;
	private String store_id;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.editimages_promotion);

		viewimage = (ImageView) findViewById(R.id.imageView1);
		preferences = PreferenceManager.getDefaultSharedPreferences(this);
		store_id = preferences.getString(CommonString.KEY_STORE_ID, "");

		Intent intent = getIntent();
		imagepath = intent.getStringExtra("Image");

		String str = Environment.getExternalStorageDirectory()
				+ "/GT_GSK_Images/"  + imagepath;

		BitmapFactory.Options options;
		options = new BitmapFactory.Options();
		options.inSampleSize = 8;
		Bitmap b = BitmapFactory.decodeFile(str, options);
		// viewimage.setImageBitmap(b);

		viewimage.setImageBitmap(Bitmap.createScaledBitmap(
				BitmapFactory.decodeFile(str, options), 500, 500, true));

	}

}
