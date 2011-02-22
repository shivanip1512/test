<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti"%>
<%@ taglib prefix="amr" tagdir="/WEB-INF/tags/amr"%>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags"%>
<%@ taglib prefix="ext" tagdir="/WEB-INF/tags/ext" %>

<cti:msg var="pageTitle" key="yukon.web.modules.amr.outageMonitorConfig.pageTitle" />
<cti:msg var="headerTitle" key="yukon.web.modules.amr.outageMonitorConfig.headerTitle" />
<cti:msg var="setupSectiontext" key="yukon.web.modules.amr.outageMonitorConfig.section.setup" />
<cti:msg var="editSetupSectionText" key="yukon.web.modules.amr.outageMonitorConfig.section.editSetup" />
<cti:msg var="scheduleSectionText" key="yukon.web.modules.amr.outageMonitorConfig.section.schedule" />
<cti:msg var="nameText" key="yukon.web.modules.amr.outageMonitorConfig.label.name"/>
<cti:msg var="deviceGroupText" key="yukon.web.modules.amr.outageMonitorConfig.label.deviceGroup"/>
<cti:msg var="outagesGroupText" key="yukon.web.modules.amr.outageMonitorConfig.label.outagesGroup"/>
<cti:msg var="chooseGroupText" key="yukon.web.modules.amr.outageMonitorConfig.label.chooseGroup"/>
<cti:msg var="changeGroupText" key="yukon.web.modules.amr.outageMonitorConfig.label.changeGroup"/>
<cti:msg var="numberOfOutagesText" key="yukon.web.modules.amr.outageMonitorConfig.label.numberOfOutages"/>
<cti:msg var="numberOfOutagesOutagesText" key="yukon.web.modules.amr.outageMonitorConfig.label.numberOfOutagesOutages"/>
<cti:msg var="timePeriodText" key="yukon.web.modules.amr.outageMonitorConfig.label.timePeriod"/>
<cti:msg var="timePeriodDaysText" key="yukon.web.modules.amr.outageMonitorConfig.label.timePeriodDays"/>
<cti:msg var="scheduleReadText" key="yukon.web.modules.amr.outageMonitorConfig.label.scheduleRead"/>
<cti:msg var="scheduleReadDescriptionText" key="yukon.web.modules.amr.outageMonitorConfig.label.scheduleReadDescription"/>
<cti:msg var="scheduleNameText" key="yukon.web.modules.amr.outageMonitorConfig.label.scheduleName"/>
<cti:msg var="readFrequencyText" key="yukon.web.modules.amr.outageMonitorConfig.label.readFrequency"/>
<cti:msg var="outageMonitoringText" key="yukon.web.modules.amr.outageMonitorConfig.label.outageMonitoring"/>
<cti:msg var="outageMonitoringEnableText" key="yukon.web.modules.amr.outageMonitorConfig.label.outageMonitoringEnable"/>
<cti:msg var="outageMonitoringDisableText" key="yukon.web.modules.amr.outageMonitorConfig.label.outageMonitoringDisable"/>
<cti:msg var="outageMonitoringDisableText" key="yukon.web.modules.amr.outageMonitorConfig.label.outageMonitoringDisable"/>
<cti:msg var="outageMonitoringEnablePopupInfo" key="yukon.web.modules.amr.outageMonitorConfig.popupInfo.outageMonitoringEnable"/>
<cti:msg var="outageMonitoringDisablePopupInfo" key="yukon.web.modules.amr.outageMonitorConfig.popupInfo.outageMonitoringDisable"/>
<cti:msg var="createText" key="yukon.web.modules.amr.outageMonitorConfig.label.create"/>
<cti:msg var="createBusyText" key="yukon.web.modules.amr.outageMonitorConfig.label.create.busy"/>
<cti:msg var="updateText" key="yukon.web.modules.amr.outageMonitorConfig.label.update"/>
<cti:msg var="updateBusyText" key="yukon.web.modules.amr.outageMonitorConfig.label.update.busy"/>
<cti:msg var="deleteText" key="yukon.web.modules.amr.outageMonitorConfig.label.delete"/>
<cti:msg var="deleteBusyText" key="yukon.web.modules.amr.outageMonitorConfig.label.delete.busy"/>
<cti:msg var="cancelText" key="yukon.web.modules.amr.outageMonitorConfig.label.cancel"/>
<cti:msg var="deleteConfirmText" key="yukon.web.modules.amr.outageMonitorConfig.deleteConfirm"/>
<cti:msg var="deviceGroupPopupInfoText" key="yukon.web.modules.amr.outageMonitorConfig.popupInfo.deviceGroup"/>
<cti:msg var="numberOfOutagesPopupInfoText" key="yukon.web.modules.amr.outageMonitorConfig.popupInfo.numberOfOutages"/>
<cti:msg var="timePeriodPopupInfoText" key="yukon.web.modules.amr.outageMonitorConfig.popupInfo.timePeriod"/>
<cti:msg var="scheduleReadPopupInfoText" key="yukon.web.modules.amr.outageMonitorConfig.popupInfo.scheduleRead"/>
<cti:msg var="scheduleReadNoteLabelText" key="yukon.web.modules.amr.outageMonitorConfig.popupInfo.scheduleReadNoteLabelText"/>
<cti:msg var="scheduleReadNoteBodyText" key="yukon.web.modules.amr.outageMonitorConfig.popupInfo.scheduleReadNoteBodyText"/>
<cti:msg var="saveOkText" key="yukon.web.modules.amr.outageMonitorConfig.saveOk"/>

