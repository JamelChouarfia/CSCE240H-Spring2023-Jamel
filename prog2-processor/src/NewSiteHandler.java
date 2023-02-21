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
import java.util.ArrayList;
import java.util.Scanner;

public class NewSiteHandler {
	private static HttpURLConnection siteConnection;
	private static URL siteLocation;
	
	/**
	 * Attempts to download the specified website into a .txt file
	 * @param destination The website's URL
	 * @param file The file name where the downloaded data is stored
	 * @return True if the site downloaded, false if it didn't
	 */
	public static boolean downloadSite(String destination, String file) {
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
	 * Extracts links and puts them into a file called "Links.txt"
	 * @param file The file that links are beinge extracted from
	 * @return A true if links were extracted, false if not
	 */
	public static boolean extractLinks(String file) {
		try {
			Scanner reader = new Scanner(new File(file));
			BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream("data/filtered/cdc-links-unorganized.txt")));
			String line = reader.next();
			
			while (reader.hasNextLine()) {
				if (line.contains("a href")) {
					line = filterLink(line);
					
					if (!line.equals("There was no link")) {
						writer.write(line);
						writer.newLine();
					}
				}
				
				line = reader.nextLine();
			}
			
			writer.close();
			
			organizeLinks("data/filtered/cdc-links.txt");
			
			return true;
		} catch (Exception e) {
			System.out.println(e);
			return false;
		}
	}
	
	/**
	 * Returns an arraylist of every link that should have its page downloaded
	 * @param file The file that contains the links
	 * @return An arraylist containing every link
	 */
	public static ArrayList<String> getLinks(String file) {
		ArrayList<String> validLinks = new ArrayList<String>();
		
		try {
			Scanner reader = new Scanner(new File(file));
			String line = reader.next();
			
			while (reader.hasNext()) {
				validLinks.add(line);
				line = reader.nextLine();
			}
		} catch (Exception e) {
			System.out.println(e);
			System.exit(2);
		}
		
		return validLinks;
	}
	
	/**
	 * Filters the downloaded data and places it into a new file
	 * @param originalFile The file being filtered
	 * @param filteredFile The file name where the filtered data will be stored
	 * @return True if the data was filtered and saved, false if it wasn't
	 */
	public static boolean filterSite(String originalFile, String filteredFile) {
		try {
			Scanner reader = new Scanner(new File(originalFile));
			BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(filteredFile)));
			String line = reader.next();
			
			while (reader.hasNextLine()) {
				if (line.contains("<p>")) {
					line = filterLine(line);
					line = trimLine(line);
					line = convertCharEntity(line);
					writer.write(line);
					writer.newLine();
				}
				
				line = reader.nextLine();
			}
			
			writer.close();
			
			return true;
		} catch (Exception e) {
			System.out.println(e);
			System.exit(2);
			return false;
		}
	}
	
	/**
	 * Prints out an answer to a question if it is both understood and the computer has an answer
	 * @param potential Files An ArrayList of locations where the requested info could be found
	 * @param query The question that the user is asking
	 */
	public static void printQuery(ArrayList<String> potentialFiles, String query) {
		try {
			while (true) {
				if (potentialFiles.isEmpty()) {
					System.out.println("Sorry, I do not understand what you are saying");
					return;
				}
				
				String temp = potentialFiles.remove(0);
				System.out.println("Temp: "+temp+" Query: "+query);
				if (LockMatch.lock_match(temp, query) > 15) {
					Scanner reader = new Scanner(new File("data/filtered/cdc-filtered-"+temp+".txt"));
					String line = reader.next();
					
					while (reader.hasNext()) {
						System.out.println(line);
						line = reader.nextLine();
					}
					
					reader.close();
					
					return;
				}
			}
		} catch (Exception e) {
			System.out.println(e);
			return;
		}
	}
	
	/**
	 * Trims off all of the HTML tags and data that isn't a part of a <p> tag
	 * @param input The String that will be trimmed
	 * @return The trimmed String
	 */
	private static String filterLine(String input) {
		StringBuilder line = new StringBuilder(input.trim());
		int position = 0;
		// trigger is used for activating a while loop that deletes every character before it gets to another p tag
		
		while (line.length() > position) {
			
			// Starts removing characters when it gets to the beginning of a tag
			if (line.charAt(position) == '<') {
				// Deletes the < and p character if they're there, if not then it sets trigger to true
				if (line.charAt(position + 1) == 'p') {
					line.deleteCharAt(position);
					line.deleteCharAt(position);
					line.deleteCharAt(position);
				} else if (line.charAt(position + 1) == '/') {
					if (line.charAt(position + 2) == 'p') {
						line.deleteCharAt(position);
						line.deleteCharAt(position);
						line.deleteCharAt(position);
						line.deleteCharAt(position);
					}
				}
			}

			// Ends the loop if the line is done being trimmed
			if (position >= line.length()) {
				break;
			}
			
			// Moves position forward one if there isn't another tag to delete
			if (line.charAt(position) != '<') {
				position++;
			} else if (line.charAt(position + 1) != 'p') {
				position++;
			}
		}
		
		return line.toString();
	}
	
	/**
	 * Trims off the remaining tags in the string
	 * @param input The string that is gonna be trimmed
	 * @return A string that has no tags
	 */
	private static String trimLine(String input) {
		StringBuilder line = new StringBuilder(input);
		int position = 0;
		boolean trigger = false;
		boolean secondTrigger = false;
		
		while (line.length() > position) {
			if (line.charAt(position) == '<') {
				trigger = true;
			}
			
			while (trigger) {
				if (position + 1 > line.length()) { 
					break;
				}
				if (line.charAt(position) == '>') {
					line.deleteCharAt(position);
					
					if (secondTrigger) {
						trigger = false;
						secondTrigger = false;
						break;
					} else {
						secondTrigger = true;
						
						if (position + 1 >= line.length()) {
							break;
						}
					}
				} else {
					line.deleteCharAt(position);
				}
			}
			
			if (position + 1 >= line.length()) {
				break;
			}
			
			if (line.charAt(position) != '<') {
				position++;
			}
		}
		
		return line.toString();
	}
	
	/**
	 * Converts character entities (Like &amp;) into normal characters
	 * @param input The string that's being worked on
	 * @return A string that has no character entities
	 */
	private static String convertCharEntity(String input) {
		StringBuilder line = new StringBuilder(input);
		int position = 0;
		char type = 'a';
		
		while (line.length() > position) {
			if (line.charAt(position) == '&') {
				type = line.charAt(position + 1);
				
				while (line.charAt(position) != ';') {
					line.deleteCharAt(position);
				}
				
				line.deleteCharAt(position);
				
				if (type == 'n') {
					line.replace(position, position, " ");
				} else if (type == 'r') {
					line.replace(position, position, "'");
				}
			}
			
			position++;
		}
		
		return line.toString();
	}
	
	/**
	 * Filters out links from the provided String
	 * @param input The String that may contain links
	 * @return A string that either has links, or says "There was no links"
	 */
	private static String filterLink(String input) {
		StringBuilder line = new StringBuilder(input);
		StringBuilder output = new StringBuilder();
		final int shift = 9;
		int position = 0;
		
		while (line.length() > position) {
			if (line.charAt(position) == '<') {
				if (line.charAt(position + 1) == 'a') {
					while (line.charAt(position + shift) != '"') {
						output.append(line.charAt(position + shift));
						position++;
					}
					
					output.append(' ');
					position = position + shift;
				}
			}
			position++;
		}
		
		if (output.toString().equals("")) {
			return "There was no link";
		}
		
		return output.toString();
	}

	/**
	 * Organizes links from a filtered file and puts the organized links into a new file
	 * @param file The file name of the file containing filtered, but not organized, links
	 */
	private static void organizeLinks(String file) {
		try {
			Scanner reader = new Scanner(new File("data/filtered/cdc-links-unorganized.txt"));
			BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file)));
			String line = reader.next();

			while (reader.hasNextLine()) {
				if (line.length() > 5) {
					if (line.charAt(1) == 'f' && line.charAt(5) == 'a') {
						if (50 > line.length()) {
							writer.write(line);
							writer.newLine();
						}
					}
				}
				
				line = reader.nextLine();
			}
			
			writer.close();
			
			return;
		} catch (Exception e) {
			System.out.println(e);
			return;
		}
	}
}
