package com.cannontech.dbeditor.wizard.point;

import java.util.List;

import com.cannontech.common.pao.PaoClass;
import com.cannontech.core.dao.DaoFactory;
import com.cannontech.database.data.lite.LitePoint;
import com.cannontech.database.data.lite.LiteYukonPAObject;
import com.cannontech.database.data.pao.DeviceClasses;
import com.cannontech.yukon.IDatabaseCache;

/**
 * This type was created in VisualAge.
 */

public class PointIDSettingsPanel extends com.cannontech.common.gui.util.DataInputPanel implements com.klg.jclass.util.value.JCValueListener, java.awt.event.ItemListener, javax.swing.event.CaretListener {
	private javax.swing.JComboBox ivjDeviceComboBox = null;
	private javax.swing.JLabel ivjDeviceLabel = null;
	private javax.swing.JLabel ivjNameLabel = null;
	private javax.swing.JTextField ivjNameTextField = null;
	private javax.swing.JLabel ivjPointIDLabel = null;
	private com.klg.jclass.field.JCSpinField ivjPointIDSpinner = null;
	private javax.swing.JLabel ivjUsedPointIDLabel = null;
public PointIDSettingsPanel() {
	super();
	initialize();
}
/**
 * Method to handle events for the CaretListener interface.
 * @param e javax.swing.event.CaretEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
public void caretUpdate(javax.swing.event.CaretEvent e) {
	// user code begin {1}
	// user code end
	if (e.getSource() == getNameTextField()) 
		connEtoC2(e);
	// user code begin {2}
	// user code end
}
/**
 * connEtoC1:  (PointIDSpinner.value.valueChanged(com.klg.jclass.util.value.JCValueEvent) --> PointIDSettingsPanel.PointIDSpinner_ValueChanged(Lcom.klg.jclass.util.value.JCValueEvent;)V)
 * @param arg1 com.klg.jclass.util.value.JCValueEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC1(com.klg.jclass.util.value.JCValueEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.PointIDSpinner_ValueChanged(arg1);
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}
/**
 * connEtoC2:  (NameTextField.caret.caretUpdate(javax.swing.event.CaretEvent) --> PointSettingsPanel.nameTextField_CaretUpdate(Ljavax.swing.event.CaretEvent;)V)
 * @param arg1 javax.swing.event.CaretEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC2(javax.swing.event.CaretEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.nameTextField_CaretUpdate(arg1);
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}
/**
 * connEtoC4:  (DeviceComboBox.item.itemStateChanged(java.awt.event.ItemEvent) --> PointSettingsPanel.deviceComboBox_ItemStateChanged(Ljava.awt.event.ItemEvent;)V)
 * @param arg1 java.awt.event.ItemEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC4(java.awt.event.ItemEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.deviceComboBox_ItemStateChanged(arg1);
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}
/**
 * Comment
 */
public void deviceComboBox_ItemStateChanged(java.awt.event.ItemEvent itemEvent) {

	getUsedPointIDLabel().setText("");
    int nextPointId = DaoFactory.getPointDao().getNextPointId();
    getPointIDSpinner().setValue(new Integer(nextPointId));
  
    revalidate();
	repaint();
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
			ivjDeviceComboBox.setFont(new java.awt.Font("dialog", 0, 14));
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
 * Return the JLabel3 property value.
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
 * Return the JLabel2 property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getNameLabel() {
	if (ivjNameLabel == null) {
		try {
			ivjNameLabel = new javax.swing.JLabel();
			ivjNameLabel.setName("NameLabel");
			ivjNameLabel.setFont(new java.awt.Font("dialog", 0, 14));
			ivjNameLabel.setText("Name:");
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
 * Return the JTextField1 property value.
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
			ivjNameTextField.setDocument(new com.cannontech.common.gui.util.TextFieldDocument(com.cannontech.common.gui.util.TextFieldDocument.MAX_POINT_NAME_LENGTH));
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
 * @return int
 */
public Integer getPointDeviceID() {

	try
	{
		return new Integer(((com.cannontech.database.data.lite.LiteYukonPAObject)getDeviceComboBox().getSelectedItem()).getYukonID());
	}
	catch(NullPointerException npe)
	{
		return new Integer(1);
	}
}
/**
 * Return the JLabel1 property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getPointIDLabel() {
	if (ivjPointIDLabel == null) {
		try {
			ivjPointIDLabel = new javax.swing.JLabel();
			ivjPointIDLabel.setName("PointIDLabel");
			ivjPointIDLabel.setFont(new java.awt.Font("dialog", 0, 14));
			ivjPointIDLabel.setText("Point ID:");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjPointIDLabel;
}
/**
 * Return the PointNumberSpinner property value.
 * @return com.klg.jclass.field.JCSpinField
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private com.klg.jclass.field.JCSpinField getPointIDSpinner() {
	if (ivjPointIDSpinner == null) {
		try {
			ivjPointIDSpinner = new com.klg.jclass.field.JCSpinField();
			ivjPointIDSpinner.setName("PointIDSpinner");
			ivjPointIDSpinner.setPreferredSize(new java.awt.Dimension(60, 22));
			ivjPointIDSpinner.setFont(new java.awt.Font("dialog", 0, 14));
			ivjPointIDSpinner.setBackground(java.awt.Color.white);
			ivjPointIDSpinner.setMinimumSize(new java.awt.Dimension(60, 22));
			// user code begin {1}
			ivjPointIDSpinner.setDataProperties(new com.klg.jclass.field.DataProperties(new com.klg.jclass.field.validate.JCIntegerValidator(null, new Integer(1), new Integer(1000000), null, true, null, new Integer(1), "#,##0.###;-#,##0.###", false, false, false, null, new Integer(0)), new com.klg.jclass.util.value.MutableValueModel(java.lang.Integer.class, new Integer(1)), new com.klg.jclass.field.JCInvalidInfo(true, 2, new java.awt.Color(0, 0, 0, 255), new java.awt.Color(255, 255, 255, 255))));
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjPointIDSpinner;
}
/**
 * Return the InvalidPointOffsetLabel property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getUsedPointIDLabel() {
	if (ivjUsedPointIDLabel == null) {
		try {
			ivjUsedPointIDLabel = new javax.swing.JLabel();
			ivjUsedPointIDLabel.setName("UsedPointIDLabel");
			ivjUsedPointIDLabel.setText("ID Already Assigned");
			ivjUsedPointIDLabel.setMaximumSize(new java.awt.Dimension(140, 20));
			ivjUsedPointIDLabel.setPreferredSize(new java.awt.Dimension(140, 20));
			ivjUsedPointIDLabel.setFont(new java.awt.Font("dialog.bold", 1, 14));
			ivjUsedPointIDLabel.setMinimumSize(new java.awt.Dimension(140, 20));
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjUsedPointIDLabel;
}
/**
 * This method was created in VisualAge.
 * @return java.lang.Object
 * @param val java.lang.Object
 */
public Object getValue(Object val) {
	//Assuming only a PointBase
	com.cannontech.database.data.point.PointBase point = (com.cannontech.database.data.point.PointBase) val;

	Integer pointID = null;
	Object pointNumSpinVal = getPointIDSpinner().getValue();
	if( pointNumSpinVal instanceof Long )
		pointID = new Integer( ((Long)pointNumSpinVal).intValue() );
	else if( pointNumSpinVal instanceof Integer )
		pointID = new Integer( ((Integer)pointNumSpinVal).intValue() );

	String name = getNameTextField().getText();
	com.cannontech.database.data.lite.LiteYukonPAObject liteDevice = (com.cannontech.database.data.lite.LiteYukonPAObject) getDeviceComboBox().getSelectedItem();

	point.setPointID(pointID);
	point.getPoint().setPointName(name);
	point.getPoint().setPaoID( new Integer(liteDevice.getYukonID()) );

	return val;
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
	getNameTextField().addCaretListener(this);
	getDeviceComboBox().addItemListener(this);
	getPointIDSpinner().addValueListener(this);
}
/**
 * Initialize the class.
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void initialize() {
	try {
		// user code begin {1}
		// user code end
		setName("PointSettingsPanel");
		setPreferredSize(new java.awt.Dimension(350, 200));
		setLayout(new java.awt.GridBagLayout());
		setSize(350, 200);

		java.awt.GridBagConstraints constraintsPointIDLabel = new java.awt.GridBagConstraints();
		constraintsPointIDLabel.gridx = 0; constraintsPointIDLabel.gridy = 2;
		constraintsPointIDLabel.fill = java.awt.GridBagConstraints.HORIZONTAL;
		constraintsPointIDLabel.anchor = java.awt.GridBagConstraints.WEST;
		constraintsPointIDLabel.insets = new java.awt.Insets(5, 0, 5, 0);
		add(getPointIDLabel(), constraintsPointIDLabel);

		java.awt.GridBagConstraints constraintsNameLabel = new java.awt.GridBagConstraints();
		constraintsNameLabel.gridx = 0; constraintsNameLabel.gridy = 0;
		constraintsNameLabel.fill = java.awt.GridBagConstraints.HORIZONTAL;
		add(getNameLabel(), constraintsNameLabel);

		java.awt.GridBagConstraints constraintsDeviceLabel = new java.awt.GridBagConstraints();
		constraintsDeviceLabel.gridx = 0; constraintsDeviceLabel.gridy = 1;
		constraintsDeviceLabel.fill = java.awt.GridBagConstraints.HORIZONTAL;
		constraintsDeviceLabel.anchor = java.awt.GridBagConstraints.WEST;
		add(getDeviceLabel(), constraintsDeviceLabel);

		java.awt.GridBagConstraints constraintsPointIDSpinner = new java.awt.GridBagConstraints();
		constraintsPointIDSpinner.gridx = 1; constraintsPointIDSpinner.gridy = 2;
		constraintsPointIDSpinner.anchor = java.awt.GridBagConstraints.WEST;
		constraintsPointIDSpinner.insets = new java.awt.Insets(5, 10, 5, 0);
		add(getPointIDSpinner(), constraintsPointIDSpinner);

		java.awt.GridBagConstraints constraintsNameTextField = new java.awt.GridBagConstraints();
		constraintsNameTextField.gridx = 1; constraintsNameTextField.gridy = 0;
		constraintsNameTextField.fill = java.awt.GridBagConstraints.HORIZONTAL;
		constraintsNameTextField.anchor = java.awt.GridBagConstraints.WEST;
		constraintsNameTextField.insets = new java.awt.Insets(5, 10, 5, 25);
		add(getNameTextField(), constraintsNameTextField);

		java.awt.GridBagConstraints constraintsDeviceComboBox = new java.awt.GridBagConstraints();
		constraintsDeviceComboBox.gridx = 1; constraintsDeviceComboBox.gridy = 1;
		constraintsDeviceComboBox.fill = java.awt.GridBagConstraints.HORIZONTAL;
		constraintsDeviceComboBox.anchor = java.awt.GridBagConstraints.WEST;
		constraintsDeviceComboBox.insets = new java.awt.Insets(5, 10, 5, 25);
		add(getDeviceComboBox(), constraintsDeviceComboBox);

		java.awt.GridBagConstraints constraintsUsedPointIDLabel = new java.awt.GridBagConstraints();
		constraintsUsedPointIDLabel.gridx = 1; constraintsUsedPointIDLabel.gridy = 2;
		constraintsUsedPointIDLabel.anchor = java.awt.GridBagConstraints.WEST;
		constraintsUsedPointIDLabel.insets = new java.awt.Insets(5, 80, 5, 0);
		add(getUsedPointIDLabel(), constraintsUsedPointIDLabel);
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
	if( getNameTextField().getText().length() <= 0 )
	{
		setErrorString("The Name text field must be filled in");
		return false;
	}

	if( getUsedPointIDLabel().getText().equalsIgnoreCase("ID Already Assigned") )
	{
		setErrorString("The PointID selected is already used");
		return false;
	}
	
	return true;	
}
/**
 * Method to handle events for the ItemListener interface.
 * @param e java.awt.event.ItemEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
public void itemStateChanged(java.awt.event.ItemEvent e) {
	// user code begin {1}
	// user code end
	if (e.getSource() == getDeviceComboBox()) 
		connEtoC4(e);
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
		PointSettingsPanel aPointSettingsPanel;
		aPointSettingsPanel = new PointSettingsPanel();
		frame.add("Center", aPointSettingsPanel);
		frame.setSize(aPointSettingsPanel.getSize());
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
 * Comment
 */
public void nameTextField_CaretUpdate(javax.swing.event.CaretEvent caretEvent) {
	fireInputUpdate();
}
/**
 * Comment
 */
public void PointIDSpinner_ValueChanged(com.klg.jclass.util.value.JCValueEvent arg1) {
    Object pointIdVal = getPointIDSpinner().getValue();
    int pointId = -1;
    if(pointIdVal instanceof Long) {
        pointId = ((Long)pointIdVal).intValue();
    }        
    else if(pointIdVal instanceof Integer) {
        pointId = (Integer) pointIdVal;
    }

    LitePoint lp = DaoFactory.getPointDao().getLitePoint(pointId);
    if(lp != null) {
        getUsedPointIDLabel().setText("ID Already Assigned");
    }

	revalidate();
	repaint();
	fireInputUpdate();
	return;
}
/**
 * This method was created in VisualAge.
 * @param val java.lang.Object
 */
public void setValue(Object val) {
}
/**
 * Insert the method's description here.
 * Creation date: (5/1/00 4:17:27 PM)
 * @param val java.lang.Object
 */
public void setValueCore(Object val, Integer initialPAOId )
{
	//Load the device list
	IDatabaseCache cache = com.cannontech.database.cache.DefaultDatabaseCache.getInstance();
	synchronized(cache)
	{
		List<LiteYukonPAObject> devices = cache.getAllDevices();
		java.util.Collections.sort( devices, com.cannontech.database.data.lite.LiteComparators.liteStringComparator );
		
		if( getDeviceComboBox().getModel().getSize() > 0 )
			getDeviceComboBox().removeAllItems();

		for (LiteYukonPAObject liteYukonPAObject : devices) {
			if(DeviceClasses.isCoreDeviceClass(liteYukonPAObject.getPaoType().getPaoClass().getPaoClassId()))
			{
				getDeviceComboBox().addItem(liteYukonPAObject);

				if( initialPAOId != null && initialPAOId.intValue()
					 == liteYukonPAObject.getYukonID() )
				{
					getDeviceComboBox().setSelectedIndex( getDeviceComboBox().getItemCount() - 1 );
				}
				
			}
		}
		getUsedPointIDLabel().setText("");
	}

    int nextId = DaoFactory.getPointDao().getNextPointId();
	getPointIDSpinner().setValue(nextId);
}
/**
 * Insert the method's description here.
 * Creation date: (5/1/00 4:17:27 PM)
 * @param val java.lang.Object
 */
public void setValueLM(Object val, Integer initialPAOId) 
{
	//Load the device list
	IDatabaseCache cache = com.cannontech.database.cache.DefaultDatabaseCache.getInstance();
	synchronized(cache)
	{
		List<LiteYukonPAObject> devices = cache.getAllLoadManagement();
		java.util.Collections.sort( devices, com.cannontech.database.data.lite.LiteComparators.liteStringComparator );
		
		if( getDeviceComboBox().getModel().getSize() > 0 )
			getDeviceComboBox().removeAllItems();

		for (LiteYukonPAObject liteYukonPAObject : devices) {
			if( liteYukonPAObject.getPaoType().getPaoClass() == PaoClass.GROUP )
			{
				getDeviceComboBox().addItem(liteYukonPAObject);

				if( initialPAOId != null && initialPAOId.intValue()
					 == liteYukonPAObject.getYukonID() )
				{
					getDeviceComboBox().setSelectedIndex( getDeviceComboBox().getItemCount() - 1 );
				}
	
			}
		}
	}

    int nextId = DaoFactory.getPointDao().getNextPointId();
	getPointIDSpinner().setValue(nextId);
}

public void setFirstFocus() 
{
    // Make sure that when its time to display this panel, the focus starts in the top component
    javax.swing.SwingUtilities.invokeLater( new Runnable() 
        { 
        public void run() 
            { 
            getNameTextField().requestFocus(); 
        } 
    });    
}

/**
 * Method to handle events for the JCValueListener interface.
 * @param arg1 com.klg.jclass.util.value.JCValueEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
public void valueChanged(com.klg.jclass.util.value.JCValueEvent arg1) {
	// user code begin {1}
	// user code end
	if (arg1.getSource() == getPointIDSpinner()) 
		connEtoC1(arg1);
	// user code begin {2}
	// user code end
}
/**
 * Method to handle events for the JCValueListener interface.
 * @param arg1 com.klg.jclass.util.value.JCValueEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
public void valueChanging(com.klg.jclass.util.value.JCValueEvent arg1) {
	// user code begin {1}
	// user code end
	// user code begin {2}
	// user code end
}
/**
 * 
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private static void getBuilderData() {
/*V1.1
**start of data**
	D0CB838494G88G88GF6F954ACGGGGGGGGGGGG8CGGGE2F5E9ECE4E5F2A0E4E1F4E14DBB8BF494D516B882864C88E8C05641590002F1A6B3139951C507F5B007DDF1E4940F30B2BB0C87D6D61933F18EFB16BD0B8E2EAF9D9202848990A18C08D102029B1BG91C38C1F10A6924806AC891DGC1D2692E6E94696EEA2B2B134EC732773ED72F2A3AD39D40F14E314F3929FA773DFB5F3D775DFB5FFDD595D270DEF2F6262D00104CEC227DBE4BA4A42D121054672BDE6638E1E2FA8EB17D5E87
	58C82EB8D743F5AC7435CCCE4F49A76E6ADCE85F8F6D4844741C2DF03F19C855F38E40CD1CF912216F75CF72DCC34F63ED64732C216DD10DF9F0FD9BA085F0F8DDA1627C139B8BB57CDC8D4FA153C648C28F0E195938C34315C33B81A089A04543464F026BDABA73080AF2EDDC6B38E43262579F5956A39F93CF8EC5DEC66B502EC564E71999DE1C6BDF291CC8538F6D69G28FC9679C96FA138D6B6F4FA0F3AE43F5DA58482F2C095DD9D76EE29D7D05C7620AC85540A0A863C54BA9AC5D515825ED0039090FD04
	F4F4A06FCF1AF2A9AFC2CA213D02624AA3B8DFAA5C4F86D824635F2478F28974856810380EC7BE2A54F4E8697B99E9FDA11035A10E0E43A426DB0AE4681894FEFD4DB63A9E8FB4215D17C2DF6E55741C9DG47815AG0681EC27FE7053460F61DA552ED64BFE3F9C288A86CF785C4A7F4AEE51877795955023614E09AED9F19342567A5F9ACBAC64998A985DEBB7CD47EC12F2102FFB6A39E9E451C3E7ECEBACEC32A82554B671AA33C55AD543A6EC6E83CD71661EF7B55AAF2367EEA666396BC64AD95996F3CFDF
	305736D4633352B4771ECEDC6BD43A5605F01F29798743BFC271BE931E7968D10A47F6BF74AD6E643E5172B61725EE75BDA4FFFF1D259DA626D7599CBEA6C32BAFD616D08F72CE7031B95768328CD0FC32894FE44926F8EC4F003E9D7E749C66DFE9CE0C59E1501E84B09DE0AEC086C0EEG4685380F7DD7633E9B743186C1D1CF04A51FDBD48823CDEB5A82D7B122AAC2EDA0248A8117689494C9E87709A1220DF93489E5EB0F22FB1C7A3E8B6678C28C080A20CAF2G388BFED19515909B73E453C69C23083437
	C9720BB001BFC8B85FE71BB6635A88A175FC50AD2822B102753F5004EB52A5B801A3B000FBE6175FB7233DAAC17FA6G37EE07F367115F99D101FCD1D1D1A74B1D61E093EAC22687504E35B4F7C460FEEC005B7837AD08DB097DB267B36A7CA6939F0764C2718525074758AFA218170BE56EB3FFEC61BE63D8BF1514ACB8ED69B334DF56FCC60E0D1D1914A74A455A0EC80BC6D3ACDB474EDF82EC0E4B7A9CD95FB27F377535BA6944359A8EBAA4834C52EDBC7BFC91735993C2C864DEFAD68187935D0645191CBD
	BDBCD6311D2D4BBF02629782CF92CC4F59936436DB57E3560B0F7FF6EDD79CB587176B5B1A035968B1A76ECBE21798D09DE2D08C3861A62475BD61C45F536256A36BB4CFD2FC90623C9A82AB2463FF6344D894D4D5115A432A4877D63DFF811DAB94EE8F9AA9024F3548093A495D90677A78A5CE0C91179C8808F431F4922D7FA59A2322CF74035055F2B820B27C9F1A514FBB459EADC73C5A0C05CF17608BE33EE1F5C21A308EF5D0A4BF38G26ACB2BD08715B56D4E832D18360305EC9006B24A00F456EDDBC96
	0BFBA713D9D7F65B2C761209D39D36D5C196D779DF993148E4FA3145DA7625B4D7E8789013754DABE887DA314454AB4C7F2FD1BB14429C8E00562F387FFFF99E7BF90E519C3FDA767918B9F5BB1FF166633A08DF05C5C84D184C4E6836BBC757405C67546C487AAFBA510EBED39EACD5500EB3FAF8AD2802BC09G29DFB1DBAE53EC4146757572F17D50CE82182E30F14B95733823FDFAED9902DA8BA098E263E60458B8E62BB5D730BE302A7F3CE1499ED6A54C36028A8E9D8C2B540A2C667BB54D5B98E3F9AA2F
	6DBC47502E8EE8378228AADF7F6756735AAE29B319AC3E6F4FF16B57C4D52BE3D4E37D0BEE65355D9C3A564B210F04791AADF01A77172AD0C8F6C9F4EB536CBD3C596C9F5ADE527C96DCBD1292D2EBA100A3FCED46B4233C3877E9E82D96D8C2F39A5A2BG662D831895C67C83E585A6590707599E168A57AC4A6FD4AF56GAEC191D5BB7D4B3654BDD705AEA1E28FF503E35AE9A631D7E36FC93C5D2B2DCB59568A3886E06FEE0204D2A309BE7715EA930F3A2118FC3E57496B38A91B0FEB724D7D69BD24F2E033
	4D2A2ECE58702EAD37CB731FAE43767D3F48494A7872CFEB189D6E6F65F66807B1533B389D328E196D1051456CD0504D6D302E8F6DA029225F0EFF9AD5D0203AC388F8C5B73505DD68D6B59DEA21BF4A8E53EA51F75CE21764922114EA17CF4811AB3531DC6A623181CBB0593DFDDC76BDA0D7DBB7175D9DB14BBE311B49BED7177D193E8A1E8B6D74AF1F922325536735DFF5E1CE2711428658CFD7531057545074B8A055701C8427282084B9DA3962B4B32C3BB10EF4ED59FE772CDE5BAF6E564E1E5DC6DD129B
	99DC23301AB779B85AE2C704553C9BF4FC9A45BBCCF8E6BB9F0DEBEC7B20EFD90457E94D97B1773BFB60EC85B091A08DE09E40729E9E1FDBDB8E126B54413510EF140060EB0C5A68789E59026BGFB1356FE2EA51B2F8761F6F825A7760C13579B2D7B1E18B6237B4FC1F4BBE35AF5B1F4ECADF76F5941F326A98AABAACE6B214AF2640BE5BCC73AFBF9AE7C3A461C8BE7EEB8E0DB97A797CE52661E59EB44E309FE4E4DECC599D6C1DFE1DFFACEA9C09DC090A091A02D0F5B7F5205A35733BFF52BA6399AF027E0
	DF2A0E2D9FF4FB7D28771B59F946A03AD8BB4F6B33323357375D5A4EBC91E84F9FAA8E65EAB44B7AF83D696F7BE632768D220B1535285FCA56EB07BEA426FD5494479EF488949A231EFA87FFEE7203884A0D6BDF8EBC59F968E53A2F86211D28631C1508CB46360EBBF894F18B213D3C1F6BBC6DB87AC4415769B9A5G8E00D60088C092C05A57FC5CA3F45CDCE8E7G64DDE37581569CD8CF14DC636314E3C18B3F3C9960B2605CD67DDCD6AE23318E637E6239118F465C06B87D7C9C0276DFCF4CB5FE5B4578B5
	3E7722F50D6F3FE8DD6307AEEEE4F860E1BE69737E3E0BC31F81EE3DE8F586F8174A7F54168D14F7A8A406E890E32126753D223EC3AC4E8275D0D0E83AEFA6BBFB57EAE3CE2AF275815D76AF1519E3206EDA745A35453455EBFCBFC8380CFBE982346F871889B09FE045B51E0B36DD3A6EDEF0B2B244DE302EB57E3AECEA35DE1742D6DC5B109CD6DCE22C6D5FEBAD547B0C2C0735831A090D59C369D5C1710A0356EE9F25E7FD71684BDB632DAFDB03A5D1EBD0262F4131C366B5A8980849DD83036309713B3F4F
	4CEF3F4EAFB94279D582ED03095E8D77910044981E0C5F9BCD28FFD091BD22822721C629977DE761G6620AD67E9DE027B5481DEAF79AB716C6893FA64308A3EB08C477DCBA5BFA36E00F699C0ABC0F0982B35D87E7CF9B74621C7917CE01E462068A26EDB107674D1CEBB8F46AF85D88D78E2G47ADFC4EA9DDD81FE835D51D50AE7A987E78C634852E85C56ABAE51C47399C3426E4BD8CFFAE7FED92DB23F19A6731759AAFFD13577995563F2D735337A62D6F45F3F8D6BC9F925DE679D86D796AF1F36D19FA8B
	33473CDBF8EDFA344C2AFF295EBFED1DD57FCA3DFF6EE7D67DF95F63FB486B34BEA902F6290EBBD5487795070ECB7994F1C950CE799E0F6531176347625D172DE3715E4B56B9723E4BC367489FDF0E97C35A7EF8086BB1D31779D29B57E31E0E7BBB3A5F965D1A1ED3F9AB47A51C65348DBA6E0D2D08F3C33BDF47BDDE4E790D55F1F5DD08DB8C6D15GF94371FD51A4F48BED6D4FD49641B5A0F697694793078467D0FCF03A67602725A5C587CACB77323AB0395B7C0E277C2D4FAC1F11D07F9C4E62542DDDE75E
	36CB2B535FBA425F71CC023E7943F9EC6DEF0B2AD7D1100A0AAF9449FDC2705651C8E5754839A3399AFD5E88FE269FC6F9BA205D8A200E60F5EC64C3DE4726CE3B07548DBBE3F926DF5CF44016B8C2BB530FB06A589428E3591C4123FC4E8C68AF9E09389F9EB57BAD5AF29C8C304315754F0B6AC75F40390DFE5BB1F3BF4A7B77B6F3FFD9D4BF2F816C041F191F5E4DF5DB75F48A71BDFCB86E738A5FC82653D86D5A605CC50AC77FE98D337BFFF88A09E57DCAF3G533F380A1F9166G8F068B6B6786F29B477D
	FE9D47156B38AC6A13B4BF5F46F3615F1E45E7F99EB920329C3D0B466EFC68DF8CF0AA81F1D78A9D1A7C8589567B88FFAFC35F59409804DD1A2DAA935879100F59CCFE75A94A6512FDE1FFA0246D8D7FC467453AE0AC0CE738819A274950CECD6032A8EDDC16C5F1E4499AC2166503E4F9A692FF3FFA05467FAA20499D45E5FA1846FF99340F0F62B6ACA84467C36D022B532B4861001B5317505C9804F17D23387C8B4A397C13C6D94B1F9DC7FE14B9EDD42C7C2EAE74C55884A5FF58CF25E7F37751DCB393464F
	9945E37059CB661C11FF4F7BB64EABD60E951AECE43491032CE673F8287E00EF984DF37B92777F4055A9DC55AA3413EEC91595DA228132C11103222276B0ACFDB021559879C73E597B511D5F102E79266958F30E95B42785C79B36E778B7A83E5F04E7F9F5241ED79320AFE3B42FF76F76E2FCB75C1E1E6383988EB089E0A640425B39ADBF96CB49F5EA5EA6B9D8879B294F3475B17D42C73F59FB63A756707CB57D31A9E4464857B263BDE348381D79C3DEA23BAEEB5AC571CC3F0FA51A3F202F85209FA089A085
	E0D6A257CF91CB18FE688ED52AAA38BA6813D62E1CD3923B8DB496F6CC373DD5E65D667379356B2A4441314468FA234EA6795FE7630A35EB797763511DDA9FF526514635E9D7B5AEDDEEF98B99EC3784ED7CA48BBA3A5781FEF6CC5FC2E8E74460D84D70E0GD70D457F67053BC9CC4DD08349FC4F87A1A1G67BCC572FCD0D28415437E323D04BFC798DB481FFF10BB52F35E23FC8849A5460F4D3392CBB14DC71664F19F5971639F90478B2FE5721C942BCB799DCC6604BB0C5C316C6A2AB5CC5ED7BF052D156C
	BF406718219A3AC8059FD86B6216841F6CB5A94274382F0D6BB16B8EDECF7DAEEC487F5FDD866F2F4F6E0C65ED54D6073034BA301753CD6DB1687EFD6B0D4A44E8377499340F14E35D2053D54BBED931F7F7C82A48644FB94C65DF294BFF385F103F75EB035768329B111F45615AEBC84762ED06087EE1191BA15ACE832F336DD761A05AA4F8F51ACE8F2E1BDEBF33142D75A99E5489369B957417FA692B13C6989870B27FFF9D9F9FEA3110F55438CFBCE295975F9630B9267ED567E07AA7FB631FE39E70DA1FE3
	FE6235BE47BC649D7AD94EAF3D715F6726FB4D6FF3593AEF994035A99D131ED3879084988EB0F18C4F3D8D9D684B78A549603CFB526D45E79A7875084A67BFA8455735C23A7EB7A755922D0BE4D5157D481F63EBA5DCAFD58ED2A44355CBE8A3C572F628EC2806EF12B0C6FC22072259B96AB82D012703DE3347703355198B743C856DE5BAAE6D82BF032D50F1634AB82DE3AC471557F35A969D77B7751C562D632E2C673453F55C0A131CF60E0EBBDF43E9676B380FE96DDDBCAEBD27EC9C470D21386350EE51F1
	A3E94D6903F6020E3BCC6B60C950CEB5F019FC0ED9BAEE8AFD5F19FFA7540CF772E732638B4D7BD8A5609B6E9C7ADB9466CBAA41BA558DE307815C8FB093E09E40B200D5G79F741798B209220892083209FE0ACC0B240F400B9F7F15FBB925C9BAF8F81D2BBDCD287D48489DDD00B6914B6337C0B005F723B0616DF7BFE643019EEF5D2FACEE15250F4CC4E1D8371FDFE774075FD5EBEC05F9F032FB6758435E7FA87877099176C7104C4BD364A8772F5DCED28C18EC918C9D92EEF2E0ABE1379293F17007C65C9
	7C4C51FE814728A08C5BAB9AF0DBA56C0C716485FE4668305099776CF8E78C7A2EB64928E7597C0F5118C9GFCD2925F27C2D946FCE975FC3E59F16633BA9F3E234D37F050FC89B44E1682FE39BE5F045546FC3E9ABEDF51F86B7906BA83160D0FBDC3D515C75B72D1DA9A313EE96574FCA5C4E24ED76C794EF160D5B71E2F493DC31CA59F21F19D01314375716A2168F59ECD63BC8D7AE70E6767C5B5136B3A5CC2575873626A8969B9883CB6AB1C902B6B23AD56674567E86EA806712593F8BEC925F5E26B84F6
	FEE4381FD31C8A380F7418C9A071E3E68C39FE4CCCA4567B448F4960FD62BEE23DCFBCC84C7B04765DAE350D97F2CB0BBE6EA19DF7C1474D20BC9157AC39558E9D7FB8158B7127C41C1063E7D3393AA92EC543BDE542DD50F04F900CFBD039C3D6B86D73042E396473E95FC752B1D2G5200EE0FC5D4C6A9A81864DE2263AE68EB712BB63EBE59199CB73B0D2F4FE77A388F8E73F119BA6E2343FC9CFDFEC77D7E4EDC436F0FDD3839DAFC6585DE4B0E1D40E376E1FAF4E077BB738D5EA575B747FBC9BD67BDC767
	DD3551601D3259603D6C644D713E37C6BF8B5D4DF9BFDDE0707E63BB866F3E48FB313C0D9AFCFF99FDBE3BD7235B385520BBF7684669FA3699F48D199F8D250B3DDD7639C1217DCCA1264F54CC2ECF795DC31D6D9E7E33F17F52F6E34E6DB724A34BB94207F9C4BF8B5050E1789DB48FEAF89EBA5A3E7C4685733E9C8499075FFDA37B794B75E63A09C013FAC3F4D3EA4CF43300E67E8D517DA853CC378CE8D6DD070E45D541C59EEB020E5BF611475A7BBA6E0DFABE6E7FF41C6D6C60F10F5670F1D7F45C63B503
	47BD11B9B8467FB1539C636CFFDA7EF06EF9C0263ECA32C38A59B4F80D2B6E786D8B29D79C85B6E22BA5B99DF471EAC9F6B72EEE84DF314F015D64CB20DD9F65137FB0795317673E68042AA02716EC43AD2DD1D46889D1F463DBE66D736F9A32167EFF88EEBC35E4F508DE32C3EA0FCF2CA3851EF37899E4C8955DD57CCC94D249BB74E32DFD78F9533ED3C228C3453D6373BA329ABFF6D349A6566D93825EFD6CA07AB915B3332A0D3ABBAE8D81F9B7D3F969C7D2BA174D3F5C307B7D53CBE7E491DB8DF94BA78B
	6E9A4185E5E9ADD92BF6A8E2288382AC92BCF61CB24A6D66DFDFE4813B6D3A7A282C9DDF7269C2F7C66ECDDC3FECFD8D14E491056C23A3313A306BA35C559D0282D309CA2D0A9F5E4BCA315EF74BCE75435FFC313555C6B2B5A53FA257486AC8D0A5B99E41AF79FAD432D6FF3A93424EDC4040869B88F9E4458F42954588473B74B96CF7BDD5BA7E4157AF52B98CD2153C472C89247B3C226C5FD76571C881D1E7G2685766F6A6CF10CBDE64C264838F7CEAFF8339DDC41C1327C2525746FFE7A77G7E5DCFB17B
	A9E67F81306FF2B264AF36BF42F5267E8372EC2072208758EBE09155FFFE7529A93F2BDC1E815A5510EC7A6D972CC33EB12ABE6CEF97151129635FDD173478F6DC26A0FACB847966C5F40E28E1AD5D1997F79D2C48FA693EAC9BD98DD141AA9570DFACB740FF9596A4D9D029D05743AC0815832672ECAA2CED10C3A2B6E730E7A17AADD8E8166131351E8E5DC07FEE3AE9D3A1D581255D22EB78AEB360B6CBC9710795DB65F7CF6E5D38B2EFC7BFCE4B1C3A75567CB18743B612DB0361618BF688355A1A5711F56D
	F4E9C485462AA45F70EDCA8BB944660284335D403B904FFA9B03028B534CDA3720F4D28EA843471748DA0F62ED27932844F6296692CCCBDB58DDD287D6684087A9986FAA59EA30EE40E32D5641B294F993FEC17A9B34A0E47002C00C53CF820AEA9D95AA4946CF8B603A06FDC6GF71BEB9DC5D5BEC5945CBDC5D52110648D086E6F5EA2FE966DFC7C779D8C13230624D13F6F1E24A7071294FFD84FA6DA34E235480C2345EA2E0503EA4173EF8EBB6EB0E910662F23DEFD344BB4E8E3332395525ED36C9DB03F63
	DCB922B06EF79113A6EB5F99CC0EFD4769247F831081785CA97C7955B467A7A47EF374C63F607329022F33AA6046E76AA7FAD451F80EFB2999790778982D137535537FA138EA2279FFD0CB878818341FD97F95GG58BCGGD0CB818294G94G88G88GF6F954AC18341FD97F95GG58BCGG8CGGGGGGGGGGGGGGGGGE2F5E9ECE4E5F2A0E4E1F4E1D0CB8586GGGG81G81GBAGGGB996GGGG
**end of data**/
}
}
