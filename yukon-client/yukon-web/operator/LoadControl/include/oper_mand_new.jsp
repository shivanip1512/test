<html>
<head>
<title>Energy Services Operations Center</title>
<meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1">
<link rel="stylesheet" href="../../WebConfig/yukon/CannonStyle.css" type="text/css">
<link rel="stylesheet" href="../../WebConfig/<cti:getProperty propertyid="<%=WebClientRole.STYLE_SHEET%>"/>" type="text/css">

<script language="JavaScript">
function confirm_form(f) {
	f.programname.value = f.program.options[f.program.selectedIndex].text;
	return true;
}
  function goBack() {
  location = "<%=request.getContextPath()%>/operator/LoadControl/oper_mand.jsp"
  }
</script>
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
                <td width="310" height = "28" class="PageHeader">&nbsp;&nbsp;&nbsp;Load Response</td>
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
		  <td width="1" height="102" bgcolor="#000000"><img src="../../Images/Icons/VerticalRule.gif" width="1"></td>
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
          <td width="1" bgcolor="#000000"><img src="../../Images/Icons/VerticalRule.gif" width="1"></td>
          <td width="657" valign="top" bgcolor="#FFFFFF"> 
<table width="657" border="0" cellspacing="0" cellpadding="0" align="left">
    <tr>       
    <td width="650" class="TitleHeader" valign="top"> 
      <div align="center"><br><%= checker.getError("duplicate_error") %> NOTIFICATION - NEW</div>
        <struts:form name="checker" type="com.cannontech.validate.PageBean" action="oper_mand.jsp?tab=new" onSubmit="return confirm_form(this)"> 
        <input type=hidden name="submitted" value="true">
        <input type=hidden name="programname">     
        <table width="350" border="1" cellspacing="0" cellpadding="4" align="center" class = "TableCell">
          <tr> 
            <td width="150" class="TableCell"> 
              <div align="right">&nbsp;Program:</div>
            </td>
            <td width="150" class="TableCell"><cti:select name="program" selectValues="<%= programIds %>" selectNames="<%= programNames %>" selectedValue='<%= checker.get("program") %>'/> 
            </td>
          </tr>
          <tr> 
            <td width="150" class="TableCell"> 
              <div align="right">Notify Date:</div>
            </td>
            <td class="TableCell"><struts:text property="notifydate" size="10" pattern="@date"/> 
            </td>
          </tr>
          <tr> <cti:errormsg colSpan="2"><center><%= checker.getError("notifydate") %></center>
            </cti:errormsg> </tr>
          <tr> 
            <td width="150" class="TableCell"> 
              <div align="right">Notify Time:</div>
            </td>
            <td>
              <table width="150" cellspacing="0">
                <tr>
                  <td width="140" class="TableCell"><struts:text property="notifytime" size="10" pattern="@time"/></td>
                  <td width="10" align="left" class="TableCell"><%= tz.getDisplayName(tz.inDaylightTime(new java.util.Date()), TimeZone.SHORT) %></td>
                </tr>
              </table>
            </td>
          </tr> 
          <tr> <cti:errormsg colSpan="2"><center><%= checker.getError("notifytime") %></center> 
            </cti:errormsg> </tr>
          <tr> 
            <td width="150" class="TableCell"> 
              <div align="right">Curtail Date:</div>
            </td>
            <td width="150" class="TableCell"><struts:text property="curtaildate" size="10" pattern="@date"/> 
            </td>
          </tr>
          <tr> <cti:errormsg colSpan="2"><center><%= checker.getError("curtaildate") %></center> 
            </cti:errormsg> </tr>
          <tr> 
            <td width="150" class="TableCell"> 
              <div align="right">Curtail Time: </div>
            </td>
            <td>
              <table width="150" cellspacing="0">
                <tr>
                  <td width="140" class="TableCell"><struts:text property="curtailtime" size="10" pattern="@time"/></td>
                  <td width="10" align="left" class="TableCell"><%= tz.getDisplayName(tz.inDaylightTime(new java.util.Date()), TimeZone.SHORT) %></td>
                </tr>
              </table>
            </td> 
          </tr>
          <tr> <cti:errormsg colSpan="2"> <center><%= checker.getError("curtailtime") %></center>
            </cti:errormsg> </tr>
          <tr> 
            <td width="150" class="TableCell"> 
              <div align="right">Duration (Hour):</div>
            </td>
            <td width="150" class="TableCell"><struts:text property="duration" size="10" pattern="@int"/> 
            </td>
          </tr>
          <tr> <cti:errormsg colSpan="2"> <center><%= checker.getError("duration") %></center></cti:errormsg> 
          </tr> 
        </table>
        <br>
        <table width="600" border="0" cellspacing="0" cellpadding="0" align="center" class="MainText">
          <tr> 
            <td width="83" height="29"> 
              <div align="right">Comments:&nbsp; </div>
            </td>
            <td width="511" height="29"><struts:text property="comments" size="80"/> 
            </td>
          </tr>
        </table>
        <br>
        <table width="300" border="0" cellspacing="0" cellpadding="6" align="center">
          <tr> 
            <td width="150" valign="TOP"> 
              <p align=RIGHT> 
                <input type="submit" value="Confirm" border="0" name="image">
            </td>
            <td width="150" valign="TOP"> 
              <div align="left"><input type = "button" value="Cancel" name = "cancel" onclick = "goBack()"></div>
            </td>
          </tr>
        </table>
        </struts:form></td>
    </tr>
  </table>
  <p><struts:form name="checker" type="com.cannontech.validate.PageBean" action="oper_mand.jsp?tab=new" onSubmit="return confirm_form(this)"></struts:form></p>
  <struts:form name="checker" type="com.cannontech.validate.PageBean" action="oper_mand.jsp?tab=new" onSubmit="return confirm_form(this)"> 
  <b><br>
  </b> 
  <p>&nbsp;</p>
  </struts:form> 
  
             
          </td>
        <td width="1" bgcolor="#000000"><img src="../../Images/Icons/VerticalRule.gif" width="1"></td>
    </tr>
      </table>
    </td>
	</tr>
</table>
<br>
</body>
</html>
