<%@ attribute name="address" required="true" type="com.cannontech.common.model.Address"%><%@ attribute name="inLine" required="false" description="If true no line break will be added." type="java.lang.Boolean" %><%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %><%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%><%@ taglib tagdir="/WEB-INF/tags" prefix="tags" %><c:if test="${not empty address}"><c:choose><c:when test="${not empty pageScope.inLine && pageScope.inLine}"><tags:notNullDataLine value="${address.locationAddress1}" inLine="${pageScope.inLine}"/><tags:notNullDataLine value="${address.locationAddress2}" inLine="${pageScope.inLine}"/></c:when><c:otherwise><tags:notNullDataLine value="${address.locationAddress1}"/><tags:notNullDataLine value="${address.locationAddress2}"/></c:otherwise></c:choose><c:if test="${not empty address.cityName}"> ${fn:escapeXml(address.cityName)}, </c:if>${fn:escapeXml(address.stateCode)} ${fn:escapeXml(address.zipCode)}</c:if>