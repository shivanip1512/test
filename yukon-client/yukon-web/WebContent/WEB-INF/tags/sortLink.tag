<%@ attribute name="fieldName" required="true" %>
<%@ attribute name="baseUrl" required="true" %>
<%@ attribute name="nameKey" required="true" %>
<%@ attribute name="sortParam" %>
<%@ attribute name="descendingParam" %>
<%@ attribute name="isDefault" type="java.lang.Boolean" %>
<%@ tag body-content="empty" %>

<%--
If the default sort field is anything other than "NAME", set the isDefault
attribute to true on the field which is the default sort field.
--%>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti"%>

<cti:msgScope paths=".${nameKey}">
    <cti:msg2 var="linkTextMsg" key=".linkText"/>
</cti:msgScope>
<cti:msgScope paths=".${nameKey},components.sortLink">
    <cti:msg2 var="ascendingMsg" key=".ascending"/>
    <cti:msg2 var="descendingMsg" key=".descending"/>
</cti:msgScope>

<c:if test="${empty pageScope.sortParam}">
    <c:set var="sortParam" value="sort" scope="page"/>
</c:if>
<c:if test="${empty pageScope.descendingParam}">
    <c:set var="descendingParam" value="descending" scope="page"/>
</c:if>

<c:set var="currentSort" value="${param[sortParam]}"/>
<c:if test="${empty currentSort}">
    <c:if test="${pageScope.isDefault}">
        <c:set var="currentSort" value="${fieldName}"/>
    </c:if>
    <c:if test="${pageScope.isDefault == null}">
        <c:set var="currentSort" value="NAME"/>
    </c:if>
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
    ${linkTextMsg}
    <c:if test="${currentSort == fieldName}">
        <c:choose>
            <c:when test="${!param[descendingParam]}">
                <span title="${ascendingMsg}">&#9650;</span>
            </c:when>
            <c:otherwise>
                <span title="${descendingMsg}">&#9660;</span>
            </c:otherwise>
        </c:choose>
    </c:if>
</a>
