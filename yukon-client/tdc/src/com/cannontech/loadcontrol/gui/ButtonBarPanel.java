package com.cannontech.loadcontrol.gui;

import com.cannontech.clientutils.CTILogger;

/**
 * Insert the type's description here.
 * Creation date: (9/27/00 2:21:31 PM)
 * @author: 
 */
public class ButtonBarPanel extends javax.swing.JPanel implements java.awt.event.ActionListener {
	private javax.swing.BoxLayout ivjButtonBarPanelBoxLayout = null;
	protected transient ButtonBarPanelListener fieldButtonBarPanelListenerEventMulticaster = null;
	private javax.swing.JButton ivjJButtonDisableAll = null;
	private javax.swing.JButton ivjJButtonEnableAll = null;
	private javax.swing.JButton jButtonStartScenario = null;
	private javax.swing.JButton jButtonStopScenario = null;
	
	//private javax.swing.JButton ivjJButtonShowGrpsPrgs = null;
	
/**
 * ButtonBarPanel constructor comment.
 */
public ButtonBarPanel() {
	super();
	initialize();
}
/**
 * ButtonBarPanel constructor comment.
 * @param layout java.awt.LayoutManager
 */
public ButtonBarPanel(java.awt.LayoutManager layout) {
	super(layout);
}
/**
 * ButtonBarPanel constructor comment.
 * @param layout java.awt.LayoutManager
 * @param isDoubleBuffered boolean
 */
public ButtonBarPanel(java.awt.LayoutManager layout, boolean isDoubleBuffered) {
	super(layout, isDoubleBuffered);
}
/**
 * ButtonBarPanel constructor comment.
 * @param isDoubleBuffered boolean
 */
public ButtonBarPanel(boolean isDoubleBuffered) {
	super(isDoubleBuffered);
}
/**
 * Method to handle events for the ActionListener interface.
 * @param e java.awt.event.ActionEvent
 */
public void actionPerformed(java.awt.event.ActionEvent e) 
{
	if (e.getSource() == getJButtonEnableAll()) 
		connEtoC2(e);
	if (e.getSource() == getJButtonDisableAll()) 
		connEtoC3(e);

	if (e.getSource() == getJButtonStartScenario()) 
		fireJButtonStartScenarioAction_actionPerformed(new java.util.EventObject(this));
		
	if (e.getSource() == getJButtonStopScenario()) 
		fireJButtonStopScenarioAction_actionPerformed(new java.util.EventObject(this));
}

/**
 * 
 * @param newListener ButtonBarPanelListener
 */
public void addButtonBarPanelListener(ButtonBarPanelListener newListener) {
	fieldButtonBarPanelListenerEventMulticaster = ButtonBarPanelListenerEventMulticaster.add(fieldButtonBarPanelListenerEventMulticaster, newListener);
	return;
}
/**
 * connEtoC2:  (JButtonEnableAll.action.actionPerformed(java.awt.event.ActionEvent) --> ButtonBarPanel.fireJButtonEnableAllAction_actionPerformed(Ljava.util.EventObject;)V)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC2(java.awt.event.ActionEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.fireJButtonEnableAllAction_actionPerformed(new java.util.EventObject(this));
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}
/**
 * connEtoC3:  (JButtonDisableAll.action.actionPerformed(java.awt.event.ActionEvent) --> ButtonBarPanel.fireJButtonDisableAllAction_actionPerformed(Ljava.util.EventObject;)V)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC3(java.awt.event.ActionEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.fireJButtonDisableAllAction_actionPerformed(new java.util.EventObject(this));
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}
/**
 * Method to support listener events.
 * @param newEvent java.util.EventObject
 */
protected void fireJButtonDisableAllAction_actionPerformed(java.util.EventObject newEvent) {
	if (fieldButtonBarPanelListenerEventMulticaster == null) {
		return;
	};
	fieldButtonBarPanelListenerEventMulticaster.JButtonDisableAllAction_actionPerformed(newEvent);
}
/**
 * Method to support listener events.
 * @param newEvent java.util.EventObject
 */
protected void fireJButtonEnableAllAction_actionPerformed(java.util.EventObject newEvent) {
	if (fieldButtonBarPanelListenerEventMulticaster == null) {
		return;
	};
	fieldButtonBarPanelListenerEventMulticaster.JButtonEnableAllAction_actionPerformed(newEvent);
}
/**
 * Method to support listener events.
 * @param newEvent java.util.EventObject
 */
protected void fireJButtonStartScenarioAction_actionPerformed(java.util.EventObject newEvent) {
	if (fieldButtonBarPanelListenerEventMulticaster == null) {
		return;
	};
	fieldButtonBarPanelListenerEventMulticaster.JButtonStartScenarioAction_actionPerformed(newEvent);
}

/**
 * Method to support listener events.
 * @param newEvent java.util.EventObject
 */
protected void fireJButtonStopScenarioAction_actionPerformed(java.util.EventObject newEvent) {
	if (fieldButtonBarPanelListenerEventMulticaster == null) {
		return;
	};
	fieldButtonBarPanelListenerEventMulticaster.JButtonStopScenarioAction_actionPerformed(newEvent);
}

/**
 * Insert the method's description here.
 * Creation date: (9/27/00 2:24:47 PM)
 * @return javax.swing.JButton[]
 */
public javax.swing.JButton[] getAllJButtons() 
{
	javax.swing.JButton[] buttons =
	{
		getJButtonEnableAll(),
		getJButtonDisableAll(),
		getJButtonStartScenario(),
		getJButtonStopScenario()
	};
	
	
	return buttons;
}
/**
 * Return the ButtonBarPanelBoxLayout property value.
 * @return javax.swing.BoxLayout
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.BoxLayout getButtonBarPanelBoxLayout() {
	javax.swing.BoxLayout ivjButtonBarPanelBoxLayout = null;
	try {
		/* Create part */
		ivjButtonBarPanelBoxLayout = new javax.swing.BoxLayout(this, javax.swing.BoxLayout.X_AXIS);
	} catch (java.lang.Throwable ivjExc) {
		handleException(ivjExc);
	};
	return ivjButtonBarPanelBoxLayout;
}
/**
 * Return the JButtonDisableAll property value.
 * @return javax.swing.JButton
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
public javax.swing.JButton getJButtonDisableAll() {
	if (ivjJButtonDisableAll == null) {
		try {
			ivjJButtonDisableAll = new javax.swing.JButton();
			ivjJButtonDisableAll.setName("JButtonDisableAll");
			ivjJButtonDisableAll.setPreferredSize(new java.awt.Dimension(125, 23));
			ivjJButtonDisableAll.setMnemonic('e');
			ivjJButtonDisableAll.setText("Disable All");
			ivjJButtonDisableAll.setMaximumSize(new java.awt.Dimension(125, 23));
			ivjJButtonDisableAll.setMinimumSize(new java.awt.Dimension(125, 23));
			// user code begin {1}
			
			ivjJButtonDisableAll.setToolTipText("Disable all Control Areas");
			
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJButtonDisableAll;
}
/**
 * Return the JButtonEnableAll property value.
 * @return javax.swing.JButton
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
public javax.swing.JButton getJButtonEnableAll() {
	if (ivjJButtonEnableAll == null) {
		try {
			ivjJButtonEnableAll = new javax.swing.JButton();
			ivjJButtonEnableAll.setName("JButtonEnableAll");
			ivjJButtonEnableAll.setPreferredSize(new java.awt.Dimension(125, 23));
			ivjJButtonEnableAll.setMnemonic('e');
			ivjJButtonEnableAll.setText("Enable All");
			ivjJButtonEnableAll.setMaximumSize(new java.awt.Dimension(125, 23));
			ivjJButtonEnableAll.setMinimumSize(new java.awt.Dimension(125, 23));
			// user code begin {1}
			
			ivjJButtonEnableAll.setToolTipText("Enable all Control Areas");
			
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJButtonEnableAll;
}
/**
 * Return the JButtonEnableControlArea property value.
 * @return javax.swing.JButton
 */
