package com.cpm.Constants;

import android.os.Environment;

public class CommonString {


    // preferenec keys
    public static final String KEY_USERNAME = "username";
    public static final String KEY_PASSWORD = "password";
    public static final String KEY_REMEMBER = "remember";
    public static final int CAPTURE_MEDIA = 131;
    public static final String KEY_PATH = "path";
    public static final String KEY_VERSION = "APP_VERSION";
    public static final String KEY_DATE = "date";
    public static final String KEY_ATTENDENCE_STATUS = "ATTENDENCE_STATUS";
    public static final String MID = "MID";
    public static final String KEY_P = "P";
    public static final String KEY_D = "D";
    public static final String KEY_U = "U";
    public static final String KEY_C = "Y";
    public static final String KEY_N = "N";

    public static final String STORE_STATUS_LEAVE = "L";
    public static final String KEY_VALID = "Valid";
    public static final String KEY_STORE_IN_TIME = "Store_in_time";


    public static final String FILE_PATH = Environment.getExternalStorageDirectory() + "/MT_GSK_Images/";
    public static final String FILE_PATH_PLANOGRAM = Environment.getExternalStorageDirectory() + "/GSKMT_Planogram_Images/";
    public static final String ONBACK_ALERT_MESSAGE = "Unsaved data will be lost - Do you want to continue?";
    public static final String KEY_SERVER_NOTICE_BOARD_URL = "NOTICE_BOARD_URL";
    public static final String KEY_SERVER_QUIZ_URL = "QUIZ_URL";

    public static final String KEY_IMAGE_PATH1 = "IMAGE_PATH1";
    public static final String KEY_IMAGE_PATH2 = "IMAGE_PATH2";
    public static final String KEY_IMAGE_PATH3 = "IMAGE_PATH3";
    public static final String TABLE_INSERT_GEO_TAG = "INSERT_GEO_TAG_DATA";
    public static final String TABLE_GEO_TAG_MAPPING = "GEOTAG_STORE";
    public static final String TABLE_GEOTAG_CITY = "GEO_TAG_CITY";
    public static final String TABLE_QUESTION_ANSWER = "QUESTION_ANSWER_TABLE";

    public static final String TABLE_QUESTION_ANSWER_STOCKAFTER = "QUESTION_ANSWER_TABLE_STOCKAFTER";
    public static final String TABLE_STORE_GEOTAGGING = "STORE_GEOTAGGING";
    public static final String TABLE_PROMOTION_DATA = "PROMOTION_DATA";
    public static final String TABLE_TEMP_QUESTION_ANSWER = "TEMP_QUESTION_ANSWER_TABLE";
    public static final String TABLE_TOT_AFTER = "TOT_AFTER";
    public static final String TABLE_TOT_BEFORE = "TOT_BEFORE";

    // webservice constants

    public static final String KEY_SUCCESS = "Success";
    public static final String KEY_FAILURE = "Failure";
    public static final String KEY_FALSE = "False";
    public static final String KEY_CHANGED = "Changed";
    public static final String KEY_NO_DATA = "NODATA";
    public static final String KEY_STATE_ID = "STATE_ID";
    public static final String KEY_CLASS_ID = "CLASS_ID";
    public static final String KEY_COMPETITION_PROMOTION = "COMPETITION_PROMOTION";
    public static final String KEY_SKU_ID = "SKU_ID";
    public static final String KEY_SKUNAME = "SKUNAME";
    public static final String KEY_IMAGE = "IMAGE";
    public static final String KEY_IMAGE1 = "BEFORE_IMAGE1";
    public static final String KEY_IMAGE2 = "BEFORE_IMAGE2";
    public static final String KEY_IMAGE3 = "BEFORE_IMAGE3";
    public static final String KEY_IMAGE4 = "BEFORE_IMAGE4";
    public static final String KEY_IMAGE5 = "AFTER_IMAGE1";
    public static final String KEY_IMAGE6 = "AFTER_IMAGE2";
    public static final String KEY_IMAGE7 = "AFTER_IMAGE3";
    public static final String KEY_IMAGE8 = "AFTER_IMAGE4";
    public static final String KEY_PRICEOFF_TOGGLE = "PRICEOFF_SPIN";
    public static final String KEY_PRICEOFF = "PRICEOFF";
    public static final String KEY_SKU_MRP = "SKU_MRP";

    public static final String KEY_COMP_SEGMENT_ID = "SEGMENT_ID";
    public static final String KEY_COMP_SEGMENT = "SEGMENT";


    public static final String KEY_REMARK = "REMARK";

    public static final String NAMESPACE = "http://tempuri.org/";

    public static final String URL = "http://gskmtm.parinaam.in/GSKmtService.asmx";
    //public static final String NOTICE_BOARD_URL = "http://gskmtm.parinaam.in/notice/notice.html";

    public static final String METHOD_LOGIN = "UserLoginDetail";
    public static final String SOAP_ACTION_LOGIN = "http://tempuri.org/" + METHOD_LOGIN;

    public static final String METHOD_UPLOAD_STORE_STATUS = "InsertUserCurrentLocation";
    public static final String SOAP_ACTION_UPLOAD_STORE_STATUS = "http://tempuri.org/"
            + METHOD_UPLOAD_STORE_STATUS;
    public static final String METHOD_NAME_JCP = "DownLoadStoreJcp_Special";

