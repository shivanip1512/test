package com.cannontech.common.gui.util;

/**
 * Insert the type's description here.
 * Creation date: (5/7/2001 9:28:52 AM)
 * @author: 
 */
public class AddRemoveJTablePanel extends javax.swing.JPanel implements java.awt.event.ActionListener {
	private javax.swing.JButton ivjJButtonAdd = null;
	private javax.swing.JButton ivjJButtonRemove = null;
	private javax.swing.JLabel ivjJLabelAssigned = null;
	private javax.swing.JLabel ivjJLabelAvailable = null;
	private javax.swing.JList ivjJListAvailable = null;
	private javax.swing.JScrollPane ivjJScrollPaneAssigned = null;
	private javax.swing.JScrollPane ivjJScrollPaneAvailable = null;
	private javax.swing.JTable ivjJTableAssigned = null;
	public static final int MODE_TRANSFER = 0;
	public static final int MODE_COPY = 1;
	private int mode = MODE_TRANSFER;
	protected transient com.cannontech.common.gui.util.AddRemoveJTablePanelListener fieldAddRemoveJTablePanelListenerEventMulticaster = null;
	IvjEventHandler ivjEventHandler = new IvjEventHandler();

class IvjEventHandler implements java.awt.event.ActionListener, java.awt.event.MouseListener {
		public void actionPerformed(java.awt.event.ActionEvent e) {
			if (e.getSource() == AddRemoveJTablePanel.this.getJButtonAdd()) 
				connEtoC1(e);
			if (e.getSource() == AddRemoveJTablePanel.this.getJButtonRemove()) 
				connEtoC2(e);
			if (e.getSource() == AddRemoveJTablePanel.this.getJButtonAdd()) 
				connEtoC3(e);
			if (e.getSource() == AddRemoveJTablePanel.this.getJButtonRemove()) 
				connEtoC4(e);
		};
		public void mouseClicked(java.awt.event.MouseEvent e) {
			if (e.getSource() == AddRemoveJTablePanel.this.getJTableAssigned()) 
				connEtoC6(e);
		};
		public void mouseEntered(java.awt.event.MouseEvent e) {};
		public void mouseExited(java.awt.event.MouseEvent e) {};
		public void mousePressed(java.awt.event.MouseEvent e) {};
		public void mouseReleased(java.awt.event.MouseEvent e) {};
	};
/**
 * AddRemoveJTablePanel constructor comment.
 */
public AddRemoveJTablePanel() {
	super();
	initialize();
}
/**
 * AddRemoveJTablePanel constructor comment.
 * @param layout java.awt.LayoutManager
 */
public AddRemoveJTablePanel(java.awt.LayoutManager layout) {
	super(layout);
}
/**
 * AddRemoveJTablePanel constructor comment.
 * @param layout java.awt.LayoutManager
 * @param isDoubleBuffered boolean
 */
public AddRemoveJTablePanel(java.awt.LayoutManager layout, boolean isDoubleBuffered) {
	super(layout, isDoubleBuffered);
}
/**
 * AddRemoveJTablePanel constructor comment.
 * @param isDoubleBuffered boolean
 */
public AddRemoveJTablePanel(boolean isDoubleBuffered) {
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
	if (e.getSource() == getJButtonAdd()) 
		connEtoC1(e);
	if (e.getSource() == getJButtonRemove()) 
		connEtoC2(e);
	if (e.getSource() == getJButtonAdd()) 
		connEtoC3(e);
	if (e.getSource() == getJButtonRemove()) 
		connEtoC4(e);
	// user code begin {2}
	// user code end
}
/**
 * 
 * @param newListener com.cannontech.common.gui.util.AddRemoveJTablePanelListener
 */
public void addAddRemoveJTablePanelListener(com.cannontech.common.gui.util.AddRemoveJTablePanelListener newListener) {
	fieldAddRemoveJTablePanelListenerEventMulticaster = com.cannontech.common.gui.util.AddRemoveJTablePanelListenerEventMulticaster.add(fieldAddRemoveJTablePanelListenerEventMulticaster, newListener);
	return;
}
/**
 * connEtoC1:  (JButtonAdd.action.actionPerformed(java.awt.event.ActionEvent) --> AddRemoveJTablePanel.jButtonAdd_ActionPerformed(Ljava.awt.event.ActionEvent;)V)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC1(java.awt.event.ActionEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.jButtonAdd_ActionPerformed(arg1);
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}
/**
 * connEtoC2:  (JButtonRemove.action.actionPerformed(java.awt.event.ActionEvent) --> AddRemoveJTablePanel.jButtonRemove_ActionPerformed(Ljava.awt.event.ActionEvent;)V)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC2(java.awt.event.ActionEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.jButtonRemove_ActionPerformed(arg1);
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}
/**
 * connEtoC3:  (JButtonAdd.action.actionPerformed(java.awt.event.ActionEvent) --> AddRemoveJTablePanel.fireJButtonAddAction_actionPerformed(Ljava.util.EventObject;)V)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC3(java.awt.event.ActionEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.fireJButtonAddAction_actionPerformed(new java.util.EventObject(this));
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}
/**
 * connEtoC4:  (JButtonRemove.action.actionPerformed(java.awt.event.ActionEvent) --> AddRemoveJTablePanel.fireJButtonRemoveAction_actionPerformed(Ljava.util.EventObject;)V)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC4(java.awt.event.ActionEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.fireJButtonRemoveAction_actionPerformed(new java.util.EventObject(this));
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}

/**
 * connEtoC6:  (JTableAssigned.mouse.mouseClicked(java.awt.event.MouseEvent) --> AddRemoveJTablePanel.jTableAssigned_MouseClicked(Ljava.awt.event.MouseEvent;)V)
 * @param arg1 java.awt.event.MouseEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC6(java.awt.event.MouseEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		//this.jTableAssigned_MouseClicked(arg1);
		this.fireMouseTableAction_actionPerformed(new java.util.EventObject(this));
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}
/**
 * This method was created in VisualAge.
 * @param src javax.swing.JList
 * @param dest javax.swing.JList
 */
protected void copySelection()
{
	int[] itemsToCopy = getJListAvailable().getSelectedIndices();

	for( int i = 0; i < itemsToCopy.length; i++ )
	{
		getJTableModel().addRow( getJListAvailable().getModel().getElementAt( itemsToCopy[i] ) );
	}
}
/**
 * Method to support listener events.
 * @param newEvent java.util.EventObject
 */
protected void fireJButtonAddAction_actionPerformed(java.util.EventObject newEvent) {
	if (fieldAddRemoveJTablePanelListenerEventMulticaster == null) {
		return;
	};
	fieldAddRemoveJTablePanelListenerEventMulticaster.JButtonAddAction_actionPerformed(newEvent);
}
/**
 * Method to support listener events.
 * @param newEvent java.util.EventObject
 */
protected void fireJButtonRemoveAction_actionPerformed(java.util.EventObject newEvent) {
	if (fieldAddRemoveJTablePanelListenerEventMulticaster == null) {
		return;
	};
	fieldAddRemoveJTablePanelListenerEventMulticaster.JButtonRemoveAction_actionPerformed(newEvent);
}

protected void fireMouseTableAction_actionPerformed(java.util.EventObject newEvent) {
	if (fieldAddRemoveJTablePanelListenerEventMulticaster == null) {
		return;
	};
	fieldAddRemoveJTablePanelListenerEventMulticaster.MouseTableAction_actionPerformed(newEvent);
}

/**
 * 
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private static void getBuilderData() {
/*V1.1
**start of data**
	D0CB838494G88G88G22E80CAFGGGGGGGGGGGG8CGGGE2F5E9ECE4E5F2A0E4E1F4E135BB8BD4D4C716EE56E4F557F346EC482E49A4A7CEE2B6E686E79846A46E2CBBF1324E2CA66644B9B109E762A4E462E64D9C13188913E5B26EEE517C9595859595A3AAA2A8B66017A0828D4D2731C19608C25440237B812D2FBF69FEE823A659FB2BDED5BF1AEE88A4FA3C3CD7376AD65DFB6B562DDB77D5132CBDB313628DD90444A7916D5F29F8C2526B8819F37E091FF0DCA309CEA63AFFFB81B61347
	0792615989F5374C514999E40E1C8AE5854A31D551495B61BD13C44B372A60A54CB89DD057BD67E5F974F1B2BB79B8A9349CD31B8E4F0DG9E40C5G44909CBF2EB6DB434F0372C66D9DA15EC0480CF8EC733AE80310974F5AA9701C84D0E140B619669C2DBE86F058A68EA05D405A6641B30DF2F7E7F919566E525D33C96C333B8C9BF0ACDD7F9CA6EB343335E7AEF904AAA2013CFF6893F8D634DE6D2F34391DC61B64F239DD2AEC9BB0C25169F6997B879D46C1552114171B6C76B659693EA6B7F5C83D0A5CAA
	39E485E833931067A4F3AA6D8FF806B1E6D15C17E65453A5F8D78126A5F03EFF3321C263AB626AEC9273484630FC2FCAE07C26A78678D6EA67A44CCF447EABE87FC8E70572E4G464BE4418BC9623CECAB4736E5D0EE859890785FD17CA4A84FG08CA62BC1EB948F96C3C7994096B2DB4EC8A436322A4465B1A24G0FD93F6FB66C203672D0974EF7D1B20C8BE0871882B09BE09140AEEA231FF59E022729D72D25AAB7F9BCB50A5D5B6C364B8A3C0362FBD58D57A65B5CDEBBA14C8E57190BC27043A1A6F9388D
	A2B059AE90EEB711EDBFA62AFA5C1094C23639AF6DB644EAFD242684E43326205E7EC4F5DFG6FC80773477009F5081F2E4333397AEB5A9E4BF321AEB7156B39F3A367451A78B049A82DB6240460E546B3A78C53D2998F33D2833CB09D8FDDC49DAF857CEA00443468649C008A00CBE9DC4733BA0F0D25E38B583D57A5A96D033D9E492B568CBA943B6C55F4DDB32A2EC38123EB9DB79D534B6CE37AB9DA71332A10FAD9B1BB4F60CFE37A181E96508B9BFBEA58312352C65A46C6B107FFCB574442B4B60789829F
	C1714BF4F8B637B9940F6515D05738AEBA19357F74B462A357855B081D62172E8B3611B74B390D44C2DD4F7A68E4B63F5BAF23BFDC8265D5GA49D56B9C099C08740C0BA1F6377BA0B3F61BAEA554DAF231D5D358D1E32DF75CA9617CF15DCB6793C64F5202373912D4DCF3BD02F3D43681E246B5B86E3B448AE59AB298E378BFA171CB298130F35F93AEBB336714A3436436114E1G270770FE7F3DAB931E0A64D3BF7558A5D58E34E075AFF421CDDC134EC30F50813C4FCBC7FD6D25FEEE8A3C4FCA673AEA2538
	35D04E5A407552745996F81E856316410D1B9DB6E4C3728EF19B7D0B99E5A800760BGBC9BC13F1B22139782F8B278BAFD37135B23F9439CD274D2E5C8FBC448584CEC2BE0F330BD2E2B43F1DA816F81188E90853094E04DE63E37CEB577687A62BC850F017C0DE7CD21CCDC1E405A1CB5ECAC345F24B06BF13C6BFE3DE647D8E673728B6A97B336C4A79781D8377039397333DCE633B512CF66D6FA4E8B86A65B83B3455A5EF19177A0791A6CD24D32C7F6596145275948540BE9817B6DF38B5B7A9E4523BF2B85
	8373897C3D97B7B3BC7421DF8D3C7EC1DAAF292A5751BB284A6D322ABADC7D817AC7A9AF3EE12E5227493A209EE535030C37G2689D90D17B2F5F6E8F53B2F8EFAE8A8215973CCEA3BB3217D3CAD5C9E2617E36C35C2146F2765C07D3E724EB16683E947B3FFA3FD7869B147C818E07F01B1AE1E4635417DEA1206CF53EC093539C25B2C85F972B24106GD40087G96E6F2FD3CDD5EB3CABF9365B1B44F0927FB420C3361BB9C0B594E5E4B61ED3F60F2E85BB7DE8EED7B47AF0FEE5B9517C359B62B2B3D0C323A
	2F3BF4EB2571B27AD71B5B6512690295A40445586FF6209C8E17C33D6030774BEA3B63861CB8323250664B284D2F0679A3D95C57DE5F4FFDED5E0D4749026E9DA16333990F1699BA333473C3D64057B21EBE2571162C48CED0D42DFB5025B27CC5BA1E0677A5D0DC1EB982E98B1AEA86D59576CD3B3DC82202985923D57676393DCE59DEF0C53AA699256B2A11CE0451C42B6B703DB07748114A5CD0C70422AB225BEED3D0FF56709D1EAEBCDFD84278F3FCFA3F1EBFB6426DE011D50C174BD23686B249D7476E3B
	1DFA6CF3F865C0570C26BB38E746AB9E580C343FF33DD740A8114D23047319FA78215B2CCF9D2B2CF25C5C9E53F87DFE4A030C57F61B572DA8583D4967F3743BE4FB1153BD68130D74EF2D6230DD9D49EDB3560DB979A33B6EEE56F79ABC7F01DE115363C22FDB575F43F8251DFF67EC8E6BF5BC9C1E5076658F9A50A7CC83DFB193E0DE965F17DFB1E3BD5F1735602396D4425C995465ECC5DFD5DD172573BFCB33D86EE32328D746281FB1C67D42B16A570CD11FD39F2E1E493930AE837D3A7C45208C61BDEA73
	2CBC2445684F55ED892ABBAFA90301587F45BA5C5794DD4C42703F2E8F6DE71754E31CF2D5743F349E93CB5734BE593E90A5E11F9E2F4389A19E86E1DA0E68A933BEC7E48759B01FB4851E8FGB05ACDE5A8EBA8C3D13751D86AB44FA14D851AA5G2B35FE0A36B11D30F6072AF83B85D0DE3EAD50AEEED83B57C43B6A6D2C3F4E6D2C5D8CF82E84E06D8ACD3CDD5CF6569FEF17118DE73AEC5E2E2B0637D381B78DA0AA1B35DB1D2D9FB7271637AB5B911D5C8A206EE06D22F630F6F1BB305DB91173A99638BE0A
	FB95821329097CDC5D1C214F1959FD25A177D6040A1D2C7F561D417906828B766B817C241D620C55200FEDE1153AED8EFA5C536C644F8D7AFD17617E27E19DBC41D77AD48B84B4FEEEEB090D48AB1E87B534E6AFBFBAFC060CBC5F56599D2A5B4B0F8E355769326FF057421134914C35D604A5679D72F5427570E53DBE87B7FFD33EE1FD98BDC4E8724F5569E1799B5D8646538705E82BB4FF08BE48EF74DD07D82A3C3C095AA24B476D2DCB55E4085D4973EE4D0D38EE0AF6C5A71B81AE81309C40E21A9B4036AA
	60A281585A4E306857764ADD6C0C101E931D4CFC6F0715383F0DFF8F633A7D28887320938ED504DE9F4E2C56F8DC726307C94557A9A17306D3B6EDB66466B0FDB66684741A7A17E6C38EG53C5FB9557C58434D91243F5515392CA97093B39EFA8AB288848B22118060D1FD24D73BA5950F76BEE2EEBD775375375299D154C6E3FC5B843734EF32A793A1F321B5BD01B09733DGF0F1C2AF9549266163CEACB6A1FC6CD5A6BEF64947FC6CBBEA781C8DGEEA6G7369D32DFA1F3E7CE3ED4E76C4A7E76F61FAFD3B
	6688BC4795AE885F7013815469378BE5F89E2E2E164BD0823C8D6C61B2945A75B24C5C437C76C2F8AE97B2C456CEDC067C9DF48DCEB44E91EB7042D13D0FDF90F3A02C0FDF33072D3D445CE09F1FFE9667B6977047F3390F7F3AF1334E473701B5C82EFE85BF9E317AA39654816C79DA3954026352BD1F70BC10450AEB37917A34835C8208G18AB4618E799B3CFDD679FA5CF1DDF971C73DD169B48FF2C4A9D198BE1B959DFD1FF3CB6B7B8D73B0C62F3778667EA5B9BF92E568CF5D1FBF97E615E4A6071F3768D
	9F33AC284C689E99C177F1D079D2901D96873523DEC36CBFEC050B355AC077EC9CCB5D47F3258FB5E3EC918965C5GB1GEB81F277C7A7D7G746C67F3125294EA5F9DBEA761B65DC0DE0D4959D5B5B17D740F208B564F247D4369185C7F5842651E311F4F51066A313EC90466E15718F4CB7707220B26F42B76FF73BC9C236BB4CD0C4F3EB169560664739DCA1715B7DEBE2754CCECCE7FE184DD701C96640532797E3A5DA4244D873C3F187FDFDA797CD7677158CB35615A75C3F9BA406CBCEE97E7EBA7A6C757
	883AE0B9960414634A4DB031A35D81987F121D6F314B857FD1B24D259F08CEAEG28BE4057736B5456BB21BCF0002FDDFB937AA86E33DB5D8E17CA383F5EFC964FE4EE2F235F616238ED94E7F3FBE9020F617E4F9ABE7758E98D1DFB7C5C9ABA77E8370E1EF7D72C61F213ACC7FE064652FE10EF9AG436D24E70BA8A8AFBA40677239C6546DEAA8A76603CF832886688138154F5B7C163641339C1E53E664337DFCBEBCD708B6DD0DBDA16C9E7B43E7E2FE7835F2CB2B43F1110F1ED15A32315410E32568ECA69C
	BD4FB727E936B2DA2EFBB7ED33F85BA6BA8FBE1F6C234E5BAC2912D6771F6AD692A21F5BA2B9E542F3BDCBAA5334B6F52A3B36D448E035842F0535A0F746C16D6E464111BAE0737EF8F3F89B0BEA8EEDE3CFB6235CBE772057A6875B513FB6E70B3A40EEE4D1E5A70A2165AEA83DAAF97B6591F678BC25E7F56168DFEE8EE72BCCC7532B74BABAAAF474C0CB300E4AC0AFD6CDB7BDA1F4447AB3D76B7BBBA65644E2FA86D001EE72C19E07FE4F24EFFBDC34CD2C41369150EE16E87BF40D3E6D8941672F2D1C4F39
	50F6D190DFABC27049E226D5543F2EB9987C7D7C0F94CF8A023F1F5F307173CA865429851C2FBBEA75FC1D94FC7559C75B5F16970463AB2F8F47D92D0D9FAF70C7A8BECE07E7FCBDA9737BB5090560D38B39CFDD5902B83F3015D534BCA46629238EFD28A28D39874159169D423E2E567133439268E7A5C0BA60F38F31F3905B8FFEFA9D57ED1FD7F2029935FBE49B49BA0C34258D1CF692340FG188B308CA076905755FD8A66D28267C94E4BDD999B82F8F65E93FCBE768546627A4C72B54921605E436A7F79C2
	E6D03DF6A851687D57F04DB6D925DE8884B929D6D7FEE4E3200E13E9793D0316D0E741CBA02B67B0BF635B7A74E77CA907D91BC8D15F28062ADFF0186F89B69A6FAE0372AA01135B685DGA82797F15C33EDFC6F4896381F35F05A81017B2F96CEFBCB60BA4FF15AA9C5FC9EE22AB16F89EE23B4718836791016170B7ADDB5436BCBEBF8BD6B73F7B4DEE904BA6588471DECC65CA4A84F9638694DFC6C79C778DE752E8D7BCAB772F29CADE34E1015A759BA357AEF9A5F0D8758987751B13E4D77605100497E251D
	4BDEA0E4BD54C7848F47F46F7365313EDDFFFB19DF3A2DE3B0DFF4CF1D386B87B2CFB6F21FD15A03E7B891C3E2EE203C3C416B3057C87DD66A01D89C79AA0D1B28EFB372B8B20D666EE3211CDE9C1D1CD74C3F3F7A73F9EEB4721F9EA656C9D5A16FA746F4649BAA0A35BD2BB810BFA878FD37010DF921010F79G54AFABC65C1D8D7AF50BF63258C008911E2C7E29E175383FE2FEA4D07F7630FA64F7EF023EFED7233EBEC9C74FE4FB6EG17ED6DF3D1C4F962C8583B17B9A5DA7C26BDBBDF5CC9967C22C5132D
	5B4263F2FF895767EBB4A7908565C5GB1A5FC0EBE688B9367D36F487A3B3F0F1FD362CA78BD4E2FD50EABA9657E63869D576982CF6EF2587C25586EEE0A7BB70081516ED69B2EFD9502267925F827F76B9514BE77A836FF234DBC0A3D713B8C899A9020C1582F69A98B5EBF515E5978AB28EF7C8F9C9F6A58F8E5D78363394F0DB6DE4A9863790FB23B64B1C3A6C9B76D0127CB3E1E9BB2C5D9208EB8FC055AFC1F11753930121BFF0ACF88B35F130F32F90E501E8B93F7E96345DA71AE75B5592BBAEC12423643
	9A495B6AD69C36A1CD871B28BF465C5F4223FC8E1A1B71DCB6G872C9BEE17BA12126B4FC1E9B1B71BF88CE2822A33471AC753990F71395E4CC057F90C698C4B8A3C3F826B6AC521336E94249B7EE516E871C50448EB7A01EE9AC0A4409200554738FF3DEFC4DE6D1B794647A6C897B3EEBA964B3DCAFDDA7A716098EFA545E79F8F0E71B655F33F1A87F5D30E733374678ABDBFC2B975844C89C099C0A700FA0247014EF3A5E40C3CD8075BE305C30D22BB6AB279AEB5CC6CDE6CAFD3387F1A7B739F12F9F73D93
	9F6E3B5514935A1DF76D3966B7BBA91E49773E9365DB8EF531G59A72313CBG4CG17CEF2791EB8D7446443CFECA6D515EC83746B9D978EBF2885509830B2596CA34E00FA701C8C971F37D9F0AEA61F8CBEB7DC22780813416706150D7C5CB08B6AD2CFF1DBFD28CFBF3EF20A0DB7C9FBC61C9AB9BE231B2C6A69E6EB6D66EB4F25A168585A125A70F9D0BA284D514C6BFA1FD3366EF0587C7BCA2D4F38D3811F437AF861223E0F0C7B379A624374D1D246E8AB4A82FB709E4A97A129A4700F7965AF715A2DB646
	9B695C16F2FE7488B13F72CE7C7EB0E3F8343E97684608281F1340647F20054A6FBD280D917B8591F67683AF6EC3F1E55C26EEDE5D874F73521511DFEF81097963F6B8CE3B7A39EE731BB137205BB60D3A730871FCDDDB072556E46DEE2FEDBBE73556185A3ACD568B262EF6B1B7131A0F0DC25FF82E4D725939160ED0BD0C54611A2AD0B8A6F38A2655341C634F6DBC6EC97F0447BD2B6C41F14F5D717A38E5279C9C77A0FD205EAA8747BD43693F1203639ECE4FEC7B0DFAA25E7F20FB5FDAFF7B63E363ED9D03
	59D53F92BEFF64D2C2670FD4A5F40E7206B27A7D48BB147077A327AA7A7B11EC8D3CC24F2AE630059E0049GB3GA2BF61EBE23B9357845E4E9F69E36B6C7D183F429B79AA9F7FA3E7F8D963DDE35F63DF6742DCC12FDBD55DCE6C1F63B3DC747B105BC3118C375505BA72BA7A87D456D4436FF2515F1A49FD944D4EE5B7ADA28F7B8977451D96DC7F59653C5CCB4B47C5798F0D61EC83E93E9B7BCB30049B633BB827B2591F2471FB8770ECAF67E7742365BCE7609738FB4A390EA68B5C77D5CE3BCC60DE6E6634
	2B846ED48F270DAD67F1CA6CA0624C274136CEF3FB0AF49DA661FDEC2B576D01D0F608E169ADAF4DCE5F3BC6447C7E51F5A0DC9FE015CD60CFD9305DA1758BBB5F694718F5927071G40820065G2B8192ABA046GA8822886688470GCC8618819089B09FE0A9404A8AAE47B31E42D164508E39F4F1F8A57DF70DC73D18FF73E97B88330127C74D990677AB2F2B61575ADB6A58EB6DBDBA37B22C110EA10F16B70ED569F7173EBE1FAC56741F558C013378DA5DBE87661B59D9606FADE31D8574E736B850DF7A19
	40D9A0778CBB8B3CCA781E784BEE24EB32BAFCEAA0ED275DA13CE758BD2322B36C3BC86B99FE26F93E8F57EF9F1CE7585965CDEA77F628771C61E73F8F71E8231D7DFED8BE966FFA88BE7B4D04BE5DC4FF56DCC875998978F9E2BC674D40F8ADBDE30D17B64AF82B204FBA6DDD1B17C12493FB81DBAEDA9D39063A42FB6D8EC9F15C20575DD874225D3F7707371D937E31ED27529F5ACF1B7DA37D34459F5ACF1F736B7DB443F57851DE7AE17DF40AF6DD8257A5F01F53BE91F741E1D787845ECE79C2FC230C83F2
	7C954A57F50A6B54F0EE9D2E0B6F3BFE34FF90990E461CF648CF6D4A21A82C7CA5ED63F041F2907A3803726870C8BA3E7FC660106F7145EC0F5EA204FF0BFD2E904F7821F2B430CA34386ED9117309953617002E16472A6578FBE9111740EBBA7530D08A7619E0E215FDC53925707FA87AB4C61B0FAEC9FBFF46986867596E717533862FFCEAFE6E05D14FDEC515A377D436373CA5766EEAE84372827FAD7274F6407981E6G4C83D8DE49F3D47B1B785D4638CA9E6BDEEC8A0EF55FA77AD875033323473A872C41
	312EE9D82CFB41BAFA2CDB502457C1C9951CEB2BC60FA5188E2A28AC1D509647E3B8B345F9G773E3095A9BF1D08F5CF1786436778B78470FCE99076BBFC4BF57C31FE64BBA4C9A589A63BDDA5496CEB0C4FCBB609E936D0FB0F39F62CBC614DC7928CA451C212597A3710C4DC449612C4573805A4407A77FBB45991F03DA4793EA0DF91038E53BF8C3BDD4352731FCA568977DF2E12B41E96AA974D93B511F13993E0F443B0FB7C558FCE3C356435733E8412ECA1BBF0CBE8173D54934AF67C1A2C7DDC331E24F9
	705A817E6A87247051C712CF9DD2E4AB496AEB43AB67BED5361B380F05753615DEF4AB464CDEF123649BD0114753D61268429F1612AD2CDA11DC7D454C611EE62AB0D55385CED3BA40EFE6A0BFA8FA497C17CD8776B62D1C17C08C75E41D62166C75128D428A8BC9D3873C32EF402D587D1EAA56D16AF51CFD4C30A4C2F73B0478A82C31833C37E07A2A7F41E99BD6ED288735793D241836446C2BD13430578EC8DE98CA76DAD47C212C5B1BA76A7E6AE3B57F79066D178CA4DE939227E03D5F83E652A7B99D4A90
	3C18C1376EFEBAB91B2048FC2AB09FD406319E64D1FF7B4E6207DF2DD8339ABA2BA7C9747A9D301491E855B2686C153DF7C55E3BFBFDC44C5FA1479E9417771C86FB124B0737C021735CA019F915E05DF84F6212FB9F7F68B34AFA00D4A5FB58A481E9F13F6CF6961B7A7A9CAED9F4GB3865D6F965DE39BE3D01BAD7E3B37B63D743F3DE0E1E61260ACA921FFCB695F23783714E2CAA92674A8CC5B9AB26A3F60FABF17191AA5ADECC39B7263DB26DFAC8422AD847C17CC31E9A34C8D319BC65A8D22B70B5943D2
	967E4946BFBBA851AE7A7763F1730ED439C1BC678F679957BA7E2E834BAC67D37835BE2F94F7E7FA585C5482B373194B4C41F7C3E75EC069B26AA01E2F63317A1AA14CFD07BBAF34BBA5C5D1A5652A49E54733C34D10AA6BFECBFB837B7771B6DA25F6163901FCDF51517CBFD0CB8788F5F742FC3297GGB4C5GGD0CB818294G94G88G88G22E80CAFF5F742FC3297GGB4C5GG8CGGGGGGGGGGGGGGGGGE2F5E9ECE4E5F2A0E4E1F4E1D0CB8586GGGG81G81GBAGGG6C97
	GGGG
**end of data**/
}
/**
 * Return the JButton1 property value.
 * @return javax.swing.JButton
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JButton getJButtonAdd() {
	if (ivjJButtonAdd == null) {
		try {
			ivjJButtonAdd = new javax.swing.JButton();
			ivjJButtonAdd.setName("JButtonAdd");
			ivjJButtonAdd.setMnemonic('a');
			ivjJButtonAdd.setText("Add");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJButtonAdd;
}
/**
 * Return the JButton2 property value.
 * @return javax.swing.JButton
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JButton getJButtonRemove() {
	if (ivjJButtonRemove == null) {
		try {
			ivjJButtonRemove = new javax.swing.JButton();
			ivjJButtonRemove.setName("JButtonRemove");
			ivjJButtonRemove.setMnemonic('r');
			ivjJButtonRemove.setText("Remove");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJButtonRemove;
}
/**
 * Return the JLabelAssigned property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getJLabelAssigned() {
	if (ivjJLabelAssigned == null) {
		try {
			ivjJLabelAssigned = new javax.swing.JLabel();
			ivjJLabelAssigned.setName("JLabelAssigned");
			ivjJLabelAssigned.setFont(new java.awt.Font("Arial", 1, 12));
			ivjJLabelAssigned.setText("Assigned:");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJLabelAssigned;
}
/**
 * Return the JLabelAvailable property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getJLabelAvailable() {
	if (ivjJLabelAvailable == null) {
		try {
			ivjJLabelAvailable = new javax.swing.JLabel();
			ivjJLabelAvailable.setName("JLabelAvailable");
			ivjJLabelAvailable.setFont(new java.awt.Font("Arial", 1, 12));
			ivjJLabelAvailable.setText("Available:");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJLabelAvailable;
}
/**
 * Return the JList1 property value.
 * @return javax.swing.JList
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JList getJListAvailable() {
	if (ivjJListAvailable == null) {
		try {
			ivjJListAvailable = new javax.swing.JList();
			ivjJListAvailable.setName("JListAvailable");
			ivjJListAvailable.setBounds(0, 0, 160, 120);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJListAvailable;
}
/**
 * Return the JScrollPane2 property value.
 * @return javax.swing.JScrollPane
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JScrollPane getJScrollPaneAssigned() {
	if (ivjJScrollPaneAssigned == null) {
		try {
			ivjJScrollPaneAssigned = new javax.swing.JScrollPane();
			ivjJScrollPaneAssigned.setName("JScrollPaneAssigned");
			ivjJScrollPaneAssigned.setVerticalScrollBarPolicy(javax.swing.JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
			ivjJScrollPaneAssigned.setHorizontalScrollBarPolicy(javax.swing.JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
			getJScrollPaneAssigned().setViewportView(getJTableAssigned());
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJScrollPaneAssigned;
}
/**
 * Return the JScrollPane1 property value.
 * @return javax.swing.JScrollPane
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JScrollPane getJScrollPaneAvailable() {
	if (ivjJScrollPaneAvailable == null) {
		try {
			ivjJScrollPaneAvailable = new javax.swing.JScrollPane();
			ivjJScrollPaneAvailable.setName("JScrollPaneAvailable");
			getJScrollPaneAvailable().setViewportView(getJListAvailable());
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJScrollPaneAvailable;
}
/**
 * Return the ScrollPaneTable property value.
 * @return javax.swing.JTable
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
public javax.swing.JTable getJTableAssigned() {
	if (ivjJTableAssigned == null) {
		try {
			ivjJTableAssigned = new javax.swing.JTable();
			ivjJTableAssigned.setName("JTableAssigned");
			getJScrollPaneAssigned().setColumnHeaderView(ivjJTableAssigned.getTableHeader());
			getJScrollPaneAssigned().getViewport().setBackingStoreEnabled(true);
			ivjJTableAssigned.setBounds(0, 0, 200, 200);
			// user code begin {1}

			ivjJTableAssigned.setAutoCreateColumnsFromModel(true);
			ivjJTableAssigned.setAutoResizeMode(javax.swing.JTable.AUTO_RESIZE_NEXT_COLUMN);
			ivjJTableAssigned.setGridColor( java.awt.Color.black );
			ivjJTableAssigned.setSelectionMode(javax.swing.ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
			ivjJTableAssigned.setRowHeight(20);
			
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJTableAssigned;
}
/**
 * Insert the method's description here.
 * Creation date: (5/7/2001 9:45:52 AM)
 * @return javax.swing.table.TableModel
 */
