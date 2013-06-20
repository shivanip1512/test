<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags"%>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="dialog" tagdir="/WEB-INF/tags/dialog"%>
<%@ taglib prefix="dt" tagdir="/WEB-INF/tags/dateTime" %>

<cti:standardPage page="bulk.archivedValueExporter" module="tools">

    <cti:msg2 var="runTitle" key=".run"/>
    <cti:msg2 var="scheduleTitle" key=".schedule"/>
    <cti:msg2 var="okBtnMsg" key="yukon.common.okButton"/>
    <cti:msg2 var="cancelBtnMsg" key="yukon.common.cancel"/>
    
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

<script type="text/javascript">
function addFormatId(selector) {
    var formatIdLink = jQuery(selector);
    var formatIdFragment = "?selectedFormatId="+jQuery('#formatId').val();
    var href = formatIdLink.attr('data-href');
    formatIdLink.attr('data-href', href + formatIdFragment);
};

function addFormatIds() {
    addFormatId('#b-copy');
    addFormatId('#b-edit');
};

function showAttributeRow() {
    if (jQuery('#archivedValuesExportFormatType').val() == 'DYNAMIC_ATTRIBUTE') {
        jQuery('#attributeRow').show();
    } else {
        jQuery('#attributeRow').hide();
    }
}

function runOkPressed() {
    jQuery('#runDialog').dialog('close');
    jQuery('#runInputsDiv').clone().appendTo(jQuery('#exporterForm'));
    submitForm('generateReport');
    jQuery('#exporterForm #runInputsDiv').remove();
};

function scheduleOkPressed() {
    jQuery('#scheduleDialog').dialog('close');
    jQuery('#scheduleInputsDiv').clone().appendTo(jQuery('#exporterForm'));
    submitForm('scheduleReport');
    jQuery('#exporterForm #scheduleInputsDiv').remove();
};

function createOkCancelDialog(dialogIdentifier, titleMsg, okFunction) {
    var buttons = [];
    buttons.push({'text' : '${okBtnMsg}', 'class' : 'primary', 'click' : okFunction });
    buttons.push({'text' : '${cancelBtnMsg}', 'click' : function() { jQuery(this).dialog('close'); }});
    var dialogOpts = {
              'title' : titleMsg,
              'position' : 'center',
              'width' : 'auto',
              'height' : 'auto',
              'modal' : true,
              'buttons' : buttons };
    if(jQuery('#archivedValuesExportFormatType').val() == 'FIXED_ATTRIBUTE' && titleMsg == '${scheduleTitle}') {
        //format=fixed and scheduling, no further user feedback required
        //DataRangeType.END_DATE is the only option
        jQuery(dialogIdentifier).dialog(dialogOpts);
        okFunction();
    } else {
        jQuery(dialogIdentifier).dialog(dialogOpts);
    }
}

function submitForm(action) {
    var exporterForm = jQuery('#exporterForm');
    exporterForm.attr('action', action);
    exporterForm[0].submit();
};

function toggleForm(dialogId, archivedValuesExporterFormat, dataRangeTypes, fixedDataRangeTypes, dynamicDataRangeTypes) {

    for (var i = 0;  i < dataRangeTypes.size(); i++) {
        var dataRangeType = dataRangeTypes[i];
        var dataRangeTypeDiv= dialogId+' .'+dataRangeType;
        var dataRangeTypeInput = dataRangeTypeDiv + ' [name $= \'DataRange.dataRangeType\'] ';

        if (archivedValuesExporterFormat == 'FIXED_ATTRIBUTE' &&
            jQuery.inArray(dataRangeType, fixedDataRangeTypes) != -1 ) {
            jQuery(dataRangeTypeDiv).show();
            jQuery(dataRangeTypeInput).click();
        } else if (archivedValuesExporterFormat == 'DYNAMIC_ATTRIBUTE' &&
                jQuery.inArray(dataRangeType, dynamicDataRangeTypes) != -1) {
            jQuery(dataRangeTypeDiv).show();
            jQuery(dataRangeTypeInput).click();
            
        } else {
            jQuery(dataRangeTypeDiv).hide();
        }
    }
}

