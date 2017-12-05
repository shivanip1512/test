<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="dt" tagdir="/WEB-INF/tags/dateTime" %>

<cti:msgScope paths="modules.operator.hardware">

<tags:setFormEditMode mode="${mode}"/>

<tags:nameValueContainer2>

    <form:form commandName="hardware">

     <tags:nameValue2 nameKey="${displayTypeKey}">
        ${fn:escapeXml(hardware.displayType)}
    </tags:nameValue2>
    
   <%-- For switchs and tstat's, show serial number. If is also a device, show device name--%>
    <c:choose>
        <c:when test="${showSerialNumber}">
            <c:choose>
                <c:when test="${serialNumberEditable}">
                    <tags:inputNameValue nameKey=".serialNumber" path="serialNumber" inputClass="js-focus"></tags:inputNameValue>
                </c:when>
                
                <c:otherwise>
                    <tags:nameValue2 nameKey=".serialNumber">
                        <tags:bind path="serialNumber">
                            <form:hidden path="serialNumber"/>
                            ${fn:escapeXml(hardware.serialNumber)}
                        </tags:bind>
                    </tags:nameValue2>
                </c:otherwise>
                
            </c:choose>
        </c:when>
        <c:otherwise>
            <tags:nameValue2 nameKey=".meterNumber">
                <form:hidden path="meterNumber"/>
                ${fn:escapeXml(hardware.meterNumber)}
            </tags:nameValue2>
        </c:otherwise>
    </c:choose>
  
    <c:if test="${hardware.deviceId > 0}">
        <cti:displayForPageEditModes modes="EDIT,VIEW">
            <tags:nameValue2 nameKey=".displayName"><cti:deviceName deviceId="${hardware.deviceId}"/></tags:nameValue2>
        </cti:displayForPageEditModes>
    </c:if>
        
    <c:if test="${showInstallCode}">
        <tags:inputNameValue nameKey=".installCode" path="installCode" disabled="false" maxlength="23" size="25"/>
    </c:if>
    
    <c:if test="${showMacAddress}">
        <c:choose>
           <c:when test="${macAddressEditable}">
               <tags:inputNameValue nameKey=".macAddress" path="macAddress" maxlength="23" size="25"/>
           </c:when>
           
           <c:otherwise>
               <tags:nameValue2 nameKey=".macAddress">
                   <tags:bind path="macAddress">
                       <form:hidden path="macAddress"/>
                       ${fn:escapeXml(hardware.macAddress)}
                   </tags:bind>
               </tags:nameValue2>
           </c:otherwise>
        </c:choose>
   </c:if>
 
    <c:if test="${showDeviceVendorUserId}">
        <c:choose>
               <c:when test="${deviceVendorUserIdEditable}">
                   <tags:inputNameValue nameKey=".deviceVendorUserId" path="deviceVendorUserId" maxlength="23" size="25"/>
               </c:when>
               
               <c:otherwise>
                   <tags:nameValue2 nameKey=".deviceVendorUserId">
                       <tags:bind path="deviceVendorUserId">
                           <form:hidden path="deviceVendorUserId"/>
                           ${fn:escapeXml(hardware.deviceVendorUserId)}
                       </tags:bind>
                   </tags:nameValue2>
               </c:otherwise>
            </c:choose>
    </c:if>
 
    <c:if test="${showFirmwareVersion}">
        <tags:inputNameValue nameKey=".firmwareVersion" path="firmwareVersion"></tags:inputNameValue>
    </c:if>
    
    <tags:inputNameValue nameKey=".label" path="displayLabel"/>
    
    <tags:inputNameValue nameKey=".altTrackingNumber" path="altTrackingNumber"/>
    
    <c:if test="${showVoltage}">
        <tags:yukonListEntrySelectNameValue nameKey=".voltage" path="voltageEntryId" energyCompanyId="${energyCompanyId}" listName="DEVICE_VOLTAGE"/>
    </c:if>

    <tags:nameValue2 nameKey=".fieldInstallDate">
        <dt:date path="fieldInstallDate" />
    </tags:nameValue2>
    
    <tags:nameValue2 nameKey=".fieldReceiveDate">
        <dt:date path="fieldReceiveDate" />
    </tags:nameValue2>
    
    <tags:nameValue2 nameKey=".fieldRemoveDate">
        <dt:date path="fieldRemoveDate" />
    </tags:nameValue2>
    
    <tags:textareaNameValue nameKey=".deviceNotes" path="deviceNotes" rows="4" cols="30" rowClass="wall-of-text"/>
