package uib.annotation.util.image;

import org.apache.batik.svggen.SVGGraphics2D;
import org.apache.batik.svggen.SVGGraphics2DIOException;
import org.apache.batik.dom.GenericDOMImplementation;
import org.w3c.dom.DOMImplementation;
import org.w3c.dom.Document;

import java.io.*;
//import java.awt.Graphics2D;
/**
 *
 * @author Olav
 */
public class SVGExporter {
    private final SVGGraphics2D svgGenerator;

    public SVGExporter() {
        DOMImplementation domImpl = GenericDOMImplementation.getDOMImplementation();
        Document document = domImpl.createDocument(null, "svg", null);
        svgGenerator = new SVGGraphics2D(document);
    }

    public void writeFile(File file) throws Exception {
        boolean useCSS = true;
        FileOutputStream fos = new FileOutputStream(file, false);
        Writer out = new OutputStreamWriter(fos, "UTF-8");
        svgGenerator.stream(out, useCSS);
    }

    /*public Graphics2D getGraphics() {
        return svgGenerator;
    }*/
}
