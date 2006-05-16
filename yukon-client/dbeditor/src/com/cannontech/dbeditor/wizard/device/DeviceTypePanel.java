package com.cannontech.dbeditor.wizard.device;
/**
 * This type was created in VisualAge.
 */
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.GridLayout;

import com.cannontech.database.data.device.DeviceBase;
import com.cannontech.database.data.pao.PAOGroups;
 
public class DeviceTypePanel extends com.cannontech.common.gui.util.DataInputPanel implements javax.swing.event.ListSelectionListener 
{
	private javax.swing.JLabel ivjTypeLabel = null;
	private int devicetype = 0;
	//CHANGE THESE STRINGS TO CONSTANTS AT LEAST AND GET THE THE STRINGS
	//BY CALLING DeviceTypes!!!!
	String deviceCategory[] = {
									"MCT",
									"Signal Transmitter",
									"Electronic Meter",
									"RTU",
									"Virtual"
								};
	String deviceType[][] = {
								{	//MCTs
									PAOGroups.STRING_MCT_470[0],
                                    PAOGroups.STRING_MCT_430A[0],
                                    PAOGroups.STRING_MCT_430S[0],
									PAOGroups.STRING_MCT_410IL[0],
									PAOGroups.STRING_MCT_410CL[0],
									PAOGroups.STRING_MCT_370[0],
									PAOGroups.STRING_MCT_360[0],
									PAOGroups.STRING_MCT_318L[0],
									PAOGroups.STRING_MCT_318[0],
									PAOGroups.STRING_MCT_310CT[0],
									PAOGroups.STRING_MCT_310ID[0],
									PAOGroups.STRING_MCT_310IDL[0],
									PAOGroups.STRING_MCT_310IL[0],                           
									PAOGroups.STRING_MCT_310IM[0],
									PAOGroups.STRING_MCT_310[0],
									PAOGroups.STRING_MCT_250[0],
									PAOGroups.STRING_MCT_248[0],
									PAOGroups.STRING_MCT_240[0],
									PAOGroups.STRING_MCT_213[0],
									PAOGroups.STRING_MCT_210[0],
									PAOGroups.STRING_MCT_BROADCAST[0],
									PAOGroups.STRING_LMT_2[0],
									PAOGroups.STRING_DCT_501[0]
								},
								{	//Signal Transmitters									
									PAOGroups.STRING_CCU_710[0],
									PAOGroups.STRING_CCU_711[0],
									PAOGroups.STRING_LCU_415[0],
									PAOGroups.STRING_LCU_LG[0],
									PAOGroups.STRING_LCU_T3026[0],
									PAOGroups.STRING_LCU_ER[0],
									PAOGroups.STRING_TAP_TERMINAL[2],
									PAOGroups.STRING_REPEATER[1],
									PAOGroups.STRING_REPEATER_800[0],
                                    PAOGroups.STRING_REPEATER_801[0],
                                    PAOGroups.STRING_REPEATER_921[0],

									PAOGroups.STRING_RTC[0],
									PAOGroups.STRING_SERIES_5_LMI[0],
									PAOGroups.STRING_SNPP_TERMINAL[0],
									PAOGroups.STRING_TCU_5000[0],
									PAOGroups.STRING_TCU_5500[0],
									PAOGroups.STRING_WCTP_TERMINAL[0]
								},								
								{	//Electronic Meters
									PAOGroups.STRING_ALPHA_POWERPLUS[0],
									PAOGroups.STRING_ALPHA_A1[0],
									PAOGroups.STRING_ALPHA_A3[0],
									PAOGroups.STRING_DR_87[0],
									PAOGroups.STRING_FULCRUM[0],  // Schlumberger
									PAOGroups.STRING_ION_7330[0],
									PAOGroups.STRING_ION_7700[0],
									PAOGroups.STRING_ION_8300[0],
						   			PAOGroups.STRING_KV[0],
							   		PAOGroups.STRING_KVII[0],									
									PAOGroups.STRING_LANDISGYR_RS4[0],
									PAOGroups.STRING_QUANTUM[0],
									PAOGroups.STRING_SENTINEL[0],
									PAOGroups.STRING_SIXNET[0],
									PAOGroups.STRING_TRANSDATA_MARKV[0],
									PAOGroups.STRING_VECTRON[0],
	
									
									

								},								
								{	//RTUs
								    PAOGroups.STRING_DAVIS_WEATHER[0],
								    PAOGroups.STRING_RTU_DART[0],
								    PAOGroups.STRING_RTU_DNP[0],
									PAOGroups.STRING_RTU_ILEX[0],
									PAOGroups.STRING_RTU_MODBUS[0],
									PAOGroups.STRING_RTU_WELCO[0],
									PAOGroups.STRING_RTM[0]
								},
								{  // Virtual Devices
									PAOGroups.STRING_VIRTUAL_SYSTEM[0]
								}
							};
	
