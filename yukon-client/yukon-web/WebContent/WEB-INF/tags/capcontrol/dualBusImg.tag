<%@ attribute name="paoId" required="true" %>
<%@ attribute name="type" required="true" %>

<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti"%>

<input type="hidden" id="dualBus_${paoId}" value='<cti:capControlValue paoId="${paoId}" type="${type}" format="DUALBUS"/>'>
                    
<span id="dualBus_${paoId}_primary" class="f-has-tooltip" style="display: none;"> 
    <cti:icon icon="icon-bullet-orange"/>
</span>

<span class="f-tooltip" id="dualBusPrimaryPopup_${paoId}" style="display: none;">
    <cti:capControlValue paoId="${paoId}" type="${type}" format="DUALBUS_MESSAGE"/> 
</span>

<span id="dualBus_${paoId}_alternate" class="f-has-tooltip" style="display: none;">
    <cti:icon icon="icon-bullet-orange"/>
</span>

<span class="f-tooltip" id="dualBusAlternatePopup_${paoId}" style="display: none;">
    <cti:capControlValue paoId="${paoId}" type="${type}" format="DUALBUS_MESSAGE"/> 
</span>

<cti:dataUpdaterCallback function="updateDualBusImage('dualBus_${paoId}')" initialize="true" value="${type}/${paoId}/DUALBUS"/>            