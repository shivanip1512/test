package com.cannontech.tdc.roweditor;
/**
 * Insert the type's description here.
 * Creation date: (3/8/00 11:45:00 AM)
 * @author: 
 */
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.util.Iterator;
import java.util.Set;
import java.util.Vector;

import javax.swing.DefaultListCellRenderer;
import javax.swing.ImageIcon;
import javax.swing.JList;
import javax.swing.UIManager;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import com.cannontech.clientutils.CTILogger;
import com.cannontech.common.gui.util.Colors;
import com.cannontech.common.util.CtiUtilities;
import com.cannontech.database.cache.functions.YukonImageFuncs;
import com.cannontech.database.data.lite.LiteTag;
import com.cannontech.message.dispatch.message.Multi;
import com.cannontech.message.dispatch.message.TagMsg;
import com.cannontech.message.util.Message;
import com.cannontech.message.util.MessageEvent;
import com.cannontech.message.util.MessageListener;
import com.cannontech.tags.Tag;
import com.cannontech.tdc.TDCMainFrame;
import com.cannontech.tdc.alarms.gui.AlarmingRow;
import com.cannontech.tdc.logbox.MessageBoxFrame;

public class TagsEditorPanel extends ManualEntryJPanel implements RowEditorDialogListener, ListSelectionListener, MessageListener
{

	/**
	 * A renderer to use for our LiteTags
	 * @author rneuharth
	 *
	 */
	protected class LiteTagRenderer extends DefaultListCellRenderer
	{
		
		public Component getListCellRendererComponent(	JList list,Object value,
			int index, boolean isSelected, boolean cellHasFocus)
		{
			setComponentOrientation(list.getComponentOrientation());

			if (isSelected) 
				setBackground( Color.LIGHT_GRAY );
			else
				setBackground(list.getBackground());

			if( value instanceof LiteTag )
			{
				LiteTag tag = (LiteTag)value;
				if( tag.getImageID() > CtiUtilities.NONE_ZERO_ID )
				{
					setIcon( 
						new ImageIcon(java.awt.Toolkit.getDefaultToolkit().createImage(
							YukonImageFuncs.getLiteYukonImage(tag.getImageID()).getImageValue()) ) );
				}
				else
					setIcon( null );
				
				setText( tag.getTagName() );
				setForeground( Colors.getColor(tag.getColorID()) );		
	
				setEnabled(list.isEnabled());
				setBorder((cellHasFocus) ? UIManager.getBorder("List.focusCellHighlightBorder") : noFocusBorder);
	
				return this;
			}
			else
				return super.getListCellRendererComponent( 
						list, value, index, isSelected, cellHasFocus);
		}
	}

	private TagTableModel _tagTableModel = null;
	private TagWizardPanel _tagWizardPanel = null;
	
