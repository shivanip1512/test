<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n" %>

<%@ attribute name="zone" required="true" type="java.lang.Object" rtexprvalue="true"%>
<%@ attribute name="phaseMap" required="true" type="java.util.Map" rtexprvalue="true"%>

<cti:msgScope paths="modules.capcontrol.ivvc">

<div class="js-tap-container" data-zone-id="${zone.zoneId}">
    <c:forEach items="${phaseMap}" var="phase">
        <style>
        </style>
        <span class="js-phase-${phase.value}" style="padding: 0 5px">
            <%-- Last Operation was a Tap Down Operation --%>
            <cti:icon icon="icon-arrow-down-green" classes="dn fn M0" data-last-tap="LOWER_TAP"/>
            <%--(Warning due to some failure ie: comms lost.) --%>
            <cti:icon icon="icon-arrow-down-orange" classes="dn fn M0" data-last-tap="LOWER_TAP" data-tap-warning=""/>

            <%-- Last Operation was a Tap Up Operation --%>
            <cti:icon icon="icon-arrow-up-green" classes="dn fn M0" data-last-tap="RAISE_TAP"/>
            <%--(Warning due to some failure ie: comms lost.) --%>
            <cti:icon icon="icon-arrow-up-orange" classes="dn fn M0" data-last-tap="RAISE_TAP" data-tap-warning=""/>

            <%-- Last Operation was a Tap Down Operation and occurred recently. Animated Icon --%>
            <cti:icon icon="icon-arrow-down-green-animated" classes="dn fn M0" data-last-tap="LOWER_TAP_RECENT"/>
            <%--(Warning due to some failure ie: comms lost.) --%>
            <cti:icon icon="icon-arrow-down-orange-animated" classes="dn fn M0" data-last-tap="LOWER_TAP_RECENT" data-tap-warning=""/>

            <%-- Last Operation was a Tap Up Operation and occurred recently. Animated Icon --%>
            <cti:icon icon="icon-arrow-up-green-animated" classes="dn fn M0" data-last-tap="RAISE_TAP_RECENT"/>
            <%--(Warning due to some failure ie: comms lost.) --%>
            <cti:icon icon="icon-arrow-up-orange-animated" classes="dn fn M0" data-last-tap="RAISE_TAP_RECENT" data-tap-warning=""/>

            <%-- Default output --%>
            <cti:icon icon="icon-blank" classes="fn dn M0" data-last-tap="NONE"/>
            <cti:icon icon="icon-blank" classes="fn dn M0" data-last-tap="NONE" data-tap-warning=""/>

            <%-- Regulator Mode Remote --%>
            <span class="dn" data-regulator-mode="normal" title="<cti:msg2 key=".mode.remote.tooltip"/>">
                <i:inline key=".mode.remote"/>
            </span>

            <%-- Regulator Mode Local --%>
            <span class="dn" data-regulator-mode="local" title="<cti:msg2 key=".mode.local.tooltip"/>">
                <i:inline key=".mode.local"/>
            </span>

            <%-- Regulator Mode Local Warning--%>
            <span class="dn warning" data-regulator-mode="warning" title="<cti:msg2 key=".mode.localWarning.tooltip"/>">
                <i:inline key=".mode.localWarning"/>
            </span>
        </span>
    </c:forEach>
</div>

<c:choose>
    <c:when test="${zone.zoneType == 'THREE_PHASE'}">
        <cti:dataUpdaterCallback function="yukon.da.updaters.regulatorTap('${zone.zoneId}','THREE_PHASE')"
            initialize="true"
            modeA="VOLTAGE_REGULATOR/${zone.regulators[phaseMap['A']].regulatorId}/MODE"
            modeB="VOLTAGE_REGULATOR/${zone.regulators[phaseMap['B']].regulatorId}/MODE"
            modeC="VOLTAGE_REGULATOR/${zone.regulators[phaseMap['C']].regulatorId}/MODE" 
            tapA="VOLTAGE_REGULATOR/${zone.regulators[phaseMap['A']].regulatorId}/TAP"
            tapB="VOLTAGE_REGULATOR/${zone.regulators[phaseMap['B']].regulatorId}/TAP"
            tapC="VOLTAGE_REGULATOR/${zone.regulators[phaseMap['C']].regulatorId}/TAP"
            tapTooltipA="VOLTAGE_REGULATOR/${zone.regulators[phaseMap['A']].regulatorId}/TAP_TOOLTIP"
            tapTooltipB="VOLTAGE_REGULATOR/${zone.regulators[phaseMap['B']].regulatorId}/TAP_TOOLTIP"
            tapTooltipC="VOLTAGE_REGULATOR/${zone.regulators[phaseMap['C']].regulatorId}/TAP_TOOLTIP"/>
    </c:when>
    <c:when test="${zone.zoneType == 'GANG_OPERATED'}">
        <cti:dataUpdaterCallback function="yukon.da.updaters.regulatorTap('${zone.zoneId}','GANG_OPERATED')"
            initialize="true"
            mode="VOLTAGE_REGULATOR/${zone.regulator.regulatorId}/MODE"
            tap="VOLTAGE_REGULATOR/${zone.regulator.regulatorId}/TAP"
            tapTooltip="VOLTAGE_REGULATOR/${zone.regulator.regulatorId}/TAP_TOOLTIP"/>
    </c:when>
    <c:otherwise>
        <!-- SINGLE_PHASE -->
        <cti:dataUpdaterCallback function="yukon.da.updaters.regulatorTap('${zone.zoneId}','SINGLE_PHASE','${zone.regulator.phase}')"
            initialize="true"
            mode="VOLTAGE_REGULATOR/${zone.regulator.regulatorId}/MODE"
            tap="VOLTAGE_REGULATOR/${zone.regulator.regulatorId}/TAP"
            tapTooltip="VOLTAGE_REGULATOR/${zone.regulator.regulatorId}/TAP_TOOLTIP"/>
    </c:otherwise>
</c:choose>
</cti:msgScope>