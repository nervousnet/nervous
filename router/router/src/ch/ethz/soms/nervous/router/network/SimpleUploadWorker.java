package ch.ethz.soms.nervous.router.network;

import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

import ch.ethz.soms.nervous.nervousproto.SensorUploadProtos;
import ch.ethz.soms.nervous.nervousproto.SensorUploadProtos.SensorUpload;
import ch.ethz.soms.nervous.nervousproto.SensorUploadProtos.SensorUpload.SensorData;
import ch.ethz.soms.nervous.router.sql.SqlSetup;
import ch.ethz.soms.nervous.router.utils.Log;

public class SimpleUploadWorker extends ConcurrentSocketWorker {

	Connection connection;
	Socket socket;
	SqlSetup sqlse;

	public SimpleUploadWorker(Socket socket, Connection connection, SqlSetup sqlse) {
		this.connection = connection;
		this.socket = socket;
		this.sqlse = sqlse;
	}

	@Override
	public void run() {
		InputStream is;
		try {
			is = socket.getInputStream();

			while (!socket.isClosed()) {
				try {
					// Parse
					SensorUpload su = SensorUpload.parseDelimitedFrom(is);
					if (su != null) {
						long uuid = su.getUuid();
						long sensorId = su.getSensorId();
						long uploadTime = su.getUploadTime();
						List<SensorData> sensorValues = su.getSensorValuesList();

						// TODO, audit data, correct data timestamps?

						// Insert into the Database
						try {
							// Insert transaction
							PreparedStatement transactstmt = sqlse.getTransactionInsertStatement(connection);
							transactstmt.setLong(1, uuid);
							transactstmt.setLong(2, uploadTime);
							transactstmt.execute();
							transactstmt.close();

							// Insert data
							PreparedStatement datastmt = sqlse.getSensorInsertStatement(connection, sensorId);

							List<Integer> types = sqlse.getArgumentExpectation(sensorId);

							for (SensorData sd : sensorValues) {
								try {
									datastmt.setLong(1,uuid);
									datastmt.setLong(2,sd.getRecordTime());
									
									Iterator<Boolean> iterBool = sd.getValueBoolList().iterator();
									Iterator<Integer> iterInteger = sd.getValueInt32List().iterator();
									Iterator<Long> iterLong = sd.getValueInt64List().iterator();
									Iterator<Float> iterFloat = sd.getValueFloatList().iterator();
									Iterator<Double> iterDouble = sd.getValueDoubleList().iterator();
									Iterator<String> iterString = sd.getValueStringList().iterator();

									int counter = 3;
									for (Integer type : types) {
										switch (type) {
										case SqlSetup.TYPE_BOOL:
											datastmt.setBoolean(counter, iterBool.next());
											break;
										case SqlSetup.TYPE_INT32:
											datastmt.setInt(counter, iterInteger.next());
											break;
										case SqlSetup.TYPE_INT64:
											datastmt.setLong(counter, iterLong.next());
											break;
										case SqlSetup.TYPE_FLOAT:
											datastmt.setFloat(counter, iterFloat.next());
											break;
										case SqlSetup.TYPE_DOUBLE:
											datastmt.setDouble(counter, iterDouble.next());
											break;
										case SqlSetup.TYPE_STRING:
											datastmt.setString(counter, iterString.next());
											break;
										default:
											break;
										}
										counter++;
									}
									datastmt.addBatch();
								} catch (NoSuchElementException e) {
									Log.getInstance().append(Log.FLAG_WARNING, "Sensor data type mismatch with database");
								}
							}
							// Add sensor data to the database
							datastmt.executeBatch();
							datastmt.close();

						} catch (SQLException e) {
							Log.getInstance().append(Log.FLAG_WARNING, "Submitting sensor data chunk to database failed");
						}
					}
				} catch (IOException e) {
					Log.getInstance().append(Log.FLAG_WARNING, "Parsing protobuf SensorUpload failed");
				}
			}
		} catch (IOException e1) {
			Log.getInstance().append(Log.FLAG_WARNING, "Opening data stream from socket failed");
		}
	}
}
