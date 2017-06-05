import java.awt.Toolkit;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URL;
import java.sql.Date;
import java.sql.Time;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;

public class ProxyServer {

	private static int PORT = 50500; // Static port to communicate with server
	// private HashMap<String, String> jsonBody; // Contains the json Body
	// received
	// from Slack
	private String urlPOST; // url to send POST requests
	private String urlGET; // url to send GET requests
	private String slugRepo;
	private String incomingLink;

	private String postDataBody; // json body data to send POST requests

	private ServerSocket server;
	private Socket client;
	private Json json;

	private RepoAssocIO rp;

	ProxyServer() throws InterruptedException {
		// jsonBody = new HashMap<String, String>();

		json = new Json();
		rp = new RepoAssocIO();

	}

	public static void main(String args[]) throws Exception {
		ProxyServer myServer = new ProxyServer();

		int number_req = 0;

		System.out.println("Listening for connection on port " + ProxyServer.PORT + " ....");
		while (true) {
			myServer.server = new ServerSocket(PORT);
			myServer.client = myServer.server.accept(); // Accepting connection

			// Attending slash command from Slack
			myServer.receivePOSTMessageSlack();

			/*
			 * Controllare che la stringa ricevuta in text sia della forma:
			 * registration slug repo_name
			 */

			// the token of custom Slack App
			if (myServer.json.get("token").equals("fVcBmBIxZ0aDwS7t3CuwLDQZ")) {

				/* REGISTRATION */
				String stemp = myServer.json.get("text").toLowerCase();
				// doesn't start registration function if required help
				// registration
				if ((stemp.contains("registration")) & !(stemp.contains("help"))) {
					System.out.println("RECEIVED: " + myServer.json.get("text"));
					// link creati in repo_slug e incoming_link
					myServer.createLinks(myServer.json.get("text"));
					// verify if the https://travis-ci.org/slugRepo exists
					if (myServer.verifyRepoLink()) {
						if (myServer.saveRegistration()) {
							// TODO after
							// myServer.sendResponseMessageSlack(myServer.utilization.getIncomingLink,"Registrazione
							// avvenuta con successo");
							myServer.sendResponseMessageSlack(myServer.json.get("response_url"),
									"Registrazione avvenuta con successo");
						} else {
							/* GESTIRE POSSIBILI ERRORI DI NON REGISTRABILITA' */
							myServer.sendResponseMessageSlack(myServer.json.get("response_url"),
									"ATTENZIONE! Errore nella registrazione!");
						}
					}

				} else { // TODO AGGIUNGERE FUNZIONE DI RICERCA su RepoAssocIO coi dati T_ID e C_ID
						// TODO PERMETTERE ACCESSO SOLO AI REGISTRATI
					switch (myServer.json.get("text").toLowerCase()) {
					case "help+build":
						System.out.println("RICEVUTO help build");

						myServer.sendResponseMessageSlack(myServer.json.get("response_url"),
								"Il comando */travis build* permette di inviare un segnale a Travis-Ci il quale fara' partire"
										+ " una build sul progetto collegato nel channel del tuo team.");
						break;

					case "help+registration":
						System.out.println("RICEVUTO help registration");

						myServer.sendResponseMessageSlack(myServer.json.get("response_url"),
								"Il comando *```/travis registration <Slug> <Repo_name> <incoming_link>```* permette l'assegnazione"
										+ " della chiave team-channel ad un progetto Travis indicandone: "
										+ "\n`<Slug>` il proprietario, "
										+ "\n`<Repo_name>` il nome esatto del progetto (rispettando maiuscole e minuscole),"
										+ "\n`<incoming link>` il link di incoming appositamente creato per il personale channel Slack.");
						break;

					case "build": // inviare i risultati a tutto il gruppo nei
									// link
									// di incoming
						System.out.println("RICEVUTO BUILD");
						myServer.sendBuildCommandTravis(myServer.json.get("user_name"));
						myServer.sendResponseMessageSlack(myServer.json.get("response_url"), "Build started");
						break;

					case "help": // inviare i risultati al solo cliente
									// richiedente
									// (tramite link referred)
									// prevedere nel caso di non risposta di
									// inviarlo al link di incoming generale
						System.out.println("RICEVUTO help");

						/*
						 * myServer.sendResponseMessageSlack(myServer.json.get(
						 * "response_url"), "*I comandi eseguibili sono*:\n" +
						 * "-Inserire una nuova registrazione ```/travis registration <Slug> <Repo>``` "
						 * + "-Avviare una nuova build ```/travis build``` " +
						 * "-Chiedere aiuto sui comandi utilizzabili ```/travis help``` "
						 * +
						 * "-Chiedere aiuto su un comando in particolare ```/travis help <command_name>```"
						 * );
						 */
						myServer.sendResponseMessageSlack(
								"https://hooks.slack.com/services/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",
								"*I comandi eseguibili sono*:\n"
										+ "-Inserire una nuova registrazione ```/travis registration <Slug> <Repo> <incoming link>``` "
										+ "-Avviare una nuova build ```/travis build``` "
										+ "-Chiedere aiuto sui comandi utilizzabili ```/travis help``` "
										+ "-Chiedere aiuto su un comando in particolare ```/travis help <command_name>```");

						break;

					default:
						System.out.println("Errore Ricezione dati HTTP");
						Toolkit.getDefaultToolkit().beep();
						TimeUnit.MILLISECONDS.sleep(250);
						Toolkit.getDefaultToolkit().beep();
						TimeUnit.MILLISECONDS.sleep(250);
						Toolkit.getDefaultToolkit().beep();

						break;

					}
				}
			} else {
				// users not authorized
				myServer.sendResponseMessageSlack(myServer.json.get("response_url"),
						"Autorizzazione non concessa!\nInsallare l'applicazione Slack 'Travis API'");
			}

			// print number of request, date and time
			Toolkit.getDefaultToolkit().beep();
			SimpleDateFormat formatTime = new SimpleDateFormat("E dd.MM.yyyy 'at' hh:mm:ss a");
			Date date = new Date(System.currentTimeMillis());
			System.out.println("Number of requests: " + ++number_req + " \n\t in " + formatTime.format(date));

		}

	}

