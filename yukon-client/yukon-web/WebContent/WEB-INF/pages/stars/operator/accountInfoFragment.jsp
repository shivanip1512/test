<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti"%>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>

<div class="accountInfoFragment">

	<h2 class="standardPageHeading">${accountInfoFragment.accountNumber}</h2>
	
	<%-- extra account info --%>
	<%-- note: when adding additional parameters the Consumer side should be updated to match. See "CustomerAccountInfoTag.java" --%>
	<cti:msg2 var="extraInfo" 
	          key="yukon.web.modules.operator.accountInfoFragment.extraInfo"
	          argument="${accountInfoFragment.alternateTrackingNumber}"/>
	          
	<c:if test="${not empty extraInfo}">
		${extraInfo}
		<br>
	</c:if>
	
	<c:if test="${not empty accountInfoFragment.companyName}">
		${accountInfoFragment.companyName}
		<br>
	</c:if>
	
	<spring:escapeBody htmlEscape="true" javaScriptEscape="true">${accountInfoFragment.firstName} ${accountInfoFragment.lastName}</spring:escapeBody>
	<br>
	
	<tags:homeAndWorkPhone homePhoneNotif="${accountInfoFragment.homePhoneNotif}" workPhoneNotif="${accountInfoFragment.workPhoneNotif}"/>
	<br>
	
	<tags:address address="${accountInfoFragment.address}"/>
	
</div>