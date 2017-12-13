<%@ page trimDirectiveWhitespaces="true" %>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<cti:msgScope paths="modules.tools.map,modules.operator.mapNetwork">
<tags:nameValueContainer2 tableClass="name-collapse">
    <tags:nameValue2 nameKey=".device"><cti:paoDetailUrl yukonPao="${pao}" newTab="true">${pao.name}</cti:paoDetailUrl></tags:nameValue2>
    <c:if test="${showMeterNumber}">
        <tags:nameValue2 nameKey=".meterNumber">${fn:escapeXml(pao.meter.meterNumber)}</tags:nameValue2>
    </c:if>
    <c:if test="${!empty gatewayIPAddress}">
        <tags:nameValue2 nameKey=".ipAddress">${fn:escapeXml(gatewayIPAddress)}</tags:nameValue2>
    </c:if>
    <c:if test="${!empty macAddress}">
        <tags:nameValue2 nameKey=".macAddress">${fn:escapeXml(macAddress)}</tags:nameValue2>
    </c:if>
    <c:if test="${!empty serialNumber}">
        <tags:nameValue2 nameKey=".serialNbr">${fn:escapeXml(serialNumber)}</tags:nameValue2>
    </c:if>
    <c:if test="${!empty primaryGateway}">
        <tags:nameValue2 nameKey=".primaryGateway">${fn:escapeXml(primaryGateway)}</tags:nameValue2>
    </c:if>
    <tags:nameValue2 nameKey=".type">${fn:escapeXml(pao.paoIdentifier.paoType.paoTypeName)}</tags:nameValue2>
    <tags:nameValue2 nameKey=".status" nameClass="dn js-status-display" valueClass="dn js-status js-status-display"></tags:nameValue2>
    <c:if test="${showRoute}">
        <tags:nameValue2 nameKey=".route">${fn:escapeXml(pao.meter.route)}</tags:nameValue2>
    </c:if>
    <c:if test="${showAddressOrSerial}">
        <tags:nameValue2 nameKey=".serialOrAddress">${fn:escapeXml(pao.meter.serialOrAddress)}</tags:nameValue2>
    </c:if>

    <tags:nameValue2 nameKey=".distance" nameClass="dn js-distance-display" valueClass="dn js-distance-display"><span class="js-distance"></span><i:inline key=".distance.miles"/></tags:nameValue2>
</tags:nameValueContainer2>

<c:forEach items="${attributes}" var="attr">
    <div>
        <h4><i:inline key="${attr}"/></h4>
        <div style="padding: 2px 10px;"><tags:attributeValue attribute="${attr}" pao="${pao}"/></div>
    </div>
</c:forEach>

<cti:checkRolesAndProperties value="ENDPOINT_PERMISSION" level="UPDATE">
<div class="action-area">
    
    <div class="dn" 
        data-dialog 
        id="confirm-delete" 
        data-title="<cti:msg2 key=".deleteCoordinates.title"/>" 
        data-ok-text="<cti:msg2 key=".deleteCoordinates.delete"/>" 
        data-event="yukon:tools:map:delete-coordinates" 
        data-cancel-class="cancel-delete">
        
        <i:inline key=".deleteCoordinates" arguments="${pao.name}"/>
    </div>
    
    <cti:button id="remove-pin" renderMode="image" icon="icon-map-delete" data-popup="#confirm-delete" data-pao="${pao.paoIdentifier.paoId}" nameKey="deleteCoordinates"/>
</div>
</cti:checkRolesAndProperties>

</cti:msgScope>