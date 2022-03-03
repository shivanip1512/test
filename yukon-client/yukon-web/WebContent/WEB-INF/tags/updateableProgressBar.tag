<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>

<%--  abortedKey should a boolean updater key, if true, the progress bar will be filled in red for remaining percentage --%>
<%@ attribute name="countKey" required="true" %>
<%@ attribute name="failureCountKey" description="If supplied, then this progress bar is treated as a two part success-failure progress bar. The countKey is treated as the successes and the failureCountKey as the failures." %>
<%@ attribute name="totalCountKey" description="If supplied, then this total count is kept updated using a dataupdater call and the static totalCount attribute below is ignored. Either this attribute or totalCount is required." %>
<%@ attribute name="totalCount" type="java.lang.Integer" description="Either this attribute or totalCountKey is required." %>
<%@ attribute name="isAbortedKey" %>
<%@ attribute name="hidePercent" type="java.lang.Boolean" %>
<%@ attribute name="hideCount" type="java.lang.Boolean" %>
<%@ attribute name="completionCallback" description="Name of a javascript function to call when progress reaches 100%. The function will be called each iteration of the data updater, so the function must manage being called multiple times if needed." %>

<%@ attribute name="containerClasses" description="Class names applied to the container element." %>
<%@ attribute name="barClasses" description="Class names applied to the progress bar." %>

<cti:includeScript link="/resources/js/common/yukon.ui.progressbar.js"/>
<cti:uniqueIdentifier var="pbarId" prefix="progress-bar-"/>

<c:if test="${empty pageScope.hidePercent}">
    <c:set var="hidePercent" value="false" />
</c:if>

<c:if test="${empty pageScope.hideCount}">
	<c:set var="hideCount" value="false"/>
</c:if>

<div id="${pbarId}" class="progress-bar-container clearfix ${pageScope.containerClasses}">
    <div class="progress dif fl ${pageScope.barClasses}">
        <c:choose>
            <c:when test="${empty pageScope.failureCountKey}">
                <div class="progress-bar"></div>
            </c:when>
            <c:otherwise>
                <div class="progress-bar progress-bar-danger"></div>
                <div class="progress-bar progress-bar-success"></div>
            </c:otherwise>
        </c:choose>
    </div>
    <c:if test="${!hidePercent}">
        <span class="progressbar-percent-complete dib fl" style="margin-left: 10px;">0%</span>
    </c:if>
    <c:if test="${!hideCount}">
        <span class="dib fl" style="margin-left: 10px;">
            <span class="progressbar-completed-count"></span>
            <span>/</span>
            <span class="progressbar-total"><c:if test="${empty totalCountKey}">${totalCount}</c:if></span>
        </span>
    </c:if>
</div>

<c:choose>
    <c:when test="${empty pageScope.completionCallback}">
        <c:choose>
            <c:when test="${empty pageScope.failureCountKey}">
                <c:choose>
                    <c:when test="${empty totalCountKey}">
                        <cti:dataUpdaterCallback function="yukon.ui.progressbar.updateProgressBar('${pbarId}', ${totalCount})"
                            initialize="true" completedCount="${countKey}"/>
                    </c:when>
                    <c:otherwise>
                        <cti:dataUpdaterCallback function="yukon.ui.progressbar.updateProgressBarWithDynamicTotal('${pbarId}')"
                            initialize="true" completedCount="${countKey}" 
                            totalCount="${totalCountKey}" />
                    </c:otherwise>
                </c:choose>
            </c:when>
            <c:otherwise>
                <c:choose>
                    <c:when test="${empty totalCountKey}">
                        <cti:dataUpdaterCallback function="yukon.ui.progressbar.updateSuccessFailureProgressBar('${pbarId}', ${totalCount})"
                            initialize="true" successCompletedCount="${countKey}" failureCompletedCount="${failureCountKey}"/>
                    </c:when>
                    <c:otherwise>
                        <cti:dataUpdaterCallback function="yukon.ui.progressbar.updateSuccessFailureProgressBarWithDynamicTotal('${pbarId}')"
                            initialize="true" successCompletedCount="${countKey}" failureCompletedCount="${failureCountKey}"
                            totalCount="${totalCountKey}" />
                    </c:otherwise>
                </c:choose>
            </c:otherwise>
        </c:choose>
    </c:when>
    <c:otherwise>
        <c:choose>
            <c:when test="${empty pageScope.failureCountKey}">
                <c:choose>
                    <c:when test="${empty totalCountKey}">
                        <cti:dataUpdaterCallback function="yukon.ui.progressbar.updateProgressBar('${pbarId}', ${totalCount},  ${pageScope.completionCallback})"
                            initialize="true" completedCount="${countKey}" />
                    </c:when>
                    <c:otherwise>
                        <cti:dataUpdaterCallback function="updateProgressBarWithDynamicTotal('${pbarId}', ${pageScope.completionCallback})"
                            initialize="true" completedCount="${countKey}" totalCount="${totalCountKey}" />
                    </c:otherwise>
                </c:choose>
            </c:when>
            <c:otherwise>
                <c:choose>
                    <c:when test="${empty totalCountKey}">
                        <cti:dataUpdaterCallback function="yukon.ui.progressbar.updateSuccessFailureProgressBarWithDynamicTotal('${pbarId}', ${totalCount},  ${pageScope.completionCallback})"
                            initialize="true" successCompletedCount="${countKey}" failureCompletedCount="${failureCountKey}"/>
                    </c:when>
                    <c:otherwise>
                        <cti:dataUpdaterCallback function="yukon.ui.progressbar.updateSuccessFailureProgressBarWithDynamicTotal('${pbarId}', ${pageScope.completionCallback})"
                            initialize="true" successCompletedCount="${countKey}" failureCompletedCount="${failureCountKey}"
                            totalCount="${totalCountKey}" />
                    </c:otherwise>
                </c:choose>
            </c:otherwise>
        </c:choose>
    </c:otherwise>
</c:choose>

<c:if test="${not empty pageScope.isAbortedKey}">
    <cti:dataUpdaterCallback function="yukon.ui.progressbar.abortProgressBar('${pbarId}')" initialize="true" isAborted="${pageScope.isAbortedKey}"/>
</c:if>