<%@ page trimDirectiveWhitespaces="true" %>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cm" tagdir="/WEB-INF/tags/contextualMenu" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<cti:msgScope paths="modules.operator.gateways">

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
<form action="${url}" method="${method}">
    <cti:csrfToken/>
    
    <table class="name-value-table with-form-controls">
        
        <tr>
            <td class="name"><i:inline key=".name"/></td>
            <td class="value">
                <input type="text" value="" class="js-focus full-width" tabindex="1">
            </td>
        </tr>
        
        <tr>
            <td class="name"><i:inline key=".ipaddress"/></td>
            <td class="value">
                <input class="js-gateway-edit-ip" type="text" maxlength="15" size="15" value="" tabindex="2">
            </td>
        </tr>
        
        <tr><td colspan="2"><strong><i:inline key=".credentials"/></strong></td></tr>
        
        <tr class="js-gateway-edit-user">
            <td class="name">
                <i:inline key=".user"/>
                <label class="db">
                    <input type="radio" name="default" tabindex="3">
                    <i:inline key="defaults.default"/>
                </label>
            </td>
            <td class="value">
                <input class="M0 left js-gateway-edit-username" type="text" value="" placeholder="<cti:msg2 key=".username.ph"/>" tabindex="4"
                ><input class="M0 middle js-gateway-edit-password" type="password" value="" placeholder="<cti:msg2 key=".password.ph"/>" tabindex="5" 
                ><cti:button renderMode="buttonImage" icon="icon-server-connect" classes="fn vat right js-conn-test-btn" disabled="true" nameKey="testConnection" tabindex="6"/>
            </td>
        </tr>
        
        <tr class="js-gateway-edit-admin">
            <td class="name">
                <i:inline key=".admin"/>
                <label class="db">
                    <input type="radio" name="default" checked tabindex="3">
                    <i:inline key="defaults.default"/>
                </label>
            </td>
            <td class="value">
                <input class="M0 left js-gateway-edit-username" type="text" value="" placeholder="<cti:msg2 key=".username.ph"/>" tabindex="7"
                ><input class="M0 middle js-gateway-edit-password" type="password" value="" placeholder="<cti:msg2 key=".password.ph"/>" tabindex="8" 
                ><cti:button renderMode="buttonImage" icon="icon-server-connect" classes="fn vat right js-conn-test-btn" disabled="true" nameKey="testConnection" tabindex="9"/>
            </td>
        </tr>
        
        <tr class="js-gateway-edit-super-admin">
            <td class="name">
                <i:inline key=".superAdmin"/>
                <label class="db">
                    <input type="radio" name="default" tabindex="3">
                    <i:inline key="defaults.default"/>
                </label>
            </td>
            <td class="value">
                <input class="M0 left js-gateway-edit-username" type="text" value="" placeholder="<cti:msg2 key=".username.ph"/>" tabindex="10"
                ><input class="M0 middle js-gateway-edit-password" type="password" value="" placeholder="<cti:msg2 key=".password.ph"/>" tabindex="11" 
                ><cti:button renderMode="buttonImage" icon="icon-server-connect" classes="fn vat right js-conn-test-btn" disabled="true" nameKey="testConnection" tabindex="12"/>
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
                ><input class="M0 right" type="text" value="" placeholder="<cti:msg2 key=".longitude"/>" tabindex="14">
            </td>
        </tr>
        
    </table>
    
</form>

</cti:msgScope>