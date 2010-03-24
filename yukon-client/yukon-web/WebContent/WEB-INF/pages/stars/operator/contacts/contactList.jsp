<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n" %>

<cti:standardPage module="operator" page="contactList">

	<script type="text/javascript">
	
		function deleteAdditionalContact(id) {

			$('deleteAdditionalContactId').value = id;
			$('deleteAdditionalContactForm').submit();
		}
		
	</script>
	
	<%-- for lining up notifications --%>
	<style type="text/css">
		table.contactNotificationListTable {
			font-size:11px;
			border-collapse:collapse;
		}
		table.contactNotificationListTable td {
	        padding: 3px;
	    }
		table.contactNotificationListTable td.type {
	        width: 100px;
	    }
	</style>
	
	<cti:url var="add" value="/WebConfig/yukon/Icons/add.gif"/>
	<cti:url var="addOver" value="/WebConfig/yukon/Icons/add_over.gif"/>
	<cti:url var="delete" value="/WebConfig/yukon/Icons/delete.gif"/>
	<cti:url var="deleteOver" value="/WebConfig/yukon/Icons/delete_over.gif"/>
	
	<form id="addContactForm" action="/spring/stars/operator/contacts/contactEdit" method="get">
		<input type="hidden" name="contactId" value="0"/>
		<input type="hidden" name="accountId" value="${accountId}"/>
		<input type="hidden" name="energyCompanyId" value="${energyCompanyId}"/>
	</form>
	
	<form id="deleteAdditionalContactForm" action="/spring/stars/operator/contacts/deleteAdditionalContact" method="post">
		<input type="hidden" name="deleteAdditionalContactId" id="deleteAdditionalContactId"/>
		<input type="hidden" name="accountId" value="${accountId}"/>
		<input type="hidden" name="energyCompanyId" value="${energyCompanyId}"/>
	</form>
	
	<table class="resultsTable">
	
		<tr>
			<th><i:inline key=".header.contact"/></th>
			<th><i:inline key=".header.notifications"/></th>
			<th style="text-align:center;width:100px;"><i:inline key=".header.remove"/></th>
		</tr>
	
		<c:forEach var="contact" items="${contacts}">
		
			<tr style="vertical-align:top;">
			
				<td style="width:20%;white-space:nowrap;">
				
					<cti:url var="contactEditUrl" value="/spring/stars/operator/contacts/contactEdit">
						<cti:param name="accountId" value="${accountId}"/>
						<cti:param name="energyCompanyId" value="${energyCompanyId}"/>
						<cti:param name="contactId" value="${contact.contactId}"/>
					</cti:url>
				
					<a href="${contactEditUrl}">${contact.lastName}, ${contact.firstName}</a>
					
				</td>
				
				<td>
					
					<table class="noStyle contactNotificationListTable">
						<c:if test="${not empty contact.homePhone}">
							<tr>
								<td class="type"><cti:msg2 key="yukon.web.modules.operator.contactNotificationEnum.HOME_PHONE"/>:</td>
								<td>${contact.homePhone}</td>
							</tr>
						</c:if>
						<c:if test="${not empty contact.workPhone}">
							<tr>
								<td class="type"><cti:msg2 key="yukon.web.modules.operator.contactNotificationEnum.WORK_PHONE"/>:</td>
								<td>${contact.workPhone}</td>
							</tr>
						</c:if>
						<c:if test="${not empty contact.email}">
							<tr>
								<td class="type"><cti:msg2 key="yukon.web.modules.operator.contactNotificationEnum.EMAIL"/>:</td>
								<td>${contact.email}</td>
							</tr>
						</c:if>
						<c:forEach var="otherNotification" items="${contact.otherNotifications}">
							<tr>
								<td class="type"><cti:msg2 key="${otherNotification.contactNotificationType.formatKey}"/>:</td>
								<td>${otherNotification.notificationValue}</td>
							</tr>
						</c:forEach>
					</table>
					
					
				</td>
				
				<td style="text-align:center;">
				
					<c:choose>
						<c:when test="${!contact.primary}">
							<img src="${delete}" onclick="deleteAdditionalContact(${contact.contactId});" onmouseover="javascript:this.src='${deleteOver}'" onmouseout="javascript:this.src='${delete}'">
						</c:when>
						<c:otherwise>
							<i:inline key=".isPrimary"/>
						</c:otherwise>
					</c:choose>
					
				</td>
			
			</tr>
			
		</c:forEach>
	
	</table>
	
	<%-- ADD CONTACT --%>
	<br>
	<tags:slowInput2 myFormId="addContactForm" key="button.addContact" width="80px"/>

</cti:standardPage>