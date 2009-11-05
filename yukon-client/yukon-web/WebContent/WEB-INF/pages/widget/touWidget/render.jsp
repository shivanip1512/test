<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti"%>
<%@ taglib tagdir="/WEB-INF/tags" prefix="tags"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>

<cti:includeScript link="/JavaScript/touPreviousReadings.js"/>

<tags:simpleDialog id="touDialog"/>

<c:choose>
	<c:when test="${touAttributesAvailable}">
		<div id="popupDiv" style="text-align:right;font-size:12px;">
			<cti:url var="touSpecificsUrl" value="/spring/meter/touPreviousReadings">
		    	<cti:param name="deviceId" value="${meter.deviceId}"/>
		    </cti:url>
		    <a id="touPopupLink" href="javascript:void(0)"
		       style="align:right"
		       onclick="openSimpleDialog('touDialog', '${touSpecificsUrl}', '<cti:msg key="yukon.web.modules.widgets.touWidget.title"/>')">
		        <cti:msg key="yukon.web.modules.widgets.touWidget.previousReadingsLink" />
		    </a>
		</div>
		<div id="touTable">
			<tags:touAttribute headerKey="yukon.web.modules.widgets.touWidget.rateA"
			                 usageAttribute="${TOU_RATE_A_USAGE}"
			                 peakAttribute="${TOU_RATE_A_PEAK_DEMAND}" />
		
			<tags:touAttribute headerKey="yukon.web.modules.widgets.touWidget.rateB"
			                 usageAttribute="${TOU_RATE_B_USAGE}"
			                 peakAttribute="${TOU_RATE_B_PEAK_DEMAND}" />
			        
			<tags:touAttribute headerKey="yukon.web.modules.widgets.touWidget.rateC"
			                 usageAttribute="${TOU_RATE_C_USAGE}"
			                 peakAttribute="${TOU_RATE_C_PEAK_DEMAND}" />
			        
			<tags:touAttribute headerKey="yukon.web.modules.widgets.touWidget.rateD"
			                 usageAttribute="${TOU_RATE_D_USAGE}"
			                 peakAttribute="${TOU_RATE_D_PEAK_DEMAND}" />
		</div>
	
		<div id="${widgetParameters.widgetId}_results"></div>
		<div style="text-align: right">
			<tags:widgetActionUpdate hide="${!readable}" method="read" label="Read Now"
				labelBusy="Reading" container="${widgetParameters.widgetId}_results" />
		</div>
	</c:when>
	<c:otherwise>
		<cti:msg key="yukon.web.modules.widgets.touWidget.notConfigured" />
	</c:otherwise>
</c:choose>