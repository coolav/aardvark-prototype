/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package uib.retrieval.engine;

import java.util.*;

import javax.swing.JProgressBar;

import org.jdom.Element;
import uib.retrieval.model.ResultListEntry;

/**
 *
 * @author Olav
 */
public interface RetrievalEngine {
    
    public List<ResultListEntry> getSimilarImages(Element VisualDescriptor, String whereToSearch, boolean recursive);

    public List<ResultListEntry> getSimilarImages(Element VisualDescriptor, String whereToSearch, boolean recursive, JProgressBar progress);
	
    public List<ResultListEntry> getSimilarImages_fromSet(Set<Element> VisualDescriptor, String whereToSearch, boolean recursive);

    public List<ResultListEntry> getSimilarImages_fromSet(Set<Element> VisualDescriptor, String whereToSearch, boolean recursive, JProgressBar progress);

    public List<ResultListEntry> getImagesByXPathSearch(String xPath, String whereToSearch, boolean recursive, JProgressBar progress);

    public List<ResultListEntry> getImagesByXPathSearch(String xPath, String whereToSearch, boolean recursive);

    public List<ResultListEntry> getImagesBySemantics(String xPath, ArrayList objects, String whereToSearch, boolean recursive, JProgressBar progress);

}
