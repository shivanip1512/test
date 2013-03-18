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
	<div class="colmask one-third-left clearfix">
    <div class="colleft">
        <div class="col1">
		<!-- Create/edit -->
		    <tags:boxContainer2 nameKey="generateReport" styleClass="stacked">
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
			            &nbsp; &nbsp;
			            <c:if test="${empty deviceCollection}">
			                <cti:button id="selectDevicesBtn1" nameKey="selectDevices" />
			            </c:if>
			        </div>
			        <br>
			            
		            <tags:nameValueContainer2 id="formatContainer" tableClass="stacked clear">
		                <tags:nameValue2 nameKey=".formatType">
		                    <select id="formatType">
		                        <c:forEach var="formatType" items="${formatTypes}">
		                            <option value="${formatType}"><cti:msg2 key="${formatType}" /></option>
		                        </c:forEach>
		                    </select>
		                    <cti:button nameKey="create" href="create" styleClass="create"/>
		                </tags:nameValue2>
	                    <c:if test="${not empty allFormats}">
		                    <tags:nameValue2 nameKey=".existingFormat">
		                        <form:select path="formatId" cssClass="fl" cssStyle="margin-right:5px;">
		                            <form:options items="${allFormats}" itemValue="formatId" title="formatName" itemLabel="formatName" />
		                        </form:select>
		                        <cti:button nameKey="edit" href="edit" styleClass="edit" />
		                        <cti:button nameKey="copy" href="copy" styleClass="copy" />
		                  </tags:nameValue2>
	                    </c:if>
		                
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
	            </form:form>
			</tags:boxContainer2>
		</div>
		
		<div class="col2">
	
		<%-- Jobs  --%>
			<tags:pagedBox2 nameKey="jobsBox" searchResult="${filterResult}" baseUrl="view">
				<div class="scrollingContainer_small">
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
									<td>
										${fn:escapeXml(job.name)}
									</td>
									<td>
										${job.cronString}
									</td>
									<td>
										<cti:dataUpdaterValue type="JOB" identifier="${job.id}/NEXT_RUN_DATE"/>
									</td>
									<td>
										<cti:url var="editUrl" value="scheduleReport">
											<cti:param name="jobId" value="${job.id}"/>
										</cti:url>
										<cti:button nameKey="edit" renderMode="image" href="${editUrl}"/>
										
										<cti:url var="historyUrl" value="/support/fileExportHistory/list">
											<cti:param name="initiator" value="Archived Data Export Schedule: ${job.name}"/>
										</cti:url>
										<cti:button nameKey="history" renderMode="image" href="${historyUrl}"/>
										
										<cti:url var="deleteUrl" value="deleteJob">
											<cti:param name="jobId" value="${job.id}"/>
										</cti:url>
										<cti:button nameKey="delete" renderMode="image" href="${deleteUrl}"/>
									</td>
								</tr>
							</c:forEach>
							<c:if test="${fn:length(filterResult.resultList) == 0}">
								<tr>
									<td class="noResults subtle" colspan="3">
										<i:inline key=".noJobs"/>
									</td>
								</tr>
							</c:if>
						</tbody>
					</table>
				</div>
			</tags:pagedBox2>
		</div>
	</div>
	
    <div id="runDialog" >
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
    
    <div id="scheduleDialog" >
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