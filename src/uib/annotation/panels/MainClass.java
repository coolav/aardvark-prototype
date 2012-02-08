/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package uib.annotation.panels;

/**
 *
 * @author Olav
 */
import uib.resource.*;
import java.net.URL;

public class MainClass {

  public static void main(String[] args) throws Exception {
    // absolute from the classpath
    URL url = MainClass.class.getResource("../../resource/mpeg7_relations.xml");
    System.out.println(url);
    // relative to the class location
    url = MainClass.class.getResource("mpeg7_relations.xml");
    System.out.println(url);
    // another relative document
    url = MainClass.class.getResource("docs/bar.txt");
    System.out.println(url);

  }
}
