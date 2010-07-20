<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib tagdir="/WEB-INF/tags/dr" prefix="dr"%>
<%@ taglib tagdir="/WEB-INF/tags" prefix="tags"%>

<cti:standardPage module="consumer" page="thermostatSchedule">
    <cti:standardMenu/>
    
    <!-- Add language specific time formatter -->
    <cti:msg var="timeFormatter" key="yukon.common.timeFormatter" />
    <cti:includeScript link="${timeFormatter}"/>

    <cti:includeCss link="/WebConfig/yukon/CannonStyle.css"/>
    
    <cti:includeScript link="/JavaScript/nav_menu.js" />

    
    <h3>
        <cti:msg key="yukon.dr.consumer.thermostatScheduleConfirm.header" /><br>
        <cti:msg key="${thermostatLabel}" htmlEscape="true"/><br>
    </h3>
    
    
    <cti:msg key="yukon.dr.consumer.thermostatScheduleConfirm.introText" /><br>
    <br>
    
    ${scheduleConfirm}
    <form id="scheduleForm" name="scheduleForm" method="POST" action="/spring/stars/consumer/thermostat/schedule/save">
        
        <input type="hidden" name="schedules" value="<spring:escapeBody htmlEscape="true">${schedules}</spring:escapeBody>"/>
        <input type="hidden" name="thermostatIds" value="${thermostatIds}"/>
        <input type="hidden" name="type" value="${type}">
        <input type="hidden" name="scheduleMode" value="${scheduleMode}"/>
        <input type="hidden" name="temperatureUnit" value="${temperatureUnit}"/>
        <input type="hidden" name="scheduleId" value="${scheduleId}"/>
        <input type="hidden" name="scheduleName" value="${scheduleName}"/>
        <input type="hidden" name="saveAction" value="${saveAction}"/>
        
        <div align="center">
		    <cti:msg key="yukon.dr.consumer.thermostatScheduleConfirm.confirmText" /><br><br>
		    <cti:msg var="okText" key="yukon.dr.consumer.thermostatScheduleConfirm.ok" />
		    <cti:msg var="cancelText" key="yukon.dr.consumer.thermostatScheduleConfirm.cancel" />
		    <input type="submit" value="${okText}"/>
		    <input type="button" value="${cancelText}" onclick="javascript: window.location='/spring/stars/consumer/thermostat/schedule/view?thermostatIds=${thermostatIds}'"/>
	    </div>
    </form>
          
</cti:standardPage>