package com.cannontech.dbeditor.editor.defaults;

/**
 * Insert the type's description here.
 * Creation date: (7/2/2002 6:31:05 PM)
 * @author: 
 */
public class DefaultRoutesDialog extends javax.swing.JDialog implements java.awt.event.ActionListener {
	private javax.swing.JButton ivjJCancelButton = null;
	private javax.swing.JPanel ivjJDialogContentPane = null;
	private javax.swing.JPanel ivjJPanel1 = null;
	private javax.swing.JPanel ivjJPanel2 = null;
	private javax.swing.JScrollPane ivjJScrollPane1 = null;
	private javax.swing.JSeparator ivjJSeparator1 = null;
	private javax.swing.JSeparator ivjJSeparator2 = null;
	private javax.swing.JButton ivjJUpdateButton = null;
	private javax.swing.JTable ivjScrollPaneTable = null;
	public static final int PRESSED_CANCEL = 1;
	public static final int PRESSED_UPDATE= 0;
	private int response = PRESSED_CANCEL;
	IvjEventHandler ivjEventHandler = new IvjEventHandler();
	private javax.swing.JLabel ivjJLabel1 = null;
	private javax.swing.JLabel ivjJLabel2 = null;
	private javax.swing.JLabel ivjJLabel5 = null;
	private javax.swing.JPanel ivjJPanel = null;
	private int totalOrigDefaults = 0;
	private int totalRecDefaults =0;
	private javax.swing.JLabel ivjCurrentLabel = null;
	private javax.swing.JLabel ivjRecommendedLabel = null;

class IvjEventHandler implements java.awt.event.ActionListener {
		public void actionPerformed(java.awt.event.ActionEvent e) {
			if (e.getSource() == DefaultRoutesDialog.this.getJUpdateButton()) 
				connEtoC1(e);
			if (e.getSource() == DefaultRoutesDialog.this.getJCancelButton()) 
				connEtoC2(e);
		};
	};
/**
 * DefaultDialog constructor comment.
 */
public DefaultRoutesDialog() {
	super();
	initialize();
}
/**
 * DefaultDialog constructor comment.
 */
public DefaultRoutesDialog(java.awt.Frame owner, String title, boolean modal, int numRec, int numOrig) {
	super(owner,title,modal);
	totalOrigDefaults = numOrig;
	totalRecDefaults = numRec;
	initialize();
}
/**
 * Insert the method's description here.
 * Creation date: (7/2/2002 6:34:20 PM)
 * @param e java.awt.event.ActionEvent
 */
public void actionPerformed(java.awt.event.ActionEvent e) {}
/**
 * Insert the method's description here.
 * Creation date: (7/1/2002 12:55:25 PM)
 * @param row java.util.Vector
 */
public void addRow(java.util.Vector row) {
	
	((DefaultRoutesTableModel)getScrollPaneTable().getModel()).addRow(row);
	
	
	
	
	}
/**
 * connEtoC1:  (JUpdateButton.action.actionPerformed(java.awt.event.ActionEvent) --> DefaultDialog.jUpdateButton_ActionPerformed(Ljava.awt.event.ActionEvent;)V)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC1(java.awt.event.ActionEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.jUpdateButton_ActionPerformed(arg1);
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}
/**
 * connEtoC2:  (JCancelButton.action.actionPerformed(java.awt.event.ActionEvent) --> DefaultDialog.jCancelButton_ActionPerformed(Ljava.awt.event.ActionEvent;)V)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC2(java.awt.event.ActionEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.jCancelButton_ActionPerformed(arg1);
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}
/**
 * 
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private static void getBuilderData() {
/*V1.1
**start of data**
	D0CB838494G88G88G21CD77ACGGGGGGGGGGGG8CGGGE2F5E9ECE4E5F2A0E4E1F4E14DBC8BD4D557152E0636F455C94D1424A4E327F6CAB3CCC2DA52319DBBE5E51CAEBBE39BDBE92BEB1CD5B3E3D713091D50C4930CB4B166725E637FD59E04G6A93D1D179BCBEB106209179848C09ACC045CFAB8F5E951EBE5EA36FDD6071D1E71F4F3E773E7B6E83B45AB52E353D6F6C334FB97B6C3377BE7B6CF3AF02F94F1224C403D9909213847E6F5DC4C1D8F5D690567E7249E791376445D613207A
	3797E0077071C1020C063A3066B6D32EF0F9F7AA14D7C03925294D74967C4E93EC3B25EE7891E41CCE28FB61C7762E594739A561B8A9347C28AF931E59GAB819787F06C25D6987FA9DFA14767C2B91B7FA610E89004B8CA73E8E9218ABF43691643F38D4006CD0466BFFDA53C3E9E4A1DG0B8122B7B15A1570CC235CFD3E6198273B7CC018907F338CC39699CB55BFC279E6563613BFAD42D729A00C42174F9E05E7E357752193036E112AC11B4B65F6C96260F015FDC034BBA437274AA38E09AE51E313440606
	96712AED4CA935B8ECCE7710A044ECA67C2EAACD25FD89C288145BDFA63816FDC4C6827CAE84303E0CBC7FB22B117394F6BDCC58702D6D0941F8DE78B263B57AE50567E3BFFB43587F8A695FB019495BED027AF2403179940DB54DB59F7E303329F865C9F53B47A4510BD363F3BBBD09F30BF88567963D070C3D9E4A71G1971081F3090FC99149B81FA63F14E478F621C7B263FA5A48CD499F2024C59994F669A962F4CB9ECEB3671ED2AF79963C4F7E221AE9EA0F5CB2B298220934089D0CC757D5F244370EC9A
	101A5DA3A3EED7536868A9275D73315BAEBA61F7C38354F0DC37B868765885016974AFCBABF478C18859622F4F84585C4E8B2803D15D0F8812D4E6C852195B5235D906F05EC75496E5EE9B379039AD233A328AFEA772FE997EA794DF712A02E7EBFF1A62C9399D6A22DEC5B977E5A3AF1D267B055C9A2BA1C50717709F6FB764EEE5BC14EFD5F8E1B2FEEE024838977023G21GCBG1683EC580AB24E9D2B1BCB469D20F39E174D59B3B6B0EA73C827469CCE3B686132CE1D1CCD56FA405A955CF6BBA61748BA75
	9A2D7F47EADD392C0FB49B36F1F91893943930314F849D3BBEA1D0B73265B5AC23B6511E405650A8632BA83ECF05E7EB7B83EADB24BC8CF52BG987D8B145E7AC7C61FA47773AA453728702C1F9B94CF4AFDD097834056773EC962DBAD2F010D827482B8819682C482443C06EBFCE92CFA1EF65425DADF5656BCDEGCF51A7F9EC9DAE2FE4F38D0A67EC9E07ED40A9FA85CE33EB1C48F5402FDDA9356FC19863C366F09DEE9774EE9B91C1193C0C6648788EC26391E9ED2FE3C40481C6C6856C37EEBC8F1ECE1BD7
	BABBEA87172DD0307A774709CE0C5B4EC10F50817C4EFC1D48EBA375616BE17E892F212C2227882E8B6A07DFC7397CE0FCA7BCBF8265961D60B99D03048D1BE792F57429FD39701C817A12EDA0CFG7B9BE81FAF76219E36E4BDACD42C2D5255C382195B19CE15ED576A6153347FCE40BB819682C4ED47FD39EBDF5FBC6D027025AE93FE4913700A38928D8D9B43B16FB1D2B4E5234AA691172421E1F25FBDC37DA14CEBA3C06A1BA87B4D6396261327ECDE9135700C8794C834ABAB41E8DF1CA0FB0CB8AE3A2496
	F1D4F4596107176B401609B4C5BF2F3AE55DF91D62093FEA8685724A78C45F8E0607BE545A0E75E9345EA6C99E47GEC21BD22A4B9DCC3CA7B9DBE420B574F95FA79DCADB748DCADB047E3G6D72DC9F716529742C536D3EBEB65ACBAC086B6BD53AAFDF86FA69CDDC7F43969227054B65FADAD66AE3764CF76D6683E41CC07DD8FBD747B0EA4AEC0C4D96B2F7741B8C370D621682CF6B65795EE769D3515DAD1E7CF9D9CC47B06A68725DG268BFFB599DC1743A675F57911C9FDDDDEB2B93B2EC6CE6A69AA2B7B
	6EA41923FB4225527D2513449F8E3ADDAE119A205CC4E071680B3DE49E8E17C3BA6F308F09D20FE3CA0418C6A06715B7294FB5416FC2G66A3A776210FAC1BFAD0D8FEA5CDB71E8A7FF639E1945A105FA17CC9FCA463697BD34426C427B88202EAF60F39A4067F9175679C6FB5D2DCF872D7C95B7253EC3BB8B5A6C9EED7050D4E250ABD3AC44FD537E7C43417DF330D5B2AEC93D295DD0B2AA6DA5DCAFE9F32D4D61E30781D8EAA2E293B3C5224692BB3F8E76F9FBAF748AAB07E0EA6F17E1A49B66A3C2B7C293B
	3CDD7EEAB8FF5D398F5FF579298FA21FD50A826773897743F7DD0E7EFCFEB6E9625E57F31368EE846874B200D8G263B6FCE13FA5C8F7826576CF6BA1959C1DDFDA23529FDE6151DEC047664FC1EAD579FA91B3D7E7C9C7577852DE7FC164D107844ABFEBAA602B848067511B809315B4CCECD5DB91BF3CC09896BE6B26069D46DF58CFF60063EBDD75CA07B5BF539FF6B0DE40144F62CCF66FF22ED244FD10FE384C28332F973BC440DC93FBC84199B60A360398C00356D6AA6E7F34D11DFD8E4A0F2285A0BB90A
	D8205F8810EAE0FD349B18BFE2F4AF4BF46B219CEFD0684CC6C6F759C8688A7661B93BDA46D5D25CCBB049ACA31EE5BE4ED567BC6C5E3D3A7E1140925E7FD2E31B660C97FA0B743B86704F9AD13F9E3B210EB71A3CDE7720030658FC8DAE5CD47BCE067B734DF4F8DEF5F83CF05E330BBEDCC77BCD42AB0941B91A2F4563C70E8B01E70AD61A0E40F02EF9029AC52F3B990E816D208A4D7256F24EA1CE88A8079BED6ABCCACCCE01A1A3089C92787C8BCD0A9CE237E69B99CF5D1F921950FC89B1D1DF15F7827643
	0606531ABC4873B45E2267A9680765BBDE18A6FA39884A11G4B8158596B7CDEBCFB2D83DCEED21B0959CE38C9EDBB6D009F858883E0BE6A293D35703CFD1F0472BDDFC2E472D99CA6136D5F651D64BC2E7A4E7DC2632D2D09FA791A501CC4C3E49213E9EC12A25B3EBDB9C6A7G13C5F1A94A22A4394D541B0C32D8B621A70B0549E8FB3FBEC96772991CAA4F4F542A6DE579067C6076124C6DA5D9EBAF4EC432763100FFB6996D653FEF6ED059CBB7E8284DB5849ECB0E59EE9919EF626587EEA521EF9250577F
	83C9488A895027B9254DD4815082E0CF41B18A05B973AC2D3ED972ACF1B4E7911252261B1F885741331C4295FA7E0A02FA7F1C050362E3D4F836461B27B1E7318A6A2C29EDBC7E7E53DEED4EA4AC55FF4CE8CD19353B9E50EE37263CC6530E69CCCECF90DBE67AA3E08EFAEC275A7747C1DF39E92017G6DG4C5ED71B505E7B8147566BCDA3716561D0DE8A308AE0A3C0E6FA1B299C20B39D57EDC85073F37E6B964C49A9E7CBA60B3D81F949794930BA201DD6064EF47FF6EC5EAFCD603CC352F15E9FA491DC8C
	14D749F3DCA1E429743F4B6DF0C9326EFF1AC876E13747B164F0A1EE1C62865D9EFAF8E038A78D414FB54B8C7A671A278C7A671A9506594F682B8C414EBD6CBCED24E70375B0BFA183561462AED21C994A95992853628C09B93A64321B16097E307256693BF97E4D66E3792FED6E4C5D9EC3FDBE5EC54BBFAD48217274FAC5AFD556961BE463F54FCB79024E196F935B08A8E03C9D36B70D5334CA6E669A59C6DFB9D428EF23DDB650A4E61FCBE63476C9F29823A007108C344FA7F6221EAE4E407D7837C6620756
	C0B98E401409EB324624173BAD49E4F235E6867AC11E57B185574FDC133EFE961808DDF85DE31EC1D12B033BCC05F21DE2719D12B8C2C447E3DD5ADE32F9602C2CEDDFC95B333AE06D0F1A0269B9DB173ED2753A544ABA95BF21A72396AE1B3E20B2EAC9A2FB01CFAE37537224BCDE51945195FE57546CF4FB45B323BC358B7808CC928B3CB305EB694C447D7F4989924FDD7558C6007D1ED1F1D01849A2341FCE21EEEC845AEDGA500EF84381C05F154071748B903C7F04D647A0B78841708BEEE5DCCAA1F770A
	AC541DE7D2096EEC0332B99B6CBD9BF567FBBB35F2E949D6E451179DEC8FBDEBA27DB9335BB4797AFE0ADF204233BD74F9A346C9E1D017100DBE68853A8FDD4EE9B3B9F390571504FE49A763B2FCC837D246B9FDC837A687E553B9C56623B23D2A738E175DBD81E2A219FA49DB55525153F52627754A19CF2EB4F7428F96C7AD19D047D147520B0346D1E539CCA64778333E3F084B7DEBD318379B053A48DC321E7B2655F1A065917E2B02A72BBF6BD7CFF49B42DDD57D977D6A892FD9A6F57D8F7D6A13D46D593C
	1E5E0F6708EDCFAF961C5F5F95748EF9931F0FF987FB2E2CAA92CE1EBDEAE0FA724C0CDACFBA0126FF07229B520E20FA12C656BDF4875307AC99EF2778A8951E69496F281E12F2AC54B56E44357EA9750711D00E11F14DE6DC7F8DBBD11FAFD1FD3266C13C1207382B94C74E3064FC4AF0FFB06A6DB1244DDCFB09F59EB4EC0CE4135E987349234EA71F4B745D10C4F36060280701AFC1789BFAAD3FA10F6846D0D28B270D5261B9D1D0F2478416838D01FC0EDF13475606G6B7B29AC256F7BFC58F739B930EF63
	9CFDB71A153ED98CF6BD1B2CD1BB606DE60C411ECEBA824FF336EB01A7E9C0F6F95C60E925C906251996AE7757266E6C9E75DFD350D6167E73C34232AFBC13986C4C9D42679C411F7D15C5944F6632B6077870B5D03789401C8F7E9660A4C0DFBE4EEDE7B26111C44A40FE13A4598607E9668183E8128CD050E403E0BA9FCFFD3B937AD9100FF6D011023691A1639ECFC55BD819AF5BC69A61AB8E4A2602B6D3A1C085C08B4065822439C6E9C6219CD6404E2C4B60398EA0C12679FDEA3FCA96242FEC5EEFC50122
	8BAD85F35B09968AB9AF0A8D195254E3216D98857FFB360C5B9C877B4F72332566949243BBED136EB18876C35F2279AD79ACD58265FA004BG23EFB159B0DB38E8C6DB08FDCB5F4EB2D43670AC50A43C45EC0114B38B95DBE0FC6C6FA1FC79E51C653A3AEADA274E9B487A3B7A12FA8F4BFDA449007D8648391071D9D6286482589E7667D4B98F84F5FB68180210AAA87F18AFA8A7A1889F773719E8B7A50FFFC5E8790FE792778599F7919FEF03EA5C766DDFB43031177AF06C42375BCCEC1C474870BC769A4DA6
	ED64580F19BC2F1B48A67BA03907012848B1C583309EA0F48DB11B1CB2137AAE6857893088A082E0595BE80F2FE7178A549EF5EC3155BEA4E27C00637DD7CE705879391C395F8FF8B1076496875C3000A324FF444767903D145C239449F0FF4CA1F20663663044C8B97E4D9C6A8B442B944D74A3036608875C9E9AD8B39D5E1F04BA3C86669B87E0A982DDAEE2F981C6F3B185E9BA0B181E5B616963B40B48D378F12A7A5C223F9F9D28A49B50691EC10F5B69A4C1E98D4377D712F931D3905E2D180695DCAD473D
	F6A50336976128EB833F0E34A9457E78DAFE5EAA98CC167B407C4712B4B51F6B789CCC456073G984D529A3D9C09956ADB0AB10777FB03BA363ADC4CE8C2647A68F13D7AF0F90C1FB8754608027A654518CBAD4A43775C561176A5F8BE2869A73998799CC932008D8D9FFA9C76D33621CE6A21184D8C64E14C59DE02317EA25A6782A80783C417605D236FG5AE95423778B1D8B2AF5E3418D3D850615A55C2717A8F63AE0EB36110D79E5B35AE979AEBD9F316983BD9C336781E29AFC1FACFACB316DC7F2778BC1
	76FA304053A002B3835760C058EBA3B9EB36B78C262A7779FE60C55AB57B1E4346EDADA47EBA94E8A3GD682ECG0887485C8D3681D08F508E508F6083D8883084E0A9408A00F5GF13B51F70C6CBC940CF7C072C5238E44E3E3F9A4467F8FFDEA7E05BD90AB6C191DFFB65E674C41FD4F174CF37B1E3F266BA7024F681D9C65676A2F19E9CE6B6AD52FA87B384566DC9957616DF2FB9D442BB23D7DB889F3226540F37F9E545BA44BFEA1B07FD1459CD2C3C32BB4B8AC5ACFF177444FCD8FA6218E076FC19D1ECC
	A67D47C2F9A300E0C1FB79DC8A5ACBA1603AAC18AF79D98A7691EA41BE92A86DF2A82F8358E4C1BB38F6DD3DDF915D8EF6A6CA5D4B5623E22FE287980798052156DDBAF3EDE8D0F9BFF90E6B5270CC6E5CAB73D7C16FC2219C8330E6AF4EA52E866DABCE260D24F77EE525F0A68268ADC5F9BFA015846181BDA56B53E2447BB7E7A90E737C387C6E2B0CFB4309E32FAF4531B7DFA0E31B77C1FC8750328F63C0CB5E1D3D67D8F5076D06EE3B9DBBEB1A4C047F4B7B14B80B6133A8FED805E7BA76106C13A528DB37
	8F734495C5B437D786F1BC40A8C0A8C0A4400AB274850D7964CEFE565CF82FFB34930C5729CA633279ADB22B67378E7A0CAB1B5D9F30D8FF0A46EC42FEB68F063BC5F1059C476FB88B037B0D7E42397D0623D0BFE6F1978646ACD221FE4CB2D5280ED9986ED6A15137A17013FDB2DD080C3BA0633EC47BA438738E3BB4AC633FC279A278F6118C087887A9DF939457077B0C8AF701633ED1C8FC9FCCF958AD676EA38A49397A2A4369646F8DD19A078BDC21AC0FA7A80F0ED11B0A6F6F49388B72DA447A8257E72D
	CF3DBEEC7DFB1266526F7A7DFAF7A136B95BE96FC6787B1449F7E6CFFF203E39EB3F561EDE25784B7B3576F4AB996DE9986AD66DC77F911AF2E7FE6021BBEC377A365B31F9B9E9CE4AFAC0BB5FB10AEFBC201D6FF6F90FEA073A288368BF3ED0CC7CC76EC17023GDDG43GB3GE18751FF343ECD6EC40345925DEEB74D741224AF049222C795D570BCDD9A4EB1927A0CB9682FAF31870365691CB4EEFF762076CEF41C62638FEA6FC437D7607EEFAA07F914631AEE2E514AF8D539B2660672E0631750BB5678F2
	6D7887A85EF4C8BB7EF726F0FCB354798E61787B0D332D717AC30163733B6471595AED8A5A4EB022EDE7BA2C50969E8EB6DF71A27D7661F01B661B888F45372B70EC3EDBAF60B791F628DBF998671BCE0EA01C17BC556F45F9F77B8EADF06E5F3F27E3B0FBF827A8781E55D824FFC7F52AC87F8E353DE876F7C33B0A023F9BFA3EC87DEEA87361FFEF8E746BD1E635DFE7B11A6DGD9BB1E1B20DB974317783294BCEEDD7C8EF2F99E2BF3BA083553AF86E4393F122C2C73B6556F4FA75F7BB57F5B3F40981ED4E5
	CE532A5F2654FBBF762EFBBA860FD70A036B74C345F347E15FA8560F433EDD9C9807BDD62C9F07C5972B63B01EDB26E7140AA330B79D41BC72476984E70772C29997190E3965A5C7508FF9AAC93C34C9AE4F50F2E68516CD15EA39923AFB30F6FEE35C6DF762595CD3EBB157EE2DC0F9FC2E8E6551AB4F37F41A4CBFC2AED75072B2393CF46ACE740C74B7FFF97C7C0E46182F5C585C9B0DB877F532BC0C9308AB2FC45C5BA384670372C299976EC23A68CA144B750BA467A0D435194CG95G6DD5D86723F51721
	BC833098A0C62EFB63025EDCC99F731D3740E9ABB4ED14718BAE5E69FD1ED6365AEF0752844C1FE6241324583868119C03B6A7CBA41C32F93A5CCE4760A4FF1F60G35BF12F3DADF05775E76F492E3428146B1C5E243001678AE42825A163C3FD5D20D77977F120E77977D557A7797C902F2FFA181CDE835F2FF31381A6DF51509B80755D3443FA9C990762EB34B09BED1294E09C6C2DBF257340A70D20D39681ADA24C928E63968DCEB1B294C4A6E24984F8F57A14F0D56396FB63B0046EED5F876D935F7AE9F51
	6F4FCEF75A86C8FE1A65F77F64F7379E42470F34A28FBDC664A1A6888FA9AA9E34F72031506677C6A6DB56DF51847637BEC8FF46D97A5B246A0F4D49BA92F81F7561884AF61B154956DC83F6DA436E77989F5FF5A19FA7EB7479C8D371518BB443B50AECE7EA34327DD11EA2DB4C7333F75E1B47BC6405B6DA437025C9444649374BA3A3E4E7342B6A9E2BD07AF9072FD154C56D9A11F90456E00E7ED735FA750B657A68BDFA7551F27D7B79FA75ABEA506E0E27578B7E6F1B2832FFD567DA3BFBBB1A1BBA2F74B4
	F71F696CBC5554FD2529674AA72D2DAD2DAD2C7D57520F4E523E7DCCF74745B31F746A77003945CD691833DBD7F33B79788992EEF21D39B69D645DB5EA40EC2C67AAF12C5CDA4C8FA65650B38B147B81FC35182B3DD00B6760C500DBD90BB946EF56E22ED62843BED6D35AE3D06E82982E13733AF5B85E023A5B1D5BA33B15393DF2B5485C28B631314226B1C73BCC9EBF440863EF2A43BB49DF11A1396CBF125FA7AD2F477AAD96D24FE4609EC19974C3FDF8BD767BCFA3A80358FA1441F314B6751DB613856058
	BBC83B4105E3F43DF33BB228AAD5F87D5DC56CA7D46EBBCD166FF299777C9E44A548B8CFBE622CC7F1BF5C122EBE7B8E83FE66686C7BAD4FBBD513F60B0250C66860F93E056A5932235ABC4CCD0ADFF1D41B07C929C15947C2DD7D3B4877155ABB4B9F0D5CE13BC7EF3B9D1BD7F39D61BF7CDD6DFCBBA9BE62DD6DFC574A76968DF566E3B85F55F56A71FD00DFF8ECBE6B7470349E5FCBF85B257C39D25597EB77A53F9C4CFA285FB42F713A7C5E51B43D57E6AAFCEFBE6D4682F2B073DB177B6E30DD4CED37E36B
	F641C5F3BE6FE957F300620F3D27DD4F97E45F5182F5116F61FABE67D20F1F59G7E22E1BEF25AF8D10F6FFA5E36DD278F56AE3BCE2F5DE5CEBF9A34DD5C9E3DF6A11CBEBCE83B09FC3DF6D11CFE39CEBBF61E484C884C476C48D067E398EE281AFE9F867D44B560D9649755F896C9E8C0B9CF13573AB9CFA12A5FD9B577BE8FF160AFB0C6C71DB227EE556F5F545E7B31636F6998ECCD7F27AED09FDE288B5407E83A6F173C8F674D77D19F72E7D09F0E4938DC7A8EEAA814A3E55C92E360985FB66A685CC4A01D
	F3A2708E680ACBD907E1556F4DA377FECD52DD77FECD3639D08EDB0C08CBF4239CA6E43AB7E92C9283F2DEA54B5AFEC94EB548387DF5C8D75E0838AF487918FA99E734A0DD080C33DB10EE110C3BBF9F69B64838177211AE2E91637E1B5564FDA4496DF676BAC6FBC51F445F0B0BA02F3E71F7791F2B4633D663F108EB81C6819681C49F47FE7EFD5A7F7DDD8FF9FD021F87ABE664778A0F4FFD46FD96E88810F3980D5D0EEB4FE187F71298D14991B4B99DC3AE72269B4F8DB4495F1C18CEA0FF85177C79FB7000
	425F74A5644FFAC27F9DE0B534G8D81BC2F761D503EDF3C6C2052775765733DDD27EF6DD9D85B37AF206F5F142B7836E05FA1C17ACE8A52B741078774FD6A3052F7F281769D91246F44D978DE266A1B455D99D6BCA72CB8015F943CE755FED370C0227A1B006BD66DB785243DD27FF52B761B827F762B2D5AEF8A30BD6369CFB328A75BCE4C760E28C8DE5F466FF2F692BA75B937077C2FFC1651595A564B7B0F3A047D57FFB0DB7F21A4CC60EDFEA11FE31C9F60B96C71CA25BE4A027591F27DAF2A147AF78B64
	77DE647A4D64D3F57CA64DFAE73162005FF79366136027CE4EA7D6FB72123ADDA334691AD73BDF7B45EAF6E8631B5FF896F53B0550E6493C5AB596285BAD05B6AB67E847FC6A64CC60FEF8F3A6F05FDCF9A9106E671782697ED7E72FFFC9E72FDFE951694F92C857D390C857D72026E3FFFF323C3E59G31699F04A4C9C8E1EFECDBA4A1193D1F6D7588B972B75C9D745D118D63F58D465FFD4BE890CC9DC2B23B436990CC64A226C3C82277B49D02D1F20F7A485F7CB229417B29F0D3B07CFF44CA02153C27E071
	A8E9BB0B175F2EDA2E3ABD1641B1AF4C53B1857841E11BEBC874CA4259E627E3703A65A0935AC10B6466F5C0CD3302967ED51D529510C843225FB0DA9A89B6ABE1B74FABDABC74AF1814FB8FA11DE8EF30D8A5E12FAA41480882E9043F2C8C793B7407EE295F61CF787C3644E05F212DEB666F68B6EB3F67EFA9A7FADA5E82FBCB8B3ED792F610B843E06F57760C581CCE08882EB7396C64DD5BD31312285C0BF714137E3DC843AB791DF9B9617B1A2A4D7F81D0CB8788DF39A053999BGG74D5GGD0CB818294
	G94G88G88G21CD77ACDF39A053999BGG74D5GG8CGGGGGGGGGGGGGGGGGE2F5E9ECE4E5F2A0E4E1F4E1D0CB8586GGGG81G81GBAGGGD39BGGGG
**end of data**/
}
/**
 * Return the JLabel3 property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getCurrentLabel() {
	if (ivjCurrentLabel == null) {
		try {
			ivjCurrentLabel = new javax.swing.JLabel();
			ivjCurrentLabel.setName("CurrentLabel");
			ivjCurrentLabel.setText("Current routes(s) defaulted: 0");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjCurrentLabel;
}
/**
 * Return the JCancelButton property value.
 * @return javax.swing.JButton
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JButton getJCancelButton() {
	if (ivjJCancelButton == null) {
		try {
			ivjJCancelButton = new javax.swing.JButton();
			ivjJCancelButton.setName("JCancelButton");
			ivjJCancelButton.setText("Cancel");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJCancelButton;
}
/**
 * Return the JDialogContentPane property value.
 * @return javax.swing.JPanel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JPanel getJDialogContentPane() {
	if (ivjJDialogContentPane == null) {
		try {
			ivjJDialogContentPane = new javax.swing.JPanel();
			ivjJDialogContentPane.setName("JDialogContentPane");
			ivjJDialogContentPane.setBorder(new javax.swing.border.EtchedBorder());
			ivjJDialogContentPane.setLayout(new java.awt.GridBagLayout());

			java.awt.GridBagConstraints constraintsJScrollPane1 = new java.awt.GridBagConstraints();
			constraintsJScrollPane1.gridx = 0; constraintsJScrollPane1.gridy = 2;
			constraintsJScrollPane1.fill = java.awt.GridBagConstraints.BOTH;
			constraintsJScrollPane1.weightx = 1.0;
			constraintsJScrollPane1.weighty = 1.0;
			constraintsJScrollPane1.ipady = 150;
			constraintsJScrollPane1.insets = new java.awt.Insets(4, 4, 4, 4);
			getJDialogContentPane().add(getJScrollPane1(), constraintsJScrollPane1);

			java.awt.GridBagConstraints constraintsJPanel1 = new java.awt.GridBagConstraints();
			constraintsJPanel1.gridx = 0; constraintsJPanel1.gridy = 0;
			constraintsJPanel1.fill = java.awt.GridBagConstraints.BOTH;
			constraintsJPanel1.weightx = 1.0;
			constraintsJPanel1.weighty = 1.0;
			constraintsJPanel1.insets = new java.awt.Insets(4, 4, 4, 4);
			getJDialogContentPane().add(getJPanel1(), constraintsJPanel1);

			java.awt.GridBagConstraints constraintsJPanel2 = new java.awt.GridBagConstraints();
			constraintsJPanel2.gridx = 0; constraintsJPanel2.gridy = 5;
			constraintsJPanel2.fill = java.awt.GridBagConstraints.BOTH;
			constraintsJPanel2.weightx = 1.0;
			constraintsJPanel2.weighty = 1.0;
			constraintsJPanel2.insets = new java.awt.Insets(4, 4, 4, 4);
			getJDialogContentPane().add(getJPanel2(), constraintsJPanel2);

			java.awt.GridBagConstraints constraintsJSeparator1 = new java.awt.GridBagConstraints();
			constraintsJSeparator1.gridx = 0; constraintsJSeparator1.gridy = 3;
			constraintsJSeparator1.ipadx = 500;
			constraintsJSeparator1.ipady = 2;
			constraintsJSeparator1.insets = new java.awt.Insets(4, 4, 4, 4);
			getJDialogContentPane().add(getJSeparator1(), constraintsJSeparator1);

			java.awt.GridBagConstraints constraintsJSeparator2 = new java.awt.GridBagConstraints();
			constraintsJSeparator2.gridx = 0; constraintsJSeparator2.gridy = 1;
			constraintsJSeparator2.ipadx = 500;
			constraintsJSeparator2.ipady = 2;
			constraintsJSeparator2.insets = new java.awt.Insets(4, 4, 4, 4);
			getJDialogContentPane().add(getJSeparator2(), constraintsJSeparator2);

			java.awt.GridBagConstraints constraintsJPanel = new java.awt.GridBagConstraints();
			constraintsJPanel.gridx = 0; constraintsJPanel.gridy = 4;
			constraintsJPanel.fill = java.awt.GridBagConstraints.BOTH;
			constraintsJPanel.weightx = 1.0;
			constraintsJPanel.weighty = 1.0;
			constraintsJPanel.insets = new java.awt.Insets(4, 4, 4, 4);
			getJDialogContentPane().add(getJPanel(), constraintsJPanel);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJDialogContentPane;
}
/**
 * Return the JLabel1 property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getJLabel1() {
	if (ivjJLabel1 == null) {
		try {
			ivjJLabel1 = new javax.swing.JLabel();
			ivjJLabel1.setName("JLabel1");
			ivjJLabel1.setText("Click \'Update\' to change current default settings to the recommended default settings");
			ivjJLabel1.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJLabel1;
}
/**
 * Return the JLabel2 property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getJLabel2() {
	if (ivjJLabel2 == null) {
		try {
			ivjJLabel2 = new javax.swing.JLabel();
			ivjJLabel2.setName("JLabel2");
			ivjJLabel2.setText("");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJLabel2;
}
/**
 * Return the JLabel5 property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getJLabel5() {
	if (ivjJLabel5 == null) {
		try {
			ivjJLabel5 = new javax.swing.JLabel();
			ivjJLabel5.setName("JLabel5");
			ivjJLabel5.setText("");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJLabel5;
}
/**
 * Return the JPanel property value.
 * @return javax.swing.JPanel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JPanel getJPanel() {
	if (ivjJPanel == null) {
		try {
			ivjJPanel = new javax.swing.JPanel();
			ivjJPanel.setName("JPanel");
			ivjJPanel.setLayout(new java.awt.GridBagLayout());

			java.awt.GridBagConstraints constraintsJLabel2 = new java.awt.GridBagConstraints();
			constraintsJLabel2.gridx = 1; constraintsJLabel2.gridy = 1;
			constraintsJLabel2.insets = new java.awt.Insets(4, 4, 4, 4);
			getJPanel().add(getJLabel2(), constraintsJLabel2);

			java.awt.GridBagConstraints constraintsCurrentLabel = new java.awt.GridBagConstraints();
			constraintsCurrentLabel.gridx = 0; constraintsCurrentLabel.gridy = 1;
			constraintsCurrentLabel.insets = new java.awt.Insets(4, 4, 4, 4);
			getJPanel().add(getCurrentLabel(), constraintsCurrentLabel);

			java.awt.GridBagConstraints constraintsRecommendedLabel = new java.awt.GridBagConstraints();
			constraintsRecommendedLabel.gridx = 0; constraintsRecommendedLabel.gridy = 2;
			constraintsRecommendedLabel.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintsRecommendedLabel.insets = new java.awt.Insets(4, 4, 4, 4);
			getJPanel().add(getRecommendedLabel(), constraintsRecommendedLabel);

			java.awt.GridBagConstraints constraintsJLabel5 = new java.awt.GridBagConstraints();
			constraintsJLabel5.gridx = 0; constraintsJLabel5.gridy = 0;
			constraintsJLabel5.insets = new java.awt.Insets(4, 4, 4, 4);
			getJPanel().add(getJLabel5(), constraintsJLabel5);
			// user code begin {1}
			getRecommendedLabel().setText("Recommended route(s) defaulted: " + totalRecDefaults);
			getCurrentLabel().setText("Current routes(s) defaulted: "+totalOrigDefaults);
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJPanel;
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

			java.awt.GridBagConstraints constraintsJLabel1 = new java.awt.GridBagConstraints();
			constraintsJLabel1.gridx = 0; constraintsJLabel1.gridy = 0;
			constraintsJLabel1.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintsJLabel1.insets = new java.awt.Insets(4, 4, 4, 4);
			getJPanel1().add(getJLabel1(), constraintsJLabel1);
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
 * Return the JPanel2 property value.
 * @return javax.swing.JPanel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JPanel getJPanel2() {
	if (ivjJPanel2 == null) {
		try {
			ivjJPanel2 = new javax.swing.JPanel();
			ivjJPanel2.setName("JPanel2");
			ivjJPanel2.setLayout(new java.awt.GridBagLayout());

			java.awt.GridBagConstraints constraintsJUpdateButton = new java.awt.GridBagConstraints();
			constraintsJUpdateButton.gridx = 0; constraintsJUpdateButton.gridy = 0;
			constraintsJUpdateButton.insets = new java.awt.Insets(4, 4, 4, 4);
			getJPanel2().add(getJUpdateButton(), constraintsJUpdateButton);

			java.awt.GridBagConstraints constraintsJCancelButton = new java.awt.GridBagConstraints();
			constraintsJCancelButton.gridx = 1; constraintsJCancelButton.gridy = 0;
			constraintsJCancelButton.insets = new java.awt.Insets(4, 4, 4, 4);
			getJPanel2().add(getJCancelButton(), constraintsJCancelButton);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJPanel2;
}
/**
 * Return the JScrollPane1 property value.
 * @return javax.swing.JScrollPane
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JScrollPane getJScrollPane1() {
	if (ivjJScrollPane1 == null) {
		try {
			ivjJScrollPane1 = new javax.swing.JScrollPane();
			ivjJScrollPane1.setName("JScrollPane1");
			ivjJScrollPane1.setVerticalScrollBarPolicy(javax.swing.JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
			ivjJScrollPane1.setHorizontalScrollBarPolicy(javax.swing.JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
			getJScrollPane1().setViewportView(getScrollPaneTable());
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJScrollPane1;
}
/**
 * Return the JSeparator1 property value.
 * @return javax.swing.JSeparator
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JSeparator getJSeparator1() {
	if (ivjJSeparator1 == null) {
		try {
			ivjJSeparator1 = new javax.swing.JSeparator();
			ivjJSeparator1.setName("JSeparator1");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJSeparator1;
}
/**
 * Return the JSeparator2 property value.
 * @return javax.swing.JSeparator
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JSeparator getJSeparator2() {
	if (ivjJSeparator2 == null) {
		try {
			ivjJSeparator2 = new javax.swing.JSeparator();
			ivjJSeparator2.setName("JSeparator2");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJSeparator2;
}
/**
 * Return the JUpdateButton property value.
 * @return javax.swing.JButton
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JButton getJUpdateButton() {
	if (ivjJUpdateButton == null) {
		try {
			ivjJUpdateButton = new javax.swing.JButton();
			ivjJUpdateButton.setName("JUpdateButton");
			ivjJUpdateButton.setText("Update ");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJUpdateButton;
}
/**
 * Insert the method's description here.
 * Creation date: (7/2/2002 12:40:55 PM)
 * @return java.util.Vector
 */
