
package uib.gui;

import java.util.Iterator;
import javax.swing.JTextField;
import org.jdom.Element;
import org.jdom.Namespace;

/**
 *
 * @author Olav
 */
public class SemanticAnnotation implements AnnotationPanel{

    private AardvarkGui parent;
   
    public SemanticAnnotation(AardvarkGui parent){
        this.parent = parent;
        
    }
       
    @Override
    public Element createXML() {
               
        Element root, textElement = null;
        Namespace mpeg7, xsi;
        String ft = parent.textAreaAnnotateFreetext.getText().trim();

        mpeg7 = Namespace.getNamespace("", "urn:mpeg:mpeg7:schema:2001");
        xsi = Namespace.getNamespace("xsi", "http://www.w3.org/2001/XMLSchema-instance");
        root = new Element("TextAnnotation", mpeg7);

        // create structured text annotation element
        Element structTextElement = new Element("StructuredAnnotation", mpeg7);
        boolean hasStructuredTextAnnotation = false;
        boolean hasFreeTextAnnotation = false;

        
        
        // create and add subelements:
        hasStructuredTextAnnotation = addStructuredTExtElement
                (structTextElement, parent.textfieldAnnotateName, "Name", mpeg7) || hasStructuredTextAnnotation;
        hasStructuredTextAnnotation = addStructuredTExtElement
                (structTextElement, parent.textfieldAnnotateCreator, "Creator", mpeg7) || hasStructuredTextAnnotation;
        hasStructuredTextAnnotation = addStructuredTExtElement
                (structTextElement, parent.textfieldAnnotatePeriod, "Period", mpeg7) || hasStructuredTextAnnotation;
        hasStructuredTextAnnotation = addStructuredTExtElement
                (structTextElement, parent.textfieldAnnotateTechnique, "Technique", mpeg7) || hasStructuredTextAnnotation;
        hasStructuredTextAnnotation = addStructuredTExtElement
                (structTextElement, parent.textfieldAnnotateMaterials, "Materials", mpeg7) || hasStructuredTextAnnotation;
        hasStructuredTextAnnotation = addStructuredTExtElement
                (structTextElement, parent.textfieldAnnotateHeight, "Height", mpeg7) || hasStructuredTextAnnotation;
        hasStructuredTextAnnotation = addStructuredTExtElement
                (structTextElement, parent.textfieldAnnotateDate, "Date", mpeg7) || hasStructuredTextAnnotation;
        hasStructuredTextAnnotation = addStructuredTExtElement
                (structTextElement, parent.textfieldAnnotateActor, "Actor", mpeg7) || hasStructuredTextAnnotation;
        hasStructuredTextAnnotation = addStructuredTExtElement
                (structTextElement, parent.textfieldAnnotateWidth, "Width", mpeg7) || hasStructuredTextAnnotation;
        hasStructuredTextAnnotation = addStructuredTExtElement
                (structTextElement, parent.textfieldAnnotateLocation, "Location", mpeg7) || hasStructuredTextAnnotation;
        hasStructuredTextAnnotation = addStructuredTExtElement
                (structTextElement, parent.textfieldAnnotateActivity, "Activity", mpeg7) || hasStructuredTextAnnotation;
        hasStructuredTextAnnotation = addStructuredTExtElement
                (structTextElement, parent.textfieldAnnotateTheme, "Theme", mpeg7) || hasStructuredTextAnnotation;
        hasStructuredTextAnnotation = addStructuredTExtElement
                (structTextElement, parent.textFieldAnnotateConcept, "Concept", mpeg7) || hasStructuredTextAnnotation;
        
        // create free text annotation element
        if (ft.length() > 0) {
            textElement = new Element("FreeTextAnnotation", mpeg7);
            hasFreeTextAnnotation = true;
            textElement.setText(ft);

        }
        
        if (hasFreeTextAnnotation || hasStructuredTextAnnotation) {
            if (hasFreeTextAnnotation) root.addContent(textElement);
            if (hasStructuredTextAnnotation) root.addContent(structTextElement);
            System.out.println(root);
            return root;
            
        } else {
            return null;
        }

    }

     private boolean addStructuredTExtElement(Element rootElement, JTextField field, String elementName, Namespace namespace) {
        boolean hasText = false;

        if (field.getText().trim().length() > 0) {
            System.out.println(field.getText());
            Element where = new Element(elementName, namespace);
            Element name = new Element("Name", namespace);
            name.addContent(field.getText().trim());
            where.addContent(name);
            rootElement.addContent(where);
            hasText = true;
        }

        return hasText;
    }

     public void resetTextFields() {
        parent.textAreaAnnotateFreetext.setText("");
        Iterator<JTextField> iterator = parent.annotationFields.iterator();
        while (iterator.hasNext()) {
            JTextField currentItem = iterator.next();
            currentItem.setText("");
        }

    }

    public void setName(String text){
        parent.textfieldAnnotateName.setText(text);
    }
    
    public void setCreator(String text){
        parent.textfieldAnnotateCreator.setText(text);
    }

    public void setPeriod(String text){
        parent.textfieldAnnotatePeriod.setText(text);
    }

    public void setTechnique(String text){
        parent.textfieldAnnotateTechnique.setText(text);
    }

    public void setMaterials(String text){
        parent.textfieldAnnotateMaterials.setText(text);
    }

    public void setActor(String text){
        parent.textfieldAnnotateActor.setText(text);
    }

    public void setLocation(String text){
        parent.textfieldAnnotateLocation.setText(text);
    }

    public void setActivity(String text){
        parent.textfieldAnnotateActivity.setText(text);
    }

    public void setTheme(String text){
        parent.textfieldAnnotateTheme.setText(text);
    }

    public void setConcept(String text){
        parent.textFieldAnnotateConcept.setText(text);
    }

    public void setFreeText(String text){
        parent.textAreaAnnotateFreetext.setText(text);
    }
    
    public void setDate(String text){
        parent.textfieldAnnotateDate.setText(text);
    }
    
    public void setHeight(String text){
        parent.textfieldAnnotateHeight.setText(text);
    }
    
    public void setWidth(String text){
        parent.textfieldAnnotateWidth.setText(null);
    }

}
