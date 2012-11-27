<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n" %>
<%@ taglib prefix="dr" tagdir="/WEB-INF/tags/dr" %>

<cti:standardPage module="operator" page="hardwareConfig">
<cti:includeCss link="/WebConfig/yukon/styles/operator/hardware.css"/>

<c:set var="configurable" value="${hardware.hardwareType.configurable}"/>
<cti:checkRolesAndProperties value="OPERATOR_DISABLE_SWITCH_SENDING">
    <c:set var="configurable" value="false"/>
</cti:checkRolesAndProperties>

<cti:msg2 key="yukon.web.components.button.readNow.labelBusy" var="reading"/>
<cti:msg2 key="yukon.web.components.button.readNow.label" var="readNow"/>
<c:if test="${hardware.hardwareType.rf}">
    <c:set var="readPath" value="rf"/>
</c:if>
<c:if test="${hardware.hardwareType.zigbee}">
    <c:set var="readPath" value="zb"/>
</c:if>

<script type="text/javascript">
jQuery(function() {
    jQuery(document).delegate('#rfReadNow', 'click', function(event) {
        var url = '<cti:url value="/stars/operator/hardware/${readPath}/readNow"/>';
        
        jQuery("<a class=\"fl loading labeled_icon\"/>").insertBefore(jQuery("#rfReadNow span"));
        jQuery("#rfReadNow span").html('${reading}');
        jQuery("#rfReadNow span").addClass("buttonBusy");
        
        jQuery.ajax({
            url: url,
            method: 'get',
            data: {'deviceId': '${deviceId}'},
            success: function(data) {
                jQuery('#rfCommandStatus').html(data.message);
                jQuery('#rfCommandStatus').show();
                if (data.success ==  true) {
                    jQuery('#rfCommandStatus').addClass('successMessage').removeClass('errorMessage');
                    setTimeout(function() {
                        jQuery('#rfCommandStatus').fadeOut('slow', function() {
                            jQuery('#rfCommandStatus').hide();
                        });
                    }, 5000);
                } else {
                     jQuery('#rfCommandStatus').removeClass('successMessage').addClass('errorMessage');
                }
                jQuery("#rfReadNow span").html('${readNow}');
                jQuery("#rfReadNow span").removeClass("buttonBusy");
                jQuery("#rfReadNow a").detach();
            },
            error: function(data) {
                jQuery("#rfReadNow span").html('${readNow}');
                jQuery("#rfReadNow span").removeClass("buttonBusy");
                jQuery("#rfReadNow a").detach();
            }
        });
    });
});
</script>

<cti:url var="submitUrl" value="/stars/operator/hardware/config/commit"/>

