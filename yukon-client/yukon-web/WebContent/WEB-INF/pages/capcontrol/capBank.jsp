<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti"%>
<%@ taglib prefix="d" tagdir="/WEB-INF/tags/dialog" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n"%>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags"%>
<%@ taglib prefix="dt" tagdir="/WEB-INF/tags/dateTime"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>

<c:set var="pageName" value="capbank.${mode}"/>
<c:if test="${orphan}">
    <c:set var="pageName" value="capbank.orphan.${mode}"/>
</c:if>

<cti:standardPage module="capcontrol" page="${pageName}">

<%@ include file="/capcontrol/capcontrolHeader.jspf" %>

    <tags:setFormEditMode mode="${mode}" />

    <cti:url var="action" value="/capcontrol/capbanks" />
    <form:form id="capbank-edit-form" commandName="capbank" action="${action}" method="POST">
        <cti:csrfToken />
        <form:hidden path="id" />
        <div class="column-14-10 clearfix">
            <div class="column one">
                <cti:tabs>
                    <cti:msg2 var="infoTab" key=".infoTab"/>
                    <cti:tab title="${infoTab}">
                        <tags:nameValueContainer2 tableClass="natural-width">
                            <tags:nameValue2 nameKey=".name">
                                <tags:input path="name" size="25" />
                            </tags:nameValue2>
                            <tags:nameValue2 nameKey=".status">
                                <tags:switchButton path="disabled" inverse="${true}"
                                    offNameKey=".disabled.label" onNameKey=".enabled.label" />
                            </tags:nameValue2>
                            <tags:nameValue2 nameKey=".address">
                                <tags:input path="location"/>
                            </tags:nameValue2>
                            <tags:nameValue2 nameKey=".mapLocationID">
                                <tags:input path="CapBank.mapLocationID"/>
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
                    </cti:tab>
                    <cti:msg2 var="setupTab" key=".setupTab"/>
                    <cti:tab title="${setupTab}">
                        <tags:sectionContainer2 nameKey="configurationSection">
                            <tags:nameValueContainer2 tableClass="natural-width">
                                <tags:nameValue2 nameKey=".operationMethod">
                                    <tags:selectWithItems path="CapBank.operationalState" 
                                    items="${opMethods}" itemValue="dbString"/>
                                </tags:nameValue2>
                                <tags:nameValue2 nameKey=".bankSize">
                                    <tags:selectWithItems id="bankSize" path="CapBank.bankSize" 
                                    items="${bankSizes}" itemValue="displayValue"/>
                                    <tags:input id="customBankSize" path="CapBank.bankSizeCustom" inputClass="dn" size="6" /> kVar
                                    <i:inline key=".custom"/><input id="customSizeCheckbox" type="checkbox" class="js-custom-bankSize" <c:if test="${capbank.capBank.customBankSize}">checked="checked"</c:if>>
                                </tags:nameValue2>
                                <tags:nameValue2 nameKey=".recloseDelay" data-toggle-group="integrity">
                                    <tags:intervalStepper path="CapBank.recloseDelay"
                                        intervals="${timeIntervals}"
                                        id="scan1" />
                                </tags:nameValue2>
                                <tags:nameValue2 nameKey=".controlDevicePoint">
                                    <form:hidden id="switch-control-point-input" path="CapBank.controlDeviceID"/>
                                    <tags:pickerDialog
                                        id="cbcOrphanPicker"
                                        type="capControlCBCOrphanPicker"
                                        linkType="selection"
                                        selectionProperty="paoName"
                                        destinationFieldId="switch-control-point-input"
                                        viewOnlyMode="${mode == 'VIEW'}"
                                        allowEmptySelection="${true}"
                                        includeRemoveButton="${true}"
                                        removeValue="0" 
                                        extraArgs="${capbank.capBank.controlDeviceID}"/>
                                    <cti:displayForPageEditModes modes="VIEW">
                                        <c:if test="${empty capbank.capBank.controlDeviceID}">
                                            <span class="empty-list"><i:inline key="yukon.common.none.choice"/></span>
                                        </c:if>
                                    </cti:displayForPageEditModes>
                                </tags:nameValue2>
                            </tags:nameValueContainer2>
                        </tags:sectionContainer2>
                        <tags:sectionContainer2 nameKey="operationsSection">
                           <tags:nameValueContainer2 tableClass="natural-width">
                                <tags:nameValue2 nameKey=".maxDailyOperations">
                                    <tags:input path="CapBank.maxDailyOps" size="5"/>
                                    <i:inline key=".unlimitedText"/>
                                </tags:nameValue2>
                                <tags:nameValue2 nameKey=".disableMaxOps">
                                    <tags:switchButton path="CapBank.maxOperationDisabled" offNameKey=".no.label" onNameKey=".yes.label"/>
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
                                    <c:set var="twoWayClass" value="${cbc.twoWay? '' : 'dn'} js-two-way"/>
                                    <c:set var="oneWayClass" value="${cbc.twoWay? 'dn' : ''} js-one-way"/>
                                    <tags:nameValue2 nameKey=".cbc.controlRoute" rowClass="${oneWayClass}">
                                        ${fn:escapeXml(controllerRouteName)}
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
                                <tags:nameValueContainer2 tableClass="fl">
                                       <tags:nameValue2 nameKey=".cbc.integrityScanRate" rowClass="${twoWayClass}">
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
                                   <tags:nameValueContainer2 tableClass="fr">
                                       <tags:nameValue2 nameKey=".cbc.exceptionScanRate" rowClass="${twoWayClass}">
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
                                    <tags:input id="customCommMedium" path="capbankAdditionalInfo.commMediumCustom" inputClass="dn"/>
                                    <i:inline key=".custom"/><input id="customMediumCheckbox" type="checkbox" class="js-custom-medium" <c:if test="${capbank.capbankAdditionalInfo.customCommMedium}">checked="checked"</c:if>>
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
                                    <tags:input path="capbankAdditionalInfo.otherComments" size="40"/>
                                </tags:nameValue2>
                                <tags:nameValue2 nameKey=".cbAddInfo.opTeamComments">
                                    <tags:input path="capbankAdditionalInfo.opTeamComments" size="40"/>
                                </tags:nameValue2>
                                <tags:nameValue2 nameKey=".cbAddInfo.cbcInstallDate">
                                    <dt:dateTime path="capbankAdditionalInfo.cbcBattInstallDate"/>
                                </tags:nameValue2>
                                <tags:nameValue2 nameKey=".cbAddInfo.drivingDirections">
                                    <tags:input path="capbankAdditionalInfo.driveDir" size="40" />
                                </tags:nameValue2>
                            </tags:nameValueContainer2>
                        </tags:sectionContainer2>
                    </cti:tab>
                </cti:tabs>
            </div>
        
            <cti:displayForPageEditModes modes="EDIT,VIEW">
                <div class="column two nogutter">
                    <cti:tabs>
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

        <div class="column-12-12 clearfix">
            <tags:sectionContainer2 nameKey="assignedPointsSection">
            <table class="compact-results-table row-highlighting">
                <tr>
                    <th><i:inline key=".assignedPoints.point"/></th>
                    <th><i:inline key=".assignedPoints.adaptiveCount"/></th>
                    <th><i:inline key=".assignedPoints.phase"/></th>
                    <th><i:inline key=".assignedPoints.initiateScan"/></th>
                    <th><i:inline key=".assignedPoints.overrideFeederLimits"/></th>
                    <th><i:inline key=".assignedPoints.upperBandwidth"/></th>
                    <th><i:inline key=".assignedPoints.lowerBandwidth"/></th>
                    <th></th>
                </tr>
                    <c:forEach var="point" items="${capbank.ccMonitorBankList}" varStatus="status">
                    <c:set var="idx" value="${status.index}" />
                    <tr>
                        <td>${point.name}</td>
                        <td>
                            <tags:input path="ccMonitorBankList[${idx}].NINAvg" size="10"/>
                        </td>
                        <td>
                            <tags:selectWithItems path="ccMonitorBankList[${idx}].phase" items="${pointPhaseList}"/>
                        </td>
                        <td>
                            <tags:switchButton path="ccMonitorBankList[${idx}].scannableBoolean" offNameKey=".no.label" onNameKey=".yes.label"/>
                        </td>
                        <td>
                            <tags:switchButton path="ccMonitorBankList[${idx}].overrideStrategySettings" offNameKey=".no.label" onNameKey=".yes.label" toggleGroup="overrideFeederLimits_${idx}" toggleAction="disabled"/>
                        </td>

                        <td>
                            <span data-toggle-group="overrideFeederLimits_${idx}"><tags:input path="ccMonitorBankList[${idx}].upperBandwidth" size="10" /></span>
                        </td>
                        <td>
                            <span data-toggle-group="overrideFeederLimits_${idx}"><tags:input path="ccMonitorBankList[${idx}].lowerBandwidth" size="10" /></span>
                        </td>
                        <td>
                            <c:if test="${canEdit}">
                                <cti:button icon="icon-cross" renderMode="buttonImage" classes="select-box-item-remove js-remove-point show-on-hover" />
                            </c:if>
                        </td>
                        <form:hidden path="ccMonitorBankList[${idx}].deviceId" />
                        <form:hidden path="ccMonitorBankList[${idx}].pointId" />
                        <form:hidden path="ccMonitorBankList[${idx}].displayOrder" />
                    </tr>
                    </c:forEach>
            </table>        
            <c:if test="${canEdit}">
                <div class="action-area">
                    <cti:button nameKey="edit" icon="icon-pencil"
                        data-popup=".js-edit-points-popup" data-popup-toggle=""/>
                </div>
            </c:if>
            </tags:sectionContainer2>    
        </div>

        <div class="page-action-area">
            <cti:displayForPageEditModes modes="EDIT,CREATE">
                <cti:button nameKey="save" type="submit" classes="primary action" />
            </cti:displayForPageEditModes>
            <cti:displayForPageEditModes modes="EDIT">

                <cti:button nameKey="delete" classes="delete js-delete" data-ok-event="yukon:da:capbank:delete"/>
                <d:confirm on=".js-delete" nameKey="confirmDelete" argument="${capbank.name}"/>

            </cti:displayForPageEditModes>
            
            <cti:url var="viewUrl" value="/capcontrol/capbanks/${capbank.id}"/>
            <cti:button nameKey="cancel" href="${viewUrl}"/>

        </div>
    </form:form>

    <cti:url var="url" value="/capcontrol/capbanks/${capbank.id}"/>
    <form:form id="delete-capbank" method="DELETE" action="${url}">
        <cti:csrfToken/>
    </form:form>
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