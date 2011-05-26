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
                <input type="text" name="entries[${entryIndex}].text" id="entries[${entryIndex}].text"/>
                <input type="hidden" name="entries[${entryIndex}].entryId" value="0">
                <input type="hidden" name="entries[${entryIndex}].order" value="${entryIndex + 1}" class="orderField">
                <input type="hidden" name="entries[${entryIndex}].deletion" value="false" class="isDeletionField">
            </td>
            <c:if test="${usesType}">
                <td>
                    <c:choose>
                        <c:when test="${list.settlementType}">
                            <input type="text" name="entries[${entryIndex}].definitionId" id="entries[${entryIndex}].definitionId" />
                        </c:when>
                        <c:otherwise>
                            <select name="entries[${entryIndex}].definitionId" id="entries[${entryIndex}].definitionId">
                                <option selected value="0">
                                    <i:inline key="yukon.web.modules.adminSetup.list.noDefinition"/>
                                </option>
                                <c:forEach var="definition" items="${listDefinitions}">
                                    <option value="${definition.definitionId}"><i:inline key="${definition}"/></option>
                                </c:forEach>
                            </select>
                        </c:otherwise>
                    </c:choose>
                </td>
            </c:if>
            <tags:dynamicTableActionsCell tableId="entryTable" isFirst="true" isLast="true"/>
        </tr>
        <tags:dynamicTableUndoRow columnSpan="${numEntryColumns}" nameKey="undoRow"/>
    </table>

    <script type="text/javascript">
    Event.observe($('entries[${entryIndex}].definitionId'), 'change', typeChanged);
    </script>
</tags:standardPageFragment>
