package com.cannontech.dbeditor.wizard.device.lmprogram;
/**
 * Insert the type's description here.
 * Creation date: (8/1/2002 2:59:27 PM)
 * @author: 
 */
import java.awt.Dimension;
import com.cannontech.database.db.device.lm.LMProgramDirectGear;
import com.cannontech.common.util.StringUtils;
 
public class GenericGearPanel extends com.cannontech.common.gui.util.DataInputPanel implements com.klg.jclass.util.value.JCValueListener, java.awt.event.ActionListener, javax.swing.event.CaretListener {

/**
 * GenericGearPanel constructor comment.
 */
public GenericGearPanel() {
	super();
	initialize();
}

	/**
	 * Invoked when an action occurs.
	 */
public void actionPerformed(java.awt.event.ActionEvent e) {}


	/**
	 * Called when the caret position is updated.
	 *
	 * @param e the caret event
	 */
public void caretUpdate(javax.swing.event.CaretEvent e) {}


/**
 * 
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private static void getBuilderData() {
/*V1.1
**start of data**
	D0CB838494G88G88GD7D182ADGGGGGGGGGGGG8CGGGE2F5E9ECE4E5F2A0E4E1F4E13599EDD0D457559FB5630F54DA87B31AF1B2F41C1AF4CABB98718B4520E2C4E31B24555424CCB5890DC986BF6A500E1331534B3A687AD9D412E0C39429DAE3693AC4E7C2170F320AD19A54ED5C9A525851476E03FD4B5B77DE5E3E95D082BD675EF75FBEF0972B136C4C615EFB6E5779BE67BEC87961745292C7B9A1A525447CFDD4C2C87F1F88F925F253C60EF3E57ADC4476BB82300F3C793E935A8A18
	4BD86AF315116F3D0D0BEAE0FC93466FC27FG197A76090350C9F18F01399BCF97BCBE72BDCDB779BD5B6978AB6FEEE87782F8G97GC84F447BA7F8ABCCFCAF0C771AFD049287A1ED53F04D255A8A9B3E50DCD7B70D352B681ACCEF25B91F86E3DC1381ED6E344419BBA8F5233DF566787AF753C87132F79CFB70AE5B799C2A1EE4FBDB4C360ABCC68531155477FE8CED033F3B2BA928C65DC1C1D1D4459003E1F728C38CC9062A3BFB245B029EF20744DBD2D0F44BD1CDD73BF4A16A75DE9095D1170297C4C177
	8B0AA8931275A4529F2BF55133C10FB0E634BE3F270173541DC656CC591112564DA60D155393342697059D4B276359AFD259633E6BB0CE83E0F79E2967F716E579DC63329077108757DE047ECD005EACCECB63DFB8AD6DF726107C45354EBF2620E5C59623E1CBD602164D2FFA1C87299D2C94D017F5B3FC2E6BG9AC09AC096408A0077287D0D3EF992DADF07F1CE0DC6D54527E9E76510FEC98D09B2743DDE18B1F11F0AC1D58F9142EC2C3736A689BD9C0AE68C352F04CDFCC638CDE4FC7AA899F7E01C23B489
	EFD9ABAB9CE4A6BB23FCE602B746578BE1646B8C607DGE100D1G69G39B3B9DF4F5EBCFDAF3EDA410EF4C5102F46BBB4C1B74E46A5B9A46AA6FF2B3DA371178C583EF5773D0F49650953F63994CC77BA36A711CB4193079D8526BC0AEDF2E1F79F2FCFF5F7792C3B75C17DFA965A61DB548ECFC03F4434D106F756A23E5F06E7B67EB0DD0F6307E1EE63ACEE376DFBB98FED4E31242C36BEA98F9397FA9C01590C9EF976F05D66C4B036CC84FC9640F200C2G679C1F2B8A604C9C2E5FE342297F53EE7DB65D32
	3D3F925E01D66CB5F421D509990292942F893AA4F448E20C18EB568AC8FB4710FDC5541F02F0870DAC02A1298A1CAEC4C5B02498DB73DBE19F2E51C5BA9B1022A2DC905588BF7777428164DB08997F56C202A1A6D63079528E1465AD619A1C8887C07FDBF3D0665F21B1ABGFD8D00496A4DEE446543F80DA517DFF76D077693B0ECD176FADB24A012A168FD7C7E55E10CA562ADD1B1DAC4CDD4C2500919F32F05F7A4645229DAB497D1BC7A48B9A0BCE661B70577B1BC1CE117B21FFF0B4E8B0621CB9DF1C33CAA
	9A0624F4A576BBA424A5B6447DE2047176320CB6D68CFC6D4E7639AA33B9FF0FC887EC7C3529EAF7DC8B2066CCB9B552B86B01759733F9BEA435189F65ECEE276B5B391D366C19C4EAD67EBD291DA20C49E676191EBD5C57F652B393F70C77345F275F0F84F276D0FFE5F77C70EB3DE36B30B1137B8D85651E8F779782B867F239BFAFD5B1FFBBAB44C46EE1D7F4501898CAD899DB3BA4125A46FE9AC9EEE3ABA249ED6C254848B674CAA4198D3139B7A218DB549E45E6136BA37B298FAB0AC809363690D6EF2C8F
	20ACA4C5B2BE13C2DD22F1D53AAD021D4CC55B1ACAFD30927A1E395C16FA7E4CED69686D89A477C6F952FCB6717B279DE366B21DCE1E1B30A5C65306EE1439A80BD19054B9B52E98E6AC217EED624D7832D6CE3EF61D0C7658AD76196BFEA3EFA398C76438E87116A96CC2996BD294D4058EEB56FC01FAFB4DB78F68437AF08B72BC4F67E2FB77DEA803F6F8C545ECE622CA73872CEDG884C63B6930D9E34792AE9AC67D4D9E62237F4A2AAF8F6CC7CB2AEC2A8C6E77E4462C3D6768F1B3BE67244668D85F912ED
	F1C01E07BC65D8B59E668906DBC1F19B401916BAF95E3DD4E62FD14B42FDC9750710B90F69ADEF5E701C75938D4FAD84FC31457B86459E27FC31189A14E8CAB0F5B1490B75C9323AB6C8931A3BABAE39630604C1DCB040D73438410AD8D6CF4E27F18EF9F76570FA726727D08F4DB0EE8750F2B8AD0F29F75B5194F5A7341D129E03BAA8A476F2F97EC0C539E0FEB2513CEE7B50AE275CB5D5A965B4A64734731C61F2BA20A39D33813F5C226D28364FA6A728B1A19DF70196B9BDBF5336B23BB647AFE8255C2E89
	0F0B0B0654BDEB606CE2G57FC33CE0756B3FFE8BCE5754D85DA47344CE775CD090577D33C5F06B7EB7BD3D6ED8FF33973F99E5E91C33E0EBE857549D31C2F6EAF775878722B12E2D8BCD568681B2AAEF5C98A473DCFF1D0E153E045F259CBD447ADF066F5G062BE9C01C8663B1966E1794B7994619966EE29362D6403848421DECC6DCD92E4FF5A217672DCB9EE249711A2D3F7A545719EB06833BE373B7FA87136B5E986A2B579A6F2763BE42E2834449FA7483C1163A94748EA0E8D4AE4AEA8715E983747DG
	EC6D3906A1EBBF67EB3F20EB73203F4ADA3B29E19B7523BEB58E0A6FDFC04FEC64F1568963CAGBFC0E88133CD9697267520CFF7C2D189E96E2AA686C97BC25C9BE962FB73E07DAA004DG95B0D737105F69EB463DD121D70A4623B4F1B17ABE27FACF07F599G0769DA089BA471E37B2B9A70FBC2C438A538059E438D153526AA40283B6D7C47019B3E36568B17AF1D3F9CA0BCA6247758E3C25D4E63A9E3422A050C47A233EDF9351A24266339062A64F4842E7893E4B01F2CED64BEF9F4917849A21E2F3A2A0F
	91DBFC15BA226E782D28DB68B05C7859406B3D9C1765B6D3BDE6CC6DEBF419E725AD62B175F1FA7EF2989782ECD94473FE6FF17EC64F18BA16340D72A67DDE30A6F044D11EE7FEC349CB44435D059EA73BF3DC933F5300790CBC9A5B1B4AEDE720CC7E8BFF5CF8971D5FB8E4FE1F39BFB1DF7D8F7BBC52FBEC487E1D4D7679D25BFE465BB347B8EFDB1E19CC6499A7D2FE8BA9B6F92AD84C3F8FD51332BA4961DF0C5F29D65ECE68F466A95429A276D4D9FAED01270F020F1C93E7BC9F56D41DB96D79004553EF5F
	C119244199190BF9BCCD0F7D955AEBC244FC551D275FFEE8A18AC83F2EEA22EE74B1AC2D904C9873D163033D634FBF603E017B5E47F24EFBCDE8E74FAD9E1E0B0ED2FC4162613968C7165D3F8EF3EDCF73DCB43F9F63C4A60C17839482EC8128DC82F1E28917659BB18C74A33E1D832A5686853CECFB56B17EA6B4BD58770D855B39CDE5E5BFC2E6BF74FA492E94B65536447CB6E036DAE6B545B37E7EB5007CCD063959G85G45G2EFC1F2BAA1F73D7EB54B07E509CFC06A184433472614CDD13441E849A03AC
	237159AF0654020F16B9CAD250F8A61F51561CBF3C169FFF874756FB05192219FBA65DC13F43370584C964B6AD872F42C2250B4976C893316C68242DBF3D711B4F454B3F51BB4CF7E17F886F427E946F427E946F427EFB3C8B7BC7F897765B5F054C2E568E22EE8C5067B800AC00FC00824B26FA3E2AE6B615441E4E43C3711CCA3FA4997C7E7F8C2466D5983877772748G569A9D2AE128D1BC1F633581541721EA9449F0718114911413E103ADB571F786B00E4AE2A7C5B3BE1DE84E667C2EC14C11C9E3AAF075
	CF0853D7BAA2D0118704AEEB4F064154FC7DEE705EFC7DE1103EC5001EC01FA6B21C1350FA3833B3A6DA725BC14AACDCEB4C2F46A4749676BF13C9154D50E62CA325B11D6C3FBB1F78463E780B0C9BAD658E62E8A55B42A28A26151476C8A1A35C2B01C77F98E0BC73798F866DBE3FF9747614B54C46254C571D4B063FEBDEBE04BCB5837E7AB2AB869D7A5B88323D9A95E4599064EE1F92C2B91F6DB344046D3EF6884F0F71B56624794EBF04F4C7ECFB7E87D0CB87889943FF84318AGG249AGGD0CB818294
	G94G88G88GD7D182AD9943FF84318AGG249AGG8CGGGGGGGGGGGGGGGGGE2F5E9ECE4E5F2A0E4E1F4E1D0CB8586GGGG81G81GBAGGG6B8AGGGG
**end of data**/
}

