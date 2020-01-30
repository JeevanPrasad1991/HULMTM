package com.cpm.xmlGetterSetter;

import java.util.ArrayList;

/**
 * Created by jeevanp on 4/3/2018.
 */

public class PdrFacingStockGetterSetter {
    ArrayList<String> process_id = new ArrayList<>();
    ArrayList<String> STATE_ID = new ArrayList<>();
    ArrayList<String> STORETYPE_ID = new ArrayList<>();
    ArrayList<String> BRAND_ID = new ArrayList<>();
    ArrayList<String> FACING_TARGET = new ArrayList<>();

    public ArrayList<String> getKEY_ID() {
        return KEY_ID;
    }

    public void setKEY_ID(String KEY_ID) {
        this.KEY_ID.add(KEY_ID);
    }

    ArrayList<String> KEY_ID = new ArrayList<>();
    String PdrFacingStock;

    public ArrayList<String> getSTATE_ID() { return STATE_ID; }

    public void setSTATE_ID(String STATE_ID) { this.STATE_ID.add(STATE_ID); }

    public ArrayList<String> getProcess_id() {
        return process_id;
    }

    public void setProcess_id(String process_id) {
        this.process_id.add(process_id);
    }

    public ArrayList<String> getSTORETYPE_ID() {
        return STORETYPE_ID;
    }

    public void setSTORETYPE_ID(String STORETYPE_ID) {
        this.STORETYPE_ID.add(STORETYPE_ID);
    }

    public ArrayList<String> getBRAND_ID() {
        return BRAND_ID;
    }

    public void setBRAND_ID(String BRAND_ID) {
        this.BRAND_ID.add(BRAND_ID);
    }

    public ArrayList<String> getFACING_TARGET() {
        return FACING_TARGET;
    }

    public void setFACING_TARGET(String FACING_TARGET) {
        this.FACING_TARGET.add(FACING_TARGET);
    }

    public String getPdrFacingStock() {
        return PdrFacingStock;
    }

    public void setPdrFacingStock(String pdrFacingStock) {
        PdrFacingStock = pdrFacingStock;
    }
}
