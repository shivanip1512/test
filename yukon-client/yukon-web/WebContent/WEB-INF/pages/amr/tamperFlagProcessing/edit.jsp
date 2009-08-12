<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti"%>
<%@ taglib prefix="amr" tagdir="/WEB-INF/tags/amr"%>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags"%>
<%@ taglib prefix="ext" tagdir="/WEB-INF/tags/ext" %>

<cti:msg var="pageTitle" key="yukon.web.modules.amr.tamperFlagEditor.pageTitle" />
<cti:msg var="headerTitle" key="yukon.web.modules.amr.tamperFlagEditor.headerTitle" />
<cti:msg var="nameText" key="yukon.web.modules.amr.tamperFlagEditor.label.name"/>
<cti:msg var="deviceGroupText" key="yukon.web.modules.amr.tamperFlagEditor.label.deviceGroup"/>
<cti:msg var="tamperFlagGroupText" key="yukon.web.modules.amr.tamperFlagEditor.label.tamperFlagGroup"/>
<cti:msg var="selectDeviceGroupText" key="yukon.web.modules.amr.tamperFlagEditor.label.selectDeviceGroup"/>
<cti:msg var="selectDeviceGroupChooseText" key="yukon.web.modules.amr.tamperFlagEditor.label.selectDeviceGroupChoose"/>
<cti:msg var="selectDeviceGroupCancelText" key="yukon.web.modules.amr.tamperFlagEditor.label.selectDeviceGroupCancel"/>
<cti:msg var="chooseGroupText" key="yukon.web.modules.amr.tamperFlagEditor.label.chooseGroup"/>
<cti:msg var="changeGroupText" key="yukon.web.modules.amr.tamperFlagEditor.label.changeGroup"/>
<cti:msg var="tamperFlagMonitoringText" key="yukon.web.modules.amr.tamperFlagEditor.label.tamperFlagMonitoring"/>
<cti:msg var="tamperFlagMonitoringEnableText" key="yukon.web.modules.amr.tamperFlagEditor.label.tamperFlagMonitoringEnable"/>
<cti:msg var="tamperFlagMonitoringDisableText" key="yukon.web.modules.amr.tamperFlagEditor.label.tamperFlagMonitoringDisable"/>
<cti:msg var="tamperFlagMonitoringDisableText" key="yukon.web.modules.amr.tamperFlagEditor.label.tamperFlagMonitoringDisable"/>
<cti:msg var="tamperFlagMonitoringEnablePopupInfo" key="yukon.web.modules.amr.tamperFlagEditor.popupInfo.tamperFlagMonitoringEnable"/>
<cti:msg var="tamperFlagMonitoringDisablePopupInfo" key="yukon.web.modules.amr.tamperFlagEditor.popupInfo.tamperFlagMonitoringDisable"/>
<cti:msg var="createText" key="yukon.web.modules.amr.tamperFlagEditor.label.create"/>
<cti:msg var="createBusyText" key="yukon.web.modules.amr.tamperFlagEditor.label.create.busy"/>
<cti:msg var="updateText" key="yukon.web.modules.amr.tamperFlagEditor.label.update"/>
<cti:msg var="updateBusyText" key="yukon.web.modules.amr.tamperFlagEditor.label.update.busy"/>
<cti:msg var="deleteText" key="yukon.web.modules.amr.tamperFlagEditor.label.delete"/>
<cti:msg var="deleteBusyText" key="yukon.web.modules.amr.tamperFlagEditor.label.delete.busy"/>
<cti:msg var="deleteConfirmText" key="yukon.web.modules.amr.tamperFlagEditor.deleteConfirm"/>
<cti:msg var="deviceGroupPopupInfoText" key="yukon.web.modules.amr.tamperFlagEditor.popupInfo.deviceGroup"/>

<c:url var="help" value="/WebConfig/yukon/Icons/help.gif"/>
<c:url var="helpOver" value="/WebConfig/yukon/Icons/help_over.gif"/>

