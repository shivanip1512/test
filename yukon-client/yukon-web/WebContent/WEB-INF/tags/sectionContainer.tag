<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti" %>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib tagdir="/WEB-INF/tags" prefix="tags" %>

<%@ attribute name="title" required="false" type="java.lang.String"%>
<%@ attribute name="id" required="false" type="java.lang.String"%>
<%@ attribute name="styleClass" required="false" type="java.lang.String"%>
<%@ attribute name="helpText" required="false" type="java.lang.String"%>
<%@ attribute name="escapeTitle" required="false" type="java.lang.Boolean" %>
<%@ attribute name="hideEnabled" required="false" type="java.lang.Boolean" %>
<%@ attribute name="hideInitially" required="false" type="java.lang.Boolean" %>

<c:url var="help" value="/WebConfig/yukon/Icons/help.gif"/>
<c:url var="helpOver" value="/WebConfig/yukon/Icons/help_over.gif"/>

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
            	<a href="javascript:void(0);" onclick="$('sectionContainerInfoPopup_${thisId}').toggle();">
            	<img src="${help}" onmouseover="javascript:this.src='${helpOver}'" onmouseout="javascript:this.src='${help}'">
            	</a>
        	</c:if>
        	
        </div>
    </div>
    
    <%-- BODY --%>
    <div id="${thisId}_content" class="content sectionContainer sectionContainer_content ${hideMe}">
        <jsp:doBody/>
    </div>    

</div>

<c:if test="${not empty pageScope.helpText}">
	<tags:simplePopup id="sectionContainerInfoPopup_${thisId}" title="${pageScope.title}">
     	${pageScope.helpText}
	</tags:simplePopup>	
</c:if>