package ch.ethz.soms.nervous.vm;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;


public class SensorStoreTree {

	// Actually not a tree. Binary search only, really.

	private final static long MAX_ENTRIES = 4095;

	private File dir;
	private long sensorID;
	private long currentPage;

	public SensorStoreTree(File dir, long sensorID, long currentPage) {
		this.dir = dir;
		this.sensorID = sensorID;
		this.currentPage = currentPage;
	}

	/**
	 * Binary search for the next timestamp after @param timestamp
	 * 
	 * @param timestamp
	 * @return offset to start reading from in the page
	 */
	public long findEntry(long timestamp) {
		long fileOffset = -1;
		RandomAccessFile raf = null;
		try {
			File file = new File(dir, "NervousVM\\" + Long.toHexString(sensorID) + "T" + Long.toHexString(currentPage));
			if (!file.exists()) {
				return -1;
			}
			raf = new RandomAccessFile(file, "r");
			long lowerbound = 0;
			long upperbound = MAX_ENTRIES - 1;
			long posTimestamp = 0;
			while (upperbound > lowerbound) {
				long readPosition = lowerbound + ((upperbound - lowerbound) / 2);
				raf.seek(readPosition * 16);
				posTimestamp = raf.readLong();
				if (posTimestamp > timestamp) {
					upperbound = readPosition;
				} else {
					lowerbound = readPosition + 1;
				}
			}
			raf.seek(lowerbound);
			posTimestamp = raf.readLong();
			if (posTimestamp > timestamp) {
				fileOffset = raf.readLong();
			} else {
				fileOffset = -1;
			}
			raf.close();
		} catch (IOException e) {
			fileOffset = -1;
		} finally {
			// Cleanup
			if (raf != null) {
				try {
					raf.close();
				} catch (IOException ex) {
				}
			}
		}
		return fileOffset;
	}

	public void addEntry(long currentEntry, long timestamp, long fileOffset) {
		long writeOffset = 16 * currentEntry;
		FileOutputStream fos = null;
		DataOutputStream dos = null;
		RandomAccessFile raf = null;
		try {
			File file = new File(dir, "NervousVM\\" + Long.toHexString(sensorID) + "T" + Long.toHexString(currentPage));
			if (!file.exists()) {
				file.createNewFile();
				fos = new FileOutputStream(file, true);
				dos = new DataOutputStream(fos);
				// Initialize empty structure
				for (int i = 0; i < MAX_ENTRIES; i++) {
					dos.writeLong(-1);
					dos.writeLong(-1);
				}
				fos.flush();
				fos.close();
			}

			raf = new RandomAccessFile(file, "rw");
			raf.seek(writeOffset);
			raf.writeLong(timestamp);
			raf.writeLong(fileOffset);
			raf.close();

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
			if (raf != null) {
				try {
					raf.close();
				} catch (IOException ex) {
				}
			}
		}
	}

}