/**
 * This method was created in VisualAge.
 * @return java.lang.Object
 * @param o java.lang.Object
 */
public Object getValue(Object o) {
	System.out.println("Generic get value I am in, my foolish apprentice.");
	return null;
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
		setName("GenericGearPanel");
		setAlignmentX(java.awt.Component.LEFT_ALIGNMENT);
		setAlignmentY(java.awt.Component.TOP_ALIGNMENT);
		setLayout(null);
		setSize(402, 430);
		setMaximumSize(new java.awt.Dimension(402, 430));
	} catch (java.lang.Throwable ivjExc) {
		handleException(ivjExc);
	}
	// user code begin {2}
	// user code end
}

public void jComboBoxWhenChange_ActionPerformed(java.awt.event.ActionEvent actionEvent) 
{ }

/**
 * main entrypoint - starts the part when it is run as an application
 * @param args java.lang.String[]
 */
public static void main(java.lang.String[] args) {
	try {
		javax.swing.JFrame frame = new javax.swing.JFrame();
		GenericGearPanel aGenericGearPanel;
		aGenericGearPanel = new GenericGearPanel();
		frame.setContentPane(aGenericGearPanel);
		frame.setSize(aGenericGearPanel.getSize());
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
public void setValue(Object o) {
	System.out.println("Generic set value of this gear panel.");
	}


/**
 * valueChanged method comment.
 */
public void valueChanged(com.klg.jclass.util.value.JCValueEvent arg1) 
{
	//fire this event for all JCSpinFields!!
	this.fireInputUpdate();
}
/**
 * valueChanging method comment.
 */
public void valueChanging(com.klg.jclass.util.value.JCValueEvent arg1) {}
	/**
	 * Method getJComboBoxWhenChange.
	 */
	

}