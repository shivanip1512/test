<html>
<head>
<meta http-equiv="X-UA-Compatible" content="IE=EDGE" />

<%@ include file="include/StarsHeader.jsp" %>
<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti" %>

<cti:verifyRolePropertyValue property="OPERATOR_IMPORT_CUSTOMER_ACCOUNT" expectedValue="upload"/>
<title>Energy Services Operations Center</title>
<meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1">
<link rel="stylesheet" href="../../WebConfig/yukon/CannonStyle.css" type="text/css">
<link rel="stylesheet" href="../../WebConfig/<cti:getProperty propertyid="<%=WebClientRole.STYLE_SHEET%>" defaultvalue="yukon/CannonStyle.css"/>" type="text/css">
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
          <td width="657" height="400" valign="top" bgcolor="#FFFFFF">
            <div align="center"> 
              <% String header = "UPLOAD FILE TO SERVER"; %>
              <%@ include file="include/InfoSearchBar2.jspf" %>
              <% if (confirmMsg != null) out.write("<span class=\"ConfirmMsg\">* " + confirmMsg.replaceAll(System.getProperty("line.separator"), "<br>") + "</span><br>"); %>
              <% if (errorMsg != null) out.write("<span class=\"ErrorMsg\">* " + errorMsg.replaceAll(System.getProperty("line.separator"), "<br>") + "</span><br>"); %>
			  <form name="form1" method="post" action="<%=request.getContextPath()%>/servlet/ImportManager" enctype="multipart/form-data">
                <input type="hidden" name="action" value="UploadGeneric">
                <input type="hidden" name="REDIRECT" value="<%= request.getContextPath() %>/operator/Consumer/GenericUpload.jsp">
                <table width="600" border="0" cellspacing="0" cellpadding="0">
                  <tr> 
                    <td class="MainText" align="center">Please specify the file you wish 
                      to upload. The target file will be uploaded to a temporary directory on 
                      the server machine for later use.<br></td>
                  </tr>
                </table>
                <br>
                <table width="500" border="0" cellspacing="0" cellpadding="3" class="TableCell">
                  <tr> 
                    <td width="30%" align="right">File Path and Name: </td>
                    <td width="70%"> 
                      <input type="file" name="GenericFile" size="35">
                    </td>
                  </tr>
                 </table>
                <table width="400" border="0" cellspacing="0" cellpadding="5">
                  <tr> 
                    <td align="center"> 
                      <input type="submit" name="Submit" value="Submit">
                    </td>
                  </tr>
                </table>
              </form>
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
