package com.cannontech.dbeditor.editor.device.customercontact;
/**
 * This type was created in VisualAge.
 */
import java.awt.Dimension;
import java.util.List;

import com.cannontech.database.cache.DefaultDatabaseCache;
import com.cannontech.database.data.customer.CustomerContact;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.database.db.user.YukonUser;
/*FIXFIX
import com.cannontech.database.db.customer.CustomerLogin;
*/

public class YukonUserLoginPanel extends com.cannontech.common.gui.util.DataInputPanel
{
	private javax.swing.JPanel ivjJPanelLoginPanel = null;
	private javax.swing.JLabel ivjJLabelUser = null;
	private javax.swing.JComboBox ivjJComboBoxUser = null;

/**
 * Constructor
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
public YukonUserLoginPanel() {
	super();
	initialize();
}


/**
 * connEtoC12:  (JComboBoxEnergyExchange.action.actionPerformed(java.awt.event.ActionEvent) --> CustomerContactLoginPanel.fireInputUpdate()V)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC12(java.awt.event.ActionEvent arg1) {
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
 * connEtoC2:  (JTextFieldLastName.caret.caretUpdate(javax.swing.event.CaretEvent) --> CustomerContactBasePanel.fireInputUpdate()V)
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
 * connEtoC3:  (JTextFieldPhone1.caret.caretUpdate(javax.swing.event.CaretEvent) --> CustomerContactBasePanel.fireInputUpdate()V)
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
 * connEtoC8:  (JComboBoxReadmeterRights.action.actionPerformed(java.awt.event.ActionEvent) --> CustomerContactLoginPanel.fireInputUpdate()V)
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
 * connEtoC9:  (JComboBoxCurtailmentRights.action.actionPerformed(java.awt.event.ActionEvent) --> CustomerContactLoginPanel.fireInputUpdate()V)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC9(java.awt.event.ActionEvent arg1) {
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
	D0CB838494G88G88GB3EB09ADGGGGGGGGGGGG8CGGGE2F5E9ECE4E5F2A0E4E1F4E13DBA8BF09C45153EBBC8F1072F8E8AF1E5B8D1E540D03AC288D3A76A1C2094BE4ADC0943F7F1F210B84161140B1338AAB66582D3B1F78AEE6DD7FF2DA559127C15ED493692E975A588598E5687A119B5D6ECE18BF0EC0DF6C75A11E6E716591135423A645E6B1E1E9D2DF7AD6C60286A293BDFFF5E37DF3F1EDED239FF29B34FD6C9C81E1398FF6F6491125FCC4863BF59390C63B63C75381BD87E8EGEC
	A70FFC6E00F2AB74D5B7E538BDE4401F8F6D62ED0F3B739BB25CD5D02FA0277D1B9B201204CEB674951E9E787D75697C330D53F153764F0345D01682F800D68740A2G8F31CB5586FE845A25C69DA14FC6080FA00F770DD6DB702B0CF13550578130190E59A76DB17AD3200DE35221DC89003835D096D06EEE6F6AB446DD3CFBA95972EFC7ECA5C84B32BE074EBCB67722D15612A728A26C24694899A80F8FCDCD7C562F063CFEC1D1D4C5977DC1EFE0CC8CC83A2AF90DA2A0DE117C2257BF9D5155102879E11860
	573B3ABE191ED2150FA32266D3A7A4E5C8D0C4192C4EC3B91E994D27B4C08EE80F5890770E003A73C07DA840C99B17655BA547394CD3CBC94E433B124A12EAE3B22C3445E4991E3B628A5BF17D8F24E2E39E01F6A7G63656FA44ECB261D73327FB20E5D88ED07A3433D474171BF25788EE87BG828E4E6309A31C47112BCF101C551BECE5C9F81CF3B05E16B9E2BCE6ABD7DCB554FFFA9D6883B95067F0E6382B810E83DC84188358CD7D76AD5B2F206C9E53FB55D0C8D53A43619EB9201DD683228C752EAE68B1
	F0E7C53F2A8588E13E39EA34B181BF9CD21C996EF89C136D82613E14FE76D132666A2BB6E78259B2DFEA302599EBE4B9E332EDF0225E9E2336DD8775BCE3AE43E7D2FC23AB06E736BAA1F0DBF5C2DF2A0B6BF924147362F35CCFBC2DBE1BBB81AFCB1E6D3691B763215A9D630569F82F8BF55C8778D100A840DDG4B815638390E876C6D8B69F8907602268872396931302069BD53129C90B5C35727256B69BA9130F91F5D70BC261734F62B0D567D5307897532AE2D4536515007A3BF2697C6BBA5A96D3A7CEBFD
	23543461BDF4CFF466B39B5ACD7C8394BFE041B35B56D3BC36C720AF8B000D7FF49471358571BE6227784602F89F59F859749168CB85E076FD5A0DB11294423E8568G708148GF7G2496F29B5FE3EF7E127BE848E2DFB657675889259855B5E1D009680262974F8B1AA40C49E20498E3BEF620DE47664D7B146EEFBF5078C0D4C4CD50A5956222A604C4F026889B63F7EC47B11AC8FB0725108884C2E142571DF2D4C0A98B917D63F0C050455888569FF120CFDC914E430A30G54B796223EDE26B1B78B6AEB8B
	392EB664A32E25A843BDD04475722A239C4A8F4139C5392B2BCF72A39B02B64BFD74C90118FE776A88773B3E12C7C863CB27927A9DBDDF0A188F69C571FE778D3A9E2B778AA3B7687FB73E47AE5ED29ACC2F678B51B7D3C15695GEBCD5DB6B8EB194F75889111FB59998D9CC48C44B44D469EF66199A2DE91953DCF8C0BCAGAA914346CD2E02187F0D2B26EF34D3BC4623DEF0100809FF57351D61E18D2BB7737EEE5AAF683AA60DCD6B62B9D157A5E5A2B6FF10729219976AA2062CA3C5A86BE610B13F984E2C
	E2AE6BF7DC9596BF72296A54F4F898F708610F0F51F33791467781B05B5CF79973292059FE1836E37D3F3EEC359D0E7B6A7D23EF9E8D7BAD714151D1E256A5CB7D354B375E7F0BC6EFA58DE67B675C49FDF735BB316FFE471D58F7573A2F6F1BB96EC43E497AFE6646DCC51DD1AC3E3E510D718D12D4C5249B4E1CC2D85E7B6AB04AA1A912FEC18ACC087AB9698B1110954568332F5198FA97541795731CE466900F01F5DFBCC8D6DE2ECF189FADF9244F16EB782B27A4968399CF1B72F18F09329882C5752A530A
	4E702F5378EC60A3EC3F5DD90CE3DBE00DBE000BA5FC3F2985584F771B91D4FAD5D9E6E21A72DFAC74203E444F27C5B836F0C3FEA84E72332730BC2E6F3CA0CF474E342942A2A8E54BDEE678CBC509798F94617E1DB2578F9639881ECDECCD266F65822E9956249004BE8CCE46FD68D949FA9F52C1CE3CBBE5C2198DE0276B6D3A0AB2779A970FDEF671305CB6A017AE41358A841E63B71B38DA0A7BCFE06218031FE727BD56FBD36D53D76D09ECC9EDDD4AEC58D99A1FDF2EAC41F5C7G9FAC6536796FA2EBAC6C
	0EC4D43FC40FF7C3FFF797DB6D4CF0FFDFDC88653824C5A0578C08D1EE0354E264954FFF83ED68713EAE4CD9935D577CB44171CECCCB5EE9DD42C3DD50E16F0527F56B1D2CF1B25F0891F3257C3E73CC886D1A896D55G6BCB394FDFE8336AE9E5CEEBC21FC748B57463A90B57132B0C46FF401FAC63FA2AAA59EE51935CDFA0351AGAFB363CA091D791351FE3E44497D09707856BA09F96CF0D96CEC88DA6AF3E5571E99AC4FECD7D0563B4A62735893943F34ACBE0FFDAE4473586550D76961B9D5AE15AB8A6D
	BBBCDC2ECB25A5963906D4C951CD190A4AF0CF281A84DE4BF19E0A035B9B8DDC6C3C9C2076D982EBAE87E038A7A8AE8B5AEBCC5CE299F1DB20DD3C1D1F1B6FAA388FDB4CF6AFED7B4C768B212F7ACCC0DA734F1D75DFB98D6B594C741667C19BC54D361336E70D31956472A4469ADDD565E1A9BC8CE79089831F04BC05693CB166DFA7DDF4FF4D2A53E0245AF254698FA7F9AC4A04712B8136G6417335C056D3FA7E7F06DF1C86661F8BA97967DA4174EFDD866F303501E83C8854882D8D74E635813A24664D3F4
	BB4642970B033AE20D03FD950CE6904A398A3E9F3F7519F5BFFA6EBFE063F725F81D25D4B03BAC2D086DC7664F4EA9FE1FFA917A0AAB39DC2D23F81F22F1E5D238A2F805993D2B6B83CD8A748893BE2AA596BF9E1B4AB76677D5727871FD3A669C34D3G52ABB93F51437CFBC97A2377935F228F93FE534999EE332D2AE4FCEE280C713BCE3962E2B41F17B94D3A9DF0966EC05CAF644AB89F793A0D90AF6A0676B7042C7D98EF7693EB7FE7737A115FEE3B357FEF6775230E3A0D79CC36976A39EC39AF24917929
	F713FE2FDA36034914ED14AD430748161E9997537B6B8D55D63DC73D1199F02C2EAE66A5CC7EF40547EC32136BFC53C774BE8E6DA10060CE9E17BAF1B9E3DF3C39339E4A734224F143FD1FFE48639712D3C06E4C58A4C45FE1E1425C3F52544DFD53183949F94BEFF89E0B5727E514FF514E786F8867A9FE714E786F887FE57A7D92684BB575B5D2038A9B2D02FD861082108E309AE0C3950F6B5DD5ADE401EF8843EA58870930EC39F6B1797EC33E39EFBD7F62667E15790D07480A2F3D19D714443FF22B18DF6D
	29B67C6C6CA10AE772B5B521FCBE688B82AC86D8863082E0CDB5176F2F2B9A19FC68A05DBAE4D7C11A21F0614ECB62CC8C0DC10F49B621DAE5DB8F6BED2D0E1D8745B5494EE0618C7D86D443EC64B471B245F7D8704CF62FD3DF4776G74255770736C9C26FE7C3EEF297FE46A565F2F5E3A25B418DD041A64772BE0CD627B15D21378FE2557DC7FFEB5D7137CFE75D7B5567B95732B08976323E7D7063B91E088A0G905D45FD2AF157C166D3897C69FD38F0C1960FDF54F4CE7FB7CD49E5BD513470F7387EA64C
	2347D49D6E8838BE477B1A50DE3A9A26C8067BDD936A88D231204E069A788BCD98E7E5F11C22D91E758795F1A9A0D75AAE1EFB5D69C05CEAE8671838C2974F479C3B39FFCE1CC15CB034E59327D15C9D50CEB3F1EFFE144817F04E1F62C34346FCC6237CCCA29A4EBF51CFF9EE4674F7551B5C06FF69DD5806F7FA697D88F4BFBC9B96996EAEAF4D1B4747A32269AB77FABDA6EEB0B224C6A43CE4319C6BDE55C57DC0239F3ED8DE77448C4F2D32C1A7B9GFB76E438DBGB0B7E6739EF2DC1B8FFE5D41678D6CE1
	7960A8143AB1EF659E9CF340553700FE2C309866AC85B8E434B3213EE95B36EDBF86E0FC3C61C3BFBE659346C4999F55F83E5A51007945A9B80146549EB54A3A48493D484332B356DCA5FFAF632F91CA561F6736765FE174AF071245576F4D70BB4EAA40656E65B961B78B715C7BD2F74DE1C917459E2AF516336C0F669B7E55310F67ACEB6DC8C70676EDG297BB87D9FB978591C89380D7B781BDD03534C7B7673B5AEB8F16CC5E86BG0B8176D37D005C047FEDFEAF910E5D817E08A299E76C0F0A79993B6731
	FFA0FD5FFBB36F90C912436DE7FBE94B7ED8FE1A325C4C61461364F0540ACC1617693EC73A0D35BCBF7E3FFE6B1BE7D6D91BAD59999F28E5F4EF2B0D519F057C18511F10F7A7264F3D45387BF73A39CDD65572587343190572135C5A79F148F883DFF05E3E38B61BD79745F955873EDCECEBBC405F0DFC54C7CE9E084F77BE26F85F0178FC6F95BB772991684BBA40657D1963667254B7EEF21E57F9F3796D8B544FEB8F464B7BA245B79E0C177777CE731D8C7AD28F727C1634E1FCA207A066811C8498811081EE
	83D882108E3092E0AD40E6GC7DD063B9A209160B84090C0200E1F6B2BDA706794896F9400B4AE0C74F05784767D0369A1670CD58F51BAE67F45C639944A4C3A79BE4178FF3C9DE3D7B6742D874885282C4FF0F754F3FECADB97FC43FDBFFA1DB75C936E6B59E79868846B677335176E391595980FA5C5169448E568CB9F615FF00E5158B78773D26AF9FEB0EAC7DC9A34336A39AF1E454FB37CCD58D2DFEF3F75796BAF7F8CB43E6D0C497483CB5D6F3C753467EEA98D66FB15ED49731D5DED8B67BBF5ED09F356
	DF35DD1B33B637A54ED95FEE33662C8C5755064A1E003DB8E20EFB4F44FDE2627A681A083BA085742009BFC579C27C000884B9FE0872B5C3F1A37C7D5B027B043FFF37E1AE85A287D50D4F3D5C068EBEAE49327186C147C88A24F026BE0214C7A9ACD8788E19B8641B6D25DF5033B49B7651DAF3FF3DBC00380D504EB5F1A787F9CE1EFF18639E25F97A88346543BC06BC551E5C0ED96D096FD94F36A73EE7E537DF7F8DF6CDFB32FB9863EF584DF9DEE4721CF689F1EB203D7EB04FD74FC6113E0EE9D73FCA63EC
	47919CBBC9E34FD69867B842474E50778DBA96F923F9D068ED5C0F2C3FF4085A8616E1EB7C4D6F6837ED285FE62E715AG0E51C191EC7E833F4179AC873EB0F8A3B9F0A62C0940F32D6CA36C37FA3FB4F360EF3A5027C485B7CB40106BC597FF5BF971884F3D275C57236B0C233B5EC2977B36C2FDB60F714068BEF369DA3A5F3D44F3D047D148D7296C2F9C0B69EE580E77DCC51C29651FCD3DE80F23878312A02B93879BEBDB9AEBDBBB9AF84E39F4467A8D3733701D24EFAA55C7593953E81429379FB2EC3769
	37B17A5F9A7832742F4D0939AFF8CE46567B390B5AD91B961367509FF54746EF3A047C0F8BF284FD0B1D597F5B0DF38240772223FC0F9DBA7EEB12BCEF985254302869338CCBDFA30D7DC2FACA2D7B05F599FD6E9E4C8D708D97E4153E20CFED67E020B241FE7B69D79DA0CC7AA662047D9151C8192922C19AF7F22E34F759FF7C305DC69C0344456270A0F1E0B09DA4CE9AEB87099D62F094FF177386G46C50CDB15BAA944ABD8AD7D0C2F93A7B63A00C449BCABBC77605BBFDD7D4A79081D3886C98DEE30F322
	C6F9958378BAE77CF8271F94047131885F2001FE04964E08BEAB0BBED2B9FE969FEAA33A986866DA0868E4078FA9B5636D2BF9C00884F5F45AE3BE62D070E7C6241CF54B02B2514CD4F20C8A1157BDCACD09CAC515D4D0FEF1952F39CA4553E57587CE2DDFE1A736FED2A82BC220DF7003D986C9019E5444C8D015835170FB54E9C87E8C5A9A6FADF6D8EE37A9BE8A6B45379713692968830BCBB614745BA034E824190E44F862B5C784FA03028624C4EDD0471FCD29DA1D5977977B74437F7EC155C59B49B3047C
	1C7C013823E11D38460510A44FC2250F7AFC84FB0AF460D6931448382A0500335AB84EF817C940FB4F73AD777E637FFCCA8944266AE4BFD3A5CCED1E9055D0F37778B864FC668220D7D8FE2F39BC0E71460DA90F5E3D6354CB5B46408F7A08BD5452C27F37527FED783F15E2DAA926358D143B19DC77AF3EBF4AE5264EE33ABA1A5F5B8F9654FF30697907DEBE3EF9A3C857CF1C74A9950C60090D7AE8BAB4A6EADFCB3FF7EFD1CA4E1DE823B03ACA9457ADE34BBB2F719F4496DC6B8808AEB14D01AD8F9DDACE7F
	6F38E161F0D6951DDBE372340F7104017320C1977F4ACDAC06AFAC719ADA46E2EF439F2D31F76B6D55C9E36F45A696F343CD716F59299A0A198E786CA69E5BB6FE5EC4123F331C8B89322C8B72D4379240B7171ED9DD0C658B8FEA38FE040FB1BAD9DF1A06FCCFDA667CBFD0CB8788C63FB7E8F191GGB0AFGGD0CB818294G94G88G88GB3EB09ADC63FB7E8F191GGB0AFGG8CGGGGGGGGGGGGGGGGGE2F5E9ECE4E5F2A0E4E1F4E1D0CB8586GGGG81G81GBAGGG2B91G
	GGG
**end of data**/
}

