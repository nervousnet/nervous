package ch.ethz.soms.nervous.router;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

import ch.ethz.soms.nervous.router.sql.SqlSensorConfiguration;
import ch.ethz.soms.nervous.router.utils.FileOperations;
import ch.ethz.soms.nervous.router.utils.Log;

@XmlRootElement(name = "config")
public class Configuration {

	private static Configuration config;

	private String sqlUsername;
	private String sqlPassword;
	private String sqlHostname;
	private int sqlPort;
	private String sqlDatabase;

	private int logWriteVerbosity;
	private int logDisplayVerbosity;

	private String configPath;
	private String logPath;
	
	private int serverPort;
	private int serverThreads;

	private List<SqlSensorConfiguration> sensors = new ArrayList<SqlSensorConfiguration>();
		
	public static Configuration getConfig() {
		return config;
	}

	public static void setConfig(Configuration config) {
		Configuration.config = config;
	}

	@XmlElementWrapper(name = "sqlsensors")
	@XmlElement(name = "sensor")
	public List<SqlSensorConfiguration> getSensors() {
		return sensors;
	}

	public void setSensors(List<SqlSensorConfiguration> sensors) {
		this.sensors = sensors;
	}

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

	public String getSqlHostname() {
		return sqlHostname;
	}

	public void setSqlHostname(String sqlHostname) {
		this.sqlHostname = sqlHostname;
	}

	public int getSqlPort() {
		return sqlPort;
	}

	public void setSqlPort(int sqlPort) {
		this.sqlPort = sqlPort;
	}

	public String getSqlDatabase() {
		return sqlDatabase;
	}

	public void setSqlDatabase(String sqlDatabase) {
		this.sqlDatabase = sqlDatabase;
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
		this.sqlHostname = "";
		this.sqlUsername = "";
		this.sqlPassword = "";
		this.sqlPort = 3306;
		this.sqlDatabase = "";
		// Networking
		this.serverPort = 25600;
		this.serverThreads = 5;
		// Sensors
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
		// Error reading the configuration, write current configuration after backing up
		try {
			FileOperations.copyFile(new File(Configuration.config.getConfigPath()), new File(Configuration.config.getConfigPath()+".back"));
		} catch (IOException e) {
		}
		marshal();
	}
}
