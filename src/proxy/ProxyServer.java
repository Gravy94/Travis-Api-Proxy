package proxy;

import java.awt.Toolkit;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URL;
import java.sql.Connection;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.concurrent.TimeUnit;

import DAO.UtilitazionDAOImpl;
import SQLConnection.ConnectionDB;

public class ProxyServer {

	private static int PORT; // Static port to communicate with server
	// private HashMap<String, String> jsonBody; // Contains the json Body
	// received
	// from Slack
	private String urlPOST; // url to send POST requests
	private String urlGET; // url to send GET requests
	private String slugRepo;
	private String incomingWebHook;

	private String postDataBody; // json body data to send POST requests

	private ServerSocket server;
	private Socket client;
	private Json json;

	private UtilitazionDAOImpl uDaoImpl;

	private Utilization tempUtilization;
	private Connection conn;

	private static boolean FIRSTTIME = true;
	private ConfigFile cf;

	ProxyServer() throws InterruptedException {
		if (ProxyServer.FIRSTTIME) {
			// cf = new ConfigFile("ConfigFile.txt"); // With JarFile
			cf = new ConfigFile("src/files/ConfigFile.txt"); // With Eclipse
			ProxyServer.PORT = cf.getProxyPort();
			new LoadDb(cf.getUrlDb(), cf.getDb(), cf.getUserDb(), cf.getPasswordDb());

			ProxyServer.FIRSTTIME = false;
		}
		/* LETTURA DATI DAL FILE */
		conn = ConnectionDB.getInstance(cf.getUrlDb() + cf.getDb(), cf.getUserDb(), cf.getPasswordDb());
		json = new Json();
		uDaoImpl = new UtilitazionDAOImpl();
	}