public java.util.Vector getRecommendedDefaults() {
	java.util.Vector d = new java.util.Vector();
	java.util.Vector allRows = ((DefaultRoutesTableModel)getScrollPaneTable().getModel()).getRows();
	for (int i=0; i<allRows.size(); i++) {
		d.addElement(((java.util.Vector)allRows.get(i)).get(DefaultRoutesTableModel.RECOMMENDED_DEFAULT));
	}


	return d;
	
}
/**
 * Return the JLabel4 property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getRecommendedLabel() {
	if (ivjRecommendedLabel == null) {
		try {
			ivjRecommendedLabel = new javax.swing.JLabel();
			ivjRecommendedLabel.setName("RecommendedLabel");
			ivjRecommendedLabel.setText("Recommended route(s) defaulted: 0");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjRecommendedLabel;
}
/**
 * Insert the method's description here.
 * Creation date: (7/2/2002 10:56:50 AM)
 */
public int getResponse() {
	return response;
	
	}
/**
 * Return the ScrollPaneTable property value.
 * @return javax.swing.JTable
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JTable getScrollPaneTable() {
	if (ivjScrollPaneTable == null) {
		try {
			ivjScrollPaneTable = new javax.swing.JTable();
			ivjScrollPaneTable.setName("ScrollPaneTable");
			getJScrollPane1().setColumnHeaderView(ivjScrollPaneTable.getTableHeader());
			getJScrollPane1().getViewport().setBackingStoreEnabled(true);
			ivjScrollPaneTable.setToolTipText("Edit recommended settings for customized changes");
			ivjScrollPaneTable.setBounds(0, 0, 200, 200);
			// user code begin {1}
			ivjScrollPaneTable.setModel(new DefaultRoutesTableModel());
			javax.swing.table.TableColumn column = null;
			for (int i = 0; i < 4; i++) {
    		column = ivjScrollPaneTable.getColumnModel().getColumn(i);
    		if (i == 0) {
        		column.setPreferredWidth(100); //sport column is bigger
    		} 
    		else if (i==1) {
	    		column.setPreferredWidth(20);
    		}
    		else if (i==3){
	    		column.setPreferredWidth(100);
    		}
    		else {
        		column.setPreferredWidth(50);
    		}
			}


			
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjScrollPaneTable;
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
 * Initializes connections
 * @exception java.lang.Exception The exception description.
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void initConnections() throws java.lang.Exception {
	// user code begin {1}
	// user code end
	getJUpdateButton().addActionListener(ivjEventHandler);
	getJCancelButton().addActionListener(ivjEventHandler);
}
/**
 * Initialize the class.
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void initialize() {
	try {
		// user code begin {1}
		// user code end
		setName("DefaultDialog");
		setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
		setSize(518, 393);
		setContentPane(getJDialogContentPane());
		initConnections();
	} catch (java.lang.Throwable ivjExc) {
		handleException(ivjExc);
	}
	// user code begin {2}
	// user code end
}
/**
 * Comment
 */
