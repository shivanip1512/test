package com.cannontech.loadcontrol.popup;

/**
 * Insert the type's description here.
 * Creation date: (1/21/2001 4:40:03 PM)
 * @author: 
 */
import java.awt.Dimension;

import javax.swing.JOptionPane;
import javax.swing.plaf.DimensionUIResource;

import com.cannontech.common.gui.panel.ManualChangeJPanel;
import com.cannontech.common.util.CtiUtilities;
import com.cannontech.loadcontrol.LoadControlClientConnection;
import com.cannontech.loadcontrol.data.LMControlArea;
import com.cannontech.loadcontrol.data.LMProgramBase;
import com.cannontech.loadcontrol.gui.manualentry.ControlAreaTimeChangeJPanel;
import com.cannontech.loadcontrol.gui.manualentry.ControlAreaTriggerJPanel;
import com.cannontech.loadcontrol.gui.manualentry.DirectControlJPanel;
import com.cannontech.loadcontrol.gui.manualentry.MultiSelectProg;
import com.cannontech.loadcontrol.messages.LMCommand;
import com.cannontech.message.dispatch.message.Multi;


public class ControlAreaPopUpMenu extends com.cannontech.tdc.observe.ObservableJPopupMenu implements java.awt.event.ActionListener
{
	public static final String STR_DIS_PRGRMS = "Disable Program(s)";
    public static final String STR_EN_PRGRMS = "Enable Program(s)";
    private LMControlArea loadControlArea = null;
	private javax.swing.JMenuItem jMenuItemTriggers = null;
	private javax.swing.JMenuItem jMenuItemDialyTime = null;
	private javax.swing.JMenuItem jMenuItemDisable = null;
	private javax.swing.JMenuItem jMenuItemStart = null;
	private javax.swing.JMenuItem jMenuItemStop = null;
	private javax.swing.JMenuItem jMenuItemEnableProgs = null;
	private javax.swing.JMenuItem jMenuItemDisableProgs = null;
    private javax.swing.JMenuItem jMenuItemResetPeak = null;

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

	if( e.getSource() == getJMenuItemStart() )
		jMenuItemStart_ActionPerformed(e);

	if( e.getSource() == getJMenuItemStop() )
		jMenuItemStop_ActionPerformed(e);

	if( e.getSource() == getJMenuItemEnableProgs() )
		jMenuItemEnableProgs_ActionPerformed(e);

	if( e.getSource() == getJMenuItemDisableProgs() )
		jMenuItemDisableProgs_ActionPerformed(e);

    if( e.getSource() == getJMenuItemResetPeak() )
        jMenuItemResetPeak_ActionPerformed(e);

}

/**
 * Insert the method's description here.
 * Creation date: (1/15/2001 9:20:50 AM)
 * @return javax.swing.JMenuItem
 */
private javax.swing.JMenuItem getJMenuItemEnableProgs() 
{
	if( jMenuItemEnableProgs == null) 
	{
		try 
		{
			jMenuItemEnableProgs = new javax.swing.JMenuItem();
			jMenuItemEnableProgs.setName("JMenuItemEnableProgs");
			jMenuItemEnableProgs.setMnemonic('n');
			jMenuItemEnableProgs.setText("Enable Program(s)...");
			jMenuItemEnableProgs.setActionCommand("jMenuItemEnableProgs");
		} 
		catch (java.lang.Throwable ivjExc) 
		{
			handleException(ivjExc);
		}
	}
	
	return jMenuItemEnableProgs;
}

/**
 * Insert the method's description here.
 * Creation date: (1/15/2001 9:20:50 AM)
 * @return javax.swing.JMenuItem
 */
