package com.cannontech.dbeditor.editor.device;

import java.util.Collections;
import java.util.List;
import java.util.Vector;

import com.cannontech.common.pao.PaoClass;
import com.cannontech.common.pao.PaoType;
import com.cannontech.common.util.CtiUtilities;
import com.cannontech.database.cache.DefaultDatabaseCache;
import com.cannontech.database.data.device.DeviceTypesFuncs;
import com.cannontech.database.data.lite.LiteComparators;
import com.cannontech.database.data.lite.LiteFactory;
import com.cannontech.database.data.lite.LiteYukonPAObject;
import com.cannontech.database.data.multi.SmartMultiDBPersistent;
import com.cannontech.database.data.pao.DeviceTypes;
import com.cannontech.database.data.pao.YukonPAObject;
import com.cannontech.database.db.pao.PAOExclusion;
import com.cannontech.yukon.IDatabaseCache;

/**
 * This type was created in VisualAge.
 */
public class PAOExclusionEditorPanel extends
		com.cannontech.common.gui.util.DataInputPanel implements
		com.cannontech.common.gui.util.AddRemovePanelListener,
		java.awt.event.ActionListener
{
	private javax.swing.JComboBox ivjJComboBoxFunction = null;
	private javax.swing.JLabel ivjJLabelFunction = null;
	private com.cannontech.common.gui.util.AddRemovePanel ivjAddRemovePanelPAOExcl = null;

	public Vector<LiteYukonPAObject> oldAvailableVector = null;
	public Vector<LiteYukonPAObject> oldExcludedVector = null;

	/*
	 * Constructor WARNING: THIS METHOD WILL BE REGENERATED.
	 */
	public PAOExclusionEditorPanel ()
	{
		super();
		initialize();
	}

	/**
	 * Method to handle events for the ActionListener interface.
	 * @param e java.awt.event.ActionEvent
	 */
	/* WARNING: THIS METHOD WILL BE REGENERATED. */
	public void actionPerformed( java.awt.event.ActionEvent e )
	{
		// user code begin {1}
		// user code end
		if ( e.getSource() == getJComboBoxFunction() )
			connEtoC1( e );
		// user code begin {2}
		// user code end
	}

	/**
	 * This method was created by Cannon Technologies Inc.
	 * @param newEvent java.util.EventObject
	 */
	public void addButtonAction_actionPerformed( java.util.EventObject newEvent )
	{

		if ( newEvent.getSource() == getAddRemovePanelPAOExcl() )
		{
			connEtoC4( newEvent );
		}
	}

	/**
	 * connEtoC1:
	 * (JComboBoxFunction.action.actionPerformed(java.awt.event.ActionEvent) -->
	 * PAOExclusionEditorPanel.fireInputUpdate()V)
	 * @param arg1 java.awt.event.ActionEvent
	 */
	/* WARNING: THIS METHOD WILL BE REGENERATED. */
	private void connEtoC1( java.awt.event.ActionEvent arg1 )
	{
		try
		{
			// user code begin {1}
			// user code end
			this.fireInputUpdate();
			// user code begin {2}
			// user code end
		} catch ( java.lang.Throwable ivjExc )
		{
			// user code begin {3}
			// user code end
			handleException( ivjExc );
		}
	}

	/**
	 * connEtoC4:
	 * (AddRemovePanel.addRemovePanel.addButtonAction_actionPerformed(java.util.EventObject)
	 * --> RepeaterSetupEditorPanel.fireInputUpdate()V)
	 * @param arg1 java.util.EventObject
	 */
	/* WARNING: THIS METHOD WILL BE REGENERATED. */
	private void connEtoC4( java.util.EventObject arg1 )
	{
		try
		{
			// user code begin {1}
			// user code end
			this.fireInputUpdate();
			// user code begin {2}

			// user code end
		} catch ( java.lang.Throwable ivjExc )
		{
			// user code begin {3}
			// user code end
			handleException( ivjExc );
		}
	}

	/**
	 * connEtoC5:
	 * (AddRemovePanelPAOExcl.addRemovePanel.removeButtonAction_actionPerformed(java.util.EventObject)
	 * --> PAOExclusionEditorPanel.fireInputUpdate()V)
	 * @param arg1 java.util.EventObject
	 */
	/* WARNING: THIS METHOD WILL BE REGENERATED. */
	private void connEtoC5( java.util.EventObject arg1 )
	{
		try
		{
			// user code begin {1}
			// user code end
			this.fireInputUpdate();
			// user code begin {2}
			// user code end
		} catch ( java.lang.Throwable ivjExc )
		{
			// user code begin {3}
			// user code end
			handleException( ivjExc );
		}
	}

	/**
	 * Return the AddRemovePanel property value.
	 * @return com.cannontech.common.gui.util.AddRemovePanel
	 */
	/* WARNING: THIS METHOD WILL BE REGENERATED. */
	private com.cannontech.common.gui.util.AddRemovePanel getAddRemovePanelPAOExcl()
	{
		if ( ivjAddRemovePanelPAOExcl == null )
		{
			try
			{
				ivjAddRemovePanelPAOExcl = new com.cannontech.common.gui.util.AddRemovePanel();
				ivjAddRemovePanelPAOExcl.setName( "AddRemovePanelPAOExcl" );
				// user code begin {1}
				ivjAddRemovePanelPAOExcl.setMode( com.cannontech.common.gui.util.AddRemovePanel.TRANSFER_MODE );
				ivjAddRemovePanelPAOExcl.leftListLabelSetText( "Available Items" );
				ivjAddRemovePanelPAOExcl.rightListLabelSetText( "Exclusion Items" );

				// user code end
			} catch ( java.lang.Throwable ivjExc )
			{
				// user code begin {2}
				// user code end
				handleException( ivjExc );
			}
		}
		return ivjAddRemovePanelPAOExcl;
	}

	/**
	 * Return the JComboBoxFunction property value.
	 * @return javax.swing.JComboBox
	 */
	/* WARNING: THIS METHOD WILL BE REGENERATED. */
	private javax.swing.JComboBox getJComboBoxFunction()
	{
		if ( ivjJComboBoxFunction == null )
		{
			try
			{
				ivjJComboBoxFunction = new javax.swing.JComboBox();
				ivjJComboBoxFunction.setName( "JComboBoxFunction" );
				// user code begin {1}

				ivjJComboBoxFunction.addItem( CtiUtilities.STRING_NONE );

				// user code end
			} catch ( java.lang.Throwable ivjExc )
			{
				// user code begin {2}
				// user code end
				handleException( ivjExc );
			}
		}
		return ivjJComboBoxFunction;
	}

	/**
	 * Return the JLabelFunction property value.
	 * @return javax.swing.JLabel
	 */
	/* WARNING: THIS METHOD WILL BE REGENERATED. */
	private javax.swing.JLabel getJLabelFunction()
	{
		if ( ivjJLabelFunction == null )
		{
			try
			{
				ivjJLabelFunction = new javax.swing.JLabel();
				ivjJLabelFunction.setName( "JLabelFunction" );
				ivjJLabelFunction.setFont( new java.awt.Font( "dialog", 0, 12 ) );
				ivjJLabelFunction.setText( "Function:" );
				// user code begin {1}
				// user code end
			} catch ( java.lang.Throwable ivjExc )
			{
				// user code begin {2}
				// user code end
				handleException( ivjExc );
			}
		}
		return ivjJLabelFunction;
	}

	/**
	 * This method was created in VisualAge.
	 * @return java.lang.Object
	 * @param val java.lang.Object
	 */
	@SuppressWarnings( "unchecked" )
	public Object getValue( Object val )
	{
	    
		YukonPAObject pao = (YukonPAObject) val;
		
		// Build up vectors of our new exclusion and available lists
		Vector<LiteYukonPAObject> newExcludedVector = new Vector<LiteYukonPAObject>();
        Vector<LiteYukonPAObject> newAvailableVector = new Vector<LiteYukonPAObject>();
        
		for ( int i = 0; i < getAddRemovePanelPAOExcl().rightListGetModel()	.getSize(); i++ )
		{
			newExcludedVector.add((LiteYukonPAObject)getAddRemovePanelPAOExcl().rightListGetModel().getElementAt(i));
		}
        
        for ( int i = 0; i < getAddRemovePanelPAOExcl().leftListGetModel().getSize(); i++ )
        {
            newAvailableVector.add((LiteYukonPAObject)getAddRemovePanelPAOExcl().leftListGetModel().getElementAt(i));
        }
        
		// remove exclusions from pao's vector we aren't excluding anymore
		for ( int i = 0; i < oldExcludedVector.size(); i++ )
		{
			if ( !newExcludedVector.contains( oldExcludedVector.elementAt(i)) )
			{
				LiteYukonPAObject excludedItem = oldExcludedVector.elementAt(i);
				Integer id = excludedItem.getLiteID();
				
				// remove this exclusion from pao's vector
				for ( int j = 0; j < pao.getPAOExclusionVector().size(); j++ )
				{
					PAOExclusion exclusion = pao.getPAOExclusionVector().elementAt( j );
					
					if ( id.intValue() == exclusion.getExcludedPaoID().intValue() )
					{
						pao.getPAOExclusionVector().removeElementAt(j);
						break;
					}
				}
			}
		}
		
		/*
		 * *********Super Hack*******************************************************************************
		 * This getValue recieves a pao and modifies it, then places it in a multi with all the other devices that changed lists 
		 * then passes the multi down the line. To do this we have to actually modify the heavy objects of each excluded 
		 * device as well. Least ugly short term way to assure that dbchange msgs get sent out for all devices that were affected.
		 * ***************************************************************************************************
		 */
		
		SmartMultiDBPersistent multi = new SmartMultiDBPersistent();
		
		// create new exclusion sets for items in the new list but not in the old and add them to the pao and excluded pao
		for ( int i = 0; i < newExcludedVector.size(); i++ )
		{
			if ( !oldExcludedVector.contains( newExcludedVector.elementAt(i)) )
			{
				
				LiteYukonPAObject litePAO = newExcludedVector.elementAt(i);
				
				// this device was not in our original list of excluded devices,
				// modify it and add to our list needing dbchangemsgs
				YukonPAObject excPAO = (YukonPAObject)LiteFactory.convertLiteToDBPersAndRetrieve(litePAO );
				
                PAOExclusion paoExcl = new PAOExclusion( pao.getPAObjectID(),	
				                                        	new Integer(((LiteYukonPAObject) getAddRemovePanelPAOExcl().rightListGetModel().getElementAt( i ) ).getYukonID() ),
															PAOExclusion.REQUEUE_OPTIMAL);
				
				/*
				 * for each new entry in this transmitter's exclusion list, there needs to be a corresponding entry in that added
				 *  transmitter's own exclusion list. It is simple: just make a second entry for the database with the current paoID and
				 * current excluded deviceID switched.
				 */
				PAOExclusion correspondingPaoExcl = new PAOExclusion(new Integer(((LiteYukonPAObject)getAddRemovePanelPAOExcl().rightListGetModel()	.getElementAt(i)).getYukonID()),
				                                                     pao.getPAObjectID(),
				                                                     PAOExclusion.REQUEUE_OPTIMAL);
				
				pao.getPAOExclusionVector().add( paoExcl );
				excPAO.getPAOExclusionVector().add( correspondingPaoExcl );
				multi.addDBPersistent( excPAO );
			}
		}
		
        // check for new items in the new available list and add them to the multi for dbchange messages
		for (int i = 0; i < newAvailableVector.size(); i ++)
        {
			LiteYukonPAObject litePAO = newAvailableVector.elementAt(i);
			if ( !oldAvailableVector.contains(litePAO) )
			{
				// this device was not in our original list of available devices, remove it's exclusion to pao and add to our list needing dbchangemsgs
				YukonPAObject unExcludedPAO = (YukonPAObject)LiteFactory.convertLiteToDBPersAndRetrieve(litePAO);
                for ( int j = 0; j < unExcludedPAO.getPAOExclusionVector().size(); j++)
                {
                    PAOExclusion exclusion = unExcludedPAO.getPAOExclusionVector().elementAt(j);
                    if ( exclusion.getExcludedPaoID().intValue() == pao.getPAObjectID())
                    {
                        unExcludedPAO.getPAOExclusionVector().removeElementAt(j);
                    }
                }
				multi.addDBPersistent( unExcludedPAO );
				
			}
        }
        
        multi.addOwnerDBPersistent( pao );
        
		return multi;
	}

	/**
	 * Called whenever the part throws an exception.
	 * @param exception java.lang.Throwable
	 */
	private void handleException( Throwable exception )
	{

		/* Uncomment the following lines to print uncaught exceptions to stdout */
		com.cannontech.clientutils.CTILogger.info( "--------- UNCAUGHT EXCEPTION ---------" );
		com.cannontech.clientutils.CTILogger.error( exception.getMessage(),
													exception );
		;
	}

	/**
	 * Initializes connections
	 */
	/* WARNING: THIS METHOD WILL BE REGENERATED. */
	private void initConnections() throws java.lang.Exception
	{
		// user code begin {1}
		// user code end
		getAddRemovePanelPAOExcl().addAddRemovePanelListener( this );
		getJComboBoxFunction().addActionListener( this );
	}

	/**
	 * Initialize the class.
	 */
	/* WARNING: THIS METHOD WILL BE REGENERATED. */
	private void initialize()
	{
		try
		{
			// user code begin {1}
			// user code end
			setName( "RepeaterSetupEditorPanel" );
			setLayout( new java.awt.GridBagLayout() );
			setSize( 420, 354 );

			java.awt.GridBagConstraints constraintsAddRemovePanelPAOExcl = new java.awt.GridBagConstraints();
			constraintsAddRemovePanelPAOExcl.gridx = 1;
			constraintsAddRemovePanelPAOExcl.gridy = 1;
			constraintsAddRemovePanelPAOExcl.gridwidth = 2;
			constraintsAddRemovePanelPAOExcl.fill = java.awt.GridBagConstraints.BOTH;
			constraintsAddRemovePanelPAOExcl.anchor = java.awt.GridBagConstraints.WEST;
			constraintsAddRemovePanelPAOExcl.weightx = 1.0;
			constraintsAddRemovePanelPAOExcl.weighty = 1.0;
			constraintsAddRemovePanelPAOExcl.insets = new java.awt.Insets(	4,
																			3,
																			3,
																			7 );
			add( getAddRemovePanelPAOExcl(), constraintsAddRemovePanelPAOExcl );

			java.awt.GridBagConstraints constraintsJLabelFunction = new java.awt.GridBagConstraints();
			constraintsJLabelFunction.gridx = 1;
			constraintsJLabelFunction.gridy = 2;
			constraintsJLabelFunction.anchor = java.awt.GridBagConstraints.WEST;
			constraintsJLabelFunction.ipadx = 5;
			constraintsJLabelFunction.ipady = - 1;
			constraintsJLabelFunction.insets = new java.awt.Insets( 7,
																	11,
																	6,
																	59 );
			add( getJLabelFunction(), constraintsJLabelFunction );

			java.awt.GridBagConstraints constraintsJComboBoxFunction = new java.awt.GridBagConstraints();
			constraintsJComboBoxFunction.gridx = 2;
			constraintsJComboBoxFunction.gridy = 2;
			constraintsJComboBoxFunction.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintsJComboBoxFunction.anchor = java.awt.GridBagConstraints.WEST;
			constraintsJComboBoxFunction.weightx = 1.0;
			constraintsJComboBoxFunction.ipadx = 45;
			constraintsJComboBoxFunction.insets = new java.awt.Insets(	3,
																		0,
																		2,
																		124 );
			add( getJComboBoxFunction(), constraintsJComboBoxFunction );
			initConnections();
		} catch ( java.lang.Throwable ivjExc )
		{
			handleException( ivjExc );
		}
		// user code begin {2}

		// not used for now so just hide them
		getJLabelFunction().setVisible( false );
		getJComboBoxFunction().setVisible( false );

		// user code end
	}

	/**
	 * Method to handle events for the AddRemovePanelListener interface.
	 * @param newEvent java.util.EventObject
	 */
	public void leftListListSelection_valueChanged(
			java.util.EventObject newEvent )
	{
		// user code begin {1}
		// user code end
		// user code begin {2}
		// user code end
	}

	/**
	 * main entrypoint - starts the part when it is run as an application
	 * @param args java.lang.String[]
	 */
	public static void main( java.lang.String[] args )
	{
		try
		{
			javax.swing.JFrame frame = new javax.swing.JFrame();
			PAOExclusionEditorPanel aPAOExclusionEditorPanel;
			aPAOExclusionEditorPanel = new PAOExclusionEditorPanel();
			frame.setContentPane( aPAOExclusionEditorPanel );
			frame.setSize( aPAOExclusionEditorPanel.getSize() );
			frame.addWindowListener( new java.awt.event.WindowAdapter()
			{
				public void windowClosing( java.awt.event.WindowEvent e )
				{
					System.exit( 0 );
				};
			} );
			frame.show();
			java.awt.Insets insets = frame.getInsets();
			frame.setSize(	frame.getWidth() + insets.left + insets.right,
							frame.getHeight() + insets.top + insets.bottom );
			frame.setVisible( true );
		} catch ( Throwable exception )
		{
			System.err.println( "Exception occurred in main() of com.cannontech.common.gui.util.DataInputPanel" );
			exception.printStackTrace( System.out );
		}
	}

	/**
	 * This method was created by Cannon Technologies Inc.
	 * @param newEvent java.util.EventObject
	 */
	public void removeButtonAction_actionPerformed(
			java.util.EventObject newEvent )
	{

		if ( newEvent.getSource() == getAddRemovePanelPAOExcl() )
		{
			connEtoC5( newEvent );
		}
	}

	/**
	 * Method to handle events for the AddRemovePanelListener interface.
	 * @param newEvent java.util.EventObject
	 */
	public void rightListListSelection_valueChanged(
			java.util.EventObject newEvent )
	{
		// user code begin {1}
		// user code end
		// user code begin {2}
		// user code end
	}

	/**
	 * This method was created by Cannon Technologies Inc.
	 * @param newEvent java.util.EventObject
	 */
	public void rightListMouse_mouseClicked( java.util.EventObject newEvent )
	{
		// user code begin {1}
		// user code end
		// user code begin {2}
		// user code end
	}

	/**
	 * This method was created by Cannon Technologies Inc.
	 * @param newEvent java.util.EventObject
	 */
	public void rightListMouse_mouseEntered( java.util.EventObject newEvent )
	{
		// user code begin {1}
		// user code end
		// user code begin {2}
		// user code end
	}

	/**
	 * This method was created by Cannon Technologies Inc.
	 * @param newEvent java.util.EventObject
	 */
	public void rightListMouse_mouseExited( java.util.EventObject newEvent )
	{
		// user code begin {1}
		// user code end
		// user code begin {2}
		// user code end
	}

	/**
	 * This method was created by Cannon Technologies Inc.
	 * @param newEvent java.util.EventObject
	 */
	public void rightListMouse_mousePressed( java.util.EventObject newEvent )
	{
		// user code begin {1}
		// user code end
		// user code begin {2}
		// user code end
	}

	/**
	 * This method was created by Cannon Technologies Inc.
	 * @param newEvent java.util.EventObject
	 */
	public void rightListMouse_mouseReleased( java.util.EventObject newEvent )
	{
		// user code begin {1}
		// user code end
		// user code begin {2}
		// user code end
	}

	/**
	 * This method was created by Cannon Technologies Inc.
	 * @param newEvent java.util.EventObject
	 */
	public void rightListMouseMotion_mouseDragged(
			java.util.EventObject newEvent )
	{
		// user code begin {1}
		// user code end
		// user code begin {2}
		// user code end
	}

	/**
	 * This method was created in VisualAge.
	 * @param val java.lang.Object
	 */
	public void setValue( Object val )
	{
		YukonPAObject pao = (YukonPAObject) val;
		PaoType paoType = PaoType.getForDbString(pao.getPAOType());
		int deviceType = paoType.getDeviceTypeId();
		Vector<PAOExclusion> currExcluded = pao.getPAOExclusionVector();
		Vector<LiteYukonPAObject> assignedPAOs = new Vector<LiteYukonPAObject>();
		Vector<LiteYukonPAObject> availablePAOs = new Vector<LiteYukonPAObject>();

		IDatabaseCache cache = DefaultDatabaseCache.getInstance();
		synchronized ( cache )
		{
			List<LiteYukonPAObject> paos = cache.getAllYukonPAObjects();
			Collections.sort( paos, LiteComparators.liteStringComparator );

			for (PAOExclusion currPaoExclusion: currExcluded) {
				for (LiteYukonPAObject litePAO : paos) {
					if ( litePAO.getYukonID() == currPaoExclusion.getExcludedPaoID() && 
							litePAO.getLiteID() != LiteYukonPAObject.LITEPAOBJECT_NONE.getLiteID() )
					{
						assignedPAOs.addElement( litePAO );
						break;
					}
				}
			}

			for (LiteYukonPAObject litePAO : paos) {

				// be sure we have a pao that is similar to ourself by category
				// AND that it is not our self!
				if ( DeviceTypesFuncs.isTransmitter(litePAO.getPaoType().getDeviceTypeId()) && 
						litePAO.getYukonID() != pao.getPAObjectID().intValue() && 
						litePAO.getPaoType().getPaoClass() != PaoClass.GROUP)
				{
					if ( ! assignedPAOs.contains( litePAO ) && litePAO.getLiteID() != LiteYukonPAObject.LITEPAOBJECT_NONE.getLiteID() )
					{
						
						if ( DeviceTypesFuncs.isCCU( deviceType ) && DeviceTypesFuncs.isCCU( litePAO.getPaoType().getDeviceTypeId() ) )
						{
							availablePAOs.addElement( litePAO );
						}
						else if ( DeviceTypesFuncs.isLCU( deviceType ) 
								&& ( DeviceTypesFuncs.isLCU( litePAO.getPaoType().getDeviceTypeId() ) ) )
						{
							availablePAOs.addElement( litePAO );
						}
						else if ( ( deviceType == DeviceTypes.RTC || deviceType == DeviceTypes.SERIES_5_LMI )
								&& ( litePAO.getPaoType() == PaoType.RTC || litePAO.getPaoType() == PaoType.SERIES_5_LMI ) )
						{
							availablePAOs.addElement( litePAO );
						}
						else if ( ( deviceType == DeviceTypes.TAPTERMINAL || deviceType == DeviceTypes.SNPP_TERMINAL 
								    			|| DeviceTypesFuncs.isTCU( deviceType ) || deviceType == DeviceTypes.WCTP_TERMINAL )
								&& ( litePAO.getPaoType() == PaoType.TAPTERMINAL || litePAO.getPaoType() == PaoType.SNPP_TERMINAL 
												|| DeviceTypesFuncs.isTCU( litePAO.getPaoType().getDeviceTypeId() ) || litePAO.getPaoType() == PaoType.WCTP_TERMINAL ) )
						{
							availablePAOs.addElement( litePAO );
						}
						//else either the pao device type shouldn't be allowed to do exl. logic or the current
						//litePAO device type isn't the correct type to show up in the list
					}
				}
			}
		}

		getAddRemovePanelPAOExcl().rightListSetListData( assignedPAOs );
		getAddRemovePanelPAOExcl().leftListSetListData( availablePAOs );

		// save the state of these lists for comparison in the getValue
		// we need to figure out what changed and send a dbchange message for
		// each device that change lists
		oldAvailableVector = availablePAOs;
		oldExcludedVector = assignedPAOs;

		// only allow them to add if we have something to add!
		getAddRemovePanelPAOExcl().setEnabled( assignedPAOs.size() > 0 );

	}
}