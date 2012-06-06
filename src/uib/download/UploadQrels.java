/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package uib.download;


/**
 *
 * @author Olav
 */


import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;
import java.io.File;
import java.io.FileInputStream;

/**
 * @author vigilance
 * 
*/
public class UploadQrels {
    private String uploadFile = "";

    /**
     *
     */
    public UploadQrels(String file) {
        this.uploadFile = file;
    }

    /**
     * @param args
     */
    public static void main(String[] args) {
        String SFTPHOST = "login.uib.no";
        int SFTPPORT = 22;
        String SFTPUSER = "olo001";
        String SFTPPASS = "123Fakemcs1!";
        String SFTPWORKINGDIR = "/Home/stud3/olo001/Master/ExperimentResults/";

        Session session = null;
        Channel channel = null;
        ChannelSftp channelSftp = null;

        try {
            JSch jsch = new JSch();
            session = jsch.getSession(SFTPUSER, SFTPHOST, SFTPPORT);
            session.setPassword(SFTPPASS);
            java.util.Properties config = new java.util.Properties();
            config.put("StrictHostKeyChecking", "no");
            session.setConfig(config);
            session.connect();
            channel = session.openChannel("sftp");
            channel.connect();
            channelSftp = (ChannelSftp) channel;
            channelSftp.cd(SFTPWORKINGDIR);
            File f = new File("C:/Users/Olav/Documents/NetBeans/Aardvark/output/testingXML.xml");
            channelSftp.put(new FileInputStream(f), f.getName());
            System.out.println("Finished");
        } catch (Exception ex) {
            ex.printStackTrace();
        }

    }
}