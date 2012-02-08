

package uib.annotation.util;

import javax.swing.filechooser.FileFilter;
import java.io.File;

/**
 * Date: 29.08.2002
 * Time: 16:04:06
 *
 * @author Mathias Lux, mathias@juggle.at
 */
public class XMLFileFilter extends FileFilter {
    /**
     * Whether the given file is accepted by this filter.
     */
    public boolean accept(File f) {
        boolean accepted = false;
        if (f.getName().endsWith(".xml"))
            accepted = true;
//        if (f.getName().endsWith(".mp7"))
//            accepted = true;
        if (f.isDirectory())
            accepted = true;
        return accepted;
    }

    /**
     * The description of this filter. For example: "JPG and GIF Images"
     */
    public String getDescription() {
        return "XML Files (*.xml)";
    }
}
