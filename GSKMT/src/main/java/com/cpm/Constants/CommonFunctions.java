package com.cpm.Constants;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.provider.MediaStore;

import androidx.core.app.ActivityCompat;

import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.gsk_mtt.R;
import com.google.firebase.crashlytics.FirebaseCrashlytics;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

import io.github.memfis19.annca.Annca;
import io.github.memfis19.annca.internal.configuration.AnncaConfiguration;

/**
 * Created by jeevan on 2/16/2017.
 */

public class CommonFunctions {
    public static String removed_special_char(EditText common_edt) {
        String value_edt = "";
        try {

            value_edt = common_edt.getText().toString().trim().replaceAll("[/;(!@#$%^&*?)\"]", "");
        } catch (Exception e) {
            e.printStackTrace();
        }

        return value_edt;
    }

    public static String getCurrentTime() {

        Calendar m_cal = Calendar.getInstance();
        SimpleDateFormat formatter = new SimpleDateFormat("HH:mm:ss:mmm");
        String cdate = formatter.format(m_cal.getTime());
        return cdate;

    }


    public static String getCurrentTimeHHMMSS() {
        Calendar m_cal = Calendar.getInstance();
        return m_cal.get(Calendar.HOUR_OF_DAY) + ""
                + m_cal.get(Calendar.MINUTE) + "" + m_cal.get(Calendar.SECOND);
    }


    public static boolean CheckNetAvailability(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        return activeNetwork != null && activeNetwork.isConnectedOrConnecting();
    }

