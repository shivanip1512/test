<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti"%>
<%@ taglib tagdir="/WEB-INF/tags" prefix="tags"%>
    
<cti:standardPage module="operator" page="thermostatScheduleHints">
	
	<div class="message">
    	<cti:msg2 key=".hint" htmlEscape="false"/>
    </div>
    <br>
    
    <form id="backForm" method="post" action="/spring/stars/operator/thermostatSchedule/view">
    	<input name="accountId" type="hidden" value="${accountId}" />
        <input name="energyCompanyId" type="hidden" value="${energyCompanyId}" />
        <input type="hidden" name="thermostatIds" value="${thermostatIds}"/>
    </form>
    
    <tags:slowInput2 myFormId="backForm" key="back" width="80px"/>

</cti:standardPage>