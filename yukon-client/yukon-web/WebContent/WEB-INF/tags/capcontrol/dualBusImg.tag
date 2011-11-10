<%@ attribute name="paoId" required="true" %>
<%@ attribute name="type" required="true" %>

<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti"%>

<input type="hidden" id="dualBus_${paoId}" value='<cti:capControlValue paoId="${paoId}" type="${type}" format="DUALBUS"/>'>
                    
<span id="dualBus_${paoId}_primary" style="display: none;"
      onmouseover="showDynamicPopupAbove('dualBusPrimaryPopup_${paoId}');" 
      onmouseout="nd();">
    <img src="/WebConfig/yukon/Icons/bullet_orange.gif" class="tierImg" alt="">
</span>

<span id="dualBus_${paoId}_alternate" style="display: none;" 
      onmouseover="showDynamicPopupAbove('dualBusAlternatePopup_${paoId}');" 
      onmouseout="nd();">
    <img src="/WebConfig/yukon/Icons/bullet_orange.gif" class="tierImg" alt="">
</span>

<cti:dataUpdaterCallback function="updateDualBusImage('dualBus_${paoId}')" initialize="true" value="${type}/${paoId}/DUALBUS"/>            
    
<span class="ccVarLoadPopup" id="dualBusPrimaryPopup_${paoId}" style="display: none;">
    <cti:capControlValue paoId="${paoId}" type="${type}" format="DUALBUS_MESSAGE"/> 
</span>

<span class="ccVarLoadPopup" id="dualBusAlternatePopup_${paoId}" style="display: none;">
    <cti:capControlValue paoId="${paoId}" type="${type}" format="DUALBUS_MESSAGE"/> 
</span>