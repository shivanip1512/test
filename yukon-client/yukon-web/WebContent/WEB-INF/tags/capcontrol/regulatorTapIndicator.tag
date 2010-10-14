<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti"%>

<%@ attribute name="paoId" required="true" %>
<%@ attribute name="type" required="true" %>

<input type="hidden" id="regulator_tap_${paoId}" value='<cti:capControlValue paoId="${paoId}" type="${type}" format="TAP"/>'>

<%-- Regulator in local mode for a normal reason ie: operator set it to local mode. --%>
<span id="regulator_tap_${paoId}_lower" style="display: none;">
    <img src="/capcontrol/images/arrow_down_green.gif" class="tierImg"  alt=""
      	 onmouseover="statusMsgAbove(this,'Lower Tap command was recently sent.');">
</span>

<%-- Regulator in local mode due to some failure ie: comms lost. --%>
<span id="regulator_tap_${paoId}_raise" style="display: none;">
    <img src="/capcontrol/images/arrow_up_green.gif" class="tierImg"  alt=""
    	 onmouseover="statusMsgAbove(this,'Raise Tap command was recently sent.');">
</span>

<cti:dataUpdaterCallback function="updateRegulatorTapIndicator('regulator_tap_${paoId}')" initialize="true" value="${type}/${paoId}/TAP"/>