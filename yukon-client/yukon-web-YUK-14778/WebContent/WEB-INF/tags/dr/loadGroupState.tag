<%@ attribute name="loadGroupId" required="true" type="java.lang.Integer" %>
<%@ tag body-content="empty" %>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<cti:classUpdater type="DR_LOADGROUP" identifier="${loadGroupId}/STATE_CLASSNAME">
    <cti:classUpdater type="DR_LOADGROUP" identifier="${loadGroupId}/ENABLED_CLASSNAME">
        <cti:dataUpdaterValue type="DR_LOADGROUP" identifier="${loadGroupId}/ENABLED"/>
        <cti:dataUpdaterValue type="DR_LOADGROUP" identifier="${loadGroupId}/STATE"/>
    </cti:classUpdater>
</cti:classUpdater>
