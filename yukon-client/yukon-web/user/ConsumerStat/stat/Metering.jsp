<html>
<!-- Java script needed for the Calender Function--->
<SCRIPT  LANGUAGE="JavaScript" SRC="../../../JavaScript/calendar.js"></SCRIPT>
<%@ include file="include/StarsHeader.jsp" %>
<%@ include file="../../../include/trending_functions.jsp" %>
<head>
<title>Consumer Energy Services</title>
<link rel="stylesheet" href="../../../WebConfig/yukon/CannonStyle.css" type="text/css">
<link rel="stylesheet" href="../../../WebConfig/<cti:getProperty propertyid="<%=WebClientRole.STYLE_SHEET%>"/>" type="text/css">
<META NAME="robots" CONTENT="noindex, nofollow">
<meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1">

<!--
<script language="JavaScript">
function MM_reloadPage(init) {  //reloads the window if Nav4 resized
  if (init==true) with (navigator) {if ((appName=="Netscape")&&(parseInt(appVersion)==4)) {
    document.MM_pgW=innerWidth; document.MM_pgH=innerHeight; onresize=MM_reloadPage; }}
  else if (innerWidth!=document.MM_pgW || innerHeight!=document.MM_pgH) location.reload();
}
MM_reloadPage(true);
</script>
// -->
</head>
<%
	graphBean.setGdefid( 
		(request.getParameter("gdefid") == null 
		 ? -1 : Integer.parseInt(request.getParameter("gdefid"))) );
	graphBean.setPage( 
		(request.getParameter("page") == null 
		 ? 1 : Integer.parseInt(request.getParameter("page"))) );
%>
<body class="Background" leftmargin="0" topmargin="0" onload = "init()">
<table width="760" border="0" cellspacing="0" cellpadding="0">
  <tr>
    <td>
      <%@ include file="include/HeaderBar.jsp" %>
    </td>
  </tr>
  <tr>
    <td>
      <table width="760" border="0" cellspacing="0" cellpadding="0" align="center" bordercolor="0">
        <tr> 
          <td width="101" bgcolor="#000000" height="1"></td>
          <td width="1" bgcolor="#000000" height="1"></td>
          <td width="657" bgcolor="#000000" height="1"></td>
          <td width="1" bgcolor="#000000" height="1"></td> 
        </tr>
        <tr> 
          <td  valign="top" width="101"> 
            <% String pageName = "Metering.jsp"; %> 
            <%@ include file="include/Nav.jsp" %>
          </td>
          <td width="1" bgcolor="#000000"><img src="../../WebConfig/yukon/Icons/VerticalRule.gif" width="1"></td>
          <td width="657" valign="top" bgcolor="#FFFFFF" >
          
			<%@include file="../../../include/trending_options.jsp"%>

            <table width="575" border="0" cellspacing="0" cellpadding="0" align="center" height="46">
              <tr> 
                <td>
                  <hr>
                  <center>
                    <%
					if( graphBean.getGdefid() <= 0 )
					{%>
        	              <p> No Data Set Selected 
					<%}
					else if( graphBean.getViewType() == GraphRenderers.SUMMARY)
					{
						graphBean.updateCurrentPane();
						out.println(graphBean.getHtmlString());
					}
					else if( graphBean.getViewType() == GraphRenderers.TABULAR)
					{%>
						<%@ include file="../../../include/trending_tabular.jsp" %>					
						<%
					}
					else // "graph" is default
					{%>
        	            <img id = "theGraph" src="<%=request.getContextPath()%>/servlet/GraphGenerator?" >
                	<%}%>

					<%
					if( request.getParameter("update") != null && request.getParameter("update").equalsIgnoreCase("now"))
					{
						graphBean.getDataNow();
					}%>
					<br><font size="-1"><cti:getProperty propertyid="<%= CommercialMeteringRole.TRENDING_DISCLAIMER%>"/></font>                	
					</center>
                </td>
              </tr>
            </table>
          <br>
</td>
        </tr>
      </table>
    </td>
  </tr>
</table>
<!--<script>Calendar.CreateCalendarLayer(10, 275, "");</script>-->
</body>
</html>
