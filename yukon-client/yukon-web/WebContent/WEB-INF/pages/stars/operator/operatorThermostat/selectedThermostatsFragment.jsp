<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti"%>
<%@ taglib tagdir="/WEB-INF/tags" prefix="tags"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n" %>

<cti:msgScope paths="modules.operator.selectedThermostatsFragment">

<%-- THERMOSTAT SELECT PAGE URL --%>
<cti:url var="changeSelectedUrl" value="/spring/stars/operator/thermostatSelect/select">
 	<cti:param name="accountId" value="${accountId}"/>
 </cti:url>
 
<c:set var="canEditThermostats" value="false"/>
<cti:checkRolesAndProperties value="OPERATOR_CONSUMER_INFO_THERMOSTATS_ALL">
	<c:set var="canEditThermostats" value="true"/>
</cti:checkRolesAndProperties>
     
<%-- THERMOSTAT NAMES --%>
<c:choose>
	<c:when test="${fn:length(thermostatNames) > 1}">
		<c:set var="formElementContainerNameKey" value="selectedThermostatsHeaderMultiple"/>
	</c:when>
	<c:otherwise>
		<c:set var="formElementContainerNameKey" value="selectedThermostatsHeader"/>
	</c:otherwise>
</c:choose>
			
<tags:formElementContainer nameKey="${formElementContainerNameKey}">

	<table class="selectedThermostatList">
		<c:forEach var="thermostatName" items="${thermostatNames}" varStatus="status">
			<tr>
				<td class="nameCol">
	   				<spring:escapeBody htmlEscape="true">${thermostatName}</spring:escapeBody>
	    		</td>
	    		
	    		<td class="editCol">
		    		<c:choose>
						<c:when test="${status.count == 1 && canEditThermostats}">
							<cti:msg2 var="thermostatChangeLinkTitle" key=".linkTitle"/>
							<a href="${changeSelectedUrl}" title="${thermostatChangeLinkTitle}">
								<i:inline key=".edit"/>
							</a>
						</c:when>
						<c:otherwise>
							&nbsp;
						</c:otherwise>
					</c:choose>
				</td>
	    	</tr>
	    </c:forEach>
	</table>
	
</tags:formElementContainer>

</cti:msgScope>
