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

        $(".button-group button")
           .has("input[type=checkbox]:checked")
           .addClass("on");

       $(".button-group button").on("click", function(){
             var checkbox = $(this).find("input[type=checkbox]");
             if (checkbox) {
                 $(this).toggleClass("on");
                 checkbox.prop("checked", !checkbox.prop("checked"));
                 return false;
             }
         });
    });
</script>

    <tags:sectionContainer title="RFN Meter Read Archive Request Test">
        <form:form action="sendMeterReadArchiveRequest" method="post" commandName="meterReading">
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
                                    <button>1<form:checkbox path="modifiers[quad1]" cssClass="dn"/></button> 
                                    <button>2<form:checkbox path="modifiers[quad2]" cssClass="dn"/></button>
                                    <button>3<form:checkbox path="modifiers[quad3]" cssClass="dn"/></button>
                                    <button>4<form:checkbox path="modifiers[quad4]" cssClass="dn"/></button>
                                </div>
                            </td>
                        </tr>
                        <tr>
                            <td>Min/max:</td>
                            <td>
                                <div class="button-group">
                                    <button>Max<form:checkbox path="modifiers[max]" cssClass="dn"/></button>
                                    <button>Min<form:checkbox path="modifiers[min]" cssClass="dn"/></button>
                                    <button>Average<form:checkbox path="modifiers[avg]" cssClass="dn"/></button>
                                    <button>Daily Max<form:checkbox path="modifiers[dailyMax]" cssClass="dn"/></button>
                                </div>
                            </td>
                        </tr>  
                        <tr>   
                            <td>Phases:</td>
                            <td>
                                <div class="button-group">
                                    <button>A<form:checkbox path="modifiers[phaseA]" cssClass="dn"/></button> 
                                    <button>B<form:checkbox path="modifiers[phaseB]" cssClass="dn"/></button> 
                                    <button>C<form:checkbox path="modifiers[phaseC]" cssClass="dn"/></button> 
                                </div>
                            </td>
                        </tr>
                        <tr> 
                            <td>TOU Rates:</td>
                            <td>
                                <div class="button-group">
                                    <button>A<form:checkbox path="modifiers[touRateA]" cssClass="dn"/></button>
                                    <button>B<form:checkbox path="modifiers[touRateB]" cssClass="dn"/></button>
                                    <button>C<form:checkbox path="modifiers[touRateC]" cssClass="dn"/></button>
                                    <button>D<form:checkbox path="modifiers[touRateD]" cssClass="dn"/></button>
                                    <button>E<form:checkbox path="modifiers[touRateE]" cssClass="dn"/></button>
                                </div>
                            </td>
                        </tr> 
                        <tr>  
                            <td>Other:</td>
                            <td>
                                <div class="button-group">
                                    <button>Net Flow<form:checkbox path="modifiers[netFlow]" cssClass="dn"/></button>
                                    <button>Coincident<form:checkbox path="modifiers[coincident]" cssClass="dn"/></button>
                                    <button>Harmonic<form:checkbox path="modifiers[harmonic]" cssClass="dn"/></button>
                                    <button>Cumulative<form:checkbox path="modifiers[cumulative]" cssClass="dn"/></button>
                                    <button>tenths<form:checkbox path="modifiers[tenths]" cssClass="dn"/></button>
                                    <button>Phase Neutral->Ground<form:checkbox path="modifiers[neutralToGround]" cssClass="dn"/></button>
                                </div>
                            </td>
                        </tr>
                    </table>
                </tags:nameValue>
                
                <tags:nameValue name="Value">
                    <form:input id="value" path="value" disabled="${meterReading.random}" />
                    <label><form:checkbox id="random" path="random" onclick="enableByCheckbox('value', 'random')" />Random</label>
                </tags:nameValue>
                
                <tags:nameValue name="Reading Time">
                    <dt:dateTime id="timestamp" path="timestamp" disabled="${meterReading.now}" />
                    <label><form:checkbox id="now" path="now" onclick="enableByCheckbox('timestamp', 'now')" />Now</label>
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
                
            </tags:nameValueContainer>
            <div class="action-area"><cti:button nameKey="send" type="submit" classes="js-blocker"/></div>
        </form:form>
    </tags:sectionContainer>
</cti:standardPage>