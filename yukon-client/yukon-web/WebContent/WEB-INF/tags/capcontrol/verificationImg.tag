<%@ attribute name="paoId" required="true" %>
<%@ attribute name="type" required="true" %>

<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti"%>

<input type="hidden" id="verification_${paoId}" value='<cti:capControlValue paoId="${paoId}" type="${type}" format="VERIFICATION_FLAG"/>'>

<span data-verification data-pao-id="${paoId}" style="display: none;">
    <cti:icon nameKey="verificationInProgress" icon="icon-flag-red" classes="fn"/>
</span>

<cti:dataUpdaterCallback function="updateVerificationImage('${paoId}')" initialize="true" value="${type}/${paoId}/VERIFICATION_FLAG"/>            
