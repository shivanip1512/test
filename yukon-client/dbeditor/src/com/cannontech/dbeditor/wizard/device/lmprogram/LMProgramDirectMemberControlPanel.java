package com.cannontech.dbeditor.wizard.device.lmprogram;

import com.cannontech.database.db.pao.PAOExclusion;
import com.cannontech.database.data.lite.LiteYukonPAObject;
import java.util.Vector;
import com.cannontech.database.data.device.DeviceTypesFuncs;

/**
 * This type was created in VisualAge.
 */

public class LMProgramDirectMemberControlPanel extends com.cannontech.common.gui.util.DataInputPanel implements com.cannontech.common.gui.util.AddRemovePanelListener {
	
	private com.cannontech.common.gui.util.AddRemovePanel ivjAddRemovePanel = null;
	IvjEventHandler ivjEventHandler = new IvjEventHandler();
	private javax.swing.JCheckBox ivjJCheckBoxActivateMaster = null;

class IvjEventHandler implements com.cannontech.common.gui.util.AddRemovePanelListener, java.awt.event.ActionListener {
		public void actionPerformed(java.awt.event.ActionEvent e) {
			if (e.getSource() == LMProgramDirectMemberControlPanel.this.getJCheckBoxActivateMaster()) 
				connEtoC3(e);
		};
		public void addButtonAction_actionPerformed(java.util.EventObject newEvent) {
			if (newEvent.getSource() == LMProgramDirectMemberControlPanel.this.getAddRemovePanel()) 
				connEtoC1(newEvent);
		};
		public void leftListListSelection_valueChanged(java.util.EventObject newEvent) {};
		public void removeButtonAction_actionPerformed(java.util.EventObject newEvent) {
			if (newEvent.getSource() == LMProgramDirectMemberControlPanel.this.getAddRemovePanel()) 
				connEtoC2(newEvent);
		};
		public void rightListListSelection_valueChanged(java.util.EventObject newEvent) {};
		public void rightListMouse_mouseClicked(java.util.EventObject newEvent) {};
		public void rightListMouse_mouseEntered(java.util.EventObject newEvent) {};
		public void rightListMouse_mouseExited(java.util.EventObject newEvent) {};
		public void rightListMouse_mousePressed(java.util.EventObject newEvent) {};
		public void rightListMouse_mouseReleased(java.util.EventObject newEvent) {};
		public void rightListMouseMotion_mouseDragged(java.util.EventObject newEvent) {};
	};
/**
 * Constructor
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
public LMProgramDirectMemberControlPanel() {
	super();
	initialize();
}
/**
 * Method to handle events for the AddRemovePanelListener interface.
 * @param newEvent java.util.EventObject
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
public void addButtonAction_actionPerformed(java.util.EventObject newEvent) {
	// user code begin {1}
	// user code end
	if (newEvent.getSource() == getAddRemovePanel()) 
		connEtoC1(newEvent);
	// user code begin {2}
	// user code end
}
/**
 * connEtoC1:  (AddRemovePanel.addRemovePanel.addButtonAction_actionPerformed(java.util.EventObject) --> LMProgramListPanel.fireInputUpdate()V)
 * @param arg1 java.util.EventObject
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC1(java.util.EventObject arg1) {
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
 * connEtoC2:  (AddRemovePanel.addRemovePanel.removeButtonAction_actionPerformed(java.util.EventObject) --> LMProgramListPanel.fireInputUpdate()V)
 * @param arg1 java.util.EventObject
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC2(java.util.EventObject arg1) {
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
 * connEtoC3:  (JCheckBoxActivateMaster.action.actionPerformed(java.awt.event.ActionEvent) --> LMProgramDirectMemberControlPanel.jCheckBoxActivateMaster_ActionPerformed(Ljava.awt.event.ActionEvent;)V)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC3(java.awt.event.ActionEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.jCheckBoxActivateMaster_ActionPerformed(arg1);
		// user code begin {2}
		fireInputUpdate();
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}
/**
 * Return the AddRemovePanel property value.
 * @return com.cannontech.common.gui.util.AddRemovePanel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private com.cannontech.common.gui.util.AddRemovePanel getAddRemovePanel() {
	if (ivjAddRemovePanel == null) {
		try {
			ivjAddRemovePanel = new com.cannontech.common.gui.util.AddRemovePanel();
			ivjAddRemovePanel.setName("AddRemovePanel");
			// user code begin {1}
			ivjAddRemovePanel.leftListLabelSetText("Available Subordinates");
			ivjAddRemovePanel.rightListLabelSetText("Assigned Subordinates");
			ivjAddRemovePanel.setAddRemoveEnabled(false);
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjAddRemovePanel;
}
/**
 * 
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private static void getBuilderData() {
/*V1.1
**start of data**
	D0CB838494G88G88G6DD37BB0GGGGGGGGGGGG8CGGGE2F5E9ECE4E5F2A0E4E1F4E13DDA8BF09B47F95658CA2A4EE8D2E7C6F114D46E30326BD19D45D5ED25D5DACDC629590E9BFB26E922541A1619119F992B93B8E6DA565154EC3A4491A4C800A4C800AF098F70E5C28A854916261198C591DF36E8C38EADB2921D28310F4091BC6900038F8711F418387F3FFBFBB80287D2D222E2661F3B7D7775BF3E7D775FBD10C0E7012B4C99A0244CC50C5F8F4B88899DA3C4F9E4CB9367957C773785
	E279F5815513EFF51543F38754957F40A178487A009B4A05D0EEBB6A901A615DCFFEBF90B98AAFF96669053A7BF616BC327ABC1240672924E50F5F8B4FDA20470167GBACDF07E9EFF0B4177C13956F8C7AAF39262AD43B657EADAAC7CA5235DFEF816G6D20ED4E795B0C7AA9A8E39B851E6B4AD85B4D702C2252FDEC7034516E6AA73711122F041DB5B817E5FCCE870D3EEDCE768C123F2006904866F72E40736C44757839281A88C745E4D2CD6AD2F4AE9C1B11E2322EEA61F979C3D10B05E3528DB9AA0515C4
	CAD3631A18989C0CDC1CE02F2332A6C5750BD2E2C652C6203B26AA93E2D2D248C6A76AF3CF0D1B4E857AC039C5C01E3B96ED78D5A89FGA28257699FEB4E9AB2EF3A3E0D6C3F3FAB2FCED785264BBAA12BD3685FDFAEBFD10E6347290F305F9E28BF8844E479B3BF17C5AF6732045D58F6BB148B010ACC7EFFD0FE89143DAE07506B62B27E381F4BB87D2B3FA487E75EF1566511712C0B4996F3E5E5ACB9DC62EC25B87AEEB9E221G6A8A017681158235D5B80433C007A9FE378B885F2199FDC4CDA45464D0AAB5
	2C44340BEACCD260FDF090EA8C5EA5A92AEAB1C298C6BFD5B3E0A38F27290A6578C4E23ADDA19CD35BAFBDCCF47D8D274BC6371D5FB8611CB346D82A486A3633826D36C4FD3B855E319F4A43789BA87F20054FFCD5CB79D876D5BA843D125BF93A164B92A95FCAFC2FCEBAABEDE4597CB7971C7BAA198C4515D9D9180D1F28C09B37399D42E9208920B9208DC05B5C5C46AF89A7573271B82C89ADA9AA1319191428694399D909C91AE16B04FFB5DB5B916B772BDB6E476C326D2455C7C50FDE3235CB51369FBA8B
	5D4C9EC56E2CDD585CBB734EDD6ADE090DDA5307DB691A70D5B19F8AA6FFBB6587ADFC665BD314CF4BD037890835172BA8962AF2B11221FCDFF5AEC6DEF6F30C0420EEA390732F6C46D8D98CE52F8756A850D9202BC08BC09BBD5C47C36547EFF29DCDD87C4B7A7EE7B9EEC452022E09634934AEA62352E5D1134599C5CA93230D60C259E61675F33B90FBD118638DA9A9E922AE2BC998DDCCC8G26B4EBD36F2A47B61AC4EB2764048493A4D2040F5B6A7243D391537A4FD2B1D11732ADD8FD178BB1F1C33C8CA3
	42G68EB8F75870D395B617DF18F37553DD5488BFAE1ADF839DD36389A60799600DBD28687C765A80AA1EA0B9C23F751359901768E200DB5FCEDFEE71AE3F0346673E4609B6F58E210461ADA06A725B697031F20E3EF827ECE203DC00C9F281D4E03CD1C5FDAC61956DAB3111CB6EC0EE3F967284CA93BEEE2DD8AB9E5E66FF28F5DBFC0AFDF1DC3B8D147ED7E14AB4830B8AC26A50E3E1FEAG9CA91675GEB3B3782779669061454C72514140C41CB5A707D3FD4D4E5F1B92B1A18F91A72B1CE0DGF052A67F79
	0AFA4607B12CA86775AF52FAD157B5F9A623CB13122E4B49F8367F7728AC69E5A1B0ED68DA61C5DDAF000E6F8325CCDD57D57AAD780A286A75CCEA8AD70E0153A768FE3C9E5AEF29637E5F6646FCEB0FD9FE04163375036EFC3E7BED88674B62E3720E4C41F14266D02BF8ECACB3F0E66C8B14DF827234799C42280F6BDDD6B5ED3495EE921737C2EC0EC3F48E17B1C76EDA78ED09E1A26D4E0F698F5D7618DE67364774475D2BE37693EEBB4C323ACF39D1CFF5BEE9D9831FF5E3BC0C2A4924C4972259453049F7
	26D08FB9A96BD764D8DC52A7658FA508E1BE4472E3B466AA703E4E476364FC8F0F133D9F7EA959735E00EDBE3579CF5EF26E73B17CB9FC59B849E47AC30A87C91192E02891B51354997F0FE9BCB778E90172B61C7CA4768D8D45E2172404FAC322097800382C980662F0C657E19F24CA3EA7524704244D2ADAC20A052E09B744F0C617157098FA6927B357409AC702470E1D8B2E19748F4CC245F8B215B1F62C50114BC7CE98F87213B33F5CF49372E9747D7FD544D719FC779E7DEBAA5F1BA3F3D2747A303A0053
	5F00B697E18B163481A6C658D69AF1DE8FD3D407194CD4301B95691A7D046F8D654C9449BF550FC00D570CF8FC2686B1632DF788C1205375BC9E37D7E3BD0F474626B322AA8A03BB54ED29C7ACFF39B6E041678470701CDBEB563F30C6FDEB1DDD7DF3269C559E9F2ECB69030C846994EE88EFC90BBCA772B46454DD9615CCB657EA75F8F01DD976924677FA6D57C90397770F6B6678CD5E8A02B9939B132D6B9DA20E19526484EC3D38B99A6775E77C56737AC12A9B583341A1C40098E63E797390BCCD970FC1BC
	09AF0EADC46744E4DC9A4924F5B5A1E991B92DD3CF877CB84643D57C0C9F03F116008A9A5858A58DEC7D33F673553C5D7BFE56EE41EF576E3C0737FB924A4F811587D83B5100F1EFC04B7FEC1E450F1B3C6FD25E0B906C6FAB67E70C0BBE6B5DC2ACBDEA9B37100A0C710B8339E72FE4AD0E6BEB848CB6F23F5F63356683C369349A15E96AEB7870A7B5561846F823B5557004B51B06F3D8CCDA60B8F83386E5455C58E09B3E7C5C8FFECCD6667AE374B60327DBA373F449CC29A3101EDFGA80D18A17F32AC4D93
	EE07DF3FEE3D0F58DDF7466949E307D30DCC7F29462C9DF641D91F5DBBCC54F13BEF02FAA667E786715CE9F7779225020763991906B8C84CC5DD24814BFA2F922177DDB856130D7C4EE2219957C0F993648EC02700587969A93A07E05B88705683B1FC9FEF3162FBF7934B43CA6169EB6678BED7FD811E3F2B904DF4AFBC5240FC1A9307395D377A4FF3BCBF3C151C7D68106D9D4B063ADEE7281959FB22B9EB7752E34FBAFD61E71DCC7702EA2E7B96E83357542B52B336D22F5B5B9A89BF274A9EBE173785569E
	900B076FF7D86361D28B337383ADDC167F29F98DE539655D05E33E655A6B707C5DECA266DD50EB569830E77F693CB1E0D78B337D13AD39B160128FB1FAG7825ADBC867CEDCD3DA586DC827BC12004A8EE660735820B57C6F93E564563B561E72BBF28C7FB7ADA9D42G50A850D52025D6BE4777EB573C5399DBD865CE67E7F537F2B733251567686F84F27B6DE84D1EBF8BDBD71ED7593D07032E5BFD2D6C5E43E5723FC7798E8B1F616CB74D9CE76410C3089D6273FF33AAF77E478FAD1F7339C3F66761EF2F68
	974EA93B8FAF6F47F0F7D13C699836BC65B3E2D83C05474560E19E43BE5F0E3ACDC3B9853461B03F3F78D6836EF185D05E8D34972898A85086B18EE82A0D7B7EE21DDD7CDF6E7BFC41BFFBA6E6F6F9307A766C39F3C53FDCFB6AEDF67DD44F6D4D776B957DBAF24A9B5B6C7CF76E72E16EBF0C898B6174BC1C1F86874D7C57705327BB5C46DC8F34F1FF74517D27884A8E53768F5561FDBDDF5B932A1C5489DF57D7FC18F7291A9C17131CF71572222AC68FE10C77697A7C6743BF2A37BF9FBED0EFFFBEFC28FE75
	BB0FC76A731D9F597D04C2776DD2502F255DA1B05E9C55FBG4A67CD5ED89372E2D0DEEA67EB715F1A11D7G655DA66F3FA88F73B84C5198EFFD335DDD83764B656D36612DC5EC0E7BEC67483D3762383855B95A69D8FF5FD4C7ED1DCECBE93A40B0EBB16A1E579B094D397AEDB1A191FEB68CD4D799ED46F4F564B5D3F625E535D83C5F46A6ACF6F6F550FB14F696BB4BCC7ED14AA79DD9BE0B290F3673EFC6DE28CBF5F0CC3F58109F0FAFB5586331348157C1DA4DE8D1A997F382BDD3303AEC64995725841A43
	B84B78313FAEEAF1E985E67D34BF2B4B573F4D1F8F574619E5192DDFB5798FFA2C7C13268F36F4208F76F6AC37758123DC5A315287CC0E2381346702D98E5372A26171C79A2AB5F599CB460C00414EA5F75335F7B7B43CD246657BA80071C29197558C34DB6A44F677864C6F2ED08E81CDG45BAD90E45667973F94C5BE761F886E618CCC9D1B29D443ED71BF85FC2E83F8F68A0D08B541D8E725CACD90B75B95B17A14F7FC671BBF31E5B82A3065C5D42E3C3C1102F79FE8C4F06EDCFD85E3F58FEA76E9D174702
	2F5D51B9184E0F527D229874F5F77198F9D788F981A8771A3C67C35CB6A73A78D965CB813E8E5781EFD79777612B6F637EEF6ED5183F8E8E3E214931E1B19E2108E0FBD5C5235B68DF5C45F30AD30DB8E6289BE2B650F4B73F1FDBF8051F6F36BF3815C456DD327D5E3CFF6A0CB3554DB03E29BB1B633A5B78796E2EA6BE67D7215E5D033C9DCD561C9F7172C5A74B83D87DB34B6AF13DE22C4C56871A2D75A86F10E02DFFFDD9BD5A284F684FF4FB220F6BD67A448E22FC61FC5EEF6993BDCCA75DF85EB3221445
	1EFB56391F4EF3DFF0F495FF733BE841206D50E78FD023D15E5B4362EC9761EB66AB9D3896F5D5D52664549444CE926AF5C0D5BB2AEF3479CC886FECF428E4F597699AE172FCBF3416BCD6D220BFD2A347943C3B4A58BBDB8F05ABF2761BDCC737594F53F82B7D583ED55104985E581B3B1F55D17E665E5C7DEC30112F2387204E5D477336931D987394A8EFGAAG5A85348F28240F6FF90F86704EF6559CFACACDC5E0D3D4ACE995536F771AEE6F3B7617ABB9DEF77E55C3E45747C95E3B9677AB8C27A17E9CD6
	4A704974FBAC087ACDC31D8E3499E887D0A1D051ABDC3F17296F70A4901375A1DD9723F3744609AB0717C0D9B6EE981D349F4CCD782F78A7F6BC06577E9EE26A7534176B5576397B4968D3445913C72FE2C31F737D5998B371524B65EC1CFF780575BC6E7BEC48D916E71C85237FFA4BB8ABE5A5463A7B27DEE2E2535B14FDEF343C1FEF3C737B523BF7F48E6677B31D79F339731D76395C6B1D76E70BC8676A5F1E26BA73FFFB7AC527755B93436D03FD984FF7014F76829D8472069CC2B044B17B2F1D5D04E256
	862FE331B86612786F841D4F7FE4B03F2EDF8F2E7D1F0622A05E3B4E283A2EA6F0FC4EFFA6087E525594E59A5F1603E8A3B85A4D692C2941FFA1C83F09C93314EDFC976F4778FD96741A86DA825A84348DE88F50DE2083C025C001A38EE1G68BC5094509C508250C62002A35CAECA8F5E3FDD962F2D3C69852611A0D063E8A2BB0FB2B92E6135BDBF877674659B832C7BA6249B6C26EFCA0C1BFD9E6E4BEF5FAF742DED5FAF75513BEE304B54E24A485B9F6B23E764595934E47A7171BE1F499BCFCF28E99973FD
	D657521FDF064E7E35E588755B7BB85C3F5247A77B6DFDFC265F6AE346BB570F780E036D274DF6A3A66FDD13F7010E093CABF2CC1FB379EFD33910FFC142894DB5C5651A273CE903772E05772E417BE5BF5A91D41ED34DFB05E8BF669433322230F21C361113E0D6539E8A15D1CE099639BFB0F9A8B77BDFE9FF706FA070EEFF0138529A69472BAFF9A6914E5CC8044599BD0C7F1C8CCFE9E2B22D4091986CBD89B1A2094841DF191338FECE7C333224445E8657E93C25B4CE6AA9771244CE07080B88E9DE469B4E
	A4E30A04A7BD1DB4500B5C630A180C9FE7D37CA8C2FC4B1A3FA5ADFEC0FEC34AA246041C4F1A532A059495AB155249A2BD488475B9B99D54552028A86ABCAB997F7AC46E0C847ACAC1B11820279CDE958E862BA7B3B32A961313208BE72703D1B189E3C8C968980478A94713D28C47310E9B56C9E0084E463E0D860D0F23E05DBA7332FA1C3AEA7F0D13034233778BCED2BECEAA98F447C9B962EF1C38A8BC4709G5025FAA1B4A06BA6687A72B46A4B13D5CC923005076F53C7BE326E2F87BF5619773E7B54G5B
	D7AF8C645EF797F5537F33G3FE0004727016EE32B44164984D8D997156BC34998461961C5DD4A6EA1CFF76378E95E4628E4F5873AD16EEB16BE7F87D0CB87885924A7C7FA90GGD0ADGGD0CB818294G94G88G88G6DD37BB05924A7C7FA90GGD0ADGG8CGGGGGGGGGGGGGGGGGE2F5E9ECE4E5F2A0E4E1F4E1D0CB8586GGGG81G81GBAGGG3490GGGG
**end of data**/
}
/**
 * Return the JCheckBoxActivateMaster property value.
 * @return javax.swing.JCheckBox
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JCheckBox getJCheckBoxActivateMaster() {
	if (ivjJCheckBoxActivateMaster == null) {
		try {
			ivjJCheckBoxActivateMaster = new javax.swing.JCheckBox();
			ivjJCheckBoxActivateMaster.setFont(new java.awt.Font("Arial", 1, 14));
			ivjJCheckBoxActivateMaster.setName("JCheckBoxActivateMaster");
			ivjJCheckBoxActivateMaster.setToolTipText("Check this to allow this program to become a master program.  \nSubordinate programs can then be assigned to this program.");
			ivjJCheckBoxActivateMaster.setText("Allow Member Control");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJCheckBoxActivateMaster;
}
/**
 * getValue method comment.
 */
