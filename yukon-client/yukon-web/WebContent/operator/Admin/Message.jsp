<html>
<head>
<meta http-equiv="X-UA-Compatible" content="IE=EDGE" />
<%@ include file="../Consumer/include/StarsHeader.jsp" %>
<%
	int delay = 10;
	if (request.getParameter("delay") != null)
		delay = Integer.parseInt(request.getParameter("delay"));
	
	String nextURL = (String)
		((errorMsg == null)? session.getAttribute(ServletUtils.ATT_MSG_PAGE_REDIRECT) : session.getAttribute(ServletUtils.ATT_MSG_PAGE_REFERRER));
	nextURL = ServletUtil.createSafeRedirectUrl(request, nextURL);
%>
<title>Energy Services Operations Center</title>
<meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1">
<% if (delay > 0) { %>
<meta http-equiv="Refresh" content="<%= delay %>; url=<%= nextURL %>">
<% } %>
<link rel="stylesheet" href="../../WebConfig/yukon/CannonStyle.css" type="text/css">
<link rel="stylesheet" href="../../WebConfig/<cti:getProperty property="STYLE_SHEET" defaultvalue="yukon/CannonStyle.css"/>" type="text/css">
</head>

<body class="Background" leftmargin="0" topmargin="0">
<table width="760" border="0" cellspacing="0" cellpadding="0">
  <tr>
    <td>
      <%@ include file="include/HeaderBar.jspf" %>
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
          <td  valign="top" width="101">&nbsp;</td>
          <td width="1" bgcolor="#000000"><img src="../../WebConfig/yukon/Icons/VerticalRule.gif" width="1"></td>
		  <td width="657" valign="top" bgcolor="#FFFFFF"> 
            <div align="center"><br> 
              <span class="title-header">MESSAGE</span><br>
			  <p>
              <table width="450" border="1" cellspacing="0" cellpadding="5">
                <tr> 
                  <td height="86" align="center"> 
<% if (errorMsg != null) { %>
                    <span class="ErrorMsg"><%= errorMsg %></span>
<% } else if (confirmMsg != null) { %>
                    <span class="ConfirmMsg"><%= confirmMsg %></span>
<% } %>
				  </td>
                </tr>
              </table>
              <p>
                <input type="button" name="OK" value="OK" onClick="location.href='<%= nextURL %>'">
              </p>
<% if (delay > 0) { %>
              <table width="600" border="0" cellspacing="0" cellpadding="0">
                <tr> 
                  <td class="MainText" align="center">You will be redirected to 
                    the next page in <%= delay %> seconds, or click the &quot;OK&quot; button 
                    if you don't want to wait.</td>
                </tr>
              </table>
<% } %>
              <p align="center" class="MainText">&nbsp;</p>
            </div>
			
          </td>
        <td width="1" bgcolor="#000000"><img src="../../WebConfig/yukon/Icons/VerticalRule.gif" width="1"></td>
    </tr>
      </table>
	  
    </td>
	</tr>
</table>
<br>
</body>
</html>
