<%@ taglib tagdir="/WEB-INF/tags" prefix="tags"%>
<div id="historicalTouTable" class="widgetInternalSection">
	<tags:touPreviousReading headerKey="yukon.web.widgets.touWidget.rateA" attributeReadings="${USAGE_RATE_A}"/>
	<tags:touPreviousReading headerKey="yukon.web.widgets.touWidget.rateB" attributeReadings="${USAGE_RATE_B}"/>
	<tags:touPreviousReading headerKey="yukon.web.widgets.touWidget.rateC" attributeReadings="${USAGE_RATE_C}"/>
	<tags:touPreviousReading headerKey="yukon.web.widgets.touWidget.rateD" attributeReadings="${USAGE_RATE_D}"/>
</div>