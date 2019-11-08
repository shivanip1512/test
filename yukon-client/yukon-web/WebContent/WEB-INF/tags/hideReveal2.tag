<%@ tag trimDirectiveWhitespaces="true"%>

<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<%@ attribute name="id" %>
<%@ attribute name="escapeTitle" type="java.lang.Boolean" %>
<%@ attribute name="showInitially" type="java.lang.Boolean" %>
<%@ attribute name="styleClass" %>
<%@ attribute name="style" required="false" type="java.lang.String" %>
<%@ attribute name="titleKey" required="true" type="java.lang.Object" %>
<%@ attribute name="titleClass" required="false" type="java.lang.String" %>

<cti:msg2 var="theTitle" key="${titleKey}"/>
<tags:hideReveal title="${theTitle}" 
    escapeTitle="${pageScope.escapeTitle}" 
    id="${pageScope.id}" 
    showInitially="${pageScope.showInitially}" 
    styleClass="${pageScope.styleClass}"
    titleClass="${pageScope.titleClass}" style="${pageScope.style}"><jsp:doBody/></tags:hideReveal>