package com.cannontech.dbeditor.wizard.route;

import java.awt.Dimension;
import java.awt.Frame;
import java.util.Vector;

import com.cannontech.database.data.route.CCURoute;
import com.cannontech.database.db.DBPersistent;
import com.cannontech.database.db.route.RepeaterRoute;
import com.cannontech.dbeditor.editor.regenerate.RegenerateRoute;
import com.cannontech.dbeditor.editor.regenerate.RoleConflictDialog;
import com.cannontech.dbeditor.editor.regenerate.RouteRole;
import com.cannontech.yukon.IDatabaseCache;

/**
 * This type was created in VisualAge.
 */
public class RepeaterSelectPanel extends com.cannontech.common.gui.util.DataInputPanel implements com.cannontech.common.gui.util.AddRemovePanelListener {
   private javax.swing.JLabel ivjRepeaterLabel = null;
   private java.awt.Frame owner = com.cannontech.common.util.CtiUtilities.getParentFrame(this);
   private com.cannontech.common.gui.util.AddRemovePanel ivjRepeatersAddRemovePanel = null;
   private int rightListItemIndex = getRepeatersAddRemovePanel().rightListGetSelectedIndex();
   private boolean rightListDragging = false;
   
public RepeaterSelectPanel() {
   super();
   initialize();
}
/**
 * Method to handle events for the AddRemovePanelListener interface.
 * @param newEvent java.util.EventObject
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
public void addButtonAction_actionPerformed(java.util.EventObject newEvent) {
   // user code begin {1}
   // user code end
   if (newEvent.getSource() == getRepeatersAddRemovePanel()) 
      connEtoC1(newEvent);
   // user code begin {2}
   // user code end
}
/**
 * connEtoC1:  (RepeatersAddRemovePanel.addRemovePanel.addButtonAction_actionPerformed(java.util.EventObject) --> RepeaterSelectPanel.fireInputUpdate()V)
 * @param arg1 java.util.EventObject
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC1(java.util.EventObject arg1) {
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
 * connEtoC2:  (RepeatersAddRemovePanel.addRemovePanel.removeButtonAction_actionPerformed(java.util.EventObject) --> RepeaterSelectPanel.fireInputUpdate()V)
 * @param arg1 java.util.EventObject
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC2(java.util.EventObject arg1) {
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
 * connEtoC3:  (RepeatersAddRemovePanel.addRemovePanel.rightListMouse_mousePressed(java.util.EventObject) --> RepeaterSelectPanel.repeatersAddRemovePanel_RightListMouse_mousePressed(Ljava.util.EventObject;)V)
 * @param arg1 java.util.EventObject
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC3(java.util.EventObject arg1) {
   try {
      // user code begin {1}
      // user code end
      this.repeatersAddRemovePanel_RightListMouse_mousePressed(arg1);
      // user code begin {2}
      // user code end
   } catch (java.lang.Throwable ivjExc) {
      // user code begin {3}
      // user code end
      handleException(ivjExc);
   }
}
/**
 * connEtoC4:  (RepeatersAddRemovePanel.addRemovePanel.rightListMouse_mouseReleased(java.util.EventObject) --> RepeaterSelectPanel.repeatersAddRemovePanel_RightListMouse_mouseReleased(Ljava.util.EventObject;)V)
 * @param arg1 java.util.EventObject
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC4(java.util.EventObject arg1) {
   try {
      // user code begin {1}
      // user code end
      this.repeatersAddRemovePanel_RightListMouse_mouseReleased(arg1);
      // user code begin {2}
      // user code end
   } catch (java.lang.Throwable ivjExc) {
      // user code begin {3}
      // user code end
      handleException(ivjExc);
   }
}
/**
 * connEtoC5:  (RepeatersAddRemovePanel.addRemovePanel.rightListMouse_mouseExited(java.util.EventObject) --> RepeaterSelectPanel.repeatersAddRemovePanel_RightListMouse_mouseExited(Ljava.util.EventObject;)V)
 * @param arg1 java.util.EventObject
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC5(java.util.EventObject arg1) {
   try {
      // user code begin {1}
      // user code end
      this.repeatersAddRemovePanel_RightListMouse_mouseExited(arg1);
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
   D0CB838494G88G88G43CB5BACGGGGGGGGGGGG8CGGGE2F5E9ECE4E5F2A0E4E1F4E13DBB8DD4D467156E06A4F657A466447418546E5A443626210D1ECDDB5BF5DB5B10131F5A13E4CFF26AC9EDCBCE5C681E9A579ACD0C0B49474C20A0207CAAA0BF030C88918790D38D228242C492E2D824B87E1C4A03F94013B77326EF9EB22669E66F7D3E773DF98CEFC0CC32F33C3E775D6F6F5E7B5DFB3F7B5D77C1724A6627A45B7288C9CEA17A6FE8B2A10B1A88F928B271B447252EEFF790532F9CA0
   1BC80790D9GF50987BD0EAC62BF1E8A659AA8DF2E72B87640FBAE7969DE57B1F809B1CF52D1C29E19555EB679BCEA961FE7BBADBFB614814F1DGF5006B81188F10C5FEBDD4206343D05E293FA3A45B88697A833689379618702B74F6AE28BB0630815B3CBED42C574F855CA2000DG299BD8DBA7BC53A8F537B69E535BDD3EFB8E5978F43AAD9367B20D4FE1065EF7819F035CC385E1A7BF68BD824F262E112113830A5FBD20C9EEDF3FBBA4EA23C177502864D615D1CDECEC6C96032220096AB9D1968734AEA1
   A04AF92FA12D7F541ACA47A1E4A50CBD1F62D67722FC3C709E8418799A2777394CA61D1EB9A3F3C86203EFA54722F755EB0C4E540D91FAB3FA5E36AF590863178DE7687D4EC0F986G63A5356F5DF13C88010092504401E177187409207AA6E186D50452BEB44C79C95A48797966F11C6F4826F6C7A700EF93474BCD087F944AF3G62B7F1BECF9C60FCFA7F7AA0595C6F366D0A41E742A6465F5ACD91BEDD7BE7598B291E3D150D3AD275BA4C8B5087908730GA081E0AF556F9FE53D834F16FE6D346277AB0116
   E030D576299FA9BED1067746C62851F15D6200227A88E1BA7CE0EB0D85BD9CD63EBEDEFF9198EF67895739786E7B092639ECA9963CBD7AC226ED3DBE06630D88EFBDEF205CEEB90FF2F341FBB2650190063F1762671A70EC3DDF257A0465A7214E37194B593B135352693813E455575936DB50B277677BED64CDC6C3411B91DA180CD764220C5BG5F879086188D30886059B7390C6D598DD349388374CC8D887239517E2020EA2D23126C93D5DD567B07A713359530FE8D536E47643230413CC6AB7F2556D2AEAB
   976659566A72F0EC0948054DAD471C5B35E522EE6CB45630105A4431ADEC8D6D863E02624F1870ECED9F263605E5AF54AD85E06D93E9FB67FF33766946B86BA83E5904E76328940F65BE28CBGE06BFBAD9FFDE9D5D23B238D60B20086B087E091C0C292DF63335935B7E8C7DD2675E5FD37676C06279856D421A3905204400058AB281250AF0BA12237494AC139760F6B171F033AB7GF33CAF86C4D550A4A5G238BFE9114A9445A1466E4E39BD52435BD12DF04897CC142472D4CC13B1005107627208F1CDA24
   852BF76720CEDC93FAE1C498G5E7336223CFECC7D76AA601FEC6532FAF0B7623CD08EEE65F2F9A8A7871E9F02F20BF2E3E31BB400E4886AF52E23DFB96F02E79C34CF82187D966CD3EF37BB66819CA19E4733G7313BD8E4D6651EDF61D176BE6DB66FD2466853725EE52FA3B076A59E57D99514D2F31B9813FGA081E095004361F138G5AGFCGF1G2C7DEB3D33AD46C73A4CE52455DCC61AE359997206FCA25F9C073C7377242D56B602F299EF63C5672DE84B0EAA6F0AAA6746208B817754B45D264CF371
   7714987D52CD6FCC9F328B502696009C1781A482B01DD81563E43652AA04C4EE9D9F2B205822AF22A12C6DCB39C83BF8CD8CE8ED306D86FC709252F5F3CDEEDA44EE8695C32757D1BC7A51532058A1833FB1B71B61E18C3395727AA4DAAFE81AAA7543C6FFCE54B4A9B094696F48C3DAC263DCF4C867B53790F95D8CBCE625F89C4E944E6B9DF939A67D6FD4141151E08FDA36EEC73750F86188346FCC617AD6560471E250A8D753F224FE71F16FA46BE785B85674760E2727BD07FD9AED599C2FB4C1C0CD38BFCF
   5171E9C67DC6DABF9BE8DF821088103A4D63285B466590EE724668BF9D3AA72771EEBAC7747869DF509CCCC7E6644756715B732DF57C0EFCEB9D3FA7FFF29D7EDA3E158E333A877211C7E5ACE03209EF67237F9ED08281119A2651053018F9DD8F72A185A46D3C649B9235F352A7A21C973661F96AB93AC7F861DD5E467D77D885775F2ECF6EA54B2E24D946FFF33FD9E5CB5846F4F64536087FE6B4AD5C0D368641379F84F5DA998DE88C7FBD3A7F6878101D626CBDD731EF15C54CDE03D4CB028C94D7D5BB8F9D
   BA69BCB720AA320C1554C46BEB609CC337702ACEBC9004C6836E2B42B5419D9A83193AB5EC6326ADE92363553D5507192E64125367E752F97F5FE62E915421FABAD6F724223EC69B16C2558D0C26A26566E5F1F87A949D61329873FC9ED95C444C1632B84CE471AE2369D8772CF132883578FC5D22DF39A6B22A04F1C5B794DBC7B58D82BBEA85D7847A689255C1C5750B3EAACA5028A6496EF6B4630F7B2F82FF0C9DAB1E87A5D56C8884C775202B8AE82EE3F44D976F1A8EDDAAFD7F12C92B575771450E4726C5
   1BB4B42CF5CAA1D83C5110F8450F7FF729E2A8B4FD22D46B992FF4CFB2C527659C4CC899BF79450FFFDEFE3A01C861CBE64818A3B6C78D045B5A12341FFDDE1E5A43127665F2244F901B9F4CA931384AD3C463AA5883B236C3FC3E1D47D56F6C41FA9ED7694163E9709BEC1B4238E1BB6EC14F1F4FB36DABCD0043FC5B2770E475F1DEF37D8A3DFE2751FF11572AFFDEAA6F5FF7E1727E334655D72532FA4D681FF9512AFE26D1FF27E57DBC23FE57A52B7AC5C67DEC4B7A04D4AE47BD85D9988F08FF9995615809
   4E72C371BABFBB9664C455758A72E864CCDAD900F110EC0AE9993E3050FAFFAEAB44B8F644983F22F09B4133A59B1345930B849CB328CAFEB882E0102E67BD6F9FB667BDD7G7D18A34DCA831DGE0FD9FBA0C3136D5F6EEED9A4A227A844F0BB6C13987A01C4646D91E46E28C56EE03516E588E562EEB07D53B72E65EEE015EEE295EAEA35D5C6EBF0DF643692C5D0CF4566E59F16D963770F6B5992CDDDB86EBB7BB0335DB13016D5698B953DA8377ECAF627E8B841CE967F91C0F324C79DDDF285CB2562273E5
   32711BB3BDD1792D3F6E45F17BG9F4C64BA7375C2731926A594D286A41ADE50573F33489C07B15C7F946D00A7ECAEA12D83825CB0572173C5C8AB669FF4342E87DD6FDB654C5AFD1226287C48F8FA0C3A0D53C6F05AAB09E3FA9EB65D4BE5941749E34F3FF94C7963253BF65B52E348E329AE0715A6F9746D1FE5E734FD96F65FGEDD1E469F42DB668AA5E4975766103A7EE14579E65B444B2E7406422075747FF6C829F3F8746E738360B9CB74B40BDEE60928C1C6712113BB6F0CF9838A33BB83D5B2FE0EE4F
   AACF0E99FF74670D0DED02A650B042146BF7C8297A7A4AB0964B015749B4C7G6525G4F8330FCDC77098C3D6DDA4039324086299FF0B7187D4065ACF6EE1E834F05GEC8F7C5109F6F8FEA1911C210F8955B9CCFF2302A3EEEB8F64B66BB4AD7F5E1D246933B7122DF276B3F7A55B16E569361C95512DB51D336CC9G0C773DA7B96FC732616C144D79DAF36A4CC43EEEBA82E4F3FD504C67DAG732CB466DA5C5CE2B15734A3BAC3FE1BCEFE844FAFB40EA3BCF71B5342F9484B81FD4861BCDCEFE93DB19E2608
   62B88FDB864FCE130729C3B74E430B868FF1C07FD203071656669B63E152280D512F3C53B9CD7A278A5474EFBA074D7B4C32447C187B4C469C26732939517B4C1592748DAE409F4B657B4C3F97E51B7619EE18D2888CC114E164CD76220CA01E504BF1FBF1DE9ACF901E3F49725E48B7104E5CC8FE0C51B32F946326BE40FF8AB087A09EE039C1DBFA71145FF45A4313FC53D9FBC3F42DB551457ABDF9E1BA5FF4C89E4F15F5DC1CCE3FBC235F53536AD7E5747B7052F47AB5997D16CF2BDF1751AFCD0A6E674B0B
   48AE1CB7B17F493E87AD207B4FAC28C7FDCDB1700FD07CFC931E59699FE5EE270FC2DDC1BE1F5FFBA2FA7ECF73474FB9B72A4C7A8DCE68670EAAAF0D6A47EC3957B1759EBC5E40599EBC584077736773799E3C2191F94A588D7BACC093G33018DE5980F76C0B988B093E0814013G2BGD277F0DB38D8EC953F0C3705D841CBA4874F64D1FE726664D8BB21DF349C2B76D8756BEA3E39797EBC21DFE9D43959F23E02161B1B2FF2C23F68793C1673A55E64FC6B261CAF98B59F5319436528DFB321EE2E219FBFAC
   46EFCA5C0FF7A9D2C0A35C077BCA708C28400EA685B8CE223881C52509E8761D64G0D1B9742184B0CF19714464E1BBFDCEA1DB7DFDCEA1DB77FE169645F06FEDA9AAB2F4E6833DF60742DGE0385FD15CFAA8E794F0DC5FC50EF39A38178C5C918357FF09634E9838178D5CE5836724B1F5904AF186EE6BD5CA8B141795F07F749D19FE2F05B2A964386FD31C2B101DB5992EC91E6EF71F29004F99512DBFFD61F3706FB378DEC24BCF6D5EC55793C24890F5BEF8A0516BD6EB7944621B46D941AF921EC73B6BC4
   1A5E26DDD3CE579B340FB4CC36BF3595465AE78669BD85EFA15BCF128D3CC271BE931E6DB31B9B799D25A054BDDF48F5BE29AC364E5B1D56BA1F66C47B8BA9236A00982D57D94E8223AE6215BBB4510F625073B9343FA628C362843BF0527E2CAED67FEAE7AC5BE132761EB44B7A30013F3D590C0F64B8BFEEB1630F9878BD2DE67C3B860DC74BD10EE123FC1C162F933E176EBE0F7D7C120F3A261031B6E73C74AECA11A76AAE0A6B823D0BE24233F144161D06AC5A83BE5D51317C4C6B54971C371BF3B6E36C8C
   6EA0244240ED26385BE1407D866EBA357BDB81F72F015BC2F1E3007B3D417FD1897D26ACDCD7C6E15E04A2ECF7D66267E5AF1403GF381620B583919513DF88C73A0032A6007E5B9979487487C3D58B7EF04774D0032932099208FE046DEFEBEE97D98F503675CBA05FEFA970E55B50BFBCCF5D1678DBDBF3ADAB667A7576CE5F4E594F3DF76778D5C17B90B390F120F93439EC7CD6FEFB5FE51FEE5623E38E75A5F4527BF47D9AF7D7EEF707B4E85AC4795735C48B5097B063900FB36182FE3FD9F46EBB4A62487
   B6E1CCEBECFCDF15FC2D42D0A7558A96837E046E9958BF35044700EB69184DD0768284CBF84EACDC49F3E6715F3A13F44E28353CA31A58335BB63304793E78124859AEEB7FACBB1B33F1044F39866A1D25387643A36635C71DD91D4C62B6D6FF7F38FA74A16837A3754B4755A33D7FE1B3573FB52EBE45541F71760B7D1C3724DF4CA37277CBE25EFF9DAEE5BC4D521F33BB0B887937A5B6A67757868B4CF28F33AF130D0D9FD0F3E07C2F8B246A6B1BGE33415A1FD77B5347179CBA76E9B913F42A08B7A38G72
   75723132481E41FC021F46C86F83245BB92E037A1DC7207C320D6B5727934ECAB726177FF0137D9E1BF6BF66E7FFF2151E4D4A22774CA7A8FEEED9741E3941300B85D0176A646718D777617A48D01E89B09FE089408A000DCE3E2FAEAB2FA3D31CE1FA14E0A7EC3C32A9BCE47C1D9A3939FB16BF5B4E756F519FFF15AC396DB7493172512965CCBF2A787343A20AE77C4DF0A1FFDE2853G6682AC82C8GD8D94E79CB5DD743784334F20B26898343B4BB4F1943A4F2840D1B00FE77ADE066ED3DBE3FE39FFB966C
   1B2837CC5F1FEE60395E9AE8D3C6581D33D442FF6BCFD961FC5096D673A0B1E474D28617D17143F71136DF7DA639A2060C966A34EC5E9771BF3EBF8F59989F89F875C15747DF1A5EB7CBDF3EBF4F3E7AE54E41567775FD3163422D7B2C63C2FB0575D9A82DE272BBC45995316F9095D4186F90B17DFC26867DF0DD856C6DGC100B8003995DCB73FD5310F692605DE367B06B0AE45DB319A1F7FAB2E583C4EF6CDFD1776BE97FE0B6AD7B4CD7163789C7F4FAEDCAFCD89D224FE46F4210CE84215B555715FF1215E
   4A62A0C533D8E5070238C7012F048A9E3F1CA9C65C8AA82FB1F05FAD66B14D468A3E47BFB36EDC516602FD58B5791DBDE69F4FDCB267DE336ECF31F11F985DDE56478B3B22F32F2A9B7721F840BF89309640315F63A8G2883E883708248GF1G73G9681A4G2CGD883108410D54957F36BC14C7B778AD7A7FE4982249E1050C5D585F3AE60D7B531577537B5D32F6B6A9A7A5D91562367FAD0BFFB2C2D217905414110E8684F9FEA328CDCC728CB89C9F8E6E1B1EB390231562092507AAAF17FBCCA5754856F
   F515BC663DD20CEDB4A006792BD7F06BA0ECCFCFA866FB7A02CA6BBD5D8CCB208D8277E94BABA3FB9A5315D7289FDD81F83C3844F03F27387500BBEE63B6F68F9ED1F57EEEB934BF167C4122BE009005A570FB04A1234FED6E58323F43BD356C3F6A363629F96E09B6758D3735CDFD5BED36A9060BF723BF99821D719A6D969B388B866E87F4CC441D17FC5A3001DFCA69C27C9991A764781FD33A46A84E2B631EB061AE683865EE549FE0F9D8D1F95F675CD4C7A4D9E66597E89BA9G6AE44863C5CA239494CCF4
   7F4E40A15DCCF7CE9DA5041F171A0A518F8644B1A78F5F5D9ED0442AFDBEC9101521FDB54E3A9AE77D11EAEE7B73474C36FFEC475E185FDDB28E68B62FBF137ED6246F531B0A79BE6DBDE035CFF3BA9F422974F8D4284CA06C4EA750C5454D7045614C881E0BDB574F5E77F04C59B0C77C816EA7620E9E1C44CFF429CAD0D4356B8CCBEF1E687B6916E2332FDC8E63A59E185CD73279AE3D076F463E4606546B7A5F4B0B50AEFDC22FB31C030601A1E6F359A55C8EA336393B045BA162585F8B1EBAB88A0B997FAA
   C98929E417310E9DD4B9922FB5B45ADFFE50EEA30E8E320D99CB87F12046F71094EA909D448E46928E12281FC66A58059627B6ACBA79B73C2AD035D3D31CD2E0C09E750970F4629DC7A77BBBB5A0A0F31CA2BD7E0DF7DFD97E5B5E101DEC6BA00568634E09AA3BFA694374187EB79E9E1296445485DE65856AC2741192522E4BE2A7499B6C467BC0A1CD7435F0C106B412CFBF915662F7075AB3C2E8185E4ABC5EC99C817CEB9412432AE5A1B0D44B24FA1C32185C52C73943F5A1C0EFEE64C323B1CA6E3F6E5ADF
   7E412AA5F6E273109D32A278BC42GE8C487C95306D5B1B42C483EF070D4B39DA8F58C178443E8BB8C3757E09F19F577008BB3089E89FFFDD6661ACC8FF85030CAEAE9CBF469EE23056F74302042D4225A2161DF57A82A4B287B3BD22D72176F6F39ECA349BA13FFA17FCBD242C10DEC9B947C12FC9DDE18590630A6CD03FF2A908861A7F9204C99C5992FB2A6F05F73D45D3D8FED39C8A708F455C89993A5F42D9D9295FFED4B602094900D81C02EB0FC09B1BC36F1C73549895F1D7F418BEF77039E34913B3F2E
   0E7EDFCF7FBF0C7F57D3CCBD45549F86612E0FD647713F687AB06719AA0FE188387CEE8F2C2076622BCFBD706B2675EB01BB8FC921DFECE19132A22D4E0E7A7BC5753678FBCB5267A47EA32ED190D5A50C63E6326123FF680318DF2C7E4C6C97B75F1A149C4BAF3AEA185FB8D6939D9375513DC8837C6C9A6E2F969DBA944BDF413EFA4EAF4832A648A3AD819F6E312D57B5B192B30BEE9CBF445B68152CCE26FB57D5D31F7F83D0CB8788BBB9C76C4D94GG58BCGGD0CB818294G94G88G88G43CB5BACBB
   B9C76C4D94GG58BCGG8CGGGGGGGGGGGGGGGGGE2F5E9ECE4E5F2A0E4E1F4E1D0CB8586GGGG81G81GBAGGG8795GGGG
**end of data**/
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
   return new Dimension(350, 200);
}
/**
 * Return the SelectLabel property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getRepeaterLabel() {
   if (ivjRepeaterLabel == null) {
      try {
         ivjRepeaterLabel = new javax.swing.JLabel();
         ivjRepeaterLabel.setName("RepeaterLabel");
         ivjRepeaterLabel.setFont(new java.awt.Font("dialog", 0, 14));
         ivjRepeaterLabel.setText("Select the repeater(s) to include in this route:");
         // user code begin {1}
         // user code end
      } catch (java.lang.Throwable ivjExc) {
         // user code begin {2}
         // user code end
         handleException(ivjExc);
      }
   }
   return ivjRepeaterLabel;
}
/**
 * Return the AddRemovePanel1 property value.
 * @return com.cannontech.common.gui.util.AddRemovePanel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private com.cannontech.common.gui.util.AddRemovePanel getRepeatersAddRemovePanel() {
   if (ivjRepeatersAddRemovePanel == null) {
      try {
         ivjRepeatersAddRemovePanel = new com.cannontech.common.gui.util.AddRemovePanel();
         ivjRepeatersAddRemovePanel.setName("RepeatersAddRemovePanel");
         // user code begin {1}
         // user code end
      } catch (java.lang.Throwable ivjExc) {
         // user code begin {2}
         // user code end
         handleException(ivjExc);
      }
   }
   return ivjRepeatersAddRemovePanel;
}
/**
 * This method was created in VisualAge.
 * @return java.lang.Object
 * @param val java.lang.Object
 */
