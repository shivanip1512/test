<html>
<head>
<meta http-equiv="X-UA-Compatible" content="IE=EDGE" />
<%@ include file="../Consumer/include/StarsHeader.jsp" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ page import="com.cannontech.stars.web.bean.WorkOrderBean" %>

<jsp:useBean id="workOrderBean" class="com.cannontech.stars.web.bean.WorkOrderBean" scope="session"/>
<jsp:setProperty name="workOrderBean" property="energyCompanyID" value="<%= user.getEnergyCompanyID() %>"/>
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
          <td  valign="top" width="101">
            <% String pageName = "Report.jsp"; %>
            <%@ include file="include/Nav.jspf" %>
          </td>
          <td width="1" bgcolor="#000000"><img src="../../WebConfig/yukon/Icons/VerticalRule.gif" width="1"></td>
          <td width="657" valign="top" bgcolor="#FFFFFF"> 
            <div align="center"> 
              <% String header = "SERVICE ORDER REPORT"; %>
              <%@ include file="include/SearchBar.jspf" %>
			  <c:if test="${workOrderBean.initialized}">
					<form name="rptForm" method="post" action="<%= request.getContextPath() %>/servlet/WorkOrderManager">
	                <input type="hidden" name="action" value="CreateReport">
	                <input type="hidden" name="type" value="<%= com.cannontech.analysis.ReportTypes.EC_WORK_ORDER %>">
	                <input type="hidden" name="fileName" value="WorkOrder">
	                <input type="hidden" name="ext" value="pdf">
	                <input type="hidden" name="NoCache">
	                <input type="hidden" name="REDIRECT" value="<%= request.getRequestURI() %>">
	                <input type="hidden" name="REFERRER" value="<%= request.getRequestURI() %>">
	                <table width="500" border="0" cellspacing="2" cellpadding="0">
	                  <tr> 
	                    <td width="50%" height="100%" valign="top"> 
	                      <table width="100%" border="1" cellspacing="0" cellpadding="0" class="TableCell">
	                        <tr> 
	                          <td> 
	                            <table width="100%" border="0" cellspacing="0" cellpadding="3" class="TableCell" height="111%">
		                		  <tr> 
		                    		<td class="HeaderCell" width="100%">Current Filters (<c:out value="${workOrderBean.numberOfRecords}"/> matching work orders)</td>
		                  		  </tr>
		                  		  <c:forEach var="filterEntry" items="${workOrderBean.filters}">
								  <tr>
									<td align="left"><c:out value="${filterEntry.filterText}"/></td>
								  </tr>
								  </c:forEach>
	                              <tr>
	                                <td align="center"><hr></td>
	                              </tr>
	                              <tr>
	                                <td><input type="radio" name="type" checked value="paged" onclick='document.rptForm.ext.value="pdf";'>Paged Work Order Report (pdf)</td>
	                              </tr>
	                              <tr>
	                                <td><input type="radio" name="type" value="paged" onclick='document.rptForm.ext.value="csv";'>Listed Work Order Report (csv)</td>
	                              </tr>
	                            </table>
	                          </td>
	                        </tr>
	                      </table>
	                    </td>
	                  </tr>
	                  <tr> 
	                    <td>&nbsp;</td>
	                  </tr>
	                  <tr> 
	                    <td width="50%" height="100%" valign="top" align="center">
	                      <input type="submit" name="Submit" value="Get Report">
	                    </td>
	                  </tr>
	                </table>
	              </form>
			  </c:if>
			  <c:if test="${!workOrderBean.initialized}">
			  	  <div class='ErrorMsg' align='center'>Please submit a work order query and try again.</div>
			  </c:if>
              <p>&nbsp;</p>
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
