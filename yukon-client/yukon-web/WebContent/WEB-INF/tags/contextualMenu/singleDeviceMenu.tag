<%@ taglib prefix="cm" tagdir="/WEB-INF/tags/contextualMenu"%>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti"%>

<%@ tag trimDirectiveWhitespaces="true"%>
<%@ tag body-content="empty" %>

<%@ attribute name="deviceId" required="true"%>
<%@ attribute name="menuBeanId" %>
<%@ attribute name="key" %>

<cti:default var="menuBeanId" value="meterMenuSingleDevice"/>

<cm:deviceDropdownActionsAjax menuBeanId="${menuBeanId}" key="${pageScope.key}">
	<input type="hidden" name="deviceId" value="${deviceId}"/>
</cm:deviceDropdownActionsAjax>