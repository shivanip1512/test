package com.cannontech.common.gui.util;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;

/**
 * A collection of dialog utility methods.
 * Similar to the JOptionPane showXXX methods but allows changing the buttons along the bottom.
 * 
 * @author alauinger
 */
public class DialogUtil {
	
	// Helper class that has a place to remember a JButton
	private static class UtilDialog extends JDialog {
			UtilDialog(Frame parent, String title) {
				super(parent,title,true);
			}
			JButton dismisser;				
		};
		
	/**
	 * Displays a modal dialog which is dismissed if any of the supplied buttons are clicked.
	 * Note this method does not add any buttons to any panels, this method assumes you've
	 * already created your own buttons.
	 * 
	 * @param parent	- the dialogs parent frame
	 * @param content	- the panel you want in the dialog
	 * @param title	- the title of the dialog
	 * @param buttons	- if any of these buttons are clicked the dialog will be dismissed
	 * @return JButton - returns the JButton clicked that dismissed the dialog
	 */
	public static JButton showDialog(Frame parent, JPanel content, String title, JButton[] buttons) {		
				
		final UtilDialog d = new UtilDialog(parent, title);
		for(int i = buttons.length-1; i >= 0; i-- ) {				 
			addActionListener(d,buttons[i]);
		}
		 		
		d.setContentPane(content);
		d.pack();
		d.setVisible(true);
		return d.dismisser;
	}
	
	/**
	 * Displays a modal dialog.
	 * Note this method adds suttons to the bottom of the dialog.
	 * @param parent	- the dialogs parent frame
	 * @param content	- the panel you want in the dialog
	 * @param title	- the title of the dialog
	 * @param buttons	- if any of these buttons are clicked the dialog will be dismissed
	 * @return String  - returns the text of the button which dismissed the dialog
	 */
	public static String showDialog(Frame parent, JPanel content, String title, String[] buttons) {
		final UtilDialog d = new UtilDialog(parent, title);
		final JPanel outerPanel = new JPanel(new BorderLayout());
		final JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
		
		for(int i = 0; i < buttons.length; i++ ) {
			JButton b = new JButton(buttons[i]);
			addActionListener(d,b);			
			buttonPanel.add(b);
		}
		
		outerPanel.add(content, BorderLayout.CENTER);
		outerPanel.add(buttonPanel, BorderLayout.SOUTH);
		
		d.setContentPane(outerPanel);
		d.pack();
		d.setVisible(true);
		return (d.dismisser == null) ? null : d.dismisser.getText();		
	}
	
	/**
	 * Helper method to dismiss dialogs and keep track of which
	 * button dismissed the dialog.
	 * @param d
	 * @param b
	 */
	private static final void addActionListener(final UtilDialog d, JButton b) {
		b.addActionListener( new ActionListener() {			 
			public void actionPerformed(ActionEvent e) {
				d.dismisser = (JButton) e.getSource();
				d.setVisible(false);
				d.dispose();					
			}
		});		
	}
	
	// no instances please!
	private DialogUtil() {
	}
	
}
