package com.cannontech.tdc.test;

/**
 * Insert the type's description here.
 * Creation date: (2/3/00 1:34:07 PM)
 * @author: 
 */
import java.awt.print.PageFormat;
import java.awt.print.Printable;


public class TestFrame extends javax.swing.JFrame implements Printable {
	private javax.swing.JPanel ivjJFrameContentPane = null;
	Object[][] rows = {
				{"ONE", "TWO", "HI", "HO"},				
				{"THREE", "FOUR", "hear", "FDD"},
				{"FIVE", "SIX", "FDS", "LODF"},
				{"SEVEN", "EIGHT", "FKJDS", "*&%^&%&(*"}
			};
	Object[] headers = {"ME","THEM", "THERE" , "HERE"};
	private javax.swing.JButton ivjJButtonPrint = null;
	IvjEventHandler ivjEventHandler = new IvjEventHandler();
	private javax.swing.JScrollPane ivjJScrollPane1 = null;
	private javax.swing.JTable ivjScrollPaneTable = null;
	private javax.swing.JButton ivjJButtonKL = null;
	private javax.swing.JTextField ivjJTextField1 = null;

class IvjEventHandler implements java.awt.event.ActionListener, java.beans.PropertyChangeListener {
		public void actionPerformed(java.awt.event.ActionEvent e) {
			if (e.getSource() == TestFrame.this.getJButtonPrint()) 
				connEtoC1(e);
			if (e.getSource() == TestFrame.this.getJButtonKL()) 
				connEtoC2(e);
		};
		public void propertyChange(java.beans.PropertyChangeEvent evt) {
			if (evt.getSource() == TestFrame.this.getScrollPaneTable() && (evt.getPropertyName().equals("columnModel"))) 
				connEtoM1(evt);
		};
	};
	private com.klg.jclass.field.JCComboField ivjJCComboField1 = null;
/**
 * TestFrame constructor comment.
 */
public TestFrame() {
	super();
	initialize();
}
/**
 * TestFrame constructor comment.
 * @param title java.lang.String
 */
public TestFrame(String title) {
	super(title);
}
/**
 * connEtoC1:  (JButtonPrint.action.actionPerformed(java.awt.event.ActionEvent) --> TestFrame.jButtonPrint_ActionPerformed(Ljava.awt.event.ActionEvent;)V)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC1(java.awt.event.ActionEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.jButtonPrint_ActionPerformed(arg1);
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}
/**
 * connEtoC2:  (JButtonKL.action.actionPerformed(java.awt.event.ActionEvent) --> TestFrame.jButtonKL_ActionPerformed(Ljava.awt.event.ActionEvent;)V)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC2(java.awt.event.ActionEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.jButtonKL_ActionPerformed(arg1);
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}
/**
 * connEtoM1:  (ScrollPaneTable.columnModel --> JTextField1.text)
 * @param arg1 java.beans.PropertyChangeEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoM1(java.beans.PropertyChangeEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		getJTextField1().setText(new java.lang.String());
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}
/**
 * Return the JButtonKL property value.
 * @return javax.swing.JButton
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JButton getJButtonKL() {
	if (ivjJButtonKL == null) {
		try {
			ivjJButtonKL = new javax.swing.JButton();
			ivjJButtonKL.setName("JButtonKL");
			ivjJButtonKL.setText("Print KL Stuff");
			ivjJButtonKL.setBounds(27, 272, 114, 27);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJButtonKL;
}
/**
 * Return the JButtonPrint property value.
 * @return javax.swing.JButton
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JButton getJButtonPrint() {
	if (ivjJButtonPrint == null) {
		try {
			ivjJButtonPrint = new javax.swing.JButton();
			ivjJButtonPrint.setName("JButtonPrint");
			ivjJButtonPrint.setText("Print Some Garbage");
			ivjJButtonPrint.setBounds(9, 11, 152, 27);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJButtonPrint;
}
/**
 * Return the JCComboField1 property value.
 * @return com.klg.jclass.field.JCComboField
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private com.klg.jclass.field.JCComboField getJCComboField1() {
	if (ivjJCComboField1 == null) {
		try {
			ivjJCComboField1 = new com.klg.jclass.field.JCComboField();
			ivjJCComboField1.setName("JCComboField1");
			ivjJCComboField1.setDataProperties(new com.klg.jclass.field.DataProperties(new com.klg.jclass.field.validate.JCDateTimeValidator(null, "MMM d, yyyy h:mm:ss a", "", new String[] {"h:mm:ss a z EEEE, MMMM d, yyyy","h:mm:ss a z EEEE, MMMM d, yyyy","h:mm:ss a EEEE, MMMM d, yyyy","h:mm a EEEE, MMMM d, yyyy","h:mm:ss a z MMMM d, yyyy","h:mm:ss a z MMMM d, yyyy","h:mm:ss a MMMM d, yyyy","h:mm a MMMM d, yyyy","h:mm:ss a z MMM d, yyyy","h:mm:ss a z MMM d, yyyy","h:mm:ss a MMM d, yyyy","h:mm a MMM d, yyyy","h:mm:ss a z M/d/yy","h:mm:ss a z M/d/yy","h:mm:ss a M/d/yy","h:mm a M/d/yy"}, false, 2, false, new java.util.GregorianCalendar(2000, 2, 23, 4, 11, 36), 70), new com.klg.jclass.util.value.MutableValueModel(java.util.Calendar.class, null), new com.klg.jclass.field.JCInvalidInfo(true, 1, new java.awt.Color(0, 0, 0, 255), new java.awt.Color(255, 255, 255, 255))));
			ivjJCComboField1.setBounds(460, 30, 124, 25);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJCComboField1;
}
/**
 * Return the JFrameContentPane property value.
 * @return javax.swing.JPanel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JPanel getJFrameContentPane() {
	if (ivjJFrameContentPane == null) {
		try {
			ivjJFrameContentPane = new javax.swing.JPanel();
			ivjJFrameContentPane.setName("JFrameContentPane");
			ivjJFrameContentPane.setLayout(null);
			getJFrameContentPane().add(getJTextField1(), getJTextField1().getName());
			getJFrameContentPane().add(getJButtonPrint(), getJButtonPrint().getName());
			getJFrameContentPane().add(getJScrollPane1(), getJScrollPane1().getName());
			getJFrameContentPane().add(getJButtonKL(), getJButtonKL().getName());
			getJFrameContentPane().add(getJCComboField1(), getJCComboField1().getName());
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJFrameContentPane;
}
/**
 * Return the JScrollPane1 property value.
 * @return javax.swing.JScrollPane
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JScrollPane getJScrollPane1() {
	if (ivjJScrollPane1 == null) {
		try {
			ivjJScrollPane1 = new javax.swing.JScrollPane();
			ivjJScrollPane1.setName("JScrollPane1");
			ivjJScrollPane1.setVerticalScrollBarPolicy(javax.swing.JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
			ivjJScrollPane1.setHorizontalScrollBarPolicy(javax.swing.JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
			ivjJScrollPane1.setBounds(182, 10, 210, 292);
			getJScrollPane1().setViewportView(getScrollPaneTable());
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJScrollPane1;
}
/**
 * Return the JTextField1 property value.
 * @return javax.swing.JTextField
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JTextField getJTextField1() {
	if (ivjJTextField1 == null) {
		try {
			ivjJTextField1 = new javax.swing.JTextField();
			ivjJTextField1.setName("JTextField1");
			ivjJTextField1.setBounds(201, 329, 113, 21);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJTextField1;
}
/**
 * Return the ScrollPaneTable property value.
 * @return javax.swing.JTable
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JTable getScrollPaneTable() {
	if (ivjScrollPaneTable == null) {
		try {
			ivjScrollPaneTable = new javax.swing.JTable();
			ivjScrollPaneTable.setName("ScrollPaneTable");
			getJScrollPane1().setColumnHeaderView(ivjScrollPaneTable.getTableHeader());
			getJScrollPane1().getViewport().setBackingStoreEnabled(true);
			ivjScrollPaneTable.setColumnSelectionAllowed(true);
			ivjScrollPaneTable.setBounds(0, 0, 200, 200);
			// user code begin {1}


			ivjScrollPaneTable = new javax.swing.JTable(rows, headers);
			
			ivjScrollPaneTable.setName("ScrollPaneTable");			
			getJScrollPane1().setColumnHeaderView(ivjScrollPaneTable.getTableHeader());
			getJScrollPane1().getViewport().setBackingStoreEnabled(true);
			ivjScrollPaneTable.setColumnSelectionAllowed(false);
			ivjScrollPaneTable.setBounds(0, 0, 200, 200);
			
			ivjScrollPaneTable.setDefaultRenderer(Object.class, new TestTableRenderer() );
			
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjScrollPaneTable;
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
	getJButtonPrint().addActionListener(ivjEventHandler);
	getJButtonKL().addActionListener(ivjEventHandler);
	getScrollPaneTable().addPropertyChangeListener(ivjEventHandler);
}
/**
 * Initialize the class.
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void initialize() {
	try {
		// user code begin {1}
		// user code end
		setName("TestFrame");
		setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
		setSize(804, 386);
		setContentPane(getJFrameContentPane());
		initConnections();
	} catch (java.lang.Throwable ivjExc) {
		handleException(ivjExc);
	}
	// user code begin {2}

	//com.cannontech.tdc.tableheader.TextFieldHeaderRenderer renderer = new com.cannontech.tdc.tableheader.TextFieldHeaderRenderer();
	com.cannontech.tdc.tableheader.ButtonHeaderRenderer renderer = new com.cannontech.tdc.tableheader.ButtonHeaderRenderer();
	
	javax.swing.table.TableColumnModel model = getScrollPaneTable().getColumnModel();
	int n = headers.length;

	for( int i = 0; i < n; i++ )
		model.getColumn(i).setHeaderRenderer( renderer );

	javax.swing.table.JTableHeader header = getScrollPaneTable().getTableHeader();
	header.addMouseListener( new com.cannontech.tdc.tableheader.HeaderListener( header, renderer ) );
	
	
	// user code end
}
/**
 * Comment
 */
public void jButtonKL_ActionPerformed(java.awt.event.ActionEvent actionEvent) 
{

	
}
/**
 * Comment
 */
public void jButtonPrint_ActionPerformed(java.awt.event.ActionEvent actionEvent) 
{

	InfoDialog di = new InfoDialog( this, "HELLO");
	di.setSize( this.getSize() );
	di.setModal( true );
	di.show();

	return;
}
/**
 * main entrypoint - starts the part when it is run as an application
 * @param args java.lang.String[]
 */
public static void main(java.lang.String[] args) {
	try {
		TestFrame aTestFrame;
		aTestFrame = new TestFrame();
		aTestFrame.addWindowListener(new java.awt.event.WindowAdapter() {
			public void windowClosing(java.awt.event.WindowEvent e) {
				System.exit(0);
			};
		});
		aTestFrame.setVisible(true);
		//aTestFrame.setTitleFocused();
		
	} catch (Throwable exception) {
		System.err.println("Exception occurred in main() of javax.swing.JFrame");
		com.cannontech.clientutils.CTILogger.error( exception.getMessage(), exception );;
	}
}
/**
 * Insert the method's description here.
 * Creation date: (2/14/00 2:08:11 PM)
 * @return int
 * @param g java.awt.Graphics
 * @param format java.awt.print.PageFormat
 * @param pageIndex int
 */
public int print(java.awt.Graphics g, PageFormat format, int pageIndex) 
{
	
	return 0;
}
/**
 * Insert the method's description here.
 * Creation date: (2/25/00 2:41:48 PM)
 */
public void setTitleFocused() 
{
	getJTextField1().grabFocus();	
}
/**
 * 
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private static void getBuilderData() {
/*V1.1
**start of data**
	D0CB838494G88G88GCCF954ACGGGGGGGGGGGG8CGGGE2F5E9ECE4E5F2A0E4E1F4E135BA8BD49C57F5C88219D8C8568779A88EE9300DE35210D811C92D5328AE66583112A80DFCAA27F68FC9F492392127F20EFB2CB678F31A81A1C096163005ED6A4AB13624DAB8E88562375AE5E5166533AB960908AD220D92E5E087D8185DD96D4E729332FA5FFBF3E7E707D930E41BF3AEB36F5E77396F7E5F1B65AA5FCD5FD7D8D449F1057BB8652F251063526AB82E78073FC69417A06416F01A3F77G
	DE6376369443F3B550BA566716D4F036574A201D816DF2E83F856F07397A1A4B2760A54EBA0DC07B5503AF35AC3C4E191338CEA9ED2FE8A80767C100CC4011679E012CFFEFC315025F2660918A0BB8EE6B886973F0439185E706F69FC0A240069156FFA3BC77531513ACE6255F65B5695C1E6FFFD074AA19C7B3A7C245A89B5B28BC2B39878A73C649DA3F24FC12B1A234B3G486677F24F4CFDG4F563E09513343125FB44C8782D2C0960647CC32F758A48BE159E271407F2E906F973841B1B2572946B2BA9666
	0776AE0AFB310B4C1F866F59G5BD47CA35D84FF4217DB5286B06843BD59BEE8D5F89E3C76G177D8F013D07626CA968E3FB59600B6EA9B572E269FFD2794F18081CF7G2DG20FCBC37248E208FA08870DFD46F89A7BF0427FDC8F6C8FE3F943087036D22B754ABF985915EAD9620A8380B42309472F29C536D338D35867CAC84EC5CCBB7BD0E49A43381751CF5712BDC5E03BDC52586B2494B2CABCA9EE732C89F0F4A042D5D5898EF6D1C71D8BB0CDD7B9227DDBBE33934F71F415A59CF56946DD06649572CDD
	B5C1F49D203AAE0377C245BE98FE1A62AFEB704CA6B90AA7ED91E85BA750B6868FA2AF6E623B380AFA37219CB6645A0B9AC54603DB54735264A4F3CF0BEC4DD21597BB452F566099AF0FD3DEC8BB93E8657E5C92E6DFCB6B080F86211D8210813085A08F20G20BC00B6763D1327BE210D7571A139BD62933DC208E3E3171A5E0427B0AD07F8D7A0AC7301E1E1008F7978A1D188F3CA1F95A6425BD04C3855547607E10DEEA1A004F859A785E0F6F0D4D98805D91FAF1ADEA3FDC28225FAFCFE70E55E9F64F05E3B
	CD07097C79307CBFC1AFAF8B519E0C7EB5935149A4BFGB34284704E6472C0BD11D7AD6C3F8340234A6158A9B2DF0F908A88224562162409C850C3F642E584081CFBE86C98037704G4A786B2789AE9F5A1C04737C61546B1AF9BAFC4304FDBEB403BEA6F713B8DCA9214D7CEA90ED2663557B385AA74F995A8C01DA0959CA0724771D0F691C04AF56AE699E540C2B140C7D67D6012D71EE4C9AFBBF43798F2A3ABA56C8F4A5827F49G192A0C6FBCD54DEC361D8F8BE82597C2E0E002B7AAF14627550979037038
	CF653F1B62F3E04EFC000AA04A6E07F4DF5A7E1F3D6C7E092E21656733DC0349A8250E64A5E1D288489DC2D088F861A52C505655915BD37CF6C4D247FC11620360678EF02C300A7FCA9D71C5DE16C33E2108AC748B326C8B0CC669775335421121E0B4C460DA5F2CA3FB1426404F557E8F56919F9916828101AACB9D22503FCDFDD49085BFB06D10A28119613FD3CF6CFCC218D1E2445F5613C2E71297A3A45E303AE0A3FF006CA164730389101045A587097F8EB5D6E9E4E4869C29EFAE43B398C4DF1CBA0A3EF8
	EC76EEAE674A71A223DC32613E0E226DC166D33B02D1DFE4BCEDBCED4C7BDF51D82160430C5707DFA76BC62B94347BBFB611B90A2F6616D48154DDC53BBF5DC06898DB9403F7C8224844284A7743468A228F61EAC40010CC02D80FAA337A4657F53481C5FE0CFE2671G7539E87CAB8B91794DBA3166F382BFFF86C8384AE418F315490075BB6B42FE9EE80BGA9A156EFDB0875CB8D13FEDB5D582F825AA7G5CGDE0044B06B47E474425BC4EF344E9B67A779E9D3F88AEC4FE2B9C7C5466A3AEDB4B6137EBB43
	D83FEDEFA6B22C12C1FEGCE99F57C442BD83F25CE24F3BB6FFDBBEECD7AE7197111A8C7F5DC3D8C6B377B1B491A0F82ED070C7A49EE546690FBB8AC8D7BE87AD2E4BB5C243581069BEBFA851EA33ED0D8F60113CE239E82CD04DF125F9434126F6F25F10B6CB59F562D0E6416EC0E903C3335D2437BA06092GB2G765279DEF11DA6BECC8D45449EFDC2E8C48A79856F89A2D993BFA51BE83CB059A939133C57209E5E39E40567F8FBC416E1E44897102F58F5333863CFE32D99D06A35FB8E37A9BCEE7B66DDDC
	6B0D43C5C675F37221F70A38C9A6779313D1794FEDAA593B7BCFAF15B2D90C3AD096B274491AC4D99C6AB412452EC914C52A7B534962318F4DD1D97438EFD2900C77FFF5A36F8540D75D9464B89A27125B35F1EA8C70A9G9B819E1DC27E87FB8F5078A9C67C81DA29F1EC5F39966D3E7326583E0B27F15C52DE92BFE5887CCC975F782DD697E53C45D097A97FFC2428EA1A6920EDBA2A0B149D0F172683301C76842D5F13270DEB0DB483BC2BEBBBDA089CB6CE6BEB6F73941FB32D2F3DFF5C0C3577F620B54EE0
	AD365CCC6436815A59G5BGF681944F66169C1BC57F7C772686EE11DA5785E748D000977BE312196213D94DE4DD48BFCAFBE3B3519F4DBF9C66762FB97567183659583DDF5635593807660D7B2D2E1D209B47EC71D0BF593FC1FCE456C9F4C26DAA29C5EBD79BE6D99D3B851E5B81D89C3D510AF1B4FF96636575AEED3C5CF268F45181839B21B6F1CD3111EBD19B29D54F3BF596926F93001686108D309DE08FC0459C68F18EF5E4E931AD26A36AC89E4981B8A764A9073E0ED0E56AF75D1AAE2E4D9B275705E7
	AEF69C13DFD7A74ACF1C43FA72576EDB63E1DF2FFE1CC9574E18B3320771EBF172A5CD664C969A4EEAEDE14B9C0B937970FCF98EEDA1279D77D2F99DF36A658E62FBEEE8FB81662E63DD4C7193C7622C8921264BA708DEB6678B963CFFC93D0EFEFB3F150C5D866DFCG6E635C12A3GEDG178192BEC63BD851E27D04F641226DFC2BE06738FCDAAB24433C0F82B05CC7B4AFBF856D67BEC63D1D2377F215B7306D21EDA2A7564EEB9E5469C38FE46CC27405002D71EF0B2E71E94EDDCC2EF718631F936EB49B1F
	93524C466704FB4C2FB1BC4C213DADC07ADF1A97BEC7ECB29B1DA35E217B7C5E1B07685C613090260A6E60E5DE217DD2FE03B3B8CF1CE7B5B54BFDBB1D7B15BE1D326428D77D65791AB87E5247C395476C76EFDA50979AEF20AF14DA6859945A09GE92A1DA4369A5DE3E5AB36305540A6189F18ECA4F767813DG20A281EAE2GE7825A3F734C22392BF3FA015CE5314457335DE22C67EE8B290D43D2A4B4AC68F5F95ED22552223E661285BF91BB6BF3090E17795028B04F967EC447B3DA3C712316F87642F41A
	61526A74346AB7BD1DC6BA98CBE0321FCB182F83B61F29D7B3DFEF54C6C6BB5FB136117E61901C3E0825F01883A64E16A9FC2CCEC0DB79FBBB31152D505E89D010003672D83B1E4F0AA5D15E0EAD09D7B3ED27F1583C0455C605AA7EE90A6F5060D94DB45401F7A0FDC04BDE026B7F07E5217B5E2A257357E7FA7916B53EADED369A5B52435678367418F5F1DB7ABBEBFCDBFA523A38AD7D543A30AD7D261144902FB042C7C488EBD2D83890D42EA689FEA911635BCD9887EA17E26E7B56942907C660B08A537587
	05E12EF59969BB5004B9B6937AEE814883F8996048B22C4767E8BCE4A7D98779FAC142E6A0DACB663668751338AC2A138D4B625947B59A1B3216B1BB582F6213A9FE2B864F6C63D50AA76DDDC0F3A6E2DE7C922D8733203DC545F1B64C15DB93B1B6B55B625B034DE6EC8F8E1BF18EF159964EA13F374553A363EF0856E03B013717D51E9F6FC41E4B125076FF7C914A3696F0EDC9313274A44513EDF7BB1155D8127E3C72FB0A1FCE521FD71E3663F9A599E83B131027BCDADF75AD4FAD1117A32E4601FCA6AFD7
	796C60D4FB781966FD2CE3315A6053GDBE349673A865B739BD65CF356F2740B7AA6229BCD28B5DD72853C52947888794AA007CD9D2E7E3E8B7D1DD7AE1C3F62F0438BBB2B24CFE93FB71ADFE9B13C3FA330FD39F2FF27BC375CF6D4095FF354474918A3376516F8EEA33832E66D391A5E5B6EE575B823DBE26844BEEBB8ADDD0C2193DE5F5A27257FC58CFD1FE6BC5B578F0E63996C659FE4F262374F443D33CA3B0D6DA7C7F9768D9C650E5D7ED2A9137553340EA9GDAF9B25A5C13E7D07EF549E8F3CBAC647E
	5083ED91A091A0CD252D22B4F2CFC76EG495D843963E33443963D3D10B9D45E12BFD9BD0CEB9A5BE46D3CB534B5707ECFE8FB716E5D197DEDEA45FB126CE472AD7DBB546FD8DCFF4B06F13DGE865DF48AD2985F08308DFC07B1D690C9D7FFCE7F47CCFBBF1FC8A74CF8748815889405D0EBAF976A32213EAE8F728382EB6029B04F6020ACBEFC35D255E0EBA68EDA7EBEFD65B17E81B5C2FAAB1442E159F697759470D827B6779ED08497879F6726D115D01758B4A8D38DD9425A9410B797A20834F1BBBE01FCC
	7EED8E147FAE4095GD42D48ADB1838C2E40BE35D66CB38D389400AC00EDG3BD76039B53D0B4CDD1E82F5FA8A6641E7EC3F0367GBF2ED4671DDE1FAC05E8F98E483E10849505BC433074569D63FA532DFDF7FF249463C276DF5F43EDDE7EC2E13C3B99E78A537318724C779C25F83617D71C746E8CE859GCF81BC87D03C92EA151538375A3304C7F24AG766D324C8F0F51DBECBCFC8C780429A81A94C2CCCF4F3510EFB4A2BFA3C5E4B8772F24E7A7B34A378E5A4E15CADD3E125DC36451BE3B4FD4AD30FFB6
	F7220D6CB7FA4BD02F608FDCA1B9771C662E1A614B5E2578E855DE4F706FDF5D9F6D5F63D6657B23BFE873C945DD6F95E1DD2D676758AA36073AD5513BAF16CFECB64CA75ED518CFA6EC7AFCC2FE3CC362BD23FF59264FA7EF4550FFE45367137AC2AD3D702CBE1F9CD646B31E0A3B1027B4152753DD8B73646D527374DE1116FEE7171E27E7E246EF6D52730473B39D943711BDCE020378066188CBD5544E077AA451B7BC2378733BB45E1DA47E3C8AE36D9F5B088D0FC9A15FACD45073C7E2AC1821E36DG15F7
	201FBF5406B666815C98C0B2C0A64056BBF07E4BD7C8ED9BB59976CD0C510EBBE81C8FC5146FD0A38E14EB7E9DA857D407DE2EEBE2F475DD07DE2E05B1F2ABF068659ABB7E438E3DDCF1BC63692295F92ADE0DBCA5D875BC7DAEE64DCDD6BDCF6BE2F43D4B2A676997B1635FEC557334CE611945127B3A88DE2D61D9A844BBF6EAA73E00CF767122EF169E407A21E3E014452A75AEC29F043D48G9BG32GF2D6E31C2A6BFE1F23F14AA0C6F5FAC7852CA1F13D434E78E717371D0B7F3E652813FC3F9B12A0F478
	497C082FF11258AECBC10AE4B8933D038A79C647E456D541B7BA090EC4E1042259396114994F6CF930374AB5F85E9CFD7FB827795D1BEF48EF0ACC7ACD7C10EC92A0B2DBAC67A32268D642AD476E9FC4BA179B6670824C2D413B7D697F467C1175553BB8F7C20FE1DD3953F33AA8F58D0BEB1B57C463DBE66445D2B64F78844EF3646BDF2234F875699EE5FC555A68BC51BC545D4329B2F954C976E918CBC1136760ECF0E1E89C323E079F65306EE8375D5A6F5E76F75D6CB8667F4DED640EC8FBCE51649653C067
	C50F4BE1F7DF69F7DC3C60F6375BAFDE313BAF597F37DF9D3F3A2DE101714E8B97DD7FF7613C47E886665BBFE9C35F9EDC0B3EFD402E77EDD70C6F5A6CFA5FFEA346F7A53B5E3727E24EBAF75B753E6DD7FC1B49F242F1EBBA38E43D59F14CCE3E6C0A6F3F992E457D779BAE7ACD9E7C4EB393D46E0AB639C84DA20D0C0485B5CEBC642AD0F12EF01F9476114844F8EFB25F5A1E9DB7BD0E5DB55CF9067E96F02D7E7E6FAB941F36D6FF7F97D2E3C9A6504A57E18DBB6AA679AF886D94008C00ADGF9G856BB01E
	3A4F55F10B5CBBFB24209B94A0EA2EEB587C0F7412794BD321C686F083F8812681D6272A3F65BAC7FCA0DE4DFCD112681598399D63FD81A1C443F788DEE5C506BFEF3BB559271D6154777BB46F53664F7FDEE54D673A8613EB509D5FAF265C46F7F2575D46F7F24B5C8BFF37D9610E7F7B2F356E793F7F7AFEEFFC5E1E68DD5CE77F3157B8676E6C1D1FF31F69B54E397F522B4D396CF7768FB5115FDBE7BD4B6D1339576939213ADF728B555DFCE8089F05D8F010A1FB5C557DF2E4E4844F89B5B734670402242A
	3877CE496BD95ECBDB9F4DFF4CD7DE24F7B3CF81FE4FFA740DF3ADA763798638DD3F1F97C528F8A76C81AF49076DB332905549EFDA487CE16C2390992D04FE3F9B570C79FFD0CB878883FDD812CB91GG44B0GGD0CB818294G94G88G88GCCF954AC83FDD812CB91GG44B0GG8CGGGGGGGGGGGGGGGGGE2F5E9ECE4E5F2A0E4E1F4E1D0CB8586GGGG81G81GBAGGG0591GGGG
**end of data**/
}
}
