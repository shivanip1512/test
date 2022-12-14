package com.cannontech.dbeditor.wizard.copy.device;

/**
 * This type was created in VisualAge.
 */
import java.awt.Dimension;

public class GoldPanel extends com.cannontech.common.gui.util.DataInputPanel {
	private javax.swing.JLabel ivjEnterLabel = null;
	private javax.swing.JLabel ivjGoldAddressLabel = null;
	private com.klg.jclass.field.JCSpinField ivjGoldAddressSpinner = null;
	private javax.swing.JPanel ivjJPanel1 = null;
/**
 * Constructor
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
public GoldPanel() {
	super();
	initialize();
}
/**
 * Return the EnterLabel property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getEnterLabel() {
	if (ivjEnterLabel == null) {
		try {
			ivjEnterLabel = new javax.swing.JLabel();
			ivjEnterLabel.setName("EnterLabel");
			ivjEnterLabel.setText("Enter the gold address:");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjEnterLabel;
}
/**
 * Return the GoldAddressLabel property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getGoldAddressLabel() {
	if (ivjGoldAddressLabel == null) {
		try {
			ivjGoldAddressLabel = new javax.swing.JLabel();
			ivjGoldAddressLabel.setName("GoldAddressLabel");
			ivjGoldAddressLabel.setText("Gold Address:");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjGoldAddressLabel;
}
/**
 * Return the GoldAddressSpinner property value.
 * @return com.klg.jclass.field.JCSpinField
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private com.klg.jclass.field.JCSpinField getGoldAddressSpinner() {
	if (ivjGoldAddressSpinner == null) {
		try {
			ivjGoldAddressSpinner = new com.klg.jclass.field.JCSpinField();
			ivjGoldAddressSpinner.setName("GoldAddressSpinner");
			ivjGoldAddressSpinner.setPreferredSize(new java.awt.Dimension(50, 22));
			ivjGoldAddressSpinner.setBackground(java.awt.Color.white);
			ivjGoldAddressSpinner.setMinimumSize(new java.awt.Dimension(50, 22));
			// user code begin {1}
			ivjGoldAddressSpinner.setDataProperties(new com.klg.jclass.field.DataProperties(new com.klg.jclass.field.validate.JCIntegerValidator(null, new Integer(0), new Integer(60), null, true, null, new Integer(1), "#,##0.###;-#,##0.###", false, false, false, null, new Integer(0)), new com.klg.jclass.util.value.MutableValueModel(java.lang.Integer.class, new Integer(1)), new com.klg.jclass.field.JCInvalidInfo(true, 2, new java.awt.Color(0, 0, 0, 255), new java.awt.Color(255, 255, 255, 255))));
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjGoldAddressSpinner;
}
/**
 * Return the JPanel1 property value.
 * @return javax.swing.JPanel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JPanel getJPanel1() {
	if (ivjJPanel1 == null) {
		try {
			ivjJPanel1 = new javax.swing.JPanel();
			ivjJPanel1.setName("JPanel1");
			ivjJPanel1.setLayout(new java.awt.GridBagLayout());

			java.awt.GridBagConstraints constraintsEnterLabel = new java.awt.GridBagConstraints();
			constraintsEnterLabel.gridx = 0; constraintsEnterLabel.gridy = 0;
			constraintsEnterLabel.gridwidth = 2;
			constraintsEnterLabel.fill = java.awt.GridBagConstraints.BOTH;
			constraintsEnterLabel.anchor = java.awt.GridBagConstraints.WEST;
			constraintsEnterLabel.insets = new java.awt.Insets(70, 0, 10, 0);
			getJPanel1().add(getEnterLabel(), constraintsEnterLabel);

			java.awt.GridBagConstraints constraintsGoldAddressLabel = new java.awt.GridBagConstraints();
			constraintsGoldAddressLabel.gridx = 0; constraintsGoldAddressLabel.gridy = 1;
			constraintsGoldAddressLabel.fill = java.awt.GridBagConstraints.BOTH;
			constraintsGoldAddressLabel.anchor = java.awt.GridBagConstraints.WEST;
			constraintsGoldAddressLabel.insets = new java.awt.Insets(0, 0, 0, 40);
			getJPanel1().add(getGoldAddressLabel(), constraintsGoldAddressLabel);

			java.awt.GridBagConstraints constraintsGoldAddressSpinner = new java.awt.GridBagConstraints();
			constraintsGoldAddressSpinner.gridx = 1; constraintsGoldAddressSpinner.gridy = 1;
			getJPanel1().add(getGoldAddressSpinner(), constraintsGoldAddressSpinner);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJPanel1;
}
/**
 * This method was created in VisualAge.
 * @return java.awt.Dimension
 */
public Dimension getPreferredSize() {
	return new Dimension( 350, 200 );
}
/**
 * getValue method comment.
 */
public Object getValue(Object o)
{

	if (o instanceof com.cannontech.database.data.device.lm.LMGroupEmetcon || o instanceof com.cannontech.database.data.multi.MultiDBPersistent)
	{
		Integer goldAddr = null;
		Object goldAddressSpinVal = getGoldAddressSpinner().getValue();
		if (goldAddressSpinVal instanceof Long)
			goldAddr = new Integer(((Long) goldAddressSpinVal).intValue());
		else if (goldAddressSpinVal instanceof Integer)
			goldAddr = new Integer(((Integer) goldAddressSpinVal).intValue());

		if (o instanceof com.cannontech.database.data.multi.MultiDBPersistent)
		{

			if ((com.cannontech.database.db.DBPersistent) ((com.cannontech.database.data.multi.MultiDBPersistent) o).getDBPersistentVector().get(0)
				instanceof com.cannontech.database.data.device.lm.LMGroupEmetcon)
			{
				( (com.cannontech.database.data.device.lm.LMGroupEmetcon) ((com.cannontech.database.db.DBPersistent) 
	                ( (com.cannontech.database.data.multi.MultiDBPersistent) o).getDBPersistentVector().get(0))).getLmGroupEmetcon().setGoldAddress(goldAddr);
				( (com.cannontech.database.data.device.lm.LMGroupEmetcon) ((com.cannontech.database.db.DBPersistent) 
	                ( (com.cannontech.database.data.multi.MultiDBPersistent) o).getDBPersistentVector().get(0))).getLmGroupEmetcon().setSilverAddress(new Integer(0));
				

			}
		}
		else
		{
			((com.cannontech.database.data.device.lm.LMGroupEmetcon) o).getLmGroupEmetcon().setGoldAddress(goldAddr);
			((com.cannontech.database.data.device.lm.LMGroupEmetcon) o).getLmGroupEmetcon().setSilverAddress(new Integer(0));
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
		setName("GoldPanel");
		setLayout(new java.awt.BorderLayout());
		setSize(350, 300);
		add(getJPanel1(), "North");
	} catch (java.lang.Throwable ivjExc) {
		handleException(ivjExc);
	}
	// user code begin {2}
	// user code end
}
/**
 * Insert the method's description here.
 * Creation date: (6/8/2001 9:55:38 AM)
 */
public void setGoldSpinnerValue(Integer g)
{
	getGoldAddressSpinner().setValue(g);
}
/**
 * setValue method comment.
 */
public void setValue(Object o) {
}

public void setFirstFocus() 
{
    // Make sure that when its time to display this panel, the focus starts in the top component
    javax.swing.SwingUtilities.invokeLater( new Runnable() 
        { 
        public void run() 
            { 
            getGoldAddressSpinner().requestFocus(); 
        } 
    });    
}

/**
 * 
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private static void getBuilderData() {
/*V1.1
**start of data**
	D0CB838494G88G88G50F854ACGGGGGGGGGGGG8CGGGE2F5E9ECE4E5F2A0E4E1F4E1359A8BF0D455B58AC1442854E126D429259A1D344681EBDA295296D326D0CDDB1C7A21B3F4CCABF668089D5B2222E6463B3B79218182C6C54D54D8C021CDA31FE0E089B1FFC9D8F5E11390158AAF3BAF1B173CFD6F7176EDB68989741CFB5FFD7B32598D9F6B4E1CF96F1EFB6F795FF34E5DDDD276765C8207330C90C781B1BFB58EC2966CA064399DDF7E0A63D2446CC2E27B3C8330092CB9138F4F19B0
	F76A6A6C42D272E335984669B01EFED5F6616B707E8A79293AFCBB3CA461B39F661E3C32F55E64FC9E5F4B799451711223A5705CG1081B8FC2E91117F23C73718789C934F4161A4E4E1AF2EF97EE839095B8FE3AF40CC00745E587AF514F32AFB3FB9BE710D39E44DFD6FB957A39D9BCD8E79C136F707792CA0739C4B0248EB6B3192F34FA9984F86C065DDE4D375EEF856FA8702F5FEB5D4659794C5D58C515FD79515C684BDD065D735612A00B8A879C5377BA3D58EF885C514E10F278F698E9CAB26F4402CB0
	DECEF16490721A896F99G0BG189E075E2BB5656CBEB30F64BD2238B6A65123DCE272B7CBB6BDDCAF943DC1ED3EFD9F5AB68566528196G2CG4883A86F4FAEFC0B7A3A327ADF70EC68B11A54D0C8D59AB42DD18E689F2B81D106F7379BE6CC5CB1512F6A81C218BFF79E2BCCA04FE4407655DC74BEE6130C3D5C371947EEA5AB6EBC62ACCAE01395993B1C557D4C961E7E18CD98EFADA9EF39FFFC6C0D67FD1C58F92F1E263A8A92704EFAE827B34524B34B46FBF1BF7A5A203E4E02F70799878CFF16620B87E2
	F8960705940F63ADB0B7BA40E323FB83174513FFBDA95D6DC9E80759598D4E5C81A6435A0118ACACAE8E5460F95AA6E7975683F483E8GB381B2819649BCB6A2556FDFE0ECF8855DE80CC8F2C054895BDB356FB5F80AC306AE34A9E1C3D07CE21720CBC20FAC06093926E69F4A5EB3EEDFAD0DD9BF7078C8D4C4DDB0A4D5816AC2C8B4C4BD4C56346C5B04EBF4114EF6C8A1919804B44269FE3C6F95742990B6BA3500E0083195EC3EF39F5AF2D068820AC0G5E19DDFA77234DF3C17F35G45A1EE07E5145E91D1
	07836DF6FBD4F5A022F520A624BB043E3A021E6DF2F82F86E034FA8EA0EE8E0C33ACBAC59FEC3651E9167CA83E208F7333B1F288F36652907775DF3A392F1B575FC2AA9FEACF68EB045C10696BD0FC4CCF25B4D1AEB6AEB9541DE07F52D062783FD8E0BC36A764617ABF505FE079EA199BFDD521E49756821CD0380D9BBF28E0B15BA804C59E251F6A90E0E2A0E6F136362986732AB8A8AAC6332809CAGDE42E60C34572C0B45EF2FEA45161762B50877A6883030056F2A4118948CC317FAA20668938DC3D20231
	79AFA92FF024C70B9D954ECB2C41FC22C6A15E2D757DB51B69B9D094118AEDEDB167E59A2B22AC06C068A6B522988C2F6FC7FF8F084366D9995CDFC8E3DD0E6039E335EC3E70B26A20CBA1B0859EDD32DC41B83E71982F4588866030A6E742F31142E3B23A15476436116F12C5A737BB9365425937B4BBABD496D355EAACA619CC418309E58F51B3E36243CC5693AFA3DFFBBDE57EDFD70BB4BA00F69FC00A4A7D7F6783EF584E186978A6D5161999AD7BBE6CAEC5FF0827A3A224A6BC4CC7AC1B7D5E3DB9EE2E4B
	349F1BFF5C0D7613EDF90049DAF4926BCB229EA198112AA2060427DEB0A0203408C155E13DC1318F75C69A33D55E9BBC5C057AAD0371AAG22F17BBF301E7786338666125C1B5FCC5A63EC51185DEB35187DCFEC623D016F90729C0539B40D5B6E54C1FB1EEB8807D53FC4D32C297746DA3BFF982E2C76A5F876CAFA58E80383B444ED74E6AD4A0BB958C41B39E13DAF3E864F55E239A7CB1B1807D81DECBF0E76D82A45574F8E0ADF2E45574F95DD3CFE2E02B94FE91E17EF2843981F8F639C0015GF9G65BA64
	911D5B61265A3D64BCF52F8D7C2AAB026C9BF720CD5D3B8F395899B447A78E9570B388CFD6B336D11FF783CF990061EE6EC45CCC98E768BCDFFB0F63F94E31461F53F12EB57EE3D722DC4B8177FD75FC7E4224BC3EEADEE7B6CA2FCB1E0BEF2BCB1C0B6F28CB1C0B6F2A5B4470C0435E1970791F55CD1E2B1754A54A55AEBAF750D7C863FAD80D9840A30CFEBB6663F9B39F4665G4DG5DE1568F305CF0C794F3C6AFB4B2404A2709FE62B1F06F5D1DFC6F82D81F83309A208466AA81984F21EE74C3BB4D983B4D
	DCFD55F1FB2E1EE5B0DE8B601983404E40080F5FE5D6822EBC42E579D3956AC973D43FB0A8D489D1436DEED4B1D7FA18E2ACAFE55398457D9D911E172E6BC01AE9B0CE87D89861F9E968DD1E17B2EF3D1EF8D20EA44CCB399DFB1C8FC64CDEA5924BCBAB614E42F866F6F21E5503F0B6869137212BACAE5EBE033BE1957A014E570F1BC75F6EA476F9FD5CBC4A7B13F17B53474D975876B35D6E5F4EF54B3BBF0348BFBC10B467E68DB21D161BCFEF57D6F2EACBDE91337B5FF7EE315BFD28AA9C05E0F03B5BED35
	606747F9ADA80DF21B57BA516607E1FC8AE0A84A4FFE4A04DCFAE167791ACB5C37642277319C9D6824775CA84B518E8B9F2278B9B6BC0B5B599DBCEEB3E02EE408676EAB6B51FE9A0C53G528196822CGD8BB44F377098FF71173646E8ED573C02211ED29016977F867255DCD6FAD627111F577CDE44134679CAFA7090F12E196971566F3D547D60AE77A15B722FEA7E0EE88E08EC09640D2005CE12EDF7A0715CC3F1600E4B49806606F235518AB57A50951989A938D53ED6971F85D568C47EAC371C8323ABB1F
	46DE798873D11105DFC87155B6BC73DDA345635883F373C7F82C76F792AB0E4236779BBBA62BADDF95980F3B3ED69E4CAF5755A72FDF5F2ACFDC3F3ED31F38FE5DD2BFF9FD3A3DBE79DDA22B5EFE17B06F22CD185F4A4EE4976E8268G688398BDE35D199A7E49E2AAC1BC358402E213CAEF7086673F3DB1392E150D673F776FEB441E31C7B58CB50474B97EE0A37A4BD0B50AE4387AC63411AE857B8C36544437B6E21E1445DE0AE67D5323B4F74E863DB24F701E6A2F8E5ED3ADBB43E371C7B4C673E0DCB68AB6
	996578EC0A7742D88318E9618FBB13458F6E370F116605464DAEF3AF63A1A465D190B73E18FE2BE8DC0C76B7A5775B6926737BED2409767FE06F0EE1CDE438F3CD588F293D3DE1510A0F29CD25962EAD6CD54392DEA2D8AF734C71097D537AEE5E4B4C871BAC85F88AA0FF8C729E78ED57983DA3B9ABA65133FC0C591271C8AB4478EA41965DA3746025516C23DEC33970AE59908868E2B8CCE7595C486E4D6367FC1A2450C3B41F72FF5AE96F9106CC596640134D07C6934D2F35666F895B67CB4E32797A333CD7
	FB221B573C8E40251D653D5A6ED378DDDDDC2F7611AE859A05A0EB56D85FD06A60FD5362333CEF28F350B807F159B968A74F71FE125D59BFD1F5230F709A9695F98DAB3F6D86D27E76530ECDA4F18DEBBE47EC2D1D0B75EFBCCE214769CD5263D0DBB3D9ABDD3C47C9BF477B4731D67B3DF66146BD4EE4B5F40549B74F463F4F6A9F53C7B96D5A94EE43339F3CCBEC777281B9D8556F470CDB55AB09F2G04EBC2EF376200FFDF7F37F031E99BA3056701DF13CB6B1BD6DC623ED79D1756EF7D007ABDB3A53E5F3A
	076297244477DB4D8E9EFB0BE1EE47E53C5FBA5C02E7AE8D4699GB9GAB81568214DE1EDD388D2076F2DEC3DEEB5E814FAE215F6C3830BDD1F5DAC5G49AE97340E68022498CC3F5D2EC47AF5DF4E7CAADFBEB117B2396EED4533148273F3G32GF2GD6DB321CECBE6F7D3DE5A8617D1D49753D51C4F215CCB163ED4AC439583EE743FCDFF55C1A43B0BE81204561593E85E8FA737DDE5BFB3B636B6F0D7CDFAB8F668FDFCB723A73F94B796B0E3FA5F13FA035CC6C971416447D0251E26F9798EE58240B3179
	099032681EEB417B9D2BD19F8B0A90243D946BA78E535CB5957CB8EB8A6FB1EEF7719EE36E949E033FECCD2E733256447D60432D097B41952D13FF1F3132B5D93F4864DBB44AE5FE4A12791DB1AEF31E055BE0A04EB3957A43299C27E874FBC2984F32F0F736DB5FD3D938D30508DB8B6352D4DEE7CE3A703BAA83945D160A3F91AF7998E3FBCEAA2EDF6CEAB6E324AFF562F962790965EF608FEB90361A631929319C46E83F78C90C767EB1CEFBE5925AAE9B4453DEEB23ED56CB8363C15345DED1574500CF9A41
	382A1DC66BCD84754E4777E9DC6FD40D4615609F886AEAC48970355F2636CC0177B4GA66BFD6DDC564CE993E5F5A51175D5D33EC55362E50D96E25D8CC90A940A0428240C773554BFB9703EFC9A2F393F79427EFBF969756FB8B92DF8B9722FE03215DE912B392CCEBEED5572E618FB1B4AG77C9927BB07F24F9711D717DC3093D57E8FE64B94756A4FCC7CDFEB9B63E5B1E4BB3EB3DB446F967CF07FAC4799CBE9663B960A316DD7C5ABF602908518A5EC6D5B5CBF053419FGF76C5BD5D931E74FCEAE5F5C28
	5DAE7BDF2AC9789B87C26DF473FB9E7339700A2DA66F5FF646F85F560E754A625B244A2ADE956D138C116971C2BB57E33A2547B3DDB1BD5E680E51FA2C76C274E0F57297F41F79DB572778170E0475994AE1BB507A3427DF749B9DC210705A707C387A3C88E4FBF07A64779B4637510BB13D8A5666DF897D84C0BDC087008C1082B08BA083E09140F20055GE4867429G5BG7683F8E670DC7A00F7E7B25981E9B64331664212DF99334BFF8A6899B3AEC47E0C23680F6930B65D12E13E6F5F13486055D5CD540D
	E106253F9619B210A3F69996833D656711016553CD79A8436A996CFCB35CEB9417FF9543193FBBFA1357197FFA4FDFDBFB3D09EB2B6C1DD8DBCFFB93575628F7E2ED9D75E2BC8741B75D563A4BAC5CE796EE9A25093863D2005EC798BE0D4A057843A2B2343EE721F2C5A92E5B44FD53067B4C444D7162BD93D46ED3F53E773BDE2CC73D12AC1B5F3350B5128257D44B9E5F27B2CA1AE013FB1E057B4C72C56A58C47F4C981B681F5B0F22CF2D5A4CC203FFD7F3340C72076B31A0CBA37427B69FACD4020C5E6BC7
	A672A8BFE267416A2370EEC90C8EB5A343178F2D0F613999897B0FD77D487DC0A073C9D2E0105769D539426893AB02F0812E90588D58ED1075F8A12EB0EF446E30CEB6DAD9270D1AADF7F02F5B75584DAEA749EFA305AC0C5AC8BE46C29BA9202152C6DC90C6C378778CF4A97F3406CF133344E943608502G0F4D93B3DB43B53FFBA473E4F31913B801CD1F486896C4D1D7CA9983FD8F6C5DB0BE4BBAEEF9FDCA6935FBA3CED25CCA8AFB85D96B93E09F7DA94EC3DE6E21826245C4B6C82987773E8FFB5F27FB8F
	FA08539FB648DBB13C9F71EC562789FE91162C8B887AG25006A94FFC156756A419E4A40A04EAFDA3FG36F40453AFF948CBE065A67CD5F848G4347C8FBB1F319938729CA64C578E874B341D474C33185DCF933F66DBCE72FDDEBD337A42DDD69D733BC34606A787F87BDC17B45B5787F40EBF87EBBB87A9F123CFE78C202AC9B02BC5020843016B48E9BE22CFFFDEA946907799AF3124DBDBB0AF2775B767C8FD0CB8788F36510CACF8FGG60A8GGD0CB818294G94G88G88G50F854ACF36510CACF8FG
	G60A8GG8CGGGGGGGGGGGGGGGGGE2F5E9ECE4E5F2A0E4E1F4E1D0CB8586GGGG81G81GBAGGG098FGGGG
**end of data**/
}
}