private javax.swing.JMenuItem getJMenuItemDisableProgs() 
{
	if( jMenuItemDisableProgs == null) 
	{
		try 
		{
			jMenuItemDisableProgs = new javax.swing.JMenuItem();
			jMenuItemDisableProgs.setName("jMenuItemDisableProgs");
			jMenuItemDisableProgs.setMnemonic('b');
			jMenuItemDisableProgs.setText("Disable Program(s)...");
			jMenuItemDisableProgs.setActionCommand("jMenuItemDisableProgs");
		} 
		catch (java.lang.Throwable ivjExc) 
		{
			handleException(ivjExc);
		}
	}
	
	return jMenuItemDisableProgs;
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
private javax.swing.JMenuItem getJMenuItemStart() 
{
	if( jMenuItemStart == null) 
	{
		try 
		{
			jMenuItemStart = new javax.swing.JMenuItem();
			jMenuItemStart.setName("JMenuItemStart");
			jMenuItemStart.setMnemonic('s');
			jMenuItemStart.setText("Start Program(s)...");
			jMenuItemStart.setActionCommand("jMenuItemStart");
		} 
		catch (java.lang.Throwable ivjExc) 
		{
			handleException(ivjExc);
		}
	}
	
	return jMenuItemStart;
}


/**
 * Insert the method's description here.
 * Creation date: (1/15/2001 9:20:50 AM)
 * @return javax.swing.JMenuItem
 */
private javax.swing.JMenuItem getJMenuItemStop() 
{
	if( jMenuItemStop == null) 
	{
		try 
		{
			jMenuItemStop = new javax.swing.JMenuItem();
			jMenuItemStop.setName("JjMenuItemStop");
			jMenuItemStop.setMnemonic('t');
			jMenuItemStop.setText("Stop Program(s)...");
			jMenuItemStop.setActionCommand("jMenuItemStop");
		} 
		catch (java.lang.Throwable ivjExc) 
		{
			handleException(ivjExc);
		}
	}
	
	return jMenuItemStop;
}


/**
 * Insert the method's description here.
 * Creation date: (7/16/2001 5:05:29 PM)
 */
private void showDirectManualEntry( final int panelMode ) 
{
	PopUpPanel p = new PopUpPanel( this );
	
	//get an array of LMProgramBase to use later 
	// (only copies the references into the new array, not a full instance copy!!)
	LMProgramBase[] prgArray = new LMProgramBase[ getLoadControlArea().getLmProgramVector().size() ]; 
	prgArray = (LMProgramBase[])getLoadControlArea().getLmProgramVector().toArray( prgArray );

	p.showDirectManualEntry( panelMode, prgArray );
	
}


/**
 * Insert the method's description here.
 * Creation date: (7/16/2001 5:05:29 PM)
 */
private void showProgramAblementPanel( final int cmd ) 
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
		cmd == LMCommand.ENABLE_PROGRAM 
		? STR_EN_PRGRMS
		: STR_DIS_PRGRMS );

		
	d.setModal(true);
	d.setContentPane(panel);
	d.setSize(300,400);
    panel.setMinimumSize(new Dimension(300,300));
    panel.setPreferredSize(new Dimension(300,300));
	d.pack();
	d.setLocationRelativeTo(this);

	//get an array of LMProgramBase to use later 
	// (only copies the references into the new array, not a full instance copy!!)
	LMProgramBase[] prgArray = new LMProgramBase[ getLoadControlArea().getLmProgramVector().size() ]; 
	prgArray = (LMProgramBase[])getLoadControlArea().getLmProgramVector().toArray( prgArray );

	if( panel.setMultiSelectObject( prgArray ) )
	{
		panel.setMode( DirectControlJPanel.MODE_MULTI_SELECT_ONLY );
		d.show();
	
		//destroy the JDialog
		d.dispose();

		if( panel.getChoice() == ManualChangeJPanel.OK_CHOICE )
		{
			MultiSelectProg[] selected = panel.getMultiSelectObject();			
				
			if( selected != null )
			{
				Multi multiCmd = new Multi();
				for( int i = 0; i < selected.length; i++ )
				{
					//only add the operate the program if it's state is opposite of the command
					if( (cmd == LMCommand.ENABLE_PROGRAM && selected[i].getBaseProgram().getDisableFlag().booleanValue())
						|| (cmd == LMCommand.DISABLE_PROGRAM && !selected[i].getBaseProgram().getDisableFlag().booleanValue()) )
					{
						multiCmd.getVector().add(
							new LMCommand( cmd,
								selected[i].getBaseProgram().getYukonID().intValue(),
								0, 0.0) );
					}
				}

				//write the multi of commands to the server
				LoadControlClientConnection.getInstance().write( multiCmd );
				
				fireObservedRowChanged(
					"Control area '" + getLoadControlArea().getYukonName() +
					"' has issued a '" + d.getTitle() + "' command successfully." );
			}
	
	
		}
	
	}
	else
	{
		JOptionPane.showMessageDialog(
			this,
			"There are no programs attached to the control area '" + getLoadControlArea().getYukonName() + "'",
			"Unable to Enable/Disable Programs",
			JOptionPane.WARNING_MESSAGE );
		
	}
	
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
private javax.swing.JMenuItem getJMenuItemResetPeak() 
{
    if (jMenuItemResetPeak == null) 
    {
        try 
        {
            jMenuItemResetPeak = new javax.swing.JMenuItem();
            jMenuItemResetPeak.setName("JMenuItemResetPeak");
            jMenuItemResetPeak.setMnemonic('p');
            jMenuItemResetPeak.setText("Reset Peak...");
            jMenuItemResetPeak.setActionCommand("jMenuItemResetPeak");
        } 
        catch (java.lang.Throwable ivjExc) 
        {
            handleException(ivjExc);
        }
    }
    
    return jMenuItemResetPeak;
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

	getJMenuItemStart().addActionListener(this);
	getJMenuItemStop().addActionListener(this);
	
	getJMenuItemEnableProgs().addActionListener(this);
	getJMenuItemDisableProgs().addActionListener(this);

    getJMenuItemResetPeak().addActionListener(this);
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

		add(getJMenuItemStart(), getJMenuItemStart().getName());
		add(getJMenuItemStop(), getJMenuItemStop().getName());
		add(getJMenuItemEnableProgs(), getJMenuItemEnableProgs().getName());
		add(getJMenuItemDisableProgs(), getJMenuItemDisableProgs().getName());

		add(getJMenuItemTriggers(), getJMenuItemTriggers().getName());
		add(getJMenuItemDialyTime(), getJMenuItemDialyTime().getName());
		add(getJMenuItemDisable(), getJMenuItemDisable().getName());
        add(getJMenuItemResetPeak(), getJMenuItemResetPeak().getName());


		initConnections();
	} 
	catch( Exception ivjExc )
	{
		handleException(ivjExc);
	}
	
}

