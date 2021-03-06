package com.xkcd.n936.lib;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

/**
 * This class accumulates words from either a file, inputstream or buffered reader, 
 * and then makes passwords by picking words randomly.
 * 
 * Options:
 * numWords: number of words to pick when making password
 * lowercase: lowercase words when adding them to the dictionary
 * space out password: add spaces between words when generating password (more readable)
 * 
 * @author alok
 *
 */
public class EasyPassword {
	private Random staticRandom = new Random();
	
	private String pickRandomWord(String[]words) {
		int size = words.length;
		int item = staticRandom.nextInt(size); // In real life, the Random object should be rather more shared than this
		return words[item];
	}
	public String makePassword() {
		String[]words = wordHash.keySet().toArray(new String[0]);
		if (words.length > 0) {
			String retval = "";
			for (int i=0;i<getNumWords();i++) {
				if (retval.length() > 0 && isSpaceOutPassword())
					retval += " ";
				retval+=pickRandomWord(words);
			}
			return retval;
		} else {
			return null;
		}
	}


	public void addWordsFromFile(String filename) throws IOException {
		FileInputStream fis = new FileInputStream(filename);
		addWords(fis);
	}
	public void addWords(File file) throws IOException {
		FileInputStream fis = new FileInputStream(file);
		addWords(fis);
	}

	public void addWords(InputStream fis) throws IOException {
		BufferedReader r = new BufferedReader(new InputStreamReader(fis));
		addWords(r);
	}

	private void addWords(BufferedReader r) throws IOException {
		String lineItem;
	    while ((lineItem = r.readLine()) != null) {
			for (String word:lineItem.split("[^\\w]"))
				addWord(word);
	    }  
	}

	public void addWord(String origval) {
		String sval = lowercase?origval.toLowerCase():origval;
		if (wordHash.containsKey(sval)) {
			wordHash.put(sval, 1+ wordHash.get(sval));
		} else {
			wordHash.put(sval, 1);
		}
	}

	public void clearDict() {
		wordHash.clear();
	}

	/**
	 * 
	 * @return number of words in dictionary
	 */
	public int getDictSize() {
		return wordHash.size();
	}

	/**
	 * how many words should we pick for each password?
	 */
	public void setNumWords(int numWords) {
		this.numWords = numWords;
	}


	public int getNumWords() {
		return numWords;
	}

	/**
	 * should all words be lowercased before adding to dict?
	 */
	public void setLowercase(boolean lowercase) {
		if (wordHash.size() > 0)
			throw new IllegalStateException("Cannot modify lowercase after words are added to the dictionary");
		this.lowercase = lowercase;
	}
	public boolean isLowercase() {
		return lowercase;
	}

	public void setSpaceOutPassword(boolean spaceOutPassword) {
		this.spaceOutPassword = spaceOutPassword;
	}
	public boolean isSpaceOutPassword() {
		return spaceOutPassword;
	}

	private Map<String, Integer> wordHash = new HashMap<String, Integer>();
	private int numWords = 4;
	
	private boolean lowercase = true;

	private boolean spaceOutPassword = true;

	
	

	private static int[] parsedVals(String[] usedValues) {
		int[]parsed = new int[usedValues.length];
		for (int i = 0; i < usedValues.length; i++)
			parsed[i] = Integer.parseInt(usedValues[i]);
		return parsed;
	}
	private static boolean inArray(int value, int[] usedValues) {
		for (int i = 0; i< usedValues.length; i++)
			if (value == usedValues[i])
				return true;
		return false;
	}
	/**
	   * Returns a string array consisting of all the integer values between two points
	   * whose string representation is not in usedValues.
	   *
	   * @param startValue int The minimum allowed value (inclusive).
	   * @param endValue int The maximum allowed valued (inclusive).
	   * @param usedValues String[] The String representation of the used integers, or null.
	   * @return String[] The String representation of the available integers.
	   */
	public static String[] findAvialable(int startValue, int endValue, String[] usedValues) {
	    ArrayList<String> values = new ArrayList<String>();
	    int[] usedInts = parsedVals(usedValues);
	    for (int i = startValue; i < endValue; i++) {
	    	if (!inArray(i, usedInts))
	    		values.add("" + i);
	    }
	    return values.toArray(new String[0]);
	}
}
