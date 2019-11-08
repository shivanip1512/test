<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti"%>

<%@ attribute name="title" required="true" type="java.lang.String"%>
<%@ attribute name="id" required="false" type="java.lang.String"%>
<%@ attribute name="styleClass" required="false" type="java.lang.String"%>
<%@ attribute name="showInitially" required="false" type="java.lang.Boolean"%>
<%@ attribute name="escapeTitle" required="false" type="java.lang.Boolean" %>
<%@ attribute name="titleClass" required="false" type="java.lang.String" %>
<%@ attribute name="style" required="false" type="java.lang.String"%>

<cti:uniqueIdentifier prefix="hideReveal_" var="thisId"/>

<c:if test="${!empty pageScope.showInitially and !pageScope.showInitially}">
    <c:set var="collapsed" value="collapsed"/>
</c:if>

<div class="titled-container hide-reveal-container ${pageScope.styleClass} ${pageScope.collapsed}" style="${pageScope.style}" <c:if test="${!empty pageScope.id}" >id="${pageScope.id}"</c:if>>
    <div class="title-bar clearfix">
        <c:choose>
            <c:when test="${pageScope.escapeTitle}"><h3 class="title toggle-title">${fn:escapeXml(pageScope.title)}</h3></c:when>
            <c:otherwise><h3 id="${thisId}_title" class="title toggle-title ${pageScope.titleClass}">${pageScope.title}</h3></c:otherwise>
        </c:choose>
    </div>
    <div id="${thisId}_content" class="content"><jsp:doBody/></div>
</div>