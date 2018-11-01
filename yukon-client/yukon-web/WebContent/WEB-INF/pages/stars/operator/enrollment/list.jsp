<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="dr" tagdir="/WEB-INF/tags/dr" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<cti:standardPage module="operator" page="enrollmentList">
<cti:checkEnergyCompanyOperator showError="true" accountId="${accountId}">
<cti:includeScript link="/resources/js/pages/yukon.assets.enroll.js"/>

<cti:msg2 var="removeTitle" key=".remove.title"/>
<cti:msg2 var="editTitle" key=".edit.title"/>
<cti:msg2 var="addTitle" key=".addEnrollmentDialogTitle"/>
<div id="enroll-popup" class="dn" 
    data-add-title="${addTitle}" 
    data-edit-title="${editTitle}" 
    data-remove-title="${removeTitle}" 
    data-account-id="${accountId}"></div>

<tags:sectionContainer2 nameKey="enrolledPrograms" styleClass="stacked">
    <c:if test="${empty enrollmentPrograms}"><span class="empty-list"><i:inline key=".noEnrolledPrograms"/></span></c:if>
    <c:if test="${not empty enrollmentPrograms}">
        <table class="compact-results-table manual-striping dashed">
            <thead>
                <tr>
                    <th class="programName"><i:inline key=".name"/></th>
                    <th class="applianceCategory"><i:inline key=".applianceCategory"/></th>
                    <cti:checkEnergyCompanySetting value="!TRACK_HARDWARE_ADDRESSING" energyCompanyId="${energyCompanyId}">
                        <th class="loadGroup"><i:inline key=".group"/></th>
                    </cti:checkEnergyCompanySetting>
                    <th class="hardware"><i:inline key=".hardware"/></th>
                    <th class="relay wsnw"><i:inline key=".relay"/></th>
                    <th class="status wsnw"><i:inline key=".status"/></th>
                    <cti:checkRolesAndProperties value="OPERATOR_ALLOW_ACCOUNT_EDITING">
                        <th></th>
                    </cti:checkRolesAndProperties>
                </tr>
            </thead>
            <tfoot></tfoot>
            <tbody>
                <c:set var="rowClass" value="alt-row"/>
                <c:forEach var="enrollmentProgram" items="${enrollmentPrograms}">
                    
                    <c:set var="programId" value="${enrollmentProgram.program.programId}"/>
                    <c:set var="rowClass" value="${empty rowClass ? 'alt-row' : ''}"/>
                    <c:set var="rows" value="0"/>
                    <c:set var="firstRow" value="true" />
                    
                    <c:forEach var="hardware" items="${enrollmentProgram.inventory}">
                        <c:if test="${hardware.enrolled}">
                            <c:set var="rows" value="${rows + 1}"/>
                        </c:if>
                    </c:forEach>
                    
                    
                    <c:forEach var="hardware" items="${enrollmentProgram.inventory}">
                        <c:if test="${hardware.enrolled}">
                            
                            <tr class="${rowClass}" data-program-id="${programId}">
                            
                                <c:if test="${firstRow == 'true'}">
                                    <td class="programName" rowspan="${rows}">
                                        ${fn:escapeXml(enrollmentProgram.program.programName)}
                                    </td>
                                    <td class="applianceCategory" rowspan="${rows}">
                                        ${fn:escapeXml(enrollmentProgram.applianceCategory.name)}
                                    </td>
                                   
                                </c:if>
                                 <cti:checkEnergyCompanySetting value="!TRACK_HARDWARE_ADDRESSING" energyCompanyId="${energyCompanyId}">
                                        <td class="loadGroup">
                                            <c:if test="${enrollmentProgram.loadGroupId != 0}">
                                                ${fn:escapeXml(loadGroupNames[hardware.loadGroupId])}
                                            </c:if>
                                        </td>
                                    </cti:checkEnergyCompanySetting>
                                <td class="hardware">${fn:escapeXml(hardware.displayName)}</td>
                                <td class="relay wsnw">
                                    <c:set var="relayStr" value="${hardware.relay}"/>
                                    <c:if test="${hardware.relay == 0}"><cti:msg2 var="relayStr" key=".noRelay"/></c:if>
                                    ${relayStr}
                                </td>
                                <td class="status wsnw">
                                    <i:inline key="${hardware.inService.shortFormatKey}"/>
                                </td>
                                <c:if test="${firstRow == 'true'}">
                                    <cti:checkRolesAndProperties value="OPERATOR_ALLOW_ACCOUNT_EDITING">
                                        <td class="tar" rowspan="${rows}">
                                            <cti:icon icon="icon-pencil" classes="fn M0 js-edit-enrollment cp"/>
                                            <c:set var="enrollmentClass" value="${enrollmentProgram.isNest() ? 'disabled' : 'js-unenroll cp'}"/>
                                            <c:if test="${enrollmentProgram.isNest()}">
                                                <cti:msg2 var="titleMessage" key="yukon.web.modules.dr.nest.enrollmentDisabled"/>
                                            </c:if>
                                            <cti:icon icon="icon-cross" classes="fn M0 ${enrollmentClass}" title="${titleMessage}"/>
                                        </td>
                                    </cti:checkRolesAndProperties>
                                </c:if>
                            </tr>
                            <c:set var="firstRow" value="false" />
                        </c:if>
                    </c:forEach>
                </c:forEach>
            </tbody>
        </table>
    </c:if>

    <cti:checkRolesAndProperties value="OPERATOR_ALLOW_ACCOUNT_EDITING">
        <div class="action-area">
            <tags:pickerDialog id="programPicker"
                type="assignedProgramPicker" 
                memoryGroup="programPicker"
                destinationFieldName="assignedProgramId"
                endAction="yukon.assets.enroll.add"
                immediateSelectMode="true" 
                extraArgs="${energyCompanyId}"
                linkType="button" 
                nameKey="add" 
                icon="icon-add" 
               />
        </div>
    </cti:checkRolesAndProperties>
</tags:sectionContainer2>

<dr:enrollmentHistory hardwareConfigActions="${hardwareConfigActions}"/>
</cti:checkEnergyCompanyOperator>
</cti:standardPage>