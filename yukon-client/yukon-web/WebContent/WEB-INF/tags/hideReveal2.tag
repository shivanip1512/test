<%@ tag trimDirectiveWhitespaces="true"%>

<%@ attribute name="id" required="false" type="java.lang.String"%>
<%@ attribute name="escapeTitle" required="false" type="java.lang.Boolean" %>
<%@ attribute name="showInitially" required="false" type="java.lang.Boolean"%>
<%@ attribute name="styleClass" required="false" type="java.lang.String"%>
<%@ attribute name="titleKey" required="true" type="java.lang.Object"%>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<c:set var="theTitle"><i:inline key="${titleKey}"/></c:set>

<tags:hideReveal title="${theTitle}" 
    escapeTitle="${pageScope.escapeTitle}" 
    id="${pageScope.id}" 
    showInitially="${pageScope.showInitially}" 
    styleClass="${pageScope.styleClass}"><jsp:doBody/></tags:hideReveal>