    public static final String METHOD_NAME_UNIVERSAL_DOWNLOAD = "Download_Universal";

    public static final String METHOD_NAME_PJP_STORE = "DownloadStoreEmployeeWise";

    public static final String METHOD_Checkout_StatusNew_deviation = "Upload_Store_ChecOut_Status_Deviation";

    public static final String SOAP_ACTION_UNIVERSAL = "http://tempuri.org/"
            + METHOD_NAME_UNIVERSAL_DOWNLOAD;

    public static final String SOAP_ACTION_PJP = "http://tempuri.org/"
            + METHOD_NAME_PJP_STORE;

    public static final String METHOD_NAME_STORE_LAYOUT = "DownLoad_Store_Layout";
    public static final String SOAP_ACTION_STORE_LAYOUT = "http://tempuri.org/"
            + METHOD_NAME_STORE_LAYOUT;

    public static final String METHOD_NAME_STORE_SIZE = "DownLoad_Store_Size";
    public static final String SOAP_ACTION_STORE_SIZE = "http://tempuri.org/"
            + METHOD_NAME_STORE_SIZE;

    public static final String METHOD_NAME_UPLOAD_GEOTAG_IMAGE = "Upload_StoreGeoTag_IMAGES";
    public static final String SOAP_ACTION_UPLOAD_GEOTAG_IMAGE = "http://tempuri.org/"
            + METHOD_NAME_UPLOAD_GEOTAG_IMAGE;

    public static final String METHOD_NAME_PLANOGRAM_IMAGES = "DownLoad_PlanoGramMapping";
    public static final String SOAP_ACTION_PLANOGRAM_IMAGES = "http://tempuri.org/"
            + METHOD_NAME_PLANOGRAM_IMAGES;

    public static final String METHOD_NAME_delete_coverage = "DeleteChekoutAndCoverage";
    public static final String SOAP_ACTION_delete_coverage = "http://tempuri.org/"
            + METHOD_NAME_delete_coverage;

    public static final String METHOD_Checkout_StatusNew = "Upload_Store_ChecOut_Status";
    public static final String METHOD_STORE_VISIT = "STORE_VISITNEW";
    public static final String SOAP_ACTION_Checkout_StatusNew = "http://tempuri.org/"
            + METHOD_Checkout_StatusNew;

    public static final String SOAP_ACTION_STORE_VISIT = "http://tempuri.org/"
            + METHOD_STORE_VISIT;

    // String value for promotional master

    public static final String METHOD_NAME_DownLoad_Promotional_Master = "DownLoad_Promotional_Master";
    public static final String SOAP_ACTION_Promotional_Master = "http://tempuri.org/"
            + METHOD_NAME_DownLoad_Promotional_Master;


    // String value for Marchendiser code and name
    public static final String METHOD_NAME_ALL_EMP = "GetEmployee";
    public static final String SOAP_ACTION_ALL_EMP = "http://tempuri.org/" + METHOD_NAME_ALL_EMP;

    public static final String METHOD_NAME_WINDOW_SIZE_STATUS = "Download_WindowSize_Status";
    public static final String SOAP_ACTION_WINDOW_SIZE = "http://tempuri.org/"
            + METHOD_NAME_WINDOW_SIZE_STATUS;

    // String value for SKU master

    public static final String METHOD_NAME_DOWNLOAD_SKU_MASTER = "DownLoad_SKU_Master";
    public static final String SOAP_ACTION_DOWNLAOD_SKU_MASTER = "http://tempuri.org/"
            + METHOD_NAME_DOWNLOAD_SKU_MASTER;

    // string value for Master

    public static final String METHOD_NAME_DOWNLOAD_NON_WORKING_MASTER = "DownLoad_NonWorkingReason_Master";

    public static final String METHOD_NAME_DOWNLOAD_NON_WORKING_MASTER_subReason = "DownLoad_NonWorkingSubReason_ByReason";

    public static final String SOAP_ACTION_DOWNLAOD_NON_WORKING_MASTER = "http://tempuri.org/"
            + METHOD_NAME_DOWNLOAD_NON_WORKING_MASTER;

    public static final String SOAP_ACTION_DOWNLAOD_NON_WORKING_MASTER_SUBREASON = "http://tempuri.org/"
            + METHOD_NAME_DOWNLOAD_NON_WORKING_MASTER_subReason;

    public static final String METHOD_NAME_DOWNLOAD_sku_mapping = "DownLoad_SKU_By_Mapping";
    public static final String SOAP_ACTION_DOWNLAOD_sku_mapping = "http://tempuri.org/"
            + METHOD_NAME_DOWNLOAD_sku_mapping;

    // string value for DowloadComplaince

    public static final String METHOD_NAME_DOWNLOAD_COMPLIANCE = "DowloadComplaince";
    public static final String SOAP_ACTION_DOWNLAOD_COMPLIANCE = "http://tempuri.org/"
            + METHOD_NAME_DOWNLOAD_COMPLIANCE;

    // STRING VALUE FOR DowloadPromotionWithComplainceByMapping

    public static final String METHOD_NAME_DOWNLOAD_COMPLIANCE_MAPPING = "DowloadPromotionWithComplainceByMapping";
    public static final String SOAP_ACTION_DOWNLAOD_COMPLIANCE_MAPPING = "http://tempuri.org/"
            + METHOD_NAME_DOWNLOAD_COMPLIANCE_MAPPING;

