<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<cti:msgScope paths="yukon.web.modules.dr.setup.gear">
    <div class="column-12-12 clearfix">
        <div class="column one">
            <tags:sectionContainer2 nameKey="controlParameters">
                <tags:nameValueContainer2>
                    <tags:nameValue2 nameKey=".mode">
                        <cti:msg2 var="heatBtnLbl" key=".mode.heat"/>
                        <cti:msg2 var="coolBtnLbl" key=".mode.cool"/>
                        <cti:displayForPageEditModes modes="CREATE,EDIT">
                            <div class="button-group">
                                <tags:check path="fields.isHeatMode" label="${heatBtnLbl}" classes="ML0"/>
                                <tags:check path="fields.isCoolMode" label="${coolBtnLbl}"/>
                            </div>
                        </cti:displayForPageEditModes>
                        <cti:displayForPageEditModes modes="VIEW">
                            <c:choose>
                                <c:when test="${programGear.fields.isHeatMode && programGear.fields.isCoolMode}">
                                    ${heatBtnLbl},&nbsp;${coolBtnLbl}
                                </c:when>
                                <c:when test="${programGear.fields.isHeatMode}">
                                    ${heatBtnLbl}
                                </c:when>
                                <c:when test="${programGear.fields.isCoolMode}">
                                    ${coolBtnLbl}
                                </c:when>
                            </c:choose>
                        </cti:displayForPageEditModes>
                    </tags:nameValue2>
                    <tags:nameValue2 nameKey=".unit">
                        <cti:displayForPageEditModes modes="CREATE,EDIT">
                            <c:forEach var="unit" items="${units}" varStatus="status">
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
                                <tags:radio path="fields.measureUnit" value="${unit}" classes="${css}" key=".${unit}" />
                            </c:forEach>
                        </cti:displayForPageEditModes>
                        <cti:displayForPageEditModes modes="VIEW">
                            <i:inline key=".${programGear.fields.measureUnit}"/>
                        </cti:displayForPageEditModes>
                    </tags:nameValue2>
                    <tags:nameValue2 nameKey=".setpoint">
                        <cti:displayForPageEditModes modes="CREATE,EDIT">
                            <c:forEach var="setpoint" items="${setpoints}" varStatus="status">
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
                                <tags:radio path="fields.absoluteOrDelta" value="${setpoint}" classes="${css}" key=".${setpoint}" />
                            </c:forEach>
                        </cti:displayForPageEditModes>
                        <cti:displayForPageEditModes modes="VIEW">
                            <i:inline key=".${programGear.fields.absoluteOrDelta}"/>
                        </cti:displayForPageEditModes>
                    </tags:nameValue2>
                    <tags:nameValue2 nameKey=".minimumTemperature">
                        <tags:input path="fields.minValue"/>
                    </tags:nameValue2>
                    <tags:nameValue2 nameKey=".maximumTempreature">
                        <tags:input path="fields.maxValue"/>
                    </tags:nameValue2>
                    <tags:nameValue2 nameKey=".randomOffsetTime">
                        <tags:input path="fields.random"/>
                    </tags:nameValue2>
                    <tags:nameValue2 nameKey=".ta">
                        <tags:input path="fields.valueTa" size="3"/>
                    </tags:nameValue2>
                    <tags:nameValue2 nameKey=".tb">
                        <table>
                            <tr>
                                <td><tags:input path="fields.valueTb" size="3" inputClass="js-delta-value"/></td>
                                <td><cti:msg2 key=".valueB"/>:</td>
                                <td><tags:input path="fields.valueB" size="3"/></td>
                            </tr>
                        </table>
                    </tags:nameValue2>
                    <tags:nameValue2 nameKey=".tc">
                        <tags:input path="fields.valueTc" size="3" inputClass="js-valueTb"/>
                    </tags:nameValue2>
                    <tags:nameValue2 nameKey=".td">
                        <table>
                            <tr>
                                <td><tags:input path="fields.valueTd" size="3" inputClass="js-delta-value"/></td>
                                <td><cti:msg2 key=".valueD"/>:</td>
                                <td><tags:input path="fields.valueD" size="3"/></td>
                            </tr>
                        </table>
                    </tags:nameValue2>
                    <tags:nameValue2 nameKey=".te">
                        <tags:input path="fields.valueTe" size="3" inputClass="test"/>
                    </tags:nameValue2>
                    <tags:nameValue2 nameKey=".tf">
                        <table>
                            <tr>
                                <td><tags:input path="fields.valueTf" size="3" inputClass="js-delta-value"/></td>
                                <td><cti:msg2 key=".valueF"/>:</td>
                                <td><tags:input path="fields.valueF" size="3"/></td>
                            </tr>
                        </table>
                    </tags:nameValue2>
                </tags:nameValueContainer2>
            </tags:sectionContainer2>
        </div>
        <div class="column two nogutter"> 
            <tags:sectionContainer2 nameKey="optionalAttributes">
                <tags:nameValueContainer2>
                    <tags:nameValue2 nameKey=".groupCapacityReductionPercentage">
                        <tags:numeric path="fields.capacityReduction" size="5" minValue="0" maxValue="100" />
                    </tags:nameValue2>
                    <c:if test="${not empty selectedGearType}">
                        <%@ include file="gearWhenToChange.jsp" %>
                    </c:if>
                </tags:nameValueContainer2>
            </tags:sectionContainer2>
            <tags:sectionContainer2 nameKey="stopControl">
                <tags:nameValueContainer2>
                    <%@ include file="howToStopControl.jsp" %>
                </tags:nameValueContainer2>
            </tags:sectionContainer2>
        </div>
    </div>
</cti:msgScope>