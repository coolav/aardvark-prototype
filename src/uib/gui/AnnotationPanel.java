/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package uib.gui;

import org.jdom.Element;

/**
 *
 * @author Olav
 */
public interface AnnotationPanel {
      /**
     * Creates the MPEG-7 descriptor from the annotation
     * and returns it as JDOM Element
     * @return the descriptor as JDOM Element or NULL on error
     */
    public Element createXML();
    
}
