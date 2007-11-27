package com.cannontech.dbeditor.wizard.state;
import com.cannontech.common.editor.PropertyPanelEvent;
import com.cannontech.common.gui.image.ImageChooser;
import com.cannontech.database.data.lite.LiteFactory;
import com.cannontech.database.data.lite.LiteYukonImage;
import com.cannontech.yukon.IDatabaseCache;

/**
 * This type was created in VisualAge.
 */
public class YukonImagePanel extends com.cannontech.common.gui.util.DataInputPanel implements com.cannontech.common.gui.util.CTICallBack, java.awt.event.ActionListener, javax.swing.event.ListSelectionListener, javax.swing.event.PopupMenuListener {

   class ImageMouseAdapter extends com.cannontech.clientutils.popup.PopUpMenuShower //java.awt.event.MouseAdapter
   {
      public ImageMouseAdapter( javax.swing.JPopupMenu popupMenu ) 
      {
         super(popupMenu);
      }
      
      public void mousePressed(java.awt.event.MouseEvent me) 
      {
         javax.swing.JLabel imageLabel = (javax.swing.JLabel)me.getSource();
         
         if( me.isControlDown() )
            unselectLabel( imageLabel );
         else
            selectLabel(imageLabel, false);
      }
   }
   public static final int CANCEL_OPTION = 0;
   public static final int OK_OPTION = 1;
   private int retResult = CANCEL_OPTION;
   private static final String ALL_CATEGORIES_STRING = "(All Images)";
   // Set up some borders to (re)-use
   private static javax.swing.border.Border backgroundBorder =
      new javax.swing.border.MatteBorder(4, 4, 4, 4, java.awt.Color.white);
   private static javax.swing.border.Border highlightBorder =
      new javax.swing.border.MatteBorder(4, 4, 4, 4, java.awt.Color.red);
   private static javax.swing.border.Border unselectedBorder =
      new javax.swing.border.CompoundBorder(backgroundBorder, backgroundBorder);
   private static javax.swing.border.Border selectedBorder =
      new javax.swing.border.CompoundBorder(highlightBorder, backgroundBorder);
   private ImagePopup imagePopupMenu = null;
   // currently selected JLabel
   private javax.swing.JLabel selectedLabel;
	private javax.swing.JButton ivjJButtonAddImages = null;
	private javax.swing.JButton ivjJButtonCancel = null;
	private javax.swing.JButton ivjJButtonOk = null;
	private javax.swing.JPanel ivjJPanelButtons = null;
	private java.awt.FlowLayout ivjJPanelButtonsFlowLayout = null;
	private javax.swing.JScrollPane ivjJScrollPaneImages = null;
	private javax.swing.JPanel ivjJPanelImages = null;
	private java.awt.FlowLayout ivjJPanelImagesFlowLayout = null;
	private javax.swing.JList ivjJListCategories = null;
	private javax.swing.JLabel ivjJLabelCategory = null;
	private javax.swing.JScrollPane ivjJScrollPaneCategory = null;

/**
 * Constructor
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
public YukonImagePanel() {
	super();
	initialize();
}

/**
 * Insert the method's description here.
 * Creation date: (8/21/2002 11:41:09 AM)
 * @param images java.awt.Image[]
 */
public YukonImagePanel(LiteYukonImage[] images) 
{
	this();
   setUpImages( images );
}


/**
 * Method to handle events for the ActionListener interface.
 * @param e java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
public void actionPerformed(java.awt.event.ActionEvent e) {
	// user code begin {1}
	// user code end
	if (e.getSource() == getJButtonAddImages()) 
		connEtoC1(e);
	if (e.getSource() == getJButtonOk()) 
		connEtoC2(e);
	if (e.getSource() == getJButtonCancel()) 
		connEtoC3(e);
	// user code begin {2}
	// user code end
}


/**
 * connEtoC1:  (JButtonAddImages.action.actionPerformed(java.awt.event.ActionEvent) --> YukonImagePanel.jButtonAddImages_ActionPerformed(Ljava.awt.event.ActionEvent;)V)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC1(java.awt.event.ActionEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.jButtonAddImages_ActionPerformed(arg1);
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}


/**
 * connEtoC2:  (JButtonOk.action.actionPerformed(java.awt.event.ActionEvent) --> YukonImagePanel.jButtonOk_ActionPerformed(Ljava.awt.event.ActionEvent;)V)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC2(java.awt.event.ActionEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.jButtonOk_ActionPerformed(arg1);
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}


/**
 * connEtoC3:  (JButtonCancel.action.actionPerformed(java.awt.event.ActionEvent) --> YukonImagePanel.jButtonCancel_ActionPerformed(Ljava.awt.event.ActionEvent;)V)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC3(java.awt.event.ActionEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.jButtonCancel_ActionPerformed(arg1);
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}


/**
 * connEtoC5:  (JListCategories.listSelection.valueChanged(javax.swing.event.ListSelectionEvent) --> YukonImagePanel.jListCategories_ValueChanged(Ljavax.swing.event.ListSelectionEvent;)V)
 * @param arg1 javax.swing.event.ListSelectionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC5(javax.swing.event.ListSelectionEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.jListCategories_ValueChanged(arg1);
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}


public void ctiCallBackAction( java.beans.PropertyChangeEvent pEvent )
{
   if( pEvent.getSource() == getImagePopupMenu() )
   {   	

		if( "DeleteChange".equalsIgnoreCase(pEvent.getPropertyName()) )
		{
			fireInputDataPanelEvent( 
				new PropertyPanelEvent(
						this,
						PropertyPanelEvent.EVENT_DB_DELETE,
						LiteFactory.createDBPersistent( (LiteYukonImage)pEvent.getNewValue()) ) );	

			setUpImages(null);
			//setSelectedLiteYukonImage( null );
			unselectLabel( selectedLabel );
		}
      else if( "RefreshPanelWithLite".equalsIgnoreCase(pEvent.getPropertyName()) )
      {         
			fireInputDataPanelEvent( 
				new PropertyPanelEvent(
						this,
						PropertyPanelEvent.EVENT_DB_UPDATE,
						LiteFactory.createDBPersistent( (LiteYukonImage)pEvent.getNewValue()) ) );

			setUpImages(null);      
         setSelectedLiteYukonImage( (LiteYukonImage)pEvent.getNewValue() );
      }
      
   }

}


/* Override me to do things when this panel is dismissed */
public void disposePanel()
{
   
}


/**
 * Insert the method's description here.
 * Creation date: (8/21/2002 11:41:09 AM)
 */
private void doImagePanelLayout()
{
   getJPanelImages().revalidate();
   getJPanelImages().repaint();         
   
   javax.swing.SwingUtilities.invokeLater( new Runnable()
   {   
      public void run()
      {
         java.awt.Component currComp = null;
         int currPoint = 0;
         int nmembers = getJPanelImages().getComponentCount();
         
         //find the lowest component so we can set our scrollbar correctly
         for (int i = 0 ; i < nmembers ; i++) 
         {                        
            java.awt.Component m = getJPanelImages().getComponent(i);         
            int mPoint = (int)m.getLocation().getY() + m.getHeight();
            
            if( m.isVisible()
                && (currComp == null
                    || mPoint >= currPoint) ) 
            {
               currComp = m;            
               currPoint = mPoint;
            }
            
         }
         

         if( currComp != null )
            getJPanelImages().setPreferredSize( new java.awt.Dimension(
                  (int)getJPanelImages().getPreferredSize().getWidth(),
                  currPoint + getJPanelImagesFlowLayout().getHgap() ) );

         getJPanelImages().revalidate();
         getJPanelImages().repaint();
      }
   });

   
}


/**
 * 
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private static void getBuilderData() {
/*V1.1
**start of data**
	D0CB838494G88G88G7AD39BADGGGGGGGGGGGG8CGGGE2F5E9ECE4E5F2A0E4E1F4E135BC8DD8D45715E6EB7239DB37B5DD7A2D3536250D491254CDECEB37EEEBF7CDE3D21350A6EDCDCBDB13124434E457A45A5AAD1B1275B2030A89FF7E569FB0B49A85459194FFC2C864CF84B38A4A0F98C1F84CBC188773674CC386C459F36EFD674DE3188131516FBB3EF74F3D775CF34FBD771CF34FFD835B74F6746AE453A64612D7B36D5F31E446324FB0F67D78DD8A61D28B1BD219615FDF8156B3E9
	B8851EDB214EBA54141A45D655E02382AC871AD27F826F9B5994E5D38D3CC498E759A946CC779DDCB076B873AE51B8EBF9D9C9CB03E786C0A1609A81228BF17CBB5236EA78G14B334F704E493E3ADC55866ABEC2B813FD4EBD7GF5E5GC93C4D4C34EC2DBE9A4A58E69EBCE30BC45B2570DC47393B336438562E756EE8367209ECD3BA0EE520CFD0F5C874F5E84FDCB60B8B424CF627174033343E377BC31B47E531C9EE37472D4AB607455EA95B9555633374AB03124FEE712B12AA1714DC6C6B7538EBDDD237
	DCAF39E5A7E3F1071067C7D9AA2787BCC359FE98F19FA4231C32613D9840FA18787EC9FA2946D7D4EFB40B3FA7B3A25FB107853F310703FC57B72ED8B3D44C47646B007D5220DC85A0F8C9CFA7DE969493AF7B0630EDA276BB52141AF70470ABB83E8C4AADG5EA3446387FB094716EB8F3121C54F19B3A370B8650860ED4E11A00F8B2BD72C5946F5252A8D57FB9954E59DEDCA5D8DD08360G188230036B682ACB7B60D95E29D6FADCAE0F3B5C6B2DF05AFDE7BCF659896FA5A5D023614E4BB60F4F4E185043A7
	D8C198FE88220F0E54C184B1378B0C74E676797B18337FB76655E166B677191D26399A0DC5C703F3CBB906F27B86DF5B8440A7EBF4857EBFB83E66D890AF562A126331EC073AC547C84EAD99440BB5E5864BAAAAB72D8D434B749F9CB5959C97BCD49D8F72A2E43C339DE56C857CD400D9G738116GA49DA79957369E9ECF46352077BE3764EC686B74CABE3522CFF15AE51FA66BA7534612F5B8907DFEB961FEC2AEB1070DEB3464DBE5E16532A466DD53567704BC8A5F8B4AC50C3DA1BD5258756F0D560D8CFD
	8D3F4C77047DBD3106E69D9F43712A81AF5636106331BC996A92GC4FB79BA622DA521BA6263F8C7C9280E7CE908F4E4B254A5G08751D5F0176507ABE6C9B00C9G53816681AC86C8F81F56B82A7560CD6E23FA437A0A3EE75A3640D38E28BE29568D264EED131BA51FA2F5BAE5BF535AB43721DCBBC7743B4477378D46B8A53BE51F24AA9EB7D017DCB2A813DF3431372D47B6BE1957B6AAAE9986F0F99951F536ED0427D3722BCDDEBB9859E08BD12F3621CC2ECA4DC09188407B71D21457735C6626427B6ED2
	12554B926206209CD5CAF2F92DED83BCBF8265161DA5A5D50A8D5910FC8324235FC94EC20D0376759FB425AE831876A16D4FD7DBC88F2B526FE5854F1C882B07FC7F1E90BAE5BF912A078BB97D29001F8D908B306C8479525A6416B176C23D468B7206FCCDE49F8577E06718E32C56F44E2C351F6898C26E97EC283BE9E560DBGEA4AC8767BAE678A1D2C107CB2E9E11D8F94C83687D7C2345D5B0EBEC63EAA3B55AA59AB3B6D706257F440523EAE281FDD9EDDF70AB99E6DD5A5A810DF4717342F97F820E154F6
	2A2F6075122A7A144EBED5EE10D5D5F1F7877B1F663C78C718C23FB6570BF61CEB874CB1GB0C51F6B4F5AB79A744C6A717476F99BF187E97A1A4F7D72CCE8BF370C56FF569046D6F1FA79C1DE8E569F9D9AEB6D88307FC457AD289FF5B7B5067996688747683D0EFB006CA6691C288B703AA418436EF20813G54F21A7F9B1CB7E3BF72913742CBF85E36DCEFB150DF7B890FA1F463310E483A7D230E703A7D330E703A7D4B0E31F577790EF03AAB6A92BAF00E1EFE37E1AF3C5201765166F13BE53EA175AECC44
	482FB662BC943722DED06C5D325A208C4A0CADACC71DCE64B6F8A63C4FA9A71B593F0BEC666E412F32856D5BCD61622B69773EE7CA2D90FA1BDD9134190227D7A55CE332D3F60120AABDFDEED560D7F27B2E617DE60E139A3F08FD73CED77429AA78BD3B1D47677E8209CF47A29E75322F4B63F349763C9E692AE4117AD58BDF8ECBB92FBE096F7939878EFC189B926297740450EDAF8FA1E80DCC717D7C667CC2A6787C55DF7F5148E7DD6FA74CE0DD6FC4B9AB52749EECEF6E670D3CD5E2G607C047993C4A74A
	63A11D47787487850FD6452FD6C220506D71A9304CCEA8B600CA88E62FCA4EBE3952A1393BB5CE8396FFBF6C8F0D3A5558F8AC1EC78E525EECA4EB3D993A48FCB1794729B26AF3A968F9BD002302FC0637936B49E7E80E3152635488C1DDFC855FE7491B8CFBE7D205B8F74742D3548F18425567D5D27D6BE16BCB757AEB66F075751544E71B8DED34DF3E52A743A222D37BC89E20784E36A1240E8BCB0F9B1D36376069B478C301EF37075F63329DFDE02FCEDF312FE1987F891A42A64D111026572738A0FCC087
	2F65974A520D7985BB700F3908D9701C87A0746879F7B743B33860A779B9DFBB4E3328AA24B37BBA65A01E06FE89G29D502D6CD1530D5221D7786350B07F2E2D5305D561346F63B0729DDAB60551341F6D1A35A45696D16GFEA54026EA512E23DA341BDB0D6DB213690CFFD0476D6138BE905627D2689CF5A64B18A3D9BE43995696A3A4EA7453CE051EAF9F6E62F9A540171EA2FDF851EE0CF54A7DFE0FCD61613D36361F130DF6DA607ED9FE931EDD0A4F8FE7CD3B9CA07D78120C3CE27C2F2135B57E7A7E8F
	5868730CD8AD8AA5AB7B79DEEB74D442912486D42CD2F7E34D0A5C4FA8BFF350C4722ABFC5FE6AFA35B18FB3BFF32F692D8832192249A446A01B566A95EB846DB9BA6D04D31403A9B293AE3706F05F54F1AD0093F37CC2891E4B43651FECFC52166EBE455227AAF8F011D4088F3CFD2A50D4C173819ED3A22DE9B5145379F9966E2BC5D00E87C88490674BE77839935BE655029E57525E4F4A9F3177819F8390DBCB7B2574865A60097B82DA475FFEF8821EFF2397557479EB9B4BB4BEE39F1C41CA07D3CD6172D2
	D3B2B71862EB45DA251E8E2E596E1F7618163FC67933AF5CA0F9B4C21B2927C99E157B4249E361E9DA3315D547037319305395E31F9926316300EE56C7B4764F8A420DDD73910D5D231486473EA507AA46CFE6B4FEAB500E52471FB1E89CBF96704B81B6D9C9979C8C657F37FADCB117BBF3CE62DCBE89C74B745C5EA1233DDB903FA7223D4B338A3DA835065A3BFB9D3827DAG6F34123D5B5A355EE06F4E436840A0F8B2BD3E6FC279004F524ACFF46138DCB20C4E994526096442A6D9A927710202EB928565B9
	G0BG62815899626DDC7738393D1301B1F2FB5F1E90DF39E7743329F9A27D0A75FE5F19D03F9A3D5FC32372F92DE70267DE6F1951E7E011EFBB3D9EF5FD721950BC5C850E0FBE931A07DB12C5F93879D017FB16467F75755071A71F9DB9E6F4C8D974FBF9D43F1DA16505A17D447E48EC4075673E41308DCACA34981D511D4A5D6B0DB14F1233A23F406AE05F56116FB9151F2A71D3D0C7FAF53C97E38DBB14A781CC87180B6D81D682EC2DA77D0A6E8E6707C76AD7A4A79C4C3B88D9FC65462D4970DFC7758B15
	61717A117D443C6377533C6B6BE99D9B06EF0D87E9D43FD09E260465614D8344C3344E435AD1F797B7474366D17DC2F9082B8F27CBCF667FA53CAE21B9557428A64F28C74B6B059EE51FEBCAADBCC7FA7413C10AE1EA4E119E851C081B8465E967C8E75E6846BBAD3217759E452DB232158FB8B01E87EB5F2D388977904759BCBE1E189079320FF8DC958DB4676BF47FCB091CC7D921044F237CC9891FC7F9C399BBC7383AA7D21EC570972B7377B400401DB193EE290E7B310ECB54F10F7138AD6DBC581773043B
	A793F1D5D0EEBDCFFAD23F9E4FDD1320BC93E0BEC01CDE57466BB0BEC884DCF6037055226E052CF0B93DB80DC6D8FF230DF1B3B6D90C716730E3D0DEAFDCBFE38EEFBC48616D9F5F124957486F177D5C48E0942C557DCE5D4C42642D4ECAAE19D14EE0430DF5DA1B132A2732C85F8FFF485F9AFEBF7060DA687A636BE95F16B6D0ECFDA10F5F81C1F9AA402C861237B5BFF4DF4E073A279B34BB6B0651F25572503D11753936B73CBE5775625E72FB7AFCB6B9D4E71BFB376AF5C1ABDC2B4AAE940BF66E667DD549
	57AD0F5279AE5EDF54C56A6F690D34AF044CFFBEC26607F4FDF9E9FF280C92C1AEE90DDA7C55B8DAC6025EBF8C9B699D5669E59E8825D78CB4EAB4BA2D9169FD480C740E68BA51FDB2A73CCEB458FC9E2793B50311CD1F14C7F6505BC83A7152G6AC68C1497G44B512EE7CFCF02CF8E7F998BE051C2FBBB1AE896865BBF8F9C067D7AE4F50576A245B2E99D9118B686436295C44583DE6423D48F15FG5CC99DE76736698FD03E2063DE6238C781FA4DA433E75351BEBB2581CF9F0C61ED42F6AB75DCCB8A1433
	812AGDA1A045F90BCFE239F4F54DDBE498522EC704AB6E6ED463E5F4D243E7320FDAC40B200B428ABE826334614CC54D331A3C548935431C757238E9AD6C69CAA757ABB3A1176E92B54A9BB354345G55BD36ED63483EFA8361875FB8904E8FC681DFB14DF4CE72E6984FC98B1AC51B254D3466BFDEEFDC73C44027DD882EF36E05C8316A135C3E94DF90B169DA9D7F8B0E379A70A2D6ED63F8AC5B21EE518572A5B69E6F9534B425163590EEB647B5C2592E63566FA31F6355F1C97B29EF140EABAB203EB1BA6E
	2B8554F72E0E5BF3007A4669381E8354F7190EFB72G75CDECA1195D19B756BE493E98C9E69787D1860597C36F6BED9CDFF6B1743E7E3F87683E3E9E6A66DEA41EDE99A41E36FECCB9136C8C7A462694F06A472457C57CCEDE3795F85AAEA9B965D36C95D237156F92E1A76C9929DA7F599F131D789C2739944AC9G1BAED1BEAA300772D1336F1B412C134A42FE97945F3857D4F0C948227ED2706C9A27672362B3E94C29D03F709262B2320C7A0AF3DF877FD960A96A4BC654635EC2DB982C770D28C7FE3FB422
	7F2C9175A823FC2D3F185B136F525C121E0CE14EEFDBA2FE7314562AFD4F21BD37566CE2E9BBFE2F4D6D0805FC3223156479159CFE0E0372BC0027DBE90D7E50954147F32BA0687DCB8651CBE8A5BDF8097B7D5C36265462B64265E4D33C562263FE13C37AB254C6BA7C933E9FE3211CG40AE937ED90E47BCB26608B17FAB70363C1B39FBA5C01A617031A1F89CB798278DCDE80CC871D9687D6FDA83FEA39B6436456516610028588AAE73F3A947BD8210FD19EC7AA379185BD7A12871DE46EF8D6350E433393C
	FD5E3E2DE370A5E83E4BED4EEFD00F20ACE87CD98D5218D7B0BE0DBAEEFBFE086B59CEB48EDF8D52F86140D8B4445A4F4C23354FEE2735EC9F4439B97532424BAB7535DEB1F0336B00B4EEF63D0C6B311E3DB9F833E35C4AFD3E705DA957C8BED3004F19G738116GA469F3FFA763563E97AB3C45FE4A047B899FE14F645FE8F6889F112C63DD9C1FE7408BFFB0DD778745D0B73D034E40DB7AD1A6C9D05EAA41F99920864081B0D922336B9AD7A19BA70F526871DA61B060B49CC73558B973563E23FBF8AD595D
	395F7D9A1BF7572B49116EB826CBC26766E94FB9553BB8DE4C6F7EG4EAF816AD2BA1BD273G4AG9A811C1DB43F332E82B1BF3C22A9D7D549666037BFB4B93C108922B19894F3AB31505CA6812D284E113A9F5399496FEF5C09EBB13FB3546F67F0FCECE7285F3FB7077C7E92645FC63A7A43913AB34B2649C1FB465AA21DA31256E3CC5C6971718314080937EC273D918F7D92810A6D208BF6114797ED2A3329CD876083G51GF335B6CC46B67F13D3B5465A5237DA2B3577D8689B8F703606DB896F1B13035F
	4DECE459EE4C9504FEA1A16AFED03CAED8D7572B7729D05303F8F1572057456463F32F34D7533368FEE35E7D781B8591736EB532F6BE531E1387F6E9B41E3AE42411B5E30BA9B9820DA82DEF349C0CFF840DC5ED46B8A3F77F7FA6070BB3925BF61B96EBFD938DB482FF7F7BB55A1DD7811F1FE72FB65F2E833CEC5B4B447AFCABEF3C752135C1A86882FD8358252DDBA33C5F834F93A61267BB1D1887887932C1BBEB6E3BC6F7FC9DDD944FF5DE8B0D678EA79B633128EB21719C4AA0D87F583550F86EC7A36A93
	2F0546F354DF6C199EE4D7FB779B5E3F1CB111D8E322205D9F5C56B104ADFB3BBFF2DEE6CFFF783CCCC1FF78BC63217E313F57AA690F7C3DD6F93F71FBADE10B9770BB3A69208BF38162G168130EE3243CF753F43389D8EE303CF5A3BB1AF04DF782AB47EB78311677A6F01713F8B7EC1G6F75BBBDE0A6DCC81F700B8238DE2A474B115A37EE811411CF69F62822A9FD6B96C09DF74ADD9CADE22CF3BC564F06F915F5D34C7DG47B5C2F9C847259C24D8ECDAB75974EDBBB1760A574B3BF8B9D1AFFFBDA71C8E
	E17BCFCECF97069D63D63F238C85B177677879A55B5114DA60A0F9FC4E0F38B2A8576B38BF79755C32034E36AB3713BF1C8D38258EBAB7FD7FCD0CD3EE6A1B04C6C5F54A955C838ADBD53A034E1E598A1D4FFAF660B80DD0F682CCD6E8FCF7B645D2B38137D821718F9F9AF16FA54E5655CECF3FD5CBBFE9F6715F32A9871756C3E395F09FDF8565D6G35C778C44C7798FF58948EA74E81F78E613B70A54F2711AF497E7A97D955AFDECD5E45427B2329BDC2EF167484FD49D470A5E23F8EDC47F1B675C2DC58CB
	FB54BE309F1E4DD24F682FDAGD96F73F8E51FBAA0307CC6DC5B7B6A8D266F1987865E0DC483F67AE990DDDDE70FECD39B25EEC63663534398FB54839FCE00E9GB1G7381966B3C3DB610B786EFE291388D71C90ADB1D18FC23D0FC5A7EB8F84D98F7A54078C93DE36FBF4D2E8CC636D7CF8D0EEF2F62F81EC786BB53B86055F25A4F8E72BB292EAE3F2C5B45978633F4DC2D3F5E63D7B0972EFD87E1417B342B30C80ACDF20AD4E205642B77B8955B00F64E7C9437D587G321CF47EFD58026B60G77BE88FBEA
	F4CFBA23BE4B7B1663DAB9A9A65CF1F02C18E3B59B99F3CC03FE33GB6EB65F970BEECB2469C33F2F12F087C2A0887C57E33F1E768FDB25EAFAD047EF17ABC7E180176348B66A078FD0B671C16C3FD125E66FE7F48337EF7CCDC9178DCB2547162A7A39438403E39C47C048AD4866F78CD519B2BD62D923A747A76714EC7D6D7387B54549D63758B3DDF957D4A476D978A62EC729C37058ED768F973F79C2F3AC24F1B5E9DE4A3A7C15DD29779AFB57B564E49FFF70B7D3EB7EE3F9A77C8790AF9C5F3FB5C6A8E1D
	6F7D9C6FF0074E77D08E4DD7053AB8B71D2F6FB5233E14FA1AD2DBG06G22GE2G96GAC86C8GC882586485DD81A883E884F0G84G2682C4FB4946BDB1047949C8B6CECB64860D9CA31BF063BA4ECD0F6504E9566A6E3C0EF199FEA72FC8CEE510FF8E59G8D5D5D5A6F3306A35B2DAF8E0FEF37E68D070F339E989E9DE7BDB49CBE4E7A767068B82B1A1FAF6602EC708C205D3DF3DCAC6070DCA176738C8B5E1F9A3F48B25C21D81ACF1EEF2C2DAC3736B7D41E2F33DAAB4A4F3717DBAF14DFECE054FF2565
	70987DEB6A4E57FEDCF736B19C05B07E24AC9CCE5819BB78EF1CC50CB0F5A756E34EA341CBB1427753D147F22FC0EC84D0FF05F2001FC571EA3C5E6547985AAD77676A9F53D503314BFB478EEB6B69FE27A03730A03728B8FFACDF77CD9CD63B2BDE30B11D6B06D3F14B156257F27A5EDA0ACB233D3FE2F83F3A63761F25BEFBDB47903A74D39E27B9C04E01ABE43F6E4B894E73A1437B7130B173A7BB67F337F58C6D5CE40E3C579718475F6B0F1B436F75274C23777AE2F3783D7EAB33F12F8B1C782DF3B75837
	963D5DD29DF7D1473D4CE9A26E02E2D79DBA7EB54E9762EBE49C1070FF64FC75F3DC0B06FB5D003B2861D619B14E02A9C388C4FDD3D2F8AC21B81D221C1A02ED94B704DD3ABC52D310C745AB99785E206310EF917BC8FB5218EE03B96B821F9DC88F6209F5CD9F6E5EC93EED6A95AD2FF4A5684B66427BA200A5861C70E1C5BC973C7CCA28EFAB617824AB213E6D11F472ED29BE083FFD94A73D1C0D73F7F4CBDE91CB2D627B856D4A53BEBA3F45F0BB968F65A49DD73B0D6E24327C044B7650B92FD8471D72726F
	D9216C55F1530FA2EEB214E77AE9CF9E4B886E43B2437B636937FFCF3EF0DB4790F25E120381E85191AA7F67CE54892FCF6E12FDBE59AEFE5626455FF1DC5EF3C0B6737D143775ED2338B841AF7236CC05F883G738A62EE6430075A94G3E8C2083E092C034CA6BFD590B0E41A58594D71F0B0FAA46AC65E7748550AED6EF7B50D15E9694F7E45B9F7275CB02F629FD643F3663F4689B17ED39AC44FFD5A9AE590DBF204EABBA0A4E6BD0E13E504B995B74BB8D20656C23EF7DDE58C6F947D9FD64BB95B736DD82
	6515GE9D7296DC58F355DFD15F214F7FBC2F314AB18B1477808E76C9C659FBD21B94A7291B94E3D5EC8B9CAA10F160C20BC269E9D654FC364F1785031234772296F354CE05F68741B1125506368ED3796DFBFF80B7DFEB7E63FD6D88B6F55F0F17933FC9DA7DF8D35DD3F6578E9D7C3ED17472D67EA200E75134D386CB10EEF87FC20FF6CB33C30BF7F47F5EBAA341D59CFB629FABB4589F3F47AAF3A19BE57E50677BE776D37C91F392DE308B977F9A876361989B76821583BDBBF2BEF5E0EBE622A6EA376F3D9
	611ED98832526C071BF00B7B49FED4EC47B5BB2E6BF2031B4A626F836D3A7EFB28187D8ADB2D324CF23BBD17A782727C79960BC5E5263ADE152D91D7BFFE9F4B54753F16BB67782B07CB4CAF5EE3B63114DA36C684AB35AC85A30EDA361A87A4354C8C41CAG7F5883A5A850391B7DD7588D4E1209B028DEAADBC75FE2150CE8E156DAA5839769A3A272C73FFA6437314FB57B4DECCDAD5B0689098659474FDF329D3F95557EE8C4B5DB67456F635004820BFE7ED86DD7871C3215ED6ABA0FBFDE742B323D1CCEF6
	FE15ED663FBEB908194E03B5125F21E204763E152538714FDB308D225AA9393B8F0AE35E7BFC1E49659D2827FC1199703B11730BD4ACBA150D5F4BFC772F27174EB3B3D3B5FB5369116C55128D8E2035EC1D6A7049FE0747E98FF8CF14F1C2297DF406B183399D7A74F13216C608A3F526FB83DF1A1A1E10DE8DA28B7858C15E9273AF963D053D52A179E0A859D72B621F6B7078F66BF5F76CD4777C6854DFDACDACD91BA4AE411B81AF68C01764D21C8370D2454FBFFCF1B2E039A006F57B71F7C740D9EE88E7D4
	258FE07927478BBFFF7F6B9F738102DDD5763690A5F4BD58ADFBDC874B3B3A601C2493G3982799C1DBC36310434599038FB7369E7D6F5029ED4B1332B30107FDF447FBF047F97F1CC9147949D8261AEE7E37E8B2D8F501C3972683B81175FD28DAB287E6A15473F766B52654BE0F655ECB57F698FACC2D6305559BED7276C3BEB7667F35E0A0A7FB42E1197D5A500F4B784F4FDE71A2E23B7DF8363EC986DF54ABF734BDF4CEE2F5AE4E2A658F88ED96C34557D986FF29D440FC623B945DC4606D799CB220EF25C
	794346BB5744BB53A35E5BCE9B92F6FD56D0686F257C7C6C9B8F7824A14A2F448CE35EB3D2EE3A41A5B91D2A646CADF75BB1CFDDB1204A417B30G9F4FCFED34CAD1B74C796EB17479FFD0CB8788176EF651309AGGC4CDGGD0CB818294G94G88G88G7AD39BAD176EF651309AGGC4CDGG8CGGGGGGGGGGGGGGGGGE2F5E9ECE4E5F2A0E4E1F4E1D0CB8586GGGG81G81GBAGGG6A9AGGGG
**end of data**/
}


	/**
	 * Returns the imagePopupMenu.
	 * @return ImagePopup
	 */
	private ImagePopup getImagePopupMenu()
	{
      if( imagePopupMenu == null )
      {
         imagePopupMenu = new ImagePopup(this);
         
      }
      
		return imagePopupMenu;
	}


