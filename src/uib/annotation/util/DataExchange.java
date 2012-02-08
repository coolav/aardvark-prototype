/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package uib.annotation.util;

import java.util.ArrayList;

/**
 *
 * @author Olav
 */
public interface DataExchange {
    public static String[] COUNTRIES_CODE_3_LETTERS = {"arg", "bel", "bra", "chn", "crc", "den", "ger", "ecu", "eng", "fra", "irl", "ita", "jpn", "cmr", "cro", "mex", "nga", "par", "pol", "por", "kor", "rus", "ksa", "swe", "sen", "svn", "esp", "rsa", "tun", "tur", "uru", "usa"};
    // public static String[] ISO_COUNTRIES_CODE_3_LETTERS = {"arg", "bel", "bra", "chn", "crc", "den", "ger", "ecu", "eng", "fra", "irl", "ita", "jpn", "cmr", "cro", "mex", "nga", "par", "pol", "por", "kor", "rus", "ksa", "swe", "sen", "svn", "esp", "rsa", "tun", "tur", "uru", "usa"};
    public static String[] ISO_COUNTRIES_CODE_2_LETTERS = {"ar", "be", "br", "cn", "cr", "dk", "de", "ec", "en", "fr", "ie", "it", "jp", "cm", "hr", "mx", "ng", "py", "pl", "pt", "kr", "ru", "sa", "se", "sn", "si", "es", "za", "tn", "tr", "uy", "us"};

    public abstract ArrayList getRelation();

    public abstract void addRelation(ArrayList v);

    public abstract ArrayList getEntities();

    public abstract void addEntities(ArrayList v);

    public abstract ArrayList getPossibleObjects();

    public abstract String[] getRelations();
}
