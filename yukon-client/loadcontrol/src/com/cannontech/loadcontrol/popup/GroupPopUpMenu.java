package com.cannontech.loadcontrol.popup;

/**
 * Insert the type's description here.
 * Creation date: (9/28/00 3:40:06 PM)
 * @author: 
 */
import javax.swing.JOptionPane;

import com.cannontech.common.gui.util.OkCancelDialog;
import com.cannontech.common.util.CtiUtilities;
import com.cannontech.common.util.SwingUtil;
import com.cannontech.loadcontrol.LCUtils;
import com.cannontech.loadcontrol.LoadControlClientConnection;
import com.cannontech.messaging.message.loadcontrol.CommandMessage;
import com.cannontech.messaging.message.loadcontrol.data.GroupBase;
import com.cannontech.messaging.message.loadcontrol.data.GroupExpresscom;
import com.cannontech.messaging.message.loadcontrol.data.GroupRipple;
import com.cannontech.messaging.message.loadcontrol.data.GroupVersacom;
import com.cannontech.messaging.message.loadcontrol.data.GroupPoint;

public class GroupPopUpMenu extends javax.swing.JPopupMenu implements java.awt.event.ActionListener
{
	private GroupBase loadControlGroup = null;

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
		OkCancelDialog dialog = new OkCancelDialog(SwingUtil.getParentFrame(this), "Enter Values", true, panel);
		
		dialog.setLocationRelativeTo(this);
		dialog.show();

		Object val = null;		

		if( dialog.getButtonPressed() == OkCancelDialog.OK_PRESSED )
		{
			Object obj = panel.getValue(null);
			
			if( obj instanceof CommandMessage )
			{
				CommandMessage cmd = (CommandMessage)obj;
				cmd.setCommand( CommandMessage.TRUE_CYCLE_GROUP );
				cmd.setYukonId( getLoadControlGroup().getYukonId().intValue() );
			
				LoadControlClientConnection.getInstance().write( cmd );
			}
			else
				throw new IllegalArgumentException(
					"Should only return " + CommandMessage.class.getName() +
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
		OkCancelDialog dialog = new OkCancelDialog(SwingUtil.getParentFrame(this), "Enter Values", true, panel);
		
		dialog.setLocationRelativeTo(this);
		dialog.show();

		Object val = null;		

		if( dialog.getButtonPressed() == OkCancelDialog.OK_PRESSED )
		{
			Object obj = panel.getValue(null);
			
			if( obj instanceof CommandMessage )
			{
				CommandMessage cmd = (CommandMessage)obj;
				cmd.setCommand( CommandMessage.SMART_CYCLE_GROUP );
				cmd.setYukonId( getLoadControlGroup().getYukonId().intValue() );
			
				LoadControlClientConnection.getInstance().write( cmd );
			}
			else
				throw new IllegalArgumentException(
					"Should only return " + CommandMessage.class.getName() +
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
					new CommandMessage( CommandMessage.ENABLE_GROUP,
						 				getLoadControlGroup().getYukonId().intValue(),
						 				0, 0.0) );
		}
		else
		{
			//send a message to the server telling it to DISABLE this group	
			LoadControlClientConnection.getInstance().write(
					new CommandMessage( CommandMessage.DISABLE_GROUP,
						 				getLoadControlGroup().getYukonId().intValue(),
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
				new CommandMessage( CommandMessage.CONFIRM_GROUP,
					 				getLoadControlGroup().getYukonId().intValue(),
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
				new CommandMessage( CommandMessage.RESTORE_GROUP,
					 				getLoadControlGroup().getYukonId().intValue(),
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
			(getLoadControlGroup() instanceof GroupRipple
			  ? new Object[] { new StringBuffer("(Ripple Groups use programmed times in switch)") }
			  : LCUtils.SHED_STRS), 
			null );


		if( val != null )
		{
			int seconds = 0;
			if( val instanceof String )  //a StringBuffer will be here if RippleGroup is used
				seconds = SwingUtil.getIntervalSecondsValue(val.toString()).intValue();

			//send a message to the server telling it to SHED this group
			LoadControlClientConnection.getInstance().write(
				new CommandMessage( CommandMessage.SHED_GROUP,
					 				getLoadControlGroup().getYukonId().intValue(),
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
				jMenuItemTrueCycle.setText(CommandMessage.CMD_STRS[CommandMessage.TRUE_CYCLE_GROUP] + "...");
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
				jMenuItemSmartCycle.setText( CommandMessage.CMD_STRS[CommandMessage.SMART_CYCLE_GROUP] + "...");
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
				jMenuItemConfirm.setText(CommandMessage.CMD_STRS[CommandMessage.CONFIRM_GROUP]);
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
				jMenuItemDisable.setText( CommandMessage.CMD_STRS[CommandMessage.DISABLE_GROUP] );
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
				jMenuItemRestore.setText(CommandMessage.CMD_STRS[CommandMessage.RESTORE_GROUP]);
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
				jMenuItemShed.setText(CommandMessage.CMD_STRS[CommandMessage.SHED_GROUP] + "...");
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
	public GroupBase getLoadControlGroup() 
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
	 * @param GroupBase
	 */
	public void setLoadControlGroup(GroupBase newGroup) 
	{
		loadControlGroup = newGroup;
	
		if( getLoadControlGroup() == null )
			return;
		

		//handle any disablement text
		getJMenuItemDisable().setText(
			getLoadControlGroup().getDisableFlag().booleanValue() 
			? CommandMessage.CMD_STRS[CommandMessage.ENABLE_GROUP]
			: CommandMessage.CMD_STRS[CommandMessage.DISABLE_GROUP] );


		//only ExpressCom groups allowed
		getJMenuItemTrueCycle().setEnabled( 
				!getLoadControlGroup().getDisableFlag().booleanValue()
				&& (getLoadControlGroup() instanceof GroupExpresscom) );
		

		//only ExpressCom & Versacom groups allowed
		getJMenuItemSmartCycle().setEnabled( 
				!getLoadControlGroup().getDisableFlag().booleanValue()
				&& 
				(getLoadControlGroup() instanceof GroupExpresscom
				 || getLoadControlGroup() instanceof GroupVersacom) );

		
		getJMenuItemConfirm().setEnabled( !getLoadControlGroup().getDisableFlag().booleanValue() );
		
		
		//all groups except PointGroup
		getJMenuItemRestore().setEnabled( 
				!getLoadControlGroup().getDisableFlag().booleanValue()
				&& !(getLoadControlGroup() instanceof GroupPoint) );
		
		
		//all groups except PointGroup
		getJMenuItemShed().setEnabled(
				!getLoadControlGroup().getDisableFlag().booleanValue()
				&& !(getLoadControlGroup() instanceof GroupPoint) );	
	}

}
