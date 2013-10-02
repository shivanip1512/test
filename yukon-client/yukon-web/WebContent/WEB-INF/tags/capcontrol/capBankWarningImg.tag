<%@ attribute name="paoId" required="true" %>
<%@ attribute name="type" required="true" %>

<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>

<input type="hidden" id="warning_${paoId}" value='<cti:capControlValue paoId="${paoId}" type="${type}" format="WARNING_FLAG"/>'>

<span id="warning_${paoId}_green">
    <cti:icon icon="icon-blank"/>
</span>

<span class="f-tooltip" id="warningPopup_${paoId}" style="display: none;">
    <cti:capControlValue paoId="${paoId}" type="${type}" format="WARNING_FLAG_MESSAGE"/> 
</span>
<span id="warning_${paoId}_yellow" style="display: none;" class="f-has-tooltip">
    <cti:icon icon="icon-error"/>
</span>

<cti:dataUpdaterCallback function="updateCapBankWarningImage('warning_${paoId}')" initialize="true" value="${type}/${paoId}/WARNING_FLAG"/>