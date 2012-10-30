<%@ tag body-content="empty" description="Google Analytics tracking - any page this tag is added to will send usage information to GA (assuming the enabled attribute is true)"%>

<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>

<%@ attribute name="enabled" required="true" type="java.lang.Boolean" description="Should analytics be enabled and this page be tracked?"%>
<%@ attribute name="additionalTrackingIds" required="false" type="java.lang.String" description="Additional tracking ids that can be used by the customer"%>

<cti:includeScript link="/JavaScript/yukon/util/analytics_manager.js"/>

<!-- Cooper Tracking Ids -->
<!-- ******************* -->
<!-- **IMPORTANT** : New Ids need to be generated for every general release of Yukon -->
<c:set var="cooperDev" value="UA-30935838-1"/>
<c:set var="cooperProd" value="UA-30935838-2"/>

<c:set var="disableAnalytics" value="false"/>
<c:choose>
	<c:when test="${!pageScope.enabled}">
	    <c:set var="disableAnalytics" value="true"/>
	</c:when>
	<c:otherwise>
		<cti:checkGlobalRolesAndProperties value="DISABLE_ANALYTICS">
			<c:set var="disableAnalytics" value="true"/>
		</cti:checkGlobalRolesAndProperties>
	</c:otherwise>
</c:choose>

<c:if test="${!disableAnalytics}">
	<c:set var="devMode" value="false"/>
	<cti:checkGlobalRolesAndProperties value="DEVELOPMENT_MODE">
		<c:set var="devMode" value="true"/>
	</cti:checkGlobalRolesAndProperties>
	<cti:checkYukonVersionUndefined>
		<c:set var="devMode" value="true"/>
	</cti:checkYukonVersionUndefined>

	<c:set var="cooperTrackingId" value="${devMode ? cooperDev : cooperProd}"/>

	<!-- Only add GA if we have tracking Id(s) -->
	<c:if test="${fn:length(cooperTrackingId) != 0 || fn:length(additionalTrackingIds) != 0}">
		<script>
            Yukon.Util.AnalyticsManager.setTrackingIds({cooper_tracking_id: "${cooperTrackingId}",
                additional_tracking_ids: "${additionalTrackingIds}"});
		</script>
	</c:if>
</c:if>