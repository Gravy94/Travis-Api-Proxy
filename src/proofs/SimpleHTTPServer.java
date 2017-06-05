package proofs;
import java.awt.Toolkit;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URL;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.HashMap;


public class SimpleHTTPServer {

	private static int PORT = 50500;
	private HashMap<String, String> jsonBody;

	SimpleHTTPServer() {
		jsonBody = new HashMap<String, String>();
	}

	public static void main(String args[]) throws Exception {
		SimpleHTTPServer myServer = new SimpleHTTPServer();

		ServerSocket server;
		int number_req = 0;

		System.out.println("Listening for connection on port " + myServer.PORT + " ....");
		while (true) {
			server = new ServerSocket(PORT);
			final Socket client = server.accept(); // Accepting connection

			// Getting input request
			InputStreamReader input = new InputStreamReader(client.getInputStream());
			BufferedReader reader = new BufferedReader(input);

			// Send output request
			String buffer;
			// OutputStreamWriter output = new
			// OutputStreamWriter(client.getOutputStream());
			// BufferedWriter writer = new BufferedWriter(output);

			// Getting Headers and Parameters
			while ((buffer = reader.readLine()) != null) {
				System.out.println(buffer);
				if (buffer.isEmpty()) {
					break;
				}
			}
			
			// Getting Body (Format: string=string&string=string&...)
			buffer = reader.readLine();
			Json json = new Json();
			myServer.jsonBody = json.jsonParserSlack(buffer);
			
			
			
			System.out.println(myServer.jsonBody);
			System.out.println("INVIO RISPOSTA");
			
			
			BufferedWriter out = new BufferedWriter(new OutputStreamWriter(client.getOutputStream()));
			out.write("HTTP/1.0 200 OK");
			myServer.sendResponseMessageSlack(myServer.jsonBody.get("response_url"),"build started");
			
			
			/*
			 * while ((buffer = reader.readLine()) != null) {
			 * System.out.println(buffer); if (buffer.isEmpty()) { break; } }
			 */

			/*
			 * writer.write("HTTP/1.0 200 OK\r\n"); writer.write(
			 * "Date: Fri, 31 Dec 1999 23:59:59 GMT\r\n"); writer.write(
			 * "Server: Apache/0.8.4\r\n"); writer.write(
			 * "Content-Type: text/html\r\n"); writer.write(
			 * "Content-Length: 59\r\n"); writer.write(
			 * "Expires: Sat, 01 Jan 2000 00:59:59 GMT\r\n"); writer.write(
			 * "Last-modified: Fri, 09 Aug 1996 14:21:40 GMT\r\n");
			 * writer.write("\r\n"); writer.write("<TITLE>Exemple</TITLE>");
			 * writer.write("<P>Ceci est une page d'exemple.</P>");
			 */

			// print number of request, date and time
			Toolkit.getDefaultToolkit().beep();
			SimpleDateFormat formatTime = new SimpleDateFormat("E dd.MM.yyyy 'at' hh:mm:ss a");
			Date date = new Date(System.currentTimeMillis());
			System.out.println("Number of requests: " + ++number_req + " \n\t in " + formatTime.format(date));

			// 1. Read HTTP request from the client socket
			// 2. Prepare an HTTP response
			// 3. Send HTTP response to the client
			// 4. Close the socket
			// output.close();
			input.close();
			reader.close();
			client.close();
			server.close();

		}

	}

	
	private void sendResponseMessageSlack(String urlPOST, String message) throws Exception{
		
		// Incoming webhook of Travis-test1 slack App. Message visible to everyone
		//urlPOST="https://hooks.slack.com/services/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";
		String postDataBody = "{\"text\": \""+message+"\"}";			/*Personalizzabile*/
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
		System.out.println("Response Code: "+responseCode);
		BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
		String inputLine;
		while ((inputLine = in.readLine()) != null)
			System.out.println(inputLine);
		in.close();
		
	}
}
