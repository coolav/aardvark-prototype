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
abstract class AbstractRetrievalEngine implements RetrievalEngine {
	abstract public List<ResultListEntry> getSimilarImages_fromSet(Set <Element> VisualDescriptorSet, String whereToSearch, boolean recursive, JProgressBar progress);

	public List<ResultListEntry> getSimilarImages_fromSet(Set<Element> VisualDescriptor, String whereToSearch, boolean recursive) {
        return getSimilarImages_fromSet(VisualDescriptor, whereToSearch, recursive, null);
    }

	public List<ResultListEntry> getSimilarImages(Element VisualDescriptor, String whereToSearch, boolean recursive) {
        return getSimilarImages(VisualDescriptor, whereToSearch, recursive, null);
    }

    public List<ResultListEntry> getImagesByXPathSearch(String xPath, String whereToSearch, boolean recursive) {
        return getImagesByXPathSearch(xPath, whereToSearch, recursive, null);
    }

    /**
     * This method has to be able to cope with null as parameter for the JProgressBar.
     * @param xPath
     * @param objects
     * @param whereToSearch
     * @param recursive
     * @param progress can be null.
     */
    abstract public List<ResultListEntry> getImagesBySemantics(String xPath, ArrayList objects, String whereToSearch, boolean recursive, JProgressBar progress);

    /**
     * This method has to be able to cope with null as parameter for the JProgressBar.
     * @param VisualDescriptor
     * @param whereToSearch
     * @param recursive
     * @param progress can be null.
     */
    abstract public List<ResultListEntry> getSimilarImages(Element VisualDescriptor, String whereToSearch, boolean recursive, JProgressBar progress);

    /**
     * This method has to be able to cope with null as parameter for the JProgressBar.
     * @param xPath
     * @param whereToSearch
     * @param recursive
     * @param progress can be null.
     */
    abstract public List<ResultListEntry> getImagesByXPathSearch(String xPath, String whereToSearch, boolean recursive, JProgressBar progress);

    
}
