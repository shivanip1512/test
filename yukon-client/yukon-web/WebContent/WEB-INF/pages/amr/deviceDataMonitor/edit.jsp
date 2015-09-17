<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags"%>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n"%>
<%@ taglib prefix="d" tagdir="/WEB-INF/tags/dialog"%>

<cti:standardPage module="amr" page="deviceDataMonitor.${mode}">

    <%@ include file="shared.jspf"%>

    <form action="toggleEnabled" method="post" class="js-toggle-enabled-form">
        <cti:csrfToken/>
        <input type="hidden" name="monitorId" value="${monitor.id}"/>
    </form>
    <form action="delete" method="post" class="js-delete-form">
        <cti:csrfToken/>
        <input type="hidden" name="monitorId" value="${monitor.id}"/>
    </form>

    <c:set var="disableAddProcessAtStart" value="true"/>
    <cti:displayForPageEditModes modes="EDIT">
        <cti:url value="/amr/deviceDataMonitor/update" var="action"/>
        <c:set var="disableAddProcessAtStart" value="false"/>
    </cti:displayForPageEditModes>
    <cti:displayForPageEditModes modes="CREATE">
        <cti:url value="/amr/deviceDataMonitor/create" var="action"/>
    </cti:displayForPageEditModes>
    <form:form commandName="monitor" action="${action}" method="post" cssClass="js-monitor-form">
        <cti:csrfToken/>
        <cti:displayForPageEditModes modes="EDIT">
            <form:hidden path="id" id="monitor-id" />
            <form:hidden path="enabled" />
        </cti:displayForPageEditModes>

        <tags:sectionContainer2 nameKey="settings" styleClass="${settings_section_class}">
            <tags:nameValueContainer2>

                <%-- name --%>
                <tags:inputNameValue nameKey=".name" path="name" size="35" maxlength="50" />
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
            <tags:dynamicTable items="${monitor.processors}" 
                nameKey="dynamicTable"
                id="processorsTable" 
                addButtonClass="js-add_processor" 
                noBlockOnAdd="true" 
                disableAddButton="${disableAddProcessAtStart}">
            <div>
            <table class="compact-results-table js-processors-table device_data_processors full-width dashed with-form-controls">
                <thead>
                    <tr>
                        <th><i:inline key=".processors.attribute" /></th>
                        <th><i:inline key=".processors.stateGroup" /></th>
                        <th><i:inline key=".processors.state" /></th>
                        <th class="remove-column"></th>
                    </tr>
                </thead>
                <tbody>
                    <tr class="js-new-row-model" style="display: none;">
                        <input type="hidden" data-name="processors[0].processorId"/>
                        <input type="hidden" data-name="processors[0].monitorId"/>
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
                        <tr class="processor">
                            <form:hidden path="processors[${status.index}].processorId" />
                            <form:hidden path="processors[${status.index}].monitorId" />
                            <form:hidden path="processors[${status.index}].deletion" class="isDeletionField"/>
                            <td>
                                <form:select path="processors[${status.index}].attribute" cssClass="js-attribute">
                                    <c:forEach items="${allGroupedReadableAttributes}" var="group">
                                        <optgroup label="<cti:msg2 key="${group.key}"/>">
                                            <c:forEach items="${group.value}" var="item">
                                               <c:set var="selected" value=""/>
                                                <c:if test="${processor.attribute == item}">
                                                    <c:set var="selected" value="selected"/>
                                                    <c:set var="attributeKey" value="${item.key}"/>
                                                </c:if>
                                                <option value="${item.key}" ${selected}>
                                                    <cti:formatObject value="${item}"/>
                                                </option>
                                            </c:forEach>
                                        </optgroup>
                                    </c:forEach>
                                </form:select>
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
                    </c:forEach>
                </tbody>
            </table>
            </div>
            </tags:dynamicTable>
        </tags:sectionContainer2>
    </form:form>

    <cti:msg2 var="updateCreateTitleVerb" key=".${mode}.areYouSureTitleVerb"/>
    <cti:msg2 var="updateCreateMsgVerb" key=".${mode}.areYouSureMsgVerb"/>
    <c:set var="nameKey" value="areYouSureUpdateOrCreateDialog"/>
    <c:set var="options" value="{width: 550}"/>
    <d:inline id="update-loading-dialog" okEvent="yukon:ami:ddm:save" 
            nameKey="${nameKey}" options="${options}" arguments="${updateCreateTitleVerb}">
        <h3 class="error"><i:inline key="yukon.common.warning"/></h3>
        <cti:msg2 key=".areYouSureLoading" arguments="${updateCreateMsgVerb}"/>
    </d:inline>
    <d:inline id="update-missing-dialog" okEvent="yukon:ami:ddm:save" 
            nameKey="${nameKey}" options="${options}" arguments="${updateCreateTitleVerb}">
        <h3 class="error"><i:inline key="yukon.common.warning"/></h3>
        <cti:msg2 key=".areYouSureMissing" arguments="${updateCreateMsgVerb}"/>
    </d:inline>

    <%-- update / enable_disable / delete / cancel --%>
    <div class="page-action-area">
        <cti:displayForPageEditModes modes="EDIT">
            <cti:button nameKey="update" classes="js-save-monitor primary action"/>
            <c:if test="${monitor.enabled}">
                <cti:button classes="js-toggle-enabled" nameKey="disable"/>
            </c:if>
            <c:if test="${!monitor.enabled}">
                <cti:button classes="js-toggle-enabled" nameKey="enable"/>
            </c:if>
            <cti:button id="deleteButton" nameKey="delete" classes="delete"/>
            <d:confirm nameKey="deleteConfirmation" argument="${monitor.name}" on="#deleteButton"  />
            <cti:url var="viewMonitorUrl" value="/amr/deviceDataMonitor/view">
                <cti:param name="monitorId" value="${monitor.id}" />
            </cti:url>
            <cti:button nameKey="back" href="${viewMonitorUrl}" />
        </cti:displayForPageEditModes>
        <cti:displayForPageEditModes modes="CREATE">
            <cti:button nameKey="create" classes="js-save-monitor primary action"/>
            <cti:url var="startMonitorUrl" value="/meter/start"/>
            <cti:button nameKey="cancel" href="${startMonitorUrl}"/>
        </cti:displayForPageEditModes>
    </div>

</cti:standardPage>
