package com.cannontech.common.gui.util;

/**
 * Insert the type's description here.
 * Creation date: (11/15/00 1:27:06 PM)
 * @author: 
 */
public class EditableJComboBox extends javax.swing.JComboBox implements javax.swing.event.CaretListener
{
	private Object[] originalData = null;
	private javax.swing.JTextField editor = null;
/**
 * EditableJComboBox constructor comment.
 */
public EditableJComboBox() {
	super();
	initialize();
}
/**
 * EditableJComboBox constructor comment.
 * @param items java.lang.Object[]
 */
public EditableJComboBox(java.lang.Object[] items) 
{
	super(items);

	originalData = new Object[items.length];
	for( int i = 0; i < originalData.length; i++ )
		originalData[i] = items[i];
}
/**
 * EditableJComboBox constructor comment.
 * @param items java.util.Vector
 */
public EditableJComboBox(java.util.Vector items) {
	super(items);
}
/**
 * Method to handle events for the CaretListener interface.
 * @param e javax.swing.event.CaretEvent
 */
public void caretUpdate(javax.swing.event.CaretEvent e) 
{
	String text = ((javax.swing.JTextField)e.getSource()).getText();

	for( int j = 0; j < getOriginalData().length; j++ )
		if( text.equalsIgnoreCase( getOriginalData()[j].toString() ) )
		{
			System.out.println("added " + getOriginalData()[j]);
			this.addItem(getOriginalData()[j]);//getOriginalData()[i]);
		}
	
/*	try
	{	
		for( int i = 0; i <getOriginalData().length; i++ )
		{
			if( getOriginalData()[i].toString().length() >= text.length() )
			{
				if( getOriginalData()[i].toString().substring(0, text.length() ).equalsIgnoreCase( text ) )
				{
					for( int j = 0; j < getItemCount(); j++ )
						if( !getItemAt(j).equals( getOriginalData()[i] ) )
						{
							System.out.println("added " + getOriginalData()[i]);
							this.addItem(text);//getOriginalData()[i]);
							break;
						}
				}
				else
				{
//					System.out.println("removed "+ getOriginalData()[i]);
//					this.removeItem( getOriginalData()[i] );
				}
			}
			else
			{
//				this.removeItem( getOriginalData()[i] );
//				System.out.println("removed " + getOriginalData()[i]);
			}
		}
	}
	catch(IllegalStateException is )
	{ System.out.println("is Occured"); }
*/
	setPopupVisible(true);
}
/**
 * Insert the method's description here.
 * Creation date: (11/15/00 2:04:20 PM)
 */
private Object[] getOriginalData() 
{
	return originalData;
}
/**
 * Called whenever the part throws an exception.
 * @param exception java.lang.Throwable
 */
private void handleException(java.lang.Throwable exception) {

	/* Uncomment the following lines to print uncaught exceptions to stdout */
	System.out.println("--------- UNCAUGHT EXCEPTION ---------");
	exception.printStackTrace(System.out);
}
/**
 * Initialize the class.
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void initialize() {
	try {
		// user code begin {1}
	
/*		this.addItem("Me");
		this.addItem("It");
		this.addItem("Them");
		this.addItem("Meat");
		this.addItem("Mine");
*/
		originalData = new Object[5];
		originalData[0] = "Me";
		originalData[1] = "It";
		originalData[2] = "Them";
		originalData[3] = "Meat";
		originalData[4] = "Mine";

		// user code end
		setName("EditableJComboBox");
		setSize(130, 23);
		setEditable(true);
	} catch (java.lang.Throwable ivjExc) {
		handleException(ivjExc);
	}
	// user code begin {2}

	try
	{
		editor = ((javax.swing.JTextField)getEditor().getEditorComponent());
		editor.addCaretListener(this);
	}
	catch( ClassCastException c )
	{
		System.out.println("The Default Editor for " + getClass() + " should only be a JTextField.");
		handleException(c);
	}

	// user code end
}
/**
 * main entrypoint - starts the part when it is run as an application
 * @param args java.lang.String[]
 */
