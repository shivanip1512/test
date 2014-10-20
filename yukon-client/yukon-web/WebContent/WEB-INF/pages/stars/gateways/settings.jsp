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
<form:form action="${url}" method="${method}" commandName="settings">
    <cti:csrfToken/>
    
    <table class="name-value-table with-form-controls">
        
        <tr>
            <td class="name"><i:inline key=".name"/></td>
            <td class="value">
                <tags:input path="name" inputClass="js-focus full-width" tabindex="1"/>
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
                    <form:radiobutton path="admin.defaultUser" tabindex="3"/>
                    <i:inline key="defaults.default"/>
                </label>
            </td>
            <td class="value">
                <input class="M0 left js-gateway-edit-username" type="text" value="" 
                    placeholder="<cti:msg2 key=".username.ph"/>" tabindex="7"
                ><input class="M0 middle js-gateway-edit-password" type="password" value="" 
                    placeholder="<cti:msg2 key=".password.ph"/>" tabindex="8" 
                ><cti:button renderMode="buttonImage" icon="icon-server-connect" classes="fn vat right js-conn-test-btn" 
                    disabled="true" nameKey="testConnection" tabindex="9"/>
                <spring:bind path="admin.username">
                    <c:if test="${status.error}"><form:errors path="${path}" cssClass="error" element="div"/></c:if>
                </spring:bind>
                <spring:bind path="admin.password">
                    <c:if test="${status.error}"><form:errors path="${path}" cssClass="error" element="div"/></c:if>
                </spring:bind>
            </td>
        </tr>
        
        <tr class="js-gateway-edit-super-admin">
            <td class="name">
                <i:inline key=".superAdmin"/>
                <label class="db">
                    <form:radiobutton path="superAdmin.defaultUser" tabindex="3"/>
                    <i:inline key="defaults.default"/>
                </label>
            </td>
            <td class="value">
                <input class="M0 left js-gateway-edit-username" type="text" value="" 
                    placeholder="<cti:msg2 key=".username.ph"/>" tabindex="10"
                ><input class="M0 middle js-gateway-edit-password" type="password" value="" 
                    placeholder="<cti:msg2 key=".password.ph"/>" tabindex="11" 
                ><cti:button renderMode="buttonImage" icon="icon-server-connect" classes="fn vat right js-conn-test-btn" 
                    disabled="true" nameKey="testConnection" tabindex="12"/>
                <spring:bind path="superAdmin.username">
                    <c:if test="${status.error}"><form:errors path="${path}" cssClass="error" element="div"/></c:if>
                </spring:bind>
                <spring:bind path="superAdmin.password">
                    <c:if test="${status.error}"><form:errors path="${path}" cssClass="error" element="div"/></c:if>
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
                <input class="M0 left" type="text" value="" placeholder="<cti:msg2 key=".latitude"/>" tabindex="13"
                    title="<cti:msg2 key=".latitude"/>" maxlength="10" size="10"
                ><input class="M0 right" type="text" value="" placeholder="<cti:msg2 key=".longitude"/>" tabindex="14"
                    title="<cti:msg2 key=".longitude"/>" maxlength="11" size="11">
            </td>
        </tr>
        
    </table>
    
</form:form>

</cti:msgScope>