package com.cpm.geotagging;

import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;



public class GeoTagCityXMLHandler extends DefaultHandler{

	private String elementValue = null;
	private GeoTagCityGetterSetter geoCityGetterSetter = null;
	
	public GeoTagCityGetterSetter getGeoTagGetterSetter()
	{
		return geoCityGetterSetter;
	}
	
	@Override
	public void startDocument() throws SAXException {
		// TODO Auto-generated method stub
		super.startDocument();
		
		geoCityGetterSetter= new GeoTagCityGetterSetter();
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
		
		if(qName.equals("CITY_ID"))
		{
			geoCityGetterSetter.setCityid(elementValue);
		}
		else if(qName.equals("CITY_NAME"))
		{
			geoCityGetterSetter.setCity(elementValue);
		}
		
	}
}
