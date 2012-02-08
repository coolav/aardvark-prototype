/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package uib.xml;

import java.io.*;
import java.util.*;
import org.jdom.*;

/**
 *
 * @author Olav
 */
public class DomTest {

    int xmlHeight(Element e) {

        List contents = e.getContent();
        Iterator i = contents.iterator();
        int max = 0;

        while (i.hasNext()) {
            Object c = i.hasNext();
            int h;
            if (c instanceof Element) {
                h = xmlHeight((Element) c);
            } else {
                h = 1;
            }
            if (h > max) {
                max = h;
            }

        }
         return max+1;
    }
}
