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
    <tags:nameValueContainer2>
        
        <tags:nameValue2 nameKey=".name">
            <tags:input path="name" inputClass="js-focus" tabindex="1" maxlength="60" size="40"/>
        </tags:nameValue2>
        
        <tags:nameValue2 nameKey=".ipaddress">
            <tags:input path="ipAddress" inputClass="js-gateway-edit-ip" maxlength="15" size="15" tabindex="2"/>
        </tags:nameValue2>
        <tags:nameValue2 nameKey=".useDefaultPort">
            <tags:switchButton path="useDefaultPort" onNameKey=".yes.label" offNameKey=".no.label" 
                toggleGroup="virtual-gateway" toggleAction="hide" toggleInverse="true"/>
            <cti:msg2 var="portHelpTextTitle" key=".default.port.title"/>
            <tags:helpInfoPopup title="${portHelpTextTitle}" classes="vam">
                <cti:msg2 key=".default.port.helpText"/>
            </tags:helpInfoPopup>
        </tags:nameValue2>
        <tags:nameValue2 nameKey=".default.port" data-toggle-group="virtual-gateway">
            <tags:input path="port" maxlength="5" size="5" tabindex="3"/>
        </tags:nameValue2>
        <tags:nameValue2 nameKey=".nmipaddressPort" nameColumnWidth="160px">
        	<select name="nmIpAddressPort" class="js-nmipaddressport" tabindex="4">
        		<c:set var="selectedOptionFound" value="false"/>
        		<c:forEach var="nmAddressPort" items="${nmIPAddressPorts}">
        			<c:set var="selected" value=""/>
        			<c:set var="ipAddress" value="${nmAddressPort.nmIpAddress}"/>
        			<c:set var="port" value="${nmAddressPort.nmPort}"/>
        			<c:if test="${ipAddress == settings.nmIpAddress && port == settings.nmPort}">
        				<c:set var="selected" value="selected"/>
        				<c:set var="selectedOptionFound" value="true"/>
        			</c:if>
        			<option ${selected} data-nmipaddress="${ipAddress}" data-nmport="${port}">${ipAddress}:${port}</option>
        		</c:forEach>
        		<c:set var="newSelected" value="${selectedOptionFound ? '' : 'selected'}"/>
        		<option ${newSelected} data-nmipaddress="NEW"><i:inline key="yukon.common.new"/></option>
        	</select>
        	<c:set var="newIPAddressPortClass" value="${selectedOptionFound ? 'dn' : ''}"/>
        	<span class="js-new-nmipaddressport ${newIPAddressPortClass}">
        	    <cti:msg2 var="nmIPAddressText" key=".nmipaddress"/>
        	    <spring:bind path="nmIpAddress">
        	        <c:set var="errorClass" value="${status.error ? 'error' : ''}"/>
        			<form:input path="nmIpAddress" placeholder="${nmIPAddressText}" maxlength="15" size="15" 
        				tabindex="4" cssClass="MR10 js-nmipaddress ${errorClass}"/>
        		</spring:bind>
        	    <cti:msg2 var="nmPortText" key=".nmport"/>
        	    <spring:bind path="nmPort">
        	        <c:set var="errorClass" value="${status.error ? 'error' : ''}"/>
        	    	<form:input path="nmPort" placeholder="${nmPortText}" maxlength="5" size="5" 
        	    		tabindex="5" cssClass="js-nmport ${errorClass}"/>
        	    </spring:bind>
        	</span>
        	<cti:msg2 var="nmIPAddressPortHelpTextTitle" key=".nmipaddressPort"/>
            <tags:helpInfoPopup title="${nmIPAddressPortHelpTextTitle}" classes="vam ML0">
                <cti:msg2 key=".nmipaddressPortHelpText"/>
            </tags:helpInfoPopup>
        	<span class="js-new-nmipaddressport ${newIPAddressPortClass}">
               	<spring:bind path="nmIpAddress">
                    <c:if test="${status.error}"><br><form:errors path="nmIpAddress" cssClass="error" /></c:if>
                </spring:bind>
                <spring:bind path="nmPort">
                    <c:if test="${status.error}"><br><form:errors path="nmPort" cssClass="error" /></c:if>
                </spring:bind>
        	</span>
        </tags:nameValue2>
        <tags:nameValue2 nameKey=".authentication" nameClass="fwb" excludeColon="true" valueClass="js-test-results">
        </tags:nameValue2>
        
        <tags:nameValue2 rowClass="js-gateway-edit-admin" nameKey=".admin">
            <spring:bind path="admin.username">
                <c:set var="clazz" value="${status.error ? 'error' : ''}"/>
                <tags:password path="admin.username" cssClass="js-gateway-edit-username M0 left ${clazz}"
                      showPassword="true" placeholder="${phUsername}" tabindex="6" includeShowHideButton="true"/>
            </spring:bind>
            <spring:bind path="admin.password">
                <c:set var="clazz" value="${status.error ? 'error' : ''}"/>
                <c:set var="clazz" value="${clazz} ${mode == 'EDIT' ? 'middle' : 'right'}"/>
                <tags:password path="admin.password" cssClass="js-gateway-edit-password M0" 
                      placeholder="${phPassword}" tabindex="7" showPassword="true" includeShowHideButton="true"/>
            </spring:bind>
        </tags:nameValue2>
        
        <tags:nameValue2 rowClass="js-gateway-edit-super-admin" nameKey=".superAdmin">
            <spring:bind path="superAdmin.username">
                <c:set var="clazz" value="${status.error ? 'error' : ''}"/>
                <tags:password path="superAdmin.username" cssClass="js-gateway-edit-username M0 left ${clazz}" 
                     placeholder="${phUsername}" tabindex="8" showPassword="true" includeShowHideButton="true"/>
            </spring:bind>
            <spring:bind path="superAdmin.password">
                <c:set var="clazz" value="${status.error ? 'error' : ''}"/>
                <c:set var="clazz" value="${clazz} ${mode == 'EDIT' ? 'middle' : 'right'}"/>
                <div class="dib M0">
                    <tags:password  path="superAdmin.password" cssClass="js-gateway-edit-password M0"
                        placeholder="${phPassword}" tabindex="9" showPassword="true" includeShowHideButton="true"/>
                </div>
            </spring:bind>
        </tags:nameValue2>
        
        <tr><td colspan="2">&nbsp;</td></tr>
        
        
    </tags:nameValueContainer2>

    <div class="section-separator"></div>
    <h4><i:inline key=".updateServer"/></h4>

    <tags:nameValueContainer2>
        <tags:nameValue2 nameKey=".updateServer.default">
            <tags:switchButton path="useDefaultUpdateServer" onNameKey=".yes.label" offNameKey=".no.label" 
                toggleGroup="update-server" toggleAction="hide" toggleInverse="true"/>
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