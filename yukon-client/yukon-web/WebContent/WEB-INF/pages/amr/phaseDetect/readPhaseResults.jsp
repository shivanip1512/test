<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<cti:msgScope paths="yukon.web.modules.amr.phaseDetect">
<c:choose>
    <c:when test="${not empty errorMsg}">
        <span class="error"><i:inline key=".errorRead"/>${errorMsg}</span>
    </c:when>
    <c:when test="${readCanceled}">
        <span class="error"><i:inline key=".readCanceled"/></span>
    </c:when>
    <c:otherwise>
        <div class="dib fl"><i:inline key=".progressBarLabel"/>:&nbsp;</div>
        <div class="dib fl"><tags:updateableProgressBar 
                        totalCount="${totalCount}" 
                        countKey="PHASE_DETECT/${id}/RESULTS_COUNT"
                        barClasses="push-down-4"/></div>
        <cti:url var="cancelReadUrl" value="/amr/phaseDetect/cancelRead"/>
        <cti:msg2 var="cancelRead" key=".cancelRead"/>
        <c:set var="disabled" value="${readComplete ? 'true' : 'false'}"/>
        <cti:button id="cancelReadButton" href="${cancelReadUrl}" value="${cancelRead}" label="${cancelRead}" disabled="${disabled}"/>
        <cti:dataUpdaterEventCallback function="readFinished" id="PHASE_DETECT/${id}/IS_COMPLETE"/>
    </c:otherwise>
</c:choose>
</cti:msgScope>