<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n" %>

<cti:standardPage module="operator" page="optOutAdmin">

    <cti:checkAccountEnergyCompanyOperator showError="true" >

    <cti:url var="systemOptOutsUrl" value="/stars/operator/optOut/systemOptOuts" />
    
    <script type="text/javascript">

        function setPickerSelectedProgramNamesFunction(spanId) {
            return function(ids) {
                var selectedNames = [];
                for (var index = 0; index < ids.length; index++) {
                    selectedNames.push(ids[index].displayName);
                }
                $('#'+spanId).html(selectedNames.join(','));
                
                systemOptOutInfoRequest();
            }
        }

        function systemOptOutInfoRequest() {
            var assignedProgramIds = $('#systemProgramPaoIds').val();
            
            var programIdArrayJson;
            if (assignedProgramIds.length == 0) {
                // Otherwise str.split(',') returns [""], we really want an empty array
                programIdArrayJson = '[]';
            } else {
                programIdArrayJson = assignedProgramIds.split(',');
            }
            var data = {
            		assignedProgramIds : programIdArrayJson	
            };
            $.ajax({
                contentType: 'application/json',
                dataType: 'json',
                url: "${systemOptOutsUrl}",
                data: JSON.stringify(data),
                type: 'post'
            }).done(function(json, status, xhrobj) {
                $('.totalAccounts .value').html(json.totalNumberOfAccounts);
                $('.todaysOptOuts .value').html(json.currentOptOuts);
                $('.futureOptOuts .value').html(json.scheduledOptOuts);
                $('.alternateEnrollments .value').html(json.alternateEnrollments);
            });
        }

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

        <div class="column-12-12">
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
                                                           allowEmptySelection="true"
                                                           extraArgs="${energyCompanyId}"
                                                           endAction="setPickerSelectedProgramNamesFunction('systemProgramNameDisplaySpan');"
                                                           multiSelectMode="true">
                                                           
                                    <cti:icon nameKey="add" icon="icon-add"/> <cti:msg2 key=".chooseProgram"/>
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
                                <span class="strong-label-small"><i:inline key=".disableOptOuts.note.label"/></span>
                                <span class="notes"><i:inline key=".disableOptOuts.note.text"/></span>
                            </div>
                            
                            <b><i:inline key=".disableOptOuts.currentDisabledPrograms"/></b>
                            <table class="compact-results-table">
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
                        
                            <cti:url var="setDisabledUrl" value="/stars/operator/optOut/admin/setDisabled"/>
                            <form action="${setDisabledUrl}" method="post">
                                <cti:csrfToken/>
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
                                                             immediateSelectMode="true"
                                                             extraDestinationFields="paoName:disabledProgramName;paoName:disabledProgramNameDisplaySpan"
                                                             extraArgs="${energyCompanyId}">
                                            <cti:icon nameKey="add" icon="icon-add"/> <cti:msg2 key=".chooseProgram"/>
                                         </tags:pickerDialog>
                                         
                                         <span id="disabledProgramNameDisplaySpan" style="font-weight:bold;"></span>
                                         
                                    </div>
                                </div>
                                <div class="box fl">
                                    <ul class="button-stack">
                                        <li><cti:button nameKey="disableOptOuts.currentDisabledPrograms.disableOptOutsButton" type="submit" name="disableOptOuts" classes="button"/></li>
                                        <li><cti:button nameKey="disableOptOuts.currentDisabledPrograms.disableOptOutsAndCommsButton" type="submit" name="disableOptOutsAndComms" classes="button"/></li>
                                        <li><cti:button nameKey="disableOptOuts.currentDisabledPrograms.enableOptOutsButton" type="submit" name="enableOptOuts" classes="button"/></li>
                                    </ul>
                                </div>
                            </form>       
                        </tags:boxContainer2>
                    </cti:checkRolesAndProperties>
                    
                    <!-- Cancel Current Opt Outs -->
                    <cti:checkRolesAndProperties value="OPERATOR_OPT_OUT_ADMIN_CANCEL_CURRENT">
                        <tags:boxContainer2 nameKey="cancelOptOuts" hideEnabled="false">
                        
                            <div class="stacked">
                                <span class="strong-label-small"><i:inline key=".cancelOptOuts.note.label"/></span>
                                <span class="notes"><i:inline key=".cancelOptOuts.note.text"/></span>
                            </div>
                            <div class="stacked">
                                <i:inline key=".cancelOptOuts.cancelOptOutsWarning" />
                            </div>
                            
                            <cti:url var="cancelAllOptOutsUrl" value="/stars/operator/optOut/admin/cancelAllOptOuts"/>
                            <form action="${cancelAllOptOutsUrl}" method="post">
                                <cti:csrfToken/>
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
                                                             immediateSelectMode="true"
                                                             extraDestinationFields="paoName:cancelOptOutsProgramName;paoName:cancelOptOutsProgramNameDisplaySpan"
                                                             extraArgs="${energyCompanyId}">
                                             <cti:icon nameKey="add" icon="icon-add"/> <cti:msg2 key=".chooseProgram"/>
                                         </tags:pickerDialog>
                                         
                                         <span id="cancelOptOutsProgramNameDisplaySpan" style="font-weight:bold;"></span>
                                         
                                    </div>
                                </div>
                                <br>
    
                                <cti:button nameKey="cancelOptOuts.cancelAllOptOutsButton" type="submit" classes="js-blocker"/>
                            </form>    
                        </tags:boxContainer2>
                    </cti:checkRolesAndProperties>      
                    <!-- Opt Outs Count/Don't Count -->
                    <cti:checkRolesAndProperties value="OPERATOR_OPT_OUT_ADMIN_CHANGE_COUNTS">
                        <tags:boxContainer2 nameKey="countOptOuts" hideEnabled="false">
                            
                            <div class="stacked">
                                <span class="strong-label-small"><i:inline key=".countOptOuts.note.label"/></span>
                                <span class="notes"><i:inline key=".countOptOuts.note.text"/></span>
                            </div>
                            <div class="stacked">
                                <b><i:inline key=".countOptOuts.currentLimits"/></b>
                            </div>
                            <table class="compact-results-table stacked">
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
                        
                            <cti:url var="setCountsUrl" value="/stars/operator/optOut/admin/setCounts"/>
                            <form action="${setCountsUrl}" method="post">
                                <cti:csrfToken/>    
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
                                                             immediateSelectMode="true"
                                                             extraDestinationFields="paoName:disabledCountProgramName;paoName:disabledCountProgramNameDisplaySpan"
                                                             extraArgs="${energyCompanyId}">
                                            <cti:icon nameKey="add" icon="icon-add"/> <cti:msg2 key=".chooseProgram"/>
                                         </tags:pickerDialog>
                                         
                                         <span id="disabledCountProgramNameDisplaySpan" style="font-weight:bold;"></span>
                                         
                                    </div>
                                </div>
                                <cti:msg2 var="countOptout" key=".countOptOuts.countOptOutsButton"/>
                                <cti:msg2 var="dontCountOptout" key=".countOptOuts.dontCountOptOutsButton"/>
                                <cti:button type="submit" name="count" value="${countOptout}" label="${countOptout}"/>
                                <cti:button type="submit" name="dontCount" value="${dontCountOptout}" label="${dontCountOptout}"/>
                            </form>
                        </tags:boxContainer2>
                    </cti:checkRolesAndProperties>
                </div>
                <div class="column two nogutter">
                    <tags:widget bean="operatorAccountSearchWidget"/>

                    <cti:checkRolesAndProperties value="OPERATOR_OPT_OUT_SURVEY_EDIT">
                        <tags:boxContainer2 nameKey="optOutSurveys">
                            <div class="column-12-12 stacked">
                                <div class="column one">
                                    <div>
                                        <span class="name">
                                            <i:inline key=".totalSurveys"/>
                                        </span>
                                        ${totalSurveys}
                                    </div>
                                    <div>
                                        <span class="name">
                                            <i:inline key=".activeSurveys"/>
                                        </span>
                                        ${activeSurveys}
                                    </div>
                                </div>
                                <div>
                                    <div>
                                        <span class="name">
                                            <i:inline key=".lastWeek"/>
                                        </span>
                                        ${resultsInLastWeek}
                                    </div>
                                    <div>
                                        <span class="name">
                                            <i:inline key=".last30Days"/>
                                        </span>
                                        ${resultsInLast30Days}
                                    </div>
                                </div>
                            </div>
                            <a href="<cti:url value="/stars/optOutSurvey/list"/>">
                                <i:inline key=".surveysLink"/>
                            </a>
                        </tags:boxContainer2>
                    </cti:checkRolesAndProperties>
                    
                    <cti:checkRolesAndProperties value="ADMIN_VIEW_OPT_OUT_EVENTS">
                    <tags:boxContainer2 nameKey="scheduledEvents" hideEnabled="false">
                        <c:choose>
                            <c:when test="${fn:length(scheduledEvents) > 0}">
                                <table class="compact-results-table">
                                    <thead>
                                    <tr>
                                        <th><i:inline key=".scheduledEvents.startDateTime" /></th>
                                        <th><i:inline key=".scheduledEvents.duration" /></th>
                                        <th><i:inline key=".scheduledEvents.accountNumber" /></th>
                                        <th><i:inline key=".scheduledEvents.serialNumber" /></th>
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
                    </cti:checkRolesAndProperties>
                </div>
            </div>
    </cti:checkAccountEnergyCompanyOperator>
</cti:standardPage>