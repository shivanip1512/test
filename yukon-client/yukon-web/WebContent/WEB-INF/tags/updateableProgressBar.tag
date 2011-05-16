<%--  updateCallback is the name of  js function that will be passed the values of --%>
<%--  completedItems and totalItems as parameters after they have been used to update the bar --%>
<%--  abortedKey should a boolean updater key, if true, the progress bar will be filled in red for remaining percentage --%>
<%@ attribute name="countKey" required="true" rtexprvalue="true"%>
<%@ attribute name="failureCountKey" rtexprvalue="true" description="If supplied, then this progress bar is treated as a two part success-failure progress bar. The countKey is treated as the successes and the failureCountKey as the failures."%>
<%@ attribute name="totalCountKey" required="false" rtexprvalue="true" description="If supplied, then this total count is kept updated using a dataupdater call and the static totalCount attribute below is ignored. Either this attribute or totalCount is required."%>
<%@ attribute name="totalCount" required="false" type="java.lang.Integer" rtexprvalue="true" description="Either this attribute or totalCountKey is required."%>
<%@ attribute name="isAbortedKey" rtexprvalue="true"%>
<%@ attribute name="hidePercent" type="java.lang.Boolean" rtexprvalue="true"%>
<%@ attribute name="hideCount" type="java.lang.Boolean" rtexprvalue="true"%>
<%@ attribute name="completionCallback" description="Name of a javascript function to call when progress reaches 100%. The function will be called each iteration of the data updater, so the function must manage being called multiple times if needed." rtexprvalue="true"%>
<%@ attribute name="borderClasses" rtexprvalue="true" description="Use this to override the progress bar border styles."%>
<%@ attribute name="barClasses" rtexprvalue="true" description="Use this to override the inner progress bar styles. If this is a two part success / failure progress bar these styles will be applied to both success and failure divs."%>
<%@ attribute name="barSuccessClasses" rtexprvalue="true" description="Use this to override the inner success progress bar styles. Only applicable for success / failure progress bar."%>
<%@ attribute name="barFailureClasses" rtexprvalue="true" description="Use this to override the inner failure progress bar styles. Only applicable for success / failure progress bar."%>
<%@ attribute name="percentClasses" rtexprvalue="true" description="Use this to override the percent styles."%>
<%@ attribute name="countClasses" rtexprvalue="true" description="Use this to override the count styles."%>

<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti" %>
    
<cti:includeScript link="/JavaScript/bulkDataUpdaterCallbacks.js"/>
<cti:uniqueIdentifier var="pbarId" prefix="pbar_"/>

<c:if test="${empty pageScope.hidePercent}">
    <c:set var="hidePercent" value="false" />
</c:if>

<c:if test="${empty pageScope.hideCount}">
	<c:set var="hideCount" value="false"/>
</c:if>

<div id="progressContainer_${pbarId}" class="progressBar">
    <div class="progressBarBorder box fl ${pageScope.borderClasses}">
        <c:choose>
            <c:when test="${empty pageScope.failureCountKey}">
                <div class="progressBarInner ${pageScope.barClasses}"></div>
            </c:when>
            <c:otherwise>
                <div class="progressBarInnerFailure ${pageScope.barClasses} ${pageScope.barSuccessClasses}"></div>
                <div class="progressBarInnerSuccess ${pageScope.barClasses} ${pageScope.barFailureClasses}"></div>
            </c:otherwise>
        </c:choose>
    </div>
    <div class="progressBarPercentComplete box fl ${pageScope.percentClasses}" <c:if test="${hidePercent}">style="display:none;"</c:if>>0%</div>
    <div class="progressBarCompletedCount box fl ${pageScope.countClasses}" <c:if test="${hideCount}">style="display:none;"</c:if>><span></span>/${totalCount}</div>
</div>

<c:choose>
    <c:when test="${empty pageScope.completionCallback}">
        <c:choose>
            <c:when test="${empty pageScope.failureCountKey}">
                <c:choose>
                    <c:when test="${empty totalCountKey}">
                        <cti:dataUpdaterCallback function="updateProgressBar('${pbarId}', ${totalCount})"
                            initialize="true" completedCount="${countKey}"/>
                    </c:when>
                    <c:otherwise>
                        <cti:dataUpdaterCallback function="updateProgressBarWithDynamicTotal('${pbarId}')"
                            initialize="true" completedCount="${countKey}" 
                            totalCount="${totalCountKey}" />
                    </c:otherwise>
                </c:choose>
            </c:when>
            <c:otherwise>
                <cti:dataUpdaterCallback function="updateSuccessFailureProgressBar('${pbarId}')"
                    initialize="true" successCompletedCount="${countKey}" failureCompletedCount="${failureCountKey}"
                    totalCount="${totalCountKey}" />
            </c:otherwise>
        </c:choose>
    </c:when>
    <c:otherwise>
        <c:choose>
            <c:when test="${empty pageScope.failureCountKey}">
                <c:choose>
                    <c:when test="${empty totalCountKey}">
                        <cti:dataUpdaterCallback function="updateProgressBar('${pbarId}', ${totalCount},  ${pageScope.completionCallback})"
                            initialize="true" completedCount="${countKey}" />
                    </c:when>
                    <c:otherwise>
                        <cti:dataUpdaterCallback function="updateProgressBarWithDynamicTotal('${pbarId}', ${pageScope.completionCallback})"
                            initialize="true" completedCount="${countKey}" totalCount="${totalCountKey}" />
                    </c:otherwise>
                </c:choose>
            </c:when>
            <c:otherwise>
                <cti:dataUpdaterCallback function="updateSuccessFailureProgressBar('${pbarId}', ${pageScope.completionCallback})"
                    initialize="true" successCompletedCount="${countKey}" failureCompletedCount="${failureCountKey}"
                    totalCount="${totalCountKey}" />
            </c:otherwise>
        </c:choose>
    </c:otherwise>
</c:choose>

<c:if test="${not empty pageScope.isAbortedKey}">
    <cti:dataUpdaterCallback function="abortProgressBar('${pbarId}')" initialize="true" isAborted="${pageScope.isAbortedKey}"/>
</c:if>