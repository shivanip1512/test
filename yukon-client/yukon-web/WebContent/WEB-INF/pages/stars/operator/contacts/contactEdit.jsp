<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring" %>

<cti:standardPage module="operator" page="contact.${mode}">

<script type="text/javascript">
$(function() {
    $(document).on('click', '.js-delete', function(e) {
        $(e.currentTarget).closest('tr').remove();
    });
});
</script>
    <cti:checkEnergyCompanyOperator showError="true" accountId="${accountId}">
    <tags:setFormEditMode mode="${mode}"/>

    <cti:url var="contactUpdateUrl" value="/stars/operator/contacts/contactUpdate"/>
    <form:form id="contactsUpdateForm" modelAttribute="contactDto" action="${contactUpdateUrl}" method="post">
        <cti:csrfToken/>
        <input type="hidden" name="contactId" value="${contactDto.contactId}">
        <input type="hidden" name="accountId" value="${accountId}"/>
        
        <c:set var="contactInformationSectionTitleKey" value="contactInformationSection"/>
        <c:if test="${contactDto.primary}">
            <c:set var="contactInformationSectionTitleKey" value="contactInformationSection_isPrimary"/>
        </c:if>
        <tags:sectionContainer2 nameKey="${contactInformationSectionTitleKey}" styleClass="stacked">
        
            <tags:nameValueContainer2>
            
                <tags:inputNameValue nameKey=".firstNameLabel" path="firstName"/>
                <tags:inputNameValue nameKey=".lastNameLabel" path="lastName"/>
                <tags:inputNameValue nameKey="yukon.web.modules.operator.contactNotificationEnum.HOME_PHONE" path="homePhone"/>
                <tags:inputNameValue nameKey="yukon.web.modules.operator.contactNotificationEnum.WORK_PHONE" path="workPhone"/>
                <tags:inputNameValue nameKey="yukon.web.modules.operator.contactNotificationEnum.EMAIL" path="email"/>
                <c:if test="${not empty username}">
                    <tags:nameValue2 nameKey=".usernameLabel">${fn:escapeXml(username)}</tags:nameValue2>
                </c:if>
            
            </tags:nameValueContainer2>
            
        </tags:sectionContainer2>
        
            <%-- NOTIFICATIONS TABLE --%>
        <c:if test="${mode == 'EDIT' || not empty contactDto.otherNotifications}">
            <tags:sectionContainer2 nameKey="additionalNotifications">
                
                <%-- EXISTING NOTIFICATIONS --%>
                <tags:nameValueContainer2>
                
                    <cti:msg2 var="noneText" key=".notificationTable.none"/>
                    <c:forEach var="notif" items="${contactDto.otherNotifications}" varStatus="notifRow">
                        <c:set var="newNotification" value="${notif.notificationId <= 0}"/>
                        <tr>
                            <td class="name">
                                <form:hidden path="otherNotifications[${notifRow.index}].notificationId"/>
                                <c:choose>
                                    <c:when test="${newNotification}">
                                        <tags:selectWithItems items="${notificationTypes}" itemValue="contactNotificationType" itemLabel="displayName" path="otherNotifications[${notifRow.index}].contactNotificationType" defaultItemLabel="${noneText}" defaultItemValue=""/>:
                                    </c:when>
                                    <c:otherwise>
                                        <tags:selectWithItems items="${notificationTypes}" itemValue="contactNotificationType" itemLabel="displayName" path="otherNotifications[${notifRow.index}].contactNotificationType"/>:
                                    </c:otherwise>
                                </c:choose>
                            </td>
                            
                            <td class="value">
                                <tags:input path="otherNotifications[${notifRow.index}].notificationValue" inputClass="fl"/>
                                <c:if test="${mode == 'EDIT' && !newNotification}">
                                    <cti:button nameKey="delete" icon="icon-cross" renderMode="buttonImage" classes="js-delete fn"/>
                                </c:if>
                            </td>
                    </c:forEach>
                    
                </tags:nameValueContainer2>
            </tags:sectionContainer2>
            
            <cti:displayForPageEditModes modes="EDIT">
                <div class="action-area">
                    <cti:button nameKey="addNotification" type="submit" name="newNotification" icon="icon-add"/>
                </div>
            </cti:displayForPageEditModes>
        </c:if>
        
        <%-- BUTTONS --%>
        <div class="page-action-area">
            <cti:displayForPageEditModes modes="EDIT,CREATE">
            
                <cti:button nameKey="save" type="submit" classes="primary action"/>
                
                <c:if test="${!contactDto.primary}">
                    <cti:displayForPageEditModes modes="EDIT">
                        <cti:url value="/stars/operator/contacts/deleteAdditionalContact" var="deleteUrl">
                            <cti:param name="accountId" value="${accountId}"/>
                            <cti:param name="contactId" value="${contactId}"/>
                        </cti:url>
                        <cti:button nameKey="delete" classes="delete" href="${deleteUrl}"/>
                    </cti:displayForPageEditModes>
                </c:if>
                
                <cti:displayForPageEditModes modes="EDIT">
                    <cti:url value="/stars/operator/contacts/view" var="cancelUrl">
                        <cti:param name="accountId" value="${accountId}"/>
                        <cti:param name="contactId" value="${contactId}"/>
                    </cti:url>
                </cti:displayForPageEditModes>
                <cti:displayForPageEditModes modes="CREATE">
                    <cti:url value="/stars/operator/contacts/contactList" var="cancelUrl">
                        <cti:param name="accountId" value="${accountId}"/>
                    </cti:url>
                </cti:displayForPageEditModes>
                <cti:button nameKey="cancel" href="${cancelUrl}"/>
                
            </cti:displayForPageEditModes>
            <cti:displayForPageEditModes modes="VIEW">
                <cti:checkRolesAndProperties value="OPERATOR_ALLOW_ACCOUNT_EDITING">
                    <cti:url value="/stars/operator/contacts/edit" var="editUrl">
                        <cti:param name="accountId" value="${accountId}"/>
                        <cti:param name="contactId" value="${contactId}"/>
                    </cti:url>
                    <cti:button nameKey="edit" icon="icon-pencil" href="${editUrl}"/>
                </cti:checkRolesAndProperties>
            </cti:displayForPageEditModes>
        </div>
    </form:form>
    </cti:checkEnergyCompanyOperator>
</cti:standardPage>