package com.xkcd.n936.swing;

import java.awt.HeadlessException;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

import javax.swing.AbstractAction;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.KeyStroke;

@SuppressWarnings("serial")
public class DisposableJFrame extends JFrame {
	public DisposableJFrame() throws HeadlessException {
		super();
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
	}
	private final AbstractAction closeAction = new AbstractAction("Close Window") {
		@Override public void actionPerformed(ActionEvent e) {
			setVisible(false);
			dispose();
		}
	};
	private final KeyStroke closeKey = KeyStroke.getKeyStroke(
				KeyEvent.VK_W, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask());
	public void attachCloseShortcut(JPanel mainPanel) {
		mainPanel.getInputMap().put(closeKey, "closeWindow");        
		mainPanel.getActionMap().put("closeWindow", closeAction);
	}
}
