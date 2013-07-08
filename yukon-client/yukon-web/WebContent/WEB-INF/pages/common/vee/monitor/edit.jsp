<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti"%>
<%@ taglib prefix="amr" tagdir="/WEB-INF/tags/amr"%>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags"%>
<%@ taglib prefix="d" tagdir="/WEB-INF/tags/dialog"%>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n"%>

<cti:standardPage module="amr" page="validationEditor.${mode}" >

<c:url var="help" value="/WebConfig/yukon/Icons/help.gif"/>
<c:url var="helpOver" value="/WebConfig/yukon/Icons/help_over.gif"/>
    
    <script type="text/javascript">
        function deleteValidationMonitor() {
            jQuery("button[data-disable-group=actionButtons]").each( function(){
                this.disabled = true;
            });
            jQuery('#configDeleteForm').submit();
        };
    </script>

    <c:if test="${not empty editError}">
        <div class="error">${editError}</div>
    </c:if>
    
    <c:if test="${saveOk}">
        <div class="fwb"><i:inline key=".saveOk"/></div>
        
    </c:if>
    
    <%-- MISC FORMS --%>
    <form id="configDeleteForm" action="/common/vee/monitor/delete" method="post">
        <input type="hidden" name="deleteValidationMonitorId" value="${validationMonitorId}">
    </form>
    
    <form id="toggleEnabledForm" action="/common/vee/monitor/toggleEnabled" method="post">
        <input type="hidden" name="validationMonitorId" value="${validationMonitorId}">
    </form>
    
    <form id="cancelForm" action="/meter/start" method="get">
	</form>
    
    <%-- UPDATE FORM --%>
    <form id="updateForm" action="/common/vee/monitor/update" method="post">
    
        <input type="hidden" name="validationMonitorId" value="${validationMonitorId}">
        
       <tags:sectionContainer2 nameKey="setup">
            <tags:nameValueContainer2>
                
                <%-- name --%>
                <tags:nameValue2 nameKey=".name">
                    <input type="text" name="name" size="50" value="${name}">
                </tags:nameValue2>
                
                <%-- device group --%>
                <tags:nameValue2 nameKey=".deviceGroup">
                    <cti:deviceGroupHierarchyJson predicates="NON_HIDDEN" var="groupDataJson" />
                    <tags:deviceGroupNameSelector fieldName="deviceGroupName" fieldValue="${deviceGroupName}" dataJson="${groupDataJson}" linkGroupName="true"/>
                    <cti:msg2 key=".deviceGroup" var="popupTitle"/>
                    <tags:helpInfoPopup title="${popupTitle}" ><i:inline key=".deviceGroup.helpText"/></tags:helpInfoPopup>
                </tags:nameValue2>
                
                <%-- threshold --%>
                <tags:nameValue2 nameKey=".threshold">
                    <div>
                        <input type="text" name="threshold" style="text-align: right;" value="${threshold}"><span><i:inline key=".thresholdUnits"/></span> 
                        <cti:msg2 key=".threshold" var="popupTitle"/>
                        <tags:helpInfoPopup title="${popupTitle}" ><i:inline key=".threshold.helpText"/></tags:helpInfoPopup>
                    </div>
                    <div>
                        <input type="checkbox" name="reread" style="text-align: right;" <c:if test="${reread}">checked</c:if>><span>${rereadThresholdText}</span> 
                        <cti:msg2 key=".rereadThreshold" var="popupTitle"/>
                        <tags:helpInfoPopup title="${popupTitle}" ><i:inline key=".rereadThreshold.helpText"/></tags:helpInfoPopup>
                    </div>
                </tags:nameValue2>
                
                <%-- slope error --%>
                <tags:nameValue2 nameKey=".slopeError">
                    <input type="text" name="slopeError" style="text-align:right;" value="${slopeError}"><span><i:inline key=".slopeErrorUnits"/></span>
                    <cti:msg2 key=".slopeError" var="popupTitle"/>
                    <tags:helpInfoPopup title="${popupTitle}" ><i:inline key=".slopeError.helpText"/></tags:helpInfoPopup>
                </tags:nameValue2>
                
                <%-- peak height minimum --%>
                <tags:nameValue2 nameKey=".peakHeightMinimum">
                    <div>
                        <input type="text" name="peakHeightMinimum" style="text-align: right;" value="${peakHeightMinimum}"><span><i:inline key=".peakHeightMinimumUnits"/></span>
                        <cti:msg2 key=".peakHeightMinimum" var="popupTitle"/>
                        <tags:helpInfoPopup title="${popupTitle}" ><i:inline key=".peakHeightMinimum.helpText"/></tags:helpInfoPopup>
                    </div>
                    <div>
                        <input type="checkbox" name="setQuestionable" style="text-align: right;" <c:if test="${setQuestionable}">checked</c:if>><span><i:inline key=".setQuestionable"/></span>
                        <cti:msg2 key=".setQuestionable" var="popupTitle"/>
                        <tags:helpInfoPopup title="${popupTitle}" ><i:inline key=".setQuestionable.helpText"/></tags:helpInfoPopup>
                    </div>        
                </tags:nameValue2>
                
                <%-- enable/disable monitoring --%>
                <c:if test="${validationMonitorId > 0}">
                    <tags:nameValue2 nameKey=".validationMonitoring">
                        ${validationMonitor.evaluatorStatus.description}
                    </tags:nameValue2>
                </c:if>
                
            </tags:nameValueContainer2>
            
       </tags:sectionContainer2>

        <div class="pageActionArea">
            <c:choose>
                <c:when test="${validationMonitorId >= 0}">
                    <cti:button nameKey="update" busy="true" type="submit" classes="primary action f-disableAfterClick" data-disable-group="actionButtons" />
                    <c:set var="toggleText" value="enable"/>
                    <c:if test="${validationMonitor.evaluatorStatus eq 'ENABLED'}">
                        <c:set var="toggleText" value="disable"/>
                    </c:if>
                     <cti:button nameKey="${toggleText}" onclick="jQuery('#toggleEnabledForm').submit();" busy="true" classes="f-disableAfterClick" data-disable-group="actionButtons"/>
                   
                    <cti:button id="deleteButton" nameKey="delete" type="button" onclick="deleteValidationMonitor();" busy="true" data-disable-group="actionButtons"/>
                    <d:confirm on="#deleteButton" nameKey="confirmDelete"/>
                </c:when>
                <c:otherwise>
                    <cti:button nameKey="save" type="submit" busy="true" classes="f-disableAfterClick" data-disable-group="actionButtons" />
                </c:otherwise>
            </c:choose>
            <cti:url var="backUrl" value="/meter/start"/>
            <cti:button nameKey="cancel" type="button" href="${backUrl}" busy="true" classes="f-disableAfterClick" data-disable-group="actionButtons" />
        </div>
    </form>
        
</cti:standardPage>