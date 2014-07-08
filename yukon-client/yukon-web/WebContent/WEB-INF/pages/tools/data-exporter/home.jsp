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
            <form:form id="exporterForm" commandName="archivedValuesExporter" action="${action}">
                <cti:csrfToken/>
                <form:hidden path="archivedValuesExportFormatType"/>
                <cti:deviceCollection deviceCollection="${archivedValuesExporter.deviceCollection}" />
                <tags:nameValueContainer2 id="formatContainer" >
                    <c:if test="${not empty allFormats}">
                        <tags:nameValue2 nameKey=".existingFormat">
                            <form:select path="formatId" cssClass="fl">
                                <form:options items="${allFormats}" itemValue="formatId" title="formatName" itemLabel="formatName" />
                            </form:select>
                            <c:if test="${not empty allFormats}">
                                <cti:button nameKey="edit" icon="icon-pencil" id="b-edit" />
                                <cti:button nameKey="copy" icon="icon-page-copy" id="b-copy" />
                            </c:if>
                            <cti:button nameKey="create" icon="icon-plus-green" id="b-create" />
                        </tags:nameValue2>
                        <tags:nameValue2 nameKey="yukon.web.defaults.devices">
                            <a href="javascript:void(0);" class="selectDevices clearfix fl" title="<cti:msg2 key=".chooseDevices.tooltip"/>">
                                <c:if test="${empty archivedValuesExporter.deviceCollection.deviceCount}">
                                    <span class="empty-list"><i:inline key="yukon.web.defaults.noDevices"/></span>
                                </c:if>
                                <c:if test="${not empty archivedValuesExporter.deviceCollection.deviceCount}">
                                    <span class="b-label"><i:inline key="${archivedValuesExporter.deviceCollection.description}"/></span><i class="icon icon-folder-edit"></i>
                                </c:if>
                            </a>
                            <c:if test="${archivedValuesExporter.deviceCollection.deviceCount > 0}">
                                <tags:selectedDevicesPopup deviceCollection="${deviceCollection}"/>
                            </c:if>
                        </tags:nameValue2>
                        <c:if test="${archivedValuesExporter.deviceCollection.deviceCount > 0}">
                            <tags:nameValue2 nameKey="yukon.web.defaults.deviceCount">${archivedValuesExporter.deviceCollection.deviceCount}</tags:nameValue2>
                        </c:if>
                    </c:if>
                    <tags:nameValue2 nameKey=".attribute" rowId="attributeRow">
                        <tags:attributeSelector attributes="${groupedAttributes}" 
                            selectedAttributes="${archivedValuesExporter.attributes}"
                            fieldName="attributes"
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
                    <cti:button id="runButton" nameKey="run" title="${runScheduleTitle}" disabled="${disableRunSchedule}" classes="fl" icon="icon-page-white-excel"/>
                    <cti:button id="scheduleButton" nameKey="schedule" title="${runScheduleTitle}" disabled="${disableRunSchedule}" classes="fl" icon="icon-calendar-view-day"/>
                </c:if>
            </div>
        </tags:sectionContainer2>

        <c:if test="${not empty allFormats}">
            <h3><i:inline key=".preview.title"/></h3>
            <pre><div preview-header>${preview.header}</div><div preview-body><c:forEach items="${preview.body}" var="line">${line}<br></c:forEach></div><div preview-footer>${preview.footer}</div></pre>
        </c:if>

        <%@ include file="scheduledJobsTable.jsp" %>
    </div>

    <div id="runDialog" class="dn">
         <cti:flashScopeMessages />
         <form:form id="runForm" commandName="archivedValuesExporter" >
            <cti:csrfToken/>
             <div id="runInputsDiv">
                <div id="endDateDiv" class="END_DATE">
                    <cti:msg2 key=".endDate" var="endDate" />
                    <form:radiobutton path="runDataRange.dataRangeType" label="${endDate}" value="END_DATE" /> <br>
                    <tags:nameValueContainer2>
                        <tags:nameValue2 nameKey=".endDate"> <dt:date path="runDataRange.endDate" /> </tags:nameValue2>
                    </tags:nameValueContainer2>
                    <br>
                </div>
                
                <div id="dateRangeDiv" class="DATE_RANGE">
                    <cti:msg2 key=".dateRange" var="dateRange"/>
                    <form:radiobutton path="runDataRange.dataRangeType" label="${dateRange}" value="DATE_RANGE" /> <br>
                    <tags:nameValueContainer2>
                        <tags:nameValue2 nameKey=".startDate"> <dt:date path="runDataRange.localDateRange.startDate" /> </tags:nameValue2>
                        <tags:nameValue2 nameKey=".endDate"> <dt:date path="runDataRange.localDateRange.endDate" /> </tags:nameValue2>
                    </tags:nameValueContainer2>
                    <br>
                </div>
                
                <div id="daysPreviousDiv" class="DAYS_PREVIOUS">
                    <cti:msg2 key=".previousDays" var="daysPrevious"  />
                    <form:radiobutton path="runDataRange.dataRangeType" label="${daysPrevious}" value="DAYS_PREVIOUS" /> <br>
                    <tags:nameValueContainer2>
                        <tags:nameValue2 nameKey=".daysPrevious"> <form:input  path="runDataRange.daysPrevious"/> </tags:nameValue2>
                    </tags:nameValueContainer2>
                   <br>
                </div>
                
                <div id="sinceLastChangeIdDiv" class="SINCE_LAST_CHANGE_ID">
                    <cti:msg2 key=".sinceLastChange" var="sinceLastChange" />
                    <form:radiobutton path="runDataRange.dataRangeType" label="${sinceLastChange}" value="SINCE_LAST_CHANGE_ID" /> <br>
                </div>
             </div>
         </form:form>
    </div>
    
    <div id="scheduleDialog" class="dn">
         <cti:flashScopeMessages />
         <form:form id="scheduleForm" commandName="archivedValuesExporter" >
            <cti:csrfToken/>
             <div id="scheduleInputsDiv">
                <div id="endDateDiv" class="END_DATE">
                    <cti:msg2 key=".endDate" var="endDate" />
                    <form:radiobutton path="scheduleDataRange.dataRangeType" label="${endDate}" value="END_DATE" /> <br>
                    <tags:nameValueContainer2>
                        <tags:nameValue2 nameKey=".endDate"> <dt:date path="scheduleDataRange.endDate" /> </tags:nameValue2>
                    </tags:nameValueContainer2>
                    <br>
                </div>
                
                <div id="dateRangeDiv" class="DATE_RANGE">
                    <cti:msg2 key=".dateRange" var="dateRange"/>
                    <form:radiobutton path="scheduleDataRange.dataRangeType" label="${dateRange}" value="DATE_RANGE" /> <br>
                    <tags:nameValueContainer2>
                        <tags:nameValue2 nameKey=".startDate"> <dt:date path="scheduleDataRange.localDateRange.startDate" /> </tags:nameValue2>
                        <tags:nameValue2 nameKey=".endDate"> <dt:date path="scheduleDataRange.localDateRange.endDate" /> </tags:nameValue2>
                    </tags:nameValueContainer2>
                    <br>
                </div>
                
                <div id="daysPreviousDiv" class="DAYS_PREVIOUS">
                    <cti:msg2 key=".previousDays" var="daysPrevious"  />
                    <form:radiobutton path="scheduleDataRange.dataRangeType" label="${daysPrevious}" value="DAYS_PREVIOUS" /> <br>
                    <tags:nameValueContainer2>
                        <tags:nameValue2 nameKey=".daysPrevious"> <form:input  path="scheduleDataRange.daysPrevious"/> </tags:nameValue2>
                    </tags:nameValueContainer2>
                   <br>
                </div>
                
                <div id="sinceLastChangeIdDiv" class="SINCE_LAST_CHANGE_ID">
                    <cti:msg2 key=".sinceLastChange" var="sinceLastChange" />
                    <form:radiobutton path="scheduleDataRange.dataRangeType" label="${sinceLastChange}" value="SINCE_LAST_CHANGE_ID" /> <br>
                </div>
             </div>
         </form:form>
    </div>
    <cti:includeScript link="/JavaScript/yukon.data.exporter.js"/>
</cti:standardPage>