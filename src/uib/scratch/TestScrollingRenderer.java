/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package uib.scratch;

/**
 *
 * @author Olav
 */
import java.awt.*;

import javax.swing.*;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;

public class TestScrollingRenderer implements Runnable {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(new TestScrollingRenderer());
    }

    public void run() {
        ScrolledText[] text = {
                new ScrolledText("Four score and seven years ago our fathers brought "
                        + "forth on this continent a new nation, ..."),
                new ScrolledText("Line1\nLine2\nLine3\nLine4\nLine5"),
                new ScrolledText("Twinkle, twinkle, little star. How I wonder what you are."
                        + "Up above the world so high..."), new ScrolledText("Just one line here.") };

        JTable table = new JTable(4, 2);
        table.setRowSelectionAllowed(false);
        table.putClientProperty("terminateEditOnFocusLost", Boolean.TRUE);
        table.getTableHeader().setReorderingAllowed(false);

        ScrollingTextAreaCellEditor editor = new ScrollingTextAreaCellEditor(2, 15);
        Dimension editorSize = editor.getCellEditorPreferredSize();

        TableColumn tc = table.getColumnModel().getColumn(1);
        tc.setCellEditor(editor);
        tc.setCellRenderer(new ScrollingTextAreaCellRenderer(2, 15));
        tc.setPreferredWidth(editorSize.width);

        table.setRowHeight(editorSize.height);
        table.setPreferredScrollableViewportSize(table.getPreferredSize());
        for (int i = 0; i < 4; i++) {
            table.setValueAt("ROW" + i, i, 0);
            table.setValueAt(text[i], i, 1);
        }

        JScrollPane scroll = new JScrollPane(table);
        scroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);

        JFrame frame = new JFrame(this.getClass().getName());
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.getContentPane().add(scroll, BorderLayout.CENTER);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);

        for (int i = 0; i < 4; i++) {
            table.setValueAt("ROW" + i, i, 0);
            table.setValueAt(text[i], i, 1);
        }
    }

    class ScrollingTextAreaCellEditor extends AbstractCellEditor implements TableCellEditor {
        private final JTextArea textArea;
        private final JScrollPane scroll;
        private Point scrollPosition;

        public ScrollingTextAreaCellEditor(int rows, int cols) {
            super();
            textArea = new JTextArea(rows, cols);
            textArea.setEditable(true);
            textArea.setWrapStyleWord(true);
            textArea.setLineWrap(true);
            scroll = new JScrollPane(textArea);
            scroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        }

        public Component getTableCellEditorComponent(JTable table, Object value,
                boolean isSelected, int row, int column) {
            textArea.setText(((ScrolledText) value).text);
            Point p = ((ScrolledText) value).position;
            scroll.getViewport().getView().setLocation(new Point(-p.x, -p.y));
            return scroll;
        }

        public void setText(String text) {
            textArea.setText(text);
        }

        @Override
        public boolean stopCellEditing() {
            scrollPosition = scroll.getViewport().getViewPosition();
            return super.stopCellEditing();
        }

        public Object getCellEditorValue() {
            ScrolledText value = new ScrolledText(textArea.getText());
            value.position = scrollPosition;
            return value;
        }

        public Dimension getCellEditorPreferredSize() {
            return scroll.getPreferredSize();
        }
    }

    class ScrollingTextAreaCellRenderer implements TableCellRenderer {
        private final JTextArea textArea;
        private final JScrollPane scroll;

        public ScrollingTextAreaCellRenderer(int rows, int cols) {
            textArea = new JTextArea(rows, cols);
            textArea.setEditable(false);
            textArea.setWrapStyleWord(true);
            textArea.setLineWrap(true);
            scroll = new JScrollPane(textArea);
            scroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        }

        public Component getTableCellRendererComponent(JTable table, Object value,
                boolean isSelected, boolean hasFocus, int row, int column) {
            textArea.setText(((ScrolledText) value).text);
            Point p = ((ScrolledText) value).position;
            scroll.getViewport().getView().setLocation(new Point(-p.x, -p.y));
            return scroll;
        }
    }

    public static class ScrolledText {
        private Point position = new Point();
        private String text = "";

        public ScrolledText(String text) {
            this.text = text;
        }
    }
}