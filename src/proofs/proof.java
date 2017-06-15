package proofs;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

public class proof implements Serializable{
	
	proof(String p, String url, String u, String pass){
		this.port = p;
		this.url_db = url;
		this.user_db = u;
		this.password_db = pass;
	}
	
	public String port;
	public String url_db;
	public String user_db;
	public String password_db;
	
	
	void WriteData(){
		
		try {
			ObjectOutputStream o = new ObjectOutputStream(new FileOutputStream("file.txt"));
			o.writeObject(this);
			o.close();
			o.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	void ReadData(){
		try {
			FileInputStream f = new FileInputStream("file.txt");
			ObjectInputStream input = new ObjectInputStream(f);
			proof p;
			
			
			while (f.available() > 0 ) {
				p = (proof) input.readObject();
				System.out.println(p.getPort() + ", "+p.getUrl()+", "+ p.getUser()+ ", "+ p.getPassword());
			}

		} catch (IOException | ClassNotFoundException e) {
			e.printStackTrace();
		}
	}
	
	
	String getPort(){
		return this.port;
	}
	
	String getUrl(){
		return this.url_db;
	}
	
	String getUser(){
		return this.user_db;
	}
	
	String getPassword(){
		return this.password_db;
	}
	
	public static void main(String args[]) {
		System.out.println("start");
		
		proof p = new proof("50500", "jdbc:mysql://localhost:3306/travis-proxy", "root", "root");
		
		p.WriteData();
		
		
		
		
		


	}

}
