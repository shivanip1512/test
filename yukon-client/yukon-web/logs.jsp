<%

String logDir = request.getParameter("dir");

if( logDir != null )
{
    java.io.File dir = new java.io.File(logDir);

    if( dir.exists() && dir.isDirectory() )
    {
        java.io.File[] logs = dir.listFiles();

        // sort by timestamp, newest to oldest
        java.util.Arrays.sort( logs, new java.util.Comparator() 
        {
                public int compare(java.lang.Object A, java.lang.Object B) 
                {
                    if((((java.io.File) A).lastModified() - ((java.io.File)B).lastModified()) < 0)                     
                        return 1;
                    else
                        return -1;
                }
                public boolean equals(java.lang.Object A) 
                {
                    return equals(A);
                }           
        } );

        out.println("<table>");

        for( int i = 0; i < logs.length; i++ )    
        {
            out.println("<tr>");
            out.println("<td><a href=\"logs.jsp?get=" + logs[i].getCanonicalPath() + "\">" + logs[i].getCanonicalPath() + "</a></td>");
            out.println("<td>" + logs[i].length() + "</td>");
            out.println("<td>" + new java.util.Date( logs[i].lastModified() ) + "</td>");
            //    out.println("<BR><a href=\"logs.jsp?get=" + logs[i].getCanonicalPath() + "\">" + logs[i].getCanonicalPath() + "</a> - " + logs[i].length() );
        }
        out.println("</table>");
    }
}
else
{
    String getFile = request.getParameter("get");

    java.io.BufferedInputStream in = null;

    try
    {
    
        int bufSize = 1000000;
        byte[] buf = new byte[bufSize];
        int avail;
        in= new java.io.BufferedInputStream( new java.io.FileInputStream(getFile) );
       
        while( (avail = in.available()) > 0 )
        {             
            int num = Math.min( avail, bufSize );
            
            in.read(buf, 0, num );
            out.write( new String(buf, 0, num ) );
        }

    }
    catch(Throwable t )
    {
        t.printStackTrace();
    }
    finally
    {
        if( in != null )
            in.close();
    }
}
%>

