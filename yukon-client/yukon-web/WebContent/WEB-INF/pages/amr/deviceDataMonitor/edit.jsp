<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags"%>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n"%>
<%@ taglib prefix="dialog" tagdir="/WEB-INF/tags/dialog"%>

<cti:standardPage module="amr" page="deviceDataMonitor.${mode}">

    <%@ include file="shared.jspf"%>

    <form action="toggleEnabled" method="post" class="f_toggle_enabled_form">
        <input type="hidden" name="monitorId" value="${monitor.id}"/>
    </form>
    <form action="delete" method="post" class="f_delete_form">
        <input type="hidden" name="monitorId" value="${monitor.id}"/>
    </form>

    <cti:displayForPageEditModes modes="EDIT">
        <cti:url value="/amr/deviceDataMonitor/update" var="action"/>
    </cti:displayForPageEditModes>
    <cti:displayForPageEditModes modes="CREATE">
        <cti:url value="/amr/deviceDataMonitor/create" var="action"/>
    </cti:displayForPageEditModes>
	<form:form commandName="monitor" action="${action}" method="post" cssClass="f_monitor_form">

        <cti:displayForPageEditModes modes="EDIT">
			<form:hidden path="id" id="monitorId" /> <!-- Giving this an id so we can easily grab it in js -->
			<form:hidden path="enabled" />
		</cti:displayForPageEditModes>

	    <div class="colmask doublepage">
	        <div class="colleft">
	            <div class="col1">
					<tags:formElementContainer nameKey="settings" styleClass="${settings_section_class}">
						<tags:nameValueContainer2>

							<%-- name --%>
							<tags:inputNameValue nameKey=".name" path="name" size="50" maxlength="50" />
							
			                <%-- device group --%>
			                <cti:msg2 var="deviceGroupTitle" key=".popupInfo.deviceGroup.title"/>
			                <tags:nameValue2 nameKey=".deviceGroup">
			                    <cti:deviceGroupHierarchyJson predicates="NON_HIDDEN" var="groupDataJson" />
			                    <tags:deviceGroupNameSelector fieldName="groupName"
			                        fieldValue="${monitor.groupName}" dataJson="${groupDataJson}"
			                        linkGroupName="true" submitCallback="DeviceDataMonitor._device_group_changed();"/>
			                    <tags:helpInfoPopup title="${deviceGroupTitle}">
			                        <cti:msg2 key=".popupInfo.deviceGroup"/>
			                    </tags:helpInfoPopup>
			                </tags:nameValue2>

							<tags:nameValue2 nameKey=".deviceGroupCount">
                                   <span class="labeled_icon loading dn"><i:inline key=".calculating"/></span>
								<span class="f_device_group_count">${monitoringCount}</span>
							</tags:nameValue2>
							<tags:nameValue2 nameKey=".supportedDevices">
	                            ${supportedDevices}
	                        </tags:nameValue2>
			                <cti:displayForPageEditModes modes="EDIT">
		                        <tags:nameValue2 nameKey=".violationsGroup">
		                            ${violationsGroup}
		                        </tags:nameValue2>
		                        <tags:nameValue2 nameKey=".violationsCount">
		                            ${violationsCount}
		                        </tags:nameValue2>
		                        <tags:nameValue2 nameKey=".monitoring">
		                            ${monitoringEnabled}
		                        </tags:nameValue2>
							</cti:displayForPageEditModes>
						</tags:nameValueContainer2>
					</tags:formElementContainer>
                </div>
                <div class="col2">
					<tags:sectionContainer2 nameKey="processors" styleClass="${processors_section_class}">
			            <tags:dynamicTable items="${monitor.processors}" nameKey="dynamicTable"
			                id="processorsTable" addButtonClass="f_add_processor" noBlockOnAdd="true">
						<div class="smallDialogScrollArea">
						<table class="compactResultsTable f_processors_table device_data_processors">
							<thead>
								<tr>
									<th><i:inline key=".processors.attribute" /></th>
			                        <th><i:inline key=".processors.stateGroup" /></th>
			                        <th><i:inline key=".processors.state" /></th>
									<th class="removeColumn"></th>
								</tr>
							</thead>
							<tbody>
			                    <tr class="f_new_row_model" style="display: none;">
			                        <input type="hidden" data-name="processors[0].processorId"/>
			                        <input type="hidden" data-name="processors[0].monitorId"/>
			                        <input type="hidden" data-name="processors[0].deletion" value="false" class="isDeletionField"/>
			                        <td>
					                    <select class="f_attribute" data-name="processors[0].attribute">
					                        <c:forEach items="${allGroupedReadableAttributes}" var="group">
					                            <optgroup label="<cti:msg2 key="${group.key}"/>">
					                                <c:forEach items="${group.value}" var="item">
					                                    <option value="${item.key}" ${selected}>
					                                        <cti:formatObject value="${item.description}"/>
					                                    </option>
					                                </c:forEach>
					                            </optgroup>
					                        </c:forEach>
					                    </select>
				                    </td>
				                    <td>
								        <select class="f_state_group" data-name="processors[0].stateGroup">
								            <c:forEach items="${stateGroups}" var="stateGroup">
								                <option value="${stateGroup.liteID}">${stateGroup.stateGroupName}</option>
								            </c:forEach>
								        </select>
				                    </td>
				                    <td>
			                            <select class="f_states" data-name="processors[0].state">
								            <c:forEach items="${stateGroups[0].statesList}" var="state">
								                <option value="${stateGroups[0].liteID}:${state.liteID}">${state.stateText}</option>
								            </c:forEach>
			                            </select>
				                    </td>
			                        <td class="actions">
										<a title="Remove" href="javascript:void(0);"
											class="removeBtn icon icon_remove">
										</a>
									</td>
			                    </tr>
			                    <tags:dynamicTableUndoRow columnSpan="4" nameKey="dynamicTable.undoRow"/>
								<c:forEach var="processor" items="${monitor.processors}" varStatus="status">
									<tr>
										<form:hidden path="processors[${status.index}].processorId" />
										<form:hidden path="processors[${status.index}].monitorId" />
										<form:hidden path="processors[${status.index}].deletion" class="isDeletionField"/>
										<td>
			                                <form:select path="processors[${status.index}].attribute" cssClass="f_attribute">
				                                <c:forEach items="${allGroupedReadableAttributes}" var="group">
				                                    <optgroup label="<cti:msg2 key="${group.key}"/>">
				                                        <c:forEach items="${group.value}" var="item">
				                                           <c:set var="selected" value=""/>
						                                    <c:if test="${processor.attribute == item}">
						                                        <c:set var="selected" value="selected"/>
						                                    </c:if>
				                                            <option value="${item.key}" ${selected}>
				                                                <cti:formatObject value="${item.description}"/>
				                                            </option>
				                                        </c:forEach>
				                                    </optgroup>
				                                </c:forEach>
			                                </form:select>
										</td>
										<td>
				                            <form:select path="processors[${status.index}].stateGroup" cssClass="f_state_group">
				                                <c:forEach items="${stateGroups}" var="stateGroup">
			                                        <c:set var="selected" value=""/>
			                                        <c:if test="${processor.stateGroup == stateGroup}">
			                                            <c:set var="selected" value="selected"/>
			                                        </c:if>
				                                    <option value="${stateGroup.liteID}" ${selected}>${stateGroup.stateGroupName}</option>
				                                </c:forEach>
				                            </form:select>
										</td>
										<td>
				                            <form:select path="processors[${status.index}].state" cssClass="f_states">
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
                </div>
            </div>
        </div>
	</form:form>

	<cti:msg2 var="updateCreateTitleVerb" key=".areYouSureTitleVerb" />
	<cti:msg2 var="updateCreateMsgVerb" key=".areYouSureMsgVerb" />
	<c:set var="errorHeader">
        <h2 class="errorMessage standardPageHeading"><i:inline key="yukon.common.warning"/></h2>
    </c:set>
    <!-- This okEvent value should be kept equal to DeviceDataMonitor._update_or_create_event in yukon.device_data_monitor.js -->
    <c:set var="okEvent" value="e_ddm_update_or_create"/>
    <c:set var="nameKey" value="areYouSureUpdateOrCreateDialog"/>
    <c:set var="options" value="{width: 550}"/>
	<dialog:inline id="update_loading_dialog" okEvent="${okEvent}" nameKey="${nameKey}" options="${options}" arguments="${updateCreateTitleVerb}">
        ${errorHeader}
        <cti:msg2 key=".areYouSureLoading" arguments="${updateCreateMsgVerb}"/>
	</dialog:inline>
	<dialog:inline id="update_missing_dialog" okEvent="${okEvent}" nameKey="${nameKey}" options="${options}" arguments="${updateCreateTitleVerb}">
        ${errorHeader}
        <cti:msg2 key=".areYouSureMissing" arguments="${updateCreateMsgVerb}"/>
	</dialog:inline>

	<%-- update / enable_disable / delete / cancel --%>
	<div class="pageActionArea">
		<cti:displayForPageEditModes modes="EDIT">
			<button class="f_update_monitor"><i:inline key="yukon.web.components.button.update.label"/></button>
			<c:set var="monitoringKey" value="yukon.common.enable" />
			<c:if test="${monitor.enabled}">
				<c:set var="monitoringKey" value="yukon.common.disable" />
			</c:if>
			<button class="f_toggle_enabled"><i:inline key="${monitoringKey}"/></button>
			<cti:button id="deleteButton" nameKey="delete" />
	        <tags:confirmDialog nameKey=".deleteConfirmation" argument="${monitor.name}" on="#deleteButton" styleClass="f_delete_btn"/>
		</cti:displayForPageEditModes>
		<cti:displayForPageEditModes modes="CREATE">
			<cti:button nameKey="create" styleClass="f_update_monitor" />
		</cti:displayForPageEditModes>

		<cti:url var="viewMonitorUrl" value="/amr/deviceDataMonitor/view">
			<cti:param name="monitorId" value="${monitor.id}" />
		</cti:url>
		<cti:button nameKey="back" href="${viewMonitorUrl}" />
	</div>

</cti:standardPage>
