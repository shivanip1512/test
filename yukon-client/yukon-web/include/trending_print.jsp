<%@ taglib uri="/WEB-INF/cti.tld" prefix="cti" %>
<%@ page import="com.cannontech.database.db.graph.GraphRenderers" %>
<%@ page import="com.cannontech.roles.application.WebClientRole"%>
<%@ page import="com.cannontech.util.ServletUtil" %>
<%@ page import="com.cannontech.database.data.lite.LiteYukonUser" %>
<%	
	LiteYukonUser lYukonUser = (LiteYukonUser) session.getAttribute(ServletUtil.ATT_YUKON_USER);
	if (com.cannontech.database.cache.functions.YukonUserFuncs.getLiteYukonUser(lYukonUser.getUserID()) != lYukonUser)
	{
		// User login no longer valid
		response.sendRedirect(request.getContextPath() + "/servlet/LoginController?ACTION=LOGOUT");
		return;
	}
%>
<html>
<head>
<title>Consumer Energy Services</title>
<link rel="stylesheet" href="../WebConfig/yukon/CannonStyle.css" type="text/css">
<link rel="stylesheet" href="../WebConfig/<cti:getProperty propertyid="<%=WebClientRole.STYLE_SHEET%>"/>" type="text/css">
<META NAME="robots" CONTENT="noindex, nofollow">
<meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1">
</head>
<%
	com.cannontech.graph.GraphBean graphBean = (com.cannontech.graph.GraphBean) session.getAttribute(ServletUtil.ATT_GRAPH_BEAN);
	if(graphBean == null)
	{
		session.setAttribute(ServletUtil.ATT_GRAPH_BEAN, new com.cannontech.graph.GraphBean());
		graphBean = (com.cannontech.graph.GraphBean)session.getAttribute(ServletUtil.ATT_GRAPH_BEAN);
	}

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
	else if( graphBean.getViewType() == GraphRenderers.TABULAR )
	{
		graphBean.updateCurrentPane();
		out.println(graphBean.getHtmlString());
	}
	else // "graph" is default
	{
	%>
		<img src="<%=request.getContextPath()%>/servlet/GraphGenerator?action=EncodeGraph">
	<%
	}
	%>

<a href="javascript:history.back()" class="Link3">Back</a> 
</html>