<%@ attribute name="retryCheckbox" required="true" type="java.lang.Boolean"%>
<%@ attribute name="retryCount" required="true" type="java.lang.String"%>
<%@ attribute name="stopRetryAfterHoursCount" required="true" type="java.lang.String"%>
<%@ attribute name="turnOffQueuingAfterRetryCount" required="true" type="java.lang.String"%>

<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti" %>
<cti:uniqueIdentifier var="uniqueId" prefix="requestRetryTag_"/>

<cti:msg var="retryCheckboxText" key="yukon.common.device.scheduledGroupRequestExecution.home.retry.checkbox"/>
<cti:msg var="retryCountText" key="yukon.common.device.scheduledGroupRequestExecution.home.retry.retryCount"/>
<cti:msg var="stopRetryAfterHoursCountText" key="yukon.common.device.scheduledGroupRequestExecution.home.retry.stopRetryAfterHoursCount"/>
<cti:msg var="turnOffQueuingAfterRetryCountText" key="yukon.common.device.scheduledGroupRequestExecution.home.retry.turnOffQueuingAfterRetryCount"/>

<input type="checkbox" name="retryCheckbox" onclick="$('retryOptionsDiv_${uniqueId}').toggle();" <c:if test="${retryCheckbox}">checked</c:if>> ${retryCheckboxText}
        		 			
<div id="retryOptionsDiv_${uniqueId}" style="padding-top:12px;padding-bottom:10px;padding-left:0px;<c:if test="${!retryCheckbox}">display:none;</c:if>">

	<div><input type="text" name="retryCount" value="${retryCount}" maxlength="2" style="width:20px;text-align:right;"> ${retryCountText}</div>
	<br>
	
	<div><input type="text" name="stopRetryAfterHoursCount" value="${stopRetryAfterHoursCount}" maxlength="2" style="width:20px;text-align:right;"> ${stopRetryAfterHoursCountText}</div>
	<br>
	
	<div><input type="text" name="turnOffQueuingAfterRetryCount" value="${turnOffQueuingAfterRetryCount}" maxlength="2" style="width:20px;text-align:right;"> ${turnOffQueuingAfterRetryCountText}</div>

</div>
	