	private javax.swing.JLabel ivjJLabelActualName = null;
	private javax.swing.JLabel ivjJLabelPointName = null;
	private javax.swing.JScrollPane ivjJScrollPaneTags = null;
	private javax.swing.JTable ivjJTableTags = null;
	private javax.swing.JSplitPane ivjJSplitPaneTags = null;

/**
 * TagsEditorPanel constructor comment.
 */
private TagsEditorPanel() {
	super();
	initialize();
}


/**
 * TagsEditorPanel constructor comment.
 */
public TagsEditorPanel(com.cannontech.tdc.roweditor.EditorDialogData data, java.lang.Object currentValue) {
	super(data, currentValue);
	initialize();
}


/**
 * TagsEditorPanel constructor comment.
 */
public TagsEditorPanel( EditorDialogData data, Object currentValue, AlarmingRow alarmRow_ )
{
	super( data, currentValue, alarmRow_ );
		
	initialize();
}


/**
 * 
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private static void getBuilderData() {
/*V1.1
**start of data**
	D0CB838494G88G88GD80CACB0GGGGGGGGGGGG8CGGGE2F5E9ECE4E5F2A0E4E1F4E13DBB8BF49457F5B8855BE9B929DC139CB5A6290D31AB470ACB9392F3943715D32745B5ED6C939C131436CAC2EDDA4BBEF64DB9A1B9BA4D5BDD2D508A018508D800C0B2869BE2E5C1985BC248B10295C6D82035A4A4994908516EE8F72459195D59590FC0C06FFDEF5E6CE835AB614414F32EE65EFD3FFB6F3B5FB78B292E3B53E933D793E2F392635F3BB6C2BC6F9062FED55B4BF1EBBB8BDC44726FD500
	CD24CFAF05E7393740651FD3602AA2CF784A21DD8B6D0ED9852ED760FD8B7921EF4FC1F84932CF4981C26A5A3FF44B747B2C1278BE6BE87B59F7AB613991209EF09E009CAF6E6FE82AB170C1E8EFB45E91ECF6C276FD04E304C40D853F5C98D77D91FBD6F66118DDCD35C6FF98703782AC85D8E90CD983CFC6641C66A6E35C60EDF9E44DBF8D5AB760DE1675B974F43139C963D9CFD6D1C1B848B36D0D70EC710E85FE67D3C3EE1F20A82A220B3E20DB777B5C1A1A907D122EEA4D4DBDC220CF3AA4E8FE2F2008B2
	4CD9CDE93DA5D1CE57A1E4D197276B098DAD463E734672C8515D1DD969EA68E674F83BD3F48D6E5D6DA4BD387687CD15463C9C68DF8A40E83E3F7650F5539CED236F86510C66BFED62B4FBE01FC768DE2FC6F12F89F81F8710EF629F08A33E7CDC012B9E20699C67713DB7B80F7D179FA405CF3F6FFCB98B0F5EF3066C4F25F8FC7C155D4EEDD41742E3288F0B21EF85C0B1C0D5EF016BA00097E0875561B7C7FF834F56E17D049A8A29CAEBB8FCDC76EBE7D43FA843FBF3B3749838AE5127EAFEC2181E4ECDB4E4
	2007C330F7320EA2B05E7A88572B7C2E874862FE2F5319013745CFDE30CF98EB647426F8E3FCD506102FC500DF86308A208420362F40555247790A0D3EB593DF6DF02E1AA2485D316130206947E3126C97B5033FB3CD53711789583C01CFBD0F49A56FAD2BDCD6FEF316FDDD86392C4C936D7DFDCC9E613E14DC585E73326E1D53B775BC281C7BD08F87289EAE04F71B213B8CAFD27CDA8B1E594E9BD41F31DD5EDF60127B395E76EF64BCF414AEA0D50DB7E564A1773BF17B637D0C1E557DA99E581E3B9338F675
	47ECCF1BC94B1B94DFEF41B3DA9647399DB7C0DF8EG5311558A7A39B5502EBADF605A87F08C60A24084C04EF92EA70523872ED37F3D969DE1F34747FE8DCFB129EBC23B92558545A7768A1AA48C4BE21498E3BEB706B2981EB46F96EA17BE586383D191B5C117D485D697C2A2A8E4140D49995B04E3B4117676C8A191B6880589DFB7F7EC8BBCE5A12A1F8B7B85DDCC0DE07DF70E61194405DED8919600772273A82F3B29CFDD846F4B4EF3D9ED8DA3EE4FG584FG174B8EF9B3BCBF8483916566E60F64C3B284
	ED1C6F3FB504BEC90C0B0A6E9143226207172851D79BF22564B2221AB46F26F8343593C0F854446F8BEDE2F8D843AAE55EFF10768B3A2EC943B1DD6C96F5DDD2822979CD141668A4B30E9243D7C4D0BF3C40D790A0E97277F0E80B053F8ED59D0B05FB70648CB95DCF7D78DC983FF00047E6A10E31FA69G5777677B393EFBB6FC1DB4BCF9AD233EA32C9AE0FADEB210EE33E3F44D549E3F0D76FFCA7F1189168EE436F73647494FE48FC796BC137BB58D653EE7906C8E6062A017FBC9281E595BF1A1AAF28B6B54
	6044C4FFCA4B981D7B924424395172DE907FAC681F5E0F7F60066E41E471BFCAF69B7A05125906FE25E436A127BA3D0DECD0B359886B2BD6B1862B8945E2F35BD434FF1F2AA8A2BD94F38AE1F95273BD480724C8FA1F648F08FA37F4C9A4644EC1341D826AE31270BEF7105BCA62B5EEABFBAE5DC78A074E5BB3457D5CFB466D2B86196E2E9DCC598A23E9FB98F5CA144590886A049AD3F4062F277E4B409B7EF3F3A473581A885A5B18B8EE0C2B0D14917413F2CCB4F9DBA42CC799EBD288D4919D1211DFFF3749
	1ADF57FC82BE85F26CFEF84AG8E3A5E53F558874961245C105944929AB7E74358DC00C51FF01B480DE0BF77C506B11CD0E519095EBC13B9DA951E2198090990EA50D9FDE872B1D75B1C5657EB70447A67E94813EC71F3CBBF21F50D19E3E39CE438559437977E5CD34A7313B3D556FAC00EE44E5B90AAAF9867F6A1BDEFF346F05DE30077DE603C3F9C357A6156E8D475C9B46499E7D1FE66B0392EFC7C0C20448439CD51357153D46AAC9FEFB8D2EE58347F824F3BBFDA0767B09F5A0B811619347CEDF42A9E7D
	FD34821EA3129605FC51AFA639BC9F0D22DCB07E9AE81E5F9E324A2930E8A4231E5318E048E76D94B9FD98C7BA2A07A0EF996234F974CD96B9C1AE8E69C6GCE1153D391F3B03DB65A9BE3CE2E57447421C7F8FE58B2E454C6C329B8619F1A9AB3D86E75BDAA2F64904B3D561978A7A8BE4702E7B9D99F45E3BB8F7AAAAF72BC23156AD5985A33AEF23E6E0EED307065D5A5C5B7F9724651B6D5CD8AC88A471D23B828C42833E2317A94BD639CD8B39F0061122508AB0476F293372B8CF1EB21DDA570F87B982575
	B0343DG3209FF0A62EF05F69E40D2937F493A6962824E473552715E8C386C31A7BE6D9E6BC866586B3C0E75D33167E4824FACE936CF537638317696B2F4844FA52C09A322865E0839F732E7184A164A3B98F8DAABF03F3738340C72BF2E4660G9F226376BB2D7EB288389C000543CC964C3E7FBA01FEE98412DF88D75DE151C7F2FDB8776DB2BE37945AB5GCDG7DGC99F5F7356CD9853CEF7884322CCB567AC2450867DDF992EB27BDAFDBA38866CE4FDEFA7F05D535DE1D95251D5E0A98F7E5C0F7BD614D7
	DBE8AE72317C2F947AD87FD775CC7D97210F45083FBAE265D977337E7970DC8430540FF7C01DD709A967D0E9BDBC95B1D1BFAA44853710505D9EC86595CC5A779DEEFC37213E711DA37B19EF39B3E175ADCD95F2D65FD2A99ABE45F84E5DF10BF1A753F8916BA26ACF79064D4D9FE8127F3890682027477CE627335CD83BDF64FE730A135ABB3473G8AC59E5B132F737B0A7CFB97100ED933B25E499475846CAB8CFACA44144FDB754AEEA75BF3200C6F59B4D2608A0FA06E73E5D6F9234CFE86835C281B347FBB
	137A51C73ECB2C7D4FCD6AC7FA6FF0D87B6B56D97BD1C66F997399EF0F6D653C15BC3608485FD2325EB7AD99E1BC2DB41E757FFD332D66095DCEA67717C6EA2CF2CF3A2389D07866E626210C7F1F1773D8D59D60323DF252FA5F75504B817B7AAC7BF784583EF2A0A55B20A95BA7F53EF6FE005FF55D612E4DCC13E999EC6EB3545E111ED5812E8BF705D097EA03606FGBA021C5E6F8FD8F5346A4B3DF6DB96FA7DC1639EA3189E7FC4027BAD84FCE1104709F9D75F04E72FB0EAD463AC3D228584A03D1A9A96B5
	FD1CE1E966E378529E67273D0FE1316DC52A6BC54174FB08DFD2FCF1B07D9E620F1C7C9EE2AD748DCABC66FD490667DE886D15GEBGAAC78BDC8DG27C6B9EF7FF6952F11273D0368D1439DD0A84816721871F7316C773BEFFAF89D572B4585DFA3CBEEF6E7552B41D1F6BE9346F3496A1BED78E47C6DF7A0FF79503794209820F48CFCA54041B14E5F5EEB8D0CBFBC1ED6DD97FCC11AE1F166FAA5B111C2E390E03C1514DBF9730C317D7B0D27BCB6B526B21AEEAFC51B1E857D73819683AC83A8B669D12F4DF8
	B758161C66EE70473A152ECA5950DF63F9D81ECA971BD7CC732FD3C67FE05A38F0DAFBB634194EEDD5D037F265747B39BA0A4F13536F677EB2446F6716C0DFED086B62B7687789A603872F6D4DE6CF407EE9F0871D432310E5C2ECB4670CD23A4C9A1319195197A1A8F32C87A5C116AE51943E9B86AA81467B4F695E6C1D14255EBF6F3C71357C029B3A87637FAFEC59EB79FBED19EB79FCDB66DA7E9B3669EB7982DB76DA7EE11B3516E7677EF7B4864E82BD188FD088309CE0F5085BC327FDB733078C36598645
	7D89155EEE6AFC7FC30E6C3C9EF14CFCA7FA5C01B555302A6BEA88576778D38EBCAFDD8DD3A4431DF1200C2084886AEC28016FF6E00C1045910AE6797D969762CA1582D73542F37E01DA446D03F61309AB2C65F500C761B23823B4BBCF8BCAB31FDFDEE96673FB20F47A7B4CEF17E6BBDFC65F0BB4D64F835A961AB47FA745AD0176D293F7BA4679D8EE629C4D74AED7ADF09DD3B9EE71D14475C0BBE8623E351E4F1DEB62DAC208DB896DB5AA2FF7F6A8747BE418377752769E337D75D0A65B42711F1D7D7EC346
	BD325DBF7EFE767B0FE5594F7FDF4AE6566927E86EAA02AE760C07C506DBD9C66B57111128E85A4ECF4A2ACCDCFB542BC6A574142C96780D8BE5BB22AABA2B330ED1FDBE85326C89731AC72B45B1BA9043EA89DAD21305E19C7FD42DE79ABE9DE9B09B66A03CC6D85C4F0577CD46BB5B2BBCB6191EDDD45F0AE15CDA131E92FDB2BDDFF6256891E25351631C069E6C5B97A9F0FD5F169A3705ECEF46B372CBF1892C29C37531FBA2C65B1FD39DC77FF0B842E9FB6CA84AFFD8552871311A7427C7F9FD6601F103G
	39G7991D663B33A172D1FC90ECE8B7D0F405C9591C6B7360B617DA83C3CED63F4F707B037D2B589F44ACA73D80857BBEBCD1AF3A5DA53F67BB4D5164D621455163D0A3536EC50984D7D7014B5DEDBBE0BDD463E1D356857A75516A761B0776D76C30CD6833B9B6A8FC2F9F9E8BFA1F5F4BC21F9B77F47565B0E6AEA640CBF2964B9E36D7DF7934FD3EEBB571DF4994D56187DAF57D2393D7768EBCE3626F7EB0A469F692FE62631958CCC86929B8D928DFA3E796B545CC5C76D70FC9B13E71A77D434703C27B28A
	3EBD4A6B1C3F2940A1196E90FDB481F487E212BB26CBF2F3F30F244B62F12AB12C7669EAA9B74EFAA24AEB9F7BFA5CE7B13417812C8218AA3F975E4F04E3BC7C7015948F1F8BE19EE6A93F5C296A4B5D5B565553FE2235E3285B5B515EE36ADC20EDA749DC3719DAC3786F913E20705A2D44247F903DC7BF2CC35D866057F9FE78C88D6A7D8434F3819E82D88E309A0044A026826C83E88168811081E6814C834883D882308C20A846E36AD0951EDB361A4D38F4204905A630FBBF666F2BDAE62AE95644A64797B6
	EF570C73F62535593CBEBA2FAAFEFDB1EBCF1C6765F12A87CD71749A719A45FB6269B5E279FADEA3FAF10DB80F2137AB1C6E1A845406G4792A9DAFA92596EE56F256798CC2457850BA8BE19C82F8B1A425CBEEE053E6284BF2795B559E35FFF544C9C7BD657E44E671E2F191A4F3DD413B91F7BC50DB51FE3387F2DC1FD8F001E741B639C5BB86EE393D73E4DE5607AA43F9EB4719B37D59878D3A2EE4871555B102E844575736FDD965C4786EE57B60C9F40F2D055785C57365118A849B2EB3FC147C88A04F3D3
	9E8D14C6A9ACD868EEB4F1C877CEFA8E05DBB650393224C00ECDFF8DC0F88E28D03F63CDC24E166479599FD7A0EE825AB9C92EBB9398DA8CBD1EE3F95F387E4657D5EF5C50BD984FDF237EEBBE70DBE84A4121A2EEB934CBCC5C850A2B9AAFF06D996738FBD41E639E9E67724A8F6379DDB45B5F266D24597EDD788F6189571DD9EEFDFF509EB3653DAC2E4CE9C11B50B11CE1BCA57C6E7EABD42F66831DF963BC67382B424947929EE76F5CCE4C359ED6B0E045C5CD17FC024C024CF1C1732A32649BB756DDC94F
	6937GAB4CF5372B98D740F824CBC04254191C26F768B9613DEB4DA5BEB726DAF33760A59CB7D745339A047747E97BA1355E60B97F52D439979AB863DB81C1DFF01AFE7AE4777DAC27FAA6BC691B6EE5509F4881DB60693D4CF32A125A14AC2ED6DCC73EF2F83FB97EA585FF83B9B93E1BD15A9260CFF4F5F6F49CEF6D9AEA6D68EB7D385B1CFFC6FDE71A7927BA3B5A4FF71E6D193CC277505936B6CF1B072F31GCBD6A307FBCF345E4F9E3C7C0E9D47E44A6102170D5F96DECEFDC3E0B945AA1567C4F91779F9
	550671893E5E58E7344A1AAB2E9CBFE65F14E51FA2E37D4A09D42E381170DCE774F216FBF3FA120C1E3A08791B4B891E63A87477A793501E8710BF41F31C37F6218EBDB241E35F7BBBB1C74E162320D7E637D85CF67FCB356698ABE01D620969ED1E6DBBD0073ACA2E9438EA810E81748348G3381F28196DD61B44D5EB95DDD37199EDAB2A7C65B1E48F4794F32AB5972081FD2D995DDC94FE31E257862AB69F90C2E19F75D50B7F815537D6EAE345928A1AF6603897DEDC6A10CF95041F1CE0ADB8138100D4747
	113A6CB9C828EE669CA4DE17B98719281B1A035CD417B9873925EEEA8EB2172E2B0981A85B6867C5E36C1F55E16CE65FBB4F880A902077BB0C3F23919EF756DC65F1E5B5F69B677422657D12F66363746DB7F48FE627426B2893467DAFCD43983EB639A10567E918710D37AB42FDEF4DB548FB81BAGE40039G4C361391DEA367DF635F846F0D24FF933CED52B741951174EF02B8BF55DF2125FF931CBC3FC5CB7FA64867B3F9DE19564EB2817B7F82BF08E0204DFF0EB8239A59BC75FBEF6B97FE74D47E10275A
	CE6C6D24ACC80555CE1C8914E692FF7AF99FD0F217CE4A68476EE61DD4F95AFA074E361EE9BB606D21CFD8F7432438743D3B5EFEFA593F77C69D242C1DEC432B1CEED123B774225FA3687C6721A709AB0CEB62E77EF6D29A258FE7D49F17458ED2BD5205BFAB0A6A223F155FD9C6F5323583F7BA00F57301D3C2B42823BD9E6DA0258A7E1015ECE65D3220848EB097FA1472E0EBBD065ACFD0DD88503B05520B2B384DD536FC67653D2F1EDE3544C16CA7C9052C8A7E13028FDCE0BBF169C1CD0C86D5591F8C3F4F
	96AA27D7E0187ABAE0399DA67B482C3B873C06C974D872ABF3B72C5EF0528EC1CDA38768C83C39F21BA37CA7020286DB09DA3B0EBF4CD535BDE65FCD3B74577F7903D7866D44E6B099A1D7C9D9B22C83D51A20C4C7D4AD84945427D1403B4C055C37BFFA700B777D72BCDDA8B5D5A7F5CCE4B075C0C0D4C387DAC7C620B2B097G794172BB4D65F10CBBED4C6664EDDBCFBF792BE1B8EF8FF1048E9E24FF9B695FC3783711E29AA9267190887185B26D3F747EE494392E501912GBDAFD3FA70185DA76124749FBF
	776802FFEDF9219838BBC91C7487C8A06C2A542833315030285D1C7F451D6B67957D891EC598D5022FDBB6A204A4F99CDEBC7416AA09786DC9F3A7643C1D16DBC571371A9DBF3B5BE1A725E0AE4C750372227F8665551875B84075D33D432B228A7E241B1526E9230DE24B2728D5A922ABE967C67AF75327969A4E5AC267EE458D7125466450641CD51FEC58B1BEEDB713788E03F404A6138544BA8D46B0765AF5D2F9A226610707FA4CF2ADFCDE2414EC7C7F6373E2BA1F58FA8D7F6C25635CC91E136E3FE64DC9
	574EB91375F7ADF9F3D88E73501C745F8C1CE55FC2GDFFDB34FC73E2AE3BD126D9BE7F7C810E528CA46DA95BFFE6FBCBE2E0B29EFB85DB1DCBF4A47981D2C6FFC8C699E354C79BFD0CB8788728A8770EE94GGECB6GGD0CB818294G94G88G88GD80CACB0728A8770EE94GGECB6GG8CGGGGGGGGGGGGGGGGGE2F5E9ECE4E5F2A0E4E1F4E1D0CB8586GGGG81G81GBAGGG2894GGGG
**end of data**/
}

