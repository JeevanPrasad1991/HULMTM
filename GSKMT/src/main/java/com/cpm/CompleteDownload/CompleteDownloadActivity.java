package com.cpm.CompleteDownload;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.text.DecimalFormat;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import com.cpm.Constants.CommonString;

import com.cpm.database.GSKMTDatabase;
import com.cpm.delegates.TableBean;
import com.cpm.message.AlertMessage;
import com.cpm.xmlGetterSetter.AdditionalGetterSetter;
import com.cpm.xmlGetterSetter.CompetitionGetterSetter;
import com.cpm.xmlGetterSetter.DisplayGetterSetter;
import com.cpm.xmlGetterSetter.EmpMeetingStatus;
import com.cpm.xmlGetterSetter.FailureGetterSetter;
import com.cpm.xmlGetterSetter.JCPGetterSetter;
import com.cpm.xmlGetterSetter.MappingCompetitionPromotionGetterSetter;
import com.cpm.xmlGetterSetter.MappingWellnessSos;
import com.cpm.xmlGetterSetter.NonWorkingAttendenceGetterSetter;
import com.cpm.xmlGetterSetter.NonWorkingGetterSetter;
import com.cpm.xmlGetterSetter.PdrFacingStockGetterSetter;
import com.cpm.xmlGetterSetter.PromotionalDataSetterGetter;
import com.cpm.xmlGetterSetter.QuestionGetterSetter;
import com.cpm.xmlGetterSetter.SKUGetterSetter;
import com.cpm.xmlGetterSetter.SOSTargetGetterSetter;
import com.cpm.xmlGetterSetter.ShelfMaster;
import com.cpm.xmlGetterSetter.StockMappingGetterSetter;
import com.cpm.xmlGetterSetter.StoreWise_Pss;
import com.cpm.xmlGetterSetter.TDSGetterSetter;
import com.cpm.xmlGetterSetter.TargetToothpestforOHCGetterSetter;
import com.cpm.xmlGetterSetter.catmanMapping;
import com.cpm.xmlHandler.XMLHandlers;
import com.crashlytics.android.Crashlytics;
import com.example.gsk_mtt.R;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;

import android.view.Window;
import android.widget.ProgressBar;
import android.widget.TextView;

