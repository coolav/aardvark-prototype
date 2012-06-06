/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package uib.annotation.panels;

import org.jdom.*;
import org.jdom.input.SAXBuilder;
import uib.annotation.model.SemanticEntityRepresentation;
import uib.annotation.model.SemanticRelationRepresentation;
import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.filechooser.FileFilter;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.*;
import java.util.List;
import uib.graphviz.*;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.datatransfer.*;
import java.awt.dnd.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.batik.svggen.*;
import uib.annotation.panels.dialogs.*;
import uib.annotation.util.DataExchange;
import uib.annotation.util.EmbedderThread;
import uib.annotation.util.image.IconCache;
import uib.annotation.util.mpeg7.Mpeg7FileFilter;
import uib.annotation.util.OCToolkit;
import uib.annotation.util.TextChangesListener;
import uib.annotation.util.XMLFileFilter;
import uib.gui.AardvarkGui;

/**
 *
 * @author Olav
 */
public class AnnotationPanel extends JPanel implements ActionListener {

    public LinkedList<EmbedderThread> running = new LinkedList<EmbedderThread>();
    public ArrayList<SemanticEntityRepresentation> objects;
    private ArrayList<SemanticRelationRepresentation> relations;
    public SemanticEntityRepresentation draggingObject, highlightedObject;
    private SemanticRelationRepresentation highlightedRelation;
    private Point pressedAt;
    private Point offset, clickedAt, startRel, endRel;
    // Logger logger = Logger.getLogger(BeePanel.class);
    private int state;
    private JPopupMenu menu;
    private JMenu newSemanticObject, exportDescription;
    private JMenuItem newAgent, newEvent, newPlace, newObject, newTime;
    private JMenuItem addObject, remObject, remRelation, distributeElements, cascadeElements, importFile, embedElements;
    private JMenuItem exportDescriptionJpg, exportDescriptionPng;
    private DataExchange parent;
    public BufferedImage logo, background;
    private DropTarget dt;
    private IconCache configuration = IconCache.getInstance();

    public AnnotationPanel(DataExchange parent) {
        super();
        this.parent = parent;
        initComponents();


    }

