package com.cpm.xmlHandler;

import java.io.IOException;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import com.cpm.xmlGetterSetter.AdditionalGetterSetter;
import com.cpm.xmlGetterSetter.AttendenceStatusGetterSetter;
import com.cpm.xmlGetterSetter.CompetitionGetterSetter;
import com.cpm.xmlGetterSetter.DisplayGetterSetter;
import com.cpm.xmlGetterSetter.EmpMeetingStatus;
import com.cpm.xmlGetterSetter.FailureGetterSetter;
import com.cpm.xmlGetterSetter.JCPGetterSetter;
import com.cpm.xmlGetterSetter.LoginGetterSetter;
import com.cpm.xmlGetterSetter.MappingCompetitionPromotionGetterSetter;
import com.cpm.xmlGetterSetter.MappingWellnessSos;
import com.cpm.xmlGetterSetter.NonWorkingAttendenceGetterSetter;
import com.cpm.xmlGetterSetter.NonWorkingGetterSetter;
import com.cpm.xmlGetterSetter.NoticeBoardGetterSetter;
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


public class XMLHandlers {

    // LOGIN XML HANDLER
    public static LoginGetterSetter loginXMLHandler(XmlPullParser xpp,
                                                    int eventType) {
        LoginGetterSetter lgs = new LoginGetterSetter();

        try {
            while (xpp.getEventType() != XmlPullParser.END_DOCUMENT) {
                if (xpp.getEventType() == XmlPullParser.START_TAG) {
                    if (xpp.getName().equals("RIGHT_NAME")) {
                        lgs.setRIGHT_NAME(xpp.nextText());
                    }
                    if (xpp.getName().equals("APP_VERSION")) {
                        lgs.setAPP_VERSION(xpp.nextText());
                    }
                    if (xpp.getName().equals("APP_PATH")) {
                        lgs.setAPP_PATH(xpp.nextText());
                    }
                    if (xpp.getName().equals("CURRENTDATE")) {
                        lgs.setCURRENTDATE(xpp.nextText());
                    }

                }
                xpp.next();
            }
        } catch (XmlPullParserException e) {

            e.printStackTrace();
        } catch (IOException e) {

            e.printStackTrace();
        }
        return lgs;
    }

    // FAILURE XML HANDLER
    public static FailureGetterSetter failureXMLHandler(XmlPullParser xpp,
                                                        int eventType) {
        FailureGetterSetter failureGetterSetter = new FailureGetterSetter();

        try {
            while (xpp.getEventType() != XmlPullParser.END_DOCUMENT) {
                if (xpp.getEventType() == XmlPullParser.START_TAG) {
                    if (xpp.getName().equals("STATUS")) {
                        failureGetterSetter.setStatus(xpp.nextText());
                    }
                    if (xpp.getName().equals("ERRORMSG")) {
                        failureGetterSetter.setErrorMsg(xpp.nextText());
                    }

                }
                xpp.next();
            }
        } catch (XmlPullParserException e) {

            e.printStackTrace();
        } catch (IOException e) {

            e.printStackTrace();
        }
        return failureGetterSetter;
    }

