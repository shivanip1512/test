package com.cannontech.dbeditor.wizard.port;
import java.awt.Dimension;
import java.util.Collections;
import java.util.List;

import com.cannontech.common.pao.PaoType;
import com.cannontech.database.cache.DefaultDatabaseCache;
import com.cannontech.database.data.lite.LiteComparators;
import com.cannontech.database.data.lite.LiteYukonPAObject;
import com.cannontech.database.data.port.PooledPort;
import com.cannontech.database.db.pao.PAOowner;
import com.cannontech.yukon.IDatabaseCache;

/**
 * This type was created in VisualAge.
 */
public class PooledPortListPanel extends com.cannontech.common.gui.util.DataInputPanel implements com.cannontech.common.gui.util.AddRemovePanelListener {
	private int rightListItemIndex = getAddRemovePanelPorts().rightListGetSelectedIndex();
	private boolean rightListDragging = false;
	private com.cannontech.common.gui.util.AddRemovePanel ivjAddRemovePanelPorts = null;

/**
 * Constructor
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
public PooledPortListPanel() {
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
	if (newEvent.getSource() == getAddRemovePanelPorts()) 
		connEtoC1(newEvent);
	// user code begin {2}
	// user code end
}

/**
 * connEtoC1:  (AddRemovePanel1.addRemovePanel.addButtonAction_actionPerformed(java.util.EventObject) --> PortMacroCommunicationPortsPanel.fireInputUpdate()V)
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
 * connEtoC2:  (AddRemovePanel1.addRemovePanel.removeButtonAction_actionPerformed(java.util.EventObject) --> PortMacroCommunicationPortsPanel.fireInputUpdate()V)
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
 * connEtoC3:  (PortsAddRemovePanel.addRemovePanel.rightListMouse_mousePressed(java.util.EventObject) --> PortMacroCommunicationPortsPanel.portsAddRemovePanel_RightListMouse_mousePressed(Ljava.util.EventObject;)V)
 * @param arg1 java.util.EventObject
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC3(java.util.EventObject arg1) {
	try {
		// user code begin {1}
		// user code end
		this.portsAddRemovePanel_RightListMouse_mousePressed(arg1);
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}


/**
 * connEtoC4:  (PortsAddRemovePanel.addRemovePanel.rightListMouse_mouseReleased(java.util.EventObject) --> PortMacroCommunicationPortsPanel.portsAddRemovePanel_RightListMouse_mouseReleased(Ljava.util.EventObject;)V)
 * @param arg1 java.util.EventObject
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC4(java.util.EventObject arg1) {
	try {
		// user code begin {1}
		// user code end
		this.portsAddRemovePanel_RightListMouse_mouseReleased(arg1);
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}


/**
 * connEtoC5:  (PortsAddRemovePanel.addRemovePanel.rightListMouse_mouseExited(java.util.EventObject) --> PortMacroCommunicationPortsPanel.portsAddRemovePanel_RightListMouse_mouseExited(Ljava.util.EventObject;)V)
 * @param arg1 java.util.EventObject
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC5(java.util.EventObject arg1) {
	try {
		// user code begin {1}
		// user code end
		this.portsAddRemovePanel_RightListMouse_mouseExited(arg1);
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}


/**
 * Return the AddRemovePanelPorts property value.
 * @return com.cannontech.common.gui.util.AddRemovePanel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private com.cannontech.common.gui.util.AddRemovePanel getAddRemovePanelPorts() {
	if (ivjAddRemovePanelPorts == null) {
		try {
			ivjAddRemovePanelPorts = new com.cannontech.common.gui.util.AddRemovePanel();
			ivjAddRemovePanelPorts.setName("AddRemovePanelPorts");
			// user code begin {1}

			ivjAddRemovePanelPorts.setMode( ivjAddRemovePanelPorts.TRANSFER_MODE );
			ivjAddRemovePanelPorts.leftListRemoveAll();
			ivjAddRemovePanelPorts.rightListRemoveAll();
			
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjAddRemovePanelPorts;
}


/**
 * 
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private static void getBuilderData() {
/*V1.1
**start of data**
	D0CB838494G88G88G08DA22AEGGGGGGGGGGGG8CGGGE2F5E9ECE4E5F2A0E4E1F4E1359AEDF094D5922BB88BA92C63147322C755251023F2029E16F822463A00A8F81ED528D01553D8246E28BB6AAAF216D20A567E186C8690798A90C8882871C8A01130FC1EE2C08D79B80984D913A5ACA4404E6E3E6CCE32BB334C4CA69B08F5576FEBF632194D17122A4E4C6BFE6FF53FFE7D3A7B752CD074F17A1AFCFB11A0642F91585F67790250F1D090F6CC1C7BEB0EBBF15E6690CCFF1FGEC954A5B
	8B60F986E86BC87B2E9D9B613999408B3871G0B818A0587F614B0FC8634B733F78C79F6C1186442FD469DA8B1610BDC341F971EBA40616FF11FE7F66CE174A50043FD72G046FE95F92F82EA7527D2C66846B57710B8CE1551F3E30EF423CCC73F3083331D35933CC18C7946190DE2D7F921E27DC5D412F7CCA446997E5D911F5648FB983BE9410F4C5F576CAB7C4B5600CAA2ADED363D214B08A38603DD952F417A823B00C5F5802651EF160FDB2A7488DFC52886E23EA2CAB973C07G4635F059DF5CF40A49B6
	25BBC3483D3FBA256CB9ADD4E621B5A17B29B717954CEE45732FA4FB014755C2BB8EC0E579ECA717E5D5AB176541F25C375AEDF3B4GF438B9DE2F40F89D5A13G263B390CDF6E67B2FAEEBEA6CC187F6E7AADA9E44CF4D359163B93B24EDE3E2CE0973117599DF84F4BAEGDFG8FC0BF40D400CC005DC05BAEF437FF8A4F53BE3DDE09C49479F4B4DA978E286714G8A43FBCD8DD0982E857995B5A0885496E79E2836100743128B836D9083DD5BC5015B4E2C160704787577562F31D85B1C25A76DAB589C368B
	A67D37E13D5DC576F68F3C63F19B309D91FCBA410FB36169DE1593BCEE27832D60A25733E7B3172539E01AD0F8642AFD1D05ACE973DB6D210BD406F1979332D09DCF3C0AF5BC8B700BGF281D68314F8EC0E939E2E63C75B0F8D2763A630FDD596432DB1DFD4D4753A18948EA01569FA550E21F4ED85F41CFD67E847D13DE49CB36FD14EA3D7AC751213D1E7F7FB28BE421E04DEA86F13A9F9CF728C360D4D469EFEC24EC43A076E2143409FA07859A6BC5D5BC74859426DAC2015DF32B9E87F95247F04CB49B672
	B641E7DCCA36119B955CC696812D7A324DC17737520BFD62E4E84F82D8G1083E083A8E907FE6DFC0F050EC3A3BCC7AE537E52310FF76C04A70A6B2A58A4EB3AA87BD11B28CA22AF0CB40175195F01756A9BB0EEA1B95FFE60718D1211AA6A12A2436CE28401B1E9344F620E2D380F0A8855ADC590B008C485BEEFF647F63CFF2226DF0886C49DA5FAD0FAEE8736099E318DE60489603D3F9D6BEBBE71394D709EEA673A3AA0E2DC9634338D3D34DF59864F33E05CA8DCD353A0793198225A47EDB42D9A02231087
	7D47F7589CFB2E589CABG66DC65E7F425075BE2432687056A259D163608217F9A352BA95712EDF186611189789CG9B409EAF5809174755CF2BBD7064FC136745F225BAAFD86ED4B43C1644D92CA7BC56B39BCB6E3BE608B3699802360EBD297E1F76E19B6E00F54581523CFC8F6E39D2C6ED33CE5490374673AA98928AA4F604763D7BAA0EB5288749FA830AA2B9GAF9A33057B2E2ECF58E927E258D0BA41E33FD58F0624997899D737D2BC4CE136FACEFF0850C5DDD7A5DFCCC72DC857A5B918987F87A20BB6
	40A5EAEC2D4F7809BF00B5AE8248B6567A75356DA6FBEBD6146ED8540DCF92335B96921F7320FF014877FFFF854E33AA0D769152CE509FAF778C318729G4FB7F2DF7D5218F88CE59B03F92C2CA8E2E37219AD2EE336C769B6C2BF83F2C78152G32GD698FA98D761E1E39D26F16B47A0F7EA996FA5BC56B09E9BCCF27E94BC280D9C75263671AF3C56B67E35575A469B3DC35B70B92F158DD31A5B0B5729744A26B3F1490B7D25DF11E5C48E26B10469E4259B2FC312A57D229488A23DD53A01607E6043367D97
	62138B613D5C477DE76FDE6EBF4BEF7CCE483A76395DAA5FCA7BED1B3D5FC76DF532AF61BF29CC17C5FC56D098C5C0D175CACC56A95ECB7CBD43EB8E02BB2F5E0147D636A8208D6DF4A05002A2CA8FA289FB35B8206904E6DDCC57A1DA1225DE9349430554CEC50D20C0E51758A3BAE33A94F6B6623DBA6F6B821DD4159DBC78D51945F5203AD3D2D1139C0D31C8D6D955D6F51859914805A60DD8AE153C5FE2510ED0393E7E6E29114BA585C3E452F3CACCC357A2783FCBC51AB6FA01D48BF657DA06183F591241
	C9D848D1C347F95EBF7E28353400A90937F2B18603542BB94656F2F84313BFEAAD0DF1C93F05ABE15327DE47F196A78B910913F02697G6478F81C4C91B11D47C9168C54AB61B0F5BBC0F37B31CFF91564CE5CCF2C72513AC3AD50A83D632095FD2AC1FF4D12BE5B20DFF5DA51979874E51674ECBFDF47F33EC26CDF517598023499877A33280F673E3EEDC934B6B19CCB645459BE9CDF42269C01621F77DB7B3B977DB8AF68B666DF6ADFAB605C184EC97D73EC914F99D5258824D4B869E17597E927397E128772
	63DACDF5GF489C0ED2E60831C3BD8D5BA5EC7F80EA37BF8FD4683E322GD382F41E95816A33E93F770CFEEEC47B0511D53F277773FED92CDFB66BD75EE96EB73112776B6FE4FCBBE93F65837A35D471FE2702341FBBC87BCD8F323BFC9077FB4328579CB2F06B2AB16EADD0686EB57C9EFA2E50DCE792B46FDA2B580521B6C467F707126F670D1DF8DE9D7063C35CE60281F30EF8DA53943FC42EC7EC7FF7A2F3DC23385D6883F8029F57602E9EC0F1EEC31FA0ACAB3EBFB1B433031987F0FDAB79BE58C8AADBBC
	852F6FA507562D5443956E8C18E13D91765BA454AB707A563603DCDFE9A19E57FF782F390E153925413EA105EE96B31D3CE1524D19654B8A685CF79BF39F16F88D2B4849F1A18377CB8317EE60F69E6238EC4051F55FDB03EB9DD6F5BDBFD104B398130843024B20280BA4A8D29B27F33ECC72F3BCD7E1972F133D3E8B1F47B35076824481681D7D723E0D2C6FE440AD8220E7EDDA29792C895DB4572F05273B1B1F35F97B4E4073278A7CF46D8B2A36D19BC90A755C1E27ED2FE5B27D7921E942297FD5582D6AFA
	93369C32073B59DD36BB31E7F0DDDA1BF3FBAE2BBF6E5D4F571E8BFDCA42FCDD9377B78EDE575892AD0157C6728CDEAE60B3416055DAD9EB21435186C32A3B691B1A60791345BFC376C7AA396CD9A077EAC376522A3A1149BECCD2424FFB4DE7E7C7A37F701988137FE695177FF0446688C5387CE78E1C9E197CC3A708063FEA7A6A5B5148BFECDAE854FB0F1AFDF8D6EE3D657D83C3FA045A7A1CC8328F2F8AE11F3098704BA35C07CFC5DBCDBE3C85180AF290A2B8774B5FF6E2BDC12CE66D6FBAB1DF922B85FE
	57ACBEB81A7A686A882FF3F5CAB8A759A85B9C15G35G9E00384CE5DB5AB9EC3D37B1BEC43DB7EDD4F225497CFE3E4BB91AF1330CF1770DEADC16B1EE7721510CDBE20C4B9ED49BDEA1A7EAA5B6F9F05D0456EE45D2FC968A15645AF0B74117A949356137F6715A70E120CDD1B8FF6F3EE47E2B1501BC0B22835BF41CBCE81CB329FDA2E99CBDDB674491474401C70F4544D7CAF91CF5C5F9CCFCEFAF29EFC3BB9DE0EE145BE6E6974E4196C3BB8F20603A4DD18650G608598FF1D5BE8F950AABF99E823291213
	C49D0F6A25EA7F5874797920F1497A1CFE5DEA5C1BE364A7D4A60F7BA8291DE9496F71C163C646EF61307C16D8723B2DEAEC7CFEBEE8DCB23F3CA4FE54E6EC5D583E8AD41B23C86576F1DB90FF97657E5525C832AEF05F6A8C61FB0F8251C612B96E98417995159433E82D55C9F24BF218335618D716D2575EE212F56D6D26E4DDFB3BCD9A3A3EFC07142AB6C7657B3DA11F9B00628EBBB9AEE4609EB6F07D866E68A10E1BEC607EC5F25769501EEB601EA841389CE82FD2397F891662FBE409465B57C99B5F17E8
	7B5DDDE329850F8492BC37155EAA9E58CFFDC81E4F6D5CC27687B2850DB8931C71B35ABF749D02C51D73BBB10284DECF7A553E752CCF232E549FB56CFCD969D0F1670416AAFE4864DBE133C663C43E016FA5F80F89CF6307D04E3F71870036C863B67CECD7EA9BFE214B5A06DF6E4267C9D3E22A9FA55B69ABDDA586AD61E51BF494416A20FD7EC6466B229AC4036C7C1FE4BC25259A7FD6D72A33C0F56D5DEF56759183DFD5E9467F47403FB1GFF5C404F2DB2630F99B26607319E63C6FB8DE97789BCC622531B
	0DB5B44A8166F0E8ED20111C53276010BCADF05C33843781F0A78D5CB7740E8AB81CA4D35CA2021B8938EF72B92E091C5D5FG2E584E65DEDB0C7DD7D86C038C98F2AA9D777B30185FA32B215DG90869827D37B22F2BF5C0B6F5D1D2A9881F536C611DF50E3F86C0BA5FCECAE74DF85D0847863G6E984F61AFEF47F50801A19F57FDB4661F5EAE653EA3B3464F2DDEAE98676087537B5AB1FDBF9AA9D09E9F5FD29E744CB59673B3378256DC56436B8AFFF7E23BA1B99439F2083DFACDCD1D026B164DF453E82E
	F4E7717BEC3C270767CACF12B9A7C3BB83A02B0757D0627BF88DE5560CE9C27338AB163F854AF5B75833FBD87D2AA7F1B7D9E054D05EA961BCCF745A9C51DE0C2B58E52EDB627DBEEB27798D25DF9CC047E7935BD102FE47GBA163751E126BFB9000EF534090D27EBFB3E022F4D767CECA17C6859143FF31A5BCB5714431E69FAACFF7543398515F14C27F0EF5990FBD694B7FF63BEDBBC365F29DC9A63383BC6BD0E7A7BBBCB30BDC46349F1609E026F0FA74701F906CDCEGDADE1C675A0FC57099F77501DFG
	988FB095A093A03B0F470A09E17C8DEA48BC5B2DC41BA11804CDA98C7B9EDBB2365F6F4CDB4777FE4E938F88F3EF5F1032B61857C777EF638D7ABC21457271132E6FDB852F6F9450BCG7DGD3GB2G32EE70753D942E266B43254D533AAE7AC324EA4A97078B1989B4F61054779D5C0B7DEDBD423FFB62F277A2AE771E19778BFBBEDEE75F1AC26EA5CCDE61E662BCA66C62EEACB6FB1FEAFADFD8FC6BFDE46EAD65C1F51E95C91DFFAC0CD8679FAFC42CF36817A3C3FF3FFEB5127A7B75DFA3666F5754E616C6
	31DFBA8E7B62825081A6GCC3D496DA5AA7F1B5A0B052DB48602B87F413F0052B97FD6A575DAAFAB437FEE4A2F603A3DCF51F5A5026767F8C9417B25ABD102E4F793856B089451E8D70657956C5F4328132069BA6B54B2F84E05F5AD81D88DD058EFF314831C82F081047A791A8B221570EC933B86FF85GA48DE2E45D2AA871EFCD2FC5D32FFBF9F478F53F9EA55FB0C0DEF7DF146500EFC6493D2D33D3C306FE5F09969A38A64D25E8924E9D59EF975454B2385561E53822DA6B5E2F8E56FDC835563D2C1AF5CF
	F19A1BD7B5AE8C945F276298C7F338F322AC8609DD7E9FD0CB8788E55FC8E9458EGG04ADGGD0CB818294G94G88G88G08DA22AEE55FC8E9458EGG04ADGG8CGGGGGGGGGGGGGGGGGE2F5E9ECE4E5F2A0E4E1F4E1D0CB8586GGGG81G81GBAGGG7F8EGGGG
**end of data**/
}

