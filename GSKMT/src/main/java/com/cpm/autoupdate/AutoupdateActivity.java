package com.cpm.autoupdate;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.DecimalFormat;



import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import androidx.core.content.FileProvider;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.cpm.Constants.CommonString;
import com.cpm.message.AlertMessage;
import com.example.gsk_mtt.R;
import com.google.firebase.crashlytics.FirebaseCrashlytics;


public class AutoupdateActivity extends Activity {
	String versionCode;
	int length;
	private Dialog dialog;
	private ProgressBar pb;
	private TextView percentage, message;
	private Data data;
	String path = "", p, s;
	private boolean status;
	boolean update_flag = true;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Intent intent = getIntent();
		path = intent.getStringExtra(CommonString.KEY_PATH);
		status = intent.getBooleanExtra(CommonString.KEY_STATUS, false);
		if (status)
			setContentView(R.layout.login);
		else
			setContentView(R.layout.login);
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle("Parinaam");
		builder.setMessage("New Update Available.")
				.setCancelable(false)
				.setPositiveButton("OK", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(AutoupdateActivity.this);
						SharedPreferences.Editor editor = preferences.edit();
						editor.clear();
						editor.commit();
						new DownloadTask(AutoupdateActivity.this).execute();

					}
				});
		AlertDialog alert = builder.create();

		alert.show();

	}

	private class DownloadTask extends AsyncTask<Void, Data, String> {
		private Context context;
		DownloadTask(Context context) {
			this.context = context;
		}
		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();
			dialog = new Dialog(context);
			dialog.setContentView(R.layout.custom);
			dialog.setTitle("Download");
			dialog.setCancelable(false);
			dialog.show();
			pb = (ProgressBar) dialog.findViewById(R.id.progressBar1);
			percentage = (TextView) dialog.findViewById(R.id.percentage);
			message = (TextView) dialog.findViewById(R.id.message);

		}

		@Override
		protected String doInBackground(Void... params) {
			// TODO Auto-generated method stub
			try {
				data = new Data();
				data.name = "Downloading Application";
				publishProgress(data);
				versionCode = getPackageManager().getPackageInfo(getPackageName(), 0).versionName;
				data.name = "Upgraditing Version : " + versionCode;
				publishProgress(data);
				// download application
				URL url = new URL(path);
				HttpURLConnection c = (HttpURLConnection) url.openConnection();
				c.setRequestMethod("GET");
				// c.setDoOutput(true);
				c.getResponseCode();
				c.connect();
				length = c.getContentLength();
				String size = new DecimalFormat("##.##").format((double) ((double) length / 1024) / 1024) + " MB";
				String PATH = Environment.getExternalStorageDirectory() + "/download/";
				File file = new File(PATH);
				file.mkdirs();
				File outputFile = new File(file, "app.apk");
				FileOutputStream fos = new FileOutputStream(outputFile);
				InputStream is = c.getInputStream();
				int bytes = 0;
				byte[] buffer = new byte[1024];
				int len1 = 0;
				while ((len1 = is.read(buffer)) != -1) {
					bytes = (bytes + len1);
					s = new DecimalFormat("##.##").format((double) ((double) (bytes / 1024)) / 1024);
					p = s.length() == 3 ? s + "0" : s;
					p = p + " MB";
					data.value = (int) ((double) (((double) bytes) / length) * 100);
					data.name = "Download " + p + "/" + size;
					publishProgress(data);
					fos.write(buffer, 0, len1);
				}
				fos.close();
				is.close();
			} catch (PackageManager.NameNotFoundException e) {
				update_flag = false;
				// TODO Auto-generated catch block
				final AlertMessage message = new AlertMessage(
						AutoupdateActivity.this,
						AlertMessage.MESSAGE_EXCEPTION, "store_checking", e);
				runOnUiThread(new Runnable() {

					@Override
					public void run() {
						// TODO Auto-generated method stub
						message.showMessage();
					}
				});
			} catch (MalformedURLException e) {
				update_flag = false;
				final AlertMessage message = new AlertMessage(
						AutoupdateActivity.this,
						AlertMessage.MESSAGE_EXCEPTION, "store_checking", e);
				runOnUiThread(new Runnable() {

					@Override
					public void run() {
						// TODO Auto-generated method stub
						message.showMessage();
					}
				});

			} catch (IOException e) {
				update_flag = false;
				final AlertMessage message = new AlertMessage(
						AutoupdateActivity.this,
						AlertMessage.MESSAGE_SOCKETEXCEPTION, "store_checking", e);
				runOnUiThread(new Runnable() {

					@Override
					public void run() {
						// TODO Auto-generated method stub
						message.showMessage();
					}
				});
			} catch (Exception e) {
				update_flag = false;
				FirebaseCrashlytics.getInstance().recordException(e);
				final AlertMessage message = new AlertMessage(
						AutoupdateActivity.this, AlertMessage.MESSAGE_EXCEPTION, "store_checking", e);
				runOnUiThread(new Runnable() {

					@Override
					public void run() {
						// TODO Auto-generated method stub
						message.showMessage();
					}
				});
			}

			if (update_flag) {
				return CommonString.KEY_SUCCESS;
			} else {
				return "";
			}
		}

		@Override
		protected void onProgressUpdate(Data... values) {
			// TODO Auto-generated method stub
			pb.setProgress(values[0].value);
			percentage.setText(values[0].value + "%");
			message.setText(values[0].name);

		}

		@Override
		protected void onPostExecute(String result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
			dialog.dismiss();
			if (update_flag) {
				if (result.equals(CommonString.KEY_SUCCESS)) {
					File toInstall = new File(Environment.getExternalStorageDirectory() + "/download/" + "app.apk");
					if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
						Uri apkUri = FileProvider.getUriForFile(AutoupdateActivity.this, "com.example.gsk_mtt.fileprovider", toInstall);
						Intent intent = new Intent(Intent.ACTION_INSTALL_PACKAGE);
						intent.setData(apkUri);
						intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
						startActivity(intent);
					} else {
						Uri apkUri = Uri.fromFile(toInstall);
						Intent intent = new Intent(Intent.ACTION_VIEW);
						intent.setDataAndType(apkUri, "application/vnd.android.package-archive");
						intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
						startActivity(intent);
					}

					AutoupdateActivity.this.finish();
				}
			}
		}
	}

	class Data {
		int value;
		String name;
	}

}
