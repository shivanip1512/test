package com.cannontech.tdc.toolbar;

import java.util.Date;

/**
 * Insert the type's description here.
 * Creation date: (4/10/00 3:03:50 PM)
 * @author: 
 * @Version: <version>
 */
public class AlarmToolBar extends javax.swing.JToolBar implements java.awt.event.ActionListener
{
	private javax.swing.JButton ivjJToolBarButtonAckAll = null;
	
	private javax.swing.JButton ivjJToolBarButtonMuteAlarms = null;
	private javax.swing.JButton jButtonSilenceAlarms = null;
	
	protected transient com.cannontech.tdc.toolbar.AlarmToolBarListener fieldAlarmToolBarListenerEventMulticaster = null;
	private javax.swing.JButton ivjJToolBarButtonClear = null;
	private javax.swing.JButton ivjJToolBarButtonRefresh = null;
	private javax.swing.JComponent[] currentComponents = null;

	// All alarm buttons must be in here
	private javax.swing.JComponent[] originalComponents = null;
	public static final int ORIGINAL_COMPONENT_COUNT = 5;

	// The height of all the JButtons
	private final int JBUTTON_HEIGHT = getJToolBarButtonAckAll().getHeight();
	private javax.swing.JLabel ivjJLabelViewDate = null;
	private javax.swing.JSeparator ivjJSeparatorDate = null;
	public static final int COMPONENT_INDEX_CLEAR = 0;
	public static final int COMPONENT_INDEX_ACKALL = 1;
	public static final int COMPONENT_INDEX_SEPARTORDATE = 2;
	public static final int COMPONENT_INDEX_DATELABEL = 3;
	public static final int COMPONENT_INDEX_DATE = 4;
	private com.cannontech.common.gui.util.DateComboBox ivjDateJComboBox = null;

/**
 * AlarmToolBar constructor comment.
 */
public AlarmToolBar() {
	super();
	initialize();
}


/**
 * AlarmToolBar constructor comment.
 * @param orientation int
 */
public AlarmToolBar(int orientation) {
	super(orientation);
}


/**
 * Method to handle events for the ActionListener interface.
 * @param e java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
public void actionPerformed(java.awt.event.ActionEvent e) {
	// user code begin {1}

	if (e.getSource() == getJToolBarButtonSilenceAlarms())
	{
		fireJToolBarButtonMuteSilenceAction_actionPerformed(new java.util.EventObject(this)); 
	}
	
	// user code end
	if (e.getSource() == getJToolBarButtonAckAll()) 
		connEtoC1(e);
	if (e.getSource() == getJToolBarButtonMuteAlarms()) 
		connEtoC3(e);
	if (e.getSource() == getJToolBarButtonClear()) 
		connEtoC4(e);
	if (e.getSource() == getJToolBarButtonRefresh()) 
		connEtoC5(e);
	if (e.getSource() == getDateJComboBox()) 
		connEtoC6(e);
	// user code begin {2}

	// make sure the event wasnt handled above first
	if (e.getSource() != getJToolBarButtonAckAll()
		&& e.getSource() != getJToolBarButtonMuteAlarms() && e.getSource() != getJToolBarButtonClear()
		&& e.getSource() != getJToolBarButtonRefresh() && e.getSource() != getJToolBarButtonSilenceAlarms() )
	{
		for( int i = 0; i < getCurrentComponents().length; i++ )
		{
			if( e.getSource() == getCurrentComponents()[i] )
				handleTDCChildButtonPress( e, i );
		}
		
	} 

	// user code end
}

/**
 * 
 * @param newListener com.cannontech.tdc.toolbar.AlarmToolBarListener
 */
public void addAlarmToolBarListener(com.cannontech.tdc.toolbar.AlarmToolBarListener newListener) {
	fieldAlarmToolBarListenerEventMulticaster = com.cannontech.tdc.toolbar.AlarmToolBarListenerEventMulticaster.add(fieldAlarmToolBarListenerEventMulticaster, newListener);
	return;
}


/**
 * connEtoC1:  (JToolBarButtonAckAll.action.actionPerformed(java.awt.event.ActionEvent) --> AlarmToolBar.fireJToolBarButtonAckAllAction_actionPerformed(Ljava.util.EventObject;)V)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC1(java.awt.event.ActionEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.fireJToolBarButtonAckAllAction_actionPerformed(new java.util.EventObject(this));
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}

/**
 * connEtoC3:  (JToolBarButtonMuteAlarms.action.actionPerformed(java.awt.event.ActionEvent) --> AlarmToolBar.fireJToolBarButtonMuteAlarmsAction_actionPerformed(Ljava.util.EventObject;)V)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC3(java.awt.event.ActionEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.fireJToolBarButtonMuteAlarmsAction_actionPerformed(new java.util.EventObject(this));
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}


/**
 * connEtoC4:  (JToolBarButtonClear.action.actionPerformed(java.awt.event.ActionEvent) --> AlarmToolBar.fireJToolBarButtonClearAction_actionPerformed(Ljava.util.EventObject;)V)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC4(java.awt.event.ActionEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.fireJToolBarButtonClearAction_actionPerformed(new java.util.EventObject(this));
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}


/**
 * connEtoC5:  (JToolBarButtonRefresh.action.actionPerformed(java.awt.event.ActionEvent) --> AlarmToolBar.fireJToolBarButtonRefreshAction_actionPerformed(Ljava.util.EventObject;)V)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC5(java.awt.event.ActionEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.fireJToolBarButtonRefreshAction_actionPerformed(new java.util.EventObject(this));
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}


/**
 * connEtoC6:  (DateJComboBox.action.actionPerformed(java.awt.event.ActionEvent) --> AlarmToolBar.dateJComboBox_ActionPerformed(Ljava.awt.event.ActionEvent;)V)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC6(java.awt.event.ActionEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.dateJComboBox_ActionPerformed(arg1);
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
private void dateJComboBox_ActionPerformed(java.awt.event.ActionEvent event) 
{
	java.util.Date newDate = getDateJComboBox().getSelectedDate();
	final java.util.Date today = new java.util.Date();
      
	//if today is before newDate, set the date to today
	if( newDate == null || today.before(newDate) )
	{
		getDateJComboBox().setSelectedDate( today );      
      newDate = today;
	}
   else
   {
      //a valid change, let it go through
      newDate = getDateJComboBox().getSelectedDate();
   }

   //tell all listeners we have a changed date
   java.beans.PropertyChangeEvent pEvent = new java.beans.PropertyChangeEvent(
            this, "ComboDateValue", newDate, newDate );

   fireJToolBarButtonDateChangedAction_actionPerformed( pEvent );

	return;
}


/**
 * Method to support listener events.
 * @param newEvent java.util.EventObject
 */
protected void fireJToolBarButtonAckAllAction_actionPerformed(java.util.EventObject newEvent) {
	if (fieldAlarmToolBarListenerEventMulticaster == null) {
		return;
	};
	fieldAlarmToolBarListenerEventMulticaster.JToolBarButtonAckAllAction_actionPerformed(newEvent);
}


/**
 * Method to support listener events.
 * @param newEvent java.util.EventObject
 */
protected void fireJToolBarButtonClearAction_actionPerformed(java.util.EventObject newEvent) {
	if (fieldAlarmToolBarListenerEventMulticaster == null) {
		return;
	};
	fieldAlarmToolBarListenerEventMulticaster.JToolBarButtonClearAction_actionPerformed(newEvent);
}


/**
 * Method to support listener events.
 * @param newEvent java.beans.PropertyChangeEvent
 */
protected void fireJToolBarButtonDateChangedAction_actionPerformed(java.beans.PropertyChangeEvent newEvent)
{
	if (fieldAlarmToolBarListenerEventMulticaster == null) {
		return;
	};
	fieldAlarmToolBarListenerEventMulticaster.JToolBarJCDateChange_actionPerformed(newEvent);
}


/**
 * Method to support listener events.
 * @param newEvent java.util.EventObject
 */
protected void fireJToolBarButtonRefreshAction_actionPerformed(java.util.EventObject newEvent) {
	if (fieldAlarmToolBarListenerEventMulticaster == null) {
		return;
	};
	fieldAlarmToolBarListenerEventMulticaster.JToolBarButtonRefreshAction_actionPerformed(newEvent);
}


/**
 * Method to support listener events.
 * @param newEvent java.util.EventObject
 */
protected void fireJToolBarButtonMuteAlarmsAction_actionPerformed(java.util.EventObject newEvent) {
	if (fieldAlarmToolBarListenerEventMulticaster == null) {
		return;
	};
	fieldAlarmToolBarListenerEventMulticaster.JToolBarButtonMuteAlarmsAction_actionPerformed(newEvent);
}

protected void fireJToolBarButtonMuteSilenceAction_actionPerformed(java.util.EventObject newEvent) {
	if (fieldAlarmToolBarListenerEventMulticaster == null) {
		return;
	};
	fieldAlarmToolBarListenerEventMulticaster.JToolBarButtonSilenceAlarmsAction_actionPerformed(newEvent);
}

/**
 * 
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private static void getBuilderData() {
/*V1.1
**start of data**
	D0CB838494G88G88G8FF461ACGGGGGGGGGGGG8CGGGE2F5E9ECE4E5F2A0E4E1F4E135BC8DF8D3471174C29267C2FB64CA9BAEA1D71210463D5046CDE9CBDA2E21791C06B43A2B5B12BB52F0ADDF123674ABE96955395204368F59E0938C98DB84D2A09104A4A6B17E8107986300E54946B6824912G878C763374EC894B12101EED998C5C4C6E5BFDCF7213FFC8626FDBBF4D6C4C6C4C6C6C6E6C3E1584539BB3D72FB21A84E155EAC1797BE015A06C7EC8904CAFB58FB35C24F2C31E20795B
	89E513F0ADAF971ED3214EF033A12FD03877FCBE4033GDEBE49103785BE978B69676BDA61C30AF636C25D6D37FBAE0E5E0EF4063513C7605C7A82F8EE00120EEDC359DA016DEF2F5F2A601783EE0372994BAA23A0E4921AB5A737EA706D9514EEBABCE7C371D7A24D87755B157A820011E6B7BC2BABA92D931EEB09F6B756D4B3B9374D9432FF3853389E5B5248E7A5C361DD28BC4D42D708A3F204976DFB61D96B6C6DB96C8D7715F945D0A89C12A52F3FCC76F94B64F0B858A5C6EBEAACC1B15A678128DE0C82
	3DED9F6AD9FAB21F48GBD814EA238D9A751B7E970F9A614397B182EBFDEDF2B68B22DF7263064DED3CADDCD7B290E557BD5DD0543C346257BD1FEA771BD7245814E00C2F5C92FE73A14D6B1DD866DC86B83F8984AB40EBFFC9C7133814E0432380A697861BBCC47364B8F8AAB9F7766ECCC21E3F69555ED7B7B2A0EF2451071B5929FAB4FE29F37C15DB01429D066C0D988A59B4AB69217334F3C8BCFCB17EC8D777505C316C824BE680B9E8F7B24A0FC8607F7498A4EA5F943D11FA050582BBCD92A238FAB8587
	92638E8B356D3440E2E5366B4B423281DF4EEA9D5B66BC7526512C4828BA205A96BFC0627B947A4D831F118F7B0F625FA378F99ABC6D2BFFA3310070922833FF407C5C360169E24F3DC3A82CECB4666968B27D51C346399FD09D32BED0F521BE7E2FF3686315005FDAED482B005284458FA52D1A79787F4E6C9F4B474D906B511098F477F7C54428DC5F9F887A2428626B6E7A51FC2DD7A81FBCE1BE6A17747D5ABEDA7C0D86DD3FACCEFF57B82B1A7AE3DE356A975A76D7D3363D38FAE4ECEC60FD78DBB2D616D7
	53BE4C61789789DEB82862E95FDEA1F8048B21CE06C2693FC54656220349B17298412FBC189CA33663ACC64CB5063CA9D0E87F4A9DB8872EG58F44810D78A45864587E59214E907D89FDB4E160FF39CB9B57DCBF9D7373F8ACFA9AEC74566D0CC96C3DE6914988D08DDC1A9A6A8B49B5A512FDD89FC456D98FBDEE86328941222229C8807C03A58A7C1B045A84D7676CDC89315C82DA750A7C183FD9101495D5DDE8C4F209813CFC6FC22AC2994343E349DE3E2C0BC8592C1GFCFE7E907A2B094C39F360F356A1
	662B3BBB91D7D2EB482B2BE5FE393F3D881E47A03825E0CD0DAD60C5B54468900B51DD2750868F5067FF88F39314E9074147C78CF9AB0E30F17A3BB6960F3675778925CF59F463110C55BA9ADB99F54971D8C95AD9847895D036DAE09D035286A5EEE16BE94649CE0DAC54E9A2E3878B6A0C3AA76351A6348DED24EDBD1950D64E845B5920446A3A94E3985B40F915764B46AE0C6D69E0679CA88BAD2CEF1EEDB753182D97E3920BD2C79482CC7229BDC5E9FFF18E57A0E9C08A49B6A9A205FC70A12644482F4E2D
	D56337BB4CE3EB994163FCE605G0BF17C7F1E5BC471A0C3BB9AD87DCB24DE1465E8202BDF165C12AC87C2BDAA3F71BC6A92CB18AAE30A2D1B3CE86BB230B1379E46ECBD33755673451AB83407433D7D918F0EB0A51EB3493ADD8274F5752C0F7EE047FC4B476117892C565FE0EF9B230F10F7227D1AB8074F98330D6B091D44B6669D4731416655558AFE2D92DFCA3C921AE130E796948314ECA85B2D906FD6660F16631D234839DE9D75F516C8DBFA6D2C7F845B2231F335A3F56C5F58219F7B37F668477EED9D
	2347763FF46845B62D1B5901360687C31A31F2DF874E2F5EF0A8A411814BD9841AD77F4E03F684C28179F44057A3496E40A58966A3AB46FC8719430370F912154D3503EF3239F677253B05799DEF9875723369779DB6AE325258DEE6D567DA2A5317BBF18CCAC1298F9CE58D7707E4A557A46B03020F65905C430377A1EFC90B125457774BB2AC1F5EDECBB0D8AA92134A6843A9C53B4351BE49D7F2C19C904B44C1390CF4C91905D4B76067BD663DFB8F1B351B04526EC0D45293CE39BA12E55B0970FEB990ACA3
	929DDD97403107761C5AD32144DB3160F1BE222333B5A80951D381E990E79522C26CD3B2C0272589DBD32958F2FE709BBA363883C1893210CF550A04B6A62C7FBEC57F479C77266A0BCF537B9356F73F22EF7A0E7BF574F5C95DD1A9667F14B4D624CFD867AAC567613D7FCCF436C1FE588279EDD738BE9C7FC4F475E9A5F6D8F4B55315056A3D4F729B1B8F671B821BA14F8C255A46567C92916B591A2FA4B656F0B0C827CA281BE143F9306714C9B337B9ED74ACE5832F7F7C98750F0CD17F5BB16A371C9E3D5E
	2ADB7FBC37F3F3D7A12E995245FE9CD0B8A99C1306D87E5FD514D4F7CA8C762B7B0A5DDD38E686B5799045EF716A4F61BB3C188375F279BB3DEB845C9FD019F44D4990D1E6A49A6803749193BC654C6919FA6D19538A509F4F274C8D063CAAA814774AAEECB7A1C89A11F75589F696E587DA9F14498D14BF931EDB010652599D0CAE8E7054C615AE83BE1B0EB23AE1A723AB81DC5DD1152E93BE4F63F433DB99DD966016C249EF22F4CD701C5144687E64E2F49900DBGE529C2D7528C6BEBB3232B77B03AE9001B
	8D4550CC698ADAE81FFBDA10EE001FE915F35CE7896E98A4BB6F2DE6FB7563055A3337199F666A1E2710B5DB11BF3BA5798CE33204F28D00DF5C42E26ACE2FB6DF3644E2E1EF00ECA11578E871E957F20AF378DE01A74CBFB139991224B80B3113BE549577188ADA0913875E7BD09839E7EE708564F014EDC72C03E43CFB42D65866B6C118DAF92A03AB20407CF025D1FB2EB7EFE329F1DD8ABFACD76CAFBC267A2120E248C84F6FAE1DE6FEF79EE3B159F96D6F02F2B609B3D23CACB688B9DBCDCD8BBF08243C8D
	D66445F6530E31B33EDEA7590FG1CEB07FC998ABDEFF87784BB372882DC908A9D6B6D035A31BE4BCE77A30B61C967625BCE6C03674467DE6673578B1AE89FFDA2C99135BB6B847359F2BB4BA36FA92ED3ECB0FC6D8E21765AFA235E99E65A46AD4612634AF972F135BF962DB36684FF4E4EDA7B9D4CD7B300E661F1662B178674FCD5F00275DB7F87A7F5ED7D8472A7017579BAA7335DFC02756DFDCE26EF9B60261EE07ABE592A276F822E6F52F79D3A7AFE2CCCC95173D0AB53F3B1577329D6262749E14833BB
	181ECF976969B95941E230567571E2704E8F0F266E176B346F320B59B75D416CB33B19FDD9004BF7B27BAE6F55332F4E496C3B597D716C4BBA562CEB5FF566E1BC56FE60E6B6FA1C4C46DECF01B21FCE81DCA63731FA07564615CEEAE3EDAB3371A71E0FE7E3F7814E379FA7FFD35E876C532EDF73173C273B5FA473C3AB1D9FFC2D496B173F877DB2897053DA597A750CEF13E67DF201A745D08FE4B7ECCD3AC5C2BFC09E23401F1530DD124788AC2F3C4F0FBECC871919D096C34906E2F231B6CC5DE31E6BB744
	C7B9573FF6FAA26773D5AEFE4EE245B321A6005BDD57FF3E04A5827C13B5B2263B5879F9B1199BE63B68B979E6011D1F3FCD70CBB4F89A0797491C073049ED481B6FE63AD61C984B4620A751864AF7F8CC3E29C9FCB4AE17ED0590D55FA3863A7A4A7A877A4A44AE88C2C8AC54C51B8EC9BE36BEBF182F44C63A07697E945141F7F27CBE26F173F2GF33CB870CD03B29B4A82A84B20641F02BC70940B9FEF37DE7E1398BF29129F754C146AFA59F1FDFE3EE5CCBE7B29C4BE6A2FCA622F9D681FD34CDF05CE14E5
	BEBDFEFFD13E871C57277B43E372D51D56535DC8560AB7503653CC773AD614155FB6D15D1FE93DBE5DFFB3A65F76B6BD5D8B0B195F2B5A186E13DCA8EB7904F5FF5BFDFD3AD70D49177B111E6E8F6DC55D71BE44760F78FB90A2EB52C793557D22A7D907E9C9B274F53FB6026F75A4B863A33DF9650616CAC17FFE02178C5E321E7EGD93CEBEA7018042D095C76708E36B7D87091FBDFF62897F1429928BF436604795DF8FE4D56ADE7B890128536E6757560DEBB9C8D7484C28C97A5B8EFB8CA8E05A96E2B7E54
	6755F37C7A67555F716B1FD74F770F7E2EE601BF55F9B6FD2F72C5321FB203FD35D0A8AEC876C9CE007D9C77ED029B86F8AA471D3711FBB4G4F6338EB8D08DB82F0B647B99D440FE78DF9DB4F3238FA2D35841E25GBB2144214C00B29F4A92A8C23BA12F820A9D0A3F1D7114921E496D74BCG7770335A691E9F4F9B70ACA19360675B693E1D728CBAE7AAF1D22A3401B2D96CE0DB2345F2FC14FA54CFD074C358B906ACED19FFD6AF766F613AE26E3316741FCAB356FB133CD16AB086B066B6EB464CF8F5CDFE
	B734036877EBF9332073DE630458A7896C9C3E6744DA0526C18EDBAB391D5387B36019FBCE754156F3A3FDD72A03CBAEF5C06301629F05F6D8291B1AC4B38B6039D08CD016E86A32D34822313C0C6C25734FC3AC1FE7F3426A8B2947F341857D71DCF4817BB3966E0FFA2564B17B5A052D3CCE4DA81AE5298F5DCBE9F691FED90C76C8A3467C9E42CF6BD2716F3B10EADE20FD7710C35BF77BF85F9DA0FDD7F2DE75D16D79117EF26A60120B8FE862D0260CC2BBD3291B13C433G6045D016EB70399D50A79DAADC
	2A79CC7B6E9DF296D087F8E7875BE7BD6B545A391F5B1967C2BBBBB5B2640E11FA266960124B8C204900122921DD144437CC01054EC43C896092A835D01C1ABA1F66F33CB3594E5364ACE18A60E7F4B2BB0BDCDABB2B381D1B0B514E599A9973BBC75A34D08727ADCB21FEA514C2511037DBCCCDDB2D545915E7277214611986E5068EEF068E0E5AF9BB4993B321FE21486CBC67565A79BE37735BFBF1CF763CC6568A78DC50256CD515E71572EC02E7BB14C8574836A783EEBA1459DD49FEFF166C7F6783BE8B8A
	1DA71FF895670688AC59D20CEC96A07B90593BA60FD65783DCD7F9874B0116A4351F8DF03E5710379DCA05D72D23E33A2E175CF764300D40C3824B11A48B75B54E8B8DA11F123C50336B2F12BC60F1D08C1928FEB7D8F16E977B65F04C9B8D8703B1214D0B34F712DCGE55B3C4C6F7FE9A539CFC43C089BF4CA77CBABBB8F1961E5E7E8DF9FC4195DD1318F269BF7C4728AF5BEF206E8E36F9EE6836DFCA8CB2088D0E77631B63E5800ED74017E18DF2579708E2E8D27BD365EB8D0B664DF60E6FA96CE753866E0
	7A9A382C031B30BD3659F4CB10C109F2B8FACC0289BCDAC9E9EE593CCDC323F35627347BC0FC7388BA65AC096BE65BBCCAFB7BA94D4F6D45A36410B3B7C546BA721EC96FDCD8317F5CCEFD7E4433D42E5367AE233FDB38B21898333ABF7413BA3B58A58511DDDBB72D91571E04370D82FB97561DF006AAC874DC2BDAE267DE1EA23DFABB2F1FBA242D6FD46A53F87D5793644FD06A67CBAC574CA27B5E05GAF63384AB862D6GDC504DF07D84E7865846F17FCAF0EDG87B9EE06136538F10E1BC7F0B3G1E43F1
	5D245D85GAF623847896EF900D7F25CBE42DB5883633707614E921C8D60CE0E7B924149GCF66386788EEBA40F3B86E5E8152AE42BDAC36F39C788E9006C965EEBF523CC660951CA744FD6477B35C0F86190FE40EAB66BECA63388E6E23999C375245FC144EF15B5D4CC7860E3BFD0879E889472D98E2BEDA46F1756E11BE6AF5B3FF888106FB504D7CE16238CC22DF8940CD9CD7B844FC544EF1A789AE827054G735BE1E7225FBC4EC43F9DF1B13F4D62F23E6CE2FE4B60385F3BC646164D4D7C1649693A0FB1
	3FE5F15CCD47185F32B9EE5F8E369F4B3F40F07F73BA7365F60E2BFF0379320463AA9CA3FDB95941FCD94B698C8E9603CE0E5B42634D47F1CF14B2DF46B9EE735E113E8C35A67AF2F2EB22AF0F13F312A9A0E39617B30760B2G4E64383F905CA200D7F21C4BC4EC6A851BFA7958A5B89B40ED9C77C0AF732F0F631EA438B400E7F25C5DE48C4D81B81363561071108570D20EFB1864D5AB81B68599AE0774F58940CD9CF71A605A818EF25C1788EE9240B3B8CEA0EDE4G1C99E4FE7B1AAB51EF1984DE44F91EA6
	3E4E6D03FE6FE3383AB2228340359CF78B41B9816E64385F15B11B029C778D8F518B60D99C17CF5EE34F85F891476DEAC35CB200D7F25C05B6E6D3E108619A4A91379B60EA0E5B4E4F89EC9CD7EBE37D608FB15B7F6CCE34FD238761E14EF3AB998769GE7F2DC37CC628360659C9761F3F6EE18612692DC8940F59C579EC31C8760E018697010957314EEB19843FDACFD87B2F3D07B8E247A154A146FC0A605E9CEB6CDF9AE3E61CDE54DBCE3C3DB64E83FC44F1B6E34711C84E8F3A3E4EC5934EB9B72D92014E1
	FF137A17936AF17F36A721FEEF03361E5C37DC232D6FC828C79B4A95FEEA6713EF31F774AB1FCC97025F5C1F723BB7768835AF421E2EB7053435170CD44E0F4EEA7DD5F847B6632A94F252957EB991751D9175175D7996BCB52F734B5C789F3633F881DF0E15B5B9DC4DE79CA7BC963B40725BB5AD18B70F635D021A71103E382B15754552887B1E502DC57C3D71C5163B5EA6EF97F46F99A449FB1C2CAD680BB64E7B476E2D7A3CA44322FCCB099E38DF18FA114563A9976634787DAACFA0621162321277566031
	2392DB7FCE66163E1054978E853CB467BDC356419B20E4F3D9EB5C78BD86BFA448174210A187D11CB39C8B504BD614EF271B79A2B74A784E3AC7665D7774E37BD5D1B207B8357706128B15F157B13483127EC0DFFF9F39642A6C9148389C86B9E93C3D6C5749D8816DE8DBBF99D0ED7D688DA2C70CA749D9CE6652B9A0E3BE17F3540171BEA0C56500B751DA4AA372FD41B24E731C83E33EAB0C17511CA2EC10A41F40FCB3195FA5589EE374F7125CAA60E5B297909A343B145314B9D1E6A4AAF5CB51286423D7FC
	951985244FEB01360953AFA94598409DD79814EF88E16A6ED3688D83CCE7195317BB70DDC9C89AB46B3E22AE117D01589EE50CFFA8E947E645651749F8539B1BE9B165BB07B1F5AE237DD10D693E224F8A62772721ACGBA0A2B0DB39D97810E72F48F2971D26C9EADDE280C86920779B22C35B213F14BA5355D1D1748D98E5459E5566EB4B773FD1C639691DC9A4053E4662FE76264BBB778FEA0A4DB221258988886B57D7CE21C7573FCAE67609033A90B4BA95DDB1CB01EAC41C0CF886FDBA9F29A77B21E6739
	8D5F3F2C5A30100CDB3CEFEA6AE77501AB2A1F2E9C9B5BCF5FA47962EE602F68E73A1E5D415AED67F26F392A36FB11640991289B66BCDFF7B05F25F35CCA877343DC0EBBCA62FB81408BB96E4F252CBDE1007946D72CBFEFBDF6CD9D4BCF960F1C371EA5381B51A7DC1649B4763CD5EEE2BA6C66FC81930EFFDFA0C68610DB42FB25122F190F559BCC4CE68F677926E964DC37D4D07B64515E3167BA37CDFF2E4BA639278F5A0A7076668EA44EF52F9AD57F2CAE571F6B4A49181B81B252391CAC725E1F592A5AC8
	5794F20647E97F6E9AFDDE2CF2713865BCA1D762BCB75945641A86994DE75AD24FF38B887DEE202D6074DB86713CCBCA185F4A86F96CF23A1F3946B33FD15A65FB7563CE4ED17DFA3AECE45C05888E3F6B90616D4EACCB6C1B5BF355B1340364391300F68A277F21E76CB87D250759B717733DF1F2747EB8C872F17455D24EF330AD31BF9CED2CBF364659383C0360CAG2E0AB33E767211717D5755EAFC6F370DBD6774125C3593E486395C37BC636F231706D247499CB2F7CD873933386CABB25EDB6D923D3DBD
	51F0FF4847E83FC0F27FAC20DB0CB6927DEF9EC7AEF23FD3FFFCBEC2663615A0ABFF08359D0B690F41DB495E218A686A06587B62EF2DC1DC906034A176EE681668FB70BCA5DE98F9FB96104EE8B8825DBEC431646637F286BC53F6FD5F8D7EFE9E4B6167FC679EE16ECDFFDC156A1EED7A90DD5797A84F7C56B7891E5AE24EC7BF2E00BA53A5584BC1F1C271C1993E44EC4B0A220EF88B86543748326875135B466CF28C1E9B2AE8BC6C25322DC4D5AA635558DB297C8392DAA016E8GF9449E013FA79D73EEC6F2
	21FCD30BC6631BFAE964D9BF65B38CB13ED9BAB4F381E70032C4D3C779DEE8557BEEE036C217FFD979ED004B09B2ABG36DD9E59CE9B60029ABC0D4F0BF1FC3EE3F9C7012B5DF886BC4A3E495AF0425360627132601CF64FE7FE6F2F2B7476FCAB4E6DB1CED25A1EF5D95D2BB57D7412D1D9A3F108AABA1C9DCC188B700221ADGA100DFD0AE292C28A8B56F5B3FC719FB569528FC6FB84743C7E9630E715A296469CE367F2670AF889C6A8F86299EE56BD5BD268F0CDF0F6786346785B43E1FD9CF4EECAE736FFF
	8C6ECA956300443BE3EEB98A4A3376B752BE8D32BEF54747EBAB354532D135E54A50787BA230C9657B40BDFE9FBC60CE746514A2D54E4371714BA998CA6CA3671056374A7B3D3D936B775F3A08AF5D6FA8B6BED82C09379D2F27566DE0A5D5CD21DD743AD697E55DB429325EF20CDF5682877397050BF9ECD239BBDFD565FE2FF47CF28B770E74571F0B097DD696CB0D2609796F8ED3E25FBA36283AFDF1E0945D2A19EE146F6AD6156FEB2D63612334FFEBCD7455B15784473DABD17F3BDFD375F8F1DC7AD35A06
	41113E3D310C785645E26B60C1D5F63AE794598792E52F74CC4C26D75B46202FF7F8BC0E631C3E3BAD51876DB52A1EB56563511372E59E524C8FC563677BF52DE6ED10C7198F76D5D4EB67234F9ED6797CCE13E2B36131060361E85920BFA0ABE3A66308CA5B9A9BFF9BBF2DD3794E584743C767746F2E41B5105F1F21291BD277689A54953F539D900301CB64D5B41D61697AF02F856B17417A10BFEC48AB05D28745B34C560B46BCDCAF7097C9C666D48D3E9E09F5B43F6F131F7A7ED1C57E583FDDF2B01F6457
	E1D98E7721FC06BF120FF90C9C0E9024F227A09F7306E82047AFD3D276FD33FC127BCA5D84CD4F576FAFE2677011E17E8EF3081D61CF63383D6A7BC30EBBCEF08604B96EBB74BDAE4005D798EEA35DD782DC45F1320B5155F15C23045783F01C63DE653ACC6138EC32DF1E89F006AAEF9B53A513637E94A56F9DGDEF105657FAF106FCF96DCB56415C031C371C109C319FA1575ED665A92A1F5AEE09707427DB2695FA89EBEAA3F6D319E77ECB1A5C7203887AC297B7CA14B58FD7EDD8B6AAFC1DFF906A24A1D30
	7996F207233BBBA67158FA4CD248F14DB136AF25FB2D6F950D3DD7ACA667F433407E0C2BEC0FE6F30D3DC773397477E84324EF97032C25D7599E6D47F17DBD5AEFC81F1B2E99724C57986D5B2E44BD7DCDB6016BFA9711ED875AB6CE7FF4FC6CBDFDF61C1D77CF3E46765EC7C8ECE5GBC0F4BDAC75A6E86B94A99A6E9EFA954E7F33E3BC82C6D1EE4482B184478B644C6BFE7AC0931765BA7715F342ACF9D9FFF299FBBBE56D8756704826B48B9E113D5FFCE58E2554E89545FDFE9D0675BDD2E9D234D3725668A
	FC3FC5798C0DAA5F0371D178AA9379FEFFD4651B56BAFAAECAB9A9DFD1334A776D78E8EB5D7B25664A83FCADDFB7E496C6DD1BDD4DC71BBC340D77DB54B6FEB3DACE445BD04EF29DAA1FFF5B2EE43EC6881212DDBED8FB42BD3B40A4605D47F99FF32A3C4B226337EB5B0E7158C5FFCB719FF3FF8493496C970455312830114B06D813645812017DB5B94F5D1BE394F21B05B5B4401A05DC0C12E6E1B58922E6A18782AC0EBF021570A78B5BAC5E5ED0F8B0087316F9C03955E49649352658C561EAB2877294G0F
	195D0012054D64C694C7505FBBC8493CA111DA9636AA97402051A0ED589C43B329149272948656F24C8C39C240E73685E21120B81412EFBDEDD9A1138533F223CC0A19E53FE4760932E88E07486718B7AA61EC102710680B043EA89A594F164FBD7D5F33BBECA623E0046E70CB547F2B8783BE59CFFAG3B54C7A4EE7AC421336306DA478DFDB8EEA88B3C31A1B11BDCF55F169B8A7FE9FF3FD148EF9456C0269F710B60897215C33B302E0B04269485DAD9A8A4DF1DAC875EF242FB48AE983DB108CF956FC5BC2D
	F5C7C42F84A4EBFDE23417C8C0D55EBCAB2C6D0E76F41186E441F83671ACB4CBA02CFE4BAE3C821DEB4537D4F1D99636282249FBC92562909DA6FF033F8879CBB2B3811205F59849E63C3AD7E366F578E7CA00DE51C2B9CBB9043900A5890A8A6B93261E476EFE7FD7061F1F0A6588EB1A0557F035F3CBD11288CBBE3C7C2A7CD2DD2330B60297D0F13D06719FA30F55B1F9A8885EB6F53B7097B1E232643330949B5C3B197AG8F174B1B4418DF46B1890E4D8D61EF6A89C534BAA806FA4AE93ECD3D334AD2C78C
	A9D006C131FAC44D2594FFF763DBBBDB1E1F1BA3989B05D702E15157A8FAA1B3EB96564AFE9CD46120AF9EB9C2856513179418F4640038ED5CFCB4368CB3842EF4EF7C2EA96B172EEFB48AC2BCAA1493CA6C36B2CE61337A45A8B4A5C51BE509DCA2584D6BBE733A7C76FF9C5D52EE94D6A9C6226B7745A3B286E8DFA08873429A9B1968C9279C10C15B28980A6197FCC1B3F312E62C0AB7D07679A7AA3E7015174E1086D4D6D9F8C30D369EA95CD7EE696E8E04A4AEG7C8A62F7F071C8D316C4D3943FEDF34BD3
	FF6B02B83089B9FD959564FFA5793F8F7FD792CCA541D402D1C7160F1C3434FF4975F1E6B3899E3E54E07717B5C28F4ABFFB6109FB1E29DD3E8C2CEBD486B2F4C221CAF5223F2FCB0A5EB47B8BBB56CDDBF2AB76D184C3A50EF2EB6209F12E1C176E3926FDFF3E62462214F7018A5368B9696E34646FC38F103DD2A7600753D83E7603BC7CFD5DD467606EBED8EEA0736B3504FCF8A6DEBFA4CB6AF740AE67237C9823D1AAE95DBF107D5A858D4F7F83D0CB878855A2E2F1B69EGGCCD9GGD0CB818294G94G
	88G88G8FF461AC55A2E2F1B69EGGCCD9GG8CGGGGGGGGGGGGGGGGGE2F5E9ECE4E5F2A0E4E1F4E1D0CB8586GGGG81G81GBAGGGF09EGGGG
**end of data**/
}

