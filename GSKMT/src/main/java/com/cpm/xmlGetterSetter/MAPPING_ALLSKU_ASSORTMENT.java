package com.cpm.xmlGetterSetter;

import java.util.ArrayList;

public class MAPPING_ALLSKU_ASSORTMENT {
    String meta_table;
    String daily_storeFootfall="";
    String shoperContact="";

    public String getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(String categoryId) {
        this.categoryId = categoryId;
    }

    String categoryId="";

    public String getDaily_storeFootfall() {
        return daily_storeFootfall;
    }

    public void setDaily_storeFootfall(String daily_storeFootfall) {
        this.daily_storeFootfall = daily_storeFootfall;
    }

    public String getShoperContact() {
        return shoperContact;
    }

    public void setShoperContact(String shoperContact) {
        this.shoperContact = shoperContact;
    }

    public String getSales_conversion() {
        return sales_conversion;
    }

    public void setSales_conversion(String sales_conversion) {
        this.sales_conversion = sales_conversion;
    }

    String sales_conversion="";
    ArrayList<String>skuId=new ArrayList<>();

    public String getMeta_table() {
        return meta_table;
    }

    public void setMeta_table(String meta_table) {
        this.meta_table = meta_table;
    }

    public ArrayList<String> getSkuId() {
        return skuId;
    }

    public void setSkuId(String skuId) {
        this.skuId.add(skuId);
    }
}
