/*
 * Written by Jamel Chouarfia
 */

import java.io.File;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;

public class SiteHandler {
	HttpURLConnection siteConnection;
	URL siteLocation;
	
	/**
	 * Attempts to download the specified website into a .txt file
	 * @param destination The website's URL
	 * @param file The file name where the downloaded data is stored
	 * @return True if the site downloaded, false if it didn't
	 */
	public boolean DownloadSite(String destination, String file) {
		try {
			siteLocation = new URL(destination);
			siteConnection = (HttpURLConnection) siteLocation.openConnection();
			
			if (siteConnection.getResponseCode() == 200) {
				BufferedReader reader = new BufferedReader(new InputStreamReader(siteConnection.getInputStream()));
				BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file)));
				String line = reader.readLine();
				
				while (line != null) {
					writer.write(line);
					writer.newLine();
					line = reader.readLine();
				}
				
				writer.close();
				reader.close();
				return true;
			} else {
				return false;
			}
		} catch (Exception e) {
			return false;
		}
	}
	
	/**
	 * Filters the downloaded data and places it into a new file
	 * @param originalFile The file being filtered
	 * @param filteredFile The file name where the filtered data will be stored
	 * @return True if the data was filtered and saved, false if it wasn't
	 */
	public boolean FilterSite(String originalFile, String filteredFile) {
		try {
			Scanner reader = new Scanner(new File(originalFile));
			BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(filteredFile)));
			String line = reader.next();
			
			while (reader.hasNextLine()) {
				if (line.contains("<p>")) {
					line = lineTrim(line);
					writer.write(line);
					writer.newLine();
				}
				
				line = reader.nextLine();
			}
			
			writer.close();
			
			return true;
		} catch (Exception e) {
			System.out.println(e);
			return false;
		}
	}
	
	/**
	 * Trims off all of the HTML tags and data that isn't a part of a p tag
	 * @param input The String that will be trimmed
	 * @return The trimmed String
	 */
	private String lineTrim(String input) {
		StringBuilder line = new StringBuilder(input.trim());
		boolean trigger = false;
		int position = 0;
		// trigger is used for activating a while loop that deletes every character before it gets to another p tag
		
		while (line.length() > position) {
			
			// Starts removing characters when it gets to the beginning of a tag
			if (line.charAt(position) == '<') {
				// Deletes the < and p character if they're there, if not then it sets trigger to true
				if (line.charAt(position + 1) == 'p') {
					line.deleteCharAt(position);
					line.deleteCharAt(position);
				} else {
					trigger = true;
				}
				
				// The while loop that deletes everything that is not a p tag
				while (trigger) {
					if (line.length() == position + 1) {
						break;
					}
					
					// Checks if the tag is a p tag and, if so, deletes it and turns off the loop
					if (line.charAt(position) == '<') {
						if (line.charAt(position + 1) == 'p') {
							line.deleteCharAt(position);
							line.deleteCharAt(position);
							trigger = false;
							break;
						}
					}
					
					// Deletes the > char
					line.deleteCharAt(position);
				}
				
				// Deletes the > char
				line.deleteCharAt(position);
			}
			
			// Ends the loop if the line is done being trimmed
			if (position == line.length()) {
				break;
			}
			
			// Moves position forward one if there isn't another tag to delete
			if (line.charAt(position) != '<') {
				position++;
			}
		}
		
		return line.toString();
	}
}