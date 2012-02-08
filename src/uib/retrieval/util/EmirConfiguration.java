/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package uib.retrieval.util;

import java.util.Properties;

/**
 *
 * @author Olav
 */
public class EmirConfiguration {

    private static EmirConfiguration configuration = null;

    static Object getInstance() {
        throw new UnsupportedOperationException("Not yet implemented");
    }
    private Properties props = new Properties();

    private EmirConfiguration() {
        // nothing to do here ... its a singleton.
    }

    public static EmirConfiguration getInstance(Properties properties) {
        if (configuration == null) {
            configuration = new EmirConfiguration();
        }
        if (properties == null) {
            configuration.setProperties(new Properties());
        } else {
            configuration.setProperties(properties);
        }
        return configuration;
    }

    private void setProperties(Properties properties) {
        throw new UnsupportedOperationException("Not yet implemented");
    }
}
