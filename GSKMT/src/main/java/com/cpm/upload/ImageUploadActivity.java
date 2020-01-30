package com.cpm.upload;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.Timer;
import java.util.Vector;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.cpm.Constants.CommonString;
import com.crashlytics.android.Crashlytics;


public class ImageUploadActivity extends Activity{

	private Handler handler = new Handler();
	ProgressBar progressBar;
	int timerCount = 0;
	final Timer timer = new Timer();

	static String datacheck = "";
	static String[] words;
	static String validity;
	public static boolean image_valid = false;
	TextView tv;

	SoapObject result;


	
	private static boolean status;

	
	public static boolean UploadImage(String path) {

		try {

			image_valid = true;
			System.out.println(path);
			BitmapFactory.Options o = new BitmapFactory.Options();
			o.inJustDecodeBounds = true;
			BitmapFactory.decodeFile(path, o);

			// The new size we want to scale to
			final int REQUIRED_SIZE = 1024;

			// Find the correct scale value. It should be the power of 2.
			int width_tmp = o.outWidth, height_tmp = o.outHeight;
			int scale = 1;

			while (true) {
				if (width_tmp < REQUIRED_SIZE && height_tmp < REQUIRED_SIZE)
					break;
				width_tmp /= 2;
				height_tmp /= 2;
				scale *= 2;
			}

			// Decode with inSampleSize
			BitmapFactory.Options o2 = new BitmapFactory.Options();
			o2.inSampleSize = scale;
			Bitmap bitmap = BitmapFactory.decodeFile(path, o2);

			ByteArrayOutputStream bao = new ByteArrayOutputStream();
			bitmap.compress(Bitmap.CompressFormat.JPEG, 90, bao);
			byte[] ba = bao.toByteArray();
			String ba1 = Base64.encodeBytes(ba);

			SoapObject request = new SoapObject(CommonString.NAMESPACE,
					CommonString.METHOD_NAME_UPLOAD_GEOTAG_IMAGE);
			
			String[] split = path.split("/");
			request.addProperty("img", ba1);
			request.addProperty("name", split[split.length-1]);

			SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
					SoapEnvelope.VER11);
			envelope.dotNet = true;
			envelope.setOutputSoapObject(request);

			HttpTransportSE androidHttpTransport = new HttpTransportSE(
					CommonString.URL);

			androidHttpTransport.call(CommonString.SOAP_ACTION_UPLOAD_GEOTAG_IMAGE,
					envelope);
		    Object result = (Object) envelope.getResponse();
			System.out.println(result);
			datacheck = result.toString();
			words = datacheck.split("\\;");
			validity = (words[0]);

			if (validity.equals("Success")) {

				image_valid = true;

			}

			if (validity.equals("Failure")) {

				image_valid = false;

			}

		} 
		catch (MalformedURLException e) {
			image_valid = false;
			e.printStackTrace();
			Crashlytics.logException(e);
		} catch (IOException e) {
			image_valid = false;
			e.printStackTrace();
			Crashlytics.logException(e);
		}
		catch (Exception e) {
			Crashlytics.logException(e);
			System.out.println(e.getMessage());

		}
		return image_valid;
	}

	public static boolean CheckGeotagImage(String path) {

		try {

			status = false;
			BitmapFactory.Options o = new BitmapFactory.Options();
			o.inJustDecodeBounds = true;
			BitmapFactory.decodeFile("/mnt/sdcard/MT_GSK_Images/"+ path, o);

			// The new size we want to scale to
			final int REQUIRED_SIZE = 1024;

			// Find the correct scale value. It should be the power of 2.
			int width_tmp = o.outWidth, height_tmp = o.outHeight;
			int scale = 1;
			while (true) {
				if (width_tmp < REQUIRED_SIZE && height_tmp < REQUIRED_SIZE)
					break;
				width_tmp /= 2;
				height_tmp /= 2;
				scale *= 2;
			}

			// Decode with inSampleSize
			BitmapFactory.Options o2 = new BitmapFactory.Options();
			o2.inSampleSize = scale;
			Bitmap bitmap = BitmapFactory.decodeFile("/mnt/sdcard/MT_GSK_Images/" + path, o2);

			if (bitmap != null) {

				status = true;

			}

		} catch (Exception e) {
			Crashlytics.logException(e);
			System.out.println(e.getMessage());
		}

		return status;
	}


}
