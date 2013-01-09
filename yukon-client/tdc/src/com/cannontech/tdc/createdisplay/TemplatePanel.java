package com.cannontech.tdc.createdisplay;

/**
 * Insert the type's description here.
 * Creation date: (3/31/00 12:02:55 PM)
 * @author: 
 * @Version: <version>
 */
import java.awt.Color;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.math.BigDecimal;
import java.util.Vector;

import javax.swing.JCheckBox;
import javax.swing.event.TableColumnModelEvent;

import com.cannontech.common.util.SwingUtil;
import com.cannontech.tdc.TDCMainFrame;
import com.cannontech.tdc.TDCMainPanel;
import com.cannontech.tdc.editdisplay.EditDisplayDialog;
import com.cannontech.tdc.logbox.MessageBoxFrame;
import com.cannontech.tdc.template.TemplateDisplay;
import com.cannontech.tdc.template.TemplateDisplayModel;
import com.cannontech.tdc.utils.DataBaseInteraction;
import com.cannontech.tdc.utils.DataModelUtils;

public class TemplatePanel extends javax.swing.JPanel 
{	
	private long currentDisplayNumber = com.cannontech.tdc.data.Display.UNKNOWN_DISPLAY_NUMBER;
	private Vector columnData = null;
	private Vector templateNums = null;
	private javax.swing.JButton ivjJButtonAdvanced = null;
	private javax.swing.JComboBox ivjJComboBoxTemplate = null;
	private javax.swing.JLabel ivjJLabelTemplate = null;
	private javax.swing.JScrollPane ivjJScrollPaneTable = null;
	IvjEventHandler ivjEventHandler = new IvjEventHandler();
	private javax.swing.JTable ivjJTable = null;
	private javax.swing.JLabel ivjJLabelCustomized = null;
    private int selectedTemplateIndex = 1;
    private TableColumnListener templateColumnListener = new TableColumnListener (this);
    private Frame parentFrame = null;
    
