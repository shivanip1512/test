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
                <span id="nameAlreadyExists" style="display:none;"><cti:msg2 key=".createTwoWay.error.nameAlreadyExists"/></span>
                <span id="mustSupplyName" style="display:none;"><cti:msg2 key=".createTwoWay.error.mustSupplyName"/></span>
                <span id="unknown" style="display:none;"><cti:msg2 key=".createTwoWay.error.unknown"/></span>
            </div>
    </i:simplePopup>
    
    <!-- Delete Hardware Popup -->
    <i:simplePopup titleKey=".delete" id="deleteHardwarePopup" arguments="${hardwareDto.displayName}">
        <form id="deleteForm" action="/spring/stars/operator/hardware/deleteHardware" method="post">
            <input type="hidden" name="inventoryId" value="${inventoryId}">
            <input type="hidden" name="accountId" value="${accountId}">
            <input type="hidden" name="energyCompanyId" value="${energyCompanyId}">
            <c:choose>
                <c:when test="${hardwareDto.mct}">
                    <c:set var="deleteMsgKeySuffix" value="DeviceName"/>
                </c:when>
                <c:otherwise>
                    <c:set var="deleteMsgKeySuffix" value="SerialNumber"/>
                </c:otherwise>
            </c:choose>
            <cti:msg2 key=".delete.deleteMessage${deleteMsgKeySuffix}" argument="${hardwareDto.displayName}"/>
            <br><br>
            <input type="radio" name="deleteOption" value="remove" checked="checked"><span class="radioLabel"><cti:msg2 key=".delete.option1"/></span>
            <br>
            <input type="radio" name="deleteOption" value="delete"><span class="radioLabel"><cti:msg2 key=".delete.option2"/></span>
            <br><br>
            <cti:checkRolesAndProperties value="OPERATOR_ALLOW_ACCOUNT_EDITING">
                <tags:slowInput2 myFormId="deleteForm" key="delete" width="80px"/>
            </cti:checkRolesAndProperties>
            <input type="button" style="width:80px;" onclick="hideDeletePopup()" value="<cti:msg2 key=".delete.cancelButton"/>"/>
        </form>
    </i:simplePopup>
    
    <cti:msg2 key=".noneSelectOption" var="noneSelectOption"/>
    
    <form:form id="updateForm" commandName="hardwareDto" action="/spring/stars/operator/hardware/updateHardware">
    
        <input type="hidden" name="accountId" value="${accountId}">
        <input type="hidden" name="inventoryId" value="${inventoryId}">
        <form:hidden path="mct"/>
        <form:hidden path="twoWayLcr"/>
        <form:hidden path="energyCompanyId"/>
        <form:hidden path="displayType"/>
        <form:hidden path="displayName"/>
        <form:hidden path="twoWayDeviceName"/>
        
        <cti:dataGrid cols="2" rowStyle="vertical-align:top;" cellStyle="padding-right:20px;width:50%;" tableClasses="widgetColumns">
        
            <%-- DEVICE INFO --%>
            <cti:dataGridCell>
            
                <tags:sectionContainer2 key="deviceInfoSection">
                    
                    <tags:nameValueContainer2>
                    
                        <tags:nameValue2 nameKey=".displayType"><spring:escapeBody htmlEscape="true">${hardwareDto.displayType}</spring:escapeBody></tags:nameValue2>
                        
                        <c:choose>
                            <c:when test="${hardwareDto.mct}">
                                <%-- For Mct's, show device name instead of serial number --%>
                                <tags:nameValue2 nameKey=".displayName"><spring:escapeBody htmlEscape="true">${hardwareDto.displayName}</spring:escapeBody></tags:nameValue2>
                            </c:when>
                            <c:otherwise>
                                <%-- For switchs and tstat's, show serial number --%>
                                <c:choose>
                                    <%-- If they don't have the inventory checking role property, let them edit the serial number --%>
                                    <c:when test="${inventoryChecking}">
                                        <form:hidden path="serialNumber"/>
                                        <tags:nameValue2 nameKey=".serialNumber"><spring:escapeBody htmlEscape="true">${hardwareDto.serialNumber}</spring:escapeBody></tags:nameValue2>
                                    </c:when>
                                    <c:otherwise>
                                        <tags:inputNameValue nameKey=".serialNumber" path="serialNumber"></tags:inputNameValue>
                                    </c:otherwise>
                                </c:choose>
                            </c:otherwise>
                        </c:choose>
                        
                        <tags:inputNameValue nameKey=".displayLabel" path="displayLabel"/>
                        
                        <tags:inputNameValue nameKey=".altTrackingLabel" path="altTrackingNumber"/>
                        
                        <tags:yukonListEntrySelectNameValue nameKey=".voltageLabel" path="voltageEntryId" accountId="${accountId}" listName="DEVICE_VOLTAGE" defaultItemValue="0" defaultItemLabel="(none)"/>
                        
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
                        
                        <c:if test="${not hardwareDto.mct}">
                            <tags:selectNameValue nameKey=".routeLabel" path="routeId"  itemLabel="paoName" itemValue="yukonID" items="${routes}"  defaultItemValue="0" defaultItemLabel="${defaultRoute}"/>
                        </c:if>
                        
                        <tags:yukonListEntrySelectNameValue nameKey=".deviceStatusLabel" path="deviceStatusEntryId" accountId="${accountId}" listName="DEVICE_STATUS" defaultItemValue="0" defaultItemLabel="(none)"/>
                        
                        <form:hidden path="originalDeviceStatusEntryId"/>
                        
                        <c:if test="${hardwareDto.twoWayLcr}">
                            
                            <%-- Two Way LCR's Yukon Device --%>
                            <tags:nameValue2 nameKey=".twoWayDeviceLabel">
                                <span id="chosenYukonDeviceNameField">
                                    <c:choose>
                                        <c:when test="${hardwareDto.deviceId > 0}">
                                            <spring:escapeBody htmlEscape="true">${hardwareDto.twoWayDeviceName}</spring:escapeBody>
                                        </c:when>
                                        <c:otherwise>
                                            <cti:msg2 key=".noTwoWayDeviceName"/>
                                        </c:otherwise>
                                    </c:choose>
                                </span> 
                                <tags:pickerDialog type="twoWayLcrPicker" id="twoWayLcrPicker" asButton="true" immediateSelectMode="true"
                                    destinationFieldId="chosenYukonDeviceId" 
                                    destinationFieldName="chosenYukonDeviceId"
                                    endAction="changeTwoWayDeviceName" styleClass="newDevice"><cti:msg2 key=".twoWayPickerButton"/></tags:pickerDialog>
                                <input type="button" value="<cti:msg2 key=".twoWayNewButton"/>" onclick="showDeviceCreationPopup();">
                                
                                <tags:hidden path="deviceId" id="chosenYukonDeviceId"/>
                            </tags:nameValue2>
                        
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
                        <tags:selectNameValue nameKey=".warehouseLabel" path="warehouseId"  itemLabel="warehouseName" itemValue="warehouseID" items="${warehouses}"  defaultItemValue="0" defaultItemLabel="${noneSelectOption}"/>
                        <tags:textareaNameValue nameKey=".installNotesLabel" path="installNotes" rows="4" cols="30" />
                        
                    </tags:nameValueContainer2>
                    
                    <br>
                    <tags:boxContainer2 key="deviceStatusHistory">
                        <c:choose>
                            <c:when test="${empty deviceStatusHistory}">
                                <i:inline key=".deviceStatusHistory.none"/>
                            </c:when>
                            <c:otherwise>
                                <div class="historyContainer">
                                    <table class="compactResultsTable">
                                        <tr>
                                            <th><cti:msg2 key=".deviceStatusHistory.tableHeader.event"/></th>
                                            <th><cti:msg2 key=".deviceStatusHistory.tableHeader.userName"/></th>
                                            <th><cti:msg2 key=".deviceStatusHistory.tableHeader.timeOfEvent"/></th>
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
                                            <th><cti:msg2 key=".hardwareHistory.tableHeader.date"/></th>
                                            <th><cti:msg2 key=".hardwareHistory.tableHeader.action"/></th>
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
            <input type="button" class="formSubmit" onclick="showDeletePopup()" value="<cti:msg2 key=".delete.deleteButton"/>"/>
        </cti:checkRolesAndProperties>
    </form:form>
    
</cti:standardPage>