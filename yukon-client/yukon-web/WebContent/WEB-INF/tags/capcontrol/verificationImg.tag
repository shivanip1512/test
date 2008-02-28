<%@ attribute name="paoId" required="true" %>
<%@ attribute name="type" required="true" %>

<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti"%>

<input type="hidden" id="verification_${paoId}" value='<cti:capControlValue paoId="${paoId}" type="${type}" format="VERIFICATION_FLAG"/>'></input>
                    
<span id="verification_span_${paoId}" style="display: none;">
    <span class="popupImg" onmouseover="statusMsg(this, 'Currently being used in a Verification schedule');">
        <img src="/capcontrol/images/flag_red.gif"/>
    </span>
</span>                    
                    
<cti:dataUpdaterCallback function="updateVerificationImage('verification_span_${paoId}')" initialize="true" value="${type}/${paoId}/VERIFICATION_FLAG"/>            
