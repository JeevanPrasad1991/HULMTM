package com.cpm.xmlGetterSetter;

import java.util.ArrayList;

public class MappingCompetitionPromotionGetterSetter {
    String  table;
    ArrayList<String>SKU_ID=new ArrayList<>();
    ArrayList<String>SKU =new ArrayList<>();
    ArrayList<String>BRAND_ID =new ArrayList<>();
    ArrayList<String>BRAND =new ArrayList<>();
    ArrayList<String>CATEGORY_ID =new ArrayList<>();
    ArrayList<String>CATEGORY =new ArrayList<>();
    ArrayList<String>COMPANY_ID =new ArrayList<>();

    public String getTable() {
        return table;
    }

    public void setTable(String table) {
        this.table = table;
    }

    public ArrayList<String> getSKU_ID() {
        return SKU_ID;
    }

    public void setSKU_ID(String SKU_ID) {
        this.SKU_ID.add(SKU_ID);
    }

    public ArrayList<String> getSKU() {
        return SKU;
    }

    public void setSKU(String SKU) {
        this.SKU.add(SKU);
    }

    public ArrayList<String> getBRAND_ID() {
        return BRAND_ID;
    }

    public void setBRAND_ID(String BRAND_ID) {
        this.BRAND_ID.add(BRAND_ID);
    }

    public ArrayList<String> getBRAND() {
        return BRAND;
    }

    public void setBRAND(String BRAND) {
        this.BRAND.add(BRAND);
    }

    public ArrayList<String> getCATEGORY_ID() {
        return CATEGORY_ID;
    }

    public void setCATEGORY_ID(String CATEGORY_ID) {
        this.CATEGORY_ID.add(CATEGORY_ID);
    }

    public ArrayList<String> getCATEGORY() {
        return CATEGORY;
    }

    public void setCATEGORY(String CATEGORY) {
        this.CATEGORY.add(CATEGORY);
    }

    public ArrayList<String> getCOMPANY_ID() {
        return COMPANY_ID;
    }

    public void setCOMPANY_ID(String COMPANY_ID) {
        this.COMPANY_ID.add(COMPANY_ID);
    }

    public ArrayList<String> getMRP() {
        return MRP;
    }

    public void setMRP(String MRP) {
        this.MRP.add(MRP);
    }

    ArrayList<String>MRP =new ArrayList<>();
    ArrayList<String>segment_Id =new ArrayList<>();

    public ArrayList<String> getSegment_Id() {
        return segment_Id;
    }

    public void setSegment_Id(String segment_Id) {
        this.segment_Id.add(segment_Id);
    }

    public ArrayList<String> getSegment() {
        return segment;
    }

    public void setSegment(String segment) {
        this.segment.add(segment);
    }

    ArrayList<String>segment =new ArrayList<>();




}
