package proxy;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;

public class ConfigFile {
	private HashMap<String, String> hm;
	private String fileName;
	private Path path;

	private int proxy_port = 0;
	private String url = null;
	private String db = null;
	private String user_db = null;
	private String password_db = null;
	private String db_port = null;

	private String url_db = null;

	ConfigFile(String file) {
		File f = new File(file);
		if (!f.exists()) {
			System.err.println("Error: configuration file does not exist!");
		} else {
			this.path = f.toPath();
			hm = this.getConfigData();
			System.out.println("Initializing server on port: " + this.getProxyPort());
			System.out.println("Connecting to DBMS: " + this.getUrlDb()+this.getDb());
			System.out.println("With Users: " + this.getUserDb());
			System.out.println("and Password: " + this.getPasswordDb());
		}
	}

	public static void main(String args[]) throws IOException {
		ConfigFile cf = new ConfigFile("ConfigFile.txt");
	}

	public HashMap<String, String> getConfigData() {
		// List used as comparator
		ArrayList<String> listParam = new ArrayList<String>();
		listParam.add("proxy_port");
		listParam.add("url");
		listParam.add("db");
		listParam.add("user_db");
		listParam.add("password_db");
		listParam.add("db_port");

		// HashMap to return completed
		HashMap<String, String> tempHm = new HashMap<String, String>();
		tempHm.put("proxy_port", "null");
		tempHm.put("url", "null");
		tempHm.put("db", "null");
		tempHm.put("user_db", "null");
		tempHm.put("password_db", "null");
		tempHm.put("db_port", "null");

		// List used to read in file
		ArrayList<String> listFile = new ArrayList<String>();
		try {
			listFile = (ArrayList<String>) Files.readAllLines(this.path, Charset.forName("UTF-8"));
		} catch (IOException e) {
			System.out.println("Error: configuration file is incorrect!");
			return null;
		}

		// verify the correctness of string in file
		boolean allRight = true;
		int i = 0;

		while (i < tempHm.size() & allRight) {
			int sizeContentListParam = listParam.get(i).length();
			int sizeContentListFile = listFile.get(i).length();
			// comparison
			if (((String) listParam.get(i) + "=").equals(listFile.get(i).substring(0, sizeContentListParam + 1))) {
				// TODO you can cech if the value of config parameters are
				// .lenght >0
				tempHm.replace(listParam.get(i),
						listFile.get(i).substring(sizeContentListParam + 1, sizeContentListFile));
			} else {
				allRight = false;
				System.out.println("Error: file config parameters are incorrect!");
			}
			i++;
		}
		if (allRight) {
			this.proxy_port = Integer.parseInt(tempHm.get("proxy_port"));
			this.url = tempHm.get("url");
			this.db = tempHm.get("db");
			this.user_db = tempHm.get("user_db");
			this.password_db = tempHm.get("password_db");
			this.db_port = tempHm.get("db_port");
			this.url_db = tempHm.get("url") + ":" + tempHm.get("db_port") + "/";

			return tempHm;
		} else
			return null;
	}

	public int getProxyPort() {
		return this.proxy_port;
	}

	public String getUrlDb() {

		return this.url_db;
	}

	public String getUserDb() {
		return this.user_db;
	}

	public String getPasswordDb() {
		return this.password_db;
	}

	public String getDb() {
		return this.db;
	}

	private String getDbPort() {
		return this.db_port;
	}

}