protected AddRemoveJTableModel getJTableModel() 
{
	try
	{
		return (AddRemoveJTableModel)getJTableAssigned().getModel();
	}
	catch( ClassCastException e )
	{
		com.cannontech.clientutils.CTILogger.error( e.getMessage(), e );
		throw new Error("AddRemoveJTablePanel should only have JTable models that are AddRemoveJTableModel!!");
	}
	
}
/**
 * Insert the method's description here.
 * Creation date: (5/7/2001 9:43:36 AM)
 * @return int
 */
public int getMode() {
	return mode;
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
	getJButtonAdd().addActionListener(ivjEventHandler);
	getJButtonRemove().addActionListener(ivjEventHandler);
	getJScrollPaneAssigned().addMouseListener(ivjEventHandler);
	getJTableAssigned().addMouseListener(ivjEventHandler);
}
/**
 * Initialize the class.
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void initialize() {
	try {
		// user code begin {1}
		// user code end
		setName("AddRemoveJTablePanel");
		setLayout(new java.awt.GridBagLayout());
		setSize(400, 356);

		java.awt.GridBagConstraints constraintsJButtonAdd = new java.awt.GridBagConstraints();
		constraintsJButtonAdd.gridx = 2; constraintsJButtonAdd.gridy = 3;
		constraintsJButtonAdd.anchor = java.awt.GridBagConstraints.WEST;
		constraintsJButtonAdd.ipadx = 28;
		constraintsJButtonAdd.insets = new java.awt.Insets(5, 1, 1, 6);
		add(getJButtonAdd(), constraintsJButtonAdd);

		java.awt.GridBagConstraints constraintsJButtonRemove = new java.awt.GridBagConstraints();
		constraintsJButtonRemove.gridx = 3; constraintsJButtonRemove.gridy = 3;
		constraintsJButtonRemove.anchor = java.awt.GridBagConstraints.WEST;
		constraintsJButtonRemove.ipadx = 4;
		constraintsJButtonRemove.insets = new java.awt.Insets(5, 6, 1, 103);
		add(getJButtonRemove(), constraintsJButtonRemove);

		java.awt.GridBagConstraints constraintsJScrollPaneAvailable = new java.awt.GridBagConstraints();
		constraintsJScrollPaneAvailable.gridx = 1; constraintsJScrollPaneAvailable.gridy = 2;
		constraintsJScrollPaneAvailable.gridwidth = 3;
		constraintsJScrollPaneAvailable.fill = java.awt.GridBagConstraints.BOTH;
		constraintsJScrollPaneAvailable.anchor = java.awt.GridBagConstraints.WEST;
		constraintsJScrollPaneAvailable.weightx = 1.0;
		constraintsJScrollPaneAvailable.weighty = 1.0;
		constraintsJScrollPaneAvailable.ipadx = 363;
		constraintsJScrollPaneAvailable.ipady = 109;
		constraintsJScrollPaneAvailable.insets = new java.awt.Insets(0, 5, 4, 10);
		add(getJScrollPaneAvailable(), constraintsJScrollPaneAvailable);

		java.awt.GridBagConstraints constraintsJScrollPaneAssigned = new java.awt.GridBagConstraints();
		constraintsJScrollPaneAssigned.gridx = 1; constraintsJScrollPaneAssigned.gridy = 5;
		constraintsJScrollPaneAssigned.gridwidth = 3;
		constraintsJScrollPaneAssigned.fill = java.awt.GridBagConstraints.BOTH;
		constraintsJScrollPaneAssigned.anchor = java.awt.GridBagConstraints.WEST;
		constraintsJScrollPaneAssigned.weightx = 1.0;
		constraintsJScrollPaneAssigned.weighty = 1.0;
		constraintsJScrollPaneAssigned.ipadx = 363;
		constraintsJScrollPaneAssigned.ipady = 105;
		constraintsJScrollPaneAssigned.insets = new java.awt.Insets(0, 6, 23, 9);
		add(getJScrollPaneAssigned(), constraintsJScrollPaneAssigned);

		java.awt.GridBagConstraints constraintsJLabelAvailable = new java.awt.GridBagConstraints();
		constraintsJLabelAvailable.gridx = 1; constraintsJLabelAvailable.gridy = 1;
		constraintsJLabelAvailable.anchor = java.awt.GridBagConstraints.WEST;
		constraintsJLabelAvailable.ipadx = 55;
		constraintsJLabelAvailable.insets = new java.awt.Insets(6, 5, 0, 0);
		add(getJLabelAvailable(), constraintsJLabelAvailable);

		java.awt.GridBagConstraints constraintsJLabelAssigned = new java.awt.GridBagConstraints();
		constraintsJLabelAssigned.gridx = 1; constraintsJLabelAssigned.gridy = 4;
		constraintsJLabelAssigned.anchor = java.awt.GridBagConstraints.WEST;
		constraintsJLabelAssigned.ipadx = 34;
		constraintsJLabelAssigned.insets = new java.awt.Insets(2, 6, 0, 18);
		add(getJLabelAssigned(), constraintsJLabelAssigned);
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
public void jButtonAdd_ActionPerformed(java.awt.event.ActionEvent actionEvent) 
{
	if( getJListAvailable().getSelectedIndices().length > 0 )
	{
		if( getMode() == MODE_TRANSFER )
		{
			transferSelection();
		}
		else if( getMode() == MODE_COPY )
		{
			copySelection();
		}
	}

	getJListAvailable().clearSelection();
	getJTableAssigned().clearSelection();

	revalidate();
	repaint();

	return;
}
/**
 * Comment
 */
