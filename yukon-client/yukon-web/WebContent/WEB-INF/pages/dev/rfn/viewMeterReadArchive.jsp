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
                
                <tags:nameValue name="Manufacturer and Model">
                    <select name="manufacturerModel">
                        <optgroup label="Itron single phase">
                            <option value="RFN_410CL">RFN-410CL (ITRN C1SX)</option>
                            <option value="RFN_420CL">RFN-420CL (ITRN C2SX)</option>
                            <option value="RFN_420CD">RFN-420CD (ITRN C2SX-SD)</option>
                        </optgroup>

                        <optgroup label="Landis & Gys single phase">
                            <option value="RFN_410FL">RFN-410FL (LGYR FocuskWh)</option>
                            <option value="RFN_410FX_D">RFN-410FX (LGYR FocusAXD)</option>
                            <option value="RFN_410FX_R">RFN-410FX (LGYR FocusAXR)</option>
                            <option value="RFN_410FD_D">RFN-410FD (LGYR FocusAXR-SD)</option>
                            <option value="RFN_410FD_R">RFN-410FD (LGYR FocusAXD-SD)</option>
                            <option value="RFN_420FL">RFN-420FL (LGYR FocuskWh)</option>
                            <option value="RFN_420FX">RFN-420FX (LGYR FocusAXD)</option>
                            <option value="RFN_420FD">RFN-420FD (LGYR FocusAXR-SD)</option>
                            <option value="RFN_420FRX">RFN-420FRX (LGYR FocusRXR)</option>
                            <option value="RFN_420FRD">RFN-420FRD (LGYR FocusRXR-SD)</option>
                            <option value="RFN_510FL">RFN-510FL (LGYR FocuskWh-500)</option>
                            <option value="RFN_520FAXD">RFN-520FAX (LGYR FocusAXD-500)</option>
                            <option value="RFN_520FAXT">RFN-520FAX (LGYR FocusAXT-500)</option>
                            <option value="RFN_520FAXR">RFN-520FAX (LGYR FocusAXR-500)</option>
                            <option value="RFN_520FRXD">RFN-520FRX (LGYR FocusRXD-500)</option>
                            <option value="RFN_520FRXT">RFN-520FRX (LGYR FocusRXT-500)</option>
                            <option value="RFN_520FRXR">RFN-520FRX (LGYR FocusRXR-500)</option>
                            <option value="RFN_520FAXD_SD">RFN-520FAXD (LGYR FocusAXD-SD-500)</option>
                            <option value="RFN_520FAXT_SD">RFN-520FAXD (LGYR FocusAXT-SD-500)</option>
                            <option value="RFN_520FAXR_SD">RFN-520FAXD (LGYR FocusAXR-SD-500)</option>
                            <option value="RFN_520FRXD_SD">RFN-520FRXD (LGYR FocusRXD-SD-500)</option>
                            <option value="RFN_520FRXT_SD">RFN-520FRXD (LGYR FocusRXT-SD-500)</option>
                            <option value="RFN_520FRXR_SD">RFN-520FRXD (LGYR FocusRXR-SD-500)</option>
                        </optgroup>

                        <optgroup label="Landis & Gyr polyphase">
                            <option value="RFN_530FAX">RFN-530FAX (LGYR FocusAXT-530)</option>
                            <option value="RFN_530FRX">RFN-530FRX (LGYR FocusAXR-530)</option>
                            <option value="RFN_530S4X">RFN-530S4X (LGYR E650)</option>
                            <option value="RFN_530S4AD">RFN-530S4EAX (LGYR S4-AD)</option>
                            <option value="RFN_530S4AT">RFN-530S4EAXR (LGYR S4-AT)</option>
                            <option value="RFN_530S4AR">RFN-530S4EAXR (LGYR S4-AR)</option>
                            <option value="RFN_530S4RD">RFN-530S4ERX (LGYR S4-RD)</option>
                            <option value="RFN_530S4RT">RFN-530S4ERXR (LGYR S4-RT)</option>
                            <option value="RFN_530S4RR">RFN-530S4ERXR (LGYR S4-RR)</option>
                        </optgroup>

                        <optgroup label="General Electric polyphase">
                            <option value="RFN_430KV">RFN-430KV (GE kV2)</option>
                        </optgroup>

                        <optgroup label="Schlumberger polyphase">
                            <option value="RFN_430SL0">RFN-430SL0 (SCH SENTINEL-L0)</option>
                            <option value="RFN_430SL1">RFN-430SL1 (SCH SENTINEL-L1)</option>
                            <option value="RFN_430SL2">RFN-430SL2 (SCH SENTINEL-L2)</option>
                            <option value="RFN_430SL3">RFN-430SL3 (SCH SENTINEL-L3)</option>
                            <option value="RFN_430SL4">RFN-430SL4 (SCH SENTINEL-L4)</option>
                        </optgroup>

                        <optgroup label="Eaton Water 2">
                            <option value="RFW201_PULSE">RFW-201 Pulse (WTR2 Pulse-201)</option>
                            <option value="RFW201_ENCODER">RFW-201 Encoder (WTR2 Encoder-201)</option>
                            <option value="RFW205_PULSE">RFW-205 Pulse (WTR2 Pulse-205)</option>
                            <option value="RFW205_ENCODER">RFW-205 Encoder (WTR2 Encoder-205)</option>
                        </optgroup>

                        <optgroup label="Legacy Water">
                            <option value="RFN_WATER_SENSOR">RFN water sensor (Eka water_sensor)</option>
                        </optgroup>

                        <optgroup label="ELO">
                            <option value="RFN_440_2131TD">RFN-440_2131TD (ELO 0131)</option>
                            <option value="RFN_440_2132TD">RFN-440_2132TD (ELO 0132)</option>
                            <option value="RFN_440_2133TD">RFN-440_2133TD (ELO 0133)</option>
                        </optgroup>
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
                        <option value="VAh">VAh</option>
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
                            <td><form:checkbox path="modifiers[neutralToGround]" label="Phase Neutral->Ground"/></td>
                            <td><form:checkbox path="modifiers[dailyMax]" label="Daily Max"/></td>
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