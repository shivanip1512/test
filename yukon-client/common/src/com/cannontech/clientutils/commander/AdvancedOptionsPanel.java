package com.cannontech.clientutils.commander;

import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.Toolkit;


/**
 * Insert the type's description here.
 * Creation date: (4/11/2002 3:51:40 PM)
 * @author: 
 */
public class AdvancedOptionsPanel extends com.cannontech.common.gui.util.DataInputPanel implements java.awt.event.ActionListener 
{
	private javax.swing.JDialog dialog = null;
	private YCDefaults ycDefaults = null;
	private javax.swing.JButton ivjCancelButton = null;
	private javax.swing.JButton ivjOkButton = null;
	private javax.swing.JPanel ivjAdvancedPanel = null;
	private javax.swing.JPanel ivjOkCancelButtonPanel = null;
	private javax.swing.JLabel ivjCommandPriorityLabel = null;
	private javax.swing.JTextField ivjCommandPriorityTextField = null;
	private javax.swing.JCheckBox ivjConfirmCommandCheckBox = null;
	private javax.swing.JCheckBox ivjQueueCommandCheckBox = null;
	private javax.swing.JCheckBox ivjShowMessageLogCheckBox = null;
	private java.awt.Panel ivjAdvancedOptionsPanel = null;

	private int CANCEL = 0;
	private int OK = 1;
	private int buttonPushed = CANCEL;
	
