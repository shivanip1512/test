package com.cannontech.dbeditor.wizard.capsubbus;
import com.cannontech.database.db.device.*;
import com.cannontech.database.data.*;
import com.cannontech.database.data.device.*;

/**
 * This type was created in VisualAge.
 */
 import java.awt.Dimension;
 import com.cannontech.database.db.*;
 import com.cannontech.database.data.device.*;

 import com.cannontech.common.gui.util.DataInputPanel;
 
public class CCSubstationBusPointSettingsPanel extends com.cannontech.common.gui.util.DataInputPanel implements java.awt.event.ActionListener {
	private javax.swing.JComboBox ivjCalculatedVarDeviceComboBox = null;
	private javax.swing.JLabel ivjCalculatedVarDeviceLabel = null;
	private javax.swing.JComboBox ivjCalculatedVarPointComboBox = null;
	private javax.swing.JLabel ivjCalculatedVarPointLabel = null;
   //store this object locally so we dont have to hit the database every time
   // the user clicks a VAR device
	private final com.cannontech.database.data.lite.LitePoint[] ALL_POINTS;
   private final com.cannontech.database.data.lite.LitePoint[] VAR_POINTS;
   private final com.cannontech.database.data.lite.LitePoint[] WATT_POINTS;
	private javax.swing.JPanel ivjJPanelCurrentVars = null;
	private javax.swing.JComboBox ivjJComboBoxCalcWattsDevice = null;
	private javax.swing.JComboBox ivjJComboBoxCalcWattsPoint = null;
	private javax.swing.JLabel ivjJLabelCalcWattsDevice = null;
	private javax.swing.JLabel ivjJLabelCalcWattsPoint = null;
	private javax.swing.JPanel ivjJPanelCurrentWatts = null;
	private com.cannontech.common.util.NativeIntVector usedVARPtIDs = new com.cannontech.common.util.NativeIntVector(10);
	private javax.swing.JCheckBox ivjJCheckBoxDisplayVars = null;
	private javax.swing.JCheckBox ivjJCheckBoxDisplayWatts = null;

/**
 * Constructor
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
public CCSubstationBusPointSettingsPanel() 
{
	super();
   
   com.cannontech.database.cache.DefaultDatabaseCache cache =
               com.cannontech.database.cache.DefaultDatabaseCache.getInstance();

   synchronized(cache)
   {
      java.util.List ptList = cache.getAllPoints();
      ALL_POINTS = new com.cannontech.database.data.lite.LitePoint[ptList.size()];
      ptList.toArray( ALL_POINTS );
      
      java.util.Arrays.sort(ALL_POINTS, com.cannontech.database.data.lite.LiteComparators.litePointDeviceIDComparator);
   }   


   VAR_POINTS = com.cannontech.database.cache.functions.PointFuncs.getLitePointsByUOMID(
            com.cannontech.database.data.point.PointUnits.CAP_CONTROL_VAR_UOMIDS);            
   java.util.Arrays.sort(VAR_POINTS, com.cannontech.database.data.lite.LiteComparators.litePointDeviceIDComparator);


   WATT_POINTS = com.cannontech.database.cache.functions.PointFuncs.getLitePointsByUOMID(
            com.cannontech.database.data.point.PointUnits.CAP_CONTROL_WATTS_UOMIDS);   
   java.util.Arrays.sort(WATT_POINTS, com.cannontech.database.data.lite.LiteComparators.litePointDeviceIDComparator);

   
	initialize();
}


/**
 * Method to handle events for the ActionListener interface.
 * @param e java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
public void actionPerformed(java.awt.event.ActionEvent e) {
	// user code begin {1}
	// user code end
	if (e.getSource() == getCalculatedVarDeviceComboBox()) 
		connEtoC1(e);
	if (e.getSource() == getJComboBoxCalcWattsDevice()) 
		connEtoC3(e);
	if (e.getSource() == getJComboBoxCalcWattsPoint()) 
		connEtoC4(e);
	if (e.getSource() == getCalculatedVarPointComboBox()) 
		connEtoC2(e);
	if (e.getSource() == getJCheckBoxDisplayVars()) 
		connEtoC5(e);
	if (e.getSource() == getJCheckBoxDisplayWatts()) 
		connEtoC6(e);
	// user code begin {2}
	// user code end
}

/**
 * Comment
 */
public void calculatedVarDeviceComboBox_ActionPerformed(java.awt.event.ActionEvent actionEvent) 
{
	this.fireInputUpdate();

	if(  getCalculatedVarDeviceComboBox().getSelectedItem() != null )
	{
		int deviceID = ((com.cannontech.database.data.lite.LiteYukonPAObject)getCalculatedVarDeviceComboBox().getSelectedItem()).getYukonID();
		
		if( getCalculatedVarPointComboBox().getModel().getSize() > 0 )
			getCalculatedVarPointComboBox().removeAllItems();

      //either use all points or just the VAR points
      com.cannontech.database.data.lite.LitePoint[] altPoints = ALL_POINTS;
      if( getJCheckBoxDisplayVars().isSelected() )
         altPoints = VAR_POINTS;
         

		for( int i = 0; i < altPoints.length; i++)
		{
			com.cannontech.database.data.lite.LitePoint point = (com.cannontech.database.data.lite.LitePoint)altPoints[i];

			if( deviceID == point.getPaobjectID()
				 && (point.getPointType() == com.cannontech.database.data.point.PointTypes.ANALOG_POINT
					  || point.getPointType() == com.cannontech.database.data.point.PointTypes.CALCULATED_POINT)				
				 && !usedVARPtIDs.contains(point.getPointID()) )
			{
				getCalculatedVarPointComboBox().addItem( altPoints[i] );
			}
			else if( deviceID < point.getPaobjectID() )
				break;
		}
	}
	
	return;
}


/**
 * connEtoC1:  (CalculatedVarDeviceComboBox.action.actionPerformed(java.awt.event.ActionEvent) --> CCSubstationBusPointSettingsPanel.calculatedVarDeviceComboBox_ActionPerformed(Ljava.awt.event.ActionEvent;)V)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC1(java.awt.event.ActionEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.calculatedVarDeviceComboBox_ActionPerformed(arg1);
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}


/**
 * connEtoC2:  (CalculatedVarPointComboBox.action.actionPerformed(java.awt.event.ActionEvent) --> CCSubstationBusPointSettingsPanel.calculatedVarPointComboBox_ActionPerformed(Ljava.awt.event.ActionEvent;)V)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC2(java.awt.event.ActionEvent arg1) {
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
 * connEtoC3:  (JComboBoxCalcWattsDevice.action.actionPerformed(java.awt.event.ActionEvent) --> CCSubstationBusPointSettingsPanel.jComboBoxCalcWattsDevice_ActionPerformed(Ljava.awt.event.ActionEvent;)V)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC3(java.awt.event.ActionEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.jComboBoxCalcWattsDevice_ActionPerformed(arg1);
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}


/**
 * connEtoC4:  (JComboBoxCalcWattsPoint.action.actionPerformed(java.awt.event.ActionEvent) --> CCSubstationBusPointSettingsPanel.fireInputUpdate()V)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC4(java.awt.event.ActionEvent arg1) {
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
 * connEtoC5:  (JCheckBoxDisplayVars.action.actionPerformed(java.awt.event.ActionEvent) --> CCSubstationBusPointSettingsPanel.jCheckBoxDisplayVars_ActionPerformed(Ljava.awt.event.ActionEvent;)V)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC5(java.awt.event.ActionEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.jCheckBoxDisplayVars_ActionPerformed(arg1);
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}


/**
 * connEtoC6:  (JCheckBoxDisplayWatts.action.actionPerformed(java.awt.event.ActionEvent) --> CCSubstationBusPointSettingsPanel.jCheckBoxDisplayWatts_ActionPerformed(Ljava.awt.event.ActionEvent;)V)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC6(java.awt.event.ActionEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.jCheckBoxDisplayWatts_ActionPerformed(arg1);
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
	D0CB838494G88G88G7DD568ACGGGGGGGGGGGG8CGGGE2F5E9ECE4E5F2A0E4E1F4E14DBC8DF8D4553174BDDA31C2C5C12514A2BF41860990B1821528D1828689828A1696DE9BDB5A52A7A03E66D9DE9F2F5EACC9B6A1BFA4A1C8945070EB501884C49F84AC01C4A0385A94D20CC24D5E6C5EE4B7596CAE3BB712C054B767E74E3D3BFBB7A1D87A647B06FB4F1CB9F3664C4CB9E74E1C3B118ADF9B1516E2AA142414B4097F7B5F94C90ABDA7C9B955E336A1EE790D9633247B379DA0DF7ACDCE
	AABC57C0DD62AFAC66BCE94E7E8CA8E7FF57E2DE7E34453C995EB7CAAEF53E85DE42749389F5D31EBD73CE4F7D3CE545FE4C34FCEBFAB6BCF38172202F83G7D3ECB7A3FBB3D08639B201C4B5F892418A449F29321D969AC526163B9DDB1549DGD8CDE9E626EF6575C321CCE8226099F79323DD8C4FCCAA5D37AB8FF23A8B370C12129F5FEA4AA1FD6978A39C9C485ADE604FE2A902AAE21DE4EB290367113A3616BF5BBC6DE5B6596D7638D54566A833B7A9F6276A7115DDF2DE16FDF628727ABB1A1ABA7C1515
	5555E7BB1A7C2A2CBABD6E639D7EBA0F532D1ED5D45569EE715749EE4515B8100CE3E3EB866D8346816506C184F7D3AB51DD9E3C17GD48D4231AC48B902E3EE9BA5A50D5D90F6ACA38631B1448D5246927D61C51377FB047FAAEA9B52CE0272C1GA64B3BE614A566FBA84B33B6C23B824A29B7DB4CDBEFC67CE4BB419F00328540FEB34A785E6BA8E34315C9D2443D0D699B424858FDB313AD62E6CD46DDD597CDAFD37F7931CCFCA0896AD287DB4CC5GC7GAEGF483EC21FE2BDA5F006731A6355A535E6EF1
	9F73FA0F3B6C3E8FBDF645856F1515D043F1E7941B47E717A4661BC52D258672A08C9D9C6817845858BE165017224E5CA58D35DA53538C46963368D5D3A46791BBD89B1BF48B515B3AB622372500A76D087D983E0062EBEE51704CD637D0DFA0E58754AD3C85755C100B32D8D207CBF97B0E194C86328CFB74DDD3552DCC0606DBB5D9180E0758080E7B83FE9440B40079G2B817206200E9F323E5D1B0EEBE1AE785C328B7C5DAB7B54639DCE17DD71F1DDA7277724EBA3E06DCC663E36E3FA09FCDBEF232577D5
	996AE5E964EED36990260F1AA11ADED85F074376ED9F926A9B3942060DF4CEF48EE1B6DCA770CE0A9F2043B35BBECF6796A98F033A35G0C7E8D05602DC303FD649045779F9A6CA3536C68A311D0D7F81B454C6C3BCFA1EBE4BFA80FG0881C884D88110F1BB4C655B514619F279D54E23BA1DFDD95BC765176029F42ABE39568D4B1D5B261C13FDCE39492578A5CEB3C7A6FAED8AE8B71F4EEF9B74F1D2F1ABBE3ACC82F739DD81E772B31A1F4A79044627505AFAE73B829D34FBA5647B0C3C911EAE592F7E4DEB
	17D5C523E0753F1309CFFCAE1F830E4000505ECE74D5CE575CA3705EF0BB6A6A33E6020B06F202500B3D29G1E9F00F3AB2E4A4A9A270D08A17B3A50C7B35B48981682FD549D96F367B00BB97B7B96F38400E3B84E53D58D680FB5B9632452C5C78D7D11C0698F38BF7EA0589F0BE8BFAE408F840882C884D88110BD82775BBBDA2D70A47D77B4C7080C39DCE6A263554CABB2B63ACEFF9F7B1ACC7B4A3C0AF91C4B7DBB67AA67F09AFFE681B03BA436905FAE05F11E82F00CC05B5C59D44CFC76386CD750CB7F62
	8387D36C1A25986D889B5903144F9537DA23F895379DDE7C5CC7465AB2B57FED76885F9ACF71E4BD2B8687738B7CA4DBBE43838F7DEC407A2934DED6D51F3329C3D5F04797758FD3D97C81CB251F0F75898799EBBF9863B000C8B156D3760DBABF34F8BCED9D5EFAB24338BF1F217BF6AC50AF947E2058E9BCA64A9EDA566A7FE0EB38CA1BDC8D10FEC25770BC7BBF320FF5867E15AFD5A8E4EEEC663EE366FA4E64BE44E80ED21A129FDA4CF5G1DG23G92GD67F907571105D9A6407E69D9FA3F9B6736716BE
	0C017575D3433EC8BF6166890E3D37B9278766BB0715703EFFDCB1767DD30A316FFF24746C5B6794A35FE6F5FFD748B8BD175C3A39522410755566F13B959B8B13F99309455D2B6A49B81CEE277A31535E2228E71D1795C92A9AC9FC7E84DD430B603DF4A42E3517F662DA3B6B72E8A92E719513D1FCB6EC5CA1D37F114CDF070D5456DAA6132D194CC145253403222ABD9DEE15611DF4FF60F87FBA0ADB7CE5BCE9DBD2AD3BEC9DAE58006C307555A81FBBED8A6C24CD1E631E4ED2190E2C0CBD6A94DF3347572E
	58CBDA654F65B27912DAC6ADD3F60CD63FCF5E77963F75561F0BFBBDBD145A42F75AF8AC28B7CB786E8E6FBD37F75FBEB61E57AFBDC847F39A991101FD8C7676B37E57EFB02DE1FA6C6BC876733932D1BA14FFC31831506E2F5FD01A1DBE25566D6D60B1C989C875B617296250CD217EC2D9DCFFF7B190EB9F4AF56A507DDCD78E455686E254B87DDE175C8552792F274DC3FB6B2B3D8F88FB8F37CFB59C83B579BFEF90343B3E0E629D3E7F2750335EB4D8139281160D447D7F574D249E77FF9E64D4FBDCAE36EC
	C25D05BB491A78D1DB21EE1DDBBB1265DDCEC19D2B1FE7D89FA16A6BDCC67553C47D82437AC4D1FF265D28BEC954FF62B72ACF3E934779E4CB9E59BF140B9D8A9CA3C80073015205E7011602203AF3322BC3BBE3BC53C276CF17AEB6E278058E63757CE78E928F35897EBFF724CB642C40F8327DA7DAA6BC3DBEE7BB0412A4586379A927D91F1F4A9885678822E7F8DEGE07E38EABB490B756AC0C0D3B31A706CD0B01F25820F01GD11C6F0AD17CDCC0698EB5A35DE0A8C70E566892C66B69D68A3A2DE3C04FG
	75E3985D50B1FA3AFBDB10AE867009GAB8C69BA9CC8D7BA56E22E81300FE5F491E38364F3A2DDB26033A3B43A5288C6579DC1687EAE72DE658227D25C9FA0989A3B8E4F739F666973F72511AB8D77F482F11C7F420860BC473BED04EFB260D347212FB5B875B175B13F5FE3F3D2B3F13FB1376A77FB064BEEDD8FCFD8C97DEAAD84D21D68FB852DC4D6F28E65E86EBF935EFCCF8ABDD73FCFB306F8E4293EC41701FACFB59C05CF017BD60BF0681CD339846DD9EE6E5BED282F22F1986FFCF1C21F8F0C5D703AA9
	AB0CEE6A46B11DF847E93A09BDFA5144F8273B10F774B84C855EA0F04B85AE239DF145F7A16EC601EB903841FE448D869C534577ABC99E49A807EA234AA9EB69F016F528CEF25016D519EED5EC06B01ECF51B38E6115F09766A0472EA7F3DB7A1145DC84D0814072A1A3E91E04501E82DC3F9F617AB3BEC53F7ECC83FC92C0EAA44E5934E692BF74FD2FC0FBDFFC6FB4BC7FF101994E01B19B2B384C099307CBC73E4AB09965D487ECA8B095C5B27BD6C5EAF63E3064D9D35D6FB0775B540C3A9988B47111281BBD
	5EE05D2C053A6271E83F3B6DA761798F0A53F85E6A25961465G74639A0F326C2DB212A5E2BC5A690B162FE727783D85EC9E87C5C418CF7B1910EB9A7439DA48B52D5A48FFCA6EC6390EB92ED5AE6E378E6C778870D46F46FE132C7AFE236EE67DAE947D26B93F1EBE1667D7D35BFE6DF80C0F23490963D88EB296CF40F144EF8E9E8731EB43847431635BEA8C643816100A676B776B770E3824BDE1778E5784B6DF7ACD885EBBDE216B5B8840C7CF403DE3D0EB3EEE6FB8832B356CEE0108C31C395B08DDA03660
	6523ED24DF9ADBC8188378EEDBDF725B7193B0CF79311B68F0B11413813223AC66DDGB5D1A85B2C36DE73756FF776102F4FF275C5AEFB9466CF867629DD27E817535E17F683C53B6F75295DA8516EB6FFDF5A4508F60BC36E849222341C57522850DCAA4B59EF4EA2F3E1F5944B5927897C8E0ACF3DC7433339B3FEBD4E1DA2286B3E877B0FD0027BCF3AA7304F3577986560A60734FBB528DCBCB1309D1B0BADAE62CFF43FA6B32D334CFF8902564ACAF11E66317027D97AD87860C416033CGCF6FC40C8712D2
	B09E98B0917D789E8F099363203C94A099A0EF924429GF5G6AA47467CDAE2398AA501F4385D0DAEE16694251FCEDBA7CBC24DD308E07CE8AEC47F4D357EA241BA8200D1B043A793B97F533F8925AFAEB4B354979C6C83B3220F2DEF4E0BBA603358AE5A809C699122ED106D53D4AE0B5146139EA1421D348906138C61F8FE997EC2F68E8A31F7F7339F0BE4FF7A4AE6BE6B9037715A0E4F5B82F5137C25A854BAA5DEB242F7F5E0CB2945D0BF3EAA57525BAA8DB6F457963F3111CB26ED574GAA61BE75DABBB9
	73FAFC4E96279BF13BA84E667151C4AD3BDF98CD4F9EDE60B9D070EDF4074FABB73B0D734AAE37F1DE59676E79CE6532BBDC5E1949374305720D82E038318297A3F03B5A9117A0F06384EE2940C5799137DA601E23670F5449F056180C3802CCFA0F88657A4968875B32882E934A43842E046212F89C4BF01356DF6B5D88610D6F43A6076EBB1AEFA5DCF39F7A3B1634B0B45BA85D6C17B6D05B797D0A1FAE446474466BFE2BEE128C727B9F496D0A04F93BA5742E01503C2FFA2A770939787B3DC546F3D1776D4C
	2DD9B88726CD96E7428C3A5FC2B963BE08BD6FC3FD7FBA2527FD7F60FD21FA6477F91E70FE7E1047584F63BDE44E79BD9DBE1B926C4BF3BCC522CE5B29EAD5251D2805672CE8FBD57635A8A1F3E189ED4F6A4235DF6689B7DF184E6FEC566B1C440BCC1F83DAD11F167BD01FFBBC741B83A80FG08917AEC7276244FC483FD323E4F36687BBEA0FCD5296A09DFD2D8FE1B9DFAFE7B853F646A1E78A5076537A4005FBB025FD6D94FAFA306355F4A1F95B161785D695473FBD770130A747CEAF87B867EF49970E336
	7D8FAF0997BBC57985DA6E9273C6B916AB7C63FD371DAF78AC3FB60D2E637F89020D1390F7082E135BD3A5E9C18A629E2038E7016E9D93628E53F572AB4025893AD80AFB9EF09F88FEB3685AC9BEBA3C1202BAC84FA0FB8A9C65BC9DA04B4298C237ADC39F9B7583DCA4C0AC9FB79B4B3D17C8BE2B59A737037B1E75AAB6A97AFE1A9F484436DB21DC81D087608598FCBF1E09130F12B940D60B6A8E9F59DA4801D65845312FA0381E9EB451BEF7E4696D338678E6CF51ECD2BCA554BEBC5E4F245F45CDE1F1FD16
	406F2678FA9D1E477BF43DA2E5956A96CF413DE5ED8A6EADC726621C9B94705D5AC0400F1A2A4991B3B51CCC43699CCE189AACD3A445AF1F9AACD32987E54A188672CCC3193A3CA853FC016B2EC2DC710F9117D503B82F403DCDE326B8A84F973813E788EE99141385AE6A8C36159EC05C66A29A57C059AAF01F52BED4A877973807C53F039F401C42BEABB9E708FD04E48CAAABCF7A1C7663F20B05FAA4DB7B2AB3C8DB22072487F06D3BC8EDD3B49DFC8C6054F44C3BF66E413CEB54DD43A5CB3FAA436FB0136A
	DFB7D927B31B741F2E65958ED6E15E751ECC6CB38E6A57CCA738A5197A9C897155AFCCAC06E675B96B75754447F7CA7A7A238175C45E19816DAF8654939DED636D5958666C46312D1D93A9396EAF8B7B0DE9D5AC9B131DBF571C5CA9398AFDA6E60B09F4EF9E81F55131E81F8F4B89EEA114178B1C3782ED167D200821288EAEC0399FC084C0A2C03228FF135697BE8467450758DD893937A0F792BC364A5047BD4843F09F60788B3A7A44B03488BC368A68833F61B022476FC774B1D5EF5FF18CF808FE9BEC2A69
	01A6CD42F512C1BD34F1G6C6478EEF89F47ED47F49DC177777900DF76906AFF4AF9024BF858E25E7AB0626EECC41BD4BD0CBA9D65A1BA9F8C65E80005GEBC81BB82C1FC86B2B20EC0DE377D2644E095C7F307A7D9E3D3EC8BB7DD8884FDC4EBFF81CEB02F08B8DE8D89F9F8474917C5D8E4631415F9199594348164CA6033DFD31C93F38C01B8C0572A0EE9346EFC8D5EF7C4CBAFE2302784504705BD15397FEF1C17C9646E9BE43764953B239A7ED7638552D0F50DC81DD471300EEC59C6E351F1DA1B4AA8442
	56C748EF83EEA4618917E746191E64C1B9D0E78E1D8F9399A2009F399F7C1F97DFFC1175D5D694A8CF8D5D9B2261BD76911467E2CD20BC8B3AB4F91E6ED1BF3D493382785938BC4C67CEE57475BBE536777E1A6EADEB9EE1FBEF0A402F227854C7B5BC5B13AFE560B77BF9D027BE0AE77E184BA496DA8C65E4006C999673AE009AG6B8CBC2BDF39D8A17512C32B77F8ADF048F1690E5FBC1712F9ED5FE1BFE246FDA46601B152346F2CCA89F7B7294EE073F9A8FF8EBE3113625978CEF6137145C3DD92C0EABC2C
	B9G95G2762F1FCB3AE163271112B55E32AAA5B9C74569687C7AED2B5B489A4191F4C4CA2F1E91347C78FD8ACAE7D45C60CADAFGFF95A09AA0BE1E49465A594BC36359AB6541716CD2E8331A37E33EF7C34555789EC246CC986BCC66FB24DC816FAB416FFEA766C265A7649B44502F09ACF2136262327E1666E78D3ED023C40CA6F65F96E39A116BE5F4937E720AE1FF020C53CD2AA0FB015157C46C9E489A90639C19496E332CB3711E6855AC23FA951E2FD17E209B897F2DBE9A0A13A4B66FF7FFA989FFFCA6
	9B7DF16B049FC8B5BFD91502F3BAE41D1F49FFC3B4D30B59567E0B4F4446B63DB554E71E94F95E3CD94CE7CA60F9E4165EE7661E8F7519E56703FD269E5AB8E669FDE6CAE3EFBE93305FC05B2133B41F091C45FC66B961B305BB28ED287583BF09E3750F9D5994DA4F3CC10C65EFCDC5543E46F7410CE64D154D06B46CC04AE8AE34EFB27093FE8C635FB47D4A936C8BA40796B38BFDE51D5728BEE116312FB03F987195A9052FFFD86A397E17A91A0F0DA7E66563B9FCE687BC5D4A25E2F1B9F982B62A129DF627
	6C72346CA8AD2EA8AD5E37FFAF7A6428CB7A7B49036BCB435ECFAE65BE311ABF6DDDBBB90FF96775BC72063FE4CA89432364B1567640E31ADF33B1B4E4E86379D336B61E0FEA2EF6BC2C6DF63A3D31FD63598C6DBDD9777EC5463566A42F86D89F37DF57BE581E73606570F941784B46F94147AF9B673F67DF6E793B6A451743FFD77D0B4B7A6F2A591E79FCAE19938E30F3FF00C800D800790F61FE59FE0558157C2AA7F42FFC5F5EC272126417BCAA76DF579DFE2C9FF7777E7B9FEBB779EE2A49232A1EF6429F
	714D5D44DE2A47CB118C5756CDF464F336B8D4C64A71973B09933914E60AE6B1113990FFBF35824636AB814F7C8F2EA7714ED5FDEBD46FD4DD4AF13A1A33B3F95AA67C4E68C2826681224AC8BFC3219C85901F00F7F7234B717C3ED4504EABA7340533AD66D2001A59D73BE7615A92CFBE01449CF8F3189CB8DD1D198CF7E4E24E7642EC146BEFA2AFBEF4B64609137349BE12886565G590F033EG8E8254815881BA818682C4G44824C87D8863086A0E38E44A2G95F35007DA3F04E9AF1D13DBC33FEA83A4CF
	30D0C772496C4E09596C0F2DE8339A6055BD87E5BE5E06FA0F19033A5CF41E7E76944A290950FFA2524E7F8C75FEA091E97F749921F5C139BF4008443E6A7DAE0266F970D73C283F9851E725402DC8C41D1EADA438DDF3611CBC977532AA7D4D9E74D26773F8951F5A4530748BD6BEE7BFA2DBA1777583665D61F8408CBD8D2E701726D64526564BAD224DFF65061F2352065E67E866867A3DA34C2D7AAEAF3FC7485DC06F871B1B7D0AD88BB6EE489338DA584D7DCEF27F40565D99053D1D952CF30D6EC097745A
	AE78CE1435DB3F09346B1E5BF33E86E170DC7CFDE5B11D2391F3034FDEFBA9BEFAEE7059EBF2991E3DE2212E78894CE95CD5FEEDE73F6A8AFA2772C4F07F960A9F7CC4F07F0B4A317F91D0A74D437EC7841C4D6C73189EBA79F3603CF0F912CF83F25B23B8DD8CFFA69834635F966507773139793D7B585379467B40527C50FD6019FC63FD6037797AFD0061D66413F52385561C86C1773C40FDA2F0AFD01E847731532EBA84FEAD150B60CFA924C3441B8A08DC17A82E813F8B5661BE61386C82B2CFE0488E0F8F
	5B669750BC055365625F31D09A279B260D5047AB85C4C627D756497D1A40FDA26CA5757A2D18713C4A6D351D713CBAC95BADB57083A3D8BD8F67550DF43DCE1D9F6C5737D2FC5E7CE03F5EF29E7DFAAB54751B0FFE3D6833EB1BD73EC642EFD9C87FD7A8FEF5C87F290D587FDA2833AE407EDF8B385B0CDF404F037C39E2C138F9F5B4201D74A423AB644F12A7434D2B06C272AE62BF36AD703A4642C2097D868EE2EE67E57A097AD9A0F43730BB76380DB9345E65F4AB556C57549266FB3753D8668874DB7FA466
	F68F14911C834A5DCF62F87710949B9FC3396EFDDA59750F55977C937A282D5046F4C6773E247C7A771DFCDD7BE0B63D0F460083401E43041DDF58CAF0D1D00E9538C7DE453CFE0240DD2431E251D396F349D3085BE127F4D036885C8DF6EC5BA0F01F3521FFC58A5C18CF513FE21FC27F3A793C2673E13A776D672F3F7EAB7F89FDF4B6EAE37A166EFDFD63756FFB67F56D035974DD9A7BA600BD978BBB9752BB2295D0DEABF0DD625EA8E3A1624E51368DD0F6885CD2CA578D650182F74C0AED07AD44BC7E41CD
	E43FD649910D649D601D657C6B683A369C6812856D87E5E9C8ABE1BEE2A289C9F8AE63714C40FB03A4BAE7F6AD02B34DA2641133AD685E002430F89EEE712B3D65617477AA417786D66883FF33417A9A1095A84FC8BA8FDDC057A964B9E48F14A77BE5CD1E11763E6485036509DC444E4AA9C227B750399C8378B8G56DF79AB1A7EBE74864AEB23F6DD8D34EB053CEFB6864A6B5D2049BB3857BCE6CF7AABFDBAD8FF6DC1727CAB1567A05055BC0D727CBEC81EA1F91ABC8D562F23BF75E976ADC00A7035678AB4
	DD1514DDCD5E6C00481BB5EE525A7E7B563E35DDC3FD1C3D7F74E50D4FCB763E71A95C227139ED1B466776CF7B4667051DDA5B0C463E35FDF937A6C3476B9A1F79FD64B32C0434E5FF5B4C337EA76098D1AB25B4D55A4BB34C459E372B2B58CBB2517E62F6C576F77894FB3153DDCC2EA80A7794FF70D7D55A5D9BADCDD37BFD5286A1D3AD8D6E13BEFF3BF25D2F462EB3C92935D2BABB6C54CA296444D2AB255183CD2D348E8EBB1D648F5910C3018900C81C663FA8FDC9CB695C35C80AA5D5E0B76830E6015D2C
	63E086F9F282F4B4F374BB3FC97C79B97FBAA93DD6FA19CC17330A0FC6120A1DFC29497FB44E89A9138E117C05859056CF9FE9FE354B25D8244266B364E737FED5319F4398552FCA1B68CFF04AC99E2D7C146CF728642CF758A2253A499F71118AD835CBF63714330075B09DF14A31AAE29D096619C15E0DD4DE4225CCF059B8FD436E6D2717CDDBA719CEC86BDD9E59FEC236299EDF2D1429BAFC0A5F61F159BB3DC799230CCBE466135B45F540EE0B98BE99ECD9BDCCFEA1F4DB678F8766AC4FB9816A6A74C965
	14122C8EE50242DE6D10FD5015622BD5499FA57278F6093AEF3D2A6E19FBF2738513144287C9CC105969D52574E6395D696A02179A6A1154B8B9AAC86B135DFE720BA5102CB8C8B22C929D148D19DDF15B78BFFECABB501A2A52EBCC1550343CC57134179FEBEE06E8DFB0G3D827BED02BD21A98B22A9683CE55369C5AFB6019F54C86B5AABAA687F7B687F7B497F7BA8E69F456C5B8F4ADDAD7578AF383E9347CC1DC74C8BE27E3293E0C175E7ABE70FD9F2E4758A985D89A90D7E988A0C1027D1FD54515E2478
	3E93F55B362C21C9B7919BF9092BF4923E791D425FD339315F90B3469C62D5841B956A9E843DC19809148AA8E9A17DFFF31F07CCDA3DCC5BEE9112EE57A4A50B9759A45E8C195BE48C3981F3FB0F8EEBD625EC7EC9A2DD11D4E9BDBC0A694DD925AAE511823BEA03D28ED2527588DA5227A04EE1C5CE2ED317158EA1777FCBDDD6DE2E683CB6196C57D126F7B593128D15640608FC195FBCC95F53A44D6012128D427C4D33F34DB7504E27B56D117BB7B35762B7504EFF8D343319532673BB603DDF696F0057FCBB
	A76CBDF2DE924BC16DCA8A7E1D6B3F65924ED640F7A761DD46DB39EFC9616FA14E364BAE17AA3B5A0E396D64CE62F8172AE8F7163F4CA57C7DC843AB795F654BA5F2376A5A7C9FD0CB8788020A2441F59BGG64D6GGD0CB818294G94G88G88G7DD568AC020A2441F59BGG64D6GG8CGGGGGGGGGGGGGGGGGE2F5E9ECE4E5F2A0E4E1F4E1D0CB8586GGGG81G81GBAGGG2F9BGGGG
**end of data**/
}

