package com.cannontech.esub.editor;

import javax.swing.JDesktopPane;
import javax.swing.JInternalFrame;

import com.cannontech.graph.GraphClient;

/**
 * @author alauinger
 */
public class EditorDesktop extends JDesktopPane {
	
	public EditorDesktop() {
		initialize();
	}
	
	private void initialize() {				
		Editor e = new Editor();
		JInternalFrame iframe = new JInternalFrame();
		iframe.setSize(400,300);
		iframe.setResizable(true);
		iframe.getContentPane().add(e);
		iframe.setVisible(true);
		add(iframe);
		
		Editor e2 = new Editor();
		JInternalFrame iframe2 = new JInternalFrame();
		iframe2.setSize(400,300);
		iframe2.setResizable(true);
		iframe2.getContentPane().add(e2);
		iframe2.setVisible(true);
		add(iframe2);
		
		GraphClient gc = new GraphClient();
		JInternalFrame iframe3 = new JInternalFrame();
		iframe3.setSize(400,300);
		iframe3.setResizable(true);
		iframe3.getContentPane().add(gc);
		iframe3.setVisible(true);
		add(iframe3);
	}
}
