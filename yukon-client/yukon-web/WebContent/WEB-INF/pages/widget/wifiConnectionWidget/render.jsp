<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<tags:nameValueContainer2>

    <tags:nameValue2 nameKey="yukon.common.attribute.builtInAttribute.COMM_STATUS">
        <cti:pointStatus pointId="${commStatus.pointID}"/>
        <cti:pointValue pointId="${commStatus.pointID}" format="VALUE"/>&nbsp;
        <tags:historicalValue pao="${device}" pointId="${commStatus.pointID}"/>
    </tags:nameValue2>
    
    <tags:nameValue2 nameKey="yukon.common.attribute.builtInAttribute.RADIO_SIGNAL_STRENGTH_INDICATOR">
        <cti:pointValue pointId="${rssi.pointID}" format="VALUE"/>&nbsp;
        <tags:historicalValue pao="${device}" pointId="${rssi.pointID}"/>
    </tags:nameValue2>

</tags:nameValueContainer2>

<div class="dn js-refresh-msg ML15 PT10"><i:inline key="yukon.web.modules.operator.wifi.connection.refreshMsg"/></div>

<div class="action-area">
    <cti:url var="refreshUrl" value="/stars/wifiConnection/refresh/${device.deviceId}"/>
    <form id="wifi-refresh-form" action="${refreshUrl}" method="post">
        <cti:csrfToken/>
        <cti:button nameKey="refresh" icon="icon-arrow-refresh" classes="js-refresh-wifi"/>
    </form>
</div>

<cti:includeScript link="/resources/js/pages/yukon.assets.wifi.connection.js"/>

