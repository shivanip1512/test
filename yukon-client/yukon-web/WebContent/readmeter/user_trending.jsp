<html>
<% String pageName = "user_trending.jsp"; %> 
<%@ include file="include/user_header.jsp" %>
<%@ include file="../include/trending_functions.jsp" %>
<%@ page import="com.cannontech.roles.application.WebClientRole"%>
<head>
<title>Trending</title>
<link id="StyleSheet" rel="stylesheet" href="../WebConfig/yukon/CannonStyle.css" type="text/css">
<link id="StyleSheet" rel="stylesheet" href="../WebConfig/<cti:getProperty propertyid="<%=WebClientRole.STYLE_SHEET%>" defaultvalue="yukon/CannonStyle.css"/>" type="text/css">
<META NAME="robots" CONTENT="noindex, nofollow">
<meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1">

<!-- Java script needed for the Calender Function--->
<SCRIPT  LANGUAGE="JavaScript1.2" SRC="../JavaScript/Calendar1-82.js"></SCRIPT>

</head>
<%
	graphBean.setGdefid( 
		(request.getParameter("gdefid") == null 
		 ? -1 : Integer.parseInt(request.getParameter("gdefid"))) );
	graphBean.setPage( 
		(request.getParameter("page") == null 
		 ? 1 : Integer.parseInt(request.getParameter("page"))) );
%>
<body class="Background" leftmargin="0" topmargin="0" link="ffffff" alink="ffffff" vlink="ffffff" ONLOAD="init()">
<table width="760" border="0" cellspacing="0" cellpadding="0">
  <tr>
    <td>
      <table width="760" border="0" cellspacing="0" cellpadding="0" align="center">
        <tr>
          <td width="150" height="102" background="MeterImage.jpg">&nbsp;</td>
          <td width="609">
            <table width="609" cellspacing="0"  cellpadding="0" border="0">
              <tr>
                <td colspan="4" height="74" background="../WebConfig/<cti:getProperty propertyid="<%= WebClientRole.HEADER_LOGO%>" defaultvalue="yukon/DemoHeader.gif"/>" width="609"></td>
              </tr>
              <tr>
                <td width="265" height = "28" class="PageHeader" valign="middle" align="left">&nbsp;&nbsp;&nbsp;&nbsp;</td>
                <td width="253" valign="middle"></td>
                <td width="57" valign="middle">
                <form name="logoutForm"  action="/servlet/LoginController">
                    <input type="hidden" name="ACTION" value="LOGOUT">
                </form>
                <div align="left"><span class="MainText"><a href="javascript:document.logoutForm.submit()" class="Link3">Log Off</a>&nbsp;</span></div>
                </td>
              </tr>
            </table>
          </td>
          <td width="1" height="102" bgcolor="#000000"><img src="VerticalRule.gif" width="1"></td>
        </tr>
      </table>
    </td>
  </tr>
  <tr>
    <td>
      <table width="760" border="0" cellspacing="0" cellpadding="0">
        <tr>
          <td width="150" bgcolor="#000000" height="0"></td>
          <td width="1" bgcolor="#000000" height="0"></td>
          <td width="608" bgcolor="#000000" height="0"></td>
          <td width="1" bgcolor="#000000" height="0"></td>
        </tr>
        <tr> 
          <td  valign="top" width="150"> 
            <%@ include file="include/nav.jsp" %>
          </td>
          <td width="1" bgcolor="#000000"></td>
          <td width="608" valign="top" bgcolor="#FFFFFF" > 
          
			<%@include file="../include/trending_options.jsp"%>
            <table width="575" border="0" cellspacing="0" cellpadding="0" align="center" height="46" class="Subtext">
              <tr>
                <td>
                  <hr>
                  <center>
                  <%
                    if( graphBean.getGdefid() <= 0 )
                    {%>
                      <p> No Data Set Selected 
                    <%}
                    else if( graphBean.getViewType() == GraphRenderers.SUMMARY )
                    {
                      graphBean.updateCurrentPane();
                      out.println(graphBean.getHtmlString());
                    }
                    else if( graphBean.getViewType() == GraphRenderers.TABULAR)
                    {%>
                      <%@ include file="../include/trending_tabular.jsp" %>
                      <%graphBean.updateCurrentPane();
                      out.println(graphBean.getHtmlString());
                    }
                    else // "graph" is default
                    {%>
                      <img id = "theGraph" src="<%=request.getContextPath()%>/servlet/GraphGenerator?action=EncodeGraph" > 
                    <%}
                  %>
                  <br><font size="-1"><cti:getProperty propertyid="<%= CommercialMeteringRole.TRENDING_DISCLAIMER%>"/></font>
                  </center>
                </td>
              </tr>
            </table>
            <br>
            <td width="1" bgcolor="#000000" height="1"></td>
          </tr>
        </table>
      </td>
    </tr>
  </table>
  <br>
</body>
</html>