package com.cannontech.common.gui.panel;

/**
 * Insert the type's description here.
 * Creation date: (5/13/2002 11:02:58 AM)
 * @author: 
 */
public class MultiSelectJPanel extends javax.swing.JPanel implements java.awt.event.ActionListener 
{
	private javax.swing.JCheckBox ivjJCheckBoxSelectAll = null;
	private javax.swing.JScrollPane ivjJScrollPaneTable = null;
	private javax.swing.JTable ivjJTableSelect = null;


	//default table model to be used if none is specified
	private class MultiTableModel extends javax.swing.table.AbstractTableModel implements IMultiSelectModel
	{
		private java.util.Vector rows = new java.util.Vector(10);
		private final String[] COLUMNS = {"Selected", "Name"};
		
		private MultiTableModel()
		{ 
			super();
		}
	
		public boolean isCellEditable( int row, int col )
		{
			return col == 0;
		}

		public int getCheckBoxCol()
		{
			return 0;
		}
	
		public MultiSelectRow getRowAt( int row )
		{
			return (MultiSelectRow)rows.get(row);
		}
		
		public Object getValueAt( int row, int col) 
		{
			MultiSelectRow val = (MultiSelectRow)rows.get(row);
	
			if( col == 0 )
				return val.isChecked();
			else
				return val.getObject().toString();
		}
	
		public void addRow( MultiSelectRow obj )
		{
			if( obj != null )
				rows.add(obj);
		}
	
		public void clear()
		{
			rows.clear();
		}
	
		public int getRowCount()
		{
			return rows.size();
		}
	
		public int getColumnCount()
		{
			return COLUMNS.length;
		}
	
		public String getColumnName(int col)
		{
			return COLUMNS[col];
		}
		
		public void setValueAt(Object val, int row, int col) 
		{
			if( col == 0 )
				getRowAt(row).setIsSelected( (Boolean)val );
		}
		
