<%@ taglib tagdir="/WEB-INF/tags" prefix="ct"%>
<div id="touTable" class="widgetInternalSection">

	<ct:touPreviousReading headerKey="yukon.web.widgets.tou.rateA"
                           attributeReadings="${TOU_RATE_A_USAGE}"/>

	<ct:touPreviousReading headerKey="yukon.web.widgets.tou.rateB"
                           attributeReadings="${TOU_RATE_B_USAGE}"/>

	<ct:touPreviousReading headerKey="yukon.web.widgets.tou.rateC"
                           attributeReadings="${TOU_RATE_C_USAGE}"/>

	<ct:touPreviousReading headerKey="yukon.web.widgets.tou.rateD"
                           attributeReadings="${TOU_RATE_D_USAGE}"/>
		
</div>
