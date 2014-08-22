package ch.ethz.soms.nervous.router.sql;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;

import ch.ethz.soms.nervous.router.Configuration;
import ch.ethz.soms.nervous.router.utils.Log;

public class SqlSetup {

	private Connection con;
	private Configuration config;

	public SqlSetup(Connection con, Configuration config) {
		this.con = con;
		this.config = config;
	}

	public void setupTables() {
		setupTransactionTable();
		setupSensorTables();
	}

	private void setupSensorTables() {
		for (SqlSensorConfiguration sensors : config.getSensors()) {
			StringBuilder sb = new StringBuilder();
			sb.append("CREATE TABLE IF NOT EXISTS `" + config.getSqlDatabase() + "`.`SENSOR_" + Long.toHexString(sensors.getSensorID()) + "` (\n");
			sb.append("`UUID` BIGINT UNSIGNED NOT NULL,\n");
			sb.append("`RecordTime` BIGINT UNSIGNED NOT NULL,\n");
			for (SqlSensorAttribute attribute : sensors.getAttributes()) {
				sb.append("`" + attribute.getName() + "` "+attribute.getType()+" NOT NULL,\n");
			}
			sb.append("PRIMARY KEY (`UUID`, `RecordTime`));");
			String command = sb.toString();
			try {
				Statement stmt = con.createStatement();
				stmt.execute(command);
				stmt.close();
			} catch (SQLException e) {
				Log.getInstance().append(Log.FLAG_ERROR, "Error setting up a sensor table (" + sensors.getSensorName() + ")");
			}
		}
	}

	private void setupTransactionTable() {
		StringBuilder sb = new StringBuilder();
		sb.append("CREATE TABLE IF NOT EXISTS `" + config.getSqlDatabase() + "`.`Transact` (\n");
		sb.append("`UUID` BIGINT UNSIGNED NOT NULL,\n");
		sb.append("`UploadTime` BIGINT UNSIGNED NOT NULL,\n");
		sb.append("`RangeFrom` BIGINT UNSIGNED NOT NULL,\n");
		sb.append("`RangeTo` BIGINT UNSIGNED NOT NULL,\n");
		sb.append("PRIMARY KEY (`UUID`, `UploadTime`));");
		String command = sb.toString();
		try {
			Statement stmt = con.createStatement();
			stmt.execute(command);
			stmt.close();
		} catch (SQLException e) {
			Log.getInstance().append(Log.FLAG_ERROR, "Error setting up the transaction table");
		}
	}

}
