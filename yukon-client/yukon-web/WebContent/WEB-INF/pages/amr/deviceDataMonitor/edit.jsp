<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags"%>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n"%>
<%@ taglib prefix="amr" tagdir="/WEB-INF/tags/amr"%>

<jsp:useBean id="paramMap" class="java.util.HashMap"/>
<c:set target="${paramMap}" property="monitorId" value="${monitor.id}"/>
<cti:standardPage module="amr" page="deviceDataMonitor.${mode}" smartNotificationsEvent="DEVICE_DATA_MONITOR" smartNotificationsParameters="${paramMap}">

    <div class="dn js-calculating-warning warning">
        <i:inline key=".calculatingWarning"/>
    </div>

    <%@ include file="shared.jspf"%>

    <form action="toggleEnabled" id="toggleEnabledForm" method="post" class="js-toggle-enabled-form">
        <cti:csrfToken/>
        <input type="hidden" name="monitorId" value="${monitor.id}"/>
    </form>
    <form action="delete" method="post" id="deleteMonitorForm">
        <cti:csrfToken/>
        <input type="hidden" name="monitorId" value="${monitor.id}"/>
    </form>

    <c:set var="disableAddProcessAtStart" value="${empty monitor.groupName ? 'true' : 'false'}"/>
    <cti:displayForPageEditModes modes="EDIT">
        <cti:url value="/amr/deviceDataMonitor/update" var="action"/>
        <c:set var="disableAddProcessAtStart" value="false"/>
        <c:set var="modeMsg" value="${1}" /> 
    </cti:displayForPageEditModes>
    <cti:displayForPageEditModes modes="CREATE">
        <cti:url value="/amr/deviceDataMonitor/create" var="action"/>
        <c:set var="modeMsg" value="${0}" /> 
    </cti:displayForPageEditModes>
    <form:form modelAttribute="monitor" action="${action}" method="post" cssClass="js-monitor-form">
        <cti:csrfToken/>
        <cti:displayForPageEditModes modes="EDIT">
            <form:hidden path="id" id="monitor-id" />
            <form:hidden path="enabled" />
        </cti:displayForPageEditModes>

        <tags:sectionContainer2 nameKey="settings" styleClass="${settings_section_class}">
            <tags:nameValueContainer2>

                <%-- name --%>
                <tags:inputNameValue nameKey=".name" path="name" size="38" maxlength="60" />
                <tags:nameValue2 nameKey=".violations">
                
                <c:if test="${monitor.id != null}">
                    <cti:dataUpdaterCallback function="yukon.ami.ddm.violationUpdater" initialize="true" value="DEVICE_DATA_MONITOR/CALCULATION_STATUS/${monitor.id}"/>
                </c:if>
                <c:if test="${monitor.id == null}">
                    <i:inline key="yukon.common.na"/>
                </c:if>

                <!-- CALCULATING -->
                <span class="dn js-violations js-violations-calculating">
                    <i class="icon icon-spinner js-violations-loading">&nbsp;</i><i:inline key=".calculating"/>
                </span>
                
                <!-- NA -->
                <span class="dn js-violations js-violations-na">
                    <i:inline key="yukon.common.na"/>
                </span>
                
                <!-- a number of violations -->
                <span class="dn js-violations js-violations-count">0</span>
                <span class="dn js-violations js-violations-exist">
                    ${violationReportLinks}
                </span>
                
                </tags:nameValue2>
          		<tags:nameValue2 nameKey=".monitoring">
                    <span class="dn js-calc-indicator">
                        <cti:icon icon="icon-spinner"/>
                        <span class="b-label"><i:inline key=".calculating"/></span>
                    </span>
                    <span class="js-device-group-count"><fmt:formatNumber type="number" value="${monitoringCount}" /></span>
                </tags:nameValue2>
                
                <tags:nameValueGap2 gapHeight="20px"/>
                
                <tags:nameValue2 nameKey=".supportedDevices">${supportedDevices}</tags:nameValue2>
                
                <tags:nameValueGap2 gapHeight="20px"/>
                
                <%-- device group --%>
                <tags:nameValue2 nameKey=".deviceGroup">
                    <cti:list var="group"><cti:item value="${monitor.groupName}"/></cti:list>
                    <tags:deviceGroupPicker inputName="groupName" inputValue="${group}" classes="js-monitor-group"/>
                </tags:nameValue2>

                <cti:displayForPageEditModes modes="EDIT">
                    <tags:nameValue2 nameKey=".violationsGroup">${violationsGroup}</tags:nameValue2>
                    <c:if test="${monitor.enabled}"><c:set var="clazz" value="success"/></c:if>
                    <c:if test="${!monitor.enabled}"><c:set var="clazz" value="error"/></c:if>
                    <tags:nameValue2 nameKey=".status" valueClass="${clazz}">${monitoringEnabled}</tags:nameValue2>
                </cti:displayForPageEditModes>
                
                <tags:nameValue2 excludeColon="true">
                    <a href="javascript:void(0);" id="refresh-violations" class="dn" data-add-key="">
                        <cti:icon icon="icon-arrow-refresh"/>
                        <span><i:inline key=".refreshViolations"/></span>
                    </a>
                </tags:nameValue2>
            </tags:nameValueContainer2>
        </tags:sectionContainer2>
        <div id="point-unknown-number-help" class="dn" target-title="<i:inline key=".missingPOINT.limitedQuery.help.title" />"><i:inline key=".missingPOINT.limitedQuery.help" /></div>
        
        <span id="str_na" class="dn"><i:inline key="yukon.common.na"/></span>
        <tags:sectionContainer2 nameKey="processors" styleClass="${processors_section_class}">
            <tags:dynamicTable items="${monitor.getStateProcessors()}" 
                nameKey="dynamicTable"
                id="processorsTable" 
                addButtonClass="js-add_processor" 
                noBlockOnAdd="true" 
                disableAddButton="${disableAddProcessAtStart}">
                <div>
                    <table class="compact-results-table js-processors-table full-width dashed with-form-controls">
                        <thead>
                            <tr>
                                <th><i:inline key=".processors.attribute" /></th>
                                <th><i:inline key=".processors.stateGroup" /></th>
                                <th><i:inline key=".processors.state" /></th>
                                <th class="remove-column"></th>
                            </tr>
                        </thead>
                        <tbody>
                            <tr class="js-new-row-model dn">
                                <input type="hidden" data-name="processors[0].processorId"/>
                                <input type="hidden" data-name="processors[0].monitorId"/>
                                <input type="hidden" data-name="processors[0].type" value="STATE"/>
                                <input type="hidden" data-name="processors[0].deletion" value="false" class="isDeletionField"/>
                                <td>
                                    <select class="js-attribute" data-name="processors[0].attribute">
                                        <option value="-1"><i:inline key="yukon.common.selector.selectOne" /></option>
                                        <c:forEach items="${allGroupedReadableAttributes}" var="group">
                                            <optgroup label="<cti:msg2 key="${group.key}"/>">
                                                <c:forEach items="${group.value}" var="item">
                                                    <option value="${item.key}" ${selected}>
                                                        <cti:formatObject value="${item}"/>
                                                    </option>
                                                </c:forEach>
                                            </optgroup>
                                        </c:forEach>
                                    </select>
                                </td>
                                <td>
                                    <div class="js-state-group"><input type="hidden" name="" value="" data-name="processors[0].stateGroup"></div>
                                    <span class="dn js-calc-indicator">
                                        <cti:icon icon="icon-spinner"/>
                                        <span class="b-label"><i:inline key=".calculating"/></span>
                                    </span>
                                </td>
                                <td>
                                    <div class="js-states"><input type="hidden" name="" value="" data-name="processors[0].state"></div>
                                </td>
                                <td class="actions">
                                    <cti:button nameKey="delete" renderMode="buttonImage" classes="js-remove-btn fr" icon="icon-cross"/>
                                </td>
                            </tr>
                            <tags:dynamicTableUndoRow columnSpan="4" nameKey="dynamicTable.undoRow"/>
                            <c:forEach var="processor" items="${monitor.processors}" varStatus="status">
                                <c:if test="${processor.type == 'STATE'}">
                                    <tr class="processor">
                                        <form:hidden path="processors[${status.index}].processorId" />
                                        <form:hidden path="processors[${status.index}].monitorId" />
                                        <form:hidden path="processors[${status.index}].type" />
                                        <form:hidden path="processors[${status.index}].deletion" class="isDeletionField"/>
                                        <td>
                                            <tags:selectWithItems path="processors[${status.index}].attribute" items="${allGroupedReadableAttributes}"
                                                groupItems="true" inputClass="js-attribute js-init-chosen"/>
                                        </td>
                                        <td>
                                        <c:set var="stateGroups" value="${mapAttributeKeyToStateGroupList.get(attributeKey)}"/>
                                        <c:set var="ctrlName" value="processors[${status.index}].stateGroup"/>
                                        <c:choose>
                                            <c:when test="${stateGroups.size() > 1}">
                                            <form:select path="${ctrlName}" cssClass="js-state-group">
                                                <c:forEach items="${stateGroups}" var="stateGroup">
                                                    <c:set var="selected" value=""/>
                                                    <c:if test="${processor.stateGroup == stateGroup}">
                                                        <c:set var="selected" value="selected"/>
                                                    </c:if>
                                                    <option value="${stateGroup.liteID}" ${selected}>${stateGroup.stateGroupName}</option>
                                                </c:forEach>
                                            </form:select>
                                            </c:when><c:otherwise>
                                                <div class="js-state-group"><input type="hidden" name="${ctrlName}" value="${processor.stateGroup.liteID}">${processor.stateGroup.stateGroupName}</div>
                                            </c:otherwise>
                                        </c:choose>
                                        <span class="dn js-calc-indicator">
                                            <cti:icon icon="icon-spinner"/>
                                            <span class="b-label"><i:inline key=".calculating"/></span>
                                        </span>
                                        </td>
                                        <td>
                                            <form:select path="processors[${status.index}].state" cssClass="js-states">
                                                <c:forEach items="${processor.stateGroup.statesList}" var="state">
                                                    <c:set var="selected" value=""/>
                                                    <c:if test="${processor.state.stateRawState == state.liteID}">
                                                        <c:set var="selected" value="selected"/>
                                                    </c:if>
                                                    <option value="${processor.stateGroup.liteID}:${state.liteID}" ${selected}>${state.stateText}</option>
                                                </c:forEach>
                                            </form:select>
                                        </td>
                                        <tags:dynamicTableActionsCell tableId="processorsTable"
                                            isFirst="${status.first}" isLast="${status.last}" skipMoveButtons="true"/>
                                    </tr>
                                    <tags:dynamicTableUndoRow columnSpan="4" nameKey="dynamicTable.undoRow"/>
                                </c:if>
                            </c:forEach>
                        </tbody>
                    </table>
                </div>
            </tags:dynamicTable>
        </tags:sectionContainer2>
        
        <tags:sectionContainer2 nameKey="valueProcessors">
            <tags:dynamicTable items="${monitor.getValueProcessors()}" 
                nameKey="dynamicTable"
                id="valueProcessorsTable" 
                addButtonClass="js-add-value-processor" 
                noBlockOnAdd="true" 
                disableAddButton="${disableAddProcessAtStart}">
                <div>
                    <table class="compact-results-table js-value-processors-table full-width dashed with-form-controls">
                        <thead>
                            <tr>
                                <th><i:inline key=".processors.attribute" /></th>
                                <th style="width:60%;"><i:inline key=".processors.rule" /></th>
                                <th class="remove-column"></th>
                            </tr>
                        </thead>
                        <tbody>
                            <tr class="js-new-row-model dn">
                                <input type="hidden" data-name="processors[0].processorId"/>
                                <input type="hidden" data-name="processors[0].monitorId"/>
                                <input type="hidden" data-name="processors[0].deletion" value="false" class="isDeletionField"/>
                                <td>
                                    <select class="js-value-attribute" data-name="processors[0].attribute">
                                        <option value="-1"><i:inline key="yukon.common.selector.selectOne" /></option>
                                        <c:forEach items="${allGroupedValueAttributes}" var="group">
                                            <optgroup label="<cti:msg2 key="${group.key}"/>">
                                                <c:forEach items="${group.value}" var="item">
                                                    <option value="${item.key}" ${selected}>
                                                        <cti:formatObject value="${item}"/>
                                                    </option>
                                                </c:forEach>
                                            </optgroup>
                                        </c:forEach>
                                    </select>
                                </td>
                                <td>
                                    <select class="js-processor-type" data-name="processors[0].type">
                                        <c:forEach items="${processorTypes}" var="type">
                                            <option value="${type}"><i:inline key="${type.formatKey}"/></option>
                                        </c:forEach>
                                    </select>
                                    <span class="js-processor-value">
                                        <i:inline key=".value"/>: <input type="text" size="8" data-name="processors[0].processorValue" inputClass="MR10"/>
                                    </span>
                                    <span class="js-range-values dn">
                                        <i:inline key="yukon.common.min"/>: <input type="text" size="8" data-name="processors[0].rangeMin" inputClass="MR10"/>
                                        <i:inline key="yukon.common.max"/>: <input type="text" size="8" data-name="processors[0].rangeMax" inputClass="MR10"/>
                                    </span>
                                </td>
                                <td class="actions">
                                    <cti:button nameKey="delete" renderMode="buttonImage" classes="js-remove-btn fr" icon="icon-cross"/>
                                </td>
                            </tr>
                            <tags:dynamicTableUndoRow columnSpan="3" nameKey="dynamicTable.undoRow"/>
                            <c:forEach var="processor" items="${monitor.processors}" varStatus="status">
                                <c:if test="${processor.type != 'STATE'}">
                                    <tr class="processor">
                                        <form:hidden path="processors[${status.index}].processorId" />
                                        <form:hidden path="processors[${status.index}].monitorId" />
                                        <form:hidden path="processors[${status.index}].deletion" class="isDeletionField"/>
                                        <td>
                                            <tags:selectWithItems path="processors[${status.index}].attribute" items="${allGroupedValueAttributes}"
                                                groupItems="true" inputClass="js-value-attribute js-init-chosen"/>
                                        </td>
                                        <td>
                                            <tags:selectWithItems inputClass="js-processor-type" path="processors[${status.index}].type" items="${processorTypes}"/>
                                            <c:set var="rangeClass" value="${(processor.type != 'RANGE' and processor.type != 'OUTSIDE') ? 'dn' : ''}"/>
                                            <c:set var="notRangeClass" value="${(processor.type == 'RANGE' or processor.type == 'OUTSIDE') ? 'dn' : ''}"/>
                                            <span class="js-processor-value ${notRangeClass}">
                                                <i:inline key=".value"/>:
                                                <tags:input size="5" path="processors[${status.index}].processorValue" 
                                                    displayValidationToRight="true" inputClass="MR10"/>
                                            </span>
                                            <span class="js-range-values ${rangeClass}">
                                                <i:inline key="yukon.common.min"/>: <tags:input size="5" path="processors[${status.index}].rangeMin" 
                                                    displayValidationToRight="true" inputClass="MR10"/>
                                                <i:inline key="yukon.common.max"/>: <tags:input size="5" path="processors[${status.index}].rangeMax" 
                                                    displayValidationToRight="true" inputClass="MR10"/>
                                            </span>
                                        </td>
                                        <tags:dynamicTableActionsCell tableId="valueProcessorsTable"
                                            isFirst="${status.first}" isLast="${status.last}" skipMoveButtons="true"/>
                                    </tr>
                                    <tags:dynamicTableUndoRow columnSpan="3" nameKey="dynamicTable.undoRow"/>
                                </c:if>
                            </c:forEach>
                        </tbody>
                    </table>
                </div>
            </tags:dynamicTable>
        </tags:sectionContainer2>
    </form:form>

    <cti:msg2 var="updateCreateTitleVerb" key=".areYouSureTitleVerb" arguments="${modeMsg}"/>
    <cti:msg2 var="updateCreateMsgVerb" key=".areYouSureMsgVerb" arguments="${modeMsg}"/>
    <cti:msg2 var="titleKey" key=".areYouSureUpdateOrCreateDialog.title" arguments="${updateCreateTitleVerb}"/>

    <div class="dn" id="update-loading-dialog" data-dialog data-title="${titleKey}">
        <h3 class="error"><i:inline key="yukon.common.warning"/></h3>
        <cti:msg2 key=".areYouSureLoading" arguments="${updateCreateMsgVerb}"/>
    </div>
    <div class="dn" id="update-missing-dialog" data-dialog data-title="${titleKey}">
        <h3 class="error"><i:inline key="yukon.common.warning"/></h3>
        <cti:msg2 key=".areYouSureMissing" arguments="${updateCreateMsgVerb}"/>
    </div>

    <%-- update / enable_disable / delete / cancel --%>
    <div class="page-action-area">
        <cti:displayForPageEditModes modes="EDIT">
            <cti:button nameKey="save" classes="js-save-monitor primary action" data-disable-group="actionButtons"/>
            <c:set var="enableDisableKey" value="disable"/>
            <c:if test="${!monitor.enabled}">
                <c:set var="enableDisableKey" value="enable"/>
            </c:if>
            <cti:button id="toggleMonitor" nameKey="${enableDisableKey}" busy="true" classes="js-calculating-disable" data-disable-group="actionButtons"/>
            <cti:button id="deleteButton" nameKey="delete" classes="delete js-calculating-disable" data-disable-group="actionButtons"
                        data-popup="#confirm-delete-monitor-popup"/>
            <amr:confirmDeleteMonitor target="#deleteButton" monitorName="${monitor.name}"/>
            <cti:url var="viewMonitorUrl" value="/amr/deviceDataMonitor/view">
                <cti:param name="monitorId" value="${monitor.id}" />
            </cti:url>
            <cti:button nameKey="cancel" href="${viewMonitorUrl}" data-disable-group="actionButtons"/>
        </cti:displayForPageEditModes>
        <cti:displayForPageEditModes modes="CREATE">
            <cti:button nameKey="create" classes="js-save-monitor primary action"/>
            <cti:url var="startMonitorUrl" value="/meter/start"/>
            <cti:button nameKey="cancel" href="${startMonitorUrl}"/>
        </cti:displayForPageEditModes>
    </div>
<cti:includeScript link="/resources/js/pages/yukon.ami.monitor.js"/>
</cti:standardPage>
