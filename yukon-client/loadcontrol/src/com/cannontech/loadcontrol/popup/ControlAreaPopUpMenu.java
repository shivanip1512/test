package com.cannontech.loadcontrol.popup;

/**
 * Insert the type's description here.
 * Creation date: (1/21/2001 4:40:03 PM)
 * @author: 
 */
import com.cannontech.loadcontrol.messages.LMCommand;
import com.cannontech.loadcontrol.data.LMControlArea;
import com.cannontech.loadcontrol.gui.manualentry.ControlAreaTriggerJPanel;
import com.cannontech.loadcontrol.gui.manualentry.ControlAreaTimeChangeJPanel;

public class ControlAreaPopUpMenu extends com.cannontech.tdc.observe.ObservableJPopupMenu implements java.awt.event.ActionListener
{
	private LMControlArea loadControlArea = null;
	private javax.swing.JMenuItem jMenuItemTriggers = null;
	private javax.swing.JMenuItem jMenuItemDialyTime = null;
	private javax.swing.JMenuItem jMenuItemDisable = null;
/**
 * ProgramPopUpMenu constructor comment.
 */
public ControlAreaPopUpMenu() 
{
	super();
	initialize();
}
/**
 * Method to handle events for the ActionListener interface.
 * @param e java.awt.event.ActionEvent
 */
public void actionPerformed(java.awt.event.ActionEvent e) 
{	
	if( e.getSource() == getJMenuItemTriggers() ) 
		jMenuItemTriggers_ActionPerformed(e);

	if( e.getSource() == getJMenuItemDisable() )
		jMenuItemDisableEnable_ActionPerformed(e);

	if( e.getSource() == getJMenuItemDialyTime() )
		jMenuItemDailyTime_ActionPerformed(e);

}
/**
 * Insert the method's description here.
 * Creation date: (1/15/2001 9:20:50 AM)
 * @return javax.swing.JMenuItem
 */
private javax.swing.JMenuItem getJMenuItemDialyTime() 
{
	if( jMenuItemDialyTime == null) 
	{
		try 
		{
			jMenuItemDialyTime = new javax.swing.JMenuItem();
			jMenuItemDialyTime.setName("JMenuItemDailyTime");
			jMenuItemDialyTime.setMnemonic('d');
			jMenuItemDialyTime.setText("Daily Time Change...");
			jMenuItemDialyTime.setActionCommand("jMenuItemDialyTime");
		} 
		catch (java.lang.Throwable ivjExc) 
		{
			handleException(ivjExc);
		}
	}
	
	return jMenuItemDialyTime;
}
/**
 * Insert the method's description here.
 * Creation date: (1/15/2001 9:20:50 AM)
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
			jMenuItemDisable.setMnemonic('b');
			jMenuItemDisable.setText("Disable / Waive");
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
 * Creation date: (1/15/2001 9:20:50 AM)
 * @return javax.swing.JMenuItem
 */
private javax.swing.JMenuItem getJMenuItemTriggers() 
{
	if (jMenuItemTriggers == null) 
	{
		try 
		{
			jMenuItemTriggers = new javax.swing.JMenuItem();
			jMenuItemTriggers.setName("JMenuItemTriggers");
			jMenuItemTriggers.setMnemonic('t');
			jMenuItemTriggers.setText("Triggers Change...");
			jMenuItemTriggers.setActionCommand("jMenuItemTrigger");
		} 
		catch (java.lang.Throwable ivjExc) 
		{
			handleException(ivjExc);
		}
	}
	
	return jMenuItemTriggers;
}
/**
 * Insert the method's description here.
 * Creation date: (1/21/2001 5:32:52 PM)
 * @return LMControlArea
 */
public LMControlArea getLoadControlArea() 
{
	return loadControlArea;
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
private void initConnections()
{
	getJMenuItemDisable().addActionListener(this);
	getJMenuItemTriggers().addActionListener(this);
	getJMenuItemDialyTime().addActionListener(this);
}
/**
 * Initialize the class.
 */
private void initialize() 
{
	try 
	{
		setName("ControlAreaPopUp");
		//setPreferredSize(new java.awt.Dimension(75, 25));
		setBorderPainted( true );

		add(getJMenuItemTriggers(), getJMenuItemTriggers().getName());
		add(getJMenuItemDialyTime(), getJMenuItemDialyTime().getName());
		add(getJMenuItemDisable(), getJMenuItemDisable().getName());


		initConnections();
	} 
	catch( Exception ivjExc )
	{
		handleException(ivjExc);
	}
	
}
/**
 * Comment
 */
private void jMenuItemDailyTime_ActionPerformed(java.awt.event.ActionEvent actionEvent)
{
	if( !getLoadControlArea().getDisableFlag().booleanValue() )
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
				
			final javax.swing.JDialog dialog = new javax.swing.JDialog( frame, "Triggers", true);

			ControlAreaTimeChangeJPanel panel = new ControlAreaTimeChangeJPanel()
			{
				protected void disposePanel()
				{
					dialog.dispose();
				};

			};

			dialog.getContentPane().add( panel );

			//tell our panel what area we are dealing with
			panel.setLmControlArea( getLoadControlArea() );

			// this location calculation tries to get the new dialog centered relative to the Main Frame
			dialog.setLocation( 
					(int)(frame.getLocationOnScreen().getX() + frame.getWidth() * .25), 
					(int)(frame.getLocationOnScreen().getY() + frame.getHeight() * .25) );
			
			dialog.setModal(true);
			dialog.pack();
			dialog.show();

			if( panel.getChoice() == panel.CONFIRMED_PANEL )
			{
				com.cannontech.message.dispatch.message.Multi multi = new com.cannontech.message.dispatch.message.Multi();
				
				//send a message to the server telling it to change the START time
				multi.getVector().add(
						new LMCommand( LMCommand.CHANGE_CURRENT_START_TIME,
						 				getLoadControlArea().getYukonID().intValue(),
						 				0, 
						 				(double)panel.getStartTime()) );

				//send a message to the server telling it to change the STOP time
				multi.getVector().add(
							new LMCommand( LMCommand.CHANGE_CURRENT_STOP_TIME,
						 				getLoadControlArea().getYukonID().intValue(),
						 				0,
						 				(double)panel.getStopTime()) );

				//only send the multi if we have some junk to send.
				if( multi.getVector().size() > 0 )
					com.cannontech.loadcontrol.LoadControlClientConnection.getInstance().write(multi);
			}
	
		}
		finally
		{
			if( savedCursor != null && frame != null )
				frame.setCursor( savedCursor );
		}
	}

	
	return;
}
/**
 * Comment
 */
