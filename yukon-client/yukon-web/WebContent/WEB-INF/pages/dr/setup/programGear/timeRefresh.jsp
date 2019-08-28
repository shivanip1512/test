<%@ taglib prefix="tags" tagdir="/WEB-INF/tags"%>

<cti:msgScope paths="yukon.web.modules.dr.setup.gear">
    <div class="column-12-12 clearfix">
        <div class="column one">
            <tags:sectionContainer2 nameKey="controlParameters">
                <tags:nameValueContainer2>
                    <tags:nameValue2 nameKey=".refreshShedType">
                        <cti:displayForPageEditModes modes="EDIT,CREATE">
                            <cti:msg2 key="yukon.web.components.button.select.label" var="selectLbl" />
                            <tags:selectWithItems id="refreshShedType" items="${refreshShedType}" path="fields.refreshShedTime"/>
                        </cti:displayForPageEditModes>
                        <cti:displayForPageEditModes modes="VIEW">
                            <input type=hidden id="refreshShedType" value="${programGear.fields.refreshShedTime}">
                               <i:inline key="${programGear.fields.refreshShedTime}" />
                        </cti:displayForPageEditModes>
                    </tags:nameValue2>
                    <tags:nameValue2 id="js-fixedShedTime-row" nameKey=".fixedShedTime" rowClass="dn">
                        <tags:intervalDropdown path="fields.shedTime" intervals="${shedTime}" />
                    </tags:nameValue2>
                    <tags:nameValue2 id="js-maxShedTime-row" nameKey=".maxShedTime" rowClass="dn">
                        <tags:intervalDropdown path="fields.shedTime" intervals="${shedTime}" />
                    </tags:nameValue2>
                    <tags:nameValue2 nameKey=".commandResendRate">
                        <tags:intervalDropdown path="fields.sendRate" intervals="${commandResendRate}" />
                    </tags:nameValue2>
                    <tags:nameValue2 nameKey=".numberOfGroups">
                        <cti:displayForPageEditModes modes="EDIT,CREATE">
                            <cti:msg2 key=".allOfThem" var="selectLbl"/>
                            <tags:selectWithItems items="${numberOfGroups}" path="fields.numberOfGroups" defaultItemLabel="${selectLbl}" defaultItemValue="0" />
                        </cti:displayForPageEditModes>
                        <cti:displayForPageEditModes modes="VIEW">
                            <c:choose>
                                <c:when test="${programGear.fields.numberOfGroups == 0}">
                                    <i:inline key=".allOfThem" />
                                </c:when>
                                <c:otherwise>
                                    <i:inline key="${programGear.fields.numberOfGroups}" />
                                </c:otherwise>
                            </c:choose>
                        </cti:displayForPageEditModes>
                    </tags:nameValue2>
                    <tags:nameValue2 nameKey=".groupSelectionMethod">
                        <cti:displayForPageEditModes modes="EDIT,CREATE">
                            <tags:selectWithItems items="${groupSelectionMethod}" path="fields.groupSelectionMethod" />
                        </cti:displayForPageEditModes>
                        <cti:displayForPageEditModes modes="VIEW">
                            <i:inline key="${programGear.fields.groupSelectionMethod}" />
                        </cti:displayForPageEditModes>
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
                <c:set var="rampInFieldEnabled"
                    value="${programGear.fields.rampInPercent > 0 || programGear.fields.rampInIntervalInSeconds > 0}" />
                <tags:nameValueContainer2>
                    <tags:nameValue2 nameKey=".rampIn">
                        <tags:switchButton name="rampIn" toggleGroup="rampInWindow" toggleAction="hide" onNameKey=".yes.label" offNameKey=".no.label"
                            checked="${rampInFieldEnabled}" />
                    </tags:nameValue2>
                    <c:set var="rampInFieldClass" value="${rampInFieldEnabled ? '' : 'dn'}" />
                    <tags:nameValue2 nameKey=".rampInPercent" data-toggle-group="rampInWindow" rowClass="${rampInFieldClass}">
                        <cti:msg2 var="percent" key="yukon.common.units.PERCENT"/>
                        <tags:numeric path="fields.rampInPercent" units="${percent}" size="10" minValue="0" maxValue="100"/>
                    </tags:nameValue2>
                    <tags:nameValue2 nameKey=".rampInInterval" data-toggle-group="rampInWindow" rowClass="${rampInFieldClass}">
                        <cti:msg2 var="rampInIntervalUnit" key="yukon.web.modules.dr.setup.gear.sec"/>
                        <tags:numeric path="fields.rampInIntervalInSeconds" units="${rampInIntervalUnit}" size="10" minValue="-99999" maxValue="99999"/>
                    </tags:nameValue2>
                </tags:nameValueContainer2>
            </tags:sectionContainer2>

            <tags:sectionContainer2 nameKey="stopControl">
                <tags:nameValueContainer2>
                    <tags:nameValue2 nameKey=".howToStopControl">
                        <cti:displayForPageEditModes modes="EDIT,CREATE">
                            <tags:selectWithItems id="howToStopControl" items="${howToStopControl}"
                                                  path="fields.howToStopControl" inputClass="js-how-to-stop-control"/>
                        </cti:displayForPageEditModes>
                        <cti:displayForPageEditModes modes="VIEW">
                            <input type=hidden id="howToStopControl" value="${programGear.fields.howToStopControl}">
                                <i:inline key="${programGear.fields.howToStopControl}"/>
                        </cti:displayForPageEditModes>
                    </tags:nameValue2>
                     <tags:nameValue2 id="js-stopOrder-row" nameKey=".stopOrder" rowClass="dn">
                        <cti:displayForPageEditModes modes="EDIT,CREATE">
                            <tags:selectWithItems items="${stopOrder}" path="fields.stopOrder" />
                        </cti:displayForPageEditModes>
                        <cti:displayForPageEditModes modes="VIEW">
                            <i:inline key="${programGear.fields.stopOrder}"/>
                        </cti:displayForPageEditModes>
                    </tags:nameValue2>
                    <tags:nameValue2 id="js-rampOutPercent-row" nameKey=".rampOutPercent" rowClass="dn">
                        <cti:msg2 var="percent" key="yukon.common.units.PERCENT"/>
                        <tags:numeric path="fields.rampOutPercent" units="${percent}" size="10" minValue="0" maxValue="100"/>
                    </tags:nameValue2>
                    <tags:nameValue2 id="js-rampOutInterval-row" nameKey=".rampOutInterval" rowClass="dn">
                        <cti:msg2 var="rampOutIntervalUnit" key="yukon.web.modules.dr.setup.gear.sec"/>
                        <tags:numeric path="fields.rampOutIntervalInSeconds" units="${rampOutIntervalUnit}" size="10" minValue="-99999" maxValue="99999"/>
                    </tags:nameValue2>
                    <tags:nameValue2 nameKey=".stopCommandRepeat">
                        <tags:numeric path="fields.stopCommandRepeat" size="10" minValue="0" maxValue="5"/>
                    </tags:nameValue2>
                </tags:nameValueContainer2>
            </tags:sectionContainer2>

        </div>
    </div>
 </cti:msgScope>