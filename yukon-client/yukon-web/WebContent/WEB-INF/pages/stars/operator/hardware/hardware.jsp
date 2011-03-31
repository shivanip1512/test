<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>

<cti:standardPage module="operator" page="hardware.${mode}">
<tags:setFormEditMode mode="${mode}"/>
<cti:includeCss link="/WebConfig/yukon/styles/operator/hardware.css"/>

<script type="text/javascript">
YEvent.observeSelectorClick('#newButton', function(event) {
    $('twoWayPickerContainer').hide();
    $('newTwoWayDeviceContainer').show();
    var deviceNameInput = $('twoWayDeviceName');
    deviceNameInput.value = '';
    deviceNameInput.focus();
    $('creatingNewTwoWayDevice').value = true;
});

YEvent.observeSelectorClick('#chooseButton', function(event) {
    $('twoWayPickerContainer').show();
    $('newTwoWayDeviceContainer').hide();
    $('creatingNewTwoWayDevice').value = false;
});

function showDeletePopup() {
    $('deleteHardwarePopup').show();
}

function hideDeletePopup() {
	$('deleteHardwarePopup').hide();
}

function updateServiceCompanyInfo() {
    var url = '/spring/stars/operator/hardware/serviceCompanyInfo';
    var serviceCompanyId = 

    	<cti:displayForPageEditModes modes="EDIT,CREATE">
            $F('serviceCompanyId');
        </cti:displayForPageEditModes>

    	<cti:displayForPageEditModes modes="VIEW">
            ${hardwareDto.serviceCompanyId};
        </cti:displayForPageEditModes>
        
    if (serviceCompanyId > 0) {
        var params = {'serviceCompanyId' : serviceCompanyId};
        new Ajax.Updater('serviceCompanyContainer', url, {method: 'get', evalScripts: true, parameters: params});
    } else {
        $('serviceCompanyContainer').innerHTML = '';
    }
}

