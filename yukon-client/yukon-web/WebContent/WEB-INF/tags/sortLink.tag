<%@ attribute name="fieldName" required="true" %>
<%@ attribute name="baseUrl" required="true" %>
<%@ attribute name="key" required="true" %>
<%@ attribute name="sortParam" %>
<%@ attribute name="descendingParam" %>
<%@ tag body-content="empty" %>

<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti"%>

<c:if test="${empty pageScope.sortParam}">
    <c:set var="sortParam" value="sort" scope="page"/>
</c:if>
<c:if test="${empty pageScope.descendingParam}">
    <c:set var="descendingParam" value="descending" scope="page"/>
</c:if>

<c:set var="currentSort" value="${param[sortParam]}"/>
<c:if test="${empty currentSort}">
    <c:set var="currentSort" value="NAME"/>
</c:if>
<cti:url var="sortUrl" value="${baseUrl}">
    <%-- keep all parameters except sort and page number --%>
    <c:forEach var="aParam" items="${param}">
        <c:if test="${aParam.key != sortParam && aParam.key != descendingParam && aParam.key != 'page'}">
            <cti:param name="${aParam.key}" value="${aParam.value}"/>
        </c:if>
    </c:forEach>
    <cti:param name="${sortParam}" value="${fieldName}"/>
    <c:if test="${currentSort == fieldName && !param[descendingParam]}">
        <cti:param name="${descendingParam}" value="true"/>
    </c:if>
</cti:url>

<a href="${sortUrl}">
    <cti:msg key="${key}"/>
    <c:if test="${currentSort == fieldName}">
        <c:choose>
            <c:when test="${!param[descendingParam]}">
                <span title="Sorted ascending">&#9650;</span>
            </c:when>
            <c:otherwise>
                <span title="Sorted descending">&#9660;</span>
            </c:otherwise>
        </c:choose>
    </c:if>
</a>
