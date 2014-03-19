<%@ page trimDirectiveWhitespaces="true" %>

<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags"%>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="d" tagdir="/WEB-INF/tags/dialog"%>
<%@ taglib prefix="dt" tagdir="/WEB-INF/tags/dateTime" %>
<%@ taglib prefix="cm" tagdir="/WEB-INF/tags/contextualMenu" %>

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
    var formatIdLink = $(selector);
    var formatIdFragment = "?selectedFormatId="+$('#formatId').val();
    var href = formatIdLink.attr('data-href');
    formatIdLink.attr('data-href', href + formatIdFragment);
};

function addFormatIds() {
    addFormatId('#b-copy');
    addFormatId('#b-edit');
};

function showAttributeRow() {
    if ($('#archivedValuesExportFormatType').val() == 'DYNAMIC_ATTRIBUTE') {
        $('#attributeRow').show();
    } else {
        $('#attributeRow').hide();
    }
}

function runOkPressed() {
    $('#runDialog').dialog('close');
    $('#runInputsDiv').clone().appendTo($('#exporterForm'));
    submitForm('generateReport');
    $('#exporterForm #runInputsDiv').remove();
};

function scheduleOkPressed() {
    $('#scheduleDialog').dialog('close');
    $('#scheduleInputsDiv').clone().appendTo($('#exporterForm'));
    submitForm('scheduleReport');
    $('#exporterForm #scheduleInputsDiv').remove();
};

function createOkCancelDialog(dialogIdentifier, titleMsg, okFunction) {
    var buttons = [];
    buttons.push({'text' : '${okBtnMsg}', 'class' : 'primary', 'click' : okFunction });
    buttons.push({'text' : '${cancelBtnMsg}', 'click' : function() { $(this).dialog('close'); }});
    var dialogOpts = {
              'title' : titleMsg,
              'position' : 'center',
              'width' : 'auto',
              'height' : 'auto',
              'modal' : true,
              'buttons' : buttons };
    if($('#archivedValuesExportFormatType').val() == 'FIXED_ATTRIBUTE' && titleMsg == '${scheduleTitle}') {
        //format=fixed and scheduling, no further user feedback required
        //DataRangeType.END_DATE is the only option
        $(dialogIdentifier).dialog(dialogOpts);
        okFunction();
    } else {
        $(dialogIdentifier).dialog(dialogOpts);
    }
}

function submitForm(action) {
    var exporterForm = $('#exporterForm');
    exporterForm.attr('action', action);
    exporterForm[0].submit();
};

function toggleForm(dialogId, archivedValuesExporterFormat, dataRangeTypes, fixedDataRangeTypes, dynamicDataRangeTypes) {

    for (var i = 0;  i < dataRangeTypes.size(); i++) {
        var dataRangeType = dataRangeTypes[i];
        var dataRangeTypeDiv= dialogId+' .'+dataRangeType;
        var dataRangeTypeInput = dataRangeTypeDiv + ' [name $= \'DataRange.dataRangeType\'] ';

        if (archivedValuesExporterFormat == 'FIXED_ATTRIBUTE' &&
            $.inArray(dataRangeType, fixedDataRangeTypes) != -1 ) {
            $(dataRangeTypeDiv).show();
            $(dataRangeTypeInput).click();
        } else if (archivedValuesExporterFormat == 'DYNAMIC_ATTRIBUTE' &&
                $.inArray(dataRangeType, dynamicDataRangeTypes) != -1) {
            $(dataRangeTypeDiv).show();
            $(dataRangeTypeInput).click();
            
        } else {
            $(dataRangeTypeDiv).hide();
        }
    }
}

$(function() {
    var dataRangeTypes = ${dataRangeTypes};
    var fixedRunDataRangeTypes = ${fixedRunDataRangeTypes};
    var dynamicRunDataRangeTypes = ${dynamicRunDataRangeTypes};
    var fixedScheduleDataRangeTypes = ${fixedScheduleDataRangeTypes};
    var dynamicScheduleDataRangeTypes = ${dynamicScheduleDataRangeTypes};
    
    showAttributeRow();
    addFormatIds();
    
    toggleForm('#runDialog', $('#archivedValuesExportFormatType').val(), dataRangeTypes, fixedRunDataRangeTypes, dynamicRunDataRangeTypes);
    toggleForm('#scheduleDialog', $('#archivedValuesExportFormatType').val(), dataRangeTypes, fixedScheduleDataRangeTypes, dynamicScheduleDataRangeTypes);
    
    $('#runButton').click(function(event) { 
        createOkCancelDialog('#runDialog', '${runTitle}', function() { runOkPressed(); });
    });
    
    $('#scheduleButton').click(function(event) { 
        createOkCancelDialog('#scheduleDialog', '${scheduleTitle}', function() { scheduleOkPressed(); });
    });
    
    $('#b-create').click(function(event) {
        var buttons = [{'text' : '<cti:msg2 key="yukon.web.components.button.create.label"/>','class' : 'primary', 
            'click' : function() {window.location.href='create?formatType=' + 
                    $('input[name=newFormatType]:checked').val();}},
                       {'text' : '${cancelBtnMsg}', 'click' : function() { $(this).dialog('close');}}];
        var dialogOpts = {'buttons' : buttons, 'width' : 500, 'height' : 'auto'};
        $("#create-format-dialog").dialog(dialogOpts);
    });
    
    $('.selectDevices').click(function(event) {
        submitForm('selectDevices');
    });
    
    $('#formatId').change(function(event) {
        addFormatIds();
        toggleForm('#runDialog', $('#archivedValuesExportFormatType').val(), dataRangeTypes, fixedRunDataRangeTypes, dynamicRunDataRangeTypes);
        toggleForm('#scheduleDialog', $('#archivedValuesExportFormatType').val(), dataRangeTypes, fixedScheduleDataRangeTypes, dynamicScheduleDataRangeTypes);
        showAttributeRow();
        submitForm('view');
    });
});
</script>
        <!-- Create/edit -->
        <tags:sectionContainer2 nameKey="generateReport" styleClass="stacked">
            <c:if test="${empty allFormats}">
                <div class="empty-list stacked"><i:inline key=".noFormatsCreated"/></div>
                <cti:button nameKey="create" icon="icon-plus-green" id="b-create"/>
            </c:if>
            <form:form id="exporterForm" commandName="archivedValuesExporter" action="${action}">
                <form:hidden path="archivedValuesExportFormatType"/>
                <cti:deviceCollection deviceCollection="${archivedValuesExporter.deviceCollection}" />
                <tags:nameValueContainer2 id="formatContainer" >
                    <c:if test="${not empty allFormats}">
                        <tags:nameValue2 nameKey=".existingFormat">
                            <form:select path="formatId" cssClass="fl">
                                <form:options items="${allFormats}" itemValue="formatId" title="formatName" itemLabel="formatName" />
                            </form:select>
                            <c:if test="${not empty allFormats}">
                                <cti:button nameKey="edit" icon="icon-pencil" href="edit" id="b-edit" />
                                <cti:button nameKey="copy" icon="icon-page-copy" href="copy" id="b-copy" />
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
            <pre><c:forEach var="previewEntry" items="${preview}">${fn:escapeXml(previewEntry)}</c:forEach></pre>
        </c:if>

        <div class="f-table-container" data-reloadable>
            <%@ include file="scheduledJobsTable.jsp" %>
        </div>
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
</cti:standardPage>