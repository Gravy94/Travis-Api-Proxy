package proxy;
import java.io.Serializable;

/**
 * Dati letti da Input /Command
 * 
 * @author Michele
 *
 */
public class Utilization implements Serializable, Comparable {

	private static final long serialVersionUID = 1L;
	
	private String teamId;
	private String teamName;
	private String channelId;
	private String channelName;
	private String slugRepo;
	private String incomingWebHook;

	public Utilization() {}
	
	public Utilization(Json json){
		this.teamId = json.get("team_id");
		this.channelId = json.get("channel_id");
	}
	
	public Utilization(String tId, String tN, String cId, String cN, String sl, String iwh) {
		this.teamId = tId;
		this.teamName = tN;
		this.channelId = cId;
		this.channelName = cN;
		this.slugRepo = sl;
		this.incomingWebHook = iwh;	
	}
	
	public Utilization(Json json, String sl, String iwh){
		this.teamId = json.get("team_id");
		this.teamName = json.get("team_domain");
		this.channelId = json.get("channel_id");
		this.channelName = json.get("channel_name");
		this.slugRepo = sl;
		this.incomingWebHook = iwh;		
	}

	public String getTeamId() {
		return this.teamId;
	}

	public String getTeamName() {
		return this.teamName;
	}

	public String getChannelId() {
		return this.channelId;
	}

	public String getChannelName() {
		return this.channelName;
	}

	public String getSlugRepo() {
		return this.slugRepo;
	}

	public String getIncomingWebHook() {
		return this.incomingWebHook;
	}
	
	@Override
	/**
	 * Confronta solamente Token_id e channelId perché lo stesso team può avere
	 * un singolo progetto per channel. Due progetti non possono esistere nello
	 * stesso channel.
	 */
	public int compareTo(Object obj) {
		if (this.getTeamId().equals(((Utilization) obj).getTeamId())) {
			if (this.getChannelId().equals(((Utilization) obj).getChannelId())) {
				return 0;// are equals
			}
		}
		return 1;// aren't equals
	}

	@Override
	public String toString() {

		return "[" + this.getTeamId() + ", " + this.getTeamName() + ", " + this.getChannelId() + ", "
				+ this.getChannelName() + ", " + this.getSlugRepo() + ", " + this.getIncomingWebHook() + "]";
	}

}
