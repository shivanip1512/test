package com.cannontech.esub.editor.element;

import com.cannontech.common.editor.PropertyPanelEvent;
import com.cannontech.common.gui.util.DataInputPanel;
import com.cannontech.common.gui.util.DataInputPanelListener;

/**
 * Creation date: (12/18/2001 12:54:28 PM)
 * @author: 
 */
public class StateImageEditor extends com.cannontech.common.editor.PropertyPanel implements DataInputPanelListener {
	private String[] tabs = { "General" };

	private StateImageEditorPanel stateImageEditorPanel = new StateImageEditorPanel();
	
	private DataInputPanel[] inputPanels = {
		stateImageEditorPanel
	};

    /**
 * DynamicTextEditor constructor comment.
 */
public StateImageEditor() {
	super();
	initialize();
}

/**
 * Creation date: (12/18/2001 4:07:28 PM)
 */
private void checkValidity() 
{
	getPropertyButtonPanel().getOkJButton().setEnabled(isInputValid());
}

/**
 * 
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private static void getBuilderData() {
/*V1.1
**start of data**
	D0CB838494G88G88GA0E6B5ACGGGGGGGGGGGG8CGGGE2F5E9ECE4E5F2A0E4E1F4E13599EDECD35735BFE805A64458C6372C4B26C8E5956DA20DEA29045AEA8B93CC2CC5030952D2AD1B3289ED34E305D5EC03C237EB478989A490A030C0DB9656C0BEF01DGCE17FAA1A1A1883206B6A3DE1A35C14933FDED3F44FE6F653D67FCD05A6D1CFB5F359F4166CBA25251FD771C7BF13E4F398E297ADBCE3143D9C5082318D8FF7FF09052FC181055CFFE6118400D8CEFABA3363F23GFB493A37DC
	B0EAC0DBBC352D2C12BCF620946633E0BE84733F4277FE725D83AB2A61A343BD8B0076B499B8F573FB169C93776CE073473D65B06E86188D38DC00AD43F87F0F3D5596FE954CF7DB5F888EA7A1D9A338662916EA9B3EED042F1BE50DB189572C733EE9518B813F8560AC607DD26ACC4E647D3ED6EBBE742579E473B33B1C95F8176DFC81F9565E3556D8C33E4294D1C45C9F7B100F3E31C8FBD00DFB021222280AC903D18FB592818F8D49262AFBE80C4629E27AFC5D530A941703FEBAE51EE7A4C2AE861067DF36
	143273882904B9676F2795ED567D7347661342077F6C48445F1CG67EBD1A045DF7E0FDEF60E84716C9F2865563ECDC02F879C673D49795E1D705EEFCAA66D09CB91AAD837F85F21895E97405935A144EDE6B6AE0079E60072101069CC1D10E97053074916004739A703CC519017E5DEA8A553597D7FF59DE2BEF4298E7DE08550B68138A84885F091A08A7086735D52630DB0F6864CF3EABC2EAA1D1A56958B691754900D41374F87948BF71986D5BDC488774FDF37385370A360BEFA3DEF267C69A3A27CA97772
	C344B4EB1D45E9E44BDBD3611CEF1D311026E463F23DD40FF2AD877CBA006DGD561EDE5DEGFFD848F5797869DB495583F65495A9561F88E812EEF6A564D8086A16FC4B541B49178E783E55F73C0F6BE561E93BDE8A3E57645C11C6AF858B2B1CDA186BE3F6B82597FEF73916696E05619B6D0130A20CFE98EDC1BFFC9A3E9D160FF23C4E701511941E474AEB4CEFF1DE8334D9916137033B058C3D2E392472E4F3DA9932FEF84C39BE4279A1511936DD5608F9498D780BGA3GD3GD9GF9G4B23423E5FBC5E
	F41BFE5BE733AD5FBBD0F790C6C8AF3A5423982624846900244BD2A0C68DE22D992EC35E83576D8B31F88A429D9728C2F54914D585CE1762949C4960EB62F5FBF10DCE9955AF47A9DC905708B8B7D1371FE08EB64C7FE8A1489529951C7ED99D6AF2C29A009361G782E16D1672F335C379E64F7411C6B6A5593089B0239A98B3DEC295B87637B6058B48629D48EA29B12BEAD6EFF359EF3891D00EC55CDB52A04604330E8DB6B4BD2FA892BC91E7F427098A36700F1A309AFEE584B71F006DD4B02DE416812E96A
	F2A0E152FEEA1A3292C96DBF50003C9857051FC12C98F7230F4D8639F2G96A5657BD643FE1BFC3D2ABA16507CE8B9CBCFEF33BC3B943E8BE4D1CB15962C2DE4D47869AB0342CF3BAB3ECE5CEBBCE97D147965A8774F56511931B6454ECC5DC1DA866FB06E8510517431F9E3EEB9F457F7945D469A2E77A18F6AFD84F83A8610BDAA747EE2C38D0F37AE4920A242BE5441E2B41472B23EF6E5E3E69FFB3EB13D0F95B4267731DFB55E5C07DEEECC67C31C767BC62CAD6A24E27349BFB66EE3B12CA814B11D5CC2F8
	3DFF450F3A1095597CC88EC52859AFDF25E067D1742D2AF751377260FBF95217A65F963ED4FB75C112BFDC16361EE5FD3B5ED9B3466D6C9DCB79926769CFA7D067D68FF1CECDA8A6473F4E625B42DB79E523BB7D5ABF38519F47683435EE2B3B04E09E09A5E8D236C552AE5431AE4741D49830D63F68536C7D229F78435EF28E0CB9E3228F5A6AC4BB24E94DAC3FF9308979DA8C6BD7G2C9B93FE13F86710ADDEAD07B92746E2DC7DC93B84BC15E8C7BA1E20100EB1205FCF4AA2FB764D208DD8F2F17A3887650A
	59F24176B1146B443B226742DA41F1DD8C3791826AEBAED1FBAFD55AFB5C10F1B42D8D914E46386D7C3119F56B19E6BC57847C2C1810FD03471E2BBA8DC38D4A2CACD876787C1386122137E44A75797AF4D5233A894602A30868A597309C073CE745C4AF5949EA72AA182F87A0F14143B71AEE74211C261DB006E55D009EA8C427049E9FE942B331B6D9E851331D326BA73F70C0C67DD44739DE5C71197A29BC05FCDC847CD01237234DFBED7A017E92CAF1842CA778D959DC44FD5A1A2FEEAE96BECDC4CEFCB459
	73E8561D336229FC1A855F39716B73AB6FED1EE07A4A0F735E461144FF1F61D758703C673960956F0335C0EBD3C48DAEB90DF26540FC11A2640A1E2C304955274A0A19146940A90CCBD517A332A2F007998E3AEB1628F89D4BE7B64E07B38BGB8EE480938CDB0F72982771E8BF175B0BF1B44FDDC0C38904C2F2922BEBD65A5C91DAD35FDDF725EEDCD399D60F7046F699DDC073BCE23ED261273BDECBECDF87C6FA7AFE8E893CD27E12AC38662E91DACD7D1DF1B182E6741F7B6GDFEFB8CBD8ACCC2B89B05E14
	066B56BBC51EAC07F9ADC0AF400846FB8D9E530FCFE2AE8AC3D388E52ADF23C1629F473DFDAE31B79F56BF873099208AE85EF1F167F8B16E050DF2BC91E79CF27EBACA103FECD837906039F17C7DE0A3BECBAD597E6D2C01D12113B52352046411A6CDCFB734158AB610755E56E6F74DE9EFF3830F651CC9FBAC376EFCA3F3AC6BD6ACDB63356143D6ACD49609D88881ADDB97321F9C415E1D65B341084FF7C117C3DDD22417E912672F4122D2EB7FF33A48DF8F30B34B8D18833499223ECE9D97EF615CC766125E
	7B1A527E7ED068BF68749B1C4FA9A315FB5C709666F7CE38441DF9C05FE030BC6A2A321D013AFC54C90887C6CEFF7EBABA66AA4C57A9FAE9311D0E7CAEA93253FD57510BED7B39EC4F9E9332EDFFB61B441EF8AB63EFABDE134BB4E80D151E43E4696E111D5C97763425FC2156FB7B3E3072933BAFD4BED46CF4E438FF1EF5EF36B9B32FBB4E20DED781FE03A9721F57F7824681E954FA0171D601B50D0094B50DE3D9A537724441223BFBF3575E653EABF73C0F570857382FA6E6560EA297622B93B3EB471CE43C
	5482EDF6C2540ED36D1893B6403CF4E2DBD98DC0AB40A000B9A1F479192F195C621D6BD735DEE836E336A798176FD3575D7D9631E407704B3CA73FCA96BF70F3472E8CFEB1FB427A0DC60C6E438CEF3D65BBD03E35C05B8CD0BD392D2C99209BE0E8D248374C67667221BBF41A26940C32CEC588B7A05349949A93AABF3B15F14A4F68EFBB16495F6004FFC19CFC9898056E42AFC512FBCA4EA01F49F785F7C371EEBD03F10BEFGD90A49D7D94B568F8B15087556E34FC97EDDE67B7EF2513D2F255F3927F7F079
	7F591E797D56551E7E7DF631BD7D7B6D03761B3F5F865AB33F5F2E345B5FEF5C6E679DE89B8D7CE78E40A20025GEB137E745B763FF3FFCA63CB6761C1F7CEE53F7818627E1FF4E416F5CD472DFFA77AC58776528155B455B81EAF706BBA50DE262AB1A4473D5401BA0256AFEA722596FEE3876650988DB3B447DDF2E4664B6F38B5DFD79C2C6F07737C531AF5E64041FA4FF05820C979A30E4AA42E4768D38D993D1D7FDF630BEFFEGE36E6FC8312113FDB756134E39AF3E10BB5CDD65A44E9ED29225A8D88FA9
	1E14C3E6F4CA030C6187A8B446496798A3FCA6F2CC437F6C35E74B7D5B9D197A106DD3D64D1B1ED9FBFEF684E56C85FCF4DA587F4411F7EE128B7A63D2ACE6CA3131CEA504F921EB5A24A9DFACBC02679BE20DC564345F9CC13EC7EDFB7E8FD0CB878859CE2B97DD8AGGD89AGGD0CB818294G94G88G88GA0E6B5AC59CE2B97DD8AGGD89AGG8CGGGGGGGGGGGGGGGGGE2F5E9ECE4E5F2A0E4E1F4E1D0CB8586GGGG81G81GBAGGG178AGGGG
**end of data**/
}
/**
 * This method must be implemented in subclasses.
 * @return com.cannontech.common.gui.util.InputPanel[]
 */
