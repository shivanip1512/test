<%@ attribute name="titleKey" required="true" type="java.lang.Object"%>
<%@ attribute name="id" required="false" type="java.lang.String"%>
<%@ attribute name="styleClass" required="false" type="java.lang.String"%>
<%@ attribute name="showInitially" required="false" type="java.lang.Boolean"%>
<%@ attribute name="escapeTitle" required="false" type="java.lang.Boolean" %>
<%@ attribute name="slide" required="false" type="java.lang.Boolean" %>
<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib tagdir="/WEB-INF/tags" prefix="tags"%>
<%@ taglib tagdir="/WEB-INF/tags/i18n" prefix="i18n"%>
<c:set var="theTitle"><i18n:inline key="${titleKey}"/></c:set>
<tags:hideReveal title="${theTitle}" escapeTitle="${pageScope.escapeTitle}" id="${pageScope.id}" showInitially="${pageScope.showInitially}" styleClass="${pageScope.styleClass}" slide="${pageScope.slide}"><jsp:doBody/></tags:hideReveal>