package com.xkcd.n936.swing;

import java.awt.GridLayout;
import java.awt.HeadlessException;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.io.IOException;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.KeyStroke;


@SuppressWarnings("serial")
public class DisposableJFrame extends JFrame {
	public DisposableJFrame() throws HeadlessException {
		super();
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		closeAction.putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(
			     KeyEvent.VK_W, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
		aboutAction.putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(
			     KeyEvent.VK_A, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
		
		JMenu menu;
		JMenuBar bar = new JMenuBar();

		menu = new JMenu("File");
		menu.add(closeAction);
		bar.add(menu);

		menu = new JMenu("Help");
		menu.add(aboutAction);
		bar.add(menu);

		setJMenuBar(bar);
	}
	private final Action aboutAction = new AbstractAction("About") {
		
		@Override
		public void actionPerformed(ActionEvent actionEvent) {
				/*
				 * Popup popup = new Popup(TreeViewFrame.this,getAppName(), new
				 * String [] { "Java TreeView was created by Alok
				 * (alok@genome).", "It is an extensible, crossplatform port of
				 * Eisen's TreeView.", "Version: " +
				 * TreeViewApp.getVersionTag(), "Homepage:
				 * http://genetics.stanford.edu/~alok/TreeView/" });
				 */
				JPanel message = new JPanel();
				// message.setLayout(new BoxLayout(message, BoxLayout.Y_AXIS));
				message.setLayout(new GridLayout(0, 1));
				message.add(new JLabel("Easy Password was created by Alok (alokito@users.sourceforge.net)."));
				message.add(new JLabel("Version: "
						+ EasyPasswordApp.getVersionTag()));

				JPanel home = new JPanel();
				home.add(new JLabel("Wiki"));
				home.add(new JTextField(EasyPasswordApp.getWikiUrl()));
				JButton yesB = new JButton("Open");
				yesB.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent arg0) {
						try {
							BrowserControl.getBrowserControl().displayURL(EasyPasswordApp.getWikiUrl());
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}

				});
				home.add(yesB);
				message.add(home);


				JOptionPane.showMessageDialog(DisposableJFrame.this, message, 
						"About...", JOptionPane.INFORMATION_MESSAGE);
		}
	};
	
	private final AbstractAction closeAction = new AbstractAction("Close") {
		@Override public void actionPerformed(ActionEvent e) {
			setVisible(false);
			dispose();
		}
	};
}
