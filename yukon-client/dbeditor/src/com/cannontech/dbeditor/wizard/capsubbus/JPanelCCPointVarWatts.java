package com.cannontech.dbeditor.wizard.capsubbus;

import java.util.Arrays;
import java.util.Iterator;
import java.util.TreeSet;

import com.cannontech.common.util.CtiUtilities;
import com.cannontech.common.util.NativeIntVector;
import com.cannontech.database.cache.DefaultDatabaseCache;
import com.cannontech.database.cache.functions.PAOFuncs;
import com.cannontech.database.cache.functions.PointFuncs;
import com.cannontech.database.data.lite.LiteComparators;
import com.cannontech.database.data.lite.LitePoint;
import com.cannontech.database.data.lite.LiteYukonPAObject;
import com.cannontech.database.data.pao.DeviceClasses;
import com.cannontech.database.data.point.PointTypes;
import com.cannontech.database.data.point.PointUnits;

/**
 * Insert the type's description here.
 * Creation date: (7/19/2002 10:42:43 AM)
 * @author: 
 */
public class JPanelCCPointVarWatts extends javax.swing.JPanel implements java.awt.event.ActionListener 
{
   //store this object locally so we dont have to hit the database every time
   // the user clicks a VAR device
//	private final LitePoint[] ALL_POINTS;
//   private final LitePoint[] VAR_POINTS;
//   private final LitePoint[] WATT_POINTS;

	//used to store the current set of UofM ids, when null we are looking at all UofM ids
	private int[] currentVarUofMIDset = null;
	private int[] currentWattUofMIDset = null;
	
	
	
	private NativeIntVector usedVARPtIDs = new NativeIntVector(10);
   