/**
 * Return the JComboBox1 property value.
 * @return javax.swing.JComboBox
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JComboBox getJComboBoxUser() {
	if (ivjJComboBoxUser == null) {
		try {
			ivjJComboBoxUser = new javax.swing.JComboBox();
			ivjJComboBoxUser.setName("JComboBoxUser");
			ivjJComboBoxUser.setEnabled(false);
			// user code begin {1}
			
			DefaultDatabaseCache cache = DefaultDatabaseCache.getInstance();	
			synchronized( cache )
			{
				List users = cache.getAllYukonUsers();
				for( int i = 0; i < users.size(); i++ )
					getJComboBoxUser().addItem( users.get(i) );
			}
			
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJComboBoxUser;
}

/**
 * Return the JLabelType property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getJLabelUser() {
	if (ivjJLabelUser == null) {
		try {
			ivjJLabelUser = new javax.swing.JLabel();
			ivjJLabelUser.setName("JLabelUser");
			ivjJLabelUser.setFont(new java.awt.Font("dialog", 0, 14));
			ivjJLabelUser.setText("User:");
			ivjJLabelUser.setEnabled(true);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJLabelUser;
}

/**
 * Return the JPanelTrigger property value.
 * @return javax.swing.JPanel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JPanel getJPanelLoginPanel() {
	if (ivjJPanelLoginPanel == null) {
		try {
			com.cannontech.common.gui.util.TitleBorder ivjLocalBorder;
			ivjLocalBorder = new com.cannontech.common.gui.util.TitleBorder();
			ivjLocalBorder.setTitleFont(new java.awt.Font("dialog", 0, 14));
			ivjLocalBorder.setTitle("User Login");
			ivjJPanelLoginPanel = new javax.swing.JPanel();
			ivjJPanelLoginPanel.setName("JPanelLoginPanel");
			ivjJPanelLoginPanel.setBorder(ivjLocalBorder);
			ivjJPanelLoginPanel.setLayout(new java.awt.GridBagLayout());

			java.awt.GridBagConstraints constraintsJLabelUser = new java.awt.GridBagConstraints();
			constraintsJLabelUser.gridx = 1; constraintsJLabelUser.gridy = 1;
			constraintsJLabelUser.anchor = java.awt.GridBagConstraints.WEST;
			constraintsJLabelUser.ipadx = 11;
			constraintsJLabelUser.ipady = -2;
			constraintsJLabelUser.insets = new java.awt.Insets(18, 11, 34, 0);
			getJPanelLoginPanel().add(getJLabelUser(), constraintsJLabelUser);

			java.awt.GridBagConstraints constraintsJComboBoxUser = new java.awt.GridBagConstraints();
			constraintsJComboBoxUser.gridx = 2; constraintsJComboBoxUser.gridy = 1;
			constraintsJComboBoxUser.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintsJComboBoxUser.anchor = java.awt.GridBagConstraints.WEST;
			constraintsJComboBoxUser.weightx = 1.0;
			constraintsJComboBoxUser.ipadx = 188;
			constraintsJComboBoxUser.insets = new java.awt.Insets(15, 1, 31, 30);
			getJPanelLoginPanel().add(getJComboBoxUser(), constraintsJComboBoxUser);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJPanelLoginPanel;
}

/**
 * Insert the method's description here.
 * Creation date: (4/5/2001 11:48:42 AM)
 * @return java.lang.String
 */
