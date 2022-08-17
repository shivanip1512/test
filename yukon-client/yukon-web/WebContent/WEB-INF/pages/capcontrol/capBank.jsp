<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti"%>
<%@ taglib prefix="d" tagdir="/WEB-INF/tags/dialog" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n"%>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags"%>
<%@ taglib prefix="dt" tagdir="/WEB-INF/tags/dateTime"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>

<c:set var="pageName" value="capbank.${mode}"/>
<c:if test="${orphan}">
    <c:set var="pageName" value="capbank.orphan.${mode}"/>
</c:if>

<cti:standardPage module="capcontrol" page="${pageName}">

<%@ include file="/capcontrol/capcontrolHeader.jspf" %>

    <tags:setFormEditMode mode="${mode}" />
    
<cti:checkRolesAndProperties value="CAPBANK_COMMANDS_AND_ACTIONS" level="ALL_DEVICE_COMMANDS_WITH_YUKON_ACTIONS,
    ALL_DEVICE_COMMANDS_WITHOUT_YUKON_ACTIONS,NONOPERATIONAL_COMMANDS_WITH_YUKON_ACTIONS,
    NONOPERATIONAL_COMMANDS_WITHOUT_YUKON_ACTIONS,YUKON_ACTIONS_ONLY">
    <c:set var="hasCapBankCommandsAndActionsAccess" value="true"/>
</cti:checkRolesAndProperties>

<c:if test="${hasCapBankCommandsAndActionsAccess}">
    <script type="text/javascript">
        addCommandMenuBehavior('a[id^="capbankState_"]');
    </script>
</c:if>

    <div class="js-page-additional-actions dn">
        <cti:displayForPageEditModes modes="VIEW,EDIT">
            <cti:checkRolesAndProperties value="SYSTEM_WIDE_CONTROLS">
                <cti:checkRolesAndProperties value="CBC_DATABASE_EDIT">
                    <li class="divider" />
                </cti:checkRolesAndProperties>
            </cti:checkRolesAndProperties>

            <c:if test="${!orphan}">
                <c:if test="${hasCapBankCommandsAndActionsAccess}">
                    <cm:dropdownOption linkId="capbankState_${capbank.id}" key=".substation.capBank.actions" icon="icon-cog" href="javascript:void(0);" />
                </c:if>
            </c:if>

            <cti:checkRolesAndProperties value="CBC_DATABASE_EDIT">
                <cm:dropdownOption key=".edit.points" icon="icon-add-remove" data-popup=".js-edit-points-popup" />
            </cti:checkRolesAndProperties>
        </cti:displayForPageEditModes>

        <cti:displayForPageEditModes modes="VIEW">
                <cti:checkRolesAndProperties value="CBC_DATABASE_EDIT">
                    <cti:url var="editUrl" value="/capcontrol/capbanks/${capbank.id}/edit" />
                    <cm:dropdownOption  key="components.button.edit.label" icon="icon-pencil" href="${editUrl}" />
                </cti:checkRolesAndProperties>
        </cti:displayForPageEditModes>
        <c:if test="${hasCapBankCommandsAndActionsAccess}">
            <li class="divider" />
        </c:if>
        
        <cm:dropdownOption classes="js-show-comments" key=".menu.viewComments" icon="icon-comment" data-pao-id="${capbank.id}" 
            data-pao-name="${fn:escapeXml(capbank.name)}"/>
        <cti:url var="recentEventsUrl" value="/capcontrol/search/recentEvents?value=${capbank.id}" />
        <cm:dropdownOption href="${recentEventsUrl}" key=".menu.viewRecentEvents" icon="icon-calendar-view-month"/>
    </div>

    <cti:url var="action" value="/capcontrol/capbanks" />
    <cti:displayForPageEditModes modes="CREATE">
    <c:if test="${not empty parent}">
        <cti:url var="action" value="/capcontrol/capbanks?parentId=${parent.liteID}"/>
    </c:if>
