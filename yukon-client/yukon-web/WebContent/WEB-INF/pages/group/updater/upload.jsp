<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<cti:standardPage title="Device Group Updater" module="amr">

    <cti:standardMenu menuSelection="devicegroups|updater"/>

   	<cti:breadCrumbs>
	   <cti:crumbLink url="/operator/Operations.jsp" title="Operations Home" />
	   &gt; File Upload
  	</cti:breadCrumbs>
        
	<tags:boxContainer title="File Upload" id="updaterContainer" hideEnabled="false">
	
		<table>
			<tr valign="top">
				<td width="30%" style="padding-right:20px;">
	
					<form id="uploadForm" method="post" action="/spring/group/updater/parseUpload" enctype="multipart/form-data">
					
						<%-- file select --%>
			            <div class="normalBoldLabel" style="display:inline;">Update File:</div>
			            <input type="file" name="dataFile" size="30px">
			            <tags:slowInput myFormId="uploadForm" label="Process" labelBusy="Processing" />
					
					</form>
					
					<br>
					<c:choose>
						<c:when test="${not empty error}">
							<div class="errorRed">${error}</div>
						</c:when>
						<c:when test="${success}">
							<div class="okGreen">Successfully updated device groups.</div>
						</c:when>
					</c:choose>
				
				</td>
				
				<%-- INSTRUCTIONS --%>
                <td>
                
                    <div class="normalBoldLabel">Instructions:</div>
                    <ul style="font-size:11px;">
                        <li>Files must contain a header row with valid column identifiers as listed in the table below.</li><br>
						<li>The first column is always the Identifier Column.<br>It will be used to determine which device is to be updated.</li><br>
						<li>One of the following column identifiers must be used in the Identifier Column:<br><span style="font-weight:bold;">ADDRESS, METER_NUMBER, NAME, or DEVICE_ID.</span></li><br>
						<li>All column headers (besides the Identifier column) should be of the form<br>
							<b>DEVICE_GROUP_PREFIX:prefix=&lt;<i>Group Name</i>&gt;</b> or <b>DEVICE_GROUP_SET:group=&lt;<i>Group Name</i>&gt;</b><br>
							Descriptions of the two column header types are listed in the table below.
						</li><br>
						<li>Rows are processed top to bottom, columns are processed left to right.</li><br>
						<li>Groups will be automatically be created if they do not yet exist.</li>
                    </ul>
                    
                    <%-- sample files --%>
                    <div class="small">
                        <div class="normalBoldLabel" style="display:inline;"><cti:msg key="yukon.common.device.bulk.importUpload.sampleFilesLabel"/>:</div>
                        <a href="<cti:url value="/WebConfig/custom/sample_bulk_files/Sample_DeviceGroup_Update_File1.csv"/>">File 1</a>, 
                        <a href="<cti:url value="/WebConfig/custom/sample_bulk_files/Sample_DeviceGroup_Update_File2.csv"/>">File 2</a>, 
                        <a href="<cti:url value="/WebConfig/custom/sample_bulk_files/Sample_DeviceGroup_Update_File3.csv"/>">File 3</a>
                    </div>
                    
                </td>
              </tr>
                
            <tr>
                <td></td>
                <td style="padding-top:20px;">
                
                	<table class="miniResultsTable">
                		<tr >
                			<th style="width:20%;">Column Header</th>
                			<th>Description</th>
                		</tr>
                		
                		<tr>
                			<td class="normalBoldLabel">DEVICE_GROUP_PREFIX</td>
                			<td>The full path of the group to be prefixed to the group name value in each row.<br><br>
                				Example: If the header is <b>DEVICE_GROUP_PREFIX:prefix=/Meter/Collection</b>, and the value in the row is <b>A</b>, then the device will be added to <b>/Meters/Collection/A</b>.<br><br>
                				In addition, the device will be removed from all other groups under <b>/Meters/Collection<b>.<br><br>
                			</td>
                		</tr>
                		
                		<tr>
                			<td class="normalBoldLabel">DEVICE_GROUP_SET</td>
                			<td>The full path of a group to either add or remove the device from.<br><br>
                				Example: If the header is <b>DEVICE_GROUP_SET:group=/Meters/Extra</b>, and the value in the row is <b>true</b>, then the device will be added to the group. If the value is <b>false</b>, it will be removed from the group.<br><br>
                			</td>
                		</tr>
                	</table>
                	
                </td>
            </tr>
				
		</table>
	
	</tags:boxContainer>
	
</cti:standardPage>