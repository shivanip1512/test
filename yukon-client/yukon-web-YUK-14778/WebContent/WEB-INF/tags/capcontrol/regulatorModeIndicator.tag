<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>

<%@ attribute name="paoId" required="true" %>

<cti:msgScope paths="modules.capcontrol.ivvc">

<%-- LTC in local mode for a normal reason ie: operator set it to local mode. --%>
<span class="dn" data-pao-id="${paoId}" data-regulator-mode="local" title="<cti:msg2 key=".mode.local.tooltip"/>">
    <img src="<cti:url value="/WebConfig/yukon/Icons/green_local.png"/>">
</span>

<%-- Regulator in local mode due to some failure ie: comms lost. --%>
<span class="dn" data-pao-id="${paoId}" data-regulator-mode="warning" title="<cti:msg2 key=".mode.localWarning.tooltip"/>">
    <cti:icon icon="icon-error"/>
</span>

<%-- Regulator is in Remote Mode and ready for control. --%>
<span class="dn" data-pao-id="${paoId}" data-regulator-mode="normal">
    <cti:icon icon="icon-blank"/>
</span>

<cti:dataUpdaterCallback function="yukon.da.updaters.regulatorMode" initialize="true" value="VOLTAGE_REGULATOR/${paoId}/MODE"/>

</cti:msgScope>