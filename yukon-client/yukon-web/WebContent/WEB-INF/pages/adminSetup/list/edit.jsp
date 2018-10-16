<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="d" tagdir="/WEB-INF/tags/dialog"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n" %>

<c:set var="numEntryColumns" value="2"/>
<c:if test="${usesType}">
    <c:set var="numEntryColumns" value="3"/>
</c:if>

<cti:standardPage module="adminSetup" page="list.EDIT">
<form:form action="save" modelAttribute="list" id="editListForm">
    <cti:csrfToken/>
<form:hidden path="listId"/>
<form:hidden path="energyCompanyId"/>

<tags:setFormEditMode mode="${mode}"/>

<script type="text/javascript">

function typeChanged(event) {
    var selectElem = event.currentTarget;
    if (selectElem.selectedIndex != -1) {
        var textElems = $("#"+ selectElem.id.replace('definitionId', 'text').replace("[", "\\[").replace("]", "\\]").replace(".", "\\."));
        if (!textElems.val() || $(selectElem).find("option:contains("+ textElems.val() +")").length) {
            textElems.val(selectElem.options[selectElem.selectedIndex].text);
        }
    }
}

$(document).ready(function() {
    $(document).on('change', 'select[name^=entries]', typeChanged);
});
</script>

<tags:nameValueContainer2 tableClass="stacked">
    <tags:nameValue2 nameKey=".where">
        <tags:input path="whereIsList"/>
    </tags:nameValue2>
    <tags:nameValue2 nameKey=".label">
        <tags:input path="selectionLabel"/>
    </tags:nameValue2>
</tags:nameValueContainer2>

<tags:sectionContainer2 nameKey="entries" id="selectionListEntries">
    <tags:dynamicTable id="entryTable" items="${list.entries}" nameKey="entries"
        addItemParameters="{'listId': ${list.listId}}">
        <table class="compact-results-table row-highlighting no-stripes">
            <thead>
                <tr>
                    <th><i:inline key=".entryText"/></th>
                    <c:if test="${usesType}">
                        <th><i:inline key=".definition"/></th>
                    </c:if>
                    <th class="actions"><i:inline key=".actions"/></th>
                </tr>
            </thead>
            <tfoot></tfoot>
            <tbody>
                <c:forEach var="entry" varStatus="status" items="${list.entries}">
                    <tr>
                        <td>
                            <tags:input path="entries[${status.index}].text"/>
                            <form:hidden path="entries[${status.index}].entryId"/>
                            <form:hidden path="entries[${status.index}].order" class="orderField"/>
                            <form:hidden path="entries[${status.index}].deletion" class="isDeletionField"/>
                        </td>
                        <c:if test="${usesType}">
                            <td>
                                <c:choose>
                                    <c:when test="${list.yukonSelectionList.settlementType}">
                                        <tags:input path="entries[${status.index}].definitionId"/>
                                    </c:when>
                                    <c:otherwise>
                                        <cti:msg2 var="noDefinitionMsg" key=".noDefinition"/>
                                        <tags:selectWithItems path="entries[${status.index}].definitionId"
                                            items="${listDefinitions}" itemValue="definitionId"
                                            defaultItemLabel="${noDefinitionMsg}" defaultItemValue="0"/>
                                    </c:otherwise>
                                </c:choose>
                            </td>
                        </c:if>
                        <tags:dynamicTableActionsCell tableId="entryTable"
                            isFirst="${status.first}" isLast="${status.last}"/>
                    </tr>
                    <tags:dynamicTableUndoRow columnSpan="${numEntryColumns}" nameKey="undoRow"/>
                </c:forEach>
            </tbody>
        </table>
    </tags:dynamicTable>
</tags:sectionContainer2>

<div class="page-action-area">
    <cti:button nameKey="save" name="save" type="submit" classes="primary action"/>
    <cti:button id="restoreDefaultBtn" nameKey="restoreDefault" name="restoreDefault" type="submit"/>
    <cti:msg2 var="listType" key="${list.type}"/>
    <d:confirm on="#restoreDefaultBtn" nameKey="confirmRestoreDefault" argument="${listType}"/>
    <cti:url var="viewUrl" value="view">
        <cti:param name="ecId" value="${list.energyCompanyId}"/>
        <cti:param name="listId" value="${list.listId}"/>
    </cti:url>
    <cti:button nameKey="cancel" href="${viewUrl}"/>
</div>

</form:form>
</cti:standardPage>
