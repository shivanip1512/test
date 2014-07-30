package com.cannontech.common.gui.util;

/**
 * Insert the type's description here.
 * Creation date: (9/20/2001 1:49:55 PM)
 * @author: 
 */
public class AdvancedPropertiesDialog extends javax.swing.JPanel implements java.awt.event.ActionListener {
	public static final int RESPONSE_CANCEL = 0;
	public static final int RESPONSE_ACCEPT = 1;
	private int response = RESPONSE_CANCEL;
	private javax.swing.JButton ivjJButtonCanel = null;
	private javax.swing.JButton ivjJButtonOk = null;
	//dialog to display the panel on (leave null if not used)
	private javax.swing.JDialog dialog = null;
	private javax.swing.JPanel ivjJPanelOkCancel = null;
	private java.awt.FlowLayout ivjJPanelOkCancelFlowLayout = null;
	private String title = "Advanced Properties";
	private AdvancedPropertiesGUI propertiesPanel = null;
	private javax.swing.JPanel ivjJPanelDynamic = null;
/**
 * BaseAdvancedPanel constructor comment.
 */

public AdvancedPropertiesDialog() {
	super();
	initialize();
}
/**
 * BaseAdvancedPanel constructor comment.
 */
public AdvancedPropertiesDialog( AdvancedPropertiesGUI panel ) 
{
	super();

	setPropertiesPanel( panel );

	initialize();
}
/**
 * BaseAdvancedPanel constructor comment.
 */
public AdvancedPropertiesDialog( AdvancedPropertiesGUI panel, String newTitle ) 
{
	super();

	setPropertiesPanel( panel );
	setTitle( newTitle );
	initialize();
}
/**
 * Method to handle events for the ActionListener interface.
 * @param e java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
@Override
public void actionPerformed(java.awt.event.ActionEvent e) {
	// user code begin {1}
	// user code end
	if (e.getSource() == getJButtonOk()) {
        connEtoC1(e);
    }
	if (e.getSource() == getJButtonCanel())
     {
        connEtoC2(e);
	// user code begin {2}
	// user code end
    }
}
/**
 * connEtoC1:  (JButtonOk.action.actionPerformed(java.awt.event.ActionEvent) --> BaseAdvancedPanel.jButtonOk_ActionPerformed(Ljava.awt.event.ActionEvent;)V)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC1(java.awt.event.ActionEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		jButtonOk_ActionPerformed(arg1);
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}
/**
 * connEtoC2:  (JButtonCanel.action.actionPerformed(java.awt.event.ActionEvent) --> BaseAdvancedPanel.jButtonCanel_ActionPerformed(Ljava.awt.event.ActionEvent;)V)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC2(java.awt.event.ActionEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		jButtonCanel_ActionPerformed(arg1);
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}
/**
 * Return the JButtonCanel property value.
 * @return javax.swing.JButton
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JButton getJButtonCanel() {
	if (ivjJButtonCanel == null) {
		try {
			ivjJButtonCanel = new javax.swing.JButton();
			ivjJButtonCanel.setName("JButtonCanel");
			ivjJButtonCanel.setMnemonic('c');
			ivjJButtonCanel.setText("Cancel");
			ivjJButtonCanel.setActionCommand("JButtonCancel");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJButtonCanel;
}
/**
 * Return the JButtonOk property value.
 * @return javax.swing.JButton
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JButton getJButtonOk() {
	if (ivjJButtonOk == null) {
		try {
			ivjJButtonOk = new javax.swing.JButton();
			ivjJButtonOk.setName("JButtonOk");
			ivjJButtonOk.setMnemonic('o');
			ivjJButtonOk.setText("Ok");
			ivjJButtonOk.setMaximumSize(new java.awt.Dimension(73, 25));
			ivjJButtonOk.setActionCommand("JButtonOk");
			ivjJButtonOk.setPreferredSize(new java.awt.Dimension(73, 25));
			ivjJButtonOk.setMinimumSize(new java.awt.Dimension(73, 25));
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJButtonOk;
}
/**
 * Return the JPanelDynamic property value.
 * @return javax.swing.JPanel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JPanel getJPanelDynamic() {
	if (ivjJPanelDynamic == null) {
		try {
			ivjJPanelDynamic = new javax.swing.JPanel();
			ivjJPanelDynamic.setName("JPanelDynamic");
			ivjJPanelDynamic.setLayout(null);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJPanelDynamic;
}
/**
 * Return the JPanel1 property value.
 * @return javax.swing.JPanel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JPanel getJPanelOkCancel() {
	if (ivjJPanelOkCancel == null) {
		try {
			ivjJPanelOkCancel = new javax.swing.JPanel();
			ivjJPanelOkCancel.setName("JPanelOkCancel");
			ivjJPanelOkCancel.setLayout(getJPanelOkCancelFlowLayout());
			ivjJPanelOkCancel.setMaximumSize(new java.awt.Dimension(176, 27));
			getJPanelOkCancel().add(getJButtonOk(), getJButtonOk().getName());
			getJPanelOkCancel().add(getJButtonCanel(), getJButtonCanel().getName());
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJPanelOkCancel;
}
/**
 * Return the JPanelOkCancelFlowLayout property value.
 * @return java.awt.FlowLayout
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private java.awt.FlowLayout getJPanelOkCancelFlowLayout() {
	java.awt.FlowLayout ivjJPanelOkCancelFlowLayout = null;
	try {
		/* Create part */
		ivjJPanelOkCancelFlowLayout = new java.awt.FlowLayout();
		ivjJPanelOkCancelFlowLayout.setVgap(1);
		ivjJPanelOkCancelFlowLayout.setHgap(10);
	} catch (java.lang.Throwable ivjExc) {
		handleException(ivjExc);
	};
	return ivjJPanelOkCancelFlowLayout;
}
/**
 * Insert the method's description here.
 * Creation date: (9/20/2001 3:04:52 PM)
 */
