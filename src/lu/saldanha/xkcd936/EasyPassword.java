package lu.saldanha.xkcd936;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

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
		this.lowercase = lowercase;
	}
	public boolean isLowercase() {
		return lowercase;
	}

	private Map<String, Integer> wordHash = new HashMap<String, Integer>();
	private int numWords = 4;
	
	private boolean lowercase = true;

}
