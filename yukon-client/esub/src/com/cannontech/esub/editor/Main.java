package com.cannontech.esub.editor;

import javax.swing.JFrame;

import com.cannontech.common.util.CtiUtilities;

/**
 * work in progress
 * @author alauinger
 */
public class Main {

	private JFrame mainFrame;
	
	public static void main(String[] args) {
		CtiUtilities.setLaF();
		JFrame f = new JFrame();
		EditorDesktop editor = new EditorDesktop();
		f.setSize(800,600);
		f.setContentPane(editor);
		f.show();	
		
		
	
	}
}
