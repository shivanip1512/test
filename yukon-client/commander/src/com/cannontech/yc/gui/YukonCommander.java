package com.cannontech.yc.gui;

/**
 * This type was created in VisualAge.
 */
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.util.Vector;

import javax.swing.event.TreeSelectionEvent;

import com.cannontech.database.model.ModelFactory;
import com.cannontech.yc.gui.menu.YCCommandMenu;
import com.cannontech.yc.gui.menu.YCFileMenu;
import com.cannontech.yc.gui.menu.YCHelpMenu;
import com.cannontech.yc.gui.menu.YCViewMenu;

public class YukonCommander extends javax.swing.JFrame implements com.cannontech.database.cache.DBChangeListener, java.awt.event.ActionListener, java.awt.event.FocusListener, java.awt.event.KeyListener, Runnable, javax.swing.event.TreeSelectionListener {
	private static  YC ycClass;
	private static final int treeModels[] =
	{
		ModelFactory.DEVICE,
		ModelFactory.DEVICE_METERNUMBER,		
		ModelFactory.MCTBROADCAST,
		ModelFactory.LMGROUPS,		
		ModelFactory.CAPBANKCONTROLLER,
		ModelFactory.CICUSTOMER,
		ModelFactory.COLLECTIONGROUP,
		ModelFactory.TESTCOLLECTIONGROUP,
		ModelFactory.EDITABLELCRSERIAL,
		
	};
	//-----------------------------------------
	private static final String YC_TITLE = "Commander";
	private static final String VERSION = "2.2.7";
	public static final String HELP_FILE = com.cannontech.common.util.CtiUtilities.getHelpDirPath() + "Yukon Commander Help.chm";
//	private com.cannontech.message.porter.ClientConnection connToPorter;
	private Thread inThread;
	private com.cannontech.message.dispatch.ClientConnection connToDispatch;
	private javax.swing.JPanel ivjJFrameContentPane = null;
	private javax.swing.JPanel ivjOutputPanel = null;
	private com.cannontech.common.gui.util.TreeViewPanel ivjTreeViewPanel = null;
	private CommandPanel ivjCommandPanel = null;
	private SerialRoutePanel ivjSerialRoutePanel = null;
	private javax.swing.JSplitPane ivjSplitPane = null;
	private static CommandLogPanel ivjCommandLogPanel = null;
	private javax.swing.JLabel ivjCGPMode = null;
	private ClearPrintButtonPanel ivjClearPrintButtons = null;
	private AdvancedOptionsPanel advOptsPanel = null;
	private LocateRouteDialog locRouteDialog = null;
	private com.cannontech.yc.gui.menu.YCCommandMenu ivjYCCommandMenu = null;
	private com.cannontech.yc.gui.menu.YCFileMenu ivjYCFileMenu = null;
	private com.cannontech.yc.gui.menu.YCHelpMenu ivjYCHelpMenu = null;
	private com.cannontech.yc.gui.menu.YCViewMenu ivjYCViewMenu = null;
	private javax.swing.JMenuBar ivjYukonCommanderJMenuBar = null;
	private javax.swing.JPanel ivjClearPrintPanel = null;
	private javax.swing.JScrollPane ivjDebugOutputScrollPane = null;
	private javax.swing.JTextArea ivjDebugOutputTextArea = null;
	private javax.swing.JScrollPane ivjDisplayOutputScrollPane = null;
	private javax.swing.JTextArea ivjDisplayOutputTextArea = null;
	private javax.swing.JPanel ivjExecutionPanel = null;
	private javax.swing.JPanel ivjNavigatorPanel = null;
	private javax.swing.JSplitPane ivjOutputSplitPane = null;
/**
 * Constructor
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
public YukonCommander() {
	super();
	initialize();
}
/**
 * Insert the method's description here.
 * Creation date: (9/10/2001 3:21:17 PM)
 */
private void about()
{
	javax.swing.JFrame popupFrame = new javax.swing.JFrame();
	popupFrame.setIconImage(java.awt.Toolkit.getDefaultToolkit().getImage("CommanderIcon.gif"));
	javax.swing.JOptionPane.showMessageDialog(popupFrame,
	"This is version " + getVersion() + "\nCopyright (C) 1999-2002 Cannon Technologies.",
	"About Yukon Commander",javax.swing.JOptionPane.INFORMATION_MESSAGE);
}
/**
 * This method was created in VisualAge.
 * @param event java.awt.event.ActionEvent
 */
public void actionPerformed(ActionEvent event)
{
	if(	event.getSource() == getTreeViewPanel().getSortByComboBox())
		sortByComboBoxAction(); 

	/////////////////////////////////////////////////////////////////////////
	// ** YC View Menu - reloadMenu **//
	else if (event.getSource() == getYCViewMenu().reloadMenuItem)
		reloadDevices();

	/////////////////////////////////////////////////////////////////////////
	// ** SerialRoutePanel **//
	else if (event.getSource() == getSerialRoutePanel().getSerialTextField())
		serialNumberAction();

	else if (event.getSource() == getSerialRoutePanel().getRouteComboBox())
	{
		setRoute(getSerialRoutePanel().getRouteComboBox().getSelectedItem());
		com.cannontech.clientutils.CTILogger.info(getRoute());
	}

	/////////////////////////////////////////////////////////////////////////
	// ** CommandPanel - execute **//
	else if( event.getSource() == getCommandPanel().getExecuteButton() ||
			event.getSource() == getYCCommandMenu().executeMenuItem )
	{
		setCommand((String) getCommandPanel().getExecuteCommandComboBoxTextField().getText().trim());
		if( isValidSetup() )
			enter();
	}
	
	/////////////////////////////////////////////////////////////////////////
	// ** CommandPanel - stopButton **//
	// ** YC Command Menu - stopMenu **//
	else if( event.getSource() == getCommandPanel().getStopButton() ||
			event.getSource() == getYCCommandMenu().stopMenuItem )
	{
		if( !getConnToPorter().isValid() )
		{
			getCommandLogPanel().addLogElement(" ** Warning: Not connected to port control service **");
			return;
		}
		ycClass.stop();
		getDebugOutputTextArea().setCaretPosition( getDebugOutputTextArea().getDocument().getEndPosition().getOffset() - 1 );
	}

	/////////////////////////////////////////////////////////////////////////
	// ** Command Panel ** //
	else if( event.getSource() == getCommandPanel().getAvailableCommandsComboBox())
	{
		if( getCommandPanel().getAvailableCommandsComboBox().getSelectedIndex() > 0) //0 is default "select"
		{
			getCommandPanel().getExecuteCommandComboBoxTextField().setText( ycClass.substituteCommand(getCommandPanel().getAvailableCommandsComboBox().getSelectedItem().toString() ));
			getCommandPanel().getExecuteCommandComboBoxTextField().requestFocus();
		}
	}
	/////////////////////////////////////////////////////////////////////////
	// ** YC Command Menu - locateRoute **//
	else if( event.getSource() == getYCCommandMenu().locateRoute )
	{
		getLocateRouteDialog().getDeviceNameTextField().setText(getTreeViewPanel().getSelectedItem().toString());		
		getLocateRouteDialog().showLocateDialog();
		setRoute(getLocateRouteDialog().getRouteComboBox().getSelectedItem());
	}
	else if ( event.getSource() == getLocateRouteDialog().getLocateButton())
	{
		setCommand("loop locateroute");
		if( isValidSetup() )
		{
			enter();
		}
		// Get routeID from comboBox / set it in the request
		//if( getRoute() != null && getRoute() instanceof com.cannontech.database.data.lite.LiteYukonPAObject)
		//{
			//com.cannontech.database.data.lite.LiteYukonPAObject r = (com.cannontech.database.data.lite.LiteYukonPAObject) getRoute();
			//req.setRouteID(r.getYukonID());
		//}
	}
	else if (event.getSource() == getLocateRouteDialog().getRouteComboBox())
	{
		setRoute(getLocateRouteDialog().getRouteComboBox().getSelectedItem());
	}
	/////////////////////////////////////////////////////////////////////////
	// ** YC File Menu - printMenu **//
	else if( event.getSource() == getYCFileMenu().printMenuItem || 
		event.getSource() == getClearPrintButtons().getPrintButton() )
		print();

	/////////////////////////////////////////////////////////////////////////
	// ** YC File Menu - saveMenu **//
	else if( event.getSource() == getYCFileMenu().saveMenuItem )
		save();

	/////////////////////////////////////////////////////////////////////////
	// ** YC File Menu - findMenu **//
	else if( event.getSource() == getYCViewMenu().findMenuItem )
	{
		String value = javax.swing.JOptionPane.showInputDialog(
			this, "Name of the item: ", "Find",
			javax.swing.JOptionPane.QUESTION_MESSAGE );

		if( value != null )
		{

			boolean found = getTreeViewPanel().searchFirstLevelString(value);

			if( !found )
				javax.swing.JOptionPane.showMessageDialog(
					this, "Unable to find your selected item", "Item Not Found",
					javax.swing.JOptionPane.INFORMATION_MESSAGE );
		}
	}
	/////////////////////////////////////////////////////////////////////////
	// ** YC File Menu - CGPMode **//
	else if( event.getSource() == getYCFileMenu().commandSpecificControl )
	{
		ycClass.setDirectSend( !ycClass.isDirectSend() );
		if( ycClass.isDirectSend() )
		{
			com.cannontech.clientutils.CTILogger.info(" ** CGPMODE IS ON ** ");
			getCGPMode().setText("CGPMODE ON:  Sending \'Execute Command\' string. (CTRL + F5)");
		}
		else
		{
			com.cannontech.clientutils.CTILogger.info(" ** CGPMODE IS OFF ** ");
			getCGPMode().setText("");
		}
	}

	/////////////////////////////////////////////////////////////////////////
	// ** YC View Menu - clearButton **//
	else if( event.getSource() == getClearPrintButtons().getClearButton() ||
			event.getSource() == getYCViewMenu().clearMenuItem )
		getDebugOutputTextArea().setText("");

	/////////////////////////////////////////////////////////////////////////
	// ** YC Help Menu - aboutMenu **//
	else if( event.getSource() == getYCHelpMenu().aboutMenuItem)
		about();

	/////////////////////////////////////////////////////////////////////////
	// ** YC Help Menu - helpTopicMenu **//
	else if( event.getSource() == getYCHelpMenu().helpTopicMenuItem)
		com.cannontech.common.util.CtiUtilities.showHelp( HELP_FILE );

	/////////////////////////////////////////////////////////////////////////
	// ** YC View Menu - deleteSerialNumberMenu **//
	else if( event.getSource() == getYCViewMenu().deleteSerialNumberMenuItem)
		deleteSerialNumber();
	
	/////////////////////////////////////////////////////////////////////////
	// ** YC View Menu - showCommandLogMenu **//
	//else if( event.getSource() == getYCViewMenu().showCommandLogButton )
		//getCommandLogPanel().setVisible( getYCViewMenu().showCommandLogButton.isSelected() );
	/////////////////////////////////////////////////////////////////////////
	// ** YC Command Menu - advancedOptions  **//
	else if( event.getSource() == getYCCommandMenu().advancedOptionsMenuItem )
	{
		advOptsPanel = null;
		getAdvOptsPanel().showAdvancedOptions();
	}
	else if( advOptsPanel != null && event.getSource() == getAdvOptsPanel().getOkButton())
	{
		YCDefaults newDefaults = new YCDefaults(
			(new Integer((String)getAdvOptsPanel().getCommandPriorityTextField().getText())).intValue(),
			getAdvOptsPanel().getQueueCommandCheckBox().isSelected(),
			getAdvOptsPanel().getShowMessageLogCheckBox().isSelected(),
			getAdvOptsPanel().getConfirmCommandCheckBox().isSelected(),
			getAdvOptsPanel().getCommandFileDirectoryTextField().getText().toString());

		ycClass.setYCDefaults(newDefaults);
		ycClass.getYCDefaults().writeDefaultsFile();

		getCommandLogPanel().setVisible( ycClass.getYCDefaults().getShowMessageLog() );
		
		getAdvOptsPanel().setVisible(false);
		getAdvOptsPanel().dispose();
		advOptsPanel = null;
	}
	/////////////////////////////////////////////////////////////////////////
	// ** YC File Menu - exitMenu **//
	else if( event.getSource() == getYCFileMenu().exitMenuItem )
		exit();
}
/**
 * Insert the method's description here.
 * Creation date: (10/15/2001 11:03:05 AM)
 * @param stringObject java.lang.Object
 */
private void appendOutputTextArea(String stringObject)
{
	getDebugOutputTextArea().append( stringObject );
}
/**
 * Insert the method's description here.
 * Creation date: (6/28/2001 9:14:02 AM)
 * @param message java.lang.String
 */
private int areYouSure(String message, int messageType )
{
	javax.swing.JFrame popupFrame = new javax.swing.JFrame();
	popupFrame.setIconImage(java.awt.Toolkit.getDefaultToolkit().getImage("CommanderIcon.gif"));
	return javax.swing.JOptionPane.showConfirmDialog(popupFrame, message, YC_TITLE, javax.swing.JOptionPane.YES_NO_OPTION, messageType);
}
/**
 * Insert the method's description here.
 * Creation date: (9/10/2001 3:21:58 PM)
 */
private void deleteSerialNumber()
{
	if(getModelType() == ModelFactory.EDITABLELCRSERIAL)
	{
		if (getTreeItem() == null)
			return;
			
		else
		{
			com.cannontech.common.gui.util.TreeViewPanel t =  getTreeViewPanel();
			if( t.getSelectedNode().getParent() == null)	// we got the parent!
				return;
				
			com.cannontech.database.model.EditableLCRSerialModel.getSerialNumberVector().remove( getTreeItem() );	// enter serial# into the vector
			
			// refreshing the serial# tree on the treeViewPanel
			((com.cannontech.database.model.EditableLCRSerialModel) t.getSelectedTreeModel()).update();
			getSerialRoutePanel().getSerialTextField().setText("");
		}
	}	
}
/**
 * Creation date: (3/9/2001 1:59:56 PM)
 * @author Stacey Nebben	Added in version 1.5 
 * @param b boolean
 * Enables/Disables (according to b value) the Route and Serial Number Labels
 *	and Combo Boxes (upper left side).
 */
private void enableSerialAndRoute(boolean b)
{
	getSerialRoutePanel().setObjectsEnabled(b);

	getYCViewMenu().deleteSerialNumberMenuItem.setEnabled(b);
}
/**
 * Handles the case when the user wants to submit the text in the text field
 * 
 * Checks for a connection to port control.
 * Uses MoveToTop thread to control storage in the commandComboBox, items appear
 *	in it only once and most recently used item is stored at the top of the list.
 * Calls substituteCommand to get the command string to be used for the selected command.
 * Determines which type of model 'device' is being used and then calls that type's 
 * handle[Object] function
 */
private void enter()
{
	class MoveToTop implements Runnable
	{
		javax.swing.JComboBox comboBox;
		Object item;
			
		MoveToTop(javax.swing.JComboBox comboBox, Object item)
		{
	   		this.comboBox = comboBox;
		 	this.item = item;
		}

		public void run()
		{			
			java.util.Vector toRemove = new java.util.Vector();
					
			for( int i = 0; i < this.comboBox.getModel().getSize(); i++ )
			{
				if( this.comboBox.getItemAt(i).equals(this.item) )
				{
					toRemove.addElement( this.comboBox.getItemAt(i) );
				}
	 		}
		
			for( int j = 0; j < toRemove.size(); j++ )
			{
				this.comboBox.removeItem( toRemove.elementAt(j) );
			}

			//Insert the item at the top
			comboBox.insertItemAt( item, 0 );
			comboBox.setSelectedIndex(0);

			//Clear the text field
			if( comboBox.getEditor().getEditorComponent() instanceof javax.swing.JTextField )
			{
				//((javax.swing.JTextField) comboBox.getEditor().getEditorComponent()).setText("");
			}
		}
	};
	//---------------------------------------------------------------------------------------	

	MoveToTop runThisLater = new MoveToTop( getCommandPanel().getExecuteCommandComboBox(), getCommand() );
	javax.swing.SwingUtilities.invokeLater( runThisLater );

	ycClass.executeCommand();
}
/**
 * Insert the method's description here.
 * Creation date: (9/25/2001 11:12:24 AM)
 */
private void exit()
{
	try
	{
		if ( getConnToDispatch() != null && getConnToDispatch().isValid() )  // free up Dispatchs resources
		{
			com.cannontech.message.dispatch.message.Command command = new com.cannontech.message.dispatch.message.Command();
			command.setPriority(15);
			
			command.setOperation( com.cannontech.message.dispatch.message.Command.CLIENT_APP_SHUTDOWN );

			getConnToDispatch().write( command );

			getConnToDispatch().disconnect();
		}
	}
	catch ( java.io.IOException e )
	{
		e.printStackTrace();
	}

	System.exit(0);

}
/**
 * Insert the method's description here.
 * Creation date: (5/7/2001 3:08:09 PM)
 * @param event java.awt.event.FocusListener
 */
public void focusGained(java.awt.event.FocusEvent event) {}
/**
 * Insert the method's description here.
 * Creation date: (5/7/2001 3:08:09 PM)
 * @param event java.awt.event.FocusListener
 */
public void focusLost(java.awt.event.FocusEvent event)
{
	if (event.getSource() == getSerialRoutePanel().getSerialTextField())
	{
		serialNumberAction();	
	}
	
}
private AdvancedOptionsPanel getAdvOptsPanel()
{
	if( advOptsPanel == null)
	{
		advOptsPanel  = new AdvancedOptionsPanel(ycClass.getYCDefaults());
		advOptsPanel.getOkButton().addActionListener(this);
		//advOptsPanel.getCancelButton().addActionListener(this);
	}
	return advOptsPanel;
}
/**
 * 
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private static void getBuilderData() {
/*V1.1
**start of data**
	D0CB838494G88G88G640507AEGGGGGGGGGGGG8CGGGE2F5E9ECE4E5F2A0E4E1F4E13DBD8DD8D45715B4A52D59258959383BA4E1B71A1A44EDE8CBDA1B723534CBFE3EAEEDCDC3D35BA48DC9E9CBA2C9CCC2A2C9081278989845FFD4B423E0C0C5038689AA09B851484F70BFA242A8A828BC18A70C8EB343B0A8C6B4FB6E3D6F5CF9BC5E8CE0C2783E631BFB6E39777C5C3FF36EFD77A9E465CE53276A3284A1D5AF48FF7BD385A15EA1883B5A9A6EC6DCD93C0B67133FADGEB04CFDB536159
	84F96666FEFD2650FFB58352D6C807B4756B5F075F6B04EB878A6C7043879F81721EBB7649DC7FFC86AFA31FA5B47D7C27AB60398A2097F0C1GCF81E48A6FFEEA10716110DEA57FA6102A93040067890D3955204067BC4F68AC706C8548FF015098766748795100A3B4F1GC9B26D8A20D9CA253B51D8AA53F55CBACDC87ADD09EEA56125289F417E82AB9BACBF7304C8EA08B42125F197BC8FB6DD68793C5B59D754ADBA9CCE07C76A6EAD3A5CDD54B3E8B39ACF8EDEF0BAAA1DFDFD2243AA3901F8AE11515D1A
	C14B030CD0E790453D54CA6CE2025F9DG1E39A8679FD79E146518FAE11A90777D8FFD4A99B3174917B457ABE7484C95E9B392C87D657BD74865CAA0ED87E0325C349FE5C9C8C0D91EB8CEE873DEF469CB811ADEC47C6DA7883E975281GA1AF220C07BEC4995B2E7CD8C86EEA552D76A1E3780BCC3627DE744A1838E93AEEA36D9B29D7C87BE63D84FC814CGAE00E000F000EC5AA7A3AF931B17F7F908D51D0EF2172B42EEF59FF1DAA5BB7CB69AA1C746B5CB5DCE37D590D83F3B54DA28A18FC274CBA37B9C81
	26DB2B007DA42C79DE4163B92253EB68B66B4F1F6AE265BA92DF726AD678B2315B19B6E237CC781DAA574B7097A85E25403336CA24FD012403A0AF71E534F35BAA14451CFE0710396724EE0906ACA18FB76AAC73188C76F9DED9180D1F99A6B68E817CAC0059G71G692F4078F985ED3C780BFDE359388E7A395BA15ADB863BDC225BD3B1E833138E4EECDD335F1F2D350015BBBE61F24CAEB377A95BA87627ED1AF6091DF9C8D7768A33C75BABDE3BB05EB77B64BD744A683E310A37E1B59D93C12F30B6CC6378
	A60A8FD560D95BBEC54796C907C1DE662BAEBD23DFC95B5C73AA23CF6575EC2078G851E55F357899C4FC1101788405A7756EBE47E333C86E387E08A40B400A800D8004457300D0FFD7171B847D11322FDD959E5D7B640D39A723845BA4700C7F4F4CBA7C437CD6C32CB8302CC336E8A31EB5708F286BA3E3B01C70D64105C224766F4C06DE21F841DE90051EC3932065038A51AEB3175C940204FA5E03DBB2F2CA37DDA9C709CF7D9C50F6425E079FB2E10BEF1D1BC81B5C285703BAC11586BE6BA67E6426F42C4
	34552F3FA438C0C807A622DD422E2C05E783F4EE49EEB41AEC5DC48C51FD997B2835AD931E61C09F8D9087101208637355B66C072615778805FFEE556C078432DEE7FD2A70F5F5BF6C2775DBGBF84908A9075BA2E1DEF3536E954C5782BF1F19A38F095AEDA15E6BCD637E971D86AE34C2AF1E92A34DE15E6F6AFCCA5FDB79E74925EF069735EC05B4F3D12477AE405B8A0E1AFBC6A068EA4D93DAD41E86307499AA3DD149C9E136412E0F9F5F88664BE707270D2EF7FBC6764FD67F50AA773D5A5F420810EFFFB
	F88D43C39D4A5E0E790BE83E687138EDDD039E29C572F8EC0E9EEF79A5D709AC83A3264281D957A29D513594F4EC8268643A86DFDD2768E7E6277342204BC2C6105CDF6F216B328768035E4076B71FA03ED4B8CF1F20E9EF7EF347355A4E9710FAA6BEC73FBDA19E6A7EB0BE9E6BDA857EBBC7717B1E939361BDD1E0BCFEBD29BCD85B5FF94DF75F3D7B1AF65F1DF9CD3B6F7E681A7F3E7960B52D3E4972FEF50D0CCF67A507222FBFFC0D4CFF5DCE07C322830E9791184F7B2A0568E1F358BC2DB6EB0F64E931FD
	A1014F79866D33F40E1D8D3F635E40B97152B61C93733F18AEC41D5D2B69BF055CF3CCD7BA1F75C773FC6F1C48E4FA64CBB206A43B5487062AF48EBABC8C7FDBBAFF4B7801B40A6B2AD8864F131555B63BF4C4F28C32BE38CE48BE4E70A7EC52A5A57E97061594AFBB69ACCB4EBBD9404A54CAF6974BE0E3DACCA37258C14EG001079B8262BE8BE0EE9F9622AF45A6D4C148246B17734A963187079AC8ECA00E78A2913524CBC25C51315645297A4A14DFD9DDAB4E510EF61B4771E512231137CA414FBEFAAD9F3
	86247EC18916F2B289B5C817657578C06ADAD55E8951BE68DD674BD31793DBA866AF06AF51E93719D1C7662C8B3C7E4FF50B8532DE33BAD99F8B97C91DAE372D8F26FBB2A14BF60B5C2F54A58464A731E2A4BCE3GD81BCDB9CF7254F1D7C292AD4F63AE627BB05C1FA86EFB50D96B9623BFF9A4D3999BDA876AB57BAC0142B7D9DFADFBD36DE77FC7BA293793702EB7514E91BA651CDFBEB0606C36D1B7C736D9CF1A32BFB31CBD0D7441F3B677G785CD6E9886D6ECEA33292BFC8C64B36BB39ECC4CCEA6B6AAB
	9A3C58D7A4F6F90AA42B4DE3B456131EFC42B6B0A85A29E965187459368C390E40B7B15EFB73A4E943C8C84F811877A6460CF12710B67DAD24CDEAA73487A1ED81303F0534FF6B6075F25A7927E93D101E83B00F53BEF706577BB6523EF116568BE98B009D0069794EA6836AF9DE3CA88E958DDC02F98F542BA67EA42BEFC7A9674D6B7BCD9D658D69B9G735E46F92AF52F324D2362BE77596669492C2D8D496AB60F5593FEA5002FCD46B6FFA5ED0D224DA1A685F73987C689DF8B525358F81053F7256BF1BC88
	38A67D23CDABB66910793B12C72F63ACF6301DA23A86A62BE38AB74587A72BE30A05A7B1268805BC619D3EE7F3CA0B3F759D46F7689DDF7CFBBBE8EC740E1AFFBF45073C23663F209D794F003C0C8548BF3EC30B3FFD81639B3040977F1EB3243E298B547CDD94BFED011A7FBB2711FF9864E5AEC47E7FBC23455F3310711D3250977FF31DB486DC28666F2478198B557C134FA27FD910E7C8C17E9F157A0B0B07D3C6728FCE411852D8CF6A1B11220ECDCD94BFABC59D1B4E2E435874D148ABF8976324A79613FE
	9D846950F731DF1768D7AA7AF51353667070BE7DF7BD19531DEEDB0F4D013897A82E5B69264E8673F15F21F3CE9854998940F0BBCE92DCB424635EC57D1D2784BE9606943FD3CFCD3EBF38E9D2F9B01DD3CE219D525FC3BBECEFC7BBE43E07F6F0F4F8F53F2478ADF4CC3E9DB6CEAA8F2673428E34C38937C37EE934C39937C35F992F6E97953F971D1EFCBB984ECC3E9D961CC1BBD8399D361DC5BB38389D6C1DDE5D87953F5FBBBB79F6F83FF3726DD0D10ABACF65F6F868G41C5C0FAB637C3D9BD71A993F93A
	1E26B397E17A093A31E4A5E53F1ABDDE9E1347C4E3E1A530B9744945E43E9E6269E7E87A320071CA66C9223787024712C564AC66D6B2C54BFE61831708CFFD8E1CA3F013DADCD237B0FB913167211368D38FADE2B688121FE170E475BCCD12B27D41F6244F927AA9DD017C2C05A723CFD96D257F6CB452774AF44372B31453172C71529B4FA2FD24CC97A3BF936149E8CE16A2CDE6EA3FBE9F4084E0858884E036F80A76936A6CD6086E14D4F246755A81ACB7835211292C5EF8F812BD94D62E301D88433C48CAF2
	1AC336049C4497E3B14815FAE58CE25231323D709C86E03E64636DCADFB2730EA29D2E736AF61D2AE3B2CC131F4417E43EEBB7DD87C919B91037A20D48FF6B49ACC5D9A2674F41FAC570E4790F0C48A76D3ED3E079CC2E5FEF47F31A145F4F946CBF2B76F916E4C9E3728C6173EA36107517575218ECDD6DA8DBF89A4A965C6EDF36075B55327DD2160D55A91E46BA93F81D371C76DF67C32755F50A294ABABB4FE21DF969D8674DE77D5799F5D6DDE725CED9671725D8E79B2F7307254AB2249F2D9ED1675FCA55
	BC378B4A7CB5871479242DEEDB2C4C1FFEC0AD53ADF2F966373DEFC43FB198E40A86E0E37E06831B844D98C99E95B05629CE96DADE50776B8DG45FA6C47C3BB303F045DFB07E08EE8536CAFF116433ADABD6BA7F63D379F47EE1A2EE3F24C5F69A3D6A3A34B4E6C7A53FA0C55E6C29DCC2EGB311AB8E5249G190B7BE5FF7467E4CBC21E83075690DB1E904F4B2795D59064BA5D38F9DC8F6C0EF61D07284BA27688B8D7771E3C3EF326071620BDE67D62EEA162BB0BF54BFD0C1F0245721CA8BFB30733A91E49
	1F19C56CB184F9D381A2GE2G6281D2G18DE3FDEC3E4849508C2659E0F585DCBF785D0B3320F65C513891D695653FEFDE7EF67CEDFDFB9696C75158B749B5378AE3759F8FD65BE1AF0B996ABC55331113924DFF5F67BE70AAFD46059D8BCE544335BDA488BDB02FD56361E34F9E6C63F3E9040845081B08C901201ED3EE4ED31B046191C4569B2CB97A53B6218026997D12F56AFAC231F7BB4D10A5FA375CB22E3ECCE8653C34F710BA8BEC101E77AFD0762C9BA8572BA1722FE619B489C75A82463G521741DC
	82D0388CF57BF45DD6261BC6DF2E32761035161CA4FAF0DCEF4C723DC71E17B57679634EAC32EF566574F81CFD24FE44FF1CC56C65F13AA8D20EE533C8FBC1385A6BE124B25E18C566A43BF40E22191D7FE054BA7FE3F94F503CDA50598E908CB08B0065F59AA77F6C6E32A60F2F6B6CCE5EF7DC6F3BDDAE2F57BE3B78F23D7659450D6B7D1FDDFCEF3D6F330B296B15E797ACB6F81C2EA1CF014D6781B0DC231947514B31AD96D0DC89244D9C27BFC2F03D108E6238A0335A1E244CC46690339FFA79CCEB940F09
	46896AF69A5DE6919BFC37597FEE98FBACC5EF207B52B086AC17DD9243BD36016E711CBBB7A071B17B4406CC0E2B9BE8F28E584866B6731B57D60EB807G3B1078AD811EA9G0CA658242449DA41E84C706CDC01B4D155CA1AA119E696BC23B90D31D6C993A75398D642FC34926303976A097CF67132F39064E771413B5C37B781DD87C0A0C088G09A54349D3104C869F364636DBA7989AC9FFEFF159ED98BBB0FC69A1528E7231045959435C9FA6737EA34AFA13E51EF92BA0C6DA05FA85D814B466D50CA6901E
	A12B50673BF878A3415FBBE9C5B0738E9A0D4A83B26AEB55D76259C878AA743F1E2D24EBG240555B01F2FC69E1D1B474943FB58C66B3BD605BC0AD7A30F1555741DB2C887G04F09E1F6FAC9A9F0F1187F7344E64EA2E8B67735D9A2A8B2405CC50A59379B4E60FD3976FA1A0FB072F166B1209BCD2697EE9AF2483GC2G581A393D817D73F040A5F05EAF94E58B8A3F38C83C64B19AAB1C645C50ACF7CCDAEFDF830FE356A02FBBE91DED10F681CCD903BE7B798B6823665CFFA77543FD45F8535630396951B5
	DE1F9D6713B4614E229CC153E777F6EBAA47BF1A78194DDA146D36E3744C865296G7BDA54775B997EEDEDB42A47866391E1C1DB87F29E3974BD2AC8C84F81183796676C2315DAFEDE7ABA5F73B073F70E19C8FD06F57D2A3D7AD3941F2F4033B67DE7A536E9B164CDDD077C3FDD2545BFF9CC7E5F2263E0C5161A7F3FD03CA1CB4DFFDDB5724F073CA9D9487F5DEAAD7E733246623F100E8FE13D1AFFFAAD15EB3D1A7F77EA103F81720657A37FCB35DA7C63560F45FF100E1D24D17C2FD13C30C14DFFC99D72DF
	81F92E8D487F23066B0B8F8EDCE7B977044BB13DD6B33FFB03DADF830BF9C769BB1D4FA173A02F69FD743B9B8DE4CD1885699800A4009506FEFD9EC02981FD6F1E77497B1C1AB1B2A01B1DCEBA1E49EE17E883FF0A3A62EE111C68B09D67B6919D4D866DB66C5440B3DD16B453F79A8D6A58E2AD45CFB128E30B5B0FE1EC918AF942C6EC53DFD97C59583A51977F5B689CB134D1ED63D00A8F5C2836717663FC9F857212B6228FF90E2E0DED1B7A69B995132955A470BE5029787DE265646F3D6BCC133F77FEC795
	6A1C4E77553FDB6D55B3D871FBE375646B3CF7D2F9B01D33EAD067922EF3EA2DD74FE50A5F3755CC3E4EE1134AC3FEAF3696F536F21D2F54F9758CD47CDEB9669947D757F947B740E33D592B53870A5F77B5CCBE6F28C96541667BD6036FF86F0CE16CF82F47203DF752E7983DF762B6E86F1D8C9914FBA72C2F3DC7FD45DE68EB811BF0EE2DBD464F78B661FC3F32196022A09D4BF1860ACB06F4CEB6626EBAB69EDB12BA5438A88D1CF6FBC50C0B07D68CBE1EB81E59E00E051FF1F35DEEBF6E6D17F7ABFEFFB4
	2177EF2F2F0F1AA615870B052FD662D9A5CB2FBFC676522D360BB608FE68CB254359A45E5ECD7B87FD479052F3A86E6C31BCBF75C7E53336E5752EDDCEFED78DC95D036C3A818DABD85E618DA42FD13CE86B9141A11273D82CDD5F2CBE67A3FBC3B9B97D7A629C0C478F57E95154C2FEC78E4A7A32451F2C1E9C252C26E3447778C4407306E917146708254BCCBE5FC91BA25793AABF63FF14AD57B9FC0C6CF3D4CA643E8E56FBC9D24608B939BA5D9A9F754E116B93B60F0C91D9DCF6C3B346FEA51BB1AE3B5FC2
	5F0D05F4A0C0A8G233DD4073433B86DDD54271D8769748F40369F605ECC46F1C2578281F02F00E7A66FEE24FBF6C05374817A35CBB712F85217DF5B64F63AA43767B2435237D6653A0A33C91B38201EA000F000E800D800A4000CDC701FF31147510D85FEF830E05D6BB04BFED2335ADFBD186B1DEF1AF2FD792BAC2635662A7D65DD4C0F4DD57B4B8FD850DF8E023C44DC1CBB066B547CADF9DE1E3DF93E782F233147F01E1A7F87949F1C2766FFCF83721F81F999DB50E63ACD24CE3E2F4BCCAF2F8BCBB711F3
	82727E38CD345B3E202F3F36G21230765FF166DFB6D2A489EFB6DEA4856DE3B0EE50FDE3BCEE4EB2FDD9D594A350B61C439DE525E0D648DD3DCAB33497E8A5BC7BCA2BA449E45FE74FF5371E1873B84EC4175EEB31F6BE7EC41765AFE1C60E2A01D40F14594A7EC6D5767EFC55C4FADE34D4B240E714E4389AADA46A3E6CC9E93591FD62F0B4C863751392096746A580AF6D95B00F689607AEE247B74E1100E6638FC0A0B07F446B6445DBF4A27A274DF47DA954679BEBA0A073FFD7709C01A00737F4F1AC76FC9
	4F666BD0A168EB82308384G90DFA8EA9B21A961EBCC483651FA07EDD32EB182FD77D55EE723AB1E7C6E50C132469E9D743886BD4AFD692E634AB5AEA31F71BD884F26FCDC879FBC25DE8789CDAF648F67631C7E033A513ABD44574F2979EC3FBD8C1ED1794C47E33A6DECC05D1E4A9F2D5B3CFC25EEFFEDA4678FAD92CC8976E660A3A97413CE11F1EAF1CB9259141617FAD94793A37674D3E47EA55BE1AD5F0EBA9635A8E9BA37B31AE9701C359D77F9131AF17FE0B66052F7603E671E4E91FB29EC1F3746ED33
	D608BD72C6AFDD8F8F3660BE6F419D38C61EEF21FBA8A48DB0E3872E279D96DCCFA3F7200D27DAC81A4EE7822E7567B315EBFD6CE5034F35BEE9073C46FF28359F6CB9D3A00CEB6FD671CE479D47F15F366DC354A70D3DFB8E6919GD19F22BE2F52F7E049D8FB0A536E266F03E796746B8BG4A8A504EAF723D3FB6408597E09DFB6A51FE5185D847D97A2E333033DFEFGA85E0934479A50FE35BB51FEB7B5901F144C7BCC67623E42B1F4D677B5D6774DE654BBE0A74AF105760D88C847GA46C443E11F6ED2F
	8F9E5D74A5GFA86812E2B5DE8D4F7DEDA6F399664A56CC2DE0F51BED38A69A6005EDD7C5CF314DFB29630F299AD57D72EFC4265183F719C15BFE017FA5FEEAE45876DD26F5B39DA30BF04C0DE8A57D79C31EF5879D13F5E73117FF5017907FF4F49036794201D81908D90871084302290FCC30052C274F596E47B73C179E14928BD553B0E7B330D2550170FF6090E1F5EC235ED82A85ED328364D52D3E81BG480BAFC45B4C9FF12723F6B74C3F3B3DBCDD3BFD711FCE47E460EE350F98C671A13B55BE623EF674
	9143A0AF3388793FB64AC79DDF1FFA77BA4BFDBA6A7D18717545BF517943D32436F7AC458794296DAD55233D03A0AF116BFBE244B90165E398179F7B6F0B72BE23596F5D8F0DBA18FC2F5179A678E3355C49949F7A31DA6EEF7135EBA6642DA8C639AF789D432EE2DF7C4BE9EC92D82C66DFCF714145EA7EFF62E3B8947204BD48BF3EC56036F8C5717BE24B646F035CB229BC185FBBC457F1AB687BF8BA622ED25CB060DE53E31CB1AF47F7DCB4BFE76C38A8A5C7BBAE52ED9E9D97AD592C9D972D5CBCBAAEDA3B
	19546B16FAA00ED346C5064D3E6322870F232F9F425B792A62DE4FF7943FD7FF83F7FC8A3E01BBBE4B68BB538920EF4A9E0CF90ED15C0A3D30266CC5DCCC875A463497EDF39F251B8A69F00E7B89454D06F4A2479DE8D76BC1684712EF6AB8E83C36EA9E45E3BCF150F8E3C6466369FAEF7B7FC3717BC27D64770540C965415A183D4FC1467CFA0EF3B76018EF6573408F783BEBD97B300D5FEC765A63BD456F6FB6CF3EED26CDAA8F26738DADFCAF1C6BDCE3716A5924787D74B730863CB129BCD88CA06EA0EB1F
	3CA756EA33FAFA99BEE7E82597DFAB49F15D34FDA4AEFE6C3849CF3D3836625E029D4A846CE35FF5A369297B3C6BAF0BFD363413781C6DEFC1ACC8F9143613F8A8E19F46C73D6D23E378AF79FD2052929643B74153DA42762D182CF3BB4612B5CD8143D0B638040D8DFAB735C4AD6B66867A9E1DDD925DCDEE1BC35E2FE0F9F35CA40F6DE3F8DFD5E2B1FCFC3BFA1F0248988E75BFDA02F1FE2228FE8F0F5044C2FEE2896E69BFE44E7323CF46A7ECBEE534373778234D11E9F3296CD0D6403F444323F102406ECB
	45FEC0FE33787E6FAB14713D6949453AED02F6FCDFAC738A78449B5F07BDBEDD476C56586E2D731024BCF7A83E320A52E85599A157957D09775C0179F377713B5C4910D77CA9465D2F713BC33D1FE23C3A195E871F8669880098007159A371B033479F72E8125615433FB710406BFF045E374E5B8FF19BC053FE1425200B5012B1563B1F6F9714935A59100E87C8590FFD5F5ACC6AECF43A69198443BD5822BCDBE131E36496525733CAC1E7G8B009DA0GA084A08CA08AE08E40BC0074837DFA83C0A1C099C093
	0095E068G4617DBF2499DAEDF71253C092408AED10E2D5B683701F8BD67F2377A292752E91558AEBC4EFBBF3C4EFBA43F3C4EF2C993AE476209DBD95FB9200EB36E207830836AB8E3DB87764B88484BB1629A33E554B786820CDE3FE82A51D71C33DFA4754DB42A63618A0A0FB42A63615FF6E2BC9C83F9A51FA17F5DDDE41D8979CCE37FD68397250123F38E6093D4F9A91016E3D95A37B38F2AE5FD0E62738F2AE5154AD156D2488BBD08FDA9E60B6FD86449ADE347A2F1DB34E31197360C0EC5DE5E229D0B3C
	3EE5F4AC725696A2EB8F0C09B6CE371063CEF21C30F5290C230BAD47AF5E3ACC4663E24B70AB369239AED1DC1B0CDB23401D14F19B361279GD46EF53A31EC76D6B28E4F596CF6164E25B4B64700646176485FCAE43439C4055CBBB94EAB770EED246C4008F17A1F0A6FGCCD37C5E798DFC9320629B78A6C0E98746C842A10C11FE423F8910FF88474FA111601AA0ED6538DA0A8B04748C0EFBFC427756C91DDAF82B861E71C818B08FDF7111D6EC45ECF0142EDD51A0C39C374B6D941784692C4F91372B0C608A
	A0ED6238C10AEB03749047BDD406B68D62388594978169184F51FECF175373B7C8E79C66FBB494D78869A60E3BD00EB6A0F42B64B2DFEF7F8B64BC50CEDF650E3D9A18FFAB72EFECF98EB37FB6881E53GC86C4DFC3C5F1E4E73D3CF44E1659955A3743EDEE5CD933956A763269CA3E7B2DEFF36E290E6C687398715DEC66A7FC2D45E7F1DAD73CEBC0CBE6BDD1DA36E0514317CA0F8CEABC37F65DC17767BB557E81FA13EF684278D6FA27C3A1DFDAE2783267623EE9B7CCBCF6AD9195F313E8774F3F819723255
	5E7A4D4EEE76DDABC6FF0A76232B3038E414A3FDD09939448B75DB1C035D3DD5C3AE51E1251F8F134B5CDB06679A06F23473D3E57E6CDCD02E34731AF2222F53AB3ADC6F4EF2FE4646E5F93B1068EF123A86FB6440215B6D345BC75C5DD9E3A3311349B6600278E79495EB078D954AF6F015333D19D0F80617E3DB25D5EB51C443B3369C4F37EE62BECA92608AAB50DF7DE3874AEE2DC0FFF591258D0574AC0059957CFE53E965BBB472BD6CF3BE6EE11B45AEBC3BFAC044F338788A3CD3F235DA798D6A485507FD
	5EFB4E29E4EDF030529BE394E663BD701BB66F9146B8C3D40D81BC6AE232BD5909FDE2389275BFC47D2CD9101E8D90DF097A7FFB178E1EFB0AB1E6FAB1DF60ED39390C5C9947235822CA0D0EDEF43C312171E8EBA3AF53DFC61EFB8E944B69DF155177575C0392CB3FD9260C85982E2C9C794826AE725E1EA9BAD155EC292BACB71FEDE9B257D9F89B75D4ED56EE2391FD1259233182E31D94EE0FD574DECA0929DFEF86301A306FFCD60D3443A624DDC06F104C0274EC00F8934E5D37E8FEEFEB6C79783F2E335C
	DFA6DC0E792D8374FBDDA9A6356FBFCC7199D5EA5FDF4F3F6F1585F9C3D5286F0322967F58AA793BA370CC2F9E39F631986B596DE41EB4C0DE890099408A90D80DFE728F370D799D6D2AA1BF5F510E6D74E71799553EE2128768180828D65B6597947FE8355AAE15A25AA50668D203F6E929C07EA1B59AF109860E7139017641281AFE55F74702A8BEC601E77C33A81E2463A04FD40B7C4B78DD26193523F9C5EA60989F873D1B94D32B66BFC471F135EA7EC28D72CF04BCCB9D723FFCDA60FD7486456F655F40F7
	2672A71587736F9E23F3FD84689BDD07BE5F5EAE748367F05CF90A936A7B7506FA6C5B4FEF779D834E5B2EFD8F7C0D6D5A774013377B7F7E32304357BDF1A65F7CCE1439301E3FABA7FA5BABCA713BCE1C7C366B1AD49EF21B50393F8D749D2E47F6FA24826D904C6DB0053EE79D49537FC65331BC3D69EB392FC46A77ED0FFDDF8B8FDF3EBD533D152EEFA9A0C7DE835AA33C9A6DD152007AFAEA087E1DBCFD0D2683F9FAB13DF3C372A72FFF64D29EDFE72C223653BAA1DE9C79ADB07707101477246B3A727C14
	1F512074233F55C53FBF6BF4CBBDEE6720430A7EB90BB5E781EDD4837A674BC4FF75460D2C3702785397A53747568D4B9FF5EEAA4077F35AED5D17E59E2134BF6F86C8EC4077817FDA610FC731D949E3FD9559AFD6787596B0C739DB9265386A11CAE5DCE50232A406351B5107F4D40FAD63CD55A8E30099E57C3FEAFFB2C60D103172900DBD14F1851792633F9F572865A4F1F60C599B5B1136EE158664369EA0DB46F27DB7F612B8CB616B5A1D6EA237E4457C8FE9BF29928764FB98BF2D58A328FDD66EF9AA1C
	564A6623E6F3C5F97359F2F3EB7949964E6F6D8AF60EC476765027FD6C6A08FB6EDE1F562BA973C7ABF96C41F51BD67D5564092FA6CF0F53A573F82186E3081CA3B8669751BB6396C8779E99BD6E07B5F02C5C4BB57E7C33E00DF2ECDE5AE0A2B24C007C48A3B8D7DD2538E8C847F25CEFCCB8FFA5F21C2E161F89B7220DF61B16D39D1DF60B4DC5ECAA47BCFF5D656D87D3CD74FB9DAE31FF104758771B5096250D78ED3DE779B7B8EA815781B08C908A9049E9B6726FEB4482AE91A073E83F3E982069A84A7430
	8963A3E67734AA0CBFDCC7713BE91FD5293F1BC6A629A2813FFB56D7257EEEDA6A087C3B2A545FCD4B9D117F47AA75F75330BC1369F0AD7F66981769D22DDA2657C75479BF357EE5FA26D6ADD3F92AB2FFE51DAF1964F7ADC7FCBFA08E64CABABA1E7744CCA36E5DE7B4756BF31A7C17E37DA9378A5FC54865387CAAFC9761C30EEB2FC5BA228F43F556A29D31917BBF437E75783D29647F3691749EA11DFCE3849E6425D50F30C4FEC1989064DB9A83EEE1B51F166A68FEF85C45FD4634FFFEBFCDA72457890B59
	9CD2A72413CD7EBAC1CF4FG6A04B4984BC3AE59F69C867A05EBC27AA82CDB586CB74C7F4DCB77D5CD8F6D1E2D93D26B04650A5DA24AE1098766D23730EA646C19FA4F7B5F4E3CE55F20CE482896961F936D2EDE9144231F6CB68B4B3B281C64D2105DA3E41AC9590F216C47346CE7E6C157BD6091323D78EE02E739ADAE31DB821225D651FD0156C034DA5EAEACBD676E69228CBC022E3D3A9D58529449DEE99616F9FA5937AA07BC9E61FDEF55CD64C4D04E780CEAA4ACDA8483107E1123C2793CF767174A735E
	649B377B3CE7965C4C7A540CE66F7E8E0B05F753EFB94781BE2599E3042B2DE4DD7475ED3E16BE51EE7708768B658EAB79CEDF45E50F640D897636117A8710C64E147FCF02B6A277F9C5197F87D0CB8788A7B89FAD3BA1GG8CEFGGD0CB818294G94G88G88G640507AEA7B89FAD3BA1GG8CEFGG8CGGGGGGGGGGGGGGGGGE2F5E9ECE4E5F2A0E4E1F4E1D0CB8586GGGG81G81GBAGGG75A1GGGG
**end of data**/
}
/**
 * Return the CGPMode property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getCGPMode() {
	if (ivjCGPMode == null) {
		try {
			ivjCGPMode = new javax.swing.JLabel();
			ivjCGPMode.setName("CGPMode");
			ivjCGPMode.setText("");
			ivjCGPMode.setForeground(java.awt.Color.red);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjCGPMode;
}
/**
 * Return the ClearPrintButtons property value.
 * @return com.cannontech.yc.gui.ClearPrintButtonPanel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private ClearPrintButtonPanel getClearPrintButtons() {
	if (ivjClearPrintButtons == null) {
		try {
			ivjClearPrintButtons = new com.cannontech.yc.gui.ClearPrintButtonPanel();
			ivjClearPrintButtons.setName("ClearPrintButtons");
			// user code begin {1}
			ivjClearPrintButtons.getClearButton().addActionListener(this);
			ivjClearPrintButtons.getPrintButton().addActionListener(this);
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjClearPrintButtons;
}
/**
 * Return the JPanel2 property value.
 * @return javax.swing.JPanel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JPanel getClearPrintPanel() {
	if (ivjClearPrintPanel == null) {
		try {
			ivjClearPrintPanel = new javax.swing.JPanel();
			ivjClearPrintPanel.setName("ClearPrintPanel");
			ivjClearPrintPanel.setLayout(new java.awt.BorderLayout());
			getClearPrintPanel().add(getCGPMode(), "West");
			getClearPrintPanel().add(getClearPrintButtons(), "East");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjClearPrintPanel;
}
/**
 * Insert the method's description here.
 * Creation date: (12/20/2001 5:14:03 PM)
 * @return com.cannontech.message.util.ClientConnection
 */
