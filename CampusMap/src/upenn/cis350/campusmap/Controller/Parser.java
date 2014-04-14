package upenn.cis350.campusmap.Controller;
import java.io.File;
import java.util.HashMap;
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
	private HashSet<Building> buildings;
	private HashMap<String, Building> nameMap;
	private HashMap<String, Building> codeMap;
	private HashMap<String, Building> nicknameMap;
	private String fileLocation;
	
	public Parser (String fileLoc) {
		this.fileLocation = fileLoc;
		buildings = new HashSet <Building>();
	}
	
	public void Parse() {

		try {

			//File file = new File("C:/Users/Max/git/CampusMaps/buildings.xml");
			File file = new File(this.fileLocation);

			DocumentBuilder dBuilder = DocumentBuilderFactory.newInstance()
					.newDocumentBuilder();

			Document doc = dBuilder.parse(file);

			// normalize text representation
			doc.getDocumentElement().normalize();
			//System.out.println ("Root element of the doc is " + doc.getDocumentElement().getNodeName());
			
			// gets a list of all the buildings in the xml
			NodeList listOfBuildings = doc.getElementsByTagName("location");
			int totalBuildings = listOfBuildings.getLength();
			//System.out.println("Total no of buildings : " + totalBuildings);

			for(int i=0; i < totalBuildings ; i++) {

				Node firstBuildingNode = listOfBuildings.item(i);
				if(firstBuildingNode.getNodeType() == Node.ELEMENT_NODE) {
					
					// gets the first element, aka the entire location node
					Element firstElement = (Element)firstBuildingNode;                              

					//-------
					NodeList firstNameList = firstElement.getElementsByTagName("name");
					String name = getElement(firstNameList);
					//System.out.println("name : " + ((Node)textFNList.item(0)).getNodeValue().trim());

					NodeList firstAddressList = firstElement.getElementsByTagName("address");
					
					String address = getElement(firstAddressList);
					
					NodeList latList = firstElement.getElementsByTagName("lat");
					double lat = 0.0;
					if (getElement(latList) != null) lat = Double.parseDouble(getElement(latList));
					
					NodeList lngList = firstElement.getElementsByTagName("lng");
					double lng = 0.0;
					if (getElement(lngList) != null) lng = Double.parseDouble(getElement(lngList));

					
					NodeList firstCodeList = firstElement.getElementsByTagName("code");
					String id = getElement(firstCodeList);
					
					NodeList firstDescriptionList = firstElement.getElementsByTagName("description");
					String des = getElement(firstDescriptionList);
					
					NodeList firstNickNamesList = firstElement.getElementsByTagName("nicknames");
					String nicknames = getElement(firstNickNamesList);
					
					
					NodeList firstAccess = firstElement.getElementsByTagName("pennaccessLink");
					String icon = getElement(firstAccess);
					
					NodeList firstHoursReg = firstElement.getElementsByTagName("hours_regular");
					//if (name.contains("Pennsylvania Bookstore")) System.out.println(firstHoursReg.getLength());
					String hours = getHours(firstHoursReg);
					
					NodeList weekHours = firstElement.getElementsByTagName("hours_weekend");
					String weekendHours = getHours(weekHours);
					
					// builds the building set
					Building b = new Building (lng, lat, id, icon, name, address);
					b.addNicknames(nicknames);
					b.setDescription(des);
					b.setRegHours(hours);
					b.setWeekendHours(weekendHours);
					buildings.add(b);
					
					
					
					if (i == 0) {
						//b.printBuilding();
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
	
	// uses getElement to open each hours segment
	private String getHours (NodeList x) {
		if (x == null) return null;
		if (x.item(0) == null) return null;
		Element firstEl = (Element)x.item(0);
		NodeList t = firstEl.getElementsByTagName("open");
		if (t.getLength() == 0) return null;
		String ret = "";
		ret += getElement(t);
		NodeList t1 = firstEl.getElementsByTagName("close");
		if (t1.getLength() == 0) return null;
		ret += getElement(t1);
		return ret;
	}
	
	// returns a single string value of a node segment in the location element
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
	
	// singleton for the name-building map
	public HashMap<String, Building> getNameMap (){
		if (this.nameMap != null) return this.nameMap;
		else {
			this.nameMap = new HashMap <String, Building>();
			for (Building temp : this.buildings) {
				this.nameMap.put(temp.getName(), temp);
			}
			return this.nameMap;
		}
	}
	
	// singleton for the code to building map
	public HashMap<String, Building> getCodeMap (){
		if (this.codeMap != null) return this.codeMap;
		else {
			this.codeMap = new HashMap <String, Building>();
			for (Building temp : this.buildings) {
				this.codeMap.put(temp.getGooglePlaceID(), temp);
			}
			return this.codeMap;
		}
	}
	
	// singleton for the nickname to building map
	public HashMap<String, Building> getNicknameMap (){
		if (this.nicknameMap != null) return this.nicknameMap;
		else {
			this.nicknameMap = new HashMap <String, Building>();
			for (Building temp : this.buildings) {
				for (String x : temp.getNicknames()) this.nicknameMap.put(x, temp);
			}
			return this.nicknameMap;
		}
	}
	

}