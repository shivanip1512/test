<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti"%>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags"%>
<tags:sectionContainer2 nameKey="optional">
    <tags:nameValueContainer2>
        <c:if test="${selectedSwitchType == 'LM_GROUP_EXPRESSCOMM' ||
              selectedSwitchType == 'LM_GROUP_RFN_EXPRESSCOMM'}">
            <tags:nameValue2 nameKey=".controlPriority">
                <cti:displayForPageEditModes modes="CREATE,EDIT">
                    <tags:selectWithItems items="${protocolPriority}" path="protocolPriority"/>
                </cti:displayForPageEditModes>
                <cti:displayForPageEditModes modes="VIEW">
                    <i:inline key="${loadGroup.protocolPriority}"/>
                 </cti:displayForPageEditModes>
            </tags:nameValue2>
        </c:if>
        <tags:nameValue2 nameKey=".kWCapacity">
            <tags:input path="kWCapacity"/>
        </tags:nameValue2>
        <tags:nameValue2 nameKey=".disableGroup">
            <tags:switchButton path="disableGroup" offNameKey="yukon.common.no" onNameKey="yukon.common.yes" />
        </tags:nameValue2>
        <tags:nameValue2 nameKey=".disableControl">
            <tags:switchButton path="disableControl" offNameKey="yukon.common.no" onNameKey="yukon.common.yes" />
        </tags:nameValue2>
    </tags:nameValueContainer2>
</tags:sectionContainer2>