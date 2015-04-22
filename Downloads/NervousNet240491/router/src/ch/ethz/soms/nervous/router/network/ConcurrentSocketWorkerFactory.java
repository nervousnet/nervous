package ch.ethz.soms.nervous.router.network;

import java.net.Socket;

public abstract class ConcurrentSocketWorkerFactory {
	
	public abstract ConcurrentSocketWorker createWorker(Socket socket);

}
