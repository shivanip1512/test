<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<cti:msgScope paths="yukon.web.modules.dr.setup.gear">
    <div class="column-12-12 clearfix js-simple-thermostat-ramping-gear">
        <div class="column one">
            <tags:sectionContainer2 nameKey="controlParameters">
                <tags:nameValueContainer2>
                    <tags:nameValue2 nameKey=".mode">
                        <tags:radioButtonGroup items="${temperatureModes}" path="fields.mode" viewModeKey="${programGear.fields.mode}"/>
                    </tags:nameValue2>
                    <tags:nameValue2 nameKey=".randomStartTimeInMinutes" nameClass="vam">
                        <cti:msg var="randomStartTimeInMinutesRange" key="yukon.web.modules.dr.setup.gear.range" arguments="0:00,2:00"/>
                        <dt:timeOffset path="fields.randomStartTimeInMinutes" wrapClass="fn vam" minValue="0" maxValue="120" rangeText="${randomStartTimeInMinutesRange}"/>
                    </tags:nameValue2>
                </tags:nameValueContainer2>
                
                <br/>
                <h3><i:inline key=".preOpCoolOrHeat"/></h3>
                
                <tags:nameValueContainer2>
                    <tags:nameValue2 nameKey=".preOpTemp" nameClass="vam">
                        <cti:displayForPageEditModes modes="CREATE,EDIT">
                            <cti:msg var="preOpTempRange" key="yukon.web.modules.dr.setup.gear.range" arguments="-20,20"/>
                        </cti:displayForPageEditModes>
                        <tags:numeric path="fields.preOpTemp" units="${preOpTempRange}" minValue="-20" maxValue="20" size="5"/>
                    </tags:nameValue2>
                    <tags:nameValue2 nameKey=".preOpTimeInMinutes" nameClass="vam">
                        <cti:msg var="preOpTimeInMinutesRange" key="yukon.web.modules.dr.setup.gear.range" arguments="0:00,5:00"/>
                        <dt:timeOffset path="fields.preOpTimeInMinutes" wrapClass="fn vam" minValue="0" maxValue="300" rangeText="${preOpTimeInMinutesRange}"/>
                    </tags:nameValue2>
                    <tags:nameValue2 nameKey=".preOpHoldInMinutes" nameClass="vam">
                        <cti:msg var="preOpHoldInMinutesRange" key="yukon.web.modules.dr.setup.gear.range" arguments="0:00,5:00"/>
                        <dt:timeOffset path="fields.preOpHoldInMinutes" wrapClass="fn vam" minValue="0" maxValue="300" rangeText="${preOpHoldInMinutesRange}"/>
                    </tags:nameValue2>
                    <tags:nameValue2 nameKey=".maxRuntimeInMinutes" nameClass="vam">
                        <cti:msg var="maxRuntimeRange" key="yukon.web.modules.dr.setup.gear.range" arguments="4:00,23:59"/>
                        <dt:timeOffset path="fields.maxRuntimeInMinutes" wrapClass="fn vam" minValue="240" rangeText="${maxRuntimeRange}"/>
                    </tags:nameValue2>
                </tags:nameValueContainer2>
            </tags:sectionContainer2>
            <tags:sectionContainer2 nameKey="optionalAttributes">
                <tags:nameValueContainer2>
                    <c:if test="${not empty selectedGearType}">
                        <%@ include file="gearWhenToChange.jsp" %>
                    </c:if>
                </tags:nameValueContainer2>
            </tags:sectionContainer2>
        </div>
        <div class="column two nogutter"> 
            <tags:sectionContainer2 nameKey="rampIn">
                <tags:nameValueContainer2>
                    <tags:nameValue2 nameKey=".rampPerHour" nameClass="vam">
                        <cti:displayForPageEditModes modes="CREATE,EDIT">
                            <cti:msg var="rampPerHourRange" key="yukon.web.modules.dr.setup.gear.range" arguments="-9.9,9.9"/>
                        </cti:displayForPageEditModes>
                        <tags:numeric path="fields.rampPerHour"
                                      units="${rampPerHourRange}"
                                      size="5"
                                      minValue="-9.9" 
                                      maxValue="9.9"
                                      isDecimalNumber="true"
                                      stepValue="0.1"/>
                    </tags:nameValue2>
                    <tags:nameValue2 nameKey=".max" nameClass="vam">
                        <cti:displayForPageEditModes modes="CREATE,EDIT">
                            <cti:msg var="maxDegreeDeltaRange" key="yukon.web.modules.dr.setup.gear.range" arguments="0,20"/>
                        </cti:displayForPageEditModes>
                        <tags:numeric path="fields.max" units="${maxDegreeDeltaRange}" size="5" minValue="0" maxValue="20"/>
                    </tags:nameValue2>
                </tags:nameValueContainer2>
            </tags:sectionContainer2>
            <tags:sectionContainer2 nameKey="stopControl">
                <tags:nameValueContainer2>
                    <tags:nameValue2 nameKey=".rampOutTimeInMinutes" nameClass="vam">
                        <cti:msg var="rampOutTimeInMinutesRange" key="yukon.web.modules.dr.setup.gear.range" arguments="0:00,5:00"/>
                        <dt:timeOffset path="fields.rampOutTimeInMinutes" wrapClass="fn vam" minValue="0" maxValue="300" rangeText="${rampOutTimeInMinutesRange}"/>
                    </tags:nameValue2>
                    <%@ include file="howToStopControl.jsp" %>
                </tags:nameValueContainer2>
            </tags:sectionContainer2>
        </div>
    </div>
</cti:msgScope>