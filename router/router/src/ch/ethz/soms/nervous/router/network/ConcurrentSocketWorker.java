package ch.ethz.soms.nervous.router.network;

public abstract class ConcurrentSocketWorker implements Runnable {
	public abstract void run();
}
