package com.cannontech.dbeditor.wizard.copy.lm;

/**
 * This type was created in VisualAge.
 */

import com.cannontech.common.gui.util.TextFieldDocument;
import com.cannontech.core.dao.DaoFactory;
import com.cannontech.core.dao.PaoDao;
import com.cannontech.database.data.device.lm.IGroupRoute;
import com.cannontech.database.data.device.lm.LMGroup;
import com.cannontech.database.data.lite.LitePoint;
import com.cannontech.yukon.IDatabaseCache;

/*
 * @author jdayton
 * GUI NOTE: Websphere GUI builder over a previous Visual Age code shell
 */
public class LMGroupCopyNameRoutePanel extends com.cannontech.common.gui.util.DataInputPanel implements java.awt.event.ActionListener, javax.swing.event.CaretListener 
{
	private javax.swing.JLabel ivjJLabelName = null;
	private javax.swing.JTextField ivjJTextFieldName = null;
	IvjEventHandler ivjEventHandler = new IvjEventHandler();
	private javax.swing.JComboBox ivjJComboBoxRoutes = null;
	private javax.swing.JLabel ivjJLabelRoute = null;

	class IvjEventHandler implements java.awt.event.ActionListener, javax.swing.event.CaretListener {
		public void actionPerformed(java.awt.event.ActionEvent e) {
			if (e.getSource() == LMGroupCopyNameRoutePanel.this.getJComboBoxRoutes()) 
				connEtoC1(e);
		};
		public void caretUpdate(javax.swing.event.CaretEvent e) {
			if (e.getSource() == LMGroupCopyNameRoutePanel.this.getJTextFieldName()) 
				connEtoC2(e);
		};
	};
/**
 * Constructor
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
public LMGroupCopyNameRoutePanel() {
	super();
	initialize();
}
/**
 * Method to handle events for the ActionListener interface.
 * @param e java.awt.event.ActionEvent
 */
public void actionPerformed(java.awt.event.ActionEvent e) {}
/**
 * Method to handle events for the CaretListener interface.
 * @param e javax.swing.event.CaretEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
public void caretUpdate(javax.swing.event.CaretEvent e) {
	// user code begin {1}
	// user code end
	if (e.getSource() == getJTextFieldName()) 
		connEtoC2(e);
	// user code begin {2}
	// user code end
}
/**
 * connEtoC1:  (JComboBoxOperationalState.action.actionPerformed(java.awt.event.ActionEvent) --> LMGroupBasePanel.fireInputUpdate()V)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC1(java.awt.event.ActionEvent arg1) {
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
 * connEtoC2:  (JTextFieldName.caret.caretUpdate(javax.swing.event.CaretEvent) --> LMGroupBasePanel.fireInputUpdate()V)
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
 * connEtoC3:  (JCheckBoxSeasonChooser.JCheckBoxSeasonChooser.JCheckBoxFallAction_actionPerformed(java.util.EventObject) --> LMGroupBasePanel.fireInputUpdate()V)
 * @param arg1 java.util.EventObject
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC3(java.util.EventObject arg1) {
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
 * connEtoC4:  (JCheckBoxSeasonChooser.JCheckBoxSeasonChooser.JCheckBoxSpringAction_actionPerformed(java.util.EventObject) --> LMGroupBasePanel.fireInputUpdate()V)
 * @param arg1 java.util.EventObject
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC4(java.util.EventObject arg1) {
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
 * connEtoC5:  (JCheckBoxSeasonChooser.JCheckBoxSeasonChooser.JCheckBoxSummerAction_actionPerformed(java.util.EventObject) --> LMGroupBasePanel.fireInputUpdate()V)
 * @param arg1 java.util.EventObject
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC5(java.util.EventObject arg1) {
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
 * connEtoC6:  (JCheckBoxSeasonChooser.JCheckBoxSeasonChooser.JCheckBoxWinterAction_actionPerformed(java.util.EventObject) --> LMGroupBasePanel.fireInputUpdate()V)
 * @param arg1 java.util.EventObject
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC6(java.util.EventObject arg1) {
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
 * connEtoC8:  (JComboBoxHoliday.action.actionPerformed(java.awt.event.ActionEvent) --> LMGroupBasePanel.fireInputUpdate()V)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC8(java.awt.event.ActionEvent arg1) {
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
 * 
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private static void getBuilderData() {
/*V1.1
**start of data**
	D0CB838494G88G88GA7ED98B1GGGGGGGGGGGG8CGGGE2F5E9ECE4E5F2A0E4E1F4E14DBB8DF8D45515F4175A38E20B0ADF51527D90290DB6D6940414C68DACA8E85020A081D18264DB690AAD36E879DC7AF1B3B3890990C8C854A0FF23028BCB888950B59BA1A4A4E00291D388B15AFC5517191719C95E4C9BE6DE1819C8F54F39BF6F3DCCE69262377DBE73FDE76E3B675CFB6EB9671EFB6E39773D1012BD63F332ADA504E467907E77FEB6A1499F909232CB4785AEF1124DC6CCFFEF839411
	A3B508CC86DA428BB6DBA1F969E09E545320BEB64BE6FB931E0B49AB87D30F42C31CF14682ED4204298F8EBE4ECF74F1B652FA4831994AADG6900DB8BD0FBAF0E7FA3C7A94717C2FD8BFFC6483690E23B8F5BFC65ADB561C772F699D02E86A013314DDD0E1D1C5E8CFD1CGA9G697731362B204C2752FD3F3A0A376B38A1112CFD741C25G47B2719750B81975D5F8E9A7CFD3C3D84942DD0DD09EEF69F155B8D4EF39C37279D41FA6BB5C654ECE59695154C0F94853A7851C65CE793247A117ABDEFFC0F585A4
	EFF5F57347AD6C71149414DBA41F2CAC25725F664D233C891988F5779444ED77214D6C70DC8550B2C568B03F60B817F1CCCFA2493C3DAE2E8EC9D3186C69D38C9DDA7F7434F5447D487FA9BAA7582F906A0DGCC3F2736FDF4157AB9D4FF8434B329D58F18F312D73EA0EB1A47678AD2958953F107D368B86BFE216365EE14E1BD54CB265AECE5D3857EC48F629B205E8120CD953A1F78AF21FB7BD753483A4ECB162DF1F49FB51569BCE52A217B3174E5395B29BFEA927A549A2015CC33597683B483A8G23GF6
	50F53022663F212C6B546AD52FD77555797D2794E760E355A9AB70DCDD8D940EBBAFBB54001390666BF7F84BE248A3E062347EFE0E40F47B14885FCCBA7FCB22E9AECBCE8C5D26ACEC31A4F39EE9538C5D721251EE57D31FD98D78EC4E1761EF257866E4834FFCE08F45E35D8D3434E4E16776ADC216E65B84D2D89934EC0CA14B5819EECB452F188C0D3FB2E4E1B6BEC75736867851G09G29G19GE43A30F1594923C3593889FCAF6013148B3D1DFEA9201D6A75A8CEB940ED3D58B1182DE3816B777C307BB1
	3BA49EB54F5152490118F6D91A7829E567F4E60FE3538D3B30316D4EF8E337CE9F689BDB74B97C8F3AD65C53599CDAF57CDA0A8F1B70ECEE2FD0BC569300369A00353F172E2D16DF3376B9BA1F940AF71A700CCFBD45E3BD8C3425GECFED7BBB1665626409A8550GC681A4814C81D81AA266B8F47270D52E2396537C323EFF175E00D28EEB8129499754A41FC3EE13829E29D31103043739C6C23BF6766BF79DDD5F8E9863C359A787A44D237A00BBC4A6F026A0EBB3C6AA42B68119D2DBBDDE9986707A0960BB
	CEAA06D211025AC53FD352E4238523CF1450A7AECBED40919840F35983E82F87E8ACDF8F7A17BEA0EC353D8BF1FE28A7BCA06C3227F39B149F01F34BCAF5F5034701E2C8010870511BFC05D00E0376FB9F34594681D4BCA45667EF5B059FB6945CCD4A96DE0A6907B4DEBC44E345C351FEB81E72DF8B7812D41B6D98C0EB2A582F77795AF97F0A07E27BA44A05B2097A5E87E3370B3F86EB74B1726334337236221EB30C754A6C7E83977A2E86FA0D861814AAEC7FF9271D79A46E43428BBF89008349CEE3A6D85B
	CF9D38474817E51F56A07BE51F939E025C877E6648B77C33CB55FD47C971982F6A410102BA3E5BD1447040436C6D029E20F4C953821E4EDECD5FC6F5FA9F15A558AF9486392E573AD157B920E3A6409ADDD72B335864E74D2A5A536BEF4595447D75FD3AAF5BE658ECBBE70879DF530D79DB2DDEFF15568D7ABFF50B399B8A1047F0622C619FBF396AB12C4364EF0CB12D87F597F11361E6D21C82F20F5175ED6AE9B7359B6ED8D7A7CBA79D43BACCBF3FDAE03E700AB33EAF7F49995B172DF2ECDF4E17877755A2
	B9162FB2DA290CBA2AA11F4977F749988F9D2A4FA7538528F7A1ACAF7DEDAB6A6171F934CFBDCE172CDD70744904E44DC09FDEC0E3EE8ABC274F90B1B2742E0811FB7B6EA429DFB4474C27467E2C4732FFA6735163B30D9849E45A5D05EBCAD6E4AF982ADE6D75E98C3F1746F30E8FDAA92E31348F7B6EBF8B7BDC27FACA8DFF6267BB8FA4AF9AECA0E592552B1C95ADF220CB8DF8E5677EEE6932D4AE0534F2BAAF65F514FC9A1F8F588F9D2A318FBCB414F5F982F2134F5F4B37267D875A8ED4B05FA9A645A795
	A6C7AB4C65990F2CB8B1A3AFF3C050514A69AF6F0463064B03A118B5BEF4BDD207BDF2A59B3739631AFF307EB1CF97035AE2E2CE787F5D96D7ADC5A50B4BA99E74218E70ADBFC042CC911747F7A1DD44E53E79542B0A425C9FE82BE7D25F7615187CF57CCCF6FE5E225309BAB83DEC88FAFBDCBA1373FA976689C179D22F8C6997EE9C9F499111C33936C5515AA42557484D4639B6E1ACB56DB98C7FA3F76CF5F5339B77199E1D7FAD6EDC02B99663496240A489F97A839EAFEC51380972FB02F3CE73BD4131FF03
	7C6BDE48CB21CCGE07EF067D73B219C6238CA2AE6513C3AC75CA90C077E49G991CE769ACE69F56AED9916DC6C2FD9CC04A2C7E6D5651F6AF68E77C43BA4E62C35C6FE0837829CD1CB1BEAEB45FD1B8034E18F10B462B590CFF4B6C683357CB5DB45F847C0859425F0ABD66BC20AE98D49D9E3ABC791C4C7418E39A43BD62F98DCA706DA01C431CF2D84C6BBC8F4A0A39B1C7733979454193E4E02EFF1A5E9A08B42BBEC4D7D52BDA8F69F9A338C63D9E725BBCF20888BB7C7D0C791EA3E56BE716CDF16CB0066B
	BF45E407F5CFAC4BE5B25DD20DE74CD877998EAAE4392B57D35E2BF9B08917B409AEE2733DC5B24D0F10575A5962FEA2016615E58F5BEC8DG9DG6C2C34CB91E7A58DF0C9GEC0D4DBBE1DEE3D98F33DCEB73A342AFC7AAC7209CFEFC94F61FF5E09B1B272898A4ECB92138160F1D764B896478B755B16FCD9236B6D86C0F70737CA306AD47E4BD1E1B8840F4DC2E6BB8865A24BDA2F45C9CB16BE81B43F4AC1BA3F4EC74225F8FB3740A3B11A366B5111A599EF7CD544EE1F2374E09DE938B953AA6GBFF20ED8
	930D5DC526B5F19E3CD07239A0CA6979D2371545A3DEFF35BBC744A3A24E983BE8FC9E8BBC27G2481E48158660AB13E57B3649D476970A0F79C8F8C38AF58BB5748292B668E4C2F59F93E14465866396CBCDF24638FD0FC5804E7737AC36A53D81F88340AC7C56E7B2E92BDFE4A237D474C082A33FE15837A15C75557C775E33E6CD2F60875EAF20D6AEABD2F606B52D6A362E949E3621C9DD4B196B7C35D8DB082E09CC0AA40D2G12A666A43DA7D61C6ABFA7710214F1E663F7C95F52BEAF8FE99FFBDAAC7B6C
	5B3FB336FD749C075927BEA262D6D51A304FE4BFDD83D09F215B6210D2E0DA83AD2A4727697E3FC4417DD78DF8DC9E1F40AD23B8079A2049BB436D7345BFD71479E21FAB0E78E21FAB2AFC031F11EBFD714E9D6CBC3B00466B51205FC4G067B230C38B9D04FCC93FE3D5BED0752B68F6294C095C08740C80071G29GD973C45B43346D6647597EDE76B84B871A21D4GC681A4BDAE5A0EF10D6073057D07BA17D571B14D381DDC06F8FDEC1C164A4B0E98ED7979D61725E888B9C41FCDD1B8741F02C17A087370
	D6DE463A4B59C55B4DFDE3AB1D57E0D08E5245053BAD27BD2F3DCEE21C7DB0B7A3A2177FDC4967EDCEEBEAFD252EE3FE4DE0F739E90F8F348D3FDBD4637B6CC8B5364F7E0B0A36882A3D81079C6D17B72825BA4D0896CD1A6CC5B5D81BDBE9FFCD8A4079B53A7FED34BF23456BFF179A4F77190DF6FB4DB6BA225B28B102B64A0CE18B0435F1ED146C47BDA22C57D3E8BD220F770293E301A2C554DE889A9DCF501CD6B66761F9002BGE8F8020D41E2583DA17433AEC841C17C8BFE59C12A52316F16AE51F7A234
	CF8648GD887D01AAE720873A14CC94FB6CB1D32429C0461176D45FE7DCFFA3A1CFB5E507BC49FCBC51B339598A3631EDEF51D2FAFAD54F941BC8541C7A0E4B25A6A16E2B38F034C7B96DDFC13525D322307527D11E6D5F2FE98D0FB7DC15EA6B77B2D68B69F4B5ECEB9G5C34002A08F31B4BED4E37C62433FCEBE23A48477E920CC5CF5669D3AF4722276974EB55D8B9EB26CE6F38E8262F657DF74E977408B5963DE23E0849769A91139BF45C97F44F9A8375A49D77F0C434CB1EAF5ED1D6FE59EF8F446C383A
	7A430047F9CAF2B5D3EFE4FB60FED95C6F65BDA9F277B699731CDA283783781F9477C5617744F9A3690E8924F9C4A066FB554C564FAC89CFB2DFCEFA5248B19B53176532B1EF6C92E32E823A7DA94425F619ED0173DCE4E57BBD237F3E9F9D57B846C5033E57E526233CABADE6FADBBFBA5A68906F4FF4FBEC1F50ED7DE31308F27F17F15F993B1FE2BA0D62E5EDF6C2767ADB1F60E729BF507599A377202B01697F2F6EBCBE6EAC6041F089C101DB2F63DE3C0CB80C83479688DC2A1A47E31632C04CF5CDDB3CFC
	102F901E8F5EF4D10CB1D167B749AAF0CB815738105EF757B444505B3C876668F18AF2CB68339F60F50EBB8E4F0F9163AE3818DC0E208E1A2AAA2D9EBFC69FB2FAA1FE93D10CE11330B11F0D8CB62698EBA3AF97C40D398A1E73ADECCC964F8F4943FDFF49F27FC7296F2FDD48F27FEC9D7F944513278DBCBB93F448629B014DC073BFAD7248D74268878B203E86A0EF9178B9C0ADC047A211D756DD2AA0C364582D2A3F99B6E0451466B17DFE5C756D5E4F4E58A87CFC4A743BC872B57638F7867EC54C3FC773B2
	E3438F3231646FD27AD03FD420AD85A099307FG1481B4E4887D2E3DD44674436B0DBACD139CEEFAF3A214434B8C830D1B9C536DF637D937768C7E1D89AFC770F2CC0631A733FE57874D7D92B9BD0517693C4C9A506F77174D7D56F17A6645BCF6C1F98C20F1F1F43F592A685731F8E0FE60875C4845314EB5E3AFE23F311C16444BD4DEAE08410B753B5F0A7DD6F17AFADE96AEE1655EA5837B6D2173GBECC445F1A1383F1042F476EABC47713651B051F6C7C456D24E1115D72EE9CBF296263A6ACB1E27D9532
	484A78BC71D7733DC4612D0D166CB8FCD2F97FB4931FD872B3D937FFCDF43B244B4673E253F327FC356F473E8D30B13E7A070E41565106F07CBCBCB792BB8F5F9C09FDF65C96997C1D54DB11786F245E0E185FC931B8701B2FF1DFA54FC06E82D08B508A60FEC64400FBA26F909A83E22C7F53CE17CC93C0898F542C7D0DFD71F53D25EF68EF9DA674E16E5B29EA1A6AC57E821F5807732529FE0AE4383B7B50C6F0F4F7EB2CA947CF6E236FB765AE0AE6B956F43AE70F843D46BCA372AEE7A96292211E2C636616
	0ADCEC160E7BF998F1A5CBA10EAC953809C1412FD1471594A02E836A0A0E0B9488FEE19DF76309017C72C3349D54B3F41C1762D6C3FD3D0EFBB2A47865BDABF09F533C209D6AFE9DF7CE9DB846D20A9B89ED466B6DE63689FED3F4DC35C65F17BF87BE711C405DC3472800FA030EDB4746053ADB47DD4CC15C95280F52F153F34498E3F5DC1AD570DB25630AEC025FFA9DB759A97895AE536D2C5FF56CDCA6766B8F89669F8A5493G12GE681E4G2C8130E5427E86D08650G508160879889B096A091A085A0BD
	D3777BAB8720EC133A875E708312A76226530FD8CFF7E3E864FE787215FD71F8406AB98B295EA71D5DF088EE15DCFA1F173E0E3FFE56FDBD747A497E063E63G3FEF0D787919BA779BFA475555951475F53A691BC29D5794ECD1039EBC8932FCEFBEFDC7558507B17BF2347773F46DE401ED56E40AB36AD72558C683E1D8BCFF8B51BC974CAC9DAC9734C641FED3AE0846F3AEE74F7F39E14386B65603E15CC35CEA405387B2C9CA1D62F179B09291A6DFC6D85C837A214EF073687A9C874F094B054C878B7A4BFC
	42E648BC3AE0A8194DB9F3344C994B595D5886DDE6FB283F8D7F072E5BACF8DE234BB3B254DF1EE9B906BC3F898DA54FC6E26453517258D758EC189796901143BBAFE25DAB05BD5EDEAF7D300049F5052E61B2E8DF35C2487587FA07638F48DDF2A0A0BB59E7883C7DE6FA3F6A04367E95623C3138EDB0D973CDF2A20CDB51BF771F344248495978FF51282C9EDF142C9E9A17D2207D9CDD5607297CC1995FA94BCE566E590B625EFC2D5EEEEBDB7F7958C7631EEDA5BC2F94ED141C7E7331A457180F79B9C34DC7
	BE07D873D18BE3E0AC78A378879B7FE52BB8C3B72F94BEDBCD635F17D0D7G188C5B728C998E4AC34930E99099462DE477GA8830B81FFA571E3CDA799BA56741058FB75A5B2F02F3ECCE26F55D708F92F666F1328DF38A046366B6D2E51F11F69386BA8CF44FD6AF1EAEE9DFF83158B710DB28EA870BF26F205A82EDD3C3FB761BE93771484A79ED40610A37A5EC1287FF894055FD352B69E9F04CF5D9E77D099BDFE49A477949D07F233B96D406545FD3D3D9473AB1F9C326BAFC14F00136EFF476911945575CE
	193D224CDEF96C00486B47074C79F855EBFF0B7B1EB0F1A5BFBF71B269C8C2B69BFF53D6E37C5F05B1C7B73F213C003FA22D8B16B71FBE534A653E2F486817D7F035F2333E8B7175ADFF2EFC5D60A305064727758D034F66378CBE756A1EE8BE8D9E58A37043557D15C7AB4B6CC70E9E607D8E6FB07ACDEB9B245F110AE32CDF7CF34C791D06BC3D3B8C3EA7AFDE8DDF56F66DC59AF7823DB26333E60F4127A9E7F87639743651F7B33DDF6A1214202C4BF8FE2FC17F39F3F83CC7E08863F94E21775096EBB1FEE6
	091C69360203A4FE4E5492D07DF2C00BB0AC7D0AC63CA7FB8F7DD3BF53B092277D6FFBA504FD9387B2F97A68FD7585E868F3317F65297AE0B9AC442497C90EC636620538DD6D32730FDF028132D5572C09AE554C4BC72B2DABEF37DA082D0964325055C4EC98FF1AC88E8DCFCD448A21AB6C6753AD1656EB41CB64EBDA4360E2A1ECF3FDDFA379674000B8F235C636196E546D74D29D10A5DC9C3B11AED65BB5D208775EF63C9C37335BF10D94D3943B8B37734BF0D022205F744C3E6D4F7F1E76DCDB50CAF21B48
	F65C51AE4881EA9C5909AF1C78575AE7C83E9F5FF4627E899A86E9119354A20A5CCCCA3A4E6397CDC158B26B0459E17C571BF1244378294861C6A96856B05CFF50CCECBE7C2E1CECE3E4C572398E33B978001AA93B6ECB3AA650CC8464AD2672A217F21DCB712F376EFB7BECD63215D84E105794507A0C64GBFE8A2791ABBA0875D2A628C7BCF56D2C6F9A1F1F1EA85F6BBF475D159F23C0054056E897FF4D4412A02B3B09F61GB9CCDB6286DE2E37F0563B25G8CA5871AB41C9BB530D727FDEF37765E3C8F5F
	6C3010EC2EA44E712630DFE386539D88D5AEBF83D2EBCBDE1CBB611963EBD6C317B3A407FEA78183979A2D4E75626CDD13F4732ECDE3B22FC3397CE89E6ABDDBB5125BA5F9BDCA849E9A68A29378FCF08CDF903F0701C16DD19A8B12AEF879CDF3ABEE3E73554F2960C6D70D6CE1D384DD8F3BE455FB382E2B4B6313F586B0DF40FE174E9E5B14C73559963E6175338BB7F402FFB5902B3722027ED6525FA378DBC9B11594D3F984A6ED8D9974AF1A9E8E234E25E1FDC109AC911F5F27587C81DE055802016E0168
	A2CAAC4666787006EEDB2CED27449D74F7773713F42FA1E981917B3088885FA9C98F9A12EEA4C6A6188FF33EED60B6D47743450B123EE8A831908B44C2374C02DFCE88F3A93A2EB1427EB365F8185752F47E02B71D3B98ED3ED3F6F95F300B0D4BBE9BED745D1334461074E7DC5299041D13185F9D78461C7F2D7B7E493879DF5A8BAC6F4BFCA17ABB3183A7D1271D2B614C37DA6C5D119AFC1F966F2E6202D7D294B8F97754791CF8EFF1AA2249461DE379C9649F94EDB811517EFC92656EB67579BFD0CB87887B
	4D91913195GG60BBGGD0CB818294G94G88G88GA7ED98B17B4D91913195GG60BBGG8CGGGGGGGGGGGGGGGGGE2F5E9ECE4E5F2A0E4E1F4E1D0CB8586GGGG81G81GBAGGG6B95GGGG
**end of data**/
}
/**
 * Return the JComboBox1 property value.
 * @return javax.swing.JComboBox
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JComboBox getJComboBoxRoutes() {
	if (ivjJComboBoxRoutes == null) {
		try {
			ivjJComboBoxRoutes = new javax.swing.JComboBox();
			ivjJComboBoxRoutes.setName("JComboBoxRoutes");
			ivjJComboBoxRoutes.setPreferredSize(new java.awt.Dimension(204, 23));
			ivjJComboBoxRoutes.setMinimumSize(new java.awt.Dimension(204, 23));
			// user code begin {1}
			ivjJComboBoxRoutes.addActionListener(new java.awt.event.ActionListener() { 
				public void actionPerformed(java.awt.event.ActionEvent e) {    
					fireInputUpdate(); 
				}
			});
			
			ivjJComboBoxRoutes.setVisible(false);

			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJComboBoxRoutes;
}
/**
 * Return the JLabel1 property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getJLabelRoute() {
	if (ivjJLabelRoute == null) {
		try {
			ivjJLabelRoute = new javax.swing.JLabel();
			ivjJLabelRoute.setName("JLabelRoute");
			ivjJLabelRoute.setFont(new java.awt.Font("dialog", 0, 14));
			ivjJLabelRoute.setText("Communication Route: ");
			ivjJLabelRoute.setMaximumSize(new java.awt.Dimension(131, 23));
			ivjJLabelRoute.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
			// user code begin {1}
			ivjJLabelRoute.setVisible(false);
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJLabelRoute;
}
/**
 * Return the SelectLabel property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getJLabelName() {
	if (ivjJLabelName == null) {
		try {
			ivjJLabelName = new javax.swing.JLabel();
			ivjJLabelName.setName("JLabelName");
			ivjJLabelName.setFont(new java.awt.Font("dialog", 0, 14));
			ivjJLabelName.setText("New Name:");
			ivjJLabelName.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJLabelName;
}
/**
 * Return the JTextField1 property value.
 * @return javax.swing.JTextField
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JTextField getJTextFieldName() {
	if (ivjJTextFieldName == null) {
		try {
			ivjJTextFieldName = new javax.swing.JTextField();
			ivjJTextFieldName.setName("JTextFieldName");
			ivjJTextFieldName.setToolTipText("Name of Group");
			// user code begin {1}

			ivjJTextFieldName.setDocument(
					new TextFieldDocument(
						TextFieldDocument.MAX_DEVICE_NAME_LENGTH,
						TextFieldDocument.INVALID_CHARS_PAO) );
			
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJTextFieldName;
}
/**
 * getValue method comment.
 */
