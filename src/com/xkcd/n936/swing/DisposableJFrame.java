package com.xkcd.n936.swing;

import java.awt.HeadlessException;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.KeyStroke;

@SuppressWarnings("serial")
public class DisposableJFrame extends JFrame {
	public DisposableJFrame() throws HeadlessException {
		super();
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		closeAction.putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(
			     KeyEvent.VK_W, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
		
		JMenu menu = new JMenu("File");
		menu.add(closeAction);
		JMenuBar bar = new JMenuBar();
		bar.add(menu);
		setJMenuBar(bar);
	}
	private final AbstractAction closeAction = new AbstractAction("Close") {
		@Override public void actionPerformed(ActionEvent e) {
			setVisible(false);
			dispose();
		}
	};
}
