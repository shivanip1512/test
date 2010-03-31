<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti"%>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n" %>

<div class="accountInfoFragment">

	<h2 class="standardPageHeading">${accountInfoFragment.accountNumber}</h2>
	
	<cti:checkRolesAndProperties value="OPERATOR_CONSUMER_SHOW_ALTTRACKING_IN_HEADER">
		${accountInfoFragment.alternateTrackingNumber}
		<br>
	</cti:checkRolesAndProperties>
	<c:if test="${not empty accountInfoFragment.companyName}">
		${accountInfoFragment.companyName}
		<br>
	</c:if>
	
	${accountInfoFragment.firstName} ${accountInfoFragment.lastName}
	<br>
	
	<tags:homeAndWorkPhone homePhoneNotif="${accountInfoFragment.homePhoneNotif}" workPhoneNotif="${accountInfoFragment.workPhoneNotif}"/>
	<br>
	
	<tags:address address="${accountInfoFragment.address}"/>
	
</div>