public void jButtonRemove_ActionPerformed(java.awt.event.ActionEvent actionEvent) 
{
	if( getJTableAssigned().getSelectedRows().length > 0 )
	{
		if( getMode() == MODE_TRANSFER )
		{
			Object[] items = new Object[ getJListAvailable().getModel().getSize() + getJTableAssigned().getSelectedRows().length ];
			int i = 0;
			for( i = 0; i < getJListAvailable().getModel().getSize(); i++ )
				items[i] = getJListAvailable().getModel().getElementAt(i);
			
			for( int j = (getJTableAssigned().getSelectedRows().length-1); j >= 0; j-- )		
			{
				items[i+j] = getJTableModel().getRowAt( getJTableAssigned().getSelectedRows()[j] );
				getJTableModel().removeRow( getJTableAssigned().getSelectedRows()[j] );
			}

			getJListAvailable().setListData(items);
		}
		else if( getMode() == MODE_COPY )
		{
			for( int i = (getJTableAssigned().getSelectedRows().length-1); i >= 0; i-- )		
			{
				getJTableModel().removeRow( getJTableAssigned().getSelectedRows()[i] );
			}
				
		}
	}

	getJListAvailable().clearSelection();
	getJTableAssigned().clearSelection();
	
	revalidate();
	repaint();

	return;
}
/**
 * Comment
 */
