/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package uib.gui;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.swing.*;
import org.jfree.chart.*;
import org.jfree.chart.plot.Plot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.data.xy.*;

/**
 *
 * @author Olav
 */
public class PRChartCreation {

    private AardvarkGui parent;
    private XYSeries series;
    private XYSeries series2;
    private JFreeChart chart;
    public static boolean isCreated = false;

    public PRChartCreation(AardvarkGui parent) {

        this.parent = parent;

    }

    public BufferedImage createChart(int x, int y, JLabel label) {

        BufferedImage image = null;
        ImageIcon icon = null;

        series = new XYSeries("Precision Recall");
        series.add(1.0, 0.0);
        series.add(0.97, 0.1);
        series.add(0.93, 0.2);
        series.add(0.88, 0.3);
        series.add(0.85, 0.4);
        series.add(0.79, 0.5);
        series.add(0.70, 0.6);
        series.add(0.62, 0.7);
        series.add(0.46, 0.8);
        series.add(0.26, 0.9);
        series.add(0.02, 1.0);

        series2 = new XYSeries("Total Precision Recall");
        series2.add(1.0, 0.0);
        series2.add(0.85, 0.1);
        series2.add(0.75, 0.2);
        series2.add(0.69, 0.3);
        series2.add(0.63, 0.4);
        series2.add(0.59, 0.5);
        series2.add(0.49, 0.6);
        series2.add(0.35, 0.7);
        series2.add(0.32, 0.8);
        series2.add(0.29, 0.9);
        series2.add(0.15, 1.0);
         

        final XYSeriesCollection dataset = new XYSeriesCollection();

        dataset.addSeries(series);
        dataset.addSeries(series2);

        chart = ChartFactory.createXYLineChart("Precision Recall Chart", // Title
                "Precision", // X-Axis label
                "Recall", // Y-Axis label
                dataset, // Dataset
                PlotOrientation.HORIZONTAL,
                true, // Show legend.
                false, //Tooltips
                false //Urls
                );

        XYPlot plot = (XYPlot) chart.getPlot();
        plot.setBackgroundPaint(Color.lightGray);
        image = chart.createBufferedImage(x, y);
        icon = new ImageIcon(image);
        isCreated = true;

        label.setIcon(icon);

        return image;
    }

    public void saveChart(String fileName) {
        try {
            ChartUtilities.saveChartAsJPEG(new File(fileName + ".jpg"), chart, 800, 600);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