/**
 * Insert the method's description here.
 * Creation date: (8/7/00 5:06:50 PM)
 * @return javax.swing.JButton[]
 */
public javax.swing.JComponent[] getCurrentComponents() {
	return currentComponents;
}


/**
 * Return the DateJComboBox property value.
 * @return com.cannontech.common.gui.util.DateComboBox
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private com.cannontech.common.gui.util.DateComboBox getDateJComboBox() {
	if (ivjDateJComboBox == null) {
		try {
			ivjDateJComboBox = new com.cannontech.common.gui.util.DateComboBox();
			ivjDateJComboBox.setName("DateJComboBox");
			ivjDateJComboBox.setPreferredSize(new java.awt.Dimension(208, 23));
			ivjDateJComboBox.setBackground(java.awt.Color.white);
			ivjDateJComboBox.setMinimumSize(new java.awt.Dimension(50, 23));
			ivjDateJComboBox.setMaximumSize(new java.awt.Dimension(208, 23));
			// user code begin {1}

         ivjDateJComboBox.setDateFormat(
            new java.text.SimpleDateFormat("MMMMMMMM d, yyyy") );

			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjDateJComboBox;
}


/**
 * Return the JLabelHistoryDate property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getJLabelViewDate() {
	if (ivjJLabelViewDate == null) {
		try {
			ivjJLabelViewDate = new javax.swing.JLabel();
			ivjJLabelViewDate.setName("JLabelViewDate");
			ivjJLabelViewDate.setText("View Date: ");
			ivjJLabelViewDate.setMaximumSize(new java.awt.Dimension(65, 16));
			ivjJLabelViewDate.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);
			ivjJLabelViewDate.setPreferredSize(new java.awt.Dimension(65, 16));
			ivjJLabelViewDate.setFont(new java.awt.Font("dialog", 0, 12));
			ivjJLabelViewDate.setMinimumSize(new java.awt.Dimension(65, 16));
			ivjJLabelViewDate.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJLabelViewDate;
}


/**
 * Return the JSeparatorDate property value.
 * @return javax.swing.JSeparator
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JSeparator getJSeparatorDate() {
	if (ivjJSeparatorDate == null) {
		try {
			ivjJSeparatorDate = new javax.swing.JSeparator();
			ivjJSeparatorDate.setName("JSeparatorDate");
			ivjJSeparatorDate.setPreferredSize(new java.awt.Dimension(20, 0));
			ivjJSeparatorDate.setMinimumSize(new java.awt.Dimension(10, 0));
			ivjJSeparatorDate.setMaximumSize(new java.awt.Dimension(1000, 0));
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJSeparatorDate;
}


/**
 * Return the JToolBarButtonAckAll property value.
 * @return javax.swing.JButton
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JButton getJToolBarButtonAckAll() {
	if (ivjJToolBarButtonAckAll == null) {
		try {
			ivjJToolBarButtonAckAll = new javax.swing.JButton();
			ivjJToolBarButtonAckAll.setName("JToolBarButtonAckAll");
			ivjJToolBarButtonAckAll.setToolTipText("Acknowledge viewable alarms");
			ivjJToolBarButtonAckAll.setMnemonic('A');
			ivjJToolBarButtonAckAll.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
			ivjJToolBarButtonAckAll.setMargin(new java.awt.Insets(0, 0, 0, 0));
			ivjJToolBarButtonAckAll.setMinimumSize(new java.awt.Dimension(50, 23));
			ivjJToolBarButtonAckAll.setText("Ack Viewable");
			ivjJToolBarButtonAckAll.setMaximumSize(new java.awt.Dimension(100, 23));
			ivjJToolBarButtonAckAll.setVerticalTextPosition(javax.swing.SwingConstants.CENTER);
			ivjJToolBarButtonAckAll.setBorderPainted(true);
			ivjJToolBarButtonAckAll.setIcon(null);
			ivjJToolBarButtonAckAll.setPreferredSize(new java.awt.Dimension(100, 23));
			ivjJToolBarButtonAckAll.setRolloverEnabled(false);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJToolBarButtonAckAll;
}


/**
 * Return the JToolBarButtonClear property value.
 * @return javax.swing.JButton
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JButton getJToolBarButtonClear() {
	if (ivjJToolBarButtonClear == null) {
		try {
			ivjJToolBarButtonClear = new javax.swing.JButton();
			ivjJToolBarButtonClear.setName("JToolBarButtonClear");
			ivjJToolBarButtonClear.setToolTipText("Clears Valid Displays");
			ivjJToolBarButtonClear.setMnemonic('C');
			ivjJToolBarButtonClear.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
			ivjJToolBarButtonClear.setDisabledSelectedIcon(null);
			ivjJToolBarButtonClear.setMargin(new java.awt.Insets(0, 0, 0, 0));
			ivjJToolBarButtonClear.setMinimumSize(new java.awt.Dimension(50, 23));
			ivjJToolBarButtonClear.setText("Clear Display");
			ivjJToolBarButtonClear.setMaximumSize(new java.awt.Dimension(81, 23));
			ivjJToolBarButtonClear.setDisabledIcon(null);
			ivjJToolBarButtonClear.setVerticalTextPosition(javax.swing.SwingConstants.CENTER);
			ivjJToolBarButtonClear.setIcon(null);
			ivjJToolBarButtonClear.setPreferredSize(new java.awt.Dimension(81, 23));
			ivjJToolBarButtonClear.setEnabled(false);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJToolBarButtonClear;
}
/**
 * Return the JToolBarButtonRefresh property value.
 * @return javax.swing.JButton
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JButton getJToolBarButtonRefresh() {
	if (ivjJToolBarButtonRefresh == null) {
		try {
			ivjJToolBarButtonRefresh = new javax.swing.JButton();
			ivjJToolBarButtonRefresh.setName("JToolBarButtonRefresh");
			ivjJToolBarButtonRefresh.setToolTipText("Refreshes the data on the screen");
			ivjJToolBarButtonRefresh.setMnemonic('R');
			ivjJToolBarButtonRefresh.setText("Refresh");
			ivjJToolBarButtonRefresh.setMaximumSize(new java.awt.Dimension(61, 23));
			ivjJToolBarButtonRefresh.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
			ivjJToolBarButtonRefresh.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
			ivjJToolBarButtonRefresh.setIcon(null);
			ivjJToolBarButtonRefresh.setPreferredSize(new java.awt.Dimension(61, 23));
			ivjJToolBarButtonRefresh.setMargin(new java.awt.Insets(0, 0, 0, 0));
			ivjJToolBarButtonRefresh.setMinimumSize(new java.awt.Dimension(50, 23));
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJToolBarButtonRefresh;
}

 
  /**
	* Return the JToolBarButtonSilenceAlarms property value.
	* @return javax.swing.JButton
	*/
  public javax.swing.JButton getJToolBarButtonSilenceAlarms() {
	  if (jButtonSilenceAlarms == null) {
		  try {
				jButtonSilenceAlarms = new javax.swing.JButton();
				jButtonSilenceAlarms.setName("JToolBarButtonSilenceAlarms");
				jButtonSilenceAlarms.setToolTipText("Turns off the audible for all current alarms");
				jButtonSilenceAlarms.setMnemonic('S');
				jButtonSilenceAlarms.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
				jButtonSilenceAlarms.setMargin(new java.awt.Insets(0, 0, 0, 0));
				jButtonSilenceAlarms.setMinimumSize(new java.awt.Dimension(50, 23));
				jButtonSilenceAlarms.setAutoscrolls(false);
				jButtonSilenceAlarms.setText("Silence");
				jButtonSilenceAlarms.setMaximumSize(new java.awt.Dimension(91, 23));
				jButtonSilenceAlarms.setVerticalTextPosition(javax.swing.SwingConstants.CENTER);
				jButtonSilenceAlarms.setIcon(null);
				jButtonSilenceAlarms.setBorderPainted(true);
				jButtonSilenceAlarms.setPreferredSize(new java.awt.Dimension(91, 23));
				jButtonSilenceAlarms.setRolloverEnabled(false);
				jButtonSilenceAlarms.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
			  // user code begin {1}
			  // user code end
		  } catch (java.lang.Throwable ivjExc) {
			  // user code begin {2}
			  // user code end
			  handleException(ivjExc);
		  }
	  }
	  return jButtonSilenceAlarms;
  }


