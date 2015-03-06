package ch.ethz.soms.nervous.vm;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.TreeMap;
import java.util.UUID;

import ch.ethz.soms.nervous.nervousproto.SensorUploadProtos.SensorUpload.SensorData;

public class NervousVM {

	public final static long MAX_PAGES = 3;
	public final static long MAX_ENTRIES = 4096;

	private static NervousVM nervousStorage;
	private File dir;
	private UUID uuid;

	private HashMap<Long, TreeMap<PageInterval, PageInterval>> sensorTreeMap;

	public static synchronized NervousVM getInstance(File dir) {
		if (nervousStorage == null) {
			nervousStorage = new NervousVM(dir);
		}
		return nervousStorage;
	}

	public NervousVM(File dir) {
		this.dir = dir;
		File file = new File(dir, "NervousVM");

		if (!file.exists()) {
			try {
				file.mkdir();
			} catch (SecurityException se) {
			}
		}

		boolean hasSTM = loadSTM();
		if (!hasSTM) {
			sensorTreeMap = new HashMap<Long, TreeMap<PageInterval, PageInterval>>();
			writeSTM();
		}
		boolean hasVMConfig = loadVMConfig();
		if (!hasVMConfig) {
			uuid = UUID.randomUUID();
			storeVMConfig();
		}
	}

	private synchronized boolean removeOldPages(long sensorID, long currentPage, long maxPages) {
		boolean success = true;
		TreeMap<PageInterval, PageInterval> treeMap = sensorTreeMap.get(sensorID);
		if (treeMap != null) {
			for (long i = currentPage - maxPages; i >= 0; i--) {
				PageInterval pi = treeMap.get(new PageInterval(new Interval(0, 0), i));
				if (pi == null) {
					break;
				}
				treeMap.remove(pi);
				SensorStorePage stp = new SensorStorePage(dir, sensorID, pi.getPageNumber());
				boolean successEvict = stp.evict();
				success = success && successEvict;
			}
			if (maxPages == 0) {
				// All removed, delete sensor as a whole
				sensorTreeMap.remove(sensorID);
			} else {
				PageInterval pi = treeMap.get(new PageInterval(new Interval(0, 0), currentPage - maxPages + 1));
				// Correct so that the time interval is always from 0 to MAX_LONG in the tree
				if (pi != null) {
					treeMap.remove(pi);
					pi.getInterval().setLower(0);
					treeMap.put(pi, pi);
				}
			}
		}
		return success;
	}

	public synchronized List<SensorData> retrieve(long sensorID, long fromTimestamp, long toTimestamp) {
		TreeMap<PageInterval, PageInterval> treeMap = sensorTreeMap.get(sensorID);
		if (treeMap != null) {
			PageInterval lower = treeMap.get(new PageInterval(new Interval(fromTimestamp, fromTimestamp), -1));
			PageInterval upper = treeMap.get(new PageInterval(new Interval(toTimestamp, toTimestamp), -1));
			ArrayList<SensorData> sensorData = new ArrayList<SensorData>();
			for (long i = lower.getPageNumber(); i <= upper.getPageNumber(); i++) {
				SensorStorePage stp = new SensorStorePage(dir, sensorID, i);
				List<SensorData> sensorDataFromPage = stp.retrieve(fromTimestamp, toTimestamp);
				if (sensorDataFromPage != null) {
					sensorData.addAll(sensorDataFromPage);
				}
			}
			return sensorData;
		} else {
			return null;
		}
	}

	public synchronized void markLastUploaded(long sensorID, long lastUploaded) {
		SensorStoreConfig ssc = new SensorStoreConfig(dir, sensorID);
		ssc.setLastUploadedTimestamp(lastUploaded);
		ssc.store();
	}

	public synchronized UUID getUUID() {
		return uuid;
	}

	public synchronized void newUUID() {
		uuid = UUID.randomUUID();
		storeVMConfig();
	}

