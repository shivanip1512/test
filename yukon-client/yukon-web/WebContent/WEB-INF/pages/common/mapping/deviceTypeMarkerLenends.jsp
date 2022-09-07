<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n"%>

<div>
    <b><i:inline key="yukon.web.modules.operator.comprehensiveMap.deviceTypeLegends"/>:</b>
    <div class="js-legend-device-types-markers">
        <cti:url value="/WebConfig/yukon/Icons/marker-meter-elec-grey.svg" var="electricMeterMarkerURL"/>
        <span class="dib PR5"><img alt="" src="${electricMeterMarkerURL}" height="25" width="25"><i:inline key="yukon.common.rfnMeters"/></span>
        <cti:url value="/WebConfig/yukon/Icons/marker-meter-plc-elec-grey.svg" var="plcMeterMarkerURL"/>
        <span class="dib PR5"><img alt="" src="${plcMeterMarkerURL}" height="25" width="25"><i:inline key="yukon.common.plcMeters"/></span>
        <cti:url value="/WebConfig/yukon/Icons/marker-meter-wifi-grey.svg" var="wifiMeterMarkerURL"/>
        <span class="dib PR5"><img alt="" src="${wifiMeterMarkerURL}" height="25" width="25"><i:inline key="yukon.common.wifiMeters"/></span>
        <cti:url value="/WebConfig/yukon/Icons/marker-meter-cell-grey.svg" var="cellMeterMarkerURL"/>
        <span class="dib PR5"><img alt="" src="${cellMeterMarkerURL}" height="25" width="25"><i:inline key="yukon.common.cellMeters"/></span>
        <cti:url value="/WebConfig/yukon/Icons/marker-meter-water-grey.svg" var="waterMeterMarkerURL"/>
        <span class="dib PR5"><img alt="" src="${waterMeterMarkerURL}" height="25" width="25"><i:inline key="yukon.common.waterMeters"/></span>
        <cti:url value="/WebConfig/yukon/Icons/marker-meter-gas-grey.svg" var="gasMeterMarkerURL"/>
        <span class="dib PR5"><img alt="" src="${gasMeterMarkerURL}" height="25" width="25"><i:inline key="yukon.common.gasMeters"/></span>
        <cti:url value="/WebConfig/yukon/Icons/marker-transmitter-grey.svg" var="transmitterMarkerURL"/>
        <span class="dib PR5"><img alt="" src="${transmitterMarkerURL}" height="25" width="25"><i:inline key="yukon.common.gateways"/></span>
        <cti:url value="/WebConfig/yukon/Icons/marker-relay-grey.svg" var="relayMarkerURL"/>
        <span class="dib PR5"><img alt="" src="${relayMarkerURL}" height="25" width="25"><i:inline key="yukon.common.rfnRelays"/></span>
        <cti:url value="/WebConfig/yukon/Icons/marker-relay-cell-grey.svg" var="cellularRelayMarkerURL"/>
        <span class="dib PR5"><img alt="" src="${cellularRelayMarkerURL}" height="25" width="25"><i:inline key="yukon.common.cellularRelays"/></span>
        <cti:url value="/WebConfig/yukon/Icons/marker-lcr-grey.svg" var="lcrMarkerURL"/>
        <span class="dib PR5"><img alt="" src="${lcrMarkerURL}" height="25" width="25"><i:inline key="yukon.common.lcrs"/></span>
        <cti:url value="/WebConfig/yukon/Icons/marker-plc-lcr-grey.svg" var="lcrMarkerURL"/>
        <span class="dib PR5"><img alt="" src="${lcrMarkerURL}" height="25" width="25"><i:inline key="yukon.common.twoWayPLCLCRs"/></span>
        <cti:url value="/WebConfig/yukon/Icons/marker-thermostat-grey.svg" var="thermostatMarkerURL"/>
        <span class="dib PR5"><img alt="" src="${thermostatMarkerURL}" height="25" width="25"><i:inline key="yukon.common.thermostats"/></span>
        <cti:url value="/WebConfig/yukon/Icons/marker-generic.svg" var="genericIconMarkerURL"/>
        <span class="dib PR5"><img alt="" src="${genericIconMarkerURL}" height="25" width="25"><i:inline key="yukon.common.others"/></span>
    </div>
</div>