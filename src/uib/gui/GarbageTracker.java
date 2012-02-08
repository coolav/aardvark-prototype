/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package uib.gui;


import java.text.DecimalFormat;
/**
 *
 * @author Olav
 */
public class GarbageTracker extends Thread {
    private boolean stopped = false;
    private AardvarkGui parent;
    public static DecimalFormat df = (DecimalFormat) DecimalFormat.getInstance();

    public GarbageTracker(AardvarkGui parent) {
        this.parent = parent;
        init();
    }

    private void init() {
        df.setMaximumFractionDigits(1);
    }

    @Override
    public void run() {
        debug(" started running ...");
        String memory;
        float full, free, used, cpu;
        try {
            while (!stopped) {
                full = Runtime.getRuntime().totalMemory() / (1024f * 1024f);
                free = Runtime.getRuntime().freeMemory() / (1024f * 1024f);
                used = full - free;
                memory = df.format(used) + "M of " + df.format(full) + "M";                
                parent.jProgressBarMemory.setValue(Math.round(used));
                parent.jProgressBarMemory.setMaximum(Math.round(full));
                parent.jProgressBarMemory.setString(memory);
                sleep(3000);
            }
        } catch (Exception e) {
            debug("GarbageTracker stopped 'cause of " + e.toString() + ": " + e.getMessage());
        }
    }

    public void stopMonitor() {
        this.stopped = true;
    }

    private void debug(String message) {
        if (parent.DEBUG)
            System.out.println("[at.lux.fotoannotation.GarbageTracker] " + message);
    }

    
}
