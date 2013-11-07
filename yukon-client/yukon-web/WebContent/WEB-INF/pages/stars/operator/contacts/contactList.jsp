<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n" %>

<cti:standardPage module="operator" page="contactList">

	<tags:setFormEditMode mode="${mode}"/>
	
	<table class="compact-results-table no-stripes">
        <thead>
    		<tr>
    			<th><i:inline key=".header.contact"/></th>
    			<th><i:inline key=".header.notifications"/></th>
    		</tr>
        </thead>
		
        <tbody>
    		<c:forEach var="contact" items="${contacts}">
    		
    			<tr>
    			
    				<td>
    				
    					<cti:displayForPageEditModes modes="EDIT,CREATE,VIEW">
    						<cti:url var="contactEditUrl" value="/stars/operator/contacts/view">
    							<cti:param name="accountId" value="${accountId}"/>
    							<cti:param name="contactId" value="${contact.contactId}"/>
    						</cti:url>
    					
    						<a href="${contactEditUrl}">${contact.lastName}, ${contact.firstName}</a>
    					</cti:displayForPageEditModes>
    					
    				</td>
    				
    				<td>
    					
    					<table class="dib fl">
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
    					
						<c:if test="${contact.primary}">
							<span class="fr"><i:inline key=".isPrimary"/></span>
						</c:if>
    				</td>
    			</tr>
    		</c:forEach>
        </tbody>
	</table>
    
    <div class="page-action-area">
        <cti:displayForPageEditModes modes="EDIT,CREATE">
            <tfoot>
                <tr>
                    <td colspan="3">
                        <form id="addContactForm" action="/stars/operator/contacts/create" method="get">
                            <input type="hidden" name="contactId" value="0"/>
                            <input type="hidden" name="accountId" value="${accountId}"/>
                            <cti:button nameKey="create" icon="icon-plus-green" type="submit"/>
                        </form>
                    </td>
                </tr>
            </tfoot>
        </cti:displayForPageEditModes>
    </div>
    
</cti:standardPage>