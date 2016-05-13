package edu.msu.cse.patelke6.MyCar;

import android.content.Context;
import android.util.Log;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import java.io.File;
import java.util.ArrayList;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

/**
 * Created by keyurpatel on 3/22/16.
 */
public class CarData {

    private Document doc;
    private File carDataXML;
    private Context mContext;

    CarData(String xmlFilePath, Context context){
        carDataXML = new File(xmlFilePath);
        mContext = context;
        getCarDataXML();

    }


    public void getCarDataXML(){

        try {
            DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
            doc = docBuilder.parse(carDataXML);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public int getMaxSpeed(String bssid){
        Node maxSpeedNode = getSettingNodeByType(getUserNode(bssid),XMLNodeNames.MaxSpeed);
        if(maxSpeedNode == null)
            return 0;
        return Integer.parseInt(maxSpeedNode.getTextContent());
    }

    public Boolean setMaxSpeed(String bssid, int maxSpeed){
        Node maxSpeedNode = getSettingNodeByType(getUserNode(bssid),XMLNodeNames.MaxSpeed);
        if(maxSpeedNode == null)
            return false;
        else {
            maxSpeedNode.setTextContent(Integer.toString(maxSpeed));
            return true;
        }
    }

    public Boolean getEnforceSeatBelt(String bssid){
        getCarDataXML(); //update the document in case it was updated

        Node enforceSeatBeltNode = getSettingNodeByType(getUserNode(bssid),XMLNodeNames.EnforceSeatBelt);
        if(enforceSeatBeltNode == null)
            return false;
        else if (enforceSeatBeltNode.getTextContent().equals("0")) {
            return false;
        }
        else {
            return true;
        }
    }


    public Boolean setEnforceSeatBelt(String bssid, String value){
        Node enforceSeatBeltNode = getSettingNodeByType(getUserNode(bssid),XMLNodeNames.EnforceSeatBelt);
        if(enforceSeatBeltNode == null)
            return false;
        else {
            enforceSeatBeltNode.setTextContent(value);
            return true;
        }

    }


    public int getLowerSeatPosition(String bssid){
        Node lowerSeatPosition = getSettingNodeByType(getUserNode(bssid),XMLNodeNames.SeatPositionX);
        if(lowerSeatPosition == null)
            return 50;
        else
            return Integer.parseInt(lowerSeatPosition.getTextContent());
    }


    public void setLowerSeatPosition(String bssid){

        Node userNode =getSettingNodeByType(getUserNode(bssid), XMLNodeNames.SeatPositionX);
        userNode.setTextContent(getSettingNodeByType(getCarNode(),XMLNodeNames.SeatPositionX).getTextContent());

    }


    public String getRadioStations(String bssid){
        Node userNode = getSettingNodeByType(getUserNode(bssid), XMLNodeNames.RadioStations);
        if(userNode == null)
                return  null;
        else
            return userNode.getTextContent();
    }


    private Node getSettingNodeByType(Node node, String typeStr) {

        NodeList childrenNode = node.getChildNodes();
        Node n ;
        NodeList cNL = null;
        for (int i = 0; i < childrenNode.getLength(); i++) {
            n = childrenNode.item(i);
            if (n.hasChildNodes()) {
                if (n.getNodeName().equals(XMLNodeNames.Settings)) {
                    cNL = n.getChildNodes();
                    for (int b = 0; b < cNL.getLength(); b++) {
                        if (cNL.item(b).hasAttributes()) {
                            Attr attr = (Attr) cNL.item(b).getAttributes().getNamedItem(XMLNodeNames.TypeAttribute);
                            if (attr.getValue().equals(typeStr)) {
                                return cNL.item(b);
                            }
                        }

                    }
                }

            }
        }
        return null;
    }


    private Node getChildNodeByTag(String bssid, String tagName){
        Node node = getUserNode(bssid);
        NodeList cNL = null;
        if (node.hasChildNodes()) {
            cNL = node.getChildNodes();
            for (int b = 0; b < cNL.getLength(); b++) {
                if (cNL.item(b).getNodeName().equals(tagName)) {
                    return cNL.item(b);
                }
            }

        }
        return null;
    }


    private Node getChildNodeByTag(Node  node, String tagName){
        NodeList cNL = null;
        if (node.hasChildNodes()) {
            cNL = node.getChildNodes();
            for (int b = 0; b < cNL.getLength(); b++) {
                if (cNL.item(b).getNodeName().equals(tagName)) {
                    return cNL.item(b);
                }
            }

        }
        return null;
    }


    public void updateXMLFile(){
        try{
            TransformerFactory factory = TransformerFactory.newInstance();
            Transformer transformer = factory.newTransformer();
            DOMSource source = new DOMSource(doc);
            StreamResult result = new StreamResult(carDataXML);
            transformer.transform(source, result);
        }
        catch (Exception ex){
            Log.e("UpdateXML Error: ", "" + ex.toString());
        }
    }

    public Node getCarNode(){
       return doc.getElementsByTagName(XMLNodeNames.CarNode).item(0);
    }

    private Node getUserNode(String userBssid){
        NodeList users = doc.getElementsByTagName(XMLNodeNames.UserNode);
        Node n = null;
        NodeList cNL = null;
        for (int i = 0; i < users.getLength(); i++) {
            n = users.item(i);
            if (n.hasChildNodes()) {
                cNL = n.getChildNodes();
                for (int b = 0; b < cNL.getLength(); b++) {
                    if (cNL.item(b).getNodeName().equals(XMLNodeNames.BSSID)) {
                        if (cNL.item(b).getTextContent().equals(userBssid)) {
                            return n;
                        }

                    }
                }

            }
        }

        return null;
    }

    public boolean doesUserExist(String bssid){
        Node node = getUserNode(bssid);
        if(node == null)
            return false;
        else
            return true;
    }


    public void setAuthenticatedUserID(String userID){
        Node node = doc.getElementsByTagName(XMLNodeNames.UserAuthenticated).item(0);
        node.setTextContent(userID);
        updateXMLFile();
    }

    public boolean verifyCarKey(String key){
        Node node = doc.getElementsByTagName(XMLNodeNames.Key).item(0);
        if(node.getTextContent().equals(key))
            return true;
        return false;
    }
    public boolean verifyAdminCarKey(String key){
        Node node = doc.getElementsByTagName(XMLNodeNames.AdminKey).item(0);
        if(node.getTextContent().equals(key))
            return true;
        return false;
    }

    public boolean isUserAdmin(String  bssid){
        Node adminNode = getChildNodeByTag(bssid, XMLNodeNames.Admin);
        if(adminNode.getTextContent().equals("1"))
            return true;
        else
            return false;
    }

    public String getUserFirstName(String bssid){
        return getChildNodeByTag(bssid,XMLNodeNames.FirstName).getTextContent().toString();
    }

    public String getUserLastName(String bssid){
        return getChildNodeByTag(bssid,XMLNodeNames.LastName).getTextContent().toString();
    }

    public ArrayList<String> getEnrolledDrivers(){
        ArrayList<String> drivers = new ArrayList<>();
        NodeList users = doc.getElementsByTagName(XMLNodeNames.UserNode);

        Node n = null;
        NodeList cNL = null;
        for (int i = 0; i < users.getLength(); i++) {
            n = users.item(i);
            if (n.hasChildNodes()) {
                cNL = n.getChildNodes();
                for (int b = 0; b < cNL.getLength(); b++) {
                    if (cNL.item(b).getNodeName().equals(XMLNodeNames.BSSID)) {
                        drivers.add(cNL.item(b).getTextContent());

                    }
                }

            }
        }
        return drivers;
    }

    //Todo check if user was successfully enrolled, if not roll back changes
    public void enrollUser(String firstName, String lastName, String BSSID, Boolean isAdmin){
        Node newUserNode = getEmptyNode();

        getChildNodeByTag(newUserNode,XMLNodeNames.LastName).setTextContent(lastName);
        getChildNodeByTag(newUserNode,XMLNodeNames.FirstName).setTextContent(firstName);
        getChildNodeByTag(newUserNode,XMLNodeNames.BSSID).setTextContent(BSSID);
        if(isAdmin)
            getChildNodeByTag(newUserNode,XMLNodeNames.Admin).setTextContent("1");
        else
            getChildNodeByTag(newUserNode,XMLNodeNames.Admin).setTextContent("0");

        setEnforceSeatBelt(BSSID, "0");
        Node lowerSeatPositionNode =getSettingNodeByType(getUserNode(BSSID), XMLNodeNames.SeatPositionX);

        lowerSeatPositionNode.setTextContent(getSettingNodeByType(getCarNode(), XMLNodeNames.SeatPositionX).getTextContent());
        setMaxSpeed(BSSID, 1000);
        updateXMLFile();
    }

    public void deactivate(String bssid){
        Node node = getUserNode(bssid);
        setEnforceSeatBelt(bssid, "0");
        Node lowerSeatPositionNode =getSettingNodeByType(getUserNode(bssid), XMLNodeNames.SeatPositionX);
        lowerSeatPositionNode.setTextContent("");
        setMaxSpeed(bssid, 0);
        try {
            getChildNodeByTag(node, XMLNodeNames.LastName).setTextContent("");
            getChildNodeByTag(node, XMLNodeNames.FirstName).setTextContent("");
            getChildNodeByTag(node, XMLNodeNames.BSSID).setTextContent("");
            getChildNodeByTag(node, XMLNodeNames.Admin).setTextContent("0");
        }
        catch (Exception ex){
            Log.i("Deactivate User: ", "Deactivate User Failed " + ex.toString());
        }
        updateXMLFile();
    }


    private Node getEmptyNode(){
        NodeList users = doc.getElementsByTagName(XMLNodeNames.UserNode);
        Node n = null;
        NodeList cNL = null;

        for (int i = 0; i < users.getLength(); i++) {
            n = users.item(i);
            if (n.hasChildNodes()) {
                cNL = n.getChildNodes();
                for (int b = 0; b < cNL.getLength(); b++) {
                    if (cNL.item(b).getNodeName().equals(XMLNodeNames.BSSID)) {
                        if(cNL.item(b).getTextContent().toString().trim().equals(""))
                            return n;
                    }
                }
            }
        }
    return  null;
    }
}