    public static final String METHOD_NAME_DOWNLOAD_COMPLIANCE_MAPPING_SPECIAL = "DownLoad_PROMOTION_COMPLIANCE_MAPPING_SPECIAL";
    public static final String SOAP_ACTION_DOWNLAOD_COMPLIANCE_MAPPING_SPECIAL = "http://tempuri.org/"
            + METHOD_NAME_DOWNLOAD_COMPLIANCE_MAPPING_SPECIAL;

    public static final String METHOD_VERTICAL_MASTER = "DOWLOAD_VERTICALMASTER";
    public static final String SOAP_ACTION_VERTICAL_MASTER = "http://tempuri.org/"
            + METHOD_VERTICAL_MASTER;

    public static final String METHOD_BRAND_MASTER = "DOWLOAD_BRANDMASTER";
    public static final String SOAP_ACTION_BRAND_Master = "http://tempuri.org/"
            + METHOD_BRAND_MASTER;

    public static final String METHOD_VERTICAL_BRAND_MAPPING = "DOWLOAD_VERTICALBRANDMAPPING";
    public static final String SOAP_ACTION_VERTICAL_BRAND_Mapping = "http://tempuri.org/"
            + METHOD_VERTICAL_BRAND_MAPPING;

    public static final String METHOD_VERTICAL_SKU_MAPPING = "SKUBRANDMAPPINGDownload";
    public static final String SOAP_ACTION_VERTICAL_SKU_Mapping = "http://tempuri.org/"
            + METHOD_VERTICAL_SKU_MAPPING;

    public static final String METHOD_CATEGORY_MASTER = "DOWLOAD_CATEGORYMASTER";
    public static final String SOAP_ACTION_CATEGORY_MASTER = "http://tempuri.org/"
            + METHOD_CATEGORY_MASTER;

    public static final String METHOD_CATEGORY_SKU_MAPPING = "CATEGORYSKUMAPPINGDownload";
    public static final String SOAP_ACTION_CATEGORY_SKU_MAPPING = "http://tempuri.org/"
            + METHOD_CATEGORY_SKU_MAPPING;

    public static final String METHOD_CATEGORY_VERTICAL_MAPPING = "CATEGORYVERTICALMAPPINGDownload";
    public static final String SOAP_ACTION_CATEGORY_VERTICAL_MAPPING = "http://tempuri.org/"
            + METHOD_CATEGORY_VERTICAL_MAPPING;

    public static final String METHOD_CATEGORY_POSM_MAPPING = "POSMBRANDMAPPINGDownload";
    public static final String SOAP_ACTION_POSM_MAPPING = "http://tempuri.org/"
            + METHOD_CATEGORY_POSM_MAPPING;

    public static final String METHOD_SKU_MASTER_DOWNLOAD = "SKU_MASTERDownload";
    public static final String SOAP_ACTION_SKU_MASTER = "http://tempuri.org/"
            + METHOD_SKU_MASTER_DOWNLOAD;

    public static final String METHOD_COMPANY_MASTER_DOWNLOAD = "COMPANY_MASTERDownload";
    public static final String SOAP_ACTION_COMPANY_MASTER = "http://tempuri.org/"
            + METHOD_COMPANY_MASTER_DOWNLOAD;

    // Shahab
    public static final String METHOD_NONSKU_REASON = "DOWLOAD_NON_STOCK_REASON_MASTER";
    public static final String SOAP_ACTION_NONSKU_REASON = "http://tempuri.org/"
            + METHOD_NONSKU_REASON;

    public static final String METHOD_SKU_FOCUS_DOWNLOAD = "SKUAVALIBILITY_FOCUS";
    public static final String SOAP_ACTION_SKU_FOCUS = "http://tempuri.org/"
            + METHOD_SKU_FOCUS_DOWNLOAD;

    public static final String METHOD_MAPPING_COMPETITOR = "DOWLOAD_MAPPINGCOMPEPITORBRAND";
    public static final String SOAP_ACTION_MAPPING_COMPETITOR = "http://tempuri.org/"
            + METHOD_MAPPING_COMPETITOR;

    public static final String SOAP_ACTION = "http://tempuri.org/";

    // Upload Coverage
    public static final String METHOD_UPLOAD_DR_STORE_COVERAGE = "UPLOAD_COVERAGE";
    public static final String METHOD_UPLOAD_DR_STORE_COVERAGE_LOC = "UPLOAD_STORE_COVERAGE_WSC_NEW";
    public static final String METHOD_UPLOAD_COVERAGE_NONWORKING_DATA = "UPLOAD_CoverageEntryNotAllow";
    public static final String SOAP_ACTION_UPLOAD_DR_STORE_COVERAGE = "http://tempuri.org/"
            + METHOD_UPLOAD_DR_STORE_COVERAGE_LOC;

    public static final String SOAP_ACTION_METHOD_UPLOAD_COVERAGE_NONWORKING_DATA = "http://tempuri.org/"
            + METHOD_UPLOAD_COVERAGE_NONWORKING_DATA;

    public static final String MEHTOD_DELETE_COVERAGE = "DeleteCoverage";

    public static final String MEHTOD_MERCHANDISOR_ATTENDENCE = "Merchandiser_Attendance";


    public static final String METHOD_UPLOAD_ASSET = "Upload_Stock_Availiablity_V1";

    public static final String METHOD_PRIMARY_WINDOW_IMAGES = "UPLOAD_Store_Image";

