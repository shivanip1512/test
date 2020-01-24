<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>

<%@ attribute name="lastRunTimestampValue" required="true" type="com.cannontech.web.multispeak.multispeakSync.LastRunTimestampValue"%>

<cti:includeScript link="/resources/js/common/yukon.ui.progressbar.js"/>
<cti:uniqueIdentifier var="uniqueId"/>

<span id="noLinkableProgressLinkSpan${uniqueId}" style="display:none;">
    <c:choose>
        <c:when test="${lastRunTimestampValue.type.enrollmentType}">
            <cti:dataUpdaterValue type="MSP_ENROLLMENT_SYNC" identifier="LAST_COMPLETED_SYNC_TIME"/>
        </c:when>
        <c:otherwise>
            <cti:dataUpdaterValue type="MSP_DEVICE_GROUP_SYNC" identifier="LAST_COMPLETED_SYNC_${lastRunTimestampValue.type}"/>
        </c:otherwise>
    </c:choose>
</span>

<span id="hasLinkableProgressLinkSpan${uniqueId}" style="display:none;">
    <c:choose>
        <c:when test="${lastRunTimestampValue.type.enrollmentType}">
            <cti:url var="enrollmentProgressUrl" value="/multispeak/setup/multispeakSync/enrollmentProgress"/>
                <a href="${enrollmentProgressUrl}">
                    <cti:dataUpdaterValue type="MSP_ENROLLMENT_SYNC" identifier="STATUS_TEXT_OR_LAST_SYNC_ENROLLMENT"/>
                </a>
        </c:when>
        <c:otherwise>
            <cti:url var="progressUrl" value="/multispeak/setup/multispeakSync/progress"/>
                <a href="${progressUrl}">
                    <cti:dataUpdaterValue type="MSP_DEVICE_GROUP_SYNC" identifier="STATUS_TEXT_OR_LAST_SYNC_${lastRunTimestampValue.type}"/>
                </a>
        </c:otherwise>
    </c:choose>
</span>

<c:choose>
    <c:when test="${lastRunTimestampValue.type.enrollmentType}">
        <cti:dataUpdaterCallback function="yukon.ui.progressbar.toggleElementsWhenTrue(['hasLinkableProgressLinkSpan${uniqueId}'], true)" initialize="true" value="MSP_ENROLLMENT_SYNC/HAS_LINKABLE_PROGRESS" />
        <cti:dataUpdaterCallback function="yukon.ui.progressbar.toggleElementsWhenTrue(['noLinkableProgressLinkSpan${uniqueId}'], false)" initialize="true" value="MSP_ENROLLMENT_SYNC/HAS_LINKABLE_PROGRESS" />
        <cti:dataUpdaterCallback function="yukon.ui.progressbar.toggleElementsWhenTrue(['noLinkableProgressLinkSpan${uniqueId}'], true)" initialize="true" value="MSP_ENROLLMENT_SYNC/NO_LINKABLE_PROGRESS" />
        <cti:dataUpdaterCallback function="yukon.ui.progressbar.toggleElementsWhenTrue(['hasLinkableProgressLinkSpan${uniqueId}'], false)" initialize="true" value="MSP_ENROLLMENT_SYNC/NO_LINKABLE_PROGRESS" />
    </c:when>
    <c:otherwise>
        <cti:dataUpdaterCallback function="yukon.ui.progressbar.toggleElementsWhenTrue(['hasLinkableProgressLinkSpan${uniqueId}'], true)" initialize="true" value="MSP_DEVICE_GROUP_SYNC/HAS_LINKABLE_PROGRESS_${lastRunTimestampValue.type}" />
        <cti:dataUpdaterCallback function="yukon.ui.progressbar.toggleElementsWhenTrue(['noLinkableProgressLinkSpan${uniqueId}'], false)" initialize="true" value="MSP_DEVICE_GROUP_SYNC/HAS_LINKABLE_PROGRESS_${lastRunTimestampValue.type}" />
        <cti:dataUpdaterCallback function="yukon.ui.progressbar.toggleElementsWhenTrue(['noLinkableProgressLinkSpan${uniqueId}'], true)" initialize="true" value="MSP_DEVICE_GROUP_SYNC/NO_LINKABLE_PROGRESS_${lastRunTimestampValue.type}" />
        <cti:dataUpdaterCallback function="yukon.ui.progressbar.toggleElementsWhenTrue(['hasLinkableProgressLinkSpan${uniqueId}'], false)" initialize="true" value="MSP_DEVICE_GROUP_SYNC/NO_LINKABLE_PROGRESS_${lastRunTimestampValue.type}" />
    </c:otherwise>
</c:choose>

