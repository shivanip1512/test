package com.cannontech.dbeditor.wizard.changetype.point;

/**
 * This type was created in VisualAge.
 */

public class PointTypePanel extends com.cannontech.common.gui.util.DataInputPanel {
	private int pointType = com.cannontech.database.data.point.PointTypes.INVALID_POINT;
	private javax.swing.JRadioButton ivjAnalogRadioButton = null;
	private javax.swing.JRadioButton ivjCalculatedRadioButton = null;
	private javax.swing.JLabel ivjSelectTypeLabel = null;
	private javax.swing.JRadioButton ivjStatusRadioButton = null;
	private javax.swing.JRadioButton ivjAccumulatorRadioButton = null;
	private javax.swing.JRadioButton ivjDemandAccumulatorRadioButton = null;
	private javax.swing.ButtonGroup ivjButtonGroup = null;
public PointTypePanel() {
	super();
	initialize();
}
/**
 * connEtoM1:  (PointTypePanel.initialize() --> ButtonGroup.add(Ljavax.swing.AbstractButton;)V)
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoM1() {
	try {
		// user code begin {1}
		// user code end
		getButtonGroup().add(getAccumulatorRadioButton());
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}
/**
 * connEtoM2:  (PointTypePanel.initialize() --> ButtonGroup.add(Ljavax.swing.AbstractButton;)V)
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoM2() {
	try {
		// user code begin {1}
		// user code end
		getButtonGroup().add(getStatusRadioButton());
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}
/**
 * connEtoM3:  (PointTypePanel.initialize() --> ButtonGroup.add(Ljavax.swing.AbstractButton;)V)
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoM3() {
	try {
		// user code begin {1}
		// user code end
		getButtonGroup().add(getCalculatedRadioButton());
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}
/**
 * connEtoM4:  (PointTypePanel.initialize() --> ButtonGroup.add(Ljavax.swing.AbstractButton;)V)
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoM4() {
	try {
		// user code begin {1}
		// user code end
		getButtonGroup().add(getAnalogRadioButton());
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}
/**
 * connEtoM4:  (PointTypePanel.initialize() --> ButtonGroup.add(Ljavax.swing.AbstractButton;)V)
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoM5() {
	try {
		// user code begin {1}
		// user code end
		getButtonGroup().add(getDemandAccumulatorRadioButton());
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}
/**
 * Return the JRadioButton3 property value.
 * @return javax.swing.JRadioButton
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JRadioButton getAccumulatorRadioButton() {
	if (ivjAccumulatorRadioButton == null) {
		try {
			ivjAccumulatorRadioButton = new javax.swing.JRadioButton();
			ivjAccumulatorRadioButton.setName("AccumulatorRadioButton");
			ivjAccumulatorRadioButton.setText("Pulse Accumulator");
			ivjAccumulatorRadioButton.setMaximumSize(new java.awt.Dimension(103, 27));
			ivjAccumulatorRadioButton.setActionCommand("Accumulator");
			ivjAccumulatorRadioButton.setFont(new java.awt.Font("dialog", 0, 14));
			ivjAccumulatorRadioButton.setMinimumSize(new java.awt.Dimension(103, 27));
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjAccumulatorRadioButton;
}
/**
 * Return the AnalogRadioButton property value.
 * @return javax.swing.JRadioButton
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JRadioButton getAnalogRadioButton() {
	if (ivjAnalogRadioButton == null) {
		try {
			ivjAnalogRadioButton = new javax.swing.JRadioButton();
			ivjAnalogRadioButton.setName("AnalogRadioButton");
			ivjAnalogRadioButton.setText("Analog");
			ivjAnalogRadioButton.setMaximumSize(new java.awt.Dimension(69, 27));
			ivjAnalogRadioButton.setActionCommand("Analog");
			ivjAnalogRadioButton.setSelected(true);
			ivjAnalogRadioButton.setFont(new java.awt.Font("dialog", 0, 14));
			ivjAnalogRadioButton.setMinimumSize(new java.awt.Dimension(69, 27));
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjAnalogRadioButton;
}
/**
 * Return the ButtonGroup property value.
 * @return javax.swing.ButtonGroup
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.ButtonGroup getButtonGroup() {
	if (ivjButtonGroup == null) {
		try {
			ivjButtonGroup = new javax.swing.ButtonGroup();
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjButtonGroup;
}
/**
 * Return the CalculatedRadioButton property value.
 * @return javax.swing.JRadioButton
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
public javax.swing.JRadioButton getCalculatedRadioButton() {
	if (ivjCalculatedRadioButton == null) {
		try {
			ivjCalculatedRadioButton = new javax.swing.JRadioButton();
			ivjCalculatedRadioButton.setName("CalculatedRadioButton");
			ivjCalculatedRadioButton.setText("Calculated");
			ivjCalculatedRadioButton.setMaximumSize(new java.awt.Dimension(92, 27));
			ivjCalculatedRadioButton.setActionCommand("Calculated");
			ivjCalculatedRadioButton.setFont(new java.awt.Font("dialog", 0, 14));
			ivjCalculatedRadioButton.setMinimumSize(new java.awt.Dimension(92, 27));
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjCalculatedRadioButton;
}
/**
 * Return the DemandAccumulatorRadioButton property value.
 * @return javax.swing.JRadioButton
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JRadioButton getDemandAccumulatorRadioButton() {
	if (ivjDemandAccumulatorRadioButton == null) {
		try {
			ivjDemandAccumulatorRadioButton = new javax.swing.JRadioButton();
			ivjDemandAccumulatorRadioButton.setName("DemandAccumulatorRadioButton");
			ivjDemandAccumulatorRadioButton.setText("Demand Accumulator");
			ivjDemandAccumulatorRadioButton.setMaximumSize(new java.awt.Dimension(92, 27));
			ivjDemandAccumulatorRadioButton.setActionCommand("Calculated");
			ivjDemandAccumulatorRadioButton.setFont(new java.awt.Font("dialog", 0, 14));
			ivjDemandAccumulatorRadioButton.setMinimumSize(new java.awt.Dimension(92, 27));
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjDemandAccumulatorRadioButton;
}
/**
 * Insert the method's description here.
 * Creation date: (4/30/2001 3:23:30 PM)
 * @return int
 */
public int getPointType()
{
    if (pointType == com.cannontech.database.data.point.PointTypes.INVALID_POINT)
    {
        if (getAnalogRadioButton().isSelected())
        {
            pointType = com.cannontech.database.data.point.PointTypes.ANALOG_POINT;
        }
        else if (getStatusRadioButton().isSelected())
        {
            pointType = com.cannontech.database.data.point.PointTypes.STATUS_POINT;
        }
        else if (getAccumulatorRadioButton().isSelected())
        {
            pointType = com.cannontech.database.data.point.PointTypes.PULSE_ACCUMULATOR_POINT;
        }
        else if (getDemandAccumulatorRadioButton().isSelected())
        {
            pointType = com.cannontech.database.data.point.PointTypes.DEMAND_ACCUMULATOR_POINT;
        }
        else if (getCalculatedRadioButton().isSelected())
        {
            pointType = com.cannontech.database.data.point.PointTypes.CALCULATED_POINT;
        }
        else
            throw new Error(getClass() + "::getSelectedType() - No radio button is selected");
    }

    return pointType;
}
/**
 * This method was created in VisualAge.
 * @return int
 */
private int getSelectedType()
{

    if (getAnalogRadioButton().isSelected())
    {
        return com.cannontech.database.data.point.PointTypes.ANALOG_POINT;
    }
    else if (getStatusRadioButton().isSelected())
    {
        return com.cannontech.database.data.point.PointTypes.STATUS_POINT;
    }
    else if (getAccumulatorRadioButton().isSelected())
    {
        return com.cannontech.database.data.point.PointTypes.PULSE_ACCUMULATOR_POINT;
    }
    else if (getDemandAccumulatorRadioButton().isSelected())
    {
        return com.cannontech.database.data.point.PointTypes.DEMAND_ACCUMULATOR_POINT;
    }
    else if (getCalculatedRadioButton().isSelected())
    {
        return com.cannontech.database.data.point.PointTypes.CALCULATED_POINT;
    }
    else
        throw new Error(getClass() + "::getSelectedType() - No radio button is selected");
}
/**
 * Return the SelectTypeLabel property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getSelectTypeLabel() {
	if (ivjSelectTypeLabel == null) {
		try {
			ivjSelectTypeLabel = new javax.swing.JLabel();
			ivjSelectTypeLabel.setName("SelectTypeLabel");
			ivjSelectTypeLabel.setFont(new java.awt.Font("dialog", 0, 14));
			ivjSelectTypeLabel.setText("Select the type of point:");
			ivjSelectTypeLabel.setMaximumSize(new java.awt.Dimension(149, 19));
			ivjSelectTypeLabel.setMinimumSize(new java.awt.Dimension(149, 19));
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjSelectTypeLabel;
}
/**
 * Return the StatusRadioButton property value.
 * @return javax.swing.JRadioButton
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
public javax.swing.JRadioButton getStatusRadioButton() {
	if (ivjStatusRadioButton == null) {
		try {
			ivjStatusRadioButton = new javax.swing.JRadioButton();
			ivjStatusRadioButton.setName("StatusRadioButton");
			ivjStatusRadioButton.setText("Status");
			ivjStatusRadioButton.setMaximumSize(new java.awt.Dimension(65, 27));
			ivjStatusRadioButton.setActionCommand("Status");
			ivjStatusRadioButton.setFont(new java.awt.Font("dialog", 0, 14));
			ivjStatusRadioButton.setMinimumSize(new java.awt.Dimension(65, 27));
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjStatusRadioButton;
}
/**
* This method was created in VisualAge.
* @return java.lang.Object
* @param val java.lang.Object
*/
public Object getValue(Object val)
{
	int type = getSelectedType();

	if (val == null)
		return new Integer(type);
	else
	{

		((com.cannontech.database.data.point.PointBase) val).getPoint().setPointType(com.cannontech.database.data.point.PointTypes.getType(type));
		com.cannontech.database.db.point.PointAlarming pointAlarming = ((com.cannontech.database.data.point.PointBase) val).getPointAlarming();
		com.cannontech.database.db.point.Point point = ((com.cannontech.database.data.point.PointBase) val).getPoint();

		try
		{
			com.cannontech.database.Transaction t =
				com.cannontech.database.Transaction.createTransaction(
					com.cannontech.database.Transaction.DELETE_PARTIAL,
					((com.cannontech.database.db.DBPersistent) val));

			val = t.execute();
		}
		catch (com.cannontech.database.TransactionException e)
		{
			com.cannontech.clientutils.CTILogger.error( e.getMessage(), e );
		}

		val = com.cannontech.database.data.point.PointFactory.createPoint(type);

		((com.cannontech.database.data.point.PointBase) val).setPoint(point);
		((com.cannontech.database.data.point.PointBase) val).setPointAlarming(pointAlarming);
		((com.cannontech.database.data.point.PointBase) val).setPointID(((com.cannontech.database.data.point.PointBase) val).getPoint().getPointID());

		if (val instanceof com.cannontech.database.data.point.StatusPoint)
			 ((com.cannontech.database.data.point.PointBase) val).getPoint().setStateGroupID(new Integer(1));
		else if (val instanceof com.cannontech.database.data.point.AccumulatorPoint)
			((com.cannontech.database.data.point.PointBase) val).getPoint().setStateGroupID(
				new Integer(com.cannontech.database.db.state.StateGroupUtils.STATEGROUP_ACCUMULATOR));
		else if (val instanceof com.cannontech.database.data.point.CalculatedPoint)
			((com.cannontech.database.data.point.PointBase) val).getPoint().setStateGroupID(
				new Integer(com.cannontech.database.db.state.StateGroupUtils.STATEGROUP_CALCULATED));
		else if (val instanceof com.cannontech.database.data.point.AnalogPoint)
			((com.cannontech.database.data.point.PointBase) val).getPoint().setStateGroupID(
				new Integer(com.cannontech.database.db.state.StateGroupUtils.STATEGROUP_ANALOG));

		//resets the pointOffset to next available pointOffset for the new type
		java.util.Vector usedPointOffsetsVector = new java.util.Vector();

		//fill vector with points of same deviceID
		com.cannontech.database.cache.DefaultDatabaseCache cache =
			com.cannontech.database.cache.DefaultDatabaseCache.getInstance();
		synchronized (cache)
	{
			java.util.List points = cache.getAllPoints();
			java.util.Collections.sort(points, com.cannontech.database.data.lite.LiteComparators.litePointDeviceIDComparator);
			com.cannontech.database.data.lite.LitePoint litePoint = null;
			for (int i = 0; i < points.size(); i++)
			{
				litePoint = ((com.cannontech.database.data.lite.LitePoint) points.get(i));
				if (((com.cannontech.database.data.point.PointBase) val).getPoint().getPaoID().intValue() == litePoint.getPaobjectID()
					&& type == litePoint.getPointType())
				{
					usedPointOffsetsVector.addElement(litePoint);
				}
				else if (litePoint.getPaobjectID() > ((com.cannontech.database.data.point.PointBase) val).getPoint().getPaoID().intValue())
				{
					break;
				}
			}
		}

		//search through vector to find next available pointOffset -- if vector is empty then pointoffset the same
		int pointOffset = 1;
		if (usedPointOffsetsVector.size() > 0)
		{
			for (int i = 0; i < usedPointOffsetsVector.size(); i++)
			{
				if (pointOffset == ((com.cannontech.database.data.lite.LitePoint) usedPointOffsetsVector.elementAt(i)).getPointOffset())
				{
					pointOffset = pointOffset + 1;
					i = -1;
				}

			}

		}
		((com.cannontech.database.data.point.PointBase) val).getPoint().setPointOffset(new Integer(pointOffset));
		try
		{
			com.cannontech.database.Transaction t2 =
				com.cannontech.database.Transaction.createTransaction(
					com.cannontech.database.Transaction.ADD_PARTIAL,
					((com.cannontech.database.db.DBPersistent) val));

			val = t2.execute();

		}
		catch (com.cannontech.database.TransactionException e)
		{
			com.cannontech.clientutils.CTILogger.error( e.getMessage(), e );
		}

		return val;
	}
}
/**
 * Called whenever the part throws an exception.
 * @param exception java.lang.Throwable
 */
private void handleException(Throwable exception) {

	/* Uncomment the following lines to print uncaught exceptions to stdout */
	// com.cannontech.clientutils.CTILogger.info("--------- UNCAUGHT EXCEPTION ---------");
	// com.cannontech.clientutils.CTILogger.error( exception.getMessage(), exception );;
}
/**
 * Initializes connections
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void initConnections() throws java.lang.Exception {
	// user code begin {1}
	// user code end
}
/**
 * Initialize the class.
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void initialize() {
	try {
		// user code begin {1}
		initConnections();
		connEtoM1();
		connEtoM2();
		connEtoM3();
		connEtoM4();
		connEtoM5();
		// user code end
		setName("PointTypePanel");
		setLayout(new java.awt.GridBagLayout());
		setSize(294, 176);

		java.awt.GridBagConstraints constraintsSelectTypeLabel = new java.awt.GridBagConstraints();
		constraintsSelectTypeLabel.gridx = 0; constraintsSelectTypeLabel.gridy = 0;
		constraintsSelectTypeLabel.fill = java.awt.GridBagConstraints.HORIZONTAL;
		constraintsSelectTypeLabel.anchor = java.awt.GridBagConstraints.WEST;
		add(getSelectTypeLabel(), constraintsSelectTypeLabel);

		java.awt.GridBagConstraints constraintsAnalogRadioButton = new java.awt.GridBagConstraints();
		constraintsAnalogRadioButton.gridx = 0; constraintsAnalogRadioButton.gridy = 1;
		constraintsAnalogRadioButton.anchor = java.awt.GridBagConstraints.WEST;
		constraintsAnalogRadioButton.insets = new java.awt.Insets(0, 20, 0, 0);
		add(getAnalogRadioButton(), constraintsAnalogRadioButton);

		java.awt.GridBagConstraints constraintsStatusRadioButton = new java.awt.GridBagConstraints();
		constraintsStatusRadioButton.gridx = 0; constraintsStatusRadioButton.gridy = 2;
		constraintsStatusRadioButton.anchor = java.awt.GridBagConstraints.WEST;
		constraintsStatusRadioButton.insets = new java.awt.Insets(0, 20, 0, 0);
		add(getStatusRadioButton(), constraintsStatusRadioButton);

		java.awt.GridBagConstraints constraintsAccumulatorRadioButton = new java.awt.GridBagConstraints();
		constraintsAccumulatorRadioButton.gridx = 0; constraintsAccumulatorRadioButton.gridy = 3;
		constraintsAccumulatorRadioButton.anchor = java.awt.GridBagConstraints.SOUTHWEST;
		constraintsAccumulatorRadioButton.insets = new java.awt.Insets(0, 20, 0, 0);
		add(getAccumulatorRadioButton(), constraintsAccumulatorRadioButton);

		java.awt.GridBagConstraints constraintsCalculatedRadioButton = new java.awt.GridBagConstraints();
		constraintsCalculatedRadioButton.gridx = 0; constraintsCalculatedRadioButton.gridy = 5;
		constraintsCalculatedRadioButton.anchor = java.awt.GridBagConstraints.WEST;
		constraintsCalculatedRadioButton.insets = new java.awt.Insets(0, 20, 0, 0);
		add(getCalculatedRadioButton(), constraintsCalculatedRadioButton);

		java.awt.GridBagConstraints constraintsDemandAccumulatorRadioButton = new java.awt.GridBagConstraints();
		constraintsDemandAccumulatorRadioButton.gridx = 0; constraintsDemandAccumulatorRadioButton.gridy = 4;
		constraintsDemandAccumulatorRadioButton.anchor = java.awt.GridBagConstraints.WEST;
		constraintsDemandAccumulatorRadioButton.insets = new java.awt.Insets(0, 20, 0, 0);
		add(getDemandAccumulatorRadioButton(), constraintsDemandAccumulatorRadioButton);
	} catch (java.lang.Throwable ivjExc) {
		handleException(ivjExc);
	}
	// user code begin {2}
	
		
	// user code end
}
/**
 * main entrypoint - starts the part when it is run as an application
 * @param args java.lang.String[]
 */
public static void main(java.lang.String[] args) {
	try {
		java.awt.Frame frame = new java.awt.Frame();
		PointTypePanel aPointTypePanel;
		aPointTypePanel = new PointTypePanel();
		frame.add("Center", aPointTypePanel);
		frame.setSize(aPointTypePanel.getSize());
		frame.addWindowListener(new java.awt.event.WindowAdapter() {
			public void windowClosing(java.awt.event.WindowEvent e) {
				System.exit(0);
			};
		});
		frame.setVisible(true);
	} catch (Throwable exception) {
		System.err.println("Exception occurred in main() of com.ibm.uvm.abt.edit.DeletedClassView");
		com.cannontech.clientutils.CTILogger.error( exception.getMessage(), exception );;
	}
}
/**
 * Insert the method's description here.
 * Creation date: (6/26/2001 3:49:16 PM)
 */
public void setButtons(Object val)
{
	//sets buttons visible or not depending if point is off LMGroup 
	int deviceID = ((com.cannontech.database.data.point.PointBase) val).getPoint().getPaoID().intValue();

	com.cannontech.database.cache.DefaultDatabaseCache cache =
		com.cannontech.database.cache.DefaultDatabaseCache.getInstance();
	synchronized (cache)
{
		java.util.List allDevices = cache.getAllYukonPAObjects();
		for (int i = 0; i < allDevices.size(); i++)
		{
			if (((com.cannontech.database.data.lite.LiteYukonPAObject) allDevices.get(i)).getYukonID() == deviceID)
			{
				if (((com.cannontech.database.data.lite.LiteYukonPAObject) allDevices.get(i)).getPaoClass() == com.cannontech.database.data.pao.DeviceClasses.GROUP)
				{
					getCalculatedRadioButton().setEnabled(false);
					getAccumulatorRadioButton().setEnabled(false);
					getCalculatedRadioButton().setVisible(false);
					getAccumulatorRadioButton().setVisible(false);
					getDemandAccumulatorRadioButton().setEnabled(false);
					getDemandAccumulatorRadioButton().setVisible(false);
				}

			}
		}
	}
}
/**
 * Insert the method's description here.
 * Creation date: (4/30/2001 3:23:30 PM)
 * @param newPointType int
 */
public void setPointType(int newPointType) {
	pointType = newPointType;
}
/**
 * This method was created in VisualAge.
 * @param val java.lang.Object
 */
public void setValue(Object val) {
}
/**
 * 
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private static void getBuilderData() {
/*V1.1
**start of data**
	D0CB838494G88G88GD4F954ACGGGGGGGGGGGG8CGGGE2F5E9ECE4E5F2A0E4E1F4E14DDB8DD0D4D716C69395FFE2B01BEC390ED36BA4A663EC189AB3CB265C99E647351C9D33BA33EE19D432D9B7454ED8D9ABE3E61DD9EB56A4EC6D25D1C42565C704062090443F89C1C4A0CAC8820A46D6B24388B198C84203FE508D5D7D5A572F6D46206EB9773EFB5F6B66B5E22AD2952A0E6F5D6F1EFB4F4FBD775C1FFE124272C55BB3EC0504E4ECA77A5F5B9904389A88794BE7DFCE62D8023CA20B18
	7E5EG4AA74E3CED70CC023A1C85AB327248155B59D0DE8C656A87D6E495437BDE52F3FB2C9ADE624869023A512DDB87A61673021D4B59C14B970AF2603987E889E0785CA2237C3EA2070E2F51F1CE99B6C2D68EA24F97C5E5BAF68A4A9DC0730112878D7E1DD4723426D3FA39F75EA232651F0E586C580F29CFCEB9C32CED2D7E2CA05F4BF8EE88E5AD26FAE29BB714978121711964711CF760595CB1B2745E00622B99107CFE452F49836E1A306706243AEA865C12FFC856C683F2CDC071783526268EFCF48150
	A179E5AF69F2E37F1B0A32E9FF0414C1B90DE22FE9A8F3BE3CAF85DA85446CF977C8332EEF77975FA3A94F7AB3F36358D365E1F634FB8CFB9E8C3D3A23047A7EC0AE7AB8916A12011683EDG5AB63CA22B8A68F5BA6607767C811E2D7D5AD94567D37C2D0140992FCB7DD0F149DEF8EFEA029A9D3BAC8FA82A0B90B62EFFD3D4ED214FC4445AAD3B6BF64CA7CBCE72B1CE3E7C98D97F3717ECBBACFC32FEC92D2DF9187922EB587089137D5B3832356168980C167DB1B14BDEBCDD495CEEA1BB6519E336C43D1F05
	A659EB06F12C15900EF5AA3CE768F14070D10A670D98B80B478C0AE339826A6E9D6131513D076B523EED8149BB51EE690779ABDAED9BC7188E44EB68426262C8BE4E2BEA40AFG7581C500668325G2D727258706FB9BE49586810D46DCC4863F549AAE1ED0F649641D30EE82A647487B549BFA0DF11D40F546F1503C467294DC55D7B235AB550189DG999F48FED915B40F62075EA51F2C49EA10713C171B0FBC2ACCEB3BBCBE998478820477FBBEF7AF1AAD85350F82AEC9138D8ED67F47DC7465F5698A74889D
	40BB73CBD7817AFCA358CFFCB086BE6E0727E8FF17E49526F3D3D33B220C0482DDE88969C3BE12DD0CF33B8A5E1B01D8DF9DFBE9FE05F22A68076495187AE9738C207A12BA4A67C6A80439F32D0F0F75EF3A79D837599FA5554FDC349CEB24CDBEB6465B7C31B1FF1B7609FA31720ED077DD4E3FBD3ACE131F6395DFCAC666A47918FF7F3E90472A9A6C3DG6476F39F3F13D741E2760C9414F9147EC905G13DD0647996F09FC4C2B72F5592F354981596F0217209EA32772F79A71BB280858FA0F628108773390
	E0C1013765E3CCCA1A26FA7AC31A5CA9EB1A47BFE45437D3D941D0FF4018AADCD6E7BE6693A58C71AE783B738B68BC707BE52A34E82257FFC2E3D5764ABED07A2C9272EB8C6FAB40719E11C7753962AA4822316E8D613CE3EB5AD2E9B75A20FAFC608A1C3AE4039F63383C182F49C88900615A3C8C1FFE9E136103BCA62BEEFC0BAC7F7C104DAA974EFF344DD62D3018E9D10C18E4BAFD3457DA779EBAE7F4BC48F4ED4C4636B1CB2A9E83B63AE677C17F9A509C05474033FBCBCC73CC9F7C330A574BDCA9FC3C32
	B08F47C43E9612A1BD61043EA47C363A30A0266E0A6EC3D63F2E90FD68B56582266F0E4FF10D315AAF8C05BCB5A14D03B3DF52A02882A10DDA437687B3E98E41BE96ABFCFFB0D60A762D077296201CG9F03FF31737D4103A30BC85AA325F177BBD581667B8B81E38CAA6E617B03F6AAB3916A6687386F2E6EB3673A56E0D0997050B42B5B1DDDE49EA306590BF641F3502386B5A7CC228877515EA2549773308E6B79E1EEF16CBA1C9CB0724B724078DC43564AEC877A6369GDBAB6D82AF2678D6934E56502954
	FFD8AE3B36A2AB719A4F4DB39D9867DB201C272E48BA8A548254871420F2BFFC2368A439435A6704F1D57D1237B3EAD26B367F39B8134DC33D7CD171F6BE8FB1E6693A71A8757FC210399465D36C578E441E0672A615676CBDD45F3CA06C55015A013CC0C9C0A9C14E331F722C07F2BAD01986EB91D0AF501846F992CB7BACE284495C9F4AB157F119134D63CCD6F2F1BCD998933B75F1094D795B7576131545FC7B00A3FE9E7FAB07F59E7FEB07F59EFF54114FF068433C2B61755FF5CC1C671FF4D86579CCDA17
	DA1CC56743289252A07EB49C6FF5A267AE04F20A467C3CDEE3FB8916D31E88233F86E1938422BA8372GD99B423636925E36994A9DC08120A42025C0CCA6A128CF270C398F93E83B548F89145721AD2D90424A50E54965D14E04B40D66C7F3FD27A6E92120F5BD6E71DA8786C23E10D75294B50A09736C83BAABF98710C5F6D975B3E7G7723ED32CF723B2CFB6B380E76F6BA8ACDE36F384EFC548CCFD63F7C1A39FE3EDE3FD2541F33DB55BFA76AEF072C6AB709FAA5ECD5CF423C7E06E5FDA15433FC74DAB1BF
	E39E852CAF4C4777C49F5ACF570CE1693AD4A305619479016AF11D11065AE934307560944DF373215DD220D5E13E9EC48E7375A079318524BD611265FA1056D5E7CB8B6B7B4408319EAC0173A2DB032617E46B6DDA20BE92C1AC2544EC930E17848CB5A815563F98D50F71F20C186B9D2566FA5477C31B397EE2D47DF6D3FBE65B6AC35C367455CB08774953F1573A653ACD9B74E747150324EA56EBBB180F7FFB50E176F124A69806495454F411CE88E67FAC1AFBF12E150DF25F5EBCEFDEEBD3F36BEC3B63486F
	9AE5F235D143375560DBA63F684C81EB79D193026971BF573275F91B82FDB1EC4C0E985A6E3841319F04B8B6A63047429CCB93589382EB7B022F85990EAFF7FE4F3D6BF6EC0DCE26313B648B36C6E7887CC90A2718F0B6D75EF37039329C6A2A46785A7D0BD2FA27816545C029C0CF83ED814A3B49576E0FCAEA499D566EAEA5508E8B0657146219FD33CB3E5C7D44BF6C60F1127243074932692FE4440B132A1BACBE5A7467462E0326F30DD40676E9D0178414823486A88DE82B306F1BD4C730621C4B2335EA1A
	B460263BB1EE5C950F9CB6E0DCB018ED77C70DDD4EADBDCE75E7BDBCAFG755EB256FA26536A7DB8C783004F81DA8C148A347E965767BFCB6E38D7BA97315CABB13D522FD91D9753F5BD72EE33E755EDC32FD37AFB7B6D68FDC99F1435980C4918E23712112873AE541FCBEF0F5F672C0C411E0372A6CB99BF32BC77128446DB28BF0FA60C17519C03F5C05915E0A5637130150C084EBBC7FFAE32101192032D0272FACB99BF301431D9675DB605BD9DD3464B280E41DA205CB545CA4677B14C395FCC6FED0EAFF3
	F7B0D9E2B2BE7DCAE568E7505278FB5297CA2D77249BCA2D7724AF15CE3C677CDDE97C3B05D7CA4DF78BECBE5B4BF1BE3BE1DC6E85DA82148A34F68A1F4FB3DE2FA4F4BEDB6416F32EA1792CC2EF74B4AEFF38AC3E2D574A6EFC8FF8238C4F0F7D8A2C01BE6C1F6389E5B8DE1A9220A0432615210FD44F10DBE32CBABE3B8C57B82FBCC8E1F616FA0C2E671B402E74A97CFC350EE2F9D3D7E4D5CC6558885D7754C339DDE03F9AC32C8F4A9101E5EA7CECB6E7AA77553A7D71EDFFFE3F75B8276D379E679777CFFC
	76784D7EF8F14074BB2022FE6BC137CDC267A7D4EE9B3907E3EFB991AB04F22D40E6D32C8D4A5D82FB5A497D679558300D7BA0C1E01FB5F2DF2D9558A3F62E4B8601556D613AEC95582F6B38AEB977F26C4CC9AE37C2E02B0B385CFA01596B395CFB85766FA1AEF73E405E3B4E65AE96584BA7395CE5823BD54F652EB1F4A961F2538456AB6C2D1D46311F07395CB601FD9A61F23B8576E6A3176B9558BD0DDCEE0240AA055C8785763E103BC1E04FD139DB211CBD1DE36FD23905D02E9658078D0835C039D7E09B
	A89600F20240CA8C39829B20F2B7C0B9FDBA0F7BFD6571633E2C7C4EF37ED0B93DC702390A27D2067D219C4F474A60E0D0963965F8F91E401C418EA568414BA8F6EE7C31836FBA87953F56BE43B437613DEC86BF7F3EC4F9B4D0067190EADF923CAF94BCD3CBF02F6613A29EDF48576939A193463B00722E047735027755B14A6B714770965CC45EF4F84FCEDC1165CEC42CD4EB1BE01D607B5DCC1D6A214D852003041D03FA930DBDB1135D20C65BF2094E6D2540178A443067A936964A6B93394EEDCE5C7BCB74
	729337E8F04C06B2637F54497B481E49799F26D8105E9948AE4677F7CE717B65CCAE6BFFA9D68D65D3B3394DD3ECF732B953C4EEE8B7B613590CF5C9B38D1B190C99B47696823EC448DDC3F34B53D0CE93D88F4DADDB20CCE6F12C70A4672B96588845DA30BC0B6B7C4B22BB693C5D24739834CB1AE5683CF8D62C4E0DB40FAC85BC95E863AC7C1DBC7BB09175C3B49FD54F863F4D66FA9551B6AEA8878476ED0A4D01727C59DC572BA5F752F507C957D550EEBD509BFA5D6659313ACE237ECD87BCFB8E177BFB1A
	D36A215CA23069B427F4C359A5302EC64E17A430E40AAD01F24A9C2E7369BB6A3C5B247386E837F50E615F1C7BE2F5F6D3998E402B6E63B17BDFE1BEB7183FDB0E90B1EF3AA256735BCBED6A00BEFAC5BF2E0678F365A64D1709407B2060FF2DA1BA8765D35D1221FE59FD5C7EDAE17F467B466F19512E1DA67B5301A7EF2EE1FF555C58BCF02351BA87BDC0E55782FF73DC9EF769EF9AFE78198377F5FEB9DC412F19EA4E0362C7ABDD9E3C822C2C2E282D2EB8D1FF0C1FE997054D5FD51C5A753665EFBDC86E39
	4C2EB17D19BA63A0E1729FF84B10AF15E0CE2FE41711951ADB2E404FA6AA14418A7665C4255E262356E8735CD8F92C4EED9E5811628F37C7CF541F8605CF9F535B5DBEE1345B9A1CCC3BF2426EA8330971E705317E3FDBC788774DFF647073FE59635FA0ED4F3E124147EB5C39CB7749367B0DFB210D4369BBD81F4575064E49B4F7D7328B59CA5D2686233E52F9A03ECD0DE0D25D5B47B86FF42739AF062DF552B92206E4567761D3C65F1F59A69B9BF767B367E9635D5BE448BBEA1F0CAD2C5D7D6F986D5E22F9
	3E12DDCA6BBE7AFE33D11FD6B7D13F8DD1BEFA7764783E763CEF747513222F46A74DAD467B672D063C7E50E4F4E771774FBD66391937608D9B4F913171572C475D85F37CD159BFBBE3487E65490966CB6DC9A61C35FB692C51AEDCEF27F1E6DC5BD78A5BFEF15E60EBAC79EAFC795B8F0C77C227A16FF2F872763CFD51E877F1846F9DAB0D9FAC743878641241D351B841B81D2E0B0A311B0D567DF97FEC74D7749579E641E5637D471D063CCF4313511F35CB6BB25A1DE918FC3B4CABC63B979A2CFDB05CED7058
	A76D83F627AD15705F4B47A02E6F4EAFAFFDEC3C5B2F1A7454A6E39F3BC37961D56CB785E42F3D1F1F09DA2BF1313DA28D6BB720F8BD2B28746684408ED589482AB64AD07AB5015E576FBF417D41E668A7A7894E14C09700FA01B420C42005C0CB01D6812581ED814A1E87679C20EA2096202EF9DC0F5F559D1BC08F7D07A5FA0923CA9E3C465175686DC5DF30BE9EE9BD942F0F56FE6DA27867CF7D43781B22B4C4783D5AE0545D399B74991BB7716F46CC56CF2E623B3857E02E516B7E71AA5ED541D6C703B870
	5C201FA2F48223FF08556F6A09FF8E4D6F3973B93434477A6E2922E77C5D53219E6B3B27B7FB4CF7CF7A37EDBD981BC3606BEE4157A0302B82EB22FDA27631472539855EC275C27C020C824537EDD42FB0453AF56C12893B2AE39DBDF81686135D0A4A5BFE54C3772A9E2FD77F560372F87CF034967E780C6A6889C8A63D878406FA33BB011B6C9CBD0F6DCD99B615E2CBF40C1D192443B94678D19399DE96319BB8B711306F10336D6B4020641749F60D64F7043CC13942343EE824007DD4EC8603AA31C7A50E1F
	FE2B6145B5AFDC89E612ACA7A9417DF9272C529012DD787D0D7E6D5FF932136EBDF1976DA45B027431BD280DFA65F6D2B8F8993F7F89C246EA65419954483EF614F49C3F65B9FEC18A3AB58C28F75A49B6BFFE25C88AD835D7728F9DE7117ACE843FCD4CE86D23B988E31D003EFB293E58CB0D68E56F0FF28F3DF1F14332CCE2BBCFF6F99549F59E0EA20A6AA4BBB537AA875D0A5795893C5FC2BB4A8E731FABB3213B570579E8ECCD97043DD0FAA4724DB9760D7673B6C2A2AAB9CEB97128D4A3B8DCE75D128A22
	E4552961D7170ADAA56A269C508E7F7C0362DE9B49500D3CC6EE11ED1100C63286A51F47BB8AAFACCD8731A6DB83EDD5499F9CD4D49FE8D69123992F9282EA7E62295A073E736AA7D40051D4A3654C15507478102C780E378E8EFA7C3268G7C8A5D6F975DA3CFCD8CCFC1E45E3E0B4F7CDFBF44C19B4974555652FFCF50FF6B705F9394B9C11193F5605C4DE4423F587A883719868F6813CB7541612FB98FA328BD7F72D38F7FDB7366CDE05DF9321DFE81840310E7F07DB1646B175569498F6D5F7DE05AEC9C23
	G06CA847B4D09E824A46E018924950CDF2ADA677E6B33491F37955A084DC9325CF4EEC1D007F17251C844D571A12AF629A8F13D31A7A74D88E957CFB6E57E6A11CC9B59865D308C89D384539C742632DEB3A1C352BE237EA09E5841C38841C4F20E16EC63268222D963E39A61DDA23230E4272C39745F023B9EA7ECD5C85B9689779EE42337DF67A9644C51FA03926C0C908537FE7D4CF9EBB26634C61506B4325B1CC73FF6B63DBD991B400ADD4676B22A2A776BE7D26BE4CC6A0DAE0DB770EBE55225C919241F
	DD0F5DB61FDD37CEF3443DD76AD840760F2E85317F0721A59F6DDB84F86A823E6FCD4A434B26F8FB56CE1F6475EA12F724556F427D6B19D1CDB6FEFBBB170F7D87B90FDE493F5DC63D07CDED7E9FD0CB87888B8422746492GG0CB5GGD0CB818294G94G88G88GD4F954AC8B8422746492GG0CB5GG8CGGGGGGGGGGGGGGGGGE2F5E9ECE4E5F2A0E4E1F4E1D0CB8586GGGG81G81GBAGGG9E93GGGG
**end of data**/
}
}
