package com.xkcd.n936.swing;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLDecoder;
import java.util.Dictionary;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import com.xkcd.n936.lib.EasyPassword;
import com.xkcd.n936.lib.EasyPasswordStats;


public class EasyPasswordApp  implements WindowListener {
	final private FileTableModel fileTable;
	final private EasyPassword easyPassword = new EasyPassword();
	final private JTextField passwordLabel = new JTextField();
	final private JTextArea statsLabel = new JTextArea();
	final private String KJV_BIBLE_URL = "http://www.gutenberg.org/ebooks/10.txt.utf8";
	final private String WAR_AND_PEACE_URL = "http://www.gutenberg.org/ebooks/2600.txt.utf8";
	final private DisposableJFrame frame;
	private EasyPasswordApp() {
		fileTable = new FileTableModel();
		scanDir();
		passwordLabel.setEditable(false);
		Box configurationPanel = createConfigurationWidgets();
		
		JPanel resultsPanelOuter = createResultsWidgets();
		
		final JPanel introPanel = new JPanel();
		introPanel.add(new JLabel("Generate easy to remember, hard to guess passwords. Based on"));
		JTextField addrField = new JTextField("http://xkcd.com/936/");
		addrField.setEditable(false);
		introPanel.add(addrField);
		JButton showBtn = new JButton("Show in Browser");
		showBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					BrowserControl.getBrowserControl().displayURL("http://xkcd.com/936/");
				} catch (IOException e1) {
					JOptionPane.showMessageDialog(introPanel, e1);
				}
			}
		});
		introPanel.setBackground(Color.orange);
		introPanel.add(showBtn);
		
		JPanel padding = new JPanel();
		padding.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));
		BorderLayout frameLayout = new BorderLayout();
		frameLayout.setVgap(10);
		padding.setLayout(frameLayout);
		padding.add(introPanel,BorderLayout.NORTH);
		padding.add(configurationPanel, BorderLayout.CENTER);
		padding.add(resultsPanelOuter, BorderLayout.SOUTH);
		frame = new DisposableJFrame();
		frame.setTitle("Easy Password Generator");
		frame.getContentPane().add(padding);
		makeNewPassword();
		frame.pack();
		frame.addWindowListener(this);
		frame.setVisible(true);
		
	}
	private void scanDir() {
		fileTable.removeAll();
		for (File file:scanForText()) {
			fileTable.addFile(file);
		}
	}
	private JPanel createResultsWidgets() {
		JPanel resultsPanelOuter = new JPanel();
		BorderLayout borderLayout = new BorderLayout();
		resultsPanelOuter.setLayout(borderLayout);
		
		final JPanel buttonP = new JPanel();
		final JButton gobutton = new JButton("Make New Password!");
		gobutton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (fileTable.getNumFiles() == 0) {
					JTextArea msgArea = new JTextArea("The password generator picks words from text files.\n"+
							"Please place text files in " + getDictDir() +"\n"+
					"Or click one of the 'Download Files:' buttons to get some text files");
					msgArea.setEditable(false);
					msgArea.setBackground(buttonP.getBackground());
					JOptionPane.showMessageDialog(gobutton, msgArea);
				} else if (fileTable.getSelectedFiles().size() == 0) {
					JOptionPane.showMessageDialog(gobutton, "Please select at least one file to generate passwords");
				} else {
					makeNewPassword();
				}
			}

		});
		buttonP.add(gobutton);
		resultsPanelOuter.add(buttonP, BorderLayout.NORTH);
		JPanel resultsPanel = new JPanel();
		TitledBorder title;
		title = BorderFactory.createTitledBorder("Results");		
		resultsPanel.setBorder(title);
		resultsPanel.setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		c.gridwidth=1;
		c.gridx=0;c.gridy =0;
		resultsPanel.add(new JLabel("Password"),c);
		c.gridx=1;c.gridy =0;
		resultsPanel.add(passwordLabel,c);
		c.gridx=0;c.gridy =1;
		resultsPanel.add(new JLabel("Stats"),c);
		c.gridx=1;c.gridy =1;
		resultsPanel.add(statsLabel,c);
		resultsPanelOuter.add(resultsPanel,BorderLayout.CENTER);
		return resultsPanelOuter;
	}
	private void makeNewPassword() {
		if	(easyPassword.getDictSize() == 0) {
			for (File file : fileTable.getSelectedFiles()) {
				try {
					easyPassword.addWords(file);
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
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
	}
	
	private Box getFileButtons() {
		Box retBox = new Box(BoxLayout.X_AXIS);
		retBox.add(new JLabel("Download Files:"));
		addFileButton(retBox,"KJV Bible", KJV_BIBLE_URL, "kjv_bible.txt");
		addFileButton(retBox,"War and Peace", WAR_AND_PEACE_URL, "war_and_peace.txt");
		return retBox;
	}
	private void addFileButton(Box retBox, final String label, final String durl, final String file) {
		final JButton kvjBible = new JButton(label);
		kvjBible.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				String dictDir  = getDictDir();
				File dictFile = new File(dictDir);
				if (dictFile.exists() || new File(dictDir).mkdirs()) {
					final String target = dictDir + File.separator + file;
					kvjBible.setText("Downloading...");
					kvjBible.setEnabled(false);
					
					SwingUtilities.invokeLater(new Runnable() {
						@Override
						public void run() {
							if (downloadUrl(durl, target)) {
								scanDir();
								easyPassword.clearDict();
							}
							kvjBible.setText(label);
							kvjBible.setEnabled(true);
						}
					});
				} else {
					JOptionPane.showMessageDialog(null,"Error: Could not create output dir " + dictDir);
				}
			}
		});
		retBox.add(kvjBible);
	}
	private boolean downloadUrl(final String urlString, final String target) {
		doDownload(urlString,target);
	    if (!downloadSuccessful)
	    	JOptionPane.showMessageDialog(frame, "Download failed!\n" + downloadException.toString());
	    return downloadSuccessful;
	}
	private boolean downloadSuccessful;
	private Exception downloadException;
	private boolean doDownload(String urlString, String target) {
		downloadSuccessful = false;
		try {
			URL           url  = new URL(urlString);
			System.out.println("Opening connection to " + urlString + "...");
			URLConnection urlC = url.openConnection();
			// Copy resource to local file, use remote file
			// if no local file name specified
			InputStream is = url.openStream();
			// Print info about resource
			System.out.print("Copying resource (type: " + urlC.getContentType());
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
			downloadSuccessful = true;
		} catch (Exception e) {
			downloadException = e;
		}
		return downloadSuccessful;
	}
	
	
	private Box createConfigurationWidgets() {
		JTable jTable = new JTable(fileTable);
		jTable.getColumnModel().getColumn(0).setPreferredWidth(10);
		jTable.getColumnModel().getColumn(1).setPreferredWidth(200);
		jTable.setFillsViewportHeight(true);
		fileTable.addFileTableSelectionListener(new FileTableSelectionListenerI() {
			@Override
			public void selectionChanged() {
				easyPassword.clearDict();
			}
		});
		JPanel filePanel = new JPanel();
		filePanel.setBorder(BorderFactory.createTitledBorder("Files"));
		filePanel.setLayout(new BorderLayout());
		JPanel loadedPanel = new JPanel();
		loadedPanel.add(new JLabel("Loaded from "));
		JTextField textField =new JTextField(getDictDir());
		textField.setEditable(false);
		loadedPanel.add(textField);
		JButton rescanButton = new JButton("Scan");
		rescanButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				scanDir();
				easyPassword.clearDict();
			}
		});
		loadedPanel.add(rescanButton);
		filePanel.add(loadedPanel, BorderLayout.NORTH);
		JScrollPane scrollpane = new JScrollPane(jTable);
		scrollpane.setPreferredSize(new Dimension(0, 100));
		filePanel.add(scrollpane, BorderLayout.CENTER);
		filePanel.add(getFileButtons(), BorderLayout.SOUTH);
		JPanel optionsPanel = new JPanel();
		optionsPanel.setBorder(BorderFactory.createTitledBorder("Options"));

		optionsPanel.setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		c.gridx=0;c.gridy =0;
		final JCheckBox lowercaseBox = new JCheckBox("Lowercase all words");
		lowercaseBox.setSelected(true);
		lowercaseBox.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent e) {
				easyPassword.clearDict();
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
		// this will also open the window.
		new EasyPasswordApp();
	}
	private File[] scanForText() {
		String dir = getDictDir();
		File f_dir = new File(dir);
		File[] list =f_dir.listFiles();
		return (list == null)?new File[0]:list;
	}
	private String getDictDir() {
		URL fileURL = getCodeBase();
		String path = fileURL.getPath();
		if (path.endsWith("/EasyPassword.app/Contents/Resources/Java"))
			path = path.substring(0, path.length() - 
					"/EasyPassword.app/Contents/Resources/Java".length());
		return URLtoFilePath(path+"/dict");
	}
	private URL codeBase = null;
	/**
	 * sometimes the location of the jar is not the location where
	 * the plugins and coordiates can be found. This is particularly
	 * the case with mac os X.I have added detection code in 
	 * getDictDir that detects this and updates the codebase so 
	 * that the dict will be fetched correctly.
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
	@Override
	public void windowActivated(WindowEvent e) {
		//nothing
	}


	@Override
	public void windowClosing(WindowEvent e) {
		//nothing
	}


	@Override
	public void windowDeactivated(WindowEvent e) {
		//nothing
	}


	@Override
	public void windowDeiconified(WindowEvent e) {
		//nothing
	}


	@Override
	public void windowIconified(WindowEvent e) {
		//nothing
	}
	@Override
	public void windowOpened(WindowEvent e) {
		//nothing
	}
	@Override
	public void windowClosed(WindowEvent e) {
		System.exit(0);
	}
	public static String getWikiUrl() {
		return "https://github.com/alokito/xkcd936/wiki";
	}
	public static String getVersionTag() {
		return "0.1";
	}
}