	private void sendResponseMessageSlack(String urlPOST, String message) throws Exception {

		System.out.println("/** INIZIO sendResponseMessageSlack**/");
		String postDataBody = "{\"text\": \"" + message + "\"}"; /* Personalizzabile */
		URL obj = new URL(urlPOST);

		// Send post request
		HttpURLConnection con = (HttpURLConnection) obj.openConnection();

		// basic reuqest header to simulate a browser request
		con.setRequestMethod("POST");
		con.setRequestProperty("Content-Type", "application/json");

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
		System.out.println("Response Code: " + responseCode);
		BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
		String inputLine;
		while ((inputLine = in.readLine()) != null)
			System.out.println("Messaggio ricevuto : " + inputLine);
		in.close();

		System.out.println("/** FINE sendResponseMessageSlack**/");

	}

	private void receivePOSTMessageSlack() throws Exception {
		System.out.println("/** INIZIO receivePOSTMessageSlack**/");
		// Getting input request
		InputStreamReader input = new InputStreamReader(this.client.getInputStream());
		BufferedReader reader = new BufferedReader(input);

		// Send output request
		String buffer;

		// Getting Headers and Parameters
		while ((buffer = reader.readLine()) != null) {
			System.out.println(buffer);
			if (buffer.isEmpty()) {
				break;
			}
		}

		// Getting Body (Format: string=string&string=string&...)
		buffer = reader.readLine();

		Json jsonTemp = new Json();
		HashMap<String, String> jsonBody;
		jsonBody = jsonTemp.jsonParserSlack(buffer);

		System.out.println(jsonBody);
		// System.out.println("INVIO RISPOSTA");

		/*
		 * BufferedWriter out = new BufferedWriter(new
		 * OutputStreamWriter(client.getOutputStream())); out.write(
		 * "HTTP/1.0 200 OK");
		 */

		// this.sendResponseMessageSlack(this.jsonBody.get("response_url"),
		// "build started");

		input.close();
		reader.close();
		this.client.close();
		this.server.close();

		System.out.println("/** FINE receivePOSTMessageSlack**/");
		this.json = jsonTemp;

	}

	private void sendBuildCommandTravis(String user) throws Exception {

		System.out.println("/** INIZIO sendBuildCommandTravis**/");
		// Parametrizzare il token, slugid|repo ed eventualmente il messaggio
		// ritornare eventuali errori

		urlPOST = "https://api.travis-ci.org/repo/Gravy94%2FFirstApplet/requests"; /* Personalizzato */
		postDataBody = "{\"request\": {\"message\": \"Build started from " + user + "\"}}"; /* Personalizabile */

		URL obj = new URL(urlPOST);

		// Send post request
		HttpURLConnection con = (HttpURLConnection) obj.openConnection();

		// basic reuqest header to simulate a browser request
		con.setRequestMethod("POST");
		// con.setRequestProperty("User-Agent", "MyClient/1.0.0");
		con.setRequestProperty("Accept", "application/json");
		con.setRequestProperty("Content-Type", "application/json");
		con.setRequestProperty("Travis-API-Version", "3");
		con.setRequestProperty("Authorization", "token rHqPvxSB_fNpIO8IQIBICA"); /* Personalizzato */

		// payload
		con.setUseCaches(false);
		con.setDoInput(true);
		con.setDoOutput(true);
		// Send POST request
		DataOutputStream wr = new DataOutputStream(con.getOutputStream());

		// DataInputStream re = new DataInputStream(con.getInputStream());

		// POST data added to the request as a part of body
		wr.writeBytes(postDataBody);
		wr.flush();
		wr.close();

		String buffer;

		// Getting Headers and Parameters
		// while ((buffer = re.readLine()) != null) {
		// System.out.println("zam"+buffer);
		// if (buffer.isEmpty()) {
		// break;
		// }
		// }

		// reading the HTML output of the POST HTTP request
		int responseCode = con.getResponseCode();

		BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
		String inputLine;
		// Receiving http response Body json from Travis
		while ((inputLine = in.readLine()) != null)
			System.out.println(inputLine);
		// Receiving Response Code (probably 202)
		while ((inputLine = in.readLine()) != null)
			System.out.println(inputLine);
		in.close();

		System.out.println("/** FINE sendBuildCommandTravis**/");

	}

