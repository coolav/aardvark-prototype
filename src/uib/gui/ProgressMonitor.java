/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package uib.gui;

import javax.swing.*;
/**
 *
 * @author Olav
 */
public class ProgressMonitor {

    JProgressBar pBar;

    public ProgressMonitor(JProgressBar progressBar){
        pBar = progressBar;
        progressBar.setMaximum(0);
        progressBar.setMaximum(100);
    }

    public int getProgressBar(){
        return pBar.getValue();
    }

    public void setProgress(){

    }
}
