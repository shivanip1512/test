<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>

<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<cti:standardPage title="Add multiple devices to group" module="amr">
<cti:standardMenu menuSelection="devicegroups|commander"/>
   	
   	<c:url var="homeUrl" value="/spring/group/home">
		<c:param name="groupName" value="${group.fullName}" />
	</c:url>
   	
   	<cti:breadCrumbs>
	    <cti:crumbLink url="/operator/Operations.jsp" title="Operations Home" />
	    <cti:crumbLink url="${homeUrl}" title="Groups Home" />
	    &gt; Add Devices by file
	</cti:breadCrumbs>
	
	<script type="text/javascript">
	
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
		
		function addDevicesByFile(){
		
			var dataFile = $F('dataFile');
		
			if(dataFile == null || dataFile == '') {
				alert('Please select a file with valid data to upload.');
			} else {
				return true;
			}
			
			$('dataFile').focus();
			
			return false;
		}
	
	</script>
	
	<h2>Group: <a href="${homeUrl}">${fn:escapeXml(group.fullName)}</a></h2>
	
	<c:if test="${not empty param.errorMessage}">
		<div style="color: red">
			${param.errorMessage}
		</div>
	</c:if>

	<div style="width: 700px">
		<tags:boxContainer title="Add Multiple Devices by file" hideEnabled="false">
			<div>
				<form method="post" action="/spring/group/addDevicesByFile" enctype="multipart/form-data" onsubmit="return addDevicesByFile()">
					<input type="hidden" name="groupName" value="${fn:escapeXml(group.fullName)}" />
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
					<input type="submit" name="submit" value="Add Devices" onclick="return addDevicesByFile();" />
				</form>
			</div>
		</tags:boxContainer>
	
	</div>
	
</cti:standardPage>