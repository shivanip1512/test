<html>
<!-- Java script needed for the Calender Function--->
<%@ include file="../include/user_header.jsp" %>
<SCRIPT  LANGUAGE="JavaScript" SRC="../../JavaScript/calendar.js"></SCRIPT>

<%@ include file="../../include/trending_functions.jsp" %>
<head>
<title>Consumer Energy Services</title>
<link rel="stylesheet" href="../../WebConfig/yukon/CannonStyle.css" type="text/css">
<link rel="stylesheet" href="../../WebConfig/<cti:getProperty propertyid="<%=WebClientRole.STYLE_SHEET%>" defaultvalue="yukon/CannonStyle.css"/>" type="text/css">
<META NAME="robots" CONTENT="noindex, nofollow">
<meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1">

</head>
<body class="Background" leftmargin="0" topmargin="0">
<table width="760" border="0" cellspacing="0" cellpadding="0">
  <tr>
    <td>
      <table width="760" border="0" cellspacing="0" cellpadding="0" align="center">
        <tr> 
          <td width="150" height="102" background="../../WebConfig/yukon/MomWide.jpg">&nbsp;</td>
          <td valign="top" height="102"> 
            <table width="609" cellspacing="0"  cellpadding="0" border="0">
              <tr> 
                <td colspan="4" height="74" background="../../WebConfig/<cti:getProperty propertyid="<%= WebClientRole.HEADER_LOGO%>" defaultvalue="yukon/DemoHeader.gif"/>">&nbsp;</td>
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
      <table width="760" border="0" cellspacing="0" cellpadding="0" align="center" bordercolor="0">
        <tr> 
          <td width="150" bgcolor="#000000" height="1"></td>
          <td width="1" bgcolor="#000000" height="1"></td>
          <td width="609" bgcolor="#000000" height="1"></td>
          <td width="1" bgcolor="#000000" height="1"></td> 
        </tr>
        <tr> 
          <td  valign="top" width="150"> 
            <% String pageName = "user_reporting.jsp"; %> 
            <%@ include file="include/nav.jsp" %>
          </td>
          <td width="1" bgcolor="#000000"><img src="../../WebConfig/yukon/Icons/VerticalRule.gif" width="1"></td>
          <td width="657" valign="top" bgcolor="#FFFFFF" >

<!--TRENDING OPTIONS-->
<table width="575" border="0" align="center" cellpadding="4" cellspacing="0">
  <tr>
    <td width="303" valign="top">
      <div>
        <table width="375" border="0" cellspacing="2" cellpadding="0">
          <tr>
            <form id=MForm method="POST" action="<%= request.getContextPath() %>/servlet/ReportGenerator"  name="MForm">
              <input type="hidden" name="REDIRECT" value="<%= request.getRequestURI() %>">
			  <input type="hidden" name="REFERRER" value="<%= request.getRequestURI() %>">
              
              <td width="75">
                <div align="left">
                  <input type="image" src="<%=request.getContextPath()%>/WebConfig/yukon/Buttons/GoButton.gif" name="image" border="0">
                </div>
              </td>
            </form>
          </tr>
        </table>
      </div>
    </td>
    <td width="200" valign = "top" align= "center"> 
      <table width="200" border="0" class = "MainText" cellspacing = "4" height="16">
        <tr>
          <td width = "40%"> 
            <div name = "report" align = "center" style = "border:solid 1px #666999; cursor:default;" onMouseOver = "menuAppear(event, 'reportMenu')" >Report</div>
          </td>
        </tr>
      </table>
    </td>    
  </tr>
</table>
<table width="575" border="0" align="center" cellpadding="0" cellspacing="0">
  <tr>
    <td>
      <form name="exportForm">
	    <div id="reportMenu" class = "bgmenu" style = "width:75px" align = "left"> 
            <div id = "LINEID" name = "format"  style = "width:75px" onmouseover = "changeOptionStyle(this)" class = "optmenu1" onclick = "reportData('png')">&nbsp;&nbsp;&nbsp;Export .png</div>
        </div>
      </form>
    </td>
  </tr>
</table>
            <table width="575" border="0" cellspacing="0" cellpadding="0" align="center" height="46">
              <tr> 
                <td>
                  <hr>
                  <center>
       	            <img id = "theReport" src="<%=request.getContextPath()%>/servlet/ReportGenerator?action=EncodeReport&type=8">
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
