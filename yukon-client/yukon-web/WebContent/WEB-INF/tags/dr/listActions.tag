<%@ attribute name="pao" required="true" type="com.cannontech.common.pao.YukonPao" %>
<%@ tag body-content="empty" %>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="dr" tagdir="/WEB-INF/tags/dr" %>

<c:set var="paoId" value="${pao.paoIdentifier.paoId}"/>
<c:choose>
    <c:when test="${pao.paoIdentifier.paoType == 'LM_SCENARIO'}">
        <dr:scenarioListActions pao="${pao}"/>
    </c:when>
    <c:when test="${pao.paoIdentifier.paoType == 'LM_CONTROL_AREA'}">
        <dr:controlAreaListActions pao="${pao}"/>
    </c:when>
    <c:when test="${pao.paoIdentifier.paoType == 'LM_DIRECT_PROGRAM'}">
        <dr:programListActions pao="${pao}"/>
    </c:when>
    <c:when test="${pao.paoIdentifier.paoType == 'LM_SEP_PROGRAM'}">
        <dr:programListActions pao="${pao}"/>
    </c:when>
    <c:otherwise>
        <dr:loadGroupListActions pao="${pao}"/>
    </c:otherwise>
</c:choose>