    private JCheckBox useTemplateCB = null;
    private TemplateDisplayModel tempDispModel = null;
	
class IvjEventHandler implements java.awt.event.ActionListener {
		public void actionPerformed(java.awt.event.ActionEvent e) {
			if (e.getSource() == TemplatePanel.this.getJComboBoxTemplate()) 
				connEtoC1(e);
			if (e.getSource() == TemplatePanel.this.getJButtonAdvanced()) 
				connEtoC2(e);
            if (e.getSource() == getUseTemplateCB()) 
                useTemplateCBPressed(e);
            
		}

	};
/**
 * TemplatePanel constructor comment.
 */
public TemplatePanel() {
	super();
	initialize();
}
/**
 * TemplatePanel constructor comment.
 * @param layout java.awt.LayoutManager
 */
public TemplatePanel(java.awt.LayoutManager layout) {
	super(layout);
}
/**
 * TemplatePanel constructor comment.
 * @param layout java.awt.LayoutManager
 * @param isDoubleBuffered boolean
 */
public TemplatePanel(java.awt.LayoutManager layout, boolean isDoubleBuffered) {
	super(layout, isDoubleBuffered);
}
/**
 * TemplatePanel constructor comment.
 * @param isDoubleBuffered boolean
 */
public TemplatePanel(boolean isDoubleBuffered) {
	super(isDoubleBuffered);
}

public TemplatePanel(Frame pf) {
    super();
    parentFrame = pf;
    initialize();
    
}
/**
 * Insert the method's description here.
 * Creation date: (1/26/00 11:11:55 AM)
 * @return int
 */
public String columnFieldData( int i, int field ) 
{
	ColumnData column = ((ColumnData)columnData.elementAt(i));

	switch( field )
	{
		case ColumnData.COLUMN_NUMBER:
		return column.getColumnNum().toString();

		case ColumnData.COLUMN_TITLE:
		return column.getColumnTitle();

		case ColumnData.COLUMN_TYPE:
		return column.getColumnType().toString();

		case ColumnData.COLUMN_TYPE_NUMBER:
		return column.getColumnTypeNumber().toString();
		
		case ColumnData.COLUMN_WIDTH:
		return column.getColumnWidth().toString();

		default:
		return "";
			
	}
}
/**
 * connEtoC1:  (JComboBoxTemplate.action.actionPerformed(java.awt.event.ActionEvent) --> TemplatePanel.jComboBoxTemplate_ActionPerformed(Ljava.awt.event.ActionEvent;)V)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC1(java.awt.event.ActionEvent arg1) {
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
 * connEtoC2:  (JButtonAdvanced.action.actionPerformed(java.awt.event.ActionEvent) --> TemplatePanel.jButtonAdvanced_ActionPerformed(Ljava.awt.event.ActionEvent;)V)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC2(java.awt.event.ActionEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.jButtonAdvanced_ActionPerformed(arg1);
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}

private void useTemplateCBPressed(ActionEvent e) {
   JCheckBox cb = (JCheckBox) e.getSource(); 
   if (cb.isSelected())
   {
       displayIsUsingTemplate();
   }
   else 
   {
       displayIsNotUsingTemplate();
   }
}
public void displayIsNotUsingTemplate() {
    removeAllColumns();
       setUpTemplateTable();
       getJComboBoxTemplate().setEnabled(false);
       getJButtonAdvanced().setEnabled(true);
       getJTable().getTableHeader().setReorderingAllowed(true);
}

public void displayIsUsingTemplate() {
    resetTemplateTable();
       getJComboBoxTemplate().setEnabled(true);
       getJButtonAdvanced().setEnabled(false);
       getJTable().getTableHeader().setReorderingAllowed(false);
};



/**
 * This method was created in VisualAge.
 * @param number int
 */
private void createColumn(String title, int prefWidth )
{	
	javax.swing.table.TableColumn tableColumn = new javax.swing.table.TableColumn();
	tableColumn.setHeaderValue( title );
	tableColumn.setPreferredWidth( prefWidth );
	tableColumn.setWidth(75);
	tableColumn.setMaxWidth(255);
	tableColumn.setMinWidth(30);
    getJTable().addColumn(tableColumn);
}
/**
 * Insert the method's description here.
 * Creation date: (4/3/00 10:14:08 AM)
 * Version: <version>
 */
private void createCustomColumns( ColumnEditorDialog editor )
{
	removeAllColumns();	
    columnData = new Vector(10);
        for ( int i = 0; i < editor.getEditorPanel().columnCount(); i++ )
    	{
    		
            columnData.addElement( new ColumnData( 
    						new Integer( i ),
    						new Object(),  // just a filler
    						editor.getEditorPanel().columnFieldData( i, ColumnData.COLUMN_WIDTH ),
    						editor.getEditorPanel().columnFieldData( i, ColumnData.COLUMN_TITLE ),
    						editor.getEditorPanel().columnFieldData( i, ColumnData.COLUMN_TYPE_NUMBER ) ) );
    		
    		createColumn( editor.getEditorPanel().columnFieldData( i, ColumnData.COLUMN_TITLE ), 
    					  Integer.parseInt( editor.getEditorPanel().columnFieldData( i, ColumnData.COLUMN_WIDTH ) ) );
    	}
    
    
}
/**
 * Insert the method's description here.
 * Creation date: (4/3/00 3:30:08 PM)
 * Version: <version>
 * @param displayNum java.lang.Long
 */
public void editInitialize( Long displayNumber ) 
{
    String query = new String
    ("select  d.title, d.typenum, d.width, c.name " +
     "from displaycolumns d, columntype c where d.displaynum = ? " +
     " and d.typenum = c.typenum order by ordering" );
    Object[] objs = new Object[1];
    objs[0] = displayNumber;
    Object[][] templates = DataBaseInteraction.queryResults( query, objs );
    if ( getJTable().getColumnCount() > 1 )
	{
        
        int template = DataModelUtils.getDisplayTemplate(displayNumber.longValue());
        if (template > -1)
        {
            getUseTemplateCB().setSelected(true);
            displayIsUsingTemplate();
        }
        else
        {
            getUseTemplateCB().setSelected(false);
            displayIsNotUsingTemplate();
        }
        
        initProperties(); 
        removeAllColumns();
	}


	
	if ( templates.length > 0 )
	{

		for( int i = 0; i < templates.length; i++ )
		{
			columnData.addElement( new ColumnData(
						new Integer( i ),
						templates[i][3],
						templates[i][2], // width
						templates[i][0].toString(), //title
						templates[i][1] ) ); // typenum
			
			createColumn( templates[i][0].toString(), Integer.parseInt( templates[i][2].toString() ) );
		}
	}

	return;
}
public void initProperties() {
    if (getUseTemplateCB().isSelected())
        setSelectedIndex();
    setCustomized();
}

private void setSelectedIndex() {
    EditDisplayDialog d = (EditDisplayDialog) SwingUtil.getParentDialog(this);
    Integer dispNum = new Integer (new Integer ((int)d.getCurrentDisplayNumber()));
    TemplateDisplayModel componentDataModel = getTempDispModel();
    componentDataModel.initModel(dispNum);
    Integer templateNum = componentDataModel.getTemplateNum();
    //special case when a template associated with a display has been deleted.
    Integer standardTemplate = 1;
    if (templateNum.equals( TemplateDisplay.INITVAL) ){

        getUseTemplateCB().setSelected(false);
        displayIsNotUsingTemplate();
        selectedTemplateIndex = standardTemplate;
    }
    else
    {
        selectedTemplateIndex = templateNums.indexOf(new BigDecimal (templateNum.intValue()));
    }
    
    getJComboBoxTemplate().setSelectedIndex(selectedTemplateIndex);
}
/**
 * Return the JButtonAdvanced property value.
 * @return javax.swing.JButton
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JButton getJButtonAdvanced() {
	if (ivjJButtonAdvanced == null) {
		try {
			ivjJButtonAdvanced = new javax.swing.JButton();
			ivjJButtonAdvanced.setName("JButtonAdvanced");
			ivjJButtonAdvanced.setToolTipText("Creates a display with your own columns");
			ivjJButtonAdvanced.setMnemonic('A');
			ivjJButtonAdvanced.setText("Advanced...");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJButtonAdvanced;
}
/**
 * Return the JComboBoxTemplate property value.
 * @return javax.swing.JComboBox
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
public javax.swing.JComboBox getJComboBoxTemplate() {
	if (ivjJComboBoxTemplate == null) {
		try {
			ivjJComboBoxTemplate = new javax.swing.JComboBox();
			ivjJComboBoxTemplate.setName("JComboBoxTemplate");
			ivjJComboBoxTemplate.setBackground(Color.white);
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
 * Return the JLabel1 property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getJLabelCustomized() {
	if (ivjJLabelCustomized == null) {
		try {
			ivjJLabelCustomized = new javax.swing.JLabel();
			ivjJLabelCustomized.setName("JLabelCustomized");
			ivjJLabelCustomized.setText("( Customized )");
			ivjJLabelCustomized.setForeground(Color.blue);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJLabelCustomized;
}
/**
 * Return the JLabelTemplate property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getJLabelTemplate() {
	if (ivjJLabelTemplate == null) {
		try {
			ivjJLabelTemplate = new javax.swing.JLabel();
			ivjJLabelTemplate.setName("JLabelTemplate");
			ivjJLabelTemplate.setText("Template");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJLabelTemplate;
}
/**
 * Return the JScrollPaneTable property value.
 * @return javax.swing.JScrollPane
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JScrollPane getJScrollPaneTable() {
	if (ivjJScrollPaneTable == null) {
		try {
			ivjJScrollPaneTable = new javax.swing.JScrollPane();
			ivjJScrollPaneTable.setName("JScrollPaneTable");
			ivjJScrollPaneTable.setVerticalScrollBarPolicy(javax.swing.JScrollPane.VERTICAL_SCROLLBAR_NEVER);
			ivjJScrollPaneTable.setHorizontalScrollBarPolicy(javax.swing.JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
			ivjJScrollPaneTable.setVisible(true);
			ivjJScrollPaneTable.setPreferredSize(new java.awt.Dimension(200, 203));
			ivjJScrollPaneTable.setEnabled(true);
			ivjJScrollPaneTable.setMinimumSize(new java.awt.Dimension(150, 60));
			getJScrollPaneTable().setViewportView(getJTable());
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJScrollPaneTable;
}
/**
 * Return the ScrollPaneTable property value.
 * @return javax.swing.JTable
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JTable getJTable() {
	if (ivjJTable == null) {
		try {
			ivjJTable = new javax.swing.JTable();
			ivjJTable.setName("JTable");
			getJScrollPaneTable().setColumnHeaderView(ivjJTable.getTableHeader());
			ivjJTable.setAutoscrolls(false);
			ivjJTable.setAutoResizeMode(javax.swing.JTable.AUTO_RESIZE_OFF);
			ivjJTable.setColumnSelectionAllowed(true);
			ivjJTable.setAutoCreateColumnsFromModel(false);
			ivjJTable.setBounds(0, 0, 200, 200);
			ivjJTable.setRowSelectionAllowed(false);
			ivjJTable.setEnabled(true);
			// user code begin {1}

			

			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJTable;
}
/**
 * Insert the method's description here.
 * Creation date: (4/3/00 12:18:01 PM)
 * Version: <version>
 * @return int
 */
public int getJTableColumnCount() 
{
	return getJTable().getColumnCount();
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
}
/**
 * Initializes connections
 * @exception java.lang.Exception The exception description.
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void initConnections() throws java.lang.Exception 
{
	// user code begin {1}

	initComboTemplate();
	
	// user code end
	getJComboBoxTemplate().addActionListener(ivjEventHandler);
	getJButtonAdvanced().addActionListener(ivjEventHandler);
    getJTable().getColumnModel().addColumnModelListener(templateColumnListener);
    getUseTemplateCB().addActionListener(ivjEventHandler);
}
/**
 * Initialize the class.
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void initialize() {
	try {
		// user code begin {1}

		if( templateNums == null )
			templateNums = new Vector( 10 );
			
		// user code end
		setName("TemplatePanel");
		setPreferredSize(new java.awt.Dimension(1099, 260));
		setLayout(new java.awt.GridBagLayout());
		setSize(478, 83);

		java.awt.GridBagConstraints constraintsJScrollPaneTable = new java.awt.GridBagConstraints();
		constraintsJScrollPaneTable.gridx = 1; constraintsJScrollPaneTable.gridy = 2;
		constraintsJScrollPaneTable.gridwidth = 3;
		constraintsJScrollPaneTable.fill = java.awt.GridBagConstraints.BOTH;
		constraintsJScrollPaneTable.weightx = 1.0;
		constraintsJScrollPaneTable.weighty = 1.0;
		constraintsJScrollPaneTable.insets = new java.awt.Insets(2, 2, 2, 2);
		add(getJScrollPaneTable(), constraintsJScrollPaneTable);


        java.awt.GridBagConstraints constraintsJChekcBoxTemp= new java.awt.GridBagConstraints();
        constraintsJChekcBoxTemp.gridx = 1; constraintsJChekcBoxTemp.gridy = 1;
        constraintsJChekcBoxTemp.anchor = java.awt.GridBagConstraints.NORTHWEST;
        constraintsJChekcBoxTemp.weightx = 1.0;
        constraintsJChekcBoxTemp.insets = new java.awt.Insets(9, 2, 7, 2);
        add(getUseTemplateCB(), constraintsJChekcBoxTemp);
        
        java.awt.GridBagConstraints constraintsJLabelTemplate = new java.awt.GridBagConstraints();
		constraintsJLabelTemplate.gridx = 2; constraintsJLabelTemplate.gridy = 1;
		constraintsJLabelTemplate.anchor = java.awt.GridBagConstraints.NORTHWEST;
		constraintsJLabelTemplate.ipadx = 12;
		constraintsJLabelTemplate.insets = new java.awt.Insets(9, 2, 11, 125);
		add(getJLabelTemplate(), constraintsJLabelTemplate);


		
        
        java.awt.GridBagConstraints constraintsJComboBoxTemplate = new java.awt.GridBagConstraints();
		constraintsJComboBoxTemplate.gridx = 2; constraintsJComboBoxTemplate.gridy = 1;
		constraintsJComboBoxTemplate.anchor = java.awt.GridBagConstraints.NORTHWEST;
		constraintsJComboBoxTemplate.weightx = 1.0;
		constraintsJComboBoxTemplate.insets = new java.awt.Insets(9, 60, 7, 2);
		add(getJComboBoxTemplate(), constraintsJComboBoxTemplate);

		java.awt.GridBagConstraints constraintsJButtonAdvanced = new java.awt.GridBagConstraints();
		constraintsJButtonAdvanced.gridx = 4; constraintsJButtonAdvanced.gridy = 1;
		constraintsJButtonAdvanced.anchor = java.awt.GridBagConstraints.NORTHEAST;
		constraintsJButtonAdvanced.insets = new java.awt.Insets(8, 47, 2, 4);
		add(getJButtonAdvanced(), constraintsJButtonAdvanced);

		java.awt.GridBagConstraints constraintsJLabelCustomized = new java.awt.GridBagConstraints();
		constraintsJLabelCustomized.gridx = 3; constraintsJLabelCustomized.gridy = 1;
		constraintsJLabelCustomized.anchor = java.awt.GridBagConstraints.NORTHWEST;
		constraintsJLabelCustomized.weightx = 1.0;
		constraintsJLabelCustomized.insets = new java.awt.Insets(10, 0, 10, 35);
		add(getJLabelCustomized(), constraintsJLabelCustomized);
		initConnections();
	} catch (java.lang.Throwable ivjExc) {
		handleException(ivjExc);
	}
	// user code begin {2}
	
	//setUpTemplateTable();
	setUnCustomized();
	initTemplateDisplay();

    
	// user code end
}
private void initTemplateDisplay() {
    if (parentFrame != null)
    {
        long currentDisp = ((TDCMainFrame)parentFrame).getCurrentDisplayNumber();
        boolean usingTemplate = DataModelUtils.getDisplayTemplate(currentDisp) > -1;    
        if (usingTemplate)
        {
            getUseTemplateCB().setSelected(true);
            displayIsUsingTemplate();
        }
        else
        {
            displayIsNotUsingTemplate();    
        }
    }
}
/**
 * Insert the method's description here.
 * Creation date: (4/4/00 11:35:23 AM)
 * Version: <version>
 * @return boolean
 */
private boolean isCustomized() 
{
	return getJLabelCustomized().getBackground() == getJLabelCustomized().getForeground();
}
/**
 * Comment
 */
public void jButtonAdvanced_ActionPerformed(java.awt.event.ActionEvent actionEvent) 
{
	java.awt.Dialog owner = com.cannontech.clientutils.CommonUtils.getParentDialog( this );

	java.awt.Cursor original = owner.getCursor();
	owner.setCursor( new java.awt.Cursor( java.awt.Cursor.WAIT_CURSOR ) );
	
	ColumnEditorDialog editor = new ColumnEditorDialog( owner, "Advanced Template", ColumnEditorDialog.DISPLAY_NO_OPTIONS, currentDisplayNumber );
	
	editor.getEditorPanel().setColumnData( columnData );
	editor.setSelectedItem( getJComboBoxTemplate().getSelectedItem() );
	
	try
	{
		editor.setModal( true );
		editor.setLocationRelativeTo( this );
		editor.show();

		if( editor.isDisplayable() )
		{
			try
			{
				// we have a new template here, so add its columns
				setCustomized();
				
				createCustomColumns( editor );
			}
			finally
			{
				editor.dispose();
			}	
		}
		
	}
	finally
	{
		owner.setCursor( original );
	}

	return;
}
/**
 * Comment
 * @throws ClassNotFoundException 
 */
public void jComboBoxTemplate_ActionPerformed(java.awt.event.ActionEvent actionEvent) 
{
    Frame owner = SwingUtil.getParentFrame(this);
    java.awt.Cursor original = owner.getCursor();
    owner.setCursor( new java.awt.Cursor( java.awt.Cursor.WAIT_CURSOR ) );  
    setUnCustomized();
    resetTemplateTable();
    owner.setCursor( original );
    
}




/**
 * main entrypoint - starts the part when it is run as an application
 * @param args java.lang.String[]
 */
public static void main(java.lang.String[] args) {
	try {
		javax.swing.JDialog frame = new javax.swing.JDialog();
		TemplatePanel aTemplatePanel;
		aTemplatePanel = new TemplatePanel();
		frame.setContentPane(aTemplatePanel);
		frame.setSize(aTemplatePanel.getSize());
		frame.addWindowListener(new java.awt.event.WindowAdapter() {
			public void windowClosing(java.awt.event.WindowEvent e) {
				System.exit(0);
			};
		});
		frame.setVisible(true);
	} catch (Throwable exception) {
		System.err.println("Exception occurred in main() of javax.swing.JPanel");
		com.cannontech.clientutils.CTILogger.error( exception.getMessage(), exception );;
	}
}
/**
 * Return the JLabelTitle property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void removeAllColumns() 
{

    if (columnData != null)
    {
        columnData.removeAllElements();
    	
    	int count = getJTable().getColumnCount();
    
    	for ( int i = 0; i < count; i++ )
    	{
    		getJTable().removeColumn( getJTable().getColumnModel().getColumn( 0 ) );
    	}
    }	
	return;
}
/**
 * Insert the method's description here.
 * Creation date: (4/3/00 4:21:49 PM)
 * Version: <version>
 * @param value long
 */
public void setCurrentDisplayNumber(long value) 
{
	currentDisplayNumber = value;	
}
/**
 * Insert the method's description here.
 * Creation date: (4/4/00 11:24:46 AM)
 * Version: <version>
 */
private void setCustomized() 
{
	getJLabelCustomized().setForeground( Color.BLUE );
	getJComboBoxTemplate().setBackground( Color.LIGHT_GRAY );
}

/**
 * Insert the method's description here.
 * Creation date: (4/4/00 11:24:46 AM)
 * Version: <version>
 */
private void setUnCustomized() 
{
	getJComboBoxTemplate().setBackground( Color.white );
	getJLabelCustomized().setForeground( getJLabelCustomized().getBackground() );
}
/**
 * Insert the method's description here.
 * Creation date: (3/31/00 12:17:48 PM)
 * Version: <version>
 */
public void resetTemplateTable() 
{
    removeAllColumns();

    int selectedIndex = getJComboBoxTemplate().getSelectedIndex();

    if( selectedIndex < 0 )
        return;


    BigDecimal templateNum = (BigDecimal) templateNums.elementAt(selectedIndex); 
	// set our template table accordingly
	String query = new String
		("select t.templatenum, t.title, t.typenum, t.ordering, t.width, c.name " + 
		 " from templatecolumns t, columntype c " +
		 " where t.templatenum = ? " +
		 " and t.typenum = c.typenum " +		 
		 " order by t.ordering");
	Object[] objs = new Object[1];
	objs[0] = templateNum.toString();
	
    Object[][] templates = DataBaseInteraction.queryResults( query, objs );
		
	setUpTemlateColumns(templates);
    
  }

private void setUpTemplateTable() 
{
    if( getJComboBoxTemplate().getSelectedIndex() < 0 )
        return;
        
    // set our template table accordingly

    String q = "select d.displaynum, d.title, d.typenum, d.ordering, d.width, c.name " +  
    "from displaycolumns d, columntype c  where d.displaynum = ? " +  
    "and d.typenum = c.typenum  order by d.ordering";
    
    Object[] objs = new Object[1];
    if (parentFrame != null)
    {
    TDCMainPanel mainPanel = ((TDCMainFrame) parentFrame).getMainPanel();
    int displayNumber = mainPanel.getCurrentDisplay().getDisplayNumber();
    objs[0] = new Integer (displayNumber); 

    Object[][] templates = DataBaseInteraction.queryResults( q, objs );
        
    setUpTemlateColumns(templates);
    }
   }


private void setUpTemlateColumns(Object[][] templates) {
    if ( templates.length > 0 )
	{
		if( columnData == null )
			columnData = new Vector( templates.length );
		
            for( int i = 0; i < templates.length; i++ )
    		{
    			columnData.addElement( new ColumnData( 
    						new Integer( i ),
    						templates[i][5],
    						templates[i][4],
    						templates[i][1].toString(),
    						templates[i][2] ) );
    			
    			createColumn( templates[i][1].toString(), Integer.parseInt( templates[i][4].toString() ) );
    		}
        

		getJTable().revalidate();
		getJTable().repaint();
	}
}
/**
 * 
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private static void getBuilderData() {
/*V1.1
**start of data**
	D0CB838494G88G88G98F954ACGGGGGGGGGGGG8CGGGE2F5E9ECE4E5F2A0E4E1F4E135BB8DF8D4551561CF02C58DEE30A94B7A2595EDF62515E23A35CABFB16BEE31E255DDDC2D325B3855CF7C163A29262E566C474D640770A3C4C10D0AA596A86026B1A0AA799193B2218486B2498CB190C2F819F949CC72E65E705E1B197C8CE44F39775DB7EFA6B321381A0F733DF74F39771EF34FBD675CF36E9B48566D19A505162D049416907D6F53C2C266FFC4C87F4CD7D50ECB91F3CA09696FFD00
	5708F43C981EE9C033E664141613B38DE550DEG6D7C39B925EF417B96525BE02D0117A4FC36814D794C5FBFB0B91FA5DD1C4FDA5A1E5D33911E1BG32G074FFC9179AF682950714BF5BC07C28BA1AB7A314F5DBD5BF45CC1E85B8152G9674337ECB60390EF21ED1F7D06FF7F6CEA6497F59EE4B2BB80FE9CE8E5B865858C67DD9C9EEABDCB5003CD6D1B9F14CB034978160620B48D53B8E403341BEB4F048A57B2ADD025FAF7BB5516529565C2EEA17A28A1A68762A81C998292BF30ABEF851C43B6097A53787
	672DB9D7C667A1248A5AF994F7EDAD724A027725G0F9978D3F508BF604DA93581383DFCFD1F6DEE50656F9A3B0DE4BF6AAF5A1CE4FD91AFDB5782EFF4FD69415F2DFD1B6E45B79D2873DC20958F6614D6GB4G1C8508G3CCBED60460E8F60595427B54BBE1F6CEF8A848ECBEE65046C96A5F82F2B830A0E6B90DD3262A6046D73F3BDD58964198C583802AB9E47F41275915F73059D371015379F372CCD201315D9B51674C1260B0503D11DB05E9F1FCB46FB79E02CCD46727E0218F9AF38CAAEAAC940BB7B11
	3D16DCFD1EFC936F1DC33857963A57DB613DD0378F065FC8719E931E59E7BA3599EC8F83ED6590370D2ECDDC96DB71BCD23E4F16D08F99B9CD16C6094950A5454BF29F65B9DDE2BCCB8CD9D6D0FC06894FE46926F8ECAF84DA39AF271459D766A974D78D5AE9GD9GCB81F2818AG4A7D5C461E697870AF34B13B20E80703DE49ADAA040D4DF43C89CFF1D8D304D63F2A89FE1758A9A8DE21CF92A18232BEFF6BC09D74450C7BBE35FD97707833689795C1734AFE18DD70091A28282C4F0F9C2FE19F74E920BA3D
	BE91987882044FFB37E38BBCA5C155CE855C6066519E0C7ED3876EC9C8680499E182F8E7FAF9209375D5836BBF8A50E56861309367BBAEAA90AD6A6AEC32BC948CB8F1A5A44B0FFA365158A1417BF4BF5771FDDFA0EEB5340BE5BE4F0873F553BCD62F8B459714916EE3E5F59813ABE4EEB34FF5F11B313EFAB329FA64D8C21BC1B8A0B3DB3149713E73BA1D9365E26DF935DDD768474920C21E68FB0C476DDF990F2284B8265FCFCF63DEE9A0C39A40A2C347594ECAE6330785D564D65A260001096E2846191C8F
	5615697AACB2E4FF1C621641FCF9B25719D057E56A77D52CEB224EC6A98F1C3F64EB6041F473F72770BC92C322DF330A81516F0697D5277D60945A1C6E2F7D32B166C7949FG7FEE8607D28D7CCFCE218F8A1A26F87B021A6890B54D6B9F08527F1172D203FD01E8E8603C161F42B54AE170EF237F432750B7DC325FAF52CDB2066874FF213EA9CA228F04EE1603FE0D61FF510976BDA40E6831615FBAB1598989D290638C4B8D96899BF08D0A5787DB0F210AF88268372EF395A69DD5G8EF31C86F85A835C8743
	BB388F6E9C7DB6D95A3B4B12688C493859EAD994E07EF0EFA06A03CC261FFF11D87695B4C668F815495A23E0CE94132098B69FB80373BC85733F0472DF60B6EF6FC2BA0FAB3A31B74B1244D4E9687833536538A76205208861988358F1C3EF56532F4751BAF59DB23A6DB46AD0B2453E559768B9D84FF33FBD50EE84B8FF0169B1738253836B7710516F20827A8588A82C5FA20575E36BFFE1AF6E894D63860510B0DC2D06412E6A6A0E19F23587E83CC53FA9D0F8CE76430BB4A7D3A1A783F02BFC7F9EFE1567E4
	69C319A47726F712661C9115491129C6772FF29A4F496E6CC61E39C04BD3395EFFFA5AFCAEB4292A6C7252A3C957D9680CF9FF99EE6C4CFAF876FB95D5EB85879C667A1DF2866545B3CBC76BE77847757C8CAF5640B68196E9089F2858EA12DD535859B39F1EC5F43E79757B51BF2981D43307DDD47AE545A73A7720E62B0530D6CDE3C1F593A537607BDE3E8FED6B9A61B988A7FF1FFCD89E6686595B94B715AD79DC75FBBB599EFCE7CB23AE67726F4FA38D63DBAC097262544D6FD9DE52F45D8723FB90D9DCDA
	347A5CABEB19BEDEB674A1C11F85C12E0FC7AE1A7531B24874D19C627AB85F707F534762F507D09F07031A861910BB04890D7B8A3541E37DBF9B79ECC5082D33AA94BDCB9BC3934FD516677D72922E5D99E2F95E86837FEB0A57CCF8262B3B28CFE0BB9DE8AF05F8EE52CAEB3A33611C52E100B400AC00E5G2B425C3657F5C339BAF96E578A75156297A4C7CC1067B9D8F7910B4BFA7B9F3ACBF8DCA6DC8F757571F9FD71F06C5AAB635AECDC6B04F13F0FEBDB634631BD9C105ECD9CDB38159396DFEE2CA85347
	1C9D66F1646291F39CD932F93FE5C312B8122E734E9E0E5AF015D15B3DF08E63E0BE504AC7F2CA77GD881BCG538132C6387ECF1C7D6CF27A27E665141B81F79462F2F37C59E968EB4D1754732B8D1753733211C4FA5E6CF8BB311E1977G0D69F963A554B36AB79757CEF369E7E9EC2F984DA9BDB04AF1B70FA26EA8344F9A3829914425C0BBE31467E9CFDF429830845A0F816483EC9D4BA92D99637497A85D866D4098BB2B709CCAB36877DC3CDC5E3AC41F530C4B5779550CA52EC5E3F57A78E5F99469BA09
	9FDB227B7265733C0E1E64F95E691E44F9DEEFCF62BCCF6CF90D61E18EF31547693E1E497340E0CF22BC70BD3A4E7B5E5CCC67D6D5D12546EC95B4C127BD233DC1926403A7219054F53405A8756B74BEAD1A5C3C4F5071CD95496E8B96419E5D1BE01F18EF0E1EC77BF88C6879G9BA3B925BB812C916E139B4EDDB6A6368CCF92937F67DC72FDA96ECD3CAF6BFBB1DFD16520629263F57FDAEF05C10BFAFF2BA67AD0CD2C4FBBF43CA6A88362043DFB1F0EE734E463BF68CD363FEC8FBEE9B06F41FEE38F9EA538
	876EC8D4574370BEDBEFE7C6A66E811B0FD0F9830A58AFAA1045BA3C23385FF991747937FB51673361FDE9041F6BCBC6B15F170491B928413C97315F3EB11E579F00368DC083C83D48F2829623FE90C6DF6AD7849FAC479190DDA4F28947FEAB42472E027E85G5BG5FGF0769267393B9E7368E38E1782B9B66625B4A1E6344AC3B87618CD6893A51EB47135559EC2DD9E0B4F290C35BB8E21FD9C0B4DB1F45A3FEFAFB766ED8E2A1A6C834D38D99D33E75CDC475CFB092DB38F1E4563BCBF3A2B431C9FED9BE7
	FD9A8DFA28B6915DB94E636D1D74CC74C0BB4540FDFA02476074F16EA3D34FA73771D967935B78F56793471E3967A70FBDFFF3BE19EDB2791EA3A85FD3A0DB01A17329C2441525403E26F05C0D94D7836DC6833726102F4D863879A9F8073EE00098FB752DD13CC7770B61CAA36533FAA14F478B25BD7BEAEAEB2BAA77FFD2836923FE971999B65FFD9FDC7FE942BA92A1B7C53F0B541FCBE66E50733953E37C6E3BEA8A54B8D338AD6FBB0FB905F15E22B0F5F5FFD63C6E4342000DFA86BBEF3FBD466FBB66CF61
	F55303F44E9550DE8DD0BC1567BB43FF64794E42DB6691DB4A7104F5D32EF33FE55BD47D2EFEEAB45F4902FC07713CBB42F9C6001EBD15662B91332D21BE3F8BD00D3ECD695BE3F2F95C639DB1747698BA4A7B13A2B3FDDA8C9DF534CB9F4F56F67FAE3E36027B330874435A24B5E181DF53B47634F76EA0BB2FFEE52D1ECF363CC79266B95148C0F82EF3FE9C75003E51B50D6B7E4593287BA550DE8130FA9A5F4F977A135440547B59FC4FF7709C35F8BA588245FD2F1657458D53F92CFA38826B4DBE41B5B420
	48C13F1B4752E729BFEB50AF85E045F4441DBCE1CD208373BE19A19346E4836C52F537ECBA331D5F2ED9330671AEA944982B49326474861C60770479513A41289FC9544FFCFE918ED42F0B49B60D622681EC1B4157314B0177BC9ACC4266289A42B9185CEFDAA613FBED1C5C593A5CEFF3DF829E3C9EE57671E03759D74B673DEFA9CCE29F87E7B03B304E085ABE4BA5FEAF619A06811FBE034746E7877E971E1D42201EB934383D1A2C50548110F6C58E080AB642307476C5BFCBFB473E5CB79B5F179CF74B950F
	E3F5E8F3846DB9FB862BC38B8D3C0D62171A70AC1EBDE2443365C02B390A5727EDBE347FF4E8AF82D886108BC0E6C24C1E49F5395F031FC0A74D451CF240868711E4BABA587A6E0DFC39EFD9772C65F1A37B4E6F10BB2EFA39B0D98DD8B3D30F977AF315F307A94F7C8FBF2EAF85E873811682BC86309A20B8152FEF54D3455607665024E9024BC3EF117862BA3DE2B80A4684062DCD9C6775C5854CD5159A1BE7B526A62ED11C89709E40C5G52CCB4361F03A7F15F962446FF9353A8BEBBB57E1BD841897ECD6C
	DE206D19456B2D7F6EC8348F33E769B9622CE45F979657A69A172D77DF864F15F1E3D94CD8F901909EF7BEBC01E704F1F6B74B122CD407BDDECD5CCE6904668E6CAF6F7309B8BE679B74FBBE330FA7B7F27B58F66BDF936B23AF976EC8E29FF93AFC0DD71B3EEF8F962CE5324DAB44676E265DBA0FF10D9836D39544F7E6A301A10C3D896389D848B1D8DBDB5FA0A475CEE1409833C3C2FEC64E44420CCE7BC0C21DF87DDE4DABC85ED1FA1569000E7E817D5EA5C20CBD68B03DBFB176D5FD6FC99E13723FD69EEC
	7DDD3E6439EB2FAFF16EAA7A92672EC33E496BE255177C7B48284F7CFD046DFBED8877E6986CA59D608E0065G39D773F8D1667F832171A2C12CE8F18FE0FD08DFE1B54E7FBF7D49577A3C7F725FEEDF71533CC206D2450773F3FC210C7B2549810AE438D299F524F887BC9A6B2A63B74A743B2958CF51AC6F1EC9732482D8D765B7F8AE7ECB0ABBG6D33862E0E7AE2G5A538D5CB4426F0EB28CDC46A84F63979A381F8FD29E50AE1F4DF1EF8DA1EEA7349BE6737894B809B9075DE80F52365BE8DBCEA633C99C
	7355587D3BC9F9FC955FA2595AB7B9F87D13EA68C3F598351D013B0F6617CFC1BB5F40D55771FE649A0E5B3885F1F6E8FB2E61E77AB7431823C5201D813084E08540AAGF2EDCE69D600BDG8DGCEG89A085A09DA08B608E0065G3957F2FBBF23E2ED15AC17520B9FEA740A60C53357ED2EA9145C6E0F06AEEF776DA17ACD876C55B992506FC8BAC3F887A177772B2261DF5D21F283572A5AE5550B511445779B4771F7DCA1C8763CAEC1E24542E1C1314B1257B5226767BF26B60F79E27E35BC87FEF19CF569
	11956F286C57A60E64397D6B548F707E3866BABE361D5AD3482BFA715B9D6B67A17CB7CF9A74E3380594B79D5AE957713A6036514B5585A5A41AE7AF01F18F813C21371F02776F96315F1DB1D91E9E443E229F956B56E5A99864326499B2D78FE1FE650383499774511BA056778875D51216D33AB10D77AD78G7760987BB429FF5F3D752479DE64A074453B5F33708C24711ADEC53569F544550EAB29FF32636A1F5434E8CD41E44A2D0D2DD9EE980D72EA291B0CD7C99C2F7434D8DED9E951DC1A715A3105FED7
	1495B1364E2B273E188D7D1726F15F39B51C5C879607AF6F83F70593477EFB4293E37FBF0593477E65E1F36C57FF839046F58F006FF6997D7E55401DB6F0CF50B9917705572DF98C7C13D4AE449F9511A147BFC3658AD3DC170EDBED421D56F13F89238D411241457858974338077DDEC9E26D5F51BEDEBF3835210FB5D4C6EFC0B049DDBC4CF1270DBDFAE89CEB96D3C9DFED3AA9286EEC69F036B6B759FA9D4D9DEDB65B61260E5E13AD00E4E37712CFA699FB342D2375CC5BC9E74C68A687CC50E2ED31F27E63
	045A23929419BD7EF80C9832DDB3DAC9125EE76D2F29292A5CDF73515E3FACB74E7B1C4DDFF9B1BA7F1621D85E4D172234273B36A7673D6FE3600DB7E94CA78647125C0DD04FE777A03FBA496FDAF25378DDCBD98756A056B9607FG5A9C6E7F7D63A8C772B54C22AD767EDF05D119355AADE4C27D50972D59D751F3FBB670491C23FF8F1C13BCA7D8B689EDA55072G0A2F0F2F43A2F4CD955747575BB3A8FE6775717576268EDEEF57GAD7DFAFEDE2FEF43F53F846D723FB26EC9A9EEA7349B8DDC9645B921BD
	EC60FECBF133211DED602CF61E93ACB3F0E2BB7DBD9C34B726F35C62F61E07D526731CEAEA876A183DFF43743E35636B2FB17E743572E0F673FE9B1EE9C2D013D56A3E2A9E27EB5B78BD6901F4FE26C9ED1B743E9D228A479F5EEE7038BE4368FF5468FFF79B46EE17AC85FDFE07287F7A27C9126430E81C870F3771F3F6589837517E0E4E27197ECE3A194E209ED1E49F3DD061E37758B94FD4984B3E17F45B7999BB9FF00BG9E824883A81F4B670F34232FA8F2F802D0EC5E9B5A793CB5F3F99E72C4BB1753EA
	4CE3EF439850AFC8EA741B497C36BF103838C8A3C1F553A9E7DBEFC70B2375CCCBEF5B11A33C7FEF5AF89CE26D4F6D66B9D97C18E22C53B9175FD95FE60F3F331ED3E83EF3FE529EFFE70D632374AD6D71F7563163DB5A636F2C79F8A667775AA72E7D79F63E96E63FD7592F74DE0B7DFF02F92D4D96C2967E1A14288AF9FDE28CEE3A76970FAE6C35EE3590CBABA97550B33315140471709D8E0024A0C671F10D6CE66623D68A157ACF6DAB43DE4DD3B9A28715CAA8E7AB19E92A9A59403FEED5D7D7834FCD31F1
	347066372615DF77D150C24A0E10D2D8F340A3GBF7A63919B5950C73389D1013E9AA927BF39799046FEC847565B0845859C5E0D62DD08E7D4C7C0F00950E51DDBD006688C9748A5D252CD5675AB83FD1401C6AC5DC73A01ADEDA1F92D0D2C57BCD8C1E3AA27113722D35B3136508975288AC25640DF007E4142B69B8BEB2599C3EE68233A22DF5DD4E4A14520C916C13512E2CC03E0018AD3EC91E4D0F4AEFA138FE0C10BD1D11475FA8BFF170DFBDF2611B27E99D1A31B76D4C6BF8DD66E25A3706FFE00E5B163
	8DB07AA01F9B0CBE9BF47A45984E6B897B43DB2D7594DBCCD78C7F580A75FB303D63667B7317E6D4A47D56151A414E347999717767AE9B7A60E3004F4F60F96B6D477FC4125FF7B9FC02A4C1D5B45464F7635D576191CD0C5E37F8ECB83F4A7B68C4C613ED6B749842477C9FD0CB8788649BD3874A93GG44B3GGD0CB818294G94G88G88G98F954AC649BD3874A93GG44B3GG8CGGGGGGGGGGGGGGGGGE2F5E9ECE4E5F2A0E4E1F4E1D0CB8586GGGG81G81GBAGGG8494
	GGGG
**end of data**/
}


