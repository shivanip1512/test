package com.cannontech.dbeditor.editor.point;

/**
 * This type was created in VisualAge.
 */
import com.cannontech.database.data.point.PointTypes;
import com.cannontech.common.util.CtiUtilities;
import com.cannontech.common.gui.util.DataInputPanel;

public class PointStatusBasePanel extends com.cannontech.common.gui.util.DataInputPanel implements java.awt.event.ActionListener, java.awt.event.ItemListener 
{
	private javax.swing.JComboBox ivjInitialStateComboBox = null;
	private javax.swing.JLabel ivjInitialStateLabel = null;
	private javax.swing.JComboBox ivjStateTableComboBox = null;
	private javax.swing.JLabel ivjStateTableLabel = null;
	private java.util.List allStateGroups = null;
	private javax.swing.JCheckBox ivjArchiveCheckBox = null;
/**
 * Constructor
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
public PointStatusBasePanel() {
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
	if (e.getSource() == getStateTableComboBox()) 
		connEtoC1(e);
	if (e.getSource() == getInitialStateComboBox()) 
		connEtoC3(e);
	if (e.getSource() == getArchiveCheckBox()) 
		connEtoC6(e);
	// user code begin {2}
	// user code end
}
/**
 * connEtoC1:  (StateTableComboBox.action.actionPerformed(java.awt.event.ActionEvent) --> StatusBasePanel.fireInputUpdate()V)
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
 * connEtoC3:  (InitialStateComboBox.action.actionPerformed(java.awt.event.ActionEvent) --> StatusBasePanel.fireInputUpdate()V)
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
 * connEtoC5:  (StateTableComboBox.item.itemStateChanged(java.awt.event.ItemEvent) --> StatusBasePanel.stateTableComboBox_ItemStateChanged(Ljava.awt.event.ItemEvent;)V)
 * @param arg1 java.awt.event.ItemEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC5(java.awt.event.ItemEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.stateTableComboBox_ItemStateChanged(arg1);
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}
/**
 * connEtoC6:  (ArchiveCheckBox.action.actionPerformed(java.awt.event.ActionEvent) --> StatusBasePanel.fireInputUpdate()V)
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
 * Return the ArchiveCheckBox property value.
 * @return javax.swing.JCheckBox
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JCheckBox getArchiveCheckBox() {
	if (ivjArchiveCheckBox == null) {
		try {
			ivjArchiveCheckBox = new javax.swing.JCheckBox();
			ivjArchiveCheckBox.setName("ArchiveCheckBox");
			ivjArchiveCheckBox.setFont(new java.awt.Font("dialog", 0, 14));
			ivjArchiveCheckBox.setText("Archive Data");
			ivjArchiveCheckBox.setHorizontalTextPosition(javax.swing.SwingConstants.LEFT);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjArchiveCheckBox;
}
/**
 * Return the InitialStateComboBox property value.
 * @return javax.swing.JComboBox
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JComboBox getInitialStateComboBox() {
	if (ivjInitialStateComboBox == null) {
		try {
			ivjInitialStateComboBox = new javax.swing.JComboBox();
			ivjInitialStateComboBox.setName("InitialStateComboBox");
			ivjInitialStateComboBox.setPreferredSize(new java.awt.Dimension(75, 24));
			ivjInitialStateComboBox.setMinimumSize(new java.awt.Dimension(75, 24));
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjInitialStateComboBox;
}
/**
 * Return the InitialStateLabel property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getInitialStateLabel() {
	if (ivjInitialStateLabel == null) {
		try {
			ivjInitialStateLabel = new javax.swing.JLabel();
			ivjInitialStateLabel.setName("InitialStateLabel");
			ivjInitialStateLabel.setText("Initial State:");
			ivjInitialStateLabel.setMaximumSize(new java.awt.Dimension(73, 16));
			ivjInitialStateLabel.setPreferredSize(new java.awt.Dimension(73, 16));
			ivjInitialStateLabel.setFont(new java.awt.Font("dialog", 0, 14));
			ivjInitialStateLabel.setMinimumSize(new java.awt.Dimension(73, 16));
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjInitialStateLabel;
}
/**
 * Return the StateTableComboBox property value.
 * @return javax.swing.JComboBox
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JComboBox getStateTableComboBox() {
	if (ivjStateTableComboBox == null) {
		try {
			ivjStateTableComboBox = new javax.swing.JComboBox();
			ivjStateTableComboBox.setName("StateTableComboBox");
			ivjStateTableComboBox.setPreferredSize(new java.awt.Dimension(125, 24));
			ivjStateTableComboBox.setMinimumSize(new java.awt.Dimension(125, 24));
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjStateTableComboBox;
}
/**
 * Return the StateTableLabel property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getStateTableLabel() {
	if (ivjStateTableLabel == null) {
		try {
			ivjStateTableLabel = new javax.swing.JLabel();
			ivjStateTableLabel.setName("StateTableLabel");
			ivjStateTableLabel.setText("State Table:");
			ivjStateTableLabel.setMaximumSize(new java.awt.Dimension(77, 16));
			ivjStateTableLabel.setPreferredSize(new java.awt.Dimension(77, 16));
			ivjStateTableLabel.setFont(new java.awt.Font("dialog", 0, 14));
			ivjStateTableLabel.setMinimumSize(new java.awt.Dimension(77, 16));
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjStateTableLabel;
}
/**
 * This method was created in VisualAge.
 * @return java.lang.Object
 * @param val java.lang.Object
 */
public Object getValue(Object val) {
	//Assume that defaultObject is an instance of com.cannontech.database.data.point.StatusPoint
	com.cannontech.database.data.point.StatusPoint point = (com.cannontech.database.data.point.StatusPoint) val;

	com.cannontech.database.data.lite.LiteStateGroup stateGroup = (com.cannontech.database.data.lite.LiteStateGroup) getStateTableComboBox().getSelectedItem();
	com.cannontech.database.data.lite.LiteState initialState = (com.cannontech.database.data.lite.LiteState) getInitialStateComboBox().getSelectedItem();

	point.getPoint().setStateGroupID( new Integer(stateGroup.getStateGroupID()) );
	point.getPointStatus().setInitialState( new Integer(initialState.getStateRawState()) );
	
	if( getArchiveCheckBox().isSelected() )
		point.getPoint().setArchiveType("On Change");
	else
		point.getPoint().setArchiveType("None");

	return point;
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
	getStateTableComboBox().addActionListener(this);
	getInitialStateComboBox().addActionListener(this);
	getStateTableComboBox().addItemListener(this);
	getArchiveCheckBox().addActionListener(this);
}
/**
 * Initialize the class.
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void initialize() {
	try {
		// user code begin {1}
		// user code end
		setName("StatusBasePanel");
		setPreferredSize(new java.awt.Dimension(300, 102));
		setLayout(new java.awt.GridBagLayout());
		setSize(386, 101);
		setMinimumSize(new java.awt.Dimension(0, 0));

		java.awt.GridBagConstraints constraintsStateTableLabel = new java.awt.GridBagConstraints();
		constraintsStateTableLabel.gridx = 1; constraintsStateTableLabel.gridy = 1;
		constraintsStateTableLabel.anchor = java.awt.GridBagConstraints.WEST;
		constraintsStateTableLabel.insets = new java.awt.Insets(14, 8, 8, 12);
		add(getStateTableLabel(), constraintsStateTableLabel);

		java.awt.GridBagConstraints constraintsInitialStateLabel = new java.awt.GridBagConstraints();
		constraintsInitialStateLabel.gridx = 1; constraintsInitialStateLabel.gridy = 2;
		constraintsInitialStateLabel.anchor = java.awt.GridBagConstraints.WEST;
		constraintsInitialStateLabel.ipadx = 16;
		constraintsInitialStateLabel.insets = new java.awt.Insets(7, 7, 40, 1);
		add(getInitialStateLabel(), constraintsInitialStateLabel);

		java.awt.GridBagConstraints constraintsStateTableComboBox = new java.awt.GridBagConstraints();
		constraintsStateTableComboBox.gridx = 2; constraintsStateTableComboBox.gridy = 1;
		constraintsStateTableComboBox.fill = java.awt.GridBagConstraints.HORIZONTAL;
		constraintsStateTableComboBox.anchor = java.awt.GridBagConstraints.WEST;
		constraintsStateTableComboBox.weightx = 1.0;
		constraintsStateTableComboBox.ipadx = 51;
		constraintsStateTableComboBox.insets = new java.awt.Insets(10, 1, 4, 2);
		add(getStateTableComboBox(), constraintsStateTableComboBox);

		java.awt.GridBagConstraints constraintsInitialStateComboBox = new java.awt.GridBagConstraints();
		constraintsInitialStateComboBox.gridx = 2; constraintsInitialStateComboBox.gridy = 2;
		constraintsInitialStateComboBox.fill = java.awt.GridBagConstraints.HORIZONTAL;
		constraintsInitialStateComboBox.anchor = java.awt.GridBagConstraints.WEST;
		constraintsInitialStateComboBox.weightx = 1.0;
		constraintsInitialStateComboBox.ipadx = 101;
		constraintsInitialStateComboBox.insets = new java.awt.Insets(3, 1, 36, 2);
		add(getInitialStateComboBox(), constraintsInitialStateComboBox);

		java.awt.GridBagConstraints constraintsArchiveCheckBox = new java.awt.GridBagConstraints();
		constraintsArchiveCheckBox.gridx = 3; constraintsArchiveCheckBox.gridy = 1;
		constraintsArchiveCheckBox.anchor = java.awt.GridBagConstraints.WEST;
		constraintsArchiveCheckBox.ipadx = -3;
		constraintsArchiveCheckBox.insets = new java.awt.Insets(8, 3, 3, 4);
		add(getArchiveCheckBox(), constraintsArchiveCheckBox);
		initConnections();
	} catch (java.lang.Throwable ivjExc) {
		handleException(ivjExc);
	}
	// user code begin {2}
	javax.swing.border.TitledBorder border = new javax.swing.border.TitledBorder("Status Summary");
	border.setTitleFont(new java.awt.Font("dialog", java.awt.Font.BOLD, 14));
	setBorder(border);
	// user code end
}
/**
 * Insert the method's description here.
 * Creation date: (5/1/2001 9:11:36 AM)
 * @return boolean
 */
public boolean isInputValid() 
{
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
	if (e.getSource() == getStateTableComboBox()) 
		connEtoC5(e);
	// user code begin {2}
	// user code end
}
/**
 * This method was created in VisualAge.
 * @param stateGroupID java.lang.Integer
 */
private void loadStateComboBoxes(int stateGroupID) 
{
	if( getInitialStateComboBox().getItemCount() > 0 )
		getInitialStateComboBox().removeAllItems();

	for(int i=0;i<allStateGroups.size();i++)
	{
		if( ((com.cannontech.database.data.lite.LiteStateGroup)allStateGroups.get(i)).getStateGroupID() == stateGroupID )
		{
			java.util.List statesList = ((com.cannontech.database.data.lite.LiteStateGroup)allStateGroups.get(i)).getStatesList();
			for(int j=0;j<statesList.size();j++)
			{
				com.cannontech.database.data.lite.LiteState ls = ((com.cannontech.database.data.lite.LiteState)statesList.get(j));
				getInitialStateComboBox().addItem(ls);
			}

			break;
		}
	}
}
/**
 * main entrypoint - starts the part when it is run as an application
 * @param args java.lang.String[]
 */
public static void main(java.lang.String[] args) {
	try {
		javax.swing.JFrame frame = new javax.swing.JFrame();
		PointStatusBasePanel aPointStatusBasePanel;
		aPointStatusBasePanel = new PointStatusBasePanel();
		frame.setContentPane(aPointStatusBasePanel);
		frame.setSize(aPointStatusBasePanel.getSize());
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
public void setValue(Object val)
{
	//Assume taht defaultObject is an instance of com.cannontech.database.data.point.StatusPoint
	com.cannontech.database.data.point.StatusPoint point = (com.cannontech.database.data.point.StatusPoint) val;

	int stateGroupID = point.getPoint().getStateGroupID().intValue();
	
	//Load all the state groups
	com.cannontech.database.cache.DefaultDatabaseCache cache = com.cannontech.database.cache.DefaultDatabaseCache.getInstance();
	synchronized(cache)
	{
		allStateGroups = cache.getAllStateGroups();

		//Load the state table combo box
		for(int i=0;i<allStateGroups.size();i++)
		{
			getStateTableComboBox().addItem( ((com.cannontech.database.data.lite.LiteStateGroup)allStateGroups.get(i)) );
			if( ((com.cannontech.database.data.lite.LiteStateGroup)allStateGroups.get(i)).getStateGroupID() == stateGroupID )
				getStateTableComboBox().setSelectedIndex(i);
		}
	}
		
	loadStateComboBoxes(stateGroupID);

	int initialRawState = point.getPointStatus().getInitialState().intValue();

	//Select the appropriate initial state
	for(int y=0;y<getInitialStateComboBox().getModel().getSize();y++)
	{
		if( ((com.cannontech.database.data.lite.LiteState)getInitialStateComboBox().getItemAt(y)).getStateRawState() == initialRawState  )
		{
			getInitialStateComboBox().setSelectedIndex(y);
			break;
		}
	}

	getArchiveCheckBox().setSelected(point.getPoint().getArchiveType().equalsIgnoreCase("On Change"));	
}
/**
 * Comment
 */
public void stateTableComboBox_ItemStateChanged(java.awt.event.ItemEvent itemEvent) {

	if( itemEvent.getStateChange() == java.awt.event.ItemEvent.SELECTED )
	{
		com.cannontech.database.data.lite.LiteStateGroup selected = (com.cannontech.database.data.lite.LiteStateGroup) getStateTableComboBox().getSelectedItem();
		loadStateComboBoxes( selected.getStateGroupID() );
	}
}
/**
 * 
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private static void getBuilderData() {
/*V1.1
**start of data**
	D0CB838494G88G88GDDF954ACGGGGGGGGGGGG8CGGGE2F5E9ECE4E5F2A0E4E1F4E14DBB8DD4D45719A4A10A4634EAC8EA55EEC8E2336C2EE95216BD6B5958BDD96A36A4B11B34CB9A1B352DE9E9636921B9EE63491A46EDAE433FC8C4C1C54508CA88A4F478D341012422C8A412848589ED58468773E086E7660DEF5E4000887BFD773EFB5F1BE186FFCEFA4EFA4E471B7BFD77FEFF773B5F7D6EFDCFD2F4A0AEB34DD2C4C8DAA6517FB5249112F41410A6E75FF7B8AECACE4CA226FF878136
	1312498CF84E871AF7F1E2D681F9401D836DE550FEA4B6B1EBB77CAEA471EED28DBFA248D9E3A564251E8BD7E616D3F6154B49266D2F54EF05679BG71004367C69965BFDCDF2C63D76BF88EE99610B508FD1E28AF51F10D506E840885089FE47D9F03E7AE15FC3B2DD16F5737A00EECFC62EDCBBE72B17164D0BA4446366A4FD272F0DA6A904AFA196A09E3A6201DG004627131F8F9F03E7F367252177879437F5C072F8940FA68FB82C325D29A92A552BB8BD1A4D56090FAECD527C3E1312CF6E14BC320B9007
	83F9B79D49213C88290176C60ADBF79465AD075F2BG568A7C5F9EC3FC23936C85F0B8390D6F3D5D2C5B50FB65E1927D1CA7FDDB849B231C4C36F827E1E32C7FF759FB68FC58A7516FA9C04B99CE4CAA81B881F091A08AE09F0D03CBD75F01E7CB3FF6CAF13B95CF0B57FB52E5D7BFD26C328BFE5BECC051F1676581C5359342667A557A2AB07A4C84ECDC76119B9D47FC92DF4F67FD797987495AEF1F35E40771495A789A4B22E1660BC7068D1FB059A7A24AFEE6B8B8AE03E5FFCA4C3235D9CAFAE69859894FD6
	DAD2F4BE1BCC32ABAE61DCF75339AE065FE9FAFCB07CDFA85EE54233987D950D99ECCFGED5DA59E9B3DEFF0DDBAB29613025A0E30FED81458E2E9F5B19D7ADC213A6CEBC05EB1AEA6B3D768D2C1714BCCF8264BC3D4C7ECBF8634B2F7E2960BAFB741B59B836DE5GAB811281B68294F8201F0747D842556A6B0C31CEC955CE7A1DAE3B2C92B656BD398B1EF2C0D325F60FCF13BC83F20F24BA25FE176CA3FA1F11C974C1FF5038ABB47687C0468732C7D6A54D29F8003B6416B5D97531BE51135B310FAAD3EA37
	53AD03G3717F03E73A68BF1EEA41FF641EB17B45968416831136813912987B88283784D7C32B88D7D558A76778138049F36CDA13F33328A7942E66BD014CBFEEFB7DAC2D6FA504F1F525C91853F97F9380F6F31A00EA809D9458A6753B63543442755B9006ACB6A98DFE3FFBE06F939CA61B173D2AF0F19567C2F132AE7BF8C9BB388E79496AB97155035E327BCD1AF56CEBD5A2B4B381135FCE339A273D82F3E867E9AB218FFB3B268DAGBB16812C92BE3EE52A1445ACE6F89E251F2890E0325D70B85373E0C3
	016E4FCCFD1D64899B2C14369678E6F893332AG3A3D5C075F267671F948A4C64E497B42FC38BA480719A6DA6E97A00379EA14607EA40F489E2DD5764A9EBB7C706934C9AAC7DF3F030A9893CD71DED86F27E0017984FEAE41B5A9E91A6A6C77EBF2172CE9CE4F10C1DFC8E5797C7DDEA3D5F0D9DF2576A923305EC57F3FA138D686940FC72613A6066874B83AD6E5176C8625CFA9FE0F46708F26E13CDF124774DC7177E9D8G0DC8AEBF669DD6AFBCA261BCF9D5279BC281D397D96745F56CB9D2EC7251846030
	6EC900E712172F495143FCCD160D5FC79E7B7CADCB38BDE55157DBAD9517599AEB3EEC2CC9265392CBF85D6F23B9C3477B182E4BA751EF21C58BDB836FE5A21FBE606F8508394C57407EF42473BC23877FA94565E22E94BE5E1201B16E13AF7BE5C84F18504E8A3FED4F5891C26B51FD4868FBB25007AED3AEECD451077D8D3C9E0C83FDD6G2C394C7CD82CB2BF30FE650D3CDFB434172A117ABDAF7A2DD4993FE41575AB70197BFD6DB86F5787F88DA056974C0F79B37BF32CA542550AC3FE27552FB9B1B3CB9A
	843D572FD1E7337AF08D4D71E8F3320F5701775B507FDBB558B3811AB59EA3BF4867F5E06C25B8326E013D91EB5DEE0D6996500C98A93D0D57013333D0668A20A5E9FCEEE3B34CFBD10B4F278CB869B6284F4B29CCF38CB15C194CAD709CF42ABE2D9D96F9004F61471928AF6E13BADA2F9B6E2075847AEBAD48AD72C3EC79916F28AFB2695E61E77B1D864FF44A6F378DF51883E810D95923D3D6879555AD5BAB06259149AA0DEAD61AEF2CAD14FC9AFFD77239D9D5390369A9D36F334D3B22320757F377971E50
	E52F7E46E252BCD5E889D7DF47EC5BEFF9462F57F3FE432F935F4ACACF7D4BEB594C46CF9A390D2DA309D993A35C46DF7B425918GF4B6AE7DB89F1784B8B24AED9FBE0E3512D3135DD67C038BD6BE6510BCC353CDEF873AEEB8337B696DE8178F0750258465D63FF2D289FC5E9E4A29A3922B264A9E265F7704FE0D201BF7145BF56608592E78D1E657B3C27FBF58EEF66E58FE7593D12327G4FCDGDB83C60DD18A3F0F8402EB8ED63B6EEAC2FD4F84D86D1AA770E5946FB06119FD71F44DE1BB86E82981DEEF
	6D4A415C5DB986F59BC0B440D20015GEB47785A79D1E6BD39C6BD5B8E39C075C82E2E200D0A571554D73037686D79747CC97796427D3020A1742C32E9AC586E227160B69BF76F34F15610F6E348B896BBC32EFDBC1F61AC852C3ED158F7ED368FF99069675BFB6BF3F4D91D63BCCFDDEDB36729155B6AACF99172D40CAEBBFE5CD8CFD5623C7AD8AE665E75C04B388267D500C600BE00G4052AB5C7FFB335F3B167FE9D0F5AB27GF7867616D3217B3F705705461B73734034F121FEDEF9A5F89C735FC10D7BAF
	798A2F83F30F5F1C8E3B270DFBB3243DF5A25C3855B7A96F79E963C2EDAE1B8897DB6F77C40AAD07BCF049082DA93AB7E2CCB5839FF6A65AC8F19A34678BDCCAA73D4F02768A011BF38EF1C950DEABF02F1C27E7272B30F7DF6538273A91578C6D6E2B5C7FA5CD18539C500E86D886308AA0C5505F21F4B21918D5BA49EA872CB330B6E074EF58FAC37C40F90546BF422A90FC0AAEB3DCDF43775F1FA6A31D9875FCA80456767CBC92C986DBD787F2A2577165B9616B782A1C70F5FCDD4EF606879E66D3BD275BF2
	E62E73CF66042B7377D33B9F5F350D727679E49FDD68D8E8693497351DA4CC3D7FB19C7CF51F95122406DC3D4FE9CDB9D5ABFC3CCC5CE91D181CBE9F5D00F30460D95E7AA08F63E382E80BG9E81C882D8BF4973551A1CEB6E972783B36C97CF64C61E179F64061F1767F231D674A9FEF5C08E757DCFF30B854D480CD883201BD81F9769F8CDD20764E9F377929D4FE811467FF7EE2479E5F3B02F51BC8775E28E4AB5BE87B9D353672084F0B5A1F8466F44F1B33F23821FEEB3630F89B951C1F7012DBA4FDE7D69
	521FD1A6D90CDF5ED1740BD71587E595CEC3DD4EF10C2B55D33456B80A39A596FE47CD71FA6835CEBCB776ABAAF5E0C99476731E4333E5C1946F93F78E7B3824B1452F81AF5A671F4E71B364C5E887G62G92224CE710EF0E623A9ED4A5B738364BAB8F1078DBF0ECFE979FDB846D8A00B3G8E0018DB384C14EE9C8B831DEE3F1BDA41EC58CC7363866837D974A5041F9DD9DD59A17543591153F01E5BBDCE4D89510BDD9815887A0E2031221C90742210713C8723BF3E93E53728838E6708AC768BD60BFE5EE4
	2EC54BEEE5356819DBF92DDABE960E5EF7AB5F8B9E25350C975A7384EE071F6F99F18277FB5AEF8334F3EE63EB7A133C48EB32A7AF7C1A6C4B8B1FAB7B73E64E1543F9115692536FBD0D5BD1F29B57F9D9AD3723C660FEBE426D089238A5F4FF0D0576B2014BAF2667C5E8A7891CDA4C79A58B5CB80D7732E870B9C0DFB43EA37A8C27DB1F5B9FF7164253A30F160A423F558967C93CAC2D282F2929AA2D2D292BE4B5E45C28793DCE6316063077A188F151EC8DA4684F15338F6B72E61DC3CE7ABB94594C2F66CA
	1AA5BD823FD41D4FE693BFF6C67859B97E1E286A7644AC7B6D7CBDD16DC52CD3C5BD030659EC9F28CE7BC9E928032EE2D64F3CCD6B8F9C3F74F6FE563FF09EF9A6C3BB95A0E3962F2183653C06DE7E60E25291F5B66CD9FFDDF71D25E496537344AC23060E079A1A492C6F62B2A7001EB08BF1D217F99D209F1EA42CF6E3743B02685BF59F9A74AB5DE6BA52B2534D740702686823FCFDBC336D493738ED1B1F0CA72EFFBC9E719EE3B337E9B6FBF6769CA6E5F3DF4BE63175AFC5C6EC4D3215C40EAD082C1ACAB6
	D7AF8F96133035A7CDD34CE777B46568BA7702DC067B5FB10EDBB91B67411FD4C74E7DA9B4C71446A4E6D5814C0BC1DC253FB50C0D413531813DB04685F0D827C5416FA15DCFCC7E6B8D61F3765BF48DA7C37FF5G8CB7C8F34256B9D07B4E617ABCDAFBADFD4CBA354E895627975ACF6A7AB099F923A8438EF86F9C6E23EF9D413DEDD071E82CCF9255EDA95063C51F32E26C23C102E4737A8216EC0469B827F8A69D33C3FC66B2798C75DA83B2DEFB7D75578D1FADB80A6F0F9C0A6A9C871DA4D7B7C86DD4FCCE
	F60149F4DCCE73985A18B2072F4727BFB3670F024587ADE991E236E0AE0B55123946FAE4F561FF6CC23E1D00F74D657BC65C0EF76159A38D6BD560E97AE6191601006CD4952F2CEAE38CCBEFCA759A735FF7234F6689BE3F5CF1E886BEF86E6D026D42B3446B1DDF1E3B397735AF5F64382A73B7BA0E5D57BCCE73E35CDCF6DF13A670BF247865A6BC4B511F1D67B9FA8550CA6E6077B89B0BF15DC7C1FBA9C08240EA00D4001CF95C07D505B5649AF5F937626D00CD5EE55A1619FD0DDDB7779E7BDF33F9AECC78
	677B490AD92F26C53AABA919275F7B6B4F755D07CDE70E59FB51BE9750E2G16832C82D88330C1582795D6B17BB0B8DAB4CD9AF0505BDCEEDC0FD39EB550D8C0B25B16B4051EB591B26EE4FA945F997E2CDA857893A134EEE8BB6E8CF71750BF96CE46045EF7FE84994BG3FA204369A5A6BC2F0AC2FF8F0696BF9A05BFF00C45CAFEA6B0ED615569D29A487882BC9E92DC37FED7863F49C677FA37C14C61F57973672F9AD7907A5247539D753F8CE8A35E1232EEB6B170CFC31FEF8333E471D58E3687C146FFAF4
	E66382FB0DF1DF2DFDB3723886FDD8D8DB992FA57B8C9B13CB8C3E6F9E399E3EECDC6AC1E3DCDA712150F1ED100CAB8E592166D7068E41EE8D2520B1F66FE1E36CF70F221E262D5B5A05FF6158099F83E8BEEB476936EEFDCDFC1F0EE3317F562E37C8047C88117FA1707B24FF988E2E5D52109833E6974E072833D97A55E9BF5B054507131DD31C6374D5894B300C7E435DF8067385655C274F93916FEBCC3F257361E27F0B82A6E37C2FAA03597CBF45114FC099FB421F01366C89FF86AA58B373FDD0711E486F
	FD8F6CB13F77E53110DCC66FE4E00DAD82D889708CC04A17F8EEFCE88F46A4FEF9B2BDAF1E368F49BC40387C79FBA35B7A153D577EC66505A2FCF7562FE81A62C67E9C7FE2914E1726F8A91261D20B50C72AF34821312EBA7E37C538D6DD72A0C533B358F7E86DCA3E8C3F3F4C4FE5671B69398C5A4D82773DEAFEC66C94387CC2DA3FC1DB93385D9EFE4E0B9138758D7C5C38C6602247385CD4015BB94E6512799CF77A8817DBA4F03F57385C8A011B3A4065B68B5CC781AEF75EFC3E2F5715E3FD9807FDG5681
	EC82583AA0B12B8C2091208360A2C0GE0BE40B20095G2B815682ECG48D84863E0DFF1E524BC81C87DD0C703C1151C988E7ADC2CAD0B9C8F3FA83BF6BC241651772BB00F5DE3DE7D4E6C3F4AB007A80303BED9445DABE58582576E6315AB4B0D49CD3C0EE6F56C2FE9AC9403EDE58BF92D6DEC26E786A990F2E63805464889685731101F99DE21B19200F63498DFD698DC4F475198FC8468AB9772F30555B3D34D1ECEE62E59D3979AB59D13313B91E5EC82FC46DDDC3707E83C55C03BD9605E2771568BED3B40
	F9E83C4D0776D201FB1B5E69A4C1BB79AE2E73BD972E25F3668CBAE744A6E69D8C52F90446EA8160CBE245F7DA6511E3642372EB47C8CFF9781C51D7BEBDE7C86561F346D039B9E7B01C2B9CED9B02586E957DAE8B5C1F84EE0472C45C27CE3B669078892A97624F48A81063EF25FA0DD2DC2F0E1BED427D093F7FAC47B8871361D84647AEA827F166F439D87BEE5A47690130977ED8CCF5F4FAA5135E778A9C6A4D6A07B7AA8861754359E61C4FC374E631145EC05A8E71FE475EB57ABDDABDD3ED53G7549B1D1
	1F582B0CF116426B9FC73F386677481E9B2B098E63D1C57F5DD6EB70499D3BFE794EBAE35C9B6318D78E69972F2554BF36C32C5F3CFA231FED461A2FBE087F43C70CF1AFEB57BFEED703B14EF761467CF23C51700B7738E95E6B2FC7BE7B965EF3651B90C44BFFC3B2FDAA59B13DFF4B17FE7C5C724FDB0BAC4452CE329CB4165BC966A8AE0AGFEAEBA8819204AF719CC9271CFA3DB75CB6BD2FCBBA508F8EA3B8AE23689B16DB446570D545B52FF71C03A05E4008C36665BC986AEDC9025B21169306629408CBD
	77D8G360470B6374E52962E10B253087410BEAA498FF2763F5DF774D72BFF5A63CBA7D96DE48F667EAED925E52FECC7E3746FDD5BC8AE7DA09EF795D055C79F19BEED4CA5F710224173784D17CF136DAD3C2873E9E4A77D72249A3F0F2ABEA3799C9A2E48268E126141AFF3498EC6F6C91E21EAD6DDB7D1FB53DAAED2FDF1AD935037106A0BDC2C02CB61235B5EBA78E14A0AF4E2E9A3DBDC0AE4EF1386E0AFEFA7391AC315FD8E45E58FF87FF802B24A99651FE125833BFD42FCB4560AF7C4C269CB012F4D4BDF
	1F5F860E8B28241A7644AD57AAFA58CFB9A495C449EA3B06DF9AABEA19205D7226D67E54873B7BACA4CDB792DD1F9F70EAA4EBD0F2BBDDE370231506B61D146D9AE82BCA9E9FFEB1831A1506E846C9C200752E47EB6E7E3B5F7D198AB006EA64GF3A58C2D9E1295F7F54B602053A38B8660D7E03FDF3047BE5610BEBB828BF6FE786C6B7D90872DA45DDDD3C37F56523FF5783716E2EAA926368E1C3B014C78AF149E60B65360912B82275F5A86B328BD7F1B476F7F4F668D29E0DD9B4924DF4840A494983DBE76
	3B7BE5F556723B7767452E3B8367480B2192C03EBB83A27A79BE0E6BA6871626F9BB900B819362C7E1DAF9538290315B26C792228BE1BED92A4256AE5A35187E5DF7435E41D17B69588342288A43289C9D68FF3A8956DBA3EF84E776A0F2FB18567FABA36B8DA3F1AB67DF2BE7876BCD785DCB6514792EF6536D45915F9DA5ACE6F7AEC90BC36FEA5F1CC28BB316A4E615AE61755B40649FC864BB04AE3764F2E9126BD20B470E7789A747B459B82F9E1EC27EBE5EC7A7B25ABBD3287730E94C7F81D0CB8788DD55
	1466A094GG70B5GGD0CB818294G94G88G88GDDF954ACDD551466A094GG70B5GG8CGGGGGGGGGGGGGGGGGE2F5E9ECE4E5F2A0E4E1F4E1D0CB8586GGGG81G81GBAGGGDA94GGGG
**end of data**/
}
}
