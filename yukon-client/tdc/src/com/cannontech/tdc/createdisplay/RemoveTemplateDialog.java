package com.cannontech.tdc.createdisplay;

/**
 * Insert the type's description here.
 * Creation date: (4/3/00 4:58:40 PM)
 * @author: 
 * @Version: <version>
 */

import java.util.Vector;
import com.cannontech.tdc.utils.DataBaseInteraction;
import com.cannontech.tdc.utils.TDCDefines;
import com.cannontech.tdc.TDCMainFrame;
import com.cannontech.tdc.logbox.MessageBoxFrame;

public class RemoveTemplateDialog extends javax.swing.JDialog {
	private Vector templateNumbers = null;
	private javax.swing.JPanel ivjJDialogContentPane = null;
	private javax.swing.JButton ivjJButtonCancel = null;
	private javax.swing.JButton ivjJButtonDelete = null;
	private javax.swing.JList ivjJListTemplate = null;
	IvjEventHandler ivjEventHandler = new IvjEventHandler();
	private javax.swing.JScrollPane ivjJScrollPane = null;

class IvjEventHandler implements java.awt.event.ActionListener {
		public void actionPerformed(java.awt.event.ActionEvent e) {
			if (e.getSource() == RemoveTemplateDialog.this.getJButtonCancel()) 
				connEtoC1(e);
			if (e.getSource() == RemoveTemplateDialog.this.getJButtonDelete()) 
				connEtoC2(e);
		};
	};
/**
 * RemoveTemplateDialog constructor comment.
 */
public RemoveTemplateDialog() {
	super();
	initialize();
}
/**
 * RemoveTemplateDialog constructor comment.
 * @param owner java.awt.Dialog
 */
public RemoveTemplateDialog(java.awt.Dialog owner) {
	super(owner);
}
/**
 * RemoveTemplateDialog constructor comment.
 * @param owner java.awt.Frame
 * @param title java.lang.String
 * @param modal boolean
 */
public RemoveTemplateDialog(java.awt.Frame owner)
{
	super(owner);

	initialize();
}
/**
 * RemoveTemplateDialog constructor comment.
 * @param owner java.awt.Frame
 * @param title java.lang.String
 * @param modal boolean
 */
public RemoveTemplateDialog(java.awt.Frame owner, String title, boolean modal) {
	super(owner, title, modal);
}
/**
 * connEtoC1:  (JButtonCancel.action.actionPerformed(java.awt.event.ActionEvent) --> RemoveTemplateDialog.jButtonCancel_ActionPerformed(Ljava.awt.event.ActionEvent;)V)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC1(java.awt.event.ActionEvent arg1) {
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
 * connEtoC2:  (JButtonDelete.action.actionPerformed(java.awt.event.ActionEvent) --> RemoveTemplateDialog.jButtonDelete_ActionPerformed(Ljava.awt.event.ActionEvent;)V)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC2(java.awt.event.ActionEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.jButtonDelete_ActionPerformed(arg1);
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}
/**
 * This method was created in VisualAge.
 * @param element java.lang.String
 */
public void deleteTemplateColumn( String element ) 
{
	String query = new String
		("delete from templatecolumns where templatenum = ?");
	Object[] objs = new Object[1];
	objs[0] = new String(element);
	DataBaseInteraction.updateDataBase( query, objs );	
}
/**
 * Return the JButton2 property value.
 * @return javax.swing.JButton
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JButton getJButtonCancel() {
	if (ivjJButtonCancel == null) {
		try {
			ivjJButtonCancel = new javax.swing.JButton();
			ivjJButtonCancel.setName("JButtonCancel");
			ivjJButtonCancel.setMnemonic('C');
			ivjJButtonCancel.setText("Cancel");
			ivjJButtonCancel.setBounds(339, 214, 85, 27);
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
 * Return the JButton1 property value.
 * @return javax.swing.JButton
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JButton getJButtonDelete() {
	if (ivjJButtonDelete == null) {
		try {
			ivjJButtonDelete = new javax.swing.JButton();
			ivjJButtonDelete.setName("JButtonDelete");
			ivjJButtonDelete.setMnemonic('D');
			ivjJButtonDelete.setText("Delete");
			ivjJButtonDelete.setBounds(236, 214, 85, 27);
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
 * Return the JDialogContentPane property value.
 * @return javax.swing.JPanel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JPanel getJDialogContentPane() {
	if (ivjJDialogContentPane == null) {
		try {
			ivjJDialogContentPane = new javax.swing.JPanel();
			ivjJDialogContentPane.setName("JDialogContentPane");
			ivjJDialogContentPane.setLayout(null);
			getJDialogContentPane().add(getJButtonDelete(), getJButtonDelete().getName());
			getJDialogContentPane().add(getJButtonCancel(), getJButtonCancel().getName());
			getJDialogContentPane().add(getJScrollPane(), getJScrollPane().getName());
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJDialogContentPane;
}
/**
 * Return the JList1 property value.
 * @return javax.swing.JList
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JList getJListTemplate() {
	if (ivjJListTemplate == null) {
		try {
			ivjJListTemplate = new javax.swing.JList();
			ivjJListTemplate.setName("JListTemplate");
			ivjJListTemplate.setBounds(0, 0, 421, 185);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJListTemplate;
}
/**
 * Return the JScrollPane1 property value.
 * @return javax.swing.JScrollPane
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JScrollPane getJScrollPane() {
	if (ivjJScrollPane == null) {
		try {
			ivjJScrollPane = new javax.swing.JScrollPane();
			ivjJScrollPane.setName("JScrollPane");
			ivjJScrollPane.setBounds(7, 8, 419, 194);
			getJScrollPane().setViewportView(getJListTemplate());
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJScrollPane;
}
/**
 * Called whenever the part throws an exception.
 * @param exception java.lang.Throwable
 */
private void handleException(java.lang.Throwable exception) 
{
	/* Uncomment the following lines to print uncaught exceptions to stdout */
	System.out.println("--------- UNCAUGHT EXCEPTION ---------");
	exception.printStackTrace(System.out);

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
	getJButtonCancel().addActionListener(ivjEventHandler);
	getJButtonDelete().addActionListener(ivjEventHandler);
}
/**
 * Initialize the class.
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void initialize() {
	try {
		// user code begin {1}
		// user code end
		setName("RemoveTemplateDialog");
		setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
		setResizable(false);
		setSize(438, 284);
		setTitle("Remove Template");
		setContentPane(getJDialogContentPane());
		initConnections();
	} catch (java.lang.Throwable ivjExc) {
		handleException(ivjExc);
	}
	// user code begin {2}
	initList();
	
	// user code end
}
/**
 * Insert the method's description here.
 * Creation date: (4/3/00 5:03:05 PM)
 * Version: <version>
 */
private void initList() 
{
	if ( templateNumbers == null )
		templateNumbers = new Vector(15);

	// Init our RemoveList  			
	String query = new String
		("select name, templatenum from template where templatenum >= ?");
	Object[] objs = new Object[1];
	objs[0] = new Integer(com.cannontech.tdc.data.Display.BEGINNING_USER_DISPLAY_NUMBER);
	Object[][] values = DataBaseInteraction.queryResults( query, objs );

	if ( values.length > 0 )
	{
		Object[] addedItems = new Object[ values.length ];
		
		for( int i = 0; i < values.length; i++ )
		{
			addedItems[i] = values[i][0];
			templateNumbers.addElement( values[i][1] );			
		}

		getJListTemplate().setListData( addedItems );

		getJListTemplate().repaint();	
		getJScrollPane().revalidate();		
	}

}
/**
 * Comment
 */
public void jButtonCancel_ActionPerformed(java.awt.event.ActionEvent actionEvent) 
{
	this.dispose();
	return;
}
/**
 * Comment
 */
public void jButtonDelete_ActionPerformed(java.awt.event.ActionEvent actionEvent) 
{
	Object selectionValues[] = getJListTemplate().getSelectedValues();

	// delete all the columns owned by the template
	for ( int i = 0; i <= getJListTemplate().getMaxSelectionIndex(); i++ )
		if ( getJListTemplate().isSelectedIndex( i ) )
			deleteTemplateColumn( templateNumbers.elementAt( i ).toString() );
	
	for ( int i = 0; i < selectionValues.length; i++ )
	{
		String query = new String
			("delete from template where name = ?");
		Object[] objs = new Object[1];
		objs[0] = selectionValues[i];
		DataBaseInteraction.updateDataBase( query, objs );

		TDCMainFrame.messageLog.addMessage("Template : " + selectionValues[ i ].toString() + "  deleted from the database successfully", MessageBoxFrame.INFORMATION_MSG );
	}
	

	TDCMainFrame.messageLog.addMessage("Template removal successfully accomplished", MessageBoxFrame.INFORMATION_MSG );

	this.dispose();
	//this.setVisible( false );
	return;
}
/**
 * main entrypoint - starts the part when it is run as an application
 * @param args java.lang.String[]
 */
public static void main(java.lang.String[] args) {
	try {
		RemoveTemplateDialog aRemoveTemplateDialog;
		aRemoveTemplateDialog = new RemoveTemplateDialog();
		aRemoveTemplateDialog.setModal(true);
		aRemoveTemplateDialog.addWindowListener(new java.awt.event.WindowAdapter() {
			public void windowClosing(java.awt.event.WindowEvent e) {
				System.exit(0);
			};
		});
		aRemoveTemplateDialog.setVisible(true);
	} catch (Throwable exception) {
		System.err.println("Exception occurred in main() of javax.swing.JDialog");
		exception.printStackTrace(System.out);
	}
}
/**
 * 
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private static void getBuilderData() {
/*V1.1
**start of data**
	D0CB838494G88G88GA0F954ACGGGGGGGGGGGG8CGGGE2F5E9ECE4E5F2A0E4E1F4E1359A8BF0D455B5C0B0D1035099F5A84DF4D29AECBA22C40B9D272695D31C261531FE50E20DB369480C34D50B15DA2A6974EEBE3BCBC0131090028693BE82D1D8C30C8583D6A1098608E6C9B61FDAC6F859FDC9DEF83BEF5977B69F8850F36EFD776D5B4DEE880CE6664CFD775CFB4FBD777C6F5D10726A14C2CBDEB9A116C2227F9D30901273AEA19DF77EDB6638B8B153CACCFF5BGCA496F0385504E01
	31445919569232234B8E7DD4683B203F993E4B482814DA8B9FB176990731DB97BDD8BC79BE0B0A79BEB65AF735EF00768D00B440E13BC6447D6576CA9D3FCC47F3306491125D0FF3925A2BF45CC168BB816682AC6CE77317C23B0E6EBC337120BE6F54F7D2481A5F6C4EFB9D6918E8F258B94056366AED8DD9E4D9BD00FB25D3BEF1CD9C746F85404367136025C3509EF61E9D780FDB71BA5C024F2778B451BD6850BCEE07BBA08A1A6811D43FAC0CB5B6F608DEE5D8F409DE68EAE233A4484AG1187117E5AF6BB
	25C7C8837457D25C53F65CB39D3E33GF28C7C7CF508BFAC01EEGE4091F73635D0775F374DED8C4967F4E17DF9C631C7192BB5FC2A9F44E1B032F5A36D01D7C2584E53F8A46B68CE5DAEBG1A81BCG71GEFD1DBF825984C0A9C69531A942FD7719D717B0F4A1E40970AC714613B3191C6F4DC0768D6829EC2183EFF5A3EB78ABF1381DB37742A57B11924FD40F53F3063B6127D1393F936A8B249CE2B4BCB9EE232D8BC9412895B7B1FB177DEBE94EE1B61FB7790735E2957A97905D176DE7CC4ED5EAA1DCE2E
	E96F3DE7D1576DD457D570ED516D03617FCB71FE931E59699F284DE0BF8E46D61E6536517B8667252DE09EA9595F96D58EF3B30F64354A0C07B3F2A4AF95C5C8BBC9E6FB969A3CEC237894931E7112C6F9447EE2982B74E6DA19FD2D5D08FEBB8E7D39G69GD9G2BG8AFCB04F47ED6C13127727E8E3CEA1209D8DCA32C78C9036F6EDC98534622896900E79D4CD703945EEA1A089FD322892FD0E2594E55097364EDA0A366F06BDBE97FDE2C050A4458754852F280981154DA9AEAD45B9685BB06A123CA2EC60
	7593CEF7F3E999343220EADDFE8FB8FAE8869B2FAEC51D8C8B5DC0918840B7134BBB65A82F03F0FEA74099C38E73A83D93E240A74A0D0DED0AF2B668F761C9C83A8F65DC6E44582141F7120F4BF85BA6442D017E860553F934F42309CE3364C67605409877312EF5981BEB94EEB3AF76F21BE9FEFD81597B447128B603702942EC25D709741D5314A672457A4F59FB2F520F5F50F91AFA2CA06B2EE50F7CA94EE3721DDD013A0A833E1281B28C99EF58D843EC7628200A5CCA5B83E0E022A7A4F146E7D511DD17E7
	3E417BEE0ADB8E74F295AE33BB69F9ACD74163554757DF51BD10FE6137308713CDDE9966A3F1D874E94D22DF74F960C355475615214D697E5A2F98EB0AA95E8F7E5D848E259A78CDE56803022685243E20A6F60A1AA67986C263DB69DEEA304F9F8A8DFC2F1DE5F8C6E5847C5B18DFDB063E61D6FCBE11AA49D8220F3FC7FDD314C5AFB05D2484FD9A436FAFC77BBEAB0E69316143F2ACFA0685B908F10655886942FABCC3C072026AB1D411D1BF7A6DFB4ECA130CF6828EEB1DB6E84F78398F0E6C60BE38737C8F
	4812536F64C54BA1F397B467AD71B39FD96E8F79A063E95726683C6F25B1C2472B0C576CBAA86CC834C20559FD56E624358676301F035A609C377B34CDB84EE30BEE70CD0AACB3F19AF23E25228475A21E8B0A9012B1081DB0E417DC31B1E22CDB17A39BCF2DC0B94A267837769C4D05C5C69D88FDA7007F9C13E57AB9A68BB6EF6BFABE2FB910E9BD95884D9B8F30F9CC869D6770AC34269B920605D107BA82365558F8DC2F5808DE335DC263AE6AE9E90057E647BBD1BE3950AFD7B32DFBD52E47C7DF6735594D
	E7D3C84E8F5F0CD903B62B0C17C1B524471A993CB65B388577CC0331FBD5AE7B6A4D667CF0C4D59537C4D313AE3707B71B754CF00FEFAE0236DF8A285AB1F044D1AE6327B6A33F183BF4341E4B279771DC3E8C76AD50A06EE9083FE4A9B771FE98F09E00C400FCCA6F0FC575682754889C2CF10A01FEA56095BDFBD03A8EE1C4F3501860B8C207DB703B166BE2C8C2398D9D8DEA1A62EB42B4AE1FBE92C126ADB61DC3355DCC7E734BBE55F9DCF667BCF278F2D9DE345AB831F8EB5ECA0D49FDC3B0A47F713B2D79
	4FFF7D1A0D49623375DC963DB0E7CE104B2231A71AAC32027C2ED0D2ECBACBB368C3932F72ACACD64FF3E2BD1A8DF4D787A766450261F0DCA574D91D768BEA1BFB07A3EB49AC0ABFBC9CD9CBBABBF9AD5986E369433C36282D4638D5BE12E92D83E885B883908710B242EDD2287C00DC21F6BB86772400CF10BB4302B42F21366433382A775F5AD24863AA61B9AB31A832AE4F9889BF7B0A08BEDBF76B04F5EFC774F3A356B17D95F746088BCC23B046627A3FBA4DF13DFC1455E58721ED83E031639E0BDD6747B3
	4AE344454F4CB1A22338BEEFFD0C9811BC4A784B98B55568467D6D719A0CF339B0D6B986BA82E8837083A481240FF19DAD58727115F4C48D4F25B4812E9562EED3E4FEB4E45A367E5AF4715504F5113AF8E4ACFC9D135F399EAE3FD5F8A6DA875FC563F14DF9382F1E67B87BC94475C25FEF606E2238F868A79B3847BA9037847A4B4F733AEBE9A766769C237FB06D23CF33FEF76735541913816E95AE9F659B5F43DCC3B2BB282F0EDD17FDD49D3DAE7B24BAFADD56D2DD4A70C043FCAB6363DFD4CFDE37F5D5C7
	2B5B36D23EFFDDD1CCE92B2A28D243EC96B4C19FFBCE5BC42254EFDF4245CDBFF399393FE81DBE27C5D31A76936E57AF57223FC671EB27G9915595B8BAE3BBE1FDC6039FEDAB73D2FC15F89B0F8015BC5CDFE24CD4741584D9718EE53AECC0C594CEF3F5A0E79A38346D7G2C86300FE7DA770CF3FF7DF37595E3EA4B68A4B175191A58FADED993DD4FFF2A41BAC5D502813798294B17EAAA0D31D0E4B82609DE94BB1B73AADD2F890181F102ADD836D59AE3315697ED0BE5AFCC27B3561BF5DAEF784DF0CF248E
	DAC10E2746196C7D6393F54068E5B9510EBCE23F9014413495D5EC776B4FG281BF1547B33CEEE8B71633C4E29BE09FC84C4D5BACFABD3BE7740C9FE47A397F99DF0578846757E00600523F47AC5B7C93BC87370C91EB74A213F872095E090A07122D1CFF5A08DCD52689E2BAF629B2B8BDFE5F57EDD7B300656AB55A6FC0AC4BF7409DCAEFFF5C54A2561D2C8962D17A64A055588FF2336FE6A922B91EC865E5203F84D04E735C3821D0F7DA498DBFD0947598379A88F57E5B857E50E3BC0F16350CFB2F05979BC
	C64F3D4C6D3FEFFBEC7B1536C737DFE5FB74B8955CBEF91C3A38BD165DB17EE67570F3E498BCBF59434F710801BB544B4F11FD196B70D5DA3F19C20D23C772F91491D0973ED4E92A2379D827333D3365F47B17271B5A60035587A9A366F76C03C587225E8591C89C53E3395EE6A46C506DE361C9DC53AF48AA5A8F0BEDAF9AB65A8673636390D755E12EE5F16DC7F904B870ACF47C4B30F114959AE1E8BCAEEC9C7977661B476F8E9BAFB42DE7E7FD689DDE8B65BE14C664FBBE0AF9DFDA261FF1355EBA3BF7101D
	B73CE6E3F4FE7BBF334CCA66ED4B33442033E79ADB5FB0ADD4D3B19D2E233F058C82FEFC9A57EBFBB757EB4AB49E77DF23B63F947AB9G39865EC6FD846F1BF8B7AD1F4E71333AFB7579D3495B39D31C17234FE3FB5C564D5FF5263256B645F97C0D0859D4AA35FB2FCF0424A439D95CBBC37DB7816060F4EE77AF74E07D2401133238F57CBD54900BFDAF677358578A73CF810C83A483E4A0D01A7751FD2E8748869C23B1FDEF08C6C7AF2771BC0C139919569A00CF814E4C60F7177777A10DB868A74F60F1653B
	3548C837B024E754960F24A9811AD281698CA8108134B10625AF923AECDFBEF9ED3FC9BCE063363D78E773493D577D43922B4E4F1841F4192D372BDCBBCC75C22A834F3BA19EF285C09B0087E094E0CEBCBFDBCEAD7208158130FFC4538477A0FD856085C337A40E0450180C986DDF61533B9E8BF73D7BCEAC79G056390475A7B06A0F93B048143A6EA3A709DCC96461420C608FDA60D7DDDDCBF2940DFC63C6E37716C1E6C24F37A3BAB2348A1BCC7DFEA44D8F05CFC0FB6460EFFED9AE36F8546186B8D147371
	CEF7C011E51A9459FD2D38503C675C190C1F25502E1849EDF7A49F5B5DCD3B7518392500474C55B3F94C7C3CA0B2E69EA06618B7D69099B36F301847EFAF080C1937072DFF26A0B2E67EC80F490C4747FAA80F4D1C4751DE4E631E6BB80F3775CE4E630A5E49F9FC53B2B90FED16C89E1F5257B3DBEA5807F8A32FB2F752478E6D433512CF5220FA01BA8A0B2ECE1868535F9E17533749B3F016F100940045GD957F1DB3FFF5FF6E66BD16C3C45B3A0727C4A773B43913B1ED86C38726FC7BFF7609BDA1F822666
	C57A9C7FCB874687CD71D3A443E5B9E8FDA88D8CEAECAAFF1BF3200EE4311F22D96DF43A0B7F563C924ED615406B46016D680746EF4DD21F57919C76BA04BE4DA102CBB6B6FE991465B65D65883B23A4D3DAAE20A18344A770370651DDBC86AD3CED9E647D93D173EB0E2BBEAFB90179691204D0DECC8B3E2A675721331CCE556D5F23E33172F4AEDF1F982243ED844E563215C43D03193C1557AB1F97725F4A4FA4723B582DD6BC67DC682783E4A5729C7BF641357D5EFC3177EA57B1FDE657453629E76B2EECD3
	4F575137DA30855718DF3F833DD40731D4696FD7C543F6DF29ABB1F047D42722CA68AD0CF7316B5A4E2CDE75BAF687A821F5CFCEA23B83D88C7C960AFF5E04E776BD5F306F35B0F66AFA1E1BB36AB18EAF01FEB6409A008DB7E4DA7782345E40FD7C467DF5648AF7E01762EF83854826321E512FE8C07A270056A8C0B240E200AC009C037E937B710E9D2B9668D094FA1B420B15A07944G8DA981785478BBD861E4B2DCF3C32C7BD5118DE5E23FB172ED361C62ABEF0CFC1BCD36723759BDB016F4A337770AEEE2
	68335A741D5675CD3FA3CD341F873E55BD18FE9E2C0F6DDF4B6A235F819F2B0FFE87DCD1BF79DBD4CEFD6C5F901F2B1F789BE2F5C3EC5EF6B5DC59775FEF081ECFBEEC1818CF9A9B226713CF9B4C79045DD55C057C2E325A3013929B566265C93C7FA66D57997D7956EB55A5521C1A3D5CF74DFBCC65BE436E84670D5A4E89FCF9G6281D28116A6997712CD34E6EC13D40D7F6489FD761B4E891B391ED919446EEF76D97C9DBF251057D8D533F80D75F0E1E40DB5B32C06A256489A8BA1B4DEEF0D2C31BE8F3BBB
	FB2C11B5D6313E1E655207EA717F752264D2BC211EC3275BF88EF54D62BADFB0E96C0A2D47845BE46B12E6CD3487E6175F3799F7E8184374F50F0D6BEBA960D6G2C85283CA953FA70A6AE7327ED7C7D2D77A6AE73B2FB244C55B0193758A3E5EE89137908BDD266A742E47EE3FB244C79FAA6031CB0994C813ED2EF1A5C8F188C7ECE4FF2AF4C457D98AE17629E819C1E0169D4580571DE2F0DFBA40FB64870D5232F07702DA2468368337F571C2D6D01F6618B24D0A3456CA77D9AEE609A31325B91FC30AB942B
	67EAAF1B5FCB564E2C0C794634ECB6BBDF4E6CD0BD47726C234348FF559C50599C1ED7378D6FA3316F101DDEC116B5C1BEFB4467417B6451B1CD8C4561A707113E4A6768037A1B76B072BDE4DA73FFD0CB8788F0E3F215458FGG88ABGGD0CB818294G94G88G88GA0F954ACF0E3F215458FGG88ABGG8CGGGGGGGGGGGGGGGGGE2F5E9ECE4E5F2A0E4E1F4E1D0CB8586GGGG81G81GBAGGG7F8FGGGG
**end of data**/
}
}
