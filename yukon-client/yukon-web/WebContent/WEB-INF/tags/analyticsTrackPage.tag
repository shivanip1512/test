<%@ tag body-content="empty"%>

<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>

<cti:includeScript link="/JavaScript/yukon/util/analytics_manager.js"/>

<c:set var="disableAnalytics" value="false"/>
<cti:checkGlobalRolesAndProperties value="DISABLE_ANALYTICS">
	<c:set var="disableAnalytics" value="true"/>
</cti:checkGlobalRolesAndProperties>

<c:if test="${!disableAnalytics}">
	<c:set var="devMode" value="false"/>
	<cti:checkGlobalRolesAndProperties value="DEVELOPMENT_MODE">
		<c:set var="devMode" value="true"/>
	</cti:checkGlobalRolesAndProperties>
	<cti:checkYukonVersionUndefined>
		<c:set var="devMode" value="true"/>
	</cti:checkYukonVersionUndefined>

	<c:set var="cooperTrackingIdKey" value="yukon.web.googleAnalytics.trackingId.production.cooper"/>
	<c:set var="additionalTrackingIdKey" value="yukon.web.googleAnalytics.trackingId.production.additional"/>
	<c:if test="${devMode}">
		<c:set var="cooperTrackingIdKey" value="yukon.web.googleAnalytics.trackingId.development.cooper"/>
		<c:set var="additionalTrackingIdKey" value="yukon.web.googleAnalytics.trackingId.development.additional"/>
	</c:if>

	<cti:msg2 var="cooperTrackingId" key="${cooperTrackingIdKey}"/>
	<cti:msg2 var="additionalTrackingId" key="${additionalTrackingIdKey}"/>

	<!-- Only add GA if we have tracking Id(s) -->
	<c:if test="${fn:length(cooperTrackingId) != 0 || fn:length(additionalTrackingId) != 0}">
		<script>
            Yukon.Util.AnalyticsManager.setTrackingIds({cooper_tracking_id: "${cooperTrackingId}",
                additional_tracking_id: "${additionalTrackingId}"});
		</script>
	</c:if>
</c:if>