package com.cannontech.graph.exportdata;

/**
 * Insert the type's description here.
 * Creation date: (2/29/00 10:17:23 AM)
 * @author: 
 */
//import com.cannontech.tdc.TDCMainFrame;
//import com.cannontech.tdc.logbox.MessageBoxFrame;

public class ExportOptionDialog extends javax.swing.JDialog implements java.awt.event.ActionListener {
	private String fileName = null;
	private String[] data = null;
	private String date = null;
	private String time = null;
	private int columnCount = -1;
	private java.io.File file = null;
	private com.klg.jclass.chart.JCChart chart = null;
	private String directory = null;

	// indexes into fileExt
	private static final int TYPE_CSV_DELIMITED = 0;
	private static final int TYPE_HTML = 1;
	private static final int TYPE_JPEG = 2;
	private static final int TYPE_PNG = 3;
	private static final int TYPE_CSV_BILLING = 4;
	private static final int TYPE_PDF = 5;

	private final String[] typeExt =
	{	
		".csv", 
		".html",
		".jpeg",
		".png"		
		//".csv",
		//".pdf"
		};

	private final String[] typeString = 
	{
		"Comma Delimited (.CSV)",
		"HTML 4.0 Page (.HTML)",
		"Graph as JPEG (.JPEG)",
		"Graph as PNG (.PNG)"
		//"Billing Format (.CSV)",
		//"PDF Format (.PDF)"
		};
			
			
	private javax.swing.JPanel ivjJDialogContentPane = null;
	private javax.swing.JButton ivjCancelButton = null;
	private javax.swing.JComboBox ivjFormatComboBox = null;
	private javax.swing.JLabel ivjFormatLabel = null;
	private javax.swing.JButton ivjOKButton = null;
/**
 * ExportOptionDialog constructor comment.
 * @param owner java.awt.Frame
 */
public ExportOptionDialog(java.awt.Frame owner, String newFileName, String[] stringDataArray ) 
{
	super(owner);

	fileName = newFileName;
	data = stringDataArray;
	initialize();
}
/**
 * ExportOptionDialog constructor comment.
 * @param owner java.awt.Frame
 * created 4/16/01
 */
public ExportOptionDialog(java.awt.Frame owner, String newFileName, int colCount, String newDate, 
							String[] stringDataArray, com.klg.jclass.chart.JCChart newChart, String dir)
{
 	super(owner);
	
	fileName = newFileName;
	columnCount = colCount;	
	data = stringDataArray;
	date = newDate;
	chart = newChart;
	directory = dir;
	initialize();
}
/**
 * Insert the method's description here.
 * Creation date: (1/8/2002 3:00:37 PM)
 * @param event java.awt.event.ActionEvent
 */
public void actionPerformed(java.awt.event.ActionEvent event)
{
	if (event.getSource() == getCancelButton())
	{
		this.dispose();
		return;
	}
	else if ( event.getSource() == getOKButton() )
	{
		//create a file to write to with the given directory.
		java.io.File file = new java.io.File( "C:/yukon/client/export" );
		file.mkdirs();

		//create a file chooser of where to save the file to.
		javax.swing.JFileChooser chooser = new javax.swing.JFileChooser( file );
		chooser.setDialogTitle("Save In");

		//set the file name and ext.
		String selectedFile = fileName + typeExt[getFormatComboBox().getSelectedIndex() ];
		chooser.setSelectedFile( new java.io.File( selectedFile ));

		int status = chooser.showSaveDialog( this );

		if( status == chooser.APPROVE_OPTION )
		{
			file = chooser.getSelectedFile();
			writeFile( file.toString() );

			this.dispose();
		}
		return;
	}
}
/**
 * Return the JButton2 property value.
 * @return javax.swing.JButton
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JButton getCancelButton() {
	if (ivjCancelButton == null) {
		try {
			ivjCancelButton = new javax.swing.JButton();
			ivjCancelButton.setName("CancelButton");
			ivjCancelButton.setMnemonic('C');
			ivjCancelButton.setText("Cancel");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjCancelButton;
}
/**
 * Return the JComboBoxFormat property value.
 * @return javax.swing.JComboBox
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JComboBox getFormatComboBox() {
	if (ivjFormatComboBox == null) {
		try {
			ivjFormatComboBox = new javax.swing.JComboBox();
			ivjFormatComboBox.setName("FormatComboBox");
			ivjFormatComboBox.setBackground(java.awt.Color.white);
			// user code begin {1}

			ivjFormatComboBox.addItem(typeString[TYPE_CSV_DELIMITED].toString());
			ivjFormatComboBox.addItem(typeString[TYPE_HTML].toString());
			ivjFormatComboBox.addItem(typeString[TYPE_JPEG].toString());
			ivjFormatComboBox.addItem(typeString[TYPE_PNG].toString());
			//ivjFormatComboBox.addItem(typeString[TYPE_CSV_BILLING].toString());
			//ivjFormatComboBox.addItem(typeString[TYPE_PDF].toString());
				
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjFormatComboBox;
}

/**
 * Return the JLabel1 property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getFormatLabel() {
	if (ivjFormatLabel == null) {
		try {
			ivjFormatLabel = new javax.swing.JLabel();
			ivjFormatLabel.setName("FormatLabel");
			ivjFormatLabel.setText("Format");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjFormatLabel;
}
/**
 * Insert the method's description here.
 * Creation date: (1/8/2002 4:50:03 PM)
 * @return int
 * @param formatType java.lang.String
 */
public int getIntValue(String formatType)
{
	//loop through all typeStrings to return the int representation
	// of the selected formatType
	for (int i = 0; i < typeString.length; i++)
	{
		if( formatType.equalsIgnoreCase( typeString[i] ))
		{
			return i;
		}
	}
	return 0;
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
			ivjJDialogContentPane.setLayout(new java.awt.GridBagLayout());

			java.awt.GridBagConstraints constraintsFormatLabel = new java.awt.GridBagConstraints();
			constraintsFormatLabel.gridx = 1; constraintsFormatLabel.gridy = 1;
			constraintsFormatLabel.ipadx = 5;
			constraintsFormatLabel.insets = new java.awt.Insets(17, 12, 16, 1);
			getJDialogContentPane().add(getFormatLabel(), constraintsFormatLabel);

			java.awt.GridBagConstraints constraintsFormatComboBox = new java.awt.GridBagConstraints();
			constraintsFormatComboBox.gridx = 2; constraintsFormatComboBox.gridy = 1;
			constraintsFormatComboBox.gridwidth = 2;
			constraintsFormatComboBox.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintsFormatComboBox.weightx = 1.0;
			constraintsFormatComboBox.insets = new java.awt.Insets(16, 1, 13, 19);
			getJDialogContentPane().add(getFormatComboBox(), constraintsFormatComboBox);

			java.awt.GridBagConstraints constraintsOKButton = new java.awt.GridBagConstraints();
			constraintsOKButton.gridx = 2; constraintsOKButton.gridy = 2;
			constraintsOKButton.anchor = java.awt.GridBagConstraints.SOUTHEAST;
			constraintsOKButton.insets = new java.awt.Insets(13, 30, 29, 8);
			getJDialogContentPane().add(getOKButton(), constraintsOKButton);

			java.awt.GridBagConstraints constraintsCancelButton = new java.awt.GridBagConstraints();
			constraintsCancelButton.gridx = 3; constraintsCancelButton.gridy = 2;
			constraintsCancelButton.anchor = java.awt.GridBagConstraints.SOUTHEAST;
			constraintsCancelButton.insets = new java.awt.Insets(13, 8, 29, 16);
			getJDialogContentPane().add(getCancelButton(), constraintsCancelButton);
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
 * Return the JButton1 property value.
 * @return javax.swing.JButton
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JButton getOKButton() {
	if (ivjOKButton == null) {
		try {
			ivjOKButton = new javax.swing.JButton();
			ivjOKButton.setName("OKButton");
			ivjOKButton.setMnemonic('O');
			ivjOKButton.setText("Ok");
			ivjOKButton.setMaximumSize(new java.awt.Dimension(73, 27));
			ivjOKButton.setPreferredSize(new java.awt.Dimension(73, 27));
			ivjOKButton.setMinimumSize(new java.awt.Dimension(73, 27));
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjOKButton;
}
/**
 * Insert the method's description here.
 * Creation date: (2/29/00 12:33:04 PM)
 */
private String[] getTextFromFormat() 
{
	int type = getIntValue( getFormatComboBox().getSelectedItem().toString() );

	String selectedFile =  ("C:/yukon/client/export/"+ fileName + typeExt[getFormatComboBox().getSelectedIndex() ]);	
	file = new java.io.File(selectedFile);
	
	switch( type )
	{
		case TYPE_HTML:
			HTMLformat html = new HTMLformat( );//columnCount, fileName, date, time, data );
			html.setDateTimeTitle( date, time, fileName );
			html.formatNewData( data, columnCount );
			return html.getHTMLData();		

		/* ALL NEW FORMAT TYPES GO HERE */
		
		case TYPE_PDF:
			file = new java.io.File(fileName);
			PDFformat pdf = new PDFformat( file );
			pdf.createPDFFormat(chart);
			return null;

		case TYPE_JPEG:
			JPEGFormat jpeg = new JPEGFormat( file );
			jpeg.createJPEGFormat(chart);
			return null;

		case TYPE_PNG:
			PNGFormat png = new PNGFormat( file );
			png.createPNGFormat( chart );
			return null;

		//case TYPE_CSV_BILLING:
			//CSVBillingFormat csvBilling = new CSVBillingFormat();
			//String [] billingData = csvBilling.retrieveBillingData();
			//return CSVBillingFormat.createLines( billingData );

				
		case TYPE_CSV_DELIMITED:		
		default:
			return CommaDeleimitedFormat.createLines( data, columnCount );
	}

}
/**
 * Called whenever the part throws an exception.
 * @param exception java.lang.Throwable
 */
private void handleException(java.lang.Throwable exception) {

	/* Uncomment the following lines to print uncaught exceptions to stdout */
	System.out.println("--------- UNCAUGHT EXCEPTION ExportOptionDialog() ---------");
	exception.printStackTrace(System.out);
}
/**
 * Initialize the class.
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void initialize() {
	try {
		// user code begin {1}
		// user code end
		setName("ExportOptionDialog");
		setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
		setResizable(false);
		setSize(272, 144);
		setTitle("Export Data");
		setContentPane(getJDialogContentPane());
	} catch (java.lang.Throwable ivjExc) {
		handleException(ivjExc);
	}
	// user code begin {2}
	getOKButton().addActionListener(this);
	getCancelButton().addActionListener(this);
	// user code end
}
/**
 * main entrypoint - starts the part when it is run as an application
 * @param args java.lang.String[]
 */
public static void main(java.lang.String[] args)
{
	try
	{
		ExportOptionDialog exportDialog;
		exportDialog = new ExportOptionDialog(new java.awt.Frame(), new java.lang.String(), null);
		
		exportDialog.setModal(true);
		exportDialog.addWindowListener(new java.awt.event.WindowAdapter() 
			{
				public void windowClosing(java.awt.event.WindowEvent e)
				{
					System.exit(0);
				};
			});
		exportDialog.setVisible(true);
	}
	catch (Throwable exception)
	{
		System.err.println("Exception occurred in main() of javax.swing.JDialog");
		exception.printStackTrace(System.out);
	}
}
/**
 * Insert the method's description here.
 * Creation date: (2/29/00 11:12:28 AM)
 * @param destination java.lang.String
 */
private void writeFile(String destination) 
{
	String[] text = getTextFromFormat();
	if (text != null)	//otherwords this is defaulted as a pdf format.
	{
		java.io.FileWriter writer = null;
		try
		{
			writer = new java.io.FileWriter( destination );

			for( int i = 0; i < text.length; i++ )
			{
				if( text[i] != null)
					writer.write( text[ i ] );
			}
					
		}
		catch ( java.io.IOException e )
		{
			handleException( e );
		}
		finally
		{
			try
			{
				writer.close();
			}
			catch(java.io.IOException ioe)
			{
				ioe.printStackTrace();
			}
			catch(NullPointerException n)
			{
				n.printStackTrace();
			}

		}
		
	}
	else
		System.out.println("@@ Text variable is null -> pdf format");
	
	return;	
}
/**
 * 
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private static void getBuilderData() {
/*V1.1
**start of data**
	D0CB838494G88G88G7BF854ACGGGGGGGGGGGG8CGGGE2F5E9ECE4E5F2A0E4E1F4E1359AEDF0D455B5941060875219CC4BD02AD45126959595ADD51CE24A8F2A362245CADBEC5396DBF428E5463403160EB71BE493A4C078D023D00902FCE89A82E1A4AEA112E44144ACECC882040499F859FD493E646D3E4DFBEF333B89C24F39775D37AF495BC49D5D19B36F5DF36EBD675CF34F57FD89593AE3E6DE36E3ABA159F944789D4EA624FC9FA1726B55AFF3DC0A1861A4165F3BG1B487C3CDCF8
	CE855A529BB21CC524A3DEG63D9B04E3CAE4379A63CEFA11762653B61A5091C8220ED78710D6EB164FC44656453719235901E9BG5281074FAC91652FD40B8D7C63861EC336031025DDB8A7DF5BEE60AAE15C841086105E4566AF0067DAAAF9222B5218F7615BB3C95623FB9C6B110F05A70712EE3656EDBCCB485D59AB3BD1D639D6E82C398C6339G38799CF2C5C55ED5CD3D5D55BEA5D86693C2A1A5240B3EC0D937AA0483E5E2AC2C283ADF5085176BB8FD7706F5C98939A5C1D63A8381649D538AA8AFB8AB
	98E7D15C1387D15EECF8DF88304C44CF2BC0FC25847B8588C8FC0FC777D499FBE89B3C1B2CF9A61433B1499ED3A43637F4A931473448AB79EF5173D8F3986D3E9CE885BD994E6DG47GAE8124GEC23FE10F37883F856F46AF5CAB02804EA4261DA592F1ED47C228C6FAE97D08CDC3368D3D4BFA16C2CBF52CAED74998D58BA7717DE47EC12DE414FFDF6736DE459FD0D0EFC9B1BACCBAFF7CC6BE1361853133089137DF9D2590BFB067A65D0596708D5764AEB141CBC9B59F31F5E67D8EE70D9ED113D37974F7A
	B8BD6BE2F84FB67C03613D94AFDB704CC71F21BE03634BC04B6C653E5136016B62491DCE0A8EF8EC6DB0AD23466116198E9764613A949EC25E29B21319ED6A72BA4527D970CC171BAAF83C4C84DAEEB043497CAB4F05B19B0071840099GF38116G2C82488DF19F739C5E7F85FD2CC9D0755A08A47BC5153035F915EF40D30C692A509052F4A164934F8A2AA4F44A22C60CB99BAA51861DC356ED29C45F77010CCF4510288A982740DD880A3A28EAEC4E764ACDB8C795A935D58A0AA0A098A61C6F2E4AADF8B602
	261F89C3540B09990CDEDA09E752AF1C850E40G5E19DD8E9EC1FB6D027DD781F8CCBB7C1072EB945510A83BDC9EC5690D04DBF1A7E4C6886D1CC6F347A5F80F05380D4B2A9017896355A61FBFD4EE3670F1CBBED4DFD063BC469AABB0AF97AA5CE7DEEC63BE63DEFF9BA9FD7AB3DB1FC1A8D50C1C298C0F1DB314A76A45464F9EEC7B12F1BC9A94AAC3630F49785757AAA3E7581859F746D19AFFA0BF95A0DD61B6FE6B639266333502A6F2AF752A60E022BFE1F1266766C34454F91B65FDD64557293FFDBEFD78
	9B1541EC31510575C76C97C33ADB8C0BA1BF3CE8866D8D977A18911FDD0A39E6BB4507A11E6BA000B4937F1E8BE3CE50F5D56A0C68E20B286BD228BBC17F004A52A21D61C4AA60328E38B06FAAD108E7F37EC7AE0C851F928A0974D04CA5863D124622A80BC1D03ACE0904F4062FBE027E5CAB460DDCD0FB849B1CFEC10EE0DEE17D409CE19D6EC11502F054181A4856B046699DDA3145C68B40E7302FD9814FB5E64CC5F772185BB570833260627B8E3B1AB16DB63763C21879FEB81C08B926D3C5153D6E9F531C
	E060B5266BD36B518EA33B926E67CFD7A32F29A0E396407CB077737B2B104EF3096160F50AACB3F31AF63E65E8911E0B589791A185E352EAB4ED7723231B07514E9AF6E474FB0E2E23B11648F7CC6766BE14CD7B2D9E21DF0815E9D17085176BB32EBAE1BD556BBA6E976D3AA44CFB27C77BF1BF05FD994EDDGD5FD5C6ECF2E673DD3DA6FCC12F96B5BC97B4356BEE66FD8DF426EA563F96F3463984A1C8734C5FD5CD62555567CDD23E90ACF22254358676F2B2D6742F019552F4133CBD2B53D8182A746ED7257
	EA5497EB0B01B66279ED9DFB14E5A0F3D55F481EA4D79D0AAB06B12B5D55D134C729BA3C3F282778AAF5F8FF31381F77979E204DD1F93D39D0033E3DD54BF016835483DC82C8811829F1BB04BE29A0E3547386681B5510A0378C89E4DED70F653058B346FB0F65715803A72B058768194F871919G8C17CAF1D9B0AE54B96EC084F1A5B02EB4F14D7D083BG63304E6B15BB0A71FE599C1F2263996678B77DDFF79EC5D9C3F375BF3EF699561A46ECA956A44F55F20DFD2ED6EB6CF3F52CE69343838FEBE74669A9
	B5236772549A3BDC1EC3E9757A6B70740BDDC2C486310AA6FA43C61B867855BA1E230F1EB77A46829833F5B76B282BAAEA52G4DA4FC6ECF68DC74712A883F4F5D93453C5385CD9F28559296FDE4F90446C404676FA6987B81A6G4C82D89861F224FE6421CBBA15D15A0F775F36FAE258FC8BF9A09BF31E11296A701A08B68A41E4D6A32EC62DB5A25C4FFA3AB4F82677F39983B456AD292F6C1C9472ABD1E0071D2E2E15399BDA1A3CAD47AFFACFDD2C73408B4BF1B32356BBF065EB07EDEB8B2DCD7D4C871798
	4F7913F69A7A17C6F0CD17A0EB38BF16E7C391EE4342E806539DC55C0C4856E17E76AECCA803A7237FEA889D7D210ED869245FCAC77D5FF5D869870750D12720311E6D753177F9BEDF73D8BA116F7FB8E9BE3FBEEA5C7B0CE753591DE457357F4EE7FC1E68305A2CE87A3B0E6CA4FCD6986B3322093A407C6925FDF826B6758A8FDEE6B68C46384F6E0D71DA659A40DCFB8946B10029B11EEB17B6605CF498AF0C715C7A7D7A8F61F9D668B1326AF13F24ABAACD2B00ECD2950892BD4E30344A9A39C50BFC35FB75
	A3795C3EF39F3C054C3B66656CF5C96C32BC466C119BE74F952DBBA91E6D253691EDF388E8CDGBA4075GB3816645795E1657230ED09DD07D9ADD97FC81DAA9F951B8AB0951849A83167196BD2829519FB63C1F4CBE4061B3B896EFE78F74AE2D82768F4CB6EAEC38ED9E0FA7F2F3E6FCE41DE53573AEFAEED9F1D6BB571A7807A83EE8A001E7B5759845633894E8D387783E1FEDC01EE61EE447E66CE7F9837A3F9412F448A41053B081364044D0B75B3BFA9269D9402BE8B043F988209E6042A0679D786CBDE6
	D39BFB9E77F70BBC17F0F90D0D496BC3F36358F76D0EC66CE7BA95DDD702481F6305C634352E04A912613A9AE92E163A83BA1B4A6BD0A37A3DACF6D1B42B5913E95C44E0DF578F72BADEB040EB7B1CC1DE27678EE09F3A8446AB818AAEE7B837DF6634C7A86D900CDB81F400B413F6E2A0D96D457523458917E54595DC363B2FDECCAAE36D983118E3640C61BC2DE3E63B0113494FEF5C49314F6F5A1334C7853B374643A2435DF8926FBECAD717A61AFE12F6324844B5E8CD0AA621E7325AB1B30673E5A12EC4E0
	FE4967347E46F83D1B8B36F99CA08B20GE858E385681C9D8345E358E08BF9FA83661F930A9A94F40F50A94A867E3B399BCCBC341D1DCA2D9263EB1651DE5D5BD89B812FB4F43C180D31D807B61099FE8555611F8356FAB34750EF89BC997D2EA3D6FA7995C6EF3B42697716D96932C1CFB369CBB5BBFABABCD90E78E30CFFCB1B8F382CAB3CD7B9F0893F25193525CC086AAE57272A642F953ABD54522C3E6C09739ADDFA155717B3F164690771E500342B7CAE945B4DF37B6C5B2793CFCA23ED6D4CECBD6818FD
	157973622B091A179EF945281DBD3D1C4F76BB3EC7E949EAF0E18ADBDF1E1260436BBA544F2EA475139EB45BDF69A02F1F61943E0F4FCFD86FF473B79EF4A42BD169065CC5967925308FA63FDB5EE6AF5FF4A82643F3C7388E85637879ECECC9F2D7E54EC5F88F75FC992EC57B9C03358C37117626984F334671FC745C081AB49630F5AFFD45F52571AF3B0E553ADFD05FDAB6EE78376227A8FE4538615F0ABB625C3F33005676AD5E6B5C6E45331F8F6325G2BG8A46435D9540BD1E5732FD274A4998774AD6A5
	6C0164A7DB2EA30CBFB90D7C5B00178EB08DE08E40C200E5A6FF6D945EDB1375D64D0AC2BBF8EC6685A9A42A34F42A702A9BB6BCB0E8E743D5630D3EE84248BA42F46BE9461CD08C74C3G9EGBF400489DC2F1F1D9A73BEFDBCE6FB1FE6FA3DF0A459594E829973EC74E26BDEAC9BDE2F9E073919467CAC1BF53476CD9C095F8E387209F6B5F12BBEDCB7B74CEBB3664AB63C583AA76244FCDFEAF997625F7CB75501EFD486BB733B3D496B7883DE7BFB7AFC2F7DBDBD43BB7ABD7CD1EF72EF2ACFFAC7FED3DDFB
	BA39EEC527476EB15EBAED5FA36EB8BD32C75CF55A3EC77C6074489E311E56069470192993F90FF867A06F91E7CC647124B6A757BF5EECEF5B2B4D76361D58BC7AB7109B1A13591E6917CD3F552E845DF22F61BAFF270A6BDCEC620A76A3AEG634BA66E7CFEF33FA66EC10A1B816339A62EE9B7629641F839097B39E91755A66EE5DA576AA7E5B8AFCD62388FF5AED7B7F10A4E65261A3845BA6717B609FF3F38B8085F50F4B0A67BBE72EA8B46E9EEAA4EDFB8689EA586F20641F26009005F0430F62C06F7DE63
	192C62AA6AD3022F37DBD5A2A1BF7F6ED3CEED698119EDGCC761DD523494EA309FE9B1F315404EC44DD1F1A28E9CC769EEA73E0C80464AC79185CEA0A3B99E09A4CE77BFF6F8CB157BC399F758F8AB1A9988936C88322216F73F4DDBA2C191B4A6D583D9B7B0530AAF6092AAA7A69EC3E3F94FA3619B0F725393F5F0DEA5BBC4B5E2C367D2F312762494377D7D4C5F505CB68D0DD77D25B6E027965G8C57CA7DE092400549FC5F6DE793677EB7FDB45D724750CD366836036A867506A4FECC5E5D67701D7506FF
	AA3476266EE7DE4E5EC96CFB4358E423B71C1C688DD7742C49E7BC27B7A74EEECA9556EB5E061755A9322216C58312AE0E54E955A7EC7D43AD0975E77663F30FF70F21EFE6EB02F6FC77BB708C095192133FDB02DBBB7E81F96F610A521203077771FD661CCD2C1BB7784557751CCB586A0636840F0734AF42034D6D55699E6A789E661CC7BE2C1F39291D7EE35035F07786B8865086A083CCG1886B09BE08140D200D5G3957C12E83A8852882E88270DF4773F556B9901B34AFB3AEC116EE0C675EFB5B136779
	1F360FDD27B25A6D6B54227611F56A176D76F56A576D56BA45F0CB5B51473BC16FB6F35E33A66E3C097BB3650938F312DF8F187895D4AF44570BA81063DF24FAC5A92E4D403DE4411DB7F02B5B71FE8EDB8EA8AADF7B1FF64C55DD12AC33F1F6874E11C2F03DB76D616CC09D2530E051FB1D09C33D597F0E115C97A040E67F1D6469E49D7B13E309DB50851DB85985DDA78EEFAF0C5875DBD349C653619A28444C7E8AD74EDFEE4DF1105C8662E4A750C0F2510C8DA40FDA390164408944703FB170CB843FD5EC82
	EE9B0606E676EDEF0EAF3A31A262A085A70833CB1043818138503FA7F9483ACE4A8E5BF9D9A7C59EDC3B9F566E27EB0FF808432769E4DB826FC3BC233604851F88D3567A853517F268A3D7C8FE87D95B25F6F7D28130550E939DA0160E103C56C3DE5383749A816E2713B7932C1B50FD8D42913AB572AA7C42742761E2BCA4FE3946C4D3845B5DBCB2DAEB267C761959975DDB9D4481768B084CE0F9D1F423987F8716864AF17D10917247E47B0E1144560FB22740B07DFA1EEB77DD35FEC3DFBD31B8695F9D36CF
	B13E83CCC964DA16BB6EF352F881FC6A9473FF501C7FCB967FF075E9898A322C8BF2EFCD480F5FF7EB633A18684F66D17E9A1FE3900DFB957DFE50E3D973FFD0CB8788FDC150D8358FGGD8A9GGD0CB818294G94G88G88G7BF854ACFDC150D8358FGGD8A9GG8CGGGGGGGGGGGGGGGGGE2F5E9ECE4E5F2A0E4E1F4E1D0CB8586GGGG81G81GBAGGG6F8FGGGG
**end of data**/
}
}
