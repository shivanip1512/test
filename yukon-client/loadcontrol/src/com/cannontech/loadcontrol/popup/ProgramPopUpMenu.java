package com.cannontech.loadcontrol.popup;

/**
 * Insert the type's description here.
 * Creation date: (1/21/2001 4:40:03 PM)
 * @author: 
 */
import com.cannontech.loadcontrol.data.LMProgramCurtailment;
import com.cannontech.loadcontrol.data.LMProgramDirect;
import com.cannontech.loadcontrol.messages.LMCommand;
import com.cannontech.loadcontrol.gui.manualentry.CurtailmentEntryPanel;
import com.cannontech.loadcontrol.data.LMProgramBase;

import com.cannontech.loadcontrol.data.LMProgramDirectGear;
import com.cannontech.loadcontrol.gui.manualentry.DirectControlJPanel;

public class ProgramPopUpMenu extends javax.swing.JPopupMenu implements java.awt.event.ActionListener
{
	private LMProgramBase loadControlProgram = null;
	private LMProgramBase[] allLoadControlProgram = null;
	
	private javax.swing.JMenuItem jMenuItemDisable = null;
	private javax.swing.JMenuItem jMenuItemStartStop = null;

/**
 * ProgramPopUpMenu constructor comment.
 */
public ProgramPopUpMenu() 
{
	super();
	initialize();
}
/**
 * ProgramPopUpMenu constructor comment.
 * @param label java.lang.String
 */
public ProgramPopUpMenu(String label)
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
	if( e.getSource() == getJMenuItemStartStop() ) 
		jMenuItemStartStop_ActionPerformed(e);

	if( e.getSource() == getJMenuItemDisable() )
		jMenuItemDisableEnable_ActionPerformed(e);
}
/**
 * Insert the method's description here.
 * Creation date: (5/14/2002 10:50:02 AM)
 * @param panel com.cannontech.loadcontrol.gui.manualentry.DirectControlJPanel
 */
