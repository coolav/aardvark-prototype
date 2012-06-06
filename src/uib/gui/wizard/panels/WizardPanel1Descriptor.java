/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package uib.gui.wizard.panels;

import uib.gui.wizard.WizardPanelDescriptor;
/**
 *
 * @author Olav
 */
public class WizardPanel1Descriptor extends WizardPanelDescriptor{
    public static final String IDENTIFIER = "INTRODUCTION_PANEL";
    
    public WizardPanel1Descriptor() {
        super(IDENTIFIER, new WizardPanel1());
    }
    
    @Override
    public Object getNextPanelDescriptor() {
        return WizardPanel2Descriptor.IDENTIFIER;
    }
    
    @Override
    public Object getBackPanelDescriptor() {
        return null;
    } 
    
}
