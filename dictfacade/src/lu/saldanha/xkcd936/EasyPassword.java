package lu.saldanha.xkcd936;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StreamTokenizer;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class EasyPassword {
	/**
	 * This method will parse all files on the command line and 
	 * generate a dictionary of words. It will then generate a random password by picking
	 * from the dictionary.
	 * @throws IOException 
	 * 
	 */
	public static final  void main(String [] argv) throws IOException {
		EasyPassword pwd = new EasyPassword();
		for (String filename : argv) { 
			pwd.addWordsFromFile(filename);
		}
		pwd.setNumWords(4);
		String password = pwd.makePassword();
		double bits = pwd.calcBits();
		double hexlen = pwd.calcHexLen();
		double randstrlen = pwd.calcRandLen();
		int dictSize = pwd.getDictSize();
		System.out.println("password: " + password);
		System.out.println("constructed by picking "+pwd.getNumWords()+" from dictsize " + dictSize);
		System.out.println("bits: " + bits);
		System.out.println("equivalent to hex string of length: " + hexlen );
		System.out.println("equivalent to random string of length: " + randstrlen);
	}

	private Random staticRandom = new Random();
	
	private String pickRandomWord(String[]words) {
		int size = words.length;
		int item = staticRandom.nextInt(size); // In real life, the Random object should be rather more shared than this
		return words[item];
	}
	private String makePassword() {
		String[]words = wordHash.keySet().toArray(new String[0]);
		if (words.length > 0) {
			String retval = "";
			for (int i=0;i<getNumWords();i++)
				retval+=pickRandomWord(words);
			return retval;
		} else {
			return null;
		}
	}


	public void addWordsFromFile(String filename) throws IOException {
		FileInputStream fis = new FileInputStream(filename);
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


	/**
	 * 
	 * @return number of words in dictionary
	 */
	private int getDictSize() {
		return wordHash.size();
	}

	/**
	 * 
	 * @return length of equivalent hex string given current num words and dict size.
	 * 
	 */
	public double calcHexLen() {
		// TODO Auto-generated method stub
		return 0;
	}
	
	public double calcRandLen() {
		// TODO Auto-generated method stub
		return 0;
	}

	/**
	 * 
	 * @return bit strength given current num words and dict size.
	 * 
	 */
	private double calcBits() {
		// TODO Auto-generated method stub
		return 0;
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
		this.lowercase = lowercase;
	}
	public boolean isLowercase() {
		return lowercase;
	}

	private Map<String, Integer> wordHash = new HashMap<String, Integer>();
	private int numWords;
	
	private boolean lowercase = true;

}
