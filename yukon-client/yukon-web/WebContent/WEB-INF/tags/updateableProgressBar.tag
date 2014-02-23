<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti" %>

<%--  updateCallback is the name of  js function that will be passed the values of --%>
<%--  completedItems and totalItems as parameters after they have been used to update the bar --%>
<%--  abortedKey should a boolean updater key, if true, the progress bar will be filled in red for remaining percentage --%>
<%@ attribute name="countKey" required="true" %>
<%@ attribute name="failureCountKey" description="If supplied, then this progress bar is treated as a two part success-failure progress bar. The countKey is treated as the successes and the failureCountKey as the failures." %>
<%@ attribute name="totalCountKey" description="If supplied, then this total count is kept updated using a dataupdater call and the static totalCount attribute below is ignored. Either this attribute or totalCount is required." %>
<%@ attribute name="totalCount" type="java.lang.Integer" description="Either this attribute or totalCountKey is required." %>
<%@ attribute name="isAbortedKey" %>
<%@ attribute name="hidePercent" type="java.lang.Boolean" %>
<%@ attribute name="hideCount" type="java.lang.Boolean" %>
<%@ attribute name="completionCallback" description="Name of a javascript function to call when progress reaches 100%. The function will be called each iteration of the data updater, so the function must manage being called multiple times if needed." %>
<%@ attribute name="borderClasses" description="Use this to override the progress bar border styles." %>
<%@ attribute name="barClasses" description="Use this to override the inner progress bar styles. If this is a two part success / failure progress bar these styles will be applied to both success and failure divs." %>
<%@ attribute name="barSuccessClasses" description="Use this to override the inner success progress bar styles. Only applicable for success / failure progress bar." %>
<%@ attribute name="barFailureClasses" description="Use this to override the inner failure progress bar styles. Only applicable for success / failure progress bar." %>
<%@ attribute name="percentClasses" description="Use this to override the percent styles." %>
<%@ attribute name="countClasses" description="Use this to override the count styles." %>
<%@ attribute name="containerClasses" description="Classes on the container" %>

<cti:includeScript link="/JavaScript/yukon.ui.progressbar.js"/>
<cti:uniqueIdentifier var="pbarId" prefix="pbar_"/>

<c:if test="${empty pageScope.hidePercent}">
    <c:set var="hidePercent" value="false" />
</c:if>

<c:if test="${empty pageScope.hideCount}">
	<c:set var="hideCount" value="false"/>
</c:if>

<div id="progressContainer_${pbarId}" class="progressbar ${pageScope.containerClasses}">
    <div class="progressbar-border ${pageScope.borderClasses}">
        <c:choose>
            <c:when test="${empty pageScope.failureCountKey}">
                <div class="progressbar-inner ${pageScope.barClasses}"></div>
            </c:when>
            <c:otherwise>
                <div class="progressbar-inner-fail ${pageScope.barClasses} ${pageScope.barSuccessClasses}"></div>
                <div class="progressbar-inner-success ${pageScope.barClasses} ${pageScope.barFailureClasses}"></div>
            </c:otherwise>
        </c:choose>
    </div>
    <c:if test="${!hidePercent}">
        <div class="progressbar-percent-complete ${pageScope.percentClasses}">0%</div>
    </c:if>
    <c:if test="${!hideCount}">
        <span class="${pageScope.countClasses}">
            <span class="progressbar-completed-count"></span>
            <span>/</span>
            <span class="progressbar-total">
                <c:if test="${empty totalCountKey}">
                    ${totalCount}
                </c:if>
            </span>
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