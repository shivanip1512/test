<%@ taglib prefix="amr" tagdir="/WEB-INF/tags/amr" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="d" tagdir="/WEB-INF/tags/dialog" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<cti:standardPage module="amr" page="validationEditor.${mode}" >
<style>.validation-value{text-align: right;margin-right: 5px !important;}</style>
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
    <cti:csrfToken/>
        <input type="hidden" name="deleteValidationMonitorId" value="${validationMonitorId}">
    </form>
    
    <form id="toggleEnabledForm" action="/common/vee/monitor/toggleEnabled" method="post">
    <cti:csrfToken/>
        <input type="hidden" name="validationMonitorId" value="${validationMonitorId}">
    </form>
    
    <form id="cancelForm" action="/meter/start" method="get">
    </form>
    
    <%-- UPDATE FORM --%>
    <form id="updateForm" action="/common/vee/monitor/update" method="post">
    <cti:csrfToken/>
        <input type="hidden" name="validationMonitorId" value="${validationMonitorId}">
        
       <tags:sectionContainer2 nameKey="setup">
            <tags:nameValueContainer2>
                
                <%-- name --%>
                <tags:nameValue2 nameKey=".name">
                    <input type="text" name="name" value="${name}">
                </tags:nameValue2>
                
                <%-- device group --%>
                <tags:nameValue2 nameKey=".deviceGroup">
                    <cti:deviceGroupHierarchyJson predicates="NON_HIDDEN" var="groupDataJson" />
                    <tags:deviceGroupNameSelector fieldName="deviceGroupName" fieldValue="${deviceGroupName}" dataJson="${groupDataJson}" linkGroupName="true"/>
                    <cti:msg2 key=".deviceGroup" var="popupTitle"/>
                    <tags:helpInfoPopup title="${popupTitle}" iconClass="show-on-hover"><i:inline key=".deviceGroup.helpText" /></tags:helpInfoPopup>
                </tags:nameValue2>
                
                <%-- threshold --%>
                <tags:nameValue2 nameKey=".threshold">
                    <div>
                        <input type="text" name="threshold" class="validation-value" value="${threshold}"><span><i:inline key=".thresholdUnits"/></span> 
                        <cti:msg2 key=".threshold" var="popupTitle"/>
                        <tags:helpInfoPopup title="${popupTitle}" iconClass="show-on-hover"><i:inline key=".threshold.helpText"/></tags:helpInfoPopup>
                    </div>
                    <div>
                        <cti:msg2 key=".rereadThreshold" var="rereadThresholdText"/>
                        <input type="checkbox" name="reread" class="validation-value" <c:if test="${reread}">checked</c:if>><span>${rereadThresholdText}</span> 
                        <tags:helpInfoPopup title="${rereadThresholdText}" iconClass="show-on-hover"><i:inline key=".rereadThreshold.helpText"/></tags:helpInfoPopup>
                    </div>
                </tags:nameValue2>
                
                <%-- slope error --%>
                <tags:nameValue2 nameKey=".slopeError">
                    <input type="text" name="slopeError" class="validation-value" value="${slopeError}"><span><i:inline key=".slopeErrorUnits"/></span>
                    <cti:msg2 key=".slopeError" var="popupTitle"/>
                    <tags:helpInfoPopup title="${popupTitle}" iconClass="show-on-hover"><i:inline key=".slopeError.helpText"/></tags:helpInfoPopup>
                </tags:nameValue2>
                
                <%-- peak height minimum --%>
                <tags:nameValue2 nameKey=".peakHeightMinimum">
                    <div>
                        <input type="text" name="peakHeightMinimum" class="validation-value" value="${peakHeightMinimum}"><span><i:inline key=".peakHeightMinimumUnits"/></span>
                        <cti:msg2 key=".peakHeightMinimum" var="popupTitle"/>
                        <tags:helpInfoPopup title="${popupTitle}" iconClass="show-on-hover"><i:inline key=".peakHeightMinimum.helpText"/></tags:helpInfoPopup>
                    </div>
                    <div>
                        <input type="checkbox" name="setQuestionable" <c:if test="${setQuestionable}">checked</c:if>><span><i:inline key=".setQuestionable"/></span>
                        <cti:msg2 key=".setQuestionable" var="popupTitle"/>
                        <tags:helpInfoPopup title="${popupTitle}" iconClass="show-on-hover"><i:inline key=".setQuestionable.helpText"/></tags:helpInfoPopup>
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

        <div class="page-action-area">
            <c:choose>
                <c:when test="${validationMonitorId >= 0}">
                    <cti:button nameKey="update" busy="true" type="submit" classes="primary action" data-disable-group="actionButtons" />
                    <c:set var="toggleText" value="enable"/>
                    <c:if test="${validationMonitor.evaluatorStatus eq 'ENABLED'}">
                        <c:set var="toggleText" value="disable"/>
                    </c:if>
                     <cti:button nameKey="${toggleText}" onclick="jQuery('#toggleEnabledForm').submit();" busy="true" data-disable-group="actionButtons"/>
                   
                    <cti:button id="deleteButton" nameKey="delete" type="button" onclick="deleteValidationMonitor();" busy="true" data-disable-group="actionButtons" classes="delete"/>
                    <d:confirm on="#deleteButton" nameKey="confirmDelete"/>
                </c:when>
                <c:otherwise>
                    <cti:button nameKey="save" type="submit" busy="true" data-disable-group="actionButtons" />
                </c:otherwise>
            </c:choose>
            <cti:url var="backUrl" value="/meter/start"/>
            <cti:button nameKey="cancel" type="button" href="${backUrl}" busy="true" data-disable-group="actionButtons" />
        </div>
    </form>
        
</cti:standardPage>