<%@ attribute name="pageNumber" required="true" type="java.lang.Integer" %>
<%@ attribute name="currentPage" required="true" type="java.lang.Integer" %>
<%@ attribute name="baseUrl" required="true" %>
<%@ tag body-content="empty" %>

<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti"%>

<cti:url var="pageUrl" value="${baseUrl}">
    <%-- keep all parameters except page number --%>
    <c:forEach var="aParam" items="${param}">
        <c:if test="${aParam.key != 'page'}">
            <cti:param name="${aParam.key}" value="${aParam.value}"/>
        </c:if>
    </c:forEach>
    <cti:param name="page" value="${pageNumber}"/>
</cti:url>

<c:set var="doIcons" value="true"/>
<c:if test="${doIcons}">
<c:if test="${currentPage < pageNumber}">
    <a href="${pageUrl}"><cti:msg key="yukon.common.paging.next"/><cti:logo key="yukon.common.paging.nextIcon"/></a>
</c:if>
<c:if test="${currentPage > pageNumber}">
    <a href="${pageUrl}"><cti:logo key="yukon.common.paging.previousIcon"/><cti:msg key="yukon.common.paging.previous"/></a>
</c:if>
</c:if>
<c:if test="${!doIcons}">
<c:if test="${currentPage < pageNumber}">
    <a href="${pageUrl}"><cti:msg key="yukon.common.paging.next"/></a>
</c:if>
<c:if test="${currentPage > pageNumber}">
    <a href="${pageUrl}"><cti:msg key="yukon.common.paging.previous"/></a>
</c:if>
</c:if>
