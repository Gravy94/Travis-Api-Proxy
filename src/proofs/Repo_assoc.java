package proofs;
import java.io.Serializable;


/**
 * Dati letti dal file
 * @author Michele
 *
 */
public class Repo_assoc implements Serializable, Comparable{
	private String team_id;
	private String channel_id;
	private String slug_repo;
	
		
	Repo_assoc(String t, String c, String sr){
		this.team_id = t;
		this.channel_id = c;
		this.slug_repo = sr;
	}
		
	
	public String getTeamId(){
		return this.team_id;
	}
	
	public String getChannelID() {
		return this.channel_id;
	}

	public String getSlugRepo() {
		return this.slug_repo;
	}

	
	@Override
	/**
	 * Confronta solamente Token_id e Channel_id
	 * perché lo stesso team può avere un singolo progetto 
	 * per channel. 
	 * Due progetti non possono esistere nello stesso channel.
	 */
	public int compareTo(Object obj) {
		if( this.getTeamId().equals(( (Repo_assoc)obj).getTeamId() )){
			if(this.getChannelID().equals(( (Repo_assoc)obj).getChannelID() ) ){
				return 0;	// TROVATO
			}
		}
		return 1;	// NON TROVATO
	}
	
	
}
