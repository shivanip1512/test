package com.cannontech.loadcontrol.gui;

/**
 * Insert the type's description here.
 * Creation date: (7/17/2001 11:56:30 AM)
 * @author: 
 */
import java.awt.BorderLayout;
import javax.swing.JTable;

import com.cannontech.database.cache.functions.PointFuncs;
import com.cannontech.database.cache.functions.StateFuncs;
import com.cannontech.database.data.lite.LitePoint;
import com.cannontech.database.data.lite.LiteState;
import com.cannontech.database.db.device.lm.IlmDefines;
import com.cannontech.loadcontrol.data.LMControlArea;
import com.cannontech.loadcontrol.datamodels.ControlAreaTableModel;

public class MultiLineControlAreaRenderer extends javax.swing.JPanel implements javax.swing.table.TableCellRenderer 
{
	private static final java.awt.Color BORDER_COLOR = new java.awt.Color(125,50,180);
	private static final java.text.DecimalFormat NMBR_FORMATTER;


	private int rowHeight = 32; //used to remember the last RowHeight
	private java.awt.Font boldFont = null;
	private java.awt.Font plainFont = null;

	private javax.swing.JLabel ivjJLabelText = null;
	private javax.swing.JLabel ivjJLabelTrigger = null;



	static
	{
		NMBR_FORMATTER = new java.text.DecimalFormat();
		NMBR_FORMATTER.setMaximumFractionDigits( 3 );
		NMBR_FORMATTER.setMinimumFractionDigits( 1 );
	}

	/**
	 * MultiLineControlAreaRenderer constructor comment.
	 */
	public MultiLineControlAreaRenderer() 
	{
		super();
		initialize();	
	}
	
	/**
	 * Return the JLabelText property value.
	 * @return javax.swing.JLabel
	 */
	private javax.swing.JLabel getJLabelText() {
		if (ivjJLabelText == null) {
			try {
				ivjJLabelText = new javax.swing.JLabel();
				ivjJLabelText.setName("JLabelText");
				ivjJLabelText.setText("TEXT");
				ivjJLabelText.setBounds(0, 0, 80, 14);
				
				//ivjJLabelText.setBorder( javax.swing.BorderFactory.createMatteBorder( 
				//		1, 1, 1, 1, borderColor) );
			} 
			catch (java.lang.Throwable ivjExc) {
	
				handleException(ivjExc);
			}
		}
		return ivjJLabelText;
	}
	
