<%@ taglib prefix="cm" tagdir="/WEB-INF/tags/contextualMenu"%>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti"%>

<%@ tag trimDirectiveWhitespaces="true"%>
<%@ tag body-content="empty" %>

<%@ attribute name="deviceCollection" required="true" type="com.cannontech.common.bulk.collection.device.DeviceCollection" %>
<%@ attribute name="menuBeanId" %>
<%@ attribute name="key" %>

<cti:default var="menuBeanId" value="meterMenuDeviceCollection"/>

<cm:deviceDropdownActionsAjax menuBeanId="${menuBeanId}" key="${pageScope.key}">
    <cti:deviceCollection deviceCollection="${pageScope.deviceCollection}"/>
</cm:deviceDropdownActionsAjax>