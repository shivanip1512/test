<%@ tag body-content="empty" %>

<%@ attribute name="test" required="true" type="com.cannontech.dr.model.PerformanceVerificationEventStats" %>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<div class="progress" style="width: 100px;float:left;">
    <c:choose>
        <c:when test="${test.percentSuccess > 0.98}">
            <c:set var="barType" value="progress-bar-success"/>
        </c:when>
        <c:when test="${test.percentSuccess > 0.90}">
            <c:set var="barType" value="progress-bar-warning"/>
        </c:when>
        <c:otherwise><c:set var="barType" value="progress-bar-danger"/></c:otherwise>
    </c:choose>
    <div class="progress-bar ${barType}" role="progressbar" aria-valuenow="${test.percentSuccess}%"
         aria-valuemin="0" aria-valuemax="100" style="width: ${test.percentSuccess*100}%"></div>
</div>
<div class="fl" style="margin-left: 10px;" title="<cti:msg2 key="yukon.web.modules.dr.home.rfPerformance.statsTooltip"/>">
    <span style="margin-right: 10px;">
        <fmt:formatNumber type="percent" maxFractionDigits="2" value="${test.percentSuccess}" />
    </span>
    <span class="label label-success">${test.numSuccesses}</span>
    <span class="label label-danger">${test.numFailures}</span>
    <span class="label label-default">${test.numUnknowns}</span>
</div>