/**
 * Insert the method's description here.
 * Creation date: (1/15/2001 11:40:23 AM)
 * @return com.cannontech.tdc.roweditor.EditorDialogData
 */
public EditorDialogData getEditorData() 
{
	return super.getEditorData();
}

/**
 * Return the JLabelActualName property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getJLabelActualName() {
	if (ivjJLabelActualName == null) {
		try {
			ivjJLabelActualName = new javax.swing.JLabel();
			ivjJLabelActualName.setName("JLabelActualName");
			ivjJLabelActualName.setFont(new java.awt.Font("Arial", 1, 12));
			ivjJLabelActualName.setText("DEV_NAME/PT_NAME");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJLabelActualName;
}

/**
 * Return the JLabelPointName property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getJLabelPointName() {
	if (ivjJLabelPointName == null) {
		try {
			ivjJLabelPointName = new javax.swing.JLabel();
			ivjJLabelPointName.setName("JLabelPointName");
			ivjJLabelPointName.setFont(new java.awt.Font("dialog", 0, 12));
			ivjJLabelPointName.setText("Point:");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJLabelPointName;
}

/**
 * Return the JScrollPaneTags property value.
 * @return javax.swing.JScrollPane
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JScrollPane getJScrollPaneTags() {
	if (ivjJScrollPaneTags == null) {
		try {
			ivjJScrollPaneTags = new javax.swing.JScrollPane();
			ivjJScrollPaneTags.setName("JScrollPaneTags");
			ivjJScrollPaneTags.setVerticalScrollBarPolicy(javax.swing.JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
			ivjJScrollPaneTags.setHorizontalScrollBarPolicy(javax.swing.JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
			getJScrollPaneTags().setViewportView(getJTableTags());
			// user code begin {1}
			
			ivjJScrollPaneTags.setVerticalScrollBarPolicy(javax.swing.JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
			
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJScrollPaneTags;
}

private TagWizardPanel getJPanelTagWizard()
{
	if( _tagWizardPanel == null )
		_tagWizardPanel = new TagWizardPanel( getEditorData(), true )
		{
			public void jButtonUpdate_ActionEvents() 
			{
				super.jButtonUpdate_ActionEvents();

				//be sure to tell out table to refresh
				getTagTableModel().fireTableRowsUpdated( 0, getTagTableModel().getRowCount()-1 );
			}
			
			
			public void jButtonDelete_ActionEvents() 
			{
				super.jButtonDelete_ActionEvents();
								
				initTagTableData();
			}			
		};

	return _tagWizardPanel;
}

/**
 * Return the JSplitPaneTags property value.
 * @return javax.swing.JSplitPane
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JSplitPane getJSplitPaneTags() {
	if (ivjJSplitPaneTags == null) {
		try {
			com.cannontech.common.gui.util.TitleBorder ivjLocalBorder;
			ivjLocalBorder = new com.cannontech.common.gui.util.TitleBorder();
			ivjLocalBorder.setTitleFont(new java.awt.Font("Arial", 1, 14));
			ivjLocalBorder.setTitle("Current Tags");
			ivjJSplitPaneTags = new javax.swing.JSplitPane(javax.swing.JSplitPane.VERTICAL_SPLIT);
			ivjJSplitPaneTags.setName("JSplitPaneTags");
			ivjJSplitPaneTags.setDividerLocation(50);
			ivjJSplitPaneTags.setBorder(ivjLocalBorder);
			getJSplitPaneTags().add(getJScrollPaneTags(), "top");
			// user code begin {1}			
			
			getJSplitPaneTags().setDividerSize( 8 );

			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJSplitPaneTags;
}

/**
 * Return the JTableTags property value.
 * @return javax.swing.JTable
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JTable getJTableTags() {
	if (ivjJTableTags == null) {
		try {
			ivjJTableTags = new javax.swing.JTable();
			ivjJTableTags.setName("JTableTags");
			getJScrollPaneTags().setColumnHeaderView(ivjJTableTags.getTableHeader());
			getJScrollPaneTags().getViewport().setBackingStoreEnabled(true);
			ivjJTableTags.setBounds(0, 0, 200, 200);
			// user code begin {1}

			ivjJTableTags.setModel( getTagTableModel() );

			ivjJTableTags.setAutoResizeMode(javax.swing.JTable.AUTO_RESIZE_NEXT_COLUMN);
			ivjJTableTags.setShowVerticalLines(false);
			ivjJTableTags.setShowHorizontalLines(false);
			ivjJTableTags.setIntercellSpacing( new Dimension(0, 0) );
			
			//set it to a monospaced font
//			ivjJTableTags.setFont(
//					new Font( "Monospaced", Font.PLAIN, 12) );

			//make the cells a little bigger than the font
			ivjJTableTags.setRowHeight( 
					ivjJTableTags.getFont().getSize() + 6 );

			ivjJTableTags.setDefaultRenderer( Object.class, new TagTableCellRenderer() );
			ivjJTableTags.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);

			getJTableTags().getSelectionModel().addListSelectionListener(this);
			
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJTableTags;
}


/**
 * Insert the method's description here.
 * Creation date: (11/14/2001 2:39:29 PM)
 * @return java.lang.String
 */
