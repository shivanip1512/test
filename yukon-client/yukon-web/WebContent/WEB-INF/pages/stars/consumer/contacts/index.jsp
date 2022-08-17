<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti"%>
<%@ taglib prefix="dr" tagdir="/WEB-INF/tags/dr"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="d" tagdir="/WEB-INF/tags/dialog"%>

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
                ${fn:escapeXml(primaryContact.contFirstName)} ${fn:escapeXml(primaryContact.contLastName)}
                <cti:url var="editUrl" value="/stars/consumer/contacts/edit">
                    <cti:param name="contactId" value="${primaryContact.contactID}"/>
                </cti:url>
                <a href="${editUrl}" class="fr button naked"><i class="icon icon-pencil"></i><span class="b-label"><i:inline key=".editContact"/></span></a>
            </h3>
            <c:if test="${fn:length(primaryContact.liteContactNotifications) eq 0}">
                <h4><i:inline key=".noNotifications" /></h4>
            </c:if>
            <ul>
                <c:forEach var="notification" items="${primaryContact.liteContactNotifications}">
                    <li>
                        <label><i:inline key="yukon.web.modules.operator.contactNotificationEnum.${notification.contactNotificationType}"/></label>
                        <div class="fl"><spring:escapeBody javaScriptEscape="true" htmlEscape="true" >${notification.notification}</spring:escapeBody></div>
                    </li>
                </c:forEach>
            </ul>
           </div>
       </tags:sectionContainer2>
  
    <!-- Display additional contacts and notifications -->
    <c:if test="${fn:length(additionalContacts) > 0}">
        <tags:sectionContainer2 nameKey="additionalContacts" >
            <c:forEach var="contact" items="${additionalContacts}">
                <cti:url var="contactEditUrl" value="/stars/consumer/contacts/edit">
                    <cti:param name="contactId" value="${contact.contactID}"/>
                </cti:url>
                <div class="contactCard">
                    <h3>
                        ${fn:escapeXml(contact.contFirstName)} ${fn:escapeXml(contact.contLastName)}
                        <cti:url var="deleteUrl" value="/stars/consumer/contacts/delete">
                            <cti:param name="contactId" value="${contact.contactID}"/>
                        </cti:url>
                        <form action="${deleteUrl}" method="POST">
                            <cti:csrfToken/>
                            <input type="hidden" name="contactID" value="${contact.contactID}"/>
                            <cti:msg2 key=".removeContact" var="removeContact"/>
                            <cti:button renderMode="labeledImage" id="contact_${contact.contactID}" classes="fr" type="submit" icon="icon-cross" label="${removeContact}"/>
                            <d:confirm on="#contact_${contact.contactID}" nameKey="removeContact" argument="${contact.contFirstName} ${contact.contLastName}"/>
                        </form>
                        
                        <cti:url var="editUrl" value="/stars/consumer/contacts/edit">
                            <cti:param name="contactId" value="${contact.contactID}"/>
                        </cti:url>
                        <a href="${editUrl}" class="fr button naked"><i class="icon icon-pencil"></i><span class="b-label"><i:inline key=".editContact"/></span></a>
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
                                            ${fn:escapeXml(notification.notification)}
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
    
    <cti:url value="/stars/consumer/contacts/new" var="newContactUrl" />
    <cti:button nameKey="createNewContact" href="${newContactUrl}" icon="icon-plus-green"/>
</cti:standardPage>