/**
 * Return the JButtonAddImages property value.
 * @return javax.swing.JButton
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JButton getJButtonAddImages() {
	if (ivjJButtonAddImages == null) {
		try {
			ivjJButtonAddImages = new javax.swing.JButton();
			ivjJButtonAddImages.setName("JButtonAddImages");
			ivjJButtonAddImages.setMnemonic('a');
			ivjJButtonAddImages.setText("Add Image(s)...");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJButtonAddImages;
}


/**
 * Return the JButtonCancel property value.
 * @return javax.swing.JButton
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JButton getJButtonCancel() {
	if (ivjJButtonCancel == null) {
		try {
			ivjJButtonCancel = new javax.swing.JButton();
			ivjJButtonCancel.setName("JButtonCancel");
			ivjJButtonCancel.setMnemonic('c');
			ivjJButtonCancel.setText("Cancel");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJButtonCancel;
}


/**
 * Return the JButtonOk property value.
 * @return javax.swing.JButton
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JButton getJButtonOk() {
	if (ivjJButtonOk == null) {
		try {
			ivjJButtonOk = new javax.swing.JButton();
			ivjJButtonOk.setName("JButtonOk");
			ivjJButtonOk.setMnemonic('o');
			ivjJButtonOk.setText("Ok");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJButtonOk;
}


/**
 * Return the JLabelSortBy property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getJLabelCategory() {
	if (ivjJLabelCategory == null) {
		try {
			ivjJLabelCategory = new javax.swing.JLabel();
			ivjJLabelCategory.setName("JLabelCategory");
			ivjJLabelCategory.setFont(new java.awt.Font("dialog", 0, 14));
			ivjJLabelCategory.setText("Category:");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJLabelCategory;
}

/**
 * Return the JListCategories property value.
 * @return javax.swing.JList
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JList getJListCategories() {
	if (ivjJListCategories == null) {
		try {
			ivjJListCategories = new javax.swing.JList();
			ivjJListCategories.setName("JListCategories");
			ivjJListCategories.setBounds(0, 0, 102, 153);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJListCategories;
}

/**
 * Return the JPanelButtons property value.
 * @return javax.swing.JPanel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JPanel getJPanelButtons() {
	if (ivjJPanelButtons == null) {
		try {
			ivjJPanelButtons = new javax.swing.JPanel();
			ivjJPanelButtons.setName("JPanelButtons");
			ivjJPanelButtons.setBorder(new com.cannontech.common.gui.util.TitleBorder());
			ivjJPanelButtons.setLayout(getJPanelButtonsFlowLayout());
			getJPanelButtons().add(getJButtonAddImages(), getJButtonAddImages().getName());
			getJPanelButtons().add(getJButtonOk(), getJButtonOk().getName());
			getJPanelButtons().add(getJButtonCancel(), getJButtonCancel().getName());
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJPanelButtons;
}


/**
 * Return the JPanelButtonsFlowLayout property value.
 * @return java.awt.FlowLayout
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private java.awt.FlowLayout getJPanelButtonsFlowLayout() {
	java.awt.FlowLayout ivjJPanelButtonsFlowLayout = null;
	try {
		/* Create part */
		ivjJPanelButtonsFlowLayout = new java.awt.FlowLayout();
		ivjJPanelButtonsFlowLayout.setHgap(15);
	} catch (java.lang.Throwable ivjExc) {
		handleException(ivjExc);
	};
	return ivjJPanelButtonsFlowLayout;
}


