<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<cti:msgScope paths="yukon.web.modules.dr.setup.gear">
    <div class="column-12-12 clearfix">
        <div class="column one">
            <tags:sectionContainer2 nameKey="controlParameters" styleClass="js-hw-temperature-ctrl-prms">
                <tags:nameValueContainer2>
                    <tags:nameValue2 nameKey=".mandatory">
                        <tags:switchButton path="fields.mandatory" onNameKey=".yes" offNameKey=".no" />
                    </tags:nameValue2>
                    <tags:nameValue2 nameKey=".mode">
                        <tags:radioButtonGroup items="${temperatureModes}" path="fields.mode" viewModeKey="${programGear.fields.mode}"/>
                    </tags:nameValue2>
                    <tags:nameValue2 nameKey=".setpointOffset">
                        <cti:msg2 var="fahrenheit" key="yukon.common.fahrenheit"/>
                        <tags:numeric path="fields.setpointOffset" units="${fahrenheit}" size="3" minValue="-10" maxValue="10" />
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
                        <tags:numeric path="fields.capacityReduction" units="${percent}" size="10" minValue="0" maxValue="100" />
                    </tags:nameValue2>
                    <c:if test="${not empty selectedGearType}">
                        <%@ include file="gearWhenToChange.jsp" %>
                    </c:if>
                </tags:nameValueContainer2>
            </tags:sectionContainer2>
        </div>
    </div>
</cti:msgScope>
