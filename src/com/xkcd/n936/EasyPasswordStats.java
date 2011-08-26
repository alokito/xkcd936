package com.xkcd.n936;


public class EasyPasswordStats {

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
