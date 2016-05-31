package com.kainat.app.android.util;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.commons.lang.StringEscapeUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

public class XMLUtils {
	private XMLUtils() {
	}

	public static final Document parseStringToXML(String xml) throws ParserConfigurationException, SAXException, IOException {
	
		try{
			 Class.forName("org.apache.commons.lang.StringEscapeUtils");
		xml = StringEscapeUtils.unescapeXml(xml);
		Document doc = null;
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		DocumentBuilder db = dbf.newDocumentBuilder();
		InputSource is = new InputSource();
		is.setCharacterStream(new StringReader(xml));
		doc = db.parse(is);
		return doc;
		}catch (Exception e) {
			ByteArrayInputStream arrayInputStream = new ByteArrayInputStream(
					xml.getBytes("UTF-8"));
			Document doc = XMLUtils.parseInputSourceToXML(new InputSource(
					arrayInputStream));
			return doc;
		}
	}

	public static final Document parseInputSourceToXML(InputSource xml) throws ParserConfigurationException, SAXException, IOException {
		Document doc = null;
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		DocumentBuilder db = dbf.newDocumentBuilder();
		doc = db.parse(xml);
		return doc;
	}

	public static final String getElementValue(Node elem) {
		Node kid;
		StringBuffer buffer = new StringBuffer();
		String sss="";
		if (elem != null) {
			if (elem.hasChildNodes()) {
				for (kid = elem.getFirstChild(); kid != null; kid = kid.getNextSibling()) {
					if (kid.getNodeType() == Node.TEXT_NODE) {
						sss= kid.getNodeValue();
						buffer.append(sss);
//					System.out.println("hhhhhhhhh====="+sss);
					}
				}
			}
		}
		return buffer.toString();
	}

	public static final String getValue(Element item, String str) {
		try{
		NodeList n = item.getElementsByTagName(str);
		//System.out.println("getNode==============================="+ n.getLength()+"str====="+str);
		return getElementValue(n.item(0)).trim();
		}catch (Exception e) {
			return null ;
		}
	}

	public static final ArrayList<String> getValues(Element item, String str) {
		ArrayList<String> values = new ArrayList<String>();
		NodeList n = item.getElementsByTagName(str);
		for (int i = 0; i < n.getLength(); i++) {
			values.add(getElementValue(n.item(i)));
		}
		return values;
	}
}