<%@ tag body-content="empty" trimDirectiveWhitespaces="true" %>

<%@ attribute name="path" required="true" %>
<%@ attribute name="onclick" %>
<%@ attribute name="id" %>
<%@ attribute name="styleClass" %>
<%@ attribute name="descriptionNameKey" %>
<%@ attribute name="disabled" %>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>

<spring:bind path="${path}">
    
    <c:if test="${not empty descriptionNameKey}"><label></c:if>
    
    <%-- VIEW MODE --%>
    <cti:displayForPageEditModes modes="VIEW">
        <c:choose>
            <c:when test="${status.value == true && not empty pageScope.id}">
                <input type="checkbox" checked="checked" disabled="disabled" id="${pageScope.id}" class="${styleClass}">
            </c:when>
            <c:when test="${status.value == true && empty pageScope.id}">
                <input type="checkbox" checked="checked" disabled="disabled" class="${styleClass}">
            </c:when>
            <c:when test="${status.value == false && not empty pageScope.id}">
                <input type="checkbox" disabled="disabled" id="${pageScope.id}" class="${styleClass}">
            </c:when>
            <c:when test="${status.value == false && empty pageScope.id}">
                <input type="checkbox" disabled="disabled" class="${styleClass}">
            </c:when>
            <c:otherwise>
                <%-- BAD STATE!? --%>
            </c:otherwise>
        </c:choose>
    </cti:displayForPageEditModes>
    
    <%-- EDIT/CREATE MODE --%>
    <cti:displayForPageEditModes modes="EDIT,CREATE">
        <c:set var="disabledVal" value="false"/>
        <c:if test="${not empty pageScope.disabled && fn:toLowerCase(pageScope.disabled) eq 'true'}">
            <c:set var="disabledVal" value="true"/>
        </c:if>
        
        <c:choose>
            <c:when test="${not empty pageScope.onclick && not empty pageScope.id}">
                <form:checkbox disabled="${disabledVal}" path="${path}" onclick="${pageScope.onclick}" id="${pageScope.id}" cssClass="${styleClass}"/>
            </c:when>
            <c:when test="${not empty pageScope.onclick && empty pageScope.id}">
                <form:checkbox disabled="${disabledVal}" path="${path}" onclick="${pageScope.onclick}" cssClass="${styleClass}"/>
            </c:when>
            <c:when test="${empty pageScope.onclick && not empty pageScope.id}">
                <form:checkbox disabled="${disabledVal}" path="${path}" id="${pageScope.id}" cssClass="${styleClass}"/>
            </c:when>
            <c:when test="${empty pageScope.onclick && empty pageScope.id}">
                <form:checkbox disabled="${disabledVal}" path="${path}" cssClass="${styleClass}"/>
            </c:when>
            <c:otherwise>
                <%-- BAD STATE!? --%>
            </c:otherwise>
        </c:choose>
    </cti:displayForPageEditModes>
    
    <c:if test="${not empty descriptionNameKey}">
        <c:choose>
            <c:when test="${not empty pageScope.id}">
                <label for="${pageScope.id}"><i:inline key="${pageScope.descriptionNameKey}"/></label>
            </c:when>
            <c:otherwise>
                <i:inline key="${pageScope.descriptionNameKey}"/>
            </c:otherwise>
        </c:choose>
        </label>
    </c:if>

</spring:bind>