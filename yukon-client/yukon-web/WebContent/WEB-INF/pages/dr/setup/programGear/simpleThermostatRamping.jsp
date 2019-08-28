<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<cti:msgScope paths="yukon.web.modules.dr.setup.gear">
    <div class="column-12-12 clearfix js-simple-thermostat-ramping-gear">
        <div class="column one">
            <tags:sectionContainer2 nameKey="controlParameters">
                <tags:nameValueContainer2>
                    <tags:nameValue2 nameKey=".mode">
                        <cti:displayForPageEditModes modes="CREATE,EDIT">
                            <c:forEach var="temperatureMode" items="${temperatureModes}" varStatus="status">
                                <c:choose>
                                    <c:when test="${status.index == 0}">
                                        <c:set var="css" value="left yes ML0"/>
                                    </c:when>
                                    <c:when test="${status.index == fn:length(units)-1}">
                                        <c:set var="css" value="right yes"/>
                                    </c:when>
                                    <c:otherwise>
                                        <c:set var="css" value="middle yes"/>
                                    </c:otherwise>
                                </c:choose>
                                <tags:radio path="fields.mode" value="${temperatureMode}" classes="${css}" key=".${temperatureMode}" />
                            </c:forEach>
                        </cti:displayForPageEditModes>
                        <cti:displayForPageEditModes modes="VIEW">
                            <i:inline key=".${programGear.fields.mode}"/>
                        </cti:displayForPageEditModes>
                    </tags:nameValue2>
                    <tags:nameValue2 nameKey=".randomStartTime">
                        <table>
                            <tr>
                                <td><dt:timeOffset path="fields.randomStartTimeInMinutes" wrapClass="fn" minValue="0" maxValue="120"/></td>
                                <cti:displayForPageEditModes modes="CREATE,EDIT">
                                    <td>(0:00 to 2:00)</td>
                                </cti:displayForPageEditModes>
                            </tr>
                        </table>
                    </tags:nameValue2>
                </tags:nameValueContainer2>
                
                <br/>
                <h3><i:inline key=".preOpCoolOrHeat"/></h3>
                
                <tags:nameValueContainer2>
                    <tags:nameValue2 nameKey=".temp">
                         <table>
                            <tr>
                                <td>
                                    <cti:displayForPageEditModes modes="CREATE,EDIT">
                                        <cti:msg2 var="preOpTempRange" key="yukon.web.modules.dr.setup.gear.preOpTempRange"/>
                                    </cti:displayForPageEditModes>
                                    <tags:numeric path="fields.preOpTemp" units="${preOpTempRange}" minValue="-20" maxValue="20" size="5"/>
                                </td>
                            </tr>
                        </table>
                    </tags:nameValue2>
                    <tags:nameValue2 nameKey=".time">
                        <table>
                            <tr>
                                <td><dt:timeOffset path="fields.preOpTimeInMinutes" wrapClass="fn" minValue="0" maxValue="300"/></td>
                                <cti:displayForPageEditModes modes="CREATE,EDIT">
                                    <td>(0:00 to 5:00)</td>
                                </cti:displayForPageEditModes>
                            </tr>
                        </table>
                    </tags:nameValue2>
                    <tags:nameValue2 nameKey=".hold">
                        <table>
                            <tr>
                                <td><dt:timeOffset path="fields.preOpHoldInMinutes" wrapClass="fn" minValue="0" maxValue="300"/></td>
                                <cti:displayForPageEditModes modes="CREATE,EDIT">
                                    <td>(0:00 to 5:00)</td>
                                </cti:displayForPageEditModes>
                            </tr>
                        </table>
                    </tags:nameValue2>
                    <tags:nameValue2 nameKey=".maxRuntime">
                        <table>
                            <tr>
                                <td><dt:timeOffset path="fields.maxRuntimeInMinutes" wrapClass="fn" minValue="240"/></td>
                                <cti:displayForPageEditModes modes="CREATE,EDIT">
                                    <td>(4:00 to 23:59)</td>
                                </cti:displayForPageEditModes>
                            </tr>
                        </table>
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
                    <tags:nameValue2 nameKey=".rampFahrenheitPerHour">
                        <cti:displayForPageEditModes modes="CREATE,EDIT">
                            <cti:msg2 var="rampPerHourRange" key="yukon.web.modules.dr.setup.gear.rampPerHourRange"/>
                        </cti:displayForPageEditModes>
                        <tags:numeric path="fields.rampPerHour"
                                      units="${rampPerHourRange}"
                                      size="5"
                                      minValue="-9.9" 
                                      maxValue="9.9"
                                      isDecimalNumber="true"
                                      stepValue="0.1"/>
                    </tags:nameValue2>
                    <tags:nameValue2 nameKey=".maxDegreeDelta">
                        <cti:displayForPageEditModes modes="CREATE,EDIT">
                            <cti:msg2 var="maxDegreeDeltaRange" key="yukon.web.modules.dr.setup.gear.maxDegreeDeltaRange"/>
                        </cti:displayForPageEditModes>
                        <tags:numeric path="fields.max" units="${maxDegreeDeltaRange}" size="5" minValue="0" maxValue="20"/>
                    </tags:nameValue2>
                </tags:nameValueContainer2>
            </tags:sectionContainer2>
            <tags:sectionContainer2 nameKey="stopControl">
                <tags:nameValueContainer2>
                    <tags:nameValue2 nameKey=".rampOutTime">
                        <table>
                            <tr>
                                <td>
                                    <dt:timeOffset path="fields.rampOutTimeInMinutes" wrapClass="fn" minValue="0" maxValue="300"/>
                                </td>
                                <cti:displayForPageEditModes modes="CREATE,EDIT">
                                    <td>(0:00 to 5:00)</td>
                                </cti:displayForPageEditModes>
                            </tr>
                        </table>
                    </tags:nameValue2>
                    <%@ include file="howToStopControl.jsp" %>
                </tags:nameValueContainer2>
            </tags:sectionContainer2>
        </div>
    </div>
</cti:msgScope>