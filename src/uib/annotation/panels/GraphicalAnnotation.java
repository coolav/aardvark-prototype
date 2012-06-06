/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package uib.annotation.panels;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.net.URISyntaxException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.*;
import java.util.*;
import java.io.*;
import java.net.URI;
import java.net.URL;
import javax.swing.border.EtchedBorder;
import org.jdom.*;
import org.jdom.input.*;
import org.jdom.output.*;
import uib.gui.AardvarkGui;
import uib.annotation.panels.dialogs.*;
import uib.annotation.model.*;
import uib.annotation.panels.util.ComponentFactory;
import uib.annotation.util.DataExchange;
import uib.annotation.util.mpeg7.Mpeg7FileFilter;
import uib.resource.OCToolkit;

/**
 *
 * @author Olav
 */
public class GraphicalAnnotation extends JPanel implements ActionListener, DataExchange {

    public final static String BASE_OBJECT_FILE =
            "../../resource/base-objects.mp7.xml";
    public final static String CRM_RELATIONS = "/uib/resource/semantic-relations.crm.xml";
    public final static String MPEG_7_RELATIONS =
            "../../resource/mpeg7_relations.xml";
    private SAXBuilder xmlBuilder;
    private AardvarkGui parent;
    private String[] relationsArray;
    public SemanticEntityTableModel entityTableModel;
    public RelationTableModel relationTableModel;
    public Mpeg7RelationsTableModel mpeg7RelationsTableModel;
    private ImageIcon deleteIcon, addIcon;
    private JSplitPane rightLeftSplitPane, topBottomSplitPane;
    protected JTable agentTable, eventTable, venueTable;
    public JTable entityTable;
    public JTable relationTable;
    private AnnotationPanel annotationPanel;
    private JSplitPane entityRealtionSplitPanel;
    URL relationsUrl = GraphicalAnnotation.class.getResource("../../resource/mpeg7_relations.xml");
    URL baseObjectsUrl = GraphicalAnnotation.class.getResource("../../resource/base-objects.mp7.xml");
    //JFrame parent;

    public GraphicalAnnotation() {
        super(new BorderLayout());

        /*
        try {
            for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    UIManager.setLookAndFeel(info.getClassName());
                    break;
                } else {
                    UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
                }
            }

        } catch (Exception e) {
            Logger.getLogger(AardvarkGui.class.getName()).log(Level.SEVERE, null, e);

        }*/

        xmlBuilder = new SAXBuilder();
        readRelations();
        initComponents();
        readBaseObjects();

    }