	/**
	 * AdvancedOptionsPanel constructor comment.
	 */
	public AdvancedOptionsPanel() {
		super();
		initialize();
	}
	/**
	 * AdvancedOptionsPanel constructor comment.
	 */
	public AdvancedOptionsPanel(YCDefaults defaultSettings) {
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
		if( event.getSource() == getOkButton())
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
	 * Return the Panel1 property value.
	 * @return java.awt.Panel
	 */
	/* WARNING: THIS METHOD WILL BE REGENERATED. */
	private java.awt.Panel getAdvancedOptionsPanel() {
		if (ivjAdvancedOptionsPanel == null) {
			try {
				ivjAdvancedOptionsPanel = new java.awt.Panel();
				ivjAdvancedOptionsPanel.setName("AdvancedOptionsPanel");
				ivjAdvancedOptionsPanel.setLayout(new java.awt.GridBagLayout());
	
				java.awt.GridBagConstraints constraintsAdvancedPanel = new java.awt.GridBagConstraints();
				constraintsAdvancedPanel.gridx = 0; constraintsAdvancedPanel.gridy = 0;
				constraintsAdvancedPanel.fill = java.awt.GridBagConstraints.BOTH;
				constraintsAdvancedPanel.weightx = 1.0;
				constraintsAdvancedPanel.weighty = 1.0;
				getAdvancedOptionsPanel().add(getAdvancedPanel(), constraintsAdvancedPanel);
	
				java.awt.GridBagConstraints constraintsOkCancelButtonPanel = new java.awt.GridBagConstraints();
				constraintsOkCancelButtonPanel.gridx = 0; constraintsOkCancelButtonPanel.gridy = 1;
				constraintsOkCancelButtonPanel.fill = java.awt.GridBagConstraints.HORIZONTAL;
				constraintsOkCancelButtonPanel.weightx = 1.0;
				constraintsOkCancelButtonPanel.weighty = 1.0;
				getAdvancedOptionsPanel().add(getOkCancelButtonPanel(), constraintsOkCancelButtonPanel);
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
	 * Return the AdvancedPanel property value.
	 * @return javax.swing.JPanel
	 */
	/* WARNING: THIS METHOD WILL BE REGENERATED. */
	private javax.swing.JPanel getAdvancedPanel() {
		if (ivjAdvancedPanel == null) {
			try {
				ivjAdvancedPanel = new javax.swing.JPanel();
				ivjAdvancedPanel.setName("AdvancedPanel");
				ivjAdvancedPanel.setLayout(new java.awt.GridBagLayout());
	
				java.awt.GridBagConstraints constraintsCommandPriorityLabel = new java.awt.GridBagConstraints();
				constraintsCommandPriorityLabel.gridx = 0; constraintsCommandPriorityLabel.gridy = 0;
				constraintsCommandPriorityLabel.anchor = java.awt.GridBagConstraints.WEST;
				constraintsCommandPriorityLabel.weightx = 1.0;
				constraintsCommandPriorityLabel.weighty = 1.0;
				constraintsCommandPriorityLabel.insets = new java.awt.Insets(10, 10, 5, 10);
				getAdvancedPanel().add(getCommandPriorityLabel(), constraintsCommandPriorityLabel);
	
				java.awt.GridBagConstraints constraintsCommandPriorityTextField = new java.awt.GridBagConstraints();
				constraintsCommandPriorityTextField.gridx = 1; constraintsCommandPriorityTextField.gridy = 0;
				constraintsCommandPriorityTextField.fill = java.awt.GridBagConstraints.HORIZONTAL;
				constraintsCommandPriorityTextField.weightx = 1.0;
				constraintsCommandPriorityTextField.insets = new java.awt.Insets(10, 10, 5, 10);
				getAdvancedPanel().add(getCommandPriorityTextField(), constraintsCommandPriorityTextField);
	
				java.awt.GridBagConstraints constraintsQueueCommandCheckBox = new java.awt.GridBagConstraints();
				constraintsQueueCommandCheckBox.gridx = 0; constraintsQueueCommandCheckBox.gridy = 1;
				constraintsQueueCommandCheckBox.gridwidth = 2;
				constraintsQueueCommandCheckBox.anchor = java.awt.GridBagConstraints.WEST;
				constraintsQueueCommandCheckBox.weightx = 1.0;
				constraintsQueueCommandCheckBox.weighty = 1.0;
				constraintsQueueCommandCheckBox.insets = new java.awt.Insets(5, 10, 5, 10);
				getAdvancedPanel().add(getQueueCommandCheckBox(), constraintsQueueCommandCheckBox);
	
				java.awt.GridBagConstraints constraintsConfirmCommandCheckBox = new java.awt.GridBagConstraints();
				constraintsConfirmCommandCheckBox.gridx = 0; constraintsConfirmCommandCheckBox.gridy = 3;
				constraintsConfirmCommandCheckBox.gridwidth = 2;
				constraintsConfirmCommandCheckBox.anchor = java.awt.GridBagConstraints.WEST;
				constraintsConfirmCommandCheckBox.weightx = 1.0;
				constraintsConfirmCommandCheckBox.weighty = 1.0;
				constraintsConfirmCommandCheckBox.insets = new java.awt.Insets(5, 10, 5, 10);
				getAdvancedPanel().add(getConfirmCommandCheckBox(), constraintsConfirmCommandCheckBox);
	
				java.awt.GridBagConstraints constraintsShowMessageLogCheckBox = new java.awt.GridBagConstraints();
				constraintsShowMessageLogCheckBox.gridx = 0; constraintsShowMessageLogCheckBox.gridy = 2;
				constraintsShowMessageLogCheckBox.anchor = java.awt.GridBagConstraints.WEST;
				constraintsShowMessageLogCheckBox.weightx = 1.0;
				constraintsShowMessageLogCheckBox.weighty = 1.0;
				constraintsShowMessageLogCheckBox.insets = new java.awt.Insets(5, 10, 5, 10);
				getAdvancedPanel().add(getShowMessageLogCheckBox(), constraintsShowMessageLogCheckBox);
				
				// user code begin {1}
				// user code end
			} catch (java.lang.Throwable ivjExc) {
				// user code begin {2}
				// user code end
				handleException(ivjExc);
			}
		}
		return ivjAdvancedPanel;
	}
	
	/**
	 * 
	 */
	/* WARNING: THIS METHOD WILL BE REGENERATED. */
	private static void getBuilderData() {
	/*V1.1
	**start of data**
		D0CB838494G88G88G56E017AEGGGGGGGGGGGG8CGGGE2F5E9ECE4E5F2A0E4E1F4E14DBC8DD8D4571536352D5BCDD3131816CDECEB9A1B58AC5D38DF53FE361B363635CD3AB5DFE9EBB6A4F5D337716B32DB1B72EDEDC39B765B477F0F9AD0D0D1D087C57991E18606C010888CA33F43CF8495AD3BB2B08F989D660DB38F991418BD675EF75EFB4C8F02E93E2FFC5F713EFB6E39671CFB6E39675CFB5F9B055CA32BD29292F385A1A1C5D07EEA9384E1D927A0BC1115FD06F0F1D9D119026E2F
		90E037506DC806B2BDBBAAF34D25284CEC61356EF42897C05D7CCED466FEF84E91623BF7B643C398B9DB1B84A179050B4B661793D3C6F252D8FD5A3F934AB7G8C003382AC4FC6791F7267A97889283F21BCA3A4A402DE3B11C6366569701B943A745D3C5C4AE81E7097A86D8E284F822C835838DB631941347BE8FD1DD29F3AEF15307D1F77A56EC2D9BA7E8465FBF85FBE25B4881FE306C892E28E14C05950F7ED7C4C28B4D9B1EAF73BA537AC0ECED408FE0F64156B6B2D163EA96F3858E7F70B2E75FBD03F07
		EC692CAF4C935439AEBF59552048DAF1ED153065510C303AA467F09DCAF2B4DD4A2BDFCD0C4EC15EBFE33645FE93D0DF8540751CFD4398C64F19510A71A9E7FDFD3363065DBDAABAFABD32D3F27BF47A664F103E8539D119ABF3917765AE14B3G4FAE002539B40E370BE99C03371E9462C65ECA4C8AB30EAD394A5C6D5546917B7A07938FB09FF92F9C67598AED93G33G2BG5683EC8148E73ED9F9328C4A669139C51A1C145C4D9E0F456570F6CB8E51854FB02291D9411D93C7A52FC390387FFD5ED69EC29F
		02383DF3FDCF73170B8279CB64394784D92ECCCC89B136271E4FCB5C39CF9953BEEDECFCDC7995B82EA6408F8148G77GC482EC5CC7631AB9D9FD27F1F5405CF95DF6D77F5408476E15ADD3CE17C374AA63FB51BF5F78C2816F776F0B6E476D3226DAEF174DDF29CFCC8BE1174DEB0EA446A8769076EBF661328DB361E49B76874F8702F5BF7AE1218D7D508C4F890A0FF27CC906DF2643737571956637D80F0036787D643703EF5098EC498F89592653A1479071DDE362F09E57471F97B8372FD5E25CD98D7875
		G51G31G598722B24B811A8E507CEEB8D939C03F6D534DAD6F3B2CFC9F1422DF765ABB5CBE99D76C853B57E99FF109BEC121392F9CF59F19536F53ECBD0D020CB651ADFA6D3846013BFDD284C772F11A4F176FC69A2F48DA871C13A288187488447771729CA8DDF61FFC5E63304B22C6415B1FACC7DB5E30DFG0E40G1E179FC01BFF0D453BE1F81E856036AAB2A1EE93543729F6A9BB35874AAEF0EC5185F153B90AEA583DB3A43F2882E309F8C3F44BD651A33A9D7060D35ACED6E4E8F69913D41DCD8C0FEB
		2485947729785A0A5D9C8FBC74D62676C656EE17E52FF3E4CA967BC5D9F63A47357EEDCC975F1C65679314B5DE05BEA69C0C4A4C83A8B9C8630B2A48510D4FA6C95726BC83B8F30A1DEAD91CEDG7A010314AB0BED18BB7D8749CFDF9DA4BF356E7A22D07E7CDBA17D14796AC16E1FEB8F862EB59363294978FA5760A257FDA0788F06DE23DAEC79697B1611B4CF9B377B3D55E877E850E5BBC0EEBE59FDC725012FB70B5DA7528A6B75420C098E4D4BB8ED02B13C0F259BC37B589B4650BE16EB1C5F078A0C21FC
		08379DB3E2EE11265DBA1FACB16EE1EB586D961952EA97016778D7875096CE37D33E68F40C0BF23F732688731C0F3E759CDB0365705C14CF3EB4FD0CFC296866A3427AAB39A173D944972B93A372797CC666EB3E44F5AAB5214DC517B88906EA11265C32121B597AD670CAFCA92C8ACDDBDC057EF8CD1CD168CED6258A98C7DCD322BA3635769DE8E32FF392268A972C329F6C19516F87B702FE38F75C8EE5FAC1D4E6925731AE9767FF6E7E2695E39F771BE74C281781684DG3682721B472B8E68562B62B0AD12
		4B454D2F4E4B0A6AEC1CC7717A148882F0C1F729E3F938FACFC05B85E5DC3C7D51EA9C17CB978B9C85B82E2D6A9E91F385473D4EF01F04496BC8255C5B1D2D5F43BAFC65A16790E1CD811F3B278B8273D6FC8D725D8C78ED6A588F1B75312A596713C61DACADA873F16EBA76E57B492B769BF6FF05EF9AFC3B3E3E536A343B24F1E863FB47F5AC26619805C334F73C7AAE5A3B8E6AFDG9307C866F366E01F7929998F94E3CE2F8F76BC8E51CFF60BB66378B197A9E85A23D5696D31FE0BA12CBD169C62F6D8F1A8
		509E16DA54E3AD60B7283A75576C565983761310FA47E136C81F04B713388FAB7514B7D34807850A015FE4FB1C68C321E365B6408B07A1EE9DD67677D0723D4C4B4CDE8D078377B8B18CEFBB9C3847713CCBFB1CA1E8DBFF18F2EEDD9D0E2B68887879919A57C3EF6E520D2BCFF23AE5F5CCED35388EA52FF35C69A61C0D61E0B74D82934FDB1B589CDB016784G471D3C0938D9282FD0F1CF5CC25CBA28EFD4F13FE338ADD05F2E629AE69117EE084AACB0D00E5A7AAE460892357EAA2B372B759B6F3E5F7C9288
		A8EBEE8E7B54DFDC06BE27F17B3EDD07F369D76B4D2CBE235066883F1E598B25C39C33CF39A05DC8BE315723EC67G3F5A0076CBE536C77FF399687C770F53388647A06841B26D77082342ECA152DAEED27C0C815AF834B9609BG068AA9FEFE67960E7F065367447844E5C45F227834C1254B1CC5D91312436ED2F5A91CA53ACDAADDBF23131DB2DBBFDB8B714EA02DD1D04778522E90711A1DC7F94CBFF2DB9F73CB0E727DD2BB14C3C7C946211B3856F421296222536D1026DBA436EB16FD95560E7E3E5E7E33
		D7FAFB2E34586001470FD5537A78D117F9A2EC7C100FF27FD826149B5B731518B7416C0FFD56C3DB5CD144ED3F199BB06F350902D081A5EFBFF1CB5F0E31661420EF9F1C530E3A3EB6277FA76634234E47157EFCDCBFBACE6772789F2D91DCDFAD89FB3F50F40C0F472114437B7305DC4305E4EE575F5E42F5A7FB27C4DE2F1B25BA177363BF6B6D177DD0F6E2C298B9AB957EEB0E855E293CD802E3D1F3CB05FDDA0650245DC7DC3CCD7E3D7998651462F40CF939C5F056823096D14C3B3F89E907206EAF2218E7
		EBBC8965857BD56514F5566114A5AF5B9882324FAB410A12E7B816E5EBE57D7F68665D1D0F3F13C636FF6A9BDF9056FDACA6E1C7981BC494F1DB2CD34A383D793A7DEDFCB37AFD8C34A59F07F385C09340G006BB80D6D01C65491763728FE33AC5BC7A7D8B625ED6F8527B82D21B190F05EC7DAD0D34E63454663616C839CBAE1CAFAC72EC2869C30E31E6736993A9DE81BA54735783962F8F04E63B96CE1B6EFEB0E739C1622629FE37827F5F81E5BCEB1BC5623204DFC02467D19A664296E357934A96379DC93
		7A936E0BE1EF623C4902E5BF903A4779584B2D589E813C1E8208865886A094936F234DC739CDC35873ACEC34A926103C94CB78B344CE4B1D4F6FFBAD38679911E4D91AC47E044F37202DE54943109CE730200FC10A1E10B92902BFE1C13FF709E38C4D732A0F2D1B8298173118F26D6F53A8CF8F94D3AEFDA49DF1FE282FD0F1118C77945423D5DCF57A7BBEB796877B458A8547E5585E370CD0674694C11FF34D567073F55AFA677932DA593E956CBCB06391B92E434A76CEE3E3BED1750B9EEB360A6B7075C9BE
		A7FAA20F231F380D74AE7B0CB4857493A5B8B72BD3A94F4503CDF28157G5087007B1E350CE6E7FA5E9D461EA3B8CD986BA91F32C02A36FD328877793D57DA304DE519821FF3F38A1EEBDF1E43BFCE11DDD08ABE8440E91E750422E90776A1G3EEEB726523D348C3848D25A171806714EACA05637F91D8E0BFD5C462C41E3FEEE9A65542D259473ADE94833208C62G002D0C4E357E9394EFA39FFBC830AD298F196B368C94A6BA4A149CDE2665204D2FFFB8117339FA0D78947C7D432CADDC4E5C20740F5171A1
		BFCB92FED71AA704BCAB69F25A3E0CF44567CBCE5278048BB83EE1287B811683F0DA61BA653FB5AAED94BB07EF01FA9CC0F6B92D511F265E5DDD70163B6CF7A0ED317DF83C7F9A1B4B12F29E6F93D47C37995E2C43F3FFEACFA3FFEA02365565146FF3DB5156692722B20BG9AG86GE4006527A81E3FE245F7A1735E3F8EC89E9B8484176EEA1073F737A17F5540EBBD40D600F8006C8A484795447F54D93C3F8F373FB8A7C9B2CEBCE48D596E04E394CB9FDEBB1E3C388D73B374B6ECG3EFD95DA1CF4D4044B
		254B193F78AB382DF62B78C798FE358E4FED184A70D80F023621CA7219146B01F398D329490C2F8CA75F4A6EF7320D01393C17610B0C01397CDFCC144B6B20ED251164BF0D05E20B6F6B1EFB527E52672FE07F9D7FC0E5F07FB8549ABE5F9435063EE3ACEB8DFD47E8EC1D7F0E312EB57C9DE3E3EB709D63F4DBF85DEE375DB997FE3CAD745E6513ED41FB177B5BC26FDDA25A0277AED5ACBE8AA64803A65A3B4CB0DC91546BD4DCDCA66DE706CC64CF1F3EE000B2926A9B81E281322BA0E7G8C82AC85082CA25A
		47996D8628EF85C82E06B882E085F0GAC85D8DDCD34BBAFACD166B5DB61251FEB1427BE8734AD2D8AFD87047A19835AE2CD21E9677AED252A8B5AF4D7G0DFE8F34BB20ADAB8CDF740D9DA1E44D278B1F03026BB4AF9B2AE9DEBE6D25F909D16D57D90538BAB344B7B3614EB31C836ACBD55CE6D338B509FD6FF6AD7619757B4C5F0715B15FFB07BBC10A0E876C89B3F00FB7A2F9590B2635E6344BF0861E1FD71B695EE2F9C670BE70718C5ADFEDB773FD604E1A28CC83G6E47DDB5C8133543BA0F9E14BB1394
		2803BEB6003DCAFD38C64B2FDC0F4233F8A64284E9F7BB7A3CCE496B14E7EC769151455B1FF36585378FC0ECE8F50AAE8727E9E3672EF72644A9D1A1EC1990C72FD9A4BFEF4FB66EE3BC5CE3CE6FE4G8569D1F389E97AA72469EE5167330F0BB6E9BC10A66B58A1CD17D627CB34BA3DF0BC153C1AC26A3822E78F0421E503D369AEDD445CE471CA53BE116F0A37B1BB7F62027E1E65A9E58E2221646DCF76685B4DEF727621B7297D62BEFD3BC7E90FD05BB7D607EA0FD45B9FBB932AFD3D5A3E79D2A8791B54F6
		CBF1A87DE354765F8C685B63157EC535D1195C974FDEA7DF2C2B653E58872523161F8B39AFFE59FBA7DFCC5241AC74DDDE4BFD91574B6A5AC0DFFC5702F321BF21F07C7746B1D674DE534F4F472B8275C73D565652782C2ED06D9BEAE97D5DE0DBFF65DB96D1FF17E53C951B10A4045E7FC757AA7B7FDA6DAE106FF1FE1CC17B543CBA30D59D6D6177E752F9E6D99D6D614FE6A26DFA28C78344829CE1BA41FED6203F5846D0B8FE07FAFF07267B3F6D541FDD2CAF44A49C8B23FB7ADBDC6741373433CBFC7FC792
		15334BD818338BDBAE4A5965A21DDD163FC5E730F7DB755FB3BD1DD518986E4E6BD9C5EE0CCEFEF97C07957947CB8AC24BD717B65761D7BD6A79291E4E16E7AE64076E2B86905E775EFD690A5D066BE9FE124C040BD0F17FFA06F0D1AAAE67B25DEF271FA65CCCB16952F41AF4496A8F733E0CBB2FC06F4B0A4ED3DF7FE9BABBCFDEA79FDA8B3898D54ED72F138F999A4807DEE13403D077GACEBD82C8F35F7892AAE5F2A22F3662A8612F94BC56F73229B684C757BAE4CE159EF537927389D6F4B421DFFDA601C
		45EF5FA877BE1FF1F7E74DD76E32DFC1665D1DD13F4E56E7515B01E7546FB23C716D40B3EAD7A64DDB83342DFA1B6CF5229B63E372994861GD600A1G8F40B20015GEB81B6GEC86588E105E9815D9GE084E88298G18E8A4FB3F50D1BA0F3D154B945DA9136C10FFD1EF0759C63E7E16AB656A46603D95577FE7AC472D0376CDG5BG121BA0C7B411BE051DF77CFE6D2CFF1E6F57FE5DC3FAD91BB49D8615E7D7D330DE3C5F837B7463D922502D00F28D4053E17B251B757D22943A2DD046816CEC8E576F17
		E7301FC1E9B7ABE53BD28E056D17F7F9BE7F73046DF73BD85FEF29C297211411E17B151E5777DB2F50EDD24A18307DFCC177G8BDBDF9F3A4BFE5FD8F4BF3E7EAEFAF11D45B7872E4BE106CF37842E4B7FD063E9AE3479ADF44E78DED5A8791BAD5CBE3116F0F6DA6222FE49AD9ACD1E72DC8EE59340002E0D2F13BFF661DE5FB7A796FE1BFDBF4D1F1F55BD3F1379415FFDDC7DC0E570B157F7073FDFE86E3E737DC2E7F7687B053E6E607B05735D216F977E5C2D3FDF603861EE4CF36390AB87D5BAD145DDD2F1
		D799CF44DDF4BA6489956FE1FAA13EDDC401043F41741AE638C185F7CB073B246016F423AF4010A7A4AF757DE8B76655B1274B456BFF43E81CEE1FA82B76381769687458F5FABF2062D0EF657E046D51A640877DADF4A6EF71A06E9E282FD4F1EBBCF4CEDF53C2717C67BD61676C17BD216F2BE2FAC25FD73D5AB37FF717FF6889F71F4575CB6CC57D62C037DCAB695C4F76EAA5D02FD3F16B4CB48E2B0AFBE29F59E0050ACBE35FBE2D017ABA9567E2388DD05F2A621EFD0F78452938A3E66257FE16F00F1DE26F
		1B216ED2F1BFB186737B13117879D53ABF327D6533D05F2C62AC0DA4A3D6457D3A14E4A437924ED6C67C32D5DC65E5428D2B38870B095FDA9557D94274037AA69577E489755D22621A4ED3DFE39B618E54D2DF2B0A3BD9CBFD7B5AA8367EAFCE3192E746F44F3F3D7E414735F46F87A9030F7925AADACB2BD4BB1C2EA6BBC42A384F307DF3BC54735A89E748233E65AA6EC3F95437CE45BDB30438E5D0DF25626EE7B21E027A46F6BA43F6DCE4F1C3F24B7C9B1CABEC6DEF0176EDAA4DE58F7BEE8396AE5F77AF69
		43B9616777EFFB687CDE50911C73826F120CC0E385207365E0C760797DC0AF5EC7FA3C621868750A8E7ED92A72FDD095DB4F8E686369A05D2E1A676A7668BB1AEEBFB613EEABC3681694205BDA205950215DADC46BF46372FF46E242D640472A728F574C15FF28DFD04772119A5C7B0ECA139E498DA12C576B04FF5997851C5FE7D96CB0F442DCF652FD48AF5EA31D9D1D216D19A4503BC2B877814DBD1D1ABDD7F5866A5C4E62CEA46057F5124E5FBCB5D76726814DE63BCDA4BFA60C7CB41D7CF820496E526497
		F5854EE7B60BC9BE913F03959DDC2762C6BA879B011E632EB1DC9354EDGDCAF7FF9CD2F51B252EB36AB34DE249342F2642B53EB1DCEAF4E2F75B2715B90065F8E9D4FE820592670E3BFEA33855A79DBACD66E84FC1E0D6CECAF19EB6707AFE86379C18949EF3705161F219B4FB61DFCEC9B8A126F64719670320D6C376922A66F3EDA1237BA04BC3A3F8F1437176C271347F93FB62871BED7BD5F9DDE60BA5BG3C9008F734AD50E77E4195BE86BC43E262D668932B0E7360E5CD17083C455CED876AD2509D284B
		4BC3E817C93B5FB9B9B549B461FA7C0145579220B7F753FCFF09FD1FBD69F48750FE07455DE120F3F5134E8FDC91D499359E3C83F10B53867A2C21229582F249D1877BBE7AE83941D8EEB0D51552DDCB2C076EDAA23A97FBE77335E1C11D4383A31A8E6D66A301BAD81D13229BFF1CD3E22A2AAAB7D499CB9579C28D490FDA30FC1259B02ABDC7081A7C574D8B35816F5B67547AFE63BD7CC6CF6D5792A260D6584E368EDC99683DF22E232DFDC099C736C063C86FD92C9DFDD735F13C6452F4F96854626652E2A2
		9D1A962D43FFCFEAF24FB45237153C2DB4D9EB7BC56962F4DAD1C6BA79972D130D05C74EE77AB271D93BE0BEE4D3A9D5FB7EF306EE1CA50B9B4BEBA524C3CC8F2D3D5B199A6FDF6D54F86F2AAD8C4D3B998E16AEE0EDD2D8AB3C3FDBCB3C8D3D44BB83430C90FEDC65D95AF3EF36A6FBEF5E61C0595A7A2B2D0665D7237A3EF737466341BD9A0F0FD7AF1C473A9C2D5F722E0574D35EFDFA74779E03B0EED76FC25EB927544C39EF03BEAB9654EF2F206F37867ABC3D20FEED26B977F4B8BF8B6A77F7E57AFEF150
		E7E75FC27A4D4E3927B3C09F73027A4517687B35C31F218575FB26D65F4F83FD165E219FBF836CE46F81A27AF81E63385D8C37F68E2E20A6182E30A6182EDC8826B38941F47FE78A269BB585532DAD8B267BEAD9B05D83170369A2AE875365148653651586533DDC9BCC77CA2D1E0E6F856DA7B0E0A85F8733EB9D0EAF706F5270F42DA3707F97613F067165E364EF0494D9483AB4F5CDF29B14174A225767953254D557412EEB365C282ECFFA655124C4A139C3C86557D79DC2B25EC1F588A96C0A2AC3C812A50F
		9FFFEEBB674FF7DD384DB6AFF37EE46120A24FC0EF5F8DA5A643A90349DCDAAF8B09A633AC3C41DE3D13DEBED9B8203CEBA714612CDF9C1D42DCAA8B59780E5D203CE4B75824F1D948A3B2FCCF6EB328EF4A01FFAAFFB38E8F7C35ACC86C6D02F16F1A93E53F7FC84D2FB63EFC4117A424F6888770C05AAFFA59B7582243EA176907422DC20687FFF8045BABB00A0F95A9BEF946A55A045C31F37863B99F1CB41A69D6880632570612AAC1F9D7E53B5DB7A163AD61E91B106C461FB48BFBF8334B6E9E2F64AF1BCE
		B34BA6B4B7B173318C817A66B0FD11CB054AA5671F320E97F6EEDD17A4A4368A19AE496EE8356338BB048CF942AB7AA6A417436FE9640C522769FDD892304BD7070F032D40F7142A52577C1F3DE7D74C2E56C4C841DE2112D162B922C225F034CC583DA0CA74F648781B9E49DB2436FD6830FC6239367DC309C202B2C8F40BAC3F8726E04CBE69F44D40837F6D9DF3189C9934755A5D3EB149BB891A9982B423A6D5C04583BFB4BE7825BFDDE682342E32F0041B923AD60E0B52E4E573581853AD2A8C402E407E10
		4A9EE9AA82E876786F5B5B797C7F0C009FD80524C923117DEBE27FD6613FA606B1B10C298A0C9B9B686FF37F825B7DB4E666BC6A7A43692FE8059914DF7A4D8F3F70730658EDB03AD6A1057DAC8FA6A1DB236A191A9C913D9F0BFC70500E95DB7E966748032E62C73E7B7D6A9A444010C9GF27684E75766FBDFFCA1720AB5B7D1C80425BEA172351DB20DF10579A0DEA05C8C4391313B027C94313B039D8E51FB55E947DA9EA3BD487EBD34E8A3E22FA32CEF21BA76B27F1C598D358F7F41FFBEFE761115239B41
		369D428EC9EBE023CD8DB952143F3E11D6071E653FC2CD6B6753B401EA8A88FFFD7ADB7CF3F29AE99A5A5289E1B4CD1E2BA98500D47EDA05CB44BE9F4C90BA7C73981B30398A70E50D3F294277679D8B26D1EB4AB7BD256F693FC70A7BE8DA585FA6EE3B247CBFAB1783FF5BEC9551008D009F3CCC6FF7D6B870370661FE6F55BFE9F739E43B6BDA335B013F7D324C482276BE27C3C47EBE22D19A15F7F26CB701D7F5FD7E9FD0CB87886B4E6071FC99GG30CAGGD0CB818294G94G88G88G56E017AE6B4E
		6071FC99GG30CAGG8CGGGGGGGGGGGGGGGGGE2F5E9ECE4E5F2A0E4E1F4E1D0CB8586GGGG81G81GBAGGG3699GGGG
	**end of data**/
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
	 * Return the JLabel1 property value.
	 * @return javax.swing.JLabel
	 */
	/* WARNING: THIS METHOD WILL BE REGENERATED. */
	private javax.swing.JLabel getCommandPriorityLabel() {
		if (ivjCommandPriorityLabel == null) {
			try {
				ivjCommandPriorityLabel = new javax.swing.JLabel();
				ivjCommandPriorityLabel.setName("CommandPriorityLabel");
				ivjCommandPriorityLabel.setFont(new java.awt.Font("dialog", 0, 12));
				ivjCommandPriorityLabel.setText("Command Priority (1 - 14):");
				// user code begin {1}
				// user code end
			} catch (java.lang.Throwable ivjExc) {
				// user code begin {2}
				// user code end
				handleException(ivjExc);
			}
		}
		return ivjCommandPriorityLabel;
	}
	/**
	 * Return the JTextField1 property value.
	 * @return javax.swing.JTextField
	 */
	/* WARNING: THIS METHOD WILL BE REGENERATED. */
	public javax.swing.JTextField getCommandPriorityTextField() {
		if (ivjCommandPriorityTextField == null) {
			try {
				ivjCommandPriorityTextField = new javax.swing.JTextField();
				ivjCommandPriorityTextField.setName("CommandPriorityTextField");
				ivjCommandPriorityTextField.setPreferredSize(new java.awt.Dimension(122, 20));
				ivjCommandPriorityTextField.setText("14");
				// user code begin {1}
				// user code end
			} catch (java.lang.Throwable ivjExc) {
				// user code begin {2}
				// user code end
				handleException(ivjExc);
			}
		}
		return ivjCommandPriorityTextField;
	}
	/**
	 * Return the JCheckBox1 property value.
	 * @return javax.swing.JCheckBox
	 */
	/* WARNING: THIS METHOD WILL BE REGENERATED. */
	public javax.swing.JCheckBox getConfirmCommandCheckBox() {
		if (ivjConfirmCommandCheckBox == null) {
			try {
				ivjConfirmCommandCheckBox = new javax.swing.JCheckBox();
				ivjConfirmCommandCheckBox.setName("ConfirmCommandCheckBox");
				ivjConfirmCommandCheckBox.setFont(new java.awt.Font("dialog", 0, 12));
				ivjConfirmCommandCheckBox.setText("Confirm Command Execution");
				ivjConfirmCommandCheckBox.setComponentOrientation(java.awt.ComponentOrientation.LEFT_TO_RIGHT);
				// user code begin {1}
				// user code end
			} catch (java.lang.Throwable ivjExc) {
				// user code begin {2}
				// user code end
				handleException(ivjExc);
			}
		}
		return ivjConfirmCommandCheckBox;
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
	 * Return the AutoEmailCheckBox property value.
	 * @return javax.swing.JCheckBox
	 */
	/* WARNING: THIS METHOD WILL BE REGENERATED. */
	public javax.swing.JCheckBox getQueueCommandCheckBox() {
		if (ivjQueueCommandCheckBox == null) {
			try {
				ivjQueueCommandCheckBox = new javax.swing.JCheckBox();
				ivjQueueCommandCheckBox.setName("QueueCommandCheckBox");
				ivjQueueCommandCheckBox.setFont(new java.awt.Font("dialog", 0, 12));
				ivjQueueCommandCheckBox.setText("Queue Commands");
				ivjQueueCommandCheckBox.setComponentOrientation(java.awt.ComponentOrientation.LEFT_TO_RIGHT);
				// user code begin {1}
				// user code end
			} catch (java.lang.Throwable ivjExc) {
				// user code begin {2}
				// user code end
				handleException(ivjExc);
			}
		}
		return ivjQueueCommandCheckBox;
	}
	/**
	 * Return the ShowMessageLogCheckBox property value.
	 * @return javax.swing.JCheckBox
	 */
	/* WARNING: THIS METHOD WILL BE REGENERATED. */
	public javax.swing.JCheckBox getShowMessageLogCheckBox() {
		if (ivjShowMessageLogCheckBox == null) {
			try {
				ivjShowMessageLogCheckBox = new javax.swing.JCheckBox();
				ivjShowMessageLogCheckBox.setName("ShowMessageLogCheckBox");
				ivjShowMessageLogCheckBox.setSelected(true);
				ivjShowMessageLogCheckBox.setFont(new java.awt.Font("dialog", 0, 12));
				ivjShowMessageLogCheckBox.setText("Show Message Log");
				// user code begin {1}
				// user code end
			} catch (java.lang.Throwable ivjExc) {
				// user code begin {2}
				// user code end
				handleException(ivjExc);
			}
		}
		return ivjShowMessageLogCheckBox;
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
			setSize(379, 283);
			setVisible(true);
		}
		catch (java.lang.Throwable ivjExc)
		{
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
	 * @see com.cannontech.common.gui.util.DataInputPanel#getValue(Object)
	 */
	public Object getValue(Object o)
	{
		ycDefaults = new YCDefaults(
			(new Integer((String)getCommandPriorityTextField().getText())).intValue(),
			getQueueCommandCheckBox().isSelected(),
			getShowMessageLogCheckBox().isSelected(),
			getConfirmCommandCheckBox().isSelected());

		ycDefaults.writeDefaultsFile();
		return ycDefaults;
	}

	/**
	 * @see com.cannontech.common.gui.util.DataInputPanel#setValue(Object)
	 */
	public void setValue(Object o)
	{
		if ( o == null || !(o instanceof YCDefaults))
			return;
		
		ycDefaults = (YCDefaults) o;
		
		getCommandPriorityTextField().setText(String.valueOf(ycDefaults.getCommandPriority()));
		getQueueCommandCheckBox().setSelected(ycDefaults.getQueueExecuteCommand());
		getShowMessageLogCheckBox().setSelected(ycDefaults.getShowMessageLog());
		getConfirmCommandCheckBox().setSelected(ycDefaults.getConfirmCommandExecute());
	}
	/**
	 * Show AdvancedOptionsPanel with a JDialog to control the closing time.
	 * @param parent javax.swing.JFrame
	 */
	public YCDefaults showAdvancedOptions(javax.swing.JFrame parent)
	{
		dialog = new javax.swing.JDialog(parent);
		dialog.setTitle("Yukon Commander Advanced Options");
		dialog.setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
		dialog.setModal(true);
		dialog.setContentPane(getAdvancedOptionsPanel());
		dialog.setModal(true);	
		dialog.getContentPane().add(this);
		dialog.setSize(379, 220);
		
		//get the location of the center of the screen
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        Rectangle defaultSize = new Rectangle();
        float percent = .25f;
        defaultSize.setSize( (int) (screenSize.width * percent), (int)( screenSize.height * percent) );
        //set the location of the frame to the center of the screen
        dialog.setLocation( (screenSize.width - defaultSize.getSize().width) / 2,
                       (screenSize.height - defaultSize.getSize().height) / 2);
		
        
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
			return (YCDefaults) getValue(null);
		else
			return null;
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
	 * Sets the buttonPushed.
	 * @param buttonPushed The buttonPushed to set
	 */
	private void setButtonPushed(int buttonPushed)
	{
		this.buttonPushed = buttonPushed;
	}

}
