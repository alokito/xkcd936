package com.xkcd.n936.swing;

import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLDecoder;
import java.util.Date;
import java.util.StringTokenizer;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import com.xkcd.n936.lib.EasyPassword;
import com.xkcd.n936.lib.EasyPasswordStats;


public class EasyPasswordApp {
	final private FileTableModel fileTable;
	final private EasyPassword easyPassword = new EasyPassword();
	final private JTextField passwordLabel = new JTextField();
	final private JTextArea statsLabel = new JTextArea();
	final private String KJV_BIBLE_URL = "http://www.gutenberg.org/ebooks/10.txt.utf8";
	final private String WAR_AND_PEACE_URL = "http://www.gutenberg.org/ebooks/2600.txt.utf8";

	private EasyPasswordApp() {
		fileTable = new FileTableModel();
		for (File file:scanForText()) {
			fileTable.addFile(file);
		}
		passwordLabel.setEditable(false);
		Box configurationPanel = createConfigurationWidgets();
		
		JPanel resultsPanel = new JPanel();
		resultsPanel.setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		c.gridx=0;c.gridy =0; c.gridwidth =2 ;
		JButton gobutton = new JButton("Make New Password!");
		gobutton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				makeNewPassword();
			}

		});
		
		resultsPanel.add(gobutton,c);
		c.gridwidth=1;
		c.gridx=0;c.gridy =1;
		resultsPanel.add(new JLabel("Password"),c);
		c.gridx=1;c.gridy =1;
		resultsPanel.add(passwordLabel,c);
		c.gridx=0;c.gridy =2;
		resultsPanel.add(new JLabel("Stats"),c);
		c.gridx=1;c.gridy =2;
		resultsPanel.add(statsLabel,c);
		
		JFrame frame = new JFrame();
		frame.getContentPane().setLayout(new BorderLayout());
		frame.add(configurationPanel, BorderLayout.CENTER);
		frame.add(resultsPanel, BorderLayout.SOUTH);
		makeNewPassword();
		frame.pack();
		frame.setVisible(true);
	}
	private void makeNewPassword() {
		for (File file : fileTable.getSelectedFiles()) {
			try {
				easyPassword.addWords(file);
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}

		
		String password = easyPassword.makePassword();
		double bits = EasyPasswordStats.calcBits(easyPassword);
		double hexlen = EasyPasswordStats.calcHexLen(easyPassword);
		double alphanum = EasyPasswordStats.calcAlphaNumLen(easyPassword);
		double randstrlen = EasyPasswordStats.calcRandLen(easyPassword);
		int dictSize = easyPassword.getDictSize();

		String stats = 
			"constructed by picking "+easyPassword.getNumWords()+" from dictsize " + dictSize+"\n" +
			"bits: " + bits +"\n"+
		"equivalent to hex string of length: " + hexlen +"\n" +
		"equivalent to alphanum string of length: " + alphanum +"\n" +
		"equivalent to random string of length: " + randstrlen;
		passwordLabel.setText(password);
		statsLabel.setText(stats);
		easyPassword.clearDict();
		
	}
	
	private Box getFileButtons() {
		Box retBox = new Box(BoxLayout.X_AXIS);
		retBox.add(new JLabel("Download Files:"));
		addFileButton(retBox,"KJV Bible", KJV_BIBLE_URL, "kjv_bible.txt");
		addFileButton(retBox,"War and Peace", WAR_AND_PEACE_URL, "war_and_peace.txt");
		return retBox;
	}
	private void addFileButton(Box retBox, String label, final String durl, final String file) {
		JButton kvjBible = new JButton(label);
		kvjBible.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				String target = getDictDir() + File.separator + file;
				downloadUrl(durl, target);
				fileTable.addFile(new File(target));
			}
		});
		retBox.add(kvjBible);
	}
	private void downloadUrl(String urlString, String target) {
		try
	      {
	          URL           url  = new URL(urlString);
	          System.out.println("Opening connection to " + urlString + "...");
	          URLConnection urlC = url.openConnection();
	          // Copy resource to local file, use remote file
	          // if no local file name specified
	          InputStream is = url.openStream();
	          // Print info about resource
	          System.out.print("Copying resource (type: " + urlC.getContentType());
	          Date date=new Date(urlC.getLastModified());
	          System.out.println(", modified on: " + date.toLocaleString() + ")...");
	          System.out.flush();
	          FileOutputStream fos = new FileOutputStream(target);
	          byte[] data = new byte[1024];
	          int x=0;
	          int count =0;
	          while((x=is.read(data,0,1024))>=0)
	          {
	        	  fos.write(data,0,x);
	        	  count += x;
	          }
	          is.close();
	          fos.close();
	          System.out.println(count + " byte(s) copied");
	      }
	      catch (MalformedURLException e)
	      { System.err.println(e.toString()); }
	      catch (IOException e)
	      { System.err.println(e.toString()); }
	}
	private Box createConfigurationWidgets() {
		JTable jTable = new JTable(fileTable);
		
		jTable.setFillsViewportHeight(true);
		JPanel filePanel = new JPanel();
		filePanel.setLayout(new BorderLayout());
		filePanel.add(new JLabel("Files (in "+getDictDir()+")"), BorderLayout.NORTH);
		filePanel.add(jTable, BorderLayout.CENTER);
		filePanel.add(getFileButtons(), BorderLayout.SOUTH);
		JPanel optionsPanel = new JPanel();
		optionsPanel.setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		c.gridx=0;c.gridy =0;
		final JCheckBox lowercaseBox = new JCheckBox("Lowercase all words");
		lowercaseBox.setSelected(true);
		lowercaseBox.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent e) {
				easyPassword.setLowercase(lowercaseBox.isSelected());
			}
		});
		c.gridwidth =2;
		optionsPanel.add(lowercaseBox,c);
		c.gridy = 1;
		final JCheckBox spaceBox = new JCheckBox("Space between words");
		spaceBox.setSelected(true);
		spaceBox.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent e) {
				easyPassword.setSpaceOutPassword(spaceBox.isSelected());
			}
		});
		optionsPanel.add(spaceBox,c);
		c.gridwidth =1;
		c.gridy = 2;
		c.gridx = 0;
		final JTextField numField = new JTextField("4");
		numField.addKeyListener(new KeyListener() {
			
			@Override
			public void keyTyped(KeyEvent e) {
			}
			
			@Override
			public void keyReleased(KeyEvent e) {
				try {
					int newNum = Integer.parseInt(numField.getText());
					easyPassword.setNumWords(newNum);
				} catch (Exception ex) {
					System.err.println("Failed to parse" + numField.getText());
				}
			}
			
			@Override
			public void keyPressed(KeyEvent arg0) {
			}
		});
		optionsPanel.add(numField,c);
		c.gridx = 1;
		optionsPanel.add(new JLabel("Number of words in password"),c);
		
		Box configurationPanel = new Box(BoxLayout.X_AXIS);
		configurationPanel.add(filePanel);
		configurationPanel.add(optionsPanel);
		return configurationPanel;
	}
	public static final void main(String []argv){
		EasyPasswordApp app = new EasyPasswordApp();
	}
	private File[] scanForText() {
		String dir = getDictDir();
		File f_dir = new File(dir);
		File[] list =f_dir.listFiles();
		return (list == null)?new File[0]:list;
	}
	private String getDictDir() {
		URL fileURL = getCodeBase();
		return URLtoFilePath(fileURL.getPath()+"/dict");
	}
	private URL codeBase = null;
	/**
	 * sometimes the location of the jar is not the location where
	 * the plugins and coordiates can be found. This is particularly
	 * the case with mac os X.I have added detection code in 
	 * scanForPlugins that detects this and updates the codebase so 
	 * that the coordinates settings will be done correctly.
	 */
	public URL getCodeBase() {
		if (codeBase != null) {
			return codeBase;
		}
		try {

			// from http://weblogs.java.net/blog/ljnelson/archive/2004/09/cheap_hack_i_re.html
			URL location;
			String classLocation = EasyPasswordApp.class.getName().replace('.', '/') + ".class";
			ClassLoader loader = EasyPasswordApp.class.getClassLoader();
			if (loader == null) {
				location = ClassLoader.getSystemResource(classLocation);
			} else {
				location = loader.getResource(classLocation);
			}
			String token = null;
			if (location != null && "jar".equals(location.getProtocol())) {
				String urlString = location.toString();
				if (urlString != null) {
					final int lastBangIndex = urlString.lastIndexOf("!");
					if (lastBangIndex >= 0) {
						urlString = urlString.substring("jar:".length(), lastBangIndex);
						if (urlString != null) {
							int lastSlashIndex = urlString.lastIndexOf("/");
							if (lastSlashIndex >= 0) {
								token = urlString.substring(0, lastSlashIndex);
							}
						}
					}
				}
			}
			if (token == null) {
				return (new File(".")).toURI().toURL();
			} else {
				return new URL(token);
			}
		} catch (MalformedURLException e) {
			e.printStackTrace();
			JOptionPane.showMessageDialog(null, e);
			return null;
		}
	}

	private static String URLtoFilePath(String fileURL) {
		String dir = null;
		try {
			dir = URLDecoder.decode(fileURL, "UTF-8");
			dir = dir.replace('/', File.separatorChar);
		} catch (UnsupportedEncodingException e) {
			// this should really never be called.
			e.printStackTrace();
		}
		return dir;
	}
}
