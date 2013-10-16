<%@ tag trimDirectiveWhitespaces="true" %>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<%@ attribute name="controls" %>
<%@ attribute name="escapeTitle" type="java.lang.Boolean" %>
<%@ attribute name="helpText" %>
<%@ attribute name="hideEnabled" type="java.lang.Boolean" %>
<%@ attribute name="hideInitially" type="java.lang.Boolean" %>
<%@ attribute name="id" %>
<%@ attribute name="styleClass" %>
<%@ attribute name="title" %>

<c:if test="${hideEnabled}">
    <c:set var="h3Class" value="toggle-title"/>
    <c:if test="${hideInitially}">
        <c:set var="collapsed" value="collapsed"/>
        <c:set var="hideMe" value="dn"/>
    </c:if>
</c:if>


<cti:uniqueIdentifier prefix="sectionContainer_" var="thisId"/>
<c:if test="${!empty pageScope.id}">
    <c:set var="thisId" value="${id}"/>
</c:if>

<div class="titledContainer sectionContainer clearfix ${collapsed} ${pageScope.styleClass}" <c:if test="${!empty pageScope.id}">id="${id}"</c:if>>
    
    <div class="title-bar clearfix">
        
        <c:choose>
          <c:when test="${pageScope.escapeTitle}">
            <h3 class="title ${h3Class}">${fn:escapeXml(pageScope.title)}</h3>
          </c:when>
          <c:otherwise>
            <h3 class="title ${h3Class}">${pageScope.title}</h3>
          </c:otherwise>
        </c:choose>
        
        <c:if test="${not empty pageScope.helpText}">
            <cti:icon icon="icon-help" id="help_icon_${thisId}"/>
    	</c:if>
    	<div class="controls">${controls}</div>
    </div>
    
    <%-- BODY --%>
    <div id="${thisId}_content" class="content clearfix ${hideMe}">
        <jsp:doBody/>
    </div>

</div>

<c:if test="${not empty pageScope.helpText}">
    <tags:simplePopup id="sectionContainerInfoPopup_${thisId}" title="${pageScope.title}" on="#help_icon_${thisId}" options="{width:600}">${helpText}</tags:simplePopup>	
</c:if>