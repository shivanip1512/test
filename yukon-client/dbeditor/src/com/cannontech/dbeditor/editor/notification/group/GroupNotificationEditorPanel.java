package com.cannontech.dbeditor.editor.notification.group;
import javax.swing.BorderFactory;
import javax.swing.JScrollPane;

import com.cannontech.common.util.CtiUtilities;
import com.cannontech.database.data.notification.NotificationGroup;

/**
 * Insert the type's description here.
 * Creation date: (11/20/00 10:45:15 AM)
 * @author: 
 */
public class GroupNotificationEditorPanel extends com.cannontech.common.gui.util.DataInputPanel implements java.awt.event.ActionListener, javax.swing.event.CaretListener {
	private javax.swing.JLabel ivjJLabelName = null;
	private javax.swing.JTextField ivjJTextFieldName = null;
	private javax.swing.JCheckBox ivjJCheckBoxDisableGroup = null;
	private JScrollPane editorScrollPane = null;

class IvjEventHandler implements java.awt.event.ActionListener, javax.swing.event.CaretListener {
		public void actionPerformed(java.awt.event.ActionEvent e) {
			if (e.getSource() == GroupNotificationEditorPanel.this.getJCheckBoxDisableGroup()) 
				connEtoC5(e);
		};
		public void caretUpdate(javax.swing.event.CaretEvent e) {
			if (e.getSource() == GroupNotificationEditorPanel.this.getJTextFieldName()) 
				connEtoC1(e);
		};
	};
	IvjEventHandler ivjEventHandler = new IvjEventHandler();

/**
 * GroupNotificationEditorPanel constructor comment.
 */
public GroupNotificationEditorPanel() {
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
	if (e.getSource() == getJCheckBoxDisableGroup()) 
		connEtoC5(e);
	// user code begin {2}
	// user code end
}


/**
 * Method to handle events for the CaretListener interface.
 * @param e javax.swing.event.CaretEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
public void caretUpdate(javax.swing.event.CaretEvent e) {
	// user code begin {1}
	// user code end
	if (e.getSource() == getJTextFieldName()) 
		connEtoC1(e);
	// user code begin {2}
	// user code end
}


/**
 * connEtoC1:  (JTextFieldName.caret.caretUpdate(javax.swing.event.CaretEvent) --> GroupNotificationEditorPanel.fireInputUpdate()V)
 * @param arg1 javax.swing.event.CaretEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC1(javax.swing.event.CaretEvent arg1) {
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
 * connEtoC2:  (JTextFieldFromAddress.caret.caretUpdate(javax.swing.event.CaretEvent) --> GroupNotificationEditorPanel.fireInputUpdate()V)
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
 * connEtoC3:  (JTextFieldSubject.caret.caretUpdate(javax.swing.event.CaretEvent) --> GroupNotificationEditorPanel.fireInputUpdate()V)
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
 * connEtoC4:  (JEditorPaneMessage.caret.caretUpdate(javax.swing.event.CaretEvent) --> GroupNotificationEditorPanel.fireInputUpdate()V)
 * @param arg1 javax.swing.event.CaretEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC4(javax.swing.event.CaretEvent arg1) {
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
 * connEtoC5:  (JCheckBoxDisable.action.actionPerformed(java.awt.event.ActionEvent) --> GroupNotificationEditorPanel.fireInputUpdate()V)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC5(java.awt.event.ActionEvent arg1) {
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
	D0CB838494G88G88G48EB41B2GGGGGGGGGGGG8CGGGE2F5E9ECE4E5F2A0E4E1F4E135BA8BD0D4D7164C1613F2A66E2CC6F17097D150E8C4A5CAA6568CB565EE482EEECC2D59A495E707E4190D1318590CEB6D3093EB17AAAF7DD1B402A8C4A10290C4108CE9D0D1A9C4820288088923E8C8424E7868FE400B5D6FB52F9F3F2C09FB4E3D6F3EFEB45D2013994A53775DF36EBD771CF34FBD771C77A459851176D4CBB6A129F6227F1DCFA5E4C3B9A19F4CBC371963367D0D4D20635F7BG87C9
	6CB7B6E8F782ED4BCFEC76CC32451B867D7D53ED768DCF596CC760B90B3C620DAD0507906BCC85DA7F8787BE19F81D53B9FC1DBD347F48697D50E6GE442DAD5GE153F17D47CF676878AE68E7684F882996C2BAE660189F8F641870897A38E220D583A453B14F1C4E5369915047B131584EE0E393215DCB257BEECD35BE2EFBC6A4C97E47F316F4DC4B441FC36DA3EC6EED3DADA4BF25063012780FBB115EF1272F4E29F89CCEC11695D9931D7D8ED70F6812B4C5F5680D2CE8D22F6494B4C9119DFD2AB2682D29
	39024D47A642E5BA36C310C57766C7D0172401B43A8E68827D6E19086BD251FE5970DC8E50B4136B73FCFA2DAE6F2CBB11A4A92AAC24BE11B3199E89B37D7A24BC13E655E6A17F6DF4FFF01E8D7A35GCC16F3E738AC6BE6F1D9BE7160589D50CF8B37598B43B97E300C78EA68F7G7407F399AF16F2993B7EEFB5492CF853FAA0040CE161CC36E561FE99A3766D3665D29F7AE9AE7A41D620254D365973G6A81EE8304819C253EBB1A7381348DBDDA236271A8F20357FB496DD22FA9AE518D4FB5B5C051F157C5
	27223A88E17E794B0172A072F00818BD56B79118EE1F926ECFB15717129DA33F325A03689677D235A5C66731FE36DF376459E837CDF4EF1360996761FC06FF19622BFF6847333D7A8345E33F9DE831BF64F66E4A6032345B66124CD3DFD8768411A5626FBAAC5991CC0672883FAC4C4691C75146DD0057G2681C483A4GEC0D60B6FEBA674CE4B6EE0573204A027B46E00FD7D035CB03125BA52A3A2D13CFCFE46BE04066D94EBC68BCE617E5E74CFB1438767320F6C9DC7611054CE176481963378BDB7BC24835
	2B670C770D8CE38F1F26E725FD8E5BC32B01FF0E623BCDF83637B7A99E7BEE20ED86E0633FCF4FD6555CC09F892778163901BE12A7F39F69865AFAG363F6772B1CE964F03338350856085188E908330FE9E5F630C5C0A7BBCC79D267DE5F37FA177B0346208268A2D32CF93E427F8CBD0A52147AD7A08BEE6F3AE5A35E74C3C176879F6429AD7C4D9D4E9F0846E02C784E77231B12F669E44B12AC8291D12C70485BCDE42793E1E1B053693FC5ACD2FCB50C47F88C65F1E0BBEB1A45C820E40G1EB367233DD2E8
	4CCD827DD366F1DBB90FA12E93686E795CAE52FB07203D8A4EAD3AEBEA1AA4A70AA128235CC77FD945B9E1B05E36001F4B6DDD5C7F1A5217107217BE8B6AFF8853976871EFC1207F3DC279EE867C0E857CCEF529DD77F98ED01E7BBDB3B6FDED364657773D0675814E25DDEF191D1FAFC0DF4DFC946EB60076C7392D235FABE4BEF8C970095C6BBED141E1C4175F72EC6C2223F82708C3222CB509DED1F64103CF5F73E5C7777A7D31D7B1FCE59545E3FCEA8407719978382387999EF8183D1B5363A9DD50B4D56A
	9954449B2226C9F21FFFFE821545B7A67479F4DDDFA8C4DDDD2063DD00E9062E577232CCFE552EA8F7863D1DF8E2F47F3CCE6F61C8983F6ED13E7F9BBD18E3A59A7D97E85FCF7F42F33FFB978C106F64B1D97BD6EBCC66B3EC0D69B26A0E67E52F011BC7F1360560B38B393EC5F29799FF2E76FE8B79464BF2012E91A8377D4F348673057A7C503EFCA5BF38AFDF4B8F6E4BB772A77655AF720379AA23756423DE4A30EC727D3EFC0CFFCEC516C5FAG0DA90465465BBBD18FC91634CFA5D71F285D103E9289393B
	90FDB80B4630CEF86EDF48E36370F19E9B0B3FDAC4567F314A92ACFF0AD8F24332FEA1734797977AE3A313297F981EA951ADFA40D00D4A202CB13CCC63370E77D9A96E2F6F8D615C13ED1DE043E6C9F43BBE067B255C8907DDF350DFF6EF1C7CD29892C69C3EE1301603EE052391291771312C70438F6B8AA7CA644BFBA5D5EC153D031CDD592D32CA66B7D9641B43E1CC0646FE51F96712B252A4797028D3066582352B03B59D225A2B289E51C565F188431AAECC83A5FF8BE92AD8BC5ADD0436CB861B26C5C2AE
	9F4963512F0EA11D47A3BD68B6AAEEB75BF620CD0944BDBD604BB66DD3F5A42B6DB28C7A0510F4364E4B851968FF62402088E982DA612AB84A6F7A02C38134DB02FB501FC33CDE708E34EED32CE4781F95867707FFAD4478F847607FDA61EE0239G63497CB7D6C01EDED57240550241DF2FC125B3669AF4936801756A7EC5E0B3GB6B7229EF32B09B6E253A21A7B493CD62D06FE8700F7916397330859060D7B2D170F4BD9EC33D782F4AC9EBBEE7AE29C77014AEB348A8357C0F1D7A1E87D42467360EB19665A
	376A0FEF862DD56899DB4C78EFDB9CD89FDCB80EFC5322EC761CA86EAB7F7B3E796EEA70799427C4B55777E3FF11799CB25C4122FD5002DF7A20D6F009A3FCCF0F94212C183F69E8FDDFD67C6EA2991F0FB253725420F1181E07CE2591D24896F00BC6A3CC5D1244E1426D70F53339E60EBF50E0F9A7049D4A23187E6DD1FEBB34C0BD4AE41AD303F5D03077GCEAA2423EFD0F28CEA92A60A02A65083C85D00305A7A85FA27A32FE9D13C060EBA073E1BG7DC400E4G164F7F56486777C7C37D8F404ED75D1079
	FC5D0DE679C1B434AC56BC6DC51FF84070265BBC216C905B230058416D38B82B1E1F07D5F3C96D3DA2CB303AFE6A01F24B3AE8E63F24E83F9D7BFF365B36A9E9370D69D7B04075ABDFE2333B16F07D4E5DB46BB7FF8953AFE189576F21015350BEF84CB46A775366B331BE29BE687D0330E589137D2DA501E7226084BD93CBED763C257CCCBCD2F450F4A62E03978AF29FC4A8630EBFEEE531C86FB79D3773D8C4F8DE5CDD023AD7824FF6G9740DD0079469A4F9E1F348E3FBCB2C19D3EE5DCCD9B3F541F73EDDE
	BABE7FE3B567117338D7DB17321AF30F01BFCE71A9A6BC5B5B655427311F7D184DBE72984F57D2644075939F9B3B664E00BE1B37CF891C67886867AD9BBB0F79730913F97C3C1A5C3F26461F8B30F3B9B84463E955B2DE8B2EA94539EE68CF8508840887C882C881A8DC4E77643D62E0F1EA6C1E048AD27EBA0369F9E25CBB297B334F19F173826DD33FBC18FD6AEE9D8DEE9F1E2768F14BF3B3CDDF2BEBB9374FFF1EC45CF468C79A3698AECEB71D018EC512B5437F0BCF605D2B28D21FA4F3DC9945B99515A61C
	8C67AA891D8B5FA9891E8B8F14844F0507CBA62E6B6E15044A15D98D769FB4DE4702FE89G8CF768AC6236C11FBC4E7DBA1F1E03E2685782F49978B20A3F7BB83B7371BEE7785567A62B2110D7B0FCED90FC576341FCE24324EB042A450255B6FB827A79F47E46438728FDFDBE51C71D9CEFBD1D760676AE89D2B7E072CDF8DEFCC6592B0F392CA90D278C593F9A1A683D5F7CA0B6506B7452503E73F4E9F05F59D80ABE6BD386D52798689F1BCBF38C1A7F5436EA22875550F3CFBADF93D428FD8267A751790C96
	EA7E1B2521FC105928FB40EC2333060D541BE69B456AB6C9505BADA1ED7476C90C55A3C67FFFE8FF54D8EFF7951EC937B02A8C42612DDF01BE1FDFE54E055F825E5900BF39022D41E249934318D3752A0287443F61951DA4AF8667161D65F327427879G71G09GA9AB781D1EFB9BF34336F621C7F4B387E1785ACBB8EFEC6546E9A5FFC07F8ADAD191DE0BF41FB767922E9816CBCC0D61E7F94690BF4B738D5C77E82CFB8B7AA9862E2A95F119ABA16E2F643870D6BE37FCA52F87CE5D9EF35FE0A6021FFFA457
	A5212F1DDA14455346AA7EFE636ECA1E878ED4E12C08037EA6002DABF93DBCD242F3571825F3C9FB5867C13F77A4F5B6D8D2D66AF94AAAFF3EB285F2D736664A33FC4DEE204FDA05381F1FB55B876D7A9B8B3BBB98BD7D1C190EFE8AE914097E5198BA4A7B46187983E368F653FC265B33A738EEA94FAEA36EA7DBC2FE4BCA54F5A22B7556A625CEF12769F979EFFAF3C850FB0CFA9453FF66F9FE07D5830F685508DBB65494E42D31F7249F3CB0E78A40891D96814FFBD3193C4CA77F7EBC561B3DD0931C0CC57E
	EB18AF41381D2B392FBF1101E3B4F0E9F7AC0A1F0DA6A0CC1E18560964316A6BF239261864C199E2019FFE0BFDFB572EDDACC634D4BD68779316577D127AE2FCAC4B6BD28D7CF60A5FE04233FCEF380AFFCBFC91E855CF709C616DDA5C177950DF873099E09BC05A9A1B3DF88D4F994270B5494479D32762ED07206EB6DD61CC3FB5E77F346FC3FF3B077BDD5C0F9713F58F25052C872B57B0FF6B525B880B140AAD536F7BF5285FD420C583ECGC882C886583F166B37212C1C690725EB0326894EFEDA95F36530
	D07523B1F0B25DA24E1BF5ABDC4B562F525B16356363FB81DD8F6A92427FF6FCB49E87311D7A1DD4CF8CFB7CE2BF37C75E0AF9246965B44B7190766856570DD96B0FB1E10E572C0CE7F3B3B14E424956A2E8E5F13850A84A1A71E09439A4412D74951597D616971E2AB26A3248E1F3DDD63D2FB9E4DD36D5DFFB275E3A4FCA290C47BFFDE16611B93752129A02C7E59C1BDB9B6757036970DFD710935B03FF2F72BFA71B1E3F2E7AB66F40A78336466C3F689A4CB749455079521E0B417325740B41F36D2C0B933F
	F7BEFAB174FB67220B66774E6CECBD5302F7079776E79AC0ACC0824016B8FE2EE2AF22FF61D74971E76A322B8F73B57C92297175E7560556F5EE5D645FAF236AB0C769D1B4CD71A0FF0EDFDEC76BDE45CB117AF726BA349114BA7D9A9B4A3FB35561B9F40B3D944D72085567F93E119C47F30B3999BC5FC8FB1263F6E570FCA37BC99EDF0F3761FD6102FE98C0B4C0BC40E600EDGA9B877C790EB81EA81BA817A816E82CC87088408835870A3EE53D575F8BF5F923E9C7F368D10FAA2C38D2B8A2C36E3F2FC6FAA
	4A1688FCF69A3C9EEE7E5D843CBAD445AB2A5AA84352373CBA2FA96D443057734DA7C27100DDEE033872C94F1710D4F78AFD461C67DAC26F730BAD136F73ABAD745DA56CCF6728D74F517F2D05562E3D3DBE517027B7DAB28DDC2B2FC371C9185B33FBBF116EE94ED3E0FBG96D736DEC33D58BDFF7840E4773C99826F794E27D89D89577CAEA64B793650BA5735CD2E731536603EFD2DED3CEFDFEF8B6E5BDDEDE65F565F933761396E833F69B2467598384F8CDC9F650938CFA51756EF60BDD4AE44370838A047
	7B28DC4394572563C6CC384FF45C57ED38EF20F23FE23CDF782BB61A23C9EEB76BBFC447C8B2EC23E10F07290C12D7B049BD5D4079657E41558CF34CE3EE2C53422FE69376DD8C6EBB69AB7AFA7C868C147B18BF4C68A0045F15BF48385F3B124D0D7BBD4EE57FF7CF3EEB8305E2FECD6C9AC9572BA076ED48271283863FD62AE05250199A6BEBD1D68B3135125DEC43DB098DF72D155869263692ABEC788896D128B06473E4859ECC6016BEC63AE7961DFDFD532B37FCD6323B1564E252FBC3D4292E228B5F8368
	7F6122196C75620BA07CAE88CB79E8E377E923EE311DE477DE470FBDBECDF4B5F0AB7AB472EEBB2ED401EF52ABDA84DF3F06CADDE8A7B6997FEB88B94448EEC16E2BE0A63DC065CDED380DF6210708003CD9D4DE6462B038E47D644009775B362E3392CBB35967D684D73360042052CA76EA7D2A686BD75C2E916FC77514D15AB07AB46606D6E0F754D09F15F5E0D5EA88FDE7E4413474ED694DE02E9115D450111863BB0C912E46FEC10525C4956AFFD500A50B8D5AF70EE9A54FDDB952EDA1293A1283649BE29F
	71EAE4F7AF94546ED1F8E8223E60C34ADE8D7E29026C43575EA0D9E100E41CE4AC60183931B2FC79FFFFCE9770CF55C881B3A5CC2D689395CFC5C3EF2FA40B868330AB304FB7586398C74018C3A3B35EEDFBE9D78F78C19331FAAAAB696FA97AFB9AFFCFD14CA90AB9F59A0C3B03CC7897C89F61BAD367B13C9A375F518CBB287D4B2FB7AE7EF96D0E37C03BE6E2272F54E193B27D23BE9E7474086AC3B161796F4CCAFA9877480B2EB202FC5399FB2C63B04343A466D574FD0D5871F5C38D25DA47F997E2770CF7
	93C43FE3EC9676B26850CC7A1B75402A632CF7695CA30644478C0975D4111D5203AC87AE3BE74E01F7FE37B4E49E1D974FF25F4A78406F9B0F5D23319C70D36379FD7E76B79F1250F7718D0F60F6EB027BCE036C42FB795228A67AF33F18FB485F474768C4C6DBFB8F657E52B4677F81D0CB8788259DFD51F691GG70ACGGD0CB818294G94G88G88G48EB41B2259DFD51F691GG70ACGG8CGGGGGGGGGGGGGGGGGE2F5E9ECE4E5F2A0E4E1F4E1D0CB8586GGGG81G81GBAG
	GG3091GGGG
**end of data**/
}

