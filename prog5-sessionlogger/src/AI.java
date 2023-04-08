/*
 * Written by Jamel Chouarfia
 */

import java.util.ArrayList;
import java.util.Random;

/**
 * The AI behind the chatbot
 * @author Jamel Chouarfia
 */
public class AI {
	private ArrayList<String> potentialAnswers;
	private Random generator;
	private String file;
	
	/**
	 * Creates the AI and initializes the required variables.
	 * @param potentialAnswers
	 */
	AI(ArrayList<String> potentialAnswers) {
		this.potentialAnswers = potentialAnswers;
		generator = new Random();
	}
	
	/**
	 * Checks if a file has been selected for reading
	 * @return True if there is, false if not
	 */
	public boolean hasFile() {
		return (file != null);
	}
	
	/**
	 * Returns the file loaded in
	 * @return A string which represents the file to read
	 */
	public String getFile() {
		return file;
	}
	
	/**
	 * Updates the ArrayList with a new set
	 * @param newAnswers The new ArrayList
	 */
	public void updateAnswers(ArrayList<String> newAnswers) {
		potentialAnswers = newAnswers;
	}
	
	/**
	 * Returns a response to the user
	 * @param input The input given by the user
	 * @return A string representing the AI's response
	 */
	public String response(String input) {
		if (input.equals("hi") || input.equals("hello") || input.equals("hola")) {
			file = null;
			return greeting();
		}
		
		if (input.equals("all")) {
			file = "all";
			return "Here is all of the stuff I know";
		}
		
		if (AISelection(input)) {
			return "Here is what I have for that";
		} else {
			return "Sorry, I don't understand you";
		}
	}
	
	/**
	 * Acts as the brain of the AI and finds an answer to the input
	 * @param input The question being asked by the user
	 * @return True if an answer was found, false if not
	 */
	private boolean AISelection(String input) {
		try {
			ArrayList<String> answers = new ArrayList<String>();
			ArrayList<Integer> answerStrength = new ArrayList<Integer>();
			int fileCounter = 0;
			int strength = 0;
			int results = 0;
			
			while (potentialAnswers.size() > fileCounter) {
				if (potentialAnswers.get(fileCounter) != "") {
					String temp = potentialAnswers.get(fileCounter);
					strength = LockMatch.lock_match(temp, input);
					
					if (strength > 5) {
						answerStrength.add(strength);
						answers.add(temp);
						results++;
					}
					
					fileCounter++;
				} else {
					fileCounter++;
				}
			}
			
			if (results == 0) {
				file = null;
				return false;
			}
			
			int highestValue = 0;
			int highestPos = 0;
			
			for (int i = 0; results > i; i++) {
				if (answerStrength.get(i) > highestValue) {
					highestValue = answerStrength.get(i);
					highestPos = i;
				}
			}
			
			file = answers.get(highestPos);
			
			return true;
		} catch (Exception e) {
			System.out.println(e);
			return false;
		}
	}

	/**
	 * Returns a greeting
	 * @return A greeting represented by a string
	 */
	private String greeting() {
		switch (generator.nextInt(5)) {
			case 0:
				return "Hi!";
			case 1:
				return "Hello!";
			case 2:
				return "Hello! What would you like to learn today?";
			case 3:
				return "Hi! What would you like to know?";
			case 4:
				return "Nice to see you!";
			default:
				return "Hello!";
		}
	}
}