/**
 * Return the JPanelImages property value.
 * @return javax.swing.JPanel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JPanel getJPanelImages() {
	if (ivjJPanelImages == null) {
		try {
			ivjJPanelImages = new javax.swing.JPanel();
			ivjJPanelImages.setName("JPanelImages");
			ivjJPanelImages.setPreferredSize(new java.awt.Dimension(160, 120));
			ivjJPanelImages.setLayout(getJPanelImagesFlowLayout());
			ivjJPanelImages.setBounds(0, 0, 160, 120);
			ivjJPanelImages.setMaximumSize(new java.awt.Dimension(160, 32767));
			ivjJPanelImages.setMinimumSize(new java.awt.Dimension(160, 120));
			// user code begin {1}
         
         ivjJPanelImages.setBackground( java.awt.Color.white );

			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJPanelImages;
}


/**
 * Return the JPanelImagesFlowLayout property value.
 * @return java.awt.FlowLayout
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private java.awt.FlowLayout getJPanelImagesFlowLayout() {
	java.awt.FlowLayout ivjJPanelImagesFlowLayout = null;
	try {
		/* Create part */
		ivjJPanelImagesFlowLayout = new java.awt.FlowLayout();
		ivjJPanelImagesFlowLayout.setVgap(8);
		ivjJPanelImagesFlowLayout.setHgap(8);
	} catch (java.lang.Throwable ivjExc) {
		handleException(ivjExc);
	};
	return ivjJPanelImagesFlowLayout;
}


