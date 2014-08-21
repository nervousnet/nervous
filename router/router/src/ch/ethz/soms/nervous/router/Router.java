package ch.ethz.soms.nervous.router;

import ch.ethz.soms.nervous.router.network.ConcurrentServer;
import ch.ethz.soms.nervous.router.network.SimpleUploadWorkerFactory;
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

		Log log = Log.getInstance(config.getLogDisplayVerbosity(), config.getLogWriteVerbosity(), config.getLogPath());
		log.append(Log.FLAG_INFO, "Reading configuration file done");

		// TODO: Setup threading pool, setup network sockets, setup SQL connection

		SimpleUploadWorkerFactory factory = new SimpleUploadWorkerFactory();
		
		// Start server
		ConcurrentServer server = new ConcurrentServer(config.getServerPort(), config.getServerThreads(), factory);
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
