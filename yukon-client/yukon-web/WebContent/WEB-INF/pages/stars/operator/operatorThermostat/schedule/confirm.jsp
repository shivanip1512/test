<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti"%>
<%@ taglib tagdir="/WEB-INF/tags/dr" prefix="dr"%>
<%@ taglib tagdir="/WEB-INF/tags" prefix="tags"%>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n" %>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring" %>
    
<cti:standardPage module="operator" page="thermostatScheduleConfirm">

    <%-- THERMOSTAT NAMES --%>
    <jsp:include page="/WEB-INF/pages/stars/operator/operatorThermostat/selectedThermostatsFragment.jsp" />
    
    <cti:url var="cancelUrl" value="/spring/stars/operator/thermostat/scheduleView">
	 	<cti:param name="accountId" value="${accountId}"/>
	 	<cti:param name="energyCompanyId" value="${energyCompanyId}"/>
	 	<cti:param name="thermostatIds" value="${thermostatIds}"/>
	 </cti:url>
 
    <cti:msg2 key=".introText" /><br>
    <br>
    
    <form id="cancelForm" method="post" action="/spring/stars/operator/thermostatSchedule/view">
    	<input name="accountId" type="hidden" value="${accountId}" />
        <input name="energyCompanyId" type="hidden" value="${energyCompanyId}" />
        <input type="hidden" name="thermostatIds" value="${thermostatIds}"/>
    </form>
    
    ${scheduleConfirm}<br><br>
    <form id="scheduleForm" name="scheduleForm" method="post" action="/spring/stars/operator/thermostatSchedule/save">
        
        <input name="accountId" type="hidden" value="${accountId}" />
        <input name="energyCompanyId" type="hidden" value="${energyCompanyId}" />
        <input type="hidden" name="schedules" value="<spring:escapeBody htmlEscape="true">${schedules}</spring:escapeBody>"/>
        <input type="hidden" name="thermostatIds" value="${thermostatIds}"/>
        <input type="hidden" name="timeOfWeek" value="${timeOfWeek}"/>
        <input type="hidden" name="scheduleMode" value="${scheduleMode}"/>
        <input type="hidden" name="temperatureUnit" value="${temperatureUnit}"/>
        <input type="hidden" name="scheduleId" value="${scheduleId}"/>
        <input type="hidden" name="scheduleName" value="${scheduleName}"/>
        <input type="hidden" name="saveAction" value="${saveAction}"/>
        
        <div>
		    
		    <cti:msg2 key=".confirmText" /><br><br>
		    
		    <tags:slowInput2 myFormId="scheduleForm" key="ok" width="80px"/>
		    <tags:slowInput2 myFormId="cancelForm" key="cancel" width="80px"/>
		    
	    </div>
	    
    </form>
    
</cti:standardPage>
