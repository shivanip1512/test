<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cm" tagdir="/WEB-INF/tags/contextualMenu" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n"%>

<cti:msgScope paths="modules.adminSetup.config.attributes,yukon.common">

    <table class="compact-results-table row-highlighting has-actions">
        <thead>
            <th><i:inline key=".attributeName"/></th>
            <th><i:inline key=".deviceType"/></th>
            <th><i:inline key=".pointType"/></th>
            <th><i:inline key=".pointOffset"/></th>
            <th class="action-column"><cti:icon icon="icon-cog" classes="M0"/></th>
        </thead>
        <tbody>
            <c:forEach var="assignment" items="${assignments}">
                <tr>
                    <td>${fn:escapeXml(assignment.attribute.name)}</td>
                    <td><i:inline key="${assignment.deviceType.formatKey}"/></td>
                    <td><i:inline key="${assignment.pointType.formatKey}"/></td>
                    <td>${assignment.pointOffset}</td>
                    <td>
                        <cm:dropdown icon="icon-cog">
                            <cm:dropdownOption key=".edit" icon="icon-pencil" classes="js-edit-assignment" data-assignment-id="${assignment.id}"/>
                            <cm:dropdownOption id="delete-assignment-${assignment.id}" key=".delete" icon="icon-cross" 
                                data-ok-event="yukon:assignment:delete" classes="js-hide-dropdown" data-assignment-id="${assignment.id}"/>
                            <d:confirm on="#delete-assignment-${assignment.id}" nameKey="assignmentConfirmDelete" argument="${assignment.attribute.name}"  />
                            <cti:url var="deleteUrl" value="/admin/config/attributeAssignments/${assignment.id}/delete"/>
                            <form:form id="delete-assignment-form-${assignment.id}" action="${deleteUrl}" method="DELETE">
                                <cti:csrfToken/>
                                <input type="hidden" name="name" value="${assignment.attribute.name}"/>
                            </form:form>
                        </cm:dropdown>
                    </td>
                </tr>
            </c:forEach>
        </tbody>
    </table>

</cti:msgScope>