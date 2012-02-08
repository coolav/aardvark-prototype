/*
 * COPYRIGHT (c) 2009 by Institute of Computer Science, 
 * Foundation for Research and Technology - Hellas
 * Contact: 
 *      POBox 1385, Heraklio Crete, GR-700 13 GREECE
 *      Tel:+30-2810-391632
 *      Fax: +30-2810-391638
 *      E-mail: isl@ics.forth.gr
 *      http://www.ics.forth.gr/isl/cci.html
 * 
 * This file is part of RDF Label Converter between CIDOC CRM schema versions.
 *
 *  RDF Label Converter is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  RDF Label Converter is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with Foobar.  If not, see <http://www.gnu.org/licenses/>.
 */

package uib.rdfConverter;

import java.io.StringWriter;
import java.util.Iterator;
import java.util.Set;
import java.util.TreeMap;
import org.w3c.dom.*;
import javax.swing.*;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

/*-----------------------------------------------------
                  class Config
-------------------------------------------------------*/
public class Config {
    private XML_parser RDFParser;
    private XML_parser translationsXmlParser;
    private int changesRdf = 0;

    /*-----------------------------------------------------
                      Config()
    -------------------------------------------------------*/    
    public Config() {
        String currentPath = System.getProperty("user.dir");

        String translationsFileName = currentPath + "\\lib\\translations.xml";
     
        // initialize translationsXmlParser
        translationsXmlParser = new XML_parser();
        int ret = translationsXmlParser.init(translationsFileName);
        if (ret == -1) {
          String errorMsg = translationsXmlParser.GetErrorMessage();
          System.out.println(errorMsg);
          return;
        }        
    }
    
//    /*----------------------------------------------------------------------
//                          GetConfigurationValue()
//    ------------------------------------------------------------------------*/
//    public String GetConfigurationValue(String configurationTagName) {
//        Node configNodes[] = null;
//        configNodes = configXmlParser.GetNodeListByTag(configurationTagName);
//        if (configNodes == null) {
//            System.out.println("Nothing found");
//            return "";
//        }
//        String value = configNodes[0].getFirstChild().getNodeValue();
//        return value;
//    }
    
    /*----------------------------------------------------------------------
                          GetTranslation()
    ------------------------------------------------------------------------*/
    public String GetTranslation(String translationTagName, int currentLanguage) {
        String currentLanguageTagName;
        if (currentLanguage == 0) {
            currentLanguageTagName = "eng";
        }
        else {
            currentLanguageTagName = "gr";
        }
        
        Node configNodes[] = null;
        configNodes = translationsXmlParser.GetNodeListByTag(translationTagName);
        if (configNodes == null) {
            System.out.println("Nothing found");
            return "";
        }
        NodeList translationsList = configNodes[0].getChildNodes();
        int translationsListCount = translationsList.getLength();        
        
        // for each child node of current subtree root
        for(int i=0; i<translationsListCount; i++) {
            Node childNode = translationsList.item(i);
            String childNodeTagName = childNode.getNodeName();        
            if (childNodeTagName.compareTo(currentLanguageTagName) == 0) {
                String value = childNode.getFirstChild().getNodeValue();
                return value;
            }
        }
        return "";
    }   
    public String parseRDF(String RDFfileName)
    {
        String failReason = null;
        RDFParser = new XML_parser();
        int ret = RDFParser.init(RDFfileName);
        changesRdf = 0;
        if (ret == -1) {
          String errorMsg = RDFParser.GetErrorMessage();
          System.out.println(errorMsg);
          failReason = errorMsg;
        }
        return failReason;
    }
    public NodeList getAllNodes()
    {
        NodeList list = RDFParser.document.getElementsByTagName("*");
        return list;
    }
    
    public String replaceRDFNodes(TreeMap<String, String> substitutes) throws TransformerException, TransformerConfigurationException
    {
//      Check nodeName for the property
        NodeList list = this.getAllNodes();
        for (int i=0; i<list.getLength(); i++) {
            Element element = (Element)list.item(i);
            String nodeName = element.getNodeName();
            Set c = substitutes.keySet();
            Iterator itr = c.iterator();
            String tmpNodeName = "";
                        String tmpNodeName_hash = "";

            while(itr.hasNext()){
                tmpNodeName = (String)itr.next();
                if(nodeName.equals(tmpNodeName)){
                    nodeName = nodeName.replace(tmpNodeName, substitutes.get(tmpNodeName));
                    RDFParser.document.renameNode(element, "", nodeName);
                    this.changesRdf++;
                }
                 tmpNodeName_hash = "#"+tmpNodeName;
                if(nodeName.equals(tmpNodeName_hash)){
                    nodeName = nodeName.replace(tmpNodeName_hash, "#"+substitutes.get(tmpNodeName));
                    RDFParser.document.renameNode(element, "", nodeName);
                    this.changesRdf++;
                }
            }
//      Check attributeValue for the property  
            if(element.hasAttributes()) {
                NamedNodeMap attrs = element.getAttributes();
                for(int j = 0 ; j<attrs.getLength() ; j++) {
                    Attr attribute = (Attr)attrs.item(j);
                    String attrValue = attribute.getValue();
                    Set c1 = substitutes.keySet();
                    Iterator itr1 = c1.iterator();
                    String tmpAttrValue = "";
                    String tmpAttrValue_hash = "";
                    while(itr1.hasNext()){
                        tmpAttrValue = (String)itr1.next();
                        if(attrValue.equals(tmpAttrValue)){
                            attrValue = attrValue.replace(tmpAttrValue, substitutes.get(tmpAttrValue));
                            attribute.setValue(attrValue);
                            this.changesRdf++;
                        }

                        tmpAttrValue_hash = "#"+tmpAttrValue;
                        if(attrValue.equals(tmpAttrValue_hash)){
                            attrValue = attrValue.replace(tmpAttrValue_hash, "#"+substitutes.get(tmpAttrValue));
                            attribute.setValue(attrValue);
                            this.changesRdf++;
                        }
                    }
                }
            }
        }
        String xmlString = transformXML(RDFParser.document);
        
        return xmlString;
    }
    
    public String transformXML(Document doc) throws TransformerException, TransformerConfigurationException
    {
        Transformer transformer = TransformerFactory.newInstance().newTransformer();
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");

        //initialize StreamResult with File object to save to file
        StreamResult result = new StreamResult(new StringWriter());
        DOMSource source = new DOMSource(doc);
        transformer.transform(source, result);

        String xmlString = result.getWriter().toString();
        return xmlString;
    }

    public int getChangesRdf() {
        return changesRdf;
    }
}
