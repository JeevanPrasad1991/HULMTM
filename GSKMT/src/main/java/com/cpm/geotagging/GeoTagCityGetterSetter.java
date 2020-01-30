package com.cpm.geotagging;

import java.util.ArrayList;

public class GeoTagCityGetterSetter {

	ArrayList<String> city = new ArrayList<String>();
	ArrayList<String> cityid = new ArrayList<String>();
	
	public ArrayList<String> getCity() {
		return city;
	}
	public void setCity(String city) {
		this.city.add(city);
	}
	public ArrayList<String> getCityid() {
		return cityid;
	}
	public void setCityid(String cityid) {
		this.cityid.add(cityid);
	}
	
	
	
	
}
