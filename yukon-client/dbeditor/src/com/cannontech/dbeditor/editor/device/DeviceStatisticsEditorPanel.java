package com.cannontech.dbeditor.editor.device;

import com.cannontech.database.db.*;
import com.cannontech.database.db.device.*;
import com.cannontech.database.data.device.*;

import com.cannontech.common.gui.util.DataInputPanel;

import com.cannontech.common.util.CtiUtilities;


/**
 * This type was created in VisualAge.
 */
public class DeviceStatisticsEditorPanel extends com.cannontech.common.gui.util.DataInputPanel implements java.awt.event.ItemListener {
	private javax.swing.JCheckBox ivjHourlyStatsCheckBox = null;
	private javax.swing.JCheckBox ivjMonthlyStatsCheckBox = null;
	private javax.swing.JCheckBox ivjTwentyFourHourStatsCheckBox = null;
	private javax.swing.JPanel ivjCommStatisticsPanel = null;
	private com.cannontech.common.gui.util.TitleBorder ivjCommStatisticsPanelTitleBorder = null;
/**
 * Constructor
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
public DeviceStatisticsEditorPanel() {
	super();
	initialize();
}
/**
 * connEtoC1:  (HourlyStatsCheckBox.item.itemStateChanged(java.awt.event.ItemEvent) --> DeviceStatisticsEditorPanel.fireInputUpdate()V)
 * @param arg1 java.awt.event.ItemEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC1(java.awt.event.ItemEvent arg1) {
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
 * connEtoC2:  (MonthlyStatsCheckBox.item.itemStateChanged(java.awt.event.ItemEvent) --> DeviceStatisticsEditorPanel.fireInputUpdate()V)
 * @param arg1 java.awt.event.ItemEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC2(java.awt.event.ItemEvent arg1) {
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
 * connEtoC3:  (TwentyFourHourStatsCheckBox.item.itemStateChanged(java.awt.event.ItemEvent) --> DeviceStatisticsEditorPanel.fireInputUpdate()V)
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
 * 
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private static void getBuilderData() {
/*V1.1
**start of data**
	D0CB838494G88G88GEDFB6CA8GGGGGGGGGGGG8CGGGE2F5E9ECE4E5F2A0E4E1F4E155BC8DD8D457153631DB5B3A29C935252B7D56242621DB7B556E5A2DDFF47325DF5929E95954ECEDBFB721AD31F66326A44B57D8E33734F9A3209099C5C4057A437027D0E1GD103CA107F1F918355A92222B2B08FE6E418B74EBC828A6D6EB9773E736671188141E537657B8E6F5DF36F39675CF34FBD775C1FB7C2FA5652C46376F4C1B0A68A4A5F3BC6C1D8E69304612F0D46932EDAB018844DDFB640
	DE613BD282BC5BA1AF45E6B0258A3FF2A6C15A816905978D2603703ECF90DCF99761A5841FB82BA00C3DF2E8F8F2BEDF3CC0FCF632741C23A9705C8D60845CFC008D82728FBB1A2160D7C0FA37720EE05CAE888B0DD82635A4C303BFEC64651C709C83183B9D4B7C435143CAFE946036GD4835E361D17F543B319C9F7372DC2A937A6E911B07764C6A38AEF5454CF90295046AACF3370F12608F8213E72BDF8D6368F74D775CA0316DE59E5F174D87C22BC6435748FC1C27C4055AB1EBBD74F1ED7E43B6C724B2E
	DEFF0343A5CB3EF63BC7F483FDF7BC4A6DABC9E2F58AC28124E3986E35925455B2F8DF8330BE1EE4FF63D225A2DB776BCF8A55BF7AD9C859CB93384C3604006C4B2A2A629392317EA85697C8379072A381F83BAA5F299A57AE3B47A3F9E43157E99119648F56B05E2E3CACEAD7F3A2356B099348F78E2443GD628F8E7916257C2BA9AE0DBA235F76385EA2F5B7985A121B45638A7C4FBD3F770F6D66C8834F74562AED3A6333D53B5E8BFB2640581AC87D887908B103A53E0BA44ECBE76426F60D953A357C90303
	1227466B2DF5BBFC17A407680677F367A0C741DD96FBA51FC39038DD8F179485110720E067F81BC660ED3BAA109D06EDDFAC04E57D0A59163EED0E3EF546CA250E761D01364D5B09FA3BD50AFA7342BB5221BC9CFF0761A3B5F85E6F314C2EB03D89721A13C84F8B8BC81614479F9356743FEC5C99C4966F375EB2AECF62B2C4A485E461BA5ED307BA0E85FCCA32411487D0895085B016CCBABED7DDBE150EDB405EFC9E3B7B4AD00F576E13EB07DCEE0768D3F43D7168E43A8E861CAEF65AF4DCAF61655ABE4AD8
	7D6320FAD93066E74630E42E0F9549813DF05E05D921F82FCB1EE89B3B55BE5C4F464426E45E0771AABE0B6137E8703CEF1FE6E30B255FB1181CG3CFC94AB9F790E5EC6B6737A5F515B082708ECA48E723AF79914B17465FA742F91100E865886101AE2B015823482F427D09FFF243A648147D13B26FFB9EDD54D81F80AA3324F5E62714BF6CF2F58E177396CBDEE51AFA8E51AEAD0361EF1F42DECFC77820FA651A37A40ABC99E285DBEA802B179F919BF546C45B2BE11655ADC03A2B098748AD46F4D9A187784
	375DAF7F416B304BE220844F97EB50A6BE30F7C00DD08176F58A6A6BD1663F67407B12945255CB8D085B8269949369A53AA68D1E974138C5B7B8D0D7AF0AE1775DA39BF530B1EB067261G253BD51F5FC9B6386D270B054D7F98955486911E5B4D6DE943EE3D8DFAD95D429E03498CD03D0766642DA51D3ABA10BF3D071B025B27BA476C1EBC1F7358D32A67912FF9CF1CE29C6E1CA21F6B3B39895B578D6D9A83D83207F4FE28564CED31566E97497A5AFCE0B822A350833CECC69D4EAD628722C72E973D224781
	AFFE256F336A1283F659A729B6134F7068276A40F07CAA7EC45DDE0E07BA34D6CE7965AC5FAE4BBED74F10ACDE91E55965698F501FE732784739C03F52D6EBB336F5A534B192A0CAEDEBC45DBE0DFD35CA5240105706A3C73153EAB69F47C27924D46A7FC3C59823952969FC168E64FF79043E6FA6832CE77A3E59B0AD9E7153A89B60313108621DF84566B87E8D0637015CE3G4B54F64BC51D3A72536179E0B27DB563211567FF138737092F57073669E76B035BF4C4FDF01B7EF67D64B67B2F7541EC16677DDB
	BD36D19A76E846400F6A519F76CA9E0F488622CAA270D8748D9B364365F149D7DD0EFED13E623AAF02CFCDC5DBFE0A795CC8F80FCEA5BFB91CCBFE525171E9217D0F11C1632995FBFFE12C584B6D34F5EF40CFF219FE5C0076A03A45C1D0D41DB46411B97E274C1FABF8FFBC43AD2E3E0F34854D52104FFD8FC3E1FF1DD36C9D2815C60ADC32B8E841FF08966B1CF6CF3F68A838E37F40EE318F4B9656A1169648EF4037C2F3F1F115F912103A284F6593DBBC5EA1E5EAAAA86CA8AC653633CFF87B60A8136392C4
	6A4E7F87C14A94B9C672BE4C64308DC3CD779AC1AB28197FF3F1CE521C7047966CCBB774719C00303D64A76D0D18CFFED2198C6AA4371B1BA1642526210DFD3EB4FD1C5D70B5EB9CBCF9FEDED9307C4EB44AFFAAE83EBB0D64F83FA995470BF8F7C804B0879D75C5719E45AACDE93A3C8E3BFBA890835DEC5A05ED5378F80EEFEF8EEE3F9D4D685787547A2FB76F90B01661F5727136420EF5FAFD2EC11892F152D2565ED73334EB6FB9A0BF2E53D741B3B20D560D4752315E499605A37B302E6CE2DA23EF825AB8
	00437BF8FD1D7B78D86465B62965A2A09D35AFD439B08B150B03F4FA7A44F22752315C9AF50DDD22623E47F0DF86477BFC82457B17D235FB83E3AF7EAC288FE17C53F97D7353756B203F37E23D4B813FA61D7AFAFD33F6EE2E717B25DE978BC315FEFB24C56BDFB8EEDE4BBB70848B77431A48A10ED05FBF5A0232E21C2A20157E7B52895C1350475D3C8FA874299BE6834BA655C1285C8C66D3273A5F8E17089BBAFC6D7ED2A969EBDDBA79573FB9295DFBD8527502F1D7885DA46D67BAA9589F50CD8C2C45F95D
	B74A28EEA764F3DC140ADB3A1FECEA67EDDCB7855B0B409D16A15905D134DD3633714F0C0C57758D9667E09D9B76537E426BB9B8AED28F98CC85G55GFC2DE3ADCED14AF682EE41819A3B8BC735E3F7AD60E381528F9ACC5C57FD35986DE81A5ECC69270842B45E2FBA4FC5B6A834D7ABFC1757FFCE98795DABC177BCE2DCAF998B8EAA7B828783FACE7F7ACD5306E7EEAA6DB3D928FDCB204C7A03543E0161E06DCB49A03DFCC325B3832EB30368BE13970CEEC1865948551363464F2F1337079ABF4BB2384C2B
	B2746367532D58FF6B813FB90346CF32F52FE67CDC860B85DD0357D3639CEBBC77EFCAFA35B5117C1BC071560FCB2733879017C1EB396FDD42B627E69ACCC5G75GDDGE319A4DB6D45A977B49AC6A6595368AE1B0EDCCBB2A9C6FDE5DAF4ABD4BA43047D08084CC05C3EA1F3E28C4F778B6C39E893B1197A7D0881060F4B546FC73C19C37B91A93FB5183C3FA57E6D45FA7E6BFFBB1EE72CAE4D69EECD20336852690746537171D655017D447C888E4D910BFF986256F3672C9415A8735C13395AF92E6890DFA7
	354353F1087C54F7C651B6516F0D9DA27BA8EC43B9E8A5245781EC86C8B88CFE86209E40F9186C64310B417C73F8BB89651C836BC72E0BFD161969B0FB821DDE07738E0F27636DFEF2B8C929E369E16A47EF4DD0066FCFA9430620B23C1007B2201F09BDCC3AF735A32E600841D4F104747C46C55C93A7DF51AE39BC32C0FEE2F1AB46BD124F556F7290EEA94375CABE36B861EB652DEC7EEB06BA9DEA3DAFDF8A3D165AF8A978DAEA732560EB29B7AECD3EBF3075D228359617EF380C649B816038EDAAEE3E0A7B
	A11B8B17C2FA250AFBA69BF19BA09DFB047A32BF9763437423143E4B52685BF97A17B953D92387832CBBD49E6711167B30BCA65B9BB842721FBF3007695D6F977DEC30E1G21643DAA6F97022CC75F378F0A822D35BEDA1C2C14E910253AB201FC4C1B0568BB027898CDDC72D2AE0D21D3C7A9AEF979BEDB23C0FAAE4092D57F6BC635E3E425223B354AB38A1E319AFDA6E4110D1637053651F3ED41ED343A8D470B9F56EA3D225E8E1B5AB2543C00B742108655222CB9983DEC77419ADDCFFF1551733CD0745DED
	21EC1D6B5CEA516A7C24EAAFCF8FEBF514114575D124BC2B33A65A9B2F6FB7636AABD76B7BDE1E36BE1BC26FD41EE34AF30126DEAE3F279D673D91B56DE369FB2AED08B53BD59DB4F89C0AC362EB90939B333B92346B12C7994E1528456DB9093855CAB95E0EADD968375C76FB52905417EA46F23B4CB407AD83B957GEC8258164567B2AE4FD707F17E6A735987C14DD73CE22F9043E8DF4CA65ACEC83B816683AC8708B4D35C7B73ACDC47608496D86571751D92E365EAF5F8AA5BE0EA4E8E682BABBBD4EC51B6
	4A4E313275E794B798FEDE367E0C62157BF4C631947292F248373D4A66E7A724C7D45CC58B6266C3FA090A9B31108F8CD7F18B4D085B866914DC422531F9AF8352C5AA6E59E162D12962428B09C73B0AFB3110F8F42938577208F6390A7B418522FDCE453DFD01E8A3D55C799308BB1C876B17BC0A534B4D342FE9835C7CBC6A63326E43DAFF044B11F3671AFCAEC72D3D3F15D98C77C9232C4DC89F11C7BE690B2C4EB8C82767C33B73A9363F9FCF6B3F38424F8B73F7EC89FA76DC39FA2331BEDF99BB790158FE
	093A7EFBAB1BF8AE037C28FC446567E85795E8EB9F594E63821EFFF5DCBE7A8B7459017C0F0D4BC7F9336335792B4765230ECA94FA5E3693B728ED36B3CF8B4D0FEC88F92E1EF70C3729D9F95A8E9E94D25E6CD17AE8AB7371F3A0EF61B16A3767F390378A5211AA2EB9CF1D030F114F7F8333756A6390EB824CB9CEF8BB4363DEC914324741712F5E574E17D87EC167C962B1D9994EC3986D546126B397CF35EF2E7863D10CB57AE0ED3F69B86AA6FD8CF5938E6FAB0F135F6931E09999A62F02823CEBF264AA76
	5A7C82AC7FC9CB7DA4BC0CC2E09F9F418DB488390AFCF38A38EFB12A72FC490CFEF5907CB546081CDFB52AC149AF9CE6FED76A356382B41A49D0BD0C722D07F75E06D7190F48ABB418CA81F89D995782B2EFAE1CCA6644C9E46EAE8C484C796D4BC7FEEE400F287CFE2D61F720FAAAFE7113708BAF546B687289BC27F5C2A0FD9F7A4D6E36C14F34CBFE975FC065B28D1E20F8FDE5A1596CD9734C4E6F1BE6C8B7E75AF4FC5E994D46B1972DE9B7474FE578988D1E7B612F28FEF89B64F57D0E56A75157F1CEFD8E
	52D1GDBGD2CE00CF85E8BEC1B15BDB6D2542946BC21B64ED05204E2DD9AA707645E54F6CDE43CE9B793B05DFF9DC98F1FFB764DEDE5789A57ED11E51C987991E374F5F056DDB8EF9EB81E2G920A8C268C0052A2EA5F055F97717661D6E40DAC5BFB1DEC17139A079B0F81B486A45C46CCF9B84EFAA49F8BA8F9FC1317C7B1CABD545F853090A09C6039A2729551B993632237F208EED3910F0B626019DA4C772F7898391D77A0FEE427B2868A0096E13F122EAC560F117FAC44D8A9486910125F72A71C13021D
	5A707CEF66237EA6B9CDD1FC53219EC165F9411283CF0FB8ECD6F7A19B01C1C10E43E5F7CB7DB9C56652A2F35929C23203D7846DFE1DF94D76107BF54D453C7F3B14E7786103CA9DA5955ABA569F792E5198220E058A6D5262C0DC1045649604A4A170475B7509DEC1355743FE325718264F8AF3FF781AB1B7840F75CA5DF916G0F704C2AF82E1F0E61C01D734A35F2A7D8DFB646079A83CADDEECB206DDC46763E006E1794BE286EB9EDC1FF202D37DC01FA3EFDE1FA751C3993201DFB428CCF4D9254F2857F43
	528BAF5348FE4B651626E69B779D9F42EB1A0A1F7C38667DEB6607DD07CF6D17DF18D59E5C6F7C477550EB61586B415742DB2F875F2F093BBE7959F7420D50E75F3BEEE84F3E395F5CB100E3FF895853AA00A800D800049272191F3D01B600B725A67A4B86C73FC89DCA7C473AC23775112E296FD44D6F4233269EC91625C12C1F700FF7E1FF491217A1B96EB3DD28A31F2B5FA973228A7EF3DDAC86917B981A4F0B5B73E9DEB4C35BBACBE8FD726A91B05FA94EF6ECAE59AD56AA6EDF5983346653594E0292DA2F
	0C59595DD1C8C783EC83607B39A5BD7A7D5C54D2036994C0F3A92D1F32F2C93EEE40051552BEEFFFAE2DD15615921FC52CECD21941E48628G886D3B5E476B3DCADF7F4A0D796DD05EDDC67D5B5DF3821E9D76BB93CF69G596E133C22CF3E47316C64D729EB6B20206A7B8BBD79216AGAB31028FE86B39A3764AB6FB3FAACB3604F35CFC10A39CA092E09340B600541386D39EC085C0AB0083E08CE08140B200D5GEB8122GE2CFD2BB567596CE528EE541490C55E7677B30DC0E0FFAB1DECEA827FA76BBF2A6
	298777432F40507374D35BF78F0436777D83D35B7B5181F65E8AF6EA3B67D57621F287585EF0DF1FDFD447D541C02A0AEB71D3044BFDD8D27E4CE25283B324EBE3F499658F364EABAA277B1F31ECFCD4176BE359DFB2FCEB39BE167D301D46C4A7642DA92771E46D515F83B01F02318450F50A464DAFEF539E27FC0A46CD8C0B1917C3BA82A06A14FA57ACF7E6FAA81FA11DFB5AF4DCCF49F96C3E6DA93D7E5298DEB82D575FE2552724C01E77B45938B008F52AF3889F560A1D6D9C4479G6FBA419C6F3A4F2E86
	F00B677927255076DEA9CDED6F8DD2F07FFED11A685F5B25607E3DC3527AF70E3BA121AF6E07F15E29166BD6F157D41C486AC45CD517C3F62A7881A697621BC5E4C8783BCC2EE1066BD4F09FE8F057945C2804E3961A8CCBD1221D433EEF69F3395DBCBD1715F1F9E0882B7A789813516535EB64FED44585647E0497E97D0A6F61FBB7FDEC8EDA8F7D9AF31A76F3926CEC8EB8E3B0251FD16F565A8555765E563C4F334FFEFC75442C72605F5B08BDF47746FC06FFEFD3814F56B35CBFB94E10CF7854B51A33474E
	10CFB8FE1B5DE105F4A440A6D5E73F392D9D23F100CFFDB7606F725E8D35FF5C4D4E1AAB5E556F9F3B993E7DDD7D7E714F33E97F5889F9116F927FBDF901FE4A503CBF7150E7D3D37759B3334A032F8F7E180F63D046488A5794826D55ACE2B6DDDAE1B0D5D650BA7A10BD114A8A9457BCF9D7D0F578E88F79B9AF5004D7D0FF7F20076E72C4D7D0FF9F6A453299E78D26D2006633045F48705D109E81D8F0167AA16C1A5ED7071F8D747D1A33216CE0B1332BF5E775F7941EE27868337ABB8A4537690EC2AC6459
	4E51F85E5F4D76ECA19D29621EBE466ECFC3BA61BC610E9550BEEE41F9123DAFA778BE2E0F61C720DC9840AA357C366C595F475D1BB31B7B385CB62EFDA028F676D677F1E112B52C25C7F2BBE0A1DB469732DC4605BDB31B571F1CA15D4BA169B61C1F6C3B1CCFB21B0DB92F37314FB2FC5CF93D0DE5772A77E0AAA19E2824382C4301787515FA1F554370B115FA1F353917FCD642FB861373BD32412FB07F9A8969E8953765A659E5C29559599337471F71FF0925F15FCC092BEE4F7E99FF6BC373186C0C1F5BDE
	D17744FD433AEE7D3EE1C115FE5F7059E393778D3FFF0C68AA2B783E218D1E4EAACE47778D5FABF818FD43B9D50178115F314B9475F76C982FAA3A0367110265AFD57397B903652F2CA25B78F98FFD87026BE15EAF0BFA47FF877237AC9D48BF5EBB7B5F01D4CF0B474C3E8369F2847F8E444970EBC1C731G9997285DEF744E7EF7A0BB9D33799D8837ED433509367D43EBE45B559738EDF74253FD015B853F93306B9615198B78B000D5G9BG36DD2079FE341B623B0CEA5AD77975B152F3BB6066D5D3590663
	342F72DCB545G7E63D8F69B24D3EBE0EC545079D5995B2B65F46DA25D3B2B2E213A1E12E82DB92262A21DF4663482F07C0E78E28745291B91877ED92865EB5771F749E3BDD42E32164F51414B9DF4D2B9DBAD1F477565388E922F138E5C103F2C16E43CF41DF490D5CBBA98E5E5536B8C26A2007ABAAADBF60B4AF65551D9796DDB131F15BFF6EB723372EF5E1A7C2C7C9737C21D15ABE7EA5DB8EFAE2CFB30B19EDEC7E3A97E784C66693DB32433CE1B0E4F3F2FB2FBDCD327DF6F47B27C5ABA7DFA7F5EF16A67
	7510D7D12F1E670BDA7ECB6A156F0315E784BCB75487F49823527D1D2425939AF819F465D950B0D16F1C6EC527162ED2A9572EBC9D4AF3C4C34F650FF050DD6F79109751C07361BFF9E6F2471D5719A9D11DF1D05F29C6728B55CE423B81372C115A3C6B7A4C7AB8F306F41727CD47ED60EBB7D17E28C63DEDBC4B701B9B753671A15587EC013C4EA6EA6F19DB53654F676E4FF4535E431AA61A4F2F9F1B387770BA065FCADB37EA5E7F7B786C2FE343E61587EF331B4D876BC087512A9E7258F7B7DB1A34773CBE
	E6D56360E642D577A34E86E9270AF3F890A7C3FA1E0A7B06C4BC42D45CE8AB71885C730AEAA59E4B55F21B1DC49B2762BA5A08B6F04F6B6BEDC41B5AC2385FF4A22E9B52F3DB28BF3F6D1859DD6B8D9368AC3AF4D8CBB03AACE9E67C0AA75069792D884A2F51B9B3FE3689F4FAFEEB0372DBFDD3D05FA3B46F9F3DBE7B63E3692C72607BB94FB1BF918DED0FE9A1FF7E08B87E1E555BFD6CBE219562ADAB15B9EF25FBD6FCED33547AB0F72BBA2D7ABB91D77BA75E2DCA462B33CA7E28E7625D2A47BDA81343CAF7
	2BDEE363528B69B1AB2D415EECFD187BD44B2D7A7BD4FBDDEC7F877091AA0F975A9E668E5516893A58FFED6ABBD42557A81E95AED29F99C57D9DF465EE03243FA72D6C79B975771D395E6F5E8F687D7DE3533BF310FEA9C01BE11D9EED0C06B65CA32872ECEC8B602556695579CCFB201E95178375D85B26D74F3CAB815A26EBE6E16A3B947C77327C6EBB50D14BDF9792E5E19F7FF9A0B33F79E3E6D7F67CBEE10FAAC18BBB0A087E207CDC7CCFBE9F3FDDC8E891F670231996A1814FD7DA04C4F67C52A2444B12
	F784FF22E43B96E4A1DD61215C9E62CCE4A1CDC1575BDD9A4E26F1ED7F67A7CE7FFB640FBA7C71420E96A1935D41955147CE1DC487FE8D217C3CC923106C451B6A7855BD0865E70FC43FFC4FAD368A69FD177153E43FACBAEA68BC8B586FEFC5CEA5F8FCDF52EC77BBE5BC03BA5FAAA4F87007D804B41E6D069032049FEE1DE7EDB356F4235659B092C05EFDCCDE2C45225632EF751E7CEC6B26D5714276C6619D37E4F7B45AFBE54957A2A44BCE1F68F7CAEE470877C2B52BA8E918D60B71D05DA1357958D88B8E
	B3D56801114F4DB7EDB6B502DAC7FCC289AB095E4F2216F0C0486B83D6228F02DC1F9DD86629F99FBAAA9F7B172603DD5B852352483B421F041DA3DE99247259BD7EBE49B7889218F592D016DA1165D34F17AE7A62FFDDE79585C8E5A10B2B8CC8CB7AC5E93024262F4F6591558AC0FFD07D9135FAACE35115C99BF9EC3F757BEF77C0FF578B710325256CFF997BFF927F17B1CC9943141D84A5468A137E6973C77C586ADDB2B7925552311BAD0D50D372CB2FBF7F644B1531B1503AC6A111FD14894ACE8D14FAFF
	E830C7747D5572C5C7F6AD0C7E84760597CD026A5D51E79F84FB0697BEEAC790FF0833A5170AFF68830C8C1BB8412A901BBC51BC90EDE219FB587F34E9B79E2952996D81E408AF1963A54D790B1134F0B2C99113292DD096F6630D55A10F2B171DD77B03B6489822C189EA6F7293DDAE98161A35165687DAFAFAA4F81BD46CB8FF7BE752D39A795F9B79B311C9E3BDF5FFB1F6EE7D0B117477A1A425BB2CEB9A34F7D8AB53FF927277AA5AED4ABE10CD7F3D3D414B3E91857C9A9B5DD5B960AD96C25FAD3BB2E8F7
	3BA1E29D2871B8701ED96DBDD98C5C057C0E976B77D399A5D3790DA0AF4AFDC7C373BFD0CB8788FF3C9C28099AGG4CD4GGD0CB818294G94G88G88GEDFB6CA8FF3C9C28099AGG4CD4GG8CGGGGGGGGGGGGGGGGGE2F5E9ECE4E5F2A0E4E1F4E1D0CB8586GGGG81G81GBAGGG439AGGGG
**end of data**/
}
/**
 * Return the JPanel1 property value.
 * @return javax.swing.JPanel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JPanel getCommStatisticsPanel() {
	if (ivjCommStatisticsPanel == null) {
		try {
			ivjCommStatisticsPanel = new javax.swing.JPanel();
			ivjCommStatisticsPanel.setName("CommStatisticsPanel");
			ivjCommStatisticsPanel.setBorder(getCommStatisticsPanelTitleBorder());
			ivjCommStatisticsPanel.setLayout(new java.awt.GridBagLayout());

			java.awt.GridBagConstraints constraintsHourlyStatsCheckBox = new java.awt.GridBagConstraints();
			constraintsHourlyStatsCheckBox.gridx = 0; constraintsHourlyStatsCheckBox.gridy = 0;
			constraintsHourlyStatsCheckBox.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintsHourlyStatsCheckBox.anchor = java.awt.GridBagConstraints.WEST;
			constraintsHourlyStatsCheckBox.insets = new java.awt.Insets(1, 0, 1, 80);
			getCommStatisticsPanel().add(getHourlyStatsCheckBox(), constraintsHourlyStatsCheckBox);

			java.awt.GridBagConstraints constraintsMonthlyStatsCheckBox = new java.awt.GridBagConstraints();
			constraintsMonthlyStatsCheckBox.gridx = 0; constraintsMonthlyStatsCheckBox.gridy = 2;
			constraintsMonthlyStatsCheckBox.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintsMonthlyStatsCheckBox.anchor = java.awt.GridBagConstraints.WEST;
			constraintsMonthlyStatsCheckBox.insets = new java.awt.Insets(1, 0, 1, 80);
			getCommStatisticsPanel().add(getMonthlyStatsCheckBox(), constraintsMonthlyStatsCheckBox);

			java.awt.GridBagConstraints constraintsTwentyFourHourStatsCheckBox = new java.awt.GridBagConstraints();
			constraintsTwentyFourHourStatsCheckBox.gridx = 0; constraintsTwentyFourHourStatsCheckBox.gridy = 1;
			constraintsTwentyFourHourStatsCheckBox.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintsTwentyFourHourStatsCheckBox.anchor = java.awt.GridBagConstraints.WEST;
			constraintsTwentyFourHourStatsCheckBox.insets = new java.awt.Insets(1, 0, 1, 80);
			getCommStatisticsPanel().add(getTwentyFourHourStatsCheckBox(), constraintsTwentyFourHourStatsCheckBox);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjCommStatisticsPanel;
}
/**
 * Return the CommStatisticsPanelTitleBorder property value.
 * @return com.cannontech.common.gui.util.TitleBorder
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private com.cannontech.common.gui.util.TitleBorder getCommStatisticsPanelTitleBorder() {
	com.cannontech.common.gui.util.TitleBorder ivjCommStatisticsPanelTitleBorder = null;
	try {
		/* Create part */
		ivjCommStatisticsPanelTitleBorder = new com.cannontech.common.gui.util.TitleBorder();
		ivjCommStatisticsPanelTitleBorder.setTitleFont(new java.awt.Font("dialog.bold", 1, 14));
		ivjCommStatisticsPanelTitleBorder.setTitle("Communications");
	} catch (java.lang.Throwable ivjExc) {
		handleException(ivjExc);
	};
	return ivjCommStatisticsPanelTitleBorder;
}
/**
 * Return the HourlyStatsCheckBox property value.
 * @return javax.swing.JCheckBox
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JCheckBox getHourlyStatsCheckBox() {
	if (ivjHourlyStatsCheckBox == null) {
		try {
			ivjHourlyStatsCheckBox = new javax.swing.JCheckBox();
			ivjHourlyStatsCheckBox.setName("HourlyStatsCheckBox");
			ivjHourlyStatsCheckBox.setFont(new java.awt.Font("dialog", 0, 14));
			ivjHourlyStatsCheckBox.setText("Record Hourly Stats");
			ivjHourlyStatsCheckBox.setMargin(new java.awt.Insets(2, 2, 2, 2));
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjHourlyStatsCheckBox;
}
/**
 * Return the MonthlyStatsCheckBox property value.
 * @return javax.swing.JCheckBox
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JCheckBox getMonthlyStatsCheckBox() {
	if (ivjMonthlyStatsCheckBox == null) {
		try {
			ivjMonthlyStatsCheckBox = new javax.swing.JCheckBox();
			ivjMonthlyStatsCheckBox.setName("MonthlyStatsCheckBox");
			ivjMonthlyStatsCheckBox.setFont(new java.awt.Font("dialog", 0, 14));
			ivjMonthlyStatsCheckBox.setText("Record Monthly Stats");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjMonthlyStatsCheckBox;
}
/**
 * Return the TwentyFourHourStatsCheckBox property value.
 * @return javax.swing.JCheckBox
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JCheckBox getTwentyFourHourStatsCheckBox() {
	if (ivjTwentyFourHourStatsCheckBox == null) {
		try {
			ivjTwentyFourHourStatsCheckBox = new javax.swing.JCheckBox();
			ivjTwentyFourHourStatsCheckBox.setName("TwentyFourHourStatsCheckBox");
			ivjTwentyFourHourStatsCheckBox.setFont(new java.awt.Font("dialog", 0, 14));
			ivjTwentyFourHourStatsCheckBox.setText("Record Daily Stats");
			ivjTwentyFourHourStatsCheckBox.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjTwentyFourHourStatsCheckBox;
}
/**
 * This method was created in VisualAge.
 * @return java.lang.Object
 * @param val java.lang.Object
 */