/**
 * Return the JToolBarButtonMuteAlarms property value.
 * @return javax.swing.JButton
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
public javax.swing.JButton getJToolBarButtonMuteAlarms() {
	if (ivjJToolBarButtonMuteAlarms == null) {
		try {
			ivjJToolBarButtonMuteAlarms = new javax.swing.JButton();
			ivjJToolBarButtonMuteAlarms.setName("JToolBarButtonMuteAlarms");
			ivjJToolBarButtonMuteAlarms.setToolTipText("Turns off the audible for all current and future alarms");
			ivjJToolBarButtonMuteAlarms.setMnemonic('M');
			ivjJToolBarButtonMuteAlarms.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
			ivjJToolBarButtonMuteAlarms.setMargin(new java.awt.Insets(0, 0, 0, 0));
			ivjJToolBarButtonMuteAlarms.setMinimumSize(new java.awt.Dimension(50, 23));
			ivjJToolBarButtonMuteAlarms.setAutoscrolls(false);
			ivjJToolBarButtonMuteAlarms.setText("Mute");
			ivjJToolBarButtonMuteAlarms.setMaximumSize(new java.awt.Dimension(61, 23));
			ivjJToolBarButtonMuteAlarms.setVerticalTextPosition(javax.swing.SwingConstants.CENTER);
			ivjJToolBarButtonMuteAlarms.setIcon(null);
			ivjJToolBarButtonMuteAlarms.setBorderPainted(true);
			ivjJToolBarButtonMuteAlarms.setPreferredSize(new java.awt.Dimension(61, 23));
			ivjJToolBarButtonMuteAlarms.setRolloverEnabled(false);
			ivjJToolBarButtonMuteAlarms.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJToolBarButtonMuteAlarms;
}


/**
 * Insert the method's description here.
 * Creation date: (7/6/2001 12:26:25 PM)
 * @return javax.swing.JComponent[]
 */
