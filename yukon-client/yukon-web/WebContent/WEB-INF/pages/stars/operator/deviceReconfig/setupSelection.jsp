<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<cti:msg var="deviceSelectionSectionTitle" key="yukon.web.modules.dr.deviceReconfig.setupSelection.deviceSelectionSectionTitle"/>
<cti:msg var="helpImg" key="yukon.web.modules.dr.deviceReconfig.setupSelection.help.img"/>

<cti:url var="add" value="/WebConfig/yukon/Icons/add.gif"/>
<cti:url var="addOver" value="/WebConfig/yukon/Icons/add_over.gif"/>
<cti:url var="delete" value="/WebConfig/yukon/Icons/delete.gif"/>
<cti:url var="deleteOver" value="/WebConfig/yukon/Icons/delete_over.gif"/>


<cti:standardPage title="Inventory Selection" module="dr">

    <cti:breadCrumbs>
	    <cti:crumbLink url="/operator/Operations.jsp" title="Operations Home"  />
	    <cti:crumbLink url="/spring/stars/operator/deviceReconfig/home" title="Inventory Operations" />
	    <cti:crumbLink title="Inventory Selection" />
	</cti:breadCrumbs>
	
	<cti:standardMenu menuSelection="blank"/>

    <h2>Inventory Selection</h2>
    <br>
    
    <cti:includeScript link="/JavaScript/deviceReconfig.js"/>
    <cti:includeScript link="/JavaScript/simpleDialog.js"/>
    
    <tags:simpleDialog id="addRuleDialog"/>
    
    <script type="text/javascript">
    </script>
    
    <style>
    	table.selectionTable td {vertical-align:top;}
    	table.selectionTable td.selectionTypeCol {width:200px;font-weight:bold;}
    	table.selectionTable td.selectionParamCol {}
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
    
    		<b>
    		Match 
			<select name="compositionType">
				<option value="UNION">At Least One</option>
				<option value="INTERSECTION" selected="selected">Every One</option>
 			</select>
 			of the following rules:
 			</b>
 			<br><br>
    		
		    
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
		    		<td class="selectionParamCol">
		    			4 Load Groups Selected
		    			&nbsp; <img src="/WebConfig/yukon/Icons/edit.gif">
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
		    		<td class="selectionParamCol">
		    			2 Load Programs Selected
		    			&nbsp; <img src="/WebConfig/yukon/Icons/edit.gif">
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
		    		<td class="selectionParamCol">
		    			12/15/2009
		    			&nbsp; <img src="/WebConfig/yukon/Icons/edit.gif">
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
		    		<td class="selectionParamCol">
		    			04/15/2008
		    			&nbsp; <img src="/WebConfig/yukon/Icons/edit.gif">
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
		    		<td class="selectionParamCol">
		    			Residential ExpressStat
		    			&nbsp; <img src="/WebConfig/yukon/Icons/edit.gif">
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
		    		<td class="selectionParamCol">
		    			100880 - 102600
		    			&nbsp; <img src="/WebConfig/yukon/Icons/edit.gif">
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
		    		<td class="selectionParamCol">
		    			Devices not enrolled in any program.
		    			&nbsp; <img src="/WebConfig/yukon/Icons/edit.gif">
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
    	<tags:slowInput myFormId="deviceReconfigForm" label="Select" width="80px"/>
    
    </form>
    
</cti:standardPage>