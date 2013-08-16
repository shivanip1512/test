<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="d" tagdir="/WEB-INF/tags/dialog" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<cti:standardPage module="amr" page="porterResponseMonitor.${mode}">

    <cti:url var="fullErrorCodesURL" value="/support/errorCodes/view"/>

    <i:simplePopup titleKey=".errorCodesPopup" id="errorCodesHelpPopup" on="#errorHelp" options="{'height': 600}">
        <table id="errorCodes" class="compactResultsTable stacked">
            <thead>
            <tr>
                <th><i:inline key=".errorCodesPopup.header.code" /></th>
                <th><i:inline key=".errorCodesPopup.header.porter" /></th>
            </tr>
            </thead>
            <tfoot></tfoot>
            <tbody>
            <c:forEach items="${allErrors}" var="error">
                <tr>
                    <td>${error.errorCode}</td>
                    <td>${error.description}</td>
                </tr>
            </c:forEach>
            </tbody>
        </table>
        <span class="footerLink">
            <i:inline key=".errorCodesVerboseMsg"/>
            <a href="${fullErrorCodesURL}"><i:inline key=".errorCodesSupportLink"/></a>
        </span>
    </i:simplePopup>

<script type="text/javascript">

jQuery(document).on('click', '.f-remove', function(event) {
    var theRow = jQuery(event.currentTarget).closest('tr');
    var rowToDelete = document.createElement('input');
    var rowId = theRow.attr('id');
    
    rowToDelete.type = 'hidden';
    rowToDelete.name = 'rulesToRemove';
    rowToDelete.value = rowId.substring(5); //omit "rule_"
    rowToDelete.id =  'deleteInput_' + rowId;
    jQuery('#updateForm').append(rowToDelete);
    theRow.hide();
    theRow.next().show();
});

jQuery(document).on('click', '.undo', function(event) {
    var theUndoRow = jQuery(event.currentTarget).closest('tr');
    jQuery('deleteInput_' + theUndoRow.prev().attr('id')).remove();
    theUndoRow.hide();
    theUndoRow.prev().show();
});

