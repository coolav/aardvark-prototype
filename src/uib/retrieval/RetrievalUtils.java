/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package uib.retrieval;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;
import java.util.Vector;
import javax.swing.JOptionPane;
import org.jaxen.XPath;
import org.jaxen.jdom.JDOMXPath;
import org.jdom.Namespace;
import uib.gui.AardvarkGui;

/**
 *
 * @author Olav
 */
public class RetrievalUtils {
    private AardvarkGui parent;
    
    public RetrievalUtils(AardvarkGui parent){
        this.parent = parent;
    }
    private void startSearch() {
        if (getXPathStatement() != null && !(getXPathStatement() != null)) {
         //   parent.searchForImage(agentSearchPanel.getXPathStatement(), parent.jTextFieldDescription.getText(), true);
        //} else if (!(agentSearchPanel.getXPathStatement() != null) && (keywordSearchPanel.getXPathStatement() != null)) {
          //  parent.searchForImage(keywordSearchPanel.getXPathStatement(), parent.jTextFieldDescription.getText(), true);
        } else {
            JOptionPane.showMessageDialog(parent, "Only one at a time is allowed:\n remove text from agents or keywords to continue!");
        }
    }
    
    public String getKeywords() {
        return parent.textfieldSearchText.getText();
    }

    public int getMode() {
        return parent.jComboBoxSearchMode.getSelectedIndex();
    }

    public String getXPathStatement() {
        String buffer = null;
        if (parent.textfieldSearchText.getText().length() > 0) {
            buffer = new String();
            if (parent.jComboBoxSearchModality.getSelectedIndex() == 0) {
                buffer = ("//*" + createPostfix());
            } else if (parent.jComboBoxSearchModality.getSelectedIndex() == 1) {
                buffer = ("//TextAnnotation/FreeTextAnnotation" + createPostfix());
            } else if (parent.jComboBoxSearchModality.getSelectedIndex() == 2) {
                buffer = ("//Definition/FreeTextAnnotation" + createPostfix());
            } else if (parent.jComboBoxSearchModality.getSelectedIndex() == 3) {
                buffer = ("//Definition/FreeTextAnnotation" + createPostfix());
            } else if (parent.jComboBoxSearchModality.getSelectedIndex() == 4) {
                buffer = ("//FreeTextAnnotation" + createPostfix());
            } else if (parent.jComboBoxSearchModality.getSelectedIndex() == 5) {
                buffer = ("//Comment/FreeTextAnnotation" + createPostfix());
            }
        }
        return buffer;
    }

    private String createPostfix() {
        String phrase = "[contains(translate(., 'ABCDEFGHIJKLMNOPQRSTUVWXYZ', 'abcdefghijklmnopqrstuvwxyz'),translate('" 
                + parent.textfieldSearchText.getText() + "', 'ABCDEFGHIJKLMNOPQRSTUVWXYZ', 'abcdefghijklmnopqrstuvwxyz'))]";
        if (parent.jComboBoxSearchMode.getSelectedIndex() == 2) { // PHRASE
            return phrase;
        } else if (parent.jComboBoxSearchMode.getSelectedIndex() == 1) {  // AND
            StringTokenizer st = new StringTokenizer(parent.textfieldSearchText.getText(), " ", false);
            String retVal = "[";
            while (st.hasMoreElements()) {
                String s = st.nextToken();
                retVal = retVal + "contains(translate(., 'ABCDEFGHIJKLMNOPQRSTUVWXYZ', 'abcdefghijklmnopqrstuvwxyz') ,translate('" 
                        + s + "', 'ABCDEFGHIJKLMNOPQRSTUVWXYZ', 'abcdefghijklmnopqrstuvwxyz'))";
                if (st.hasMoreElements()) retVal = retVal + " and ";
            }
            retVal = retVal + "]";
            return retVal;
        } else {  // OR
            StringTokenizer st = new StringTokenizer(parent.textfieldSearchText.getText(), " ", false);
            String retVal = "[";
            while (st.hasMoreElements()) {
                String s = st.nextToken();
                retVal = retVal + "contains(translate(., 'ABCDEFGHIJKLMNOPQRSTUVWXYZ', 'abcdefghijklmnopqrstuvwxyz') ,translate('" 
                        + s + "', 'ABCDEFGHIJKLMNOPQRSTUVWXYZ', 'abcdefghijklmnopqrstuvwxyz'))";
                if (st.hasMoreElements()) retVal = retVal + " or ";
            }
            retVal = retVal + "]";
            return retVal;
        }
    }
    
     public static List xpathQuery(org.jdom.Element document, String xpathQuery1, Namespace xNs) {
        List returnValue = new Vector();
        String xpathQuery = xpathQuery1;
        XPath xPath;
        try {
            Namespace ns = document.getNamespace();
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
            Object jdomNode = document;
            returnValue = xPath.selectNodes(jdomNode);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return returnValue;
    }
     
    public static String[] getAllImages(File directory, boolean descendIntoSubDirectories) throws IOException {
        ArrayList<String> v = new ArrayList<String>();
        File[] f = directory.listFiles();
        for (int i = 0; i < f.length; i++) {
            File file = f[i];
            String fnameLow = file.getName().toLowerCase();
            if (file != null && (fnameLow.endsWith(".jpg") || fnameLow.endsWith(".ppm") || fnameLow.endsWith(".png"))
                    && !file.getName().startsWith("tn_")) {
                v.add(file.getCanonicalPath());
            }

            if (descendIntoSubDirectories && file.isDirectory()) {
                String[] tmp = getAllImages(file, true);
                if (tmp != null) {
                    for (int j = 0; j < tmp.length; j++) {
                        v.add(tmp[j]);
                    }
                }
            }
        }
        if (v.size() > 0)
            return v.toArray(new String[1]);
        else
            return null;
    } 
    
}
