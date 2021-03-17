package com.cpm.xmlGetterSetter;

import java.util.ArrayList;

public class Targetsossubcategorywise {
    String table;
    ArrayList<String>processid=new ArrayList<>();
    ArrayList<String>storeid=new ArrayList<>();
    ArrayList<String>subcategoryId=new ArrayList<>();

    public String getTable() {
        return table;
    }

    public void setTable(String table) {
        this.table = table;
    }

    public ArrayList<String> getProcessid() {
        return processid;
    }

    public void setProcessid(String processid) {
        this.processid.add(processid);
    }

    public ArrayList<String> getStoreid() {
        return storeid;
    }

    public void setStoreid(String storeid) {
        this.storeid.add(storeid);
    }

    public ArrayList<String> getSubcategoryId() {
        return subcategoryId;
    }

    public void setSubcategoryId(String subcategoryId) {
        this.subcategoryId.add(subcategoryId);
    }

    public ArrayList<String> getTarget() {
        return Target;
    }

    public void setTarget(String target) {
        Target.add(target);
    }

    ArrayList<String>Target=new ArrayList<>();
}
