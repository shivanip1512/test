<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n" %>

<c:set var="numEntryColumns" value="2"/>
<c:if test="${usesType}">
    <c:set var="numEntryColumns" value="3"/>
</c:if>

<tags:standardPageFragment module="adminSetup" pageName="list.EDIT" fragmentName="entry">

    <tags:setFormEditMode mode="${mode}"/>

    <table>
        <tr>
            <td>
                <input type="text" name="entries[${entryIndex}].text"/>
                <input type="hidden" name="entries[${entryIndex}].entryId" value="0">
                <input type="hidden" name="entries[${entryIndex}].order"
                    value="${entryIndex + 1}" class="orderField">
                <input type="hidden" name="entries[${entryIndex}].deletion"
                    value="false" class="isDeletionField">
            </td>
            <c:if test="${usesType}">
                <td>
                <select name="entries[${entryIndex}].definitionId">
                    <option selected value="0">
                        <i:inline key="yukon.web.modules.adminSetup.list.noDefinition"/>
                    </option>
                    <c:forEach var="definition" items="${listDefinitions}">
                        <option value="${definition.definitionId}"><i:inline key="${definition}"/></option>
                    </c:forEach>
                </select>
                </td>
            </c:if>
            <tags:dynamicTableActionsCell tableId="entryTable" isFirst="true" isLast="true"/>
        </tr>
        <tags:dynamicTableUndoRow columnSpan="${numEntryColumns}" nameKey="undoRow"/>
    </table>

</tags:standardPageFragment>
