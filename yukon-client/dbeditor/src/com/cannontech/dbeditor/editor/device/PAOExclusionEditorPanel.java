package com.cannontech.dbeditor.editor.device;

import java.util.Collections;
import java.util.Vector;

import com.cannontech.common.util.CtiUtilities;
import com.cannontech.database.cache.DefaultDatabaseCache;
import com.cannontech.database.data.lite.LiteComparators;
import com.cannontech.database.data.lite.LiteYukonPAObject;
import com.cannontech.database.data.pao.PAOGroups;
import com.cannontech.database.data.pao.YukonPAObject;
import com.cannontech.database.db.pao.PAODefines;
import com.cannontech.database.db.pao.PAOExclusion;
import com.cannontech.database.data.pao.DeviceClasses;

/**
 * This type was created in VisualAge.
 */
public class PAOExclusionEditorPanel extends com.cannontech.common.gui.util.DataInputPanel 
implements com.cannontech.common.gui.util.AddRemovePanelListener, java.awt.event.ActionListener 
{
	private javax.swing.JComboBox ivjJComboBoxFunction = null;
	private javax.swing.JComboBox ivjJComboBoxRequeue = null;
	private javax.swing.JLabel ivjJLabelFunction = null;
	private javax.swing.JLabel ivjJLabelRequeue = null;
	private com.cannontech.common.gui.util.AddRemovePanel ivjAddRemovePanelPAOExcl = null;


/*
  * Constructor
 WARNING: THIS METHOD WILL BE REGENERATED.
  */
public PAOExclusionEditorPanel() {
   super();
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
	if (e.getSource() == getJComboBoxFunction()) 
		connEtoC1(e);
	if (e.getSource() == getJComboBoxRequeue()) 
		connEtoC2(e);
	// user code begin {2}
	// user code end
}

/**
 * This method was created by Cannon Technologies Inc.
 * @param newEvent java.util.EventObject
 */
public void addButtonAction_actionPerformed(java.util.EventObject newEvent) {
	// user code begin {1}
	// user code end
	if (newEvent.getSource() == getAddRemovePanelPAOExcl()) 
		connEtoC4(newEvent);
	// user code begin {2}
   
	// user code end
}

/**
 * connEtoC1:  (JComboBoxFunction.action.actionPerformed(java.awt.event.ActionEvent) --> PAOExclusionEditorPanel.fireInputUpdate()V)
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
 * connEtoC2:  (JComboBoxRequeue.action.actionPerformed(java.awt.event.ActionEvent) --> PAOExclusionEditorPanel.fireInputUpdate()V)
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
 * connEtoC4:  (AddRemovePanel.addRemovePanel.addButtonAction_actionPerformed(java.util.EventObject) --> RepeaterSetupEditorPanel.fireInputUpdate()V)
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
 * connEtoC5:  (AddRemovePanelPAOExcl.addRemovePanel.removeButtonAction_actionPerformed(java.util.EventObject) --> PAOExclusionEditorPanel.fireInputUpdate()V)
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
 * Return the AddRemovePanel property value.
 * @return com.cannontech.common.gui.util.AddRemovePanel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private com.cannontech.common.gui.util.AddRemovePanel getAddRemovePanelPAOExcl() {
	if (ivjAddRemovePanelPAOExcl == null) {
		try {
			ivjAddRemovePanelPAOExcl = new com.cannontech.common.gui.util.AddRemovePanel();
			ivjAddRemovePanelPAOExcl.setName("AddRemovePanelPAOExcl");
			// user code begin {1}
         ivjAddRemovePanelPAOExcl.setMode(com.cannontech.common.gui.util.AddRemovePanel.TRANSFER_MODE);
         ivjAddRemovePanelPAOExcl.leftListLabelSetText( "Available Items" );
         ivjAddRemovePanelPAOExcl.rightListLabelSetText( "Exclusion Items" );

			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjAddRemovePanelPAOExcl;
}

/**
 * 
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private static void getBuilderData() {
/*V1.1
**start of data**
	D0CB838494G88G88G84C735AEGGGGGGGGGGGG8CGGGE2F5E9ECE4E5F2A0E4E1F4E155DB8DF4D4551D37AEF53132DBBC62B6DB25C5245DD8294B36313215BD076E41ADAED871ACDA0E666C894AC7C441D829A2A461E6A6A113B0797EA0B1D18386C2B0A601709183D8C292A098B58BA986E5CBDEB2AF4910493C694C1BE430C8777F3FDF73B2F91390D44E591C734FFB77773F777EBF6E7FFE3FA179EF4FC8C93264931214C2785F21A4C2BA0E93325C774F9F8A4C9613E0A5063F6DC0B9E4DA
	E5B2BC0B00B7E9FB22B51BAC2A33C13A8A52475ECC34EE0377BC72BF0536237092C6CE4C91C29E3823B6E6ECB9AD29C2CEAACD3FF6408E4FCC20DA40BA00E6GE5132C03C59C8FC0BA133FA3A5D98869DC0EF9822EA2833E02672B825EB12036E7B14F1E03251CBF833039C0B640CB1FE5F97743B30DEA776D06FA1E6F426D11E47DFF9E31E420ACC37D02267132F379330CAC200E3012180B6F4373C87BC05F87BD5AE0F50F6E2CF6F4D77BD45D6F296E73BB2B3D1ADFD79B9A4E289ED551D56FD9E434B81C3A66
	EDD75C2A8B4A6EFE8EF57E014BC66BA3A48EE4C4D12CF0907D640277C9C0F7BFA774FEA2630850EBA012445CDB93D66F8D4FB1FDCBD784750E0ADD1932F88556FF76201D173BG69E9C04C264AB3CDA3ECD25CEE4D2D2BBD7DE01ACAF52F668F07BA646C81735A9BBFE98974387CBE2766B6DA47EC3B6B2030ADFB2530EDF99F4ABE8569AE200044A37B911F8A69C82079AB054DC7AB044D1DFFF9089C3BE7E1F2D6981B172DE43612D5C11BF7BF33B22518465E93B60C1FA3406B825201A2006682AD83AA21B1BF
	A5F58FBC9B3B75A6EDF0D0F3B7FABC47DD8E6FC71AC3F541FBC383F0B8F6C66D513C8EC2D8DC5F6B2AB251C7D05C2A11B10D44EC7B1408B81CFD6681D25B7EE8F20A09EDD1CFFEE0C9DE4D6AA8DA9D34AD309A7D76AC0D15FAF847F2E88743B7D0FC1E81E7EDDFCAF1CC4783EFD92C70F3E7265025ADF9A6495E2BD8D2CDF4097877F3162867198E0B1F8F6A42FCFCFFBA7AF8836079EB922CD5C04DC08E20C9EB040F1FCA2D9B4F472D90F3DE3762BA6B6F76A8DE7D385F69F228DE6E6B73874772359931F2FD93
	AE477C92D9E7EC23681FF7197AA5BA3245B2E38D9FAB56847D42E44F88ABFB591A513111A95B700F34CF3C38063521D56297A99EEF40D95B2E26FD8B537697922C9EA0163F5409785297D87EB4D9CFA545638CB82B6781DABF266DAFC2BDC02CFDDB6DB83EAE05F49C10EDED02358C68985085A04FDA51465BEDB557580F5A8D6D4B4ABEEDAB04279A503DCA2B5B27AB6E9E751C62F5AA5DAE55C7F81E67EC68576E9165E2E97F6E8199A7D5376AD5F4981120F6E5D005E07231BCAF5BF2B00FD7255C8E67208A82
	86BDC4547B3AAD8F1EAE45277F516300D1BB180371135250A7C34AB92891AA007779EB51DF3F24E378D4F81F3DD6786ADB9908A52F0379E71D704B1534DCF8FE88412D3A9A9A1A1DBD2806623DACE2B4F6F0BFBCEBA1FFAC500C17922C75F18956A5C011AFA7D8EF7A9D4C192FA4D85783CDDB1FE02D7DBD7000BADE95FDF8DD270855660C1F122AA7AF1A46AA0D3BD7D95C453D9A9A2B09D4072257C0B6D0A7D0GA882E8AE50B2208DC0C59B008F548994G0A58A066789D03CBC2E421BD786C603250260974C3
	A434B99C8F7DC06744754C9F783EA4C4867AEF2C7A512F68DFFC0FDD974C0BED909CC34E0F324D4A1FA5A4B8E6CC54363CB1F85B7833G28183FEF8D4913B1CE7D9873D97CBD45002772A70B5717B2314F4F05F6DC8A34FE03085907524A886D4B47951FAAFA6FA7DE68F82AA39841ACEFD4BA4AD107D4375E8C6B9E3783DEFC3C6FBC1C0E3E617D3AD7A3224F7D0A62B84EB7C14773C97C5769B98C07BA0C230460BFCE790A2EFB1D5D30520275156EF477854B7F166A629BB10578382DAFE7212D7657932C65C0
	7BDF9736F6ED4536907D33CD53867C1E8E9CF9F8BFFF082EE7CEC17E2E57C53CFF5E0FEB5CC9B22D52F4103F362F5324CDB07FC463440C180CAD7DE6B22CDFC37DC199BB1C59BC3ED3F99C15C8DE8D65C502CDCB00B6G15ED9476C70D50AD05173916FED2F24D3ABDB6C206506FDA6C3FD6BD7248D9FB78587E4CEE9E5BDDF673586E350F9D3BEE3BD96CB21E5F0E36E943EEC3DF78420E73CA0F66F62B34C34AA25C8F6BBA508E275B29FF6AF474297AD96797AA683591E37A89BAF71D02778B9B45BCB27C0E18
	C74A3F380F4C3F58E0B1DB17C67C683CA5EAA30B63059B0373885369168C6CE32ACB9D84C7B5E9FE374E5758F4DE64384FCA317E37FE02E5F7B7BA06F03EF7504D52F13F2EEB6EAA05DAD44D9E6D2A37D7738E2A0E5D1714A125DA99562BE90BD4B7D2F68B3ED7163D775E87E56176DDD54AE899979BC3AAEF8BDF7B614AF31535AC3672086D587FAB3D77893D97E6FF076B6DB8238EEAC32A90E1CCD6FB1DFD7DFA1B53872D675695A7845CC55895FAB4B7D487A505B1FE5D692A26B2BE693E8471B70E9D3DCE2F
	5A6A76787992EBB768D5A774EA325FF6ADFA51F76682E661C5D3AFFFF54576F33D629A9E25FA09323EEBF45CC71A5F27DE9C447F6DDE55673BCE4D3C66E2AF1E99C3CE1B29A00C023DF5C4787B976D0B3E2ADDE720A7ABB742B0A9E88C4BC4FB9DBC769FDF552E1600D33F81D6F1B161EDBAA05AAABE7B76095884499B51C30231347713E99372790D6B44CC3F15E9D3EF107F421C09193A0F4FA3476F38F13E1B00FA22BFC5748FD37DCE43FE3EDBBB2E85CE785DD460754EF29350E19F5761552BA1BA1CD17F6C
	D77D6A8DD081C6CC36EFA84F41B5CDAC2CF5620172B709FD03AD9379E25F40B7C7CD1A4B4516E3408BEC423556E1F73EE17DD435091D01E7CAFE1FA9FFBEBC99BF56143FD4721D1A99BFD67257187263A5FF40E3464F0E977C1F794C786512DF271B716BA5BF4A149F34BF46145FA64BABFEB3FE1764BFEB4A57A53FE7480CBFB9DE34636FB3F19F634340022596EE7CBED4AF733323784C5C905EB945658F1EC9A5E7613E5CE558B3B27C35AC73F5F0C2966E9387E47DC959DB881EAD31BA593AFD0E02F5FA3C4E
	C158E263A6185F057C662071AE64EE348F6C0806E7DC3CB84B5F5202F9428D14F393309E37D35C1758A0DD86541C406A0AC860E719B45F21CBA2DFF2A24BD71AE8160F8C08FC9E60CF860AB44D37C0660B85BE59BCBADF59E69A6F72EE24C6E21BA9768AB82BA4C51C51FE14ED3C4F196ABB13E236DFC1F2EC667136B9746C7AFCAE569B013AEC96717017EC63F9C0234F2775B86951A1EF5BBDB9463D8C43EAF25261892308CFEF05CDE2C04447019C54954F96B94C5B787EF7711AAD742C14B515B8EEE99A2683
	D7075624B954D390E2CDF22BF74E296238487C706589637D503C2C13162DE17CB00F5B9FED70437C27D72670BBA93770FBA9C934B26CEDCDE09D921BA131A9925B6E915882094D14D83C441E7789ECFF1240AE6982F3C9EC1BDFE033A496A131E809958F89AC5FA2306FCB2C8DB0666FFFEC40B50B599DD78FEDGFAF907B3E7C3C33322ABF4AAB05EE9559F3271361B84F5323BABDB833DEB0174D220970158B97A776879BA66CD36A6DA0FG317E7BC803317F6A0063F9D194BC97823179AE51F98A1EDF574C4F
	ECDFD0194BE2B3E4069371B3B36F9857E97183B349113F6E36185D35CD4E5AE70981BD6938111CA86327FDD1FBCA47172B789DE357A5E1FB87641912AC6C7A75251651F6DD5FEA0B087B0A4A81A1EB81480987E2637D3FDEB10E777BD3181FAF2488DD268E6CC3DDA63C0090F257CA399E28B3B2C548DDE0B5139B83CFFE6F374FB8DE4D0FB9E1FA3E0234BE057BF9CBE248F8F5B49F63309C70FAA0B6464C4949B10CD7E740C70A3B8FE6A2B1867DA9976D06B907273BF3D1AE1DF308B8877E22GF5EF03BA7B01
	EE820AG1AABE5BC1FBB6E7DDDCBE00C7B3BFB5D933907DB86F2D939F2EDA265DE1465FEB421F2A4D5145B6519C839FCD9EE50B711F23BE539F593AAF7C41653740914EB1765CA7D93A967106526CF28DCC016FBEBE8A2652648F2BF1CD03999325C51C3216522D29365197BC2433B54155E09DE21E3F9B47031BFEE1578F7A89EEF40599810C9F1CC175A203F58241F1C2172D758C64AA4E9A3532C5C7D234AD507244BC34A31317DA3651A6735113BCBBE2FA5B4083972C81A1857BA4A51B69D52D300E68131B1
	E2C9A12E5F66C1FA99D09C10BDBD51DA8B548664C997E3C5FD1E595AE664D891EEE1933C6FE1FE693DF4FD7E54C7158B756754F433F2B58357A76F6828F22172A2534D5A2F4FD5A25A8FE71EC035EF58696EEBE8107BDD5ECEF1D7C4BB4DCF9731764AF56A1AB46A3B15D0DDB704684AF458E4155F75EC95B111DDC46F91A05D39D5347F1DF9F8B7A96623F64D695609180B0E6763FEC773BA7B1CEE011D24D80F6625179B8C3B39A07C3D4B2D856677AE5FADB03FF73933E06CBB456985616EE578BDBFDD977703
	FD130098F6D893D8044466C9EC36440EFA84B6DFE213FC82DBAA3194DDE031923BC5E271922B738BAC5BAE30A80915CB6C4010406AA5768B0935C92C032EEB3BA02DCBEC381E6E09A0BD47AEE26D6686DC9FAC057486207C8C6877C06D9902BF1572F10F6882EC8A50E43E47E37C7C86333BCAA42C8B6B9469257651E3341095EE7CE7B25E8BABC35C8D0A34599DA146CE689D3B115E24659EAD4C2231647329BEBA0861EE0173D66B854464BE6EE3E5D0A5623CF429B30D67E951352683D2F74B6131663B59A636
	3379696ACE9C2B67E73079A9C962FFCB7145861C4DDB1F150BEFG17816FC8266833CF97066FEF4B8B4D7B5B6AC26C67BE4D6F6DD1C37B543A42A2498B0E6E2D3ABA086E60E7A4343C2EF87B54D1FDB21116E73CF065D337056B374C5753AF99FD3DDF623D83C6FC2FEC034F2F0E5586B3B2C737812BEF0F4BD85FBED99FC99E2B3EA8137A18DD85C5349DE53A1826AF93B1E7280D1952B7ADEE879FE059B94993F42C6A3190B25DA230B3F4DC3A55EABCCF79AF0AB5A593721E44BA68D85584C286A4F6979D2BDA
	81DBA431D2BAAE9D86ACA7C9E053A976F310D1A9731D26635207A063C119EF9145EAGDBAB31B6BAAE15G763FD26747A8369D3007AD424F8307F01EF0A917B5BF58DB1A055FB05FFC483827DE88BE0D83CA4EE23EE57E7A17E16C573DDEE59042632CC76DA19BE8593F2F97E59D108EGC5G4D85DA16A5760A7755E31F9E39F462E748327DD3AFE09D275B14EE55A50E4885EF4B018C4963C757325CAC87466F2853F549FFFADF36114F0B333D663F6DB46EB50FE5333D26A3DB6CC59DF6B3FEA0DB0C71450745
	98BFA5DB0C6113F692993BFFE7F86FAA8FB72EFE9D44E4DC7EC6E5B01B3DD7054D51528F5B1285D615A34E0A76F661BAD72E4970B4202161245769B82E74355168E36B21C8BAF7E25F3C12A3564ECF9D4271B58A520B01D664083DFFE017B8231B7D6319246D26AE53EF38E3BACEDA62F3781EBFB73877BF720CB8234BFDDF483CG7CE9399847475EB736B5464EA3963626E3FC6F88BE469C4ED7C17E2C91FC543755EA64BFB5020FBEFA1717E736AD5AA9EC0BDFB407389E7CB86C7769515CA612471E7DC31EA4
	4F34D5A94C6F2F7496915335B06DBD4C7E75BBEDBCE66A7344F95FDDF601F9G0B4EC31D6B1A1A470887ABA7713DD0A4141987748ECFAF498B4E1B2C3EDB2F0ED5DF0A21AE2472FCD8F765337A90EF03774FC754D71DBCDEFD29067ABAC36AF3996AE37D672723763D6391DBA71C2E47F88D64072E9F4ED2FCD2FE687AA17A10D8BFCC855E7AFC318FD8D40CED57D9G6BC820A9C0330096G459408B5468F0AEA49B87B428E4D53860B9017E1194E6C5B767E75FD477D2BD4911BD13F7C8919FBCBE65833717585
	ACA6738BF90C7ABDC978E4767D29846DEB86DE9750A4203B0166GADAC94762DAA2AE27661B1FE232EABBD7D7406C098070776C198A7A2E65B6BBB0D36C5F379F17C193CED74BA01FFDFEEB716AB62792A787318C93937897B6E54C644DF5C9F4C308EA8CB48BF6CA0520F4B6D420F25775FCB1AFF1BE9F9A70C9FA3385C58ED4171ABA2E9F54A3549A60449A665A40C3E0CDFED181F5A6B036F678D6F4F9E7A66672DCD5F288C96D7CB0B432F4D1FA9B6DF1BAFAFB65F8B2FAE9E7B9B44170A43FF0378EA31719B
	C456AF9269BA242C887692C0DDC0BA5064A251A7A63D01C104DF7E0F6E8FAD0EBE5C9B60577E3A105FD7925ED6F749783F9198AE41FB38EECD5735C12CDF60DF16E0FB691A07027C0C2184FDC4BF83E2D9B9BE3984634D2576D2182D9332F7A1B68B6C0AAA92EB076F525F4FAD0474B2092D2F936B099592FB084ECF7B0B932D4D4582AB22D8A7247BA57625DD14550B453A61714F0DF786595FBFE89163F1E82CACA8E67DEDC9F1681D41BD95B857A43F11E8AD83EA863AG64811A8CF4B7509C2085C051C06B01
	ECA50956D220DA20E3C09DC07DA522CD1F5901EB7CF34A25513716G720592EDD82FE2BC7B5932ABFC5BE66C9A3FED8BE99B2850A69D17BDFC8FD8320B1EA775767AD499C3E53B32A5566AEB57FCCEDCD633B5F545AE3CFF6F555C7A5C921C070F52BA2F407B649231667FF19D6651C199B656BC0E6672F9FB6B7E71569546F5C03DE15E46B60B8799B87FEC06BFA66BB776117A2C20B1E0ABCD3466178AFDEA6CA375795D1E20BE536C636913BA06BE4DA023006B43E26507383463FE7C592E1D615A99FA6FE9D8
	22319B398E25CF16F930A2FC9B4F2B983F0D9F29B06F3F0BAAC6775FA595667D77298AE37F65B1DB016D52873159A97345C86C3C44D652BA917B5469507BA57E82558B71D3AA8A94784BD42FE10AF50AF152001D6758268A0CD3B039DF13E71CE4B7EDFB274B453FA5590DF91CEE88DB690F345D2823532398744E14986A4DE2E2A5AE2D783E71EF6AF04CF52B43E572B2738484586E9D8E2762527AF6D41555D6156D5DD7A96676994346B126BE3DAD6C3DE4FBA99BDB9C7C39741FBCC9CC7EA7F86CA1666EC3C1
	DD6C76EB550515251F207339E589BD073C9BE44DAD95E34F1E3D6F060BC9G5B3D1AC7756A1799CA3F5461BEFB329E7515739EE3F1DEF4FDBEE15F4903CE4EAF68279EE7A1233B0F7DA63A25229E9AEA76DA12627312ACE9C7ABED2818213A866BF377DAADA43915ECE1015BCA12B17ADAC98A8D4ED6E205408DF88C9D9183A45977E7F215B60005303FAB4815284D086AA4CD1C8FB468A4071F85149DD77B15A12766EDG4DB2C6F87811FB8E2CDA7C5F67FCD63225159463GF3D675D27BD4871ED9729F331DA0
	E99EBCBF425F86005ABE7AC8716917DDEA9B496F25F2FC3A6AE8941E73692400FE37DA0337CAB52794DF3F0EBD78F09BC9F6634F6EC8AEE33B94F7DF8DF363E1EAFBD263B1EA8FEEA088681B973C5E173564BD1C35F37B6995F32D44F202243BB445F1C2690126ECA5E9FA3FD77575EBAEC7407387D611ED98DD3415BA4DCBCA2479E8ECF5870CB3D269014074A9993199A7405181AF2921B9F1BC2D16B99CCD7D0A97C4295ED69DFFC628F94BA56FDBEF693B9EBB396D0205A4F1A33109EC810FCE3674AA03CE57
	E5F8E92671CC9B4F8E0D85E31C5B071F9400E6E5A11A891694D0FD472335F75E3771B3AAA0D8D4A7EFB3D7C2511ABED59B2CE96C6DF53AD5D98178952AFFD3D60FF92AC3726486EEAFB87D6466EE0803E6E29D2C2D257F77527F7B707FDE0A6C25485EFD605CB8B266DFA8BFA0EC2641A3FB88B6FF7589E8C17D69350F4EFC66C85C0BE05D8912C2BFBB00C6488E667A58BF582DFAEF19FD671BDB27455C06ED6441D089E03D5981599F367046BE0C7D0722DBC6C595226923438361CC59C8184A21D973687F4289
	1B0C250AE859B72426E5C1CDD117F41EB7150D3115FFB50E319B3E7DDE58B1F661F6B636C6EF8FDD475D36878DAA5A11E85D3FC32E45AA717CB65C9CFDF6D0F139F445B55068F660FCFD7C322E86577AD377E07DBE1107B3996FFBFBD06FCB06B27F87D0CB8788DA691CFC9996GGFCC2GGD0CB818294G94G88G88G84C735AEDA691CFC9996GGFCC2GG8CGGGGGGGGGGGGGGGGGE2F5E9ECE4E5F2A0E4E1F4E1D0CB8586GGGG81G81GBAGGGD396GGGG
**end of data**/
}

