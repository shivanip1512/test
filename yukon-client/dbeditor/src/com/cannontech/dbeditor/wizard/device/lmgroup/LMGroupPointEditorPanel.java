package com.cannontech.dbeditor.wizard.device.lmgroup;

import com.cannontech.database.data.lite.LiteComparators;
import com.cannontech.database.data.lite.LiteStateGroup;

/**
 * This type was created in VisualAge.
 */
public class LMGroupPointEditorPanel extends com.cannontech.common.gui.util.DataInputPanel implements java.awt.event.ActionListener 
{
	//localHashTable is used to store all status points with control by
	// their respected PAObject
	// Will contain a LiteDevice as Key and an ArrayList of LitePoints
	private java.util.Hashtable localHashTable = new java.util.Hashtable();
	
	private javax.swing.JComboBox ivjJComboBoxControlDevice = null;
	private javax.swing.JComboBox ivjJComboBoxControlPoint = null;
	private javax.swing.JComboBox ivjJComboBoxControlStartState = null;
	private javax.swing.JLabel ivjJLabelControlPoint = null;
	private javax.swing.JLabel ivjJLabelControlStartState = null;
	private javax.swing.JLabel ivjJLabelDeviceControl = null;
/**
 * Constructor
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
public LMGroupPointEditorPanel() {
	super();
	initialize();
}
/**
 * Method to handle events for the ActionListener interface.
 */
public void actionPerformed(java.awt.event.ActionEvent e) {
	// user code begin {1}
	// user code end
	if (e.getSource() == getJComboBoxControlStartState()) 
		connEtoC1(e);
	if (e.getSource() == getJComboBoxControlDevice()) 
		connEtoC3(e);
	if (e.getSource() == getJComboBoxControlPoint()) 
		connEtoC2(e);
	if (e.getSource() == getJComboBoxControlDevice()) 
		connEtoC4(e);
	if (e.getSource() == getJComboBoxControlPoint()) 
		connEtoC5(e);
	// user code begin {2}

	// user code end
}
/**
 * connEtoC1:  (JComboBoxControlStartState.action.actionPerformed(java.awt.event.ActionEvent) --> LMGroupPointEditorPanel.fireInputUpdate()V)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC1(java.awt.event.ActionEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.fireInputUpdate();
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}
/**
 * connEtoC2:  (JComboBoxControlPoint.action.actionPerformed(java.awt.event.ActionEvent) --> LMGroupPointEditorPanel.fireInputUpdate()V)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC2(java.awt.event.ActionEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.fireInputUpdate();
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}
/**
 * connEtoC3:  (JComboBoxControlDevice.action.actionPerformed(java.awt.event.ActionEvent) --> LMGroupPointEditorPanel.fireInputUpdate()V)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC3(java.awt.event.ActionEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.fireInputUpdate();
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}
/**
 * connEtoC4:  (JComboBoxControlDevice.action.actionPerformed(java.awt.event.ActionEvent) --> LMGroupPointEditorPanel.jComboBoxControlDevice_ActionPerformed(Ljava.awt.event.ActionEvent;)V)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC4(java.awt.event.ActionEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.jComboBoxControlDevice_ActionPerformed(arg1);
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}
/**
 * connEtoC5:  (JComboBoxControlPoint.action.actionPerformed(java.awt.event.ActionEvent) --> LMGroupPointEditorPanel.jComboBoxControlPoint_ActionPerformed(Ljava.awt.event.ActionEvent;)V)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC5(java.awt.event.ActionEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.jComboBoxControlPoint_ActionPerformed(arg1);
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}
/**
 * Return the JComboBoxControlDevice property value.
 * @return javax.swing.JComboBox
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JComboBox getJComboBoxControlDevice() {
	if (ivjJComboBoxControlDevice == null) {
		try {
			ivjJComboBoxControlDevice = new javax.swing.JComboBox();
			ivjJComboBoxControlDevice.setName("JComboBoxControlDevice");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJComboBoxControlDevice;
}
/**
 * Return the JComboBoxControlPoint property value.
 * @return javax.swing.JComboBox
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JComboBox getJComboBoxControlPoint() {
	if (ivjJComboBoxControlPoint == null) {
		try {
			ivjJComboBoxControlPoint = new javax.swing.JComboBox();
			ivjJComboBoxControlPoint.setName("JComboBoxControlPoint");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJComboBoxControlPoint;
}
/**
 * Return the JComboBoxControlStartState property value.
 * @return javax.swing.JComboBox
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JComboBox getJComboBoxControlStartState() {
	if (ivjJComboBoxControlStartState == null) {
		try {
			ivjJComboBoxControlStartState = new javax.swing.JComboBox();
			ivjJComboBoxControlStartState.setName("JComboBoxControlStartState");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJComboBoxControlStartState;
}
/**
 * Return the JLabelControlPoint property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getJLabelControlPoint() {
	if (ivjJLabelControlPoint == null) {
		try {
			ivjJLabelControlPoint = new javax.swing.JLabel();
			ivjJLabelControlPoint.setName("JLabelControlPoint");
			ivjJLabelControlPoint.setText("Control Point:");
			ivjJLabelControlPoint.setEnabled(true);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJLabelControlPoint;
}
/**
 * Return the JLabelSerialAddress property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getJLabelControlStartState() {
	if (ivjJLabelControlStartState == null) {
		try {
			ivjJLabelControlStartState = new javax.swing.JLabel();
			ivjJLabelControlStartState.setName("JLabelControlStartState");
			ivjJLabelControlStartState.setText("Control Start State:");
			ivjJLabelControlStartState.setEnabled(true);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJLabelControlStartState;
}
/**
 * Return the JLabelDeviceControl property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getJLabelDeviceControl() {
	if (ivjJLabelDeviceControl == null) {
		try {
			ivjJLabelDeviceControl = new javax.swing.JLabel();
			ivjJLabelDeviceControl.setName("JLabelDeviceControl");
			ivjJLabelDeviceControl.setText("Control Device:");
			ivjJLabelDeviceControl.setEnabled(true);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJLabelDeviceControl;
}
/**
 * getValue method comment.
 */
public Object getValue(Object o) 
{
	com.cannontech.database.data.device.lm.LMGroupPoint group = null;
	
	if( o instanceof com.cannontech.database.data.multi.SmartMultiDBPersistent )
		group = (com.cannontech.database.data.device.lm.LMGroupPoint)
					((com.cannontech.database.data.multi.SmartMultiDBPersistent)o).getOwnerDBPersistent();
	else
		group = (com.cannontech.database.data.device.lm.LMGroupPoint) o;
	
	if( group != null )
	{
		group.getLMGroupPoint().setDeviceIDUsage( new Integer(
			((com.cannontech.database.data.lite.LiteYukonPAObject)getJComboBoxControlDevice().getSelectedItem()).getYukonID() ) );
		
		group.getLMGroupPoint().setPointIDUsage( new Integer( 
			((com.cannontech.database.data.lite.LitePoint)getJComboBoxControlPoint().getSelectedItem()).getPointID() ) );

		group.getLMGroupPoint().setStartControlRawState( new Integer(
			getJComboBoxControlStartState().getSelectedIndex() ) );
	}

	return o;
}
/**
 * Called whenever the part throws an exception.
 * @param exception java.lang.Throwable
 */
private void handleException(Throwable exception) {

	/* Uncomment the following lines to print uncaught exceptions to stdout */
	com.cannontech.clientutils.CTILogger.info("--------- UNCAUGHT EXCEPTION ---------");
	com.cannontech.clientutils.CTILogger.error( exception.getMessage(), exception );;
}
/**
 * Insert the method's description here.
 * Creation date: (11/15/2001 2:33:55 PM)
 */
private void initComboBoxes() 
{
	getJComboBoxControlDevice().removeAllItems();
	getJComboBoxControlPoint().removeAllItems();
	getJComboBoxControlStartState().removeAllItems();

	com.cannontech.database.cache.DefaultDatabaseCache cache =
					com.cannontech.database.cache.DefaultDatabaseCache.getInstance();

	synchronized(cache)
	{
		java.util.List devices = cache.getAllYukonPAObjects();
		java.util.Collections.sort(devices, LiteComparators.liteStringComparator);

		int deviceID;
		com.cannontech.database.data.lite.LiteYukonPAObject liteDevice = null;
		
		for(int i=0;i<devices.size();i++)
		{
			liteDevice = (com.cannontech.database.data.lite.LiteYukonPAObject)devices.get(i);

			//only do RTUs, MCTs and CAPBANKCONTROLLERS for now!
			if( com.cannontech.database.data.device.DeviceTypesFuncs.isRTU(liteDevice.getType())
				 || com.cannontech.database.data.device.DeviceTypesFuncs.isCapBankController(liteDevice)
				 || com.cannontech.database.data.device.DeviceTypesFuncs.isMCT(liteDevice.getType()) )
			{
				com.cannontech.database.data.lite.LitePoint[] points = com.cannontech.database.cache.functions.PAOFuncs.getLitePointsForPAObject(
																		liteDevice.getYukonID() );
				
				//pointList gets inserted into the hashtable
				java.util.ArrayList pointList = new java.util.ArrayList(points.length / 2);
				
				for( int j = 0; j < points.length; j++ )
					if( points[j].getPointType() == com.cannontech.database.data.point.PointTypes.STATUS_POINT )
					{
			         //a DBPersistent must be created from the Lite object so you can do a retrieve
			         // this process is expensive!!!  This is why LitePoints are stored in the localHashTable
			         com.cannontech.database.data.point.StatusPoint dbPoint = (com.cannontech.database.data.point.StatusPoint)
			         					com.cannontech.database.data.lite.LiteFactory.createDBPersistent( points[j] );
			         try
			         {
			            com.cannontech.database.Transaction t = 
			            	com.cannontech.database.Transaction.createTransaction(
				            	com.cannontech.database.Transaction.RETRIEVE, dbPoint);

			            dbPoint = (com.cannontech.database.data.point.StatusPoint)t.execute();

				         //only add status points that have control
				         if( com.cannontech.database.data.point.PointTypes.hasControl(dbPoint.getPointStatus().getControlType()) )
				         	pointList.add( points[j] );  //adds a LitePoint to our pointList

			         }
			         catch (Exception e)
			         {
			            handleException(e);
			         }

					}

				
				if( pointList.size() > 0 )
				{
					localHashTable.put( liteDevice, pointList );
					getJComboBoxControlDevice().addItem( liteDevice );
				}

			}
		}

		if( getJComboBoxControlPoint().getItemCount() > 0 )
			jComboBoxControlPoint_ActionPerformed( null );
	}

	
}
/**
 * Initializes connections
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void initConnections() throws java.lang.Exception {
	// user code begin {1}

	// user code end
	getJComboBoxControlStartState().addActionListener(this);
	getJComboBoxControlDevice().addActionListener(this);
	getJComboBoxControlPoint().addActionListener(this);
}
/**
 * Initialize the class.
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void initialize() {
	try {
		// user code begin {1}
		// user code end
		setName("LMGroupPointEditorPanel");
		setLayout(new java.awt.GridBagLayout());
		setSize(342, 371);

		java.awt.GridBagConstraints constraintsJLabelControlStartState = new java.awt.GridBagConstraints();
		constraintsJLabelControlStartState.gridx = 1; constraintsJLabelControlStartState.gridy = 3;
		constraintsJLabelControlStartState.anchor = java.awt.GridBagConstraints.WEST;
		constraintsJLabelControlStartState.ipadx = 6;
		constraintsJLabelControlStartState.insets = new java.awt.Insets(14, 9, 249, 0);
		add(getJLabelControlStartState(), constraintsJLabelControlStartState);

		java.awt.GridBagConstraints constraintsJComboBoxControlStartState = new java.awt.GridBagConstraints();
		constraintsJComboBoxControlStartState.gridx = 2; constraintsJComboBoxControlStartState.gridy = 3;
		constraintsJComboBoxControlStartState.fill = java.awt.GridBagConstraints.HORIZONTAL;
		constraintsJComboBoxControlStartState.anchor = java.awt.GridBagConstraints.WEST;
		constraintsJComboBoxControlStartState.weightx = 1.0;
		constraintsJComboBoxControlStartState.ipadx = 78;
		constraintsJComboBoxControlStartState.insets = new java.awt.Insets(9, 1, 245, 14);
		add(getJComboBoxControlStartState(), constraintsJComboBoxControlStartState);

		java.awt.GridBagConstraints constraintsJLabelDeviceControl = new java.awt.GridBagConstraints();
		constraintsJLabelDeviceControl.gridx = 1; constraintsJLabelDeviceControl.gridy = 1;
		constraintsJLabelDeviceControl.anchor = java.awt.GridBagConstraints.WEST;
		constraintsJLabelDeviceControl.ipadx = 6;
		constraintsJLabelDeviceControl.insets = new java.awt.Insets(27, 9, 12, 23);
		add(getJLabelDeviceControl(), constraintsJLabelDeviceControl);

		java.awt.GridBagConstraints constraintsJComboBoxControlDevice = new java.awt.GridBagConstraints();
		constraintsJComboBoxControlDevice.gridx = 2; constraintsJComboBoxControlDevice.gridy = 1;
		constraintsJComboBoxControlDevice.fill = java.awt.GridBagConstraints.HORIZONTAL;
		constraintsJComboBoxControlDevice.anchor = java.awt.GridBagConstraints.WEST;
		constraintsJComboBoxControlDevice.weightx = 1.0;
		constraintsJComboBoxControlDevice.ipadx = 78;
		constraintsJComboBoxControlDevice.insets = new java.awt.Insets(22, 1, 8, 14);
		add(getJComboBoxControlDevice(), constraintsJComboBoxControlDevice);

		java.awt.GridBagConstraints constraintsJLabelControlPoint = new java.awt.GridBagConstraints();
		constraintsJLabelControlPoint.gridx = 1; constraintsJLabelControlPoint.gridy = 2;
		constraintsJLabelControlPoint.anchor = java.awt.GridBagConstraints.WEST;
		constraintsJLabelControlPoint.ipadx = 10;
		constraintsJLabelControlPoint.insets = new java.awt.Insets(13, 9, 14, 23);
		add(getJLabelControlPoint(), constraintsJLabelControlPoint);

		java.awt.GridBagConstraints constraintsJComboBoxControlPoint = new java.awt.GridBagConstraints();
		constraintsJComboBoxControlPoint.gridx = 2; constraintsJComboBoxControlPoint.gridy = 2;
		constraintsJComboBoxControlPoint.fill = java.awt.GridBagConstraints.HORIZONTAL;
		constraintsJComboBoxControlPoint.anchor = java.awt.GridBagConstraints.WEST;
		constraintsJComboBoxControlPoint.weightx = 1.0;
		constraintsJComboBoxControlPoint.ipadx = 78;
		constraintsJComboBoxControlPoint.insets = new java.awt.Insets(9, 1, 9, 14);
		add(getJComboBoxControlPoint(), constraintsJComboBoxControlPoint);
		initConnections();
	} catch (java.lang.Throwable ivjExc) {
		handleException(ivjExc);
	}
	// user code begin {2}

	initComboBoxes();

	// user code end
}
/**
 * This method must be implemented if a notion of data validity needs to be supported.
 * @return boolean
 */
public boolean isInputValid() 
{
	if( getJComboBoxControlDevice().getSelectedItem() == null )
	{
		setErrorString("A control device must be selected");
		return false;
	}
	
	if( getJComboBoxControlPoint().getSelectedItem() == null )
	{
		setErrorString("A control point must be selected");
		return false;
	}

	if( getJComboBoxControlStartState().getSelectedItem() == null )
	{
		setErrorString("A control start state must be selected");
		return false;
	}
	
	return true;
}
/**
 * Comment
 */
public void jComboBoxControlDevice_ActionPerformed(java.awt.event.ActionEvent actionEvent) 
{
	if( getJComboBoxControlDevice().getSelectedItem() != null )
	{
		getJComboBoxControlPoint().removeAllItems();

		com.cannontech.database.data.lite.LitePoint[] points = com.cannontech.database.cache.functions.PAOFuncs.getLitePointsForPAObject(
				((com.cannontech.database.data.lite.LiteYukonPAObject)getJComboBoxControlDevice().getSelectedItem()).getYukonID() );

		java.util.ArrayList litePointList = (java.util.ArrayList)
						localHashTable.get( getJComboBoxControlDevice().getSelectedItem() );

		for( int j = 0; j < litePointList.size(); j++ )
			getJComboBoxControlPoint().addItem( litePointList.get(j) );
	}

	return;
}
/**
 * Comment
 */
public void jComboBoxControlPoint_ActionPerformed(java.awt.event.ActionEvent actionEvent) 
{

	if( getJComboBoxControlPoint().getSelectedItem() != null )
	{
		com.cannontech.database.cache.DefaultDatabaseCache cache =
						com.cannontech.database.cache.DefaultDatabaseCache.getInstance();

		synchronized(cache)
		{

			getJComboBoxControlStartState().removeAllItems();
	
			com.cannontech.database.data.lite.LitePoint point = (com.cannontech.database.data.lite.LitePoint)getJComboBoxControlPoint().getSelectedItem();

			LiteStateGroup stateGroup = (LiteStateGroup)
				cache.getAllStateGroupMap().get( new Integer(point.getStateGroupID()) );

			for( int j = 0; j < stateGroup.getStatesList().size(); j++ )
			{
				//only add the first 2 states to the ComboBoxes
				if( j == 0 || j == 1 )
					getJComboBoxControlStartState().addItem( 
							(com.cannontech.database.data.lite.LiteState)stateGroup.getStatesList().get(j) );
			}
		}
	}
	
	return;
}
/**
 * main entrypoint - starts the part when it is run as an application
 * @param args java.lang.String[]
 */
public static void main(java.lang.String[] args) {
	try {
		java.awt.Frame frame;
		try {
			Class aFrameClass = Class.forName("com.ibm.uvm.abt.edit.TestFrame");
			frame = (java.awt.Frame)aFrameClass.newInstance();
		} catch (java.lang.Throwable ivjExc) {
			frame = new java.awt.Frame();
		}
		LMGroupVersacomEditorPanel aLMGroupVersacomEditorPanel;
		aLMGroupVersacomEditorPanel = new LMGroupVersacomEditorPanel();
		frame.add("Center", aLMGroupVersacomEditorPanel);
		frame.setSize(aLMGroupVersacomEditorPanel.getSize());
		frame.setVisible(true);
	} catch (Throwable exception) {
		System.err.println("Exception occurred in main() of com.cannontech.common.gui.util.DataInputPanel");
		com.cannontech.clientutils.CTILogger.error( exception.getMessage(), exception );;
	}
}
/**
 * setValue method comment.
 */
public void setValue(Object o) 
{
	if( o instanceof com.cannontech.database.data.device.lm.LMGroupPoint )
	{
		com.cannontech.database.data.device.lm.LMGroupPoint group = (com.cannontech.database.data.device.lm.LMGroupPoint) o;

		for( int i = 0; i < getJComboBoxControlDevice().getItemCount(); i++)
				if( ((com.cannontech.database.data.lite.LiteYukonPAObject)getJComboBoxControlDevice().getItemAt(i)).getYukonID()
					 == group.getLMGroupPoint().getDeviceIDUsage().intValue() )
				{
					getJComboBoxControlDevice().setSelectedIndex(i);
				}
		
		for( int i = 0; i < getJComboBoxControlPoint().getItemCount(); i++)
				if( ((com.cannontech.database.data.lite.LitePoint)getJComboBoxControlPoint().getItemAt(i)).getPointID()
					 == group.getLMGroupPoint().getPointIDUsage().intValue() )
				{
					getJComboBoxControlPoint().setSelectedIndex(i);
				}

		int startState = group.getLMGroupPoint().getStartControlRawState().intValue();
		if( startState >= 0 && startState < getJComboBoxControlStartState().getItemCount() )
			getJComboBoxControlStartState().setSelectedIndex( startState );
	}

}
/**
 * 
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private static void getBuilderData() {
/*V1.1
**start of data**
	D0CB838494G88G88G6AF854ACGGGGGGGGGGGG8CGGGE2F5E9ECE4E5F2A0E4E1F4E1359A8BF0D455B588CA8444D8E307A234D1230D5334E2454AF4300DA91DC1E47C34D89DBFB371530AB32945A95528B83D59108F9AA0C224D121C603CAB446A8A849A6E8BE9B42062891C292AAD3F8493EA41B3C5D373CFD1BEC1610741C7BF97B327BB6C146B2F3F86F1EF34F671EFB4E39676E8BA9FEB3A9B75BD6CCC8F6AE617F76E793B26F83C22A8B9E6E9638B8B9BD0F187E3D85708A79E75EC6F8A6
	GEDDFC2FADE91E98A9540B89946951726677D8B5E3713C32131F2F809212783E889F1EF5FB2351E329A21A71F0E6F8C9642F38BC08A607039DEC67DFF8916F07CEA0E9710EDA36471BE1C13BD3A13632AE15C8E108430340F4DDF8D4FCDD4730576EABE6F44E5C9E47D2A77EC1BD10EC926000AFE465B4E1F2564066C2CFE54757EE8A1670907F19AGAEBE073C5D5D884F3A76217E4FFBD5CFE52F64752ADEDD6E9D289CF107A44DD56912075D3DF22562695754004FEEEF7B3285DF5AD537D7EFF639F5D5EB17
	3C32C208BE00FA345182AA97E442183891D7D40D3A174178EE0035867EBE0AF7407B89G5DAD56FB603DBA3E1E6EB3B7108D77F9F33646D86FBCB7DB67D2F7F83D09016773DF23FB53EB47BDD887346241743CBDGEDG8A40BC00B7E8CC8C563C8F4F069E3DC975F8D4EF034F57283834AFD5172C403B5D8E940EBBA2772A1A0B90366FD523959676CC850C6F736F4C47FC1232D744C06A116B48C3B79D3665DB78642114AADB72A07345724130CF186E11183A9F9A1C9C2313F5F791336E4C0B541CDC8B5DCB6F
	AD372D63F2B68E05F56F9B423D1EC577FAB73CE77378E07879949FB461D93C6E20F89C4783ADF3C844C67796E1CB5B4605246863B6CBBFACC8EF3035AB4C0681A5521643B5A8BBD1E1BADFB6ECB9CE71CBCDF8E64B4354969C2F81DA35A7BD0F4517D207793B88464BG6E86D88BD060CD4F5B8DD06D95B1F6D34DC76798E36D1226B7865C0ACB568863D56CBB60A987F5CDF2FA7D3A646D15BBA54DAD75A8321F70B981BB7A20E792DF0846FEAF68E8113D32A669EE558B52A50F2C4B1A1F4D19E9FF8567E8B225
	F638BDB2A870780810BB573E9D1E0A64570F79DC12AE07E7B07A65F65C13E12993A402GF8E7FE79518174D7B72C5F8790E778E1CBAD4ABBACEBD0AF2010286AD0405701ABA1F7FBE9AC505A11886F29DE6163ABBEC7DC318AF5DC95F21AEB371964B85C3DE83E240D0A9C1BD30DB53ADE95B173D43708994766EBC9453D2D16B10360D2D92C0C29113913C8E522DDEC3C333A7BBB66F1AC28D723F30F697874FB5311E301E37E5DD20FFB158CB62481E498BE0E2BADE5B15BA879E59125DFEB90E032AB6CF1E667
	B7B5455C1F39DCAE62B7996BB8C969D9A03B54171EF78840873040A7FC7998DD27580FDC937FD4EBFA6D9C574EF4EC20BAF2CCBAD07E4B6740FBAEBA184F066B701C1207E52F6E10FD325785AFFECEBBDB077AF89E7729864FCC0A77C15EB7C122798D7C45F5181B122EEB6E1E00AE9F15F55D6D6D8F53AF233A7C019EDF38E488DD8B6AF0FD6A88643DB1FFF19D66CC2F6A754AF4738C96CE7F894DD9D911BDE0F4139A706A8CFF6D810C7BA1F914570C6B8FE0D3B4ACA9812CBF2C07D8A2219FFD1A5B83A101A5
	0C6CBE0D79FC6DE80949C7E9305F588BE541337034484D11B2111B3BC3D71134136F582C4E168557BAEC4AE916A7F127433949ECDA7839356DC934F6F03CBF0762366C41DEB0D69343F222258165A5011E25G2BCF0B1C58798552C55D61495024AA8AF329616B627AA25C9B79F4C006F20D856E30613FB76A37C550BA39AF993D2C9EFD2918EA6318063ECC2C913D6263E04F86001D9A7367890D57ACBA2F5BAE662D04F10696EBDEE12D18C77C69F9A57ED873520CF9557E49725A7D66F9FA1D18179CA1EFB91F
	47E2A57FA476A0D67DE6FF40DD99505DD851A59D124497506926303EF2AB6D67D1C621AE7A47723334FF047189GDD97B1F54FE651BFA68EA5110CEBDE0F59AF4F5319EDCB74F0CC154E947D63E50D28F39D6A8B089878F13D798CEB707B55DEB7BDBE797EFD59E00EB906BB527092BC7B5C1ADFF7C2D1880A3D6EEEC0FB71FC65E85EEF5CCA7B90DCEBB168ED83C88BA07E913958E47B2CGBBA7D36149E2FB377D932C99B498AB5923DD567AD44DA33B768CCA43D225B422D7527AD459C0494D78DEAE76E6C579
	B6E627CC3D4F8E7DBD651D228F3CFAFBBD573D7A978BC95D44F61BD5DF9E3FF5172DE0187933EAB86C5731DF6665E47E7705FC364699F63146B11833F4D82C71CFC3D6EB4C84BA637B34D67011916831C6841FEB420A4FB7A2781EB478E681EE7908387FBC67E80667A0F4E6BDEA239AEC02E054D445C1AFBDA79BA2FC58965B093568A63EFF0B6B042E752027A2A8F4AD7E48E1210B96216F280A1DE9452148FE5E958C779241E0F4DF417A5C2BE85E4C8B32BEF73301DFC271CBCCF8361E0FA89E47EB00D6BFAA
	FA33D2875677D4982F8448G4882A889413E04C43EE4B46CA553743ECE48FF4DABA9C7A79DE6BCC7FE5808318D678F9FAFECC4DF52730708337DF7B511FE6888CDDE3BAFE24C786E09622B0C98A7C67031FD6CD75E90B58CF7A9D8699F013359EEEF95BB4B6F42A5FD62AE1C9A9235696CC1F3EDDA3E75935B4BB1EA5363DC7746B3619C2AB06E362B1A315EBA00F68AA0881088308CE08DC05699617F5DCD872673BF8D2A8E3589F007605CE90A6C918CFF4D361F1F1F2F08620B74F34958E4BE663F4D0A70DF55
	186895EBEB4F4F86E794DF248D8316B6DCB9A1EC08B3EC785BF95A70E2945F3FA3464B462C782E29BBBFFDB7C671C56A3BBBC29F3B07FDC04F5A35C05BG40F04F1CC1DC69D9486D33428FBFBD0B79FF8246F1GA9GAB81B2818A47451CDF51B9D5632CE7E99FE7BD04EF1C75887C1EF37614C57C238C710EBAA26911A0EC40F7E1C734CF5AADF5EDB63DDB5D15A2C15CD1705D6A9E41F22F5C917BBED065303E8FFC66303E8F54B9DEE1F810E17E15C0509D0E296F8BED8E2B7B42AE3A5E5BF7EC25327DFE59CF
	0B81B6E01C76047EAA3138B7FCA5F9E46E2B6D64791ACDFCCE332EB6FDEC78FAD75FD43FF5A50CC76FA92BED5F3460D91E8C74B4007B81568194CD081A7628E35AB325B9B845193226B9763EBC58EC3DAF0FB4E30C78550056ABC77AFEEDF309C18BD7CF27AEFB50CDEC4E5314DF1734FEB9EA6F32A8BF2345624FBE98EBFF599E7C46EE5E033D469EECD3265A032A0968BDE07234DA333CCF8CF9C993D349F3441477EE1DD95EBE837F40A47C27863EB10477CCC59AD583BAA1ABA6686FF3A1F11F6C06318FE081
	C06A0479FEF1638866E21FA6F940BDC7FDF2AFC90A23E7491860AD04F1A9C0BD40A90038B851BFEF99463BE1EB1B54A3AB3CB9BB8A3B2243FF3A4C6D7A73072F631C089E4EB40D4F2BEC59EE48E25DA41F4A69B39CBBAC643019C2462A4F36C55AC31BC5C13FED572B96B27894427A624C33663EF8ED9C6B0B8BE7083EB9F9540ADEEA50EFEE36226F1BA14E05FDFD62DCB8B4C3646771165879F52A45BA3F7ADA2C6B1E52B2F55D53DBE26585332FDC9176ADB0EC3EE5C260564E90770399A171FD247A82082D8B
	C44F7E71A96C330CDE8FBBF23B3DC5F33B9A257EB69A1D2CCF381D1E19481FF201381FBECBE5E640B88B20E8266881036F0A9EB0753A0524AD6E30657DB42363935B1E19AC365BE606FB40946881194E3DE3C2E7BC5053E6A2CE9AB36F97464993B6566BB07A65136868BB7CA496267FFE929D6D7D79A47EE7A751F3CD7CECEDF73CA35636610E94225CDC93736EDDB00B2D2902BF5BBB4B486EB9AF64B33F7F3D2F04D87658B49B587A4B468BF8DDF2018C06FBF0D4609699384CE601DB83387A8B69B700BEC70C
	9AE6BE5FF3B89482CFA9C099276D037745FCDDCCDE3DB21D3C9C9344C748DB94A56F4E09696419E526C448DBEE1247FB77283B62F440FB7773643B73BB7331BB686DE7B0E7D6DE486E2059867E8F943F4604E7F97B22113799C0F3DCA46E267B1D98BFA9B0DE81709040FA00625969F9D533C51DFA69D09519260F68D0FDEDD04894D369E16B2B9EBB3F6FF837650B7CD87A6B2B49320B1E4B0EF57FF34CE6F961624F359DE5269EE9D5AB2EAF91E8CBG56GE48210F8682B63457A0E9C2AE06B431FB89BF4DD6A
	9D203FCA0945F53A6511B09A8FCB3636BD63D6EB2B08E7F6544333A35E2A374E307C3EB840670E41B341126F69E6AB3EE4BEF799BCD7C7703DC95787B50408FF6B3E0846C1CC4463B7FC5B91E2787F11C2617F1DBF3B12B86EFBAE3BAC067FF7DF4CF46A9707EB6F5A418D79D63A199E65A809E19B23DF5AC10CB55FF5A67CFE3F69BD674C77754DA7F6EE6E7C3F6AE031D76F0C5D8334B82DFB00AF1D56BD40D16754F71BEF1D313F0574B84D5FC2D86E1CBC06E7D8AA6C6BCA00CCGB287EA439C11B76B1CEF93
	1AB796B9536C6A47CE8E3F446AC27F9FDBE32F750156693F5FBE560A3F8F77283A2EFAD03E40BF590A7B252BBE0AE43827DA51C71A3BFFC0E7D3B9FEFDAB461BA277D1B46BF33E2067E3952C2BFE0E68FD7EE374BEED866EA299F10BE03C4440BDCB4F5195B0DEE360CE0484EF0601BBB3C4F54C055ABBD7608A8EA22E9B4683864EF3D0706A86EE9335E5850C6F1FABFA233B3EB57F2DCB514237EC225EC746D966DC1617D9F343794976575605E7E745BC68C9819400D9G8BG16G2CG389FA093E063A569F9
	BB812AG9CG5DG83GE3G891708F8E8B902BDCE27B448CF88764508C684A0F9B3C803C213D86F4D787A0F450E8B6F316963E264987D16G7B59B16A63774031E3F8AFD27B7A7C3291FFB30E9599B8273FDD753B717E486E881D63787DCE87C52C867D769B74B26BB1CECF59E3641049BDD0292947407DC8867F085E0E69DAAC63FC590B0BF01134CBD047D2D97CFE366492631E951AEC5323C742B679C3535914B305CD9B67C75A740321E81B2E9F92B6954D97B63DF9F032CDC59DE11BE69D1C4E265CA9ECAA
	859D2FCE3229F4A25A267D466F0C9573C58FF3AB35016D71764EF7E245A0D425D6E8113F6E9914FB758E295F60595195BB7EF6F5CD9FFF6FF6D957250A2E683A74F117F5DD5A5FE52ECB7C372CAE74D7BF64CD37B12F41409DB7F08729CC44F539DD7A0001EF23F6A17E100C8A857E8835EB04623AB92E4B04BB4EF1A73AB007E049832AA6F825AE5C7FBE37223031CC67383D10D206BF86290DEE1FE432DBB5F0E8B70B1DDD78A9086F759DB20DABADA0337A16B4E22EEF55AF6D377C5E0DD0B71F5F0B78F3796C
	B2AEFFD1D7D8FE5A10104FE82B3A43342FA6840D455348F15C0BF81017BCDF54361E4E8FE24595A05BB555A7EB7AA84352AF55FC5DE34751A663CCE6A471B78D470B296F208CC90ABBC4BF0F9E05095EFE76F7189339AF40B3752FA4572F112D4607AEA7F5F64670DEFB4EE357645848C6A749E34167A49BB1021CA41786181364C07085718F157EE1F25EA67FE9B2CE1DDCE0426EB0301B4CF3F5D24CFFAFA925BF5E1452DFEF6CBA59AA506C37980E51A80A7E34E2B784BEC7C4D789C28DECA14762DA5FB030DB
	CC584D1C2BFC429C8BD99716440C05E0820B01F98911E75DB37938581500FFBCC16C69417C8FE36DA9540A239EC9D1F4C999EA703A30EEB40E6AF23817FAA19F657B459CCEE4B4DB815ABDE862799FD0CB8788A488E766458FGG34ABGGD0CB818294G94G88G88G6AF854ACA488E766458FGG34ABGG8CGGGGGGGGGGGGGGGGGE2F5E9ECE4E5F2A0E4E1F4E1D0CB8586GGGG81G81GBAGGG7F8FGGGG
**end of data**/
}
}
