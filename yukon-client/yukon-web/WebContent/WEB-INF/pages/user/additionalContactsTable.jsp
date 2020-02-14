<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cm" tagdir="/WEB-INF/tags/contextualMenu" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="d" tagdir="/WEB-INF/tags/dialog" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<div style="margin-top:50px">
    <cti:url var="createUrl" value="/user/contacts/create"/>
    <cti:button nameKey="create" icon="icon-plus-green" href="${createUrl}" classes="fr"/>
    
    <tags:sectionContainer2 nameKey="additionalContacts">
        <div class="scroll-sm">
            <table class="compact-results-table no-stripes row-highlighting has-actions">
                <th><i:inline key=".firstname"/></th>
                <th><i:inline key=".lastname"/></th>
                <th><i:inline key=".contactInfo"/></th>
                <th class="action-column"><cti:icon icon="icon-cog" classes="M0" /></th>
                <tbody>
                    <c:forEach var="contact" items="${additionalContacts}">
                        <tr>
                            <td>${fn:escapeXml(contact.firstName)}</td>
                            <td>${fn:escapeXml(contact.lastName)}</td>
                            <td>                        
                                <table class="dib fl">
                                    <c:if test="${not empty contact.homePhone}">
                                        <tr>
                                            <td class="name"><i:inline key="yukon.web.modules.operator.contactNotificationEnum.HOME_PHONE"/>:</td>
                                            <td>${contact.homePhone}</td>
                                        </tr>
                                    </c:if>
                                    <c:if test="${not empty contact.workPhone}">
                                        <tr>
                                            <td class="name"><i:inline key="yukon.web.modules.operator.contactNotificationEnum.WORK_PHONE"/>:</td>
                                            <td>${contact.workPhone}</td>
                                        </tr>
                                    </c:if>
                                    <c:if test="${not empty contact.email}">
                                        <tr>
                                            <td class="name"><i:inline key="yukon.web.modules.operator.contactNotificationEnum.EMAIL"/>:</td>
                                            <td>${contact.email}</td>
                                        </tr>
                                    </c:if>
                                    <c:forEach var="otherNotification" items="${contact.otherNotifications}">
                                        <tr>
                                            <td class="name"><i:inline key="${otherNotification.contactNotificationType.formatKey}"/>:</td>
                                            <td>${fn:escapeXml(otherNotification.notificationValue)}</td>
                                        </tr>
                                    </c:forEach>
                                </table>
                            </td>
                            <td>
                                <cm:dropdown icon="icon-cog">
                                    <cti:url var="contactEditUrl" value="/user/contacts/${contact.contactId}/edit"/>
                                    <cm:dropdownOption key="yukon.web.components.button.edit.label" icon="icon-pencil" href="${contactEditUrl}"/>
                                    <cm:dropdownOption id="delete_${contact.contactId}" key="yukon.web.components.button.delete.label" icon="icon-cross" 
                                        data-ok-event="yukon:profile:contact:remove" data-contact-id="${contact.contactId}"/>
                                    <d:confirm nameKey="confirmDelete" argument="${contact.firstName} ${contact.lastName}" on="#delete_${contact.contactId}"/>
                                </cm:dropdown>
                            </td>
                        </tr>
                    </c:forEach>
                    <c:if test="${empty additionalContacts}">
                        <tr>
                            <td colspan="4">
                                <span class="empty-list"><i:inline key="yukon.common.search.noResultsFound"/></span>
                            </td
                        </tr>
                    </c:if>
                </tbody>
            </table>
        </div>
    </tags:sectionContainer2>
</div>