    // SKU XML HANDLER
    public static SKUGetterSetter SKUXMLHandler(XmlPullParser xpp, int eventType) {
        SKUGetterSetter jcpGetterSetter = new SKUGetterSetter();

        try {
            while (xpp.getEventType() != XmlPullParser.END_DOCUMENT) {
                if (xpp.getEventType() == XmlPullParser.START_TAG) {

                    if (xpp.getName().equals("META_DATA")) {
                        jcpGetterSetter.setMeta_data(xpp.nextText());
                    }

                    if (xpp.getName().equals("SKU_ID")) {
                        jcpGetterSetter.setSku_id(xpp.nextText());
                    }
                    if (xpp.getName().equals("SKU")) {
                        jcpGetterSetter.setSku_name(xpp.nextText());
                    }

                    if (xpp.getName().equals("BRAND_ID")) {
                        jcpGetterSetter.setBrand_id(xpp.nextText());
                    }
                    if (xpp.getName().equals("BRAND")) {
                        jcpGetterSetter.setBrand(xpp.nextText());
                    }
                    if (xpp.getName().equals("CATEGORY_ID")) {
                        jcpGetterSetter.setCategory_id(xpp.nextText());
                    }
                    if (xpp.getName().equals("CATEGORY")) {
                        jcpGetterSetter.setCategory_name(xpp.nextText());
                    }
                    if (xpp.getName().equals("COMPANY_ID")) {
                        jcpGetterSetter.setCompany_id(xpp.nextText());
                    }
                    if (xpp.getName().equals("MRP")) {
                        jcpGetterSetter.setMRP_sku(xpp.nextText());
                    }
                }
                xpp.next();
            }
        } catch (XmlPullParserException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return jcpGetterSetter;
    }

    // JCP XML HANDLER
    public static JCPGetterSetter JCPXMLHandler(XmlPullParser xpp, int eventType) {
        JCPGetterSetter jcpGetterSetter = new JCPGetterSetter();

        try {
            while (xpp.getEventType() != XmlPullParser.END_DOCUMENT) {
                if (xpp.getEventType() == XmlPullParser.START_TAG) {

                    if (xpp.getName().equals("META_DATA")) {
                        jcpGetterSetter.setMeta_data(xpp.nextText());
                    }

                    if (xpp.getName().equals("STORE_ID")) {
                        jcpGetterSetter.setSTORE_ID(xpp.nextText());
                    }
                    if (xpp.getName().equals("EMP_ID")) {
                        jcpGetterSetter.setEMP_ID(xpp.nextText());
                    }

                    if (xpp.getName().equals("STORE")) {
                        jcpGetterSetter.setSTORE(xpp.nextText());
                    }
                    if (xpp.getName().equals("CITY")) {
                        jcpGetterSetter.setCITY(xpp.nextText());
                    }
                    if (xpp.getName().equals("VISIT_DATE")) {
                        jcpGetterSetter.setVISIT_DATE(xpp.nextText());
                    }
                    if (xpp.getName().equals("PROCESS_ID")) {
                        jcpGetterSetter.setPROCESS_ID(xpp.nextText());
                    }
                    if (xpp.getName().equals("UPLOAD_STATUS")) {
                        jcpGetterSetter.setUPLOAD_STATUS(xpp.nextText());
//							jcpGetterSetter.setUPLOAD_STATUS("N");
                    }

                    if (xpp.getName().equals("REGION_ID")) {
                        jcpGetterSetter.setREGION_ID(xpp.nextText());
                    }

                    if (xpp.getName().equals("KEY_ID")) {
                        jcpGetterSetter.setKEY_ID(xpp.nextText());
                    }

                    if (xpp.getName().equals("STORETYPE_ID")) {
                        jcpGetterSetter.setSTORETYPE_ID(xpp.nextText());
                    }

                    if (xpp.getName().equals("CHECKOUT_STATUS")) {
                        jcpGetterSetter.setCHECKOUT_STATUS(xpp.nextText());
                    }


                    if (xpp.getName().equals("PKD_ENABLE")) {
                        jcpGetterSetter.setPACKED_KEY(xpp.nextText());
                    }

                    if (xpp.getName().equals("STATE_ID")) {
                        jcpGetterSetter.setSTATE_ID(xpp.nextText());
                    }
                    if (xpp.getName().equals("CLASS_ID")) {
                        jcpGetterSetter.setCLASS_ID(xpp.nextText());
                    }

                    if (xpp.getName().equals("COMP_ENABLE")) {
                        jcpGetterSetter.setCOMP_ENABLE(xpp.nextText());
                    }

                }
                xpp.next();
            }
        } catch (XmlPullParserException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return jcpGetterSetter;
    }

    //Brand - Master
    public static SKUGetterSetter BrandXMLHandler(XmlPullParser xpp, int eventType) {
        SKUGetterSetter jcpGetterSetter = new SKUGetterSetter();

        try {
            while (xpp.getEventType() != XmlPullParser.END_DOCUMENT) {
                if (xpp.getEventType() == XmlPullParser.START_TAG) {

                    if (xpp.getName().equals("META_DATA")) {
                        jcpGetterSetter.setMeta_data(xpp.nextText());
                    }

                    if (xpp.getName().equals("BRAND_ID")) {
                        jcpGetterSetter.setBrand_id(xpp.nextText());
                    }
                    if (xpp.getName().equals("BRAND")) {
                        jcpGetterSetter.setBrand(xpp.nextText());
                    }

                    if (xpp.getName().equals("CATEGORY")) {
                        jcpGetterSetter.setCategory_name(xpp.nextText());
                    }
                    if (xpp.getName().equals("CATEGORY_ID")) {
                        jcpGetterSetter.setCategory_id(xpp.nextText());
                    }
                    if (xpp.getName().equals("COMPANY_ID")) {
                        jcpGetterSetter.setCompany_id(xpp.nextText());
                    }

                }
                xpp.next();
            }
        } catch (XmlPullParserException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return jcpGetterSetter;
    }

    //Stock - Mapping
    public static StockMappingGetterSetter StockMappingXMLHandler(XmlPullParser xpp, int eventType) {
        StockMappingGetterSetter jcpGetterSetter = new StockMappingGetterSetter();

        try {
            while (xpp.getEventType() != XmlPullParser.END_DOCUMENT) {
                if (xpp.getEventType() == XmlPullParser.START_TAG) {

                    if (xpp.getName().equals("META_DATA")) {
                        jcpGetterSetter.setMeta_data(xpp.nextText());
                    }

                    if (xpp.getName().equals("SKU_ID")) {
                        jcpGetterSetter.setSku_id(xpp.nextText());
                    }
                    if (xpp.getName().equals("BRAND_ID")) {
                        jcpGetterSetter.setBrand_id(xpp.nextText());
                    }

                    if (xpp.getName().equals("SKU_SEQUENCE")) {
                        jcpGetterSetter.setSku_sequence(xpp.nextText());
                    }
                    if (xpp.getName().equals("BRAND_SEQUENCE")) {
                        jcpGetterSetter.setBrand_sequence(xpp.nextText());
                    }
                    if (xpp.getName().equals("PROCESS_ID")) {
                        jcpGetterSetter.setProcess_id(xpp.nextText());
                    }

                    if (xpp.getName().equals("KEY_ID")) {
                        jcpGetterSetter.setKEY_ID(xpp.nextText());
                    }

                    if (xpp.getName().equals("STORETYPE_ID")) {
                        jcpGetterSetter.setSTORETYPE_ID(xpp.nextText());
                    }

                    if (xpp.getName().equals("CLASS_ID")) {
                        jcpGetterSetter.setCLASS_ID(xpp.nextText());
                    }

                    if (xpp.getName().equals("STATE_ID")) {
                        jcpGetterSetter.setSTATE_ID(xpp.nextText());
                    }

                }
                xpp.next();
            }
        } catch (XmlPullParserException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return jcpGetterSetter;
    }


    public static DisplayGetterSetter DisplayXMLHandler(XmlPullParser xpp,
                                                        int eventType) {
        DisplayGetterSetter failureGetterSetter = new DisplayGetterSetter();

        try {
            while (xpp.getEventType() != XmlPullParser.END_DOCUMENT) {
                if (xpp.getEventType() == XmlPullParser.START_TAG) {

                    if (xpp.getName().equals("META_DATA")) {
                        failureGetterSetter.setMeta_data(xpp.nextText());
                    }

                    if (xpp.getName().equals("DISPLAY_ID")) {
                        failureGetterSetter.setDisplay_id(xpp.nextText());
                    }
                    if (xpp.getName().equals("DISPLAY")) {
                        failureGetterSetter.setDisplay(xpp.nextText());
                    }

                    if (xpp.getName().equals("IMAGE_URL")) {
                        failureGetterSetter.setImage_url(xpp.nextText());

                    }
                    if (xpp.getName().equals("PATH")) {
                        failureGetterSetter.setPath(xpp.nextText());

                    }
                    if (xpp.getName().equals("COMPETITOR_ONLY")) {
                        failureGetterSetter.setCOMPETITOR_ONLY(xpp.nextText());

                    }

                }
                xpp.next();
            }
        } catch (XmlPullParserException e) {

            e.printStackTrace();
        } catch (IOException e) {

            e.printStackTrace();
        }
        return failureGetterSetter;
    }


    public static TDSGetterSetter TDSXMLHandler(XmlPullParser xpp,
                                                int eventType) {
        TDSGetterSetter failureGetterSetter = new TDSGetterSetter();

        try {
            while (xpp.getEventType() != XmlPullParser.END_DOCUMENT) {
                if (xpp.getEventType() == XmlPullParser.START_TAG) {

                    if (xpp.getName().equals("META_DATA")) {
                        failureGetterSetter.setMeta_data(xpp.nextText());
                    }

                    if (xpp.getName().equals("STORE_ID")) {
                        failureGetterSetter.setSTORE_ID(xpp.nextText());
                    }
                    if (xpp.getName().equals("CATEGORY_ID")) {
                        failureGetterSetter.setCATEGORY_ID(xpp.nextText());
                    }

                    if (xpp.getName().equals("DISPLAY_ID")) {
                        failureGetterSetter.setDISPLAY_ID(xpp.nextText());
                    }

                    if (xpp.getName().equals("TARGET_QTY")) {
                        failureGetterSetter.setTARGET_QTY(xpp.nextText());
                    }

                    if (xpp.getName().equals("PROCESS_ID")) {
                        failureGetterSetter.setPROCESS_ID(xpp.nextText());
                    }

                    if (xpp.getName().equals("BRAND_ID")) {
                        failureGetterSetter.setBRAND_ID(xpp.nextText());
                    }

                    if (xpp.getName().equals("DISPLAY_TYPE")) {
                        failureGetterSetter.setTYPE(xpp.nextText());
                    }

                    if (xpp.getName().equals("ID")) {
                        failureGetterSetter.setUID(xpp.nextText());
                    }

                }
                xpp.next();
            }
        } catch (XmlPullParserException e) {

            e.printStackTrace();
        } catch (IOException e) {

            e.printStackTrace();
        }
        return failureGetterSetter;
    }


    public static SKUGetterSetter XMLHandler(XmlPullParser xpp,
                                             int eventType) {
        SKUGetterSetter failureGetterSetter = new SKUGetterSetter();

        try {
            while (xpp.getEventType() != XmlPullParser.END_DOCUMENT) {
                if (xpp.getEventType() == XmlPullParser.START_TAG) {

                    if (xpp.getName().equals("META_DATA")) {
                        failureGetterSetter.setMeta_data(xpp.nextText());
                    }

                    if (xpp.getName().equals("META_DATA1")) {
                        failureGetterSetter.setStock_entry_table(xpp.nextText());
                    }

                    if (xpp.getName().equals("META_DATA2")) {
                        failureGetterSetter.setStock_image_table(xpp.nextText());
                    }

                    if (xpp.getName().equals("META_DATA3")) {
                        failureGetterSetter.setTot_entry_table(xpp.nextText());
                    }


                    if (xpp.getName().equals("META_DATA4")) {
                        failureGetterSetter.setTot_image_table(xpp.nextText());
                    }


                }
                xpp.next();
            }
        } catch (XmlPullParserException e) {

            e.printStackTrace();
        } catch (IOException e) {

            e.printStackTrace();
        }
        return failureGetterSetter;
    }


    public static PromotionalDataSetterGetter PromotionalXMLHandler(XmlPullParser xpp,
                                                                    int eventType) {
        PromotionalDataSetterGetter failureGetterSetter = new PromotionalDataSetterGetter();

        try {
            while (xpp.getEventType() != XmlPullParser.END_DOCUMENT) {
                if (xpp.getEventType() == XmlPullParser.START_TAG) {

                    if (xpp.getName().equals("META_DATA")) {
                        failureGetterSetter.setMeta_data(xpp.nextText());
                    }

                    if (xpp.getName().equals("ID")) {
                        failureGetterSetter.setID(xpp.nextText());
                    }

                    if (xpp.getName().equals("KEY_ID")) {
                        failureGetterSetter.setKEY_ID(xpp.nextText());
                    }

                    if (xpp.getName().equals("PROCESS_ID")) {
                        failureGetterSetter.setPROCESS_ID(xpp.nextText());
                    }


                    if (xpp.getName().equals("CATEGORY_ID")) {
                        failureGetterSetter.setCATEGORY_ID(xpp.nextText());
                    }


                    if (xpp.getName().equals("SKU_ID")) {
                        failureGetterSetter.setSKU_ID(xpp.nextText());
                    }


                    if (xpp.getName().equals("PROMOTION")) {
                        failureGetterSetter.setPROMOTION(xpp.nextText());
                    }

						

						/*if (xpp.getName().equals("REGION_ID")) {
                            failureGetterSetter.setREGION_ID(xpp.nextText());
						}*/

                    if (xpp.getName().equals("STATE_ID")) {
                        failureGetterSetter.setSTATE_ID(xpp.nextText());
                    }

                }
                xpp.next();
            }
        } catch (XmlPullParserException e) {

            e.printStackTrace();
        } catch (IOException e) {

            e.printStackTrace();
        }
        return failureGetterSetter;
    }


    // code for mapping catman stock after


    public static catmanMapping MappingStockAfterCatmanXMLHandler(XmlPullParser xpp,
                                                                  int eventType) {
        catmanMapping failureGetterSetter = new catmanMapping();

        try {
            while (xpp.getEventType() != XmlPullParser.END_DOCUMENT) {
                if (xpp.getEventType() == XmlPullParser.START_TAG) {

                    if (xpp.getName().equals("META_DATA")) {
                        failureGetterSetter.setTable_Structure(xpp.nextText());
                    }

                    if (xpp.getName().equals("STORE_ID")) {
                        failureGetterSetter.setStore_ID(xpp.nextText());
                    }

                    if (xpp.getName().equals("CATEGORY_ID")) {
                        failureGetterSetter.setCategoryid(xpp.nextText());
                    }

                    if (xpp.getName().equals("DISPLAY_ID")) {
                        failureGetterSetter.setDisplayid(xpp.nextText());
                    }


                    if (xpp.getName().equals("PROCESS_ID")) {
                        failureGetterSetter.setProcessid(xpp.nextText());
                    }


                    if (xpp.getName().equals("UID")) {
                        failureGetterSetter.setUid(xpp.nextText());
                    }

                    if (xpp.getName().equals("BRAND_ID")) {
                        failureGetterSetter.setBrand_id(xpp.nextText());
                    }


                }
                xpp.next();
            }
        } catch (XmlPullParserException e) {

            e.printStackTrace();
        } catch (IOException e) {

            e.printStackTrace();
        }
        return failureGetterSetter;
    }


    public static ShelfMaster ShelfMasterXMLHandler(XmlPullParser xpp,
                                                    int eventType) {
        ShelfMaster failureGetterSetter = new ShelfMaster();

        try {
            while (xpp.getEventType() != XmlPullParser.END_DOCUMENT) {
                if (xpp.getEventType() == XmlPullParser.START_TAG) {

                    if (xpp.getName().equals("META_DATA")) {
                        failureGetterSetter.setTable_Structure(xpp.nextText());
                    }

                    if (xpp.getName().equals("SHELF_ID")) {
                        failureGetterSetter.setShelf_id(xpp.nextText());

                    }

                    if (xpp.getName().equals("SHELF")) {
                        failureGetterSetter.setShelf(xpp.nextText());
                    }


                }
                xpp.next();
            }
        } catch (XmlPullParserException e) {

            e.printStackTrace();
        } catch (IOException e) {

            e.printStackTrace();
        }
        return failureGetterSetter;
    }


    public static MappingWellnessSos MappingWellnessXMLHandler(XmlPullParser xpp,
                                                               int eventType) {
        MappingWellnessSos failureGetterSetter = new MappingWellnessSos();

        try {
            while (xpp.getEventType() != XmlPullParser.END_DOCUMENT) {
                if (xpp.getEventType() == XmlPullParser.START_TAG) {

                    if (xpp.getName().equals("META_DATA")) {
                        failureGetterSetter.setTable_Structure(xpp.nextText());
                    }

                    if (xpp.getName().equals("PROCESS_ID")) {
                        failureGetterSetter.setPROCESS_ID(xpp.nextText());

                    }

                    if (xpp.getName().equals("REGION_ID")) {
                        failureGetterSetter.setREGION_ID(xpp.nextText());
                    }

                    if (xpp.getName().equals("STORETYPE_ID")) {
                        failureGetterSetter.setSTORETYPE_ID(xpp.nextText());
                    }

                    if (xpp.getName().equals("BRAND_ID")) {
                        failureGetterSetter.setBRAND_ID(xpp.nextText());
                    }

                    if (xpp.getName().equals("SHELF_ID")) {
                        failureGetterSetter.setSHELF_ID(xpp.nextText());
                    }


                }
                xpp.next();
            }
        } catch (XmlPullParserException e) {

            e.printStackTrace();
        } catch (IOException e) {

            e.printStackTrace();
        }
        return failureGetterSetter;
    }


    public static EmpMeetingStatus EmpMeetingStatusXMLHandler(XmlPullParser xpp,
                                                              int eventType) {
        EmpMeetingStatus failureGetterSetter = new EmpMeetingStatus();

        try {
            while (xpp.getEventType() != XmlPullParser.END_DOCUMENT) {
                if (xpp.getEventType() == XmlPullParser.START_TAG) {

                    if (xpp.getName().equals("META_DATA")) {
                        failureGetterSetter.setTable_Structure(xpp.nextText());
                    }

                    if (xpp.getName().equals("STATUS")) {
                        failureGetterSetter.setSTATUS(xpp.nextText());

                    }


                }
                xpp.next();
            }
        } catch (XmlPullParserException e) {

            e.printStackTrace();
        } catch (IOException e) {

            e.printStackTrace();
        }
        return failureGetterSetter;
    }

    public static StoreWise_Pss StoreWisePssXMLHandler(XmlPullParser xpp,
                                                       int eventType) {
        StoreWise_Pss failureGetterSetter = new StoreWise_Pss();

        try {
            while (xpp.getEventType() != XmlPullParser.END_DOCUMENT) {
                if (xpp.getEventType() == XmlPullParser.START_TAG) {

                    if (xpp.getName().equals("META_DATA")) {
                        failureGetterSetter.setTable_Structure(xpp.nextText());
                    }
                    if (xpp.getName().equals("STORE_ID")) {
                        failureGetterSetter.setStore_id(xpp.nextText());
                    }

                    if (xpp.getName().equals("PROCESS_ID")) {
                        failureGetterSetter.setProcess_id(xpp.nextText());

                    }
                    if (xpp.getName().equals("CATEGORY_ID")) {
                        failureGetterSetter.setCategory_id(xpp.nextText());
                    }
                    if (xpp.getName().equals("PERIOD")) {
                        failureGetterSetter.setPeriod(xpp.nextText());
                    }
                    if (xpp.getName().equals("PSS")) {
                        failureGetterSetter.setPss(xpp.nextText());
                    }

                    if (xpp.getName().equals("SOS_SCORE")) {
                        failureGetterSetter.setSos(xpp.nextText());
                    }
//////ASSET_SCORE is combination of TOT and PAID
                    if (xpp.getName().equals("ASSET_SCORE")) {
                        failureGetterSetter.setASSET_SCORE(xpp.nextText());
                    }

                    if (xpp.getName().equals("PROMO_SCORE")) {
                        failureGetterSetter.setPROMO_SCORE(xpp.nextText());
                    }

                    if (xpp.getName().equals("STOCK_SCORE")) {
                        failureGetterSetter.setSTOCK_SCORE(xpp.nextText());
                    }
                }
                xpp.next();
            }
        } catch (XmlPullParserException e) {

            e.printStackTrace();
        } catch (IOException e) {

            e.printStackTrace();
        }
        return failureGetterSetter;
    }


    public static TargetToothpestforOHCGetterSetter TargetforOHCCategoryHandler(XmlPullParser xpp,
                                                                                int eventType) {
        TargetToothpestforOHCGetterSetter failureGetterSetter = new TargetToothpestforOHCGetterSetter();

        try {
            while (xpp.getEventType() != XmlPullParser.END_DOCUMENT) {
                if (xpp.getEventType() == XmlPullParser.START_TAG) {

                    if (xpp.getName().equals("META_DATA")) {
                        failureGetterSetter.setTableMetaData(xpp.nextText());
                    }
                    if (xpp.getName().equals("STORE_ID")) {
                        failureGetterSetter.setStore_Id(xpp.nextText());
                    }

                    if (xpp.getName().equals("PROCESS_ID")) {
                        failureGetterSetter.setProcess_Id(xpp.nextText());

                    }
                    if (xpp.getName().equals("CATEGORY_ID")) {
                        failureGetterSetter.setCategory_Id(xpp.nextText());
                    }
                    if (xpp.getName().equals("BRAND_GROUP_ID")) {
                        failureGetterSetter.setBrand_group_Id(xpp.nextText());
                    }
                    if (xpp.getName().equals("BRAND_GROUP")) {
                        failureGetterSetter.setBrand_group(xpp.nextText());
                    }

                    if (xpp.getName().equals("TARGET")) {
                        failureGetterSetter.setTarget(xpp.nextText());
                    }
                }
                xpp.next();
            }
        } catch (XmlPullParserException e) {

            e.printStackTrace();
        } catch (IOException e) {

            e.printStackTrace();
        }
        return failureGetterSetter;
    }


    //

    public static NonWorkingGetterSetter NonWorkingReasonXMLHandler(XmlPullParser xpp,
                                                                    int eventType) {
        NonWorkingGetterSetter failureGetterSetter = new NonWorkingGetterSetter();

        try {
            while (xpp.getEventType() != XmlPullParser.END_DOCUMENT) {
                if (xpp.getEventType() == XmlPullParser.START_TAG) {

                    if (xpp.getName().equals("META_DATA")) {
                        failureGetterSetter.setReason_meta_data(xpp.nextText());
                    }

                    if (xpp.getName().equals("REASON_ID")) {
                        failureGetterSetter.setREASON_ID(xpp.nextText());
                    }

                    if (xpp.getName().equals("REASON")) {
                        failureGetterSetter.setREASON(xpp.nextText());
                    }


                    if (xpp.getName().equals("ENTRY_ALLOW")) {
                        failureGetterSetter.setENTRY_ALLOW(xpp.nextText());
                    }


                }
                xpp.next();
            }
        } catch (XmlPullParserException e) {

            e.printStackTrace();
        } catch (IOException e) {

            e.printStackTrace();
        }
        return failureGetterSetter;
    }


    public static NonWorkingGetterSetter NonWorkingSubReasonXMLHandler(XmlPullParser xpp,
                                                                       int eventType) {
        NonWorkingGetterSetter failureGetterSetter = new NonWorkingGetterSetter();

        try {
            while (xpp.getEventType() != XmlPullParser.END_DOCUMENT) {
                if (xpp.getEventType() == XmlPullParser.START_TAG) {

                    if (xpp.getName().equals("META_DATA")) {
                        failureGetterSetter.setSubreason_meta_data(xpp.nextText());
                    }

                    if (xpp.getName().equals("SUB_REASON_ID")) {
                        failureGetterSetter.setSUB_REASON_ID(xpp.nextText());
                    }

                    if (xpp.getName().equals("SUB_REASON")) {
                        failureGetterSetter.setSUB_REASON(xpp.nextText());
                    }

                    if (xpp.getName().equals("REASON_ID")) {
                        failureGetterSetter.setREASON_ID(xpp.nextText());
                    }


                }
                xpp.next();
            }
        } catch (XmlPullParserException e) {

            e.printStackTrace();
        } catch (IOException e) {

            e.printStackTrace();
        }
        return failureGetterSetter;
    }


    public static QuestionGetterSetter QuestionXMLHandler(XmlPullParser xpp,
                                                          int eventType) {
        QuestionGetterSetter failureGetterSetter = new QuestionGetterSetter();

        try {
            while (xpp.getEventType() != XmlPullParser.END_DOCUMENT) {
                if (xpp.getEventType() == XmlPullParser.START_TAG) {

                    if (xpp.getName().equals("META_DATA")) {
                        failureGetterSetter.setMeta_data(xpp.nextText());
                    }

                    if (xpp.getName().equals("QUESTION_ID")) {
                        failureGetterSetter.setQuestion_id(xpp.nextText());
                    }

                    if (xpp.getName().equals("QUESTION")) {
                        failureGetterSetter.setQuestion(xpp.nextText());
                    }

                }
                xpp.next();
            }
        } catch (XmlPullParserException e) {

            e.printStackTrace();
        } catch (IOException e) {

            e.printStackTrace();
        }
        return failureGetterSetter;
    }


    public static QuestionGetterSetter QuestionMappingXMLHandler(XmlPullParser xpp,
                                                                 int eventType) {
        QuestionGetterSetter failureGetterSetter = new QuestionGetterSetter();

        try {
            while (xpp.getEventType() != XmlPullParser.END_DOCUMENT) {
                if (xpp.getEventType() == XmlPullParser.START_TAG) {

                    if (xpp.getName().equals("META_DATA")) {
                        failureGetterSetter.setMeta_data(xpp.nextText());
                    }

                    if (xpp.getName().equals("QUESTION_ID")) {
                        failureGetterSetter.setQuestion_id(xpp.nextText());
                    }

                    if (xpp.getName().equals("DISPLAY_ID")) {
                        failureGetterSetter.setDisplay_id(xpp.nextText());
                    }

                }
                xpp.next();
            }
        } catch (XmlPullParserException e) {

            e.printStackTrace();
        } catch (IOException e) {

            e.printStackTrace();
        }
        return failureGetterSetter;
    }

    public static AdditionalGetterSetter AdditionalMappingXMLHandler(XmlPullParser xpp,
                                                                     int eventType) {
        AdditionalGetterSetter failureGetterSetter = new AdditionalGetterSetter();

        try {
            while (xpp.getEventType() != XmlPullParser.END_DOCUMENT) {
                if (xpp.getEventType() == XmlPullParser.START_TAG) {

                    if (xpp.getName().equals("META_DATA")) {
                        failureGetterSetter.setMeta_data(xpp.nextText());
                    }

                    if (xpp.getName().equals("STORETYPE_ID")) {
                        failureGetterSetter.setStore_type_id(xpp.nextText());
                    }

                    if (xpp.getName().equals("CATEGORY_ID")) {
                        failureGetterSetter.setCategory_id(xpp.nextText());
                    }

                    if (xpp.getName().equals("DISPLAY_ID")) {
                        failureGetterSetter.setDisplay_id(xpp.nextText());
                    }

                    if (xpp.getName().equals("PROCESS_ID")) {
                        failureGetterSetter.setProcess_id(xpp.nextText());
                    }


                }
                xpp.next();
            }
        } catch (XmlPullParserException e) {

            e.printStackTrace();
        } catch (IOException e) {

            e.printStackTrace();
        }
        return failureGetterSetter;
    }


    public static SOSTargetGetterSetter SosTargetXMLHandler(XmlPullParser xpp,
                                                            int eventType) {
        SOSTargetGetterSetter failureGetterSetter = new SOSTargetGetterSetter();

        try {
            while (xpp.getEventType() != XmlPullParser.END_DOCUMENT) {
                if (xpp.getEventType() == XmlPullParser.START_TAG) {

                    if (xpp.getName().equals("META_DATA")) {
                        failureGetterSetter.setMeta_data(xpp.nextText());
                    }

                    if (xpp.getName().equals("STORE_ID")) {
                        failureGetterSetter.setStore_id(xpp.nextText());
                    }

                    if (xpp.getName().equals("CATEGORY_ID")) {
                        failureGetterSetter.setCategory_id(xpp.nextText());
                    }

                    if (xpp.getName().equals("PROCESS_ID")) {
                        failureGetterSetter.setProcess_id(xpp.nextText());
                    }

                    if (xpp.getName().equals("TARGET")) {
                        failureGetterSetter.setTarget(xpp.nextText());
                    }

                }
                xpp.next();
            }
        } catch (XmlPullParserException e) {

            e.printStackTrace();
        } catch (IOException e) {

            e.printStackTrace();
        }
        return failureGetterSetter;
    }


    public static CompetitionGetterSetter CompetitionXMLHandler(XmlPullParser xpp,
                                                                int eventType) {
        CompetitionGetterSetter failureGetterSetter = new CompetitionGetterSetter();

        try {
            while (xpp.getEventType() != XmlPullParser.END_DOCUMENT) {
                if (xpp.getEventType() == XmlPullParser.START_TAG) {

                    if (xpp.getName().equals("META_DATA")) {
                        failureGetterSetter.setMeta_data(xpp.nextText());
                    }

                    if (xpp.getName().equals("COMPANY_ID")) {
                        failureGetterSetter.setCompany_id(xpp.nextText());
                    }

                    if (xpp.getName().equals("COMPANY")) {
                        failureGetterSetter.setCompany(xpp.nextText());
                    }

                    if (xpp.getName().equals("ISCOMPETITOR")) {
                        failureGetterSetter.setIsCompetitor(xpp.nextText());
                    }


                }
                xpp.next();
            }
        } catch (XmlPullParserException e) {

            e.printStackTrace();
        } catch (IOException e) {

            e.printStackTrace();
        }
        return failureGetterSetter;
    }


    public static JCPGetterSetter GeoTagXMLHandler(XmlPullParser xpp,
                                                   int eventType) {
        JCPGetterSetter jcpGetterSetter = new JCPGetterSetter();

        try {
            while (xpp.getEventType() != XmlPullParser.END_DOCUMENT) {
                if (xpp.getEventType() == XmlPullParser.START_TAG) {

                    if (xpp.getName().equals("META_DATA")) {
                        jcpGetterSetter.setGeo_meta_data(xpp.nextText());
                    }

                    if (xpp.getName().equals("STORE_ID")) {
                        jcpGetterSetter.setSTORE_ID(xpp.nextText());
                    }
                    if (xpp.getName().equals("STORENAME")) {
                        jcpGetterSetter.setSTORE(xpp.nextText());
                    }
                    if (xpp.getName().equals("KEYACCOUNT")) {
                        jcpGetterSetter.setKEY_ACCOUNT(xpp.nextText());
                    }
                    if (xpp.getName().equals("STORETYPE")) {
                        jcpGetterSetter.setSTORE_TYPE(xpp.nextText());
                    }
                    if (xpp.getName().equals("CITY")) {
                        jcpGetterSetter.setCITY(xpp.nextText());
                    }
                    if (xpp.getName().equals("GEO_TAG_STATUS")) {
                        jcpGetterSetter.setGEO_TAG_STATUS(xpp.nextText());
                    }

                }
                xpp.next();
            }
        } catch (XmlPullParserException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return jcpGetterSetter;
    }

    public static PdrFacingStockGetterSetter MappingPdrFacingStockXMLHandler(XmlPullParser xpp,
                                                                             int eventType) {
        PdrFacingStockGetterSetter failureGetterSetter = new PdrFacingStockGetterSetter();

        try {
            while (xpp.getEventType() != XmlPullParser.END_DOCUMENT) {
                if (xpp.getEventType() == XmlPullParser.START_TAG) {

                    if (xpp.getName().equals("META_DATA")) {
                        failureGetterSetter.setPdrFacingStock(xpp.nextText());
                    }

                    if (xpp.getName().equals("PROCESS_ID")) {
                        failureGetterSetter.setProcess_id(xpp.nextText());

                    }

                    if (xpp.getName().equals("STATE_ID")) {
                        failureGetterSetter.setSTATE_ID(xpp.nextText());
                    }

                    if (xpp.getName().equals("KEY_ID")) {
                        failureGetterSetter.setKEY_ID(xpp.nextText());
                    }

                    if (xpp.getName().equals("STORETYPE_ID")) {
                        failureGetterSetter.setSTORETYPE_ID(xpp.nextText());
                    }

                    if (xpp.getName().equals("BRAND_ID")) {
                        failureGetterSetter.setBRAND_ID(xpp.nextText());
                    }

                    if (xpp.getName().equals("FACING_TARGET")) {
                        failureGetterSetter.setFACING_TARGET(xpp.nextText());
                    }

                }
                xpp.next();
            }
        } catch (XmlPullParserException e) {

            e.printStackTrace();
        } catch (IOException e) {

            e.printStackTrace();
        }
        return failureGetterSetter;
    }


    public static NonWorkingAttendenceGetterSetter MarchAttendenceXMLHandler(XmlPullParser xpp,
                                                                             int eventType) {
        NonWorkingAttendenceGetterSetter failureGetterSetter = new NonWorkingAttendenceGetterSetter();

        try {
            while (xpp.getEventType() != XmlPullParser.END_DOCUMENT) {
                if (xpp.getEventType() == XmlPullParser.START_TAG) {

                    if (xpp.getName().equals("META_DATA")) {
                        failureGetterSetter.setMetaDATA(xpp.nextText());
                    }

                    if (xpp.getName().equals("REASON_ID")) {
                        failureGetterSetter.setREASON_ID(xpp.nextText());

                    }

                    if (xpp.getName().equals("REASON")) {
                        failureGetterSetter.setREASON(xpp.nextText());
                    }

                    if (xpp.getName().equals("ENTRY_ALLOW")) {
                        failureGetterSetter.setENTRY_ALLOW(xpp.nextText());
                    }

                }
                xpp.next();
            }
        } catch (XmlPullParserException e) {

            e.printStackTrace();
        } catch (IOException e) {

            e.printStackTrace();
        }
        return failureGetterSetter;
    }

    public static AttendenceStatusGetterSetter AttendenceStatusXMLHandler(XmlPullParser xpp,
                                                                          int eventType) {
        AttendenceStatusGetterSetter lgs = new AttendenceStatusGetterSetter();

        try {
            while (xpp.getEventType() != XmlPullParser.END_DOCUMENT) {
                if (xpp.getEventType() == XmlPullParser.START_TAG) {

                    if (xpp.getName().equals("ATT_STATUS")) {
                        lgs.setStaus(xpp.nextText());
                    }
                }
                xpp.next();
            }
        } catch (XmlPullParserException e) {

            e.printStackTrace();
        } catch (IOException e) {

            e.printStackTrace();
        }
        return lgs;
    }


    public static NoticeBoardGetterSetter noticeBoardXmlHandler(XmlPullParser xpp,
                                                                int eventType) {
        NoticeBoardGetterSetter lgs = new NoticeBoardGetterSetter();
        try {
            while (xpp.getEventType() != XmlPullParser.END_DOCUMENT) {
                if (xpp.getEventType() == XmlPullParser.START_TAG) {

                    if (xpp.getName().equals("META_DATA")) {
                        lgs.setNoticeBoardTable(xpp.nextText());
                    }
                    if (xpp.getName().equals("USER_ID")) {
                        lgs.setUser_Id(xpp.nextText());

                    }

                    if (xpp.getName().equals("PROJECT_CODE")) {
                        lgs.setProjectCode(xpp.nextText());
                    }

                    if (xpp.getName().equals("NOTICE_BOARD")) {
                        lgs.setNoticeBoard(xpp.nextText());
                    }
                    if (xpp.getName().equals("QUIZ_URL")) {
                        lgs.setQuizUrl(xpp.nextText());
                    }

                }
                xpp.next();
            }
        } catch (XmlPullParserException e) {

            e.printStackTrace();
        } catch (IOException e) {

            e.printStackTrace();
        }
        return lgs;
    }

    public static MappingCompetitionPromotionGetterSetter MappingCompetitionPromotionXMLHandler(XmlPullParser xpp,
                                                                                                int eventType) {
        MappingCompetitionPromotionGetterSetter failureGetterSetter = new MappingCompetitionPromotionGetterSetter();

        try {
            while (xpp.getEventType() != XmlPullParser.END_DOCUMENT) {
                if (xpp.getEventType() == XmlPullParser.START_TAG) {

                    if (xpp.getName().equals("META_DATA")) {
                        failureGetterSetter.setTable(xpp.nextText());
                    }

                    if (xpp.getName().equals("SKU_ID")) {
                        failureGetterSetter.setSKU_ID(xpp.nextText());

                    }

                    if (xpp.getName().equals("SKU")) {
                        failureGetterSetter.setSKU(xpp.nextText());
                    }

                    if (xpp.getName().equals("BRAND_ID")) {
                        failureGetterSetter.setBRAND_ID(xpp.nextText());
                    }

                    if (xpp.getName().equals("BRAND")) {
                        failureGetterSetter.setBRAND(xpp.nextText());
                    }

                    if (xpp.getName().equals("CATEGORY_ID")) {
                        failureGetterSetter.setCATEGORY_ID(xpp.nextText());
                    }

                    if (xpp.getName().equals("CATEGORY")) {
                        failureGetterSetter.setCATEGORY(xpp.nextText());
                    }
                    if (xpp.getName().equals("COMPANY_ID")) {
                        failureGetterSetter.setCOMPANY_ID(xpp.nextText());
                    }
                    if (xpp.getName().equals("MRP")) {
                        failureGetterSetter.setMRP(xpp.nextText());
                    }
                    if (xpp.getName().equals("COMP_SEGMENT_ID")) {
                        failureGetterSetter.setSegment_Id(xpp.nextText());
                    }
                    if (xpp.getName().equals("COMP_SEGMENT")) {
                        failureGetterSetter.setSegment(xpp.nextText());
                    }

                }
                xpp.next();
            }
        } catch (XmlPullParserException e) {

            e.printStackTrace();
        } catch (IOException e) {

            e.printStackTrace();
        }
        return failureGetterSetter;
    }

}
