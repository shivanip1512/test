package com.cannontech.dbeditor.editor.point;

import com.cannontech.database.cache.DefaultDatabaseCache;
import com.cannontech.database.cache.functions.StateFuncs;
import com.cannontech.database.data.lite.LiteStateGroup;
import com.cannontech.database.db.state.StateGroupUtils;

/**
 * This type was created in VisualAge.
 */

public class PointStatusBasePanel extends com.cannontech.common.gui.util.DataInputPanel implements java.awt.event.ActionListener, java.awt.event.ItemListener 
{
	private javax.swing.JComboBox ivjInitialStateComboBox = null;
	private javax.swing.JLabel ivjInitialStateLabel = null;
	private javax.swing.JComboBox ivjStateTableComboBox = null;
	private javax.swing.JLabel ivjStateTableLabel = null;
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
			ivjArchiveCheckBox.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);
			ivjArchiveCheckBox.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
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
			ivjStateTableLabel.setText("State Group:");
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

	LiteStateGroup stateGroup = (LiteStateGroup) getStateTableComboBox().getSelectedItem();
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
		constraintsStateTableLabel.ipadx = 22;
		constraintsStateTableLabel.insets = new java.awt.Insets(14, 7, 7, 2);
		add(getStateTableLabel(), constraintsStateTableLabel);

		java.awt.GridBagConstraints constraintsInitialStateLabel = new java.awt.GridBagConstraints();
		constraintsInitialStateLabel.gridx = 1; constraintsInitialStateLabel.gridy = 2;
		constraintsInitialStateLabel.anchor = java.awt.GridBagConstraints.WEST;
		constraintsInitialStateLabel.ipadx = 26;
		constraintsInitialStateLabel.insets = new java.awt.Insets(8, 7, 6, 2);
		add(getInitialStateLabel(), constraintsInitialStateLabel);

		java.awt.GridBagConstraints constraintsStateTableComboBox = new java.awt.GridBagConstraints();
		constraintsStateTableComboBox.gridx = 2; constraintsStateTableComboBox.gridy = 1;
		constraintsStateTableComboBox.fill = java.awt.GridBagConstraints.HORIZONTAL;
		constraintsStateTableComboBox.anchor = java.awt.GridBagConstraints.WEST;
		constraintsStateTableComboBox.weightx = 1.0;
		constraintsStateTableComboBox.ipadx = 85;
		constraintsStateTableComboBox.insets = new java.awt.Insets(10, 2, 3, 66);
		add(getStateTableComboBox(), constraintsStateTableComboBox);

		java.awt.GridBagConstraints constraintsInitialStateComboBox = new java.awt.GridBagConstraints();
		constraintsInitialStateComboBox.gridx = 2; constraintsInitialStateComboBox.gridy = 2;
		constraintsInitialStateComboBox.fill = java.awt.GridBagConstraints.HORIZONTAL;
		constraintsInitialStateComboBox.anchor = java.awt.GridBagConstraints.WEST;
		constraintsInitialStateComboBox.weightx = 1.0;
		constraintsInitialStateComboBox.ipadx = 135;
		constraintsInitialStateComboBox.insets = new java.awt.Insets(4, 2, 2, 66);
		add(getInitialStateComboBox(), constraintsInitialStateComboBox);

		java.awt.GridBagConstraints constraintsArchiveCheckBox = new java.awt.GridBagConstraints();
		constraintsArchiveCheckBox.gridx = 1; constraintsArchiveCheckBox.gridy = 3;
		constraintsArchiveCheckBox.gridwidth = 2;
		constraintsArchiveCheckBox.anchor = java.awt.GridBagConstraints.WEST;
		constraintsArchiveCheckBox.ipadx = 49;
		constraintsArchiveCheckBox.ipady = -7;
		constraintsArchiveCheckBox.insets = new java.awt.Insets(3, 7, 11, 224);
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

	DefaultDatabaseCache cache = DefaultDatabaseCache.getInstance();
	synchronized(cache)
	{	
		LiteStateGroup stateGroup = (LiteStateGroup)
			cache.getAllStateGroupMap().get( new Integer(stateGroupID) );

		java.util.List statesList = stateGroup.getStatesList();
		for(int j=0;j<statesList.size();j++)
		{
			com.cannontech.database.data.lite.LiteState ls = ((com.cannontech.database.data.lite.LiteState)statesList.get(j));
			getInitialStateComboBox().addItem(ls);
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
		LiteStateGroup[] allStateGroups = StateFuncs.getAllStateGroups();

		//Load the state table combo box
		for(int i=0;i<allStateGroups.length;i++)
		{
			LiteStateGroup grp = (LiteStateGroup)allStateGroups[i];

			//only show the editable states
			if( grp.getStateGroupID() > StateGroupUtils.SYSTEM_STATEGROUPID )
			{			
				getStateTableComboBox().addItem( grp );
				if( grp.getStateGroupID() == stateGroupID )
					getStateTableComboBox().setSelectedItem( grp );
			}
			
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
		LiteStateGroup selected = (LiteStateGroup) getStateTableComboBox().getSelectedItem();
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
	D0CB838494G88G88GF1F7A2AEGGGGGGGGGGGG8CGGGE2F5E9ECE4E5F2A0E4E1F4E14DDB8FD8D45715B7DFB475EB7CF64D46B4A631D9E3EC16D4125A54266E466D1A0DF51315ED92EAB626EBF75956F6ED57E4ED6B37CB3B36DE86908465BFCAAAC622C6D1F09CD0549222A0834C60A8A3A0CCA2919EB38F9819FF3EF948C0CC5DF36EFD773DB7438CE8B65BEF7D3E43FB77F76E1FF34EBD775CF36FBCC9515E7969A906A2C2D25209726FD48AA1E9F5047C7457736E66D84919448C227B77AE
	D0BEF924BB8D1E6540B30FA5E664117B1BB221DC87656941440CDD70DEC866B6555540CB0CF10A007728FDF65B6463641A79B8992CFCE8BBBCF3006A81F381ADBE03639FBAD42260B3211C233CA3259888711C45BACF94176870CDCA3DFA60F5A23F9E6B1CBBD42A7017G06F5926019F41655CD0667B6AA5D0C5A53CA3D1E7B6248263F2FB46C4031F47DF3F2543336B715E799D9C28D11CA3EDBF396EDE69F9EBC6374FB0DCE4167737BE451B9E4F4750BAE376C170C4AA360F77B645ADABBBEBAE4C19E891E93
	0222DD70099E32014A1DD01CC97B8439219CEAC04C3C93EDE502F78BD0DF83177D559DF50AECF30663C85263FBE34A3E2C01491C5420493E46531BBE7B9C76BFCC67825B15C1598144E47946A1AE4B6AF3DC167772316E7646440C83C0F50D9C5FC071CEA8FB0026B7F299BFB844E5F4FC72AD127C2781C3EE8C9967B5B259D6B4EAB25AFAFB535FE6FE5A04F3CE4CB0AE50E9209E201BC07300F6D3BF6DB29F01E7C33F5C6877FA7D3E06C0601C47A5DD703BC48F3C575682C7415AC527DFF29142FC7155624AA8
	72F0DAE28E77C3A426DBB7613E935F7EB4D9FF663AA1BD0AEECBDEB39ADED47AD8EB56F4ABE9C23BAD25F33B99709425DF06FF0762BDCD9A4E662A1562D88E81EFED933733A3074BE2CBDBC0722AAE99B22248B277EF1B8D754DCC86C733A68B3371C88B5AF8BA60730116823D823491A82F055BF83E7978D4B636026FCBBE4153B1529F90A4795C085B6392A54556578FCDE66BE8445A5D3A63F64CAEF14775F334769B7628F6D99BD7E328ECE176B037E8F6E1E3AF0DB9362BE522EF6428F37890DD932196B607
	29AA7EB845E76AF0B63795944772DC60ED86E2757B8A906F33C47A081762B7AD11BE720BFC6EA3F3013799084DEF2195E3E21F95568D509C20F820842075C01B2DFC0E7F5DFC6CB65711DDB73F2C6D42261D7094C332A4D8FDC1D970B945AEC1F28B7D9EB1C814BACFB521DD7B435ABDC357379346E891FD22A448EE3F8FFA973CA2B8D31055F92EA99F6BC8A265F63A3DA28C608D905E6F8BCD05707488C179F240A54822D60371D7B521CDEE88DD50A3F400EB25956D351E465CD2F8BF5D4AEDF53E953159D00E
	EB65F6E9EEAE0067F9F0EE51D3DBEBF6BBD18CC19A63BE7A6D1DF970DC86756B4FA7E62CG8A58787AFC4B417D503C63A9D279DADBD4BFC45AE8E3BE15F9A1528FD7517ECD005B0182C033AF707D543E5391518F0E0DB264A8B2CD35DE6A4F472E4346F0CF98E31B424BD17CF62AB152A761B13B7F65F9745D4520D78250068B5C76F7B517B11F448D13FBE11B848EA43A3419E0F57F5002FB0CF8C37449E6B1A07ADC7092D4FCE0C64BB64DBF877C2A6F5CCBF10CD70D60C0C1953F2FA51F6150075E5BB97FA14A
	97E4D9F2770F48E207A84BEE5F20567EB1AACBB0AC948695DD1F33212EE99793B3CA01AAAFF2DD0FD88AF5FEE6737B07C7821D380294FFFD196E4B75D05FF1114F7F1F67E3FEF5D3ADBFC94B9A3FAA5F21706FA4065ED90CEE25E35C0E9FFC76B1868AF08D24A83E04F3162D720214B797F4DC8E349EA82F0D6B1FCCE5636B0D7BA1EF7F794816DF2057BFDD475B76B90C41FCE30FB536EF9F30C6776DA356683EED32CE6E3B272D51FC17714EDAD1BF7F28CF37964CD60C0FCE3F4FA7528529B6A1ACCFFE2B9375
	F07B5CF2375BB5A84A9D6EF1917623B67469ADB4861F06F7DB9B0F19237BF94CBCB07EA4D95EFBA4EAFEB577ABD64342B66657CB5B341849E43237629A93BD22978C55689F7149CA2EC8633B0287D3A9D6FBEB845B16E3E6AE5285879BE03F7F1CBFD4A9D0050C6CE19725813F6495DD6557049B02D198150DF4C20C8D145D046F074B0E9EBDD396AD512F9CF0CB22559798D1F6297243DD07CD0A5F9412E96523D486ABD8498DB9940A72471222DA11A137EAFEACBB38E551EB44BFCC30A141B7B8D1GAB70279A
	BEB8216FDEEBE43736D87D3E8F421EE032FEA9C52232B6C84EA1778DE8AABA077F88262A617B552FAE20FF2D813F5BG14E667B1BC319579BC06AB9BD5235F63E1CB83F8C3F66A77BB0BF43EDCEAE7E7619C15DF31EBF2FE77947C3BE372191CFFF39EE3E6D03CBEA2C22A0673F1DE9CD3722D15678BA2F8DD02E7C44B633EFB9EE327C737BFB17CF9DB74B577F7B65C1306557ED75936924C47D81FACC6AC96304F00647642F60E9B2EF2667F49A17D19FFB1680177839B6119F6A9B103350D7F83462B48F37512
	CB587EAF8A78FDC0A914CFGF5DEE2FD4C39446C416A8D9772FA249D726F76D875769671FA275BD9FF76F6D6EFD63B3E5E8F55FAEBG5F881457915E1F3D836B6D5C494F5A47D46CA8452AE083F9B3151FE7AE64696FAB629F5C92B5A6A24D69E07D47F5C41E730CED586F0B002F69607E5AF4C11FF3B48403FE271B26594A1CEE39200F178CCB3B1885CFD89DC1B873394490770B2C0BA8AB66618A2C4C6D220A8F4844F3C5933D9A61A9DD63A8DD0D1D7EC6B88AD840359A556D244BADE2ECE4F67834D9FF7731
	AC7714A1BB069DB6A87AEF6F546C306EEAEFBA136921DABC4FC63B3BF1D2A10D03A3EE63086C4604DF10859A867477B576F2348B0EED6B64F7A1BFB7217FCF0772BC20A5C06CDC768ABD2F6138890025DDCE4CE0EBB4B7C73FC66BAE333C4E85CF9663CE95D643734EA39A377B0A4385EC1EA2229837650242FAE56C042797103ADBA5D16FE8E6666EB304AEB39B4E3F2C59B261555F98FAB27B143B24B905DC478DD067C097573129A61A0EBDDD5CB616A25EAEGD8DC97577DEBC5389E6EF05BD174FEB99F757A
	5F6EB4CC3E6CE2AEDF9248D6524D757AE2585C593B19DE21EEAE3FD47CD967CE39E32A562F7765C9A7E32E77D95D4A5CF4C72E7702CB68234BGFF251B2F77999773F56B3D9DD698D882A2B8DF43475BD0FE0855CA79D49B0ECBE3B561FBDFDBBB6A3E8E7AA48E30891089486E60E3FC3BED4A3B222650A4F7C5174B793DCBG7A1C8EB457211DDF6261FD39A37CBC436EC544AA1C2B558EF6AF122D62C10AEF5261ECEE7F0B2ED7AC97FD88F377A1BFB37CE3C1643D4F5A8F4347CB0EA833F66BA634B3C614CBBF
	8AEF477CF550331B47A2740C10B1B88A79F8EDEDABF7DCA566B467E4AAE31D78085FD7FC258377998F14E7824D87DA8614843499286C8A1F139D76E8B1B8FCCEE285E06D6C4B74FC38703359A7EEC23BC87B54DF89EF47743ED94375F6DC61F3F43168334950B3215DBB916559D722354B1FF0D7FCFB636D1D50AED26738AB51FC62CCD7AC1FD0B2DD45B64F6770FDE879956E93BF6EC4AC994A19BDFC7E2F5971B7823E6EE93ACB781A5FF58973B13F649EF47BB8D6CAB127DF2287BD06F53747BE03DEED0FFE86
	F535C7BF03DEEB1F7CFEC5EA0FF5C6E5F7A19E3A7F1600FEA7009876549144ACD06ED131768A44BCD01E26E2A31508AD0472B295339991CB02F2F28F773161AA0CF35BBFCE4CA88732G8D814D7C187347A8FFA114D7FC4CF24192A557E37C5F18A26F4DF8DF11B19CE9A8821F290C992D2E66DFBB2BA247C8A5316F5BA26FA3789DCA2CB176507EDE5A19CB67AA989403B4C8E0C624707ECDAEA6D14E7E97852FC87859F6C761B625CE136CEF2CD2E5BF1F4357463AA8FAA683367DEAB84EFC63EB9D317D70590E
	68FE382C837DBF689F111CE2242F3D50D1227234280739G2A416AA4507632A08D0A93FC75FB34BD6345EA3F36A316BFB39B3D9AE623632A0D6E2A61B6AA3BBA51C6A7G33C4602C3F99C57A7EEAD45CD22C47CF2A632C4C5147299E254F00721C2EBC6742B37E2A7E9C7933CE5CD3C3EA79ADDA9ED3473B3B9C6D96104481D10213B03B7AA149D7F14D4D23EBF8B93CA7DC65F9530AA398F77A7D92B53025976B7D3E824F0FF53D3C0E3B826BF804B17F888431A4DAE7DA057E1CB88B30A5C0AF76B2D9D9CC7D7A
	A87A7B00A4F841749D8151C91675E15BD9153CEDB9146B00FA00EE824D6B63E3BA0D58969A3A3DA3DE2A855361939AB75284F897F8DDC278F914651FB6219F4E8D1C07FE203F65E1DC22728B425A2AA9004AAF0AE84FEBB07ECB45B8F6442D08F2A770F415BEE7BDADA8393840F35A9FD9227183820F117DF42F198E6579AAF6CF8D0F25CBD46CA82D17561F18D15A4F3139B53C2DC94556E77336E695FB78B8EFBBC745FEC07B0B0372D2954BF1A016G6535AA96F4707E562B18A70F5EAFBAC1CFA74716D3ACG
	6559C08B1D785D42726BC41D0385C74A606993C74B54C45D6C0673AF7EF8D4FE5CE42AAC2BB2D59FE6F97A7CD1FD1EFEBA2BAEE61E3E5E49FCF13372CC7B42FEE53C368A6CC9795DDC5477E77AE42BA1B5C6FFA7DC4A6F16AE2DBF6667D5FD18932A3983AAD0DB5BA239DD6704C19BDDB5ACF7F8026E0D58376C62E7585595189347C3F98550DA973F138D9D64676E78A796905BB4FB54EF8E12BACF99B6A97214085AF9242F37B71D0D79CCA59F33937833C44412AA75FE077A2EA2ACCFE27C0223FAFE3EE2AB0D
	EF8E63A3AFBDD54F2FB46A79E8239DCAFB265B2A7738EE1BD7ADA61EEF1E0979BD45EAC527E465B97772FE92BA5E174EFC48A8EBBE741A29B4368F0187198E33397A65C089091A67513040EC76632ACCC5E653G7FC663F7D60E4D9960F147B696BB565ECB57D0B254CD9BCC4CF08DA276659AF3949D4373D00DCC50468C34DF61B960FD11E2A7B67E533B234748AEBAF6BC54DF8A443067E9BCD887650D2ABCDB322712C7AFD369D038BCA6A86726B0F95898AF50D8D28738E5085BE8D38E6EA583FE1F4C6AE465
	E29D99785354BA6FB9300E8CC9841B57CB7873GE1B2FE51B1190C58BEC3A727D9E7B314EBB10C610467AF37EC59226C0FF9783B7990A45F63A013606904D16D7E201BDD96B3999F25718AFDF519AA636BF905E16D9ABC6EC19F5E51A9EDFECE5B209C6B07789AFE790ABE3664BDFC501092434F0B5C4A5AF5EBEB181DAD9AEFE03F1D00875C7CEC11AEE1G6B922ECD3C598350AE798322A40FB1145EA4AB778856D13473EC351FD6E95FA47D6039348352A75FA04FD996D55C6937A16CBE40D209B1EE211B5D87
	242878A50AAF56616C1E607B957CBB29E540AB3B464FD97E907DEE864A730116823D823491E87BB05769BE89FF6319745C5D698F58A01975680EC1CC3FEFD5FE36EFDF1E4F6071EC49DFFD15AC3D6737A9316EC84B0615EF68142729F3BF4519FE06F154AFG3CD9C00B01DE84DA8B34C9556F8D291269071355A04B02F3085E94F3657072D603B169E23A7DEC423985A9534364A875C4BF7718GB7C7709CD076F8221E3D2D51461826541D93E30CB84017C570DE01723A880C450638CF8861716F131A3DA4E64C
	2F2A2E29AC2BBEF1186CA5AC0F4BA47C5F463393B15EFF5E27C41D57FF5D4E6735F45163447C7AEFD3F8DC0954A1D911556255FDF776985F27467F2049DCFEDBB233F64F5C525A7DA07B1D586DCEA94D226A4A7AFACD27E30EC16B571BF3BB7D32F687B334F686473E48F64D90984B7739A0CF768F6E039D9752B62DAD5DBE15365F4CAB0327EE7BB5F660DFB84A6107CCF25058EEED31F4323A1979FBA72FEBEBEA6ED4564FFF52B158BAF9E064BD92A33641AAE9057E5A7A2F414131D398D45BD8EE605C29E7C6
	96BA95DE5B8D4CE95C6C9C609E27BF593068487897C6710C948C0B1797F129AA6F5D3A77240AE86B6473A2B6462F7ECF47E0BA07C2316F85EE05225F8B4C88C53F1F3AB7B479B7927707E2FFA371F0C87F0D8473051CAC1C2FE9309E6781AD87DA8D345E4B63E879987AAFFEE1B7B106B63986C56EE0FC7C6C71583A960CCF7DAD5E6EF17C8D2F5FAF4BFEAF764F71F747F13EE4FF00028CBBB80EB6125C03C3B22B2A60156374EECD9C20B0BBDBED2F2667B7DFE2C6110F1F37EED22C8C4A27D52CE60C1F87EDAA
	76843D8F6803F2C04584A3BF53CDD73187F773B362EA95EB3072F1572BD822150F3BD9456AF27938F9FE0ED9CE72F18F2858AE939F77340ABDDB43471D6967B90007662C73203C94E8AD50A6204CG6489C0A700ACC0BDC0B250AC2079C0CB00DE84DA83348108DC67FEF0795343316284004AA10EBA03A4686FF0F7E6457607BDD9D37B4341AC7ABBAF4CE367D8C039332A482277428383C1D175BBD3D61E0AD903BCD3E531F1FFF5F85EEC41630892E36F2F26E783A194F1B6D8C4FD238874AF3B4E4F86A2758D
	9B149D57F93E7B576561F97B77282FCC877E6C6B7C7C90B2CE151B6BA9B2B7DF8E7D9CA57A7343CA6AE789002FD1E53B467CCC02F709E37FCD31CAA81FD6B1117A198B4A81953BCE7DEC9E1463A4AE736FCDD3491CBE094CEB201F1AB0197F051D2BG5FA8F11F1A169D5BB73E10BD35EF4C4E0E9EAB3E14BDB1D6BC129DBDD6BC162D0F958CFBA29BF59B841FF6287596295807AA36187609D8375BA58F297833D4AE44ADA28E285E75D239C6A966605F2668308F95ECE5B67AB7288C47A85EF6D5B675B3374743
	4AAF53BAEE9F383BEA0F55D4C6F7C050497D060A215C4C7F83F91A7F9F991BAC2FB9853949C9B5B739BFDFEB77C8796D372BAD525A7DC7651D65C39F63B53E72BEED27564FCA4B6D0F7F78AE2D5DAAABAA3FCF3928AC23B715357BD83D15A5DA3D2A5C497ABF9E567FCF5F565AFDF072765B59CB35F65926BB33CBF00FE6172F3E2375730B9D37B3BE7BFFD4EFF6FE9D1CA37ECD129E14C841447A8DFF72466B713D66A283B1D84956A16A23D612BE0A4E9E424F5C87E005D7862F931A3733FFB2592EDC7216612F
	B1AA93CD7BA98C132B8EE3253E1BF463F8ED6A0F9ECFB510B498032DE5ABC9438589C3C9EC48D4D84BF4C054911372F3C0D991FD6BCB67E9A9DD294DDB24C55411480EB0E3FF67311A1FA47CF3D7B015EC351237B126F70892CDE3C597AA23FC275FCC36517FB7053B85089A240F7420BC6691ED24E8209D3FCF8B4A222B01A7E9C1199453AFBA0E6107BB47ACC2F0C846C0763E8D92837C9F85240031BD02EF70984B165F277A26B474D1F9B166921037104A0B3D9855DE8A1F4BFD6F5D56F5CBD3092119E4F97C
	022BD9F0425EECA55B64A1C98C8E79BD2ED060ECBD6DA8F3147FAC1B8A5D6DD655C7E50DF847238ABD9C7A722C9D6BF7B40371C292B9C6EB6296EAD4EB389A0784890692A52B0C7FC342AF9DD0F9F73DA39F7C0716DDBD861222A809265F918A48A015A47802784589C8D096A181E7299D996FFF497440133F7E08F624B51549DEE6B2E8FAECD074FB0FB58C8C38FD225A81588F3A5F23F60FF50C91F58AC27795373E36259F665BCCD23DA6937DDBC57FD6635FAA0AD4D1242A9A0C3811CC7AAF129F228E17AFB3
	A7D13D9F2759588CB3A5FF7F4D17967CD35D468D20DDB3C927DF3300317334DA97C73C7D22F4CF7C83FB3267A45D0BF391C017607DEE9D903CEE4F983C18E91888A17EEEC875FE7EBBEA92CEB8ACCD9A6E4ADAA47FC820D6DD8C9890AFC4A9E5CFF0C0C4F3A7FA92420514D9CC7F165C311D30556FE85BD29C90DF7602D2AECD298ACD298C05F0D967044BAD131C70489E4636C6A97D3FD272242664D61D121961F293FE6FF278167E5EB4F9461E183F714C9CE377AD7346A26FCD7F0C7EB6358670CDE3BCAF7BE1
	B7661B316E84BA3C0247A38B1E61861F8B6F874E0D4922F67EFC50017D87F99D0549F80FBAD06EEB3AB67F83D0CB87882F222C2D6295GGACBCGGD0CB818294G94G88G88GF1F7A2AE2F222C2D6295GGACBCGG8CGGGGGGGGGGGGGGGGGE2F5E9ECE4E5F2A0E4E1F4E1D0CB8586GGGG81G81GBAGGG9C96GGGG
**end of data**/
}

}
