<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cm" tagdir="/WEB-INF/tags/contextualMenu" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="d" tagdir="/WEB-INF/tags/dialog" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags"%>

<cti:standardPage module="tools" page="notificationGroup.${mode}">
    <tags:setFormEditMode mode="${mode}" />
     <!-- TODO : View page code changes will be done under YUK-26551 -->
    This is a Notification Group page name !!
        ${fn:escapeXml(notificationGroup.name)}
    <!-- Actions drop-down -->
    <cti:displayForPageEditModes modes="VIEW">
        <div id="page-actions" class="dn">
            <!-- Delete -->
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
    <cti:includeScript link="/resources/js/pages/yukon.tools.notificationgroup.js" />
</cti:standardPage>