package com.cannontech.dbeditor.wizard.copy.point;

/**
 * This type was created in VisualAge.
 */

 import java.awt.Dimension;

import com.cannontech.database.data.point.PointBase;
import com.cannontech.database.db.point.Point;

  public class PointCopyNameDevicePanel extends com.cannontech.common.gui.util.DataInputPanel implements javax.swing.event.CaretListener {
	private javax.swing.JLabel ivjNameLabel = null;
	private javax.swing.JTextField ivjNameTextField = null;
	private javax.swing.JComboBox ivjDeviceComboBox = null;
	private javax.swing.JLabel ivjDeviceLabel = null;
	private javax.swing.JPanel ivjPointCopyNameDevicePanel = null;
	private boolean copiedToSameDevice = false;
	public int originalDeviceID;
/**
 * Constructor
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
public PointCopyNameDevicePanel()
{
	super();
	initialize();
}
/**
 * Method to handle events for the CaretListener interface.
 * @param e javax.swing.event.CaretEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
public void caretUpdate(javax.swing.event.CaretEvent e)
{
	// user code begin {1}
	// user code end
	if (e.getSource() == getNameTextField())
		connEtoC1(e);
	// user code begin {2}
	// user code end
}
/**
 * connEtoC1:  (NameTextField.caret.caretUpdate(javax.swing.event.CaretEvent) --> DeviceCopyNameAddressPanel.fireInputUpdate()V)
 * @param arg1 javax.swing.event.CaretEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC1(javax.swing.event.CaretEvent arg1)
{
	try
	{
		// user code begin {1}
		// user code end
		this.fireInputUpdate();
		// user code begin {2}
		// user code end
	}
	catch (java.lang.Throwable ivjExc)
	{
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}
/**
 * Return the JComboBox1 property value.
 * @return javax.swing.JComboBox
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JComboBox getDeviceComboBox() {
	if (ivjDeviceComboBox == null) {
		try {
			ivjDeviceComboBox = new javax.swing.JComboBox();
			ivjDeviceComboBox.setName("DeviceComboBox");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjDeviceComboBox;
}
/**
 * Return the JLabel1 property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getDeviceLabel() {
	if (ivjDeviceLabel == null) {
		try {
			ivjDeviceLabel = new javax.swing.JLabel();
			ivjDeviceLabel.setName("DeviceLabel");
			ivjDeviceLabel.setFont(new java.awt.Font("dialog", 0, 14));
			ivjDeviceLabel.setText("Device:");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjDeviceLabel;
}
/**
 * This method was created in VisualAge.
 * @return java.awt.Dimension
 */
public Dimension getMinimumSize()
{
	return getPreferredSize();
}
/**
 * Return the NameLabel property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getNameLabel() {
	if (ivjNameLabel == null) {
		try {
			ivjNameLabel = new javax.swing.JLabel();
			ivjNameLabel.setName("NameLabel");
			ivjNameLabel.setFont(new java.awt.Font("dialog", 0, 14));
			ivjNameLabel.setText("New Point Name:");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjNameLabel;
}
/**
 * Return the NameTextField property value.
 * @return javax.swing.JTextField
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JTextField getNameTextField() {
	if (ivjNameTextField == null) {
		try {
			ivjNameTextField = new javax.swing.JTextField();
			ivjNameTextField.setName("NameTextField");
			ivjNameTextField.setFont(new java.awt.Font("sansserif", 0, 14));
			ivjNameTextField.setColumns(12);
			// user code begin {1}
			ivjNameTextField.setDocument(new com.cannontech.common.gui.util.TextFieldDocument(com.cannontech.common.gui.util.TextFieldDocument.MAX_DEVICE_NAME_LENGTH));
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjNameTextField;
}
/**
 * This method was created in VisualAge.
 * @return com.cannontech.dbeditor.wizard.point.PointSettingsPanel
 */
protected javax.swing.JPanel getPointCopyNameDevicePanel() {
	if (ivjPointCopyNameDevicePanel == null) {
		try {
			ivjPointCopyNameDevicePanel = new javax.swing.JPanel();
			ivjPointCopyNameDevicePanel.setName("PointCopyNameDevicePanel");
			ivjPointCopyNameDevicePanel.setLayout(new java.awt.GridBagLayout());

			java.awt.GridBagConstraints constraintsNameLabel = new java.awt.GridBagConstraints();
			constraintsNameLabel.gridx = 0; constraintsNameLabel.gridy = 0;
			constraintsNameLabel.anchor = java.awt.GridBagConstraints.WEST;
			constraintsNameLabel.ipadx = 5;
			constraintsNameLabel.insets = new java.awt.Insets(53, 35, 10, 18);
			getPointCopyNameDevicePanel().add(getNameLabel(), constraintsNameLabel);

			java.awt.GridBagConstraints constraintsNameTextField = new java.awt.GridBagConstraints();
			constraintsNameTextField.gridx = 1; constraintsNameTextField.gridy = 0;
			constraintsNameTextField.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintsNameTextField.anchor = java.awt.GridBagConstraints.WEST;
			constraintsNameTextField.weightx = 1.0;
			constraintsNameTextField.ipadx = 168;
			constraintsNameTextField.insets = new java.awt.Insets(53, 5, 10, 70);
			getPointCopyNameDevicePanel().add(getNameTextField(), constraintsNameTextField);

			java.awt.GridBagConstraints constraintsDeviceLabel = new java.awt.GridBagConstraints();
			constraintsDeviceLabel.gridx = 0; constraintsDeviceLabel.gridy = 1;
			constraintsDeviceLabel.anchor = java.awt.GridBagConstraints.WEST;
			constraintsDeviceLabel.insets = new java.awt.Insets(0, 35, 50, 5);
			getPointCopyNameDevicePanel().add(getDeviceLabel(), constraintsDeviceLabel);

			java.awt.GridBagConstraints constraintsDeviceComboBox = new java.awt.GridBagConstraints();
			constraintsDeviceComboBox.gridx = 1; constraintsDeviceComboBox.gridy = 1;
			constraintsDeviceComboBox.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintsDeviceComboBox.anchor = java.awt.GridBagConstraints.WEST;
			constraintsDeviceComboBox.weightx = 1.0;
			constraintsDeviceComboBox.insets = new java.awt.Insets(0, 5, 50, 70);
			getPointCopyNameDevicePanel().add(getDeviceComboBox(), constraintsDeviceComboBox);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjPointCopyNameDevicePanel;
}
/**
 * This method was created in VisualAge.
 * @return java.awt.Dimension
 */
public Dimension getPreferredSize()
{
	return new Dimension(350, 200);
}
/**
 * Insert the method's description here.
 * Creation date: (6/27/2001 2:06:03 PM)
 * @return java.lang.Object
 */
public Object getSelectedDevice()
{
	return getDeviceComboBox().getSelectedItem();
}
/**
 * This method was created in VisualAge.
 * @return java.lang.Object
 * @param val java.lang.Object
 */
public Object getValue(Object val)
{

	PointBase point = ((PointBase) val);
	com.cannontech.database.data.lite.LiteYukonPAObject liteDevice = (com.cannontech.database.data.lite.LiteYukonPAObject) getDeviceComboBox().getSelectedItem();
	String nameString = getNameTextField().getText();

	point.setPointID( Point.getNextCachedPointID() );
	point.getPoint().setPointName(nameString);
	point.getPoint().setPaoID(new Integer(liteDevice.getYukonID()));
	
	return val;

}
/**
 * Called whenever the part throws an exception.
 * @param exception java.lang.Throwable
 */
private void handleException(Throwable exception)
{

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
	getNameTextField().addCaretListener(this);
}
/**
 * Initialize the class.
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void initialize() {
	try {
		// user code begin {1}
		// user code end
		setName("PointNameDevicePanel");
		setLayout(new java.awt.GridLayout());
		setSize(350, 200);
		add(getPointCopyNameDevicePanel(), getPointCopyNameDevicePanel().getName());
		initConnections();
	} catch (java.lang.Throwable ivjExc) {
		handleException(ivjExc);
	}
	// user code begin {2}
		
	// user code end
}
/**
 * This method was created in VisualAge.
 * @return boolean
 */
public boolean isInputValid()
{
	if (getNameTextField().getText() == null || getNameTextField().getText().length() < 1)
	{
		setErrorString("The Name text field must be filled in");
		return false;
	}

	else
		return true;
}
/**
 * main entrypoint - starts the part when it is run as an application
 * @param args java.lang.String[]
 */
public static void main(java.lang.String[] args)
{
	try
	{
		javax.swing.JFrame frame = new javax.swing.JFrame();
		PointCopyNameDevicePanel aPointCopyNameDevicePanel;
		aPointCopyNameDevicePanel = new PointCopyNameDevicePanel();
		frame.getContentPane().add("Center", aPointCopyNameDevicePanel);
		frame.setSize(aPointCopyNameDevicePanel.getSize());
		frame.setVisible(true);
	}
	catch (Throwable exception)
	{
		System.err.println("Exception occurred in main() of com.cannontech.common.gui.util.DataInputPanel");
		com.cannontech.clientutils.CTILogger.error( exception.getMessage(), exception );;
	}
}
/**
 * Insert the method's description here.
 * Creation date: (6/4/2001 8:50:39 AM)
 * @param index int
 */
public void setSelectedDeviceIndex(int deviceID)
{
	//sets selected index of DeviceComboBox to selected device

	{

		for (int i = 0; i < getDeviceComboBox().getItemCount(); i++)
		{
			if ((new Integer(((com.cannontech.database.data.lite.LiteYukonPAObject) getDeviceComboBox().getItemAt(i)).getYukonID()))
				.compareTo(new Integer(deviceID))
				== 0)
			{
				getDeviceComboBox().setSelectedIndex(i);
				break;
			}

		}
	}
}
/**
 * This method was created in VisualAge.
 * @param val java.lang.Object
 */
public void setValue(Object val)
{
	//Sets point name field
	com.cannontech.database.data.point.PointBase point = (com.cannontech.database.data.point.PointBase) val;
	
	getNameTextField().setText(point.getPoint().getPointName());
	originalDeviceID = point.getPoint().getPaoID().intValue();

}
/**
 * Insert the method's description here.
 * Creation date: (5/1/00 4:17:27 PM)
 * @param val java.lang.Object
 */
public void setValueCore(Object val)
{
	//Load the device list
	getDeviceComboBox().removeAllItems();
	com.cannontech.database.cache.DefaultDatabaseCache cache =
		com.cannontech.database.cache.DefaultDatabaseCache.getInstance();

	synchronized (cache)
	{
		java.util.List devices = cache.getAllDevices();
		java.util.Collections.sort( devices, com.cannontech.database.data.lite.LiteComparators.liteStringComparator );
		
		for (int i = 0; i < devices.size(); i++)
			getDeviceComboBox().addItem(((com.cannontech.database.data.lite.LiteYukonPAObject) devices.get(i)));

	}
}

}
