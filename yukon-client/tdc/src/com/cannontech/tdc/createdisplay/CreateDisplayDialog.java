package com.cannontech.tdc.createdisplay;

/**
 * Insert the type's description here.
 * Creation date: (1/24/00 3:46:59 PM)
 * @author: 
 */
import java.awt.Cursor;

import com.cannontech.tdc.TDCMainFrame;
import com.cannontech.tdc.logbox.MessageBoxFrame;
import com.cannontech.tdc.utils.DataBaseInteraction;
import com.cannontech.tdc.utils.TDCDefines;
 
public class CreateDisplayDialog extends javax.swing.JDialog {
	private long currentDisplayNumber = com.cannontech.tdc.data.Display.UNKNOWN_DISPLAY_NUMBER;
	private javax.swing.JPanel ivjJDialogContentPane = null;
	private CreateTopPanel ivjTopPanel = null;
	private javax.swing.JOptionPane warningMsg = null;
	IvjEventHandler ivjEventHandler = new IvjEventHandler();
	private com.cannontech.tdc.addpoints.AddPointsCenterPanel ivjEditDataSetPanel = null;
	private TemplatePanel ivjTemplatePanel = null;
	private javax.swing.JPanel ivjJPanelBottomPanelHolder = null;
	private javax.swing.JPanel ivjJPanelTemplateHolder = null;
	private com.cannontech.common.gui.util.OkCancelPanel ivjOkCancelPanel = null;

class IvjEventHandler implements com.cannontech.common.gui.util.OkCancelPanelListener {
		public void JButtonCancelAction_actionPerformed(java.util.EventObject newEvent) {
			if (newEvent.getSource() == CreateDisplayDialog.this.getOkCancelPanel()) 
				connEtoC3(newEvent);
		};
		public void JButtonOkAction_actionPerformed(java.util.EventObject newEvent) {
			if (newEvent.getSource() == CreateDisplayDialog.this.getOkCancelPanel()) 
				connEtoC4(newEvent);
		};
	};

/**
 * CreateDisplayDialog constructor comment.
 * @param owner java.awt.Frame
 */
public CreateDisplayDialog(java.awt.Frame owner) 
{
	super(owner);
	initialize();
}
/**
 * connEtoC3:  (OkCancelPanel.okCancelPanel.JButtonCancelAction_actionPerformed(java.util.EventObject) --> CreateDisplayDialog.okCancelPanel_JButtonCancelAction_actionPerformed(Ljava.util.EventObject;)V)
 * @param arg1 java.util.EventObject
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC3(java.util.EventObject arg1) {
	try {
		// user code begin {1}
		// user code end
		this.okCancelPanel_JButtonCancelAction_actionPerformed(arg1);
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}
/**
 * connEtoC4:  (OkCancelPanel.okCancelPanel.JButtonOkAction_actionPerformed(java.util.EventObject) --> CreateDisplayDialog.okCancelPanel_JButtonOkAction_actionPerformed(Ljava.util.EventObject;)V)
 * @param arg1 java.util.EventObject
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC4(java.util.EventObject arg1) {
	try {
		// user code begin {1}
		// user code end
		this.okCancelPanel_JButtonOkAction_actionPerformed(arg1);
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}
/**
 * Insert the method's description here.
 * Creation date: (3/22/00 9:28:51 AM)
 * @return int
 */
public long getCurrentDisplayNumber() 
{
	return currentDisplayNumber;
}
/**
 * Insert the method's description here.
 * Creation date: (2/1/00 4:30:32 PM)
 * @return java.lang.String
 */
public String getDisplayName() 
{
	return getTopPanel().getName();
}
/**
 * Return the EditDataSetPanel property value.
 * @return com.cannontech.tdc.addpoints.AddPointsCenterPanel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private com.cannontech.tdc.addpoints.AddPointsCenterPanel getEditDataSetPanel() {
	if (ivjEditDataSetPanel == null) {
		try {
			ivjEditDataSetPanel = new com.cannontech.tdc.addpoints.AddPointsCenterPanel();
			ivjEditDataSetPanel.setName("EditDataSetPanel");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjEditDataSetPanel;
}

/**
 * Removes any resources used by this Dialog
 */
public void dispose()
{
	getEditDataSetPanel().dispose();
	super.dispose();
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

			java.awt.GridBagConstraints constraintsTopPanel = new java.awt.GridBagConstraints();
			constraintsTopPanel.gridx = 1; constraintsTopPanel.gridy = 1;
			constraintsTopPanel.fill = java.awt.GridBagConstraints.BOTH;
			constraintsTopPanel.anchor = java.awt.GridBagConstraints.WEST;
			getJDialogContentPane().add(getTopPanel(), constraintsTopPanel);

			java.awt.GridBagConstraints constraintsJPanelTemplateHolder = new java.awt.GridBagConstraints();
			constraintsJPanelTemplateHolder.gridx = 1; constraintsJPanelTemplateHolder.gridy = 2;
			constraintsJPanelTemplateHolder.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintsJPanelTemplateHolder.anchor = java.awt.GridBagConstraints.WEST;
			getJDialogContentPane().add(getJPanelTemplateHolder(), constraintsJPanelTemplateHolder);

			java.awt.GridBagConstraints constraintsJPanelBottomPanelHolder = new java.awt.GridBagConstraints();
			constraintsJPanelBottomPanelHolder.gridx = 1; constraintsJPanelBottomPanelHolder.gridy = 3;
			constraintsJPanelBottomPanelHolder.fill = java.awt.GridBagConstraints.BOTH;
			constraintsJPanelBottomPanelHolder.anchor = java.awt.GridBagConstraints.WEST;
			constraintsJPanelBottomPanelHolder.weightx = 1.0;
			constraintsJPanelBottomPanelHolder.weighty = 1.0;
			constraintsJPanelBottomPanelHolder.insets = new java.awt.Insets(0, 0, 1, 0);
			getJDialogContentPane().add(getJPanelBottomPanelHolder(), constraintsJPanelBottomPanelHolder);
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
 * Return the JPanelBottom property value.
 * @return javax.swing.JPanel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JPanel getJPanelBottomPanelHolder() {
	if (ivjJPanelBottomPanelHolder == null) {
		try {
			com.cannontech.common.gui.util.TitleBorder ivjLocalBorder1;
			ivjLocalBorder1 = new com.cannontech.common.gui.util.TitleBorder();
			ivjLocalBorder1.setTitleFont(new java.awt.Font("dialog.bold", 1, 14));
			ivjLocalBorder1.setTitle("Data Set");
			ivjJPanelBottomPanelHolder = new javax.swing.JPanel();
			ivjJPanelBottomPanelHolder.setName("JPanelBottomPanelHolder");
			ivjJPanelBottomPanelHolder.setPreferredSize(new java.awt.Dimension(1071, 300));
			ivjJPanelBottomPanelHolder.setBorder(ivjLocalBorder1);
			ivjJPanelBottomPanelHolder.setLayout(new java.awt.GridBagLayout());

			java.awt.GridBagConstraints constraintsEditDataSetPanel = new java.awt.GridBagConstraints();
			constraintsEditDataSetPanel.gridx = 1; constraintsEditDataSetPanel.gridy = 1;
			constraintsEditDataSetPanel.fill = java.awt.GridBagConstraints.BOTH;
			constraintsEditDataSetPanel.anchor = java.awt.GridBagConstraints.WEST;
			constraintsEditDataSetPanel.weightx = 1.0;
			constraintsEditDataSetPanel.weighty = 1.0;
			constraintsEditDataSetPanel.insets = new java.awt.Insets(0, 5, 0, 5);
			getJPanelBottomPanelHolder().add(getEditDataSetPanel(), constraintsEditDataSetPanel);

			java.awt.GridBagConstraints constraintsOkCancelPanel = new java.awt.GridBagConstraints();
			constraintsOkCancelPanel.gridx = 1; constraintsOkCancelPanel.gridy = 2;
			constraintsOkCancelPanel.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintsOkCancelPanel.anchor = java.awt.GridBagConstraints.WEST;
			constraintsOkCancelPanel.weightx = 0.0;
			constraintsOkCancelPanel.weighty = 0.0;
			constraintsOkCancelPanel.insets = new java.awt.Insets(0, 5, 5, 5);
			getJPanelBottomPanelHolder().add(getOkCancelPanel(), constraintsOkCancelPanel);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJPanelBottomPanelHolder;
}
/**
 * Return the JPanelTemplateHolder property value.
 * @return javax.swing.JPanel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JPanel getJPanelTemplateHolder() {
	if (ivjJPanelTemplateHolder == null) {
		try {
			ivjJPanelTemplateHolder = new javax.swing.JPanel();
			ivjJPanelTemplateHolder.setName("JPanelTemplateHolder");
			ivjJPanelTemplateHolder.setLayout(new java.awt.BorderLayout());
			getJPanelTemplateHolder().add(getTemplatePanel(), "Center");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJPanelTemplateHolder;
}
/**
 * Return the OkCancelPanel property value.
 * @return com.cannontech.tdc.utils.OkCancelPanel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private com.cannontech.common.gui.util.OkCancelPanel getOkCancelPanel() {
	if (ivjOkCancelPanel == null) {
		try {
			ivjOkCancelPanel = new com.cannontech.common.gui.util.OkCancelPanel();
			ivjOkCancelPanel.setName("OkCancelPanel");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjOkCancelPanel;
}
/**
 * Return the TemplatePanel property value.
 * @return com.cannontech.tdc.createdisplay.TemplatePanel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private TemplatePanel getTemplatePanel() {
	if (ivjTemplatePanel == null) {
		try {
			ivjTemplatePanel = new com.cannontech.tdc.createdisplay.TemplatePanel();
			ivjTemplatePanel.setName("TemplatePanel");
			// user code begin {1}

			ivjTemplatePanel.setCurrentDisplayNumber( currentDisplayNumber );
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjTemplatePanel;
}
/**
 * Return the TopPanel property value.
 * @return com.cannontech.tdc.createdisplay.CreateTopPanel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private CreateTopPanel getTopPanel() {
	if (ivjTopPanel == null) {
		try {
			com.cannontech.common.gui.util.TitleBorder ivjLocalBorder;
			ivjLocalBorder = new com.cannontech.common.gui.util.TitleBorder();
			ivjLocalBorder.setTitleFont(new java.awt.Font("dialog.bold", 1, 14));
			ivjLocalBorder.setTitle("Display");
			ivjTopPanel = new com.cannontech.tdc.createdisplay.CreateTopPanel();
			ivjTopPanel.setName("TopPanel");
			ivjTopPanel.setBorder(ivjLocalBorder);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjTopPanel;
}
/**
 * Called whenever the part throws an exception.
 * @param exception java.lang.Throwable
 */
private void handleException(java.lang.Throwable exception) 
{
	/* Uncomment the following lines to print uncaught exceptions to stdout */
	com.cannontech.clientutils.CTILogger.info("--------- UNCAUGHT EXCEPTION CreateDisplayDialog() ---------");
	com.cannontech.clientutils.CTILogger.error( exception.getMessage(), exception );;

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
	getOkCancelPanel().addOkCancelPanelListener(ivjEventHandler);
}
/**
 * Initialize the class.
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void initialize() {
	try {
		// user code begin {1}
		// user code end
		setName("CreateDisplayDialog");
		setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
		setSize(629, 650);
		setTitle("Create Display");
		setContentPane(getJDialogContentPane());
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
private void insertCreatedColumns()
{

	for ( int i = 0; i < getTemplatePanel().getJTableColumnCount(); i++ )
	{			
		String query = new String
			("insert into displaycolumns (displaynum, title, typenum, ordering, width) values (?, ?, ?, ?, ?)");
		Object[] objs = new Object[5];
		objs[0] = new Long(currentDisplayNumber);
		objs[1] = getTemplatePanel().columnFieldData( i, ColumnData.COLUMN_TITLE );
		objs[2] = getTemplatePanel().columnFieldData( i, ColumnData.COLUMN_TYPE_NUMBER );
		objs[3] = getTemplatePanel().columnFieldData( i, ColumnData.COLUMN_NUMBER );
		objs[4] = getTemplatePanel().columnFieldData( i, ColumnData.COLUMN_WIDTH );
		DataBaseInteraction.updateDataBase( query, objs );
	}
			
//			TDCMainFrame.messageLog.addMessage("Display " + displayName + " added to the display tables", MessageBoxFrame.INFORMATION_MSG );
	this.setVisible( false );
	
	return;
}
/**
 * Insert the method's description here.
 * Creation date: (4/3/00 2:10:23 PM)
 * Version: <version>
 */
private void insertDataSet() 
{
	// Lets delete the current display Numbers points just in case
	// ordering was the only thing that got changed
	String query = new String
		("delete from display2waydata where DisplayNum = ?");
	Object[] objs = new Object[1];
	objs[0] = new Long(currentDisplayNumber);
	DataBaseInteraction.updateDataBase( query, objs );
			
	for ( int i = 0; i < getEditDataSetPanel().getRightTablePointCount(); i++ )
	{

		query = new String
			("insert into display2waydata (displaynum, ordering, pointid) values (?, ?, ?)");
		Object[] objs2 = new Object[3];
		objs2[0] = new Long(currentDisplayNumber);
		objs2[1] = String.valueOf(i + 1);
		objs2[2] = new Long(getEditDataSetPanel().getRightTablePointID( i ));
		DataBaseInteraction.updateDataBase( query, objs2 );
	}

	TDCMainFrame.messageLog.addMessage("New data set for " + getTopPanel().getName() + " has been created", MessageBoxFrame.INFORMATION_MSG );
	this.setVisible( false );
	
	return;
}

/**
 * Comment
 */
public void okCancelPanel_JButtonCancelAction_actionPerformed(java.util.EventObject newEvent) 
{
	removeCache();
	this.dispose();
	return;
}
/**
 * Comment
 */
public void okCancelPanel_JButtonOkAction_actionPerformed(java.util.EventObject newEvent) 
{
	String displayName = new String( getTopPanel().getName() );
	String displayType = new String( getTopPanel().getType().toString() );
	String displayTitle = new String( getTopPanel().getTitle() );
	String displayDescr = new String( getTopPanel().getDescription() );
	
		
	if ( !displayName.equalsIgnoreCase("") )
	{

		Cursor original = getCursor();
		setCursor( new java.awt.Cursor( java.awt.Cursor.WAIT_CURSOR ) );

		try
		{
			// set this up for our new row number		
			currentDisplayNumber = TDCDefines.createValidDisplayNumber();

			String query = new String
				("insert into display (displaynum, name, type, title, description) values (?, ?, ?, ?, ?)");
			Object[] objs = new Object[5];
			objs[0] = new Long(currentDisplayNumber);
			objs[1] = displayName;
			objs[2] = displayType;
			objs[3] = displayTitle;
			objs[4] = displayDescr;
			DataBaseInteraction.updateDataBase( query, objs );
			
			insertCreatedColumns();

			insertDataSet();
				
			TDCMainFrame.messageLog.addMessage("Display " + displayName + " added to the display tables", MessageBoxFrame.INFORMATION_MSG );

			removeCache();			
			this.setVisible( false );
		}
		finally
		{
			setCursor( original );			
		}		
		
	}
	else
		warningMsg.showMessageDialog(this, 
			"Make sure all fields are completely filled in and you have created at least 1 column", "Message Box", warningMsg.WARNING_MESSAGE);

	return;
}
/**
 * Insert the method's description here.
 * Creation date: (5/2/00 10:28:56 AM)
 * Version: <version>
 */
private void removeCache() 
{
	// do this for now, change lager !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
	com.cannontech.database.cache.DefaultDatabaseCache.getInstance().releaseAllCache();	
}
/**
 * 
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private static void getBuilderData() {
/*V1.1
**start of data**
	D0CB838494G88G88GC9F954ACGGGGGGGGGGGG8CGGGE2F5E9ECE4E5F2A0E4E1F4E13DBC8BD4D4D712182071C37C449FD1927117B593E6634E309BB3E1F78C476C1844B91319180D493833E463E61CC4F3589932E30C0997E8BEAAA8888AC414D6B4E222FC9496E85048C785C5C4E48D4960C8C3BF2065512F6DFE88A8512D3A775D570F269B0411F8D23CF76B7E2A6AD6552DDB77F5C8E21AEFC4E8D8A2A12191C479179FCAC862D9C2C2022667F01C079098C9B47F76834413BDC5BAF8CE06
	3A009F85C646115B7151D0DEG65D58B83A31360BD01AC7DA263B43C381907C05D363F16278CBC4F2CE4BECF94AD3FFABE861E5B8196818E1FA1824E7F6E79E4853FC241F3888DA3E4F58B36796CFC2A02AB00F28D00B700DF8BEB3F8C1EDB684C238D85CA3B06A93EA4641543E131B80EE6CC8E87DBD95F8A6529A74F06863762DC07AF44A8FDBC203C84G198FA77FF54AG4F621A7656D34DD2C7E63351E211AC32505C16A91B1AB31BED02D196CCE63BD5B476988CE534D84E0A65E623A83592A23661785205
	E8BA9EA139D05EC0F19E05B867E2F8DF8E9024624F9990DFEC8E0C2C83904D1C4F13070B95BE6A6FBCCB7C5F34046FF0432727197167E7F670B92DF3E3D48ADD1337D3D076EB21AE66C6E0249E209C4084608930076A42BB49DF423324C9AE13BABAA4CB0955DAAA1AEC97A513A0423B41GB58A2ED6E816ECA6C258FA9F3BF05485BD83816BD7B464FECCA60BCE7035772BFD0A2C7E690530A897B2D93DA8BB4C678613057F8D07CC585C325B39D75E682B1BFD677E06E867DEB0C68A0FF0B1377F9BC74256AA63
	EC524CFD349D57BA046AC0AA3C07AA7A41701FD13CD503E7FABA0662316C81F5EB5A39EE54EF6734D469E611381CAA17F2708EAC892B90998D0D22B3ADAFD03D7392591C91AAAD7FCA713E9ABC2365A245E3599F6A12BB82A319FE3D3B876D36974A5EG0B8116832C8550D9201D056B5817A9D97729E3B5C61BDC5AE996CD020D303E6F266C062750AD5B0C15963BEC34B48BD70DB63331C9946CC4E933AE85E55054275FFFD35DEF06B94E899641E6144D1285C6B7F68832E03333B69BD362318D9AB3545619BB
	84182043CA7838E11FA740D3B45A657F331A40609DADD87D564FF1CDEE992F4208B0G3CB339446DC5F995G7FB5G0D2A9CC65371AE88B60BA09A8CD512545EE92DC3CE48E28B4AF982759DB23CFBD9380CE376A1AE844AB1929FA760731D1AF14A4D4DC83E515643EDECE3A17AE63D44F56643FA2EB365318B4951B74E3B54990453925315FA4959F6F4869C9369E2E52F42FACDBF3D645AFE068BEC0E05FD6688FF0063EFD7576AF69A2E1587506F8390204A784F1F6B194E169A6D825752CBB6D0B0416410B8
	2373C5C334E29FD1AA6DABA8EEA50C37C962B23BE25072435BBFD8190986E719C5BC40B918EC566F417DC838A5D864F241AAD8CC70E2D76AC276204EA9765AA229FDB6D23C956C3B8C8C4A2E62C9AA5A20D116ED6626CED938A2483259526A280FCE45396C1DCDD607EB60F36DC8C55E24AE30EF35FDE2AA5AC633E43188F41154AECA7DEEEA1B02A8F4G51E5D227C5E678543D285F6DC20F629B74FBB16839E594BB514F3098E131F19B72E0B3F74052232BA25DD6345BF99712B5B2BA88B80CF52A6059E865B6
	58154EED70606D39E46975C3E12E76906F0565E1CB2D4C16D6DA9DB648E80A5F671A76A46AA3943C1D515AFB8AFD0D2BC005697DCF75B8D6884C91FD93E2031BDC6F276E43FA6EDB9405AF13C41109D31533C7DA9C2E0BF033D3G170CCE6C02AA3BC752F6BA55DDD564486AA725219CC50D7F535B68DEE8D063C0202786C03E49E4397C26A28B5A6E97C53C1DE8E36DBCED2C5DCA9BEB47E4D0FB93F921B15D8D63ADE3F7263D8BF44BE0B84FC3B6A5E60B290AD66CA7434EE333DB55A81FC6A8F783CC3673F57C
	F5AC0F4D26357B1220791F3B0DC1975999ADAB6C0EF554BF4CE333CF77631CB1F2E0E42A4CE53FD52F5D9FCA6CF62959CC37A6C5EE4F6B356B4CF07F225F8A4F96334DAED702A1F6F399AF53A33D38F7A9E8E5AFBFDC447772EC18378D006970C73B34BA6C8B78D5G1BG62BA83A3436918930BAA505E5A4BF09796EB0CC0E6667952CED916AC8CD5C2F5643A11BEEA84DB0BE46B90CC99A87C4CCE59ACE61EC11FF1296986A8539136C63A7D971C473CFEBFE3D6399C346848D5A5E61B17F0DA61E5455F4FA245
	779242DC4550E3F76C8D4B68E46BD35369D8275E1FC4062F7B6BA7D1CCE6412A4CFC204DCAD59E5F94173B13472576E148A26B4BF36EE461F6BC77F2E07BC1FC95463CB137DC6F4FFAA7FCAE14D9AC78BE557F0ADB4E71EA8845575FF20ED7FB2BF93C5A86F5ABEE717845AB9D75AA3B8B7CA1008920974087A0200B6BFD557E93E4107830924EE2B60BD13C52E7A360F15A01F0663B15F26401886E3B09FADEA9F20E7DD7F57565BD44292C1CD77A755B67D4CE6E6E5B0F2D655C6E23447DF992574B8E6ED53B54
	4C8EF726E86DB09B462E8368867082E07E6A58EEFE46DC504D7D55AF8BD07E2B219C82905343657F7EC174158721FC9A209EC08670867087D8554357E0720113032D81552FBA298CF095603B4B1C77D8D5E67FB3CCD96B0A871375061E3E7DD85C1CC17DF75C6D4048DCG063BC7F195D0EED4F11FDDC41CA7143DEF7318AC389A777D85EAF9A3AD234CD979216AFAA79A7ED640397A72BD7B014F210D15597ACECFF79F3379243B0E5966253B0E59962547B3BC0C21BD31717A45698347F4CB52DD45F4FBA95DAF
	6F5EC147365B85BBD538F223ECD46AFEAF6FA2AEE23BEAB854A9BCA790D35196254D99D9AABB215A631F0EA49317310046763E3A64ECFBF85ED9866B356AB6373D73A928C3B8C770EDEEFB87CE226DE55C819B83283F43F567786E01F29D56BB7DFDB27331A5746C6FF9077958D095FF1E62A7EB704C773EDE40F3DABED0C7FA39CD079CF43F661F9CF43D662107B01E31CB1D36E641F9DD238FA52BF58E6B2F14058EDC82A5C6257DE5232DD56827974934BF2BF357FF7FA1F73A435677D831F6FDF3D57C57D528
	9BA6214558A9023ACAF66112D5B97623DCFAD1CE5FD571F31A2917677D7E218BE351961B31832639E2951A097CBD361DD145F5A1885A06GA482BE97206EFB3E7F6F2A46B1E433AC227E85FC0FF9D29DAE39C253EF12B08ED6224DB2F47FE8AF9601471D4D17B8BD64AE57251517E96EG4AB5GEDF739AEDDCA613A648138E9G0B6EBA74A7602EBBDD1AC1F573353B4E391E39943F662EF32EA763A40FF5B6C0DD43BD6EBBABF7F3FF3A749E2769DCA1124E5EEBB46FEF95BCE8FF56FF2F7863084E41566FA60D
	1DB46EA2739B3345A4F541D2E2A6CA36E716D7DE2939F4654C75CB55574B2A6005451E3EDD5ABCF54156FC17E7BD04B88F36EE8715E740A3690AEE84D0DD45BE8DD0B74D83F19597931DE85C8583E74213555F6ED39F2F581D235E2FCFBD527A01CEDB7F3BBE75C8F309521F71756AA1FE7659746AA2A27EE3215B334FEA059FB2CA092D2F26130363BF09E2637C6AAFDA79444D5A9F966AE61C8225FF79A867FCD45BA5E75F5ABD4AE183DE235C59C3C5AD4DF10EE2FA9F25626BA8FE09864F6C61EDEA0FD8FE8D
	6AF29F627650C27781EFA87B2B389DF908DB8E6555AAAEDE07B872B0780F07B96EEE843725020739AD791CC45CD8A8AFD0F1FFC7F1CB9FE6E7DC064BBCB9D43D67E35E3FED1D9B729CC349B3A5FB228C9AF7175F47F8618A2C817AB7G24AB7898986339F026B3AC4E75BC0632EA84FC2FA75F7FAA0FFE854F2B469BCAB4FB46E416A59B8DE781D9E313E00710FB9816E6A8947E53AE8E6F0E6245A8EE9F7EBF1BC7160C79B8F41B9B3D7671D46CDDF92E2DCB27F8464B071938772C033A6851F09E83A887288730
	0ED6637523C8A3C665C0FE09AC9B1B5BE8D600876BD74DC217830D9B9B9BBBBE9BA9E5E37C5B51C36E6483A31C873F47CE13F5C64C15A8F1B55D33BC018E6F51DC1F67DD667A9CB01A6BE9B53527A0A807GA40E61786FA83EG4A8DE3D80E016103AE8F571F639C7CBDC4730EF376556B0F07BD07B61E566AF914D31B84322196FD2255A63488B61BE03AE23E0D31810BBFA6D11F6181F4F90D61B14497F918E7EC12ECB4D0AAF8845B1D4C6331C7G34DB899083F87DA3CCDE2CDF068E7B09469E29D3667D3E56
	71FE9ED07686D88A307291A687E6F753A386333BA80D5D2D033EE4AC33BBACA7427B7A4D1BB77F810051717CD71887C44D41C09D42B59ACF33BAFDAF4A224FA1974AAC56FE6B24B656560FE53C1960598D10C67B93C2FDA77D977C35AB9C0B391E27F7624C865F0961B6187A4CECD27E664721C83BAB9B1CBC1649A5E82CE3EFD9FBE3D394330DF147902746214CBDA8F6B379986E13E60BD906484EFC1B8619D7202125155958EB4731BEF99C6C97G75GA20047B8EE3B0919878835DD97F6FB4654AA7078024F
	7767CC77F17CE719035F0FC5E5ED25BAA64BD2870E4F7131D9385632E425C8065B1905F23419DB5BE45614476ED9A8F7D1E821E8363EB3AE7557419F2BE7A81FF1CC877D6139FC1CA26BF15866794B49E4E09DCCA0B363F03EBA492A512743A7D02E6C44D6A7F4D8C523ACD4C8F844E0753F5E17245617D2EE69ABEB22685D23355A39B346337C0A88CF0F71BC97DAFB4DD58E469B6A7DG184ED9716821509A1E075FC4D804AEBDCD46993B644CF2B32CAADE8AE664E467679C551F484EBEC2F8CCA16471DC59B2
	98EF53F8FE96797DDEEC63AAE7545AE926B9A303218ECF9B250A3BD0E2459F6771F3C46E84FE0E70A6B80788E5CFG1F89BCFF5D7D85378F3F27E611AA0F8BAEE338203A63E17E93581A2D1A60300FC51D9B151CE82B0E1FB9933D82A32B3CB88FB90D29C4F34EC6E1988C67ECE6D32931350AEA8A2379D99D0FEFBD3DB84D7FCE478C0072CA00F5DEC37583A77E17909EB7FCF4AD5F0DACB17FE6B4192C12992C88EC4EE42A212FE58266981917E2B4461F62F9B65D231C4652DD741B8DA85781080F72BC5B7EFC
	742B1ED07685D886309AA098A0FAE2E0E4AAC0B6C0B9C083008CB0964087E0B140D20015GEBA7F29F71D98E6A023B58C591A8F594B62399DD05E2272B0E3B77957FF1FCF0DF3176B83DE3G9B2F6B31AAE7727547514E2516963B207A24BF9E0FD3F115769A49EEC6AF48F4E3C28D570DCD40CF6EA41EBBF93D062F39B8094BB31446578B209CG30F29257539B6DDABF0E3A676E2C31EE92534F38498EBD657BF4B81933B5EFGBDF079DD8FF7BA2AFD3DD44BF3BEB513B95DF39A106E31D07685D8B21973B87E
	9AEF7B1A5AF6DB92360D1E82FBB8C041949E7334678D96E756CCF115A76D99341FF35E14757B896DA7CE393F33004794FE97701BF024FF5A9467FC54EFA95EF70AF3BE6AC942575F8F6A629E63B692DDC873BFD01E8C3088E0A9C090402647385EBF170B816A00F9E8D81FAAD0F0D113FAE47CF5680677CD521DE176739FF2BFA6374B91A81F38294E727C0E62D327BA4B73DD5557E6C01D57D42EBF3BCF8DB4FF70D4F7E760FF22BECCB74DF97E1FD3FC5CB46779ABF67179D3214EE39ADF2B3CFC773E26B8FFF0
	DFF3A65FF5DCF2A13FFFDCD2136FBAAE391A2F0DCB986EAF79E87B2D60A36B55F60DAA6EDB95A750B191770D59A4372978F6CA9762AB841C1063EFD23A3AA82EDE415D52603ED5F0F77251DF824BED12CD3DAF4F47FB059633A83232A7EDE336007BD465B10E52E8369AB5F4CFD4F15F2AFAB725E6F87ABAE7187DD68D399F5327CE6A5B577453333B949F5CCF4F22AE2B7B8654B5CE67FE234900FEE3991403GF4B3E0BF854886281841F5F1FF81667A5D6D173512C48FA61804056DD230E9F6CE4663AB351C47
	869853BA232F6DF8C2595B8947F8C93806B47B4DF066B11562174CF066F1E18367F19954E54C6436BC699A1F5FEBA61B47F726E3BEFFF8DFBE53557C77685E32FA2673BD6DB80ADFB753791EB6BE09471E1B202E51BBB0125D2DACCD08257AA81AD1CA742BB9424F7CAF5378F29934DD6D4D4F7CFA9AFF85C3D977B867E1958E2B48739D4D7B82B2727960FF9E51B9143B0DC2777EAE395075FD4A3EC257F7E8878B873EA3BBDA687E3B2793055A6F1E987F5FC4B8645D28F97F03EE64E59F96B112F3B0FDBBF60A
	671862D4FD8B58656073A54DFB752E116739758718634EE587CF1E1A7758E167316EFF6EAFC6F48EE51FB53837A71BE17078610E41F57CB04A50BFFE98E3F09DBFF8993471835335F7EAF9EE3F71F16E6F22CA693739D09E2B62528FF01D74D3F18BFA9037894A0933381E26DCF32C5D814D7BB38DA33F0E4BC6F48E4673D357383CB2E6F1B90C0FC1DCB114EBD45C166DDCDE320A2BBE4D6535CC155768A40C15D62965C9349C221613129E94CFB807EB39E5BF30B9DCFD374C72C6C1F92813747E782CC44F70AC
	CF3C016E37493383A38F4E66795EFC527F2E7913A254A51657ADBFB5D8DED78B75B02EB85B1157750077B7B6EF5EFCC425A17E8A526085F8EF1506FD257DF355F925BCD736FCB64BD58741B384GF37C0CB64301215096638367E49F87ED39703E8B02184B21DCEEAF74603DCA0759E26E686C2099F6C6736B5476CEC37BAAG067B95554DEEA8FB7AF0BE6646744FDBAE0961FCAC06F64B810281F4CF30FB8A4607FE7BD06E2CD221EF76938EBE4A617DCDD064DF28B26E223ADF03F3BC41E90B0947B80CE7B77B
	664C17A6E97312E3955A1643F37593BCAF39902FEC95B9A5104344A9AF09FA16F140C43FC848EC12C45301237A9C78AF778899DA2EEDF9A9D1757003B2471C1F148C1A8B4D4949F364C22F1572F8B484F8B87AA44F23ACACD53F55F81267D13652EF16FD206C8F306249216687371FF1501CF356C173C38DE96EE94EADC412330FA87D6A4EB97A5D0B518F46EBEE5E9146E7D08C0F7B57G5D29F3F83E743DCC5C071C7225AC3F4B5225CA8EAAA316674E6A66F0F9DC0D253F630172820000B9C31547F3958EF93C
	DD69606B435DFB87626B283E70082A6BE38E65127B4E2F75D1EB465706A41EE3DD2972F5BD99790A71857F87D06C4B7DFEF3697072E5B7876D67BADF76A35A2F4E777E72E5ED3EBCDF36E6BF525F6B6BFCA6DCCF71E3FD1D4F04F1FA2E9B5320EE034A6FFBB143BB2F078C335F3158611D73DF21FA58B057195F57A93EED2EB33F8D311CDF996AD64D657CFE1AB4507C45735C6533DE277AD2B34FF97E55945FB04FF9FEA1194F5F86F5AB6671D864C56AF3326787C6D64C673847529057GE56BFCCE677B686294
	197DC9733EDBBF7271DC568FB047EB318E1E5E523CB7457EGE71E911D032DE9E2B20FC3FB55B5FDAB594167FFEA5E5B13C71EE74F911D037974D5252EE34DE02A730BC08E7E73F98C72F4DA7FD8735373A813CD7369F732497A8166DBBBDF79360576B3DDA4AA8D235306920F30BE9549982B313BB9FE475FA5E86F06D253BE8A45DC0A2B3B2172850C966B023EF7C3AC2FB8319A79D8GF5CB8156GEC8208DB88679800E2003A05BC3F3868E286F11FDFE49B36A329487ABCDE6D7EFC6BDBBD78797669EA5767
	5BE72B7B1FEF7D2BDD1FEFFFD65DBFBF7EF3E5DC1B7A71B4433FD40D464E62620BC60B31156617587FB3607102B7A0F675DBCFA2E4321D5D1369155F1559EDE4073A2015B4239DF46B04A17C5D7961E1C4D7C9A2998715C407E4D41288CAE5A589878E3A715736919AA0B05A4E7EE1C74944375E743BDE1E98C642E040B6010D905105F9FB9CA394F68EDD167DA639CB382FD77F49A49A3FCD51DF8142ECA4360F6E3DB4B76F3D953F3BEA8FA71115A42585B0D7849B7D2AC3B0E1A765273DE74996FADF0C078D60
	43CE9F91F639C7942AC8E2CBAD7E544D8E01CE894F6759E5320B7EB4A78B2F6D33AA0C76B6995736280A68AC78A3E43213D50BC6CBEB967BF824088AA3342411D20DEA9A8A74A6B8FE60230E1270420EC37B4F2FD992CE424E122D22E4B41DB5B603BAD612ADF21BCD303701A1F5DB3FBECD870A6EC28D4093138E065B23320F4CE65601862AC437F7BF61953BB676EC989CB8ECA40B361421C1265A42D456E63441D4022DD2462F2BA55BC135EE54BE790BDF1ECBEA88A3218A13389ADB3A2DB209ECB1F618459E
	F8E15FC253F50A05A23BB7D195880511F99678115FDE7F723C5F96872F03414E12887AD39EA0A94E512A3A3323C9300D711B3EF75B342089C831955965A3EF813B325871B7D2B0385E09E7DE25121EB975656C69CFEF7C0E126E68AA13B436C850B52BD510BA32CADADA4C96C19DGD68C065F2B8E0FEDB21D5A6C6C1E326B7C9B1B1BC0434AC9F8C7F6B67D1BC37F9E473FB99413C3B1B947E1590249007F1C6B3BB94FD4ADE9A191F5289B5F623BD51BA0FD2C8C3168E16DFD6C264FBF358DF80B508F5452317B
	68013EE82BCC223807C0FCD4BF65C66C367EDA0A689D2A2EE0E9A77E590DFF12E827947AB7F5481242DE7B1CA474D5B7617B4A11FB5A6F12B70CCEF67B5DB6F9066DA7094FB8769536276CAC26E7FA400B4F703D232988E46676BB4BAB9DC6D1140DE2FB0945045FDC16764802639EA02998473773B6CA25F237C0FF8BF1C353677F81D0CB878856AE59AF4997GGBCC6GGD0CB818294G94G88G88GC9F954AC56AE59AF4997GGBCC6GG8CGGGGGGGGGGGGGGGGGE2F5E9ECE4E5F2
	A0E4E1F4E1D0CB8586GGGG81G81GBAGGG8398GGGG
**end of data**/
}
}
