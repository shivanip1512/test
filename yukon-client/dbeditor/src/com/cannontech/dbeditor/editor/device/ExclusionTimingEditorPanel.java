package com.cannontech.dbeditor.editor.device;

import com.cannontech.database.data.pao.YukonPAObject;
import com.cannontech.database.data.lite.LiteYukonPAObject;
import com.cannontech.database.db.pao.PAODefines;
import com.cannontech.database.db.pao.PAOExclusion;
import com.cannontech.common.util.CtiUtilities;
import java.util.Vector;
import java.util.StringTokenizer;
/**
 * Insert the type's description here.
 * Creation date: (4/4/2004 11:31:17 AM)
 * @author: 
 */
public class ExclusionTimingEditorPanel extends com.cannontech.common.gui.util.DataInputPanel {
	private javax.swing.JLabel ivjCycleTimeJLabel = null;
	private javax.swing.JTextField ivjCycleTimeJTextField = null;
	private javax.swing.JLabel ivjOffsetJLabel = null;
	IvjEventHandler ivjEventHandler = new IvjEventHandler();
	private javax.swing.JLabel ivjJLabelMaxTransmit = null;
	private javax.swing.JLabel ivjJLabelMinutes = null;
	private javax.swing.JLabel ivjJLabelSeconds1 = null;
	private javax.swing.JLabel ivjJLabelSeconds2 = null;
	private javax.swing.JLabel ivjJLabelSeconds3 = null;
	private javax.swing.JTextField ivjJTextFieldMaxTransmit = null;
	private javax.swing.JTextField ivjJTextFieldOffset = null;
	private javax.swing.JTextField ivjJTextFieldTransmitTime = null;
	private javax.swing.JLabel ivjTransmitTimeJLabel = null;
	

class IvjEventHandler implements javax.swing.event.CaretListener {
		public void caretUpdate(javax.swing.event.CaretEvent e) {
			if (e.getSource() == ExclusionTimingEditorPanel.this.getCycleTimeJTextField()) 
				connEtoC1(e);
			if (e.getSource() == ExclusionTimingEditorPanel.this.getJTextFieldOffset()) 
				connEtoC2(e);
			if (e.getSource() == ExclusionTimingEditorPanel.this.getJTextFieldTransmitTime()) 
				connEtoC3(e);
			if (e.getSource() == ExclusionTimingEditorPanel.this.getJTextFieldMaxTransmit()) 
				connEtoC4(e);
		};
	};
/**
 * LMIExclusionEditorPanel constructor comment.
 */
public ExclusionTimingEditorPanel() {
	super();
	initialize();
}
/**
 * connEtoC1:  (CycleTimeJTextField.caret.caretUpdate(javax.swing.event.CaretEvent) --> LMIExclusionEditorPanel.fireInputUpdate()V)
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
 * connEtoC2:  (JTextFieldOffset.caret.caretUpdate(javax.swing.event.CaretEvent) --> ExclusionTimingEditorPanel.fireInputUpdate()V)
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
 * connEtoC3:  (JTextFieldTransmitTime.caret.caretUpdate(javax.swing.event.CaretEvent) --> ExclusionTimingEditorPanel.fireInputUpdate()V)
 * @param arg1 javax.swing.event.CaretEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC3(javax.swing.event.CaretEvent arg1) {
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
 * connEtoC4:  (JTextFieldMaxTransmit.caret.caretUpdate(javax.swing.event.CaretEvent) --> ExclusionTimingEditorPanel.fireInputUpdate()V)
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
 * Comment
 */
public void cycleTimeJTextField_CaretUpdate(javax.swing.event.CaretEvent caretEvent) {
	return;
}
/**
 * 
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private static void getBuilderData() {
/*V1.1
**start of data**
	D0CB838494G88G88GB50727B0GGGGGGGGGGGG8CGGGE2F5E9ECE4E5F2A0E4E1F4E15DDC8BF4D4553926CB6CE5DDD9F7E145F5D171AE2A5452B63A9023D2CD2FD1238441CBD0D0AA298D088A0A9AB5D2EE1B55EEB2A1848890720084828C181009044903A8B1241AB7C4832612E2EA3166A4F3128CCCE6461993B2A1916E7F6FFD76BEE7E64EA441DAD7EF3378F34E7E767EDF7B3D7F3383495AB7ED7D3A24ACC2562DA76A4F51F504CC7E93A1D7C9377E00E36E19CA8A517D6C875ACEA28EA7
	43F39C64952F6BCFC9A7795929101E8C6908B5CA4ACEF84FA4C559F9CD7092C2CFC2A3A1D7976CBBB6321E8752391E8DB4FDE3DD9ABC3782CDC1DDC029F7AA20FFD6DD0E0A478136D5FDC7DA17C4C8B4AD73EF17F2F4F84B1D2C5CD4F84E840A1A05E59E2F4BD37353A1ED819A87B4F996AB9B864F0D543A2B2BAA55F2E72F1DC1929E5B1B3485F56964F3CA3B0B71162ACFB3390DD60409EC4B2E07E7F56B791E8F3ADCFD56AE4969F4B995392B57EA6B14EDF64565312A8F1BFC415EA5D7D5351CECEE74F5B97A
	3DF6173311E635CACE59C1265D05365FFEA9154AA524975245610895DC467ADA8169C4202CF0EE7F42AD552AFD134F4FA0F137EE8FE93FBB1C59BDB9DC33FF516992D3435DA8FF8DED8F64DB8479E900B15F52FD4DD768DB2B476533775915C1C38F096A6387755C4789F7F39FF7DEC49BA2A13D84A8DE604FD2BC759EA5458CD4F98F777D0F055C777621FB495A4E5A24EDA1FCEF3D07796C3BC767FB4110A91776450FB330BF05C35E922055C06977AAA925C02DC03B6998C8597E8EBCEBBB15FAD7DF1F4BD96B
	F657B9EC1E13AE1B6C00772AAA48D131CF65AE1747C6886B6733AE959B58432977DE7FBE0E44FC7B0C70FE9976699DC4D10EA62DB770ADFC31B9E9D815B169DE1DEF77E23D5DCC7B4CCCF8C7BE6C978C8F23F84EEC8DE7FD609045B1DD8DF9B3E673FAEE5F4AEDE9C93E05241755A7EDB030E54AC315C9D9BFE7B6947FDC330555F1C19656F1BB608A50C42069C0D1C0ABFE4E6B38F57B11516A38997A1E47A9B9CE77F73AA50FD257EFF758E40FDA5709F5A3553591B13ECD75D74A476AE54691FD9B455ED5E7D8
	AF31B38A1348FD2CBEF26E536A05692E8F293B723E603E31D53461B5F4CC345C475A50A4706BA9FED60733365DC3F1CCBBA0AF8608157FF488710A7BD9790DC24E1794EF5561CC4E8AAA87538E480B81E26D7BF0B64E379591B0E600FA014681CD830AG0A0960EDFC537612B10E23D6DD7BB25E360C9D7014FD0AC7EAF6FA954959A51F11BCF62953A1FB09DA660B8C2C57CEBF3ECEBA3E3BC047F159A9FBA48566A410AE754950193C2C4C790C6DD846A3535CB6FB1F8C8A7A5C044B75E6E4224F12D779335BA6
	A932D602658FE5E01DDC104E00C49000ED7C8B2C2F39F4AE0F877F53FE416BAAFE87E2B6C88F7F02574B1A0C8CF8FE8C1DDBF6D4D5B5583B508C49B3487B682D175107C9D03E663FE15CG35BCGED8854914947692B6D3CBFB6EC391D94AF2EB56C0FC82BA3D95FCAFEB030BF4E22FA0A81EF81F2GCD848A831A7FA0DF3B4BAEF70468335CB63413765587027BF0C724063598647B0F4D16103A562BE5CCEAFA33C119AD81E3B614AE56AEFF4B413E3D9A7CCCFD88768C8F7136A94BB433BEDBA7F9E55ECBBF71
	C08713EDDACB313256ACDC03648B32D3E9105D325386AFDE350F3C1B35D16B3F5DAE51370ED19C6733FA68E0DE0157E6EDE7B848500F861E5FCC73A5C57158BB7B9579342CA8F6E70F467FA7EA0B57EF2A742A3EF664222FB560E3BBD02F7075192CCCDDBFECF1394E773B5BF004297D79F33AEE8FC379498F717EF06DC55C5B4D96691BE8DA4BFF7BE27B88F57FCD88F57977136AEFDD07C9774EF4348F2593B61ECC3A7E4772E41A37826CCAFBD8C929GBA7BB0777FA5EADBA05F37E5A753E19AEA8F10BFD2FF
	3FD2E2FD63716C50FDFBD136F15FFEA65B38EF47E50F5CF7D7E69B75DD1677DAB67A689AF06A46C2C2B64E1FDDAE27D32683D23090360FFD358D7D30BB6D4AE7F6DB0F2C1C36DF14E15DFE987BF49C1D2347437B140779DCBA70B61FCB73AF7E04C4FE19E7387F1A7223E3C9718F2BF3E914B617B21B5E5A01E3CCF648FDD0D1752EFE2742705F5179DF453DA60A3D7A49F46435548FF6B9686272D19BD4E613DDF6580A3BE086D02C74B7DBFCAC67248B124F6A9D00AA3352763056E3EEA33E96198F9F7E409C62
	04D15C95AC7F4BFA1D641611C49FABBAD3D44A6C7D53668B5F093D5DF60F5C6CF477F3C9E8816B6719E4FF757037EA4308A797C3CB189D116B86299D1A7ACF3A3B3D32725D98D1C6F8FDEC493B9CE0C71BC7F2FAC198D64EF7E3CD39EA4B320CCB81361C14FC5C1C6F4614A3FCDD3FEE970E43FC981F15C02DD1FC2D9B580979FC2DD39774FA1743412690488B0F4279E16565AC5D186F0DE2710A2DA2BF0B0C1CDFB3CA3EE3147CA92364C7054CE7FE5AF2F0ED724ADF754B30E546457CE3F9106FFBF3B282724E
	C80EFEEDBF6D4D41FD15C3378FE0F8F72E715C664845357F3C106F4E45E05085D5A61B0BE7CAA8536D3177413689B7B6EADC672BFAFDDCA7917C4098D075A350E6C02CDFBD33E38FBCC70BB1A4BE0272EE9862B120DEC80F831A768813997B882B9FD66E6CB0AFF762D158CF830DFB54285C3E2FF9398848DF94325C1322DC8264274D89AED7BC874B7DC644F9CA843675B2E2CBE1813FBA191FB3CF266BE3D4B6EF0561DA04641E43640F1F93F87E0E5E0DF227839EBE07777F17F275FB3DDA2F5755E527479F35
	0DEF5825DF279876DF3BB64193C61B974E62B659477B4974DD74CCA889D8ED6B1F9D7AA389BE6F315662DB697A81BA4C5BDC75F0C4BB81DD2DDEAC63E76C72G617570F513BE5695312DA8E9F308FA08D67DDF292B07A985C3A6E6538DD598E7B00AE9F5D1A32DBD7DF6EB3FE247031824C8F4DA5147671652BDB06A36C47398557177F1BCF4C05A87B489081D179F2267E854BB8330B8A0B6E67FD6259F33B9F359FE3AE6AE6F67C54358A72EF01ED46BBC2AA80335D1400C486B71164C9AD56F7CBBEEA15517B7
	99464DA6EC4BCAEA1B4B6AEFF82ED60FD3534E19E682B17F2E9E667EC5C3997298776F76C2A37FAA9F637E2D7EC67EB11DA73E66BADBC05EF821F3D53D114E0847F8DBA48A3E9840D267F1BEE73EE1DB4C63FB414F4A7563AFB24E92F27C354DE37554BBAFF07C056D21FBCC40274C6363EF5F6E6D3A7177A974F8495983B32C58EF6F463A0239D4CDAF5C0DFA69DCCA785A36EAAF5A9E86B2230162005682654C67BA5A73C60D29B57AC60829450978D465FC66D34BFC6D6C55816F8A50849D4662C79B2A302E27
	4EE77123DC0167D1FC098EE7EDF3120E9FCCE7BD8EFEBC4E4FCE51C3017131307F710F99C48724995FD3C1FC5600746AGBEB6F68AACF9FCEE50F5452AAAED9F4366006B2BD255F9A9F9810F6BFCE8263C10EE87F283CD841A89B49FA8FE81EF139F67994D097EED92EAC254E2G4C4FEB063FD97D5C904497D8BF69B17EFC4C6F95055CEFCB8CEF23D3DFFFB39B4E8671855A50E1E80339015B6093B6A485455947E6C3FA90DF208DB38DED78D1BE37A1BA06377F657D081105103F1037F5729E0CC171B15E6A32
	BB954247771DFBF0EF647258FB6CCE0E5DC331AE17079EEE99767A5E50676E35FB0D4F5D3F5FEBFC6ECE5EB7F2CCA9EDDF28F3B90B7F7C062EFDE6702F9A08E111B90835C25AA630DA3AF718G69E9826B24FB10A8C8478ACC223C7110CEFD02376516AAB86211BCC8D78335833901A681058145G4583E5BD2924148235G75BE49F98FD0DE5C6B0D87ECBAD05413ECCF077B3595CF327DD872A2A5A59F2881483608731EFEBF921EA8BFF0DE57936A522763F5655D863CF9AA562EBECB1F9CD93E1E2AD5BF43F4
	F2DBD47EC9A16CE43EFC1F7A32ED94793981696D3AF7237811C927831F59E374034F2F385F5447E4F72ACF2358AD529E2AF36E0EED344FFA3D3217CE0C38ABD373DED43209C15C671454A793FEAE7E4350C635CC23622AAF93F5F4E3959F7B6397E9F5B9C5FD8F03E764A27FBAD6632E6650633155ECBC9E4F1851EF2F2B5F53A5870E39AF4CB9A2CF1B691B95398F5DD04FDB14DF11BCBDF250186D237CACAF947F85F328F14D6A6821E1FD9D150BBADAD5486BE8112EAED62A6F0970CCDF6CDFC7CC5E55DF6B65
	9D91728A9A383CFC9DDF257A5E824F8EC3F96FDF524B2B9072FE1A4F65A9BA3E896A7BD4F84EDCEC54066377637E43A75293E8FAD0685B5A00756D76485D3247A35BD83810A4AE46B92B102EBFD1709E33186FE71F4E41F95BA18D3A7AE1021F7C9416EB49551FE7EBG6B859AFE0A5943769BF78EE05F6F76C8FD509C275DF297F1BF0D3C6D3BB8EFAC140FC7F900178235BE4DF58E6EC6DEE034777577D18B19FD5752F9F6B6140B92E5FD749C5FA779824ACEE273AF14CB94E59F2C46F1EA90D5D36B26E47FB6
	3F7C9629934EC6AC6F23815C473178974BE0784623F8A6514723D4B615EF689D0CD184C650D85E1E0A9DFEF9FAA91C3F3F977BB713290BB409766C4F4C7613E1D4E64116AD1A8C3B930609B6BE0676EC95F927E56807B6EF994F7375876515733C8DBDC1F9C7F85C6670C17D394332041DBBDA1670F349EB05C6799DA2FF67CE23FCC564FF11EF14BFC164AF28B14A1FAA72FF67B54A1FA972730B0D7223C47E7D070C7217AC61EB7F1DF48F3D9A52293F64587D5B914B03F431404C99FCAFD1A930A48B45A0BDCD
	E0444265058BECF689E25110DEA230BD25DC5E8A018D88F9954FF02C3A014BBBA130B9F47FFB96528E0195DB38BC1F40A28EF3F95182FBB01F4B0B9558F534DCBC2449D20E2DBD4C6525894C134F651D854C3D946FD875DA08681FF76718616914874CF8EC31CA830A3581060793E3E3160A3252E2731177CA0B58F9F65A007EBCDB39499A72BC9B3E144DD5516AB3BFF93FBAEF15F5781D13D0E1D555F10F5DD6A7753450D9106D13BD39292AEC924BE3A553E86C3092522DC03D317C1C6DBB4863BBE1375DC2DA
	4655995EE50AEBABCA9A974B6C199E2B1D33D7968C19184ECB3B384EB8484F79953D7332CB5F97314E96A6313DBE4BFF56AF9F67923C372665276F5667233D6F1A7479F5FE79D8C707D4FE665B3C826EDB623C1944F1F7E948FBDA9D3FD2E3F86AF37693E6127C70B993EB73896FE8EDFEE6CCED4E78FEF6C863FB677D317031F6FE33BB07981E07690C466A7BB1BAA6304EA240E6069D24E382578973339CBB3513E36385B6B91FE3AB8516D20B982E6FAD3F66D839423171827BEBB147A2847646A10EA57C1A2F
	E751D5215772E5F48EAA0E0339A50E17FFE2BB463095D8AAD85DAD45AD86199207650FE6B49834193E5DCDBA32810F8FA8DB4D1B9847629E7CDBF0A64935982F61E5347E62207CEAA00695D1ACE7991CDD16F1DB9714785B7A7B431A2D952523592A3737F5193F2D36E51A2DCC7FB42A5F8D7870B2EE13CC674899108E97B6BDDB68EF537E924D26FA4BE8B6ED50599497E0D3C210CD4F533912AC0739F2B93769A55A27AAA15D301C5B7453437EB6B514EAB6ED330EE653C61DCD6E657EB60DDF9EE853CDD47FA4
	4027AE67F35B023F6A675C749BF312B8CF203E68656A5D196501B144774AD0EE6AF3507E4F7178426D969C58E724F34111F5GDBBDAE376CD18699CA3F6C287B66015CAB3D734662F7D974DBDA43F31A4F8C5FC371D69D4E62FA376572FB25E7A1AF72B99EAB69A947F9A5FF0512D283548E64861A849436C25C0932E0A30D98A7EBF339DB60A0665005B218FFD76D7AE6F77A9E5C406769707BFECAE6FFBFFEDD28EF8C11ABD87B44294F5605E60AB37FFEFB847DCBFF9E56BC20D620DE20E120494FF37FBEA8AA
	E67EE17354AA0A5455CB3F34F0677063078663610079362050682E4B0C67199D916AB366F97FF34F8AC82F8D4052DE007D85D0458B0177E98ED90CF41CD04B1DFD41B8766086FCFCC05E94C80781C58669B83D53C847A2355C4A90BA9281CFFF51BFAF9F5215C0ADAF866A38A15FC8C707DACEF951D84784402786644D04F4945012A09D1BEB75FDED351ACFD632E7564AE09D0C6F2822673328652A55E7EBC83E0EE2BD1FCDAD67D31F93C3727D66101EEF1ADAAEDCFDC69B703139F2463B688E1BC7E7EE5FCBC2
	6E9D0E16951B4BCB0B8859C7D82C074E4D74A77E43E00C4BBFF8945F5978DB164647DF5E4FEEA58DCF472FFB1B980F3FD8555E62D55ABC19F826C45DA7CD2A54ECCEBFB8961B995FDD6FEBFC3717FCD3DF192C272AB49FAFD5EBF267570F5D1E9F7ED1630BA87C7B6CC97DD03367D00DA637A6FF6C761C2A5578A69C7E7B6C792ACE33E7F23DA6F7D64DD86432F9369617EE9E43A9ADA0A156C218CDBF82D91FF41E133B14B629C7709CAB433EA362E2EC09D4736A4AF0CF0FF7466C1243FE11FE9ABF8D851DBDEC
	BC6D5AC54458FADB779E167BED5FC58C9E4B0F7CC3F528F7B74AC347B9874A0D631CDF179BFFF7382AFC647BFE5714073E6F7703F27DFDBF566E279A31EDECB0764781CD871A8DB4FF95DFDB4DC78E903A369A2C2B0D369E0C33628DF2056BB7D50476F5D34568774EB3AB70DEC427CBD1DCFDA81F63399558DE0A4BCDC1066D2D40BA7258FBFA95D6D44573ABF00CB864EE8A33F37F6B74EC35927CDA3B0A47824ED878B7136417B8D6CF639CB9102E9658FFD22C95528E01758DA1B68E521384D6DE4D638D5385
	F6B37D4E13G693417B9766F5496B324EB84F6125A5286E91B40E6897B5C82DBE365360C97580CAAEE4B9401FDF0115B92A6B073C5EECB2240E2F7845B52CF4F17692BA13D1AE34F6C6436948B6C25435C169A0135DB38ADED823BD9FC436A955871D2EE4BEC01FDEED0AF7753B340FCA893AB4A198BE8DBC21A3CA24A39914B02F4294096FA38AD8D827B5F4D5C16F601F5D29DB3A09DA930A37941369C2865729609F20ECCEA8B2457EAFAB3390E64D7B9F6CA9175A2308B0D1C37D7E0CFB7F2DEC5E083451CB7
	CAE0BFCE623CCB8416EA9271B801ED953C952F0978D99A67BDA13083DBB8EF5BEB7CEC9156040B42F4C8C7822DGCA84CA0FD7D2ACC055C02DC0B620E12049C0B300A200E20062009200D2DF67F3497923305C04BCE329C1AFBA21F8A47D775DB30D216714AF9AC71FD33AE91D4AB097348D3A556FB8679B69B7E39A9D9773F4E33A401A3D2DAE2F9DD7AA361626DA7C4F3DDBB194221ED3AF53BE1F87FEDADE6767644FCB502EDE1747FE5165D4A487864C39C44E57CF67951C135B845F9DA521E3ACF3E879F1D0
	F622A8BFFE886D6A869D2C4C0DF4DE1A8D79D1C06CDC5EDCBD52395C94C0BEDD2C85770B892F8746DA426B0DE3AD7369DCC75E00396E8DEE5F8F8BB37D6A2156E16FF162AA22725CAF6EC1D48A9EDBC0DD532D05DAFEA92DEB9B14F53F4163947BE83DCC05748C01AD24F362A2C8473D416B614F97C72B0775A354C37A1B017109E6BA1F6681EEF9135BBEF1273F6DED5BB45BCD749BD19B146DF8135B19C9677309101EA2306FD19F23A09D73A637BDFC57E836EF9841F61290E83B1B4E3FE90067A408F8DD3E3F
	6D83B89C54B6BFDAE07C5D6B93BA97B7000CD6CD0EBBF49FFE194E5343D0F6C282777737F41E8E07F4E48277B7BDEDB4FFB70E60EFFCC2E01F9D4C8F710D0F4E7309D0BE75ADEE4F47F41E2F00F44DDB5C272BB27D6B66D15C0629F12F1519A3592AEF97A3DB5D206310DF5B8C5035E2987089EF71EF942BB2B4FD2FB60E26EF24FEBCBBC85FB43AC6C4819EA37425E5E97A7619C653B7E25FDB93286FA8551786F84E9A2E6FD836266F7DAD23699B296DCF687431F9BD32A9747AB127E97475A32649F8CF7AD4D3
	701EF4E913711EF4D913FECF4A30971A301DFAE05DEB9765DE9658DF8496CFE5A276195D26748AFC8D358B7193B2AA64F8A235EB00E26DAA366E38067DC545D20E639A88AE43744CF9B79F276B095D61E069ED340C5D89CB22280F2C63E8235DAD696C4E95985A4D5A74EFD8356A782BAC4173EB0833E8D94551E2F3D9C511DAF6A2AD2B3F41FA9AFF732306575A525854266A785ECECDC76750D8F4B03ECDFBB53E03557B83791AE031329C28050E60B8GEC702F3CC878F59855286FF718B5B9178B476E639305
	6611FD7C34797809B626E361FECD474B56317B18F0C0633B39EA6CFC0B8BB43E720BD7D6B7E70A343A9954FDE35B1CB5F67DD3F55F58FEFDF86CFC8F94EBFCA7ADE3675BDD2271DD3B6B4A7C7D20D473D7AA5364FCB2263E40784617EBFCB35DE3677B71910D6FD14F5879CC6FE9FC2FEC3EB2FFD90C113DAF524586AD79E3577F6E079ADFC7661569EF2F51744F2F53641CEB3CB2B92F55EBF2BEEE52645C14F4E5F21C47B5B9EC0DD66F9124DD199CFA4504307FA7E6F9118493FB58ABE43D57C3B2026B3376BF
	FE79F458178DD9C9A42919247452B93D192C9F4045416706158F1608F5B7FA3FA27827E306A9D8AA44BF1EC37F7869B8E85CA6B4B653E9BF6E4211AA53F3371A12C8B228E34BE0B3C946358C34FA18F693AC03281BFE3E466D9FAEA339813203D39B83B8367F03B82668B8B27C4BA8E4B33D02E546EBD0D58A59402ECC415BB6FE3309E5199512F9D272190350E458D6DA6161153B829EC293EEDC2E8BC8F948963F3E71488F5FFDE17E33E73CA61252CCF271C8F7DA76500829EC431B1A6AFF0950C4B63A717AB3
	EEE221993C74315E2B8CBA649612557DA97E891AD711ED35BCC668D5C8B67DF354927CDB1C12931237D7417D4D3196126C447F3C01E430EC07646CA9E10159E334AD57555251C66827C730B7135A0BD22CC2CA667D5B8A76FF34E23609A4B511CD8E17E4EB123A60D85FCCB6AA3D9E595B6BF258FC6E8F192054817E97BF49A0EE37F09F1D356291D998FD5EF77344ADAB37B4A541864FC3CAE8C95CFDDBC589DBFD2F6481D53227D9417F0C42654997F95F5B2B9CFC7C784E33C9E41D6AA4D67DFE1FDBA1A95DD2
	1F5DB188AF8DD0372E9E5AA88510E4FBA3517F31B22CCD600F724CABF3EFD9DA9D3F9A04B51175744FC64024F42D54297E3ECE59737D306B77EC1E9CF78DDA6CC6F739640D8AF618EE17278F041B83FC66D942F46BF5F3CB2F7F49EFBF2726EB2C8A59479A89D8CBFAE4D7DFC9EDF7375DA98B8150E2A0FE0F900FE52C81E5B2FC57E6FF34788F1D50439A08292F34147EAE233F4B71F799C54AA8D2D68E4D96CFC67C894C77F11FE9372409E26CC3BEFCAB7209610883EF3D5A3172C69CA44129FDC19D93513703
	FB984205221DB1758E7E32622F924AD4C6FFD7DC31174875DE00F79F9AFB17752F61DD733F34F727B56F260E625DBF2987FF75EF9FDC667A889FDB7F24B6F79A5B1C754FEC337D7F214DEE7F5EADECA67C2EC051E57D1D2A35D7EF8B7DF7516B7A591DCA75295D293A0B46B1A3810F85D23F85DA7192DD286F48277BA407C3119C67EB1DB67C26DCB7486E09B35E59D6146F65E554CC167700956DBE276379BFD0CB8788562731B4BE9AGG3CD1GGD0CB818294G94G88G88GB50727B0562731B4BE9AGG
	3CD1GG8CGGGGGGGGGGGGGGGGGE2F5E9ECE4E5F2A0E4E1F4E1D0CB8586GGGG81G81GBAGGGF89AGGGG
**end of data**/
}
/**
 * Return the CycleTimeJLabel property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getCycleTimeJLabel() {
	if (ivjCycleTimeJLabel == null) {
		try {
			ivjCycleTimeJLabel = new javax.swing.JLabel();
			ivjCycleTimeJLabel.setName("CycleTimeJLabel");
			ivjCycleTimeJLabel.setText("Cycle Time:");
			ivjCycleTimeJLabel.setMaximumSize(new java.awt.Dimension(147, 14));
			ivjCycleTimeJLabel.setHorizontalTextPosition(javax.swing.SwingConstants.LEFT);
			ivjCycleTimeJLabel.setPreferredSize(new java.awt.Dimension(147, 14));
			ivjCycleTimeJLabel.setFont(new java.awt.Font("Arial", 0, 12));
			ivjCycleTimeJLabel.setMinimumSize(new java.awt.Dimension(147, 14));
			ivjCycleTimeJLabel.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjCycleTimeJLabel;
}
/**
 * Return the CycleTimeJTextField property value.
 * @return javax.swing.JTextField
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JTextField getCycleTimeJTextField() {
	if (ivjCycleTimeJTextField == null) {
		try {
			ivjCycleTimeJTextField = new javax.swing.JTextField();
			ivjCycleTimeJTextField.setName("CycleTimeJTextField");
			ivjCycleTimeJTextField.setPreferredSize(new java.awt.Dimension(71, 20));
			ivjCycleTimeJTextField.setText("");
			ivjCycleTimeJTextField.setMinimumSize(new java.awt.Dimension(71, 20));
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjCycleTimeJTextField;
}
/**
 * Return the JLabelMaxTransmit property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getJLabelMaxTransmit() {
	if (ivjJLabelMaxTransmit == null) {
		try {
			ivjJLabelMaxTransmit = new javax.swing.JLabel();
			ivjJLabelMaxTransmit.setName("JLabelMaxTransmit");
			ivjJLabelMaxTransmit.setText("Max Transmit Time: ");
			ivjJLabelMaxTransmit.setMaximumSize(new java.awt.Dimension(147, 14));
			ivjJLabelMaxTransmit.setPreferredSize(new java.awt.Dimension(147, 14));
			ivjJLabelMaxTransmit.setFont(new java.awt.Font("Arial", 0, 12));
			ivjJLabelMaxTransmit.setMinimumSize(new java.awt.Dimension(147, 14));
			ivjJLabelMaxTransmit.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJLabelMaxTransmit;
}
/**
 * Return the JLabelMinutes property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getJLabelMinutes() {
	if (ivjJLabelMinutes == null) {
		try {
			ivjJLabelMinutes = new javax.swing.JLabel();
			ivjJLabelMinutes.setName("JLabelMinutes");
			ivjJLabelMinutes.setFont(new java.awt.Font("Arial", 0, 12));
			ivjJLabelMinutes.setText("min.");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJLabelMinutes;
}
/**
 * Return the JLabelSeconds1 property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getJLabelSeconds1() {
	if (ivjJLabelSeconds1 == null) {
		try {
			ivjJLabelSeconds1 = new javax.swing.JLabel();
			ivjJLabelSeconds1.setName("JLabelSeconds1");
			ivjJLabelSeconds1.setFont(new java.awt.Font("Arial", 0, 12));
			ivjJLabelSeconds1.setText("sec.");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJLabelSeconds1;
}
/**
 * Return the JLabelSeconds2 property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getJLabelSeconds2() {
	if (ivjJLabelSeconds2 == null) {
		try {
			ivjJLabelSeconds2 = new javax.swing.JLabel();
			ivjJLabelSeconds2.setName("JLabelSeconds2");
			ivjJLabelSeconds2.setFont(new java.awt.Font("Arial", 0, 12));
			ivjJLabelSeconds2.setText("sec.");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJLabelSeconds2;
}
/**
 * Return the JLabelSeconds3 property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getJLabelSeconds3() {
	if (ivjJLabelSeconds3 == null) {
		try {
			ivjJLabelSeconds3 = new javax.swing.JLabel();
			ivjJLabelSeconds3.setName("JLabelSeconds3");
			ivjJLabelSeconds3.setFont(new java.awt.Font("Arial", 0, 12));
			ivjJLabelSeconds3.setText("sec.");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJLabelSeconds3;
}
/**
 * Return the JTextFieldMaxTransmit property value.
 * @return javax.swing.JTextField
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JTextField getJTextFieldMaxTransmit() {
	if (ivjJTextFieldMaxTransmit == null) {
		try {
			ivjJTextFieldMaxTransmit = new javax.swing.JTextField();
			ivjJTextFieldMaxTransmit.setName("JTextFieldMaxTransmit");
			ivjJTextFieldMaxTransmit.setPreferredSize(new java.awt.Dimension(71, 20));
			ivjJTextFieldMaxTransmit.setText("");
			ivjJTextFieldMaxTransmit.setMinimumSize(new java.awt.Dimension(71, 20));
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJTextFieldMaxTransmit;
}
/**
 * Return the JTextField property value.
 * @return javax.swing.JTextField
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JTextField getJTextFieldOffset() {
	if (ivjJTextFieldOffset == null) {
		try {
			ivjJTextFieldOffset = new javax.swing.JTextField();
			ivjJTextFieldOffset.setName("JTextFieldOffset");
			ivjJTextFieldOffset.setPreferredSize(new java.awt.Dimension(71, 20));
			ivjJTextFieldOffset.setText("");
			ivjJTextFieldOffset.setMinimumSize(new java.awt.Dimension(71, 20));
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJTextFieldOffset;
}
/**
 * Return the JTextField1 property value.
 * @return javax.swing.JTextField
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JTextField getJTextFieldTransmitTime() {
	if (ivjJTextFieldTransmitTime == null) {
		try {
			ivjJTextFieldTransmitTime = new javax.swing.JTextField();
			ivjJTextFieldTransmitTime.setName("JTextFieldTransmitTime");
			ivjJTextFieldTransmitTime.setPreferredSize(new java.awt.Dimension(71, 20));
			ivjJTextFieldTransmitTime.setText("");
			ivjJTextFieldTransmitTime.setMinimumSize(new java.awt.Dimension(71, 20));
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJTextFieldTransmitTime;
}
/**
 * Return the OffsetJLabel property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getOffsetJLabel() {
	if (ivjOffsetJLabel == null) {
		try {
			ivjOffsetJLabel = new javax.swing.JLabel();
			ivjOffsetJLabel.setName("OffsetJLabel");
			ivjOffsetJLabel.setText("Offset:");
			ivjOffsetJLabel.setMaximumSize(new java.awt.Dimension(147, 14));
			ivjOffsetJLabel.setPreferredSize(new java.awt.Dimension(147, 14));
			ivjOffsetJLabel.setFont(new java.awt.Font("Arial", 0, 12));
			ivjOffsetJLabel.setMinimumSize(new java.awt.Dimension(147, 14));
			ivjOffsetJLabel.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjOffsetJLabel;
}
/**
 * Return the ImpliedDurationJLabel property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getTransmitTimeJLabel() {
	if (ivjTransmitTimeJLabel == null) {
		try {
			ivjTransmitTimeJLabel = new javax.swing.JLabel();
			ivjTransmitTimeJLabel.setName("TransmitTimeJLabel");
			ivjTransmitTimeJLabel.setText("Transmit Time: ");
			ivjTransmitTimeJLabel.setMaximumSize(new java.awt.Dimension(147, 14));
			ivjTransmitTimeJLabel.setPreferredSize(new java.awt.Dimension(147, 14));
			ivjTransmitTimeJLabel.setFont(new java.awt.Font("Arial", 0, 12));
			ivjTransmitTimeJLabel.setMinimumSize(new java.awt.Dimension(147, 14));
			ivjTransmitTimeJLabel.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjTransmitTimeJLabel;
}
/**
 * This method was created in VisualAge.
 * @return java.lang.Object
 * @param o java.lang.Object
 */