public com.cannontech.message.util.ClientConnection getClientConnection() {
	return getConnToDispatch();
}
/**
 * Insert the method's description here.
 * Creation date: (9/11/2001 12:11:11 PM)
 * @return java.lang.String
 */
private String getCommand()
{
	return ycClass.getCommand();
	
}
/**
 * Return the CommandLogPanel property value.
 * @return com.cannontech.yc.gui.CommandLogPanel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
public static CommandLogPanel getCommandLogPanel() {
	if (ivjCommandLogPanel == null) {
		try {
			ivjCommandLogPanel = new com.cannontech.yc.gui.CommandLogPanel();
			ivjCommandLogPanel.setName("CommandLogPanel");
			// user code begin {1}
			ivjCommandLogPanel.setVisible( ycClass.getYCDefaults().getShowMessageLog() );
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjCommandLogPanel;
}
/**
 * Return the CommandPanel1 property value.
 * @return com.cannontech.yc.gui.CommandPanel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private CommandPanel getCommandPanel() {
	if (ivjCommandPanel == null) {
		try {
			ivjCommandPanel = new com.cannontech.yc.gui.CommandPanel();
			ivjCommandPanel.setName("CommandPanel");
			// user code begin {1}
			ivjCommandPanel.getExecuteButton().addActionListener(this);
			ivjCommandPanel.getStopButton().addActionListener(this);

			ivjCommandPanel.getExecuteCommandComboBoxTextField().addKeyListener(this);
			ivjCommandPanel.getExecuteCommandComboBoxTextField().addActionListener(this);
			//ivjCommandPanel.getExecuteCommandComboBox().addItemListener(this);
			//ivjCommandPanel.getAvailableCommandsComboBox().addItemListener(this);
			ivjCommandPanel.getAvailableCommandsComboBox().addActionListener(this);
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjCommandPanel;
}
/**
 * Set up a connection to dispatch, for database changes.
 * In the future we could also get point changes and dynamically
 * update a graph.
 * Creation date: (10/31/00 12:18:31 PM)
 */