private javax.swing.JComponent[] getOriginalComoponents() 
{
	if( originalComponents == null )
	{
		originalComponents = new javax.swing.JComponent[ORIGINAL_COMPONENT_COUNT];
		originalComponents[0] = getJToolBarButtonClear();
		originalComponents[1] = getJToolBarButtonAckAll();
		originalComponents[2] = getJSeparatorDate();
		originalComponents[3] = getJLabelViewDate();
		originalComponents[4] = getDateJComboBox();
	}
	
	return originalComponents;
}


/**
 * Called whenever the part throws an exception.
 * @param exception java.lang.Throwable
 */
private void handleException(java.lang.Throwable exception) 
{
	/* Uncomment the following lines to print uncaught exceptions to stdout */
	com.cannontech.clientutils.CTILogger.info("--------- UNCAUGHT EXCEPTION ---------");
	com.cannontech.clientutils.CTILogger.error( exception.getMessage(), exception );;
}


/**
 * Insert the method's description here.
 * Creation date: (8/7/00 5:37:11 PM)
 * @param e java.awt.event.ActionEvent
 */
public void handleTDCChildButtonPress(java.awt.event.ActionEvent e, int index ) 
{
	
}


/**
 * Initializes connections
 * @exception java.lang.Exception The exception description.
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void initConnections() throws java.lang.Exception {
	// user code begin {1}

	getJToolBarButtonSilenceAlarms().addActionListener(this);
	
	// user code end
	getJToolBarButtonAckAll().addActionListener(this);
	getJToolBarButtonMuteAlarms().addActionListener(this);
	getJToolBarButtonClear().addActionListener(this);
	getJToolBarButtonRefresh().addActionListener(this);
	getDateJComboBox().addActionListener(this);
}

/**
 * Initialize the class.
 */
