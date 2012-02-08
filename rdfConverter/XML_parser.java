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

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.FactoryConfigurationError;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.*;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

/*-----------------------------------------------------
class XML_parser
-------------------------------------------------------*/
public class XML_parser {
    // current XML file's parser

    public Document document = null;
    // error message
    private String XMLParserErrorMessage;
    // the name and path-name of current XML file
    public String currentXMLFileName = "";
    public String currentXMLFilePathName = "";

    /*----------------------------------------------------------------------
    Constructors of XML_parser class
    ------------------------------------------------------------------------*/
    public XML_parser() {
    }
    /*----------------------------------------------------------------------
    init()
    ------------------------------------------------------------------------
    INPUT : - String XMLfile: the full-path of the current XML file to be parsed
    OUTPUT : - 0, in case the current XML file's parser is initialized correctly
    - -1, otherwise
    FUNCTION: initializes the current XML file's parser (document)
    CALLED BY: ParseXMLfile() of this and any derived class
    ------------------------------------------------------------------------*/

    public int init(String XMLfile) {
        XMLParserErrorMessage = "";

        int ret = 0;
        XMLParserErrorMessage = "File parsed: " + XMLfile;

        // get the given XML file's path-name
        File f = new File(XMLfile);
        currentXMLFilePathName = f.getParent();
        currentXMLFileName = f.getName();

        // 1. create a DocumentBuilder to parse the current XML file
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        try {
            DocumentBuilder builder = factory.newDocumentBuilder();
            document = builder.parse(new File(XMLfile));
        } catch (SAXParseException sxe) {
            // Error generated during parsing)
            Exception x = sxe;


            if (sxe.getException() != null) {
                x = sxe.getException();
            }
            x.printStackTrace();
            XMLParserErrorMessage += "\n" + x.getMessage() + "(Line " + sxe.getLineNumber()+")";
            ret = -1;
            return ret;
        } catch (SAXException sxe) {
            // Error generated during parsing)
            Exception x = sxe;

            if (sxe.getException() != null) {
                x = sxe.getException();
            }
            x.printStackTrace();
            XMLParserErrorMessage += "\n" + x.getMessage();
            ret = -1;
            return ret;
        } catch (ParserConfigurationException pce) {

            // Parser with specified options can't be built
            pce.printStackTrace();
            XMLParserErrorMessage += "\n" + pce.getMessage();
            ret = -1;
            return ret;
        } catch (IOException ioe) {
            // I/O error
            ioe.printStackTrace();
            XMLParserErrorMessage += "\n" + ioe.getMessage();
            ret = -1;
            return ret;
        }
        return ret;
    }

    /*----------------------------------------------------------------------
    GetErrorMessage()
    ------------------------------------------------------------------------*/
    String GetErrorMessage() {
        return XMLParserErrorMessage;
    }

    /*----------------------------------------------------------------------
    ParseXMLfile()
    ------------------------------------------------------------------------
    INPUT : - String XMLfile: the full-path of the current XML file to be parsed
    OUTPUT : - 0, in case the current XML file is parsed correctly
    - -1, otherwise
    FUNCTION: - creates a DocumentBuilder to parse the current XML file
    ------------------------------------------------------------------------*/
    int ParseXMLfile(String XMLfile) {
        int ret = -1;
        ret = init(XMLfile);

        return ret;
    }

    /*----------------------------------------------------------------------
    GetElementsByTag()
    ------------------------------------------------------------------------
    INPUT: - String tag : the specified tag-name to be searched in current XML file (ex. "author")
    - ArrayList tagPath : the full path of given tag in INVERSE ORDER : from tag -> Root
    (ex. for tag "author", tagPath = {"assessment", "#document"})
    OUTPUT : - String values[] : the values of the tags found in current XML file
    with name = tag and inverse path to the root = tagPath
    FUNCTION: gets the values of the tags found in current XML file
    with name = tag and inverse path to the root = tagPath
    ------------------------------------------------------------------------*/
    String[] GetElementsByTag(String tag, ArrayList tagPath) {
        String values[] = null;
        Node tagNode;
        NodeList tagList = document.getElementsByTagName(tag);
        // get the count of tags found INDEPEDENTLY from the given tag-path
        int tagCount = tagList.getLength();
        if (tagCount > 0) {
            values = new String[tagCount];
        }
        int matches = 0;
        for (int i = 0; i < tagCount; i++) {
            tagNode = tagList.item(i);

            if (TagHasPath(tagNode, tagPath) == true) {
                values[matches] = RemoveTag(tag, tagNode.toString());
                matches++;
            }
        }
        // in case NO tags found having the given tag-path
        if (matches == 0) {
            return null;
        }
        // in case the tags found having the given tag-path
        // are less than the tags found having the given tag-name
        // return ONLY them
        if (matches < tagCount) {
            String finalValues[] = new String[matches];
            for (int i = 0; i < matches; i++) {
                finalValues[i] = values[i];
            }
            return finalValues;
        }
        return values;
    }