<cti:standardPage title="${pageTitle}" module="amr">

    <cti:standardMenu menuSelection="" />
    
    <cti:breadCrumbs>
        <cti:crumbLink url="/operator/Operations.jsp" title="Operations Home" />
        <cti:crumbLink url="/spring/meter/start" title="Metering" />
        <cti:crumbLink>${pageTitle}</cti:crumbLink>
    </cti:breadCrumbs>
    
    <script type="text/javascript">

		function setSelectedGroupName() {
			if ($('deviceGroupName').value != $('deviceGroup').value) {
				$('deviceGroupName').value = $('deviceGroup').value;
				$('deviceGroupNameSpan').innerHTML = $('deviceGroup').value;
				$('deviceGroupNameForView').value = $('deviceGroup').value;
				$('selectGroupButton').value = '${changeGroupText}';
				$('deviceGroupLinkDiv').show();
			}
		}

		function tamperFlagMonitorEditor_deleteTamperFlagMonitor(id) {

			var deleteOk = confirm('${deleteConfirmText}');

			if (deleteOk) {
				$('deleteTamperFlagMonitorId').value = id;
				$('monitorDeleteForm').submit();
			}
		}

		function viewDeviceGroup() {
			$('viewDeviceGroupForm').submit();
		}

		function rewriteTamperFlagGroupName(textEl) {
			$('tamperFlagGroupNameDiv').innerHTML = '${tamperFlagGroupBase}' + textEl.value;
		}
		
	</script>

    <h2>${pageTitle}</h2>
    <br>
    
	<tags:boxContainer title="${headerTitle}" id="editorContainer" hideEnabled="false">
	
		<c:if test="${not empty editError}">
	    	<div class="errorRed">${editError}</div>
	    </c:if>
    
    	<%-- MISC FORMS --%>
		<form id="monitorDeleteForm" action="/spring/amr/tamperFlagProcessing/delete" method="post">
			<input type="hidden" id="deleteTamperFlagMonitorId" name="deleteTamperFlagMonitorId" value="">
		</form>
		
		<form id="viewDeviceGroupForm" action="/spring/group/editor/home" method="get">
			<input type="hidden" id="deviceGroupNameForView" name="groupName" value="${deviceGroupName}">
		</form>
		
		<form id="enableMonitoringForm" action="/spring/amr/tamperFlagProcessing/toggleMonitorEvaluationEnabled" method="post">
			<input type="hidden" name="tamperFlagMonitorId" value="${tamperFlagMonitorId}">
			<input type="hidden" name="enable" value="true">
		</form>
		
		<form id="disableMonitoringForm" action="/spring/amr/tamperFlagProcessing/toggleMonitorEvaluationEnabled" method="post">
			<input type="hidden" name="tamperFlagMonitorId" value="${tamperFlagMonitorId}">
			<input type="hidden" name="enable" value="false">
		</form>
		
		<%-- UPDATE FORM --%>
		<form id="updateForm" action="/spring/amr/tamperFlagProcessing/update" method="post">
		
			<input type="hidden" name="tamperFlagMonitorId" value="${tamperFlagMonitorId}">
			
			<tags:nameValueContainer style="border-collapse:separate;border-spacing:5px;">
			
				<%-- name --%>
				<tags:nameValue name="${nameText}" nameColumnWidth="250px">
					<input type="text" name="name" size="50" value="${name}" onkeyup="rewriteTamperFlagGroupName(this);" onchange="rewriteTamperFlagGroupName(this);">
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
			
				<%-- tamper flag group --%>
				<tags:nameValue name="${tamperFlagGroupText}">
					<div id="tamperFlagGroupNameDiv">${tamperFlagGroupBase}${name}</div>			
				</tags:nameValue>
			
				<%-- enable/disable monitoring --%>
				<c:if test="${tamperFlagMonitorId > 0}">
					<tags:nameValue name="${tamperFlagMonitoringText}">
						<c:choose>
							<c:when test="${tamperFlagMonitor.evaluatorStatus eq 'ENABLED'}">
								
								<tags:slowInput myFormId="disableMonitoringForm" labelBusy="${tamperFlagMonitoringDisableText}" label="${tamperFlagMonitoringDisableText}"/>
								<img onclick="$('disableMonitoringInfoPopup').toggle();" src="${help}" onmouseover="javascript:this.src='${helpOver}'" onmouseout="javascript:this.src='${help}'">
								
								<tags:simplePopup id="disableMonitoringInfoPopup" title="${tamperFlagMonitoringDisableText} ${tamperFlagMonitoringText}" onClose="$('disableMonitoringInfoPopup').toggle();">
								     ${tamperFlagMonitoringDisablePopupInfo}
								</tags:simplePopup>
							
							</c:when>
							<c:when test="${tamperFlagMonitor.evaluatorStatus eq 'DISABLED'}">

								<tags:slowInput myFormId="enableMonitoringForm" labelBusy="${tamperFlagMonitoringEnableText}" label="${tamperFlagMonitoringEnableText}"/>
								<img onclick="$('enableMonitoringInfoPopup').toggle();" src="${help}" onmouseover="javascript:this.src='${helpOver}'" onmouseout="javascript:this.src='${help}'">
								
								<tags:simplePopup id="enableMonitoringInfoPopup" title="${tamperFlagMonitoringEnableText} ${tamperFlagMonitoringText}" onClose="$('enableMonitoringInfoPopup').toggle();">
								     ${tamperFlagMonitoringEnablePopupInfo}
								</tags:simplePopup>
								
							</c:when>
							<c:otherwise>
								${tamperFlagMonitor.evaluatorStatus.description}
							</c:otherwise>
						</c:choose>
					</tags:nameValue>
				</c:if>
				
			</tags:nameValueContainer>
				
			<%-- create / update / delete --%>
			<br>
			<c:choose>
				<c:when test="${tamperFlagMonitorId > 0}">
					<tags:slowInput myFormId="updateForm" labelBusy="${updateBusyText}" label="${updateText}"/>
					<input type="button" onclick="tamperFlagMonitorEditor_deleteTamperFlagMonitor(${tamperFlagMonitorId});" value="${deleteText}"/>
				</c:when>
				<c:otherwise>
					<tags:slowInput myFormId="updateForm" labelBusy="${createBusyText}" label="${createText}"/>
				</c:otherwise>
			</c:choose>
			
		</form>
		
	</tags:boxContainer>
		
	<br><br>
	<cti:msg var="tamperFlagMonitorsWidgetPopupInfoText" key="yukon.web.modules.amr.tamperFlagMonitorsWidget.popupInfo"/>
	<tags:widget bean="tamperFlagMonitorsWidget" helpText="${tamperFlagMonitorsWidgetPopupInfoText}"/>

</cti:standardPage>\ No newline at end of file
