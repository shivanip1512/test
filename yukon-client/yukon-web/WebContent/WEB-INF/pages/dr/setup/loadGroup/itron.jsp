<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti"%>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags"%>

<tags:nameValueContainer2>
    <tags:nameValue2 nameKey=".virtualRelayId">
        <cti:displayForPageEditModes modes="EDIT,CREATE">
            <tags:selectWithItems items="${relayIds}" path="virtualRelayId"/>
        </cti:displayForPageEditModes>
        <cti:displayForPageEditModes modes="VIEW">
            <i:inline key="${loadGroup.virtualRelayId}"/>
        </cti:displayForPageEditModes>
    </tags:nameValue2>
</tags:nameValueContainer2>