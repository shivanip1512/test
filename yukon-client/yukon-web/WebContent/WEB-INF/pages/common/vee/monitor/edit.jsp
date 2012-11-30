<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti"%>
<%@ taglib prefix="amr" tagdir="/WEB-INF/tags/amr"%>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags"%>

<cti:standardPage page="validationEditor.${mode}" module="amr">

<cti:msg var="pageTitle" key="yukon.web.modules.common.vee.monitor.editor.pageTitle" />
<cti:msg var="setupSectiontext" key="yukon.web.modules.common.vee.monitor.editor.section.setup" />
<cti:msg var="editSetupSectionText" key="yukon.web.modules.common.vee.monitor.editor.section.editSetup" />

<cti:msg var="nameText" key="yukon.web.modules.common.vee.monitor.editor.label.name"/>

<cti:msg var="deviceGroupText" key="yukon.web.modules.common.vee.monitor.editor.label.deviceGroup"/>
<cti:msg var="deviceGroupPopupInfoText" key="yukon.web.modules.common.vee.monitor.editor.popupInfo.deviceGroup"/>

<cti:msg var="thresholdText" key="yukon.web.modules.common.vee.monitor.editor.label.thresholdText"/>
<cti:msg var="thresholdUnits" key="yukon.web.modules.common.vee.monitor.editor.label.thresholdUnits"/>
<cti:msg var="thresholdPopupInfoText" key="yukon.web.modules.common.vee.monitor.editor.popupInfo.thresholdPopupInfoText"/>

<cti:msg var="rereadThresholdText" key="yukon.web.modules.common.vee.monitor.editor.label.rereadThresholdText"/>
<cti:msg var="rereadThresholdPopupInfoText" key="yukon.web.modules.common.vee.monitor.editor.popupInfo.rereadThresholdPopupInfoText"/>

<cti:msg var="slopeErrorText" key="yukon.web.modules.common.vee.monitor.editor.label.slopeErrorText"/>
<cti:msg var="slopeErrorUnits" key="yukon.web.modules.common.vee.monitor.editor.label.slopeErrorUnits"/>
<cti:msg var="slopeErrorPopupInfoText" key="yukon.web.modules.common.vee.monitor.editor.popupInfo.slopeErrorPopupInfoText"/>

<cti:msg var="readingErrorText" key="yukon.web.modules.common.vee.monitor.editor.label.readingErrorText"/>
<cti:msg var="readingErrorUnits" key="yukon.web.modules.common.vee.monitor.editor.label.readingErrorUnits"/>
<cti:msg var="readingErrorPopupInfoText" key="yukon.web.modules.common.vee.monitor.editor.popupInfo.readingErrorPopupInfoText"/>

<cti:msg var="peakHeightMinimumText" key="yukon.web.modules.common.vee.monitor.editor.label.peakHeightMinimumText"/>
<cti:msg var="peakHeightMinimumUnits" key="yukon.web.modules.common.vee.monitor.editor.label.peakHeightMinimumUnits"/>
<cti:msg var="peakHeightMinimumPopupInfoText" key="yukon.web.modules.common.vee.monitor.editor.popupInfo.peakHeightMinimumPopupInfoText"/>

<cti:msg var="setQuestionableText" key="yukon.web.modules.common.vee.monitor.editor.label.setQuestionableText"/>
<cti:msg var="setQuestionablePopupInfoText" key="yukon.web.modules.common.vee.monitor.editor.popupInfo.setQuestionablePopupInfoText"/>

<cti:msg var="updateBusyText" key="yukon.web.modules.common.vee.monitor.editor.label.update.updateBusyText"/>
<cti:msg var="updateText" key="yukon.web.modules.common.vee.monitor.editor.label.update.updateText"/>

<cti:msg var="deleteText" key="yukon.web.modules.common.vee.monitor.editor.label.delete.deleteText"/>
<cti:msg var="deleteConfirmText" key="yukon.web.modules.common.vee.monitor.editor.label.delete.deleteConfirmText"/>