private void initialize() 
{
	/**********************************************
	 * 
	 * ignore this method as the real INIT stuff is done in setCurrentComponents()
	 * 
	 *******************************/

	try 
	{

		setName("AlarmToolBar");
		setAutoscrolls(false);
		setOpaque(true);
		setSize(773, 32);
		setMargin(new java.awt.Insets(0, 0, 0, 0));
		setOrientation(javax.swing.SwingConstants.HORIZONTAL);
		addSeparator();
		add(getJToolBarButtonSilenceAlarms(), getJToolBarButtonSilenceAlarms().getName());
		add(getJToolBarButtonMuteAlarms(), getJToolBarButtonMuteAlarms().getName());
		add(getJToolBarButtonRefresh(), getJToolBarButtonRefresh().getName());
		addSeparator();
		add(getJToolBarButtonClear(), getJToolBarButtonClear().getName());
		add(getJToolBarButtonAckAll(), getJToolBarButtonAckAll().getName());
		add(getJSeparatorDate(), getJSeparatorDate().getName());
		add(getJLabelViewDate());
		add(getDateJComboBox(), getDateJComboBox().getName());
		initConnections();
	} catch (java.lang.Throwable ivjExc) 
	{
		handleException(ivjExc);
	}


	currentComponents = new javax.swing.JComponent[ getOriginalComoponents().length ];
	setOriginalButtons();
}

