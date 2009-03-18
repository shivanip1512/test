<%@page import="com.cannontech.stars.dr.optout.dao.OptOutEventDao"%>
<%@page import="com.cannontech.stars.dr.optout.model.OptOutEventDto"%>

<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti"%>
<%@ taglib tagdir="/WEB-INF/tags" prefix="ct"%>

<%@ include file="include/StarsHeader.jsp" %>
<% if (accountInfo == null) { response.sendRedirect("../Operations.jsp"); return; } %>
<html>
<head>
<title>Energy Services Operations Center</title>
<meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1">
<link rel="stylesheet" href="../../WebConfig/yukon/CannonStyle.css" type="text/css">
<link rel="stylesheet" href="../../WebConfig/<cti:getProperty propertyid="<%=WebClientRole.STYLE_SHEET%>" defaultvalue="yukon/CannonStyle.css"/>" type="text/css">
<link rel="stylesheet" href="../../WebConfig/yukon/styles/YukonGeneralStyles.css" type="text/css">

<script language="JavaScript">
<!--
function confirmSubmit(form) { //v1.0
  if (form.OptOutPeriod.value == 0) return false;
  return confirm('Are you sure you would like to temporarily opt out of all programs?');
}
//-->
</script>
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
          <td  valign="top" width="101">
		  <% String pageName = "ProgHist.jsp"; %>
          <%@ include file="include/Nav.jspf" %>
		  </td>
          <td width="1" bgcolor="#000000"><img src="../../WebConfig/yukon/Icons/VerticalRule.gif" width="1"></td>
          <td width="657" valign="top" bgcolor="#FFFFFF"> 
            <div align="center"> 
            
<%

    int accountId = account.getAccountID();
    OptOutEventDao optOutEventDao = 
        YukonSpringHook.getBean("optOutEventDao", OptOutEventDao.class);
    
    // Get the list of completed and canceled opt outs
    List<OptOutEventDto> previousOptOutList = 
        optOutEventDao.getOptOutHistoryForAccount(accountId);
%>            
                <c:set var="previousOptOutList" value="<%=previousOptOutList %>" />                   
                
                <!-- Opt Out History -->
		        <cti:msg key="yukon.dr.operator.optoutHistory.header"/>
                <table id="deviceTable" class="miniResultsTable">
                    <tr class="<ct:alternateRow odd="" even="altRow"/>">
                        <th><cti:msg key="yukon.dr.operator.optoutHistory.device"/></th>
                        <th><cti:msg key="yukon.dr.operator.optoutHistory.program"/></th>
                        <th><cti:msg key="yukon.dr.operator.optoutHistory.dateScheduled"/></th>
                        <th><cti:msg key="yukon.dr.operator.optoutHistory.dateActive"/></th>
                        <th><cti:msg key="yukon.dr.operator.optoutHistory.durationHeader"/></th>
                    </tr>
                    
                    <c:forEach var="optOut" items="${previousOptOutList}">
                        <tr class="<ct:alternateRow odd="" even="altRow"/>">
                            <td>${optOut.inventory.deviceLabel}</td>
                            <td>
                                <c:forEach var="program" items="${optOut.programList}">
                                    ${program.programName} 
                                </c:forEach>
                            </td>
                            <td>
                               <cti:formatDate value="${optOut.scheduledDate}" type="DATEHM"/>
                            </td>
                            <td>
                               <cti:formatDate value="${optOut.startDate}" type="DATEHM"/>
                            </td>
                            <td>
                                <c:choose>
                                    <c:when test="${optOut.state == 'SCHEDULE_CANCELED'}">
                                        <cti:msg key="yukon.dr.operator.optoutHistory.canceled"/>
                                    </c:when>
                                    <c:otherwise>
                                        <cti:formatTimePeriod startDate="${optOut.startDate}" endDate="${optOut.stopDate}" type="DH"/>
                                    </c:otherwise>
                                </c:choose>
                            </td>
                        </tr>
                    </c:forEach>
                </table>
	            <cti:msg var="backToOptOut" key="yukon.dr.operator.optoutHistory.backToOptOut" />
	            <input type="button" value="${backToOptOut}" onclick='location.href="./OptOut.jsp"'>
            </div>
            <p>&nbsp;</p>
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
