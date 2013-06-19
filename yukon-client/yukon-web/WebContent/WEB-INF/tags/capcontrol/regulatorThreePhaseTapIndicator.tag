<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n" %>

<%@ attribute name="zone" required="true" type="java.lang.Object" rtexprvalue="true"%>
<%@ attribute name="type" required="true" %>
<%@ attribute name="phaseMap" required="true" type="java.util.Map" rtexprvalue="true"%>

<cti:msgScope paths="modules.capcontrol.ivvc">

<div id="tapContainer_${zone.zoneId}" class="tapContainer">
    <c:forEach items="${phaseMap}" var="phase">
        <span class="phase${phase.value}">
            <span class="lastOpLeft">
                <%-- Last Operation was a Tap Down Operation --%>
                <span class="tapLower" style="display: none;">
                    <cti:icon nameKey="greenDownArrow" icon="icon-arrow-down-green"/>
                </span>
        
                <%-- Last Operation was a Tap Down Operation --%>
                <%--(Warning (orange) due to some failure ie: comms lost.) --%>
                <span class="tapLowerWarning" style="display: none;">
                    <cti:icon nameKey="orangeDownArrow" icon="icon-arrow-down-orange"/>
                </span>
                
                <%-- Last Operation was a Tap Up Operation --%>
                <span class="tapRaise" style="display: none;">
                    <cti:icon nameKey="greenUpArrow" icon="icon-arrow-up-green"/>
                </span>
                
                <%-- Last Operation was a Tap Up Operation --%>
                <%--(Warning (orange) due to some failure ie: comms lost.) --%>
                <span class="tapRaiseWarning" style="display: none;">
                    <cti:icon nameKey="orangeUpArrow" icon="icon-arrow-up-orange"/>
                </span>
                
                <%-- Last Operation was a Tap Down Operation and occurred recently. Animated Icon --%>
                <span class="tapLowerRecent" style="display: none;">
                    <cti:icon nameKey="flashingGreenDownArrow" icon="icon-arrow-down-green-animated"/>
                </span>
                
                <%-- Last Operation was a Tap Down Operation and occurred recently. Animated Icon --%>
                <%--(Warning (orange) due to some failure ie: comms lost.) --%>
                <span class="tapLowerRecentWarning" style="display: none;">
                    <cti:icon nameKey="flashingOrangeDownArrow" icon="icon-arrow-down-orange-animated"/>
                </span>
                
                <%-- Last Operation was a Tap Up Operation and occurred recently. Animated Icon --%>
                <span class="tapRaiseRecent" style="display: none;">
                    <cti:icon nameKey="flashingGreenUpArrow" icon="icon-arrow-up-green-animated"/>
                </span>
                
                <%-- Last Operation was a Tap Up Operation and occurred recently. Animated Icon --%>
                <%--(Warning (orange) due to some failure ie: comms lost.) --%>
                <span class="tapRaiseRecentWarning" style="display: none;">
                    <cti:icon nameKey="flashingOrangeUpArrow" icon="icon-arrow-up-orange-animated"/>
                </span>
                
                <%-- Default output --%>
                <span class="tapDefault" style="display: none;">
                    <cti:msg2 key="yukon.web.defaults.dashesTwo"/>
                </span>
            </span>

            <span class="lastOpRight">
                <%-- Regulator Mode Remote --%>
                <span class="regulatorModeRemote" title="<i:inline key=".mode.remote.tooltip"/>" style="display: none;">
                    <i:inline key=".mode.remote"/>
                </span>
        
                <%-- Regulator Mode Local --%>
                <span class="regulatorModeLocal" title="<i:inline key=".mode.local.tooltip"/>" style="display: none;">
                    <i:inline key=".mode.local"/>
                </span>
        
                <%-- Regulator Mode Local Warning--%>
                <span class="regulatorModeLocalWarning" title="<i:inline key=".mode.localWarning.tooltip"/>" style="display: none;">
                    <i:inline key=".mode.localWarning"/>
                </span>
            </span>
        </span>
    </c:forEach>
</div>

<c:choose>
    <c:when test="${zone.zoneType == 'THREE_PHASE'}">
        <cti:dataUpdaterCallback function="updateRegulatorThreePhaseTapIndicator('${zone.zoneId}','${zone.zoneType}')"
            initialize="true"
            modeA="${type}/${zone.regulators[phaseMap['A']].regulatorId}/MODE"
            modeB="${type}/${zone.regulators[phaseMap['B']].regulatorId}/MODE"
            modeC="${type}/${zone.regulators[phaseMap['C']].regulatorId}/MODE" 
            tapA="${type}/${zone.regulators[phaseMap['A']].regulatorId}/TAP"
            tapB="${type}/${zone.regulators[phaseMap['B']].regulatorId}/TAP"
            tapC="${type}/${zone.regulators[phaseMap['C']].regulatorId}/TAP"
            tapTooltipA="${type}/${zone.regulators[phaseMap['A']].regulatorId}/TAP_TOOLTIP"
            tapTooltipB="${type}/${zone.regulators[phaseMap['B']].regulatorId}/TAP_TOOLTIP"
            tapTooltipC="${type}/${zone.regulators[phaseMap['C']].regulatorId}/TAP_TOOLTIP"/>
    </c:when>
    <c:when test="${zone.zoneType == 'GANG_OPERATED'}">
        <cti:dataUpdaterCallback function="updateRegulatorThreePhaseTapIndicator('${zone.zoneId}','${zone.zoneType}')"
            initialize="true"
            mode="${type}/${zone.regulator.regulatorId}/MODE" 
            value="${type}/${zone.regulator.regulatorId}/TAP"
            tapTooltip="${type}/${zone.regulator.regulatorId}/TAP_TOOLTIP"/>
    </c:when>
    <c:otherwise>
        <!-- SINGLE_PHASE -->
        <cti:dataUpdaterCallback function="updateRegulatorThreePhaseTapIndicator('${zone.zoneId}','${zone.zoneType}','${zone.regulator.phase}')"
            initialize="true"
            mode="${type}/${zone.regulator.regulatorId}/MODE"
            value="${type}/${zone.regulator.regulatorId}/TAP"
            tapTooltip="${type}/${zone.regulator.regulatorId}/TAP_TOOLTIP"/>
    </c:otherwise>
</c:choose>
</cti:msgScope>