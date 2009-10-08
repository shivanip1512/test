<%@ attribute name="type" required="true" type="java.lang.String"%>
<%@ attribute name="title" required="false" type="java.lang.String"%>
<%@ attribute name="id" required="false" type="java.lang.String"%>
<%@ attribute name="styleClass" required="false" type="java.lang.String"%>
<%@ attribute name="hideEnabled" required="false" type="java.lang.Boolean"%>
<%@ attribute name="showInitially" required="false" type="java.lang.Boolean"%>
<%@ attribute name="helpText" required="false" type="java.lang.String"%>

<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="ct" tagdir="/WEB-INF/tags" %>

<c:choose>
<c:when test="${type == 'box'}">
<ct:boxContainer hideEnabled="${pageScope.hideEnabled}" id="${pageScope.id}" showInitially="${pageScope.showInitially}" styleClass="${pageScope.styleClass}" title="${pageScope.title}" helpText="${pageScope.helpText}"><jsp:doBody/></ct:boxContainer>
</c:when>
<c:when test="${type == 'rounded'}">
<ct:roundedContainer id="${pageScope.id}" styleClass="${pageScope.styleClass}" title="${pageScope.title}"><jsp:doBody/></ct:roundedContainer>
</c:when>
<c:when test="${type == 'triangle'}">
<ct:hideReveal id="${pageScope.id}" styleClass="${pageScope.styleClass}" showInitially="${pageScope.showInitially}" title="${pageScope.title}"><jsp:doBody/></ct:hideReveal>
</c:when>
<c:when test="${type == 'section'}">
<ct:sectionContainer id="${pageScope.id}" title="${pageScope.title}" styleClass="${pageScope.styleClass}" helpText="${pageScope.helpText}"><jsp:doBody/></ct:sectionContainer>
</c:when>
<c:otherwise>
${pageScope.title}<br>
<jsp:doBody/>
</c:otherwise>
</c:choose>