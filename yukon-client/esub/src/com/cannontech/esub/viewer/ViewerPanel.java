package com.cannontech.esub.viewer;

import java.awt.event.MouseListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseAdapter;

import java.awt.CardLayout;

import javax.swing.Timer;

import java.util.*;

import com.cannontech.esub.editor.element.LinkedElement;
import com.cannontech.esub.util.ClientSession;

/**
 * Creation date: (1/4/2002 2:17:05 PM)
 * @author: 
 */
public class ViewerPanel extends javax.swing.JPanel implements LoginPanelListener {

 	private final MouseListener viewMouseListener = new MouseAdapter() {
		public void mousePressed(MouseEvent evt) {
			System.out.println("view mouselistener");
			com.loox.jloox.LxComponent c = drawingPanel.getLxView().getLxComponentAt( evt.getX(), evt.getY() );

			if( c != null && c instanceof LinkedElement) {
				String link = ((LinkedElement) c).getLinkTo().trim();
				System.out.println("Link: " + link + " len: " + link.length());
				if( link != null && link.length() > 0 )
					showDrawing(link);					
			}
		}		
	};
	
	private static final long DEFAULT_UPDATE_INTERVAL = 10L;

	private Timer updateTimer;
	private long updateInterval = DEFAULT_UPDATE_INTERVAL;
		
	// Constants for use with card layout
	private static final String LOGIN_PANEL = "Login Panel";
	private static final String DRAWING_PANEL = "Drawing Panel";
	
	private String host;
	private int port;
	
	private ClientSession session = null;
	private CardLayout cardLayout;
	private LoginPanel loginPanel;
	private DrawingPanel drawingPanel;
/**
 * Viewer constructor comment.
 */
public ViewerPanel() {
	super();
	initialize();
}
/**
 * Creation date: (1/4/2002 3:12:56 PM)
 * @return java.lang.String
 */
public java.lang.String getHost() {
	return host;
}
/**
 * Creation date: (1/4/2002 3:12:56 PM)
 * @return int
 */
public int getPort() {
	return port;
}
/**
 * Creation date: (1/28/2002 10:05:48 AM)
 * @return long
 */
public long getUpdateInterval() {
	return updateTimer.getDelay();
}
/**
 * Creation date: (1/4/2002 2:26:18 PM)
 */
private void initialize() {
	cardLayout = new CardLayout();
	loginPanel = new LoginPanel();
	loginPanel.addLoginPanelListener(this);

	drawingPanel = new DrawingPanel();
	drawingPanel.getLxView().addMouseListener(viewMouseListener);
		
	/*try {
	drawingPanel = new DrawingPanel(new java.net.URL("http://cti.esubstation.com/esub-viewer/test_drawing.jlx"));

	} catch(Exception e ) { e.printStackTrace(); }*/
	
	setLayout(cardLayout);

	add(loginPanel, LOGIN_PANEL);
	add(drawingPanel, DRAWING_PANEL);

	DrawingUpdate du = new DrawingUpdate();
	du.setLxGraph(drawingPanel.getLxGraph());
	
	updateTimer = new Timer((int) updateInterval, du);	
		
	showLogin();	
}
/**
 * Creation date: (1/7/2002 11:43:11 AM)
 * @param args java.lang.String[]
 */
public static void main(String[] args) {
	try {
		javax.swing.UIManager.setLookAndFeel(javax.swing.UIManager.getSystemLookAndFeelClassName());
	}
	catch(Exception e)
	{ e.printStackTrace(); /*Not much to do about*/ }

	// Init the image cache so it can retrieve our images
	com.cannontech.esub.util.ImageCache ic = com.cannontech.esub.util.ImageCache.getInstance();
	ic.setImageHost("cti.esubstation.com");
	ic.setImagePort(80);
	ic.setImageDir("/images");
	
	ViewerPanel v = new ViewerPanel();
	v.setHost("cti.esubstation.com");
	v.setPort(80);
	
	v.setPreferredSize(new java.awt.Dimension(1000,600));

	javax.swing.JFrame f = new javax.swing.JFrame();
	f.setSize(800,600);
	f.getContentPane().add(v);
	f.show();
}
/**
 * Creation date: (1/4/2002 3:12:56 PM)
 * @param newHost java.lang.String
 */
public void setHost(java.lang.String newHost) {
	host = newHost;
}
/**
 * Creation date: (1/4/2002 3:12:56 PM)
 * @param newPort int
 */
public void setPort(int newPort) {
	port = newPort;
}
/**
 * Creation date: (1/28/2002 10:05:48 AM)
 * @param newUpdateInterval long
 */
public void setUpdateInterval(long newUpdateInterval) {
	updateTimer.setDelay((int) newUpdateInterval );
}
/**
 * Creation date: (1/28/2002 9:36:17 AM)
 * @param drawing java.lang.String
 */
protected void showDrawing(String drawing) {
	stopUpdates();
	
	drawingPanel.loadDrawing(drawing);
	
	cardLayout.show(this, DRAWING_PANEL);
	
	startUpdates();
}
/**
 * Creation date: (1/28/2002 9:35:47 AM)
 */
protected void showLogin() {
	stopUpdates();
	cardLayout.show(this, LOGIN_PANEL);	
}
/**
 * Creation date: (1/28/2002 10:13:38 AM)
 */
protected void startUpdates() {
	updateTimer.restart();
}
/**
 * Creation date: (1/28/2002 10:13:00 AM)
 */
protected void stopUpdates() {
	updateTimer.stop();	
}
/**
 * Creation date: (1/4/2002 2:48:32 PM)
 * @param e java.util.EventObject
 */
public void submitButtonAction_actionPerformed(EventObject e) 
{
	setCursor( java.awt.Cursor.getPredefinedCursor(java.awt.Cursor.WAIT_CURSOR));
	
    if( session == null )
    	session = ClientSession.getInstance();

    try {
	    String username = loginPanel.getUsernameTextFieldText();
    	String password = new String(loginPanel.getPasswordTextFieldPassword());
    	
    	java.net.URL u = new java.net.URL("http", getHost(), "/servlet/LoginController");
			
    	if( session.establishSession(u, username, password ) ) {
	    	showDrawing("index.jlx");
    	}
    	else {
	    	//bad login
	    	javax.swing.JOptionPane.showMessageDialog(this, "Invalid login", null, javax.swing.JOptionPane.WARNING_MESSAGE);
    	}
    }
    catch(java.net.MalformedURLException me ) {
	    me.printStackTrace();
    }

    setCursor( java.awt.Cursor.getPredefinedCursor(java.awt.Cursor.DEFAULT_CURSOR));
}
/*
URL url = new URL("http://www.m-w.com/cgi-bin/dictionary");
            URLConnection connection = url.openConnection();
            connection.setDoOutput(true);
            OutputStreamWriter ostream = new OutputStreamWriter(connection.getOutputStream());
            BufferedWriter out = new BufferedWriter(ostream);
            out.write("book=Dictionary&va=doggerel\r\n");
            out.flush();
            out.close();

            InputStream stream = connection.getInputStream();
            BufferedInputStream in = new BufferedInputStream(stream);
            int i = 0;
            while ((i = in.read()) != -1) {
                System.out.write(i);
            }
            in.close();
*/
}
