package com.cannontech.graph.menu;

import com.cannontech.database.db.graph.GraphRenderers;

/**
 * This type was created in VisualAge.
 */

//import com.cannontech.graph.model.GraphModelType;

public class ViewMenu extends javax.swing.JMenu {
	private javax.swing.ButtonGroup graphViewButtonGroup = null;
	private javax.swing.JRadioButtonMenuItem ivjBarGraphRadioButtonItem = null;
	private javax.swing.JRadioButtonMenuItem ivjLineGraphRadioButtonItem = null;
	private javax.swing.JMenuItem ivjRefreshMenuItem = null;
	private javax.swing.JSeparator ivjViewSeparator = null;
	private javax.swing.JRadioButtonMenuItem ivjBarGraph3DRadioButtonItem = null;
	private javax.swing.JRadioButtonMenuItem ivjStepGraphRadioButtonItem = null;
	private javax.swing.JRadioButtonMenuItem ivjShapeLineGraphRadioButtonItem = null;
/**
 * YukonCommanderFileMenu constructor comment.
 */
public ViewMenu() {
	super();
	initialize();
}
/**
 * YukonCommanderFileMenu constructor comment.
 */
public ViewMenu(int viewType) {
	super();
	initialize();
	setSelectedView(viewType);
}

/**
 * @param viewType
 */
private void setSelectedView(int viewType)
{
	if( viewType == GraphRenderers.LINE)
		getLineGraphRadioButtonItem().setSelected(true);
	else if( viewType == GraphRenderers.STEP)
		getStepGraphRadioButtonItem().setSelected(true);
	else if( viewType == GraphRenderers.SHAPES_LINE)
		getShapeLineGraphRadioButtonItem().setSelected(true);
	else if( viewType == GraphRenderers.BAR)
		getBarGraphRadioButtonItem().setSelected(true);
	else if( viewType == GraphRenderers.BAR_3D)
		getBarGraph3DRadioButtonItem().setSelected(true);
	else	//DEFAULT
		getLineGraphRadioButtonItem().setSelected(true);

}
/**
 * Return the BarGraph3DRadioButtonItem property value.
 * @return javax.swing.JRadioButtonMenuItem
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
public javax.swing.JRadioButtonMenuItem getBarGraph3DRadioButtonItem() {
	if (ivjBarGraph3DRadioButtonItem == null) {
		try {
			ivjBarGraph3DRadioButtonItem = new javax.swing.JRadioButtonMenuItem();
			ivjBarGraph3DRadioButtonItem.setName("BarGraph3DRadioButtonItem");
			ivjBarGraph3DRadioButtonItem.setText("3D Bar Graph");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjBarGraph3DRadioButtonItem;
}
/**
 * Return the BarGraphRadioButtonItem property value.
 * @return javax.swing.JRadioButtonMenuItem
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
public javax.swing.JRadioButtonMenuItem getBarGraphRadioButtonItem() {
	if (ivjBarGraphRadioButtonItem == null) {
		try {
			ivjBarGraphRadioButtonItem = new javax.swing.JRadioButtonMenuItem();
			ivjBarGraphRadioButtonItem.setName("BarGraphRadioButtonItem");
			ivjBarGraphRadioButtonItem.setText("Bar Graph");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjBarGraphRadioButtonItem;
}
/**
 * 
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private static void getBuilderData() {
/*V1.1
**start of data**
	D0CB838494G88G88GB600D0B0GGGGGGGGGGGG8CGGGE2F5E9ECE4E5F2A0E4E1F4E135D98DF0D4559526B5BAB8F5C66D50E9349D25961DF40AED3451C607692025AD2D582A032D1DE6BAB8459ADBF0EC0769248EEDEFB60979A58493CDECD4D29B84ADC614D4528888A10AA01410AC49A28173F6776E6ECB5E3E775877B6496627741C7B7376A53ECDEAC718B95CF74FB9775EF34FB9775C6FEEC8435645E5453E86C20A4B087837271810705F88313357DDAEF905A79A2B096B9F0849967253
	37CA21AD8219EDB6D6571115BBAB20DF5353D83D51E82CEE046FFAF26F4E35DB61A343BA4BC0F6E9E9601D395759E44BF54AD97F81DA83EDADD09D2C558E342087577F8DED927CG74EB45B7D2310F10561328E319CDAE7E8A21D784ED8D48D7B21D324873C22EC19FF532204DBE49F5F3212DE456DD5A59A174862FDECCB67C285D378957F24DAF292E170FED97ED8BD9469CD1C2EEFDF98734FBFBC6227BC3C63CAD24683A215BB494EB0BA694B3569627FA323333DF25E34761EBC9AF5AF70DD9414602FD505F
	55073C31847A24907A847ACDFD52467BB76D95B6AC9AD9CC56FCF5FBC69B6DBEEEDBF6DF5AC6B2FAF9697BFE1C3FADD2A346BD046B810F5BB29A1136DC6117367CD1C75D9550AFG5A6070179B482FB955D85D8A34771434F15FF6E9E3E0721B24A8B8605B1C41C67FA9EE5B5429340D79F77C3564B9169757A4B13679A0ABGFA8228219F7283488F544C7251EF3F8A6D4120FD4808478D7D20E9F6E96144F1A3CCB5786E6C84096075521011889342F36EBAF32707BD12347E1979064477B6C0E40EAC6D3D0958
	76F1DF19475E729EFC4B37C04C3128BF3D37157D68375F32586641B70E43711C7F87462F99C873F92C26999F7BADA04B9A10FE8E54CADB0E15DECB6A5EB86DAB7730A57B6EFF7B8A87388DA41036057B589AC39F6F847E7BC0C3C063C059C0798169636B6C5D7379788864F8C2D7343EE450D492F6D7D25542B4A1FC5D9E194B57DE4447F5FD62F15CAFB93B5DB1AA78D64053AF85B97BFC2B05BF56397C42571E4C38F64D690F67C62D9343B8BB93AD27F98CCB9C7E98636FF271F9EC7F424E96768F00EC899057
	EFB711DF71A157AFF6665947788DAEBE1F677B06BC4FADA04B8262715D12425AD7887D52B30D554F8335837901CC202CB3B2460F592F7F0F67284795DFBE760664335052F1BB219C51ADDB51C334DFC928CAD0239691BADFCF22DF03B3466532739D02B58ED31DA694DBB5F418DD09D3C8A60B6B5C115C02BA894A24FEB5CEE10138C9643CF7A56B713CAA16FD4A8CABB6CDEBF0798F13189323CABF4C889340776AB3682F5BD84DDD825F4B4FC8DF6D1FC0DE53A05437C169176E5127217DG121BEA1D1D5DEA88
	4DD092A919233FCB6018EE508F8C4AF379F8C066DF77269B494E8787BC730F553DC1D1A386E7675FC6B66FB2602F99147764A50940A7BC8F73D1C02C5B6D2C7165CFFD0DB251E20EF3BFAF1D44DCA5E7A19F01761E153E3EB05A42F330CB31284C3A9389C8989ACEFB1E6BCE0D611DC2C729EEF7D3136AE17830C44CAF992BCC67E344F0F265F246477AF4889246F278D70EED61FC18431D5DD27EC5A6D7ECBB218613B66D23362D6A5174786B19AD560C52E7093DFEE38A776A07BDEAC0D34EDEF70C573B726A18
	E10CA4CDBF1E98111F1FE77770D5201FF3D64646B2CAE744EA1A755372B263530E1D24741AAFDE34B5B0BF789A1DF83C4577A157772A0B36F62D3346A397F58D9E7B66D4665CFDA9651D3BBBD25E395B161ABBB75FCAF965A6176DCB6159B446F4D72EF725305E058CDD276C40B9C38847370F7BF19F2A2E5A83EAB8CA6DBEF502923261AC666CAAD6D3D741771A3332960E7DDD564256092F10659F3D69091732EFBC696BB847EB5131F369DA48EDBAB401E708EA9440B35CCFC95D667CA32CDE8B3E456DBB5C00
	BC092FCB18DEFF6BB3509E6DD5422A511534EDC3C759911B465FA032A6BDBC0DEBE8307682206CF3721CFEEF8265721C0AE2F44850B46E9EA25FA1ED167B9D12FB0E3FE38A215D88547D916A28498617CE43C7DCA71D9737CE56E12C6879A405AB940B4187B4A56F414927E7497A95AD193EDF6F1A440716662AA31C1FB7656D3BBB2730F60CB873FFE76AA90277A41F137BB2D741B94D049A07320B05D16C75EB9177DE2F00FD607BAC975A95C085EC2F3FF25EB408ABB86FC946FB9B7E7BC10944EA476B5CEF2E
	30F554B3BFD8BE8C09B75750EC8C7B6C851C578F7C70100CDB47343B3E9E34ACA324B288A17C726834BBF7B8EF5DF4953491B5E1811E8D53F1695B5FCF232D08B184DB7867F4953EBD1C371E9A0C37A5C763EDCA50EE23E1556E6CBC0AF9562FDAC9C5E36EA37C0D9531AA449C63C372ADF53B0DF14A057ECA20B5C0FC0D471EFD09FC9C173D8B539B8987FC07B09139BF275FF33F8F17EDFE47D71D411FC5420FCDCA5A1F9B606D45572DECEC14FB9BD6C61571B6EB8C6A0A581258C7FDD46EA32048FD9C0F61BE
	2E02FE8E50F2C5560101B75DF1DE3EE6FF46B8BFA46CD9274C0EF38E4175AA02009B02B24E717FECF145995EF8GBF23107DCE2D3DD04273DC7437DEA813F9CEE44D9F313C307E2E2040C6410F63FD0E45DFCD62DE83415998FDB763070333B17AB2DBE2F493E42B0392F7BED20CF521A384389CC8834A82DA9212FB3C69423C6F2FF74767F8FFB5C5E76FEF854C7DD0487B9D135E5F7DAC166BC2FC9F1B9D7E430C5F90CE73797E7A989F7BFBC1169F167BABAD413835D32803D46E0910CD2E3875982AEEBBB13B
	19E0ADB292EAD4551D5A48F87002E7179547C81FE567488FF30601B86FD146B3213F10CA4C32ABC91CD87F4B75FD37FD717144AFAE6A9A5CB76B0BB363098D455EF86249E2EFBCC1FCF3E3618ADFA63C417D7FBA2B89CB41774B1D180CB35EEA682FF5E27233983ECB1F00FEC324313A83A88C3490A897E8D5C46ABD4A748A21DF91ED2C5E8174BED08CE8A1D0CED46A1D0E1942A7F28E371FBAA219F39DE9559C7286E19B5A6A9D63F1E76DEA115733F566DAFB739CB22CD59827120C312FAC41B3B86E74B731FE
	CA0C29A757D8F84F5B10GAB2378DBECA132457DF54B985A9B0187B704304F24A1B29443F87D5941B2B5B1D103C5FBGDABE6FFBAC26F11DC2C1D2C379EC5CD946DB8F146368DD991D39BE3B5604ED0F90DC67182A5343785B288B1FA1B663BAB73520CE1FCD4D4CBADB3597D0A72618B453E44E1A41845EEBDDCA42D30B6BBCB57D1CCBE7F7F726399EBE0931C62C59C76144A8F0A5BB323C7D9857DE9AC9D0ABA661A661B870C5550D83B746789BEF17AAF1629B43DE72EEC7FE4B0817BC6048A7B5AF3966488F
	463D648B9CF9396E16AFD2391C5D05CC3EFB06FC19102FD565DD7F13B36E3B3E6E5AB61F3C87E767F0114A67AD9DCE5F759CDB345A95E2CC87484CE164755A6EF5318E3D8254864465D9B364E5AEB9376B1EED929BED3CA707E8373D17713773FCE1CF01E897CEB5135C7BFEDD42E3EBC7315DBE3ADD4C3BF84C3D5F0E2A6E0C5826E1044F37C3349BEEEE96E771172F3DCCBC7116472BC56248D32A44DFC3A372777B5830445D598E2FF0C4726EF3F837EA12D721C95EF8DC728EE892FFDEEDBF6FED171372DCEF
	E5D933371EE3B4DFE3D817EB0CEB929FFC8E2FF6D13FCE14ED43B3258CFBA25D23B0750960B020BC3F92A5327E3EE27FFF3F935FD5AE73A16F4E9BC87EE5CFE5444A399A0F57FD22ED19ECE6FCEE7F5AF22CD1A48E389468GD0GC8834A0ACB3C7305B23491119AEC6820EDAB2198FBF9481DE195C933319873394BAB51DF59B0CF1EB3D7C159BFB2798818BD89432489BB45396C15A87C74210AB62C0E4F3C478A63196FBEBEAEBE6C655F0D730EDBBF62B52ECE1FEF5C37B52FF12D730E1B0EFB0D6B18F71C21
	FB0DBBB647B89E1BEDD5D8EF06C0EF8AE8B1D09E50CADD46292ADC3C5DBC627DEEB8CA8F996C57F25B41F165F360387279FFE37FD3B93EDF0386540AB84EAF79A59538CF5BB099D360388ABC83G31E3B6D7957C5A8A4CB70DC6981B73EAAAF14F8E6663A9A5746BAB518778FB102AE86A84FBE27701229E65586FBC2BD1EB41A7C53A4403F7B2DE0D816745103CACD637F6C03F436115329A558DFD3F437B31A6478EB93CC5F1B9D6F3F81769D2EF5C1071F82DAA33FF5B2B66776F3EAA761B8178451FB205EF3A
	2A58DBA5923128934743D5F58E6F0855E3D8AAFA055F9D8B595EAD0A3F9F51B047CD572B32A6E69992B75D2F4E44CD8F303F23F3591706E74A3EDB1B16B5EBB3E5CDE88A615FEFEFC9FF1FFBBA7D7D197A74785B793A09A465FF6F1F7827866D5275244CA625D8241C268A31D68B03C8B6590C882D454D29E71D17DB98AC33A43792D0157C2E595D5D626A168B3044EB73AB975CF7EA51255BB25E2979A6BF272B4C593F975C3B077DDEF03E313A753C4C0197763CC6B25FAFFDF1C553ECC59BB92807712E69CA59
	B47DB6FAE08F4EEFC99DA164321F6FC13B07DDE37E8BD0CB87886CC4EFB2EC8CGG0CA1GGD0CB818294G94G88G88GB600D0B06CC4EFB2EC8CGG0CA1GG8CGGGGGGGGGGGGGGGGGE2F5E9ECE4E5F2A0E4E1F4E1D0CB8586GGGG81G81GBAGGG268CGGGG
**end of data**/
}

