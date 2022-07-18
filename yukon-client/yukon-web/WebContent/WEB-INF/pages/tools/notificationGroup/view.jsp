<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cm" tagdir="/WEB-INF/tags/contextualMenu" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="d" tagdir="/WEB-INF/tags/dialog" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n"%>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags"%>


<cti:standardPage module="tools" page="notificationGroup.${mode}">
    <tags:setFormEditMode mode="${mode}" />

    <!-- Actions drop-down -->
    <cti:displayForPageEditModes modes="VIEW">
        <div id="page-actions" class="dn">
            <!-- Create -->
            <cti:url var="createUrl" value="/tools/notificationGroup/create" />
            <cm:dropdownOption icon="icon-plus-green" key="yukon.web.components.button.create.label" id="js-create-option" href="${createUrl}"/>
            <!-- Edit -->
            <cti:url var="editUrl" value="/tools/notificationGroup/${notificationGroup.id}/edit"/>
            <cm:dropdownOption icon="icon-pencil" key="yukon.web.components.button.edit.label" href="${editUrl}" />
            <!-- Delete -->
            <li class="divider"></li>
            <cm:dropdownOption icon="icon-delete" 
                               key="yukon.web.components.button.delete.label" 
                               classes="js-hide-dropdown" 
                               id="js-delete" 
                               data-ok-event="yukon:notificationGroup:delete"/>
            <d:confirm on="#js-delete" nameKey="confirmDelete" argument="${notificationGroup.name}" />
            <cti:url var="deleteUrl" value="/tools/notificationGroup/${notificationGroup.id}/delete"/>
            <form:form id="delete-notificationGroup-form" action="${deleteUrl}" method="delete" modelAttribute="notificationGroup">
                <tags:hidden path="id"/>
                <tags:hidden path="name"/>
                <cti:csrfToken/>
            </form:form>
        </div>
    </cti:displayForPageEditModes>

   <%--  <cti:url var="action" value="/tools/notificationGroup/save" /> --%>
   
    <form:form modelAttribute="notificationGroup" action="${action}" method="post">
        <cti:csrfToken />
        <form:hidden path="id" />
        <tags:sectionContainer2 nameKey="general">
            <tags:nameValueContainer2>
                <tags:nameValue2 nameKey=".name">
                    <tags:input path="name" maxlength="60" autofocus="autofocus" inputClass="w300 js-name"/>
                </tags:nameValue2>
                <tags:nameValue2 nameKey=".status" rowClass="noswitchtype">
                    <tags:switchButton path="enabled" offNameKey="yukon.common.disabled" onNameKey="yukon.common.enabled" />
                </tags:nameValue2>
            </tags:nameValueContainer2>
        </tags:sectionContainer2>

        <cti:displayForPageEditModes modes="VIEW">
            <tags:sectionContainer2 nameKey="notificationSettings" styleClass="select-box">
                <div class="js-table-container scroll-lg">
                    <table id="js-notification-settings-table" class="compact-results-table dashed">
                        <thead>
                            <tr>
                                <th width="60%"><i:inline key=".name"/></th>
                                <th width="20%"><i:inline key=".sendEmail"/></th>
                                <th width="20%"><i:inline key=".makePhoneCall"/></th>
                            </tr>
                        </thead>

                        <tbody>
                            <c:forEach var="cICustomers" items="${notificationGroup.cICustomers}">
                                <tr>
                                    <td> - ${fn:escapeXml(cICustomers.companyName)}</td>
                                    <td>
                                        ${cICustomers.emailEnabled}
                                   <%--  <tags:switchButton path="cICustomers.emailEnabled" offNameKey="yukon.common.no" onNameKey="yukon.common.yes" /> --%>
                                    </td>
                                    <td>
                                        ${cICustomers.phoneCallEnabled}
                                        <%-- <tags:switchButton path="cICustomers.phoneCallEnabled" offNameKey="yukon.common.no" onNameKey="yukon.common.yes" /> --%>
                                    </td> 
                                </tr>
                                <c:forEach var="contacts" items="${cICustomers.contacts}">
                                    <tr>
                                        <td> - - ${fn:escapeXml(contacts.name)}</td>
                                        <td>${contacts.emailEnabled}</td>
                                        <td>${contacts.phoneCallEnabled}</td> 
                                    </tr>
                                    <c:forEach var="notifications" items="${contacts.notifications}">
                                        <tr>
                                            <td> - - - ${fn:escapeXml(notifications.notification)}</td>
                                            <td>${notifications.emailEnabled}</td>
                                            <td>${notifications.phoneCallEnabled}</td> 
                                        </tr>
                                    </c:forEach>
                                </c:forEach>
                            </c:forEach>

                            <c:forEach var="unassignedContacts" items="${notificationGroup.unassignedContacts}">
                                <tr>
                                    <td> - ${fn:escapeXml(unassignedContacts.name)}</td>
                                    <td>${unassignedContacts.emailEnabled}</td>
                                    <td>${unassignedContacts.phoneCallEnabled}</td> 
                                </tr>
                                <c:forEach var="notifications" items="${unassignedContacts.notifications}">
                                    <tr>
                                        <td> - - ${fn:escapeXml(notifications.notification)}</td>
                                        <td>${notifications.emailEnabled}</td>
                                        <td>${notifications.phoneCallEnabled}</td> 
                                    </tr>
                                </c:forEach>
                            </c:forEach>
                        </tbody>
                    </table>
                </div>
            </tags:sectionContainer2>
        </cti:displayForPageEditModes>
    </form:form>

    <cti:includeScript link="/resources/js/pages/yukon.tools.notificationgroup.js" />
</cti:standardPage>