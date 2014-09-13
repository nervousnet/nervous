package ch.ethz.soms.nervous.vm;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import ch.ethz.soms.nervous.nervousproto.SensorUploadProtos.SensorUpload.SensorData;

public class SensorStorePage {

	private File dir;
	private SensorStoreTree sst;
	private long sensorID;
	private long currentPage;

	public SensorStorePage(File dir, long sensorID, long currentPage) {
		this.dir = dir;
		this.sensorID = sensorID;
		this.currentPage = currentPage;
		sst = new SensorStoreTree(dir, sensorID, currentPage);
	}

	public long store(SensorData protoSensor, long currentEntry) {
		long fileOffset = -1;
		FileOutputStream fos = null;
		try {
			File file = new File(dir, "NervousVM/" + Long.toHexString(sensorID) + "P" + Long.toHexString(currentPage));
			if (!file.exists()) {
				file.createNewFile();
			}
			fileOffset = file.length();
			System.out.println("Current entry: "+currentEntry+", offset: "+fileOffset);
			fos = new FileOutputStream(file, true);
			protoSensor.writeDelimitedTo(fos);
			fos.flush();
			fos.close();
			sst.addEntry(currentEntry, protoSensor.getRecordTime(), fileOffset);
		} catch (IOException ex) {
			fileOffset = -1;
		} finally {
			// Cleanup
			if (fos != null) {
				try {
					fos.close();
				} catch (IOException ex) {
				}
			}
		}
		return fileOffset;
	}

	private List<SensorData> read(long startOffset, long endOffset) {

		List<SensorData> sensorDataList = null;
		FileInputStream fis = null;
		LimitInputStream lis = null;
		try {
			File file = new File(dir, "NervousVM/" + Long.toHexString(sensorID) + "C");
			if (!file.exists()) {
				return null;
			}
			if (startOffset < 0) {
				startOffset = 0;
			}
			if (endOffset < 0) {
				endOffset = file.length();
			}
			fis = new FileInputStream(file);
			lis = new LimitInputStream(fis, endOffset);
			lis.skip(startOffset);
			boolean success = true;
			sensorDataList = new ArrayList<SensorData>();
			while (success) {
				SensorData sensorData = null;
				try {
					sensorData = SensorData.parseDelimitedFrom(lis);
				} catch (IOException ex) {
					success = false;
				}
				if (sensorData != null) {
					sensorDataList.add(sensorData);
				} else {
					success = false;
				}
			}
			lis.close();
		} catch (IOException e) {
			sensorDataList = null;
		} finally {
			// Cleanup
			if (fis != null) {
				try {
					fis.close();
				} catch (IOException ex) {
				}
			}
			if (lis != null) {
				try {
					lis.close();
				} catch (IOException ex) {
				}
			}
		}
		return sensorDataList;
	}

	public List<SensorData> retrieveAll() {
		return read(-1, -1);
	}

	public List<SensorData> retrieve(long fromTimestamp, long toTimestamp) {
		long startOffset = sst.findEntry(fromTimestamp);
		long endOffset = sst.findEntry(toTimestamp);
		if (startOffset < 0 || endOffset < 0) {
			return null;
		} else {
			return read(startOffset, endOffset);
		}
	}

}