public Object getValue(Object o) 
{
	com.cannontech.database.data.device.lm.LMProgramDirect program = (com.cannontech.database.data.device.lm.LMProgramDirect)o;
	if(getJCheckBoxActivateMaster().isSelected())
	{
		program.getPAOExclusionVector().removeAllElements();
	
		for( int i = 0; i < getAddRemovePanel().rightListGetModel().getSize(); i++ )
		{
			PAOExclusion subordinateProg = new PAOExclusion();

			subordinateProg.setPaoID( program.getPAObjectID() );	
			subordinateProg.setExcludedPaoID( new Integer(
					((LiteYukonPAObject)getAddRemovePanel().rightListGetModel().getElementAt(i)).getLiteID() ) );
		
			program.getPAOExclusionVector().addElement( subordinateProg );
		}
	}
	
	return o;
	
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
 * @exception java.lang.Exception The exception description.
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void initConnections() throws java.lang.Exception {
	// user code begin {1}
	// user code end
	getAddRemovePanel().addAddRemovePanelListener(ivjEventHandler);
	getJCheckBoxActivateMaster().addActionListener(ivjEventHandler);
}
/**
 * Initialize the class.
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void initialize() {
	try {
		// user code begin {1}
		// user code end
		setName("LMProgramDirectMemberControlPanel");
		setLayout(new java.awt.GridBagLayout());
		setSize(416, 348);

		java.awt.GridBagConstraints constraintsAddRemovePanel = new java.awt.GridBagConstraints();
		constraintsAddRemovePanel.gridx = 1; constraintsAddRemovePanel.gridy = 2;
		constraintsAddRemovePanel.fill = java.awt.GridBagConstraints.BOTH;
		constraintsAddRemovePanel.weightx = 1.0;
		constraintsAddRemovePanel.weighty = 1.0;
		constraintsAddRemovePanel.ipady = 16;
		constraintsAddRemovePanel.insets = new java.awt.Insets(4, 2, 7, 4);
		add(getAddRemovePanel(), constraintsAddRemovePanel);

		java.awt.GridBagConstraints constraintsJCheckBoxActivateMaster = new java.awt.GridBagConstraints();
		constraintsJCheckBoxActivateMaster.gridx = 1; constraintsJCheckBoxActivateMaster.gridy = 1;
		constraintsJCheckBoxActivateMaster.ipadx = 130;
		constraintsJCheckBoxActivateMaster.insets = new java.awt.Insets(11, 24, 3, 112);
		add(getJCheckBoxActivateMaster(), constraintsJCheckBoxActivateMaster);
		initConnections();
	} catch (java.lang.Throwable ivjExc) {
		handleException(ivjExc);
	}
	// user code begin {2}
	initializeAddPanel();
	// user code end
}
/**
 * Insert the method's description here.
 * Creation date: (6/21/2001 4:56:13 PM)
 */
private void initializeAddPanel()
{
	getAddRemovePanel().setMode( com.cannontech.common.gui.util.AddRemovePanel.TRANSFER_MODE );

	com.cannontech.database.cache.DefaultDatabaseCache cache = com.cannontech.database.cache.DefaultDatabaseCache.getInstance();
	synchronized( cache )
	{
		java.util.List availableSubs = cache.getAllLMPrograms();
		Vector lmSubordinates = new Vector();

		for( int i = 0; i < availableSubs.size(); i++ )
		{ 
			Integer theID = new Integer(((LiteYukonPAObject)availableSubs.get(i)).getLiteID());
			//makes sure it is a direct program and it is not already a master
			if(DeviceTypesFuncs.isLMProgramDirect(((LiteYukonPAObject)availableSubs.get(i)).getType()) 
					&& !(PAOExclusion.isMasterProgram(theID)))
				lmSubordinates.addElement( availableSubs.get(i) );
		}

		getAddRemovePanel().leftListSetListData(lmSubordinates);
	}
}
/**
 * Comment
 */
public void jCheckBoxActivateMaster_ActionPerformed(java.awt.event.ActionEvent actionEvent) {
	
	getAddRemovePanel().setAddRemoveEnabled(getJCheckBoxActivateMaster().isSelected());

	return;
}
/**
 * Method to handle events for the AddRemovePanelListener interface.
 * @param newEvent java.util.EventObject
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
public void leftListListSelection_valueChanged(java.util.EventObject newEvent) {
	// user code begin {1}
	// user code end
	// user code begin {2}
	// user code end
}
/**
 * main entrypoint - starts the part when it is run as an application
 * @param args java.lang.String[]
 */
public static void main(java.lang.String[] args) {
	try {
		javax.swing.JFrame frame = new javax.swing.JFrame();
		LMProgramDirectMemberControlPanel aLMProgramDirectMemberControlPanel;
		aLMProgramDirectMemberControlPanel = new LMProgramDirectMemberControlPanel();
		frame.setContentPane(aLMProgramDirectMemberControlPanel);
		frame.setSize(aLMProgramDirectMemberControlPanel.getSize());
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
 * Method to handle events for the AddRemovePanelListener interface.
 * @param newEvent java.util.EventObject
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
public void removeButtonAction_actionPerformed(java.util.EventObject newEvent) {
	// user code begin {1}
	// user code end
	if (newEvent.getSource() == getAddRemovePanel()) 
		connEtoC2(newEvent);
	// user code begin {2}
	// user code end
}
/**
 * Method to handle events for the AddRemovePanelListener interface.
 * @param newEvent java.util.EventObject
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
public void rightListListSelection_valueChanged(java.util.EventObject newEvent) {
	// user code begin {1}
	// user code end
	// user code begin {2}
	// user code end
}
/**
 * Method to handle events for the AddRemovePanelListener interface.
 * @param newEvent java.util.EventObject
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
public void rightListMouse_mouseClicked(java.util.EventObject newEvent) {
	// user code begin {1}
	// user code end
	// user code begin {2}
	// user code end
}
/**
 * Method to handle events for the AddRemovePanelListener interface.
 * @param newEvent java.util.EventObject
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
public void rightListMouse_mouseEntered(java.util.EventObject newEvent) {
	// user code begin {1}
	// user code end
	// user code begin {2}
	// user code end
}
/**
 * Method to handle events for the AddRemovePanelListener interface.
 * @param newEvent java.util.EventObject
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
public void rightListMouse_mouseExited(java.util.EventObject newEvent) {
	// user code begin {1}
	// user code end
	// user code begin {2}
	// user code end
}
/**
 * Method to handle events for the AddRemovePanelListener interface.
 * @param newEvent java.util.EventObject
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
public void rightListMouse_mousePressed(java.util.EventObject newEvent) {
	// user code begin {1}
	// user code end
	// user code begin {2}
	// user code end
}
/**
 * Method to handle events for the AddRemovePanelListener interface.
 * @param newEvent java.util.EventObject
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
public void rightListMouse_mouseReleased(java.util.EventObject newEvent) {
	// user code begin {1}
	// user code end
	// user code begin {2}
	// user code end
}
/**
 * Method to handle events for the AddRemovePanelListener interface.
 * @param newEvent java.util.EventObject
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
public void rightListMouseMotion_mouseDragged(java.util.EventObject newEvent) {
	// user code begin {1}
	// user code end
	// user code begin {2}
	// user code end
}
/**
 * setValue method comment.
 */
public void setValue(Object o) 
{
	com.cannontech.database.data.device.lm.LMProgramDirect program = (com.cannontech.database.data.device.lm.LMProgramDirect)o;
	
	if(program.getPAOExclusionVector().size() > 0)
	{
		getJCheckBoxActivateMaster().setSelected(true);
		getAddRemovePanel().setAddRemoveEnabled(true);
	}
	
	//init storage that will contain exclusion (member control) information
	java.util.Vector allSubordinates = new java.util.Vector( getAddRemovePanel().leftListGetModel().getSize() );
	
	for( int i = 0; i < getAddRemovePanel().leftListGetModel().getSize(); i++ )
	{
		//make sure this program itself isn't showing up as an available subordinate
		if(program.getPAObjectID().intValue() != ((LiteYukonPAObject)getAddRemovePanel().leftListGetModel().getElementAt(i)).getLiteID())
			allSubordinates.add( getAddRemovePanel().leftListGetModel().getElementAt(i) );
	}
	
	Vector assignedSubordinates = new Vector( getAddRemovePanel().leftListGetModel().getSize() );

	for( int j = 0; j < program.getPAOExclusionVector().size(); j++ )
	{
		PAOExclusion subordinateProg = (PAOExclusion)program.getPAOExclusionVector().elementAt(j);
	
		for( int x = 0; x < allSubordinates.size(); x++ )
		{
			if( ((LiteYukonPAObject)allSubordinates.get(x)).getLiteID() ==
				subordinateProg.getExcludedPaoID().intValue())
			{
				assignedSubordinates.add( allSubordinates.get(x) );
				allSubordinates.removeElementAt(x);				
				break;
			}
		
		}		
	}

	getAddRemovePanel().leftListSetListData( allSubordinates );
	getAddRemovePanel().rightListSetListData( assignedSubordinates );
}

}
