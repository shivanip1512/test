<html>
<head>
<title>Energy Services Operations Center</title>
<meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1">
<link rel="stylesheet" href="../demostyle.css" type="text/css">
<script language="JavaScript">
function confirm_form(f) {
	f.programname.value = f.program.options[f.program.selectedIndex].text;
	return true;
}
  function goBack() {
  location = "/OperatorDemos/LoadControl/oper_mand.jsp"
  }
</script>
</head>

<body class="Background" text="#000000" leftmargin="0" topmargin="0">
<table width="760" border="0" cellspacing="0" cellpadding="0">
  <tr>
    <td>
      <table width="760" border="0" cellspacing="0" cellpadding="0" align="center">
        <tr> 
          <td width="102" height="102" background="LoadImage.jpg">&nbsp;</td>
          <td valign="bottom" height="102"> 
            <table width="657" cellspacing="0"  cellpadding="0" border="0">
              <tr> 
                <td colspan="4" height="74" background="../Header.gif">&nbsp;</td>
              </tr>
              <tr> 
                <td width="310" height = "28" class="BlueHeader">&nbsp;&nbsp;&nbsp;Notification</td>
                <td width="235" valign="middle">&nbsp;</td>
                  <td width="58" valign="middle"> 
                    <div align="center"><span class="Main"><a href="../Operations.jsp" class="blueLink">Home</a></span></div>
                  </td>
                  <td width="57" valign="middle"> 
                    <div align="left"><span class="Main"><a href="../../login.jsp" class="blueLink">Log 
                      Off</a>&nbsp;</span></div>
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
          <td width="1" bgcolor="#000000"><img src="VerticalRule.gif" width="1"></td>
          <td width="657" valign="top" bgcolor="#FFFFFF"> 
<table width="657" border="0" cellspacing="0" cellpadding="0" align="left">
    <tr>       
    <td width="650" class="Main" valign="top"> 
      <p align="center"><b><br>
<%= checker.getError("duplicate_error") %>CREATE NOTIFICATION</b></p>
        <struts:form name="checker" type="com.cannontech.validate.PageBean" action="oper_mand.jsp?tab=new" onSubmit="return confirm_form(this)"> 
        <input type=hidden name="submitted" value="true">
        <input type=hidden name="programname">     
        <table width="350" border="1" cellspacing="0" cellpadding="4" align="center">
          <tr> 
            <td width="150" class="TableCell"> 
              <div align="right">&nbsp;Program:</div>
            </td>
            <td width="150" class="TableCell"><cti:select name="program" selectValues="<%= programIds %>" selectNames="<%= programNames %>" selectedValue="<%= checker.get(\"program\") %>"/> 
            </td>
          </tr>
          <tr> 
            <td width="150" class="TableCell"> 
              <div align="right">Notify Date:</div>
            </td>
            <td width="150" class="TableCell"><struts:text property="notifydate" size="10" pattern="@date"/> 
            </td>
          </tr>
          <tr> <cti:errormsg colSpan="2"> <%= checker.getError("notifydate") %> 
            </cti:errormsg> </tr>
          <tr> 
            <td width="150" class="TableCell"> 
              <div align="right">Notify Time:</div>
            </td>
            <td width="150" class="TableCell"><struts:text property="notifytime" size="10" pattern="@time"/> 
            </td>
          </tr>
          <tr> <cti:errormsg colSpan="2"> <%= checker.getError("notifytime") %> 
            </cti:errormsg> </tr>
          <tr> 
            <td width="150" class="TableCell"> 
              <div align="right">Curtail Date:</div>
            </td>
            <td width="150" class="TableCell"><struts:text property="curtaildate" size="10" pattern="@date"/> 
            </td>
          </tr>
          <tr> <cti:errormsg colSpan="2"> <%= checker.getError("curtaildate") %> 
            </cti:errormsg> </tr>
          <tr> 
            <td width="150" class="TableCell"> 
              <div align="right">Curtail Time: </div>
            </td>
            <td width="150" class="TableCell"><struts:text property="curtailtime" size="10" pattern="@time"/> 
            </td>
          </tr>
          <tr> <cti:errormsg colSpan="2"> <%= checker.getError("curtailtime") %> 
            </cti:errormsg> </tr>
          <tr> 
            <td width="150" class="TableCell"> 
              <div align="right">Duration (Hour):</div>
            </td>
            <td width="150" class="TableCell"><struts:text property="duration" size="10" pattern="@int"/> 
            </td>
          </tr>
          <tr> <cti:errormsg colSpan="2"> <%= checker.getError("duration") %> </cti:errormsg> 
          </tr>
        </table>
        <br>
        <table width="600" border="0" cellspacing="0" cellpadding="0" align="center" class="Main">
          <tr> 
            <td width="83" height="29"  class="MainHeader"> 
              <div align="right">Comments:&nbsp; </div>
            </td>
            <td class="Main" width="511" height="29"><struts:text property="comments" size="80"/> 
            </td>
          </tr>
        </table>
        <br>
        <table
    width="300" border="0" cellspacing="0" cellpadding="6" align="center">
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
        <td width="1" bgcolor="#000000"><img src="VerticalRule.gif" width="1"></td>
    </tr>
      </table>
    </td>
	</tr>
</table>
<br>
</body>
</html>
