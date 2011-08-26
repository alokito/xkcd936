package com.xkcd.n936;

import java.io.IOException;

public class MakePassword {

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
		double bits = EasyPasswordStats.calcBits(pwd);
		double hexlen = EasyPasswordStats.calcHexLen(pwd);
		double alphanum = EasyPasswordStats.calcAlphaNumLen(pwd);
		double randstrlen = EasyPasswordStats.calcRandLen(pwd);
		int dictSize = pwd.getDictSize();
		System.out.println("password: " + password);
		System.out.println("constructed by picking "+pwd.getNumWords()+" from dictsize " + dictSize);
		System.out.println("bits: " + bits);
		System.out.println("equivalent to hex string of length: " + hexlen );
		System.out.println("equivalent to alphanum string of length: " + alphanum );
		System.out.println("equivalent to random string of length: " + randstrlen);
	}

}
