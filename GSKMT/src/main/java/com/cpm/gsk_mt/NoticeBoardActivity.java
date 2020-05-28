package com.cpm.gsk_mt;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.webkit.DownloadListener;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.annotation.RequiresApi;

import com.cpm.Constants.CommonString;
import com.cpm.xmlGetterSetter.NoticeBoardGetterSetter;
import com.example.gsk_mtt.R;

public class NoticeBoardActivity extends Activity {
    private SharedPreferences preferences;
    private SharedPreferences.Editor editor = null;
    String serverNoticeBoard, quizUrl;
    WebView mWebView;
    Button mcontinuebtn;
    Button notice_fab;
    RelativeLayout noticeRl;
    private ValueCallback<Uri> mUploadMessage;
    public ValueCallback<Uri[]> uploadMessage;
    public static final int REQUEST_SELECT_FILE = 100;
    private final static int FILECHOOSER_RESULTCODE = 1;
    WebSettings mWebSettings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notice_board);
        preferences = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        serverNoticeBoard = preferences.getString(CommonString.KEY_SERVER_NOTICE_BOARD_URL, "");
        quizUrl = preferences.getString(CommonString.KEY_SERVER_QUIZ_URL, "");
        mcontinuebtn = (Button) findViewById(R.id.header);
        mWebView = (WebView) findViewById(R.id.webview);
        notice_fab = findViewById(R.id.notice_fab);
        noticeRl = findViewById(R.id.noticeRl);

        if (CheckNetAvailability()) {
            if (!serverNoticeBoard.equals("")) {
                mWebView.loadUrl(serverNoticeBoard);
            }
        }

        mcontinuebtn.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                // TODO Auto-generated method stub
                Intent i = new Intent(NoticeBoardActivity.this, MainMenuActivity.class);
                startActivity(i);
                NoticeBoardActivity.this.finish();
            }
        });


        if (!quizUrl.equals("")) {
            noticeRl.setVisibility(View.VISIBLE);
            notice_fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    noticeRl.setVisibility(View.GONE);
                    mWebView.getSettings().setJavaScriptEnabled(true);
                    mWebView.loadUrl(quizUrl);
                    mWebView.setWebViewClient(new MyWebViewClient());
                }
            });
        } else {
            noticeRl.setVisibility(View.GONE);
        }
    }


    @Override
    protected void onResume() {
        super.onResume();
        if (!quizUrl.equals("")) {
            noticeRl.setVisibility(View.VISIBLE);
            notice_fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    noticeRl.setVisibility(View.GONE);
                    mWebView.getSettings().setJavaScriptEnabled(true);
                    mWebView.loadUrl(quizUrl);
                    mWebView.setWebViewClient(new MyWebViewClient());
                }
            });
        } else {
            noticeRl.setVisibility(View.GONE);
        }

        mWebView.setWebViewClient(new MyWebViewClient());
        mWebView.getSettings().setJavaScriptEnabled(true);
        mWebView.getSettings().setAppCacheEnabled(false);
        mWebView.clearCache(true);
        mWebView.getSettings().setSupportZoom(true);
        mWebView.getSettings().setJavaScriptEnabled(true);
        mWebView.getSettings().setUseWideViewPort(true);
        mWebView.getSettings().setAllowFileAccess(true);
        mWebView.getSettings().setAllowFileAccess(true);
        mWebView.getSettings().setAllowContentAccess(true);
        mWebView.setLongClickable(true);
        mWebSettings = mWebView.getSettings();

        mWebView.setWebChromeClient(new WebChromeClient() {
            // For 3.0+ Devices (Start)
            // onActivityResult attached before constructor
            protected void openFileChooser(ValueCallback uploadMsg, String acceptType) {
                mUploadMessage = uploadMsg;
                Intent i = new Intent(Intent.ACTION_GET_CONTENT);
                i.addCategory(Intent.CATEGORY_OPENABLE);
                i.setType("image/*");
                startActivityForResult(Intent.createChooser(i, "File Browser"), FILECHOOSER_RESULTCODE);
            }


            // For Lollipop 5.0+ Devices
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            public boolean onShowFileChooser(WebView mWebView, ValueCallback<Uri[]> filePathCallback, WebChromeClient.FileChooserParams fileChooserParams) {
                if (uploadMessage != null) {
                    uploadMessage.onReceiveValue(null);
                    uploadMessage = null;
                }

                uploadMessage = filePathCallback;
                Intent intent = fileChooserParams.createIntent();
                try {
                    startActivityForResult(intent, REQUEST_SELECT_FILE);
                } catch (ActivityNotFoundException e) {
                    uploadMessage = null;
                    Toast.makeText(getApplicationContext(), "Cannot Open File Chooser", Toast.LENGTH_LONG).show();
                    return false;
                }

                return true;
            }
        });


        mWebView.setDownloadListener(new DownloadListener() {
            public void onDownloadStart(String url, String userAgent,
                                        String contentDisposition, String mimetype,
                                        long contentLength) {
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(url));
                startActivity(i);
            }
        });


        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (!serverNoticeBoard.equals("")) {
                    mWebView.loadUrl(serverNoticeBoard);
                }
            }
        }, 300000);

    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            if (requestCode == REQUEST_SELECT_FILE) {
                if (uploadMessage == null)
                    return;
                uploadMessage.onReceiveValue(WebChromeClient.FileChooserParams.parseResult(resultCode, intent));
                uploadMessage = null;
            }
        } else if (requestCode == FILECHOOSER_RESULTCODE) {
            if (null == mUploadMessage)
                return;
            // Use MainActivity.RESULT_OK if you're implementing WebView inside Fragment
            // Use RESULT_OK only if you're implementing WebView inside an Activity
            Uri result = intent == null || resultCode != NoticeBoardActivity.RESULT_OK ? null : intent.getData();
            mUploadMessage.onReceiveValue(result);
            mUploadMessage = null;
        } else
            Toast.makeText(getApplicationContext(), "Failed to Upload Image", Toast.LENGTH_LONG).show();
    }


    private class MyWebViewClient extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            view.loadUrl(url);
            return true;
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
            view.clearCache(true);
        }

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            super.onPageStarted(view, url, favicon);
        }
    }

    @Override
    public void onBackPressed() {
        // TODO Auto-generated method stub
        AlertDialog.Builder builder = new AlertDialog.Builder(this).setTitle("Alert").setMessage("Do you want to exit ?").setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent i = new Intent(NoticeBoardActivity.this, LoginActivity.class);
                startActivity(i);
                NoticeBoardActivity.this.finish();
                dialog.dismiss();
            }
        }).setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.show();

    }

    public boolean CheckNetAvailability() {
        boolean connected = false;
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE)
                .getState() == NetworkInfo.State.CONNECTED
                || connectivityManager.getNetworkInfo(
                ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED) {
            // we are connected to a network
            connected = true;
        }
        return connected;
    }


}