/**
 * Return the JScrollPaneCategory property value.
 * @return javax.swing.JScrollPane
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JScrollPane getJScrollPaneCategory() {
	if (ivjJScrollPaneCategory == null) {
		try {
			ivjJScrollPaneCategory = new javax.swing.JScrollPane();
			ivjJScrollPaneCategory.setName("JScrollPaneCategory");
			getJScrollPaneCategory().setViewportView(getJListCategories());
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJScrollPaneCategory;
}


/**
 * Return the JScrollPaneImages property value.
 * @return javax.swing.JScrollPane
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JScrollPane getJScrollPaneImages() {
	if (ivjJScrollPaneImages == null) {
		try {
			ivjJScrollPaneImages = new javax.swing.JScrollPane();
			ivjJScrollPaneImages.setName("JScrollPaneImages");
			ivjJScrollPaneImages.setVerticalScrollBarPolicy(javax.swing.JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
			ivjJScrollPaneImages.setHorizontalScrollBarPolicy(javax.swing.JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
			getJScrollPaneImages().setViewportView(getJPanelImages());
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJScrollPaneImages;
}


public int getReturnResult()
{
   return retResult;
}


public javax.swing.ImageIcon getSelectedImageIcon()
{
   if( selectedLabel != null )
      return (javax.swing.ImageIcon)selectedLabel.getIcon();
   else
      return null;
}


/* Return a null image if there is nothing selected */
public LiteYukonImage getSelectedLiteImage() 
{
   if( selectedLabel == null )
      return null;
   else
      return (LiteYukonImage)selectedLabel.getClientProperty("YukonImage");
}


