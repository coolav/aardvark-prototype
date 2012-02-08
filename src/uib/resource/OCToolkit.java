/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package uib.resource;

/**
 *
 * @author Olav
 */

import org.jaxen.XPath;
import org.jaxen.jdom.JDOMXPath;
import org.jdom.Namespace;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.StringTokenizer;
import java.util.Vector;


public class OCToolkit {

    public static URL getLog4JPropertyFile() {
        URL returnURL = null;
        try {
            returnURL = new File("log4j.properties").toURL();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return returnURL;
    }


    public static URL getRelationsFile() {
        return OCToolkit.class.getResource("mpeg7_relations.xml");
    }

    public static URL getBaseObjectsFile() {
        return OCToolkit.class.getResource("base-objects.mp7.xml");
    }

    public static URL getCidocCrmFile(){
        return OCToolkit.class.getResource("CidocMpeg7.xml");
    }

    public static List xpathQuery(org.jdom.Document document, String xpathQuery1, Namespace xNs) {
        List returnValue = new Vector();
        String xpathQuery = xpathQuery1;
        XPath xPath;
        try {
            // if there is a xmlns namespace wihtout a prefix, then
            // a default one has to be added. Otherwise jaxen does not work
            // Note: the prefix "x" has to be used in the xpath-query as well.
            Namespace ns = document.getRootElement().getNamespace();
            if (ns.getPrefix().length() == 0) {
                StringTokenizer tokens = new StringTokenizer(xpathQuery, "/", true);
                xpathQuery = "";
                while (tokens.hasMoreTokens()) {
                    String token = tokens.nextToken();
                    if (!token.equalsIgnoreCase("/")) {
                        token = "x:" + token;
                    }
                    xpathQuery += token;
                }

                xPath = new JDOMXPath(xpathQuery);
                xPath.addNamespace("x", ns.getURI());
            } else {
                xPath = new JDOMXPath(xpathQuery);
            }

            if (xNs != null) {
                xPath.addNamespace(xNs.getPrefix(), xNs.getURI());
            }

            // console.echo("OLD: \"" + xpathQuery1 + "\", NEW: \"" + xpathQuery + "\"");

            Object jdomNode = document; // Document, Element etc.
            //XPath path = new JDOMXPath("/*"); //--> das root element

            returnValue = xPath.selectNodes(jdomNode); // entries are from the type "org.jdom.Element"
        } catch (Exception e) {
            e.printStackTrace();
        }

        return returnValue;
    }

    public static Namespace getFSWNamespace() {
        return Namespace.getNamespace("fsw", "http://www.at.know-center.at/fsw");
    }
}
