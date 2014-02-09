<%@ tag body-content="empty" trimDirectiveWhitespaces="true" %>

<%@ attribute name="test" required="true" type="com.cannontech.dr.model.PerformanceVerificationEventStats" %>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<c:set var="successWidth" value="${test.percentSuccess * 100}"/>
<c:set var="failedWidth" value="${100 - successWidth}"/>

<div class="progress" style="width: 80px;float:left;">
    <div class="progress-bar progress-bar-success" role="progressbar" aria-valuenow="${test.percentSuccess}%"
         aria-valuemin="0" aria-valuemax="100" style="width: ${successWidth}%"></div>
    <div class="progress-bar progress-bar-danger" role="progressbar" aria-valuenow="${failedWidth}%"
         aria-valuemin="0" aria-valuemax="100" style="width: ${failedWidth}%"></div>
</div>
<div class="fl" style="margin-left: 10px;" title="<cti:msg2 key="yukon.web.modules.dr.home.rfPerformance.statsTooltip"/>">
    <span style="margin-right: 10px;">
        <fmt:formatNumber type="percent" maxFractionDigits="2" value="${test.percentSuccess}" />
    </span>
    <span class="label label-success">${test.numSuccesses}</span>
    <span class="label label-danger">${test.numFailures}</span>
    <span class="label label-default">${test.numUnknowns}</span>
</div>