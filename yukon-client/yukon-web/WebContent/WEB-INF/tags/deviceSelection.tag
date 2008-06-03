<%@ tag  dynamic-attributes="extraInputs" %>
<%@ attribute name="action" required="true" type="java.lang.String"%>
<%@ attribute name="groupDataJson" required="true" type="java.lang.String"%>

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
        
        function updateFileNote(){
        
            var selection = $F('uploadType');
            var fileNote = $('fileNote');
            
            if(selection == 'ADDRESS') {
                fileNote.update('Note: The file must contain 1 valid Physical Address per line.');
            } else if(selection == 'PAONAME') {
                fileNote.update('Note: The file must contain 1 valid Device Name per line.');
            } else if(selection == 'DEVICEID') {
                fileNote.update('Note: The file must contain 1 valid Device Id per line.');
            } else if(selection == 'METERNUMBER') {
                fileNote.update('Note: The file must contain 1 valid Meter number per line.');
            } else if(selection == 'BULK') {
                fileNote.update('Note: The file must be a valid Bulk Importer upload file.');
            }
        }
        
        function divToggle(theDiv) {
            $(theDiv).toggle();
        }
        
    </script>
    
	<cti:msg key="yukon.common.device.bulk.deviceSelection.cancel" var="cancel" />

    <table cellspacing="10">
    

    <%-- DEVICES --%>
    <tr>
    
        <td>
            <form id="selectDevicesForm" method="post" action="${action}">
                <input type="hidden" id="deviceIds" name="idList.ids" />
                <input type="hidden" name="collectionType" value="idList" />
                <tags:mapToHiddenInputs values="${extraInputs}"/>
            </form>
            
            <cti:msg var="selectDevicesLabel" key="yukon.common.device.bulk.deviceSelection.selectDevices" />
            <cti:multiPaoPicker paoIdField="deviceIds" finalTriggerAction="selectDevices" selectionLinkName="Select Devices" asButton="true">${selectDevicesLabel}</cti:multiPaoPicker>
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
            
            
            <form id="selectDevicesByGroupForm" method="post" action="${action}">
                
                <input type="hidden" name="collectionType" value="group" />
                <tags:mapToHiddenInputs values="${extraInputs}"/>
            
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
            <cti:msg var="selectAddressLabel" key="yukon.common.device.bulk.deviceSelection.selectDevicesByAddress" />
            <input type="button" id="addressButton" value="${selectAddressLabel}" onclick="javascript:divToggle('selectDevicesByAddress');" style="width:140px;"/>
            
            <div id="selectDevicesByAddress" class="popUpDiv" style="width: 350px; display: none; background-color: white; border: 1px solid black;padding: 10px 10px;">
                <div style="width: 100%; text-align: right;margin-bottom: 10px;">
                    <a href="javascript:divToggle('selectDevicesByAddress');">${cancel}</a>
                </div>
                <div style="font-weight: bold;margin-bottom: 5px;">
                    <cti:msg key="yukon.common.device.bulk.deviceSelection.selectAddress" />
                </div>
                <form id="addByAddressForm" method="post" action="${action}">
                
                    <input type="hidden" name="collectionType" value="addressRange" />
                    
                    <tags:nameValueContainer>
                        <tags:nameValue name="Start of Range">
                            <input type="text" id="startRange" name="addressRange.start" />
                        </tags:nameValue>
                        <tags:nameValue name="End of Range">
                            <input type="text" id="endRange" name="addressRange.end" />
                        </tags:nameValue>
                    </tags:nameValueContainer>
                    <input type="submit" name="addressSubmit" value="Select Devices" onclick="return selectDevicesByAddress();" />
                    <tags:mapToHiddenInputs values="${extraInputs}"/>
                </form> 
            </div>
        
        </td>
        
        <td>
            <cti:msg key="yukon.common.device.bulk.deviceSelection.selectDevicesByAddressTitle" />
        </td>
    
    </tr>
    
    <%-- FILE UPLOAD --%>
    <tr>
        <td>
            <cti:msg var="selectFileLabel" key="yukon.common.device.bulk.deviceSelection.selectDevicesByFile" />
            <input type="button" id="fileButton" value="${selectFileLabel}" onclick="javascript:divToggle('selectDevicesByFile');" style="width:140px;"/>
        
            <div id="selectDevicesByFile" class="popUpDiv" style="width: 350px; display: none; background-color: white; border: 1px solid black;padding: 10px 10px;">
                <div style="width: 100%; text-align: right;margin-bottom: 10px;">
                    <a href="javascript:divToggle('selectDevicesByFile');">${cancel}</a>
                </div>
               <form method="post" action="${action}" enctype="multipart/form-data">
               
                    <input type="hidden" name="collectionType" value="fileUpload" />
                    <cti:msg key="yukon.common.device.bulk.deviceSelection.selectDataFileType" />
                    <select id="uploadType" name="fileUpload.uploadType" onchange="updateFileNote()">
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
                    
                    <br/><br/>
                    
                    <cti:msg key="yukon.common.device.bulk.deviceSelection.selectDataFile" />
                    <input type="file" id="fileUpload.dataFile" name="fileUpload.dataFile" size="40" /><br/>
                    <span id="fileNote" style="font-size: .7em; color: blue;">Note: The file must contain 1 valid Physical Address per line.</span>
                    <br/><br/>
                    <input type="submit" name="fileSubmit" value="Select Devices" />
                    <tags:mapToHiddenInputs values="${extraInputs}"/>
                </form>
            </div>
        
        </td>
        
        <td>
            <cti:msg key="yukon.common.device.bulk.deviceSelection.selectDevicesByFileTitle" />
        </td>
        
    </td>
 </tr>
 </table>
   
