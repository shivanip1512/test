package com.cannontech.tdc.createdisplay;

/**
 * Insert the type's description here.
 * Creation date: (3/31/00 4:42:45 PM)
 * @author: 
 * @Version: <version>
 */
import java.awt.Frame;
import java.math.BigDecimal;
import java.util.List;
import java.util.Vector;

import com.cannontech.common.util.SwingUtil;
import com.cannontech.tdc.TDCMainFrame;
import com.cannontech.tdc.editdisplay.EditDisplayDialog;
import com.cannontech.tdc.logbox.MessageBoxFrame;
import com.cannontech.tdc.template.TemplateDisplay;
import com.cannontech.tdc.template.TemplateDisplayModel;
import com.cannontech.tdc.utils.DataBaseInteraction;
import com.cannontech.tdc.utils.DataModelUtils;
import com.cannontech.tdc.utils.TDCDefines;

public class ColumnEditorDialog extends javax.swing.JDialog {
	private String title = "";
	private int currentMode = -1;
	private Vector templateNums = null;
	private long displayNumber = -1;
	private CreateBottomPanel ivjEditorPanel = null;
	private javax.swing.JPanel ivjJDialogContentPane = null;
	private com.cannontech.common.gui.util.OkCancelPanel ivjOkCancelPanel = null;
	IvjEventHandler ivjEventHandler = new IvjEventHandler();
	// type of display
	public static final int DISPLAY_COMBO_ONLY = 0;
	public static final int DISPLAY_NAME_ONLY = 1;
	public static final int DISPLAY_ALL_OPTIONS = 2;
	public static final int DISPLAY_NO_OPTIONS = 3;
	private javax.swing.JComboBox ivjJComboBoxTemplate = null;
	private javax.swing.JLabel ivjJLabelTemplateName = null;
	private javax.swing.JLabel ivjJLabelName = null;
	private javax.swing.JTextField ivjJTextFieldName = null;