    public static void startCameraActivity(Activity activity, String path) {
        String gallery_package = "";
        Uri outputFileUri = null;

        try {
            File file = new File(path);
            outputFileUri = Uri.fromFile(file);

            String defaultCameraPackage = "";
            final PackageManager packageManager = activity.getPackageManager();
            List<ApplicationInfo> list = packageManager.getInstalledApplications(PackageManager.GET_UNINSTALLED_PACKAGES);
            for (int n = 0; n < list.size(); n++) {
                if ((list.get(n).flags & ApplicationInfo.FLAG_SYSTEM) == 1) {
                /*Log.e("TAG", "Installed Applications  : " + list.get(n).loadLabel(packageManager).toString());
                Log.e("TAG", "package name  : " + list.get(n).packageName);*/

                    //temp value in case camera is gallery app above jellybean
                    if (list.get(n).loadLabel(packageManager).toString().equalsIgnoreCase("Gallery")) {
                        gallery_package = list.get(n).packageName;
                    }


                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                        if (list.get(n).loadLabel(packageManager).toString().equalsIgnoreCase("Camera")) {
                            defaultCameraPackage = list.get(n).packageName;
                            break;
                        }
                    } else {
                        if (list.get(n).loadLabel(packageManager).toString().equalsIgnoreCase("Camera")) {
                            defaultCameraPackage = list.get(n).packageName;
                            break;
                        }
                    }
                }
            }

            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, outputFileUri);
            intent.setPackage(defaultCameraPackage);
            activity.startActivityForResult(intent, 1);
            //startActivityForResult(intent, position);

        } catch (ActivityNotFoundException e) {
            e.printStackTrace();

            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, outputFileUri);
            intent.setPackage(gallery_package);
            activity.startActivityForResult(intent, 1);

        } catch (Exception e) {
            FirebaseCrashlytics.getInstance().recordException(e);
            e.printStackTrace();
        }
    }

    public static boolean convertInttoBool(String value) {
        return Integer.parseInt(value) > 0;
    }

    public String getCurrentTimeX() {

        Calendar m_cal = Calendar.getInstance();
        int hour = m_cal.get(Calendar.HOUR_OF_DAY);
        int min = m_cal.get(Calendar.MINUTE);

        String intime;

        if (hour == 0) {
            intime = "" + 12 + ":" + min + " AM";
        } else if (hour == 12) {
            intime = "" + 12 + ":" + min + " PM";
        } else {

            if (hour > 12) {
                hour = hour - 12;
                intime = "" + hour + ":" + min + " PM";
            } else {
                intime = "" + hour + ":" + min + " AM";
            }
        }
        return intime;
    }


    public static void startAnncaCameraActivity(final Context context, final String path) {
        final AnncaConfiguration.Builder dialogDemo = new AnncaConfiguration.Builder((Activity) context, CommonString.CAPTURE_MEDIA);
        dialogDemo.setMediaAction(AnncaConfiguration.MEDIA_ACTION_PHOTO);
        dialogDemo.setMediaResultBehaviour(AnncaConfiguration.PREVIEW);
        final Dialog dialog = new Dialog(context);
        dialog.setContentView(R.layout.view_horizontal_camera);
        dialog.setCancelable(false);
        dialog.show();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                dialog.dismiss();
                if (ActivityCompat.checkSelfPermission(context, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
                    return;
                }
                new Annca(dialogDemo.build()).launchCamera(path);
            }
        }, 3000);
    }

    public static String setMetadataAtImages(String store_Nm, String store_Id, String type, String userId) {
        String ss = "Store Name : " + store_Nm + " | Store Id : " + store_Id + " " + " | Merchandiser Id : " + userId + " | Image Type : " + type;
        return ss;
    }

    public static String setMetadataAtImagesforcategory(String store_Nm, String store_Id, String type, String userId, String category) {
        String ss = "Store Name : " + store_Nm + " | Store Id : " + store_Id + " " + " | Merchandiser Id : " + userId + " | Image Type : " + type + " | Category : " + category;
        return ss;
    }

    public static String setMetadataAtImagesforattendence(String type, String userId) {
        String ss = "Merchandiser Id : " + userId + " | Image Type : " + type;
        return ss;
    }


    public static Bitmap addMetadataAndTimeStampToImage(Context context, final String path, String metadata, String visit_date) {
        Bitmap bmp1 = BitmapFactory.decodeFile(path);
        View view = LayoutInflater.from(context).inflate(R.layout.preview_image, null);
        view.layout(0, 0, bmp1.getWidth(), bmp1.getHeight());
        Bitmap bmp = getViewBitmap(view, bmp1, path, metadata, visit_date, context);
        try {
            bmp.compress(Bitmap.CompressFormat.JPEG, 100, new FileOutputStream(new File(path)));
            return bmp;
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return bmp1;
        }
    }

    public static Bitmap getViewBitmap(View view, Bitmap bmp, String path, String metadata, String visit_date, Context context) {
        try {
            //Get the dimensions of the view so we can re-layout the view at its current size
            SimpleDateFormat sdf = new SimpleDateFormat("MM-dd-yyyy HH:mm:ss");
            String dateTime = sdf.format(Calendar.getInstance().getTime());
            String copm = dateTime;
            ImageView temp_img = (ImageView) view.findViewById(R.id.temp_img);
            //ImageView temp_map = (ImageView) view.findViewById(R.id.temp_map);
            TextView storeM = (TextView) view.findViewById(R.id.storeM);
            int copleteValue = 0;
            try {
                copm = copm.replaceAll("[- ]", " ");
                String[] items = copm.split(":");
                String seconds = items[2];
                int lastIndex;
                lastIndex = Integer.parseInt(seconds);
                int day = Integer.parseInt(visit_date.substring(3, 5));
                int a = Integer.parseInt("10") * Integer.parseInt("40");
                a = a + day;
                lastIndex = lastIndex * 2;
                copleteValue = a + lastIndex;
            } catch (NumberFormatException e) {
                FirebaseCrashlytics.getInstance().recordException(e);
                e.printStackTrace();
            }


            // storeM.setTextSize(5 * context.getResources().getDisplayMetrics().density);
            storeM.setText(metadata);
            //timestamp on image
            Bitmap dest = Bitmap.createBitmap(bmp.getWidth(), bmp.getHeight(), Bitmap.Config.ARGB_8888);
            Canvas cs = new Canvas(dest);
            Paint tPaint = new Paint();
            tPaint.setTextSize(17);
            tPaint.setColor(Color.RED);
            tPaint.setStyle(Paint.Style.FILL_AND_STROKE);
            cs.drawBitmap(bmp, 0f, 0f, null);
            float height_ = tPaint.measureText("yY");
            cs.drawText(dateTime + "[" + "10" + "] " + String.valueOf(copleteValue), 20f, height_ + 15f, tPaint);
            try {
                dest.compress(Bitmap.CompressFormat.JPEG, 100, new FileOutputStream(new File(path)));
            } catch (FileNotFoundException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

            bmp = BitmapFactory.decodeFile(path);
            temp_img.setImageBitmap(bmp);
            int width = bmp.getWidth();
            int height = bmp.getHeight();
            int measuredWidth = View.MeasureSpec.makeMeasureSpec(width, View.MeasureSpec.EXACTLY);
            int measuredHeight = View.MeasureSpec.makeMeasureSpec(height, View.MeasureSpec.EXACTLY);
            //Cause the view to re-layout
            view.measure(measuredWidth, measuredHeight);
            view.layout(0, 0, view.getMeasuredWidth(), view.getMeasuredHeight());
            //Create a bitmap backed Canvas to draw the view into
            Bitmap b = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
            Canvas c = new Canvas(b);
            view.draw(c);
            return b;
        } finally {
        }
    }

    public static String return_decimal_value(String value1) {
        String final_value = "";
        try {
            String tempsubstr = value1.substring(0, 1);
            if (tempsubstr.equals(".")) {
                if (value1.length() == 2) {
                    char tempsubstrsecond = value1.charAt(1);
                    final_value = "0" + tempsubstr + "" + tempsubstrsecond;
                } else {
                    final_value = "0" + tempsubstr + "0";
                }
            } else {
                if (value1.contains(".")) {
                    String strlastindex = value1.substring(value1.lastIndexOf(".") + 1);
                    if (strlastindex == null || strlastindex.equals("")) {
                        final_value = value1 + "0";
                    } else {
                        final_value = value1;
                    }
                } else {
                    final_value = value1;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return final_value;
    }

}
