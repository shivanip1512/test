package com.cannontech.loadcontrol.popup;

/**
 * Insert the type's description here.
 * Creation date: (9/28/00 3:40:06 PM)
 * @author: 
 */


import com.cannontech.loadcontrol.data.LMGroupBase;
import com.cannontech.loadcontrol.gui.manualentry.LMGroupInfoPanel;

public class GroupPopUpMenu extends javax.swing.JPopupMenu implements java.awt.event.ActionListener
{



	
	
	private LMGroupBase loadControlGroup = null;
	private javax.swing.JMenuItem jMenuItemCycle = null;
	private javax.swing.JMenuItem jMenuItemDisable = null;
	private javax.swing.JMenuItem jMenuItemRestore = null;
	private javax.swing.JMenuItem jMenuItemShed = null;

/**
 * GroupPopUpMenu constructor comment.
 */
public GroupPopUpMenu() 
{
	super();

	initialize();
}
/**
 * GroupPopUpMenu constructor comment.
 * @param label java.lang.String
 */
public GroupPopUpMenu(String label) 
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
	if( e.getSource() == getJMenuItemShed() )
		executeShed(e);
	if( e.getSource() == getJMenuItemRestore() )
		executeRestore(e);
	if( e.getSource() == getJMenuItemCycle() )
		executeCycle(e);
	if( e.getSource() == getJMenuItemDisable() )
		executeDisableEnable(e);

}
/**
 * Insert the method's description here.
 * Creation date: (9/28/00 3:43:40 PM)
 * @param e java.awt.event.ActionEvent
 */
private void executeCycle(java.awt.event.ActionEvent e) 
{
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
 * Creation date: (9/28/00 3:43:40 PM)
 * @param e java.awt.event.ActionEvent
 */
private void executeRestore(java.awt.event.ActionEvent e) 
{
}
/**
 * Insert the method's description here.
 * Creation date: (9/28/00 3:43:40 PM)
 * @param e java.awt.event.ActionEvent
 */
private void executeShed(java.awt.event.ActionEvent e) 
{

}
/**
 * Insert the method's description here.
 * Creation date: (4/17/2001 1:56:43 PM)
 * @return javax.swing.JMenuItem
 */
private javax.swing.JMenuItem getJMenuItemCycle() 
{
	if (jMenuItemCycle == null) 
	{
		try 
		{
			jMenuItemCycle = new javax.swing.JMenuItem();
			jMenuItemCycle.setName("JMenuItemCycle");
			jMenuItemCycle.setMnemonic('y');
			jMenuItemCycle.setText("Cycle");
			jMenuItemCycle.setActionCommand("jMenuItemCycle");
		} 
		catch (java.lang.Throwable ivjExc) 
		{
			handleException(ivjExc);
		}
	}

	return jMenuItemCycle;
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
 * Creation date: (4/17/2001 1:56:43 PM)
 * @return javax.swing.JMenuItem
 */
private javax.swing.JMenuItem getJMenuItemRestore() 
{
	if (jMenuItemRestore == null) 
	{
		try 
		{
			jMenuItemRestore = new javax.swing.JMenuItem();
			jMenuItemRestore.setName("JMenuItemRestore");
			jMenuItemRestore.setMnemonic('t');
			jMenuItemRestore.setText("Restore");
			jMenuItemRestore.setActionCommand("jMenuItemRestore");
		} 
		catch (java.lang.Throwable ivjExc) 
		{
			handleException(ivjExc);
		}
	}

	return jMenuItemRestore;
}
/**
 * Insert the method's description here.
 * Creation date: (4/17/2001 1:56:43 PM)
 * @return javax.swing.JMenuItem
 */
private javax.swing.JMenuItem getJMenuItemShed() 
{
	if (jMenuItemShed == null) 
	{
		try 
		{
			jMenuItemShed = new javax.swing.JMenuItem();
			jMenuItemShed.setName("JMenuItemShed");
			jMenuItemShed.setMnemonic('s');
			jMenuItemShed.setText("Shed");
			jMenuItemShed.setActionCommand("jMenuItemShed");
		} 
		catch (java.lang.Throwable ivjExc) 
		{
			handleException(ivjExc);
		}
	}

	return jMenuItemShed;
}
/**
 * Insert the method's description here.
 * Creation date: (9/28/00 3:50:46 PM)
 * @return LMGroupBase
 */
public LMGroupBase getLoadControlGroup() 
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
	exception.printStackTrace(System.out);
}
/**
 * Initializes connections
 * @exception java.lang.Exception The exception description.
 */
private void initConnections() throws java.lang.Exception 
{
	getJMenuItemShed().addActionListener( this );
	getJMenuItemCycle().addActionListener( this );
	getJMenuItemRestore().addActionListener( this );
	getJMenuItemDisable().addActionListener( this );
}
/**
 * Initialize the class.
 */
private void initialize() 
{
	try 
	{
		setName("GroupPopUp");
		setBorderPainted( true );

		add(getJMenuItemShed(), getJMenuItemShed().getName());
		add(getJMenuItemRestore(), getJMenuItemRestore().getName());
		add(getJMenuItemCycle(), getJMenuItemCycle().getName());
		add(getJMenuItemDisable(), getJMenuItemDisable().getName());
		
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
	loadControlGroup = newGroup;

	if( getLoadControlGroup() == null )
		return;
		
	/*if( getLoadControlProgram() instanceof LMProgramCurtailment )
	{
		processCurtailProgram( (LMProgramCurtailment)getLoadControlProgram() );
	}
	else if( getLoadControlProgram() instanceof LMProgramDirect )
	{
		processDirectProgram( (LMProgramDirect)getLoadControlProgram() );
	}*/


	//lastly, check for disablement
	if( getLoadControlGroup().getDisableFlag().booleanValue() )
	{
		getJMenuItemCycle().setEnabled(false);
		getJMenuItemRestore().setEnabled(false);
		getJMenuItemShed().setEnabled(false);
		
		getJMenuItemDisable().setText("Enable");
	}
	else
	{
		getJMenuItemDisable().setText("Disable");
	}

}
}
