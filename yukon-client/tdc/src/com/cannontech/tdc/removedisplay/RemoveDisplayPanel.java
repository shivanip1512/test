package com.cannontech.tdc.removedisplay;

/**
 * Insert the type's description here.
 * Creation date: (1/21/00 12:23:41 PM)
 * @author: 
 */

import java.util.*;
import com.cannontech.database.*;
import java.awt.Cursor;
import com.cannontech.tdc.TDCMainFrame;
import com.cannontech.tdc.logbox.MessageBoxFrame;
import com.cannontech.tdc.utils.TDCDefines;
import com.cannontech.tdc.utils.DataBaseInteraction;

public class RemoveDisplayPanel extends javax.swing.JPanel 
{
	private Object[] removedDisplays = null;
	private Vector displayNumbers = null;
	private javax.swing.JButton ivjJButtonCancel = null;
	private javax.swing.JList ivjJList = null;
	private javax.swing.JPanel ivjJPanel2 = null;
	private javax.swing.JPanel ivjJPanel3 = null;
	private javax.swing.JPanel ivjJPanel4 = null;
	private javax.swing.JScrollPane ivjJScrollPane = null;
	IvjEventHandler ivjEventHandler = new IvjEventHandler();
	protected transient com.cannontech.tdc.removedisplay.RemoveDisplayPanelListener fieldRemoveDisplayPanelListenerEventMulticaster = null;
	private javax.swing.JButton ivjJButtonDelete = null;

class IvjEventHandler implements java.awt.event.ActionListener {
		public void actionPerformed(java.awt.event.ActionEvent e) {
			if (e.getSource() == RemoveDisplayPanel.this.getJButtonCancel()) 
				connEtoC1(e);
			if (e.getSource() == RemoveDisplayPanel.this.getJButtonDelete()) 
				connEtoC2(e);
			if (e.getSource() == RemoveDisplayPanel.this.getJButtonDelete()) 
				connEtoC3(e);
		};
	};
/**
 * Dummy constructor comment.
 */
public RemoveDisplayPanel() {
	super();
	initialize();
}
/**
 * Dummy constructor comment.
 * @param layout java.awt.LayoutManager
 */
public RemoveDisplayPanel(java.awt.LayoutManager layout) {
	super(layout);
}
/**
 * Dummy constructor comment.
 * @param layout java.awt.LayoutManager
 * @param isDoubleBuffered boolean
 */
public RemoveDisplayPanel(java.awt.LayoutManager layout, boolean isDoubleBuffered) {
	super(layout, isDoubleBuffered);
}
/**
 * Dummy constructor comment.
 * @param layout java.awt.LayoutManager
 */
public RemoveDisplayPanel( String singleRemoval, String displayNumber ) 
{
	removeSingleDisplay( singleRemoval, displayNumber );
}
/**
 * Dummy constructor comment.
 * @param isDoubleBuffered boolean
 */
public RemoveDisplayPanel(boolean isDoubleBuffered) {
	super(isDoubleBuffered);
}
/**
 * 
 * @param newListener com.cannontech.tdc.removedisplay.RemoveDisplayPanelListener
 */
public void addRemoveDisplayPanelListener(com.cannontech.tdc.removedisplay.RemoveDisplayPanelListener newListener) {
	fieldRemoveDisplayPanelListenerEventMulticaster = com.cannontech.tdc.removedisplay.RemoveDisplayPanelListenerEventMulticaster.add(fieldRemoveDisplayPanelListenerEventMulticaster, newListener);
	return;
}
/**
 * connEtoC1:  (JButtonCancel.action.actionPerformed(java.awt.event.ActionEvent) --> RemoveDisplayPanel.fireJButtonCancelAction_actionPerformed(Ljava.util.EventObject;)V)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC1(java.awt.event.ActionEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.fireJButtonCancelAction_actionPerformed(new java.util.EventObject(this));
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}
/**
 * connEtoC2:  (JButtonDelete.action.actionPerformed(java.awt.event.ActionEvent) --> RemoveDisplayPanel.jButtonDelete_ActionPerformed(Ljava.awt.event.ActionEvent;)V)
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
 * connEtoC3:  (JButtonOk.action.actionPerformed(java.awt.event.ActionEvent) --> RemoveDisplayPanel.jButtonOk_ActionPerformed(Ljava.awt.event.ActionEvent;)V)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC3(java.awt.event.ActionEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.fireJButtonDeleteAction_actionPerformed(new java.util.EventObject(this));
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
private void deleteDisplay2WayData( String element ) 
{
	String query = new String
	("delete from display2waydata where displaynum = ?");
		
	Object[] objs = new Object[1];
	objs[0] = new String(element);
	DataBaseInteraction.updateDataBase( query, objs );
}
/**
 * This method was created in VisualAge.
 * @param element java.lang.String
 */
private void deleteDisplayColumns( String element ) 
{
	String query = new String
	("delete from displaycolumns where displaynum = ?" );

	Object[] objs = new Object[1];
	objs[0] = new String(element);
	DataBaseInteraction.updateDataBase( query, objs );	
}
/**
 * Method to support listener events.
 * @param newEvent java.util.EventObject
 */
protected void fireJButtonCancelAction_actionPerformed(java.util.EventObject newEvent) {
	if (fieldRemoveDisplayPanelListenerEventMulticaster == null) {
		return;
	};
	fieldRemoveDisplayPanelListenerEventMulticaster.JButtonCancelAction_actionPerformed(newEvent);
}
/**
 * Method to support listener events.
 * @param newEvent java.util.EventObject
 */
protected void fireJButtonDeleteAction_actionPerformed(java.util.EventObject newEvent) {
	if (fieldRemoveDisplayPanelListenerEventMulticaster == null) {
		return;
	};
	fieldRemoveDisplayPanelListenerEventMulticaster.JButtonDeleteAction_actionPerformed(newEvent);
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
			ivjJButtonCancel.setMnemonic('C');
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
private javax.swing.JButton getJButtonDelete() {
	if (ivjJButtonDelete == null) {
		try {
			ivjJButtonDelete = new javax.swing.JButton();
			ivjJButtonDelete.setName("JButtonDelete");
			ivjJButtonDelete.setMnemonic('D');
			ivjJButtonDelete.setText("Delete");
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
 * Return the JList1 property value.
 * @return javax.swing.JList
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JList getJList() {
	if (ivjJList == null) {
		try {
			ivjJList = new javax.swing.JList();
			ivjJList.setName("JList");
			ivjJList.setToolTipText("Hold SHIFT+MOUSE_CLICK for multiple selections");
			ivjJList.setBounds(0, 0, 389, 126);
			ivjJList.setSelectionMode(javax.swing.ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
			// user code begin {1}
//ivjJList.setSelectionMode( javax.swing.ListSelectionModel.SINGLE_INTERVAL_SELECTION );
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJList;
}
/**
 * Return the JPanel2 property value.
 * @return javax.swing.JPanel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JPanel getJPanel2() {
	if (ivjJPanel2 == null) {
		try {
			ivjJPanel2 = new javax.swing.JPanel();
			ivjJPanel2.setName("JPanel2");
			ivjJPanel2.setLayout(new java.awt.BorderLayout());
			getJPanel2().add(getJPanel4(), "East");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJPanel2;
}
/**
 * Return the JPanel3 property value.
 * @return javax.swing.JPanel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JPanel getJPanel3() {
	if (ivjJPanel3 == null) {
		try {
			ivjJPanel3 = new javax.swing.JPanel();
			ivjJPanel3.setName("JPanel3");
			ivjJPanel3.setLayout(new java.awt.BorderLayout());
			getJPanel3().add(getJPanel2(), "South");
			getJPanel3().add(getJScrollPane(), "Center");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJPanel3;
}
/**
 * Return the JPanel4 property value.
 * @return javax.swing.JPanel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JPanel getJPanel4() {
	if (ivjJPanel4 == null) {
		try {
			ivjJPanel4 = new javax.swing.JPanel();
			ivjJPanel4.setName("JPanel4");
			ivjJPanel4.setLayout(new java.awt.FlowLayout());
			ivjJPanel4.add(getJButtonDelete());
			getJPanel4().add(getJButtonCancel(), getJButtonCancel().getName());
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJPanel4;
}
/**
 * Return the JList property value.
 * @return javax.swing.JScrollPane
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JScrollPane getJScrollPane() {
	if (ivjJScrollPane == null) {
		try {
			ivjJScrollPane = new javax.swing.JScrollPane();
			ivjJScrollPane.setName("JScrollPane");
			getJScrollPane().setViewportView(getJList());
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
 * Insert the method's description here.
 * Creation date: (4/17/00 4:56:44 PM)
 * Version: <version>
 * @return java.lang.Object[]
 */
public Object[] getRemovedDisplays() 
{
	return removedDisplays;
}
/**
 * Called whenever the part throws an exception.
 * @param exception java.lang.Throwable
 */
private void handleException(java.lang.Throwable exception) {

	/* Uncomment the following lines to print uncaught exceptions to stdout */
	com.cannontech.clientutils.CTILogger.info("--------- UNCAUGHT EXCEPTION ---------");
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
		setName("RemoveDisplayPanel");
		setLayout(new java.awt.GridBagLayout());
		setSize(422, 186);

		java.awt.GridBagConstraints constraintsJPanel3 = new java.awt.GridBagConstraints();
		constraintsJPanel3.gridx = 0; constraintsJPanel3.gridy = 0;
		constraintsJPanel3.gridwidth = 3;
constraintsJPanel3.gridheight = 2;
		constraintsJPanel3.fill = java.awt.GridBagConstraints.BOTH;
		constraintsJPanel3.weightx = 1.0;
		constraintsJPanel3.weighty = 1.0;
		constraintsJPanel3.insets = new java.awt.Insets(10, 15, 10, 15);
		add(getJPanel3(), constraintsJPanel3);
		initConnections();
	} catch (java.lang.Throwable ivjExc) {
		handleException(ivjExc);
	}
	// user code begin {2}


	if ( displayNumbers == null )
		displayNumbers = new Vector(15);

	// Init our RemoveList  			
	String query = new String
		("select name, displaynum from display where displaynum >= ?");
	Object[] objs = new Object[1];
	objs[0] = new Integer(com.cannontech.tdc.data.Display.BEGINNING_USER_DISPLAY_NUMBER);
	Object[][] values = DataBaseInteraction.queryResults( query, objs );
	
	if ( values.length > 0 )
	{
		Object[] addedItems = new Object[ values.length ];
		
		for( int i = 0; i < values.length; i++ )
		{
			addedItems[i] = values[i][0];
			displayNumbers.addElement( values[i][1] );			
		}

		getJList().setListData( addedItems );

		getJList().repaint();	
		getJScrollPane().revalidate();		
	}
	
	// user code end
}
/**
 * Comment
 */
public void jButtonDelete_ActionPerformed(java.awt.event.ActionEvent actionEvent) 
{
	removeSelections();
	
	return;
}
/**
 * main entrypoint - starts the part when it is run as an application
 * @param args java.lang.String[]
 */
public static void main(java.lang.String[] args) {
	try {
		javax.swing.JFrame frame = new javax.swing.JFrame();
		RemoveDisplayPanel aRemoveDisplayPanel;
		aRemoveDisplayPanel = new RemoveDisplayPanel();
		frame.setContentPane(aRemoveDisplayPanel);
		frame.setSize(aRemoveDisplayPanel.getSize());
		frame.addWindowListener(new java.awt.event.WindowAdapter() {
			public void windowClosing(java.awt.event.WindowEvent e) {
				System.exit(0);
			};
		});
		frame.setVisible(true);
	} catch (Throwable exception) {
		System.err.println("Exception occurred in main() of javax.swing.JPanel");
		exception.printStackTrace(System.out);
	}
}
/**
 * 
 * @param newListener com.cannontech.tdc.removedisplay.RemoveDisplayPanelListener
 */
public void removeRemoveDisplayPanelListener(com.cannontech.tdc.removedisplay.RemoveDisplayPanelListener newListener) {
	fieldRemoveDisplayPanelListenerEventMulticaster = com.cannontech.tdc.removedisplay.RemoveDisplayPanelListenerEventMulticaster.remove(fieldRemoveDisplayPanelListenerEventMulticaster, newListener);
	return;
}
/**
 * This method was created in VisualAge.
 */
private void removeSelections() 
{
	// this will delete the fields in less important tables
	for ( int i = 0; i <= getJList().getMaxSelectionIndex(); i++ )
	{
		if ( getJList().isSelectedIndex( i ) )
		{
			deleteDisplayColumns ( displayNumbers.elementAt( i ).toString() );			
			deleteDisplay2WayData( displayNumbers.elementAt( i ).toString() );
		}
	}
	
	removedDisplays = getJList().getSelectedValues();			
	
	for ( int i = 0; i < removedDisplays.length; i++ )
	{
		String query = new String
			("delete from display where name = ?");
		Object[] objs = new Object[1];
		objs[0] = removedDisplays[ i ].toString();
		DataBaseInteraction.updateDataBase( query, objs );	
	}

	TDCMainFrame.messageLog.addMessage("Display removal successfully accomplished", MessageBoxFrame.INFORMATION_MSG );
	
	//setCursor( new java.awt.Cursor( java.awt.Cursor.DEFAULT_CURSOR ) );
}
/**
 * This method was created in VisualAge.
 */
private void removeSingleDisplay( String displayName, String displayNumber ) 
{
	// this will delete the fields in less important tables
	deleteDisplayColumns ( displayNumber );
	deleteDisplay2WayData( displayNumber );
	
	String query = new String
		("delete from display where name = ?");
	Object[] objs = new Object[1];
	objs[0] = displayName;
	int res = DataBaseInteraction.updateDataBase( query, objs );	

	if( res > 0 )
		TDCMainFrame.messageLog.addMessage("Display " + displayName + " successfully removed", MessageBoxFrame.INFORMATION_MSG );
}
/**
 * Insert the method's description here.
 * Creation date: (2/23/00 3:53:03 PM)
 * @param str java.lang.String
 */
public void setListSelection( String value ) 
{
	for( int i = 0; i < getJList().getModel().getSize(); i++ )
	{
		if( getJList().getModel().getElementAt( i ).toString().equalsIgnoreCase( value ) )
		{
			getJList().setSelectedIndex( i );
			break;
		}
	}
	
}
/**
 * 
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private static void getBuilderData() {
/*V1.1
**start of data**
	D0CB838494G88G88G74F854ACGGGGGGGGGGGG8CGGGE2F5E9ECE4E5F2A0E4E1F4E13DBB8DD4D4571944E0B49123A11810288929A4F54F32ADE94C566458D3171A9E5AF8B6F60FB9352DFBE2CEEDE3F7594626E4CB9253FD7C0D28A890CC81B316D1C94404D2225946D0C4E0861C11B110B09AF2EA22A3730041E15EB8730681FF777B6EFD5F1B4770063F461DF33E735E7D6E3D5F7D6E77F73FFB6F9BE1F7E5D2FEF64EEEC1484E97145F0759021071D7C1F8FCCDCB9C61E244B40320796D83
	A8916A7CF9701C8FF589C9E906E2C16AAA00F2B214B7BF18E6A80377D2613233299EDE220CD38EF55FAA6ECA9FFF1C1F6451B8DBD9797A25C2F86E82D88AB8FCE60AB87EBD174A957CEA85CF101DA3886BFB314D23170C8A6EA814BB819281D2FAF97BD5705C46C61ED9FFD4E9F7762EA4A1730787F3F6A29D8DCD022ABE5E572ABCCD42B733B77761D8BFE5FCE21F98A8AF874049678A3FB1FD844F064E0BFD0DBD52E0ED0F536B153C325853DFAB3BFAEA7D6220B4A43A5C811F47B9D2DF7FA9ABDAF831536995
	BD5057530F74777B8B98BDC178G4AD98C77AB9B0E198A6F69G9BD4FC6AC944B738538C8EG0F1B66F96CE003B20F6EAB5F94367C581BDB94E51E31EEBE3F94F7F81E8941D737D6B01DD498D0761B202EF0A04DE08230G38GE2815EE236E04AFF971E4D97E433B4B8A8F91BFD3E960F4BFFCAF2099EF82F2F079A85772958A379DD0240753D61D20D8EBF63816F776BA97763B2D9FA04F41F7269A3427A475AF3366A48E47D523A1CC583DC964B8642B261E39F74C79BFB6D40E85B9CBD76E702F66C0C5B245C
	FC1D3117BDFBA8E713C2E70BE66C1A0B286BAF182E0D701E2D58874777B13CCF0367F67AA23399AC47C05D460BE49B5D3B0897FB5E7DC2717BF6DDB9A426B567D8BD1C07731EC8DE2A5A11F61C070F39D565250E6113B5F84E4BBFB39E313C826ACC03E986EEDF7B8B50EFE3211C84309CE08D40E600C2AF3473120D39737FBCC99B6BF47A6516205B63927D826F3B5F7087F80A43325FE97386E42737C7BC63743B1D97BCE2C0D05A3C47E25005D17D6A186D774098A7C42F68F74AEE498B541D0322AC7A833C4D
	C70692EC6397D92D43BDA842G03BE0168B69BCA512F1D8179344F651445F08BDEEFE3BA99F21E810AC0G5E39DCBA36213C1AE07E5DG7D2A9CD6B1FA6D229F82C6FD3DDD12AE86FD8E1C09305C0BF2EEE43163AA3CA7F8C9466D5B9137854A3BA522731A618D8D9D0B3B8759F77AC74847FACEE2EC2E1648E6FE5DCDB6E3597930D0736CC9DD1BE171D2D2ECC60A741DC1C69379626517ED5DCA1FEAC95FF77E9E60E36CBAC9E364FE4574F7293AFAE2876AEAB64CA199E015AA633FECB5F11BEDF186C432528E
	BF9818688ACB1C7379AE739B2407F161CFAA7FFFE17875C0B34F17E62871116CD630F97DC961A3FF8A7CCEDEF64F32B136CEF10C494A194B2826G57A5F1C8744A9651A7FADD7092D06A0E94206DA9FE5BAB29FDBEE2F89F7839999CAB20620F9720AFBAE5596F3E901445AED1165D5E3EF07D89B6D6A0F841978E91B4564785B8BFA984FE2E366FAAC09F69113CDE11A9CB6D22541FE6BEAAFA44C1E05AAC853DB2477FED9B5A79C5F1C409916736E172B3647484B15E70DCA15539836760F78F0289E0489212FD
	683F277D659A99B9G07B94FB0BC63FC640B2183640BD5179F92D61EFBBBC7EFADC9FC5812136163FEA5DC8A7BA26729F33BBE6FE7D82CD07001DC069B29C7B968A5AC5C7EDFAAC4DAA6986383G7BA5327FFF5B0E7594E39443B7CB9E8F9727AA676F6EA8C63D081702A204E68CE66D2A6C3E3F630D083AB30A9CF97D3F6EC0B9FAB4F130490FF2F437D3BE6882FE2E82AC3A44E5399E1EB55006373B5CC16D62201C8C304A4F5B6585384CF83B46CEEAE78F0CEE67027232G65003FBD047AE3B96000F348B9DC
	9B88018D56571FE49263795DF7D90C467669814A637EB308B29464B4C3B9C01DCC3A7E51CE4A6392AEA6899B166C091A2FDAE54E2FC78E6B5AF4AB65F18D3BF04C9428DBA113FEEC055A3524B99010FA5CEC99D3E41BD52835850EFB3DF0BBBCFB5D7E00EC83E79DA6BD646EC4FEF11DD3504A3A9F5FCEB9489A9837A0886BFC9071452D3BB53CB7812E9FA08E005B5CAB6D07511719215472C727686F157C03222B9AA5DB6B8C4935ACEE54B6336AD6FCBFC47E756221E64E27F832A5A84B12570C4B3E07B7BD97
	C9504EA886E5372716116938B0GB67A57C3E7844A490F5AA9A75F94A45DFC2D34C961FF75B76E979AEE1466686558330B76661806143D44D0D8B7D73FE5484D7872352DDCCECBBA8A951BC800B62B07C8CE2F1B75641497A2B9ED6F7C7B64D4DC0476B540E5E481DD4B6239E6DD6968528191F17E7BBAC94F6540DBE7087C615D5688BD70B1262D0757BBC98F33C334962E9A13132F8C05730935213139854FD54F8DA14F1BC21179341B6133C2117974EF02B44702E158EB8DD3FE75F4917A7C9AA8E7G640D24
	990CG8DGE7C74857666CBCA2CC103F5AE02F6877BABDDD2396A84AA3F761383026A86556DDA8DF36268834DEA736CF34AF9A9E99AD8B5E6F11897BAD0A6847F5DB54D5219F6B3826210E2FE96947A35734E5C0EF9DC086G576B095682051F3C4B64DF575A3431EFC551611C9DD1E2DF43E54E5F594B1AFD8A6CE1797C0E59F1FE0BAECFBE4764FA8D16E04CCF05FE2B81B68288D7528CD5GCDG2EAB245BE7F69D1BC8374C1A9D1299F0D6D82B4C11B9052A0BFF680854C56C1551FC6B6B705BE37A7DB1221C
	94C1074B7D838B2DC54B2F10BF8DF6CE4F0E2ECC58EF73953DFEB91FE03F022B13578F5F6B7C0C2D23C668D78740F14BE544B9206C39CA73910758B913DA16D8B9D9AD7FAE58BD49F18966CF01D7BEC67150D447184AFE055B615A226839744F0A74F369670B74F369970ACAB89EE8E8F754D47FE2517839766F0A74F26D3DECDEAF48EF8ABAF975473031D6668DBE553ECDE953AACB6677850AA52FB6C4469254E3BA407A2B94CB04A632694C2B14DF3519B1F657DCCBB3D8G4EDEA35D5BDBA3EDD6063A595738
	FE97DD9B2BE79E9B363C016BF2AA542F865888308540F81DE2425C628963FD6B30EE3C67F66CE079C68350332BB4EF14C4576FEDA57A7A0DAB41FCB1A0857DBDE2248E6FAEA9D76B429149A60B03288A5EE6916BAFBB7DFD62989BD8427A733AE87DD3CA22598937059F7C210859D9A0A086D81034B8E527E2839BBA34B6F0D875250FAC11BABB8BB272DD0F6E131CDEF227169E6A074B3A629314F5AC74CF38CEF955CBFE5C3341CEC78A4A02D0FC835BED8B106DA5C33B95G9B81322E7333963EA6BE9A42B52A
	576F9C04E9F679449EA10375FDC6263E5DD076G448124G2C3EC1E31A5F4679711D4291455E1306A26D9D477AA0A64DE00DA1FBBFFC0C6C7DFC8C59FB69F13477C4A827822447103D67B7C54AEEC3CCD8EE19B151F225A7197F945C42F322FC957F94439BB5F81EAB594D144F56C1DD42AD941FFB58BA1F85654299047BBA431920DC27628A5AA80EB74CA07B4FAB0DEE7FBBCA756D3F24D4BF3E15150E9F5FAACB2359AD5755C5BB2EA1E07A6F474DC0FEEFE16573AA7F1D961A679A95F71F056639F186693C6E
	1351F45AD8F9B33CF3B93E9F203B86632DE986472DE4E76F1F475CC44D3DB0BB2F2FBF61F73BDA1CFDF6E6355CB6C68285CA7F38DB49B63E4EE82E0272FA004CDBA95F9AFE07722D14C76E976CB16D3AFB4D8D0E43B905315CCE6AE2437956D248377818AF4BB426876A13E391F7A0285D47605C03502096FD09551F9ED50F3207AD0A26FE5628FA6477D42E367E09D175791A7EFCEECF3FCDF35B7274D2417378C7D17751994A1C0AE772E7671983C2559D2FA9F3DBB7C43EE61DC972B4B720BCE3201C8810BA13
	FC6D43E3113E16BEB36CDF6BE7C67335D94CF7B74F0C5C175C457042ED117B120A6334AFA906BA79B632B7A3DB8757C1B9C345EDE9A13FAA1EC5FC2EB43335864A5DAA6EE90643F38EBCBB6138CF4D5DCA3B2964B193C137C20F0FF1110D71D51F555259A57739BE660703DE9196E6F7CF6CAC14CB9C0BC133G64D9641B299674C59902CA7AAC3C3F3C3C8745DFB89B5BD736150FB39E6FFF2F3D921EDEB1E4525D56D64B7D6E40A145BE0F095AFB493AAB59B939D16C53B11B4B4B25BCD74CBAA070B9DCE7733A
	9DA0816AB84F33716AC361676CA71361C7315D40746E66DA27596F7A147BF11F981159FD53EC6E9359AAFE864327EA705CD77EC90D27AB204EF4BB6D61AF1621BEE3211C84309CE08D40E60042BBE87DF93C34CE18A027F3C8BEBBACD09E4D124267772ABC3DBB4BFF59CAF1EB5913DF93165F76CAF634BD30698E653CD2F9EEF29CE0F8BE3F5F17637CFCD09787108A108E309EA0D31D5F6B25B5FCFE2DAE375CAC4B4E1EFEF672C713BB6396C3E1B4A6B7FCEE7FBE94B93702B96198E11C93AD4EFD1B454D3AB9
	11B9C59A43DB66C4669436864AA93A21EE459C0AE99F9F23B8E70AA3DCFAA345B9CB9C452F3947511FFDEA79DED64673B4DE2EBCBE55BD1A9EA07D68F6FE74AB99A3DAFC64FE3EA3B4B6F7EDED22F8958B7CAD82D88530AE0E1FA78AF3D94EE8B60DB366E6E5DEFC0C995538FE0EBA53D36BE20AB4F57C5CD52DDB520A4F0366030AFDA70534F16F68768FF56FB2902CF315B31E39613847E9BC73B7AD0D627B7765E4C721112074CD1A9B4EDBB80D74AF347901693DBC5D7CA06B0BBF672CD57AE6E9E82C9D5822
	1C333F3487ED2A66CE72A9714D7D70BC639CD022853A1664E79E85C8BC8F6A0294535BC772D9E0E172319CD46C6506AC2836B3DB737ED8602B3023E84047D8FDD3476072114B226774D74A74F37ADB4A74F37AD9E5631FC94CAB0BFE7FF7CF19767E0F6BB25F08B9E5936872ACC08CC08240D2D537D56528DB7C72E0EC2CECF575611E9A3FB610E97C2D9551673A2BE262EF944AAA706E650284EEB50874893F378275A5CBBE06D43EF928C099795DFD7DB2EF2A602BABB0AEFA44DE066631B2F30862672ABBA9FE
	9E9A21783969CE0A0F5FE9C05C51F9E9867BBC4225B39C1E673AD4DCC743E46D06E8CD5DB67BA7BD867A53E46F2E89F84C8C9D2318B99BF8CC82C887D8BF0F1FDF731879DC23E99CBA197334B1736532707E7E83E5CD9BEC22FC24E0FE1A21E9BE6D6F0E9C9EF5364E77F7ED9EA9E4D7423AF2FE7BF8936D35066753FE64670CE6AA145381B64CD7771AE60CCD2D4E002C5CA30B94730C7F38D0B0D63E1CDDA268474D2C79FC9ED5F705E35EAE55CF7A76A28F5DD0A75FC53E71C445FBC27438576917FC22DF9E61
	D8F6232B503A0047560A2D7ECF455B51E800071D8431704455616CD3F99929C41DA5G9F29GEB81B2G0478B4C3F9BC71E659D3BD8EEFDC404CF77DCEB75D0B7F779EDCD383CA2C66381D46687E3C5BB831BF6FB5327BD570C34708CFB9AF5BEF44F1245E5E0028460D0346E295E78BF4CA81B71E33F19B5D9FA29B2D0967B6EA01E7F7BCBF97DD9E0FED1AC6ACCA9BDF7CD8DF2335F22B62A73150A69160CD45EED36243B9B43723FD1F056D7830027B69BE4CCBCFF67578A50F87EB14732B7AD17BA461EE4ED7
	8DBC1B6E26B30E870F118FF483EE7E5D64833F2CFDEB2C8F34C8789D0162855C87DEB8C6BE10FEB779401B0DEC2F1CGFA8728CBA09F585208343B20F33F9ACB82E34E44A69703EE19E63FA7275C0F67455FE941B9D993A277BA5FE7F8C7C264DE67D7EAECB98FF52B92E82F3356E402E77582309500FE0098008400940015G6B9628FEEC9C4FD738B2A2FD453397F9D657E16E5E0AAD8776621A6A760289BBBD6E4B6CF3831E81717A991551FD6A764A09FDAA3ED2FF0DCC2C9C3BC6AE2E54DFA317D4EA57C8AE
	CBE118E41989322939074EDE2D43E4FB2EFB487606D85BC5D0DE8630DAEDFBE9044E0EB6822E120D85F1DF205F6663FAB83E0F7F85269ACA3E7BFC21B6EEDBFE7CCA7681C1BFEE9B6FD53E8939B79C37B7C12E4A7D2B2FF52F20FBFF2B71E2DAE77CEDF46F1EFCAF4D73A7ED6C5BB4A8E78294A6527A7F7018F32F497945E3536C17B165FE5CEFEEE7B122AAB1521F62993EAEB1521F5EEAA49BE8003AA4F53E5FEB91D4DED6EB5EDFEC3A79B9FAC1734D4F511FB3C57749DF1AA6764997CD7ABE7932E92CCF3EEA
	5277C9E11F56A7B9AEFF9F276BD7AF8F153B00FD38F7637B5FD3CE2F33CFF33FEE981E1E0D6D1EE63FD3D36E47ED70BFD88C493BAF52B6FF4B704577C55A66D5B5BE9921AE66BE324D21916D789B811FF55F7879B6DFA376D4E14ED0F8FF1A219A408E60829886188F108C309CE0B540C600ADG458B538CD5GC7812CGE781FC8BE99DF977G1E47C7DBC7140B0B70C2224AAF54B61E7CE297C6BBDBBAF5025DFFAD0CBCC37F0C6117AE0CBCC3FF2E0D4E5017C31DF19149EFDE2320721228F95F57F873FD3B7E
	7FE10C4D4361B93D22F91FB1FC7347DEF8D347607B5839EC4D0DD944754DF1718C17286038FD56D6C50FF17FDBB5F10CEB2C520FF1162A31B14ED6259F63BA2A4646B8C7952E55FD609F5DEA3B4FD55C67AA6ECBC693F11F39DDAC3765F897638B71D69187A47CG63AB44F05D8ACE5260BE277319AA5C0F40147BA5BF759D2942FDE42F5B636165EB2C0D5B8B5B93D59EB3980FEE1FD34377AC9577392A0BBB55FB5EE52A4FF51DC07A9B5472972C1C2916FF51B6D93BC19A53334D5FCFFA0C695CD5717C4B79CE
	21206642CCED9CEF9C5E9946135A84FE5E70F01BE99C3A3B97EB4F9BB2BEC1FAA76D6EG76675F135EF9C2FBCFDA3D181F5B5897537EA82D8DF5E99671DBC4627369C3781FBF1DBC11D1667961D3A7687B3D730BC9CFBFBC0131BBF1313E8ED2A270AB97535953730C56DAA8E728B82B156CA4CB45DD37316F3F9E00B5678162BF6B448E66EF12476176B940E7851EBFF73D03A2645FE28CD8E9CFEA07FE6781E2G12GD62AF4965858F7B322722DBADE72C0195F33FD43C6F97C0687A8371FEFA5DC7503F4671D
	EE0D3C73FEDD505ED93FEA0D3C734E9ED5DFE30B3C735EB16A4E7BCBDB641DB77567F3F84A06FF5154282C969556251D13277654E93B4356E9EFBDE77B58517A6919E67B392ED6FB2B59E16B7818FCE101D53B861F0779498F0E6FD7DCBFA9D60A3D48B747BDEA25580B32607FA57D01EF815A43FF897932E06077857042AF9582FE21C87D7C5846C24C0621A37539BFDF121BA3645984838F39B6A18F6326CD48E7E155A664C2489D463FE1E178ABD2E03BAC54DAA50F4B54E53535B95EBB55F13A2B751C59EEB3
	371BFAA53FE9B068115DBE0FE8D2559E90E84FF56806760EA1EBE6F954FB0AF5C9DCB699C961BD974F298E32EFB12A9F02FD77C31497057C789F1EE8E7DCDD03CE0FC7F6FAAEB6FBDDF85E55B2A20B61736C1AG528FD09B25D2796FCCE01B22FB6A73FFD0CB87880C1A0C0D0A94GG94BCGGD0CB818294G94G88G88G74F854AC0C1A0C0D0A94GG94BCGG8CGGGGGGGGGGGGGGGGGE2F5E9ECE4E5F2A0E4E1F4E1D0CB8586GGGG81G81GBAGGG4494GGGG
**end of data**/
}
}