private String getLoginTypeString()
{
	StringBuffer retValue = new StringBuffer("");
/*FIXFIX	
	//check for the curtailment login
	if( getJCheckBoxCurtailment().isSelected() )
	{
		if( getJComboBoxCurtailmentRights().getSelectedItem().toString().equalsIgnoreCase(CustomerLogin.RIGHTS_VIEW_ONLY) )
			retValue.append( "V" + CustomerLogin.CURTAILMENT );
		else
			retValue.append( CustomerLogin.CURTAILMENT );
	}

	//check for the loadcontrol login
	if( getJCheckBoxLoadControl().isSelected() )
	{
		if( retValue.length() > 0 )
			retValue.append(",");
			
		if( getJComboBoxLoadControlRights().getSelectedItem().toString().equalsIgnoreCase(CustomerLogin.RIGHTS_VIEW_ONLY) )
			retValue.append( "V" + CustomerLogin.LOADCONTROL );
		else
			retValue.append( CustomerLogin.LOADCONTROL );
	}


	//check for the readmeter login
	if( getJCheckBoxReadmeter().isSelected() )
	{
		if( retValue.length() > 0 )
			retValue.append(",");
			
		if( getJComboBoxReadmeterRights().getSelectedItem().toString().equalsIgnoreCase(CustomerLogin.RIGHTS_VIEW_ONLY) )
			retValue.append( "V" + CustomerLogin.READMETER );
		else
			retValue.append( CustomerLogin.READMETER );
	}

	//check for the energyexchange login
	if( getJCheckBoxEnergyExchange().isSelected() )
	{
		if( retValue.length() > 0 )
			retValue.append(",");
			
		if( getJComboBoxEnergyExchangeRights().getSelectedItem().toString().equalsIgnoreCase(CustomerLogin.RIGHTS_VIEW_ONLY) )
			retValue.append( "V" + CustomerLogin.ENERGYEXCHANGE );
		else
			retValue.append( CustomerLogin.ENERGYEXCHANGE );
	}
	
	*/	
	if( retValue.length() <= 0 )
		return "(none)";
	else	
		return retValue.toString();
}


