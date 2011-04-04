<%@ taglib tagdir="/WEB-INF/tags" prefix="ct"%>
<div id="touTable" class="widgetInternalSection">

	<ct:touPreviousReading headerKey="yukon.web.modules.widgets.touWidget.rateA"
                           attributeReadings="${TOU_RATE_A_USAGE}" />

	<ct:touPreviousReading headerKey="yukon.web.modules.widgets.touWidget.rateB"
                           attributeReadings="${TOU_RATE_B_USAGE}" />

	<ct:touPreviousReading headerKey="yukon.web.modules.widgets.touWidget.rateC"
                           attributeReadings="${TOU_RATE_C_USAGE}" />

	<ct:touPreviousReading headerKey="yukon.web.modules.widgets.touWidget.rateD"
                           attributeReadings="${TOU_RATE_D_USAGE}" />
		
</div>
