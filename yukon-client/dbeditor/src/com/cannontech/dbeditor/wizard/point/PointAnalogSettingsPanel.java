package com.cannontech.dbeditor.wizard.point;

/**
 * This type was created in VisualAge.
 */

public class PointAnalogSettingsPanel extends com.cannontech.common.gui.util.DataInputPanel {
	private javax.swing.JLabel ivjUnitOfMeasureLabel = null;
	private javax.swing.JComboBox ivjUnitOfMeasureComboBox = null;
	private com.klg.jclass.field.JCSpinField ivjJCSpinFieldDecimalPlaces = null;
	private javax.swing.JLabel ivjJLabelDecimalPlaces = null;
	private javax.swing.JTextField ivjMultiplierTextField = null;
	private javax.swing.JLabel ivjMultiplierLabel = null;
	private javax.swing.JLabel ivjDataOffsetLabel = null;
	private javax.swing.JTextField ivjDataOffsetTextField = null;
public PointAnalogSettingsPanel() {
	super();
	initialize();
}
/**
 * Return the JLabel1 property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getDataOffsetLabel() {
	if (ivjDataOffsetLabel == null) {
		try {
			ivjDataOffsetLabel = new javax.swing.JLabel();
			ivjDataOffsetLabel.setName("DataOffsetLabel");
			ivjDataOffsetLabel.setFont(new java.awt.Font("dialog", 0, 14));
			ivjDataOffsetLabel.setText("Data Offset:");
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
 * Return the JTextField1 property value.
 * @return javax.swing.JTextField
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JTextField getDataOffsetTextField() {
	if (ivjDataOffsetTextField == null) {
		try {
			ivjDataOffsetTextField = new javax.swing.JTextField();
			ivjDataOffsetTextField.setName("DataOffsetTextField");
			ivjDataOffsetTextField.setFont(new java.awt.Font("sansserif", 0, 14));
			ivjDataOffsetTextField.setMinimumSize(new java.awt.Dimension(4, 23));
			ivjDataOffsetTextField.setColumns(0);
			ivjDataOffsetTextField.setDocument( new com.cannontech.common.gui.unchanging.DoubleRangeDocument(000000.000000000, 999999.999999999, 10) );
			// user code begin {1}
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
 * Return the JCSpinFieldDecimalPlaces property value.
 * @return com.klg.jclass.field.JCSpinField
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private com.klg.jclass.field.JCSpinField getJCSpinFieldDecimalPlaces() {
	if (ivjJCSpinFieldDecimalPlaces == null) {
		try {
			ivjJCSpinFieldDecimalPlaces = new com.klg.jclass.field.JCSpinField();
			ivjJCSpinFieldDecimalPlaces.setName("JCSpinFieldDecimalPlaces");
			ivjJCSpinFieldDecimalPlaces.setPreferredSize(new java.awt.Dimension(15, 7));
			// user code begin {1}
			
			ivjJCSpinFieldDecimalPlaces.setDataProperties(new com.klg.jclass.field.DataProperties(
				new com.klg.jclass.field.validate.JCIntegerValidator(
					null, new Integer(0), new Integer(10), null, true, 
					null, new Integer(1), "#,##0.###;-#,##0.###", false, false, 
					false, null, new Integer(0)), new com.klg.jclass.util.value.MutableValueModel(
						java.lang.Integer.class, new Integer(0)), new com.klg.jclass.field.JCInvalidInfo(
							true, 2, new java.awt.Color(0, 0, 0, 255), new java.awt.Color(255, 255, 255, 255))));


			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJCSpinFieldDecimalPlaces;
}
/**
 * Return the JLabelDecimalPlaces property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getJLabelDecimalPlaces() {
	if (ivjJLabelDecimalPlaces == null) {
		try {
			ivjJLabelDecimalPlaces = new javax.swing.JLabel();
			ivjJLabelDecimalPlaces.setName("JLabelDecimalPlaces");
			ivjJLabelDecimalPlaces.setFont(new java.awt.Font("dialog", 0, 14));
			ivjJLabelDecimalPlaces.setText("Decimal Places:");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJLabelDecimalPlaces;
}
/**
 * Return the MultiplierTextField property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getMultiplierLabel() {
	if (ivjMultiplierLabel == null) {
		try {
			ivjMultiplierLabel = new javax.swing.JLabel();
			ivjMultiplierLabel.setName("MultiplierLabel");
			ivjMultiplierLabel.setFont(new java.awt.Font("dialog", 0, 14));
			ivjMultiplierLabel.setAlignmentX(java.awt.Component.LEFT_ALIGNMENT);
			ivjMultiplierLabel.setText("Multiplier:");
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
 * Return the JTextField1 property value.
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
 * Return the JComboBox1 property value.
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
	//Assuming commonObject is AnalogPoint (real or not)
	Double multiplier = null;
	Double dataOffset = null;

	try
	{
		multiplier = new Double(getMultiplierTextField().getText());
		dataOffset = new Double(getDataOffsetTextField().getText());
	}
	catch (NumberFormatException n)
	{
		com.cannontech.clientutils.CTILogger.error( n.getMessage(), n );
		multiplier = new Double(1.0);
		dataOffset = new Double(0.0);
	}

	com.cannontech.database.data.point.AnalogPoint point = (com.cannontech.database.data.point.AnalogPoint) val;

	int uOfMeasureID =
		((com.cannontech.database.data.lite.LiteUnitMeasure) getUnitOfMeasureComboBox().getSelectedItem()).getUomID();

	point.getPointUnit().setUomID( new Integer(uOfMeasureID) );
	point.getPoint().setStateGroupID(new Integer(com.cannontech.database.db.state.StateGroupUtils.STATEGROUP_ANALOG));

	point.getPointUnit().setDecimalPlaces(new Integer(((Number) getJCSpinFieldDecimalPlaces().getValue()).intValue()));
	point.getPointAnalog().setMultiplier(multiplier);
	point.getPointAnalog().setDataOffset(dataOffset);
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
 * Initialize the class.
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void initialize() {
	try {
		// user code begin {1}
		// user code end
		setName("PointAnalogSettingsPanel");
		setPreferredSize(new java.awt.Dimension(350, 200));
		setLayout(new java.awt.GridBagLayout());
		setSize(350, 250);

		java.awt.GridBagConstraints constraintsUnitOfMeasureLabel = new java.awt.GridBagConstraints();
		constraintsUnitOfMeasureLabel.gridx = 1; constraintsUnitOfMeasureLabel.gridy = 1;
		constraintsUnitOfMeasureLabel.anchor = java.awt.GridBagConstraints.WEST;
		constraintsUnitOfMeasureLabel.insets = new java.awt.Insets(91, 10, 7, 0);
		add(getUnitOfMeasureLabel(), constraintsUnitOfMeasureLabel);

		java.awt.GridBagConstraints constraintsUnitOfMeasureComboBox = new java.awt.GridBagConstraints();
		constraintsUnitOfMeasureComboBox.gridx = 2; constraintsUnitOfMeasureComboBox.gridy = 1;
		constraintsUnitOfMeasureComboBox.fill = java.awt.GridBagConstraints.HORIZONTAL;
		constraintsUnitOfMeasureComboBox.anchor = java.awt.GridBagConstraints.WEST;
		constraintsUnitOfMeasureComboBox.weightx = 1.0;
		constraintsUnitOfMeasureComboBox.ipadx = 81;
		constraintsUnitOfMeasureComboBox.insets = new java.awt.Insets(84, 3, 5, 16);
		add(getUnitOfMeasureComboBox(), constraintsUnitOfMeasureComboBox);

		java.awt.GridBagConstraints constraintsJLabelDecimalPlaces = new java.awt.GridBagConstraints();
		constraintsJLabelDecimalPlaces.gridx = 1; constraintsJLabelDecimalPlaces.gridy = 2;
		constraintsJLabelDecimalPlaces.anchor = java.awt.GridBagConstraints.WEST;
		constraintsJLabelDecimalPlaces.ipadx = 14;
		constraintsJLabelDecimalPlaces.insets = new java.awt.Insets(5, 10, 5, 0);
		add(getJLabelDecimalPlaces(), constraintsJLabelDecimalPlaces);

		java.awt.GridBagConstraints constraintsJCSpinFieldDecimalPlaces = new java.awt.GridBagConstraints();
		constraintsJCSpinFieldDecimalPlaces.gridx = 2; constraintsJCSpinFieldDecimalPlaces.gridy = 2;
		constraintsJCSpinFieldDecimalPlaces.anchor = java.awt.GridBagConstraints.WEST;
		constraintsJCSpinFieldDecimalPlaces.ipadx = 55;
		constraintsJCSpinFieldDecimalPlaces.ipady = 15;
		constraintsJCSpinFieldDecimalPlaces.insets = new java.awt.Insets(6, 1, 7, 169);
		add(getJCSpinFieldDecimalPlaces(), constraintsJCSpinFieldDecimalPlaces);

		java.awt.GridBagConstraints constraintsMultiplierLabel = new java.awt.GridBagConstraints();
		constraintsMultiplierLabel.gridx = 1; constraintsMultiplierLabel.gridy = 3;
		constraintsMultiplierLabel.anchor = java.awt.GridBagConstraints.WEST;
		constraintsMultiplierLabel.ipadx = 51;
		constraintsMultiplierLabel.insets = new java.awt.Insets(5, 10, 9, 3);
		add(getMultiplierLabel(), constraintsMultiplierLabel);

		java.awt.GridBagConstraints constraintsMultiplierTextField = new java.awt.GridBagConstraints();
		constraintsMultiplierTextField.gridx = 2; constraintsMultiplierTextField.gridy = 3;
		constraintsMultiplierTextField.fill = java.awt.GridBagConstraints.HORIZONTAL;
		constraintsMultiplierTextField.anchor = java.awt.GridBagConstraints.WEST;
		constraintsMultiplierTextField.weightx = 1.0;
		constraintsMultiplierTextField.ipadx = 130;
		constraintsMultiplierTextField.insets = new java.awt.Insets(5, 1, 5, 91);
		add(getMultiplierTextField(), constraintsMultiplierTextField);

		java.awt.GridBagConstraints constraintsDataOffsetLabel = new java.awt.GridBagConstraints();
		constraintsDataOffsetLabel.gridx = 1; constraintsDataOffsetLabel.gridy = 4;
		constraintsDataOffsetLabel.anchor = java.awt.GridBagConstraints.WEST;
		constraintsDataOffsetLabel.ipadx = 35;
		constraintsDataOffsetLabel.insets = new java.awt.Insets(5, 10, 47, 3);
		add(getDataOffsetLabel(), constraintsDataOffsetLabel);

		java.awt.GridBagConstraints constraintsDataOffsetTextField = new java.awt.GridBagConstraints();
		constraintsDataOffsetTextField.gridx = 2; constraintsDataOffsetTextField.gridy = 4;
		constraintsDataOffsetTextField.fill = java.awt.GridBagConstraints.HORIZONTAL;
		constraintsDataOffsetTextField.anchor = java.awt.GridBagConstraints.WEST;
		constraintsDataOffsetTextField.weightx = 1.0;
		constraintsDataOffsetTextField.ipadx = 130;
		constraintsDataOffsetTextField.insets = new java.awt.Insets(5, 1, 43, 91);
		add(getDataOffsetTextField(), constraintsDataOffsetTextField);
	} catch (java.lang.Throwable ivjExc) {
		handleException(ivjExc);
	}
	// user code begin {2}

	//Add units of measure to the Unit of Measure combo box
	com.cannontech.database.cache.DefaultDatabaseCache cache =
		com.cannontech.database.cache.DefaultDatabaseCache.getInstance();
	synchronized (cache)
   {
		java.util.List allUnitMeasures = cache.getAllUnitMeasures();
		for (int i = 0; i < allUnitMeasures.size(); i++)
      {
			getUnitOfMeasureComboBox().addItem(allUnitMeasures.get(i));
         
         if( ((com.cannontech.database.data.lite.LiteUnitMeasure)allUnitMeasures.get(i)).getUomID()
              == com.cannontech.database.data.point.PointUnits.UOMID_KW )
         {
            getUnitOfMeasureComboBox().setSelectedIndex( i ); //default to KW
         }
      }
           
	}
	getMultiplierTextField().setText("1.0");
	getDataOffsetTextField().setText("0.0");
	getJCSpinFieldDecimalPlaces().getDataProperties().getValueModel().setValue(new Integer (com.cannontech.dbeditor.DatabaseEditor.getDecimalPlaces()));
	
	// user code end
}
/**
 * main entrypoint - starts the part when it is run as an application
 * @param args java.lang.String[]
 */
public static void main(java.lang.String[] args) {
	try {
		java.awt.Frame frame = new java.awt.Frame();
		PointAnalogSettingsPanel aPointAnalogSettingsPanel;
		aPointAnalogSettingsPanel = new PointAnalogSettingsPanel();
		frame.add("Center", aPointAnalogSettingsPanel);
		frame.setSize(aPointAnalogSettingsPanel.getSize());
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
}
/**
 * 
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private static void getBuilderData() {
/*V1.1
**start of data**
	D0CB838494G88G88GE1F954ACGGGGGGGGGGGG8CGGGE2F5E9ECE4E5F2A0E4E1F4E14DBA8BD0D4D71644DFB092AB2918891BF5D3E4E2D2E6065DE4AAEE2D59F1D2994A2DF192D2F1E7CDE2B24C4619E1A7568C336B4EE4EBA609D5FBE92485C56527A01004A888BA58B4880A48A42208002D3202060C6E4803FE8D8F3A7B352FDFCB0328FB4E3D6F3E773A690668A6D5C355613DFB4E39671CFB6E39671EFBDF13420F93B253AD050424E7926D6FC8BAA176FAC2329E729D673898B149CACCFF
	1FG64114F33B7417381202D7DC612B5179C9D4F027692E827ADCC329641FB8179F43CE5BF3CC451D38834F71AEFFB2657B3124B75EC266DFF316740F3BB40D2406173DD91757F4ADE2C6113B5BC07F48BA1ABDD48E32DAB53F0CD506E860887C8F4997C59D4735C66A62DFD654184726ECBD516EDA847A413C34E906BEB571E6564197435C3282B36AEC76BB38C6D84G9CFC86997D22991EAD5D23C31F8E4A9E5B2060754ADED59C9C360DC99302623479E4492BB6B7F7632355AB3865219ED1D5A56F103FDB70
	0AEEC2FA07D17ED8DD9615C7C8993457D15C22A06A0C0777E781D68130717C312AC533376F46B3E44E9B5E0C9DD146D3A131F1B8A4E3BC0B826FEF5ECDFD7F2DBC74F1AC509281DE84C88558B412E42DGA825F37E1D5C8370EC9DD0CF4A9E0F6CED7579CE381D4AD959A93A613D3999A89A6E02B8A8ABCEC2583CB65655C430E7BAE07DCE5EF1BF66132507789CA7DEF80A24BCF74632B902CFD2165AADADA34C973DA306CF186E49283A551150988C557DB9B16BD66749991991F4AFFBED3FA5D613335824BBF9
	8467BAEB9C67FA853C27EBF1407079941FBBEA60D9BCBECAE3835B65C01BB34AE323EFBB37453169D112DB670868077824D64B7AD1E683F19B363038F82EG57D58D60BBG7A8102G71G4BGD639F9EC3C1FDB7BA5E323DBD0549381496D9495427ABE17378B1EE2D0D504CE2FDF953C0362A5C11104813768A79A4F0ABC34FDA0245F77E94C8E020E5322D7D484D5123DA0DD70082A2878994FCBF9F9482308145AABF9C4D0607191AE77C7F985B8EC412FDE74B985D5B4B8987D2DBC7465F56192C88481704E
	7C322E90FD3E9E46CFBCB0879E6E070B79A86F0C2840FAEEEEF64872E840570BA3A17D48C79E22EB3B825EDBG182C141DB43FC2FB05AEA7AEBF5FA427CD9AC47385E51C2F0D6D6318BBD7FB78DC7FFB9F1F6B36EDCF121A573AA24EB54D299EB647B95E7018AF26B251AE56CE88760575DD6D099C7BF783CC4773D3F4E4FCC572376BF355D504F355G636D8670F9390FFF10DF4EE276046097F9141ED7A040C42761F1467B3D824C2B62F5512B3609BE516B04973F96A3AB8B320D78F549FAEC3DC471BE087713
	90E0FE9D3F3AGE3D2D0D5C59A8828A25F83F47ADA2A4B9F987099CB056BFA3BG7309BC86712E7327966453F560750A54E83D0BC67F850DD551ADFA406813F2402BB2FCDAA14E7728B82E2D158D05D69A6B6EG2EBB3627BDABEC45B1A81287DC01CB1724F9B10E3FD54777E404D840615E7CA2BC57F8F9CC0E6D65B1D9B171B8F971EA25A5D2AE0CFF324D52A033386A100D18E4B67DF4E7E45B5F21EBC6437B192DBF3908B6C55BDDD9AC4CAEC6F94320E792E0114CE3E1F057EE53FA530260246CF6B3176A3E
	6EAE4A4539912F85C4C8D338304F687E7B3CA8BF0CF6C973A523DFADC2DF3ACDB901593D79AA6EB5116A062100E48B2892E6GC105607285D4BA96D6A79456E7E96BA8D166F542E1DA1B24C2FBA3C0210F4F45BFEF63F542225184326E091228F5CF0D0F4DC1374F180B72593CCEF8E5B76AFCGE889BE6E3B370A4CB92F556F1787A51AEE35F14FAFB64F9543ADAC5E82CF17247855CED8CCC16E23C545E8AF66E38D2D65095D75FCBFDE2659354AE76422945F543C44764C49FA7447869F5BB373F47C7D94DFF6
	4D40333D34057A8F5B9DC0DBF30D6768AD2598DB7D8A448D40A200C400E400B4057B21EC77A1B2439E5889732AC0EC7604ACEE3E976D4EE06BD1EB3F3EBB132FC754CD778FBB1D73ACBF54E5G8C57E4C3DC93349DBAAE430EB89F34E37DBC1F4F2A47FC17886DE4008DG39EA123581209B40879027F25E0798AF34138152GF282C9569AG87009BA0AE40F9CF5445E03CE8B2387FD1BEFF67BAF9BBCE63EBD06FEEBF490D500F59A2D2DBB036F704514C7BCB26291D27BD375E059D885991F0EC7E8ACA236F99
	A52511770C725248FB463E52BC068799668A0A53ED25536FA907CBA36DA99914F63A9E7D65D3C41728C0566B11A6B05ED28398C77DB45E9261FDB9G63BFEF3352B5BAAE87D448A75711EF250D6F875550BE8EB08CB0F91D4D934BF35F99C39F3B20C08353FAFC62A0710DE15F927DFC17827C9BD09E606DG5DE3DC678BB7B037DE74CA6AF957D9D170879451A18C601909519F1B5C99CE07D5B8A01F100304737C319873D4976D57A68E4216666EF68B03A277434FAB500E2E13BDBE495BAE09EEE788936379E0
	947DF8B660D6A51FDB9295E6842355CD960450FAE1FA299C0D7E3F9FE1DF4C6467DDAE3F281A7B3EF23BA004E6745DC0FD74F2FD21A93E160F317AB1851E0C9E3F53CCEF8AB2FAFF1053338BA375D7F57AFF56186931DA7F95BA7D6061C87DD76B74D90DE6FA2A56BFE71C533F5B9829FF39CEFF70C8247E8DC06F98473B03FDFB08BE0F87ED6570740AE365A342F541A60C2936B6A85B3DD820D79FEA345714575B6D7B593E17B0E6BE6FB7EDB992318621B9E91C45EA0276DCF16FDEEDCFC83371737EDA2065CE
	7073FEDDBF1E0D683E4D8DE9EEBE2DC84E934210032E8E36CFB754E6E97DBBA678BEBDD00BB2E3209D8F706C845F27037B78BE1D7854234491F3A662BE3D2E375E32EA025919B6E16C53CB61BC4FF49E37F31D55B7A08EEEA04EEBB77B98FD79DF40E003A723A70450B11FB492B37DC7A1F43477FB96B3FDD31D190EBEBA2975E7E3FB39120FED634BCB097B6F0FC62DC1165EE0E3CA561E5D1776120A7BBE584C7C7EDBD731596FC11BFF8C92CAF3B3DB59EC7C16C33CB65AB449FDFB335DDC832D58D1EF599AC5
	FF4BA453FBE55270ED8D7816699FF217C6562FE79CE6432BBB398D0BC18E43BD256356CC72B83AF5F89FB155FF236EA15B48A0E6FF1B8BB3G8A56D3923FA32ABE182579B867A6176DBC1C25793A6DA617DDD9DD9659D6BD41303E4FB7F23BA6F5F93F51F14BF45C2AA3DCC75ACD6E57D7AF1B57D86E231FD8522378356216F627F6AB7C1E21258265BA819FF30B57D323FB7EG4FCB4208D6C91DF2CA2A2C50D28A105D0A6C9395F51CE1E9252D55G3F315D5DFD93293D3BFEC76F389F2BC1DF27B9A061962BC1
	53F57C5B941FE84233BCB4DC4B7350F220155D663529279257C38C349783AC83C886C883480A613EDC3647CEE628CDFBE51F830A9537293CE063BBE53F3BFB386FEF666BED59BFFE13AC1F77DEFA3475D696436221C5FB2E6F5DCB71ECFC248A476786DAACC0A240CA0035G9B7471ED5CD343460761502A2A4260B0BDED7041DD1244B1830D45879B5B66C36631ED3A0769AF561EB577CC2DB319CDA355186B0E837D8A008A908710F88F3727FD6F0CE701D34108E781E6571A1DE63B12CDF62C0377F7A358457A
	FDBB24DF56AC46D726BD6D332275BBF81077EB0B405397351FF4582C4F8DFCB1G0BB47E25D17B3D50E86E37A20CEFF5D8BBF596BFEB7CAE245F6FB53E1C59DABE199DCD5FABC74C7D9A4278BA20FD4504734D66EBEBBEFE6550EA6805FBABC914FC8453598569757C4008B828768AC3FA1F568A14275765ACCDE9340E8A0CA189EAD6C9F0CB9374485C830C5EA1E6F38ADE29EA767F1B695DDB9BFEE776D58253F16F572A030D7FFAE57473514D4A4867235915114FC773AB27BF7FBCD8997DCEAD3E52FC274666
	7D9B87F0EE66C09CA4G2C84D88B10B61B2F6FFD34A045DB6D29396614F3888E9F74A6DB657A332B220FB537EA667B6F5DD5F8DFB2A02B2A6CC1799C7FF1954E17AA7BA81261AA2A50C70AB4B42CB2D68DFF208A77D8376822E8F6CF70912D133266A4D90B67703B0339DD082B01F60B0E7BE5973FCF681843FD20D4C79F53F8F564793BDD9DF97E66D6CFFF3E3D3FBA5A7CB27BFEC56B2AF830ED29EEF3CABF62D6C0FB2D0E2B9278B852F4DC926D5BB2B7496A184BF147A44475C35B276392A45EF7D2473551BA
	AC995A6BF55C45B14E37D14745505A4CB18F724CBC0E7BD9916503F62C0EEB20F7E50B211D28637E2A184BDB2E63DE2335D9563DB0EF77F25CD11D2FC64775D33EC9E8BF206336D3DB16C0FB190E3BD80238D550DE2B633C7DDCDE2A0ECB2575DFC3EC1235AD1663AE17F03EBE9DF70972AD05760BBA6EE890F1EB203DBE164750E3872247501387E6DE97FFF7005EADC2BC770E7BC46DFB52813C1F1069B91A73ADBF102B63BA7D5D32DF42EC4B4E130D07709E5CA5FBD5F667F1062E090DE0E356FCFE7F30388B
	F9D4B0061DC74F61F6C27A66A3FF7A1936E9F2DDC698B4C19F043D0455C20E79C61D47F4D9F6055AD33C8B6DC9G3EC45D1E5F76235FBCC2D07284BC9764B1FAB52E5D596450984733D58A70B39BBF952633B1B34C46B4130D74437EFDC956A7C2ECFCF3E7280D7FC157D08E70955F47ED3CA0057A6C5AC143E77FA04D64B333CD61BE6BG9D9F0658F379E074BBAC152E51DE6853FF9F7707FBECA6FFCC27BF8E64E0CE7BD8577F4861D0FF7CADDDE3714037C47747FBC51843E12B9F7262FE51476D4B236BF68D
	702D57F94F94077A6EF11B613BE70BE7725D66E9ECAFDE10E49D8A71DDDAE3286D96EAFB85705997F0FFF5CF2BB3B3CC27F3C1E87C84211D19EE56B956087C0332BB60717AB5BFACEC64F796F396F0BFD40679B57DA872AC82FA024E63AE8975D5365D70555C7E19FC15BD0D2FD6030E53A13E7A60C828BDF9CDE8CF8A702D57FD25144C64AB334E3238D0DF596342FD65B90267A68F940A3056E9E4B35DF3E90EED817E0EB86E0B6E60D43F8E8539DF55B8FE1F160FD7BB1A7C71AE2CE5C26E535AE10055FB1C92
	FEC55BD3D3EE2FA92FEB584F629D6FA9D19E7FE3729A8F93424FFDBF4D61673E32EF7FB5E9FB633DF46E4FF0DF4C09E3B5771AB863BEE07D48464DCC661EE3060D2B7B3F2C0D0CFF2F007A502829B6EF780C496FEEB16C7F5D27062EDA694B6AE2FDAB0F9BF2BADA8DB9DDE39F064BB16EA96B9AEB4A0F6C577B0D1EB0E47C204D1071FA517FE7EE180C6AE27C3D155E9FCE24BE598B894866B8555EFB355551F97A5C59D367FAF57D3FBCE558323C4310D3D3DC9959963F60757BC5C5F2194C19466FBF6EB4E4BE
	590873E457752DF098344E12BB1B07374F9AF69F6BB6641CAB79EA6C7E737F987263AE9872ED41E9663921263C7E50FE1D77D6100F171DB3A6FAF032F36ECF32D683B8G1CGC10087G16GAC87C886C8855888103BB049DA8150845081F08540371057D39307D1D734BBB36DF21A9ECE94C1426389BF5B76C42F439E6E19398EFB2CA77279E4C94F54734953BD114FA74F74184FA75A3DD58F6644A1303BCF67FBDE47FD2163DE20B291773964D407F57CCAEA9762BBC4D4287FB6035AB5C6F1FD9A6ED5936E8B
	8D372687EBC99872302C703EEF7650BDC0F23BD97BA714C772C2E9297B63E754C649A7186CFEC747215D6CF7149FD957015244DF13CC3FC2722746CF6B42B75FC83C5AD6E8A116CEE29D2636F5124CB19CE490FF46348AB26B1FD5120F1F3E4A4F3B4A350FDF4D6565AA5921FD40AAE7DF301AD5325578BA852D9C7C5ED44EBEB8B5037EEDA16B631F9EEF7CC5723FDE72E790EBA7590DDBDE8F2C953C29901D58D17BC9D4BB49263FEB440D2913EC7253C726DF9DF70B8ED2683A00BF07702B2233151F5D7CAA59
	69C0CD357853065A8E41BF2CE2AC9DF310CDDE7C719649E7E437609D2AE597A24768D853DB7B6902442D02003D8554DE14E2532594FCF7C765A7DD294BB308251DECF14B0233DD9804A550C93255E1C5748F4BEEE7507759F1AAA80B1E54F0F74DGF1257A70F130B63CA1570D9E8D7ECD5C36755B5AADD04CA82416F2E245E153B91CA7078585D409CA270ABFC613158A1DF64FC76A3ED7CE97DD3110F4ED105748AD12957429446A92BC12FB9CDED8D673A3A5C785EB9548CEAED97100E565E116F1122E407650
	8F6D8FBF7D7E1F2882232BCABEE62E042E35C3226C29EDF539A42F288BG3F02788FF57148E38B63498FBE38336B357F9E00B8E8A3999E3B1D7E2F237F6B71FF9D4554D1CCDDBDB8F7831976AF1C9E64E3264183766C207660745B5AE1865537FE7D43EF7E38E5C39A0C2E1DE4529FC440A4649ADC67821E81D11917787007DB972DDB00F36443D089225C5C20AE91C758C917693A6B071AB3FE7EC40605EC02E545529684AF669E88DE052D328CC8DBB46EB0ADEC510C3DCC0D35CE09C644EE199AD6085E2ECFAE
	3672A8EB817D3F6B0EDD053D0AE95F92FD041FB08762A6448FADF8586D7D4B3334BA54D26E53CC0AA51A27F768BC7CA70CCF83D86FEEB4EDDF63E86A0C51E4EB234950C7F32716E67740F243846B7AFA0CED8AF5BD3A7572DD9B7B353A75B343525A3FF837369B467EE10ADB718F3779CD6CDC347F3679BB696F6796C77DADC259A35A771AC7423F131E5BC2EBA44047BE225F21E56345CA346F92BDD0G38D541BD5A6AF562B70A93632AE85C3B76ECC179FE4E2391996DCFDB506E91D31F7F83D0CB8788878191
	D50F92GGD8B2GGD0CB818294G94G88G88GE1F954AC878191D50F92GGD8B2GG8CGGGGGGGGGGGGGGGGGE2F5E9ECE4E5F2A0E4E1F4E1D0CB8586GGGG81G81GBAGGG4992GGGG
**end of data**/
}
}