/**
 * This method was created in VisualAge.
 * @return java.awt.Dimension
 */
public Dimension getPreferredSize() {
	return null;
}


/**
 * getValue method comment.
 */
public Object getValue(Object o) 
{
	//FIXFIX
/*	CustomerContact cContact = (CustomerContact)o;
	YukonUser login = cContact.getYukonUser();

	if( getJCheckBoxEnableLogin().isSelected() )
	{
		
		if( login.getUserID().intValue() == YukonUser.INVALID_ID.intValue() )
			login.setUserID( YukonUser.getNextUserID() );
		
		if( getJTextFieldUserID().getText() != null && getJTextFieldUserID().getText().length() > 0 )
			login.setUsername( getJTextFieldUserID().getText() );

		if( getJPasswordFieldPassword().getPassword() != null && getJPasswordFieldPassword().getPassword().length > 0 )
			login.setPassword( new String(getJPasswordFieldPassword().getPassword()) );

		//login.setLoginType( getLoginTypeString() );
	}
	else
	{
		//if we have a valid LoginID, just disable the login and save any changes made
		if( login.getUserID().intValue() != YukonUser.INVALID_ID.intValue() )
		{
			login.setStatus( YukonUser.STATUS_DISABLED );
			
			if( getJTextFieldUserID().getText() != null && getJTextFieldUserID().getText().length() > 0 )
				login.setUsername( getJTextFieldUserID().getText() );

			if( getJPasswordFieldPassword().getPassword() != null && getJPasswordFieldPassword().getPassword().length > 0 )
				login.setPassword( new String(getJPasswordFieldPassword().getPassword()) );

			//login.setLoginType( getLoginTypeString() );
		}
	}
	
	return o;
*/
	return o;
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
}