public class CompleteDownloadActivity extends Activity {
    private Dialog dialog;
    private ProgressBar pb;
    private TextView percentage, message;
    private Data data;
    int eventType;
    PromotionalDataSetterGetter promotionData;
    ShelfMaster shelfmasterData;
    MappingWellnessSos mappingWellnessSosData;
    EmpMeetingStatus empstatusData;
    StoreWise_Pss storeWisePssData;
    catmanMapping catmanStockAfter;
    AdditionalGetterSetter additionalData;
    NonWorkingGetterSetter reasonData, subReasonData;
    SOSTargetGetterSetter sostarget;
    QuestionGetterSetter questionData, questionMappingData;
    SKUGetterSetter skudata, brandData, alltable;
    JCPGetterSetter jcpData, geotagData;
    StockMappingGetterSetter stockData;
    DisplayGetterSetter displayData;
    CompetitionGetterSetter comdata;
    TDSGetterSetter TDsData;
    PdrFacingStockGetterSetter pdrFacingStockGetterSetter;
    MappingCompetitionPromotionGetterSetter mappingCompetitionPromotionGetterSetter;
    NonWorkingAttendenceGetterSetter nonWorkingAttendenceGetterSetter;
    TargetToothpestforOHCGetterSetter targetToothpestforOHCObject;
    GSKMTDatabase db;
    TableBean tb;
    String user_name;
    SharedPreferences preferences;
    private SharedPreferences.Editor editor = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mainpage);
        tb = new TableBean();
        db = new GSKMTDatabase(this);

        preferences = PreferenceManager.getDefaultSharedPreferences(CompleteDownloadActivity.this);
        user_name = preferences.getString(CommonString.KEY_USERNAME, "");

        new BackgroundTask(this).execute();
    }

    @Override
    protected void onResume() {
        super.onResume();
        db.open();
    }

    class Data {
        int value;
        String name;
    }

    private class BackgroundTask extends AsyncTask<Void, Data, String> {
        private Context context;

        BackgroundTask(Context context) {
            this.context = context;
        }

        @Override
        protected void onPreExecute() {
            // TODO Auto-generated method stub
            super.onPreExecute();
            dialog = new Dialog(context);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setContentView(R.layout.custom);
            dialog.setTitle("Download PJP...");
            dialog.setCancelable(false);
            dialog.show();
            pb = (ProgressBar) dialog.findViewById(R.id.progressBar1);
            percentage = (TextView) dialog.findViewById(R.id.percentage);
            message = (TextView) dialog.findViewById(R.id.message);

        }

        @Override
        protected String doInBackground(Void... params) {
            try {
                data = new Data();
                data.value = 5;
                data.name = "Downloading";
                publishProgress(data);
                XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
                factory.setNamespaceAware(true);
                XmlPullParser xpp = factory.newPullParser();
                // JCP Master
                SoapObject request = new SoapObject(CommonString.NAMESPACE, CommonString.METHOD_NAME_UNIVERSAL_DOWNLOAD);
                request.addProperty("UserName", user_name);
                request.addProperty("Type", "JOURNEYPLAN");
                SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
                envelope.dotNet = true;
                envelope.setOutputSoapObject(request);
                HttpTransportSE androidHttpTransport = new HttpTransportSE(CommonString.URL);
                androidHttpTransport.call(CommonString.SOAP_ACTION_UNIVERSAL, envelope);
                Object result = (Object) envelope.getResponse();

                if (result.toString().equalsIgnoreCase(CommonString.KEY_FALSE)) {
                    //return CommonString.METHOD_NAME_STORE_LAYOUT;
                }

                // for failure
                xpp.setInput(new StringReader(result.toString()));
                xpp.next();
                eventType = xpp.getEventType();
                FailureGetterSetter failureGetterSetter = XMLHandlers.failureXMLHandler(xpp, eventType);
                if (failureGetterSetter.getStatus().equalsIgnoreCase(CommonString.KEY_FAILURE)) {
                    return CommonString.METHOD_NAME_UNIVERSAL_DOWNLOAD + "," + failureGetterSetter.getErrorMsg();
                }
                if (!result.toString().equalsIgnoreCase(CommonString.KEY_FALSE)) {
                    xpp.setInput(new StringReader(result.toString()));
                    xpp.next();
                    eventType = xpp.getEventType();
                    jcpData = XMLHandlers.JCPXMLHandler(xpp, eventType);
                    String jcp_table = jcpData.getMeta_data();
                    TableBean.setJorney_plan_table(jcp_table);
                }
                if (jcpData.getSTORE_ID().size() == 0) {
                    return "NO JCP for Today";
                } else {
                    data.value = 10;
                    data.name = "JCP Data Downloading";
                    publishProgress(data);
                }


                request = new SoapObject(CommonString.NAMESPACE, CommonString.METHOD_NAME_UNIVERSAL_DOWNLOAD);
                // request.addProperty("UserName", "admin");
                request.addProperty("UserName", user_name);
                request.addProperty("Type", "SKUMASTER");
                envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
                envelope.dotNet = true;
                envelope.setOutputSoapObject(request);
                androidHttpTransport = new HttpTransportSE(CommonString.URL);
                androidHttpTransport.call(CommonString.SOAP_ACTION_UNIVERSAL, envelope);
                result = (Object) envelope.getResponse();
                if (result.toString().equalsIgnoreCase(CommonString.KEY_FALSE)) {
                    return CommonString.METHOD_NAME_UNIVERSAL_DOWNLOAD;

                }
                if (result.toString().equalsIgnoreCase(CommonString.KEY_NO_DATA)) {
                    return CommonString.METHOD_NAME_UNIVERSAL_DOWNLOAD;
                }

                // for failure
                xpp.setInput(new StringReader(result.toString()));
                xpp.next();
                eventType = xpp.getEventType();
                failureGetterSetter = XMLHandlers.failureXMLHandler(xpp, eventType);
                if (failureGetterSetter.getStatus().equalsIgnoreCase(CommonString.KEY_FAILURE)) {
                    return CommonString.METHOD_NAME_UNIVERSAL_DOWNLOAD + "," + failureGetterSetter.getErrorMsg();
                }

                if (!result.toString().equalsIgnoreCase(CommonString.KEY_FALSE)) {
                    xpp.setInput(new StringReader(result.toString()));
                    xpp.next();
                    eventType = xpp.getEventType();
                    skudata = XMLHandlers.SKUXMLHandler(xpp, eventType);
                    String sku_table = skudata.getMeta_data();
                    TableBean.setSku_master_table(sku_table);
                    data.value = 15;
                    data.name = "Sku Master";
                    publishProgress(data);
                }


                // Brand - master list
                request = new SoapObject(CommonString.NAMESPACE, CommonString.METHOD_NAME_UNIVERSAL_DOWNLOAD);
                request.addProperty("UserName", user_name);
                request.addProperty("Type", "BRANDMASTER");
                envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
                envelope.dotNet = true;
                envelope.setOutputSoapObject(request);

                androidHttpTransport = new HttpTransportSE(CommonString.URL);

                androidHttpTransport.call(CommonString.SOAP_ACTION_UNIVERSAL, envelope);
                result = (Object) envelope.getResponse();

                if (result.toString().equalsIgnoreCase(CommonString.KEY_FALSE)) {
                    //	return CommonString.METHOD_NAME_STORE_SIZE;
                }

                if (result.toString().equalsIgnoreCase(CommonString.KEY_NO_DATA)) {
                    //	return CommonString.METHOD_NAME_STORE_SIZE;
                }

                // for failure
                xpp.setInput(new StringReader(result.toString()));
                xpp.next();
                eventType = xpp.getEventType();
                failureGetterSetter = XMLHandlers.failureXMLHandler(xpp, eventType);

                if (failureGetterSetter.getStatus().equalsIgnoreCase(
                        CommonString.KEY_FAILURE)) {
                    return CommonString.METHOD_NAME_UNIVERSAL_DOWNLOAD + ","
                            + failureGetterSetter.getErrorMsg();
                }


                if (!result.toString().equalsIgnoreCase(CommonString.KEY_FALSE)) {
                    //return CommonString.METHOD_NAME_JCP;
                    xpp.setInput(new StringReader(result.toString()));
                    xpp.next();
                    eventType = xpp.getEventType();
                    brandData = XMLHandlers.BrandXMLHandler(xpp, eventType);
                    String brand_table = brandData.getMeta_data();
                    TableBean.setBrand_master_table(brand_table);

                    data.value = 20;
                    data.name = "Brand Master Data";
                    publishProgress(data);
                }


                // Download Geo Tag Stores


                request = new SoapObject(CommonString.NAMESPACE,
                        CommonString.METHOD_NAME_UNIVERSAL_DOWNLOAD);

                request.addProperty("UserName", user_name);
                request.addProperty("Type", "GEOTAG_STORE");

                envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
                envelope.dotNet = true;
                envelope.setOutputSoapObject(request);

                androidHttpTransport = new HttpTransportSE(CommonString.URL);

                androidHttpTransport.call(CommonString.SOAP_ACTION_UNIVERSAL,
                        envelope);
                result = (Object) envelope.getResponse();

                if (result.toString().equalsIgnoreCase(CommonString.KEY_FALSE)) {
                    //	return CommonString.METHOD_NAME_STORE_SIZE;
                }

                if (!result.toString().equalsIgnoreCase(CommonString.KEY_FALSE)) {
                    //return CommonString.METHOD_NAME_JCP;
                    xpp.setInput(new StringReader(result.toString()));
                    xpp.next();
                    eventType = xpp.getEventType();

                    geotagData = XMLHandlers.GeoTagXMLHandler(xpp, eventType);
                    String geo_tag_table = geotagData.getGeo_meta_data();
                    TableBean.setGeo_tag_table(geo_tag_table);
                    data.value = 25;
                    data.name = "GEOTAG STORE Data";
                    publishProgress(data);
                }

                // download STOCKMAPPING Mapping
                request = new SoapObject(CommonString.NAMESPACE, CommonString.METHOD_NAME_UNIVERSAL_DOWNLOAD);
                request.addProperty("UserName", user_name);
                request.addProperty("Type", "STOCK_MAPPING");
                envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
                envelope.dotNet = true;
                envelope.setOutputSoapObject(request);
                androidHttpTransport = new HttpTransportSE(CommonString.URL);
                androidHttpTransport.call(CommonString.SOAP_ACTION_UNIVERSAL, envelope);
                result = (Object) envelope.getResponse();
                if (result.toString().equalsIgnoreCase(CommonString.KEY_FALSE)) {

                }
                if (result.toString().equalsIgnoreCase(CommonString.KEY_NO_DATA)) {

                }
                if (result.toString().equalsIgnoreCase(CommonString.KEY_FAILURE)) {
                    // for failure
                    xpp.setInput(new StringReader(result.toString()));
                    xpp.next();
                    eventType = xpp.getEventType();
                    failureGetterSetter = XMLHandlers.failureXMLHandler(xpp,
                            eventType);

                    if (failureGetterSetter.getStatus().equalsIgnoreCase(
                            CommonString.KEY_FAILURE)) {
                        return CommonString.METHOD_NAME_UNIVERSAL_DOWNLOAD + ","
                                + failureGetterSetter.getErrorMsg();
                    }
                }

                if (!result.toString()
                        .equalsIgnoreCase(CommonString.KEY_FALSE)) {
                    //return CommonString.METHOD_NAME_JCP;

                    xpp.setInput(new StringReader(result.toString()));
                    xpp.next();
                    eventType = xpp.getEventType();
                    stockData = XMLHandlers.StockMappingXMLHandler(xpp, eventType);
                    String stock_table = stockData.getMeta_data();
                    TableBean.setStock_mapping_master_table(stock_table);
                    data.value = 30;
                    data.name = "STOCK MAPPING Data";
                    publishProgress(data);
                }


                // starting download for promotional master

                request = new SoapObject(CommonString.NAMESPACE, CommonString.METHOD_NAME_UNIVERSAL_DOWNLOAD);
                request.addProperty("UserName", user_name);
                request.addProperty("Type", "DISPLAYMASTER_NEW_WITHPATH");
                envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
                envelope.dotNet = true;
                envelope.setOutputSoapObject(request);
                androidHttpTransport = new HttpTransportSE(CommonString.URL);
                androidHttpTransport.call(CommonString.SOAP_ACTION_UNIVERSAL, envelope);
                result = (Object) envelope.getResponse();
                if (result.toString().equalsIgnoreCase(CommonString.KEY_FALSE)) {
                }

                if (result.toString().equalsIgnoreCase(CommonString.KEY_NO_DATA)) {
                }
                // for failure

                if (result.toString().equalsIgnoreCase(CommonString.KEY_FAILURE)) {
                    xpp.setInput(new StringReader(result.toString()));
                    xpp.next();
                    eventType = xpp.getEventType();
                    failureGetterSetter = XMLHandlers.failureXMLHandler(xpp, eventType);
                    if (failureGetterSetter.getStatus().equalsIgnoreCase(CommonString.KEY_FAILURE)) {
                        return CommonString.METHOD_NAME_UNIVERSAL_DOWNLOAD + "," + failureGetterSetter.getErrorMsg();
                    }

                }

                if (!result.toString().equalsIgnoreCase(CommonString.KEY_FALSE)) {
                    //return CommonString.METHOD_NAME_JCP;

                    xpp.setInput(new StringReader(result.toString()));
                    xpp.next();
                    eventType = xpp.getEventType();
                    displayData = XMLHandlers.DisplayXMLHandler(xpp, eventType);
                    String display_table = displayData.getMeta_data();
                    TableBean.setDisplay_table(display_table);
                    data.value = 35;
                    data.name = "DISPLAY MASTER Data";
                    publishProgress(data);
                }

//				 download DTSMAPPING

                request = new SoapObject(CommonString.NAMESPACE,
                        CommonString.METHOD_NAME_UNIVERSAL_DOWNLOAD);

                request.addProperty("UserName", user_name);
//				request.addProperty("Type","DTSMAPPING");

                /*new Change*/

                request.addProperty("Type", "TOT_MAPPING");

                envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
                envelope.dotNet = true;
                envelope.setOutputSoapObject(request);

                androidHttpTransport = new HttpTransportSE(CommonString.URL);

                androidHttpTransport.call(
                        CommonString.SOAP_ACTION_UNIVERSAL, envelope);
                result = (Object) envelope.getResponse();

                if (result.toString().equalsIgnoreCase(CommonString.KEY_FALSE)) {
                    //	return CommonString.METHOD_NAME_DownLoad_Promotional_Master;
                }

                if (result.toString()
                        .equalsIgnoreCase(CommonString.KEY_NO_DATA)) {
                    //return CommonString.METHOD_NAME_DownLoad_Promotional_Master;
                }

                // for failure


                if (result.toString()
                        .equalsIgnoreCase(CommonString.KEY_FAILURE)) {

                    xpp.setInput(new StringReader(result.toString()));
                    xpp.next();
                    eventType = xpp.getEventType();
                    failureGetterSetter = XMLHandlers.failureXMLHandler(xpp,
                            eventType);

                    if (failureGetterSetter.getStatus().equalsIgnoreCase(
                            CommonString.KEY_FAILURE)) {
                        return CommonString.METHOD_NAME_UNIVERSAL_DOWNLOAD
                                + "," + failureGetterSetter.getErrorMsg();
                    }

                }

                if (!result.toString()
                        .equalsIgnoreCase(CommonString.KEY_FALSE)) {
                    //return CommonString.METHOD_NAME_JCP;

                    xpp.setInput(new StringReader(result.toString()));
                    xpp.next();
                    eventType = xpp.getEventType();
                    TDsData = XMLHandlers.TDSXMLHandler(xpp, eventType);
                    String tds_table = TDsData.getMeta_data();
                    TableBean.setTds_table(tds_table);

                    data.value = 40;
                    data.name = "TOT MAPPING Data";
                    publishProgress(data);
                }

//				 download Non-working reason

                request = new SoapObject(CommonString.NAMESPACE,
                        CommonString.METHOD_NAME_UNIVERSAL_DOWNLOAD);

                request.addProperty("UserName", user_name);
                request.addProperty("Type", "NONWORKINGREASON_NEW");

                envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
                envelope.dotNet = true;
                envelope.setOutputSoapObject(request);

                androidHttpTransport = new HttpTransportSE(CommonString.URL);

                androidHttpTransport.call(
                        CommonString.SOAP_ACTION_UNIVERSAL, envelope);
                result = (Object) envelope.getResponse();

                if (result.toString().equalsIgnoreCase(CommonString.KEY_FALSE)) {
                    //	return CommonString.METHOD_NAME_DownLoad_Promotional_Master;
                }

                if (result.toString()
                        .equalsIgnoreCase(CommonString.KEY_NO_DATA)) {
                    //return CommonString.METHOD_NAME_DownLoad_Promotional_Master;
                }

                // for failure


                if (result.toString()
                        .equalsIgnoreCase(CommonString.KEY_FAILURE)) {

                    xpp.setInput(new StringReader(result.toString()));
                    xpp.next();
                    eventType = xpp.getEventType();
                    failureGetterSetter = XMLHandlers.failureXMLHandler(xpp,
                            eventType);

                    if (failureGetterSetter.getStatus().equalsIgnoreCase(
                            CommonString.KEY_FAILURE)) {
                        return CommonString.METHOD_NAME_UNIVERSAL_DOWNLOAD
                                + "," + failureGetterSetter.getErrorMsg();
                    }

                }

                if (!result.toString()
                        .equalsIgnoreCase(CommonString.KEY_FALSE)) {
                    //return CommonString.METHOD_NAME_JCP;

                    xpp.setInput(new StringReader(result.toString()));
                    xpp.next();
                    eventType = xpp.getEventType();
                    reasonData = XMLHandlers.NonWorkingReasonXMLHandler(xpp, eventType);
                    String reason_table = reasonData.getReason_meta_data();
                    TableBean.setNonworkingReason_table(reason_table);

                    data.value = 45;
                    data.name = "NONWORKING REASON Data";
                    publishProgress(data);
                }

                // sub- reason non working Downloading

                request = new SoapObject(CommonString.NAMESPACE,
                        CommonString.METHOD_NAME_UNIVERSAL_DOWNLOAD);

                request.addProperty("UserName", user_name);
                request.addProperty("Type", "NONWORKINGSUBREASON");

                envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
                envelope.dotNet = true;
                envelope.setOutputSoapObject(request);

                androidHttpTransport = new HttpTransportSE(CommonString.URL);

                androidHttpTransport.call(
                        CommonString.SOAP_ACTION_UNIVERSAL, envelope);
                result = (Object) envelope.getResponse();

                if (result.toString().equalsIgnoreCase(CommonString.KEY_FALSE)) {
                    //	return CommonString.METHOD_NAME_DownLoad_Promotional_Master;
                }

                if (result.toString()
                        .equalsIgnoreCase(CommonString.KEY_NO_DATA)) {
                    //return CommonString.METHOD_NAME_DownLoad_Promotional_Master;
                }

                // for failure


                if (result.toString()
                        .equalsIgnoreCase(CommonString.KEY_FAILURE)) {

                    xpp.setInput(new StringReader(result.toString()));
                    xpp.next();
                    eventType = xpp.getEventType();
                    failureGetterSetter = XMLHandlers.failureXMLHandler(xpp,
                            eventType);

                    if (failureGetterSetter.getStatus().equalsIgnoreCase(
                            CommonString.KEY_FAILURE)) {
                        return CommonString.METHOD_NAME_UNIVERSAL_DOWNLOAD
                                + "," + failureGetterSetter.getErrorMsg();
                    }

                }

                if (!result.toString()
                        .equalsIgnoreCase(CommonString.KEY_FALSE)) {
                    //return CommonString.METHOD_NAME_JCP;

                    xpp.setInput(new StringReader(result.toString()));
                    xpp.next();
                    eventType = xpp.getEventType();
                    subReasonData = XMLHandlers.NonWorkingSubReasonXMLHandler(xpp, eventType);
                    String sub_reason = subReasonData.getSubreason_meta_data();
                    TableBean.setNonworkingSubReason_table(sub_reason);

                    data.value = 55;
                    data.name = "NONWORKING SUBREASON Data";
                    publishProgress(data);
                }


                // Downloading the question Master

                request = new SoapObject(CommonString.NAMESPACE,
                        CommonString.METHOD_NAME_UNIVERSAL_DOWNLOAD);

                request.addProperty("UserName", user_name);
                request.addProperty("Type", "QUESTION_MASTER");

                envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
                envelope.dotNet = true;
                envelope.setOutputSoapObject(request);

                androidHttpTransport = new HttpTransportSE(CommonString.URL);

                androidHttpTransport.call(
                        CommonString.SOAP_ACTION_UNIVERSAL, envelope);
                result = (Object) envelope.getResponse();

                if (result.toString().equalsIgnoreCase(CommonString.KEY_FALSE)) {
                    //	return CommonString.METHOD_NAME_DownLoad_Promotional_Master;
                }

                if (result.toString()
                        .equalsIgnoreCase(CommonString.KEY_NO_DATA)) {
                    //return CommonString.METHOD_NAME_DownLoad_Promotional_Master;
                }

                // for failure


                if (result.toString()
                        .equalsIgnoreCase(CommonString.KEY_FAILURE)) {

                    xpp.setInput(new StringReader(result.toString()));
                    xpp.next();
                    eventType = xpp.getEventType();
                    failureGetterSetter = XMLHandlers.failureXMLHandler(xpp,
                            eventType);

                    if (failureGetterSetter.getStatus().equalsIgnoreCase(
                            CommonString.KEY_FAILURE)) {
                        return CommonString.METHOD_NAME_UNIVERSAL_DOWNLOAD
                                + "," + failureGetterSetter.getErrorMsg();
                    }

                }

                if (!result.toString()
                        .equalsIgnoreCase(CommonString.KEY_FALSE)) {
                    //return CommonString.METHOD_NAME_JCP;

                    xpp.setInput(new StringReader(result.toString()));
                    xpp.next();
                    eventType = xpp.getEventType();
                    questionData = XMLHandlers.QuestionXMLHandler(xpp, eventType);
                    String question_meta_data = questionData.getMeta_data();

                    TableBean.setQuestion_table(question_meta_data);

                    data.value = 60;
                    data.name = "QUESTION MASTER ";
                    publishProgress(data);
                }


                // Downloading the question Mapping

                request = new SoapObject(CommonString.NAMESPACE,
                        CommonString.METHOD_NAME_UNIVERSAL_DOWNLOAD);

                request.addProperty("UserName", user_name);
                request.addProperty("Type", "QUESTION_MAPPING");

                envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
                envelope.dotNet = true;
                envelope.setOutputSoapObject(request);

                androidHttpTransport = new HttpTransportSE(CommonString.URL);

                androidHttpTransport.call(
                        CommonString.SOAP_ACTION_UNIVERSAL, envelope);
                result = (Object) envelope.getResponse();

                if (result.toString().equalsIgnoreCase(CommonString.KEY_FALSE)) {
                    //	return CommonString.METHOD_NAME_DownLoad_Promotional_Master;
                }

                if (result.toString()
                        .equalsIgnoreCase(CommonString.KEY_NO_DATA)) {
                }

                // for failure


                if (result.toString()
                        .equalsIgnoreCase(CommonString.KEY_FAILURE)) {

                    xpp.setInput(new StringReader(result.toString()));
                    xpp.next();
                    eventType = xpp.getEventType();
                    failureGetterSetter = XMLHandlers.failureXMLHandler(xpp,
                            eventType);

                    if (failureGetterSetter.getStatus().equalsIgnoreCase(
                            CommonString.KEY_FAILURE)) {
                        return CommonString.METHOD_NAME_UNIVERSAL_DOWNLOAD
                                + "," + failureGetterSetter.getErrorMsg();
                    }

                }

                if (!result.toString()
                        .equalsIgnoreCase(CommonString.KEY_FALSE)) {
                    //return CommonString.METHOD_NAME_JCP;

                    xpp.setInput(new StringReader(result.toString()));
                    xpp.next();
                    eventType = xpp.getEventType();


                    questionMappingData = XMLHandlers.QuestionMappingXMLHandler(xpp, eventType);
                    String questionMapping = questionMappingData.getMeta_data();

                    TableBean.setQuestion_mapping_table(questionMapping);

                    data.value = 65;
                    data.name = "QUESTION MAPPING Data";
                    publishProgress(data);
                }


                // Downloading the Additional Display Mapping

                request = new SoapObject(CommonString.NAMESPACE,
                        CommonString.METHOD_NAME_UNIVERSAL_DOWNLOAD);

                request.addProperty("UserName", user_name);
                request.addProperty("Type", "MAPPING_ADDITIONAL_VISIBILITY_NEW");

                envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
                envelope.dotNet = true;
                envelope.setOutputSoapObject(request);

                androidHttpTransport = new HttpTransportSE(CommonString.URL);

                androidHttpTransport.call(
                        CommonString.SOAP_ACTION_UNIVERSAL, envelope);
                result = (Object) envelope.getResponse();

                if (result.toString().equalsIgnoreCase(CommonString.KEY_FALSE)) {
                    //	return CommonString.METHOD_NAME_DownLoad_Promotional_Master;
                }

                if (result.toString()
                        .equalsIgnoreCase(CommonString.KEY_NO_DATA)) {
                    //return CommonString.METHOD_NAME_DownLoad_Promotional_Master;
                }

                // for failure


                if (result.toString()
                        .equalsIgnoreCase(CommonString.KEY_FAILURE)) {

                    xpp.setInput(new StringReader(result.toString()));
                    xpp.next();
                    eventType = xpp.getEventType();
                    failureGetterSetter = XMLHandlers.failureXMLHandler(xpp,
                            eventType);

                    if (failureGetterSetter.getStatus().equalsIgnoreCase(
                            CommonString.KEY_FAILURE)) {
                        return CommonString.METHOD_NAME_UNIVERSAL_DOWNLOAD
                                + "," + failureGetterSetter.getErrorMsg();
                    }

                }

                if (!result.toString()
                        .equalsIgnoreCase(CommonString.KEY_FALSE)) {


                    xpp.setInput(new StringReader(result.toString()));
                    xpp.next();
                    eventType = xpp.getEventType();

                    additionalData = XMLHandlers.AdditionalMappingXMLHandler(xpp, eventType);

                    String additionalMapping = additionalData.getMeta_data();


                    TableBean.setAddtional_mapping_table(additionalMapping);

                    data.value = 70;
                    data.name = "MAPPING ADDITIONAL VISIBILITY";
                    publishProgress(data);

                }


                // Downloading SOS TARGET

                request = new SoapObject(CommonString.NAMESPACE,
                        CommonString.METHOD_NAME_UNIVERSAL_DOWNLOAD);

                request.addProperty("UserName", user_name);
                request.addProperty("Type", "TARGET_SOS");

                envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
                envelope.dotNet = true;
                envelope.setOutputSoapObject(request);

                androidHttpTransport = new HttpTransportSE(CommonString.URL);

                androidHttpTransport.call(
                        CommonString.SOAP_ACTION_UNIVERSAL, envelope);
                result = (Object) envelope.getResponse();

                if (result.toString().equalsIgnoreCase(CommonString.KEY_FALSE)) {
                    //	return CommonString.METHOD_NAME_DownLoad_Promotional_Master;
                }

                if (result.toString()
                        .equalsIgnoreCase(CommonString.KEY_NO_DATA)) {
                    //return CommonString.METHOD_NAME_DownLoad_Promotional_Master;
                }

                // for failure


                if (result.toString()
                        .equalsIgnoreCase(CommonString.KEY_FAILURE)) {

                    xpp.setInput(new StringReader(result.toString()));
                    xpp.next();
                    eventType = xpp.getEventType();
                    failureGetterSetter = XMLHandlers.failureXMLHandler(xpp,
                            eventType);

                    if (failureGetterSetter.getStatus().equalsIgnoreCase(
                            CommonString.KEY_FAILURE)) {
                        return CommonString.METHOD_NAME_UNIVERSAL_DOWNLOAD
                                + "," + failureGetterSetter.getErrorMsg();
                    }

                }

                if (!result.toString()
                        .equalsIgnoreCase(CommonString.KEY_FALSE)) {


                    xpp.setInput(new StringReader(result.toString()));
                    xpp.next();
                    eventType = xpp.getEventType();

                    sostarget = XMLHandlers.SosTargetXMLHandler(xpp, eventType);

                    String sostargetTable = sostarget.getMeta_data();


                    TableBean.setSos_target_table(sostargetTable);

                    data.value = 75;
                    data.name = "TARGET SOS Data";
                    publishProgress(data);
                }


                // Downloading Company_master

                request = new SoapObject(CommonString.NAMESPACE, CommonString.METHOD_NAME_UNIVERSAL_DOWNLOAD);
                request.addProperty("UserName", user_name);
                request.addProperty("Type", "COMPANY_MASTER");
                envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
                envelope.dotNet = true;
                envelope.setOutputSoapObject(request);

                androidHttpTransport = new HttpTransportSE(CommonString.URL);

                androidHttpTransport.call(CommonString.SOAP_ACTION_UNIVERSAL, envelope);
                result = (Object) envelope.getResponse();

                if (result.toString().equalsIgnoreCase(CommonString.KEY_FALSE)) {
                    //	return CommonString.METHOD_NAME_DownLoad_Promotional_Master;
                }
                if (result.toString().equalsIgnoreCase(CommonString.KEY_NO_DATA)) {
                    //return CommonString.METHOD_NAME_DownLoad_Promotional_Master;
                }
                // for failure


                if (result.toString()
                        .equalsIgnoreCase(CommonString.KEY_FAILURE)) {

                    xpp.setInput(new StringReader(result.toString()));
                    xpp.next();
                    eventType = xpp.getEventType();
                    failureGetterSetter = XMLHandlers.failureXMLHandler(xpp,
                            eventType);

                    if (failureGetterSetter.getStatus().equalsIgnoreCase(
                            CommonString.KEY_FAILURE)) {
                        return CommonString.METHOD_NAME_UNIVERSAL_DOWNLOAD
                                + "," + failureGetterSetter.getErrorMsg();
                    }

                }

                if (!result.toString()
                        .equalsIgnoreCase(CommonString.KEY_FALSE)) {


                    xpp.setInput(new StringReader(result.toString()));
                    xpp.next();
                    eventType = xpp.getEventType();

                    comdata = XMLHandlers.CompetitionXMLHandler(xpp, eventType);

                    String comTable = comdata.getMeta_data();


                    TableBean.setCompany_table(comTable);

                    data.value = 78;
                    data.name = "COMPANY MASTER Data";
                    publishProgress(data);
                }

                publishProgress(data);

                // Generic Tables Structure download

                request = new SoapObject(CommonString.NAMESPACE,
                        CommonString.METHOD_NAME_UNIVERSAL_DOWNLOAD);

                request.addProperty("UserName", user_name);
                request.addProperty("Type", "DATAENTRYTABLES");

                envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
                envelope.dotNet = true;
                envelope.setOutputSoapObject(request);

                androidHttpTransport = new HttpTransportSE(CommonString.URL);

                androidHttpTransport.call(
                        CommonString.SOAP_ACTION_UNIVERSAL, envelope);
                result = (Object) envelope.getResponse();

                if (result.toString().equalsIgnoreCase(CommonString.KEY_FALSE)) {
                    //	return CommonString.METHOD_NAME_DownLoad_Promotional_Master;
                }

                if (result.toString()
                        .equalsIgnoreCase(CommonString.KEY_NO_DATA)) {
                    //return CommonString.METHOD_NAME_DownLoad_Promotional_Master;
                }

                // for failure


                if (result.toString().equalsIgnoreCase(CommonString.KEY_FAILURE)) {

                    xpp.setInput(new StringReader(result.toString()));
                    xpp.next();
                    eventType = xpp.getEventType();
                    failureGetterSetter = XMLHandlers.failureXMLHandler(xpp,
                            eventType);

                    if (failureGetterSetter.getStatus().equalsIgnoreCase(
                            CommonString.KEY_FAILURE)) {
                        return CommonString.METHOD_NAME_UNIVERSAL_DOWNLOAD
                                + "," + failureGetterSetter.getErrorMsg();
                    }

                }

                if (!result.toString()
                        .equalsIgnoreCase(CommonString.KEY_FALSE)) {
                    //return CommonString.METHOD_NAME_JCP;

                    xpp.setInput(new StringReader(result.toString()));
                    xpp.next();
                    eventType = xpp.getEventType();

                    alltable = XMLHandlers.XMLHandler(xpp, eventType);
                    String all_entry_tables = alltable.getMeta_data();
                    String stock_table = alltable.getStock_entry_table();
                    String stock_image_table = alltable.getStock_image_table();
                    String tot_entry_table = alltable.getTot_entry_table();
                    String tot_image_table = alltable.getTot_image_table();

                    TableBean.setCoverage_table(all_entry_tables);
                    TableBean.setStock_table(stock_table);
                    TableBean.setStock_image_table(stock_image_table);

                    TableBean.setTot_entry_table(tot_entry_table);
                    TableBean.setTot_image_table(tot_image_table);

                    data.value = 80;
                    data.name = "DATA ENTRY TABLES Data";
                    publishProgress(data);
                }


                //---------------------------

                // PROMOTION_MAPPING_NEW download

                request = new SoapObject(CommonString.NAMESPACE,
                        CommonString.METHOD_NAME_UNIVERSAL_DOWNLOAD);

                request.addProperty("UserName", user_name);
                request.addProperty("Type", "PROMOTION_MAPPING_NEW");

                envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
                envelope.dotNet = true;
                envelope.setOutputSoapObject(request);

                androidHttpTransport = new HttpTransportSE(CommonString.URL);

                androidHttpTransport.call(
                        CommonString.SOAP_ACTION_UNIVERSAL, envelope);
                result = (Object) envelope.getResponse();

                if (result.toString().equalsIgnoreCase(CommonString.KEY_FALSE)) {
                    //	return CommonString.METHOD_NAME_DownLoad_Promotional_Master;
                }

                if (result.toString()
                        .equalsIgnoreCase(CommonString.KEY_NO_DATA)) {
                    //return CommonString.METHOD_NAME_DownLoad_Promotional_Master;
                }

                // for failure


                if (result.toString().equalsIgnoreCase(CommonString.KEY_FAILURE)) {
                    xpp.setInput(new StringReader(result.toString()));
                    xpp.next();
                    eventType = xpp.getEventType();
                    failureGetterSetter = XMLHandlers.failureXMLHandler(xpp, eventType);
                    if (failureGetterSetter.getStatus().equalsIgnoreCase(CommonString.KEY_FAILURE)) {
                        return CommonString.METHOD_NAME_UNIVERSAL_DOWNLOAD + "," + failureGetterSetter.getErrorMsg();
                    }
                }

                if (!result.toString().equalsIgnoreCase(CommonString.KEY_FALSE)) {
                    //return CommonString.METHOD_NAME_JCP;
                    xpp.setInput(new StringReader(result.toString()));
                    xpp.next();
                    eventType = xpp.getEventType();
                    promotionData = XMLHandlers.PromotionalXMLHandler(xpp, eventType);
                    String promotion = promotionData.getMeta_data();
                    TableBean.setPromotional_mapping_table(promotion);
                    data.value = 83;
                    data.name = "PROMOTION MAPPING";
                    publishProgress(data);
                }
                //----------------------------

                //downloading stockafter_Catman mapping

                request = new SoapObject(CommonString.NAMESPACE,
                        CommonString.METHOD_NAME_UNIVERSAL_DOWNLOAD);

                request.addProperty("UserName", user_name);
                request.addProperty("Type", "MAPPING_PRIMARY_WINDOW_DISPLAY_NEW");

                envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
                envelope.dotNet = true;
                envelope.setOutputSoapObject(request);

                androidHttpTransport = new HttpTransportSE(CommonString.URL);

                androidHttpTransport.call(CommonString.SOAP_ACTION_UNIVERSAL, envelope);
                result = (Object) envelope.getResponse();

                if (result.toString().equalsIgnoreCase(CommonString.KEY_FALSE)) {
                    //	return CommonString.METHOD_NAME_DownLoad_Promotional_Master;
                }

                if (result.toString().equalsIgnoreCase(CommonString.KEY_NO_DATA)) {
                    //return CommonString.METHOD_NAME_DownLoad_Promotional_Master;
                }

                // for failure


                if (result.toString().equalsIgnoreCase(CommonString.KEY_FAILURE)) {

                    xpp.setInput(new StringReader(result.toString()));
                    xpp.next();
                    eventType = xpp.getEventType();
                    failureGetterSetter = XMLHandlers.failureXMLHandler(xpp, eventType);

                    if (failureGetterSetter.getStatus().equalsIgnoreCase(CommonString.KEY_FAILURE)) {
                        return CommonString.METHOD_NAME_UNIVERSAL_DOWNLOAD + "," + failureGetterSetter.getErrorMsg();
                    }
                }


                if (!result.toString().equalsIgnoreCase(CommonString.KEY_FALSE)) {
                    //return CommonString.METHOD_NAME_JCP;
                    xpp.setInput(new StringReader(result.toString()));
                    xpp.next();
                    eventType = xpp.getEventType();
                    catmanStockAfter = XMLHandlers.MappingStockAfterCatmanXMLHandler(xpp, eventType);
                    String catmanStockAftr = catmanStockAfter.getTable_Structure();
                    TableBean.setCatman_stockafter_table(catmanStockAftr);

                    data.value = 84;
                    data.name = "MAPPING PRIMARY WINDOW DISPLAY";
                    publishProgress(data);

                }

                //Downloading Wellness Category SOS Capture Plan Inputs

                request = new SoapObject(CommonString.NAMESPACE,
                        CommonString.METHOD_NAME_UNIVERSAL_DOWNLOAD);

                request.addProperty("UserName", user_name);
                request.addProperty("Type", "SHELF_MASTER");

                envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
                envelope.dotNet = true;
                envelope.setOutputSoapObject(request);

                androidHttpTransport = new HttpTransportSE(CommonString.URL);

                androidHttpTransport.call(
                        CommonString.SOAP_ACTION_UNIVERSAL, envelope);
                result = (Object) envelope.getResponse();

                if (result.toString().equalsIgnoreCase(CommonString.KEY_FALSE)) {
                    //	return CommonString.METHOD_NAME_DownLoad_Promotional_Master;
                }

                if (result.toString().equalsIgnoreCase(CommonString.KEY_NO_DATA)) {
                    //return CommonString.METHOD_NAME_DownLoad_Promotional_Master;
                }
                // for failure

                if (result.toString().equalsIgnoreCase(CommonString.KEY_FAILURE)) {
                    xpp.setInput(new StringReader(result.toString()));
                    xpp.next();
                    eventType = xpp.getEventType();
                    failureGetterSetter = XMLHandlers.failureXMLHandler(xpp, eventType);
                    if (failureGetterSetter.getStatus().equalsIgnoreCase(CommonString.KEY_FAILURE)) {
                        return CommonString.METHOD_NAME_UNIVERSAL_DOWNLOAD + "," + failureGetterSetter.getErrorMsg();
                    }
                }


                if (!result.toString().equalsIgnoreCase(CommonString.KEY_FALSE)) {
                    //return CommonString.METHOD_NAME_JCP;
                    xpp.setInput(new StringReader(result.toString()));
                    xpp.next();
                    eventType = xpp.getEventType();
                    shelfmasterData = XMLHandlers.ShelfMasterXMLHandler(xpp, eventType);
                    String shelfmasterTable = shelfmasterData.getTable_Structure();
                    TableBean.setShelf_master(shelfmasterTable);

                    data.value = 85;
                    data.name = "SHELF MASTER";
                    publishProgress(data);

                }


                //Downloading employee meeting status

                request = new SoapObject(CommonString.NAMESPACE, CommonString.METHOD_NAME_UNIVERSAL_DOWNLOAD);
                request.addProperty("UserName", user_name);
                request.addProperty("Type", "EMP_MEETING_STATUS");
                envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
                envelope.dotNet = true;
                envelope.setOutputSoapObject(request);
                androidHttpTransport = new HttpTransportSE(CommonString.URL);
                androidHttpTransport.call(CommonString.SOAP_ACTION_UNIVERSAL, envelope);
                result = (Object) envelope.getResponse();
                if (result.toString().equalsIgnoreCase(CommonString.KEY_FALSE)) {
                    //	return CommonString.METHOD_NAME_DownLoad_Promotional_Master;
                }

                if (result.toString().equalsIgnoreCase(CommonString.KEY_NO_DATA)) {
                    //return CommonString.METHOD_NAME_DownLoad_Promotional_Master;
                }

                // for failure


                if (result.toString()
                        .equalsIgnoreCase(CommonString.KEY_FAILURE)) {

                    xpp.setInput(new StringReader(result.toString()));
                    xpp.next();
                    eventType = xpp.getEventType();
                    failureGetterSetter = XMLHandlers.failureXMLHandler(xpp,
                            eventType);

                    if (failureGetterSetter.getStatus().equalsIgnoreCase(
                            CommonString.KEY_FAILURE)) {
                        return CommonString.METHOD_NAME_UNIVERSAL_DOWNLOAD
                                + "," + failureGetterSetter.getErrorMsg();
                    }

                }

                if (!result.toString().equalsIgnoreCase(CommonString.KEY_FALSE)) {
                    xpp.setInput(new StringReader(result.toString()));
                    xpp.next();
                    eventType = xpp.getEventType();
                    empstatusData = XMLHandlers.EmpMeetingStatusXMLHandler(xpp, eventType);
                    String empMeetingStatustable = empstatusData.getTable_Structure();
                    String meetingStatus = empstatusData.getSTATUS().get(0);
                    editor = preferences.edit();
                    editor.putString("EmpMeetingStatus", meetingStatus);
                    editor.commit();
                    TableBean.setEmp_meeting_status_table(empMeetingStatustable);

                    data.value = 89;
                    data.name = "EMP MEETING STATUS";
                    publishProgress(data);

                }


                //Downloading NON_WORKING_ATTENDANCE
                request = new SoapObject(CommonString.NAMESPACE, CommonString.METHOD_NAME_UNIVERSAL_DOWNLOAD);
                request.addProperty("UserName", user_name);
                request.addProperty("Type", "NON_WORKING_ATTENDANCE");
                envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
                envelope.dotNet = true;
                envelope.setOutputSoapObject(request);
                androidHttpTransport = new HttpTransportSE(CommonString.URL);
                androidHttpTransport.call(CommonString.SOAP_ACTION_UNIVERSAL, envelope);
                Object nonATT = (Object) envelope.getResponse();

                if (nonATT.toString() != null) {
                    xpp.setInput(new StringReader(nonATT.toString()));
                    xpp.next();
                    eventType = xpp.getEventType();
                    nonWorkingAttendenceGetterSetter = XMLHandlers.MarchAttendenceXMLHandler(xpp, eventType);
                    TableBean.setNonwokingAttendenceTable(nonWorkingAttendenceGetterSetter.getMetaDATA());
                    data.value = 90;
                    data.name = "NON WORKING ATTENDANCE";
                    publishProgress(data);
                }


                //Downloading MAPPING_PDR_FACING replace by MAPPING_PDR_FACING_STATEWISE
                request = new SoapObject(CommonString.NAMESPACE, CommonString.METHOD_NAME_UNIVERSAL_DOWNLOAD);
                request.addProperty("UserName", user_name);
                request.addProperty("Type", "MAPPING_PDR_FACING_STATEWISE");
                envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
                envelope.dotNet = true;
                envelope.setOutputSoapObject(request);
                androidHttpTransport = new HttpTransportSE(CommonString.URL);
                androidHttpTransport.call(CommonString.SOAP_ACTION_UNIVERSAL, envelope);
                Object MAPPING_PDR_FACING = (Object) envelope.getResponse();

                if (MAPPING_PDR_FACING.toString() != null) {
                    xpp.setInput(new StringReader(MAPPING_PDR_FACING.toString()));
                    xpp.next();
                    eventType = xpp.getEventType();
                    pdrFacingStockGetterSetter = XMLHandlers.MappingPdrFacingStockXMLHandler(xpp, eventType);
                    String mappingwellnesstable = pdrFacingStockGetterSetter.getPdrFacingStock();
                    TableBean.setMappingPdrFacingTable(mappingwellnesstable);

                    data.value = 91;
                    data.name = "MAPPING PDR FACING STATEWISE";
                    publishProgress(data);
                }


                //Downloading MAPPING_PDR_FACING replace by MAPPING_PDR_FACING_STATEWISE
                request = new SoapObject(CommonString.NAMESPACE, CommonString.METHOD_NAME_UNIVERSAL_DOWNLOAD);
                request.addProperty("UserName", user_name);
                request.addProperty("Type", "MAPPING_COMPETITION_PROMO");
                envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
                envelope.dotNet = true;
                envelope.setOutputSoapObject(request);
                androidHttpTransport = new HttpTransportSE(CommonString.URL);
                androidHttpTransport.call(CommonString.SOAP_ACTION_UNIVERSAL, envelope);
                MAPPING_PDR_FACING = (Object) envelope.getResponse();

                if (MAPPING_PDR_FACING.toString() != null) {
                    xpp.setInput(new StringReader(MAPPING_PDR_FACING.toString()));
                    xpp.next();
                    eventType = xpp.getEventType();
                    mappingCompetitionPromotionGetterSetter = XMLHandlers.MappingCompetitionPromotionXMLHandler(xpp, eventType);
                    TableBean.setMAPPINGCompeti_promotionTable(mappingCompetitionPromotionGetterSetter.getTable());

                    data.value = 93;
                    data.name = "MAPPING_COMPETITION_PROMO";
                    publishProgress(data);
                }


                //Downloading Mapping Wellness Category SOS Capture Plan Inputs

                request = new SoapObject(CommonString.NAMESPACE, CommonString.METHOD_NAME_UNIVERSAL_DOWNLOAD);

                request.addProperty("UserName", user_name);
                request.addProperty("Type", "MAPPING_WELLNESS_SOS");

                envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
                envelope.dotNet = true;
                envelope.setOutputSoapObject(request);

                androidHttpTransport = new HttpTransportSE(CommonString.URL);

                androidHttpTransport.call(
                        CommonString.SOAP_ACTION_UNIVERSAL, envelope);
                result = (Object) envelope.getResponse();

                if (result.toString().equalsIgnoreCase(CommonString.KEY_FALSE)) {
                    //	return CommonString.METHOD_NAME_DownLoad_Promotional_Master;
                }

                if (result.toString()
                        .equalsIgnoreCase(CommonString.KEY_NO_DATA)) {
                    //return CommonString.METHOD_NAME_DownLoad_Promotional_Master;
                }

                // for failure


                if (result.toString()
                        .equalsIgnoreCase(CommonString.KEY_FAILURE)) {

                    xpp.setInput(new StringReader(result.toString()));
                    xpp.next();
                    eventType = xpp.getEventType();
                    failureGetterSetter = XMLHandlers.failureXMLHandler(xpp,
                            eventType);

                    if (failureGetterSetter.getStatus().equalsIgnoreCase(
                            CommonString.KEY_FAILURE)) {
                        return CommonString.METHOD_NAME_UNIVERSAL_DOWNLOAD
                                + "," + failureGetterSetter.getErrorMsg();
                    }

                }


                if (!result.toString()
                        .equalsIgnoreCase(CommonString.KEY_FALSE)) {
                    //return CommonString.METHOD_NAME_JCP;

                    xpp.setInput(new StringReader(result.toString()));
                    xpp.next();
                    eventType = xpp.getEventType();

                    mappingWellnessSosData = XMLHandlers.MappingWellnessXMLHandler(xpp, eventType);
                    String mappingwellnesstable = mappingWellnessSosData.getTable_Structure();

                    TableBean.setMapping_shelf_table(mappingwellnesstable);

                    data.value = 93;
                    data.name = "MAPPING WELLNESS SOS";
                    publishProgress(data);
                }


                //Downloading STOREWISE_PSS replace by STOREWISE_PSS_SCORE

                request = new SoapObject(CommonString.NAMESPACE, CommonString.METHOD_NAME_UNIVERSAL_DOWNLOAD);
                request.addProperty("UserName", user_name);
                request.addProperty("Type", "STOREWISE_PSS_SCORE");
                envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
                envelope.dotNet = true;
                envelope.setOutputSoapObject(request);
                androidHttpTransport = new HttpTransportSE(CommonString.URL);
                androidHttpTransport.call(CommonString.SOAP_ACTION_UNIVERSAL, envelope);
                result = (Object) envelope.getResponse();
                if (result.toString().equalsIgnoreCase(CommonString.KEY_FALSE)) {
                }
                if (result.toString().equalsIgnoreCase(CommonString.KEY_NO_DATA)) {
                }
                // for failure
                if (result.toString().equalsIgnoreCase(CommonString.KEY_FAILURE)) {
                    xpp.setInput(new StringReader(result.toString()));
                    xpp.next();
                    eventType = xpp.getEventType();
                    failureGetterSetter = XMLHandlers.failureXMLHandler(xpp, eventType);
                    if (failureGetterSetter.getStatus().equalsIgnoreCase(CommonString.KEY_FAILURE)) {
                        return CommonString.METHOD_NAME_UNIVERSAL_DOWNLOAD + "," + failureGetterSetter.getErrorMsg();
                    }
                }


                if (!result.toString().equalsIgnoreCase(CommonString.KEY_FALSE)) {
                    xpp.setInput(new StringReader(result.toString()));
                    xpp.next();
                    eventType = xpp.getEventType();
                    storeWisePssData = XMLHandlers.StoreWisePssXMLHandler(xpp, eventType);
                    String StoreWisePsstable = storeWisePssData.getTable_Structure();
                    TableBean.setStorewise_pss_table(StoreWisePsstable);
                    data.value = 94;
                    data.name = "STOREWISE PSS Data";
                    publishProgress(data);
                }
                //Downloading STOREWISE_PSS replace by STOREWISE_PSS_SCORE

                request = new SoapObject(CommonString.NAMESPACE, CommonString.METHOD_NAME_UNIVERSAL_DOWNLOAD);
                request.addProperty("UserName", user_name);
                request.addProperty("Type", "TARGET_BRAND_GROUP_WISE");
                envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
                envelope.dotNet = true;
                envelope.setOutputSoapObject(request);
                androidHttpTransport = new HttpTransportSE(CommonString.URL);
                androidHttpTransport.call(CommonString.SOAP_ACTION_UNIVERSAL, envelope);
                result = (Object) envelope.getResponse();
                if (result.toString().equalsIgnoreCase(CommonString.KEY_FALSE)) {
                }
                if (result.toString().equalsIgnoreCase(CommonString.KEY_NO_DATA)) {
                }
                // for failure
                if (result.toString().equalsIgnoreCase(CommonString.KEY_FAILURE)) {
                    xpp.setInput(new StringReader(result.toString()));
                    xpp.next();
                    eventType = xpp.getEventType();
                    failureGetterSetter = XMLHandlers.failureXMLHandler(xpp, eventType);
                    if (failureGetterSetter.getStatus().equalsIgnoreCase(CommonString.KEY_FAILURE)) {
                        return CommonString.METHOD_NAME_UNIVERSAL_DOWNLOAD + "," + failureGetterSetter.getErrorMsg();
                    }
                }

                if (!result.toString().equalsIgnoreCase(CommonString.KEY_FALSE)) {
                    xpp.setInput(new StringReader(result.toString()));
                    xpp.next();
                    eventType = xpp.getEventType();
                    targetToothpestforOHCObject = XMLHandlers.TargetforOHCCategoryHandler(xpp, eventType);
                    TableBean.setTargetforohctoothpestTable(targetToothpestforOHCObject.getTableMetaData());
                    data.value = 96;
                    data.name = "TARGET BRAND GROUP WISE Data";
                    publishProgress(data);
                }


                data.value = 98;
                data.name = "Downloading Planogram Images";
                publishProgress(data);
                for (int i = 0; i < displayData.getDisplay_id().size(); i++) {
                    String extStorageDirectory = Environment.getExternalStorageDirectory().toString();
                    File folder = new File(extStorageDirectory, "GSKMT_Planogram_Images");
                    folder.mkdir();
                    if (!displayData.getImage_url().get(0).equals("") && !displayData.getImage_url().get(0).equals("NA")) {
                        File pdfFile = new File(folder, displayData.getImage_url().get(i));
                        try {
                            pdfFile.createNewFile();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        if (new File(CommonString.FILE_PATH_PLANOGRAM + displayData.getImage_url().get(i)).exists()) {
                        } else {
                            downloadFile(displayData.getPath().get(i), displayData.getImage_url().get(i), folder);
                        }
                    }
                }

                data.value = 99;
                data.name = "Data Inserting";
                publishProgress(data);
                db.open();
                db.InsertSkuMasterData(skudata);
                db.InsertJCP(jcpData);
                db.InsertBrandMasterData(brandData);
                db.InsertSkuMappingData(stockData);
                db.InsertDisplay(displayData);
                db.InsertTDS(TDsData);
                db.InsertNonWorkingReasonData(reasonData);
                db.InsertNonWorkingSubReasonData(subReasonData);
                db.InsertQuestionData(questionData);
                db.InsertQuestionMappingData(questionMappingData);
                db.InsertGeoTagData(geotagData);
                db.InsertCatmanStockAfter(catmanStockAfter);
                db.InsertPromotionalMappingData(promotionData);
                db.InsertAdditionalMappingData(additionalData);
                db.InsertCompetitionData(comdata);
                db.InsertSOSTargetData(sostarget);
                db.InsertShelfMasterData(shelfmasterData);
                db.InsertMappingShelfData(mappingWellnessSosData);
                ///pdr STOCK FACING
                db.InsertMappingPDRFACING(pdrFacingStockGetterSetter);
                db.insertmappingCompetitionPromotion(mappingCompetitionPromotionGetterSetter);

                db.InsertStoreWisePss(storeWisePssData);
                //insert for ohc category target
                db.InsertTargetOHC(targetToothpestforOHCObject);

                db.InsertEmpMeetingStatus(empstatusData);
                db.InsertNonWoATTENDENCEData(nonWorkingAttendenceGetterSetter);

                data.value = 100;
                data.name = "Finishing";
                publishProgress(data);

                return CommonString.KEY_SUCCESS;

            } catch (MalformedURLException e) {
                Crashlytics.logException(e);
                final AlertMessage message = new AlertMessage(CompleteDownloadActivity.this,
                        AlertMessage.MESSAGE_EXCEPTION, "download", e);
                runOnUiThread(new Runnable() {

                    @Override
                    public void run() {
                        // TODO Auto-generated method stub
                        message.showMessage();
                    }
                });

            } catch (IOException e) {
                final AlertMessage message = new AlertMessage(CompleteDownloadActivity.this,
                        AlertMessage.MESSAGE_SOCKETEXCEPTION, "socket", e);

                runOnUiThread(new Runnable() {

                    @Override
                    public void run() {
                        message.showMessage();

                    }
                });
            } catch (Exception e) {
                Crashlytics.logException(e);
                final AlertMessage message = new AlertMessage(CompleteDownloadActivity.this,
                        AlertMessage.MESSAGE_EXCEPTION, "download", e);

                e.getMessage();
                e.printStackTrace();
                e.getCause();
                runOnUiThread(new Runnable() {

                    @Override
                    public void run() {

                        message.showMessage();
                    }
                });
            }

            return "";
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
            super.onPostExecute(result);
            dialog.dismiss();
            if (result.equals(CommonString.KEY_SUCCESS)) {
                AlertMessage message = new AlertMessage(CompleteDownloadActivity.this, AlertMessage.MESSAGE_DOWNLOAD, "success", null);
                message.showMessage();
            } else if (result.equals(CommonString.METHOD_NAME_JCP)) {
                AlertMessage message = new AlertMessage(CompleteDownloadActivity.this, AlertMessage.MESSAGE_JCP_FALSE, "success", null);
                message.showMessage();
            } else if (!result.equals("")) {
                AlertMessage message = new AlertMessage(CompleteDownloadActivity.this, result, "success", null);
                message.showMessage();
            }

        }

    }

    public void downloadFile(String fileUrl, String directory, File folder_path) {
        try {
            final int MEGABYTE = 1024 * 1024;
            URL url = new URL(fileUrl + directory);
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.getResponseCode();
            urlConnection.connect();
            if (urlConnection.getResponseCode() == 200) {
                int length = urlConnection.getContentLength();
                String size = new DecimalFormat("##.##").format((double) ((double) length / 1024)) + " KB";
                if (!new File(folder_path.getPath() + directory).exists() && !size.equalsIgnoreCase("0 KB")) {
                    File outputFile = new File(folder_path, directory);
                    FileOutputStream fos = new FileOutputStream(outputFile);
                    InputStream is1 = (InputStream) urlConnection.getInputStream();
                    int bytes = 0;
                    byte[] buffer = new byte[1024];
                    int len1 = 0;

                    while ((len1 = is1.read(buffer)) != -1) {
                        bytes = (bytes + len1);

                        fos.write(buffer, 0, len1);

                    }

                    fos.close();
                    is1.close();
                }
            }
        } catch (FileNotFoundException e) {
            Crashlytics.logException(e);
            e.printStackTrace();
        } catch (ProtocolException e) {
            e.printStackTrace();
        } catch (MalformedURLException e) {
            Crashlytics.logException(e);
            e.printStackTrace();
        } catch (IOException e) {
            Crashlytics.logException(e);
            e.printStackTrace();
        }
    }

}
