package hr.fer.poslovna.dao;

import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class DB extends SQLiteOpenHelper {
	private static String DB_PATH = "data/data/hr.fer.poslovna.activity/databases/";
	private static String DB_NAME = "AppDB";

	private final Context context;
	private SQLiteDatabase db;

	public DB(Context context) {
		super(context, DB_NAME, null, 1);
		this.context = context;
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
	}

	public void importDBDump() {
		boolean dbExist = checkDataBase();

		if (dbExist) {
		} else {
			this.getReadableDatabase(); // kreira /data/data/.../databases/AppDB database
			try {
				copyDataBase();
			} catch (IOException e) {
				throw new DAOException("Error copying database !", e);
			}
		}
	}

	private boolean checkDataBase() {
		SQLiteDatabase checkDB = null;

		try {
			String path = DB_PATH + DB_NAME;
			checkDB = SQLiteDatabase.openDatabase(path, null, SQLiteDatabase.OPEN_READONLY);
		} catch (SQLiteException e) {
		}
		if (checkDB != null) {
			checkDB.close();
		}

		return checkDB != null ? true : false;
	}

	private void copyDataBase() throws IOException {
		InputStream myInput = context.getAssets().open(DB_NAME);
		String outFileName = DB_PATH + DB_NAME;
		OutputStream myOutput = new FileOutputStream(outFileName);

		byte[] buffer = new byte[1024];
		int length;
		while ((length = myInput.read(buffer)) > 0) {
			myOutput.write(buffer, 0, length);
		}

		myOutput.flush();
		myOutput.close();
		myInput.close();

	}

	public boolean open() {
		if (db != null) {
			return true;
		}

		boolean canOpen = true;
		try {
			String myPath = DB_PATH + DB_NAME;
			db = SQLiteDatabase.openDatabase(myPath, null, SQLiteDatabase.OPEN_READWRITE);
		} catch (SQLException sqle) {
			db = null;
			canOpen = false;
		}
		return canOpen;
	}

	@Override
	public synchronized void close() {
		if (db != null) {
			db.close();
		}
		super.close();
	}

	public Cursor executeQuery(String query) throws Exception {
		Cursor cursor = null;
		try {
			cursor = db.rawQuery(query, null);
		} catch (Exception e) {
			// sql error
			if (db != null) {
				db.close();
			}
			throw new Exception("executeQuery() err, possible SQL syntax error!");
		}
		return cursor;
	}
}