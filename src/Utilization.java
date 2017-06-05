import java.io.Serializable;

/**
 * Dati letti da Input /Command
 * 
 * @author Michele
 *
 */
public class Utilization implements Serializable, Comparable {
	private String team_id;
	private String team_name;
	private String channel_id;
	private String channel_name;
	private String slug_repo;
	private String incoming_link;

	Utilization() {}
	
	Utilization(Json json, String sl, String il){
		this.team_id = json.get("team_id");
		this.team_name = json.get("team_domain");
		this.channel_id = json.get("channel_id");
		this.channel_name = json.get("channel_name");
		this.slug_repo = sl;
		this.incoming_link = il;/*CORREGGI*/
		
		
	}

	public String getTeamId() {
		return this.team_id;
	}

	public String getTeamName() {
		return this.team_name;
	}

	public String getChannelID() {
		return this.channel_id;
	}

	public String getChannelName() {
		return this.channel_name;
	}

	public String getSlugRepo() {
		return this.slug_repo;
	}

	public String getIncomingLink() {
		return this.incoming_link;
	}
	
	// va fatta in RepoAssocIO
	public String getIncomingLink(String ChannelId){
		return this.incoming_link;
	}

	@Override
	/**
	 * Confronta solamente Token_id e Channel_id perché lo stesso team può avere
	 * un singolo progetto per channel. Due progetti non possono esistere nello
	 * stesso channel.
	 */
	public int compareTo(Object obj) {
		if (this.getTeamId().equals(((Utilization) obj).getTeamId())) {
			if (this.getChannelID().equals(((Utilization) obj).getChannelID())) {
				return 0;// TROVATO
			}
		}
		return 1;// NON TROVATO
	}

	@Override
	public String toString() {

		return "[" + this.getTeamId() + ", " + this.getTeamName() + ", " + this.getChannelID() + ", "
				+ this.getChannelName() + ", " + this.getSlugRepo() + ", " + this.getIncomingLink() + "]";
	}

}
