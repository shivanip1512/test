<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti"%>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags"%>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n"%>

<cti:standardPage module="dev" page="rfnTest">

<script type="text/javascript">
function useRandom() {
    var checked = $('#random').prop('checked');
    if (checked) {
        $('#static').prop('disabled', true);
    } else {
        $('#static').prop('disabled', false);
    }
}
</script>

    <tags:sectionContainer title="RFN Meter Read Archive Request Test">
        <form action="sendMeterArchiveRequest" method="post">
            <cti:csrfToken/>
            <tags:nameValueContainer>
                
                <tags:nameValue name="Serial Number">
                    <input name="serialFrom" type="text" value="1000"> 
                    to 
                    <input name="serialTo" type="text" value="1000">
                </tags:nameValue>
                
                <tags:nameValue name="Manufacturer">
                    <select name="manufacturer">
                        <option value="LGYR">LGYR</option>
                        <option value="Eka">Eka</option>
                        <option value="EE">EE</option>
                        <option value="GE">GE</option>
                        <option value="ELO">ELO</option>
                        <option value="ITRN">ITRN</option>
                    </select>
                </tags:nameValue>
                
                <tags:nameValue name="Model">
                    <select name="model">
                        <option value="FocuskWh">FocuskWh</option>
                        <option value="water_sensor">water_sensor</option>
                        <option value="water_sensor">water_node</option>
                        <option value="A3R">A3R</option>
                        <option value="Centron">Centron</option>
                        <option value="kV2">kV2</option>
                        <option value="FocusAXD">FocusAXD</option>
                        <option value="FocusAXR">FocusAXR</option>
                        <option value="2131T">2131T</option>
                        <option value="2131xT">2131xT</option>
                        <option value="C2SX">C2SX</option>
                    </select>
                </tags:nameValue>
                
                <tags:nameValue name="UoM">
                    <select name="uom">
                        <option value="Wh">Wh</option>
                        <option value="W">W</option>
                        <option value="gal">gal</option>
                        <option value="-">-</option>
                        <option value="ft^3">ft^3</option>
                        <option value="m^3">m^3</option>
                        <option value="Restore Blink Count">Restore Blink Count</option>
                        <option value="Restore Count">Restore Count</option>
                        <option value="Outage Blink Count">Outage Blink Count</option>
                        <option value="Outage Count">Outage Count</option>
                        <option value="V">V</option>
                        <option value="V degree">V degree</option>
                        <option value="A">A</option>
                        <option value="Var">Var</option>
                        <option value="Varh">Varh</option>
                        <option value="Pulse">Pulse</option>
                    </select>
                </tags:nameValue>
                
                <tags:nameValue name="Modifiers">
                    <table cellspacing="5">
                        <tr>
                            <td><label><input type="checkbox" name="quad1" checked="checked">Quadrant 1</label></td>
                            <td><label><input type="checkbox" name="quad2">Quadrant 2</label></td>
                            <td><label><input type="checkbox" name="quad3">Quadrant 3</label></td>
                            <td><label><input type="checkbox" name="quad4" checked="checked">Quadrant 4</label></td>
                        </tr>
                        <tr>
                            <td><label><input type="checkbox" id="max" name="max">Max</label></td>
                            <td><label><input type="checkbox" id="min" name="min">Min</label></td>
                            <td><label><input type="checkbox" id="avg" name="avg">Average</label></td>
                        </tr>
                        <tr>
                            <td><label><input type="checkbox" id="phaseA" name="phaseA">Phase A</label></td>
                            <td><label><input type="checkbox" id="phaseB" name="phaseB">Phase B</label></td>
                            <td><label><input type="checkbox" id="phaseC" name="phaseC">Phase C</label></td>
                        </tr>
                        <tr>
                            <td><label><input type="checkbox" id="touRateA" name="touRateA">TOU Rate A</label></td>
                            <td><label><input type="checkbox" id="touRateB" name="touRateB">TOU Rate B</label></td>
                            <td><label><input type="checkbox" id="touRateC" name="touRateC">TOU Rate C</label></td>
                            <td><label><input type="checkbox" id="touRateD" name="touRateD">TOU Rate D</label></td>
                            <td><label><input type="checkbox" id="touRateE" name="touRateE">TOU Rate E</label></td>
                        </tr>
                        <tr>
                            <td><label><input type="checkbox" id="netFlow"    name="netFlow">Net Flow</label></td>
                            <td><label><input type="checkbox" id="coincident" name="coincident">Coincident</label></td>
                            <td><label><input type="checkbox" id="harmonic"   name="harmonic">Harmonic</label></td>
                            <td><label><input type="checkbox" id="cumulative" name="cumulative">Cumulative</label></td>
                        </tr>
                        <tr>
                            <td><label><input type="checkbox" id="tenths" name="tenths">tenths</label></td>
                        </tr>
                    </table>
                </tags:nameValue>
                
                <tags:nameValue name="Value">
                    <input id="static" name="value" type="text" value="34543.4">
                    <label><input type="checkbox" id="random" name="random" onclick="useRandom()">Random</label>
                </tags:nameValue>
                
                <tags:nameValue name="Type">
                    <select name="type">
                        <option value="INTERVAL">Interval</option>
                        <option value="BILLING">Billing</option>
                        <option value="CURRENT">Current (on demand)</option>
                        <option value="STATUS">Status</option>
                        <option value="PROFILE">Profile</option>
                    </select>
                </tags:nameValue>
                
            </tags:nameValueContainer>
            <div class="action-area"><cti:button nameKey="send" type="submit" classes="js-blocker"/></div>
        </form>
    </tags:sectionContainer>
</cti:standardPage>