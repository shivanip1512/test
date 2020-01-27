<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<cti:msgScope paths="yukon.web.modules.dr.setup.gear">
    <cti:msg2 var="heatingOffsetLbl" key=".offset"/>
    <cti:msg2 var="coolingOffsetLbl" key=".coolingOffset"/>
            
    <input type="hidden" class="js-heating-offset-lbl" value="${heatingOffsetLbl}"/>
    <input type="hidden" class="js-cooling-offset-lbl" value="${coolingOffsetLbl}"/>

    <c:choose>
        <c:when test="${programGear.fields.mode == 'HEAT'}">
            <c:set var="modeOffsetLblKey" value=".offset"/>
        </c:when>
        <c:otherwise>
            <c:set var="modeOffsetLblKey" value=".coolingOffset"/>
        </c:otherwise>
    </c:choose>
    <div class="column-12-12 clearfix">
        <div class="column one">
            <tags:sectionContainer2 nameKey="controlParameters" styleClass="js-sep-temperature-ctrl-prms">
                <tags:nameValueContainer2>
                    <tags:nameValue2 nameKey=".ramp">
                        <cti:msg2 var="rampInLbl" key=".rampIn"/>
                        <cti:msg2 var="rampOutBtnLbl" key=".rampOut"/>
                        <cti:displayForPageEditModes modes="CREATE,EDIT">
                            <div class="button-group">
                                <tags:check path="fields.rampIn" label="${rampInLbl}" classes="ML0"/>
                                <tags:check path="fields.rampOut" label="${rampOutBtnLbl}"/>
                            </div>
                        </cti:displayForPageEditModes>
                        <cti:displayForPageEditModes modes="VIEW">
                            <c:choose>
                                <c:when test="${programGear.fields.rampIn && programGear.fields.rampOut}">
                                    ${rampInLbl},&nbsp;${rampOutBtnLbl}
                                </c:when>
                                <c:when test="${programGear.fields.rampIn}">
                                    ${rampInLbl}
                                </c:when>
                                <c:when test="${programGear.fields.rampOut}">
                                    ${rampOutBtnLbl}
                                </c:when>
                                <c:otherwise>
                                    <i:inline key="yukon.common.none.choice"/>
                                </c:otherwise>
                            </c:choose>
                        </cti:displayForPageEditModes>
                    </tags:nameValue2>
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
                                <tags:radio path="fields.mode" value="${temperatureMode}" classes="${css}"
                                            key=".${temperatureMode}" inputClass="js-temperature-mode"/>
                            </c:forEach>
                        </cti:displayForPageEditModes>
                        <cti:displayForPageEditModes modes="VIEW">
                            <i:inline key=".${programGear.fields.mode}"/>
                        </cti:displayForPageEditModes>
                    </tags:nameValue2>
                    <tags:nameValue2 nameKey=".tempMeasureUnit">
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
                                <tags:radio path="fields.tempMeasureUnit" value="${unit}" classes="${css}" key=".${unit}" />
                            </c:forEach>
                        </cti:displayForPageEditModes>
                        <cti:displayForPageEditModes modes="VIEW">
                            <i:inline key=".${programGear.fields.tempMeasureUnit}"/>
                        </cti:displayForPageEditModes>
                    </tags:nameValue2>
                    <tags:nameValue2 nameKey="${modeOffsetLblKey}" nameClass="vam js-temperature-mode-td">
                        <tags:numeric path="fields.offset" size="5" isDecimalNumber="true" stepValue="0.1"/>
                    </tags:nameValue2>
                    <tags:nameValue2 nameKey=".criticality" nameClass="vam">
                        <tags:numeric path="fields.criticality" size="5" minValue="1" maxValue="15"/>
                    </tags:nameValue2>
                </tags:nameValueContainer2>
            </tags:sectionContainer2>
        </div>
        <div class="column two nogutter"> 
            <tags:sectionContainer2 nameKey="optionalAttributes">
                <tags:nameValueContainer2>
                    <tags:nameValue2 nameKey=".capacityReduction">
                        <cti:msg2 var="percent" key="yukon.common.units.PERCENT"/>
                        <tags:numeric path="fields.capacityReduction" units="${percent}" size="5" minValue="0" maxValue="100"/>
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