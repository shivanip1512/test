<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti"%>
<%@ taglib prefix="dr" tagdir="/WEB-INF/tags/dr"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>

<cti:standardPage module="consumer" page="contacts">
<cti:standardMenu/>

	<cti:flashScopeMessages/>
	
   <h3><cti:msg key="yukon.dr.consumer.contacts.header" /></h3>
   
   <c:if test="${promptForEmail}">
        <tags:boxContainer2 nameKey="promptForEmail" hideEnabled="false" >
            <cti:msg2 key="yukon.web.modules.consumer.general.promptForEmail"/>
            <cti:msg2 key=".promptForEmail.instructions"/>
        </tags:boxContainer2>
        <br>
    </c:if>
    
    <!-- Display primary contact and notifications -->
    <tags:sectionContainer2 nameKey="primaryContact" >
	   	<div class="contactCard">
	    	<h3>
	    		<spring:htmlEscape defaultHtmlEscape="">${primaryContact.contFirstName} ${primaryContact.contLastName}</spring:htmlEscape>
		    	<cti:url var="editUrl" value="/spring/stars/consumer/contacts/edit">
					<cti:param name="contactId" value="${primaryContact.contactID}"/>
				</cti:url>
	    		<a href="${editUrl}" class="fr labeled_icon icon_pencil"><i:inline key=".editContact"/></a>
    		</h3>
	    	<c:if test="${fn:length(primaryContact.liteContactNotifications) eq 0}">
		    	<h4><i:inline key=".noNotifications" /></h4>
	    	</c:if>
	    	<ul>
		    	<c:forEach var="notification" items="${primaryContact.liteContactNotifications}">
		    		<li><label><i:inline key="yukon.web.modules.operator.contactNotificationEnum.${notification.contactNotificationType}"/></label>${notification.notification}</li>
		    	</c:forEach>
	    	</ul>
	   	</div>
   	</tags:sectionContainer2>
  
    <!-- Display additional contacts and notifications -->
    <c:if test="${fn:length(additionalContacts) > 0}">
	    <tags:sectionContainer2 nameKey="additionalContacts" >
		    <c:forEach var="contact" items="${additionalContacts}">
		    	<cti:url var="contactEditUrl" value="/spring/stars/consumer/contacts/edit">
					<cti:param name="contactId" value="${contact.contactID}"/>
				</cti:url>
		    	<div class="contactCard">
		    		<h3>
		    			<spring:htmlEscape defaultHtmlEscape="">${contact.contFirstName} ${contact.contLastName}</spring:htmlEscape>
				    	<cti:url var="deleteUrl" value="/spring/stars/consumer/contacts/delete">
							<cti:param name="contactId" value="${contact.contactID}"/>
						</cti:url>
			    		<form action="${deleteUrl}" method="POST">
							<input type="hidden" name="contactID" value="${contact.contactID}"/>
				    		<a id="contact_${contact.contactID}" href="javascript:void();" class="fr labeled_icon icon_remove"><i:inline key=".removeContact"/></a>
							<tags:confirmDialog nameKey=".removeContact" on="#contact_${contact.contactID}" argument="${contact.contFirstName} ${contact.contLastName}" />
						</form>
						
				    	<cti:url var="editUrl" value="/spring/stars/consumer/contacts/edit">
							<cti:param name="contactId" value="${contact.contactID}"/>
						</cti:url>
			    		<a href="${editUrl}" class="fr labeled_icon icon_pencil"><i:inline key=".editContact"/></a>
		    		</h3>
		    		<c:if test="${fn:length(contact.liteContactNotifications) eq 0}">
			    		<h4><i:inline key=".noNotifications" /></h4>
		    		</c:if>
		    		<ul>
				    	<c:forEach var="notification" items="${contact.liteContactNotifications}">
				    		<c:choose>
				    			<c:when test="${notification.contactNotificationType.phoneType or notification.contactNotificationType.faxType}">
						    		<cti:formatPhoneNumber value="${notification.notification}" var="phone"/>
						    		<li>
						    			<label><i:inline key="yukon.web.modules.operator.contactNotificationEnum.${notification.contactNotificationType}"/></label>
						    			<div class="fl">
						    				${phone}
						    			</div>
					    			</li>
				    			</c:when>
				    			<c:otherwise>
						    		<li>
						    			<label><i:inline key="yukon.web.modules.operator.contactNotificationEnum.${notification.contactNotificationType}"/></label>
						    			<div class="fl">
						    				<spring:escapeBody>
						    				${notification.notification}
						    				</spring:escapeBody>
						    			</div>
					    			</li>
				    			</c:otherwise>
				    		</c:choose>
				    	</c:forEach>
			    	</ul>
		    	</div>
		    </c:forEach>
	    </tags:sectionContainer2>
    </c:if>
    
    <cti:url value="/spring/stars/consumer/contacts/new" var="newContactUrl" />
    <cti:button nameKey="createNewContact" href="${newContactUrl}"/>
</cti:standardPage>