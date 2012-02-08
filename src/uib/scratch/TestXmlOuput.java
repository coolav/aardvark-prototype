/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package uib.scratch;

import java.io.FileWriter;
import java.io.IOException;
import org.jdom.Attribute;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.Namespace;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;
import uib.gui.AardvarkGui;

/**
 *
 * @author Olav
 */
public class TestXmlOuput {

    private AardvarkGui parent;
    Element root, textElement = null;
    Namespace mpeg7, xsi;

    public TestXmlOuput(AardvarkGui parent){
        this.parent = parent;
        
    }
   
    public Element createXML() {

        try {
            
            mpeg7 = Namespace.getNamespace("", "urn:mpeg:mpeg7:schema:2001");
            xsi = Namespace.getNamespace("xsi", "http://www.w3.org/2001/XMLSchema-instance");
            root = new Element("TextAnnotation", mpeg7);
            Element company = new Element("company");
            Document doc = new Document(company);
            doc.setRootElement(company);

            Element staff = new Element("staff");
            staff.setAttribute(new Attribute("id", "1"));
            staff.addContent(new Element("firstname").setText("yong"));
            staff.addContent(new Element("lastname").setText("mook kim"));
            staff.addContent(new Element("nickname").setText("mkyong"));
            staff.addContent(new Element("salary").setText("199999"));

            doc.getRootElement().addContent(staff);

            Element staff2 = new Element("staff");
            staff2.setAttribute(new Attribute("id", "2"));
            staff2.addContent(new Element("firstname").setText("low"));
            staff2.addContent(new Element("lastname").setText("yin fong"));
            staff2.addContent(new Element("nickname").setText("fong fong"));
            staff2.addContent(new Element("salary").setText("188888"));

            doc.getRootElement().addContent(staff2);

            // new XMLOutputter().output(doc, System.out);
            XMLOutputter xmlOutput = new XMLOutputter();

            // display nice nice
            xmlOutput.setFormat(Format.getPrettyFormat());
            xmlOutput.output(doc, new FileWriter("c:\\file.xml"));

            System.out.println("File Saved!");
            } catch (IOException io) 
            {
                System.out.println(io.getMessage());
            }
    
    }
}
