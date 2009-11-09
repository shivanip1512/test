<%@ attribute name="paoId" required="true" %>
<%@ attribute name="type" required="true" %>

<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti"%>

<input type="hidden" id="dualBus_${paoId}" value='<cti:capControlValue paoId="${paoId}" type="${type}" format="DUALBUS"/>'></input>
                    
<span id="dualBus_${paoId}_primary" style="display: none;"
      onmouseover="showDynamicPopup($('dualBusPrimaryPopup_${paoId}'));" 
      onmouseout="nd();">
    <img src="/WebConfig/yukon/Icons/bullet_orange.gif" style="vertical-align: middle;"/>
</span>

<span id="dualBus_${paoId}_alternate" style="display: none;" 
      onmouseover="showDynamicPopup($('dualBusAlternatePopup_${paoId}'));" 
      onmouseout="nd();">
    <img src="/WebConfig/yukon/Icons/bullet_orange.gif" style="vertical-align: middle;"/>
</span>

<cti:dataUpdaterCallback function="updateDualBusImage('dualBus_${paoId}')" initialize="true" value="${type}/${paoId}/DUALBUS"/>            
    
<div class="ccVarLoadPopup" id="dualBusPrimaryPopup_${paoId}" style="display: none;">
    <cti:capControlValue paoId="${paoId}" type="${type}" format="DUALBUS_MESSAGE"/> 
</div>

<div class="ccVarLoadPopup" id="dualBusAlternatePopup_${paoId}" style="display: none;">
    <cti:capControlValue paoId="${paoId}" type="${type}" format="DUALBUS_MESSAGE"/> 
</div>