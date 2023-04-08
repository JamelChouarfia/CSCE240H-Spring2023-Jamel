/*
 * Written by Jamel Chouarfia
 */

import java.util.Scanner;
import java.io.IOException;
import java.util.ArrayList;

/**
 * A UI class that handles input and output
 * @author Jamel Chouarfia
 */
public class userInterface {
	private facadeAI facade;
	private Scanner input;
	
	/**
	 * Creates the UI and initializes the variables required for it to function
	 */
	userInterface() {
		facade = new facadeAI();
		input = new Scanner(System.in);
	}
	
	/**
	 * Forces the user to input an integer value
	 * @return An int value that the user inputted
	 */
	private int intCheck() {
        String userInput = "";

        while (true) {
            userInput = input.nextLine();

            try {
                return Integer.parseInt(userInput);
            } catch (Exception e) {
                System.out.println("Please input a number");
            }
        }
	}
	
	/**
	 * Asks the user if they want to update the database
	 */
	private void downloadPrompt() {
		System.out.println("Hello! Do you want to update the database? (This takes some time)\n1. Yes\n2. No\n");
		int userInputINT;
		
		while (true) {
			userInputINT = intCheck();

			switch(userInputINT) {
				case 1:
					facade.update();
					return;
				case 2:
					return;
				default:
					System.out.println("Please input a proper response");
					break;
			}
		}
	}
	
	/**
	 * 
	 * @param input
	 * @return
	 */
	private boolean AIResponse(String input) {
		input = input.toLowerCase();
		
		if (input.equals("quit") || input.equals("q") || input.equals("exit") || input.equals("leave") || input.equals("bye") || input.equals("goodbye")) {
			facade.addChatLog("\nUser: "+input);
			return true;
		}
		
		System.out.println("\nChatBOT: "+ facade.AIResponse(input)+"\n");
		
		try {
			ArrayList<String> list = facade.getFileResponse();
			
			if (list == null) {
				return false;
			}
			
			while (!list.isEmpty()) {
				facade.addChatLog(list.get(0));
				System.out.println(list.remove(0));
			}
		} catch (IOException e) {
			System.out.println(e);
		}
		
		return false;
	}
	
	/**
	 * Runs the UI and subsequently the rest of the program
	 */
	public void run() {		
		downloadPrompt();
		
		System.out.println("ChatBOT: Hello, I am here to answer any questions you have about the Avian Flu virus. Please ask away!");
		facade.addChatLog("ChatBOT: Hello, I am here to answer any questions you have about the Avian Flu virus. Please ask away!");
		
		while (true) {
			System.out.print("\nUser: ");
			
			if (AIResponse(input.nextLine())) {
				break;
			}
		}
		
		System.out.println("\nChatBOT: Goodbye!");
		facade.addChatLog("\nChatBOT: Goodbye!");
		
		if (!facade.save()) {
			System.out.println("An error has occured with saving");
		}
	}
}
