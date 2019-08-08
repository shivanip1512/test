
<cti:msgScope paths="yukon.web.modules.dr.setup.gear">
    <div class="column-12-12 clearfix">
        <div class="column one">
            <tags:sectionContainer2 nameKey="controlParameters">
                <tags:nameValueContainer2>
                    <tags:nameValue2 nameKey=".mandatory">
                        <tags:switchButton path="fields.mandatory" onNameKey=".yes" offNameKey=".no" />
                    </tags:nameValue2>
                    <tags:nameValue2 nameKey=".controlPercent">
                        <tags:numeric path="fields.controlPercent" size="10" minValue="5" maxValue="100" />
                    </tags:nameValue2>
                    <tags:nameValue2 nameKey=".howToStopControl">
                        <cti:displayForPageEditModes modes="EDIT,CREATE">
                            <cti:msg2 key="yukon.web.components.button.select.label" var="selectLbl"/>
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
                        <tags:numeric path="fields.capacityReduction" size="10" minValue="0" maxValue="100" />
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
