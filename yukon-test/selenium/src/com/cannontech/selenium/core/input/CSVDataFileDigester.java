/**
 * 
 */
package com.cannontech.selenium.core.input;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

import org.apache.log4j.Logger;
import org.junit.Before;

/**
 * This an API level class will extract username and password from csv file and 
 * return sets of data collection in multiple formats. Different Method Signature types
 * manipulate the raw csv data file and return a data collection in etc: format of an Array, 
 * Map.
 * 
 * @author anuradha.uduwage
 * 
 */
public class CSVDataFileDigester {

	private static final Logger log = Logger.getLogger(CSVDataFileDigester.class.getName());
	private String username;
	private String password;
	
	private final HashMap<String, String> parameters = new HashMap<String, String>();
	
	@Before
	public void setUp() {
		//nothing at this point.
	}

	/**
	 * Reading the the csv file and add to a String collection.
	 * 
	 * @param filename name of the csv file.
	 * @return lines collection of username and password.
	 */
	public Collection<String> parse(String filename) {
		Collection<String> lines = new ArrayList<String>();
		try {
			BufferedReader br = new BufferedReader(new FileReader(new File(
					filename)));
			String line;
			while ((line = br.readLine()) != null) {
				lines.add(line);
			}
		} catch (Exception e) {
			log.error("Error attempting reading file.", e);
			e.printStackTrace();
		}

		/*
		int i;
		for (i = 0; i < userInfo(lines).size(); i++) {
			if (i < 1) {
				this.username = userInfo(lines).get(i);
				this.password = userInfo(lines).get(i+1);
				//System.out.println(username + "-" + password);
			}
		}
		*/		
	
		return lines;
	}
	
	/**
	 * split the line at the end and format and retrun data as ArrayList. 
	 * @param lines collection of strings as an input.
	 * @return return an ArrayList of Strings.
	 */
	public ArrayList<String> userInfo(Collection<String> lines) {
		ArrayList<String> userNamePasswordArrayList = new ArrayList<String>();
		for (String line : lines) {
			String[] recordsOnLine = line.split(",");
			for (String record : recordsOnLine) {
				userNamePasswordArrayList.add(record);
			}
		}
		return userNamePasswordArrayList;
	}
	
	/**
	 * Analyze string collection and put username and password in Key, Value pairs.
	 * @param lines Collection of Strings as an input.
	 * @return returns a HashMap with username and password as Key value pairs.
	 */
	public HashMap<String, String> returnStringParams(Collection<String> lines) {
		for (String line : lines) {
			String user = line.substring(0, line.indexOf(",")).trim();
			String pass = line.substring(line.indexOf(",")+1).trim();
			if(!parameters.containsKey(user)) {
				log.info("Adding Input Param: " + user + " => " + pass);
				parameters.put(user, pass);
			}
			System.out.println(user + "=" + pass);
		}
		return parameters;
	}
	
	/**
	 * Parse data from csv file and return data in a hash map.
	 * @param fileName name of the csv file.
	 * @return parameters a hash map.
	 */
	public HashMap<String, String> getParams(String fileName) {
		Collection<String> lines = new ArrayList<String>();
		try {
			BufferedReader br = new BufferedReader(new FileReader(new File(
					fileName)));
			String line;
			while ((line = br.readLine()) != null) {
				lines.add(line);
			}
		} catch (Exception e) {
			log.error("Error attempting reading " + fileName + ".", e);
			e.printStackTrace();
		}
		for (String line : lines) {
			String user = line.substring(0, line.indexOf(",")).trim();
			String pass = line.substring(line.indexOf(",")+1).trim();
			if(!parameters.containsKey(user)) {
				log.info("Adding Input Param: " + user + " => " + pass);
				parameters.put(user, pass);
			}
		}
		return parameters;		
	}
	
	/**
	 * Reminder: File needs to be at the root level of the workspace (in
	 * eclipse) I found this by hard way.
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		CSVDataFileDigester readFromCSV = new CSVDataFileDigester();
		Collection<String> list = readFromCSV.parse("UserName_Password.csv");
		readFromCSV.returnStringParams(list);
	}
	
	/**
	 * Return username from csv file. 
	 * @return username yukon app username
	 */
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	
	/**
	 * Return password from csv file.
	 * @return password yukon password.
	 */
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
}
