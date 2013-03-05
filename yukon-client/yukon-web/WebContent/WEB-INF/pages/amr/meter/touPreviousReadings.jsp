<%@ taglib tagdir="/WEB-INF/tags" prefix="tags"%>
<div id="historicalTouTable" class="widgetInternalSection">
	<tags:touPreviousReading headerKey="yukon.web.widgets.touWidget.rateA" attributeReadings="${TOU_RATE_A_USAGE}"/>
	<tags:touPreviousReading headerKey="yukon.web.widgets.touWidget.rateB" attributeReadings="${TOU_RATE_B_USAGE}"/>
	<tags:touPreviousReading headerKey="yukon.web.widgets.touWidget.rateC" attributeReadings="${TOU_RATE_C_USAGE}"/>
	<tags:touPreviousReading headerKey="yukon.web.widgets.touWidget.rateD" attributeReadings="${TOU_RATE_D_USAGE}"/>
</div>