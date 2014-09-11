package ch.ethz.soms.nervous.vm;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.HashMap;
import java.util.TreeMap;
import java.util.UUID;

import android.content.Context;
import ch.ethz.soms.nervous.android.SensorDesc;

public class NervousVM {

	private static NervousVM nervousStorage;
	private Context context;
	private UUID uuid;

	private HashMap<Long, TreeMap<Long, Long>> sensorTreeMap;

	public static synchronized NervousVM getInstance(Context context) {
		if (nervousStorage == null) {
			nervousStorage = new NervousVM(context);
		}
		return nervousStorage;
	}

	public NervousVM(Context context) {
		this.context = context;
		boolean hasSTM = loadSTM();
		if (!hasSTM) {
			sensorTreeMap = new HashMap<Long, TreeMap<Long, Long>>();
			writeSTM();
		}
		boolean hasVMConfig = loadVMConfig();
		if (!hasVMConfig) {
			uuid = UUID.randomUUID();
			storeVMConfig();
		}
	}

	
	public synchronized UUID getUUID()
	{
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
			File file = new File(context.getFilesDir(), "NervousVM\\VMC");
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
			File file = new File(context.getFilesDir(), "NervousVM\\VMC");
			if (!file.exists()) {
				file.createNewFile();
			}
			fos = new FileOutputStream(file);
			dos = new DataOutputStream(fos);
			dos.writeLong(uuid.getMostSignificantBits());
			dos.writeLong(uuid.getLeastSignificantBits());
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

	private synchronized boolean loadSTM() {
		boolean success = true;
		FileInputStream fis = null;
		ObjectInputStream ois = null;
		try {
			File file = new File(context.getFilesDir(), "NervousVM\\STM");
			if (!file.exists()) {
				return false;
			}
			fis = new FileInputStream(file);
			ois = new ObjectInputStream(fis);
			sensorTreeMap = (HashMap<Long, TreeMap<Long, Long>>) (ois.readObject());
			ois.close();
			fis.close();
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
			File file = new File(context.getFilesDir(), "NervousVM\\STM");
			if (!file.exists()) {
				file.createNewFile();
			}
			fos = new FileOutputStream(file);
			oos = new ObjectOutputStream(fos);
			oos.writeObject(sensorTreeMap);
			oos.flush();
			fos.flush();
			oos.close();
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
			if (oos != null) {
				try {
					oos.close();
				} catch (IOException ex) {
				}
			}
		}
	}

	public synchronized void storeSensor(SensorDesc sensorDesc) {
		long sensorID = sensorDesc.getSensorIdentifier();
		SensorStoreConfig ssc = new SensorStoreConfig(context, sensorID);
		
		if()
		{
			addPage();
		}
	}

	private synchronized void removePage() {

	}

	private synchronized void addPage(long sensorID) {
		store
	}

	private synchronized void addSensor() {

	}
}
