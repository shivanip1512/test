package com.cannontech.loadcontrol.popup;

/**
 * Insert the type's description here.
 * Creation date: (9/28/00 3:40:06 PM)
 * @author: 
 */
import javax.swing.JOptionPane;

import com.cannontech.common.gui.util.OkCancelDialog;
import com.cannontech.common.util.CtiUtilities;
import com.cannontech.loadcontrol.LCUtils;
import com.cannontech.loadcontrol.LoadControlClientConnection;
import com.cannontech.loadcontrol.data.LMGroupBase;
import com.cannontech.loadcontrol.data.LMGroupExpresscom;
import com.cannontech.loadcontrol.data.LMGroupPoint;
import com.cannontech.loadcontrol.data.LMGroupRipple;
import com.cannontech.loadcontrol.data.LMGroupVersacom;
import com.cannontech.loadcontrol.messages.LMCommand;

public class GroupPopUpMenu extends javax.swing.JPopupMenu implements java.awt.event.ActionListener
{
	private LMGroupBase loadControlGroup = null;

	private javax.swing.JMenuItem jMenuItemTrueCycle = null;
	private javax.swing.JMenuItem jMenuItemSmartCycle = null;
	private javax.swing.JMenuItem jMenuItemConfirm = null;
	private javax.swing.JMenuItem jMenuItemDisable = null;
	private javax.swing.JMenuItem jMenuItemRestore = null;
	private javax.swing.JMenuItem jMenuItemShed = null;


	/**
	 * GroupPopUpMenu constructor comment.
	 */
	public GroupPopUpMenu() 
	{
		this(null);
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
	
	
		if( e.getSource() == getJMenuItemTrueCycle() )
			executeTrueCycle(e);
	
		if( e.getSource() == getJMenuItemSmartCycle() )
			executeSmartCycle(e);
	
		if( e.getSource() == getJMenuItemDisable() )
			executeDisableEnable(e);
	
		if( e.getSource() == getJMenuItemConfirm() )
			executeConfirm(e);
	}
	/**
	 * Insert the method's description here.
	 * Creation date: (9/28/00 3:43:40 PM)
	 * @param e java.awt.event.ActionEvent
	 */
	private void executeTrueCycle(java.awt.event.ActionEvent e) 
	{
		GroupMsgPanel panel = new GroupMsgPanel();
		OkCancelDialog dialog = new OkCancelDialog(
				CtiUtilities.getParentFrame(this), 
				"Enter Values", 
				true, 
				panel );
		
		dialog.setLocationRelativeTo(this);
		dialog.show();

		Object val = null;		

		if( dialog.getButtonPressed() == OkCancelDialog.OK_PRESSED )
		{
			Object obj = panel.getValue(null);
			
			if( obj instanceof LMCommand )
			{
				LMCommand cmd = (LMCommand)obj;
				cmd.setCommand( LMCommand.TRUE_CYCLE_GROUP );
				cmd.setYukonID( getLoadControlGroup().getYukonID().intValue() );
			
				LoadControlClientConnection.getInstance().write( cmd );
			}
			else
				throw new IllegalArgumentException(
					"Should only return " + LMCommand.class.getName() +
					" instances, instead reuturned a " + obj.getClass().getName() + 
					" (very unbecoming)");	
		}
	}


	/**
	 * Insert the method's description here.
	 * Creation date: (9/28/00 3:43:40 PM)
	 * @param e java.awt.event.ActionEvent
	 */
	private void executeSmartCycle(java.awt.event.ActionEvent e) 
	{
		GroupMsgPanel panel = new GroupMsgPanel();
		OkCancelDialog dialog = new OkCancelDialog(
				CtiUtilities.getParentFrame(this), 
				"Enter Values", 
				true, 
				panel );
		
		dialog.setLocationRelativeTo(this);
		dialog.show();

		Object val = null;		

		if( dialog.getButtonPressed() == OkCancelDialog.OK_PRESSED )
		{
			Object obj = panel.getValue(null);
			
			if( obj instanceof LMCommand )
			{
				LMCommand cmd = (LMCommand)obj;
				cmd.setCommand( LMCommand.SMART_CYCLE_GROUP );
				cmd.setYukonID( getLoadControlGroup().getYukonID().intValue() );
			
				LoadControlClientConnection.getInstance().write( cmd );
			}
			else
				throw new IllegalArgumentException(
					"Should only return " + LMCommand.class.getName() +
					" instances, instead reuturned a " + obj.getClass().getName() + 
					" (very unbecoming)");	
		}
				
	}
	
