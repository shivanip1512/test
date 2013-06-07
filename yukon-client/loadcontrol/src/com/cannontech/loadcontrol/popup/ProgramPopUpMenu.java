package com.cannontech.loadcontrol.popup;

/**
 * Insert the type's description here.
 * Creation date: (1/21/2001 4:40:03 PM)
 * @author: 
 */
import java.awt.Frame;

import javax.swing.JMenuItem;
import javax.swing.JOptionPane;

import com.cannontech.common.util.SwingUtil;
import com.cannontech.loadcontrol.LCUtils;
import com.cannontech.loadcontrol.LoadControlClientConnection;
import com.cannontech.loadcontrol.gui.manualentry.CurtailmentEntryPanel;
import com.cannontech.loadcontrol.gui.manualentry.DirectControlJPanel;
import com.cannontech.messaging.message.loadcontrol.CommandMessage;
import com.cannontech.messaging.message.loadcontrol.ManualControlRequestMessage;
import com.cannontech.messaging.message.loadcontrol.data.Program;
import com.cannontech.messaging.message.loadcontrol.data.ProgramCurtailment;
import com.cannontech.messaging.message.loadcontrol.data.ProgramDirect;

public class ProgramPopUpMenu extends javax.swing.JPopupMenu implements java.awt.event.ActionListener
{
	private Program loadControlProgram = null;

	
	private JMenuItem jMenuItemDisable = null;
	private JMenuItem jMenuItemStartStop = null;
    private JMenuItem jMenuItemChangeGear = null;
    
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
    
    if( e.getSource() == getJMenuItemChangeGear() ) 
        jMenuItemChangeGear_ActionPerformed(e);
}

/**
 * Insert the method's description here.
 * Creation date: (1/15/2001 9:20:50 AM)
 * @return javax.swing.JMenuItem
 */