    /*----------------------------------------------------------------------
    GetNodeListByTag()
    ------------------------------------------------------------------------
    INPUT: - String tag : the specified tag-name to be searched in current XML file (ex. "author")
    - ArrayList tagPath : the full path of given tag in INVERSE ORDER : from tag -> Root
    (ex. for tag "author", tagPath = {"assessment", "#document"})
    OUTPUT : - Node nodes[] : the nodes of the tags found in current XML file
    with name = tag and inverse path to the root = tagPath
    FUNCTION: gets the nodes of the tags found in current XML file
    with name = tag and inverse path to the root = tagPath
    ------------------------------------------------------------------------*/
    Node[] GetNodeListByTag(String tag, ArrayList tagPath) {
        Node nodes[] = null;
        Node tagNode;
        NodeList tagList = document.getElementsByTagName(tag);
        // get the count of tags found INDEPEDENTLY from the given tag-path
        int tagCount = tagList.getLength();
        if (tagCount > 0) {
            nodes = new Node[tagCount];
        }
        int matches = 0;
        for (int i = 0; i < tagCount; i++) {
            tagNode = tagList.item(i);
            if (TagHasPath(tagNode, tagPath) == true) {
                nodes[matches] = tagNode;
                //values[matches] = RemoveTag(tag, tagNode.toString());
                matches++;
            }
        }
        // in case NO tags found having the given tag-path
        if (matches == 0) {
            return null;
        }
        // in case the tags found having the given tag-path
        // are less than the tags found having the given tag-name
        // return ONLY them
        if (matches < tagCount) {
            Node finalNodes[] = new Node[matches];
            for (int i = 0; i < matches; i++) {
                finalNodes[i] = nodes[i];
            }
            return finalNodes;
        }
        return nodes;
    }

    /*----------------------------------------------------------------------
    GetNodeListByTag()
    ------------------------------------------------------------------------
    INPUT: - String tag : the specified tag-name to be searched in current XML file (ex. "author")
    OUTPUT : - Node nodes[] : the nodes of the tags found in current XML file
    with name = tag
    FUNCTION: gets the nodes of the tags found in current XML file
    with name = tag
    ------------------------------------------------------------------------*/
    Node[] GetNodeListByTag(String tag) {
        Node nodes[] = null;
        Node tagNode;
        NodeList tagList = document.getElementsByTagName(tag);
        // get the count of tags found INDEPEDENTLY from the given tag-path
        int tagCount = tagList.getLength();
        if (tagCount > 0) {
            nodes = new Node[tagCount];
        }
        for (int i = 0; i < tagCount; i++) {
            tagNode = tagList.item(i);
            nodes[i] = tagNode;
        }
        // in case NO tags found
        if (tagCount == 0) {
            return null;
        }
        return nodes;
    }

    /*----------------------------------------------------------------------
    TagHasPath()
    ------------------------------------------------------------------------
    INPUT: - Node tagNode : the specified tag-node to be checked
    - ArrayList checkPath : the specified full path in INVERSE ORDER : from tag -> Root
    (ex. for tag "author", tagPath = {"assessment", "#document"})
    OUTPUT : - TRUE: in case the specified tag-node has inverse path to the root = checkPath
    - FALSE: otherwise
    FUNCTION: checks if the specified tag-node has inverse path to the root = checkPath
    ------------------------------------------------------------------------*/
    boolean TagHasPath(Node tagNode, ArrayList checkPath) {
        // get the path of the given tag (INVERSE ORDER : from tag -> Root)
        ArrayList tagPath = new ArrayList();
        Node parentTag = tagNode.getParentNode();
        while (parentTag != null) {
            tagPath.add(parentTag.getNodeName());
            parentTag = parentTag.getParentNode();
        }

        // compare the paths
        int checkPathSize = checkPath.size();
        int tagPathSize = tagPath.size();
        if (tagPathSize != checkPathSize) {
            return false;
        }
        for (int i = 0; i < tagPathSize; i++) {
            String tagPathElement = (String) (tagPath.get(i));
            String checkPathElement = (String) (checkPath.get(i));
            if (tagPathElement.compareTo(checkPathElement) != 0) {
                return false;
            }
        //System.out.println("Parent of tag: " + tagNode.getNodeName() + " : " + pathElement);
        }

        return true;
    }

    /*----------------------------------------------------------------------
    GetChildValuesOfNodeByTag()
    ------------------------------------------------------------------------
    INPUT: - Node node : the specified tag-node to get the values of it's children-tags
    - String tagName : the specified name of the children-tags to be get
    OUTPUT : String values[] : the values of the given node children-tags with name = tagName
    FUNCTION: gets the values of the given node children-tags with name = tagName
    ------------------------------------------------------------------------*/
    String[] GetChildValuesOfNodeByTag(Node node, String tagName) {
        String values[] = null;
        Node tagNode;
        // get ALL the children nodes of given node
        NodeList childrenList = node.getChildNodes();
        int childrenCount = childrenList.getLength();
        String childrenValues[] = null;
        if (childrenCount > 0) {
            childrenValues = new String[childrenCount];
        }
        int matches = 0;
        for (int i = 0; i < childrenCount; i++) {
            tagNode = childrenList.item(i);
            // get it's name
            String tagNodeName = tagNode.getNodeName();
            // compare it with tagName
            if (tagNodeName.compareTo(tagName) == 0) {
                childrenValues[matches] = RemoveTag(tagName, tagNode.toString());
                matches++;
            }
        }
        // in case NO tags found having the given tag-path
        if (matches == 0) {
            return null;
        }
        // fill values[] with the #matches childrenValues[]
        values = new String[matches];
        for (int i = 0; i < matches; i++) {
            values[i] = childrenValues[i];
        }

        return values;
    }

    /*----------------------------------------------------------------------
    RemoveTag()
    ------------------------------------------------------------------------
    INPUT : String tag = the name of a tag (ex. "author")
    String tagedString = the name of a taged string (ex. "<author>Jean-Baptiste Chevance</author>")
    OUTPUT : the value of the tagedString (ex. "Jean-Baptiste Chevance")
    ------------------------------------------------------------------------*/
    String RemoveTag(String tag, String tagedString) {
        int beginIndex, endIndex;
        beginIndex = tag.length() + 2;
        endIndex = tagedString.length() - (tag.length() + 3);
        String s = tagedString.substring(beginIndex, endIndex);
        s = s.trim();
        return s;
    }
}