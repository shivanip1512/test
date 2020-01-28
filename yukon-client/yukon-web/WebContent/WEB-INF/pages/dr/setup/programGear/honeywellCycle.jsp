
<cti:msgScope paths="yukon.web.modules.dr.setup.gear">
    <div class="column-12-12 clearfix">
        <div class="column one">
            <tags:sectionContainer2 nameKey="controlParameters">
                <tags:nameValueContainer2>
                    <cti:msg2 key="yukon.web.components.button.select.label" var="selectLbl"/>
                    <tags:nameValue2 nameKey=".controlPercent">
                        <cti:msg2 var="percent" key="yukon.common.units.PERCENT"/>
                        <tags:numeric path="fields.controlPercent" units="${percent}" size="10" minValue="0" maxValue="100"/>
                    </tags:nameValue2>
                    <tags:nameValue2 nameKey=".cyclePeriodInMinutes"> 
                        <cti:displayForPageEditModes modes="EDIT,CREATE">
                            <tags:selectWithItems items="${cyclePeriod}" path="fields.cyclePeriodInMinutes" />
                        </cti:displayForPageEditModes>
                        <cti:displayForPageEditModes modes="VIEW">
                            ${programGear.fields.cyclePeriodInMinutes}
                        </cti:displayForPageEditModes>
                    </tags:nameValue2>
                    <tags:nameValue2 nameKey=".howToStopControl">
                        <cti:displayForPageEditModes modes="EDIT,CREATE">
                            <tags:selectWithItems items="${howToStopControl}" path="fields.howToStopControl" />
                        </cti:displayForPageEditModes>
                        <cti:displayForPageEditModes modes="VIEW">
                            ${programGear.fields.howToStopControl}
                        </cti:displayForPageEditModes>
                    </tags:nameValue2>
                </tags:nameValueContainer2>
            </tags:sectionContainer2>
            <tags:sectionContainer2 nameKey="optionalAttributes">
                <tags:nameValueContainer2>
                    <tags:nameValue2 nameKey=".capacityReduction">
                        <cti:msg2 var="percent" key="yukon.common.units.PERCENT"/>
                        <tags:numeric path="fields.capacityReduction" units="${percent}" size="10" minValue="0" maxValue="100"/>
                    </tags:nameValue2>
                    <c:if test="${not empty selectedGearType}">
                        <%@ include file="gearWhenToChange.jsp" %>
                    </c:if>
                </tags:nameValueContainer2>
            </tags:sectionContainer2>
        </div>
        <div class="column two nogutter"> 
            <tags:sectionContainer2 nameKey="rampInRampOut">
                <tags:nameValueContainer2>
                    <tags:nameValue2 nameKey=".rampInOut">
                        <tags:switchButton path="fields.rampInOut"   onNameKey=".yes" offNameKey=".no" />
                    </tags:nameValue2>
                </tags:nameValueContainer2>
            </tags:sectionContainer2>
        </div>
    </div>
    <cti:includeScript link="/resources/js/pages/yukon.dr.setup.programGear.js" />
</cti:msgScope>
