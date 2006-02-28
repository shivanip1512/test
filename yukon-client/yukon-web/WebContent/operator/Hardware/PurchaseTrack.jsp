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
	    	<input type="hidden" name="action" value="PurchaseChange"> 
	    	<div align="center">
	    		<span class="TitleHeader">PURCHASE PLAN</span>
			</div>
			<br>
			<br>
			<table id="edittopsection" align="center" width="500" border="0" cellspacing="0" cellpadding="1" class="TableCell">
		    	<tr> 
					<td width="30%" class="SubtitleHeader">Current Plan:</td>
					<td width="60%">
						<select id="plans" name="plans" onchange="setContentChanged(true)">
							<c:forEach var="plan" items="${purchaseBean.availablePlans}">
								<option value='<c:out value="${plan.purchaseID}"/>'> <c:out value="${plan.planName}"/> </option>
							</c:forEach>
						</select>
						<INPUT id="load" type="Button" name="load" value="Load Plan" onclick="loadPlan(this.form)"/>
					</td>
					<td width="10%">
						<INPUT type="Button" name="new" value="New Plan" onclick="createNew(this.form)" />
					</td>
				</tr>
			</table>
			<div id="newtopsection" align="center">
	    		<span class="ConfirmMsg">Please create a new purchase plan below.</span>
	    	</div>
			<br>
			<br>
			<table width="500" border="1" cellspacing="0" cellpadding="0" align="center">
        		<tr> 
              		<td class="HeaderCell">Purchase Plan Data</td>
            	</tr>
        	</table>
			<table width="500" border="1" cellspacing="0" cellpadding="5" align="center">
	        	<tr>
		        	<td width="20%" class="TableCell"> 
		            	<div align="right">Plan Name:</div>
		                </td>
		            <td width="80%"> 
		                <input type="text" name="name" maxlength="30" size="24" value='<c:out value="${purchaseBean.currentPlan.planName}"/>' onchange="setContentChanged(true)">
		            </td>
		        </tr>
	            <tr> 
	                <td width="20%" class="TableCell"> 
	                  <div align="right">PO Number:</div>
	                </td>
	                <td width="80%"> 
	                  <input type="text" name="poNumber" maxlength="40" size="24" value='<c:out value="${purchaseBean.currentPlan.poDesignation}"/>' onchange="setContentChanged(true)">
	                </td>
	          	</tr>
	          	<tr> 
	                <td width="20%" class="TableCell"> 
	                  <div align="right">Accounting Code:</div>
	                </td>
	                <td width="80%"> 
	                  <input type="text" name="accountingCode" maxlength="40" size="24" value='<c:out value="${purchaseBean.currentPlan.accountingCode}"/>' onchange="setContentChanged(true)">
	                </td>
	          	</tr>
	          	<tr> 
	                <td width="20%" class="TableCell"> 
	                  <div align="right">Created on:</div>
	                </td>
	                <td width="80%"> 
	                  <div align="left"><c:out value="${purchaseBean.currentPlan.timePeriod}"/></div>
	                </td>
		     	</tr>
	        </table>
	        <br>
	        <br>
	        <table width="500" border="1" cellspacing="0" cellpadding="0" align="center">
        		<tr> 
              		<td class="HeaderCell">Assigned Delivery Schedules</td>
            	</tr>
        	</table>
        	<table width="500" border="1" cellspacing="0" cellpadding="5" align="center">
        		<tr>
        			<td width="20%">
						<div align="right">
							<INPUT type="Button" name="edit" value="Edit" />
							<INPUT type="Button" name="delete" value="Delete" />
						</div>
					</td> 
					<td width="60%" valign="top" class="TableCell"><br>
	                    <div align="center">
	                    	<select id="assigned" name="assigned" size="5" style="width:250">
	                        	<c:forEach var="sched" items="${purchaseBean.currentPlan.deliverySchedules}">
									<option value='<c:out value="${sched.scheduleID}"/>'> <c:out value="${sched.scheduleName}"/> </option>
								</c:forEach>
							</select>
						</div>
            		</td>
                    <td width="20%">
            			<div align="left">
            				<INPUT type="Button" name="newAvailable" value="New" />
            			</div>
            		</td>
            	</tr>
			</table>
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
			document.getElementById("newtopsection").style.display = "none";
			
			<c:if test="${purchaseBean.currentPlan.purchaseID == null}">
				document.getElementById("edittopsection").style.display = "none";
				document.getElementById("newtopsection").style.display = "";
			</c:if>
		}
		
		function validate(form) 
		{
			if (form.name.value == "") 
			{
					alert("Plan name cannot be empty");
					return false;
			}
			
			return true;
		}
		
		function createNew(form)
		{
			form.action.value = "RequestNewPurchasePlan"; 
			form.submit();			
		}
		
		function loadPlan(form)
		{
			form.action.value = "LoadPurchasePlan"; 
			form.submit();	
		}
		
		function back(form)
		{
			<c:if test="${purchaseBean.currentPlan.purchaseID == null}">
				<c:if test="${purchaseBean.numberOfPlans > 0}">
					loadPlan(form);
				</c:if>
			</c:if>
			<c:if test="${purchaseBean.currentPlan.purchaseID != null}">
				if (warnUnsavedChanges()) location.href='../Operations.jsp'
			</c:if>
		}
		
		function prepareSubmit(form) 
		{
			var assignedSchedules = document.getElementById("assigned").options;
			
			for (idx = 0; idx < assignedSchedules.length; idx++) 
			{
				var html = '<input type="hidden" name="schedules" value="' + assignedSchedules[idx].value + '">';
				form.insertAdjacentHTML("beforeEnd", html);
			}
			
			return validate(form);
		}
	
	</script>
</cti:standardPage>          
