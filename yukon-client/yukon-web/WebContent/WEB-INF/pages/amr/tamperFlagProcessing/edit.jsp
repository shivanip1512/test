<%@ taglib prefix="amr" tagdir="/WEB-INF/tags/amr" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>

<cti:url value="/amr/tamperFlagProcessing/delete" var="monitorDeleteURL"/>
<cti:url value="/amr/tamperFlagProcessing/toggleEnabled" var="toggleEnabledURL"/>
<cti:url value="/amr/tamperFlagProcessing/update" var="updateURL"/>

<cti:standardPage module="amr" page="tamperFlagEditor.${mode}">

        <c:if test="${not empty editError}">
            <div class="error">${fn:escapeXml(editError)}</div>
        </c:if>
    
        <%-- MISC FORMS --%>
        <form:form id="deleteMonitorForm" action="${monitorDeleteURL}" method="post" modelAttribute="tamperFlagMonitor">
            <cti:csrfToken/>
            <tags:hidden path="tamperFlagMonitorId"/>
            <tags:hidden path="name"/>
        </form:form>
        
        <form id="toggleEnabledForm" action="${toggleEnabledURL}" method="post">
            <cti:csrfToken/>
            <input type="hidden" name="tamperFlagMonitorId" value="${tamperFlagMonitorId}">
        </form>
        
        <%-- UPDATE FORM --%>
       <form:form id="updateForm" action="${updateURL}" method="post" modelAttribute="tamperFlagMonitor">
            <cti:csrfToken/>
            <c:if test="${not empty tamperFlagMonitorId}">
                <form:hidden path="tamperFlagMonitorId" />
            </c:if>
            <form:hidden path="evaluatorStatus" />
            <cti:msg2 var="setupSectionText" key=".section.setup"/>
            <cti:msg2 var="editSetupSectionText" key=".section.editSetup"/>  
            <c:set var="setupSectionTitle" value="${setupSectionText}"/>
            <c:if test="${tamperFlagMonitorId > 0}">
                <c:set var="setupSectionTitle" value="${editSetupSectionText}"/>
            </c:if>
            
            <tags:sectionContainer title="${setupSectionTitle}">
            
            <tags:nameValueContainer2>
            
                <%-- name --%>
                <tags:nameValue2 nameKey=".label.name">
                    <tags:input path="name" maxlength="60" inputClass="js-monitor-name" />
                </tags:nameValue2>
                
                <%-- device group --%>
                <tags:nameValue2 nameKey=".label.deviceGroup">
                    
                    <cti:deviceGroupHierarchyJson predicates="NON_HIDDEN" var="groupDataJson"/>
                    <tags:deviceGroupNameSelector fieldName="groupName" 
                                                  fieldValue="${tamperFlagMonitor.groupName}" 
                                                  dataJson="${groupDataJson}"
                                                  linkGroupName="true"/>
                    <cti:msg2 var="deviceGroupText" key=".label.deviceGroup"/>
                    <tags:helpInfoPopup title="${deviceGroupText}">
                        <cti:msg2 key="yukon.web.modules.amr.tamperFlagEditor.popupInfo.deviceGroup"/>
                    </tags:helpInfoPopup>
                    
                </tags:nameValue2>
            
                <%-- tamper flag group --%>
                <tags:nameValue2 nameKey=".label.tamperFlagGroup">
                    <span class="group-base" data-groupbase="${fn:escapeXml(tamperFlagGroupBase)}">${fn:escapeXml(tamperFlagGroupBase)}${fn:escapeXml(tamperFlagMonitor.name)}</div>            
                </tags:nameValue2>
            
                <%-- enable/disable monitoring --%>
                <c:if test="${tamperFlagMonitorId > 0}">
                    <c:if test="${tamperFlagMonitor.evaluatorStatus.description=='Enabled'}"><c:set var="clazz" value="success"/></c:if>
                    <c:if test="${tamperFlagMonitor.evaluatorStatus.description=='Disabled'}"><c:set var="clazz" value="error"/></c:if>
                    <tags:nameValue2 nameKey=".label.tamperFlagMonitoring" valueClass="${clazz}">
                        ${fn:escapeXml(tamperFlagMonitor.evaluatorStatus.description)}
                    </tags:nameValue2>
                </c:if>
                
            </tags:nameValueContainer2>
            
            </tags:sectionContainer>
                
            <%-- create / update / delete --%>
            <div class="page-action-area">
            
                <c:choose>
                    <c:when test="${tamperFlagMonitorId > 0}">
                        <cti:button nameKey="save" busy="true" type="submit" classes="primary action" data-disable-group="actionButtons"/>
                        <c:set var="toggleText" value="enable"/>
                        <c:if test="${tamperFlagMonitor.evaluatorStatus eq 'ENABLED'}">
                            <c:set var="toggleText" value="disable"/>
                        </c:if>
                        <cti:button id="toggleMonitor" nameKey="${toggleText}" busy="true" 
                            data-disable-group="actionButtons"/>
                        <cti:button id="deleteButton" nameKey="delete" data-disable-group="actionButtons" 
                                    classes="delete" data-popup="#confirm-delete-monitor-popup"/>
                        <amr:confirmDeleteMonitor target="#deleteButton" monitorName="${tamperFlagMonitor.name}"/>
                        
                        <cti:url var="backUrl" value="/amr/tamperFlagProcessing/process/process">
                            <cti:param name="tamperFlagMonitorId" value="${tamperFlagMonitorId}"/>
                        </cti:url>
                    </c:when>
                    <c:otherwise>
                        <cti:button nameKey="save" type="submit" busy="true" data-disable-group="actionButtons" classes="primary action"/>
                        <cti:url var="backUrl" value="/meter/start"/>
                    </c:otherwise>
                </c:choose>
                <cti:button nameKey="cancel" href="${backUrl}" busy="true" data-disable-group="actionButtons"/>
                
            </div>
       </form:form>
<cti:includeScript link="/resources/js/pages/yukon.ami.monitor.js"/>
</cti:standardPage>
