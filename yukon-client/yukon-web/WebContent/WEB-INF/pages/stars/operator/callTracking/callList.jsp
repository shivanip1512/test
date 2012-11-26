<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring" %>

<cti:url var="delete" value="/WebConfig/yukon/Icons/delete.gif"/>
<cti:url var="deleteOver" value="/WebConfig/yukon/Icons/delete_over.gif"/>
	
<cti:standardPage module="operator" page="callList">
<tags:setFormEditMode mode="${mode}"/>

	<tags:boxContainer2 nameKey="callsBox">
	<table class="compactResultsTable callListTable rowHighlighting">
	
		<tr>
			<th><i:inline key=".header.callNumber"/></th>
			<th><i:inline key=".header.dateTime"/></th>
			<th><i:inline key=".header.type"/></th>
			<th class="description"><i:inline key=".header.description"/></th>
			<th><i:inline key=".header.takenBy"/></th>
			
			<%-- delete header --%>
			<cti:displayForPageEditModes modes="EDIT,CREATE">
				<th class="removeCol"><i:inline key=".header.remove"/></th>
			</cti:displayForPageEditModes>
		</tr>
		
		<c:if test="${fn:length(callReportsWrappers) <= 0}">
			<cti:displayForPageEditModes modes="VIEW">
				<tr><td colspan="5" class="noCalls subtleGray"><i:inline key=".noCalls"/></td></tr>
			</cti:displayForPageEditModes>
			<cti:displayForPageEditModes modes="EDIT,CREATE">
				<tr><td colspan="6" class="noCalls subtleGray"><i:inline key=".noCalls"/></td></tr>
			</cti:displayForPageEditModes>
		</c:if>
		
		<c:forEach var="callReportWrapper" items="${callReportsWrappers}">
			<tr>
				<td>
					<%-- callNumber with edit link --%>
					<cti:displayForPageEditModes modes="EDIT,CREATE,VIEW">
						<cti:url var="viewCallUrl" value="/spring/stars/operator/callTracking/view">
							<cti:param name="accountId">${accountId}</cti:param>
							<cti:param name="callId">${callReportWrapper.callReport.callId}</cti:param>
						</cti:url>
						<a href="${viewCallUrl}"><spring:escapeBody htmlEscape="true">${callReportWrapper.callReport.callNumber}</spring:escapeBody></a>
					</cti:displayForPageEditModes>
				</td>
				<td><cti:formatDate value="${callReportWrapper.callReport.dateTaken}" type="BOTH"/></td>
				<td>${callReportWrapper.type}</td>
				<td class="description"><spring:escapeBody htmlEscape="true">${callReportWrapper.callReport.description}</spring:escapeBody></td>
				<td><spring:escapeBody htmlEscape="true">${callReportWrapper.callReport.takenBy}</spring:escapeBody> </td>
				
				<%-- delete icon --%>
				<cti:displayForPageEditModes modes="EDIT,CREATE">
					<td class="removeCol">
						<form id="deleteCallForm" action="/spring/stars/operator/callTracking/deleteCall" method="post">
							<input type="hidden" name="accountId" value="${accountId}">
							<input type="hidden" name="deleteCallId" value="${callReportWrapper.callReport.callId}">
                            <div class="dib">
    							<input type="submit" class="icon icon_remove">
                            </div>
						</form>
					</td>
				</cti:displayForPageEditModes>
			
			</tr>
		</c:forEach>
	</table>
    
    <%-- create button --%>
    <cti:displayForPageEditModes modes="CREATE">
        <div class="actionArea">
            <form id="createCallForm" action="/spring/stars/operator/callTracking/create" method="get">
                <input type="hidden" name="accountId" value="${accountId}">
                <cti:button nameKey="create" type="submit"/>
            </form>
        </div>
    </cti:displayForPageEditModes>
    
	</tags:boxContainer2>
		
</cti:standardPage>