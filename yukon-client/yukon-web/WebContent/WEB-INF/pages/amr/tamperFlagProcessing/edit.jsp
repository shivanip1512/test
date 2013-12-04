<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti"%>
<%@ taglib prefix="amr" tagdir="/WEB-INF/tags/amr"%>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags"%>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n"%>
<%@ taglib prefix="d" tagdir="/WEB-INF/tags/dialog"%>

<cti:standardPage module="amr" page="tamperFlagEditor.${mode}">

<script type="text/javascript">

    function deleteTamperFlagMonitor() {
        jQuery("button[data-disable-group=actionButtons]").each( function(){
            this.disabled = true;
        });
        jQuery('#monitorDeleteForm').submit();
    }

    function rewriteTamperFlagGroupName(textEl) {
        jQuery('#tamperFlagGroupNameDiv').html('${tamperFlagGroupBase}' + textEl.value);
    }
    
</script>

        <c:if test="${not empty editError}">
            <div class="error">${editError}</div>
        </c:if>
    
        <%-- MISC FORMS --%>
        <form id="monitorDeleteForm" action="/amr/tamperFlagProcessing/delete" method="post">
            <input type="hidden" id="deleteTamperFlagMonitorId" name="deleteTamperFlagMonitorId" value="${tamperFlagMonitorId}">
        </form>
        
        <form id="toggleEnabledForm" action="/amr/tamperFlagProcessing/toggleEnabled" method="post">
            <input type="hidden" name="tamperFlagMonitorId" value="${tamperFlagMonitorId}">
        </form>
        
        <%-- UPDATE FORM --%>
        <form id="updateForm" action="/amr/tamperFlagProcessing/update" method="post">
        
            <input type="hidden" name="tamperFlagMonitorId" value="${tamperFlagMonitorId}">
            
            <cti:msg2 var="setupSectionText" key=".section.setup" />
            <cti:msg2 var="editSetupSectionText" key=".section.editSetup" />  
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
                    
                    <cti:deviceGroupHierarchyJson predicates="NON_HIDDEN" var="groupDataJson" />
                    <tags:deviceGroupNameSelector fieldName="deviceGroupName" 
                                                    fieldValue="${deviceGroupName}" 
                                                  dataJson="${groupDataJson}"
                                                  linkGroupName="true"/>
                                                        
                    <tags:helpInfoPopup title="${deviceGroupText}">
                        <cti:msg2 key="yukon.web.modules.amr.tamperFlagEditor.popupInfo.deviceGroup"/>
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
            <div class="page-action-area">
            
                <c:choose>
                    <c:when test="${tamperFlagMonitorId > 0}">
                        <cti:button nameKey="update" busy="true" type="submit" classes="primary action" data-disable-group="actionButtons" />
                        <c:set var="toggleText" value="enable"/>
                        <c:if test="${tamperFlagMonitor.evaluatorStatus eq 'ENABLED'}">
                            <c:set var="toggleText" value="disable"/>
                        </c:if>
                        <cti:button nameKey="${toggleText}" onclick="jQuery('#toggleEnabledForm').submit();" busy="true" data-disable-group="actionButtons"/>
                        <cti:button id="deleteButton" type="button" nameKey="delete" onclick="deleteTamperFlagMonitor(${tamperFlagMonitorId});" busy="true" data-disable-group="actionButtons" classes="delete"/>
                        <d:confirm on="#deleteButton" nameKey="confirmDelete" />
                        <cti:url var="backUrl" value="/amr/tamperFlagProcessing/process/process">
                            <cti:param name="tamperFlagMonitorId" value="${tamperFlagMonitorId}" />
                        </cti:url>
                    </c:when>
                    <c:otherwise>
                        <cti:button nameKey="save" type="submit" busy="true" data-disable-group="actionButtons" />
                        <cti:url var="backUrl" value="/meter/start" />
                    </c:otherwise>
                </c:choose>
                <cti:button nameKey="cancel" type="button" href="${backUrl}" busy="true" data-disable-group="actionButtons" />
                
            </div>
        </form>
</cti:standardPage>
