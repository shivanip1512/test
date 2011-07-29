<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring" %>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@ taglib tagdir="/WEB-INF/tags" prefix="tags" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n" %>

<tags:standardPopup pageName="ivvc" module="capcontrol" popupName="zoneWizard">

<script type="text/javascript">
    getExtraArgs = function(url, addRegPhase) {
        var extraParameters = {};
        if (addRegPhase) {
            <c:if test="${zoneDto.zoneType !=  threePhase && !empty zoneDto.regulator.phase}">
                extraParameters.phase = '${zoneDto.regulator.phase}';
            </c:if>
        }
        return {'url' : url, 'extraParameters' : extraParameters};
    };
    
    addBankHandler = function (selectedPaoInfo, picker) {
        var extraArgs = getExtraArgs('/spring/capcontrol/ivvc/wizard/addCapBank', false);
        for(var i = 0; i < selectedPaoInfo.length; i++) {
            var paoId = selectedPaoInfo[i].paoId;
            extraArgs.extraParameters.id = paoId;
            picker.excludeIds.push(paoId);
            bankTable.addItem(null, extraArgs);   
        }
        picker.clearEntireSelection();
    };

    addPointHandler = function (selectedPointInfo, picker) {
        var extraArgs = getExtraArgs('/spring/capcontrol/ivvc/wizard/addVoltagePoint', true);
        for(var i = 0; i < selectedPointInfo.length; i++) {
            var pointId = selectedPointInfo[i].pointId;
            extraArgs.extraParameters.id = pointId;
            picker.excludeIds.push(pointId);
            pointTable.addItem(null, extraArgs);
        }
        picker.clearEntireSelection();
    };
    
    updateRegPickerExcludes = function(selectedItems, picker) {
        if (picker != voltageThreePhaseRegulatorPicker${zoneDto.zoneId}A) {
            if (voltageThreePhaseRegulatorPicker${zoneDto.zoneId}A.getSelected() == selectedItems[0].paoId) {
                voltageThreePhaseRegulatorPicker${zoneDto.zoneId}A.clearSelected();
            }
        }
        if (picker != voltageThreePhaseRegulatorPicker${zoneDto.zoneId}B) {
            if (voltageThreePhaseRegulatorPicker${zoneDto.zoneId}B.getSelected() == selectedItems[0].paoId) {
                voltageThreePhaseRegulatorPicker${zoneDto.zoneId}B.clearSelected();
            }
        }
        if (picker != voltageThreePhaseRegulatorPicker${zoneDto.zoneId}C) {
            if (voltageThreePhaseRegulatorPicker${zoneDto.zoneId}C.getSelected() == selectedItems[0].paoId) {
                voltageThreePhaseRegulatorPicker${zoneDto.zoneId}C.clearSelected();
            }
        }
    };
    
    cancelZoneWizard = function() {
        $('zoneWizardPopup').hide();
    };
    
    backToTypeSelect = function() {
        submitFormViaAjax('zoneWizardPopup', 'zoneDetailsForm', '/spring/capcontrol/ivvc/wizard/wizardParentSelected', false);
    };

    zoneSubmit = function() {
        submitFormViaAjax('zoneWizardPopup', 'zoneDetailsForm', null);
    };

</script>

<tags:setFormEditMode mode="${mode}"/>

<cti:displayForPageEditModes modes="EDIT">
	<cti:url var="action"  value="/spring/capcontrol/ivvc/wizard/updateZone"/>
</cti:displayForPageEditModes>
<cti:displayForPageEditModes modes="CREATE">
	<cti:url var="action" value="/spring/capcontrol/ivvc/wizard/createZone"/>
</cti:displayForPageEditModes>

