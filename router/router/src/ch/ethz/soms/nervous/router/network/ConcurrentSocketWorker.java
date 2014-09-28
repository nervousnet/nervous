package ch.ethz.soms.nervous.router.network;

import java.io.IOException;
import java.net.Socket;

public abstract class ConcurrentSocketWorker implements Runnable {
	public abstract void run();

	protected Socket socket;

	protected ConcurrentSocketWorker(Socket socket) {
		this.socket = socket;
	}

	protected void cleanup() {
		try {
			socket.close();
		} catch (IOException e) {
		}
	}
}
