package com.cannontech.loadcontrol.eexchange.popupmenu;

/**
 * Insert the type's description here.
 * Creation date: (1/21/2001 4:40:03 PM)
 * @author: 
 */
import com.cannontech.loadcontrol.eexchange.datamodels.OfferRowData;

public class OfferPopUpMenu extends javax.swing.JPopupMenu implements java.awt.event.ActionListener
{
	//private LMProgramEnergyExchange exchangeProgram = null;
	private OfferRowData selectedOffer = null;
	//private javax.swing.JMenuItem jMenuItemDisable = null;
	private javax.swing.JMenuItem jMenuItemOpenClose = null;
	private javax.swing.JMenuItem jMenuItemCancel = null;
	
	private javax.swing.JMenuItem jMenuItemCreateRevision = null;
	private javax.swing.JMenuItem jMenuItemViewRevisions = null;
	private javax.swing.JMenuItem jMenuItemViewOffer = null;
/**
 * ProgramPopUpMenu constructor comment.
 */
public OfferPopUpMenu() 
{
	super();
	initialize();
}
/**
 * ProgramPopUpMenu constructor comment.
 * @param label java.lang.String
 */
public OfferPopUpMenu(String label)
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
	if( e.getSource() == getJMenuItemOpenClose() ) 
		jMenuItemOpenClose_ActionPerformed(e);

	if( e.getSource() == getJMenuItemViewOffer() )
		jMenuItemViewOffer_ActionPerformed(e);

	if( e.getSource() == getJMenuItemViewRevisions() )
		jMenuItemViewRevisions_ActionPerformed(e);

	if( e.getSource() == getJMenuItemCancel() )
		jMenuItemCancel_ActionPerformed(e);

	if( e.getSource() == getJMenuItemCreateRevision() )
		jMenuItemCreateRevision_ActionPerformed(e);

}
/**
 * Insert the method's description here.
 * Creation date: (1/15/2001 9:20:50 AM)
 * @return javax.swing.JMenuItem
 */
private javax.swing.JMenuItem getJMenuItemCancel() 
{
	if( jMenuItemCancel == null )
	{
		try 
		{
			jMenuItemCancel = new javax.swing.JMenuItem();
			jMenuItemCancel.setName("JMenuItemCancel");
			jMenuItemCancel.setMnemonic('c');
			jMenuItemCancel.setText("Cancel");
			jMenuItemCancel.setActionCommand("jMenuItemCancel");
		} 
		catch (java.lang.Throwable ivjExc) 
		{
			handleException(ivjExc);
		}
	}
	
	return jMenuItemCancel;
}
/**
 * Insert the method's description here.
 * Creation date: (8/2/2001 11:11:41 AM)
 * @return javax.swing.JMenuItem
 */
private javax.swing.JMenuItem getJMenuItemCreateRevision() 
{
	if (jMenuItemCreateRevision == null) 
	{
		try 
		{
			jMenuItemCreateRevision = new javax.swing.JMenuItem();
			jMenuItemCreateRevision.setName("JMenuItemCreateRevision");
			jMenuItemCreateRevision.setMnemonic('c');
			jMenuItemCreateRevision.setText("Create Revision");
			jMenuItemCreateRevision.setActionCommand("jMenuItemCreateRevision");
		} 
		catch (java.lang.Throwable ivjExc) 
		{
			handleException(ivjExc);
		}
	}

	return jMenuItemCreateRevision;
}
/**
 * Insert the method's description here.
 * Creation date: (1/15/2001 9:20:50 AM)
 * @return javax.swing.JMenuItem
 */
private javax.swing.JMenuItem getJMenuItemOpenClose() 
{
	if (jMenuItemOpenClose == null) 
	{
		try 
		{
			jMenuItemOpenClose = new javax.swing.JMenuItem();
			jMenuItemOpenClose.setName("JMenuItemOpenClose");
			jMenuItemOpenClose.setMnemonic('o');
			jMenuItemOpenClose.setText("Open");
			jMenuItemOpenClose.setActionCommand("jMenuItemOpenClose");
		} 
		catch (java.lang.Throwable ivjExc) 
		{
			handleException(ivjExc);
		}
	}
	
	return jMenuItemOpenClose;
}
/**
 * Insert the method's description here.
 * Creation date: (8/2/2001 11:11:41 AM)
 * @return javax.swing.JMenuItem
 */
