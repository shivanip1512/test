<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags"%>
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

	<cti:msg2 var="filterButton" key="yukon.web.components.button.filter.label"/>
	<cti:msg2 var="filterButtonTitle" key=".filterButtonTitle"/>
	<cti:msg2 var="resetButtonTitle" key=".filterResetButtonTitle"/>
	<cti:msg2 var="resetButton" key="yukon.web.components.button.reset.label"/>
	<cti:msg2 var="cancelButton" key="yukon.web.components.button.cancel.label"/>
	<cti:msg2 var="cancelButtonTitle" key=".filterShortcutClose"/>

	<dialog:inline id="leakFilterDialog" okEvent="none" nameKey="leakFilterDialog" on=".f_open_filter_dialog"
		options="{width: 550, 'buttons': [{text: '${filterButton}', click: function() { WaterLeakReport.filter_submit(); }, title: '${filterButtonTitle}', 'class': 'leakFilterSubmitButton' },
                                          {text: '${resetButton}', click: function() { WaterLeakReport.reset_filter_submit(); }, title: '${resetButtonTitle}' },
                                          {text: '${cancelButton}', click: function() { jQuery(this).dialog('close'); }, title: '${cancelButtonTitle}', 'class': 'naked leakFilterCancelButton anchorUnderlineHover' }]}">
		<form:form id="filterForm" action="report" method="get" commandName="backingBean">
			<tags:sortFields backingBean="${backingBean}"/>
			<tags:selectDevicesTabbed deviceCollection="${backingBean.deviceCollection}" tabClass="waterLeakFilterTab" individualPickerType="waterMeterPicker"/>
			<div class="under_tabs">
				<tags:nameValueContainer2>
					<tags:nameValue2 nameKey=".filter.fromDate">
						<dt:dateTime path="fromInstant" value="${backingBean.fromInstant}" cssClass="f_from_datetime f_focus"
							stepMinute="60" stepHour="1" cssDialogClass="hide_minutes" />
					</tags:nameValue2>
					<tags:nameValue2 nameKey=".filter.toDate">
						<dt:dateTime path="toInstant" value="${backingBean.toInstant}" cssClass="f_to_datetime"
							stepMinute="60" stepHour="1" cssDialogClass="hide_minutes" />
					</tags:nameValue2>

                    <tags:nameValue2 nameKey=".filter.threshold">
                        <tags:input path="threshold" size="3" inputClass="threshold_input f_threshold"/>
						<span class="focusableFieldHolder">
                            <a class="icon icon_help"><i:inline key=".filter.helpText" /></a>
						</span>
						<span class="focusedFieldDescription"><i:inline key=".filter.threshold.helpText"/></span>
                    </tags:nameValue2>

					<tags:nameValue2 nameKey=".filter.includeDisabledDevices">
						<form:checkbox path="includeDisabledPaos"
							cssClass="f_include_disabled_paos"/>
					</tags:nameValue2>
				</tags:nameValueContainer2>
			</div>
		</form:form>
	</dialog:inline>
	<input type="hidden" id="filter_shortcut_text" value="<cti:msg2 key=".filterShortcutOpen"/>"/>

    <%@ include file="leakAlgorithmDialog.jspf"%>

	<form:form id="resetForm" action="report" method="get">
		<input type="hidden" name="resetReport" value="true"/>
	</form:form>

	<form:form id="csvLeakForm" action="csvWaterLeak" method="get"
		commandName="backingBean" cssClass="dib" title="${exportTitle}">
		<cti:deviceCollection deviceCollection="${backingBean.deviceCollection}"/>
		<tags:sortFields backingBean="${backingBean}"/>
		<%@ include file="reportFilterFormValues.jspf"%>
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
		<a href="javascript:void(0);" class="f_open_filter_dialog">${current_filter_text}</a>
	</c:set>
	<c:set var="detection_algorithm">
		<a href="javascript:void(0);" class="f_open_detection_algorithm"><i:inline key=".leakDetectionAlgorithm"/></a>
	</c:set>
	<c:choose>
		<c:when test="${first_visit}">
			<c:set var="click_here">
				<a href="javascript:void(0);" class="f_open_filter_dialog"><i:inline key=".clickHere"/></a>
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
				</cti:list>
				<cti:msg2 key=".filterBetweenFromAndToBreach" arguments="${arguments}" htmlEscapeArguments="false"/>
		    </div>
		</c:when>
		<c:when test="${not empty toInstant_now_breach}">
		    <div class="page_error">
				<cti:list var="arguments">
                    <cti:item value="${current_filter}"/>
					<cti:item value="${toInstant_now_breach}"/>
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
        <tags:dropdownActions>
            <c:if test="${collectionFromReportResults != null && filterResult.hitCount > 0}">
                <li>
                    <cti:link href="/spring/bulk/collectionActions"
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
                <a href="javascript:void(0);" class="f_open_filter_dialog">
                    <i:inline key=".filter"/>
                </a>
            </li>
        </tags:dropdownActions>
        
        <c:if test="${collectionFromReportResults != null && filterResult.hitCount > 0}">
            <form:form id="intervalDataForm" action="intervalData" method="get" commandName="backingBean" cssClass="">
                <%@ include file="reportFilterFormValues.jspf"%>
                <button class="fancy" type="submit" title="<cti:msg2 key=".viewIntervalDataTitle"/>"><cti:msg2 key=".viewIntervalData"/></button>
            </form:form>
        </c:if>
    </c:set>

	<tags:pagedBox2 nameKey="tableTitle" searchResult="${filterResult}" baseUrl="report" titleLinkHtml="${actionsMenu}" styleClass="waterLeakReport">
        <tags:filteredByContainer>
            <!-- Devices -->
            <c:set var="isDeviceGroup" value="${backingBean.deviceCollection.collectionParameters['collectionType'] == 'group'}" />
            <cti:msg2 key=".filteredBy.devices.value.individual" var="individual_value"/>
            <c:set var="devices_filter" value="${isDeviceGroup ? backingBean.deviceCollection.collectionParameters['group.name'] :
             individual_value} (${backingBean.deviceCollection.deviceCount})"/>
			<tags:filteredBy labelKey=".filteredBy.devices.label" value="${devices_filter}"
				cssClass="${isDeviceGroup ? 'device_group' : 'individual' }" isClearable="false" />

            <!-- Dates -->
			<c:set var="dates_filter">
				<span class="date from">
				    <cti:formatDate type="DATEHM" value="${backingBean.fromInstant}" />
			    </span>
			    -
			    <span class="date to">
					<cti:formatDate type="DATEHM" value="${backingBean.toInstant}" />
				</span>
			</c:set>
			<tags:filteredBy labelKey=".filteredBy.dates.label" value="${dates_filter}" cssClass="range" isClearable="false"/>

            <!-- Threshold -->
            <cti:msg2 key=".filteredBy.threshold.value" argument="${backingBean.threshold}" var="threshold_value" />
			<tags:filteredBy labelKey=".filteredBy.threshold.label" value="${threshold_value}" cssClass="threshold" isClearable="false"/>

            <!-- Disabled Devices -->
            <c:if test="${backingBean.includeDisabledPaos}">
				<cti:msg2 key=".filteredBy.disabledDevices.value" var="disabled_value"/>
				<tags:filteredBy labelKey=".filteredBy.disabledDevices.label" value="${disabled_value}" cssClass="disabled_devices" clearClass="reset_disabled_devices"/>
			</c:if>

            <!-- Reset link if not using defaults -->
			<c:if test="${!usingDefaultFilter}">
                <cti:msg2 key=".filteredBy.reset.value" var="reset_value"/>
                <tags:filteredBy labelKey=".filteredBy.reset.label" value="${reset_value}" isClearable="false" isReset="true"/>
			</c:if>
        </tags:filteredByContainer>
		<table id="leaksTable" class="compactResultsTable">
            <c:choose>
                <c:when test="${fn:length(filterResult.resultList) > 0}">
					<thead>
						<tr>
							<c:if test="${filterResult.hitCount > 0}">
								<th class="small_width"><input id="f_check_all" type="checkbox"></th>
							</c:if>
							<th><tags:sortLink nameKey="tableHeader.deviceName" baseUrl="report" fieldName="DEVICE_NAME" isDefault="false"/></th>
							<th><tags:sortLink nameKey="tableHeader.meterNumber" baseUrl="report" fieldName="METER_NUMBER"/></th>
							<th><tags:sortLink nameKey="tableHeader.deviceType" baseUrl="report" fieldName="PAO_TYPE"/></th>
							<th><tags:sortLink nameKey="tableHeader.leakRate" baseUrl="report" fieldName="LEAK_RATE"/></th>
		                    <c:if test="${hasVendorId}"><th><i:inline key=".tableHeader.cisDetails"/></th></c:if>
						</tr>
					</thead>
					<tbody>
						<c:forEach var="row" items="${filterResult.resultList}">
							<c:set var="trClass" value=""/>
							<c:if test="${row.meter.disabled}">
								<c:set var="trClass" value="subtleGray"/>
							</c:if>
							<tr class="<tags:alternateRow odd="" even="altRow"/> ${trClass}">
								<cti:url var="accountInfoUrl" value="cisDetails">
									<cti:param name="paoId" value="${row.meter.paoIdentifier.paoId}"/>
								</cti:url>
								<input type="hidden" value="${accountInfoUrl}" class="account_info_url"/>
								<input type="hidden" value="${row.meter.paoIdentifier.paoId}" class="the_pao_id"/>
								<td class="small_width"><input type="checkbox" class="f_check_single"></td>
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
										<a href="javascript:void(0);" class="f_cis_details">
		                                    <i:inline key=".viewCISDetails"/>
										</a>
			                        </td>
		                        </c:if>
							</tr>
						</c:forEach>
					</tbody>
                </c:when>
                <c:otherwise>
					<tr>
						<td class="noResults subtleGray" colspan="3">
	                        <i:inline key=".noLeaks"/>
	                    </td>
					</tr>
                </c:otherwise>
            </c:choose>
		</table>
	</tags:pagedBox2>
</cti:standardPage>