/**
 * getValue method comment.
 */
public Object getValue(Object val) 
{

	return null;
}


/**
 * Called whenever the part throws an exception.
 * @param exception java.lang.Throwable
 */
private void handleException(Throwable exception) {

	/* Uncomment the following lines to print uncaught exceptions to stdout */
	com.cannontech.clientutils.CTILogger.error("--------- UNCAUGHT EXCEPTION ---------");
	com.cannontech.clientutils.CTILogger.error( "Throwable caught", exception );
}


/**
 * Initializes connections
 * @exception java.lang.Exception The exception description.
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void initConnections() throws java.lang.Exception {
	// user code begin {1}
   
   getImagePopupMenu().addPopupMenuListener( this );      
   
	// user code end
	getJButtonAddImages().addActionListener(this);
	getJButtonOk().addActionListener(this);
	getJButtonCancel().addActionListener(this);
	getJListCategories().addListSelectionListener(this);
}

/**
 * Initialize the class.
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void initialize() {
	try {
		// user code begin {1}
		// user code end
		setName("YukonImageEditorPanel");
		setLayout(new java.awt.GridBagLayout());
		setSize(462, 388);

		java.awt.GridBagConstraints constraintsJPanelButtons = new java.awt.GridBagConstraints();
		constraintsJPanelButtons.gridx = 1; constraintsJPanelButtons.gridy = 3;
		constraintsJPanelButtons.gridwidth = 2;
		constraintsJPanelButtons.fill = java.awt.GridBagConstraints.HORIZONTAL;
		constraintsJPanelButtons.anchor = java.awt.GridBagConstraints.SOUTH;
		constraintsJPanelButtons.ipadx = 138;
		constraintsJPanelButtons.insets = new java.awt.Insets(3, 5, 5, 6);
		add(getJPanelButtons(), constraintsJPanelButtons);

		java.awt.GridBagConstraints constraintsJScrollPaneImages = new java.awt.GridBagConstraints();
		constraintsJScrollPaneImages.gridx = 2; constraintsJScrollPaneImages.gridy = 2;
		constraintsJScrollPaneImages.fill = java.awt.GridBagConstraints.BOTH;
		constraintsJScrollPaneImages.weightx = 2.0;
		constraintsJScrollPaneImages.weighty = 2.0;
		constraintsJScrollPaneImages.ipadx = 319;
		constraintsJScrollPaneImages.ipady = 287;
		constraintsJScrollPaneImages.insets = new java.awt.Insets(1, 3, 2, 6);
		add(getJScrollPaneImages(), constraintsJScrollPaneImages);

		java.awt.GridBagConstraints constraintsJLabelCategory = new java.awt.GridBagConstraints();
		constraintsJLabelCategory.gridx = 1; constraintsJLabelCategory.gridy = 1;
		constraintsJLabelCategory.anchor = java.awt.GridBagConstraints.WEST;
		constraintsJLabelCategory.ipadx = 31;
		constraintsJLabelCategory.ipady = -2;
		constraintsJLabelCategory.insets = new java.awt.Insets(6, 6, 0, 13);
		add(getJLabelCategory(), constraintsJLabelCategory);

		java.awt.GridBagConstraints constraintsJScrollPaneCategory = new java.awt.GridBagConstraints();
		constraintsJScrollPaneCategory.gridx = 1; constraintsJScrollPaneCategory.gridy = 2;
		constraintsJScrollPaneCategory.fill = java.awt.GridBagConstraints.VERTICAL;
		constraintsJScrollPaneCategory.anchor = java.awt.GridBagConstraints.WEST;
		constraintsJScrollPaneCategory.ipadx = 81;
		constraintsJScrollPaneCategory.ipady = 287;
		constraintsJScrollPaneCategory.insets = new java.awt.Insets(1, 6, 2, 3);
		add(getJScrollPaneCategory(), constraintsJScrollPaneCategory);
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
public void jButtonAddImages_ActionPerformed(java.awt.event.ActionEvent actionEvent) 
{
   ImageChooser.getInstance().setDialogTitle("Select Image File");
   ImageChooser.getInstance().setMultiSelectionEnabled(true);
	int res = ImageChooser.getInstance().showOpenDialog(this);

   if( res == javax.swing.JFileChooser.APPROVE_OPTION )
   {
      if(ImageChooser.getInstance().getSelectedFile() != null )
      {
         java.io.File[] files = ImageChooser.getInstance().getSelectedFiles();
      
         for( int i = 0; i < files.length; i++ )
         {
            com.cannontech.database.db.state.YukonImage dbImg = 
                  new com.cannontech.database.db.state.YukonImage();
            
            try
            {
               byte[] byteArray = new byte[ (int)files[i].length() ];
               new java.io.FileInputStream(files[i]).read( byteArray, 0, byteArray.length );
                              
               String name = files[i].getName();
                                       
               dbImg.setImageCategory( com.cannontech.common.util.CtiUtilities.STRING_NONE );  //not sure if this is right!
               dbImg.setImageName( (name.length() >= 80 ? name.substring(0,80) : name) );
               dbImg.setImageValue( byteArray );
               
               dbImg.setImageID( new Integer(dbImg.getNextImageID(
                     com.cannontech.database.PoolManager.getInstance().getConnection(
                              com.cannontech.common.util.CtiUtilities.getDatabaseAlias()))) );

               //insert the new image
//               com.cannontech.database.Transaction.createTransaction(
//                  com.cannontech.database.Transaction.INSERT, dbImg ).execute();
                  

					fireInputDataPanelEvent( 
						new PropertyPanelEvent(
									this,
									PropertyPanelEvent.EVENT_DB_INSERT,
									dbImg) );                  
            }
            catch( Exception e )
            {
               com.cannontech.clientutils.CTILogger.error( "Exception occurred when saving image file to database", e );
            }
         
         }
      }
      
   }

   //the null setting tells us to get a new list of YukonImages from the DB
   setUpImages( null );

	return;
}


/**
 * Comment
 */