private javax.swing.JMenuItem getJMenuItemViewOffer() 
{
	if (jMenuItemViewOffer == null) 
	{
		try 
		{
			jMenuItemViewOffer = new javax.swing.JMenuItem();
			jMenuItemViewOffer.setName("JMenuItemViewOffer");
			jMenuItemViewOffer.setMnemonic('v');
			jMenuItemViewOffer.setText("View Offer");
			jMenuItemViewOffer.setActionCommand("jMenuItemViewOffer");
		} 
		catch (java.lang.Throwable ivjExc) 
		{
			handleException(ivjExc);
		}
	}
	
	return jMenuItemViewOffer;
}
/**
 * Insert the method's description here.
 * Creation date: (8/2/2001 11:11:41 AM)
 * @return javax.swing.JMenuItem
 */
private javax.swing.JMenuItem getJMenuItemViewRevisions() 
{
	if (jMenuItemViewRevisions == null) 
	{
		try 
		{
			jMenuItemViewRevisions = new javax.swing.JMenuItem();
			jMenuItemViewRevisions.setName("JMenuItemViewRevisions");
			jMenuItemViewRevisions.setMnemonic('r');
			jMenuItemViewRevisions.setText("View Revisions");
			jMenuItemViewRevisions.setActionCommand("jMenuItemViewRevisions");
		} 
		catch (java.lang.Throwable ivjExc) 
		{
			handleException(ivjExc);
		}
	}

	return jMenuItemViewRevisions;
}
/**
 * Insert the method's description here.
 * Creation date: (8/7/2001 11:33:47 AM)
 * @return com.cannontech.loadcontrol.eexchange.datamodels.OfferRowData
 */
