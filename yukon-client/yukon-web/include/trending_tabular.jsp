<%
if( (graphBean.getOption() & GraphRenderers.LOAD_DURATION_MASK) == GraphRenderers.LOAD_DURATION_MASK)
{
	graphBean.updateCurrentPane();
	out.println(graphBean.getHtmlString());
}
else if( graphBean.getPeriod().equalsIgnoreCase(ServletUtil.ONEDAY))
{
	graphBean.updateCurrentPane();
	out.println(graphBean.getHtmlString());
}
else
{
    java.text.SimpleDateFormat shortDateFormat = new java.text.SimpleDateFormat("M/d");
	int numDays = graphBean.getNumDays();
    int numWeeks = (numDays-1) / 7;
    int page_ = graphBean.getPage();
    int currentWeek = (page_-1) / 7;
    int basePage = currentWeek * 7 +  1;
    java.util.Date now = new java.util.Date();
    if( numWeeks > 0 )
    {
        if( currentWeek == 0 )
        {%>   
			<FONT COLOR="gray">< Prev</FONT>
		<%}
        else
        {%>        
			<a href="<%= request.getRequestURI() %>?<%="&page=" + (currentWeek*7) %>">
			<FONT COLOR="black">< Prev</FONT></A>
		<%}
    }

    // Use this calendar to figure out the month and day of each day in this period
    java.util.GregorianCalendar cal = new java.util.GregorianCalendar();
    cal.setTime(graphBean.getStartDate());
    cal.set( java.util.Calendar.DAY_OF_YEAR, cal.get(java.util.Calendar.DAY_OF_YEAR) + basePage-1 );

	for( int p = basePage; numDays != 1 && p < basePage + 7 && p <= numDays; p++ )
    {    
        if( cal.getTime().compareTo( now ) > 0 )
            break;

        if( p == page_ )
        {%>    
	        <FONT COLOR="gray"><%= shortDateFormat.format( cal.getTime() )%></FONT>
		<%}
        else
        {%>
	        <a href="<%= request.getRequestURI() %>?<%= "&page=" + p %>">
    	    <FONT COLOR="black"><%= shortDateFormat.format( cal.getTime() ) %></FONT></A>
		<%}

        cal.set( java.util.Calendar.DAY_OF_YEAR, cal.get(java.util.Calendar.DAY_OF_YEAR) + 1 );
    }
    
	if( numWeeks > 0 && cal.getTime().compareTo( now ) <= 0 )
    {
        if( currentWeek == numWeeks )
        {%>    
			<FONT COLOR="gray">  Next></FONT>
		<%}
        else
        {%>               
			<a href="<%= request.getRequestURI() %>?<%= "&page=" + (((currentWeek+1)*7)+1) %>">
			<FONT COLOR="black">  Next></FONT></A></CENTER>
		<%}
	}%>
<%
graphBean.updateCurrentPane();
out.println(graphBean.getHtmlString());
%>
<br>
<center>

	<%if( numWeeks > 0 )
    {
        if( currentWeek == 0 )
        {%>
			<FONT COLOR="gray">< Prev</FONT>
		<%}
        else
        {%>
			<a href="<%= request.getRequestURI() %>?<%= "&page=" + (currentWeek*7) %>">
			<FONT COLOR="black">< Prev</FONT></A>
		<%}
    }

    // Use this calendar to figure out the month and day of each day in this period
    cal = new java.util.GregorianCalendar();
    cal.setTime(graphBean.getStartDate());
    cal.set( java.util.Calendar.DAY_OF_YEAR, cal.get(java.util.Calendar.DAY_OF_YEAR) + basePage-1 );

    for( int p = basePage; numDays != 1 && p < basePage + 7 && p <= numDays; p++ )
    {    
        if( p == page_ )
        {%>
	        <FONT COLOR="gray"><%= shortDateFormat.format( cal.getTime() ) %></FONT>
		<%}
        else
        {%>
	        <a href="<%= request.getRequestURI() %>?<%= "&page=" + p %>">
    	    <FONT COLOR="black"><%= shortDateFormat.format( cal.getTime() ) %></FONT></A>
		<%}
	
		cal.set( java.util.Calendar.DAY_OF_YEAR, cal.get(java.util.Calendar.DAY_OF_YEAR) + 1 );
    }

	if( numWeeks > 0 )
    {
        if( currentWeek == numWeeks )
        {%>
			<FONT COLOR="gray">Next ></FONT>
		<%}
        else
        {%>               
			<a href="<%= request.getRequestURI() %>?<%= "&page=" + (((currentWeek+1)*7)+1) %>">
			<FONT COLOR="black">Next ></FONT></A></CENTER>
		<%}
    }
   }%>
<BR>