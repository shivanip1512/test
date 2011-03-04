package com.cannontech.loadcontrol.datamodels;

/**
 * This type was created in VisualAge.
 */
import java.awt.Color;
import java.sql.PreparedStatement;
import java.util.Vector;

import javax.swing.event.TableModelListener;

import com.cannontech.clientutils.CTILogger;
import com.cannontech.clientutils.commonutils.ModifiedDate;
import com.cannontech.common.util.CtiUtilities;
import com.cannontech.database.PoolManager;
import com.cannontech.database.data.point.PointTypes;
import com.cannontech.database.db.point.RawPointHistory;
import com.cannontech.dr.controlarea.model.TriggerType;
import com.cannontech.loadcontrol.data.LMControlArea;
import com.cannontech.loadcontrol.data.LMControlAreaTrigger;
import com.cannontech.loadcontrol.data.LMProgramBase;

public class ControlHistoryTableModel extends javax.swing.table.AbstractTableModel implements TableModelListener, IProgramTableModel
{
	private static final java.text.DecimalFormat NMBR_FORMATTER;

	//only get the thresholds value history for now	
	public static String SQL_RAWPT_HISTORY = 
			 "select TimeStamp, Value " +
			 "from " + RawPointHistory.TABLE_NAME + " " +
			 "where PointID=? " +
			 "order by TimeStamp desc";


	private Vector rows = null;

  	// the holder for the current LMControlArea
	private LMControlArea currentControlArea = null;
	
	//The columns and their column index	
  	public static final int DATE_TIME			= 0;
	public static final int VALUE					= 1;
	public static final int DELTA					= 2;
  	
	//The column names based on their column index
	public static final String[] columnNames =
	{
		"Date/Time",
		"Value",
		"Delta"
	};


	static
	{
		NMBR_FORMATTER = new java.text.DecimalFormat();
		NMBR_FORMATTER.setMaximumFractionDigits( 3 );
		NMBR_FORMATTER.setMinimumFractionDigits( 1 );
	}
	

	/**
	 * ControlHistoryTableModel constructor comment.
	 */
	public ControlHistoryTableModel() {
		super();
	}

	
	/**
	 * Insert the method's description here.
	 * Creation date: (3/20/00 11:40:31 AM)
	 */
	public void clear() 
	{
		//do NOT remove the elements, but just set the reference to our list to a
		//  new reference...setting it to NULL would work (getRows().removeAllElements();)
		rows = new Vector(10);
		
		currentControlArea = null;
	
		//we can drop the current row selection here
		fireTableDataChanged();
	}


	/**
	 * This method was created in VisualAge.
	 * @return java.awt.Color
	 * @param row int
	 * @param col int
	 */
	public java.awt.Color getCellBackgroundColor(int row, int col) 
	{
		return Color.BLACK;
	}

	/**
	 * This method was created in VisualAge.
	 * @return java.awt.Color
	 * @param row int
	 * @param col int
	 */
	public java.awt.Color getCellForegroundColor(int row, int col) 
	{
		return Color.WHITE;
	}
	
	/**
	 * getColumnCount method comment.
	 */
	public int getColumnCount() {
		return columnNames.length;
	}
	/**
	 * This method was created in VisualAge.
	 * @return java.lang.String
	 * @param index int
	 */
	public String getColumnName(int index) {
		return columnNames[index];
	}
	/**
	 * Insert the method's description here.
	 * Creation date: (4/6/2001 10:08:28 AM)
	 * @return com.cannontech.loadcontrol.data.LMControlArea
	 */
	private LMControlArea getCurrentControlArea() {
		return currentControlArea;
	}

	/**
	 * This method returns the value of a row in the form of 
	 * an Object, but its really a LMProgramBase object.
	 */
	public synchronized Object getRowAt(int rowIndex) 
	{
		if( rowIndex < 0 || rowIndex >= getRowCount() )
			return null;
			
		return getRows().get(rowIndex);
	}

