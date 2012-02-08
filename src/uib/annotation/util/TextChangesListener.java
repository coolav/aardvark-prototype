/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package uib.annotation.util;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import uib.gui.AardvarkGui;

/**
 *
 * @author Olav
 */
public class TextChangesListener extends KeyAdapter implements ActionListener, ChangeListener {
    private static TextChangesListener listener_ = null;
    private static AardvarkGui aframe_ = null;

    private TextChangesListener() {
        super();
    }

    public static TextChangesListener getInstance() {
        if (!(listener_ != null)) {
            listener_ = new TextChangesListener();
        }
        return listener_;
    }

    public static void createInstance(AardvarkGui frame) {
        aframe_ = frame;
        listener_ = new TextChangesListener();
    }

    public void keyTyped(KeyEvent e) {
        super.keyTyped(e);
        AardvarkGui.setDirty(true);
        if (aframe_.getTitle().indexOf("*") == -1) {
            aframe_.setTitle("* " + aframe_.getTitle());
        }
    }

    public void actionPerformed(ActionEvent e) {
        AardvarkGui.setDirty(true);
        if (aframe_.getTitle().indexOf("*") == -1) {
            aframe_.setTitle("* " + aframe_.getTitle());
        }
    }

    public void stateChanged(ChangeEvent e) {
        AardvarkGui.setDirty(true);
        if (aframe_.getTitle().indexOf("*") == -1) {
            aframe_.setTitle("* " + aframe_.getTitle());
        }
    }

    public void fireDataChanged() {
        AardvarkGui.setDirty(true);
        if (aframe_.getTitle().indexOf("*") == -1) {
            aframe_.setTitle("* " + aframe_.getTitle());
        }
    }

}
