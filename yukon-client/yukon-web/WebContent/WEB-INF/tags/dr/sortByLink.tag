<%@ attribute name="fieldName" required="true" type="java.lang.String" %>
<%@ attribute name="baseUrl" required="true" type="java.lang.String" %>
<%@ attribute name="key" required="true" type="java.lang.String" %>
<%@ tag body-content="empty" %>

<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti"%>

<c:set var="currentSort" value="${param.sort}"/>
<c:if test="${empty currentSort}">
    <c:set var="currentSort" value="NAME"/>
</c:if>
<cti:url var="sortUrl" value="${baseUrl}">
    <%-- keep all parameters except sort and page number --%>
    <c:forEach var="aParam" items="${param}">
        <c:if test="${aParam.key != 'sort' && aParam.key != 'descending' && aParam.key != 'page'}">
            <cti:param name="${aParam.key}" value="${aParam.value}"/>
        </c:if>
    </c:forEach>
    <cti:param name="sort" value="${fieldName}"/>
    <c:if test="${currentSort == fieldName && !param.descending}">
        <cti:param name="descending" value="true"/>
    </c:if>
</cti:url>

<a href="${sortUrl}"><cti:msg key="${key}"/></a>
<c:if test="${currentSort == fieldName}">
    <c:choose>
        <c:when test="${!param.descending}">
            <span title="Sorted ascending">&#9650;</span>
        </c:when>
        <c:otherwise>
            <span title="Sorted descending">&#9660;</span>
        </c:otherwise>
    </c:choose>
</c:if>