private JMenuItem getJMenuItemDisable() 
{
	if (jMenuItemDisable == null) 
	{
		try 
		{
			jMenuItemDisable = new JMenuItem();
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

private JMenuItem getJMenuItemChangeGear() {
    if (jMenuItemChangeGear == null) {
        try {
            jMenuItemChangeGear = new JMenuItem();
            jMenuItemChangeGear.setName("JMenuItemChangeGear");
            jMenuItemChangeGear.setMnemonic('g');
            jMenuItemChangeGear.setText("Change Gear");
            jMenuItemChangeGear.setActionCommand("jMenuItemChangeGear");
        } 
        catch (java.lang.Throwable ivjExc) {
            handleException(ivjExc);
        }
    }
    
    return jMenuItemChangeGear;
}
/**
 * Insert the method's description here.
 * Creation date: (1/15/2001 9:20:50 AM)
 * @return JMenuItem
 */
private JMenuItem getJMenuItemStartStop() 
{
	if (jMenuItemStartStop == null) 
	{
		try 
		{
			jMenuItemStartStop = new JMenuItem();
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
public Program getLoadControlProgram() {
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
    getJMenuItemChangeGear().addActionListener(this);
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
        add(getJMenuItemChangeGear(), getJMenuItemChangeGear().getName());
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
	if( getLoadControlProgram().getDisableFlag() )
	{
		//send a message to the server telling it to ENABLE this program

		LoadControlClientConnection.getInstance().write(
				new CommandMessage( CommandMessage.ENABLE_PROGRAM,
					 				getLoadControlProgram().getYukonId(),
					 				0, 0.0) );
	}
	else
	{
		//send a message to the server telling it to DISABLE this program
		int res = JOptionPane.showConfirmDialog( this, "Supress sending restoration commands?", 
					"Supress Restores", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE );

		if( res == JOptionPane.YES_OPTION )		
			LoadControlClientConnection.getInstance().write(
				new CommandMessage( CommandMessage.EMERGENCY_DISABLE_PROGRAM,
									getLoadControlProgram().getYukonId(),
									0, 0.0) );
		else
			LoadControlClientConnection.getInstance().write(
				new CommandMessage( CommandMessage.DISABLE_PROGRAM,
					 				getLoadControlProgram().getYukonId(),
					 				0, 0.0) );
	}

	return;
}

private void jMenuItemChangeGear_ActionPerformed(java.awt.event.ActionEvent actionEvent) {
	if( getLoadControlProgram() instanceof ProgramDirect ) {
        PopUpPanel p = new PopUpPanel( this );
        p.showChangeGearOptions( (ProgramDirect)getLoadControlProgram());
	}

	return;
}

private void jMenuItemStartStop_ActionPerformed(java.awt.event.ActionEvent actionEvent) {

    if( getLoadControlProgram() instanceof ProgramDirect )
    {
        showDirectManualEntry(
            "Start...".equalsIgnoreCase(getJMenuItemStartStop().getText())
            ? DirectControlJPanel.MODE_START_STOP
            : DirectControlJPanel.MODE_STOP  );
    }

    if( getLoadControlProgram() instanceof ProgramCurtailment )
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
	PopUpPanel p = new PopUpPanel( this );
	
	p.showDirectManualEntry(
			panelMode,
			new Program[] { getLoadControlProgram() } );
}


/**
 * Insert the method's description here.
 * Creation date: (1/21/2001 5:32:52 PM)
 * @param newLoadControlProgram LMProgramBase
 */
public void setLoadControlProgram(Program newLoadControlProgram) 
{
	loadControlProgram = newLoadControlProgram;

	if( getLoadControlProgram() != null )
	{
		syncButtons();
	
		//lastly, check for disablement
		if( getLoadControlProgram().getDisableFlag() )
		{
			getJMenuItemStartStop().setEnabled(false);
            getJMenuItemChangeGear().setEnabled(false);
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
        getJMenuItemChangeGear().setEnabled(false);
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
		LoadControlClientConnection.getInstance().write( 
				((ProgramCurtailment)getLoadControlProgram()).createStartStopNowMsg(
					com.cannontech.common.util.CtiUtilities.get1990GregCalendar().getTime(), 
					0, null, false, ManualControlRequestMessage.CONSTRAINTS_FLAG_USE) );
					//com.cannontech.common.util.CtiUtilities.get1990GregCalendar().getTime(), 
					//0, null) );
	}
	else
	{
		Frame frame = SwingUtil.getParentFrame(this.getInvoker());
		java.awt.Cursor savedCursor = null;
		
		try
		{
			if( frame != null )
			{
				savedCursor = frame.getCursor();
				frame.setCursor( new java.awt.Cursor( java.awt.Cursor.WAIT_CURSOR ) );
			}
				
			final javax.swing.JDialog dialog = new javax.swing.JDialog( frame, "Curtailment Notice", true);

			CurtailmentEntryPanel panel = new CurtailmentEntryPanel( (ProgramCurtailment)getLoadControlProgram() )
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
	
	switch( getLoadControlProgram().getProgramStatus() )
	{
		case Program.STATUS_ACTIVE:
		case Program.STATUS_TIMED_ACTIVE:
			getJMenuItemStartStop().setEnabled(false);
			getJMenuItemDisable().setEnabled(true);
			break;
		
		case Program.STATUS_MANUAL_ACTIVE:
            getJMenuItemStartStop().setEnabled(true);
            getJMenuItemDisable().setEnabled(true);
            getJMenuItemChangeGear().setEnabled(true);
            break;
            
		case Program.STATUS_FULL_ACTIVE:
			getJMenuItemStartStop().setEnabled(true);
			getJMenuItemDisable().setEnabled(true);
            getJMenuItemChangeGear().setEnabled(false);
			break;
		
		case Program.STATUS_INACTIVE:
		case Program.STATUS_NON_CNTRL:
			getJMenuItemStartStop().setEnabled(true);
			getJMenuItemDisable().setEnabled(true);
            getJMenuItemChangeGear().setEnabled(false);
			break;
			
		case Program.STATUS_NOTIFIED:
		case Program.STATUS_SCHEDULED:
		case Program.STATUS_CNTRL_ATTEMPT:
			getJMenuItemStartStop().setEnabled(true);
			getJMenuItemDisable().setEnabled(true);
            getJMenuItemChangeGear().setEnabled(false);
			break;

		case Program.STATUS_STOPPING: /*only used by the server*/
			getJMenuItemStartStop().setEnabled(false);
			getJMenuItemDisable().setEnabled(false);
            getJMenuItemChangeGear().setEnabled(false);
			break;

		default:
			throw new IllegalStateException("Found an unexpected state for a LMProgram object, value = " + getLoadControlProgram().getProgramStatus() );
	}


}
}
