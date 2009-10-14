<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti"%>
<%@ taglib tagdir="/WEB-INF/tags" prefix="ct"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<cti:includeScript link="/JavaScript/touPopup.js"/>

<tags:simpleDialog id="touDialog"/>

<div id="popupDiv" style="text-align:right;font-size:12px;">
	<cti:url var="touSpecificsUrl" value="/spring/meter/touPopup">
    	<cti:param name="deviceId" value="${meter.deviceId}"/>
    </cti:url>
    <a id="touPopupLink" href="javascript:void(0)"
       style="align:right"
       onclick="openSimpleDialog('touDialog', '${touSpecificsUrl}', '<cti:msg key="yukon.web.widget.touWidget.title"/>')">
        <cti:msg key="yukon.web.widget.touWidget.popupLink" />
    </a>
</div>
<div id="touTable">
	<ct:tou headerKey="yukon.web.widget.touWidget.rateA"
	        usageAttribute="${TOU_RATE_A_USAGE}"
	        peakAttribute="${TOU_RATE_A_PEAK_DEMAND}" />

	<ct:tou headerKey="yukon.web.widget.touWidget.rateB"
	        usageAttribute="${TOU_RATE_B_USAGE}"
	        peakAttribute="${TOU_RATE_B_PEAK_DEMAND}" />
	        
	<ct:tou headerKey="yukon.web.widget.touWidget.rateC"
	        usageAttribute="${TOU_RATE_C_USAGE}"
	        peakAttribute="${TOU_RATE_C_PEAK_DEMAND}" />
	        
	<ct:tou headerKey="yukon.web.widget.touWidget.rateD"
	        usageAttribute="${TOU_RATE_D_USAGE}"
	        peakAttribute="${TOU_RATE_D_PEAK_DEMAND}" />
</div>

<c:if test="${touAttributesAvailable}">
	<div id="${widgetParameters.widgetId}_results"></div>
	<div style="text-align: right">
		<ct:widgetActionUpdate hide="${!readable}" method="read" label="Read Now"
			labelBusy="Reading" container="${widgetParameters.widgetId}_results" />
	</div>
</c:if>