<div class="threeQuarterLayout">
    <div class="primary">
        <div class="columnContent">
            <form:form id="editForm" name="editForm" action="${submitUrl}" commandName="configuration">
                <input id="actionInput" type="hidden" name="action" value=""/>
                <tags:hidden path="accountId"/>
                <tags:hidden path="inventoryId"/>
            
                <tags:formElementContainer nameKey="enrolledPrograms">
                    <c:if test="${fn:length(enrollments) == 0}">
                        <i:inline key=".noEnrolledPrograms"/>
                    </c:if>
                    <c:if test="${fn:length(enrollments) > 0}">
                        <table class="compactResultsTable sectionContainerCompactResults">
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
                                    <td>
                                        <spring:escapeBody htmlEscape="true">
                                            ${applianceCategories[assignedPrograms[enrollment.assignedProgramId].applianceCategoryId].name}
                                        </spring:escapeBody>
                                    </td>
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
                </tags:formElementContainer>
            
                <cti:checkRolesAndProperties value="OPERATOR_ALLOW_ACCOUNT_EDITING">
                    <cti:checkEnergyCompanySetting value="TRACK_HARDWARE_ADDRESSING" energyCompanyId="${energyCompanyId}">
                        <br>
                        <dr:hardwareAddressingInfo/>
                    </cti:checkEnergyCompanySetting>
                </cti:checkRolesAndProperties>
            
                <div class="pageActionArea marginBottom">
                    <cti:checkRolesAndProperties value="OPERATOR_ALLOW_ACCOUNT_EDITING">
                        <c:if test="${fn:length(enrollments) > 0}">
                            <c:if test="${configurable}">
                                <input type="submit" value="<cti:msg2 key=".config"/>"
                                       title="<cti:msg2 key=".config.description"/>"
                                       onclick="$('actionInput').value = 'config';" class="formSubmit">
                                <input type="submit" value="<cti:msg2 key=".saveToBatch"/>"
                                       title="<cti:msg2 key=".saveToBatch.description"/>"
                                       onclick="$('actionInput').value = 'saveToBatch';" class="formSubmit">
                            </c:if>
                            <input type="submit" value="<cti:msg2 key=".saveConfigOnly"/>"
                                   title="<cti:msg2 key=".saveConfigOnly.description"/>"
                                   onclick="$('actionInput').value = 'saveConfigOnly';" class="formSubmit">
                        </c:if>
                    </cti:checkRolesAndProperties>
                    <cti:url var="cancelUrl" value="/stars/operator/hardware/list">
                        <cti:param name="accountId" value="${accountId}"/>
                    </cti:url>
                </div>
            </form:form>
            
            <c:if test="${showDeviceReportedConfig}">
                <!-- Reported Addressing -->
                <tags:formElementContainer nameKey="deviceReportedConfig">
                    <div class="marginBottom">
                        <c:choose>
                            <c:when test="${not empty reportedConfig}">
                                <div class="pointStat nonStatusPointStat reportedConfig_timestamp">
                                    <i:inline key=".configReportedAt"/><cti:dataUpdaterValue type="LM_REPORTED_ADDRESS" identifier="${deviceId}/TIMESTAMP"/>
                                </div>
                                <c:if test="${hardware.hardwareType.hardwareConfigType == 'EXPRESSCOM'}">
                                    <%@ include file="xcomReportedAddress.jspf" %>
                                </c:if>
                                <c:if test="${hardware.hardwareType.hardwareConfigType == 'SEP'}">
                                    <%@ include file="sepReportedAddress.jspf" %>
                                </c:if>
                            </c:when>
                            <c:otherwise><i:inline key=".noDeviceReportedConfig"/></c:otherwise>
                        </c:choose>
                    </div>
                    <div class="cl">
                        <div class="rfCommandMsg fl">
                            <cti:button id="rfReadNow" nameKey="readNow"/>
                            <span id="rfCommandStatus" class="dn errorMessage rfCommandMsg"></span>
                        </div>
                    </div>
                </tags:formElementContainer>
            </c:if>
        </div>
    </div>
    
    <div class="secondary">
        <div class="columnContent">
            <c:if test="${configurable}">
                <tags:formElementContainer nameKey="serviceStatus">
                    <div class="wsnp marginBottom">
                        <c:choose>
                            <c:when test="${showStaticServiceStatus}">
                                <c:if test="${inService}">
                                    <i:inline key=".inService"/>
                                </c:if>
                                <c:if test="${!inService}">
                                    <i:inline key=".outOfService"/>
                                </c:if>
                            </c:when>
                            <c:otherwise>
                                <cti:pointValue pointId="${serviceStatusPointId}" cssClass="pointStat" format="VALUE" colorForStatus="true"/>
                                <cti:pointValue pointId="${serviceStatusPointId}" cssClass="pointStat nonStatusPointStat" format="DATE"/>
                            </c:otherwise>
                        </c:choose>
                    </div>
                    <ul class="labeledImageStack">
                        <li>
                            <cti:url var="enableUrl" value="/stars/operator/hardware/config/enable">
                               <cti:param name="accountId" value="${accountId}"/>
                               <cti:param name="inventoryId" value="${param.inventoryId}"/>
                            </cti:url>
                            <a class="labeled_icon enable" href="${enableUrl}"><i:inline key="yukon.web.components.button.enable.label"/></a>
                        </li>
                        
                        <li>
                            <cti:url var="disableUrl" value="/stars/operator/hardware/config/disable">
                               <cti:param name="accountId" value="${accountId}"/>
                               <cti:param name="inventoryId" value="${param.inventoryId}"/>
                            </cti:url>
                            <a class="labeled_icon disable cl" href="${disableUrl}"><i:inline key="yukon.web.components.button.disable.label"/></a>
                        </li>
                    </ul>
                </tags:formElementContainer>
            </c:if>
        </div>
    </div>

</div>

</cti:standardPage>