package hr.fer.pi.utility;

import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Properties;

public class AppSettings {
	private static String jdbcConnString;
	
	private AppSettings(String fileName) {
	}

	public static void loadSettings(String filePath) {
		Properties prop = new Properties();
		try {
			InputStreamReader is = new InputStreamReader(new FileInputStream(filePath), StandardCharsets.UTF_8);
			prop.load(is);
		} catch (Exception e) {
			throw new RuntimeException(e.getStackTrace().toString());
		}
		jdbcConnString  = prop.getProperty("connString", null);
	}
	
	public static String getConnString() {
		return jdbcConnString;
	}

}
