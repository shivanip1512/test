<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib prefix="ct" tagdir="/WEB-INF/tags" %>

<cti:verifyRolesAndProperties value="CONSUMER_INFO"/>
    
<cti:standardPage module="dr" page="optOutScheduledEvents">
    
    <table class="widgetColumns">
		<tr>
			<td class="widgetColumnCell" valign="top" style="padding: 9px 0px 0px 0px">
			    <c:choose>
			        <c:when test="${fn:length(scheduledEvents) > 0}">
					    <table class="resultsTable">
					        <tr>
					            <th><cti:msg key="yukon.web.modules.dr.scheduledEvents.startDateTime" /></th>
					            <th><cti:msg key="yukon.web.modules.dr.scheduledEvents.duration" /></th>
					            <th><cti:msg key="yukon.web.modules.dr.scheduledEvents.accountNumber" /></th>
					            <th><cti:msg key="yukon.web.modules.dr.scheduledEvents.serialNumber" /></th>
					        </tr>
					        <c:forEach var="event" items="${scheduledEvents}">
					            <tr>
					                <td><cti:formatDate value="${event.startDate}" type="DATEHM"/></td>
					                <td><cti:formatDuration startDate="${event.startDate}" endDate="${event.stopDate}" type="DH"/></td>
					                <td>${event.accountNumber}</td>
					                <td>${event.serialNumber}</td>
					            </tr>
					        </c:forEach>
					    </table>
				    </c:when>
				    <c:otherwise>
				       <cti:msg key="yukon.web.modules.dr.scheduledEvents.noScheduledEvents" />
				    </c:otherwise>
			    </c:choose>
			</td>
    		<td class="widgetColumnCell" valign="top">
			    <ct:widget bean="operatorAccountSearch"/>
    		</td>
    	</tr>
    </table>
</cti:standardPage>