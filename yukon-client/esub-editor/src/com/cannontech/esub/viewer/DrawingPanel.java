package com.cannontech.esub.viewer;

import java.net.URL;

import javax.swing.JPanel;


import com.loox.jloox.LxGraph;
import com.loox.jloox.LxView;

import com.cannontech.esub.util.ClientSession;

/**
 * Displays glx drawings with cti elements.
 *
 * Creation date: (1/4/2002 12:57:57 PM)
 * @author: Aaron Lauinger
 */
public class DrawingPanel extends JPanel {	
	
	// The jloox graph and view to use
	private LxGraph lxGraph;
	private LxView lxView;


	
/**
 * Viewer constructor comment.
 */
public DrawingPanel() {
	super();
	initialize(null);
}
/**
 * Viewer constructor comment.
 */
public DrawingPanel(URL drawing) {
	super();
	initialize(drawing);
}
/**
 * Creation date: (1/28/2002 9:40:22 AM)
 * @return com.loox.jloox.LxGraph
 */
protected LxGraph getLxGraph() {
	return lxGraph;
}
/**
 * Creation date: (1/28/2002 9:40:42 AM)
 * @return com.loox.jloox.LxView
 */
protected LxView getLxView() {
	return lxView;
}
/**
 * Creation date: (1/4/2002 12:59:53 PM)
 * @param drawing java.net.URL
 */
private void initialize(URL drawing) {
	lxGraph = new LxGraph();
	lxView = new LxView();
	
	lxView.setGraph(lxGraph);
	lxView.setEditMode(LxView.STANDARD_MODE);
	lxView.setAntialiasingActivated(true);	
	lxView.setBackground(java.awt.Color.black);
	
	if( drawing != null ) {
		loadDrawing(drawing);
	}

	setLayout(new java.awt.BorderLayout());
	add(lxView, java.awt.BorderLayout.CENTER);		
}
/**
 * Creation date: (1/23/2002 2:33:23 PM)
 * @param file java.lang.String
 */
protected void loadDrawing(String file) {
	ClientSession session = ClientSession.getInstance();

	try {
	if( session.isValid() ) {
		System.out.println("loading " + file);
		URL u = ClientSession.getInstance().createURL(file);
		loadDrawing(u);
	}
	}
	catch( java.net.MalformedURLException e) {
		e.printStackTrace();
	}
}
/**
 * Creation date: (1/4/2002 1:00:13 PM)
 * @param drawing java.net.URL
 */
protected void loadDrawing(URL drawing) {
	System.out.println("Loading url: " + drawing);
	lxGraph.removeAll();
	lxGraph.cleanUp();
	String error = lxGraph.read(drawing);

	if( error != null ) {
		javax.swing.JOptionPane.showMessageDialog(this, error, "Error", javax.swing.JOptionPane.ERROR_MESSAGE );	
	}
}
}
