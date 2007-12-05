<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>

<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<cti:standardPage title="Assign Configuration to Devices" module="amr">
<cti:standardMenu />
   	
   	<cti:breadCrumbs>
	    <cti:crumbLink url="/operator/Operations.jsp" title="Operations Home" />
	    <cti:crumbLink url="/spring/deviceConfiguration?home" title="Device Configuration Home" />
	    &gt; Assign Configuration to Devices
	</cti:breadCrumbs>
	
	<script type="text/javascript">
		
		function assignDevicesByAddress() {
		
			var startAddress = $F('startRange');
			var endAddress = parseInt($F('endRange'));
		
			if(startAddress == null || startAddress == '' || (startAddress != parseInt(startAddress))) {
				alert('Please enter an integer Start of Range value');
				$('startRange').focus();
			} else if(endAddress == null || endAddress == '' || (endAddress != parseInt(endAddress))) {
				alert('Please enter an integer End of Range value');
				$('endRange').focus();
			} else if(endAddress <= startAddress) {
				alert('Please enter an End of Range value that is greater than the Start of Range value');
				$('endRange').focus();
			} else {
				return true;
			}
			
			return false;
		}
		
		function assignDevices(devices) {
		
			var ids = '';
			$(devices).each(function(device){
				ids += device.paoId + ',';
			});

			$('deviceIds').value = ids;

			$('assignDevicesForm').submit();
		
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
		
		function assignDevicesByFile(){
		
			var dataFile = $F('dataFile');
		
			if(dataFile == null || dataFile == '') {
				alert('Please select a file with valid data to upload.');
			} else {
				return true;
			}
			
			$('dataFile').focus();
			
			return false;
		}
		
		function assignConfigByGroup(group) {
		
			$('groupName').value = group;
			$('assignConfigByGroupForm').submit();
			
		}
		
		function unassignConfig(){
			var confirmRemove = confirm("Are you sure you want to unassign this config?");
			return confirmRemove
		}
		
		function showGroupPopup(){
			$('assignConfigByGroup').toggle();
		}
		
	</script>
	
	<h2>Device Configuration: ${configuration.name}</h2>
	
	<c:if test="${not empty param.errorMessage}">
		<div style="color: red;">
			${param.errorMessage}
		</div>
		<br/><br/>
	</c:if>
	
	<div style="float: left; margin-right: .75em; margin-bottom: .5em; width: 450px;">	
		<tags:boxContainer title="Devices Assigned this Configuration" hideEnabled="false">
			<div style="overflow: auto; height: 200px;">
				<table style="width: 95%;" >
					<c:choose>
						<c:when test="${fn:length(deviceList) > 0}">
							<c:forEach var="device" items="${deviceList}">
								<tr class="<tags:alternateRow odd="" even="altRow"/>">
									<td style="border: none;">
										<cti:deviceName deviceId="${device.deviceId}" />
									</td>
									<td style="border: none; width: 15px;text-align: center;">
										
									<cti:uniqueIdentifier prefix="groupHierarchy_" var="thisId"/>
									<form style="display: inline;" id="${unassignId}unassignForm" action="/spring/deviceConfiguration" method="post">
										<input type="hidden" name="deviceId" value="${device.deviceId}" />
										<input type="hidden" id="configuration" name="configuration" value="${configuration.id}" />
										<input name="unassignConfigForDevice" type="image" alt="Unassign Configuration" title="Unassign Configuration" class="cssicon" src="<c:url value="/WebConfig/yukon/Icons/clearbits/close.gif"/>" onClick="return unassignConfig()" />
									</form>
									</td>
								</tr>
							</c:forEach>
						</c:when>
						<c:otherwise>
							<tr>
								<td colspan="2">No Devices are assigned this configuration</td>
							</tr>
						</c:otherwise>
					</c:choose>	
				</table>
			</div>
		</tags:boxContainer>
	</div>
	<br style="clear: both;"/>
	
	<div style="float: left; margin-right: .75em; margin-bottom: .5em; width: 450px;">	
		<tags:boxContainer title="Assign configuration to multiple devices" hideEnabled="false">
			<form id="assignDevicesForm" method="post" action="/spring/deviceConfiguration">
				<input type="hidden" id="configuration" name="configuration" value="${configuration.id}" />
				<input type="hidden" id="deviceIds" name="deviceIds" />
				<input type="hidden" name="assignConfigToDevices" />
			</form>
			<cti:multiPaoPicker 
				pickerId="devicePickerId" 
				paoIdField="deviceIds" 
				constraint="${configuration.type.criteria.name}" 
				finalTriggerAction="assignDevices" 
				selectionLinkName="Assign Config to Devices"
				excludeIds="${selectedDeviceIds}">
				
				<span title="Click to select devices to assign">Select Devices</span>
			</cti:multiPaoPicker>
			
			<br/><br/>
			
			<a title="Click to assign config to devices by a group" href="javascript:showGroupPopup();">Assign configuration by Group</a>
				<div id="assignConfigByGroup" class="popUpDiv" style="width: 350px; display: none; background-color: white; border: 1px solid black;padding: 10px 10px;">
					<div style="width: 100%; text-align: right;margin-bottom: 10px;">
						<a href="javascript:showGroupPopup('editGroupNameDiv');">cancel</a>
					</div>
					<div>
						Select Group to assign configuration to:
					</div>
					<form id="assignConfigByGroupForm" method="post" action="/spring/deviceConfiguration">
						<input type="hidden" name="configuration" value="${configuration.id}" />
						<tags:groupSelect groupList="${groups}" onSelect="assignConfigByGroup"/>
						<input type="hidden" id="groupName" name="groupName" />
						<input type="hidden" name="assignConfigByGroup" />
					</form>
				</div>
			
		</tags:boxContainer>
	</div>
	<br style="clear: both;"/>
	
	
	<div style="float: left; margin-right: .75em; margin-bottom: .5em; width: 450px;">	
		<tags:boxContainer title="Assign configuration to multiple devices by physical address range" hideEnabled="false">
			<form id="addByAddressForm" method="post" action="/spring/deviceConfiguration">
				<input type="hidden" id="configuration" name="configuration" value="${configuration.id}" />
				Start of Range: <input type="text" id="startRange" name="startRange" />
				<br/><br/>
				End of Range: <input type="text" id="endRange" name="endRange" />
				<br/><br/>
				<input type="submit" name="assignConfigByAddress" value="Assign Configuration" onclick="return assignDevicesByAddress();" />
			</form>
		</tags:boxContainer>
	</div>
	<br style="clear: both;"/>
	
	<div style="float: left; margin-right: .75em; margin-bottom: .5em; width: 700px;">	
		<tags:boxContainer title="Assign configuration to multiple devices by file upload" hideEnabled="false">
			<div>
				<form method="post" action="/spring/deviceConfiguration" enctype="multipart/form-data" onsubmit="return assignDevicesByFile()">
					<input type="hidden" id="configuration" name="configuration" value="${configuration.id}" />
					Select the type of data included in the upload file:
					<select id="uploadType" name="uploadType" onchange="updateFileNote()">
						<option value="ADDRESS">Physical Address</option>
						<option value="PAONAME">Device Name</option>
						<option value="METERNUMBER">Meter Number</option>
						<option value="BULK">Bulk Importer File</option>
					</select>
					<br/><br/>
					Select the file containing the data:
					<input type="file" id="dataFile" name="dataFile" size="40" /><br/>
					<span id="fileNote" style="font-size: .7em; color: blue;">Note: The file must contain 1 valid Physical Address per line.</span>
					<br/><br/>
					<input type="submit" name="assignConfigByFileUpload" value="Assign Configuration" onclick="return assignDevicesByFile();" />
				</form>
			</div>
		</tags:boxContainer>
	</div>
	<br style="clear: both;"/>
	
</cti:standardPage>