/**
 * Return the JCheckBoxDisable property value.
 * @return javax.swing.JCheckBox
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JCheckBox getJCheckBoxDisableGroup() {
	if (ivjJCheckBoxDisableGroup == null) {
		try {
			ivjJCheckBoxDisableGroup = new javax.swing.JCheckBox();
			ivjJCheckBoxDisableGroup.setName("JCheckBoxDisableGroup");
			ivjJCheckBoxDisableGroup.setFont(new java.awt.Font("dialog", 0, 14));
			ivjJCheckBoxDisableGroup.setText("Disable Group");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJCheckBoxDisableGroup;
}


/**
 * Return the JLabelName property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getJLabelName() {
	if (ivjJLabelName == null) {
		try {
			ivjJLabelName = new javax.swing.JLabel();
			ivjJLabelName.setName("JLabelName");
			ivjJLabelName.setFont(new java.awt.Font("dialog", 0, 14));
			ivjJLabelName.setText("Name:");
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
 * Return the JTextFieldName property value.
 * @return javax.swing.JTextField
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JTextField getJTextFieldName() {
	if (ivjJTextFieldName == null) {
		try {
			ivjJTextFieldName = new javax.swing.JTextField();
			ivjJTextFieldName.setName("JTextFieldName");
			// user code begin {1}
			ivjJTextFieldName.setDocument(new com.cannontech.common.gui.util.TextFieldDocument(com.cannontech.common.gui.util.TextFieldDocument.STRING_LENGTH_40));
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
 * This method was created in VisualAge.
 * @return java.lang.Object
 * @param o java.lang.Object
 */
