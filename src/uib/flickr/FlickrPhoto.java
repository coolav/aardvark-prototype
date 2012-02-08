/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package uib.flickr;

import java.awt.image.BufferedImage;
import java.util.*;
/**
 *
 * @author Olav
 */
public class FlickrPhoto {
    String title, url, photourl;
    BufferedImage img = null;
    List<String> tags;

    public FlickrPhoto(String title, String url, String photourl, List<String> tags) {
        this.title = title;
        this.url = url;
        this.photourl = photourl;
        this.tags = new LinkedList<String>(tags);
    }

    public String toString() {
        return title + ": " + url + " (" + photourl + ")";
    }

}
