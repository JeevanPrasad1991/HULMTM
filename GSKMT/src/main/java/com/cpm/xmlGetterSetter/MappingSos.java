package com.cpm.xmlGetterSetter;

import java.util.ArrayList;

public class MappingSos {
    String mappingsostable;

    public String getMappingsostable() {
        return mappingsostable;
    }

    public void setMappingsostable(String mappingsostable) {
        this.mappingsostable = mappingsostable;
    }

    public ArrayList<String> getProcessId() {
        return processId;
    }

    public void setProcessId(String processId) {
        this.processId.add(processId);
    }

    public ArrayList<String> getRegionId() {
        return regionId;
    }

    public void setRegionId(String regionId) {
        this.regionId.add(regionId);
    }

    public ArrayList<String> getStoretypeId() {
        return storetypeId;
    }

    public void setStoretypeId(String storetypeId) {
        this.storetypeId.add(storetypeId);
    }

    public ArrayList<String> getKeyAcId() {
        return keyAcId;
    }

    public void setKeyAcId(String keyAcId) {
        this.keyAcId.add(keyAcId);
    }

    public ArrayList<String> getSubcategoryId() {
        return subcategoryId;
    }

    public void setSubcategoryId(String subcategoryId) {
        this.subcategoryId.add(subcategoryId);
    }

    public ArrayList<String> getLinearMeasurement() {
        return linearMeasurement;
    }

    public void setLinearMeasurement(String linearMeasurement) {
        this.linearMeasurement.add(linearMeasurement);
    }

    ArrayList<String>processId=new ArrayList<>();
    ArrayList<String>regionId=new ArrayList<>();
    ArrayList<String>storetypeId=new ArrayList<>();
    ArrayList<String>keyAcId=new ArrayList<>();
    ArrayList<String>subcategoryId=new ArrayList<>();
    ArrayList<String>linearMeasurement=new ArrayList<>();
}
