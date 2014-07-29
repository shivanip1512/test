package com.cannontech.loadcontrol.gui;

/**
 * Insert the type's description here.
 * Creation date: (9/27/00 12:51:31 PM)
 * @author: 
 */
 
public class LoadControlStandAlone 
{
/**
 * LoadControlStandAlone constructor comment.
 */
public LoadControlStandAlone() {
	super();
}
/**
 * Insert the method's description here.
 * Creation date: (9/27/00 12:51:43 PM)
 * @param args java.lang.String[]
 */
public static void main(String[] args) 
{
	try
	{
		javax.swing.UIManager.setLookAndFeel( javax.swing.UIManager.getSystemLookAndFeelClassName() );

		javax.swing.JFrame appFrame = new javax.swing.JFrame();
		
		appFrame.addWindowListener( new java.awt.event.WindowAdapter() 
			{
				public void windowClosing(java.awt.event.WindowEvent e)
				{
					java.awt.Window win = e.getWindow();
					win.setVisible(false);
					win.dispose();
					System.exit(0);
				}
			} );

		LoadControlMainPanel mainPanel = new LoadControlMainPanel();
		mainPanel.setButtonBarPanelVisible( true );

		appFrame.getContentPane().add( mainPanel );
		
		appFrame.setIconImage(java.awt.Toolkit.getDefaultToolkit().getImage("CTILogo.gif"));
		appFrame.setSize( 670, 520 );
		appFrame.setVisible(true);		
	}
	catch( Exception e)
	{
		com.cannontech.clientutils.CTILogger.error( e.getMessage(), e );
	}	
}
}
