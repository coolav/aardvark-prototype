/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package uib.retrieval.model;

import org.jdom.Element;

/**
 *
 * @author Olav
 */
public class ElementEntry {
    
    
    public Element semanticElement;
    public int id;

    public ElementEntry(Element semanticElement, int id) {
        this.semanticElement = semanticElement;
        this.id = id;
    }
    
}
