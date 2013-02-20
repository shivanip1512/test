<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti"%>

<%@ attribute name="title" required="true" type="java.lang.String"%>
<%@ attribute name="id" required="false" type="java.lang.String"%>
<%@ attribute name="styleClass" required="false" type="java.lang.String"%>
<%@ attribute name="showInitially" required="false" type="java.lang.Boolean"%>
<%@ attribute name="escapeTitle" required="false" type="java.lang.Boolean" %>
<%@ attribute name="slide" required="false" type="java.lang.Boolean" %>

<cti:uniqueIdentifier prefix="hideReveal_" var="thisId"/>

<div class="titledContainer triangleContainer ${pageScope.styleClass}" <c:if test="${!empty pageScope.id}" >id="${pageScope.id}"</c:if>>
    <div class="titleBar triangleContainer_titleBar">
        <div class="title triangleContainer_title">
            <c:choose>
                <c:when test="${pageScope.escapeTitle}"><h3 class="toggle-title">${fn:escapeXml(pageScope.title)}</h3></c:when>
                <c:otherwise><h3 id="${thisId}_title" class="toggle-title">${pageScope.title}</h3></c:otherwise>
            </c:choose>
        </div>
    </div>
    <div id="${thisId}_content" class="content triangleContainer_content"><jsp:doBody/></div>
</div>

<c:if test="${!empty pageScope.showInitially and !pageScope.showInitially}">
    <script type="text/javascript">jQuery(function() {jQuery('#${thisId}_title').trigger('click');});</script>
</c:if>