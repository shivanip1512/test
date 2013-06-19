<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti"%>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<div class="info-fragment" style="padding-bottom: 10px;">
	<strong class="stacked dib">${fn:escapeXml(accountInfoFragment.accountNumber)}</strong>
	<%-- extra account info --%>
	<%-- note: when adding additional parameters the Consumer side should be updated to match. See "CustomerAccountInfoTag.java" --%>
	<cti:msg2 var="extraInfo" 
	          key="yukon.web.modules.operator.accountInfoFragment.extraInfo"
	          argument="${accountInfoFragment.alternateTrackingNumber}"/>
	<c:if test="${not empty extraInfo}">
		<div class="stacked">${extraInfo}</div>
	</c:if>
	<c:if test="${not empty accountInfoFragment.companyName}">
		<div class="stacked">${fn:escapeXml(accountInfoFragment.companyName)}</div>
	</c:if>
    <div class="stacked">${fn:escapeXml(accountInfoFragment.firstName)} ${fn:escapeXml(accountInfoFragment.lastName)}</div>
	<c:if test="${not empty pageScope.homePhone || not empty pageScope.workPhone}">
	   <div class="stacked"><tags:homeAndWorkPhone homePhoneNotif="${accountInfoFragment.homePhoneNotif}" workPhoneNotif="${accountInfoFragment.workPhoneNotif}"/></div>
	</c:if>
    <tags:address address="${accountInfoFragment.address}"/>
</div>