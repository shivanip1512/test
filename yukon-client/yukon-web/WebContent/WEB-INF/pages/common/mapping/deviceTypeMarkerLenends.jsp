<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n"%>

<div>
    <b><i:inline key="yukon.web.modules.operator.comprehensiveMap.deviceTypeLegends"/>:</b>
    <div class="js-legend-device-types-markers">
        <cti:url value="/WebConfig/yukon/Icons/marker-meter-elec-grey.svg" var="electricMeterMarkerURL"/>
        <img alt="" src="${electricMeterMarkerURL}" height="25" width="25"><i:inline key="yukon.common.rfnMeters"/>&nbsp;&nbsp;
        <cti:url value="/WebConfig/yukon/Icons/marker-meter-plc-elec-grey.svg" var="plcMeterMarkerURL"/>
        <img alt="" src="${plcMeterMarkerURL}" height="25" width="25"><i:inline key="yukon.common.plcMeters"/>&nbsp;&nbsp;
        <cti:url value="/WebConfig/yukon/Icons/marker-meter-wifi-grey.svg" var="wifiMeterMarkerURL"/>
        <img alt="" src="${wifiMeterMarkerURL}" height="25" width="25"><i:inline key="yukon.common.wifiMeters"/>&nbsp;&nbsp;
        <cti:url value="/WebConfig/yukon/Icons/marker-meter-water-grey.svg" var="waterMeterMarkerURL"/>
        <img alt="" src="${waterMeterMarkerURL}" height="25" width="25"><i:inline key="yukon.common.waterMeters"/>&nbsp;&nbsp;
        <cti:url value="/WebConfig/yukon/Icons/marker-meter-gas-grey.svg" var="gasMeterMarkerURL"/>
        <img alt="" src="${gasMeterMarkerURL}" height="25" width="25"><i:inline key="yukon.common.gasMeters"/>&nbsp;&nbsp;
        <cti:url value="/WebConfig/yukon/Icons/marker-transmitter-grey.svg" var="transmitterMarkerURL"/>
        <img alt="" src="${transmitterMarkerURL}" height="25" width="25"><i:inline key="yukon.common.gateways"/>&nbsp;&nbsp;
        <cti:url value="/WebConfig/yukon/Icons/marker-relay-grey.svg" var="relayMarkerURL"/>
        <img alt="" src="${relayMarkerURL}" height="25" width="25"><i:inline key="yukon.common.rfnRelays"/>&nbsp;&nbsp;
        <cti:url value="/WebConfig/yukon/Icons/marker-relay-cell-grey.svg" var="cellularRelayMarkerURL"/>
        <img alt="" src="${cellularRelayMarkerURL}" height="25" width="25"><i:inline key="yukon.common.cellularRelays"/>
        <cti:url value="/WebConfig/yukon/Icons/marker-lcr-grey.svg" var="lcrMarkerURL"/>
        <img alt="" src="${lcrMarkerURL}" height="25" width="25"><i:inline key="yukon.common.lcrs"/>&nbsp;&nbsp;
        <cti:url value="/WebConfig/yukon/Icons/marker-plc-lcr-grey.svg" var="lcrMarkerURL"/>
        <img alt="" src="${lcrMarkerURL}" height="25" width="25"><i:inline key="yukon.common.twoWayPLCLCRs"/>&nbsp;&nbsp;
        <cti:url value="/WebConfig/yukon/Icons/marker-thermostat-grey.svg" var="thermostatMarkerURL"/>
        <img alt="" src="${thermostatMarkerURL}" height="25" width="25"><i:inline key="yukon.common.thermostats"/>&nbsp;&nbsp;
        <cti:url value="/WebConfig/yukon/Icons/marker-generic.svg" var="genericIconMarkerURL"/>
        <img alt="" src="${genericIconMarkerURL}" height="25" width="25"><i:inline key="yukon.common.others"/>&nbsp;&nbsp;
    </div>
</div>