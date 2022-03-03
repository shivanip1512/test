<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<tags:nameValueContainer2>

    <input type="hidden" id="baseUrl" value="/stars/wifiConnection"/>

    <tags:nameValue2 nameKey="yukon.common.attribute.builtInAttribute.COMM_STATUS">
        <c:choose>
            <c:when test="${wifiData.commStatusPoint.pointID != null}">
                <cti:pointStatus pointId="${wifiData.commStatusPoint.pointID}"/>
                <cti:pointValue pointId="${wifiData.commStatusPoint.pointID}" format="VALUE"/>&nbsp;
                <tags:historicalValue pao="${device}" pointId="${wifiData.commStatusPoint.pointID}" format="DATE_QUALITY"/>
            </c:when>
            <c:otherwise>
                <span class="error"><i:inline key="yukon.common.attributes.pointNotFound"/></span>
            </c:otherwise>
        </c:choose>
    </tags:nameValue2>
    
    <tags:nameValue2 nameKey="yukon.common.attribute.builtInAttribute.RADIO_SIGNAL_STRENGTH_INDICATOR">
        <tags:attributeValue pao="${device}" attribute="${rssiAttribute}"/>
    </tags:nameValue2>

</tags:nameValueContainer2>

<div class="dn js-refresh-msg ML15 PT10"><i:inline key="yukon.web.modules.operator.wifiConnection.queryMsg"/></div>

<div class="action-area">
    <cti:msg2 var="labelBusy" key="yukon.web.components.button.query.labelBusy"/>
    <cti:button nameKey="query" data-busy="${labelBusy}" icon="icon-read" classes="js-refresh-status" data-device-id="${device.paoIdentifier.paoId}"/>
</div>

<cti:includeScript link="/resources/js/pages/yukon.assets.gateway.connectedDevices.js"/>

