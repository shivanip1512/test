<%@ page import="com.cannontech.database.db.graph.GraphRenderers" %>
<jsp:useBean id="graphBean" class="com.cannontech.graph.GraphBean" scope="session"></jsp:useBean>
	<%
	if( graphBean.getGdefid() <= 0 )
	{
	%>
		<p class="MainText"> No Data Set Selected 
	<%
	}
	else if( graphBean.getViewType() == GraphRenderers.SUMMARY)
	{
		graphBean.updateCurrentPane();				
		out.println(graphBean.getHtmlString());
	}
	else if( graphBean.getViewType() == GraphRenderers.TABULAR)
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
