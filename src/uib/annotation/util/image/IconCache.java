/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package uib.annotation.util.image;

import javax.swing.*;

/**
 *
 * @author Olav
 */
public class IconCache {



    private static IconCache instance = null;

    private ImageIcon agentIcon, placeIcon, timeIcon, objectIcon, eventIcon, helpIcon, removeIcon;
    private ImageIcon embedIcon, dbIcon, addIcon, saveAsIcon, iconImageFilePath;

    private IconCache() {
       agentIcon = new ImageIcon(getClass().getResource("/uib/resource/PlainPeople.gif"));
       eventIcon = new ImageIcon(getClass().getResource("/uib/resource/event.gif"));
       placeIcon = new ImageIcon(getClass().getResource("/uib/resource/place.gif"));
       timeIcon = new ImageIcon(getClass().getResource("/uib/resource/time.gif"));
       helpIcon = new ImageIcon(getClass().getResource("/uib/resource/linkto_help.gif"));
       objectIcon = new ImageIcon(getClass().getResource("/uib/resource/cube.png"));
       dbIcon = new ImageIcon(getClass().getResource("/uib/resource/db.png"));
       removeIcon = new ImageIcon(getClass().getResource("/uib/resource/delete_obj.gif"));
       embedIcon = new ImageIcon(getClass().getResource("/uib/resource/elements_obj.gif"));
       addIcon = new ImageIcon(getClass().getResource("/uib/resource/addgreen.png"));
       iconImageFilePath = new ImageIcon(getClass().getResource("/uib/resource/folder_16.png"));
    }

    public static IconCache getInstance() {
        if (instance == null) instance = new IconCache();
        return instance;
    }

    public ImageIcon getAgentIcon() {
        return agentIcon;
    }

    public ImageIcon getPlaceIcon() {
        return placeIcon;
    }

    public ImageIcon getTimeIcon() {
        return timeIcon;
    }

    public ImageIcon getObjectIcon() {
        return objectIcon;
    }

    public ImageIcon getEventIcon() {
        return eventIcon;
    }

    public ImageIcon getHelpIcon() {
        return helpIcon;
    }

    public ImageIcon getRemoveIcon() {
        return removeIcon;
    }

    public ImageIcon getEmbedIcon() {
        return embedIcon;
    }

    public ImageIcon getDbIcon() {
        return dbIcon;
    }

    public ImageIcon getAddIcon() {
        return addIcon;
    }

    public ImageIcon getSaveAsIcon(){
        return saveAsIcon;
    }
}



