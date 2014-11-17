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

<c:choose>
    <c:when test="${mode == 'CREATE'}">
        <cti:url var="url" value="/stars/gateways"/>
        <c:set var="method" value="post"/>
    </c:when>
    <c:otherwise>
        <cti:url var="url" value="/stars/gateways/${settings.id}"/>
        <c:set var="method" value="put"/>
    </c:otherwise>
</c:choose>
<form:form id="gateway-settings-form" action="${url}" method="${method}" commandName="settings">
    <cti:csrfToken/>
    
    <tags:nameValueContainer2 tableClass="with-form-controls">
        
        <tags:nameValue2 nameKey=".name">
            <tags:input path="name" inputClass="js-focus full-width" tabindex="1" maxlength="60"/>
        </tags:nameValue2>
        
        <tags:nameValue2 nameKey=".ipaddress">
            <tags:input path="ipAddress" inputClass="js-gateway-edit-ip" maxlength="15" size="15" tabindex="2"/>
        </tags:nameValue2>
        
        <tags:nameValue2 nameKey=".authentication" nameClass="fwb" excludeColon="true" valueClass="js-test-results">
        </tags:nameValue2>
        
        <tags:nameValue2 rowClass="js-gateway-edit-admin" nameKey=".admin">
            <spring:bind path="admin.username">
                <c:set var="clazz" value="${status.error ? 'error' : ''}"/>
                <form:input path="admin.username" cssClass="js-gateway-edit-username M0 left ${clazz}" 
                    placeholder="${phUsername}" tabindex="3"/>
            </spring:bind>
            <spring:bind path="admin.password">
                <c:set var="clazz" value="${status.error ? 'error' : ''}"/>
                <c:set var="clazz" value="${clazz} ${mode == 'EDIT' ? 'middle' : 'right'}"/>
                <form:password path="admin.password" cssClass="js-gateway-edit-password M0 ${clazz}" 
                    placeholder="${phPassword}" tabindex="4" showPassword="true"/>
                <c:if test="${mode == 'EDIT'}">
                    <cti:button renderMode="buttonImage" icon="icon-server-connect" classes="fn vat right js-conn-test-btn" 
                         disabled="true" nameKey="testConnection" tabindex="5"/>
                </c:if>
            </spring:bind>
            
            <spring:bind path="admin.username">
                <c:if test="${status.error}"><form:errors path="admin.username" cssClass="error" element="div"/></c:if>
            </spring:bind>
            <spring:bind path="admin.password">
                <c:if test="${status.error}"><form:errors path="admin.password" cssClass="error" element="div"/></c:if>
            </spring:bind>
        </tags:nameValue2>
        
        <tags:nameValue2 rowClass="js-gateway-edit-super-admin" nameKey=".superAdmin">
            <spring:bind path="superAdmin.username">
                <c:set var="clazz" value="${status.error ? 'error' : ''}"/>
                <form:input path="superAdmin.username" cssClass="js-gateway-edit-username M0 left ${clazz}" 
                    placeholder="${phUsername}" tabindex="6"/>
            </spring:bind>
            <spring:bind path="superAdmin.password">
                <c:set var="clazz" value="${status.error ? 'error' : ''}"/>
                <c:set var="clazz" value="${clazz} ${mode == 'EDIT' ? 'middle' : 'right'}"/>
                <form:password  path="superAdmin.password" cssClass="js-gateway-edit-password M0 ${clazz}" 
                    placeholder="${phPassword}" tabindex="7" showPassword="true"/>
                <c:if test="${mode == 'EDIT'}">
                    <cti:button renderMode="buttonImage" icon="icon-server-connect" classes="fn vat right js-conn-test-btn" 
                         disabled="true" nameKey="testConnection" tabindex="8"/>
                </c:if>
            </spring:bind>
            <spring:bind path="superAdmin.username">
                <c:if test="${status.error}"><form:errors path="superAdmin.username" cssClass="error" element="div"/></c:if>
            </spring:bind>
            <spring:bind path="superAdmin.password">
                <c:if test="${status.error}"><form:errors path="superAdmin.password" cssClass="error" element="div"/></c:if>
            </spring:bind>
        </tags:nameValue2>
        
        <tr><td colspan="2">&nbsp;</td></tr>
        
        <tr>
            <td class="name">
                <i:inline key=".detail.location.title"/>&nbsp;
                <em class="subtle"><i:inline key=".optional"/></em>
            </td>
            <td class="value">
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
            </td>
        </tr>
        
    </tags:nameValueContainer2>
    
</form:form>

</cti:msgScope>