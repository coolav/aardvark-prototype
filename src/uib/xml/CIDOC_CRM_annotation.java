/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package uib.xml;

import org.w3c.dom.*;
import org.xml.sax.*;
import java.util.*;
import java.io.*;
import javax.xml.stream.events.Namespace;
import uib.gui.AardvarkGui;

/**
 *
 * @author Olav
 */
public class CIDOC_CRM_annotation {

    private AardvarkGui parent;
    private Namespace Mpeg7, xsi, crm;

    public CIDOC_CRM_annotation(AardvarkGui parent){
        this.parent = parent;
    }

    public void annotate(String...annotations){

    }

    public void writeFile(String fileName){

    }

}