/**
 * Return the JComboBoxFunction property value.
 * @return javax.swing.JComboBox
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JComboBox getJComboBoxFunction() {
	if (ivjJComboBoxFunction == null) {
		try {
			ivjJComboBoxFunction = new javax.swing.JComboBox();
			ivjJComboBoxFunction.setName("JComboBoxFunction");
			// user code begin {1}
			
			ivjJComboBoxFunction.addItem( CtiUtilities.STRING_NONE );
			
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJComboBoxFunction;
}

/**
 * Return the JComboBoxRequeue property value.
 * @return javax.swing.JComboBox
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JComboBox getJComboBoxRequeue() {
	if (ivjJComboBoxRequeue == null) {
		try {
			ivjJComboBoxRequeue = new javax.swing.JComboBox();
			ivjJComboBoxRequeue.setName("JComboBoxRequeue");
			// user code begin {1}
			
			//must be added in this order as the index of this combo box
			// corresponds to the C++ IDs
			ivjJComboBoxRequeue.addItem( PAODefines.EXCL_REQ_OPTIMAL );
			ivjJComboBoxRequeue.addItem( PAODefines.EXCL_REQ_MAINTAIN );
			ivjJComboBoxRequeue.addItem( PAODefines.EXCL_REQ_PRIORITY );

			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJComboBoxRequeue;
}

/**
 * Return the JLabelFunction property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getJLabelFunction() {
	if (ivjJLabelFunction == null) {
		try {
			ivjJLabelFunction = new javax.swing.JLabel();
			ivjJLabelFunction.setName("JLabelFunction");
			ivjJLabelFunction.setFont(new java.awt.Font("dialog", 0, 12));
			ivjJLabelFunction.setText("Function:");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJLabelFunction;
}

/**
 * Return the JLabelRequeue property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getJLabelRequeue() {
	if (ivjJLabelRequeue == null) {
		try {
			ivjJLabelRequeue = new javax.swing.JLabel();
			ivjJLabelRequeue.setName("JLabelRequeue");
			ivjJLabelRequeue.setFont(new java.awt.Font("dialog", 0, 12));
			ivjJLabelRequeue.setText("Requeue Behavior:");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJLabelRequeue;
}

/**
 * This method was created in VisualAge.
 * @return java.lang.Object
 * @param val java.lang.Object
 */
