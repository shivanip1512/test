package com.cannontech.yc.gui;

import com.cannontech.common.util.KeyAndValue;
import com.cannontech.common.util.KeysAndValues;
import com.cannontech.common.util.KeysAndValuesTableModel;

/**
 * Insert the type's description here.
 * Creation date: (4/11/2002 3:51:40 PM)
 * @author: 
 */
public class CustomCommandEditPanel extends com.cannontech.common.gui.util.DataInputPanel implements java.awt.event.ActionListener {

	private javax.swing.JDialog dialog = null;
	private String dialogTitle = null;
	private YCDefaults ycDefaults = null;
	private javax.swing.JButton ivjCancelButton = null;
	private javax.swing.JButton ivjOkButton = null;
	private javax.swing.JPanel ivjOkCancelButtonPanel = null;
	private int CANCEL = 0;
	private int OK = 1;
	private int buttonPushed = CANCEL;
	private KeysAndValues keysAndValues = null;
	private javax.swing.JPanel ivjAdvancedOptionsPanel = null;
	private com.cannontech.common.gui.util.AddRemoveButtonsHorizontalPanel ivjAddRemoveButtonsPanel = null;
	private javax.swing.JTable ivjCommandTable = null;
	private javax.swing.JScrollPane ivjCommandTableScrollPane = null;
	private javax.swing.JLabel ivjCommonCommandLabel = null;
	private javax.swing.JTextField ivjCommonCommandTextField = null;
	private javax.swing.JLabel ivjExecuteCommandLabel = null;
	private javax.swing.JTextField ivjExecuteCommandTextField = null;
	/**
	 * AdvancedOptionsPanel constructor comment.
	 */
	public CustomCommandEditPanel() {
		super();
		initialize();
	}
	/**
	 * AdvancedOptionsPanel constructor comment.
	 */
	public CustomCommandEditPanel(YCDefaults defaultSettings) {
		super();
		initialize();
		setValue(defaultSettings);
	}
	/**
	 * Insert the method's description here.
	 * Creation date: (5/17/2002 11:49:08 AM)
	 * @param source java.awt.event.ActionEvent
	 */
	public void actionPerformed(java.awt.event.ActionEvent event)
	{
		if( event.getSource() == getAddRemoveButtonsPanel().getAddButton())
		{
			String key = getCommonCommandTextField().getText();
			String value = getExecuteCommandTextField().getText();
			KeyAndValue kav = new KeyAndValue(key, value);
			((KeysAndValuesTableModel)getCommandTable().getModel()).addRow(kav);
		}				
		else if( event.getSource() == getAddRemoveButtonsPanel().getRemoveButton())
		{
			int[] rowsToRemove = getCommandTable().getSelectedRows();
			((KeysAndValuesTableModel)getCommandTable().getModel()).removeRow(rowsToRemove);
			getCommandTable().getSelectionModel().clearSelection();
		}
		else if( event.getSource() == getOkButton())
		{
			setButtonPushed(OK);
			exit();
		}				
		else if( event.getSource() == getCancelButton())
		{
			setButtonPushed(CANCEL);
			exit();
		}
	}
	/**
	 * Insert the method's description here.
	 * Creation date: (6/3/2002 4:00:35 PM)
	 */
	public void exit() 
	{
		removeAll();
		setVisible(false);
		dialog.dispose();
	}
/**
 * Return the AddRemoveButtonsPanel property value.
 * @return com.cannontech.common.gui.util.AddRemoveButtonsHorizontalPanel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private com.cannontech.common.gui.util.AddRemoveButtonsHorizontalPanel getAddRemoveButtonsPanel() {
	if (ivjAddRemoveButtonsPanel == null) {
		try {
			ivjAddRemoveButtonsPanel = new com.cannontech.common.gui.util.AddRemoveButtonsHorizontalPanel();
			ivjAddRemoveButtonsPanel.setName("AddRemoveButtonsPanel");
			// user code begin {1}
			ivjAddRemoveButtonsPanel.getAddButton().addActionListener(this);
			ivjAddRemoveButtonsPanel.getRemoveButton().addActionListener(this);
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjAddRemoveButtonsPanel;
}
/**
 * Return the JPanel1 property value.
 * @return javax.swing.JPanel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JPanel getAdvancedOptionsPanel() {
	if (ivjAdvancedOptionsPanel == null) {
		try {
			ivjAdvancedOptionsPanel = new javax.swing.JPanel();
			ivjAdvancedOptionsPanel.setName("AdvancedOptionsPanel");
			ivjAdvancedOptionsPanel.setLayout(new java.awt.GridBagLayout());

			java.awt.GridBagConstraints constraintsCommonCommandLabel = new java.awt.GridBagConstraints();
			constraintsCommonCommandLabel.gridx = 0; constraintsCommonCommandLabel.gridy = 0;
			constraintsCommonCommandLabel.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintsCommonCommandLabel.anchor = java.awt.GridBagConstraints.WEST;
			constraintsCommonCommandLabel.weightx = 1.0;
			constraintsCommonCommandLabel.insets = new java.awt.Insets(10, 10, 5, 10);
			getAdvancedOptionsPanel().add(getCommonCommandLabel(), constraintsCommonCommandLabel);

			java.awt.GridBagConstraints constraintsCommonCommandTextField = new java.awt.GridBagConstraints();
			constraintsCommonCommandTextField.gridx = 0; constraintsCommonCommandTextField.gridy = 1;
			constraintsCommonCommandTextField.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintsCommonCommandTextField.weightx = 1.0;
			constraintsCommonCommandTextField.insets = new java.awt.Insets(5, 5, 5, 5);
			getAdvancedOptionsPanel().add(getCommonCommandTextField(), constraintsCommonCommandTextField);

			java.awt.GridBagConstraints constraintsCommandTableScrollPane = new java.awt.GridBagConstraints();
			constraintsCommandTableScrollPane.gridx = 0; constraintsCommandTableScrollPane.gridy = 3;
			constraintsCommandTableScrollPane.gridwidth = 2;
			constraintsCommandTableScrollPane.fill = java.awt.GridBagConstraints.BOTH;
			constraintsCommandTableScrollPane.weightx = 1.0;
			constraintsCommandTableScrollPane.weighty = 1.0;
			constraintsCommandTableScrollPane.insets = new java.awt.Insets(5, 5, 5, 5);
			getAdvancedOptionsPanel().add(getCommandTableScrollPane(), constraintsCommandTableScrollPane);

			java.awt.GridBagConstraints constraintsOkCancelButtonPanel = new java.awt.GridBagConstraints();
			constraintsOkCancelButtonPanel.gridx = 0; constraintsOkCancelButtonPanel.gridy = 4;
			constraintsOkCancelButtonPanel.gridwidth = 2;
constraintsOkCancelButtonPanel.gridheight = 0;
			constraintsOkCancelButtonPanel.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintsOkCancelButtonPanel.anchor = java.awt.GridBagConstraints.SOUTH;
			constraintsOkCancelButtonPanel.insets = new java.awt.Insets(5, 5, 5, 5);
			getAdvancedOptionsPanel().add(getOkCancelButtonPanel(), constraintsOkCancelButtonPanel);

			java.awt.GridBagConstraints constraintsExecuteCommandLabel = new java.awt.GridBagConstraints();
			constraintsExecuteCommandLabel.gridx = 1; constraintsExecuteCommandLabel.gridy = 0;
			constraintsExecuteCommandLabel.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintsExecuteCommandLabel.anchor = java.awt.GridBagConstraints.WEST;
			constraintsExecuteCommandLabel.weightx = 1.0;
			constraintsExecuteCommandLabel.insets = new java.awt.Insets(10, 10, 5, 10);
			getAdvancedOptionsPanel().add(getExecuteCommandLabel(), constraintsExecuteCommandLabel);

			java.awt.GridBagConstraints constraintsExecuteCommandTextField = new java.awt.GridBagConstraints();
			constraintsExecuteCommandTextField.gridx = 1; constraintsExecuteCommandTextField.gridy = 1;
			constraintsExecuteCommandTextField.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintsExecuteCommandTextField.weightx = 1.0;
			constraintsExecuteCommandTextField.insets = new java.awt.Insets(5, 5, 5, 5);
			getAdvancedOptionsPanel().add(getExecuteCommandTextField(), constraintsExecuteCommandTextField);

			java.awt.GridBagConstraints constraintsAddRemoveButtonsPanel = new java.awt.GridBagConstraints();
			constraintsAddRemoveButtonsPanel.gridx = 0; constraintsAddRemoveButtonsPanel.gridy = 2;
			constraintsAddRemoveButtonsPanel.gridwidth = 2;
			constraintsAddRemoveButtonsPanel.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintsAddRemoveButtonsPanel.weightx = 1.0;
			constraintsAddRemoveButtonsPanel.insets = new java.awt.Insets(5, 5, 5, 5);
			getAdvancedOptionsPanel().add(getAddRemoveButtonsPanel(), constraintsAddRemoveButtonsPanel);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjAdvancedOptionsPanel;
}
	/**
	 * 
	 */
	/* WARNING: THIS METHOD WILL BE REGENERATED. */
	private static void getBuilderData() {
/*V1.1
**start of data**
	D0CB838494G88G88G2CF0B8AFGGGGGGGGGGGG8CGGGE2F5E9ECE4E5F2A0E4E1F4E135BB8DF8D4551581828409989494E96CC60B4A370BDBDAC34B7A655B0FAAEB33CA95ADAE76DBDCE325DDEA2D62E72C79DC365FE3C8B048CF9209B5AE3146C2A4C0C842DFCD4308A419C0C2869313917909A4AF73DEE69E194CCCE65E1084226CB9773E7366654DCC0834647BCE5E3B671EF36FB9671EFB6FB9773E918ABFC849D9EBA9940435B902767771DAC158D1A78807BB73DFA1DC519945A2987EBE
	84A890D655E743338C6A24590AA5DF9838128BE5AB1463E7A916F761FD2BB0FC457AA93C446827906A7E3C7C17FF9C3D1FB3DB281FB7D9F9F743A6F8EE81B80AFD834CBF037D57B594E97884A8EF515E9156C22B5633C8D3FF3248004F54684A4E72E76EB924792A21D82B0F87FCB2408A0055E743EDEEE052CD2C29564A9D53D3044CC78AAC1B31AFC37B842D67B82F2ABDCB045B19A156892B5EABC31B35763A0EB8FDFD95CE516B75F9D559692E10877D3E00DAD3E32BEB8D85DCF22B6815BDAB19FC4A49DC46
	AB8873204CE5F9E233D56BEBC6EF0A10713D6C183214F5F0996C9DE1D9D6345DE7D95301EDCFE236C53EC4A8AF8660F2BE3CFDFF8CB9071C952E10D2D3D39F8A2A3E3EFADFDF1F68159AA4C5E5928B8217793186127968D70AE551D7089B36E3DF03701E84B077AB5265131D244B694B5F97323A8FDA72E26812FD1E6BD0FEBE2CCBE67EEBB96FB1BF79D59E0E359B6A9281D2G96832C84486ED4AC5B18FF26EC598D4F5AEE958577F9EB7D7EBA0F94B86913E48F3C5754C00D06EB131D3E00A4885C873DA74B23
	48C3D05CB9527F42BE7325C0BEB32F6D9EC1D5F7B37B1AF5CBFDEA2B25CAEB435699560D6BB52EG7512G9F87B08BE0BE40D200553ADEAFECB9F0B53D1AE07C82DE51539E6A760B8135AE24F8A4B920691758B01AFE5100734D7E56FC5CAEF38F986D32627EC3EC2E1B6D32E26E7FD9B6F5F1FB6C688A5B05777D7318FD5B3AA24783415D05FE381A7961E9F8DF2B79A8473F4170738CF8BEC746B3BC1653202EC4A43FBD3D05F430E74F9672777DA52A8E339E5CE3099739BC4922F9EC4F17625A138E7815GEB
	G8A3B954BC100D6G291B4677D0DE65987D3655B0361C7725BCDCF065C1B5A0B6F9032A68F54A274400A2F6FB642020513C1E0732F70F60DB436613937AB8AEFB650028AABEAF34AE764960C8C1CE1313DF00B4811955BA14BE99BA68738B546E667C2D707408C1758B3FA42AF21802573F130F363CA81E029621811C8B5DE87309EC4D0B077794GEE2B299FA1AE8B6B1DE417247C3761598C0EADFBE06DD41CA8069898227EEFA84035C43EA8FBD51B6C173D923C84353A69859B42F6697169B25F4670B8C76A
	C170200E3F23200063218D2315297EEED6AF2AEAC0698E29F23B2C2A0A5795663F0F49929CB17D028257ED7CCE74319D201795402E6B77710E2D867D6CBEDFEF486F4011536C7482DBE73B00FE50C97B656F6D38FFA6BB49CFDFBACDFEEA5BFC37D07E544128FE0A1066647E394CE91EEB42C9ECB35CC7027D74371C77E6C8F6C61F236135654E3F390FF52354F13B3FD606F6DF833294C90A25DAA23B87F6147079D6A786E51AE1AD8198B1D98AFB9927F5174676B1DFE9F49F8B15C677314B25237B500452E8BE
	2479FAA96EAD3E812F41A7EFAAFD1B4DE12FD7E6C26BAC825F67DFF220AD942F22FE29C8AED9EDD7AE49B04E927AD6B21B032D70AEC964CB835B4917F6DC3AD3D8541997F5BF1BF5F705A5CD62633BD48A7B12B6E7BFC21B4B9E398F8CD56F8BF9D50E3F154DEF8D2F2DAF89BB2353CE5B097E58AB8FE9F4B76FDCAF60BA6289493AEE73450DE863005287C30593D60B89579EB346042BC1BE0C9F2BE445E283D8475A5BD5DD0863AFDD4455D2EA71236D0247F06D63FED3319B656AGFABFC002CCFE33256CBD43
	FC559C265E6771F0736B6372BFE579B80EF2FFC8068EF0C2B76B3AD8F63DED2ABB2569456B5F5A05FAF98CEBC10A0CFA5D2F470938D7F05CA38C77AECCA8B4989F43137946B8D68A6E0CBA0688ABE5BEF6D932F95F726E41F60BFA94CBD98F69BEF117F12D2A8D86FDCE05ED8B5AF85CDE03B1CA34185249B6B50CABABC22A028B392842FC710734201247143D47F23535415AC3B1E5C6930E038A65C400B93AAC87F7C77A52DFF73F854F9EA59004D8C81287491E1F6EC63B609E2521A9F65BEF3453220CEDB16D
	14562379FA0F59CE2965A8470B0097DCA45B833B8B8CF602B89336E4970CA249935C330E7B36D69E5813C33EAD505A68BFE60EFD8ADD6175348C5E2D2E116BAB0FF19CA750DE2DAEF36CF31E613BDC66586757CD947B7821EE190B7662FF5C0BFAD53B95CB231B745AD43E5920D72BCF712A3ACE7FD20E735397D0DC0A17F07F4AF090E533850B6FE71359981F06B6BDG9CA7B5A2AE894AF3F55C44A644AD01F2061B762433A7F0CD48544B92AB97AAD4FE3169EF5DCF4C00FD0D5C33F2CE7C3D7BB06EE15CEE5F
	5F0BE3B428176FE765A10DF62B705DE36B19BF8F79C2B0GF394345782FD4D2B06F2AB40A0C022426386BEBFFFB0006BCA8FACF23065347BE52790F781F9AFB7926F0BC0AFG2E8460A8C05785DAAF131BD07F0BCAD0417568E946F7FF932DC769BA5DB325D156D5B649795ABC7304F1EDAE6E65729D052783007B64B347A8E775GEECEAF493F2F8BE3E9363EDC90AF0A9562G245347830AD4A73A6C4C9AFC9DA9BD1E2B712F6825F5247DB836D968D1AC65GB68F6DF7039FD1EEBA6F1E5902BD6ED054BCBB43
	D1EC6970F0DF08730457008C48CDF91FF7B6D21FE9D01F69C15C630D0526F12E04FF9568D72CFEED13319E570CFD02317E4008FA147719ECE33DBC229EED7401464FF5FB3414F4DB7368DC4133E0FB4CB384EB9F5729CBFB96ED5CA64C3DFCBC1B0F69A3CDB84F54C0C8666D268C98574D6A37BE0C39EE4E525A1B27BD632D5B343D6255DDC546319C2C888E00D356541CB01C0BCCE84E5574DA59C763186E44F1AC76C28E85E077525CFFBEE23D3C9AF03ED72E11EF7771EF4B47576152C614DF7272F5F82D0E2F
	E0F83F814F67421963B4970621AE43CB6B733CCA341F4D87F394E090A089E0BE40929F2D575595D542D5F2CA074FEF074056E3C8F738FE77B4DEDB5E7F13B74967D29F38CBD8B86979359BE378C6060F7B0460674F799B36B1BC576F53FD28DF955459817CG89G49G29FE52AF2E221C6B0727D3352AAABA5DAC9AA065CEA972C0980D8B9EEF3BFBBF7AD5BA3433D2EF6B2795FB60F9CA3C20D9891B7485D8CB00EC8D787CF2C09D62D8962DE9FE1050EC36D3F6FFF84FA86A0F4C0178F85BA59C5772FEBE5EF9
	BA3E03619D86BC770347185FE3F9986A327A496F77B48A3ACF9EB43CF79E7FFB6FD311F3E0703A764147E5C1E56C5CAB2DB2FA6E75E0E5745CAB3DF2745CEBE9E56C5CEBF925B17762FE94F7G57DFE84D1284108A108E30A2C0BEA5D77D197BE7945FEC00E42C5E47CEEBD46A3FF1DFECDDDB76DD7D0C6774BE0CFF3BFD2A6A6B437689FFFE9F0E176A73B3A447B977210DA0DCF32B1CD443AB7BF0EEF8649E0666B8577EADC63BF17757683D7B518618DFAA22C739445201F6A0743AF89C778FEC8D4F821BEC8A
	D2EC776197946FD585490F5BA45C5B63201C823098E095C02E8A798700DDA53AEEC687E105A589E0BE40B200AC0022906477A122DB6674EBFE04ED987D8A7B20771460483AB2B576190591D6E9FC0B8D7CE199DF557B5EE862B346FAB9263A4DE31C979B8CF310F0F97A78A59C086DC3B79D383A8F4DBA4072AA98FB4710DF9B7FBB8E30583E27A7A86B3EFA5701FC9D5794EC7585959CF99EAF7C28B9B2BEDD5ACEF1DFC7084727AABC93GB0FEDF96C21A6AAFEC2368CD7BE20EE62B9460C985F8C733EBFAA83C
	F7F2B9DE5E01F9B65F7C34FB8C3B580DF7983CFE7B5EBF1A6B9DE01DE30A6C1108A669A251E0AD1A2A5D9900F49F43942239111D6135745623C5BDF5A118FFDE1E57727A5F8CE0FD43206C0459B3C290DEDF5E7CEEC4FDD890CE7365865433D6125A64BE5FC519F792542EE48447C5345F6D12B19EAC3E48EDFB941E3C7E8F3D467AE42DFEB1BCF9ECFE63DEE3EC7E34D61FBDGF3EB00E8E6771863F7242902FA5BG7573E4FFB4B9BA747AE94D5164706BFDFC52EF6CA3FEC07391F82E9A209CA12B1D62A2E190
	66BDG0F990FBAA88E778FD24C68F5A06DDCAC83BC8D708133A92C8D827D2D7EB49AEE75A05EDF7E30C690A83EF9EE9345B7457F749D41367C79355B0568714D41A1AD669D8A678B0DDF3D1623453FBDB162DF6698DC172592453FE9C3142FFCFD4CF8AF1816D7EC0995DFE5EA7D96DE8A77DFD5C07D171695C76FDFF7BCAE43A18F65F80ECB1407399A5E0F4EEB1C9A5A194E1ECABA53C93CC463D1A762F8AC06728A004CCBB4F6CF76103E1BAE936DCFFA115686658EG75B251EE8890ED220E7B37E612F571E5
	127515A61BB046B3A97364EA748514CBC0ACFA8CB9C9281F7AD8234B786F4DA86341E15827G24E15A877E50FEED3945A6473571B5FCEBBE9EF37E124D0F61E1F3CE72DB06CF98B667A42A03665E8C284B9AA65FFC7C1CB1AF4C1F3DD167B3775B71B577C97757667334C1AB0ED7B26097822CG4884487DC63114G9C84E88468GD0819281D2GD28152811E86F891C038C2B1586BB53B0458F13DF6F840823100484F3338AD1F12225932700A76FD411548B8014BDFFA8457B6AB541F867083A4G4C556529BD
	FC55FB5C0641D16EF1EBBC230DF1FA9439B8DFD5A5712DG1A55A63A6C38D8F94A0F58DCAD0AB31FA7AEE27832B873F9E223C8670955D0179CC7F3E1F90FD9EEE1DC384F42F1317ABF64E17D0C637D646878238CEFB560F97FBFE56B8516DB21AEF59C753FA5904DEEC563356F8D4647325BA391791D910E46643B54BF9A1FA38A9F576B93F6976D9EEF1E0B4798DE9DEF1E0B0FB753DC0C033A1563C95FD58EC1176595437BA547754F8BEF392EFD70F9E6432F1034D8F44A61D2A1469C0769F4823628166E8B32
	D3F508AE1DA7519ABBCE3E45FA75B8B9599ABD573A4B9A19EB5DEB0D1EEB5DE70D4C35FEE8C55FF5413AF4DA277B310EBB236352D81B087BD211D4370EFF10490578C699BB54F3DFA657G431D56F0BFB3604EE838A72D983B034AEEDF00F817DB717C2FC771F8F879BF990D620550DF37C78613D1710B8639FF2563D0EE1E7B3D48E21A468930BECE20FC70F31972C1E9822D13FB0F449E1F03C722677DB5C722677DC70F0CFE2F5FF8A4563900A61F0764CB55E5BE4E621A6CF848CF63891742F0D6A8CBBA6E0D
	CA629D0C273919A622BD537572432C1C2117BF932FF57EE01BE31B232EEB6EE334BBF7822E7BCBAC1E4B1E887943C432C71F1B6CE11DC87A9EE676CD04729C9DF70C6196C279E99D77335ED8B2A36F352EA7F3B4DE5E472FE376B1963DE381657A5C8625811A83D93ADDA68548AE59138877DCB3619CBAEEFABF6166CCA23B545B85DDFE3B617D29666B3F563FF4DD7B60396FBA195DBF40046779582A2328A3BFB3F04BF4E610B9A952870C678188391395CBB140F6EDCC2AA60777D35E5E5943541EF5F2747656
	9920D5EB6F9D2DBDE932798C62FCA566769765002AB8A124E389D21D98E875F9946710F60FB64456083DG7E49F40F7663CA5C675CFA8AE26694B45E1FB3DE5C8C67CF26F31402AA52E1F58C9D5E94689B8A181F89B0BF934267A86589E69D2AD80CE1BC8361F97A6B22B1CF2FCE6067F19D7074A7109EB92E483320E2177EDDEF82BF8BCA01E7EA82BF4B63BA443BC907A58951F5582069006F99C013194073FA4465CEB16B70F59D5341F0C243718F3950FEAD3D9C474FA79E2EB01ECF94CE61F2D9275079C562
	7AE8752D70647EF92EAE6C1FF784C88FE9CA249E6BCCBEB5B8E524FFA6CEB17BE7D6B335B7ABC6FBC618B7E524FFAE0AE8EFEE3FEE67A86DD1ECCD3E1281B419D3426D655E90EE0F6747374B1417975EB056F39640B23BCD2D9774F6269E26F66CB15A61BC7F0C6253D9DDA55E1F9A0EA4AA8CA79295279A5A9CCD7535764E767A36963B3D2E362D33567EE56D19F61D7F054A7D2370B73634B51DED794CB122054F9A20D912392E0AE40E9F336E2447938D02EE678B17E31C8D3139477B2A6C46B6706CA76D86BA
	0778EF99FDBC934A1B26AA169DD3692C4C66229C418AB8F5AA1D17A43A682CEC5ED4EA6381C63B92F9818A9309F6011BF4ABCF0C269B3FFB0EBE2E1947423A65353F97DDB7BE8175717F5F8A6ACF52E5F8E8BD691C2C639E534F0116C5150B6C7B034630EF6D326B67CCB70E4673ECD31867977D44E30B4193DB7F22E6FC677B65F7E5E3DEB88CEDA55DB8FA6C4279128E9B7966G4F42B1719D2FB272AD811E0CB1719D6CC63ECC204D1D86EB88C05534B01FEDDA2C7C75DC8F7B76FA1AB9FF1598DE1AE64EDFFF
	A713EF7A21EE59B40AE9CE382E6D8C6C74B572CD7F56FC5A3D3D9B652F3E492C6F478CEF3B492C6FA2FDAE36C2DD6ACD246F83EEE37FC5C90A25BCE9AC63E4290CA677D10D5791258D4E173E1E785C861AE16DBD891EF3G969A6A34EF72C71C23AC017A0CB149D9E1B772E582CF6E7431707D5708F314E260293A8A9F0F139730FD48B61D5B1F639EE0380E9138FB8FC7524DBB9CC9F73EAA124ED995C9574C5665C140A5CE27187D91A76266C3F9090E7B00654199D096EEA6DCAF4395C239CC4725CAFA3EF4B3
	790A58A3687639E0F8FFD53E7E717E261E6B5F47C1F7D827CF8C6F8F3B2EFF5F4FDE57BE7818BE610E74257FF0C77A52FBAC366F02F1776B7E700DFE1EB02C631EE57B6292A82F52F1AFF7926F9A9D575AC53C3937902EA107FD0F89E53F0E33317DB19E4A49BA6E05C0244CAF87A2E5EE30C7521D30C7523D56CFF4B996423D51CFF48F8994AF086CDBF26D7B82F654457145031B43F8BA6A5262386EEE246943B3F501F20E21EE4AB96603CE69G1BE6A81692GBCF761FC4FB931DE8C293EB6B928DC1271D41E
	722CB7589C461C23FA86517F055D37C2C261E917356F3F9B3C9822C91C27D5223BA23F4EB3DEE2F769FE31BF2437FD3BCE9787F47C1B4CC7A512B789F0F3G96832C84481EC9EDD5D50E4C0B8F335C1F4775B3ABC73BCBB7679DBB20CD04F7B4FC75CCF37E747B75E8EFFFC06E118381D962BFA350644FFB130DA570344E249C687C4930AC2E2E315C6B4712E5B8C2160E9C0C817B44C125AF5447A461F2284C179300FE06EE23F3F6C68B0EB212B648FCF29150AD19C9F1721D5DF4F71185382ADB698E713EEDF8
	2FB54A9DA23FBF448F7CEB3D927F161F056A6CDA07470F3F6926D85BFDAB455A6F32EF3AD2203C90E0692DA4F301043FE19C11073066AAEA3FF034F436B534B71DED005C455ED95FE27F6264E76DE49717C45FE97272FC3D4CE5F8C5A299D65DCA5F28FEA01B3FD11D3E56780D696732799BD5648F57CF104D5F280E64DFA01B3FD1A57E587172384F426FF3DA84FD5CD7F67E891EDEF92004BE7F2D30A9FD3297FFD8D676710172127D07F6915F464F43EDD434055B782BFD6CEDFC5E9E66DB50BF96BE3E075C5B
	FD35D832643611B18C67DBF0D53EBF194A5A995CD5794C40E354294C7F2C3719E357998C5FF81BB9F6FD5FC9312B836A965E267F9EAD6ADD69E870BE336B222DFC8D77A8DE391E7F8AD157E93AD3505FE79B5E37BB2FFF8CE03D2EFD705F511F49FF828CB96FF7C2CEB0A06469BE5544AED5B2AE9E28D9777C7756D9046CA6E1BD3FE4EA9232712628C948E197C9CD42BA55679F448F4DF20C208A9F711B65926D3C2EE4EFC9D9A8A8877676849459ABF906CAC40FA286F72942CE6D0BA3A32568D4C322274429E1
	0232D75D956C973E4181E09D3CC5B0822B43B5E32F29DDD5D84FCF74D44152522C53E0BB059A3C8566FDBBF24A56CE7B4572F91D36C20BE0818B3BE5EE521C818CB018D1711422A9C60B085DB202C6AF89741D502EAB46EFA132A6DAE2FEA3BFF7B6DF8B52E61B3F05F856463E1D84FC61F768DE2C52066744316EBA5B7BC40FC795BD3D308F613DE75D10AA076F41D65A307DA051E8153C6E37B6147B0201677F81D0CB878839E439EDD896GGCCC2GGD0CB818294G94G88G88G2CF0B8AF39E439EDD896
	GGCCC2GG8CGGGGGGGGGGGGGGGGGE2F5E9ECE4E5F2A0E4E1F4E1D0CB8586GGGG81G81GBAGGG1296GGGG
**end of data**/
}
	/**
	 * Returns the buttonPushed.
	 * @return int
	 */
	private int getButtonPushed()
	{
		return buttonPushed;
	}
	/**
	 * Return the JButton2 property value.
	 * @return javax.swing.JButton
	 */
	/* WARNING: THIS METHOD WILL BE REGENERATED. */
	public javax.swing.JButton getCancelButton() {
		if (ivjCancelButton == null) {
			try {
				ivjCancelButton = new javax.swing.JButton();
				ivjCancelButton.setName("CancelButton");
				ivjCancelButton.setText("Cancel");
				// user code begin {1}
				ivjCancelButton.addActionListener(this);
				// user code end
			} catch (java.lang.Throwable ivjExc) {
				// user code begin {2}
				// user code end
				handleException(ivjExc);
			}
		}
		return ivjCancelButton;
	}
/**
 * Return the ScrollPaneTable property value.
 * @return javax.swing.JTable
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
//private javax.swing.JTable getCommandTable() {
private javax.swing.JTable getCommandTable() {
	if (ivjCommandTable == null) {
		try {
			ivjCommandTable = new javax.swing.JTable();
			ivjCommandTable.setName("CommandTable");
			getCommandTableScrollPane().setColumnHeaderView(ivjCommandTable.getTableHeader());
			getCommandTableScrollPane().getViewport().setBackingStoreEnabled(true);
			ivjCommandTable.setModel(new com.cannontech.common.util.KeysAndValuesTableModel());
			ivjCommandTable.setAutoResizeMode(javax.swing.JTable.AUTO_RESIZE_ALL_COLUMNS);
			ivjCommandTable.setCellSelectionEnabled(true);
			ivjCommandTable.setOpaque(true);
			ivjCommandTable.setBounds(0, 0, 200, 200);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjCommandTable;
}
/**
 * Return the JScrollPane1 property value.
 * @return javax.swing.JScrollPane
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JScrollPane getCommandTableScrollPane() {
	if (ivjCommandTableScrollPane == null) {
		try {
			ivjCommandTableScrollPane = new javax.swing.JScrollPane();
			ivjCommandTableScrollPane.setName("CommandTableScrollPane");
			ivjCommandTableScrollPane.setVerticalScrollBarPolicy(javax.swing.JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
			ivjCommandTableScrollPane.setHorizontalScrollBarPolicy(javax.swing.JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
			getCommandTableScrollPane().setViewportView(getCommandTable());
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjCommandTableScrollPane;
}
/**
 * Return the JLabel1 property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getCommonCommandLabel() {
	if (ivjCommonCommandLabel == null) {
		try {
			ivjCommonCommandLabel = new javax.swing.JLabel();
			ivjCommonCommandLabel.setName("CommonCommandLabel");
			ivjCommonCommandLabel.setText("Common Command - (user-friendly alias)");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjCommonCommandLabel;
}
/**
 * Return the JTextField1 property value.
 * @return javax.swing.JTextField
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JTextField getCommonCommandTextField() {
	if (ivjCommonCommandTextField == null) {
		try {
			ivjCommonCommandTextField = new javax.swing.JTextField();
			ivjCommonCommandTextField.setName("CommonCommandTextField");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjCommonCommandTextField;
}
/**
 * Return the JLabel2 property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getExecuteCommandLabel() {
	if (ivjExecuteCommandLabel == null) {
		try {
			ivjExecuteCommandLabel = new javax.swing.JLabel();
			ivjExecuteCommandLabel.setName("ExecuteCommandLabel");
			ivjExecuteCommandLabel.setText("Execute Command - (actual command sent)");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjExecuteCommandLabel;
}
/**
 * Return the JTextField2 property value.
 * @return javax.swing.JTextField
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JTextField getExecuteCommandTextField() {
	if (ivjExecuteCommandTextField == null) {
		try {
			ivjExecuteCommandTextField = new javax.swing.JTextField();
			ivjExecuteCommandTextField.setName("ExecuteCommandTextField");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjExecuteCommandTextField;
}
	/**
	 * Return the JButton1 property value.
	 * @return javax.swing.JButton
	 */
	/* WARNING: THIS METHOD WILL BE REGENERATED. */
	public javax.swing.JButton getOkButton() {
		if (ivjOkButton == null) {
			try {
				ivjOkButton = new javax.swing.JButton();
				ivjOkButton.setName("OkButton");
				ivjOkButton.setPreferredSize(new java.awt.Dimension(73, 25));
				ivjOkButton.setText("OK");
				ivjOkButton.setMaximumSize(new java.awt.Dimension(73, 25));
				ivjOkButton.setMinimumSize(new java.awt.Dimension(73, 25));
				// user code begin {1}
				// This listener is not used because it's calling class implements it instead
				ivjOkButton.addActionListener(this);
				// user code end
			} catch (java.lang.Throwable ivjExc) {
				// user code begin {2}
				// user code end
				handleException(ivjExc);
			}
		}
		return ivjOkButton;
	}
	/**
	 * Return the JPanel1 property value.
	 * @return javax.swing.JPanel
	 */
	/* WARNING: THIS METHOD WILL BE REGENERATED. */
	private javax.swing.JPanel getOkCancelButtonPanel() {
		if (ivjOkCancelButtonPanel == null) {
			try {
				ivjOkCancelButtonPanel = new javax.swing.JPanel();
				ivjOkCancelButtonPanel.setName("OkCancelButtonPanel");
				ivjOkCancelButtonPanel.setLayout(new java.awt.GridBagLayout());
	
				java.awt.GridBagConstraints constraintsCancelButton = new java.awt.GridBagConstraints();
				constraintsCancelButton.gridx = 1; constraintsCancelButton.gridy = 0;
				constraintsCancelButton.insets = new java.awt.Insets(10, 20, 10, 20);
				getOkCancelButtonPanel().add(getCancelButton(), constraintsCancelButton);
	
				java.awt.GridBagConstraints constraintsOkButton = new java.awt.GridBagConstraints();
				constraintsOkButton.gridx = 0; constraintsOkButton.gridy = 0;
				constraintsOkButton.insets = new java.awt.Insets(10, 20, 10, 20);
				getOkCancelButtonPanel().add(getOkButton(), constraintsOkButton);
				// user code begin {1}
				// user code end
			} catch (java.lang.Throwable ivjExc) {
				// user code begin {2}
				// user code end
				handleException(ivjExc);
			}
		}
		return ivjOkCancelButtonPanel;
	}
	/**
	 * @see com.cannontech.common.gui.util.DataInputPanel#getValue(Object)
	 */
	public Object getValue(Object o)
	{
		int rowCount = getCommandTable().getModel().getRowCount();
		String [] keys = new String[rowCount];
		String [] values = new String[rowCount];
		
		for (int i = 0; i < rowCount; i++)
		{
			keys[i] = (String)getCommandTable().getModel().getValueAt(i, 0);
			values[i] = (String)getCommandTable().getModel().getValueAt(i, 1);
		}
		keysAndValues = new KeysAndValues(keys, values);
		return keysAndValues;
	}
	/**
	 * Called whenever the part throws an exception.
	 * @param exception java.lang.Throwable
	 */
	private void handleException(java.lang.Throwable exception) {
	
		/* Uncomment the following lines to print uncaught exceptions to stdout */
		 com.cannontech.clientutils.CTILogger.info("--------- UNCAUGHT EXCEPTION ---------");
		 exception.printStackTrace(System.out);
	}
	/**
	 * Initialize the class.
	 */
	/* WARNING: THIS METHOD WILL BE REGENERATED. */
	private void initialize() {
	try {
		// user code begin {1}
			//set the app to start as close to the center as you can....
			//  only works with small gui interfaces.
			java.awt.Dimension d = java.awt.Toolkit.getDefaultToolkit().getScreenSize();
			this.setLocation((int)(d.width * .3),(int)(d.height * .2));
		// user code end
		setName("AdvancedOptionsFrame");
		setLayout(new java.awt.GridBagLayout());
		setSize(622, 407);
		setVisible(true);

		java.awt.GridBagConstraints constraintsAdvancedOptionsPanel = new java.awt.GridBagConstraints();
		constraintsAdvancedOptionsPanel.gridx = 0; constraintsAdvancedOptionsPanel.gridy = 0;
		constraintsAdvancedOptionsPanel.gridwidth = 2;
constraintsAdvancedOptionsPanel.gridheight = 6;
		constraintsAdvancedOptionsPanel.fill = java.awt.GridBagConstraints.BOTH;
		constraintsAdvancedOptionsPanel.weightx = 1.0;
		constraintsAdvancedOptionsPanel.weighty = 1.0;
		constraintsAdvancedOptionsPanel.insets = new java.awt.Insets(5, 5, 5, 5);
		add(getAdvancedOptionsPanel(), constraintsAdvancedOptionsPanel);
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
	public static void main(java.lang.String[] args)
	{
		try
		{
			AdvancedOptionsPanel aAdvancedOptionsPanel;
			aAdvancedOptionsPanel = new AdvancedOptionsPanel();
			aAdvancedOptionsPanel.showAdvancedOptions(new javax.swing.JFrame());
		}
		catch (Throwable exception)
		{
			System.err.println("Exception occurred in main() of javax.swing.JDialog");
			exception.printStackTrace(System.out);
		}
	}
	/**
	 * Sets the buttonPushed.
	 * @param buttonPushed The buttonPushed to set
	 */
	private void setButtonPushed(int buttonPushed)
	{
		this.buttonPushed = buttonPushed;
	}
	/**
	 * @see com.cannontech.common.gui.util.DataInputPanel#setValue(Object)
	 */
	public void setValue(Object o)
	{
		if ( o == null || !(o instanceof KeysAndValues ))
			return;
		
		keysAndValues = (KeysAndValues ) o;
		
		for (int i = 0; i < keysAndValues.getKeys().length; i++)
		{
			((KeysAndValuesTableModel)getCommandTable().getModel()).addRow((KeyAndValue)keysAndValues.getKeysAndValues().get(i));
//			getCommandTable().getModel().setValueAt(keysAndValues.getKeys()[i], i, 0);
//			getCommandTable().getModel().setValueAt(keysAndValues.getValues()[i], i, 1);

		}
	}
	/**
	 * Show AdvancedOptionsPanel with a JDialog to control the closing time.
	 * @param parent javax.swing.JFrame
	 */
	public KeysAndValues showAdvancedOptions(javax.swing.JFrame parent)
	{
		dialog = new javax.swing.JDialog(parent);
		dialog.setTitle(getDialogTitle());
		dialog.setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
		dialog.setModal(true);
		dialog.setContentPane(getAdvancedOptionsPanel());
		dialog.setModal(true);	
		dialog.getContentPane().add(this);
		dialog.setSize(580, 420);
		
		// Add a keyListener to the Escape key.
		javax.swing.KeyStroke ks = javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_ESCAPE, 0, true);
		dialog.getRootPane().getInputMap().put(ks, "CloseAction");
		dialog.getRootPane().getActionMap().put("CloseAction", new javax.swing.AbstractAction()
		{
			public void actionPerformed(java.awt.event.ActionEvent ae)
			{
				setButtonPushed(CANCEL);
				exit();
			}
		});
		
		// Add a window closeing event, even though I think it's already handled by setDefaultCloseOperation(..)
		dialog.addWindowListener(new java.awt.event.WindowAdapter()
		{
			public void windowClosing(java.awt.event.WindowEvent e)
			{
				setButtonPushed(CANCEL);
				exit();
			};
		});

		dialog.show();
		if( getButtonPushed() == this.OK )
			return (KeysAndValues) getValue(null);
		else
			return null;
	}
	/**
	 * @return
	 */
	public String getDialogTitle()
	{
		if (dialogTitle == null)
			dialogTitle = "Edit Custom Command File";
		return dialogTitle;
	}

	/**
	 * @param string
	 */
	public void setDialogTitle(String string)
	{
		dialogTitle = string;
	}

}
