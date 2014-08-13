<%@ tag trimDirectiveWhitespaces="true" %>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<%@ attribute name="controls" %>
<%@ attribute name="escapeTitle" type="java.lang.Boolean" description="If true, html escapes the title text." %>
<%@ attribute name="helpText" description="The text to put inside of a help popup." %>
<%@ attribute name="helpUrl" description="A url used to load a help popup with content before showing." %>
<%@ attribute name="hideEnabled" type="java.lang.Boolean" %>
<%@ attribute name="hideInitially" type="java.lang.Boolean" %>
<%@ attribute name="id" description="The id of the outer container element. If not provided, the id is generated." %>
<%@ attribute name="styleClass" description="CSS class names applied to the outer container element." %>
<%@ attribute name="title" %>

<c:if test="${hideEnabled}">
    <c:set var="h3Class" value="toggle-title"/>
    <c:if test="${hideInitially}">
        <c:set var="collapsed" value="collapsed"/>
        <c:set var="hideMe" value="dn"/>
    </c:if>
</c:if>

<cti:uniqueIdentifier prefix="section-container-" var="thisId"/>
<c:if test="${!empty pageScope.id}">
    <c:set var="thisId" value="${id}"/>
</c:if>

<div class="titled-container section-container clearfix ${collapsed} ${pageScope.styleClass}" <c:if test="${!empty pageScope.id}">id="${id}"</c:if>>
    
    <div class="title-bar clearfix">
        
        <c:choose>
          <c:when test="${pageScope.escapeTitle}">
            <h3 class="title ${h3Class}">${fn:escapeXml(pageScope.title)}</h3>
          </c:when>
          <c:otherwise>
            <h3 class="title ${h3Class}">${pageScope.title}</h3>
          </c:otherwise>
        </c:choose>
        
        <c:if test="${not empty pageScope.helpText or not empty pageScope.helpUrl}">
            <cti:icon icon="icon-help" classes="cp" data-popup="#section-container-info-popup-${thisId}"/>
    	</c:if>
    	<div class="controls">${controls}</div>
    </div>
    
    <%-- BODY --%>
    <div id="${thisId}_content" class="content clearfix ${hideMe}">
        <jsp:doBody/>
    </div>

</div>

<c:if test="${not empty pageScope.helpText or not empty pageScope.helpUrl}">
    <div id="section-container-info-popup-${thisId}"
            class="dn" 
            data-title="${pageScope.title}" 
            <c:if test="${not empty pageScope.helpUrl}">data-url="${helpUrl}"</c:if>
            data-width="600">${helpText}</div>	
</c:if>