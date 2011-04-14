<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags"%>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n"%>

<cti:standardPage module="amr" page="porterResponseMonitor.${mode}">

    <cti:url var="fullErrorCodesURL" value="/spring/support/errorCodes/view"/>

    <i:simplePopup titleKey=".errorCodesPopup" id="errorCodesHelpPopup" on="#errorHelp">
        <div class="largeDialogScrollArea">
        <table id="errorCodes" class="resultsTable">
            <tr>
                <th><i:inline key=".errorCodesPopup.header.code" /></th>
                <th><i:inline key=".errorCodesPopup.header.porter" /></th>
            </tr>
            <c:forEach items="${allErrors}" var="error">
                <tr class="<tags:alternateRow odd="tableCell" even="altTableCell"/>">
                    <td nowrap="nowrap">${error.errorCode}</td>
                    <td>${error.description}</td>
                </tr>
            </c:forEach>
        </table><br>
        <span class="footerLink">
            <i:inline key=".errorCodesVerboseMsg"/>
            <a href="${fullErrorCodesURL}"><i:inline key=".errorCodesSupportLink"/></a>
        </span>
        </div>
    </i:simplePopup>

<script type="text/javascript">

YEvent.observeSelectorClick('.removeRow', function(event) {
    var theRow = event.findElement('tr');
    var rowToDelete = document.createElement('input');
    rowToDelete.type = 'hidden';
    rowToDelete.name = 'rulesToRemove';
    rowToDelete.value = theRow.id.substring(5); //omit "rule_"
    rowToDelete.id =  'deleteInput_' + theRow.id;
    $('updateForm').appendChild(rowToDelete);
    theRow.hide();
    theRow.next().show();
});

YEvent.observeSelectorClick('.undoRemoveBtn', function(event) {
    var theUndoRow = event.findElement('tr');
    $('deleteInput_' + theUndoRow.previous().id).remove();
    theUndoRow.hide();
    theUndoRow.previous().show();
});

