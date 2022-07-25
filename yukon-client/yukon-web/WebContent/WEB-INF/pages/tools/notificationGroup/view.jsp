<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cm" tagdir="/WEB-INF/tags/contextualMenu" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="d" tagdir="/WEB-INF/tags/dialog" %>
<%@ taglib prefix="fancyTree" tagdir="/WEB-INF/tags/fancyTree" %>
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

    <cti:url var="action" value="/tools/notificationGroup/save"/>
    <form:form modelAttribute="notificationGroup" action="${action}" method="post" id="js-notification-grup-settings-form">
        
        <cti:csrfToken />
        <form:hidden path="id" />
        <input type="hidden" id="js-ci-customers" name="ciCustomersJsonString"/>
        <input type="hidden" id="js-unassigned-contacts" name="unassignedContactsJsonString"/>
        
        <tags:sectionContainer2 nameKey="general">
            <tags:nameValueContainer2>
                <tags:nameValue2 nameKey="yukon.common.name">
                    <tags:input path="name" maxlength="60" autofocus="autofocus" inputClass="w300 js-name"/>
                </tags:nameValue2>
                <tags:nameValue2 nameKey="yukon.common.status" rowClass="noswitchtype">
                    <tags:switchButton path="enabled" offNameKey="yukon.common.disabled" onNameKey="yukon.common.enabled" />
                </tags:nameValue2>
            </tags:nameValueContainer2>
        </tags:sectionContainer2>
        
        <cti:displayForPageEditModes modes="CREATE,EDIT">
            <tags:sectionContainer2 nameKey="notificationSettings">
                <div class="column-12-12 clearfix">
                    <div class="column one">
                        <tags:boxContainer2 nameKey="notification" hideEnabled="false">
                            <fancyTree:inlineTree id="js-notification-tree-id"
                                                  maxHeight="250"
                                                  styleClass="stacked"
                                                  dataJson="${notificationTreeJson}" 
                                                  includeControlBar="true"
                                                  treeParameters='{"minExpandLevel": "1", "icon": false}'
                                                  includeCheckbox="${true}"
                                                  multiSelect="${true}"
                                                  toggleNodeSelectionOnClick="${false}"/>
                       </tags:boxContainer2>
                    </div>
                    <div class="column two nogutter">
                        <tags:boxContainer2 nameKey="notification" hideEnabled="false" styleClass="dn" id="js-notification-settings">
                            <tags:nameValueContainer2>
                                <tags:nameValue2 nameKey=".notificationGroup.sendEmail" rowClass="noswitchtype">
                                    <tags:switchButton offNameKey="yukon.common.no" onNameKey="yukon.common.yes" 
                                                       name="sendEmail"/>
                                </tags:nameValue2>
                                <tags:nameValue2 nameKey=".notificationGroup.makePhoneCalls" rowClass="noswitchtype">
                                    <tags:switchButton offNameKey="yukon.common.no" onNameKey="yukon.common.yes"
                                                       name="makePhoneCalls"/>
                                </tags:nameValue2>
                            </tags:nameValueContainer2>
                        </tags:boxContainer2>
                    </div>
                </div>
            </tags:sectionContainer2>
        </cti:displayForPageEditModes>

        <cti:displayForPageEditModes modes="VIEW">
            <tags:sectionContainer2 nameKey="notificationSettings" styleClass="select-box">
                <div class="js-table-container scroll-lg">
                    <table id="js-notification-settings-table" class="compact-results-table dashed">
                        <thead>
                            <tr>
                                <th width="60%"><i:inline key="yukon.common.name"/></th>
                                <th width="20%"><i:inline key=".notificationGroup.sendEmail"/></th>
                                <th width="20%"><i:inline key=".notificationGroup.makePhoneCalls"/></th>
                            </tr>
                        </thead>

                        <tbody>
                            <c:forEach var="cICustomer" items="${notificationGroup.cICustomers}" varStatus="status">
                                <tr>
                                    <td>${fn:escapeXml(cICustomer.companyName)}</td>
                                    <td><tags:displayBooleanStatusText value="${cICustomer.emailEnabled}"/></td>
                                    <td><tags:displayBooleanStatusText value="${cICustomer.phoneCallEnabled}"/></td> 
                                </tr>
                                <c:forEach var="contact" items="${cICustomer.contacts}">
                                    <tr>
                                        <td> - ${fn:escapeXml(contact.name)}</td>
                                        <td><tags:displayBooleanStatusText value="${contact.emailEnabled}"/></td>
                                        <td><tags:displayBooleanStatusText value="${contact.phoneCallEnabled}"/></td> 
                                    </tr>
                                    <c:forEach var="notifications" items="${contact.notifications}">
                                        <tr>
                                            <td> - - ${fn:escapeXml(notifications.notification)}</td>
                                            <td><tags:displayBooleanStatusText value="${notifications.emailEnabled}"/></td>
                                            <td><tags:displayBooleanStatusText value="${notifications.phoneCallEnabled}"/></td> 
                                        </tr>
                                    </c:forEach>
                                </c:forEach>
                            </c:forEach>

                            <c:forEach var="unassignedContact" items="${notificationGroup.unassignedContacts}">
                                <tr>
                                    <td>${fn:escapeXml(unassignedContact.name)}</td>
                                    <td><tags:displayBooleanStatusText value="${unassignedContact.emailEnabled}"/></td>
                                    <td><tags:displayBooleanStatusText value="${unassignedContact.phoneCallEnabled}"/></td> 
                                </tr>
                                <c:forEach var="notifications" items="${unassignedContact.notifications}">
                                    <tr>
                                        <td> - ${fn:escapeXml(notifications.notification)}</td>
                                        <td><tags:displayBooleanStatusText value="${notifications.emailEnabled}"/></td>
                                        <td><tags:displayBooleanStatusText value="${notifications.phoneCallEnabled}"/></td> 
                                    </tr>
                                </c:forEach>
                            </c:forEach>
                        </tbody>
                    </table>
                </div>
            </tags:sectionContainer2>
        </cti:displayForPageEditModes>
        
        <div class="page-action-area">
            <cti:displayForPageEditModes modes="EDIT,CREATE">
                <cti:button id="js-save-notification-group" nameKey="save" classes="primary action" busy="true"/>
            </cti:displayForPageEditModes>
            
            <cti:displayForPageEditModes modes="CREATE">
                <cti:url var="listUrl" value="/tools/notificationGroup/list" />
                <cti:button nameKey="cancel" href="${listUrl}" />
            </cti:displayForPageEditModes>
        </div>
        
    </form:form>
    <cti:includeScript link="/resources/js/pages/yukon.tools.notificationgroup.js" />
</cti:standardPage>