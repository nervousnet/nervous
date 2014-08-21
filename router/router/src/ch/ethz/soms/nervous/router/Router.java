package ch.ethz.soms.nervous.router;

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
		log.append(Log.FLAG_INFO, "Reading configuration file");
		
		// TODO: Setup threading pool, setup network sockets, setup SQL connection

	}

}