/**
 * This method was created in VisualAge.
 * @return java.awt.Dimension
 */
public Dimension getMinimumSize() {
	return getPreferredSize();
}


/**
 * This method was created in VisualAge.
 * @return java.awt.Dimension
 */
public Dimension getPreferredSize() {
	return new Dimension(350, 200);
}


/**
 * This method was created in VisualAge.
 * @return java.lang.Object
 * @param val java.lang.Object
 */
public Object getValue(Object val) 
{
	//be sure we have not left over children
	((PooledPort)val).getPortVector().removeAllElements();
	
	Integer portID = ((PooledPort)val).getPAObjectID();
	
	java.util.Vector portVector = new java.util.Vector();

	for( int i = 0; i < getAddRemovePanelPorts().rightListGetModel().getSize(); i++ )
	{
		PAOowner childPort = new PAOowner();
		childPort.setOwnerID( portID );
		childPort.setChildID( new Integer(((LiteYukonPAObject)
									getAddRemovePanelPorts().rightListGetModel().getElementAt(i)).getYukonID()) );
		
		//apparently order of ports is NOT important??!!
		//childPort.setPortOrder( new Integer(i+1) );

		portVector.addElement( childPort );	
	}

	((PooledPort) val).setPortVector( portVector );

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
 * Initializes connections
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void initConnections() throws java.lang.Exception {
	// user code begin {1}
	// user code end
	getAddRemovePanelPorts().addAddRemovePanelListener(this);
}

/**
 * Initialize the class.
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void initialize() {
	try {
		// user code begin {1}
		// user code end
		setName("PooledPortListPanel");
		setLayout(new java.awt.BorderLayout());
		setSize(418, 293);
		add(getAddRemovePanelPorts(), "Center");
		initConnections();
	} catch (java.lang.Throwable ivjExc) {
		handleException(ivjExc);
	}
	// user code begin {2}

	IDatabaseCache cache = com.cannontech.database.cache.DefaultDatabaseCache.getInstance();
	java.util.Vector availablePorts = null;
	synchronized(cache)
	{
		List<LiteYukonPAObject> allPorts = cache.getAllPorts();
		
		availablePorts = new java.util.Vector( allPorts.size() );

		//do not add other POOLED ports or non-dialup ports
		for (LiteYukonPAObject litePort : allPorts) {
			if (litePort.getPaoType() != PaoType.DIALOUT_POOL && 
				litePort.getPaoType() != PaoType.LOCAL_SHARED &&  
				litePort.getPaoType() != PaoType.TSERVER_SHARED) {
				availablePorts.add(litePort);
			}
		}
	}


	getAddRemovePanelPorts().leftListSetListData( availablePorts );
	// user code end
}

/**
 * This method was created in VisualAge.
 * @return boolean
 */
public boolean isInputValid() 
{
	if( getAddRemovePanelPorts().rightListGetModel().getSize() < 1 )
	{
		setErrorString("One or more ports should be selected for this Pooled Port");
		return false;
	}
	else
		return true;
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
		PooledPortListPanel aPooledPortListPanel;
		aPooledPortListPanel = new PooledPortListPanel();
		frame.setContentPane(aPooledPortListPanel);
		frame.setSize(aPooledPortListPanel.getSize());
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
		exception.printStackTrace(System.out);
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
	if (newEvent.getSource() == getAddRemovePanelPorts()) 
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
	if (newEvent.getSource() == getAddRemovePanelPorts()) 
		connEtoC5(newEvent);
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
	if (newEvent.getSource() == getAddRemovePanelPorts()) 
		connEtoC3(newEvent);
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
	if (newEvent.getSource() == getAddRemovePanelPorts()) 
		connEtoC4(newEvent);
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
 * Comment
 */
public void portsAddRemovePanel_RightListMouse_mouseExited(java.util.EventObject newEvent) {
	rightListItemIndex = -1;
	rightListDragging = false;

	return;
}


/**
 * Comment
 */
public void portsAddRemovePanel_RightListMouse_mousePressed(java.util.EventObject newEvent) 
{
	rightListItemIndex = getAddRemovePanelPorts().rightListGetSelectedIndex();
	rightListDragging = true;

	return;
}


/**
 * Comment
 */
public void portsAddRemovePanel_RightListMouse_mouseReleased(java.util.EventObject newEvent) 
{
	int indexSelected = getAddRemovePanelPorts().rightListGetSelectedIndex();

	if ( rightListDragging &&  indexSelected != -1 && indexSelected != rightListItemIndex )
	{

		Object itemSelected = new Object();
		java.util.Vector destItems = new java.util.Vector( getAddRemovePanelPorts().rightListGetModel().getSize() + 1 );

		for( int i = 0; i < getAddRemovePanelPorts().rightListGetModel().getSize(); i++ )
			destItems.addElement( getAddRemovePanelPorts().rightListGetModel().getElementAt(i) );

		itemSelected = destItems.elementAt( rightListItemIndex );
		destItems.removeElementAt( rightListItemIndex );
		destItems.insertElementAt( itemSelected, indexSelected );
		getAddRemovePanelPorts().rightListSetListData(destItems);

		getAddRemovePanelPorts().revalidate();
		getAddRemovePanelPorts().repaint();

		// reset the values
		rightListItemIndex = -1;
		fireInputUpdate();
	}

	rightListDragging = false;

	return;
}


/**
 * This method was created in VisualAge.
 * @param val java.lang.Object
 */
public void setValue(Object val) 
{
	IDatabaseCache cache = DefaultDatabaseCache.getInstance();
	java.util.Vector availablePorts = null;
	java.util.Vector assignedPorts = null;
	synchronized(cache)
	{
		java.util.Vector macroPortsVector = ((PooledPort)val).getPortVector();
		List<LiteYukonPAObject> allPorts = cache.getAllPorts();

		assignedPorts = new java.util.Vector();
		int singlePortID;
		for(int i=0;i<macroPortsVector.size();i++)
		{
			singlePortID = ((PAOowner)macroPortsVector.get(i)).getChildID().intValue();
			for(int j=0;j<allPorts.size();j++)
			{
				if( ((LiteYukonPAObject)allPorts.get(j)).getYukonID() == singlePortID )
				{
					assignedPorts.addElement( allPorts.get(j) );
					break;
				}
			}
		}

		availablePorts = new java.util.Vector();
		for (LiteYukonPAObject litePort : allPorts) {
			if (litePort.getPaoType() != PaoType.DIALOUT_POOL
				 && !assignedPorts.contains(litePort) && 
				 litePort.getPaoType() != PaoType.LOCAL_SHARED &&  
				 litePort.getPaoType() != PaoType.TSERVER_SHARED)
			{
				availablePorts.addElement(litePort);
			}
		}		
	}


	getAddRemovePanelPorts().leftListRemoveAll();
	getAddRemovePanelPorts().rightListRemoveAll();

	getAddRemovePanelPorts().leftListSetListData( availablePorts );
	getAddRemovePanelPorts().rightListSetListData( assignedPorts );

//	getNameTextField().setText( ((PooledPort)val).getPortName() );
}
}