<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags"%>
<%@ taglib prefix="cm" tagdir="/WEB-INF/tags/contextualMenu"%>
<%@ taglib prefix="dt" tagdir="/WEB-INF/tags/dateTime"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="jsTree" tagdir="/WEB-INF/tags/jsTree"%>
<%@ taglib prefix="dialog" tagdir="/WEB-INF/tags/dialog"%>

<cti:standardPage page="waterLeakReport.report" module="amr">

	<cti:includeScript link="/JavaScript/water_leak_report.js"/>
    <cti:includeScript link="/JavaScript/yukon/ui/fieldHelper.js"/>

	<input type="hidden" id="hasFilterError" value="${hasFilterError}"/>
	<input type="hidden" id="hasScheduleError" value="${hasScheduleError}"/>

	<cti:msg2 var="filterButton" key="yukon.web.components.button.filter.label"/>
	<cti:msg2 var="filterButtonTitle" key=".filterButtonTitle"/>
	<cti:msg2 var="scheduleButton" key=".scheduleButton"/>
	<cti:msg2 var="scheduleButtonTitle" key=".scheduleButtonTitle"/>
	<cti:msg2 var="updateButton" key=".updateButton"/>
	<cti:msg2 var="updateButtonTitle" key=".updateButtonTitle"/>
	<cti:msg2 var="resetButtonTitle" key=".filterResetButtonTitle"/>
	<cti:msg2 var="resetButton" key="yukon.web.components.button.reset.label"/>
	<cti:msg2 var="cancelButton" key="yukon.web.components.button.cancel.label"/>
	<cti:msg2 var="cancelButtonTitle" key=".filterShortcutClose"/>
	
	<dialog:inline id="leakFilterDialog" okEvent="none" nameKey="leakFilterDialog" on=".f-open_filter_dialog"
		options="{width: 550, 'buttons': [{text: '${cancelButton}', click: function() { jQuery(this).dialog('close'); }, title: '${cancelButtonTitle}', 'class': 'leakFilterCancelButton' },
                                          {text: '${resetButton}', click: function() { Yukon.WaterLeakReport.reset_filter_submit(); }, title: '${resetButtonTitle}' },
                                          {text: '${filterButton}', click: function() { Yukon.WaterLeakReport.filter_submit(); }, title: '${filterButtonTitle}', 'class': 'leakFilterSubmitButton primary action'}]}">
		<form:form id="filterForm" action="report" method="get" commandName="backingBean">
			<tags:sortFields backingBean="${backingBean}"/>
			<tags:selectDevicesTabbed deviceCollection="${backingBean.deviceCollection}" tabClass="waterLeakFilterTab" individualPickerType="waterMeterPicker"
				groupSelectedCallback="Yukon.WaterLeakReport.filter_group_selected_callback();" individualSelectedCallback="Yukon.WaterLeakReport.filter_individual_selected_callback"
				uniqueId="filterSelector"/>
			<div class="under_tabs">
				<tags:nameValueContainer2>
					<tags:nameValue2 nameKey=".filter.fromDate">
						<dt:dateTime path="fromInstant" value="${backingBean.fromInstant}" cssClass="f-from_datetime f-focus"
							stepMinute="60" stepHour="1" cssDialogClass="hide_minutes" />
					</tags:nameValue2>
					<tags:nameValue2 nameKey=".filter.toDate">
						<dt:dateTime path="toInstant" value="${backingBean.toInstant}" cssClass="f-to_datetime"
							stepMinute="60" stepHour="1" cssDialogClass="hide_minutes" />
					</tags:nameValue2>

                    <tags:nameValue2 nameKey=".filter.threshold">
                        <tags:input path="threshold" size="3" inputClass="threshold_input f-threshold"/>
						<span class="focusableFieldHolder">
                            <a title="<i:inline key=".filter.helpText"/>"><i class="icon icon-help"></i></a>
						</span>
						<span class="focusedFieldDescription"><i:inline key=".filter.threshold.helpText"/></span>
                    </tags:nameValue2>

					<tags:nameValue2 nameKey=".filter.includeDisabledDevices">
						<form:checkbox path="includeDisabledPaos"
							cssClass="f-include_disabled_paos"/>
					</tags:nameValue2>
				</tags:nameValueContainer2>
			</div>
			<div class="filter_shortcut tac hint"><cti:msg2 key=".filterShortcutOpen"/></div>
		</form:form>
	</dialog:inline>
    <%@ include file="leakAlgorithmDialog.jspf"%>
	
	<c:set var="popupTitleArgs" value=""/>
	<c:if test="${not empty exportData.scheduleName}">
		<c:set var="popupTitleArgs" value="\"${fn:escapeXml(fileExportData.scheduleName)}\""/>
	</c:if>
	<dialog:inline id="leakScheduleDialog" okEvent="none" nameKey="leakScheduleDialog" arguments="${popupTitleArgs}" on=".f-open_schedule_dialog"
		options="{width: 650, 'buttons': [{text: '${empty jobId ? scheduleButton : updateButton}', click: function() {Yukon.WaterLeakReport.schedule_submit();}, title: '${empty jobId ? scheduleButtonTitle : updateButtonTitle}', 'class': 'leakScheduleSubmitButton'},
										  {text: '${cancelButton}', click: function() {jQuery(this).dialog('close');}, title: '${cancelButtonTitle}', 'class': 'leakScheduleCancelButton'}]}">
		
		<form:form id="scheduleForm" action="schedule" method="get" commandName="fileExportData">
			<tags:selectDevicesTabbed deviceCollection="${backingBean.deviceCollection}" tabClass="waterLeakFilterTab" individualPickerType="waterMeterPicker"
				groupSelectedCallback="Yukon.WaterLeakReport.schedule_group_selected_callback();" individualSelectedCallback="Yukon.WaterLeakReport.schedule_individual_selected_callback"
				uniqueId="scheduleSelector"/>
			<div class="under_tabs">
				<tags:nameValueContainer2>
					<c:if test="${not empty jobId}">
						<input type="hidden" name="jobId" value="${jobId}" id="jobId">
					</c:if>
					<tags:nameValue2 nameKey=".schedule.hoursPrevious">
						<input type="text" name="hoursPrevious" value="${not empty hoursPrevious ? hoursPrevious : '25' }" size="3">
					</tags:nameValue2>
					<tags:nameValue2 nameKey=".filter.threshold">
						<input type="text" name="threshold" value="${backingBean.threshold}" size="3">
					</tags:nameValue2>
					<tags:nameValue2 nameKey=".filter.includeDisabledDevices">
						<input type="checkbox" name="includeDisabledPaos" ${backingBean.includeDisabledPaos ? "checked" : ""}>
					</tags:nameValue2>
					<input type="hidden" name="collectionType" value="${backingBean.deviceCollection.collectionParameters['collectionType']}">
					<tags:scheduledFileExportInputs cronExpressionTagState="${cronExpressionTagState}" exportData="${fileExportData}" />
				</tags:nameValueContainer2>
			</div>
		</form:form>
	</dialog:inline>
	<input type="hidden" id="schedule_shortcut_text" value="<cti:msg2 key=".scheduleShortcutOpen"/>"/>

	<form:form id="resetForm" action="report" method="get">
		<input type="hidden" name="resetReport" value="true"/>
	</form:form>

	<div id="accountInfoAjaxDialog" class="dn"></div>

    <!-- Help messages at the top of the page -->
    <c:choose>
	    <c:when test="${usingDefaultFilter}">
	        <cti:msg2 var="current_filter_text" key=".defaultFilter"/>
	    </c:when>
	    <c:otherwise>
	        <cti:msg2 var="current_filter_text" key=".currentFilter"/>
	    </c:otherwise>
    </c:choose>
	<c:set var="current_filter">
		<a href="javascript:void(0);" class="f-open_filter_dialog">${current_filter_text}</a>
	</c:set>
	<c:set var="detection_algorithm">
		<a href="javascript:void(0);" class="f-open_detection_algorithm"><i:inline key=".leakDetectionAlgorithm"/></a>
	</c:set>
	<c:choose>
		<c:when test="${first_visit}">
			<c:set var="click_here">
				<a href="javascript:void(0);" class="f-open_filter_dialog"><i:inline key=".clickHere"/></a>
			</c:set>
			<div class="page_help">
			    <cti:list var="arguments">
			        <cti:item value="${detection_algorithm}"/>
			        <cti:item value="${click_here}"/>
			    </cti:list>
				<cti:msg2 key=".firstVisit" arguments="${arguments}" htmlEscapeArguments="false"/>
			</div>
		</c:when>
		<c:when test="${not empty from_toInstant_breach}">
		    <div class="page_error">
				<cti:list var="arguments">
                    <cti:item value="${current_filter}"/>
					<cti:item value="${from_toInstant_breach}"/>
					<cti:item value="${reporting_interval}"/>
                    <cti:item value="${detection_algorithm}"/>
				</cti:list>
				<cti:msg2 key=".filterBetweenFromAndToBreach" arguments="${arguments}" htmlEscapeArguments="false"/>
		    </div>
		</c:when>
		<c:when test="${not empty toInstant_now_breach}">
		    <div class="page_error">
				<cti:list var="arguments">
                    <cti:item value="${current_filter}"/>
					<cti:item value="${toInstant_now_breach}"/>
                    <cti:item value="${detection_algorithm}"/>
				</cti:list>
				<cti:msg2 key=".filterBetweenToAndNowBreach" arguments="${arguments}" htmlEscapeArguments="false"/>
		    </div>
		</c:when>
		<c:when test="${filterResult.hitCount == 0}">
			<div class="page_warning">
                <cti:list var="arguments">
                    <cti:item value="${detection_algorithm}"/>
                    <cti:item value="${current_filter}"/>
                </cti:list>
				<cti:msg2 key=".noResults" arguments="${arguments}" htmlEscapeArguments="false"/>
			</div>
		</c:when>
		<c:when test="${filterResult.hitCount > 0}">
			<div class="page_help">
                <cti:list var="arguments">
                    <cti:item value="${filterResult.hitCount}"/>
                    <cti:item value="${detection_algorithm}"/>
                    <cti:item value="${current_filter}"/>
                </cti:list>
				<cti:msg2 key=".foundLeaks" arguments="${arguments}" htmlEscapeArguments="false"/>
			</div>
		</c:when>
	</c:choose>

    <c:set var="actionsMenu">
        <cm:dropdown containerCssClass="fr">
            <c:if test="${collectionFromReportResults != null && filterResult.hitCount > 0}">
                <li>
                    <cti:link href="/bulk/collectionActions"
                        key="yukon.web.modules.amr.waterLeakReport.report.performCollectionAction">
                        <cti:mapParam value="${collectionFromReportResults.collectionParameters}"/>
                    </cti:link>
                </li>
                <li>
                    <a id="exportLeakCsv" href="javascript:void(0);">
                        <i:inline key=".exportCSV"/>
                    </a>
                </li>
                <li class="divider"></li>
            </c:if>
            <li>
                <a href="javascript:void(0);" class="f-open_filter_dialog">
                    <i:inline key=".filter"/>
                </a>
            </li>
            <li>
            	<a href="javascript:void(0);" class="f-open_schedule_dialog">
            		<i:inline key=".schedule"/>
            	</a>
            </li>
        </cm:dropdown>
        
        <c:if test="${collectionFromReportResults != null && filterResult.hitCount > 0}">
            <form:form id="intervalDataForm" action="intervalData" method="get" commandName="backingBean" cssClass="dib">
                <%@ include file="reportFilterFormValues.jspf"%>
                <button class="fancy" type="submit" title="<cti:msg2 key=".viewIntervalDataTitle"/>"><cti:msg2 key=".viewIntervalData"/></button>
            </form:form>
        </c:if>
    </c:set>

	<tags:pagedBox2 nameKey="tableTitle" searchResult="${filterResult}" baseUrl="report" titleLinkHtml="${actionsMenu}" styleClass="waterLeakReport">
        <tags:filteredByContainer>
            <!-- Devices -->
            <c:set var="isDeviceGroup" value="${backingBean.deviceCollection.collectionParameters['collectionType'] == 'group'}" />
            <cti:msg2 key="yukon.common.filteredBy.devices.value.individual" var="individual_value"/>
            <c:set var="devices_filter" value="${isDeviceGroup ? backingBean.deviceCollection.collectionParameters['group.name'] :
             individual_value} (${backingBean.deviceCollection.deviceCount})"/>
			<tags:filteredBy labelKey="yukon.common.filteredBy.devices.label" value="${devices_filter}"
				cssClass="${isDeviceGroup ? 'f-filter_group_clicked' : 'f-filter_individual_clicked' }" isClearable="false" />

            <!-- Dates -->
			<c:set var="dates_filter">
				<span class="date">
				    <cti:formatDate type="DATEHM" value="${backingBean.fromInstant}" />
			    </span>
			    -
			    <span class="date f-filter_to_date_clicked">
					<cti:formatDate type="DATEHM" value="${backingBean.toInstant}" />
				</span>
			</c:set>
			<tags:filteredBy labelKey="yukon.common.filteredBy.dates.label" value="${dates_filter}" cssClass="range f-filter_date_range" isClearable="false"/>

            <!-- Threshold -->
            <cti:msg2 key="yukon.common.filteredBy.threshold.value.galPerHour" argument="${backingBean.threshold}" var="threshold_value" />
			<tags:filteredBy labelKey="yukon.common.filteredBy.threshold.label" value="${threshold_value}" cssClass="f-filter_threshold_clicked" isClearable="false"/>

            <!-- Disabled Devices -->
            <c:if test="${backingBean.includeDisabledPaos}">
				<cti:msg2 key="yukon.common.filteredBy.disabledDevices.value" var="disabled_value"/>
				<tags:filteredBy labelKey="yukon.common.filteredBy.disabledDevices.label" value="${disabled_value}" cssClass="f-disabled_devices" clearClass="f-filter_reset_disabled_devices_clicked"/>
			</c:if>

            <!-- Reset link if not using defaults -->
			<c:if test="${!usingDefaultFilter}">
                <cti:msg2 key="yukon.common.filteredBy.reset.value" var="reset_value"/>
                <tags:filteredBy labelKey="yukon.common.filteredBy.reset.label" value="${reset_value}" isClearable="false" isReset="true" cssClass="f-reset_filter_submit"/>
			</c:if>
        </tags:filteredByContainer>
		<table id="leaksTable" class="compactResultsTable f-traversable has-actions">
            <c:choose>
                <c:when test="${fn:length(filterResult.resultList) > 0}">
					<thead>
						<tr>
							<c:if test="${filterResult.hitCount > 0}">
								<th class="small_width"><input id="f-check_all" type="checkbox"></th>
							</c:if>
							<th><tags:sortLink nameKey="tableHeader.deviceName" baseUrl="report" fieldName="DEVICE_NAME" isDefault="false"/></th>
							<th><tags:sortLink nameKey="tableHeader.meterNumber" baseUrl="report" fieldName="METER_NUMBER"/></th>
							<th><tags:sortLink nameKey="tableHeader.deviceType" baseUrl="report" fieldName="PAO_TYPE"/></th>
							<th><tags:sortLink nameKey="tableHeader.leakRate" baseUrl="report" fieldName="LEAK_RATE"/></th>
		                    <c:if test="${hasVendorId}"><th><i:inline key=".tableHeader.cisDetails"/></th></c:if>
		                    <th></th>
						</tr>
					</thead>
					<tfoot></tfoot>
					<tbody>
						<c:forEach var="row" items="${filterResult.resultList}">
							<tr>
								<cti:url var="accountInfoUrl" value="cisDetails">
									<cti:param name="paoId" value="${row.meter.paoIdentifier.paoId}"/>
								</cti:url>
								<input type="hidden" value="${accountInfoUrl}" class="account_info_url"/>
								<input type="hidden" value="${row.meter.paoIdentifier.paoId}" class="the_pao_id"/>
								<td class="small_width"><input type="checkbox" class="f-check_single"></td>
								<td>
		                            <cti:paoDetailUrl yukonPao="${row.meter}">
		                                ${fn:escapeXml(row.meter.name)}
									</cti:paoDetailUrl>
		                        </td>
								<td>${fn:escapeXml(row.meter.meterNumber)}</td>
								<td><tags:paoType yukonPao="${row.meter}"/></td>
								<td><i:inline key=".leakRateLabel" arguments="${row.leakRate}"/></td>
		                        <c:if test="${hasVendorId}">
									<td>
										<a href="javascript:void(0);" class="f-cis_details">
		                                    <i:inline key=".viewCISDetails"/>
										</a>
			                        </td>
		                        </c:if>
			                    <td class="contextual-menu">
			                        <cm:singleDeviceMenu deviceId="${row.meter.paoIdentifier.paoId}" containerCssClass="fr"/>
			                    </td>
							</tr>
						</c:forEach>
					</tbody>
                </c:when>
                <c:otherwise>
					<tr>
						<td class="empty-list" colspan="6">
	                        <i:inline key=".noLeaks"/>
	                    </td>
					</tr>
                </c:otherwise>
            </c:choose>
		</table>
	</tags:pagedBox2>
	<form:form id="csvLeakForm" action="csvWaterLeak" method="get"
        commandName="backingBean" cssClass="dib" title="${exportTitle}">
        <cti:deviceCollection deviceCollection="${backingBean.deviceCollection}"/>
        <tags:sortFields backingBean="${backingBean}"/>
        <%@ include file="reportFilterFormValues.jspf"%>
    </form:form>
</cti:standardPage>