private void jMenuItemEnableProgs_ActionPerformed(java.awt.event.ActionEvent actionEvent)
{
	showProgramAblementPanel( LMCommand.ENABLE_PROGRAM );
}
private void jMenuItemDisableProgs_ActionPerformed(java.awt.event.ActionEvent actionEvent)
{
	showProgramAblementPanel( LMCommand.DISABLE_PROGRAM );
}

private void jMenuItemStart_ActionPerformed(java.awt.event.ActionEvent actionEvent)
{
	showDirectManualEntry( DirectControlJPanel.MODE_START_STOP );
}

private void jMenuItemStop_ActionPerformed(java.awt.event.ActionEvent actionEvent)
{
	showDirectManualEntry( DirectControlJPanel.MODE_STOP );
}

/**
 * Comment
 */
private void jMenuItemDailyTime_ActionPerformed(java.awt.event.ActionEvent actionEvent)
{
	if( !getLoadControlArea().getDisableFlag().booleanValue() )
	{
		java.awt.Frame frame = CtiUtilities.getParentFrame( this.getInvoker() );
		java.awt.Cursor savedCursor = null;
		
		try
		{
			if( frame != null )
			{
				savedCursor = frame.getCursor();
				frame.setCursor( new java.awt.Cursor( java.awt.Cursor.WAIT_CURSOR ) );
			}
				
			final javax.swing.JDialog dialog = new javax.swing.JDialog( frame, "Daily Time Change", true);

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
				if( panel.getStartTime() != LMControlArea.INVALID_INT )
					multi.getVector().add(
							new LMCommand( LMCommand.CHANGE_CURRENT_START_TIME,
							 				getLoadControlArea().getYukonID().intValue(),
							 				0, 
							 				(double)panel.getStartTime()) );

				//send a message to the server telling it to change the STOP time
				if( panel.getStopTime() != LMControlArea.INVALID_INT )
					multi.getVector().add(
								new LMCommand( LMCommand.CHANGE_CURRENT_STOP_TIME,
							 				getLoadControlArea().getYukonID().intValue(),
							 				0,
							 				(double)panel.getStopTime()) );

				//only send the multi if we have some junk to send.
				if( multi.getVector().size() > 0 )
					LoadControlClientConnection.getInstance().write(multi);
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
		int res = JOptionPane.showConfirmDialog( this,
							"Are you sure you want to ENABLE the selected control area?", 
							"Enable Confirmation", 
							JOptionPane.OK_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE );

		if( res == JOptionPane.OK_OPTION )
		{
			LoadControlClientConnection.getInstance().write(
					new LMCommand( LMCommand.ENABLE_CONTROL_AREA,
						 				getLoadControlArea().getYukonID().intValue(),
						 				0, 0.0) );
						 				
			fireObservedRowChanged(
				"Control area '" + getLoadControlArea().getYukonName() +
				"' has been manually ENABLED." );
		}
	}
	else
	{
		int res = JOptionPane.showConfirmDialog( this,
							"Are you sure you want to DISABLE the selected control area?", 
							"Disable Confirmation",
							JOptionPane.OK_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE );

		if( res == JOptionPane.OK_OPTION )
		{
			//send a message to the server telling it to DISABLE this LMControlArea
			LoadControlClientConnection.getInstance().write(
					new LMCommand( LMCommand.DISABLE_CONTROL_AREA,
						 				getLoadControlArea().getYukonID().intValue(),
						 				0, 0.0) );
	
			fireObservedRowChanged(
				"Control area '" + getLoadControlArea().getYukonName() +
				"' has been manually DISABLED." );
		}
	}

	return;
}

/**
 * Sends the Reset Peak command for this LMControlArea to the server
 */
private void jMenuItemResetPeak_ActionPerformed(java.awt.event.ActionEvent actionEvent)
{
    //send a message to the server
    int res = JOptionPane.showConfirmDialog( this,
            "Are you sure you want to reset the " +
            "Peak Value(s) for all triggers to zero on the selected CONTROL AREA?",
            "Reset Peak", 
            JOptionPane.OK_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE );

    if( res == JOptionPane.OK_OPTION )
    {
        
        Multi multi = new Multi();
        for( int i = 0; i < getLoadControlArea().getTriggerVector().size(); i++ ) {
            multi.getVector().add(
                new LMCommand( LMCommand.RESET_PEAK_POINT_VALUE,
                        getLoadControlArea().getYukonID().intValue(),
                        i+1, 0.0) );
        }
        
        LoadControlClientConnection.getInstance().write( multi );
                                    
        fireObservedRowChanged(
            "Control area '" + getLoadControlArea().getYukonName() +
            "' has had its Peak reset." );
    }
}

/**
 * Comment
 */
private void jMenuItemTriggers_ActionPerformed(java.awt.event.ActionEvent actionEvent) 
{
	java.awt.Frame frame = CtiUtilities.getParentFrame( this.getInvoker() );
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


		if( panel.getChoice() == ControlAreaTriggerJPanel.CONFIRMED_PANEL ) {
			fireObservedRowChanged(
				"Trigger change for '" + getLoadControlArea().getYukonName() +
				"' executed." );			
		}
		
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
		if(newLoadControlArea.getControlAreaState() == LMControlArea.STATE_INACTIVE) {
		    getJMenuItemStart().setVisible(true);
		    getJMenuItemStop().setVisible(false);
		}else {
		    getJMenuItemStart().setVisible(false);
		    getJMenuItemStop().setVisible(true);
		}
		
	}

}

/**
 * Insert the method's description here.
 * Creation date: (4/9/2001 5:26:53 PM)
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
			
		//case LMControlArea.STATE_SCHEDULED:
		case LMControlArea.STATE_CNTRL_ATTEMPT:
			getJMenuItemDisable().setEnabled(true);
			break;

		default:
			throw new IllegalStateException("Found an nonexistent state for a LMControlArea object, value = " + getLoadControlArea().getControlAreaState().intValue() );
	}


}

}
