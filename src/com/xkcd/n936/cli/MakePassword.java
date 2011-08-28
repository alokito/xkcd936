package com.xkcd.n936.cli;

import java.io.IOException;

import com.xkcd.n936.lib.EasyPassword;
import com.xkcd.n936.lib.EasyPasswordStats;

public class MakePassword {

	/**
	 * This method will parse all files on the command line and 
	 * generate a dictionary of words. It will then generate a random password by picking
	 * from the dictionary.
	 * @throws IOException 
	 * 
	 */
	public static final  void main(String [] argv) throws IOException {
		if (argv.length == 0) {
			System.err.println("Please list at least one file on the command line\n");
			printUsage();
		} else {
			printPassword(argv);
		}
	}

	private static void printUsage() {
		System.err.println("Usage: java -jar xkcd936cli.jar <text file>+");
		System.err.println("");
		System.err.println("This program will read in text files on the command line,");
		System.err.println("then pick words randomly to generate a password.");
		System.err.println("For more info, see http://xkcd.com/936/.");
	}

	private static void printPassword(String[] argv) throws IOException {
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
