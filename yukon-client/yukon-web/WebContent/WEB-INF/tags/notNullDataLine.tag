<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ attribute name="value" required="true" %>
<%@ attribute name="inLine" required="false" description="If true no line break will be added." type="java.lang.Boolean" %>
<%@ attribute name="ignore" required="false" description="Treat this value as null" type="java.lang.String" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<c:if test='${not empty value && value != pageScope.ignore}'>
    <spring:escapeBody htmlEscape="true">${value}</spring:escapeBody>
    <c:if test="${empty pageScope.inLine || not pageScope.inLine}">
        <BR>
    </c:if>
</c:if>