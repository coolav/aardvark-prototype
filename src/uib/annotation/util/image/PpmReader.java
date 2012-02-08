/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package uib.annotation.util.image;

import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;
import java.io.IOException;
import java.io.InputStream;
/**
 *
 * @author Olav
 */
public class PpmReader {

    public static BufferedImage read(InputStream in) throws IOException {
        String magicNumber = null;
        String comment = null;
        int height = -1, width = -1, maxVal = -1;

        magicNumber = getMagicNumer(in);
        width = getNumber(in);
        height = getNumber(in);
        maxVal = getNumber(in);

//        System.out.println("magicNumber = " + magicNumber);
//        System.out.println("width = " + width);
//        System.out.println("height = " + height);
//        System.out.println("maxVal = " + maxVal);

        if (maxVal > 255 || !magicNumber.equals("P6")) throw new IOException("Could not decode image!");

        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        WritableRaster r = image.getRaster();

        int[] rgb = new int[3];

        int count = 0;

        if (maxVal < 256) decode255(count, width, height, in, maxVal, rgb, r);
        else decode65535(count, width, height, in, maxVal, rgb, r);
        return image;
    }

    private static void decode255(int count, int width, int height, InputStream in, int maxVal, int[] rgb, WritableRaster r) throws IOException {
        int val;
        int numpixel;
        while (count < width * height * 3) {
            while ((val = in.read()) > -1) {
                // support for less 255 maxVal:
                if (maxVal < 255) val = val * 255 / maxVal;
                rgb[count % 3] = val;
                if (count % 3 == 2) {
                    numpixel = (count / 3);
                    try {
                        r.setPixel(numpixel % width, numpixel / width, rgb);
//                        System.out.println("Set pixel @ (" + numpixel % width + "," + numpixel / width + ")");
                    } catch (Exception e) {
                        System.err.println("Tried to set pixel @ (" + numpixel % width + "," + numpixel / width + ")");
                        e.printStackTrace();
                        throw new IOException("Could not decode image!");
                    }
                }
                count++;
            }
        }
    }

    private static void decode65535(int count, int width, int height, InputStream in, int maxVal, int[] rgb, WritableRaster r) throws IOException {
        int val, tmpVal;
        int numpixel;
        while (count < width * height * 3) {
            while ((val = in.read()) > -1) {
                tmpVal = val * 255;
                tmpVal += in.read();
                // support for less 65535 maxVal:
                if (maxVal < 65535) tmpVal = tmpVal * 65535 / maxVal;
                rgb[count % 3] = tmpVal;
                if (count % 3 == 2) {
                    numpixel = (count / 3);
                    try {
                        r.setPixel(numpixel % width, numpixel / width, rgb);
//                        System.out.println("Set pixel @ (" + numpixel % width + "," + numpixel / width + ")");
                    } catch (Exception e) {
                        System.err.println("Tried to set pixel @ (" + numpixel % width + "," + numpixel / width + ")");
                        e.printStackTrace();
                        throw new IOException("Could not decode image!");
                    }
                }
                count++;
            }
        }
    }

    private static int getNumber(InputStream in) throws IOException {
        StringBuilder sb = new StringBuilder(64);
        int val;
        char letter, lastLetter = '\n';

        while ((val = in.read()) > -1) {
            letter = (char) val;
            if (lastLetter == '\n' && letter == '#') {
                skipComment(in);
            } else if (!Character.isWhitespace(letter)) {
                sb.append(letter);
            } else if (Character.isWhitespace(lastLetter)) {
                // skip ..
            } else {
                return Integer.parseInt(sb.toString());
            }
            lastLetter = letter;
        }
        return 0;
    }

    private static String getMagicNumer(InputStream in) throws IOException {
        StringBuilder sb = new StringBuilder(64);
        int val;
        char letter, lastLetter = '0';

        while ((val = in.read()) > -1) {
            letter = (char) val;
            if (lastLetter == 'P') {
                sb.append(lastLetter);
                sb.append(letter);
                return sb.toString();
            } else if (lastLetter == '\n' && letter == '#') {
                skipComment(in);
            }
            lastLetter = letter;
        }
        return null;
    }

    private static void skipComment(InputStream in) throws IOException {
        StringBuilder sb = new StringBuilder(64);
        int val;
        char letter;
        sb.append('#');

        while ((val = in.read()) > -1) {
            letter = (char) val;
            if (letter != '\n') {
                sb.append(letter);
            } else {
//                System.out.println(sb.toString());
                return;
            }
        }
    }

}
