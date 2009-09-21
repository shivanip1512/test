<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti"%>
<%@ taglib prefix="amr" tagdir="/WEB-INF/tags/amr"%>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags"%>
<%@ taglib prefix="ext" tagdir="/WEB-INF/tags/ext" %>

<cti:msg var="pageTitle" key="yukon.web.modules.amr.tamperFlagEditor.pageTitle" />
<cti:msg var="headerTitle" key="yukon.web.modules.amr.tamperFlagEditor.headerTitle" />
<cti:msg var="setupSectiontext" key="yukon.web.modules.amr.tamperFlagEditor.section.setup" />
<cti:msg var="editSetupSectionText" key="yukon.web.modules.amr.tamperFlagEditor.section.editSetup" />
<cti:msg var="nameText" key="yukon.web.modules.amr.tamperFlagEditor.label.name"/>
<cti:msg var="deviceGroupText" key="yukon.web.modules.amr.tamperFlagEditor.label.deviceGroup"/>
<cti:msg var="tamperFlagGroupText" key="yukon.web.modules.amr.tamperFlagEditor.label.tamperFlagGroup"/>
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

		function tamperFlagMonitorEditor_deleteTamperFlagMonitor(id) {

			var deleteOk = confirm('${deleteConfirmText}');

			if (deleteOk) {
				$('deleteTamperFlagMonitorId').value = id;
				$('monitorDeleteForm').submit();
			}
		}

		function rewriteTamperFlagGroupName(textEl) {
			$('tamperFlagGroupNameDiv').innerHTML = '${tamperFlagGroupBase}' + textEl.value;
		}
		
	</script>

    <h2>${pageTitle}</h2>
    <br>
    
		<c:if test="${not empty editError}">
	    	<div class="errorRed">${editError}</div>
	    </c:if>
    
    	<%-- MISC FORMS --%>
		<form id="monitorDeleteForm" action="/spring/amr/tamperFlagProcessing/delete" method="post">
			<input type="hidden" id="deleteTamperFlagMonitorId" name="deleteTamperFlagMonitorId" value="">
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
			
			<c:set var="setupSectionTitle" value="${setupSectiontext}"/>
			<c:if test="${tamperFlagMonitorId > 0}">
				<c:set var="setupSectionTitle" value="${editSetupSectionText}"/>
			</c:if>
			
			<tags:sectionContainer title="${setupSectionTitle}">
			
			<tags:nameValueContainer style="border-collapse:separate;border-spacing:5px;">
			
				<%-- name --%>
				<tags:nameValue name="${nameText}" nameColumnWidth="250px">
					<input type="text" name="name" size="50" value="${name}" onkeyup="rewriteTamperFlagGroupName(this);" onchange="rewriteTamperFlagGroupName(this);">
				</tags:nameValue>
				
				<%-- device group --%>
				<tags:nameValue name="${deviceGroupText}">
					
					<cti:deviceGroupHierarchyJson predicates="NON_HIDDEN" var="groupDataJson" />
					<tags:deviceGroupNameSelector fieldName="deviceGroupName" 
											  	  fieldValue="${deviceGroupName}" 
											      dataJson="${groupDataJson}"
											      linkGroupName="true"/>
	                                                    
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
			
			</tags:sectionContainer>
				
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
		
</cti:standardPage>
