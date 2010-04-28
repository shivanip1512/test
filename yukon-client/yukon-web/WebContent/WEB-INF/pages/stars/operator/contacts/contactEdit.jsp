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
		
		<c:set var="contactInformationSectionTitleKey" value="contactInformationSection"/>
		<c:if test="${contactDto.primary}">
			<c:set var="contactInformationSectionTitleKey" value="contactInformationSection_isPrimary"/>
		</c:if>
		<tags:formElementContainer nameKey="${contactInformationSectionTitleKey}">
		
			<tags:nameValueContainer2>
			
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
					<cti:displayForPageEditModes modes="EDIT">
						<th style="text-align:center;width:100px;"><i:inline key=".notificationTable.removeHeader"/></th>
					</cti:displayForPageEditModes>
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
				<cti:displayForPageEditModes modes="EDIT">
					<tr style="background-color:#EEE;">
						<td colspan="3">
						
							<%-- replicates look and feel of LabeledImgTag but since this action won't work as an href it is done as a <input type="image"/> --%>
							<cti:msg2 var="addNotificationhoverText" key=".addNotification.hoverText"/>
							<span title="${addNotificationhoverText}" class="pointer" onmouseover="javascript:$('addNotificationImgInput').src='${addOver}'" onmouseout="javascript:$('addNotificationImgInput').src='${add}'">
								<input id="addNotificationImgInput" type="image" src="${add}" name="newNotification" value="true" class="logoImage">
								<label for="addNotificationImgInput" class="logoImage pointer"><cti:msg2 key=".addNotification.label"/></label>
							</span>
							 
						</td>
					</tr>
				</cti:displayForPageEditModes>
				
			</table>
			
		</tags:formElementContainer>
		
		<%-- BUTTONS --%>
		<br>
		<tags:slowInput2 formId="contactsUpdateForm" key="save"/>
		<tags:slowInput2 formId="contactListForm" key="cancel"/>

	</form:form>
	
</cti:standardPage>