package ch.ethz.soms.nervous.router.network;

import java.net.Socket;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;

public class SimpleUploadWorker extends ConcurrentSocketWorker {

	Connection connection;

	public SimpleUploadWorker(Socket socket, Connection connection) {
		this.connection = connection;
	}

	@Override
	public void run() {
		// TODO Basic data flow: Socket --> Connection
		// TODO Retrieve data from socket, check/review/audit data, insert data into database
		
		
		try {
			PreparedStatement stmt = connection.prepareStatement(sql);
			for() {
				stmt.
			}
			
			int[] success = stmt.executeBatch();
		} catch (SQLException e) {

		}
	}
}