    public static final String METHOD_UPLOAD_PRIMARY_WINDOW_IMAGES = "GetImageNew";

    public static final String SOAP_ACTION_UPLOAD_PRIMARY_WINDOW_IMAGES = "http://tempuri.org/"
            + METHOD_UPLOAD_PRIMARY_WINDOW_IMAGES;

    public static final String SOAP_ACTION_PRIMARY_WINDOW_IMAGES = "http://tempuri.org/"
            + METHOD_PRIMARY_WINDOW_IMAGES;

    public static final String METHOD_UPLOAD_STOCK_XML_DATA = "DrUploadXml";
    public static final String SOAP_ACTION_UPLOAD_ASSET = "http://tempuri.org/"
            + METHOD_UPLOAD_ASSET;

    public static final String SOAP_ACTION_UPLOAD_ASSET_XMLDATA = "http://tempuri.org/"
            + METHOD_UPLOAD_STOCK_XML_DATA;

    public static final String METHOD_UPLOAD_SEC_SKU = "Upload_Stock_Availiablity_SECONDARY";
    public static final String SOAP_ACTION_UPLOAD_SEC_SKU = "http://tempuri.org/"
            + METHOD_UPLOAD_SEC_SKU;

    public static final String METHOD_UPLOAD_PCKGE_SKU = "Upload_DR_CORE_SKU_PACKAGING";
    public static final String SOAP_ACTION_UPLOAD_PCKGE_SKU = "http://tempuri.org/"
            + METHOD_UPLOAD_PCKGE_SKU;

    public static final String METHOD_UPLOAD_POSM = "Upload_Posm_Deployed";
    public static final String SOAP_ACTION_UPLOAD_POSM = "http://tempuri.org/"
            + METHOD_UPLOAD_POSM;

    public static final String METHOD_Upload_Posm_Deployed_Deviation = "Upload_Posm_Deployed_Deviation";
    public static final String SOAP_ACTION_Upload_Posm_Deployed_Deviation = "http://tempuri.org/"
            + METHOD_Upload_Posm_Deployed_Deviation;

    public static final String METHOD_UPLOAD_COMPLIANCE = "Upload_Promotion_WindowExists";
    public static final String SOAP_ACTION_COMPLIANCE = "http://tempuri.org/"
            + METHOD_UPLOAD_COMPLIANCE;

    public static final String METHOD_UPLOAD_COMPLIANCE_SPECIAL = "Upload_Promotion_Special";
    public static final String SOAP_ACTION_COMPLIANCE_SPECIAL = "http://tempuri.org/"
            + METHOD_UPLOAD_COMPLIANCE_SPECIAL;

    public static final String METHOD_NON_WORKING_MASTER = "DOWLOAD_NONWORKINGREGIONMASTER";
    public static final String SOAP_ACTION_NONWORKING = "http://tempuri.org/"
            + METHOD_NON_WORKING_MASTER;

    public static final String METHOD_SET_COVERAGE_STATUS = "Upload_Status";
    public static final String SOAP_ACTION_SET_COVERAGE_STATUS = "http://tempuri.org/"
            + METHOD_SET_COVERAGE_STATUS;

    public static final String METHOD_SET_UPLOAD_GEODATA = "Upload_Store_Geo_Tag";
    public static final String SOAP_ACTION_UPLOAD_GEODATA = "http://tempuri.org/"
            + METHOD_SET_UPLOAD_GEODATA;

    // database

    public static final String TABLE_COVERAGE_DATA = "DR_STORE_COVERAGE";


    // FOR JCP DOWNLOAD
    public static final String KEY_ID = "KEY_ID";
    public static final String KEY_CHECK_IN = "I";

    public static final String KEY_PACKED_KEY = "PACKED_KEY";

    public static final String UNIQUE_KEY_ID = "UNIQUE_KEY_ID";
    public static final String KEY_STORE_ID = "STORE_ID";
    public static final String KEY_STORE_NAME = "STORE_NAME";
    public static final String KEY_STORE_IMAGE = "STORE_IMAGE";
    public static final String KEY_STORE_IMAGE_ALLOW = "IMAGE_ALLOW";
    public static final String KEY_USER_ID = "USER_ID";
    public static final String KEY_IN_TIME = "IN_TIME";
    public static final String KEY_OUT_TIME = "OUT_TIME";
    public static final String KEY_VISIT_DATE = "VISIT_DATE";
    public static final String KEY_PROCESS_ID = "PROCESS_ID";
    public static final String KEY_STOCK = "STOCK";
    public static final String KEY_LATITUDE = "LATITUDE";
    public static final String KEY_LONGITUDE = "LONGITUDE";
    public static final String KEY_REASON_ID = "REASON_ID";
    public static final String KEY_ENTRY_ALLOW = "ENTRY_ALLOW";
    public static final String KEY_PROMOTION = "PROMOTION";
    public static final String KEY_REASON = "REASON";
    public static final String KEY_STATUS = "STATUS";
    public static final String KEY_CHECKOUT_IMAGE = "CHECKOUT_IMAGE";

    public static final String KEY_CHECKOUT_STATUS = "CHECKOUT_STATUS";

    public static final String KEY_STORE_CD = "STORE_ID";
    public static final String KEY_STORE = "STORE";
    public static final String KEY_ADDRES = "CITY";
    public static final String KEY_EMP_CD = "EMP_ID";
    public static final String KEY_CURRENT_DATETIME = "VISIT_DATE";

