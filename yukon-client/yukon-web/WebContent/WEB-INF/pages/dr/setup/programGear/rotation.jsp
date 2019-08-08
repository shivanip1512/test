<%@ taglib prefix="tags" tagdir="/WEB-INF/tags"%>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti"%>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<cti:msgScope paths="yukon.web.modules.dr.setup.gear">
    <div class="column-12-12 clearfix">
        <div class="column one">
            <tags:sectionContainer2 nameKey="controlParameters">
                <tags:nameValueContainer2>
                    <tags:nameValue2 nameKey=".shedTime">
                        <tags:intervalDropdown path="fields.shedTime" intervals="${shedTime}" />
                    </tags:nameValue2>
                    <tags:nameValue2 nameKey=".groupEachTime">
                        <cti:displayForPageEditModes modes="EDIT,CREATE">
                            <cti:msg2 key=".allOfThem" var="selectLbl"/>
                            <tags:selectWithItems items="${groupOptions}" path="fields.numberOfGroups" defaultItemLabel="${selectLbl}" defaultItemValue="0"/>
                        </cti:displayForPageEditModes>
                        <cti:displayForPageEditModes modes="VIEW">
                        <c:choose>
                            <c:when test="${programGear.fields.numberOfGroups == 0}">
                                <i:inline key=".allOfThem"/>
                            </c:when>
                            <c:otherwise>
                                <i:inline key="${programGear.fields.numberOfGroups}"/>
                            </c:otherwise>
                        </c:choose>
                        </cti:displayForPageEditModes>
                    </tags:nameValue2>
                    <tags:nameValue2 nameKey=".commandResendRate">
                        <tags:intervalDropdown path="fields.sendRate" intervals="${commandResendRate}" />
                    </tags:nameValue2>
                    <tags:nameValue2 nameKey=".groupSelectionMethod">
                        <cti:displayForPageEditModes modes="EDIT,CREATE">
                            <tags:selectWithItems items="${groupSelectionMethodOptions}" path="fields.groupSelectionMethod" />
                        </cti:displayForPageEditModes>
                        <cti:displayForPageEditModes modes="VIEW">
                            <i:inline key="${programGear.fields.groupSelectionMethod}"/>
                        </cti:displayForPageEditModes>
                    </tags:nameValue2>
                </tags:nameValueContainer2>
            </tags:sectionContainer2>
            <tags:sectionContainer2 nameKey="optionalAttributes">
                <tags:nameValueContainer2>
                    <tags:nameValue2 nameKey=".groupCapacityReduction">
                        <tags:numeric path="fields.capacityReduction" size="10" minValue="0" maxValue="100" stepValue="1"/>
                    </tags:nameValue2>
                    <%@ include file="gearWhenToChange.jsp" %>
                </tags:nameValueContainer2>
            </tags:sectionContainer2>
        </div>
        <div class="column two nogutter">
            <tags:sectionContainer2 nameKey="stopControl">
                <tags:nameValueContainer2>
                    <tags:nameValue2 nameKey=".stopControl">
                        <cti:displayForPageEditModes modes="EDIT,CREATE">
                            <tags:selectWithItems items="${stopControlOptions}" path="fields.howToStopControl" />
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