<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>

<cti:msg var="loadingText" key="yukon.common.loading"/>
<input type="hidden" class="js-loading-text" value="${loadingText}"/>

<div id="js-route-details-container" class="fr MT5 vh dib">
    <cti:button renderMode="image" icon="icon-arrow-refresh" classes="js-request-route-update fr"></cti:button>
    <span class="js-last-update-msg"><cti:msg2 key="yukon.web.modules.operator.comprehensiveMap.routeLastUpdated"/></span>
    <span class="js-last-update-date-time"></span>
</div>