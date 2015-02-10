<%@ taglib prefix="cm" tagdir="/WEB-INF/tags/contextualMenu"%>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti"%>

<%@ tag trimDirectiveWhitespaces="true"%>
<%@ tag body-content="empty" %>

<%@ attribute name="deviceCollection" required="true" type="com.cannontech.common.bulk.collection.device.model.DeviceCollection" %>
<%@ attribute name="menuBeanId" %>
<%@ attribute name="key" %>
<%@ attribute name="menuClasses" %>
<%@ attribute name="triggerClasses" %>

<cti:default var="menuBeanId" value="meterMenuDeviceCollection"/>

<cm:ajaxDropdown menuBeanId="${menuBeanId}" key="${pageScope.key}" triggerClasses="${pageScope.triggerClasses}" menuClasses="${pageScope.menuClasses}">
    <cti:deviceCollection deviceCollection="${pageScope.deviceCollection}"/>
</cm:ajaxDropdown>