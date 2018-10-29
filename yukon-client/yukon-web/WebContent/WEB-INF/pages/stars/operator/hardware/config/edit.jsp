<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n" %>
<%@ taglib prefix="dr" tagdir="/WEB-INF/tags/dr" %>

<cti:standardPage module="operator" page="hardwareConfig">
<cti:checkEnergyCompanyOperator showError="true" accountId="${accountId}">
<c:set var="configurable" value="${hardware.hardwareType.configurable}"/>
<cti:checkRolesAndProperties value="OPERATOR_DISABLE_SWITCH_SENDING">
    <c:set var="configurable" value="false"/>
</cti:checkRolesAndProperties>

<script type="text/javascript">
$(function() {
    $(document).on('click', '#rf-read-now', function(event) {
        var url = '<cti:url value="/stars/operator/hardware/readNow"/>';
        
        $.ajax({
            url: url,
            method: 'get',
            data: {'deviceId': '${deviceId}'}
        }).done(function(data) {
            $('#rf-command-status').html(data.message);
            $('#rf-command-status').show();
            if (data.success ==  true) {
                $('#rf-command-status').addClass('success').removeClass('error');
                setTimeout(function() {
                    $('#rf-command-status').fadeOut('slow', function() {
                        $('#rf-command-status').hide();
                    });
                }, 5000);
            } else {
                 $('#rf-command-status').removeClass('success').addClass('error');
            }
        }).always(function(data) {
            yukon.ui.unbusy($("#rf-read-now"));
        });
    });
});
updateSub = function (data) {
    if (data.value == -1) {
        $('.js-non-readable-value').show();
        $('.js-readable-value').hide();
    } else {
        $('.js-readable-value').html(data.value);
        $('.js-readable-value').show();
        $('.js-non-readable-value').hide();
    }
} 
</script>

<cti:url var="submitUrl" value="/stars/operator/hardware/config/commit"/>

<c:if test="${hardware.hardwareType.isNest()}">
    <tags:alertBox key="yukon.web.modules.dr.nest.changeGroupMessage" type="info"/>
</c:if>

