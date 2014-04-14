package upenn.cis350.campusmap.Controller;
import java.io.File;
import java.util.HashSet;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

public class Parser {

	public static void main(String[] args) {
		Parser p = new Parser();
		HashSet<Building> buildings = new HashSet <Building>();
		
		try {

			File file = new File("C:/Users/Max/git/CampusMaps/buildings.xml");

			DocumentBuilder dBuilder = DocumentBuilderFactory.newInstance()
					.newDocumentBuilder();

			Document doc = dBuilder.parse(file);

			// normalize text representation
			doc.getDocumentElement().normalize();
			System.out.println ("Root element of the doc is " + doc.getDocumentElement().getNodeName());

			NodeList listOfBuildings = doc.getElementsByTagName("location");
			int totalBuildings = listOfBuildings.getLength();
			System.out.println("Total no of buildings : " + totalBuildings);

			for(int i=0; i < listOfBuildings.getLength() ; i++) {

				Node firstBuildingNode = listOfBuildings.item(i);
				if(firstBuildingNode.getNodeType() == Node.ELEMENT_NODE) {

					Element firstElement = (Element)firstBuildingNode;                              

					//-------
					NodeList firstNameList = firstElement.getElementsByTagName("name");
					String name = p.getElement(firstNameList);
					//System.out.println("name : " + ((Node)textFNList.item(0)).getNodeValue().trim());

					NodeList firstAddressList = firstElement.getElementsByTagName("address");
					
					String address = p.getElement(firstAddressList);
					
					NodeList latList = firstElement.getElementsByTagName("lat");
					double lat = 0.0;
					if (p.getElement(latList) != null) lat = Double.parseDouble(p.getElement(latList));
					
					NodeList lngList = firstElement.getElementsByTagName("lng");
					double lng = 0.0;
					if (p.getElement(lngList) != null) lng = Double.parseDouble(p.getElement(lngList));

					
					NodeList firstCodeList = firstElement.getElementsByTagName("code");
					String id = p.getElement(firstCodeList);
					
					NodeList firstDescriptionList = firstElement.getElementsByTagName("description");
					String des = p.getElement(firstDescriptionList);
					
					NodeList firstNickNamesList = firstElement.getElementsByTagName("nicknames");
					String nicknames = p.getElement(firstNickNamesList);
					
					
					NodeList firstAccess = firstElement.getElementsByTagName("pennaccessLink");
					String icon = p.getElement(firstAccess);
					
					
					
					Building b = new Building (lng, lat, id, icon, name, address);
					b.addNicknames(nicknames);
					b.setDescription(des);
					buildings.add(b);
					
					
					if (i == 0) {
						b.printBuilding();
					}
				}
			}//end of for loop with s var
		} catch (SAXParseException err) {
			System.out.println ("** Parsing error" + ", line " + err.getLineNumber () + ", uri " + err.getSystemId ());
			System.out.println(" " + err.getMessage ());
		} catch (SAXException e) {
			Exception x = e.getException ();
			((x == null) ? e : x).printStackTrace ();
		} catch (Throwable t) {
			t.printStackTrace ();
		}                



	}
	

	private String getElement(NodeList x) {
		if (x == null) return null;
		if (x.item(0) == null) return null;
		Element firstEl = (Element)x.item(0);
		NodeList t = firstEl.getChildNodes();
		String ret = "";
		if (t == null) return null;
		if (t.item(0) != null) {
			ret = ((Node)t.item(0)).getNodeValue();
			if (ret != null) ret = ret.trim();
			return ret;
		}
		
		return null;
	}

}