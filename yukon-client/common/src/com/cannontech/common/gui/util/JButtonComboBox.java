package com.cannontech.common.gui.util;

/**
 * Insert the type's description here.
 * Creation date: (2/13/2001 12:42:39 PM)
 * @author: 
 */
public class JButtonComboBox extends javax.swing.JComboBox implements java.awt.event.ItemListener
{

class JButtonCellRenderer extends javax.swing.JButton implements javax.swing.ListCellRenderer 
{
   public JButtonCellRenderer()
	{
	  setOpaque(true);
	  setBorder(null);
   }
	
   public java.awt.Component getListCellRendererComponent( javax.swing.JList list,
										  Object value,
										  int index,
										  boolean isSelected,
										  boolean cellHasFocus)
	{
	  setText(value.toString());


	 setBackground(isSelected ? java.awt.Color.red : java.awt.Color.white);
	 setForeground(isSelected ? java.awt.Color.white : java.awt.Color.black);
	  
	  if( isSelected )
			setBackground( list.getSelectionBackground() );
	  else
	  		setBackground( list.getBackground() );

 
	  return this;
   }
	
}

/**
 * JButtonComboBox constructor comment.
 */
public JButtonComboBox() 
{
	super();
	initialize();
}
/**
 * JButtonComboBox constructor comment.
 * @param items java.lang.Object[]
 */
public JButtonComboBox(java.lang.Object[] items) 
{
	super(items);
	initialize();
}
/**
 * JButtonComboBox constructor comment.
 * @param items java.util.Vector
 */
public JButtonComboBox(java.util.Vector items) 
{
	super(items);
	initialize();
}
/**
 * JButtonComboBox constructor comment.
 * @param aModel javax.swing.ComboBoxModel
 */
public JButtonComboBox(javax.swing.ComboBoxModel aModel) 
{
	super(aModel);
	initialize();
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
		// user code end
		setName("JButtonComboBox");
		setSize(130, 23);
	} catch (java.lang.Throwable ivjExc) {
		handleException(ivjExc);
	}
	// user code begin {2}
addItem("Create");
addItem("Simple");
addItem("Complex");
addItem("Strategic");

	this.setEditable( false );
	this.setRenderer( new JButtonCellRenderer() );
	this.addItemListener( this );
		
	// user code end
}
public void itemStateChanged( java.awt.event.ItemEvent e )
{
	System.out.println(e.getItem() +" " + e.getID() + " " + java.awt.AWTEvent.ITEM_EVENT_MASK ); //e.DESELECTED == 2 e.SELECTED == 1
	System.out.println("  Item DONE!!" + ((javax.swing.JComboBox)e.getSource()).getSelectedItem() );


	if( this.getItemCount() > 0 )
	{
		this.removeItemListener( this );
		this.setSelectedIndex(0);
		this.addItemListener( this );
	}
	
}
/**
 * main entrypoint - starts the part when it is run as an application
 * @param args java.lang.String[]
 */