public com.cannontech.loadcontrol.messages.LMManualControlMsg createMessage(DirectControlJPanel panel, LMProgramBase program) 
{
	com.cannontech.loadcontrol.messages.LMManualControlMsg msg = null;
	
	//create the new message
	if( panel.getMode() == com.cannontech.common.gui.panel.ManualChangeJPanel.MODE_STOP )
	{
		if( panel.isStopStartNowSelected() )
			msg = program.createStartStopNowMsg( 
						panel.getStopTime(),
			 			(panel.getSelectedGear() != null ? 
				 		panel.getSelectedGear().getGearNumber().intValue() : 1),
					 	null, false);
		else					
			msg = program.createScheduledStopMsg(
			 			panel.getStartTime(), 
			 			panel.getStopTime(),
			 			(panel.getSelectedGear() != null ? 
				 		panel.getSelectedGear().getGearNumber().intValue() : 1),
			 			null);
	}
	else
	{
		if( panel.isStopStartNowSelected() )
			msg = program.createStartStopNowMsg(
						panel.getStopTime(),
			 			(panel.getSelectedGear() != null ? 
				 		panel.getSelectedGear().getGearNumber().intValue() : 1),
					 	null, true);
		else
			msg = program.createScheduledStartMsg( 
			 			panel.getStartTime(), 
			 			panel.getStopTime(),
			 			(panel.getSelectedGear() != null ? 
				 		panel.getSelectedGear().getGearNumber().intValue() : 1),
			 			null, null );
	}

	
	//write the message out
	return msg;
	//com.cannontech.loadcontrol.LoadControlClientConnection.getInstance().write(msg);	
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
 * Creation date: (1/15/2001 9:20:50 AM)
 * @return javax.swing.JMenuItem
 */
private javax.swing.JMenuItem getJMenuItemStartStop() 
{
	if (jMenuItemStartStop == null) 
	{
		try 
		{
			jMenuItemStartStop = new javax.swing.JMenuItem();
			jMenuItemStartStop.setName("JMenuItemStartStop");
			jMenuItemStartStop.setMnemonic('s');
			jMenuItemStartStop.setText("Start...");
			jMenuItemStartStop.setActionCommand("jMenuItemStartStop");
		} 
		catch (java.lang.Throwable ivjExc) 
		{
			handleException(ivjExc);
		}
	}
	
	return jMenuItemStartStop;
}
/**
 * Insert the method's description here.
 * Creation date: (1/21/2001 5:32:52 PM)
 * @return LMProgramBase
 */
public LMProgramBase getLoadControlProgram() {
	return loadControlProgram;
}
/**
 * Called whenever the part throws an exception.
 * @param exception java.lang.Throwable
 */
private void handleException(java.lang.Throwable exception) 
{
	/* Uncomment the following lines to print uncaught exceptions to stdout */
	System.out.println("--------- UNCAUGHT EXCEPTION ---------");
	exception.printStackTrace(System.out);
}
/**
 * Initializes connections
 * @exception java.lang.Exception The exception description.
 */
private void initConnections() throws java.lang.Exception 
{
	getJMenuItemDisable().addActionListener(this);
	getJMenuItemStartStop().addActionListener(this);
}
/**
 * Initialize the class.
 */
private void initialize() 
{
	try 
	{
		setName("ProgramPopUp");
		//setPreferredSize(new java.awt.Dimension(75, 25));
		setBorderPainted( true );

		add(getJMenuItemStartStop(), getJMenuItemStartStop().getName());
		add(getJMenuItemDisable(), getJMenuItemDisable().getName());


		initConnections();
	} 
	catch (java.lang.Throwable ivjExc) 
	{
		handleException(ivjExc);
	}
	
}
/**
 * Comment
 */
private void jMenuItemDisableEnable_ActionPerformed(java.awt.event.ActionEvent actionEvent)
{
	if( getLoadControlProgram().getDisableFlag().booleanValue() )
	{
		//send a message to the server telling it to ENABLE this program

		com.cannontech.loadcontrol.LoadControlClientConnection.getInstance().write(
				new LMCommand( LMCommand.ENABLE_PROGRAM,
					 				getLoadControlProgram().getYukonID().intValue(),
					 				0, 0.0) );
	}
	else
	{
		//send a message to the server telling it to DISABLE this program

		com.cannontech.loadcontrol.LoadControlClientConnection.getInstance().write(
				new LMCommand( LMCommand.DISABLE_PROGRAM,
					 				getLoadControlProgram().getYukonID().intValue(),
					 				0, 0.0) );
	}

	return;
}
/**
 * Comment
 */
private void jMenuItemStartStop_ActionPerformed(java.awt.event.ActionEvent actionEvent) 
{

	if( getLoadControlProgram() instanceof LMProgramDirect )
	{
		showDirectManualEntry();
	}
	else if(  getLoadControlProgram() instanceof LMProgramCurtailment )
	{
		showCurtailManualEntry();
	}
	else
		throw new IllegalArgumentException("Unable to find LMProgramBase subclass called : " + getLoadControlProgram().getClass().getName() );
		
	
	return;
}
/**
 * Insert the method's description here.
 * Creation date: (1/21/2001 5:32:52 PM)
 * @param newLoadControlProgram LMProgramBase
 */
public void setAllLoadControlPrograms( LMProgramBase[] rows ) 
{
	allLoadControlProgram = rows;
}
/**
 * Insert the method's description here.
 * Creation date: (1/21/2001 5:32:52 PM)
 * @param newLoadControlProgram LMProgramBase
 */
public void setLoadControlProgram(LMProgramBase newLoadControlProgram) 
{
	loadControlProgram = newLoadControlProgram;

	if( getLoadControlProgram() == null )
		return;

	syncButtons();

	//lastly, check for disablement
	if( getLoadControlProgram().getDisableFlag().booleanValue() )
	{
		getJMenuItemStartStop().setEnabled(false);
		getJMenuItemDisable().setText("Enable");
	}
	else
	{
		getJMenuItemDisable().setText("Disable");
	}

}
/**
 * Insert the method's description here.
 * Creation date: (7/16/2001 5:05:29 PM)
 */
private void showCurtailManualEntry() 
{
	if( getJMenuItemStartStop().getText().equalsIgnoreCase("Stop...") )
	{
		//send the new message to the server
		com.cannontech.loadcontrol.LoadControlClientConnection.getInstance().write( 
				((LMProgramCurtailment)getLoadControlProgram()).createStartStopNowMsg(
					com.cannontech.common.util.CtiUtilities.get1990GregCalendar().getTime(), 
					0, null, false) );
					//com.cannontech.common.util.CtiUtilities.get1990GregCalendar().getTime(), 
					//0, null) );
	}
	else
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
				
			final javax.swing.JDialog dialog = new javax.swing.JDialog( frame, "Curtailment Notice", true);

			CurtailmentEntryPanel panel = new CurtailmentEntryPanel( (LMProgramCurtailment)getLoadControlProgram() )
			{

				public void jButtonCancel_ActionPerformed(java.awt.event.ActionEvent actionEvent)
				{
					dialog.dispose();
				};

			};

			dialog.getContentPane().add( panel );

			// this location calculation tries to get the new dialog centered relative to the Main Frame
			dialog.setLocation( (int)(frame.getLocationOnScreen().getX() + frame.getWidth() * .25), 
					(int)(frame.getLocationOnScreen().getY() + frame.getHeight() * .25) );
			
			dialog.setModal(true);
			dialog.pack();
			//dialog.setSize(440, 415);
			dialog.show();						
		}
		finally
		{
			if( savedCursor != null && frame != null )
				frame.setCursor( savedCursor );
		}
	
	}

}
/**
 * Insert the method's description here.
 * Creation date: (7/16/2001 5:05:29 PM)
 */
