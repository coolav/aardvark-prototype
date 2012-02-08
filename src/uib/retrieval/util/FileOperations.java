/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package uib.retrieval.util;

import java.io.*;
import java.util.*;
/**
 *
 * @author Olav
 */
public class FileOperations {
    
    public static String[] getAllDescriptions(File directory, boolean descendIntoSubDirectories) throws IOException {
        Vector v = new Vector();
        File[] f = directory.listFiles();
        for (int i = 0; i < f.length; i++) {
            File file = f[i];
            if (file != null && file.getName().endsWith(".mp7.xml")) {
                v.add(file.getCanonicalPath());
            }

            if (descendIntoSubDirectories && file.isDirectory()) {
                String[] tmp = getAllDescriptions(file, true);
                if (tmp != null) {
                    for (int j = 0; j < tmp.length; j++) {
                        v.add(tmp[j]);
                    }
                }
            }
        }
//        for (int i = 0; i < f.length; i++) {
//            System.out.println(f[i].toString());
//        }
        if (v.size() > 0)
            return (String[]) v.toArray(new String[1]);
        else
            return null;
    }
    
}
