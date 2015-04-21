package ch.ethz.soms.nervous.router.sql;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import ch.ethz.soms.nervous.router.Configuration;
import ch.ethz.soms.nervous.router.utils.Log;

public class SqlSetup {

	public static final int TYPE_BOOL = 0;
	public static final int TYPE_INT32 = 1;
	public static final int TYPE_INT64 = 2;
	public static final int TYPE_FLOAT = 3;
	public static final int TYPE_DOUBLE = 4;
	public static final int TYPE_STRING = 5;

	private Connection con;
	private Configuration config;
	private HashMap<Long, List<Integer>> sensorsHash;

	public SqlSetup(Connection con, Configuration config) {
		this.con = con;
		this.config = config;
		// Hash for performance reasons when asking for insert statements
		this.sensorsHash = new HashMap<Long, List<Integer>>();
	}

	public void setupTables() {
		setupTransactionTable();
		setupSensorTables();
	}

	public PreparedStatement getTransactionInsertStatement(Connection con) throws SQLException {
		return con.prepareStatement("INSERT INTO `Transact` (`UUID`, `UploadTime`) VALUES (?,?);");
	}

	public List<Integer> getArgumentExpectation(long sensorId) {
		return sensorsHash.get(sensorId);
	}

	public PreparedStatement getSensorInsertStatement(Connection con, long sensorId) throws SQLException {
		List<Integer> types = sensorsHash.get(sensorId);
		if (types != null) {
			StringBuilder sb = new StringBuilder();
			sb.append("INSERT INTO `SENSOR_" + Long.toHexString(sensorId) + "` VALUES (DEFAULT,?,?,");
			for (int i = 0; i < types.size() - 1; i++) {
				sb.append("?,");
			}
			if (types.size() >= 1) {
				sb.append("?");
			}
			sb.append(");");
			return con.prepareStatement(sb.toString());
		} else {
			return null;
		}
	}

	private void setupSensorTables() {
		for (SqlSensorConfiguration sensor : config.getSensors()) {
			List<Integer> types = new ArrayList<Integer>(sensor.getAttributes().size());
			StringBuilder sb = new StringBuilder();
			sb.append("CREATE TABLE IF NOT EXISTS `" + config.getSqlDatabase() + "`.`SENSOR_" + Long.toHexString(sensor.getSensorID()) + "` (\n");
			sb.append("`RecordID` INT NOT NULL UNIQUE AUTO_INCREMENT,\n");
			sb.append("`UUID` BINARY(16) NOT NULL,\n");
			sb.append("`RecordTime` BIGINT UNSIGNED NOT NULL,\n");
			for (SqlSensorAttribute attribute : sensor.getAttributes()) {
				types.add(attribute.getType());
				String sqlType = "";
				switch (attribute.getType()) {
				case TYPE_BOOL:
					sqlType = "BIT";
					break;
				case TYPE_INT32:
					sqlType = "INT";
					break;
				case TYPE_INT64:
					sqlType = "BIGINT";
					break;
				case TYPE_FLOAT:
					sqlType = "FLOAT";
					break;
				case TYPE_DOUBLE:
					sqlType = "FLOAT";
					break;
				case TYPE_STRING:
					sqlType = "VARCHAR(255)";
					break;
				default:
					sqlType = "VARCHAR(255)";
					break;
				}
				sb.append("`" + attribute.getName() + "` " + sqlType + " NOT NULL,\n");
			}
			sb.append("PRIMARY KEY (`RecordID`));");
			try {
				String command = sb.toString();
				Statement stmt = con.createStatement();
				stmt.execute(command);
				stmt.close();
			} catch (SQLException e) {
				Log.getInstance().append(Log.FLAG_ERROR, "Error setting up a sensor table (" + sensor.getSensorName() + ")");
			}
			sb = new StringBuilder();
			sb.append("CREATE INDEX `idx_SENSOR_" + Long.toHexString(sensor.getSensorID()) + "_UUID` ON `" + config.getSqlDatabase() + "`.`SENSOR_" + Long.toHexString(sensor.getSensorID()) + "` (`UUID`);");
			try {
				String command = sb.toString();
				Statement stmt = con.createStatement();
				stmt.execute(command);
				stmt.close();
			} catch (SQLException e) {
				Log.getInstance().append(Log.FLAG_WARNING, "Error setting up a sensor table (" + sensor.getSensorName() + ") index. Index might already exist.");
			}
			sb = new StringBuilder();
			sb.append("CREATE INDEX `idx_SENSOR_" + Long.toHexString(sensor.getSensorID()) + "_RecordTime` ON `" + config.getSqlDatabase() + "`.`SENSOR_" + Long.toHexString(sensor.getSensorID()) + "` (`RecordTime`);");
			try {
				String command = sb.toString();
				Statement stmt = con.createStatement();
				stmt.execute(command);
				stmt.close();
			} catch (SQLException e) {
				Log.getInstance().append(Log.FLAG_WARNING, "Error setting up a sensor table (" + sensor.getSensorName() + ") index. Index might already exist.");
			}
			sensorsHash.put(sensor.getSensorID(), types);
		}
	}

	private void setupTransactionTable() {
		StringBuilder sb = new StringBuilder();
		sb.append("CREATE TABLE IF NOT EXISTS `" + config.getSqlDatabase() + "`.`Transact` (\n");
		sb.append("`RecordID` INT NOT NULL UNIQUE AUTO_INCREMENT,\n");
		sb.append("`UUID` BINARY(16) NOT NULL,\n");
		sb.append("`UploadTime` BIGINT UNSIGNED NOT NULL,\n");
		sb.append("PRIMARY KEY (`RecordID`));\n");
		try {
			String command = sb.toString();
			Statement stmt = con.createStatement();
			stmt.execute(command);
			stmt.close();
		} catch (SQLException e) {
			Log.getInstance().append(Log.FLAG_ERROR, "Error setting up the transaction table");
		}
		sb = new StringBuilder();
		sb.append("CREATE INDEX `idx_Transact_UUID` ON `" + config.getSqlDatabase() + "`.`Transact` (`UUID`);\n");
		try {
			String command = sb.toString();
			Statement stmt = con.createStatement();
			stmt.execute(command);
			stmt.close();
		} catch (SQLException e) {
			Log.getInstance().append(Log.FLAG_WARNING, "Error setting up the transaction table index. Index might already exist.");
		}
		sb = new StringBuilder();
		sb.append("CREATE INDEX `idx_Transact_UploadTime` ON `" + config.getSqlDatabase() + "`.`Transact` (`UploadTime`);");
		try {
			String command = sb.toString();
			Statement stmt = con.createStatement();
			stmt.execute(command);
			stmt.close();
		} catch (SQLException e) {
			Log.getInstance().append(Log.FLAG_WARNING, "Error setting up the transaction table index. Index might already exist.");
		}
	}

}