/**
 * Insert the method's description here.
 * Creation date: (6/11/2002 5:18:21 PM)
 * @return javax.swing.ButtonGroup
 */
public javax.swing.ButtonGroup getButtonGroup()
{
	if( graphViewButtonGroup == null)
		graphViewButtonGroup = new javax.swing.ButtonGroup();
	return graphViewButtonGroup;
}
/**
 * Return the LineGraphRadioButtonItem property value.
 * @return javax.swing.JRadioButtonMenuItem
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
public javax.swing.JRadioButtonMenuItem getLineGraphRadioButtonItem() {
	if (ivjLineGraphRadioButtonItem == null) {
		try {
			ivjLineGraphRadioButtonItem = new javax.swing.JRadioButtonMenuItem();
			ivjLineGraphRadioButtonItem.setName("LineGraphRadioButtonItem");
			ivjLineGraphRadioButtonItem.setSelected(true);
			ivjLineGraphRadioButtonItem.setText("Line Graph");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjLineGraphRadioButtonItem;
}
/**
 * Return the RefreshMenuItem property value.
 * @return javax.swing.JMenuItem
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
public javax.swing.JMenuItem getRefreshMenuItem() {
	if (ivjRefreshMenuItem == null) {
		try {
			ivjRefreshMenuItem = new javax.swing.JMenuItem();
			ivjRefreshMenuItem.setName("RefreshMenuItem");
			ivjRefreshMenuItem.setText("Refresh");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjRefreshMenuItem;
}
/**
 * Return the ShapeLineGraphRadioButtonItem property value.
 * @return javax.swing.JRadioButtonMenuItem
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
public javax.swing.JRadioButtonMenuItem getShapeLineGraphRadioButtonItem() {
	if (ivjShapeLineGraphRadioButtonItem == null) {
		try {
			ivjShapeLineGraphRadioButtonItem = new javax.swing.JRadioButtonMenuItem();
			ivjShapeLineGraphRadioButtonItem.setName("ShapeLineGraphRadioButtonItem");
			ivjShapeLineGraphRadioButtonItem.setText("Line & Shapes Graph");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjShapeLineGraphRadioButtonItem;
}
/**
 * Return the StepGraphRadioButtonItem property value.
 * @return javax.swing.JRadioButtonMenuItem
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
public javax.swing.JRadioButtonMenuItem getStepGraphRadioButtonItem() {
	if (ivjStepGraphRadioButtonItem == null) {
		try {
			ivjStepGraphRadioButtonItem = new javax.swing.JRadioButtonMenuItem();
			ivjStepGraphRadioButtonItem.setName("StepGraphRadioButtonItem");
			ivjStepGraphRadioButtonItem.setText("Step Line Graph");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjStepGraphRadioButtonItem;
}
/**
 * Return the ViewSeparator property value.
 * @return javax.swing.JSeparator
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JSeparator getViewSeparator() {
	if (ivjViewSeparator == null) {
		try {
			ivjViewSeparator = new javax.swing.JSeparator();
			ivjViewSeparator.setName("ViewSeparator");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjViewSeparator;
}
/**
 * Called whenever the part throws an exception.
 * @param exception java.lang.Throwable
 */
