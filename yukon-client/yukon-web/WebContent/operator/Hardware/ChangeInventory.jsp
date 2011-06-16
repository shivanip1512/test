<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ include file="../Consumer/include/StarsHeader.jsp" %>
<jsp:useBean id="manipBean" class="com.cannontech.stars.web.bean.ManipulationBean" scope="session"/>
<jsp:useBean id="inventoryBean" class="com.cannontech.stars.web.bean.InventoryBean" scope="session"/>

<cti:standardPage title="Energy Services Operations Center" module="stars" htmlLevel="quirks">
	
	<cti:includeCss link="/include/PurpleStyles.css"/>
	<div class="headerbar">
		<%@ include file="include/HeaderBar.jspf" %>
	</div>
 	<br clear="all"> 
 	
	<%pageContext.setAttribute("liteEC",liteEC);%>
	<c:set target="${manipBean}" property="energyCompany" value="${liteEC}" />
	<%pageContext.setAttribute("currentUser", lYukonUser);%>
	<c:set target="${manipBean}" property="currentUser" value="${currentUser}" />
 	
	<div class="standardpurplesidebox"> 
		<% String pageName = "ChangeSet.jsp"; %>
		<%@ include file="include/Nav.jspf" %>
	</div>

	<div class="standardcentralwhitebody">
		<div align="center"> <br>
            <% String header = "APPLY ACTIONS TO INVENTORY"; %>
            <%@ include file="include/SearchBar.jspf" %>
            <% if (errorMsg != null) out.write("<span class=\"ErrorMsg\">* " + errorMsg + "</span><br>"); %>
            <% if (confirmMsg != null) out.write("<span class=\"ConfirmMsg\">* " + confirmMsg + "</span><br>"); %>
    		<br clear="all">
    	</div>
    	
		<form name="MForm" method="post" action="<%=request.getContextPath()%>/servlet/InventoryManager" onsubmit="prepareSubmit(this)">
	    	<input type="hidden" name="action" value="ApplyActions">
	    	<table width="600" border="1" cellspacing="0" cellpadding="0" align="center">
	        	<tr> 
	              	<td width="55" class="HeaderCell">Actions</td>
	               	<td width="85"> 
	                </td>
	                <td width="200"> 
	                	<select name="actionType" size="1" style="width: 200px" onChange="changeActionType(this.value)" >
							<option value="1"> Change Device Type </option>
	                    	<option value="2"> Change Device Status </option>
	                    	<option value="3"> Move to Service Company </option>
	                    	<option value="4"> Move to Warehouse </option>
						</select>
	            	</td>
	            	<td width="240">
	                	<div id="1"> 
	                    	<select id="11" name="11" size="1" style="width: 200px" onChange="selectAction(this.value)">
	                            <c:forEach var="deviceTypeEntry" items="${manipBean.availableDeviceTypes.yukonListEntries}">
                                    <c:if test="${!inventoryBean.unsupportedDeviceTypes[deviceTypeEntry.yukonDefID]}">
									    <option value="${deviceTypeEntry.entryID}">
                                            <spring:escapeBody>${deviceTypeEntry.entryText}</spring:escapeBody>
                                        </option>
                                    </c:if>
								</c:forEach>
	                      	</select>
	                    </div>
	                	<div id="2" style="display:none"> 
	                    	<select id="21" name="21" size="1" style="width: 200px" onChange="selectAction(this.value)">
	                            <c:forEach var="deviceStatusEntry" items="${manipBean.availableDeviceStates.yukonListEntries}">
									<option value='<c:out value="${deviceStatusEntry.entryID}"/>'> <c:out value="${deviceStatusEntry.entryText}"/> </option>
								</c:forEach>
	                      	</select>
	                    </div>
	                    <div id="3" style="display:none" > 
	                    	<select id="31" name="31" size="1" style="width: 200px" onChange="selectAction(this.value)">
	                            <option value="0" selected/>(none)</option>
                                <c:forEach var="serviceCompany" items="${manipBean.availableServiceCompanies}">
									<option value='<c:out value="${serviceCompany.liteID}"/>'> <c:out value="${serviceCompany.companyName}"/> </option>
								</c:forEach>
	                      	</select>
	                    </div>
	                   	<div id="4" style="display:none" > 
	                    	<select id="41" name="41" size="1" style="width: 200px" onChange="selectAction(this.value)">
	                            <option value="0" selected/>(none)</option> 
                                <c:forEach var="warehouse" items="${manipBean.availableWarehouses}">
									<option value='<c:out value="${warehouse.warehouseID}"/>'> <c:out value="${warehouse.warehouseName}"/> </option>
								</c:forEach>
	                      	</select>
	                    </div>
	                </td>
	           	</tr>
			</table>
			<br>
			<div align="center"> 
            	<input type="button" name="Save" value="Add Action to List" onclick="saveEntry(this.form)">
            </div>
			<br>
			<table width="600" border="1" cellspacing="0" cellpadding="0" align="center">
	        	<tr> 
	              	<td width="55" class="HeaderCell">Current Actions</td>
	                <td width="285"> 
	                	<select name="AssignedActions" size="7" style="width: 293px">
	                    </select>
	            	</td>
	            	<td width="240"> 
						<input type="button" name="Remove" value="Remove" style="width:80" onclick="deleteEntry(this.form)">
                        <br>
                        <input type="button" name="RemoveAll" value="Remove All" style="width:80" onclick="deleteAllEntries(this.form)">
	                </td>
	           	</tr>
        	</table>
        	<br>
        	<table width="600" border="0" cellspacing="0" cellpadding="5" align="center">
            	<tr>
                	<td width="320" class="headeremphasis"> 
                    	<c:out value="${inventoryBean.numberOfRecordsSelected}"/>
						record(s) selected from inventory.
                  	</td>
                  	<td width="205"> 
                    	<input type="reset" name="Reset" value="Reset" onclick="location.reload()">
                  	</td>
                  	<td width="75" align="right"> 
                    	<input type="button" name="Back" value="Back" onclick="if (warnUnsavedChanges()) location.href='Inventory.jsp'">
                  	</td>
              	</tr>
              	<tr>
					<td width="100%" class="headeremphasis">  
						<input type="submit" name="Submit" value="Apply to Selected Inventory">
					</td>
				</tr>
                <tr>
                    <td class="headeremphasis" style="font-size: 10px;">
                        NOTE:  Manipulating MCTs, Digi Gateways and UtilityPRO ZigBee devices is
                        not yet supported and any devices of these types will be ignored.
                    </td>
                </tr>
        	</table>
        	<br>
        </form>
    </div>
    
    <script language="JavaScript">
		var actionTexts = new Array();
		var selectionIDs = new Array();
		var actionTypeIDs = new Array();
		var curIdx = 0;
		var selectedActionType = "1";
		var selectedAction = ""
		var selectedActionID = "0";
		
		function init()
		{
			selectedAction = "Change Device Type: ";
			selectedAction += '<c:out value="${manipBean.defaultActionSelection.entryText}"/>';
			selectedActionID = '<c:out value="${manipBean.defaultActionSelection.entryID}"/>';
		}
		
		function changeActionType(action) 
		{
			selectedActionType = action;
			var type = document.MForm.actionType;
			document.getElementById("1").style.display = "none";
			document.getElementById("2").style.display = "none";
			document.getElementById("3").style.display = "none";
			document.getElementById("4").style.display = "none";
 			document.getElementById(action).style.display = "";
			action += 1;
			selectedAction = type.options[type.selectedIndex].text;  
			selectedAction += ": ";
			selectedAction += document.getElementById(action).options[0].text;
			selectedActionID = document.getElementById(action).options[0].value;
		}
		
		function selectAction(actionID)
		{
			selectedActionID = actionID;
			var type = document.MForm.actionType;
			var actionBy = selectedActionType;
			actionBy += 1;
			var action = document.getElementById(actionBy);
			selectedAction = type.options[type.selectedIndex].text;  
			selectedAction += ": ";
			selectedAction += action.options[action.selectedIndex].text;
		}
		
		function saveEntry(form) 
		{
			var actions = form.AssignedActions;
			var oOption = document.createElement("OPTION");
			oOption.text = selectedAction;
			oOption.value = selectedAction;
			actions.options.add(oOption, curIdx);
			actions.selectedIndex = curIdx;
			actionTexts[curIdx] = selectedAction;
			selectionIDs[curIdx] = selectedActionID;
			actionTypeIDs[curIdx] = selectedActionType;
			curIdx = actionTexts.length;

			setContentChanged(true);
		}
		
		function deleteEntry(form) 
		{
			var actions = form.AssignedActions;
			var idx = actions.selectedIndex;
			if (idx >= 0 && idx < actions.options.length) 
			{
				actions.options.remove(idx);
				actionTexts.splice(idx, 1);
				selectionIDs.splice(idx, 1);
				actionTypeIDs.splice(idx, 1);
				actions.selectedIndex = actions.options.length;
				setContentChanged(true);
			}
			
			curIdx = actionTexts.length;
		}
		
		function deleteAllEntries(form) 
		{
			var actions = form.AssignedActions;
			if (actions.options.length > 1) 
			{
				if (!confirm("Are you sure you want to remove all actions?")) return;
				for (idx = actions.options.length; idx >= 0; idx--)
					actions.options.remove(idx);
				actionTexts.splice(0, actionTexts.length);
				selectionIDs.splice(idx, selectionIDs.length);
				actionTypeIDs.splice(idx, actionTypeIDs.length);
				actions.selectedIndex = 0;
				setContentChanged(true);
			}
			
			curIdx = actionTexts.length;
		}
		
		function prepareSubmit(form) 
		{
			for (idx = 0; idx < actionTexts.length; idx++) 
			{
				var html = '<input type="hidden" name="SelectionIDs" value="' + selectionIDs[idx] + '">';
				form.insertAdjacentHTML("beforeEnd", html);
				html = '<input type="hidden" name="ActionTexts" value="' + actionTexts[idx] + '">';
				form.insertAdjacentHTML("beforeEnd", html);
				html = '<input type="hidden" name="ActionTypeIDs" value="' + actionTypeIDs[idx] + '">';
				form.insertAdjacentHTML("beforeEnd", html);
			}
		}
	</script>
</cti:standardPage>          
