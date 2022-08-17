<%@ page trimDirectiveWhitespaces="true" %>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cm" tagdir="/WEB-INF/tags/contextualMenu" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="d" tagdir="/WEB-INF/tags/dialog" %>
<%@ taglib prefix="dt" tagdir="/WEB-INF/tags/dateTime" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<cti:standardPage page="bulk.archivedValueExporter" module="tools">

    <cti:includeScript link="YUKON_TIME_FORMATTER"/>

    <cti:toJson id="module-config" object="${jsConfig}"/>

    <div id="create-format-dialog" title="<cti:msg2 key=".createNewFormat.title"/>" class="dn">
        <h4 class="db stacked"><i:inline key=".selectFormatType"/></h4>
        <div class="stacked">
            <div><label><input type="radio" name="newFormatType" checked="checked" value="FIXED_ATTRIBUTE"><i:inline key="${fixedAttribute}"/></label></div>
            <p class="detail" style="margin-left: 30px;"><i:inline key="${fixedAttribute.description}"/></p>
        </div>
        <div class="stacked">
            <div><label><input type="radio" name="newFormatType" value="DYNAMIC_ATTRIBUTE"><i:inline key="${dynamicAttribute}"/></label></div>
            <p class="detail" style="margin-left: 30px;"><i:inline key="${dynamicAttribute.description}"/></p>
        </div>
    </div>

        <!-- Create/edit -->
        <tags:sectionContainer2 nameKey="generateReport" styleClass="stacked">
            <c:if test="${empty allFormats}">
                <div class="empty-list stacked"><i:inline key=".noFormatsCreated"/></div>
                <cti:button nameKey="create" icon="icon-plus-green" id="b-create"/>
            </c:if>
            <form:form id="exporter-form" modelAttribute="archivedValuesExporter" action="${action}">
                <cti:csrfToken/>
                <form:hidden id="format-type" path="archivedValuesExportFormatType"/>
                <cti:deviceCollection deviceCollection="${archivedValuesExporter.deviceCollection}"/>
                <tags:nameValueContainer2 tableClass="with-form-controls">
                    <c:if test="${not empty allFormats}">
                        <tags:nameValue2 nameKey=".existingFormat">
                            <form:select id="format-id" path="formatId" cssClass="fl">
                                <form:options items="${allFormats}" itemValue="formatId" title="formatName" itemLabel="formatName"/>
                            </form:select>
                            <c:if test="${not empty allFormats}">
                                <cti:button nameKey="edit" icon="icon-pencil" id="b-edit"/>
                                <cti:button nameKey="copy" icon="icon-page-copy" id="b-copy"/>
                            </c:if>
                            <cti:button nameKey="create" icon="icon-plus-green" id="b-create"/>
                        </tags:nameValue2>
                        <tags:nameValue2 nameKey="yukon.common.Devices">
                            <a href="javascript:void(0);" class="selectDevices clearfix fl" title="<cti:msg2 key="yukon.common.chooseDevices.tooltip"/>">
                                <c:if test="${empty archivedValuesExporter.deviceCollection.deviceCount}">
                                    <span class="empty-list fl"><i:inline key="yukon.common.noDevices"/></span>
                                </c:if>
                                <c:if test="${not empty archivedValuesExporter.deviceCollection.deviceCount}">
                                    <span class="b-label fl"><i:inline key="${archivedValuesExporter.deviceCollection.description}" htmlEscape="true"/></span>
                                </c:if>
                                <i class="icon icon-folder-edit"></i>
                            </a>
                            <c:if test="${archivedValuesExporter.deviceCollection.deviceCount > 0}">
                                <tags:selectedDevicesPopup deviceCollection="${deviceCollection}"/>
                            </c:if>
                        </tags:nameValue2>
                        <c:if test="${archivedValuesExporter.deviceCollection.deviceCount > 0}">
                            <tags:nameValue2 nameKey="yukon.common.deviceCount">${archivedValuesExporter.deviceCollection.deviceCount}</tags:nameValue2>
                        </c:if>
                    </c:if>
                    <tags:nameValue2 nameKey=".attribute" rowId="attributeRow">
                        <tags:attributeSelector id="attribute-select"
                            attributes="${groupedAttributes}" 
                            selectedAttributes="${archivedValuesExporter.attributes}"
                            name="attributes"
                            multipleSize="8"
                            groupItems="true"/>
                    </tags:nameValue2>
                </tags:nameValueContainer2>
            </form:form>
            <div class="action-area">
                <c:if test="${not empty allFormats}">
                    <c:choose>
                        <c:when test="${empty deviceCollection}">
                            <c:set var="disableRunSchedule" value="true"/>
                            <cti:msg2 var="runScheduleTitle" key=".noDevices.tooltip"/>
                        </c:when>
                        <c:otherwise>
                            <c:set var="disableRunSchedule" value="false"/>
                        </c:otherwise>
                    </c:choose>
                    <cti:button id="run-btn" nameKey="run" title="${runScheduleTitle}" disabled="${disableRunSchedule}" classes="fl" icon="icon-page-white-excel"/>
                    <cti:button id="schedule-btn" nameKey="schedule" title="${runScheduleTitle}" disabled="${disableRunSchedule}" classes="fl" icon="icon-calendar-view-day"/>
                </c:if>
            </div>
        </tags:sectionContainer2>

        <c:if test="${not empty allFormats}">
            <h3><i:inline key=".preview.title"/></h3>
            <pre><div><i:inline key=".preview.rulerNumbers"/></div><div><i:inline key=".preview.rulerMarks"/></div><div preview-header>${fn:escapeXml(preview.header)}</div><div preview-body><c:forEach items="${preview.body}" var="line">${fn:escapeXml(line)}<br></c:forEach></div><div preview-footer>${fn:escapeXml(preview.footer)}</div></pre>
        </c:if>

        <%@ include file="scheduledJobsTable.jsp" %>
    </div>

    <div id="run-dialog" class="dn">
         <cti:flashScopeMessages />
         <form:form modelAttribute="archivedValuesExporter" >
            <cti:csrfToken/>
             <div class="js-run-inputs">
                <div class="stacked js-fixed">
                    <cti:msg2 key=".endDate" var="endDate"/>
                    <span class="fl">
                        <form:radiobutton path="runDataRange.dataRangeType" label="${endDate}" value="END_DATE" />&nbsp;
                    </span>
                    <dt:date path="runDataRange.endDate" cssClass="js-focus" />
                </div>

                 <div class="stacked js-dynamic clearfix">
                    <cti:msg2 key=".dateRange" var="dateRange"/>
                    <span class="fl">
                        <form:radiobutton path="runDataRange.dataRangeType" label="${dateRange}" value="DATE_RANGE"/>&nbsp;
                    </span>
                    <dt:dateRange startPath="runDataRange.localDateRange.startDate" endPath="runDataRange.localDateRange.endDate" cssClass="js-focus"/>
                </div>

                <div class="stacked js-dynamic clearfix">
                    <cti:msg2 key=".previousDays" var="daysPrevious"  />
                    <form:radiobutton path="runDataRange.dataRangeType" label="${daysPrevious}" value="DAYS_PREVIOUS"/>&nbsp;
                    <form:input path="runDataRange.daysPrevious" size="4" maxlength="4"/>
                </div>

                 <div class="stacked js-dynamic">
                    <hr>
                    <label class="form-control dib fl">
                        <form:checkbox path="runDataRange.timeSelected" cssClass="js-time-check"/>
                        <i:inline key=".time"/>&nbsp;<cti:msg2 key=".sinceLastChange" var="sinceLastChange"/>
                    </label>
                    <dt:time path="runDataRange.time" cssClass="js-time"/>
                </div>
             </div>
         </form:form>
    </div>

    <div id="schedule-dialog" class="dn">
         <cti:flashScopeMessages />
         <form:form id="scheduleForm" modelAttribute="archivedValuesExporter" >
            <cti:csrfToken/>
             <div class="js-schedule-inputs">
                <div class="stacked js-fixed">
                    <cti:msg2 key=".daysOffset" var="daysOffset"/>
                    <form:radiobutton path="scheduleDataRange.dataRangeType" label="${daysOffset}" value="DAYS_OFFSET"/>&nbsp;
                    <form:input path="scheduleDataRange.daysOffset" maxlength="4" size="4" cssClass="js-focus"/>
                </div>

                <div class="stacked js-dynamic">
                    <cti:msg2 key=".previousDays" var="daysPrevious"/>
                    <form:radiobutton path="scheduleDataRange.dataRangeType" label="${daysPrevious}" value="DAYS_PREVIOUS"/>&nbsp;
                    <form:input path="scheduleDataRange.daysPrevious" maxlength="4" size="4" cssClass="js-focus"/>
                </div>

                <div class="stacked js-dynamic">
                    <cti:msg2 key=".sinceLastChange" var="sinceLastChange"/>
                    <form:radiobutton path="scheduleDataRange.dataRangeType" label="${sinceLastChange}" value="SINCE_LAST_CHANGE_ID"/>
                </div>

                <div class="stacked js-dynamic">
                    <hr>
                    <label class="form-control dib fl">
                        <form:checkbox path="scheduleDataRange.timeSelected" cssClass="js-time-check"/>
                        <i:inline key=".time"/>&nbsp;<cti:msg2 key=".sinceLastChange" var="sinceLastChange"/>
                    </label>
                    <dt:time path="scheduleDataRange.time" cssClass="js-time"/>
                </div>
             </div>
         </form:form>
    </div>

    <cti:includeScript link="/resources/js/pages/yukon.tools.data.exporter.js"/>
    <dt:pickerIncludes/>
</cti:standardPage>