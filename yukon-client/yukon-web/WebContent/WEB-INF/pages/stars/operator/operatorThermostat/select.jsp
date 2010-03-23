<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti"%>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring" %>

<cti:standardPage module="operator" page="thermostatSelect">

	<script>

	    function checkboxChanged(this_cb) {

			var this_thermostatId = this_cb.id.split('-')[1];
			var this_thermostatType = this_cb.id.split('-')[2];

			// thermostatIds value -> currentIds array
			var currentIds = $F('thermostatIds')
	        if(currentIds == '') {
	            currentIds = $A();
	        } else {
	            currentIds = $A(currentIds.split(','));
	        }

			// add or remove from currentIds array
			if(this_cb.checked) {
	            currentIds.push(this_thermostatId);
	        } else {
	            currentIds = currentIds.without(this_thermostatId);
	        }

			// currentIds array -> thermostatIds value
			$('thermostatIds').value = currentIds.join();

			// loop over all themostat checkboxes
			$$('input[id|="THERMOSTATCHECKBOX"]').each(function(cb) {

				var cb_thermostatType = cb.id.split('-')[2];

				// when checking, disable all others that are not of this type
				if (this_cb.checked && cb_thermostatType != this_thermostatType) {
					cb.disabled = true;

				// when unchecking, if this is the last to be unchecked re-enable all others
				} else if (!this_cb.checked && currentIds.length == 0) {
					cb.disabled = false;
				}
			});
	    }
	
	</script>

    <tags:boxContainer2 key="chooseThermostats" hideEnabled="false">
    	
    	<form id="themostatSelectForm" method="post" action="/spring/stars/operator/thermostatSelect/selectRedirect">
    	
    		<input type="hidden" name="accountId" value="${accountId}">
    		<input type="hidden" name="energyCompanyId" value="${energyCompanyId}">
    		<input type="hidden" id="schedule" name="schedule" value="false">
    		<input type="hidden" id="thermostatIds" name="thermostatIds" value="${thermostatIds}">
    		
    		<table class="compactResultsTable">
    		
    			<tr>
                    <th style="width:20px;">&nbsp;</th>
                    <th style="width:250px;">
                    	<cti:msg key="yukon.web.modules.operator.thermostatSelect.name"/>
                    </th>
                    <th><cti:msg key="yukon.web.modules.operator.thermostatSelect.type"/></th>
                </tr>
    		
    			<c:forEach var="thermostat" items="${thermostats}">
    				
    				 <tr class="<tags:alternateRow odd="" even="altRow"/>">
                        <td>
                            <input type="checkbox" id="THERMOSTATCHECKBOX-${thermostat.id}-${thermostat.type}" onclick="checkboxChanged(this)">
                        </td>
                        <td class="nonwrapping">
                            <spring:escapeBody htmlEscape="true">${thermostat.label}</spring:escapeBody>
                        </td>
                        <td>
                            <cti:msg key="${thermostat.type.displayKey}"/>
                        </td>
                    </tr>
    			
    			</c:forEach>
    		
    		</table>
    	
    	</form>
    
    </tags:boxContainer2>
    
    <br>
    <cti:msg var="scheduleText" key="yukon.web.modules.operator.thermostatSelect.schedule"/>
    <input type="button" value="${scheduleText}" onclick="$('schedule').value = true;$('themostatSelectForm').submit();" style="width:80px;"/>
    
    <cti:msg var="manualText" key="yukon.web.modules.operator.thermostatSelect.manual" />
    <input type="button" value="${manualText}" onclick="$('themostatSelectForm').submit();" style="width:80px"/>

</cti:standardPage>