	/**
	 * Function checking 3 parameters
	 * 
	 * @param text
	 *            string returned in slack json body response
	 * @return true if there are 3 parameters including incoming link
	 */
	private boolean ceckParameters(String text) {
		int i = 0, c = 0;
		while (i < text.length() & i != -1) {
			i = text.indexOf('+', i);
			if (i != -1) {
				// System.out.println(this.s.charAt(i) + " at " + i + "
				// position");
				i++;
				c++;
			}
		}
		// check numbers of parameters
		if (c != 3) {
			System.err.println("Error: numbers of parameters not equals to 3!");
			return false;
		} else {
			// check the http://
			if (text.contains("https://hooks.slack.com/services/"))
				return true;
			else {
				System.err.println("Error: incoming link not correct!");
				return false;
			}
		}
	}

	/**
	 * Function creating repo_link and incoming_link from
	 * <slug> <repo> <incominglink> string
	 * 
	 * @param text
	 *            string returned in slack json body response
	 * @throws Exception
	 */
	private void createLinks(String text) {
		// String text = this.json.get("text");
		String temp_Repo_Slug = new String();
		String temp_incoming_link = new String();
		int c = 0;
		int index = 13;
		int length = text.length();
		char c_temp;
		/* EXTRACT repo_link from message body json text */
		while (index < length & c < 2) {
			c_temp = text.charAt(index);
			if (c_temp != '+')
				temp_Repo_Slug += text.charAt(index);
			else if (c_temp == '+' & c < 1) {
				temp_Repo_Slug += "%2F";
				c++;
			} else
				c++;
			index++;
		}
		// System.out.println("\nLink creato: " + temp_Repo_Slug);
		this.slugRepo = temp_Repo_Slug;
		/* EXTRACT incoming_link from message body json text */
		while (index < text.length()) {
			temp_incoming_link += text.charAt(index);
			index++;
		}
		// System.out.println("\nLink creato: " + temp_incoming_link);
		this.incomingLink = temp_incoming_link;
	}

	/**
	 * Verifies if extracted link exists and works
	 * 
	 * @param rl
	 * @return
	 * @throws IOException
	 */
	private boolean verifyRepoLink() throws IOException {

		String temp_rl = this.slugRepo;
		temp_rl.replace("%2F", "/");
		this.urlGET = "https://travis-ci.org/" + temp_rl;

		URL obj = new URL(this.urlGET);
		// Send post request
		HttpURLConnection con = (HttpURLConnection) obj.openConnection();

		// basic reuqest header to simulate a browser request
		con.setRequestMethod("GET");
		con.setDoOutput(true);

		// reading the HTML output of the POST HTTP request
		int responseCode = con.getResponseCode();
		BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
		String inputLine;
		// while ((inputLine = in.readLine()) != null)
		// System.out.println(inputLine);
		in.close();

		if (responseCode == 200) {
			System.out.println("Link esistente: responseCode= " + responseCode);
			return true;
		} else {
			System.out.println("Link inesistente: responseCode= " + responseCode);
			return false;
		}
	}

	/**
	 * Stores verified user to the file/db
	 * 
	 * @return
	 */
	public boolean saveRegistration() {
		// return this.rp.addRegistration(
		// new Utilization(this.json.get("team_id"),
		// this.json.get("channel_id"), this.slug_Repo));
		return this.rp.addRegistration(new Utilization(this.json, this.slugRepo, this.incomingLink));
		// probably I've to return the Utilization instance or null if
		// registration doesn't go well
	}

	private void send200Ok() throws Exception {

		URL obj = new URL(this.incomingLink);
		// Send post request
		HttpURLConnection con = (HttpURLConnection) obj.openConnection();

		// basic reuqest header to simulate a browser request
		con.setRequestMethod("GET");
		con.setRequestProperty("Accept-Language", "en-US,en;q=0.5");
		con.setRequestProperty("Upgrade-Insecure-Requests", "1");
		con.setRequestProperty("Connection", "keep-alive");
		con.setRequestProperty("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8");
		con.setRequestProperty("Accept-Encoding", "gzip, deflate, br");
		con.setDoOutput(true);

		// reading the HTML output of the POST HTTP request
		int responseCode = con.getResponseCode();
		BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
		String inputLine;
		// while ((inputLine = in.readLine()) != null)
		// System.out.println(inputLine);
		in.close();
	}
}