public Object getValue(Object val) 
{
   Integer routeID = ((com.cannontech.database.data.route.RouteBase) val).getRouteID();

   java.util.Vector repeaterVector = ((com.cannontech.database.data.route.CCURoute)val).getRepeaterVector();
   repeaterVector.removeAllElements();

   for( int i = 0; i < getRepeatersAddRemovePanel().rightListGetModel().getSize(); i++ )
   {
      com.cannontech.database.db.route.RepeaterRoute rRoute = new com.cannontech.database.db.route.RepeaterRoute();
      rRoute.setRouteID(routeID);
      rRoute.setDeviceID( new Integer(((com.cannontech.database.data.lite.LiteYukonPAObject)getRepeatersAddRemovePanel().rightListGetModel().getElementAt(i)).getYukonID()) );
      rRoute.setVariableBits(new Integer(7));
      rRoute.setRepeaterOrder(new Integer( i + 1 ) );
      
      repeaterVector.addElement( rRoute );   
   }
   RegenerateRoute routeBoss = new RegenerateRoute();
   RouteRole role = routeBoss.assignRouteLocation((CCURoute)val,0);
   if( role.getDuplicates().isEmpty() ) {
       ((CCURoute)val).getCarrierRoute().setCcuFixBits(new Integer(role.getFixedBit()));
       ((CCURoute)val).getCarrierRoute().setCcuVariableBits(new Integer(role.getVarbit()));
       
       int rptVarBit = role.getVarbit();

       for (int j = 0; j < repeaterVector.size(); j++) {
           RepeaterRoute rpt = ((RepeaterRoute) repeaterVector.get(j));
           if (rptVarBit + 1 <= 7) rptVarBit++;
           if (j+1 == repeaterVector.size()) rptVarBit = 7;  // Last repeater's variable bit is always lucky 7.
           rpt.setVariableBits(new Integer(rptVarBit));
       }
       
   }else {  // All route combinations have been used,  suggest a suitable role combonation to reuse.
       
       RoleConflictDialog frame = new RoleConflictDialog(owner, role, (CCURoute)val);
       frame.setLocationRelativeTo(this);
       String choice = frame.getValue();
       boolean finished = false;
       int startingSpot = role.getFixedBit();
       while(!finished) {
           
           if(choice == "Yes") {
               finished = true;
                ((CCURoute) val).getCarrierRoute().setCcuFixBits(new Integer(frame.getRole().getFixedBit()));
                ((CCURoute) val).getCarrierRoute().setCcuVariableBits(new Integer(frame.getRole().getVarbit()));
    
                int rptVarBit = frame.getRole().getVarbit();
    
                for (int j = 0; j < repeaterVector.size(); j++) {
                    RepeaterRoute rpt = ((RepeaterRoute) repeaterVector.get(j));
                    if (rptVarBit + 1 <= 7) {
                        rptVarBit++;
                    }
                    if (j + 1 == repeaterVector.size()) {
                        rptVarBit = 7; // Last repeater's variable bit is always lucky 7.
                    }
                    rpt.setVariableBits(new Integer(rptVarBit));
                }
           }else if(choice == "No") {
               startingSpot = startingSpot+1;
               if(startingSpot >= 32) {
                   finished = true;
               }else {
                   RouteRole nextRole = routeBoss.assignRouteLocation((CCURoute)val,startingSpot);
                   startingSpot = nextRole.getFixedBit();
                   frame.setNewRole(nextRole);
                   choice = frame.getValue();
               }
           }else {
               finished = true;
//               wizard.getWizardButtonPanel().getCancelButton().doClick();
               return null;
           }
       }
       
   }
   
   return val;
}
/**
 * Called whenever the part throws an exception.
 * @param exception java.lang.Throwable
 */