/**
 * Return the CalculatedVarDeviceComboBox property value.
 * @return javax.swing.JComboBox
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JComboBox getCalculatedVarDeviceComboBox() {
	if (ivjCalculatedVarDeviceComboBox == null) {
		try {
			ivjCalculatedVarDeviceComboBox = new javax.swing.JComboBox();
			ivjCalculatedVarDeviceComboBox.setName("CalculatedVarDeviceComboBox");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjCalculatedVarDeviceComboBox;
}


/**
 * Return the CalculatedVarDeviceLabel property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getCalculatedVarDeviceLabel() {
	if (ivjCalculatedVarDeviceLabel == null) {
		try {
			ivjCalculatedVarDeviceLabel = new javax.swing.JLabel();
			ivjCalculatedVarDeviceLabel.setName("CalculatedVarDeviceLabel");
			ivjCalculatedVarDeviceLabel.setFont(new java.awt.Font("dialog", 0, 14));
			ivjCalculatedVarDeviceLabel.setText("Var Device:");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjCalculatedVarDeviceLabel;
}


/**
 * Return the CalculatedVarPointComboBox property value.
 * @return javax.swing.JComboBox
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JComboBox getCalculatedVarPointComboBox() {
	if (ivjCalculatedVarPointComboBox == null) {
		try {
			ivjCalculatedVarPointComboBox = new javax.swing.JComboBox();
			ivjCalculatedVarPointComboBox.setName("CalculatedVarPointComboBox");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjCalculatedVarPointComboBox;
}


/**
 * Return the CalculatedVarPointLabel property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getCalculatedVarPointLabel() {
	if (ivjCalculatedVarPointLabel == null) {
		try {
			ivjCalculatedVarPointLabel = new javax.swing.JLabel();
			ivjCalculatedVarPointLabel.setName("CalculatedVarPointLabel");
			ivjCalculatedVarPointLabel.setFont(new java.awt.Font("dialog", 0, 14));
			ivjCalculatedVarPointLabel.setText("Var Point:");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjCalculatedVarPointLabel;
}


/**
 * Return the JCheckBoxDisplayVars property value.
 * @return javax.swing.JCheckBox
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JCheckBox getJCheckBoxDisplayVars() {
	if (ivjJCheckBoxDisplayVars == null) {
		try {
			ivjJCheckBoxDisplayVars = new javax.swing.JCheckBox();
			ivjJCheckBoxDisplayVars.setName("JCheckBoxDisplayVars");
			ivjJCheckBoxDisplayVars.setFont(new java.awt.Font("dialog", 0, 12));
			ivjJCheckBoxDisplayVars.setText("Display only points measured in Vars & KQ");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJCheckBoxDisplayVars;
}


/**
 * Return the JCheckBoxDisplayWatts property value.
 * @return javax.swing.JCheckBox
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JCheckBox getJCheckBoxDisplayWatts() {
	if (ivjJCheckBoxDisplayWatts == null) {
		try {
			ivjJCheckBoxDisplayWatts = new javax.swing.JCheckBox();
			ivjJCheckBoxDisplayWatts.setName("JCheckBoxDisplayWatts");
			ivjJCheckBoxDisplayWatts.setFont(new java.awt.Font("dialog", 0, 12));
			ivjJCheckBoxDisplayWatts.setText("Display only points measured in Watts");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJCheckBoxDisplayWatts;
}


/**
 * Return the JComboBoxCalcWattsDevice property value.
 * @return javax.swing.JComboBox
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JComboBox getJComboBoxCalcWattsDevice() {
	if (ivjJComboBoxCalcWattsDevice == null) {
		try {
			ivjJComboBoxCalcWattsDevice = new javax.swing.JComboBox();
			ivjJComboBoxCalcWattsDevice.setName("JComboBoxCalcWattsDevice");
			// user code begin {1}

			ivjJComboBoxCalcWattsDevice.addItem( com.cannontech.database.data.lite.LiteYukonPAObject.LITEPAOBJECT_NONE );

			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJComboBoxCalcWattsDevice;
}


/**
 * Return the JComboBoxCalcWattsPoint property value.
 * @return javax.swing.JComboBox
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JComboBox getJComboBoxCalcWattsPoint() {
	if (ivjJComboBoxCalcWattsPoint == null) {
		try {
			ivjJComboBoxCalcWattsPoint = new javax.swing.JComboBox();
			ivjJComboBoxCalcWattsPoint.setName("JComboBoxCalcWattsPoint");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJComboBoxCalcWattsPoint;
}


/**
 * Return the JLabelCalcWattsDevice property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getJLabelCalcWattsDevice() {
	if (ivjJLabelCalcWattsDevice == null) {
		try {
			ivjJLabelCalcWattsDevice = new javax.swing.JLabel();
			ivjJLabelCalcWattsDevice.setName("JLabelCalcWattsDevice");
			ivjJLabelCalcWattsDevice.setFont(new java.awt.Font("dialog", 0, 14));
			ivjJLabelCalcWattsDevice.setText("Watts Device:");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJLabelCalcWattsDevice;
}


/**
 * Return the JLabelCalcWattsPoint property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getJLabelCalcWattsPoint() {
	if (ivjJLabelCalcWattsPoint == null) {
		try {
			ivjJLabelCalcWattsPoint = new javax.swing.JLabel();
			ivjJLabelCalcWattsPoint.setName("JLabelCalcWattsPoint");
			ivjJLabelCalcWattsPoint.setFont(new java.awt.Font("dialog", 0, 14));
			ivjJLabelCalcWattsPoint.setText("Watts Point:");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJLabelCalcWattsPoint;
}


/**
 * Return the JPanelCurrentVars property value.
 * @return javax.swing.JPanel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JPanel getJPanelCurrentVars() {
	if (ivjJPanelCurrentVars == null) {
		try {
			com.cannontech.common.gui.util.TitleBorder ivjLocalBorder;
			ivjLocalBorder = new com.cannontech.common.gui.util.TitleBorder();
			ivjLocalBorder.setTitleFont(new java.awt.Font("dialog", 0, 14));
			ivjLocalBorder.setTitle("Current Vars");
			ivjJPanelCurrentVars = new javax.swing.JPanel();
			ivjJPanelCurrentVars.setName("JPanelCurrentVars");
			ivjJPanelCurrentVars.setBorder(ivjLocalBorder);
			ivjJPanelCurrentVars.setLayout(new java.awt.GridBagLayout());

			java.awt.GridBagConstraints constraintsCalculatedVarPointLabel = new java.awt.GridBagConstraints();
			constraintsCalculatedVarPointLabel.gridx = 1; constraintsCalculatedVarPointLabel.gridy = 3;
			constraintsCalculatedVarPointLabel.anchor = java.awt.GridBagConstraints.WEST;
			constraintsCalculatedVarPointLabel.ipadx = 7;
			constraintsCalculatedVarPointLabel.insets = new java.awt.Insets(9, 10, 25, 7);
			getJPanelCurrentVars().add(getCalculatedVarPointLabel(), constraintsCalculatedVarPointLabel);

			java.awt.GridBagConstraints constraintsCalculatedVarDeviceLabel = new java.awt.GridBagConstraints();
			constraintsCalculatedVarDeviceLabel.gridx = 1; constraintsCalculatedVarDeviceLabel.gridy = 2;
			constraintsCalculatedVarDeviceLabel.anchor = java.awt.GridBagConstraints.WEST;
			constraintsCalculatedVarDeviceLabel.ipadx = 2;
			constraintsCalculatedVarDeviceLabel.insets = new java.awt.Insets(7, 10, 8, 1);
			getJPanelCurrentVars().add(getCalculatedVarDeviceLabel(), constraintsCalculatedVarDeviceLabel);

			java.awt.GridBagConstraints constraintsCalculatedVarDeviceComboBox = new java.awt.GridBagConstraints();
			constraintsCalculatedVarDeviceComboBox.gridx = 2; constraintsCalculatedVarDeviceComboBox.gridy = 2;
			constraintsCalculatedVarDeviceComboBox.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintsCalculatedVarDeviceComboBox.anchor = java.awt.GridBagConstraints.WEST;
			constraintsCalculatedVarDeviceComboBox.weightx = 1.0;
			constraintsCalculatedVarDeviceComboBox.ipadx = 82;
			constraintsCalculatedVarDeviceComboBox.insets = new java.awt.Insets(5, 2, 6, 40);
			getJPanelCurrentVars().add(getCalculatedVarDeviceComboBox(), constraintsCalculatedVarDeviceComboBox);

			java.awt.GridBagConstraints constraintsCalculatedVarPointComboBox = new java.awt.GridBagConstraints();
			constraintsCalculatedVarPointComboBox.gridx = 2; constraintsCalculatedVarPointComboBox.gridy = 3;
			constraintsCalculatedVarPointComboBox.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintsCalculatedVarPointComboBox.anchor = java.awt.GridBagConstraints.WEST;
			constraintsCalculatedVarPointComboBox.weightx = 1.0;
			constraintsCalculatedVarPointComboBox.ipadx = 82;
			constraintsCalculatedVarPointComboBox.insets = new java.awt.Insets(7, 2, 23, 40);
			getJPanelCurrentVars().add(getCalculatedVarPointComboBox(), constraintsCalculatedVarPointComboBox);

			java.awt.GridBagConstraints constraintsJCheckBoxDisplayVars = new java.awt.GridBagConstraints();
			constraintsJCheckBoxDisplayVars.gridx = 1; constraintsJCheckBoxDisplayVars.gridy = 1;
			constraintsJCheckBoxDisplayVars.gridwidth = 2;
			constraintsJCheckBoxDisplayVars.anchor = java.awt.GridBagConstraints.WEST;
			constraintsJCheckBoxDisplayVars.ipadx = 23;
			constraintsJCheckBoxDisplayVars.ipady = -2;
			constraintsJCheckBoxDisplayVars.insets = new java.awt.Insets(5, 11, 4, 42);
			getJPanelCurrentVars().add(getJCheckBoxDisplayVars(), constraintsJCheckBoxDisplayVars);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJPanelCurrentVars;
}

/**
 * Return the JPanelCurrentWatts property value.
 * @return javax.swing.JPanel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JPanel getJPanelCurrentWatts() {
	if (ivjJPanelCurrentWatts == null) {
		try {
			com.cannontech.common.gui.util.TitleBorder ivjLocalBorder1;
			ivjLocalBorder1 = new com.cannontech.common.gui.util.TitleBorder();
			ivjLocalBorder1.setTitleFont(new java.awt.Font("dialog", 0, 14));
			ivjLocalBorder1.setTitle("Current Watts");
			ivjJPanelCurrentWatts = new javax.swing.JPanel();
			ivjJPanelCurrentWatts.setName("JPanelCurrentWatts");
			ivjJPanelCurrentWatts.setBorder(ivjLocalBorder1);
			ivjJPanelCurrentWatts.setLayout(new java.awt.GridBagLayout());

			java.awt.GridBagConstraints constraintsJLabelCalcWattsPoint = new java.awt.GridBagConstraints();
			constraintsJLabelCalcWattsPoint.gridx = 1; constraintsJLabelCalcWattsPoint.gridy = 3;
			constraintsJLabelCalcWattsPoint.anchor = java.awt.GridBagConstraints.WEST;
			constraintsJLabelCalcWattsPoint.ipadx = 7;
			constraintsJLabelCalcWattsPoint.insets = new java.awt.Insets(9, 7, 17, 8);
			getJPanelCurrentWatts().add(getJLabelCalcWattsPoint(), constraintsJLabelCalcWattsPoint);

			java.awt.GridBagConstraints constraintsJLabelCalcWattsDevice = new java.awt.GridBagConstraints();
			constraintsJLabelCalcWattsDevice.gridx = 1; constraintsJLabelCalcWattsDevice.gridy = 2;
			constraintsJLabelCalcWattsDevice.anchor = java.awt.GridBagConstraints.WEST;
			constraintsJLabelCalcWattsDevice.ipadx = 4;
			constraintsJLabelCalcWattsDevice.insets = new java.awt.Insets(6, 7, 8, 0);
			getJPanelCurrentWatts().add(getJLabelCalcWattsDevice(), constraintsJLabelCalcWattsDevice);

			java.awt.GridBagConstraints constraintsJComboBoxCalcWattsDevice = new java.awt.GridBagConstraints();
			constraintsJComboBoxCalcWattsDevice.gridx = 2; constraintsJComboBoxCalcWattsDevice.gridy = 2;
			constraintsJComboBoxCalcWattsDevice.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintsJComboBoxCalcWattsDevice.anchor = java.awt.GridBagConstraints.WEST;
			constraintsJComboBoxCalcWattsDevice.weightx = 1.0;
			constraintsJComboBoxCalcWattsDevice.ipadx = 70;
			constraintsJComboBoxCalcWattsDevice.insets = new java.awt.Insets(4, 1, 6, 41);
			getJPanelCurrentWatts().add(getJComboBoxCalcWattsDevice(), constraintsJComboBoxCalcWattsDevice);

			java.awt.GridBagConstraints constraintsJComboBoxCalcWattsPoint = new java.awt.GridBagConstraints();
			constraintsJComboBoxCalcWattsPoint.gridx = 2; constraintsJComboBoxCalcWattsPoint.gridy = 3;
			constraintsJComboBoxCalcWattsPoint.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintsJComboBoxCalcWattsPoint.anchor = java.awt.GridBagConstraints.WEST;
			constraintsJComboBoxCalcWattsPoint.weightx = 1.0;
			constraintsJComboBoxCalcWattsPoint.ipadx = 70;
			constraintsJComboBoxCalcWattsPoint.insets = new java.awt.Insets(7, 1, 15, 41);
			getJPanelCurrentWatts().add(getJComboBoxCalcWattsPoint(), constraintsJComboBoxCalcWattsPoint);

			java.awt.GridBagConstraints constraintsJCheckBoxDisplayWatts = new java.awt.GridBagConstraints();
			constraintsJCheckBoxDisplayWatts.gridx = 1; constraintsJCheckBoxDisplayWatts.gridy = 1;
			constraintsJCheckBoxDisplayWatts.gridwidth = 2;
			constraintsJCheckBoxDisplayWatts.anchor = java.awt.GridBagConstraints.WEST;
			constraintsJCheckBoxDisplayWatts.ipadx = 49;
			constraintsJCheckBoxDisplayWatts.ipady = -2;
			constraintsJCheckBoxDisplayWatts.insets = new java.awt.Insets(5, 9, 3, 43);
			getJPanelCurrentWatts().add(getJCheckBoxDisplayWatts(), constraintsJCheckBoxDisplayWatts);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJPanelCurrentWatts;
}

/**
 * This method was created in VisualAge.
 * @return java.awt.Dimension
 */
