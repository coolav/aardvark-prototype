/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package uib.download;

import com.jcraft.jsch.*;

/**
 *
 * @author Olav
 */
public class SFTPExample {

    public static void main(String args[]) throws Exception {


        String user = "olo001";
        String password = "123Fakemcs1!";
        String host = "login.uib.no";
        int port = 22;
        int exitCode = 0;
        String knownHostsFilename = "/Home/stud3/olo001/.ssh/id_dsa.pub";
        String privateKey = "C:/Program Files (x86)/WinSCP/PuTTY/privateKey.ppk";
        String sourcePath = "C:/Users/Olav/Documents/NetBeans/Aardvark/output/testingXML.xml";
        String destPath = "/Home/stud3/olo001/Master/ExperimentResults/testingXML.xml";
        try {
            java.util.Properties config = new java.util.Properties();
            config.put("StrictHostKeyChecking", "no");
            JSch jsch = new JSch();
            jsch.setKnownHosts(knownHostsFilename);

            Session session = jsch.getSession(user, host, port);
            session.setConfig(config);
            session.setPassword(password);
            session.connect();
            System.out.println("connected");
            
            ChannelSftp sftpChannel = (ChannelSftp) session.openChannel("sftp");            
            sftpChannel.connect();
            if (sftpChannel.isClosed()) {
                //get exit code
                exitCode = sftpChannel.getExitStatus();
                System.out.println(exitCode);
                //break
            }

            //final Vector files = sftpChannel.ls(".");
            //Iterator itFiles = files.iterator();
            //while (itFiles.hasNext()) {
            //    System.out.println("Index: " + itFiles.next());
            //}
            System.out.println("Uploading test file");
            sftpChannel.put(sourcePath, destPath, new ExampleProgressMonitor());
            //System.out.println("Downloading test file");
            //sftpChannel.get(destPath, sourcePath + ".new", new ExampleProgressMonitor());
            //System.out.println("Remove test file");
            //sftpChannel.rm(destPath);

            sftpChannel.exit();
            session.disconnect();
        } catch (Exception e) {
            System.err.println("Unable to connect to FTP server. " + e.toString());
            throw e;
        }
    }
}

class MyLogger implements Logger {

    static java.util.Hashtable name = new java.util.Hashtable();

    static {
        name.put(new Integer(DEBUG), "DEBUG: ");
        name.put(new Integer(INFO), "INFO: ");
        name.put(new Integer(WARN), "WARN: ");
        name.put(new Integer(ERROR), "ERROR: ");
        name.put(new Integer(FATAL), "FATAL: ");
    }

    public boolean isEnabled(int level) {
        return true;
    }

    public void log(int level, String message) {
        System.err.print(name.get(new Integer(level)));
        System.err.println(message);
    }
}

class ExampleProgressMonitor implements SftpProgressMonitor {

    private double count;
    private double max;
    private String src;
    private int percent;
    private int lastDisplayedPercent;

    ExampleProgressMonitor() {
        count = 0;
        max = 0;
        percent = 0;
        lastDisplayedPercent = 0;
    }

    public void init(int op, String src, String dest, long max) {
        this.max = max;
        this.src = src;
        count = 0;
        percent = 0;
        lastDisplayedPercent = 0;
        status();
    }

    public boolean count(long count) {
        this.count += count;
        percent = (int) ((this.count / max) * 100.0);
        status();
        return true;
    }

    public void end() {
        percent = (int) ((count / max) * 100.0);
        status();
    }

    private void status() {
        if (lastDisplayedPercent <= percent - 10) {
            System.out.println(src + ": " + percent + "% " + ((long) count) + "/" + ((long) max));
            lastDisplayedPercent = percent;
        }
    }
}
