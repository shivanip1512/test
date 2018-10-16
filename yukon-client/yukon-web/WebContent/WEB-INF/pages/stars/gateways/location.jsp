<%@ page trimDirectiveWhitespaces="true" %>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<cti:msgScope paths="modules.operator.gateways">

<cti:msg2 var="phUsername" key=".username.ph"/>
<cti:msg2 var="phPassword" key=".password.ph"/>

<c:if test="${not empty errorMsg}"><tags:alertBox>${errorMsg}</tags:alertBox></c:if>

<cti:url var="url" value="/stars/gateways/${location.paoId}/location"/>
<form:form id="gateway-location-form" action="${url}" method="post" modelAttribute="location">
    <cti:csrfToken/>
    <form:hidden path="paoId"/>
    <tags:nameValueContainer2 tableClass="with-form-controls">
        
        <tags:nameValue2 nameKey=".detail.location.title">
            <cti:msg2 var="phLatitude" key=".latitude"/>
            <cti:msg2 var="phLongitude" key=".longitude"/>
            <spring:bind path="latitude">
                <c:set var="clazz" value="${status.error ? 'error' : ''}"/>
                <form:input path="latitude" cssClass="M0 left ${clazz}" maxlength="10" size="10"
                    placeholder="${phLatitude}" tabindex="9" title="${phLatitude}"/>
            </spring:bind>
            <spring:bind path="longitude">
                <c:set var="clazz" value="${status.error ? 'error' : ''}"/>
                <form:input path="longitude" cssClass="M0 right ${clazz}" maxlength="11" size="11"
                    placeholder="${phLongitude}" tabindex="10" title="${phLongitude}"/>
            </spring:bind>
            <spring:bind path="latitude">
                <c:if test="${status.error}"><form:errors path="latitude" cssClass="error" element="div"/></c:if>
            </spring:bind>
            <spring:bind path="longitude">
                <c:if test="${status.error}"><form:errors path="longitude" cssClass="error" element="div"/></c:if>
            </spring:bind>
        </tags:nameValue2>
        
    </tags:nameValueContainer2>
    
</form:form>

</cti:msgScope>