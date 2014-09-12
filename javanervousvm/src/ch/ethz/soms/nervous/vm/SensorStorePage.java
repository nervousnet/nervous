package ch.ethz.soms.nervous.vm;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

import ch.ethz.soms.nervous.nervousproto.SensorUploadProtos.SensorUpload.SensorData;

public class SensorStorePage {

	private static final String DEBUG_TAG = "SensorStorePage";

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
		DataOutputStream dos = null;
		try {
			File file = new File(dir, "NervousVM\\" + Long.toHexString(sensorID) + "P" + Long.toHexString(currentPage));
			if (!file.exists()) {
				file.createNewFile();
			}
			fileOffset = file.length();
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
			if (dos != null) {
				try {
					dos.close();
				} catch (IOException ex) {
				}
			}
		}
		return fileOffset;
	}

	private List<SensorData> read(long startOffset, long endOffset)
	{
		List<SensorData> sensorData = null;
		FileInputStream fis = null;
		LimitInputStream lis = null;
		try {
			File file = new File(dir, "NervousVM\\" + Long.toHexString(sensorID) + "C");
			if (!file.exists()) {
				return null;
			}
			fis = new FileInputStream(file);
			lis = new LimitInputStream(fis, endOffset);
			lis.skip(startOffset);
			SensorData.parseDelimitedFrom(lis);
			lis.close();
			fis.close();
		} catch (IOException e) {
			sensorData = null;
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
		return sensorData;
	}
	
	
	public List<SensorData> retrieveAll() {
		
		return null;
	}

	public List<SensorData> retrieve(long fromTimestamp, long toTimestamp) {
		long startOffset = sst.findEntry(fromTimestamp);
		long endOffset = sst.findEntry(toTimestamp);

		return null;
	}

}
