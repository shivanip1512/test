<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cm" tagdir="/WEB-INF/tags/contextualMenu" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n"%>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags"%>
<%@ taglib prefix="d" tagdir="/WEB-INF/tags/dialog" %>

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