<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti"%>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<div class="accountInfoFragment">

	<h2 class="standardPageHeading">${fn:escapeXml(accountInfoFragment.accountNumber)}</h2>
	
	<%-- extra account info --%>
	<%-- note: when adding additional parameters the Consumer side should be updated to match. See "CustomerAccountInfoTag.java" --%>
	<cti:msg2 var="extraInfo" 
	          key="yukon.web.modules.operator.accountInfoFragment.extraInfo"
	          argument="${accountInfoFragment.alternateTrackingNumber}"/>
	          
	<c:if test="${not empty extraInfo}">
		${fn:escapeXml(extraInfo}
		<br>
	</c:if>
	
	<c:if test="${not empty accountInfoFragment.companyName}">
		${fn:escapeXml(accountInfoFragment.companyName)}
		<br>
	</c:if>
	
	${fn:escapeXml(accountInfoFragment.firstName)} ${fn:escapeXml(accountInfoFragment.lastName)}
	<br>
	
	<tags:homeAndWorkPhone homePhoneNotif="${accountInfoFragment.homePhoneNotif}" workPhoneNotif="${accountInfoFragment.workPhoneNotif}"/>
	<br>
	
	<tags:address address="${accountInfoFragment.address}"/>
	
</div>