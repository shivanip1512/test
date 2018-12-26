<%@ tag body-content="empty" trimDirectiveWhitespaces="true" %>

<%@ attribute name="test" required="true" type="com.cannontech.dr.model.PerformanceVerificationEventStats" %>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<c:set var="successWidth" value="${test.percentSuccess * 100}"/>
<c:set var="failedWidth" value="${test.percentFailed * 100}"/>

<div class="progress" style="width: 80px;float:left;">
    <div class="progress-bar bg-color-pie-green" role="progressbar" aria-valuenow="${test.percentSuccess}%"
         aria-valuemin="0" aria-valuemax="100" style="width: ${successWidth}%">
    </div>
    <div class="progress-bar bg-color-orange" role="progressbar" aria-valuenow="${failedWidth}%"
         aria-valuemin="0" aria-valuemax="100" style="width: ${failedWidth}%">
    </div>
</div>
<div class="fl" style="margin-left: 5px;" title="<cti:msg2 key="yukon.web.modules.dr.home.rfPerformance.successTooltip"/>">
    <span style="margin-right: 10px;width:48px;display: inline-block;">
        <fmt:formatNumber type="percent" maxFractionDigits="2" value="${test.percentSuccess}" />
    </span>
    <span class="label bg-color-pie-green">${test.numSuccesses}</span>
</div>
<div class="fl" style="margin-left: 10px;" title="<cti:msg2 key="yukon.web.modules.dr.home.rfPerformance.missedTooltip"/>">
    <span class="label bg-color-orange">${test.numFailures}</span>
</div>
<div class="fl" style="margin-left: 10px;" title="<cti:msg2 key="yukon.web.modules.dr.home.rfPerformance.unreportedTooltip"/>">
    <span class="label bg-color-grey">${test.numUnknowns}</span>
</div>
