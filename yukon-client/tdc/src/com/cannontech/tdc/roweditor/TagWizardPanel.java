package com.cannontech.tdc.roweditor;
/**
 * Insert the type's description here.
 * Creation date: (3/8/00 11:45:00 AM)
 * @author: 
 */
import java.awt.Component;
import java.util.List;

import javax.swing.DefaultListCellRenderer;
import javax.swing.ImageIcon;
import javax.swing.JList;
import javax.swing.JScrollPane;
import javax.swing.UIManager;

import com.cannontech.clientutils.CTILogger;
import com.cannontech.common.util.CtiUtilities;
import com.cannontech.database.cache.DefaultDatabaseCache;
import com.cannontech.database.cache.functions.TagFuncs;
import com.cannontech.database.cache.functions.YukonImageFuncs;
import com.cannontech.database.data.lite.LiteTag;
import com.cannontech.tags.Tag;
import com.cannontech.tdc.TDCMainFrame;
import com.cannontech.tdc.logbox.MessageBoxFrame;

public class TagWizardPanel extends ManualEntryJPanel implements RowEditorDialogListener, java.awt.event.ActionListener {

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
			{ 
				setBackground(list.getSelectionBackground());
				setForeground(list.getSelectionForeground());				
			}
			else
			{
				setForeground(list.getForeground());
				setBackground(list.getBackground());
			}


			if( value instanceof LiteTag )
			{
				LiteTag tag = (LiteTag)value;
				if( tag.getImageID() > CtiUtilities.NONE_ID )
				{
					setIcon( 
						new ImageIcon(java.awt.Toolkit.getDefaultToolkit().createImage(
							YukonImageFuncs.getLiteYukonImage(tag.getImageID()).getImageValue()) ) );
				}
				else
					setIcon( null );
				
				setText( tag.getTagName() +
					" (Level: " + tag.getTagLevel() + 
					(tag.isInhibit() ? ", Inhibits Control" : "") + 
					")" );
				
				//setForeground( Colors.getColor(tag.getColorID()) );		
	
				setEnabled(list.isEnabled());
				setBorder((cellHasFocus) ? UIManager.getBorder("List.focusCellHighlightBorder") : noFocusBorder);
	
				return this;
			}
			else
				return super.getListCellRendererComponent( 
						list, value, index, isSelected, cellHasFocus);
		}
	}
	private javax.swing.JComboBox ivjJComboBoxTag = null;
	private javax.swing.JEditorPane ivjJEditorPaneDesc = null;
	private javax.swing.JLabel ivjJLabelDesc = null;
	private javax.swing.JLabel ivjJLabelTag = null;
	private javax.swing.JScrollPane ivjJScrollPaneDesc = null;
	private javax.swing.JLabel ivjJLabelActualName = null;
	private javax.swing.JLabel ivjJLabelPointName = null;
	private javax.swing.JButton ivjJButtonDelete = null;
	private javax.swing.JButton ivjJButtonUpdate = null;

	private boolean _isEditor = false;

/**
 * TagWizardPanel constructor comment.
 */
private TagWizardPanel() {
	super();
	initialize();
}


/**
 * TagWizardPanel constructor comment.
 */
public TagWizardPanel( EditorDialogData data, boolean isEditor )
{
	super(data, null);
	_isEditor = isEditor;

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
	if (e.getSource() == getJButtonDelete()) 
		connEtoC1();
	if (e.getSource() == getJButtonUpdate()) 
		connEtoC2();
	// user code begin {2}
	// user code end
}


