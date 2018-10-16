<%@ taglib prefix="amr" tagdir="/WEB-INF/tags/amr" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>

<cti:standardPage module="amr" page="validationEditor.${mode}" >
<tags:setFormEditMode mode="${mode}" />
<c:set var="viewMode" value="${false}" />
        <cti:displayForPageEditModes modes="VIEW">
            <c:set var="viewMode" value="${true}" />
        </cti:displayForPageEditModes>

        <c:set var="tableClass" value="${viewMode ? '' : 'with-form-controls'}" />
        
<style>.validation-value{text-align: right;margin-right: 5px !important;}</style>

    <c:if test="${not empty editError}">
        <div class="error">${fn:escapeXml(editError)}</div>
    </c:if>
    
    <c:if test="${saveOk}">
        <div class="fwb"><i:inline key=".saveOk"/></div>
        
    </c:if>
    
    <%-- MISC FORMS --%>
    <cti:url var="deleteUrl" value="/amr/vee/monitor/delete"/>
    <form:form id="deleteMonitorForm" action="${deleteUrl}" method="post" modelAttribute="validationMonitor">
        <cti:csrfToken/>
        <tags:hidden path="validationMonitorId"/>
        <tags:hidden path="name"/>
    </form:form>

    <cti:url var="enabledUrl" value="/amr/vee/monitor/toggleEnabled"/>
    <form id="toggleEnabledForm" action="${enabledUrl}" method="post">
        <cti:csrfToken/>
        <input type="hidden" name="validationMonitorId" value="${validationMonitor.validationMonitorId}">
    </form>
    
    <cti:url var="startUrl" value="/meter/start"/>
    <form id="cancelForm" action="${startUrl}" method="get">
    </form>
    
    <%-- UPDATE FORM --%>
    <cti:url var="updateUrl" value="/amr/vee/monitor/update"/>
    <form:form id="updateForm" action="${updateUrl}" method="post" modelAttribute="validationMonitor">
        <cti:csrfToken/>
        <c:if test="${not empty validationMonitorId}">
            <form:hidden path="validationMonitorId" />
        </c:if>
        <form:hidden path="evaluatorStatus" />
       <tags:sectionContainer2 nameKey="mainDetail.sectionHeader">
            <tags:nameValueContainer2 tableClass="natural-width ${tableClass}">
                
                <%-- name --%>
                <tags:nameValue2 nameKey=".name">
                    <tags:input path="name" maxlength="60"/>
                </tags:nameValue2>
                
                <%-- device group --%>
                <tags:nameValue2 nameKey=".deviceGroup">
                    <cti:deviceGroupHierarchyJson predicates="NON_HIDDEN" var="groupDataJson" />
                    <tags:deviceGroupNameSelector fieldName="groupName" fieldValue="${validationMonitor.groupName}" dataJson="${groupDataJson}" linkGroupName="true"/>
                    <cti:msg2 key=".deviceGroup" var="popupTitle"/>
                    <tags:helpInfoPopup title="${popupTitle}" classes="show-on-hover" width="600"><i:inline key=".deviceGroup.helpText" /></tags:helpInfoPopup>
                </tags:nameValue2>
                
                <%-- threshold --%>
                <tags:nameValue2 nameKey=".threshold">
                    <div>
                    <tags:input units="kWh/day" id="reasonableMaxKwhPerDay" path="reasonableMaxKwhPerDay" size="6"/>
                        <cti:msg2 key=".threshold" var="popupTitle"/>
                        <tags:helpInfoPopup title="${popupTitle}" classes="show-on-hover" width="600"><i:inline key=".threshold.helpText"/></tags:helpInfoPopup>
                    </div>
                    <div>
                        <cti:msg2 key=".rereadThreshold" var="rereadThresholdText"/> 
                        <tags:checkbox path="reReadOnUnreasonable" id="reReadOnUnreasonable"/>
                        <i:inline key=".rereadThreshold"/>
                        <tags:helpInfoPopup title="${rereadThresholdText}" classes="show-on-hover" width="600"><i:inline key=".rereadThreshold.helpText"/></tags:helpInfoPopup>
                    </div>
                </tags:nameValue2>
                
                <%-- slope error --%>
                <tags:nameValue2 nameKey=".slopeError">
                    <tags:input units="kWh/day" id="kwhSlopeError" path="kwhSlopeError" size="6"/>
                    <cti:msg2 key=".slopeError" var="popupTitle"/>
                    <tags:helpInfoPopup title="${popupTitle}" classes="show-on-hover" width="600"><i:inline key=".slopeError.helpText"/></tags:helpInfoPopup>
                </tags:nameValue2>
                
                <%-- peak height minimum --%>
                <tags:nameValue2 nameKey=".peakHeightMinimum">
                    <div>
                        <tags:input units="kWh" id="peakHeightMinimum" path="peakHeightMinimum" size="6"/>
                        <cti:msg2 key=".peakHeightMinimum" var="popupTitle"/>
                        <tags:helpInfoPopup title="${popupTitle}" classes="show-on-hover" width="600"><i:inline key=".peakHeightMinimum.helpText"/></tags:helpInfoPopup>
                    </div>
                    <div>
                        <label>
                            <tags:checkbox path="questionableOnPeak" id="questionableOnPeak"/>
                            <i:inline key=".setQuestionable"/>
                        </label>
                        <cti:msg2 key=".setQuestionable" var="popupTitle"/>
                        <tags:helpInfoPopup title="${popupTitle}" classes="show-on-hover" width="600"><i:inline key=".setQuestionable.helpText"/></tags:helpInfoPopup>
                    </div>        
                </tags:nameValue2>
                
                <%-- enable/disable monitoring --%>
                <c:if test="${validationMonitor.validationMonitorId > 0}">
                    <c:if test="${validationMonitor.evaluatorStatus.description=='Enabled'}"><c:set var="clazz" value="success"/></c:if>
                    <c:if test="${validationMonitor.evaluatorStatus.description=='Disabled'}"><c:set var="clazz" value="error"/></c:if>
                    <tags:nameValue2 nameKey=".validationMonitoring" valueClass="${clazz}">
                        ${validationMonitor.evaluatorStatus.description}
                    </tags:nameValue2>
                </c:if>
                
            </tags:nameValueContainer2>
            
       </tags:sectionContainer2>

        <div class="page-action-area">
            <c:choose>
                <c:when test="${validationMonitor.validationMonitorId >= 0}">
                <cti:displayForPageEditModes modes="VIEW">
                    <cti:url var="editUrl" value="/amr/vee/monitor/${validationMonitor.validationMonitorId}/edit" />
                <cti:button classes="js-calculating-disable" nameKey="edit" icon="icon-pencil" href="${editUrl}"/>
                </cti:displayForPageEditModes>
                    <cti:displayForPageEditModes modes="EDIT,CREATE">
                    <cti:button nameKey="save" busy="true" type="submit" classes="primary action" data-disable-group="actionButtons" />
                </cti:displayForPageEditModes>
                <c:set var="toggleText" value="enable"/>
                <c:if test="${validationMonitor.evaluatorStatus eq 'ENABLED'}">
                    <c:set var="toggleText" value="disable"/>
                </c:if>
                <cti:displayForPageEditModes modes="EDIT">
                    <cti:button id="toggleMonitor" nameKey="${toggleText}" busy="true" data-disable-group="actionButtons"/>
                    <cti:button id="deleteButton" nameKey="delete" data-disable-group="actionButtons" 
                                classes="delete" data-popup="#confirm-delete-monitor-popup"/>
                    <amr:confirmDeleteMonitor target="#deleteButton" monitorName="${validationMonitor.name}"/>
                    <cti:url var="backUrl" value="/amr/vee/monitor/${validationMonitor.validationMonitorId}/view"/>
                    <cti:button nameKey="cancel" href="${backUrl}" busy="true" data-disable-group="actionButtons" />
                </cti:displayForPageEditModes>
                </c:when>
                <c:otherwise>
                    <cti:button nameKey="save" type="submit" busy="true" data-disable-group="actionButtons" classes="primary action"/>
                </c:otherwise>
            </c:choose>
            
        </div>
    </form:form>
<cti:includeScript link="/resources/js/pages/yukon.ami.monitor.js"/>
</cti:standardPage>