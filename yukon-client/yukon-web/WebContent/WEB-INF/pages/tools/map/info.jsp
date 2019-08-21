<%@ page trimDirectiveWhitespaces="true" %>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cm" tagdir="/WEB-INF/tags/contextualMenu" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<cti:msgScope paths="modules.tools.map,modules.operator.mapNetwork">
<tags:nameValueContainer2 tableClass="name-collapse">
    <tags:nameValue2 nameKey=".device">
        <cti:paoDetailUrl yukonPao="${pao}" newTab="true">${fn:escapeXml(pao.name)}</cti:paoDetailUrl>
        <div class="fr" style="padding-left: 10px">
            <c:if test="${hasNotes}">
                <cti:msg2 var="viewAllNotesTitle" key="yukon.web.common.paoNotesSearch.viewAllNotes"/>
                <cti:icon icon="icon-notes-pin" classes="js-view-all-notes cp" title="${viewAllNotesTitle}" data-pao-id="${pao.paoIdentifier.paoId}"/>
            </c:if>
            <cm:dropdown icon="icon-cog" triggerClasses="js-cog-menu fr">
                <cm:dropdownOption key=".mapDevice" classes="js-device-map" data-device-id="${pao.paoIdentifier.paoId}" showIcon="false"></cm:dropdownOption>
                <c:if test="${pao.paoIdentifier.paoType.isRfn()}">
                    <cm:dropdownOption key=".viewNeighbors" classes="js-device-neighbors" data-device-id="${pao.paoIdentifier.paoId}" showIcon="false"></cm:dropdownOption>
                    <c:if test="${!pao.paoIdentifier.paoType.isRfGateway()}">
                        <cm:dropdownOption key=".viewPrimaryRoute" classes="js-device-route" data-device-id="${pao.paoIdentifier.paoId}" showIcon="false"></cm:dropdownOption>
                    </c:if>
                </c:if>
                <cti:checkRolesAndProperties value="ENDPOINT_PERMISSION" level="UPDATE">
                    <cm:dropdownOption id="remove-pin" data-device-id="${pao.paoIdentifier.paoId}" key=".deleteCoordinates.delete" data-popup="#confirm-delete" showIcon="false"/>
                </cti:checkRolesAndProperties>
            </cm:dropdown>
        </div>
    </tags:nameValue2>
    <c:if test="${showMeterNumber}">
        <tags:nameValue2 nameKey=".meterNumber">${fn:escapeXml(pao.meter.meterNumber)}</tags:nameValue2>
    </c:if>
    <tags:nameValue2 nameKey=".type">${fn:escapeXml(pao.paoIdentifier.paoType.paoTypeName)}</tags:nameValue2>
    <c:if test="${!empty address}">
        <tags:nameValue2 nameKey=".address">${fn:escapeXml(address)}</tags:nameValue2>
    </c:if>
    <c:if test="${!empty sensorSN}">
        <tags:nameValue2 nameKey=".serialNumber">${fn:escapeXml(sensorSN)}</tags:nameValue2>
    </c:if>
    <c:if test="${!empty primaryGateway}">
        <tags:nameValue2 nameKey=".primaryGateway"><cti:paoDetailUrl yukonPao="${primaryGateway}" newTab="true">${fn:escapeXml(primaryGatewayName)}</cti:paoDetailUrl></tags:nameValue2>
    </c:if>
    <c:if test="${!empty gatewayIPAddress}">
        <tags:nameValue2 nameKey=".ipAddress">${fn:escapeXml(gatewayIPAddress)}</tags:nameValue2>
    </c:if>
    <c:if test="${!empty macAddress}">
        <tags:nameValue2 nameKey=".macAddress">${fn:escapeXml(macAddress)}</tags:nameValue2>
    </c:if>
    <c:if test="${!empty nodeSN}">
        <tags:nameValue2 nameKey=".nodeSN">${fn:escapeXml(nodeSN)}</tags:nameValue2>
    </c:if>
    <cti:msgScope paths="yukon.web.widgets.RfnDeviceMetadataWidget.WifiSuperMeterData">
        <c:if test="${!empty channelNum}">
            <tags:nameValue2 nameKey=".channelNum">${fn:escapeXml(channelNum)}</tags:nameValue2>
        </c:if>
        <c:if test="${!empty rssi}">
            <tags:nameValue2 nameKey=".rssi">${fn:escapeXml(rssi)}</tags:nameValue2>
        </c:if>
        <c:if test="${!empty configuredApBssid}">
            <tags:nameValue2 nameKey=".configuredApBssid">${fn:escapeXml(configuredApBssid)}</tags:nameValue2>
        </c:if>
        <c:if test="${!empty connectedApBssid}">
            <tags:nameValue2 nameKey=".connectedApBssid">${fn:escapeXml(connectedApBssid)}</tags:nameValue2>
        </c:if>
        <c:if test="${!empty apSsid}">
            <tags:nameValue2 nameKey=".apSsid">${fn:escapeXml(apSsid)}</tags:nameValue2>
        </c:if>
        <c:if test="${!empty securityType}">
            <tags:nameValue2 nameKey=".securityType">${fn:escapeXml(securityType)}</tags:nameValue2>
        </c:if>
        <c:if test="${!empty virtualGatewayIpv6Address}">
            <tags:nameValue2 nameKey=".virtualGatewayIpv6Address">${fn:escapeXml(virtualGatewayIpv6Address)}</tags:nameValue2>
        </c:if>
    </cti:msgScope>
    <c:if test="${!empty deviceStatus}">
        <tags:nameValue2 nameKey=".status" valueClass="js-status">${fn:escapeXml(deviceStatus)}</tags:nameValue2>
    </c:if>
    <c:if test="${showRoute}">
        <tags:nameValue2 nameKey=".route">${fn:escapeXml(pao.meter.route)}</tags:nameValue2>
    </c:if>
    <tags:nameValue2 nameKey=".distance" nameClass="dn js-distance-display" valueClass="dn js-distance-display"><span class="js-distance"></span><i:inline key=".distance.miles"/></tags:nameValue2>
</tags:nameValueContainer2>

<c:if test="${not empty errorMsg}">
    <tags:hideReveal2 styleClass="mw300" titleClass="error" titleKey="yukon.web.modules.tools.map.network.error" showInitially="false">${errorMsg}</tags:hideReveal2>
</c:if>

<c:forEach items="${attributes}" var="attr">
    <div>
        <h4><i:inline key="${attr}"/></h4>
        <div style="padding: 2px 10px;"><tags:attributeValue attribute="${attr}" pao="${pao}"/></div>
    </div>
</c:forEach>

<div class="dn" 
    data-dialog 
    id="confirm-delete" 
    data-title="<cti:msg2 key=".deleteCoordinates.title"/>" 
    data-ok-text="<cti:msg2 key=".deleteCoordinates.delete"/>" 
    data-event="yukon:tools:map:delete-coordinates" 
    data-cancel-class="cancel-delete">
    
    <i:inline key=".deleteCoordinates" arguments="${pao.name}"/>
</div>
</cti:msgScope>