package com.cannontech.dbeditor.editor.state;

import java.awt.Dimension;

import com.cannontech.database.db.*;
import com.cannontech.database.data.state.GroupState;

import com.cannontech.common.editor.PropertyPanel;
import com.cannontech.common.gui.util.DataInputPanel;

/**
 * This type was created in VisualAge.
 */
public class StateEditorPanel extends com.cannontech.common.editor.PropertyPanel implements com.cannontech.common.editor.IMultiPanelEditor
{
	private DataInputPanel[] inputPanels;
	private String[] inputPanelTabNames;

	private javax.swing.JTabbedPane ivjStateEditorTabbedPane = null;
public StateEditorPanel() {
	super();
	initialize();
}
/**
 * Insert the method's description here.
 * Creation date: (3/15/2002 1:17:24 PM)
 * @return Object[]
 * 
 *  This method should return an object array with 2 elements,
 *   Object[0] is a DataInputPanel
 *   Object[1] is a String (Tab Name)
 */
public Object[] createNewPanel(int panelIndex)
{
	Object[] objs = new Object[2];
	
	switch( panelIndex )
	{
		case 0: 
			objs[0] = new com.cannontech.dbeditor.editor.state.GroupStateEditorPanel();
			objs[1] = "General";
			break;
	}
		
	return objs;
}
/**
 * This method was created in VisualAge.
 * @return DataInputPanel[]
 */
public DataInputPanel[] getInputPanels() {
	//At least guarantee a non-null array if not a meaningful one
	if( this.inputPanels == null )
		this.inputPanels = new DataInputPanel[0];
		
	return this.inputPanels;
}
/**
 * This method was created in VisualAge.
 * @return java.awt.Dimension
 */
public java.awt.Dimension getPreferredSize() {
	return new java.awt.Dimension( 400, 450 );
}
/**
 * Return the RouteEditorTabbedPane property value.
 * @return javax.swing.JTabbedPane
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JTabbedPane getStateEditorTabbedPane() {
	if (ivjStateEditorTabbedPane == null) {
		try {
			ivjStateEditorTabbedPane = new javax.swing.JTabbedPane();
			ivjStateEditorTabbedPane.setName("StateEditorTabbedPane");
			ivjStateEditorTabbedPane.setPreferredSize(new java.awt.Dimension(400, 350));
			ivjStateEditorTabbedPane.setFont(new java.awt.Font("dialog", 0, 14));
			ivjStateEditorTabbedPane.setBounds(0, 0, 400, 350);
			ivjStateEditorTabbedPane.setMaximumSize(new java.awt.Dimension(400, 350));
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjStateEditorTabbedPane;
}
/**
 * This method was created in VisualAge.
 * @return java.lang.String[]
 */
public String[] getTabNames() {
	if( this.inputPanelTabNames == null )
		this.inputPanelTabNames = new String[0];
		
	return this.inputPanelTabNames;
}
/**
 * Called whenever the part throws an exception.
 * @param exception java.lang.Throwable
 */
private void handleException(java.lang.Throwable exception) {

	/* Uncomment the following lines to print uncaught exceptions to stdout */
	// com.cannontech.clientutils.CTILogger.info("--------- UNCAUGHT EXCEPTION ---------");
	// com.cannontech.clientutils.CTILogger.error( exception.getMessage(), exception );;
}
/**
 * Initialize the class.
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void initialize() {
	try {
		// user code begin {1}
		// user code end
		setName("RouteEditorPanel");
		setPreferredSize(new java.awt.Dimension(400, 350));
		setLayout(null);
		setSize(400, 350);
		setMaximumSize(new java.awt.Dimension(400, 350));
		add(getStateEditorTabbedPane(), getStateEditorTabbedPane().getName());
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
		StateEditorPanel aStateEditorPanel;
		aStateEditorPanel = new StateEditorPanel();
		frame.setContentPane(aStateEditorPanel);
		frame.setSize(aStateEditorPanel.getSize());
		frame.addWindowListener(new java.awt.event.WindowAdapter() {
			public void windowClosing(java.awt.event.WindowEvent e) {
				System.exit(0);
			};
		});
		frame.setVisible(true);
	} catch (Throwable exception) {
		System.err.println("Exception occurred in main() of com.cannontech.common.editor.PropertyPanel");
		com.cannontech.clientutils.CTILogger.error( exception.getMessage(), exception );;
	}
}
/**
 * This method was created in VisualAge.
 * @param val java.lang.Object
 */
public void setValue(Object val) {
	
	//Vector to hold the panels temporarily
	java.util.Vector panels = new java.util.Vector();
	java.util.Vector tabs = new java.util.Vector();
	
	DataInputPanel tempPanel;

	GroupState stateGroup = (com.cannontech.database.data.state.GroupState) val;

	Object[] panelTabs = createNewPanel(0);
	tempPanel = (DataInputPanel)panelTabs[0];
	panels.addElement( tempPanel );
	tabs.addElement( panelTabs[1] );

	this.inputPanels = new DataInputPanel[panels.size()];
	panels.copyInto( this.inputPanels );

	this.inputPanelTabNames = new String[tabs.size()];
	tabs.copyInto( this.inputPanelTabNames );
	
	//Allow super to do whatever it needs to
	super.setValue( val );
}
/**
 * This method was created in VisualAge.
 * @return java.lang.String
 */
public String toString() {
	return "State Editor";
}
/**
 * 
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private static void getBuilderData() {
/*V1.1
**start of data**
	D0CB838494G88G88G59F854ACGGGGGGGGGGGG8CGGGE2F5E9ECE4E5F2A0E4E1F4E135998DD09C55B9EA7C19CA1DE40AEDCCEB87FFCBBB34511A360C261D5452992D3135B3DAD3CBB536E91AE931E5BA3613EA5ABE0E1F109892A4241A98158422C048F1F0F14403A08708C2D019F092C6B3B5FB5C9E375C5E6EE6F70FBB92CA7AFD6F6D5BBB4EBBE22CE666636D7B3E775E777F3D6FDDC86D0BB965A50EDAC2CA4A09756F5592C2363FC84815FB3E6C6138DC312002A47D5B8F70B419D24AE0
	4C87DA61A585955524ABD08973B5B04FDAD4D0718CFC6FA25E404A3D701101CF9650163A3EFB7142FC16B5F2BEDB68FC6F58F698F7G5C87B89C7785117F5131E7AD7C63961EC309031075F44D7858BE8BF78A66F100FC00B541447A2D1473251E8EEBFEF2E98EF97CA7AFBB2A701C24B3B934CC323DA356D8C7D614EC1AC4DE97D1B9F14F95B0DF8D004A1712BEEFBB0CDDA361496E89B5621C9094C5D5CCF1A264948312296ACE4394CC51639945618D0A9A9194D1A6448C6139F70FD552F3608C189389F17B3A
	10D7BE4C6F8358884074B87AF217A567785995E47303CA694E8CFA74CBCCFED9CA68119DFDE24B9EEA7335CDE85BDC202D81D887D0B6D5D051G508F709C7575A30D4DB0FA7DE61F9A09280AD753FA65007E369A90E57876F800E2610E0B932A9EA0047973723116B472AC84EC5F55973C0F59A4574DFD1BF77CE6D2F85BB14716B4B6A94CF5B9CECEB1DB18D3891BB05EBF4D48BBFBEAFE6C4D67FD02A473AE3ACCADADCF43FB6583CD0EDC6B1CD5C93C0B265057CB282F8B613B440A8306DFCE712EF0824F6270
	G45633C87E8B706F9EC0C6F60328C17ADA755ED43E96D306CCE2F23D236E41093323038F874906653B060C3G0BG168164835C8F30D166315158547AB1E3E3C4504D5E28A487C41D303D0FB67D9BC6B1EE6A4220823921CC08E302AE89FED9B40835668FCDA83BFF5E3EBF53181DG9EEF0A0A288B2624AAF03A9091CDD1B7581ACDCDCF639ADD24D41F949101C1C4A37CDC523C8BF3C2B04C7734G24E3E28523D7B623AD2705B1B8918E00EFE6179D0751661511020ABAGD7045B6192FA5EB1D107FC76F806
	D5B59C55FC2889D994C1DF7D0E66F6BFFC1F84E0E7EDF3A22EG660576B937B757A41D53AFCD207802BE43F3630BDDD8B30BA35C57FF9A673E6E2F3A0934BCB0145657885B95666386A5B566F36819A8971BB7F60D27590FBCAFA4A7974EF1EFDA9E251F4279BBECDF45DB51D74320EF88608A055B782F4DF5ACE6FB85C364D17A2E8E81A6869296E7EB7FF8886B2AB8ADAAE63F2809CAGBE8CABC67EF2E8EBA2FE032A9DDB1BA8DE03F86F03GB3EC7CBF8FE1CC8A2629CB7E28A90E0A26A9A91389FAC58B72B2
	22FEAD11AA1CD7D58B5693B58671EE2F2FE92921F920A8A2955A5EE251EBE92C0A329881217B5428E2B27C5E43686F30B8E3654A8B07ABE82C4BD14CBBF6175DA2BC05BA68D284CC0129CB4AD40C634D76DD0C1083B645BBF9AD0CC58A0F49D8BD0F4906B35711559F9EF4242B054BEE6AF78C2BAC26CE290918E4B26DF42617FDB74D998BEFB0D9BB36D743F8DC0D4E3BD5D98C5C6A42F396417959GF9AA0F01254EBDC9F9E6B93FCF15E5E6CA5B46F32DF836A11E0E0AD01EB0210F59F63B3435A605B6E65910
	51BF5F0AB61413EA8113F76E03A632D01F60710C682AA66A660C559ED87DC170846A0EB6AED0F9FF602FC77DB6433CD6AB28E851388FFEDE457B036CF08ED9F7435E0CFDCE3F46EC9F5292BE283B047787CF36A14FDC2065EB5CF6152E64DA67B58CF5C222E556527BFB2EE49FB15CAA57B6980312EE180310C4F1EE23BBDDA8AF56E18BED55077089FE8F2F3164DA27A56A4F6370DDF9FAFEBDE2F765AF28BD761DCE3DC357D1FC4B6954BBB4D44F6F50AE20651C663539430DF1DE26C31D8768G988150G1668
	5C8EC3EDEEF21E3BEF907C2AAB02BCBAAF29799D54DE4A72501A17351773BC4418257746455467F94073AEG067B9635EFA14C0BED5C156D082BB5A0868C0E4BF3A3EE8466A103577642FA4C7DD9FB3E014EF36C39D21F2EAE7FBF003C66577E4BBFF59E4977863325571D39EE3F65CEDF375FF1272F5BBE77538C8FE7A4F7B11CFE523DF0DD8F38535575D2CA3B66846625260BC1D1078AB4AA1DC15FEFB650FF5F263EAF006F7BG587A9F3BAAE83E4CC03503F701096BBCED3CA67740FC9CA08BA047E43D862B
	B9374630968521C98251C6B5F102E4C7F16F756DFCAF01F9AD4091G9F009965BCFF64463D91A1AEC52291AAA113EF69919A0730EE233D36E3E6B70CC9AF9CA8FCFEB100058C73095EA14FB4A45FA13553CC4E9E98FDG72B43E95FD740D4A4E3C41D58723A2466A2604E941A944CCE7BF34EB8AB6E60DC7DC2E16BA77AB2EA6D677F2E2496F3B0EED2F263DFB90EE19E6F1D9E00D2BAE2F37EA023F0D3F6FB6836DC80C5BF07240C112D437A5FF44991D0EB8853FC98B374773CED41607AD57D8B57B9B34FE62D9
	F20C576C35747C9BE13E8A607E982F597117F84D4E3BF9B999DEF4ACED4DDE67EBF79445184C5571C44D4E05B79D63F9D7BB67B98C7445F1447D2D3DB6A5DF3E8FEFE7A70C0C5E68CE26E3BC639D15203FBF0F0E726EAFCD26E74D2317A76DE73A5DF3106B36791EDCA2FF67350C77519ACB27E2EB9C992BA78D1FFBF28B0B8B33AB9197EF54FF1C38E03EBBD479BC779DAE0EBB0D98A40247B3B4AFBE197D5EED6077EC638C77195409BEBB8573B84012996E331FFD109CEF554B77BBCAB26815BB4374D9B9137A
	568A9EC5FE9B81DFF6065FAD57F88E41B8A6CCD9378ACBA6FA2DG1277878CCB3B8E2B36065ABE591BFB7A936EDBF1417B58FDBC5C0E76ECB8436E63929B7F9E453B1270ACA79F36F3328BE8B9E778BD7D25EEFACF1F05FB9A2083E084C08348BA4BED799B0F0B1C671E76295AB0DC96F2D2F9E77A5D5B7E49FE0B7861969E6FABEF3F1E64DF76771227B244C54ED9968F2B2DF1232F1E6219FE4DBD28DFB150364F42FB9360AD0013G66AC572F39330569076160B5CDE1A2C4BBAF2E5C18A44692E8AC7ECCB7DF
	83576D8AB86BAB33737B29DBE0DEB01B2E477AB54D053533293E5BC0716BE7D3FD27F6F25F95834D77DF6E3B373DE8A926C3FE5741CC718E9A8CC14A3E6B1F025E5CA7CC5AFB42C7D1A67B5EE5E9E0517423D80FA4C5B2A5C1164E5076F49496AA13CC776378D31D95439FA4FD2FEF7B34FB130F664CBFBED39ECC7F1C6E4C7D4F573A5377BF5F6CCE5F7F5C563DF0FFF3C7F766F76B1D5D496FD6667777FB51B77990877783EC8258BE8779BB4763F9647583AC1E5344729B01C931CF253F96191C3F3BA7332E1E
	1E737F46545703EF933FEA1AEA844F6778419E741729EA94E97508BDE8A3DD1A8C19ECA96F91FB30064BE21022D97F9DEE603DFB579C6F49AFD6E8CF8E73D3B66E5AB06F5DCD9B778319776E4B66F8EF7E5B4EC41C96A5FDC7BBBF7B183D6AB36541EC686F4D6CC729777CFE54FB693B936C6F1B51C4060B75E23F2C860306E8474BECEF350D9BB4C6D4C3426A40FA5AEC66B330F97E9C6FE92F217F9F9204B7BADB33027AF0BD500B81D89F3DB24CFB5A3AF350B38278GCC00ECG366F775427ABE13E7A9C6FF3
	9F6BC3BFB2BEB1250E1C3F1FE17B5A86937B16068F246E9BGE19B8F842076291387DA6AE0EB5B11A65E5FBC94667DE151B95E9FD64A8B77079EB935BF447D893AACAF5C9FFED5CE6D8F79FE2B5F9FCA683361350FE38796170F04AF74FEE67FFFB5AE7783333C47C83921131A0F7271DE754B8773BE6C2FF59047A029880918700324BCA6854CD0DC83697F833044B8CD66D06E34B31DD44D73470F2FFBE5431A07470CD2D2B1C876E0B80D0ABA3D1D44C03FE0721F1A8748D6CD15008878C1D2E650215CB0E7E4
	F11854860F634FCD06A9863C3C9E9BA659BD0C1CDA2326A4373EA598A1937322F31814A97823B829E1E4D9D0A6DB59A55809BA10926FA914171A05003C3B283CF80A53BEE5579DBB8F6E9FDA1FDFCA9C83E41B2C8A0181E1822E66C132558C692291D265C0DCFB3D079ED499434842CE27940EFB4ED69F15F57AA0FBED214371EB332AB6D68DC01045F552CAD71A3040E92F887405849DD8097A2009BF722BFA03CD3B68857325FB5FFC66240314D8CA2269AB621AC9AA02704415E76003C5BFF54AB69324558545
	882AFA84A42BCB110C13EC864EAF5C6D3A7A6BCF3CCF99A4361A64C5E6CA585ABAA92A11D6EFB0A8A922FDG58950EFF5EBE9E57B8D3565444176E9EFA60DFFE0803FED29AF139685FB67A379D7F36D1CC9B45343503F10B49027FD269F12EB38D9E10E7A715875D6F9CG8F1A8FBDF677753F6AAAAE826D86C8B97D41891CD01DD875CEB46297754B722EFE7E296CF5D7220FB48C15B81E3BA3BEBF4ED93F5BF4AE799D346952E7B33E3B6B96331E72486254F71020E274E8004FDA4CFB01DB1543A4F3DFB89A91
	E459946430D789E00F58BBE30A093EA428627986DFE39199AD22225CD3C9FB7E87D0CB8788E93A95D0F78CGGB49FGGD0CB818294G94G88G88G59F854ACE93A95D0F78CGGB49FGG8CGGGGGGGGGGGGGGGGGE2F5E9ECE4E5F2A0E4E1F4E1D0CB8586GGGG81G81GBAGGG318CGGGG
**end of data**/
}
}
