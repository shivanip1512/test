<%@ taglib prefix="amr" tagdir="/WEB-INF/tags/amr" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="d" tagdir="/WEB-INF/tags/dialog" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<cti:url value="/amr/tamperFlagProcessing/delete" var="monitorDeleteURL"/>
<cti:url value="/amr/tamperFlagProcessing/toggleEnabled" var="toggleEnabledURL"/>
<cti:url value="/amr/tamperFlagProcessing/update" var="updateURL"/>

<cti:standardPage module="amr" page="tamperFlagEditor.${mode}">

<script type="text/javascript">

    $(function() {
        $(document).on('yukon.dialog.confirm.cancel', function(ev) {
            yukon.ui.unbusy('#deleteButton');
            $('.page-action-area .button').enable();
        });
    });
    
    function deleteTamperFlagMonitor() {
        $("button[data-disable-group=actionButtons]").each( function(){
            this.disabled = true;
        });
        $('#monitorDeleteForm').submit();
    }

    function rewriteTamperFlagGroupName(textEl) {
        $('#tamperFlagGroupNameDiv').text('${tamperFlagGroupBase}' + textEl.value);
    }
    
</script>

        <c:if test="${not empty editError}">
            <div class="error">${editError}</div>
        </c:if>
    
        <%-- MISC FORMS --%>
        <form id="monitorDeleteForm" action="${monitorDeleteURL}" method="post">
            <cti:csrfToken/>
            <input type="hidden" id="deleteTamperFlagMonitorId" name="deleteTamperFlagMonitorId" value="${tamperFlagMonitorId}">
        </form>
        
        <form id="toggleEnabledForm" action="${toggleEnabledURL}" method="post">
            <cti:csrfToken/>
            <input type="hidden" name="tamperFlagMonitorId" value="${tamperFlagMonitorId}">
        </form>
        
        <%-- UPDATE FORM --%>
        <form id="updateForm" action="${updateURL}" method="post">
            <cti:csrfToken/>        
            <input type="hidden" name="tamperFlagMonitorId" value="${tamperFlagMonitorId}">
            
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
                    <input type="text" name="name" size="50" value="${name}" onkeyup="rewriteTamperFlagGroupName(this);" onchange="rewriteTamperFlagGroupName(this);">
                </tags:nameValue2>
                
                <%-- device group --%>
                <tags:nameValue2 nameKey=".label.deviceGroup">
                    
                    <cti:deviceGroupHierarchyJson predicates="NON_HIDDEN" var="groupDataJson"/>
                    <tags:deviceGroupNameSelector fieldName="deviceGroupName" 
                                                  fieldValue="${deviceGroupName}" 
                                                  dataJson="${groupDataJson}"
                                                  linkGroupName="true"/>
                    <cti:msg2 var="deviceGroupText" key=".label.deviceGroup"/>
                    <tags:helpInfoPopup title="${deviceGroupText}">
                        <cti:msg2 key="yukon.web.modules.amr.tamperFlagEditor.popupInfo.deviceGroup"/>
                    </tags:helpInfoPopup>
                    
                </tags:nameValue2>
            
                <%-- tamper flag group --%>
                <tags:nameValue2 nameKey=".label.tamperFlagGroup">
                    <div id="tamperFlagGroupNameDiv">${fn:escapeXml(tamperFlagGroupBase)}${fn:escapeXml(name)}</div>            
                </tags:nameValue2>
            
                <%-- enable/disable monitoring --%>
                <c:if test="${tamperFlagMonitorId > 0}">
                    <tags:nameValue2 nameKey=".label.tamperFlagMonitoring">
                        ${fn:escapeXml(tamperFlagMonitor.evaluatorStatus.description)}
                    </tags:nameValue2>
                </c:if>
                
            </tags:nameValueContainer2>
            
            </tags:sectionContainer>
                
            <%-- create / update / delete --%>
            <div class="page-action-area">
            
                <c:choose>
                    <c:when test="${tamperFlagMonitorId > 0}">
                        <cti:button nameKey="update" busy="true" type="submit" classes="primary action" data-disable-group="actionButtons"/>
                        <c:set var="toggleText" value="enable"/>
                        <c:if test="${tamperFlagMonitor.evaluatorStatus eq 'ENABLED'}">
                            <c:set var="toggleText" value="disable"/>
                        </c:if>
                        <cti:button nameKey="${toggleText}" onclick="$('#toggleEnabledForm').submit();" busy="true" 
                            data-disable-group="actionButtons"/>
                        <cti:button id="deleteButton" nameKey="delete" 
                            onclick="deleteTamperFlagMonitor(${tamperFlagMonitorId});" busy="true"
                            data-disable-group="actionButtons" classes="delete"/>
                        <d:confirm on="#deleteButton" nameKey="confirmDelete"/>
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
        </form>
</cti:standardPage>
