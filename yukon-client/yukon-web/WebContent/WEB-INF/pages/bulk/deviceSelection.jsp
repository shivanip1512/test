<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti"%>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<cti:msg var="title" key="yukon.common.device.bulk.deviceSelection.title" />
<cti:standardPage title="${title}" module="amr">
<cti:standardMenu/>

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

        function selectDevicesByGroup(groupName) {
            $('groupName').value = groupName;
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
    

    <cti:msg key="yukon.common.device.bulk.deviceSelection.header" /><br><br>

	<cti:msg key="yukon.common.device.bulk.deviceSelection.cancel" var="cancel" />

	<form id="selectDevicesForm" method="post" action="/spring/bulk/bulkActions">
	    <input type="hidden" id="deviceIds" name="idList.ids" />
	    <input type="hidden" name="collectionType" value="idList" />
	</form>
	<cti:multiPaoPicker paoIdField="deviceIds" finalTriggerAction="selectDevices" selectionLinkName="Select Devices">
        <span title="<cti:msg key="yukon.common.device.bulk.deviceSelection.selectDevicesTitle" />">
            <cti:msg key="yukon.common.device.bulk.deviceSelection.selectDevices" />
        </span>
	</cti:multiPaoPicker>
	
	<br/><br/>
	
    <a title="<cti:msg key="yukon.common.device.bulk.deviceSelection.selectDevicesByGroupTitle" />" href="javascript:divToggle('selectDevicesByGroup');">
		<cti:msg key="yukon.common.device.bulk.deviceSelection.selectDevicesByGroup" />
    </a>
	<div id="selectDevicesByGroup" class="popUpDiv" style="width: 350px; display: none; background-color: white; border: 1px solid black;padding: 10px 10px;">
	    <div style="width: 100%; text-align: right;margin-bottom: 10px;">
	        <a href="javascript:divToggle('selectDevicesByGroup');">${cancel}</a>
	    </div>
	    <div>
			<cti:msg key="yukon.common.device.bulk.deviceSelection.selectGroup" />
	    </div>
	    <form id="selectDevicesByGroupForm" method="post" action="/spring/bulk/bulkActions">
	        <tags:groupSelect groupList="${groups}" onSelect="selectDevicesByGroup"/>
	        <input type="hidden" id="groupName" name="group.name" />
	        <input type="hidden" name="collectionType" value="group" />
	    </form>
	</div>
	
	<br/><br/>
	
    <a title="<cti:msg key="yukon.common.device.bulk.deviceSelection.selectDevicesByAddressTitle" />" href="javascript:divToggle('selectDevicesByAddress');">
		<cti:msg key="yukon.common.device.bulk.deviceSelection.selectDevicesByAddress" />
    </a>
	<div id="selectDevicesByAddress" class="popUpDiv" style="width: 350px; display: none; background-color: white; border: 1px solid black;padding: 10px 10px;">
	    <div style="width: 100%; text-align: right;margin-bottom: 10px;">
	        <a href="javascript:divToggle('selectDevicesByAddress');">${cancel}</a>
	    </div>
	    <div style="font-weight: bold;margin-bottom: 5px;">
			<cti:msg key="yukon.common.device.bulk.deviceSelection.selectAddress" />
	    </div>
	    <form id="addByAddressForm" method="post" action="/spring/bulk/bulkActions">
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
		</form>	
	</div>
	
	<br/><br/>
	
    <a title="<cti:msg key="yukon.common.device.bulk.deviceSelection.selectDevicesByFileTitle" />" href="javascript:divToggle('selectDevicesByFile');">
		<cti:msg key="yukon.common.device.bulk.deviceSelection.selectDevicesByFile" />
    </a>
	<div id="selectDevicesByFile" class="popUpDiv" style="width: 350px; display: none; background-color: white; border: 1px solid black;padding: 10px 10px;">
	    <div style="width: 100%; text-align: right;margin-bottom: 10px;">
	        <a href="javascript:divToggle('selectDevicesByFile');">${cancel}</a>
	    </div>
	   <form method="post" action="/spring/bulk/bulkActions" enctype="multipart/form-data">
			<cti:msg key="yukon.common.device.bulk.deviceSelection.selectDataFileType" />
	        <select id="uploadType" name="uploadType" onchange="updateFileNote()">
	            <option value="ADDRESS">
                    <cti:msg key="yukon.common.device.bulk.deviceSelection.dataFileAddress" />
	            </option>
	            <option value="PAONAME">
                    <cti:msg key="yukon.common.device.bulk.deviceSelection.dataFileName" />
                </option>
	            <option value="METERNUMBER">
                    <cti:msg key="yukon.common.device.bulk.deviceSelection.dataFileMeterNumber" />
                </option>
                <option value="BULK">
                    <cti:msg key="yukon.common.device.bulk.deviceSelection.dataFileBulk" />
                </option>
	        </select>
	        
	        <br/><br/>
			
			<cti:msg key="yukon.common.device.bulk.deviceSelection.selectDataFile" />
	        <input type="file" id="dataFile" name="dataFile" size="40" /><br/>
	        <span id="fileNote" style="font-size: .7em; color: blue;">Note: The file must contain 1 valid Physical Address per line.</span>
	        <br/><br/>
	        <input type="submit" name="fileSubmit" value="Select Devices" />
	    </form>
	</div>	
	
	
	
	
	
	
		

</cti:standardPage>