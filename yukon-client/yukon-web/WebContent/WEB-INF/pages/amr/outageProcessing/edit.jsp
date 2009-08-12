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
<cti:msg var="selectDeviceGroupText" key="yukon.web.modules.amr.outageMonitorConfig.label.selectDeviceGroup"/>
<cti:msg var="selectDeviceGroupChooseText" key="yukon.web.modules.amr.outageMonitorConfig.label.selectDeviceGroupChoose"/>
<cti:msg var="selectDeviceGroupCancelText" key="yukon.web.modules.amr.outageMonitorConfig.label.selectDeviceGroupCancel"/>
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
<cti:msg var="deleteConfirmText" key="yukon.web.modules.amr.outageMonitorConfig.deleteConfirm"/>
<cti:msg var="deviceGroupPopupInfoText" key="yukon.web.modules.amr.outageMonitorConfig.popupInfo.deviceGroup"/>
<cti:msg var="numberOfOutagesPopupInfoText" key="yukon.web.modules.amr.outageMonitorConfig.popupInfo.numberOfOutages"/>
<cti:msg var="timePeriodPopupInfoText" key="yukon.web.modules.amr.outageMonitorConfig.popupInfo.timePeriod"/>
<cti:msg var="scheduleReadPopupInfoText" key="yukon.web.modules.amr.outageMonitorConfig.popupInfo.scheduleRead"/>
<cti:msg var="scheduleReadNoteLabelText" key="yukon.web.modules.amr.outageMonitorConfig.popupInfo.scheduleReadNoteLabelText"/>
<cti:msg var="scheduleReadNoteBodyText" key="yukon.web.modules.amr.outageMonitorConfig.popupInfo.scheduleReadNoteBodyText"/>


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

	    Ext.onReady(function(){
	    	toggleReadFrequencyOptions();
		});
	
		function setSelectedGroupName() {

			if ($('deviceGroupName').value != $('deviceGroup').value) {
				$('deviceGroupName').value = $('deviceGroup').value;
				$('deviceGroupNameSpan').innerHTML = $('deviceGroup').value;
				$('deviceGroupNameForView').value = $('deviceGroup').value;
				$('selectGroupButton').value = '${changeGroupText}';
				$('deviceGroupLinkDiv').show();
			}
		}

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
			}
		}

		function viewDeviceGroup() {
			$('viewDeviceGroupForm').submit();
		}

		function rewriteOutageGroupName(textEl) {
			$('outageGroupNameDiv').innerHTML = '${outageGroupBase}' + textEl.value;
		}
		
	</script>

    <h2>${pageTitle}</h2>
    <br>
    
	<tags:boxContainer title="${headerTitle}" id="configContainer" hideEnabled="false">
	
		<c:if test="${not empty editError}">
	    	<div class="errorRed">${editError}</div>
	    </c:if>
    
    	<%-- MISC FORMS --%>
		<form id="configDeleteForm" action="/spring/amr/outageProcessing/monitorEditor/delete" method="post">
			<input type="hidden" id="deleteOutageMonitorId" name="deleteOutageMonitorId" value="">
		</form>
		
		<form id="viewDeviceGroupForm" action="/spring/group/editor/home" method="get">
			<input type="hidden" id="deviceGroupNameForView" name="groupName" value="${deviceGroupName}">
		</form>
		
		<form id="enableMonitoringForm" action="/spring/amr/outageProcessing/monitorEditor/toggleMonitorEvaluationEnabled" method="post">
			<input type="hidden" name="outageMonitorId" value="${outageMonitorId}">
			<input type="hidden" name="enable" value="true">
		</form>
		
		<form id="disableMonitoringForm" action="/spring/amr/outageProcessing/monitorEditor/toggleMonitorEvaluationEnabled" method="post">
			<input type="hidden" name="outageMonitorId" value="${outageMonitorId}">
			<input type="hidden" name="enable" value="false">
		</form>
		
		<%-- UPDATE FORM --%>
		<form id="updateForm" action="/spring/amr/outageProcessing/monitorEditor/update" method="post">
		
			<input type="hidden" name="outageMonitorId" value="${outageMonitorId}">
			
			<c:set var="setupSectionTitle" value="${setupSectiontext}"/>
			<c:if test="${outageMonitorId > 0}">
				<c:set var="setupSectionTitle" value="${editSetupSectionText}"/>
			</c:if>
			
			<br>
			<tags:sectionContainer title="${setupSectionTitle}">
			
				<tags:nameValueContainer style="border-collapse:separate;border-spacing:5px;">
				
					<%-- name --%>
					<tags:nameValue name="${nameText}" nameColumnWidth="250px">
						<input type="text" name="name" size="50" value="${name}" onkeyup="rewriteOutageGroupName(this);" onchange="rewriteOutageGroupName(this);">
					</tags:nameValue>
					
					<%-- device group --%>
					<tags:nameValue name="${deviceGroupText}">
						
						<span id="deviceGroupLinkDiv" <c:if test="${empty deviceGroupName}">style="display:none;"</c:if>>
							<a href="javascript:void(0);" onclick="viewDeviceGroup();">
								<span id="deviceGroupNameSpan">${deviceGroupName}</span>
							</a>&nbsp;
						</span>
						
						<input type="hidden" id="deviceGroupName" name="deviceGroupName" value="${deviceGroupName}">
						
						<c:choose>
							<c:when test="${not empty deviceGroupName}">
								<input type="button" id="selectGroupButton" value="${changeGroupText}" onclick="">
							</c:when>
							<c:otherwise>
								<input type="button" id="selectGroupButton" value="${chooseGroupText}" onclick="">
							</c:otherwise>
						</c:choose>
						
						<cti:deviceGroupHierarchyJson predicates="NON_HIDDEN" var="groupDataJson" />
			            <ext:nodeValueSelectingPopupTree    fieldId="deviceGroup"
		                                                    fieldName="deviceGroup"
		                                                    nodeValueName="groupName"
		                                                    submitButtonText="${selectDeviceGroupChooseText}"
		                                                    cancelButtonText="${selectDeviceGroupCancelText}"
		                                                    submitCallback="setSelectedGroupName();"
		                                                    
		                                                    id="selectGroupTree"
		                                                    treeAttributes="{}"
		                                                    triggerElement="selectGroupButton"
		                                                    dataJson="${groupDataJson}"
		                                                    title="${selectDeviceGroupText}"
		                                                    width="432"
		                                                    height="600" />
		                                                    
		            	<img onclick="$('deviceGroupInfoPopup').toggle();" src="${help}" onmouseover="javascript:this.src='${helpOver}'" onmouseout="javascript:this.src='${help}'">
					
						<tags:simplePopup id="deviceGroupInfoPopup" title="${deviceGroupText}" onClose="$('deviceGroupInfoPopup').toggle();">
						     ${deviceGroupPopupInfoText}
						</tags:simplePopup>
						
						
					</tags:nameValue>
				
					<%-- outages group --%>
					<tags:nameValue name="${outagesGroupText}">
						<div id="outageGroupNameDiv">${outageGroupBase}${name}</div>			
					</tags:nameValue>
				
					<%-- number of outages --%>
					<tags:nameValue name="${numberOfOutagesText}">
						
						<input type="text" name="numberOfOutages" maxlength="3" size="3" style="text-align:right;" value="${numberOfOutages}"> 
						${numberOfOutagesOutagesText}
						<img onclick="$('numberOfOutagesInfoPopup').toggle();" src="${help}" onmouseover="javascript:this.src='${helpOver}'" onmouseout="javascript:this.src='${help}'">
					
						<tags:simplePopup id="numberOfOutagesInfoPopup" title="${numberOfOutagesText}" onClose="$('numberOfOutagesInfoPopup').toggle();">
						     ${numberOfOutagesPopupInfoText}
						</tags:simplePopup>
						
					</tags:nameValue>
					
					<%-- time period --%>
					<tags:nameValue name="${timePeriodText}">
							
						<input type="text" name="timePeriod" maxlength="3" size="3" style="text-align:right;" value="${timePeriod}">
						${timePeriodDaysText}
						<img onclick="$('timePeriodInfoPopup').toggle();" src="${help}" onmouseover="javascript:this.src='${helpOver}'" onmouseout="javascript:this.src='${help}'">
					
						<tags:simplePopup id="timePeriodInfoPopup" title="${timePeriodText}" onClose="$('timePeriodInfoPopup').toggle();">
						     ${timePeriodPopupInfoText}
						</tags:simplePopup>
						
					</tags:nameValue>
					
					<%-- enable/disable monitoring --%>
					<c:if test="${outageMonitorId > 0}">
						<tags:nameValue name="${outageMonitoringText}">
							<c:choose>
								<c:when test="${outageMonitor.evaluatorStatus eq 'ENABLED'}">
									
									<tags:slowInput myFormId="disableMonitoringForm" labelBusy="${outageMonitoringDisableText}" label="${outageMonitoringDisableText}"/>
									<img onclick="$('disableMonitoringInfoPopup').toggle();" src="${help}" onmouseover="javascript:this.src='${helpOver}'" onmouseout="javascript:this.src='${help}'">
									
									<tags:simplePopup id="disableMonitoringInfoPopup" title="${outageMonitoringDisableText} ${outageMonitoringText}" onClose="$('disableMonitoringInfoPopup').toggle();">
									     ${outageMonitoringDisablePopupInfo}
									</tags:simplePopup>
								
								</c:when>
								<c:when test="${outageMonitor.evaluatorStatus eq 'DISABLED'}">
									
									<tags:slowInput myFormId="enableMonitoringForm" labelBusy="${outageMonitoringEnableText}" label="${outageMonitoringEnableText}"/>
									<img onclick="$('enableMonitoringInfoPopup').toggle();" src="${help}" onmouseover="javascript:this.src='${helpOver}'" onmouseout="javascript:this.src='${help}'">
									
									<tags:simplePopup id="enableMonitoringInfoPopup" title="${outageMonitoringEnableText} ${outageMonitoringText}" onClose="$('enableMonitoringInfoPopup').toggle();">
									     ${outageMonitoringEnablePopupInfo}
									</tags:simplePopup>
									
								</c:when>
								<c:otherwise>
									${outageMonitor.evaluatorStatus.description}
								</c:otherwise>
							</c:choose>
						</tags:nameValue>
					</c:if>
					
				</tags:nameValueContainer>
				
			</tags:sectionContainer>
			
			<%-- SCHEDULE --%>
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
					<img onclick="$('scheduleReadInfoPopup').toggle();" src="${help}" onmouseover="javascript:this.src='${helpOver}'" onmouseout="javascript:this.src='${help}'">
				
					<tags:simplePopup id="scheduleReadInfoPopup" title="${scheduleReadText}" onClose="$('scheduleReadInfoPopup').toggle();">
					     ${scheduleReadPopupInfoText}
					</tags:simplePopup>
						
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
			
			<%-- create / update / delete --%>
			<br>
			<c:choose>
				<c:when test="${outageMonitorId > 0}">
					<tags:slowInput myFormId="updateForm" labelBusy="${updateBusyText}" label="${updateText}"/>
					<input type="button" onclick="outageMonitorEditor_deleteOutageMonitor(${outageMonitorId});" value="${deleteText}"/>
				</c:when>
				<c:otherwise>
					<tags:slowInput myFormId="updateForm" labelBusy="${createBusyText}" label="${createText}"/>
				</c:otherwise>
			</c:choose>
			
		</form>
		
	</tags:boxContainer>
		
	<br><br>
	<cti:msg var="outagesWidgetPopupInfoText" key="yukon.web.modules.amr.outageMonitorsWidget.popupInfo"/>
	<tags:widget bean="outageMonitorsWidget" helpText="${outagesWidgetPopupInfoText}"/>

</cti:standardPage>