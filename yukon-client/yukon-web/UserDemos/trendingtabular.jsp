<%



    java.text.SimpleDateFormat shortDateFormat = new java.text.SimpleDateFormat("M/d");      
%>
<br>
<center>
<%
    int numWeeks = (numDays-1) / 7;
    int currentWeek = (Integer.parseInt(page)-1) / 7;
    int basePage = currentWeek * 7 +  1;
    java.util.Date now = new java.util.Date();

 /*out.println("num weeks = " + numWeeks );
 out.println("current Week = " +currentWeek );
 out.println("base page = " +basePage );*/
    if( numWeeks > 0 )
    {
        if( currentWeek == 0 )
        {        
%>   
<FONT COLOR="gray">< Prev</FONT>
<%
        }
        else
        {
%>        
<a href="<%= request.getRequestURI() %>?<%= "gdefid=" + graphDefinitionId + "&start=" + dateFormat.format(start) + "&period=" + java.net.URLEncoder.encode(period) + "&tab=" + tab + "&page=" + (currentWeek*7) %>">
<FONT COLOR="black">< Prev</FONT>
</A>
<% 
        }
    }

    // Use this calendar to figure out the month and day of each 
    // day in this period
    java.util.GregorianCalendar cal = new java.util.GregorianCalendar();
    cal.setTime(start);

    cal.set( java.util.Calendar.DAY_OF_YEAR, cal.get(java.util.Calendar.DAY_OF_YEAR) + basePage-1 );

     for( int p = basePage; numDays != 1 && p < basePage + 7 && p <= numDays; p++ )
    {    
        if( cal.getTime().compareTo( now ) > 0 )
            break;

        if( p == Integer.parseInt(page) )
        {
            
%>    
        <FONT COLOR="gray"><%= shortDateFormat.format( cal.getTime() ) %></FONT>
<%
        }
        else
        {        
%>        
        <a href="<%= request.getRequestURI() %>?<%= "gdefid=" + graphDefinitionId + "&start=" + dateFormat.format(start) + "&period=" + java.net.URLEncoder.encode(period) + "&tab=" + tab + "&page=" + p %>">
        <FONT COLOR="black"><%= shortDateFormat.format( cal.getTime() ) %></FONT>
        </A> 
<%        
        }

        cal.set( java.util.Calendar.DAY_OF_YEAR, cal.get(java.util.Calendar.DAY_OF_YEAR) + 1 );
    }
%>
 
<% 
if( numWeeks > 0 && cal.getTime().compareTo( now ) <= 0 )
    {
        if( currentWeek == numWeeks )
        {        
%>    
<FONT COLOR="gray">  Next></FONT>
<%
        }
        else
        {
%>               
<a href="<%= request.getRequestURI() %>?<%= "gdefid=" + graphDefinitionId + "&start=" + dateFormat.format(start) + "&period=" + java.net.URLEncoder.encode(period) + "&tab=" + tab + "&page=" + (((currentWeek+1)*7)+1) %>">
<FONT COLOR="black">  Next></FONT>
</A>
</CENTER>
<% 
        }
    }
%>

<jrun:servlet code="TabularDataGenerator">
<jrun:servletparam name="gdefid" value="<%= Integer.toString(graphDefinitionId) %>" />
<jrun:servletparam name="start" value="<%= dateFormat.format(start) %>" />
<jrun:servletparam name="page" value="<%= page %>" />
<jrun:servletparam name="end" value="<%= dateFormat.format(stop) %>" />
<jrun:servletparam name="db" value="<%= dbAlias %>" />
</jrun:servlet>

<br>
<center>
<%
    if( numWeeks > 0 )
    {
        if( currentWeek == 0 )
        {        
%>    
<FONT COLOR="gray">< Prev</FONT>
<%
        }
        else
        {
%>        
<a href="<%= request.getRequestURI() %>?<%= "gdefid=" + graphDefinitionId + "&start=" + dateFormat.format(start) + "&period=" + java.net.URLEncoder.encode(period) + "&tab=" + tab + "&page=" + (currentWeek*7) %>">
<FONT COLOR="black">< Prev</FONT>
</A>
<% 
        }
    }

    // Use this calendar to figure out the month and day of each 
    // day in this period
    cal = new java.util.GregorianCalendar();
    cal.setTime(start);

    cal.set( java.util.Calendar.DAY_OF_YEAR, cal.get(java.util.Calendar.DAY_OF_YEAR) + basePage-1 );

    for( int p = basePage; numDays != 1 && p < basePage + 7 && p <= numDays; p++ )
    {    
        if( p == Integer.parseInt(page) )
        {
            
%>
        <FONT COLOR="gray"><%= shortDateFormat.format( cal.getTime() ) %></FONT>
<%
        }
        else
        {        
%>
        <a href="<%= request.getRequestURI() %>?<%= "gdefid=" + graphDefinitionId + "&start=" + dateFormat.format(start) + "&period=" + java.net.URLEncoder.encode(period) + "&tab=" + tab + "&page=" + p %>">
        <FONT COLOR="black"><%= shortDateFormat.format( cal.getTime() ) %></FONT>
        </A> 
<%        
        }
        cal.set( java.util.Calendar.DAY_OF_YEAR, cal.get(java.util.Calendar.DAY_OF_YEAR) + 1 );
    }
%>
 
<% 
if( numWeeks > 0 )
    {
        if( currentWeek == numWeeks )
        {        
%>    
<FONT COLOR="gray">Next ></FONT>
<%
        }
        else
        {
%>               
<a href="<%= request.getRequestURI() %>?<%= "gdefid=" + graphDefinitionId + "&start=" + dateFormat.format(start) + "&period=" + java.net.URLEncoder.encode(period) + "&tab=" + tab + "&page=" + (((currentWeek+1)*7)+1) %>">
<FONT COLOR="black">Next ></FONT>
</A>
</CENTER>
<% 
        }
    }
%>
<BR>