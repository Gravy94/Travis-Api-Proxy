package proofs;
import java.util.HashMap;

public class Json {

	private HashMap<String, String> jsonMap;
	//private static String buffer = "token=fVcBmBIxZ0aDwS7t3CuwLDQZ&team_id=T3P12PZCM&team_domain=myown-org&channel_id=C3PJTKXKR&channel_name=general&user_id=U3NBETCLR&user_name=michelelomb&command=%2Frunscope&text=a&response_url=https%3A%2F%2Fhooks.slack.com%2Fcommands%2FT3P12PZCM%2F186043543824%2FUQEr2hfqOsvJ1xzgKtP8Z9VF";

	Json() {
		jsonMap = new HashMap<String, String>();
	}

	public HashMap<String, String> jsonParserSlack(String buffer) {

		//HashMap<String, String>
		
		int length = buffer.length();
		int state = 0; // state = 0 : name
						// state = 1 : value
		String key = "", value = "";
		for (int i = 0; i < length; i++) {
			if (value.contains("%2F"))
				value = value.replace("%2F", "/");
			if (value.contains("%3A"))
				value = value.replace("%3A", ":");
//			if(value.contains("+"))
//				value = value.replace("+"," ");

			char c = buffer.charAt(i);
			switch (c) {
			case '&':
				state = 0;

				this.jsonMap.put(key, value);
				key = "";
				value = "";

				break;
			case '=':
				state = 1;
				break;
			default:
				if (state == 0)
					key = key + c;
				else if (state == 1)
					value = value + c;
				break;
			}
		}
		this.jsonMap.put(key, value);

		return this.jsonMap;
	}
	
	
	public String get(String key){
		return this.jsonMap.get(key);
	}
	
	public static void main(String args[]) {

		/*
		 * Json = new Json(); HashMap<String, String> j =
		 * Json.jsonParserSlack(buffer); System.out.println(j.toString());
		 * System.out.println("\n\nToken= "+j.get("token"));
		 */
	}

}
