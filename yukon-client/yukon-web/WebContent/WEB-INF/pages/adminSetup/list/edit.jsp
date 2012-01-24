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

<cti:standardPage module="adminSetup" page="list.EDIT">
<form:form action="save" commandName="list" id="editListForm">
<form:hidden path="listId"/>
<form:hidden path="energyCompanyId"/>

<tags:setFormEditMode mode="${mode}"/>

<script type="text/javascript">

function typeChanged(event) {
    var selectElem = event.currentTarget;
    if (selectElem.selectedIndex != -1) {
        var textElems = jQuery("#"+ selectElem.id.replace('definitionId', 'text').replace("[", "\\[").replace("]", "\\]").replace(".", "\\."));
        if (!textElems.val() || jQuery(selectElem).find("option:contains("+ textElems.val() +")").length) {
            textElems.val(selectElem.options[selectElem.selectedIndex].text);
        }
    }
}

jQuery(document).ready(function() {
    jQuery(document).delegate('select[name^=entries]', 'change', typeChanged);
});
</script>

<tags:nameValueContainer2>
    <tags:nameValue2 nameKey=".where">
        <tags:input path="whereIsList"/>
    </tags:nameValue2>
    <tags:nameValue2 nameKey=".label">
        <tags:input path="selectionLabel"/>
    </tags:nameValue2>
</tags:nameValueContainer2>

<tags:boxContainer2 nameKey="entries" id="selectionListEntries">
    <tags:dynamicTable id="entryTable" items="${list.entries}" nameKey="entries"
        addItemParameters="{'listId': ${list.listId}}">
        <table class="compactResultsTable rowHighlighting">
            <tr>
                <th><i:inline key=".entryText"/></th>
                <c:if test="${usesType}">
                    <th><i:inline key=".definition"/></th>
                </c:if>
                <th class="actions"><i:inline key=".actions"/></th>
            </tr>
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
        </table>
    </tags:dynamicTable>
</tags:boxContainer2>

<div class="pageActionArea">
    <cti:button nameKey="save" name="save" type="submit"/>
    <cti:button id="restoreDefaultBtn" nameKey="restoreDefault"/>
    <cti:msg2 var="listType" key="${list.type}"/>
    <tags:confirmDialog nameKey=".confirmRestoreDefault" argument="${listType}"
        on="#restoreDefaultBtn" submitName="restoreDefault"/>
    <cti:url var="viewUrl" value="view">
        <cti:param name="ecId" value="${list.energyCompanyId}"/>
        <cti:param name="listId" value="${list.listId}"/>
    </cti:url>
    <cti:button nameKey="cancel" href="${viewUrl}"/>
</div>

</form:form>
</cti:standardPage>
