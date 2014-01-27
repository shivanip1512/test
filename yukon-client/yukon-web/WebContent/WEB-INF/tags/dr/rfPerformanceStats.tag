<%@ tag body-content="empty" %>

<%@ attribute name="test" required="true" type="com.cannontech.web.dr.model.BroadcastPerformanceStats" %>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>

<div class="progress" style="width: 100px;float:left;">
    <c:choose>
        <c:when test="${test.success / (test.success + test.failed) > .98}"><c:set var="barType" value="progress-bar-success"/></c:when>
        <c:when test="${test.success / (test.success + test.failed) > .90}"><c:set var="barType" value="progress-bar-warning"/></c:when>
        <c:otherwise><c:set var="barType" value="progress-bar-danger"/></c:otherwise>
    </c:choose>
    <div class="progress-bar ${barType}" role="progressbar" aria-valuenow="${test.successPercent}" aria-valuemin="0" aria-valuemax="100" style="width: ${test.successPercent}"></div>
</div>
<div class="fl" style="margin-left: 10px;" title="<cti:msg2 key="yukon.web.modules.dr.home.rfPerformance.statsTooltip"/>">
    <span style="margin-right: 10px;">${test.successPercent}</span>
    <span class="label label-success">${test.success}</span>
    <span class="label label-danger">${test.failed}</span>
    <span class="label label-default">${test.unknown}</span>
</div>