public AdvancedPropertiesGUI getPropertiesPanel() {
	return propertiesPanel;
}
/**
 * Insert the method's description here.
 * Creation date: (9/20/2001 1:53:58 PM)
 * @return int
 */
public int getResponse() {
	return response;
}
/**
 * Insert the method's description here.
 * Creation date: (9/20/2001 2:17:48 PM)
 * @return java.lang.String
 */
public java.lang.String getTitle() {
	return title;
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
	getJButtonOk().addActionListener(this);
	getJButtonCanel().addActionListener(this);
}
/**
 * Initialize the class.
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void initialize() {
	try {
		// user code begin {1}		
		// user code end
		setName("BaseAdvancedPanel");
		setLayout(new java.awt.GridBagLayout());
		setSize(430, 180);

		java.awt.GridBagConstraints constraintsJPanelOkCancel = new java.awt.GridBagConstraints();
		constraintsJPanelOkCancel.gridx = 1; constraintsJPanelOkCancel.gridy = 2;
		constraintsJPanelOkCancel.fill = java.awt.GridBagConstraints.BOTH;
		constraintsJPanelOkCancel.anchor = java.awt.GridBagConstraints.SOUTH;
		constraintsJPanelOkCancel.weightx = 1.0;
		constraintsJPanelOkCancel.weighty = 1.0;
		constraintsJPanelOkCancel.ipadx = 254;
		constraintsJPanelOkCancel.ipady = 7;
		constraintsJPanelOkCancel.insets = new java.awt.Insets(3, 0, 1, 0);
		add(getJPanelOkCancel(), constraintsJPanelOkCancel);

		java.awt.GridBagConstraints constraintsJPanelDynamic = new java.awt.GridBagConstraints();
		constraintsJPanelDynamic.gridx = 1; constraintsJPanelDynamic.gridy = 1;
		constraintsJPanelDynamic.fill = java.awt.GridBagConstraints.BOTH;
		constraintsJPanelDynamic.weightx = 1.0;
		constraintsJPanelDynamic.weighty = 1.0;
		constraintsJPanelDynamic.ipadx = 430;
		constraintsJPanelDynamic.ipady = 140;
		constraintsJPanelDynamic.insets = new java.awt.Insets(0, 0, 2, 0);
		add(getJPanelDynamic(), constraintsJPanelDynamic);
		initConnections();
	} catch (java.lang.Throwable ivjExc) {
		handleException(ivjExc);
	}
	// user code begin {2}
		
	// user code end
}
/**
 * Comment
 */
