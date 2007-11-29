<%@ attribute name="type" required="true" type="java.lang.String"%>
<%@ attribute name="title" required="false" type="java.lang.String"%>
<%@ attribute name="id" required="false" type="java.lang.String"%>
<%@ attribute name="styleClass" required="false" type="java.lang.String"%>
<%@ attribute name="hideEnabled" required="false" type="java.lang.Boolean"%>
<%@ attribute name="showInitially" required="false" type="java.lang.Boolean"%>

<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="ct" tagdir="/WEB-INF/tags" %>

<c:choose>
<c:when test="${type == 'box'}">
<ct:boxContainer hideEnabled="${hideEnabled}" id="${id}" showInitially="${showInitially}" styleClass="${styleClass}" title="${title}"><jsp:doBody/></ct:boxContainer>
</c:when>
<c:when test="${type == 'rounded'}">
<ct:roundedContainer id="${id}" styleClass="${styleClass}" title="${title}"><jsp:doBody/></ct:roundedContainer>
</c:when>
<c:when test="${type == 'triangle'}">
<ct:hideReveal id="${id}" styleClass="${styleClass}" showInitially="${showInitially}" title="${title}"><jsp:doBody/></ct:hideReveal>
</c:when>
<c:otherwise>
${title}<br>
<jsp:doBody/>
</c:otherwise>
</c:choose>