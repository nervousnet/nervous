package ch.ethz.soms.nervous.router;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlRootElement;

import ch.ethz.soms.nervous.router.utils.Log;

@XmlRootElement(name = "config")
public class Configuration {

	private static Configuration config;

	private String sqlServer;
	private String sqlUsername;
	private String sqlPassword;

	private int logWriteVerbosity;
	private int logDisplayVerbosity;

	private String configPath;
	private String logPath;
	
	private int serverPort;
	private int serverThreads;
	
	public int getServerThreads() {
		return serverThreads;
	}
	
	public void setServerThreads(int serverThreads) {
		this.serverThreads = serverThreads;
	}
	
	public int getServerPort() {
		return serverPort;
	}
	
	public void setServerPort(int serverPort) {
		this.serverPort = serverPort;
	}

	public String getSqlServer() {
		return sqlServer;
	}

	public void setSqlServer(String sqlServer) {
		this.sqlServer = sqlServer;
	}

	public String getSqlUsername() {
		return sqlUsername;
	}

	public void setSqlUsername(String sqlUsername) {
		this.sqlUsername = sqlUsername;
	}

	public String getSqlPassword() {
		return sqlPassword;
	}

	public void setSqlPassword(String sqlPassword) {
		this.sqlPassword = sqlPassword;
	}

	public int getLogWriteVerbosity() {
		return logWriteVerbosity;
	}

	public void setLogWriteVerbosity(int logWriteVerbosity) {
		this.logWriteVerbosity = logWriteVerbosity;
	}

	public int getLogDisplayVerbosity() {
		return logDisplayVerbosity;
	}

	public void setLogDisplayVerbosity(int logDisplayVerbosity) {
		this.logDisplayVerbosity = logDisplayVerbosity;
	}

	public String getConfigPath() {
		return configPath;
	}

	public void setConfigPath(String configPath) {
		this.configPath = configPath;
	}

	public String getLogPath() {
		return logPath;
	}

	public void setLogPath(String logPath) {
		this.logPath = logPath;
	}

	public void setSqlServer() {

	}

	public static synchronized Configuration getInstance(String path) {
		if (config == null) {
			config = new Configuration(path);
			// Load configuration from file
			unmarshal();
		}
		return config;
	}

	public static synchronized Configuration getInstance() {
		if (config == null) {
			config = new Configuration("config.xml");
			// Load configuration from file
			unmarshal();
		}
		return config;
	}
	
	/**
	 * No-arg default constructor for unmarshal
	 */
	private Configuration() {
		
	}

	/**
	 * Default constructor if configuration file is not found
	 * @param path
	 */
	private Configuration(String path) {
		// Write default configuration here
		this.configPath = path;
		// Logging
		this.logDisplayVerbosity = Log.FLAG_ERROR|Log.FLAG_WARNING;
		this.logWriteVerbosity = Log.FLAG_ERROR|Log.FLAG_WARNING;
		this.logPath = "log.txt";
		// SQL
		this.sqlServer = "";
		this.sqlUsername = "";
		this.sqlPassword = "";
		// Networking
		this.serverPort = 25600;
		this.serverThreads = 5;
	}

	public static synchronized void marshal() {
		try {
			JAXBContext context = JAXBContext.newInstance(Configuration.class);
			Marshaller m = context.createMarshaller();
			m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
			m.marshal(config, new File(config.getConfigPath()));
		} catch (JAXBException jbe) {
			Log.getInstance().append(Log.FLAG_WARNING, "Couldn't write the configuration file");
		}
	}

	public static synchronized void unmarshal() {
		try {
			JAXBContext context = JAXBContext.newInstance(Configuration.class);
			Unmarshaller um = context.createUnmarshaller();
			Configuration config = (Configuration) um.unmarshal(new FileReader(Configuration.config.getConfigPath()));
			Configuration.config = config;
			return;
		} catch (IOException ioe) {
			Log.getInstance().append(Log.FLAG_WARNING, "Couldn't read the configuration file");
		} catch (JAXBException jbe) {
			Log.getInstance().append(Log.FLAG_ERROR, "Error parsing the configuration file");
		}
		// Error reading the configuration, write current configuration
		marshal();
	}
}
