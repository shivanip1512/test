<%@ tag body-content="empty" trimDirectiveWhitespaces="true" %>

<%@ taglib prefix="cm" tagdir="/WEB-INF/tags/contextualMenu" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>

<%@ attribute name="deviceId" required="true"%>
<%@ attribute name="menuBeanId" %>
<%@ attribute name="key" %>
<%@ attribute name="containerCssClass" %>

<cti:default var="menuBeanId" value="meterMenuSingleDevice"/>

<cm:ajaxDropdown menuBeanId="${menuBeanId}" key="${pageScope.key}" containerCssClass="${pageScope.containerCssClass}">
    <input type="hidden" name="deviceId" value="${deviceId}"/>
</cm:ajaxDropdown>