<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring" %>

<%-- THERMOSTAT SELECT PAGE URL --%>
<cti:url var="changeSelectedUrl" value="/spring/stars/operator/thermostatSelect/select">
 	<cti:param name="accountId" value="${accountId}"/>
 	<cti:param name="energyCompanyId" value="${energyCompanyId}"/>
 </cti:url>
     
<%-- THERMOSTAT NAMES --%>
<table class="compactResultsTable" style="width:200px;">
	<tr>
		<th>
			<c:choose>
				<c:when test="${fn:length(thermostatNames) > 1}">
					<cti:msg2 var="namesHeader" key="yukon.web.modules.operator.selectedThermostatsFragment.header.multiple"/>
				</c:when>
				<c:otherwise>
					<cti:msg2 var="namesHeader" key="yukon.web.modules.operator.selectedThermostatsFragment.header"/>
				</c:otherwise>
			</c:choose>
				
			${namesHeader}
			<cti:checkRolesAndProperties value="OPERATOR_CONSUMER_INFO_THERMOSTATS_ALL">
				<cti:msg2 var="thermostatChangeLinkTitle" key="yukon.web.modules.operator.selectedThermostatsFragment.linkTitle"/>
				<a href="${changeSelectedUrl}" title="${thermostatChangeLinkTitle}">
					<cti:img key="edit"/>
				</a>
			</cti:checkRolesAndProperties>
		</th>
	</tr>
	<c:forEach var="thermostatName" items="${thermostatNames}">
		<tr>
			<td>
   				<spring:escapeBody htmlEscape="true">${thermostatName}</spring:escapeBody>
    		</td>
    	</tr>
    </c:forEach>
   </table>
<br>