public Vector getTemplateNums() {
    return templateNums;
}


public void jTableModel_ColumnMoved(TableColumnModelEvent e) 
{
        swap( columnData, e.getFromIndex(), e.getToIndex());
}

public void swap( Vector vector, int source, int dest) {

    Object temp = new Object();

    if ( source != dest &&
         source >= 0 && source < vector.size() &&
         dest >= 0 && dest < vector.size() )
    {

        temp = vector.elementAt( source );
        vector.setElementAt( vector.elementAt( dest ), source );
        vector.setElementAt( temp, dest );
    }
    
}
public JCheckBox getUseTemplateCB() {
    if (useTemplateCB == null) {
        try {
            useTemplateCB = new javax.swing.JCheckBox();
            useTemplateCB.setName("UseTemplateCheckBox");
            useTemplateCB.setFont(new java.awt.Font("dialog", 0, 12));
            useTemplateCB.setText("Use Template");
            useTemplateCB.setMargin(new java.awt.Insets(0, 0, 0, 0));
            // user code begin {1}
            // user code end
        } catch (java.lang.Throwable ivjExc) {
            // user code begin {2}
            // user code end
            handleException(ivjExc);
        }
    }
    return useTemplateCB;

}

public boolean isTableEditable () 
{
    return ! getUseTemplateCB().isSelected();
}

public TemplateDisplayModel getTempDispModel() {
    if (tempDispModel   == null)
    {
        tempDispModel = new TemplateDisplayModel();
    }
    return tempDispModel;
}
}