public String getPanelTitle()
{
	return
		getEditorData().getDeviceName() + " / " +
		getEditorData().getPointName() + " Tag Editor";
}


/**
 * Return the tagTableModel property value.
 * @return javax.swing.JTable
 */
private TagTableModel getTagTableModel() 
{
	if( _tagTableModel == null )
		_tagTableModel = new TagTableModel();

	return _tagTableModel;
}


/**
 * Called whenever the part throws an exception.
 * @param exception java.lang.Throwable
 */
private void handleException(java.lang.Throwable exception) {

	/* Uncomment the following lines to print uncaught exceptions to stdout */
	CTILogger.info("--------- UNCAUGHT EXCEPTION AnalogPanel() ---------");
	CTILogger.error( exception.getMessage(), exception );;

	TDCMainFrame.messageLog.addMessage(exception.toString() + " in : " + this.getClass(), MessageBoxFrame.ERROR_MSG );
}

private void initTagTableData()
{
	getTagTableModel().clear();

	Set tagSet = SendData.getInstance().getTagMgr().getTags( getEditorData().getPointID() );
	Iterator it = tagSet.iterator();
	while( it.hasNext() )
	{
		_tagTableModel.addRow( (Tag)it.next() );
	}


	getJPanelTagWizard().setTag( null );
	
	//	be sure to tell out table to refresh
	getTagTableModel().fireTableDataChanged();
}

