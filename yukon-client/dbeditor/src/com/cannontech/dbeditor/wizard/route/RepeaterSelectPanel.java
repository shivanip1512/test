package com.cannontech.dbeditor.wizard.route;

import java.awt.Dimension;

import com.cannontech.database.db.*;
import com.cannontech.database.db.device.*;
import com.cannontech.database.db.route.*;
import com.cannontech.database.data.route.*;

import com.cannontech.common.gui.util.DataInputPanel;

/**
 * This type was created in VisualAge.
 */
public class RepeaterSelectPanel extends com.cannontech.common.gui.util.DataInputPanel implements com.cannontech.common.gui.util.AddRemovePanelListener {
	private javax.swing.JLabel ivjRepeaterLabel = null;
	private com.cannontech.common.gui.util.AddRemovePanel ivjRepeatersAddRemovePanel = null;
	private int rightListItemIndex = getRepeatersAddRemovePanel().rightListGetSelectedIndex();
	private boolean rightListDragging = false;
public RepeaterSelectPanel() {
	super();
	initialize();
}
/**
 * Method to handle events for the AddRemovePanelListener interface.
 * @param newEvent java.util.EventObject
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
public void addButtonAction_actionPerformed(java.util.EventObject newEvent) {
	// user code begin {1}
	// user code end
	if (newEvent.getSource() == getRepeatersAddRemovePanel()) 
		connEtoC1(newEvent);
	// user code begin {2}
	// user code end
}
/**
 * connEtoC1:  (RepeatersAddRemovePanel.addRemovePanel.addButtonAction_actionPerformed(java.util.EventObject) --> RepeaterSelectPanel.fireInputUpdate()V)
 * @param arg1 java.util.EventObject
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC1(java.util.EventObject arg1) {
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
 * connEtoC2:  (RepeatersAddRemovePanel.addRemovePanel.removeButtonAction_actionPerformed(java.util.EventObject) --> RepeaterSelectPanel.fireInputUpdate()V)
 * @param arg1 java.util.EventObject
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC2(java.util.EventObject arg1) {
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
 * connEtoC3:  (RepeatersAddRemovePanel.addRemovePanel.rightListMouse_mousePressed(java.util.EventObject) --> RepeaterSelectPanel.repeatersAddRemovePanel_RightListMouse_mousePressed(Ljava.util.EventObject;)V)
 * @param arg1 java.util.EventObject
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC3(java.util.EventObject arg1) {
	try {
		// user code begin {1}
		// user code end
		this.repeatersAddRemovePanel_RightListMouse_mousePressed(arg1);
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}
/**
 * connEtoC4:  (RepeatersAddRemovePanel.addRemovePanel.rightListMouse_mouseReleased(java.util.EventObject) --> RepeaterSelectPanel.repeatersAddRemovePanel_RightListMouse_mouseReleased(Ljava.util.EventObject;)V)
 * @param arg1 java.util.EventObject
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC4(java.util.EventObject arg1) {
	try {
		// user code begin {1}
		// user code end
		this.repeatersAddRemovePanel_RightListMouse_mouseReleased(arg1);
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}
/**
 * connEtoC5:  (RepeatersAddRemovePanel.addRemovePanel.rightListMouse_mouseExited(java.util.EventObject) --> RepeaterSelectPanel.repeatersAddRemovePanel_RightListMouse_mouseExited(Ljava.util.EventObject;)V)
 * @param arg1 java.util.EventObject
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC5(java.util.EventObject arg1) {
	try {
		// user code begin {1}
		// user code end
		this.repeatersAddRemovePanel_RightListMouse_mouseExited(arg1);
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
 * @return java.awt.Dimension
 */
public Dimension getMinimumSize() {
	return getPreferredSize();
	}
/**
 * This method was created in VisualAge.
 * @return java.awt.Dimension
 */
public Dimension getPreferredSize() {
	return new Dimension(350, 200);
}
/**
 * Return the SelectLabel property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getRepeaterLabel() {
	if (ivjRepeaterLabel == null) {
		try {
			ivjRepeaterLabel = new javax.swing.JLabel();
			ivjRepeaterLabel.setName("RepeaterLabel");
			ivjRepeaterLabel.setFont(new java.awt.Font("dialog", 0, 14));
			ivjRepeaterLabel.setText("Select the repeater(s) to include in this route:");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjRepeaterLabel;
}
/**
 * Return the AddRemovePanel1 property value.
 * @return com.cannontech.common.gui.util.AddRemovePanel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private com.cannontech.common.gui.util.AddRemovePanel getRepeatersAddRemovePanel() {
	if (ivjRepeatersAddRemovePanel == null) {
		try {
			ivjRepeatersAddRemovePanel = new com.cannontech.common.gui.util.AddRemovePanel();
			ivjRepeatersAddRemovePanel.setName("RepeatersAddRemovePanel");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjRepeatersAddRemovePanel;
}
/**
 * This method was created in VisualAge.
 * @return java.lang.Object
 * @param val java.lang.Object
 */
public Object getValue(Object val) 
{
	Integer routeID = ((com.cannontech.database.data.route.RouteBase) val).getRouteID();

	java.util.Vector repeaterVector = ((com.cannontech.database.data.route.CCURoute)val).getRepeaterVector();
	repeaterVector.removeAllElements();

	for( int i = 0; i < getRepeatersAddRemovePanel().rightListGetModel().getSize(); i++ )
	{
		com.cannontech.database.db.route.RepeaterRoute rRoute = new com.cannontech.database.db.route.RepeaterRoute();
		rRoute.setRouteID(routeID);
		rRoute.setDeviceID( new Integer(((com.cannontech.database.data.lite.LiteYukonPAObject)getRepeatersAddRemovePanel().rightListGetModel().getElementAt(i)).getYukonID()) );
		rRoute.setVariableBits(new Integer(7));
		rRoute.setRepeaterOrder(new Integer( i + 1 ) );
		
		repeaterVector.addElement( rRoute );	
	}
	
	return val;
}
/**
 * Called whenever the part throws an exception.
 * @param exception java.lang.Throwable
 */
private void handleException(Throwable exception) {

	/* Uncomment the following lines to print uncaught exceptions to stdout */
	// System.out.println("--------- UNCAUGHT EXCEPTION ---------");
	// exception.printStackTrace(System.out);
}
/**
 * Initializes connections
 * @exception java.lang.Exception The exception description.
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void initConnections() throws java.lang.Exception {
	// user code begin {1}
	// user code end
	getRepeatersAddRemovePanel().addAddRemovePanelListener(this);
}
/**
 * Initialize the class.
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void initialize() {
	try {
		// user code begin {1}
		// user code end
		setName("RepeaterSelectPanel");
		setLayout(new java.awt.GridBagLayout());
		setSize(370, 243);

		java.awt.GridBagConstraints constraintsRepeaterLabel = new java.awt.GridBagConstraints();
		constraintsRepeaterLabel.gridx = 1; constraintsRepeaterLabel.gridy = 1;
		constraintsRepeaterLabel.anchor = java.awt.GridBagConstraints.WEST;
		constraintsRepeaterLabel.insets = new java.awt.Insets(5, 0, 0, 0);
		add(getRepeaterLabel(), constraintsRepeaterLabel);

		java.awt.GridBagConstraints constraintsRepeatersAddRemovePanel = new java.awt.GridBagConstraints();
		constraintsRepeatersAddRemovePanel.gridx = 1; constraintsRepeatersAddRemovePanel.gridy = 2;
		constraintsRepeatersAddRemovePanel.fill = java.awt.GridBagConstraints.BOTH;
		constraintsRepeatersAddRemovePanel.weightx = 1.0;
		constraintsRepeatersAddRemovePanel.weighty = 1.0;
		add(getRepeatersAddRemovePanel(), constraintsRepeatersAddRemovePanel);
		initConnections();
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
	if( getRepeatersAddRemovePanel().rightListGetModel().getSize() < 1 )
	{
		setErrorString("One or more repeaters should be selected");
		return false;
	}
	else
		return true;
}
/**
 * Method to handle events for the AddRemovePanelListener interface.
 * @param newEvent java.util.EventObject
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
public void leftListListSelection_valueChanged(java.util.EventObject newEvent) {
	// user code begin {1}
	// user code end
	// user code begin {2}
	// user code end
}
/**
 * main entrypoint - starts the part when it is run as an application
 * @param args java.lang.String[]
 */
public static void main(java.lang.String[] args) {
	try {
		java.awt.Frame frame = new java.awt.Frame();
		RepeaterSelectPanel aRepeaterSelectPanel;
		aRepeaterSelectPanel = new RepeaterSelectPanel();
		frame.add("Center", aRepeaterSelectPanel);
		frame.setSize(aRepeaterSelectPanel.getSize());
		frame.addWindowListener(new java.awt.event.WindowAdapter() {
			public void windowClosing(java.awt.event.WindowEvent e) {
				System.exit(0);
			};
		});
		frame.setVisible(true);
	} catch (Throwable exception) {
		System.err.println("Exception occurred in main() of com.ibm.uvm.abt.edit.DeletedClassView");
		exception.printStackTrace(System.out);
	}
}
/**
 * Method to handle events for the AddRemovePanelListener interface.
 * @param newEvent java.util.EventObject
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
public void removeButtonAction_actionPerformed(java.util.EventObject newEvent) {
	// user code begin {1}
	// user code end
	if (newEvent.getSource() == getRepeatersAddRemovePanel()) 
		connEtoC2(newEvent);
	// user code begin {2}
	// user code end
}
/**
 * Comment
 */
public void repeatersAddRemovePanel_RightListMouse_mouseExited(java.util.EventObject newEvent) {
	rightListItemIndex = -1;
	rightListDragging = false;

	return;
}
/**
 * Comment
 */
public void repeatersAddRemovePanel_RightListMouse_mousePressed(java.util.EventObject newEvent) {
	rightListItemIndex = getRepeatersAddRemovePanel().rightListGetSelectedIndex();
	rightListDragging = true;

	return;
}
/**
 * Comment
 */
public void repeatersAddRemovePanel_RightListMouse_mouseReleased(java.util.EventObject newEvent) {
	int indexSelected = getRepeatersAddRemovePanel().rightListGetSelectedIndex();

	if ( rightListDragging &&  indexSelected != -1 && indexSelected != rightListItemIndex )
	{

		Object itemSelected = new Object();
		java.util.Vector destItems = new java.util.Vector( getRepeatersAddRemovePanel().rightListGetModel().getSize() + 1 );

		for( int i = 0; i < getRepeatersAddRemovePanel().rightListGetModel().getSize(); i++ )
			destItems.addElement( getRepeatersAddRemovePanel().rightListGetModel().getElementAt(i) );

		itemSelected = destItems.elementAt( rightListItemIndex );
		destItems.removeElementAt( rightListItemIndex );
		destItems.insertElementAt( itemSelected, indexSelected );
		getRepeatersAddRemovePanel().rightListSetListData(destItems);

		getRepeatersAddRemovePanel().revalidate();
		getRepeatersAddRemovePanel().repaint();

		// reset the values
		rightListItemIndex = -1;
		fireInputUpdate();
	}

	rightListDragging = false;

	return;
}
/**
 * Method to handle events for the AddRemovePanelListener interface.
 * @param newEvent java.util.EventObject
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
public void rightListListSelection_valueChanged(java.util.EventObject newEvent) {
	// user code begin {1}
	// user code end
	// user code begin {2}
	// user code end
}
/**
 * Method to handle events for the AddRemovePanelListener interface.
 * @param newEvent java.util.EventObject
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
public void rightListMouse_mouseClicked(java.util.EventObject newEvent) {
	// user code begin {1}
	// user code end
	// user code begin {2}
	// user code end
}
/**
 * Method to handle events for the AddRemovePanelListener interface.
 * @param newEvent java.util.EventObject
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
public void rightListMouse_mouseEntered(java.util.EventObject newEvent) {
	// user code begin {1}
	// user code end
	// user code begin {2}
	// user code end
}
/**
 * Method to handle events for the AddRemovePanelListener interface.
 * @param newEvent java.util.EventObject
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
public void rightListMouse_mouseExited(java.util.EventObject newEvent) {
	// user code begin {1}
	// user code end
	if (newEvent.getSource() == getRepeatersAddRemovePanel()) 
		connEtoC5(newEvent);
	// user code begin {2}
	// user code end
}
/**
 * Method to handle events for the AddRemovePanelListener interface.
 * @param newEvent java.util.EventObject
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
public void rightListMouse_mousePressed(java.util.EventObject newEvent) {
	// user code begin {1}
	// user code end
	if (newEvent.getSource() == getRepeatersAddRemovePanel()) 
		connEtoC3(newEvent);
	// user code begin {2}
	// user code end
}
/**
 * Method to handle events for the AddRemovePanelListener interface.
 * @param newEvent java.util.EventObject
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
public void rightListMouse_mouseReleased(java.util.EventObject newEvent) {
	// user code begin {1}
	// user code end
	if (newEvent.getSource() == getRepeatersAddRemovePanel()) 
		connEtoC4(newEvent);
	// user code begin {2}
	// user code end
}
/**
 * Method to handle events for the AddRemovePanelListener interface.
 * @param newEvent java.util.EventObject
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
public void rightListMouseMotion_mouseDragged(java.util.EventObject newEvent) {
	// user code begin {1}
	// user code end
	// user code begin {2}
	// user code end
}
/**
 * This method was created in VisualAge.
 * @param val java.lang.Object
 */
public void setValue(Object val) {
	com.cannontech.database.cache.DefaultDatabaseCache cache =
					com.cannontech.database.cache.DefaultDatabaseCache.getInstance();
	java.util.Vector allRepeaters = null;
	synchronized(cache)
	{
		java.util.List allDevices = cache.getAllDevices();
		allRepeaters = new java.util.Vector();
		for(int i=0;i<allDevices.size();i++)
			if( com.cannontech.database.data.device.DeviceTypesFuncs.isRepeater( ((com.cannontech.database.data.lite.LiteYukonPAObject)allDevices.get(i)).getType()) )
				allRepeaters.add(allDevices.get(i));
	}

	com.cannontech.common.gui.util.AddRemovePanel repeatersPanel = getRepeatersAddRemovePanel();
	repeatersPanel.setMode(repeatersPanel.TRANSFER_MODE);
	repeatersPanel.leftListRemoveAll();
	repeatersPanel.rightListRemoveAll();

	repeatersPanel.leftListSetListData(allRepeaters);
}
/**
 * 
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private static void getBuilderData() {
/*V1.1
**start of data**
	D0CB838494G88G88GB6F954ACGGGGGGGGGGGG8CGGGE2F5E9ECE4E5F2A0E4E1F4E135BA8DD4D45719E80892E376E8561CF8D233EB934C219BF6CB3376544D661C3A54BD473436F54FB11BEC6BF669A959522E5DE3B73647D85ADE8698D0219241840DB698C904ACC5B06694C7D4FE065F1008B0924C526A03F9838F5F4C9B5FFB83B3C8F43F6F5EF75FBC06B790BC89474F776E775DFB3F1F7BFD5F7D6E3BC34A5ED81B1F63A8A3A4A71F98FFFF4CA124648F04B85695BD4DF189E23A13D87E
	0E833CC2DA1DF970DC81348DAB531DA5E4E938905A4950BE7397694E57603D14AC8FA7D641CB9CBEC3C03B1312761B39797CCD8367D3C05BE9170A6079BB00944061F317087CB7DFAAB770DB8CBC079C87A1AF78304F0FAE9DB5F08D506E85D8851062E37DB742F3AF65FC2F2B4168B734F2AD59754D130EE21C47B2A7070AD1B656EDBCAB4817F3F60CA22F225EA2E34CB434578320723924707AFB70EC6C3DB1FAE1C471570E88010092504511315AC9E9CAD03D352A9252C5172BCF8C0A02AE2A7D22AC0E683D
	C2C014E15C58984E6D69AD24F391D2876DDD94D7FC847925427BA6006DA63EB20478868974859813380E67CFB69ABA8C5E7AB2D97FFCA077C09C9D93A426DB0A9455F1D5E8CF41E13A9EBFBD0BF64F84DA61F83A73A8C09340F500840057299F7C3C619DF8B68F6B2D0A5F2F841A034196592BFE20F8C5995EDDAE209838BEF1C4D13D043035BE58DBE3A34FDC4046D5ACF89C33C94A3BFC5DD37B1EA05B7F7EFDC7010DCD362754B9D60FB3DB240DC7ED42F8DF0D4BFB6B78CC3F1C497BA3E2651D3CC4494D3761
	3D7E39EAC726B14FEE8B6F2A9B38560B288F1443FB0E619F8CFFBF454B96BC7351D20A4776B450B2EEF05F987C9D1725A76F91D2D25FE3EB0755694D8E374CE49812E3E5D9CD7DAEC9E6BC771B32BCC171A996BC13659D0A4776E6205578531D4C3FDEBA07B13B825A29G1BG3683E483148720DF007B186B6C29CF69E33D022A3704A459AB2A040DFD696C2B7094433AAAF485B4DD880C088302AA894332289123CF76D93441700CF1B9AE747D916051A186C4D550A5A5G338BFE9182D7E3FD7639DE41BE2AC8
	299E49AF8283FF1070F98F3ACAF1ED84CD3F9274C240C7FBB07A91972E4904B0GB34284704E6CD2F19E6D55837AFB81F4538E8F51795E97D5C898AED70F225C8885BD28895994C0BB87E96EC802773581EE63239790D72824BBAB94BE4F9657C14BBCEEE984459754880F3125C7B0AF1FD1384F7C77A07799F771BAD273DC372D4FA0F89466AB32929BBBAB691CA897EB9FBEB23840B8BE235847D87CDCF1FA41BCF297501759F759C5DC2BD5A0DB9A40D653468567AA184F36881A483D74128A8EA6FA2396E7F2
	7ED5284C30A7724FB7645FEB6A11CA69D9B0F7C9B05D59GF09DA0A948EDD9979A0CB3FEA13A4FED4BAE4AA3F67E7D1F918FE633DF1F43FDCA1C90833A9B36480097DEB4031657087C0CB876A96618FD949F0438EF05C053CCFCC9A34626206B2AB48C1BEE3F286BD2E0B4CAAFEFC4DEDAE8B898CD991C579B0D2823B289F1EF762FEC441899D1828111AE1EB944203FCDE396B6F4BF885D2A0482BA43571CC73F3FA1C60C1CD1F79E8B2189C18EE17EE1F5C31A00F68C2A129FDC82D398A93BC96B2E5EF20B0D52
	E03D317E4902E7F610476664899E1B15D3DFA49B2F3D65305BDBD62FF3BB06EE3278895E0C46A613697885FB59CF525CE160B5A66B17CEE38E322BE0D8BCFC238967DA81BC12811E39496361718BC867B9478804D6C51619B9CDBB2F38D8026BA25E8C09102AB1393DEF5A6E610B87E3E883069D997D2FAF229DE5CBDECCD4510EBF8C715AF09B483383ED2BB2DBFAD5E68B566F2B933C5FD615754B326D1799667D9AB5564F2331FE491A35DFEA0477A3BA6BD72E33FEE33A351FBE457BEDB27AEDB77AD505D8BF
	E67B02EBD8FF58551723A129B624CB184D859D82A498E20B42EA4A1F53FD8167C888715A71188757E983343782E40538BF7DCBB12F9DD75DD8CBB29EBF9237BEA693CC364A0928BFD55C43EB4762966469851ABE41FD60C813F57FEA56B4E5C422DB2731FE4FB6D97D0D613E53348F1EBEC95574AEC888E13E565B1AD0DE5CDB8D34D1EB9C1AA8B2F4CD823E9B81D8AC25D4D9E3E977845BA36BA6531DEE00DCBA6774C49B668C2F37CF74AB93A235E0ADB4DBC23A8EE587F554EB82FD740A2ACFD17D2237EADC18
	90286DEB5BB0F1DD9A9E870F2EB657ECD375C1A63FC8D7059582D555833CB6FC2C344910E94B5FBDC29A6F14BA6CEA752483479CC313064FCCC66DBD7D14B3F7471FFFD540F47F01297BD668D39866FABD996E1C2D17CA5F6FC2B5462BB842F9B5811F3049CB0FB45B7012C647749EC953BFD0C21AF84D0F7F772A22264DEF3FDFDC78G1E2A11EF34669993DF6B1BE3629E5B194F21698DDFF94E54A18D644FB2F5E81CEA79F4BA74012F8AF3A941F458637BF001BA18934F2184536151A92EC3C5A45DB99861BA
	645DEA7AF4BA3405A5BD2E86CCFE651D1E854AEFCC9AD7FAD6D37CF256F9EAC5A4DA63A5C7E657FB6C1C3112652E88BBE7641B78B5943F4502E7B6BA6161B64AG1AFB0A5746EBDAB1A724C0FB9340F600DDGE537521DE7EE711CB5547CAE19676C5185B9D88D88F27F0CE2021F81DAF0BDE07FB75A2F36209CF47FA73C36FAFDA2568E3D37E66AAE473459386AD963EAE36D99B30E2D67C05E7C7B484CC5E67BC8C6957FB611F20B6FA39F9FC65BE6C0FBB7C049B4376D9EB76EE9D550F6838C81048156GECG
	58BE4D6D3B36657CFC76252E63D1DA815789FBFBEBEC8DE65AA373AE6D78BF735AB1FB5AEEDCE9646E789D1FB56E77B16D32CF6C46FD7FAE797DE4DEFEB5367C9E1F3ABBFEDF19B5AE161FBB069FBB3F96DFC67F9A82DA900061FE5A0738D550CE7D041FF79E76A0EEAB34F7183847A82E64B62B1F0C7870AC747CB79FF01ED1DDDBBEF39E7C0C056FACC6563546BF9725365A1F0B1EEA35BF97EDE8FD0561E18E6B5792CE5F58BA7739E9F32B5D396998157B59D78F5039E17B55E850E2B1EA50FE2C9FA2B66727
	8F853FE868DCCA7AA6769AFD5AF425355E34710BD5F3FD732B3B9DEF1F788E4DCD6E5BECBF48B1714FD3FC2F854F76096B0779F75DA120ED3C4DF3D4DEDBFC7B6FEF33377FABEDD8B7EBCAC89D91E3EDDC5ED6EE5222592CCB977DE88E63CCC6476B02BAAA4EDA23AABA1E51620D2FEF0B370E4C562FCFDAEDFD5A44F7C72C78F7CD7C41A9AB7E0C097F5E8C7CFBA63E72B21EF3E5A12204F458076E20FD4FDD66B5F8A5349B813CG61BB2C96E7FB53D7A6718C67D385BF085B9F94C7081C00E3F37A795897207F
	8E00A24057G74A4701A2765925A0C1FCDFB04E17ADD1D511A4457AC34189A066C20BC36F94AACBE34BE014975C2824FC559D5BC971DD944F34CB65C6E8CBF7DF74B3B6F70E71D97E66759BB1FAB8F9693A317F9CCA4024E4FAC626BD4FF9D77F7DAC350A2CF1854DD2E8ED572368823BDF455D94D70A44D69B8FE57A2DEB37C9B1D33E6B15486GC30B7959B37CB6BFFB26BE718869C9F85F766C1961B96D50973318DF3DB8FA96CA896DA9E0BCCF76F31E5B00DEF48F622E74DB5796FD6247B9EC1FE77425B368
	98BB182F2274FF18C1C7F9DFF4D8697FB5031EEF994FF47B56DBDC376CEF2590792BE7631E2BBD77B01D424633F7608429DC762B82E67717FC65D63B07EB35C98894172B1B3ABB53FF7F15C243C7526E61367D245DFA2EFF6640E9477EB87C338C3E0509D15B5600EDD7A452FB05EA37512FB3F1F65E65FBD72E81C350A788F048E8A7A6C6F3AE73E169725D5D83CD5E653834850FE3FB4613FD6833AFA4466EA56BA9BEB3B1F6AF798FB3EEF6824DF3AF2F0BBF6840755BG6DEDGBBG721624BBAB811A16707D
	E6DBDB9D1927A676A8419E5810E44B964E742B693F3B7B31FFAA607E317E69474806A5AF6744738F4F92366E3A714C721C20F826DFC6A76A178C340DG19G59GA5CB531DD5CB39FEC75BEB18FEEDDEC9EF56F5E1E40CFE71614A8DC862E4940D1BG532D640AD5374625C6FC98CF6F52597E48E46A6E46588C83FD95C0AA40A600CCD39EDF7B3CE73C36306D990F451B3B1AFFDF4BCBCAF73EC1670478A17CEF67C5BBDCE3925E095F3ECC4CB578E191DF0323CFFE01381FFFB967C41CB5C8C8E23AEECD0A46E8
	56F8F601D1FF7791535F1F363C3FF879735F13FE733972E06BB550913F9E7BD307FDBDB652E1DF8FCB9DF3573BB7BB625F9304BB2C77844C579E6AC3DFAB3C8FE29A2093E088A0F89F77B5E7671B4C57EC7C3E4DBB0A75A05ED26A1C7FCFBA636B3A33F37E3B4D17BB717B6930226B0A9F6767785FF662FA69CA10A20DBA378BEDC4BF92312E7C0E258B7DD696FD944DEA2113F40FD98EFA2D390F57C79F0B08CB0376C69377F51157CCDBCC9B74F5475769EA377D7ADD6B36DFBF31FB6E730C3FBB5E7A9A77DA55
	561CF2F4D93A33EE595C770CACD63F7BFF565F1114BCF25C4173FFECFF37B1DF6F32E82CB2DFE97060DE1A887835G1BG36836C84A83CBF5DF994208E408DB0842083A4812C81C8835888308DA06BFEEE57EFDC466F268342381141B03DAAAA75AEC09A4599F5B0D51050458C9BBD5A97FFBD127B6677313F25E7F491FC4393899A670F277A307ED7FCBECDB4FD79EBFDA5A62ECB6BD5B4894FAD2CBE3FF0856F49FCCAC0EFD8CEFF87C37DAB9BF4ABDC4E6B7B1BA27651C19816BBD371D3AEE1F5493F0A3CAE19
	DEEEDF17D8E18574C16079B5F9F9F47FE6FEFB1D6674B4403F174BF1A245ED865CB9870F772B9F91530EDD7DEF4533BFC4F7B714EB6C8328C798B54774F8625B3E5FB33F6D07BC7671BD6C199D5F239E7B7876FB2C714DF02A87F35BA8784C2059EF5244DDB5F153F4CE44FDA4F975B1933F184A0578CE9199F2FC9215EB1262868D5C83965CD5837720877D87D49ED3D4BE76E18F7591C916D97B8B340F94GF7B26D31164AA885850B5C6BCC9C4A4DFC674F54F718DFFDA8E2CE8E0813957CA8D35B8E0ED87526
	D792E4E5744D1A0A3A1A0A7AB355BC7657CEDAE33FE15F9FED6FDC91B28DDF5CEDBC1FD9FA4228998AFC3CE628FB402EE660F2EE267E45EAEA616DA22AAF040DA0D3F3B37C51F0F1944F4DED78E7E890679C839E098F18F731837FBBC71E68D51520286A910625B72CC62EAC72D9F3E5B24C37610139F3A563B7B5086FE68EE6D39A34C503E5D15925A9FA9D588F9D8323AC66CE78F89CC6E3334A476390F16C770815F9A1D84C541F11FCCDA5874CF56C224E11B1712EAB77C70F67BAE00BA3CE96ACDDA48FBD3E
	0B64530068A23990AC61A0097953C99D3B642E5047448AFE87D225D5D768CA05949811C3DE911EC015348A767BBE9020F806A37D73975F7B4FADBF9850F209330B9C469C57AF2AD4DD510B1F480C5F7F34133DC18572F2BF60C1BA0DBE72B5BDA20BBD244C5707775E1AAEFA1B39A1B51D9C22D7AC27701B7D29CEC19B53B1B91C6BA1F9817C2592B94848329098BD452CFA0E2A1853FC1DEA076BC2C05E5268C50DB9CB69BF9EF86BF8F76606DC62E8A77BE4C570368BA36091DDE42FBE260A5A18A2FB43410BCD
	F42242C9DC92BCB26442F42F1B6A2332359EC8E12650B7420FAEAF4EAAEE078C9AD649A95A93D3FA2D59435BBAA62840CAD43BF47C651522D61A34C53F575F7EF647EBC38E12E3A8F9135CA6796120CE1CBE41AF4991F8E1E12BA1E52F8E7FD4A1206115A6C8D691A399A7198CEA7F72593A073E3467E34AA0BAD4A7EFB0D34250D3232262BF556C73C981511CG6C8A539FB32747BE35B1FD8E06D79E6AFE6E37436087EE126B2F2B237F57537FCF637F7594D3CFB175274138BBE35DF166DFACBD4CF5264EE386
	82AEFFEDBB2C207E3D1FBD7B58779BF76EG6D5AC9BE3D0502C5A809767AB0649F9655A5298F9D5B3FAA63FEDC23A03ACA9867ADE653477EE18EE2F9317A0EB5AF6E3E37BCEEDE4CDE4972C6490331B5112BG9D298770E38F72FC354C7907F8798A7655FE3FA04B3AA05FE88EF8F10FED0968E234BE3CD80073EB3C0FC1E434768214FB5CB2667F81D0CB8788FD1AFCE84790GGB8ADGGD0CB818294G94G88G88GB6F954ACFD1AFCE84790GGB8ADGG8CGGGGGGGGGGGGGGGGGE2
	F5E9ECE4E5F2A0E4E1F4E1D0CB8586GGGG81G81GBAGGG8191GGGG
**end of data**/
}
}
