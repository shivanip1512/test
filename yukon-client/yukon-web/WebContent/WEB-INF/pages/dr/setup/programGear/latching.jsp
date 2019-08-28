<%@ taglib prefix="tags" tagdir="/WEB-INF/tags"%>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti"%>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n"%>
<cti:msgScope paths="yukon.web.modules.dr.setup.gear">
    <tags:sectionContainer2 nameKey="controlParameters">
        <tags:nameValueContainer2>
            <tags:nameValue2 nameKey=".startControlState">
                <cti:displayForPageEditModes modes="EDIT,CREATE">
                    <tags:selectWithItems items="${controlStartState}" path="fields.startControlState"/>
                </cti:displayForPageEditModes>
                <cti:displayForPageEditModes modes="VIEW">
                    <i:inline key="${programGear.fields.startControlState}"/>
                </cti:displayForPageEditModes>
            </tags:nameValue2>
        </tags:nameValueContainer2>
    </tags:sectionContainer2>
    <tags:sectionContainer2 nameKey="optionalAttributes">
        <tags:nameValueContainer2>
            <tags:nameValue2 nameKey=".groupCapacityReduction">
                <cti:msg2 var="percent" key="yukon.common.units.PERCENT"/>
                <tags:numeric path="fields.capacityReduction" units="${percent}" size="10" minValue="0" maxValue="100" stepValue="1"/>
            </tags:nameValue2>
        </tags:nameValueContainer2>
    </tags:sectionContainer2>
</cti:msgScope>