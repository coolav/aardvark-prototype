/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package uib.gui.wizard;

import java.util.ArrayList;
import uib.gui.AardvarkGui;
import uib.gui.wizard.panels.WizardPanel1Descriptor;
import uib.gui.wizard.panels.WizardPanel2;
import uib.gui.wizard.panels.WizardPanel2Descriptor;
import uib.gui.wizard.panels.WizardPanel3Descriptor;

/**
 *
 * @author Olav
 */
public class InitializeWizard {

    private AardvarkGui parent;
    private String title;
    private ArrayList<String> queries;

    public void InittializeWizard(AardvarkGui parent, String title) {
        this.parent = parent;
        this.title = title;
        Wizard wizard = new Wizard(parent);
        wizard.getDialog().setTitle(title);
        WizardPanelDescriptor descriptor1 = new WizardPanel1Descriptor();
        wizard.registerWizardPanel(WizardPanel1Descriptor.IDENTIFIER, descriptor1);

        WizardPanelDescriptor descriptor2 = new WizardPanel2Descriptor();
        wizard.registerWizardPanel(WizardPanel2Descriptor.IDENTIFIER, descriptor2);

        WizardPanelDescriptor descriptor3 = new WizardPanel3Descriptor();
        wizard.registerWizardPanel(WizardPanel3Descriptor.IDENTIFIER, descriptor3);

        wizard.setCurrentPanel(WizardPanel1Descriptor.IDENTIFIER);

        int ret = wizard.showModalDialog();

        System.out.println("Dialog return code is (0=Finish,1=Cancel,2=Error): " + ret);
        System.out.println("Second panel selection is: "
                + (((WizardPanel2) descriptor2.getPanelComponent()).getRadioButtonSelected()));

        queries = new ArrayList<String>();

        queries.add("Find images depicting a building");
        queries.add("Find images depicting a forest");
        queries.add("Find images depicting a person");
        queries.add("Find images depicting water");
        queries.add("Find images depicting a tree");
        queries.add("Find images depicting a boat");
        queries.add("Find images depicting two or more people");
        queries.add("Find images depicting an urban setting");
        queries.add("Find images depicting a landscape");
        queries.add("Find images depicting a nautical theme");
        queries.add("Find images depicting a mountain scene");
        queries.add("Find images depicting boats on a body of water");
        queries.add("Find images depicting “The adoration of the magi”");
        queries.add("Find images depicting Paris, France");
        queries.add("Find images painted by Pierre-Auguste Renoir");
        queries.add("Find images categorized as a portrait");
        queries.add("Find images depicting a scene similar to image 24");
        queries.add("Find images depicting a scene similar to image 32");
        queries.add("Find images depicting Jesus Christ");
        queries.add("Find images that depict themes regarding war");
        queries.add("Find images depicting winter");
        queries.add("Find images depicting two or more people having fun");
        queries.add("Find images depicting summer");
        queries.add("Find images conveying sorrow");
        queries.add("Find images depicting solitude");


    }
}
