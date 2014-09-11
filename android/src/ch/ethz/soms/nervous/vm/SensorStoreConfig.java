package ch.ethz.soms.nervous.vm;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import android.content.Context;

public class SensorStoreConfig {

	private Context context;
	/**
	 * ID of the sensor
	 */
	private long sensorID;
	/**
	 * Last timestamp that has already been uploaded to the server
	 */
	private long lastUploadedTimestamp;
	/**
	 * First timestamp that has been recorded
	 */
	private long firstWrittenTimestamp;
	/**
	 * Last timestamp that has been recorded
	 */
	private long lastWrittenTimestamp;
	public long getFirstWrittenTimestamp() {
		return firstWrittenTimestamp;
	}

	public void setFirstWrittenTimestamp(long firstWrittenTimestamp) {
		this.firstWrittenTimestamp = firstWrittenTimestamp;
	}

	/**
	 * Current page number
	 */
	private long currentPage;
	/**
	 * Current write position in the pagefile
	 */
	private long writeOffset;
	/**
	 * Current entry number within the page/tree
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

	public long getWriteOffset() {
		return writeOffset;
	}

	public void setWriteOffset(long writeOffset) {
		this.writeOffset = writeOffset;
	}

	public long getEntryNumber() {
		return entryNumber;
	}

	public void setEntryNumber(long entryNumber) {
		this.entryNumber = entryNumber;
	}

	public SensorStoreConfig(Context context, long sensorID) {
		this.sensorID = sensorID;
		boolean exists = load();
		if (!exists) {
			this.lastUploadedTimestamp = 0;
			this.firstWrittenTimestamp = 0;
			this.lastWrittenTimestamp = 0;
			this.currentPage = 0;
			this.writeOffset = 0;
			this.entryNumber = 0;
			store();
		}
	}

	public boolean load() {
		boolean success = true;
		FileInputStream fis = null;
		DataInputStream dis = null;
		try {
			File file = new File(context.getFilesDir(), "NervousVM\\" + Long.toHexString(sensorID) + "C");
			if (!file.exists()) {
				return false;
			}
			fis = new FileInputStream(file);
			dis = new DataInputStream(fis);
			lastUploadedTimestamp = dis.readLong();
			firstWrittenTimestamp = dis.readLong();
			lastWrittenTimestamp = dis.readLong();
			currentPage = dis.readLong();
			writeOffset = dis.readLong();
			entryNumber = dis.readLong();
			dis.close();
			fis.close();
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
			File file = new File(context.getFilesDir(), "NervousVM\\" + Long.toHexString(sensorID) + "C");
			if (!file.exists()) {
				file.createNewFile();
			}
			fos = new FileOutputStream(file);
			dos = new DataOutputStream(fos);
			dos.writeLong(lastUploadedTimestamp);
			dos.writeLong(lastWrittenTimestamp);
			dos.writeLong(currentPage);
			dos.writeLong(writeOffset);
			dos.writeLong(entryNumber);
			dos.flush();
			fos.flush();
			dos.close();
			fos.close();
		} catch (IOException ex) {
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
}