	public static void main(String args[]) throws Exception {

		ProxyServer myServer = new ProxyServer();
		int number_req = 0;
		boolean utilizationFound;

		if (!myServer.conn.isClosed()) {
			System.out.println("Listening for connection on port " + ProxyServer.PORT + " ....");
			myServer.server = new ServerSocket(ProxyServer.PORT);

			while (true) {

				myServer.client = myServer.server.accept(); // Accepting

				// Attending slash command from Slack
				myServer.receivePOSTMessageSlack();

				/*
				 * Controllare che la stringa ricevuta in text sia della forma:
				 * registration slug repo_name
				 */

				// the token of custom Slack App
				if (myServer.json.get("token").equals("fVcBmBIxZ0aDwS7t3CuwLDQZ")) {

					// temporary utilization (team_id and channel_id only)
					myServer.tempUtilization = new Utilization(myServer.json);

					if (myServer.uDaoImpl.Read(myServer.tempUtilization)) {
						utilizationFound = true;
						// System.out.println("UTENTE TROVATO");
						// load User data
						myServer.tempUtilization = myServer.uDaoImpl.Read2(myServer.tempUtilization);
					} else {
						utilizationFound = false;
						// System.out.println("UTENTE NON TROVATO, POSSO
						// REGISTRARE");
					}

					String stemp = myServer.json.get("text").toLowerCase();
					// doesn't start registration function if required help
					// registration

					if ((stemp.contains("registration")) & !(stemp.contains("help"))) {
						/* REGISTRATION */
						System.out.println("RECEIVED: " + myServer.json.get("text"));

						if (utilizationFound == false) {

							if (myServer.checkRegistrationParameters()) {

								// link creati in this.repoSlug e
								// this.incomingWebHook
								myServer.createLinks(myServer.json.get("text"));
								// verify if the https://travis-ci.org/slugRepo
								// exists
								if (myServer.verifyRepoLink()) {
									if (myServer.saveRegistration()) {
										myServer.sendResponseMessageSlack(myServer.incomingWebHook,
												"Registration succesfull! :+1:");
									} else {
										/*
										 * GESTIRE POSSIBILI ERRORI DI NON
										 * REGISTRABILITA'
										 */
										myServer.sendResponseMessageSlack(myServer.incomingWebHook,
												"ERROR: registration error! :-1:");
									}
								} else {
									myServer.sendResponseMessageSlack(myServer.json.get("response_url"),
											"ERROR: repository link 'https://travis-ci.org/"
													+ myServer.slugRepo.replace("%2F", "/")
													+ "' does not exists! :-1:");
								}
							} else {
								myServer.sendResponseMessageSlack(myServer.json.get("response_url"),
										"ERROR: parameters are not correct! :-1:");
							}

						} else {
							// System.err.println("ATTENZIONE! Utente gia'
							// registrato!");
							myServer.sendResponseMessageSlack(myServer.tempUtilization.getIncomingWebHook(),
									"WARNING: This channel already has an active registration!");
						}

					} else {
						switch (myServer.json.get("text").toLowerCase()) {
						case "help+build": // send private message about build
											// help
							System.out.println("RICEVUTO help build");
							myServer.sendResponseMessageSlack(myServer.json.get("response_url"),
									"The command *```/travis build```* allows to send a Travis-Ci build signal that will start"
											+ " a build linked to your team-channel");
							break;

						case "help+registration": // send private message about
													// registration help
							System.out.println("RICEVUTO help registration");
							myServer.sendResponseMessageSlack(myServer.json.get("response_url"),
									"The command *```/travis registration <Slug> <Repo_name> <Incoming_webhook>```* allows to associate"
											+ " team-channel ID to your Travis Project, specifing: "
											+ "\n`<Slug>` the owner, "
											+ "\n`<Repo_name>` the project name (respecting upper case and lower case),"
											+ "\n`<Incoming_webhook>` the Incoming WebHook associated to your Team");
							break;

						case "help": // send private message about general help
							System.out.println("RICEVUTO help");
							myServer.sendResponseMessageSlack(myServer.json.get("response_url"),
									"*Available commands are*:\n"
											+ "-Insert new registration *```/travis registration <Slug> <Repo> <incoming link>```*"
											+ "-Start new build *```/travis build```* "
											+ "-See help *```/travis help```* "
											+ "-See help about particular command *```/travis help <command_name>```*");

							break;
						case "build": // send public message about build result
							System.out.println("RICEVUTO BUILD");
							if (utilizationFound == false) {
								System.err.println("Comando non consentito: utente non registrato!");
								myServer.sendResponseMessageSlack(myServer.json.get("response_url"),
										"ERROR: Access denied! You should register a Travis-Ci repository to start a build!");
							} else {
								myServer.sendBuildCommandTravis(myServer.json.get("user_name"));
								myServer.sendResponseMessageSlack(myServer.tempUtilization.getIncomingWebHook(),
										"Build started... :+1:");
							}
							break;

						default:
							System.out.println("Error: Malformed request sent");
							myServer.sendResponseMessageSlack(myServer.json.get("response_url"),
									"`ERROR: Malformed request sent`");
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
							"ERROR: Access denied!!\nBefore Install 'Travis-api-command' on Slack :-1:");
				}

				// print number of request, date and time
				Toolkit.getDefaultToolkit().beep();
				SimpleDateFormat formatTime = new SimpleDateFormat("E dd.MM.yyyy 'at' hh:mm:ss a");
				Date date = new Date(System.currentTimeMillis());
				System.out.println("Number of requests: " + ++number_req + " \n\t in " + formatTime.format(date));

				myServer.client.close();
			}
		} else {

			System.err.println("Connessione col DB non instaurata!");
		}

	}

	private void sendResponseMessageSlack(String urlPOST, String message) throws Exception {

		// System.out.println("/** INIZIO sendResponseMessageSlack**/");
		String postDataBody = "{\"text\": \"" + message + "\"}"; /* Personalizzabile */
		URL obj = new URL(urlPOST);
		// URL obj = new
		// URL("https://hooks.slack.com/services/T3P12PZCM/B5F4BQ2EM/I8QNBYEzebYqt81bSGPguZr");

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
		BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
		if ((responseCode == 200) | (responseCode == 202)) {
			System.out.println("Response Code: " + responseCode);
			String inputLine;
			while ((inputLine = in.readLine()) != null)
				System.out.println("Messaggio ricevuto : " + inputLine);
		}

		in.close();

		// System.out.println("/** FINE sendResponseMessageSlack**/");

	}

	private void receivePOSTMessageSlack() throws Exception {
		// System.out.println("/** INIZIO receivePOSTMessageSlack**/");
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
		// HashMap<String, String> jsonBody;

		jsonTemp.jsonParserSlack(buffer);
		// jsonBody = jsonTemp.jsonParserSlack(buffer);
		// System.out.println(jsonBody);
		// System.out.println("INVIO RISPOSTA");

		input.close();
		reader.close();
		// this.client.close();
		// this.server.close();

		// System.out.println("/** FINE receivePOSTMessageSlack**/");
		this.json = jsonTemp;

	}

	private void sendBuildCommandTravis(String user) throws Exception {

		// System.out.println("/** INIZIO sendBuildCommandTravis**/");

		// Parametrizzare il token, slugid|repo ed eventualmente il messaggio
		// ritornare eventuali errori

		// urlPOST =
		// "https://api.travis-ci.org/repo/Gravy94%2FFirstApplet/requests"; /*
		// Personalizzato */
		urlPOST = "https://api.travis-ci.org/repo/" + this.tempUtilization.getSlugRepo() + "/requests";

		// System.err.println(urlPOST);
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

		// reading the HTML output of the POST HTTP request
		int responseCode = con.getResponseCode();
		BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
		if ((responseCode == 200) | (responseCode == 202)) {
			System.out.println("Response Code: " + responseCode);
			String inputLine;
			// Receiving http response Body json from Travis
			// and
			// Receiving Response Code (probably 202)
			while ((inputLine = in.readLine()) != null)
				System.out.println(inputLine);
		}

		in.close();
		// System.out.println("/** FINE sendBuildCommandTravis**/");
	}

	/**
	 * Function checking 3 parameters
	 * 
	 * @param text
	 *            string returned in slack json body response
	 * @return true if there are 3 parameters including incoming link
	 */
	public boolean checkRegistrationParameters() {
		String checker = this.json.get("text");
		String beginningIncomingWebHook = "https://hooks.slack.com/services/";
		int lenghtBeginningIncomingWebHook = beginningIncomingWebHook.length();
		int[] plusDelimiters = new int[3];

		int i = 0, c = 0;
		// check if number of parameters is 3
		while (i < checker.length() & i != -1) {
			i = checker.indexOf('+', i);
			if (i != -1) {
				// System.out.println(this.s.charAt(i) + " at " + i + "
				// position");
				plusDelimiters[c] = i;
				i++;
				c++;
			}
		}
		// are not 3 parameters
		if (c != 3) {
			System.err.println("Error: numbers of parameters not equals to 3!");
			return false;
		} else {
			// are 3 parameters
			if (checker.substring(0, 13).equals("registration+")) {
				// System.out.println(checker.subSequence(plusDelimiters[0] + 1,
				// plusDelimiters[1]));
				// System.out.println(checker.subSequence(plusDelimiters[1] + 1,
				// plusDelimiters[2]));
				// System.out.println(checker.subSequence(plusDelimiters[2] + 1,
				// plusDelimiters[2] + lenghtBeginningIncomingWebHook + 1));
				if (checker.subSequence(plusDelimiters[0] + 1, plusDelimiters[1]).length() > 0) {
					if (checker.subSequence(plusDelimiters[1] + 1, plusDelimiters[2]).length() > 0) {
						if (checker
								.subSequence(plusDelimiters[2] + 1,
										plusDelimiters[2] + lenghtBeginningIncomingWebHook + 1)
								.equals(beginningIncomingWebHook)) {

							return true;
						} else {
							System.err.println("Error: incoming WebHook not correct!");
							return false;
						}
					} else {
						System.err.println("Error: Repo name is null!");
						return false;
					}
				} else {
					System.err.println("Error: Slug value is null!");
					return false;
				}
			} else {
				System.err.println("Error: malformed query registration");
				return false;
			}
		}
	}

	/**
	 * Function creating repo_link and incoming_link from
	 * <slug> <repo> <incomingWebHook> string
	 * 
	 * @param text
	 *            string returned in slack json body response
	 * @throws Exception
	 */
	private void createLinks(String text) {
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
		this.incomingWebHook = temp_incoming_link;
	}

	/**
	 * Verifies if extracted link exists and works
	 * 
	 * @param rl
	 * @return
	 * @throws IOException
	 */
	private boolean verifyRepoLink() {
		// System.err.println("IN verifyRepoLink");
		String temp_rl = this.slugRepo;
		temp_rl = temp_rl.replace("%2F", "/");
		this.urlGET = "https://api.travis-ci.org/repositories/" + temp_rl + ".json";

		// System.out.println("Stringa a cui fare richiesta get: \n" +
		// this.urlGET);
		URL obj = null;
		try {
			obj = new URL(this.urlGET);
		} catch (MalformedURLException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		// Send get request
		HttpURLConnection con = null;
		try {
			con = (HttpURLConnection) obj.openConnection();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		// basic reuqest header to simulate a browser request
		try {
			con.setRequestMethod("GET");
		} catch (ProtocolException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		con.setDoOutput(true);

		// reading the HTML output of the POST HTTP request

		int responseCode = 0;
		try {
			responseCode = con.getResponseCode();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
			return false;
		}
		try {
			BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
			String inputLine;
			while ((inputLine = in.readLine()) != null)
				System.out.println(inputLine);
			in.close();

			if (responseCode == 200) {
				System.out.println("Link esistente: responseCode= " + responseCode);
				return true;
			} else {
				System.out.println("Link inesistente: responseCode= " + responseCode);
				return false;
			}
		} catch (FileNotFoundException e) {
			System.out.println("Error: problem with data sending in verifyRepoLink");
			return false;

		} catch (IOException e) {
			e.printStackTrace();
			System.out.println("Error: problem with data sending in verifyRepoLink");
			return false;
		}
	}

	/**
	 * Stores verified user into the db
	 * 
	 * @return
	 */
	public boolean saveRegistration() {
		return this.uDaoImpl.Insert(new Utilization(this.json, this.slugRepo, this.incomingWebHook));
	}

}