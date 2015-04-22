package ch.ethz.soms.nervous.vm;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

public class SensorStoreConfig {

	private File dir;
	/**
	 * ID of the sensor
	 */

	private long sensorID;
	/**
	 * Last timestamp that has already been uploaded to the server
	 */

	private long lastUploadedTimestamp;
	/**
	 * Last timestamp that has been recorded
	 */
	private long lastWrittenTimestamp;

	/**
	 * Current page number
	 */
	private long currentPage;

	/**
	 * Current write position in the pagefile
	 */
	private long entryNumber;

	public long getSensorID() {
		return sensorID;
	}

	public void setSensorID(long sensorID) {
		this.sensorID = sensorID;
	}

	public long getLastUploadedTimestamp() {
		return lastUploadedTimestamp;
	}

	public void setLastUploadedTimestamp(long lastUploadedTimestamp) {
		this.lastUploadedTimestamp = lastUploadedTimestamp;
	}

	public long getLastWrittenTimestamp() {
		return lastWrittenTimestamp;
	}

	public void setLastWrittenTimestamp(long lastWrittenTimestamp) {
		this.lastWrittenTimestamp = lastWrittenTimestamp;
	}

	public long getCurrentPage() {
		return currentPage;
	}

	public void setCurrentPage(long currentPage) {
		this.currentPage = currentPage;
	}

	public long getEntryNumber() {
		return entryNumber;
	}

	public void setEntryNumber(long entryNumber) {
		this.entryNumber = entryNumber;
	}

	public SensorStoreConfig(File dir, long sensorID) {
		this.dir = dir;
		this.sensorID = sensorID;
		boolean exists = load();
		if (!exists) {
			this.lastUploadedTimestamp = 0;
			this.lastWrittenTimestamp = 0;
			this.currentPage = 0;
			this.entryNumber = 0;
			store();
		}
	}

	public boolean load() {
		boolean success = true;
		FileInputStream fis = null;
		DataInputStream dis = null;
		try {
			File file = new File(dir, "NervousVM/" + Long.toHexString(sensorID) + "G");
			if (!file.exists()) {
				return false;
			}
			fis = new FileInputStream(file);
			dis = new DataInputStream(fis);
			lastUploadedTimestamp = dis.readLong();
			lastWrittenTimestamp = dis.readLong();
			currentPage = dis.readLong();
			entryNumber = dis.readLong();
			dis.close();
		} catch (IOException e) {
			success = false;
		} finally {
			// Cleanup
			if (fis != null) {
				try {
					fis.close();
				} catch (IOException ex) {
				}
			}
			if (dis != null) {
				try {
					dis.close();
				} catch (IOException ex) {
				}
			}
		}
		return success;
	}

	void store() {
		FileOutputStream fos = null;
		DataOutputStream dos = null;
		try {
			File file = new File(dir, "NervousVM/" + Long.toHexString(sensorID) + "G");
			if (!file.exists()) {
				file.createNewFile();
			}
			fos = new FileOutputStream(file);
			dos = new DataOutputStream(fos);
			dos.writeLong(lastUploadedTimestamp);
			dos.writeLong(lastWrittenTimestamp);
			dos.writeLong(currentPage);
			dos.writeLong(entryNumber);
			dos.flush();
			fos.flush();
			dos.close();
			fos.close();
		} catch (IOException ex) {
			System.out.println(ex.getMessage());
		} finally {
			// Cleanup
			if (fos != null) {
				try {
					fos.close();
				} catch (IOException ex) {
				}
			}
			if (dos != null) {
				try {
					dos.close();
				} catch (IOException ex) {
				}
			}
		}
	}

	public void delete() {
		File file = new File(dir, "NervousVM/" + Long.toHexString(sensorID) + "G");
		if (file.exists()) {
			file.delete();
		}
	}
}
