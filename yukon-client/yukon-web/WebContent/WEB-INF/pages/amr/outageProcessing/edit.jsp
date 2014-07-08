<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti"%>
<%@ taglib prefix="amr" tagdir="/WEB-INF/tags/amr"%>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags"%>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n"%>
<%@ taglib prefix="d" tagdir="/WEB-INF/tags/dialog"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<cti:url value="/amr/outageProcessing/monitorEditor/delete" var="deleteURL"/>
<cti:url value="/amr/outageProcessing/monitorEditor/toggleEnabled" var="toggleEnabledURL"/>
<cti:url value="/amr/outageProcessing/monitorEditor/update" var="updateURL"/>

<cti:standardPage module="amr" page="outageMonitorConfig.${mode}">

    
    <script type="text/javascript">
    
        $(function() {
            toggleReadFrequencyOptions();
            $(document).on('yukon.dialog.confirm.cancel', function(ev) {
                yukon.ui.unbusy('#deleteButton');
                $('.page-action-area .button').enable();
            });
        });
    
        function toggleReadFrequencyOptions() {
            if (${outageMonitorId} == 0) {
                if ($('#scheduleGroupCommand').is(':checked')) {
                    $('#scheduleNameTr').show();
                    $('#readFrequencyTr').show();
                } else {
                    $('#scheduleNameTr').hide();
                    $('#readFrequencyTr').hide();
                }
            }
        }

        function deleteOutageMonitor() {
            $('#configDeleteForm').submit();
        }

        function rewriteOutageGroupName(textEl) {
            $('#outageGroupNameDiv').innerHTML = '${outageGroupBase}' + textEl.value;
        }
        
    </script>
    
    <c:if test="${not empty editError}">
        <div class="error">${editError}</div>
    </c:if>
    
    <c:if test="${saveOk}">
        <div class="fwb"><i:inline key=".saveOk"/></div>
    </c:if>

    <%-- MISC FORMS --%>
    <form id="configDeleteForm" action="${deleteURL}" method="post">
        <cti:csrfToken/>
        <input type="hidden" name="deleteOutageMonitorId" value="${outageMonitorId}">
    </form>
    
    <form id="toggleEnabledForm" action="${toggleEnabledURL}" method="post">
        <cti:csrfToken/>
        <input type="hidden" name="outageMonitorId" value="${outageMonitorId}">
    </form>
        
        <%-- UPDATE FORM --%>
    <form id="updateForm" action="${updateURL}" method="post">
        <cti:csrfToken/>
        <input type="hidden" name="outageMonitorId" value="${outageMonitorId}">
        
        <cti:msg2 var="setupSectiontext" key=".section.setup" />
        <cti:msg2 var="editSetupSectionText" key=".section.editSetup" />
        <c:set var="setupSectionTitle" value="${setupSectiontext}"/>
        <c:if test="${outageMonitorId > 0}">
            <c:set var="setupSectionTitle" value="${editSetupSectionText}"/>
        </c:if>
        
        <tags:sectionContainer title="${setupSectionTitle}">
        
            <tags:nameValueContainer2>
            
                <%-- name --%>
                <tags:nameValue2 nameKey=".label.name">
                    <input type="text" name="name" size="50" value="${name}" onkeyup="rewriteOutageGroupName(this);" onchange="rewriteOutageGroupName(this);">
                </tags:nameValue2>
                
                <%-- device group --%>
                <tags:nameValue2 nameKey=".label.deviceGroup">
                    
                    <cti:deviceGroupHierarchyJson predicates="NON_HIDDEN" var="groupDataJson" />
                    <tags:deviceGroupNameSelector fieldName="deviceGroupName" 
                                                  fieldValue="${deviceGroupName}" 
                                                  dataJson="${groupDataJson}"
                                                  linkGroupName="true"/>
                    <cti:msg2 var="deviceGroupText" key=".label.deviceGroup"/>
                    <tags:helpInfoPopup title="${deviceGroupText}" >
                        <cti:msg2 key=".popupInfo.deviceGroup"/>
                    </tags:helpInfoPopup>
                    
                </tags:nameValue2>
            
                <%-- outages group --%>
                <tags:nameValue2 nameKey=".label.outagesGroup">
                    <div id="outageGroupNameDiv">${fn:escapeXml(outageGroupBase)}${fn:escapeXml(name)}</div>
                </tags:nameValue2>
            
                <%-- number of outages --%>
                <tags:nameValue2 nameKey=".label.numberOfOutages">
                    
                    <input type="text" name="numberOfOutages" maxlength="3" size="3" style="text-align:right;" value="${numberOfOutages}"> 
                    <i:inline key=".label.numberOfOutagesOutages"/>
                    <cti:msg2 var="numberOfOutagesText" key=".label.numberOfOutages"/>
                    <tags:helpInfoPopup title="${numberOfOutagesText}">
                        <cti:msg2 key=".popupInfo.numberOfOutages"/>
                    </tags:helpInfoPopup>
                    
                </tags:nameValue2>
                
                <%-- time period --%>
                <tags:nameValue2 nameKey=".label.timePeriod">
                        
                    <input type="text" name="timePeriod" maxlength="3" size="3" style="text-align:right;" value="${timePeriod}">
                    <i:inline key=".label.timePeriodDays"/>
                    <cti:msg2 var="timePeriodText" key=".label.timePeriod"/>
                    <tags:helpInfoPopup title="${timePeriodText}">
                        <cti:msg2 key=".popupInfo.timePeriod"/>
                    </tags:helpInfoPopup>
                    
                </tags:nameValue2>
                
                <%-- enable/disable monitoring --%>
                <c:if test="${outageMonitorId > 0}">
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
        <c:if test="${outageMonitorId == 0}">
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
                
                <input type="checkbox" id="scheduleGroupCommand" name="scheduleGroupCommand" onclick="toggleReadFrequencyOptions();" <c:if test="${scheduleGroupCommand}">checked</c:if>> 
                <i:inline key=".label.scheduleReadDescription"/>
                
                <cti:msg2 var="scheduleReadText" key=".label.scheduleRead"/>
                <tags:helpInfoPopup title="${scheduleReadText}">
                    <cti:msg2 key=".popupInfo.scheduleRead"/>
                </tags:helpInfoPopup>
                    
                <%-- schedule name --%>
                <cti:msg2 var="scheduleNameText" key=".label.scheduleName"/>
                <tags:nameValue name="${fn:escapeXml(scheduleNameText)}" id="scheduleNameTr" nameColumnWidth="250px">
                     <input type="text" name="scheduleName" value="${scheduleName}">
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
                <c:when test="${outageMonitorId > 0}">
                    <cti:button nameKey="update" busy="true" type="submit" classes="primary action" data-disable-group="actionButtons" />
                    <c:set var="toggleText" value="enable"/>
                    <c:if test="${outageMonitor.evaluatorStatus eq 'ENABLED'}">
                        <c:set var="toggleText" value="disable"/>
                    </c:if>
                    <cti:button nameKey="${toggleText}" onclick="$('#toggleEnabledForm').submit();" busy="true" data-disable-group="actionButtons"/>
                    <cti:button id="deleteButton" nameKey="delete" onclick="deleteOutageMonitor();" busy="true" data-disable-group="actionButtons" classes="delete"/>
                    <d:confirm on="#deleteButton" nameKey="confirmDelete" />
                    <cti:url var="backUrl" value="/amr/outageProcessing/process/process">
                        <cti:param name="outageMonitorId" value="${outageMonitorId}" />
                    </cti:url>
                </c:when>
                <c:otherwise>
                    <cti:button nameKey="save" type="submit" busy="true" data-disable-group="actionButtons" classes="primary action"/>
                    <cti:url var="backUrl" value="/meter/start"/>
                </c:otherwise>
            </c:choose>
            <cti:button nameKey="cancel" href="${backUrl}" busy="true" data-disable-group="actionButtons" />
        </div>
    </form>
</cti:standardPage>