private void handleException(java.lang.Throwable exception) {

	/* Uncomment the following lines to print uncaught exceptions to stdout */
	// com.cannontech.clientutils.CTILogger.info("--------- UNCAUGHT EXCEPTION ---------");
	// exception.printStackTrace(System.out);
}
/**
 * This method was created in VisualAge.
 */
private void initialize() {
	try {
		// user code begin {1}
		getButtonGroup().add(getLineGraphRadioButtonItem());
		getButtonGroup().add(getStepGraphRadioButtonItem());
		getButtonGroup().add(getShapeLineGraphRadioButtonItem());
		getButtonGroup().add(getBarGraphRadioButtonItem());
		getButtonGroup().add(getBarGraph3DRadioButtonItem());
		// user code end
		setName("ViewMenu");
		setMnemonic('v');
		setText("View");
		add(getLineGraphRadioButtonItem());
		add(getStepGraphRadioButtonItem());
		add(getShapeLineGraphRadioButtonItem());
		add(getBarGraphRadioButtonItem());
		add(getBarGraph3DRadioButtonItem());
		add(getViewSeparator());
		add(getRefreshMenuItem());
	} catch (java.lang.Throwable ivjExc) {
		handleException(ivjExc);
	}
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
		ViewMenu aViewMenu;
		aViewMenu = new ViewMenu();
		frame.setContentPane(aViewMenu);
		frame.setSize(aViewMenu.getSize());
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
		System.err.println("Exception occurred in main() of javax.swing.JMenu");
		exception.printStackTrace(System.out);
	}
}
}
