package ch.ethz.soms.nervous.router.utils;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.sql.Timestamp;
import java.util.Date;

public class Log {

	public static final int FLAG_ERROR = 1;
	public static final int FLAG_WARNING = 2;
	public static final int FLAG_DEBUGGING = 4;
	public static final int FLAG_INFO = 8;

	private static Log log;

	private int displayVerbosity;
	private int writeVerbosity;
	/**
	 * Full (absolute or relative) path to the log file
	 */
	private String logPath;

	private Log(int displayVerbosity, int writeVerbosity, String logPath) {
		this.displayVerbosity = displayVerbosity;
		this.writeVerbosity = writeVerbosity;
		this.logPath = logPath;
	}

	public synchronized boolean append(int flag, String msg) {
		// Not a legal flag
		if (flag > (FLAG_INFO | FLAG_DEBUGGING | FLAG_WARNING | FLAG_ERROR) || flag < FLAG_ERROR) {
			return false;
		}
		String symbol = symbolize(flag);
		String timestamp = (new Timestamp(new Date().getTime())).toString();
		String message = timestamp + " - " + symbol + " - " + msg + "\n";
		if ((flag & displayVerbosity) > 0) {
			System.out.println(message);
		}
		if (logPath != null && (flag & writeVerbosity) > 0) {
			try {
				BufferedWriter bw = new BufferedWriter(new FileWriter(logPath, true));
				bw.write(message);
				bw.close();
			} catch (Exception e) {
				// Ignore errors here as they can't be logged anyways
			}
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
			return new Log(FLAG_ERROR | FLAG_WARNING, FLAG_ERROR | FLAG_WARNING, null);
		} else {
			return log;
		}
	}

	public static synchronized Log getInstance(int displayVerbosity, int writeVerbosity, String logPath) {
		if (log == null) {
			return new Log(displayVerbosity, writeVerbosity, logPath);
		} else {
			log.displayVerbosity = displayVerbosity;
			log.writeVerbosity = writeVerbosity;
			log.logPath = logPath;
			return log;
		}
	}

}