public static void main(java.lang.String[] args) {
	try {
		javax.swing.JFrame frame = new javax.swing.JFrame();
		JButtonComboBox aJButtonComboBox;
		aJButtonComboBox = new JButtonComboBox();
		frame.getContentPane().setLayout( null );
		frame.getContentPane().add(aJButtonComboBox);
		frame.setSize(aJButtonComboBox.getSize());
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
		System.err.println("Exception occurred in main() of com.cannontech.common.gui.util.JButtonComboBox");
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
	D0CB838494G88G88G8DF954ACGGGGGGGGGGGG8CGGGE2F5E9ECE4E5F2A0E4E1F4E12DD6EFEC94C59447C4A0B1CD4458C4C20CA9AA26B1A4E0A8C9A50D290D1F048FFC90B566BE2870418FE8B029D181635C353D72373450C009C42BADC1C92925AD6D7568F17DCB2F2560051E359F1A6876EE2E5D5E5E6E323BC78B564477F6E66E36F8871AF849AF33735E4CFBEF5E7B3D19A3755F95D439BD7504382B887FF53B89D9D3C348574FEC1D923295344CCB9C3F6F81F5E4DDFBA50CEBC0D7ED4F
	F7DFBF86A3ECA505A043319C46DAF260FA83176F60F28137071062B82EB9BDF20E4BFCB08F8372G85714C7A43366715FE9F1FCFBFD5C04A5F3C60B90EF69CB68548AC5B5B4C47C632493DE396FD85C70E71BD53B04F876061AB487A4B17E06C8DA7E62FC535E4EBD4D2D5CD35E8F42EF5B6A53726ACD9717B4786D216252903DAF2C69B5096E1FDE88EED2E9EB9E45BA0249666EF59321D2368A78F3EDF8414G58998297FAF90CD3FFEEA2875FD1ABCE64B8C3034CE26F13B3E748CF6D2FBEEB677B3D8E4C6B92
	688AGC500F781DFG9A664B3C5F006E9479207DA20C7DB396C41C5454FEDD9FD0E246CDADC69578767BC143E537E9D4B3E20430DA8E0F34E40967E1E07BA67E73BE1613428ED1570D37DFA22EADB7BC55D9F262AAEC73344D33DC0466B3B9E13E73F27A1E1BDF4E3B653EFFA3CE5FFBD7E995D5D9FC976D7A4933446D2CF178AE1D47DA8F0DE12DB743371B730049EF59724AC4C64EB878364D8D1C5703EEB1A138B1D5A3E2992FDCC7EAAF0FE74D435A32FE0FAB41FBAC11090571E277956C25C6254C5B8B08G
	94C09E2090D02288EEDC6C7879DFF2A3AC9956C0CAD6E254A0EC6F6E0E53B052C54B10C6D5531254A81D148CD91AD128C9781A3D9D987B4C32FD1F5A1C0D020F912AD2C332E4CD856BD212DA54B0591A7D9DF538462036B6A2A7A9B8C86AC45875F41E02D111CC6BD7BDA6D9B43302690FF4E2AE6FCA13E0918C40B74BCBEDB7665C8567AF87D4A6C59ED65A76EED0C3255061631A16C86991BC8989A731D6AF583D5DG5FEDGE66B188FE579B05F1836D352F952E1E7C80EE27812F1CF744658985E1725C9D16B
	CF26C42D070EEFA0AD3B4632565A66FD1257BA79A067A336CD0C0B4DDD23D3D97617A633737F3F027978AC2B0F0A7F41FECD3AD677FB30D66754B22F8FB0250A9CFF5E5948B8BBA019D43074978388C6E3190C33359FDF41FB155E252AB5C4F52A466043649CA93FF2B8435F38166656FEDB2E835F8701E0E6DACE3A101312E59972CC4A22935432E4F5B623BF54053E4C540C1EE995616BC4975EA75A8270BD3D3E3E6B245D872ACA6D2053DB387E344DD52A50A484BD2825D40B494FF5E33D93749E6F1546EE2F
	4DF5A505FD475E314D52D1BC03A1A7A195583A64F195F93CED245E11E7FCE371BD56D5414705A64147667B6BC9696F67BD596E41359B06BCBBD4462777558C9FD9BCF53E6CF11F317B054BCD9667074D183387DFD4D67FADFE34C334B2EFBD20C59375FF5AF75651E33C70031A2230B426733B2227966BC16F24A8DCCD584CB752B9DB5DF372815DA44F9F53BF590379D39C77GFB631BC2620D9F52C44E7E3AAE5E727CC481A9B9516EB91A230717B4162BE735CC4EDA52EF79D7D751368BF4FB53E7BD62F75ECB
	7D2629C5E57BCA64F13E6AF7661449DE739F01B1AE9B26B58A04DF94E7FA430F71621D49453C17FF8CBD6C3DA67ABF6F8D96EFCFG39D48F7AB6C0887087E005AEE29F3F5AC19E71368CAF42152FCA4A44322691F7FCEF8563B91FFBFB2B844F113B763D9C346BD1GBE0B53FE7B8339FBBE94485E73378259FBBE9228E3F23061FC8105FEBA106BCEE07CFEE581399007478D4493BA0D927D8E46BD99927F07DD9077BEC0AD485BGE1G635A12FDB664D1B14876597BDE8EB97B9773036FF7AB0069DD4B74985B
	79E57A1AD1279E4FDC65F66A1FDB2647B35C647B9957371F97DCBF383D10A8DBFBF27E7776990CB753FC8CCFB61166A78ED4B34EBD76D04E657E1F76FA3570DF346DF9D23C6ACBF72EDE4B67FE0B7878D12449415D1AA066FF8F6808D966ED8674824200B9D370E8DF200571F7B8A6DB7D16A5C5676CAE93641D1469C2C60C45E536A3FD1843A5303396D08258895813365BF96D87E6B70B4D6158AC9D546CFFD316605819E0EEBEFF9BFC74FF308BC13C8FE6B438E213E8DF48AF85319616265BC2A6EB8FE21E8D
	F9F64EE2CB395C97C4AEAAB4EE0BB9777BF247956DFBF4DCCA1FFDF7013D48BD1D5B3C5307EF19960F1BB4FD7E3BFD35E959289956CC99AF403F81D0CB878833BD1559D186GG388EGGD0CB818294G94G88G88G8DF954AC33BD1559D186GG388EGG8CGGGGGGGGGGGGGGGGGE2F5E9ECE4E5F2A0E4E1F4E1D0CB8586GGGG81G81GBAGGG0B86GGGG
**end of data**/
}
}
