
<%@ taglib tagdir="/WEB-INF/tags" prefix="ct"%>
<div id="touTable" class="widgetInternalSection">

	<ct:touPopup headerKey="yukon.web.widget.touWidget.rateA"
     			 attributeReadings="${TOU_RATE_A_USAGE}" />

	<ct:touPopup headerKey="yukon.web.widget.touWidget.rateB"
     			 attributeReadings="${TOU_RATE_B_USAGE}" />

	<ct:touPopup headerKey="yukon.web.widget.touWidget.rateC"
     			 attributeReadings="${TOU_RATE_C_USAGE}" />

	<ct:touPopup headerKey="yukon.web.widget.touWidget.rateD"
     			 attributeReadings="${TOU_RATE_D_USAGE}" />
		
</div>
