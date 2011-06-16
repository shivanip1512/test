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
            <%-- Last Operation was a Tap Down Operation --%>
            <span class="tapLower" style="display: none;">
                <cti:img key="greenDownArrow"/>
            </span>
    
            <%-- Last Operation was a Tap Down Operation --%>
            <%--(Warning (orange) due to some failure ie: comms lost.) --%>
            <span class="tapLowerWarning" style="display: none;">
                <cti:img key="orangeDownArrow"/>
            </span>
            
            <%-- Last Operation was a Tap Up Operation --%>
            <span class="tapRaise" style="display: none;">
                <cti:img key="greenUpArrow"/>
            </span>
            
            <%-- Last Operation was a Tap Up Operation --%>
            <%--(Warning (orange) due to some failure ie: comms lost.) --%>
            <span class="tapRaiseWarning" style="display: none;">
                <cti:img key="orangeUpArrow"/>
            </span>
            
            <%-- Last Operation was a Tap Down Operation and occurred recently. Animated Icon --%>
            <span class="tapLowerRecent" style="display: none;">
                <cti:img key="flashingGreenDownArrow"/>
            </span>
            
            <%-- Last Operation was a Tap Down Operation and occurred recently. Animated Icon --%>
            <%--(Warning (orange) due to some failure ie: comms lost.) --%>
            <span class="tapLowerRecentWarning" style="display: none;">
                <cti:img key="flashingOrangeDownArrow"/>
            </span>
            
            <%-- Last Operation was a Tap Up Operation and occurred recently. Animated Icon --%>
            <span class="tapRaiseRecent" style="display: none;">
                <cti:img key="flashingGreenUpArrow"/>
            </span>
            
            <%-- Last Operation was a Tap Up Operation and occurred recently. Animated Icon --%>
            <%--(Warning (orange) due to some failure ie: comms lost.) --%>
            <span class="tapRaiseRecentWarning" style="display: none;">
                <cti:img key="flashingOrangeUpArrow"/>
            </span>
            
            <%-- Default output --%>
            <span class="tapDefault" style="display: none;">
                <cti:msg2 key="yukon.web.defaults.dashesTwo"/>
            </span>
    
            <%-- Regulator Mode Remote (class hides it by default) --%>
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