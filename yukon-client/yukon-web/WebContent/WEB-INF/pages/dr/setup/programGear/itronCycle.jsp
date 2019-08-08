
<cti:msgScope paths="yukon.web.modules.dr.setup.gear">
    <div class="column-12-12 clearfix">
        <div class="column one">
            <tags:sectionContainer2 nameKey="controlParameters">
                <tags:nameValueContainer2>
                    <cti:msg2 key="yukon.web.components.button.select.label" var="selectLbl"/>
                    <tags:nameValue2 nameKey=".dutyCycleType">
                        <cti:displayForPageEditModes modes="EDIT,CREATE">
                            <tags:selectWithItems items="${cycleType}" path="fields.cycleType" />
                        </cti:displayForPageEditModes>
                        <cti:displayForPageEditModes modes="VIEW">
                            <i:inline key="${programGear.fields.cycleType}"/>
                        </cti:displayForPageEditModes>
                    </tags:nameValue2> 
                    <tags:nameValue2 nameKey=".dutyCyclePercentage">
                        <tags:numeric path="fields.dutyCyclePercent" size="10" minValue="0" maxValue="100"/>
                    </tags:nameValue2>
                    <tags:nameValue2 nameKey=".dutyCyclePeriod">
                        <cti:displayForPageEditModes modes="EDIT,CREATE">
                            <tags:selectWithItems items="${dutyCyclePeriod}" path="fields.dutyCyclePeriodInMinutes"/>
                        </cti:displayForPageEditModes>
                        <cti:displayForPageEditModes modes="VIEW">
                            ${programGear.fields.dutyCyclePeriodInMinutes}
                        </cti:displayForPageEditModes>
                    </tags:nameValue2>  
                    <tags:nameValue2 nameKey=".criticality">
                        <tags:numeric path="fields.criticality" size="10" minValue="0" maxValue="255"/>
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
                    <tags:nameValue2 nameKey=".groupCapacityReduction">
                        <tags:numeric path="fields.capacityReduction" size="10" minValue="0" maxValue="100"/>
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
                    <tags:nameValue2 nameKey=".rampIn">
                        <tags:switchButton path="fields.rampIn" onNameKey=".yes" offNameKey=".no" />
                    </tags:nameValue2>
                    <tags:nameValue2 nameKey=".rampOut">
                        <tags:switchButton path="fields.rampOut" onNameKey=".yes" offNameKey=".no" />
                    </tags:nameValue2>
                </tags:nameValueContainer2>
            </tags:sectionContainer2>
        </div>
    </div>
<cti:includeScript link="/resources/js/pages/yukon.dr.setup.programGear.js" />
</cti:msgScope>