    public static final String KEY_STOREVISITED = "STORE_VISITED";
    public static final String KEY_STOREVISITED_STATUS = "STORE_VISITED_STATUS";


    public static final String region_id = "REGION_ID";
    public static final String storetype_id = "STORETYPE_ID";
    public static final String KEY_CATEGORY_ID = "CATEGORY_ID";
    public static final String KEY_CATEGORY_NAME = "CATEGORY_NAME";

    public static final String KEY_COMP_TRACT_EXIST = "TRACT_EXIST";


    // geo tag
    public static final String METHOD_NAME_DOWNLOAD_GEO = "DownLoadStoreByUser";
    public static final String SOAP_ACTION_DOWNLAOD_GEO = "http://tempuri.org/"
            + METHOD_NAME_DOWNLOAD_GEO;

    public static final String TABLE_STORE_DETAIL = "STORE_MASTER";

    public static final String TABLE_INSERT_ADDTIONAL_DETAILS = "ADDTIONAL_INFO";

    public static final String TABLE_INSERT_STOCK_TOT = "STOCK_TOT";
    public static final String TABLE_INSERT_SALES_STOCK = "SALES_STOCK";

    public static final String TABLE_INSERT_COMPETITION_INFO = "COMPETITION_INFO";


    public static final String TABLE_INSERT_AFTER_ADDTIONAL_DETAILS = "AFTER_ADDTIONAL_INFO";
    public static final String KEY_BRAND_ID = "BRAND_ID";
    public static final String KEY_COMPANY_ID = "COMPANY_ID";
    public static final String KEY_COMPANY = "COMPANY";
    public static final String KEY_TYPE = "TYPE";
    public static final String KEY_IMAGE_URL = "IMAGE_URL";
    public static final String KEY_BRAND = "BRAND";
    public static final String KEY_DISPLAY_ID = "DISPLAY_ID";
    public static final String KEY_DISPLAY = "DISPLAY";
    public static final String KEY_QUANTITY = "QUANTITY";
    public static final String KEY_ADDITIONAL_YESYorNO = "ADDITIONALYESORNO";


    public static final String KEY_AFTER_QUANTITY = "AFTER_QUANTITY";
    public static final String KEY_AFTER_STOCK_COUNT = "AFTER_STOCK_COUNT";

    public static final String KEY_BEFORE_QUANTITY = "BEFORE_QUANTITY";
    public static final String KEY_BEFORE_STOCK_COUNT = "BEFORE_STOCK_COUNT";
    public static final String KEY_TARGER_QUANTITY = "TARGER_QUANTITY";


    public static final String CREATE_TABLE_ADDITIONAL_DETAILS = "CREATE TABLE "
            + TABLE_INSERT_ADDTIONAL_DETAILS + " (" + KEY_ID
            + " INTEGER PRIMARY KEY AUTOINCREMENT," + KEY_STORE_ID + " VARCHAR,"
            + KEY_BRAND_ID + " VARCHAR," + KEY_BRAND + " VARCHAR,"
            + KEY_DISPLAY_ID + " VARCHAR,"
            + KEY_DISPLAY + " VARCHAR," + KEY_QUANTITY + " VARCHAR," + KEY_IMAGE + " VARCHAR,"
            + KEY_CATEGORY_ID + " VARCHAR,"
            + KEY_ADDITIONAL_YESYorNO + " VARCHAR,"
            + KEY_PROCESS_ID + " VARCHAR)";


    public static final String CREATE_TABLE_STOCK_TOT = "CREATE TABLE "
            + TABLE_INSERT_STOCK_TOT + " (" + KEY_ID
            + " INTEGER PRIMARY KEY AUTOINCREMENT," + KEY_STORE_ID + " VARCHAR,"
            + KEY_BRAND_ID + " VARCHAR," + KEY_BRAND + " VARCHAR,"
            + KEY_DISPLAY_ID + " VARCHAR,"
            + KEY_SKU_ID + " VARCHAR," + KEY_QUANTITY + " VARCHAR," + UNIQUE_KEY_ID + " VARCHAR,"
            + KEY_CATEGORY_ID + " VARCHAR,"
            + KEY_SKUNAME + " VARCHAR,"
            + KEY_PROCESS_ID + " VARCHAR)";


    public static final String CREATE_TABLE_SALES_STOCK = "CREATE TABLE "
            + TABLE_INSERT_SALES_STOCK + " (" + KEY_ID
            + " INTEGER PRIMARY KEY AUTOINCREMENT," + KEY_STORE_ID + " VARCHAR,"

            + KEY_BRAND_ID + " VARCHAR," + KEY_BRAND + " VARCHAR,"
            + KEY_SKU_ID + " VARCHAR," + KEY_QUANTITY + " INTEGER,"
            + KEY_CATEGORY_ID + " VARCHAR,"
            + KEY_SKUNAME + " VARCHAR,"
            + KEY_PROCESS_ID + " VARCHAR,"
            + KEY_USER_ID + " VARCHAR)";


