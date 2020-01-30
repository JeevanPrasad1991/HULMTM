package com.cpm.gsk_mt;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.RelativeLayout;

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
            if (!serverNoticeBoard.equals(""))
                mWebView.loadUrl(serverNoticeBoard);
            mWebView.getSettings().setJavaScriptEnabled(true);
            mWebView.setWebViewClient(new MyWebViewClient());
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
