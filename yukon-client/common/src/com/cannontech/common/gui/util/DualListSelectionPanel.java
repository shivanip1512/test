package com.cannontech.common.gui.util;

/**
 * This type was created in VisualAge.
 */
public class DualListSelectionPanel extends javax.swing.JPanel implements java.awt.event.ActionListener, java.beans.PropertyChangeListener {
	private javax.swing.JButton ivjAddButton = null;
	private javax.swing.JLabel ivjAssignedListHeaderLabel = null;
	private javax.swing.JList ivjAssignedRoutesList = null;
	private javax.swing.JPanel ivjJPanel1 = null;
	private java.awt.GridLayout ivjJPanel1GridLayout = null;
	private javax.swing.JScrollPane ivjJScrollPane1 = null;
	private javax.swing.JScrollPane ivjJScrollPane2 = null;
	private javax.swing.JButton ivjRemoveButton = null;
	private javax.swing.JList ivjUnassignedList = null;
	private javax.swing.JLabel ivjUnassignedListHeaderLabel = null;
	private boolean ivjConnPtoP1Aligning = false;
	private boolean ivjConnPtoP2Aligning = false;
	private String ivjtext1 = null;
	private String ivjtext2 = null;
/**
 * Constructor
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
public DualListSelectionPanel() {
	super();
	initialize();
}
/**
 * DualListSelectionPanel constructor comment.
 * @param layout java.awt.LayoutManager
 */
public DualListSelectionPanel(java.awt.LayoutManager layout) {
	super(layout);
}
/**
 * DualListSelectionPanel constructor comment.
 * @param layout java.awt.LayoutManager
 * @param isDoubleBuffered boolean
 */
public DualListSelectionPanel(java.awt.LayoutManager layout, boolean isDoubleBuffered) {
	super(layout, isDoubleBuffered);
}
/**
 * DualListSelectionPanel constructor comment.
 * @param isDoubleBuffered boolean
 */
public DualListSelectionPanel(boolean isDoubleBuffered) {
	super(isDoubleBuffered);
}
/**
 * Method to handle events for the ActionListener interface.
 * @param e java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
public void actionPerformed(java.awt.event.ActionEvent e) {
	// user code begin {1}
	// user code end
	if (e.getSource() == getAddButton()) 
		connEtoC1(e);
	if (e.getSource() == getRemoveButton()) 
		connEtoC2(e);
	// user code begin {2}
	// user code end
}
/**
 * Comment
 */
public void addButton_ActionPerformed(java.awt.event.ActionEvent actionEvent) {

	transferSelected( getUnassignedList(), getAssignedRoutesList() );			
}
/**
 * 
 * @param text java.lang.String
 */
public void assignedListHeaderLabelSetText(String text) {
		getAssignedListHeaderLabel().setText(text);
}
/**
 * 
 * @return javax.swing.ListModel
 */
public javax.swing.ListModel assignedRoutesListGetModel() {
		return getAssignedRoutesList().getModel();
}
/**
 * 
 * @return java.lang.Object[]
 */
public java.lang.Object[] assignedRoutesListGetSelectedValues() {
		return getAssignedRoutesList().getSelectedValues();
}
/**
 * 
 * @param listData java.lang.Object[]
 */
public void assignedRoutesListSetListData(java.lang.Object[] listData) {
		getAssignedRoutesList().setListData(listData);
}
/**
 * 
 * @param listData java.util.Vector
 */
public void assignedRoutesListSetListData(java.util.Vector listData) {
		getAssignedRoutesList().setListData(listData);
}
/**
 * connEtoC1:  (AddButton.action.actionPerformed(java.awt.event.ActionEvent) --> DualListSelectionPanel.addButton_ActionPerformed(Ljava.awt.event.ActionEvent;)V)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC1(java.awt.event.ActionEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.addButton_ActionPerformed(arg1);
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}
/**
 * connEtoC2:  (RemoveButton.action.actionPerformed(java.awt.event.ActionEvent) --> DualListSelectionPanel.removeButton_ActionPerformed(Ljava.awt.event.ActionEvent;)V)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC2(java.awt.event.ActionEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.removeButton_ActionPerformed(arg1);
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}
/**
 * connPtoP1SetSource:  (UnassignedListHeaderLabel.text <--> text1.this)
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connPtoP1SetSource() {
	/* Set the source from the target */
	try {
		if (ivjConnPtoP1Aligning == false) {
			// user code begin {1}
			// user code end
			ivjConnPtoP1Aligning = true;
			if ((gettext1() != null)) {
				getUnassignedListHeaderLabel().setText(gettext1());
			}
			// user code begin {2}
			// user code end
			ivjConnPtoP1Aligning = false;
		}
	} catch (java.lang.Throwable ivjExc) {
		ivjConnPtoP1Aligning = false;
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}
/**
 * connPtoP1SetTarget:  (UnassignedListHeaderLabel.text <--> text1.this)
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connPtoP1SetTarget() {
	/* Set the target from the source */
	try {
		if (ivjConnPtoP1Aligning == false) {
			// user code begin {1}
			// user code end
			ivjConnPtoP1Aligning = true;
			settext1(getUnassignedListHeaderLabel().getText());
			// user code begin {2}
			// user code end
			ivjConnPtoP1Aligning = false;
		}
	} catch (java.lang.Throwable ivjExc) {
		ivjConnPtoP1Aligning = false;
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}
/**
 * connPtoP2SetSource:  (AssignedListHeaderLabel.text <--> text2.this)
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connPtoP2SetSource() {
	/* Set the source from the target */
	try {
		if (ivjConnPtoP2Aligning == false) {
			// user code begin {1}
			// user code end
			ivjConnPtoP2Aligning = true;
			if ((gettext2() != null)) {
				getAssignedListHeaderLabel().setText(gettext2());
			}
			// user code begin {2}
			// user code end
			ivjConnPtoP2Aligning = false;
		}
	} catch (java.lang.Throwable ivjExc) {
		ivjConnPtoP2Aligning = false;
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}
/**
 * connPtoP2SetTarget:  (AssignedListHeaderLabel.text <--> text2.this)
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connPtoP2SetTarget() {
	/* Set the target from the source */
	try {
		if (ivjConnPtoP2Aligning == false) {
			// user code begin {1}
			// user code end
			ivjConnPtoP2Aligning = true;
			settext2(getAssignedListHeaderLabel().getText());
			// user code begin {2}
			// user code end
			ivjConnPtoP2Aligning = false;
		}
	} catch (java.lang.Throwable ivjExc) {
		ivjConnPtoP2Aligning = false;
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}
/**
 * Return the AddButton property value.
 * @return javax.swing.JButton
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JButton getAddButton() {
	if (ivjAddButton == null) {
		try {
			ivjAddButton = new javax.swing.JButton();
			ivjAddButton.setName("AddButton");
			ivjAddButton.setFont(new java.awt.Font("dialog", 0, 14));
			ivjAddButton.setText("Add >>");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjAddButton;
}
/**
 * Return the AssignedListHeaderLabel property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getAssignedListHeaderLabel() {
	if (ivjAssignedListHeaderLabel == null) {
		try {
			ivjAssignedListHeaderLabel = new javax.swing.JLabel();
			ivjAssignedListHeaderLabel.setName("AssignedListHeaderLabel");
			ivjAssignedListHeaderLabel.setFont(new java.awt.Font("dialog", 0, 14));
			ivjAssignedListHeaderLabel.setText("Assigned List:");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjAssignedListHeaderLabel;
}
/**
 * Return the AssignedRoutesList property value.
 * @return javax.swing.JList
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JList getAssignedRoutesList() {
	if (ivjAssignedRoutesList == null) {
		try {
			ivjAssignedRoutesList = new javax.swing.JList();
			ivjAssignedRoutesList.setName("AssignedRoutesList");
			ivjAssignedRoutesList.setFixedCellWidth(-1);
			ivjAssignedRoutesList.setMaximumSize(new java.awt.Dimension(130, 200));
			ivjAssignedRoutesList.setPreferredSize(new java.awt.Dimension(130, 93));
			ivjAssignedRoutesList.setFont(new java.awt.Font("dialog", 0, 14));
			ivjAssignedRoutesList.setVisibleRowCount(10);
			ivjAssignedRoutesList.setBounds(0, 0, 130, 200);
			ivjAssignedRoutesList.setMinimumSize(new java.awt.Dimension(0, 0));
			ivjAssignedRoutesList.setSelectionMode(javax.swing.ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjAssignedRoutesList;
}
/**
 * 
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private static void getBuilderData() {
/*V1.1
**start of data**
	D0CB838494G88G88GC8EDFEA7GGGGGGGGGGGG8CGGGE2F5E9ECE4E5F2A0E4E1F4E14DBC8DD4144735793129FD4F57184424B6A1ADEDBD2DE9EDE3CF344736364FB634A50DFDCF73F80D353C54342625F92635AD6754571A748384D603000286949581A3A8AE60CFC2902388A868AA884B1FAE51057D58DDD9F6575D8FD88C01F76F4CF7E7BF16DDC01A1C934F39FE5F5C19BB77EF664E1D790615B27705A74645E7CAD2DC22247EFBBBCE12E21BA56940E3891FA71CAEBDBADE527C5B8F30CD
	EAC95B844FAC28AB680E0ECF17FA7AF4D0AE06F278356878B760BDC30A9D30DD041790FC4EC01DBBF62DEEE2BE8E8B71C9E6654B4694F8268214828E1F43690C3FB1CB459BD5BCC19C745A3E8D5B5CED4CD1F14B204DAA00CC4097EE636D4F40F3B363FC2B21DCED37D47730E4B96ACE5A0A7DE87AA488D7E9A354A7C81A64580E3C3E4A64C41A0DD0F79CF028FC02F46B5091F8D6B676DA5F6DF675693B953B5E5AEF5777ABF6C7C5C5CD3F59E130FB15E659A1C30D4B59E8F64A8EE89F437A7C33D1477A102405
	D0D6B290774AED64D38C6FB5G268C01BF0C78A5703E82A0A603F47B7569CAD5F6076D4B525A12112434903AC919DC2742CC3FEE0B224FE4E4B3BFAC10515E96281B899086308CE08D0034A3BAFEB7737FD316C3702C6AD22ADDFDFDAEE7155BFD56E171DCF2D9E4873CD7D4C00D0AEB123BDD9E0BA4F19FAFB4968711E7A260F45F3FE3BAEE137947485FF3639F13B6FC27AFA9B908CD5CCB071232F6F0DB146E705B04737EEBC85E0DBB460E47313C5BA4ADEF5F3ECFA5A58661DD5373DE12C56D4727613DF087
	7A3A75907AFAAE3C4729366078AE060F556079587C8B9BB3D8CE59999D6F5EC9E3E3CEA14912726083D218559E548E0DBF79C4D254CEAE433A1D0132180B316F2CB7B84F4DC296A7439F5760D5D958B845F2A354AD826063EB319567EAD6964C9DG830085E086C0B84022AC9AE39BADA5D39CE30DE60FF2365F6E30489E0953AE16F742D376A99EF33D532B181D5DF22B59E3B7F7B9E42F2436793E0CB6689AC377B49B7B5D40633C6C14BDE6BED3BD66BED911BDDE5EE6393C8D5BF8E4D6EB34774940204FADD1
	3FD1F2865A406CD5DA5C9633A27BDB707AE899EDB2E0EE059E2183F867F679358D6D95857A2F8358A86C5056037DDD14BD90B0AAAA8CAED7EF3F5B081AC806EC34F3EA8B460EF4F8AF4CA69B3FE0C75CECA84F4F26FEBE5D33DD53CF0D1D05A133E7086618090D292550FE8DC0ECB60D1DBF345358597052E3D26437EE869DBB883ADDFC4C646E8A1CC3B656B74AD787F536DD94737F6FF63BCA0BBCC34D65D835EE6984EDC2470E6D6F930F44C95A62B3618E7ACF953EB37422FEB3C12FF9G913B48664F746472
	B1FC566C15E954B6F8E04049963F87381C5662F4552E892A2C49C2870555C5C33F1BF6C747978398F78B9B9EEE8FC2F7A7BACC4106456D817DEF7E90F9F0DB7D3B9557ABF9C0F6AAB532DBF6DA60452B567D580A7C54795C6392B44B995E8D733F9AA61CD7601F33629CB5AB0A475E552F484D3222581DD6FF7DEA464B5B5F6576078E62752295F5F48D427C976DFFEB4539536DF2BA796AADC8547A98B6F7E1ED6F83212BDD7DCE05637FE0439C27D79ED2E3471FEDC9924EFDC7BF46A11EB7ACB43F0EBAF86CFD
	B094B014C94BF7633CBE5012253151B07A9CF3B0F8AE5ACDF3F3B01F66262575912974032BC15718595B6ECD4A4CD1577C9C7F5C64B27D469EDC765F339822623DDC562C37B02E06CAE692981D446C2700334AA442456FA75CD1B1E7E6BB10EF9D48E382D0F2E84E48B7311E6214BAD92ADD8E159754AD4AC17BFC7DD0262A5FACA84F4B6176C995758D0729FEA314B377786BB91F26DE1CD3DE79D63F8C4B8386540B42CF9D3D5B836ADAD51F71FAF3AF7A4C210945EB76A04F7945140796C039924024728D5B43
	F973F60EA3542EF8AF64F7FB7D6DACFBF93BCD7B30DD2E1E5AADGFC84405A3DBC064642F32FE87732E85788E55BBE5E4E26E9477D7717A29CD3ACC73DE99EB07B745EC118979595971887F9AEFABFDBCF307D4CFD14F3BE510E3EDA8665D5G6B7709356192651CB7DE7922147B42D048FCDA174B75AA4E750F3F70BFD14E391A6DB19CD0B71CCB63A03AD73B6ED5F93D2EEEBBDBF2D59FBC62500ECF0EFB4C31851EBDF60FD72907G62A3FFFD4141729233C02BB9CA94DB17F00C4E863E11G52FE44EFBB0C73
	96ED487574EDA7BDD7B89E10F24E48C92F075033E6BF574F325F2FE72A60B7F0187805C1FD54FE62771460475B2DE4B9957AA0A58F4687C0F49E62AF26E5EAF88D83EE9EC0A4G1FE765C7F0FE1959C45173C7236C69F1F97AE4CBA1FADCEF9ED474AC466A2BD8F5AD3E975178085F037144EC311C6DD7941773FAD5C0970650FD1CACEA6536129A4F50182DF9DCEABF648A3A8FD9EE37A59564F39B99727D36BA9E7E391D739E2C4A60F6F8788859E1AE34D911CFF678019C4C8E3A825A0BBDF775A4BCBDF21FEB
	C01E16AA2A3F6A75642F9C685BD8C07CB78EE9794FAA60395742827243C166CF0CF73C9C25E7E51B5D4BEDF4792056C6050B67863511EE09B5A922C04D478B7CB6DA7A74750C95GDC460C92123172G4C5783A4E3646DE0B2AEB8C0F9431C1640FD4D32837E7CAA6A4078DC0B677BFBBA10DF4C811E6F3FAE700799DE77269F4F65FB12458EAC97C25D2CB7A9C7CDF4E3ACDA8F651403517185G15GA600610394830A7A0EC9136C816A1D101ABB4D0E66B10B39BA6FFF5507F60175D7AD7F268F4787DBFFA54A
	EDD69FC2BB4CBDB8755C125345B03A05F7C047FDFFB1DBFB8631B1663E2404A03EE7EB055A777A03FE5FF35BFD6B967A36349076CFG9600E10030C2321B4DD9BC195DE8BF249A0C6BB4FCF8FA36781743F7EA8BCE77FCF16098DCDAB8360FD581E5CE7752B83A3D81650D81F4BCAE24B5237D022CFFBCC8C09D4F3DFABA82F3AF1CBB19C57084606B64BDE9BAD51EB3C534CEBCFAD43B9E8E1BCCA15789DF9117EFEE115F376B4599CC3B975773D5D0978B10F288788298GECG338E111F97BBCFCD66E796510C
	2EEA4055C13ED29D18438A1BBEF6E4FA3EF8E29CDD20AF969C9ACB476D37C1A67BC59C221854201F6C1CA7388C1D1352EDBC94ECBC7C25A7C4BEE4B0F7D1BEF46CB64919F918E257EA857DE300328D60AE00B000E5G51GD2B179C767BABB19FF2AE813E2F40957A0CE62FAE617CC4FBE87A7254BAA8E6623FF885DCB211E1FB7FC0D6507B6A88F8B5CC09B6242213C2418FC195F01B1AA9A4A6B81F4C72263F3816A0ED0FD992B47F55B813859C7F8FE0B39AB2FDF5C5E2E11C72752D2997B4DBDB2F93CD9DF9C
	3C8D67719FE3F8A06EF5CD996372D47639214E957898C8F5075E576EF0875F576EF6875F576EF7EF63F868C3FB6AC575C56E09773DE56EE07B5EBDCC571F4763BA6D017D1F3DCFE6EC799C11FD7B7998EE6AF772E10C1C3D016B08C47B3A2C814CF5969D21B970937751A0F32056E2D7DC9E6A44A81BBD8DBDBDE282B43B65EEF1CEF7AB347DFEF4AB387D1E3D955CFEABEFCDEC3FDF5C8AE5BFEE23E7F626B1FA2FD7763240DAE3D64CEA5D6F149DD210B3024B66BED922FDF06A114DEA1BDA45D5DDA6665967BA
	E84F3562886D39662432FCCA8F639C20DCCF736213E95A395C28D7779B6A53279FBF5E39DF2E8C62FC1B8575738116812C825820A7BF39BD1366DA353E89F22DAEEFE8BF713CF73C1FDCDE5CAFFADD7D1EEEB95097835EACD1678F08750A5C07A665EDC6993DE276D865F13E3C0F51733AD07477FBC3791B7B6B39B17EBAAA62462B325687D9A56A5ED6FD1EA9996F835E5FFDFA6A2FD1F1B516097E4AEFEB7BB32A74B675B99C24BFAE7FDAA5344D7FD789EE73BFA921ED3EC1195C66F17D21ED2E6B1F5C665B7A
	B146F659ADF60FFA2243ED53D822350D5F561B5E575AE636EA0BF96AF3C9C8DB7F30854F5A9C66A1D73FA2C96525B827FEA14E5416835DDA009440671672BEF83C7B66A04A5763B17741D0E001A8338CE97F2D0DE86F0276F3G9681C4814416523E6F41E3A8B3BFCDA9D365C89B42F9FC21395B63F2B830260C63AFF784600F2A78FDB8DFDB1C90E46CD627EC411336BA598CE6E6C90950EF7DA17C96DB9522954FE39F9E51663126B21E474E04E798GEF73EA50B60B6099D9C631A1E7E02BF61E28B10D7B67CB
	9D0139451AB23FCFE24B467B076F97F5E9AC3E9D657B45C4014FE0789C8D1E6FA39FCD253300D2281BF314F20F4F31BCFC8314F54788F7086516B9D0AE96387BE54AD14A0F11DE9D0321674F0D41607327E7B0785A62981CF8EDD186C3459AAEDFE68F69E192B27F7B872407DB60F2C7C80FE101EBE0F9DAB814978A1C1C0F3888A8C7895CCF7309F60D406DFE1FE8CB0F936E9DC1DBA3F00F8A5A4663F4CE9557CA5F424381376AB81D3F145D407CD86455F84CD2D1F15EE3371CB5DB8DECC672F52F3ED5275227
	1F20F5EF1075D987659BG3E9334373A1DC0FB2B0DC5DF129CC97784BDEB4C792EBBE97689759C7804662C9176D61C67D3ED44F3BD54973E0538975B34E7A5B83F4A62A5C98FCFDE1F512E2DC7BF96C95A7A5AB175A86F7E84ED7D60187AC48DBD576D70B5524D7856D72442FBFBC21E233A5F62BA4DD11F73D2D7A6AEF1FEEDA7373B35F60F94F4BF630FB292659CBFE36388477F723748769B2CE87B5237A157863871B66DF5CF0CD2DB5F5B547604895BC6C3B996A0251C4649F7D8AED3G65F201FB4DC4E347
	D4CEF9CCAA1B378B209CA1F0598C076766F8A64EF161295A9C9D5B864B35970440CF861C4737477088154F5F693741316B51E2F62F264765D4325EC11B14EDC3DD23C16E18F2DABF2AE4BAB7B43D03F7E64E0D606888E76D3FACE7CD400377F90A451098F80F8560FD6C9D757771360875859521564D1E5BE3659CE631259C5A57D4101C4379E3655C49C6717C8AB61772EB547E14A0BC62A47F37CA04195086A1DF35FBD8850F7BF1C2279A961F5683FEE3856974878D3FEF8AFE45A74773CB1C045F1913FEFEFC
	ACA437CE6F0E4A0EE952DD3EE3BA3ECE5637617CB3E8646778A606EF5760F92C7EC044EA8B54C51E24330C68B89C934515B06781EEG78G6682AC2A24F5F20B2FD41AE4EFE0F4398D3010BAB4CB9F57EFC95B746E6EA49BA9A64EF972C1295D519972ACAD32127BBDC6FD16CAAB93714975F3A520FED92722630F8334833881E6G041DA27D1AFC6AF9A9EECBE197EA6E36312FCAE213EB17877DE8CC8A39EE4F0DD05CD888FDC51C8AB8AF03723AD3634724746EF8DCA6608A83705C1F8E76ED3972DD6E4F8401
	9FE0F803864F7D7C1A153E871820EE593B94C7ABC6C9560253901BCF0F65E500720D536365D20260E682AE6CF4B0D91FE0EB4242530132BE45704BCE874AFA52C4322E003A63E7A83E2C6C981F3B4708B3593950EEA140AA00F5E7389CBC966C6824D83029AAF8FC538245029D6A384A2A724F99AE479F0BF1ACD65177B0010FB901F2B7E93EAF7138F4105DF964ED764AF970F44A033962CB53B9882A05F9963B596132669567169667169DAF22717E22247DAEA0AD1D9D74DBBE731F2ADB397A5C1031B21177D1
	D22E6DE3511E4E243890FD38D55A61AAFF0E447B783BC71B0BEC1C77C850DC4478E6EB52C215B6CA5387BE39AD52377AED715487D335854FB7BAC768BCC4F716E27E15B4FFFF9D79F7565F76826A6F8C74370F7581FDCB7EFF1C47E93C92207AA347CBF6D8F77E331265FFFCC979A1EC69BB4B750FBC9B4C8E1F5C6E177B489D4A7D6BFC12DB2AA6BBDCC751553523214D7F5E2EF93F3735BD081C9F96F09EE19FA98F9E17DF0A8B3DBFDB9797FCFF766738607B338DF1931F0DCA71216F84A547EB6F8471F525A2
	93F37AF2700B914087B08BA03C1A5614076351577C04BFF0BD2935D871FC85EFA6AA445F1790DA575104496FB34ECC403B8FDDAE08C6FD58BF61BF1D007ED2DCEE066438079250C69E3B55267026AA7E33896C9E065C4350BCF70FE5F3F1A96835261A7279CBECBF908B651C9A42FD0561CA21DCA7F0794908B3C159A7F08F6A90978665A582F75DCA7B036582F79FEB17D38B6BC2AD2DE19EF61F2ACE1407D959A24A1BEC130DC9247D67467D1EC9F9CCF71F0040F5B70D0E3779A702587C9B4266B3EA89774688
	59FC2E400D0E124D178A5CF31DE4739582F7738332F96EB9326913A63471F1D17EB6AB9BC5394A243587367B70E34D55B1BC7E997B86831E0B7C78AA65478A6830609C1DE57C6775B15F0C7DE79986B5BDD1E3F56AB5349D4E2B3567E80F5C49721B1C73B006818C67E9BFFD2E13623A65BC4575FBB08DD1E3CB49CE66AF28DBF21E6249206EB0BCDB4DB747FFC181E423476516BD4A9047325BEAEADF6C18D01D5F5F539D885587C4258B20DEC357CD39DBB11A2DC296C39666DD2BC00ED8002C3A6878D2009AG
	D39D4936F5F3618432F1C331F867B15B1D8A77E95708F6DF60063EE654CDBCF6B85D9B85DA3A39C033E092BA2E4742EC4C8D17C15BE8G299EF481A8862881E887F0G5C85B097E0A1C08440AA00F5G9B8152AFC44797DCA01DE72497CD2033FA5865D7DA487F3BFC2D7C65501F61424472F3FEE7B3C32F8F7519132F8FCD196C3E9C44F56310DBBD0BEF4B44BC5B5553631545BAE44ACC9738FAEF234BEB47C3FA1E3F3CBE0A03E9G861A3D5B6C6007CAE74D1EC617435EBD246E575FE43186EDF36382657316
	D174014D65315F06F4E7BCA56D756FE234278142856D77C7F15F57E776597B7A7B1A6D37E5356D4FD95BB550EE1DE8DB110C769A005D1A5B850B384B438EE0797E21B919768FD997E18C83D8GFC97396DB97D93BACCDA5D9E39C776F8E48B63C6327D102DC73321FD98G5FEF8CD8E93F91F9B178D947C47B0D680B017B0D47F5CCD73BB3C057450CF78C340F3DC83A06B7A00F315F8920CFF6776B6BB66DF9E32E01FFE32F01A76F6FF9E63BF6A8DBG38FD4E0833215986EE1F7970DCEA60FAF13A790CAE8AF0
	EB8CE4035F0BBDD74A256067AF9359A077D260F94FC3EC8DAA86FC79A552752F6FE392CD5FBA1AA01A48DE2D3EAF1B8275457B8416CB34C6AD3FBA3D33195553244B382625738558E5961443B538C5704E7749AB59FA91F9A970CCE7B543C7DD8ABC5369B6511A35866AEAAE133E97BB357C6783FE696529441863BB71DD6448FC99D16BCE6EC49B4350D4E02FE23F4D2E62B6C3C327155737E4050ECF262C4963131C95BCFF6D4D9A1F3F3A3302672F83D9637357A1168F2DG7D2B62A83F9AE1389840E16C61B8
	FD8135DBA9F0478A285DFE41A3A2BB341E4FE6CF2EE7D4F6F0BDD7E70F5773D7594175FCA9DB2BA7473D1C0DF34D8A6BCE3BE877C7016B9038D856A7625A6C9645A670FFE3F2A13ECEC606E2DF348B659AE438F695172841F52838AD3BF0AD8115A14C93ED5AAEF616ECF7B8F8B9033531BBE1E99176485A05B25A5DE60D5CFB842EC3786235FC724FB785AEE10CFFF82CB8B5FA9C1E1A4F9FFA4D578FFDEBED13313E3A4AF03D393A2941E0B8DB55F43D2A797A655A5A1A5A9ACE7F195123935057B5B455DFED38
	EC9C534365DA6816767BD52802AACBD6F22E94305F2F3143CE907F702B306C84EC760F16169671397CFEB24D65CD8D5171158D14FFA666E24E9678CDC75F4596370A0ADA255BA6DBE829D3F3512713699B1752C039681FD87F8B209C81905DC0F9E7C10EDFDE23F5A2F90F2972F29EDF31D28E9BAB7ACA464FCF2A9DFE35475F6FAF2754AF57775922B702680BF772B060F35EDF6DC55E38D696B7926F9F6E7573CB99193A5DEDA362DC9C7A8AEB249CFD618859B02A11EC7882EB1BF28572C0004AAB447B51FDFE
	5EBF691C3A8DF73DC7B6B40A3E7EFE51EF437FB2E84612E96A7D6EE77B8BBC6F18F5C5FC93E77B1375D0161A8867E138CCA897885CF7982E9C4AF582974F3E5F19206CEE22B566F9934A465F57E85E6D260F7E6C691E0F148757392B13624D7382E76D24F87330C4390D79CD9C936A3A48C2AA476778367A7194D2D5796FCE1EDEDEF17FB469A26F180E679D328EF51B5194180FB899FEF6D3E0BE72D791434220CEEA263132402A65EF813C2FF9AAFB3DA7FB09EE96348F8F20D98465C800E88D1E53B9C726E727
	61E952AD3EE3BA75DB5DA85AEBFDF320FD2FB17C4666C07B2E9531CAE7041C5EC8765D771E167F8A402FB5CE453EEFF6EA675106G1A141660FD64827E38DAD727BECD70F4EB5A4FE8915F342FE18E60F18DFAF9FE5FF60DE25EDC5166D32C4D00556C66EDBEFF0D761073C51B152C0DCD34795DB50A4FCBC41BEC936A556DF27477B93D6AFE2254C43F2D9059C276E24B877DED2D667D55640FBEF66D54FD74316BE9AB452EC41173C6DAA9F6BDA0F0BF6E25F341D5AD946B4FEE21FBC86B846E06114E8A25D642
	5D5DC2E70F8582774BC2BABFACEFA5DB370F7A6DEB563C3FBC72515BBAE174233775DB6F0D37754977465B3A0E2D9D86300BCD586B21CE326130404D9FA61F4C9638889379E41E406D4FC35C92A8C7891CA30F7C94A3F01F49A71F2434916E01A14465C239320D660E739A4EA54CFDF979DB5770C7DB0E16D279B5D6DFAA6A4F1B300CF7D879BC7BA39BCB0D501F290D4E99FA59D81A8965B9G7C9E652078BD1788402D8258006D5BE14C34935DD75958EA0732C560B695317397A80735134C57BB311F9E3BCF36
	D44B8E073A21616DFD4C367F00FF91220F258C978565B582371259BB25834A9D04FB05593B864A0D9D62EFAB73F05FB7E0775AE1B757649AE4FFAA2D723A426C0EE79AF7F5101E7766097394404D8308GD88B3069AA71D946FCD3G65522B44E77E903B1FCBFFDB0D17AC040DFFB0C431337DAA650B1BAFCB42A7EFEF4915C26708C77163E2F9E991271B516827B39A6F6C3B6463C60A3D61D7698E5D334640BBF476B86D9D38D70D01F768105EDFDF5292F8076EFBE36A4DAD01F768081E6BE33A6257E7C561C4
	F620DC19539DEF72533DB812931A8E28CA0BF83B7BBBF1EE9CA9A35EA94D7EBE9E9F1EBA6F359A3AEF1B76CDC2774E31A2155F458EBF5D0E3C297A0E373765216CE525644BA772E8CC2D923EFCB1AF50178F0C71E5C67E443E2C4D8F74E562987A417C09FD293F6A576F13C3D373492A218AE96C3EFD4CEF94304BCA7ACBAD86E3FD232176FA7DE5E3EDD3AB6E62EB8D3555467A064B34E6FCF11A3FD1740DBCADDD7AB5D8972FCD1C0371D87C233C716B47CF73346B877F4D2D37D286015102D724C4C5CA2A32D8
	F25FFDD711B6D7D66672EBA80A146EBF8B4EC555AB94A935EA8C426B11360E99A4BF7A42095FAEFF21551BA0A555CB59F8672FD9763063BD5902FF5A227EC64DB9E9335BE507CA4057CB1B3C6C1168D5069C32C14A6CE9425FC970AA32C57C7D8EEC54F730BF30AE411F3FAA29B3FBED8A9E021DB4C81B1C78EBBA52F6DE6DB0BB2DA57C9471248FFFC3A72E6A863A1E59C782F9B3183C580BDE7412715D34837BAF2CDD12A0451F1336B8DCE64BB9F337627254CB1B951BC7765ADC8E0B4FFD7A8C6BC8B7C8C785
	89505DEE21BEAA2BB74ABE7F9F60757A9E1F35B5E66BB998FFBE0FD442DA6295C73DE8E1293619BD40CA7654AB786BC0AECF01283BFB2F72664F4E3FE10A1762D4A5EFC9A3D2124F8D5E6AB177599DC37042272497EB360073940F5969453FC78749F283A423AA41C07F50B3258FBF71372B0C011FD41176F1D382E909D5F67515D47574581D3268G6C8A5D6F915DE39BFDC01B6D3E87F6DC786FFFF441B82811927ACACB597FE56C7F2378FF994314B1CC59D1B06EFAE942FF0175BE52198D9E10A70D49036E57
	1F838FAA2BDFF9660B3F2CDC3F8E34BBA7A5323F5086A7247BDBDD6E6F6B12BD77ADF8F84F6BF3567CAB7A480DC345077D26726EB70D9BBF08550D9F8808CE916E40D29AEB3A0D7D1FF9474AA155CEC61BA5E45A73B114A95FA7AC0FA3211E1D432E99B8D611701BAFA544CB1B6A25A4FEAE8D139C8F17E112333367FAA9C1F139597C44D83DC505440F1F060739558F861574E0E017E00D6D6397152ACF7F62798557EBB26325F8B008CD6696C89C44641259A092609156E319DA3AE7822EE35B14FD8874BFGF8
	7F63E77BD33E9072CAC232603A5C737153653CEF2C746A1D4025355A7B15B91937C25E5134F52B77AB3B83FF3B41104E76ED000F6A263B84E1E9785B11216EE5B477199D8E456C682DF2DA700E4659A1C5765FDB3B120E7DFB290DDA496B5A53D16E1B9A1A7F87D0CB87887A3C4C90DD9CGG9CD4GGD0CB818294G94G88G88GC8EDFEA77A3C4C90DD9CGG9CD4GG8CGGGGGGGGGGGGGGGGGE2F5E9ECE4E5F2A0E4E1F4E1D0CB8586GGGG81G81GBAGGG179CGGGG
**end of data**/
}
/**
 * Return the JPanel1 property value.
 * @return javax.swing.JPanel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JPanel getJPanel1() {
	if (ivjJPanel1 == null) {
		try {
			ivjJPanel1 = new javax.swing.JPanel();
			ivjJPanel1.setName("JPanel1");
			ivjJPanel1.setLayout(getJPanel1GridLayout());
			getJPanel1().add(getAddButton(), getAddButton().getName());
			getJPanel1().add(getRemoveButton(), getRemoveButton().getName());
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJPanel1;
}
/**
 * Return the JPanel1GridLayout property value.
 * @return java.awt.GridLayout
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private java.awt.GridLayout getJPanel1GridLayout() {
	java.awt.GridLayout ivjJPanel1GridLayout = null;
	try {
		/* Create part */
		ivjJPanel1GridLayout = new java.awt.GridLayout(2, 1);
		ivjJPanel1GridLayout.setVgap(20);
		ivjJPanel1GridLayout.setHgap(0);
	} catch (java.lang.Throwable ivjExc) {
		handleException(ivjExc);
	};
	return ivjJPanel1GridLayout;
}
/**
 * Return the JScrollPane1 property value.
 * @return javax.swing.JScrollPane
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JScrollPane getJScrollPane1() {
	if (ivjJScrollPane1 == null) {
		try {
			ivjJScrollPane1 = new javax.swing.JScrollPane();
			ivjJScrollPane1.setName("JScrollPane1");
			ivjJScrollPane1.setVerticalScrollBarPolicy(javax.swing.JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
			ivjJScrollPane1.setHorizontalScrollBarPolicy(javax.swing.JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
			ivjJScrollPane1.setMaximumSize(new java.awt.Dimension(150, 200));
			ivjJScrollPane1.setViewportBorder(new javax.swing.border.EtchedBorder());
			ivjJScrollPane1.setPreferredSize(new java.awt.Dimension(130, 200));
			ivjJScrollPane1.setMinimumSize(new java.awt.Dimension(130, 200));
			getJScrollPane1().setViewportView(getUnassignedList());
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJScrollPane1;
}
/**
 * Return the JScrollPane2 property value.
 * @return javax.swing.JScrollPane
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JScrollPane getJScrollPane2() {
	if (ivjJScrollPane2 == null) {
		try {
			ivjJScrollPane2 = new javax.swing.JScrollPane();
			ivjJScrollPane2.setName("JScrollPane2");
			ivjJScrollPane2.setVerticalScrollBarPolicy(javax.swing.JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
			ivjJScrollPane2.setHorizontalScrollBarPolicy(javax.swing.JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
			ivjJScrollPane2.setMaximumSize(new java.awt.Dimension(150, 200));
			ivjJScrollPane2.setViewportBorder(new javax.swing.border.EtchedBorder());
			ivjJScrollPane2.setPreferredSize(new java.awt.Dimension(130, 200));
			ivjJScrollPane2.setMinimumSize(new java.awt.Dimension(130, 200));
			getJScrollPane2().setViewportView(getAssignedRoutesList());
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJScrollPane2;
}
/**
 * Return the RemoveButton property value.
 * @return javax.swing.JButton
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JButton getRemoveButton() {
	if (ivjRemoveButton == null) {
		try {
			ivjRemoveButton = new javax.swing.JButton();
			ivjRemoveButton.setName("RemoveButton");
			ivjRemoveButton.setFont(new java.awt.Font("dialog", 0, 14));
			ivjRemoveButton.setText("<< Remove");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjRemoveButton;
}
/**
 * Return the text1 property value.
 * @return java.lang.String
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private java.lang.String gettext1() {
	// user code begin {1}
	// user code end
	return ivjtext1;
}
/**
 * Return the text2 property value.
 * @return java.lang.String
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private java.lang.String gettext2() {
	// user code begin {1}
	// user code end
	return ivjtext2;
}
/**
 * Return the UnassignedList property value.
 * @return javax.swing.JList
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JList getUnassignedList() {
	if (ivjUnassignedList == null) {
		try {
			ivjUnassignedList = new javax.swing.JList();
			ivjUnassignedList.setName("UnassignedList");
			ivjUnassignedList.setPreferredSize(new java.awt.Dimension(120, 180));
			ivjUnassignedList.setFont(new java.awt.Font("dialog", 0, 14));
			ivjUnassignedList.setBounds(0, 0, 94, 180);
			ivjUnassignedList.setMaximumSize(new java.awt.Dimension(130, 200));
			ivjUnassignedList.setMinimumSize(new java.awt.Dimension(0, 0));
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjUnassignedList;
}
/**
 * Return the UnassignedListHeaderLabel property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getUnassignedListHeaderLabel() {
	if (ivjUnassignedListHeaderLabel == null) {
		try {
			ivjUnassignedListHeaderLabel = new javax.swing.JLabel();
			ivjUnassignedListHeaderLabel.setName("UnassignedListHeaderLabel");
			ivjUnassignedListHeaderLabel.setFont(new java.awt.Font("dialog", 0, 14));
			ivjUnassignedListHeaderLabel.setText("Unassigned List:");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjUnassignedListHeaderLabel;
}
/**
 * Called whenever the part throws an exception.
 * @param exception java.lang.Throwable
 */