/**
 * Insert the method's description here.
 * Creation date: (8/7/00 6:16:39 PM)
 * @return boolean
 */
private boolean isOriginalComponentsSet() 
{
	if( getOriginalComoponents().length != getCurrentComponents().length )
		return false;
		
	for( int i = 0; i < getOriginalComoponents().length; i++ )
	{
		if( getOriginalComoponents()[i].equals( getCurrentComponents()[i] ) )
			continue;
		else
			return false;
	}
	
	return false;
}


/**
 * Insert the method's description here.
 * Creation date: (8/7/00 5:06:50 PM)
 * @param newCurrentButtons javax.swing.JButton[]
 */
public void setCurrentComponents(javax.swing.JComponent[] newCurrentComponents ) 
{
	// remove all the previous buttons and their listeners
	removeAll();

	//add the default buttons that are on every screen
	addSeparator();
	add(getJToolBarButtonSilenceAlarms(), getJToolBarButtonSilenceAlarms().getName());
	add(getJToolBarButtonMuteAlarms(), getJToolBarButtonMuteAlarms().getName());
	add(getJToolBarButtonRefresh(), getJToolBarButtonRefresh().getName());
	addSeparator();		

	//We must re-add the PopUp listener
	//getJCPopupFieldDate().addValueListener( this );

	currentComponents = newCurrentComponents;

	// add the new buttons and their listeners	
	for( int i = 0; i < newCurrentComponents.length; i++ )
	{
		newCurrentComponents[i].setSize( newCurrentComponents[i].getWidth(), JBUTTON_HEIGHT );
		add( newCurrentComponents[i], newCurrentComponents[i].getName() );		
	}
		
}