<cti:msg var="createBusyText" key="yukon.web.modules.common.vee.monitor.editor.label.create.createBusyText"/>
<cti:msg var="createText" key="yukon.web.modules.common.vee.monitor.editor.label.create.createText"/>

<cti:msg var="cancelText" key="yukon.web.modules.common.vee.monitor.editor.label.cancel.cancelText"/>

<cti:msg var="validationMonitoringText" key="yukon.web.modules.common.vee.monitor.editor.label.validationMonitoring"/>
<cti:msg var="validationMonitoringEnableText" key="yukon.web.modules.common.vee.monitor.editor.label.validationMonitoringEnable"/>
<cti:msg var="validationMonitoringDisableText" key="yukon.web.modules.common.vee.monitor.editor.label.validationMonitoringDisable"/>
<cti:msg var="validationMonitoringDisableText" key="yukon.web.modules.common.vee.monitor.editor.label.validationMonitoringDisable"/>
<cti:msg var="validationMonitoringEnablePopupInfo" key="yukon.web.modules.common.vee.monitor.editor.popupInfo.validationMonitoringEnable"/>
<cti:msg var="validationMonitoringDisablePopupInfo" key="yukon.web.modules.common.vee.monitor.editor.popupInfo.validationMonitoringDisable"/>

<cti:msg var="saveOkText" key="yukon.web.modules.common.vee.monitor.editor.saveOk"/>

