package com.xkcd.n936.swing;

import java.awt.Frame;
import java.awt.TextArea;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;

/**
*  Abstract class to allow platform-independant control of an external web browser
*
* @author     Alok Saldanha <aloksaldanha@gmail.com>
*/
public abstract class BrowserControl {
	// adapted from http://www.javaworld.com/javaworld/javatips/jw-javatip66.html
	// shit, you would think they would object orient a bit more...
	/**
	 *  Method to display a url
	 *
	 * @param  url              String representing url
	 * @exception  IOException  Exceptions throw if display fails.
	 */
	public abstract void displayURL(String url) throws IOException;



	/** Used to identify the windows platform. */
	private final static String WIN_ID  = "Windows";

	/** Used to identify the mac platform. */
	private final static String MAC_ID  = "Mac";

	/**
	 *  Pops up a window with the html source of a url.
	 *  Primarily for debugging.
	 * 
	 * @param  url  url to show.
	 */
	public final static void showText(java.net.URL url) {
		try {
			Reader st        = new InputStreamReader(url.openStream());
			int ch;
			TextArea mp      = new TextArea();
			ch = st.read();
			while (ch != -1) {
				char[] cbuf  = new char[1];
				cbuf[0] = (char) ch;
				mp.append(new String(cbuf));
				ch = st.read();
			}
			final Frame top  = new Frame("Show URL");
			top.addWindowListener(
				new WindowAdapter() {

					public void windowClosing(WindowEvent windowEvent) {
						top.dispose();
					}
				});

			top.add(mp);
			top.pack();
			top.setVisible(true);

		} catch (java.io.IOException e) {
		}
	}

	/**
	 *  Simple example, causes a browser window pop up.
	 *
	 * @param  args  no arguments required.
	 */

	public static void main(String[] args) {
		try {
			BrowserControl bc  = getBrowserControl();
			bc.displayURL("http://www.javaworld.com");
		} catch (IOException x) {
			// couldn't exec browser
			System.err.println("Could not invoke browser, Caught: " + x);
		}
	}

	public boolean isValidUrl(String urlString) {
		try {
			@SuppressWarnings("unused")
			URL url = new URL(urlString);
			// url.openConnection()... can test to see if live (throws IOException)
			return true;
		} catch (java.net.MalformedURLException e) {
			return false;
		}
	}

	/**
	 *  Generates an appropriate <code>BrowserControl</code> for the current platform.
	 *
	 * @return    A new <code>BrowserControl</code>
	 */
	public static BrowserControl getBrowserControl() {
		// christ, need to detect os type...
		String os  = System.getProperty("os.name");

		if (os == null) {
			return new UnixBrowserControl();
		}

		
		if (os.startsWith(WIN_ID)) {
			if (os.indexOf("NT") > 0) {
				return new WinNTBrowserControl();
			} else {
				return new Win32BrowserControl();
			}
		}

		if (os.startsWith(MAC_ID)) {
			return new MacBrowserControl();
		}
		// default to unix style?
		return new UnixBrowserControl();
	}

}

/**
*  Win32 browser control subclass for windows
*
* @author     Alok Saldanha <alok@genome.stanford.edu>
* @version    @version $Revision: 1.15 $ $Date: 2008-03-09 21:06:34 $
*/
class Win32BrowserControl extends BrowserControl {


	/**
	 *  Display a file in the system browser. If you want to display a file, you must
	 *  include the absolute path name.
	 *
	 * @param  url              the file's url (the url must start with either "http://"
	 *      or "file://").
	 * @exception  IOException  Not thrown by me
	 */

	public void displayURL(String url) throws IOException {
		//  This one was my original
		// String cmd  = WIN_PATH + " " + WIN_FLAG + " " + url;
		// Matt suggested this
		//	String cmd = "start " + url;
		// NT requires this		
		String cmd  = "cmd /c start " + url;
		// The problem with the above is that special shell characters, notably & | ( ) < > ^ , 
		// need to be escape, or put in double quotes (which can be doubled to escape them, 
		// but I don't think that's necessary.)
		// the problem with the below is that it doesn't work.
		//String cmd  = "cmd /c start \"" + url +"\"";
		// Long term, the solution is probably to URI escape the string, although it's too late at this stage since we don't know what part is the search string.
		if (isValidUrl(url) == false) {
			cmd = url;
		}
		try {
			Runtime.getRuntime().exec(cmd );
		} catch (Exception e) {
			cmd = "start " + url;
			Runtime.getRuntime().exec(cmd);
		}
	}



	/** The default system browser under windows. */
//	private final static String WIN_PATH  = "rundll32";

