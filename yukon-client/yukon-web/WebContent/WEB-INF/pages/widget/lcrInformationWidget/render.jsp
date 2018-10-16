<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="dt" tagdir="/WEB-INF/tags/dateTime" %>

<cti:msgScope paths="modules.operator.hardware">

<tags:setFormEditMode mode="${mode}"/>

<tags:nameValueContainer2>

    <form:form modelAttribute="hardware">
    
    <tags:nameValue2 nameKey=".serialNumber">
        <cti:url var="inventoryUrl" value="/stars/operator/inventory/view">
            <cti:param name="inventoryId" value="${hardware.inventoryId}"/>
        </cti:url>
        <c:if test="${hardware.accountId > 0}">
            <cti:url var="inventoryUrl" value="/stars/operator/hardware/view">
                <cti:param name="inventoryId" value="${hardware.inventoryId}"/>
                <cti:param name="accountId" value="${hardware.accountId}"/>
            </cti:url>
        </c:if>
        <a href="${inventoryUrl}">${fn:escapeXml(hardware.serialNumber)}</a>
    </tags:nameValue2>
    
    <c:if test="${hardware.accountId > 0}">
        <cti:url var="accountUrl" value="/stars/operator/account/view">
            <cti:param name="accountId" value="${hardware.accountId}"/>
        </cti:url>
        <tags:nameValue2 nameKey=".accountNumber">
            <a href="${accountUrl}">${fn:escapeXml(accountDto.accountNumber)}</a>
        </tags:nameValue2>
    </c:if>
    
    <c:if test="${hardware.deviceId > 0}">
        <tags:nameValue2 nameKey=".displayName">
            <cti:deviceName deviceId="${hardware.deviceId}"/>
        </tags:nameValue2>
    </c:if>
    
    <tags:nameValue2 nameKey="${displayTypeKey}">
        ${fn:escapeXml(hardware.displayType)}
    </tags:nameValue2>
    
    <c:if test="${!empty route}">
        <tags:nameValue2 nameKey=".route">
            ${fn:escapeXml(route.paoName)}
        </tags:nameValue2>
    </c:if>
    <c:if test="${isRf}">
        <tags:nameValue2 nameKey=".serviceStatus">
            <cti:pointValue pointId="${serviceStatusPointId}" format="VALUE"/>
        </tags:nameValue2>
    </c:if>
  

    


</form:form>

</tags:nameValueContainer2>

</cti:msgScope>