/**
 * Insert the method's description here.
 * Creation date: (8/7/00 5:06:50 PM)
 * @param newCurrentButtons javax.swing.JButton[]
 */
public void setJComponentEnabled( int buttonPosition, boolean enabled )
{
	if( buttonPosition >= 0 && buttonPosition < getCurrentComponents().length )
	{
		boolean lastState = getCurrentComponents()[buttonPosition].isEnabled();
		
		getCurrentComponents()[buttonPosition].setEnabled( enabled );
		
		//special case, set the Calendar to todays date if it is being disabled
		if( enabled != lastState && getCurrentComponents()[buttonPosition] instanceof com.klg.jclass.field.JCPopupField )
		{
			getDateJComboBox().setSelectedDate( new java.util.Date() );
		}
		
	}
	else
		throw new IllegalArgumentException("JComponent Enablement for toolbar button["+buttonPosition+"] is not valid");
}

public void setSelectedDate( Date newDate_ )
{
	for( int i = 0; i < getCurrentComponents().length; i++ )
	{
		if( getCurrentComponents()[i] == getDateJComboBox() )
		{
			if( getDateJComboBox().isEnabled() )
				getDateJComboBox().setSelectedDate( newDate_ );
				
			break;
		}
	}
}

/**
 * Insert the method's description here.
 * Creation date: (8/7/00 5:06:50 PM)
 * @param newCurrentButtons javax.swing.JButton[]
 */
public void setOriginalButtons()
{
	if( !isOriginalComponentsSet() )
		setCurrentComponents( getOriginalComoponents() );
}
}