	/**
	 * Insert the method's description here.
	 * Creation date: (9/28/00 3:43:40 PM)
	 * @param e java.awt.event.ActionEvent
	 */
	private void executeDisableEnable(java.awt.event.ActionEvent e) 
	{
		if( getLoadControlGroup().getDisableFlag().booleanValue() )
		{
			//send a message to the server telling it to ENABLE this group
			LoadControlClientConnection.getInstance().write(
					new LMCommand( LMCommand.ENABLE_GROUP,
						 				getLoadControlGroup().getYukonID().intValue(),
						 				0, 0.0) );
		}
		else
		{
			//send a message to the server telling it to DISABLE this group	
			LoadControlClientConnection.getInstance().write(
					new LMCommand( LMCommand.DISABLE_GROUP,
						 				getLoadControlGroup().getYukonID().intValue(),
						 				0, 0.0) );
		}		
	}

	
	/**
	 * Insert the method's description here.
	 * Creation date: (9/28/00 3:43:40 PM)
	 * @param e java.awt.event.ActionEvent
	 */
	private void executeConfirm(java.awt.event.ActionEvent e) 
	{
		//send a message to the server telling it to CONFIRM this group
		LoadControlClientConnection.getInstance().write(
				new LMCommand( LMCommand.CONFIRM_GROUP,
					 				getLoadControlGroup().getYukonID().intValue(),
					 				0, 
					 				0.0,
					 				0,
					 				0) ); //this auxid will be used for the alt routeID soon		
	}


	/**
	 * Insert the method's description here.
	 * Creation date: (9/28/00 3:43:40 PM)
	 * @param e java.awt.event.ActionEvent
	 */
	private void executeRestore(java.awt.event.ActionEvent e) 
	{
		//send a message to the server telling it to RESTORE this group
		LoadControlClientConnection.getInstance().write(
				new LMCommand( LMCommand.RESTORE_GROUP,
					 				getLoadControlGroup().getYukonID().intValue(),
					 				0,
					 				0.0) );
	}
	
	/**
	 * Insert the method's description here.
	 * Creation date: (9/28/00 3:43:40 PM)
	 * @param e java.awt.event.ActionEvent
	 */
	private void executeShed(java.awt.event.ActionEvent e) 
	{
		Object val = JOptionPane.showInputDialog( 
			this, "Shed Duration: ",
			"Enter Shed Time", JOptionPane.QUESTION_MESSAGE,
			null, 
			(getLoadControlGroup() instanceof LMGroupRipple
			  ? new Object[] { new StringBuffer("(Ripple Groups use programmed times in switch)") }
			  : LCUtils.SHED_STRS), 
			null );


		if( val != null )
		{
			int seconds = 0;
			if( val instanceof String )  //a StringBuffer will be here if RippleGroup is used
				seconds = CtiUtilities.getIntervalSecondsValue(val.toString()).intValue();

			//send a message to the server telling it to SHED this group
			LoadControlClientConnection.getInstance().write(
				new LMCommand( LMCommand.SHED_GROUP,
					 				getLoadControlGroup().getYukonID().intValue(),
					 				seconds, //shed time in seconds
					 				0.0,
					 				0,
					 				0) ); //this auxid will be used for the alt routeID soon
		}		

	}
	
	/**
	 * Insert the method's description here.
	 * Creation date: (4/17/2001 1:56:43 PM)
	 * @return javax.swing.JMenuItem
	 */
	private javax.swing.JMenuItem getJMenuItemTrueCycle() 
	{
		if( jMenuItemTrueCycle == null ) 
		{
			try 
			{
				jMenuItemTrueCycle = new javax.swing.JMenuItem();
				jMenuItemTrueCycle.setName("JMenuItemTrueCycle");
				jMenuItemTrueCycle.setText(LMCommand.CMD_STRS[LMCommand.TRUE_CYCLE_GROUP] + "...");
				jMenuItemTrueCycle.setMnemonic('y');
				jMenuItemTrueCycle.setActionCommand("jMenuItemTrueCycle");
			} 
			catch (java.lang.Throwable ivjExc) 
			{
				handleException(ivjExc);
			}
		}
	
		return jMenuItemTrueCycle;
	}
	