public Object getValue(Object o) 
{
	LMGroup group = (LMGroup)o;
	int previousGroupID = group.getPAObjectID().intValue();
	
    PaoDao paoDao = DaoFactory.getPaoDao();
	group.setPAObjectID(paoDao.getNextPaoId());
	
	group.setPAOName( getJTextFieldName().getText() );
	
	//only set the route ID for certain LmGroups
	if( group instanceof IGroupRoute)
	{
		((IGroupRoute) group).setRouteID( 
			new Integer(((com.cannontech.database.data.lite.LiteYukonPAObject)getJComboBoxRoutes().getSelectedItem()).getYukonID()) );
	}
	
	com.cannontech.database.data.multi.MultiDBPersistent objectsToAdd = new com.cannontech.database.data.multi.MultiDBPersistent();
	objectsToAdd.getDBPersistentVector().add(group);
	
	LitePoint[] litPts = DaoFactory.getPaoDao().getLitePointsForPAObject( previousGroupID );
	int startingPointID = com.cannontech.database.db.point.Point.getNextPointID();
	
	for(int c = 0; c < litPts.length; c++)
	{
		com.cannontech.database.data.point.PointBase pointBase = (com.cannontech.database.data.point.PointBase) com.cannontech.database.data.lite.LiteFactory.createDBPersistent(litPts[c]);
		try
		{
			com.cannontech.database.Transaction t =
				com.cannontech.database.Transaction.createTransaction(com.cannontech.database.Transaction.RETRIEVE, pointBase);
			t.execute();
		}
		catch (com.cannontech.database.TransactionException e)
		{
			com.cannontech.clientutils.CTILogger.error( e.getMessage(), e );
		}
		pointBase.setPointID(new Integer(startingPointID + c));
		pointBase.getPoint().setPaoID(group.getPAObjectID());
		objectsToAdd.getDBPersistentVector().add(pointBase);
	}	
	
	return objectsToAdd;
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
 * @exception java.lang.Exception The exception description.
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void initConnections() throws java.lang.Exception {
	// user code begin {1}
	// user code end
	getJTextFieldName().addCaretListener(ivjEventHandler);
}
/**
 * Initialize the class.
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void initialize() {
	try {
		// user code begin {1}
		// user code end
		setName("LMGroupCopyNameSettingsPanel");
		java.awt.GridBagConstraints consGridBagConstraints1 = new java.awt.GridBagConstraints();
		java.awt.GridBagConstraints consGridBagConstraints2 = new java.awt.GridBagConstraints();
		java.awt.GridBagConstraints consGridBagConstraints3 = new java.awt.GridBagConstraints();
		java.awt.GridBagConstraints consGridBagConstraints4 = new java.awt.GridBagConstraints();
		consGridBagConstraints1.insets = new java.awt.Insets(60,20,19,10);
		consGridBagConstraints1.ipady = 5;
		consGridBagConstraints1.ipadx = 10;
		consGridBagConstraints1.gridy = 0;
		consGridBagConstraints1.gridx = 0;
		consGridBagConstraints4.insets = new java.awt.Insets(17,2,244,28);
		consGridBagConstraints4.ipady = 5;
		consGridBagConstraints4.fill = java.awt.GridBagConstraints.HORIZONTAL;
		consGridBagConstraints4.weightx = 1.0;
		consGridBagConstraints4.gridy = 1;
		consGridBagConstraints4.gridx = 2;
		consGridBagConstraints2.insets = new java.awt.Insets(60,10,16,28);
		consGridBagConstraints2.ipady = 7;
		consGridBagConstraints2.ipadx = 260;
		consGridBagConstraints2.fill = java.awt.GridBagConstraints.HORIZONTAL;
		consGridBagConstraints2.weightx = 1.0;
		consGridBagConstraints2.gridwidth = 2;
		consGridBagConstraints2.gridy = 0;
		consGridBagConstraints2.gridx = 1;
		consGridBagConstraints3.insets = new java.awt.Insets(17,20,247,2);
		consGridBagConstraints3.ipady = 6;
		consGridBagConstraints3.ipadx = 9;
		consGridBagConstraints3.gridwidth = 2;
		consGridBagConstraints3.gridy = 1;
		consGridBagConstraints3.gridx = 0;
		setLayout(new java.awt.GridBagLayout());
		this.add(getJLabelName(), consGridBagConstraints1);
		this.add(getJTextFieldName(), consGridBagConstraints2);
		this.add(getJLabelRoute(), consGridBagConstraints3);
		this.add(getJComboBoxRoutes(), consGridBagConstraints4);
		setSize(412, 392);

		initConnections();
	} catch (java.lang.Throwable ivjExc) {
		handleException(ivjExc);
	}
	// user code begin {2}
	// user code end
}
/**
 * This method was created in VisualAge.
 * @return boolean
 */
public boolean isInputValid() 
{
	if( getJTextFieldName().getText() == null || getJTextFieldName().getText().length() <= 0 )
	{
		setErrorString("The Name text field must be filled in");
		return false;
	}


	return true;
}
/**
 * main entrypoint - starts the part when it is run as an application
 * @param args java.lang.String[]
 */
public static void main(java.lang.String[] args) {
	try {
		javax.swing.JFrame frame = new javax.swing.JFrame();
		LMGroupCopyNameRoutePanel aLMGroupBasePanel;
		aLMGroupBasePanel = new LMGroupCopyNameRoutePanel();
		frame.setContentPane(aLMGroupBasePanel);
		frame.setSize(aLMGroupBasePanel.getSize());
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
		com.cannontech.clientutils.CTILogger.error( exception.getMessage(), exception );;
	}
}
/**
 * setValue method comment.
 */
public void setValue(Object o) 
{
	LMGroup group = (LMGroup)o;

	getJTextFieldName().setText( group.getPAOName() + "(copy)");

	if(group instanceof IGroupRoute)
	{
		getJComboBoxRoutes().setVisible(true);
		getJLabelRoute().setVisible(true);
		
		IDatabaseCache cache = com.cannontech.database.cache.DefaultDatabaseCache.getInstance();
		synchronized(cache)
		{
			java.util.List routes = cache.getAllRoutes();

			for( int i = 0 ; i < routes.size(); i++ )
				getJComboBoxRoutes().addItem( routes.get(i) );
		}
		
		((IGroupRoute) group).setRouteID( 
			new Integer(((com.cannontech.database.data.lite.LiteYukonPAObject)getJComboBoxRoutes().getSelectedItem()).getYukonID()) );
	}
	else
	{
		getJComboBoxRoutes().setVisible(false);
		getJLabelRoute().setVisible(false);	
	}
}
/**
 * Method to handle events for the JCValueListener interface.
 * @param arg1 com.klg.jclass.util.value.JCValueEvent
 */
public void valueChanged(com.klg.jclass.util.value.JCValueEvent arg1) 
{
	//fire this event for all JCSpinFields!!
	this.fireInputUpdate();
}
/**
 * Method to handle events for the JCValueListener interface.
 * @param arg1 com.klg.jclass.util.value.JCValueEvent
 */
public void valueChanging(com.klg.jclass.util.value.JCValueEvent arg1) 
{
}
}  //  @jve:visual-info  decl-index=0 visual-constraint="17,106"
