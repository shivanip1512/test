<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n" %>

<cti:verifyRolesAndProperties value="CONSUMER_INFO"/>
    
<cti:standardPage module="dr" page="optOutAdmin">
    <cti:checkEnergyCompanyOperator showError="true" >

    <cti:url var="systemOptOutsUrl" value="/stars/operator/optOut/systemOptOuts" />
    
    <script type="text/javascript">

        function setPickerSelectedProgramNamesFunction(spanId) {
            return function(ids) {
                var selectedNames = [];
                for (var index = 0; index < ids.length; index++) {
                    selectedNames.push(ids[index].displayName);
                }
                jQuery('#'+spanId).html(selectedNames.join(","));
                
                systemOptOutInfoRequest();
            }
        }
    
        function systemOptOutInfoRequest() {
            var assignedProgramIds = jQuery('#systemProgramPaoIds').val(),
                url = "/stars/operator/optOut/systemOptOuts",
                params = {'parameters': {'assignedProgramIdsStr': assignedProgramIds}};
            jQuery.ajax({
                dataType: "json",
                url: url,
                data: params
            }).done(function(data, status, xhrobj) {
                var json;
                json = jQuery.parseJSON(xhrobj.getResponseHeader('X-JSON'));
                jQuery('.totalAccounts .value').html(json.totalNumberOfAccounts);
                jQuery('.todaysOptOuts .value').html(json.currentOptOuts);
                jQuery('.futureOptOuts .value').html(json.scheduledOptOuts);
                jQuery('.alternateEnrollments .value').html(json.alternateEnrollments);
            });
        }

