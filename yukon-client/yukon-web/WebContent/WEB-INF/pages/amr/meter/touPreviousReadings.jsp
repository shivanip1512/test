<%@ taglib tagdir="/WEB-INF/tags" prefix="tags"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n" %>
<c:if test="${!previousReadingsExist}">
    <div class="empty-list"><i:inline key="yukon.web.widgets.touWidget.previousReadings.notFound"/></div>
</c:if>
<div id="historicalTouTable" class="widgetInternalSection">
	<tags:touPreviousReading headerKey="yukon.web.widgets.touWidget.rateA" attributeReadings="${USAGE_RATE_A}"/>
	<tags:touPreviousReading headerKey="yukon.web.widgets.touWidget.rateB" attributeReadings="${USAGE_RATE_B}"/>
	<tags:touPreviousReading headerKey="yukon.web.widgets.touWidget.rateC" attributeReadings="${USAGE_RATE_C}"/>
	<tags:touPreviousReading headerKey="yukon.web.widgets.touWidget.rateD" attributeReadings="${USAGE_RATE_D}"/>
    <tags:touPreviousReading headerKey="yukon.web.widgets.touWidget.rateE" attributeReadings="${USAGE_RATE_E}"/>
</div>