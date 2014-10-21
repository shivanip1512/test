<%@ page trimDirectiveWhitespaces="true" %>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cm" tagdir="/WEB-INF/tags/contextualMenu" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
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
        <cti:url var="url" value="/stars/gateways/${gateway.paoIdentifier.paoId}"/>
        <c:set var="method" value="put"/>
    </c:otherwise>
</c:choose>
<form:form id="gateway-settings-form" action="${url}" method="${method}" commandName="settings">
    <cti:csrfToken/>
    
    <table class="name-value-table with-form-controls">
        
        <tr>
            <td class="name"><i:inline key=".name"/></td>
            <td class="value">
                <tags:input path="name" inputClass="js-focus full-width" tabindex="1" maxlength="60"/>
            </td>
        </tr>
        
        <tr>
            <td class="name"><i:inline key=".ipaddress"/></td>
            <td class="value">
                <tags:input path="ipAddress" inputClass="js-gateway-edit-ip" maxlength="15" size="15" tabindex="2"/>
            </td>
        </tr>
        
        <tr><td colspan="2"><strong><i:inline key=".credentials"/></strong></td></tr>
        
        <tr class="js-gateway-edit-admin">
            <td class="name">
                <i:inline key=".admin"/>
                <label class="db">
                    <form:radiobutton path="adminDefault" tabindex="3" value="true"/>
                    <i:inline key="defaults.default"/>
                </label>
            </td>
            <td class="value">
                <spring:bind path="adminUsername">
                    <c:set var="clazz" value="${status.error ? 'error' : ''}"/>
                    <form:input path="adminUsername" cssClass="M0 left js-gateway-edit-username ${clazz}" 
                        placeholder="${phUsername}" tabindex="4"/>
                </spring:bind>
                <spring:bind path="adminPassword">
                    <c:set var="clazz" value="${status.error ? 'error' : ''}"/>
                    <form:input path="adminPassword" cssClass="M0 middle js-gateway-edit-password ${clazz}" 
                        placeholder="${phPassword}" tabindex="5"/>
                    <cti:button renderMode="buttonImage" icon="icon-server-connect" classes="fn vat right js-conn-test-btn" 
                        disabled="true" nameKey="testConnection" tabindex="6"/>
                </spring:bind>
                
                <spring:bind path="adminUsername">
                    <c:if test="${status.error}"><form:errors path="adminUsername" cssClass="error" element="div"/></c:if>
                </spring:bind>
                <spring:bind path="adminPassword">
                    <c:if test="${status.error}"><form:errors path="adminPassword" cssClass="error" element="div"/></c:if>
                </spring:bind>
            </td>
        </tr>
        
        <tr class="js-gateway-edit-super-admin">
            <td class="name">
                <i:inline key=".superAdmin"/>
                <label class="db">
                    <form:radiobutton path="adminDefault" tabindex="3" value="false"/>
                    <i:inline key="defaults.default"/>
                </label>
            </td>
            <td class="value">
                <spring:bind path="superAdminUsername">
                    <c:set var="clazz" value="${status.error ? 'error' : ''}"/>
                    <form:input path="superAdminUsername" cssClass="M0 left js-gateway-edit-username ${clazz}" 
                        placeholder="${phUsername}" tabindex="7"/>
                </spring:bind>
                <spring:bind path="superAdminPassword">
                    <c:set var="clazz" value="${status.error ? 'error' : ''}"/>
                    <form:input path="superAdminPassword" cssClass="M0 middle js-gateway-edit-password ${clazz}" 
                        placeholder="${phPassword}" tabindex="8"/>
                    <cti:button renderMode="buttonImage" icon="icon-server-connect" classes="fn vat right js-conn-test-btn" 
                        disabled="true" nameKey="testConnection" tabindex="9"/>
                </spring:bind>
                <spring:bind path="superAdminUsername">
                    <c:if test="${status.error}"><form:errors path="superAdminUsername" cssClass="error" element="div"/></c:if>
                </spring:bind>
                <spring:bind path="superAdminPassword">
                    <c:if test="${status.error}"><form:errors path="superAdminPassword" cssClass="error" element="div"/></c:if>
                </spring:bind>
            </td>
        </tr>
        
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
                        placeholder="${phLatitude}" tabindex="13" title="${phLatitude}"/>
                </spring:bind>
                <spring:bind path="longitude">
                    <c:set var="clazz" value="${status.error ? 'error' : ''}"/>
                    <form:input path="longitude" cssClass="M0 right ${clazz}" maxlength="11" size="11"
                        placeholder="${phLongitude}" tabindex="14" title="${phLongitude}"/>
                </spring:bind>
                <spring:bind path="latitude">
                    <c:if test="${status.error}"><form:errors path="latitude" cssClass="error" element="div"/></c:if>
                </spring:bind>
                <spring:bind path="longitude">
                    <c:if test="${status.error}"><form:errors path="longitude" cssClass="error" element="div"/></c:if>
                </spring:bind>
            </td>
        </tr>
        
    </table>
    
</form:form>

</cti:msgScope>