package proofs;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * HTTP GET and POST example
 * 
 * @author iampayload
 *
 */
public class TestInOut {

	
	// GET action url, query string appended to the url as ?stype=models
	//private final String urlGET = "https://www.servicesplus.sel.sony.com/PartsPLUSResults.aspx?stype=models";

	// POST action url
	
	//private final String urlPOST = "https://api.travis-ci.org/repo/Gravy94%2FFirstApplet/requests";
	private final String urlPOST = "https://hooks.slack.com/services/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";

	// post data or a payload
	private String postDataBody = "{\"text\": \"Figlio di giuda\"}";
	//private String postDataBody = "{\"request\": {\"message\": \"Hi there\"}}";
	
	

	// main class
	public static void main(String[] args) throws Exception {

		TestInOut http = new TestInOut();

		System.out.println("Testing Send Http POST request    HTML output below \n");
		http.sendPost();
	}

	// HTTP POST request
	private void sendPost() throws Exception {

		// POST example url
		URL obj = new URL(urlPOST);

		// Send post request
		HttpURLConnection con = (HttpURLConnection) obj.openConnection();

		// basic reuqest header to simulate a browser request
		con.setRequestMethod("POST");
		//con.setRequestProperty("Accept", "application/json");
		con.setRequestProperty("Content-Type", "application/json");
		//con.setRequestProperty("Travis-API-Version", "3");
		//con.setRequestProperty("Authorization", "token rHqPvxSB_fNpIO8IQIBICA");
		con.setRequestProperty("text", "ciaoscemo");

		// payload
		con.setUseCaches(false);
		con.setDoInput(true);
		con.setDoOutput(true);
		DataOutputStream wr = new DataOutputStream(con.getOutputStream());

		// POST data added to the request as a part of body
	
		wr.writeBytes(postDataBody);
		wr.flush();
		wr.close();

		// reading the HTML output of the POST HTTP request
		int responseCode = con.getResponseCode();
		System.out.println("Response Code: "+responseCode);
		BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
		String inputLine;
		while ((inputLine = in.readLine()) != null)
			System.out.println(inputLine);
		in.close();
	}
}