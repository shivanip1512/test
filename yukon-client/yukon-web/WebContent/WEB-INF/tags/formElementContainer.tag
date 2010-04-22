<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti" %>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>

<%@ attribute name="nameKey" required="true" description="Base i18n key. Available settings: .title (required)"%>
<%@ attribute name="id" required="false" type="java.lang.String"%>
<%@ attribute name="styleClass" required="false" type="java.lang.String"%>
<%@ attribute name="escapeTitle" required="false" type="java.lang.Boolean" %>

<cti:msgScope paths=".${nameKey},">
	<cti:msg2 var="title" key=".title"/>
</cti:msgScope>

<cti:uniqueIdentifier prefix="formElementContainer_" var="thisId"/>

<div class="titledContainer formElementContainer ${pageScope.styleClass}" <c:if test="${not empty pageScope.id}">id="${pageScope.id}"</c:if>>
    
    <div class="titleBar formElementContainer_titleBar">
        
        <div class="titleBar formElementContainer_title">
        
            <c:choose>
              <c:when test="${pageScope.escapeTitle}">
                <spring:escapeBody htmlEscape="true">${pageScope.title}</spring:escapeBody>
              </c:when>
              <c:otherwise>
                ${pageScope.title} 
              </c:otherwise>
            </c:choose>
        	
        </div>
    </div>
    
    <%-- BODY --%>
    <div id="${thisId}_content" class="content formElementContainer formElementContainer_content">
        <jsp:doBody/>
    </div>    

</div>