Event.observe(window, 'load', updateServiceCompanyInfo);
</script>
    
    <!-- Delete Hardware Popup -->
    <i:simplePopup styleClass="mediumSimplePopup" titleKey=".deleteDevice" id="deleteHardwarePopup" arguments="${hardwareDto.displayName}">
        <form id="deleteForm" action="/spring/stars/operator/hardware/deleteHardware" method="post">
            <input type="hidden" name="inventoryId" value="${inventoryId}">
            <input type="hidden" name="accountId" value="${accountId}">
            <c:choose>
                <c:when test="${hardwareDto.hardwareClass == 'METER'}">
                    <c:set var="deleteMsgKeySuffix" value="DeviceName"/>
                </c:when>
                <c:otherwise>
                    <c:set var="deleteMsgKeySuffix" value="SerialNumber"/>
                </c:otherwise>
            </c:choose>
            <cti:msg2 key=".deleteMessage${deleteMsgKeySuffix}" argument="${hardwareDto.displayName}"/>
            <br><br>
            <input type="radio" name="deleteOption" value="remove" checked="checked" id="removeRadio">
            <label for="removeRadio" class="radioLabel"><i:inline key=".deleteOption1"/></label>
            <br>
            <input type="radio" name="deleteOption" value="delete" id="deleteRadio">
            <label for="deleteRadio" class="radioLabel"><i:inline key=".deleteOption2"/></label>
            <br><br>
            <table class="popupButtonTable">
                <tr>
                    <td>
                        <cti:checkRolesAndProperties value="OPERATOR_ALLOW_ACCOUNT_EDITING">
                            <cti:button key="delete" type="submit" name="delete"/>
                        </cti:checkRolesAndProperties>
                        <cti:button key="cancel" onclick="hideDeletePopup()"/>
                    </td>
                </tr>
            </table>
        </form>
    </i:simplePopup>
    
    <cti:msg2 key=".noneSelectOption" var="noneSelectOption"/>
    
    <cti:displayForPageEditModes modes="EDIT">
        <cti:url value="/spring/stars/operator/hardware/updateHardware" var="action"/>
    </cti:displayForPageEditModes>
    <cti:displayForPageEditModes modes="CREATE">
        <cti:url value="/spring/stars/operator/hardware/createHardware" var="action"/>
    </cti:displayForPageEditModes>
    
    <form:form id="updateForm" commandName="hardwareDto" action="${action}">
    
        <input type="hidden" name="accountId" value="${accountId}">
        <input type="hidden" name="inventoryId" value="${inventoryId}">
        <form:hidden path="energyCompanyId"/>
        <form:hidden path="displayType"/>
        <form:hidden path="displayName"/>
        <form:hidden path="hardwareType"/>
        <form:hidden path="hardwareClass"/>
        <c:if test="${not showTwoWay}">
            <form:hidden path="deviceId"/>
        </c:if>
        
        <cti:dataGrid cols="2" rowStyle="vertical-align:top;" cellStyle="padding-right:20px;">
        
            <%-- DEVICE INFO --%>
            <cti:dataGridCell>
            
                <tags:formElementContainer nameKey="deviceInfoSection">
                    
                    <tags:nameValueContainer2>
                    
                        <cti:displayForPageEditModes modes="EDIT,VIEW">
                        
                            <tags:nameValue2 nameKey=".deviceType">
                                <spring:escapeBody htmlEscape="true">${hardwareDto.displayType}</spring:escapeBody>
                            </tags:nameValue2>
                        
                        </cti:displayForPageEditModes>
                        
                        <cti:displayForPageEditModes modes="CREATE">
                            
                            <tags:selectNameValue nameKey="${displayTypeKey}" path="hardwareTypeEntryId"  itemLabel="displayName" 
                             					  itemValue="hardwareTypeEntryId" items="${deviceTypes}"/>
                                                  
                        </cti:displayForPageEditModes>
                        
						<c:if test="${showInstallCode}">
	                            <tags:inputNameValue nameKey=".installCode" path="installCode" disabled="false"/>
                        </c:if>
                        
                        <%-- For switchs and tstat's, show serial number, otherwise device name --%>
                        <c:choose>
                            <c:when test="${showSerialNumber}">
                                <c:choose>
                                
                                    <c:when test="${serialNumberEditable}">
                                        <tags:inputNameValue nameKey=".serialNumber" path="serialNumber"></tags:inputNameValue>
                                    </c:when>
                                    
                                    <c:otherwise>
                                        <form:hidden path="serialNumber"/>
                                        <tags:nameValue2 nameKey=".serialNumber"><spring:escapeBody htmlEscape="true">${hardwareDto.serialNumber}</spring:escapeBody></tags:nameValue2>
                                    </c:otherwise>
                                    
                                </c:choose>
                            </c:when>
                            
                            <c:otherwise>
                                <cti:displayForPageEditModes modes="EDIT,VIEW">
                                    <tags:nameValue2 nameKey=".displayName"><spring:escapeBody htmlEscape="true">${hardwareDto.displayName}</spring:escapeBody></tags:nameValue2>
                                </cti:displayForPageEditModes>
                            </c:otherwise>
                            
                        </c:choose>
						
						<c:if test="${showMacAddress}">
                    		<tags:inputNameValue nameKey=".macAddress" path="macAddress"></tags:inputNameValue>                   
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
                            <tags:dateInputCalendar fieldName="fieldInstallDate" fieldValue="fieldInstallDate" springInput="true"></tags:dateInputCalendar>
                        </tags:nameValue2>
                        
                        <tags:nameValue2 nameKey=".fieldReceiveDate">
                            <tags:dateInputCalendar fieldName="fieldReceiveDate" fieldValue="fieldReceiveDate" springInput="true"></tags:dateInputCalendar>
                        </tags:nameValue2>
                        
                        <tags:nameValue2 nameKey=".fieldRemoveDate">
                            <tags:dateInputCalendar fieldName="fieldRemoveDate" fieldValue="fieldRemoveDate" springInput="true"></tags:dateInputCalendar>
                        </tags:nameValue2>
                        
                        <tags:textareaNameValue nameKey=".deviceNotes" path="deviceNotes" rows="4" cols="20" />
                        
                        <c:if test="${showRoute}">
                            <tags:selectNameValue nameKey=".route" path="routeId"  itemLabel="paoName" itemValue="yukonID" items="${routes}"  defaultItemValue="0" defaultItemLabel="${defaultRoute}"/>
                        </c:if>
                        
                        <tags:yukonListEntrySelectNameValue nameKey=".status" path="deviceStatusEntryId" energyCompanyId="${energyCompanyId}" listName="DEVICE_STATUS" defaultItemValue="0" defaultItemLabel="(none)"/>
                        
                        <cti:displayForPageEditModes modes="EDIT,VIEW">
                            
                            <form:hidden path="originalDeviceStatusEntryId"/>
                            
                            <c:if test="${showTwoWay}">
                                <form:hidden path="creatingNewTwoWayDevice" id="creatingNewTwoWayDevice"/>
                                <%-- Two Way LCR's Yukon Device --%>
                                <tags:nameValue2 nameKey=".twoWayDevice" rowClass="pickerRow">

                                    <div id="twoWayPickerContainer" <c:if test="${hardwareDto.creatingNewTwoWayDevice}">style="display: none;"</c:if>>
                                        <tags:pickerDialog type="twoWayLcrPicker" id="twoWayLcrPicker" linkType="selection" immediateSelectMode="true"
                                            destinationFieldName="deviceId" selectionProperty="paoName" initialId="${hardwareDto.deviceId}" viewOnlyMode="${mode == 'VIEW'}"/>
                                            
                                        <cti:displayForPageEditModes modes="EDIT">
                                            <cti:button key="new" id="newButton"/>
                                        </cti:displayForPageEditModes>
                                    </div>
                                    <div id="newTwoWayDeviceContainer" <c:if test="${!hardwareDto.creatingNewTwoWayDevice}">style="display: none;"</c:if>>
                                        <spring:bind path="twoWayDeviceName">
                                            <c:if test="${status.error}"><c:set var="inputToClass" value="error"/></c:if>
                                            <form:input path="twoWayDeviceName" id="twoWayDeviceName"  cssClass="${inputToClass}"/>
                                            <cti:button key="choose" id="chooseButton"/>
                                            <c:if test="${status.error}">
                                                <br>
                                                <form:errors path="twoWayDeviceName" cssClass="errorMessage"/>
                                            </c:if>
                                        </spring:bind>
                                    </div>
                                </tags:nameValue2>
                            
                            </c:if>
                            
                        </cti:displayForPageEditModes>
                        
                    </tags:nameValueContainer2>
                
                </tags:formElementContainer>
            
            </cti:dataGridCell>
            
            <%--SERVICE AND STORAGE --%>
            <cti:dataGridCell>
            
                <tags:formElementContainer nameKey="serviceAndStorageSection">
                    
                    <tags:nameValueContainer2>
                        
                        <cti:displayForPageEditModes modes="EDIT,CREATE">
                            <tags:selectNameValue nameKey=".serviceCompany" path="serviceCompanyId" itemLabel="serviceCompanyName" itemValue="serviceCompanyId" 
                                items="${serviceCompanies}" defaultItemValue="0" defaultItemLabel="(none)" onchange="updateServiceCompanyInfo()"/>
                        </cti:displayForPageEditModes>
                        
                        <tags:nameValue2 nameKey=".serviceCompanyInfo">
                            <div id="serviceCompanyContainer"></div>
                        </tags:nameValue2>
                        
                        <cti:displayForPageEditModes modes="EDIT,VIEW">
                            <tags:selectNameValue nameKey=".warehouse" path="warehouseId"  itemLabel="warehouseName" itemValue="warehouseID" items="${warehouses}"  defaultItemValue="0" defaultItemLabel="${noneSelectOption}"/>
                        </cti:displayForPageEditModes>
                        
                        <tags:textareaNameValue nameKey=".installNotes" path="installNotes" rows="4" cols="20" />
                        
                    </tags:nameValueContainer2>
                    
                    <br>
                    
                    <cti:displayForPageEditModes modes="EDIT,VIEW">
                    
                        <tags:boxContainer2 nameKey="deviceStatusHistory">
                            <c:choose>
                                <c:when test="${empty deviceStatusHistory}">
                                    <i:inline key=".deviceStatusHistory.none"/>
                                </c:when>
                                <c:otherwise>
                                    <div class="historyContainer">
                                        <table class="compactResultsTable">
                                            <tr>
                                                <th><i:inline key=".deviceStatusHistory.event"/></th>
                                                <th><i:inline key=".deviceStatusHistory.userName"/></th>
                                                <th><i:inline key=".deviceStatusHistory.timeOfEvent"/></th>
                                            </tr>
                                            <c:forEach var="event" items="${deviceStatusHistory}">
                                                <tr class="<tags:alternateRow odd="" even="altRow"/>">
                                                    <td><spring:escapeBody htmlEscape="true">${event.actionText}</spring:escapeBody></td>
                                                    <td><spring:escapeBody htmlEscape="true">${event.userName}</spring:escapeBody></td>
                                                    <td><cti:formatDate value="${event.eventBase.eventTimestamp}" type="BOTH"/></td>
                                                </tr>
                                            </c:forEach>
                                        </table>
                                    </div>
                                </c:otherwise>
                            </c:choose>
                        </tags:boxContainer2>
                        
                        <br>
                        <tags:boxContainer2 nameKey="hardwareHistory">
                            <c:choose>
                                <c:when test="${empty hardwareHistory}">
                                    <i:inline key=".hardwareHistory.none"/>
                                </c:when>
                                <c:otherwise>
                                    <div class="historyContainer">
                                        <tags:alternateRowReset/>
                                        <table class="compactResultsTable">
                                            <tr>
                                                <th><i:inline key=".hardwareHistory.date"/></th>
                                                <th><i:inline key=".hardwareHistory.action"/></th>
                                            </tr>
                                            
                                            <c:forEach var="event" items="${hardwareHistory}">
                                                <tr class="<tags:alternateRow odd="" even="altRow"/>">
                                                    <td><cti:formatDate value="${event.date}" type="BOTH"/></td>
                                                    <td><spring:escapeBody htmlEscape="true">${event.action}</spring:escapeBody></td>
                                                </tr>
                                            </c:forEach>
                                            
                                        </table>
                                    </div>
                                </c:otherwise>
                            </c:choose>
                        </tags:boxContainer2>
                    
                    </cti:displayForPageEditModes>
                    
                </tags:formElementContainer>
            
            </cti:dataGridCell>
            
        </cti:dataGrid>
        
        <br>
        <br>
        
        <cti:displayForPageEditModes modes="EDIT,CREATE">
            <cti:checkRolesAndProperties value="OPERATOR_ALLOW_ACCOUNT_EDITING">
                <tags:slowInput2 formId="updateForm" key="save" />
            </cti:checkRolesAndProperties>
            <cti:checkRolesAndProperties value="OPERATOR_ALLOW_ACCOUNT_EDITING">
                <cti:displayForPageEditModes modes="EDIT">
                    <input type="button" class="formSubmit" onclick="showDeletePopup()" value="<cti:msg2 key="yukon.web.components.slowInput.delete.label"/>"/>
                </cti:displayForPageEditModes>
                <cti:displayForPageEditModes modes="CREATE,EDIT">
                    <input type="submit" class="formSubmit" id="cancelButton" name="cancel" value="<cti:msg2 key="yukon.web.components.slowInput.cancel.label"/>">
                </cti:displayForPageEditModes>
            </cti:checkRolesAndProperties>
        </cti:displayForPageEditModes>
        
    </form:form>
    
</cti:standardPage>