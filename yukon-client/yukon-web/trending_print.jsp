<%@ page import="com.cannontech.graph.model.TrendModelType" %>
<jsp:useBean id="graphBean" class="com.cannontech.graph.GraphBean" scope="session"></jsp:useBean>
	<%
	if( graphBean.getGdefid() <= 0 )
	{
	%>
		<p class="MainText"> No Data Set Selected 
	<%
	}
	else if( graphBean.getViewType() == TrendModelType.SUMMARY_VIEW)
	{
		graphBean.updateCurrentPane();				
		out.println(graphBean.getHtmlString());
	}
	else if( graphBean.getViewType() == TrendModelType.TABULAR_VIEW )
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