<%-- 
    <c:if test="${showGateway}">
        
        <tags:nameValue2 nameKey=".gateway">
            <c:if test="${mode == 'VIEW' and hardware.gatewayId != null}">
                <cti:url value="/stars/operator/hardware/view" var="viewUrl">
                    <cti:param name="inventoryId" value="${gatewayInventoryId}"/>
                    <cti:param name="accountId" value="${accountId}"/>
                </cti:url>
                <a href="${viewUrl}">
            </c:if>
            
            <tags:selectWithItems path="gatewayId" items="${gateways}" itemValue="paoId" itemLabel="name" 
                    defaultItemValue="" defaultItemLabel="${none}"/>
            <c:if test="${mode == 'VIEW' and hardware.gatewayId != null}">
                </a>
            </c:if>
        </tags:nameValue2>
        
    </c:if>
    
    <c:if test="${showRoute}">
        <tags:selectNameValue nameKey=".route" path="routeId"  itemLabel="paoName" itemValue="yukonID" items="${routes}"  defaultItemValue="${defaultRouteId}" defaultItemLabel="${fn:escapeXml(defaultRoute)}"/>
    </c:if>
    
    <tags:yukonListEntrySelectNameValue nameKey=".status" path="deviceStatusEntryId" energyCompanyId="${energyCompanyId}" listName="DEVICE_STATUS" defaultItemValue="0" defaultItemLabel="${none}"/>
    
    <c:if test="${showTwoWay}">
        Two Way LCR's Yukon Device
        <tags:nameValue2 nameKey=".twoWayDevice" rowClass="pickerRow">
            <form:hidden path="creatingNewTwoWayDevice" id="creatingNewTwoWayDevice"/>

            <div id="twoWayPickerContainer" <c:if test="${hardware.creatingNewTwoWayDevice}">style="display: none;"</c:if>>
                
                <tags:pickerDialog type="twoWayLcrPicker" 
                        id="twoWayLcrPicker" 
                        linkType="selection" 
                        immediateSelectMode="true"
                        destinationFieldName="deviceId" 
                        selectionProperty="paoName" 
                        initialId="${hardware.deviceId}" 
                        viewOnlyMode="${mode == 'VIEW'}"/>
                    
                <cti:displayForPageEditModes modes="CREATE,EDIT">
                    <cti:button nameKey="new" id="newButton"/>
                </cti:displayForPageEditModes><br><br><form:errors path="deviceId" cssClass="error"/>
                
            </div>
            
            <div id="newTwoWayDeviceContainer" <c:if test="${!hardware.creatingNewTwoWayDevice}">style="display: none;"</c:if>>
                <spring:bind path="twoWayDeviceName">
                    <c:if test="${status.error}"><c:set var="inputToClass" value="error"/></c:if>
                    <form:input path="twoWayDeviceName" id="twoWayDeviceName"  cssClass="${inputToClass}"/>
                    <cti:button nameKey="choose" id="chooseButton"/>
                    <c:if test="${status.error}">
                        <br><br>
                        <form:errors path="twoWayDeviceName" cssClass="error"/>
                    </c:if>
                </spring:bind>
            </div>
        </tags:nameValue2>
    
    </c:if>

<cti:checkRolesAndProperties value="SHOW_ASSET_AVAILABILITY">
    <c:if test="${isTwoWayDevice && (!empty assetAvailability)}">
        <tags:nameValue2 nameKey=".assetAvailability.label">
            <c:set var="aaStatus" value="${assetAvailability.status}" />
            <c:set var="isOptedOut" value="${assetAvailability.optedOut}" />
            <c:choose>
                <c:when test="${aaStatus == 'ACTIVE'}">
                    <c:choose>
                        <c:when test="${isOptedOut}">
                            <span title="<cti:msg2 key=".assetAvailability.optedOutActive.title"/>">
                                <i:inline key=".assetAvailability.optedOutActive"/>
                            </span>
                        </c:when>
                        <c:otherwise>
                            <span class="success"  title="<cti:msg2 key=".assetAvailability.active.title"/>">
                                <i:inline key=".assetAvailability.active"/>
                            </span>
                        </c:otherwise>
                    </c:choose>
                </c:when>
                <c:when test="${aaStatus == 'INACTIVE'}">
                    <c:choose>
                        <c:when test="${isOptedOut}">
                            <span title="<cti:msg2 key=".assetAvailability.optedOutInactive.title"/>">
                                <i:inline key=".assetAvailability.optedOutInactive"/>
                            </span>
                        </c:when>
                        <c:otherwise>
                            <span title="<cti:msg2 key=".assetAvailability.inactive.title"/>">
                                <i:inline key=".assetAvailability.inactive"/>
                            </span>
                        </c:otherwise>
                    </c:choose>
                </c:when>
                <c:when test="${aaStatus == 'UNAVAILABLE'}">
                    <c:choose>
                        <c:when test="${isOptedOut}">
                            <span title="<cti:msg2 key=".assetAvailability.optedOutUnavailable.title"/>">
                                <i:inline key=".assetAvailability.optedOutUnavailable"/>
                            </span>
                        </c:when>
                        <c:otherwise>
                            <span title="<cti:msg2 key=".assetAvailability.unavailable.title"/>">
                                <i:inline key=".assetAvailability.unavailable"/>
                            </span>
                        </c:otherwise>
                    </c:choose>
                </c:when>
                <c:otherwise>
                    <span class="error"><i:inline key=".assetAvailability.invalidStatus" arguments="${aaStatus}"/></span>
                </c:otherwise>
            </c:choose>
        </tags:nameValue2>
    </c:if>
</cti:checkRolesAndProperties> --%>

</form:form>

</tags:nameValueContainer2>

</cti:msgScope>