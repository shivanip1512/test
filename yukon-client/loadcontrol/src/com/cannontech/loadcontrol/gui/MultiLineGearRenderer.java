package com.cannontech.loadcontrol.gui;

/**
 * Insert the type's description here.
 * Creation date: (7/17/2001 11:56:30 AM)
 * @author: 
 */

import com.cannontech.loadcontrol.datamodels.ProgramTableModel;

public class MultiLineGearRenderer extends javax.swing.JPanel implements javax.swing.table.TableCellRenderer 
{
	private java.awt.Color borderColor = new java.awt.Color(125,50,180);

	private int rowHeight = 32; //used to remember the last RowHeight
	private javax.swing.JLabel ivjJLabelGearName = null;
	private javax.swing.JLabel ivjJLabelMethod = null;
	private javax.swing.JLabel ivjJLabelState = null;
	private java.awt.Font boldFont = null;
	private java.awt.Font plainFont = null;
	private javax.swing.JLabel ivjJLabelText = null;
	
	public static final String STRING_ARROW = "  ->";
	
/**
 * MultiLineGearRenderer constructor comment.
 */
public MultiLineGearRenderer() {
	super();
	initialize();
}
/**
 * MultiLineGearRenderer constructor comment.
 * @param layout java.awt.LayoutManager
 */
public MultiLineGearRenderer(java.awt.LayoutManager layout) {
	super(layout);
}
/**
 * MultiLineGearRenderer constructor comment.
 * @param layout java.awt.LayoutManager
 * @param isDoubleBuffered boolean
 */
public MultiLineGearRenderer(java.awt.LayoutManager layout, boolean isDoubleBuffered) {
	super(layout, isDoubleBuffered);
}
/**
 * MultiLineGearRenderer constructor comment.
 * @param isDoubleBuffered boolean
 */
public MultiLineGearRenderer(boolean isDoubleBuffered) {
	super(isDoubleBuffered);
}
/**
 * Return the JLabelGearName property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getJLabelGearName() {
	if (ivjJLabelGearName == null) {
		try {
			ivjJLabelGearName = new javax.swing.JLabel();
			ivjJLabelGearName.setName("JLabelGearName");
			ivjJLabelGearName.setText("NAME");
			ivjJLabelGearName.setBounds(0, 15, 67, 14);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJLabelGearName;
}
/**
 * Return the JLabelMethod property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getJLabelMethod() {
	if (ivjJLabelMethod == null) {
		try {
			ivjJLabelMethod = new javax.swing.JLabel();
			ivjJLabelMethod.setName("JLabelMethod");
			ivjJLabelMethod.setFont(new java.awt.Font("dialog", 0, 12));
			ivjJLabelMethod.setText("METHOD");
			ivjJLabelMethod.setBounds(69, 15, 94, 14);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJLabelMethod;
}
/**
 * Return the JLabelState property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getJLabelState() {
	if (ivjJLabelState == null) {
		try {
			ivjJLabelState = new javax.swing.JLabel();
			ivjJLabelState.setName("JLabelState");
			ivjJLabelState.setFont(new java.awt.Font("dialog", 0, 12));
			ivjJLabelState.setText("STATE");
			ivjJLabelState.setBounds(69, 1, 94, 14);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJLabelState;
}
/**
 * Return the JLabelText property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getJLabelText() {
	if (ivjJLabelText == null) {
		try {
			ivjJLabelText = new javax.swing.JLabel();
			ivjJLabelText.setName("JLabelText");
			ivjJLabelText.setText("TEXT");
			ivjJLabelText.setBounds(0, 1, 67, 14);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJLabelText;
}
/**
 */
public java.awt.Component getTableCellRendererComponent(final javax.swing.JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) 
{
	javax.swing.table.TableModel model = table.getModel();
	removeAll();
	invalidate();

	// do anything that only needs to be assigned once per repainting here
	if( row == 0 && column == 0 )
	{
		rowHeight = (table.getFont().getSize()+5) * 2; //allow for 2 lines of text
		boldFont = new java.awt.Font( table.getFont().getName(), java.awt.Font.BOLD | java.awt.Font.ITALIC, table.getFont().getSize() );
		plainFont = new java.awt.Font( table.getFont().getName(), java.awt.Font.PLAIN, table.getFont().getSize() );
	}

	if( isSelected )
	{				
		//Each border is a newly allocated object (uses the new operator)
		//  but, we only create at most the number of columns the table has
		if( column == 0 )
			setBorder( javax.swing.BorderFactory.createMatteBorder( 2, 2, 2, 0, borderColor) );
		else if( column == (table.getModel().getColumnCount()-1) )
			setBorder( javax.swing.BorderFactory.createMatteBorder( 2, 0, 2, 2, borderColor) );
		else
			setBorder( javax.swing.BorderFactory.createMatteBorder( 2, 0, 2, 0, borderColor) );

	}
	else
	{
		// javax.swing.BorderFactory.createEmptyBorder() returns a single instance
		//   of an empty border, so performance is not degrated by the new operator
		setBorder( javax.swing.BorderFactory.createEmptyBorder() );
	}

	
	if( value instanceof com.cannontech.messaging.message.loadcontrol.data.ProgramDirect )
	{
		processDirectProgram( (com.cannontech.messaging.message.loadcontrol.data.ProgramDirect)value, table );
	}
	else if( value instanceof com.cannontech.messaging.message.loadcontrol.data.ProgramCurtailment )
	{
		processCurtailmentProgram( (com.cannontech.messaging.message.loadcontrol.data.ProgramCurtailment)value, table );
	}
	else
	{  //must be some other LMProgram not handled yet, just add it like its a string
		//add( javax.swing.Box.createVerticalGlue() );
		add(getJLabelText());
		//add( javax.swing.Box.createVerticalGlue() );
	}

	if( model instanceof ProgramTableModel )
		handleProgramTableModel( (ProgramTableModel)model, row, column, table, isSelected );
	
	if( value != null )
	{
		getJLabelText().setText( value.toString() );
		((javax.swing.JComponent)this).setToolTipText( value.toString() );
	}
	else
	{
		getJLabelText().setText( "" );
		((javax.swing.JComponent)this).setToolTipText( "" );
	}

	
	return this;
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
 * Insert the method's description here.
 * Creation date: (8/23/00 1:51:05 PM)
 * @param model CapBankTableModel
 * @param row int
 * @param column int
 * @param table javax.swing.JTable
 */
private void handleProgramTableModel(ProgramTableModel model, int row, int column, javax.swing.JTable table, boolean isSelected ) 
{
	setBackground( model.getCellBackgroundColor( row, column ));
	
	if( isSelected )
	{
		setForeground( model.getCellForegroundColor( row, column ).brighter());		
		setFont( boldFont );
	}
	else
	{ 
		setForeground( model.getCellForegroundColor( row, column ));		
		setFont( plainFont );		
	}	
}
/**
 * Initialize the class.
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void initialize() {
	try {
		// user code begin {1}
		// user code end
		setName("MultiLineGearRenderer");
		setLayout(null);
		setSize(163, 32);
		add(getJLabelGearName(), getJLabelGearName().getName());
		add(getJLabelState(), getJLabelState().getName());
		add(getJLabelMethod(), getJLabelMethod().getName());
		add(getJLabelText(), getJLabelText().getName());
	} catch (java.lang.Throwable ivjExc) {
		handleException(ivjExc);
	}
	// user code begin {2}

	setLayout( new javax.swing.BoxLayout(this, javax.swing.BoxLayout.Y_AXIS) );
	setOpaque(true);
	setBorder( new javax.swing.border.EmptyBorder(1,2,1,2) );
	
	// user code end
}
/**
 * main entrypoint - starts the part when it is run as an application
 * @param args java.lang.String[]
 */
public static void main(java.lang.String[] args) {
	try {
		javax.swing.JFrame frame = new javax.swing.JFrame();
		MultiLineGearRenderer aMultiLineGearRenderer;
		aMultiLineGearRenderer = new MultiLineGearRenderer();
		frame.setContentPane(aMultiLineGearRenderer);
		frame.setSize(aMultiLineGearRenderer.getSize());
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
		System.err.println("Exception occurred in main() of javax.swing.JPanel");
		com.cannontech.clientutils.CTILogger.error( exception.getMessage(), exception );;
	}
}
/**
 * Insert the method's description here.
 * Creation date: (7/18/2001 5:03:03 PM)
 */
private void processCurtailmentProgram( com.cannontech.messaging.message.loadcontrol.data.ProgramCurtailment value, javax.swing.JTable table )
{
	add(getJLabelText());
		
	com.cannontech.messaging.message.loadcontrol.data.ProgramCurtailment dPrg = value;
	
	if( table.getRowHeight() != rowHeight )
		table.setRowHeight( rowHeight );

	getJLabelGearName().setText(
			STRING_ARROW + "Notify Time: " + (int)(value.getMinNotifyTime() / 60) + "(minutes)");
	
	add(getJLabelGearName());	
}
/**
 * Insert the method's description here.
 * Creation date: (7/18/2001 5:03:03 PM)
 */
private void processDirectProgram( com.cannontech.messaging.message.loadcontrol.data.ProgramDirect value, javax.swing.JTable table ) 
{
	add(getJLabelText());
		
	if( value.getDirectGearVector().size() > 0 )
	{
		com.cannontech.messaging.message.loadcontrol.data.ProgramDirect dPrg = value;
		
		if( table.getRowHeight() != rowHeight )
			table.setRowHeight( rowHeight );

		
		boolean found = false;
		com.cannontech.messaging.message.loadcontrol.data.ProgramDirectGear gear = null;
		
		//get the current gear we are in
		for( int i = 0; i < dPrg.getDirectGearVector().size(); i++ )
		{			
			gear = (com.cannontech.messaging.message.loadcontrol.data.ProgramDirectGear)dPrg.getDirectGearVector().get(i);

			if( dPrg.getCurrentGearNumber().intValue() == gear.getGearNumber())
			{
				getJLabelGearName().setText(
						STRING_ARROW + gear.getGearName() );
				found = true;
				break;
			}			
		}

		if( !found )
		{
			getJLabelGearName().setText(
				STRING_ARROW + "(Gear #" + gear.getGearNumber() + " not Found)");

			com.cannontech.clientutils.CTILogger.info("*** For DirectProgram: " + dPrg.getYukonName() + ", gear #" + gear.getGearNumber() + " was not found.");
		}
		
		add(getJLabelGearName());
	}
	else
	{
		getJLabelGearName().setText(
			STRING_ARROW + "(No Gears Found)");

		add(getJLabelGearName());
	}

	
}
/**
 * Insert the method's description here.
 * Creation date: (7/18/2001 10:49:47 AM)
 * @param frgnd java.awt.Color
 */
public void setFont(java.awt.Font font) 
{
	super.setFont( font );
	for( int i = 0; i < getComponents().length; i++ )
	{
		if( getComponents()[i] instanceof javax.swing.JLabel )
			getComponents()[i].setFont( font );
	}

}
/**
 * Insert the method's description here.
 * Creation date: (7/18/2001 10:49:47 AM)
 * @param frgnd java.awt.Color
 */
public void setForeground(java.awt.Color frgnd) 
{
	super.setForeground( frgnd );
	for( int i = 0; i < getComponents().length; i++ )
	{
		if( getComponents()[i] instanceof javax.swing.JLabel )
			getComponents()[i].setForeground( frgnd );
	}

}
}