public static void main(java.lang.String[] args) {
	try {
		javax.swing.JFrame frame = new javax.swing.JFrame();
		EditableJComboBox aEditableJComboBox;
		aEditableJComboBox = new EditableJComboBox();
		frame.setContentPane(aEditableJComboBox);
		frame.setSize(aEditableJComboBox.getSize());
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
		System.err.println("Exception occurred in main() of com.cannontech.common.gui.util.EditableJComboBox");
		exception.printStackTrace(System.out);
	}
}
/**
 * Insert the method's description here.
 * Creation date: (11/15/00 2:04:20 PM)
 * @param newOriginalData java.lang.Object[]
 */
public void setOriginalData(java.lang.Object[] newOriginalData) {
	originalData = newOriginalData;
}
/**
 * 
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private static void getBuilderData() {
/*V1.1
**start of data**
	D0CB838494G88G88G9CF954ACGGGGGGGGGGGG8CGGGE2F5E9ECE4E5F2A0E4E1F4E135D7EBECD4C594C693919193C8B0E988BFD6825A9FA4E0A831C1B43892C30098FE1408069F1A78039F44204178A2BA3B5BEE37AB1436B4BCD424D288E217EDF9A414DA0DFD492BE8C34B32B10D74F6F73A3D6D5DFBEF6F5E6D83CA741CF9ECEF4B965460A6DF664EB9B367B5671C19A515C7DCC59EEFA5A11EA2A2FE67BD04ECAFA324BDFFFF05244D20EEBFF17C3E831413973A8AE11C8B3CE2B67F2AE6
	B70C2500DC2061389346B2F226662020EF94F4890F1710CDFD3866DA4DB702D68F738E409C002B0F2F4F073104E9FE2C21DE2C6B1E67A2BB571F706EC1B98E19928793FCEF23982B48B24F3B8954B54C6C44BDB64C178250F99FF111DADC5FB11878B9E6A443B1C5578D5D26317EF0A22D0653362AB5B43445D5DB6955681596A359EBB49B2324279FE53EDC93E0B288799E665B992DA30CFA964377EAC0C106FE209669F52A5BFF895023CA5FFEBA51A8EC0F5ED9C6725E50FDFB2771EDD465BE3954895F6627BF
	ADBE444E61239B986F2D40A383EEFFA5209E90850C823E865EBE72F9578FB0B6755A60C752501BCC33D90BDB570CB855603B2181B802F61D468CABCE88BF63711AD096FB6E873EEF763F5E47E312FBD61E77526B4F122DAB2EFA0B3344E4EBEE1DF76EG0FC56E40C4CC386ED727553DEEE0F2BECE56FD13B8F5AF1EE9780A32684E5BFC523BD548596150DDB508E73D1665C099FCFBC4FEF07A6B0C9EF750F9EEC6989D67B670368C4A5C0816CADB5A8B971032536DD9631063EE72B6EA5C06AEED2AADCF323C1B
	21F11D1E0CAD7399FD0E034EEDB9DCAB6BA487F83BGBC3FC894EB35A769760F8366831682B6823681F6A5E50EDD3CD17B8FF32CC3316C66342A4529C578DE92598FA39D35AD65321E3295BDC6A30A25E2692508D8D392419874CE5AD79A415C0F010E0BD42716E22B068E5215A435291562EB8EC44AF10DC59937CBCDD2D010B40914FBA4827A0926246C9BE6DC3169448A4EBF9141B399D6A2A0918440B70FCB8541F8FD233B7D67G2D3A0C43A2A66FAA35F48A9D245DB08653E697FAC2666A986737D86F0842
	37294B9817FA10D6G736D99B9EFC6AA9CF2DA55981A2FD8E332465607319F93C3664CFBD119B32DFB16105066ABD9F306650841F3255E18DABB85CCA65A45675D6168143D4448DEC37F85DCC77A9E9D3E07A43FB4F3D663DEBC2B9E30FD9C305010B12E3CD945F336D9C9D119253FDB10E0B4BE91F1EE67BCCCEFE1738247F7F56DD47B9FA6380E467FD5870FC5B00A778F9D263A5DCACD2A4761A3A5F865D14CB1D11FFDC6E64F81C6B7211EDB2000D2997A61A8561CE25B165A1B36E9A735EDD5CFCC700FB3DD
	29F42FB9518A242ED0947B2EB182751CD9FFBA0A3590B3F41D32C349EC917C032C9629C613E0F40B1156EDCE7F16E0BE8F52B1518B0E92FC588CABDA9A7B8AFF87ACD73EC49FACB589C70D2D092CB330CE5F2F29F444885FA8781E494B545BC8352C37E3371FA1EBEE9D77E63BAFF21634FA4BCCDEB3474C09FA63766C73E4377B906B87021E62F63E1440185E7BA211B93E520732DAC1C794E01AB2471F76A0DF7691115CAD062671D0E6E27C28378C4F048E25A934DF54F1B59337A73C95D3F89191C34E1F6745
	98EA0EDE475FC9FF04508F34FF16A963F677D7796E19BF68A22B771EE1FB337529B5A2DEEFBB62964A3CFB3EA8C459D5C3EEFF5D1074F5374F59FB1BD2A9A3263236AF6CFC51670CAB272D7185E16CD32D14FD9912FED47A741A8F6D45FBC110C5AD5E8A5D6FED538A36C407A677C8EE6F0FFE4CA78DF833GAEC0BEE0CB46765F8A4F12875C1FED23F02D690A56B929F0043D05C5BE1E6BE29EA8AA12398EA33FFB0659F9FC8CBA4BAC371F53BEBC0534F3B03FE4C9DBDA7C537702AB7E6C3D20431F3D97C47C65
	1C8EB21CB73F647F691F2ED7703CFFFE8473238FAEF5A0F71AB4C6F6D8E877D3A179BF239B66A67A83D88E58847071FC88337396B5F3A9057B2A43726DDE8FF31E27ABD8DCB08767GEDADDB77CE48D97F389E1BFE9840797BCEB979684349C97C36C9FC0CCD1147495FBC09DF64584F6BE443F1D9A73BB66492ED6505E97F7B6C36F93E550931A3D2CD0E4D7E2C184720015D69361526DC2EEB44791F2ABEF8BEEB5FC2748BF963E2DC7DF83528017C7B56C0B6F05D2F94CB1F72D6ADA279B3BF71CCD77BF9C2E7
	019837F5D5BBEA69E6G4F3EB06D76D781DA815DG93B0ABADF37883FF085793364CA65BD6E27D2C6AE5B1C5D4BAB2C146446232EF17E0AEAC84B97999D9970AEBE00CA88322AAD124E1B1C9C06C308C13DA769827324EA86C4C896299EF81B9BB8187075D7E906017E1A977EE71D1EEE3967B5A62895AE2305730ADEB25A7B0FDDDA6828FFEC31B816CF53D06ED9BC914AF694381BCD75BB09911536E84704CACB551EF7325027EC8G632351BEC666343FCACA1DB54CC3A0564FAC4198283AEA2B0A265EE66D38
	93966A894EF785277769396003FDDA9E64B51E20DDE32630E7E5906FF9232FAFC5B331DB95AC4B50AE27BA0C140A167C8DD0CB878805D37B8A8F87GG9491GGD0CB818294G94G88G88G9CF954AC05D37B8A8F87GG9491GG8CGGGGGGGGGGGGGGGGGE2F5E9ECE4E5F2A0E4E1F4E1D0CB8586GGGG81G81GBAGGGC987GGGG
**end of data**/
}
}