private void showDirectManualEntry() 
{
	final javax.swing.JDialog d = new javax.swing.JDialog( com.cannontech.common.util.CtiUtilities.getParentFrame(this.getInvoker()) );
	DirectControlJPanel panel = new DirectControlJPanel()
	{
		public void exit()
		{
			d.dispose();
		}

		public void setParentWidth( int x )
		{
			d.setSize( d.getWidth() + x, d.getHeight() );
		}
	};


	if( getJMenuItemStartStop().getText().equalsIgnoreCase("Start...") )
	{
		d.setTitle("Start Program");
		panel.setMode( DirectControlJPanel.MODE_START_STOP );
	}
	else
	{
		d.setTitle("Stop Program");
		panel.setMode( DirectControlJPanel.MODE_STOP );
	}
		
	
	panel.setGears( ((LMProgramDirect)getLoadControlProgram()).getDirectGearVector() );
	panel.setMultiSelectObject( allLoadControlProgram );

	d.setModal(true);
	d.setContentPane(panel);
	d.setSize(300,250);
	d.pack();
	d.setLocationRelativeTo(this);
	d.show();

	if( panel.getChoice() == com.cannontech.common.gui.panel.ManualChangeJPanel.OK_CHOICE )
	{
		Object[] selected = panel.getMultiSelectObject();

		if( selected == null )
		{
			Object[] o = { getLoadControlProgram() };
			selected = o; //create an array with 1 element
		}

		//create a multi to hold all of our messages
		com.cannontech.message.dispatch.message.Multi multi = 
				new com.cannontech.message.dispatch.message.Multi();

		for( int i = 0; i < selected.length; i++ )
		{
			multi.getVector().add( createMessage(panel, (LMProgramBase)selected[i]) );
		}

		com.cannontech.loadcontrol.LoadControlClientConnection.getInstance().write(multi);
	}

	//destroy the JDialog
	d.dispose();
	
}
/**
 * Insert the method's description here.
 * Creation date: (4/9/2001 5:26:53 PM)
 * @param program com.cannontech.loadcontrol.data.LMProgramEnergyExchange
 */
private void syncButtons()
{

	switch( getLoadControlProgram().getProgramStatus().intValue() )
	{
		case LMProgramBase.STATUS_ACTIVE:
			getJMenuItemStartStop().setText("Stop...");
			getJMenuItemStartStop().setEnabled(false);
			getJMenuItemDisable().setEnabled(true);
			break;
		
		case LMProgramBase.STATUS_MANUAL_ACTIVE:
		case LMProgramBase.STATUS_FULL_ACTIVE:
			getJMenuItemStartStop().setText("Stop...");
			getJMenuItemStartStop().setEnabled(true);
			getJMenuItemDisable().setEnabled(true);
			break;
		
		case LMProgramBase.STATUS_INACTIVE:
			getJMenuItemStartStop().setText("Start...");
			getJMenuItemStartStop().setEnabled(true);
			getJMenuItemDisable().setEnabled(true);
			break;
			
		case LMProgramBase.STATUS_NOTIFIED:
		case LMProgramBase.STATUS_SCHEDULED:
			getJMenuItemStartStop().setText("Stop...");
			getJMenuItemStartStop().setEnabled(true);
			getJMenuItemDisable().setEnabled(true);
			break;

		case LMProgramBase.STATUS_STOPPING: /*only used by the server*/
			getJMenuItemStartStop().setText("Start...");
			getJMenuItemStartStop().setEnabled(false);
			getJMenuItemDisable().setEnabled(false);
			break;

		default:
			throw new IllegalStateException("Found an unexpected state for a LMProgram object, value = " + getLoadControlProgram().getProgramStatus().intValue() );
	}


}
}
