<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>


	<br>
	<select id="selectionTypeSelect" style="width:300px;">
		<option value="loadGroups">Load Groups</option>
		<option value="loadPrograms">Load Programs</option>
		<option value="fieldInstallDate">Field Install Date</option>
		<option value="programSignupDate">Program Sign-Up Date</option>
		<option value="deviceType">Device Type</option>
		<option value="serialNumberRange">Serial Number Range</option>
		<option value="enrolled">Enrolled</option>
	</select>
	
	<br><br>
	<tags:sectionContainer title="Parameters" styleClass="">
	
		<div id="loadGroupsDiv" >
			<span class="subtleGray"><i>4 Load Groups Selected</i></span>
   			<a href="javascript:void(0);" title=""  id="chooseGroupIcon_${uniqueId}" style="text-decoration:none;">	
				<img src="/WebConfig/yukon/Icons/database_add.png">
			</a>
		</div>
		
		<div id="loadProgramsDiv" style="display:none;">
			<span class="subtleGray"><i>2 Load Programs Selected</i></span>
   			<a href="javascript:void(0);" title=""  id="chooseGroupIcon_${uniqueId}" style="text-decoration:none;">	
				<img src="/WebConfig/yukon/Icons/database_add.png">
			</a>
		</div>
		
		<div id="fieldInstallDateDiv"  style="display:none;">
			<tags:dateInputCalendar fieldName="fieldInstallDate" fieldValue="01/16/2010"/>
		</div>
		
		<div id="programSignupDateDiv" style="display:none;">
			<tags:dateInputCalendar fieldName="programSignupDate" fieldValue="04/15/2008"/>
		</div>
		
		<div id="deviceTypeDiv" style="display:none;">
			<select>
				<option></option>
				<option></option>
				<option></option>
				<option></option>
				<option></option>
			</select>
		</div>
	
	</tags:sectionContainer>
	
		
		
		
		

<br>
<input type="button" value="Add" width="80px">