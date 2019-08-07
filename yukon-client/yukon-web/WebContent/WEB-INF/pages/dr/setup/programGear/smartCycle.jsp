<%@ taglib prefix="tags" tagdir="/WEB-INF/tags"%>

<cti:msgScope paths="yukon.web.modules.dr.setup.gear">
    <div class="column-12-12 clearfix">
        <div class="column one">
            <tags:sectionContainer2 nameKey="controlParameters">
                <tags:nameValueContainer2>
                    <tags:nameValue2 nameKey=".controlPercent">
                        <tags:numeric path="fields.controlPercent" size="10" minValue="5" maxValue="100" stepValue="1"/>
                    </tags:nameValue2>
                    <tags:nameValue2 nameKey=".cyclePeriod"> 
                        <tags:numeric path="fields.cyclePeriodInMinutes" size="10" minValue="1" maxValue="945" stepValue="1"/>
                    </tags:nameValue2>
                    <tags:nameValue2 nameKey=".cycleCountSendType">
                        <cti:displayForPageEditModes modes="EDIT,CREATE">
                            <tags:selectWithItems items="${cycleCountSendType}" path="fields.cycleCountSendType" />
                        </cti:displayForPageEditModes>
                    </tags:nameValue2>
                    <tags:nameValue2 nameKey=".maxCycleCount">
                        <cti:displayForPageEditModes modes="EDIT,CREATE">
                            <tags:selectWithItems items="${maxCycleCount}" path="fields.maxCycleCount" defaultItemLabel="(none)" defaultItemValue="0"/>
                        </cti:displayForPageEditModes>
                    </tags:nameValue2>
                    <tags:nameValue2 nameKey=".startingPeriodCount">
                        <cti:displayForPageEditModes modes="EDIT,CREATE">
                            <tags:selectWithItems items="${startingPeriodCount}" path="fields.startingPeriodCount" defaultItemLabel="None"/>
                        </cti:displayForPageEditModes>
                    </tags:nameValue2>
                    <tags:nameValue2 nameKey=".commandResendRate">
                        <tags:intervalDropdown path="fields.sendRate" intervals="${commandResendRate}"/>
                    </tags:nameValue2>
                </tags:nameValueContainer2>
            </tags:sectionContainer2>
            <tags:sectionContainer2 nameKey="optionalAttributes">
                <tags:nameValueContainer2>
                    <tags:nameValue2 nameKey=".groupCapacityReduction">
                        <tags:numeric path="fields.capacityReduction" size="10" minValue="0" maxValue="100" stepValue="1"/>
                    </tags:nameValue2>
                    <c:if test="${selectedGearType == 'TargetCycle'}">
                        <tags:nameValue2 nameKey=".kWReduction">
                            <tags:numeric path="fields.kWReduction" size="10" minValue="0" maxValue="99999.999" stepValue="1"/>
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
                    <tags:switchButton path="fields.noRamp" onNameKey=".yes.label" offNameKey=".no.label"/>
                </tags:nameValue2>
                </tags:nameValueContainer2>
            </tags:sectionContainer2>

        <tags:sectionContainer2 nameKey="stopControl">
            <tags:nameValueContainer2>
                <tags:nameValue2 nameKey=".howToStopControl">
                <cti:displayForPageEditModes modes="EDIT,CREATE">
                    <tags:selectWithItems items="${howToStopControl}" path="fields.howToStopControl" />
                </cti:displayForPageEditModes>
                </tags:nameValue2>
                    <c:if test="${selectedGearType == 'SmartCycle' || selectedGearType == 'TrueCycle'}">
                        <tags:nameValue2 nameKey=".stopCommandRepeat">
                            <tags:numeric path="fields.stopCommandRepeat" size="10" minValue="0" maxValue="5" stepValue="1"/>
                        </tags:nameValue2>
                    </c:if>
                </tags:nameValueContainer2>
            </tags:sectionContainer2>
        </div>
    </div>
</cti:msgScope>