</cti:displayForPageEditModes>
    <form:form id="capbank-edit-form" modelAttribute="capbank" action="${action}" method="POST">
        <cti:csrfToken />
        <form:hidden path="id" />
        <div class="column-14-10 clearfix">
            <div class="column one">
                <cti:tabs containerName="capbank_Tab1">
                    <cti:msg2 var="infoTab" key=".infoTab"/>
                    <cti:tab title="${infoTab}">
                        <tags:nameValueContainer2 tableClass="natural-width">
                            <tags:nameValue2 nameKey=".name">
                                <tags:input path="name" size="25" maxlength="60" autofocus="autofocus"/>
                            </tags:nameValue2>
                            <tags:nameValue2 nameKey=".status">
                                <tags:switchButton path="disabled" inverse="${true}"
                                    offNameKey=".disabled.label" onNameKey=".enabled.label" />
                            </tags:nameValue2>
                        </tags:nameValueContainer2>
                            
                        <cti:displayForPageEditModes modes="CREATE">
                           <tags:nameValueContainer2 tableClass="natural-width">
                               <tags:nameValue2 nameKey=".parent">
                                    <c:if test="${empty parent }">
                                        <span class="empty-list"><i:inline key="yukon.common.none"/></span>
                                    </c:if>
                                    <c:if test="${not empty parent}">
                                        <cti:url var="editParent" value="/capcontrol/feeders/${parent.liteID}"/>
                                            <a href="${editParent}">${fn:escapeXml(parent.paoName)}</a>
                                    </c:if>
                                </tags:nameValue2>
                            </tags:nameValueContainer2>
                            <tags:sectionContainer2 nameKey="cbcControllerSection">
                                <tags:nameValueContainer2 tableClass="natural-width">
                                    <tags:nameValue2 nameKey=".createCBC">
                                        <tags:switchButton path="createCBC" classes="js-create-cbc"
                                            inputClass="js-createCBC" offNameKey=".no.label"
                                            onNameKey=".yes.label" toggleGroup="newCBC"
                                            toggleAction="hide" />
                                    </tags:nameValue2>
                                    <tags:nameValue2 nameKey=".cbcControllerName" data-toggle-group="newCBC">
                                        <tags:input path="cbcControllerName" size="25" maxlength="60"
                                            inputClass="js-cbcControllerName" />
                                    </tags:nameValue2>
                                    <tags:nameValue2 nameKey=".cbcType" data-toggle-group="newCBC">
                                        <tags:selectWithItems id="pao-type" items="${cbcTypes}" path="cbcType"/>
                                    </tags:nameValue2>
                                    <tags:nameValue2 nameKey=".cbcCommChannel" data-toggle-group="newCBC">
                                        <tags:selectWithItems
                                            id="comm-port"
                                            path="cbcCommChannel"
                                            disabled="true"
                                            items="${availablePorts}"
                                            itemValue="liteID" itemLabel="paoName"
                                            inputClass="with-option-hiding" />                                    
                                    </tags:nameValue2>
                                    <tags:nameValue2 nameKey=".cbc.parentRtu" rowClass="js-logical-cbc dn">
                                        <spring:bind path="parentRtuId">
                                            <form:hidden id="parent-rtu-input" path="parentRtuId" />
                                            <tags:pickerDialog id="rtuDnpPicker" type="dnpRtuPicker"
                                                linkType="selectionLabel" selectionProperty="paoName"
                                                destinationFieldId="parent-rtu-input"/>
                                            <c:if test="${status.error}"><br><form:errors path="parentRtuId" cssClass="error"/></c:if>
                                        </spring:bind>
                                    </tags:nameValue2>
                                </tags:nameValueContainer2>
                            </tags:sectionContainer2>
                        </cti:displayForPageEditModes>
                            
                        <cti:displayForPageEditModes modes="EDIT,VIEW">   
                            <tags:nameValueContainer2 tableClass="natural-width">
                                <tags:nameValue2 nameKey=".address">
                                    <tags:input path="location" maxlength="60"/>
                                </tags:nameValue2>
                                <tags:nameValue2 nameKey=".mapLocationID">
                                    <tags:input path="CapBank.mapLocationID" maxlength="64"/>
                                </tags:nameValue2>
                                <tags:nameValue2 nameKey=".parent">
                                    <c:if test="${empty parent }">
                                        <span class="empty-list"><i:inline key="yukon.common.none"/></span>
                                    </c:if>
                                    <c:if test="${not empty parent}">
                                        <cti:url var="editParent" value="/capcontrol/feeders/${parent.liteID}"/>
                                            <a href="${editParent}">${fn:escapeXml(parent.paoName)}</a>
                                    </c:if>
                                </tags:nameValue2>
                                <tags:yukonListEntrySelectNameValue nameKey=".switchManufacturer" path="CapBank.switchManufacture" energyCompanyId="${energyCompanyId}" listName="SWITCH_MANUFACTURER" useTextAsValue="${true}"/>
                                <tags:yukonListEntrySelectNameValue nameKey=".controllerType" path="CapBank.controllerType" energyCompanyId="${energyCompanyId}" listName="CONTROLLER_TYPE" useTextAsValue="${true}"/>
                                <tags:yukonListEntrySelectNameValue nameKey=".switchType" path="CapBank.typeOfSwitch" energyCompanyId="${energyCompanyId}" listName="TYPE_OF_SWITCH" useTextAsValue="${true}"/>
                            </tags:nameValueContainer2>
                        </cti:displayForPageEditModes>
                    </cti:tab>
                    <cti:displayForPageEditModes modes="EDIT,VIEW">
                        <cti:msg2 var="setupTab" key=".setupTab"/>
                        <cti:tab title="${setupTab}">
                            <tags:sectionContainer2 nameKey="configurationSection">
                                <tags:nameValueContainer2 tableClass="natural-width">
                                    <tags:nameValue2 nameKey=".operationMethod">
                                        <tags:selectWithItems path="CapBank.operationalState" 
                                        items="${opMethods}" itemValue="dbString"/>
                                    </tags:nameValue2>
                                    <tags:nameValue2 nameKey=".bankSize">
                                    <table> 
                                    <tr>
                                    <td>
                                        <tags:selectWithItems id="bankSize" path="CapBank.bankSize" 
                                         items="${bankSizes}" itemValue="displayValue"/>
                                        <tags:input units="kVar" id="customBankSize" path="CapBank.bankSizeCustom" inputClass="dn" size="6"/>
                                    </td> 
                                    <td class="vam">
                                        <i:inline key=".custom"/><tags:checkbox path="CapBank.customBankSize" id="customSizeCheckbox" styleClass="js-custom-bankSize"/>
                                    </td>
                                    </tr>
                                    </table>
                                    </tags:nameValue2>
                                    <tags:nameValue2 nameKey=".recloseDelay">
                                        <tags:intervalDropdown path="CapBank.recloseDelay"
                                            intervals="${timeIntervals}"
                                            id="scan1" />
                                    </tags:nameValue2>
                                    <tags:nameValue2 nameKey=".maxDailyOperations">
                                        <table>
                                        <tr>
                                        <td>
                                            <tags:switchButton path="CapBank.maxOperationDisabled" offClasses="M0" toggleGroup="maxOperation" inputClass="js-use-maxDailyOp" toggleAction="hide"/>
                                        </td>
                                        <td>
                                            <tags:input path="CapBank.maxDailyOps" size="5" toggleGroup="maxOperation" inputClass="dn"/>
                                        </td>
                                        </tr>
                                        </table>
                                    </tags:nameValue2>
                                    <tags:nameValue2 nameKey=".controlDevicePoint">
                                        <cti:displayForPageEditModes modes="EDIT">
                                            <form:hidden id="switch-control-point-input" path="CapBank.controlDeviceID"/>
                                                <tags:pickerDialog
                                                    id="cbcOrphanPicker"
                                                    type="capControlCBCOrphanPicker"
                                                    linkType="selectionLabel"
                                                    selectionProperty="paoName"
                                                    destinationFieldId="switch-control-point-input"
                                                    viewOnlyMode="${mode == 'VIEW'}"
                                                    allowEmptySelection="${true}"
                                                    includeRemoveButton="${true}"
                                                    removeValue="0"
                                                    extraArgs="${capbank.capBank.controlDeviceID}"/>
                                        </cti:displayForPageEditModes>
                                        <cti:displayForPageEditModes modes="VIEW">
                                            <c:choose>
                                                <c:when test="${empty capbank.capBank.controlDeviceID || capbank.capBank.controlDeviceID == 0}">
                                                    <span class="empty-list"><i:inline key="yukon.common.none"/></span>
                                                </c:when>
                                                <c:otherwise>${fn:escapeXml(cbc.name)}</c:otherwise>
                                           </c:choose>
                                       </cti:displayForPageEditModes>
                                    </tags:nameValue2>
                                </tags:nameValueContainer2>
                            </tags:sectionContainer2>
                            <tags:sectionContainer2 nameKey="controllerConfigurationSection">
                                <tags:nameValueContainer2 tableClass="natural-width">
                                    <cti:default var="format" value="DHMS_REDUCED"/>
                                    <tags:nameValue2 nameKey=".cbcController">
                                        <c:if test="${empty cbc}">
                                            <span class="empty-list"><i:inline key="yukon.common.none"/></span>
                                        </c:if>
                                        <c:if test="${not empty cbc}">
                                            <cti:url var="editCBC" value="/capcontrol/cbc/${cbc.id}"/>
                                                <a href="${editCBC}">${fn:escapeXml(cbc.name)}</a>
                                        </c:if>
                                    </tags:nameValue2>
                                    <c:if test="${not empty cbc}">
                                        <tags:nameValue2 nameKey=".cbc.serialNumber">
                                            ${fn:escapeXml(cbc.deviceCBC.serialNumber)}
                                        </tags:nameValue2>
                                        <c:if test="${not empty cbc.parentRtu}">
                                            <tags:nameValue2 nameKey=".cbc.parentRtu">
                                                <cti:paoDetailUrl yukonPao="${cbc.parentRtu}">
                                                    ${fn:escapeXml(cbc.parentRtu.paoName)}
                                                </cti:paoDetailUrl>
                                            </tags:nameValue2>
                                        </c:if>
                                        <c:set var="twoWayClass" value="${cbc.twoWay? '' : 'dn'} js-two-way"/>
                                        <c:set var="oneWayClass" value="${cbc.oneWay? '' : 'dn'} js-one-way"/>
                                        <tags:nameValue2 nameKey=".cbc.controlRoute" rowClass="${oneWayClass}">
                                            <c:choose>
                                                <c:when test="${cbc.deviceCBC.routeID == 0}">
                                                    <span class="empty-list"><i:inline key="yukon.common.none"/></span>
                                                </c:when>
                                                <c:otherwise>${fn:escapeXml(controllerRouteName)}</c:otherwise>
                                            </c:choose>
                                        </tags:nameValue2>
                                        <tags:nameValue2 nameKey=".cbc.masterAddr" rowClass="${twoWayClass}">
                                            ${fn:escapeXml(cbc.deviceAddress.masterAddress)}
                                        </tags:nameValue2>
                                        <tags:nameValue2 nameKey=".cbc.slaveAddr" rowClass="${twoWayClass}">
                                            ${fn:escapeXml(cbc.deviceAddress.slaveAddress)}
                                         </tags:nameValue2>
                                        <tags:nameValue2 nameKey=".cbc.commChannel" rowClass="${twoWayClass}">
                                            <c:forEach var="commChannel" items="${availablePorts}">
                                                <c:if test="${cbc.deviceDirectCommSettings.portID == commChannel.liteID}">
                                                    ${fn:escapeXml(commChannel.paoName)}                                            
                                                </c:if>                              
                                            </c:forEach>
                                        </tags:nameValue2>
                                        <tags:nameValue2 nameKey=".cbc.postCommWait" rowClass="${twoWayClass}">
                                            ${fn:escapeXml(cbc.deviceAddress.postCommWait)}
                                        </tags:nameValue2>
                                    </c:if>
                                </tags:nameValueContainer2>
                                <c:if test="${not empty cbc}">
                                    <tags:nameValueContainer2 tableClass="fl ${twoWayClass}">
                                           <tags:nameValue2 nameKey=".cbc.integrityScanRate">
                                                <tags:switchButton name="integ" toggleGroup="integrity" toggleAction="hide" checked="${cbc.editingIntegrity}" disabled="true" />
                                            </tags:nameValue2>
                                            <tags:nameValue2 nameKey=".cbc.interval" data-toggle-group="integrity">
                                                 <cti:formatDuration type="${format}" value="${cbc.deviceScanRateMap['Integrity'].intervalRate * 1000}"/>
                                            </tags:nameValue2>
                                            <tags:nameValue2 nameKey=".cbc.altInterval" data-toggle-group="integrity">
                                                 <cti:formatDuration type="${format}" value="${cbc.deviceScanRateMap['Integrity'].alternateRate * 1000}"/>
                                            </tags:nameValue2>
                                            <tags:nameValue2 nameKey=".cbc.scanGroup" data-toggle-group="integrity">
                                                <c:forEach var="scanGroup" items="${scanGroups}">
                                                    <c:if test="${cbc.deviceScanRateMap['Integrity'].scanGroup == scanGroup.dbValue}">
                                                        <i:inline key=".cbc.scanGroup.${scanGroup}"/>                                       
                                                    </c:if>                              
                                                </c:forEach>                                    
                                           </tags:nameValue2>
                                       </tags:nameValueContainer2>
                                       <tags:nameValueContainer2 tableClass="fr ${twoWayClass}">
                                           <tags:nameValue2 nameKey=".cbc.exceptionScanRate">
                                                <tags:switchButton name="excep" toggleGroup="exception" toggleAction="hide" checked="${cbc.editingException}" disabled="true" />
                                            </tags:nameValue2>
                                            <tags:nameValue2 nameKey=".cbc.interval" data-toggle-group="exception">
                                                 <cti:formatDuration type="${format}" value="${cbc.deviceScanRateMap['Exception'].intervalRate * 1000}"/>
                                            </tags:nameValue2>
                                            <tags:nameValue2 nameKey=".cbc.altInterval" data-toggle-group="exception">
                                                 <cti:formatDuration type="${format}" value="${cbc.deviceScanRateMap['Exception'].alternateRate * 1000}"/>
                                            </tags:nameValue2>
                                            <tags:nameValue2 nameKey=".cbc.scanGroup" data-toggle-group="exception">
                                                <c:forEach var="scanGroup" items="${scanGroups}">
                                                    <c:if test="${cbc.deviceScanRateMap['Exception'].scanGroup == scanGroup.dbValue}">
                                                        <i:inline key=".cbc.scanGroup.${scanGroup}"/>                                       
                                                    </c:if>                              
                                                </c:forEach>
                                            </tags:nameValue2> 
                                    </tags:nameValueContainer2>  
                                </c:if>
                            </tags:sectionContainer2>
                        </cti:tab>
                        <cti:msg2 var="addInfoTab" key=".additionalInfoTab"/>
                        <cti:tab title="${addInfoTab}">
                            <form:hidden path="capbankAdditionalInfo.deviceID" />
                            <tags:sectionContainer2 nameKey="configurationSection">
                                <tags:nameValueContainer2 tableClass="natural-width">
                                    <tags:nameValue2 nameKey=".cbAddInfo.maintAreaId">
                                        <tags:input path="capbankAdditionalInfo.maintAreaID" />
                                    </tags:nameValue2>
                                    <tags:nameValue2 nameKey=".cbAddInfo.poleNumber">
                                        <tags:input path="capbankAdditionalInfo.poleNumber" />
                                    </tags:nameValue2>
                                    <tags:nameValue2 nameKey=".cbAddInfo.config">
                                        <tags:selectWithItems path="capbankAdditionalInfo.capBankConfig" 
                                        items="${configList}" itemValue="displayName"/>
                                    </tags:nameValue2>
                                    <tags:nameValue2 nameKey=".cbAddInfo.potentialTransformer">
                                        <tags:selectWithItems path="capbankAdditionalInfo.potentTransformer" 
                                        items="${potentialTransformerList}" itemValue="displayName"/>
                                    </tags:nameValue2>
                                </tags:nameValueContainer2>
                            </tags:sectionContainer2>
                            <tags:sectionContainer2 nameKey="communicationSection">
                                <tags:nameValueContainer2 tableClass="natural-width">  
                                    <tags:nameValue2 nameKey=".cbAddInfo.medium">
                                        <tags:selectWithItems id="commMedium" path="capbankAdditionalInfo.commMedium" 
                                        items="${communicationMediumList}" itemValue="displayName"/>
                                        <tags:input id="customCommMedium" path="capbankAdditionalInfo.commMediumCustom" inputClass="dn" maxlength="20"/>
                                        <i:inline key=".custom"/><input id="customMediumCheckbox" type="checkbox" class="js-custom-medium" <c:if test="${capbank.capbankAdditionalInfo.customCommMedium}">checked="checked"</c:if> <c:if test="${mode == 'VIEW'}"> disabled="disabled"</c:if>>
                                    </tags:nameValue2>
                                    <tags:nameValue2 nameKey=".cbAddInfo.commStrength">
                                        <tags:input path="capbankAdditionalInfo.commStrengh" />
                                    </tags:nameValue2>
                                    <tags:nameValue2 nameKey=".cbAddInfo.externalAntenna">
                                        <tags:switchButton path="capbankAdditionalInfo.extAnt" offNameKey=".no.label" onNameKey=".yes.label" toggleGroup="externalAntenna" toggleAction="hide" />
                                    </tags:nameValue2>
                                    <tags:nameValue2 nameKey=".cbAddInfo.antennaType" data-toggle-group="externalAntenna">
                                        <tags:selectWithItems path="capbankAdditionalInfo.antennaType" 
                                        items="${antennaTypeList}" itemValue="displayName"/>
                                    </tags:nameValue2>
                                </tags:nameValueContainer2>
                            </tags:sectionContainer2>
                            <tags:sectionContainer2 nameKey="locationSection">
                                <tags:nameValueContainer2 tableClass="natural-width">
                                    <tags:nameValue2 nameKey=".cbAddInfo.latitude">
                                        <tags:input path="capbankAdditionalInfo.latit" />
                                    </tags:nameValue2>
                                    <tags:nameValue2 nameKey=".cbAddInfo.longitude">
                                        <tags:input path="capbankAdditionalInfo.longtit" />
                                    </tags:nameValue2>
                                </tags:nameValueContainer2>
                            </tags:sectionContainer2>
                            <tags:sectionContainer2 nameKey="maintenanceSection">
                                 <tags:nameValueContainer2 tableClass="natural-width">  
                                    <tags:nameValue2 nameKey=".cbAddInfo.lastVisit">
                                        <dt:dateTime path="capbankAdditionalInfo.lastMaintVisit"/>
                                    </tags:nameValue2>
                                    <tags:nameValue2 nameKey=".cbAddInfo.lastInspection">
                                        <dt:dateTime path="capbankAdditionalInfo.lastInspVisit"/>
                                    </tags:nameValue2>
                                    <tags:nameValue2 nameKey=".cbAddInfo.lastOpCountReset">
                                        <dt:dateTime path="capbankAdditionalInfo.opCountResetDate"/>
                                    </tags:nameValue2>
                                    <tags:nameValue2 nameKey=".cbAddInfo.requestPending">
                                        <tags:switchButton path="capbankAdditionalInfo.reqPend" offNameKey=".no.label" onNameKey=".yes.label"/>
                                    </tags:nameValue2>
                                    <tags:nameValue2 nameKey=".cbAddInfo.otherComments">
                                        <tags:input path="capbankAdditionalInfo.otherComments" size="40" maxlength="150"/>
                                    </tags:nameValue2>
                                    <tags:nameValue2 nameKey=".cbAddInfo.opTeamComments">
                                        <tags:input path="capbankAdditionalInfo.opTeamComments" size="40" maxlength="150"/>
                                    </tags:nameValue2>
                                    <tags:nameValue2 nameKey=".cbAddInfo.cbcInstallDate">
                                        <dt:dateTime path="capbankAdditionalInfo.cbcBattInstallDate"/>
                                    </tags:nameValue2>
                                    <tags:nameValue2 nameKey=".cbAddInfo.drivingDirections">
                                        <tags:input path="capbankAdditionalInfo.driveDir" size="40" maxlength="120"/>
                                    </tags:nameValue2>
                                </tags:nameValueContainer2>
                            </tags:sectionContainer2>
                        </cti:tab>
                    </cti:displayForPageEditModes>
                </cti:tabs>
            </div>
        
            <cti:displayForPageEditModes modes="EDIT,VIEW">
                <div class="column two nogutter">
                    <cti:tabs containerName="capbank_Tab2">
                        <cti:msg2 var="attachedPointsTab" key=".attachedPointsTab"/>
                        <cti:tab title="${attachedPointsTab}">
                            <div class="scroll-md">
                                <%@ include file="pointsTable.jsp" %>
                            </div>
                            <cti:checkRolesAndProperties value="CBC_DATABASE_EDIT">
                                <div class="action-area">
                                    <tags:pointCreation paoId="${capbank.id}" />
                                </div>
                            </cti:checkRolesAndProperties>
                        </cti:tab>
                    </cti:tabs>
                  </div>
              </cti:displayForPageEditModes>
         </div>

        <cti:displayForPageEditModes modes="EDIT,VIEW">

            <div class="column-12-12 clearfix">
                <c:set var="tableWidth" value="100%"/>
                <c:if test="${!capbank.overrideFeederLimitsSupported}">
                    <c:set var="tableWidth" value="70%"/>
                </c:if>
                <tags:sectionContainer2 nameKey="assignedPointsSection">
                <table class="compact-results-table row-highlighting" style="width:${tableWidth}">
                    <tr>
                        <th><i:inline key=".assignedPoints.point"/></th>
                        <th><i:inline key=".assignedPoints.adaptiveCount"/></th>
                        <th><i:inline key=".assignedPoints.phase"/></th>
                        <th><i:inline key=".assignedPoints.initiateScan"/></th>
                        <c:if test="${capbank.overrideFeederLimitsSupported}">
                            <th><i:inline key=".assignedPoints.overrideFeederLimits"/></th>
                            <th><i:inline key=".assignedPoints.upperBandwidth"/></th>
                            <th><i:inline key=".assignedPoints.lowerBandwidth"/></th>
                        </c:if>
                        <th></th>
                    </tr>
                        <c:forEach var="point" items="${capbank.ccMonitorBankList}" varStatus="status">
                        <c:set var="idx" value="${status.index}" />
                        <tr>
                            <td>${point.name}</td>
                            <td style="max-width:200px;">
                                <tags:input path="ccMonitorBankList[${idx}].NINAvg" size="10"/>
                            </td>
                            <td>
                                <tags:selectWithItems path="ccMonitorBankList[${idx}].phase" items="${pointPhaseList}"/>
                            </td>
                            <td>
                                <tags:switchButton path="ccMonitorBankList[${idx}].scannableBoolean" offNameKey=".no.label" onNameKey=".yes.label"/>
                            </td>
                            <c:if test="${capbank.overrideFeederLimitsSupported}">
                                <td>
                                    <tags:switchButton path="ccMonitorBankList[${idx}].overrideStrategySettings" offNameKey=".no.label" onNameKey=".yes.label" toggleGroup="overrideFeederLimits_${idx}" toggleAction="invisible"/>
                                </td>
        
                                <td style="max-width:150px;" class="db wrbw wsn">
                                    <span data-toggle-group="overrideFeederLimits_${idx}"><tags:input path="ccMonitorBankList[${idx}].upperBandwidth" size="10" /></span>
                                </td>
                                <td style="max-width:150px;" class="wsn">
                                    <span data-toggle-group="overrideFeederLimits_${idx}"><tags:input path="ccMonitorBankList[${idx}].lowerBandwidth" size="10" /></span>
                                </td>
                            </c:if>
                            <td>
                            <cti:displayForPageEditModes modes="EDIT">
                                <cti:checkRolesAndProperties value="CBC_DATABASE_EDIT">
                                    <cti:button icon="icon-cross" renderMode="buttonImage" classes="select-box-item-remove js-remove-capbank-point show-on-hover" />
                                </cti:checkRolesAndProperties>
                            </cti:displayForPageEditModes>
                            </td>
                            <form:hidden path="ccMonitorBankList[${idx}].deviceId" />
                            <form:hidden path="ccMonitorBankList[${idx}].pointId" />
                            <form:hidden path="ccMonitorBankList[${idx}].displayOrder" />
                        </tr>
                        </c:forEach>
                </table>        
                
             </tags:sectionContainer2>    
                
            </div>
            
            
        
        </cti:displayForPageEditModes>

        <div class="page-action-area">
            <cti:displayForPageEditModes modes="EDIT,CREATE">
                <cti:button nameKey="save" type="submit" classes="primary action"/>
            </cti:displayForPageEditModes>
    
            <cti:displayForPageEditModes modes="EDIT">
    
                <cti:button nameKey="delete" classes="delete js-delete" data-ok-event="yukon:da:capbank:delete"/>
                <d:confirm on=".js-delete" nameKey="confirmDelete" argument="${capbank.name}"/>
    
                <cti:url var="viewUrl" value="/capcontrol/capbanks/${capbank.id}"/>
                <cti:button nameKey="cancel" href="${viewUrl}"/>
    
            </cti:displayForPageEditModes>
    
            <cti:displayForPageEditModes modes="CREATE">
                <cti:button nameKey="cancel" href="javascript:window.history.back()"/>
            </cti:displayForPageEditModes>
        </div>
    </form:form>

    <cti:url var="url" value="/capcontrol/capbanks/${capbank.id}"/>
    <form:form id="delete-capbank" method="DELETE" action="${url}">
        <cti:csrfToken/>
    </form:form>
    <cti:toJson id="two-way-types" object="${twoWayTypes}"/>
    <cti:toJson id="logical-types" object="${logicalTypes}"/>
    <cti:toJson id="tcp-comm-ports" object="${tcpCommPorts}"/>
    <cti:includeScript link="/resources/js/pages/yukon.da.capbank.js" />
    <%-- EDIT POINTS POPUP --%>
<div class="dn js-edit-points-popup"
    data-dialog
    data-parent-id="${capbank.id}"
    data-title="<cti:msg2 key=".edit.points"/>"
    data-ok-text="<cti:msg2 key="yukon.common.save"/>"
    data-url="<cti:url value="/capcontrol/capbanks/${capbank.id}/points/edit"/>"
    data-event="yukon:vv:children:save"
    data-height="300"
    data-width="750"></div>
</cti:standardPage>