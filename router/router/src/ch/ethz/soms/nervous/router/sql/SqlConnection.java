package ch.ethz.soms.nervous.router.sql;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

import javax.swing.JOptionPane;

import com.mysql.jdbc.Driver;

/**
 * A Wrapper around an SQL Connection
 */
public final class SqlConnection {

	// Server properties
	public static final String USERNAME = "dmdb";
	public static final String PASSWORD = "1234";
	public static final String HOSTNAME = "localhost";
	public static final int PORT = 3306;
	public static final String DATABASE = "dmdb2014";

	private Connection connection;

	/**
	 * Singleton instance: We want to avoid re-establishing connections across web server requests.
	 */
	private static SqlConnection instance = null;

	public static synchronized SqlConnection getInstance() {
		if (instance == null) {
			instance = new SqlConnection();
		}
		return instance;
	}
	
	private void connect()
	{
		Connection connection = null;


		try {
			new Driver();
			
			// See: http://dev.mysql.com/doc/connector-j/en/connector-j-reference-configuration-properties.html
			Properties properties = new Properties();
			properties.put("user", USERNAME);
			properties.put("password", PASSWORD);
			properties.put("autoReconnect", "true");
			properties.put("maxReconnects", "10");
			properties.put("initialTimeout", "1");
			
			
			connection = DriverManager.getConnection("jdbc:mysql://" + HOSTNAME + ":" + PORT + "/" + DATABASE, properties);
			
			//connection = DriverManager.getConnection("jdbc:mysql://" + HOSTNAME + ":" + PORT + "/" + DATABASE, USERNAME, PASSWORD);
			

		} catch (final SQLException e) {
			/**
			 * Make sure that we really see this error.
			 */
			System.err.println("Could not connect to MYSQL. Is the server running?");
			JOptionPane.showMessageDialog(null, "Could not connect to MYSQL. Is the server running?\n" + "Error in " + this.getClass().getName() + ".", "Critical Error!", JOptionPane.ERROR_MESSAGE);
			e.printStackTrace();
		}

		this.connection = connection;
	}

	private SqlConnection() {
		connect();
	}

	public final Connection getConnection() {
		return this.connection;
	}
	
	public void reconnect()
	{
		connect();
	}

}