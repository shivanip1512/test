package com.cannontech.loadcontrol.popup;

/**
 * Insert the type's description here.
 * Creation date: (9/28/00 3:40:06 PM)
 * @author: 
 */


import com.cannontech.loadcontrol.gui.manualentry.AckUserPanel;
import com.cannontech.loadcontrol.data.LMCurtailCustomer;
import com.cannontech.loadcontrol.data.LMGroupBase;
import com.cannontech.loadcontrol.gui.manualentry.LMGroupInfoPanel;
import com.cannontech.loadcontrol.messages.LMCurtailmentAcknowledgeMsg;

public class CurtailPopUpMenu extends javax.swing.JPopupMenu implements java.awt.event.ActionListener
{
	private LMCurtailCustomer loadControlGroup = null;
	private javax.swing.JMenuItem jMenuItemAck = null;
	private javax.swing.JMenuItem jMenuItemDisable = null;
	private javax.swing.JMenuItem jMenuItemGroupInfo = null;

/**
 * GroupPopUpMenu constructor comment.
 */
public CurtailPopUpMenu() 
{
	super();

	initialize();
}
/**
 * GroupPopUpMenu constructor comment.
 * @param label java.lang.String
 */
public CurtailPopUpMenu(String label) 
{
	super(label);
	initialize();
}
/**
 * Method to handle events for the ActionListener interface.
 * @param e java.awt.event.ActionEvent
 */
public void actionPerformed(java.awt.event.ActionEvent e) 
{	
	if( e.getSource() == getJMenuItemGroupInfo() )
		executeGroupInfo(e);
	if( e.getSource() == getJMenuItemDisable() )
		executeDisableEnable(e);

	if( e.getSource() == getJMenuItemAck() )
		executeAcknowledgement(e);
}
/**
 * Insert the method's description here.
 * Creation date: (9/28/00 3:43:40 PM)
 * @param e java.awt.event.ActionEvent
 */
private void executeAcknowledgement(java.awt.event.ActionEvent e) 
{
	java.awt.Frame frame = com.cannontech.common.util.CtiUtilities.getParentFrame( this.getInvoker() );
	java.awt.Cursor savedCursor = null;
	
	try
	{
		if( frame != null )
		{
			savedCursor = frame.getCursor();
			frame.setCursor( new java.awt.Cursor( java.awt.Cursor.WAIT_CURSOR ) );
		}
			
		final javax.swing.JDialog dialog = new javax.swing.JDialog( frame, "Acknowledge Customer", true);

		AckUserPanel panel = new AckUserPanel()
		{
			protected void disposePanel()
			{
				dialog.dispose();
			};

		};

		
		dialog.getContentPane().add( panel );

		// this location calculation tries to get the new dialog centered relative to the Main Frame
		dialog.setLocation( (int)(frame.getLocationOnScreen().getX() + frame.getWidth() * .25), 
				(int)(frame.getLocationOnScreen().getY() + frame.getHeight() * .25) );
		
		dialog.setModal(true);
		dialog.setSize(400, 210);
		dialog.show();						

		if( panel.getChoice() == AckUserPanel.CONFIRMED_PANEL )
		{
			LMCurtailmentAcknowledgeMsg msg = new LMCurtailmentAcknowledgeMsg();

			msg.setAcknowledgeStatus( LMCurtailmentAcknowledgeMsg.VERBAL );
			msg.setUserIdName( msg.getUserName() );
			msg.setYukonID( getLoadControlGroup().getYukonID().intValue() );
			msg.setCurtailReferenceID( getLoadControlGroup().getCurtailRefID() );
			msg.setIpAddressOfAckUser( com.cannontech.common.util.CtiUtilities.getUserIPAddress() );


			//get these values from the GUI
			msg.setNameOfAckPerson( panel.getUserName() );
			msg.setCurtailmentNotes( panel.getNotes() );

			//write the ackMessage to the server
			com.cannontech.loadcontrol.LoadControlClientConnection.getInstance().write( msg );
		}


	}
	finally
	{
		if( savedCursor != null && frame != null )
			frame.setCursor( savedCursor );
	}


}
/**
 * Insert the method's description here.
 * Creation date: (9/28/00 3:43:40 PM)
 * @param e java.awt.event.ActionEvent
 */
private void executeDisableEnable(java.awt.event.ActionEvent e) 
{
}
/**
 * Insert the method's description here.
 * Creation date: (9/28/00 3:43:40 PM)
 * @param e java.awt.event.ActionEvent
 */
private void executeGroupInfo(java.awt.event.ActionEvent e) 
{
	java.awt.Frame frame = com.cannontech.common.util.CtiUtilities.getParentFrame( this.getInvoker() );
	java.awt.Cursor savedCursor = null;
	
	try
	{
		if( frame != null )
		{
			savedCursor = frame.getCursor();
			frame.setCursor( new java.awt.Cursor( java.awt.Cursor.WAIT_CURSOR ) );
		}
			
		final javax.swing.JDialog dialog = new javax.swing.JDialog( frame, "Mandatory Curtailment Notice", true);

		LMGroupInfoPanel panel = new LMGroupInfoPanel()
		{
			public void jButtonOk_ActionPerformed(java.awt.event.ActionEvent actionEvent)
			{
				dialog.dispose();
			};

		};

		panel.setLmGroup( getLoadControlGroup() );
		
		dialog.getContentPane().add( panel );

		// this location calculation tries to get the new dialog centered relative to the Main Frame
		dialog.setLocation( (int)(frame.getLocationOnScreen().getX() + frame.getWidth() * .25), 
				(int)(frame.getLocationOnScreen().getY() + frame.getHeight() * .25) );
		
		dialog.setModal(true);
		dialog.setSize(517, 400);
		dialog.show();						
	}
	finally
	{
		if( savedCursor != null && frame != null )
			frame.setCursor( savedCursor );
	}
	
}
/**
 * Insert the method's description here.
 * Creation date: (4/19/2001 2:28:59 PM)
 * @return javax.swing.JMenuItem
 */
private javax.swing.JMenuItem getJMenuItemAck() 
{
	if( jMenuItemAck == null )
	{
		try 
		{
			jMenuItemAck = new javax.swing.JMenuItem();
			jMenuItemAck.setName("JMenuItemAck");
			jMenuItemAck.setMnemonic('c');
			jMenuItemAck.setText("Acknowledge...");
			jMenuItemAck.setActionCommand("jMenuItemAck");
		} 
		catch (java.lang.Throwable ivjExc) 
		{
			handleException(ivjExc);
		}
	}

	return jMenuItemAck;
}
/**
 * Insert the method's description here.
 * Creation date: (4/17/2001 2:03:46 PM)
 * @return javax.swing.JMenuItem
 */
private javax.swing.JMenuItem getJMenuItemDisable() 
{
	if (jMenuItemDisable == null) 
	{
		try 
		{
			jMenuItemDisable = new javax.swing.JMenuItem();
			jMenuItemDisable.setName("JMenuItemDisable");
			jMenuItemDisable.setMnemonic('s');
			jMenuItemDisable.setText("Disable");
			jMenuItemDisable.setActionCommand("jMenuItemDisable");
		} 
		catch (java.lang.Throwable ivjExc) 
		{
			handleException(ivjExc);
		}
	}

	return jMenuItemDisable;
}
/**
 * Insert the method's description here.
 * Creation date: (4/17/2001 1:59:58 PM)
 * @return javax.swing.JMenuItem
 */
private javax.swing.JMenuItem getJMenuItemGroupInfo() 
{
	if (jMenuItemGroupInfo == null) 
	{
		try 
		{
			jMenuItemGroupInfo = new javax.swing.JMenuItem();
			jMenuItemGroupInfo.setName("JMenuItemGroupInfo");
			jMenuItemGroupInfo.setMnemonic('g');
			jMenuItemGroupInfo.setText("Group Info");
			jMenuItemGroupInfo.setActionCommand("jMenuItemGroupInfo");
		} 
		catch (java.lang.Throwable ivjExc) 
		{
			handleException(ivjExc);
		}
	}

	return jMenuItemGroupInfo;
}
/**
 * Insert the method's description here.
 * Creation date: (9/28/00 3:50:46 PM)
 * @return LMCurtailCustomer
 */
public LMCurtailCustomer getLoadControlGroup() 
{
	return loadControlGroup;
}
/**
 * Called whenever the part throws an exception.
 * @param exception java.lang.Throwable
 */
private void handleException(java.lang.Throwable exception) 
{
	/* Uncomment the following lines to print uncaught exceptions to stdout */
	com.cannontech.clientutils.CTILogger.info("--------- UNCAUGHT EXCEPTION ---------");
	com.cannontech.clientutils.CTILogger.error( exception.getMessage(), exception );;
}
/**
 * Initializes connections
 * @exception java.lang.Exception The exception description.
 */
private void initConnections() throws java.lang.Exception 
{
	getJMenuItemGroupInfo().addActionListener( this );
	getJMenuItemDisable().addActionListener( this );
	getJMenuItemAck().addActionListener( this );
}
/**
 * Initialize the class.
 */
private void initialize() 
{
	try 
	{
		setName("CurtailPopUpMenu");
		setBorderPainted( true );

		add(getJMenuItemGroupInfo(), getJMenuItemGroupInfo().getName());
		add(getJMenuItemAck(), getJMenuItemAck().getName());

		//Dont allow this yet
		//add(getJMenuItemDisable(), getJMenuItemDisable().getName());
		
		initConnections();
	} 
	catch (java.lang.Throwable ivjExc) 
	{
		handleException(ivjExc);
	}
}
/**
 * Insert the method's description here.
 * Creation date: (8/25/00 10:40:12 AM)
 * @param LMGroupBase
 */
public void setLoadControlGroup(LMGroupBase newGroup) 
{
	if( !(newGroup instanceof LMCurtailCustomer) )
		throw new RuntimeException("Trying to set the CurtailPopUpMenu()'s LMGroup to a none LMCurtailCustomer!!");
		
	loadControlGroup = (LMCurtailCustomer)newGroup;

	if( getLoadControlGroup() == null )
		return;
		
	getJMenuItemAck().setEnabled( getLoadControlGroup().getGroupControlStateString().equalsIgnoreCase(LMCurtailCustomer.ACK_UNACKNOWLEDGED) );




	//lastly, check for disablement
	if( getLoadControlGroup().getDisableFlag().booleanValue() )
	{
		getJMenuItemDisable().setText("Enable");
		getJMenuItemAck().setEnabled(false);
	}
	else
	{
		getJMenuItemDisable().setText("Disable");
	}

}
}
