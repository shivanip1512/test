<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti"%>
<%@ taglib tagdir="/WEB-INF/tags/dr" prefix="dr"%>
<%@ taglib tagdir="/WEB-INF/tags" prefix="tags"%>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n" %>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring" %>
    
<cti:standardPage module="operator" page="thermostatScheduleConfirm">

	<cti:includeCss link="/WebConfig/yukon/styles/operator/thermostat.css"/>

	<table class="thermostatPageContent">
    	<tr>
    	
    		<%-- THE SCHEDULE UI --%>
    		<td>
   
   				<tags:formElementContainer nameKey="confirmUiContainerHeader">
   				
   					<cti:msg2 key=".introText.${saveAction}" />
   					<br><br>
   					
   					<form id="cancelForm" method="post" action="/spring/stars/operator/thermostatSchedule/view">
				    	<input name="accountId" type="hidden" value="${accountId}" />
				        <input type="hidden" name="thermostatIds" value="${thermostatIds}"/>
				        <input type="hidden" name="canceledAction" value="${saveAction}"/>
				    </form>
				    
				    <%-- the meat --%>
				    ${scheduleConfirm}
				    <br><br>
				    
				    <form id="scheduleForm" name="scheduleForm" method="post" action="/spring/stars/operator/thermostatSchedule/save">
				        
				        <input name="accountId" type="hidden" value="${accountId}" />
				        <input type="hidden" name="schedules" value="<spring:escapeBody htmlEscape="true">${schedules}</spring:escapeBody>"/>
				        <input type="hidden" name="thermostatIds" value="${thermostatIds}"/>
				        <input type="hidden" name="type" value="${type}"/>
				        <input type="hidden" name="scheduleMode" value="${scheduleMode}"/>
				        <input type="hidden" name="temperatureUnit" value="${temperatureUnit}"/>
				        <input type="hidden" name="scheduleId" value="${scheduleId}"/>
				        <input type="hidden" name="scheduleName" value="${scheduleName}"/>
				        <input type="hidden" name="saveAction" value="${saveAction}"/>
				        
				        <div>
						    
						    <cti:msg2 key=".confirmText.${saveAction}" /><br><br>
						    
						    <tags:slowInput2 formId="scheduleForm" key="ok" />
						    <tags:slowInput2 formId="cancelForm" key="cancel" />
						    
					    </div>
					    
				    </form>
   					
   				</tags:formElementContainer>
    		
    		</td>
    	
    		<td class="selectedThermostatsTd">
    			<%-- THERMOSTAT NAMES --%>
    			<jsp:include page="/WEB-INF/pages/stars/operator/operatorThermostat/selectedThermostatsFragment.jsp" />
    		</td>
    	</tr>
    </table>

</cti:standardPage>
