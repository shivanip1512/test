<%@ taglib prefix="tags" tagdir="/WEB-INF/tags"%>

<cti:msgScope paths="yukon.web.modules.dr.setup.gear">
    <div class="column-12-12 clearfix">
        <div class="column one">
            <tags:sectionContainer2 nameKey="controlParameters">
                <tags:nameValueContainer2>
                    <tags:nameValue2 nameKey=".controlPercent">
                        <cti:msg2 var="percent" key="yukon.common.units.PERCENT"/>
                        <tags:numeric path="fields.controlPercent" units="${percent}" size="10" minValue="5" maxValue="100" />
                    </tags:nameValue2>
                    <tags:nameValue2 nameKey=".criticality">
                        <tags:numeric path="fields.criticality" size="10" minValue="1" maxValue="15" />
                    </tags:nameValue2>
                    <tags:nameValue2 nameKey=".trueCycle">
                        <tags:switchButton path="fields.trueCycle" onNameKey=".yes.label" offNameKey=".no.label" />
                    </tags:nameValue2>
                </tags:nameValueContainer2>
            </tags:sectionContainer2>
            
            <tags:sectionContainer2 nameKey="optionalAttributes">
                <tags:nameValueContainer2>
                    <tags:nameValue2 nameKey=".groupCapacityReduction">
                        <cti:msg2 var="percent" key="yukon.common.units.PERCENT"/>
                        <tags:numeric path="fields.capacityReduction" units="${percent}" size="10" minValue="0" maxValue="100" />
                    </tags:nameValue2>
                    <%@ include file="gearWhenToChange.jsp"%>
                </tags:nameValueContainer2>
            </tags:sectionContainer2>
        </div>

        <div class="column two nogutter">
            <tags:sectionContainer2 nameKey="rampIn">
                <tags:nameValueContainer2>
                    <tags:nameValue2 nameKey=".rampIn">
                        <tags:switchButton path="fields.rampIn" onNameKey=".yes.label" offNameKey=".no.label" />
                    </tags:nameValue2>
                    <tags:nameValue2 nameKey=".rampOut">
                        <tags:switchButton path="fields.rampOut" onNameKey=".yes.label" offNameKey=".no.label" />
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
                            <i:inline key="${programGear.fields.howToStopControl}"/>
                        </cti:displayForPageEditModes>
                    </tags:nameValue2>
                </tags:nameValueContainer2>
            </tags:sectionContainer2>
        </div>
    </div>
</cti:msgScope>