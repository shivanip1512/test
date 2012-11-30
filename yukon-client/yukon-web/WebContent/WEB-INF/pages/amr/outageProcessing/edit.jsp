<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti"%>
<%@ taglib prefix="amr" tagdir="/WEB-INF/tags/amr"%>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags"%>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n"%>

<c:url var="help" value="/WebConfig/yukon/Icons/help.gif"/>
<c:url var="helpOver" value="/WebConfig/yukon/Icons/help_over.gif"/>

<cti:standardPage module="amr" page="outageMonitorConfig.${mode}">
    
    <script type="text/javascript">
    var deleteConfirmText = '<cti:msg2 key=".deleteConfirm" javaScriptEscape="true"/>';
    
        jQuery(function() {
	    	toggleReadFrequencyOptions();
		});
	
		function toggleReadFrequencyOptions() {
		    if (${outageMonitorId} == 0) {
    			if ($('scheduleGroupCommand').checked == true) {
    				$('scheduleNameTr').show();
    				$('readFrequencyTr').show();
    			} else {
    				$('scheduleNameTr').hide();
    				$('readFrequencyTr').hide();
    			}
		    }
		}

		function outageMonitorEditor_deleteOutageMonitor(id) {

			var deleteOk = confirm(deleteConfirmText);

			if (deleteOk) {
				$('deleteOutageMonitorId').value = id;
				$('configDeleteForm').submit();

				$$('input[type=button]').each(function(el) {
					el.disable();
				});
			}
		}

		function rewriteOutageGroupName(textEl) {
			$('outageGroupNameDiv').innerHTML = '${outageGroupBase}' + textEl.value;
		}
		
	</script>
	
		<c:if test="${not empty editError}">
	    	<div class="errorMessage">${editError}</div>
	    </c:if>
	    
	    <c:if test="${saveOk}">
	    	<div class="fwb"><i:inline key=".saveOk"/></div>
	    </c:if>
    
    	<%-- MISC FORMS --%>
		<form id="configDeleteForm" action="/amr/outageProcessing/monitorEditor/delete" method="post">
			<input type="hidden" id="deleteOutageMonitorId" name="deleteOutageMonitorId" value="">
		</form>
		
		<form id="toggleEnabledForm" action="/amr/outageProcessing/monitorEditor/toggleEnabled" method="post">
			<input type="hidden" name="outageMonitorId" value="${outageMonitorId}">
		</form>
		
		<form id="cancelForm" action="/meter/start" method="get">
		</form>
		
		<%-- UPDATE FORM --%>
		<form id="updateForm" action="/amr/outageProcessing/monitorEditor/update" method="post">
		
			<input type="hidden" name="outageMonitorId" value="${outageMonitorId}">
			
			<cti:msg2 var="setupSectiontext" key=".section.setup" />
            <cti:msg2 var="editSetupSectionText" key=".section.editSetup" />
            <c:set var="setupSectionTitle" value="${setupSectiontext}"/>
			<c:if test="${outageMonitorId > 0}">
				<c:set var="setupSectionTitle" value="${editSetupSectionText}"/>
			</c:if>
			
			<tags:sectionContainer title="${setupSectionTitle}">
			
				<tags:nameValueContainer2 style="border-collapse:separate;border-spacing:5px;">
				
					<%-- name --%>
					<tags:nameValue2 nameKey=".label.name">
						<input type="text" name="name" size="50" value="${name}" onkeyup="rewriteOutageGroupName(this);" onchange="rewriteOutageGroupName(this);">
					</tags:nameValue2>
					
					<%-- device group --%>
					<tags:nameValue2 nameKey=".label.deviceGroup">
						
						<cti:deviceGroupHierarchyJson predicates="NON_HIDDEN" var="groupDataJson" />
						<tags:deviceGroupNameSelector fieldName="deviceGroupName" 
												  	  fieldValue="${deviceGroupName}" 
												      dataJson="${groupDataJson}"
												      linkGroupName="true"/>
		                                                    
		                <tags:helpInfoPopup title="${deviceGroupText}">
		            		<cti:msg2 key=".popupInfo.deviceGroup"/>
						</tags:helpInfoPopup>
						
					</tags:nameValue2>
				
					<%-- outages group --%>
					<tags:nameValue2 nameKey=".label.outagesGroup">
						<div id="outageGroupNameDiv">${outageGroupBase}${name}</div>			
					</tags:nameValue2>
				
					<%-- number of outages --%>
					<tags:nameValue2 nameKey=".label.numberOfOutages">
						
						<input type="text" name="numberOfOutages" maxlength="3" size="3" style="text-align:right;" value="${numberOfOutages}"> 
						<i:inline key=".label.numberOfOutagesOutages"/>
						
						<tags:helpInfoPopup title="${numberOfOutagesText}">
		            		<cti:msg2 key=".popupInfo.numberOfOutages"/>
						</tags:helpInfoPopup>
						
					</tags:nameValue2>
					
					<%-- time period --%>
                    <tags:nameValue2 nameKey=".label.timePeriod">
							
						<input type="text" name="timePeriod" maxlength="3" size="3" style="text-align:right;" value="${timePeriod}">
                        <i:inline key=".label.timePeriodDays"/>
						
						<tags:helpInfoPopup title="${timePeriodText}">
		            		<cti:msg2 key=".popupInfo.timePeriod"/>
						</tags:helpInfoPopup>
						
					</tags:nameValue2>
					
					<%-- enable/disable monitoring --%>
					<c:if test="${outageMonitorId > 0}">
                        <tags:nameValue2 nameKey=".label.outageMonitoring">
							${outageMonitor.evaluatorStatus.description}
						</tags:nameValue2>
					</c:if>
					
				</tags:nameValueContainer2>
				
			</tags:sectionContainer>
			
			<%-- SCHEDULE --%>
			<cti:checkRolesAndProperties value="MANAGE_SCHEDULES">
			<c:if test="${outageMonitorId == 0}">
			<br>
			<cti:msg2 var="scheduleSectionText" key=".section.schedule" />
            <tags:sectionContainer title="${scheduleSectionText}">
			
				<%-- note --%>
				<table cellpadding="2">
		            <tr>
		                <td valign="top" class="smallBoldLabel">
		                	<i:inline key=".popupInfo.scheduleReadNoteLabelText"/>
		                </td>
		                <td style="font-size:11px;">
                            <i:inline key=".popupInfo.scheduleReadNoteBodyText"/>
		                </td>
		            </tr>
		    	</table>
		    	<br>
	    	
				<tags:nameValueContainer style="border-collapse:separate;border-spacing:5px;">
					
					<%-- schedule read --%>
                    
					<input type="checkbox" id="scheduleGroupCommand" name="scheduleGroupCommand" onclick="toggleReadFrequencyOptions();" <c:if test="${scheduleGroupCommand}">checked</c:if>> 
					<i:inline key=".label.scheduleReadDescription"/>
					
					<cti:msg2 var="scheduleReadText" key=".label.scheduleRead"/>
                    <tags:helpInfoPopup title="${scheduleReadText}">
	            		<cti:msg2 key=".popupInfo.scheduleRead"/>
					</tags:helpInfoPopup>
						
					<%-- schedule name --%>
					<cti:msg2 var="scheduleNameText" key=".label.scheduleName"/>
                    <tags:nameValue name="${scheduleNameText}" id="scheduleNameTr" nameColumnWidth="250px">
       		 			<input type="text" name="scheduleName" value="${scheduleName}">
       		 		</tags:nameValue>
       		 		
					<%-- time / frequency --%>
					<cti:msg2 var="readFrequencyText" key=".label.readFrequency"/>
                    <tags:nameValue name="${readFrequencyText}" id="readFrequencyTr">
						<tags:cronExpressionData id="${cronExpressionTagId}" state="${cronExpressionTagState}"/>
					</tags:nameValue>
					
				</tags:nameValueContainer>
				
			</tags:sectionContainer>
			</c:if>
			</cti:checkRolesAndProperties>
			
			<%-- create / update / delete --%>
			<br>
			<c:choose>
				<c:when test="${outageMonitorId > 0}">
                    <cti:msg2 var="updateText" key=".label.update"/>
					<cti:msg2 var="updateBusyText" key=".label.update.busy"/>
                    <tags:slowInput myFormId="updateForm" labelBusy="${updateBusyText}" label="${updateText}" disableOtherButtons="true"/>
					<c:choose>
						<c:when test="${outageMonitor.evaluatorStatus eq 'ENABLED'}">
							<cti:msg2 var="outageMonitoringDisableText" key=".label.outageMonitoringDisable"/>
                            <tags:slowInput myFormId="toggleEnabledForm" labelBusy="${outageMonitoringDisableText}" label="${outageMonitoringDisableText}" disableOtherButtons="true"/>
						</c:when>
						<c:when test="${outageMonitor.evaluatorStatus eq 'DISABLED'}">
							<cti:msg2 var="outageMonitoringEnableText" key=".label.outageMonitoringEnable"/>
                            <tags:slowInput myFormId="toggleEnabledForm" labelBusy="${outageMonitoringEnableText}" label="${outageMonitoringEnableText}" disableOtherButtons="true"/>
						</c:when>
					</c:choose>
					<cti:msg2 var="deleteText" key=".label.delete"/>
                    <input type="button" onclick="outageMonitorEditor_deleteOutageMonitor(${outageMonitorId});" value="${deleteText}" class="formSubmit"/>
				</c:when>
				<c:otherwise>
					<cti:msg2 var="createText" key=".label.create"/>
                    <cti:msg2 var="createBusyText" key=".label.create.busy"/>
                    <tags:slowInput myFormId="updateForm" labelBusy="${createBusyText}" label="${createText}" disableOtherButtons="true"/>
				</c:otherwise>
			</c:choose>
			<cti:msg2 var="cancelText" key=".label.cancel"/>
            <tags:slowInput myFormId="cancelForm" label="${cancelText}" disableOtherButtons="true"/>
			
		</form>
		
</cti:standardPage>