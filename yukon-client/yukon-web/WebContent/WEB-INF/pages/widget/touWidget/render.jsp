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
			                 usageAttribute="${TOU_RATE_A_USAGE}"
			                 peakAttribute="${TOU_RATE_A_PEAK_DEMAND}" />
		
			<tags:touAttribute headerKey="yukon.web.widgets.touWidget.rateB"
			                 usageAttribute="${TOU_RATE_B_USAGE}"
			                 peakAttribute="${TOU_RATE_B_PEAK_DEMAND}" />
			        
			<tags:touAttribute headerKey="yukon.web.widgets.touWidget.rateC"
			                 usageAttribute="${TOU_RATE_C_USAGE}"
			                 peakAttribute="${TOU_RATE_C_PEAK_DEMAND}" />
			        
			<tags:touAttribute headerKey="yukon.web.widgets.touWidget.rateD"
			                 usageAttribute="${TOU_RATE_D_USAGE}"
			                 peakAttribute="${TOU_RATE_D_PEAK_DEMAND}" />
		</div>
		<div id="${widgetParameters.widgetId}_results"></div>
		<div class="actionArea clearfix full_width">
            <a id="touPopupLink" href="javascript:void(0)" class="fl"><cti:msg2 key=".previousReadingsLink"/></a>
            <tags:widgetActionUpdate hide="${!readable}" method="read" nameKey="read" container="${widgetParameters.widgetId}_results"/>
        </div>
	</c:when>
	<c:otherwise><i:inline key=".notConfigured"/></c:otherwise>
</c:choose>