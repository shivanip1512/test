<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n" %>

<cti:verifyRolesAndProperties value="CONSUMER_INFO"/>
    
<cti:standardPage module="dr" page="optOutAdmin">
    
    <script type="text/javascript">

    	function toggleProgramNameEnabled(toggleEl, txtEl) {
    		if (toggleEl.checked) {
    			txtEl.enable();
    		} else {
    			txtEl.value = '';
    			txtEl.disable();
    		}
    	}
    </script>

	<table class="widgetColumns">
		<tr>
			<td class="widgetColumnCell" valign="top" style="padding: 9px 0px 0px 0px">    
			    <!-- System Information -->
			    <cti:checkProperty property="ConsumerInfoRole.OPT_OUT_ADMIN_STATUS">
				    <tags:boxContainer2 nameKey="systemInfo" hideEnabled="false">
				        <tags:nameValueContainer2>
				            <tags:nameValue2 nameKey=".totalAccounts">
				                ${totalNumberOfAccounts}
				            </tags:nameValue2>
				            <tags:nameValue2 nameKey=".todaysOptOuts">
				                ${currentOptOuts}
				            </tags:nameValue2>
				            <tags:nameValue2 nameKey=".futureOptOuts">
				                ${scheduledOptOuts}
				            </tags:nameValue2>
				        </tags:nameValueContainer2>
				    </tags:boxContainer2>
		        </cti:checkProperty>
		        <br/>

                <!-- Disable Consumer Opt Out Ability for Today -->
                <cti:checkProperty property="ConsumerInfoRole.OPT_OUT_ADMIN_CHANGE_ENABLE">
                    <tags:boxContainer2 nameKey="disableOptOuts" hideEnabled="false">
                        
                        <div style="font-size:11px;"><b><i:inline key=".disableOptOuts.note.label"/></b> <i:inline key=".disableOptOuts.note.text"/></div>
                        <br>
                        
                        <b><i:inline key=".disableOptOuts.currentDisabledPrograms"/></b>
                        <br><br>
                        <table class="compactResultsTable">
                            <tr>
                                <th><i:inline key=".disableOptOuts.currentDisabledPrograms.header.programName"/></th>
                                <th><i:inline key=".disableOptOuts.currentDisabledPrograms.header.enabled"/></th>
                            </tr>
                            
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
                        
                        </table>
                        <br>
                    
                        <form action="/spring/stars/operator/optOut/admin/setDisabled" method="post">
                            <table style="padding:10px;background-color:#EEE;width:100%;">
                                <tr><td>
                                    <b><i:inline key=".countOptOuts.byProgramName.label"/></b> <i:inline key=".countOptOuts.byProgramName.instruction"/>
                                </td></tr>
                                <tr><td style="padding-top:10px;">
                                    
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
                                        <cti:img nameKey="add"/> <cti:msg key="yukon.web.modules.dr.chooseProgram"/>
                                     </tags:pickerDialog>
                                     
                                     <span id="disabledProgramNameDisplaySpan" style="font-weight:bold;"></span>
                                     
                                </td></tr>
                            </table>
                            <br/>
                            <div class="box fl">
                                <cti:button nameKey="disableOptOuts.currentDisabledPrograms.disableOptOutsButton" type="submit" name="disableOptOuts" styleClass="formSubmit"/>
                                <br>
                                <cti:button nameKey="disableOptOuts.currentDisabledPrograms.disableOptOutsAndCommsButton" type="submit" name="disableOptOutsAndComms" styleClass="formSubmit"/>
                            </div>
                            <div class="box fl">
                                <cti:button nameKey="disableOptOuts.currentDisabledPrograms.enableOptOutsButton" type="submit" name="enableOptOuts" styleClass="formSubmit"/>
                            </div>
                        </form>       
                    </tags:boxContainer2>
                </cti:checkProperty>
                <br/>
                
			    <!-- Cancel Current Opt Outs -->
			    <cti:checkProperty property="ConsumerInfoRole.OPT_OUT_ADMIN_CANCEL_CURRENT">
				    <tags:boxContainer2 nameKey="cancelOptOuts" hideEnabled="false">
				    
				    	<div style="font-size:11px;"><b><i:inline key=".cancelOptOuts.note.label"/></b> <i:inline key=".cancelOptOuts.note.text"/></div>
				        <br>
				        
				        <cti:msg key="yukon.web.modules.dr.optOutAdmin.cancelOptOuts.cancelOptOutsWarning" />
				        <br><br>
				        
				        <form action="/spring/stars/operator/optOut/admin/cancelAllOptOuts" method="post">
				        
				        	<table style="padding:10px;background-color:#EEE;width:100%;">
				        		<tr><td>
				        			<b><i:inline key=".cancelOptOuts.byProgramName.instruction.label"/></b> <i:inline key=".cancelOptOuts.byProgramName.instruction.text"/>
				        		</td></tr>
				        		<tr><td style="padding-top:10px;">
				        		
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
						             	<cti:img nameKey="add"/> <cti:msg key="yukon.web.modules.dr.chooseProgram"/>
		                             </tags:pickerDialog>
		                             
		                             <span id="cancelOptOutsProgramNameDisplaySpan" style="font-weight:bold;"></span>
		                             
				        		</td></tr>
				        	</table>
				        	<br>

                            <cti:button nameKey="cancelOptOuts.cancelAllOptOutsButton" type="submit" styleClass="f_blocker"/>
				        </form>    
				    </tags:boxContainer2>
			    </cti:checkProperty>
			    <br/>

			    <!-- Opt Outs Count/Don't Count -->
			    <cti:checkProperty property="ConsumerInfoRole.OPT_OUT_ADMIN_CHANGE_COUNTS">
				    <tags:boxContainer2 nameKey="countOptOuts" hideEnabled="false">
				        
				        <div style="font-size:11px;"><b><i:inline key=".countOptOuts.note.label"/></b> <i:inline key=".countOptOuts.note.text"/></div>
				        <br>
				        
				        <b><i:inline key=".countOptOuts.currentLimits"/></b>
				        <br><br>
			        	<table class="compactResultsTable">
			        		<tr>
			        			<th><i:inline key=".countOptOuts.currentLimits.header.programName"/></th>
			        			<th><i:inline key=".countOptOuts.currentLimits.header.counts"/></th>
			        		</tr>
			        		
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
                        
			        	</table>
			        	<br>
			        
			        	<form action="/spring/stars/operator/optOut/admin/setCounts" method="post">
				                
		                	<table style="padding:10px;background-color:#EEE;width:100%;">
				        		<tr><td>
				        			<b><i:inline key=".countOptOuts.byProgramName.label"/></b> <i:inline key=".countOptOuts.byProgramName.instruction"/>
				        		</td></tr>
				        		<tr><td style="padding-top:10px;">
				        			
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
						                <cti:img nameKey="add"/> <cti:msg key="yukon.web.modules.dr.chooseProgram"/>
		                             </tags:pickerDialog>
		                             
		                             <span id="disabledCountProgramNameDisplaySpan" style="font-weight:bold;"></span>
		                             
				        		</td></tr>
				        	</table>
				        	<br>
				        	
		                    <input type="submit" name="count" value="<i:inline key=".countOptOuts.countOptOutsButton" />" class="formSubmit">
		                    <input type="submit" name="dontCount" value="<i:inline key=".countOptOuts.dontCountOptOutsButton" />" class="formSubmit">
		                </form>       
			        </tags:boxContainer2>
		        </cti:checkProperty>
			</td>
			
			<td class="widgetColumnCell" valign="top">
				<tags:widget bean="operatorAccountSearchWidget"/>
			</td>
		</tr>
	</table>
            
</cti:standardPage>