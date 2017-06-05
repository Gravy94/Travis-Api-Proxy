import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.lang.reflect.Array;
import java.util.ArrayList;

import javax.rmi.CORBA.Util;

/**
 * Gestisce I/O sui dati dei gruppi registrati (per ora utilizza un Array,
 * implementare con file o DB)
 * 
 * @author Michele
 *
 */
public class RepoAssocIO implements Serializable {
	// private Repo_assoc ra; // Lettura file
	// private Utilization utilization; // Lettura /Command

	private static String FileName = "dati.txt";
	private File f;

	private ArrayList<Utilization> utilizationList;
	private Utilization utilization;

	public RepoAssocIO() {
		utilizationList = new ArrayList<Utilization>();
		utilization = new Utilization();

		f = new File(FileName);
		f.delete();
		try {
			if (!f.exists())
				f.createNewFile();
			else {
				System.out.println("FILE ESISTENTE!!");
				// try {
				// FileInputStream file = new FileInputStream(FileName);
				// ObjectInputStream input = new ObjectInputStream(file);
				// Utilization u_temp = new Utilization();
				//
				// int i = 1;
				// while (file.available() > 0) {
				// try {
				// u_temp = (Utilization) input.readObject();
				// //utilizationList.add((Utilization) input.readObject());
				// utilizationList.add(u_temp);
				// } catch (ClassNotFoundException e) {
				// // TODO Auto-generated catch block
				// e.printStackTrace();
				// }
				//
				// /*System.out.println(utilizationList.get(i).getTeamId()+
				// ", " + utilizationList.get(i).getChannelID()+
				// ", " + utilizationList.get(i).getSlugRepo());
				// */
				// i++;
				// }
				//
				// file.close();
				// input.close();
				// input.reset();
				// ((ObjectOutput) input).flush();
				// } catch (IOException e) {
				// e.printStackTrace();
				// }
			}

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		// utilization = new Utilization();

	}

	public static void main(String args[]) {
		
		/*
		
		Utilization u1 = new Utilization("hfdfsd", "7h48", "RepoAssoc1");
		Utilization u2 = new Utilization("sdds", "fgsfg", "RepoAssoc2");
		Utilization u3 = new Utilization("456yter", "nbnv", "RepoAssoc3");

		RepoAssocIO raio = new RepoAssocIO();
		raio.addRegistration(u1);
		raio.addRegistration(u2);
		raio.addRegistration(u3);
		raio.addRegistration(new Utilization("hfdfsd", "7h48", "RepoAssoc4"));

		raio.printRegistrations2();

		raio.addRegistration(new Utilization("456yter", "nbnv", "RepoAssoc5"));
	
		// Utilization u = new Utilization("456yteàr", "nbnv", "RepoAssoc3");
		// System.out.println(u.searchUtilization(u));
		//
		// if (rw3.addRegistration(u))
		// System.out.println("INSERITO !!!!");
		// else
		// System.out.println("NON INSERITO !!!!");
		 * 
		 
		 */

	}

	/**
	 * Due registrazioni sono uguali se hanno in comune le due chiavi
	 * (team-channel)
	 * 
	 * @param u
	 * @return true se esiste già la coppia (team_id-channel_id) false
	 *         altrimenti
	 */
	// public boolean searchUtilization(Utilization temp) {
	// boolean found = false;
	// try {
	// FileInputStream f = new FileInputStream(FileName);
	// ObjectInputStream input = new ObjectInputStream(f);
	// String s;
	// s = Byte.toString(input.readByte());
	// System.out.println("BYTE: "+s);
	// while ((f.available() > 0) & (found == false)) {
	// this.utilization = (Utilization) input.readObject();
	// if (this.utilization.compareTo(temp) == 0) {
	// found = true;
	// }
	// }
	// f.close();
	// input.close();
	// //input.reset();
	// ((ObjectOutput) input).flush();
	//
	// } catch (IOException | ClassNotFoundException e) {
	// e.printStackTrace();
	// }
	// return found;
	// }
	//
	//
	// public void printRegistrations() {
	// // LETTURA GENERALE (fino alla fine del file)
	// try {
	// FileInputStream f = new FileInputStream(FileName);
	// ObjectInputStream input = new ObjectInputStream(f);
	// // Utilization u4;
	// int i = 0;
	// while (f.available() > 0) {
	// // while (i < 3) {
	// // System.out.println(f.available());
	// this.utilization = (Utilization) input.readObject();
	// System.out.println(this.utilization.getTeamId() + ", " +
	// this.utilization.getChannelID() + ", "
	// + this.utilization .getSlugRepo() + " available:" + f.available());
	//
	// i++;
	// }
	// f.close();
	// input.close();
	// //input.reset();
	// ((ObjectOutput) input).flush();
	// } catch (IOException | ClassNotFoundException e) {
	// e.printStackTrace();
	// }
	// }

	public boolean addRegistration(Utilization temp) {
		if (this.searchUtilization2(temp) == false) { // TENERE D'OCCHIO QUESTA
			// SCRITTURA FILE
			try {
				FileOutputStream f = new FileOutputStream(FileName, true);
				// opens file in append
				ObjectOutputStream output = new ObjectOutputStream(f);
				// AppendingObjectOutputStream output = new
				// AppendingObjectOutputStream();
				output.writeObject(temp);

				this.utilizationList.add(temp);
				System.out.println("NUOVO INSERIMENTO! "
						+ "Il team " + temp.getTeamName() 
						+ " nel channel " + temp.getChannelName()
						+ " ha aggiunto una repository con nome " + temp.getSlugRepo());

				// output.reset();
				// output.close();
				// output.flush();
				f.close();

			} catch (IOException e) {
				e.printStackTrace();
			}
			
			this.printRegistrations2();
			return true;
		} else {
			System.out.println("NON INSERITO! "
					+ "Il team " + temp.getTeamName() 
					+ " nel channel " + temp.getChannelName()
					+ " ha già aggiunto una repository con nome " + this.getRepoLink());
			return false;
		}

	}

	public void printRegistrations2() {
		System.out.println(this.utilizationList.toString());
	}

	public boolean searchUtilization2(Utilization temp) {
		boolean found = false;
		/* Farlo con file */
		for (int i = 0; i < this.utilizationList.size(); i++) {
			if (this.utilizationList.get(i).compareTo(temp) == 0) {
				this.utilization = this.utilizationList.get(i);
				found = true;
				i = this.utilizationList.size() + 1;
			}
		}

		return found;
	}

	// non va bene questa. farla su utilizationList
	public String getRepoLink() {
		return this.utilization.getSlugRepo();
	}

}