public void jButtonCancel_ActionPerformed(java.awt.event.ActionEvent actionEvent) 
{
   disposePanel();

	return;
}


/**
 * Comment
 */
public void jButtonOk_ActionPerformed(java.awt.event.ActionEvent actionEvent) 
{
   retResult = OK_OPTION;
   disposePanel();
   
	return;
}


/**
 * Comment
 */
public void jListCategories_ValueChanged(javax.swing.event.ListSelectionEvent listSelectionEvent) 
{
   if( !listSelectionEvent.getValueIsAdjusting()
        && getJListCategories().getSelectedValue() != null )
   {
      //just in case, remove all the JLabels
      getJPanelImages().removeAll();

      IDatabaseCache cache = 
         com.cannontech.database.cache.DefaultDatabaseCache.getInstance();
       
      synchronized( cache )
      {
         java.util.List imgList = cache.getAllYukonImages();
         String selCategory = getJListCategories().getSelectedValue().toString();

         //create and add the image as a JLabel   
         for( int i = 0; i < imgList.size(); i++ )
         {
            LiteYukonImage lImage = (LiteYukonImage)imgList.get(i);
            if( lImage.getImageValue() != null
                && ( selCategory.equalsIgnoreCase(ALL_CATEGORIES_STRING)
                     || lImage.getImageCategory().equalsIgnoreCase(selCategory)) )
            {
               createImageLabel( (LiteYukonImage)imgList.get(i) );
            }
            
         }
         
      }   

      //relayout our display
      doImagePanelLayout();
      
      setSelectedLiteYukonImage( getSelectedLiteImage() );
   }

	return;
}


