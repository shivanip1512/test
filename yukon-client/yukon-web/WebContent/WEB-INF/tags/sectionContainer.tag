<%@ tag trimDirectiveWhitespaces="true" %>

<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti" %>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib tagdir="/WEB-INF/tags" prefix="tags" %>

<%@ attribute name="title"%>
<%@ attribute name="id"%>
<%@ attribute name="styleClass"%>
<%@ attribute name="helpText"%>
<%@ attribute name="escapeTitle" type="java.lang.Boolean" %>
<%@ attribute name="hideEnabled" type="java.lang.Boolean" %>
<%@ attribute name="hideInitially" type="java.lang.Boolean" %>
<%@ attribute name="controls" %>

<c:if test="${hideEnabled}">
    <c:set var="h3Class" value="toggle-title"/>
</c:if>
<c:if test="${hideEnabled and hideInitially}">
    <c:set var="collapsed" value="collapsed"/>
    <c:set var="hideMe" value="dn"/>
</c:if>

<cti:uniqueIdentifier prefix="sectionContainer_" var="thisId"/>

<div class="titledContainer sectionContainer ${collapsed} ${pageScope.styleClass}" <c:if test="${not empty pageScope.id}">id="${pageScope.id}"</c:if>>
    
    <div class="titleBar sectionContainer_titleBar">
        <div class="titleBar sectionContainer_title">
            
            <c:choose>
              <c:when test="${pageScope.escapeTitle}">
                <h3 class="${h3Class}"><spring:escapeBody htmlEscape="true">${pageScope.title}</spring:escapeBody></h3>
              </c:when>
              <c:otherwise>
                <h3 class="${h3Class}">${pageScope.title}</h3>
              </c:otherwise>
            </c:choose>
            
            <c:if test="${not empty pageScope.helpText}">
                <cti:icon icon="icon-help" id="help_icon_${thisId}"/>
        	</c:if>
        	<div class="controls">${controls}</div>
        </div>
    </div>
    
    <%-- BODY --%>
    <div id="${thisId}_content" class="content sectionContainer sectionContainer_content ${hideMe}">
        <jsp:doBody/>
    </div>    

</div>

<c:if test="${not empty pageScope.helpText}">
    <tags:simplePopup id="sectionContainerInfoPopup_${thisId}" title="${pageScope.title}" on="#help_icon_${thisId}" options="{width:600}">${helpText}</tags:simplePopup>	
</c:if>