/**
 * Initialize the class.
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void initialize() {
	try {
		// user code begin {1}
		// user code end
		setName("CustomerContactLoginPanel");
		setToolTipText("");
		setLayout(new java.awt.GridBagLayout());
		setSize(416, 348);

		java.awt.GridBagConstraints constraintsJPanelLoginPanel = new java.awt.GridBagConstraints();
		constraintsJPanelLoginPanel.gridx = 1; constraintsJPanelLoginPanel.gridy = 1;
		constraintsJPanelLoginPanel.fill = java.awt.GridBagConstraints.BOTH;
		constraintsJPanelLoginPanel.anchor = java.awt.GridBagConstraints.WEST;
		constraintsJPanelLoginPanel.weightx = 1.0;
		constraintsJPanelLoginPanel.weighty = 1.0;
		constraintsJPanelLoginPanel.ipadx = -10;
		constraintsJPanelLoginPanel.ipady = -15;
		constraintsJPanelLoginPanel.insets = new java.awt.Insets(20, 10, 245, 6);
		add(getJPanelLoginPanel(), constraintsJPanelLoginPanel);
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
	if( getJComboBoxUser().getSelectedItem() == null )
	{
		setErrorString("The user login must be selected");
		return false;
	}
	
	return true;
}

/**
 * setValue method comment.
 */
public void setValue(Object o) 
{
	//FIXFIX
	if( o == null )
		return;

	CustomerContact cContact = (CustomerContact)o;
	
	for( int i = 0; i < getJComboBoxUser().getItemCount(); i++ )
	{
		LiteYukonUser user = (LiteYukonUser)getJComboBoxUser().getItemAt(i);
		if( user.getUserID() == cContact.getCustomerContact().getLogInID().intValue() )
		{
			getJComboBoxUser().setSelectedIndex( i );
			break;	
		}
	}
}

}