private void handleException(Throwable exception) {

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
   getRepeatersAddRemovePanel().addAddRemovePanelListener(this);
}
/**
 * Initialize the class.
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void initialize() {
   try {
      // user code begin {1}
      // user code end
      setName("RepeaterSelectPanel");
      setLayout(new java.awt.GridBagLayout());
      setSize(370, 243);

      java.awt.GridBagConstraints constraintsRepeaterLabel = new java.awt.GridBagConstraints();
      constraintsRepeaterLabel.gridx = 1; constraintsRepeaterLabel.gridy = 1;
      constraintsRepeaterLabel.anchor = java.awt.GridBagConstraints.WEST;
      constraintsRepeaterLabel.insets = new java.awt.Insets(5, 0, 0, 0);
      add(getRepeaterLabel(), constraintsRepeaterLabel);

      java.awt.GridBagConstraints constraintsRepeatersAddRemovePanel = new java.awt.GridBagConstraints();
      constraintsRepeatersAddRemovePanel.gridx = 1; constraintsRepeatersAddRemovePanel.gridy = 2;
      constraintsRepeatersAddRemovePanel.fill = java.awt.GridBagConstraints.BOTH;
      constraintsRepeatersAddRemovePanel.weightx = 1.0;
      constraintsRepeatersAddRemovePanel.weighty = 1.0;
      add(getRepeatersAddRemovePanel(), constraintsRepeatersAddRemovePanel);
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
   if( getRepeatersAddRemovePanel().rightListGetModel().getSize() < 1 )
   {
      setErrorString("One or more repeaters should be selected");
      return false;
   }
   else
      return true;
}
/**
 * Method to handle events for the AddRemovePanelListener interface.
 * @param newEvent java.util.EventObject
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
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
      java.awt.Frame frame = new java.awt.Frame();
      RepeaterSelectPanel aRepeaterSelectPanel;
      aRepeaterSelectPanel = new RepeaterSelectPanel();
      frame.add("Center", aRepeaterSelectPanel);
      frame.setSize(aRepeaterSelectPanel.getSize());
      frame.addWindowListener(new java.awt.event.WindowAdapter() {
         public void windowClosing(java.awt.event.WindowEvent e) {
            System.exit(0);
         };
      });
      frame.setVisible(true);
   } catch (Throwable exception) {
      System.err.println("Exception occurred in main() of com.ibm.uvm.abt.edit.DeletedClassView");
      com.cannontech.clientutils.CTILogger.error( exception.getMessage(), exception );;
   }
}
/**
 * Method to handle events for the AddRemovePanelListener interface.
 * @param newEvent java.util.EventObject
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
public void removeButtonAction_actionPerformed(java.util.EventObject newEvent) {
   // user code begin {1}
   // user code end
   if (newEvent.getSource() == getRepeatersAddRemovePanel()) 
      connEtoC2(newEvent);
   // user code begin {2}
   // user code end
}
/**
 * Comment
 */
