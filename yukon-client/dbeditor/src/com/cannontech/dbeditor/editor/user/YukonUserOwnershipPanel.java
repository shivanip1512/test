package com.cannontech.dbeditor.editor.user;

import com.cannontech.database.model.ModelFactory;
import java.util.Vector;
import com.cannontech.database.data.user.YukonUser;
import com.cannontech.common.util.NativeIntVector;
import com.cannontech.database.cache.functions.PAOFuncs;
import com.cannontech.database.data.lite.LiteTypes;
import com.cannontech.database.data.lite.LiteYukonPAObject;
import com.cannontech.database.model.LiteBaseTreeModel;
import com.cannontech.database.data.pao.PAOGroups;
import com.cannontech.database.model.DBTreeNode;
import com.cannontech.database.model.LiteBaseTreeModel;

/**
 * Insert the type's description here.
 * Creation date: (9/1/2004 1:27:00 PM)
 * @author: 
 */
public class YukonUserOwnershipPanel extends com.cannontech.common.gui.util.DataInputPanel {
	
	private static final Integer[] OWNERSHIP_MODELS =
	{
		new Integer(ModelFactory.LMCONTROLAREA),
		new Integer(ModelFactory.LMGROUPEXPRESSCOM),
		new Integer(ModelFactory.LMGROUPSA305),
		new Integer(ModelFactory.LMGROUPVERSACOM),
		new Integer(ModelFactory.LMPROGRAM),
	};
	
	private LiteBaseTreeModel[] assignedModels = null;
	private LiteBaseTreeModel[] availableModels = null;
	
	private com.cannontech.common.gui.util.AddRemoveJTreePanel ivjOwnershipTreePanel = null;
	
	protected transient com.cannontech.common.gui.util.AddRemoveJTablePanelListener fieldAddRemoveJTablePanelListenerEventMulticaster = null;
	IvjEventHandler ivjEventHandler = new IvjEventHandler();
	
	class IvjEventHandler implements java.awt.event.ActionListener, java.awt.event.MouseListener {
		public void actionPerformed(java.awt.event.ActionEvent e) {
			if (e.getSource() == getOwnershipTreePanel().getAssignedSortBy()) 
				fireModelChange();
			if (e.getSource() == getOwnershipTreePanel().getAvailableSortBy()) 
				fireModelChange();
			if (e.getSource() == getOwnershipTreePanel().getAddButton())
				treeItemAdd();
			if (e.getSource() == getOwnershipTreePanel().getRemoveButton())
				treeItemRemove();
		}
		
