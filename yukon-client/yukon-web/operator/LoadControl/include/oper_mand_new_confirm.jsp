<html>
<head>
<title>Energy Services Operations Center</title>
<meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1">
<link rel="stylesheet" href="../../WebConfig/yukon/CannonStyle.css" type="text/css">
<link rel="stylesheet" href="../../WebConfig/<cti:getProperty propertyid="<%=WebClientRole.STYLE_SHEET%>"/>" type="text/css">

<SCRIPT LANGUAGE="JAVASCRIPT" TYPE="TEXT/JAVASCRIPT">
  <!-- Hide the script from older browsers
  
  function goBack() {
  location = "<%=request.getContextPath()%>/operator/LoadControl/oper_mand.jsp?tab=new&confirmed=false"
  }

  //End hiding script -->
  </SCRIPT>
</head>

<body class="Background" text="#000000" leftmargin="0" topmargin="0">
<table width="760" border="0" cellspacing="0" cellpadding="0">
  <tr>
    <td>
      <table width="760" border="0" cellspacing="0" cellpadding="0" align="center">
        <tr> 
          <td width="102" height="102" background="../../WebConfig/yukon/LoadImage.jpg">&nbsp;</td>
          <td valign="bottom" height="102"> 
            <table width="657" cellspacing="0"  cellpadding="0" border="0">
              <tr> 
                <td colspan="4" height="74" background="../../WebConfig/<cti:getProperty propertyid="<%= WebClientRole.HEADER_LOGO%>"/>">&nbsp;</td>
              </tr>
              <tr> 
                <td width="310" height = "28"class="PageHeader">&nbsp;&nbsp;&nbsp;Load Response</td>
                <td width="235" valign="middle">&nbsp;</td>
                  <td width="58" valign="middle"> 
                    <div align="center"><span class="MainText"><a href="../Operations.jsp" class="Link3">Home</a></span></div>
                  </td>
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
          <td width="101" bgcolor="#000000" height="1"></td>
          <td width="1" bgcolor="#000000" height="1"></td>
          <td width="657" bgcolor="#000000" height="1"></td>
		  <td width="1" bgcolor="#000000" height="1"></td>
        </tr>
        <tr> 
          <td  valign="top" width="101"> 
            <table width="101" border="0" cellspacing="0" cellpadding="6" height="200">
              <tr> 
                <td height="20" valign="top" align="center"> 
                  <form name="form1" method="get" action="oper_mand.jsp?tab=current">
                    <p><br>
                      <input type="submit" name="tab" value="Current">
                    </p>
                  </form>
                  <form name="form1" method="get" action="oper_mand.jsp?tab=new">
                    <p> 
                      <input type="submit" name="tab" value="New">
                    </p>
                  </form>
                  <form name="form1" method="get" action="oper_mand.jsp?tab=history">
                    <p> 
                      <input type="submit" name="tab" value="History">
                    </p>
                  </form>
                  <form name="form1" method="get" action="oper_mand.jsp?tab=programs">
                    <p> 
                      <input type="submit" name="tab" value="Programs">
                    </p>
                  </form>
                  <p>
                </td>
              </tr>
            </table>
          </td>
          <td width="1" bgcolor="#000000"><img src="../../WebConfig/yukon/Icons/VerticalRule.gif" width="1"></td>
          <td width="657" valign="top" bgcolor="#FFFFFF"> 
            <table width="657" border="0" cellspacing="0" cellpadding="0" align="left">
              <tr>      
                <td width="650"> 
                  <div align="center" class="TitleHeader"><br>
                  <form action="oper_mand.jsp?tab=newconfirm" method="post">
                    <input type=hidden name="confirmed" value="true">
                    <input type=hidden name="program" value="<%= programStr %>">
                    <input type=hidden name="notifydate" value="<%= notifyDateStr %>">
                    <input type=hidden name="notifytime" value="<%= notifyTimeStr %>">
                    <input type=hidden name="startdate" value="<%= curtailDateStr %>">
                    <input type=hidden name="starttime" value="<%= curtailTimeStr %>">
                    <input type=hidden name="duration" value='<%= checker.get("duration") %>'>
                    <input type=hidden name="comments" value='<%= checker.get("comments") %>'>
                    NOTIFICATION - NEW<br><br>

                    <table width="250" border="1" cellspacing="0" cellpadding="4" align="center">
                      <tr> 
                        <td width="150" class="TableCell"> 
                          <div align="right">&nbsp;Program:</div>
                        </td>
                        <td width="150" class="TableCell">&nbsp;<%= checker.get("programname") %></td>
                      </tr>
                      <tr>  
                        <td width="150" class="TableCell"> 
                          <div align="right">Notify Date:</div>
                        </td>
                        <td width="150" class="TableCell">&nbsp;<%= notifyDateStr %></td>
                      </tr>
                      <tr> 
                        <td width="150" class="TableCell"> 
                          <div align="right">Notify Time:</div>
                        </td>
                        <td>
                          <table>
                            <tr>
                              <td width="150" class="TableCell">&nbsp;<%= notifyTimeStr %></td>
                             <td class="TableCell"><%= tz.getDisplayName(tz.inDaylightTime(new java.util.Date()), TimeZone.SHORT) %></td>
                            </tr>
                          </table>
                        </td>
                      </tr>
                      <tr> 
                        <td width="150" class="TableCell"> 
                          <div align="right">Curtail Date:</div>
                        </td>
                        <td width="150" class="TableCell">&nbsp;<%= curtailDateStr %></td>
                      </tr>
                      <tr> 
                        <td width="150" class="TableCell"> 
                          <div align="right">Curtail Time:</div>
                        </td>
                        <td>
                          <table>
                            <tr>
                              <td width="150" class="TableCell">&nbsp;<%= curtailTimeStr %></td>
                              <td class="TableCell"><%= tz.getDisplayName(tz.inDaylightTime(new java.util.Date()), TimeZone.SHORT) %></td>
                            </tr>
                          </table>
                        </td>
                      </tr>
                      <tr> 
                        <td width="150" class="TableCell"> 
                          <div align="right"> Duration:</div>
                        </td>
                        <td width="150" class="TableCell">&nbsp;<%= checker.get("duration") %></td>
                      </tr>
                    </table>
                    <br>
                    <table width="600" border="1" cellspacing="0" cellpadding="0" align="center">
                      <tr> 
                        <td  class="TableCell" width="83" height="29"> 
                          <div align="right">Comments:&nbsp;</div>
                        </td>
                        <td class="TableCell" width="511" height="29"><%= checker.get("comments") %> </td>
                      </tr>
                    </table>
                    <br>
                    <table width="300" border="0" cellspacing="0" cellpadding="6" align="center">
                      <tr> 
                        <td width="150" valign="top"> 
                          <p align=RIGHT valign="TOP"> 
                          <input type="submit" IMG value="Send" border="0" name="image">
                        </td>
                        <td width="150" valign="TOP"> 
                          <div align="left"><input type = "button" value="Cancel" name = "cancel" onclick = "javascipt:history.back()"></div>
                        </td>
                      </tr>
                    </table>
                    <br>  
                  </form>
                  </div>                  
                </td>
              </tr>
            </table>
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
