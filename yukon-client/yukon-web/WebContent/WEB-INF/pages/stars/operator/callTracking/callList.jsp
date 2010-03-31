<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n" %>

<cti:url var="delete" value="/WebConfig/yukon/Icons/delete.gif"/>
<cti:url var="deleteOver" value="/WebConfig/yukon/Icons/delete_over.gif"/>
	
<cti:standardPage module="operator" page="callList">

	<form id="createCallForm" action="/spring/stars/operator/callTracking/viewCall" method="get">
		<input type="hidden" name="accountId" value="${accountId}">
		<input type="hidden" name="energyCompanyId" value="${energyCompanyId}">
	</form>
	
	<form action="/spring/stars/operator/callTracking/deleteCall" method="post">
	
		<input type="hidden" name="accountId" value="${accountId}">
		<input type="hidden" name="energyCompanyId" value="${energyCompanyId}">

		<table class="resultsTable rowHighlighting">
		
			<tr>
				<th><i:inline key=".header.callNumber"/></th>
				<th><i:inline key=".header.dateTime"/></th>
				<th><i:inline key=".header.type"/></th>
				<th><i:inline key=".header.description"/></th>
				<th><i:inline key=".header.takenBy"/></th>
				
				<%-- delete header --%>
				<cti:checkRolesAndProperties value="OPERATOR_ALLOW_ACCOUNT_EDITING">
					<th><i:inline key=".header.remove"/></th>
				</cti:checkRolesAndProperties>
			</tr>
			
			<c:forEach var="callReportWrapper" items="${callReportsWrappers}">
				<tr>
					<td>
						<%-- callNumber with edit link --%>
						<cti:checkRolesAndProperties value="OPERATOR_ALLOW_ACCOUNT_EDITING">
							<cti:url var="viewCallUrl" value="/spring/stars/operator/callTracking/viewCall">
								<cti:param name="accountId">${accountId}</cti:param>
								<cti:param name="energyCompanyId">${energyCompanyId}</cti:param>
								<cti:param name="callId">${callReportWrapper.callReport.callId}</cti:param>
							</cti:url>
							<a href="${viewCallUrl}">${callReportWrapper.callReport.callNumber}</a>
						</cti:checkRolesAndProperties>
						
						<%-- callNumber without edit link --%>
						<cti:checkRolesAndProperties value="!OPERATOR_ALLOW_ACCOUNT_EDITING">
							${callReportWrapper.callReport.callNumber}
						</cti:checkRolesAndProperties>
					</td>
					<td><cti:formatDate value="${callReportWrapper.callReport.dateTaken}" type="BOTH"/></td>
					<td>${callReportWrapper.type}</td>
					<td>${callReportWrapper.callReport.description}</td>
					<td>${callReportWrapper.callReport.takenBy}</td>
					
					<%-- delete icon --%>
					<cti:checkRolesAndProperties value="OPERATOR_ALLOW_ACCOUNT_EDITING">
						<td>
							<input type="image" src="${delete}" name="callId" value="${callReportWrapper.callReport.callId}" onmouseover="javascript:this.src='${deleteOver}'" onmouseout="javascript:this.src='${delete}'">
						</td>
					</cti:checkRolesAndProperties>
				
				</tr>
			</c:forEach>
		</table>
	</form>
	
	<%-- create button --%>
	<cti:checkRolesAndProperties value="OPERATOR_ALLOW_ACCOUNT_EDITING">
		<br>
		<tags:slowInput2 myFormId="createCallForm" key="create" width="80px"/>
	</cti:checkRolesAndProperties>

</cti:standardPage>