	/**
	 * This method returns the value of a row in the form of 
	 * a LMProgramBase object.
	 */
	public synchronized LMProgramBase getProgramAt(int rowIndex) 
	{
		return null;
	}

	
	/**
	 * getRowCount method comment.
	 */
	public int getRowCount() 
	{
		return getRows().size();
	}

	/**
	 * Insert the method's description here.
	 * Creation date: (8/23/00 10:01:03 AM)
	 * @return Vector
	 */
	private Vector getRows()
	{
		if( rows == null )
			rows = new Vector(10);
			
		return rows;
	}

	/**
	 * This method was created in VisualAge.
	 * @return java.awt.Color
	 * @param row int
	 * @param col int
	 */
	public java.awt.Color getSelectedRowColor(int row, int col) 
	{
		return getCellForegroundColor(row, col);
	}
	
	/**
	 * getValueAt method comment.
	 */
	public Object getValueAt(int row, int col) 
	{
		if( col == DELTA )
		{
			Double thrsh = (Double)getValueAt( row, VALUE );
				
			//calculated columns go here
			double cThresh = getThresholdValue( getCurrentControlArea() );
			return new Double( 
				Math.abs( cThresh - thrsh.doubleValue() ) );			
		}
		else
		{
			try
			{
				Vector rowVect = (Vector)getRows().get(row);
				return rowVect.get(col);
			}
			catch( ArrayIndexOutOfBoundsException ex )
			{
				CTILogger.error(
						getClass().toString() + ".getValueAt("+col+","+row+
						") threw ArrayIndexOutOfBounds, no major problem, using dashed lines");
			}
			
			// we need to return something
			return CtiUtilities.STRING_DASH_LINE;
		}

		
	}


	/**
	 * This method was created in VisualAge.
	 * @return boolean
	 * @param row int
	 * @param column int
	 */
	public boolean isCellEditable(int row, int column) {
		return false;
	}
	

	/**
	 * The threshold value of the trigger must change or the pointID of the
	 * trigger must change to return TRUE
	 * @return boolean
	 */
	private boolean hasChanged( LMControlArea newCntrlArea )
	{
		//be sure there was a change of the are that affects this model
		if( getCurrentControlArea() == null 
			 || !getCurrentControlArea().equals(newCntrlArea)  )
		{
			return true;
		}
		else
			return 
				getThresholdValue(getCurrentControlArea()) != getThresholdValue(newCntrlArea)
				|| getTriggerPtID(getCurrentControlArea()) != getTriggerPtID(newCntrlArea);
	}
	
	/**
	 * Insert the method's description here.
	 * Creation date: (4/6/2001 10:08:28 AM)
	 * @param newCurrentControlArea com.cannontech.loadcontrol.data.LMControlArea
	 */
	public synchronized void setCurrentControlArea(LMControlArea newCurrentControlArea) 
	{
		//be sure there was a change of the area affects this model
		boolean changed = hasChanged(newCurrentControlArea);
		currentControlArea = newCurrentControlArea;
		
		if( changed )
		{
			updateModel();
			CTILogger.debug("*** Done update CtrlHistory LM model.");
		}
		else
			CTILogger.debug("*** ControlArea, has not changed, not updating LM ControlHistoryModel" );
		

		fireTableDataChanged();
	}


	/**
	 * Tells the model to update its rows from the DB
	 * Creation date: (4/6/2003 10:08:28 AM)
	 * @param newCurrentControlArea com.cannontech.loadcontrol.data.LMControlArea
	 */
	private void updateModel()
	{
		getRows().removeAllElements();
		
		if( getCurrentControlArea() == null ) 
		{
			clear();
		}
		else
		{	
			int valPtID = PointTypes.SYS_PID_SYSTEM;
			int peakPtID = PointTypes.SYS_PID_SYSTEM;
			
			for( int i = 0; i < getCurrentControlArea().getTriggerVector().size(); i++ )
			{
				LMControlAreaTrigger trigger =
					(LMControlAreaTrigger)getCurrentControlArea().getTriggerVector().get(i);

                if (trigger.getTriggerType() == TriggerType.THRESHOLD || 
                    trigger.getTriggerType() == TriggerType.THRESHOLD_POINT) {
					valPtID = trigger.getPointId().intValue();
					peakPtID = trigger.getPeakPointId().intValue();
				}
                

			}
			
			executeSQL( valPtID, peakPtID );
		}
	}
	

