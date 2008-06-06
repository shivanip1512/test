<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib tagdir="/WEB-INF/tags/dr" prefix="dr"%>
<%@ taglib tagdir="/WEB-INF/tags" prefix="tags"%>

<cti:standardPage module="consumer" page="savedSchedules">
<cti:standardMenu/>


	<h3>
	    <cti:msg key="yukon.dr.consumer.savedSchedules.header" /><br>
	</h3>
	
	<div class="message">
	    
        <c:choose>
            <c:when test="${fn:length(schedules) == 0}">
				    <cti:msg key="yukon.dr.consumer.savedSchedules.noSchedules" />
	        </c:when>
            <c:otherwise>
				<form method="post" action="/spring/stars/consumer/thermostat/schedule/saved">
				    
				    <input type="hidden" name="thermostatIds" value="${thermostatIds}">
				    
				    <cti:msg key="yukon.dr.consumer.savedSchedules.chooseSchedule" />
				    <select name="scheduleId">
				        <c:forEach var="schedule" items="${schedules}">
				            <option value="${schedule.id}"><spring:escapeBody htmlEscape="true">${schedule.name}</spring:escapeBody></option>
				        </c:forEach>
				    </select>
				    
				    <br><br>
				    
				    <cti:msg var="viewText" key="yukon.dr.consumer.savedSchedules.viewSchedule" />
				    <input type="submit" name="view" value="${viewText}">
				    <cti:msg var="deleteText" key="yukon.dr.consumer.savedSchedules.deleteSchedule" />
				    <input type="submit" name="delete" value="${deleteText}">
				</form>
            </c:otherwise>
        </c:choose>
	</div>

</cti:standardPage>