package sfdata;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.logging.Level;
import java.util.logging.Logger;

public class CLLogger implements Serializable 
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private Logger logger = Logger.getLogger("com.crmdata");
	
	private String siteName = "";
	
	public static CLLogger getLogger(String siteName)
	{
		return new CLLogger();
	}
	
	private String getValue(String value)
	{
		return siteName + "~>" + value;
	}
	
	public void info(String value) 
	{
		logger.log(Level.INFO, getValue(value));
	}
	public void debug(String value) 
	{
		logger.log(Level.INFO, getValue(value));
	}
	public void error(String value) 
	{
		logger.log(Level.SEVERE, getValue(value));
	}
	public void severe(String value) 
	{
		logger.log(Level.SEVERE, getValue(value));
	}
	
	public void debug(Hashtable<String, String> map)
	{
		for (String key : map.keySet()) {
			debug("(key, value) = (" + key + ", " + map.get(key) + ")");
		}
	}
	public void debug(HashMap<String, String> map)
	{
		for (String key : map.keySet()) {
			debug("(key, value) = (" + key + ", " + map.get(key) + ")");
		}
	}

}