private com.cannontech.message.dispatch.ClientConnection getConnToDispatch() 
{
	if( connToDispatch == null )
	{
		String host = "127.0.0.1";
		int port = 1510;
		try
		{
         host = com.cannontech.common.util.CtiProperties.getInstance().getProperty(
                  com.cannontech.common.util.CtiProperties.KEY_DISPATCH_MACHINE, 
                  "127.0.0.1");
            
			port = (new Integer( com.cannontech.common.util.CtiProperties.getInstance().getProperty(
                  com.cannontech.common.util.CtiProperties.KEY_DISPATCH_PORT, 
                  "1510"))).intValue();			
		}
		catch( Exception e)
		{
			com.cannontech.clientutils.CTILogger.error( e.getMessage(), e );
		}

		connToDispatch = new com.cannontech.message.dispatch.ClientConnection();
		com.cannontech.message.dispatch.message.Registration reg = new com.cannontech.message.dispatch.message.Registration();
		reg.setAppName("Commander @" + com.cannontech.common.util.CtiUtilities.getUserName() );
		reg.setAppIsUnique(0);
		reg.setAppKnownPort(0);
		reg.setAppExpirationDelay( 300 );  // 5 minutes should be OK

		connToDispatch.setHost(host);
		connToDispatch.setPort(port);
		connToDispatch.setAutoReconnect(true);
		connToDispatch.setRegistrationMsg(reg);
		
		try
		{
			connToDispatch.connectWithoutWait();
		}
		catch( Exception e ) 
		{
			com.cannontech.clientutils.CTILogger.error( e.getMessage(), e );
		}
	}
	return connToDispatch;
}
/**
 * Set up a connection to dispatch, for database changes.
 * In the future we could also get point changes and dynamically
 * update a graph.
 * Creation date: (10/31/00 12:18:31 PM)
 */
