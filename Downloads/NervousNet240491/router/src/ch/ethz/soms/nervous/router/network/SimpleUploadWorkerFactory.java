package ch.ethz.soms.nervous.router.network;

import java.net.Socket;

import ch.ethz.soms.nervous.router.sql.SqlConnection;
import ch.ethz.soms.nervous.router.sql.SqlSetup;

public class SimpleUploadWorkerFactory extends ConcurrentSocketWorkerFactory{

	SqlConnection sqlco;
	SqlSetup sqlse;
	
	public SimpleUploadWorkerFactory(SqlConnection sqlco, SqlSetup sqlse) {
		this.sqlco = sqlco;
		this.sqlse = sqlse;
	}

	@Override
	public ConcurrentSocketWorker createWorker(Socket socket) {
		SimpleUploadWorker suwo = new SimpleUploadWorker(socket, sqlco.getConnection(), sqlse);
		return suwo;
	}

}
