/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package uib.gui.wizard.panels;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import uib.gui.wizard.WizardPanelDescriptor;

/**
 *
 * @author Olav
 */
public class WizardPanel2Descriptor extends WizardPanelDescriptor implements ActionListener{
    
    public static final String IDENTIFIER = "CONNECTOR_CHOOSE_PANEL";
    
    WizardPanel2 panel2;
    
    public WizardPanel2Descriptor() {
        
        panel2 = new WizardPanel2();
        panel2.addCheckBoxActionListener(this);
        
        setPanelDescriptorIdentifier(IDENTIFIER);
        setPanelComponent(panel2);
        
    }
    
    @Override
    public Object getNextPanelDescriptor() {
        return WizardPanel3Descriptor.IDENTIFIER;
    }
    
    @Override
    public Object getBackPanelDescriptor() {
        return WizardPanel1Descriptor.IDENTIFIER;
    }
    
    
    @Override
    public void aboutToDisplayPanel() {
        setNextButtonAccordingToCheckBox();
    }    

    @Override
    public void actionPerformed(ActionEvent e) {
        setNextButtonAccordingToCheckBox();
    }
            
    
    private void setNextButtonAccordingToCheckBox() {
         if (panel2.isCheckBoxSelected())
            getWizard().setNextFinishButtonEnabled(true);
         else
            getWizard().setNextFinishButtonEnabled(false);           
    
    }
    
}
