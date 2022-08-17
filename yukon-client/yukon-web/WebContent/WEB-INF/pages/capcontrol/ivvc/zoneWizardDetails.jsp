<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="d" tagdir="/WEB-INF/tags/dialog"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<tags:standardPopup pageName="ivvc" module="capcontrol" popupName="zoneWizard">

<tags:setFormEditMode mode="${mode}"/>

<cti:displayForPageEditModes modes="EDIT">
    <cti:url var="action"  value="/capcontrol/ivvc/wizard/updateZone"/>
</cti:displayForPageEditModes>
<cti:displayForPageEditModes modes="CREATE">
    <cti:url var="action" value="/capcontrol/ivvc/wizard/createZone"/>
</cti:displayForPageEditModes>

<form:form id="zoneDetailsForm" modelAttribute="zoneDto" action="${action}" >
    <cti:csrfToken/>
    <form:hidden path="zoneId"/>
    <form:hidden path="parentId"/>
    <form:hidden path="substationBusId" id="selectedBusId"/>
    <input type="hidden" id="zoneType" name="zoneType" value="${zoneDto.zoneType}"/>
    <c:if test="${zoneDto.zoneType != 'THREE_PHASE'}">
        <input type="hidden" id="regulatorPhase" value="${zoneDto.regulator.phase}"/>
    </c:if>

    <tags:nameValueContainer2 tableClass="stacked">
        <%-- Zone Name --%>
        <tags:inputNameValue path="name" nameKey=".label.name" size="25"/>

        <%-- Zone Type --%>
        <tags:nameValue2 nameKey=".label.zoneType">
            <c:choose>
                <c:when test="${zoneDto.zoneType == singlePhase}">
                    <i:inline key="${zoneDto.zoneType}"/>:&nbsp;<i:inline key="${zoneDto.regulator.phase}"/>
                </c:when>
                <c:otherwise>
                    <i:inline key="${zoneDto.zoneType}"/>
                </c:otherwise>
            </c:choose>
        </tags:nameValue2>

        <%-- Parent Zone --%>
        <tags:nameValue2 nameKey=".label.parentZone">
            <c:choose>
                <c:when test="${zoneDto.parentId == null}">
                    <i:inline key="yukon.common.dashes"/>
                </c:when>
                <c:otherwise>
                    <c:choose>
                        <c:when test="${parentZone.zoneType == singlePhase}">
                            ${fn:escapeXml(parentZone.name)} - <i:inline key="${parentZone.zoneType}"/>: 
                            <i:inline key="${parentZone.regulator.phase}"/>
                        </c:when>
                        <c:otherwise>
                            ${fn:escapeXml(parentZone.name)} - <i:inline key="${parentZone.zoneType}"/>
                        </c:otherwise>
                    </c:choose>
                </c:otherwise>
            </c:choose>
        </tags:nameValue2>

        <%-- Substation Bus --%>
        <tags:nameValue2 nameKey=".label.substationBus">
            ${fn:escapeXml(subBusName)}
        </tags:nameValue2>

        <c:choose>
            <%-- Regulator Selection - Gang --%>
            <c:when test="${zoneDto.zoneType ==  gangOperated}">
                <tags:nameValue2 nameKey=".label.regulator">
                    <tags:bind path="regulator.regulatorId">
                        <tags:pickerDialog id="voltageGangRegulatorPicker${zoneDto.zoneId}" 
                            type="availableVoltageRegulatorGangPicker" 
                            destinationFieldName="regulator.regulatorId"
                            initialId="${zoneDto.regulator.regulatorId}"
                            selectionProperty="paoName"
                            linkType="selection"
                            extraArgs="${zoneDto.zoneId}"
                            useInitialIdsIfEmpty="true"
                            multiSelectMode="false"
                            immediateSelectMode="true"/>
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
                                allowEmptySelection="true"
                                endAction="yukon.da.zone.wizard.updateRegPickerExcludes"/>
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
                            immediateSelectMode="true"/>
                    </tags:bind>
                </tags:nameValue2>
            </c:when>
        </c:choose>

        <%-- Graph Start Position --%>
        <tags:inputNameValue path="graphStartPosition" nameKey=".label.graphStartPosition" size="6"/>

    </tags:nameValueContainer2>

    <div class="stacked">
        <c:if test="${zoneDto.zoneType != singlePhase}">
            <tags:sectionContainer2 nameKey="assignedVoltageDevice" hideEnabled="false">
                <tags:dynamicTable items="${zoneDto.bankAssignments}" nameKey="dynamicTable"
                    id="bankTable" addButtonClass="bankAddItem" noBlockOnAdd="true">
                    <div class="scroll-md">
                        <table class="compact-results-table">
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
                                            ${fn:escapeXml(row.name)}
                                        </td>
                                        <td>
                                            ${fn:escapeXml(row.device)}
                                        </td>
                                        <td>
                                            <tags:input path="bankAssignments[${status.index}].graphPositionOffset" size="3"/>
                                        </td>
                                        <td>
                                            <tags:input path="bankAssignments[${status.index}].distance" size="4"/>
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
                <tags:pickerDialog endAction="yukon.da.zone.wizard.addBankHandler"
                    extraArgs="${zoneDto.substationBusId}"
                    id="bankPicker" 
                    multiSelectMode="true"
                    type="availableCapBankPicker"
                    linkType="none"/>
            </tags:sectionContainer2>
        </c:if>
    </div>
    <div class="stacked-md">
        <tags:sectionContainer2 nameKey="assignedVoltagePoint" hideEnabled="false">
            <c:set var="addItemParameters" value="{'zoneType': '${zoneDto.zoneType}'}"/>
            <tags:dynamicTable items="${zoneDto.pointAssignments}" nameKey="dynamicTable"
                id="pointTable" addItemParameters="${addItemParameters}"  addButtonClass="pointAddItem"
                noBlockOnAdd="true" >
                <div class="scroll-md">
                    <table class="compact-results-table">
                        <thead>
                            <tr>
                                <th><i:inline key=".table.point.name"/></th>
                                <th><i:inline key=".table.point.device"/></th>
                                <th><i:inline key=".table.phase"/></th>
                                <th><i:inline key=".table.position"/></th>
                                <th><i:inline key=".table.distance"/></th>
                                <th><i:inline key=".table.ignore"/></th>
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
                                        ${fn:escapeXml(row.name)}
                                    </td>
                                    <td>
                                        ${fn:escapeXml(row.device)}
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
                                        <tags:input path="pointAssignments[${status.index}].graphPositionOffset" size="3"/>
                                    </td>
                                    <td>
                                        <tags:input path="pointAssignments[${status.index}].distance" size="4"/>
                                    </td>
                                    <td>
                                        <tags:checkbox path="pointAssignments[${status.index}].ignore"/>
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
            <tags:pickerDialog endAction="yukon.da.zone.wizard.addPointHandler"
                id="pointPicker" 
                multiSelectMode="true"
                type="voltPointPicker"
                linkType="none"
                excludeIds="${usedPointIds}"/>
        </tags:sectionContainer2>
    </div>
    
</form:form>

</tags:standardPopup>