public Dimension getMinimumSize() {
	return getPreferredSize();
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
 */
public Object getValue(Object val) 
{
	com.cannontech.database.data.capcontrol.CapControlSubBus ccBus = (com.cannontech.database.data.capcontrol.CapControlSubBus)val;

	ccBus.getCapControlSubstationBus().setCurrentVarLoadPointID( new Integer(((com.cannontech.database.data.lite.LitePoint)getCalculatedVarPointComboBox().getSelectedItem()).getPointID()) );

	if( !((com.cannontech.database.data.lite.LiteYukonPAObject)getJComboBoxCalcWattsDevice().getSelectedItem()).equals( com.cannontech.database.data.lite.LiteYukonPAObject.LITEPAOBJECT_NONE ) )
		ccBus.getCapControlSubstationBus().setCurrentWattLoadPointID( 
					new Integer(((com.cannontech.database.data.lite.LitePoint)getJComboBoxCalcWattsPoint().getSelectedItem()).getPointID()) );
	else
		ccBus.getCapControlSubstationBus().setCurrentWattLoadPointID( 
					new Integer(com.cannontech.database.data.point.PointTypes.SYS_PID_SYSTEM) );
		
	return val;
}


/**
 * Called whenever the part throws an exception.
 * @param exception java.lang.Throwable
 */
private void handleException(Throwable exception) {

	/* Uncomment the following lines to print uncaught exceptions to stdout */
	System.out.println("--------- UNCAUGHT EXCEPTION ---------");
	exception.printStackTrace(System.out);
}


/**
 * Initializes connections
 * @exception java.lang.Exception The exception description.
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void initConnections() throws java.lang.Exception {
	// user code begin {1}
	// user code end
	getCalculatedVarDeviceComboBox().addActionListener(this);
	getJComboBoxCalcWattsDevice().addActionListener(this);
	getJComboBoxCalcWattsPoint().addActionListener(this);
	getCalculatedVarPointComboBox().addActionListener(this);
	getJCheckBoxDisplayVars().addActionListener(this);
	getJCheckBoxDisplayWatts().addActionListener(this);
}

/**
 * Initialize the class.
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void initialize() {
	try {
		// user code begin {1}
		// user code end
		setName("CCSubstationBusPointSettingsPanel");
		setLayout(new java.awt.GridBagLayout());
		setSize(354, 318);

		java.awt.GridBagConstraints constraintsJPanelCurrentVars = new java.awt.GridBagConstraints();
		constraintsJPanelCurrentVars.gridx = 1; constraintsJPanelCurrentVars.gridy = 1;
		constraintsJPanelCurrentVars.fill = java.awt.GridBagConstraints.BOTH;
		constraintsJPanelCurrentVars.anchor = java.awt.GridBagConstraints.WEST;
		constraintsJPanelCurrentVars.weightx = 1.0;
		constraintsJPanelCurrentVars.weighty = 1.0;
		constraintsJPanelCurrentVars.ipadx = -10;
		constraintsJPanelCurrentVars.ipady = -12;
		constraintsJPanelCurrentVars.insets = new java.awt.Insets(4, 4, 6, 14);
		add(getJPanelCurrentVars(), constraintsJPanelCurrentVars);

		java.awt.GridBagConstraints constraintsJPanelCurrentWatts = new java.awt.GridBagConstraints();
		constraintsJPanelCurrentWatts.gridx = 1; constraintsJPanelCurrentWatts.gridy = 2;
		constraintsJPanelCurrentWatts.fill = java.awt.GridBagConstraints.BOTH;
		constraintsJPanelCurrentWatts.anchor = java.awt.GridBagConstraints.WEST;
		constraintsJPanelCurrentWatts.weightx = 1.0;
		constraintsJPanelCurrentWatts.weighty = 1.0;
		constraintsJPanelCurrentWatts.ipadx = -10;
		constraintsJPanelCurrentWatts.ipady = -9;
		constraintsJPanelCurrentWatts.insets = new java.awt.Insets(7, 4, 38, 14);
		add(getJPanelCurrentWatts(), constraintsJPanelCurrentWatts);
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
	try
	{
		if( getCalculatedVarPointComboBox().getSelectedItem() != null
			 && getJComboBoxCalcWattsPoint().getSelectedItem() != null )
		{
			if( ((com.cannontech.database.data.lite.LitePoint)getCalculatedVarPointComboBox().getSelectedItem()).getPointID()
				 == ((com.cannontech.database.data.lite.LitePoint)getJComboBoxCalcWattsPoint().getSelectedItem()).getPointID() )
			{
				setErrorString("The Calc Var Point can not be the same point as the Watt Var Point");
				return false;
			}
		}
      
      if( getCalculatedVarPointComboBox().getSelectedItem() == null )
      {
         setErrorString("A Var Point needs to be selected");
         return false;
      }
      
	}
	catch( ClassCastException e )
	{
		//strange stuff here, would be the programmers error if this happens
		handleException(e);
		return false;
	}	

	return true;
}


/**
 * Comment
 */
public void jCheckBoxDisplayVars_ActionPerformed(java.awt.event.ActionEvent actionEvent) 
{
   initializeComboBoxes( getCalculatedVarDeviceComboBox() );

   //fire the deviceComboBox event so the points fill into the pointComboBox
   calculatedVarDeviceComboBox_ActionPerformed( actionEvent );

	return;
}


/**
 * Comment
 */
public void jCheckBoxDisplayWatts_ActionPerformed(java.awt.event.ActionEvent actionEvent) 
{
   initializeComboBoxes( getJComboBoxCalcWattsDevice() );

   //fire the wattComboBox event so the points fill into the pointComboBox
   jComboBoxCalcWattsDevice_ActionPerformed( actionEvent );

	return;
}


/**
 * Comment
 */
public void jComboBoxCalcWattsDevice_ActionPerformed(java.awt.event.ActionEvent actionEvent) 
{
	this.fireInputUpdate();

	int deviceID = ((com.cannontech.database.data.lite.LiteYukonPAObject)getJComboBoxCalcWattsDevice().getSelectedItem()).getYukonID();
	
	getJComboBoxCalcWattsPoint().removeAllItems();

   //either use all points or just the WATT points
   com.cannontech.database.data.lite.LitePoint[] altPoints = ALL_POINTS;
   if( getJCheckBoxDisplayVars().isSelected() )
      altPoints = WATT_POINTS;

	for( int i=0; i < altPoints.length; i++)
	{
		if( deviceID == altPoints[i].getPaobjectID() )
			getJComboBoxCalcWattsPoint().addItem( altPoints[i] );
		else if( deviceID < altPoints[i].getPaobjectID() )
			break;
	}

	return;
}


/**
 * main entrypoint - starts the part when it is run as an application
 * @param args java.lang.String[]
 */
public static void main(java.lang.String[] args) {
	try {
		javax.swing.JFrame frame = new javax.swing.JFrame();
		CCSubstationBusPointSettingsPanel aCCSubstationBusPointSettingsPanel;
		aCCSubstationBusPointSettingsPanel = new CCSubstationBusPointSettingsPanel();
		frame.setContentPane(aCCSubstationBusPointSettingsPanel);
		frame.setSize(aCCSubstationBusPointSettingsPanel.getSize());
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
 * @param val java.lang.Object
 */
private void setPointComboBoxes(Object val) 
{	
	com.cannontech.database.data.capcontrol.CapControlSubBus ccBus = (com.cannontech.database.data.capcontrol.CapControlSubBus)val;
	com.cannontech.database.data.lite.LitePoint varPoint = null;
	com.cannontech.database.data.lite.LitePoint wattPoint = null;

	//find the var point we have assigned to the sub bus
	for( int i = 0; i < ALL_POINTS.length; i++ )
	{
		if( ccBus.getCapControlSubstationBus().getCurrentVarLoadPointID().intValue()
			 == ALL_POINTS[i].getPointID() )
		{
			varPoint = ALL_POINTS[i];
		}

		if( ccBus.getCapControlSubstationBus().getCurrentWattLoadPointID().intValue()
			 == ALL_POINTS[i].getPointID() )
		{
			wattPoint = ALL_POINTS[i];
		}

		if( varPoint != null
			 && 
			 ( ccBus.getCapControlSubstationBus().getCurrentWattLoadPointID().intValue() <=
				com.cannontech.database.data.point.PointTypes.SYS_PID_SYSTEM
				|| (wattPoint != null)) )
			break; //help speed up this loop

	}

	//set the device combo boxes and point combo boxes to the appropriate selections
	com.cannontech.database.cache.DefaultDatabaseCache cache =
					com.cannontech.database.cache.DefaultDatabaseCache.getInstance();
	synchronized(cache)
	{
		java.util.List allDevices = cache.getAllDevices();
		java.util.Collections.sort(allDevices);

		for( int i = 0; i < allDevices.size(); i++ )
		{
			com.cannontech.database.data.lite.LiteYukonPAObject device = (com.cannontech.database.data.lite.LiteYukonPAObject)allDevices.get(i);
			
			if( varPoint.getPaobjectID() == device.getYukonID() )
			{
				getCalculatedVarDeviceComboBox().setSelectedItem(device);
				getCalculatedVarPointComboBox().setSelectedItem(varPoint);
			}

			if( wattPoint != null && wattPoint.getPaobjectID() == device.getYukonID() )
			{
				getJComboBoxCalcWattsDevice().setSelectedItem(device);
				getJComboBoxCalcWattsPoint().setSelectedItem(wattPoint);
			}

			if( (wattPoint == null || 
				 (getJComboBoxCalcWattsPoint().getSelectedItem() != null && wattPoint.getPointID() == 
				    ((com.cannontech.database.data.lite.LitePoint)getJComboBoxCalcWattsPoint().getSelectedItem()).getPointID()) )
				 && (getCalculatedVarPointComboBox().getSelectedItem() != null 
					  && varPoint.getPointID() == 
				  	 ((com.cannontech.database.data.lite.LitePoint)getCalculatedVarPointComboBox().getSelectedItem()).getPointID()) )
			{
				break;
			}
			
		}
	}
	
}


private void initializeComboBoxes( javax.swing.JComboBox comboBox )
{
   if( comboBox == getCalculatedVarDeviceComboBox() )
   {
      getCalculatedVarDeviceComboBox().removeAllItems();
      getCalculatedVarPointComboBox().removeAllItems();
   }
   
   if( comboBox == getJComboBoxCalcWattsDevice() )
   {
      getJComboBoxCalcWattsDevice().removeAllItems();
      getJComboBoxCalcWattsPoint().removeAllItems();
   }
   
   
   com.cannontech.database.cache.DefaultDatabaseCache cache =
               com.cannontech.database.cache.DefaultDatabaseCache.getInstance();

   synchronized(cache)
   {
      java.util.List devices = cache.getAllDevices();
      com.cannontech.database.data.lite.LitePoint[] altPoints = null;

      java.util.Collections.sort(devices);
      
      if( getJCheckBoxDisplayVars().isSelected() 
          && comboBox == getCalculatedVarDeviceComboBox() )
      {
         altPoints = VAR_POINTS;
      }


      if( getJCheckBoxDisplayWatts().isSelected()
          && comboBox == getJComboBoxCalcWattsDevice() )
      {
         altPoints = WATT_POINTS;
      }

      com.cannontech.database.data.lite.LiteYukonPAObject liteDevice = null;

         
      for(int i=0;i<devices.size();i++)
      {
         liteDevice = (com.cannontech.database.data.lite.LiteYukonPAObject)devices.get(i);

         setDeviceComboBoxes( liteDevice, altPoints, comboBox );
      }
   }

   
}
/**
 * This method was created in VisualAge.
 * @param val java.lang.Object
 */
public void setValue(Object val ) 
{
      
   if( val == null )
      usedVARPtIDs = com.cannontech.database.db.capcontrol.CapControlSubstationBus.getUsedVARPointIDs(0);
   else
      usedVARPtIDs = com.cannontech.database.db.capcontrol.CapControlSubstationBus.getUsedVARPointIDs(
         ((com.cannontech.database.data.capcontrol.CapControlSubBus)val).getCapControlPAOID().intValue() );
   
   initializeComboBoxes( getCalculatedVarDeviceComboBox() );
   initializeComboBoxes( getJComboBoxCalcWattsDevice() );

	//val will not be null if we are using this panel for an editor
	if( val != null )
		setPointComboBoxes( val );

}

private void setDeviceComboBoxes( 
   final com.cannontech.database.data.lite.LiteYukonPAObject liteDevice,
   final com.cannontech.database.data.lite.LitePoint[] altPoints,
   final javax.swing.JComboBox comboBox )
{     
   int deviceID = liteDevice.getYukonID();
   com.cannontech.database.data.lite.LitePoint litePoint = null;

   if( altPoints == null )
   {
      //we do not have a selected CheckBox, just add all possible devices
      for( int j = 0; j < ALL_POINTS.length; j++ )
      {
         litePoint = ALL_POINTS[j];
   
         if( litePoint.getPaobjectID() == deviceID
             && com.cannontech.database.data.pao.DeviceClasses.isCoreDeviceClass(liteDevice.getPaoClass()) )
         {
            if( litePoint.getPointType() == com.cannontech.database.data.point.PointTypes.ANALOG_POINT
                || litePoint.getPointType() == com.cannontech.database.data.point.PointTypes.CALCULATED_POINT )
            {               
               comboBox.addItem(liteDevice);
               break;
            }
   
         }
         else if( litePoint.getPaobjectID() > deviceID )
            break;
      }

   }
   else
   {
   
      //we have a CheckBox checked, add only devices that have the specifed point UOM
      for( int j = 0; j < altPoints.length; j++ )
      {
         litePoint = (com.cannontech.database.data.lite.LitePoint)altPoints[j];
   
         if( litePoint.getPaobjectID() == deviceID
             && com.cannontech.database.data.pao.DeviceClasses.isCoreDeviceClass(liteDevice.getPaoClass()) )
         {
            if( litePoint.getPointType() == com.cannontech.database.data.point.PointTypes.ANALOG_POINT
                || litePoint.getPointType() == com.cannontech.database.data.point.PointTypes.CALCULATED_POINT )             
            {
               comboBox.addItem(liteDevice);

               break;
            }
   
         }
         else if( litePoint.getPaobjectID() > deviceID )
            break;
         
      }
   }

}
}