<jsp:useBean id="graphBean" class="com.cannontech.graph.GraphBean" scope="session"></jsp:useBean>
	<%
	if( graphBean.getGdefid() <= 0 )
	{
	%>
		<p class="Main"> No Data Set Selected 
	<%
	}
	else if( graphBean.getTab().equalsIgnoreCase("summary") )
	{
		graphBean.updateCurrentPane();				
		out.println(graphBean.getHtmlString());
	}
	else if( graphBean.getTab().equalsIgnoreCase("tab") )
	{
		graphBean.updateCurrentPane();
		out.println(graphBean.getHtmlString());
	}
	else // "graph" is default
	{
	%>
		<img src="/servlet/GraphGenerator?">
	<%
	}
	%>

<a href="javascript:history.back()" class="Link3">Back</a> 
