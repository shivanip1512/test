package com.cannontech.dbeditor.wizard.copy.device;

import com.cannontech.database.db.device.*;
import com.cannontech.database.data.*;
import com.cannontech.database.data.device.*;

/**
 * This type was created in VisualAge.
 */
import java.awt.Dimension;

import javax.swing.JLabel;

import com.cannontech.database.db.*;
import com.cannontech.database.data.device.*;
import com.cannontech.database.data.capcontrol.CapBank;
import com.cannontech.database.data.capcontrol.CapBankController;
import com.cannontech.database.data.capcontrol.CapBankController6510;
import com.cannontech.database.data.capcontrol.ICapBankController;
import com.cannontech.common.gui.util.DataInputPanel;
 
public class DeviceCopyNameAddressPanel extends com.cannontech.common.gui.util.DataInputPanel implements java.awt.event.ItemListener, javax.swing.event.CaretListener {
	private javax.swing.JTextField ivjAddressTextField = null;
	private javax.swing.JLabel ivjNameLabel = null;
	private javax.swing.JTextField ivjNameTextField = null;
	private javax.swing.JLabel ivjPhysicalAddressLabel = null;
	private javax.swing.JCheckBox ivjPointCopyCheckBox = null;
	private javax.swing.JLabel ivjJLabelMeterNumber = null;
	private javax.swing.JPanel ivjJPanelCopyDevice = null;
	private javax.swing.JTextField ivjJTextFieldMeterNumber = null;
   	private int deviceType = 0;
   	private JLabel jLabelRange = null;

	class IvjEventHandler implements java.awt.event.ItemListener, javax.swing.event.CaretListener 
	{
		public void caretUpdate(javax.swing.event.CaretEvent e) 
		{
			if (e.getSource() == DeviceCopyNameAddressPanel.this.getNameTextField()) 
				connEtoC1(e);
			if (e.getSource() == DeviceCopyNameAddressPanel.this.getAddressTextField()) 
				connEtoC2(e);
			if (e.getSource() == DeviceCopyNameAddressPanel.this.getJTextFieldMeterNumber()) 
				connEtoC4(e);
			if (e.getSource() == DeviceCopyNameAddressPanel.this.getJTextFieldPhoneNumber()) 
				connEtoC5(e);
		};
	
		public void itemStateChanged(java.awt.event.ItemEvent e) {
			if (e.getSource() == DeviceCopyNameAddressPanel.this.getPointCopyCheckBox()) 
				connEtoC3(e);
		};
	};