    public static final String CREATE_TABLE_COMPETITION_INFO = "CREATE TABLE "
            + TABLE_INSERT_COMPETITION_INFO + " (" + KEY_ID
            + " INTEGER PRIMARY KEY AUTOINCREMENT," + KEY_STORE_ID + " INTEGER,"
            + KEY_BRAND_ID + " INTEGER," + KEY_BRAND + " VARCHAR,"
            + KEY_DISPLAY_ID + " INTEGER,"
            + KEY_DISPLAY + " VARCHAR," + KEY_QUANTITY + " INTEGER," + KEY_IMAGE + " VARCHAR,"
            + KEY_CATEGORY_ID + " INTEGER,"
            + KEY_COMP_TRACT_EXIST + " INTEGER,"
            + KEY_PROCESS_ID + " INTEGER)";


    public static final String CREATE_TABLE_AFTER_ADDITIONAL_DETAILS = "CREATE TABLE "
            + TABLE_INSERT_AFTER_ADDTIONAL_DETAILS + " (" + KEY_ID
            + " INTEGER PRIMARY KEY AUTOINCREMENT," + KEY_STORE_ID + " VARCHAR,"
            + KEY_BRAND_ID + " VARCHAR," + KEY_BRAND + " VARCHAR,"
            + KEY_DISPLAY_ID + " VARCHAR,"
            + KEY_DISPLAY + " VARCHAR," + KEY_QUANTITY + " VARCHAR," + KEY_IMAGE + " VARCHAR,"
            + KEY_CATEGORY_ID + " VARCHAR)";


    public static final String CREATE_TABLE_TOT_AFTER = "CREATE TABLE "
            + TABLE_TOT_AFTER + " (" + KEY_ID
            + " INTEGER PRIMARY KEY AUTOINCREMENT ," + KEY_AFTER_QUANTITY + " VARCHAR,"
            + KEY_DISPLAY_ID + " VARCHAR,"
            + KEY_AFTER_STOCK_COUNT + " VARCHAR,"
            + KEY_STORE_ID + " VARCHAR,"
            + KEY_CATEGORY_ID + " VARCHAR,"
            + KEY_IMAGE1 + " VARCHAR,"
            + KEY_IMAGE2 + " VARCHAR,"
            + KEY_IMAGE3 + " VARCHAR,"
            + KEY_DISPLAY + " VARCHAR,"
            + KEY_TARGER_QUANTITY + " VARCHAR,"
            + KEY_BRAND_ID + " VARCHAR,"
            + KEY_BRAND + " VARCHAR,"
            + UNIQUE_KEY_ID + " VARCHAR,"
            + KEY_TYPE + " VARCHAR,"
            + KEY_IMAGE_URL + " VARCHAR,"
            + KEY_PROCESS_ID + " VARCHAR)";


    public static final String CREATE_TABLE_TOT_BEFORE = "CREATE TABLE "
            + TABLE_TOT_BEFORE + " (" + KEY_ID
            + " INTEGER PRIMARY KEY AUTOINCREMENT ," + KEY_BEFORE_QUANTITY + " VARCHAR,"
            + KEY_DISPLAY_ID + " VARCHAR,"
            + KEY_BEFORE_STOCK_COUNT + " VARCHAR,"
            + KEY_STORE_ID + " VARCHAR,"
            + KEY_CATEGORY_ID + " VARCHAR,"
            + KEY_IMAGE1 + " VARCHAR,"
            + KEY_IMAGE2 + " VARCHAR,"
            + KEY_IMAGE3 + " VARCHAR,"
            + KEY_DISPLAY + " VARCHAR,"
            + KEY_TARGER_QUANTITY + " VARCHAR,"
            + KEY_BRAND_ID + " VARCHAR,"
            + KEY_TYPE + " VARCHAR,"
            + KEY_PROCESS_ID + " VARCHAR)";


    public static final String CREATE_TABLE_COVERAGE_DATA = "CREATE TABLE DR_STORE_COVERAGE " +
            "(KEY_ID INTEGER PRIMARY KEY AUTOINCREMENT ,STORE_ID INT, VISIT_DATE TEXT, IN_TIME TEXT, OUT_TIME TEXT, LATITUDE TEXT, " +
            "LONGITUDE TEXT,  REASON_ID INT, SUB_REASON_ID INT, REMARK  TEXT, IMAGE_ALLOW INT, " +
            "STORE_IMAGE TEXT,  USER_ID TEXT, APP_VERSION TEXT, PROCESS_ID INT,CHECKOUT_IMAGE VARCHAR, STATUS VARCHAR)";


    // Upload Image

    public static final String METHOD_Get_DR_STORE_IMAGES = "GET_StoreLayout_IMAGES";
    public static final String SOAP_ACTION_Get_DR_STORE_IMAGES = "http://tempuri.org/"
            + METHOD_Get_DR_STORE_IMAGES;

    public static final String METHOD_Get_DR_CHEQUE_IMAGES = "Upload_StoreCheque_IMAGES";
    public static final String SOAP_ACTION_Get_DR_CHEQUE_IMAGES = "http://tempuri.org/"
            + METHOD_Get_DR_CHEQUE_IMAGES;

    public static final String METHOD_Get_DR_POSM_IMAGES = "GetImageNew";
    public static final String SOAP_ACTION_Get_DR_POSM_IMAGES = "http://tempuri.org/"
            + METHOD_Get_DR_POSM_IMAGES;


    public static final String METHOD_Upload_StoreDeviationImage = "GetImageNew";
    public static final String SOAP_ACTION_Upload_StoreDeviationImage = "http://tempuri.org/"
            + METHOD_Upload_StoreDeviationImage;

