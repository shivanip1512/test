<%@ taglib prefix="tags" tagdir="/WEB-INF/tags"%>

<cti:msgScope paths="yukon.web.modules.dr.setup.gear">
    <div class="column-12-12 clearfix">
        <div class="column one">
            <tags:sectionContainer2 nameKey="controlParameters">
                <tags:nameValueContainer2>
                    <tags:nameValue2 nameKey=".controlPercent">
                        <cti:msg2 var="percent" key="yukon.common.units.PERCENT"/>
                        <tags:numeric path="fields.controlPercent" units="${percent}" size="10" minValue="5" maxValue="100"/>
                    </tags:nameValue2>
                    <tags:nameValue2 nameKey=".cyclePeriodInMinutes">
                        <tags:numeric path="fields.cyclePeriodInMinutes" size="10" minValue="1" maxValue="945"/>
                    </tags:nameValue2>
                    <tags:nameValue2 nameKey=".cycleCountSendType">
                        <cti:displayForPageEditModes modes="EDIT,CREATE">
                            <tags:selectWithItems items="${cycleCountSendType}" path="fields.cycleCountSendType" />
                        </cti:displayForPageEditModes>
                        <cti:displayForPageEditModes modes="VIEW">
                            <i:inline key="${programGear.fields.cycleCountSendType}" />
                        </cti:displayForPageEditModes>
                    </tags:nameValue2>
                    <tags:nameValue2 nameKey=".maxCycleCount">
                        <cti:displayForPageEditModes modes="EDIT,CREATE">
                            <cti:msg2 key=".NONE" var="selectLbl"/>
                            <tags:selectWithItems items="${maxCycleCount}" path="fields.maxCycleCount" defaultItemLabel="${selectLbl}" defaultItemValue="0" />
                        </cti:displayForPageEditModes>
                        <cti:displayForPageEditModes modes="VIEW">
                            <c:choose>
                                <c:when test="${programGear.fields.maxCycleCount == 0}">
                                    <i:inline key=".NONE" />
                                </c:when>
                                <c:otherwise>
                                    <i:inline key="${programGear.fields.maxCycleCount}" />
                                </c:otherwise>
                            </c:choose>
                        </cti:displayForPageEditModes>
                    </tags:nameValue2>
                    <tags:nameValue2 nameKey=".startingPeriodCount">
                        <cti:displayForPageEditModes modes="EDIT,CREATE">
                            <tags:selectWithItems items="${startingPeriodCount}" path="fields.startingPeriodCount"/>
                        </cti:displayForPageEditModes>
                        <cti:displayForPageEditModes modes="VIEW">
                           <i:inline key="${programGear.fields.startingPeriodCount}" />
                        </cti:displayForPageEditModes>
                    </tags:nameValue2>
                    <tags:nameValue2 nameKey=".commandResendRate">
                        <tags:intervalDropdown path="fields.sendRate" intervals="${commandResendRate}" />
                    </tags:nameValue2>
                </tags:nameValueContainer2>
            </tags:sectionContainer2>

            <tags:sectionContainer2 nameKey="optionalAttributes">
                <tags:nameValueContainer2>
                    <tags:nameValue2 nameKey=".capacityReduction">
                        <cti:msg2 var="percent" key="yukon.common.units.PERCENT"/>
                        <tags:numeric path="fields.capacityReduction" units="${percent}" size="10" minValue="0" maxValue="100"/>
                    </tags:nameValue2>
                    <c:if test="${selectedGearType == 'TargetCycle'}">
                        <tags:nameValue2 nameKey=".kWReduction">
                            <tags:numeric path="fields.kWReduction" size="10" minValue="0" maxValue="99999.999"/>
                        </tags:nameValue2>
                    </c:if>
                    <%@ include file="gearWhenToChange.jsp" %>
                </tags:nameValueContainer2>
            </tags:sectionContainer2>
        </div>

        <div class="column two nogutter">
            <tags:sectionContainer2 nameKey="rampIn">
                <tags:nameValueContainer2>
                    <tags:nameValue2 nameKey=".noRamp">
                        <tags:switchButton path="fields.noRamp" onNameKey=".yes.label" offNameKey=".no.label" />
                    </tags:nameValue2>
                </tags:nameValueContainer2>
            </tags:sectionContainer2>

            <tags:sectionContainer2 nameKey="stopControl">
                <tags:nameValueContainer2>
                    <tags:nameValue2 nameKey=".howToStopControl">
                        <cti:displayForPageEditModes modes="EDIT,CREATE">
                            <tags:selectWithItems items="${howToStopControl}" path="fields.howToStopControl" />
                        </cti:displayForPageEditModes>
                        <cti:displayForPageEditModes modes="VIEW">
                             <i:inline key="${programGear.fields.howToStopControl}" />
                        </cti:displayForPageEditModes>
                    </tags:nameValue2>
                    <c:if test="${selectedGearType == 'SmartCycle' || selectedGearType == 'TrueCycle'}">
                        <tags:nameValue2 nameKey=".stopCommandRepeat">
                            <tags:numeric path="fields.stopCommandRepeat" size="10" minValue="0" maxValue="5"/>
                        </tags:nameValue2>
                    </c:if>
                </tags:nameValueContainer2>
            </tags:sectionContainer2>
        </div>
    </div>
</cti:msgScope>
