package ch.ethz.soms.nervous.utils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;

public class DBAccessHelper extends SQLiteOpenHelper {

	private SQLiteDatabase sqldb;

	private final String name;
	private final String dbPath;
	private final Context context;

	public DBAccessHelper(Context context, String name) {
		super(context, name, null, 1);
		this.dbPath = context.getDatabasePath(name).getAbsolutePath();
		this.context = context;
		this.name = name;
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub
	}

	public void createDB() {
		boolean exists = checkDB();
		if (exists) {

		} else {
			this.getReadableDatabase();
			copyDB();
		}
	}

	public synchronized void copyDB() {
		try {
			InputStream is = context.getAssets().open(name);
			OutputStream os = new FileOutputStream(dbPath);
			byte[] buffer = new byte[4 * 1024];
			int length;
			while ((length = is.read(buffer)) > 0) {
				os.write(buffer, 0, length);
			}
			os.flush();
			os.close();
			is.close();
		} catch (IOException e) {
		}
	}

	public synchronized void openDB() {
		sqldb = SQLiteDatabase.openDatabase(dbPath, null,
				SQLiteDatabase.OPEN_READWRITE
						| SQLiteDatabase.NO_LOCALIZED_COLLATORS);
	}

	public synchronized boolean checkDB() {
		SQLiteDatabase sqrdb = null;
		try {
			sqrdb = SQLiteDatabase.openDatabase(dbPath, null,
					SQLiteDatabase.OPEN_READONLY
							| SQLiteDatabase.NO_LOCALIZED_COLLATORS);
		} catch (SQLiteException e) {
			return false;
		}
		if (sqrdb != null) {
			sqrdb.close();
		}
		return sqrdb != null;
	}

	public synchronized void closeDB() {
		if (sqldb != null && sqldb.isOpen()) {
			sqldb.close();
			sqldb = null;
		}
	}

	public SQLiteDatabase getDatabase() {
		return sqldb;
	}
	
	public File getDatabaseFile() {
		return new File(dbPath);
	}
}
