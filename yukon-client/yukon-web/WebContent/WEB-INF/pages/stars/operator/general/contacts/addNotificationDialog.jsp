<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n" %>

<form id="addNotificationForm" action="/spring/stars/operator/general/contacts/addNotification" method="post">

	<input type="hidden" name="accountId" value="${accountId}">
	<input type="hidden" name="energyCompanyId" value="${energyCompanyId}">
	<input type="hidden" name="contactId" value="${contactId}">

	<select name="notificationType">
		<c:forEach var="notificationType" items="${notificationTypes}">
			<option value="${notificationType}">
				<cti:msg key="${notificationType.formatKey}"/>
			</option>
		</c:forEach>
	</select>
	
	<input type="text" name="notificationValue">
		
	<br><br>
	<cti:msg var="addButtonText" key="yukon.web.defaults.add"/>
	<tags:slowInput myFormId="addNotificationForm" label="${addButtonText}"/>
	
</form>