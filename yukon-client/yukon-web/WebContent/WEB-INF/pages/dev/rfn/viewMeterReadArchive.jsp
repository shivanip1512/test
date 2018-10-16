<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti"%>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags"%>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="dt" tagdir="/WEB-INF/tags/dateTime"%>

<cti:standardPage module="dev" page="rfnTest">

<script type="text/javascript">
    function enableByCheckbox(valueElement, checkbox) {
        $("#" + valueElement).prop('disabled',
                $("#" + checkbox).prop('checked'));
    }
    $(document).ready(function() {
        $(".optional").prop("placeholder", "optional");
    });
</script>

    <tags:sectionContainer title="RFN Meter Read Archive Request Test">
        <form:form action="sendMeterReadArchiveRequest" method="post" modelAttribute="meterReading">
            <cti:csrfToken/>
            <tags:nameValueContainer tableClass="natural-width">
                
                <tags:nameValue name="Serial Number">
                    <form:input path="serialFrom" size="5" />
                    to 
                    <form:input path="serialTo" size="5" cssClass="optional"/>
                </tags:nameValue>
                
                <tags:nameValue name="Manufacturer and Model">
                    <form:select path="manufacturerModel">
                    <c:forEach var="group" items="${rfnTypeGroups}">
                        <optgroup label="${group.key}">
                        <c:forEach var="mm" items="${group.value}">
                            <form:option value="${mm}"><cti:msg2 key="${mm.type}" /> (${mm.manufacturer} ${mm.model})</form:option>
                        </c:forEach>
                        </optgroup>
                    </c:forEach>
                    </form:select>
                </tags:nameValue>

                <tags:nameValue name="Manufacturer override">
                    <form:input path="manufacturerOverride" size="7" cssClass="optional"/>
                </tags:nameValue>

                <tags:nameValue name="Model override">
                    <form:input path="modelOverride" size="7" cssClass="optional"/>
                </tags:nameValue>

                <tags:nameValue name="UoM">
                    <form:select path="uom">
                        <form:option value="Wh"/>
                        <form:option value="W"/>
                        <form:option value="gal"/>
                        <form:option value="-"/>
                        <form:option value="ft^3"/>
                        <form:option value="m^3"/>
                        <form:option value="Restore Blink Count"/>
                        <form:option value="Restore Count"/>
                        <form:option value="Outage Blink Count"/>
                        <form:option value="Outage Count"/>
                        <form:option value="V"/>
                        <form:option value="V degree"/>
                        <form:option value="A"/>
                        <form:option value="VAh"/>
                        <form:option value="Var"/>
                        <form:option value="Varh"/>
                        <form:option value="Pulse"/>
                        
                    </form:select>
                </tags:nameValue>
                
                <tags:nameValue name="Modifiers">
                    <table>
                        <tr>
                            <td>Quadrants:</td>
                            <td>
                                <div class="button-group">
                                    <tags:check label="1" path="modifiers" value="quad1"/> 
                                    <tags:check label="2" path="modifiers" value="quad2"/> 
                                    <tags:check label="3" path="modifiers" value="quad3"/> 
                                    <tags:check label="4" path="modifiers" value="quad4"/> 
                                </div>
                            </td>
                        </tr>
                        <tr>
                            <td>Min/max:</td>
                            <td>
                                <div class="button-group">
                                    <tags:check label="Max" path="modifiers" value="max"/>
                                    <tags:check label="Min" path="modifiers" value="min"/>
                                    <tags:check label="Average" path="modifiers" value="avg"/>
                                    <tags:check label="Daily Max" path="modifiers" value="dailyMax"/>
                                </div>
                            </td>
                        </tr>  
                        <tr>   
                            <td>Phases:</td>
                            <td>
                                <div class="button-group">
                                    <tags:check label="A" path="modifiers" value="phaseA"/> 
                                    <tags:check label="B" path="modifiers" value="phaseB"/> 
                                    <tags:check label="C" path="modifiers" value="phaseC"/> 
                                </div>
                            </td>
                        </tr>
                        <tr> 
                            <td>TOU Rates:</td>
                            <td>
                                <div class="button-group">
                                    <tags:check label="A" path="modifiers" value="touRateA"/>
                                    <tags:check label="B" path="modifiers" value="touRateB"/>
                                    <tags:check label="C" path="modifiers" value="touRateC"/>
                                    <tags:check label="D" path="modifiers" value="touRateD"/>
                                    <tags:check label="E" path="modifiers" value="touRateE"/>
                                </div>
                            </td>
                        </tr> 
                        <tr>  
                            <td>Other:</td>
                            <td>
                                <div class="button-group">
                                    <tags:check label="Net Flow" path="modifiers" value="netFlow"/>
                                    <tags:check label="Coincident" path="modifiers" value="coincident"/>
                                    <tags:check label="Harmonic" path="modifiers" value="harmonic"/>
                                    <tags:check label="Cumulative" path="modifiers" value="cumulative"/>
                                    <tags:check label="tenths" path="modifiers" value="tenths"/>
                                    <tags:check label="Phase Neutral to Ground" path="modifiers" value="neutralToGround"/>
                                </div>
                            </td>
                        </tr>
                    </table>
                </tags:nameValue>
                
                <tags:nameValue name="Type">
                    <form:select path="type">
                        <form:option value="INTERVAL">Interval</form:option>
                        <form:option value="BILLING">Billing</form:option>
                        <form:option value="CURRENT">Current (on demand)</form:option>
                        <form:option value="STATUS">Status</form:option>
                        <form:option value="PROFILE">Profile</form:option>
                    </form:select>
                </tags:nameValue>
                
                <tags:nameValue name="Reading Time">
                    <dt:dateTime id="timestamp" path="timestamp" disabled="${meterReading.now}" />
                    <tags:check id="now" path="now" label="Now" onclick="enableByCheckbox('timestamp', 'now')"/>
                </tags:nameValue>

                <tags:nameValue name="Value">
                    <form:input id="value" path="value" size="15" cssClass="fl" disabled="${meterReading.random}" />
                    <tags:check id="random" path="random" label="Random" onclick="enableByCheckbox('value', 'random')"/>
                </tags:nameValue>
                
            </tags:nameValueContainer>
            <div class="action-area"><cti:button nameKey="send" type="submit" classes="js-blocker"/></div>
        </form:form>
    </tags:sectionContainer>
</cti:standardPage>