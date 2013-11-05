<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<cti:standardPage title="Consumer Energy Services" module="commercialcurtailment_user">
<cti:standardMenu />

<%@ include file="../include/user_header.jsp" %>

<%@ include file="../../include/trending_functions.jspf" %>

<%
	graphBean.setGdefid( 
		(request.getParameter("gdefid") == null 
		 ? -1 : Integer.parseInt(request.getParameter("gdefid"))) );
	graphBean.setPage( 
		(request.getParameter("page") == null 
		 ? 1 : Integer.parseInt(request.getParameter("page"))) );
%>
<script type="text/javascript">
window.onload = function() {
	init();
}

function dispStatusMsg(msgStr) 
{
  status=msgStr;
  document.statVal = true;
}
</script>


<table width="96%" align="center"><tr><td class="leftColumn" valign="top" style="font-size: 10px;">
<h3>Available Trends</h3>
<%   /* Retrieve all the predefined graphs for this user*/                       
if( gData != null )
{%>
<table width="100%" border="0" cellspacing="0" cellpadding="0">
    <%
    for( int i = 0; i < gData.length; i++ )                                                          
    {
        if( Integer.parseInt(gData[i][0].toString()) == graphBean.getGdefid() )
        {%>
  <tr> 
    <td width="10"><img src="/WebConfig/<cti:getProperty property="NAV_BULLET_SELECTED"/>" width="9" height="9" ></td>
    <td style="padding:1px"><span><%=gData[i][1] %></span></td>
  </tr>
        <%}
        else 
        {%>
  <tr> 
    <td width="10">&nbsp;</td>
    <td style="padding:1px"><a href="user_trending_sp.jsp?<%= "gdefid=" + gData[i][0]%>" class="link2"><span><%=gData[i][1] %></span></a></td>
  </tr>
        <%}
    }%>
</table>
<%}%>
</td><td>

<table width="100%" border="0" cellspacing="0" cellpadding="0">
  <tr> 
    <td>
<%@include file="../../include/trending_options.jspf"%>
      <hr>
      <center>
      <%
      if( graphBean.getGdefid() <= 0 )
      {%>
        <p> No Data Set Selected </p>
      <%}
      else if( graphBean.getViewType() == GraphRenderers.SUMMARY)
      {
        graphBean.updateCurrentPane();
        out.println(graphBean.getHtmlString());
      }
      else if( graphBean.getViewType() == GraphRenderers.TABULAR)
      {%>
        <%@ include file="../../include/trending_tabular.jspf" %>					
      <% }
      else // "graph" is default
      {%>
        <img id = "theGraph" src="<cti:url value="/servlet/GraphGenerator?action=EncodeGraph"/>">					
      <%}%>
      <br><font size="-1"><cti:msg key="yukon.web.modules.trending.user.disclaimer"/></font>                	
      </center>
    </td>
  </tr>
</table>
</td></tr></table>
</cti:standardPage>
