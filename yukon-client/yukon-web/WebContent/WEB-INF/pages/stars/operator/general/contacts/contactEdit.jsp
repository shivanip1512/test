<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="op" tagdir="/WEB-INF/tags/operator" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n" %>

<cti:standardPage module="operator" page="contactEdit">

	<script type="text/javascript">
	
		function removeNotification(id) {

			$('removeNotificationId').value = id;
			$('contactsRemoveNotificationForm').submit();
		}
		
	</script>
	
	<cti:includeScript link="/JavaScript/simpleDialog.js"/>
    <tags:simpleDialog id="addNotificationDialog"/>

	<cti:url var="add" value="/WebConfig/yukon/Icons/add.gif"/>
	<cti:url var="addOver" value="/WebConfig/yukon/Icons/add_over.gif"/>
	<cti:url var="delete" value="/WebConfig/yukon/Icons/delete.gif"/>
	<cti:url var="deleteOver" value="/WebConfig/yukon/Icons/delete_over.gif"/>
	
	<form id="contactListForm" action="/spring/stars/operator/general/contacts/contactList" method="get">
		<input type="hidden" name="accountId" value="${accountId}"/>
		<input type="hidden" name="energyCompanyId" value="${energyCompanyId}"/>
	</form>
	
	<form id="newNotficationForm" action="/spring/stars/operator/general/contacts/contactEdit" method="post">
		<input type="hidden" name="accountId" value="${accountId}"/>
		<input type="hidden" name="energyCompanyId" value="${energyCompanyId}"/>
		<input type="hidden" name="contactId" value="${contact.contactId}"/>
		<input type="hidden" name="newNotifs" value="1"/>
	</form>
	
	<form id="contactsRemoveNotificationForm" action="/spring/stars/operator/general/contacts/contactsRemoveNotification" method="post">
		<input type="hidden" name="accountId" value="${accountId}"/>
		<input type="hidden" name="energyCompanyId" value="${energyCompanyId}"/>
		<input type="hidden" name="contactId" value="${contact.contactId}"/>
		<input type="hidden" id="removeNotificationId" name="removeNotificationId" value=""/>
	</form>

	<form id="contactsUpdateForm" action="/spring/stars/operator/general/contacts/updateContact" method="post">
	
		<input type="hidden" name="accountId" value="${accountId}"/>
		<input type="hidden" name="energyCompanyId" value="${energyCompanyId}"/>
		<c:if test="${not empty contact}">
			<input type="hidden" name="contactId" value="${contact.contactId}">
		</c:if>
		
		<c:set var="contactInformationSectionTitleKey" value=".contactInformationSection"/>
		<c:if test="${contact.primary}">
			<c:set var="contactInformationSectionTitleKey" value=".contactInformationSection_isPrimary"/>
		</c:if>
		
		<i:sectionContainer titleKey="${contactInformationSectionTitleKey}">
		
			<tags:nameValueContainer>
			
				<i:nameValue nameKey=".firstNameLabel" nameColumnWidth="110px">
					<input type="text" name="firstName" value="${contact.firstName}">
				</i:nameValue>
				
				<i:nameValue nameKey=".lastNameLabel">
					<input type="text" name="lastName" value="${contact.lastName}">
				</i:nameValue>
				
				<i:nameValue nameKey="yukon.web.modules.operator.contactNotificationEnum.HOME_PHONE">
					<cti:formatNotification var="firstHomePhoneNotificationValue" value="${contact.firstHomePhoneNotification.notification}"/>
					<input type="text" name="homePhone" value="${firstHomePhoneNotificationValue}">
				</i:nameValue>
				
				<i:nameValue nameKey="yukon.web.modules.operator.contactNotificationEnum.WORK_PHONE">
					<cti:formatNotification var="firstWorkPhoneNotificationValue" value="${contact.firstWorkPhoneNotification.notification}"/>
					<input type="text" name="workPhone" value="${firstWorkPhoneNotificationValue}">
				</i:nameValue>

				<i:nameValue nameKey="yukon.web.modules.operator.contactNotificationEnum.EMAIL">
					<cti:formatNotification var="firstEmailNotificationValue" value="${contact.firstEmailNotification.notification}"/>
					<input type="text" name="email" value="${firstEmailNotificationValue}">
				</i:nameValue>
			
			</tags:nameValueContainer>
			
			<%-- NOTIFICATIONS TABLE --%>
			<br>
			<table class="resultsTable">
			
				<tr>
					<th style="width:160px;"><i:inline key=".notificationTable.notificationMethodHeader"/></th>
					<th><i:inline key=".notificationTable.valueHeader"/></th>
					<c:if test="${not empty contact}">
						<th style="text-align:center;width:100px;"><i:inline key=".notificationTable.removeHeader"/></th>
					</c:if>
				</tr>
			
				<%-- EXISTING NOTIFICATIONS --%>
				<c:forEach var="otherContactNotification" items="${contact.otherContactNotifications}" varStatus="status">
				
					<tr>
					
						<%--NOTIFICATION TYPE --%>
						<td>
							<select name="notificationType${otherContactNotification.notificationId}">
								<option value="">(none)</option>
								<c:forEach var="notificationType" items="${notificationTypes}">
									<option value="${notificationType}" <c:if test="${otherContactNotification.operatorContactNotificationType == notificationType}">selected</c:if>>
										<i:inline key="${notificationType.formatKey}"/>
									</option>
								</c:forEach>
							</select>
						</td>
						
						<%--NOTIFICATION VALUE --%>
						<td>
							<cti:formatNotification var="notificationValue" value="${otherContactNotification.notification}"/>
							<input type="text" name="notificationValue${otherContactNotification.notificationId}" value="${notificationValue}">
						</td>
						
						<%--REMOVE NOTIFICATION --%>
						<td style="text-align:center;">
							<img src="${delete}" onclick="removeNotification(${otherContactNotification.notificationId});" onmouseover="javascript:this.src='${deleteOver}'" onmouseout="javascript:this.src='${delete}'">
						</td>
					
					</tr>
				
				</c:forEach>
				
				<%-- NEW CONTACT NOTIFICATIONS --%>
				<c:if test="${empty contact}">
				
					<c:forEach var="newNotifCount" begin="1" end="4">
					
						<tr>
					
							<%--NOTIFICATION TYPE --%>
							<td>
								<select name="notificationType${newNotifCount}">
									<option value="">(none)</option>
									<c:forEach var="notificationType" items="${notificationTypes}">
										<option value="${notificationType}">
											<i:inline key="${notificationType.formatKey}"/>
										</option>
									</c:forEach>
								</select>
							</td>
							
							<%--NOTIFICATION VALUE --%>
							<td>
								<input type="text" name="notificationValue${newNotifCount}">
							</td>
						
						</tr>
					
					</c:forEach>
				
				</c:if>
			
				<%-- ADD NOTIFICATION --%>
				<c:if test="${not empty contact}">
					<tr style="background-color:#EEE;">
						<td colspan="3">
						
							<%--
							<img src="${add}" onclick="$('newNotficationForm').submit();" onmouseover="javascript:this.src='${addOver}'" onmouseout="javascript:this.src='${add}'">
							<i:inline key=".notificationTable.addNotification"/>
							--%>
							
							<cti:msg2 var="addNotificationText" key=".notificationTable.addNotification"/>
							<img src="${add}" onclick="openSimpleDialog('addNotificationDialog', '/spring/stars/operator/general/contacts/addNotificationDialog', '${addNotificationText}', {'accountId':${accountId},'energyCompanyId':${energyCompanyId},'contactId':${contact.contactId}});" onmouseover="javascript:this.src='${addOver}'" onmouseout="javascript:this.src='${add}'">
							${addNotificationText}
							 
						</td>
					</tr>
				</c:if>
				
			</table>
			
		</i:sectionContainer>
		
		<%-- BUTTONS --%>
		<br>
		<i:slowInput myFormId="contactListForm" labelKey=".viewAll" width="80px"/>
		<i:slowInput myFormId="contactsUpdateForm" labelKey="defaults.save" width="80px"/>

	</form>
	
</cti:standardPage>