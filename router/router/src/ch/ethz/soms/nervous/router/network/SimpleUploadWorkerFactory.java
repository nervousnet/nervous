package ch.ethz.soms.nervous.router.network;

import java.net.Socket;

import ch.ethz.soms.nervous.router.sql.SqlConnection;

public class SimpleUploadWorkerFactory extends ConcurrentSocketWorkerFactory{

	SqlConnection sqlco;
	
	public SimpleUploadWorkerFactory(SqlConnection sqlco) {
		this.sqlco = sqlco;
	}

	@Override
	public ConcurrentSocketWorker createWorker(Socket socket) {
		SimpleUploadWorker suwo = new SimpleUploadWorker(socket, sqlco.getConnection());
		return suwo;
	}

}
