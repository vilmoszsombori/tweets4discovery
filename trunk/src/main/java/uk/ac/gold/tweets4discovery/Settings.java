package uk.ac.gold.tweets4discovery;

import java.io.InputStream;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.log4j.Logger;

/**
 * Settings
 */
public class Settings {
	
	private static Logger LOG = Logger.getLogger(Settings.class);
	private static String SETTINGS_FILE = "tweets4discovery.properties";
	private static Settings instance = null;
	private PropertiesConfiguration config = new PropertiesConfiguration();

	private Settings() {		
		try {
			InputStream fileStream = Settings.class.getClassLoader().getResourceAsStream(SETTINGS_FILE);
			config.load(fileStream);
		} catch (ConfigurationException e) {
			LOG.error(e.getMessage() + " [" + SETTINGS_FILE + "]");			
		} 
	}

	public static Settings i() {
		if (instance == null) {
			instance = new Settings();
		}
		return instance;
	}

	public String get(String key, String defaultValue) {
		return config.getString(key, defaultValue);
	}
	
	public String[] getStringArray(String key) {
		return config.getStringArray(key);
	}

	public String get(String key) {
		return config.getString(key);
	}
	
	public int getInt(String key) {
		return config.getInt(key);
	}
	
	public double getDouble(String key) {
		return config.getDouble(key);
	}
	
	public boolean getBoolean(String key) {
		return config.getBoolean(key);
	}

	public void setProperty(String key, String value) {
		config.setProperty(key, value);
	}

	public static void clear() {
		instance = null;
	}
}