    private void initComponents() {
        dt = new DropTarget(this, new DropTargetAdapter() {

            public void drop(DropTargetDropEvent dtde) {
                Point p = dtde.getLocation();
                Transferable t = dtde.getTransferable();
                try {
                    String s = (String) t.getTransferData(DataFlavor.stringFlavor);
                    addObjectFromDND(p, s.trim());
                } catch (UnsupportedFlavorException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

        this.setDropTarget(dt);
        /*try {
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

        objects = new ArrayList<SemanticEntityRepresentation>();
        relations = new ArrayList<SemanticRelationRepresentation>();
        draggingObject = null;
        highlightedObject = null;
        highlightedRelation = null;
        pressedAt = null;

        menu = new JPopupMenu();
        remObject = new JMenuItem("Remove object");
        remObject.addActionListener(this);
        remObject.setActionCommand("remObject");
        JMenuItem remMarkedObject = new JMenuItem("Remove marked objects");
        remMarkedObject.setIcon(IconCache.getInstance().getRemoveIcon());
        remMarkedObject.addActionListener(this);
        remMarkedObject.setActionCommand("remMarked");
        remRelation = new JMenuItem("Remove relation");
        remRelation.addActionListener(this);
        remRelation.setActionCommand("remRelation");
        cascadeElements = new JMenuItem("Cascade elements");
        cascadeElements.addActionListener(this);
        cascadeElements.setActionCommand("cascade");
        distributeElements = new JMenuItem("Distribute elements");
        distributeElements.addActionListener(this);
        distributeElements.setActionCommand("distribute");
        embedElements = new JMenuItem("Embed elements");
        embedElements.addActionListener(this);
        embedElements.setActionCommand("embed");
        embedElements.setIcon(configuration.getEmbedIcon());
        importFile = new JMenuItem("Import description ...");
        importFile.addActionListener(this);
        importFile.setActionCommand("import");

        newAgent = createMenuItem("Person", "newA", this);
        newAgent.setIcon(IconCache.getInstance().getAgentIcon());
        newEvent = createMenuItem("Event", "newE", this);
        newEvent.setIcon(IconCache.getInstance().getEventIcon());
        newObject = createMenuItem("Object", "newO", this);
        newObject.setIcon(IconCache.getInstance().getObjectIcon());
        newPlace = createMenuItem("Place", "newP", this);
        newPlace.setIcon(IconCache.getInstance().getPlaceIcon());
        newTime = createMenuItem("Time", "newT", this);
        newTime.setIcon(IconCache.getInstance().getTimeIcon());

        exportDescription = new JMenu("Export description");
        exportDescription.setIcon(IconCache.getInstance().getSaveAsIcon());
        exportDescriptionJpg = createMenuItem("JPG image ...", "exportJpg", this);
        exportDescriptionPng = createMenuItem("PNG image ...", "exportPng", this);

        exportDescription.add(exportDescriptionJpg);
        exportDescription.add(exportDescriptionPng);


        newSemanticObject = new JMenu("New Semantic ...");
        newSemanticObject.add(newAgent);
        newSemanticObject.add(newEvent);
        newSemanticObject.add(newPlace);
        newSemanticObject.add(newTime);
        newSemanticObject.add(newObject);

        menu.add(newSemanticObject);
        menu.add(remObject);
        menu.add(remMarkedObject);
        // menu.addSeparator();
        menu.add(remRelation);
        menu.addSeparator();
        menu.add(cascadeElements);
        menu.add(distributeElements);
        menu.add(embedElements);
        menu.add(exportDescription);
        menu.add(importFile);
        menu.addSeparator();
        JMenuItem helpMenuItem = createMenuItem("Help", "help", this);
        helpMenuItem.setIcon(configuration.getHelpIcon());
        menu.add(helpMenuItem);

        this.addComponentListener(new ComponentAdapter() {

            @Override
            public void componentResized(ComponentEvent e) {
                background = null;
                super.componentResized(e);

            }
        });

        this.addMouseMotionListener(new MouseMotionAdapter() {

            @Override
            public void mouseMoved(MouseEvent e) {
                checkIfIsInObject(e.getPoint());
                repaint();
            }

            @Override
            public void mouseDragged(MouseEvent e) {
                if (pressedAt != null && draggingObject != null) {
                    Point p = draggingObject.getP();
                    p.setLocation(e.getPoint().x + offset.x, e.getPoint().y + offset.y);
                    if (e.getPoint().x > 0 && e.getPoint().y > 0 && e.getPoint().x
                            < getWidth() && e.getPoint().y
                            < getHeight()) {
                        draggingObject.setP(p);
                    }
                } else if (startRel != null) {
                    endRel = e.getPoint();
                }
                checkIfIsInObject(e.getPoint());
                repaint();
            }
        });

        this.addMouseListener(new BeePanelMouseAdapter());
        background = null;
    }

    public Document createDocument() {
        Namespace mpeg7 = Namespace.getNamespace("", "urn:mpeg:mpeg7:schema:2001");
        Namespace xsi = Namespace.getNamespace("xsi", "http://www.w3.org/2001/XMLSchema-configuration");
        Namespace fsw = OCToolkit.getFSWNamespace();
        Element root = new Element("Mpeg7", mpeg7);
        root.setAttribute("schemaLocation", "urn:mpeg:mpeg7:schema:2001 Mpeg7-2001.xsd http://www.at.know-center.at/fsw Know-soccer.xsd", xsi);
        root.addNamespaceDeclaration(mpeg7);
        root.addNamespaceDeclaration(xsi);
        root.addNamespaceDeclaration(fsw);
        Document d = new Document(root);
        // Description
        Element description = new Element("Description", mpeg7);
        description.setAttribute("type", "SemanticDescriptionType", xsi);
        root.addContent(description);
        // Semantics
        Element semantics = new Element("Semantics", mpeg7);
        semantics.addContent(new Element("Label", mpeg7).addContent(new Element("Name", mpeg7).addContent("")));
        description.addContent(semantics);
        HashMap<SemanticEntityRepresentation, String> h = new HashMap<SemanticEntityRepresentation, String>();
        int count = 0;
        for (Iterator<SemanticEntityRepresentation> iterator = objects.iterator(); iterator.hasNext();) {
            SemanticEntityRepresentation r = iterator.next();
            count++;
            String id = "id_" + count;
            h.put(r, id);
            Element node = (Element) r.getNode().clone();
            node.setAttribute("id", id);
            semantics.addContent(node);
        }
        Element graph = new Element("Graph", mpeg7);
        for (Iterator<SemanticRelationRepresentation> it = relations.iterator(); it.hasNext();) {
            SemanticRelationRepresentation r = it.next();
            Element tmpElement = new Element("Relation", mpeg7);
            tmpElement.setAttribute("type", "urn:mpeg:mpeg7:cs:SemanticRelationCS:2001:" + r.getLabel());
            tmpElement.setAttribute("source", "#" + h.get(r.getSource()));
            tmpElement.setAttribute("target", "#" + h.get(r.getTarget()));
            graph.addContent(tmpElement);
        }
        semantics.addContent(graph);
        return d;
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        if (!(background != null)) {
            background = generateBackground();
        }

        g2.clearRect(0, 0, getWidth(), getHeight());
        g2.drawImage(background, 0, 0, this);

        g2.setFont(Font.decode("Verdana-11"));

        paintSemanticDescription(g2);

        g2.drawImage(IconCache.getInstance().getHelpIcon().getImage(), getWidth() - 22, 6, null);
    }

    private void paintSemanticDescription(Graphics2D g2) {
        for (Object object : objects) {
            SemanticEntityRepresentation sem = (SemanticEntityRepresentation) object;
            sem.drawObject(g2);
        }

        for (Object relation1 : relations) {
            SemanticRelationRepresentation relation = (SemanticRelationRepresentation) relation1;
            relation.drawRelation(g2);
        }
        if (startRel != null && endRel != null) {
            g2.drawLine(startRel.x, startRel.y, endRel.x, endRel.y);
        }
    }

    public void addObject(Point p) {
        String label = JOptionPane.showInputDialog("Please give a name for the new object:");
        objects.add(new SemanticEntityRepresentation(p, label));
        TextChangesListener.getInstance().fireDataChanged();
        repaint();
        embedElements();
    }

    public void addObject(Point p, String label) {
        objects.add(new SemanticEntityRepresentation(p, label));
        TextChangesListener.getInstance().fireDataChanged();
        repaint();
        embedElements();
    }

    public void addObject(Point p, Element node) {
        objects.add(new SemanticEntityRepresentation(p, node));
        TextChangesListener.getInstance().fireDataChanged();
        repaint();
        embedElements();
    }

    public void checkIfIsInObject(Point p) {
        highlightedObject = null;
        highlightedRelation = null;
        for (SemanticEntityRepresentation sem : objects) {
            if (sem.checkIfInside(p)) {
                highlightedObject = sem;
            }
        }
        for (SemanticRelationRepresentation rel : relations) {
            if (rel.contains(p)) {
                highlightedRelation = rel;
            }
        }
    }

    public void checkIfPressedInObject(Point p) {
        for (SemanticEntityRepresentation sem : objects) {
            if (sem.checkIfInside(p)) {
                draggingObject = sem;
                offset = new Point(sem.getP().x - pressedAt.x, sem.getP().y - pressedAt.y);
            }
        }
    }

    public void showMenu(int x, int y) {
        if (highlightedObject != null) {
            remObject.setEnabled(true);
        } else {
            remObject.setEnabled(false);
        }
        if (highlightedRelation != null) {
            remRelation.setEnabled(true);
        } else {
            remRelation.setEnabled(false);
        }
        menu.show(this, x, y);
    }

    private void remObject(Point p) {
        Object toremove = null;
        for (SemanticEntityRepresentation sem : objects) {
            if (sem.checkIfInside(p)) {
                toremove = sem;
            }
        }
        if (toremove != null) {
            ArrayList<SemanticRelationRepresentation> rem = new ArrayList<SemanticRelationRepresentation>();
            for (SemanticRelationRepresentation rel : relations) {
                if (rel.getSource().equals(toremove) || rel.getTarget().equals(toremove)) {
                    rem.add(rel);
                }
            }
            objects.remove(toremove);
            for (SemanticRelationRepresentation aRem : rem) {
                relations.remove(aRem);
            }
            TextChangesListener.getInstance().fireDataChanged();
        }
    }

    private void remRelation(Point p) {

        if (highlightedRelation != null) {
            relations.remove(highlightedRelation);
            TextChangesListener.getInstance().fireDataChanged();
        }
    }

    private void addRelation(SemanticEntityRepresentation src, SemanticEntityRepresentation target) {
        Object label = JOptionPane.showInputDialog(this, "Please specify relation:", "Add semantic relation",
                JOptionPane.PLAIN_MESSAGE, null, parent.getRelations(), parent.getRelations()[0]);
        if (label != null) {
            relations.add(new SemanticRelationRepresentation(src, target, label.toString()));
            TextChangesListener.getInstance().fireDataChanged();
            embedElements();
        }
    }

    private void addRelation(SemanticEntityRepresentation src, SemanticEntityRepresentation target, String label) {
        relations.add(new SemanticRelationRepresentation(src, target, label));
        TextChangesListener.getInstance().fireDataChanged();
        embedElements();
    }

    public void actionPerformed(ActionEvent e) {
        if (e.getActionCommand().equals("addObject")) {
            addObject(clickedAt);
        } else if (e.getActionCommand().equals("newA")) {
            GraphicalAnnotation graphicalAnnotation = ((GraphicalAnnotation) parent);
            NewAgentDialog dialog = new NewAgentDialog(graphicalAnnotation.getParentJFrame());
            dialog.pack();
            Dimension ss = Toolkit.getDefaultToolkit().getScreenSize();

            dialog.setLocation((ss.width - dialog.getWidth()) >> 1, (ss.height - dialog.getHeight()) >> 1);
            dialog.setVisible(true);

            if (dialog.createXML() != null) {
                Element element = dialog.createXML();
                graphicalAnnotation.relationTableModel.addAgent(element);
                graphicalAnnotation.relationTableModel.sort();
                graphicalAnnotation.relationTableModel.fireTableDataChanged();
                addObject(new Point(20 + (int) (Math.random() * 100), 20 + (int) (Math.random() * 100)), element);
            }

        } else if (e.getActionCommand().equals("newO")) {
            GraphicalAnnotation graphicalAnnotation = ((GraphicalAnnotation) parent);
            NewDescriptorDialogInterface descDialog = new NewObjectDialog(graphicalAnnotation.getParentJFrame());
            createNewObject(descDialog, graphicalAnnotation);
        } else if (e.getActionCommand().equals("newP")) {
            GraphicalAnnotation graphicalAnnotation = ((GraphicalAnnotation) parent);
            NewDescriptorDialogInterface descDialog = new NewPlaceDialog(graphicalAnnotation.getParentJFrame());
            createNewObject(descDialog, graphicalAnnotation);
        } else if (e.getActionCommand().equals("newT")) {
            GraphicalAnnotation graphicalAnnotation = ((GraphicalAnnotation) parent);
            NewDescriptorDialogInterface descDialog = new NewTimeDialog(graphicalAnnotation.getParentJFrame());
            createNewObject(descDialog, graphicalAnnotation);
        } else if (e.getActionCommand().equals("help")) {
            showHelp();
        } else if (e.getActionCommand().equals("remObject")) {
            remObject(clickedAt);
        } else if (e.getActionCommand().equals("remMarked")) {
            removeMarkedElements();
        } else if (e.getActionCommand().equals("remRelation")) {
            remRelation(clickedAt);
        } else if (e.getActionCommand().equals("cascade")) {
            cascadeElements();
        } else if (e.getActionCommand().equals("distribute")) {
            distributeElements();
        } else if (e.getActionCommand().equals("embed")) {
            embedElements();
        } else if (e.getActionCommand().equals("exportJpg")) {
            String formatName = "jpg";
            saveSemanticDescriptionToFile(formatName);
        } else if (e.getActionCommand().equals("exportPng")) {
            String formatName = "png";
            saveSemanticDescriptionToFile(formatName);
        } else if (e.getActionCommand().equals("import")) {
            importFile();
        } else if (e.getActionCommand().startsWith("addVenue-")) {
//            System.out.println(e.getActionCommand());
            String label = e.getActionCommand().substring(9);
            ArrayList a = parent.getPossibleObjects();
            for (Object anA : a) {
                Element elem = (Element) anA;
                if (elem.getChild("Label", elem.getNamespace()).getChildText("Name", elem.getNamespace()).equals(label)) {
                    addObject(new Point(10, 10), elem);
                }
            }
        }

    }

    private void saveSemanticDescriptionToFile(String formatName) {
        JFileChooser jfc = new JFileChooser();
        int jfcReturnCode = jfc.showSaveDialog(this);
        if (jfcReturnCode == JFileChooser.APPROVE_OPTION) {
            boolean writeFile = false;
            if (jfc.getSelectedFile().exists()) {
                int overwriteReturnCode = JOptionPane.showConfirmDialog(this, "File already exists! Do you want to overwrite the existing file?",
                        "Overwrite file?", JOptionPane.OK_CANCEL_OPTION);
                if (overwriteReturnCode == JOptionPane.OK_OPTION) {
                    writeFile = true;
                }
            } else {
                writeFile = true;
            }
            if (writeFile) {
                if (!formatName.equals("svg")) {
                    saveRasterizedFile(formatName, jfc.getSelectedFile());
                }
            }
        }
    }

    private void saveRasterizedFile(String formatName, File file) {
        BufferedImage img;
        if (formatName.equals("png")) {
            img = new BufferedImage(getWidth(), getHeight(), BufferedImage.TYPE_INT_ARGB);
        } else {
            img = new BufferedImage(getWidth(), getHeight(), BufferedImage.TYPE_INT_RGB);
        }
        Graphics2D g2 = ((Graphics2D) img.getGraphics());
        if (!formatName.equals("png")) {
            g2.clearRect(0, 0, this.getWidth(), this.getHeight());
            g2.drawImage(background, 0, 0, this);
        }
        g2.setFont(Font.decode("Verdana-10"));
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        paintSemanticDescription(g2);
        try {
            ImageIO.write(img, formatName, file);
        } catch (IOException e1) {
            JOptionPane.showMessageDialog(this, "Could not save to file:\n" + e1.toString(),
                    "Error saving to file", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Shows basic help for using this panel.
     */
    public void showHelp() {
        String helpString = "Drag and drop objects from the tables on the right hand side to this drawing panel.\n"
                + "Use <alt>+<left mouse button> or <middle mouse button> to draw relations between objects.\n"
                + "<ctrl>+<left mouse button> selects nodes, with <right mouse button> a context menu is shown.";
        JOptionPane.showMessageDialog(this, helpString, "Help", JOptionPane.INFORMATION_MESSAGE);
    }

    private void createNewObject(NewDescriptorDialogInterface descDialog, GraphicalAnnotation graphicalAnnotation) {
        JDialog dialog = ((JDialog) descDialog);
        dialog.pack();
        Dimension ss = Toolkit.getDefaultToolkit().getScreenSize();
        dialog.setLocation((ss.width - dialog.getWidth()) >> 1, (ss.height - dialog.getHeight()) >> 1);
        dialog.setVisible(true);

        if (descDialog.createXML() != null) {
            Element element = descDialog.createXML();
            graphicalAnnotation.entityTableModel.addObject(element);
            graphicalAnnotation.entityTableModel.sort();
            graphicalAnnotation.entityTableModel.fireTableDataChanged();
            addObject(new Point(20 + (int) (Math.random() * 100), 20 + (int) (Math.random() * 100)), element);
        }
    }

    public void checkIfNewRelation() {
        SemanticEntityRepresentation src = null;
        SemanticEntityRepresentation tgt = null;
        for (Iterator<SemanticEntityRepresentation> iterator = objects.iterator(); iterator.hasNext();) {
            SemanticEntityRepresentation sem = iterator.next();
            if (sem.checkIfInside(startRel)) {
                src = sem;
            }
            if (sem.checkIfInside(endRel)) {
                tgt = sem;
            }
        }
        if (src != null && tgt != null) {
            addRelation(src, tgt);
        }
    }

    private void cascadeElements() {
        int x = 30, y = 30;
        for (Iterator<SemanticEntityRepresentation> iterator = objects.iterator(); iterator.hasNext();) {
            SemanticEntityRepresentation sem = iterator.next();
            Point p = sem.getP();
            p.setLocation(x, y);
            sem.setP(p);
            x += 40;
            y += 40;
        }
    }

    private BufferedImage generateBackground() {
        BufferedImage tmp = new BufferedImage(getWidth(), getHeight(), BufferedImage.TYPE_INT_RGB);
        Graphics2D g2 = (Graphics2D) tmp.getGraphics();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setColor(Color.black);
        g2.fillRect(0, 0, getWidth(), getHeight());
        g2.setColor(new Color(245, 245, 245));
        g2.fillRect(2, 2, getWidth() - 4, getHeight() - 4);

        // draw Help:
        g2.setColor(Color.black);
//        g2.setFont(g2.getFont().deriveFont(Font.ITALIC, 10f));
//        g2.drawString("Help:", 10, this.getHeight() - 38);
//        g2.drawString("Drag and drop objects from the tables on the right hand side.", 10, this.getHeight() - 24);
//        g2.drawString("<alt>+<left mouse button> or <middle mouse button> to draw relations.", 10, this.getHeight() - 10);

//        g2.drawImage(logo, this.getWidth() - logo.getWidth() - 11, this.getHeight() - logo.getHeight() - 11, this);
        return tmp;
    }

    /**
     * Creates list of all names of semantic agents currently attached to the
     * semantic description.
     *
     * @return list of names
     */
    public String[] getSemanticAgentsNames() {
        return getObjectNamesOfType("AgentObjectType");
    }

    /**
     * Creates list of all names of semantic events currently attached to the
     * semantic description.
     *
     * @return list of names
     */
    public String[] getSemanticEventsNames() {
        return getObjectNamesOfType("EventType");
    }

    /**
     * Creates list of all names of semantic times currently attached to the
     * semantic description.
     *
     * @return list of names
     */
    public String[] getSemanticTimesNames() {
        return getObjectNamesOfType("SemanticTimeType");
    }

    /**
     * Creates list of all names of semantic places currently attached to the
     * semantic description.
     *
     * @return list of names
     */
    public String[] getSemanticPlacesNames() {
        return getObjectNamesOfType("SemanticPlaceType");
    }

    private String[] getObjectNamesOfType(String type) {
        Namespace xsi;
        xsi = Namespace.getNamespace("xsi", "http://www.w3.org/2001/XMLSchema-configuration");

        String[] result;
        LinkedList<String> l = new LinkedList<String>();
        for (SemanticEntityRepresentation r : objects) {
            Element elem = r.getNode();
            if (elem.getAttribute("type", xsi).getValue().startsWith(type)) {
                l.add(r.getLabel());
            }
        }
        result = new String[l.size()];
        int i = 0;
        for (Iterator<String> iterator = l.iterator(); iterator.hasNext();) {
            String s = iterator.next();
            result[i] = s;
            i++;
        }
        return result;
    }

    public void importNode(Element node) {
        Namespace mpeg7 = Namespace.getNamespace("", "urn:mpeg:mpeg7:schema:2001");
        if (node.getChild("SemanticBase", mpeg7) != null) { // are there any useable elements?
            // simply add all Elements ...
            objects = new ArrayList<SemanticEntityRepresentation>();
            relations = new ArrayList<SemanticRelationRepresentation>();
            java.util.List oList = node.getChildren("SemanticBase", mpeg7);
            for (Object anOList : oList) {
                Element e = (Element) anOList;
                SemanticEntityRepresentation sor = new SemanticEntityRepresentation(new Point(10, 10), e);
                objects.add(sor);
            }

            // now create the graph ...
            if (node.getChild("Graph", node.getNamespace()) != null) {
                if (node.getChild("Graph", node.getNamespace()).getChild("Relation", node.getNamespace()) != null) {
                    oList = node.getChild("Graph", node.getNamespace()).getChildren("Relation", node.getNamespace());
                    for (Iterator i = oList.iterator(); i.hasNext();) {
                        Element e = (Element) i.next();
                        String src = e.getAttributeValue("source");
                        String tgt = e.getAttributeValue("target");
                        String rel = e.getAttributeValue("type");
                        //logger.debug("Searching for relation source=" + src + ", target=" + tgt + ", relation=" + rel);
                        SemanticEntityRepresentation sort = null, sors = null;
                        if (rel != null && rel.lastIndexOf(":") > -1) {
                            rel = rel.substring(rel.lastIndexOf(":") + 1);
                        } else {
                            // logger.error("Import: no relation found");
                        }
                        if (src != null && tgt != null) {
                            src = src.substring(1);
                            tgt = tgt.substring(1);
                            for (Iterator<SemanticEntityRepresentation> it = objects.iterator(); it.hasNext();) {
                                SemanticEntityRepresentation rep = it.next();
                                String oID = rep.getNode().getAttributeValue("id");
                                if (oID.equals(tgt)) {
                                    sort = rep;
                                }
                                if (oID.equals(src)) {
                                    sors = rep;
                                }
                            }
                        } else {
                            // logger.error("Import: no source or target found");
                        }
                        if (sort != null && sors != null && rel != null) {
                            SemanticRelationRepresentation srr = new SemanticRelationRepresentation(sors, sort, rel);
                            relations.add(srr);
                        } else {
                            // logger.error("Import: Could not import: " + e.toString());
                        }
                    }
                }
            }
            distributeElements();
            repaint();
            embedElements();
        } else { // no description found
            // logger.error("Import: No MPEG-7 description available in given node ...");
        }
    }

    public void addObjectFromDND(Point p, String label) {
        if (label.indexOf('\n') == -1) {
            ArrayList a = parent.getPossibleObjects();
            for (Iterator i = a.iterator(); i.hasNext();) {
                Element e = (Element) i.next();
                if (e.getChild("Label", e.getNamespace()).getChildText("Name", e.getNamespace()).equals(label)) {
                    addObject(p, e);
                }
            }
        }
        TextChangesListener.getInstance().fireDataChanged();
    }

    public void distributeElements() {
        int number = objects.size();
        if (number > 1) {
            int width = this.getWidth();
            int height = this.getHeight();
            double max = 0.0;

            if (width > height) {
                max = (double) ((height >> 1) - SemanticEntityRepresentation.HEIGHT) - 10.0;
            } else {
                max = (double) ((width >> 1) - SemanticEntityRepresentation.WIDTH) - 10.0;
            }
            int step = 0;
            double split = 2.0 * Math.PI / (double) number;
            for (Iterator<SemanticEntityRepresentation> i = objects.iterator(); i.hasNext();) {
                SemanticEntityRepresentation r = i.next();
                Point p = new Point((width >> 1) + ((int) (max * Math.cos(((double) step) * split))) - (SemanticEntityRepresentation.WIDTH >> 1),
                        (height >> 1) + ((int) (max * Math.sin(((double) step) * split))) - (SemanticEntityRepresentation.HEIGHT >> 1));
                r.setP(p);
                step++;
            }
        } else if (number > 0) {
            SemanticEntityRepresentation r = objects.get(0);
            Point p = new Point(getWidth() / 2, getHeight() / 2);
            r.setP(p);
        }
    }

    public void embedElements() {
        // getting maximum for scaling to [0,1]^2:
        if (objects.size() > 4) {
            double maxX = 1.0, minX = 0.0, maxY = 1.0, minY = 0.0;
            for (Iterator<SemanticEntityRepresentation> itObj = objects.iterator(); itObj.hasNext();) {
                SemanticEntityRepresentation sor = itObj.next();
                double x = sor.getP().getX();
                double y = sor.getP().getY();
                if (x < minX) {
                    minX = x;
                }
                if (y < minY) {
                    minY = y;
                }
                if (x > maxX) {
                    maxX = x;
                }
                if (y > maxY) {
                    maxY = y;
                }
            }
            // creating nodelist
            List<Node> nodeList = new LinkedList<Node>();
            HashMap<SemanticEntityRepresentation, DefaultNode> lookupNodes = new HashMap<SemanticEntityRepresentation, DefaultNode>(objects.size());
            for (Iterator<SemanticEntityRepresentation> itObj = objects.iterator(); itObj.hasNext();) {
                SemanticEntityRepresentation sor = itObj.next();
                sor.getP().getX();
                double x = sor.getP().getX();
                double y = sor.getP().getY();
                x = (x - minX) / (maxX - minX);
                y = (y - minY) / (maxY - minY);
                DefaultNode d = new DefaultNode(x, y, sor);
                nodeList.add(d);
                lookupNodes.put(sor, d);
            }
            List<Edge> edges = new LinkedList<Edge>();
            for (Iterator<SemanticRelationRepresentation> itRel = relations.iterator(); itRel.hasNext();) {
                SemanticRelationRepresentation rel = itRel.next();
                DefaultEdge e = new DefaultEdge(lookupNodes.get(rel.getSource()), lookupNodes.get(rel.getTarget()));
                edges.add(e);
            }
            SpringEmbedder se = new SpringEmbedder(nodeList, edges);
            // todo: embedGraph thread
            EmbedderThread embedderThread = new EmbedderThread(this, se, running);
            Thread t = new Thread(embedderThread);

            t.start();
        } else {
            distributeElements();
        }
    }

    public void importFile() {
        JFileChooser jfc = new JFileChooser(".");
        FileFilter std = jfc.getFileFilter();
        jfc.setFileFilter(new Mpeg7FileFilter());
        jfc.addChoosableFileFilter(new XMLFileFilter());
        jfc.addChoosableFileFilter(std);
        if (jfc.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            SAXBuilder builder = new SAXBuilder();
            try {
                Document d = builder.build(jfc.getSelectedFile());
                java.util.List l = OCToolkit.xpathQuery(d, "//SemanticBase", Namespace.getNamespace("", "urn:mpeg:mpeg7:schema:2001"));
                if (l != null) {
                    Element node = (Element) ((Element) ((Element) l.get(0)).getParent()).detach();
                    importNode(node);
                } else {
                    // logger.error("Import: No matching node found in file");
                }
            } catch (JDOMException e) {
                // logger.error(e);
            } catch (IOException e) {
            }
        }
    }

    public void removeMarkedElements() {

        ArrayList<SemanticEntityRepresentation> remO = new ArrayList<SemanticEntityRepresentation>();
        for (Iterator<SemanticEntityRepresentation> iterator = objects.iterator(); iterator.hasNext();) {
            SemanticEntityRepresentation sem = iterator.next();
            if (sem.isMarked()) {
                remO.add(sem);
                ArrayList<SemanticRelationRepresentation> rem = new ArrayList<SemanticRelationRepresentation>();
                for (Iterator<SemanticRelationRepresentation> i2 = relations.iterator(); i2.hasNext();) {
                    SemanticRelationRepresentation rel = i2.next();
                    if (rel.getSource().equals(sem) || rel.getTarget().equals(sem)) {
                        rem.add(rel);
                    }
                }
//                objects.remove(sem);
                for (Iterator<SemanticRelationRepresentation> i3 = rem.iterator(); i3.hasNext();) {
                    relations.remove(i3.next());
                }
            }
        }
        for (Iterator<SemanticEntityRepresentation> iterator = remO.iterator(); iterator.hasNext();) {
            SemanticEntityRepresentation sem = iterator.next();
            objects.remove(sem);
        }
        TextChangesListener.getInstance().fireDataChanged();
    }

    public class BeePanelMouseAdapter extends MouseAdapter {

        @Override
        public void mousePressed(MouseEvent e) {
            if (running.size() > 0) {
                for (EmbedderThread embedderThread : running) {
                    embedderThread.endEmbedding();
                }
            }
            if (e.getButton() == MouseEvent.BUTTON1 && !e.isAltDown()) {
                pressedAt = e.getPoint();
                checkIfPressedInObject(e.getPoint());
            }
            if (e.getButton() == MouseEvent.BUTTON2 || e.getButton() == MouseEvent.BUTTON1 && e.isAltDown()) {
                startRel = e.getPoint();
            }
            repaint();
        }

        @Override
        public void mouseReleased(MouseEvent e) {
            checkIfIsInObject(e.getPoint());
            draggingObject = null;
            pressedAt = null;
            offset = null;
            repaint();
            if (startRel != null) {
                //            if (e.getButton() == MouseEvent.BUTTON2 || e.getButton() == MouseEvent.BUTTON1 && e.isAltDown()) {
                endRel = e.getPoint();
                checkIfNewRelation();
                startRel = null;
                endRel = null;
            }
        }

        @Override
        public void mouseClicked(MouseEvent e) {
            clickedAt = e.getPoint();
            if (e.getButton() == MouseEvent.BUTTON1 && e.getX() > getWidth() - 24 && e.getY() < 24) {
                showHelp();
            }
            if (e.getButton() == MouseEvent.BUTTON3) {
                showMenu(e.getPoint().x, e.getPoint().y);
            }
            if (e.getButton() == MouseEvent.BUTTON1 && e.isControlDown()) {
                for (Iterator<SemanticEntityRepresentation> iterator = objects.iterator(); iterator.hasNext();) {
                    SemanticEntityRepresentation sem = iterator.next();
                    if (sem.checkIfInside(e.getPoint())) {
                        // Toggle Markierung ... :)
                        sem.setMarked(!sem.isMarked());
                    }
                }
            }
            repaint();
        }
    }

    private JMenuItem createMenuItem(String label, String actionCommand, ActionListener listener) {
        JMenuItem result = new JMenuItem(label);
        result.addActionListener(listener);
        result.setActionCommand(actionCommand);
        return result;
    }
}
