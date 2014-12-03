<%@ attribute name="paoId" required="true" %>
<%@ attribute name="type" required="true" %>

<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>

<span id="bank-warning-msg-${paoId}" class="dn">
    <cti:capControlValue paoId="${paoId}" type="${type}" format="WARNING_FLAG_MESSAGE"/> 
</span>
<span class="dn js-warning-image" data-pao-id="${paoId}" data-tooltip="#bank-warning-msg-${paoId}">
    <cti:icon icon="icon-error"/>
</span>

<cti:dataUpdaterCallback function="yukon.da.updaters.warningIcon" initialize="true" value="${type}/${paoId}/WARNING_FLAG"/>