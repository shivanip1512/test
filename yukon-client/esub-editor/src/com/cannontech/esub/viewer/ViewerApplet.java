package com.cannontech.esub.viewer;

import javax.swing.JApplet;

/**
 * Esubstation viewer applet.
 * Views .glx files with cti jloox elements.
 * 
 * Creation date: (1/4/2002 11:49:07 AM)
 * @author: Aaron Lauinger
 */
public class ViewerApplet extends JApplet {
	private static final String DEFAULT_IMAGE_BASE = "/images";

	private ViewerPanel viewerPanel;
/**
 * Viewer constructor comment.
 */
public ViewerApplet() {
	super();
}
/**
 * Creation date: (1/4/2002 11:53:19 AM)
 */
public void init() {
	try {
		javax.swing.UIManager.setLookAndFeel(javax.swing.UIManager.getSystemLookAndFeelClassName());
	}
	catch(Exception e)
	{ e.printStackTrace(); /*Not much to do about*/ }

	String imageBase = getParameter("imagebase");
	if( imageBase == null )
		imageBase = DEFAULT_IMAGE_BASE;
	
	// Init the image cache so it can retrieve our images
	com.cannontech.esub.util.ImageCache ic = com.cannontech.esub.util.ImageCache.getInstance();
	ic.setImageHost(getCodeBase().getHost());
	ic.setImagePort(getCodeBase().getPort());	
	ic.setImageDir(imageBase);
	
	viewerPanel = new ViewerPanel();
	viewerPanel.setHost(getCodeBase().getHost());
	viewerPanel.setPort(getCodeBase().getPort());
	
	viewerPanel.setPreferredSize(new java.awt.Dimension(getWidth(), getHeight()));	
	getContentPane().add(viewerPanel);
}
/**
 * Creation date: (1/4/2002 11:53:56 AM)
 */
public void start() {
	System.out.println("Applet::start");
	viewerPanel.showLogin();
}
/**
 * Creation date: (1/4/2002 11:54:01 AM)
 */
public void stop() {
	System.out.println("Applet::stop");
	viewerPanel.stopUpdates();
}
}