YEvent.observeSelectorClick('.addRuleTableRow', function(event) {
	var maxOrder = 0;
    var numRows = $$('.ruleTableRow').length
    for (var i = 0; i < numRows; i++) {
        var order = $('rulesTableBody').down('.ruleOrder', i).value;
	    if (maxOrder < order) {
	        maxOrder = order;
	    }
    }

    var newRow = $('defaultRuleRow').cloneNode(true);
    $('rulesTableBody').appendChild(newRow);

    new Ajax.Request("addRule",{
        parameters: {'monitorId': ${monitorDto.monitorId}, 'maxOrder': maxOrder},
        onSuccess: function(transport) {
            var dummyHolder = document.createElement('div');
            dummyHolder.innerHTML = transport.responseText;
            var replacementRow = $(dummyHolder).down('tr');
            var undoRow = replacementRow.next();
            $('rulesTableBody').replaceChild(replacementRow, newRow);
            $('rulesTableBody').appendChild(undoRow);
        },
        onFailure: function() { 
	        newRow.remove();
        }
    });
});
</script>

	<form:form id="updateForm" commandName="monitorDto"
		action="/spring/amr/porterResponseMonitor/update" method="post">

		<form:hidden path="monitorId" />
        <form:hidden path="stateGroup"/>
		<form:hidden path="evaluatorStatus" />

		<tags:formElementContainer nameKey="sectionHeader">
			<tags:nameValueContainer2 style="border-collapse:separate;border-spacing:5px;">

				<%-- name --%>
				<tags:inputNameValue nameKey=".name" path="name" size="50" maxlength="50" />

                <cti:msg2 var="deviceGroupTitle" key=".popupInfo.deviceGroup.title"/>

                <%-- device group --%>
                <tags:nameValue2 nameKey=".deviceGroup">
                    <cti:deviceGroupHierarchyJson predicates="NON_HIDDEN" var="groupDataJson" />
                    <tags:deviceGroupNameSelector fieldName="groupName"
                        fieldValue="${monitorDto.groupName}" dataJson="${groupDataJson}"
                        linkGroupName="true" />
                    <tags:helpInfoPopup title="${deviceGroupTitle}">
                        <cti:msg2 key=".popupInfo.deviceGroup" htmlEscape="false" />
                    </tags:helpInfoPopup>
                </tags:nameValue2>

                <%-- enable/disable monitoring --%>
                <tags:nameValue2 nameKey=".monitoring">
					<i:inline key="${monitorDto.evaluatorStatus}" />
				</tags:nameValue2>
			</tags:nameValueContainer2>
		</tags:formElementContainer>

		<table style="display: none">
			<tr id="defaultRuleRow">
				<td colspan="6" style="text-align: center">
					<img src="/WebConfig/yukon/Icons/indicator_arrows.gif">
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
                        <th class="errorsColumn"><i:inline key=".rulesTable.header.errors" />
                            <cti:img id="errorHelp" key="help" styleClass="pointer hoverableImage"/>
                        </th>
                        <th class="matchColumn"><i:inline key=".rulesTable.header.matchStyle" /></th>
                        <th class="stateColumn"><i:inline key=".rulesTable.header.state" /></th>
						<th class="ruleRemoveColumn removeColumn"><i:inline key=".rulesTable.header.remove" /></th>
					</tr>
				</thead>
				<tbody id="rulesTableBody">
					<c:forEach var="ruleEntry" items="${monitorDto.rules}">
						<c:set var="key" value="${ruleEntry.key}" />
						<tr id="rule_${key}" class="ruleTableRow">
							<td class="orderColumn">
								<form:hidden path="rules[${key}].ruleId" />
								<form:input path="rules[${key}].ruleOrder" cssClass="ruleOrder" maxlength="2"/>
							</td>
                            <td class="checkBox outcomeColumn">
                                <form:checkbox path="rules[${key}].success"/>
                                <span><i:inline key=".rule.success"/></span>
                            </td>
							<td class="errorsColumn">
                                <form:input path="rules[${key}].errorCodes"/>
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
										<form:option value="${state.liteID}">
											<spring:escapeBody htmlEscape="true">
                                                ${state.stateText}
                                            </spring:escapeBody>
										</form:option>
									</c:forEach>
								</form:select>
							</td>
							<td class="removeColumn">
								<cti:img key="rulesTable.delete" styleClass="removeRow pointer"/>
							</td>
						</tr>
						<tr style="display: none" id="rule_${key}_undo" class="undoRow">
                            <td colspan="1">
                                <cti:img key="rulesTable.delete"/>
                            </td>
							<td colspan="4"><i:inline key=".rulesTable.removedRow"/></td>
							<td colspan="1">
								<span class="undoRemoveBtn"><i:inline key=".rulesTable.undoLink"/></span>
							</td>
						</tr>
					</c:forEach>
				</tbody>
			</table>
			</div>
            <div class="actionArea">
                <cti:button key="rulesTable.add" styleClass="addRuleTableRow"/>
            </div>
		</tags:boxContainer2>

		<%-- update / enable_disable / delete / cancel --%>
		<div class="pageActionArea">
            <cti:button key="update" type="submit"/>

            <c:set var="monitoringKey" value="monitoringEnable"/>
			<c:if test="${monitorDto.evaluatorStatus eq 'ENABLED'}">
                <c:set var="monitoringKey" value="monitoringDisable"/>
			</c:if>

            <cti:button key="${monitoringKey}" type="submit" name="toggleEnabled"/>
			<cti:button id="deleteButton" key="delete"/>
			<tags:confirmDialog nameKey=".deleteConfirmation" argument="${monitorDto.name}" submitName="delete" on="#deleteButton"/>

            <cti:button key="cancel" type="submit" name="cancelToView"/>
		</div>
	</form:form>

</cti:standardPage>
