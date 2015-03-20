<%@ attribute name="paoId" required="true" %>
<%@ attribute name="type" required="true" %>

<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti"%>

<span id="dual-bus-msg-${paoId}" class="dn">
    <cti:capControlValue paoId="${paoId}" type="${type}" format="DUALBUS_MESSAGE"/>
</span>

<cti:icon icon="icon-bullet-orange" classes="dn fn js-dual-bus" data-pao-id="${paoId}" data-tooltip="#dual-bus-msg-${paoId}"/>

<cti:dataUpdaterCallback function="yukon.da.updaters.dualBus" initialize="true" value="${type}/${paoId}/DUALBUS"/>