/**
 * Initialize the class.
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void initialize() {
	try {
		// user code begin {1}
		// user code end
		setName("TagEditorPanel");
		setPreferredSize(new java.awt.Dimension(417, 169));
		setLayout(new java.awt.GridBagLayout());
		setSize(461, 241);

		java.awt.GridBagConstraints constraintsJLabelPointName = new java.awt.GridBagConstraints();
		constraintsJLabelPointName.gridx = 1; constraintsJLabelPointName.gridy = 1;
		constraintsJLabelPointName.anchor = java.awt.GridBagConstraints.WEST;
		constraintsJLabelPointName.ipadx = 8;
		constraintsJLabelPointName.insets = new java.awt.Insets(10, 10, 2, 2);
		add(getJLabelPointName(), constraintsJLabelPointName);

		java.awt.GridBagConstraints constraintsJLabelActualName = new java.awt.GridBagConstraints();
		constraintsJLabelActualName.gridx = 2; constraintsJLabelActualName.gridy = 1;
		constraintsJLabelActualName.fill = java.awt.GridBagConstraints.HORIZONTAL;
		constraintsJLabelActualName.anchor = java.awt.GridBagConstraints.WEST;
		constraintsJLabelActualName.ipadx = 231;
		constraintsJLabelActualName.ipady = 2;
		constraintsJLabelActualName.insets = new java.awt.Insets(10, 2, 2, 56);
		add(getJLabelActualName(), constraintsJLabelActualName);

		java.awt.GridBagConstraints constraintsJSplitPaneTags = new java.awt.GridBagConstraints();
		constraintsJSplitPaneTags.gridx = 1; constraintsJSplitPaneTags.gridy = 2;
		constraintsJSplitPaneTags.gridwidth = 2;
		constraintsJSplitPaneTags.fill = java.awt.GridBagConstraints.BOTH;
		constraintsJSplitPaneTags.anchor = java.awt.GridBagConstraints.WEST;
		constraintsJSplitPaneTags.weightx = 1.0;
		constraintsJSplitPaneTags.weighty = 1.0;
		constraintsJSplitPaneTags.ipadx = 413;
		constraintsJSplitPaneTags.ipady = 154;
		constraintsJSplitPaneTags.insets = new java.awt.Insets(2, 10, 5, 6);
		add(getJSplitPaneTags(), constraintsJSplitPaneTags);
	} catch (java.lang.Throwable ivjExc) {
		handleException(ivjExc);
	}
	// user code begin {2}
	
	getJLabelActualName().setText(
		getEditorData().getDeviceName() + " / " +
		getEditorData().getPointName() );


	//add a editor for the Tag
	javax.swing.JScrollPane jScroll = new javax.swing.JScrollPane();
	jScroll.setVerticalScrollBarPolicy(javax.swing.JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
	jScroll.setHorizontalScrollBarPolicy(javax.swing.JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);			
	jScroll.setViewportView(getJPanelTagWizard());

		
	getJSplitPaneTags().add( jScroll, "bottom");


	//best way to set the divider for now
	getJSplitPaneTags().setDividerLocation(
		(int)(getSize().getHeight() * .75) );


	initTagTableData();
	
	//set our selected table row
	if( getStartingValue() != null && getStartingValue() instanceof Integer )
		getJTableTags().getSelectionModel().setLeadSelectionIndex(
				((Integer)getStartingValue()).intValue() );				
	

	// user code end
}

/**
 * Insert the method's description here.
 * Creation date: (3/10/00 3:25:00 PM)
 * @param newEvent java.util.EventObject
 */
public void JButtonCancelAction_actionPerformed(java.util.EventObject newEvent) 
{	
}


/**
 * Insert the method's description here.
 * Creation date: (3/10/00 3:25:00 PM)
 * @param newEvent java.util.EventObject
 */
public void JButtonSendAction_actionPerformed(java.util.EventObject newEvent) 
{
}

public void messageReceived(MessageEvent e)
{
	if( e.getMessage() instanceof Multi )
	{
		Vector v = ((Multi)e.getMessage()).getVector();
		for( int i = 0; i < v.size(); i++ )
			messageReceived( new MessageEvent( e.getSource(), (Message)v.get(i) ) );
	}


	if( e.getMessage() instanceof TagMsg )
	{
		initTagTableData();
	}

}


public void valueChanged(ListSelectionEvent e) 
{
	if( !e.getValueIsAdjusting() )  // make sure we have the last event in a
	{												// sequence of events.		
		if( e.getSource() == getJTableTags().getSelectionModel() )
		{
			int selRow = getJTableTags().getSelectedRow();
					
			if( selRow >= 0 )
			{
				getJPanelTagWizard().setTag(
					getTagTableModel().getRowAt(selRow) );
			}
			else
				getJPanelTagWizard().setTag( null );
		}
			
	}
	
}
}