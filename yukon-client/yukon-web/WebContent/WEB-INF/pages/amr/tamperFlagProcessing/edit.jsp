<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti"%>
<%@ taglib prefix="amr" tagdir="/WEB-INF/tags/amr"%>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags"%>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n"%>

<cti:standardPage module="amr" page="tamperFlagEditor.${mode}">

    <script type="text/javascript">
    var deleteConfirmMsg = '<cti:msg2 key="yukon.web.modules.amr.tamperFlagEditor.deleteConfirm" javaScriptEscape="true"/>';

		function tamperFlagMonitorEditor_deleteTamperFlagMonitor(id) {

			var deleteOk = confirm(deleteConfirmMsg);

			if (deleteOk) {
				$('deleteTamperFlagMonitorId').value = id;
				$('monitorDeleteForm').submit();

				$$('input[type=button]').each(function(el) {
					el.disable();
				});
			}
		}

		function rewriteTamperFlagGroupName(textEl) {
			$('tamperFlagGroupNameDiv').innerHTML = '${tamperFlagGroupBase}' + textEl.value;
		}
		
	</script>

		<c:if test="${not empty editError}">
	    	<div class="errorRed">${editError}</div>
	    </c:if>
    
    	<%-- MISC FORMS --%>
		<form id="monitorDeleteForm" action="/spring/amr/tamperFlagProcessing/delete" method="post">
			<input type="hidden" id="deleteTamperFlagMonitorId" name="deleteTamperFlagMonitorId" value="">
		</form>
		
		<form id="toggleEnabledForm" action="/spring/amr/tamperFlagProcessing/toggleEnabled" method="post">
			<input type="hidden" name="tamperFlagMonitorId" value="${tamperFlagMonitorId}">
		</form>
		
		<form id="cancelForm" action="/spring/meter/start" method="get">
		</form>
		
		<%-- UPDATE FORM --%>
		<form id="updateForm" action="/spring/amr/tamperFlagProcessing/update" method="post">
		
			<input type="hidden" name="tamperFlagMonitorId" value="${tamperFlagMonitorId}">
			
            <cti:msg2 var="setupSectionText" key=".section.setup" />
            <cti:msg2 var="editSetupSectionText" key=".section.editSetup" />  
			<c:set var="setupSectionTitle" value="${setupSectionText}"/>
			<c:if test="${tamperFlagMonitorId > 0}">
				<c:set var="setupSectionTitle" value="${editSetupSectionText}"/>
			</c:if>
			
			<tags:sectionContainer title="${setupSectionTitle}">
			
			<tags:nameValueContainer2 style="border-collapse:separate;border-spacing:5px;">
			
				<%-- name --%>
				<tags:nameValue2 nameKey=".label.name">
					<input type="text" name="name" size="50" value="${name}" onkeyup="rewriteTamperFlagGroupName(this);" onchange="rewriteTamperFlagGroupName(this);">
				</tags:nameValue2>
				
				<%-- device group --%>
				<tags:nameValue2 nameKey=".label.deviceGroup">
					
					<cti:deviceGroupHierarchyJson predicates="NON_HIDDEN" var="groupDataJson" />
					<tags:deviceGroupNameSelector fieldName="deviceGroupName" 
											  	  fieldValue="${deviceGroupName}" 
											      dataJson="${groupDataJson}"
											      linkGroupName="true"/>
	                                                    
	                <tags:helpInfoPopup title="${deviceGroupText}">
	            		<cti:msg2 key="yukon.web.modules.amr.tamperFlagEditor.popupInfo.deviceGroup" htmlEscape="false"/>
					</tags:helpInfoPopup>
					
				</tags:nameValue2>
			
				<%-- tamper flag group --%>
				<tags:nameValue2 nameKey=".label.tamperFlagGroup">
					<div id="tamperFlagGroupNameDiv">${tamperFlagGroupBase}${name}</div>			
				</tags:nameValue2>
			
				<%-- enable/disable monitoring --%>
				<c:if test="${tamperFlagMonitorId > 0}">
					<tags:nameValue2 nameKey=".label.tamperFlagMonitoring">
						${tamperFlagMonitor.evaluatorStatus.description}
					</tags:nameValue2>
				</c:if>
				
			</tags:nameValueContainer2>
			
			</tags:sectionContainer>
				
			<%-- create / update / delete --%>
			<br>
			<c:choose>
				<c:when test="${tamperFlagMonitorId > 0}">
					<cti:msg2 var="updateText" key=".label.update"/>
                    <cti:msg2 var="updateBusyText" key=".label.update.busy"/>
                    <tags:slowInput myFormId="updateForm" labelBusy="${updateBusyText}" label="${updateText}" disableOtherButtons="true"/>
					<c:choose>
						<c:when test="${tamperFlagMonitor.evaluatorStatus eq 'ENABLED'}">
							<cti:msg2 var="tamperFlagMonitoringDisableText" key=".label.tamperFlagMonitoringDisable"/>
                            <tags:slowInput myFormId="toggleEnabledForm" labelBusy="${tamperFlagMonitoringDisableText}" label="${tamperFlagMonitoringDisableText}" disableOtherButtons="true"/>
						</c:when>
						<c:when test="${tamperFlagMonitor.evaluatorStatus eq 'DISABLED'}">
							<cti:msg2 var="tamperFlagMonitoringEnableText" key=".label.tamperFlagMonitoringEnable"/>
                            <tags:slowInput myFormId="toggleEnabledForm" labelBusy="${tamperFlagMonitoringEnableText}" label="${tamperFlagMonitoringEnableText}" disableOtherButtons="true"/>
						</c:when>
					</c:choose>
                    <cti:msg2 var="deleteText" key=".label.delete"/>
					<cti:msg2 var="deleteBusyText" key=".label.delete.busy"/>
                    <cti:msg2 var="deleteConfirmText" key=".deleteConfirm"/>
                    <input type="button" onclick="tamperFlagMonitorEditor_deleteTamperFlagMonitor(${tamperFlagMonitorId});" value="${deleteText}" class="formSubmit"/>
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
