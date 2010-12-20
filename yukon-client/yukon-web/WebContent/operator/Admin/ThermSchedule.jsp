<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>

<%@ include file="../Consumer/include/StarsHeader.jsp" %>


<html>
<head>
<title>Energy Services Operations Center</title>
<meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1">
<link rel="stylesheet" href="../../WebConfig/yukon/CannonStyle.css" type="text/css">
      <link rel="stylesheet" href="../../WebConfig/<cti:getProperty propertyid="<%=WebClientRole.STYLE_SHEET%>" defaultvalue="yukon/CannonStyle.css"/>" type="text/css">
      <link rel="stylesheet" href="../../WebConfig/yukon/styles/YukonGeneralStyles.css" type="text/css">
      <link rel="stylesheet" href="../../WebConfig/yukon/styles/StandardStyles.css" type="text/css">
      <link rel="stylesheet" href="../../WebConfig/yukon/styles/StarsConsumerStyles.css" type="text/css">
</head>

<body class="Background" leftmargin="0" topmargin="0" onload="init()">
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
          <td  valign="top" width="101">&nbsp; </td>
          <td width="1" bgcolor="#000000"><img src="../../WebConfig/yukon/Icons/VerticalRule.gif" width="1"></td>
          
          <td width="657" valign="top" bgcolor="#FFFFFF"> 
            <div align="center"> <br>
              <span class="TitleHeader">ADMINISTRATION - DEFAULT THERMOSTAT SCHEDULE</span><br>
              <c:if test="${!empty param.confirmMsg}">
                  <br><span class="ConfirmMsg">${param.confirmMsg}</span><br>
              </c:if>

              <!--  'type' parameter is passed through automatically to include request -->
              <cti:url var="scheduleUrl" value="/spring/stars/admin/thermostat/schedule/view" />
              <jsp:include page="${scheduleUrl}" />

              <p align="center" class="MainText">
                <%@ include file="../../include/copyright.jsp" %>
              </p>
              <p align="center" class="MainText">&nbsp; </p>
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