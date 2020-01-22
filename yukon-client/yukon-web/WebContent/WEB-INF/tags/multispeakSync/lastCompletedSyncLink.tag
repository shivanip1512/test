<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti"%>

<%@ attribute name="lastRunTimestampValue" required="true" type="com.cannontech.web.multispeak.multispeakSync.LastRunTimestampValue"%>

<cti:includeScript link="/resources/js/common/yukon.ui.progressbar.js"/>
<cti:uniqueIdentifier var="uniqueId"/>
				
<span id="noLinkableProgressLinkSpan${uniqueId}" style="display:none;">
    <c:choose>
        <c:when test="${lastRunTimestampValue.type.enrollmentType}">
            <!-- To Do - Add the updaters for enrollment -->
        </c:when>
        <c:otherwise>
            <cti:dataUpdaterValue type="MSP_DEVICE_GROUP_SYNC" identifier="LAST_COMPLETED_SYNC_${lastRunTimestampValue.type}"/>
        </c:otherwise>
    </c:choose>
</span>

<span id="hasLinkableProgressLinkSpan${uniqueId}" style="display:none;">
    <cti:url var="progressUrl" value="/multispeak/setup/multispeakSync/progress"/>
    <c:choose>
        <c:when test="${lastRunTimestampValue.type.enrollmentType}">
            <!-- To Do - Add the updaters for enrollment -->
        </c:when>
        <c:otherwise>
            <a href="${progressUrl}"><cti:dataUpdaterValue type="MSP_DEVICE_GROUP_SYNC" identifier="STATUS_TEXT_OR_LAST_SYNC_${lastRunTimestampValue.type}"/></a>
        </c:otherwise>
    </c:choose>
</span>

<cti:dataUpdaterCallback function="yukon.ui.progressbar.toggleElementsWhenTrue(['hasLinkableProgressLinkSpan${uniqueId}'], true)" initialize="true" value="MSP_DEVICE_GROUP_SYNC/HAS_LINKABLE_PROGRESS_${lastRunTimestampValue.type}" />
<cti:dataUpdaterCallback function="yukon.ui.progressbar.toggleElementsWhenTrue(['noLinkableProgressLinkSpan${uniqueId}'], false)" initialize="true" value="MSP_DEVICE_GROUP_SYNC/HAS_LINKABLE_PROGRESS_${lastRunTimestampValue.type}" />
<cti:dataUpdaterCallback function="yukon.ui.progressbar.toggleElementsWhenTrue(['noLinkableProgressLinkSpan${uniqueId}'], true)" initialize="true" value="MSP_DEVICE_GROUP_SYNC/NO_LINKABLE_PROGRESS_${lastRunTimestampValue.type}" />
<cti:dataUpdaterCallback function="yukon.ui.progressbar.toggleElementsWhenTrue(['hasLinkableProgressLinkSpan${uniqueId}'], false)" initialize="true" value="MSP_DEVICE_GROUP_SYNC/NO_LINKABLE_PROGRESS_${lastRunTimestampValue.type}" />