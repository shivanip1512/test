<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti"%>
<%@ taglib prefix="amr" tagdir="/WEB-INF/tags/amr"%>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags"%>

<cti:msg var="pageTitle" key="yukon.common.vee.monitor.editor.pageTitle" />
<cti:msg var="setupSectiontext" key="yukon.common.vee.monitor.editor.section.setup" />
<cti:msg var="editSetupSectionText" key="yukon.common.vee.monitor.editor.section.editSetup" />

<cti:msg var="nameText" key="yukon.common.vee.monitor.editor.label.name"/>

<cti:msg var="deviceGroupText" key="yukon.common.vee.monitor.editor.label.deviceGroup"/>
<cti:msg var="deviceGroupPopupInfoText" key="yukon.common.vee.monitor.editor.popupInfo.deviceGroup"/>

<cti:msg var="thresholdText" key="yukon.common.vee.monitor.editor.label.thresholdText"/>
<cti:msg var="thresholdUnits" key="yukon.common.vee.monitor.editor.label.thresholdUnits"/>
<cti:msg var="thresholdPopupInfoText" key="yukon.common.vee.monitor.editor.popupInfo.thresholdPopupInfoText"/>

<cti:msg var="rereadThresholdText" key="yukon.common.vee.monitor.editor.label.rereadThresholdText"/>
<cti:msg var="rereadThresholdPopupInfoText" key="yukon.common.vee.monitor.editor.popupInfo.rereadThresholdPopupInfoText"/>

<cti:msg var="slopeErrorText" key="yukon.common.vee.monitor.editor.label.slopeErrorText"/>
<cti:msg var="slopeErrorUnits" key="yukon.common.vee.monitor.editor.label.slopeErrorUnits"/>
<cti:msg var="slopeErrorPopupInfoText" key="yukon.common.vee.monitor.editor.popupInfo.slopeErrorPopupInfoText"/>

<cti:msg var="readingErrorText" key="yukon.common.vee.monitor.editor.label.readingErrorText"/>
<cti:msg var="readingErrorUnits" key="yukon.common.vee.monitor.editor.label.readingErrorUnits"/>
<cti:msg var="readingErrorPopupInfoText" key="yukon.common.vee.monitor.editor.popupInfo.readingErrorPopupInfoText"/>

<cti:msg var="peakHeightMinimumText" key="yukon.common.vee.monitor.editor.label.peakHeightMinimumText"/>
<cti:msg var="peakHeightMinimumUnits" key="yukon.common.vee.monitor.editor.label.peakHeightMinimumUnits"/>
<cti:msg var="peakHeightMinimumPopupInfoText" key="yukon.common.vee.monitor.editor.popupInfo.peakHeightMinimumPopupInfoText"/>

<cti:msg var="setQuestionableText" key="yukon.common.vee.monitor.editor.label.setQuestionableText"/>
<cti:msg var="setQuestionablePopupInfoText" key="yukon.common.vee.monitor.editor.popupInfo.setQuestionablePopupInfoText"/>

<cti:msg var="updateBusyText" key="yukon.common.vee.monitor.editor.label.update.updateBusyText"/>
<cti:msg var="updateText" key="yukon.common.vee.monitor.editor.label.update.updateText"/>

<cti:msg var="deleteText" key="yukon.common.vee.monitor.editor.label.delete.deleteText"/>
<cti:msg var="deleteConfirmText" key="yukon.common.vee.monitor.editor.label.delete.deleteConfirmText"/>

<cti:msg var="createBusyText" key="yukon.common.vee.monitor.editor.label.create.createBusyText"/>
<cti:msg var="createText" key="yukon.common.vee.monitor.editor.label.create.createText"/>

