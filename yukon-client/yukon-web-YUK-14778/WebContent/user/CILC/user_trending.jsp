<html>
<!-- Java script needed for the Calender Function--->
<%@ include file="../include/user_header.jsp" %>

<%@ include file="../../include/trending_functions.jspf" %>
<head>
<title>Consumer Energy Services</title>
<link rel="stylesheet" href="../../WebConfig/yukon/CannonStyle.css" type="text/css">
<link rel="stylesheet" href="../../WebConfig/<cti:getProperty property="STYLE_SHEET" defaultvalue="yukon/CannonStyle.css"/>" type="text/css">
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
<table width="810" border="0" cellspacing="0" cellpadding="0">
  <tr>
    <td>
      <table width="810" border="0" cellspacing="0" cellpadding="0" align="center">
        <tr> 
          <td width="150" height="102" background="../../WebConfig/yukon/MomWide.jpg">&nbsp;</td>
          <td valign="top" height="102"> 
            <table width="100%" cellspacing="0"  cellpadding="0" border="0">
              <tr> 
                <td colspan="4" height="74" background="../../WebConfig/<cti:getProperty property="HEADER_LOGO" defaultvalue="yukon/DemoHeader.gif"/>">&nbsp;</td>
              </tr>
              <tr>
			    <td width="265" height = "28" class="PageHeader" valign="middle" align="left">&nbsp;&nbsp;&nbsp;Trending</td>
                <td width="253" valign="middle">&nbsp;</td>
                <td width="58" valign="middle">&nbsp;</td>
                <td width="57" valign="middle"> 
                  <div align="left"><span class="MainText"><a href="<%=request.getContextPath()%>/servlet/LoginController?ACTION=LOGOUT" class="Link3">Log Off</a>&nbsp;</span></div>
                </td>
              </tr>
            </table>
          </td>
		  <td width="1" height="102" bgcolor="#000000"><img src="../../WebConfig/yukon/Icons/VerticalRule.gif" width="1"></td>
          </tr>
      </table>
    </td>
  </tr>
  <tr>
    <td>
      <table width="810" border="0" cellspacing="0" cellpadding="0" align="center" bordercolor="0">
        <tr> 
          <td width="150" bgcolor="#000000" height="1"></td>
          <td width="1" bgcolor="#000000" height="1"></td>
          <td width="658" bgcolor="#000000" height="1"></td>
          <td width="1" bgcolor="#000000" height="1"></td> 
        </tr>
        <tr> 
          <td  valign="top" width="150"> 
            <% String pageName = "user_trending.jsp"; %> 
            <%@ include file="include/nav.jspf" %>
          </td>
          <td width="1" bgcolor="#000000"><img src="../../WebConfig/yukon/Icons/VerticalRule.gif" width="1"></td>
          <td width="657" valign="top" bgcolor="#FFFFFF" >
          
			<%@include file="../../include/trending_options.jspf"%>

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
						<%@ include file="../../include/trending_tabular.jspf" %>					
						<%
					}
					else // "graph" is default
					{%>
        	            <img id = "theGraph" src="<%=request.getContextPath()%>/servlet/GraphGenerator?action=EncodeGraph">					
                	<%}%>
					<br><font size="-1"><cti:msg key="yukon.web.modules.trending.user.disclaimer"/></font>                	
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
</body>
</html>
