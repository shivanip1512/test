<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n"%>

<tags:nameValueContainer2>
	<tags:nameValue2 nameKey=".deviceName">${meter.name}</tags:nameValue2>
	<tags:nameValue2 nameKey=".meterNumber">${meter.meterNumber}</tags:nameValue2>
	<tags:nameValue2 nameKey=".type"><span class="fl">${deviceType}&nbsp;</span><cti:paoTypeIcon yukonPao="${meter}" /></tags:nameValue2>
    <c:if test="${showCarrierSettings}">
    	<tags:nameValue2 nameKey=".physicalAddress">
        	<c:if test='${meter.address != null}'>${meter.address}</c:if>
            <c:if test='${meter.address == null}'><i:inline key=".notApplicable"/></c:if>
        </tags:nameValue2>
    	<tags:nameValue2 nameKey=".route">
        	<c:if test='${meter.route != null}'>${meter.route}</c:if>
            <c:if test='${meter.route == null}'><i:inline key=".notApplicable"/></c:if>
        </tags:nameValue2>
    </c:if>
    <c:if test="${showRFMeshSettings}">
        <tags:nameValue2 nameKey=".serialNumber">${rfnMeter.meterIdentifier.sensorSerialNumber}</tags:nameValue2>
        <tags:nameValue2 nameKey=".model">${rfnMeter.meterIdentifier.sensorModel}</tags:nameValue2>
        <tags:nameValue2 nameKey=".manufacturer">${rfnMeter.meterIdentifier.sensorManufacturer}</tags:nameValue2>
    </c:if>
	<tags:nameValue2 nameKey=".status">
	<c:if test='${meter.disabled}'><span style="font-weight:bold;color:#CC0000;"><i:inline key=".disabled"/></span></c:if>
    <c:if test='${!meter.disabled}'><span style="font-weight:bold;color:#006633;"><i:inline key=".enabled"/></span></c:if>
    </tags:nameValue2>
</tags:nameValueContainer2>

<c:if test="${supportsPing}">
    <br>
    <div id="${widgetParameters.widgetId}_results"></div>
    <div style="text-align: right">
    	<tags:widgetActionUpdate method="ping" nameKey="ping" container="${widgetParameters.widgetId}_results" />
    </div>
</c:if>