    private void initComponents() {

        entityTableModel = new SemanticEntityTableModel();
        relationTableModel = new RelationTableModel();
        //mpeg7RelationsTableModel = new Mpeg7RelationsTableModel(getRelations());

        relationTableModel.detachAll();
        relationTableModel.sort();
        entityTableModel.detachAll();
        entityTableModel.sort();
        entityTable = new DNDJTable(relationTableModel);
        entityTable.setTableHeader(null);
        entityTable.setShowGrid(false);
        relationTable = new DNDJTable(entityTableModel);
        relationTable.setTableHeader(null);
        relationTable.setShowGrid(false);


        relationTable.addMouseListener(new MouseAdapter() {

            long lastMs = System.currentTimeMillis();

            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getButton() == MouseEvent.BUTTON1) {
                    if (System.currentTimeMillis() - lastMs < 300) {
                        // edit agent ...
                        editAgent();
                    } else {
                        lastMs = System.currentTimeMillis();
                    }
                }
            }
        });

        deleteIcon = new ImageIcon(getClass().getResource("/uib/resource/delete_16px.png"));
        addIcon = new ImageIcon(getClass().getResource("/uib/resource/add.png"));
        Dimension minimumSize = new Dimension(150, 200);
        Dimension minimumPanelSize = new Dimension(600, 450);
        //EntityPanel
        JButton newEntityButton = new JButton();
        newEntityButton.addActionListener(this);
        newEntityButton.setActionCommand("newAgent");
        newEntityButton.setToolTipText("Create a new entity.");
        newEntityButton.setIcon(addIcon);

        JButton removeEntityButton = new JButton();
        removeEntityButton.addActionListener(this);
        removeEntityButton.setActionCommand("removeAgent");
        removeEntityButton.setToolTipText("Remove selected entity.");
        removeEntityButton.setIcon(deleteIcon);

        JPanel entityPanel = new JPanel(new BorderLayout());
        entityPanel.add(new JScrollPane(entityTable), BorderLayout.CENTER);
        JPanel entityPanelButtons = new JPanel();
        entityPanelButtons.add(newEntityButton);
        entityPanelButtons.add(removeEntityButton);
        entityPanel.add(entityPanelButtons, BorderLayout.SOUTH);
        entityPanel.setMinimumSize(minimumSize);
        entityPanel.setPreferredSize(minimumSize);

        //RelationPanel
        JButton newRelationButton = new JButton();
        newRelationButton.addActionListener(this);
        newRelationButton.setActionCommand("newObject");
        newRelationButton.setToolTipText("Create a new relation");
        newRelationButton.setIcon(addIcon);

        JButton removeRelationButton = new JButton();
        removeRelationButton.addActionListener(this);
        removeRelationButton.setActionCommand("removeObject");
        removeRelationButton.setToolTipText("Remove selected relation");
        removeRelationButton.setIcon(deleteIcon);

        JPanel relationPanel = new JPanel(new BorderLayout());
        relationPanel.add(new JScrollPane(relationTable), BorderLayout.CENTER);
        JPanel relationPanelButtons = new JPanel();
        relationPanelButtons.add(newRelationButton);
        relationPanelButtons.add(removeRelationButton);
        relationPanel.add(relationPanelButtons, BorderLayout.SOUTH);
        relationPanel.setMinimumSize(minimumSize);
        relationPanel.setPreferredSize(minimumSize);

        //Splitpanel for entities and realtions
        entityRealtionSplitPanel = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
        entityRealtionSplitPanel.setContinuousLayout(true);
        entityRealtionSplitPanel.setDividerSize(3);
        entityRealtionSplitPanel.setDividerLocation(0.5d);

        JPanel titledAgentPanel = ComponentFactory.createTitledPanel("Actors", entityPanel);
        JPanel titledEventPanel = ComponentFactory.createTitledPanel("Objects", relationPanel);
        titledAgentPanel.setBorder(BorderFactory.createEtchedBorder(EtchedBorder.RAISED));
        titledEventPanel.setBorder(BorderFactory.createEtchedBorder(EtchedBorder.RAISED));
        entityRealtionSplitPanel.add(titledAgentPanel, JSplitPane.TOP);
        entityRealtionSplitPanel.add(titledEventPanel, JSplitPane.BOTTOM);

        annotationPanel = new AnnotationPanel(this);
        annotationPanel.setMinimumSize(minimumPanelSize);
        annotationPanel.setPreferredSize(new Dimension(1000, 800));

        //Main splitpanel
        rightLeftSplitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        rightLeftSplitPane.setDividerSize(8);
        rightLeftSplitPane.setContinuousLayout(true);
        rightLeftSplitPane.setOneTouchExpandable(true);
        rightLeftSplitPane.setResizeWeight(1d);
        rightLeftSplitPane.add(annotationPanel, JSplitPane.LEFT);
        rightLeftSplitPane.add(entityRealtionSplitPanel, JSplitPane.RIGHT);
        rightLeftSplitPane.setDividerLocation(0.1d);
        //rightLeftSplitPane.setDividerLocation(rightLeftSplitPane.getSize().width
        //                     - rightLeftSplitPane.getInsets().right
        //                     - rightLeftSplitPane.getDividerSize()
        //                     - 150);
        this.add(rightLeftSplitPane, BorderLayout.CENTER);
        reArrange();

    }

    private void readRelations() {
        relationsArray = new String[1];
        relationsArray[0] = "no relation found";
        try {
            Document relDoc = xmlBuilder.build(OCToolkit.getRelationsFile());

            java.util.List relList = relDoc.getRootElement().getChildren();
            ArrayList<String> tmpRelationsVector = new ArrayList<String>();
            for (Object aRelList : relList) {
                Element elem = (Element) aRelList;
                String tmpRelationName = elem.getChildText("name");
                String tmpInverseRelationName = elem.getChildText("inverse");

                if (tmpRelationName != null) {
                    tmpRelationsVector.add(tmpRelationName);
                }
                if (tmpInverseRelationName != null) {
                    tmpRelationsVector.add(tmpInverseRelationName);
                }
            }
            relationsArray = new String[tmpRelationsVector.size()];
            tmpRelationsVector.toArray(relationsArray);
        } catch (JDOMException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        Arrays.sort(relationsArray);
    }

    public void actionPerformed(ActionEvent e) {
        if (e.getActionCommand().equals("createObjectFromAgent")) {
            if (relationTable.getSelectedRow() > -1) {
                // String s = (String) agentTable.getValueAt(agentTable.getSelectedRow(), 0);
                Element elem = relationTableModel.getNodeAt(relationTable.getSelectedRow());
                annotationPanel.addObject(new Point(20 + (int) (Math.random() * 100), 20 + (int) (Math.random() * 100)), elem);
            }

        } else if (e.getActionCommand().equals("export")) {
            exportXMLData(annotationPanel.createDocument());
        } else if (e.getActionCommand().equals("removeAgent")) {
            int[] indices = relationTable.getSelectedRows();
            Arrays.sort(indices);
            for (int i = 0; i < indices.length; i++) {
                int index = indices[indices.length - i - 1];   // von hinten loeschen, sonst stimmts nimmer
                relationTableModel.getAgents().remove(index);
            }
            relationTableModel.fireTableDataChanged();

        } else if (e.getActionCommand().equals("removeObject")) {
            int[] indices = entityTable.getSelectedRows();
            Arrays.sort(indices);
            for (int i = 0; i < indices.length; i++) {
                int index = indices[indices.length - i - 1];   // von hinten loeschen, sonst stimmts nimmer
                entityTableModel.getObjects().remove(index);
            }
            entityTableModel.fireTableDataChanged();
        } else if (e.getActionCommand().equals("newAgent")) {
            NewAgentDialog dialog = new NewAgentDialog(parent);
            dialog.pack();
            Dimension ss = Toolkit.getDefaultToolkit().getScreenSize();

            dialog.setLocation((ss.width - dialog.getWidth()) >> 1, (ss.height - dialog.getHeight()) >> 1);
            dialog.setVisible(true);

            if (dialog.createXML() != null) {
                relationTableModel.addAgent(dialog.createXML());
                relationTableModel.sort();
                relationTableModel.fireTableDataChanged();
            }
        } else if (e.getActionCommand().equals("importAgents")) {
            importAgents();

        } else if (e.getActionCommand().equals("importObjects")) {
            importObjects();

        } else if (e.getActionCommand().equals("newObject")) {
            String[] options = {"SemanticTime", "SemanticPlace", "Object"};
            JComboBox cbox = new JComboBox(options);
            int selection = JOptionPane.showConfirmDialog(parent, cbox, "Select Type", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
            if (selection == JOptionPane.OK_OPTION) {
                NewDescriptorDialogInterface descDialog = null;
                if (cbox.getSelectedIndex() == 0) {
                    descDialog = new NewTimeDialog(parent);
                } else if (cbox.getSelectedIndex() == 1) {
                    descDialog = new NewPlaceDialog(parent);
                } else if (cbox.getSelectedIndex() == 2) {
                    descDialog = new NewObjectDialog(parent);
                }
                JDialog dialog = ((JDialog) descDialog);
                dialog.pack();
                Dimension ss = Toolkit.getDefaultToolkit().getScreenSize();
                dialog.setLocation((ss.width - dialog.getWidth()) >> 1, (ss.height - dialog.getHeight()) >> 1);
                dialog.setVisible(true);

                if (descDialog.createXML() != null) {
                    entityTableModel.addObject(descDialog.createXML());
                    entityTableModel.sort();
                    entityTableModel.fireTableDataChanged();
                }
            }
        }

    }

    private void exportXMLData(Document data) {
        try {
            XMLOutputter outputter = new XMLOutputter(Format.getPrettyFormat());
            String strData = outputter.outputString(data);
            JFileChooser jfc = new JFileChooser(".");
            int returnVal = jfc.showSaveDialog(parent);
            if (returnVal == JFileChooser.APPROVE_OPTION) {
                FileOutputStream fout = new FileOutputStream(jfc.getSelectedFile());
                OutputStreamWriter stream_out = new OutputStreamWriter(fout, "UTF-8");
                stream_out.write(strData);
                stream_out.flush();
                stream_out.close();

            }
        } catch (IOException e) {

            e.printStackTrace();
        }
    }

    public ArrayList getRelation() {
        return relationTableModel.getAgents();
    }

    public ArrayList getEntities() {
        return entityTableModel.getObjects();
    }

    public ArrayList getPossibleObjects() {
        ArrayList ret = new ArrayList();
        ret.addAll(relationTableModel.getAgents());
        ret.addAll(entityTableModel.getObjects());
        return ret;
    }

    public Document getSemanticsDocument() {
        return annotationPanel.createDocument();
    }

    public void importAgents() {
        File f = getFile(".", new Mpeg7FileFilter());
        if (f != null) {
            relationTableModel.addAllAgents(retrieveNodes(f, "AgentObjectType"));
            relationTableModel.addAllAgents(retrieveNodes(f, "fsw:FSWPlayerType"));
            relationTableModel.addAllAgents(retrieveNodes(f, "fsw:FSWCoachType"));
            relationTableModel.addAllAgents(retrieveNodes(f, "fsw:FSWRefereeType"));
            relationTableModel.detachAll();
            relationTableModel.sort();
            relationTableModel.fireTableDataChanged();
        }
    }

    public void importObjects() {
        File f = getFile(".", new Mpeg7FileFilter());
        if (f != null) {
            entityTableModel.addAllObjects(retrieveNodes(f, "SemanticPlaceType"));
            entityTableModel.addAllObjects(retrieveNodes(f, "ObjectType"));
            entityTableModel.addAllObjects(retrieveNodes(f, "SemanticTimeType"));
            entityTableModel.detachAll();
            entityTableModel.sort();
            entityTableModel.fireTableDataChanged();
        }
    }

    private ArrayList<Element> retrieveNodes(File f, String type) {
        ArrayList<Element> v = new ArrayList<Element>();
        SAXBuilder builder = new SAXBuilder();
        try {
            Document agents = builder.build(f);
            Namespace mpeg7 = agents.getRootElement().getNamespace();
            Namespace xsi = Namespace.getNamespace("xsi", "http://www.w3.org/2001/XMLSchema-instance");
            java.util.List nodeList = agents.getRootElement().getChild("Description", mpeg7).getChild("Semantics", mpeg7).getChildren("SemanticBase", mpeg7);
            for (Iterator i = nodeList.iterator(); i.hasNext();) {
                Element e = (Element) i.next();
                if (e.getAttributeValue("type", xsi).equals(type)) {
                    v.add(e);
                }
            }
        } catch (JDOMException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return v;
    }

    private File getFile(String directory, javax.swing.filechooser.FileFilter filter) {
        File myFile = null;
        JFileChooser jfc = new JFileChooser(directory);
        jfc.setFileFilter(filter);
        if (jfc.showOpenDialog(parent) == JFileChooser.APPROVE_OPTION) {
            myFile = jfc.getSelectedFile();
        }
        return myFile;
    }

    public void addRelation(ArrayList v) {
        relationTableModel.addAllAgents(v);
        relationTableModel.sort();
        relationTableModel.fireTableDataChanged();
    }

    private JPopupMenu generateVenueMenu() {
        JPopupMenu menu = new JPopupMenu("Venues");
        Namespace mpeg7 = null;
        ArrayList venues = entityTableModel.getObjects();
        for (Iterator i = venues.iterator(); i.hasNext();) {
            Element e = (Element) i.next();
            String label = e.getChild("Label", e.getNamespace()).getChildTextTrim("Name", e.getNamespace());
            JMenuItem item = new JMenuItem(label);
            item.setActionCommand("addVenue-" + label);
            item.addActionListener(annotationPanel);
            menu.add(item);
        }
        return menu;
    }

    private void readBaseObjects() {
        try {
            Document d = xmlBuilder.build(OCToolkit.getBaseObjectsFile());

            if (d != null) {
                Element e = d.getRootElement();
                Namespace xsi = Namespace.getNamespace("xsi", "http://www.w3.org/2001/XMLSchema-instance");
                Namespace mpeg7 = e.getNamespace();
                Namespace fsw = OCToolkit.getFSWNamespace();

                java.util.List latt = e.getAttributes();
                for (Iterator it = latt.iterator(); it.hasNext();) {
                    Attribute attribute = (Attribute) it.next();
                    if (attribute.getNamespacePrefix().equals("xsi")) {
                        xsi = attribute.getNamespace();
                    }
                }
                if (!(e != null)) {
                }
                e = e.getChild("Description", mpeg7);
                e = e.getChild("Semantics", mpeg7);

                java.util.List l = e.getChildren();
                for (Iterator it = l.iterator(); it.hasNext();) {
                    Element tmpElement = (Element) it.next();
                    if (tmpElement.getName().equals("SemanticBase")
                            && tmpElement.getAttributeValue("type", xsi) != null) {
                        if (tmpElement.getAttributeValue("type", xsi).equals("AgentObjectType") || tmpElement.getAttributeValue("type", xsi).equals("fsw:FSWRefereeType")) {
                            relationTableModel.addAgent(tmpElement);
                        } else if (tmpElement.getAttributeValue("type", xsi).equals("SemanticPlaceType") || tmpElement.getAttributeValue("type", xsi).equals("ObjectType") || tmpElement.getAttributeValue("type", xsi).equals("SemanticTimeType")) {
                            entityTableModel.addObject(tmpElement);
                        }
                    } else {
//                        logger.debug(tmpElement + " has no matching attribute!");
                    }
                }
            } else {
            }
        } catch (Exception e1) {

            e1.printStackTrace();
        }
    }

    public void saveCatalog() {
        try {
            Document d = xmlBuilder.build(BASE_OBJECT_FILE);

            Element semantics = d.getRootElement();
            Namespace xsi = Namespace.getNamespace("xsi", "http://www.w3.org/2001/XMLSchema-instance");
            Namespace mpeg7 = semantics.getNamespace();

            semantics = semantics.getChild("Description", mpeg7).getChild("Semantics", mpeg7);
            semantics.removeContent();
            semantics.addContent(new Element("Label", mpeg7).addContent(new Element("Name", mpeg7).addContent("Semantic Folder")));

            for (Iterator iterator = relationTableModel.getAgents().iterator(); iterator.hasNext();) {
                Element elem = (Element) iterator.next();
                if (elem.getParent() != null) {
                    elem = (Element) elem.clone();
                }
                semantics.addContent(elem.detach());
            }
            for (Iterator iterator = entityTableModel.getObjects().iterator(); iterator.hasNext();) {
                Element elem = (Element) iterator.next();
                if (elem.getParent() != null) {
                    elem = (Element) elem.clone();
                }
                semantics.addContent(elem.detach());
            }

            FileOutputStream fos = new FileOutputStream(BASE_OBJECT_FILE);
            OutputStreamWriter osw = new OutputStreamWriter(fos, "utf-8");
            new XMLOutputter(Format.getPrettyFormat()).output(d, osw);
            osw.close();
            fos.close();
        } catch (JDOMException e) {
        } catch (IOException e) {
        }
    }

    public String[] getRelations() {
        return relationsArray;
    }

    public void setSemantics(Element node) {
        Namespace xsi = Namespace.getNamespace("xsi", "http://www.w3.org/2001/XMLSchema-instance");
        Namespace mpeg7 = node.getNamespace();
        java.util.List l = node.getChildren();
        for (Iterator it = l.iterator(); it.hasNext();) {
            Element tmpElement = (Element) it.next();
            if (tmpElement.getName().equals("SemanticBase") && tmpElement.getAttributeValue("type", xsi) != null) {
                if (tmpElement.getAttributeValue("type", xsi).equals("AgentObjectType") || tmpElement.getAttributeValue("type", xsi).equals("fsw:FSWRefereeType")) {
                    relationTableModel.addAgent(tmpElement);
                } else if (tmpElement.getAttributeValue("type", xsi).equals("SemanticPlaceType")
                        || tmpElement.getAttributeValue("type", xsi).equals("ObjectType")
                        || tmpElement.getAttributeValue("type", xsi).equals("SemanticTimeType")) {
                    entityTableModel.addObject(tmpElement);
                }
            }
        }
        annotationPanel.importNode(node);
        relationTableModel.detachAll();
        entityTableModel.detachAll();
        relationTableModel.sort();
        entityTableModel.sort();
        relationTableModel.fireTableDataChanged();
        entityTableModel.fireTableDataChanged();
    }

    public void addEntities(ArrayList v) {
        entityTableModel.addAllObjects(v);
        entityTableModel.sort();
        entityTableModel.fireTableDataChanged();
    }

    public String[] getSemanticAgentsNames() {
        return annotationPanel.getSemanticAgentsNames();
    }

    public String[] getSemanticEventsNames() {
        return annotationPanel.getSemanticEventsNames();
    }

    public String[] getSemanticPlacesNames() {
        return annotationPanel.getSemanticPlacesNames();
    }

    public String[] getSemanticTimesNames() {
        return annotationPanel.getSemanticTimesNames();

    }

    public JFrame getParentJFrame() {
        return parent;
    }

    public void reArrange() {
        rightLeftSplitPane.setDividerLocation(0.75d);
        entityRealtionSplitPanel.setDividerLocation(0.5d);
    }

    public void editAgent() {
        // TODO: add code here to edit the agent curently selected.
    }
}