public Object getValue(Object val) 
{
   YukonPAObject pao = (YukonPAObject)val;


   //Build up an assigned PAOExclusion Vector
	pao.getPAOExclusionVector().removeAllElements();

   for( int i = 0; i < getAddRemovePanelPAOExcl().rightListGetModel().getSize(); i++ )
   {
		PAOExclusion paoExcl = new PAOExclusion(
				pao.getPAObjectID(),
				new Integer( ((LiteYukonPAObject)getAddRemovePanelPAOExcl().rightListGetModel().getElementAt(i)).getYukonID() ),
				new Integer(getJComboBoxRequeue().getSelectedIndex()) );
		
		/*for each new entry in this transmitter's exclusion list, there needs to be
		a corresponding entry in that added transmitter's own exclusion list.
		It is simple: just make a second entry for the database with the current paoID and
		current excluded deviceID switched.
		*/	
		PAOExclusion correspondingPaoExcl = new PAOExclusion(
				new Integer( ((LiteYukonPAObject)getAddRemovePanelPAOExcl().rightListGetModel().getElementAt(i)).getYukonID() ),
				pao.getPAObjectID(),
				new Integer(getJComboBoxRequeue().getSelectedIndex()) );
      
      pao.getPAOExclusionVector().add( paoExcl );
	  pao.getPAOExclusionVector().add( correspondingPaoExcl );
   }

   
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
	getAddRemovePanelPAOExcl().addAddRemovePanelListener(this);
	getJComboBoxFunction().addActionListener(this);
	getJComboBoxRequeue().addActionListener(this);
}