	private javax.swing.JList ivjDeviceCategoryList = null;
	private javax.swing.JScrollPane ivjDeviceCategoryScrollPane = null;
	private javax.swing.JList ivjDeviceTypeList = null;
	private javax.swing.JScrollPane ivjDeviceTypeScrollPane = null;
	private javax.swing.JPanel ivjListBoxPanel = null;
	private GridLayout ivjListBoxPanelGridLayout = null;

public DeviceTypePanel() {
	super();
	initialize();
}


/**
 * connEtoC1:  (TypeList.listSelection.valueChanged(javax.swing.event.ListSelectionEvent) --> DeviceTypePanel.typeList_ValueChanged(Ljavax.swing.event.ListSelectionEvent;)V)
 * @param arg1 javax.swing.event.ListSelectionEvent
 */
private void connEtoC1(javax.swing.event.ListSelectionEvent arg1) {
	try {
		this.deviceCategoryList_ValueChanged();
	} catch (java.lang.Throwable ivjExc) {
		handleException(ivjExc);
	}
}


/**
 * Comment
 */
public void deviceCategoryList_ValueChanged() {

	
	int selected = getDeviceCategoryList().getSelectedIndex();

	getDeviceTypeList().setListData( deviceType[selected] );

	getDeviceTypeList().setSelectedIndex(0);
	
	invalidate();
	repaint();
}


/**
 * Return the DeviceCategoryList property value.
 * @return javax.swing.JList
 */
private javax.swing.JList getDeviceCategoryList() {
	if (ivjDeviceCategoryList == null) {
		try {
			ivjDeviceCategoryList = new javax.swing.JList();
			ivjDeviceCategoryList.setName("DeviceCategoryList");
			ivjDeviceCategoryList.setFont(new java.awt.Font("dialog", 0, 14));
			ivjDeviceCategoryList.setBounds(0, 0, 160, 120);
			ivjDeviceCategoryList.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjDeviceCategoryList;
}


/**
 * Return the DeviceCategoryScrollPane property value.
 * @return javax.swing.JScrollPane
 */
private javax.swing.JScrollPane getDeviceCategoryScrollPane() {
	if (ivjDeviceCategoryScrollPane == null) {
		try {
			ivjDeviceCategoryScrollPane = new javax.swing.JScrollPane();
			ivjDeviceCategoryScrollPane.setName("DeviceCategoryScrollPane");
			getDeviceCategoryScrollPane().setViewportView(getDeviceCategoryList());
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjDeviceCategoryScrollPane;
}


/**
 * This method was created in VisualAge.
 * @return int
 */
public int getDeviceType() 
{
	return com.cannontech.database.data.pao.PAOGroups.getDeviceType( ((String)getDeviceTypeList().getSelectedValue()).trim() );
}


/**
 * Return the DeviceTypeList property value.
 * @return javax.swing.JList
 */
private javax.swing.JList getDeviceTypeList() {
	if (ivjDeviceTypeList == null) {
		try {
			ivjDeviceTypeList = new javax.swing.JList();
			ivjDeviceTypeList.setName("DeviceTypeList");
			ivjDeviceTypeList.setFont(new java.awt.Font("dialog", 0, 14));
			ivjDeviceTypeList.setVisibleRowCount(12);
			ivjDeviceTypeList.setBounds(-9, 0, 169, 120);
			ivjDeviceTypeList.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjDeviceTypeList;
}


/**
 * Return the DeviceTypeScrollPane property value.
 * @return javax.swing.JScrollPane
 */
private javax.swing.JScrollPane getDeviceTypeScrollPane() {
	if (ivjDeviceTypeScrollPane == null) {
		try {
			ivjDeviceTypeScrollPane = new javax.swing.JScrollPane();
			ivjDeviceTypeScrollPane.setName("DeviceTypeScrollPane");
			ivjDeviceTypeScrollPane.setVerticalScrollBarPolicy(javax.swing.JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
			getDeviceTypeScrollPane().setViewportView(getDeviceTypeList());
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjDeviceTypeScrollPane;
}


/**
 * Return the ListBoxPanel property value.
 * @return javax.swing.JPanel
 */
private javax.swing.JPanel getListBoxPanel() {
	if (ivjListBoxPanel == null) {
		try {
			ivjListBoxPanel = new javax.swing.JPanel();
			ivjListBoxPanel.setName("ListBoxPanel");
			ivjListBoxPanel.setLayout(getListBoxPanelGridLayout());
			getListBoxPanel().add(getDeviceCategoryScrollPane(), getDeviceCategoryScrollPane().getName());
			getListBoxPanel().add(getDeviceTypeScrollPane(), getDeviceTypeScrollPane().getName());
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjListBoxPanel;
}


/**
 * Return the ListBoxPanelGridLayout property value.
 * @return java.awt.GridLayout
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private java.awt.GridLayout getListBoxPanelGridLayout() {
	java.awt.GridLayout ivjListBoxPanelGridLayout = null;
	try {
		/* Create part */
		ivjListBoxPanelGridLayout = new java.awt.GridLayout();
		ivjListBoxPanelGridLayout.setHgap(20);
		ivjListBoxPanelGridLayout.setColumns(2);
	} catch (java.lang.Throwable ivjExc) {
		handleException(ivjExc);
	};
	return ivjListBoxPanelGridLayout;
}


/**
 * This method was created in VisualAge.
 * @return java.awt.Dimension
 */
public Dimension getMinimumSize() {
	return getPreferredSize();
}


/**
 * This method was created in VisualAge.
 * @return java.awt.Dimension
 */
public Dimension getPreferredSize() {
	return new Dimension( 350, 200);
}


/**
 * Return the TypeLabel property value.
 * @return javax.swing.JLabel
 */
private javax.swing.JLabel getTypeLabel() {
	if (ivjTypeLabel == null) {
		try {
			ivjTypeLabel = new javax.swing.JLabel();
			ivjTypeLabel.setName("TypeLabel");
			ivjTypeLabel.setFont(new java.awt.Font("dialog", 0, 14));
			ivjTypeLabel.setText("Select the type of device:");
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjTypeLabel;
}


/**
 * This method was created in VisualAge.
 * @return java.lang.Object
 * @param val java.lang.Object
 */
public Object getValue(Object val)
{
	//Determine the correct type of device and return it

	String typeString = (String) getDeviceTypeList().getSelectedValue();

	int type = com.cannontech.database.data.pao.PAOGroups.getDeviceType(typeString);
	devicetype = type;
	DeviceBase returnDevice = com.cannontech.database.data.device.DeviceFactory.createDevice(type);

	return returnDevice;
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
 * Initializes connections
 */
private void initConnections() throws java.lang.Exception {
	getDeviceCategoryList().addListSelectionListener(this);
}


/**
 * Initialize the class.
 */
private void initialize() {
	try {
		setName("DeviceTypePanel");
		setLayout(new java.awt.GridBagLayout());
		setSize(350, 200);

		java.awt.GridBagConstraints constraintsTypeLabel = new java.awt.GridBagConstraints();
		constraintsTypeLabel.gridx = 1; constraintsTypeLabel.gridy = 1;
		constraintsTypeLabel.anchor = java.awt.GridBagConstraints.WEST;
		constraintsTypeLabel.ipadx = 4;
		constraintsTypeLabel.insets = new java.awt.Insets(17, 12, 4, 175);
		add(getTypeLabel(), constraintsTypeLabel);

		java.awt.GridBagConstraints constraintsListBoxPanel = new java.awt.GridBagConstraints();
		constraintsListBoxPanel.gridx = 1; constraintsListBoxPanel.gridy = 2;
		constraintsListBoxPanel.fill = java.awt.GridBagConstraints.BOTH;
		constraintsListBoxPanel.anchor = java.awt.GridBagConstraints.WEST;
		constraintsListBoxPanel.weightx = 1.0;
		constraintsListBoxPanel.weighty = 1.0;
		constraintsListBoxPanel.ipadx = 264;
		constraintsListBoxPanel.ipady = 125;
		constraintsListBoxPanel.insets = new java.awt.Insets(4, 12, 9, 10);
		add(getListBoxPanel(), constraintsListBoxPanel);
		initConnections();
	} catch (java.lang.Throwable ivjExc) {
		handleException(ivjExc);
	}
	//Manually add the device types to the TypeList for now

	getDeviceCategoryList().setListData(deviceCategory);
	getDeviceCategoryList().setSelectedIndex(0);
}


/**
 * isDataComplete method comment.
 */
public boolean isDataComplete() {

	if( getDeviceTypeList().getSelectedValue() != null )
		return true;
	else
		return false;
}


/**
 * This method was created in VisualAge.
 * @return boolean
 */
public boolean isInputValid() 
{
	if( getDeviceTypeList().getSelectedValue() == null )
	{
		setErrorString("A device type must be selected");		
		return false;
	}
	else
		return true;

}


/**
 * main entrypoint - starts the part when it is run as an application
 * @param args java.lang.String[]
 */
public static void main(java.lang.String[] args) {
	try {
		Frame frame = new java.awt.Frame();
		DeviceTypePanel aDeviceTypePanel;
		aDeviceTypePanel = new DeviceTypePanel();
		frame.add("Center", aDeviceTypePanel);
		frame.setSize(aDeviceTypePanel.getSize());
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
 * This method was created in VisualAge.
 * @param val java.lang.Object
 */
public void setValue(Object val) {
	return;
}


/**
 * Comment
 */
public void typeList_ValueChanged(javax.swing.event.ListSelectionEvent listSelectionEvent) {
	fireInputUpdate();
}


/**
 * Method to handle events for the ListSelectionListener interface.
 * @param e javax.swing.event.ListSelectionEvent
 */
public void valueChanged(javax.swing.event.ListSelectionEvent e) {
	if (e.getSource() == getDeviceCategoryList()) 
		connEtoC1(e);
}

public void setFirstFocus() 
{
    // Make sure that when its time to display this panel, the focus starts in the top component
    javax.swing.SwingUtilities.invokeLater( new Runnable() 
        { 
        public void run() 
            { 
            getDeviceCategoryScrollPane().requestFocus(); 
        } 
    });    
}

}