    public static final String METHOD_Get_DR_COMPLIANCE_IMAGES = "GET_Store_SecondaryWindowImage";
    public static final String SOAP_ACTION_Get_DR_COMPLIANCE_IMAGES = "http://tempuri.org/"
            + METHOD_Get_DR_COMPLIANCE_IMAGES;

    public static final String METHOD_Get_DR_STORE_IMAGES_GEO = "Upload_StoreGeoTag_IMAGES";
    public static final String SOAP_ACTION_DR_STORE_IMAGES_GEO = "http://tempuri.org/"
            + METHOD_Get_DR_STORE_IMAGES_GEO;

    public static final String CREATE_TABLE_INSERT_GEOTAG = "CREATE TABLE "
            + TABLE_INSERT_GEO_TAG + " (" + KEY_ID
            + " INTEGER PRIMARY KEY AUTOINCREMENT ," + KEY_STORE_ID
            + " VARCHAR," + KEY_LATITUDE + " VARCHAR," + KEY_LONGITUDE
            + " VARCHAR," + KEY_STATUS + " VARCHAR," + KEY_IMAGE_PATH1
            + " VARCHAR," + KEY_IMAGE_PATH2 + " VARCHAR," + KEY_IMAGE_PATH3
            + " VARCHAR)";


    public static final String CREATE_TABLE_QUESTION_ANSWER = "CREATE TABLE "
            + TABLE_QUESTION_ANSWER + " (" + KEY_ID
            + " INTEGER PRIMARY KEY AUTOINCREMENT ," + "QUESTION_ID" + " VARCHAR,"
            + "DISPLAY_ID" + " VARCHAR,"
            + "ANSWER" + " VARCHAR,"
            + KEY_STORE_ID + " VARCHAR,"
            + KEY_CATEGORY_ID + " VARCHAR,"
            + UNIQUE_KEY_ID + " VARCHAR,"
            + "QUESTION" + " VARCHAR,"
            + KEY_PROCESS_ID + " VARCHAR)";


    public static final String CREATE_TABLE_QUESTION_ANSWER_STOCKAFTER = "CREATE TABLE "
            + TABLE_QUESTION_ANSWER_STOCKAFTER + " (" + KEY_ID
            + " INTEGER PRIMARY KEY AUTOINCREMENT ," + "QUESTION_ID" + " VARCHAR,"
            + "DISPLAY_ID" + " VARCHAR,"
            + "ANSWER" + " VARCHAR,"
            + KEY_STORE_ID + " VARCHAR,"
            + KEY_CATEGORY_ID + " VARCHAR,"
            + UNIQUE_KEY_ID + " VARCHAR,"
            + "QUESTION" + " VARCHAR,"
            + KEY_PROCESS_ID + " VARCHAR)";


    public static final String CREATE_TABLE_PROMOTION_DATA = "CREATE TABLE "
            + TABLE_PROMOTION_DATA + " (" + KEY_ID
            + " INTEGER PRIMARY KEY AUTOINCREMENT ," + "PROMOTION" + " VARCHAR,"
            + "SKU_ID" + " VARCHAR,"
            + "STOCK" + " VARCHAR,"
            + "POP" + " VARCHAR,"
            + "RUNNING" + " VARCHAR,"
            + KEY_STORE_ID + " VARCHAR,"
            + KEY_CATEGORY_ID + " VARCHAR,"
            + KEY_PROCESS_ID + " VARCHAR,"
            + KEY_IMAGE + " VARCHAR,"
            + "SPECIAL_ID" + " VARCHAR,"
            + "RUNNING_CHILD_TOGGLE" + " VARCHAR,"
            + "RUNNING_CHILD_PRICE" + " INTEGER,"
            + "KEY_SKUNAME" + " VARCHAR)";


    public static final String TABLE_AFTERSTOCK_OTHER = "AFTERSTOCK_OTHER";
    public static final String TABLE_STOCKWAREHOUSE = "STOCKWAREHOUSE_DATA";
    public static final String TABLE_SHELF_VISIBILITY = "SHELF_VISIBILITY_WITH_STOCK";


    public static final String UID = "UID";
    public static final String CAT_ID = "CAT_ID";
    public static final String YESNO = "YESNO";
    public static final String DISPLAY_ID = "DISPLAY_ID";
    public static final String SHELF_ID = "SHELF_ID";
    public static final String SHELF_NAME = "SHELF_NAME";

    public static final String DISPLAY = "DISPLAY";
    public static final String KEY_STORE_TYPEID = "STORE_TYPEID";
    public static final String KEY_EXISTS = "IS_EXISTS";

    public static final String CREATE_TABLE_INSERT_AFTERSOCK_OTHER = "CREATE TABLE IF NOT EXISTS "
            + TABLE_AFTERSTOCK_OTHER + " (" + KEY_ID
            + " INTEGER PRIMARY KEY AUTOINCREMENT ," + KEY_STORE_ID
            + " VARCHAR," + YESNO + " VARCHAR," + DISPLAY_ID + " VARCHAR," + CAT_ID
            + " VARCHAR," + DISPLAY
            + " VARCHAR," + UID + " VARCHAR," + " VARCHAR,"
            + " IMAGE_URL" + " VARCHAR,"
            + KEY_PROCESS_ID + " VARCHAR,"
            + KEY_IMAGE + " VARCHAR,"
            + KEY_EXISTS + " INTEGER,"
            + KEY_BRAND + " VARCHAR,"
            + KEY_BRAND_ID + " VARCHAR)";


