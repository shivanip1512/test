<%@ attribute name="retryCheckbox" required="true" type="java.lang.Boolean"%>
<%@ attribute name="queuedRetryCount" required="true" type="java.lang.String"%>
<%@ attribute name="nonQueuedRetryCount" required="true" type="java.lang.String"%>
<%@ attribute name="maxTotalRunTimeHours" required="true" type="java.lang.String"%>

<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti" %>
<cti:uniqueIdentifier var="uniqueId" prefix="requestRetryTag_"/>

<cti:msg var="retryCheckboxText" key="yukon.common.device.scheduledGroupRequestExecution.home.retry.checkbox"/>
<cti:msg var="queuedRetryCountText" key="yukon.web.modules.amr.scheduledGroupRequests.results.jobDetail.queuedRetryCount"/>
<cti:msg var="nonQueuedRetryCountText" key="yukon.web.modules.amr.scheduledGroupRequests.results.jobDetail.nonQueuedRetryCount"/>
<cti:msg var="maxTotalRunTimeHoursText" key="yukon.web.modules.amr.scheduledGroupRequests.results.jobDetail.maxTotalRunTimeHours"/>

<input type="checkbox" name="retryCheckbox" onclick="$('retryOptionsDiv_${uniqueId}').toggle();" <c:if test="${retryCheckbox}">checked</c:if>> ${retryCheckboxText}
        		 			
<div id="retryOptionsDiv_${uniqueId}" style="padding-top:12px;padding-bottom:10px;padding-left:0px;<c:if test="${!retryCheckbox}">display:none;</c:if>">

	<div><input type="text" name="queuedRetryCount" value="${queuedRetryCount}" maxlength="2" style="width:20px;text-align:right;"> ${queuedRetryCountText}</div>
	<br>
	
	<div><input type="text" name="nonQueuedRetryCount" value="${nonQueuedRetryCount}" maxlength="2" style="width:20px;text-align:right;"> ${nonQueuedRetryCountText}</div>
	<br>
	
	<div><input type="text" name="maxTotalRunTimeHours" value="${maxTotalRunTimeHours}" maxlength="2" style="width:20px;text-align:right;"> ${maxTotalRunTimeHoursText}</div>

</div>
	
