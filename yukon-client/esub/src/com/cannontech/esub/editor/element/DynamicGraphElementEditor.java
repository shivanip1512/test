package com.cannontech.esub.editor.element;

/**
 * Insert the type's description here.
 * Creation date: (9/25/2002 11:57:12 AM)
 * @author: 
 */
import com.cannontech.common.gui.util.DataInputPanel;
import com.cannontech.common.gui.util.DataInputPanelEvent;
import com.cannontech.common.editor.PropertyPanel;
import com.cannontech.common.gui.util.DataInputPanelListener;

public class DynamicGraphElementEditor extends com.cannontech.common.editor.PropertyPanel implements DataInputPanelListener {
	private String[] tabs = { "General" };
	private DynamicGraphElementEditorPanel dynamicGraphElementEditorPanel = new DynamicGraphElementEditorPanel();
	private DataInputPanel[] inputPanels = {
		dynamicGraphElementEditorPanel
	};
/**
 * DynamicGraphElementEditor constructor comment.
 */
public DynamicGraphElementEditor() {
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
	D0CB838494G88G88GC1D1C2ADGGGGGGGGGGGG8CGGGE2F5E9ECE4E5F2A0E4E1F4E135D8FDCCDB57956F24F44A9FD5C5B5DA51ACD2115AC8CCE5D23A1189A5ED14CD51962D5116CD2B56C9E945242AEBB535630F7C91CDEC3AB6DF664346981B9A1236C08DA38BF1D913F40C26D978C8838DD9D8E104F6D94833FD01874F6FF94F0FGD9266E1C7BE1BF40A66D24A09D6E3B675C0FF34E7D1DF34FB5713F1DDF69F07A89F1D4927177E787A18581C2CE3C764C1B12B7C2F2DC4476578E54C83E
	21D4C0BB8D32E0A04765A58FFF0603B4689F7467385E006FA6326D33C9D81DE4592787B8BD9FB76C5AFC1FDA375C271A75EF446B2175GA500178BD4C2F07FDB71206097C35FA33E119CCEC236BAF04C1EC850468FB978B8CB34FEA70EF9A8FEDC488F819F473C82D466C82FD943343B3F3FCF742FBF14CB0E7C304E69463DEC6BCB3A6E64F36F93ED9B799AF3C4B9B9FFEA905AF3630BF36723C6BC9CD5F45D50AD9A1D8F5364D2A4CCE32AE518E12A51B8552D7E7E61D5DD092B51CB2612183F4819975990C2CA
	196E3F0C38583A608368F3BDFF62BEA77448DD4CA5A50F7F5E11CD4FB365DC3F6972341EDBFC474ADD953876EE66F71C178772C3C0FCCF27AE779C03F1875958A38A0E5D825F5B010AAA24AE9F7EC16AB27D1F474951C8585910C597EFA557212FB22DCBC577CAF98B4340A39EBCC78BE4F9C005C00700CA013CD5B92EE38CFBFFF77F915A0108B5E4446306BE10C88CEAB1730A91239AFC777703C470BE21D1430C91427175F224A703BE123A2B56E2AB0D07EBC462217013ED4432C24E4A8C3695BD67F61E93EB
	0CD725ED63F63D67C53B9240FFG68892027018E8315256C7A3A7B745D6C9A858C193A22CDACC5920AE98DAE29DA0C1A423EFD714D6C4BC4FC5E0F3E74BC6E170253F63F9C7E763B4E6A8CFEB9DC60F7862B39BFFA2B53FE61FBD76B59769E2F5EF89EAC262B9107739144E18C3E9D82231CEFB2FE310D4FB17EDB06DB6C9FGD90FCB62F65AA3ED982BF810F87F541B51063C6FF5C2C4F0FD8ADC6B4F5663433C72B360178239EAF2DCEDC097002E83A5EA64793E6CFE778B62F65CF636FC6EE31EE6E8690AE5AA
	23FA52D274A81DD2CCD509E8B4C94418828F6A9ED9B36F1BAC1E223047A52AD3D331D4C3075515B885A0A579186FF89AF10CC919F4D20DD358A01EA0F25D3D1EA6E8B5A5E97DA391D3AC1A9E41655F7720AFEFA9D330A2ACG5F7BEA50673FE3B9AB873E8B01382FBEE8C2DEC5ED0E6BF82D744BC0038F5A4BGEC2AC1AAD4232806E22E4A7DBF70E2AE2137A0A98E538455E370119432C1EFCD5AAF33C6CA679146479899824513A97E5F3C0D1C8FEB583DAC65D3CC2ED81629C616ACBAC1ADCB55675273EFB0DD
	12EB42AFC9C40C7B91E33DE0578850F44A3EDFF91BEC760D994662D2E292CFCE7869AD16E7B5983F25D65E05FA846F4602DA09535727A5CE075D0F121E674299F14A6E36DA0E4FD7EA57475A8ADBB33D8709CCFF49387FA2D4A07665FB345C13BD4AC54B7D3EA300FE7756C1DC830D55C93FF7B43671F89BD412D4C658D593CE0C4652A863E3033E6C98FB5B1799E31D3E4C98BB695B9CC327FD19B044E57DBE3CDB0CE55D06493F7AFCAC06F51DB225D3D3882FB5DE1FC4DF283AEADDD3E3F3541AD0EFD3C29E28
	C3EC7923082D98FC2F54C9ACAD3FA331943A7DB059B7D31371BE4B5B516DBCD4474F72253AB41638CE971A506722FE99B216F40B73BFE271AD78A23F7479B30FBD6FC7BCAE52D5B1EE50DFC5B00FE8CBB4E55BCE258EFDEC2AF1B8AA8CD8D16F1D556D75DEC5FD0E8BEB438B50CE82893F3C05785824349278F931997553E05EFDC0F97592BFFB7DAD3638954099B2B40D9FC36AFC1E8CF871BC693F17A824E58C6C4BA91B3E95702D13CD897B38FCF7G6D53ECB9E1E7BD5AF7B2AAEBAF3CB3B86FA863DD05891F
	D649BB780A57DE2B46126D994F9269C8BDBFC317FB7D7D559444F53B015F6716361FED36672C01E452082A6CFA9067527F17DE12393E0D324BCDD4387D7D632611202685E787AB713A72B1166FF01DB1372CAB87F8DD897D6DC0C5A9BDDEEA5E0827D71BEB211DD54DA454C3B13AA2FD791BE674895ED302AD6B37776CBE5AD792486A230342B7A59BFC74E98B6AD186FC2FC76A3670C6234DC7D0EB4235BC87A7A87589864BB93EC57FF830D2621B487C78C42A7E89F9B656BBFD99F83C4E59457CB56661F50EA3
	455F4B7853B6BE2FFFAEA932460F016C00C75E47D7DB512E1E069C573986E957D3C1374D2EF1C3552D14CDB7DAB0C68DD31DD3F5490BB29ED45AACE971BBED8FBB63B1D8F39E0873F64EA06F8E74F39B649D34C7A1A9DF6C37FDDFD16E453DA1096FB1FBCF77603E19ECC51F2F247A532C3FCAF8ECB7111F6A686B04C9E72989590527EED25A007E7AB573E1B1FC6FEF10B96D29E54C9733D0404115B21120D1F2400BE3FFB0A3736199680FG4D83ED85AAC0F9A33E3F7B7059AD765DB153862DCE175B9614DBCA
	D8D9364243D05669D840F51F6D6D6DE903FFA7F87C64AF5B63272F76D8567819EC64F81D976D9D25D53CDFAF5FF409B979A0FB3E91F92BB77D363928D7GAE04B034DC7E649AB9465A89E2173F30C60E3A3EDAEE17B7A8F6F925EDBE376B59CE79E6ADFBF6BB51F63D19751D5F6663768C0B565B5BCAFA5A3FD24E577971BF6D7E71EE2BF4BA322CF3C74C5F6ADB1FDF0ABB50BFCF837FF91F0C437F361F04F6CAD9902F82FEED31C28618B231F2AE3BD5842E47EF7EFF6F396FD6CBFF946DFE0494FF7585C7DD96
	BB0E8A7D03CD62CD94EE3555E327C20845911045003682658395819DEC12366DEFC79D219EC37587ACCB094E338CAF4B34A915AE275988F43E76F8A7C945D2EDC7E7B67F408A9F832E2FC696A0ABCFAAF329B94FF4201E295A0C3BCD4876F7A0DE300ED295CD3D4D2E3B8998284F713D7F5505729230218C284D1F63BA83B46217B6CD3C5341ED4AE04FC5A84C068C76F23364FE6F0732571C9F066E7E5E7BA80477E04430ACA30E6BCB7ED888F1E0998946943F1904700CA1ED4FDBFC2860DF8B3137871DE5EC4E
	23DD5975523A6E2E57D2973B33E13D495504D873F6973BB7E6E713B4E57F67DD5E94EFB4B9EEA4D574B87FFDF17A6685E88BDFA315C91378B666280187FF71734219E13F13B8C7C955BCC543C6C965329A3366D760696AB80244F1FD62F3FBFC9E3D3F4C11ADFFDD8FF0BCA7826B63B31F3DE58B01FFA0A04F3BD4B935897EA6620A26D90A36B82047900B032B96CD3F81F6C4F07D249CA304DCD698C13D97ECF37E87D0CB87889D2427A09189GG5095GGD0CB818294G94G88G88GC1D1C2AD9D2427A091
	89GG5095GG8CGGGGGGGGGGGGGGGGGE2F5E9ECE4E5F2A0E4E1F4E1D0CB8586GGGG81G81GBAGGGCB89GGGG
**end of data**/
}

/**
 * Insert the method's description here.
 * Creation date: (9/25/2002 12:01:08 PM)
 * @return com.cannontech.esub.editor.element.DynamicGraphElementEditorPanel
 */
public DynamicGraphElementEditorPanel getDynamicGraphElementEditorPanel() {
	return dynamicGraphElementEditorPanel;
}
/**
 * This method must be implemented in subclasses.
 * @return com.cannontech.common.gui.util.InputPanel[]
 */
protected com.cannontech.common.gui.util.DataInputPanel[] getInputPanels() {
	return inputPanels;
}
/**
 * This method must be implementd by subclasses to provide the strings
 * to represent each tab.
 * @return java.lang.String
 */
protected java.lang.String[] getTabNames() {
	return tabs;
}
/**
 * This method was created in VisualAge.
 * @return java.lang.Object
 * @param o java.lang.Object
 */
public Object getValue(Object o) {
	return getDynamicGraphElementEditorPanel().getValue(o);
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
/**
 * Initialize the class.
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void initialize() {
	try {
		// user code begin {1}
		// user code end
		setName("DynamicGraphElementEditor");
		setPreferredSize(new java.awt.Dimension(500, 500));
		setSize(500, 522);
	} catch (java.lang.Throwable ivjExc) {
		handleException(ivjExc);
	}
	// user code begin {2}
	getDynamicGraphElementEditorPanel().addDataInputPanelListener(this);
	checkValidity();
	// user code end
}

/**
 * main entrypoint - starts the part when it is run as an application
 * @param args java.lang.String[]
 */
public static void main(java.lang.String[] args) {
	try {
		javax.swing.JFrame frame = new javax.swing.JFrame();
		DynamicGraphElementEditor aDynamicGraphElementEditor;
		aDynamicGraphElementEditor = new DynamicGraphElementEditor();
		frame.setContentPane(aDynamicGraphElementEditor);
		frame.setSize(aDynamicGraphElementEditor.getSize());
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
		System.err.println("Exception occurred in main() of com.cannontech.common.editor.PropertyPanel");
		exception.printStackTrace(System.out);
	}
}
/**
 * Insert the method's description here.
 * Creation date: (9/25/2002 12:01:08 PM)
 * @param newDynamicGraphElementEditorPanel com.cannontech.esub.editor.element.DynamicGraphElementEditorPanel
 */
public void setDynamicGraphElementEditorPanel(DynamicGraphElementEditorPanel newDynamicGraphElementEditorPanel) {
	dynamicGraphElementEditorPanel = newDynamicGraphElementEditorPanel;
}
/**
 * This method was created in VisualAge.
 * @param o java.lang.Object
 */
public void setValue(Object o) {
	super.setValue(o);
}

/**
 * Creation date: (12/18/2001 4:05:26 PM)
 * @param evt com.cannontech.common.gui.util.DataInputPanelEvent
 */
public void inputUpdate(DataInputPanelEvent evt) {
	checkValidity();
}
}
