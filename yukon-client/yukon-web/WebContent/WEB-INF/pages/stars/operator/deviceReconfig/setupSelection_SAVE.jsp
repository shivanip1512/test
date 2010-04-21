<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<cti:msg var="pageTitle" key="yukon.web.modules.dr.deviceReconfig.setupSelection.pageTitle"/>
<cti:msg var="deviceSelectionSectionTitle" key="yukon.web.modules.dr.deviceReconfig.setupSelection.deviceSelectionSectionTitle"/>
<cti:msg var="helpImg" key="yukon.web.modules.dr.deviceReconfig.setupSelection.help.img"/>

<cti:url var="add" value="/WebConfig/yukon/Icons/add.gif"/>
<cti:url var="addOver" value="/WebConfig/yukon/Icons/add_over.gif"/>
<cti:url var="delete" value="/WebConfig/yukon/Icons/delete.gif"/>
<cti:url var="deleteOver" value="/WebConfig/yukon/Icons/delete_over.gif"/>


<cti:standardPage title="${pageTitle}" module="dr">

    <cti:breadCrumbs>
	    <cti:crumbLink url="/operator/Operations.jsp" title="Operations Home"  />
	    <cti:crumbLink url="/spring/stars/operator/deviceReconfig/home" title="Device Reconfiguration" />
	    <cti:crumbLink url="/spring/stars/operator/deviceReconfig/chooseSelectionType" title="Device Selection Type" />
	    <cti:crumbLink>${pageTitle}</cti:crumbLink>
	</cti:breadCrumbs>
	
	<cti:standardMenu menuSelection="blank"/>

    <h2>${pageTitle}</h2>
    <br>
    
    <cti:includeScript link="/JavaScript/deviceReconfig.js"/>
    <cti:includeScript link="/JavaScript/simpleDialog.js"/>
    
    <tags:simpleDialog id="addRuleDialog"/>
    
    <script type="text/javascript">
    </script>
    
    <style>
    	table.selectionTable td {vertical-align:top;}
    	table.selectionTable td.selectionTypeCol {width:200px;}
    	table.selectionTable td.deviceCountCol {text-align:right;padding-right:20px;}
    	table.selectionTable th.deviceCountCol {text-align:right;padding-right:20px;}
    	table.reconfigStyleTable td {vertical-align:top;}
    </style>

    <%-- ERRORS --%>
    <c:if test="${not empty errors}">
    	<tags:errorMessages/>
    	<br>
    </c:if>
    
    <%-- MISC FORMS --%>
    <form id="cancelForm" action="/spring/stars/operator/deviceReconfig/home" method="get"></form>
    
    <form id="deviceReconfigForm" action="/spring/stars/operator/deviceReconfig/chooseOperation" method="post">
    
    	<%--
    	<tags:boxContainer title="Instructions">
    	
    		<div style="font-size:11px;">
    	
	    		<b>1) Device Selection</b>
	    		<br>
	    		<div style="padding-left:20px;">
	    				&bull; File Upload: Upload a CSV formatted file containing serial number, device type, and account number.
	    				<br>
	    				&bull; Device Properties: Set parameters. Devices that meet <u>all</u> set parameters will be included.
	    		</div>
	    		<br>
	    		
	    		<b>2) Configuration Options</b>
	    		<br>
	    		<div style="padding-left:20px;">
	    				&bull; Current Configuration: Use the current configuration already saved for each piece of inventory in the database. 
	    				<br>
	    				&bull; Device Properties: Use same configuration for all devices based on a specific load group.
	    		</div>
	    		<br>
	    		
	    		<b>3) Click "Next" button to setup scheduling options.</b>
	    		<br>
    		
    		</div>
    	
    	</tags:boxContainer>
    	<br>
    	 --%>
    	
    	<%--
    	<tags:sectionContainer title="${deviceSelectionSectionTitle}">
    	 --%>
    	 
    		<%-- FILE IMPORT 
    		<div style="padding-bottom:8px;">
	    		<input type="radio" name="deviceSelectionStyle" onclick="$('fileUploadField').disabled = false;" value="IMPORT" id="deviceSelectionStyleByImportRadio" <c:if test="${deviceReconfigOptions.deviceSelectionStyle == 'IMPORT'}">checked</c:if>>
	    		<b>File Upload</b>
    		</div>
   			<input type="file" id="fileUploadField" value="Select File" onclick="checkByImportRadio();">
   			<br><br>
   			--%>
		    
	    	<%-- SELECTION TABLE 
	    	<div style="padding-bottom:8px;">
		    	<input type="radio" name="deviceSelectionStyle" onclick="$('fileUploadField').disabled = true;" value="SELECTION" id="deviceSelectionStyleBySelectionRadio" <c:if test="${deviceReconfigOptions.deviceSelectionStyle == 'SELECTION'}">checked</c:if>>
			    <b>Device Properties</b> 
		    </div>
		    --%>
		    
		    <table class="resultsTable selectionTable">
		    
		    	<tr>
		    		<th colspan="2">Rules</th>
		    		<th style="text-align:center;width:100px;">Remove</th>
		    	</tr>
		    	
		    	<%-- LOAD GROUP --%>
		    	<tr>
		    		<td class="selectionTypeCol">
		    			Load Group
		    		</td>
		    		<td>
		    			<input type="hidden" id="loadGroupPaoIds" name="loadGroupPaoIds" value="">
		    			<span class="subtleGray"><i>4 Load Groups Selected</i></span>
		    			<a href="javascript:void(0);" title="${selectDeviceGroupChooseText}"  id="chooseGroupIcon_${uniqueId}" style="text-decoration:none;">	
							<img src="/WebConfig/yukon/Icons/database_add.gif">
						</a>
						<%--
		    			<cti:multiPaoPicker pickerId="loadGroupPaoIdsPicker" paoIdField="loadGroupPaoIds" asButton="true" finalTriggerAction="loadGroupPaoIdsPickerComplete" constraint="com.cannontech.common.search.criteria.LMGroupCriteria">Select</cti:multiPaoPicker>
		    			 --%>
		    		</td>
		    		<td style="text-align:center;">
						<input type="image" src="${delete}" onmouseover="javascript:this.src='${deleteOver}'" onmouseout="javascript:this.src='${delete}'" name="removeRow${group.order}">
					</td>
		    	</tr>
		    	
		    	<%-- PROGRAM --%>
		    	<tr>
		    		<td class="selectionTypeCol">
		    			Program
		    		</td>
		    		<td>
		    			<input type="hidden" id="loadProgramPaoIds" name="loadProgramPaoIds" value="67">
		    			<span class="subtleGray"><i>Select Programs</i></span>
		    			<a href="javascript:void(0);" title="${selectDeviceGroupChooseText}"  id="chooseGroupIcon_${uniqueId}" style="text-decoration:none;">	
							<img src="/WebConfig/yukon/Icons/database_add.gif">
						</a>	
						<%--
		    			<cti:multiPaoPicker pickerId="loadProgramPaoIdsPicker" paoIdField="loadProgramPaoIds" asButton="true" finalTriggerAction="loadProgramPaoIdsPickerComplete" constraint="com.cannontech.common.search.criteria.LMProgramCriteria">Select</cti:multiPaoPicker>
		    			 --%>
		    		</td>
		    		<td style="text-align:center;">
						<input type="image" src="${delete}" onmouseover="javascript:this.src='${deleteOver}'" onmouseout="javascript:this.src='${delete}'" name="removeRow${group.order}">
					</td>
		    	</tr>
		    	
		    	<%-- FIELD INSTALL DATE --%>
		    	<tr>
		    		<td class="selectionTypeCol">
		    			Field Install Date
		    		</td>
		    		<td>
		    			<cti:formatDate var="fieldInstallDateStr" type="DATE" value="${deviceReconfigOptions.fieldInstallDate}" nullText=""/>
		    			<tags:dateInputCalendar fieldName="fieldInstallDate" fieldValue="${fieldInstallDateStr}"/>
		    		</td>
		    		<td style="text-align:center;">
						<input type="image" src="${delete}" onmouseover="javascript:this.src='${deleteOver}'" onmouseout="javascript:this.src='${delete}'" name="removeRow${group.order}">
					</td>
		    	</tr>
		    	
		    	<%-- PROGRAM SIGNUP DATE --%>
		    	<tr>
		    		<td class="selectionTypeCol">
		    			Program Sign-Up Date
		    		</td>
		    		<td>
		    			<cti:formatDate var="programSignupDateStr" type="DATE" value="${deviceReconfigOptions.programSignupDate}" nullText=""/>
		    			<tags:dateInputCalendar fieldName="programSignupDate"  fieldValue="${programSignupDateStr}"/>
		    		</td>
		    		<td style="text-align:center;">
						<input type="image" src="${delete}" onmouseover="javascript:this.src='${deleteOver}'" onmouseout="javascript:this.src='${delete}'" name="removeRow${group.order}">
					</td>
		    	</tr>
		    	
		    	<%-- DEVICE TYPE --%>
		    	<tr>
		    		<td class="selectionTypeCol">
		    			Device Type
		    		</td>
		    		<td>
		    			<select name="deviceType">
		    				<c:forEach var="availableDeviceType" items="${availableDeviceTypes}">
		    					<option value="${availableDeviceType.yukonListEntry.yukonDefID}" <c:if test="${deviceReconfigOptions.deviceType == availableDeviceType.yukonListEntry.yukonDefID}">selected</c:if>>${availableDeviceType.name}</option>
		    				</c:forEach>
		    			</select>
		    		</td>
		    		<td style="text-align:center;">
						<input type="image" src="${delete}" onmouseover="javascript:this.src='${deleteOver}'" onmouseout="javascript:this.src='${delete}'" name="removeRow${group.order}">
					</td>
		    	</tr>
		    	
		    	<%-- SERIAL NUMBER RANGE --%>
		    	<tr>
		    		<td class="selectionTypeCol">
		    			Serial Number Range
		    		</td>
		    		<td>
		    			<input type="text" name="serialNumberRangeStart" id="serialNumberRangeStart" maxlength="12" size="12" value="${deviceReconfigOptions.serialNumberRangeStart}">
		    			&nbsp;to&nbsp;
		    			<input type="text" name="serialNumberRangeEnd" id="serialNumberRangeEnd" maxlength="12" size="12"  value="${deviceReconfigOptions.serialNumberRangeEnd}">
		    		</td>
		    		<td style="text-align:center;">
						<input type="image" src="${delete}" onmouseover="javascript:this.src='${deleteOver}'" onmouseout="javascript:this.src='${delete}'" name="removeRow${group.order}">
					</td>
		    	</tr>
		    	
		    	<%-- NOT ENROLLED --%>
		    	<tr>
		    		<td class="selectionTypeCol">
		    			Enrolled
		    		</td>
		    		<td>
		    			<select>
		    				<option>Not Enrolled</option>
		    				<option>Enrolled</option>
		    			</select>
		    			<img src="/WebConfig/yukon/Icons/help.gif">
		    			<%--
		    			<input type="checkbox" name="notEnrolled" id="notEnrolled" <c:if test="${deviceReconfigOptions.notEnrolled}">checked</c:if>> 
		    			 --%>
		    		</td>
		    		<td style="text-align:center;">
						<input type="image" src="${delete}" onmouseover="javascript:this.src='${deleteOver}'" onmouseout="javascript:this.src='${delete}'" name="removeRow${group.order}">
					</td>
		    	</tr>
		    	
		    	<%-- ADD RULE --%>
				<tr style="background-color:#EEE;">
					<td colspan="3">
						<img src="${add}" onclick="openSimpleDialog('addRuleDialog', '/spring/stars/operator/deviceReconfig/addRuleDialog', 'Add Rule')" onmouseover="javascript:this.src='${addOver}'" onmouseout="javascript:this.src='${add}'" name="addRow">
						Add Rule
					</td>
				</tr>
		    	
		    </table>
		    
		    
	    <%--
	    </tags:sectionContainer>
	     --%>
	    
	    <%-- SETUP SCHEDULE BUTTON --%>
    	<br>
    	<tags:slowInput myFormId="deviceReconfigForm" label="Select" />
    
    </form>
    
</cti:standardPage>