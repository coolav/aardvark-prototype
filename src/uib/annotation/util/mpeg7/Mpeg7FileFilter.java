/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package uib.annotation.util.mpeg7;

import javax.swing.filechooser.FileFilter;
import java.io.File;

/**
 *
 * @author Olav
 */
public class Mpeg7FileFilter extends FileFilter {
    /**
     * Whether the given file is accepted by this filter.
     */
    public boolean accept(File f) {
        boolean accepted = false;
        if (f.getName().endsWith(".mp7.xml"))
            accepted = true;
        if (f.getName().endsWith(".mp7"))
            accepted = true;
        if (f.isDirectory())
            accepted = true;
        return accepted;
    }

    /**
     * The description of this filter. For example: "JPG and GIF Images"
     */
    public String getDescription() {
        return "MPEG-7 Files (*.mp7, *.mp7.xml)";
    }

}