	IvjEventHandler ivjEventHandler = new IvjEventHandler();
	private javax.swing.JLabel ivjJLabelPhoneNumber = null;
	private javax.swing.JTextField ivjJTextFieldPhoneNumber = null;
	/**
	 * Constructor
	 */
	/* WARNING: THIS METHOD WILL BE REGENERATED. */
	public DeviceCopyNameAddressPanel() {
		super();
		initialize();
	}
	/**
	 * Method to handle events for the CaretListener interface.
	 * @param e javax.swing.event.CaretEvent
	 */
	/* WARNING: THIS METHOD WILL BE REGENERATED. */
	public void caretUpdate(javax.swing.event.CaretEvent e) {
		// user code begin {1}
		// user code end
		if (e.getSource() == getNameTextField()) 
			connEtoC1(e);
		if (e.getSource() == getAddressTextField()) 
			connEtoC2(e);
		// user code begin {2}
		// user code end
	}
	
	
	/**
	 * connEtoC1:  (NameTextField.caret.caretUpdate(javax.swing.event.CaretEvent) --> DeviceCopyNameAddressPanel.fireInputUpdate()V)
	 * @param arg1 javax.swing.event.CaretEvent
	 */
	/* WARNING: THIS METHOD WILL BE REGENERATED. */
	private void connEtoC1(javax.swing.event.CaretEvent arg1) {
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
	 * connEtoC2:  (AddressTextField.caret.caretUpdate(javax.swing.event.CaretEvent) --> DeviceCopyNameAddressPanel.fireInputUpdate()V)
	 * @param arg1 javax.swing.event.CaretEvent
	 */
	/* WARNING: THIS METHOD WILL BE REGENERATED. */
	private void connEtoC2(javax.swing.event.CaretEvent arg1) {
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
	 * connEtoC3:  (PointCopyCheckBox.item.itemStateChanged(java.awt.event.ItemEvent) --> DeviceCopyNameAddressPanel.pointCopyCheckBox_ItemStateChanged(Ljava.awt.event.ItemEvent;)V)
	 * @param arg1 java.awt.event.ItemEvent
	 */
	/* WARNING: THIS METHOD WILL BE REGENERATED. */
	private void connEtoC3(java.awt.event.ItemEvent arg1) {
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
 * connEtoC4:  (JTextFieldMeterNumber.caret.caretUpdate(javax.swing.event.CaretEvent) --> DeviceCopyNameAddressPanel.fireInputUpdate()V)
 * @param arg1 javax.swing.event.CaretEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC4(javax.swing.event.CaretEvent arg1) {
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
 * connEtoC5:  (JTextFieldPhoneNumber.caret.caretUpdate(javax.swing.event.CaretEvent) --> DeviceCopyNameAddressPanel.fireInputUpdate()V)
 * @param arg1 javax.swing.event.CaretEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC5(javax.swing.event.CaretEvent arg1) {
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
	 * Insert the method's description here.
	 * Creation date: (2/23/00 10:40:51 AM)
	 * @return boolean
	 */
	public boolean copyPointsIsChecked() {
		return getPointCopyCheckBox().isSelected();
	}
	
	
	/**
	 * Return the AddressTextField property value.
	 * @return javax.swing.JTextField
	 */
	/* WARNING: THIS METHOD WILL BE REGENERATED. */
	private javax.swing.JTextField getAddressTextField() {
		if (ivjAddressTextField == null) {
			try {
				ivjAddressTextField = new javax.swing.JTextField();
				ivjAddressTextField.setName("AddressTextField");
				ivjAddressTextField.setFont(new java.awt.Font("sansserif", 0, 14));
				ivjAddressTextField.setColumns(6);
				// user code begin {1}
				ivjAddressTextField.setDocument( new com.cannontech.common.gui.unchanging.LongRangeDocument(-9999999999L, 9999999999L) );
				// user code end
			} catch (java.lang.Throwable ivjExc) {
				// user code begin {2}
				// user code end
				handleException(ivjExc);
			}
		}
		return ivjAddressTextField;
	}
	
	
	/**
	 * 
	 */
	/* WARNING: THIS METHOD WILL BE REGENERATED. */
	private static void getBuilderData() {
/*V1.1
**start of data**
	D0CB838494G88G88GC5F90EB0GGGGGGGGGGGG8CGGGE2F5E9ECE4E5F2A0E4E1F4E14DBC8DD8D4571564F7C9C21498CDC82A2DB5B65146E4492EE97D2A5D352D6DE79AED6CE237EE16F6E9572F4DB6E9176D3A795856EFF31999FECCD091C80B46B428080063B09053D2C08302BF18C9C20460B4A1A90F19870C8EB35319078C0AE64F39FF6F4D70862ADF53AFFE5F613DFB4E3D671CFB6E39675EFB6E9BC959AB7373F2ADE5046466917E6FF73904CC1CA4A4ED43036F8ADCE1FF0615987E6D
	8158C17EEE3F8D1E65C0DBF2FF0635185C76EEA1146DD076FEBA437AEBF8AFA5F54576237012C00E8DE839360BF32616F35FD1A1270016EBACC5705C866084DC8F407CFE145FE6A96778A8143771F704DC8BA1438A56D9DCD7EE40EF6475DA005685D0B400F57AAC3BB9BD9DF0D8E7B5BCB395D6F793BC37D06DEEEAEA64757A6ED8C4B6FD73B7162DA84B40DFC04FGEB9B654F8AF2BFB50415FC77529BA83F6B7C50D1CFF04461D18201E0C0D3BD438E6F006A75E94130E34CF7D189FB9D1EE0E85C61D5AF78BC
	EAD3D387FD36836AADE5C4ED73FA43EAA4522584D4BFF90A7661093AC24A9F7AGE54D033813CE341B9D5EBBG7ABC229F5F5E5A42751CFDFE91493AEFC742FEAC73B07DB3BDFABF16FEA65D1A62C57EB751F141F63B215C8340F4D9EB913A2C738ADD34DA2CDB28E6D8ABG9AD501EFBE0878AEA88F83A429C247A387040E6ECBDFA4B983ED166D89F4CCD319EEABD5DD47B55665361D4C17766278EFC63903995643GEE00A8C09A40CB54E7FFF234961EED83DAFBF0E4A498E88B050E793D61B703DE558F6FCD
	CDC06138B32AA7987692427CF2FD1D5DC49F81690331BE09407AF61688BFDAF266E122E93F33641974ED69F7AAAC2BB80F8D03FA5F4A0750EE4B695866G9E5B3D007305629F23783EA19D4F466ACD0A477284503206041D5D5B04AEAE5B82D2DC5FEEA9B051A56D2B0D160EE126C35F302E8B337165CA34F1B26097822CGD88F3089204CA7ECFC7875D72733F1A74C03F0C071F70F8E041430F6EC5467772AE1EE6B5BACD3595A8CD83BFB2E3A9D334B22D70DE314797747CC6D123968004569E376B865536D42
	E47FD3C259433E493E31CD0E61D3F42ECC785898DAA57E67941FE24033313DC871D81E8BB4F2AE434A6A7FAD1DDB5EF371BE72E50A3F71DC3C0F349D94BE3290E80567B32CECFCDF2A42786807F2B240FC00E5G9BGB681947A4598FF74FA5D1FB90F3A8C634B5AAE597BABF82AD1AD2CF486A21A9270283DCA58278C78558861F59E5D0BF69D08E977A5BA3FBDA063249AD0430A668B8600BB84C9F026882B73353DBB30CED825549E5F088A82C6C2C470FDFCEFA976CF09E86F063C0A266AB598BDE3AF7A4485
	2597B882831CB5FE345753B466D643FB07DF586A5481444D0572D2E91737761540738DF0EE558F215C67C1B514703870512FB8E121A42B213EEBA4433A8620ABG768D08B97AB33770450E2D8F917BF75ACCFD9121BF48571DE03CAFBEC1E54C877CCA00ACG9202189892EBEC3753BD4D3CC07D44BB6AE824398CE554DB3CF771F7A643BF2D8CEB5C7CB552728C6FAF444DD7FCB27BFF219AFD38957A5587B09192E3F04B3E8A661B471408AA3C716DB0B8126A55C704553D3992579A75029A50BA54109A7042CB
	047B426D15DBF4BF9D8CCA9F3A1362B1EE3503A3C5A47E1E4A9D8C8FBC0CDEAF681F25F4C553423E01D1CD6DD6B54D9798525BAF263AC4E2C2E20477F5D98D76F5B674F189408A5957067D2586FFF3850367C7C3BDB813385F7E87DD1F57C17D2764787F2C9677DCC5FF92659CDA5669C935530D9D363D1AB8EB922B2715E13DC67E3A0CC78F16F1DF32F21FA960BE4468DF23F4A774458D10843088E01D344B4903EE03AEF90676D3495EF9D5BA76CB99C24FADF1BE1F286D1FA30379487E2A44BEEE2FB2777106
	AAF39FEF2C1A5A07DB2B4CFC1851CED6E19F03E381431CF0D5E13C7484838115CECC59045B63E7BD588FDF40271D75F907D42D5BF7D105D897C65F36B85137F3E03C0A42A2FE0E6D9371337262E232720F3BCD77DBE91FEB36F843BCFE06757849F4FA73G4EB5552F0E00215A0323810D613BE93C67780815623E7F4E98362D6E8A7A30EEE83CFDD8751CBF960C5AFD1ABA6240BF5D9A2CB36D43CAE0C875D61FD3AEA88EE5CCF35061F0F4827DB83E55D49CBAF434A271565FBE688B2B1D0150A8DF372AEBFAEB
	9A885321F6BCC2F540B6BDB014A7FC2A5FEB77C09C529C74AFEF0232230E48988C98975F0E54EB16DF4F650F3F42EC406B7FD5D590B6F8F06C92556134147EA66E8A5E9A9D99D043FF85C5DE65FA2C9F0D5723EBB898D07FEAFA9C666B4553F41F18863E3D84E0D5D82C97CF9CC03AD8AF7822589E747B597483DAF58467162D3E4CB0DFB2436CAC3ECD521D53507B2621A7CFC3DF1A104E7A31223A98630C7A27D195360F38603D210E0BBDE0F5C99C2DD7710F6AFB4B472BB13E7A8DEBA5437FC30D793CDFD503
	6B63F949FFF5CDBE41BDA2634962D43A02BCC3E15F88ECADF07167B989E2B566A4C250B74CDF2443F395GEB7BF6AD6E0759B8CFBA72131006FCF28E0A3CC5A6345B84D026B1DEDD9A338B2B375CAE6A2D0072FA00CD26752E9C92751C239956D323096AB5B8C43DE1281384B0FF542C5E7F48FA19484B24DE59852C3753A97285F5926724387F04C5ED1ECD1C495EACB666DF3C114326F19C41FD017177DF08BF2BD65811EF8A6067DE907EFF2C5638DFEA0BC4029E9FBDAAF09F782FDAE30CE738E7EB1F07A7CC
	36881CDB3DEAD4785166DA54954F929C4DFD614103C748643351F11A71915B5176B1BA45FB026DF01CB9852E58AE1740DE1F0A711459617289E37EE645769A4B8B896C104E7B3F4EE007D46BF29B53691EA6BC131B6522BCD4C94750284FB12A79705022E88A0DAA461C53B9EBA16FDF711848675C150F736594147B8122G6CEC3947DE446B2682EEB5G1B5359AD46B9CD220087F081305819ECC71F384AF5135BFCD5CD899B233800A86C382034154BDD7370825272517326B926646DE5167EA833DFF2D43763
	6C1FAC36AD82E07D5BA87B379E6A940D0B7EBD780E31FF9D632CFF7EF1513FB78EE17F2EF28D606775878E8919C940EF1914F9EF29194C6CF1B1966FBBC4BB5B458CEB4BC5512E204C2C1DF6D1687AD547356B7A1BBAA1B399782D14B2BFB7E6A6B36722583B1DEDB04E7915D9558967FC51A5BEB61762673C4D01FE5A82782ECBE24E0F9D5CE11873E7E01601CFC154177BE3BB769962BBAF5758D1AE0D6FC41CB55B6AD0F7AF70CC82C883C887D8A7E5245B274DF99D0FCE11738ADA6373C7591774B316EDE272
	F90B65F71EAFC0DB17CF44670F4AA83EFAA2BEFFB4B7DF640F9A01B6F7C21CF58E586365134B31B2F74715D93B57A635F34415DB635A3179FA3477A5910F8CAE5554F4DA6C1F797A79C601F17DF4DFE6675889F826DE96B96883C78A39BE0BAE0B5C4C5DCEDC0F32203C99206CCA0655895083908218FDC50C5BF3F633D89DBBEE098235FE3EE7367849B55AB0E7DA9BAE3DE2E6437D553B4DEDA8771E4CBEBFFCC758E74D95B15E8F9D3AB6DD17CFEA972F2B6D2358F6CC07EF178A9DF6FFA4F4D09C5726C3F0D2
	3BF89DFACCF5B8DAA6F470CB9DAAA765757FBC9D9AA6350B57E121298E19E3C207959F89DF7DEEBD62B6C15916A47C326F905E530818C5CF17C4442BAD8E5CFF86433EA1DFC06036D11CA7982607EB066B2CCBFC6E6F2AB3BF777756191F7B7B6A264EEDF96B926585D89E6AE63AFFA8037ED583B01CA78FF1AEA8FB1344F8949760DEBBD916F751B22E932C3CA0FF3A3C513502AEF37947A683636E4B7479582F36D3FBC6A2EA0486985CF5F15A0F359709C9CE840F861CC7A979503E0557B92E855BF16DE1B161
	3FEBB0DE1A44843A19E3BEF8C301584BADCC92FB396C9D38AEE4C2F993C051F5429676A3E6779A1557B15FEE04276B3AD8BF6739CEE7E25FDB64B4773D071D68731160E85823467B57234EF2C953A3B0EE8351AC7C7CC65BEBCAF8C81D641FDF276D99ADD17BEFB99379B033791EC3C61B5793918BFE7C0E51C67DF17650624A495709BD564698FE8D125FBF179A794D0DEB1F9ED7DEA57925B90C7C8ECBFE6DE5C6FE9B625ABF93D7A6578BFE6F5799793DA6797DE04C482F6C7A587655F165166BC5CC4B2C473D
	C8D40EC996AD0FCB7B4E33E28C73AB6341D188F6AB2E27F9GF9FE6E0272B0C0AA40426B59BE00797CA3E3F8A69A8CABA3B0DC5DA155C352EE20F7E5F9222D8D4A65G0DGEE00688D220FBFAF40BC821B9D18E3E127F0564F6402F85F4F3AC16F5B269B926D49D2CA687D770DEC6FD5AA711F2678C6831E6D491E25F38F4B43C0335DA462E3BB5D574C07F23A44ADBAA2E2668A09DB5B0438221BB32C95B78B5C7F51B57FB014BBA42E38D134F5CB5C6BF4DDDE846595927753D251F61D443DDDA25AEE1438FBDB
	C45B43FFA3F06F51B5769416A5EEED0DE86B1738D7DC08DB8165F59217C357460DD05EA4F1AF350A3605498237131E515DD076A70BF3CE26D5FC1F108438E54942B71E78A32E31B29E6291322969E45867F5F1B7636BF2252D10374FCE96B1710C0D6E21EF4930368238EF916714E815B813AF39FF8168F64C741B0A2C1E9ACB689666A333EF514FA9D9F0A6E7B2676789196B00DEF8AB623E15E7BCA3210F5E114B76960CCE720DF40CD77B08115E90C3C7FD3FECB1523DB1F434514B3CBD6B5B637BC55FB6BF1E
	CE7C0FBA93FEAF52F5AB6B13461FE9EBF7114D73BFEFE3E374993A46AC815A4ADB4538DD5AA1FD68D6313E4CAA29C45DEE835F85E885688308822481AC83488458FC1B283F10562FC8013AA9AC3FC5F3D7F03E6CCFE17929249416FFE275B76FC805A77220FD3ACD1F332DDCE6FC3F7ACCF0822286DA9AFFAF1C223E99AC13FD59D7124A47F09B1F7F22CE81093D83B50EE7A23E46BB232D867F399A5D3693910B5FBB027BAE8D966979A9785DD67D6F51BBEC3763B87E63110EA9F8D839AED68E9B21BD420B5C0F
	F2D2583EB7D74A5A58047DD583180A7512FEAA6317CDE2EE38EE97317AE4A96AB4988CE82C4E01B3D8E7A13C2783B05D9A9A2752AD4F2097556DF6DDB72465C0F9B45728DBEFA96A66897AC7C782912E5B880D0F58875B8C70BB0A7B950DFBE5D02E18A1F499AC19CE9723BEBDB3E2F5991E216B42E4FC0D46DB3A4719A1ECF2E7F32CCDEE20FA5C8874D42947461A6974A8B06831AACE0F8DB362ED72DB57E41B3C69923AED143AEDF4456A36A7CA4FB9C0A7B365F835CE275B9603EE07E7466AF6EAE63CEE4E5A
	493A1D2C953A754C94F36F071372AD5381EB17FD0D6DEAECD75B0E6D99AA7368F7BDB3591EA1D7628FD1FC4800E76B205BA656418920E54D94E74C32A65C8BF524023F82C481D281528156240A7D195A60A45364C2FA02A197EC6A7D06A3A06B5F67722E6D5B2B2F940875E66917BECF165D1C1D1BA83F1D154A62AC3903BD1DEBF6D1BC6B5F834D58BFA750DCGA100E40039GCB6F907DFB28414E7A072978B6CDD3BC43B44BAFBA0709F79D0D9BC966D3DFA8183CAFDD375D1817DA8DB23281EC3338EE33304E
	2C9D224E6ED9135788467B73DE34831EC1DC4AGDE77F07CD98D57692E6171084F2378790D902D4268CFF7EF656DE432C736ED5903FB5A785BD8C6CB3F0C6B4D245BEA59B67A8152CFD3C9069BD4C95FFB9E671E69AD2B2CB3512973B05CFECA7AF30331BCE26B30DC7177CA0C7B1468AC16AB1EBBCB662F4B4D686912DE53ECC6DFA56997DDE6748D33449E6F09770D396862FB4BADE21E450F63B3FCECF3E669FB3CD788DB4F8B097E0F2D235F44F4A3777D9F94895F5F7D60FD24633B59397B9248305F49F8CF
	5C294B48DC104E63ECC9B34642C1459FD1A56F79E3C67D9B1FF7A44C25AF65BCD773276D39DDDC576F3593B9E656467D708C28E39572A6668484766A3D91A5900928E15F60DEFB0553DED1FF38866ACD6EFF766B0CA7B92E733C31943626243EC158662113BA6DD849DFC65E3AD3BA4FA7DB307F3F15E361FFC3277D226AAFA3EF26CB6769F5454ABB7F0ECE5B567A1711B7630C4E7343DAA10F4564DA7ABDBEFBFF55707E216D634AEF696B5D5847AA034573171B926718AA1B4CF3CC35CD6679CDE753545FB5B5
	B6A57E2E6975A663F7CDECBD7A309B638B191DE15D8D508A5083B0BCDB2CC5EB1B76923A96192CC3473DC3186342AFE3B5A1BF3DB9F1DF3F58BC7D7734AB1B718EFBA028E9419164AF705FE846715202A10A645FC7B4230D423E21E10DD56578EFB7237F7955C10AE6FB577A7E18BB887DFCFDCC99E2C7ECF656DDD2AC7673B74E96676BA70BF12F32824A6B811E19AD6EF3166D9079C95B9C910FD7FFE0BC17D69C7CE52E5939B46703AACB4B9C96477A676831B29DE2A54F0F8EA6480F52550A49AF13F7953367
	881D0E164BBB94096BEF96B85BDD827708CB60DA6E92FB2F15BDC45AD0E941F95F2B1CB37DAA60B428747640B9552375A8C352171EFE9777489A70CB85D88A3086A08BA0872068EEB8F3G9C86F081F88122GA9G73815281D681EC38DB785E7623B509F4GA49FBC6A00E1055DBBB0BD5EB90BFDFB8678942689DE3DC78FCE412BAB9C8C29E1ED1CE169D7B85C8E035D097DF924FBFAFF3E50CD3F53GBF6C998F717C6D25EEFAAFB2B898D1653CC96AAE16384EC8D7B062433CAF0BD10F94DF5B1E7CAD1A2F2B
	CE0B5F131F257843E971FB722CE231A7EF855A42B4F18679728E237C42FBA0D65CB3F5AE010D43381B6EF5212E8BA084108CB097E069BDE2DCCAFA2A279897B6B375A1E67A6C0C49BB2F663AE472E73609EEBC3FDCEEECE73B1755AB67CF7B3D095A294D46F62D3CDE8FFF8EA7EC778517315D842F174A1F8BCD5A713B5EF7937BDCFE6F74BE373D57BC063E58BBB9066E6AB50F21953D46984A3FBF684558B484734EAD6B55CA5C9FA42E0E72C45CD91FD79B16785728DE08BF2522C001EF26FA0DD11CDBFC1FEB
	407D01630E77623C01AE8F87655D6169DEFAF677797D7CF78D340EAFG53C85A231B6A688BA9863D5D92276B5DFB9647C02E2DAC8CF05A87E74B887B3E572778FD976927D85DD0B1B044463A8717E5BE6E9F985EFFD47C716FE11E7BD8E5301C68FA3A56AD831F45BCAD437D8B452D839C66FE191D2EF7A7765DDB5C537B6EAC37396F2639A77B6EBC37396F5E67B67AAE5355A96F8A1E39D764F9ADF4DDA41F0277CF895CD033487D6E1638774AC5DB3F446D320B36C992F749AE5A26C85CC43338E7481438EC3A
	BF4E06B219ABF06F660A36C5923756A56E997AA46E5F2A919702F21244F548FB0B143962BEC129AA527D15CEC3065F9D5D2A6345B46479278F280DB20147D3F3C5EEECE7F3ECFE3564BD6CB64B09DDE91EAE3F9A13430C4B2F164F4B300AF3A44F89D2BBD7825E8940643D763EAEEF16FDBAF9F9D3486B871ECA0C3C45EC1F887868BC515F025C587E3A7BF4797E5C696497CCA1FF51BC76FBE4DD7EAF6878AE857CCAA97F596AD8794E1D3A7C77274C0F0A5FE4A4121F8DB244DE14711E0F53051F491E257BFC13
	F31E977CA4B8E4B8643136FB70BA063FDBAAF4BE5B6CD747E77F9E1D4FF57BF4BE9B495571D9D82973A187F4BE9955D747E79FEEC7887B5D7840DECCE4AF79A94953488B18052A207928C8A4CC36CBEE1DF429493A70EA13755F6F33DA082D1364337055C9EC9803BAC99E8DD11D448A61AB0ABF714323C895E17FEC40EDEB0CEEDF7F6CEBBFDA737D5E081564F7121D18166E06F3B62EBA2A973F3E60BF79BBC13604D09D7C2589080A50C7DEC49B772BAED2B6F886BFAB0FE82A37CD2CE7910D3C68C2C9F5787D
	EC5DA9A5B22CE16CEDF691DBGFF1CC8CA98592F84066A586256CC754DED6B273A221B915037146A0BDC9C12CB69726D7B771C5E384CCAACA748737E2062BD21F8E0AB57C936E843E1B5B29C74FB232157DBA923C2FA03CAEFDF005DCB327B58D987665B2452672373D236BE35750485FFGC36AE8CD1C9D8ED9435BBE2C04C1149A6E5470471641F02524DD779B2DEA6D49DF77D9C8AE6F641F489512978DE920D5D889C4860361915020A2CE83C1120C9CF7BE661C3378976FD1C6FAD30D3C42CC86CD6B065460
	C8DD5B6020AF20CA86E0BFE07F32E40FF59CF1F5CA22F73CF87ABB4F8D40F8F7906B0853C97F56533F8D78371EE26AA9263E810C18CD267C97CF0FC63057DBB466A452FBF1189DA7E02434A7FF7A5802FFED49FE86FAF70264518FCA415845FAAD16743CF9491C17DF181DF59B0EC588DDC2704D9FD4C6FC7EF1B02A4C8AC5113413C94608A37E4B857C7AF99745DAA7B996E28BA6FB8822DF20442D74EF71D5779FDB155036E5A890DFFEBDC9598ADDD9717FBF2032F91FC8E58F684A5A8C4A5ABE114ABA4CADEB
	53C8917B0C25826F871AB4D2486F8260F5ABCD58D7B04743B2CD3E0B72A7231B79EC8D21545F6BFD44CD00086E3BBFF91A3646CECA21E941A7CF53133A267986CD73BFF91A3EE5EE53FC16932C7948F84711F3537604F79C5977733B0D8762FFAF30388ABB548A783E87C46E64FF779FA209F3F45DA30A5F2FA97E73ED81AF666B0E0DEB2A1EA7FE388A79C7C49DCE645FB0525F541EB33479FFD0CB8788E04BB4696097GG24C7GGD0CB818294G94G88G88GC5F90EB0E04BB4696097GG24C7GG8CG
	GGGGGGGGGGGGGGGGE2F5E9ECE4E5F2A0E4E1F4E1D0CB8586GGGG81G81GBAGGG9A98GGGG
**end of data**/
}
	/**
	 * Return the JLabelMeterNumber property value.
	 * @return javax.swing.JLabel
	 */
	/* WARNING: THIS METHOD WILL BE REGENERATED. */
	private javax.swing.JLabel getJLabelMeterNumber() {
		if (ivjJLabelMeterNumber == null) {
			try {
				ivjJLabelMeterNumber = new javax.swing.JLabel();
				ivjJLabelMeterNumber.setName("JLabelMeterNumber");
				ivjJLabelMeterNumber.setFont(new java.awt.Font("dialog", 0, 14));
				ivjJLabelMeterNumber.setText("Meter Number:");
				// user code begin {1}
				// user code end
			} catch (java.lang.Throwable ivjExc) {
				// user code begin {2}
				// user code end
				handleException(ivjExc);
			}
		}
		return ivjJLabelMeterNumber;
	}
/**
 * Return the JLabelPhoneNumber property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getJLabelPhoneNumber() {
	if (ivjJLabelPhoneNumber == null) {
		try {
			ivjJLabelPhoneNumber = new javax.swing.JLabel();
			ivjJLabelPhoneNumber.setName("JLabelPhoneNumber");
			ivjJLabelPhoneNumber.setFont(new java.awt.Font("dialog", 0, 14));
			ivjJLabelPhoneNumber.setText("Phone Number:");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJLabelPhoneNumber;
}
	/**
	 * Return the JPanel1 property value.
	 * @return javax.swing.JPanel
	 */
	/* WARNING: THIS METHOD WILL BE REGENERATED. */
	private javax.swing.JPanel getJPanelCopyDevice() {
	if (ivjJPanelCopyDevice == null) {
		try {
			ivjJPanelCopyDevice = new javax.swing.JPanel();
			ivjJPanelCopyDevice.setName("JPanelCopyDevice");
			ivjJPanelCopyDevice.setLayout(new java.awt.GridBagLayout());

			java.awt.GridBagConstraints constraintsNameLabel = new java.awt.GridBagConstraints();
			constraintsNameLabel.gridx = 1; constraintsNameLabel.gridy = 1;
			constraintsNameLabel.ipadx = 17;
			constraintsNameLabel.insets = new java.awt.Insets(28, 38, 7, 0);
			getJPanelCopyDevice().add(getNameLabel(), constraintsNameLabel);

			java.awt.GridBagConstraints constraintsPhysicalAddressLabel = new java.awt.GridBagConstraints();
			constraintsPhysicalAddressLabel.gridx = 1; constraintsPhysicalAddressLabel.gridy = 2;
			constraintsPhysicalAddressLabel.ipadx = 48;
			constraintsPhysicalAddressLabel.insets = new java.awt.Insets(7, 38, 5, 0);
			getJPanelCopyDevice().add(getPhysicalAddressLabel(), constraintsPhysicalAddressLabel);

			java.awt.GridBagConstraints constraintsNameTextField = new java.awt.GridBagConstraints();
			constraintsNameTextField.gridx = 2; constraintsNameTextField.gridy = 1;
			constraintsNameTextField.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintsNameTextField.weightx = 1.0;
			constraintsNameTextField.ipadx = 186;
			constraintsNameTextField.insets = new java.awt.Insets(26, 0, 5, 18);
			getJPanelCopyDevice().add(getNameTextField(), constraintsNameTextField);

			java.awt.GridBagConstraints constraintsAddressTextField = new java.awt.GridBagConstraints();
			constraintsAddressTextField.gridx = 2; constraintsAddressTextField.gridy = 2;
			constraintsAddressTextField.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintsAddressTextField.weightx = 1.0;
			constraintsAddressTextField.ipadx = 186;
			constraintsAddressTextField.insets = new java.awt.Insets(5, 0, 3, 18);
			getJPanelCopyDevice().add(getAddressTextField(), constraintsAddressTextField);

			java.awt.GridBagConstraints constraintsPointCopyCheckBox = new java.awt.GridBagConstraints();
			constraintsPointCopyCheckBox.gridx = 1; constraintsPointCopyCheckBox.gridy = 5;
			constraintsPointCopyCheckBox.ipadx = 12;
			constraintsPointCopyCheckBox.insets = new java.awt.Insets(4, 38, 28, 0);
			getJPanelCopyDevice().add(getPointCopyCheckBox(), constraintsPointCopyCheckBox);

			java.awt.GridBagConstraints constraintsJLabelMeterNumber = new java.awt.GridBagConstraints();
			constraintsJLabelMeterNumber.gridx = 1; constraintsJLabelMeterNumber.gridy = 3;
			constraintsJLabelMeterNumber.ipadx = 11;
			constraintsJLabelMeterNumber.insets = new java.awt.Insets(6, 38, 6, 0);
			getJPanelCopyDevice().add(getJLabelMeterNumber(), constraintsJLabelMeterNumber);

			java.awt.GridBagConstraints constraintsJTextFieldMeterNumber = new java.awt.GridBagConstraints();
			constraintsJTextFieldMeterNumber.gridx = 2; constraintsJTextFieldMeterNumber.gridy = 3;
			constraintsJTextFieldMeterNumber.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintsJTextFieldMeterNumber.weightx = 1.0;
			constraintsJTextFieldMeterNumber.ipadx = 186;
			constraintsJTextFieldMeterNumber.insets = new java.awt.Insets(4, 0, 4, 18);
			getJPanelCopyDevice().add(getJTextFieldMeterNumber(), constraintsJTextFieldMeterNumber);

			java.awt.GridBagConstraints constraintsJLabelPhoneNumber = new java.awt.GridBagConstraints();
			constraintsJLabelPhoneNumber.gridx = 1; constraintsJLabelPhoneNumber.gridy = 4;
			constraintsJLabelPhoneNumber.ipadx = 6;
			constraintsJLabelPhoneNumber.insets = new java.awt.Insets(6, 38, 5, 0);
			getJPanelCopyDevice().add(getJLabelPhoneNumber(), constraintsJLabelPhoneNumber);

			java.awt.GridBagConstraints constraintsJTextFieldPhoneNumber = new java.awt.GridBagConstraints();
			constraintsJTextFieldPhoneNumber.gridx = 2; constraintsJTextFieldPhoneNumber.gridy = 4;
			constraintsJTextFieldPhoneNumber.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintsJTextFieldPhoneNumber.weightx = 1.0;
			constraintsJTextFieldPhoneNumber.ipadx = 186;
			constraintsJTextFieldPhoneNumber.insets = new java.awt.Insets(4, 0, 3, 18);
			getJPanelCopyDevice().add(getJTextFieldPhoneNumber(), constraintsJTextFieldPhoneNumber);
			// user code begin {1}
				
	         java.awt.GridBagConstraints cpg = new java.awt.GridBagConstraints();
	         cpg.gridx = 1;
	         cpg.gridy = 5;
	         cpg.anchor = java.awt.GridBagConstraints.WEST;
	         cpg.fill = java.awt.GridBagConstraints.HORIZONTAL;
	         cpg.gridwidth = 2;
	         cpg.insets = new java.awt.Insets(10, 15, 10, 15);
	         getJPanelCopyDevice().add(getJLabelRange(), cpg);
				
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJPanelCopyDevice;
}
	/**
	 * Return the JTextFieldMeterNumber property value.
	 * @return javax.swing.JTextField
	 */
	/* WARNING: THIS METHOD WILL BE REGENERATED. */
	private javax.swing.JTextField getJTextFieldMeterNumber() {
		if (ivjJTextFieldMeterNumber == null) {
			try {
				ivjJTextFieldMeterNumber = new javax.swing.JTextField();
				ivjJTextFieldMeterNumber.setName("JTextFieldMeterNumber");
				ivjJTextFieldMeterNumber.setFont(new java.awt.Font("sansserif", 0, 14));
				ivjJTextFieldMeterNumber.setColumns(6);
				// user code begin {1}
				ivjJTextFieldMeterNumber.setDocument(
					new com.cannontech.common.gui.util.TextFieldDocument(com.cannontech.common.gui.util.TextFieldDocument.MAX_METER_NUMBER_LENGTH));
				// user code end
			} catch (java.lang.Throwable ivjExc) {
				// user code begin {2}
				// user code end
				handleException(ivjExc);
			}
		}
		return ivjJTextFieldMeterNumber;
	}
/**
 * Return the JTextFieldPhoneNumber property value.
 * @return javax.swing.JTextField
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JTextField getJTextFieldPhoneNumber() {
	if (ivjJTextFieldPhoneNumber == null) {
		try {
			ivjJTextFieldPhoneNumber = new javax.swing.JTextField();
			ivjJTextFieldPhoneNumber.setName("JTextFieldPhoneNumber");
			ivjJTextFieldPhoneNumber.setFont(new java.awt.Font("sansserif", 0, 14));
			ivjJTextFieldPhoneNumber.setColumns(6);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJTextFieldPhoneNumber;
}
	/**
	 * This method was created in VisualAge.
	 * @return java.awt.Dimension
	 */
	public Dimension getMinimumSize() {
		return getPreferredSize();
	}
	
	
	/**
	 * Return the NameLabel property value.
	 * @return javax.swing.JLabel
	 */
	/* WARNING: THIS METHOD WILL BE REGENERATED. */
	private javax.swing.JLabel getNameLabel() {
		if (ivjNameLabel == null) {
			try {
				ivjNameLabel = new javax.swing.JLabel();
				ivjNameLabel.setName("NameLabel");
				ivjNameLabel.setFont(new java.awt.Font("dialog", 0, 14));
				ivjNameLabel.setText("Device Name:");
				// user code begin {1}
				// user code end
			} catch (java.lang.Throwable ivjExc) {
				// user code begin {2}
				// user code end
				handleException(ivjExc);
			}
		}
		return ivjNameLabel;
	}
	
	/**
	 * Return the NameTextField property value.
	 * @return javax.swing.JTextField
	 */
	/* WARNING: THIS METHOD WILL BE REGENERATED. */
	private javax.swing.JTextField getNameTextField() {
		if (ivjNameTextField == null) {
			try {
				ivjNameTextField = new javax.swing.JTextField();
				ivjNameTextField.setName("NameTextField");
				ivjNameTextField.setFont(new java.awt.Font("sansserif", 0, 14));
				ivjNameTextField.setColumns(12);
				// user code begin {1}
				ivjNameTextField.setDocument(new com.cannontech.common.gui.util.TextFieldDocument(com.cannontech.common.gui.util.TextFieldDocument.MAX_DEVICE_NAME_LENGTH));
				// user code end
			} catch (java.lang.Throwable ivjExc) {
				// user code begin {2}
				// user code end
				handleException(ivjExc);
			}
		}
		return ivjNameTextField;
	}
	
	
	/**
	 * Return the PhysicalAddressLabel property value.
	 * @return javax.swing.JLabel
	 */
	/* WARNING: THIS METHOD WILL BE REGENERATED. */
	private javax.swing.JLabel getPhysicalAddressLabel() {
		if (ivjPhysicalAddressLabel == null) {
			try {
				ivjPhysicalAddressLabel = new javax.swing.JLabel();
				ivjPhysicalAddressLabel.setName("PhysicalAddressLabel");
				ivjPhysicalAddressLabel.setFont(new java.awt.Font("dialog", 0, 14));
				ivjPhysicalAddressLabel.setText("Address:");
				// user code begin {1}
				// user code end
			} catch (java.lang.Throwable ivjExc) {
				// user code begin {2}
				// user code end
				handleException(ivjExc);
			}
		}
		return ivjPhysicalAddressLabel;
	}
	
	/**
	 * Return the PointCopyCheckBox property value.
	 * @return javax.swing.JCheckBox
	 */
	/* WARNING: THIS METHOD WILL BE REGENERATED. */
	private javax.swing.JCheckBox getPointCopyCheckBox() {
		if (ivjPointCopyCheckBox == null) {
			try {
				ivjPointCopyCheckBox = new javax.swing.JCheckBox();
				ivjPointCopyCheckBox.setName("PointCopyCheckBox");
				ivjPointCopyCheckBox.setText("Copy Points");
				ivjPointCopyCheckBox.setEnabled(false);
				// user code begin {1}
				// user code end
			} catch (java.lang.Throwable ivjExc) {
				// user code begin {2}
				// user code end
				handleException(ivjExc);
			}
		}
		return ivjPointCopyCheckBox;
	}
	
	
	/**
	 * This method was created in VisualAge.
	 * @return java.awt.Dimension
	 */
	public Dimension getPreferredSize() {
		return new Dimension( 350, 200);
	}
	
	
	/**
	 * This method was created in VisualAge.
	 * @return java.lang.Object
	 * @param val java.lang.Object
	 * Note: Cap Bank pointID assignment should be handled by utilizing a CommonMulti
	 */
	public Object getValue(Object val)
	{
		DeviceBase device = ((DeviceBase) val);
		int previousDeviceID = device.getDevice().getDeviceID().intValue();
		com.cannontech.database.data.route.RouteBase newRoute = null;
	
		device.setDeviceID( com.cannontech.database.db.pao.YukonPAObject.getNextYukonPAObjectID() );
	
		boolean hasRoute = false;
		boolean hasPoints = false;
		boolean isCapBank = false;
	
		String nameString = getNameTextField().getText();
		device.setPAOName(nameString);
	
		com.cannontech.database.data.multi.MultiDBPersistent objectsToAdd = new com.cannontech.database.data.multi.MultiDBPersistent();
		objectsToAdd.getDBPersistentVector().add(device);
	
		//Search for the correct sub-type and set the address
		if( getAddressTextField().isVisible() )
		{
			
			if (val instanceof IDLCBase)
				((IDLCBase) val).getDeviceIDLCRemote().setAddress(new Integer(getAddressTextField().getText()));
			else if (val instanceof DNPBase)
				((DNPBase) val).getDeviceAddress().setMasterAddress(new Integer(getAddressTextField().getText()));
			else if (val instanceof CarrierBase)
			{
				 Integer addressHolder = new Integer(getAddressTextField().getText());
				 if(val instanceof Repeater900)
				  	addressHolder = new Integer(addressHolder.intValue() + 4190000);
				 ((CarrierBase) val).getDeviceCarrierSettings().setAddress(addressHolder);
				 
				if( DeviceTypesFuncs.isMCT(getDeviceType()) )
				{
					checkMCTAddresses( new Integer(getAddressTextField().getText()).intValue() );
				}
			}
			else if (val instanceof CapBank)
			{
				 ((CapBank) val).setLocation(getAddressTextField().getText());
				 isCapBank = true;
			}
			else if (val instanceof ICapBankController )
				 ((ICapBankController) val).assignAddress( new Integer(getAddressTextField().getText()) );
			else if (val instanceof Ion7700)
				 ((Ion7700) val).getDeviceAddress().setSlaveAddress( new Integer(getAddressTextField().getText()) );
			else if (val instanceof Series5Base)
				 ((Series5Base) val).getSeries5().setSlaveAddress( new Integer(getAddressTextField().getText()) );			
			else if (val instanceof RTCBase)
				((RTCBase) val).getDeviceRTC().setRTCAddress( new Integer( getAddressTextField().getText()));
			else //didn't find it
				throw new Error("Unable to determine device type when attempting to set the address");
		}
	
		//Search for the correct sub-type and set the meter fields
		if( getJTextFieldMeterNumber().isVisible() )
		{
			if( val instanceof MCTBase )
				 ((MCTBase ) val).getDeviceMeterGroup().setMeterNumber( getJTextFieldMeterNumber().getText() );
			else if( val instanceof IEDMeter )
				 ((IEDMeter) val).getDeviceMeterGroup().setMeterNumber( getJTextFieldMeterNumber().getText() );
			else //didn't find it
				throw new Error("Unable to determine device type when attempting to set the Meter Number");
		}
		
		if (com.cannontech.database.data.pao.DeviceClasses.getClass(device.getPAOClass()) == com.cannontech.database.data.pao.DeviceClasses.TRANSMITTER)
		{
			com.cannontech.database.cache.DefaultDatabaseCache cache =
				com.cannontech.database.cache.DefaultDatabaseCache.getInstance();
			synchronized (cache)
		{
				java.util.List routes = cache.getAllRoutes();
				DBPersistent oldRoute = null;
				Integer routeID = null;
				String type = null;
	
				for (int i = 0; i < routes.size(); i++)
				{
					oldRoute = com.cannontech.database.data.lite.LiteFactory.createDBPersistent(((com.cannontech.database.data.lite.LiteYukonPAObject) routes.get(i)));
					try
					{
						com.cannontech.database.Transaction t =
							com.cannontech.database.Transaction.createTransaction(com.cannontech.database.Transaction.RETRIEVE, oldRoute);
						t.execute();
					}
					catch (Exception e)
					{
						com.cannontech.clientutils.CTILogger.error( e.getMessage(), e );
					}
					
					if (oldRoute instanceof com.cannontech.database.data.route.RouteBase)
					{
						if (((com.cannontech.database.data.route.RouteBase) oldRoute).getDeviceID().intValue()
							== previousDeviceID)
						{
							type = com.cannontech.database.data.pao.PAOGroups.getPAOTypeString( ((com.cannontech.database.data.lite.LiteYukonPAObject) routes.get(i)).getType() );
							newRoute = com.cannontech.database.data.route.RouteFactory.createRoute(type);
	
							routeID = com.cannontech.database.db.pao.YukonPAObject.getNextYukonPAObjectID();
							hasRoute = true;
							break;
	
						}
					}
				}
				
				if (hasRoute) 
				{
					((com.cannontech.database.data.route.RouteBase) newRoute).setRouteID( com.cannontech.database.db.pao.YukonPAObject.getNextYukonPAObjectID() );
					((com.cannontech.database.data.route.RouteBase) newRoute).setRouteType( type );
					((com.cannontech.database.data.route.RouteBase) newRoute).setRouteName(nameString);
					((com.cannontech.database.data.route.RouteBase) newRoute).setDeviceID(
						((com.cannontech.database.data.route.RouteBase) oldRoute).getDeviceID() );
					((com.cannontech.database.data.route.RouteBase) newRoute).setDefaultRoute(
						((com.cannontech.database.data.route.RouteBase) oldRoute).getDefaultRoute() );
					((com.cannontech.database.data.route.RouteBase) newRoute).setDeviceID(device.getDevice().getDeviceID());
					
					if( type.equalsIgnoreCase(com.cannontech.database.data.pao.RouteTypes.STRING_CCU) )
					{
						((com.cannontech.database.data.route.CCURoute) newRoute).setCarrierRoute(((com.cannontech.database.data.route.CCURoute) oldRoute).getCarrierRoute());
						((com.cannontech.database.data.route.CCURoute) newRoute).getCarrierRoute().setRouteID(routeID);
					}
	
					/*//put the route as the second place in our Vector
					objectsToAdd.getDBPersistentVector().insertElementAt( newRoute, 1 );*/
				}
			}
	
		}
	
		if (getPointCopyCheckBox().isSelected())
		{
			java.util.Vector devicePoints = null;
			com.cannontech.database.cache.DefaultDatabaseCache cache =
				com.cannontech.database.cache.DefaultDatabaseCache.getInstance();
			synchronized (cache)
		{
				java.util.List allPoints = cache.getAllPoints();
				devicePoints = new java.util.Vector();
	
				com.cannontech.database.data.point.PointBase pointBase = null;
				com.cannontech.database.data.lite.LitePoint litePoint = null;
	
				for (int i = 0; i < allPoints.size(); i++)
				{
					litePoint = (com.cannontech.database.data.lite.LitePoint) allPoints.get(i);
					if (litePoint.getPaobjectID() == previousDeviceID)
					{
						pointBase = (com.cannontech.database.data.point.PointBase) com.cannontech.database.data.lite.LiteFactory.createDBPersistent(litePoint);
						try
						{
							com.cannontech.database.Transaction t =
								com.cannontech.database.Transaction.createTransaction(com.cannontech.database.Transaction.RETRIEVE, pointBase);
							t.execute();
							devicePoints.addElement(pointBase);
						}
						catch (com.cannontech.database.TransactionException e)
						{
							com.cannontech.clientutils.CTILogger.error( e.getMessage(), e );
						}
					}
				}
	
				java.util.Collections.sort(allPoints, com.cannontech.database.data.lite.LiteComparators.litePointIDComparator);
				int startingPointID = ((com.cannontech.database.data.lite.LitePoint) allPoints.get(allPoints.size() - 1)).getPointID() + 10;
				Integer newDeviceID = device.getDevice().getDeviceID();
	
				for (int i = 0; i < devicePoints.size(); i++)
				{
					((com.cannontech.database.data.point.PointBase) devicePoints.get(i)).setPointID(new Integer(startingPointID + i));
					((com.cannontech.database.data.point.PointBase) devicePoints.get(i)).getPoint().setPaoID(newDeviceID);
					objectsToAdd.getDBPersistentVector().add(devicePoints.get(i));
				}
				hasPoints = true;
			}
	
		}
		
		//user can input new phone number; otherwise they may later control/scan the wrong device
		if( val instanceof RemoteBase)
		{
			 if(getJTextFieldPhoneNumber().isVisible())
			 {
				  ((RemoteBase)val).getDeviceDialupSettings().setPhoneNumber(getJTextFieldPhoneNumber().getText());
			 }
		}
		
		if (hasPoints || hasRoute || isCapBank)
		{
			if(hasRoute)
				objectsToAdd.getDBPersistentVector().add(newRoute);
			return objectsToAdd;
		}
		else
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
		getPointCopyCheckBox().addItemListener(ivjEventHandler);
		getNameTextField().addCaretListener(ivjEventHandler);
		getAddressTextField().addCaretListener(ivjEventHandler);
		getJTextFieldMeterNumber().addCaretListener(ivjEventHandler);
		getJTextFieldPhoneNumber().addCaretListener(ivjEventHandler);
	}
	
	
	private javax.swing.JLabel getJLabelRange()
	{
	   if( jLabelRange == null )
	   {
	      jLabelRange = new javax.swing.JLabel();
	      jLabelRange.setFont(new java.awt.Font("dialog.bold", 1, 10));
	      
	      jLabelRange.setMaximumSize(new java.awt.Dimension(250, 20));
	      jLabelRange.setPreferredSize(new java.awt.Dimension(220, 20));
	      jLabelRange.setMinimumSize(new java.awt.Dimension(220, 20));            
	   }
	   
	   return jLabelRange;
	}
	
	/**
	 * Initialize the class.
	 */
	/* WARNING: THIS METHOD WILL BE REGENERATED. */
	private void initialize() {
		try {
			// user code begin {1}
			// user code end
			setName("DeviceNameAddressPanel");
			setLayout(new java.awt.GridLayout());
			setSize(350, 200);
			add(getJPanelCopyDevice(), getJPanelCopyDevice().getName());
			initConnections();
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
		// user code begin {2}
		getJTextFieldPhoneNumber().setVisible(false);
		getJLabelPhoneNumber().setVisible(false);	
		// user code end
	}
	
	/**
	 * This method was created in VisualAge.
	 * @return boolean
	 */
	public boolean isInputValid() {
		if( getNameTextField().getText() == null   ||
				getNameTextField().getText().length() < 1 )
		{
			setErrorString("The Name text field must be filled in");
			return false;
		}
	
	   if( getAddressTextField().isVisible() )
	   {
	   		if( getAddressTextField().getText() == null
	        	|| getAddressTextField().getText().length() < 1 )
	   		{
	   			setErrorString("The Address text field must be filled in");
	   			return false;
	   		}
	   	
			try 
			{
		      	long addy = Long.parseLong(getAddressTextField().getText());
		      	if( !com.cannontech.device.range.DeviceAddressRange.isValidRange( getDeviceType(), addy ) )
		      	{
		        	setErrorString( com.cannontech.device.range.DeviceAddressRange.getRangeMessage( getDeviceType() ) );
		
		         	getJLabelRange().setText( "(" + getErrorString() + ")" );
		         	getJLabelRange().setToolTipText( "(" + getErrorString() + ")" );
		         	return false;
		      	}
		      	else
		         	getJLabelRange().setText( "" );
	   		}
	   		catch( NumberFormatException e )
	   		{} //if this happens, we assume they know what they are 
	   	   	// doing and we accept any string as input	      
		}
	
		return true;
	}
	
	
	/**
	 * Method to handle events for the ItemListener interface.
	 * @param e java.awt.event.ItemEvent
	 */
	/* WARNING: THIS METHOD WILL BE REGENERATED. */
	public void itemStateChanged(java.awt.event.ItemEvent e) {
		// user code begin {1}
		// user code end
		if (e.getSource() == getPointCopyCheckBox()) 
			connEtoC3(e);
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
			DeviceCopyNameAddressPanel aDeviceCopyNameAddressPanel;
			aDeviceCopyNameAddressPanel = new DeviceCopyNameAddressPanel();
			frame.getContentPane().add("Center", aDeviceCopyNameAddressPanel);
			frame.setSize(aDeviceCopyNameAddressPanel.getSize());
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
	public void setValue(Object val ) 
	{
		int deviceClass = -1;
		
		if( val instanceof DeviceBase )
	   {
			deviceClass = com.cannontech.database.data.pao.DeviceClasses.getClass( ((DeviceBase)val).getPAOClass() );
	      setDeviceType( com.cannontech.database.data.pao.PAOGroups.getDeviceType( ((DeviceBase)val).getPAOType() ) );
	   }

	
		//handle all device Address fields
		boolean showAddress = 
				(val instanceof IEDBase)
				 //|| (val instanceof ICapBankController)
				 || (deviceClass == com.cannontech.database.data.pao.DeviceClasses.GROUP)
				 || (deviceClass == com.cannontech.database.data.pao.DeviceClasses.VIRTUAL);
	
		getAddressTextField().setVisible( !showAddress );
		getPhysicalAddressLabel().setVisible( !showAddress );
	
		
		//handle all meter fields
		boolean showMeterNumber = (val instanceof MCTBase) || (val instanceof IEDMeter);
		getJTextFieldMeterNumber().setVisible( showMeterNumber );
		getJLabelMeterNumber().setVisible( showMeterNumber );
	
	
		setPanelState( (com.cannontech.database.data.device.DeviceBase)val );
	
	
		int deviceDeviceID = ((com.cannontech.database.data.device.DeviceBase)val).getDevice().getDeviceID().intValue();
	
		
		com.cannontech.database.cache.DefaultDatabaseCache cache = com.cannontech.database.cache.DefaultDatabaseCache.getInstance();
		synchronized(cache)
		{
			java.util.List allPoints = cache.getAllPoints();
			for(int i=0;i<allPoints.size();i++)
			{
				if( ((com.cannontech.database.data.lite.LitePoint)allPoints.get(i)).getPaobjectID() == deviceDeviceID )
				{
					getPointCopyCheckBox().setEnabled(true);
					getPointCopyCheckBox().setSelected(true);
					break;
				}
			}
		}
	}


   private void setPanelState( com.cannontech.database.data.device.DeviceBase val )
   {

      Integer addressHolder;
      
      if( val instanceof CarrierBase )
         {
          	addressHolder = new Integer(((CarrierBase)val).getDeviceCarrierSettings().getAddress().toString());
            if(val instanceof Repeater900 )
         		addressHolder = new Integer(addressHolder.intValue() - 4190000);
          	getAddressTextField().setText( addressHolder.toString() );
         }
      if( val instanceof IDLCBase )
         getAddressTextField().setText( ((IDLCBase)val).getDeviceIDLCRemote().getAddress().toString() );
   
   	  if( val instanceof DNPBase )
   	  	 getAddressTextField().setText( ((DNPBase)val).getDeviceAddress().getMasterAddress().toString() );
   
      if( val instanceof MCTBase )
         getJTextFieldMeterNumber().setText( ((MCTBase)val).getDeviceMeterGroup().getMeterNumber().toString() );
   
      if( val instanceof IEDMeter )
         getJTextFieldMeterNumber().setText( ((IEDMeter)val).getDeviceMeterGroup().getMeterNumber().toString() );

      if( val instanceof Ion7700 )
      {
         getPhysicalAddressLabel().setText("Slave Address:");
         
         getAddressTextField().setText( 
            ((Ion7700)val).getDeviceAddress().getSlaveAddress().toString() );            
      }
      
      if( val instanceof Series5Base )
      {
      	 getAddressTextField().setText( ((Series5Base)val).getSeries5().getSlaveAddress().toString());
      }
      
      if( val instanceof RTCBase )
      {
      	 getAddressTextField().setText( ((RTCBase)val).getDeviceRTC().getRTCAddress().toString());
      }
   
	  //user can input new phone number; otherwise they may later control/scan the wrong device
		if( val instanceof RemoteBase)
		{
			if(((RemoteBase)val).hasPhoneNumber())
			{
				getJTextFieldPhoneNumber().setVisible(true);
			  	getJLabelPhoneNumber().setVisible(true);
			  	getJTextFieldPhoneNumber().setText(((RemoteBase)val).getDeviceDialupSettings().getPhoneNumber());
		 	}
			else
			{
			  	getJTextFieldPhoneNumber().setVisible(false);
			  	getJLabelPhoneNumber().setVisible(false);
			} 
		}
		
       if( val instanceof ICapBankController )
       {
         	getPhysicalAddressLabel().setText(
            (val instanceof CapBankController ? "Serial Number:"
             : (val instanceof CapBankController6510 ? "Master Address:" : "Address:")) );
   
         getAddressTextField().setText( 
            ((ICapBankController)val).copiableAddress().toString() );            
      }

      if( val instanceof CapBank )
      {
         getPhysicalAddressLabel().setText( "     Street Location:" );

         getAddressTextField().setText( 
            ((CapBank)val).getPAODescription().toString() ); 
            
         getAddressTextField().setDocument(new com.cannontech.common.gui.util.TextFieldDocument(com.cannontech.common.gui.util.TextFieldDocument.MAX_CAP_BANK_ADDRESS_LENGTH));           
      }


      getNameTextField().setText( val.getPAOName() );      
   }
   private void setDeviceType( int devType_ )
   {
      deviceType = devType_;
   }
   
   private int getDeviceType()
   {
      return deviceType;
   }
   
   private void checkMCTAddresses( int address )
   {
	   try
	   {
		   String[] devices = DeviceCarrierSettings.isAddressUnique(address, null);
		   if( devices != null )
		   {
			   String devStr = new String();
			   for( int i = 0; i < devices.length; i++ )
				   devStr += "          " + devices[i] + "\n";

			   int res = javax.swing.JOptionPane.showConfirmDialog(
							   this, 
							   "The address '" + address + "' is already used by the following devices,\n" + 
							   "are you sure you want to use it again?\n" +
							   devStr,
							   "Address Already Used",
							   javax.swing.JOptionPane.YES_NO_OPTION,
							   javax.swing.JOptionPane.WARNING_MESSAGE );

			   if( res == javax.swing.JOptionPane.NO_OPTION )
			   {
			   		throw new com.cannontech.common.wizard.CancelInsertException("Device was not inserted");
			   }
		   }
	   }
	   catch( java.sql.SQLException sq )
	   {
		   com.cannontech.clientutils.CTILogger.error( sq.getMessage(), sq );
	   }
   }
}