public Object getValue(Object val) 
{
	DeviceBase device = (DeviceBase)val;

	StringBuffer buff = new StringBuffer("-----");

	buff.setCharAt( 0, 
		(getHourlyStatsCheckBox().isSelected() 
		 ? com.cannontech.database.db.pao.YukonPAObject.STAT_HOURLY
		 : '-') );

	buff.setCharAt( 1,
		(getTwentyFourHourStatsCheckBox().isSelected()
		 ? com.cannontech.database.db.pao.YukonPAObject.STAT_DAILY
		 : '-') );

	buff.setCharAt( 2,
		(getMonthlyStatsCheckBox().isSelected()
		 ? com.cannontech.database.db.pao.YukonPAObject.STAT_MONTHLY
		 : '-') );


	device.setPAOStatistics( buff.toString() );
	
	return val;
}
/**
 * Called whenever the part throws an exception.
 * @param exception java.lang.Throwable
 */
private void handleException(Throwable exception) {

	/* Uncomment the following lines to print uncaught exceptions to stdout */
	com.cannontech.clientutils.CTILogger.info("--------- UNCAUGHT EXCEPTION ---------");
	exception.printStackTrace(System.out);
}
/**
 * Initializes connections
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void initConnections() throws java.lang.Exception {
	// user code begin {1}
	// user code end
	getHourlyStatsCheckBox().addItemListener(this);
	getMonthlyStatsCheckBox().addItemListener(this);
	getTwentyFourHourStatsCheckBox().addItemListener(this);
}
/**
 * Initialize the class.
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void initialize() {
	try {
		// user code begin {1}
		// user code end
		setName("DeviceStatisticsEditorPanel");
		setLayout(new java.awt.GridBagLayout());
		setSize(400, 322);

		java.awt.GridBagConstraints constraintsCommStatisticsPanel = new java.awt.GridBagConstraints();
		constraintsCommStatisticsPanel.gridx = 0; constraintsCommStatisticsPanel.gridy = 0;
		constraintsCommStatisticsPanel.gridwidth = 3;
		constraintsCommStatisticsPanel.fill = java.awt.GridBagConstraints.BOTH;
		constraintsCommStatisticsPanel.insets = new java.awt.Insets(0, 0, 25, 0);
		add(getCommStatisticsPanel(), constraintsCommStatisticsPanel);
		initConnections();
	} catch (java.lang.Throwable ivjExc) {
		handleException(ivjExc);
	}
	// user code begin {2}
	// user code end
}
/**
 * Method to handle events for the ItemListener interface.
 * @param e java.awt.event.ItemEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
public void itemStateChanged(java.awt.event.ItemEvent e) {
	// user code begin {1}
	// user code end
	if (e.getSource() == getHourlyStatsCheckBox()) 
		connEtoC1(e);
	if (e.getSource() == getMonthlyStatsCheckBox()) 
		connEtoC2(e);
	if (e.getSource() == getTwentyFourHourStatsCheckBox()) 
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
		DeviceStatisticsEditorPanel aDeviceStatisticsEditorPanel;
		aDeviceStatisticsEditorPanel = new DeviceStatisticsEditorPanel();
		frame.getContentPane().add("Center", aDeviceStatisticsEditorPanel);
		frame.setSize(aDeviceStatisticsEditorPanel.getSize());
		frame.setVisible(true);
		frame.setIconImage(java.awt.Toolkit.getDefaultToolkit().getImage("dbEditorIcon.gif"));
	} catch (Throwable exception) {
		System.err.println("Exception occurred in main() of com.cannontech.common.gui.util.DataInputPanel");
		exception.printStackTrace(System.out);
	}
}
/**
 * This method was created in VisualAge.
 * @param val java.lang.Object
 */
public void setValue(Object val) 
{
	DeviceBase device = (DeviceBase)val;

	getHourlyStatsCheckBox().setSelected(
		device.getPAOStatistics().indexOf(com.cannontech.database.db.pao.YukonPAObject.STAT_HOURLY) >= 0 );

	getTwentyFourHourStatsCheckBox().setSelected(
		device.getPAOStatistics().indexOf(com.cannontech.database.db.pao.YukonPAObject.STAT_DAILY) >= 0 );

	getMonthlyStatsCheckBox().setSelected(
		device.getPAOStatistics().indexOf(com.cannontech.database.db.pao.YukonPAObject.STAT_MONTHLY) >= 0 );

}
}
