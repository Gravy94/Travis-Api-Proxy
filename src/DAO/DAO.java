package DAO;

import proxy.Utilization;

/**
 * Classe interfaccia appartenente al Pattern Architetturale  DATA ACCESS OBJECT
 * @author Lombardi - Defino
 * @version $Revision: 1.0 $
 */
public interface DAO {
	
	/**
	 * Inserisce l'entità passata all'interno del DB
	 * @param utilization : Entità passata da cui leggere i dati da scrivere nel DB
	 * @return esotp dell'inserimento
	 * */
	public boolean Insert(Utilization utilization);
	/**
	 * Elimina l'entità passata all'interno del DB
	 * @param utilization : Entità passata da eliminare all'interno del DB 	
	 * @return boolean esito della cancellazione
	 * */
	public boolean Delete(Utilization utilization);

	/**
	 * 
	 */
	//public ArrayList<Utilization> Read();

	/**
	 * 
	 * @param utilization
	 * @return boolean esito della lettura
	 */
	public boolean Read(Utilization utilization);
	
	public Utilization Read2(Utilization utilization);
	
	
}
