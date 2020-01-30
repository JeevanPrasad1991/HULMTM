package com.cpm.DailyEntry;

import org.acra.ACRA;
import org.acra.ReportField;
import org.acra.annotation.ReportsCrashes;

import android.app.Application;

@ReportsCrashes(formKey = "",
        mailTo = "jeevan.prasad@in.cpm-int.com",
        customReportContent = {
                ReportField.SHARED_PREFERENCES,
                ReportField.APP_VERSION_CODE,
                ReportField.APP_VERSION_NAME,
                ReportField.PHONE_MODEL,
                ReportField.DEVICE_ID,
                ReportField.USER_CRASH_DATE,
                ReportField.ANDROID_VERSION,
                ReportField.STACK_TRACE,
        })

public class MyApplication extends Application {
    /**
     * Called when the activity is first created.
     */

    public void onCreate() {
        super.onCreate();
        // The following line triggers the initialization of ACRA
        ACRA.init(this);
        //ACRA.getErrorReporter().putCustomData("ImeI", num);
    }

}