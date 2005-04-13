package com.cannontech.dbeditor.wizard.device.capcontrol;

/**
 * This type was created in VisualAge.
 */
import java.awt.Dimension;

import com.cannontech.database.data.capcontrol.CapBank;
import com.cannontech.database.data.device.DeviceBase;
import com.cannontech.database.data.point.PointFactory;
 
public class CapBankSettingsPanel extends com.cannontech.common.gui.util.DataInputPanel implements java.awt.event.ActionListener {
	private String capBankSelectedType = null;
	private javax.swing.JLabel ivjBankSizeLabel = null;
	private java.util.List points = null;
	private javax.swing.JLabel ivjJLabelKVAR = null;
	private javax.swing.JComboBox ivjJComboBoxControllerType = null;
	private javax.swing.JLabel ivjJLabelControllerType = null;
	private javax.swing.JLabel ivjJLabelSwitchManufacture = null;
	private javax.swing.JLabel ivjJLabelTypeOfSwitch = null;
	private javax.swing.JComboBox ivjJComboBoxBankSize = null;
	private javax.swing.JComboBox ivjJComboBoxSwitchManufacture = null;
	private javax.swing.JComboBox ivjJComboBoxTypeSwitch = null;


   private class NewComboBoxEditor extends javax.swing.plaf.basic.BasicComboBoxEditor
   {
      public javax.swing.JTextField getJTextField()
      {
         //create this method so we don't have to cast the getEditorComponent() call
         return editor;
      }
      
   };         

/**
 * Constructor
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
public CapBankSettingsPanel() {
	super();
	initialize();
}
/**
 * Method to handle events for the ActionListener interface.
 * @param e java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
public void actionPerformed(java.awt.event.ActionEvent e) {
	// user code begin {1}
	// user code end
	if (e.getSource() == getJComboBoxControllerType()) 
		connEtoC6(e);
	if (e.getSource() == getJComboBoxBankSize()) 
		connEtoC1(e);
	if (e.getSource() == getJComboBoxSwitchManufacture()) 
		connEtoC2(e);
	if (e.getSource() == getJComboBoxTypeSwitch()) 
		connEtoC3(e);
	// user code begin {2}
	// user code end
}
/**
 * connEtoC1:  (BankSizeTextField.action.actionPerformed(java.awt.event.ActionEvent) --> CapBankSettingsPanel.fireInputUpdate()V)
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
 * connEtoC2:  (JTextFieldSwitchManufacture.action.actionPerformed(java.awt.event.ActionEvent) --> CapBankSettingsPanel.fireInputUpdate()V)
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
 * connEtoC3:  (JTextFieldTypeSwitch.action.actionPerformed(java.awt.event.ActionEvent) --> CapBankSettingsPanel.fireInputUpdate()V)
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
 * connEtoC6:  (JComboBoxControllerType.action.actionPerformed(java.awt.event.ActionEvent) --> CapBankSettingsPanel.fireInputUpdate()V)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC6(java.awt.event.ActionEvent arg1) {
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
 * Return the PhysicalAddressLabel property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getBankSizeLabel() {
	if (ivjBankSizeLabel == null) {
		try {
			ivjBankSizeLabel = new javax.swing.JLabel();
			ivjBankSizeLabel.setName("BankSizeLabel");
			ivjBankSizeLabel.setFont(new java.awt.Font("dialog", 0, 14));
			ivjBankSizeLabel.setText("Bank Size:");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjBankSizeLabel;
}
/**
 * Insert the method's description here.
 * Creation date: (12/14/2001 5:36:25 PM)
 * @return java.lang.String
 */
public java.lang.String getCapBankSelectedType() {
	return capBankSelectedType;
}
/**
 * Return the BankSizeTextField property value.
 * @return javax.swing.JComboBox
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JComboBox getJComboBoxBankSize() {
	if (ivjJComboBoxBankSize == null) {
		try {
			ivjJComboBoxBankSize = new javax.swing.JComboBox();
			ivjJComboBoxBankSize.setName("JComboBoxBankSize");
			ivjJComboBoxBankSize.setFont(new java.awt.Font("sansserif", 0, 12));
			// user code begin {1}

			ivjJComboBoxBankSize.addItem( new Integer(50) );
			ivjJComboBoxBankSize.addItem( new Integer(100) );
			ivjJComboBoxBankSize.addItem( new Integer(150) );
         ivjJComboBoxBankSize.addItem( new Integer(275) );
			ivjJComboBoxBankSize.addItem( new Integer(300) );
			ivjJComboBoxBankSize.addItem( new Integer(450) );
         ivjJComboBoxBankSize.addItem( new Integer(550) );         
			ivjJComboBoxBankSize.addItem( new Integer(600) );
         ivjJComboBoxBankSize.addItem( new Integer(825) );         
			ivjJComboBoxBankSize.addItem( new Integer(900) );
         ivjJComboBoxBankSize.addItem( new Integer(1100) );         
			ivjJComboBoxBankSize.addItem( new Integer(1200) );


         ivjJComboBoxBankSize.setEditable( true );

         NewComboBoxEditor ncb = new NewComboBoxEditor();
         ncb.getJTextField().setDocument( 
               new com.cannontech.common.gui.unchanging.LongRangeDocument(0, 10000) );
   		
         ivjJComboBoxBankSize.setEditor( ncb );

			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJComboBoxBankSize;
}
/**
 * Return the JComboBoxControllerType property value.
 * @return javax.swing.JComboBox
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JComboBox getJComboBoxControllerType() {
	if (ivjJComboBoxControllerType == null) {
		try {
			ivjJComboBoxControllerType = new javax.swing.JComboBox();
			ivjJComboBoxControllerType.setName("JComboBoxControllerType");
			ivjJComboBoxControllerType.setFont(new java.awt.Font("sansserif", 0, 12));
			// user code begin {1}

			ivjJComboBoxControllerType.addItem( com.cannontech.common.util.CtiUtilities.STRING_NONE );
         ivjJComboBoxControllerType.addItem( CapBank.CONTROL_TYPE_DLC );
         ivjJComboBoxControllerType.addItem( CapBank.CONTROL_TYPE_PAGING );
         ivjJComboBoxControllerType.addItem( CapBank.CONTROL_TYPE_FM );
         ivjJComboBoxControllerType.addItem( CapBank.CONTROL_TYPE_FP_PAGING );
			
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJComboBoxControllerType;
}
/**
 * Return the JTextFieldSwitchManufacture property value.
 * @return javax.swing.JComboBox
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JComboBox getJComboBoxSwitchManufacture() {
	if (ivjJComboBoxSwitchManufacture == null) {
		try {
			ivjJComboBoxSwitchManufacture = new javax.swing.JComboBox();
			ivjJComboBoxSwitchManufacture.setName("JComboBoxSwitchManufacture");
			ivjJComboBoxSwitchManufacture.setFont(new java.awt.Font("sansserif", 0, 12));
			ivjJComboBoxSwitchManufacture.setMinimumSize(new java.awt.Dimension(130, 25));
			// user code begin {1}

			ivjJComboBoxSwitchManufacture.addItem( com.cannontech.common.util.CtiUtilities.STRING_NONE );
			ivjJComboBoxSwitchManufacture.addItem( CapBank.SWITCHMAN_WESTING );
			ivjJComboBoxSwitchManufacture.addItem( CapBank.SWITCHMAN_ABB );
			ivjJComboBoxSwitchManufacture.addItem( CapBank.SWITCHMAN_COOPER );
			ivjJComboBoxSwitchManufacture.addItem( CapBank.SWITCHMAN_SIEMENS );
         ivjJComboBoxSwitchManufacture.addItem( CapBank.SWITCHMAN_TRINETICS );
			
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJComboBoxSwitchManufacture;
}
/**
 * Return the JTextFieldTypeSwitch property value.
 * @return javax.swing.JComboBox
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JComboBox getJComboBoxTypeSwitch() {
	if (ivjJComboBoxTypeSwitch == null) {
		try {
			ivjJComboBoxTypeSwitch = new javax.swing.JComboBox();
			ivjJComboBoxTypeSwitch.setName("JComboBoxTypeSwitch");
			ivjJComboBoxTypeSwitch.setFont(new java.awt.Font("sansserif", 0, 12));
			// user code begin {1}

			ivjJComboBoxTypeSwitch.addItem( com.cannontech.common.util.CtiUtilities.STRING_NONE );
			ivjJComboBoxTypeSwitch.addItem( CapBank.SWITCHTYPE_OIL );
         ivjJComboBoxTypeSwitch.addItem( CapBank.SWITCHTYPE_VACUUM );

			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJComboBoxTypeSwitch;
}
/**
 * Return the JLabelControllerType property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getJLabelControllerType() {
	if (ivjJLabelControllerType == null) {
		try {
			ivjJLabelControllerType = new javax.swing.JLabel();
			ivjJLabelControllerType.setName("JLabelControllerType");
			ivjJLabelControllerType.setFont(new java.awt.Font("dialog", 0, 14));
			ivjJLabelControllerType.setText("Controller Type:");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJLabelControllerType;
}
/**
 * Return the JLabelKVAR property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getJLabelKVAR() {
	if (ivjJLabelKVAR == null) {
		try {
			ivjJLabelKVAR = new javax.swing.JLabel();
			ivjJLabelKVAR.setName("JLabelKVAR");
			ivjJLabelKVAR.setFont(new java.awt.Font("dialog", 0, 14));
			ivjJLabelKVAR.setText("kVAr");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJLabelKVAR;
}
/**
 * Return the JLabelSwitchManufacture property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getJLabelSwitchManufacture() {
	if (ivjJLabelSwitchManufacture == null) {
		try {
			ivjJLabelSwitchManufacture = new javax.swing.JLabel();
			ivjJLabelSwitchManufacture.setName("JLabelSwitchManufacture");
			ivjJLabelSwitchManufacture.setFont(new java.awt.Font("dialog", 0, 14));
			ivjJLabelSwitchManufacture.setText("Switch Manufacture:");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJLabelSwitchManufacture;
}
/**
 * Return the JLabelTypeOfSwitch property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getJLabelTypeOfSwitch() {
	if (ivjJLabelTypeOfSwitch == null) {
		try {
			ivjJLabelTypeOfSwitch = new javax.swing.JLabel();
			ivjJLabelTypeOfSwitch.setName("JLabelTypeOfSwitch");
			ivjJLabelTypeOfSwitch.setFont(new java.awt.Font("dialog", 0, 14));
			ivjJLabelTypeOfSwitch.setText("Type of Switch:");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJLabelTypeOfSwitch;
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
 * This method was created in VisualAge.
 * @return java.lang.Object
 * @param val java.lang.Object
 */
public Object getValue(Object val)
{
	CapBank capBank = (CapBank) val;

   try
   {
   	capBank.getCapBank().setBankSize( 
         (getJComboBoxBankSize().getSelectedItem().toString().length() <= 0
         ? new Integer(150)
         : new Integer(getJComboBoxBankSize().getSelectedItem().toString()) ) );
   }
   catch( NumberFormatException e )
   {
      capBank.getCapBank().setBankSize( new Integer(150) );
   }
   
	capBank.getCapBank().setSwitchManufacture( getJComboBoxSwitchManufacture().getSelectedItem().toString() );

	capBank.getCapBank().setTypeOfSwitch( getJComboBoxTypeSwitch().getSelectedItem().toString() );

	capBank.getCapBank().setControllerType( getJComboBoxControllerType().getSelectedItem().toString() );

	
	//add any objects that get created automaitcally
	com.cannontech.database.data.multi.SmartMultiDBPersistent newVal = new com.cannontech.database.data.multi.SmartMultiDBPersistent();

	//only create Status point if the capbank is Fixed
	/*if( capBank.getCapBank().getOperationalState().equalsIgnoreCase(com.cannontech.database.data.capcontrol.CapBank.FIXED_OPSTATE) )
	{
		PointFactory.createBankStatusPt( newVal );
	}
	else
	{*/
	PointFactory.createBankStatusPt( newVal );
	PointFactory.createBankOpCntPoint( newVal );		
	//}

	((DeviceBase) val).setDeviceID( com.cannontech.database.db.pao.YukonPAObject.getNextYukonPAObjectID() );
	newVal.addDBPersistent( capBank );

	//the capBank is the owner in this case
	newVal.setOwnerDBPersistent( capBank );
	
	return newVal;
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
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void initConnections() throws java.lang.Exception {
	// user code begin {1}
	// user code end
	getJComboBoxControllerType().addActionListener(this);
	getJComboBoxBankSize().addActionListener(this);
	getJComboBoxSwitchManufacture().addActionListener(this);
	getJComboBoxTypeSwitch().addActionListener(this);
}
/**
 * Initialize the class.
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void initialize() {
	try {
		// user code begin {1}
		// user code end
		setName("CapBankSettingsPanel");
		setLayout(new java.awt.GridBagLayout());
		setSize(350, 216);

		java.awt.GridBagConstraints constraintsBankSizeLabel = new java.awt.GridBagConstraints();
		constraintsBankSizeLabel.gridx = 1; constraintsBankSizeLabel.gridy = 1;
		constraintsBankSizeLabel.anchor = java.awt.GridBagConstraints.WEST;
		constraintsBankSizeLabel.ipadx = 31;
		constraintsBankSizeLabel.insets = new java.awt.Insets(44, 7, 8, 33);
		add(getBankSizeLabel(), constraintsBankSizeLabel);

		java.awt.GridBagConstraints constraintsJComboBoxBankSize = new java.awt.GridBagConstraints();
		constraintsJComboBoxBankSize.gridx = 2; constraintsJComboBoxBankSize.gridy = 1;
		constraintsJComboBoxBankSize.fill = java.awt.GridBagConstraints.HORIZONTAL;
		constraintsJComboBoxBankSize.anchor = java.awt.GridBagConstraints.WEST;
		constraintsJComboBoxBankSize.weightx = 1.0;
		constraintsJComboBoxBankSize.ipadx = -4;
		constraintsJComboBoxBankSize.insets = new java.awt.Insets(42, 1, 6, 1);
		add(getJComboBoxBankSize(), constraintsJComboBoxBankSize);

		java.awt.GridBagConstraints constraintsJLabelKVAR = new java.awt.GridBagConstraints();
		constraintsJLabelKVAR.gridx = 3; constraintsJLabelKVAR.gridy = 1;
		constraintsJLabelKVAR.anchor = java.awt.GridBagConstraints.WEST;
		constraintsJLabelKVAR.ipadx = 21;
		constraintsJLabelKVAR.ipady = -3;
		constraintsJLabelKVAR.insets = new java.awt.Insets(46, 1, 9, 30);
		add(getJLabelKVAR(), constraintsJLabelKVAR);

		java.awt.GridBagConstraints constraintsJLabelSwitchManufacture = new java.awt.GridBagConstraints();
		constraintsJLabelSwitchManufacture.gridx = 1; constraintsJLabelSwitchManufacture.gridy = 2;
		constraintsJLabelSwitchManufacture.anchor = java.awt.GridBagConstraints.WEST;
		constraintsJLabelSwitchManufacture.ipadx = 3;
		constraintsJLabelSwitchManufacture.insets = new java.awt.Insets(9, 7, 7, 0);
		add(getJLabelSwitchManufacture(), constraintsJLabelSwitchManufacture);

		java.awt.GridBagConstraints constraintsJComboBoxSwitchManufacture = new java.awt.GridBagConstraints();
		constraintsJComboBoxSwitchManufacture.gridx = 2; constraintsJComboBoxSwitchManufacture.gridy = 2;
		constraintsJComboBoxSwitchManufacture.gridwidth = 2;
		constraintsJComboBoxSwitchManufacture.fill = java.awt.GridBagConstraints.HORIZONTAL;
		constraintsJComboBoxSwitchManufacture.anchor = java.awt.GridBagConstraints.WEST;
		constraintsJComboBoxSwitchManufacture.weightx = 1.0;
		constraintsJComboBoxSwitchManufacture.ipadx = 60;
		constraintsJComboBoxSwitchManufacture.insets = new java.awt.Insets(7, 1, 5, 26);
		add(getJComboBoxSwitchManufacture(), constraintsJComboBoxSwitchManufacture);

		java.awt.GridBagConstraints constraintsJLabelTypeOfSwitch = new java.awt.GridBagConstraints();
		constraintsJLabelTypeOfSwitch.gridx = 1; constraintsJLabelTypeOfSwitch.gridy = 3;
		constraintsJLabelTypeOfSwitch.anchor = java.awt.GridBagConstraints.WEST;
		constraintsJLabelTypeOfSwitch.ipadx = 34;
		constraintsJLabelTypeOfSwitch.insets = new java.awt.Insets(7, 7, 7, 0);
		add(getJLabelTypeOfSwitch(), constraintsJLabelTypeOfSwitch);

		java.awt.GridBagConstraints constraintsJComboBoxTypeSwitch = new java.awt.GridBagConstraints();
		constraintsJComboBoxTypeSwitch.gridx = 2; constraintsJComboBoxTypeSwitch.gridy = 3;
		constraintsJComboBoxTypeSwitch.gridwidth = 2;
		constraintsJComboBoxTypeSwitch.fill = java.awt.GridBagConstraints.HORIZONTAL;
		constraintsJComboBoxTypeSwitch.anchor = java.awt.GridBagConstraints.WEST;
		constraintsJComboBoxTypeSwitch.weightx = 1.0;
		constraintsJComboBoxTypeSwitch.ipadx = 60;
		constraintsJComboBoxTypeSwitch.insets = new java.awt.Insets(5, 1, 5, 26);
		add(getJComboBoxTypeSwitch(), constraintsJComboBoxTypeSwitch);

		java.awt.GridBagConstraints constraintsJLabelControllerType = new java.awt.GridBagConstraints();
		constraintsJLabelControllerType.gridx = 1; constraintsJLabelControllerType.gridy = 4;
		constraintsJLabelControllerType.anchor = java.awt.GridBagConstraints.WEST;
		constraintsJLabelControllerType.ipadx = 18;
		constraintsJLabelControllerType.insets = new java.awt.Insets(8, 7, 50, 10);
		add(getJLabelControllerType(), constraintsJLabelControllerType);

		java.awt.GridBagConstraints constraintsJComboBoxControllerType = new java.awt.GridBagConstraints();
		constraintsJComboBoxControllerType.gridx = 2; constraintsJComboBoxControllerType.gridy = 4;
		constraintsJComboBoxControllerType.gridwidth = 2;
		constraintsJComboBoxControllerType.fill = java.awt.GridBagConstraints.HORIZONTAL;
		constraintsJComboBoxControllerType.anchor = java.awt.GridBagConstraints.WEST;
		constraintsJComboBoxControllerType.weightx = 1.0;
		constraintsJComboBoxControllerType.ipadx = 60;
		constraintsJComboBoxControllerType.insets = new java.awt.Insets(6, 1, 48, 26);
		add(getJComboBoxControllerType(), constraintsJComboBoxControllerType);
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
	
	return true;
}
/**
 * main entrypoint - starts the part when it is run as an application
 * @param args java.lang.String[]
 */
public static void main(java.lang.String[] args) {
	try {
		javax.swing.JFrame frame = new javax.swing.JFrame();
		CapBankNameAddressPanel aCapBankNameAddressPanel;
		aCapBankNameAddressPanel = new CapBankNameAddressPanel();
		frame.setContentPane(aCapBankNameAddressPanel);
		frame.setSize(aCapBankNameAddressPanel.getSize());
		frame.addWindowListener(new java.awt.event.WindowAdapter() {
			public void windowClosing(java.awt.event.WindowEvent e) {
				System.exit(0);
			};
		});
		frame.setVisible(true);
	} catch (Throwable exception) {
		System.err.println("Exception occurred in main() of com.cannontech.common.gui.util.DataInputPanel");
		com.cannontech.clientutils.CTILogger.error( exception.getMessage(), exception );;
	}
}
/**
 * Insert the method's description here.
 * Creation date: (12/14/2001 5:36:25 PM)
 * @param newCapBankSelectedType java.lang.String
 */
public void setCapBankSelectedType(java.lang.String newCapBankSelectedType) {
	capBankSelectedType = newCapBankSelectedType;
}
/**
 * Insert the method's description here.
 * Creation date: (9/11/00 3:35:21 PM)
 * @param visible boolean
 */
private void setControllerVisible()
{
	boolean show = ( getCapBankSelectedType() != null 
						&& !getCapBankSelectedType().toString().equalsIgnoreCase(
							CapBank.FIXED_OPSTATE) ) ? true : false;
	
	getJLabelControllerType().setVisible( show );
	getJComboBoxControllerType().setVisible( show );
}
/**
 * This method was created in VisualAge.
 * @param val java.lang.Object
 */
public void setValue(Object val ) 
{
	setControllerVisible();
	
	// if val != null, then we will display the combo boxes with data
	if( val != null )
	{
		//this is just in case we dont have a CapBank object
		if( val instanceof CapBank )
		{
			CapBank capBank = (CapBank)val;
			getJComboBoxSwitchManufacture().setSelectedItem( capBank.getCapBank().getSwitchManufacture() );
			getJComboBoxTypeSwitch().setSelectedItem( capBank.getCapBank().getTypeOfSwitch() );
			getJComboBoxControllerType().setSelectedItem( capBank.getCapBank().getControllerType().toString() );
		}
		
	}
	else  // here we set the combo boxes invisible and their data to null
	{

	}

}
/**
 * 
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private static void getBuilderData() {
/*V1.1
**start of data**
	D0CB838494G88G88G8BF954ACGGGGGGGGGGGG8CGGGE2F5E9ECE4E5F2A0E4E1F4E14DBB8BD0DCD51628092222132CD81B550CC6C517594DEA5C4D2E191529D2A656C64D0EF1A616BA26AC36CC4D30E346C9A9BB9BC72A3CF4C38311A488B8A1A6D1CC089314A164B7B20CB3895090BE0DA140A09A4664C13F06066EFE4DFB2F2179A46CB9773E7B5E6B66B5A4D927CA2ACE5E3B675C7339671EFB6E3977F5C8497B4BF232ECA504E465906D6F13ACC2D67F1E106FA69475F2DC1C186AA0263F
	8FG5EA637AA5970DC84B45F9D290EC272D6C79E3413206D3AB55531935E0B4989E169A1F80921C7855A0E1EFBC76756734AC92EA71736BFA685705C8E108CB8FCE608283F0114EA78359A1EC3160D100D9E6C7395592361EA205D81308CE0250775DF8B4FFC2AF9E1ED0D562FEF71B2127144EFEC5BD00EC9A6074AC1465B21BD4B4883D9DB86D157F554CE64090776A3GB8F8BB79494775707C34E3E470CF83123FEAC088842400AA8E8CD50DFBA78559DD6596473C83A2900283C010A5DFEDED03902C9782A3
	DD222AFA8303CA079090FD603FA15473944923F2C1A6340997F127A6D077AAE82F83C857715BA7916F0477BEG554B477B475FFC2A0D27F76A811279EC403EA346F8933CEC1CAB3D46F893C33F4A5DC567262A9E67E0B350CA06D39D8781DCGBE0084005DB4A60E57FD844F3AFE35C1727B25C0DDB0D86FF34B6D12DB7441FBEDADD0B4DC27B8A049EEC2583C5FC4AAAD6C198B985F6D174D47FC12FC144740724E7B4806FFE93365DA78E4C3F235ADE91879A2E5587089533DBE266E8D4311B19A297BF3E2563D
	69DA491EE321FB65B395364D1A1C6C91C37731911C6B07E98C1443FB16969F8C7F984507CDF8962F6D940F6DF820ED9A6131513B1D5B624A3E0D949EF6D97AE1C9EA1D2D4347EC9872C55BF2B50D3BC49F53192F5B72AD0ADFE142B3DBCAA89E5BEB01D66DCFF530787A44096BB7915AABG56G248110C02AE38FC0F5004758453AC31798E39D022C56073CBE37A893467BC97D2F61A906D5D9E889A82A909890FB8459AB747BC405E8FDCE56238F7AA3789AE96C8F000ED3E2C0148555AB85C03A6097D5D1D6D8
	1F4F6A5F46BE32C8295DDE3F888A7CC142657654974353A7A86A1F03EEC1950D9E0C7ED5BD7AE4CC688109A0G5E19DF5C0D682F8E987F90C0D8774373D4DE1BA8C33E282DF5C952C8A8580DA3A1EB82686717E96EC8007724G777179A64495C8290EF2094BA9ECA8B249F1FA8750FCC11E60EB6C51894C51B5920F19D7FAF94CB8375DCBAA1FE9350C991AEFA496ABC1A9FA6DBCC9E522DD2C5DB551FB096B37C632DEDB73678A5FA56B30DF267C6D7ADC7D60944E559230F1A540FA5D47ED8DE5ACE66B85C564
	D1FADA06G935D0647191D71130570FCCB33A3C7331F51EE2534CDA037A4186A7894409D64BEFCF73257E4BF675B2E493A5C71D87B70282E034B4F7FFF78AC9A182F6A1C38BF09E3E2C0F50AC1B1600697C523B5BBD11F36FEBD124E73994587E13DB74082D3F4FC37935724202A3237BF240AFC0F5569FDD417926A8F9A29026B9AF0629825F1D86FFA7FA1A72E95583A83A21DB41DC5238F5335AA7AC4BF985DA00582AA43879BB15EC74489ADD7280DD88C0D893E90669DD6BB2C90F01E0232578F2100290BEC
	8E62BA36E5151AFC14GB82C01D6437339A0DF13637B791AAC1F3C0BBCF26E004DEACFD9F22F53D6B34A560FEB54D8134CA631495A76911AB3B43CE2273802FDD070912B4A052D035C961465839DF1GCBC679BA78E99352F92E5196C0036473B1F76AFEDEFF8A63DC91C7C3A224E8CCEAED3A6FDEB8D594C56B517C4868AF1DC2BF7ACC7930D9C6BFBEBE496B4395E04F9A0074D1664B03B273856BF753946F17886D65GEBE52BFEE726F93F82857229922BDF4E855E4F89FD7AAC7A31794FBD077504D56DB898
	72D605D4AFE6E7C105408F06D46AEBD6A37A3272B499538A2F8517E4237FD722AE000D8A0F13273771DAB0F1E499C93B675D1835EF26224507EA44C959B53C96BC530ABA4F83AD2872397D2A593C9F55A90AB46025DB21B6AF5BDA4CF144F0C5ADDB6169714A0A5A828BBD4C673034856D453DD2C3EB35432B34F64031AEG3DA9G55A1447FF63244E4FB064A763CBDC0E3717A4554914C83B4482A5823C314BD126C975D870705B121CA98D72BE84E292A2364C6FC2F60F3333A220859A9D26F338D7CE0C58F2F
	696EAEBE2969DEFB7FED645319E21BD50D9D3FE32FAD9CE27EDC96B27CBA7D4F8E7B262F5E48E5E37C37A9BE46CD5027FC0C0F314DE9B5463EB1BE463D53D7BAC626F7F21A6B8D024CE4DDEF41F1AB3DE9C0E7FCD597B8DF8660AA47B95FF7472D785C63FC3F9A4D0A2E1F4363465E1EB0BEFB1FE7F565319C5437EC1C551545BA3E09625719704C3EBB68DA40B68927BA1606F9AD145606F935E6A255518D6083D8G108430FA024774321623E41EDA338556289C90FCDD911B882F79DAF1EEA06FEB6DAFDB51CF
	B46F933E27EE190C76C35AC46458B3235A0C4FB1954DD79555AE1B0C64E331BC685B4D738CC6C538CA99076CDDDB5B8AE36C176A2530F676F4CF723CD2B349734705A6F37EC859F14476D60C7C9154F4AF1AB4623CD2BFCB5E500EB9F19D50B681E4CF25BA4AG1CG678196CCF17FEFEC7D63FC7E27C15CADB5G2E99F2FEC3745E2C7BABE5163FAE4D4FCF4C6B672429C8BE663F2B1D5CFFA9D3BC6663262F4C061BE771C55B10E1E9C34371BCCDC641B437615805AB3321FE96DF348D4EE9AB9BB60FF3BF7481
	1D1DCD62691E15886DE49DA75A9137965A9BF5DBEBF34A6119F78172A00093208FA08EA099E08DC0BAC05EC55E5FC57BD7DEE47B368B1E3E0BECEFCE3848765E6597595E4F7AA7E5AF52ECC599E65BD1B6FF4FB33DA7EB6F6597E21FC37A22E869F37425FE534694696FA7F55B0A237A6FB03D1B6B6EED44D86731F465C7355F1E23AF3745FCFEE06B723F5BE257681B5B2CEB74DF36D9576819EDEFB3BC48B01F58B9BD5BB5F78DDF60322A6177D25FBC7E6B9DD43622088ACD94D8C0E9341F2A6F900BDA7EB3B8
	54EBFEADA607267235BE0D2A5470BBFDEE3C91B9FBEDD46C24DD1CBD5FAC6F3DFD9A77448C20974EC07D87F092607C8C4FF7F73B665DEF9A43F36CB7096D31676576F66BF9392B9D63C611C27200986D7B6476D21DE6E45696D574231BD81F87A93FAA4803622C39DBC57999AD967F236D3166174D41EB91F3F0D81F036B1C66B9081B0974F9624C6CB9E0729E1AB64BBB224BEBBEEE16179C451F826DF5D1B8A6EF6402D95ED1DD5E7F0C1B65ED0C625D92533EBB3370EC6893A624108AA5CB9C2D8975B3E08134
	4B81DCG6763D8CD4576758747F11DFAE4418F6E6B8A0A83247BAA641D36F35EC7207FFA008C0092209D3B0A570E474AF08E69E14EBBA93A04FE3C02566C716E4679512B835EC927BFDA01B5FCABE5EA6B296B6478AC7A1D00613B463D6A40D03B9088F920B28D49225627366BDD336C595D34FEBF9BA952E5F5CF8445539E561553DF28ADB64B41AEDA87C6EF9FAC51799B58453BCF14319B2367FF39534C9F590530FAF6E10E391EDDFA95737DA3D771FAF7D33395FD3DCE9F6E3522276B7421B1ABFA26CEDFBB
	EEC5AF3C1A6F1FF134DEAB0776B19DB7584B7754662BF90E59F5BAF60EA8BBED1DA3BEBCED1D3BABCF4F1D3B0F1D0E353619FDF7509AE98958162C5B7C50D6EEF30A0EBBC76B1802EB60CCF78D47356F627D2AF5DCA56D97876DA5BA2EA00C386550CE51F19FFD4EF957DD434FB1AFE671EFBAE58BD29D3D8B783ABAFC9EEBD53DDE46D3D4ED6DA9596B2E9786DDF41532BA67C3DB1E463FE881BF07F75BD0669AE82781ECD9406B6870073C0EDEFE5FED449557E6F98ECF6BBEE2ABDC4872443105C69D1D8CF5B4
	53F9371D6B8C82BDF9A16256594DF102717DE0962B55985D16ED26637C6CA7E67A3188BA5A7BBD1B196E0920230F76EA7CECECCF9E60E34BFCB219787E7577B16F98B6EBE3AA39D67B4E56331F145F70C6AE737BEB1ED2E2F9CE212B180DBFA5075737AE1041F07BDBB8EE010E3B2B1763D2F45C1DE39C1721636CE10E2B3C0E67463F1D443BBB8F2404246B304FBF51F5660677A0401EF844DD6CF5DA0C4F6C23FC424FDFD08F82DFA640FE8DD788B2A6B49FB11D3F480954799FF41D3821DFB01E6B7C415679F4
	5ACD10949F29F39534DFFF734DB78D1D6BFBA3F5465335349A7A2D53F53AF74D27B34724BB7B7AC81D2557C70F3361B86ED930E1F97DA1BF5DD5186E33D4F7B9742F3E1E4F438F47A26DBBCE672A8F68C357F37BDC1F4FE7DF2E493E25D1762DB0594776700D336EA8668346776A9572FDEC3BDCBEF67771219D73CC4A756C6EA3CB479F2278B5A6BC4BF5DF5AF82EDB8F341A9B781D487FF6601ADB8A6DD5G6BG528172EE041A63C63EFFBCF13A1A4CD323F6CBC197ECB0BE53164046777776AB7BDE7BE8AE4F
	A9AB7F7DEE326A5A5733E25DBB545C48F2C92F76CC6F5EEF2A3FEF6844714583AD89E08DC09AC086C0C1829FDFD1C7A59BDF235B2B56292AB0B0C4EFAC7960FA3C62380146C2CB7BCE16E39EDBD902163735E7F3426CFA0F719DECB17375E97D025AF3C1CC3E7BFA4DFCCB34FE4B3567A3B17912464CFC6B35FE695AB3B3A6DFDE584CD7F813F68E561EB5B74D66F35D043F79386FAFB803EC8DFF59FBG1E81F13CCC3F10EC0285FDF01FA284600CA6FBBD7BAA4B2AAB4B8E9F295067FE593879779AB5DBBF317C
	360130C833A1C9FB26DC371F3CCF5879B90F703F4DA7E663382D0567082E773F8AF84C6D79475B09735957337647503BC153D7F93331C7278FE766B219AF8B46787FE16B650C5F4AF6A6F37B00E1738322A17F642E2FC7FE5120A17F7810A13FED6C7DE879CEAFAC837CE0FD7070D110FC64F80546771E5F605B7B7957E3D7DD006871D8E9B75E0F1B5E4F5BAE75BB6F15G53B171D755417290B9933BCE4FBDE3DD27EFBBE3DD27971F197B8EE57719585FC1771DB1FF87556EB1A42C815D906FF1GC9G2BG56
	5E4CF3680AB37B884D2196793351BD08F789786B8B156B3F25B376D8EF6B1C7FB79B77F4629934DFD2D5490F72B97EBB1DB8DF2A9424C8063B3F93FDA4FB8707D456D5432F6C44F873099E0AE6670467F27879A47DE6FEF668B90B38CCE897FC0B639EBE4B4F9325BA6E842DC307209D56F1574E505FE4C0FB290EFBF90673A66B386F7572734E969D770B1D943788EA1DC59C57DD4AF98F6A38074778F9A72C633E5FC375C2FB290EBB281F0112F55C7B34265A826D3C459CF76FBE446D01F6250E5B3A1F7356AC
	66E71A274E1A73F461ED9F58F84D919D5F710BD91ECCDCEC64CB96D7CA90652E817C4645BC16DE7E4B47706C910635CA8337E5C9265184488ED98A0A32BA413074CB3096238F0CE2AD187DB7290EB2GA7C09FC090A09EE0A9408A0055G9BGB2G72EE815F82D4831C846886983A055B7167F395F358219D88E9E04B0297C3DB0B2BDD529C67EDE97E582E1068F7D308C93C0CE038AA896F08A40FC79175B5F4D4AA54F1ADCA0724F8712E0D554AB98E14B18DE3093F0557537E33D8CF2B201C65569BB19D90D6
	C77F706CDCF534BD8AB2CD67991C6FD5206384B15779BDAD11353B0F2E0D5550EF1DEE4F37E7A26D39DFB56C3111396C491D479E12186A4098FA834EBBCC7F4F587A82FCE9A2576FA80D547FF44850BFD2BA1FBFF266505F1C483EB760BB531FCE57E8B7604F6B7AFF5C9329BFE34C50FFE24EF30DBD4A87517A97010E46087978DDB8F2BEDCF45DAF05FE493ABDA97BA26D5999B66C79E07FFC76644FE14FF3206300764E566AEAAA1B4571E35201D86B8CF60FD6289FCE778F0B83EA37B02873040328D75F5FD8
	9A50E8B3C15C2B600829FA851FF7127EA4218BBA8686997D7B233157679323732F4F9F0DDA6FBD4F0F4E5EFBDE9C355EFBB60E1A779E6D9B4BA84654A06415DE3D5FABBA6E8B9D779A1509384F3DEEF5C8473FCE6DC2FC33088AB97ECDEA57B8457572EFAC32017BC243654B18E3E048C3124CF9374BB4EE3CBE1FF6D622FD3C81C8B93ABFF64AE823B7A8186C5E236350EE96CFCFCD91BD9EDB4FE23DE0D19F3AE14E24C12DB8BCD641F9DF1DA63A1C9F4D98F2A6E7AECF4EAF09A12753EE48F9E967654989E59B
	F21EF698F29E6A39BCB99939061C0E2D061C3F5BF7F9F294BC9E9376BB7D9D41E2881065BFA7B90ACC36C570BFF657711F2CFD31C73193C78B590569208BEAF1DCA5229B3F14E9BFC5EDA279C18936B43C20E9A1598AFD64A86A04CFF41192CFA77E94CBD1C5F79DDFFF0ACA5EF12126C3783325C34D02B224621A79030BE48770C733240811FDC2E07090DB0CFF8863CFE5336ACE52A9416B89827696D3FBD1CA15AE2578619D87BEE85D384ACEECCDE42BCF925CCD42G240896122F8E4922B2A4795C616089A6
	A80FFEFD263F2684F13B756163E02B3AE1E56BC60F043F1D30ADFDDB130D1030CC8E511E187D2A749E6E06A1C186D5225C22620F00A539DC27DD751E7A61FF1E5A59E7A3D95AA0C749C5E28B87D5627088FE2FEFC2A579DDF05AC18FFB94A4BAG8309A52060EFF4403832A863B8C957D1F54B635537FE67D7DFD29D862BCA5EE75E8456C3032264BFD46771F88322AEGDC8B62776A6231CFD5D41F227062F7DA1FF9339FC241C96C7E6AEA7A6FE17A6F917C77B0459C261843C7403F1B491CFF5174B09FB30D9F
	30E7873587A3202A89A6D17D714F9F3F7B05CFB7EF0251B5119C7A9B991807C22357E7A1FF3FA8DF3B7C563DEFA5265D08539444E889235C2DE1DDA20E3005E6243431233576176E315BC8F68BF1308C8D710BE9966217E66196E2078CCDC38FEF35F0C742AFD28EEAAC199510084D1E9DD9084EB34FAFA25E223D3751FF8BAF5BDB48D5C4F9CB74C16EE6BE44E56D500CC578689BE96CBE43D8FEDF031E4D05F9AA1AFDF9D0F773734FAEBF67AC31919BCC55104866A6E79CF7B4BABBD8A7DC7D0D9C684756834D
	D149D67CD6DA06D93396429E3F0D2A2418FD17ACB3FD6484628EAC6D4BA4CF99238226507886D90644DA53584F536776CB983B036D8FDF6F404FEB6DCF0C01732FA6783F3AAA2F4CD267DF5152BF991666EBD3749EC055B74F270D06257CD77F78177F4D3354E5EDE96EB74F52AE43D27EEB8FAC2D7234FB5E0A99733D45160525B16F17D75C416EABD65F91FDEF1120523B4ABBD39DFB6E6477859B145F12586718AE3F60732902EF24AE6046B3CD7D04AA9A77F10BD5142F70BE9A11511628E8773009677FGD0
	CB878888D85FD5A594GG6CB9GGD0CB818294G94G88G88G8BF954AC88D85FD5A594GG6CB9GG8CGGGGGGGGGGGGGGGGGE2F5E9ECE4E5F2A0E4E1F4E1D0CB8586GGGG81G81GBAGGGDF94GGGG
**end of data**/
}
}
