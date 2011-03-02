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
	
		<form method="post" action="/spring/stars/consumer/thermostat/schedule/saved">
		    
		    <input type="hidden" name="thermostatIds" value="${thermostatIds}">
		    
		    <c:choose>
		    
		    	<%-- no saved schedules --%>
			    <c:when test="${fn:length(schedules) == 0}">
			    	
			    	<cti:msg key="yukon.dr.consumer.savedSchedules.noSchedules" />
			    	
			    	<br><br>
			    	
			    	<cti:msg2 var="createNewText" key="yukon.dr.consumer.savedSchedules.createNewSchedule" />
				    <input type="submit" name="createNew" value="${createNewText}">
				    
		      	</c:when>
		      	
		      	<%-- have saved schedules --%>
		      	<c:otherwise>
		      	
		      		<cti:msg key="yukon.dr.consumer.savedSchedules.chooseSchedule" />
				    <select name="scheduleId">
				        <c:forEach var="schedule" items="${schedules}">
				        	<option value="${schedule.accountThermostatScheduleId}" <c:if test="${schedule.accountThermostatScheduleId == currentScheduleId}">selected</c:if>>
				            	<spring:escapeBody htmlEscape="true">${schedule.scheduleName}</spring:escapeBody>
				            </option>
				        </c:forEach>
				    </select>
				    
				    <br><br>
				    
				    <cti:msg var="viewText" key="yukon.dr.consumer.savedSchedules.viewSchedule" />
				    <input type="submit" name="view" value="${viewText}">
				    
				    <cti:msg2 var="createNewText" key="yukon.dr.consumer.savedSchedules.createNewSchedule" />
				    <input type="submit" name="createNew" value="${createNewText}">

                    <cti:button id="deleteSchedule" key="deleteSchedule"/>
                    <tags:confirmDialog nameKey=".deleteConfirm" styleClass="smallSimplePopup" submitName="delete" on="#deleteSchedule"/>

		      	</c:otherwise>
	      	
	      	</c:choose>
		    
		</form>
		
	</div>

</cti:standardPage>