<form:form id="zoneDetailsForm" commandName="zoneDto" action="${action}" >
	<form:hidden path="zoneId"/>
    <form:hidden path="parentId"/>
    <form:hidden path="substationBusId" id="selectedBusId"/>
    <input type="hidden" name="zoneType" value="${zoneDto.zoneType}"/>
	
	<span id="errorOnPage" style="display:none" class="errorIndicator">
		<i:inline key=".error.missingFields"/>
	</span>

	<tags:nameValueContainer2>
		<%-- Zone Name --%>
		<tags:inputNameValue path="name" nameKey=".label.name" size="25"/>

        <%-- Zone Type --%>
        <tags:nameValue2 nameKey=".label.zoneType">
            <span class="disabledRow">
                <c:choose>
                    <c:when test="${zoneDto.zoneType == singlePhase}">
                        <i:inline key="${zoneDto.zoneType}"/>:&nbsp;<i:inline key="${zoneDto.regulator.phase}"/>
                    </c:when>
                    <c:otherwise>
                        <i:inline key="${zoneDto.zoneType}"/>
                    </c:otherwise>
                </c:choose>
            </span>
        </tags:nameValue2>

        <%-- Parent Zone --%>
        <tags:nameValue2 nameKey=".label.parentZone">
            <span id="parentZoneName" class="disabledRow">
                <c:choose>
                    <c:when test="${zoneDto.parentId == null}">
                        <spring:escapeBody htmlEscape="true"><i:inline key="yukon.web.defaults.dashes"/></spring:escapeBody>
                    </c:when>
                    <c:otherwise>
                        <c:choose>
                            <c:when test="${parentZone.zoneType == singlePhase}">
                                <spring:escapeBody htmlEscape="true">${parentZone.name} - <i:inline key="${parentZone.zoneType}"/>: 
                                <i:inline key="${parentZone.regulator.phase}"/></spring:escapeBody>
                            </c:when>
                            <c:otherwise>
                                <spring:escapeBody htmlEscape="true">${parentZone.name} - <i:inline key="${parentZone.zoneType}"/></spring:escapeBody>
                            </c:otherwise>
                        </c:choose>
                    </c:otherwise>
                </c:choose>
            </span>
        </tags:nameValue2>

        <%-- Substation Bus --%>
        <tags:nameValue2 nameKey=".label.substationBus">
            <span id="selectedBusName" class="disabledRow">
                <spring:escapeBody htmlEscape="true">${subBusName}</spring:escapeBody>
            </span>
        </tags:nameValue2>

        <c:choose>
    		<%-- Regulator Selection - Gang --%>
            <c:when test="${zoneDto.zoneType ==  gangOperated}">
        		<tags:nameValue2 nameKey=".label.regulator">
                    <tags:bind path="regulator.regulatorId">
            			<tags:pickerDialog 	id="voltageGangRegulatorPicker${zoneDto.zoneId}" 
            				type="availableVoltageRegulatorGangPicker" 
                            destinationFieldName="regulator.regulatorId"
                            initialId="${zoneDto.regulator.regulatorId}"
                            selectionProperty="paoName"
                            linkType="selection"
                            extraArgs="${zoneDto.zoneId}"
                            useInitialIdsIfEmpty="true"
            				multiSelectMode="false"
            				immediateSelectMode="true">
            			</tags:pickerDialog>
                    </tags:bind>
        		</tags:nameValue2>
            </c:when>
    
            <%-- Regulator Selection - Three Phase --%>
            <c:when test="${zoneDto.zoneType ==  threePhase}">
                <c:forEach items="${zoneDto.regulators}" var="regulator">
                    <c:set var="phaseKey" value="${regulator.key}"/>
                    <input type="hidden" name="regulators[${phaseKey}].phase" value="${phaseKey}"/>
                    <tags:nameValue2 nameKey=".label.regulator.${phaseKey}">
                        <tags:bind path="regulators[${phaseKey}].regulatorId">
                            <tags:pickerDialog  id="voltageThreePhaseRegulatorPicker${zoneDto.zoneId}${phaseKey}"
                                type="availableVoltageRegulatorPhasePicker" 
                                destinationFieldName="regulators[${phaseKey}].regulatorId"
                                initialId="${zoneDto.regulators[phaseKey].regulatorId}"
                                selectionProperty="paoName"
                                linkType="selection"
                                extraArgs="${zoneDto.zoneId}"
                                useInitialIdsIfEmpty="true"
                                multiSelectMode="false"
                                immediateSelectMode="true" 
                                allowEmptySelection="true">
                            </tags:pickerDialog>
                        </tags:bind>
                    </tags:nameValue2>
                </c:forEach>
            </c:when>

            <%-- Regulator Selection - Single Phase --%>
            <c:when test="${zoneDto.zoneType == singlePhase}">
                <input type="hidden" name="regulator.phase" value="${zoneDto.regulator.phase}"/>
                <tags:nameValue2 nameKey=".label.regulator.${zoneDto.regulator.phase}">
                    <tags:bind path="regulator.regulatorId">
                        <tags:pickerDialog id="voltageSinglePhaseRegulatorPicker${zoneDto.zoneId}" 
                            type="availableVoltageRegulatorPhasePicker" 
                            destinationFieldName="regulator.regulatorId"
                            initialId="${zoneDto.regulator.regulatorId}"
                            selectionProperty="paoName"
                            linkType="selection"
                            extraArgs="${zoneDto.zoneId}"
                            useInitialIdsIfEmpty="true"
                            multiSelectMode="false"
                            immediateSelectMode="true">
                        </tags:pickerDialog>
                    </tags:bind>
                </tags:nameValue2>
            </c:when>
        </c:choose>

        <%-- Graph Start Position --%>
        <tags:inputNameValue path="graphStartPosition" nameKey=".label.graphStartPosition" size="2"/>

	</tags:nameValueContainer2>
    
    <table>
        <tr>
            <c:if test="${zoneDto.zoneType != singlePhase}">
                <td>
                	<tags:boxContainer2 nameKey="assignedVoltageDevice" hideEnabled="false" showInitially="true" styleClass="zoneWizardCapBanks">
                        <tags:dynamicTable items="${zoneDto.bankAssignments}" nameKey="dynamicTable"
                            id="bankTable" addButtonClass="bankAddItem">
                        <div class="zoneWizardTableScrollArea">
            			<table class="compactResultsTable">
            				<thead>
            					<tr>
            						<th><i:inline key=".table.bank.name"/></th>
            						<th><i:inline key=".table.bank.device"/></th>
            						<th><i:inline key=".table.position"/></th>
            						<th><i:inline key=".table.distance"/></th>
            						<th class="removeColumn"><i:inline key=".table.remove"/></th>
            					</tr>
            				</thead>
                            <tbody>
            					<c:forEach var="row" varStatus="status" items="${zoneDto.bankAssignments}">
            						<tr>
            							<td>
            								<form:hidden path="bankAssignments[${status.index}].id" id="bankAssignments[${status.index}].id"/>
            								<form:hidden path="bankAssignments[${status.index}].name" htmlEscape="true"/>
            								<form:hidden path="bankAssignments[${status.index}].device" htmlEscape="true"/>
                                            <form:hidden path="bankAssignments[${status.index}].deletion" class="isDeletionField"/>
            								<spring:escapeBody htmlEscape="true">${row.name}</spring:escapeBody>
            							</td>
            							<td>
            								<spring:escapeBody htmlEscape="true">${row.device}</spring:escapeBody>
            							</td>
            							<td>
            								<tags:input path="bankAssignments[${status.index}].graphPositionOffset" size="1"/>
                                        </td>
            							<td>
            								<tags:input path="bankAssignments[${status.index}].distance" size="3"/>
            							</td>
                                        <tags:dynamicTableActionsCell tableId="bankTable"
                                            isFirst="${status.first}" isLast="${status.last}" skipMoveButtons="true"/>
            						</tr>
                                    <tags:dynamicTableUndoRow columnSpan="5" nameKey="undoRow"/>
            					</c:forEach>
                            </tbody>
            			</table>
                        </div>
                        </tags:dynamicTable>
                		<div class="actionArea">
                			<tags:pickerDialog 	id="bankPicker" 
                				type="availableCapBankPicker"
                				multiSelectMode="true"
                				endAction="addBankHandler"
                                extraArgs="${zoneDto.substationBusId}"/>
                            <script type="text/javascript">
                                bankPicker.excludeIds = [
                                    <c:forEach var="bank" varStatus="status" items="${zoneDto.bankAssignments}">
                                        ${bank.id}<c:if test="${!status.last}">,</c:if>
                                    </c:forEach> ];
                            </script>
                		</div>
                	</tags:boxContainer2>
                </td>
            </c:if>
            <td>
            	<tags:boxContainer2 nameKey="assignedVoltagePoint" hideEnabled="false" showInitially="true" styleClass="zoneWizardVoltagePoints">
                    <c:set var="addItemParameters" value="{'zoneType': '${zoneDto.zoneType}'}"/>
                    <tags:dynamicTable items="${zoneDto.pointAssignments}" nameKey="dynamicTable"
                        id="pointTable" addItemParameters="${addItemParameters}" addButtonClass="pointAddItem">
                    <div class="zoneWizardTableScrollArea">
            		<table class="compactResultsTable">
            			<thead>
            				<tr>
            					<th><i:inline key=".table.point.name"/></th>
            					<th><i:inline key=".table.point.device"/></th>
                                <th><i:inline key=".table.phase"/></th>
            					<th><i:inline key=".table.position"/></th>
            					<th><i:inline key=".table.distance"/></th>
            					<th class="removeColumn"><i:inline key=".table.remove"/></th>
            				</tr>
            			</thead>
            			<tbody>
            				<c:forEach var="row" varStatus="status" items="${zoneDto.pointAssignments}">
            					<tr>
            						<td>
            							<form:hidden path="pointAssignments[${status.index}].id" id="pointAssignments[${status.index}].id"/>
            							<form:hidden path="pointAssignments[${status.index}].name" htmlEscape="true"/>
            							<form:hidden path="pointAssignments[${status.index}].device" htmlEscape="true"/>
                                        <form:hidden path="pointAssignments[${status.index}].deletion" class="isDeletionField"/>
            							<spring:escapeBody htmlEscape="true">${row.name}</spring:escapeBody>
            						</td>
            						<td>
            							<spring:escapeBody htmlEscape="true">${row.device}</spring:escapeBody>
            						</td>
                                    <td>
                                        <c:choose>
                                            <c:when test="${phaseUneditable}">
                                                <form:hidden path="pointAssignments[${status.index}].phase"/>
                                                <i:inline key="${zoneDto.regulator.phase}" />
                                            </c:when>
                                            <c:otherwise>
                                                <form:select path="pointAssignments[${status.index}].phase">
                                                    <c:forEach var="phase" items="${phases}">
                                                        <form:option value="${phase}">
                                                            <i:inline key="${phase}" />
                                                        </form:option>
                                                    </c:forEach>
                                                </form:select>
                                            </c:otherwise>
                                        </c:choose>
                                    </td>
            						<td>
                                        <tags:input path="pointAssignments[${status.index}].graphPositionOffset" size="1"/>
                                    </td>
            						<td>
            							<tags:input path="pointAssignments[${status.index}].distance" size="3"/>
            						</td>
                                    <tags:dynamicTableActionsCell tableId="pointTable"
                                        isFirst="${status.first}" isLast="${status.last}" skipMoveButtons="true"/>
            					</tr>
                                <tags:dynamicTableUndoRow columnSpan="6" nameKey="undoRow"/>
            				</c:forEach>
            			</tbody>
            		</table>
                    </div>
                    </tags:dynamicTable>
            		<div class="actionArea">
            			<tags:pickerDialog 	id="pointPicker" 
            				type="voltPointPicker"
            				multiSelectMode="true"
            				endAction="addPointHandler"/>
                        <script type="text/javascript">
                            pointPicker.excludeIds = [
                                <c:forEach var="pointId" varStatus="status" items="${usedPointIds}">
                                    ${pointId}<c:if test="${!status.last}">,</c:if>
                                </c:forEach> ];
                        </script>
            		</div>
            	</tags:boxContainer2>
        	</td>
        </tr>
    </table>

	<div class="actionArea">
		<cti:displayForPageEditModes modes="EDIT">
			<cti:button key="save" onclick="zoneSubmit()"/>
            <cti:button key="cancel" onclick="cancelZoneWizard()"/>
		</cti:displayForPageEditModes>
		<cti:displayForPageEditModes modes="CREATE">
            <cti:button key="back" onclick="backToTypeSelect()"/>
            <cti:button key="create" onclick="zoneSubmit()"/>
		</cti:displayForPageEditModes>
	</div>
    
</form:form>

<c:if test="${zoneDto.zoneType ==  threePhase}">
    <script type="text/javascript">
        voltageThreePhaseRegulatorPicker${zoneDto.zoneId}A.endAction = updateRegPickerExcludes;
        voltageThreePhaseRegulatorPicker${zoneDto.zoneId}B.endAction = updateRegPickerExcludes;
        voltageThreePhaseRegulatorPicker${zoneDto.zoneId}C.endAction = updateRegPickerExcludes;
    </script>
</c:if>

</tags:standardPopup>
