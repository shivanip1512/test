package com.cannontech.dbeditor.wizard.port;

/**
 * This type was created in VisualAge.
 */
 
import java.awt.Dimension;

import com.cannontech.database.data.multi.SmartMultiDBPersistent;
import com.cannontech.database.data.port.LocalDialupPortBase;
import com.cannontech.database.data.port.LocalDirectPortBase;
import com.cannontech.database.data.port.TerminalServerDialupPort;
  
public class SimpleDialupModemPanel extends com.cannontech.common.gui.util.DataInputPanel implements java.awt.event.ActionListener {
	private javax.swing.JComboBox ivjModemTypeComboBox = null;
	private javax.swing.JLabel ivjModemTypeLabel = null;
/**
 * Constructor
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
public SimpleDialupModemPanel() {
	super();
	initialize();
}
/**
 * Comment
 */
public void actionPerformed(java.awt.event.ActionEvent e) {
	// user code begin {1}
	// user code end
	if (e.getSource() == getModemTypeComboBox()) 
		connEtoC1(e);
	// user code begin {2}
	// user code end
}
/**
 * Comment
 */
public void comboBox_ActionPerformed(java.awt.event.ActionEvent actionEvent) {
	
}
/**
 * connEtoC1:  (ModemTypeComboBox.action.actionPerformed(java.awt.event.ActionEvent) --> SimpleDialupModemPanel.actionPerformed(Ljava.awt.event.ActionEvent;)V)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC1(java.awt.event.ActionEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.comboBox_ActionPerformed(arg1);
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}
/**
 * This method was created in VisualAge.
 * @return java.awt.Dimension
 */
public Dimension getMinimumSize() {
	return getPreferredSize();
}
/**
 * Return the ModemTypeComboBox property value.
 * @return javax.swing.JComboBox
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JComboBox getModemTypeComboBox() {
	if (ivjModemTypeComboBox == null) {
		try {
			ivjModemTypeComboBox = new javax.swing.JComboBox();
			ivjModemTypeComboBox.setName("ModemTypeComboBox");
			ivjModemTypeComboBox.setPreferredSize(new java.awt.Dimension(200, 27));
			ivjModemTypeComboBox.setFont(new java.awt.Font("dialog", 0, 14));
			ivjModemTypeComboBox.setMinimumSize(new java.awt.Dimension(200, 27));
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjModemTypeComboBox;
}
/**
 * Return the ModemTypeLabel property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getModemTypeLabel() {
	if (ivjModemTypeLabel == null) {
		try {
			ivjModemTypeLabel = new javax.swing.JLabel();
			ivjModemTypeLabel.setName("ModemTypeLabel");
			ivjModemTypeLabel.setFont(new java.awt.Font("dialog", 0, 14));
			ivjModemTypeLabel.setText("Select the type of Modem:");
			ivjModemTypeLabel.setMaximumSize(new java.awt.Dimension(200, 20));
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjModemTypeLabel;
}
/**
 * This method was created in VisualAge.
 * @return java.awt.Dimension
 */
public Dimension getPreferredSize() {
	return new Dimension( 350, 200 );
}
/**
 * This method was created in VisualAge.
 * @return java.lang.Object
 * @param val java.lang.Object
 */
public Object getValue(Object val) {
	//Its either a local dialup or a terminal server kind

	String modemType = (String) getModemTypeComboBox().getSelectedItem();
	
	if( val instanceof com.cannontech.database.data.port.LocalDialupPortBase )
	{
		((LocalDialupPortBase) val).getPortDialupModem().setModemType( modemType );
	}
	else if( val instanceof com.cannontech.database.data.port.TerminalServerDialupPort )
	{
		((TerminalServerDialupPort) val).getPortDialupModem().setModemType( modemType );
	} else if (val instanceof SmartMultiDBPersistent) {
            LocalDirectPortBase localDirectPortBase =
                (LocalDirectPortBase) ((SmartMultiDBPersistent) val).getOwnerDBPersistent();
            if (localDirectPortBase instanceof LocalDialupPortBase) {
                ((LocalDialupPortBase) localDirectPortBase).getPortDialupModem().setModemType(modemType);
            }
	}
/*	
	else if( val instanceof com.cannontech.database.data.port.PortDialBack )
	{
		((PortDialBack) val).getPortDialback().setModemType( modemType );
	}
*/
	else
		throw new Error("Unrecognized port type instance, unknown instance is = " 
								+ val.getClass().getName() );
	
	
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
	getModemTypeComboBox().addActionListener(this);
}
/**
 * Initialize the class.
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void initialize() {
	try {
		// user code begin {1}
		// user code end
		setName("SimpleDialupModemPanel");
		setFont(new java.awt.Font("dialog", 0, 14));
		setLayout(new java.awt.GridBagLayout());
		setSize(291, 278);

		java.awt.GridBagConstraints constraintsModemTypeLabel = new java.awt.GridBagConstraints();
		constraintsModemTypeLabel.gridx = 0; constraintsModemTypeLabel.gridy = 0;
		constraintsModemTypeLabel.anchor = java.awt.GridBagConstraints.WEST;
		add(getModemTypeLabel(), constraintsModemTypeLabel);

		java.awt.GridBagConstraints constraintsModemTypeComboBox = new java.awt.GridBagConstraints();
		constraintsModemTypeComboBox.gridx = 0; constraintsModemTypeComboBox.gridy = 1;
		constraintsModemTypeComboBox.fill = java.awt.GridBagConstraints.HORIZONTAL;
		constraintsModemTypeComboBox.anchor = java.awt.GridBagConstraints.EAST;
		constraintsModemTypeComboBox.insets = new java.awt.Insets(5, 0, 5, 0);
		add(getModemTypeComboBox(), constraintsModemTypeComboBox);
		initConnections();
	} catch (java.lang.Throwable ivjExc) {
		handleException(ivjExc);
	}
	// user code begin {2}
	getModemTypeComboBox().addItem("U.S. Robotics Sportster");
	getModemTypeComboBox().addItem("U.S. Robotics Courier");
	getModemTypeComboBox().addItem("Telenetics");
	getModemTypeComboBox().addItem("Motorola");
	getModemTypeComboBox().setSelectedIndex(0);
	// user code end
}
/**
 * main entrypoint - starts the part when it is run as an application
 * @param args java.lang.String[]
 */
public static void main(java.lang.String[] args) {
	try {
		javax.swing.JFrame frame = new javax.swing.JFrame();
		SimpleDialupModemPanel aSimpleDialupModemPanel;
		aSimpleDialupModemPanel = new SimpleDialupModemPanel();
		frame.getContentPane().add("Center", aSimpleDialupModemPanel);
		frame.setSize(aSimpleDialupModemPanel.getSize());
		frame.setVisible(true);
	} catch (Throwable exception) {
		System.err.println("Exception occurred in main() of com.cannontech.common.gui.util.DataInputPanel");
		com.cannontech.clientutils.CTILogger.error( exception.getMessage(), exception );;
	}
}
/**
 * This method was created in VisualAge.
 * @param val java.lang.Object
 */
public void setValue(Object val) {
}

public void setFirstFocus() 
{
    // Make sure that when its time to display this panel, the focus starts in the top component
    javax.swing.SwingUtilities.invokeLater( new Runnable() 
        { 
        public void run() 
            { 
            getModemTypeComboBox().requestFocus(); 
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
	D0CB838494G88G88G72F854ACGGGGGGGGGGGG8CGGGE2F5E9ECE4E5F2A0E4E1F4E14DBA8DF09457F9D24C0F12380E5CA88DC31D994516BD7218B1CE03DB52B089D5A1011856EE860F9D17F114311A289952B2D36A580136CFA769F4A089748BA78116EC74F36748A7690C03AC30031010ADD09D85A9C209E9E1255B3BDBE96F7658DD69CED8C03F6F3DFDFB2B638E0CA71E29E6BE6D3E6F7DFD7F5F775E9E29EB4AAEAD325591D2D4CA0C3F63C504BC66864C7F589F6138B4B14FCEACFF2F82
	D4134F78CA601989FD937764592B483D3FF3C0BB875A72E7736C87613D163CFBE15DD1F8C931CF9674E56524BFF06BFD3E6F647B14517609068AF86E8348859CBEF70A387FE8035340EFB1709C0AEC04BC6B47B1610643862E875AA3GD9G39FEB6FEBDBC77501D1776769863AE5E1BCDF6FE2F5DD6096BD85664509CE0F3870CE7B3D9DD34BD00FBFD195209F396203D86G19AFA657470F43736448CC605DA9A564199242E1A52C0BD3C1CFD43AAA28BECFC4D1755E5ED1A994114581C910E7A37F2D78445008
	9096E5928C625ABFECF0503588690276CE0ABB97457DD6417B8600EDA63EA606789E89788588CA1C47F75ACF9ABCCCFC341A6CFEA6DC3CBF850FE99263ADD70A7318B57B0B32862A0F1AC614FB8174B926736C0781CE81DC86C883B8C46D202161F5F876CD6A7DCAA824047BA2115332CFC536E4F86F6D059E83F7DE1CD2D49FA1CC57DFE968C8C24F2D004DFB640E67B119643E49753E6A7CC3E45B574F594A1248E4DBEE17ED45B41345E353F119303D8BD26E7D5474E23BDC3C7785E25DFB7BB22538B4495EEB
	1EF65B8A0CF5DE326C6D1AC1DDFF1B5A00935E0B8C7BE0784D94AFDB704CC6FFCB7158DE003E7C99EE9B937BB8AD43A5ABC9D577F0D2B92C486B338D480C060BF2A2AD4B285DE5480934E4D27C8AB91196E70C53128BFD9521BCBB332FBF2DC41F0DC07BEE009C00F5G5BGDE822888F39B1BEC687C18B6B6A2287A69D9C97609AAE1F38FB59E00279853D5E1A82C69C2F8CA9C97D4C91814C50D98E3DA9AD186130B6639296DCF419E6F09E1D195F4C989436AC2C854C5D5E3E33C0D55B8C695E96F189492E103
	D00470F5FB9BEBD1B702267FAE6293F4B1BE02751FEEC41D4C896330A2ACG6FCCAE5F5E07726AG7E87G46CCB99C2B40754E09AA040B5E5EE1C519190D0CA1A7A4A70CF25EC5E3C7905E5342DC465F5C0F38C2E893052FB3D3D1E3D9E7C01AC27285F51E7B5846A84665BA055B4C3FCCF01B9928FC10F4BCFDB6294D50582BB0DB99D192FD67C93AA652455A972293469EF7624BF796ABA2F40F62CFE17DFD262E5C55282B9870118530461471C3954D4CE6CF8B1A482D74B7AA9818680BCB1C51F9F72C4410A7
	EB7F99EDEFD1382CDE09CD2410779FCFD647E31F262C18CC6E2B44BCA44E09E1FDC00C08E19F3CE8C65F8315E8EB061F7A95F34E43949F813F6E87C752CC7C6ACA74BDC157D5E9F2D697C7C5DD174201F87FDF513D345949C8BCA4703D3ED50971D70902DF1B637F3692FDE2CA8907C52A9CF30A513F117A24A80BA1A03ADF198D6B8C7F44BE54550CB8EF4404A777E121B387399D638B2B8B9E934A1187D58A014AB1C41109884D5F0DCE0B0CF6023E313E69003ED3916EFB51235C77DA2EFE152C3F54EACB16BB
	D6BCB8E04B0EB0DFDA93097B9E23E97D7E6434FF074686832FB1DA4F7F8163E50A92453079499ADCEFAB6CD388C02EF01BBFDD057DBC2E98465E2F48B29329A96B4E6AAA540DF8E5D604F00C816C1CA93FDFD557A4740D9B32E47D6FD623ACE5CB6C5BFA85E5795D982F7F1C506E829839426419F905490371D7F689F3F8329AADB0ABF9E6F589A322200391C6E6F54AB52B4B969A9D062FB42B3C7E9AF6219CA6209D8148D0394E7E211257DFD9B359A47F014614B5E6364AE85B2046F556FC972F3FD655621EA5
	DA1E3DCE63B27EC635B58774E91AB2A55174E348A7D8E355A94305EA764253AF291ABE84CE97633254EA10DE4CCF865A4857BBE83EC6F939E05F4BGDBB5447FC9F91D5566B416E7564333182EF7E15E0BBEC9156DE10F91D175ABEAC874392605B941A3C4F58F75FFCF9F6DBE036FEE2E9B1B7BB875415024F2DA09DD6ACBD8E1B87592A75C63C62DF5FF6DA9033EAD0F2EA4A7EF545A12553E997B0F588E6BC6FD2F47E53F709776626D7F3B2B0C454474C6EB3D178963F274F83EDA2B5F1C3BD89DB467C679ED
	51D99DD4EA6253A9BE5F02E772BE6F62725E89FD9333BCF71F284778308E5A4F826CGF04C6559DDG6F4FF1FB58D573A639CDEDB484762D0685F9F4D1B0B4EC60EB3545ACFE996D47EBCBF97CA2BCB73C98CB2CFBFDF30BF9CFCBE833F9A5730973DEC9E867A44CE3F690108FF19FC51547BCDA94E2FBEF6F59FE43B40C335206F234F5547B3AB96EFB57862D3E37EE3F57D61E4277F69AFB57C5637A6FB04FBE7FF4G63499874C5GB2GF2GB68194GB8E2DC7E3F2EFB67F6722796BA2674836EFD08DB7D09
	B98665C06B3795B44638E06D9EG06BB590A38E1E8DFB6F18D6D08DB826D7BE23CC6D8F0A3EEBD34371938659437BB4662A143CD38A612488267A56236A541FD3CDA62CA52BD12559625F738FE3C169828CFDDCB1C2BCFDECB7C36BEF9ADF1213E1A61E18D6B890277DF2A3FF52D9128CFD6EB9C21BCEFBE301F2E2DE922C68D81130B5177933D1EA429B9BE00C306A12FDA72467C9EE34C99DD696FB6E53C391C7B5661F9506F7CE2DD8DC0FBA281476CDAF6E28C1721AF83A09BE0AD4033735C1E7F237E367164
	CC6C967164DF8F24564B0B87126BE577814C0F1AB22BCE090932AFBE68B47B621EB3240BA194939BD3F190676B029A90EF52DDAD1D4F7AD24DBFFCB015FE198ED6B7E20D6707BA6158D5743F0D54CFDF821915DC6539EF34956BBED918D7E6F5C28A6838CD2D3C3E0BC1BB93E0AD40162BACEF32D87735A846A7BF9CACE14B5108B8C556FF04F33B5A795C8EE81F82388C1086107D915F736F7B301F56E7E37391F1D818449A0D756DFD3D5A5AE7C6CE285BE8AE7F065B1A4B9D8B0C260E859EE372E9ECBD856D0B
	A66E46A00FCF9193F72D094722A5A66E43B3085B8A6D6D8B5C2EF4E7EA3B7848195CAE521D497DF53973567E7A85E7AAFD12EBF88F79C33C39B46454557A9ABC43E234592CAE86C1512E57FCD07CAA0157BA1A3BBA1A3B0F3979DDCEF654FAFF583377F852FA1C7A60B566FF9346F35D72233C3EEA657707195037699A37056E4B18F34C3C074474763E27CA3E53C2E018DA964BFBBDED8EE3BE394EEBD1FF9B3D8B047688C070BA4F0731B61E8FD7BD34128C271DCBDA0B660FF9EDE957991DB9576379B09772A1
	5BF33C1D6F198F7D4E9B08DB52EE35A114679BB6C2BC70E47DFF39289FF582E2347497AE6AC7FA5FDBB43F51E56DAF354CE73CBD514AF95B7DC4AE119FFFBBE51DFD798663E909719C99BFCADABE372B0C497D5F7CCE12345E209E45785F61B6EB0D9B5C7FFEDE0E7BC5D451AF2AF05299152EE25CE6B1222A82476F0431A4AD4F4EF03954FFBA205D83F0DF9A62321A868C9A03E9B767DBFE0F46E18146A09CB264111946EAC9BE2E1628E7B01EC0B011C233A1CA915B7BF354A78BE07C0EB4EE9BCFFEE835652A
	152F5A78DA09F49CCEE734395263364172494334BE398878D8BA77751616DF42F3DC18B63247991F24ABAACD9F009CD11508286A738CCBCFF73C4EEC7DE4F742A5ED1FEC5EBBF7BC0F55714FD1DFDB119E17BD433FC071B996BC73F7390D7B7BE350D77799DE5FBF5706F69703F69640AA008DG85G64AEAE4BD0CB9739CDBE9ED3A24390D8E5CBA8E47CFD587E496E4A7F260C7B551A3F3E1F2CDD76F2D12A9A3A6EAEE68F470CE76158D1CB2D91F2A1FFBE68CB834881D8877094C021495F63AD9D0CBFB407BE
	DD97260274644A199B1744E89C0D891271766FEEABEF3B0D7D2B1630E78BBCFB16AC76A7967B6D7839427095FD3009A4447E818936414BD817378B823F77385B1877E0C7FC5E15260F1BB3D86C58C8F3A47A6BB05054C457001CCBACFFF40FCA8CED068EFED441F5F07811BFA7834F3CDCF4B4058E1698FCEEDD92774D42695DE5EC4DE8E71CEECF7463704BF4C74D4E48273BDADAC98AFF868D1D0535FEB3B98D05551890B067BC5A0A3418B91839B9BFE336E2DC079025033C242B747AE294860683CC3779ED44
	7451FF363C2BED494E95FFACE0FBFC7ED35D03717FC2DB6ADA6827ED49EB219DED49EB21975BEEFDB6794F36547720256D56FBD026776EEE54CD4552BC3B8BE09860B2003E147B6B975B5176710B4B4D3EFA4697409A9D3F3268FC7FB9D7EADE2F39EE7FEDE6198B6F2EA695DDD7C238BE475F6DC2FD69CA04A2996EDE974AC81582C11D8DB570DFF6212F49221F22D90DDA71BA2FE5B317723A752C03FE6706769A93375A416B5B75A64EBE02382AE5A023E57CDC7D3DA6EBBCBA897811E537BEBFB3F92F74E27E
	774158B4009C00F5GCF8194826C8628DA1EE7EF81B889B0869084D8G48844886D88330E9B9574FF90FBB152F8252A8A62912D4C1C2B599B2FA3BBB351ECEF55FDECFC35D745E91640B478D067B209B4FC40A5F2F0926BD0CF6D71938A1EDC451A474FED6233CC5F5328DF8A9DC4EEBAC4101E7AF9DB6E771EC6E984683D6BBFD4741EBA7E7C6725A49DABF715A0947510E0C445A6948882D1D04D8C26D749655F78F0C9F4860B670A39A53A350AEAD6277B7BF26388C409DB7657A28B735DC9F775EDE2E5F72A6
	377F8D5E1B6DFF33B7397DBF69355ABF43ED75226F87409EA64CF1BFB0F13FB7F14F53B591F7C17269C193DFC069C27C7BA2EE4871BF25F4C5A9EE025F89D8F03FB7F0BB3DE89B40F2D0D1795C173D741CAD4932F197C147C8E1B095D39E36B711C6A9A2D868AEB3F1C8B73313A32882C32F6FBA6EACDF7F5C4173F58E68122DF78ACBE5E36D293763EBC74AEF1BD33B5C463D26C06BC0A36FD1F1B27C61D8E59C4F45E964235D8EEBDC5986746CC8E26BD6E036D83B075B6728E91F4EBD5CBE7177B76CF7BCFBA6
	1F87212F7A99A955D4D239C8BE9B3F7A560F37BCBF2E95937B90E9406B0FD1D12599DB7461AD167159FA106C09A890CA50E106C809C69F251ABEAF0B43244EFF9EBF65E83A686B63B9CA53C9BD3D4B6F444FAD1D6F8BDAD0C71BBFB1CCCA4278011D5430EED98887BAD9E1F0A2061F550B7AAED3AD50F28E682D257462AA9EF3155AEF6EEFFD75EC415AE2E29BA4FBE5C5708D8AD3908207489EBD280ADAD011FD31482FCF51059CD11488FAFDB1ACF748E49F19750C01E71AC44F443EF2F7E5E165A09CD0E3AA69
	24A3B1AAF94C913E7E202042D622BA24638F869435456CCBFFC5EF7B3B778EDE3411A20349AB64BA3145A2BA317B0510A44F438B33C68DFB6CBAD02B8AE18DBFFDGE54D891471AEF3834F97B7F7FD6961DF7C01EE901F2A13A6A6CA185A999015D0E71F5FAF05C5F38110ABACFF44DC9E47F8924654446E2DBF7B74FFCD029D8C1062D0D7977D5FCD7FFB71FFB745F4D3CC3797043B035C72AF31BF46F9264683746C2774207ABD0320C17D391FED3E7F9FCF6E588E5C8D12D27AF18214D0959F7541ECE8D2D417
	2D7A5211722C7C4F230EA2E8AAB1DCF7EF8CD62C3959BB7B6E7941B32BAE8D545908ED085803546D40F0236817B17EA30ECD14B4F2136DA13664E6A3C23443D4A5364A6950CA7A3F6A0E850333EA685CBA131FC3B1D3C2484F908D0F79F3EF76963F70C0310D14GB3ACDD00B3E24C871ED446DBB1248B4AD929E50FD23A7CC14E2FCE9AC6C57CE05B2C8745E69DF2F93362EF2677083D7F7FE4514ACC27A9F132499396331B78194AFD43FA5F7152D2E74A3B3B7B6EB1FE83F6CF62FD473C088CEE873C638B3C66
	5A6CFB0324BE9F0D0684D95685F926AF6C43335269F9DD0C576757C5DCDF63E30CCE5637D4C43A27ADF37E8FD0CB87885C98B0DFEA90GG58A9GGD0CB818294G94G88G88G72F854AC5C98B0DFEA90GG58A9GG8CGGGGGGGGGGGGGGGGGE2F5E9ECE4E5F2A0E4E1F4E1D0CB8586GGGG81G81GBAGGG2490GGGG
**end of data**/
}
}