private com.cannontech.message.porter.ClientConnection getConnToPorter() 
{
	return ycClass.getConnToPorter();
}
/**
 * Return the JScrollPane1 property value.
 * @return javax.swing.JScrollPane
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JScrollPane getDebugOutputScrollPane() {
	if (ivjDebugOutputScrollPane == null) {
		try {
			ivjDebugOutputScrollPane = new javax.swing.JScrollPane();
			ivjDebugOutputScrollPane.setName("DebugOutputScrollPane");
			ivjDebugOutputScrollPane.setVerticalScrollBarPolicy(javax.swing.JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
			getDebugOutputScrollPane().setViewportView(getDebugOutputTextArea());
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjDebugOutputScrollPane;
}
/**
 * Return the OutputTextArea property value.
 * @return javax.swing.JTextArea
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JTextArea getDebugOutputTextArea() {
	if (ivjDebugOutputTextArea == null) {
		try {
			ivjDebugOutputTextArea = new javax.swing.JTextArea();
			ivjDebugOutputTextArea.setName("DebugOutputTextArea");
			ivjDebugOutputTextArea.setToolTipText("");
			ivjDebugOutputTextArea.setOpaque(true);
			ivjDebugOutputTextArea.setBounds(0, 0, 15, 19);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjDebugOutputTextArea;
}
/**
 * Return the JScrollPane1 property value.
 * @return javax.swing.JScrollPane
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JScrollPane getDisplayOutputScrollPane() {
	if (ivjDisplayOutputScrollPane == null) {
		try {
			ivjDisplayOutputScrollPane = new javax.swing.JScrollPane();
			ivjDisplayOutputScrollPane.setName("DisplayOutputScrollPane");
			ivjDisplayOutputScrollPane.setVerticalScrollBarPolicy(javax.swing.JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
			getDisplayOutputScrollPane().setViewportView(getDisplayOutputTextArea());
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjDisplayOutputScrollPane;
}
/**
 * Return the JTextArea1 property value.
 * @return javax.swing.JTextArea
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JTextArea getDisplayOutputTextArea() {
	if (ivjDisplayOutputTextArea == null) {
		try {
			ivjDisplayOutputTextArea = new javax.swing.JTextArea();
			ivjDisplayOutputTextArea.setName("DisplayOutputTextArea");
			ivjDisplayOutputTextArea.setBounds(0, 0, 160, 120);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjDisplayOutputTextArea;
}
/**
 * Return the JPanel1 property value.
 * @return javax.swing.JPanel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JPanel getExecutionPanel() {
	if (ivjExecutionPanel == null) {
		try {
			ivjExecutionPanel = new javax.swing.JPanel();
			ivjExecutionPanel.setName("ExecutionPanel");
			ivjExecutionPanel.setLayout(new java.awt.GridBagLayout());

			java.awt.GridBagConstraints constraintsCommandPanel = new java.awt.GridBagConstraints();
			constraintsCommandPanel.gridx = 0; constraintsCommandPanel.gridy = 0;
			constraintsCommandPanel.fill = java.awt.GridBagConstraints.BOTH;
			constraintsCommandPanel.anchor = java.awt.GridBagConstraints.NORTH;
			constraintsCommandPanel.weightx = 1.0;
			constraintsCommandPanel.insets = new java.awt.Insets(5, 5, 5, 5);
			getExecutionPanel().add(getCommandPanel(), constraintsCommandPanel);

			java.awt.GridBagConstraints constraintsOutputPanel = new java.awt.GridBagConstraints();
			constraintsOutputPanel.gridx = 0; constraintsOutputPanel.gridy = 1;
			constraintsOutputPanel.fill = java.awt.GridBagConstraints.BOTH;
			constraintsOutputPanel.weightx = 1.0;
			constraintsOutputPanel.weighty = 1.0;
			constraintsOutputPanel.insets = new java.awt.Insets(5, 5, 5, 5);
			getExecutionPanel().add(getOutputPanel(), constraintsOutputPanel);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjExecutionPanel;
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
			ivjJFrameContentPane.setLayout(new java.awt.BorderLayout());
			getJFrameContentPane().add(getSplitPane(), "Center");
			getJFrameContentPane().add(getCommandLogPanel(), "South");
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
private LocateRouteDialog getLocateRouteDialog()
{
	if( locRouteDialog == null)
	{
		locRouteDialog = new LocateRouteDialog();
		if( ycClass.getAllRoutes() != null)
		{
			for ( int i = 0; i < ycClass.getAllRoutes().length; i++)
			{
				locRouteDialog.getRouteComboBox().addItem(ycClass.getAllRoutes()[i]);
			}
			if( locRouteDialog.getRouteComboBox().getItemCount() > 0 )
				locRouteDialog.getRouteComboBox().setSelectedIndex(0);
		}
		locRouteDialog.getLocateButton().addActionListener(this);
		locRouteDialog.getRouteComboBox().addActionListener(this);
	}
	return locRouteDialog;
}
/**
 * Insert the method's description here.
 * Creation date: (2/26/2002 2:13:06 PM)
 * @return int
 */
