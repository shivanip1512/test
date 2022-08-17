<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<span class="js-notes-containing-widget"/>
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
            <c:if test='${meter.route != null}'>${fn:escapeXml(meter.route)}</c:if>
            <c:if test='${meter.route == null}'><i:inline key=".notApplicable"/></c:if>
        </tags:nameValue2>
    </c:if>
    <c:if test="${showRFMeshSettings}">
        <tags:nameValue2 nameKey=".serialNumber">${fn:escapeXml(meter.rfnIdentifier.sensorSerialNumber)}</tags:nameValue2>
        <tags:nameValue2 nameKey=".manufacturer">${fn:escapeXml(meter.rfnIdentifier.sensorManufacturer)}</tags:nameValue2>
        <tags:nameValue2 nameKey=".model">${fn:escapeXml(meter.rfnIdentifier.sensorModel)}</tags:nameValue2>
    </c:if>
    <c:if test="${!showRFMeshSettings && !showCarrierSettings}">
        <tags:nameValue2 nameKey=".ports">
            <c:if test='${meter.port != null}'>${meter.port}</c:if>
            <c:if test='${meter.port == null}'><i:inline key=".notApplicable"/></c:if>
        </tags:nameValue2>
    </c:if>
    <tags:nameValue2 nameKey=".status">
        <c:if test='${meter.disabled}'><span class="fwb error"><i:inline key=".disabled"/></span></c:if>
        <c:if test='${!meter.disabled}'><span class="fwb success"><i:inline key=".enabled"/></span></c:if>
    </tags:nameValue2>
    <c:choose>
        <c:when test="${hasNotes}">
            <cti:msg2 var="viewAllTitle" key="yukon.web.common.paoNote.more.title"/>
            <tags:nameValue2 nameKey=".notes">
                ${fn:escapeXml(note)} (<a class="js-view-all-notes js-no-link" href="#" title="${viewAllTitle}" 
                                          data-pao-id="${meter.deviceId}"><i:inline key="yukon.common.viewAll"/></a>)
            </tags:nameValue2>
        </c:when>
        <c:otherwise>
            <div class="js-view-all-notes"></div>
        </c:otherwise>
    </c:choose>

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
    <cti:checkRolesAndProperties value="ENDPOINT_PERMISSION" level="INTERACT">
        <cti:button nameKey="edit" icon="icon-pencil" data-popup="#meter-info-popup"/>
    </cti:checkRolesAndProperties>
</div>
<div class="dn" id="js-pao-notes-popup"></div>
<cti:includeScript link="/resources/js/pages/yukon.tools.paonotespopup.js"/>
<cti:includeScript link="/resources/js/widgets/yukon.widget.meter.info.js"/>