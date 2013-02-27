<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags"%>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="dialog" tagdir="/WEB-INF/tags/dialog"%>
<%@ taglib prefix="dt" tagdir="/WEB-INF/tags/dateTime" %>

<cti:standardPage page="archivedValueExporter" module="amr">
    <cti:msg2 var="runTitle" key=".run"/>
    <cti:msg2 var="scheduleTitle" key=".schedule"/>
    <cti:msg2 var="okBtnMsg" key="yukon.common.okButton"/>
    <cti:msg2 var="cancelBtnMsg" key="yukon.common.cancel"/>

    <script type="text/javascript">
        function addFormatIdToLink(linkHref) {
        	var formatIdLink = jQuery('.'+linkHref);
        	var formatIdFragment = "?selectedFormatId="+jQuery('#formatId').val();
            formatIdLink.attr('data-href', linkHref + formatIdFragment);
        };

        function addFormatTypeToLink(linkHref) {
            var formatTypeLink = jQuery('.'+linkHref);
            var formatTypeFragment = "?formatType="+jQuery('#formatType').val();
            formatTypeLink.attr('data-href', linkHref + formatTypeFragment);
        };

        function addFormatIdToLinks() {
            addFormatIdToLink('copy');
            addFormatIdToLink('edit');
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
            buttons.push({'text' : '${okBtnMsg}', 'click' : okFunction });
            buttons.push({'text' : '${cancelBtnMsg}', 'click' : function() { jQuery(this).dialog('close'); }});
            var dialogOpts = {
                      'title' : titleMsg,
                      'position' : 'center',
                      'width' : 'auto',
                      'height' : 'auto',
                      'modal' : true,
                      'buttons' : buttons };
            jQuery(dialogIdentifier).dialog(dialogOpts);
        }

        function submitForm(action) {
            var exporterForm = jQuery('#exporterForm');
            exporterForm.attr('action', action);
            exporterForm[0].submit();
        };
        
        function toggleForm(dialogId, archivedValuesExporterFormat, dataRangeTypes, fixedDataRangeTypes, dynamicDataRangeTypes) {
        	for (var i = 0;  i < dataRangeTypes.size(); i++) {
        		var dataRangeType = dataRangeTypes[i];
        		var  dataRangeTypeDiv= dialogId+' .'+dataRangeType;
        		if (archivedValuesExporterFormat == 'FIXED_ATTRIBUTE' &&
        			jQuery.inArray(dataRangeType, fixedDataRangeTypes) != -1 ) {
                    jQuery(dataRangeTypeDiv).show();
        			
        		} else if (archivedValuesExporterFormat == 'DYNAMIC_ATTRIBUTE' &&
        				jQuery.inArray(dataRangeType, dynamicDataRangeTypes) != -1) {
                    jQuery(dataRangeTypeDiv).show();
        			
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
        	var dynamicScheduleDataRangeTypes = ${dynamicRunDataRangeTypes};
        	
        	showAttributeRow();
        	
        	addFormatTypeToLink('create');
        	addFormatIdToLinks();
        	
        	jQuery('#runDialog').hide();
        	jQuery('#scheduleDialog').hide();
            toggleForm('#runDialog', jQuery('#archivedValuesExportFormatType').val(), dataRangeTypes, fixedRunDataRangeTypes, dynamicRunDataRangeTypes);
            toggleForm('#scheduleDialog', jQuery('#archivedValuesExportFormatType').val(), dataRangeTypes, fixedScheduleDataRangeTypes, dynamicScheduleDataRangeTypes);

        	
            jQuery('#runButton').click(function(event) { 
            	createOkCancelDialog('#runDialog', '${runTitle}', function() { runOkPressed(); });
            });
            jQuery('#scheduleButton').click(function(event) { 
                createOkCancelDialog('#scheduleDialog', '${scheduleTitle}', function() { scheduleOkPressed(); });
            });

            jQuery('.selectDevices, #selectDevicesBtn1').click(function(event) {
                submitForm('selectDevices');
            });
            jQuery('#formatId').change(function(event) {
            	addFormatIdToLinks();
                toggleForm('#runDialog', jQuery('#archivedValuesExportFormatType').val(), dataRangeTypes, fixedRunDataRangeTypes, dynamicRunDataRangeTypes);
                toggleForm('#scheduleDialog', jQuery('#archivedValuesExportFormatType').val(), dataRangeTypes, fixedScheduleDataRangeTypes, dynamicScheduleDataRangeTypes);
                showAttributeRow();
                submitForm('view');
            });
            jQuery('#formatType').change(function(event) {
            	addFormatTypeToLink('create');
            });
        });
    </script>

    <form:form id="exporterForm" commandName="archivedValuesExporter" action="${action}">
        <form:hidden path="archivedValuesExportFormatType"/>
        <cti:deviceCollection deviceCollection="${archivedValuesExporter.deviceCollection}" />

        <div class="smallBoldLabel notesSection stacked">
            <c:choose>
                <c:when test="${empty archivedValuesExporter.deviceCollection}">
                    <i:inline key=".noSelectedDevice" />
                </c:when>
                <c:otherwise>
                    <div class="fl">
                        <tags:selectedDevices id="deviceColletion" deviceCollection="${archivedValuesExporter.deviceCollection}" />
                    </div>
                    <a href="javascript:void();" class="icon icon_folder_edit selectDevices fl" title="<i:inline key=".iconFolderEditDeviceGroup"/>"><i:inline key=".iconFolderEditDeviceGroup"/></a>
                </c:otherwise>
            </c:choose>
            &nbsp &nbsp
            <c:if test="${empty deviceCollection}">
                <cti:button id="selectDevicesBtn1" nameKey="selectDevices" />
            </c:if>
        </div>
        <br>

        <tags:boxContainer2 nameKey="generateReport" styleClass="stacked">
            <tags:nameValueContainer2 id="formatContainer" tableClass="stacked clear">
                <tags:nameValue2 nameKey=".formatType">
                    <select id="formatType">
                        <c:forEach var="formatType" items="${formatTypes}">
                            <option value="${formatType}"><i:inline key="${formatType}" /></option>
                        </c:forEach>
                    </select>
                    <cti:button nameKey="create" href="create" styleClass="create"/>
                </tags:nameValue2>
                <tags:nameValue2 nameKey=".existingFormat">
                    <c:if test="${not empty allFormats}">
                        <form:select path="formatId" cssClass="fl" cssStyle="margin-right:5px;">
                            <form:options items="${allFormats}" itemValue="formatId" title="formatName" itemLabel="formatName" />
                        </form:select>
                        <cti:button nameKey="edit" href="edit" styleClass="edit" />
                        <cti:button nameKey="copy" href="copy" styleClass="copy" />
                    </c:if>
                </tags:nameValue2>
                
                <tags:selectNameValue  rowId="attributeRow" nameKey=".attribute" path="attribute" items="${groupedAttributes}" itemLabel="message" itemValue="key" groupItems="true"/>
            </tags:nameValueContainer2>

            <c:if test="${not empty allFormats}">

                <c:choose>
                    <c:when test="${empty deviceCollection}">
                        <cti:button id="runButton" nameKey="run" disabled="true"/> <cti:button id="scheduleButton" nameKey="schedule" disabled="true"/>
                    </c:when>
                    <c:otherwise>
                        <cti:button id="runButton" nameKey="run"/> <cti:button id="scheduleButton" nameKey="schedule"/>
                    </c:otherwise>
                </c:choose>
                
            </c:if>
        </tags:boxContainer2>
    </form:form>

                <div id="runDialog" >
                     <cti:flashScopeMessages />
                     <form:form id="runForm" commandName="archivedValuesExporter" >
                         <div id="runInputsDiv">
                             <%@ include file="report.jspf" %>
                         </div>
                     </form:form>
                </div>
                
<!--                 <div id="scheduleDialog" > -->
<%--                      <cti:flashScopeMessages /> --%>
<%--                      <form:form id="scheduleForm" commandName="archivedValuesExporter" > --%>
<!--                          <div id="scheduleInputsDiv"> -->
<%--                              <%@ include file="report.jspf" %> --%>
<!--                          </div> -->
<%--                      </form:form> --%>
<!--                 </div> -->

    <c:if test="${not empty allFormats}">
        <tags:boxContainer2 nameKey="preview" styleClass="stacked">
            <div class="code">
            <!-- Please do not format this code -->
<pre><c:forEach var="previewEntry" items="${preview}">${previewEntry}
</c:forEach></pre>
            </div>
        </tags:boxContainer2>
    </c:if>

</cti:standardPage>