/**
 * Initialize the class.
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void initialize() {
	try {
		// user code begin {1}
		// user code end
		setName("RepeaterSetupEditorPanel");
		setLayout(new java.awt.GridBagLayout());
		setSize(420, 354);

		java.awt.GridBagConstraints constraintsAddRemovePanelPAOExcl = new java.awt.GridBagConstraints();
		constraintsAddRemovePanelPAOExcl.gridx = 1; constraintsAddRemovePanelPAOExcl.gridy = 1;
		constraintsAddRemovePanelPAOExcl.gridwidth = 2;
		constraintsAddRemovePanelPAOExcl.fill = java.awt.GridBagConstraints.BOTH;
		constraintsAddRemovePanelPAOExcl.anchor = java.awt.GridBagConstraints.WEST;
		constraintsAddRemovePanelPAOExcl.weightx = 1.0;
		constraintsAddRemovePanelPAOExcl.weighty = 1.0;
		constraintsAddRemovePanelPAOExcl.insets = new java.awt.Insets(4, 3, 3, 7);
		add(getAddRemovePanelPAOExcl(), constraintsAddRemovePanelPAOExcl);

		java.awt.GridBagConstraints constraintsJLabelFunction = new java.awt.GridBagConstraints();
		constraintsJLabelFunction.gridx = 1; constraintsJLabelFunction.gridy = 2;
		constraintsJLabelFunction.anchor = java.awt.GridBagConstraints.WEST;
		constraintsJLabelFunction.ipadx = 5;
		constraintsJLabelFunction.ipady = -1;
		constraintsJLabelFunction.insets = new java.awt.Insets(7, 11, 6, 59);
		add(getJLabelFunction(), constraintsJLabelFunction);

		java.awt.GridBagConstraints constraintsJLabelRequeue = new java.awt.GridBagConstraints();
		constraintsJLabelRequeue.gridx = 1; constraintsJLabelRequeue.gridy = 3;
		constraintsJLabelRequeue.anchor = java.awt.GridBagConstraints.WEST;
		constraintsJLabelRequeue.ipadx = 10;
		constraintsJLabelRequeue.ipady = -1;
		constraintsJLabelRequeue.insets = new java.awt.Insets(7, 10, 12, 0);
		add(getJLabelRequeue(), constraintsJLabelRequeue);

		java.awt.GridBagConstraints constraintsJComboBoxFunction = new java.awt.GridBagConstraints();
		constraintsJComboBoxFunction.gridx = 2; constraintsJComboBoxFunction.gridy = 2;
		constraintsJComboBoxFunction.fill = java.awt.GridBagConstraints.HORIZONTAL;
		constraintsJComboBoxFunction.anchor = java.awt.GridBagConstraints.WEST;
		constraintsJComboBoxFunction.weightx = 1.0;
		constraintsJComboBoxFunction.ipadx = 45;
		constraintsJComboBoxFunction.insets = new java.awt.Insets(3, 0, 2, 124);
		add(getJComboBoxFunction(), constraintsJComboBoxFunction);

		java.awt.GridBagConstraints constraintsJComboBoxRequeue = new java.awt.GridBagConstraints();
		constraintsJComboBoxRequeue.gridx = 2; constraintsJComboBoxRequeue.gridy = 3;
		constraintsJComboBoxRequeue.fill = java.awt.GridBagConstraints.HORIZONTAL;
		constraintsJComboBoxRequeue.anchor = java.awt.GridBagConstraints.WEST;
		constraintsJComboBoxRequeue.weightx = 1.0;
		constraintsJComboBoxRequeue.ipadx = 45;
		constraintsJComboBoxRequeue.insets = new java.awt.Insets(3, 0, 8, 124);
		add(getJComboBoxRequeue(), constraintsJComboBoxRequeue);
		initConnections();
	} catch (java.lang.Throwable ivjExc) {
		handleException(ivjExc);
	}
	// user code begin {2}
	
	
	//not used for now so just hide them
	getJLabelFunction().setVisible( false );
	getJComboBoxFunction().setVisible( false );


	// user code end
}

/**
 * Method to handle events for the AddRemovePanelListener interface.
 * @param newEvent java.util.EventObject
 */