private void handleException(Throwable exception) {

	/* Uncomment the following lines to print uncaught exceptions to stdout */
	// com.cannontech.clientutils.CTILogger.info("--------- UNCAUGHT EXCEPTION ---------");
	// exception.printStackTrace(System.out);
}
/**
 * Initializes connections
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void initConnections() throws java.lang.Exception {
	// user code begin {1}
	// user code end
	getAddButton().addActionListener(this);
	getRemoveButton().addActionListener(this);
	getUnassignedListHeaderLabel().addPropertyChangeListener(this);
	getAssignedListHeaderLabel().addPropertyChangeListener(this);
	connPtoP1SetTarget();
	connPtoP2SetTarget();
}
/**
 * Initialize the class.
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void initialize() {
	try {
		// user code begin {1}
		// user code end
		setName("DualListSelectionPanel");
		setLayout(new java.awt.GridBagLayout());
		setSize(400, 280);

		java.awt.GridBagConstraints constraintsJPanel1 = new java.awt.GridBagConstraints();
		constraintsJPanel1.gridx = 1; constraintsJPanel1.gridy = 1;
		add(getJPanel1(), constraintsJPanel1);

		java.awt.GridBagConstraints constraintsJScrollPane1 = new java.awt.GridBagConstraints();
		constraintsJScrollPane1.gridx = 0; constraintsJScrollPane1.gridy = 1;
		constraintsJScrollPane1.insets = new java.awt.Insets(5, 10, 5, 10);
		add(getJScrollPane1(), constraintsJScrollPane1);

		java.awt.GridBagConstraints constraintsJScrollPane2 = new java.awt.GridBagConstraints();
		constraintsJScrollPane2.gridx = 2; constraintsJScrollPane2.gridy = 1;
		constraintsJScrollPane2.insets = new java.awt.Insets(5, 10, 5, 10);
		add(getJScrollPane2(), constraintsJScrollPane2);

		java.awt.GridBagConstraints constraintsUnassignedListHeaderLabel = new java.awt.GridBagConstraints();
		constraintsUnassignedListHeaderLabel.gridx = 0; constraintsUnassignedListHeaderLabel.gridy = 0;
		constraintsUnassignedListHeaderLabel.fill = java.awt.GridBagConstraints.HORIZONTAL;
		constraintsUnassignedListHeaderLabel.anchor = java.awt.GridBagConstraints.WEST;
		constraintsUnassignedListHeaderLabel.insets = new java.awt.Insets(5, 10, 5, 10);
		add(getUnassignedListHeaderLabel(), constraintsUnassignedListHeaderLabel);

		java.awt.GridBagConstraints constraintsAssignedListHeaderLabel = new java.awt.GridBagConstraints();
		constraintsAssignedListHeaderLabel.gridx = 2; constraintsAssignedListHeaderLabel.gridy = 0;
		constraintsAssignedListHeaderLabel.fill = java.awt.GridBagConstraints.HORIZONTAL;
		constraintsAssignedListHeaderLabel.anchor = java.awt.GridBagConstraints.WEST;
		constraintsAssignedListHeaderLabel.insets = new java.awt.Insets(5, 10, 5, 10);
		add(getAssignedListHeaderLabel(), constraintsAssignedListHeaderLabel);
		initConnections();
	} catch (java.lang.Throwable ivjExc) {
		handleException(ivjExc);
	}
	// user code begin {2}
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
		DualListSelectionPanel aDualListSelectionPanel;
		aDualListSelectionPanel = new DualListSelectionPanel();
		frame.add("Center", aDualListSelectionPanel);
		frame.setSize(aDualListSelectionPanel.getSize());
		frame.setVisible(true);
	} catch (Throwable exception) {
		System.err.println("Exception occurred in main() of javax.swing.JPanel");
		exception.printStackTrace(System.out);
	}
}
/**
 * Method to handle events for the PropertyChangeListener interface.
 * @param evt java.beans.PropertyChangeEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
public void propertyChange(java.beans.PropertyChangeEvent evt) {
	// user code begin {1}
	// user code end
	if (evt.getSource() == getUnassignedListHeaderLabel() && (evt.getPropertyName().equals("text"))) 
		connPtoP1SetTarget();
	if (evt.getSource() == getAssignedListHeaderLabel() && (evt.getPropertyName().equals("text"))) 
		connPtoP2SetTarget();
	// user code begin {2}
	// user code end
}
/**
 * Comment
 */