		public void mouseClicked(java.awt.event.MouseEvent e) {};
		public void mouseEntered(java.awt.event.MouseEvent e) {};
		public void mouseExited(java.awt.event.MouseEvent e) {};
		public void mousePressed(java.awt.event.MouseEvent e) {};
		public void mouseReleased(java.awt.event.MouseEvent e) {};
	}
/**
 * YukonUserOwnershipPanel constructor comment.
 */
public YukonUserOwnershipPanel() {
	super();
	initialize();
}
/**
 * 
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private static void getBuilderData() {
/*V1.1
**start of data**
	D0CB838494G88G88G18F0A1B1GGGGGGGGGGGG8CGGGE2F5E9ECE4E5F2A0E4E1F4E135D9FBD0D457994F245878071D31D326E3BB690C93CDCBD312B10D9DED07B43423D3A75A11CEB15106F44844BF5229F55296A3535A4CE1D9FC90D4D49EA2CFC590D3448516E5D9D1D89E8A86AB55C55756CE3C309738F0593B5E3DAB10986D771DF34F5D8B6E62E3C6E6FEF36E794E6BFB1F6FACA42FFCF1D606A50F100CAC227FB5E79032391410FB5F9B9A633424FFE6DB09696FA860G99AFC3E2AA0C
	2574E4DBF3C9E77EEE6827C17FAE748B617B9069493FDB829FD14E7140582736531B66BE2728001F330B766FB566C03B8F108E34125AEC6BE8AD1E7F6D4643BA3D9D7A7B74EFC40605105CD3B8A7A97F30099E2F4F5B21374F53B92FB6166863DE68631CG34B12742FB6E215C4DF3B9757E4DEF461134B77FE5590BE7197667582C2F5DDD475A8A7296D5C4A659D666063635FFFC645C10B2D1B7A4787D0ADF9307C66BFC0322CF529435CEEFC2C1D1F539EE0446957FGFCDE1E740BEAF0D48A748BFED106BD26
	6A10770F288DB2D18750E7FC7EE6EF2B4EC76CF89CC9DD620E4AE7024EDF2A094FDBE983198BCF635E3F23FA47F5955077825819476C7C4CE420F9ECC85B130FF3F3607BB8205546F9B9FB0273627DF285C99F3CED599F0517C5B646C322AD4CCB407AADCB917501978B510EBBE02C22BE5BEA87F881D300C500E26AFB378ABE0556BD28F5AA93930A5F9D88F448BE75126293E578F639E0C427DD91079455C788732F3F6757C66007E3D97DCC5F8A7B43F5427DA17E4AABC453C6ACD991E4DB3E215F32CA5FE3E3
	FDD8B6A6173B8865CA87FADEC336359650857081E29A38DCAF95B4BECC2EDE70A155AF48D7C30381C155BAC2126C93D5DD3E544639640B8436EE6BE32FE3FA09EBB46BA565B55532AB02DED2622EDB96B7B0FD2CEC886B051DFD429E6D6C0D8D8F5A0346F4837A61D7548F3740F7066E230C7E9C2537B706694C4777D3BA767BE1ECF9A377DB6FBEAEC31F758512DB9F0CA84322DF0EDA8E5B99BFB67BEC5B5694E3DE398974A940C2C09CE08DE0B3A05D4E6D3B2D6074A37AED3F4936EC6DAB0585500AD31AAA74
	7A031A609F922F892AA48C4AE21068F3D694A26F03B356A550F89A02B3AE0810D884CDD27C303BB0A102A385591C550587F00EAA52D10FB4A14281938142777DF561A1E8E5A1288D84FC02A606E730718D05284BBB42B55891B6006F1EA6547941824C9F07615B8EE03A9AA8C15A8268AFED62FA69BFFC905A4F4031C559656A1206108DC11D6667DFAC42DCA25E917DDA1798907DBE78886AE37FAE5A93564B30E270FC1D52B1C6BA017120C17FDF5181C607BD4CDA6663A29D97B4CD1586C31AF8D554B449BF92
	DEBFC1F9894E883FA0E1327515220F2D843912G2906FC9F959DB2495727A863210087AD276B699C4D33E9701D6360F761577271EE34B9381FEE75F2BF6D5A7BB2295DF0BB221FA2BC8E661F32E3F62CFD036E99BEA3BF5F7B8831EEF3C40E43C74BADF50FF4C666936C0FE0FAFF238C759E8BFCAE83A4B9385EDB0FD430F86B9002A20F304BAAD8CC74053D0C4DEDA80E6EE3AD4511FD2C2DB8320FF5974F6DC3170AA379909B7394635D22E091E04C3FD1FC1046305FAFD2260DA504559ADBBD288B49AFE957A5
	5F0828DD153E9021AEEAC65FBAC9E3708350498E4317A6AB39AF9D7F6207A471733E087759229747AD1EE6E67B516630AFB11E7ED302BA97E5F182945529047C1AFEB753785669FAFE39D29AF92E37947DF1DC1C56675DAC5DC9B00F48A15110ED1970896AD815A640D49830FA3DB7E0B757FB7301BF2C8D57C01B82E0EB536DD82BC62B2C186F2417A3EFE930E63793EA8EA7771D0D25C52618551D26D311E5E68243B62B4BF25116626D1088A999037AB3C31EF5E587E70DDD53E5E36349E5A81BEC4A87CEA74A
	56DE406BAE3CAF986DBA25ED02201AE765776F25DCF31D6A8B7AA25A91F157496C3750B97B6EBAF59477DD8674C4C3764B6566FC658E8615A109DE8D3ACD3E6342BAA5D2EDBBC4AF363A1110D49752A4CC660286B19388E9CC672C2EFC0366BB5CAB59496B4ABFB6533A32856ACAC0EB8B67656372877DA933A29B5AE1C98DC2BD64932738BE77D420DE701E52493CFEEBB06BA9B1559BD5CF1E965D5FDBE66BA9F88C7908817AA20337180A83A6BDC12D89577288D8117353F0B41379375EF79C4D627ECDF8FEDC
	75C07D935F924E3709AD8F66DED667FC6EC4FDA5374C2EFFA4CA5F5CB23B7E79F3B32FFF52E04C6B627731D409F2A5C0FF1D0B4B357668DE13DC7D0A64578C19260EE10CAA2AB4A279B96DABCA03CA1BA6ADF6273DC3ED1C82FBFE88E034158E24651D017A778C3F03C4A76600FE232F50FE4068EFEFFE147B63F10167056F28DC6753B8035FE9CCC77ECA34471451D7E9FFDA1FF7087C453E137A6E3492C2E5375237D6934FF13140EFBCA099704199D6A730D8FCF592F348B094F4F045DC8D08C3A4052EDD6F60
	EBFB20FF93F09770BCA081404E3CBB067949C80B9ED59475C7A74B310B1D669CFB7CACBBF7945A3BG66D775CD7C6D988B34F5E7B9DF753730A626B9E2CC38A35489131A4BF5C115FC9D42C89F1512650269265D7AFA6BB91E8BFEC077EC45BEC0BE476F2D29EA7E460C7F7E8B246F99B562FBB755F34392F30E59B76EDCB80EE3600D494E5C6660E7EE067112B624D5B6674D325D6B96C26A20E56383B346B16ECF927378F3B34611DF8C7E7078CFE70CE71956B35956D6F159F62CDDC6641F5C0A7A167735B119
	666BED7A4FAB493224E7ADCC6F8943CE720839596D73DD91A714BB62C54372CCBF9B1D7C7EC9EC63B6391607B6C9C7D95A332DB56DBCBE1B1B1E6C4D597D046B6EBD76BA1697279D74DD584E72E206C1FF16527BCCF46657AF9BFE6D053104F61EAF0FD7E1CCD438610E82F8GB2E081A04E4D736776CA9BF948BB4F23847A205814CDCF90A65F5F9CCF76967F45AE6EBF4BFF7692D979758A4BA7D17CA7414D7CA6C5EF973EDEC969CC3E9555A8DFCE8758985087708126G8BBB38FCED47EB19FC5DBEC9F3EB1A
	B0B4CAEFE7AE5CB5C91C8C13B1A9B1595EF54EA55B520EE8F75D1B5477D6F64C365DEFA9FDD547EC5B5D4C6336CB02B1FBA777557B8EE27851FC5377EBCDCFA37F4F745BF5CF758CE617D3D5515FB0762A48EF98D7D564B7CCFB555CEF181E2A68EF184BD566B78C732BFF1C407C3788EC319C30913085C03A38CF3DDBF50C79D484FF6A06C7CD27C2FF75507879095551E57DD57543FFABC92A469AF2D051B4E582776774372A51DE1A9220C4C6FB279AF584A54F2846266A74772AB1CF4A62B0A5339AA74D49EB
	1C12AE6EF7C9F9E1DF5BE47A9E4CFB7AFE7765D3BD0369A12BA63AADF2EA9EEE0B7C9ADA63038EBD538111510E5450BAF3F8B8A89AB6AF2F49B5E83D41FEA5A86123E4F9976AFD1F3BCB67C96EFAB00F707C1029639918938B48577BF1DD619C426E40B6B7FF236C0031F2FABEA1C8E3FF9F36C522593B717F9CAEFC3669F9793D9C1E17CBFE3404F43DDDE129A4117372D4B763FBCDF738E6193FFDA0D33F975C665CC94EE7DB734E4F7D3B8CBBFF7DFF4DEF1F5C6F76D8B2221CDF236FE7BFBF7B6D335C063A9D
	85FA8CA08E1088C886EC81108B408B2086508E7082E440B300D840D240CA403A8BBC566F1F465A671AB0265F0C5D749FA1B45A01289709B460D5013DBDD8EC9D68C0DD27DEE036E23482CACB57E90C5FA38D44704D55272B221D851965A2142617874760656691C60CB5EBEC517DF93D6D617E3C5196B9377C5E76E0EEF95F96B9377C41E64EAD0C3655067EB8827A719A737EEA50EE9834F43AA7522ECBBEED5420FFCC79C2FA0F0887F23A359E791A24B4AFFFCB1BE8B7F45A3EFA0CC990F9D4D1795AC37578DE
	9B16E419758B699C498FA1EA6823249EF9148202096F23866D86613F758855B9047D0EA589B2E51D51CB267606691CF5427E8F19DFFA855A78BF112C20CA767327C6DDAFE5A075CE23AB737DA51996E26DA5BB19C1FA0995355ACB322852FBC9A698E48AFF6C364E840B1F13774D7113BE2FAD6AEF87953DFA7C744E0E1FC17A9BC6G688BFAF98C7C386C549CFEF9F5C210E5CD10475DFE9F7AE84734A6066F5E11F25CBF486768037ABB339C799EB32D79BFD0CB8788889DE86EF98BGG8C9EGGD0CB818294
	G94G88G88G18F0A1B1889DE86EF98BGG8C9EGG8CGGGGGGGGGGGGGGGGGE2F5E9ECE4E5F2A0E4E1F4E1D0CB8586GGGG81G81GBAGGG338BGGGG
**end of data**/
}
/**
 * Return the OwnershipTreePanel property value.
 * @return com.cannontech.common.gui.util.AddRemoveJTreePanel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private com.cannontech.common.gui.util.AddRemoveJTreePanel getOwnershipTreePanel() {
	if (ivjOwnershipTreePanel == null) {
		try {
			ivjOwnershipTreePanel = new com.cannontech.common.gui.util.AddRemoveJTreePanel();
			ivjOwnershipTreePanel.setName("OwnershipTreePanel");
			// user code begin {1}
			ivjOwnershipTreePanel.setTreeModels(OWNERSHIP_MODELS);
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjOwnershipTreePanel;
}
/**
 * This method was created in VisualAge.
 * @return java.lang.Object
 * @param o java.lang.Object
 */