/**
 * connEtoC1:  (JButtonDelete.action. --> TagWizardPanel.jButtonDelete_ActionEvents()V)
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC1() {
	try {
		// user code begin {1}
		// user code end
		this.jButtonDelete_ActionEvents();
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}


/**
 * connEtoC2:  (JButtonUpdate.action. --> TagWizardPanel.jButtonUpdate_ActionEvents()V)
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC2() {
	try {
		// user code begin {1}
		// user code end
		this.jButtonUpdate_ActionEvents();
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
	D0CB838494G88G88GD5DEADB0GGGGGGGGGGGG8CGGGE2F5E9ECE4E5F2A0E4E1F4E14DDB8FD4145715373B563A0D27C9CF4D091BA63BB6512CE9EC0EE952169237EBDB5B0DDB2DB6A759582DA7E1CFE86BE6ED8EBDEBD2B6311B47GAA8A0A11C4D2A10E080A1170A71A06G8A48G838EFFE4C6C09C158F6683C667DF3E798823E232773EFF734DB0836A591E331CF37D3E77FB7F6E3D6F3E7B6EFD5FC872764D4FCCB365911216C9785FC7E90484EA881977785F7D2D40CA2A92330861EFBF
	50AE12D11E814FAA28DB26A5E66512B78A36C259866510A7B1EB8F3C6FA6990566637092E71E54E3047CF478406A2967F9F42F18E78BAD5FDB178D4F9DC0DD0069B8FF954E7F6D3AFC0E4F03728E7E0E14E68219BE46B60BEF649B700C0FD9BB8BBC7B00722A314D2A3A82DE1FG9836D983144C5BEE02A7E3720B55D53C1D732B0B4846FFF11AF2F0AE4378021C552C6F8C7EB413E42A08F4725B56CAF856F6DD99B9B96477168DA9BE1F5F272BC323E53AEB28CC730F2BAE376E572A2B9D4AC82F7B1A22393A94
	1F6A01BEA9145727EFEC2563G2FD01E28C12C6DB3544FF1F8378139EA843F3F4829657C4C3D3208A4BD5899175FE5B50C4F641AB03F4E03C519F3EBF17CD7297E31DFB1141DC00C1713750217353502179FDE433639A79233CA006ACF88FC76F5447B209CG1AFDC270F8623060316F7A77484453F519BB6370B87F84636D2993E19E576CA94AFC075AC8F5A12EF346C918972896E8GE88650FC203D54B6DF597B9EBC9B8675C63F576B77B5848227BCAE2D5D6FD2BD70DEDD8DB59C6BD6077C1A0B90E6FF4F5D
	A80D410F20041311360744E46BA542DE96F7BFCA82162E4C4C9832BD716CC553CABEC6524930ECF9F528379F5035A5700E7D30BF43D7D03C4F0033356A22B816BDD03726CE6839EF0760451671G492D7C02E9CB8CDE667D68D35371FA46032DBE4C8B53F15EBE54F1887039C00B011683A583E5B4889D57959C1BCE472DE06F1ACF71584786830A261F9AF3FBDC2A46F53D29EEAADD47A256EFDB7D2D76E3FAD9F44C38C66B9E1F91D3AF6B9629A6F38353C7D5C3D8AFEC6E4638F377B5CC360D9DF28DBF24FB42
	534056B0DD62F5941FB060ECED7F196EADAC4F013A94A056FE93ED6FB895EDA3DBA6909FBD95EDA3DF39AEECE4826A1200587AFE5B0CFE505288FB87A884F49750922015C0C90DE20D3FDCD0FE137B284B303E2CEFE3615B70D4C33A26347A023A629BD2FB944D2D8CFA54A061EDEC0528574108FEE7687E9E02B9DAD41F2AA93A5B6F0351952F8A4694E4ED7A8BF7E19BCD25358E37D705893C81A2469DAC5C0D7AD6027A5900CB5155F08BD6FF3990ED62D32587C60481603D42027AFA197ADC826F8596212B97
	76A326C3F90EC568655705EF4273B498376A292E363807108DC53BAAECF479E758E7BE34CF3508FD79F21F30BFCB4E37C869331F4734BFFA76B5B1DB9AED0A363F55F45C3BGDF52A44E4C1E4F7A6258A44EFFAB7BC4D02A219F1B439FF70E745B98FF8737C9714E745CFEGEDF58D483581A83BD9685A7C2E195960A9A5288A2B6B5440E0D4D7D8732CED61BEBCD354CFD51FEED1832A4F85AFC13E66457B324276B86C1736729E4551BFB5024184A5DE31EF9743E18C23F50B7AAADA2F683A669E9C53D53B2A6B
	EE5FC838FFBD65A598617A02DC5611E2143598E42C8532C9D9D76C5BED30AB1B5FFFE5AC60409D436D331F1E438350BE54AC563F66BA46D07749F293AD076BD7DD0B37F65104E35C6ABA32B9FEF353F35C0E3D30B9CC93A83B70138C4B21D882701DA4651DFB3D4F506EF6ECF3FADE3EC567C80F32637FABE23630549C5F16FFEC0EED4BAB4C31EDF935F9EADBFD4E9C4BD6D95D73E61451BF6EB3587E0BE674FFC3FE1FCF259BD0F6A1AC76FD5901F238FDEE3D576D9AD1F53B7B1A8ABE3685EDB8137A302D70DE
	50A2FC637881619B0B2FBDCC16DD6AB7450A1F66AD3CEA52DB180D4EEE897BC646D372FE5CD32AC77502229A7DE3BE1D61AFD17F4D71E0BA456AF736E15F1236D3E33A6E77D920D6D7CB95AACB19797D77CF1AA30372524B4606179AE843A6DC1AE049111EA3954C96F6131F6F0898179DBEB7B1AEEBB8E95CCA619B6FBD0832AD82191782BD53A27C45F091568BFF411DE2235F63E14B82F5B6AB6ABC6D73BC039EB734305C2A8A6AD87D2E1AD875F32DE29E7B01DC348F7513B1950EF1F4C827552B62ACBE70E6
	D4DD0F62998B1F710387364353E370E58C779647DE2FE0B17A2FABF27C71624D844FEAB6A6332FA58A0E99505CDEF07D681CF98E78C93DB187DC847CE33E389E1E1B00589A553D033240BAB47270012E441CD69A0FCD087CB08F4A25C036D6B646C0AB33B756AED536DB8B6594C33B5CB6562E368D5B95FCA67226F2099D2358C178E7E10608CD5BF30D79E64905587983528CBE7E5C366818FD75A19C778940173709F57BFD3171BCE988867DC3EEAAB3DF0379870DFB03E18B8FEE036730DB8BC27C6ED2C3E29D
	9FB9083CE2CC45E13E960F9CBDC1A64708CDB4399547F563B8B5EA073F9142BAAB18C223F49DBDEEF51C081C376AF319E736895FF02359185BAE5DB9E25A9EC7B7392719CEAACE07F5B38AF9A39B5BDAA3468EC0BD63FDEBBBDEE15CC40E5E2E784694CF13CF572E36D1FBE1E37E0A1E47F8A6ACBAAD72DF47D9346BA4A82782655AE09F51D8FCF4C24462A5008D58445E7B1FF70DFBEF1E0D1D6D4BEC42DED7CF60785C07F01DEF99A9046714CE09885BFAE0F7BD9FFB65238F105A4F2D26D8F9776C1D5D26E7EC
	7C9E44E6483B1FA9C85F74FDF1BF307F0610415A8EB9F73B10A171FCAC99925A051E6F709B780D6D6CA43F3D9F98774232246118E78115271D67526D51FB617192DC17CD0067F6083DF06850AE43DE003C9FD2129170C6423E1FB9144E7C8EAF2FBD14A97C8E91677E4A6ADB49BD0BBBC48E11F28475D385E5871087E8A65082495B2543536652CD21A9F269761ADB61EBF907081512E4BE3B360369F0C3C7B886A41D1363C116EFA6D39B4F6BE47966AE093FC271FA834E6C261F62D876C0DDEA27185FB7914D77
	4C2E48B967C715D93F09C97D4A22E50C6A476CF0279D57036E774B4A27CA28ACB88EF1D0F5B5BF2F0970C32F5318816DF1DD17D84366236873734EA4E6D5GD901DCC0330196GBDF5C62C652307E3794348350C678C43B986137370247B281B534F07137AC56BE77D1948FECC6E5F1F97F213EEA1772CD2442A20EC6D96B2EEAF49B16C25AE3F5B274BFD14D0026735DFF30F38FD827BC70A8D79B59AC432DC6645EA9C378F4654653829C762473CEF9C099D73E63E97BB665D7E5E54795B6E776245440CBFD70D
	60EFB690437EC07D7DFDD0CE10587CEE44D6C2B9C9E27B9D08E558A146358BDBFF692C997786147B0082C0330196GAD83DA8714AA5B3ECE5B66B992330E83B900F420F9C0898E96EFE0EC425A8E1C1D414795EB394EBEF95FAE0B01CDC5291CBFFCDF40790DEE53E70F3EA778EB4A0BB16F5A99E36C1CDB60E387E1FEA30B3FEF67F3C537B166E105140795EF6F246B9F8C2AC13A61AC0A2E703A5F6AEF119879CB0762D509086597CEE471B6CD3A3F31D24AC80A227756DAD8138D8E268BF216BD734E06F5436C
	2D6BE8FC5B6EBD9A5B36AF9CC5DD857DE35A109AED3F2E237932AE6CC1DAF5550BE270981B7657958DF230687EBA6D4F6A62753FF1B45E9EE1BA3AF84328238F240EDEBD9F2D239250C7AD57C997FF3AA669680ED2BCB7C232FCA7ADDFA542FF2B8DBBA44FCDBE97F7BCAC2EFE0D7A13FBA0267EEFA2306BF48F6305C82764F9C7836AA42029432A865992CBCD491233587EF083368F417B4C33A27E7ADEB77AAE0FF255BF86F3D9FB30DD295D18E7EC04368500D774B0F9982F0F0D232D8EEB0A97D4E68F28C324
	381746388E5177AEE83FGE8B9D0B2D0C62F187363A654EB1BCD99D4BD1029C85EB715EF1338C58D8E897C0D901E2FEDF0628C7ACF79C35005615F9FC01BEB338FE91093E25CCABB313A593BF64A3128A22975333A7B87F3E59D44E9907AE2A52B9B8B217EA2B2DC495FE3E78DF5ACAEA5A287BD56E30C91BD3DACC63C2BD7441057AC316A97487A659EE3FD822FCF02A74B7D462AA25604571B7B40077609B1EA1D316630497ABF472C9F6893FEFDCF913D8B0672EC098DD06CBEA8AF1658CF0A047FCF10D84BB5
	FA6FF1AEB12B781C40F21C08D5C059A2319E0A75C1F9D4E2CFBA45F83A44E63E0B58F2A8A781EDB8075FD75B6A08DC0B3B3B4D70742963E68CF54A14F13D4C6206BC8FAF3CCB0ED7FED4EA2E7C7347C7D84CBDFF5C98F3D7ED73440D39AB7A79F7D57E1C3377CB7CFBE765G46C7B23642892B2BDBB4376B14B2E2233B0745985DF6F10FB751AFF22699F6345FA7203C92A839DF64FE21C3A277DB7C508344B6E3C64C6F1AC90E9153A64E0F79FCB897C816395FC50718538975F34FA3B647E1DCEB54595752D81C
	446AFF90D10FFE70A3E22CCF0928C7FE6FCCB756971DB556230ECE707ECC361F9D9432ED7A599262790EAF6EB75BF5DCA66264323DB4ABAD7F97C519CC6F7FB51CCFE24634F477B2794D3DA2CF2DF20AEF3A0FD98496F00ABCED4433B776D84233087858A3F269C5613ECD05317B063D8F91F1666EEA51BF49A9EC608F67508672AF00FD83D9AF88BE03CE416700443C921BF7C1784CB23A673CBE9502EE77907367CD94DB8134C6369BAF427BA59D8EDB36E77E2A01AE47C5EC3F2E4892C37F4698E78B671B728E
	E3A29D606B36718233DBF1778C6763B5DCD3163BE3BA2078DCD0E673AEBC955E2B454E493C57D06CBF012C97856FDEE7A46F4FB606F9FF5AB99D6F19D3705EF71165ECE15E3F7EEEFC5ED3F1AAEE472B4F9BFDC76E3DBDA6B1CEB48F2E0B4CFE83972373F5D3B34A3AG70E597C5AE708FF5C7615923DC1EFC2384E017668F281AFE1521744611479E3D76DB7DBE4CF2570D5487A599744070BFD2FC0381E7B96DAC3B782DC4AA54B9AF09FC69D88B6E47E5D0DE873491A8FBG62F5A06B0010AD252E02CC13A3BA
	7C819B840B9EC36A42649BF25C5E776F9FEE917E660944EF1204D9E5F16F511C83ECFDA678B3E17D2CB4FCB279FEEAC57996C35DD3C09B00B214442CFC208AC548D7C56F95B0530565E950F5E5E8145EB08A617052AF8CE3F044E4BB58EB144D2230797B785323CC4EC1D83F6FDA0C7DE670F6F379F3D15CFE44BB15AE17466D17BF65BD501A987D18ADDD69C71BC9D622FF87B0C6710DCA746FG7EF8CE7C8EA0E3906201C11173051C4679D78E72FBB07E5CB8980FEFAD225F56A1562E00BFAB06A677E33E40D9
	044F432E43E49FADC3DF927EE3BEE181FE7A6036766F5942568A9EF910D81EABB389BF93BD3E054FB9F7A86C933AEA8EE432F93B1DF45E3143FC8E2B0DC8BBFC053EB3FB2BEDBAC862788A3036B6B80EBA87AFC30243E2E956E7CBB34EA7F39B66C2F85D0EE6BC3B717B15DB71382F51ABF3BBB4740DB0BD3EE6A7D227690E707B9C7B4DFE873D9DE2F3AC7C0B4E41755992BFD7EDEC091D2B3636443E0769EC197A5BE3EFCB7CEF0F97DB0C5F9E79D95101EB33926CE5BDD0368BE2FC2063AE61FBB63494916AFB
	E2781DA65788664A78EB94DDFE9B35461775DF2D537F066585AB66E403FEC8F93CB83E40FFED4575527D818A72EF11D65411669E9955D9D30E27D8F1BFF954E18A33783E1646ECDDA05700CB447C99811AFFC0F926445C81119B5CA531D59651B7C9E2879AC55F0D921B59A87A92D566B8DE01F5C92C3CDAE073D56103BE6947D8A4C5162F53F27630A827775F0E1D62F8B73F978AEEEB0E1B7DDDG137DCE9A071502DC3543C29F97285E3A20BCA0319FC9DDFAA476B7346FD2A82F14D8FE93E2C9D0CE11580DA6
	51F7533008A386BAB148320D005F871A8334GA881E88DD0B2D0AAD076A86413C035C0DDC0AE209050EC207B00160C0AFD719FEDC7C87CF80AA7EAF4F3E80A71BEF7DBC77C7D312BE37A7D71278E7A0D916C5AF1B5406F1576F550BB62616120AA77E1F1C72E44DA03DD7E209B3DAEBB876C3DE8DB43FE1F4EE2E3B75D9B4BC136B523A2B65E9B080C0DEB4F907C2331710C40D43131789DC73C58183893335E626FEC7ED7697E4A86BC5FADF26F316E70FCDFEF1CEEBEE3BC9EBD1F95463CDF4CCD47AB73CEB5DE
	E6947F8EF7F8BC44C721ECCFB30E57DCBD9DFFD9A41C6B6850FF361B0D07657B607D843CBCC8C4FC303C125EA5B1131277CC6CCEE5F13FB18FDE64E6F7594F38453ABD640CDC5B25F44F2C077A0D320D5E983936736CE1DD7FF2DADD9BA9DA5745179333C45E4D661A9F454FF7599D8D342B3DAC78A9EF0A64277EEC181F39CD532DD5BCFE68EFF9E10E8BE96C1D5F53F6465FFF57BB275FFFB3BBE31FCFDF6E1CFCBE5D59997BFC3A3B53F8BEB16C5ECE64F1847CC61FEC777792BBA73185F4CC44FA5DAEFDD462
	5F24FCA1EED5F1C201AF21FC0DD32C0FE35FB1E067B8165809BE84C49E75EB226FBFF55235F3FBBC7CF7D2340D5B87AEC56A63A94A23BB209878DEA5B1641B2DE9923EF21BC875E3CC91F117568C86D2D264027850BFD2D4EA2EA8B5D7FEF0C474CDBFCF64B8CBAF05475953F8EB633CB000FDA7475CA9F52C7E70E03C7A78FD5A060834434A6CB8F7B6611DCC441D4D0B7D624EA6F4D9783C075430ECE7EED1369543E19DBD698E0F135D34BF76B88DE0589E9826128F437BBEFF997B32F8E0FD7FD4F959124B13
	E38BF67E3E51AF4E5F6550065D533FFDDE5CA72F87EC93D041957081C04EAB629E2A16FEAF253F2F3EA26E95757351770A7790633D60C367236F9553A26A571E0F3ED73CA322FE1BB37ADED174E7BAE808787672847075541529E3AA2603BEAA4BBAE8FB0744AED0ECA3E077C81BD18EE593199B51AD4D70827ADD0063E2CB9376FF050E175640C6DC7CBB129954481B136F239B3E724B6796DF326419082915EC9E251DDBC966B80E9E421FF5BF8CD2FEC3A71B59B7851D64C4FCE3008A76A1A2DC414AB05BCEB9
	DBABF583C91F9E2BCEFF7141F49349002918DBECA599685BE0C60D4D1C8EEE114ECBC3DA3E8CE941CF48E7849D0A4900266BA483524AEA1DE463212739839836C0F1B37D2882AF3916261ECB9D8D6DCD65DD8E7A842EF2A2EC7BA75F7870B7AB1F6F8926134D2D649DBCFA6C2AC6B3CF5505DFAD794F541BC9D6G4744CFD640F210BEB2037AD50FEAA3F9435D78933C202E3A9AC42E955449DBB61C29FCCCF7FB4A2DCAF0D447332146C6B2FC7803FA72A62B76A83E11F2166056D039539A6A2953207FA585785D
	CD7945D14A64A83B1F5CF9F0FFDBF2C2BAB1B513ED9E3F62EAD60640EB3412ACFDD4D30323FE0FAB9428E383ED9DC7CB429B27F498EE2F949F05ADF34089A6193E923AFFCE4E7A1CE6D0F2C8A36534A51E34E532052BF1D451E0AAD5EB5571BF887835E2D97705F775C32BDA76B8CDA40D8B090B36B394008599D63CEE4FD534A26A1168F26E5601DBCD7185077D1A97B8B3C7F1A62A6484E5DFDBD1F177432F75538942DDF5320F29923A160F28FEEFF94370305B274A81C02FB0FC219C9E5B14C535F9B3745537
	5A1EFDE3906C40C2523D9595745FCA7A6F8778EFA5C5AAA9D27981A8B705CC7997DD9F92B2D363113B83173F2C99D6D07F375F2DF860D735A99BC03AE612C9FF7C840B109BEE55B1669DD435D90B6FAE5CBEB7698EDC23G1ACA88475D9312BB84778D9E6F783E0D22B9132C8A51DD134D83613764A2E1A91FB67D937D377016C546DE7BE85F7D125343E1CE53B8272B00F2A40AFF691C23AD925D4A5B623EB77D7F132AA294612136F199E26BBFAD8E2799111C8A03594C02AFB6A376794B0870D12488E2893671
	FB45A31F9B3FB524FE31BD6EF74A0CA0BB737203515F9ADEAFC7D19D008702A2B7BEDD7EBE09FFFFE877AA9E0F2EF82EB478DCF817F86A2A2E066F2FC8850E9F94EDF8A53FE32CC03EAF9B7A7CAFD0CB87885CCE14798496GG18BBGGD0CB818294G94G88G88GD5DEADB05CCE14798496GG18BBGG8CGGGGGGGGGGGGGGGGGE2F5E9ECE4E5F2A0E4E1F4E1D0CB8586GGGG81G81GBAGGGBE96GGGG
**end of data**/
}


