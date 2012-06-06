<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti"%>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags"%>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n"%>

<cti:standardPage module="support" page="rfnTest">

<script type="text/javascript">
function useRandom() {
    var checked = $('random').checked;
    if (checked) {
        $('static').disable();
    } else {
        $('static').enable();
    }
}
</script>

    <tags:sectionContainer title="RFN Meter Read Archive Request Test">
        <form action="sendMeterArchiveRequest" method="post">
            <tags:nameValueContainer>
                
                <tags:nameValue name="Serial Number"><input name="serialFrom" type="text" value="1000"> to <input name="serialTo" type="text" value="1000"></tags:nameValue>
                
                <tags:nameValue name="Manufacturer">
                    <select name="manufacturer">
                        <option value="LGYR">LGYR</option>
                        <option value="Eka">Eka</option>
                        <option value="EE">EE</option>
                        <option value="GE">GE</option>
                        <option value="ELO">ELO</option>
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
                    <input type="checkbox" id="quad1" name="quad1" checked="checked"> <label for="quad1">Quadrant 1</label><br>
                    <input type="checkbox" id="quad2" name="quad2"> <label for="quad2">Quadrant 2</label><br>
                    <input type="checkbox" id="quad3" name="quad3"> <label for="quad3">Quadrant 3</label><br>
                    <input type="checkbox" id="quad4" name="quad4" checked="checked"> <label for="quad4">Quadrant 4</label><br>
                </tags:nameValue>
                
                <tags:nameValue name="Value">
                    <input id="static" name="value" type="text" value="34543.4">  <input type="checkbox" id="random" name="random" onclick="useRandom()">  <label for="random">Random</label>
                </tags:nameValue>
                
            </tags:nameValueContainer>
            <div clas="actionArea"><cti:button nameKey="send" type="submit" styleClass="f_blocker"/></div>
        </form>
    </tags:sectionContainer>

</cti:standardPage>