private void jMenuItemDisableEnable_ActionPerformed(java.awt.event.ActionEvent actionEvent)
{
	if( getLoadControlArea().getDisableFlag().booleanValue() )
	{
		//send a message to the server telling it to ENABLE this LMControlArea
		com.cannontech.loadcontrol.LoadControlClientConnection.getInstance().write(
				new LMCommand( LMCommand.ENABLE_CONTROL_AREA,
					 				getLoadControlArea().getYukonID().intValue(),
					 				0, 0.0) );
	}
	else
	{
		//send a message to the server telling it to DISABLE this LMControlArea
		com.cannontech.loadcontrol.LoadControlClientConnection.getInstance().write(
				new LMCommand( LMCommand.DISABLE_CONTROL_AREA,
					 				getLoadControlArea().getYukonID().intValue(),
					 				0, 0.0) );
	}

	return;
}
/**
 * Comment
 */
private void jMenuItemTriggers_ActionPerformed(java.awt.event.ActionEvent actionEvent) 
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
			
		final javax.swing.JDialog dialog = new javax.swing.JDialog( frame, "Triggers", true);

		ControlAreaTriggerJPanel panel = new ControlAreaTriggerJPanel()
		{
			protected void disposePanel()
			{
				dialog.dispose();
			};

		};

		//lets observe this rows for any changes
		//if( getObservable() != null )
			//getObservable().addObserver( panel );
			
		dialog.getContentPane().add( panel );
		panel.setLmControlArea( getLoadControlArea() );

		// this location calculation tries to get the new dialog centered relative to the Main Frame
		dialog.setLocation( (int)(frame.getLocationOnScreen().getX() + frame.getWidth() * .25), 
				(int)(frame.getLocationOnScreen().getY() + frame.getHeight() * .25) );
		
		dialog.setModal(true);
		dialog.setSize(400, 270);
		dialog.pack();
		dialog.show();						

		//remove our observer
		//if( getObservable() != null )
			//getObservable().deleteObserver( panel );


	}
	finally
	{
		if( savedCursor != null && frame != null )
			frame.setCursor( savedCursor );
	}

}
/**
 * Insert the method's description here.
 * Creation date: (1/21/2001 5:32:52 PM)
 * @param newLoadControlArea LMControlArea
 */
public void setLoadControlArea(LMControlArea newLoadControlArea) 
{
	loadControlArea = newLoadControlArea;

	if( getLoadControlArea() == null )
		return;

	syncMenuItems();

	//lastly, check for disablement
	if( getLoadControlArea().getDisableFlag().booleanValue() )
	{
		getJMenuItemDisable().setText("Enable / ReEnable");
	}
	else
	{
		getJMenuItemDisable().setText("Disable / Waive");
	}

}
/**
 * Insert the method's description here.
 * Creation date: (4/9/2001 5:26:53 PM)
 * @param program com.cannontech.loadcontrol.data.LMProgramEnergyExchange
 */
private void syncMenuItems()
{

	switch( getLoadControlArea().getControlAreaState().intValue() )
	{
		case LMControlArea.STATE_ACTIVE:
			getJMenuItemDisable().setEnabled(true);
			break;
		
		case LMControlArea.STATE_MANUAL_ACTIVE:
		case LMControlArea.STATE_FULLY_ACTIVE:
			getJMenuItemDisable().setEnabled(true);
			break;
		
		case LMControlArea.STATE_INACTIVE:
			getJMenuItemDisable().setEnabled(true);
			break;
			
		case LMControlArea.STATE_SCHEDULED:
			getJMenuItemDisable().setEnabled(true);
			break;

		default:
			throw new IllegalStateException("Found an nonexistent state for a LMControlArea object, value = " + getLoadControlArea().getControlAreaState().intValue() );
	}


}
}
