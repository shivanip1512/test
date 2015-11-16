<%@ tag body-content="empty" trimDirectiveWhitespaces="true" %>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<%@ attribute name="totalCount" required="true" type="java.lang.Integer" %>
<%@ attribute name="countKey" required="true" %>
<%@ attribute name="progressLabelTextKey" required="true" %>
<%@ attribute name="statusTextKey" required="true" %>
<%@ attribute name="statusClassKey" %>
<%@ attribute name="isAbortedKey" %>
<%@ attribute name="hideCount" %>
<%@ attribute name="completionCallback" description="Name of a javascript function to call when progress reaches 100%. The function will be called each iteration of the data updater, so the function must manage being called multiple times if needed." %>
<%@ attribute name="classes" description="CSS class names applied to the container." %>
<%@ attribute name="barClasses" description="CSS class names applied to the progress bar." %>

<cti:includeScript link="/resources/js/common/yukon.ui.progressbar.js"/>
<cti:uniqueIdentifier var="pDescId" prefix="pgd_"/>

<div class="stacked clearfix ${pageScope.classes}">
    <span class="fwb dib fl"><cti:msg2 key="${progressLabelTextKey}" />:&nbsp;</span>
    <span class="dib fl" style="margin-right:10px;">
        <c:choose>
            <c:when test="${not empty pageScope.statusClassKey}">
                <cti:classUpdater key="${pageScope.statusClassKey}">
                    <span data-class-updater="${pageScope.statusClassKey}" ><span id="progressStatus_${pDescId}"></span></span>
                </cti:classUpdater>
            </c:when>
            <c:otherwise>
                <span id="progressStatus_${pDescId}"></span>
            </c:otherwise>
        </c:choose>
    </span>
    <tags:updateableProgressBar totalCount="${totalCount}" 
                                containerClasses="dib fl" 
                                countKey="${countKey}" 
                                isAbortedKey="${pageScope.isAbortedKey}" 
                                hideCount="${pageScope.hideCount}" 
                                completionCallback="${pageScope.completionCallback}"
                                barClasses="${barClasses}"/>
</div>

<cti:dataUpdaterCallback function="yukon.ui.progressbar.updateProgressStatus('${pDescId}')" initialize="true" statusText="${statusTextKey}" />