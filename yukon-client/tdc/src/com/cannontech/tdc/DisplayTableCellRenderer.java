package com.cannontech.tdc;

/**
 * Insert the type's description here.
 * Creation date: (2/3/00 1:26:04 PM)
 * @author: 
 */
import java.awt.Component;
import java.awt.Image;

import javax.swing.ImageIcon;

import org.apache.commons.lang3.StringUtils;

import com.cannontech.common.YukonColorPalette;
import com.cannontech.common.gui.util.SortTableModelWrapper;
import com.cannontech.tdc.custom.CustomDisplay;

public class DisplayTableCellRenderer extends javax.swing.JLabel implements javax.swing.table.TableCellRenderer
{
	private java.awt.Color borderColor = new java.awt.Color(125,50,180);
	private java.awt.Font boldFont = null;
	private java.awt.Font plainFont = null;

/**
 * DisplayTableCellRenderer constructor comment.
 */
public DisplayTableCellRenderer() 
{
	super();
	setOpaque( true );
}
/**
 * Insert the method's description here.
 * Creation date: (12/19/2000 5:33:23 PM)
 * @param table javax.swing.JTable
 */
private void doOncePerTableRefresh(javax.swing.JTable table) 
{
	boldFont = new java.awt.Font( table.getFont().getName(), java.awt.Font.BOLD | java.awt.Font.ITALIC, table.getFont().getSize() );
	plainFont = new java.awt.Font( table.getFont().getName(), java.awt.Font.PLAIN, table.getFont().getSize() );
	
}
/**
 * getTableCellRendererComponent method comment.
 */
public Component getTableCellRendererComponent(javax.swing.JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column)
{
	Display2WayDataAdapter model = (Display2WayDataAdapter)
				(((SortTableModelWrapper)table.getModel()).getRealDataModel());

	setBackground(YukonColorPalette.getColor(model.getRowBackgroundColor(row)).getAwtColor());

	// do anything that only needs to be assigned once per repainting here
	if( row == 0 && column == 0 )
		doOncePerTableRefresh( table );


	if( table.getSelectedRow() == row )
	{				
		//Each border is a newly allocated object (uses the new operator)
		//  but, we only create at most the number of columns the table has
		if( column == 0 )
			setBorder( javax.swing.BorderFactory.createMatteBorder( 2, 2, 2, 0, borderColor) );
		else if( column == (table.getModel().getColumnCount()-1) )
			setBorder( javax.swing.BorderFactory.createMatteBorder( 2, 0, 2, 2, borderColor) );
		else
			setBorder( javax.swing.BorderFactory.createMatteBorder( 2, 0, 2, 0, borderColor) );

		setForeground(YukonColorPalette.getColor(model.getRowForegroundColor(row)).getAwtColor().brighter());
		setFont( boldFont );
	}
	else
	{
		// javax.swing.BorderFactory.createEmptyBorder() returns a single instance
		//   of an empty border, so performance is not degrated by the new operator
		setBorder( javax.swing.BorderFactory.createEmptyBorder() );
		setForeground(YukonColorPalette.getColor(model.getRowForegroundColor(row)).getAwtColor());
		setFont( plainFont );
	}


	//we have the same colors, lets make a distinction
	if( getBackground().equals(getForeground()) )
		setForeground( getForeground().darker().darker() );


	if( value != null )
	{
		if( value instanceof Image )
		{
			/**** This is SLOW and may need another visit when it becomes a problem ****/
			ImageIcon icon = new ImageIcon( (Image)value );
		
	      int width = table.getTableHeader().getColumnModel().getColumn(column).getWidth();
	      int height = table.getTableHeader().getHeight(); 


	      icon.setImage(
	         icon.getImage().getScaledInstance( 
	               width,
	               height,
	               java.awt.Image.SCALE_FAST ) );

			setText( null );
			setIcon( icon );
		}
		else
		{
			setText( value.toString() );
			setIcon( null );
		}			
	}
	else
	{
		setText( "" );
		setIcon( null );
	}

	if(value == null || StringUtils.isBlank(value.toString()))
		((javax.swing.JComponent)this).setToolTipText("FILLER ROW");
	else
		((javax.swing.JComponent)this).setToolTipText( value.toString() );


	// we have to check the the actual ColumnIndexToModel mapping in order to find out what
	// column is the PointValue, because the user could have moved it.
	try
	{
		if( model.getColumnTypeName( table.convertColumnIndexToModel(column) ).equalsIgnoreCase(CustomDisplay.COLUMN_TYPE_POINTVALUE)
			 || model.getColumnTypeName( table.convertColumnIndexToModel(column) ).equalsIgnoreCase(CustomDisplay.COLUMN_TYPE_DEVICEID)
			 || model.getColumnTypeName( table.convertColumnIndexToModel(column) ).equalsIgnoreCase(CustomDisplay.COLUMN_TYPE_POINTID)
			 || model.getColumnTypeName( table.convertColumnIndexToModel(column) ).equalsIgnoreCase(CustomDisplay.COLUMN_TYPE_QUALITYCNT) )
		{
			renderNumberColumns();
		}
		else
			this.setHorizontalAlignment( javax.swing.SwingConstants.LEFT );
	}
	catch( NullPointerException ne )
	{
		this.setHorizontalAlignment( javax.swing.SwingConstants.LEFT );
	}
	
		
	return this;
}
/**
 * Insert the method's description here.
 * Creation date: (12/19/2000 5:39:02 PM)
 */
private void renderNumberColumns() 
{
	this.setHorizontalAlignment( javax.swing.SwingConstants.CENTER );
}
}
