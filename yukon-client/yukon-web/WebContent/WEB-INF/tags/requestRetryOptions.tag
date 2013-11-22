<%@ attribute name="retryCheckbox" required="true" type="java.lang.Boolean"%>
<%@ attribute name="queuedRetryCount" required="true" type="java.lang.String"%>
<%@ attribute name="nonQueuedRetryCount" required="true" type="java.lang.String"%>
<%@ attribute name="maxTotalRunTimeHours" required="true" type="java.lang.String"%>

<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti" %>

<style>
    .retryOptions { padding:12px 0px 10px 0px; }
    .rightInputs { width:20px; text-align:right; }
</style>

<cti:uniqueIdentifier var="uniqueId" prefix="requestRetryTag_"/>

<cti:msg var="retryCheckboxText" key="yukon.common.device.schedules.home.retry.checkbox"/>
<cti:msg var="queuedRetryCountText" key="yukon.web.modules.tools.schedules.VIEW.results.jobDetail.queuedRetryCount"/>
<cti:msg var="nonQueuedRetryCountText" key="yukon.web.modules.tools.schedules.VIEW.results.jobDetail.nonQueuedRetryCount"/>
<cti:msg var="maxTotalRunTimeHoursText" key="yukon.web.modules.tools.schedules.VIEW.results.jobDetail.maxTotalRunTimeHours"/>

<input type="checkbox" name="retryCheckbox" onclick="jQuery('#retryOptionsDiv_${uniqueId}').toggle();"
        <c:if test="${retryCheckbox}">checked</c:if>> ${retryCheckboxText}

<div class="retryOptions" id="retryOptionsDiv_${uniqueId}"
        <c:if test="${!retryCheckbox}">style="display:none;"</c:if>>
	<div>
        <input class="rightInputs" type="text" name="queuedRetryCount"
            value="${queuedRetryCount}" maxlength="2" />${queuedRetryCountText}
    </div>
	<br />
	<div>
        <input class="rightInputs" type="text" name="nonQueuedRetryCount"
            value="${nonQueuedRetryCount}" maxlength="2" /> ${nonQueuedRetryCountText}
    </div>
	<br />
	<div>
        <input class="rightInputs" type="text" name="maxTotalRunTimeHours"
            value="${maxTotalRunTimeHours}" maxlength="2" /> ${maxTotalRunTimeHoursText}
    </div>
</div>
	
