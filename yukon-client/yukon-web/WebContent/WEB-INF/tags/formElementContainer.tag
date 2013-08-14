<%@ tag trimDirectiveWhitespaces="true" %>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<%@ attribute name="nameKey" required="true" description="Base i18n key. Available settings: .title (required)"%>
<%@ attribute name="id" %>
<%@ attribute name="styleClass" %>
<%@ attribute name="escapeTitle" type="java.lang.Boolean" %>
<%@ attribute name="hideEnabled" type="java.lang.Boolean" %>

<cti:msgScope paths=".${nameKey},">
	<cti:msg2 var="title" key=".title"/>
</cti:msgScope>

<c:if test="${hideEnabled}"><c:set var="h3Class" value="toggle-title"/></c:if>

<cti:uniqueIdentifier prefix="formElementContainer_" var="thisId"/>

<div class="titledContainer formElementContainer ${pageScope.styleClass}" <c:if test="${not empty pageScope.id}">id="${pageScope.id}"</c:if>>
    <div class="title-bar">
        <c:choose>
            <c:when test="${pageScope.escapeTitle}">
                <h3 class="title ${h3Class}">${fn:escapeXml(pageScope.title)}</h3>
            </c:when>
            <c:otherwise>
                <h3 class="title ${h3Class}">${pageScope.title}</h3> 
            </c:otherwise>
        </c:choose>
    </div>
    
    <%-- BODY --%>
    <div id="${thisId}_content" class="content">
        <jsp:doBody/>
    </div>
</div>