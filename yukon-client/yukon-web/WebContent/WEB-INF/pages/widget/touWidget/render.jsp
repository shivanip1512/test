<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<cti:includeScript link="/resources/js/widgets/yukon.widget.tou.js"/>

<c:if test="${touAttributesAvailable}">
<cti:url var="touSpecificsUrl" value="/meter/touPreviousReadings"><cti:param name="deviceId" value="${meter.deviceId}"/></cti:url>
<div id="touDialog" title="<cti:msg2 key=".title"/>" class="scroll-lg dn"></div>
<script type="text/javascript">
$(function() {
    $('#touPopupLink').click(function(event) {
        $.ajax({
            url: "${touSpecificsUrl}"
        }).done(function (transport, textStatus, jqXHR) {
            $("#touDialog").html(transport);
            $("#touDialog").dialog({width: "auto", minWidth: 400});
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
                               peakAttribute="${PEAK_DEMAND_RATE_A}"/>
        
            <tags:touAttribute headerKey="yukon.web.widgets.touWidget.rateB"
                               usageAttribute="${USAGE_RATE_B}"
                               peakAttribute="${PEAK_DEMAND_RATE_B}"/>
                    
            <tags:touAttribute headerKey="yukon.web.widgets.touWidget.rateC"
                               usageAttribute="${USAGE_RATE_C}"
                               peakAttribute="${PEAK_DEMAND_RATE_C}"/>
                    
            <tags:touAttribute headerKey="yukon.web.widgets.touWidget.rateD"
                               usageAttribute="${USAGE_RATE_D}"
                               peakAttribute="${PEAK_DEMAND_RATE_D}"/>
            <tags:touAttribute headerKey="yukon.web.widgets.touWidget.rateE"
                               usageAttribute="${USAGE_RATE_E}"
                               peakAttribute="${PEAK_DEMAND_RATE_E}"/>
        </div>
        <div id="${widgetParameters.widgetId}_results"></div>
        <div class="action-area clearfix full-width">
            <a id="touPopupLink" href="javascript:void(0)" class="fl"><cti:msg2 key=".previousReadingsLink"/></a>
            <tags:widgetActionUpdate hide="${!readable}" method="read" nameKey="read" 
                    container="${widgetParameters.widgetId}_results" icon="icon-read" classes="M0"/>
        </div>
    </c:when>
    <c:otherwise><i:inline key=".notConfigured"/></c:otherwise>
</c:choose>