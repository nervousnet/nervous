package ch.ethz.soms.nervous.vm;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;

public class SensorStoreTree {

	// Actually not a tree. Binary search only, really.

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
	 * @param mode
	 * @return offset to start reading from in the page
	 */
	public long findEntry(long timestamp, boolean mode) {
		long fileOffset = -1;
		RandomAccessFile raf = null;
		try {
			File file = new File(dir, "NervousVM/" + Long.toHexString(sensorID) + "T" + Long.toHexString(currentPage));
			if (!file.exists()) {
				return -1;
			}
			raf = new RandomAccessFile(file, "r");
			long lowerbound = 0;
			long upperbound = file.length() / 16 - 1;
			long posTimestamp = 0;
			while (upperbound > lowerbound) {
				long readPosition = lowerbound + ((upperbound - lowerbound) / 2);
				raf.seek(readPosition * 16);
				posTimestamp = raf.readLong();
				if (posTimestamp > timestamp) {
					upperbound = readPosition - 1;
				} else if (posTimestamp < timestamp) {
					lowerbound = readPosition + 1;
				} else {
					lowerbound = readPosition;
					upperbound = readPosition;
				}
			}
			// Fix if only one entry exists
			raf.seek(lowerbound * 16);
			posTimestamp = raf.readLong();
			raf.seek(lowerbound * 16);
			// Value correction (highest lower and lowest higher bound)
			lowerbound = mode ? ((posTimestamp > timestamp) ? lowerbound -= 1 : lowerbound) : ((posTimestamp < timestamp) ? lowerbound += 1 : lowerbound);
			// Safety border check
			lowerbound = Math.max(0, Math.min(file.length() / 16 - 1, lowerbound));
			raf.seek(lowerbound * 16);
			posTimestamp = raf.readLong();
			fileOffset = raf.readLong();
			raf.close();
			// Out of range test
			fileOffset = (!mode && posTimestamp < timestamp) ? -1 : fileOffset;
			fileOffset = (mode && posTimestamp > timestamp) ? -1 : fileOffset;
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
		RandomAccessFile raf = null;
		try {
			File file = new File(dir, "NervousVM/" + Long.toHexString(sensorID) + "T" + Long.toHexString(currentPage));
			if (!file.exists()) {
				file.createNewFile();
			}

			raf = new RandomAccessFile(file, "rw");
			raf.seek(writeOffset);
			raf.writeLong(timestamp);
			raf.writeLong(fileOffset);
			raf.close();

		} catch (IOException ex) {
		} finally {
			// Cleanup
			if (raf != null) {
				try {
					raf.close();
				} catch (IOException ex) {
				}
			}
		}
	}

	public boolean evict() {
		File file = new File(dir, "NervousVM/" + Long.toHexString(sensorID) + "T" + Long.toHexString(currentPage));
		if (file.exists()) {
			return file.delete();
		} else {
			return true;
		}
	}

	public long getSize() {
		File file = new File(dir, "NervousVM/" + Long.toHexString(sensorID) + "T" + Long.toHexString(currentPage));
		if (file.exists()) {
			return file.length();
		} else {
			return 0;
		}
	}

}
