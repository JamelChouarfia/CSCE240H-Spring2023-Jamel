/*
 * Written by Jamel Chouarfia
 */

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

/**
 * A facade that acts as a middle man for the UI and the other classes
 * @author Jamel Chouarfia
 */
public class facadeAI {
	ArrayList<String> childDirectories;
	statisticsRecorder statistics;
	boolean fileResponse;
	AI cdcAI;
	
	/**
	 * Creates a new facade and initializes its ArrayList
	 */
	facadeAI() {
		childDirectories = new ArrayList<String>();
		statistics = new statisticsRecorder();
		getLinks("data/filtered/cdc-links.txt");
		cdcAI = new AI(childDirectories);
		fileResponse = false;
	}
	
	/**
	 * Loads in the links from the filtered file to the ArrayList
	 * @param file The file containing the filtered links
	 */
	private void getLinks(String file) {
		try {
			childDirectories = siteDownloader.getLinks(file);
			return;
		} catch (Exception e) {
			System.out.println(e);
			return;
		}
	}
	
	/**
	 * Filters out the data within the sites to something more presentable
	 */
	private void filterSites() {
        for (int timer = 0; childDirectories.size() > timer; timer++) {
        	if (childDirectories.get(timer) != "") {
        		siteDownloader.filterSite("data/raw/"+childDirectories.get(timer).substring(14)+".txt", "data/filtered/"+childDirectories.get(timer).substring(14)+".txt");
        	}
        }	
	}
	
	/**
	 * Updates the database of site files
	 * @return True if it worked, false if not
	 */
	public boolean update() {
		try {
			siteDownloader.download("https://www.cdc.gov/flu/avianflu/", "data/raw/avian-flu-cdc.txt");
			siteDownloader.downloadLinks("data/raw/avian-flu-cdc.txt");
			siteDownloader.linkCleaner("data/filtered/cdc-links.txt");
			getLinks("data/filtered/cdc-links.txt");
			
			for (int timer = 0; childDirectories.size() > timer; timer++) {
				if (childDirectories.get(timer) != "") {
					siteDownloader.download("https://www.cdc.gov"+childDirectories.get(timer), "data/raw/"+childDirectories.get(timer).substring(14)+".txt");
				}
			}
			
			filterSites();
			cdcAI.updateAnswers(childDirectories);
			
			return true;
		} catch (Exception e) {
			System.out.println(e);
			return false;
		}
	}
	
	/**
	 * Gives the AI an input and returns a response. This method is also responsible for updating the statistics class
	 * @param input The input given by the user (Their query)
	 * @return The AI's response
	 */
	public String AIResponse(String input) {
		String result = cdcAI.response(input);
		
		statistics.addChatLog("\nUser: "+input);
		statistics.addChatLog("\nChatBOT: "+result);
		statistics.addChat();
		
		if (result.equals("Sorry, I don't understand you")) {
			statistics.addMiss();
		} else {
			statistics.addHit();
		}
		
		return result;
	}
	
	/**
	 * Returns an ArrayList of Strings that represents the AI's answer to the user's question
	 * @return An ArrayList of Strings that contains the answer
	 * @throws IOException If an error occurs while searching for the file
	 */
	public ArrayList<String> getFileResponse() throws IOException {
		if (!cdcAI.hasFile()) {
			return null;
		}
		
		ArrayList<String> result = new ArrayList<String>();
		String file = cdcAI.getFile();
		String temp = null;
		
		if (!file.equals("all")) {
			BufferedReader reader = new BufferedReader(new FileReader("data/filtered/"+file.substring(14)+".txt"));
			temp = reader.readLine();
			
			while (temp != null) {
				result.add(temp);
				temp = reader.readLine();
			}
			
			reader.close();
			return result;
		}
		
		for (int fileCounter = 0; childDirectories.size() > fileCounter; fileCounter++) {
			if (childDirectories.get(fileCounter) != "") {
				BufferedReader reader = new BufferedReader(new FileReader("data/filtered/"+childDirectories.get(fileCounter).substring(14)+".txt"));
				temp = reader.readLine();
				
				while (temp != null) {
					result.add(temp);
					temp = reader.readLine();
				}
				
				if (!(childDirectories.size() > fileCounter)) {
					reader.close();
				}
			}
		}
		
		return result;
	}

	/**
	 * Saves the data stored within the statisticsRecorder class to a file
	 * @return True if it worked, false if an error occured
	 */
	public boolean save() {
		return statistics.save();
	}
	
	/**
	 * Saves the chat logs
	 * @param log The log being saved
	 */
	public void addChatLog(String log) {
		statistics.addChatLog(log);
	}
}