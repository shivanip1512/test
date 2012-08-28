<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti" %>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>

<%@ attribute name="nameKey" required="true" description="Base i18n key. Available settings: .title (required)"%>
<%@ attribute name="id" required="false" type="java.lang.String"%>
<%@ attribute name="styleClass" required="false" type="java.lang.String"%>
<%@ attribute name="escapeTitle" required="false" type="java.lang.Boolean" %>
<%@ attribute name="hideEnabled" required="false" type="java.lang.Boolean" %>

<cti:msgScope paths=".${nameKey},">
	<cti:msg2 var="title" key=".title"/>
</cti:msgScope>

<c:if test="${hideEnabled}"><c:set var="h3Class" value="toggle-title"/></c:if>

<cti:uniqueIdentifier prefix="formElementContainer_" var="thisId"/>

<div class="titledContainer formElementContainer ${pageScope.styleClass}" <c:if test="${not empty pageScope.id}">id="${pageScope.id}"</c:if>>
    
    <div class="titleBar formElementContainer_titleBar">
        
        <div class="titleBar formElementContainer_title">
        
            <c:choose>
              <c:when test="${pageScope.escapeTitle}">
                <h3 class="${h3Class}"><spring:escapeBody htmlEscape="true">${pageScope.title}</spring:escapeBody></h3>
              </c:when>
              <c:otherwise>
                <h3 class="${h3Class}">${pageScope.title}</h3> 
              </c:otherwise>
            </c:choose>
        	
        </div>
    </div>
    
    <%-- BODY --%>
    <div id="${thisId}_content" class="content formElementContainer formElementContainer_content" style="overflow:visible;">
        <jsp:doBody/>
    </div>    

</div>