public void leftListListSelection_valueChanged(java.util.EventObject newEvent) {
	// user code begin {1}
	// user code end
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
		PAOExclusionEditorPanel aPAOExclusionEditorPanel;
		aPAOExclusionEditorPanel = new PAOExclusionEditorPanel();
		frame.setContentPane(aPAOExclusionEditorPanel);
		frame.setSize(aPAOExclusionEditorPanel.getSize());
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
 * This method was created by Cannon Technologies Inc.
 * @param newEvent java.util.EventObject
 */
public void removeButtonAction_actionPerformed(java.util.EventObject newEvent) {
	// user code begin {1}
	// user code end
	if (newEvent.getSource() == getAddRemovePanelPAOExcl()) 
		connEtoC5(newEvent);
	// user code begin {2}
	// user code end
}

/**
 * Method to handle events for the AddRemovePanelListener interface.
 * @param newEvent java.util.EventObject
 */
public void rightListListSelection_valueChanged(java.util.EventObject newEvent) {
	// user code begin {1}
	// user code end
	// user code begin {2}
	// user code end
}

/**
 * This method was created by Cannon Technologies Inc.
 * @param newEvent java.util.EventObject
 */
public void rightListMouse_mouseClicked(java.util.EventObject newEvent) {
	// user code begin {1}
	// user code end
	// user code begin {2}
	// user code end
}

/**
 * This method was created by Cannon Technologies Inc.
 * @param newEvent java.util.EventObject
 */
public void rightListMouse_mouseEntered(java.util.EventObject newEvent) {
	// user code begin {1}
	// user code end
	// user code begin {2}
	// user code end
}

/**
 * This method was created by Cannon Technologies Inc.
 * @param newEvent java.util.EventObject
 */
public void rightListMouse_mouseExited(java.util.EventObject newEvent) {
	// user code begin {1}
	// user code end
	// user code begin {2}
	// user code end
}

/**
 * This method was created by Cannon Technologies Inc.
 * @param newEvent java.util.EventObject
 */
public void rightListMouse_mousePressed(java.util.EventObject newEvent) {
	// user code begin {1}
	// user code end
	// user code begin {2}
	// user code end
}

/**
 * This method was created by Cannon Technologies Inc.
 * @param newEvent java.util.EventObject
 */
public void rightListMouse_mouseReleased(java.util.EventObject newEvent) {
	// user code begin {1}
	// user code end
	// user code begin {2}
	// user code end
}

/**
 * This method was created by Cannon Technologies Inc.
 * @param newEvent java.util.EventObject
 */
public void rightListMouseMotion_mouseDragged(java.util.EventObject newEvent) {
	// user code begin {1}
	// user code end
	// user code begin {2}
	// user code end
}

/**
 * This method was created in VisualAge.
 * @param val java.lang.Object
 */
public void setValue(Object val) 
{
   YukonPAObject pao = (YukonPAObject)val;

   Vector currExcluded = pao.getPAOExclusionVector();
   Vector assignedPAOs = new Vector();
   Vector availablePAOs = new Vector();
   
   DefaultDatabaseCache cache = DefaultDatabaseCache.getInstance();
   synchronized(cache)
   {
      java.util.List paos = cache.getAllYukonPAObjects();
      Collections.sort( paos, LiteComparators.liteStringComparator );

      LiteYukonPAObject litePAO = null;
      int paoCat = PAOGroups.getCategory( pao.getPAOCategory() );
      

      for(int i = 0; i < currExcluded.size(); i++)
      {
         for(int j = 0; j < paos.size(); j++)
         {
            litePAO = (LiteYukonPAObject)paos.get(j);
            if( litePAO.getYukonID() == ((PAOExclusion)currExcluded.get(i)).getExcludedPaoID().intValue() )
            {
               assignedPAOs.addElement( litePAO );
               break;
            }
         }
      }


      for(int i = 0; i < paos.size(); i++)
      {
         litePAO = (LiteYukonPAObject)paos.get(i);
         
         //be sure we have a pao that is similar to ourself by category 
         //  AND that it is not our self!
         if( litePAO.getCategory() == PAOGroups.getCategory(pao.getPAOCategory())
         	 && litePAO.getYukonID() != pao.getPAObjectID().intValue()
         	 && litePAO.getPaoClass() != PAOGroups.getPAOClass(PAOGroups.STRING_CAT_DEVICE, DeviceClasses.STRING_CLASS_GROUP))
         {
            if( !assignedPAOs.contains(litePAO) )
               availablePAOs.addElement( litePAO );
         }
      }
   }

   getAddRemovePanelPAOExcl().rightListSetListData( assignedPAOs );
	getAddRemovePanelPAOExcl().leftListSetListData( availablePAOs );


	//only allow them to add if we have something to add!   
	getAddRemovePanelPAOExcl().setEnabled( assignedPAOs.size() > 0 );

}
}