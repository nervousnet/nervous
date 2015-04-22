package ch.ethz.soms.nervous.router.network;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import ch.ethz.soms.nervous.router.utils.Log;

public class ConcurrentServer implements Runnable {

	private int sport = 0;
	private ServerSocket ssocket = null;
	private boolean stopped = false;
	private Thread runThread = null;
	private ExecutorService threadPool;
	private ConcurrentSocketWorkerFactory factory;

	public ConcurrentServer(int port, int numThreads, ConcurrentSocketWorkerFactory factory) {
		this.sport = port;
		this.threadPool = Executors.newFixedThreadPool(numThreads);
		this.factory = factory;
	}

	@Override
	public void run() {
		synchronized (this) {
			runThread = Thread.currentThread();
		}
		createSocket();
		while (!isStopped()) {
			boolean success = false;
			Socket csocket = null;
			try {
				csocket = ssocket.accept();
				success = true;
			} catch (IOException e) {
				if (isStopped()) {
					Log.getInstance().append(Log.FLAG_INFO, "Connection refused: server is closing");
				} else {
					Log.getInstance().append(Log.FLAG_ERROR, "Connection refused: error accepting");
				}
				success = false;
			}
			if (success) {
				try {
					this.threadPool.execute(factory.createWorker(csocket));
				} catch (Exception e) {
					Log.getInstance().append(Log.FLAG_ERROR, "Threadpool execution failure");
				}
			}
		}
		threadPool.shutdown();
		Log.getInstance().append(Log.FLAG_INFO, "Server threading pool is shut down");
	}

	private synchronized boolean isStopped() {
		return stopped;
	}

	public synchronized void stop() {
		stopped = true;
		try {
			ssocket.close();
		} catch (IOException e) {
			Log.getInstance().append(Log.FLAG_ERROR, "Can't close the server on port " + String.valueOf(sport));
		}
	}

	private synchronized void createSocket() {
		try {
			ssocket = new ServerSocket(sport);
		} catch (IOException e) {
			Log.getInstance().append(Log.FLAG_ERROR, "Can't open the server on port " + String.valueOf(sport));
		}
	}

}
