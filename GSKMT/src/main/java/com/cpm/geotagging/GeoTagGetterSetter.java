package com.cpm.geotagging;

import java.util.ArrayList;

public class GeoTagGetterSetter {

	ArrayList<String> storeid = new ArrayList<String>();
	ArrayList<String> storename = new ArrayList<String>();
	
	ArrayList<String> address = new ArrayList<String>();
	ArrayList<String> cityid = new ArrayList<String>();
	
	ArrayList<String> storetype = new ArrayList<String>();
	ArrayList<String> latitude = new ArrayList<String>();
	
	ArrayList<String> longitude = new ArrayList<String>();
	ArrayList<String> status = new ArrayList<String>();
	
	
	public ArrayList<String> getStoreid() {
		return storeid;
	}
	public void setStoreid(String storeid) {
		this.storeid.add(storeid);
	}
	public ArrayList<String> getStorename() {
		return storename;
	}
	public void setStorename(String storename) {
		this.storename.add(storename);
	}
	public ArrayList<String> getAddress() {
		return address;
	}
	public void setAddress(String address) {
		this.address.add(address);
	}
	public ArrayList<String> getCityid() {
		return cityid;
	}
	public void setCityid(String cityid) {
		this.cityid.add(cityid);
	}
	public ArrayList<String> getStoretype() {
		return storetype;
	}
	public void setStoretype(String storetype) {
		this.storetype.add(storetype);
	}
	public ArrayList<String> getLatitude() {
		return latitude;
	}
	public void setLatitude(String latitude) {
		this.latitude.add(latitude);
	}
	public ArrayList<String> getLongitude() {
		return longitude;
	}
	public void setLongitude(String longitude) {
		this.longitude.add(longitude);
	}
	public ArrayList<String> getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status.add(status);
	}


	
	
	
	
}