	/**
	 * Executes the SQL to retrieve the data from DB
	 * Creation date: (4/6/2003 10:08:28 AM)
	 * @param valPtID int, peakPtID int
	 */
	private void executeSQL( int valPtID, int peakPtID )
	{
		//5min refresh, trig threshold pt value
		java.sql.Connection conn = null;
		java.sql.ResultSet rset = null;
		PreparedStatement pstmt = null;
		Vector newRows = new Vector(10);
		
		try
		{		
			conn = PoolManager.getInstance().getConnection(CtiUtilities.getDatabaseAlias());
			pstmt = conn.prepareStatement( SQL_RAWPT_HISTORY );
			pstmt.setInt( 1, valPtID );
			
			rset = pstmt.executeQuery();

			while( rset.next() )
			{
				Vector values = new Vector(2);
				
				values.add( new ModifiedDate(rset.getTimestamp(1).getTime()) );
				values.add( new Double(rset.getDouble(2)) );

				newRows.add( values ); 
			}

		}
		catch( java.sql.SQLException e )
		{
			com.cannontech.clientutils.CTILogger.error( e.getMessage(), e );
		}
		finally
		{
			try
			{
				if( rset != null ) rset.close();
				if( pstmt != null ) pstmt.close();
				if( conn != null ) conn.close();
			} 
			catch( java.sql.SQLException e2 )
			{
				com.cannontech.clientutils.CTILogger.error( e2.getMessage(), e2 );//something is up
			}	
		}
		
		rows = newRows;
	}
	
	
	
	private double getThresholdValue( LMControlArea area )
	{
		if( area != null && area.getTriggerVector().size() > 0 )
		{
			for( int i = 0; i < area.getTriggerVector().size(); i++ )
			{
				LMControlAreaTrigger trigger = 
						(LMControlAreaTrigger)area.getTriggerVector().get(i);
				
                if (trigger.getTriggerType() == TriggerType.THRESHOLD ||
                    trigger.getTriggerType() == TriggerType.THRESHOLD_POINT) {
                    return trigger.getThreshold().doubleValue();
                }
			}
			

		}
		
		return 0.0;
	}
	

	private int getTriggerPtID( LMControlArea area )
	{
		if( area != null && area.getTriggerVector().size() > 0 )
		{
			for( int i = 0; i < area.getTriggerVector().size(); i++ )
			{
				LMControlAreaTrigger trigger = 
						(LMControlAreaTrigger)area.getTriggerVector().get(i);
				
                if (trigger.getTriggerType() == TriggerType.THRESHOLD) {
					return trigger.getPointId().intValue();
				}
			}
			

		}
		
		return PointTypes.SYS_PID_SYSTEM;
	}	
	
	/**
	 * This method was created in VisualAge.
	 * @param event javax.swing.event.TableModelEvent
	 */
	public void tableChanged(javax.swing.event.TableModelEvent event ) 
	{
		if( event instanceof com.cannontech.loadcontrol.events.LCGenericTableModelEvent )
		{
			if( ((com.cannontech.loadcontrol.events.LCGenericTableModelEvent)event).getType()
				 == com.cannontech.loadcontrol.events.LCGenericTableModelEvent.TYPE_CLEAR )
			{
				clear();
			}
	
		}
	
		//fireTableDataChanged();
		fireTableChanged( event );
	}

	/** 
	 * Tells us if we should show a waiting GUI or not while updating
	 * @return boolean
	 */
	public boolean showWaiting( LMControlArea newCntrlArea )
	{
		return hasChanged( newCntrlArea );
	}
	
}
