package com.cannontech.dbeditor.wizard.point;

/**
 * This type was created in VisualAge.
 */

public class PointAccumulatorSettingsPanel extends com.cannontech.common.gui.util.DataInputPanel {
	private javax.swing.JLabel ivjDataOffsetLabel = null;
	private javax.swing.JLabel ivjMultiplierLabel = null;
	private javax.swing.JComboBox ivjUnitOfMeasureComboBox = null;
	private javax.swing.JLabel ivjUnitOfMeasureLabel = null;
	private javax.swing.JTextField ivjDataOffsetTextField = null;
	private javax.swing.JTextField ivjMultiplierTextField = null;
	private javax.swing.JRadioButton ivjDemandReadingRadioButton = null;
	private javax.swing.JRadioButton ivjDialReadingRadioButton = null;
	private javax.swing.ButtonGroup buttonGroup = null;
	private javax.swing.JPanel ivjReadingPanel = null;
/**
 * Constructor
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
public PointAccumulatorSettingsPanel() {
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
		getButtonGroup().add(getDemandReadingRadioButton());
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
		getButtonGroup().add(getDialReadingRadioButton());
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}
/**
 * Insert the method's description here.
 * Creation date: (4/30/2001 3:14:26 PM)
 */
public int getAccumulatorPointType() 
{

	if(getDialReadingRadioButton().isSelected() )
	{
		return com.cannontech.database.data.point.PointTypes.PULSE_ACCUMULATOR_POINT;
	}
	else
	{
		return com.cannontech.database.data.point.PointTypes.DEMAND_ACCUMULATOR_POINT;	
	}
	
}
/**
 * Return the ButtonGroup property value.
 * @return javax.swing.ButtonGroup
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.ButtonGroup getButtonGroup() {
	if (buttonGroup == null) {
		try {
			buttonGroup = new javax.swing.ButtonGroup();
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return buttonGroup;
}
/**
 * Return the DataOffsetLabel property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getDataOffsetLabel() {
	if (ivjDataOffsetLabel == null) {
		try {
			ivjDataOffsetLabel = new javax.swing.JLabel();
			ivjDataOffsetLabel.setName("DataOffsetLabel");
			ivjDataOffsetLabel.setFont(new java.awt.Font("dialog", 0, 14));
			ivjDataOffsetLabel.setText("Data Offset:  ");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjDataOffsetLabel;
}
/**
 * Return the DataOffsetTextField property value.
 * @return javax.swing.JTextField
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JTextField getDataOffsetTextField() {
	if (ivjDataOffsetTextField == null) {
		try {
			ivjDataOffsetTextField = new javax.swing.JTextField();
			ivjDataOffsetTextField.setName("DataOffsetTextField");
			ivjDataOffsetTextField.setFont(new java.awt.Font("sansserif", 0, 14));
			ivjDataOffsetTextField.setColumns(0);
			ivjDataOffsetTextField.setDocument( new com.cannontech.common.gui.unchanging.DoubleRangeDocument(000000.000000000, 999999.999999999, 10) );
			// user code begin {1}

			getDataOffsetTextField().setText( (new Double(0.0)).toString() );
			
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjDataOffsetTextField;
}
/**
 * Return the JRadioButton2 property value.
 * @return javax.swing.JRadioButton
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JRadioButton getDemandReadingRadioButton() {
	if (ivjDemandReadingRadioButton == null) {
		try {
			ivjDemandReadingRadioButton = new javax.swing.JRadioButton();
			ivjDemandReadingRadioButton.setName("DemandReadingRadioButton");
			ivjDemandReadingRadioButton.setFont(new java.awt.Font("dialog", 0, 12));
			ivjDemandReadingRadioButton.setText("Demand");
			ivjDemandReadingRadioButton.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
			ivjDemandReadingRadioButton.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjDemandReadingRadioButton;
}
/**
 * Return the JRadioButton1 property value.
 * @return javax.swing.JRadioButton
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JRadioButton getDialReadingRadioButton() {
	if (ivjDialReadingRadioButton == null) {
		try {
			ivjDialReadingRadioButton = new javax.swing.JRadioButton();
			ivjDialReadingRadioButton.setName("DialReadingRadioButton");
			ivjDialReadingRadioButton.setFont(new java.awt.Font("dialog", 0, 12));
			ivjDialReadingRadioButton.setText("Dial");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjDialReadingRadioButton;
}
/**
 * Return the MultiplierLabel property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getMultiplierLabel() {
	if (ivjMultiplierLabel == null) {
		try {
			ivjMultiplierLabel = new javax.swing.JLabel();
			ivjMultiplierLabel.setName("MultiplierLabel");
			ivjMultiplierLabel.setFont(new java.awt.Font("dialog", 0, 14));
			ivjMultiplierLabel.setText("Multiplier:  ");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjMultiplierLabel;
}
/**
 * Return the MultiplierTextField property value.
 * @return javax.swing.JTextField
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JTextField getMultiplierTextField() {
	if (ivjMultiplierTextField == null) {
		try {
			ivjMultiplierTextField = new javax.swing.JTextField();
			ivjMultiplierTextField.setName("MultiplierTextField");
			ivjMultiplierTextField.setFont(new java.awt.Font("sansserif", 0, 14));
			ivjMultiplierTextField.setColumns(0);
			// user code begin {1}

			ivjMultiplierTextField.setDocument( new com.cannontech.common.gui.unchanging.DoubleRangeDocument(-999999.999999999, 999999.999999999, 9) );

			getMultiplierTextField().setText( (new Double(1.0)).toString() );

			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjMultiplierTextField;
}
/**
 * Return the ReadingPanel property value.
 * @return javax.swing.JPanel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JPanel getReadingPanel() {
	if (ivjReadingPanel == null) {
		try {
			com.cannontech.common.gui.util.TitleBorder ivjLocalBorder;
			ivjLocalBorder = new com.cannontech.common.gui.util.TitleBorder();
			ivjLocalBorder.setTitleFont(new java.awt.Font("Arial", 1, 14));
			ivjLocalBorder.setTitle("Reading Type");
			ivjReadingPanel = new javax.swing.JPanel();
			ivjReadingPanel.setName("ReadingPanel");
			ivjReadingPanel.setBorder(ivjLocalBorder);
			ivjReadingPanel.setLayout(new java.awt.GridBagLayout());
			ivjReadingPanel.setFont(new java.awt.Font("dialog", 0, 12));

			java.awt.GridBagConstraints constraintsDialReadingRadioButton = new java.awt.GridBagConstraints();
			constraintsDialReadingRadioButton.gridx = 1; constraintsDialReadingRadioButton.gridy = 1;
			constraintsDialReadingRadioButton.ipadx = 1;
			constraintsDialReadingRadioButton.insets = new java.awt.Insets(0, 12, 6, 2);
			getReadingPanel().add(getDialReadingRadioButton(), constraintsDialReadingRadioButton);

			java.awt.GridBagConstraints constraintsDemandReadingRadioButton = new java.awt.GridBagConstraints();
			constraintsDemandReadingRadioButton.gridx = 2; constraintsDemandReadingRadioButton.gridy = 1;
			constraintsDemandReadingRadioButton.ipadx = -4;
			constraintsDemandReadingRadioButton.insets = new java.awt.Insets(0, 3, 6, 6);
			getReadingPanel().add(getDemandReadingRadioButton(), constraintsDemandReadingRadioButton);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjReadingPanel;
}
/**
 * Return the UnitOfMeasureComboBox property value.
 * @return javax.swing.JComboBox
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JComboBox getUnitOfMeasureComboBox() {
	if (ivjUnitOfMeasureComboBox == null) {
		try {
			ivjUnitOfMeasureComboBox = new javax.swing.JComboBox();
			ivjUnitOfMeasureComboBox.setName("UnitOfMeasureComboBox");
			ivjUnitOfMeasureComboBox.setFont(new java.awt.Font("dialog", 0, 14));
			ivjUnitOfMeasureComboBox.setMaximumRowCount(6);
			// user code begin {1}

			//Add units of measure to the Unit of Measure combo box
			com.cannontech.database.cache.DefaultDatabaseCache cache = com.cannontech.database.cache.DefaultDatabaseCache.getInstance();
			synchronized(cache)
			{
				java.util.List allUnitMeasures = cache.getAllUnitMeasures();
				for(int i=0; i<allUnitMeasures.size(); i++)
					getUnitOfMeasureComboBox().addItem( allUnitMeasures.get(i) );
			}
			
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjUnitOfMeasureComboBox;
}
/**
 * Return the UnitOfMeasureLabel property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getUnitOfMeasureLabel() {
	if (ivjUnitOfMeasureLabel == null) {
		try {
			ivjUnitOfMeasureLabel = new javax.swing.JLabel();
			ivjUnitOfMeasureLabel.setName("UnitOfMeasureLabel");
			ivjUnitOfMeasureLabel.setFont(new java.awt.Font("dialog", 0, 14));
			ivjUnitOfMeasureLabel.setText("Unit Of Measure:  ");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjUnitOfMeasureLabel;
}
/**
 * This method was created in VisualAge.
 * @return java.lang.Object
 * @param val java.lang.Object
 */
public Object getValue(Object val)
{
	//Assuming commonObject is an AccumulatorPoint
	com.cannontech.database.data.point.AccumulatorPoint point = (com.cannontech.database.data.point.AccumulatorPoint) val;

	if( getAccumulatorPointType() == com.cannontech.database.data.point.PointTypes.PULSE_ACCUMULATOR_POINT )
		point.getPoint().setPointType( com.cannontech.database.data.point.PointTypes.getType(com.cannontech.database.data.point.PointTypes.PULSE_ACCUMULATOR_POINT) );
	else
		point.getPoint().setPointType( com.cannontech.database.data.point.PointTypes.getType(com.cannontech.database.data.point.PointTypes.DEMAND_ACCUMULATOR_POINT) );
	
	//Make sure the text in correct format
	Double multiplier = null;
	Double dataOffset = null;

	try
	{
		multiplier = new Double(getMultiplierTextField().getText());
	}
	catch (NumberFormatException n)
	{
		com.cannontech.clientutils.CTILogger.error( n.getMessage(), n );
		multiplier = new Double(1.0);
	}

	try
	{
		dataOffset = new Double(getDataOffsetTextField().getText());
	}
	catch (NumberFormatException n)
	{
		com.cannontech.clientutils.CTILogger.error( n.getMessage(), n );
		dataOffset = new Double(0.0);
	}

	int uOfMeasureID =
		((com.cannontech.database.data.lite.LiteUnitMeasure) getUnitOfMeasureComboBox().getSelectedItem()).getUomID();


	point.getPointAccumulator().setDataOffset(dataOffset);
	point.getPointAccumulator().setMultiplier(multiplier);
	point.getPointUnit().setUomID( new Integer(uOfMeasureID) );
	point.getPointUnit().setDecimalPlaces(new Integer(com.cannontech.dbeditor.DatabaseEditor.getDecimalPlaces()));
	point.getPoint().setStateGroupID(new Integer(com.cannontech.database.db.state.StateGroupUtils.STATEGROUP_ACCUMULATOR));

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
 * Initialize the class.
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void initialize() {
	try {
		// user code begin {1}
		connEtoM1();
		connEtoM2();
		// user code end
		setName("PointAccumulatorSettingsPanel");
		setPreferredSize(new java.awt.Dimension(350, 200));
		setLayout(new java.awt.GridBagLayout());
		setSize(445, 214);

		java.awt.GridBagConstraints constraintsUnitOfMeasureLabel = new java.awt.GridBagConstraints();
		constraintsUnitOfMeasureLabel.gridx = 1; constraintsUnitOfMeasureLabel.gridy = 1;
		constraintsUnitOfMeasureLabel.anchor = java.awt.GridBagConstraints.NORTHWEST;
		constraintsUnitOfMeasureLabel.insets = new java.awt.Insets(33, 38, 10, 5);
		add(getUnitOfMeasureLabel(), constraintsUnitOfMeasureLabel);

		java.awt.GridBagConstraints constraintsMultiplierLabel = new java.awt.GridBagConstraints();
		constraintsMultiplierLabel.gridx = 1; constraintsMultiplierLabel.gridy = 2;
		constraintsMultiplierLabel.anchor = java.awt.GridBagConstraints.NORTHWEST;
		constraintsMultiplierLabel.ipadx = 46;
		constraintsMultiplierLabel.insets = new java.awt.Insets(7, 38, 7, 5);
		add(getMultiplierLabel(), constraintsMultiplierLabel);

		java.awt.GridBagConstraints constraintsDataOffsetLabel = new java.awt.GridBagConstraints();
		constraintsDataOffsetLabel.gridx = 1; constraintsDataOffsetLabel.gridy = 3;
		constraintsDataOffsetLabel.anchor = java.awt.GridBagConstraints.NORTHWEST;
		constraintsDataOffsetLabel.ipadx = 30;
		constraintsDataOffsetLabel.insets = new java.awt.Insets(7, 38, 4, 5);
		add(getDataOffsetLabel(), constraintsDataOffsetLabel);

		java.awt.GridBagConstraints constraintsUnitOfMeasureComboBox = new java.awt.GridBagConstraints();
		constraintsUnitOfMeasureComboBox.gridx = 2; constraintsUnitOfMeasureComboBox.gridy = 1;
		constraintsUnitOfMeasureComboBox.fill = java.awt.GridBagConstraints.HORIZONTAL;
		constraintsUnitOfMeasureComboBox.anchor = java.awt.GridBagConstraints.NORTHWEST;
		constraintsUnitOfMeasureComboBox.weightx = 1.0;
		constraintsUnitOfMeasureComboBox.ipadx = 21;
		constraintsUnitOfMeasureComboBox.insets = new java.awt.Insets(29, 5, 5, 136);
		add(getUnitOfMeasureComboBox(), constraintsUnitOfMeasureComboBox);

		java.awt.GridBagConstraints constraintsMultiplierTextField = new java.awt.GridBagConstraints();
		constraintsMultiplierTextField.gridx = 2; constraintsMultiplierTextField.gridy = 2;
		constraintsMultiplierTextField.fill = java.awt.GridBagConstraints.HORIZONTAL;
		constraintsMultiplierTextField.anchor = java.awt.GridBagConstraints.NORTHWEST;
		constraintsMultiplierTextField.weightx = 1.0;
		constraintsMultiplierTextField.ipadx = 143;
		constraintsMultiplierTextField.insets = new java.awt.Insets(5, 5, 5, 136);
		add(getMultiplierTextField(), constraintsMultiplierTextField);

		java.awt.GridBagConstraints constraintsDataOffsetTextField = new java.awt.GridBagConstraints();
		constraintsDataOffsetTextField.gridx = 2; constraintsDataOffsetTextField.gridy = 3;
		constraintsDataOffsetTextField.fill = java.awt.GridBagConstraints.HORIZONTAL;
		constraintsDataOffsetTextField.anchor = java.awt.GridBagConstraints.NORTHWEST;
		constraintsDataOffsetTextField.weightx = 1.0;
		constraintsDataOffsetTextField.ipadx = 143;
		constraintsDataOffsetTextField.insets = new java.awt.Insets(5, 5, 2, 136);
		add(getDataOffsetTextField(), constraintsDataOffsetTextField);

		java.awt.GridBagConstraints constraintsReadingPanel = new java.awt.GridBagConstraints();
		constraintsReadingPanel.gridx = 1; constraintsReadingPanel.gridy = 4;
		constraintsReadingPanel.gridwidth = 2;
		constraintsReadingPanel.fill = java.awt.GridBagConstraints.HORIZONTAL;
		constraintsReadingPanel.anchor = java.awt.GridBagConstraints.NORTHWEST;
		constraintsReadingPanel.weightx = 1.0;
		constraintsReadingPanel.weighty = 1.0;
		constraintsReadingPanel.ipadx = 120;
		constraintsReadingPanel.insets = new java.awt.Insets(3, 38, 30, 137);
		add(getReadingPanel(), constraintsReadingPanel);
	} catch (java.lang.Throwable ivjExc) {
		handleException(ivjExc);
	}
	// user code begin {2}
	getDialReadingRadioButton().setSelected(true);
	// user code end
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
		PointAccumulatorSettingsPanel aPointAccumulatorSettingsPanel;
		aPointAccumulatorSettingsPanel = new PointAccumulatorSettingsPanel();
		frame.add("Center", aPointAccumulatorSettingsPanel);
		frame.setSize(aPointAccumulatorSettingsPanel.getSize());
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
public void setValue(Object val) {
}
/**
 * 
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private static void getBuilderData() {
/*V1.1
**start of data**
	D0CB838494G88G88G56F854ACGGGGGGGGGGGG8CGGGE2F5E9ECE4E5F2A0E4E1F4E14DBB8DD494C71A1A10C4A30990514DA5668EDD13B33B5C0BFB175C391E59E5F96EDD5C10976F1EDE3C2C3B71F63D8BFBEBEECD626519DDDED24C40008A9111905DE8B2C6C94C05A09ACDBC568888226898D1D99D95159E2601061E6E3127C78684F35FD755555D03B3206E79DEF86F1B6A7A3E7A6A7B292F2A3E2AEEC8697B29F9B90ED2C2F272087177C58EA1ADFB8959B37572B40E9BA5E638086DEFB3
	40DAF2FFA8974AA420E527E6380A49E1CF815427C13D6ADB992EF760F99DB9669935839E624871826D9FFB6F794B61650CBB4D656453FA6B3EC2A85F82188EB8AC170BA8DF5F375E40E79AF88EB98EC21E6A40B66377EDB4F0FB205E8CB099E0C60755FEA515FCC7759E235E1A1CCA167F64A3C79176E36B13C3F1A7635DE514EE72D84E22CE14B5136A09BCB25427812071CE1251D783655E661E4EFD6DEA2032DDD094D55145762E4ADE29DF507C15C1D5D2746A6AE6AC6A5A5B430130AC682AF6D254F5C9698C
	B58B0AA893EF974AA0B58534CF108B75A594D71B0FF2A7437363G19G4C26AFBF5AEB686C3D729849FECEF12E09E353B6095952ACD9B64DF37EAEFF83757F3FF8514F09C01B81B087A08B2020BB43358D60DDBA6EBFBB7DBFD0567974FAB590D0153AE0F03F6C570E2AFED106676AEA20983893E23B2A7989E1E37B03FD95B174998E98DF668D73B11FCC7F0C0FF35A09C748223FB962480F6113C5532B9C355D4C975EEE4BA7CCF6DECDBC59116E68B80C16FD1A58E5AF3DD3F56645107D78020F9D09C6BF29B6
	59733AF12C9F27E31D8E4FB9C69CB07C8FA83E3447423318EC24F82CEF855A189E9E9B5E3738AE1E5C87C8718ECFCCBF5C1FD167D85A43F44815ADDDD8DC3CF09667D69560BDGFE00810049GB381B2E59E9B9FFA37DFE7ECB48B1A3EBFAC49FED1A30C7785EF9914E2C45704A6A5248BCA3BF8CA50A441A70BA1E2344972226E3EA83E17E84C36030C0322A2EA02AE298A74AE84C4DD54C22C4DEB5E3558C693A935C58A08A0A090A43C5FB73C6BF0AE8BA17D0FC13F200BD68BC6F71DC1DFDE96CEC10F5081BC
	B33F9436224F17027D39018CD7E9007BE174996C6F082841F42E2E7628EACFB858021690FFG47EAA31D5B5B6039960075D5F09EF153211EEE76B373CC092D1F86299D559734BEBEB7E665637AB9BF40477A3F3CFC2C9B0A9EA6958B8E479CEB842230B1DE2F8C0D79B934CF540B55BD795E9B1CFF57832833B54745DBA243E9144C3F17AF60D855023D2DG838A77712BE75CACE6778BA111C769B18D82CC74DB9EE7EDFFF59657D5713228688DE2D0D47C7090B2E2E4595915D67CF628E6EC3DC671C10877FA88
	3010094FBE0BB1A9683AA679423A4837G131EFB8EE505423E20B5D5382C5567F0BDD1FBA15E4D764567CA68BCD09411AAED329874929A2B22AC86C069FAB52C688C3F3E95473BC76CB3664A46D6970DF5B90C730E6DEBB30455E803A6854095B8F5498A856378339A3EAFA324G8E7767CCA897ABBCA6FB3770185C5ABF0524DFACF744DA8B6FFF3841D12B3218E8D12D18E4BA951D0F2D7BBABAE78CFC08693A6C68A6A8075FE1F9CCBCF6917B55C15E9800D415474478739BEC734E8806FAD51619EBCD1F8FDC
	A846B192AF05C5D82EF0029FB17DF87B0512A134D306CF99FD5C857429ECDB9B187E7997F14F0915C3F406254A30AE61CAA06890E44130CE4DE17942C3F4CD4179B4D3657942BF5421FD4B20DE904CF03903FCCC7E2908678BA9BD29E47137FF9FB7875A93E4E3519A344624F5AD4F97DEEFC31913011696643EF3DD342FFDF521105AAE51E557307B090B76B1E338D997D7C159A1E9A13D89A6D5047BA87DA26A0B6B3201B6560B29663E1CEE68B5BFE86D47D941EB77E636F712FA74C7F6106D1DEBCCFCB145EF
	3DE461591E1AC97D07F58F501E3A44576A4FFD98778DDA062B8DA0821084B083E01E467DF0286DB3B242DE5884632AA902FCB2EA1273BD294D496625D1F788F9FCDEC24976116F53B1DF8AB28BC230AFD35CD37B9037916AD5A6EEC2AD62DA20AE07783A6E2A47BDBD896AB3GB28132G8A758CD78500C7676D4AE8BBBF5493G26814C81D882109B4EF0ED8D73F67FDC970C91BF31GE571E714972FDD1251EE06D122FE9EDB7B8AD347172FDBF63E6DF975B06D785E11C72C3CEE0D0DDEE42E81B53E78FBC223
	AF761E6071455E93CE7856B2BC74E14F10B87D2CEF78BD43670B35E7B8A9EDDE8D6A9C54448ED103556C24540FF1B4AA0C7171AB9AC7076039850035FFE11F0B4E3DBEB52CC36EF7995B35186B7D9CE83788A097706BAF33B1E16B5777FA512F9D100001EAA703E2BBA96CC55E6F54F25E8834CF84C8834884D8FA194BFCEFBF2E19FFD4A47DD847D1D18805B551A378F041E6F4078B6DBB9A16F5A9A8CB2246080CA61DC29A2E0C47BABAC222EE271D7F646D217D422C74297B558861FD2F3A30AE2A6F969836C6
	C9147D86FDF2493A287E0752FF3895570A932260873B693A1CC86DBEDADFEA0B1B16DE1673C520E474FD3BE351671B747241D874AC13DE57EB27E79B742D91CE7F89315377C4983D4D249F4D09C55721E4FB7C7B8E7B9EBF462027C1198E30B002677A9392B17D7864BEB7140A586B6E96AE8B15C22FDE59G6934020973364FF6D7D538F7D6D5FD4C762154DE7BD9FC4F2AAFE26686889B7BD80CD599656C3B3698EB74C08DBF0BF781AD350F1F45F734613AC577D12EC8F575C1CD726F97BABDB42A593EB90746
	BE72AF6C637B66AB344F62FEB87782547673FDB372A15FB7539EF900F8C69D0939EFAEEE59697076B3BD877A2DFDF3891C33194CF935DC66CC20AF6BC75C9B3576F1C0DF7E95B4280412512B6A6CF4DC87BEA5F63AAF0A0E7ADEF15A69A9D1F47451F1031F5976F4B937AD7B6969C4FE62FF636784D70C1C40A81BCFEDA1ED6B3373195FDF6DD8EF77FB24B2548B93223A7AB01D104C7E04FA1E2B24DC613E9DEC3467A433576CF42C0EA3BF53103B740A655BAE5337615D3C6F0A018C9743358EF21CFF0047C727
	7CEEECBD4D150171C6FAB96FE41337FC5B46583C662240F8F3F2B86FFCD3178493D7B8487BFB7563B8BE3365F7279D8546D8B58C72B8DDFE8563F4G6AA9GE9035C174F1E334F2B6287B6BBF26278F24EA073613C4121E77E2E9E1417FB9572462BBC2779167489142704EEA31BB96017A03726698CA01BB5B5A8EAFA9F4352EC574857EE0BFB0FB25CBE3D16A45DA45F5C9B66E3F9E0C7ADBDA3DEE5F9E00E09D7A95EE3433335E7053976F801B67BAA4F8F7FCC4671F4FF1D615A8B5082A083A482CC7F1A7BF2
	31D4C5C6488FDB542087928B59168AB07B96545E5C1D580F72799CFBFC56D4B2734E57F3624D3159DF33F8D8E414D9ADDBA81E59D72120FD0523E0FD8470G7881A2GC92338FD5B3AAB18FD988EF53AAE34F7510C1F9BF7CA92FBADB4A68A4C363175F65B260DE272E79AE56628EB73C526539495573745C04F86A89D8DF9AC00E7B457C76B9EB19FBF9009190FB33D7AF75B7512C7B3BDC699E54A68EB75E2FC42201DEF3A51EE36D14E0B4BB758EB67DBE2EB37821E8BEF0B47674A31733907345BB524FE68B6
	1EC30F0D62EB9D52AEB8241EE07235BA6CFC7783BE8DA05DE8BFBF061EECCEF58D605C493A0D4D29BC939F2278EC9B1E4D355F52358E6B4537C3FEF2BB1FEB69187A9B796F2C6EF292E7FD0261BD8CCB6BB1DFB71C61DB04CE13A75003BA19B9B5DB368C1A5E03B1A5C15EA8893254CF0F31A721215249EC17EB096977306D790D1ADBF11F95BD4E6F545ECA994C7E29F27C334D23F26C334DE3F26C334D937270E717A7657877DDF3E47BFD979B77E7AE6158AC04B8D88E30BE8166BBC0ED821F6FA7829F90BA5F
	E32CBD877C1DF0G20374CBA173FDB09EF6BDEE5643B69838A5EE178D4DDD78358BF479FD1F03CF4B5C8118C572C200FB42933CBE7CD8D7CA98577DCD96C20E8F6F6CF2539D2AB58A5A770737C0663089B8575A493F775B8BF63CFB6FD70289A5F2699EA6C717BDBB576784DD607BF1B4ED1630DAF53AFF9B737E32929730EF2EE873903636EAB67F6941A3848A067EDB5F1939BB8EF50443D5640F987CCDCCAAF674DB4F1BB292F96C3BD5B448D1A7EAB3E1363B651DC2D8D6AC19377291737CBB0F12F90444D07
	FA1609CBB5F933CD5C6DE7CC99F7F15C5C9C4E5BE662C2B4575321BE4644CD75A3AE8D6A69A6EEF3876216C0FD39094B7D0A4BA8984357E8E5GF1DE288F1838BE0ACB05FA3A09A383FF6A1A027D5B6BDEDB1D495870A74B489BD2F75A1ED9ACAE3894BF6E97DD9AF9AE7F7BA5FAC789F33025AFA8B25CFFDE427B90151E7DF93B5FDCAAB6F1CD21E6B5A4618E414E41FFD70F776A9D2A2233BB16E768BCDE8C7ED8BA065FF7D49D47B6BAA8434E51F760F4A1FB475278BE5EB00C8F1CC3E0A370A0607E077BE655
	D8ABD7E572E7517917G7824317C5CBEDD3764BDDABE123C3CE16425437379A8F9D3681C4DC4D9263C05E1CBDE51011164658FA32F606E8C5717D1721E24733C94706E3B39FF1B0EE39C840408948887CE283D745501515EC767BB1EF11B21BD53A9E5D81F5B6DC7E83B5B5289E9BA54BFB1F4E272F765C44740E1BA57C7C13BC4D3E603F7A419F63969C3E44E077A7BB9F619AB69FB3CF6D58E871410E12BBB075FB7AC06768C47561DE5D04FB6F5F9604CC83A584724EADC34AE8D63ACDD180CD7CC394D637818
	5C6D40F964D3B53AF930FB45A9669DD39B340BG4CG18B30E5DAFB33E6F7B2F3D0F1C6767FC0B46317B4865D096A45A7976F4C40F41D7F44DF4C31B0AC4EE773BDF0D940BABED63508AFC41C44B6E84F8FEB9EA5C7BBAF10DE9002451388FBC813F6A7EB064868AEF637B1C3EC3908302623F36953B1B7B6E007D4EA64930EBF6A23F3B3BB8980BBEAF114F37AF2F90D3272263188FC55D4BB502D336FD6087B5554E8FAA5CD5956E9D3BBEA664FD5A1E50BD0D7F317E6481C278D9725F8A79D9F263F79FA48D4F
	3D1E330544BECBAEC9B43E2339473AE3587AFAF6BE6B3365EBCB4772726B551171765AF4BB12E775B33661467AD1DCD6BF8B8A2CFEBE3CEE1F9963FE9C6D7F1C447261329A563FF315A5ABD4E849F2FB4BE34B8A89CAA8A4EAD207CD5CB07DCFAA327A7CA2076E85263C66E20BF6794C7F0FBCE7095567CA8717474EECF39D7C1B949946FE5ABD7C3E6B45CD5867706FB8DBA4DD96779B4B04F1973756D9E04C39C57770BB305F34536FE16E05798C50GE05C4D78795DCCAB60D26E6577EF35A2DF13B26F65FDA8
	A2364D9D8F6B99402E71FC7EF417DA362DB8FABD3130B3667CD9D6935FFF77602B89E3EDA95B7F9E09F98F9835FEB0BBFEB1406F91BD6339ED13681E0BF7BA8963797978CA992EED3351A600EC0052248C57B600DA009600AE0081009400B400F40079GD9GABG8A13E1CFCD66E70A6F8D6058453B83B4AE586961CA93A4BCDE99F95F330E116E2CF6A5C7670E0C6FE7A3723DB724EE3C37F4A2DFF37235778F31408F6D587DC3BB0D1BC87250BBC019629312075E817EF7BB0F33A420ADCF6639749B7E1B3B3B
	AC94EF0E6F408D73B13BFEC1E35FFB5FD0FBFFCD717E7B065A2B0B5C5EA050665F476DFDF140AEFF4F8466F70FD136CD08F7DF75E0547D18EE349BE314130DF28614F30CE7639B0D3278B97E243211F37C3FA80BFDDEFF386C5A737AF74AE21F57E714594F6B46BDC7994E65CE1887DE335DAC93F746447D107609385312DF6FB271BF26FAA17E100882B97EE92AD7AF45F98D5CBC9B6E8CBF7B1461E686A6F7299A67FD3E0C66C012AC337A4FE99BC901E30B690FDFD29D2520E053BB4B44215E78DD0D13242FAB
	223C322408756C2BBB424F781F5275F9A10C5332897CBC5A5BCE5FA324E438562770D8117C449CFF4D767C3B76DBFFEFD66A3F75B232C44B2617ED4F7D622D17BD61164AE0E37A865DFB3642F86ECA6163DCF110FEC3GF52F09CBBE486FA27CA66E2F87B96F63A6EE4AB94E7B1409F334F25E79A66EAE97651D887B7CC40EFB1E623CD09F1848F37E86A74EAD9DB3885C2389BF0F345278CC02F6134D3677366771360467C927B1B5B7767D3787234F8F1F30F3AD70AFB47B28BF98FD0E6F472D57B867CEBFF8A3
	677802C9F0FE814024G57580D13188C6BCC75D3AAFF9B60F7CD62F94AC33F3764EDE83D11B36CD0F9FEF8FE7D4DB75F344EB2FF6E423C859693299FFCA048F532542960ED2A214FCC974F49021338BFDEF615C57160EBD1FEBB426DA84C676FB3A71BF67CFA036577075B70FF84E264DBF530104B10EE6D6039A9637DE833456BBEF873399A6F6F70875673A3DB2D3EA71E3B5E3E996F5CF20B77D9974A3325F615A77117DFC507AABD879ADB0C36DB725D4337BD51F470D08B7BDF0C9755F4F0E45ACBA4AF2411
	12EB3F3B283B77DF1FCB3B58D06AA00EA6626A227BC593496B450DA7026FCF1E02513F2013927CC446FD2C43EDFCA4D36DF66B24487AA606565F42EFE05C6CA398C0004422A8CF7CF84A6E7F487C79291013381A48861CB8A7C50D3EBF917D48EAFCC45DC8D6527F06408F111AC8EE0896F9A13DCF96BD24346384FEB89952C5FF9D3FD18E6964ED8FCA5A0E87046D0704D0170EBB7A9FBCA4D7414F3DC989A34B0252391D3D26798335AE272E0D8EACCEG827A2E237AE2AF15E6AF6B7EFECD796643CBE6BA0923
	112C12D5415FA834C3E25BC4D66ADD1A986AD2E5FFA4D8D3CBBBAA6865F936933AFB57B49F0D2D44E0B7156E09BC14D814D554680014D2A35BE9CBDCA7AA4D967E7AAEC183D122562463676B2A3655240DFECF7F7019036F34BAC80EE164A5F21564C702BAF1F58881C96E038796F7A1242C56C1DB8D4EEF9D2A96G4D5CC3B463A4D3C0650439D5939F7D6DD9AA40E255497B4C15403A3DD3D4835B6BBABAE03FB7BBG3FC2771B4C6E31CD6510B6A51164378FAFF85387F150C01C012AAA7A3B037E6E445F9D94
	3303E2F66C8467AEA3437E8D25C7384DB4F8C01FB5D49F9C7E4AC698C17D791766CE7D695EE5CB413AC612C7BF1D04C1A836DAFD958E78C46D4E34091BD627AC9E07E39444D009E03FEBA2E60FE8E193CD1696DF7E2C5A794BEFBB9DA497A692CB9EA1F8B183046055583CF2C272C8638E93398F0D45F1B3D5B66F1AE8C46C4AEB438A51C56660E22D18B6AD213FEF5F302B106B9D4A3B4134504D9C081F32645BB44D7F66E97A1125E96EB7DB53CACBD3FEF5BB86E053CDE93A523C0A39B54A6E0E2DEC5EB7D259
	3D16320EB84AB2558A2FC3D9D743AD0801DA23DEEFE90AD793982DE3D136CE8A0D0BDEB73ED602F58B35C0E568A5F0CC2F6744B1A4B75A903E923A58D910A904BC37E6AC0EC63EF9DEBFE5F95DE56863B0F57AC6E9FABEDAD327D19A776A9FFFED7FF6EF459D6B63FE93DBBB4578FF19A9C33F5D9B2F53F38E60532670FB3B97C2783DE83CEFE3CE8684D906CC3D27CE7163F7B27B7BF451FA771F22E37FA15E46A0B25A03BA6A5DED6379BFD0CB8788B41D99FA5294GGFCBCGGD0CB818294G94G88G88G
	56F854ACB41D99FA5294GGFCBCGG8CGGGGGGGGGGGGGGGGGE2F5E9ECE4E5F2A0E4E1F4E1D0CB8586GGGG81G81GBAGGG8C95GGGG
**end of data**/
}
}
