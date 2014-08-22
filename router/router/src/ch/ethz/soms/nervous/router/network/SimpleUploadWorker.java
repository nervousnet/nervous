package ch.ethz.soms.nervous.router.network;

import java.net.Socket;
import java.sql.Connection;

public class SimpleUploadWorker extends ConcurrentSocketWorker {

	Connection connection;
	
	public SimpleUploadWorker(Socket socket, Connection connection) {
		this.connection = connection;
	}

	@Override
	public void run() {
		// TODO Basic data flow: Socket --> Connection
		// TODO Retrieve data from socket, check/review/audit data, insert data into database
	}

}
