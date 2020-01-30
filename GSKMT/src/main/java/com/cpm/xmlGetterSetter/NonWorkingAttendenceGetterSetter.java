package com.cpm.xmlGetterSetter;

import com.cpm.Constants.CommonString;

import java.util.ArrayList;

/**
 * Created by jeevanp on 4/12/2018.
 */

public class NonWorkingAttendenceGetterSetter {
    String  metaDATA;
    ArrayList<String>REASON_ID=new ArrayList<>();
    ArrayList<String>REASON=new ArrayList<>();
    public String getStatus() {
        return status;
    }
    public void setStatus(String status) {
        this.status = status;
    }
    String  status="";
    public String getImage() {
        return image;
    }
    public void setImage(String image) {
        this.image = image;
    }
    String image="";
    public String getMetaDATA() {
        return metaDATA;
    }
    public void setMetaDATA(String metaDATA) {
        this.metaDATA = metaDATA;
    }
    public ArrayList<String> getREASON_ID() {
        return REASON_ID;
    }
    public void setREASON_ID(String REASON_ID) {
        this.REASON_ID.add(REASON_ID);
    }

    public ArrayList<String> getREASON() {
        return REASON;
    }

    public void setREASON(String REASON) {
        this.REASON.add(REASON);
    }

    public ArrayList<String> getENTRY_ALLOW() {
        return ENTRY_ALLOW;
    }

    public void setENTRY_ALLOW(String ENTRY_ALLOW) {
        this.ENTRY_ALLOW.add(ENTRY_ALLOW);
    }

    ArrayList<String>ENTRY_ALLOW=new ArrayList<>();
}
