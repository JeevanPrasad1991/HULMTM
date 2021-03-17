package com.cpm.xmlGetterSetter;

import java.util.ArrayList;

public class SubCategoryMaster {
    String subcat_table;
    ArrayList<String>subcategory=new ArrayList<>();
    ArrayList<String>subcategoryId=new ArrayList<>();
    ArrayList<String>categoryId=new ArrayList<>();

    public String getSubcat_table() {
        return subcat_table;
    }

    public void setSubcat_table(String subcat_table) {
        this.subcat_table = subcat_table;
    }

    public ArrayList<String> getSubcategory() {
        return subcategory;
    }

    public void setSubcategory(String subcategory) {
        this.subcategory.add(subcategory);
    }

    public ArrayList<String> getSubcategoryId() {
        return subcategoryId;
    }

    public void setSubcategoryId(String subcategoryId) {
        this.subcategoryId.add(subcategoryId);
    }

    public ArrayList<String> getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(String categoryId) {
        this.categoryId.add(categoryId);
    }

    public ArrayList<String> getSequencesubcategory() {
        return sequencesubcategory;
    }

    public void setSequencesubcategory(String sequencesubcategory) {
        this.sequencesubcategory.add(sequencesubcategory);
    }

    ArrayList<String>sequencesubcategory=new ArrayList<>();
}
