<%@ tag trimDirectiveWhitespaces="true" body-content="empty"%>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags"%>

<%@ attribute name="address" required="true" type="com.cannontech.common.model.Address"%>
<%@ attribute name="inLine" description="If true no line break will be added." type="java.lang.Boolean" %>

<c:if test="${not empty address}">
    ${pageScope.inLine ? '' : '<address>'}
        <c:if test="${not empty address.locationAddress1}">
            ${fn:escapeXml(address.locationAddress1)}${pageScope.inLine ? '&nbsp;' : '<br>'}</c:if>
        <c:if test="${not empty address.locationAddress2}">
            ${fn:escapeXml(address.locationAddress2)}${pageScope.inLine ? '&nbsp;' : '<br>'}</c:if>
        <c:if test="${not empty address.cityName}">
            ${fn:escapeXml(address.cityName)},&nbsp;</c:if>
        <c:if test="${not empty address.stateCode}">
            ${fn:escapeXml(address.stateCode)}&nbsp;</c:if>
        <c:if test="${not empty address.zipCode}">
            ${fn:escapeXml(address.zipCode)}</c:if>
    ${pageScope.inLine ? '' : '</address>'}
</c:if>