public javax.swing.JButton getJButtonStartScenario() 
{
	if( jButtonStartScenario == null ) 
	{
		try 
		{
			jButtonStartScenario = new javax.swing.JButton();
			jButtonStartScenario.setName("JButtonStartScenario");
			jButtonStartScenario.setPreferredSize(new java.awt.Dimension(125, 23));
			jButtonStartScenario.setMnemonic('c');
			jButtonStartScenario.setText("Start Scenario...");
			jButtonStartScenario.setToolTipText("Start a pre-defined Control Scenario");
			jButtonStartScenario.setMaximumSize(new java.awt.Dimension(125, 23));
			jButtonStartScenario.setMinimumSize(new java.awt.Dimension(125, 23));
		}
		catch (java.lang.Throwable ivjExc)
		{
			handleException(ivjExc);
		}
	}
	return jButtonStartScenario;
}

/**
 * 
 * @return javax.swing.JButton
 */
public javax.swing.JButton getJButtonStopScenario() 
{
	if( jButtonStopScenario == null ) 
	{
		try 
		{
			jButtonStopScenario = new javax.swing.JButton();
			jButtonStopScenario.setName("JButtonStopScenario");
			jButtonStopScenario.setPreferredSize(new java.awt.Dimension(125, 23));
			jButtonStopScenario.setMnemonic('t');
			jButtonStopScenario.setText("Stop Scenario...");
			jButtonStopScenario.setToolTipText("Stop/Cancel a pre-defined Control Scenario");
			jButtonStopScenario.setMaximumSize(new java.awt.Dimension(125, 23));
			jButtonStopScenario.setMinimumSize(new java.awt.Dimension(125, 23));
		}
		catch (java.lang.Throwable ivjExc)
		{
			handleException(ivjExc);
		}
	}
	return jButtonStopScenario;
}

