package com.cannontech.tdc.exportdata;

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
		"<H3> Time : No Time \r\n",
		" No Date \r\n",
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
	if( columnCount == 0 )  // cant have this
		return;
		
	int rowCount = receivedData.length / columnCount; //inlcudes headers

	newData = new String[ (receivedData.length + (2 * rowCount) ) + htmlHeaderCode.length + htmlFooterCode.length ];

	int position = insertHeaderValues();
	int columnWidth = (int)(100 / columnCount);
	int dataPosition = 0;
	
	// add the column Headers
	newData[position++] = "<TR VALIGN=\"bottom\" ALIGN=\"middle\">\r\n";
	for( dataPosition = 0; dataPosition < columnCount; dataPosition++ )
		newData[position++] = "<TH BGCOLOR=\"#DCDCDC\" WIDTH=" + columnWidth + "%><I><H4>" + receivedData[dataPosition] +"</I></H4></TH>\r\n";

	newData[position++] = "</TR>\r\n";

	int counter = 1;
	for( int j = dataPosition; j < receivedData.length; j++ )
	{
		if( j % columnCount == 0 )
		{
			counter = 1;
			newData[position++] = "<TR VALIGN=\"middle\" ALIGN=\"middle\">\r\n";
		}

		newData[position++] = "<TD BGCOLOR=\"#AACCFF\" WIDTH=" + columnWidth + "%><H4>" + receivedData[j] +"</H4></TD>\r\n";
		
		if( counter == columnCount )
		{
			counter = 1;
			newData[position++] = "</TR>\r\n";
		}
		else		
			counter++;
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
	htmlHeaderCode[4] = "<H3> Time : " + newTime + "\r\n";
	htmlHeaderCode[5] = " " + newDate + "\r\n";
}
}
