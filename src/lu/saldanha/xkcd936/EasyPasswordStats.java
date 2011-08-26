package lu.saldanha.xkcd936;

import java.io.IOException;

public class EasyPasswordStats {

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
		double bits = calcBits(pwd);
		double hexlen = calcHexLen(pwd);
		double alphanum = calcAlphaNumLen(pwd);
		double randstrlen = calcRandLen(pwd);
		int dictSize = pwd.getDictSize();
		System.out.println("password: " + password);
		System.out.println("constructed by picking "+pwd.getNumWords()+" from dictsize " + dictSize);
		System.out.println("bits: " + bits);
		System.out.println("equivalent to hex string of length: " + hexlen );
		System.out.println("equivalent to alphanum string of length: " + alphanum );
		System.out.println("equivalent to random string of length: " + randstrlen);
	}

	/**
	 * 
	 * @return length of equivalent hex string given current num words and dict size.
	 * 
	 */
	public static double calcHexLen(EasyPassword pass) {
		double hexBitsPerChar = 4;// Math.log(16)/Math.log(2)
		return calcBits(pass)/hexBitsPerChar;
	}
	
	public static double calcAlphaNumLen(EasyPassword pass) {
		return calcLen(pass,26 // letters 
				+ 10 // digits 
				);
	}
	public static double calcRandLen(EasyPassword pass) {
		return calcLen(pass, 26 // letters 
				+ 10 // digits 
				+ 10 // !@#$%^&*()
				+ 5 //~`-=+_ 
				);
	}
	public static double calcLen(EasyPassword pass, int numSymbols) {
		double hexBitsPerChar = Math.log(numSymbols)/Math.log(2);
		return calcBits(pass)/hexBitsPerChar;
	}

	/**
	 * 
	 * @return bit strength given current num words and dict size.
	 * 
	 */
	public static double calcBits(EasyPassword pass) {
		return (double)pass.getNumWords() * Math.log(pass.getDictSize())/Math.log(2);
	}
}