	private synchronized boolean loadVMConfig() {
		boolean success = true;
		FileInputStream fis = null;
		DataInputStream dis = null;
		try {
			File file = new File(dir, "NervousVM/VMC");
			if (!file.exists()) {
				return false;
			}
			fis = new FileInputStream(file);
			dis = new DataInputStream(fis);
			uuid = new UUID(dis.readLong(), dis.readLong());
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

	private synchronized void storeVMConfig() {
		FileOutputStream fos = null;
		DataOutputStream dos = null;
		try {
			File file = new File(dir, "NervousVM/VMC");
			if (!file.exists()) {
				file.createNewFile();
			}
			fos = new FileOutputStream(file);
			dos = new DataOutputStream(fos);
			dos.writeLong(uuid.getMostSignificantBits());
			dos.writeLong(uuid.getLeastSignificantBits());
			dos.flush();
			dos.close();
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

	private synchronized boolean loadSTM() {
		boolean success = true;
		FileInputStream fis = null;
		ObjectInputStream ois = null;
		try {
			File file = new File(dir, "NervousVM/STM");
			if (!file.exists()) {
				return false;
			}
			fis = new FileInputStream(file);
			ois = new ObjectInputStream(fis);
			sensorTreeMap = (HashMap<Long, TreeMap<PageInterval, PageInterval>>) (ois.readObject());
			ois.close();
		} catch (IOException e) {
			success = false;
		} catch (ClassNotFoundException e) {
			success = false;
		} finally {
			// Cleanup
			if (fis != null) {
				try {
					fis.close();
				} catch (IOException ex) {
				}
			}
			if (ois != null) {
				try {
					ois.close();
				} catch (IOException ex) {
				}
			}
		}
		return success;
	}

	private synchronized void writeSTM() {
		FileOutputStream fos = null;
		ObjectOutputStream oos = null;
		try {
			File file = new File(dir, "NervousVM/STM");
			if (!file.exists()) {
				file.createNewFile();
			}
			fos = new FileOutputStream(file, false);
			oos = new ObjectOutputStream(fos);
			oos.writeObject(sensorTreeMap);
			oos.flush();
			oos.close();
		} catch (IOException ex) {
		} finally {
			// Cleanup
			if (fos != null) {
				try {
					fos.close();
				} catch (IOException ex) {
				}
			}
			if (oos != null) {
				try {
					oos.close();
				} catch (IOException ex) {
				}
			}
		}
	}

	public synchronized boolean storeSensor(long sensorID, SensorData sensorData) {
		if (sensorData != null) {
			boolean stmHasChanged = false;
			boolean success = true;
			SensorStoreConfig ssc = new SensorStoreConfig(dir, sensorID);

			TreeMap<PageInterval, PageInterval> treeMap = sensorTreeMap.get(sensorID);
			if (treeMap == null) {
				treeMap = new TreeMap<PageInterval, PageInterval>();
				// Open the initial interval
				PageInterval piFirst = new PageInterval(new Interval(0, Long.MAX_VALUE), 0);
				treeMap.put(piFirst, piFirst);
				sensorTreeMap.put(sensorID, treeMap);
				stmHasChanged = true;
			}

			// Reject non monotonically increasing timestamps
			if (ssc.getLastWrittenTimestamp() - sensorData.getRecordTime() >= 0) {
				return false;
			}

			// Add new page if the last one is full
			if (ssc.getEntryNumber() == MAX_ENTRIES) {
				ssc.setCurrentPage(ssc.getCurrentPage() + 1);
				ssc.setEntryNumber(0);

				// Close the last interval
				PageInterval piLast = treeMap.get(new PageInterval(new Interval(0, 0), ssc.getCurrentPage() - 1));
				treeMap.remove(piLast);
				piLast.getInterval().setUpper(ssc.getLastWrittenTimestamp());
				treeMap.put(piLast, piLast);
				// Open the next interval
				PageInterval piNext = new PageInterval(new Interval(ssc.getLastWrittenTimestamp() + 1, Long.MAX_VALUE), ssc.getCurrentPage());
				treeMap.put(piNext, piNext);

				// Remove old pages
				removeOldPages(sensorID, ssc.getCurrentPage(), MAX_PAGES);
				stmHasChanged = true;
			}

			SensorStorePage ssp = new SensorStorePage(dir, ssc.getSensorID(), ssc.getCurrentPage());
			ssp.store(sensorData, ssc.getEntryNumber());

			ssc.setEntryNumber(ssc.getEntryNumber() + 1);

			ssc.setLastWrittenTimestamp(sensorData.getRecordTime());
			ssc.store();
			if (stmHasChanged) {
				writeSTM();
			}
			return success;
		}
		return false;
	}

	public class PageInterval implements Comparable<PageInterval>, Serializable {

		private static final long serialVersionUID = -3883324724432537835L;

		private long pageNumber;
		private Interval interval;

		public long getPageNumber() {
			return pageNumber;
		}

		public void setPageNumber(long pageNumber) {
			this.pageNumber = pageNumber;
		}

		public Interval getInterval() {
			return interval;
		}

		public void setInterval(Interval interval) {
			this.interval = interval;
		}

		public PageInterval(Interval interval, long pageNumber) {
			this.interval = interval;
			this.pageNumber = pageNumber;
		}

		public String toString() {
			return interval.toString() + "->(" + Long.toHexString(pageNumber) + ")";
		}

		@Override
		public int compareTo(PageInterval o) {
			if (this.pageNumber == -1) {
				return this.interval.compareTo(o.interval);
			} else {
				if (this.pageNumber > o.pageNumber) {
					return 1;
				} else if (this.pageNumber < o.pageNumber) {
					return -1;
				} else {
					return 0;
				}
			}
		}

	}

	public class Interval implements Comparable<Interval>, Serializable {
		private static final long serialVersionUID = 1255883524821812371L;

		public Interval(long lower, long upper) {
			this.lower = lower;
			this.upper = upper;
		}

		private long lower;
		private long upper;

		public Long getLower() {
			return lower;
		}

		public void setLower(long lower) {
			this.lower = lower;
		}

		public Long getUpper() {
			return upper;
		}

		public void setUpper(long upper) {
			this.upper = upper;
		}

		@Override
		public int compareTo(Interval another) {
			if ((another.lower >= this.lower && another.upper <= this.upper) || (another.lower <= this.lower && another.upper >= this.upper)) {
				return 0;
			} else if (another.lower > this.upper) {
				return -1;
			} else {
				return 1;
			}
		}

		public String toString() {
			return "[" + String.valueOf(lower) + "," + String.valueOf(upper) + "]";
		}
	}

	public long getLastUploadedTimestamp(long sensorID) {
		SensorStoreConfig ssc = new SensorStoreConfig(dir, sensorID);
		return ssc.getLastUploadedTimestamp();
	}

	public void setLastUploadedTimestamp(long sensorID, long timestamp) {
		SensorStoreConfig ssc = new SensorStoreConfig(dir, sensorID);
		ssc.setLastUploadedTimestamp(timestamp);
		ssc.store();
	}

	public void deleteSensor(long sensorID) {
		SensorStoreConfig ssc = new SensorStoreConfig(dir, sensorID);
		removeOldPages(sensorID, ssc.getCurrentPage(), 0);
		ssc.delete();
	}

	public long[] getSensorStorageSize(long sensorID) {
		long[] size = { 0, 0 };
		SensorStoreConfig ssc = new SensorStoreConfig(dir, sensorID);
		for (int i = 0; i < MAX_PAGES; i++) {
			SensorStorePage ssp = new SensorStorePage(dir, sensorID, ssc.getCurrentPage());
			size[0] += ssp.getStoreSize();
			size[1] += ssp.getIndexSize();
		}
		return size;
	}
}
