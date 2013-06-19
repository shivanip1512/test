<%@ attribute name="paoId" required="true" %>
<%@ attribute name="type" required="true" %>

<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti"%>

<input type="hidden" id="warning_${paoId}" value='<cti:capControlValue paoId="${paoId}" type="${type}" format="WARNING_FLAG"/>'>
                    
<span id="warning_${paoId}_ok" style="display: none;">
    <img src="/WebConfig/yukon/da/green.png">
</span>

<span id="warning_${paoId}_alert" style="display: none;" class="f-has-tooltip">
    <img src="/WebConfig/yukon/da/yellow.png">
</span>

<cti:dataUpdaterCallback function="updateWarningImage('warning_${paoId}')" initialize="true" value="${type}/${paoId}/WARNING_FLAG"/>            
    
<span class="f-tooltip" id="warningPopup_${paoId}" style="display: none;">
    <cti:capControlValue paoId="${paoId}" type="${type}" format="WARNING_FLAG_MESSAGE"/> 
</span>