		public void setAllGearNumbers( Integer val )
		{}
	
	}


/**
 * MutliSelectJPanel constructor comment.
 */
public MultiSelectJPanel() {
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
	if (e.getSource() == getJCheckBoxSelectAll()) 
		connEtoC1(e);
	// user code begin {2}
	// user code end
}
/**
 * connEtoC1:  (JCheckBoxSelectAll.action.actionPerformed(java.awt.event.ActionEvent) --> MutliSelectJPanel.jCheckBoxSelectAll_ActionPerformed(Ljava.awt.event.ActionEvent;)V)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC1(java.awt.event.ActionEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.jCheckBoxSelectAll_ActionPerformed(arg1);
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}
/**
 * Return the JCheckBoxSelectAll property value.
 * @return javax.swing.JCheckBox
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JCheckBox getJCheckBoxSelectAll() {
	if (ivjJCheckBoxSelectAll == null) {
		try {
			ivjJCheckBoxSelectAll = new javax.swing.JCheckBox();
			ivjJCheckBoxSelectAll.setName("JCheckBoxSelectAll");
			ivjJCheckBoxSelectAll.setText("Select All");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJCheckBoxSelectAll;
}

public void doClickSelectAll()
{
	getJCheckBoxSelectAll().doClick();
}

public void selectAllSelected( boolean val )
{
	getJCheckBoxSelectAll().setSelected( val );
}


/**
 * Return the JScrollPaneTable property value.
 * @return javax.swing.JScrollPane
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JScrollPane getJScrollPaneTable() {
	if (ivjJScrollPaneTable == null) {
		try {
			ivjJScrollPaneTable = new javax.swing.JScrollPane();
			ivjJScrollPaneTable.setName("JScrollPaneTable");
			ivjJScrollPaneTable.setVerticalScrollBarPolicy(javax.swing.JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
			ivjJScrollPaneTable.setHorizontalScrollBarPolicy(javax.swing.JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
			getJScrollPaneTable().setViewportView(getJTableSelect());
			// user code begin {1}

			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJScrollPaneTable;
}
/**
 * Return the JTableSelect property value.
 * @return javax.swing.JTable
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JTable getJTableSelect() {
	if (ivjJTableSelect == null) {
		try {
			ivjJTableSelect = new javax.swing.JTable();
			ivjJTableSelect.setName("JTableSelect");
			getJScrollPaneTable().setColumnHeaderView(ivjJTableSelect.getTableHeader());
			ivjJTableSelect.setBounds(0, 0, 200, 200);
			// user code begin {1}

			ivjJTableSelect.setModel( new MultiTableModel() );
			ivjJTableSelect.setShowGrid( false );
			ivjJTableSelect.setIntercellSpacing( new java.awt.Dimension(0,0) );
			ivjJTableSelect.getTableHeader().setReorderingAllowed(false);
			ivjJTableSelect.setAutoResizeMode(javax.swing.JTable.AUTO_RESIZE_NEXT_COLUMN);
			
			//ivjJTableSelect.setForeground( Color.WHITE );
			//ivjJTableSelect.setBackground( Color.BLACK );


			initJTableCellComponents();
			
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJTableSelect;
}

public javax.swing.table.TableColumn getTableColumn( int col )
{
	return getJTableSelect().getColumnModel().getColumn( col );
}


/**
 * Insert the method's description here.
 * Creation date: (5/13/2002 11:06:32 AM)
 * @return java.lang.Object[]
 */
public Object[] getSelectedData()
{
	java.util.ArrayList list = new java.util.ArrayList( getTableModel().getRowCount() );
	
	for( int i = 0; i < getTableModel().getRowCount(); i++ )
	{
		MultiSelectRow val = getTableModel().getRowAt(i);
		if( val.isChecked().booleanValue() )
			list.add( val.getObject() );
	}
	
	return list.toArray();
}
/**
 * Insert the method's description here.
 * Creation date: (5/13/2002 11:15:36 AM)
 */
private IMultiSelectModel getTableModel()
{
	return (IMultiSelectModel)getJTableSelect().getModel();
}

public void setTableModel( IMultiSelectModel model_ )
{
	getJTableSelect().setModel( model_ );
	
	initJTableCellComponents();
}


/**
 * Called whenever the part throws an exception.
 * @param exception java.lang.Throwable
 */
private void handleException(java.lang.Throwable exception) {

	/* Uncomment the following lines to print uncaught exceptions to stdout */
	com.cannontech.clientutils.CTILogger.info("--------- UNCAUGHT EXCEPTION ---------");
	com.cannontech.clientutils.CTILogger.error( exception.getMessage(), exception );;
}
/**
 * Initializes connections
 * @exception java.lang.Exception The exception description.
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void initConnections() throws java.lang.Exception {
	// user code begin {1}
	// user code end
	getJCheckBoxSelectAll().addActionListener(this);
}
/**
 * Initialize the class.
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void initialize() {
	try {
		// user code begin {1}
		// user code end
		setName("MutliSelectJPanel");
		setLayout(new java.awt.GridBagLayout());
		setSize(287, 264);

		java.awt.GridBagConstraints constraintsJCheckBoxSelectAll = new java.awt.GridBagConstraints();
		constraintsJCheckBoxSelectAll.gridx = 1; constraintsJCheckBoxSelectAll.gridy = 1;
		constraintsJCheckBoxSelectAll.anchor = java.awt.GridBagConstraints.WEST;
		constraintsJCheckBoxSelectAll.ipadx = 200;
		constraintsJCheckBoxSelectAll.insets = new java.awt.Insets(6, 5, 1, 4);
		add(getJCheckBoxSelectAll(), constraintsJCheckBoxSelectAll);

		java.awt.GridBagConstraints constraintsJScrollPaneTable = new java.awt.GridBagConstraints();
		constraintsJScrollPaneTable.gridx = 1; constraintsJScrollPaneTable.gridy = 2;
		constraintsJScrollPaneTable.fill = java.awt.GridBagConstraints.BOTH;
		constraintsJScrollPaneTable.anchor = java.awt.GridBagConstraints.WEST;
		constraintsJScrollPaneTable.weightx = 1.0;
		constraintsJScrollPaneTable.weighty = 1.0;
		constraintsJScrollPaneTable.ipadx = 256;
		constraintsJScrollPaneTable.ipady = 204;
		constraintsJScrollPaneTable.insets = new java.awt.Insets(2, 5, 3, 4);
		add(getJScrollPaneTable(), constraintsJScrollPaneTable);
		initConnections();
	} catch (java.lang.Throwable ivjExc) {
		handleException(ivjExc);
	}
	// user code begin {2}
	// user code end
}
/**
 * Insert the method's description here.
 * Creation date: (11/15/00 10:52:29 AM)
 */
private void initJTableCellComponents()
{
	// Do any column specific initialization here
	javax.swing.table.TableColumn isSelected = 
			getJTableSelect().getColumnModel().getColumn( getTableModel().getCheckBoxCol() );

	isSelected.setMaxWidth(65);
	isSelected.setWidth(65);
	isSelected.setPreferredWidth(65);
	
	// Create and add the column renderers	
	com.cannontech.common.gui.util.CheckBoxTableRenderer bxRender = new com.cannontech.common.gui.util.CheckBoxTableRenderer();
	bxRender.setHorizontalAlignment(javax.swing.JCheckBox.CENTER);

	// Create and add the column CellEditors
	javax.swing.JCheckBox chkBox = new javax.swing.JCheckBox();			
	chkBox.setHorizontalAlignment(javax.swing.JCheckBox.CENTER);
	chkBox.setBackground( getJTableSelect().getBackground() );

	isSelected.setCellRenderer(bxRender);
	isSelected.setCellEditor( new javax.swing.DefaultCellEditor(chkBox) );
}
/**
 * Comment
 */
public void jCheckBoxSelectAll_ActionPerformed(java.awt.event.ActionEvent actionEvent) 
{
	for( int i = 0; i < getTableModel().getRowCount(); i++ )
		getTableModel().getRowAt(i).setIsSelected( 
					new Boolean(getJCheckBoxSelectAll().isSelected()) );

	getTableModel().fireTableDataChanged();

	return;
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
		MultiSelectJPanel aMutliSelectJPanel;
		aMutliSelectJPanel = new MultiSelectJPanel();

		Object[] o = {"AAA", "CCC", "LLL"}; 
		aMutliSelectJPanel.setSelectableData(o);
		
		frame.setContentPane(aMutliSelectJPanel);
		frame.setSize(aMutliSelectJPanel.getSize());
		frame.addWindowListener(new java.awt.event.WindowAdapter() {
			public void windowClosing(java.awt.event.WindowEvent e) {
				System.exit(0);
			};
		});
		frame.show();
	} catch (Throwable exception) {
		System.err.println("Exception occurred in main() of javax.swing.JPanel");
		com.cannontech.clientutils.CTILogger.error( exception.getMessage(), exception );;
	}
}
/**
 * Insert the method's description here.
 * Creation date: (5/13/2002 11:06:32 AM)
 * @param data java.lang.Object[]
 */
