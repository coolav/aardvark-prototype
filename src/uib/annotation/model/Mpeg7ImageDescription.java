/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package uib.annotation.model;


import org.jdom.Document;
import org.jdom.Element;
import org.jdom.Namespace;

import java.util.Iterator;
/**
 *
 * @author Olav
 */

public class Mpeg7ImageDescription {
    private Element mediaInstance, textAnnotation, semantics;
    private Document doc;
    private Element root;
    private Namespace mpeg7, xsi;

    
    
    public Mpeg7ImageDescription(Element mediaInstance,  Element semantics, Element textAnnotation){
    
        this.mediaInstance = mediaInstance;
        this.semantics = semantics;
        this.textAnnotation = textAnnotation;
    }

    public Mpeg7ImageDescription(Element textAnnotation, Element semantics){
    
        this.semantics = semantics;
        this.textAnnotation = textAnnotation;
    }
    
    private void init() {
        mpeg7 = Namespace.getNamespace("", "urn:mpeg:mpeg7:schema:2001");
        xsi = Namespace.getNamespace("xsi", "http://www.w3.org/2001/XMLSchema-instance");

        root = new Element("Mpeg7", mpeg7);
        root.addNamespaceDeclaration(mpeg7);
        root.addNamespaceDeclaration(xsi);

        doc = new Document(root);
        

        // setting up structure to Description/MultimediaContent/Image
        Element desc = new Element("Description", mpeg7);
        root.addContent(desc);
        desc.setAttribute("type", "ContentEntityType", xsi);
        Element mumeContent = new Element("MultimediaContent", mpeg7);
        mumeContent.setAttribute("type", "ImageType", xsi);
        Element img = new Element("Image", mpeg7);
        desc.addContent(mumeContent.addContent(img));

        
        //if (mediaInstance != null) profile.addContent(mediaInstance);
     
        // TextAnnotation
        if (textAnnotation != null) {
            img.addContent(textAnnotation);
        }

        // Semantic
        if (semantics != null) {
            img.addContent(semantics);
        }

    }

    /**
     * returns the full MPEG-7 Description
     *
     * @return MPEG-7 Description
     */
    public Document createDocument() {
        init();
        return doc;
    }
}