    public static final String CREATE_TABLE_INSERT_STOCKWAREHOUSE = "CREATE TABLE IF NOT EXISTS "
            + TABLE_STOCKWAREHOUSE + " (" + KEY_ID
            + " INTEGER PRIMARY KEY AUTOINCREMENT ," + KEY_STORE_ID
            + " VARCHAR," + KEY_CATEGORY_ID + " VARCHAR," + KEY_STOCK + " INTEGER,"
            + KEY_PROCESS_ID + " VARCHAR,"
            + KEY_BRAND + " VARCHAR,"
            + KEY_SKU_ID + " VARCHAR,"
            + KEY_SKUNAME + " VARCHAR,"
            + KEY_BRAND_ID + " VARCHAR,"
            + KEY_USER_ID + " VARCHAR)";


    public static final String CREATE_TABLE_INSERT_SHELF_VISIBILITY = "CREATE TABLE IF NOT EXISTS "
            + TABLE_SHELF_VISIBILITY + " (" + KEY_ID
            + " INTEGER PRIMARY KEY AUTOINCREMENT ," + KEY_STORE_ID
            + " INTEGER,"
            + KEY_BRAND_ID + " INTEGER," + CAT_ID
            + " VARCHAR,"
            + " IMAGE_URL" + " VARCHAR,"
            + " FACING_TARGET" + " INTEGER,"
            + KEY_PROCESS_ID + " VARCHAR,"
            + KEY_STORE_TYPEID + " VARCHAR,"
            + KEY_BRAND + " VARCHAR,"
            + CommonString.YESNO + " INTEGER)";


    public static final String CREATE_TABLE_GEO_TAG_MAPPING = "CREATE TABLE IF NOT EXISTS "
            + TABLE_GEO_TAG_MAPPING + " (" + KEY_ID
            + " INTEGER PRIMARY KEY AUTOINCREMENT ," + KEY_STORE_ID
            + " VARCHAR," + "STORETYPE" + " VARCHAR," + "STORENAME" + " VARCHAR," + KEY_LATITUDE
            + " VARCHAR," + KEY_LONGITUDE + " VARCHAR," + "GEO_TAG_STATUS"
            + " VARCHAR," + "CITY" + " VARCHAR," + "KEYACCOUNT" + " VARCHAR)";


    public static final String CREATE_TABLE_STORE_GEOTAGGING = "CREATE TABLE IF NOT EXISTS "
            + TABLE_STORE_GEOTAGGING
            + " ("
            + "KEY_ID"
            + " INTEGER PRIMARY KEY AUTOINCREMENT ,"

            + "STORE_ID"
            + " VARCHAR,"

            + "LATITUDE"
            + " VARCHAR,"

            + "LONGITUDE"
            + " VARCHAR,"

            + "GEO_TAG"
            + " VARCHAR,"

            + "FRONT_IMAGE" + " VARCHAR)";

    public static final String TABLE_INSERT_MERCHANDISER_ATTENDENCE_TABLE = "MERCHANDISER_ATTENDENCE_TABLE";

    public static final String CREATE_TABLE_INSERT_MERCHANDISER_ATTENDENCE_TABLE = "CREATE TABLE IF NOT EXISTS "
            + TABLE_INSERT_MERCHANDISER_ATTENDENCE_TABLE
            + " ("
            + "KEY_ID"
            + " INTEGER PRIMARY KEY AUTOINCREMENT ,"
            + KEY_USER_ID
            + " VARCHAR,"
            + KEY_VISIT_DATE
            + " VARCHAR,"
            + KEY_STATUS
            + " VARCHAR,"
            + KEY_IMAGE
            + " VARCHAR,"
            + KEY_REASON
            + " VARCHAR,"
            + KEY_REASON_ID
            + " INTEGER,"
            + KEY_ENTRY_ALLOW + " INTEGER)";


    public static final String TABLE_INSERT_COMPETITION_PROMOTION = "TABLE_COMPETITION_PROMOTION";
    public static final String CREATE_TABLE_INSERT_COMPETITION_PROMOTION = "CREATE TABLE "
            + TABLE_INSERT_COMPETITION_PROMOTION + " (" + KEY_ID
            + " INTEGER PRIMARY KEY AUTOINCREMENT," + KEY_STORE_ID + " INTEGER,"
            + KEY_CATEGORY_ID + " INTEGER,"
            + KEY_PROCESS_ID + " INTEGER,"
            + KEY_COMPANY_ID + " INTEGER,"
            + KEY_COMPANY + " VARCHAR,"
            + KEY_BRAND_ID + " INTEGER," + KEY_BRAND + " VARCHAR,"
            + KEY_SKU_ID + " INTEGER," + KEY_SKUNAME + " VARCHAR," + KEY_SKU_MRP + " INTEGER,"
            + KEY_PRICEOFF_TOGGLE + " VARCHAR," + KEY_PRICEOFF + " INTEGER,"
            + KEY_PROMOTION + " VARCHAR,"

            + KEY_COMP_SEGMENT_ID + " INTEGER,"
            + KEY_COMP_SEGMENT + " VARCHAR,"

            + KEY_IMAGE + " VARCHAR)";

   // + KEY_COMP_TRACT_EXIST + " INTEGER,"


}
