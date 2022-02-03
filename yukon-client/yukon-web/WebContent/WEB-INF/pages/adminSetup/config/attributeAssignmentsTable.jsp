<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cm" tagdir="/WEB-INF/tags/contextualMenu" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti"%>
<%@ taglib prefix="d" tagdir="/WEB-INF/tags/dialog" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n"%>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<cti:msgScope paths="modules.adminSetup.config.attributes,yukon.common">
    
    <c:set var="errorClass" value="${not empty errorMessage ? '' : 'dn'}"/>
    <tags:alertBox classes="js-error-msg ${errorClass}" includeCloseButton="true">${fn:escapeXml(errorMessage)}</tags:alertBox>
    <c:set var="successClass" value="${not empty successMessage ? '' : 'dn'}"/>
    <tags:alertBox type="success" classes="js-success-msg ${successClass}" includeCloseButton="true">${fn:escapeXml(successMessage)}</tags:alertBox>

    <table class="compact-results-table row-highlighting has-actions">
        <thead>
            <tr>
                <tags:sort column="${attributeName}"/>
                <tags:sort column="${deviceType}"/>
                <tags:sort column="${pointType}"/>
                <tags:sort column="${pointOffset}"/>
                <th class="action-column"><cti:icon icon="icon-cog" classes="M0"/></th>
            </tr>
        </thead>
        <tbody>
            <c:forEach var="assignment" items="${assignments}">
                <tr>
                    <c:set var="assignmentId" value="${assignment.attributeAssignmentId}"/>
                    <td>${fn:escapeXml(assignment.customAttribute.name)}</td>
                    <td><i:inline key="${assignment.paoType.formatKey}"/></td>
                    <td><i:inline key="${assignment.pointType.formatKey}"/></td>
                    <td>${assignment.offset}</td>
                    <td>
                        <cm:dropdown icon="icon-cog">
                            <cm:dropdownOption key=".edit" icon="icon-pencil" classes="js-edit-assignment" data-assignment-id="${assignmentId}"/>
                            <cm:dropdownOption key=".delete" icon="icon-delete" data-ok-event="yukon:assignment:delete" 
                                classes="js-hide-dropdown js-delete-assignment-${assignmentId}" data-assignment-id="${assignmentId}"/>
                            <d:confirm on=".js-delete-assignment-${assignmentId}" nameKey="assignmentConfirmDelete" argument="${assignment.customAttribute.name}"  />
                            <cti:url var="deleteUrl" value="/admin/config/attributeAssignments/${assignmentId}/delete"/>
                            <form:form id="delete-assignment-form-${assignmentId}" action="${deleteUrl}" method="DELETE">
                                <cti:csrfToken/>
                                <input type="hidden" name="name" value="${fn:escapeXml(assignment.customAttribute.name)}"/>
                            </form:form>
                        </cm:dropdown>
                    </td>
                </tr>
            </c:forEach>
        </tbody>
    </table>
    
    <c:if test="${empty assignments}">
        <span class="empty-list compact-results-table"><i:inline key="yukon.common.search.noResultsFound"/></span>
    </c:if>
    
    <cti:msg2 var="editAssignmentTitle" key=".editAssignmentTitle"/>
    <div class="dn js-edit-assignment-popup"
             data-popup
             data-dialog
             data-title="${editAssignmentTitle}">
    </div>

</cti:msgScope>