	/**
	 * Insert the method's description here.
	 * Creation date: (4/17/2001 1:56:43 PM)
	 * @return javax.swing.JMenuItem
	 */
	private javax.swing.JMenuItem getJMenuItemSmartCycle() 
	{
		if( jMenuItemSmartCycle == null ) 
		{
			try 
			{
				jMenuItemSmartCycle = new javax.swing.JMenuItem();
				jMenuItemSmartCycle.setName("JMenuItemSmartCycle");
				jMenuItemSmartCycle.setText( LMCommand.CMD_STRS[LMCommand.SMART_CYCLE_GROUP] + "...");
				jMenuItemSmartCycle.setMnemonic('m');
				jMenuItemSmartCycle.setActionCommand("jMenuItemSmartCycle");
			} 
			catch (java.lang.Throwable ivjExc) 
			{
				handleException(ivjExc);
			}
		}
	
		return jMenuItemSmartCycle;
	}
	
	/**
	 * Insert the method's description here.
	 * Creation date: (4/17/2001 1:56:43 PM)
	 * @return javax.swing.JMenuItem
	 */
	private javax.swing.JMenuItem getJMenuItemConfirm() 
	{
		if (jMenuItemConfirm == null) 
		{
			try 
			{
				jMenuItemConfirm = new javax.swing.JMenuItem();
				jMenuItemConfirm.setName("JMenuItemConfirm");
				jMenuItemConfirm.setText(LMCommand.CMD_STRS[LMCommand.CONFIRM_GROUP]);
				jMenuItemConfirm.setMnemonic('f');
				jMenuItemConfirm.setActionCommand("jMenuItemConfirm");
			} 
			catch (java.lang.Throwable ivjExc) 
			{
				handleException(ivjExc);
			}
		}
	
		return jMenuItemConfirm;
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
				jMenuItemDisable.setText( LMCommand.CMD_STRS[LMCommand.DISABLE_GROUP] );
				jMenuItemDisable.setMnemonic('s');
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
				jMenuItemRestore.setText(LMCommand.CMD_STRS[LMCommand.RESTORE_GROUP]);
				jMenuItemRestore.setMnemonic('t');
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
				jMenuItemShed.setText(LMCommand.CMD_STRS[LMCommand.SHED_GROUP] + "...");
				jMenuItemShed.setMnemonic('s');
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
		com.cannontech.clientutils.CTILogger.error( exception.getMessage(), exception );;
	}
	/**
	 * Initializes connections
	 * @exception java.lang.Exception The exception description.
	 */
	private void initConnections() throws java.lang.Exception 
	{
		getJMenuItemShed().addActionListener( this );
		getJMenuItemTrueCycle().addActionListener( this );
		getJMenuItemSmartCycle().addActionListener( this );
		getJMenuItemConfirm().addActionListener( this );
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
			
			add(getJMenuItemTrueCycle(), getJMenuItemTrueCycle().getName());
			add(getJMenuItemSmartCycle(), getJMenuItemSmartCycle().getName());

			add(getJMenuItemDisable(), getJMenuItemDisable().getName());
			add(getJMenuItemConfirm(), getJMenuItemConfirm().getName());
			
			
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
		

		//handle any disablement text
		getJMenuItemDisable().setText(
			getLoadControlGroup().getDisableFlag().booleanValue() 
			? LMCommand.CMD_STRS[LMCommand.ENABLE_GROUP]
			: LMCommand.CMD_STRS[LMCommand.DISABLE_GROUP] );


		//only ExpressCom groups allowed
		getJMenuItemTrueCycle().setEnabled( 
				!getLoadControlGroup().getDisableFlag().booleanValue()
				&& (getLoadControlGroup() instanceof LMGroupExpresscom) );
		

		//only ExpressCom & Versacom groups allowed
		getJMenuItemSmartCycle().setEnabled( 
				!getLoadControlGroup().getDisableFlag().booleanValue()
				&& 
				(getLoadControlGroup() instanceof LMGroupExpresscom
				 || getLoadControlGroup() instanceof LMGroupVersacom) );

		
		getJMenuItemConfirm().setEnabled( !getLoadControlGroup().getDisableFlag().booleanValue() );
		
		
		//all groups except PointGroup
		getJMenuItemRestore().setEnabled( 
				!getLoadControlGroup().getDisableFlag().booleanValue()
				&& !(getLoadControlGroup() instanceof LMGroupPoint) );
		
		
		//all groups except PointGroup
		getJMenuItemShed().setEnabled(
				!getLoadControlGroup().getDisableFlag().booleanValue()
				&& !(getLoadControlGroup() instanceof LMGroupPoint) );	
	}

}
