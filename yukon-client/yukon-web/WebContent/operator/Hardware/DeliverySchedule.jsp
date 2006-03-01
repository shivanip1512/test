<%@ taglib uri="http://java.sun.com/jstl/core" prefix="c" %>
<%@ include file="../Consumer/include/StarsHeader.jsp" %>
<jsp:useBean id="purchaseBean" class="com.cannontech.stars.web.bean.PurchaseBean" scope="session"/>

<cti:standardPage title="Energy Services Operations Center" module="stars" htmlLevel="quirks">
	
	<link rel="stylesheet" href="../../include/PurpleStyles.css" type="text/css">
	<div class="headerbar">
		<%@ include file="include/PurchaseHeaderBar.jsp" %>
	</div>
 	<br clear="all"> 
 	
	<%pageContext.setAttribute("liteEC",liteEC);%>
	<c:set target="${purchaseBean}" property="energyCompany" value="${liteEC}" />
	<%pageContext.setAttribute("currentUser", lYukonUser);%>
	<c:set target="${purchaseBean}" property="currentUser" value="${currentUser}" />
 	
	<div class="standardpurplesidebox"> 
	</div>

	<div class="standardcentralwhitebody">
		<div align="center"> <br>
            <% if (errorMsg != null) out.write("<span class=\"ErrorMsg\">* " + errorMsg + "</span><br>"); %>
            <% if (confirmMsg != null) out.write("<span class=\"ConfirmMsg\">* " + confirmMsg + "</span><br>"); %>
    		<br clear="all">
    	</div>
    	
		<form name="PForm" method="post" action="<%=request.getContextPath()%>/servlet/InventoryManager" onsubmit="return prepareSubmit(this)">
	    	<input type="hidden" name="action" value="DeliveryScheduleChange"> 
	    	<div align="center">
	    		<span class="TitleHeader">DELIVERY SCHEDULE</span>
			</div>
			<br>
			<br>
			<table width="500" border="1" cellspacing="0" cellpadding="0" align="center">
        		<tr> 
              		<td class="HeaderCell">Delivery Schedule Data</td>
            	</tr>
        	</table>
			<table width="500" border="1" cellspacing="0" cellpadding="5" align="center">
	        	<tr>
		        	<td width="20%" class="TableCell"> 
		            	<div align="right">Schedule Name:</div>
		                </td>
		            <td width="80%"> 
		                <input type="text" name="name" maxlength="30" size="24" value='<c:out value="${purchaseBean.currentSchedule.scheduleName}"/>' onchange="setContentChanged(true)">
		            </td>
		        </tr>
	            <tr> 
	                <td width="20%" class="TableCell"> 
	                  <div align="right">Model Type:</div>
	                </td>
	                <td width="80%"> 
                  	    <select id="modelType" name="modelType" size="1" style="width:170">
                        	<c:forEach var="deviceEntry" items="${purchaseBean.availableDeviceTypes.yukonListEntries}">
								<option value='<c:out value="${deviceEntry.entryID}"/>'> <c:out value="${deviceEntry.entryText}"/> </option>
							</c:forEach>
						</select>
	                </td>
	          	</tr>
	          	<tr> 
	                <td width="20%" class="TableCell"> 
	                  <div align="right">Filing:</div>
	                </td>
	                <td width="80%"> 
	                  <div align="left" class="fieldinfo">This schedule filed under plan <c:out value="${purchaseBean.currentPlan.planName}"/></div>
	                </td>
		     	</tr>
	        </table>
	        <br>
	        <br>
		    <div id="onlyavailableaftercreation">
		        <table width="500" border="1" cellspacing="0" cellpadding="0" align="center">
	        		<tr> 
	              		<td class="HeaderCell">Delivery Time Projections</td>
	            	</tr>
	        	</table>
	        	<table width="500" border="1" cellspacing="0" cellpadding="5" align="center">
	        		<tr>
	        			<td width="20%">
							<div align="right">
								<INPUT type="Button" name="editTime" value="Edit" onclick="loadPeriod(this.form)" />
								<INPUT type="Button" name="deleteTime" value="Remove" onclick="removePeriod()"/>
							</div>
						</td> 
						<td width="60%" valign="top" class="TableCell"><br>
		                    <div align="center">
		                    	<select id="times" name="times" size="5" style="width:250">
		                        	<c:forEach var="time" items="${purchaseBean.availableTimePeriods}">
										<option value='<c:out value="${time.timePeriodID}"/>'> <c:out value="${time.timePeriodName}"/> -- <c:out value="${time.quantity}"/> assets expected</option>
									</c:forEach>
								</select>
							</div>
	            		</td>
	                    <td width="20%">
	            			<div align="left">
	            				<INPUT type="Button" name="newAssignedTime" value="New" onclick="createNewPeriod(this.form)" />
	            			</div>
	            		</td>
	            	</tr>
				</table>
		        <br>
		        <br>
		        <table width="500" border="1" cellspacing="0" cellpadding="0" align="center">
	        		<tr> 
	              		<td class="HeaderCell">Assigned Shipments</td>
	            	</tr>
	        	</table>
	        	<table width="500" border="1" cellspacing="0" cellpadding="5" align="center">
	        		<tr>
	        			<td width="20%">
							<div align="right">
								<INPUT type="Button" name="editShipment" value="Edit" onclick="loadShipment(this.form)" />
								<INPUT type="Button" name="deleteShipment" value="Remove" onclick="removeShipment()"/>
							</div>
						</td> 
						<td width="60%" valign="top" class="TableCell"><br>
		                    <div align="center">
		                    	<select id="shipments" name="shipments" size="5" style="width:250">
		                        	<c:forEach var="sched" items="${purchaseBean.availableSchedules}">
										<option value='<c:out value="${sched.scheduleID}"/>'> <c:out value="${sched.scheduleName}"/> </option>
									</c:forEach>
								</select>
							</div>
	            		</td>
	                    <td width="20%">
	            			<div align="left">
	            				<INPUT type="Button" name="newAssignedShipment" value="New" onclick="createNewShipment(this.form)" />
	            			</div>
	            		</td>
	            	</tr>
				</table>
			</div> 
			<br>
			<br>
			<table width="500" border="0" cellspacing="0" cellpadding="5" align="center">
	        	<tr>
	           		<td width="20%"></td>
	           		<td width="40%">  
						<input type="submit" name="Submit" value="Submit">
					</td>
	            	<td width="40%">
	                	<input type="reset" name="Reset" value="Reset" onclick="location.reload()">
	                  	<input type="button" name="Back" value="Back" onclick="back(this.form)">
	              	</td>
	          	</tr>
			</table> 
        </form>
    </div>
    
    <script language="JavaScript">
		function init() 
		{
			<c:if test="${purchaseBean.currentSchedule.scheduleID == null}">
				document.getElementById("onlyavailableaftercreation").style.display = "none";
			</c:if>
		}
		
		function validate(form) 
		{
			if (form.name.value == "") 
			{
					alert("Schedule name cannot be empty");
					return false;
			}
			
			return true;
		}
		
		function createNewPeriod(form)
		{
			form.action.value = "RequestNewTimePeriod"; 
			form.submit();			
		}
		
		function loadPeriod(form)
		{
			var assignList = document.getElementById("times");
									
			if (assignList.selectedIndex >= 0) 
			{
				form.action.value = "LoadTimePeriod";
				form.submit();
			}
		}
		
		function removePeriod()
		{
			var assignList = document.getElementById("times");
			var idx = assignList.selectedIndex;
									
			if (assignList.selectedIndex >= 0) 
			{
				assignList.remove(idx);
				setContentChanged(true);
			}
		}
		
		function createNewShipment(form)
		{
			form.action.value = "RequestNewShipment"; 
			form.submit();			
		}
		
		function loadShipment(form)
		{
			var assignList = document.getElementById("shipments");
									
			if (assignList.selectedIndex >= 0) 
			{
				form.action.value = "LoadShipment";
				form.submit();
			}
		}
		
		function removeShipment()
		{
			var assignList = document.getElementById("shipments");
									
			if (assignList.selectedIndex >= 0) 
			{
				assignList.remove(idx);
				setContentChanged(true);
			}
		}
		
		function back(form)
		{
			if (warnUnsavedChanges()) location.href='PurchaseTrack.jsp'
		}
		
		function prepareSubmit(form) 
		{
			var assignedTimePeriods = document.getElementById("times").options;
			var assignedShipments = document.getElementById("shipments").options;
			
			for (idx = 0; idx < assignedTimePeriods.length; idx++) 
			{
				var html = '<input type="hidden" name="times" value="' + assignedTimePeriods[idx].value + '">';
				form.insertAdjacentHTML("beforeEnd", html);
			}
			for (idx = 0; idx < assignedShipments.length; idx++) 
			{
				var html = '<input type="hidden" name="shipments" value="' + assignedShipments[idx].value + '">';
				form.insertAdjacentHTML("beforeEnd", html);
			}
			
			return validate(form);
		}
	
	</script>
</cti:standardPage>          
