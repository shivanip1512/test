<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring" %>

<cti:standardPage module="operator" page="contact.${mode}">
<tags:setFormEditMode mode="${mode}"/>

	<script type="text/javascript">
		Event.observe(window, 'load', function() {
			var additionalNotificationTrs = $$('tr.additionalNotificationTr');
			additionalNotificationTrs.each(function(el) {
				new Effect.Highlight(el, {'duration': 0.5, 'startcolor': '#FFFF00'});
			});
		});
	
		function removeNotification(it) {

			$(it).ancestors().find(function(el) {return el.match('tr')}).remove();
			$('contactsUpdateForm').submit();
		}
		
	</script>
	
	<cti:url var="add" value="/WebConfig/yukon/Icons/add.gif"/>
	<cti:url var="addOver" value="/WebConfig/yukon/Icons/add_over.gif"/>
	<cti:url var="delete" value="/WebConfig/yukon/Icons/delete.gif"/>
	<cti:url var="deleteOver" value="/WebConfig/yukon/Icons/delete_over.gif"/>
	
	<form id="contactListForm" action="/spring/stars/operator/contacts/contactList" method="get">
		<input type="hidden" name="accountId" value="${accountId}"/>
	</form>
	
	<form:form id="contactsUpdateForm" commandName="contactDto" action="/spring/stars/operator/contacts/contactUpdate" method="post">
	
		<input type="hidden" name="contactId" value="${contactDto.contactId}">
		<input type="hidden" name="accountId" value="${accountId}"/>
		<input type="hidden" name="hasPendingNewNotification" value="${hasPendingNewNotification == true}"/>
		
		<c:set var="contactInformationSectionTitleKey" value="contactInformationSection"/>
		<c:if test="${contactDto.primary}">
			<c:set var="contactInformationSectionTitleKey" value="contactInformationSection_isPrimary"/>
		</c:if>
		<tags:sectionContainer2 key="${contactInformationSectionTitleKey}">
		
			<tags:nameValueContainer2 nameColumnWidth="150px">
			
				<tags:inputNameValue nameKey=".firstNameLabel" path="firstName"/>
				<tags:inputNameValue nameKey=".lastNameLabel" path="lastName"/>
				<tags:inputNameValue nameKey="yukon.web.modules.operator.contactNotificationEnum.HOME_PHONE" path="homePhone"/>
				<tags:inputNameValue nameKey="yukon.web.modules.operator.contactNotificationEnum.WORK_PHONE" path="workPhone"/>
				<tags:inputNameValue nameKey="yukon.web.modules.operator.contactNotificationEnum.EMAIL" path="email"/>
			
			</tags:nameValueContainer2>
			
			<%-- NOTIFICATIONS TABLE --%>
			<br>
			<table class="resultsTable">
			
				<tr>
					<th style="width:160px;"><i:inline key=".notificationTable.notificationMethodHeader"/></th>
					<th><i:inline key=".notificationTable.valueHeader"/></th>
					<c:if test="${mode == 'CREATE'}">
						<th style="text-align:center;width:100px;"><i:inline key=".notificationTable.removeHeader"/></th>
					</c:if>
				</tr>
			
				<%-- EXISTING NOTIFICATIONS --%>
				<cti:msg2 var="noneText" key=".notificationTable.none"/>
				<c:forEach var="notif" items="${contactDto.otherNotifications}" varStatus="notifRow">
				
					<c:set var="newNotification" value="${notif.notificationId <= 0}"/>
					
					<c:choose>
						<c:when test="${mode == 'EDIT' && newNotification}">
							<tr class="additionalNotificationTr" style="vertical-align: top">
						</c:when>
						<c:otherwise>
							<tr style="vertical-align: top">
						</c:otherwise>
					</c:choose>
				
						<td>
							<c:choose>
								<c:when test="${newNotification}">
									<tags:selectWithItems items="${notificationTypes}" itemValue="contactNotificationType" itemLabel="displayName" path="otherNotifications[${notifRow.index}].contactNotificationType" defaultItemLabel="${noneText}" defaultItemValue=""/>
								</c:when>
								<c:otherwise>
									<tags:selectWithItems items="${notificationTypes}" itemValue="contactNotificationType" itemLabel="displayName" path="otherNotifications[${notifRow.index}].contactNotificationType"/>
								</c:otherwise>
							</c:choose>
						
							<form:hidden path="otherNotifications[${notifRow.index}].notificationId"/>
						</td>
						
						<td><tags:input path="otherNotifications[${notifRow.index}].notificationValue"></tags:input></td>
						
						<c:if test="${!newNotification}">
							<td style="text-align:center;">
								<img src="${delete}" onclick="removeNotification(this)" onmouseover="javascript:this.src='${deleteOver}'" onmouseout="javascript:this.src='${delete}'">
							</td>
						</c:if>
						
					</tr>
				</c:forEach>
			
				<%-- ADD NOTIFICATION --%>
				<c:if test="${mode == 'EDIT'}">
					<tr style="background-color:#EEE;">
						<td colspan="3">
						
							<input type="image" src="${add}" name="newNotification" value="true" onmouseover="javascript:this.src='${addOver}'" onmouseout="javascript:this.src='${add}'">
						
							<i:inline key=".notificationTable.addNotification"/>
							 
						</td>
					</tr>
				</c:if>
				
			</table>
			
		</tags:sectionContainer2>
		
		<%-- BUTTONS --%>
		<br>
		<tags:slowInput2 myFormId="contactListForm" key="button.viewAll" />
		<tags:slowInput2 myFormId="contactsUpdateForm" key="save" />

	</form:form>
	
</cti:standardPage>