/**
 * Return the JButtonDelete property value.
 * @return javax.swing.JButton
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JButton getJButtonDelete() {
	if (ivjJButtonDelete == null) {
		try {
			ivjJButtonDelete = new javax.swing.JButton();
			ivjJButtonDelete.setName("JButtonDelete");
			ivjJButtonDelete.setMnemonic('d');
			ivjJButtonDelete.setText("Delete");
			ivjJButtonDelete.setActionCommand("JButtonDelete");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJButtonDelete;
}


/**
 * Return the JButtonUpdate property value.
 * @return javax.swing.JButton
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JButton getJButtonUpdate() {
	if (ivjJButtonUpdate == null) {
		try {
			ivjJButtonUpdate = new javax.swing.JButton();
			ivjJButtonUpdate.setName("JButtonUpdate");
			ivjJButtonUpdate.setMnemonic('u');
			ivjJButtonUpdate.setText("Update");
			ivjJButtonUpdate.setActionCommand("JButtonUpdate");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJButtonUpdate;
}


/**
 * Return the JComboBoxTag property value.
 * @return javax.swing.JComboBox
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JComboBox getJComboBoxTag() {
	if (ivjJComboBoxTag == null) {
		try {
			ivjJComboBoxTag = new javax.swing.JComboBox();
			ivjJComboBoxTag.setName("JComboBoxTag");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJComboBoxTag;
}


/**
 * Return the JEditorPaneDesc property value.
 * @return javax.swing.JEditorPane
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JEditorPane getJEditorPaneDesc() {
	if (ivjJEditorPaneDesc == null) {
		try {
			ivjJEditorPaneDesc = new javax.swing.JEditorPane();
			ivjJEditorPaneDesc.setName("JEditorPaneDesc");
			ivjJEditorPaneDesc.setBounds(0, 0, 69, 75);
			// user code begin {1}
			
			ivjJEditorPaneDesc.setContentType("text/plain");
			
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJEditorPaneDesc;
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
 * Return the JLabelValue property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getJLabelDesc() {
	if (ivjJLabelDesc == null) {
		try {
			ivjJLabelDesc = new javax.swing.JLabel();
			ivjJLabelDesc.setName("JLabelDesc");
			ivjJLabelDesc.setFont(new java.awt.Font("dialog", 0, 12));
			ivjJLabelDesc.setText("Description:");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJLabelDesc;
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
 * Return the JLabelPtName property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getJLabelTag() {
	if (ivjJLabelTag == null) {
		try {
			ivjJLabelTag = new javax.swing.JLabel();
			ivjJLabelTag.setName("JLabelTag");
			ivjJLabelTag.setFont(new java.awt.Font("dialog", 0, 12));
			ivjJLabelTag.setText("Tag:");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJLabelTag;
}


/**
 * Return the JScrollPaneDesc property value.
 * @return javax.swing.JScrollPane
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JScrollPane getJScrollPaneDesc() {
	if (ivjJScrollPaneDesc == null) {
		try {
			ivjJScrollPaneDesc = new javax.swing.JScrollPane();
			ivjJScrollPaneDesc.setName("JScrollPaneDesc");
			getJScrollPaneDesc().setViewportView(getJEditorPaneDesc());
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJScrollPaneDesc;
}


/**
 * Insert the method's description here.
 * Creation date: (11/14/2001 2:39:29 PM)
 * @return java.lang.String
 */
