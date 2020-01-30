package com.cpm.xmlGetterSetter;

import java.util.ArrayList;

public class TargetToothpestforOHCGetterSetter {
    ArrayList<String>store_Id=new ArrayList<>();
    ArrayList<String>process_Id=new ArrayList<>();
    ArrayList<String>category_Id=new ArrayList<>();

    public String getTableMetaData() {
        return tableMetaData;
    }

    public void setTableMetaData(String tableMetaData) {
        this.tableMetaData = tableMetaData;
    }

    String tableMetaData;

    public ArrayList<String> getStore_Id() {
        return store_Id;
    }

    public void setStore_Id(String store_Id) {
        this.store_Id.add(store_Id);
    }

    public ArrayList<String> getProcess_Id() {
        return process_Id;
    }

    public void setProcess_Id(String process_Id) {
        this.process_Id.add(process_Id);
    }

    public ArrayList<String> getCategory_Id() {
        return category_Id;
    }

    public void setCategory_Id(String category_Id) {
        this.category_Id.add(category_Id);
    }

    public ArrayList<String> getBrand_group_Id() {
        return brand_group_Id;
    }

    public void setBrand_group_Id(String brand_group_Id) {
        this.brand_group_Id.add(brand_group_Id);
    }

    public ArrayList<String> getBrand_group() {
        return brand_group;
    }

    public void setBrand_group(String brand_group) {
        this.brand_group.add(brand_group);
    }

    public ArrayList<String> getTarget() {
        return target;
    }

    public void setTarget(String target) {
        this.target.add(target);
    }

    ArrayList<String>brand_group_Id=new ArrayList<>();
    ArrayList<String>brand_group=new ArrayList<>();
    ArrayList<String>target=new ArrayList<>();

}

