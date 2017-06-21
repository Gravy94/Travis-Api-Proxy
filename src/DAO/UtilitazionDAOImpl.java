package DAO;

import SQLConnection.ConnectionDB;
import java.sql.*;
import java.util.ArrayList;
import java.util.Collection;

import proxy.Utilization;

/**
 * Classe che concretizza l'interfaccia DAO con le implementazioni dei metodi
 * CRUD per le operazioni sul DB nella tabbella
 * 
 * @author Lombardi
 *
 * @version $Revision: 1.0 $
 */
public class UtilitazionDAOImpl implements DAO {
	// Connection conn =
	// ConnectionDB.getInstance("jdbc:mysql://localhost:3306/travis-proxy",
	// "root", "root");
	Connection conn = ConnectionDB.getInstance(null, null, null);
	PreparedStatement ps;

	/*public UtilitazionDAOImpl(Connection c) {
		conn = c;
	}*/

	public boolean Insert(Utilization utilization) {
		System.err.println("IN insert(utilization)");
		System.out.println(utilization.toString());
		try {

			ps = conn.prepareStatement(
					"INSERT INTO utilizations (teamId, teamName, channelId, channelName, slugRepo, incomingWebhook)VALUES (?,?,?,?,?,?)");
			ps.setString(1, utilization.getTeamId());
			ps.setString(2, utilization.getTeamName());
			ps.setString(3, utilization.getChannelId());
			ps.setString(4, utilization.getChannelName());
			ps.setString(5, utilization.getSlugRepo());
			ps.setString(6, utilization.getIncomingWebHook());
			ps.executeUpdate();

			return true;
		} catch (SQLException e) {

			e.printStackTrace();
			return false;
		}
	}

	public boolean Delete(Utilization utilization) {

		try {

			ps = conn.prepareStatement("DELETE FROM utilizations WHERE teamId= ? AND channelId= ?");
			ps.setString(1, utilization.getTeamId());
			ps.setString(2, utilization.getChannelId());
			ps.executeUpdate();

			return true;
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
	}

	public ArrayList<Utilization> Read() {
		System.err.println("IN ArrayList<Utilization> Read()");
		Utilization utilization;
		ArrayList<Utilization> list = new ArrayList<Utilization>();
		try {
			ResultSet resultSet;
			ps = conn.prepareStatement("SELECT * FROM utilizations");
			resultSet = ps.executeQuery();

			while (resultSet.next()) {
				// resultSet.getString()
				utilization = new Utilization(resultSet.getString(2), resultSet.getString(3), resultSet.getString(4),
						resultSet.getString(5), resultSet.getString(6), resultSet.getString(7));
				list.add(utilization);
			}
		} catch (SQLException e) {
			System.err.println("ERRORE NEL DB (LETTURA)");
		}
		return list;
	}

	public boolean Read(Utilization utilization) {
		int lenght = 0;
		// System.err.println("IN Read(utilization)");
		// System.out.println(utilization.toString());
		try {
			ResultSet resultSet;
			// String query = "SELECT * FROM utilizations WHERE teamId =
			// '"+utilization.getTeamId()+"' AND channelId =
			// '"+utilization.getChannelId()+"' ";
			// System.out.println(query);
			// ps = conn.prepareStatement(query);

			ps = conn.prepareStatement("SELECT * FROM utilizations WHERE teamId = ? AND channelId = ?");
			ps.setString(1, utilization.getTeamId());
			ps.setString(2, utilization.getChannelId());
			resultSet = ps.executeQuery();

			// sposto alla fine il puntatore
			resultSet.last();
			lenght = resultSet.getRow();

			//System.out.println("Lunghezza di lettura t_ID e c_ID: " + lenght);

		} catch (SQLException e) {
			System.err.println("ERRORE NEL DB (LETTURA)");
			e.printStackTrace();
		}

		if (lenght == 1)
			return true;
		else
			return false;
	}

	/**
	 * Search into the result set given by Read()
	 * 
	 * @param utilization
	 * @param MODE
	 *            = 1 if utilization is completed, MODE = 2 if utilization
	 *            contains only t_Id and c_Id
	 * @return
	 */
	public boolean searchUtilization(Utilization utilization) {
		ArrayList<Utilization> uList = null;
		boolean found = false;
//		if (MODE == 1) {
//			uList = this.Read();
//			found = uList.contains(utilization);
//		}
//
//		else if (MODE == 2)
			found = this.Read(utilization);

		return found;
	}

	@Override
	public Utilization Read2(Utilization utilization) {
		Utilization uRead = null;
		try {
			ResultSet resultSet;
			ps = conn.prepareStatement("SELECT * FROM utilizations WHERE teamId = ? AND channelId = ?");
			ps.setString(1, utilization.getTeamId());
			ps.setString(2, utilization.getChannelId());
			resultSet = ps.executeQuery();

			resultSet.next();
			uRead = new Utilization(resultSet.getString(2), resultSet.getString(3), resultSet.getString(4),
					resultSet.getString(5), resultSet.getString(6), resultSet.getString(7));

		} catch (SQLException e) {
			System.err.println("ERRORE NEL DB (LETTURA)");
		}
		return uRead;
	}

}