public com.cannontech.loadcontrol.eexchange.datamodels.OfferRowData getSelectedOffer() {
	return selectedOffer;
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
	getJMenuItemOpenClose().addActionListener(this);
	getJMenuItemViewOffer().addActionListener(this);
	getJMenuItemViewRevisions().addActionListener(this);
	getJMenuItemCancel().addActionListener(this);
	getJMenuItemCreateRevision().addActionListener(this);
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

		add( getJMenuItemOpenClose(), getJMenuItemOpenClose().getName() );
		add( getJMenuItemCancel(), getJMenuItemCancel().getName() );
		add( getJMenuItemCreateRevision(), getJMenuItemCreateRevision().getName() );
		add( getJMenuItemViewOffer(), getJMenuItemViewOffer().getName() );
		add( getJMenuItemViewRevisions(), getJMenuItemViewRevisions().getName() );


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
private void jMenuItemCancel_ActionPerformed(java.awt.event.ActionEvent actionEvent) 
{
	int res = javax.swing.JOptionPane.showConfirmDialog(this, "Are you sure you want to cancel this offer?", "Offer Cancelation",
		javax.swing.JOptionPane.OK_CANCEL_OPTION, javax.swing.JOptionPane.QUESTION_MESSAGE );

	if( res == javax.swing.JOptionPane.OK_OPTION )
	{
		com.cannontech.loadcontrol.messages.LMEnergyExchangeControlMsg s = new com.cannontech.loadcontrol.messages.LMEnergyExchangeControlMsg();

		s.setExpirationDateTime( getSelectedOffer().getCurrentRevision().getOfferExpirationDateTime() );
		s.setNotificationDateTime( getSelectedOffer().getCurrentRevision().getNotificationDateTime() );
		s.setOfferDate( getSelectedOffer().getOwnerOffer().getOfferDate() );
		
		s.setYukonID( getSelectedOffer().getOwnerOffer().getYukonID() );
		s.setAmountRequested( new Double[0] ); //cant be null
		s.setPricesOffered( new Integer[0] ); //cant be null

		s.setMessage("Offer canceled by a TDC operator named : " + com.cannontech.common.util.CtiUtilities.getUserName() );
		s.setAdditionalInfo("Offer canceled for program : " + getSelectedOffer().getEnergyExchangeProgram().getYukonName() );
		s.setCommand( new Integer(com.cannontech.loadcontrol.messages.LMEnergyExchangeControlMsg.CANCEL_OFFER) );

		//write the message out
		com.cannontech.loadcontrol.LoadControlClientConnection.getInstance().write(s)	;
	}

	return;
}
/**
 * Comment
 */
private void jMenuItemCreateRevision_ActionPerformed(java.awt.event.ActionEvent actionEvent)
{
	
	if( getSelectedOffer() == null )
	{
		getJMenuItemCreateRevision().setEnabled( getSelectedOffer() != null );
		return;
	}
	
	java.awt.Cursor savedCursor = com.cannontech.common.util.CtiUtilities.getParentFrame(this.getInvoker()).getCursor();
	com.cannontech.common.util.CtiUtilities.getParentFrame(this.getInvoker()).setCursor( new java.awt.Cursor(java.awt.Cursor.WAIT_CURSOR) );
	final javax.swing.JDialog dialog = new javax.swing.JDialog( com.cannontech.common.util.CtiUtilities.getParentFrame(this) );
	
	try
	{	
		com.cannontech.loadcontrol.eexchange.views.JPanelCreateOffer panel = new com.cannontech.loadcontrol.eexchange.views.JPanelCreateOffer()
		{
			public void exit()
			{
				dialog.dispose();
			}

		};

		panel.setMode( panel.MODE_REVISE );
		panel.setOfferData( getSelectedOffer() );
		
		dialog.setContentPane(panel);
		dialog.setTitle("Create Revision for Offer ID : " + getSelectedOffer().getOwnerOffer().getOfferID() );
		dialog.setModal(false);
		dialog.setSize(450, 530);
		dialog.pack();
		dialog.setLocationRelativeTo(this.getInvoker());
	}
	finally
	{
		com.cannontech.common.util.CtiUtilities.getParentFrame(this.getInvoker()).setCursor( savedCursor );
	}
	
	dialog.show();

	return;
}
/**
 * Comment
 */
private void jMenuItemOpenClose_ActionPerformed(java.awt.event.ActionEvent actionEvent) 
{
	if( getJMenuItemOpenClose().getText().equalsIgnoreCase("Close") )
	{

		int res = javax.swing.JOptionPane.showConfirmDialog(this, "Are you sure you want to close this offer?", "Offer Stop",
			javax.swing.JOptionPane.OK_CANCEL_OPTION, javax.swing.JOptionPane.QUESTION_MESSAGE );

		if( res == javax.swing.JOptionPane.OK_OPTION )
		{
			com.cannontech.loadcontrol.messages.LMEnergyExchangeControlMsg s = new com.cannontech.loadcontrol.messages.LMEnergyExchangeControlMsg();

			s.setExpirationDateTime( getSelectedOffer().getCurrentRevision().getOfferExpirationDateTime() );
			s.setNotificationDateTime( getSelectedOffer().getCurrentRevision().getNotificationDateTime() );
			s.setOfferDate( getSelectedOffer().getOwnerOffer().getOfferDate() );
			
			s.setYukonID( getSelectedOffer().getOwnerOffer().getYukonID() );
			s.setAmountRequested( new Double[0] ); //cant be null
			s.setPricesOffered( new Integer[0] ); //cant be null

			s.setMessage("Offer closed by a TDC operator named : " + com.cannontech.common.util.CtiUtilities.getUserName() );
			s.setAdditionalInfo("Offer closed for program : " + getSelectedOffer().getEnergyExchangeProgram().getYukonName() );
			s.setCommand( new Integer(com.cannontech.loadcontrol.messages.LMEnergyExchangeControlMsg.CLOSE_OFFER) );

			//write the message out
			com.cannontech.loadcontrol.LoadControlClientConnection.getInstance().write(s)	;
		}
	}
		
	return;
}
/**
 * Comment
 */
private void jMenuItemViewOffer_ActionPerformed(java.awt.event.ActionEvent actionEvent)
{
	
	java.awt.Cursor savedCursor = com.cannontech.common.util.CtiUtilities.getParentFrame(this.getInvoker()).getCursor();
	com.cannontech.common.util.CtiUtilities.getParentFrame(this.getInvoker()).setCursor( new java.awt.Cursor(java.awt.Cursor.WAIT_CURSOR) );
	final javax.swing.JDialog dialog = new javax.swing.JDialog( com.cannontech.common.util.CtiUtilities.getParentFrame(this.getInvoker()) );
	
	try
	{	
		com.cannontech.loadcontrol.eexchange.views.JPanelCreateOffer panel = new com.cannontech.loadcontrol.eexchange.views.JPanelCreateOffer()
		{
			public void exit()
			{
				dialog.dispose();
			}

		};

		panel.setEditable(false);
		panel.setOfferData( getSelectedOffer() );
		
		dialog.setContentPane(panel);
		dialog.setTitle("View OfferID : " + getSelectedOffer().getOfferIDString());
		dialog.setModal(false);
		dialog.setSize(450, 530);
		dialog.pack();
		dialog.setLocationRelativeTo(this.getInvoker());
	}
	finally
	{
		com.cannontech.common.util.CtiUtilities.getParentFrame(this.getInvoker()).setCursor( savedCursor );
	}
	
	dialog.show();

	return;
}
/**
 * Comment
 */
private void jMenuItemViewRevisions_ActionPerformed(java.awt.event.ActionEvent actionEvent)
{
	java.awt.Cursor savedCursor = com.cannontech.common.util.CtiUtilities.getParentFrame(this.getInvoker()).getCursor();
	com.cannontech.common.util.CtiUtilities.getParentFrame(this.getInvoker()).setCursor( new java.awt.Cursor(java.awt.Cursor.WAIT_CURSOR) );
	final javax.swing.JDialog dialog = new javax.swing.JDialog( com.cannontech.common.util.CtiUtilities.getParentFrame(this.getInvoker()) );
	
	try
	{						
		com.cannontech.loadcontrol.eexchange.views.JPanelViewRevisions panel = new com.cannontech.loadcontrol.eexchange.views.JPanelViewRevisions()
		{
			public void exit()
			{
				dialog.dispose();
			}

		};

		panel.setSelectedOfferID( getSelectedOffer().getOwnerOffer().getOfferID().longValue() );
		dialog.setContentPane(panel);
		dialog.setTitle("Revision History");
		dialog.setModal(true);
		dialog.setSize(560, 530);
		dialog.pack();
		dialog.setLocationRelativeTo(this.getInvoker());
	}
	finally
	{
		com.cannontech.common.util.CtiUtilities.getParentFrame(this.getInvoker()).setCursor( savedCursor );
	}

	dialog.show();

	return;
}
/**
 * Insert the method's description here.
 * Creation date: (8/7/2001 11:33:47 AM)
 * @param newSelectedOffer com.cannontech.loadcontrol.eexchange.datamodels.OfferRowData
 */
public void setSelectedOffer(com.cannontech.loadcontrol.eexchange.datamodels.OfferRowData newSelectedOffer) 
{
	selectedOffer = newSelectedOffer;

	if( getSelectedOffer() == null )
		return;

	syncButtons();

	//lastly, check for disablement
	if( getSelectedOffer().getEnergyExchangeProgram().getDisableFlag().booleanValue() )
	{
		getJMenuItemOpenClose().setEnabled(false);
		getJMenuItemCancel().setEnabled(false);		
	}


}
/**
 * Insert the method's description here.
 * Creation date: (4/9/2001 5:26:53 PM)
 * @param program com.cannontech.loadcontrol.data.LMProgramEnergyExchange
 */
private void syncButtons()
{
	if( getSelectedOffer().getOwnerOffer().getRunStatus().equalsIgnoreCase(
		 com.cannontech.loadcontrol.data.LMEnergyExchangeOffer.RUN_CANCELED)
		 || getSelectedOffer().getOwnerOffer().getRunStatus().equalsIgnoreCase(
		 com.cannontech.loadcontrol.data.LMEnergyExchangeOffer.RUN_COMPLETED)
		 || getSelectedOffer().getOwnerOffer().getRunStatus().equalsIgnoreCase(
		 com.cannontech.loadcontrol.data.LMEnergyExchangeOffer.RUN_NULL) 
		 || getSelectedOffer().getOwnerOffer().getRunStatus().equalsIgnoreCase(
		 com.cannontech.loadcontrol.data.LMEnergyExchangeOffer.RUN_CLOSING) )
	{
			getJMenuItemOpenClose().setText("Open");
			getJMenuItemOpenClose().setEnabled(true);			
			getJMenuItemCancel().setEnabled(false);
	}
	else if( getSelectedOffer().getOwnerOffer().getRunStatus().equalsIgnoreCase(
		 com.cannontech.loadcontrol.data.LMEnergyExchangeOffer.RUN_CURTAILMENT_ACTIVE)
		 || getSelectedOffer().getOwnerOffer().getRunStatus().equalsIgnoreCase(
		 com.cannontech.loadcontrol.data.LMEnergyExchangeOffer.RUN_CURTAILMENT_PENDING)
		 || getSelectedOffer().getOwnerOffer().getRunStatus().equalsIgnoreCase(
		 com.cannontech.loadcontrol.data.LMEnergyExchangeOffer.RUN_SCHEDULED)
		 || getSelectedOffer().getOwnerOffer().getRunStatus().equalsIgnoreCase(
		 com.cannontech.loadcontrol.data.LMEnergyExchangeOffer.RUN_OPEN) )
	{
	
			getJMenuItemOpenClose().setText("Close");
			getJMenuItemOpenClose().setEnabled(true);			
			getJMenuItemCancel().setEnabled(true);
	}
	else
		throw new IllegalStateException("Found an unexpected state for a LMProgram object, value = " + getSelectedOffer().getEnergyExchangeProgram().getProgramStatus().intValue() );


}
}
