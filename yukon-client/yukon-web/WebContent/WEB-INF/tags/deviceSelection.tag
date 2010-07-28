<%@ tag  dynamic-attributes="extraInputs" %>
<%@ attribute name="action" required="true" type="java.lang.String"%>
<%@ attribute name="groupDataJson" required="true" type="java.lang.String"%>
<%@ attribute name="pickerType" required="true" type="java.lang.String"%>

<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti"%>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="ext" tagdir="/WEB-INF/tags/ext" %>

<c:set var="tree_id" value="bulkDeviceSelectionByGroupTree" />
<cti:msg var="noGroupSelectedAlertText" key="yukon.common.device.bulk.deviceSelection.selectDevicesByGroupTree.noGroupSelectedAlertText" />

    <script type="text/javascript">
    
        function selectDevices(devices) {
            var ids = [];
            
            // Get the id out of each of the selected devices
            for(i=0;i<devices.length;i++){
                ids[i] = devices[i].paoId;
            }
            
            $('deviceIds').value = ids;
            $('selectDevicesForm').submit();
            return true;
        }

        function submitSelectDevicesByGroupForm() {
        
            if ($('group.name').value == '') {
                alert('${noGroupSelectedAlertText}');
                return false;
            }
            $('selectDevicesByGroupForm').submit();
        }

        function selectDevicesByAddress() {
            return true;
        }
        
        function updateFileNote(id){
        
            var selection = $F(id + '_uploadType');
            var fileNote = $(id + '_fileNote');
            
            if(selection == 'ADDRESS') {
                fileNote.update('Note: The file must contain 1 valid Physical Address per line.');
            } else if(selection == 'PAONAME') {
                fileNote.update('Note: The file must contain 1 valid Device Name per line.');
            } else if(selection == 'DEVICEID') {
                fileNote.update('Note: The file must contain 1 valid Device ID per line.');
            } else if(selection == 'METERNUMBER') {
                fileNote.update('Note: The file must contain 1 valid Meter number per line.');
            } else if(selection == 'BULK') {
                fileNote.update('Note: The file must be a valid Bulk Importer upload file.');
            }
        }
        
        function toggleByAddrPopup(id) {
            $(id + '_startRange').value = '';;
            $(id + '_endRange').value = '';
            togglePopup(id);
            if ($(id).visible()) { 
                $(id + '_startRange').focus();
            }
        }
        
        function toggleByFileUploadPopup(id) {
            togglePopup(id);
            if ($(id).visible()) {
                $(id + '_uploadType').options[0].selected = true;
                updateFileNote(id);
                $(id + '_uploadType').focus();
            }
        }
        
        function togglePopup(id) {
            $(id).toggle();
        }
        
    </script>
    
	<cti:msg key="yukon.common.device.bulk.deviceSelection.cancel" var="cancel" />

    <table cellspacing="10">
    

    <%-- DEVICES --%>
    <tr>
    
        <td>
        
            <cti:msg var="SelectDevicesLabel" key="yukon.common.device.bulk.deviceSelection.selectDevices" />

            <input type="button" id="selectDevicesButton" value="${SelectDevicesLabel}" onclick="javascript:selectDevicesPicker.show()" style="width:140px;" />
            

            <form id="selectDevicesForm" method="get" action="${action}">
                <input type="hidden" id="deviceIds" name="idList.ids" />
                <input type="hidden" name="collectionType" value="idList" />
                <tags:mapToHiddenInputs values="${pageScope.extraInputs}"/>
            </form>
            
		    <tags:pickerDialog id="selectDevicesPicker" type="${pageScope.pickerType}"
		        destinationFieldId="deviceIds" multiSelectMode="true"
		        endAction="selectDevices" asButton="false">${selectDevicesLabel}</tags:pickerDialog>
        </td>
        
        <td>
            <cti:msg key="yukon.common.device.bulk.deviceSelection.selectDevicesTitle" />
        </td>
    
    </tr>
    
    <%-- GROUP --%>
    <tr>
        <td>
            
            
            
            
            <cti:msg var="addDeviceTitle" key="yukon.common.device.bulk.deviceSelection.selectDevicesByGroupTitle" />
            <cti:msg var="addDeviceLabel" key="yukon.common.device.bulk.deviceSelection.selectDevicesByGroup" />
            <cti:msg var="rootLabel" key="yukon.common.device.bulk.deviceSelection.selectDevicesByGroupTree.rootLabel" />
            <cti:msg var="submitButtonText" key="yukon.common.device.bulk.deviceSelection.selectDevicesByGroupTree.submitButtonText" />
            <cti:msg var="cancelButtonText" key="yukon.common.device.bulk.deviceSelection.selectDevicesByGroupTree.cancelButtonText" />
            
            <input type="button" id="selectByGroupButton" value="${addDeviceLabel}" style="width:140px;" />
            
            
            <form id="selectDevicesByGroupForm" method="get" action="${action}">
                
                <input type="hidden" name="collectionType" value="group" />
                <tags:mapToHiddenInputs values="${pageScope.extraInputs}"/>
            
                <ext:nodeValueSelectingPopupTree    fieldId="group.name"
                                                    fieldName="group.name"
                                                    nodeValueName="groupName"
                                                    submitButtonText="${submitButtonText}"
                                                    cancelButtonText="${cancelButtonText}"
                                                    submitCallback="submitSelectDevicesByGroupForm();"
                                                    
                                                    id="bulkDeviceSelectionByGroupTree"
                                                    treeAttributes="{}"
                                                    triggerElement="selectByGroupButton"
                                                    dataJson="${groupDataJson}"
                                                    title="${addDeviceTitle}"
                                                    width="432"
                                                    height="600" />
                                
            </form>                    
                                
        </td>
        
        <td>
            ${addDeviceTitle}
        </td>
	
    </tr>
    
	
    <%-- ADDRESS RANGE --%>
    <tr>
        <td>

            <c:set var="byAddrPopupId" value="byAddrPopup" />
            <cti:msg var="selectAddressPopupTitle" key="yukon.common.device.bulk.deviceSelection.selectAddressPopupTitle" />
            <cti:msg var="selectAddressLabel" key="yukon.common.device.bulk.deviceSelection.selectDevicesByAddress" />

            <input type="button" id="addressButton" value="${selectAddressLabel}" onclick="toggleByAddrPopup('${byAddrPopupId}');" style="width:140px;"/>
            
            <tags:simplePopup id="${byAddrPopupId}" title="${selectAddressPopupTitle}" styleClass="deviceSelectionPopup"  onClose="toggleByAddrPopup('${byAddrPopupId}');">
                
                <form id="addByAddressForm" method="get" action="${action}">
                
                    <input type="hidden" name="collectionType" value="addressRange" />
                    
                    <tags:nameValueContainer>
                        <tags:nameValue name="Start of Range">
                            <input type="text" id="${byAddrPopupId}_startRange" name="addressRange.start" />
                        </tags:nameValue>
                        <tags:nameValue name="End of Range">
                            <input type="text" id="${byAddrPopupId}_endRange" name="addressRange.end" />
                        </tags:nameValue>
                    </tags:nameValueContainer>
                    
                    <br>
                    <cti:msg var="selectDevicesPopupButtonText" key="yukon.common.device.bulk.deviceSelection.selectDevicesPopupButtonText" />
                    <tags:slowInput myFormId="addByAddressForm" labelBusy="${selectDevicesPopupButtonText}" label="${selectDevicesPopupButtonText}" />
                    <tags:mapToHiddenInputs values="${pageScope.extraInputs}"/>
                
                </form>
                
            </tags:simplePopup>
        
        </td>
        
        <td>
            <cti:msg key="yukon.common.device.bulk.deviceSelection.selectDevicesByAddressTitle" />
        </td>
    
    </tr>
    
    <%-- FILE UPLOAD --%>
    <tr>
        <td>
            
            <c:set var="byFileUploadId" value="byFileUpload" />
            <cti:msg var="selectDataFilePopupTitle" key="yukon.common.device.bulk.deviceSelection.selectDataFilePopupTitle" />
            <cti:msg var="selectFileLabel" key="yukon.common.device.bulk.deviceSelection.selectDevicesByFile" />
            
            <input type="button" id="fileButton" value="${selectFileLabel}" onclick="toggleByFileUploadPopup('${byFileUploadId}');" style="width:140px;"/>
        
            <tags:simplePopup id="${byFileUploadId}" title="${selectDataFilePopupTitle}" styleClass="deviceSelectionPopup" onClose="toggleByFileUploadPopup('${byFileUploadId}');">
                
                <form id="addByFileUploadForm" method="post" action="${action}" enctype="multipart/form-data">
                
                    <input type="hidden" name="collectionType" value="fileUpload" />
                    
                    <cti:msg var="typeLabel" key="yukon.common.device.bulk.deviceSelection.selectDataFileType" />
                    <cti:msg var="dataFileLabel" key="yukon.common.device.bulk.deviceSelection.selectDataFile" />
                    
                    <tags:nameValueContainer>
                    
                        <tags:nameValue name="${typeLabel}">
                            <select id="${byFileUploadId}_uploadType" name="fileUpload.uploadType" onchange="updateFileNote('${byFileUploadId}')">
                                <option value="ADDRESS">
                                    <cti:msg key="yukon.common.device.bulk.deviceSelection.dataFileAddress" />
                                </option>
                                <option value="PAONAME">
                                    <cti:msg key="yukon.common.device.bulk.deviceSelection.dataFileName" />
                                </option>
                                <option value="METERNUMBER">
                                    <cti:msg key="yukon.common.device.bulk.deviceSelection.dataFileMeterNumber" />
                                </option>
                                <option value="DEVICEID">
                                    <cti:msg key="yukon.common.device.bulk.deviceSelection.dataFileDeviceId" />
                                </option>
                                <option value="BULK">
                                    <cti:msg key="yukon.common.device.bulk.deviceSelection.dataFileBulk" />
                                </option>
                            </select>
                        </tags:nameValue>
                        
                        <tags:nameValue name="${dataFileLabel}">
                            <input type="file" id="fileUpload.dataFile" name="fileUpload.dataFile" size="40" />
                            <br>
                            <span id="${byFileUploadId}_fileNote" style="font-size: .7em; color: blue;">Note: The file must contain 1 valid Physical Address per line.</span>
                        </tags:nameValue>
                        
                    </tags:nameValueContainer>
                    
                    <br>
                    <cti:msg var="selectDevicesPopupButtonText" key="yukon.common.device.bulk.deviceSelection.selectDevicesPopupButtonText" />
                    <tags:slowInput myFormId="addByFileUploadForm" labelBusy="${selectDevicesPopupButtonText}" label="${selectDevicesPopupButtonText}" />
                    <tags:mapToHiddenInputs values="${pageScope.extraInputs}"/>
                    
                
                </form>
                
            </tags:simplePopup>
        
        </td>
        
        <td>
            <cti:msg key="yukon.common.device.bulk.deviceSelection.selectDevicesByFileTitle" />
        </td>
        
    </tr>
 
 </table>
   
