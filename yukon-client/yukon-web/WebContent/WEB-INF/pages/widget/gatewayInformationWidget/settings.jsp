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
        <cti:url var="url" value="/widget/gatewayInformationWidget/edit">
            <cti:param name="deviceId" value="${deviceId}"/>
            <cti:param name="shortName" value="gatewayInformationWidget"/>
        </cti:url>
        <c:set var="method" value="put"/>
    </c:otherwise>
</c:choose>
<form:form id="gateway-settings-form" action="${url}" method="${method}" modelAttribute="settings">
    <cti:csrfToken/>
    <tags:hidden path="id" />
    <tags:nameValueContainer2 tableClass="with-form-controls">
        
        <tags:nameValue2 nameKey=".name">
            <tags:input path="name" inputClass="js-focus full-width" tabindex="1" maxlength="60"/>
        </tags:nameValue2>
        
        <tags:nameValue2 nameKey=".ipaddress">
            <tags:input path="ipAddress" inputClass="js-gateway-edit-ip" maxlength="15" size="15" tabindex="2"/>
        </tags:nameValue2>
        <tags:nameValue2 nameKey=".virtual">
            <tags:switchButton path="virtual" onNameKey=".yes.label" offNameKey=".no.label" 
                toggleGroup="virtual-gateway" toggleAction="invisible"/>
        </tags:nameValue2>
        <tags:nameValue2 nameKey=".virtual.port" data-toggle-group="virtual-gateway">
            <tags:input path="port" maxlength="15" size="15" tabindex="3"/>
        </tags:nameValue2>
        <tags:nameValue2 nameKey=".authentication" nameClass="fwb" excludeColon="true" valueClass="js-test-results">
        </tags:nameValue2>
        
        <tags:nameValue2 rowClass="js-gateway-edit-admin" nameKey=".admin">
            <spring:bind path="admin.username">
                <c:set var="clazz" value="${status.error ? 'error' : ''}"/>
                <tags:password path="admin.username" cssClass="js-gateway-edit-username M0 left ${clazz}"
                      showPassword="true" placeholder="${phUsername}" tabindex="4" includeShowHideButton="true"/>
            </spring:bind>
            <spring:bind path="admin.password">
                <c:set var="clazz" value="${status.error ? 'error' : ''}"/>
                <c:set var="clazz" value="${clazz} ${mode == 'EDIT' ? 'middle' : 'right'}"/>
                <tags:password path="admin.password" cssClass="js-gateway-edit-password M0" 
                      placeholder="${phPassword}" tabindex="5" showPassword="true" includeShowHideButton="true"/>
                <c:if test="${mode == 'EDIT'}">
                    <cti:button renderMode="buttonImage" icon="icon-server-connect" classes="fn vat right js-conn-test-btn admin" 
                         disabled="true" nameKey="testConnection" tabindex="6"/>
                </c:if>
            </spring:bind>
        </tags:nameValue2>
        
        <tags:nameValue2 rowClass="js-gateway-edit-super-admin" nameKey=".superAdmin">
            <spring:bind path="superAdmin.username">
                <c:set var="clazz" value="${status.error ? 'error' : ''}"/>
                <tags:password path="superAdmin.username" cssClass="js-gateway-edit-username M0 left ${clazz}" 
                     placeholder="${phUsername}" tabindex="7" showPassword="true" includeShowHideButton="true"/>
            </spring:bind>
            <spring:bind path="superAdmin.password">
                <c:set var="clazz" value="${status.error ? 'error' : ''}"/>
                <c:set var="clazz" value="${clazz} ${mode == 'EDIT' ? 'middle' : 'right'}"/>
                <div class="dib M0">
                    <tags:password  path="superAdmin.password" cssClass="js-gateway-edit-password M0"
                        placeholder="${phPassword}" tabindex="8" showPassword="true" includeShowHideButton="true"/>
                </div>
                <c:if test="${mode == 'EDIT'}">
                    <cti:button renderMode="buttonImage" icon="icon-server-connect" classes="fn vat right js-conn-test-btn superAdmin" 
                         disabled="true" nameKey="testConnection" tabindex="9"/>
                </c:if>
            </spring:bind>
        </tags:nameValue2>
        
        <tr><td colspan="2">&nbsp;</td></tr>
        
        
    </tags:nameValueContainer2>

    <div class="section-separator"></div>
    <h4><i:inline key=".updateServer"/></h4>

    <tags:nameValueContainer2>
        <tags:nameValue2 nameKey=".updateServer.default">
            <tags:switchButton path="useDefault" onNameKey=".yes.label" offNameKey=".no.label" 
                toggleGroup="update-server" toggleAction="invisible" toggleInverse="true"/>
        </tags:nameValue2>

        <tags:nameValue2 nameKey=".updateServer.url" data-toggle-group="update-server">
            <tags:input path="updateServerUrl" placeholder="${defaultUpdateServer}" size="40"/>
        </tags:nameValue2>
        <tr data-toggle-group="update-server">
            <td class="name">
                <i:inline key=".updateServer.login"/>
            </td>
            <td class="value">
                <spring:bind path="updateServerLogin.username">
                    <c:set var="clazz" value="${status.error ? 'error' : ''}"/>
                    <tags:password path="updateServerLogin.username" cssClass="M0 left ${clazz}"
                        showPassword="true" placeholder="${phUsername}" includeShowHideButton="true"/>
                </spring:bind>
                <spring:bind path="updateServerLogin.password">
                    <c:set var="clazz" value="${status.error ? 'error' : ''}"/>
                    <tags:password path="updateServerLogin.password" cssClass="M0 left "
                        placeholder="${phPassword}" showPassword="true" includeShowHideButton="true"/>
                </spring:bind>
            </td>
        </tr>
    </tags:nameValueContainer2>

</form:form>

</cti:msgScope>

<cti:includeScript link="/resources/js/widgets/yukon.widget.gateway.info.js"/>