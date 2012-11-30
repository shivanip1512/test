<%@ attribute name="paoId" required="true" %>
<%@ attribute name="type" required="true" %>

<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti"%>

<input type="hidden" id="verification_${paoId}" value='<cti:capControlValue paoId="${paoId}" type="${type}" format="VERIFICATION_FLAG"/>'>
                    
<span id="verification_span_${paoId}" style="display: none;">
    <span class="helpImg" onmouseover="statusMsgAbove(this, 'Currently being used in a Verification schedule');">
        <img src="/WebConfig/yukon/da/flag_red.gif" class="tierImg" alt="">
    </span>
</span>                    
                    
<cti:dataUpdaterCallback function="updateVerificationImage('verification_span_${paoId}')" initialize="true" value="${type}/${paoId}/VERIFICATION_FLAG"/>            