<c:url var="help" value="/WebConfig/yukon/Icons/help.gif"/>
<c:url var="helpOver" value="/WebConfig/yukon/Icons/help_over.gif"/>

<cti:standardPage title="${pageTitle}" module="amr">

    <cti:standardMenu menuSelection="" />
    
    <cti:breadCrumbs>
    
        <cti:crumbLink url="/operator/Operations.jsp" title="Operations Home" />
        
        <%-- metering home --%>
        <cti:crumbLink url="/spring/meter/start" title="Metering" />
        
        <%-- locate route --%>
        <cti:crumbLink>${pageTitle}</cti:crumbLink>
        
    </cti:breadCrumbs>
    
    <script type="text/javascript">

    Event.observe(window, 'load', function() {
	    	toggleReadFrequencyOptions();
		});
	
		function toggleReadFrequencyOptions() {

			if ($('scheduleGroupCommand').checked) {
				$('scheduleNameTr').show();
				$('readFrequencyTr').show();
			} else {
				$('scheduleNameTr').hide();
				$('readFrequencyTr').hide();
			}
		}

		function outageMonitorEditor_deleteOutageMonitor(id) {

			var deleteOk = confirm('${deleteConfirmText}');

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

    <h2>${pageTitle}</h2>
    <br>
	
		<c:if test="${not empty editError}">
	    	<div class="errorRed">${editError}</div>
	    </c:if>
	    
	    <c:if test="${saveOk}">
	    	<div class="normalBoldLabel">${saveOkText}</div>
	    </c:if>
    
    	<%-- MISC FORMS --%>
		<form id="configDeleteForm" action="/spring/amr/outageProcessing/monitorEditor/delete" method="post">
			<input type="hidden" id="deleteOutageMonitorId" name="deleteOutageMonitorId" value="">
		</form>
		
		<form id="toggleEnabledForm" action="/spring/amr/outageProcessing/monitorEditor/toggleEnabled" method="post">
			<input type="hidden" name="outageMonitorId" value="${outageMonitorId}">
		</form>
		
		<form id="cancelForm" action="/spring/meter/start" method="get">
		</form>
		
		<%-- UPDATE FORM --%>
		<form id="updateForm" action="/spring/amr/outageProcessing/monitorEditor/update" method="post">
		
			<input type="hidden" name="outageMonitorId" value="${outageMonitorId}">
			
			<c:set var="setupSectionTitle" value="${setupSectiontext}"/>
			<c:if test="${outageMonitorId > 0}">
				<c:set var="setupSectionTitle" value="${editSetupSectionText}"/>
			</c:if>
			
			<tags:sectionContainer title="${setupSectionTitle}">
			
				<tags:nameValueContainer style="border-collapse:separate;border-spacing:5px;">
				
					<%-- name --%>
					<tags:nameValue name="${nameText}" nameColumnWidth="250px">
						<input type="text" name="name" size="50" value="${name}" onkeyup="rewriteOutageGroupName(this);" onchange="rewriteOutageGroupName(this);">
					</tags:nameValue>
					
					<%-- device group --%>
					<tags:nameValue name="${deviceGroupText}">
						
						<cti:deviceGroupHierarchyJson predicates="NON_HIDDEN" var="groupDataJson" />
						<tags:deviceGroupNameSelector fieldName="deviceGroupName" 
												  	  fieldValue="${deviceGroupName}" 
												      dataJson="${groupDataJson}"
												      linkGroupName="true"/>
		                                                    
		                <tags:helpInfoPopup title="${deviceGroupText}">
		            		${deviceGroupPopupInfoText}
						</tags:helpInfoPopup>
						
					</tags:nameValue>
				
					<%-- outages group --%>
					<tags:nameValue name="${outagesGroupText}">
						<div id="outageGroupNameDiv">${outageGroupBase}${name}</div>			
					</tags:nameValue>
				
					<%-- number of outages --%>
					<tags:nameValue name="${numberOfOutagesText}">
						
						<input type="text" name="numberOfOutages" maxlength="3" size="3" style="text-align:right;" value="${numberOfOutages}"> 
						${numberOfOutagesOutagesText}
						
						<tags:helpInfoPopup title="${numberOfOutagesText}">
		            		${numberOfOutagesPopupInfoText}
						</tags:helpInfoPopup>
						
					</tags:nameValue>
					
					<%-- time period --%>
					<tags:nameValue name="${timePeriodText}">
							
						<input type="text" name="timePeriod" maxlength="3" size="3" style="text-align:right;" value="${timePeriod}">
						${timePeriodDaysText}
						
						<tags:helpInfoPopup title="${timePeriodText}">
		            		${timePeriodPopupInfoText}
						</tags:helpInfoPopup>
						
					</tags:nameValue>
					
					<%-- enable/disable monitoring --%>
					<c:if test="${outageMonitorId > 0}">
						<tags:nameValue name="${outageMonitoringText}">
							${outageMonitor.evaluatorStatus.description}
						</tags:nameValue>
					</c:if>
					
				</tags:nameValueContainer>
				
			</tags:sectionContainer>
			
			<%-- SCHEDULE --%>
			<cti:checkRolesAndProperties value="MANAGE_SCHEDULES">
			<c:if test="${outageMonitorId == 0}">
			<br>
			<tags:sectionContainer title="${scheduleSectionText}">
			
				<%-- note --%>
				<table cellpadding="2">
		            <tr>
		                <td valign="top" class="smallBoldLabel">
		                	${scheduleReadNoteLabelText}
		                </td>
		                <td style="font-size:11px;">
		                	${scheduleReadNoteBodyText}
		                </td>
		            </tr>
		    	</table>
		    	<br>
	    	
				<tags:nameValueContainer style="border-collapse:separate;border-spacing:5px;">
					
					<%-- schedule read --%>
					<input type="checkbox" id="scheduleGroupCommand" name="scheduleGroupCommand" onclick="toggleReadFrequencyOptions();" <c:if test="${scheduleGroupCommand}">checked</c:if>> 
					${scheduleReadDescriptionText}
					
					<tags:helpInfoPopup title="${scheduleReadText}">
	            		${scheduleReadPopupInfoText}
					</tags:helpInfoPopup>
						
					<%-- schedule name --%>
					<tags:nameValue name="${scheduleNameText}" id="scheduleNameTr" nameColumnWidth="250px">
       		 			<input type="text" name="scheduleName" value="${scheduleName}">
       		 		</tags:nameValue>
       		 		
					<%-- time / frequency --%>
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
					<tags:slowInput myFormId="updateForm" labelBusy="${updateBusyText}" label="${updateText}" disableOtherButtons="true"/>
					<c:choose>
						<c:when test="${outageMonitor.evaluatorStatus eq 'ENABLED'}">
							<tags:slowInput myFormId="toggleEnabledForm" labelBusy="${outageMonitoringDisableText}" label="${outageMonitoringDisableText}" disableOtherButtons="true"/>
						</c:when>
						<c:when test="${outageMonitor.evaluatorStatus eq 'DISABLED'}">
							<tags:slowInput myFormId="toggleEnabledForm" labelBusy="${outageMonitoringEnableText}" label="${outageMonitoringEnableText}" disableOtherButtons="true"/>
						</c:when>
					</c:choose>
					<input type="button" onclick="outageMonitorEditor_deleteOutageMonitor(${outageMonitorId});" value="${deleteText}" class="formSubmit"/>
				</c:when>
				<c:otherwise>
					<tags:slowInput myFormId="updateForm" labelBusy="${createBusyText}" label="${createText}" disableOtherButtons="true"/>
				</c:otherwise>
			</c:choose>
			<tags:slowInput myFormId="cancelForm" label="${cancelText}" disableOtherButtons="true"/>
			
		</form>
		
</cti:standardPage>