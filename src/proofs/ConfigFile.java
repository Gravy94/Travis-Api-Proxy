package proofs;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;


public class ConfigFile {
	private HashMap<String,String> hm;
	private String fileName;
	private Path path;
	
	ConfigFile(String file){
		File f = new File(file);
		if(!f.exists()){
			try {
				this.path = f.toPath();
				Files.createFile(this.path);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				System.out.println("Error: on Files.createFile();");
				e.printStackTrace();
			}
			
			System.err.println("Error: configuration files is empty!");
		}
		else{
			hm = this.getConfigData();
			System.out.println("Initializing server on port: "+hm.get("proxy_port"));
			System.out.println("Connecting to DBMS: "+hm.get("url_db"));
			System.out.println("With Users: "+hm.get("user_db"));
			System.out.println("and Password: "+hm.get("password_db"));
		}
	}
	
	
	
	public static void main(String args[]) throws IOException{
		ConfigFile cf = new ConfigFile("ConfigFile.txt");
		HashMap<String,String> data  = cf.getConfigData();
				
	}
	
	public HashMap<String,String> getConfigData(){
		//List used as comparator
		ArrayList<String> listParam = new ArrayList<String>();
		listParam.add("proxy_port");
		listParam.add("url_db");
		listParam.add("user_db");
		listParam.add("password_db");
		
		//HashMap to return completed
		HashMap <String,String> tempHm = new HashMap<String,String>();
		tempHm.put("proxy_port", "null");
		tempHm.put("url_db", "null");
		tempHm.put("user_db", "null");
		tempHm.put("password_db", "null");
		
		// List used to read in file
		ArrayList<String>  listFile = new ArrayList<String>();
		try {
			listFile = (ArrayList<String>) Files.readAllLines(this.path, Charset.forName("UTF-8"));
		} catch (IOException e) {
			System.out.println("Error: configuration file is incorrect!");
			return null;
		}
		
		// verify the correctness of string in file 
		boolean allRight = true;
		int i=0;
		
		while(i<tempHm.size() & allRight){
			int sizeContentListParam = listParam.get(i).length();
			int sizeContentListFile = listFile.get(i).length();
			// comparison
			if( ((String)listParam.get(i)+"=").equals(listFile.get(i).substring(0,sizeContentListParam+1)) ){
				// TODO you can cech if the value of config parameters are .lenght >0
				tempHm.replace(listParam.get(i),listFile.get(i).substring(sizeContentListParam+1, sizeContentListFile));
			}
			else{
				allRight = false;
				System.out.println("Error: file config parameters are incorrect!");
			}
			i++;
		}
		if(allRight){
			return tempHm;
		}
		else
			return null;
	}
}
