package com.cannontech.tdc.roweditor;

import javax.swing.ImageIcon;

import com.cannontech.common.gui.table.ICTITableRenderer;
import com.cannontech.common.util.CtiUtilities;
import com.cannontech.database.cache.functions.YukonImageFuncs;
import com.cannontech.database.data.lite.LiteTag;

/**
 * This type was created in VisualAge.
 */

public class TagTableCellRenderer extends javax.swing.JLabel implements javax.swing.table.TableCellRenderer 
{
	private java.awt.Color borderColor = new java.awt.Color(125,50,180);

	private java.awt.Font boldFont = null;
	private java.awt.Font plainFont = null;

	/**
	 * TagTableCellRenderer constructor comment.
	 */
	public TagTableCellRenderer() {
		super();
		setOpaque(true);
	
	}
	/**
	 * getTableCellRendererComponent - requres that table be a
	 */
	public java.awt.Component getTableCellRendererComponent(javax.swing.JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) 
	{
		ICTITableRenderer model = (ICTITableRenderer)table.getModel();

		// do anything that only needs to be assigned once per repainting here
		if( row == 0 && column == 0 )
		{
			boldFont = new java.awt.Font( table.getFont().getName(), java.awt.Font.BOLD | java.awt.Font.ITALIC, table.getFont().getSize() );
			plainFont = new java.awt.Font( table.getFont().getName(), java.awt.Font.PLAIN, table.getFont().getSize() );
		}
		
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
	
			setForeground( model.getCellForegroundColor(row, column) );
			setFont( boldFont );
		}
		else
		{
			// javax.swing.BorderFactory.createEmptyBorder() returns a single instance
			//   of an empty border, so performance is not degrated by the new operator
			setBorder( javax.swing.BorderFactory.createEmptyBorder() );
			setForeground( model.getCellForegroundColor( row, column ));		
			setFont( plainFont );
		}
	
	
		//do the BG color here
		setBackground( model.getCellBackgroundColor(row, column) );	
	
		if( value != null )
		{
			setText( value.toString() );
			((javax.swing.JComponent)this).setToolTipText( value.toString() );
			
			if( value instanceof LiteTag 
				 && ((LiteTag)value).getImageID() > CtiUtilities.NONE_ID )
			{
				setIcon( 
					new ImageIcon(java.awt.Toolkit.getDefaultToolkit().createImage(
						YukonImageFuncs.getLiteYukonImage(
							((LiteTag)value).getImageID()).getImageValue()) ) );
			}
			else
				setIcon( null );
		}
		else
		{
			setText( "" );
			setIcon( null );
			((javax.swing.JComponent)this).setToolTipText( "" );
		}
		
		return this;
	}
}
