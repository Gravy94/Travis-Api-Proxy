package proxy;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;

import SQLConnection.ConnectionDB;

/**
 * Class that initialize DB 'travis-proxy'
 * 
 * @author Michele Lombardi
 *
 */
public class LoadDb {

	private Connection conn;
	private PreparedStatement ps;
	private Path path;

	/**
	 * Constructor method that initialize DB 'travis-proxy'
	 * 
	 * @param url_db
	 *            contains url+port to db
	 * @param db
	 *            contains database name
	 * @param username
	 *            contains database username
	 * @param password
	 *            contains database password
	 */
	LoadDb(String url_db, String db, String username, String password) {

		//File f = new File("travis-proxy_utilizations.sql");	// With Jar File
		 File f = new File("src/files/travis-proxy_utilizations.sql");	// With Eclipse
		if (!f.exists()) {
			System.err.println("Error: Configuration DB file .sql does not exists!");
			return;

		} else {
			this.path = f.toPath();
		}

		conn = ConnectionDB.getInstance(url_db, username, password);

		try {
			// Creating DB
			this.ps = this.conn.prepareStatement("CREATE DATABASE IF NOT EXISTS `" + db + "`");
			this.ps.executeUpdate();
			// Using DB
			this.ps = this.conn.prepareStatement("USE `" + db + "`");
			this.ps.executeQuery();

			// List used to read in file
			// Getting query to create Table
			ArrayList<String> listFileSql = new ArrayList<String>();
			try {
				listFileSql = (ArrayList<String>) Files.readAllLines(this.path, Charset.forName("UTF-8"));
			} catch (IOException e) {
				System.out.println("Error: configuration file is incorrect!");
			}

			String query = "";
			for (int i = 0; i < listFileSql.size(); i++)
				query += listFileSql.get(i);
			this.ps = this.conn.prepareStatement(query);
			this.ps.executeUpdate();
			
			

		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Main testing class
	 * 
	 * @param args
	 */
	public static void main(String args[]) {
		new LoadDb("jdbc:mysql://localhost:3306/","travis-proxy","root","root");
	}
}