public void jButtonCanel_ActionPerformed(java.awt.event.ActionEvent actionEvent) 
{

	//set the current values in this Panel to the original values
	getPropertiesPanel().setValue( getPropertiesPanel().getOriginalObject() );

	if( dialog != null ) {
        dialog.setVisible(false);
    }

	return;
}
/**
 * Comment
 */
public void jButtonOk_ActionPerformed(java.awt.event.ActionEvent actionEvent) 
{	
	//set the original values in this Panel to the current values selected by the user
	getPropertiesPanel().setOriginalObject( 
			getPropertiesPanel().getValue( 
					getPropertiesPanel().getOriginalObject() ) );
	
	getPropertiesPanel().setValue( getPropertiesPanel().getOriginalObject() );
	
	setResponse( RESPONSE_ACCEPT );

	if( dialog != null ) {
        dialog.setVisible(false);
    }
	
	return;
}
/**
 * main entrypoint - starts the part when it is run as an application
 * @param args java.lang.String[]
 */
public static void main(java.lang.String[] args) {
	try {
		javax.swing.JFrame frame = new javax.swing.JFrame();
		AdvancedPropertiesDialog aAdvancedPropertiesDialog;
		aAdvancedPropertiesDialog = new AdvancedPropertiesDialog();
		frame.setContentPane(aAdvancedPropertiesDialog);
		frame.setSize(aAdvancedPropertiesDialog.getSize());
		frame.addWindowListener(new java.awt.event.WindowAdapter() {
			@Override
            public void windowClosing(java.awt.event.WindowEvent e) {
				System.exit(0);
			};
		});
		frame.show();
		java.awt.Insets insets = frame.getInsets();
		frame.setSize(frame.getWidth() + insets.left + insets.right, frame.getHeight() + insets.top + insets.bottom);
		frame.setVisible(true);
	} catch (Throwable exception) {
		System.err.println("Exception occurred in main() of javax.swing.JPanel");
		com.cannontech.clientutils.CTILogger.error( exception.getMessage(), exception );;
	}
}
/**
 * Insert the method's description here.
 * Creation date: (9/20/2001 3:04:52 PM)
 */
private void setPropertiesPanel(AdvancedPropertiesGUI newPropertiesPanel) {
	propertiesPanel = newPropertiesPanel;
}
/**
 * Insert the method's description here.
 * Creation date: (9/20/2001 1:53:58 PM)
 * @param newResponse int
 */
private void setResponse(int newResponse) {
	response = newResponse;
}
/**
 * Insert the method's description here.
 * Creation date: (9/20/2001 2:16:54 PM)
 * @param title java.lang.String
 */
public void setTitle(String newTitle) 
{
	title = newTitle;
}
/**
 * Insert the method's description here.
 * Creation date: (9/18/2001 4:39:32 PM)
 */