public void removeButton_ActionPerformed(java.awt.event.ActionEvent actionEvent) {

	transferSelected( getAssignedRoutesList(), getUnassignedList() );
}
/**
 * Set the text1 to a new value.
 * @param newValue java.lang.String
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void settext1(java.lang.String newValue) {
	if (ivjtext1 != newValue) {
		try {
			ivjtext1 = newValue;
			connPtoP1SetSource();
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	};
	// user code begin {3}
	// user code end
}
/**
 * Set the text2 to a new value.
 * @param newValue java.lang.String
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void settext2(java.lang.String newValue) {
	if (ivjtext2 != newValue) {
		try {
			ivjtext2 = newValue;
			connPtoP2SetSource();
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	};
	// user code begin {3}
	// user code end
}
/**
 * This method was created in VisualAge.
 * @param src javax.swing.JList
 * @param dest javax.swing.JList
 */
private void transferSelected(javax.swing.JList src, javax.swing.JList dest) {
		//geez there must be an easier way!
	java.util.Vector srcV  = new java.util.Vector( src.getModel().getSize() );
	java.util.Vector destV = new java.util.Vector( dest.getModel().getSize() );

	//Load all list items into srcV and destV
	for( int i = 0; i < src.getModel().getSize(); i++ )
		srcV.addElement( src.getModel().getElementAt(i) );
	
	for( int j = 0; j < dest.getModel().getSize(); j++ )
		destV.addElement( dest.getModel().getElementAt(j) );	

	//Remove all selected in srcV and add them to destV
	Object selected[] = src.getSelectedValues();

	for( int k = 0; k < selected.length; k++ )
	{
		srcV.removeElement( selected[k] );
		destV.addElement( selected[k] );
	}

	src.setListData(srcV);
	dest.setListData(destV);

	revalidate();
	repaint();
}
/**
 * 
 * @return javax.swing.ListModel
 */
public javax.swing.ListModel unassignedListGetModel() {
		return getUnassignedList().getModel();
}
/**
 * 
 * @return java.lang.Object[]
 */
public java.lang.Object[] unassignedListGetSelectedValues() {
		return getUnassignedList().getSelectedValues();
}
/**
 * 
 * @param text java.lang.String
 */
public void unassignedListHeaderLabelSetText(String text) {
		getUnassignedListHeaderLabel().setText(text);
}
/**
 * 
 * @param listData java.lang.Object[]
 */
public void unassignedListSetListData(java.lang.Object[] listData) {
		getUnassignedList().setListData(listData);
}
/**
 * 
 * @param listData java.util.Vector
 */
public void unassignedListSetListData(java.util.Vector listData) {
		getUnassignedList().setListData(listData);
}
}
