package com.cannontech.dbeditor.editor.device.capcontrol;

/**
 * This type was created in VisualAge.
 */
import java.awt.Dimension;

import com.cannontech.common.login.ClientSession;
import com.cannontech.database.data.capcontrol.CapBank;
import com.cannontech.roles.application.TDCRole;
 
public class CapBankInfoPanel extends com.cannontech.common.gui.util.DataInputPanel implements java.awt.event.ActionListener, javax.swing.event.CaretListener {
	private Integer originalMapLocID = null;
	private java.util.List points = null;
	private javax.swing.JComboBox ivjJComboBoxControllerType = null;
	private javax.swing.JLabel ivjJLabelControllerType = null;
	private javax.swing.JLabel ivjJLabelSwitchManufacture = null;
	private javax.swing.JLabel ivjJLabelTypeOfSwitch = null;
	private javax.swing.JLabel ivjJLabelMapLocation = null;
	private javax.swing.JComboBox ivjJComboBoxSwitchManufacture = null;
	private javax.swing.JComboBox ivjJComboBoxTypeSwitch = null;
	private javax.swing.JTextField ivjJTextFieldMapID = null;
/**
 * Constructor
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
public CapBankInfoPanel() {
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
	if (e.getSource() == getJComboBoxSwitchManufacture()) 
		connEtoC1(e);
	if (e.getSource() == getJComboBoxTypeSwitch()) 
		connEtoC2(e);
	// user code begin {2}
	// user code end
}
/**
 * Method to handle events for the CaretListener interface.
 * @param e javax.swing.event.CaretEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
public void caretUpdate(javax.swing.event.CaretEvent e) {
	// user code begin {1}
	// user code end
	if (e.getSource() == getJTextFieldMapID()) 
		connEtoC3(e);
	// user code begin {2}
	// user code end
}
/**
 * connEtoC1:  (JComboBoxSwitchManufacture.action.actionPerformed(java.awt.event.ActionEvent) --> CapBankInfoPanel.fireInputUpdate()V)
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
 * connEtoC2:  (JComboBoxTypeSwitch.action.actionPerformed(java.awt.event.ActionEvent) --> CapBankInfoPanel.fireInputUpdate()V)
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
 * connEtoC3:  (JTextField1.caret.caretUpdate(javax.swing.event.CaretEvent) --> CapBankInfoPanel.fireInputUpdate()V)
 * @param arg1 javax.swing.event.CaretEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC3(javax.swing.event.CaretEvent arg1) {
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
 * Return the JComboBoxSwitchManufacture property value.
 * @return javax.swing.JComboBox
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JComboBox getJComboBoxSwitchManufacture() {
	if (ivjJComboBoxSwitchManufacture == null) {
		try {
			ivjJComboBoxSwitchManufacture = new javax.swing.JComboBox();
			ivjJComboBoxSwitchManufacture.setName("JComboBoxSwitchManufacture");
			ivjJComboBoxSwitchManufacture.setFont(new java.awt.Font("sansserif", 0, 12));
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
 * Return the JComboBoxTypeSwitch property value.
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
 * Return the JLabelMapLocation property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getJLabelMapLocation() {
	if (ivjJLabelMapLocation == null) {
		try {
			ivjJLabelMapLocation = new javax.swing.JLabel();
			ivjJLabelMapLocation.setName("JLabelMapLocation");
			ivjJLabelMapLocation.setFont(new java.awt.Font("dialog", 0, 14));
			ivjJLabelMapLocation.setText("Map Location ID:");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJLabelMapLocation;
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
 * Return the JTextField1 property value.
 * @return javax.swing.JTextField
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JTextField getJTextFieldMapID() {
	if (ivjJTextFieldMapID == null) {
		try {
			ivjJTextFieldMapID = new javax.swing.JTextField();
			ivjJTextFieldMapID.setName("JTextFieldMapID");
			// user code begin {1}

			ivjJTextFieldMapID.setDocument( 
					new com.cannontech.common.gui.unchanging.LongRangeDocument() );

			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJTextFieldMapID;
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

	capBank.getCapBank().setSwitchManufacture( getJComboBoxSwitchManufacture().getSelectedItem().toString() );

	capBank.getCapBank().setTypeOfSwitch( getJComboBoxTypeSwitch().getSelectedItem().toString() );

	capBank.getCapBank().setControllerType( getJComboBoxControllerType().getSelectedItem().toString() );

	if( getJTextFieldMapID().getText() == null || getJTextFieldMapID().getText().length() <= 0 )
		capBank.getCapBank().setMapLocationID( new Integer(0) );
	else
		capBank.getCapBank().setMapLocationID(
				new Integer( getJTextFieldMapID().getText() ) );

	originalMapLocID = capBank.getCapBank().getMapLocationID();

	return val;
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
	getJComboBoxSwitchManufacture().addActionListener(this);
	getJComboBoxTypeSwitch().addActionListener(this);
	getJTextFieldMapID().addCaretListener(this);
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

		java.awt.GridBagConstraints constraintsJLabelSwitchManufacture = new java.awt.GridBagConstraints();
		constraintsJLabelSwitchManufacture.gridx = 1; constraintsJLabelSwitchManufacture.gridy = 1;
		constraintsJLabelSwitchManufacture.anchor = java.awt.GridBagConstraints.WEST;
		constraintsJLabelSwitchManufacture.ipadx = 3;
		constraintsJLabelSwitchManufacture.insets = new java.awt.Insets(32, 6, 7, 0);
		add(getJLabelSwitchManufacture(), constraintsJLabelSwitchManufacture);

		java.awt.GridBagConstraints constraintsJComboBoxSwitchManufacture = new java.awt.GridBagConstraints();
		constraintsJComboBoxSwitchManufacture.gridx = 2; constraintsJComboBoxSwitchManufacture.gridy = 1;
		constraintsJComboBoxSwitchManufacture.fill = java.awt.GridBagConstraints.HORIZONTAL;
		constraintsJComboBoxSwitchManufacture.anchor = java.awt.GridBagConstraints.WEST;
		constraintsJComboBoxSwitchManufacture.weightx = 1.0;
		constraintsJComboBoxSwitchManufacture.ipadx = 60;
		constraintsJComboBoxSwitchManufacture.insets = new java.awt.Insets(30, 1, 5, 27);
		add(getJComboBoxSwitchManufacture(), constraintsJComboBoxSwitchManufacture);

		java.awt.GridBagConstraints constraintsJLabelTypeOfSwitch = new java.awt.GridBagConstraints();
		constraintsJLabelTypeOfSwitch.gridx = 1; constraintsJLabelTypeOfSwitch.gridy = 2;
		constraintsJLabelTypeOfSwitch.anchor = java.awt.GridBagConstraints.WEST;
		constraintsJLabelTypeOfSwitch.ipadx = 34;
		constraintsJLabelTypeOfSwitch.insets = new java.awt.Insets(7, 6, 7, 0);
		add(getJLabelTypeOfSwitch(), constraintsJLabelTypeOfSwitch);

		java.awt.GridBagConstraints constraintsJComboBoxTypeSwitch = new java.awt.GridBagConstraints();
		constraintsJComboBoxTypeSwitch.gridx = 2; constraintsJComboBoxTypeSwitch.gridy = 2;
		constraintsJComboBoxTypeSwitch.fill = java.awt.GridBagConstraints.HORIZONTAL;
		constraintsJComboBoxTypeSwitch.anchor = java.awt.GridBagConstraints.WEST;
		constraintsJComboBoxTypeSwitch.weightx = 1.0;
		constraintsJComboBoxTypeSwitch.ipadx = 60;
		constraintsJComboBoxTypeSwitch.insets = new java.awt.Insets(5, 1, 5, 27);
		add(getJComboBoxTypeSwitch(), constraintsJComboBoxTypeSwitch);

		java.awt.GridBagConstraints constraintsJLabelControllerType = new java.awt.GridBagConstraints();
		constraintsJLabelControllerType.gridx = 1; constraintsJLabelControllerType.gridy = 3;
		constraintsJLabelControllerType.anchor = java.awt.GridBagConstraints.WEST;
		constraintsJLabelControllerType.ipadx = 18;
		constraintsJLabelControllerType.insets = new java.awt.Insets(8, 6, 9, 10);
		add(getJLabelControllerType(), constraintsJLabelControllerType);

		java.awt.GridBagConstraints constraintsJComboBoxControllerType = new java.awt.GridBagConstraints();
		constraintsJComboBoxControllerType.gridx = 2; constraintsJComboBoxControllerType.gridy = 3;
		constraintsJComboBoxControllerType.fill = java.awt.GridBagConstraints.HORIZONTAL;
		constraintsJComboBoxControllerType.anchor = java.awt.GridBagConstraints.WEST;
		constraintsJComboBoxControllerType.weightx = 1.0;
		constraintsJComboBoxControllerType.ipadx = 60;
		constraintsJComboBoxControllerType.insets = new java.awt.Insets(6, 1, 7, 27);
		add(getJComboBoxControllerType(), constraintsJComboBoxControllerType);

		java.awt.GridBagConstraints constraintsJLabelMapLocation = new java.awt.GridBagConstraints();
		constraintsJLabelMapLocation.gridx = 1; constraintsJLabelMapLocation.gridy = 4;
		constraintsJLabelMapLocation.anchor = java.awt.GridBagConstraints.WEST;
		constraintsJLabelMapLocation.insets = new java.awt.Insets(10, 6, 60, 41);
		add(getJLabelMapLocation(), constraintsJLabelMapLocation);

		java.awt.GridBagConstraints constraintsJTextFieldMapID = new java.awt.GridBagConstraints();
		constraintsJTextFieldMapID.gridx = 2; constraintsJTextFieldMapID.gridy = 4;
		constraintsJTextFieldMapID.fill = java.awt.GridBagConstraints.HORIZONTAL;
		constraintsJTextFieldMapID.anchor = java.awt.GridBagConstraints.WEST;
		constraintsJTextFieldMapID.weightx = 1.0;
		constraintsJTextFieldMapID.ipadx = 110;
		constraintsJTextFieldMapID.ipady = 3;
		constraintsJTextFieldMapID.insets = new java.awt.Insets(8, 1, 58, 99);
		add(getJTextFieldMapID(), constraintsJTextFieldMapID);
		initConnections();
	} catch (java.lang.Throwable ivjExc) {
		handleException(ivjExc);
	}
	// user code begin {2}

	boolean amfmInterface = false;
	try
	{	
		amfmInterface = ClientSession.getInstance().getRolePropertyValue(
			TDCRole.CAP_CONTROL_INTERFACE, "NotFound").trim().equalsIgnoreCase( "AMFM" );
	}
	catch( java.util.MissingResourceException e )
	{}
	
	getJLabelMapLocation().setVisible( amfmInterface );
	getJTextFieldMapID().setVisible( amfmInterface );
	
	// user code end
}
/**
 * This method was created in VisualAge.
 * @return boolean
 */
public boolean isInputValid() 
{
	//if we are using MapLocation IDs, we must validate them!
	if( getJTextFieldMapID().isVisible()
		 && getJTextFieldMapID().getText() != null 
		 && getJTextFieldMapID().getText().length() > 0 )	
	{
		int[] mapIDs = null;
		
		if( originalMapLocID != null )
			mapIDs = com.cannontech.database.data.capcontrol.CapControlYukonPAOBase.getAllUsedCapControlMapIDs( originalMapLocID.intValue() );
		else
			mapIDs = com.cannontech.database.data.capcontrol.CapControlYukonPAOBase.getAllUsedCapControlMapIDs();

		StringBuffer buf = new StringBuffer("The MapLocationID selected is already used, try another\nUsed IDs: ");
		long mapID = Long.parseLong(getJTextFieldMapID().getText());
		for( int i = 0; i < mapIDs.length; i++ )
			if( mapIDs[i] == mapID )
			{
				//setErrorString("The MapLocationID selected is already used, try another");
				for( int j = 0; j < mapIDs.length; j ++ )
				{
					if( (j % 20 == 0) && j != 0 )
						buf.append("\n  ");
						
					buf.append( mapIDs[j] + "," );
				}

				setErrorString( buf.toString() );
				return false;
			}
	}

	return true;
}
/**
 * main entrypoint - starts the part when it is run as an application
 * @param args java.lang.String[]
 */
public static void main(java.lang.String[] args) {
	try {
		javax.swing.JFrame frame = new javax.swing.JFrame();
		CapBankInfoPanel aCapBankInfoPanel;
		aCapBankInfoPanel = new CapBankInfoPanel();
		frame.setContentPane(aCapBankInfoPanel);
		frame.setSize(aCapBankInfoPanel.getSize());
		frame.addWindowListener(new java.awt.event.WindowAdapter() {
			public void windowClosing(java.awt.event.WindowEvent e) {
				System.exit(0);
			};
		});
		frame.show();
		java.awt.Insets insets = frame.getInsets();
		frame.setSize(frame.getWidth() + insets.left + insets.right, frame.getHeight() + insets.top + insets.bottom);
		frame.setVisible(true);
	} catch (Throwable exception) {
		System.err.println("Exception occurred in main() of com.cannontech.common.gui.util.DataInputPanel");
		com.cannontech.clientutils.CTILogger.error( exception.getMessage(), exception );;
	}
}
/**
 * This method was created in VisualAge.
 * @param val java.lang.Object
 */
public void setValue(Object val ) 
{
	// if val != null, then we will display the combo boxes with data
	if( val != null )
	{
		CapBank capBank = (CapBank)val;			

      com.cannontech.common.util.CtiUtilities.setJComboBoxSelection( 
         getJComboBoxSwitchManufacture(), capBank.getCapBank().getSwitchManufacture() );
      

      com.cannontech.common.util.CtiUtilities.setJComboBoxSelection( 
         getJComboBoxTypeSwitch(), capBank.getCapBank().getTypeOfSwitch() );

      com.cannontech.common.util.CtiUtilities.setJComboBoxSelection( 
         getJComboBoxControllerType(), capBank.getCapBank().getControllerType().toString() );

		//set our map location id values
		originalMapLocID = capBank.getCapBank().getMapLocationID();
		getJTextFieldMapID().setText( capBank.getCapBank().getMapLocationID().toString() );
	}

}
/**
 * 
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private static void getBuilderData() {
/*V1.1
**start of data**
	D0CB838494G88G88GEEF954ACGGGGGGGGGGGG8CGGGE2F5E9ECE4E5F2A0E4E1F4E14DBB8DF4D465152000D12300465D883AC60B1A36E945165DE5156D529C3655B6D6ED35D225BD694ADA0EA7366CB127C7EB5CFEB38913C4C8B279A144GF5A0D0C9CDC3C2D0C28C98C8088486491291C2E5D7DEE65E7CA4AF7306B7EFC882C8765E6FFB5F1BB713B789305DF34CB9F75E7B6E7D3EFB6FF73FFB6FF73F77DEC855BBE9EBAC56AAC2ACEB08763747C2C8518704FCED6DBFAD6638E9E2260D98
	7E36G1413437682384E815A42F919B6BB699EAF027682E82F3BBD5336816EAB495171B3FF061B84F2F6836D561FADDFB4391CD9A5DCCEA1ED9F9557413594A09DF0F84D95D13EDB2C5170D99A1E0345CAC836977BDC96B7E938D6E877825C8530504B7ABF8657E2AAF9E6DB2B566F4C5CB4127B03F72DA548474013438E9F9B6B542E8E7210A55707329E24FA62189950DE8200134FA7EB576C07EBFB6F10EF3FDB9EEEF48B41209CD4C5373FD17484D4D9E974086783EE9188A1B79094D9EAEB6B94C2870560D0
	CF50AB778AC1D1A2A464C7993F910BA8CF188F3473A86EC7FBD06EA2384F82D829636FE8C5FCC7A053568F908A70396EFB37DD1BCB7F45074819670279E589661A94E0F3DC98084EB5A5723B4237693AFC2F946D1F83B47BE026ED9B40E1GBFC09240C66A8FCF147C892E878654CEF9F8D88E9E88058ECA9E65186C91A538EFEB830A06BBA13AE545C388DB73EF088DA67ACC86EC5C23D7BD0E59A43D052FFF460987480AEF9F35961A58E4C5FA13B5ED1059E271E054A6CCF6EEC2594B87E37DB3D676A9E2146D
	1CA5672FB1113D6859FAEB0E46078CC5E5B78D61DA9F24EB6D00FB0B669F8C6F24F85500E73E7A8C7599EC4FG5A2AA16E9B7D25DC97E741BCE2EFF61A5AA1B5730055A9B19DBCD23CAE45FB11779C0949AC56F52926788C831E69B21F6A086D470056B01CE9E37E75249DE3F78E34B3G16812CG48832889C23FA077316A521DD768E33D02229E0C84A40F2890B6764952EA380A232AA27484432A90F40BA785A5A08CC8E218E8FD1617228D86E2467D1C7A3E9BE4FCAC86C5C5D083F290388B4322AAAAE15667
	2552F26C2308146A8A8C0BA0E0B8C4B85FDF17D642D592426A27A10F200A519E0C7E5BD23449F961A4F08486F04F6C12D70E76F2427CBDG2AEE07F3E5486F2828C03EE8EBF34A72D0A464421910E5C13473DAAF660EA438CF8BF29B3FD60138A2B9536610B91F74328A831F2E009B559714B19EE307DBB1BF6F16394F7C3A1F7BCCD7497D246159A326BE036012192FC8F2FC6C1C20BCD1AF56FEFECF7F95466FEE59BC36264E952FDE310C7C2B64DF2A2F15378A57AA85F4DC887014EE63974B9C4CE78F8AE111
	FB69F1859CCC74C4AD4E74AC5BEB076BDACD0FB51A7E0C360952D681DFFBA853568AF0B644EDF8EFEB3FC1FFBE2ED463F53573B13761A33A8C4E3F787FE033F8E0367A199D77A7713C98D43B4410987440CDD823E55BD11E963FDED99F73924507A05EBBA14042BABE470EB1A9282A92980828E21F282A0120AFCA4F2532421101D0B4D5F0D9EF58F10E7288443B5E1F14E32C40369D94692269C3B43A358257DF1444E1D03AD30E84D506AF2AC0FF9F924734DC312E828B21730294413C436A0605822ED3C8898C
	03ABE06AA2B9A10C632D5E9A030D12G07754FD238BE9362B1B9D247E3F25B05FB49524F37DB4D7614547B3B2C3B4F3178B9FCAE9A13CC27B793681EDFD994450773197EDB2158A3DA55422DCAAB979687772CC7DEFE10F189A0659C0F0371CA2473DC2385C027ACC94C1C3A1D25AA747330F8AEA2C20A4624F6D437DD382AA20EF6D233A323DF2CC2BBCA06FC5825209DFB77725AB08374D98630729C3365B605590275FB3E1D771B836DF400479433FEFF370F77AB8AC3BE8DA76AF7E2BF6F5781FD7A81C64331
	7D587A97FE0E7504D95D680B849AA3EAG333320026307A2AA35B52B9113E91EC79E0B423C96DC9CC07B67C03BC04D34EDD2391FBCDD42EB411421B4127D155A04F56FEE1569562FC67D44F1BD2F85F32BD1E6AA5052D53E363F2DB26EC78742E1599D20DB2136AE772FB77A9143FDED7DDBF0758614305A8301BE4A57F061FA549777CA8D2D558EE334264039AE813939G7E8862FFCA2A8C3AB7C4581E6702AB7357875AF7E19E20CE5648AE3D22621515E15133E3D0B8AFB48AA3EAA34DB90D87A879905E5773
	35D9D6DF417494297559863E237EA42F696E2B6C50E4E7FDF39EE99F2F341A5558C9E51B2D8BA34C1E4BA3D13BDE7A16ADBF67FF5EA8E4F34C5F4767386DFC266D4CF9BE47F963E6F3CCB94F67B8386FDA674864FEDF171B8EBC33F539ABCD65560C007D69380B7B79388640F9C678389FD80C636E9AE163160DF0FD7FEDBF56F1EEC19155C67A2BD995D59DED8C0FC0CA51346DC42A26AC5B0766FA63EB6F65A3513AA0E7E4E20D40EA52430328AB99E5B5E9390EBFCD718D86BC1B5BF7E89CE13B9FE8ABC7F99D
	F5FD8D666450D826AD99E08140920095GF9E3BC9EDA2BDB4894F5EA8F44379294243E188DC80B01572B51CE30E7E86DB72B5146F44FA0FCBFBE549EEF071A8B31F35F9D57E6637AA60CEB0CEB77470DE3F16013B6729CE5D8263636A3B047817920BC2A1DDBCBACBCA705AE705C73C537B177ACA95BE5DD1BA07724EB329F3B900D11867D9C7A67DA4C27C597A1FF8234837483DC82C885D8F2115B7F068D7B2632BF75A9175C89384330DFF4466F6B3A3D0A77DD1B1DB7CC9897EF679597E347B17BBDBD4E6D17
	FB1157F7CFDC238EAFCC29C343A5B39DB6922EC357A52E43747D5726436C896362F538F4494C5F366F58E46EEFAE28813B832264E13ADA2C7CB9C98A70E1671FF43AAFE6C1BBDB474D74A32E608B58933F60F37AE45081578EE8FBG1281B2G32GF2G6C17B3EDCD17F95F4FE9DF67E5D697C817593E1FFC1955939917597E4E7AAE8BCCB371EDDAFFFC911DAB4AC57E3C6D51641A0DCB567432DF3E72B3905365D789F5A9B65C6793166B0CEBD0B289DF0C5F32AB54830145E5F2CD627A7E769A737A7E4E9A73
	7A7E1E1AF206879E4653BE27BFD8B3F97D7FD00DD97D3F195A62716AB24ABB9C9643B4D1E0712551FE252EA7A66700CF04E1D133E3A53954DE2C75B9244A1D9F68EBD1E1B146C2D65CDAE61B2CAD4BFB29D5381F66825DBE1EE95B815081F0F61C673BC3EFCF395F9C9A1DE43F59D31BF8DD76571A2FCBD7AD7AC7D80EA8EEB15E76C7EBEBF4DAB433762862B01A0975F151712A20784489EB77999D4FE80946FBEA932DAFDB030EFD46B5E8565760276346B518B69EEB73147109EB4078956CB7725B2573F390A3
	3F743871CB9272DB9C432FC567E733CE76DC7329047C7E450B67CAC99813A3AAA13D53E8ED2C1F9FD74238BC00ED00EF1D466AB116E79F9E41D876AA42B0182FAFA43AC943F49CFB171F0FCD057E99GD9GB9G6B2673BA4EC25F859CF18A832254B792D05D7EE3C2B062058AB422081ADE771656E29FDEA5CC6C2675FBF928C267659A8B09473D2CAB273F50DDE96403DD348E0CFE4CD7250F6FE48F57A5D141EE0CDE7C178D467131DDB49E7F6DAE57F99C93C2CE59ADE8E7D3C63F7DB30C13688E84FDFA3A88
	2BF97FF650D8732A53190DD327731A38B1E2C64F5069B5440C3ED42797D84C684FCC677BDBBD2D3BD6C17BB59D376EB45F7356DD47F3C5DAD562D8FF304ABC563FD1E51E033FDDB5F98EFE34AAD10CB27DDE21354E9950CD3A0E6B5C3A116BBCCD476D217548D3504E56F1771C627DF2F55CE6AB47F5DD4F4FA87F69656FEAD2G17F5BD777B6633B175851E105A5ABED6821E03024FC9230855975D3EA2EDFC418CFE46D6FD48339D5AAEGE9862FF3C77F486B5C0C87669167342326E76CEC57AE6B0C99AC0E53
	E7C46B5CF428F3194CCCBF173992681BE6A2EE355F387E680F8FDBD8AD4568DB83C6BA5A3D0E98692E98BA6A7B4FD6A3FDD68C9DED34D99B4F6676436DFCEEF9BFCCA7525F6FCD787C40B3134DA9C93B761E2CA35BEEFA2310597DD51A13CC6ABA9AF5EC7EBF9A4479E3BEDC8ABC98CE0CF05CEBBA6EB8613826D99C772105637C33F81E6A3060B3B8AF04FC76AC6CF31276C9037B8C007E9B90D7F73A4BE4AEC6FB94EAF6C75886E3DA816AB41A936E47B4FBE839F1A8D6E6B555B58D7AE55C40E59E5FB8154CFC
	83E447494C1DA0B3B592AB73AC15C912A07E13384CFFB8B515CC235C0E24D8192E2428CC46EF37F5AAFE4586FEE7637829507EB9B1BE67AE70DD5B7B400AEB9CF7742A473167865DFE4C81531258F3830B0EBFCE714986BC4BC397FCBC8F2582ADAF09BFCF38DC0D71F076C6B8478224GE4GE4812C3A11676C7FDA5FC426286FDCF24889C9DDB224E1B63F6F7B2F6DBD69F78BF93CAFFA64BE32F8566B16C4E7763C9BD91CEF3A09DDD7396A8C35EBB9FDDE62841A9FA099E08140E20027EE62733BF3FD831B5F
	A1CFC0BD202A025BCF1F9472491D8C08A3D1B496A9ECEEBF99B44EED25A67FB56D3A6E660935929BB798B10EF3E87DF6EB5743894739E2EA3DB3DA3F10F61D11F0DCC7CC5D1F2A754B502ECBCD46BDF3B3FEAF1104E12245766F7B3743B5A80EB87487FA5D9068BB360605A01CD31400F7EB03232941513C2BDEDF43349163378E2DEF6DB1FDB700D0134CF4E8502ECBEE28A36FD0390465907A377A2309B82E6B1B5B08AE77176B386FEC7A7AFC52755C6B163A84F2BD1A3CC54951FDF055E0DEA16379623B5179
	3F3771EA66EF26BB63E92B0F6A7C2D77227CEFBB7557617F9F0DE4D2FA9506326697CEFF747ED3437DB45F153E2F3C96E0B26E7CFF1521BD072BCEDCEB5EDAEDDEEB26D41B571A732AA7BF4F5FD71D78FD5ED72B0D6F73D8DEDA590475CE8E78DD51AD193686008EG57ADBCA76D593015501CE4120F8EF9FCF82E452F88D4AE3FF612E79BDBEA267E7660CFB578BEE4C0D6D5F99879F3FCD38D2E17AA07A812615E274FC414004F2F322E9A7E439A74B7C974D2B42B09DF9E64B5F66896DEA7FFDA0B389950CE51
	F10F54725AB9CD47CD0F703AFB350EEBF551FE37E65A2AEE65383BDDFC2CC3478DD15F8FC1FB1A0EFB10566CA950DE2063567573318BF55CA9DA1F1159F0BFDB57191EE536C1FB370EFBC4BF5FF44C66B578131F99F31EFD5E96AB5F0763FDF44EEC16F36E1A9D4DBD4CB73AF751F983BEE7B6770765F518ACCE8A035A6EFB08FE51C5BD82103D0A9C9295F50CE169DBC94D4F2EEB417AE85D1CCC5B8EGA70087E094E08E40820045GD9GAB8172G6CF3611C8E508AF098608CC0E8AE576360767AC974508EB0
	54B995A1006E495F07B7A5764FD71B26764F3CA67A8E8F7C8A8F4C8CE7E946E78E32579B9675B830B55BF5DCCF38D78E8770884D6A518DC3546FE0AEF366725ADC2A451AD58561ACBF865E47D5E43565533513551679F150EA280FF13D174EE54FE3711E497FA67567AC40AF57659FF7454ACF7EA0AAFF11EBAA79EBA611DFF4DB266D90B156675711587ABC1546C89574F35C4675794E29D8FD9E5A9355272EFFAAFD8AA7512797E4EC0F51A798F7AE1AC1636E8C74736B7AC827E37579F12BE1FDA6BDB3617862
	C974C93DADDAA333D8995FCBF4FFCDED5E1E484FA1839F01BD7A78402068D6DD02CF9FF3F4974A55778896069A6D44AE4C7701A03823A085AE5087BBFD50B168E3743FE9C99C9FF737CC9D9F8F3418676F0C1609797B6196737C7D0FAD467C4DF0CBDA5047FC90577DFA3F6F6A3853BA6EFB14A762CE85BC2ADF47E7D13D90FFD8C4019C7FE32A57884575EB38678C38539AEEC58B46B8CC59AFABFC6CAFDB285F84A409357F1D768984A164F5FB3CCCF58C0484035E3F51F128B773270578492C660FBD3538671A
	54E09ED8B3592795E03B6B79581FFCC4F4BE1D8723FC9ADCD747475B996573DEF7144F7CD3D7294F47D1BEEDC78C73BAFDF5FC7CC7F1AC7B4EFACB499D30A8992F10B5E10514440C7F57FB5FFFA96B97A743794456C35E46B06E03BA953DDB74609B8B6DF342EED29C12E1AB6883FC8FA988534B1A30BAA609CED265BD011F5304D551F30047CDD8A56B1DA8E9A7FEFA32733090762B686B9FBAC9C190BFFCA4950CAC89C15FCE96C49F0E62670E16839D28B9212F5AC15FCA2AAFF2E95439D4BEDA36FD4B11150B
	7309351B3CA5490227DBF0C3E87710E2552F08E13FACF9C6C39FB1C6C5A39891740BB8E037D11FBECE36911F436ACA8F0D5E1DDC322A245BCA4828C2F6521E18359A759E1ECE3F2000A8D169D171C3CED9592653267FC17D63939FEFB8E3A596ED126748E5E29D8D2944669506835218CA0A7B60A40096760611E8838CA404E0983F33G659CF14AF112AE237176471B6E786A6F7EC2E5C4072A649DE6CD983A53A74A43BB8FF83D012028B3G53827B4DBAFB6C53985727E2F46E7AA34F7EFEGDC210B648FB7B5
	515FE67A3B8BFF1BA92619E21AF701FDD713C97F626923FC4E54FFC01FB22A8FFAC0E3B7AC227A7CAB0F5F77C27B6A9C18DDB7D9C33FD500F530C7FBFD92999E9015D999F7ECDE1B12FDB3AED3883DE594793EB52AF344997650CC12FD3E25AD7F452F64DBC9C18F31314C8A7E0B69917C17E64F9E128F19153A9E5F69B153582832E402C3A236E022E7A13A48383E08D8CBFB1750DF7BD5DB8BC7D55031D57AA4B7B29BE2D873E3B8AA7B07AF2532DB4D15DD23E35BB42C9B55D3C9A5FB9963B03C59E9D3C99956
	0A8E596BE0D4405823AFDE9CC8C454B1A164606FDA9CBDDDED86031C255752ABB0080D65523F2EB54EEA6D4628B5504F783F32B83EFC1A3EEF2EE96117CF53762826451A26D82994FD79B46D0CEA5A2EE9DA2FE94B4E1C756346B367EBB3EB92BEE74B1D474E1AC573634F1C9F15631C3AGFFF6BEBF6B4D36633FD8A52A017B0685C9D285E968C05003757041B1D50CBE8F69AAC77EE15EC7A3EAF5F2B96ABDE898733FD0CB8788C527E128BA93GGB0B6GGD0CB818294G94G88G88GEEF954ACC527E128
	BA93GGB0B6GG8CGGGGGGGGGGGGGGGGGE2F5E9ECE4E5F2A0E4E1F4E1D0CB8586GGGG81G81GBAGGGF493GGGG
**end of data**/
}
}