/**
 * Called whenever the part throws an exception.
 * @param exception java.lang.Throwable
 */
private void handleException(java.lang.Throwable exception) {

	/* Uncomment the following lines to print uncaught exceptions to stdout */
	CTILogger.info("--------- UNCAUGHT EXCEPTION ---------");
	CTILogger.error( exception.getMessage(), exception );;
}
/**
 * Initializes connections
 * @exception java.lang.Exception The exception description.
 */
private void initConnections() throws java.lang.Exception
{
	getJButtonEnableAll().addActionListener(this);
	getJButtonDisableAll().addActionListener(this);
	getJButtonStartScenario().addActionListener(this);
	getJButtonStopScenario().addActionListener(this);
}

/**
 * Initialize the class.
 */
private void initialize() 
{
	try 
	{
		setName("ButtonBarPanel");
		setLayout(getButtonBarPanelBoxLayout());
		setSize(575, 34);
		add(getJButtonEnableAll());
		add(getJButtonDisableAll());
		add(getJButtonStartScenario());
		add(getJButtonStopScenario());

		initConnections();
	}
	catch (java.lang.Throwable ivjExc)
	{
		handleException(ivjExc);
	}
}

/**
 * main entrypoint - starts the part when it is run as an application
 * @param args java.lang.String[]
 */
public static void main(java.lang.String[] args) {
	try {
		javax.swing.JFrame frame = new javax.swing.JFrame();
		ButtonBarPanel aButtonBarPanel;
		aButtonBarPanel = new ButtonBarPanel();
		frame.setContentPane(aButtonBarPanel);
		frame.setSize(aButtonBarPanel.getSize());
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
		com.cannontech.clientutils.CTILogger.error( exception.getMessage(), exception );;
	}
}
/**
 * 
 * @param newListener ButtonBarPanelListener
 */
