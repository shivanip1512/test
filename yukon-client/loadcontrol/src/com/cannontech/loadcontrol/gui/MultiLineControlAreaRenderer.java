package com.cannontech.loadcontrol.gui;

/**
 * Insert the type's description here.
 * Creation date: (7/17/2001 11:56:30 AM)
 * @author: 
 */
import com.cannontech.loadcontrol.datamodels.ControlAreaTableModel;

public class MultiLineControlAreaRenderer extends javax.swing.JPanel implements javax.swing.table.TableCellRenderer 
{
	private java.awt.Color borderColor = new java.awt.Color(125,50,180);
	private java.text.DecimalFormat numberFormatter = null;

	private int rowHeight = 32; //used to remember the last RowHeight
	private javax.swing.JLabel ivjJLabelTrigger = null;
	//private javax.swing.JLabel ivjJLabelMethod = null;
	//private javax.swing.JLabel ivjJLabelState = null;
	private java.awt.Font boldFont = null;
	private java.awt.Font plainFont = null;
	private javax.swing.JLabel ivjJLabelText = null;
/**
 * MultiLineGearRenderer constructor comment.
 */
public MultiLineControlAreaRenderer() {
	super();
	initialize();

	numberFormatter = new java.text.DecimalFormat();
	numberFormatter.setMaximumFractionDigits( 3 );
	numberFormatter.setMinimumFractionDigits( 1 );
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
 * Return the JLabelGearName property value.
 * @return javax.swing.JLabel
 */

private javax.swing.JLabel getJLabelTrigger() 
{
	if( ivjJLabelTrigger == null )
	{
		try 
		{
			ivjJLabelTrigger = new javax.swing.JLabel();
			ivjJLabelTrigger.setName("JLabelTrigger");
			ivjJLabelTrigger.setText("TRIGGER");
			ivjJLabelTrigger.setBounds(0, 15, 67, 14);
		} 
		catch (java.lang.Throwable ivjExc) 
		{
			handleException(ivjExc);
		}
	}

	return ivjJLabelTrigger;
}

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

	
	if( value instanceof com.cannontech.loadcontrol.data.LMControlArea )
	{
		processLMControlArea( (com.cannontech.loadcontrol.data.LMControlArea)value, table );
	}
	else
	{  
		add(getJLabelText());

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

	}


	if( model instanceof ControlAreaTableModel )
		handleControlAreaTableModel( 
			(ControlAreaTableModel)model, row, column, table, isSelected);
		

	
	return this;
}
/**
 * Insert the method's description here.
 * Creation date: (1/8/2002 12:21:57 PM)
 * @return java.lang.String
 * @param trigger com.cannontech.loadcontrol.data.LMControlAreaTrigger
 */
private String getTriggerString(com.cannontech.loadcontrol.data.LMControlAreaTrigger trigger) 
{
	if( trigger == null )
		return null;

	com.cannontech.database.data.lite.LitePoint point = com.cannontech.database.cache.functions.PointFuncs.getLitePoint(
		trigger.getPointId().intValue() );

	if( trigger.getTriggerType().equalsIgnoreCase(
		   com.cannontech.database.db.device.lm.LMControlAreaTrigger.TYPE_STATUS) )
	{
		return com.cannontech.database.cache.functions.StateFuncs.getLiteState( point.getStateGroupID(), trigger.getPointValue().intValue() ).getStateText() +
			" / " +
			com.cannontech.database.cache.functions.StateFuncs.getLiteState( point.getStateGroupID(), trigger.getThreshold().intValue() ).getStateText();
	}	
	else
	{
		//com.cannontech.database.db.device.lm.LMControlAreaTrigger.TYPE_THRESHOLD
		return numberFormatter.format(trigger.getPointValue()) +
			" / " +
			numberFormatter.format(trigger.getThreshold());
		
	}


}
/**
 * Insert the method's description here.
 * Creation date: (8/23/00 1:51:05 PM)
 * @param model ControlAreaTableModel
 * @param row int
 * @param column int
 * @param table javax.swing.JTable
 */
private void handleControlAreaTableModel(ControlAreaTableModel model, int row, int column, javax.swing.JTable table, boolean isSelected ) 
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

private void initialize() 
{
	try {
		setName("MultiLineGearRenderer");
		setLayout(null);
		setSize(163, 32);
		add(getJLabelTrigger(), getJLabelTrigger().getName());
		//add(getJLabelState(), getJLabelState().getName());
		//add(getJLabelMethod(), getJLabelMethod().getName());
		add(getJLabelText(), getJLabelText().getName());
	} catch (java.lang.Throwable ivjExc) {
		handleException(ivjExc);
	}

	setLayout( new javax.swing.BoxLayout(this, javax.swing.BoxLayout.Y_AXIS) );
	setOpaque(true);
	setBorder( new javax.swing.border.EmptyBorder(1,2,1,2) );
	
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
		exception.printStackTrace(System.out);
	}
}
/**
 * Insert the method's description here.
 * Creation date: (7/18/2001 5:03:03 PM)
 */
private void processLMControlArea( com.cannontech.loadcontrol.data.LMControlArea value, javax.swing.JTable table )
{
	add(getJLabelText());
	String trigStr = null;
	String text = null;

	if( value.getTriggerVector().size() > 0 )
	{
		for( int i = 0; i < value.getTriggerVector().size(); i++ )
		{
			com.cannontech.loadcontrol.data.LMControlAreaTrigger trigger = 
					(com.cannontech.loadcontrol.data.LMControlAreaTrigger)value.getTriggerVector().get(i);
			
			if( table.getRowHeight() != rowHeight )
				table.setRowHeight( rowHeight );

			if( trigger.getTriggerNumber().intValue() == 1 )
				trigStr = getTriggerString(trigger);
			else if( trigger.getTriggerNumber().intValue() == 2 )
				text = getTriggerString(trigger);
			else
				System.out.println("**** ControlArea '" + value.getYukonName() +"' has more than 2 Triggers defined for it.");
			
		}
	}
	else
	{
		 trigStr = "(No Triggers Found)";
	}

	getJLabelTrigger().setText( text ); //trigger 2 text
	getJLabelText().setText( trigStr );  // trigger 1 text
	
	//always add this JLabel
	add(getJLabelTrigger());	
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