/*            This should be uncommented and fixed when we figure out the json to spring controller issue.

            jQuery.ajax({
                url: "${systemOptOutsUrl}",
                data: { assignedProgramIds: assignedProgramIds},
                dataType: 'json'
            }).done(function(data){
                    jQuery('.totalAccounts .value').html(data.totalNumberOfAccounts);
                    jQuery('.todaysOptOuts .value').html(data.scheduledOptOuts);
                    jQuery('.futureOptOuts .value').html(data.currentOptOuts);
                    jQuery('.totalSeasonalOptOuts .value').html(data.totalSeasonalOptOuts);
               });
*/            
  
        function toggleProgramNameEnabled(toggleEl, txtEl) {
            if (toggleEl.checked) {
                txtEl.enable();
            } else {
                txtEl.value = '';
                txtEl.disable();
            }
        }
    </script>
        <c:set var="show">
            <i:inline key=".countOptOuts.byProgramName.instruction.show" />
        </c:set>
        <c:set var="change">
            <i:inline key=".countOptOuts.byProgramName.instruction.change" />
        </c:set>
        <div class="column_12_12">
            <div class="column one">
                <!-- System Information -->
                <cti:checkRolesAndProperties value="OPERATOR_OPT_OUT_ADMIN_STATUS">
                <tags:boxContainer2 nameKey="systemInfo">
                        <tags:nameValueContainer2 tableClass="stacked">
                             <tags:nameValue2 rowClass="totalAccounts" nameKey=".totalAccounts">${totalNumberOfAccounts}</tags:nameValue2>
                             <tags:nameValue2 rowClass="todaysOptOuts" nameKey=".todaysOptOuts">${currentOptOuts}</tags:nameValue2>
                             <tags:nameValue2 rowClass="futureOptOuts" nameKey=".futureOptOuts">${scheduledOptOuts}</tags:nameValue2>
                             <cti:checkEnergyCompanySetting value="ALTERNATE_PROGRAM_ENROLLMENT" energyCompanyId="${energyCompanyId}" >
                                  <tags:nameValue2 rowClass="alternateEnrollments" nameKey=".alternateEnrollments">${alternateEnrollments}</tags:nameValue2>
                             </cti:checkEnergyCompanySetting>
                        </tags:nameValueContainer2>

                        <div style="background-color:#EEE;">
                            <div class="stacked">
                                <span class="fwb"><i:inline key=".countOptOuts.byProgramName.label"/></span> <i:inline key=".countOptOuts.byProgramNames.instruction" arguments="${show}"/>
                            </div>
                            <div class="stacked">

                                  <%-- PROGRAM PICKER --%>
                                  <input type="hidden" id="systemProgramPaoIds">
                                  <input type="hidden" id="systemProgramName" name="programName" value="">
                                
                                  <tags:pickerDialog type="assignedProgramPicker"
                                                           id="systemProgramPicker" 
                                                           destinationFieldId="systemProgramPaoIds"
                                                           styleClass="simpleLink"
                                                           allowEmptySelection="true"
                                                           extraArgs="${energyCompanyId}"
                                                           endAction="setPickerSelectedProgramNamesFunction('systemProgramNameDisplaySpan');"
                                                           multiSelectMode="true">
                                                           
                                    <cti:icon nameKey="add" icon="icon-add"/> <cti:msg2 key="yukon.web.modules.dr.chooseProgram"/>
                                </tags:pickerDialog>
                                 
                                <span id="systemProgramNameDisplaySpan" style="font-weight:bold;"></span>
                                 
                           </div>
                        </div>
                </tags:boxContainer2>
                                     
                <span id="systemProgramNameDisplaySpan" style="font-weight:bold;"></span>

                </cti:checkRolesAndProperties>

                    <!-- Disable Consumer Opt Out Ability for Today -->
                    <cti:checkRolesAndProperties value="OPERATOR_OPT_OUT_ADMIN_CHANGE_ENABLE">
                        <tags:boxContainer2 nameKey="disableOptOuts" hideEnabled="false">
                            
                            <div>
                                <span class="smallBoldLabel"><i:inline key=".disableOptOuts.note.label"/></span>
                                <span class="notes"><i:inline key=".disableOptOuts.note.text"/></span>
                            </div>
                            
                            <b><i:inline key=".disableOptOuts.currentDisabledPrograms"/></b>
                            <table class="compactResultsTable">
                                <thead>
                                <tr>
                                    <th><i:inline key=".disableOptOuts.currentDisabledPrograms.header.programName"/></th>
                                    <th><i:inline key=".disableOptOuts.currentDisabledPrograms.header.enabled"/></th>
                                </tr>
                                </thead>
                                <tfoot></tfoot>
                                <tbody>
                                <c:forEach var="program" items="${programNameEnabledMap}">
                                    <tr>
                                        <td>${program.key}</td>
                                        <td><cti:msg key="${program.value.formatKey}"/></td>
                                    </tr>
                                </c:forEach>
                                
                                <tr>
                                    <c:choose>
                                        <c:when test="${fn:length(programNameEnabledMap) > 0}">
                                            <td><i:inline key=".otherPrograms" /></td>
                                        </c:when>
                                        <c:otherwise>
                                            <td><i:inline key=".allPrograms" /></td>
                                        </c:otherwise>
                                    </c:choose>
                                    <td><cti:msg key="${energyCompanyOptOutEnabledSetting}"/></td>
                                </tr>
                            
                                </tbody>
                            </table>
                            <br>
                        
                            <form action="/stars/operator/optOut/admin/setDisabled" method="post">
                                <div class="stacked" style="background-color:#EEE;">
                                    <div class="stacked">
                                        <b><i:inline key=".countOptOuts.byProgramName.label"/></b> <i:inline key=".countOptOuts.byProgramName.instruction" arguments="${change}"/>
                                    </div>
                                    <div class="stacked">
                                        
                                        <%-- PROGRAM PICKER --%>
                                        <input type="hidden" id="disabledProgramPaoId"> <%-- dummy destination for selected programId, unused. We actually want to submit the program name to do a lookup for webpublishingProgramId --%>
                                        <input type="hidden" id="disabledProgramName" name="programName" value="">
                                        
                                        <tags:pickerDialog  type="lmProgramPicker"
                                                             id="disabledProgramPicker" 
                                                             destinationFieldId="disabledProgramPaoId"
                                                             styleClass="simpleLink"
                                                             immediateSelectMode="true"
                                                             extraDestinationFields="paoName:disabledProgramName;paoName:disabledProgramNameDisplaySpan"
                                                             extraArgs="${energyCompanyId}">
                                            <cti:icon nameKey="add" icon="icon-add"/> <cti:msg key="yukon.web.modules.dr.chooseProgram"/>
                                         </tags:pickerDialog>
                                         
                                         <span id="disabledProgramNameDisplaySpan" style="font-weight:bold;"></span>
                                         
                                    </div>
                                </div>
                                <div class="box fl">
                                        <cti:button nameKey="disableOptOuts.currentDisabledPrograms.disableOptOutsButton" type="submit" name="disableOptOuts" classes="formSubmit"/>
                                        <cti:button nameKey="disableOptOuts.currentDisabledPrograms.disableOptOutsAndCommsButton" type="submit" name="disableOptOutsAndComms" classes="formSubmit"/>
                                        <cti:button nameKey="disableOptOuts.currentDisabledPrograms.enableOptOutsButton" type="submit" name="enableOptOuts" classes="formSubmit"/>
                                </div>
                            </form>       
                        </tags:boxContainer2>
                    </cti:checkRolesAndProperties>
                    
                    <!-- Cancel Current Opt Outs -->
                    <cti:checkRolesAndProperties value="OPERATOR_OPT_OUT_ADMIN_CANCEL_CURRENT">
                        <tags:boxContainer2 nameKey="cancelOptOuts" hideEnabled="false">
                        
                            <div class="stacked">
                                <span class="smallBoldLabel"><i:inline key=".cancelOptOuts.note.label"/></span>
                                <span class="notes"><i:inline key=".cancelOptOuts.note.text"/></span>
                            </div>
                            <div class="stacked">
                                <i:inline key=".cancelOptOuts.cancelOptOutsWarning" />
                            </div>
                            
                            <form action="/stars/operator/optOut/admin/cancelAllOptOuts" method="post">
                            
                                <div style="background-color:#EEE;">
                                    <div>
                                        <b><i:inline key=".cancelOptOuts.byProgramName.instruction.label"/></b> <i:inline key=".cancelOptOuts.byProgramName.instruction.text"/>
                                    </div>
                                    <div>
                                    
                                        <%-- PROGRAM PICKER --%>
                                        <input type="hidden" id="cancelOptOutsProgramPaoId"> <%-- dummy destination for selected programId, unused. We actually want to submit the program name to do a lookup for webpublishingProgramId --%>
                                        <input type="hidden" id="cancelOptOutsProgramName" name="programName" value="">
                                        
                                        <tags:pickerDialog  type="lmProgramPicker"
                                                             id="cancelOptOutsProgramPicker" 
                                                             destinationFieldId="cancelOptOutsProgramPaoId"
                                                             styleClass="simpleLink"
                                                             immediateSelectMode="true"
                                                             extraDestinationFields="paoName:cancelOptOutsProgramName;paoName:cancelOptOutsProgramNameDisplaySpan"
                                                             extraArgs="${energyCompanyId}">
                                             <cti:icon nameKey="add" icon="icon-add"/> <cti:msg key="yukon.web.modules.dr.chooseProgram"/>
                                         </tags:pickerDialog>
                                         
                                         <span id="cancelOptOutsProgramNameDisplaySpan" style="font-weight:bold;"></span>
                                         
                                    </div>
                                </div>
                                <br>
    
                                <cti:button nameKey="cancelOptOuts.cancelAllOptOutsButton" type="submit" classes="f_blocker"/>
                            </form>    
                        </tags:boxContainer2>
                    </cti:checkRolesAndProperties>  	
                    <!-- Opt Outs Count/Don't Count -->
                    <cti:checkRolesAndProperties value="OPERATOR_OPT_OUT_ADMIN_CHANGE_COUNTS">
                        <tags:boxContainer2 nameKey="countOptOuts" hideEnabled="false">
                            
                            <div class="stacked">
                                <span class="smallBoldLabel"><i:inline key=".countOptOuts.note.label"/></span>
                                <span class="notes"><i:inline key=".countOptOuts.note.text"/></span>
                            </div>
                            <div class="stacked">
                                <b><i:inline key=".countOptOuts.currentLimits"/></b>
                            </div>
                            <table class="compactResultsTable stacked">
                                <thead>
                                <tr>
                                    <th><i:inline key=".countOptOuts.currentLimits.header.programName"/></th>
                                    <th><i:inline key=".countOptOuts.currentLimits.header.counts"/></th>
                                </tr>
                                </thead>
                                <tfoot></tfoot>
                                <tbody>
                                
                                <c:forEach var="program" items="${programNameCountsMap}">
                                    <tr>
                                        <td>${program.key}</td>
                                        <td><cti:msg key="${program.value.formatKey}"/></td>
                                    </tr>
                                </c:forEach>
                            
                                <tr>
                                    <c:choose>
                                        <c:when test="${fn:length(programNameCountsMap) > 0}">
                                            <td><i:inline key=".otherPrograms" /></td>
                                        </c:when>
                                        <c:otherwise>
                                            <td><i:inline key=".allPrograms" /></td>
                                        </c:otherwise>
                                    </c:choose>
                                    <td><cti:msg key="${energyCompanyOptOutCountsSetting}"/></td>
                                </tr>
                            
                                </tbody>
                            </table>
                        
                            <form action="/stars/operator/optOut/admin/setCounts" method="post">
                                    
                                <div style="background-color:#EEE">
                                    <div class="stacked">
                                        <b><i:inline key=".countOptOuts.byProgramName.label"/></b> <i:inline key=".countOptOuts.byProgramName.instruction" arguments="${change}"/>
                                    </div>
                                    <div class="stacked">
                                        
                                        <%-- PROGRAM PICKER --%>
                                        <input type="hidden" id="disabledCountProgramPaoId"> <%-- dummy destination for selected programId, unused. We actually want to submit the program name to do a lookup for webpublishingProgramId --%>
                                        <input type="hidden" id="disabledCountProgramName" name="programName" value="">
                                        
                                        <tags:pickerDialog  type="lmProgramPicker"
                                                             id="disabledCountProgramPicker" 
                                                             destinationFieldId="disabledCountProgramPaoId"
                                                             styleClass="simpleLink"
                                                             immediateSelectMode="true"
                                                             extraDestinationFields="paoName:disabledCountProgramName;paoName:disabledCountProgramNameDisplaySpan"
                                                             extraArgs="${energyCompanyId}">
                                            <cti:icon nameKey="add" icon="icon-add"/> <cti:msg key="yukon.web.modules.dr.chooseProgram"/>
                                         </tags:pickerDialog>
                                         
                                         <span id="disabledCountProgramNameDisplaySpan" style="font-weight:bold;"></span>
                                         
                                    </div>
                                </div>
                                
                                <input type="submit" name="count" value="<i:inline key=".countOptOuts.countOptOutsButton" />" class="formSubmit">
                                <input type="submit" name="dontCount" value="<i:inline key=".countOptOuts.dontCountOptOutsButton" />" class="formSubmit">
                            </form>       
                        </tags:boxContainer2>
                    </cti:checkRolesAndProperties>
                </div>
                <div class="column two nogutter">
                    <tags:widget bean="operatorAccountSearchWidget"/>

                    <tags:boxContainer2 nameKey="scheduledEvents" hideEnabled="false">
                        <c:choose>
                            <c:when test="${fn:length(scheduledEvents) > 0}">
                                <table class="compactResultsTable">
                                    <thead>
                                    <tr>
                                        <th><cti:msg key="yukon.web.modules.dr.scheduledEvents.startDateTime" /></th>
                                        <th><cti:msg key="yukon.web.modules.dr.scheduledEvents.duration" /></th>
                                        <th><cti:msg key="yukon.web.modules.dr.scheduledEvents.accountNumber" /></th>
                                        <th><cti:msg key="yukon.web.modules.dr.scheduledEvents.serialNumber" /></th>
                                    </tr>
                                    </thead>
                                    <tfoot></tfoot>
                                    <tbody>
                                    <c:forEach var="event" items="${scheduledEvents}">
                                        <tr>
                                            <td><cti:formatDate value="${event.startDate}" type="DATEHM"/></td>
                                            <td><cti:formatDuration startDate="${event.startDate}" endDate="${event.stopDate}" type="DH"/></td>
                                            <td>${event.accountNumber}</td>
                                            <td>${event.serialNumber}</td>
                                        </tr>
                                    </c:forEach>
                                    </tbody>
                                </table>
                            </c:when>
                            <c:otherwise>
                               <span class="empty-list"><i:inline key=".scheduledEvents.noScheduledEvents" /></span>
                            </c:otherwise>
                        </c:choose>
                    </tags:boxContainer2>
                </div>
            </div
    </cti:checkEnergyCompanyOperator>
</cti:standardPage>