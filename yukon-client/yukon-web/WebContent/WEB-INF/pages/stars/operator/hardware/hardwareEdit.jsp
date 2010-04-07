<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>

<cti:standardPage module="operator" page="hardwareEdit">
    <cti:includeCss link="/WebConfig/yukon/styles/operator/hardware.css"/>

    <script type="text/javascript">
        function changeTwoWayDeviceName(selectedItems) {
            var selectedPao = selectedItems[0];
            var nameField = $('chosenYukonDeviceNameField');
            nameField.innerHTML = selectedPao.paoName;
            new Effect.Highlight(nameField , {'duration': 3.5, 'startcolor': '#FFE900'});
            return true;
        }

        function showDeviceCreationPopup() {
        	$('createTwoWayDeviceName').value = '';
            clearCreationErrors();
            $('createDevicePopup').show();
        }

        function clearCreationErrors() {
        	var errorSpans = $$('div#creationErrors span');
            for(i = 0; i < errorSpans.length; i++){
                errorSpans[i].hide();
            }
        }

        function showDeletePopup() {
            $('deleteHardwarePopup').show();
        }

        function hideDeletePopup() {
        	$('deleteHardwarePopup').hide();
        }

        function updateServiceCompanyInfo() {
            var url = '/spring/stars/operator/hardware/serviceCompanyInfo';
            var serviceCompanyId = $F('serviceCompanyId');
            if(serviceCompanyId > 0) {
                var params = {'serviceCompanyId' : serviceCompanyId};
                new Ajax.Updater('serviceCompanyContainer', url, {method: 'get', evalScripts: true, parameters: params});
            } else {
                $('serviceCompanyContainer').innerHTML = '';
            }
        }

        function createTwoWayDevice() {
        	clearCreationErrors();
        	var url = '/spring/stars/operator/hardware/createYukonDevice';
            var deviceName = $F('createTwoWayDeviceName');
        	var params = {'accountId' : '${accountId}', 'inventoryId' : '${inventoryId}', 'deviceName' : deviceName};
            
        	new Ajax.Request(url, {
                method: 'post',
                evalScripts: true,
                parameters: params,
                onSuccess: function(resp, json) {
                    if(json.errorOccurred){
                        $(json.errorMsg).show();
                    } else {
                        /* Successfully created two way device */
                        $('chosenYukonDeviceId').value = json.deviceId;
                        $('chosenYukonDeviceNameField').innerHTML = deviceName;
                        $('createTwoWayDeviceName').value = '';
                        $('createDevicePopup').hide();
                        var nameField = $('chosenYukonDeviceNameField');
                        new Effect.Highlight(nameField , {'duration': 3.5, 'startcolor': '#FFE900'});
                    }
                }
            });
        }
        
        Event.observe(window, 'load', updateServiceCompanyInfo);
    </script>
    
    <!-- Create Two Way Device Popup -->
    <i:simplePopup titleKey=".createTwoWay" id="createDevicePopup" styleClass="smallSimplePopup">
            <tags:nameValueContainer2>
                <tags:nameValue2 nameKey=".displayName">
                    <input type="text" name="deviceName" id="createTwoWayDeviceName">
                    <input type="button" value="Create" onclick="createTwoWayDevice()">
                </tags:nameValue2>
            </tags:nameValueContainer2>
            <div id="creationErrors" class="errorMessage">
                <span id="nameAlreadyExists" style="display:none;"><i:inline key=".createTwoWay.error.nameAlreadyExists"/></span>
                <span id="mustSupplyName" style="display:none;"><i:inline key=".createTwoWay.error.mustSupplyName"/></span>
                <span id="unknown" style="display:none;"><i:inline key=".createTwoWay.error.unknown"/></span>
            </div>
    </i:simplePopup>
    
    <!-- Delete Hardware Popup -->
    <i:simplePopup titleKey=".delete" id="deleteHardwarePopup" arguments="${hardwareDto.displayName}">
        <form id="deleteForm" action="/spring/stars/operator/hardware/deleteHardware" method="post">
            <input type="hidden" name="inventoryId" value="${inventoryId}">
            <input type="hidden" name="accountId" value="${accountId}">
            <input type="hidden" name="energyCompanyId" value="${energyCompanyId}">
            <c:choose>
                <c:when test="${hardwareDto.hardwareClass == 'METER'}">
                    <c:set var="deleteMsgKeySuffix" value="DeviceName"/>
                </c:when>
                <c:otherwise>
                    <c:set var="deleteMsgKeySuffix" value="SerialNumber"/>
                </c:otherwise>
            </c:choose>
            <cti:msg2 key=".delete.deleteMessage${deleteMsgKeySuffix}" argument="${hardwareDto.displayName}"/>
            <br><br>
            <input type="radio" name="deleteOption" value="remove" checked="checked"><span class="radioLabel"><i:inline key=".delete.option1"/></span>
            <br>
            <input type="radio" name="deleteOption" value="delete"><span class="radioLabel"><i:inline key=".delete.option2"/></span>
            <br><br>
            <cti:checkRolesAndProperties value="OPERATOR_ALLOW_ACCOUNT_EDITING">
                <tags:slowInput2 myFormId="deleteForm" key="delete" width="80px" />
            </cti:checkRolesAndProperties>
            <input type="button" class="formSubmit" onclick="hideDeletePopup()" value="<cti:msg2 key=".delete.cancelButton"/>"/>
        </form>
    </i:simplePopup>
    
    <cti:msg2 key=".noneSelectOption" var="noneSelectOption"/>
    
    <c:choose>
        <c:when test="${editMode}">
            <c:set var="action" value="/spring/stars/operator/hardware/updateHardware"/>
        </c:when>
        <c:otherwise>
            <c:set var="action" value="/spring/stars/operator/hardware/createHardware"/>
        </c:otherwise>
    </c:choose>
    
    <form:form id="updateForm" commandName="hardwareDto" action="${action}">
    
        <input type="hidden" name="accountId" value="${accountId}">
        <input type="hidden" name="inventoryId" value="${inventoryId}">
        <form:hidden path="energyCompanyId"/>
        <form:hidden path="displayType"/>
        <form:hidden path="displayName"/>
        <form:hidden path="twoWayDeviceName"/>
        <form:hidden path="hardwareType"/>
        
        <cti:dataGrid cols="2" rowStyle="vertical-align:top;" cellStyle="padding-right:20px;width:50%;" tableClasses="widgetColumns">
        
            <%-- DEVICE INFO --%>
            <cti:dataGridCell>
            
                <tags:sectionContainer2 key="deviceInfoSection">
                    
                    <tags:nameValueContainer2>
                    
                        <c:choose>
                            <c:when test="${editMode}">
                                <tags:nameValue2 nameKey=".displayType"><spring:escapeBody htmlEscape="true">${hardwareDto.displayType}</spring:escapeBody></tags:nameValue2>
                            </c:when>
                            <c:otherwise>
                                <tags:selectNameValue nameKey="${displayTypeKey}" path="hardwareTypeEntryId"  itemLabel="displayName" itemValue="hardwareTypeEntryId" items="${deviceTypes}"/>
                            </c:otherwise>
                        </c:choose>
                        
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
                                <c:if test="${editMode}">
                                    <tags:nameValue2 nameKey=".displayName"><spring:escapeBody htmlEscape="true">${hardwareDto.displayName}</spring:escapeBody></tags:nameValue2>
                                </c:if>
                            </c:otherwise>
                            
                        </c:choose>
                        
                        <tags:inputNameValue nameKey=".displayLabel" path="displayLabel"/>
                        
                        <tags:inputNameValue nameKey=".altTrackingLabel" path="altTrackingNumber"/>
                        
                        <tags:yukonListEntrySelectNameValue nameKey=".voltageLabel" path="voltageEntryId" accountId="${accountId}" listName="DEVICE_VOLTAGE"/>
                        
                        <tags:nameValue2 nameKey=".fieldInstallDateLabel">
                            <tags:dateInputCalendar fieldName="fieldInstallDate" fieldValue="fieldInstallDate" springInput="true"></tags:dateInputCalendar>
                        </tags:nameValue2>
                        
                        <tags:nameValue2 nameKey=".fieldReceiveDateLabel">
                            <tags:dateInputCalendar fieldName="fieldReceiveDate" fieldValue="fieldReceiveDate" springInput="true"></tags:dateInputCalendar>
                        </tags:nameValue2>
                        
                        <tags:nameValue2 nameKey=".fieldRemoveDateLabel">
                            <tags:dateInputCalendar fieldName="fieldRemoveDate" fieldValue="fieldRemoveDate" springInput="true"></tags:dateInputCalendar>
                        </tags:nameValue2>
                        
                        <tags:textareaNameValue nameKey=".deviceNotesLabel" path="deviceNotes" rows="4" cols="30" />
                        
                        <c:if test="${showRoute}">
                            <tags:selectNameValue nameKey=".routeLabel" path="routeId"  itemLabel="paoName" itemValue="yukonID" items="${routes}"  defaultItemValue="0" defaultItemLabel="${defaultRoute}"/>
                        </c:if>
                        
                        <c:if test="${editMode}">
                        
                            <tags:yukonListEntrySelectNameValue nameKey=".deviceStatusLabel" path="deviceStatusEntryId" accountId="${accountId}" listName="DEVICE_STATUS" defaultItemValue="0" defaultItemLabel="(none)"/>
                            
                            <form:hidden path="originalDeviceStatusEntryId"/>
                            
                            <c:if test="${showTwoWay}">
                                
                                <%-- Two Way LCR's Yukon Device --%>
                                <tags:nameValue2 nameKey=".twoWayDeviceLabel">
                                    <span id="chosenYukonDeviceNameField">
                                        <c:choose>
                                            <c:when test="${hardwareDto.deviceId > 0}">
                                                <spring:escapeBody htmlEscape="true">${hardwareDto.twoWayDeviceName}</spring:escapeBody>
                                            </c:when>
                                            <c:otherwise>
                                                <i:inline key=".noTwoWayDeviceName"/>
                                            </c:otherwise>
                                        </c:choose>
                                    </span> 
                                    <tags:pickerDialog type="twoWayLcrPicker" id="twoWayLcrPicker" asButton="true" immediateSelectMode="true"
                                        destinationFieldId="chosenYukonDeviceId" 
                                        destinationFieldName="chosenYukonDeviceId"
                                        endAction="changeTwoWayDeviceName" styleClass="newDevice"><cti:msg2 key=".twoWayPickerButton"/></tags:pickerDialog>
                                    <input type="button" value="<cti:msg2 key=".twoWayNewButton"/>" onclick="showDeviceCreationPopup();">
                                    
                                </tags:nameValue2>
                            
                            </c:if>
                            
                            <tags:hidden path="deviceId" id="chosenYukonDeviceId"/>
                            
                        </c:if>
                        
                    </tags:nameValueContainer2>
                
                </tags:sectionContainer2>
            
            </cti:dataGridCell>
            
            <%--SERVICE AND STORAGE --%>
            <cti:dataGridCell>
            
                <tags:sectionContainer2 key="serviceAndStorageSection">
                    
                    <tags:nameValueContainer2>
                    
                        <tags:selectNameValue nameKey=".serviceCompanyLabel" path="serviceCompanyId" itemLabel="serviceCompanyName" itemValue="serviceCompanyId" 
                            items="${serviceCompanies}" defaultItemValue="0" defaultItemLabel="(none)" onchange="updateServiceCompanyInfo()"/>
                        
                        <tags:nameValue2 nameKey=".serviceCompanyInfoLabel">
                            <div id="serviceCompanyContainer"></div>
                        </tags:nameValue2>
                        
                        <c:if test="${editMode}">
                            <tags:selectNameValue nameKey=".warehouseLabel" path="warehouseId"  itemLabel="warehouseName" itemValue="warehouseID" items="${warehouses}"  defaultItemValue="0" defaultItemLabel="${noneSelectOption}"/>
                        </c:if>
                        
                        <tags:textareaNameValue nameKey=".installNotesLabel" path="installNotes" rows="4" cols="30" />
                        
                    </tags:nameValueContainer2>
                    
                    <br>
                    
                    <c:if test="${editMode}">
                    
                        <tags:boxContainer2 key="deviceStatusHistory">
                            <c:choose>
                                <c:when test="${empty deviceStatusHistory}">
                                    <i:inline key=".deviceStatusHistory.none"/>
                                </c:when>
                                <c:otherwise>
                                    <div class="historyContainer">
                                        <table class="compactResultsTable">
                                            <tr>
                                                <th><i:inline key=".deviceStatusHistory.tableHeader.event"/></th>
                                                <th><i:inline key=".deviceStatusHistory.tableHeader.userName"/></th>
                                                <th><i:inline key=".deviceStatusHistory.tableHeader.timeOfEvent"/></th>
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
                        <tags:boxContainer2 key="hardwareHistory">
                            <c:choose>
                                <c:when test="${empty hardwareHistory}">
                                    <i:inline key=".hardwareHistory.none"/>
                                </c:when>
                                <c:otherwise>
                                    <div class="historyContainer">
                                        <tags:alternateRowReset/>
                                        <table class="compactResultsTable">
                                            <tr>
                                                <th><i:inline key=".hardwareHistory.tableHeader.date"/></th>
                                                <th><i:inline key=".hardwareHistory.tableHeader.action"/></th>
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
                    
                    </c:if>
                    
                </tags:sectionContainer2>
            
            </cti:dataGridCell>
            
        </cti:dataGrid>
        
        <br>
        <br>
        
        <cti:checkRolesAndProperties value="OPERATOR_ALLOW_ACCOUNT_EDITING">
            <tags:slowInput2 myFormId="updateForm" key="save" width="80px"/>
        </cti:checkRolesAndProperties>
        <tags:reset/>
        <cti:checkRolesAndProperties value="OPERATOR_ALLOW_ACCOUNT_EDITING">
            <c:choose>
                <c:when test="${editMode}">
                    <input type="button" class="formSubmit" onclick="showDeletePopup()" value="<cti:msg2 key=".delete.deleteButton"/>"/>
                </c:when>
                <c:otherwise>
                    <input type="submit" class="formSubmit" id="cancelButton" name="cancel" value="<cti:msg2 key=".create.cancelButton"/>">
                </c:otherwise>
            </c:choose>
        </cti:checkRolesAndProperties>
    </form:form>
    
</cti:standardPage>