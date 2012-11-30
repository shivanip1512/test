<%@ attribute name="paoId" required="true" %>
<%@ attribute name="type" required="true" %>

<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti"%>

<input type="hidden" id="warning_${paoId}" value='<cti:capControlValue paoId="${paoId}" type="${type}" format="WARNING_FLAG"/>'>
                    
<span id="warning_${paoId}_green" style="display: none;">
    <img src="/WebConfig/yukon/da/green.png" class="tierImg" alt="">
</span>

<span id="warning_${paoId}_yellow" style="display: none;" 
      onmouseover="showDynamicPopupAbove('warningPopup_${paoId}');" 
      onmouseout="nd();">
    <img src="/WebConfig/yukon/da/yellow.png" class="tierImg" alt="">
</span>

<span id="warning_${paoId}_green_local" style="display: none;">
    <img src="/WebConfig/yukon/da/green_local.png" class="tierImg" alt="">
</span>

<span id="warning_${paoId}_yellow_local" style="display: none;" 
      onmouseover="showDynamicPopupAbove('warningPopup_${paoId}');" 
      onmouseout="nd();">
    <img src="/WebConfig/yukon/da/yellow_local.png" class="tierImg" alt="">
</span>

<cti:dataUpdaterCallback function="updateCapBankWarningImage('warning_${paoId}')" initialize="true" value="${type}/${paoId}/WARNING_FLAG"/>            
    
<span class="ccVarLoadPopup" id="warningPopup_${paoId}" style="display: none;">
    <cti:capControlValue paoId="${paoId}" type="${type}" format="WARNING_FLAG_MESSAGE"/> 
</span>