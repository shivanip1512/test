<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring" %>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@ taglib tagdir="/WEB-INF/tags" prefix="tags" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n" %>

<tags:standardPopup pageName="ivvc" module="capcontrol" popupName="zoneWizard">

<script type="text/javascript">
    zoneId = null;
    <c:if test="${!empty zoneDto.zoneId}">
        zoneId = ${zoneDto.zoneId};
    </c:if>

    regPhase = null;
    <c:if test="${zoneDto.zoneType !=  threePhase}">
        <c:if test="${!empty zoneDto.regulator.phase}">
            regPhase = '${zoneDto.regulator.phase}';
        </c:if>
    </c:if>

    addBankHandler = function (selectedPaoInfo) {
        var url = '/spring/capcontrol/ivvc/wizard/addCapBank';
        var index = $$('.bankRowCounter').length;
        
        for(var i = 0; i < selectedPaoInfo.length; i++) {
            var paoId = selectedPaoInfo[i].paoId;
            addRow(url,paoId,index++,'bank');   
        }
        
        return true;
    };
    
    addPointHandler = function (selectedPointInfo) {
        var url = '/spring/capcontrol/ivvc/wizard/addVoltagePoint';
        var index = $$('.pointRowCounter').length;
        
        for(var i = 0; i < selectedPointInfo.length; i++) {
            var pointId = selectedPointInfo[i].pointId;     
            addRow(url,pointId,index++,'point');
        }
        
        return true;
    };
    
    addRow = function (url,id,index,rowType) {
        var newRow = $('defaultRow').cloneNode(true);
        $(rowType+'TableBody').appendChild(newRow);
        
        var parameters = {'id': id, 'index': index, 'zoneType': '${zoneDto.zoneType}'};
        if (zoneId) {
            parameters.zoneId = zoneId;
        }
        if (regPhase) {
            parameters.phase = regPhase;
        }
        new Ajax.Request(url,{
            'parameters': parameters,
            'onSuccess': function(transport) {
                var dummyHolder = document.createElement('div');
                dummyHolder.innerHTML = transport.responseText;
                var replacementRow = $(dummyHolder).getElementsBySelector('tr')[0];
                $(rowType+'TableBody').replaceChild(replacementRow, newRow);
            },
            'onFailure': function() { 
                newRow.remove();
            }
        });
    };

    removeTableRow = function (rowType, rowId) {
        var rowToDelete = document.createElement('input');
        rowToDelete.type = 'hidden';
        //rowType will be "bank" or "point"
        rowToDelete.name = rowType + 'sToRemove';
        rowToDelete.value = rowId;
        rowToDelete.id =  'deleteInput_' + rowId;
        $('zoneDetailsForm').appendChild(rowToDelete);
        $(rowType +'_'+ rowId).hide();
        var undoRow = $(rowType +'_'+ rowId + '_undo');
        if (undoRow) {
            undoRow.show();
        }
    };
    
    undoRemoveTableRow = function (rowType, rowId) {
        $('deleteInput_' + rowId).remove();
        $(rowType +'_'+ rowId).show();
        $(rowType +'_'+ rowId + '_undo').hide();
    };
    
    updateRegPickerExcludes = function(selectedItems, picker) {
        if (picker != voltageThreePhaseARegulatorPicker) {
            if (voltageThreePhaseARegulatorPicker.getSelected() == selectedItems[0].paoId) {
                voltageThreePhaseARegulatorPicker.clearSelected();
            }
        }
        if (picker != voltageThreePhaseBRegulatorPicker) {
            if (voltageThreePhaseBRegulatorPicker.getSelected() == selectedItems[0].paoId) {
                voltageThreePhaseBRegulatorPicker.clearSelected();
            }
        }
        if (picker != voltageThreePhaseCRegulatorPicker) {
            if (voltageThreePhaseCRegulatorPicker.getSelected() == selectedItems[0].paoId) {
                voltageThreePhaseCRegulatorPicker.clearSelected();
            }
        }
    }
    
    cancelZoneWizard = function() {
        $('zoneWizardPopup').hide();
    }
    
    backToTypeSelect = function() {
        submitFormViaAjaxWithSkipShow('zoneWizardPopup', 'zoneDetailsForm', '/spring/capcontrol/ivvc/wizard/wizardParentSelected', null, false);
    }

    zoneSubmit = function() {
        submitFormViaAjax('zoneWizardPopup', 'zoneDetailsForm', null);
    }

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
                        <i:inline key="${zoneDto.zoneType}"/>:&nbsp<i:inline key="${zoneDto.regulator.phase}"/>
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
        		<tags:nameValue2 nameKey=".label.regulator" rowId="gangRow">
                    <tags:bind path="regulator.regulatorId">
            			<tags:pickerDialog 	id="voltageGangRegulatorPicker" 
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
                <input type="hidden" name="regulatorA.phase" value="${phaseA}"/>
                <input type="hidden" name="regulatorB.phase" value="${phaseB}"/>
                <input type="hidden" name="regulatorC.phase" value="${phaseC}"/>
                <tags:nameValue2 nameKey=".label.regulatorA" rowId="phaseARow">
                    <tags:bind path="regulatorA.regulatorId">
                        <tags:pickerDialog  id="voltageThreePhaseARegulatorPicker" 
                            type="availableVoltageRegulatorPhasePicker" 
                            destinationFieldName="regulatorA.regulatorId"
                            initialId="${zoneDto.regulatorA.regulatorId}"
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
                <tags:nameValue2 nameKey=".label.regulatorB" rowId="phaseBRow">
                    <tags:bind path="regulatorB.regulatorId">
                        <tags:pickerDialog  id="voltageThreePhaseBRegulatorPicker" 
                            type="availableVoltageRegulatorPhasePicker" 
                            destinationFieldName="regulatorB.regulatorId"
                            initialId="${zoneDto.regulatorB.regulatorId}"
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
                <tags:nameValue2 nameKey=".label.regulatorC" rowId="phaseCRow">
                    <tags:bind path="regulatorC.regulatorId">
                        <tags:pickerDialog  id="voltageThreePhaseCRegulatorPicker" 
                            type="availableVoltageRegulatorPhasePicker" 
                            destinationFieldName="regulatorC.regulatorId"
                            initialId="${zoneDto.regulatorC.regulatorId}"
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
            </c:when>

            <%-- Regulator Selection - Single Phase --%>
            <c:when test="${zoneDto.zoneType == singlePhase}">
                <input type="hidden" name="regulator.phase" value="${zoneDto.regulator.phase}"/>
                <c:choose>
                    <c:when test="${zoneDto.regulator.phase == phaseA}">
                        <c:set var="regKey" value=".label.regulatorA"/>
                        <c:set var="regNameRowId" value="phaseARow"/>
                    </c:when>
                    <c:when test="${zoneDto.regulator.phase == phaseB}">
                        <c:set var="regKey" value=".label.regulatorB"/>
                        <c:set var="regNameRowId" value="phaseBRow"/>
                    </c:when>
                    <c:when test="${zoneDto.regulator.phase == phaseC}">
                        <c:set var="regKey" value=".label.regulatorC"/>
                        <c:set var="regNameRowId" value="phaseCRow"/>
                    </c:when>
                </c:choose>
                <tags:nameValue2 nameKey="${regKey}" rowId="${regNameRowId}">
                    <tags:bind path="regulator.regulatorId">
                        <tags:pickerDialog id="voltageSinglePhaseRegulatorPicker" 
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
    
	<table style="display:none">
		<tr id="defaultRow">
			<td colspan="5" style="text-align: center"><img src="/WebConfig/yukon/Icons/indicator_arrows.gif"></td>
		</tr>
	</table>
    <table>
        <tr>
            <c:if test="${zoneDto.zoneType != singlePhase}">
                <td>
                	<tags:boxContainer2 nameKey="assignedVoltageDevice" hideEnabled="false" showInitially="true" styleClass="zoneWizardCapBanks">
                        <div class="zoneWizardTableScrollArea">
            			<table id="bankTable" class="compactResultsTable">
            				<thead>
            					<tr>
            						<th><i:inline key=".table.bank.name"/></th>
            						<th><i:inline key=".table.bank.device"/></th>
            						<th><i:inline key=".table.position"/></th>
            						<th><i:inline key=".table.distance"/></th>
            						<th class="removeColumn"><i:inline key=".table.remove"/></th>
            					</tr>
            				</thead>
            				<tbody id="bankTableBody">
            					<c:forEach var="row" varStatus="status" items="${zoneDto.bankAssignments}">
            						<tr id="${row.type}_${row.id}" class="bankRowCounter">
            							<td>
            								<form:hidden path="bankAssignments[${status.index}].id" id="bankAssignments[${status.index}].id"/>
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
            							<td class="removeColumn" >
            								<cti:img key="delete" href="javascript:removeTableRow('${row.type}','${row.id}')"/>
            							</td>
            						</tr>
            						<tr style="display: none" id="${row.type}_${row.id}_undo" class="undoRow">
            							<td colspan="4" align="center">
            								${row.name} will be removed
            							</td>
            							<td colspan="1" align="center">
            								<a href="javascript:undoRemoveTableRow('${row.type}','${row.id}')">Undo</a>
            							</td>
            						</tr>
            					</c:forEach>
            				</tbody>
            			</table>
                        </div>
                		<div class="actionArea">
                			<tags:pickerDialog 	id="bankPicker" 
                				type="availableCapBankPicker"
                				multiSelectMode="true"
                				endAction="addBankHandler"
                				linkType="button" nameKey="add"
                                extraArgs="${zoneDto.substationBusId}"/>
                		</div>
                	</tags:boxContainer2>
                </td>
            </c:if>
            <td>
            	<tags:boxContainer2 nameKey="assignedVoltagePoint" hideEnabled="false" showInitially="true" styleClass="zoneWizardVoltagePoints">
                    <div class="zoneWizardTableScrollArea">
            		<table id="pointTable" class="compactResultsTable">
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
            			<tbody id="pointTableBody">
            				<c:forEach var="row" varStatus="status" items="${zoneDto.pointAssignments}">
            					<tr id="${row.type}_${row.id}" class="pointRowCounter">
            						<td>
            							<form:hidden path="pointAssignments[${status.index}].id" id="pointAssignments[${status.index}].id"/>
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
            						<td class="removeColumn">
            							<cti:img key="delete" href="javascript:removeTableRow('${row.type}','${row.id}')"/>
            						</td>
            					</tr>
            					<tr style="display: none" id="${row.type}_${row.id}_undo" class="undoRow">
            						<td colspan="5" align="center">
            							${row.name} will be removed
            						</td>
            						<td colspan="1" align="center">
            							<a href="javascript:undoRemoveTableRow('${row.type}','${row.id}')">Undo</a>
            						</td>
            					</tr>
            				</c:forEach>
            			</tbody>
            		</table>
                    </div>
            		<div class="actionArea">
            			<tags:pickerDialog 	id="pointPicker" 
            				type="voltPointPicker"
            				multiSelectMode="true"
            				endAction="addPointHandler"
            				linkType="button" nameKey="add"/>
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
        voltageThreePhaseARegulatorPicker.endAction = updateRegPickerExcludes;
        voltageThreePhaseBRegulatorPicker.endAction = updateRegPickerExcludes;
        voltageThreePhaseCRegulatorPicker.endAction = updateRegPickerExcludes;
    </script>
</c:if>

</tags:standardPopup>
