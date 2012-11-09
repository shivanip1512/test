<html>
<head>
<meta http-equiv="X-UA-Compatible" content="IE=EDGE" />
<%@ include file="../Consumer/include/StarsHeader.jsp" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ page import="com.cannontech.web.bean.WorkOrderBean" %>
<%@ page import="com.cannontech.stars.util.FilterWrapper" %>
<%@ page import="com.cannontech.common.constants.YukonListEntryTypes" %>
<%@ page import="com.cannontech.common.constants.YukonSelectionList" %>
<%@ page import="com.cannontech.common.constants.YukonListEntry" %>
<%@ page import="com.cannontech.stars.database.data.report.ServiceCompany" %>
<%@ page import="com.cannontech.stars.web.bean.FilterBean" %>
<%@ page import="java.util.ArrayList" %>

<jsp:useBean id="workOrderBean" class="com.cannontech.web.bean.WorkOrderBean" scope="session"/>
<%
    com.cannontech.stars.database.db.report.ServiceCompany serviceCompany = (com.cannontech.stars.database.db.report.ServiceCompany) session.getAttribute("ServiceCompany");
	if(serviceCompany == null)
	{
		session.setAttribute("ServiceCompany", ServiceCompany.retrieveServiceCompanyByContactLoginID(lYukonUser.getUserID()));
		serviceCompany = (com.cannontech.stars.database.db.report.ServiceCompany)session.getAttribute("ServiceCompany");
	}

    ArrayList<FilterWrapper> filterWrappers = new ArrayList<FilterWrapper>();
    FilterWrapper filter = null;

	//Add the ServiceCompany filter.
    filter = new FilterWrapper(String.valueOf(YukonListEntryTypes.YUK_DEF_ID_SO_FILTER_BY_SRV_COMPANY), ("Service Company: " + serviceCompany.getCompanyName()), serviceCompany.getCompanyID().toString());
    filterWrappers.add(filter);
    
  	YukonSelectionList servStatList = liteEC.getYukonSelectionList(YukonSelectionListDefs.YUK_LIST_NAME_SERVICE_STATUS);
  	for (int i = 0; i < servStatList.getYukonListEntries().size(); i++)
  	{
  		YukonListEntry entry = (YukonListEntry)servStatList.getYukonListEntries().get(i);
  		if( entry.getYukonDefID() == YukonListEntryTypes.YUK_DEF_ID_SERV_STAT_RELEASED)
  		{//Add the Release service status filter.
  			filter = new FilterWrapper(String.valueOf(YukonListEntryTypes.YUK_DEF_ID_SO_FILTER_BY_STATUS), "Service Status: Released", String.valueOf(entry.getEntryID()));
    filterWrappers.add(filter);
        }
    }
	workOrderBean.setFilters(filterWrappers);
%>
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
            <% String pageName = "ServCompReport.jsp"; %>
          </td>
          <td width="1" bgcolor="#000000"><img src="../../WebConfig/yukon/Icons/VerticalRule.gif" width="1"></td>
          <td width="657" valign="top" bgcolor="#FFFFFF"> 
            <div align="center"> 
			<table width="100%" border="0" cellspacing="0" cellpadding="3">
			  <tr>
			    <td align="center" class="TitleHeader"><BR>SERVICE ORDER REPORT</td>
			  </tr>
			</table>
			  <form name="rptForm" method="post" action="<%= request.getContextPath() %>/servlet/WorkOrderManager">
                <input type="hidden" name="action" value="CreateReport">
                <input type="hidden" name="type" value="<%= com.cannontech.analysis.ReportTypes.EC_WORK_ORDER%>">
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
	                    		<td class="HeaderCell" width="100%">Work Orders (<%=workOrderBean.getNumberOfRecords()%> released)</td>
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
