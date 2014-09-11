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
	private long sensorID;
	private long lastUploadedTimestamp;
	private long lastWrittenTimestamp;
	private long currentPage;
	private long writeOffset;
	private long treeOffset;

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

	public long getTreeOffset() {
		return treeOffset;
	}

	public void setTreeOffset(long treeOffset) {
		this.treeOffset = treeOffset;
	}

	public SensorStoreConfig(Context context, long sensorID) {
		this.sensorID = sensorID;
		boolean exists = load();
		if (!exists) {
			this.lastUploadedTimestamp = 0;
			this.lastWrittenTimestamp = 0;
			this.currentPage = 0;
			this.writeOffset = 0;
			this.treeOffset = 0;
			store();
		}
	}

	private boolean load() {
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
			lastWrittenTimestamp = dis.readLong();
			currentPage = dis.readLong();
			writeOffset = dis.readLong();
			treeOffset = dis.readLong();
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

	private void store() {
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
			dos.writeLong(treeOffset);
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
