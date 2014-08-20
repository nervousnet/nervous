package ch.ethz.soms.nervous.router.utils;

import java.sql.Timestamp;
import java.util.Date;

public class Log {

	private static final int FLAG_ERROR = 1;
	private static final int FLAG_WARNING = 2;
	private static final int FLAG_DEBUGGING = 4;
	private static final int FLAG_INFO = 8;

	private static Log log;

	private int displayVerbosity;
	private int writeVerbosity;

	private Log(int displayVerbosity, int writeVerbosity) {
		this.displayVerbosity = displayVerbosity;
		this.writeVerbosity = writeVerbosity;
	}

	public synchronized boolean append(int flag, String msg) {
		// Not a legal flag
		if (flag > (FLAG_INFO | FLAG_DEBUGGING | FLAG_WARNING | FLAG_ERROR)
				|| flag < FLAG_ERROR) {
			return false;
		}
		String symbol = symbolize(flag);
		String timestamp = (new Timestamp(new Date().getTime())).toString();
		String message = timestamp + " - " + symbol + " - " + msg;
		if ((flag & displayVerbosity) > 0) {
			System.out.println(message);
		}
		if ((flag & writeVerbosity) > 0) {
			// TODO
		}
		return true;
	}

	private String symbolize(int flag) {
		String symbol = "";
		if ((flag & FLAG_ERROR) > 0) {
			symbol += "[E]";
		}
		if ((flag & FLAG_WARNING) > 0) {
			symbol += "[W]";
		}
		if ((flag & FLAG_DEBUGGING) > 0) {
			symbol += "[D]";
		}
		if ((flag & FLAG_INFO) > 0) {
			symbol += "[I]";
		}
		return symbol;
	}
	
	public static synchronized Log getInstance() {
		if (log == null) {
			return new Log(FLAG_ERROR|FLAG_WARNING, FLAG_ERROR|FLAG_WARNING);
		} else {
			return log;
		}
	}

	public static synchronized Log getInstance(int displayVerbosity, int writeVerbosity) {
		if (log == null) {
			return new Log(displayVerbosity, writeVerbosity);
		} else {
			log.displayVerbosity = displayVerbosity;
			log.writeVerbosity = writeVerbosity;
			return log;
		}
	}

}
