<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti"%>

<%@ attribute name="paoId" required="true" %>
<%@ attribute name="type" required="true" %>

<input type="hidden" id="ltc_mode_${paoId}" value='<cti:capControlValue paoId="${paoId}" type="${type}" format="MODE"/>'>

<%-- LTC in local mode for a normal reason ie: operator set it to local mode. --%>
<span id="ltc_mode_${paoId}_local_normal" style="display: none;">
    <img src="/capcontrol/images/green_local.png" class="tierImg"  alt="">
</span>

<%-- LTC in local mode due to some failure ie: comms lost. --%>
<span id="ltc_mode_${paoId}_local_warning" style="display: none;">
    <img src="/capcontrol/images/yellow_local.png" class="tierImg"  alt="">
</span>

<cti:dataUpdaterCallback function="updateLtcModeIndicator('ltc_mode_${paoId}')" initialize="true" value="${type}/${paoId}/MODE"/>