<form:form id="editForm" name="editForm" action="${submitUrl}" modelAttribute="configuration">
    
    <div class="column-16-8 clearfix stacked">
        <div class="column one">
        
            <cti:csrfToken/>
            <input id="actionInput" type="hidden" name="action" value=""/>
            <tags:hidden path="accountId"/>
            <tags:hidden path="inventoryId"/>
        
            <tags:sectionContainer2 nameKey="enrolledPrograms">
                <c:if test="${fn:length(enrollments) == 0}">
                    <i:inline key=".noEnrolledPrograms"/>
                </c:if>
                <c:if test="${fn:length(enrollments) > 0}">
                    <table class="compact-results-table with-form-controls">
                        <tr>
                            <th><i:inline key=".name"/></th>
                            <th><i:inline key=".applianceCategory"/></th>
                            <cti:checkEnergyCompanySetting value="!TRACK_HARDWARE_ADDRESSING" energyCompanyId="${energyCompanyId}">
                                <th><i:inline key=".group"/></th>
                            </cti:checkEnergyCompanySetting>
                            <th><i:inline key=".relay"/></th>
                        </tr>
            
                        <c:forEach var="enrollment" varStatus="status" items="${enrollments}">
                            <tags:hidden path="programEnrollments[${status.index}].assignedProgramId"/>
                            <c:set var="programId" value="${enrollment.assignedProgramId}"/>
                            <c:choose>
                                <c:when test="${status.last}"><c:set var="trClass" value="last"/></c:when>
                                <c:otherwise><c:set var="trClass" value="middle"/></c:otherwise>
                            </c:choose>
                            <tr class="${trClass}">
                                <td>
                                    <c:set var="assignedProgram" value="${assignedPrograms[enrollment.assignedProgramId]}"/>
                                    <dr:assignedProgramName assignedProgram="${assignedPrograms[enrollment.assignedProgramId]}"/>
                                </td>
                                <td>${fn:escapeXml(applianceCategories[assignedPrograms[enrollment.assignedProgramId].applianceCategoryId].name)}</td>
                                <cti:checkEnergyCompanySetting value="!TRACK_HARDWARE_ADDRESSING" energyCompanyId="${energyCompanyId}">
                                    <td>
                                        <c:set var="loadGroups" value="${loadGroupsByProgramId[programId]}"/>
                                        <c:if test="${fn:length(loadGroups) == 0}">
                                            <i:inline key=".groupNotApplicable"/>
                                        </c:if>
                                        <c:if test="${fn:length(loadGroups) > 0}">
                                            <tags:selectWithItems path="programEnrollments[${status.index}].loadGroupId"
                                                items="${loadGroups}" 
                                                itemLabel="name"
                                                itemValue="loadGroupId"/>
                                        </c:if>
                                    </td>
                                </cti:checkEnergyCompanySetting>
                                <td>
                                    <form:select path="programEnrollments[${status.index}].relay">
                                        <form:option value="0"><i:inline key=".noRelay"/></form:option>
                                        <c:forEach var="relayNumber" begin="1" end="${hardware.numRelays}">
                                            <form:option value="${relayNumber}">${relayNumber}</form:option>
                                        </c:forEach>
                                    </form:select>
                                </td>
                        </c:forEach>
                    </table>
                </c:if>
            </tags:sectionContainer2>
        </div>
        <c:if test="${canEnableDisable}">
            <div class="column two nogutter">
                <c:if test="${configurable}">
                    <tags:sectionContainer2 nameKey="serviceStatus">
                        <div class="stacked">
                            <c:choose>
                                <c:when test="${showStaticServiceStatus}">
                                    <i:inline key="${inService}"/>
                                </c:when>
                                <c:otherwise>
                                    <cti:pointValue pointId="${serviceStatusPointId}" format="VALUE" colorForStatus="true"/>
                                    <cti:pointValue pointId="${serviceStatusPointId}" format="DATE"/>
                                </c:otherwise>
                            </c:choose>
                        </div>
                        <div class="action-area">
                            <cti:url var="enableUrl" value="/stars/operator/hardware/config/enable">
                               <cti:param name="accountId" value="${accountId}"/>
                               <cti:param name="inventoryId" value="${param.inventoryId}"/>
                            </cti:url>
                            <cti:button href="${enableUrl}" icon="icon-accept" nameKey="enable"/>
                            <cti:url var="disableUrl" value="/stars/operator/hardware/config/disable">
                               <cti:param name="accountId" value="${accountId}"/>
                               <cti:param name="inventoryId" value="${param.inventoryId}"/>
                            </cti:url>
                            <cti:button href="${disableUrl}" icon="icon-delete" nameKey="disable"/>
                        </div>
                    </tags:sectionContainer2>
                </c:if>
            </div>
        </c:if>
    </div>
    
    <cti:checkRolesAndProperties value="OPERATOR_ALLOW_ACCOUNT_EDITING">
        <cti:checkEnergyCompanySetting value="TRACK_HARDWARE_ADDRESSING" energyCompanyId="${energyCompanyId}">
        	<c:if test="${!hideHardwareAddressing}">
               <dr:hardwareAddressingInfo type="${hardware.hardwareType.hardwareConfigType}"/>
            </c:if>
        </cti:checkEnergyCompanySetting>
    </cti:checkRolesAndProperties>
    
    <div class="page-action-area stacked-md">
        <cti:checkRolesAndProperties value="OPERATOR_ALLOW_ACCOUNT_EDITING">
            <c:choose>
                <c:when test="${fn:length(enrollments) > 0}">
                    <c:if test="${configurable}">
                        <cti:msg2 key=".config.description" var="configTitle"/>
                        <cti:button type="submit" nameKey="config" onclick="$('#actionInput').val('config');" title="${configTitle}" classes="action primary"/>
                        <c:if test="${!hardware.hardwareType.isNest()}">
                            <cti:msg2 key=".saveToBatch.description" var="saveToBatchTitle"/>
                            <cti:button type="submit" nameKey="saveToBatch" onclick="$('#actionInput').val('saveToBatch');" title="${saveToBatchTitle}"/>
                        </c:if>
                    </c:if>
                    <c:if test="${!hardware.hardwareType.isNest()}">
                        <cti:msg2 key=".saveConfigOnly.description" var="saveConfigOnlyTitle"/>
                        <cti:button type="submit" nameKey="saveConfigOnly" onclick="$('#actionInput').val('saveConfigOnly');" title="${saveConfigOnlyTitle}"/>
                    </c:if>
                </c:when>
                <c:otherwise>
                    <c:if test="${showSaveOnly}">
                        <cti:msg2 key=".saveConfigOnly.description" var="saveConfigOnlyTitle"/>
                        <cti:button type="submit" nameKey="saveConfigOnly" onclick="$('#actionInput').val('saveConfigOnly');" title="${saveConfigOnlyTitle}"/>
                    </c:if>
                </c:otherwise>
            </c:choose>
        </cti:checkRolesAndProperties>
        <cti:url var="cancelUrl" value="/stars/operator/hardware/list">
            <cti:param name="accountId" value="${accountId}"/>
        </cti:url>
    </div>
</form:form>
        
<c:if test="${showDeviceReportedConfig}">
    <!-- Reported Addressing -->
    <tags:sectionContainer2 nameKey="deviceReportedConfig">
        <div class="stacked">
            <c:choose>
                <c:when test="${not empty reportedConfig}">
                    <em class="fr">
                        <i:inline key=".configReportedAt"/>
                        <cti:dataUpdaterValue type="LM_REPORTED_ADDRESS" identifier="${deviceId}/TIMESTAMP"/>
                    </em>
                    <cti:msgScope paths=",yukon.dr.config">
                        <c:if test="${hardware.hardwareType.hardwareConfigType == 'EXPRESSCOM'}">
                            <%@ include file="xcomReportedAddress.jspf" %>
                        </c:if>
                        <c:if test="${hardware.hardwareType.hardwareConfigType == 'SEP'}">
                            <%@ include file="sepReportedAddress.jspf" %>
                        </c:if>
                    </cti:msgScope>
                </c:when>
                <c:otherwise><i:inline key=".noDeviceReportedConfig"/></c:otherwise>
            </c:choose>
        </div>
        <div class="page-action-area">
            <div class="fl">
                <cti:button id="rf-read-now" nameKey="readNow" busy="true" icon="icon-read"/>
                <span id="rf-command-status" class="dn error"></span>
            </div>
        </div>
    </tags:sectionContainer2>
</c:if>
</cti:checkEnergyCompanyOperator>
</cti:standardPage>