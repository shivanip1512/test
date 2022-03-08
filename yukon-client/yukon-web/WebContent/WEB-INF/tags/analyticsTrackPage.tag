<%@ tag body-content="empty" trimDirectiveWhitespaces="true" description="Google Analytics tracking - any page this tag is added to will send usage information to GA (assuming the enabled attribute is true)"%>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>

<cti:includeScript link="YUKON_ANALYTICS"/>

<%-- Cooper Tracking Ids --%>
<%-- ******************* --%>
<%-- **IMPORTANT** : New Ids need to be generated for every general release of Yukon --%>
<%-- See Confluence: http://cipt0534.nam.ci.root:8090/display/EEST/Google+Analytics --%>
<c:set var="cooperDev" value="UA-30935838-8"/>
<c:set var="cooperGA4Dev" value="G-TWQV742YGD"/>
<c:set var="cooperProd" value="UA-30935838-19"/>
<c:set var="cooperGA4Prod" value="G-JTNYMB32NE"/>

<cti:globalSetting var="enabled" globalSettingType="GOOGLE_ANALYTICS_ENABLED"/>
<cti:globalSetting var="additionalTrackingIds" globalSettingType="GOOGLE_ANALYTICS_TRACKING_IDS"/>

<c:set var="disableAnalytics" value="false"/>
<c:choose>
    <c:when test="${!enabled}">
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
    <c:set var="cooperGA4TrackingId" value="${devMode ? cooperGA4Dev : cooperGA4Prod}"/>

    <%-- Only add GA if we have tracking Id(s) --%>
    <c:if test="${fn:length(cooperTrackingId) != 0 || fn:length(additionalTrackingIds) != 0}">
        <script async src="https://www.googletagmanager.com/gtag/js?id=${cooperTrackingId}"></script>
        <script>
            yukon.analytics.setTrackingIds({cooper_tracking_id: '${cooperTrackingId}',
                cooper_ga4_tracking_id: '${cooperGA4TrackingId}',
                additional_tracking_ids: '${additionalTrackingIds}'});
        </script>
    </c:if>
</c:if>