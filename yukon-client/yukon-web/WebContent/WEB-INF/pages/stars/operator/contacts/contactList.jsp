<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<cti:standardPage module="operator" page="contactList">
<cti:checkEnergyCompanyOperator showError="true" accountId="${accountId}">
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
                            <a href="${contactEditUrl}">${fn:escapeXml(contact.lastName)}, ${fn:escapeXml(contact.firstName)}</a>
                        </cti:displayForPageEditModes>
                    </td>
                    
                    <td>
                        <table class="dib fl no-borders">
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
                        <cti:url var="createUrl" value="/stars/operator/contacts/create"/>
                        <form id="addContactForm" action="${createUrl}" method="get">
                            <input type="hidden" name="contactId" value="0"/>
                            <input type="hidden" name="accountId" value="${accountId}"/>
                            <cti:button nameKey="create" icon="icon-plus-green" type="submit"/>
                        </form>
                    </td>
                </tr>
            </tfoot>
        </cti:displayForPageEditModes>
    </div>
</cti:checkEnergyCompanyOperator>
</cti:standardPage>