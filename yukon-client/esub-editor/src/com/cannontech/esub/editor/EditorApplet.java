package com.cannontech.esub.editor;

import javax.swing.*;
/**
 * Creation date: (12/13/2001 4:40:09 PM)
 * @author: 
 */
public class EditorApplet extends JApplet {
	private static final java.awt.Dimension defaultSize = new java.awt.Dimension(800,600);
/**
 * EditorApplet constructor comment.
 */
public EditorApplet() {
	super();
}
	 public void init() {
		  try {
	javax.swing.UIManager.setLookAndFeel(javax.swing.UIManager.getSystemLookAndFeelClassName());
	}
	catch(Exception e)
	{ e.printStackTrace(); /*Not much to do about*/ }

	
		SwingUtilities.invokeLater (new Runnable() {
			public void run() {
				
					Editor editor = new Editor();

	JScrollPane scrollPane = new JScrollPane();
	scrollPane.setViewportView(editor);
	
	getContentPane().add(scrollPane);

	setSize(defaultSize);
	editor.setPreferredSize(defaultSize);
	
	
	
	
				
			}
		});
    }

}