/**
 * main entrypoint - starts the part when it is run as an application
 * @param args java.lang.String[]
 */
public static void main(java.lang.String[] args) {
	try {
		javax.swing.JFrame frame = new javax.swing.JFrame();
		GroupStateNamePanel aGroupStateNamePanel;
		aGroupStateNamePanel = new GroupStateNamePanel();
		frame.setContentPane(aGroupStateNamePanel);
		frame.setSize(aGroupStateNamePanel.getSize());
		frame.addWindowListener(new java.awt.event.WindowAdapter() {
			public void windowClosing(java.awt.event.WindowEvent e) {
				System.exit(0);
			};
		});
		frame.setVisible(true);
	} catch (Throwable exception) {
		System.err.println("Exception occurred in main() of com.cannontech.common.gui.util.DataInputPanel");
		exception.printStackTrace(System.out);
	}
}


   public void popupMenuCanceled(javax.swing.event.PopupMenuEvent e)
   {
   }


   public void popupMenuWillBecomeInvisible(javax.swing.event.PopupMenuEvent e)
   {  
   }


   public void popupMenuWillBecomeVisible(javax.swing.event.PopupMenuEvent e) 
   {
      
      if( e.getSource() == getImagePopupMenu() )
      {
         if( selectedLabel != null )
            getImagePopupMenu().setSelectedLiteImage(
                  (LiteYukonImage)selectedLabel.getClientProperty("YukonImage") );
         else
            getImagePopupMenu().setSelectedLiteImage(null);

      }
         
   }