public void repeatersAddRemovePanel_RightListMouse_mouseExited(java.util.EventObject newEvent) {
   rightListItemIndex = -1;
   rightListDragging = false;

   return;
}
/**
 * Comment
 */
public void repeatersAddRemovePanel_RightListMouse_mousePressed(java.util.EventObject newEvent) {
   rightListItemIndex = getRepeatersAddRemovePanel().rightListGetSelectedIndex();
   rightListDragging = true;

   return;
}
/**
 * Comment
 */
public void repeatersAddRemovePanel_RightListMouse_mouseReleased(java.util.EventObject newEvent) {
   int indexSelected = getRepeatersAddRemovePanel().rightListGetSelectedIndex();

   if ( rightListDragging &&  indexSelected != -1 && indexSelected != rightListItemIndex )
   {

      Object itemSelected = new Object();
      java.util.Vector destItems = new java.util.Vector( getRepeatersAddRemovePanel().rightListGetModel().getSize() + 1 );

      for( int i = 0; i < getRepeatersAddRemovePanel().rightListGetModel().getSize(); i++ )
         destItems.addElement( getRepeatersAddRemovePanel().rightListGetModel().getElementAt(i) );

      itemSelected = destItems.elementAt( rightListItemIndex );
      destItems.removeElementAt( rightListItemIndex );
      destItems.insertElementAt( itemSelected, indexSelected );
      getRepeatersAddRemovePanel().rightListSetListData(destItems);

      getRepeatersAddRemovePanel().revalidate();
      getRepeatersAddRemovePanel().repaint();

      // reset the values
      rightListItemIndex = -1;
      fireInputUpdate();
   }

   rightListDragging = false;

   return;
}
/**
 * Method to handle events for the AddRemovePanelListener interface.
 * @param newEvent java.util.EventObject
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
public void rightListListSelection_valueChanged(java.util.EventObject newEvent) {
   // user code begin {1}
   // user code end
   // user code begin {2}
   // user code end
}
/**
 * Method to handle events for the AddRemovePanelListener interface.
 * @param newEvent java.util.EventObject
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
public void rightListMouse_mouseClicked(java.util.EventObject newEvent) {
   // user code begin {1}
   // user code end
   // user code begin {2}
   // user code end
}
/**
 * Method to handle events for the AddRemovePanelListener interface.
 * @param newEvent java.util.EventObject
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
public void rightListMouse_mouseEntered(java.util.EventObject newEvent) {
   // user code begin {1}
   // user code end
   // user code begin {2}
   // user code end
}
/**
 * Method to handle events for the AddRemovePanelListener interface.
 * @param newEvent java.util.EventObject
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
public void rightListMouse_mouseExited(java.util.EventObject newEvent) {
   // user code begin {1}
   // user code end
   if (newEvent.getSource() == getRepeatersAddRemovePanel()) 
      connEtoC5(newEvent);
   // user code begin {2}
   // user code end
}
/**
 * Method to handle events for the AddRemovePanelListener interface.
 * @param newEvent java.util.EventObject
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
public void rightListMouse_mousePressed(java.util.EventObject newEvent) {
   // user code begin {1}
   // user code end
   if (newEvent.getSource() == getRepeatersAddRemovePanel()) 
      connEtoC3(newEvent);
   // user code begin {2}
   // user code end
}
/**
 * Method to handle events for the AddRemovePanelListener interface.
 * @param newEvent java.util.EventObject
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
public void rightListMouse_mouseReleased(java.util.EventObject newEvent) {
   // user code begin {1}
   // user code end
   if (newEvent.getSource() == getRepeatersAddRemovePanel()) 
      connEtoC4(newEvent);
   // user code begin {2}
   // user code end
}
/**
 * Method to handle events for the AddRemovePanelListener interface.
 * @param newEvent java.util.EventObject
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
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
public void setValue(Object val) {
   IDatabaseCache cache =
               com.cannontech.database.cache.DefaultDatabaseCache.getInstance();
   java.util.Vector allRepeaters = null;
   synchronized(cache)
   {
      java.util.List allDevices = cache.getAllDevices();
      allRepeaters = new java.util.Vector();
      for(int i=0;i<allDevices.size();i++)
         if( com.cannontech.database.data.device.DeviceTypesFuncs.isRepeater( ((com.cannontech.database.data.lite.LiteYukonPAObject)allDevices.get(i)).getType()) )
            allRepeaters.add(allDevices.get(i));
   }

   com.cannontech.common.gui.util.AddRemovePanel repeatersPanel = getRepeatersAddRemovePanel();
   repeatersPanel.setMode(repeatersPanel.TRANSFER_MODE);
   repeatersPanel.leftListRemoveAll();
   repeatersPanel.rightListRemoveAll();

   repeatersPanel.leftListSetListData(allRepeaters);
}

public void setFirstFocus() 
{
    // Make sure that when its time to display this panel, the focus starts in the top component
    javax.swing.SwingUtilities.invokeLater( new Runnable() 
        { 
        public void run() 
            { 
            getRepeatersAddRemovePanel().requestFocus();
        } 
    });    
}

}