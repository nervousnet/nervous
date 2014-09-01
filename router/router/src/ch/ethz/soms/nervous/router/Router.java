package ch.ethz.soms.nervous.router;

import ch.ethz.soms.nervous.router.network.ConcurrentServer;
import ch.ethz.soms.nervous.router.network.SimpleUploadWorkerFactory;
import ch.ethz.soms.nervous.router.sql.SqlConnection;
import ch.ethz.soms.nervous.router.sql.SqlSetup;
import ch.ethz.soms.nervous.router.utils.Log;

public class Router {

	public static Configuration config;

	public static void main(String[] args) {

		// Load configuration from custom path or current directory
		if (args.length > 0) {
			config = Configuration.getInstance(args[0]);
		} else {
			config = Configuration.getInstance();
		}

		// Set up logging
		Log log = Log.getInstance(config.getLogDisplayVerbosity(), config.getLogWriteVerbosity(), config.getLogPath());
		log.append(Log.FLAG_INFO, "Reading configuration file done");

		// Set up SQL connection
		SqlConnection sqlco = new SqlConnection(config.getSqlHostname(), config.getSqlUsername(), config.getSqlPassword(), config.getSqlPort(), config.getSqlDatabase());
		log.append(Log.FLAG_INFO, "Establishing connection to SQL database done");
		
		// Set up SQL tables
		SqlSetup sqlse = new SqlSetup(sqlco.getConnection(), config);
		sqlse.setupTables();
		
		// Create factory which creates workers for uploading to the SQL database
		SimpleUploadWorkerFactory factory = new SimpleUploadWorkerFactory(sqlco, sqlse);
		
		// Start server
		ConcurrentServer server = new ConcurrentServer(config.getServerPort(), config.getServerThreads(), factory);
		Thread serverThread = new Thread(server);
		serverThread.start();
		log.append(Log.FLAG_INFO, "Setting up concurrent server done");

		boolean running = true;

		while (running) {
			try {
				Thread.sleep(50000);
			} catch (InterruptedException e) {
				log.append(Log.FLAG_WARNING, "Server execution interrupted");
				running = false;
			}
		}
		
		// Tear down server
		server.stop();
		
		log.append(Log.FLAG_INFO, "Server terminated");
	}

}
