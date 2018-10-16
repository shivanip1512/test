<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti"%>
<%@ taglib prefix="amr" tagdir="/WEB-INF/tags/amr"%>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags"%>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>

<cti:url value="/amr/outageProcessing/monitorEditor/delete" var="deleteURL"/>
<cti:url value="/amr/outageProcessing/monitorEditor/toggleEnabled" var="toggleEnabledURL"/>
<cti:url value="/amr/outageProcessing/monitorEditor/update" var="updateURL"/>

<cti:standardPage module="amr" page="outageMonitorConfig.${mode}">

    <c:if test="${not empty editError}">
        <div class="error">${fn:escapeXml(editError)}</div>
    </c:if>
    
    <c:if test="${saveOk}">
        <div class="fwb"><i:inline key=".saveOk"/></div>
    </c:if>

    <%-- MISC FORMS --%>
    <form:form id="deleteMonitorForm" action="${deleteURL}" method="post" modelAttribute="outageMonitor">
        <cti:csrfToken/>
        <form:hidden path="outageMonitorId"/>
        <form:hidden path="name"/>
    </form:form>
    
    <form id="toggleEnabledForm" action="${toggleEnabledURL}" method="post">
        <cti:csrfToken/>
        <input type="hidden" id="outageMonitorId" name="outageMonitorId" value="${outageMonitor.outageMonitorId}">
    </form>
        
        <%-- UPDATE FORM --%>
    <form:form id="updateForm" action="${updateURL}" method="post" modelAttribute="outageMonitor">
        <cti:csrfToken/>
        <form:hidden path="outageMonitorId" id="outageMonitorId"/>
        <form:hidden path="evaluatorStatus" />
        
        <cti:msg2 var="setupSectiontext" key=".section.setup" />
        <cti:msg2 var="editSetupSectionText" key=".section.editSetup" />
        <c:set var="setupSectionTitle" value="${setupSectiontext}"/>
        <c:if test="${not empty outageMonitor.outageMonitorId}">
            <c:set var="setupSectionTitle" value="${editSetupSectionText}"/>
        </c:if>
        
        <tags:sectionContainer title="${setupSectionTitle}">
        
            <tags:nameValueContainer2>
            
                <%-- name --%>
                <tags:nameValue2 nameKey=".label.name">
                    <tags:input path="name" id="name" inputClass="js-monitor-name" size="60" maxlength="60" placeholder="${outageGroupBase}"/>
                </tags:nameValue2>
                
                <%-- device group --%>
                <tags:nameValue2 nameKey=".label.deviceGroup">
                    
                    <cti:deviceGroupHierarchyJson predicates="NON_HIDDEN" var="groupDataJson" />
                    <tags:deviceGroupNameSelector fieldName="groupName" 
                                                  fieldValue="${outageMonitor.groupName}" 
                                                  dataJson="${groupDataJson}"
                                                  linkGroupName="true"/>
                    <cti:msg2 var="deviceGroupText" key=".label.deviceGroup"/>
                    <tags:helpInfoPopup title="${deviceGroupText}" >
                        <cti:msg2 key=".popupInfo.deviceGroup"/>
                    </tags:helpInfoPopup>
                    
                </tags:nameValue2>
            
                <%-- outages group --%>
                <tags:nameValue2 nameKey=".label.outagesGroup">
                    <span class="group-base" data-groupbase="${fn:escapeXml(outageGroupBase)}">${fn:escapeXml(outageGroupBase)}${fn:escapeXml(outageMonitor.name)}</div>
                </tags:nameValue2>
            
                <%-- number of outages --%>
                <tags:nameValue2 nameKey=".label.numberOfOutages">
                    <tags:input path="numberOfOutages" id="numberOfOutages" maxlength="3" size="3" />
                    <i:inline key=".label.numberOfOutagesOutages"/>
                    <cti:msg2 var="numberOfOutagesText" key=".label.numberOfOutages"/>
                    <tags:helpInfoPopup title="${numberOfOutagesText}">
                        <cti:msg2 key=".popupInfo.numberOfOutages"/>
                    </tags:helpInfoPopup>
                    
                </tags:nameValue2>
                
                <%-- time period --%>
                <tags:nameValue2 nameKey=".label.timePeriod">
                    <tags:input path="timePeriodDays" id="timePeriodDays" maxlength="3" size="3" />
                    <i:inline key=".label.timePeriodDays"/>
                    <cti:msg2 var="timePeriodText" key=".label.timePeriod"/>
                    <tags:helpInfoPopup title="${timePeriodText}">
                        <cti:msg2 key=".popupInfo.timePeriod"/>
                    </tags:helpInfoPopup>
                    
                </tags:nameValue2>
                
                <%-- enable/disable monitoring --%>
                <c:if test="${not empty outageMonitor.outageMonitorId}">
                    <c:if test="${outageMonitor.enabled}"><c:set var="clazz" value="success"/></c:if>
                    <c:if test="${!outageMonitor.enabled}"><c:set var="clazz" value="error"/></c:if>
                    <tags:nameValue2 nameKey=".label.outageMonitoring" valueClass="${clazz}">
                        ${outageMonitor.evaluatorStatus.description}
                    </tags:nameValue2>
                </c:if>
                
            </tags:nameValueContainer2>
            
        </tags:sectionContainer>
        
        <%-- SCHEDULE --%>
        <cti:checkRolesAndProperties value="MANAGE_SCHEDULES">
        <c:if test="${empty outageMonitor.outageMonitorId}">
        <cti:msg2 var="scheduleSectionText" key=".section.schedule" />
        <tags:sectionContainer title="${scheduleSectionText}">
        
            <%-- note --%>
            <div class="stacked">
                <span class="strong-label-small">
                    <i:inline key=".popupInfo.scheduleReadNoteLabelText"/>
                </span>
                <span class="notes">
                    <i:inline key=".popupInfo.scheduleReadNoteBodyText"/>
                </span>
            </div>
    
        
            <tags:nameValueContainer>
                
                <%-- schedule read --%>
                <tags:checkbox path="scheduleGroupCommand" id="scheduleGroupCommand"/>
                <i:inline key=".label.scheduleReadDescription"/>
                
                <cti:msg2 var="scheduleReadText" key=".label.scheduleRead"/>
                <tags:helpInfoPopup title="${scheduleReadText}">
                    <cti:msg2 key=".popupInfo.scheduleRead"/>
                </tags:helpInfoPopup>
                    
                <%-- schedule name --%>
                <cti:msg2 var="scheduleNameText" key=".label.scheduleName"/>
                <tags:nameValue name="${fn:escapeXml(scheduleNameText)}" id="scheduleNameTr" nameColumnWidth="250px">
                    <tags:input path="scheduleName"/>
                 </tags:nameValue>
                 
                <%-- time / frequency --%>
                <cti:msg2 var="readFrequencyText" key=".label.readFrequency"/>
                <tags:nameValue name="${readFrequencyText}" id="readFrequencyTr">
                    <tags:cronExpressionData id="${cronExpressionTagId}" state="${cronExpressionTagState}"/>
                </tags:nameValue>
                
            </tags:nameValueContainer>
            
        </tags:sectionContainer>
        </c:if>
        </cti:checkRolesAndProperties>

        <%-- create / update / delete --%>
        <div class="page-action-area">
            <c:choose>
                <c:when test="${not empty outageMonitor.outageMonitorId}">
                    <cti:button nameKey="save" busy="true" type="submit" classes="primary action" data-disable-group="actionButtons" />
                    <c:set var="enableDisableKey" value="disable"/>
                    <c:if test="${outageMonitor.evaluatorStatus eq 'DISABLED'}">
                        <c:set var="enableDisableKey" value="enable"/>
                    </c:if>
                    <cti:button id="toggleMonitor" nameKey="${enableDisableKey}" busy="true" data-disable-group="actionButtons"/>
                    <cti:button id="deleteButton" nameKey="delete" data-disable-group="actionButtons" 
                                classes="delete" data-popup="#confirm-delete-monitor-popup"/>
                    
                    <amr:confirmDeleteMonitor target="#deleteButton" monitorName="${outageMonitor.name}"/>
                    
                    <cti:url var="backUrl" value="/amr/outageProcessing/process/process">
                        <cti:param name="outageMonitorId" value="${outageMonitor.outageMonitorId}" />
                    </cti:url>
                </c:when>
                <c:otherwise>
                    <cti:button nameKey="save" type="submit" busy="true" data-disable-group="actionButtons" classes="primary action"/>
                    <cti:url var="backUrl" value="/meter/start"/>
                </c:otherwise>
            </c:choose>
            <cti:button nameKey="cancel" href="${backUrl}" busy="true" data-disable-group="actionButtons" />
        </div>
    </form:form>
    <cti:includeScript link="/resources/js/pages/yukon.ami.outage.monitor.js"/>
    <cti:includeScript link="/resources/js/pages/yukon.ami.monitor.js"/>
</cti:standardPage>