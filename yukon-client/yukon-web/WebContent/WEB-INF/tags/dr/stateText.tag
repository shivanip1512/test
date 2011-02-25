<%@ attribute name="pao" required="true" type="com.cannontech.common.pao.YukonPao" %>
<%@ tag body-content="empty" %>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="dr" tagdir="/WEB-INF/tags/dr" %>

<c:set var="paoId" value="${pao.paoIdentifier.paoId}"/>
<c:choose>
    <c:when test="${pao.paoIdentifier.paoType == 'LM_SCENARIO'}">
    </c:when>
    <c:when test="${pao.paoIdentifier.paoType == 'LM_CONTROL_AREA'}">
        <dr:controlAreaState controlAreaId="${paoId}"/>
    </c:when>
    <c:when test="${pao.paoIdentifier.paoType == 'LM_DIRECT_PROGRAM'}">
        <dr:programState programId="${paoId}"/>
    </c:when>
    <c:when test="${pao.paoIdentifier.paoType == 'LM_SEP_PROGRAM'}">
        <dr:programState programId="${paoId}"/>
    </c:when>
    <c:otherwise>
        <dr:loadGroupState loadGroupId="${paoId}"/>
    </c:otherwise>
</c:choose>