<cti:msg var="validationMonitoringText" key="yukon.common.vee.monitor.editor.label.validationMonitoring"/>
<cti:msg var="validationMonitoringEnableText" key="yukon.common.vee.monitor.editor.label.validationMonitoringEnable"/>
<cti:msg var="validationMonitoringDisableText" key="yukon.common.vee.monitor.editor.label.validationMonitoringDisable"/>
<cti:msg var="validationMonitoringDisableText" key="yukon.common.vee.monitor.editor.label.validationMonitoringDisable"/>
<cti:msg var="validationMonitoringEnablePopupInfo" key="yukon.common.vee.monitor.editor.popupInfo.validationMonitoringEnable"/>
<cti:msg var="validationMonitoringDisablePopupInfo" key="yukon.common.vee.monitor.editor.popupInfo.validationMonitoringDisable"/>

<cti:msg var="saveOkText" key="yukon.common.vee.monitor.editor.saveOk"/>

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
        function validationMonitorEditor_deleteValidationMonitor(id) {
    
            var deleteOk = confirm('${deleteConfirmText}');
    
            if (deleteOk) {
                $('deleteValidationMonitorId').value = id;
                $('configDeleteForm').submit();
            }
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
    <form id="configDeleteForm" action="/spring/common/vee/monitor/delete" method="post">
        <input type="hidden" id="deleteValidationMonitorId" name="deleteValidationMonitorId" value="">
    </form>
    
    <form id="enableMonitoringForm" action="/spring/common/vee/monitor/toggleMonitorEvaluationEnabled" method="post">
        <input type="hidden" name="validationMonitorId" value="${validationMonitorId}">
        <input type="hidden" name="enable" value="true">
    </form>
    
    <form id="disableMonitoringForm" action="/spring/common/vee/monitor/toggleMonitorEvaluationEnabled" method="post">
        <input type="hidden" name="validationMonitorId" value="${validationMonitorId}">
        <input type="hidden" name="enable" value="false">
    </form>
    
    <%-- UPDATE FORM --%>
    <form id="updateForm" action="/spring/common/vee/monitor/update" method="post">
    
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
                
                <%-- reading error --%>
                <tags:nameValue name="${readingErrorText}">
                    
                    <input type="text" name="readingError" style="text-align:right;" value="${readingError}"> 
                    ${readingErrorUnits}
                                                        
                    <tags:helpInfoPopup title="${readingErrorText}">
                        ${readingErrorPopupInfoText}
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
                        <c:choose>
                            <c:when test="${validationMonitor.evaluatorStatus eq 'ENABLED'}">
                                
                                <tags:slowInput myFormId="disableMonitoringForm" labelBusy="${validationMonitoringDisableText}" label="${validationMonitoringDisableText}"/>
                                
                                <tags:helpInfoPopup title="${validationMonitoringDisableText} ${validationMonitoringText}">
                                    ${validationMonitoringDisablePopupInfo}
                                </tags:helpInfoPopup>
                            
                            </c:when>
                            <c:when test="${validationMonitor.evaluatorStatus eq 'DISABLED'}">
                                
                                <tags:slowInput myFormId="enableMonitoringForm" labelBusy="${validationMonitoringEnableText}" label="${validationMonitoringEnableText}"/>
                                
                                <tags:helpInfoPopup title="${validationMonitoringEnableText} ${validationMonitoringText}">
                                    ${validationMonitoringEnablePopupInfo}
                                </tags:helpInfoPopup>
                                
                            </c:when>
                            <c:otherwise>
                                ${validationMonitor.evaluatorStatus.description}
                            </c:otherwise>
                        </c:choose>
                    </tags:nameValue>
                </c:if>
                
            </tags:nameValueContainer>
            
        </tags:sectionContainer>
        
        <br>
        <c:choose>
            <c:when test="${validationMonitorId >= 0}">
                <tags:slowInput myFormId="updateForm" labelBusy="${updateBusyText}" label="${updateText}"/>
                <input type="button" onclick="validationMonitorEditor_deleteValidationMonitor(${validationMonitorId});" value="${deleteText}"/>
            </c:when>
            <c:otherwise>
                <tags:slowInput myFormId="updateForm" labelBusy="${createBusyText}" label="${createText}"/>
            </c:otherwise>
        </c:choose>
        
    </form>
        
</cti:standardPage>