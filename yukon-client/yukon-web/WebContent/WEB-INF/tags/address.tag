<%@ tag trimDirectiveWhitespaces="true" body-content="empty"%>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags"%>

<%@ attribute name="address" required="true" type="com.cannontech.common.model.Address"%>
<%@ attribute name="inLine" description="If true no line break will be added." type="java.lang.Boolean" %>

<c:if test="${not empty address}">
    <address>
		<c:choose>
			<c:when test="${not empty pageScope.inLine && pageScope.inLine}">
				<tags:notNullDataLine value="${address.locationAddress1}" inLine="${pageScope.inLine}"/>
				<tags:notNullDataLine value="${address.locationAddress2}" inLine="${pageScope.inLine}"/>
			</c:when>
			<c:otherwise>
				<tags:notNullDataLine value="${address.locationAddress1}"/>
				<tags:notNullDataLine value="${address.locationAddress2}"/>
			</c:otherwise>
		</c:choose>
		<c:if test="${not empty address.cityName}">${fn:escapeXml(address.cityName)},&nbsp;</c:if>
		${fn:escapeXml(address.stateCode)}&nbsp;${fn:escapeXml(address.zipCode)}
    </address>
</c:if>