public void setSelectableData(Object[] data) 
{
	if( data == null )
		return;

	getTableModel().clear();

	for( int i = 0; i < data.length; i++ )
	{
		MultiSelectRow ms = new MultiSelectRow(data[i]);
		ms.setIsSelected( Boolean.TRUE );
		getTableModel().addRow( ms );
	}

	getTableModel().fireTableDataChanged();
}

/**
 * 
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private static void getBuilderData() {
/*V1.1
**start of data**
	D0CB838494G88G88GF8F954ACGGGGGGGGGGGG8CGGGE2F5E9ECE4E5F2A0E4E1F4E135DAFDF0D455958F98A1826DE01B51C829260A1A51A8DAF1063636469D9CBF48CC3545B16D509127B8B216DA2672C7D46A5CEC1285B1C2B00184854582CAAA46A034592C8966D38884591290962542E377A57BB6EF77ADEF5FE6F7E1C9FA4E3D6F3EFDD9F613E2A5B33FF96F1E7B6E57B9671E733BF7C32A5F4BADAF31D693D2D2CE743F4692C2D6FEC0C8DD772F0A39ACC3345808696FFD4086723D06B2
	F84E043A5953AD36CAD23BF38D1467C039964AB570DEC5366E746E0417B46334E21DF0F7F87CF10E9C67632C2665975E2A0067BAC09E487039D244712DD5353A3CD017F314D889F9F4G3F59DA35C51759216C84E482F2865877774073F5BA72558E3B7E5D29EBF3494A47BF343E097D187A64289CE4ED77694FEDE4EE4962C19C6B0B2A8A3D0D8C65B9GDCFCA9B9503E8B1E4D4E21417DEE45DF6F56247A4130D49F56A45961B89216B529D714C537F648A984C45965453E3EDF350636057E213C144A0EB4E37F
	3361FD9E205010D73720FC17E4313582DC92DFD34B074D7A1CDD976612D3CF87CA5727D9D3D0E2EB491192EB4A8E3F32FAB333FBBF6AF991549583AAFC96DB83408988825E21F64FBE7E8FF8361D55BA943FDF893485036D32C7BD22F8C4995E9D8E2851E547C43722FA88E1366D295A1DE2BE6301357B6A325BB11D646D65F64EBFF69BD9FC5FE16B6A94BAD91C57E04D72B1DD647A92BAE1E37FB06D58853E31FEB8F66C9344BCF64F942534BC4558731E2A33AE527BD9E69A3BF688ED7D907501B570DE227B87
	13AF2472D3A6B97349DEAA47328CF5CF8CF15FF02D63F369A91BC5AA77742454C30E254D3ACFE6F36811136712C57DAEAAA74F6587D41EA5A74F656D96BE179C28DB85E07EF51D8B7768993F459687E48372810500E500D5FE6EE3FFB87E497F68E3CEC1555A43126C91D5425ADE573F891EE2D4D3056EC0C89382EE31DFD0A5612CAC06087E4DCD7D280333E35A4D213E6F06B18E0881D195B4C989C06F02DF54C4B5443E392BFF83FE230A3436CF720BB000BFC8F83F7777D7217E0510F6BC6891B4B171852B7F
	E5BF5AE4D868079E2183F8E7FA19A9223E3684AC36FD004EG57037D8476F7D8D4A1DAB89CBD0AB2948E7661CAC8F6G75FC870D9DAEF88F86380EE78C202C884AAB0CFE1493EF197A69145CB8FDC10D71BD76C8B34661B2057B4C0BAE6EB31DEF5ECAF6BFF5A8254FA0F6A84CD71A15643D73A46D9367454AA71BDD7A98173317AFAFD628F40C52AB507FBA43D62F8C222DBC300E8CC02E42F5FC4709ED4CE75B0510483D74288A8EA6FA929AE7737CFECB192ECFD64E2165798A57553696D79AFDFFF73AEAEA39
	123AE2BA49F5E19E9207450056A986450087DEC2FA5D6DAE74B5FD1F8EA8C61B3B28BC887B3A83B6D248105F67423DA7E81AAA1D8DEBE22F28E9D2E0B0D17F8BBAD6A8FCB6188889FCAC0B8B632F9201FDEDFC7F088B7704DB8984C4EA9C23890FA3F4CFC23A764324BB14F0C0E37259A25AEAC80C69B1A1D7C4E2B3AC48E10CAF0C875CA33C01EBD0A5BF189CC394E9885218EE7098449230B77219B2282B8D723D97594E775E0E73BFA18527F7DAD3650E1CDBBB2D51A05BCBB302093D4766F46DC06A39DFCFE3
	03AE8F31396E8F21DE0F04B5F98CB9B17CFD2F04FD654398850022A077772AC12C67B1C5F7748EC51619BA8DBD93EFA55AC5BC9796A194E370BAEC68EE35772D243AFEDD0F2CFE3D9775A81B625E7CF3284747DAB857DB8173D98332DD6718AE3D6718AE585ADE2AC3FDD3BE66930605E8FDA882BE63F0187957C755EB746F4BD44E333AEAF04D4DD06E8348AA374D13EFF21E15BD14CB165C72F6DA6E182932F96429895BEC3B0A732C0BBE9CF39954952BDC1F533C66D85F968AA9EE092699DD97CD1259F6CC36
	DFDA8B4F81C98DE95D303922DCEF1D924E977310AE567372DFE9DE4635D604A04FG6607D03E2A265AB477F8086513B970AC257D1D5C7FA96EBDEA58FA76F00A6A00227AC54FAE54EC3D90516A69BE2FEF2355DD78DE476DB07BD09BBCFD9DDE51BD542EC4191F3549726936243EFA52F776D9DDBF617C6A662AD6FD2E05F74FA24D23D556D4FCB7EB7DD6AB51187EF7E989BB44FFEAABDD7E4D2B2BD99CEC28CE66F81A1648D1D95A25790AF11F1FD19F192DB16ED3EE4897D079BDA6B9537D619A2E7B85D037AB
	4C73F50D0CBEBA834AF90082C0912098D0BB4CFD23D05ACBA660C35D8120A981C16E9D93GF5FF186CABE5B1CBAFCF71157318C5F8BEA8EEC956433E6131EBF7A615D9BB5B7E64F66FA66BB3291D9EF77A5FC93DB739179036BF7756727DB9E3186F430BDF187761836BBF353E11E69FAE5047DEBA1C307FEE633C73109F63480E881C27GAE008648814C8394C5387E5F99EA19C87F54C77B148E109D04F855111CD7F02E14339D207126987A2E088287263237B6212C814A87231C83BCD803B2994AB38C5963D4
	0671AE4F107D3B4615E25DE6E07B4B495D3269FBB606EF42B14AAF237FF5E4AC6FE0BA5EA627678A9F4A29394247F2EA2E30D75E406450077904406BBF134767929DF2AAAE31154E7B31CD6BE95F21109822C66F94B4C12FFBDE5BC8D2F00AAF6190212F390AFC327FF57D1BAECD6958E36858DE1B6A2CD9285BE2C98A9BB29FBE28E06CDE897515B108AB00D6401998775D5F4993460E2E68B831E321BF3DDDFE63CFED17A2BF66451092D65DE2326E1F71579AF509DD522D09FED4937BE699ED2F896A20F8096D
	7EC25B333AF46D0B7D696C4BEC70CDB5F2B6D908A9E10D1065B15C0361EA4E4DB2201C83A8GAC0A1979443D910CB383F0A8046EFA03221BBCFA9E5B56ED62ED77C179A0408B4882641D67B9747AA348F58EDD1283791C3E6EC31EF428572D8207C2FEC2898F630DD90DE65ED8F9016567E6F8BAAF7098F21446C9AF1423064C371947154C8B5C9F1CCAFAFB1ED0D25B73BFCA6AFD66D6465FE7C3CABABB301C7863EAFE0F368866D7916777E8FB4EE0DCB6F2839282076300AAF95A05419EEAB5169B1E2331945B
	F746B9F72B5A08FD46211C8D480F731C917D00670C7C5BE6111E0C43A9395B123ECF2D8B627A1DCCBC11B372A0E730B10FEE62E36E38E8311D3A08320B1B4C7C896D3941CAC8BDBCD97D5CB175289320C6267AE7475463FCBF9F533EEA8CBFABB735E7EBDB38132FED5542BCA25F5F1416174E39485674287EF476EFA7BB263D3A1A69FD30EBABC9191393DE8975CC8F850DEB7458C5C6386E0B9BD177F6A8BB815E916E7B8335F8E6524031B2C6709E38F0981BAD9D41B613B6F7261827D917E6AC00B6086D7A7A
	0A607D5A9273D97A16CB785CC4E06D6E7D166DDE386CF60CA7BEC77DF477A86309A5067CC5AA371B64EC2FEC5E4877CAA754650DF27EB8B7023121A283E2BF20956082C4815999FC4F17849B4884B920CF8976C0D010CD5B182D2FEF53373BFFFDE8B57749F9BF3F194C1F72F2C9BA0E1617217B247EDC56375D145F5AE3383EE2282B1EE4315981CE0087901F4457579D5C4D5657651134B6CD935CDEFACA620B6B1744C8C20C011B2D6D61C63E361950571CC9E3F36C7CC969383FECC7DB94CECA3E8F9D267245
	13126FC3DFEB6477214BF17E13F9CC7A7557665FA2AAE73DEF65B6CF9EF75EE4B61702490918749EDDBB70C11278E37BCB3CC08CBBBCDB416D30654E9F114E27DFAE591EE60C15FA5F1DD7A546D866DB251F137E0454C6777915267777B6CE448D7F9F685C680A0E41FCE8FEA4FDDEFCB012BAAFBE9CC91D9797C64667173F0D243F2B7AFD44FCD74576C0BC0E71BF8EF64981BCGF8823074AA6E7F5E685F89757F943E5F6599C41E05B7619A9F7FF0AC7DDA0F45A63EBF7FBA06778EE795CDD37C58BF178B74FE
	D3D302D448E483B15411AA8DFAB57629AE17E374FCA38ED0B163AB0BE9CEA119965B0ECC4EE17EE9C7199D4AE78C59CCBB67B5DAA6BF8729F64CB18B0CF21C16979B65B27BF769C3B84E25FE3A75BB9DA379EE1679428D97092143D5B17C1935DF7069919D431F22F24B9F0294FE742C8FE8E61FB0E834DFB0027916DCED31ED813482DCG991089488164838AGC500950032A996DBADE0B720996084F82698775E976A524D8104BAE9238E288A923A20BE774CF8FA9F1C9E1F5887336374BE8CFC27AF9654F97F
	8DF164754A40C0C8B4FC7D46F825A16B8EB9151004678546CBDEEA445F3207C5D5135CF07C21A427DDD01D0AAC39E31033505756D1DF43B5C627F0BE53E3C7DDFA95D5BA2F8434CBDB923D2D1736453B03D5D0E6FC67F67BC4FC271CA478CE6BD40B2DEFAA63BBD87642BB7E66FDC389BFB77C6DE34CAD0768E9019DAA581961AE3B794C20CDE5F786B9704C1F4AEDD7B4125E864F0CCCEC03E5A32963400BA317460117C6D2470117C74CF10049DE9BC19D8C026F380C6FCAC7396C24A1DBB37A3AAEBBA1F9B42F
	A1DFB73AD6179F94F1C0AE2F9E45F9C5284C254BB61BE4A7F5593B2368C730E4B0B1EF3BFD94634900A44B7A1D847DC68A00DB997A58CD67A88585533C77983213045BEC27FD9FBCCD943B5E44306B7B3B0E75F5F734751C6E6DB8F63427273D6D5869365E53DFF6F5F5F6F5321C1E9BB173867B5A46143F9150B8B5154529A27D7940546D7A9CC66D7F9AE78E870F9E6B7E6A6817FD29E7C148255CE3456729E40C0FBCCFCB4C2FFD97525C49D38F564FF5F67EBF9115D97CEC314F01FEFE904AE7G51AC4EFFEF
	AA41EFE7C2B9BF0B7B35E57423F1E212D3D5023065E3CCCAFFF160DC5EBE9E57AD4CCA478757515F5E17E4A5F3799AAADF1E154C659D8E4E650B216E54B5FC5EF3CA30CF03B7302969FEF4E7C9B575B788DF02AC1D27BF8D74420701C11693CB1AF8CEAC3806673CEA24FEBCB7195EDB9DD71E331D3C22E330FB175AA69E6316421A0B813553600CGF0A1285F6CEF62677568B4FEDE171B124F6BF8062CA77C3CFDE3D372F99D5BA76A1FE8CABE2F0FEDEFF5A41F57F9FB261B0E264BBDD3317F392A7F847F21AA
	7F4F24DCA3EBD9305F56A64BA1152CB7FEB8692691E94970DEC7699FEFA9351232EEE2E3913A1B14E1186DA665B48AF713D208505120BEBB7A8B984096BAC7C688BF3FF029157073C45D28B97694DFDD1BB676AC192E7F8F577444F9026D594CBD54D7E7D8ECBDB3386FAFEA78182467B23DFEC116A1538E3585BC48EB5AE31A186053576C417EC37C9B3D12554D5C037376195A7C97D0CB8788BA89C03CFC8EGG9CA7GGD0CB818294G94G88G88GF8F954ACBA89C03CFC8EGG9CA7GG8CGGGGG
	GGGGGGGGGGGGE2F5E9ECE4E5F2A0E4E1F4E1D0CB8586GGGG81G81GBAGGG368EGGGG
**end of data**/
}
}
