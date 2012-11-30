<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<cti:standardPage title="Device Group Updater" module="amr">

    <cti:standardMenu menuSelection="devicegroups|updater"/>

   	<cti:breadCrumbs>
	   <cti:crumbLink url="/operator/Operations.jsp" title="Operations Home" />
	   <cti:crumbLink url="/group/editor/home" title="Device Groups" />
	   &gt; File Upload
  	</cti:breadCrumbs>
        
    <h2>Group File Upload</h2>
    <br>
    
	<tags:boxContainer title="Upload a CSV formatted file containing required column headers for updating or setting device group members." id="updaterContainer" hideEnabled="false">
	
		<table>
			<tr valign="top">
				<td width="30%" style="padding-right:20px;">
	
					<form id="uploadForm" method="post" action="/group/updater/parseUpload" enctype="multipart/form-data">
					
					<%-- options --%>
                    <table>
                        <tr>
                            <td valign="top" class="smallBoldLabel">Options:</td>
                            <td style="font-size:11px;">
                                &nbsp;<label><input type="checkbox" name="createGroups"> Create groups if they don't already exist.</label>
                                 <img src="<cti:url value="/WebConfig/yukon/Icons/help.gif"/>" onclick="$('createGroupsOptionInfoPopup').toggle();">
                                <br>
                            </td>
                        </tr>
                    </table>
                    <br>
                    
                    <tags:simplePopup id="createGroupsOptionInfoPopup" title="Create Groups" onClose="$('createGroupsOptionInfoPopup').toggle();">
					     <br>
					     This option is not checked by default, an error will be displayed if any of the groups do not yet exist.
					     <br><br>
					     Do not use the Create Groups option if you expect the groups to already exists, this will prevent any misspellings from causing unwanted groups to be created.
					     <br><br>
					     When this option is checked, if any of the group names specified in the column headers or in any of the row values does not yet exist, they will be created automatically.
					     <br><br>
					     
					</tags:simplePopup>
					
						<%-- file select --%>
			            <div class="fwb" style="display:inline;">Update File:</div>
			            <input type="file" name="dataFile" size="30px">
			            <tags:slowInput myFormId="uploadForm" label="Process" labelBusy="Processing" />
					
					</form>
					
					<br>
					<c:choose>
						<c:when test="${not empty error}">
							<div class="error">${error}</div>
						</c:when>
						<c:when test="${success}">
							Successfully updated ${deviceCount} meter's device groups.
						</c:when>
					</c:choose>
				
				</td>
				
				<%-- INSTRUCTIONS --%>
                <td>
                
                    <div class="fwb">Instructions:</div>
                    <ul style="font-size:11px;">
                        <li>Files must contain a header row with valid column identifiers as listed in the table below.</li><br>
						<li>The first column is always the Identifier Column.<br>It will be used to determine which device is to be updated.</li><br>
						<li>One of the following column identifiers must be used in the Identifier Column:<br><span style="font-weight:bold;">ADDRESS, METER_NUMBER, NAME, or DEVICE_ID.</span></li><br>
						<li>All column headers (besides the Identifier column) should be of the form<br>
							<b>DEVICE_GROUP_PREFIX:prefix=&lt;<i>Group Name</i>&gt;</b> or <b>DEVICE_GROUP_SET:group=&lt;<i>Group Name</i>&gt;</b><br>
							Descriptions of the two column header types are listed in the table below.
						</li><br>
						<li>Rows are processed top to bottom, columns are processed left to right.</li><br>
						<li>Groups will automatically be created if they do not yet exist.</li>
                    </ul>
                    
                    <%-- sample files --%>
                    <div class="small">
                        <div class="fwb" style="display:inline;"><cti:msg key="yukon.common.device.bulk.importUpload.sampleFilesLabel"/>:</div>
                        <a href="<cti:url value="/WebConfig/custom/sample_bulk_files/Sample_DeviceGroup_Update_File1.csv"/>">File 1</a>, 
                        <a href="<cti:url value="/WebConfig/custom/sample_bulk_files/Sample_DeviceGroup_Update_File2.csv"/>">File 2</a>, 
                        <a href="<cti:url value="/WebConfig/custom/sample_bulk_files/Sample_DeviceGroup_Update_File3.csv"/>">File 3</a>
                    </div>
                    
                </td>
              </tr>
                
            <tr>
                <td></td>
                <td style="padding-top:20px;">
                
                	<cti:url var="check" value="/WebConfig/yukon/Icons/check.gif"/>
                	
                	<table class="resultsTable">
                        <thead>
                    		<tr>
                    			<th style="width:20%;">Column Header</th>
                    			<th>Description</th>
                    			<th>Identifier</th>
                    		</tr>
                		</thead>
                        <tfoot></tfoot>
                        <tbody>
                    		<tr>
                    			<td class="fwb">ADDRESS</td>
                    			<td>
                    				Address of the device.
                    			</td>
                    			<td style="text-align:center;"><img src="${check}"></td>
                    		</tr>
                    		<tr>
                    			<td class="fwb">METER_NUMBER</td>
                    			<td>
                    				Meter number of the device.
                    			</td>
                    			<td style="text-align:center;"><img src="${check}"></td>
                    		</tr>
                    		<tr>
                    			<td class="fwb">NAME</td>
                    			<td>
                    				Name of the device.
                    			</td>
                    			<td style="text-align:center;"><img src="${check}"></td>
                    		</tr>
                    		<tr>
                    			<td class="fwb">DEVICE_ID</td>
                    			<td>
                    				Device ID of the device.
                    			</td>
                    			<td style="text-align:center;"><img src="${check}"></td>
                    		</tr>
                    		
                    		<tr>
                    			<td class="fwb">DEVICE_GROUP_PREFIX</td>
                    			<td>The full path of the group to be prefixed to the group name value in each row.<br><br>
                    				Example: If the header is <b>DEVICE_GROUP_PREFIX:prefix=/Meters/Collection</b>, and the value in the row is <b>A</b>, then the device will be added to <b>/Meters/Collection/A</b>.<br><br>
                    				In addition, the device will be removed from all other groups under <b>/Meters/Collection<b>.<br><br>
                    			</td>
                    			<td></td>
                    		</tr>
                    		
                    		<tr>
                    			<td class="fwb">DEVICE_GROUP_SET</td>
                    			<td>The full path of a group to either add or remove the device from.<br><br>
                    				Example: If the header is <b>DEVICE_GROUP_SET:group=/Meters/Extra</b>, and the value in the row is <b>true</b>, then the device will be added to the group. If the value is <b>false</b>, it will be removed from the group.<br><br>
                    			</td>
                    			<td></td>
                    		</tr>
                        </tbody>
                	</table>
                	
                </td>
            </tr>
				
		</table>
	
	</tags:boxContainer>
	
</cti:standardPage>