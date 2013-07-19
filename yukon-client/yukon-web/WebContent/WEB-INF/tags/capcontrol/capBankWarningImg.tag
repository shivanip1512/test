<%@ attribute name="paoId" required="true" %>
<%@ attribute name="type" required="true" %>

<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti"%>

<input type="hidden" id="warning_${paoId}" value='<cti:capControlValue paoId="${paoId}" type="${type}" format="WARNING_FLAG"/>'>
                    
<span id="warning_${paoId}_green" style="display: none;">
    <img src="/WebConfig/yukon/da/green.png">
</span>

<span class="f-tooltip" id="warningPopup_${paoId}" style="display: none;">
    <cti:capControlValue paoId="${paoId}" type="${type}" format="WARNING_FLAG_MESSAGE"/> 
</span>
<span id="warning_${paoId}_yellow" style="display: none;" class="f-has-tooltip">
    <img src="/WebConfig/yukon/da/yellow.png">
</span>

<span id="warning_${paoId}_green_local" style="display: none;">
    <img src="/WebConfig/yukon/da/green_local.png">
</span>

<span class="f-tooltip" id="warningPopup_${paoId}" style="display: none;">
    <cti:capControlValue paoId="${paoId}" type="${type}" format="WARNING_FLAG_MESSAGE"/> 
</span>
<span id="warning_${paoId}_yellow_local" style="display: none;" class="f-has-tooltip">
    <img src="/WebConfig/yukon/da/yellow_local.png">
</span>

<cti:dataUpdaterCallback function="updateCapBankWarningImage('warning_${paoId}')" initialize="true" value="${type}/${paoId}/WARNING_FLAG"/>