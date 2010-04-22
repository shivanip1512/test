<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti"%>

<%@ attribute name="homePhoneNotif" required="false" type="com.cannontech.database.data.lite.LiteContactNotification"%>
<%@ attribute name="workPhoneNotif" required="false" type="com.cannontech.database.data.lite.LiteContactNotification"%>

<cti:formatNotification var="homePhone" value="${pageScope.homePhoneNotif}" htmlEscape="true" />
<cti:formatNotification var="workPhone" value="${pageScope.workPhoneNotif}" htmlEscape="true" />

<c:if test="${not empty homePhone}">
	<cti:msg var="homePhoneSymbol" key="yukon.web.modules.operator.accountInfoFragment.homePhoneSymbol"/>
	<c:set var="homePhone" value="${pageScope.homePhone} ${homePhoneSymbol}"/>
</c:if>

<c:if test="${not empty workPhone}">
	<cti:msg var="workPhoneSymbol" key="yukon.web.modules.operator.accountInfoFragment.workPhoneSymbol"/>
	<c:set var="workPhone" value="${pageScope.workPhone} ${workPhoneSymbol}"/>
</c:if>

<c:choose>

	<c:when test="${not empty pageScope.homePhone && not empty pageScope.workPhone}">
		${pageScope.homePhone}
		<br>
		${pageScope.workPhone}
	</c:when>
	
	<c:when test="${not empty pageScope.homePhone && empty pageScope.workPhone}">
		${pageScope.homePhone}
	</c:when>

	<c:when test="${empty pageScope.homePhone && not empty pageScope.workPhone}">
		${pageScope.workPhone}
	</c:when>
	
	<c:otherwise>
		<cti:msg2 key="yukon.web.defaults.na"/>
	</c:otherwise>

</c:choose>