<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti"%>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags"%>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="dt" tagdir="/WEB-INF/tags/dateTime"%>

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
        <form:form action="sendMeterArchiveRequest" method="post" commandName="meterReading">
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
                            <td><form:checkbox path="modifiers[quad1]" label="Quadrant 1"/></td>
                            <td><form:checkbox path="modifiers[quad2]" label="Quadrant 2"/></td>
                            <td><form:checkbox path="modifiers[quad3]" label="Quadrant 3"/></td>
                            <td><form:checkbox path="modifiers[quad4]" label="Quadrant 4"/></td>
                        </tr>
                        <tr>
                            <td><form:checkbox path="modifiers[max]" label="Max"/></td>
                            <td><form:checkbox path="modifiers[min]" label="Min"/></td>
                            <td><form:checkbox path="modifiers[avg]" label="Average"/></td>
                        </tr>  
                        <tr>   
                            <td><form:checkbox path="modifiers[phaseA]" label="Phase A"/></td>
                            <td><form:checkbox path="modifiers[phaseB]" label="Phase B"/></td>
                            <td><form:checkbox path="modifiers[phaseC]" label="Phase C"/></td>
                        </tr>
                        <tr> 
                            <td><form:checkbox path="modifiers[touRateA]" label="TOU Rate A"/></td>
                            <td><form:checkbox path="modifiers[touRateB]" label="TOU Rate B"/></td>
                            <td><form:checkbox path="modifiers[touRateC]" label="TOU Rate C"/></td>
                            <td><form:checkbox path="modifiers[touRateD]" label="TOU Rate D"/></td>
                            <td><form:checkbox path="modifiers[touRateE]" label="TOU Rate E"/></td>
                        </tr> 
                        <tr>  
                            <td><form:checkbox path="modifiers[netFlow]"    label="Net Flow"/></td>
                            <td><form:checkbox path="modifiers[coincident]" label="Coincident"/></td>
                            <td><form:checkbox path="modifiers[harmonic]"   label="Harmonic"/></td>
                            <td><form:checkbox path="modifiers[cumulative]" label="Cumulative"/></td>
                        </tr>  
                        <tr>   
                            <td><form:checkbox path="modifiers[tenths]" label="tenths"/></td>
                        </tr>
                    </table>
                </tags:nameValue>
                
                <tags:nameValue name="Value">
                    <input id="static" name="value" type="text" value="34543.4">
                    <label><input type="checkbox" id="random" name="random" onclick="useRandom()">Random</label>
                </tags:nameValue>
                
                <tags:nameValue name="Reading Time">
                    <dt:dateTime id="timestamp" path="timestamp" value="${reading.timestamp}"/>
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
        </form:form>
    </tags:sectionContainer>
</cti:standardPage>