private int getModelType()
{
	return ycClass.getModelType();
}
/**
 * Return the JPanel1 property value.
 * @return javax.swing.JPanel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JPanel getNavigatorPanel() {
	if (ivjNavigatorPanel == null) {
		try {
			ivjNavigatorPanel = new javax.swing.JPanel();
			ivjNavigatorPanel.setName("NavigatorPanel");
			ivjNavigatorPanel.setLayout(new java.awt.BorderLayout());
			getNavigatorPanel().add(getSerialRoutePanel(), "North");
			getNavigatorPanel().add(getTreeViewPanel(), "Center");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjNavigatorPanel;
}
/**
 * Return the JPanel1 property value.
 * @return javax.swing.JPanel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JPanel getOutputPanel() {
	if (ivjOutputPanel == null) {
		try {
			ivjOutputPanel = new javax.swing.JPanel();
			ivjOutputPanel.setName("OutputPanel");
			ivjOutputPanel.setLayout(new java.awt.BorderLayout());
			getOutputPanel().add(getClearPrintPanel(), "South");
			getOutputPanel().add(getOutputSplitPane(), "Center");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjOutputPanel;
}
/**
 * Return the JSplitPane1 property value.
 * @return javax.swing.JSplitPane
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JSplitPane getOutputSplitPane() {
	if (ivjOutputSplitPane == null) {
		try {
			ivjOutputSplitPane = new javax.swing.JSplitPane(javax.swing.JSplitPane.VERTICAL_SPLIT);
			ivjOutputSplitPane.setName("OutputSplitPane");
			ivjOutputSplitPane.setDividerSize(10);
			ivjOutputSplitPane.setComponentOrientation(java.awt.ComponentOrientation.UNKNOWN);
			ivjOutputSplitPane.setDividerLocation(190);
			ivjOutputSplitPane.setOneTouchExpandable(true);
			getOutputSplitPane().add(getDebugOutputScrollPane(), "bottom");
			getOutputSplitPane().add(getDisplayOutputScrollPane(), "top");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjOutputSplitPane;
}
/**
 * Insert the method's description here.
 * Creation date: (2/26/2002 1:53:53 PM)
 * @return java.lang.Object
 */
