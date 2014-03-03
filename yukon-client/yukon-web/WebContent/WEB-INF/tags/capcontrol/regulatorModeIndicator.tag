<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>

<%@ attribute name="paoId" required="true" %>
<%@ attribute name="type" required="true" %>

<input type="hidden" id="regulator_mode_${paoId}" value='<cti:capControlValue paoId="${paoId}" type="${type}" format="MODE"/>'>

<%-- LTC in local mode for a normal reason ie: operator set it to local mode. --%>
<span id="regulator_mode_${paoId}_local_normal" style="display: none;">
    <img src="<cti:url value="/WebConfig/yukon/da/green_local.png"/>">
</span>

<%-- Regulator in local mode due to some failure ie: comms lost. --%>
<span id="regulator_mode_${paoId}_local_warning" style="display: none;">
    <cti:icon icon="icon-error"/>
</span>

<%-- Regulator is in Remote Mode and ready for control. --%>
<span id="regulator_mode_${paoId}_normal" style="display: none;">
    <cti:icon icon="icon-blank"/>
</span>

<cti:dataUpdaterCallback function="updateRegulatorModeIndicator('regulator_mode_${paoId}')" initialize="true" value="${type}/${paoId}/MODE"/>