public Object getValue(Object o) {
	
	YukonPAObject pao = (YukonPAObject)o;

	//if there is no cycle time, forget the whole thing!
	if(getCycleTimeJTextField().getText().compareTo("") != 0)
	{
		//Add new exclusion timing information to the assigned PAOExclusion Vector
		Integer paoID = pao.getPAObjectID();
		Integer functionID = CtiUtilities.EXCLUSION_TIMING_FUNC_ID;
		String funcName = CtiUtilities.EXCLUSION_TIME_INFO;
	
		//CycleTime:#,Offset:#,TransmitTime:#,MaxTime:#
		StringBuffer exclusionTiming = new StringBuffer();
		exclusionTiming.append("CycleTime:");
		Integer cycleTime = new Integer(getCycleTimeJTextField().getText());
		cycleTime = new Integer(cycleTime.intValue() * 60);
		exclusionTiming.append(cycleTime);
		exclusionTiming.append(",Offset:" + getJTextFieldOffset().getText());
		exclusionTiming.append(",TransmitTime:" + getJTextFieldTransmitTime().getText());
		exclusionTiming.append(",MaxTime:" + getJTextFieldMaxTransmit().getText());
	
		System.out.println(cycleTime);
	
		PAOExclusion paoExcl = new PAOExclusion(
			paoID, functionID, funcName, exclusionTiming.toString()
			);
		
		pao.getPAOExclusionVector().add( paoExcl );

	}  
	return pao;
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
 * Initializes connections
 * @exception java.lang.Exception The exception description.
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void initConnections() throws java.lang.Exception {
	// user code begin {1}
	// user code end
	getCycleTimeJTextField().addCaretListener(ivjEventHandler);
	getJTextFieldOffset().addCaretListener(ivjEventHandler);
	getJTextFieldTransmitTime().addCaretListener(ivjEventHandler);
	getJTextFieldMaxTransmit().addCaretListener(ivjEventHandler);
}
/**
 * Initialize the class.
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void initialize() {
	try {
		// user code begin {1}
		// user code end
		setName("ExclusionTimingEditorPanel");
		setPreferredSize(new java.awt.Dimension(410, 360));
		setLayout(new java.awt.GridBagLayout());
		setSize(410, 360);
		setMinimumSize(new java.awt.Dimension(410, 360));
		setMaximumSize(new java.awt.Dimension(410, 360));

		java.awt.GridBagConstraints constraintsCycleTimeJTextField = new java.awt.GridBagConstraints();
		constraintsCycleTimeJTextField.gridx = 2; constraintsCycleTimeJTextField.gridy = 1;
		constraintsCycleTimeJTextField.fill = java.awt.GridBagConstraints.HORIZONTAL;
		constraintsCycleTimeJTextField.weightx = 1.0;
		constraintsCycleTimeJTextField.insets = new java.awt.Insets(25, 13, 17, 4);
		add(getCycleTimeJTextField(), constraintsCycleTimeJTextField);

		java.awt.GridBagConstraints constraintsCycleTimeJLabel = new java.awt.GridBagConstraints();
		constraintsCycleTimeJLabel.gridx = 1; constraintsCycleTimeJLabel.gridy = 1;
		constraintsCycleTimeJLabel.insets = new java.awt.Insets(28, 5, 20, 12);
		add(getCycleTimeJLabel(), constraintsCycleTimeJLabel);

		java.awt.GridBagConstraints constraintsOffsetJLabel = new java.awt.GridBagConstraints();
		constraintsOffsetJLabel.gridx = 1; constraintsOffsetJLabel.gridy = 2;
		constraintsOffsetJLabel.insets = new java.awt.Insets(20, 5, 20, 12);
		add(getOffsetJLabel(), constraintsOffsetJLabel);

		java.awt.GridBagConstraints constraintsTransmitTimeJLabel = new java.awt.GridBagConstraints();
		constraintsTransmitTimeJLabel.gridx = 1; constraintsTransmitTimeJLabel.gridy = 3;
		constraintsTransmitTimeJLabel.insets = new java.awt.Insets(20, 5, 20, 12);
		add(getTransmitTimeJLabel(), constraintsTransmitTimeJLabel);

		java.awt.GridBagConstraints constraintsJTextFieldOffset = new java.awt.GridBagConstraints();
		constraintsJTextFieldOffset.gridx = 2; constraintsJTextFieldOffset.gridy = 2;
		constraintsJTextFieldOffset.fill = java.awt.GridBagConstraints.HORIZONTAL;
		constraintsJTextFieldOffset.weightx = 1.0;
		constraintsJTextFieldOffset.insets = new java.awt.Insets(17, 13, 17, 4);
		add(getJTextFieldOffset(), constraintsJTextFieldOffset);

		java.awt.GridBagConstraints constraintsJTextFieldTransmitTime = new java.awt.GridBagConstraints();
		constraintsJTextFieldTransmitTime.gridx = 2; constraintsJTextFieldTransmitTime.gridy = 3;
		constraintsJTextFieldTransmitTime.fill = java.awt.GridBagConstraints.HORIZONTAL;
		constraintsJTextFieldTransmitTime.weightx = 1.0;
		constraintsJTextFieldTransmitTime.insets = new java.awt.Insets(17, 13, 17, 4);
		add(getJTextFieldTransmitTime(), constraintsJTextFieldTransmitTime);

		java.awt.GridBagConstraints constraintsJLabelMaxTransmit = new java.awt.GridBagConstraints();
		constraintsJLabelMaxTransmit.gridx = 1; constraintsJLabelMaxTransmit.gridy = 4;
		constraintsJLabelMaxTransmit.insets = new java.awt.Insets(20, 5, 156, 12);
		add(getJLabelMaxTransmit(), constraintsJLabelMaxTransmit);

		java.awt.GridBagConstraints constraintsJTextFieldMaxTransmit = new java.awt.GridBagConstraints();
		constraintsJTextFieldMaxTransmit.gridx = 2; constraintsJTextFieldMaxTransmit.gridy = 4;
		constraintsJTextFieldMaxTransmit.fill = java.awt.GridBagConstraints.HORIZONTAL;
		constraintsJTextFieldMaxTransmit.weightx = 1.0;
		constraintsJTextFieldMaxTransmit.insets = new java.awt.Insets(18, 13, 152, 4);
		add(getJTextFieldMaxTransmit(), constraintsJTextFieldMaxTransmit);

		java.awt.GridBagConstraints constraintsJLabelMinutes = new java.awt.GridBagConstraints();
		constraintsJLabelMinutes.gridx = 3; constraintsJLabelMinutes.gridy = 1;
		constraintsJLabelMinutes.ipadx = 21;
		constraintsJLabelMinutes.insets = new java.awt.Insets(28, 4, 20, 109);
		add(getJLabelMinutes(), constraintsJLabelMinutes);

		java.awt.GridBagConstraints constraintsJLabelSeconds1 = new java.awt.GridBagConstraints();
		constraintsJLabelSeconds1.gridx = 3; constraintsJLabelSeconds1.gridy = 2;
		constraintsJLabelSeconds1.ipadx = 21;
		constraintsJLabelSeconds1.insets = new java.awt.Insets(20, 4, 20, 109);
		add(getJLabelSeconds1(), constraintsJLabelSeconds1);

		java.awt.GridBagConstraints constraintsJLabelSeconds2 = new java.awt.GridBagConstraints();
		constraintsJLabelSeconds2.gridx = 3; constraintsJLabelSeconds2.gridy = 3;
		constraintsJLabelSeconds2.ipadx = 21;
		constraintsJLabelSeconds2.insets = new java.awt.Insets(20, 4, 20, 109);
		add(getJLabelSeconds2(), constraintsJLabelSeconds2);

		java.awt.GridBagConstraints constraintsJLabelSeconds3 = new java.awt.GridBagConstraints();
		constraintsJLabelSeconds3.gridx = 3; constraintsJLabelSeconds3.gridy = 4;
		constraintsJLabelSeconds3.ipadx = 21;
		constraintsJLabelSeconds3.insets = new java.awt.Insets(20, 4, 156, 109);
		add(getJLabelSeconds3(), constraintsJLabelSeconds3);
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
		ExclusionTimingEditorPanel aExclusionTimingEditorPanel;
		aExclusionTimingEditorPanel = new ExclusionTimingEditorPanel();
		frame.setContentPane(aExclusionTimingEditorPanel);
		frame.setSize(aExclusionTimingEditorPanel.getSize());
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
	YukonPAObject pao = (YukonPAObject)o;

	Vector exclusionVector = pao.getPAOExclusionVector();
	PAOExclusion exclusionTimeInfo = null;
	   
	for(int j = 0; j < exclusionVector.size(); j++)
	{
		PAOExclusion temp = (PAOExclusion)exclusionVector.elementAt(j);
		if(temp.getFunctionID().compareTo(CtiUtilities.EXCLUSION_TIMING_FUNC_ID) == 0
			&& temp.getFuncName().compareTo(CtiUtilities.EXCLUSION_TIME_INFO) == 0)
			{
				exclusionTimeInfo = temp;
			}
	}
	
	if(exclusionTimeInfo != null)
	{
		//CycleTime:#,Offset:#,TransmitTime:#,MaxTime:#
		StringTokenizer timeInfo = new StringTokenizer(exclusionTimeInfo.getFuncParams());
		Integer cycleTime = new Integer(timeInfo.nextToken("CycleTime:,"));
		String offset = timeInfo.nextToken("Offset:,");
		String transmitTime = timeInfo.nextToken("TransmitTime:,");
		String maxTime = timeInfo.nextToken("MaxTime:,");
	
		cycleTime = new Integer(cycleTime.intValue() / 60);
		getCycleTimeJTextField().setText(cycleTime.toString());
		getJTextFieldOffset().setText(offset);
		getJTextFieldTransmitTime().setText(transmitTime);
		getJTextFieldMaxTransmit().setText(maxTime);
	
	}
}

public boolean isInputValid() 
{
	Integer cycleTime = new Integer(getCycleTimeJTextField().getText());
	cycleTime = new Integer(cycleTime.intValue() * 60);
	Integer offset = new Integer(getJTextFieldOffset().getText());
	Integer transmitTime = new Integer(getJTextFieldTransmitTime().getText());
	Integer maxTime = new Integer(getJTextFieldMaxTransmit().getText());
	
	if(offset.compareTo(cycleTime) > 0 || transmitTime.compareTo(cycleTime) > 0 
		|| maxTime.compareTo(cycleTime) > 0 )
	{
		setErrorString( "Offset and transmit times cannot be greater than the cycle time." );
		return false;
	}
	
	return true;
}
}
