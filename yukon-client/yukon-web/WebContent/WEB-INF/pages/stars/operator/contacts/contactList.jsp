<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n" %>

<cti:standardPage module="operator" page="contactList">

	<tags:setFormEditMode mode="${mode}"/>
	
	<script type="text/javascript">
	
		function deleteAdditionalContact(id) {

			$('deleteAdditionalContactId').value = id;
			$('deleteAdditionalContactForm').submit();
		}
		
	</script>
	
	<cti:url var="add" value="/WebConfig/yukon/Icons/add.gif"/>
	<cti:url var="addOver" value="/WebConfig/yukon/Icons/add_over.gif"/>
	
	<form id="deleteAdditionalContactForm" action="/stars/operator/contacts/deleteAdditionalContact" method="post">
		<input type="hidden" name="deleteAdditionalContactId" id="deleteAdditionalContactId"/>
		<input type="hidden" name="accountId" value="${accountId}"/>
	</form>
	
	<table class="resultsTable contactListTable">
        <thead>
    		<tr>
    			<th><i:inline key=".header.contact"/></th>
    			<th><i:inline key=".header.notifications"/></th>
    			<cti:displayForPageEditModes modes="EDIT,CREATE">
    				<th class="removeCol"><i:inline key=".header.remove"/></th>
    			</cti:displayForPageEditModes>
    		</tr>
        </thead>
		
        <tbody>
    		<c:forEach var="contact" items="${contacts}">
    		
    			<tr>
    			
    				<td class="nameCol">
    				
    					<cti:displayForPageEditModes modes="EDIT,CREATE,VIEW">
    						<cti:url var="contactEditUrl" value="/stars/operator/contacts/view">
    							<cti:param name="accountId" value="${accountId}"/>
    							<cti:param name="contactId" value="${contact.contactId}"/>
    						</cti:url>
    					
    						<a href="${contactEditUrl}">${contact.lastName}, ${contact.firstName}</a>
    					</cti:displayForPageEditModes>
    					
    				</td>
    				
    				<td>
    					
    					<table class="noStyle contactNotificationListTable">
    						<c:if test="${not empty contact.homePhone}">
    							<tr>
    								<td class="type"><i:inline key="yukon.web.modules.operator.contactNotificationEnum.HOME_PHONE"/>:</td>
    								<td>${contact.homePhone}</td>
    							</tr>
    						</c:if>
    						<c:if test="${not empty contact.workPhone}">
    							<tr>
    								<td class="type"><i:inline key="yukon.web.modules.operator.contactNotificationEnum.WORK_PHONE"/>:</td>
    								<td>${contact.workPhone}</td>
    							</tr>
    						</c:if>
    						<c:if test="${not empty contact.email}">
    							<tr>
    								<td class="type"><i:inline key="yukon.web.modules.operator.contactNotificationEnum.EMAIL"/>:</td>
    								<td>${contact.email}</td>
    							</tr>
    						</c:if>
    						<c:forEach var="otherNotification" items="${contact.otherNotifications}">
    							<tr>
    								<td class="type"><i:inline key="${otherNotification.contactNotificationType.formatKey}"/>:</td>
    								<td>${otherNotification.notificationValue}</td>
    							</tr>
    						</c:forEach>
    					</table>
    					
    					
    				</td>
    				
    				<%-- REMOVE COLUMN --%>
    				<cti:displayForPageEditModes modes="EDIT,CREATE">
    				<td class="removeCol">
    				
    					<c:choose>
    						<c:when test="${!contact.primary}">
                                <cti:button nameKey="deleteContact" onclick="deleteAdditionalContact(${contact.contactId});" renderMode="labeledImage"/>
    						</c:when>
    						<c:otherwise>
    							<i:inline key=".isPrimary"/>
    						</c:otherwise>
    					</c:choose>
    					
    				</td>
    				</cti:displayForPageEditModes>
    			
    			</tr>
    			
    		</c:forEach>
        </tbody>
        
        <%-- ADD CONTACT --%>
    	<cti:displayForPageEditModes modes="EDIT,CREATE">
            <tfoot>
                <tr>
                    <td colspan="3">
                        <form id="addContactForm" action="/stars/operator/contacts/create" method="get">
                            <input type="hidden" name="contactId" value="0"/>
                            <input type="hidden" name="accountId" value="${accountId}"/>
                            <cti:button nameKey="create" type="submit"/>
                        </form>
                    </td>
                </tr>
            </tfoot>
    	</cti:displayForPageEditModes>
	
	</table>
    
</cti:standardPage>