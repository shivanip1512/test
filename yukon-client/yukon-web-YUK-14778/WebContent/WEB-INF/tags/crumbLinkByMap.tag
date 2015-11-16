<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti"%>

<%@ attribute name="url" required="true" type="java.lang.String"%>
<%@ attribute name="parameterMap" required="true" type="java.util.Map"%>
<%@ attribute name="titleKey" required="true" type="java.lang.String"%>

<c:url var="linkUrl" value="${url}">
    <c:forEach var="p" items="${parameterMap}">
        <c:param name="${p.key}" value="${p.value}"/>
    </c:forEach>
</c:url>
<cti:msg var="linkTitle" key="${titleKey}"/>
<cti:crumbLink url="${linkUrl}" title="${linkTitle}" />