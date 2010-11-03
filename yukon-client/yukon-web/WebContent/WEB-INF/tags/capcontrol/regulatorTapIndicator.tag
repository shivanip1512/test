<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti"%>

<%@ attribute name="paoId" required="true" %>
<%@ attribute name="type" required="true" %>

<cti:msgScope paths="modules.capcontrol">

<input type="hidden" id="regulator_tap_${paoId}" value='<cti:capControlValue paoId="${paoId}" type="${type}" format="TAP"/>'>

<%-- Last Operation was a Tap Down Operation --%>
<span id="regulator_tap_${paoId}_lower" style="display: none;">
    <cti:img key="greenDownArrow"/>
</span>

<%-- Last Operation was a Tap Up Operation --%>
<span id="regulator_tap_${paoId}_raise" style="display: none;">
    <cti:img key="greenUpArrow"/>
</span>

<%-- Last Operation was a Tap Down Operation and occurred recently. Animated Icon --%>
<span id="regulator_tap_${paoId}_lower_recent" style="display: none;">
    <cti:img key="flashingGreenDownArrow"/>
</span>

<%-- Last Operation was a Tap Up Operation and occurred recently. Animated Icon --%>
<span id="regulator_tap_${paoId}_raise_recent" style="display: none;">
    <cti:img key="flashingGreenUpArrow"/>
</span>

<%-- Last Operation Timestamp --%>
<span id="regulator_tap_${paoId}_time" style="display: none;">
    <cti:capControlValue paoId="${paoId}" type="${type}" format="OPTIME"/>
</span>

<%-- Default output --%>
<span id="regulator_tap_${paoId}_default" style="display: none;">
    <cti:msg2 key=".noPreviousOperation"/>
</span>

<cti:dataUpdaterCallback function="updateRegulatorTapIndicator('regulator_tap_${paoId}')" initialize="true" value="${type}/${paoId}/TAP"/>

</cti:msgScope>