	private javax.swing.JCheckBox ivjJCheckBoxDisplayVars = null;
	private javax.swing.JCheckBox ivjJCheckBoxDisplayWatts = null;
	private javax.swing.JComboBox ivjJComboBoxCalcWattsDevice = null;
	private javax.swing.JComboBox ivjJComboBoxCalcWattsPoint = null;
	private javax.swing.JComboBox ivjJComboBoxVarDevice = null;
	private javax.swing.JComboBox ivjJComboBoxVarPoint = null;
	private javax.swing.JLabel ivjJLabelCalcWattsDevice = null;
	private javax.swing.JLabel ivjJLabelCalcWattsPoint = null;
	private javax.swing.JLabel ivjJLabelVarDevice = null;
	private javax.swing.JLabel ivjJLabelVarPoint = null;
	private javax.swing.JPanel ivjJPanelCurrentVars = null;
	private javax.swing.JPanel ivjJPanelCurrentWatts = null;

/**
 * JPanelCCPointVarWatts constructor comment.
 */
public JPanelCCPointVarWatts() 
{
	super();
/*
   com.cannontech.database.cache.DefaultDatabaseCache cache =
               com.cannontech.database.cache.DefaultDatabaseCache.getInstance();

   synchronized(cache)
   {
      java.util.List ptList = cache.getAllPoints();
      ALL_POINTS = new LitePoint[ptList.size()];
      ptList.toArray( ALL_POINTS );
      
      java.util.Arrays.sort(ALL_POINTS, com.cannontech.database.data.lite.LiteComparators.litePointDeviceIDComparator);
   }   
*/

	//init our UofM point measurements
/*
   VAR_POINTS = PointFuncs.getLitePointsByUOMID(
            PointUnits.CAP_CONTROL_VAR_UOMIDS);            
   java.util.Arrays.sort(VAR_POINTS, com.cannontech.database.data.lite.LiteComparators.litePointDeviceIDComparator);


   WATT_POINTS = PointFuncs.getLitePointsByUOMID(
            PointUnits.CAP_CONTROL_WATTS_UOMIDS);   
   java.util.Arrays.sort(WATT_POINTS, com.cannontech.database.data.lite.LiteComparators.litePointDeviceIDComparator);
*/
   
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
	if (e.getSource() == getJCheckBoxDisplayVars()) 
		connEtoC1(e);
	if (e.getSource() == getJCheckBoxDisplayWatts()) 
		connEtoC2(e);
	if (e.getSource() == getJComboBoxVarDevice()) 
		connEtoC3(e);
	if (e.getSource() == getJComboBoxCalcWattsDevice()) 
		connEtoC4(e);
	if (e.getSource() == getJComboBoxVarPoint()) 
		connEtoC5(e);
	if (e.getSource() == getJComboBoxCalcWattsPoint()) 
		connEtoC6(e);
	// user code begin {2}

	//tell everyone our panel changed
	if( e.getSource() != getJCheckBoxDisplayVars()
	    && e.getSource() != getJCheckBoxDisplayWatts() )
	{
		fireActionPerformed( new java.awt.event.ActionEvent(
					  				  JPanelCCPointVarWatts.this,
					  				  e.getID(),
									  e.getActionCommand(),
									  e.getModifiers()) );
	}
	
	
	// user code end
}


/**
 * adds an ActionListener to the button
 */
public void addActionListener(java.awt.event.ActionListener l) 
{
	listenerList.add(java.awt.event.ActionListener.class, l);
}

/**
 * connEtoC1:  (JCheckBoxDisplayVars.action.actionPerformed(java.awt.event.ActionEvent) --> JPanelCCPointVarWatts.jCheckBoxDisplayVars_ActionPerformed(Ljava.awt.event.ActionEvent;)V)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC1(java.awt.event.ActionEvent arg1) {
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
 * connEtoC2:  (JCheckBoxDisplayWatts.action.actionPerformed(java.awt.event.ActionEvent) --> JPanelCCPointVarWatts.jCheckBoxDisplayWatts_ActionPerformed(Ljava.awt.event.ActionEvent;)V)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC2(java.awt.event.ActionEvent arg1) {
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
 * connEtoC3:  (JComboBoxVarDevice.action.actionPerformed(java.awt.event.ActionEvent) --> JPanelCCPointVarWatts.jComboBoxVarDevice_ActionPerformed(Ljava.awt.event.ActionEvent;)V)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC3(java.awt.event.ActionEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.jComboBoxVarDevice_ActionPerformed(arg1);
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}


/**
 * connEtoC4:  (JComboBoxCalcWattsDevice.action.actionPerformed(java.awt.event.ActionEvent) --> JPanelCCPointVarWatts.jComboBoxCalcWattsDevice_ActionPerformed(Ljava.awt.event.ActionEvent;)V)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC4(java.awt.event.ActionEvent arg1) {
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
 * connEtoC5:  (JComboBoxVarPoint.action.actionPerformed(java.awt.event.ActionEvent) --> JPanelCCPointVarWatts.jComboBoxVarPoint_ActionPerformed(Ljava.awt.event.ActionEvent;)V)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC5(java.awt.event.ActionEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.jComboBoxVarPoint_ActionPerformed(arg1);
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}


/**
 * connEtoC6:  (JComboBoxCalcWattsPoint.action.actionPerformed(java.awt.event.ActionEvent) --> JPanelCCPointVarWatts.jComboBoxCalcWattsPoint_ActionPerformed(Ljava.awt.event.ActionEvent;)V)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC6(java.awt.event.ActionEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.jComboBoxCalcWattsPoint_ActionPerformed(arg1);
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}


/*
 * Notify all listeners that have registered interest for
 * notification on this event type.  The event instance 
 * is lazily created using the parameters passed into 
 * the fire method.
 * @see EventListenerList
 */
protected void fireActionPerformed(java.awt.event.ActionEvent event) 
{
	// Guaranteed to return a non-null array
	Object[] listeners = listenerList.getListenerList();
	java.awt.event.ActionEvent e = null;

	// Process the listeners last to first, notifying
	// those that are interested in this event
	for (int i = listeners.length-2; i>=0; i-=2) {
		if (listeners[i]==java.awt.event.ActionListener.class) {
			// Lazily create the event:
			if (e == null) 
			{
				  e = new java.awt.event.ActionEvent(
					  				  JPanelCCPointVarWatts.this,
									  java.awt.event.ActionEvent.ACTION_PERFORMED,
									  event.getActionCommand(),
									  event.getModifiers() );
			}

			((java.awt.event.ActionListener)listeners[i+1]).actionPerformed(e);
		}          
	}

}


/**
 * 
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private static void getBuilderData() {
/*V1.1
**start of data**
	D0CB838494G88G88G6BD573ACGGGGGGGGGGGG8CGGGE2F5E9ECE4E5F2A0E4E1F4E14DBC8DD8D45715F697D35B58862B09441806A89AD40C78D3C3A2C9B0A58DC653900351A436B1DDEC5C5434B431DB52351B47B0E44020E200CAD6EDA67EC2644F3F2C0BE8C090FFC288D58B23218D83730051E1E6BA739050D1775CFB5F3973664D9B900C5D70FD07776E39671CFB6E39677E1DFB5F88B99F06262568F284A1A5CD107F7EA7C590EA9B85C1FA22E28962723EDFE79094FF9F81EC907EE327
	9E1E7910B7BC314E10A5ACBC158E69F2C8073FDCE758886F5902E9ED73677092201C031077DAE409296FF27E50036598E8DAE8480467BA008A40D9GA23FCF4A4FEB48137143A03DCEFEA710229384679D04A6ABA5CF01DFA553D5C31E89A0A718506CEB58AC67C7810E504443B3718EC63B9A1E99D43B21E587E53A269121422A27376A5612329472911A02996F9079E994A6D3C3248A12271A582C7EE27B11D6C7E7F12B59EEF758A53135235852A2DA2C1243D55CED3DECF6D9A04B696EEAE969F217151D2AB7
	5BC5DBD5D53D43EA179A4C2EC633A43913286E1F2424D33920BB24BDA3886E4194E22F7D70DE8BE099017ABF3736DC56EF5445D0E159785C007A470CE0FAA70E706AFF2276B7E923FEC064B75176A0FC5BA15D8440F489EBC0DD167CGF5A93EC2E833C656997281AAC6A27EF794EF023493E058C8547170AE5451F4E51AD07AFAE26A7AGBA060EE43A450D746A98FD64B7E99FD01F396DB2E9F77DA8A897209C2099E088C0A840A66A2B153D9F433332C52AF2F4F6BA6C15CE67D11B45753943A25A603D2C8CF2
	E45CE93155613288827347C3A9059A7AA0C40D7275C5822CEE0D827ACF44690902716E6529E99AF51B35E83BEE01ACE359A8EF5D365FC96C36D3C76CA640BB61A36D477087A83EC301E7EDB50B7A82C98F073C64BB514E26F528CB2DFE0C103567384E2021CB489365BA4BDDCC874FDDDEDD180D1BBD444661000F85D88A308AA0E7F41DE17FE834716D3D7B7A3371C970FF175DECBB535562B43B2423DDD61BC5F44936EEE9684B56DA40781C8366E3F6895F27EC2325B3EBB46D32B4FC372EFEB433C747E82FDD
	D859138216BDEC343FEF2C63EDB80E76093123D99B26F2FC8445C7A8702CED77D3BCC9C7C31EB1244E4068BBBD849F942276918F45070728FD64BF2F200FAC003C03F75799D87B7E57D5B2AE06C1BA86E089C092C05618BAC3A1C04598EC6397FACBEE309F55AB5A17715EF179CF7094FBA41779245DAD196D2DA28CF0D6F30BCDF48BB2CD48E5E257969F3EFBE97FEE05B20E0BF651E5162C8EBBC8B7F70A60CCEEC6B3717286C26392E96ED9EB278885F4BA85147B6065ECF85A4CEE692FCE0BD9923D94ACFF76
	E56293174C8DA0918440FB7098E22F53F44CED06F74F9834556E6B8417G69155CAEFB2E3C8F4F4F4039C5DBD9D935351528E1F675220F966AC89D04FB6A8CD1G4E3150FE77423C83B06A0758CFFFEDC2FF2CDEBBC1A8DCF4CC539F89545FA77B63FDEAFFBCC44B8986FCA4C0BCC092C0D6E81D21B4946758C7D21A091F5C2B6D1F88097D6493A075A07A13FA790C0B77E0D90BE8D9997D48F96F867A2CC1765BD4951E0C2B2CDD6E9608EF57C3BDEDG43C331EDECD70C4CE70F1A5DA2FA6997AEF0B05162EDA9
	C6EB7510B9C83CA45A25EA51A95AAD706216FD446949707AEF1B037B56A50AA763D995B8181B63BD1E8D8C8FB2143D817303E8BE4C6FAEEBCB17A41E91A549EAEF77728F273A38FD06CA37DC5709F42C8F03BAC683AC60F54D3D1A2D7043DA0763E21773AC69E132BF4750F9FB9950A7F3FF883FC256E01B6F47748C1A766617DDB129ECCEE887B25E76BF061F71ABC35D4EB7DF061B4EE1B82E2649788C597F984D3FD01A8A28DF87C0B0C09440B20074F1E80F94CFF39FF23EDE1DB75332344AD97BB51645FCA7
	7DDAE05FDFFFCD5B77F32FE97B7E26EBFD7B7636EBDA3E4D72BE3EC66A6A6836AB7ACA49B5B23E36BA6CF611F6D84EA23035762F4F12FAD86DD62951EAE99725B3564BA22CFC46911F3752B13C945E2B4761D85B3D8D475A6D176F97E23EB26A3456E7A193BE550D9D47FCBDF21CF72CE5BA15DEA7FDD034091DE028AAC717DDE278CF687CA0635D299457DDB00970661F2A6A90DBAF9EF574D4DB5DCE1B3997A6C0F72119D62918BD6AC5D71B4355A9DA72AF18AF190B4D5DD2B1ED1262CA1AFD0C3C97980B0A0E
	98B5378A0597B4CA782AD2A53AB6306CC3858D8525324F41FCFCFC0A265E2C2CDB2AB8458ED473BD325EFF58B1C1565B515962G21D0CC35F8495AAAFE5DCA2B658FD4633D5C56E70ECF7755394AECEB25455CD24DD5258CD47FFDDC7F780D637DECCE4B3CF5A627B98355F83F2CEF23E3DAGFB5FCA2DFD8B99286E8764797FC53A870C07B1A991E075B81C7FE7DCA779B87F4B0B1CAA074D4606CD48134613B1719FA4E04347391C71AC56528CCF167F26E6FEB44FF727EA6547737CD51A7909BC3FCB2F151F4C
	73BDEB3472B343301E7789D9E47E907F51A542B602ACF0BE93FBF1AFA03C2F4AEBB05B3A3CFB0C87697CE9D32C0D98BEA2C5FBBC1F11C256C397397C59A96F8AE42F40E43279A752CCE4BADD56CED8CA12451E9C13DA5C200CC999C1FF923F2A0627890071F6166742D353EDGFFF0821193654138151348G8817E52D88E373922373DCC53AA0C80F1D60250B1E2024AB3D06F41993E1AF82D0BD115185CDD4522D62F48900DF29204BFCC0C997FE9D691A815F730017AE44072EC9C03AC4402FD650ED8EE7F44E
	F0C2B704473ACAB8EEB4457D9E96C163D3F18F7FF916B2CEA7BC60521C47894C126547052BE39B6B74C46E8A40A70723FF156A146B68CA375B51EA25DBCB59D7DE51A967F806CB542D01E71B556516CE426229877D6DF59D51156CBDE5346CB3D3F69F967C775247E8E49037A9D55D346B1FF5D44176778438EC95DF82B5D845EE60E77138F09D5AABF3922EF12E56A8E300516B0BF46F8530CD75A4E6934BA42FED4E9E79CD9A13DD100A3243A6E17CEFB247AD65385DFA4465CCC6DC844755F25C4CB5088B1A0C
	FD602D8252CFE82C148C00BD456EEED8AFE2B81471D5B6264B360B1E0C31458D67C91FDD85694CA96047GAC4E71AA0DFF905AF240C9D3F0DCB9562D9CD7A2G1FG108C40660493D749FCBC70119F5BF44506AAF8FE8DEBB0593F47E5D7483AAEF8F00CD0FEFD1DCEABC6BAECFD1EAEB302355D7E88EF9B063C64542DF88DE339932E22CD06GCDD4845A6451E235CD12A0AFE7AA364D51AD559AF59A4C720C69F16E9A6A118FE5B4CFC5BD0E64A97588194A5AA6E6AA36CD4C351BEB1B7B5BABE9BDEEF245A6E0
	ACED4BF52CC782681879A05623A2DDDD8FE24F6A87511E13AD4715FA5C5C7ACB5665B92E0B894A89663A3C752516CD6338AE5FE92FD059E4B04B922643D081F5D88A72F326218EF50DDA76281D06BA347556E85AE3B01A70787BDE653C90332CB0603C50BC0D7597E91AFADEF8B30D0CB741008F1B067302183AC1B1AF1C061158ECEF0795840E7559FA622B30D610539B7524DC3AD690F0CFBFD5B710F8F554B40CBB8EB790BBAE00748AGA1324E1087F0B092F57B213E5F787B311EBE626F452983514B940971
	10E9836233F13ED27DC07806F03E6983629B457922568C04AF1C7355B42A7922A33D712F78C87FD8980B418F25F3D3E2A40B4127F1FCB045A7AB702C7FECBC0F7DA7F3FA1D41B99D4BFF4D23AEBFE13AEF19C92AB4637B1D9F5F1FD5691C993EFC2CBF9EE9A07E24B197638CA02FED634E2B5736A4E6D888B2ABE660DC6F69CE1775B14DC0BF7EC5BAD98386C3BA82A096E09900FEE61D4188D0BD937D798D3D567A48571F83AD0E3C31D6E60BA9D787E743077D7854B6344C7465E336B92753320D87E803E722ED
	D69523ED42E7E2DBFFF5EDF0FAF67A712975DCB6D32B2D5BED015ADA9E0D6575E5D79E2E5912392E5B2E8FCE57BDFEFCEADDCBE72964D09D3A53D1079333D007450354E1F93FBA8C51546163A654A1046BF03BB0B89D6E7663D36B902F294350C654E145AC6CD3DB691A5A7823BAC3690F307F1CCAA3B1E21C2B68ECAA60BC757BB43207F5382C6DD6BB62D6D3DC2B43C5832F6C3CA0166EABAAC0E69317BB4A90B8CEFC0FC1BBCEFC3FC1BBCE7C00216FB312E906C0F1E4265F31D4544F8940F0BF6138A00EBB2E
	C7DC884745F1DC8447BD3B86F1B19C77B7EA6F84C82F6038A1A6024B1C8DFB0E595C8F4E935C89C8F7F05CDD9417A02F2999EE4BF975D9470D82110D6F9D337D679D2FEFFDB268B21467AEE981E836D03AF9FFDACF5B4E6D965DF4A02EB6CBE6B96FB5A9D75008575719BBC5816305FF3E1AA1539C139CD5FB38FF8BDF6238B8ECB66E73DA8F1339B18652CBG1238FD3F552D6CC3698FB11BEC161F258F795B0879554A7440BE7CD63A368F3F1DCE7A135B5165EA9555FEAAE46471BC6FACF4D292BBC91599CDC6
	8661174C2EF6514F4F33A8BF4B8B447FC1C6203E406C798B9FFB6E65769C21C3FBD6BF04767C4AC06C690174A8000807501EEF97AB6D99AB5BEF097CDC29E1CFD676DD5714E56F63FEF8B92F2FB5179095C85E796BCAF97B39BCCFFADF72F2824ADBA7A8659D6072CA1A7A12179FD05E93BE72BE61723E6B37D6D4C23906BC56363B561035E08FCF6F2669DE8167E931F29D770FE3F60BBC183338584B34FDF76821638808EB22E360F14009A9087B994515GEEBC277BBB9D830BGB707533D22C7F9EFF33ADF2E
	C1F959BA340154C8668B1B395751853A04C5913A5B9B156B1EFA4075G8C176B4D6AB23D1B44215ADC66CEF05FB3CE31D59872B00D6F1910F7A5508B00B382D4GB4BF0CB10014FD248F483152AE9719B6C8F005374B26FD6F2B7365C0AAEB1F34AF156D9387F217BE6CED1324077D5B072D59975371C77F88DB333F47714BA9FE3B824F5672D60AA769EA488BFF84670D903A9E4E1CD3E758BC87F15BAF5073C9C8D7F05CF7AE607CD2BB074FA5BE7B4631000F1C6355B7F6CEA05D1F27FDFD499C356E2FD2FC72
	9C356E2281F54F0B0671A29AF54AAA21F787A01D44F1ADF4BD24FF94767B0FA26E29A6545D48F1A372906764381169080B61389A4E1B7598626EEFC4DC73E398B35853CC7690FC5DCA6281E5E547DDD64BD1F3FBAD75C8B676CD273C24FE110F615877CAA3295F8AC88B0F439E7AF10C17766C44F8E94444B1C26D109A4DBB134B4E9669CA9FE736BE7B38B7E6904F63251B4EE11941109F77B8411DBC270C0190DF7D947AD1B171C11AFF45A71F78F801204C1F6A13CF741D6B437F0ACFFE1A021F55ED7E8E2C5B
	6A7961026DC77B835E874D0A11630872336374B6C11F1D14465AE2B31D1BED10B7A4865B47C56B9B8669A80E7B6DF9EC3325B1683FBFBBCCF4AC1CDBE7B881E0818882881B0B793F22793110CE1C4B4EA448B982B96BE0790E43E4CDC3F809DEC1F3835FC1896BA3CF8905B21D056BB00C16016B1E3E6EAD5D68DD8FDCCF99849C2326F5137324B607DD2AF802586AC9BAA668497B93B84E9D3AC0E8A4D80484BDC16ECD4FA3AEA12C207421972A7BA98F6F0B10E79C709058A63777D2F8A7E7AAFFFC671DF7D8DB
	CD2573C7690FE16F7DE3EC3F029E026B003407636C9EEC53509F233DDE24B129A4C8E7455699768318GBC31BCF6C173C3A19D934B4E0F48198FB917E279978D4AF5AA617371C5B9CD642B6BE9D2516E0F7527E1E584791421EEB7ECEB2D7BC66A76B78430B74C5FA53EEDFA180D15244E3158263DCD3EED3AA15B5B26BFEF3A19B65D7CE41D21D5EED3A6AFBC2FAFF9E98AD984721F744A23E7A6101EA04BE672A6278FC45E89153CA6C8FF4FC75E5F7A2CAF5EF5C2F99DAAF99EBF7D229A87A2EF18CA5ED805FE
	4CE7E677390E5382B6271EB151FB37CF32B9B505631BA8FE16824F665A650DF83FBE8672367F8477732B3249DAE8982443G228192GD681E4BD057B7048B5641ECE1F7131338EE7ADECF2EC0A2DB52B1F715C606ECC4FB560BCB26B11F1C254ED2BD2821DA9EEFFCA7E7EC2FE8AFF59C6712CFE5F4E257B9D488B86188530GE099C0B22FDFB20D5712A81F45AAD5CA123935031E36E26548812897CD96122C1F6D7B122CCBDB9CAE3A41E26B524FDBF1ED199987F686E886106258F988637B4EB97F75EC68B964
	9B8E34E4BD9B864FA8190F79DE7A17B752F7916201B7B10E799E0D27453175875EA7047941C578CE551ADBC49B9E4870BDDE77FAE59EBBB2127374A5F46D6BF73E053C2BCEEDD065E3CCC8AE37B407667B1FDD32F39C6391659AC3BF0F59A0FF9E1E736CA9504AAF07A79B6FE27FDB60E565DD588ACF3B58ED6447B9B5B0F866EF35D84DB6C77B56C2E3E921F14F5E82743B50EE65F94E41B5FB831E67544F1367F47919702F5BE4994FFE291411B5E633AEA5000C31B2EF78BC6F7A6EC32A37A0248B5EBFD62F7B
	48D2D4AE6355CC6C9F1B275CA3D4AFDE1532ADC099CBE55905CFFB4B887AD6D29A33CF2C4E5F07DF66B1E54B534C07BD708CFE1A451698DFC50F3F8FEF3902FC61CFB39F0E01E7024C47FCF82827BF9FD60E6FAB01D7186F75611C79EA9F4E591A437D5477C00F65BF456E3C7866233B4AB27E5A122774D7959923C93E32D11306F9B623E96A4C5570EB3CDF43FCF7F33A5AF709ED0C735137DFB7E8656F1F0F3EBD7EA32FEFB7B45D28EFB35E1F90F2415F3F12BEE5797F3E2D6F7C4C9DFD67176C6CBB7FEF3BC8
	0A4D8B67CC5E7796457B2F9A879BCF3C91E0E568CD37328CB6277CB1BBF05CCF1F239D77DB13239D3B4E4A697B0EF3DECE60BB4E9F66A86FB833B931A81F78588970A98B40B00031G1173F1BE1C1AC39C05FCE163BF979E33341338A3792AC6427203F38357F5F4EE7F5F620466127BCCAD8EC9F2F49279081F18CB5ACBF2B8A912612664929B392C6D9D92A3157153F369FED1EC23E8366669EA45EF1996C05D748BF0CF7FA8DD4FE8FDAF59CA97B8456DDD5662AE49EAABABBBEB15EC62D1BABA32BD375B02F7
	034A9760BE7FF13A6612A0BD9CA0EC815E8D0ABB07EB2EE8402DDA0067F1D967F15FDE788C4AB8C24FE4ADA48DB08AC02B9F856EDB82796CD09EBB5FEA8BF047090E02CC0746A3982B0DF886756AA8C0DC62B338BE1C7AB141ED7EE91D6160CF51BFB2B66D06E70379027FCDB2C0563B9CCE51A575B2AC3D91A97B5DCC42A63757971BF6841281DEF68A547E22650258AA1DB537F33F7DF9A119BBCC20078490829089908B3094A089C07FEC1DA18F20946084C0930093A088E094C0B8C07433D80F444D85FD54C3
	8E72D0E7F719597A0535EBEAAA7AD5BC48CA0FC77BFD160A3ED1910F6DFA291B5076C0BA98A0BC9EFD6358953E9E87DCB217713DAB689B254F210CB914368352C3GC21E9B28EFBCD2CCD3D47E19F4EC634867304C0FB8EE45F3E81B8379017BF4F9FE7FFD7AD8BE3D3788FD71EC2FD3BED7B815CF4F825B5A5CA29FBB3E484F623813B01B3A2D64BCC2BE3F357437F7B0AE54BA6F7C34DFBE7579A7636B21FC658BEFAC26D33F903F0D5CC77B39E521FAAFD6CE714E056A3D5813A6ECFB8F64ADDB08B10A1F8EF8
	2F4478168E12AF777C60761EB368D8D57D3C3A3EF3A83E7EF9F5FD2B4FE3FD1BA0AF66F92CEF5391E5795B93647B3B7233B6A1D01C2533C04957AC53C972F318861FFC3F7AE3724E67D1B6B449F9319F13B911FC5783EBA76BE5FA85778C905A5BD97E1B05017B435B057D7787DD11769C17DE64BF472DAD521E63320B14F31CBC77971171269D46AA93275B42F167B8EEAB15C9F00DD60B54417179D4AF02BFA1128291DFCC756A26B8130C5B27401D13F1870BC81F06AAF7B8DC48FB380846282CB61B7CCDBC25
	315A210BF3FB54D09D2DCE33C26F4FB86E9CEF6B0CFE6FFCE90F819B7B65539E836AA95FD88D9F520248849C83DE2763FCEC023ACF3CC57171896ABE616946BE319472AA96E11F786C4A607A7239C1727DE040FC2CDEA5745EF358A2F5FD8FD2FC64A2F5FD677279AE9A720CAFE0FDCDBE672CC1AFB03B07484F0897820D81BBFD78E2E43A847939C2030F5D970849DECBFD51E6350BD56CCBEF816FF85CC157F62B01B7E7B13F4FC16FDF64C33AFCB16A7C3F44BDE5BD0EAA5E675F52FD85ABA3711FD0C654F9EF
	1D66AA5ECF8D7A2E470D175DFACB4BE0ED6A26EB5F93501EA65E4E7623F4EC053407631EBF0A679043B8AE082E1117C0FA05170E1EE12C06F46692449D6CC55EBC0EEBCFC57F6A603854EE74AF4F92742F24EE2F4D5FD63C7FCB772D377FBD7F04B28EF83CF5BA22F8FF4A6F5B6C2F3F6CE537348C56268E3A1E9E866D99425BF9B9ED6788C8C7F35C299E7411B80EBBCCF973DE2CB3643F0838B7A8DDB924EBB96EB477AF530B98131BE8A1F33CC4362CA48E846F6C0C68B1BA2E05835DACCE7BF7D39A528A989F
	7A99B92E13E3CB5FBD62FB4E94CA7BCCA270A7F199C30F7A1EB38964EA15FCCE727A517E62FC4AF39275B9137125BA83FEFF424A3A2740D71F69349F96825D4117D01F57FBFC755936572B4F973D8309BB2A7569F809458ED238CD03E9DF16GBF64A50C4D5D3653EB3F5B52FD759DCB5BB592E823393E2B3BFD751D344B2B2F3967E66C37524FFEE7D57ADC207AA4831D7EE554C75F6B2B4F02FC2FBE561B325F7E173DF1DBA6FB5FEE2F2D8E19EEA40E391F47B14797F9F9FFDBB1B05E6D544759FB5ADE2F1C27
	FA86A6A7FF1FD7CE50812F1C5FDF9A181C65073C3CAFF68F0CB77D30D7874B27DEB92F8E30AE5F2EA03C6C37562EEFDB8C0E91710610A68985F2845B68305BFA0DCE9269F69BBBC5333B4BA5DA0CD63B119C5998F79ABF7B0BA46C680F1606415DAEE12D0FCECF5EFF60178BFE5E60CE955EBDA9FCC05C730C6822BBB051C2EEF94ABF13D3A3E4D011645796CE8AFAB7FD243925DE1BD8AB64341DA61F603AA551D209FBBB37A464528FF8CAC89C2F6404595DA1117D5821DAC1EFA7BF68A33C4F32EDE6FBFB895B
	689D6AA1BF6313D2C92DC1BFB192C05FEC2AAF11D24C25E44FD93F6323D309D129022EC6D8E3F318ADB566D64961BAA9E4C89DAE515D6130D9FA1C1FB2C169F464A7271729A0EE932FBE29EC71D968ECDC690BBD778EDF3BE2ED0DCE90FADCC28925A43D3118D3D82ABA4CAEA8CAF41D14488F94B9DC5BF95E377EAC6DFC667846A61D10A2D7729F42B5C15F631404F75B4C1DD6DBAF3CD4D38FF0131CF48934F5196DEE723D93E8E6D4E906D93C00621173CA6F1C74077334GAF2BA4FC48CC892CA56D22233324
	322D8DD657DCG589544EF616289CD310A667D1E913927963D5382FED0AD24F61616527FFB687F3D647F9E0A59C3B1FB7602F11304BE7F5479BDD8E76ABC204FFA2A8FE97E629AE8C16925B7660DFB39BCE9A5542EC6C8239FD2C1A3E4F9296A3ABADBC457ED91F7EEF9EF54325BC99BB9092B74903919BDDCA22961C93A55DDF6E9DFD96A3F0DCF55897A13423BEC6B8F4ECB766F603CF4FBFFD2C8052DBF75BB3D6CA4A40E1BC6155579F9A3413E6B6FD6843D06B7AEC92D25246B697F7787ECAA4215C3F97F44
	EB3805997083F910A10379EE2EA94112ADBE51B85F8FCB86FD92E855F36CC6852D01E357ABE83DD87CA374BACDEC0AA4E44AD7B06908A489EB60E124A7F3E512709EC93023BCC82DC5CABA9E81A7FDF26235ACA913AB1A211996376E7F2B991A65F4E94F60F4325CC21DBE713A868192CFA2EB130CEF1E26073D1A264B1A521BB85F3CF62E7E863673A92F7548F924C13662B7301D7F625B4E861976DD796EC441F5657D0C642159816FF844A74A3FD91A287EE6770DDD74BB2865F5067265F8E6F2F897D91886BA
	53BA53E93659A4336DE2255DC24E370E76CA22778C774DDDC43E9BE964CCB93E3C0B68FDC14173FFD0CB878828F70286D69BGGF4D7GGD0CB818294G94G88G88G6BD573AC28F70286D69BGGF4D7GG8CGGGGGGGGGGGGGGGGGE2F5E9ECE4E5F2A0E4E1F4E1D0CB8586GGGG81G81GBAGGG109BGGGG
**end of data**/
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
			ivjJCheckBoxDisplayVars.setText("Display only points measured in Vars & kQ");
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
 * Return the JComboBoxVarDevice property value.
 * @return javax.swing.JComboBox
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JComboBox getJComboBoxVarDevice() {
	if (ivjJComboBoxVarDevice == null) {
		try {
			ivjJComboBoxVarDevice = new javax.swing.JComboBox();
			ivjJComboBoxVarDevice.setName("JComboBoxVarDevice");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJComboBoxVarDevice;
}


/**
 * Return the JComboBoxVarPoint property value.
 * @return javax.swing.JComboBox
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JComboBox getJComboBoxVarPoint() {
	if (ivjJComboBoxVarPoint == null) {
		try {
			ivjJComboBoxVarPoint = new javax.swing.JComboBox();
			ivjJComboBoxVarPoint.setName("JComboBoxVarPoint");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJComboBoxVarPoint;
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
 * Return the JLabelVarDevice property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getJLabelVarDevice() {
	if (ivjJLabelVarDevice == null) {
		try {
			ivjJLabelVarDevice = new javax.swing.JLabel();
			ivjJLabelVarDevice.setName("JLabelVarDevice");
			ivjJLabelVarDevice.setFont(new java.awt.Font("dialog", 0, 14));
			ivjJLabelVarDevice.setText("Var Device:");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJLabelVarDevice;
}


/**
 * Return the JLabelVarPoint property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getJLabelVarPoint() {
	if (ivjJLabelVarPoint == null) {
		try {
			ivjJLabelVarPoint = new javax.swing.JLabel();
			ivjJLabelVarPoint.setName("JLabelVarPoint");
			ivjJLabelVarPoint.setFont(new java.awt.Font("dialog", 0, 14));
			ivjJLabelVarPoint.setText("Var Point:");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJLabelVarPoint;
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

			java.awt.GridBagConstraints constraintsJLabelVarPoint = new java.awt.GridBagConstraints();
			constraintsJLabelVarPoint.gridx = 1; constraintsJLabelVarPoint.gridy = 3;
			constraintsJLabelVarPoint.anchor = java.awt.GridBagConstraints.WEST;
			constraintsJLabelVarPoint.ipadx = 7;
			constraintsJLabelVarPoint.insets = new java.awt.Insets(9, 10, 25, 7);
			getJPanelCurrentVars().add(getJLabelVarPoint(), constraintsJLabelVarPoint);

			java.awt.GridBagConstraints constraintsJLabelVarDevice = new java.awt.GridBagConstraints();
			constraintsJLabelVarDevice.gridx = 1; constraintsJLabelVarDevice.gridy = 2;
			constraintsJLabelVarDevice.anchor = java.awt.GridBagConstraints.WEST;
			constraintsJLabelVarDevice.ipadx = 2;
			constraintsJLabelVarDevice.insets = new java.awt.Insets(7, 10, 8, 1);
			getJPanelCurrentVars().add(getJLabelVarDevice(), constraintsJLabelVarDevice);

			java.awt.GridBagConstraints constraintsJComboBoxVarDevice = new java.awt.GridBagConstraints();
			constraintsJComboBoxVarDevice.gridx = 2; constraintsJComboBoxVarDevice.gridy = 2;
			constraintsJComboBoxVarDevice.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintsJComboBoxVarDevice.anchor = java.awt.GridBagConstraints.WEST;
			constraintsJComboBoxVarDevice.weightx = 1.0;
			constraintsJComboBoxVarDevice.ipadx = 82;
			constraintsJComboBoxVarDevice.insets = new java.awt.Insets(5, 2, 6, 40);
			getJPanelCurrentVars().add(getJComboBoxVarDevice(), constraintsJComboBoxVarDevice);

			java.awt.GridBagConstraints constraintsJComboBoxVarPoint = new java.awt.GridBagConstraints();
			constraintsJComboBoxVarPoint.gridx = 2; constraintsJComboBoxVarPoint.gridy = 3;
			constraintsJComboBoxVarPoint.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintsJComboBoxVarPoint.anchor = java.awt.GridBagConstraints.WEST;
			constraintsJComboBoxVarPoint.weightx = 1.0;
			constraintsJComboBoxVarPoint.ipadx = 82;
			constraintsJComboBoxVarPoint.insets = new java.awt.Insets(7, 2, 23, 40);
			getJPanelCurrentVars().add(getJComboBoxVarPoint(), constraintsJComboBoxVarPoint);

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
	getJCheckBoxDisplayVars().addActionListener(this);
	getJCheckBoxDisplayWatts().addActionListener(this);
	getJComboBoxVarDevice().addActionListener(this);
	getJComboBoxCalcWattsDevice().addActionListener(this);
	getJComboBoxVarPoint().addActionListener(this);
	getJComboBoxCalcWattsPoint().addActionListener(this);
}


private void initDeviceComboBoxes( javax.swing.JComboBox comboBox )
{
   if( comboBox == getJComboBoxVarDevice() )
   {
      getJComboBoxVarDevice().removeAllItems();
      getJComboBoxVarPoint().removeAllItems();
      
      getJComboBoxVarDevice().addItem( 
            LiteYukonPAObject.LITEPAOBJECT_NONE );

      //if not selected, assign the ID set to null (all PAOs)
		currentVarUofMIDset = 
				getJCheckBoxDisplayVars().isSelected()
				? PointUnits.CAP_CONTROL_VAR_UOMIDS
				: null;
   }
   else if( comboBox == getJComboBoxCalcWattsDevice() )
   {
      getJComboBoxCalcWattsDevice().removeAllItems();
      getJComboBoxCalcWattsPoint().removeAllItems();
      
      getJComboBoxCalcWattsDevice().addItem( 
            LiteYukonPAObject.LITEPAOBJECT_NONE );

		//if not selected, assign the ID set to null (all PAOs)
		currentWattUofMIDset = 
				getJCheckBoxDisplayWatts().isSelected()
				? PointUnits.CAP_CONTROL_WATTS_UOMIDS
				: null;
   }


   setDeviceComboBoxes( comboBox );   

}

/**
 * Used to see if the we are looking at all points, if not, we make sure the
 * points uofm ID is in the current uofm ID set
 * 
 */
private boolean isValidVarPoint( LitePoint lPoint )
{
	if( lPoint == null )
		return false;
	else
		return 
			(currentVarUofMIDset == null 
			? true
			: CtiUtilities.isInSet( currentVarUofMIDset, lPoint.getUofmID() ) );
}

/**
 * Used to see if the we are looking at all points, if not, we make sure the
 * points uofm ID is in the current uofm ID set
 * 
 */
private boolean isValidWattPoint( LitePoint lPoint )
{
	if( lPoint == null )
		return false;
	else
		return 
			(currentWattUofMIDset == null 
			? true
			: CtiUtilities.isInSet( currentWattUofMIDset, lPoint.getUofmID() ) );
}


/**
 * Initialize the class.
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void initialize() {
	try {
		// user code begin {1}
		// user code end
		setName("JPanelCCPointVarWatts");
		setLayout(new java.awt.GridBagLayout());
		setSize(352, 287);

		java.awt.GridBagConstraints constraintsJPanelCurrentVars = new java.awt.GridBagConstraints();
		constraintsJPanelCurrentVars.gridx = 1; constraintsJPanelCurrentVars.gridy = 1;
		constraintsJPanelCurrentVars.fill = java.awt.GridBagConstraints.BOTH;
		constraintsJPanelCurrentVars.anchor = java.awt.GridBagConstraints.WEST;
		constraintsJPanelCurrentVars.weightx = 1.0;
		constraintsJPanelCurrentVars.weighty = 1.0;
		constraintsJPanelCurrentVars.ipadx = 4;
		constraintsJPanelCurrentVars.insets = new java.awt.Insets(2, 0, 0, 2);
		add(getJPanelCurrentVars(), constraintsJPanelCurrentVars);

		java.awt.GridBagConstraints constraintsJPanelCurrentWatts = new java.awt.GridBagConstraints();
		constraintsJPanelCurrentWatts.gridx = 1; constraintsJPanelCurrentWatts.gridy = 2;
		constraintsJPanelCurrentWatts.fill = java.awt.GridBagConstraints.BOTH;
		constraintsJPanelCurrentWatts.anchor = java.awt.GridBagConstraints.WEST;
		constraintsJPanelCurrentWatts.weightx = 1.0;
		constraintsJPanelCurrentWatts.weighty = 1.0;
		constraintsJPanelCurrentWatts.ipadx = 4;
		constraintsJPanelCurrentWatts.insets = new java.awt.Insets(1, 0, 0, 2);
		add(getJPanelCurrentWatts(), constraintsJPanelCurrentWatts);
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
public void jCheckBoxDisplayVars_ActionPerformed(java.awt.event.ActionEvent actionEvent) 
{	
   initDeviceComboBoxes( getJComboBoxVarDevice() );

   //fire the deviceComboBox event so the points fill into the pointComboBox
   jComboBoxVarDevice_ActionPerformed( actionEvent );
   
}


/**
 * Comment
 */
public void jCheckBoxDisplayWatts_ActionPerformed(java.awt.event.ActionEvent actionEvent) 
{
   initDeviceComboBoxes( getJComboBoxCalcWattsDevice() );

   //fire the wattComboBox event so the points fill into the pointComboBox
   jComboBoxCalcWattsDevice_ActionPerformed( actionEvent );
}


/**
 * Comment
 */
public void jComboBoxCalcWattsDevice_ActionPerformed(java.awt.event.ActionEvent actionEvent) 
{
   //this.fireInputUpdate();

   int deviceID = ((LiteYukonPAObject)getJComboBoxCalcWattsDevice().getSelectedItem()).getYukonID();
   
   getJComboBoxCalcWattsPoint().removeAllItems();

   //if the (none) object is selected, just return
   getJComboBoxCalcWattsPoint().setEnabled(
         getJComboBoxCalcWattsDevice().getSelectedItem() != LiteYukonPAObject.LITEPAOBJECT_NONE );

   if( getJComboBoxCalcWattsDevice().getSelectedItem() == LiteYukonPAObject.LITEPAOBJECT_NONE )
      return;


	LitePoint[] litePts = PAOFuncs.getLitePointsForPAObject( deviceID );
	Arrays.sort( litePts, LiteComparators.liteStringComparator ); //sort the small list by PointName
   for( int i = 0; i < litePts.length; i++ )
   {
      if( isValidWattPoint( litePts[i] ) 
      	 && (litePts[i].getPointType() == PointTypes.ANALOG_POINT
              || litePts[i].getPointType() == PointTypes.CALCULATED_POINT) )
      {      
         getJComboBoxCalcWattsPoint().addItem( litePts[i] );
      }
   	
   }

	return;
}


/**
 * Comment
 */
public void jComboBoxCalcWattsPoint_ActionPerformed(java.awt.event.ActionEvent actionEvent) 
{

	return;
}


/**
 * Comment
 */
public void jComboBoxVarDevice_ActionPerformed(java.awt.event.ActionEvent actionEvent) 
{
   //this.fireInputUpdate();
   int deviceID = ((LiteYukonPAObject)getJComboBoxVarDevice().getSelectedItem()).getYukonID();
   
   getJComboBoxVarPoint().removeAllItems();

   //if the (none) object is selected, just return
   getJComboBoxVarPoint().setEnabled(
         getJComboBoxVarDevice().getSelectedItem() != LiteYukonPAObject.LITEPAOBJECT_NONE );

   if( getJComboBoxVarDevice().getSelectedItem() == LiteYukonPAObject.LITEPAOBJECT_NONE )
      return;


	LitePoint[] litePts = PAOFuncs.getLitePointsForPAObject( deviceID );
	Arrays.sort( litePts, LiteComparators.liteStringComparator ); //sort the small list by PointName
	for( int i = 0; i < litePts.length; i++ )
	{
		if( isValidVarPoint( litePts[i] ) 
			 && (litePts[i].getPointType() == PointTypes.ANALOG_POINT
				  || litePts[i].getPointType() == PointTypes.CALCULATED_POINT) )
		{      
			getJComboBoxVarPoint().addItem( litePts[i] );
		}
	}
	
	return;
}


/**
 * Comment
 */
public void jComboBoxVarPoint_ActionPerformed(java.awt.event.ActionEvent actionEvent) {
	return;
}


/**
 * main entrypoint - starts the part when it is run as an application
 * @param args java.lang.String[]
 */
public static void main(java.lang.String[] args) {
	try {
		javax.swing.JFrame frame = new javax.swing.JFrame();
		JPanelCCPointVarWatts aJPanelCCPointVarWatts;
		aJPanelCCPointVarWatts = new JPanelCCPointVarWatts();
		frame.setContentPane(aJPanelCCPointVarWatts);
		frame.setSize(aJPanelCCPointVarWatts.getSize());
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
		System.err.println("Exception occurred in main() of javax.swing.JPanel");
		com.cannontech.clientutils.CTILogger.error( exception.getMessage(), exception );;
	}
}


private void setDeviceComboBoxes( final javax.swing.JComboBox comboBox )
{     
   LitePoint litePoint = null;
	
	DefaultDatabaseCache cache = DefaultDatabaseCache.getInstance();
	synchronized(cache)
	{
		java.util.List allPoints = cache.getAllPoints();
		
		//ensures uniqueness and ordering by name
		TreeSet paoSet = new TreeSet( LiteComparators.liteStringComparator );

		boolean validPt = false;
	   for( int i = 0; i < allPoints.size(); i++ )
	   {
	      litePoint = (LitePoint)allPoints.get(i);
			if( comboBox == getJComboBoxVarDevice() )
				validPt = isValidVarPoint( litePoint );
			else if( comboBox == getJComboBoxCalcWattsDevice() )
				validPt = isValidWattPoint( litePoint );


			//use the validPt boolean to see if this point is worthy
			if( validPt
				 && (litePoint.getPointType() == PointTypes.ANALOG_POINT
					  || litePoint.getPointType() == PointTypes.CALCULATED_POINT) )
			{               
				LiteYukonPAObject liteDevice = PAOFuncs.getLiteYukonPAO( litePoint.getPaobjectID() );

		      if( DeviceClasses.isCoreDeviceClass(liteDevice.getPaoClass()) )
		      {
					paoSet.add( liteDevice );
		      }
		      
	
	      }
	   }

		//add the unique ordered elements to the combo box
		Iterator it = paoSet.iterator();
		while( it.hasNext() )
			comboBox.addItem( it.next() );

	}
	
}


/**
 * This method was created in VisualAge.
 * @param val java.lang.Object
 */
public void setInitialValues( 
		NativeIntVector usedVarPtIDs_,
		NativeIntVector usedWattPtIDs_ )
{
      
   usedVARPtIDs = usedVarPtIDs_;
   
   initDeviceComboBoxes( getJComboBoxVarDevice() );
   initDeviceComboBoxes( getJComboBoxCalcWattsDevice() );
}


/**
 * This method was created in VisualAge.
 * @param val java.lang.Object
 */
public void setInitialValues( 
		NativeIntVector usedVarPtIDs_,
		NativeIntVector usedWattPtIDs_,
		int currentVarPtID,
		int currentWattPtID ) 
{
	setInitialValues( usedVarPtIDs_, usedWattPtIDs_ );
      
	//these values will be present if this panel is an editor
	setPointComboBoxes( currentVarPtID, currentWattPtID );

}

public Integer getSelectedVarPointID()
{
   if( getJComboBoxVarPoint().getSelectedItem() != null )
   {
      return( new Integer( 
         ((LitePoint)getJComboBoxVarPoint().getSelectedItem()).getPointID() ) );
   }
   else
      return( new Integer(
         PointTypes.SYS_PID_SYSTEM) );
}

public Integer getSelectedWattPointID()
{
   if( getJComboBoxCalcWattsPoint().getSelectedItem() != null )
   {
      return( new Integer( 
         ((LitePoint)getJComboBoxCalcWattsPoint().getSelectedItem()).getPointID() ) );
   }
   else
      return( new Integer(
         PointTypes.SYS_PID_SYSTEM) );
}

/**********************************************************
 * 
 * Returns a String describing the problem with this Panel 
 * 
 *********************************************************/
public String isInputValid() 
{
   
   try
   {
      if( getJComboBoxVarPoint().getSelectedItem() != null
          && getJComboBoxCalcWattsPoint().getSelectedItem() != null )
      {
         if( ((LitePoint)getJComboBoxVarPoint().getSelectedItem()).getPointID()
             == ((LitePoint)getJComboBoxCalcWattsPoint().getSelectedItem()).getPointID() )
         {
            return ("The VAR point can not be the same point as the WATT Point");
         }
      }
      
      if( getJComboBoxVarPoint().getSelectedItem() == null
          && getJComboBoxVarPoint().isEnabled() )
      {
         return ("A VAR point needs to be selected");
      }

      if( getJComboBoxCalcWattsPoint().getSelectedItem() == null
          && getJComboBoxCalcWattsPoint().isEnabled() )
      {
         return ("A WATT point needs to be selected");
      }
      
   }
   catch( ClassCastException e )
   {
      //strange stuff here, would be the programmers error if this happens
      handleException(e);
      return ("An exception occured:");
   }  

   return null;
}

/**
 * This method was created in VisualAge.
 * @param val java.lang.Object
 */
private void setPointComboBoxes( int currentVarPtID, int currentWattPtID ) 
{	
	LitePoint varPoint = null;
	LitePoint wattPoint = null;

	//find the var point we have assigned to the sub bus
	
	varPoint = PointFuncs.getLitePoint( currentVarPtID );
	wattPoint = PointFuncs.getLitePoint( currentWattPtID );


   if( varPoint == null )
   {
      getJComboBoxVarDevice().setSelectedItem( LiteYukonPAObject.LITEPAOBJECT_NONE );
   }
	else
	{
		getJComboBoxVarDevice().setSelectedItem( PAOFuncs.getLiteYukonPAO(varPoint.getPaobjectID()) );
		getJComboBoxVarPoint().setSelectedItem(varPoint);
	}
      
   if( wattPoint == null )
   {
      getJComboBoxCalcWattsDevice().setSelectedItem( LiteYukonPAObject.LITEPAOBJECT_NONE );
   }
   else
	{
		getJComboBoxCalcWattsDevice().setSelectedItem( PAOFuncs.getLiteYukonPAO(wattPoint.getPaobjectID()) );
		getJComboBoxCalcWattsPoint().setSelectedItem(wattPoint);
	}
	
}
}