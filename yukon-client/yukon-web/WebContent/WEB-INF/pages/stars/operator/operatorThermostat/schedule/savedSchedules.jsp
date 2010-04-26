<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib tagdir="/WEB-INF/tags" prefix="tags"%>
    
<cti:standardPage module="operator" page="thermostatSavedSchedules">

	<tags:formElementContainer nameKey="savedSchedulesUiContainerHeader">

		<c:choose>
		
			<c:when test="${fn:length(schedules) == 0}">
				<cti:msg2 key=".noSchedules" />
			</c:when>
	          
			<c:otherwise>
				<form method="post" action="/spring/stars/operator/thermostatSchedule/viewSavedSchedule">
			    
			    	<input name="accountId" type="hidden" value="${accountId}" />
			    	<input type="hidden" name="thermostatId" value="${thermostatId}">
			    
			    	<cti:msg2 key=".chooseSchedule" />
				    <select name="scheduleId">
				        <c:forEach var="schedule" items="${schedules}">
				            <option value="${schedule.id}"><spring:escapeBody htmlEscape="true">${schedule.name}</spring:escapeBody></option>
				        </c:forEach>
				    </select>
				    
				    <br><br>
				    
				    <cti:msg2 var="viewText" key=".viewSchedule" />
				    <input type="submit" name="view" value="${viewText}">
				    
				    <cti:msg2 var="deleteText" key=".deleteSchedule" />
				    <input type="submit" name="delete" value="${deleteText}">
				    
				</form>
			</c:otherwise>
			
		</c:choose>
	
	</tags:formElementContainer>
    
</cti:standardPage>
