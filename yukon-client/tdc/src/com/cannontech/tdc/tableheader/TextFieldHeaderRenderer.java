package com.cannontech.tdc.tableheader;

/**
 * Insert the type's description here.
 * Creation date: (4/4/00 1:01:34 PM)
 * @author: 
 * @Version: <version>
 */
public class TextFieldHeaderRenderer extends javax.swing.JTextField implements javax.swing.table.TableCellRenderer 
{
	private int columnediting = -1;
/**
 * TextFieldHeaderRenderer constructor comment.
 */
public TextFieldHeaderRenderer() 
{
	super();

	setMargin( new java.awt.Insets( 0, 0, 0, 0 ) );
}
/**
 * TextFieldHeaderRenderer constructor comment.
 * @param columns int
 */
public TextFieldHeaderRenderer(int columns) {
	super(columns);
}
/**
 * TextFieldHeaderRenderer constructor comment.
 * @param text java.lang.String
 */
public TextFieldHeaderRenderer(String text) {
	super(text);
}
/**
 * TextFieldHeaderRenderer constructor comment.
 * @param text java.lang.String
 * @param columns int
 */
public TextFieldHeaderRenderer(String text, int columns) {
	super(text, columns);
}
/**
 * TextFieldHeaderRenderer constructor comment.
 * @param doc javax.swing.text.Document
 * @param text java.lang.String
 * @param columns int
 */
public TextFieldHeaderRenderer(javax.swing.text.Document doc, String text, int columns) {
	super(doc, text, columns);
}
/**
 * getTableCellRendererComponent method comment.
 */
public java.awt.Component getTableCellRendererComponent(javax.swing.JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column)
{
	setText( (value == null ) ? "" : value.toString() );

	if( row == 0 && column == columnediting )
	{
		
	}

	
	return this;
}
/**
 * getTableCellRendererComponent method comment.
 */
public void setEditingColumn( int col )
{
	columnediting = col;
}
}