<c:url var="help" value="/WebConfig/yukon/Icons/help.gif"/>
<c:url var="helpOver" value="/WebConfig/yukon/Icons/help_over.gif"/>

    
    <script type="text/javascript">
        function validationMonitorEditor_deleteValidationMonitor(id) {
    
            var deleteOk = confirm('${deleteConfirmText}');
    
            if (deleteOk) {
                $('deleteValidationMonitorId').value = id;
                $('configDeleteForm').submit();

                $$('input[type=button]').each(function(el) {
					el.disable();
				});
            }
        }
    </script>

    <c:if test="${not empty editError}">
        <div class="error">${editError}</div>
    </c:if>
    
    <c:if test="${saveOk}">
        <div class="fwb">${saveOkText}</div>
    </c:if>
    
    <%-- MISC FORMS --%>
    <form id="configDeleteForm" action="/common/vee/monitor/delete" method="post">
        <input type="hidden" id="deleteValidationMonitorId" name="deleteValidationMonitorId" value="">
    </form>
    
    <form id="toggleEnabledForm" action="/common/vee/monitor/toggleEnabled" method="post">
        <input type="hidden" name="validationMonitorId" value="${validationMonitorId}">
    </form>
    
    <form id="cancelForm" action="/meter/start" method="get">
	</form>
    
    <%-- UPDATE FORM --%>
    <form id="updateForm" action="/common/vee/monitor/update" method="post">
    
        <input type="hidden" name="validationMonitorId" value="${validationMonitorId}">
        
        <c:set var="setupSectionTitle" value="${setupSectiontext}"/>
        <c:if test="${validationMonitorId > 0}">
            <c:set var="setupSectionTitle" value="${editSetupSectionText}"/>
        </c:if>
        
        <tags:sectionContainer title="${setupSectionTitle}">
        
            <tags:nameValueContainer style="border-collapse:separate;border-spacing:5px;">
                
                <%-- name --%>
                <tags:nameValue name="${nameText}" nameColumnWidth="250px">
                    <input type="text" name="name" size="50" value="${name}">
                </tags:nameValue>
                
                <%-- device group --%>
                <tags:nameValue name="${deviceGroupText}">
                    
                    <cti:deviceGroupHierarchyJson predicates="NON_HIDDEN" var="groupDataJson" />
                    <tags:deviceGroupNameSelector fieldName="deviceGroupName" fieldValue="${deviceGroupName}" dataJson="${groupDataJson}" linkGroupName="true"/>
                                                        
                    <tags:helpInfoPopup title="${deviceGroupText}">
                        ${deviceGroupPopupInfoText}
                    </tags:helpInfoPopup>
                    
                </tags:nameValue>
                
                <%-- threshold --%>
                <tags:nameValue name="${thresholdText}">
                <table>
                    <tr>
                        <td>
                            <input type="text" name="threshold" style="text-align: right;" value="${threshold}"> ${thresholdUnits} 
                            <tags:helpInfoPopup title="${thresholdText}">
                                ${thresholdPopupInfoText}
                            </tags:helpInfoPopup>
                        </td>
                    </tr>
                    <tr>
                        <td>
                            <input type="checkbox" name="reread" style="text-align: right;" <c:if test="${reread}">checked</c:if>> ${rereadThresholdText} 
                            <tags:helpInfoPopup title="${rereadThresholdText}">
                                ${rereadThresholdPopupInfoText}
                            </tags:helpInfoPopup>
                        </td>
                    </tr>
                </table>
            </tags:nameValue>
                
                <%-- slope error --%>
                <tags:nameValue name="${slopeErrorText}">
                    
                    <input type="text" name="slopeError" style="text-align:right;" value="${slopeError}"> 
                    ${slopeErrorUnits}
                                                        
                    <tags:helpInfoPopup title="${slopeErrorText}">
                        ${slopeErrorPopupInfoText}
                    </tags:helpInfoPopup>
                    
                </tags:nameValue>
                
                <%-- peak height minimum --%>
                <tags:nameValue name="${peakHeightMinimumText}">
                    <table>
                        <tr>
                            <td>
                                <input type="text" name="peakHeightMinimum" style="text-align: right;" value="${peakHeightMinimum}"> ${peakHeightMinimumUnits}
                                <tags:helpInfoPopup title="${peakHeightMinimumText}">
                                    ${peakHeightMinimumPopupInfoText}
                                </tags:helpInfoPopup>
                            </td>
                        </tr>
                        <tr>
                            <td>
                                <input type="checkbox" name="setQuestionable" style="text-align: right;" <c:if test="${setQuestionable}">checked</c:if>> ${setQuestionableText}
                                <tags:helpInfoPopup title="${setQuestionableText}">
                                    ${setQuestionablePopupInfoText}
                                </tags:helpInfoPopup>
                            </td>
                        </tr>
                    </table>
    
                    </tags:nameValue>
                
                <%-- enable/disable monitoring --%>
                <c:if test="${validationMonitorId > 0}">
                    <tags:nameValue name="${validationMonitoringText}">
                        ${validationMonitor.evaluatorStatus.description}
                    </tags:nameValue>
                </c:if>
                
            </tags:nameValueContainer>
            
        </tags:sectionContainer>
        
        <br>
        <c:choose>
            <c:when test="${validationMonitorId >= 0}">
                <tags:slowInput myFormId="updateForm" labelBusy="${updateBusyText}" label="${updateText}" disableOtherButtons="true"/>
                <c:choose>
				    <c:when test="${validationMonitor.evaluatorStatus eq 'ENABLED'}">
				        <tags:slowInput myFormId="toggleEnabledForm" labelBusy="${validationMonitoringDisableText}" label="${validationMonitoringDisableText}" disableOtherButtons="true"/>
				    </c:when>
				    <c:when test="${validationMonitor.evaluatorStatus eq 'DISABLED'}">
				        <tags:slowInput myFormId="toggleEnabledForm" labelBusy="${validationMonitoringEnableText}" label="${validationMonitoringEnableText}" disableOtherButtons="true"/>
				    </c:when>
				</c:choose>
				<input type="button" onclick="validationMonitorEditor_deleteValidationMonitor(${validationMonitorId});" value="${deleteText}" class="formSubmit"/>
            </c:when>
            <c:otherwise>
                <tags:slowInput myFormId="updateForm" labelBusy="${createBusyText}" label="${createText}" disableOtherButtons="true"/>
            </c:otherwise>
        </c:choose>
        <tags:slowInput myFormId="cancelForm" label="${cancelText}" disableOtherButtons="true"/>
        
    </form>
        
</cti:standardPage>