jQuery(function() {
    var dataRangeTypes = ${dataRangeTypes};
    var fixedRunDataRangeTypes = ${fixedRunDataRangeTypes};
    var dynamicRunDataRangeTypes = ${dynamicRunDataRangeTypes};
    var fixedScheduleDataRangeTypes = ${fixedScheduleDataRangeTypes};
    var dynamicScheduleDataRangeTypes = ${dynamicScheduleDataRangeTypes};
    
    showAttributeRow();
    addFormatIds();
    
    toggleForm('#runDialog', jQuery('#archivedValuesExportFormatType').val(), dataRangeTypes, fixedRunDataRangeTypes, dynamicRunDataRangeTypes);
    toggleForm('#scheduleDialog', jQuery('#archivedValuesExportFormatType').val(), dataRangeTypes, fixedScheduleDataRangeTypes, dynamicScheduleDataRangeTypes);
    
    jQuery('#runButton').click(function(event) { 
        createOkCancelDialog('#runDialog', '${runTitle}', function() { runOkPressed(); });
    });
    
    jQuery('#scheduleButton').click(function(event) { 
        createOkCancelDialog('#scheduleDialog', '${scheduleTitle}', function() { scheduleOkPressed(); });
    });
    
    jQuery('#b-create').click(function(event) {
        var buttons = [{'text' : '<cti:msg2 key="yukon.web.components.button.create.label"/>','class' : 'primary', 'click' : function() {window.location.href='create?formatType=' + jQuery('input[name=newFormatType]:checked').val();}},
                       {'text' : '${cancelBtnMsg}', 'click' : function() { jQuery(this).dialog('close');}}];
        var dialogOpts = {'buttons' : buttons, 'width' : 500, 'height' : 'auto'};
        jQuery("#create-format-dialog").dialog(dialogOpts);
    });
    
    jQuery('.selectDevices').click(function(event) {
        submitForm('selectDevices');
    });
    
    jQuery('#formatId').change(function(event) {
        addFormatIds();
        toggleForm('#runDialog', jQuery('#archivedValuesExportFormatType').val(), dataRangeTypes, fixedRunDataRangeTypes, dynamicRunDataRangeTypes);
        toggleForm('#scheduleDialog', jQuery('#archivedValuesExportFormatType').val(), dataRangeTypes, fixedScheduleDataRangeTypes, dynamicScheduleDataRangeTypes);
        showAttributeRow();
        submitForm('view');
    });
});
</script>
        <!-- Create/edit -->
        <tags:sectionContainer2 nameKey="generateReport" styleClass="stacked">
            <c:if test="${empty allFormats}">
                <span class="empty-list"><i:inline key=".noFormatsCreated"/></span>
            </c:if>
            <form:form id="exporterForm" commandName="archivedValuesExporter" action="${action}">
                <form:hidden path="archivedValuesExportFormatType"/>
                <cti:deviceCollection deviceCollection="${archivedValuesExporter.deviceCollection}" />
        
                <tags:nameValueContainer2 id="formatContainer" naturalWidth="false">
                    <c:if test="${not empty allFormats}">
                        <tags:nameValue2 nameKey=".existingFormat">
                            <form:select path="formatId" cssClass="fl">
                                <form:options items="${allFormats}" itemValue="formatId" title="formatName" itemLabel="formatName" />
                            </form:select>
                        </tags:nameValue2>
                        <tags:nameValue2 nameKey="yukon.web.defaults.devices">
                            <a href="javascript:void(0);" class="selectDevices clearfix fl" title="<cti:msg2 key=".chooseDevices.tooltip"/>">
                                <c:if test="${empty archivedValuesExporter.deviceCollection.deviceCount}">
                                    <span class="empty-list"><i:inline key="yukon.web.defaults.noDevices"/></span>
                                </c:if>
                                <c:if test="${not empty archivedValuesExporter.deviceCollection.deviceCount}">
                                    <span class="label"><i:inline key="${archivedValuesExporter.deviceCollection.description}"/></span><i class="icon icon-folder-edit"></i>
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
                            fieldName="attribute"
                            multipleSize="8"
                            groupItems="true"/>
                    </tags:nameValue2>
                </tags:nameValueContainer2>
    
            </form:form>
            <div class="actionArea">
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
                <c:if test="${not empty allFormats}">
                    <cti:button nameKey="edit" icon="icon-pencil" href="edit" id="b-edit"/>
                    <cti:button nameKey="copy" icon="icon-page-copy" href="copy" id="b-copy"/>
                </c:if>
                <cti:button nameKey="create" icon="icon-plus-green" id="b-create"/>
            </div>
        </tags:sectionContainer2>
    
    <%-- Jobs  --%>
        <tags:boxContainer2 nameKey="jobsBox">
            <div class="scrollingContainer_small" style="max-height: 249px;">
                <c:if test="${fn:length(filterResult.resultList) > 0}">
                    <table id="jobsTable" class="compactResultsTable">
                        <thead>
                            <th><i:inline key=".nameHeader"/></th>
                            <th><i:inline key=".scheduleHeader"/></th>
                            <th><i:inline key=".nextRunHeader"/></th>
                            <th><i:inline key=".actionsHeader"/></th>
                        </thead>
                        <tfoot></tfoot>
                        <tbody>
                            <c:forEach var="job" items="${filterResult.resultList}">
                                <tr>
                                    <td>${fn:escapeXml(job.name)}</td>
                                    <td>${job.cronString}</td>
                                    <td><cti:dataUpdaterValue type="JOB" identifier="${job.id}/NEXT_RUN_DATE"/></td>
                                    <td>
                                        <cti:url var="editUrl" value="scheduleReport">
                                            <cti:param name="jobId" value="${job.id}"/>
                                        </cti:url>
                                        <cti:button nameKey="edit" icon="icon-pencil" renderMode="image" href="${editUrl}"/>
                                        <cti:url var="historyUrl" value="/support/fileExportHistory/list">
                                            <cti:param name="initiator" value="Archived Data Export Schedule: ${job.name}"/>
                                        </cti:url>
                                        <cti:button nameKey="history" renderMode="image" href="${historyUrl}" icon="icon-script"/>
                                        <cti:url var="deleteUrl" value="deleteJob">
                                            <cti:param name="jobId" value="${job.id}"/>
                                        </cti:url>
                                        <cti:button id="deleteScheduleItem_${job.id}" nameKey="remove" renderMode="image" href="${deleteUrl}" icon="icon-cross"/>
                                        <dialog:confirm on="#deleteScheduleItem_${job.id}" nameKey="confirmDelete" argument="${job.name}"/>
                                    </td>
                                </tr>
                            </c:forEach>
                        </tbody>
                    </table>
                </c:if>
                <c:if test="${fn:length(filterResult.resultList) == 0}">
                    <span class="empty-list" colspan="3"><i:inline key=".noJobs"/></span>
                </c:if>
            </div>
        </tags:boxContainer2>
    </div>
    
    <div id="runDialog" class="dn">
         <cti:flashScopeMessages />
         <form:form id="runForm" commandName="archivedValuesExporter" >
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

    <c:if test="${not empty allFormats}">
        <tags:boxContainer2 nameKey="preview" styleClass="stacked clear">
            <div class="code">
            <!-- Please do not format this code -->
<pre><c:forEach var="previewEntry" items="${preview}">${previewEntry}
</c:forEach></pre>
            </div>
        </tags:boxContainer2>
    </c:if>

</cti:standardPage>