public Object getValue(Object o) 
{
	YukonUser login = (YukonUser)o;
	
	NativeIntVector ownedIDs = new NativeIntVector(10);
	
	LiteBaseTreeModel[] models = getOwnershipTreePanel().getTreeModels();
	
	for(int j = 0; j < models.length; j++)
	{
		for(int m = 0; m < models[j].getChildCount(models[j].getRoot()); m++)
		{
			LiteYukonPAObject liteObj = (LiteYukonPAObject)models[j].getChild(models[j].getRoot(), m);
			ownedIDs.add(liteObj.getLiteID());
		}
	}
	
	login.setUserOwnedPAOs(ownedIDs);
	return login;
}
/**
 * Called whenever the part throws an exception.
 * @param exception java.lang.Throwable
 */
private void handleException(java.lang.Throwable exception) {

	/* Uncomment the following lines to print uncaught exceptions to stdout */
	// System.out.println("--------- UNCAUGHT EXCEPTION ---------");
	// exception.printStackTrace(System.out);
}

private void initConnections() throws java.lang.Exception 
{
	// user code begin {1}
	getOwnershipTreePanel().getAssignedSortBy().addActionListener(ivjEventHandler);
	getOwnershipTreePanel().getAvailableSortBy().addActionListener(ivjEventHandler);
	getOwnershipTreePanel().getAddButton().addActionListener(ivjEventHandler);
	getOwnershipTreePanel().getRemoveButton().addActionListener(ivjEventHandler);
	// user code end
}
/**
 * Initialize the class.
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void initialize() {
	try {
		// user code begin {1}
		// user code end
		setName("YukonUserOwnershipPanel");
		setLayout(new java.awt.GridBagLayout());
		setSize(400, 350);

		java.awt.GridBagConstraints constraintsOwnershipTreePanel = new java.awt.GridBagConstraints();
		constraintsOwnershipTreePanel.gridx = 0; constraintsOwnershipTreePanel.gridy = 0;
		constraintsOwnershipTreePanel.fill = java.awt.GridBagConstraints.BOTH;
		constraintsOwnershipTreePanel.weightx = 1.0;
		constraintsOwnershipTreePanel.weighty = 1.0;
		constraintsOwnershipTreePanel.insets = new java.awt.Insets(4, 4, 4, 4);
		add(getOwnershipTreePanel(), constraintsOwnershipTreePanel);
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
		javax.swing.JFrame frame = new javax.swing.JFrame();
		YukonUserOwnershipPanel aYukonUserOwnershipPanel;
		aYukonUserOwnershipPanel = new YukonUserOwnershipPanel();
		frame.setContentPane(aYukonUserOwnershipPanel);
		frame.setSize(aYukonUserOwnershipPanel.getSize());
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
 * This method was created in VisualAge.
 * @param o java.lang.Object
 */