public String getPanelTitle()
{
	return "Tag Creation";
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


/**
 * Initializes connections
 * @exception java.lang.Exception The exception description.
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void initConnections() throws java.lang.Exception {
	// user code begin {1}
	// user code end
	getJButtonDelete().addActionListener(this);
	getJButtonUpdate().addActionListener(this);
}


/**
 * Initialize the class.
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void initialize() {
	try {
		// user code begin {1}
		// user code end
		setName("TagCreationPanel");
		setPreferredSize(new java.awt.Dimension(417, 169));
		setLayout(new java.awt.GridBagLayout());
		setSize(455, 201);

		java.awt.GridBagConstraints constraintsJLabelTag = new java.awt.GridBagConstraints();
		constraintsJLabelTag.gridx = 1; constraintsJLabelTag.gridy = 2;
		constraintsJLabelTag.anchor = java.awt.GridBagConstraints.WEST;
		constraintsJLabelTag.ipadx = 10;
		constraintsJLabelTag.insets = new java.awt.Insets(10, 10, 4, 6);
		add(getJLabelTag(), constraintsJLabelTag);

		java.awt.GridBagConstraints constraintsJLabelDesc = new java.awt.GridBagConstraints();
		constraintsJLabelDesc.gridx = 1; constraintsJLabelDesc.gridy = 3;
		constraintsJLabelDesc.gridwidth = 2;
		constraintsJLabelDesc.anchor = java.awt.GridBagConstraints.WEST;
		constraintsJLabelDesc.ipadx = 78;
		constraintsJLabelDesc.insets = new java.awt.Insets(13, 10, 1, 205);
		add(getJLabelDesc(), constraintsJLabelDesc);

		java.awt.GridBagConstraints constraintsJComboBoxTag = new java.awt.GridBagConstraints();
		constraintsJComboBoxTag.gridx = 2; constraintsJComboBoxTag.gridy = 2;
		constraintsJComboBoxTag.fill = java.awt.GridBagConstraints.HORIZONTAL;
		constraintsJComboBoxTag.anchor = java.awt.GridBagConstraints.WEST;
		constraintsJComboBoxTag.weightx = 1.0;
		constraintsJComboBoxTag.ipadx = 169;
		constraintsJComboBoxTag.insets = new java.awt.Insets(7, 3, 0, 11);
		add(getJComboBoxTag(), constraintsJComboBoxTag);

		java.awt.GridBagConstraints constraintsJScrollPaneDesc = new java.awt.GridBagConstraints();
		constraintsJScrollPaneDesc.gridx = 1; constraintsJScrollPaneDesc.gridy = 4;
		constraintsJScrollPaneDesc.gridwidth = 3;
		constraintsJScrollPaneDesc.fill = java.awt.GridBagConstraints.BOTH;
		constraintsJScrollPaneDesc.anchor = java.awt.GridBagConstraints.WEST;
		constraintsJScrollPaneDesc.weightx = 1.0;
		constraintsJScrollPaneDesc.weighty = 1.0;
		constraintsJScrollPaneDesc.ipadx = 414;
		constraintsJScrollPaneDesc.ipady = 81;
		constraintsJScrollPaneDesc.insets = new java.awt.Insets(2, 10, 8, 9);
		add(getJScrollPaneDesc(), constraintsJScrollPaneDesc);

		java.awt.GridBagConstraints constraintsJLabelPointName = new java.awt.GridBagConstraints();
		constraintsJLabelPointName.gridx = 1; constraintsJLabelPointName.gridy = 1;
		constraintsJLabelPointName.anchor = java.awt.GridBagConstraints.WEST;
		constraintsJLabelPointName.ipadx = 6;
		constraintsJLabelPointName.insets = new java.awt.Insets(10, 10, 2, 3);
		add(getJLabelPointName(), constraintsJLabelPointName);

		java.awt.GridBagConstraints constraintsJLabelActualName = new java.awt.GridBagConstraints();
		constraintsJLabelActualName.gridx = 2; constraintsJLabelActualName.gridy = 1;
		constraintsJLabelActualName.fill = java.awt.GridBagConstraints.HORIZONTAL;
		constraintsJLabelActualName.anchor = java.awt.GridBagConstraints.WEST;
		constraintsJLabelActualName.ipadx = 174;
		constraintsJLabelActualName.ipady = 2;
		constraintsJLabelActualName.insets = new java.awt.Insets(10, 3, 2, 11);
		add(getJLabelActualName(), constraintsJLabelActualName);

		java.awt.GridBagConstraints constraintsJButtonUpdate = new java.awt.GridBagConstraints();
		constraintsJButtonUpdate.gridx = 3; constraintsJButtonUpdate.gridy = 2;
		constraintsJButtonUpdate.anchor = java.awt.GridBagConstraints.WEST;
		constraintsJButtonUpdate.insets = new java.awt.Insets(3, 12, 2, 9);
		add(getJButtonUpdate(), constraintsJButtonUpdate);

		java.awt.GridBagConstraints constraintsJButtonDelete = new java.awt.GridBagConstraints();
		constraintsJButtonDelete.gridx = 3; constraintsJButtonDelete.gridy = 3;
		constraintsJButtonDelete.anchor = java.awt.GridBagConstraints.WEST;
		constraintsJButtonDelete.ipadx = 4;
		constraintsJButtonDelete.insets = new java.awt.Insets(1, 12, 4, 9);
		add(getJButtonDelete(), constraintsJButtonDelete);
		initConnections();
	} catch (java.lang.Throwable ivjExc) {
		handleException(ivjExc);
	}
	// user code begin {2}

	
	getJComboBoxTag().setRenderer( new LiteTagRenderer() );

	
	DefaultDatabaseCache cache = DefaultDatabaseCache.getInstance();
	synchronized( cache )
	{
		List l = cache.getAllTags();
		for( int i = 0; i < l.size(); i++ )
		{
			LiteTag tag = (LiteTag)l.get(i);
			getJComboBoxTag().addItem( tag );
		}
	}

	
	getJLabelActualName().setText(
		getEditorData().getDeviceName() + " / " +
		getEditorData().getPointName() );
	
	
	
	getJLabelPointName().setVisible( !_isEditor );
	getJLabelActualName().setVisible( !_isEditor );
	
	getJButtonDelete().setVisible( _isEditor );
	getJButtonUpdate().setVisible( _isEditor );	

	// user code end
}


/**
 * Insert the method's description here.
 * Creation date: (3/10/00 3:25:00 PM)
 * @param newEvent java.util.EventObject
 */
public void JButtonCancelAction_actionPerformed(java.util.EventObject newEvent) 
{}


/**
 * Comment
 */
public void jButtonDelete_ActionEvents() 
{
	Tag theTag = (Tag)getClientProperty( "the_cti_tag" );

	if( theTag != null )
	{
		try
		{
			SendData.getInstance().getTagMgr().removeTag(
				theTag,
				CtiUtilities.getUserName() );
		}
		catch( Exception e )
		{
			handleException( e );
		}

	}

}


/**
 * Insert the method's description here.
 * Creation date: (3/10/00 3:25:00 PM)
 * @param newEvent java.util.EventObject
 */
public void JButtonSendAction_actionPerformed(java.util.EventObject newEvent) 
{
	LiteTag tag = (LiteTag)getJComboBoxTag().getSelectedItem();
	
	if( tag != null )
	{
		try
		{
			SendData.getInstance().getTagMgr().createTag(
				getEditorData().getPointID(),
				tag.getTagID(),
				CtiUtilities.getUserName(),
				getJEditorPaneDesc().getText(),
				"", //refStr
				"");//forStr
		}
		catch( Exception e )
		{
			handleException( e );
		}

	}
		
}


/**
 * Comment
 */
public void jButtonUpdate_ActionEvents() 
{
	Tag theTag = (Tag)getClientProperty( "the_cti_tag" );

	if( theTag != null )
	{
		try
		{
			LiteTag liteTag = (LiteTag)getJComboBoxTag().getSelectedItem();
			
			theTag.setTagID( liteTag.getTagID() );
			theTag.setDescriptionStr( getJEditorPaneDesc().getText() );
			
			SendData.getInstance().getTagMgr().updateTag(
				theTag,
				CtiUtilities.getUserName() );
		}
		catch( Exception e )
		{
			handleException( e );
		}

	}

}


/**
 * Only for apps that want to use this as a Tag Editor
 * @param theTag
 */
public void setTag( Tag theTag )
{
	//can be null or an actual Tag
	putClientProperty( "the_cti_tag", theTag );


	Component[] comps = getComponents();
	for( int i = 0; i < comps.length; i++ )
	{
		comps[i].setEnabled( theTag != null );
		if( comps[i] instanceof JScrollPane )  //for our description
			((JScrollPane)comps[i]).getViewport().getView().setEnabled( theTag != null );
	}

	if( theTag != null )
	{
		getJComboBoxTag().setSelectedItem( 
				TagFuncs.getLiteTag(theTag.getTagID()) );
	
		getJEditorPaneDesc().setText( theTag.getDescriptionStr() );		
	}
	else
	{
		getJEditorPaneDesc().setText("");		
	}


}

/**
 * Constructor
 * @param data Symbol
 * @param currentValue Symbol
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
public TagWizardPanel(com.cannontech.tdc.roweditor.EditorDialogData data, java.lang.Object currentValue) {
	super(data, currentValue);
	initialize();
}


/**
 * main entrypoint - starts the part when it is run as an application
 * @param args java.lang.String[]
 */
public static void main(java.lang.String[] args) {
	try {
		javax.swing.JFrame frame = new javax.swing.JFrame();
		TagWizardPanel aTagWizardPanel;
		aTagWizardPanel = new TagWizardPanel(new com.cannontech.tdc.roweditor.EditorDialogData(), false);
		frame.setContentPane(aTagWizardPanel);
		frame.setSize(aTagWizardPanel.getSize());
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
		System.err.println("Exception occurred in main() of com.cannontech.tdc.roweditor.ManualEntryJPanel");
		exception.printStackTrace(System.out);
	}
}
}