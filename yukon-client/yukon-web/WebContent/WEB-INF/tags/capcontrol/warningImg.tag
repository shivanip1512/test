<%@ attribute name="paoId" required="true" %>
<%@ attribute name="type" required="true" %>

<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti"%>

<input type="hidden" id="warning_${paoId}" value='<cti:capControlValue paoId="${paoId}" type="${type}" format="WARNING_FLAG"/>'>
                    
<span id="warning_${paoId}_ok" style="display: none;">
    <img src="/capcontrol/images/green.gif" class="tierImg"  alt="">
</span>

<span id="warning_${paoId}_alert" style="display: none;" 
      onmouseover="showDynamicPopup($('warningPopup_${paoId}'));" 
      onmouseout="nd();">
    <img src="/capcontrol/images/yellow.gif" class="tierImg"  alt="">
</span>

<cti:dataUpdaterCallback function="updateWarningImage('warning_${paoId}')" initialize="true" value="${type}/${paoId}/WARNING_FLAG"/>            
    
<div class="ccVarLoadPopup" id="warningPopup_${paoId}" style="display: none;">
    <cti:capControlValue paoId="${paoId}" type="${type}" format="WARNING_FLAG_MESSAGE"/> 
</div>