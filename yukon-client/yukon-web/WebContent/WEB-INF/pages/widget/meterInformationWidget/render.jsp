<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<tags:nameValueContainer2>
    <tags:nameValue2 nameKey=".deviceName">${fn:escapeXml(meter.name)}</tags:nameValue2>
    <tags:nameValue2 nameKey=".meterNumber">${fn:escapeXml(meter.meterNumber)}</tags:nameValue2>
    <tags:nameValue2 nameKey=".type">
       <tags:paoType yukonPao="${meter}"/>
    </tags:nameValue2>
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
        <tags:nameValue2 nameKey=".serialNumber">${fn:escapeXml(meter.rfnIdentifier.sensorSerialNumber)}</tags:nameValue2>
        <tags:nameValue2 nameKey=".model">${fn:escapeXml(meter.rfnIdentifier.sensorModel)}</tags:nameValue2>
        <tags:nameValue2 nameKey=".manufacturer">${fn:escapeXml(meter.rfnIdentifier.sensorManufacturer)}</tags:nameValue2>
    </c:if>
    <tags:nameValue2 nameKey=".status">
    <c:if test='${meter.disabled}'><span class="fwb error"><i:inline key=".disabled"/></span></c:if>
    <c:if test='${!meter.disabled}'><span class="fwb success"><i:inline key=".enabled"/></span></c:if>
    </tags:nameValue2>
</tags:nameValueContainer2>

<c:if test="${supportsPing}">
    <div class="action-area">
        <div class="dib fl" id="${widgetParameters.widgetId}_results"></div>
        <tags:widgetActionUpdate method="ping" nameKey="ping" container="${widgetParameters.widgetId}_results" 
                icon="icon-ping" classes="M0"/>
    </div>
</c:if>