/**
 * Comment
 */
public void jTableAssigned_MouseClicked(java.awt.event.MouseEvent mouseEvent) {
	return;
}
/**
 * main entrypoint - starts the part when it is run as an application
 * @param args java.lang.String[]
 */
public static void main(java.lang.String[] args) {
	try {
		javax.swing.JFrame frame = new javax.swing.JFrame();
		AddRemoveJTablePanel aAddRemoveJTablePanel;
		aAddRemoveJTablePanel = new AddRemoveJTablePanel();
		frame.setContentPane(aAddRemoveJTablePanel);
		frame.setSize(aAddRemoveJTablePanel.getSize());
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
		System.err.println("Exception occurred in main() of javax.swing.JPanel");
		exception.printStackTrace(System.out);
	}
}
/**
 * 
 * @param newListener com.cannontech.common.gui.util.AddRemoveJTablePanelListener
 */
public void removeAddRemoveJTablePanelListener(com.cannontech.common.gui.util.AddRemoveJTablePanelListener newListener) {
	fieldAddRemoveJTablePanelListenerEventMulticaster = com.cannontech.common.gui.util.AddRemoveJTablePanelListenerEventMulticaster.remove(fieldAddRemoveJTablePanelListenerEventMulticaster, newListener);
	return;
}
/**
 * This method was created in VisualAge.
 * @param list javax.swing.JList
 */
