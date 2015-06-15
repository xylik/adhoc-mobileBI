package hr.fer.poslovna.utility;

import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.Properties;

public final class AppSettings {
	private static String jdbcConnString = "jdbc:jtds:sqlserver://192.168.56.2:1433;databaseName=NorthwindStarSchema;user=java;password=java123+";
	private static int rowsPerPage = 20;
	private static int rowLimit = 1000;
	
	private AppSettings(String fileName) {
	}

	public static void loadSettings(String filePath) {
		Properties prop = new Properties();
		try {
			InputStreamReader is = new InputStreamReader(new FileInputStream(filePath), "UTF-8");
			prop.load(is);
		} catch (Exception e) {
			throw new RuntimeException(e.getStackTrace().toString());
		}
		jdbcConnString  = prop.getProperty("connString", null);
	}
	
	public static String getConnString() {
		return jdbcConnString;
	}

	public static void setJdbcConnString(String jdbcConnString) {
		AppSettings.jdbcConnString = jdbcConnString;
	}

	public static int getRowsPerPage() {
		return rowsPerPage;
	}

	public static void setRowsPerPage(int rowsPerPage) {
		AppSettings.rowsPerPage = rowsPerPage;
	}

	public static int getRowLimit() {
		return rowLimit;
	}

	public static void setRowLimit(int rowLimit) {
		AppSettings.rowLimit = rowLimit;
	}

}
