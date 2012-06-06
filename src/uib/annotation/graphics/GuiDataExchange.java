/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package uib.annotation.graphics;

import uib.scratch.AvatarChooser;
import java.io.FileNotFoundException;
import java.io.IOException;
import org.apache.lucene.document.Document;
import uib.gui.AardvarkGui;
import uib.gui.SearchForDocument;

/**
 *
 * @author Olav
 */
public class GuiDataExchange {
    
    private AardvarkGui parent; 
    private AvatarChooser chooser;
    
    public GuiDataExchange(AardvarkGui parent){
        this.parent = parent;
    }
    
    public void setCurrentDocument(int currentDocument){
        parent.setCurrentDocument(currentDocument);
    }
    
    public void startSearch(int docId) throws FileNotFoundException, IOException{
        Document d = parent.reader.document(docId);
        SearchForDocument searchDoc = new SearchForDocument(parent, d);
        searchDoc.start();
    }
    
    public void testOutput(String text){
        String newText = "";
        text = newText;
        parent.status.setText(newText);
        //System.out.println(text);
        
    }
    
    public void testParent() throws FileNotFoundException, IOException{
        //TODO fix the browsingpanel 
        //System.out.println(chooser.getImage());
        //int docId = chooser.getImage();
        //startSearch(docId);
        
    }
    
}
