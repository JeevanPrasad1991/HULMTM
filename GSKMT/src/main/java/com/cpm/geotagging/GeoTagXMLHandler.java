package com.cpm.geotagging;

import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;



public class GeoTagXMLHandler extends DefaultHandler{

	private String elementValue = null;
	private GeoTagGetterSetter geoGetterSetter = null;
	
	public GeoTagGetterSetter getGeoTagGetterSetter()
	{
		return geoGetterSetter;
	}
	
	@Override
	public void startDocument() throws SAXException {
		// TODO Auto-generated method stub
		super.startDocument();
		
		geoGetterSetter= new GeoTagGetterSetter();
	}

	@Override
	public void characters(char[] ch, int start, int length)
			throws SAXException {
		// TODO Auto-generated method stub
		super.characters(ch, start, length);
		
		elementValue = new String(ch, start, length);
	}

	@Override
	public void endElement(String uri, String localName, String qName)
			throws SAXException {
		// TODO Auto-generated method stub
		super.endElement(uri, localName, qName);
		
		if(qName.equals("STORE_ID"))
		{
			geoGetterSetter.setStoreid(elementValue);
		}
		else if(qName.equals("STORE_NAME"))
		{
			geoGetterSetter.setStorename(elementValue);
		}
		else if(qName.equals("ADDRESS"))
		{
			geoGetterSetter.setAddress(elementValue);
		}
		else if(qName.equals("CITY_ID"))
		{
			geoGetterSetter.setCityid(elementValue);
		}
		else if(qName.equals("STORETYPE_ID"))
		{
			geoGetterSetter.setStoretype(elementValue);
		}
		else if(qName.equals("LOGITUDE"))
		{
			geoGetterSetter.setLongitude(elementValue);
		}
		else if(qName.equals("LATITUDE"))
		{
			geoGetterSetter.setLatitude(elementValue);
		}
		else if(qName.equals("STATUS"))
		{
			geoGetterSetter.setStatus(elementValue);
		}
		
	}
}