jQuery(document).on('click', '.f-add-rule', function(event) {
    var maxOrder = 0;
    var tableBody = jQuery('#rulesTableBody');
    
    jQuery('#rulesTableBody .ruleOrder').each(function(idx, elem) {
        var order = jQuery(elem).val();
        if (maxOrder < order) maxOrder = order;
    });
    
    var newRow = jQuery('#defaultRuleRow').clone();
    tableBody.append(newRow);

    jQuery.get(
        'addRule', 
        {'monitorId': '${monitorDto.monitorId}', 'maxOrder': maxOrder}
    ).done(function (data) {
        var dummyHolder = jQuery('<div>');
        dummyHolder.html(data);
        var replacementRow = dummyHolder.find('.ruleTableRow');
        var undoRow = replacementRow.next();
        newRow.replaceWith(replacementRow);
        tableBody.append(undoRow);
    }).fail(function() {
        newRow.remove();
    });
    
});
</script>

    <form:form id="updateForm" 
        commandName="monitorDto"
        action="/amr/porterResponseMonitor/update" 
        method="post">

        <form:hidden path="monitorId" />
        <form:hidden path="stateGroup"/>
        <form:hidden path="evaluatorStatus" />

        <div class="stacked">
            <tags:nameValueContainer2 style="border-collapse:separate;border-spacing:5px;">

                <%-- name --%>
                <tags:inputNameValue nameKey=".name" path="name" size="50" maxlength="50" />

                <cti:msg2 var="deviceGroupTitle" key=".popupInfo.deviceGroup.title"/>

                <%-- device group --%>
                <tags:nameValue2 nameKey=".deviceGroup">
                    <cti:deviceGroupHierarchyJson predicates="NON_HIDDEN" var="groupDataJson" />
                    <tags:deviceGroupNameSelector fieldName="groupName"
                        fieldValue="${monitorDto.groupName}" 
                        dataJson="${groupDataJson}"
                        linkGroupName="true" />
                </tags:nameValue2>

                <%-- enable/disable monitoring --%>
                <tags:nameValue2 nameKey=".monitoring">
                    <i:inline key="${monitorDto.evaluatorStatus}" />
                </tags:nameValue2>
            </tags:nameValueContainer2>
        </div>

        <table style="display: none">
            <tr id="defaultRuleRow">
                <td colspan="5" style="text-align: center">
                    <img src="/WebConfig/yukon/Icons/spinner.gif">
                </td>
            </tr>
        </table>

        <tags:boxContainer2 nameKey="rulesTable" hideEnabled="false" showInitially="true" styleClass="fixedMediumWidth">
            <div class="smallDialogScrollArea">
            <table id="rulesTable" class="compactResultsTable">
                <thead>
                    <tr>
                        <th class="orderColumn"><i:inline key=".rulesTable.header.ruleOrder" /></th>
                        <th class="outcomeColumn"><i:inline key=".rulesTable.header.outcome" /></th>
                        <th class="errorsColumn">
                            <i:inline key=".rulesTable.header.errors" />
                            <cti:icon id="errorHelp" nameKey="help" icon="icon-help" classes="fn"/>
                        </th>
                        <th class="matchColumn"><i:inline key=".rulesTable.header.matchStyle" /></th>
                        <th class="stateColumn"><i:inline key=".rulesTable.header.state" /></th>
                    </tr>
                </thead>
                <tbody id="rulesTableBody">
                    <c:forEach var="ruleEntry" items="${monitorDto.rules}">
                        <c:set var="key" value="${ruleEntry.key}" />
                        <tr id="rule_${key}" class="ruleTableRow">
                            <td class="orderColumn">
                                <form:hidden path="rules[${key}].ruleId" />
                                <form:input path="rules[${key}].ruleOrder" cssClass="ruleOrder" maxlength="2" size="2"/>
                            </td>
                            <td class="checkBox outcomeColumn">
                                <label>
                                    <form:checkbox path="rules[${key}].success"/>
                                    <i:inline key=".rule.success"/>
                                </label>
                            </td>
                            <td class="errorsColumn">
                                <form:input path="rules[${key}].errorCodes" maxlength="4" size="4"/>
                            </td>
                            <td class="matchColumn">
                                <form:select path="rules[${key}].matchStyle">
                                    <c:forEach var="style" items="${matchStyleChoices}">
                                        <form:option value="${style}">
                                            <i:inline key="${style.formatKey}" />
                                        </form:option>
                                    </c:forEach>
                                </form:select>
                            </td>
                            <td class="stateColumn">
                                <form:select path="rules[${key}].state">
                                    <c:forEach var="state" items="${monitorDto.stateGroup.statesList}">
                                        <form:option value="${state.liteID}">${fn:escapeXml(state.stateText)}</form:option>
                                    </c:forEach>
                                </form:select>
                                <cti:button classes="f-remove fr" icon="icon-cross" renderMode="buttonImage"/>
                            </td>
                        </tr>
                        <tr style="display: none" id="rule_${key}_undo" class="undo-row">
                            <td colspan="5">
                                <i:inline key=".rulesTable.removedRow"/>
                                <a href="javascript:void(0)" class="undo"><i:inline key=".rulesTable.undoLink"/></a>
                            </td>
                        </tr>
                    </c:forEach>
                </tbody>
            </table>
            </div>
            <div class="actionArea">
                <cti:button nameKey="rulesTable.add" classes="f-add-rule" icon="icon-add"/>
            </div>
        </tags:boxContainer2>

        <%-- update / enable_disable / delete / cancel --%>
        <div class="pageActionArea">
            <cti:button nameKey="update" type="submit" busy="true"  classes="primary action f-disableAfterClick" data-disable-group="actionButtons"/>

            <c:set var="monitoringKey" value="enable"/>
            <c:if test="${monitorDto.evaluatorStatus eq 'ENABLED'}">
                <c:set var="monitoringKey" value="disable"/>
            </c:if>
            <cti:button nameKey="${monitoringKey}" type="submit" name="toggleEnabled" busy="true" classes="f-disableAfterClick" data-disable-group="actionButtons"/>

            <cti:button id="deleteButton" nameKey="delete" name="delete" type="submit" busy="true" data-disable-group="actionButtons"/>
            <d:confirm on="#deleteButton" nameKey="confirmDelete" argument="${monitorDto.name}"/>
            <cti:url value="/amr/porterResponseMonitor/viewPage" var="viewUrl">
                <cti:param name="monitorId" value="${monitorDto.monitorId}"/>
            </cti:url>
            <cti:button nameKey="cancel" type="button" href="${viewUrl}" busy="true" classes="f-disableAfterClick" data-disable-group="actionButtons" />
        </div>
    </form:form>

</cti:standardPage>
