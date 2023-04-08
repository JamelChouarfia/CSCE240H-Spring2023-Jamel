/*
 * Written by Jamel Chouarfia
 */

import java.util.ArrayList;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.BufferedReader;
import java.io.FileReader;

public class statisticsRecorder {
	private ArrayList<String> chatLogs;
	private int sessionNum;
	private int userChats;
	private int answerHits;
	private int answerMisses;
	
	statisticsRecorder() {
		chatLogs = new ArrayList<String>();
		sessionNum = getSessionNum();
		userChats = 0;
		answerHits = 0;
		answerMisses = 0;
	}
	
	public void addChatLog(String log) {
		chatLogs.add(log);
	}
	
	public void addChat() {
		userChats++;
	}
	
	public void addHit() {
		answerHits++;
	}
	
	public void addMiss() {
		answerMisses++;
	}
	
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
	
	private String formatData() {
		return sessionNum+","+userChats+","+answerHits+","+answerMisses+","+(((double)answerHits)/userChats)*100;
	}
	
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
