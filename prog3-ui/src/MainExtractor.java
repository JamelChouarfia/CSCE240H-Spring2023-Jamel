/*
 * Written by Jamel Chouarfia
 */

import java.util.ArrayList;
import java.util.Scanner;

public class MainExtractor {
    public static void main(String[] args) {
        ArrayList<String> childDirectories = new ArrayList<String>();
        ArrayList<String> secondChildDirectories = new ArrayList<String>();
        Scanner input = new Scanner(System.in);
        String query = "";
    	String temp = "";
        int timer = 0;
    	
        // Downloads the main site for avian flu from the CDC
        if (NewSiteHandler.downloadSite("https://www.cdc.gov/flu/avianflu/", "data/raw/avian-flu-cdc.txt")) {
        	System.out.println("Information from cdc.gov successfully downloaded");
        }
        
        // Extracts links from the main page for use in the next while loop
        if (NewSiteHandler.extractLinks("data/raw/avian-flu-cdc.txt")) {
    		System.out.println("The links have been extracted");
    		childDirectories = NewSiteHandler.getLinks("data/filtered/cdc-links.txt");
    	}
    	
    	// Downloads the pages for directories that are a child of the main page
    	while (!childDirectories.isEmpty()) {
    		temp = childDirectories.remove(0);
    		temp = temp.trim();
    		
    		if (temp.length() > 14) {
        		NewSiteHandler.downloadSite("https://www.cdc.gov"+temp, "data/raw/avian-flu-cdc-"+temp.substring(14)+".txt");
        		secondChildDirectories.add(temp.substring(14));
        		timer++;
    		}
    	}
    	
        // Extracts the raw data from the main page into something that can be easily readable by humans
        if (NewSiteHandler.filterSite("data/raw/avian-flu-cdc.txt", "data/filtered/cdc-filtered.txt")) {
        	System.out.println("Data from cdc.gov successfully filtered");
        }
            
        // Extracts the rest of the raw data into something that can be easily readable by humans
        while (timer >= 0) {
        	if (secondChildDirectories.isEmpty()) {
        		break;
        	}
        	
        	temp = secondChildDirectories.remove(0);
        	temp = temp.trim();
        	
        	childDirectories.add(temp);
        	
        	NewSiteHandler.filterSite("data/raw/avian-flu-cdc-"+temp+".txt", "data/filtered/cdc-filtered-"+temp+".txt");
        	timer--;
        }
        
        if (args.length != 0) {
            NewSiteHandler.printQuery(childDirectories, args[0]);
        }
        
        
        // Tries to answer the user's questions
        while (true) {
            System.out.println("Please feel free to ask any question. If you are done, type \"Quit\"");
            
            query = input.nextLine();
            
            if (query.equals("Quit") || query.equals("quit") || query.equals("q")) {
            	System.out.println("Goodbye!");
            	input.close();
            	
            	return;
            }
            
            NewSiteHandler.printQuery(childDirectories, query);
        }
    }
}