protected com.cannontech.common.gui.util.DataInputPanel[] getInputPanels() {
	return inputPanels;
}
/**
 * Creation date: (1/21/2002 12:32:09 PM)
 * @return com.cannontech.esub.editor.element.StateImageEditorPanel
 */
public StateImageEditorPanel getStateImageEditorPanel() {
	return stateImageEditorPanel;
}
/**
 * Creation date: (12/18/2001 2:35:42 PM)
 * @return java.lang.String[]
 */
public String[] getTabNames() {
	return tabs;
}
/**
 * This method was created in VisualAge.
 * @return java.lang.Object
 * @param o java.lang.Object
 */
public Object getValue(Object o) {
	return getStateImageEditorPanel().getValue(o);
}

/**
 * Called whenever the part throws an exception.
 * @param exception java.lang.Throwable
 */
private void handleException(java.lang.Throwable exception) {

	/* Uncomment the following lines to print uncaught exceptions to stdout */
	// CTILogger.info("--------- UNCAUGHT EXCEPTION ---------");
	// exception.printStackTrace(System.out);
}

/**
 * Initialize the class.
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void initialize() {
	try {
		setName("DynamicTextEditor");
		setLayout(new java.awt.GridBagLayout());
        
		setPreferredSize(new java.awt.Dimension(540, 630));
		
	} catch (java.lang.Throwable ivjExc) {
		handleException(ivjExc);
	}
	getStateImageEditorPanel().addDataInputPanelListener(this);
	checkValidity();
}

/**
 * Creation date: (12/18/2001 4:05:26 PM)
 * @param evt PropertyPanelEvent
 */
public void inputUpdate(PropertyPanelEvent evt) {
	checkValidity();
}

/**
 * main entrypoint - starts the part when it is run as an application
 * @param args java.lang.String[]
 */
public static void main(java.lang.String[] args) {
	try {
		javax.swing.JFrame frame = new javax.swing.JFrame();
		DynamicTextEditor aDynamicTextEditor;
		aDynamicTextEditor = new DynamicTextEditor();
		frame.setContentPane(aDynamicTextEditor);
		frame.setSize(aDynamicTextEditor.getSize());
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
 * Creation date: (1/21/2002 12:32:09 PM)
 * @param newStateImageEditorPanel com.cannontech.esub.editor.element.StateImageEditorPanel
 */
public void setStateImageEditorPanel(StateImageEditorPanel newStateImageEditorPanel) {
	stateImageEditorPanel = newStateImageEditorPanel;
}

/**
 * This method was created in VisualAge.
 * @param o java.lang.Object
 */
public void setValue(Object o) 
{
	super.setValue(o);
}

/**
 * @see PropertyPanel#createNewPanel(int)
 */
public Object[] createNewPanel(int arg0) 
{
	return null;
}
}
