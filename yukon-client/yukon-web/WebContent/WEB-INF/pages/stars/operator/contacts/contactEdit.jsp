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
	    jQuery(function() {
			var additionalNotificationTrs = $$('tr.additionalNotificationTr');
			additionalNotificationTrs.each(flashYellow);
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
                <c:if test="${not empty username}">
                    <tags:nameValue2 nameKey=".usernameLabel">${username}</tags:nameValue2>
                </c:if>
			
			</tags:nameValueContainer2>
			
			<%-- NOTIFICATIONS TABLE --%>
            <c:if test="${mode == 'EDIT' || not empty contactDto.otherNotifications}">
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
						
						<td <c:if test="${newNotification}">colspan="2"</c:if>><tags:input path="otherNotifications[${notifRow.index}].notificationValue"></tags:input></td>
						
						<c:if test="${mode == 'EDIT' && !newNotification}">
							<td style="text-align:center;">
								<img src="${delete}" onclick="removeNotification(this)" onmouseover="javascript:this.src='${deleteOver}'" onmouseout="javascript:this.src='${delete}'">
							</td>
						</c:if>
						
					</tr>
				</c:forEach>
			
				<%-- ADD NOTIFICATION --%>
				<cti:displayForPageEditModes modes="EDIT">
                    <tfoot>
    					<tr>
    						<td colspan="3">
    							 <cti:button nameKey="addNotification" type="submit" name="newNotification"/>
    						</td>
    					</tr>
                    </tfoot>
				</cti:displayForPageEditModes>
				
			</table>
            </c:if>
		</tags:formElementContainer>
        
		<%-- BUTTONS --%>
		<br>
        <cti:displayForPageEditModes modes="EDIT,CREATE">
        
            <cti:button nameKey="save" type="submit" styleClass="f_blocker"/>
            
            <cti:displayForPageEditModes modes="EDIT">
                <cti:url value="/spring/stars/operator/contacts/view" var="cancelUrl">
                    <cti:param name="accountId" value="${accountId}"/>
                    <cti:param name="contactId" value="${contactId}"/>
                </cti:url>
            </cti:displayForPageEditModes>
            <cti:displayForPageEditModes modes="CREATE">
                <cti:url value="/spring/stars/operator/contacts/contactList" var="cancelUrl">
                    <cti:param name="accountId" value="${accountId}"/>
                </cti:url>
            </cti:displayForPageEditModes>
            
            <cti:button nameKey="cancel" href="${cancelUrl}"/>
            
        </cti:displayForPageEditModes>
        <cti:displayForPageEditModes modes="VIEW">
            <cti:checkRolesAndProperties value="OPERATOR_ALLOW_ACCOUNT_EDITING">
                <cti:url value="/spring/stars/operator/contacts/edit" var="editUrl">
                    <cti:param name="accountId" value="${accountId}"/>
                    <cti:param name="contactId" value="${contactId}"/>
                </cti:url>
                <cti:button nameKey="edit" href="${editUrl}"/>
            </cti:checkRolesAndProperties>
        </cti:displayForPageEditModes>
	</form:form>
	
</cti:standardPage>