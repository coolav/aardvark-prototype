/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package uib.flickr;

import java.io.*;
import uib.gui.AardvarkGui;

/**
 *
 * @author Olav
 */
public class TestPath {

    final static AardvarkGui parent = new AardvarkGui();
   final static String indexDir = parent.textfieldIndexDir.getText();
    File iDir = new File(indexDir);

    public void TestPath() {
        String[] children = iDir.list();
        if (children == null) {
            // Either dir does not exist or is not a directory
        } else {
            for (int i = 0; i < children.length; i++) {
                // Get filename of file or directory
                String filename = children[i];
                System.out.println(filename);
            }
        }


    }

    public static void main(String args[]) {
        System.out.println(indexDir);

    }
}
