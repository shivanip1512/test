package com.cannontech.graph.exportdata;

/**
 * Insert the type's description here.
 * Creation date: (2/29/00 10:16:58 AM)
 * @author: 
 */
public class HTMLformat 
{
	private String[] newData = null;
	
	private String[] htmlHeaderCode =
	{
		"<HTML>\r\n",
		"<HEAD><TITLE>Tabular Data Console Display Data</TITLE></HEAD>\r\n",
		"<BODY BGCOLOR= \"white\">\r\n",
		"<H1>No Title</H1>\r\n",
		"<H3> Date : No Date</H3> \r\n",
		"<TABLE BORDER=3 WIDTH = 100% CELLPADDING=3 CELLSPACING=3>\r\n"
	};

	private String[] htmlFooterCode =
	{		
		"</TABLE>\r\n",
		"</BODY></HTML>\r\n"
	};

/**
 * HTML constructor comment.
 */
public HTMLformat()
{
	super();
}
/**
 * Insert the method's description here.
 * Creation date: (2/29/00 2:13:08 PM)
 */
public void formatNewData( String[] receivedData, int columnCount ) 
{
	if( columnCount <= 2 )  // cant have this, we put 2(date, time) on to columnCount in Graph()
		return;
		


	int rowCount = receivedData.length / columnCount; //includes headers
	newData = new String[ (receivedData.length + (2 * rowCount) ) + htmlHeaderCode.length + htmlFooterCode.length ];		
		
	int position = insertHeaderValues();
	int columnWidth = (int)(100 / columnCount);

	
	// add the column Headers
	newData[position++] = "<TR VALIGN=\"middle\" ALIGN=\"middle\">\r\n";		
	for (int x = 0; x < columnCount; x++)
	{
		newData[position++] = "<TD BGCOLOR=\"#DCDCDC\" WIDTH=" + columnWidth + "%><I><B><H4>" 
							+ receivedData[rowCount * x] +"</H4></B></I></TD>\r\n";		
	}
	newData[position++] = "</TR>\r\n";

		
	// add the rest of the column data
	for (int i = 1; i < rowCount;i++)
	{
		int j = 0;

		if (i == (rowCount- 1) )
			break;	//don't want a comma on last one

		else
		{
			newData[position++] = "<TR VALIGN=\"middle\" ALIGN=\"middle\">\r\n";		
	
			for (j = 0; j < columnCount; j++)
			{
				newData[position++] = "<TD BGCOLOR=\"#AACCFF\" WIDTH=" + columnWidth + "%><H4>" 
									+ receivedData[i+(rowCount * j)] +"</H4></TD>\r\n";		
			}
				newData[position++] = "</TR>\r\n";
		}
	}

	insertFooterValues( position );	
}
/**
 * Insert the method's description here.
 * Creation date: (2/29/00 2:00:34 PM)
 * @return java.lang.String[]
 */
public String[] getHTMLData() 
{
	return newData;
}
/**
 * Insert the method's description here.
 * Creation date: (2/29/00 2:00:34 PM)
 * @return java.lang.String[]
 */
private void insertFooterValues( int pos )
{
	for( int i = 0; i < htmlFooterCode.length; i++ )
		newData[pos++] = htmlFooterCode[i];
}
/**
 * Insert the method's description here.
 * Creation date: (2/29/00 2:00:34 PM)
 * @return java.lang.String[]
 */
private int insertHeaderValues()
{
	int i = 0;
	
	for( i = 0; i < htmlHeaderCode.length; i++ )
		newData[i] = htmlHeaderCode[i];

	return i;
}
/**
 * Insert the method's description here.
 * Creation date: (2/29/00 1:53:52 PM)
 * @param t java.lang.String
 */
public void setDateTimeTitle(String newDate, String newTime, String newTitle )
{	
	htmlHeaderCode[3] = "<H1>" + newTitle + "</H1>\r\n";
	htmlHeaderCode[4] = "<H3>" + newDate + "</H3>\r\n";
}
}