public void setValue(Object o) 
{
	YukonUser login = (YukonUser)o;
	
	//need the actual objects for the Trees
	runForTheTrees(login.getUserOwnedPAOs());
}

public void runForTheTrees(NativeIntVector ownedIDs)
{
	assignedModels = getOwnershipTreePanel().getAssignedTreeModels();
	availableModels = getOwnershipTreePanel().getAvailableTreeModels();

	for(int f = 0; f < assignedModels.length; f++)
	{
		for(int g = 0; g < assignedModels[f].getChildCount(assignedModels[f].getRoot()); g++)
		{
			DBTreeNode theNode = (DBTreeNode)assignedModels[f].getChild(assignedModels[g].getRoot(), g);
			LiteYukonPAObject flea = (LiteYukonPAObject)theNode.getUserObject();
			assignedModels[g].removeTreeObject(flea);
		}
	}

	for(int y = 0; y < ownedIDs.size(); y++)
	{
		LiteYukonPAObject temp = PAOFuncs.getLiteYukonPAO(ownedIDs.elementAt(y));
		switch(temp.getType())
		{
			case PAOGroups.LM_CONTROL_AREA:
				availableModels[0].removeTreeObject(temp);
				assignedModels[0].insertTreeObject(temp);
				break;
			case PAOGroups.LM_GROUP_EXPRESSCOMM:
				availableModels[1].removeTreeObject(temp);
				assignedModels[1].insertTreeObject(temp);
				break;
			case PAOGroups.LM_GROUP_SA305:
				availableModels[2].removeTreeObject(temp);
				assignedModels[2].insertTreeObject(temp);
				break;
			case PAOGroups.LM_GROUP_VERSACOM:
				availableModels[3].removeTreeObject(temp);
				assignedModels[3].insertTreeObject(temp);
				break;
			case PAOGroups.LM_DIRECT_PROGRAM:
				availableModels[4].removeTreeObject(temp);
				assignedModels[4].insertTreeObject(temp);
				break;
		}
	}
}

public void fireModelChange()
{
	getOwnershipTreePanel().setAvailableTreeModels(availableModels);
	getOwnershipTreePanel().setAssignedTreeModels(assignedModels);
}

public void treeItemAdd()
{
	
}

public void treeItemRemove()
{
	
}

}