private Object getRoute()
{
	return ycClass.getRoute();
}
/**
 * Insert the method's description here.
 * Creation date: (9/10/2001 4:16:48 PM)
 * @return String
 */
private String getSerialNumber()
{
	return ycClass.getSerialNumber();
}
/**
 * Return the SerialRoutePanel property value.
 * @return com.cannontech.yc.gui.SerialRoutePanel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private SerialRoutePanel getSerialRoutePanel() {
	if (ivjSerialRoutePanel == null) {
		try {
			ivjSerialRoutePanel = new com.cannontech.yc.gui.SerialRoutePanel();
			ivjSerialRoutePanel.setName("SerialRoutePanel");
			// user code begin {1}
			ivjSerialRoutePanel.getSerialTextField().addKeyListener(this);
			ivjSerialRoutePanel.getSerialTextField().addActionListener(this);
			ivjSerialRoutePanel.getSerialTextField().addFocusListener(this);
			ivjSerialRoutePanel.getRouteComboBox().addActionListener(this);
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjSerialRoutePanel;
}
/**
 * Return the JSplitPane1 property value.
 * @return javax.swing.JSplitPane
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JSplitPane getSplitPane() {
	if (ivjSplitPane == null) {
		try {
			ivjSplitPane = new javax.swing.JSplitPane(javax.swing.JSplitPane.HORIZONTAL_SPLIT);
			ivjSplitPane.setName("SplitPane");
			ivjSplitPane.setDividerSize(5);
			getSplitPane().add(getExecutionPanel(), "right");
			getSplitPane().add(getNavigatorPanel(), "left");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjSplitPane;
}
/**
 * Insert the method's description here.
 * Creation date: (9/10/2001 2:19:40 PM)
 * @return java.lang.Object
 */
private Object getTreeItem()
{
	return ycClass.getTreeItem();
}
/**
 * Return the TreeViewPanel property value.
 * @return com.cannontech.common.gui.util.TreeViewPanel
 */
