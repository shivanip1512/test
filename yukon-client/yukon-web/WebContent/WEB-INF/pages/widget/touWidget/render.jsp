<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti"%>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n"%>

<cti:includeScript link="/JavaScript/touPreviousReadings.js"/>

<c:if test="${touAttributesAvailable}">
<cti:url var="touSpecificsUrl" value="/meter/touPreviousReadings"><cti:param name="deviceId" value="${meter.deviceId}"/></cti:url>
<div id="touDialog" title="<cti:msg2 key=".title"/>" class="scrollingContainer_large dn"></div>
<script type="text/javascript">
jQuery(function() {
    jQuery('#touPopupLink').click(function(event) {
        jQuery.ajax({
            url: "${touSpecificsUrl}",
            success: function(transport) {
                jQuery("#touDialog").html(transport);
                jQuery("#touDialog").dialog({width: "auto", minWidth: 400});
            }
        });
    });
});
</script>
</c:if>

<c:choose>
	<c:when test="${touAttributesAvailable}">
		<div id="touTable">
			<tags:touAttribute headerKey="yukon.web.widgets.touWidget.rateA"
			                 usageAttribute="${USAGE_RATE_A}"
			                 peakAttribute="${PEAK_DEMAND_RATE_A}" />
		
			<tags:touAttribute headerKey="yukon.web.widgets.touWidget.rateB"
			                 usageAttribute="${USAGE_RATE_B}"
			                 peakAttribute="${PEAK_DEMAND_RATE_B}" />
			        
			<tags:touAttribute headerKey="yukon.web.widgets.touWidget.rateC"
			                 usageAttribute="${USAGE_RATE_C}"
			                 peakAttribute="${PEAK_DEMAND_RATE_C}" />
			        
			<tags:touAttribute headerKey="yukon.web.widgets.touWidget.rateD"
			                 usageAttribute="${USAGE_RATE_D}"
			                 peakAttribute="${PEAK_DEMAND_RATE_D}" />
		</div>
		<div id="${widgetParameters.widgetId}_results"></div>
		<div class="actionArea clearfix full_width">
            <a id="touPopupLink" href="javascript:void(0)" class="fl"><cti:msg2 key=".previousReadingsLink"/></a>
            <tags:widgetActionUpdate hide="${!readable}" method="read" nameKey="read" container="${widgetParameters.widgetId}_results"/>
        </div>
	</c:when>
	<c:otherwise><i:inline key=".notConfigured"/></c:otherwise>
</c:choose>