package com.cannontech.loadcontrol.popup;

/**
 * Insert the type's description here.
 * Creation date: (1/21/2001 4:40:03 PM)
 * @author: 
 */
import com.cannontech.common.gui.panel.ManualChangeJPanel;
import com.cannontech.common.util.CtiUtilities;
import com.cannontech.loadcontrol.LCUtils;
import com.cannontech.loadcontrol.LoadControlClientConnection;
import com.cannontech.loadcontrol.data.LMProgramBase;
import com.cannontech.loadcontrol.data.LMProgramCurtailment;
import com.cannontech.loadcontrol.data.LMProgramDirect;
import com.cannontech.loadcontrol.gui.manualentry.CurtailmentEntryPanel;
import com.cannontech.loadcontrol.gui.manualentry.DirectControlJPanel;
import com.cannontech.loadcontrol.gui.manualentry.MultiSelectProg;
import com.cannontech.loadcontrol.messages.LMCommand;

public class ProgramPopUpMenu extends javax.swing.JPopupMenu implements java.awt.event.ActionListener
{
	private LMProgramBase loadControlProgram = null;

	
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
	com.cannontech.clientutils.CTILogger.info("--------- UNCAUGHT EXCEPTION ---------");
	com.cannontech.clientutils.CTILogger.error( exception.getMessage(), exception );;
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
		showDirectManualEntry(
			"Start...".equalsIgnoreCase(getJMenuItemStartStop().getText())
			? DirectControlJPanel.MODE_START_STOP
			: DirectControlJPanel.MODE_STOP  );
	}

	if( getLoadControlProgram() instanceof LMProgramCurtailment )
	{
		showCurtailManualEntry();
	}
		
	
	return;
}



/**
 * Insert the method's description here.
 * Creation date: (7/16/2001 5:05:29 PM)
 */
private void showDirectManualEntry( final int panelMode ) 
{
	final javax.swing.JDialog d = new javax.swing.JDialog( CtiUtilities.getParentFrame(this.getInvoker()) );
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


	d.setTitle(
		panelMode == DirectControlJPanel.MODE_START_STOP 
		? "Start Program"
		: "Stop Program" );
		
	panel.setMode( panelMode );
	
	
	d.setModal(true);
	d.setContentPane(panel);
	d.setSize(300,250);
	d.pack();
	d.setLocationRelativeTo(this);

	if( panel.setMultiSelectObject( new LMProgramBase[] { getLoadControlProgram() } ) )
	{
		d.show();
	
		if( panel.getChoice() == ManualChangeJPanel.OK_CHOICE )
		{
			MultiSelectProg[] selected = panel.getMultiSelectObject();
	
			if( selected != null )
			{
				//create a multi to hold all of our messages
				com.cannontech.message.dispatch.message.Multi multi = 
						new com.cannontech.message.dispatch.message.Multi();
		
				for( int i = 0; i < selected.length; i++ )
				{
					multi.getVector().add( 
							panel.createMessage(
									selected[i].getBaseProgram(),
									selected[i].getGearNum() ) );
				}
	
				LoadControlClientConnection.getInstance().write(multi);
			}
	
	
		}
	
	}


	//destroy the JDialog
	d.dispose();
	
}

/**
 * Insert the method's description here.
 * Creation date: (1/21/2001 5:32:52 PM)
 * @param newLoadControlProgram LMProgramBase
 */
public void setLoadControlProgram(LMProgramBase newLoadControlProgram) 
{
	loadControlProgram = newLoadControlProgram;

	if( getLoadControlProgram() != null )
	{
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
	else
	{
		getJMenuItemStartStop().setEnabled(false);
		getJMenuItemDisable().setEnabled(false);
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
 * Creation date: (4/9/2001 5:26:53 PM)
 * @param program com.cannontech.loadcontrol.data.LMProgramEnergyExchange
 */
private void syncButtons()
{
	//what string should we display
	getJMenuItemStartStop().setText(
		LCUtils.getProgAvailChgStr(getLoadControlProgram()) );
	
	switch( getLoadControlProgram().getProgramStatus().intValue() )
	{
		case LMProgramBase.STATUS_ACTIVE:
		case LMProgramBase.STATUS_TIMED_ACTIVE:
			getJMenuItemStartStop().setEnabled(false);
			getJMenuItemDisable().setEnabled(true);
			break;
		
		case LMProgramBase.STATUS_MANUAL_ACTIVE:
		case LMProgramBase.STATUS_FULL_ACTIVE:
			getJMenuItemStartStop().setEnabled(true);
			getJMenuItemDisable().setEnabled(true);
			break;
		
		case LMProgramBase.STATUS_INACTIVE:
		case LMProgramBase.STATUS_NON_CNTRL:
			getJMenuItemStartStop().setEnabled(true);
			getJMenuItemDisable().setEnabled(true);
			break;
			
		case LMProgramBase.STATUS_NOTIFIED:
		case LMProgramBase.STATUS_SCHEDULED:
		case LMProgramBase.STATUS_CNTRL_ATTEMPT:
			getJMenuItemStartStop().setEnabled(true);
			getJMenuItemDisable().setEnabled(true);
			break;

		case LMProgramBase.STATUS_STOPPING: /*only used by the server*/
			getJMenuItemStartStop().setEnabled(false);
			getJMenuItemDisable().setEnabled(false);
			break;

		default:
			throw new IllegalStateException("Found an unexpected state for a LMProgram object, value = " + getLoadControlProgram().getProgramStatus().intValue() );
	}


}
}
