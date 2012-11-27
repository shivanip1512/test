<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti"%>

<%@ attribute name="lastRunTimestampValue" required="true" type="com.cannontech.web.multispeak.deviceGroupSync.LastRunTimestampValue"%>

<cti:includeScript link="/JavaScript/bulkDataUpdaterCallbacks.js"/>
<cti:uniqueIdentifier var="uniqueId"/>
				
<span id="noLinkableProgressLinkSpan${uniqueId}" style="display:none;">
	<cti:dataUpdaterValue type="MSP_DEVICE_GROUP_SYNC" identifier="LAST_COMPLETED_SYNC_${lastRunTimestampValue.type}"/>
</span>

<span id="hasLinkableProgressLinkSpan${uniqueId}" style="display:none;">
	<cti:url var="progressUrl" value="/multispeak/setup/deviceGroupSync/progress"/>
	<a href="${progressUrl}"><cti:dataUpdaterValue type="MSP_DEVICE_GROUP_SYNC" identifier="STATUS_TEXT_OR_LAST_SYNC_${lastRunTimestampValue.type}"/></a>
</span>

<cti:dataUpdaterCallback function="toggleElementsWhenTrue(['hasLinkableProgressLinkSpan${uniqueId}'], true)" initialize="true" value="MSP_DEVICE_GROUP_SYNC/HAS_LINKABLE_PROGRESS_${lastRunTimestampValue.type}" />
<cti:dataUpdaterCallback function="toggleElementsWhenTrue(['noLinkableProgressLinkSpan${uniqueId}'], false)" initialize="true" value="MSP_DEVICE_GROUP_SYNC/HAS_LINKABLE_PROGRESS_${lastRunTimestampValue.type}" />
<cti:dataUpdaterCallback function="toggleElementsWhenTrue(['noLinkableProgressLinkSpan${uniqueId}'], true)" initialize="true" value="MSP_DEVICE_GROUP_SYNC/NO_LINKABLE_PROGRESS_${lastRunTimestampValue.type}" />
<cti:dataUpdaterCallback function="toggleElementsWhenTrue(['hasLinkableProgressLinkSpan${uniqueId}'], false)" initialize="true" value="MSP_DEVICE_GROUP_SYNC/NO_LINKABLE_PROGRESS_${lastRunTimestampValue.type}" />