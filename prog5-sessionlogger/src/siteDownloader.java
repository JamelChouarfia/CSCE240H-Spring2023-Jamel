/*
 * Written by Jamel Chouarfia
 */

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Scanner;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

/**
 * A class containing a bunch of methods that downloads and manipulates HTML code
 * @author Jamel Chouarfia
 */
public class siteDownloader {
	/**
	 * Downloads the HTML of a site if a connection can be established
	 * @param destination The URL of the website
	 * @param file The destination where you want the HTML to be saved
	 * @return True if the downloading was successful, false if not
	 */
	public static boolean download(String destination, String file) {
		try {
			URL siteLocation = new URL(destination);
			HttpURLConnection siteConnection = (HttpURLConnection) siteLocation.openConnection();
			
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
	 * Filters out most of the HTML code into a new file that contains the content the user wishes to read
	 * @param originalFile The destination of the raw HTML code
	 * @param filteredFile The destination where you want the new filtered text to be
	 */
	public static void filterSite(String originalFile, String filteredFile) {
		try {
			FileWriter writer = new FileWriter(filteredFile);
			File avianFlu = new File(originalFile);
			Document doc = Jsoup.parse(avianFlu, "UTF-8", "");
			Elements textblock;
			
			switch (0) {
				case 0:
					textblock = doc.getElementsByClass("cdc-textblock");
					if (!textblock.isEmpty()) {
						break;
					}
				case 1:	
					textblock = doc.getElementsByClass("col-md-6");
					if (!textblock.isEmpty()) {
						break;
					}
				case 2:
					textblock = doc.getElementsByClass("col-md-12");
					if (!textblock.isEmpty()) {
						break;
					}
				default:
					writer.close();
					return;
			}
			
			Element[] textblocks = new Element[100];
			
			for (int i = 0; !textblock.isEmpty(); i++) {
				textblocks[i] = textblock.remove(0);
				writer.write(textblocks[i].wholeText());
				writer.write("\n");
			}
			
			writer.close();
			return;
		} catch (Exception e) {
			System.out.println(e);
			return;
		}
	}
	
	/**
	 * A helper method that breaks up lines with multiple links
	 * @param input The line to break up
	 * @param writer The writer which is current writing to the links file
	 * @throws IOException
	 */
	private static void lineBreaker(String input, BufferedWriter writer) throws IOException {
		int startPos = 0;
		int endPos = 0;
		
		while (input.length() > endPos) {
			if (input.charAt(endPos) == ' ') {
				if (input.charAt(1) == 'f' && input.charAt(5) == 'a') {
					writer.write(input.substring(startPos, endPos));
					writer.newLine();
				}
				
				startPos = endPos + 1;
			}
			
			endPos++;
		}
	}
	
	/**
	 * Organizes all of the downloaded links into a new file while removing unnecessary links
	 * @param file The location of the unorganized links
	 */
	private static void organizeLinks(String file) {
		try {
			Scanner reader = new Scanner(new File("data/filtered/cdc-links-unorganized.txt"));
			BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file)));
			String line = reader.next();

			while (reader.hasNextLine()) {
				if (line.length() > 5) {
					if (line.contains(" ")) {
						lineBreaker(line, writer);
					}
					
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
	
	/**
	 * Filters the links out of the String provided
	 * @param input A String which should contain HTML code
	 * @return Either a String with the links found, or a String saying "There was no link"
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
	 * Checks the downloaded file for links and saves them when found
	 * @param file The file where the wanted links are located
	 * @return True if everything worked fine, false if something failed
	 */
	public static boolean downloadLinks(String file) {
		try {
			Scanner reader = new Scanner(new File(file));
			BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream("data/filtered/cdc-links-unorganized.txt")));
			String line = reader.next();
			
			while (reader.hasNextLine()) {
				if (line.contains("a href")) {
					line = filterLink(line);
					
					if (line.charAt(line.length() - 1) == ' ') {
						line = line.substring(0, line.length() - 1);
					}
					
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
	 * Returns an ArrayList of Strings which each contain a link to more information
	 * @param file The destination of the file with the links
	 * @return An ArrayList of Strings which each store one link
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
	 * Removes duplicates and other links that aren't needed
	 * @param file The destination of the file storing all of the links
	 */
	public static void linkCleaner(String file) {
		try {
			ArrayList<String> cleanLinks = new ArrayList<String>();
			ArrayList<String> links = getLinks(file);
			String tempLink;
			int timer;
			
			while (!links.isEmpty()) {
				tempLink = links.remove(0);
				timer = 0;
				
				while (true) {
					if (tempLink == null) {
						timer = 999;
						break;
					}
					
					if (tempLink.length() == 0) {
						timer = 999;
						break;
					}
					
					if (tempLink.charAt(0) == '#') {
						timer = 999;
						break;
					}
					
					if (tempLink.charAt(5) != 'a') {
						timer = 999;
						break;
					}
					
					break;
				}
				
				while (links.size() > timer) {
					if (tempLink.equals(links.get(timer))) {
						links.remove(timer);
					} else {
						timer++;
					}
					
					if (links.size() == timer) {
						cleanLinks.add(tempLink);
					}
				}				
			}
			
			BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file)));
			
			while (!cleanLinks.isEmpty()) {
				writer.write(cleanLinks.remove(0));
				writer.newLine();
			}
			
			writer.close();
		} catch (Exception e) {
			System.out.println(e);
			return;
		}
	}
}