public void jCancelButton_ActionPerformed(java.awt.event.ActionEvent actionEvent) {
	response = PRESSED_CANCEL;
	dispose();

	return;
}
/**
 * Comment
 */
public void jUpdateButton_ActionPerformed(java.awt.event.ActionEvent actionEvent) {
	response = PRESSED_UPDATE;
	dispose();
	return;
}
/**
 * main entrypoint - starts the part when it is run as an application
 * @param args java.lang.String[]
 */
public static void main(java.lang.String[] args) {
	try {
		DefaultRoutesDialog aDefaultRoutesDialog;
		aDefaultRoutesDialog = new DefaultRoutesDialog();
		aDefaultRoutesDialog.setModal(true);
		aDefaultRoutesDialog.addWindowListener(new java.awt.event.WindowAdapter() {
			public void windowClosing(java.awt.event.WindowEvent e) {
				System.exit(0);
			};
		});
		aDefaultRoutesDialog.show();
		java.awt.Insets insets = aDefaultRoutesDialog.getInsets();
		aDefaultRoutesDialog.setSize(aDefaultRoutesDialog.getWidth() + insets.left + insets.right, aDefaultRoutesDialog.getHeight() + insets.top + insets.bottom);
		aDefaultRoutesDialog.setVisible(true);
	} catch (Throwable exception) {
		System.err.println("Exception occurred in main() of javax.swing.JDialog");
		com.cannontech.clientutils.CTILogger.error( exception.getMessage(), exception );;
	}
}
}
