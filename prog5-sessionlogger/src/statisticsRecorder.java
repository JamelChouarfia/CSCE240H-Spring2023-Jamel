/*
 * Written by Jamel Chouarfia
 */

import java.util.ArrayList;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.BufferedReader;
import java.io.FileReader;

/**
 * This class keeps track of various statistics and saves them right before the program ends
 * @author Jamel Chouarfia
 */
public class statisticsRecorder {
	private ArrayList<String> chatLogs;
	private int sessionNum;
	private int userChats;
	private int answerHits;
	private int answerMisses;
	
	/**
	 * Creates a new instance of this class and initializes the required variables
	 */
	statisticsRecorder() {
		chatLogs = new ArrayList<String>();
		sessionNum = getSessionNum();
		userChats = 0;
		answerHits = 0;
		answerMisses = 0;
	}
	
	/**
	 * Adds a new chat log to the ArrayList
	 * @param log The log being added
	 */
	public void addChatLog(String log) {
		chatLogs.add(log);
	}
	
	/**
	 * Increases userChats by one
	 */
	public void addChat() {
		userChats++;
	}
	
	/**
	 * Increases answerHits by one
	 */
	public void addHit() {
		answerHits++;
	}
	
	/**
	 * Increases answerMisses by one
	 */
	public void addMiss() {
		answerMisses++;
	}
	
	/**
	 * Reads the chat_sessions.csv file and returns the session number that's next
	 * @return The session number
	 */
	private int getSessionNum() {
		try {
			BufferedReader reader = new BufferedReader(new FileReader("data/chat_sessions.csv"));
			String temp = reader.readLine();
			int counter = 0;
			
			while (temp != null) {
				temp = reader.readLine();
				counter++;
			}
			
			reader.close();
			return counter;
		} catch (Exception e) {
			return 9999;
		}
	}
	
	/**
	 * Formats the data to be used in chat_sessions.csv
	 * @return A formatted String
	 */
	private String formatData() {
		return sessionNum+","+userChats+","+answerHits+","+answerMisses+","+(((double)answerHits)/userChats)*100;
	}
	
	/**
	 * Saves both the chat logs and the statistics to their respective files
	 * @return True if it worked, false if it failed
	 */
	public boolean save() {
		try {
			BufferedWriter writerCSV = new BufferedWriter(new FileWriter("data/chat_sessions.csv", true));
			
			writerCSV.newLine();
			writerCSV.write(formatData());
			writerCSV.close();
			
			BufferedWriter writerLog = new BufferedWriter(new FileWriter("data/chat_sessions/chat_number_"+sessionNum+".txt"));
			
			while (!chatLogs.isEmpty()) {
				writerLog.write(chatLogs.remove(0));
				writerLog.newLine();
			}
			
			writerLog.close();
			return true;
		} catch (Exception e) {
			System.out.println(e);
			return false;
		}
	}
}
