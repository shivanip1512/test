<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<cti:msgScope paths="yukon.web.modules.dr.setup.gear">
    <div class="column-12-12 clearfix js-simple-thermostat-ramping-gear">
        <div class="column one">
            <tags:sectionContainer2 nameKey="controlParameters">
                <tags:nameValueContainer2>
                    <tags:nameValue2 nameKey=".mode">
                        <cti:displayForPageEditModes modes="CREATE,EDIT">
                            <c:forEach var="tempreatureMode" items="${tempreatureModes}" varStatus="status">
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
                                <tags:radio path="fields.mode" value="${tempreatureMode}" classes="${css}" key=".${tempreatureMode}" />
                            </c:forEach>
                        </cti:displayForPageEditModes>
                        <cti:displayForPageEditModes modes="VIEW">
                            <i:inline key=".${programGear.fields.mode}"/>
                        </cti:displayForPageEditModes>
                    </tags:nameValue2>
                    <tags:nameValue2 nameKey=".randomStartTime" nameClass="vam">
                        <table>
                            <tr>
                                <td><dt:timeOffset path="fields.randomStartTimeInMinutes" minValue="0" maxValue="120"/></td>
                                <cti:displayForPageEditModes modes="CREATE,EDIT">
                                    <td class="vam">(0:00 to 2:00)</td>
                                </cti:displayForPageEditModes>
                            </tr>
                        </table>
                    </tags:nameValue2>
                </tags:nameValueContainer2>
                
                <br/>
                <h3><i:inline key=".preOpCoolOrHeat"/></h3>
                
                <tags:nameValueContainer2>
                    <tags:nameValue2 nameKey=".temp" nameClass="vam">
                        <table>
                            <tr>
                                <td class="vam"><tags:numeric path="fields.preOpTemp" minValue="-20" maxValue="20" size="5"/></td>
                                <cti:displayForPageEditModes modes="CREATE,EDIT">
                                    <td class="vam">(-20 to 20)</td>
                                </cti:displayForPageEditModes>
                            </tr>
                        </table>
                    </tags:nameValue2>
                    <tags:nameValue2 nameKey=".time">
                        <table>
                            <tr>
                                <td><dt:timeOffset path="fields.preOpTimeInMinutes" minValue="0" maxValue="300"/></td>
                                <cti:displayForPageEditModes modes="CREATE,EDIT">
                                    <td class="vam">(0:00 to 5:00)</td>
                                </cti:displayForPageEditModes>
                            </tr>
                        </table>
                    </tags:nameValue2>
                    <tags:nameValue2 nameKey=".hold">
                        <table>
                            <tr>
                                <td><dt:timeOffset path="fields.preOpHoldInMinutes" minValue="0" maxValue="300"/></td>
                                <cti:displayForPageEditModes modes="CREATE,EDIT">
                                    <td class="vam">(0:00 to 5:00)</td>
                                </cti:displayForPageEditModes>
                            </tr>
                        </table>
                    </tags:nameValue2>
                    <tags:nameValue2 nameKey=".maxRuntime">
                        <table>
                            <tr>
                                <td><dt:timeOffset path="fields.maxRuntimeInMinutes" minValue="240" maxValue="1439"/></td>
                                <cti:displayForPageEditModes modes="CREATE,EDIT">
                                    <td class="vam">(4:00 to 24:00)</td>
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
                    <tags:nameValue2 nameKey=".rampFahrenheitPerHour" nameClass="vam">
                        <table>
                            <tr>
                                <td class="vam"><tags:numeric path="fields.rampPerHour" size="5" minValue="-9.9" maxValue="9.9" isDecimalNumber="true"/></td>
                                <cti:displayForPageEditModes modes="CREATE,EDIT">
                                    <td class="vam">(-9.9 to 9.9)</td>
                                </cti:displayForPageEditModes>
                            </tr>
                        </table>
                    </tags:nameValue2>
                    <tags:nameValue2 nameKey=".maxDegreeDelta" nameClass="vam">
                        <table>
                            <tr>
                                <td><tags:numeric path="fields.max" size="5" minValue="0" maxValue="20"/></td>
                                <cti:displayForPageEditModes modes="CREATE,EDIT">
                                    <td class="vam">(0 to 20)</td>
                                </cti:displayForPageEditModes>
                            </tr>
                        </table>
                    </tags:nameValue2>
                </tags:nameValueContainer2>
            </tags:sectionContainer2>
            <tags:sectionContainer2 nameKey="stopControl">
                <tags:nameValueContainer2>
                    <tags:nameValue2 nameKey=".rampOutTime" nameClass="vam">
                        <table>
                            <tr>
                                <td class="vam">
                                    <dt:timeOffset path="fields.rampOutTimeInMinutes" minValue="0" maxValue="300"/>
                                </td>
                                <cti:displayForPageEditModes modes="CREATE,EDIT">
                                    <td class="vam">(0:00 to 5:00)</td>
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