public int showPanel( java.awt.Frame parentFrame )
{

	if( dialog == null )
	{
		dialog = new javax.swing.JDialog(parentFrame);
		dialog.setDefaultCloseOperation( javax.swing.WindowConstants.HIDE_ON_CLOSE );

		//remove the Place holder JPanel and the OkCancel JPanel
		remove( getJPanelDynamic() );
		remove( getJPanelOkCancel() );

		// if we have a GridBagLayout, add the real panel and readd 
		if( getLayout() instanceof java.awt.GridBagLayout )
		{
			//be sure the users defined panel is in the top portion of the AdvancedPanel
			java.awt.GridBagConstraints g = ((java.awt.GridBagLayout)getLayout()).getConstraints(getJPanelDynamic());
			g.gridy = 1;
			add(getPropertiesPanel().getMainJPanel(), g );

			//be sure the users defined OkCancelJPanel is in the bottom portion of the AdvancedPanel
			java.awt.GridBagConstraints gb = ((java.awt.GridBagLayout)getLayout()).getConstraints(getJPanelOkCancel());
			gb.gridy = 2;
			add( getJPanelOkCancel(), gb );
		} else {
            throw new IllegalStateException(this.getClass().getName() + " is only allowed to have a GridBagLayout, please check source code!!!");
        }

		dialog.setTitle( getTitle() );
		dialog.setContentPane( this );
		dialog.pack();
		dialog.setModal( true );
		dialog.setSize( (int)(getSize().getWidth() + 15), 
							 (int)(getSize().getHeight() + 35) );


		if( parentFrame != null ) {
            dialog.setLocationRelativeTo(parentFrame);
        }
	}


	dialog.show();	

	return getResponse();
}
/**
 * 
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private static void getBuilderData() {
/*V1.1
**start of data**
	D0CB838494G88G88GD1F954ACGGGGGGGGGGGG8CGGGE2F5E9ECE4E5F2A0E4E1F4E135BA8DD4D457192828A4A1EA54A45430E70D31A95DE59B52E5F76D2EB94730F6EBDB3BEB77180D4D21DD32E1CFC9EBD35312D312586DE500919564C7B490B552289510A2AA81C2C9A2B0D8B0B452C8A9B9C92A8F6641BC1819B73CF9438CBF6AFE5F3D6F3EF98CEFC0BC47B967BB6F5D6F5E7BFD77FE7F773EA1A5475766E4DACA88494CA15A2FA113102D35047C4F351CC7B8AEC2CC3292436F57GC564
	2531ECF82E00BE7541A4EBA1F941118B6D7550762CCE323E816F87C91AE3D38D3C04619383FDBB177A9E1E1DCF72814EE78FEDA76D4B03678100B84061B3DDC47E4F6DAB5570DBB5BC07CC8BA1BB86F14CAB7B0EEA38C6E8F7832C86089BE463B741F3AF653C3829D19B775983EBC97A37CEDB72110E01A60772A1B65726BD4B499319BB071057DB7B7334B9D350CE84404DE7114DC76A61595CBDB2747E006C2A99905CEE592D0A830E1AA11FD463D3A5E7D3D30BFDCCF08F0876EEC576080AAA09DE1BA4B865A1
	C29C8E24BD38BF1752A224965A6994774898720B07774DG49BA3E5F0F78C689768B6010789E5FBB5D2C6D21EF728912782CBB2BA04C9EA3A43637B8A93847553E577614D1FD9CADC43927C2DF6EF01275A840C5006BG91GC7289D1CA82802E7CB3F5AA63BDC323B4563E9F55A15BF48F651896F305FFED543FDAC8E480A1D90266B137B2BCD56B39B30F96767BD0F49A46E8257FB7C470F13643F7F503247C4A649F1351618E1A60B046120CC986F11303C378DCF374B693C7FCC0C3C57AF1133F2CCF8A7BED3
	E9C95568E498F8D70C202E8B282ECB61BDD3338F06AF23F827814FEC74D1EAB3581E023E1491EE9BFD8778DA3A32571042F3DD26F208C9EA31581CEC8D1FB9C35772DF14E71413714C515772BC45479A70ECAD8E0A47F682741538122C4C3ECE95234F86203D9AA09EE0B3C0AAGF143B8B737B1E741595B3431EEC1D1DBFD1253AEAA044DBDD5F8981EE2C0D504CE37D7C55F6B9594C968F70ADE220D29A9C49974CF1BF7015A7EG70783D689695C115E4B7D097DC22AAAADEB62639308847A8A26D6D11DCA2B0
	F0F98827EBABBC083A913C6A1FBCF6C19503A3D8FFD7A16AE4CC68850AC0G5E19DCBEBE08722A037DF781FC264B619B145E07226296A112F44972084F5303BBA171EE1473C29ABBBC709E6566B27E2884F13B201DABF3BA7FD7D8EC20E31386F07902B24EFD6C109F6372D1195B4CCBFD5CE6EC79DFA4554FDCB6359904E61959CA0F9C6ABB47A9CDDC97EBFFE92CEF9EBEFCD4B6772F59E34526F971481A4758833A2EB69EC2DDCD417AE2GB668B2FE37281C59EC2B60953915DED14040C4FBD062EC1D497EDC
	CD1ED97A5AFFC8F15B00DE064CE566767799464DE72D37AF33251487524F398BBC18EC4E96E3BE9247C437EA93BD225B8EAFDE2D2F3E98EDCE7357C1D91F53CC719E706FB6F0A82F0EEFADC69F94D4D5117AFD2AF8D5D4D549BD946C3FCCF9F9FD7D1EE0E8603C7ED80CFB147D605F7A785EE2740D81596D96291274A9DAFF9F75CD51A93AE051ED324F2DB27CDF8E22FD0F0863DAEC68BF08854F186074E11CE175C102309F7720C8AED0BD06AA6271205FFEE1FF29C1C6650043BA47864FBE8F77C17F896E03A7
	A79EA51B2E1D3218651018AF5AAC9BBC4CD736FA02BE4856F43544FC6D1F509821613DEC2DBF5B03F225464BAB96814205EE772F9CC6DABB018799CD32160EF23BFF26847BF9EC518C3ECDF6BA19B8F5B9A79DAAC43D0823BE9108E3907BD0175DB78F95077475EAF2E47D5FBD04F2F49A625F4ED11A8B833C862C05F68700E31449B2EE1449020D7B3EBE2ED1015AC9890E73A8EC9C13414FABD1AF341E9B964604C00D578F365554F4190A0255EBD9F9395A788D8A2F4B0E9520ECF6C1BB571BE4AD77F29DFEB7
	1F57E52BC7561214475E8CDBFBB6FA355C6B8D6A30FC912F4BBEA8C51E3150976F65F26FBAEC4C8DADDE2FBCA0513424496C7543C69DB39CF9E39FBC87A5452BF602938638FC2DEF60FAB1EFE9E8AD0F3786F89E5F84FCB3GD628087F79C409E16D35006B8318824822743E9CB80FBECA8D2006BD3AC5E5D0D6DC223D82A5DBA378559A9A8FEADAE8F7BB3ED7F2BD245B507607DBFD2AAA3B2F0CDCEB89A151951E466FAAFB196C579D3C282DEF6BD7561066DB87ADE6B5F1D441B14B8E15491C7802321F7A2AB5
	EB67DFDE5F4364E09D67F26802B111BEAE07D7DA4D643051474F874364F741FD34218D4DF3ABDA0C4E4D8B2DC337790279B155B7B3D7327A6C81EA17993E503AAD16627346C26B369385FC1F655097B946EB0AFFAFC33FC803F6369F4E85GF5GBDG2A1F5B6332B7AE10B9EA36CEB89BA9EE41F9F5DAF06635D3E9960B27DA3B23B40747D342F3D5C7A0D48EAB7C53779E9F52E673FAE74CFBABD41EA173187E8A2E1619478326D168E371FCFBC1E8BC473AA78DE86E86E0F123EA927D91ED30A44063430DCB46
	783031603CE5FF187890883075458402B6DA2D1F5B82C7B03E6D003EF4003C71A4EB85C0970083A0FA1C6B68BFCB5F1BCBC75472FA64B640F5C03CED8B4D0B3ACC73466FCC97EF4E18972A0B78716973187C2A5BD0FEA8374D504F6A5F63B4966F06F66184477DA89FF1F5506E1A60F554438534CE06F6340EDBC7F1682FF1BA2E32402CFE44F9F355B95137B10671E8B565F1BB755D6D54E7CC4F4F16052F37FED0E6DEEF25161957DBBFAAABE2F820E1BCED717E1F154DDE0F3DD6E6D60F9D23FB7E666182CA5B
	6B953D5470EC02AAE8FDBFD48F91133A6CA3B810E972BAC8EE8576EAE35AD5396D1CEEABF905391A3C37CE709C5DBC0131ADFB926297C05DA457795949D0FDF4C15F75C926CFD5FBC6CDCE57EF6C64CCFDA7GEEF3881E79672F0EE21E5881FD69GF9D3C95613G36A96E17AB5E1CB3F63687E6091D8B0F045777BDC74C753D7C8856A2DE59278C0821BAFD68C8295E970CG1D2A68C27133B1FFCD672B02B2A44E30094769FC5697EE7E93C742598D53ED5B38D13767F57FB957969ABF6CA047409413FB3476DC
	BB95548323D71F077539D39817FDAA444F9BE893FF4863F1BB9146EF85C887483DE12C891F74E3CC9ED48497AC6F2AC79CA03B695C4C83FC2E835AD3G31G9B8112EF70BA6869CF311FD50FD7C65A302AF672753CE84F577BEC63EEA0BFC0F8BE09341965935BCD28AFEEF23BADAA8C150367E6F04F11709EF3F33AAD325C1FC07DA07EE6E8CD70AF943F69E6E8CD50BA41EB026D5057F80B4759BF52FC968B6D849D370662B6C3FB3B0E93EFA0AE855A99BA2E130E4B0B00BC9541F1CFD2DC9D343BF55C97BCBC
	165BF55C57A8AE8A5AEBA3387F6CB9965E7E8F9CB3377F12E366716E4DE333473B370F0533DB363E0EB6AE17C4FD4D519437855ABBF45C4BEDFCEF29BA2E484665123BA0494A646F4B63F74FA5006BDA40EB4BF357B1676B35891610CDCD3FD7A4FB2BB054C52D1C4542277278DDC364829E8BFFCCE9EE0476F664330057A3015F70FAA47E71B524AB62C35373CACA4FF9CB76C2E6DB958B0375C89C54A30C6769831C279D7AE397A26ECF870C35B27A008483EAD0D634FF6934FE14B19C878C7DDF1B560F6B7DBC
	4B587F63E97DB90679ECEF5FBE4577367B5BF144798F6F06BD0B25EAFB4ADD441E5D3DA74849FBDF57763651464FFF9797F1F92679D01E81E82FG08DB4475D9C17350A6E8EF57F1BFA966FA4F50F1BF25365F9189F9A7127B77BFCEA0CE05F6340EFB1A62701C0BE7D82DA61E184F1D97528BDB431874B19E423CF84CD64B1855B0AC9EBA288CDCEE919220B4109809F239C5F17F8D30BE124754A7A7715EC585074E0844EFCC7E13C427B1F583E335CB88C8AE1F6B2AB4A192C6E7B50DC14D0B132C9D0BB91D6B
	1328FFF6B84534AB386D50E6B43FFAAAC8F38B9D67D144C1D1D1C4BB25CAB43A4F537529C0B382A0FEB10D2F1E52D9764FE8EE7350F5CA6E10F5265158168EF44892089F94B7CA6344B7G2A177035BF56B6DD864F7C86574B78675AE6634F4667D790DD3713B34E16F3811BF74F9D4ECB1A77BC161F1E4AC79FEBDE4272D3260E7FB70A6FB060D95CFCD90F1BBD5037E189BF4B6E2AC0799CDD8A398C209B4081908530FEA94FA79B0E5512B96A319E5953858947E9C891ECFF55876E6CFB5553FBF8FCCA7C67F5
	E44312D7B3431D85B7ACE5FE3AC3FB26751C20F8363FC5D5383F5CA828B181BAG2E8328G51D1FCFF642DEA363FF63B2436282AB0602037C4FCF33D12688F22316851620FADF4EFEB2302B1A3B1AA5CBD44E7745B541628D05D09943FBDAAD4F7AF79386ED2204FF68F377B8F8AE756F03D055C5E63E05CA600B4005D77303B180EFBF1CC76C15BAC7267F75FD99A14401C8A0063DA3B995EFF19494E6EEC9D56A973B87356947A6A8A989FFBAFDF73DF39D03F1779DD16CE63BBE77202F8FAB744708FCD627334
	FCDA330B35FE63776B46FD8D26774088DB6EE572CF561E9B171E50E87C4727C69A05EBFEED498CC32364BEAD1F5F974C6BACE6BC072A57567E7EA41E595D223FDC3F3D32C9E0AA78D12B2261C2F57979F7AAF92C7965EF037316F9EEE79EDB73164F0D793A7CCC36E93E4E787C2C25C7DBEB4030666D433B77B07EFFF3B648FFC76B6D70E7E3235A289E86CEEBF16BDF113D763E55707E0B3C796446798263D118FFB7F9301870EAC5789A1AD41A575056CA739ABA2FF276BB01124A705FE80ED49A3F513018B6D1
	0B35DDBC68F78B40CEG921DE4AD0966716C4B15EF939A4FCCE2D93BFD884F3078A5D8657C232B426FF5D5555C5F0FE32B709E3DDF8657F5A1FD0EDFD7057AD2E58FC5B25C172AD0C60AB464D059D08D7FF7D598AB1C62A0C533BAF0070D5FE755C6735A306C922F97FB22F94D57BF96343F9143FB3A6F6E5BE27E3C3E314E97D87C2FAB6271FFBD6CB9B91A1FF9AE1C1FF69F4B4EBC171C323FCBCB895A7D4FD4912F51CB6E67B5FAD3B1526C02369DE06AFE965F719B8A0EE53FDD9F1861D89CF99B2FA0345874
	FC9E0FCDC77F7691E2FB76554C9344BC1E2E3E1F4524547B0331A9CD0FCD6B705AC70BB747A710F71858F42E219E02D3C325668FD76AF0FDC717C18D330C7B407BE74E4033D798562A95CC6D32C23DG105A3F0F469916FEDD53E8DDFE0768B699F966D4B89A60C917E1DDD77A07458135C79852FDD328477C5B876BD081E2G92GB683A46BEBEB292918E5EDCCF954C795C1625FA897564EF227D5BB370F2E2C255F3F4037FA46BD5A1D534335D8BF480303DED10F85313505BA2E535BADFBA5BC91305AA2331D
	57963BE0AF394B198E1B1733BBE3D6DB54DE1A2B36B0C28F4CF5G9C525AD3700E675832CC6EDFCDC5F7D6D7F75C613C85334E0B027545AEB73BC77A8A7527046521B55DBFD17C466521B55D45E26E53DB202FFA852F473F5E0072DB816DF800EDGE9G3B81CA9E00BA8C2099209B40819081309AA08EE0A340D60094007487382D8D1D2F1C4556348B12203169365FD307FB53F396F3912D6F53BA4CBFF0DCD3A541A9CD504F2CD7E120FB08758F57073755517A39EDF5225EBC1FC4544F4CA70B6B4D7349FD75
	46FC42E4DE594EE51E3BB249FAF1A50F23C3EF231F6B7FDB147ADDB53EB1D70D502F5608A02E26260FFCCE279ED3D97C9C243482C0E385C05C4A79464FC75F45964BE9B1B4BEEF829A5BD672BC17C2F12950CEDF4973DCA27EADC03353278C6F170A6FFE1E3BF6D7F9B01DFD2FA13C5D24B44CEDB7AFB6185B4DCF9BE65ACDFA03395D3C56E0349B067BD5835A499078CA1FBEAE3B1163BE51F1FB9B77EA38BFCBF65521638B9A77E9788E9199F27C21C6DC171F627A78FD2B81770906BB5E08F19A366C1095BE77
	E4A35EF98CCACEA7EBD750B1129B4236AE0F5F52B5CA9E41306E73BA6E93DD37E75B6FACD63EB76FF9ACA6BEC7FDA7F7D5E82CFC0162CBD60546CA276E37655097390A1FA51FA8C2990C8D899EF65E7CFA917F6FF01ABE6697F40CC39F13CF6F9BB15FE42C62FE1552CE74BD3DE8F8D75A6F3EDF5DF7D7F9B099A497216C4E696D134596EC5792427E4BAE7BB260997F931223924B1591156C554F67AA31326FB4AA498F9E4E216DD5C8714C4A2CE559771E0D3FE6AB3190CBA731BA2895F7129CBF3AC3G7F980A
	C92E53BBCAEE126CB0AD059468F4BB29B9270CDDE84AFA6131AC8B4986224C3DBBC9B67AA850D6980FACF0EF4A81374AFFD9BAEDB47E845AC275DB8CE3349A36721671CEA0E3F1E958FB058D8FEA7FAFFBB0D843325C7D3F74BF0B398FA5D9CFBE44736FBBFEBC70072BA12F3A8427D3951CA3ADEEBB56132D632AE878566CC77ADEBEC66BE4FDAF7BF15D4306B97F8FD0CB87885A8E76511691GG74B0GGD0CB818294G94G88G88GD1F954AC5A8E76511691GG74B0GG8CGGGGGGGGGGG
	GGGGGGE2F5E9ECE4E5F2A0E4E1F4E1D0CB8586GGGG81G81GBAGGG5091GGGG
**end of data**/
}
}
