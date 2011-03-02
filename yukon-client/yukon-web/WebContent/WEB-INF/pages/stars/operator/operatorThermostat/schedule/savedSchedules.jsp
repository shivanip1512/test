<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags"%>
    
<cti:standardPage module="operator" page="thermostatSavedSchedules">

	<tags:formElementContainer nameKey="savedSchedulesUiContainerHeader">
	
		<form method="post" action="/spring/stars/operator/thermostatSchedule/viewSavedSchedule">
		
			<input name="accountId" type="hidden" value="${accountId}" />
	    	<input type="hidden" name="thermostatId" value="${thermostatId}">
			    
		    <c:choose>
		    
		    	<%-- no saved schedules --%>
			    <c:when test="${fn:length(schedules) == 0}">
			    	
			    	<cti:msg2 key=".noSchedules" />
			    	
			    	<br><br>
			    	
			    	<cti:msg2 var="createNewText" key=".createNewSchedule" />
				    <input type="submit" name="createNew" value="${createNewText}">
				    
		      	</c:when>
		      	
		      	<%-- have saved schedules --%>
		      	<c:otherwise>
		      	
		      		<cti:msg2 key=".chooseSchedule" />
				    <select name="scheduleId">
				        <c:forEach var="schedule" items="${schedules}">
				            <option value="${schedule.accountThermostatScheduleId}" <c:if test="${schedule.accountThermostatScheduleId == currentScheduleId}">selected</c:if>>
				            	<spring:escapeBody htmlEscape="true">${schedule.scheduleName}</spring:escapeBody>
				            </option>
				        </c:forEach>
				    </select>
				    
				    <br><br>
				    
				    <cti:msg2 var="viewText" key=".viewSchedule" />
				    <input type="submit" name="view" value="${viewText}">
				    
				    <cti:msg2 var="createNewText" key=".createNewSchedule" />
				    <input type="submit" name="createNew" value="${createNewText}">

				    <cti:button id="deleteSchedule" key="deleteSchedule"/>
                    <tags:confirmDialog nameKey=".deleteConfirm" styleClass="smallSimplePopup" submitName="delete" on="#deleteSchedule"/>

			</c:otherwise>
	      	
	      	</c:choose>
		      	
		</form>
	
	</tags:formElementContainer>
    
</cti:standardPage>
