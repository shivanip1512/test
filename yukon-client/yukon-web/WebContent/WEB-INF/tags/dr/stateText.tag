<%@ attribute name="pao" required="true" type="com.cannontech.common.pao.YukonPao" %>
<%@ tag body-content="empty" %>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>

<c:choose>
    <c:when test="${pao.paoIdentifier.paoType == 'LM_SCENARIO'}">
    </c:when>
    <c:when test="${pao.paoIdentifier.paoType == 'LM_CONTROL_AREA'}">
        <cti:dataUpdaterValue type="DR_CONTROLAREA" identifier="${pao.paoIdentifier.paoId}/STATE"/>
    </c:when>
    <c:when test="${pao.paoIdentifier.paoType == 'LM_DIRECT_PROGRAM'}">
        <cti:dataUpdaterValue type="DR_PROGRAM" identifier="${pao.paoIdentifier.paoId}/STATE"/>
    </c:when>
    <c:otherwise>
        <cti:dataUpdaterValue type="DR_LOADGROUP" identifier="${pao.paoIdentifier.paoId}/STATE"/>
    </c:otherwise>
</c:choose>
