package SQLConnection;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Classe appartenente al Pattern Architetturale  SINGLETON
 * Instaura una ed una sola connessione per volta con il DB 
 * @author Lombardi
 * @version $Revision: 1.0 $
 */
public class ConnectionDB {
	
	private static Connection istanza;
	
	/**
	 * Metodo costruttore della classe ConnectionDB
	 * @param URL : localhost 
	 * @param USER : root 
	 * @param PASS : root
	
	 * @return istanza della connessione */
	public static Connection getInstance(String URL, String USER, String PASS) {
	    
		if (istanza == null){
			try {
				istanza =  DriverManager.getConnection(URL, USER, PASS);
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	    }
	    return istanza;
	  }
}



	