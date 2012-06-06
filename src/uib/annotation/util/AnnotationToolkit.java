/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package uib.annotation.util;

import uib.annotation.util.image.PpmReader;
import uib.annotation.util.image.BmpReader;
import org.jaxen.XPath;
import org.jaxen.jdom.JDOMXPath;
import org.jdom.Element;
import org.jdom.Namespace;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.stream.ImageOutputStream;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.*;
import java.util.List;
/**
 *
 * @author Olav
 */
public class AnnotationToolkit {
    public static String AGENTS_FILE = "./agents.mp7.xml";
    public static String PROGRAM_NAME = "Aardvark";
    public static String PROGRAM_VERSION = "v0.56";
    private static final int MAX_THUMBNAIL_EDGE_LENGTH = 120;

    public static List xpathQuery(org.jdom.Element document, String xpathQuery1, Namespace xNs) {
        List returnValue = new ArrayList();
        String xpathQuery = xpathQuery1;
        XPath xPath;
        try {
            // if there is a xmlns namespace wihtout a prefix, then
            // a default one has to be added. Otherwise jaxen does not work
            // Note: the prefix "x" has to be used in the xpath-query as well.
            Namespace ns = document.getNamespace();
            if (ns.getPrefix().length() == 0) {
                StringTokenizer tokens = new StringTokenizer(xpathQuery, "/", true);
                xpathQuery = "";
                while (tokens.hasMoreTokens()) {
                    String token = tokens.nextToken();
                    if (!token.equalsIgnoreCase("/")) {
                        token = "x:" + token;
                    }
                    xpathQuery += token;
                }

                xPath = new JDOMXPath(xpathQuery);
                xPath.addNamespace("x", ns.getURI());
            } else {
                xPath = new JDOMXPath(xpathQuery);
            }

            if (xNs != null) {
                xPath.addNamespace(xNs.getPrefix(), xNs.getURI());
            }

            // console.echo("OLD: \"" + xpathQuery1 + "\", NEW: \"" + xpathQuery + "\"");

            Object jdomNode = document; // Document, Element etc.
            //XPath path = new JDOMXPath("/*"); //--> das root element

            returnValue = xPath.selectNodes(jdomNode); // entries are from the type "org.jdom.Element"
        } catch (Exception e) {
            e.printStackTrace();
        }

        return returnValue;
    }

    /**
      * Solves the path of the file in a URI and returns the media locator DS
      *
      * @ Param file java.io.File, to which the URI is to be found.
      * @ Return JDOM element with the DS media locator, or null if the URI to the file can not be determined.
     */
    public static Element getMpeg7MediaInstance(File file) {
        Namespace mpeg7 = Namespace.getNamespace("", "urn:mpeg:mpeg7:schema:2001");
        Element inst = new Element("MediaInstance", mpeg7).addContent(new Element("InstanceIdentifier", mpeg7));
        if (file != null) {
            try {
                inst.addContent(new Element("MediaLocator", mpeg7).addContent(new Element("MediaUri",
                        mpeg7).addContent(file.getCanonicalFile().toURI().toString())));
            } catch (IOException e) {
                inst = null;
            }
        } else {
            inst = null;
        }
        System.out.println(inst);
        return inst;
        
    }

    public static String getMpeg7Time() {
        Calendar c = Calendar.getInstance();
        c.setTime(new Date());
        String t = c.get(Calendar.YEAR) + "-" + to2letters(c.get(Calendar.MONTH) + 1) + "-" + to2letters(c.get(Calendar.DAY_OF_MONTH))
                + "T" + to2letters(c.get(Calendar.HOUR_OF_DAY)) + ":" + to2letters(c.get(Calendar.MINUTE));
        return t;
    }

    public static String getMpeg7Time(Calendar c) {
        String t = c.get(Calendar.YEAR) + "-" + to2letters(c.get(Calendar.MONTH) + 1) + "-" + to2letters(c.get(Calendar.DAY_OF_MONTH))
                + "T" + to2letters(c.get(Calendar.HOUR_OF_DAY)) + ":" + to2letters(c.get(Calendar.MINUTE));
        return t;
    }

    public static String to2letters(int number) {
        String ret = null;
        // String min = null;
        if (number < 10)
            ret = ("0" + number);
        else
            ret = ("" + number);

        return ret;

    }

    public static File generateThumbnail(File f) {
        File thumb = null;
        try {
            String name = "tn_" + f.getName();
            if (!name.endsWith("jpg")) name = name.substring(0, name.lastIndexOf('.')) + ".jpg";
            String path = f.getParentFile().getCanonicalPath();
            thumb = new File(path + "/" + name);
            if (!thumb.exists()) {
                Image image;
                if (f.getName().toLowerCase().endsWith(".ppm")) {
                    image = PpmReader.read(new FileInputStream(f));
                } else if (f.getName().toLowerCase().endsWith(".bmp")) {
                    image = BmpReader.read(new FileInputStream(f));
                } else {
                    image = ImageIO.read(new FileInputStream(f));
                }
                int x, y;
                double factor;
                x = image.getWidth(null);
                y = image.getHeight(null);
                if (x > y) {
                    factor = ((double) MAX_THUMBNAIL_EDGE_LENGTH) / ((double) x);
                    x = MAX_THUMBNAIL_EDGE_LENGTH;
                    y = (int) Math.floor((double) y * factor);
                } else {
                    factor = ((double) MAX_THUMBNAIL_EDGE_LENGTH) / ((double) y);
                    x = (int) Math.floor((double) x * factor);
                    y = MAX_THUMBNAIL_EDGE_LENGTH;
                }
                BufferedImage thumbnailImage = new BufferedImage(x, y, BufferedImage.TYPE_INT_RGB);
                Graphics2D g2 = ((Graphics2D) thumbnailImage.getGraphics());
                g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
                g2.drawImage(image, 0, 0, x, y, null);
                // Find a jpeg writer
                ImageWriter writer = null;
                Iterator iter = ImageIO.getImageWritersByFormatName("jpg");
                if (iter.hasNext()) {
                    writer = (ImageWriter) iter.next();
                } else {
                    System.err.print("Didn't find JPEG Writer -> could not create thumbnail");
                }

                // Prepare output file
                ImageOutputStream ios = ImageIO.createImageOutputStream(thumb);
                writer.setOutput(ios);

                // Set the compression quality
                ImageWriteParam iwparam = writer.getDefaultWriteParam();
                iwparam.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
                iwparam.setCompressionQuality(0.9f);

                // Write the image
                writer.write(null, new IIOImage(thumbnailImage, null, null), iwparam);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return thumb;
    }

    public static String toMonthString(int monthID) {
        switch (monthID) {
            case 0:
                return "January";
            case 1:
                return "February";
            case 2:
                return "March";
            case 3:
                return "April";
            case 4:
                return "May";
            case 5:
                return "June";
            case 6:
                return "July";
            case 7:
                return "August";
            case 8:
                return "September";
            case 9:
                return "October";
            case 10:
                return "November";
            case 11:
                return "December";
            default:
                return "";
        }
    }

}