private com.cannontech.common.gui.util.TreeViewPanel getTreeViewPanel() {
	if (ivjTreeViewPanel == null) {
		try {
			ivjTreeViewPanel = new com.cannontech.common.gui.util.TreeViewPanel();
			ivjTreeViewPanel.setName("TreeViewPanel");
			// user code begin {1}
			ivjTreeViewPanel.addTreeSelectionListener(this);
			ivjTreeViewPanel.getSortByComboBox().addActionListener(this);
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjTreeViewPanel;
}
/**
 * Insert the method's description here.
 * Creation date: (10/3/2001 2:02:57 PM)
 * @return java.lang.String
 */
private static String getVersion()
{
	return  (com.cannontech.common.version.VersionTools.getYUKON_VERSION() + VERSION);
}
/**
 * Return the YCCommandMenu property value.
 * @return com.cannontech.yc.gui.menu.YCCommandMenu
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private com.cannontech.yc.gui.menu.YCCommandMenu getYCCommandMenu() {
	if (ivjYCCommandMenu == null) {
		try {
			ivjYCCommandMenu = new com.cannontech.yc.gui.menu.YCCommandMenu();
			ivjYCCommandMenu.setName("YCCommandMenu");
			ivjYCCommandMenu.setText("Command");
			// user code begin {1}
			javax.swing.JMenuItem item;
			for (int i = 0; i < ivjYCCommandMenu.getItemCount(); i++)
			{
				item = ivjYCCommandMenu.getItem(i);

				if (item != null)
					ivjYCCommandMenu.getItem(i).addActionListener(this);
			}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjYCCommandMenu;
}
/**
 * Return the YCFileMenu property value.
 * @return com.cannontech.yc.gui.menu.YCFileMenu
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private com.cannontech.yc.gui.menu.YCFileMenu getYCFileMenu() {
    if (ivjYCFileMenu == null) {
        try {
            ivjYCFileMenu = new com.cannontech.yc.gui.menu.YCFileMenu();
            ivjYCFileMenu.setName("YCFileMenu");
            ivjYCFileMenu.setText("File");
            // user code begin {1}
            javax.swing.JMenuItem item;

            for (int i = 0; i < ivjYCFileMenu.getItemCount(); i++) {
                item = ivjYCFileMenu.getItem(i);

                if (item != null)
                    ivjYCFileMenu.getItem(i).addActionListener(this);
            }
            // user code end
        } catch (java.lang.Throwable ivjExc) {
            // user code begin {2}
            // user code end
            handleException(ivjExc);
        }
    }
    return ivjYCFileMenu;
}
/**
 * Return the YCHelpMenu property value.
 * @return com.cannontech.yc.gui.menu.YCHelpMenu
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private com.cannontech.yc.gui.menu.YCHelpMenu getYCHelpMenu() {
	if (ivjYCHelpMenu == null) {
		try {
			ivjYCHelpMenu = new com.cannontech.yc.gui.menu.YCHelpMenu();
			ivjYCHelpMenu.setName("YCHelpMenu");
			ivjYCHelpMenu.setText("Help");
			// user code begin {1}
			javax.swing.JMenuItem item;

			for (int i = 0; i < ivjYCHelpMenu.getItemCount(); i++) {
				item = ivjYCHelpMenu.getItem(i);

				if (item != null)
					ivjYCHelpMenu.getItem(i).addActionListener(this);
			}

			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjYCHelpMenu;
}
/**
 * Return the YCViewMenu property value.
 * @return com.cannontech.yc.gui.menu.YCViewMenu
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private com.cannontech.yc.gui.menu.YCViewMenu getYCViewMenu() {
	if (ivjYCViewMenu == null) {
		try {
			ivjYCViewMenu = new com.cannontech.yc.gui.menu.YCViewMenu();
			ivjYCViewMenu.setName("YCViewMenu");
			ivjYCViewMenu.setText("View");
			// user code begin {1}
			javax.swing.JMenuItem item;

			for (int i = 0; i < ivjYCViewMenu.getItemCount(); i++) {
				item = ivjYCViewMenu.getItem(i);

				if (item != null)
					ivjYCViewMenu.getItem(i).addActionListener(this);
			}

			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjYCViewMenu;
}
/**
 * Return the YukonCommanderJMenuBar property value.
 * @return javax.swing.JMenuBar
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JMenuBar getYukonCommanderJMenuBar() {
	if (ivjYukonCommanderJMenuBar == null) {
		try {
			ivjYukonCommanderJMenuBar = new javax.swing.JMenuBar();
			ivjYukonCommanderJMenuBar.setName("YukonCommanderJMenuBar");
			ivjYukonCommanderJMenuBar.add(getYCFileMenu());
			ivjYukonCommanderJMenuBar.add(getYCViewMenu());
			ivjYukonCommanderJMenuBar.add(getYCCommandMenu());
			ivjYukonCommanderJMenuBar.add(getYCHelpMenu());
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjYukonCommanderJMenuBar;
}
/**
 * Insert the method's description here.
 * Creation date: (12/20/2001 5:12:47 PM)
 * @param msg com.cannontech.message.dispatch.message.DBChangeMsg
 */
public void handleDBChangeMsg(com.cannontech.message.dispatch.message.DBChangeMsg msg, com.cannontech.database.data.lite.LiteBase object)
{
	//com.cannontech.database.cache.DefaultDatabaseCache.getInstance().handleDBChangeMessage((com.cannontech.message.dispatch.message.DBChangeMsg)msg);
	
	java.awt.Frame frame = null;
	java.awt.Cursor savedCursor = null;

	try
	{
		// Cursor set to WAIT during the route combo box update.
		frame = com.cannontech.common.util.CtiUtilities.getParentFrame( getRootPane().getContentPane());
		savedCursor = getRootPane().getCursor();
		getRootPane().setCursor( new java.awt.Cursor( java.awt.Cursor.WAIT_CURSOR ) );
		// Refresh the device tree and the route combo box on database changes.
		restoreCurrentTree();
		setRouteModel();
	}
	catch( Exception e )
	{
		e.printStackTrace();
	}
	finally
	{
		getRootPane().setCursor( savedCursor);
	}

}
/**
 * Called whenever the part throws an exception.
 * @param exception java.lang.Throwable
 */
private static void handleException(Throwable exception) {

	/* Uncomment the following lines to print uncaught exceptions to stdout */
	com.cannontech.clientutils.CTILogger.info("--------- UNCAUGHT EXCEPTION ---------");
	exception.printStackTrace(System.out);
}
/**
 * Initialize the class.
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void initialize() {
	try {
		// user code begin {1}
		ycClass = new YC();		
		// user code end
		setName("YukonCommander");
		setSize(841, 647);
		setJMenuBar(getYukonCommanderJMenuBar());
		setContentPane(getJFrameContentPane());
	} catch (java.lang.Throwable ivjExc) {
		handleException(ivjExc);
	}
	// user code begin {2}

	inThread = new Thread(this);
	inThread.start();

	//--------Setup treeViewPanel and tree models-------------	
	//This model is created in a weird way.  We can do the model creation similar for all
	// the models except for the Device model.  In order to use a different constructor
	// that shows the device types without their points, we have to specifically look for
	// that model type.  Create a new model for that type (false sets showPoints off).
	// For all other 'normal' types, we can follow through with OO and use the same
	// constructor template (no parameters).
	com.cannontech.database.model.LiteBaseTreeModel[] models =
		new com.cannontech.database.model.LiteBaseTreeModel[treeModels.length];
	
	for (int i = 0; i < models.length; i++)
	{
		if( treeModels[i] == ModelFactory.DEVICE)
			models[i] =  new com.cannontech.database.model.DeviceTreeModel(false);
		else if ( treeModels[i] == ModelFactory.LMGROUPS)
			models[i] = new com.cannontech.database.model.LMGroupsModel(false);
		else if ( treeModels[i] == ModelFactory.CAPBANKCONTROLLER )
			models[i] = new com.cannontech.database.model.CapBankControllerModel(false);
		else		
			models[i] = ModelFactory.create(treeModels[i]);
	}

	//serial and route panel visible only when first item in tree is Versacom Serial #
	if (treeModels[0] != ModelFactory.EDITABLELCRSERIAL)
		enableSerialAndRoute(false);

	//add listeners to treeViewPanel Objects
	getTreeViewPanel().setTreeModels(models);
		
	setRouteModel(); //fill route combo box

	com.cannontech.database.model.EditableLCRSerialModel.setSerialNumberVector( new Vector());
	
	com.cannontech.database.cache.DefaultDatabaseCache.getInstance().addDBChangeListener(this);
	//DBChangeMessageListener dbChangeMessageListener = new DBChangeMessageListener();
	//dbChangeMessageListener.start();

	addWindowListener(new java.awt.event.WindowAdapter(){
		public void windowClosing(java.awt.event.WindowEvent e){ 
				exit();
		};
	});
	// user code end
}
/**
 * Insert the method's description here.
 * Creation date: (9/10/2001 3:13:16 PM)
 */
private boolean isValidSetup()
{
	//setCommand((String) getCommandPanel().getExecuteCommandComboBoxTextField().getText().trim());
	if( getConnToPorter().isValid() )
	{
		if( ycClass.isDirectSend() )
		{
			//bypassing all of these checks, the user MUST Know what they are doing I guess!.
			getCommandPanel().getExecuteCommandComboBoxTextField().requestFocus();
			//enter();
			return true;	
		}
		else if( getTreeItem() == null )
		{
			getCommandLogPanel().addLogElement(" ** Warning: Please make a selection from the tree.");
		}
		else if( getCommand() == null || getCommand().length() == 0 )
		{
			getCommandLogPanel().addLogElement(" ** Warning: No command is specified");
		}
		else if( getTreeViewPanel().getSelectedNode().getParent() == null )
		{
			getCommandLogPanel().addLogElement(" ** Warning: Please select a specific tree item");
		}
		else
		{
			//confirm command execution if needed
			if( ycClass.getYCDefaults().getConfirmCommandExecute())
			{
				String message = "Execute '"+ getCommand() +"' -- Are You Sure?";
				int response = areYouSure(message, javax.swing.JOptionPane.QUESTION_MESSAGE);
				if( response == javax.swing.JOptionPane.OK_OPTION )
				{
					getCommandPanel().getExecuteCommandComboBoxTextField().requestFocus();
					return true;
				}
			}
			else
			{
				//enter();
				getCommandPanel().getExecuteCommandComboBoxTextField().requestFocus();
				return true;
			}
		}
	}
	else
	{
		getCommandLogPanel().addLogElement(" ** Warning: Not connected to port control service **");
	}
	return false;
}
/**
 * This method was created in VisualAge.
 * @param event java.awt.event.KeyEvent
 */
public void keyPressed(KeyEvent event) {
	
	if( event.getKeyCode() == KeyEvent.VK_ENTER && event.getSource() == getCommandPanel().getExecuteCommandComboBoxTextField())
	{
		setCommand((String) getCommandPanel().getExecuteCommandComboBoxTextField().getText().trim());		
		if( isValidSetup())
		{
			enter();
		}
	}
	
	else if( event.getKeyCode() == KeyEvent.VK_ENTER && event.getSource() == getSerialRoutePanel().getSerialTextField())
	{
		serialNumberAction();
	}
	//else if (event.getKeyCode() == KeyEvent.VK_ESCAPE)
	//{
		//getAdvOptsPanel().setVisible(false);
		//getAdvOptsPanel().dispose();
		//getLocateRouteDialog().dispose();
		//getLocateRouteDialog().setVisible(false);
	//}
	
	
}
/**
 * This method was created in VisualAge.
 * @param event java.awt.event.KeyEvent
 */
public void keyReleased(KeyEvent event) {
}
/**
 * This method was created in VisualAge.
 * @param event java.awt.event.KeyEvent
 */
public void keyTyped(KeyEvent event) {
}
/**
 * main entrypoint - starts the part when it is run as an application
 * @param args java.lang.String[]
 */
public static void main(java.lang.String[] args)
{
	try
	{
		System.setProperty("cti.app.name", "Commander");
		javax.swing.UIManager.setLookAndFeel( javax.swing.UIManager.getSystemLookAndFeelClassName());

		YukonCommander ycClient;
		ycClient = new YukonCommander();
		
		com.cannontech.common.gui.util.SplashWindow splash = new com.cannontech.common.gui.util.SplashWindow( ycClient, "ctismall.gif", "Loading resources...", new java.awt.Font("dialog", 0, 14), java.awt.Color.black, java.awt.Color.black, 1 );
		ycClient.setIconImage(java.awt.Toolkit.getDefaultToolkit().getImage("CommanderIcon.gif"));
		ycClient.setTitle(YC_TITLE);
		splash.setDisplayText("Opening connection to database...");

		//Test that a connection can actually be made.
		java.sql.Connection c = null;
		try
		{
			c = com.cannontech.database.PoolManager.getInstance().getConnection(com.cannontech.common.util.CtiUtilities.getDatabaseAlias());

			if (c == null)
			{
				splash.setDisplayText("Error connecting to database, closing");
				Thread.sleep(2000);		//let them see
				System.exit(0);
			}
		}
		catch (Throwable t)
		{
			t.printStackTrace();
			splash.setDisplayText("Error connecting to database, closing");
			Thread.sleep(2000);		//let them see
			System.exit(0);
		}
		finally
		{
			if (c != null)
				c.close();
		}

		ycClient.initialize();
		ycClient.getConnToDispatch();

		//set the app to start as close to the center as you can....
		//  only works with small gui interfaces.
		java.awt.Dimension d = java.awt.Toolkit.getDefaultToolkit().getScreenSize();
		ycClient.setLocation((int)(d.width * .07),(int)(d.height * .07));
		ycClient.show();
				
	}
	catch (Throwable exception)
	{
		System.err.println("Exception occurred in main() of javax.swing.JFrame");
		exception.printStackTrace(System.out);
	}
}
/**
 * Insert the method's description here.
 * Creation date: (9/10/2001 3:17:39 PM)
 */
private void print()
{
	java.awt.Frame parent = com.cannontech.common.util.CtiUtilities.getParentFrame(this);
	java.awt.PrintJob pj = java.awt.Toolkit.getDefaultToolkit().getPrintJob(parent, "YC Output", null);
	java.awt.Graphics printGraphics = pj.getGraphics();
	getDebugOutputTextArea().printAll(printGraphics);
	printGraphics.dispose();
	pj.end();
}
/**
 * Insert the method's description here.
 * Creation date: (9/10/2001 3:10:42 PM)
 */
private void reloadDevices()
{
	java.awt.Frame frame = null;
	java.awt.Cursor savedCursor = null;
	
	try
	{
		frame = com.cannontech.common.util.CtiUtilities.getParentFrame(getRootPane());
		savedCursor = frame.getCursor();
		frame.setCursor( new java.awt.Cursor( java.awt.Cursor.WAIT_CURSOR ) );
		
		//load all data from the database to the cache for update to catch anything new.
		com.cannontech.database.cache.DefaultDatabaseCache.getInstance().loadAllCache();
		
		restoreCurrentTree();
	}
	catch( Exception e )
	{
		e.printStackTrace();
	}
	finally
	{
		frame.setCursor( savedCursor );
	}
}
/**
 * Insert the method's description here.
 * Reselects the selected item in the tree, should the tree be refreshed from cache.
 * Creation date: (9/12/2001 8:59:15 AM)
 */
private void restoreCurrentTree()
{
	com.cannontech.common.gui.util.TreeViewPanel tvp = getTreeViewPanel();
	Object item = getTreeItem();
	
	if (tvp != null)
		tvp.refresh();
		
	if (item instanceof com.cannontech.database.data.lite.LiteBase)
	{
		com.cannontech.database.data.lite.LiteBase lb = (com.cannontech.database.data.lite.LiteBase) item;

		tvp.selectLiteBase( 
				new javax.swing.tree.TreePath ( tvp.getTree().getModel().getRoot() ),
				lb );
				//lb.getLiteType() , lb.getLiteID() );

	}
	else if( item instanceof String)
	{
		tvp.selectByString( item.toString());
	}
	else
	{
		tvp.selectObject(null);
		com.cannontech.clientutils.CTILogger.info("WARNING:  nothing reselected in the tree, dbChangeMessageListener is missing an instanceof");
	}
	// ** Add else if... here to include other objects that may be in the treeViewPanel
}
/**
 * Run simply waits for Return messages to appear on our connection and
 * This should be done without using a separate thread but its not.  rev 2 baby
 * adds them to the output pane.
 */
public void run()
{
	class WriteOutput implements java.lang.Runnable
	{
		public String output = null;
		public WriteOutput(String out)
		{
			output = out;
		}
		
		public void run()
		{
			YukonCommander.this.appendOutputTextArea(output);
			//YukonCommander.this.scrollRectToVisible(  new java.awt.Rectangle(0,getOutputTextArea().getHeight()-1,1,1));
		}
	}
	
	try
	{
		for (;;)
		{
			Object in;
			if (getConnToPorter() != null && getConnToPorter().isValid())
			{
				if ((in = getConnToPorter().read()) != null)
				{
					if (in instanceof com.cannontech.message.porter.message.Return)
					{
						com.cannontech.message.porter.message.Return ret = (com.cannontech.message.porter.message.Return) in;
						//appendOutputTextArea("\n-> Executing command -> " + ret.getCommandString() +"\n");							
						String output = null;

						java.text.SimpleDateFormat format = new java.text.SimpleDateFormat("MMM d HH:mm:ss a z");					
						output = "\n["+ format.format(ret.getTimeStamp()) + "]-{" + ret.getUserMessageID() +"} Return from \'" + ret.getCommandString() + "\'\n";
																				
						for (int i = 0; i < ret.getVector().size(); i++)
						{
							Object o = ret.getVector().elementAt(i);
							if (o instanceof com.cannontech.message.dispatch.message.PointData)
							{
								com.cannontech.message.dispatch.message.PointData pd = (com.cannontech.message.dispatch.message.PointData) o;
								if ( pd.getStr().length() > 0 )
									output += pd.getStr() + "\n";
							}
							else
								output += "Warning, was expecting a com.cannontech.message.dispatch.message.PointData but recieved something else.\n";
						}

						output += "->  " + ret.getResultString() + "\n";		
						javax.swing.SwingUtilities.invokeLater( new WriteOutput(output) );

						synchronized ( YukonCommander.class )
						{
							if( ret.getExpectMore() == 0)	//Only send next message when ret expects nothing more
							{
								
								//Break out of this outer loop.
								doneSendMore:
								if( ycClass.sendMore == 0)
								{
									// command finished
								}
								else if ( ycClass.sendMore > 0)
								{
									ycClass.sendMore--;	//decrement the number of messages to send
									if (ycClass.getLoopType() == ycClass.LOOPLOCATE)
									{
								 		if( ycClass.getAllRoutes()[ycClass.sendMore] instanceof com.cannontech.database.data.lite.LiteYukonPAObject)
										{
											com.cannontech.database.data.lite.LiteYukonPAObject rt = (com.cannontech.database.data.lite.LiteYukonPAObject) ycClass.getAllRoutes()[ycClass.sendMore];
											while( rt.getType() == com.cannontech.database.data.pao.PAOGroups.ROUTE_MACRO
												&& ycClass.sendMore > 0)
											{
												ycClass.sendMore--;
												rt = (com.cannontech.database.data.lite.LiteYukonPAObject) ycClass.getAllRoutes()[ycClass.sendMore];
											}
											// Have to check again because last one may be route_ macro
											if(rt.getType() == com.cannontech.database.data.pao.PAOGroups.ROUTE_MACRO)
												break doneSendMore;

											ycClass.loopReq.setRouteID(rt.getYukonID());
											//ycClass.loopReq.setCommandString(getCommand() + " select route id " + r.getYukonID());
										}
									}
									getConnToPorter().write( ycClass.loopReq);	//do the saved loop request
								}
								else
								{
									output = "Command cancelled\n";
									javax.swing.SwingUtilities.invokeLater( new WriteOutput(output));
								}
							}
						}
//						else
//						{
//							com.cannontech.clientutils.CTILogger.info("** From Porter: " + ret.getResultString() + "\n");
//						}
					}
//					else
					//appendOutputTextArea(" ELSE from Return");	
				}
//				else
//				appendOutputTextArea(" ELSE from connection");			
			}
			else
			{
				Thread.sleep(1000);
			}
		}
	}
	catch (InterruptedException e)
	{
		e.printStackTrace();
	}
}
/**
 * Insert the method's description here.
 * Creation date: (9/10/2001 3:19:50 PM)
 */
private void save()
{
	//This will need to be updated someday for a new version of swing
	java.awt.Frame parent = com.cannontech.common.util.CtiUtilities.getParentFrame(this);
	javax.swing.JFileChooser  fileChooser = new javax.swing.JFileChooser();
	
	if( fileChooser.showSaveDialog(parent) == javax.swing.JFileChooser.APPROVE_OPTION )
	{
		java.io.FileWriter fWriter = null;
		try
		{
			fWriter = new java.io.FileWriter( fileChooser.getSelectedFile().getPath(), true );
			java.io.PrintWriter pWriter = new java.io.PrintWriter( fWriter );

			pWriter.print( getDebugOutputTextArea().getText() );
			fWriter.close();
		}
		catch( java.io.IOException e )
		{				
			javax.swing.JOptionPane.showMessageDialog( parent, "An error occured saving to a file", "Error", javax.swing.JOptionPane.ERROR_MESSAGE );
		}
		finally
		{
			try
			{
				if( fWriter != null )
					fWriter.close();
			} 
			catch( java.io.IOException e2 )
			{
				e2.printStackTrace();
			}
		}

	}	 	
}
/**
 * Insert the method's description here.
 * Creation date: (9/10/2001 3:12:12 PM)
 */
private void serialNumberAction()
{
	com.cannontech.common.gui.util.TreeViewPanel t =  getTreeViewPanel();
	String tempSerialNumber = (String) getSerialRoutePanel().getSerialTextField().getText().trim();

	// If invalid serialNumber entered ( null or length 0), set selected tree item to null.
	if(	tempSerialNumber == null || tempSerialNumber.length() == 0 )
	{	
		setSerialNumber(null);
		t.getTree().getSelectionModel().setSelectionPath( null );
		return;
	}
	// Else if not already in serial vector, add it to the serialVector.
	else if(!com.cannontech.database.model.EditableLCRSerialModel.getSerialNumberVector().contains(tempSerialNumber) )
	{	
		com.cannontech.database.model.EditableLCRSerialModel.getSerialNumberVector().add(tempSerialNumber);

		// Refresh the tree selection.
		((com.cannontech.database.model.EditableLCRSerialModel) t.getSelectedTreeModel()).update();

		// Clear serial number text field.
		if( getSerialRoutePanel().getSerialTextField() instanceof javax.swing.JTextField )
			getSerialRoutePanel().getSerialTextField().setText("");

	}

	setSerialNumber( tempSerialNumber );

	updateCurrentSelection( getSerialNumber() );
}
/**
 * Insert the method's description here.
 * Creation date: (9/11/2001 12:07:43 PM)
 * @param newCommand java.lang.String
 */
private void setCommand(String newCommand)
{
	ycClass.setCommand(newCommand);
}
/**
 * Insert the method's description here.
 * Creation date: (2/26/2002 2:12:46 PM)
 * @param typeSelected int
 */
private void setModelType(int typeSelected)
{
	ycClass.setModelType( typeSelected);
}
/**
 * Insert the method's description here.
 * Creation date: (2/26/2002 4:36:23 PM)
 * @param newRoute java.lang.Object
 */
private void setRoute(Object newRoute)
{
	ycClass.setRoute(newRoute);
}
/**
 * Creation date: (3/9/2001 4:30:36 PM)
 * @author Stacey Nebben	Added for version 1.5
 * -----------------------------------------------------------------------------------------
 * Fills the routeComboBox with all of the created routes.
 * Retrieves the routes from cache memory and then sorts them (alphabetically).
 * Removes all items in the combo box (applicable when new routes exist)
 * Add a default of All Routes as the first item in the combo box
 * Add the database routes to the combo box.
 */
private void setRouteModel()
{
	if( getSerialRoutePanel().getRouteComboBox() == null )
		return; 

	int index = 0;	//Index in the routeComboBox to be selected
	
	// Save the routeComboBox's currently selected item.
	Object saveSelectedRoute = getSerialRoutePanel().getRouteComboBox().getSelectedItem();
	
	// Get an instance of the cache.
	com.cannontech.database.cache.DefaultDatabaseCache cache =
				com.cannontech.database.cache.DefaultDatabaseCache.getInstance();

	synchronized(cache)
	{
		java.util.List routes = cache.getAllRoutes();
		java.util.Collections.sort( routes, com.cannontech.database.data.lite.LiteComparators.liteStringComparator );

		getSerialRoutePanel().getRouteComboBox().removeAllItems();	
		getSerialRoutePanel().getRouteComboBox().addItem("All Default Routes");	//default first item

		Object[] tempRouteArray = new Object[routes.size()];

		for( int i = 0; i < routes.size(); i++ )
		{
			getSerialRoutePanel().getRouteComboBox().addItem( routes.get(i));
			tempRouteArray[i] = routes.get(i);
			
			if(saveSelectedRoute != null && routes.get(i).toString() == saveSelectedRoute.toString())
				index = i + 1;
		}
		
		ycClass.setAllRoutes(tempRouteArray);
		
	}

	// Set the newly selected item to the index of the previously selected item.
	//  If the prev item doesn't exist, set selected item to the 0 index (default).
	getSerialRoutePanel().getRouteComboBox().setSelectedIndex( index );
		
}
/**
 * Insert the method's description here.
 * Creation date: (9/10/2001 4:17:54 PM)
 * @param newSerialNumber java.lang.String
 */
private void setSerialNumber(String newSerialNumber)
{
	ycClass.setSerialNumber(newSerialNumber);
}
/**
 * Insert the method's description here.
 * Creation date: (9/10/2001 2:21:41 PM)
 * @param selectedTreeItem java.lang.Object
 */
private void setTreeItem(Object newSelectedItem)
{
	ycClass.setTreeItem(newSelectedItem);
}
/**
 * Insert the method's description here.
 * Creation date: (9/10/2001 3:08:57 PM)
 */
private void sortByComboBoxAction()
{
	////////////////////////////////////////////////////////////////////////////////////////////
	// Serial Number/Route -- enable/disable (with Versacom Serial tree)
	setModelType( treeModels[getTreeViewPanel().getSortByComboBox().getSelectedIndex()] );
	
	if (getModelType() == ModelFactory.EDITABLELCRSERIAL)
	{
		enableSerialAndRoute(true);
		getSerialRoutePanel().getSerialTextField().requestFocus();
	}
	else
	{
		enableSerialAndRoute(false);
	}
	////////////////////////////////////////////////////////////////////////////////////////////
	// Locate Route Menu Item -- enable
	getYCCommandMenu().locateRoute.setEnabled(false);	//init to false, will change below if valid state.
	if (getModelType() == ModelFactory.DEVICE)
	{
		if( getTreeItem() instanceof com.cannontech.database.data.lite.LiteYukonPAObject)
		{
			com.cannontech.database.data.lite.LiteYukonPAObject lpao = (com.cannontech.database.data.lite.LiteYukonPAObject) getTreeItem();
			if( com.cannontech.database.data.device.DeviceTypesFuncs.isMCT(lpao.getType()) ||
				com.cannontech.database.data.device.DeviceTypesFuncs.isRepeater(lpao.getType()))
			{
				getYCCommandMenu().locateRoute.setEnabled(true);
			}
		}
	}
	////////////////////////////////////////////////////////////////////////////////////////////	
	if (getTreeItem() == null )
	{
		getCommandPanel().getAvailableCommandsComboBox().removeAllItems();
		getCommandPanel().getExecuteCommandComboBoxTextField().setText("");
	}

}
/**
 * Creation date: (3/15/2001 2:28:56 PM)
 * @author Stacey Nebben	Added for version 1.5
 * -----------------------------------------------------------------------------------------
 * Updates the current tree selection to the parameter passed in.
 * Used for synchronization.
 */
private void updateCurrentSelection(Object currentSelection)
{
	com.cannontech.common.gui.util.TreeViewPanel t =  getTreeViewPanel();
		
	javax.swing.tree.TreePath selectedPath = 
				com.cannontech.common.util.CtiUtilities.getTreePath( t.getTree(), currentSelection);
	
	t.getTree().getSelectionModel().setSelectionPath( selectedPath );

}
/**
 * -----------------------------------------------------------------------------------------
 * Method for treeSelectionEvents.
 * If nothing is selected in the tree, we return (do nothing).
 * Gets the appropriate configFile for the object type selected in the treeViewPanel tree.
 * Parses the keys and Values in the configFile and fills the CommandComboBox accordingly.
 * Only updates the command boxes when the selected tree item differs in object type from
 *  the previously selected tree item.
 */
public void valueChanged(TreeSelectionEvent event)
{

	com.cannontech.common.util.KeysAndValuesFile kavFile = null;
	setModelType( treeModels[getTreeViewPanel().getSortByComboBox().getSelectedIndex()] );
	setTreeItem( getTreeViewPanel().getSelectedItem());

	if (getTreeItem() == null)
		return;

	// If the parent value of the tree is invalid, return.
	if (getTreeViewPanel().getSelectedNode().getParent() == null)
		return;

	// If serial number, use the serial number file string constant.
	if ( getModelType() == ModelFactory.EDITABLELCRSERIAL)
	{
		setSerialNumber( (String) getTreeItem());
		getSerialRoutePanel().setSerialNumberText( getSerialNumber().toString() );
		kavFile = ycClass.getConfigFile( ycClass.SERIALNUMBER_FILENAME );
		if (!kavFile.exists())
		{
			//This is only temporary until all files have been changed from ALT_SERIALNUMBER_FILENAME to SERIALNUMBER_FILENAME.
			kavFile = ycClass.getConfigFile(ycClass.ALT_SERIALNUMBER_FILENAME);
		}
	}
	else if( getModelType() == ModelFactory.COLLECTIONGROUP ||
			getModelType() == ModelFactory.TESTCOLLECTIONGROUP )
	{
		kavFile = ycClass.getConfigFile( ycClass.COLLECTION_GROUP_FILENAME );
	}
	// Else if a lite object, using the device type selected. 
	else if ( getTreeItem() instanceof com.cannontech.database.data.lite.LiteBase)
	{
		com.cannontech.database.db.DBPersistent dbp = com.cannontech.database.data.lite.LiteFactory.createDBPersistent( (com.cannontech.database.data.lite.LiteBase) getTreeItem());
				
		if (dbp == null)
			return;

		kavFile = ycClass.getConfigFile( dbp );

		////////////////////////////////////////////////////////////////////////////////////////////
		// Locate Route Menu Item -- enable
		getYCCommandMenu().locateRoute.setEnabled(false);	//init to false, will change below if valid state.
		if( getTreeItem() instanceof com.cannontech.database.data.lite.LiteYukonPAObject)
		{
			com.cannontech.database.data.lite.LiteYukonPAObject lpao = (com.cannontech.database.data.lite.LiteYukonPAObject) getTreeItem();
			if( com.cannontech.database.data.device.DeviceTypesFuncs.isMCT(lpao.getType()) ||
				com.cannontech.database.data.device.DeviceTypesFuncs.isRepeater(lpao.getType()))
			{
				getYCCommandMenu().locateRoute.setEnabled(true);
			}
		}
	
	}
	else //use the "default" filename
	{
		kavFile = ycClass.getConfigFile( ycClass.DEFAULT_FILENAME);
	}

	if (!kavFile.exists())
	{
		getCommandLogPanel().addLogElement(" *** The command file: " + kavFile+ "     Does not exist. ***");
		getCommandPanel().getAvailableCommandsComboBox().removeAllItems();
		getCommandPanel().getExecuteCommandComboBox().setSelectedItem(""); //clear text field
		return;
	}

	com.cannontech.clientutils.CTILogger.info(" COMMAND FILE: " + kavFile);
	
	// Only update command boxes on a change in device type/ file name.
	if( !kavFile.toString().equalsIgnoreCase( ycClass.getCommandFile().toString() ) )
	{
		ycClass.setCommandFile( kavFile.toString() );
		
		// Collect the config file keys and values (a.k.a. Commands and CommandStrings).		
		com.cannontech.common.util.KeysAndValues keysAndValues = kavFile.getKeysAndValues();

		// Clear out the old, get ready for the new!
		getCommandPanel().getAvailableCommandsComboBox().removeAllItems();
		getCommandPanel().getExecuteCommandComboBox().setSelectedItem("");
			
		// Add the keys to the availableCommandsComboBox, first one is default ("Select A Command")
		getCommandPanel().getAvailableCommandsComboBox().addItem(" <Select A Command>" );
		for (int i = 0; i < keysAndValues.getKeys().length; i++)
			getCommandPanel().getAvailableCommandsComboBox().addItem(keysAndValues.getKeys()[i]);
	}
	return;
}
}
