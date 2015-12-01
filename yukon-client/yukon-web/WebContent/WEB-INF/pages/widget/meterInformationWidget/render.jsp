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
        <tags:nameValue2 nameKey=".manufacturer">${fn:escapeXml(meter.rfnIdentifier.sensorManufacturer)}</tags:nameValue2>
        <tags:nameValue2 nameKey=".model">${fn:escapeXml(meter.rfnIdentifier.sensorModel)}</tags:nameValue2>
    </c:if>
    <tags:nameValue2 nameKey=".status">
    <c:if test='${meter.disabled}'><span class="fwb error"><i:inline key=".disabled"/></span></c:if>
    <c:if test='${!meter.disabled}'><span class="fwb success"><i:inline key=".enabled"/></span></c:if>
    </tags:nameValue2>
</tags:nameValueContainer2>

<%-- Edit Popup --%>
<cti:url var="editUrl" value="/widget/meterInformationWidget/edit">
    <cti:param name="deviceId" value="${meter.deviceId}"/>
    <cti:param name="shortName" value="meterInformationWidget"/>
</cti:url>
<div id="meter-info-popup" data-dialog
    data-event="yukon:widget:meter:info:save"
    data-width="500"
    data-title="<cti:msg2 key=".edit" arguments="${meter.name}"/>"
    data-ok-text="<cti:msg2 key="yukon.common.save"/>"
    data-url="${editUrl}"></div>
    
<div class="action-area">
    <c:if test="${supportsPing}">
            <div class="dib fl" id="${widgetParameters.widgetId}_results"></div>
            <tags:widgetActionUpdate method="ping" nameKey="ping" container="${widgetParameters.widgetId}_results" 
                    icon="icon-ping" classes="M0"/>
    </c:if>
    <cti:checkRolesAndProperties value="ENDPOINT_PERMISSION" level="LIMITED">
        <cti:button nameKey="edit" icon="icon-pencil" data-popup="#meter-info-popup"/>
    </cti:checkRolesAndProperties>
</div>

<cti:includeScript link="/resources/js/widgets/yukon.widget.meter.info.js"/>