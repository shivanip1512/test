package com.cannontech.tdc.exportdata;

/**
 * Insert the type's description here.
 * Creation date: (7/25/00 11:48:06 AM)
 * @author: 
 */
import com.cannontech.tdc.TDCMainFrame;
import com.cannontech.tdc.logbox.MessageBoxFrame;
import com.cannontech.tdc.utils.TDCDefines;
import com.cannontech.tdc.utils.DataBaseInteraction;

public class ExportCreatedDisplay 
{
	private String fileName = "DefaultDisplay";
	private java.awt.Component parent = null;
	private long displayNumber = 0;

	private String[] displayStrings = null;
	private String[][] displayColumns = null;
	private String[][] displayPoints = null;
/**
 * ExportCreatedDisplay constructor comment.
 */
public ExportCreatedDisplay() 
{
	super();
	initialize();
}
/**
 * ExportCreatedDisplay constructor comment.
 */
public ExportCreatedDisplay( long currentDisplayNumber, java.awt.Component parentComponent, String fileName ) 
{
	super();
	this.fileName = fileName;
	parent = parentComponent;
	displayNumber = currentDisplayNumber;
		
	initialize();
}
/**
 * Insert the method's description here.
 * Creation date: (7/25/00 12:03:38 PM)
 */
private String[][] getDisplayColumnsData() throws com.cannontech.common.util.CommandExecutionException 
{
	if( displayColumns == null )
	{
		String query = new String(
			"select title, typenum, ordering, width " +
			"from displaycolumns where displaynum = ?");
		Object[] objs = new Object[1];
		objs[0] = new Long(displayNumber);
		Object[][] statement = DataBaseInteraction.queryResults( query, objs );
	
		displayColumns = new String[ statement.length ][ statement[0].length ];
		
		for( int i = 0; i < statement.length; i++ )
			for( int j = 0; j < statement[i].length; j++ )
				displayColumns[i][j] = getString(statement[i][j]);
	}

	return displayColumns;
}
/**
 * Insert the method's description here.
 * Creation date: (7/25/00 12:03:38 PM)
 */
private String[] getDisplayData() throws com.cannontech.common.util.CommandExecutionException 
{
	if( displayStrings == null )
	{
		String query= new String(
			"select name, type, title, description " +
			"from display where displaynum = ?");
		Object[] objs = new Object[1];
		objs[0] = new Long(displayNumber);
		Object[][] statement = DataBaseInteraction.queryResults( query, objs );		

		displayStrings = new String[ statement[0].length ];	
		for( int i = 0; i < statement[0].length; i++ )
			displayStrings[i] = getString(statement[0][i]);
	}
	
	return displayStrings;
}
/**
 * Insert the method's description here.
 * Creation date: (7/25/00 12:03:38 PM)
 */
private String[][] getDisplayPoints() throws com.cannontech.common.util.CommandExecutionException 
{
	if( displayPoints == null )
	{
		String query = new String(
			"select pointid, ordering " +
			"from display2waydata where displaynum = ?");
		
		Object[] objs = new Object[1];
		objs[0] = new Long(displayNumber);
		Object[][] statement = DataBaseInteraction.queryResults( query, objs );		

		displayPoints = new String[ statement.length ][ statement[0].length ];
		
		for( int i = 0; i < statement.length; i++ )
			for( int j = 0; j < statement[i].length; j++ )
				displayPoints[i][j] = getString(statement[i][j]);
	}

	return displayPoints;
}
/**
 * Insert the method's description here.
 * Creation date: (7/25/00 4:23:53 PM)
 * @return java.lang.String
 * @param value java.lang.String
 */
private String getString(Object value) 
{
	if( value == null )
		return "";
	else
		return value.toString();
}
/**
 * Insert the method's description here.
 * Creation date: (7/25/00 4:23:53 PM)
 * @return java.lang.String
 * @param value java.lang.String
 */
private String getString(String value) 
{
	if( value == null )
		return "";
	else
		return value;
}
/**
 * Insert the method's description here.
 * Creation date: (7/25/00 11:48:32 AM)
 */
private void initialize() 
{
	javax.swing.JFileChooser chooser = new javax.swing.JFileChooser(".");
	chooser.setDialogTitle("Save In");

	chooser.setSelectedFile( new java.io.File( fileName + ".sql" ) );

	int status = chooser.showSaveDialog( parent );

	if( status == chooser.APPROVE_OPTION )
	{
		writeFile( chooser.getSelectedFile().toString() );
		TDCMainFrame.messageLog.addMessage("File exportation for user created display to file " + fileName + ".sql was successful", MessageBoxFrame.INFORMATION_MSG );
	}
	
}
/**
 * Insert the method's description here.
 * Creation date: (7/25/00 12:26:27 PM)
 */
private void writeFile( String destination ) 
{
	try
	{
		java.io.FileWriter writer = new java.io.FileWriter( destination );

		try
		{
			writer.write("insert into display (displaynum, name, type, title, description) values (" + displayNumber +
					 ", '" + getDisplayData()[0] + 
					 "', '" + getDisplayData()[1] + 
					 "', '" + getDisplayData()[2] +
					 "', '" + getDisplayData()[3] +"');\r\n" );

			for( int i = 0; i < getDisplayPoints().length; i++ )
			{
				writer.write("insert into display2waydata (displaynum, ordering, pointid) values (" +
							displayNumber + 
							", " + getDisplayPoints()[i][0] + 
							", " + getDisplayPoints()[i][1] + ");\r\n" );					
			}

			for( int i = 0; i < getDisplayColumnsData().length; i++ )
			{
				writer.write("insert into displaycolumns (displaynum, title, typenum, ordering, width) values (" +
						displayNumber + ", '" + getDisplayColumnsData()[i][0] + 
						"', " + getDisplayColumnsData()[i][1] +
						", " + getDisplayColumnsData()[i][2] +
						", " + getDisplayColumnsData()[i][3] + ");\r\n");
			}
			
		}
		catch( com.cannontech.common.util.CommandExecutionException ex )
		{} // aint no thang
				
		writer.close();
	}
	catch ( java.io.IOException e )
	{
		e.printStackTrace( System.out );
	}
	
	return;		
}
}