public void removeButtonBarPanelListener(ButtonBarPanelListener newListener) {
	fieldButtonBarPanelListenerEventMulticaster = ButtonBarPanelListenerEventMulticaster.remove(fieldButtonBarPanelListenerEventMulticaster, newListener);
	return;
}
/**
 * 
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private static void getBuilderData() {
/*V1.1
**start of data**
	D0CB838494G88G88GA2F954ACGGGGGGGGGGGG8CGGGE2F5E9ECE4E5F2A0E4E1F4E135BA8DD09C57F9C8C6921211ACB938E19CCF02ED6248939491C7CAC9C2DACCE824D6F402DC2911DB2526D3CF2CF4540E5A5206B6CCE7B9814216AC01B8FEE449B10E20C296C66210AC0C648CFF2AFE0C630BF545CC2244DA6E96B8583BBD6D6DF1F79CD87D3E77766D6E1D7660045B1B79E677FD6F3D6F7FFD6FFB6F16333E11D1D9EE31F2DCF9A5277EAE16F35C5E339C37E54337DC8C17A464D7F1065F
	1BG355C06E085BC57C2DFDADAFED58D77976EEAE8E7C2DB79DCFED5A33C57F13B5C85EF43CB9CBE0FC15F2F5F9AFEFAFEBE9FDA991F8324FD6C76A1F89E86D88FB8FC1688483F73F6130AAFD4718C4AAD9C37E38C478C5DBE21623A21ED87D88B10B9C647674273E542F9D9CF37BA6E563A8C2E644FCED9DEC5BA861A8CCE0C53393D6A33195BD83EE79CF985081CB8A7846DECGD4FEBF377BEC97BCAF5B2746FF6914BC9DCE5E6B153C0A601C6890A55E6504F7D992BB46836E1E1E7E0022C85EFEDE3673DEC1
	64DC93C8773B372B899D0EEB0776BE02AB8EA02F75701E8730CB43FFE59A71675D79D5C3GAEB7536F3DD317D579C7A29B39CDBF746EBF92C73FD9B755AB532D6B1796780F834708AF9E2AC71B97C3DF45E4FED59340E500DBG33G47C98C7C0175ADF8760DAA831247A3F97BFC3EFE51A5FFA03984915EFBFA20C745FDA4B8A599220E7AB9F23B5DC41E79004EDBF95F7328CD56F7B11FE7FD7494377B9B6FDB8E1858E4777AF31634C9EA0B2CC95DA614F7FEDC5E051351B1994D7BE34E48BBF339343F520477
	261D27AD45AA1D92836F16A9743503785A8A6F65EAFCD07C6F89FE4200277179E392B3588EC15F6EA9969BA30719ACC3950FF2B51DC326F6C84F6F33740AD40691B1D61663C1241DACD21EAFEB323491FC0681CFE559C0E444F6AE74B5FB722BE8FC25B762FACD06F686C08EC09140BE00C3DE9867E5B1760275EC02B1E667E525BF6096DD024C513969758D7094C20A4CDF777A955E6B9406F9594D0F0A021FD347BCDE0FB6980D1A779409FDA77038AAF88519D75C129728739EC191E4BF9D33313E9647488269
	F538BD82B07078B8C6778F6B6B60A972FE65FFFCAEDE917491347F0F6B51A6537CB0D0848270CE6D72A7CDE82FDE50FF84E0C233C3CF8352FBDF10A1DD74748CC952D44067C0CD389CAF5A79F912BBE661BD4D4BEC7C6CB14415C15BAAB1BAF79A0E9A688C3A1DA8BEAF07599A7B4DB46664B609454CBF0E301899FC754BDC7B4E9B26B1C37AA59AAB3724583563A2B4D1AE5A5E9B98394FF5FCD815A971DCD1B93D989E7B939CC76D3B26062C8510AB83A0CF33F1D6C3B30D59FE5EAF30A87DD006G93DC3A4529
	1CEF86EBD43E156ABAB9286950C17AF681DD4E17DF5586E077B19BFEC3530F4DBDEC18FF3FFA1B5B3050144781154FE765C1ED75C4A36ECF423460D586851F60F5410BDF6DFB3A9179286BF7CC5266ECA4F89F2C7781D8E0FE8D3F399157A42FA832FBB42088B785C5F1FB47757E6F92DE7E4028CFCF950CD7C1A36AA785E13DEB637F3491578A6C5BDE01B8CD1B2276EFA3EBD590858F88BDA0853C8A45EFEF42F81F9242EA2E585904454FB4AF86B06F50DAA11BC7BF79E43787C281D3973755076BF81FD66BA0
	04G07B54FE3704C7631B599BC4956E44B4C63DC5EA72D9633BDA57D4B03161ABBF44D345C5157A415697B474CE5AFA4B9C3457B292C551DB8B726E4D1574016DA24B38874C7GFCF7589A38769A7633BC23867F00A40A54141A0D53EAB0467D421D00G6999935A7B1A5D3ED0F3B426EFD835A16DFF3286EDA89AF221CBC69B0E86D9AD188272E4G645D21F664205F8EC047ED8831F159505EAA6B6376407BE5BF9B7723B09BE7875C84C00A1F0E5B8A4FE6050DEB0B30F16781B784A0AAF4DC96BC39G2B937F
	65B47A185409137CB49F6A7087A1CEFBFAEE904B52DA70B7FC353A662C81D673BD6EC2DB8FC15B85B09BE071307DD5D67325CDE5F045CF3E963726CD8B50B848896871507CG2B79EA6B10E7A974D5CFB3BF7E3C56386F74797D1253CD36BC5587FFDEEB0C970AFB2E7695F80E39E53FF29D96F40879EBD7AD4A0BFB220AD6EB0487032CDEB981FC9DGDB27917FF658EA10BDF51A6EED1B60391F507B67208D57BB89288E7A308B7218A4FB84D79BDA36038FAA9DA437F474116EAB78FE1A2D41ED4D03D4CE6186
	8D6EABDE0C22BED12423BF0925B9C40886943758C1A8FDB8BA8961FC6974B075D9E708556FB5C1661BA76AFAD5798B3F76A8F779EE1D452C9ECFB9723A65DA107A449754FDB37BCC557E3D3F7F5981EA273F8CB1BB65421812A03353DF4F1A5929BD44622D3D6DCA341E03EE7FA29585BAD4D7B14CF46D85BED43E2A992D6E84DCF60849F7CBB013EFCF087951B373597CF87C6A10199F8768012B8FEA344579B3EB0669D82669786B885371FC9856F7186958EBB353B1B34CF44C09FCB69D33BCD723F53CB9A185
	2F4ABE3FDD9E775F379729FEBF0DB07DF2422C2E78040FBD676C8B6BF5D7454C3DB5982D7F6B8534CB53CC6C39609784FFFEA676DC709597BB975821EF538C2BD9B32C189B1BA279D5678106GDCG49G19911613EE9F45F3763CE7026BF06E163D3CF8B3EA33D773D0C39D6A8C7B335AFE2D8EEDCC76E70E55BE6B028B1DB573A2513620733E34603C97E366D17F9E39097A1B6C89545B5A3AF311F5077BDED9046518397FB6667F5CA3B64BC1139CC37625D94A5BB12B6718F6B873D3595F89216CE933095559
	FA9DFC2381773D75B02FGE0B7C0A94009397C2AEE00DBF34CEF19F56FAD64B792278EE9GF057E0DF9F082D3DB4BB6F8EAD4EBFFF3F603C593968F9546EC767185D536658DA3993C6DA05F3095B0B4EBBB433B859DFDBF0DE31296C1F93D0F62C89CAB5593FCEE85D7A74FEE57FB832B85905856779BEB513BDE8E33227FC4AE4AFA334765E076C740CBCCDEAAA6EAE1C0781A84E6BC4DCB7346DF7997D638256248A345381B6816C507A5AC89F5600D8D7E24D08F59E6D5B60CA644C09740C6D9DB16DF4352D5C
	B53363779362F1A0269DFBDEE3E73958732EFA6E3246BF13E5DB4D4FE45F341A1F49F22D35948FB40CB7B42C3F40BA7F192D50EAF6E6FB1D487E12D24F191C4FFE45FB84D537BAEEEB70E5F54C95C59A30E9F6F407D9BC1628F62ECE021C15C45F4F41F3B0A95A7EB457EDEF44FDFE847A9400D400CC0002A41663C66B975C1B2E04CC77A69A0F9F109A72C520D72251F4B4447747EF9B4C7D41B7601A71CB8159A9445AFC2221C96B53B36CF5C570206958BD914ED7F8F9DC3847E7A1B21F76451B3F24A11EDF29
	DF6A42C63FF4E9FEE91CE3FE29D8C26D5F844FF6753D971E0EA551FE21F6CBA17B6284740516301AEBE04648630246A3DD08DDC3A9B067B115EE7692733CD2E0025F85383D2672ECA535A337945E17B2F956C40C723C2349B3EB0B152799661CDFCA69DEDBEAAE4FAD933C8FF049CB4D64291BC5F95221EF3DA64FEA9E6FA5C4BEAC859428579E40F119BC3BFF4801F1C5G25G079E2075AC2DD53E9E44FDF0CC66BD60521BBE4149F149B8F719134D95E1FC92C086C09EC071830C67D0B30E09D72753B17F6428
	099D83C7955AF778EE2D5E271FE334387A0235CE6FB756482A9D12C81CDC83F9DD49EC8FF8EE8AF1A1E827EA3863A262B2201D2D611288AE975ABBB45C21FE446D01B6370C2D5767673953AA0EF3277563B8F7DAFB9B674F1FA5F16F3C28BDDCA13C0B02255A593B8C651DA1ED6BB2A6FF479C33C732062B31B1FB24EBB8070D59A3CB43ED36B1FB64E83892BF33C761B26673AF0523E5589C0E16615B24BEA906F609067B5698622A1767D7B5ADE7381F0FA32E855A17B55C8B650833C3DB54F01E885B5313B45C
	EA024B04F66EF2A6578BB351F27DABE997E9F33EC874AB03F64D8A06FB0960DA20FDDE438DF5A12E975AA39A6EC385F1A234D3B4DC2A0D4975180613AFA0AE8F5A3BD6B0395E1A0D162B1734772EE0E750C21E7D7F581482F5CB8ADB179FDD431838778C51AF050668A227B515F5141D7BD2D2585D52F7C611E69E34F7A1AE051DADC27F454E96D9CFBD4A8DA53DEFFA37D46C30D92AD7523C533ED2BFDB2C0733856579D5D2E7613C89684FD8C97E33F09A4F4CA88F5ED3F4E01C117E03AEE3BF467969287E7722
	7AD15E3B9546FEA92A3F52B01F6A362D1569D636EDBDA7EEFEB76E3D599ED527C32B6853BEFC12EBD975338354DF1F8F63BC1FAC0C8932AC38EE3AE7307E18DD05B2E413FCB3886F76D54C3FBFA17946639520C0F0BB69B80E6416ED6883ED1CCDC43FAB10AC0AD6617F67375F6034F57DF991F330078F39BD818F61C869ECA4F42C8F66D7B5BF48681C6CA7E35D5E183117C86E9A02F1A35A5835A1E46295024D261787ED4A045BFFDA355FFB02713F74F311F24B7EB876CBFE105AADCDFD662EB8294A70B149BF
	8500AF822038F7ED4C96E508A73AEFEF66B41B7E8D69BF148A39A1154DF91D2C37C1E85BD319AE5B05FB6D7CC512D73E073434F1C5E351F6FE79973A1DAF0C1B5B7983120BD201C63AC6675F4A634741918B51934696811425A2AEB952E4E2AB63FA00B3F3445CF739A41F8D2E86FDD7B37E87A309780E0E2539BB5AB6058477924A2C517CCFDB34ED5ACF6A36F1F6195BA6C272E1AA50C857680CA971EDF317782EG46962DE636F9523630EDD6DEB0374D63A42716812D6AB50C7F70ECA236217558F3775C01A5
	F25E2A650A97B96F58A81BF7FECDE2E7DAFA37F65609B97B5A9AFA37D62E613B895EE14053FD6359D136EF5C063E02B56C4E6DC88B5A3A6DA1B873G5C82D0G52G329FE2B54DA3CD6738854EB58E49B784C50FE8A8D328FE594E45FD37716C81161FB7FD7B89AEE7793F17473B57AAF8085A6DC5753947F152F0E6FB229575332E4D2FBA8760G908112G5257B27DFE5454CE753B62F2ABFD0A42BBA748BF9ACC39E137905451D874527560B9252F07ADD3D1B106973A03EE980EFF7037F55AAE34B75B3AD86E
	4C889AF3E777AB97CD7F43A37B6EDA2AD35EDABDF7D2FE0D24A6BEA51CD2E97E6037C61AB50F3EE9A90FC333D425D531CE5F2B29BE7CE9DD1F727EC4742173125F52677D51ECA27328EFBA4FE0FE69DE477C7035B7B0798E7313EA0421BBA419F88110F8AFF893CADD6FB827724DEA5779D65A9261CB475AED4CF6144E8F5E56697C441F385E55E775F9A76F037F66B1421FE77C4F1C53696CA81F0FCE67B9A37FDFF56A73C6A3094C23313275F7465A2779CC05E96DD37A3B3316A1B5C6FC06D859B1D9F600727F
	26CD67DF93CE04BF9D7BF7547EFE267F73DDBA1DC1E5BEBADDD176DFF2C11F37E4DE7BC74F5B730EBE6FFB01C46651FA7D9C291DB357515CCA735B85A7273D7F52703EE5F4B15F205C671E737F4A032E492616786759B7DA4C4F332DAD666759B3AD735F77F535447FC6232745780D864D9FB99731465F847EA882A88128FEB83F6A4443AC1FECED41FC02DF095D1B532F38467172813F8CD3987F2F364657751956053FA77BCEAB7E77BDAAA90A64C17A8C7FECAB7ACB11FC8429FE03510AB6125D63938A9D4A6E
	BCDB4959D198A3E82A674EEEF2F6843D9C1AEE4F1CBA43454F15F6D97289329226D8726504EA3322F7B90DD7692956F8B440C2B7E0BD508249410FEBF3AE1C41FBA33F1A07A94D17FA481DA0483696E093C0A1C0B1C029A66F431DED7348CB0F28449FB26F76AAF4EDDE9DA5E76110DB7D36A06FE2FC7FEC393830FF0AAE126F95402E0E30CF3D2B5CF991751146467C029687FFF531C643DD775BA53F9B6FB8553E6E4346982626D5677DEDB746A314240A1B975DB364D3893AE3517EFDBD71657FE94F4272F317
	4C632B6A523D71F57012F9FC55DEB246977D1E7B54F4ADA4342CFF60AA956E95FA67565CA70A8AF7D03DE4D3DB2C8BBFFC648E6285DB73D5D98A787CFE19BBFAEF7A6CDB737C8F33BE9934DAB84BF52EEAC2C0D92EF315C137CB198861571248FD4EFF077B142B78BFEDF993EE2D182F4531DA7474DDE3EDD53A2CA9EE3D36AB0D6E13FB53747D122E11CCF2375F768854590F30B5D192FCFB1E75F753430355F9F12A4F6B4235589FD684BDD7BE9DC27AFEB6C66DD47F2789215C1306B97F8BD0CB87884CA78967
	7E90GG94B0GGD0CB818294G94G88G88GA2F954AC4CA789677E90GG94B0GG8CGGGGGGGGGGGGGGGGGE2F5E9ECE4E5F2A0E4E1F4E1D0CB8586GGGG81G81GBAGGGB891GGGG
**end of data**/
}
}