protected void removeSelection()
{
	javax.swing.ListModel model = getJListAvailable().getModel();

	Object[] items = new Object[model.getSize()];

	for( int i = 0; i < model.getSize(); i++ )
		items[i] = model.getElementAt(i);

	int[] selectedItems = getJListAvailable().getSelectedIndices();

	for( int i = 0; i < selectedItems.length; i++ )
	{
		items[ selectedItems[i] ] = null;
	}

	Object[] itemsRemaining = new Object[ items.length - selectedItems.length ];

	int j = 0;

	for( int i = 0; i < items.length; i++ )
		if( items[i] != null )
			itemsRemaining[j++] = items[i];

	getJListAvailable().setListData(itemsRemaining);
}
/**
 * 
 * @param listData java.lang.Object[]
 */
public void setJListData(java.lang.Object[] listData) 
{
	getJListAvailable().setListData(listData);
}
/**
 * Insert the method's description here.
 * Creation date: (5/7/2001 10:31:04 AM)
 * @param model com.cannontech.common.gui.util.AddRemoveJTableModel
 */
public void setJTableModel(AddRemoveJTableModel model) 
{
	getJTableAssigned().setModel( model );
}
/**
 * Insert the method's description here.
 * Creation date: (5/7/2001 9:43:36 AM)
 * @param newMode int
 */
public void setMode(int newMode) {
	mode = newMode;
}
/**
 * This method was created in VisualAge.
 */
protected void transferSelection()
{		
	copySelection();

	removeSelection();
}
}