	/** The flag to display a url.*/
//	private final static String WIN_FLAG  = "url.dll,FileProtocolHandler";

}

/**
*  Win32 browser control subclass for windows
*
* @author     Alok Saldanha <alok@genome.stanford.edu>
* @version    @version $Revision: 1.15 $ $Date: 2008-03-09 21:06:34 $
*/
class WinNTBrowserControl extends BrowserControl {

	private String WinEscape(String url) {
		String cons = "&|()<>^,\"\\";
		StringBuffer buf = new StringBuffer();
		char [] inChars = url.toCharArray();
		for (int i = 0; i < inChars.length; i++) {
			if (cons.indexOf(inChars[i]) >= 0) {
				buf.append("\\");
			}
			buf.append(inChars[i]);
		}
		return buf.toString();
	}
	/**
	 *  Display a file in the system browser. If you want to display a file, you must
	 *  include the absolute path name.
	 *
	 * @param  url              the file's url (the url must start with either "http://"
	 *      or "file://").
	 * @exception  IOException  Not thrown by me
	 */

	public void displayURL(String url) throws IOException {
		//  This one was my original
		// String cmd  = WIN_PATH + " " + WIN_FLAG + " " + url;
		// Matt suggested this
		//	String cmd = "start " + url;
		// The problem with the above is that special shell characters, notably & | ( ) < > ^ , 
		// need to be escape, or put in double quotes (which can be doubled to escape them, 
		// but I don't think that's necessary.)
		
		// NT is confused by quotes...
		 String cmd  = "cmd /c start " + WinEscape(url);

		 if (isValidUrl(url) == false) {
			cmd = url;
		 }
		 Runtime.getRuntime().exec(cmd);
	}
}


/**
*  Subclass for unix
*
* @author     Alok Saldanha <alok@genome.stanford.edu>
* @version    @version $Revision: 1.15 $ $Date: 2008-03-09 21:06:34 $
*/
class UnixBrowserControl extends BrowserControl {


	/**
	 *  Display a file in netscape. If you want to display a file, you must include
	 *  the absolute path name.
	 *
	 * @param  url              the file's url (the url must start with either "http://"
	 *      or "file://").
	 * @exception  IOException  not thrown by me
	 */

	public void displayURL(String url) throws IOException {
		String cmd  = UNIX_PATH + " " + UNIX_FLAG + "(" + url + ")";
		if (isValidUrl(url) == false) {
			cmd = url;
		}

		try {
			// Under Unix, Netscape has to be running for the "-remote"
			// command to work.  So, we try sending the command and
			// check for an exit value.  If the exit command is 0,
			// it worked, otherwise we need to start the browser.

			// cmd = 'netscape -remote openURL(http://www.javaworld.com)'
			Process p     = Runtime.getRuntime().exec(cmd);

			// wait for exit code -- if it's 0, command worked,
			// otherwise we need to start the browser up.
			int exitCode  = p.waitFor();

			if (exitCode != 0) {
				// Command failed, start up the browser

				//cmd = "netscape \"http://www.javaworld.com\""
				cmd = UNIX_PATH + " \"" + url + "\"";
				System.out.println("Opening new netscape on unix can be flaky, paste this into a terminal if nothing happens... ");
				System.out.println(cmd);
				p = Runtime.getRuntime().exec(cmd);
			}
		} catch (InterruptedException x) {
			System.err.println("Error bringing up browser, cmd='" +
					cmd + "'");
			System.err.println("Caught: " + x);
			x.printStackTrace();
		}
	}



	/** The default browser under unix.*/
	private final static String UNIX_PATH  = "netscape";

	/** The flag to display a url.*/
	private final static String UNIX_FLAG  = "-remote openURL";
}

//I wonder if MacBrowserControl will use the right MRJ?

/**
*  Subclass for mac
*
* @author     Alok Saldanha <alok@genome.stanford.edu>
* @version    @version $Revision: 1.15 $ $Date: 2008-03-09 21:06:34 $
*/
class MacBrowserControl extends BrowserControl {


	/**
	 *  Display a file in the system browser. If you want to display a file, you must
	 *  include the absolute path name.
	 *
	 * @param  url              the file's url (the url must start with either "http://"
	 *      or "file://").
	 * @exception  IOException  not thrown by me.
	 */

	public void displayURL(String url) throws IOException {
//		System.out.println("Mac browser dislaying url " + url);
		try {
		Class fileMgr = Class.forName("com.apple.eio.FileManager");
        Method openURL;
			openURL = fileMgr.getDeclaredMethod("openURL",
			   new Class[] {String.class});
	        openURL.invoke(null, new Object[] {url});
		} catch (SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        
	}

}

