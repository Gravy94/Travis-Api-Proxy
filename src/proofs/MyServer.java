package proofs;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;

/**
 * HTTP GET and POST example
 * 
 * @author iampayload
 *
 */
public class MyServer {

	// POST action url

	// private String urlPOST =
	// "https://api.travis-ci.org/repo/Gravy94%2FFirstApplet/requests";
	// private String urlPOST =
	// "https://hooks.slack.com/services/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";
	private String urlPOST;
	private String urlGET;

	// post data or a payload
	// private String postDataBody = "{\"text\": \"message\"}"; // Messaggio
	// semplice
	// private String postDataBody = "{\"request\": {\"message\": \"Hi
	// there\"}}"; // Messaggio per build
	private String postDataBody;

	// main class
	public static void main(String[] args) throws Exception {

		MyServer http = new MyServer();

		System.out.println("Testing Send Http POST request    HTML output below \n");
		http.sendBuildCommandTravis();
		http.sendResponseMessageSlack();
	}

	/**
	 * @throws Exception
	 */
	private void sendBuildCommandTravis() {

		// Parametrizzare il token, slugid|repo ed eventualmente il messaggio
		// ritornare eventuali errori

		urlPOST = "https://api.travis-ci.org/repo/Gravy94%2FFirstApplet/requests"; /* Personalizzato */
		postDataBody = "{\"request\": {\"message\": \"Hi there3\"}}"; /* Personalizabile */

		URL obj = null;
		try {
			obj = new URL(urlPOST);
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		// Send post request
		HttpURLConnection con = null;
		try {
			con = (HttpURLConnection) obj.openConnection();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		// basic reuqest header to simulate a browser request
		try {
			con.setRequestMethod("POST");
			// con.setRequestProperty("User-Agent", "MyClient/1.0.0");
			con.setRequestProperty("Accept", "application/json");
			con.setRequestProperty("Content-Type", "application/json");
			con.setRequestProperty("Travis-API-Version", "3");
			con.setRequestProperty("Authorization", "token rHqPvxSB_fNpIO8IQIBICA"); /* Personalizzato */
		} catch (ProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		// payload
		con.setUseCaches(false);
		con.setDoInput(true);
		con.setDoOutput(true);
		// Send POST request
		DataOutputStream wr = null;
		DataInputStream re = null;
		int code = 0;
		try {
			code = con.getResponseCode();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		if ((code == 200) | (code == 202)) {

			try {
				// re.close();
				wr = new DataOutputStream(con.getOutputStream());
				re = new DataInputStream(con.getInputStream());
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			/*
			 * SONO UGUALI 
			 * DataInputStream re = new DataInputStream(con.getInputStream()); 
			 * BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
			 */

			// POST data added to the request as a part of body
			try {
				wr.writeBytes(postDataBody);
				wr.flush();
				wr.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			String buffer;

			// Getting Headers and Parameters
			// while ((buffer = re.readLine()) != null) {
			// System.out.println("zam"+buffer);
			// if (buffer.isEmpty()) {
			// break;
			// }
			// }

			// reading the HTML output of the POST HTTP request
			int responseCode = 0;
			try {
				responseCode = con.getResponseCode();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			System.out.println("MIO CODICE RISPOSTA: " + responseCode);

			BufferedReader in = null;
			try {
				in = new BufferedReader(new InputStreamReader(con.getInputStream()));
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			String inputLine;

			// RECEIVING http response Body json from Travis
			try {
				while ((inputLine = in.readLine()) != null)
					System.out.println(inputLine);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			// Receiving Response Code (probably 202)
			try {
				while ((inputLine = in.readLine()) != null)
					System.out.println(inputLine);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			try {
				in.close();
				re.close();
				wr.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} else {
			try {
				String inputLine;
//				IMPORTANTE PER SCOVARE GLI ERRORI
				BufferedReader in = new BufferedReader(new InputStreamReader(con.getErrorStream()));
				while ((inputLine = in.readLine()) != null)
					System.out.println(inputLine);
				System.out.println("Response code Travis: "+con.getResponseCode());
				
				//re.close();
				in.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

	}

	private void sendResponseMessageSlack() throws Exception {

		urlPOST = "https://hooks.slack.com/services/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"; /* Personalizzabile */
		postDataBody = "{\"text\": \"Messaggio a Slack\"}"; /* Personalizzabile */
		URL obj = new URL(urlPOST);

		// Send post request
		HttpURLConnection con = (HttpURLConnection) obj.openConnection();

		// basic reuqest header to simulate a browser request
		con.setRequestMethod("POST");
		con.setRequestProperty("Content-Type", "application/json");
		// con.setRequestProperty("text", "Questo è un messagio di risposta");

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
		System.out.println("Response Code Slack: " + responseCode);
		BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
		String inputLine;
		while ((inputLine = in.readLine()) != null)
			System.out.println(inputLine);
		in.close();

	}

	// HTTP POST request
	/*
	 * private void sendPost() throws Exception {
	 * 
	 * // POST example url URL obj = new URL(urlPOST);
	 * 
	 * // Send post request HttpURLConnection con = (HttpURLConnection)
	 * obj.openConnection();
	 * 
	 * // basic reuqest header to simulate a browser request
	 * con.setRequestMethod("POST"); //con.setRequestProperty("Accept",
	 * "application/json"); con.setRequestProperty("Content-Type",
	 * "application/json"); //con.setRequestProperty("Travis-API-Version", "3");
	 * //con.setRequestProperty("Authorization", "token rHqPvxSB_fNpIO8IQIBICA"
	 * ); //con.setRequestProperty("text", "ciaoscemo");
	 * 
	 * // payload con.setUseCaches(false); con.setDoInput(true);
	 * con.setDoOutput(true); DataOutputStream wr = new
	 * DataOutputStream(con.getOutputStream());
	 * 
	 * // POST data added to the request as a part of body
	 * 
	 * wr.writeBytes(postDataBody); wr.flush(); wr.close();
	 * 
	 * // reading the HTML output of the POST HTTP request int responseCode =
	 * con.getResponseCode(); System.out.println("Response Code: "
	 * +responseCode); BufferedReader in = new BufferedReader(new
	 * InputStreamReader(con.getInputStream())); String inputLine; while
	 * ((inputLine = in.readLine()) != null) System.out.println(inputLine);
	 * in.close(); }
	 */
}