public Object getValue(Object val) 
{
	NotificationGroup gn = null;
	if( val == null )
		gn = new NotificationGroup();
	else
		gn = (NotificationGroup)val; 


	String groupName = getJTextFieldName().getText();
	if( groupName != null )
		gn.getNotificationGroup().setGroupName(groupName);
		

	if( getJCheckBoxDisableGroup().isSelected() )
		gn.getNotificationGroup().setDisableFlag("Y");
	else
		gn.getNotificationGroup().setDisableFlag("N");

	return gn;
}


/**
 * Called whenever the part throws an exception.
 * @param exception java.lang.Throwable
 */
private void handleException(java.lang.Throwable exception) {

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
	getJCheckBoxDisableGroup().addActionListener(ivjEventHandler);
}

/**
 * Initialize the class.
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void initialize() {
	try {
		// user code begin {1}
		// user code end
		setName("GroupNotificationEditorPanel");
		setLayout(new java.awt.GridBagLayout());
		setSize(371, 377);

		java.awt.GridBagConstraints constraintsJLabelName = new java.awt.GridBagConstraints();
		constraintsJLabelName.gridx = 1; constraintsJLabelName.gridy = 1;
		constraintsJLabelName.anchor = java.awt.GridBagConstraints.NORTHWEST;
		constraintsJLabelName.ipadx = 3;
		constraintsJLabelName.ipady = -1;
		constraintsJLabelName.insets = new java.awt.Insets(17, 17, 7, 2);
		add(getJLabelName(), constraintsJLabelName);

		java.awt.GridBagConstraints constraintsJTextFieldName = new java.awt.GridBagConstraints();
		constraintsJTextFieldName.gridx = 2; constraintsJTextFieldName.gridy = 1;
		constraintsJTextFieldName.fill = java.awt.GridBagConstraints.HORIZONTAL;
		constraintsJTextFieldName.anchor = java.awt.GridBagConstraints.NORTHWEST;
		constraintsJTextFieldName.weightx = 1.0;
		constraintsJTextFieldName.ipadx = 132;
		constraintsJTextFieldName.insets = new java.awt.Insets(17, 2, 5, 171);
		add(getJTextFieldName(), constraintsJTextFieldName);

		java.awt.GridBagConstraints constraintsJCheckBoxDisableGroup = new java.awt.GridBagConstraints();
		constraintsJCheckBoxDisableGroup.gridx = 1; constraintsJCheckBoxDisableGroup.gridy = 2;
		constraintsJCheckBoxDisableGroup.gridwidth = 2;
		constraintsJCheckBoxDisableGroup.anchor = java.awt.GridBagConstraints.NORTHWEST;
		constraintsJCheckBoxDisableGroup.ipadx = 16;
		constraintsJCheckBoxDisableGroup.ipady = -5;
		constraintsJCheckBoxDisableGroup.insets = new java.awt.Insets(5, 17, 308, 222);
		add(getJCheckBoxDisableGroup(), constraintsJCheckBoxDisableGroup);
		initConnections();
	} catch (java.lang.Throwable ivjExc) {
		handleException(ivjExc);
	}
	// user code begin {2}
	// user code end
}

/**
 * This method must be implemented if a notion of data validity needs to be supported.
 * @return boolean
 */