private void selectLabel(final javax.swing.JLabel label, final boolean scrollToLabel) 
{
   javax.swing.SwingUtilities.invokeLater(new Runnable() 
   {
      public void run() 
      {
         if (selectedLabel != null) 
            selectedLabel.setBorder(unselectedBorder);

         label.setBorder(selectedBorder);
         selectedLabel = label;
         
         if( scrollToLabel )
            getJScrollPaneImages().getViewport().scrollRectToVisible(
               new java.awt.Rectangle(label.getLocation(), label.getSize()) );
      }
   });

}


public void setSelectedLiteYukonImage( LiteYukonImage liteImg )
{
   for( int i = 0; i < getJPanelImages().getComponentCount(); i++ )
   {
      if( !(getJPanelImages().getComponent(i) instanceof javax.swing.JLabel) )
         continue;

      javax.swing.JLabel imgLabel = (javax.swing.JLabel)getJPanelImages().getComponent(i);
      LiteYukonImage labelImage = (LiteYukonImage)imgLabel.getClientProperty("YukonImage");

      if( labelImage != null && liteImg != null )
         if( labelImage.getImageID() == liteImg.getImageID() )
         {
            selectLabel( imgLabel, true );
            return;
         }   
   }

}


protected void setUpImages( LiteYukonImage[] images )
{
   //just in case, remove all the JLabels
   getJPanelImages().removeAll();

   if( images == null )
   {
      IDatabaseCache cache = 
         com.cannontech.database.cache.DefaultDatabaseCache.getInstance();
       
      synchronized( cache )
      {
         java.util.List imgList = cache.getAllYukonImages();
         images = new LiteYukonImage[ imgList.size() ];
   
         for( int i = 0; i < imgList.size(); i++ )
            if( ((LiteYukonImage)imgList.get(i)).getImageValue() != null )
               images[i] = (LiteYukonImage)imgList.get(i);
      }   
   }

   //put our images in order by their category
	java.util.Arrays.sort( images, com.cannontech.database.data.lite.LiteComparators.liteYukonImageCategoryComparator );
	java.util.Vector catVector = new java.util.Vector(images.length / 3);
	catVector.add( ALL_CATEGORIES_STRING );
	String currCategory = null;
	
   for (int i = 0; i < images.length; i++) 
   {
      LiteYukonImage image = images[i];
      
      //create and add the image as a JLabel
      createImageLabel( image );

      if( currCategory == null || !image.getImageCategory().equalsIgnoreCase(currCategory) )
      {
			currCategory = image.getImageCategory();

         if( !currCategory.equalsIgnoreCase(com.cannontech.common.util.CtiUtilities.STRING_NONE) )
      	  catVector.add( currCategory );
		}
      
   }

  	getJListCategories().setListData( catVector );
   
   doImagePanelLayout();
}

   private void createImageLabel( LiteYukonImage image )
   {
      javax.swing.JLabel imageLabel = new javax.swing.JLabel( image.getImageName(),
            new javax.swing.ImageIcon(
               java.awt.Toolkit.getDefaultToolkit().createImage(image.getImageValue())),
            javax.swing.SwingConstants.CENTER);
      
      imageLabel.setHorizontalTextPosition( javax.swing.SwingConstants.CENTER );
      imageLabel.setVerticalAlignment( javax.swing.SwingConstants.BOTTOM );
      imageLabel.setVerticalTextPosition( javax.swing.SwingConstants.BOTTOM );
      
      imageLabel.setToolTipText("CTRL+CLICK to unselect image");
      imageLabel.setBorder(unselectedBorder);
      imageLabel.putClientProperty( "YukonImage", image );
      
      imageLabel.addMouseListener( new ImageMouseAdapter(getImagePopupMenu()) );

      getJPanelImages().add(imageLabel);
   }


   /**
    * setValue method comment.
    */
   public void setValue(Object val) {
   }


   private void unselectLabel(final javax.swing.JLabel label) 
   {
      if( label != selectedLabel )
         return;
   
      javax.swing.SwingUtilities.invokeLater(new Runnable() 
      {
         public void run()
         {
            if (selectedLabel != null) 
               selectedLabel.setBorder(unselectedBorder);
   
            label.setBorder(unselectedBorder);
            selectedLabel = null;
         }
      });
   
   }


/**
 * Method to handle events for the ListSelectionListener interface.
 * @param e javax.swing.event.ListSelectionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
public void valueChanged(javax.swing.event.ListSelectionEvent e) {
	// user code begin {1}
	// user code end
	if (e.getSource() == getJListCategories()) 
		connEtoC5(e);
	// user code begin {2}
	// user code end
}
}