	/**
	 * Return the JLabelTrigger property value.
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
				ivjJLabelTrigger.setBounds(0, 0, 80, 14);
	
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
		//removeAll();
		//invalidate();
	
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
				setBorder( javax.swing.BorderFactory.createMatteBorder(2, 2, 2, 0, BORDER_COLOR) );
			else if( column == (table.getModel().getColumnCount()-1) )
				setBorder( javax.swing.BorderFactory.createMatteBorder(2, 0, 2, 2, BORDER_COLOR) );
			else
				setBorder( javax.swing.BorderFactory.createMatteBorder(2, 0, 2, 0, BORDER_COLOR) );
	
		}
		else
		{
			// javax.swing.BorderFactory.createEmptyBorder() returns a single instance
			//   of an empty border, so performance is not degrated by the new operator
			//setBorder( javax.swing.BorderFactory.createEmptyBorder() );
			setBorder( null );
		}
	
		
		if( value instanceof LMControlArea )
		{
			processLMControlArea(
				(LMControlArea)value,
				table,
				column );
		}
		else
		{  
			//add(getJLabelText());
	
			if( value != null )
			{
				getJLabelText().setText( value.toString() );
				getJLabelText().setToolTipText( value.toString() );
				((javax.swing.JComponent)this).setToolTipText( value.toString() );
			}
			else
			{
				getJLabelText().setText( "" );
				getJLabelText().setToolTipText( "" );
				((javax.swing.JComponent)this).setToolTipText( "" );
			}
	
			getJLabelTrigger().setText( "" );
			getJLabelTrigger().setToolTipText( "" );
		}
	
	
		if( model instanceof ControlAreaTableModel )
			handleControlAreaTableModel( 
				(ControlAreaTableModel)model, row, column, table, isSelected);
			
	
		setCellAlignment( table, column );
		return this;
	}
	
	private void setCellAlignment( JTable table, int column )
	{
		
		if( table.convertColumnIndexToModel(column) == ControlAreaTableModel.PRIORITY
			 || table.convertColumnIndexToModel(column) == ControlAreaTableModel.VALUE_THRESHOLD
			 || table.convertColumnIndexToModel(column) == ControlAreaTableModel.PEAK_PROJECTION
			 || table.convertColumnIndexToModel(column) == ControlAreaTableModel.ATKU )
	   {
			getJLabelText().setHorizontalAlignment( javax.swing.SwingConstants.CENTER );
			getJLabelTrigger().setHorizontalAlignment( javax.swing.SwingConstants.CENTER );
	   }
		else
		{
			getJLabelText().setHorizontalAlignment( javax.swing.SwingConstants.LEFT );
			getJLabelTrigger().setHorizontalAlignment( javax.swing.SwingConstants.LEFT );			
		}
					
	}
	
	/**
	 * Insert the method's description here.
	 * Creation date: (1/8/2002 12:21:57 PM)
	 * @return java.lang.String
	 * @param trigger LMControlAreaTrigger
	 */
	public static synchronized void setTriggerStrings(
			com.cannontech.loadcontrol.data.LMControlAreaTrigger trigger, 
			JTable table, 
			StringBuffer strBuf, 
			final int col ) 
	{
		if( trigger == null )
			return;
	

		switch( (table == null ? col : table.convertColumnIndexToModel(col)) )
		{
			case ControlAreaTableModel.VALUE_THRESHOLD:
			{
				LitePoint point = PointFuncs.getLitePoint( trigger.getPointId().intValue() );
			
				if( IlmDefines.TYPE_STATUS.equalsIgnoreCase(trigger.getTriggerType()) )
				{
					LiteState lsVal = StateFuncs.getLiteState( point.getStateGroupID(), trigger.getPointValue().intValue() );
					LiteState lsThresh = StateFuncs.getLiteState( point.getStateGroupID(), trigger.getThreshold().intValue() );
					
					strBuf.append(
						(lsVal == null ? "(Unknown State)" : lsVal.getStateText()) +
						" / " +
						(lsThresh == null ? "(Unknown State)" : lsThresh.getStateText()) );
				}	
				else
				{
					//com.cannontech.database.db.device.lm.LMControlAreaTrigger.TYPE_THRESHOLD
					strBuf.append(
						NMBR_FORMATTER.format(trigger.getPointValue()) +
						" / " +
						NMBR_FORMATTER.format(trigger.getThreshold()) );
				}
			
				break;	
			}
	
			case ControlAreaTableModel.PEAK_PROJECTION:
			{	
				if( IlmDefines.TYPE_THRESHOLD.equalsIgnoreCase(trigger.getTriggerType()) )
				{
					strBuf.append(
						NMBR_FORMATTER.format(trigger.getPeakPointValue()) +
						" / " + 
						NMBR_FORMATTER.format(trigger.getProjectedPointValue()) );
				}
				
				break;
			}
			
			
			case ControlAreaTableModel.ATKU:
			{
				if( IlmDefines.TYPE_THRESHOLD.equalsIgnoreCase(trigger.getTriggerType()) )
				{
					strBuf.append(
						(trigger.getThresholdKickPercent().intValue() <= 0
						 ? "DISABLED KU"
						 : trigger.getThresholdKickPercent().toString()) );
				}
				
				break;
			}
		

		} //end case
	

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
		setBackground( model.getCellBackgroundColor(row, column) );
		
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
		com.cannontech.clientutils.CTILogger.info("--------- UNCAUGHT EXCEPTION ---------");
		com.cannontech.clientutils.CTILogger.error( exception.getMessage(), exception );
	}

	/**
	 * Initialize the class.
	 */
	private void initialize() 
	{
		try {
			setName("MultiLineControlAreaRenderer");
	
			setSize( 180, rowHeight );		
			setLayout( new BorderLayout() );
	
			add( getJLabelText(), BorderLayout.NORTH );
			add( getJLabelTrigger(), BorderLayout.SOUTH );	
		} 
		catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	
		setOpaque(true);	
	}
	
	/**
	 * Insert the method's description here.
	 * Creation date: (7/18/2001 5:03:03 PM)
	 */
	private synchronized void processLMControlArea( LMControlArea value, JTable table, final int col )
	{
		//add(getJLabelText());
		StringBuffer topStrBuf = new StringBuffer();
		StringBuffer botStrBuf = new StringBuffer();


		if( table.getRowHeight() != rowHeight )
			table.setRowHeight( rowHeight );
		
		if( value.getTriggerVector().size() > 0 )
		{			
			for( int i = 0; i < value.getTriggerVector().size(); i++ )
			{
				com.cannontech.loadcontrol.data.LMControlAreaTrigger trigger = 
						(com.cannontech.loadcontrol.data.LMControlAreaTrigger)value.getTriggerVector().get(i);


				if( trigger.getTriggerNumber().intValue() == 1 )
				{
					setTriggerStrings( trigger, table, topStrBuf, col );
				}
				else if( trigger.getTriggerNumber().intValue() == 2 )
				{
					setTriggerStrings( trigger, table, botStrBuf, col );
				}
				else
					com.cannontech.clientutils.CTILogger.info("**** ControlArea '" + value.getYukonName() +"' has more than 2 Triggers defined for it.");

			}
				
		}
		else
		{
			 topStrBuf = new StringBuffer( "(No Triggers Found)" );
		}
	
		getJLabelTrigger().setText( botStrBuf.toString() );
		getJLabelTrigger().setToolTipText( botStrBuf.toString() );

		getJLabelText().setText( topStrBuf.toString() );
		getJLabelText().setToolTipText( topStrBuf.toString() );
		
		//always add this JLabel
		//add(getJLabelTrigger());	
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
