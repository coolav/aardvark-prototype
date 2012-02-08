/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package uib.rdfConverter;


//import com.hp.hpl.jena.rdf.model.*;
//import com.hp.hpl.jena.vocabulary.DC;
//import com.hp.hpl.jena.vocabulary.RDF;
//import com.hp.hpl.jena.vocabulary.DCTerms;
//import com.hp.hpl.jena.vocabulary.RDFS;
import org.jdom.Element;
import org.jdom.Namespace;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;

import java.util.*;
import uib.gui.AardvarkGui;

/**
 *
 * @author Olav
 */
public class Annotations {

    AardvarkGui parent;
    String name, creator, period, technique, material, height, width, date;
    String actor, location, activity, theme, concept, freetext;
    private final static String cidocNameSpace = "http://cidoc.ics.forth.gr/rdfs/caspar/cidoc.rdfs#";
    private final static String rdfNameSpace = "http://www.w3.org/1999/02/22-rdf-syntax-ns#";
    private final static String rdfsNameSpace = "http://www.w3.org/2000/01/rdf-schema#";
    private final static String xmlSchemaNameSpace = "http://www.w3.org/2001/XMLSchema#";
    private final static String nameTest = "Olav";
    private final static String contributorTest = "Coolav";
   // public static Model model;
    

    public Annotations(AardvarkGui parent) {
        this.parent = parent;
        //this.name = parent.textfieldAnnotateName.getText();
        //this creator
        //this period
        //this technique, material, height, width, date;
        //String actor, location, activity, theme, concept, freetext;
    }/*

    public static Model createRDFModel() {
        //model = ModelFactory.createDefaultModel();
        //Property E55_Type = model.createProperty("#E55_Type",  "rdf:ID=E55_Type");
        //Resource E1.CRM_Entity = model.createResource(nameTest);
        //  model.create

        //Resource document =
                model.createResource(cidocNameSpace)
        //        .addProperty(DC.contributor, nameTest)
        //        .addProperty(DCTerms.creator, contributorTest);


        return model;
    }

    public String getName() {
        return name;

    }*/

    public static void main(String args[]) {
       // createRDFModel();
        //StmtIterator iter = createRDFModel().listStatements();
        //while (iter.hasNext()) {
            //Statement stmt = iter.nextStatement();  // get next statement
            //Resource subject = stmt.getSubject();     // get the subject
            //Property predicate = stmt.getPredicate();   // get the predicate
            //RDFNode object = stmt.getObject();      // get the object

            /*
            System.out.print(subject.toString());
            System.out.print(" " + predicate.toString() + " ");
            if (object instanceof Resource) {
                System.out.print(object.toString());
            } else {
                // object is a literal
                System.out.print(" \"" + object.toString() + "\"");
            }

            System.out.println(" .");*/

        }
         //           createRDFModel().write(System.out, "RDF/XML-ABBREV");

    
}