    private TemplateDisplayModel tempDispModel = null;
    
class IvjEventHandler implements com.cannontech.common.gui.util.OkCancelPanelListener, java.awt.event.ActionListener {
		public void actionPerformed(java.awt.event.ActionEvent e) {
			if (e.getSource() == ColumnEditorDialog.this.getJComboBoxTemplate()) 
				connEtoC3(e);
		};
		public void JButtonCancelAction_actionPerformed(java.util.EventObject newEvent) {
			if (newEvent.getSource() == ColumnEditorDialog.this.getOkCancelPanel()) 
				connEtoC1(newEvent);
		};
		public void JButtonOkAction_actionPerformed(java.util.EventObject newEvent) {
			if (newEvent.getSource() == ColumnEditorDialog.this.getOkCancelPanel()) 
				connEtoC2(newEvent);
		};
	};
/**
 * ColumnEditorDialog constructor comment.
 */
public ColumnEditorDialog() {
	super();
	initialize();
}
/**
 * ColumnEditorDialog constructor comment.
 * @param owner java.awt.Dialog
 */
public ColumnEditorDialog(java.awt.Dialog owner) 
{
	super(owner);

	displayNumber = ((CreateDisplayDialog)owner).getCurrentDisplayNumber();
	initialize();
}
/**
 * ColumnEditorDialog constructor comment.
 * @param owner java.awt.Dialog
 * @param title java.lang.String
 */
public ColumnEditorDialog(java.awt.Dialog owner, String title, int displayMode, long displayNum )
{
	super(owner, title);

	this.title = title;
	setDisplayMode( displayMode );
	
	displayNumber = displayNum;
	
	initialize();
}
/**
 * ColumnEditorDialog constructor comment.
 * @param owner java.awt.Frame
 * @param title java.lang.String
 */
public ColumnEditorDialog(java.awt.Frame owner, String title) 
{
	super(owner, title);

	this.title = title;
	displayNumber = ((TDCMainFrame)owner).getCurrentDisplayNumber();
	initialize();
}
/**
 * ColumnEditorDialog constructor comment.
 * @param owner java.awt.Frame
 * @param title java.lang.String
 */
public ColumnEditorDialog(java.awt.Frame owner, String title, int  displayMode, long displayNumber ) 
{
	super(owner, title);

	this.title = title;

	setDisplayMode( displayMode );
	
	initialize();
}
/**
 * connEtoC1:  (OkCancelPanel.okCancelPanel.JButtonCancelAction_actionPerformed(java.util.EventObject) --> ColumnEditorDialog.okCancelPanel_JButtonCancelAction_actionPerformed(Ljava.util.EventObject;)V)
 * @param arg1 java.util.EventObject
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC1(java.util.EventObject arg1) {
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
 * connEtoC2:  (OkCancelPanel.okCancelPanel.JButtonOkAction_actionPerformed(java.util.EventObject) --> ColumnEditorDialog.okCancelPanel_JButtonOkAction_actionPerformed(Ljava.util.EventObject;)V)
 * @param arg1 java.util.EventObject
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC2(java.util.EventObject arg1) {
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
 * connEtoC3:  (JComboBoxTemplate.action.actionPerformed(java.awt.event.ActionEvent) --> ColumnEditorDialog.jComboBoxTemplate_ActionPerformed(Ljava.awt.event.ActionEvent;)V)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC3(java.awt.event.ActionEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.jComboBoxTemplate_ActionPerformed(arg1);
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}
/**
 * Comment
 */
private void deletePreviouslyCreatedColumns( int templateNum )
{

	String query = new String
			("delete from templatecolumns where templatenum = ?");					
	Object[] objs = new Object[1];
	objs[0] = new Integer(templateNum);
	DataBaseInteraction.updateDataBase( query, objs );
	
	return;
}
/**
 * Return the EditorPanel property value.
 * @return com.cannontech.tdc.createdisplay.CreateBottomPanel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
public CreateBottomPanel getEditorPanel() {
	if (ivjEditorPanel == null) {
		try {
			ivjEditorPanel = new com.cannontech.tdc.createdisplay.CreateBottomPanel();
			ivjEditorPanel.setName("EditorPanel");
			ivjEditorPanel.setMinimumSize(new java.awt.Dimension(1, 1));
			// user code begin {1}
			ivjEditorPanel.initColumnType();
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjEditorPanel;
}
/**
 * Return the JComboBoxTemplate property value.
 * @return javax.swing.JComboBox
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JComboBox getJComboBoxTemplate() {
	if (ivjJComboBoxTemplate == null) {
		try {
			ivjJComboBoxTemplate = new javax.swing.JComboBox();
			ivjJComboBoxTemplate.setName("JComboBoxTemplate");
			ivjJComboBoxTemplate.setBackground(java.awt.Color.white);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJComboBoxTemplate;
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

			java.awt.GridBagConstraints constraintsOkCancelPanel = new java.awt.GridBagConstraints();
			constraintsOkCancelPanel.gridx = 2; constraintsOkCancelPanel.gridy = 4;
			constraintsOkCancelPanel.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintsOkCancelPanel.anchor = java.awt.GridBagConstraints.SOUTHEAST;
			constraintsOkCancelPanel.weightx = 1.0;
			constraintsOkCancelPanel.weighty = 1.0;
			constraintsOkCancelPanel.ipadx = 278;
			constraintsOkCancelPanel.ipady = 19;
			constraintsOkCancelPanel.insets = new java.awt.Insets(1, 2, 4, 9);
			getJDialogContentPane().add(getOkCancelPanel(), constraintsOkCancelPanel);

			
            java.awt.GridBagConstraints constraintsJComboBoxTemplate = new java.awt.GridBagConstraints();
			constraintsJComboBoxTemplate.gridx = 2; constraintsJComboBoxTemplate.gridy = 2;
			constraintsJComboBoxTemplate.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintsJComboBoxTemplate.anchor = java.awt.GridBagConstraints.NORTHWEST;
			constraintsJComboBoxTemplate.weightx = 1.0;
			//constraintsJComboBoxTemplate.ipadx = -6;
			//constraintsJComboBoxTemplate.ipady = -6;
			constraintsJComboBoxTemplate.insets = new java.awt.Insets(8, 3, 1, 310);
			getJDialogContentPane().add(getJComboBoxTemplate(), constraintsJComboBoxTemplate);

			java.awt.GridBagConstraints constraintsJLabelTemplateName = new java.awt.GridBagConstraints();
			constraintsJLabelTemplateName.gridx = 1; constraintsJLabelTemplateName.gridy = 2;
			constraintsJLabelTemplateName.anchor = java.awt.GridBagConstraints.NORTHWEST;
			constraintsJLabelTemplateName.insets = new java.awt.Insets(9, 13, 4, 2);
			getJDialogContentPane().add(getJLabelTemplateName(), constraintsJLabelTemplateName);

			java.awt.GridBagConstraints constraintsJLabelName = new java.awt.GridBagConstraints();
			constraintsJLabelName.gridx = 1; constraintsJLabelName.gridy = 1;
			constraintsJLabelName.anchor = java.awt.GridBagConstraints.NORTHWEST;
			constraintsJLabelName.ipadx = 4;
			constraintsJLabelName.insets = new java.awt.Insets(9, 16, 4, 15);
			getJDialogContentPane().add(getJLabelName(), constraintsJLabelName);

			java.awt.GridBagConstraints constraintsJTextFieldName = new java.awt.GridBagConstraints();
			constraintsJTextFieldName.gridx = 2; constraintsJTextFieldName.gridy = 1;
			constraintsJTextFieldName.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintsJTextFieldName.anchor = java.awt.GridBagConstraints.NORTHWEST;
			constraintsJTextFieldName.weightx = 1.0;
			constraintsJTextFieldName.ipadx = 116;
			constraintsJTextFieldName.insets = new java.awt.Insets(8, 4, 1, 309);
			getJDialogContentPane().add(getJTextFieldName(), constraintsJTextFieldName);

			java.awt.GridBagConstraints constraintsEditorPanel = new java.awt.GridBagConstraints();
			constraintsEditorPanel.gridx = 1; constraintsEditorPanel.gridy = 3;
			constraintsEditorPanel.gridwidth = 2;
			constraintsEditorPanel.fill = java.awt.GridBagConstraints.BOTH;
			constraintsEditorPanel.weightx = 1.0;
			constraintsEditorPanel.weighty = 1.0;
			constraintsEditorPanel.ipadx = 487;
			constraintsEditorPanel.ipady = 212;
			constraintsEditorPanel.insets = new java.awt.Insets(1, 11, 1, 2);
			getJDialogContentPane().add(getEditorPanel(), constraintsEditorPanel);
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
 * Return the JLabelName property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getJLabelName() {
	if (ivjJLabelName == null) {
		try {
			ivjJLabelName = new javax.swing.JLabel();
			ivjJLabelName.setName("JLabelName");
			ivjJLabelName.setText("Name");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJLabelName;
}
/**
 * Return the JLabelTemplateName property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getJLabelTemplateName() {
	if (ivjJLabelTemplateName == null) {
		try {
			ivjJLabelTemplateName = new javax.swing.JLabel();
			ivjJLabelTemplateName.setName("JLabelTemplateName");
			ivjJLabelTemplateName.setText("Template");
			ivjJLabelTemplateName.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJLabelTemplateName;
}
/**
 * Return the JTextFieldName property value.
 * @return javax.swing.JTextField
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JTextField getJTextFieldName() {
	if (ivjJTextFieldName == null) {
		try {
			ivjJTextFieldName = new javax.swing.JTextField();
			ivjJTextFieldName.setName("JTextFieldName");
			// user code begin {1}

			ivjJTextFieldName.setDocument( new com.cannontech.common.gui.unchanging.StringRangeDocument(20) );
			
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJTextFieldName;
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
 * Called whenever the part throws an exception.
 * @param exception java.lang.Throwable
 */
private void handleException(java.lang.Throwable exception) 
{
	/* Uncomment the following lines to print uncaught exceptions to stdout */
	com.cannontech.clientutils.CTILogger.info("--------- UNCAUGHT EXCEPTION ---------");
	com.cannontech.clientutils.CTILogger.error( exception.getMessage(), exception );;

	TDCMainFrame.messageLog.addMessage(exception.toString() + " in : " + this.getClass(), MessageBoxFrame.ERROR_MSG );
}
/**
 * This method was created in VisualAge.
 */
private void initComboTemplate()
{
	
	// Init our Template Name Combo Box
	String query = new String
		("select name, templatenum from template");
	Object[][] templates = DataBaseInteraction.queryResults( query, null );
		
	if ( templates.length > 0 )
	{
		
		for( int i = 0; i < templates.length; i++ )
		{
			getJComboBoxTemplate().addItem( templates[i][0] );			
			templateNums.addElement( templates[i][1] );			
		}

		getJComboBoxTemplate().revalidate();
		getJComboBoxTemplate().repaint();
	}

	//init our first display
	if( getJComboBoxTemplate().getItemCount() > 0 )
	{
        Integer templateNum = new Integer (1);
        Frame parentFrame = SwingUtil.getParentFrame(this);
        if (parentFrame instanceof TDCMainFrame)
        {
            Integer dispNum = new Integer (((TDCMainFrame)parentFrame).getMainPanel().getCurrentDisplay().getDisplayNumber());
            TemplateDisplayModel m = getTempDispModel();
            m.initModel(dispNum);
            int selectedTemplateIndex = 0;
            templateNum = m.getTemplateNum();
            if (!templateNum.equals( TemplateDisplay.INITVAL) )
            {
                selectedTemplateIndex = templateNums.indexOf(new BigDecimal (templateNum.intValue()));
            }
                
       
            getJComboBoxTemplate().setSelectedIndex( selectedTemplateIndex );
        }
        getEditorPanel().editInitialize( new Long ( templateNum.intValue() ) );
	}
	
	getJComboBoxTemplate().revalidate();
	getJComboBoxTemplate().repaint();
}
/**
 * Initializes connections
 * @exception java.lang.Exception The exception description.
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void initConnections() throws java.lang.Exception {
	// user code begin {1}

	if( getJComboBoxTemplate().isVisible() )
	{
		templateNums = new Vector( 15 );
		initComboTemplate();
	}
		
	// user code end
	getOkCancelPanel().addOkCancelPanelListener(ivjEventHandler);
	getJComboBoxTemplate().addActionListener(ivjEventHandler);
}
/**
 * Initialize the class.
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void initialize() {
	try {
		// user code begin {1}
		// user code end
		setName("ColumnEditorDialog");
		setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
		setResizable(true);
		setSize(501, 330);
		setModal(true);
		setTitle("");
		setContentPane(getJDialogContentPane());
        initConnections();
	} catch (java.lang.Throwable ivjExc) {
		handleException(ivjExc);
	}
	// user code begin {2}

	setTitle( title );
	
	// user code end
}
/**
 * Comment
 */
public void jComboBoxTemplate_ActionPerformed(java.awt.event.ActionEvent actionEvent) 
{
	Frame owner = SwingUtil.getParentFrame(this);
	java.awt.Cursor original = owner.getCursor();
	owner.setCursor( new java.awt.Cursor( java.awt.Cursor.WAIT_CURSOR ) );	
	try
	{
		//getEditorPanel().removeAllColumns();
		
		if ( getJComboBoxTemplate().getSelectedIndex() >= 0 &&
			getJComboBoxTemplate().getItemCount() > 0 )
		{	
				
            BigDecimal templateNum   = (BigDecimal) templateNums.elementAt( getJComboBoxTemplate().getSelectedIndex() );
            getEditorPanel().editInitialize( new Long( templateNum.toString() ) );

        
        }
		
	}
	finally
	{	
        owner.setCursor( original );
	}
	return;
}
/**
 * main entrypoint - starts the part when it is run as an application
 * @param args java.lang.String[]
 */
public static void main(java.lang.String[] args) {
	try {
		ColumnEditorDialog aColumnEditorDialog;
		aColumnEditorDialog = new ColumnEditorDialog();
		aColumnEditorDialog.setModal(true);
		aColumnEditorDialog.addWindowListener(new java.awt.event.WindowAdapter() {
			public void windowClosing(java.awt.event.WindowEvent e) {
				System.exit(0);
			};
		});
		aColumnEditorDialog.setVisible(true);
	} catch (Throwable exception) {
		System.err.println("Exception occurred in main() of javax.swing.JDialog");
		com.cannontech.clientutils.CTILogger.error( exception.getMessage(), exception );;
	}
}
/**
 * Comment
 */
public void okCancelPanel_JButtonCancelAction_actionPerformed(java.util.EventObject newEvent) 
{
    this.dispose();
	return;
}
/**
 * Comment
 */
public void okCancelPanel_JButtonOkAction_actionPerformed(java.util.EventObject newEvent) 
{
    Integer templateNum;
    switch ( currentMode )
	{
		case DISPLAY_COMBO_ONLY:  // editing a template
    		templateNum = new Integer ( Integer.parseInt( templateNums.elementAt( getJComboBoxTemplate().getSelectedIndex() ).toString() ) );
            deletePreviouslyCreatedColumns( templateNum );
    		writeCreatedColumns( templateNum );
    		//update our template display data model   
            updateTemplateDisplayModel(templateNum);
            updateDisplaysAssociated (templateNum);
            updateCurrentDisplay(templateNum, true);
            this.dispose();
            return;
		
		case DISPLAY_NAME_ONLY:  // Created a Template from scratch
    		templateNum = new Integer ( TDCDefines.getValidDisplayNumber() );
            writeCreatedTemplate(templateNum.intValue());
            //update our data model
            updateTemplateDisplayModel(templateNum);
            updateCurrentDisplay(templateNum, true);
            this.dispose();
    		return;

		
		case DISPLAY_ALL_OPTIONS:
		    return;

		case DISPLAY_NO_OPTIONS:  // Advanced template option
    		default:
    		this.setVisible( false );
	}
	
	return;
}




private void updateDisplaysAssociated(Integer templateNum) 
{
    List<Integer> allDisplaysForTemplate = DataModelUtils.getAllDisplaysForTemplate(templateNum);
    for (Integer dispNum : allDisplaysForTemplate) {
        DataModelUtils.deleteDisplayColumns(dispNum);
        DataModelUtils.templatizeDisplay(templateNum, dispNum);
    }
}
private void updateTemplateDisplayModel(Integer templateNum) {
  
    Frame parentFrame = SwingUtil.getParentFrame(this);
    Integer displayNum = new Integer ((int)((TDCMainFrame)parentFrame).getCurrentDisplayNumber());
    getTempDispModel().saveModel(displayNum, templateNum);
}

private void updateCurrentDisplay(Integer tempNum, boolean templatize) {
    TDCMainFrame mainFrame = ((TDCMainFrame)getParent());
    EditDisplayDialog dialog = mainFrame.getEditDisplayDialog();
    dialog.updateCurrentDisplay(templatize);
    mainFrame.getMainPanel().executeRefresh_Pressed();
}
/**
 * Insert the method's description here.
 * Creation date: (4/3/00 10:26:27 AM)
 * Version: <version>
 * @param mode int
 */
private void setDisplayMode(int mode) 
{
	currentMode = mode;

	switch( mode )
	{
		case DISPLAY_COMBO_ONLY:
		getJLabelName().setVisible( false );
		getJTextFieldName().setVisible( false );
		return;
		
		case DISPLAY_NAME_ONLY:
		getJLabelTemplateName().setVisible( false );
		getJComboBoxTemplate().setVisible( false );		
		return;

		
		case DISPLAY_ALL_OPTIONS:
		return;

		default:
		com.cannontech.clientutils.CTILogger.info("INVALID mode in " + this.getClass().toString() + ", displaying no options." );
		/* NO BREAK OR RETURN HERE!!, let it fall */
		
		case DISPLAY_NO_OPTIONS:
		getJLabelTemplateName().setVisible( false );
		getJComboBoxTemplate().setVisible( false );		
		getJLabelName().setVisible( false );
		getJTextFieldName().setVisible( false );
		return;
	}	
}
/**
 * Insert the method's description here.
 * Creation date: (4/3/00 5:43:20 PM)
 * Version: <version>
 * @param item java.lang.Object
 */
public void setSelectedItem(Object item) 
{
	getJComboBoxTemplate().setSelectedItem( item );
}
/**
 * Comment
 */
private void writeCreatedColumns( int templateNum )
{
	for ( int i = 0; i < getEditorPanel().columnCount(); i++ )
	{
		String templateQuery = new String
			("insert into templatecolumns (templatenum, title, typenum, ordering, width) values (?, ?, ?, ?, ?)");
        Object[] objs = new Object[5];
        objs[0] = new Integer(templateNum);
        objs[1] = getEditorPanel().columnFieldData( i, ColumnData.COLUMN_TITLE );
        objs[2] = getEditorPanel().columnFieldData( i, ColumnData.COLUMN_TYPE_NUMBER );
        objs[3] = new Integer (i);
        objs[4] = getEditorPanel().columnFieldData( i, ColumnData.COLUMN_WIDTH );


		int result = DataBaseInteraction.updateDataBase( templateQuery, objs );
        if( result >0 )             
            TDCMainFrame.messageLog.addMessage("Template column data written to the database successfully", MessageBoxFrame.INFORMATION_MSG );
        
    
    }

	return;
    
}

/*private Object[] getColumnData(int templateNum, int i) {
    Object[] objs = new Object[5];
    objs[0] = new Integer(templateNum);
    objs[1] = getEditorPanel().columnFieldData( i, ColumnData.COLUMN_TITLE );
    objs[2] = getEditorPanel().columnFieldData( i, ColumnData.COLUMN_TYPE_NUMBER );
    objs[3] = new Integer (i);
    objs[4] = getEditorPanel().columnFieldData( i, ColumnData.COLUMN_WIDTH );
    return objs;
}
*//**
 * Comment
 *//*
private void writeCreatedTemplate()
{
	int templateNum = TDCDefines.getValidDisplayNumber();
	writeCreatedTemplate(templateNum);
}*/
private void writeCreatedTemplate(int templateNum) {
    String query = new String
			("insert into template (templatenum, name, description) values (?, ?, '')");
	Object[] objs = new Object[2];
	objs[0] = new Integer(templateNum);
	objs[1] = getJTextFieldName().getText();
	int result = DataBaseInteraction.updateDataBase( query, objs );

	if( result > 0 )
		TDCMainFrame.messageLog.addMessage("New Template : " + getJTextFieldName().getText() + "  written to the database successfully", MessageBoxFrame.INFORMATION_MSG );

	writeCreatedColumns( templateNum );	
	return;
}
/**
 * 
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private static void getBuilderData() {
/*V1.1
**start of data**
	D0CB838494G88G88GD8F954ACGGGGGGGGGGGG8CGGGE2F5E9ECE4E5F2A0E4E1F4E135BB8BD0D4D7169A7093A5918D9AE25814A6E6462930B3192CDBE3E55DDD433895F763D41C29E41C4CD0DB6E8E3B63E41C1419B5DBCEE21517869AD0C0FEEABB20A1D18CA404CFA0A36297E80DA89959C094D7AB7A209F50703ADF5B7D9A1A0F3A675C4F6BC7739A9257D0F5FC6F1EFB6F39771E7FBD7DA4056FAEC9CB369492121CC6785F51E4C2626B8879F57A607B82B7CDCE3092435FFBGF9647723
	29700C063E0E47922C39642F158CE8AF037675C7932C7B603D00BC27445740CB18F5A2216FEA7962FF1FFC1D1D39E21DF45A5E1616854F9C0065004367D699574F355AB8FE9D478BC83690125803E3BE3296F3DCBD345BGE28162FB5878B5704C24AB4FE82867632E4FDFC2367E4B8716EC24E320A960F0AF1BFB1EBFCB488F12377462DA9D56ACBEE79A34D7826061D3485541E370BC51B650FB2ADBF5D5F6CBEE376A56646E3ECA4D51DD596D15A5CDF6B8FD9EC59AEEE8E8D6953F4BFD566154D42F5DA9A9EA
	AF4CD57A10FE1CB50352A3248E5A5BA86E1F72F14DE761FDAD40C69D9FD5007893CE109500629467BC794189FE0E4E519F10488D6E145DE14E9969E4670BF7864F9963FFBBFDBF154917F9487BCD5017551FE0AD813083B8GA2810A28AEDC4F7D901E0DDDDA336AF2296EC60F27C9F1F8BFD79D32826F8D8D5043F1DF485D2A57C18813777FDAABCC76B39930F9FD5FF89E634972DAA17B78AF3EC3927F6622A55D04A7094B2BADF17D0C97AB7A03BCE1EBAF8D3B76AB7D63F5F37C5AD708F1EDED261A12E63276
	0AD74BAD1BB81D6D0635AB86D05647282C0B61BD196B8743B7D33C4700E7FA7A925599ECCF033E2481219B1DB9E2AF2D290BC9EECD2BA99FE2939AAD679536071BCA68DEC8A1520ED2581A697ADE32A8FE19814F7632006E915B2B202F4415E0E57A75D5815AEDA43417G2C84D88F3085A04B8D635CC2471E4E2D7A1ABA56A6F935A63FD3F148DE4266FE1537971EF2C073CAADEE1FA6393B654B1257A9F5A9320F70B13DF938372EF173942A7B5D3046E732DB76CA1AD3F583F549A5EB32574746787372F08C5A
	B674F6B8DDB2AC607290C177F6DE815A3564533E74B8407E03A3D87F0CBC144920F499A88281F8E7FC19CB79F8864E5F895027730164A33D0B3257ADAB8D8D2D2ABA6077F460C948CAB772F9A5759DE3709E6396BC1E3587F15B21DD288ABAC773738DF46C4EEE5C3E649D96B6F63AGFDF319AAF4660DCE21B3766C27C9452B97CCF5067ACB156B0C9AEABBAD14A66E0B35FF1A5F798D6D588C4AD4F33BE3EB3CFEDF56C88903E77CFDEBAF4AEAB66CE399409A1D475E7C922633CD12CF96DAFA498B8AA6BB029C
	E77B34960A1802EB25697B4F2778C420196AC930D6F8846FE28BBAC3467FFF4FB811F7BF24EB9877F3BF57E0BC6AAB4038A48F4AEE4DAEFBE43783DEFC3CCFADC05D63F65B236AF37C946F81BBEF8643726978518234C5C9533C4EAE3FA6374B1A66F477867BA368DABEFF17A768A244DAF38A700C6A905839BEFEDE815AC8376AF64BD4D87A945EBF1F4AC6D6E497EC3AD9753BB506FF349075FCC09E66BE6271C2CCFE86A5450F7E06658A4FCA3B708CDE278BD4GDD96D96EC17BFDD94FF590BAG07B94F98BC
	23BD4296078E89DBBCBC32142C3ED1EAB10BA531CF5BADDBBC4CB6D2EF85ED116DA9EA0F795E97D0DF4171BE365736F26403D94242743F5206348E439A75GED37047E676C45FE61E3386283990531D36773BBFBF3D1AE72AD3F8C2E991D59C51DF716FD79A1FD17B99FD97F4EFD48C74560875B3C48473305A29F6C037DCC83D8F60B71F29B67859B1733C70C0B7332F1AB3CECDC2A4FB86E677A38CE40FBG22FDEC5CFABE0E716A0BDBF8E61A8376CB03D22052B784BA5850F0A1A447BB160EFC463D97EBA217
	5B3C93795881ED85A0D293727EE9364865E28616100DCF7DA9EC4E9A27313D2C560272AE099039DCE0BF2EC97C3016DF48E846BEE3BCE9747954EEA78DE51C3FF5B623BEB05CB15BCEF876B83DBE2D858CB6A0E4F15A067B45D84751BC763B291DA0FF8E433A5781182EDF49B16AFA8C605781EC81C89DCC3026D01A2F961EC73B9CE846282D34C9304D4A8BCDFECDD35D8C55C8F56906C49FED3237C7753AE4C799B23F522FB9154A3368DBAEF575035215B399253EF7B114660D2FC33355146871724BBC47FB32
	608CBF4B3A6FAFA6A76E96D84CF26E593B8FD80A8719FC6C03C1B90DBDE7CD597C55BB690CE7C777881EC5431835BABF1E58E38F470FCB8377400B2A8FBF8B470B30744273016DFD073EF7B284BE81A0FE88719F1EAEB47041B344649DBBA44ED6317F93745FF435CA53C3C8C3DAA50D9515ECEBF479F2E1FB17B2117F7D10A1F629CDEA20C3F641D5CB13EFB41A6E5F14969C02453ED3691867EF053DE584A66697459CD74D1FF6F8327C7766AEBCFBE7A0B4C76F27783EC0E80E7E071DA2C78FC0DFE2C064EC69
	C5E89BE70641DEGB400A800F800F54342F6DF345512A9F2629638FFFA5D1252BEAE68095CF4BF2E8BF10A37276FC7BE52B8C5C48E2095065EF7B68D0F67C76A4878B61B774004F987C35AE5A173188C1786AAC878BBB46A1C8FC209D1DD19AFD93A4B68CB4E0C307C57814FGG733973F2443DBAFAC4785C21D3487F35505E08389141FF39987DFD6EA84895408EF093A092E089401AD1A103DDFFBAB9158C28E2F5284D00BB8FF12AB9B41F50F9F6F24F3D717A6204F9213CCE9AB51B77126D5E56FBED423C50
	75360F1A4936D7A9A22631D1D8AC0F0D52698CBE27F0CC44405B670CB1F0556ECFAC98BB4DE2E06B98DBDB99B35CD97DEF27337B5117B46E46C35FFAG060B2738A4E8EF57F19D99084B3D1DE0AD3BADF07ECC4435C27B260E7B870A5320BD7B36484DFBF651FB0E5E7650B67ABADE675AF9BF6EA4C64035464BA8EF577DDE43F887E23A1FDB94BEF75FD7E41E3B9FA8B24F5D8F9565B1BC50B05E60C57FC7C513677635C5E6397D813A6FFF5E3B1B527679E49FB5C63B24C93C6FB75A9EE212637F85AE797C4C85
	641942CCBE662C26B657687A7CDF65B6F3FD26FE096972EB3B04AE2F38ADFCCECCB67A1C4D50CE3D13E0AD3EA374A2A6E7323AD65D1D09F10845165FD19FF67E8E0BAD49BAFEAB45F79870AC665CBEA56E1AB721EF4D9D614B546270729CACB61767D8B16622BE556F6D16C3E596D1EC537B02DE2FC513DD48DEFE8F2173B5495BABCF1079A3F4BE6B8BB7BF2EB81CDEB059E9FB0C327BC447DB769B71353A5D741C8EA703323B895693F7A74A00450B7A03185B34C13F8290899087306A2E606D1983D3466A3381
	53D84D762B24231EBA649E492F006928BE7912071724GDFB88D65BAABDD68DA62DD11D37DB89D4F6915FD4E91FABB92E3FF15AEE2E167B4D13F7E61907A549E2F64829636FB64EE321D0E9D489031358F5AE3GF1G2BG922709F522B21106CBF5C80A3E46D319E28D7BF4BD1F2763B427C67752B19DFF97A8AEA53ADC7EBE956FFB7C36540C219F7D01DB96727957DD2172091D9E14C97C74F0B6121503BA3FFABA3305EC9DDFC471C986BC33114554C6311D71G64DF8F88FF7FCC0E088B9B9F90F67B66A922
	6B4F5B067708D3775B87CF0C45F15F6A9A6C4C37E9BC2E0EC8301E0F50E3E11360437588DD478F06779F1F9DB4779F1F9FB40F876D87A70F87578ECEEE77CF507C5D6016AB2FB85D8EF588D48AAB401A2F525E525EF6297D6C0DCBFF39515C8AAFAC4FD8B2E47CFD28FE67D1535A8A553588FE57664FD5338E71355BB2B05EEBDE3F4CE240FC1ACBA00DEDB036BA92F17F18E13C8B613C0596C2AA114F347F4DF17DF856D94458FFA853580FFB7FDD0A317F4BF17DF8068F78FCF64E17CAC5BE357D2565C4795BE3
	E1EB8A01C8F63EB87EEC3BFC089C1E73CEBABB6B7319622C0C6E4B570C7C4BDD7C1EA5B98C5DD7B83D0D1141BC0D6956DBB4E6964E005CEB0650372EEC21EF2DB3045D65537A01875AD1G71G6BG12G48CCB166BDBA866BBAD83331813E9E2093A0200FD912FDB3E4EFB85F58CE6274A733958F6F0FB29937CE5F735FCDD8ABBB240DBEF82ADA2B48454C6A3EA80775B311E789B976C9E8247131A99C62E04E33G87F85F9AF87F5842740649BA66B456FD3A246E01DE2F6AF7BB041F0F277E41B6AB41FA9800
	2D9D55B4555AE906356DB02F9320146395F8FFEB470E9D88AC3E469D41B582000F19A57C0DD47A91BCAFCB7DBC1A325AA48D2700EC732A90A135E10625D5C36EB397E55C5B6F07AF248B1BD9717C13E4654CBFA6073B032C1845F460957E5C54F1086259D92CE528132933932CA5GF6006BG9E00593345591617629EB1AB006DB7EA1A545DC7ABF0A2D9386C140702E88C560CF6F9B96E1451F82334B49CFF004285700D2CF253A1756AB24E4A42DA35A28D2BFE8DE25F035457E409789F877BDB81108830E5B6
	7787F44CF0364D048FC6FDADA0A3A7D15747DD5E753E9F65230F3B90DA3B517B7B884D8BDA25AED9911DACC9E77DA7CF6C547B0D78A3A5387E058E08BC671C3262B07695DEC379B12E305D30BA559315C65F997BA02BD32DFAD054319E3719752F577BDDD70C7DC93CBFEB0E28FB1E49B4722AE48E6BF740B3G70AE5D9B701E083FAD274DF0ACF77A8D7E744E75735F3204FE96BF73B831EF78E37221B07A99BD077B5EB9C15F3B29FFFBBA23796847C467FBFAA31EC5AF18B52B0A6A2D9C6AF3EA72575B274871
	1E2F8C6A6552A338269E3B19E9723E65C7F0BD275B29C18688B9ABA6386DB0505D4B74FBCE95765B66A6D86BGBA819400E9F3055DD4FC70BE339B931BB96B6815C57C976B6DAE8B1F336CAD1B7AF763F74B28CFD4B5CDF5A1FD01AFADC3DF23299E0AE4388F4BE8AE6E6C6D5358D00E2FAEC3FEA9F28FC5337C73E2163823454159DE19ABF2741A1BD817526F1CA81406064F3CCEC713545BCA2D16659C0FE4091CA3ABCA5CBBDF20B46D503E8E20C5095ACA6008501B786FACA62D53AE1A66829BBBBE31C4C5B1
	FDF9B6AA28B74B7DEF27B3BA7D83C67D433EF0B945E6BEBF7721A09D919760DE5D9366DECD2D1B1D6F55CABC9F72386DA1D15F7B19CD5CFFE6BFA478D5DAD6ECCECB7788EC6E32AB622E32DA1F5BF43A1EFC7D6FF9E83309AA02617E9F48947B5C761010C3BE7DAE237AE1482781EEBEACEA0E37EBB0E686209D833092E0BDC09240F6005CF990DF816A815AG7AG46G228116812CGD88B10B8CF37050FB098040B0FDCE928C1F8A5A71A84574785D561ED62312A29ED6249AA7A1B9168F24730075F533FDB05
	F1C56D6971493A6D45D7656A3896DF1B6AF3223533987DBFD97776ED500DFB1C977B0D67317B6327741E38E5DEE86D659445EF1F97DAFBF9D1376B0CE8505BE8A17B7D3528736B213D99A0F5FE0235986084C067FCA14F466AEAB2C58D21C37534026094431509517755A1FD85E84D860887D883708B00ADBA7D39553513684B972ACAEF62F8A987F5113D864DE1BC7CE525998FD397B09B3771E7450250DA65068975E404B3FCDC47020979B41B773DAB13492CAF6C3C5B1946F9E3A16322213DAC8447E4BA135A
	6D4A8521321EC771EB96044A5A16AFE43D9E7A6A9E91FC1EDF935E3EE2EB26362F25B566B1673BB593E3CEFC0DF94CF92E4698F3986EC7B56877FA41AFF46A63D669382BBA6E85CA93F1D71C8E2DCF473FC877057873B2AEA870BF217B9A2238CE0E7B1981F71563B654200F00A37729DEB1B731866F17BDCEC5E16D0DF40C538DAEC36747FF50BDBABD12E15F2F6B382B3A5C0FE3B8626FF6437BCF323E7DBA4DFFFE2BEB30B33FDF9BDE2F4AEB4D6BAFD535667517CFEBA72F371F2C8D7FAD0D3DF66237B4E3F5
	617796D1B7354EC755196B7C023A09BA7FE81D394E7FD51DD167D9DD61CD1AC74402EDAEFFC4549AAED709DA438A9D77E71ADB2446C08C0E9138B7ECE2EE3D0E3B34DF4C356B3855D784AEC64745503CA0895A5BF45C2FB245380C058257FA9CF11D5076AC94B504287C206EAEB43C9F3CAF5F3ACD2E47C73F55B5581DA3230A5614C0A1585D617DBF6399D9DD41D5B5D9DDC1FC2BA66A8A1D063A827A614805C19F4D567A5C067795F0B84E910CEBCA23626CF5632D0157B6FA751CB29A66327D1CBD9A5CCF4F7E
	297613B249FED6ADE4B9F832CEEF4315296AA6C6FAEB970E2F1BA4C27B37634E77CC26155A0D1796CF37AE24F5A1DA335B34D054EA7E10B95599528DEB57AFCA3036826C616D1B707EB23CFCAFD92C59FD9C6F55AE3844397C2EF667084CF9B9CAF5D9017101C50217FD18E273F9F7ECF8FFB456EC5B715FE04936756C398EFE1F1C0B1F5A137077626FB784697E02DECF78F7713231BED9BD1AECC9A166F714A50B78DDE7D1303E7B4D6E6605F89D627B9DBD995C4FFF67611D44AD8F156877EA3B93F48EBFGAE
	2B29A829A9E7B9DB59A9145346C5A2EFF833816F366172B37C3E005D14053F2BB8053277F19C7BFF9BB5A32F637F8379BDC903706C73125D7A96DAE8CC5DB8D85B10726B27D2ACA43505D8193FEDA12968B4DBC89A7529ADA4857CEDG3FF1467DD9B8EF1301DA4E78C2C172537BA2F267557AADA4639C3176C80A27CF82AA748B13D6322B0B1243C4D251C8AE7DAA278A66D6513947DB09255B271122A03E9B712C375DA3F54BB0A453A1F987A805DB648EC93DC6B2FB3C3DDDF4810DD82E1D3B864B5296F6DBDB
	49CE2D0FA63060DBB432AFC83A8D53D85EF11C9E0D6C00BF8F7D7361E4035BC8C6FF1421118CD10852C8AA561574F14816EC411AF1D83C706C9E0765BD491FA24DF158DD1C82B2B6FF2226B4BE7C5A06789B76C28B3100247AE4A61A34A1CC496889B89D267765F70DFA3FED06AD6C6F9A310F717F8F72D870EE4E74B1E98CF5FC9360D3978B1D2C9B7BB81CCE42F5245DA5A98AF85501C637836B0DCD431A9C4CA1FEBB0674FDE28C6FE4FDDB46B239AF90F37E8FD0CB8788B2E3F4E2CB93GG54B4GGD0CB81
	8294G94G88G88GD8F954ACB2E3F4E2CB93GG54B4GG8CGGGGGGGGGGGGGGGGGE2F5E9ECE4E5F2A0E4E1F4E1D0CB8586GGGG81G81GBAGGG0593GGGG
**end of data**/
}
//must contain all contexts this component belongs to
public TemplateDisplayModel getTempDispModel() {
    if (tempDispModel == null)
    {
        tempDispModel = new TemplateDisplayModel();
    }
    return tempDispModel;
}

}