public boolean isInputValid() 
{
	if( getJTextFieldName().getText() == null || getJTextFieldName().getText().length() <= 0 ||
		getJTextFieldName().getText().equalsIgnoreCase(CtiUtilities.STRING_NONE) )
	{
		setErrorString("The Name text field must be filled in");
		return false;
	}

//	if( getJTextFieldFromAddress().getText().indexOf("@") == -1 )
//	{
//		setErrorString("The e-mail Address you entered is invalid");
//		return false;
//	}
	
	return true;
}


/**
 * main entrypoint - starts the part when it is run as an application
 * @param args java.lang.String[]
 */
public static void main(java.lang.String[] args) {
	try {
		javax.swing.JFrame frame = new javax.swing.JFrame();
		GroupNotificationEditorPanel aGroupNotificationEditorPanel;
		aGroupNotificationEditorPanel = new GroupNotificationEditorPanel();
		frame.setContentPane(aGroupNotificationEditorPanel);
		frame.setSize(aGroupNotificationEditorPanel.getSize());
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
 * setValue method comment.
 */
public void setValue(Object val) 
{
	if( val == null )
		return;

	NotificationGroup gn = (NotificationGroup)val;
	
	String groupName = gn.getNotificationGroup().getGroupName();
	if( groupName != null )
		getJTextFieldName().setText(groupName);
	

	String disabled = gn.getNotificationGroup().getDisableFlag();
	if( disabled != null )
	{
		if( disabled.equalsIgnoreCase("Y") )
			getJCheckBoxDisableGroup().setSelected(true);
		else
			getJCheckBoxDisableGroup().setSelected(false);
	}
	
}
}