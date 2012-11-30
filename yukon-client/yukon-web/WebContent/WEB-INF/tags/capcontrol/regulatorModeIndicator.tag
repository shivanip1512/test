<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti"%>

<%@ attribute name="paoId" required="true" %>
<%@ attribute name="type" required="true" %>

<input type="hidden" id="regulator_mode_${paoId}" value='<cti:capControlValue paoId="${paoId}" type="${type}" format="MODE"/>'>

<%-- LTC in local mode for a normal reason ie: operator set it to local mode. --%>
<span id="regulator_mode_${paoId}_local_normal" style="display: none;">
    <img src="/WebConfig/yukon/da/green_local.png" class="tierImg"  alt="">
</span>

<%-- Regulator in local mode due to some failure ie: comms lost. --%>
<span id="regulator_mode_${paoId}_local_warning" style="display: none;">
    <img src="/WebConfig/yukon/da/yellow_local.png" class="tierImg"  alt="">
</span>

<%-- Regulator is in Remote Mode and ready for control. --%>
<span id="regulator_mode_${paoId}_normal" style="display: none;">
    <img src="/WebConfig/yukon/da/green.png" class="tierImg"  alt="">
</span>

<cti:dataUpdaterCallback function="updateRegulatorModeIndicator('regulator_mode_${paoId}')" initialize="true" value="${type}/${paoId}/MODE"/>