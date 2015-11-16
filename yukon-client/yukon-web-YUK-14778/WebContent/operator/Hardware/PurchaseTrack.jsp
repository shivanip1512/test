
<%-- ---------------------------------------------------------------------- --%>
<%-- These are no longer linked to but they can't be deleted until 1/1/2015 --%>
<%-- ---------------------------------------------------------------------- --%>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<%@ include file="../Consumer/include/StarsHeader.jsp" %>
<jsp:useBean id="purchaseBean" class="com.cannontech.stars.web.bean.PurchaseBean" scope="session"/>

<cti:standardPage title="Energy Services Operations Center" module="stars" htmlLevel="quirks">
	
	<cti:includeCss link="/include/PurpleStyles.css"/>
	<div class="headerbar">
		<%@ include file="include/PurchaseHeaderBar.jspf" %>
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
            <cti:csrfToken/>
	    	<input type="hidden" name="action" value="PurchaseChange"> 
	    	<div align="center">
	    		<span class="title-header">PURCHASE PLAN</span>
			</div>
			<br>
			<br>
			<table id="edittopsection" align="center" width="500" border="0" cellspacing="0" cellpadding="1" class="TableCell">
		    	<tr> 
					<td width="30%" class="SubtitleHeader">Change Plan View:</td>
					<td width="60%">
						<select id="plans" name="plans">
							<c:forEach var="plan" items="${purchaseBean.availablePlans}">
								<option value='<c:out value="${plan.purchaseID}"/>'> <c:out value="${fn:escapeXml(plan.planName)}"/> </option>
							</c:forEach>
						</select>
						<INPUT id="load" type="Button" name="load" value="Load Plan" onclick="loadPlan(this.form)"/>
					</td>
					<td width="10%">
						<INPUT type="Button" name="deletePurchase" value="Delete Plan" onclick="deletePlan(this.form)" />
                    </td>
				</tr>
			 	<tr>
                    <td width="30%"> </td>
					<td width="60%"> </td>
					<td width="10%">
					    <INPUT type="Button" name="new" value="New Plan" onclick="createNewPlan(this.form)" />
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
		                <input type="text" name="name" maxlength="30" size="24" value='<c:out value="${fn:escapeXml(purchaseBean.currentPlan.planName)}"/>'>
		            </td>
		        </tr>
	            <tr> 
	                <td width="20%" class="TableCell"> 
	                  <div align="right">PO Number:</div>
	                </td>
	                <td width="80%"> 
	                  <input type="text" name="poNumber" maxlength="40" size="24" value='<c:out value="${purchaseBean.currentPlan.poDesignation}"/>'>
	                </td>
	          	</tr>
	          	<tr> 
	                <td width="20%" class="TableCell"> 
	                  <div align="right">Accounting Code:</div>
	                </td>
	                <td width="80%"> 
	                  <input type="text" name="accountingCode" maxlength="40" size="24" value='<c:out value="${purchaseBean.currentPlan.accountingCode}"/>'>
	                </td>
	          	</tr>
	          	<tr> 
	                <td width="20%" class="TableCell"> 
	                  <div align="right">Created on:</div>
	                </td>
	                <td width="80%"> 
	                  <div align="left" class="fieldinfo"><cti:formatDate value="${purchaseBean.currentPlan.timePeriod}" type="BOTH"/></div>
	                </td>
		     	</tr>
	        </table>
	        <br>
	        <div id="onlyavailableaftercreation">
		        <table width="500" border="1" cellspacing="0" cellpadding="0" align="center">
	        		<tr> 
	              		<td class="HeaderCell">Assigned Delivery Schedules</td>
	            	</tr>
	        	</table>
	        	<table width="500" border="1" cellspacing="0" cellpadding="5" align="center">
	        		<tr>
	        			<td width="20%">
							<div align="right">
								<INPUT type="Button" name="edit" value="Edit" onclick="loadSchedule(this.form)" />
								<INPUT type="Button" name="delete" value="Remove" onclick="removeSched()"/>
							</div>
						</td> 
						<td width="60%" valign="top" class="TableCell"><br>
		                    <div align="center">
		                    	<select id="schedules" name="schedules" size="5" style="width:250">
		                        	<c:forEach var="sched" items="${purchaseBean.availableSchedules}">
										<option value='<c:out value="${sched.scheduleID}"/>'> <c:out value="${fn:escapeXml(sched.scheduleName)}"/> </option>
									</c:forEach>
								</select>
							</div>
	            		</td>
	                    <td width="20%">
	            			<div align="left">
	            				<INPUT type="Button" name="newAssigned" value="New" onclick="createNewSchedule(this.form)" />
	            			</div>
	            		</td>
	            	</tr>
				</table>
				<br>
				<table width="500" border="1" cellspacing="0" cellpadding="0" align="center">
	        		<tr> 
	              		<td class="HeaderCell">Plan Invoices</td>
	            	</tr>
	        	</table>
	        	<table width="500" border="1" cellspacing="0" cellpadding="5" align="center">
	        		<tr>
	        			<td width="20%">
							<div align="right">
								<INPUT type="Button" name="edit" value="Edit" onclick="loadInvoice(this.form)" />
								<INPUT type="Button" name="delete" value="Remove" onclick="removeInvoice()"/>
							</div>
						</td> 
						<td width="60%" valign="top" class="TableCell"><br>
		                    <div align="center">
		                    	<select id="invoices" name="invoices" size="5" style="width:250">
		                        	<c:forEach var="invoice" items="${purchaseBean.availableInvoices}">
										<option value='<c:out value="${invoice.invoiceID}"/>'> <c:out value="${invoice.invoiceDesignation}"/> </option>
									</c:forEach>
								</select>
							</div>
	            		</td>
	                    <td width="20%">
	            			<div align="left">
	            				<INPUT type="Button" name="newAssigned" value="New" onclick="createNewInvoice(this.form)" />
	            			</div>
	            		</td>
	            	</tr>
				</table>
			</div>
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
				document.getElementById("onlyavailableaftercreation").style.display = "none";
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
		
		function createNewPlan(form)
		{
			form.action.value = "RequestNewPurchasePlan"; 
			form.submit();			
		}
		
		function loadPlan(form)
		{
			form.action.value = "LoadPurchasePlan"; 
			form.submit();	
		}
		
		function deletePlan(form)
		{
			form.action.value = "DeletePurchasePlan"; 
			form.submit();   
		}
		
		function createNewSchedule(form)
		{
			form.action.value = "RequestNewDeliverySchedule"; 
			form.submit();			
		}
		
		function loadSchedule(form)
		{
			var assignList = document.getElementById("schedules");
			
			if (assignList.selectedIndex >= 0) 
			{
				form.action.value = "LoadDeliverySchedule";
				form.submit();
			}
		}
		
		function removeSched()
		{
			var assignList = document.getElementById("schedules");
			var idx = assignList.selectedIndex;
									
			if (assignList.selectedIndex >= 0) 
			{
				assignList.remove(idx);
				
			}
		}
		
		function createNewInvoice(form)
		{
			form.action.value = "RequestNewInvoice"; 
			form.submit();			
		}
		
		function loadInvoice(form)
		{
			var assignList = document.getElementById("invoices");
			
			if (assignList.selectedIndex >= 0) 
			{
				form.action.value = "LoadInvoice";
				form.submit();
			}
		}
		
		function removeInvoice()
		{
			var assignList = document.getElementById("invoices");
			var idx = assignList.selectedIndex;
									
			if (assignList.selectedIndex >= 0) 
			{
				assignList.remove(idx);
				
			}
		}
		
		function back(form)
		{
			<c:choose>
                <c:when test="${purchaseBean.currentPlan.purchaseID == null}">
    				<c:choose>
                        <c:when test="${purchaseBean.numberOfPlans > 0}">
        					loadPlan(form);
        				</c:when>
                        <c:otherwise>
                            location.href='<cti:url value="/dashboard"/>';
                        </c:otherwise>
                    </c:choose>
                </c:when>
			    <c:otherwise>
				    location.href='<cti:url value="/dashboard"/>';
			    </c:otherwise>
            </c:choose>
		}
		
		function prepareSubmit(form) 
		{
			var assignedSchedules = document.getElementById("schedules").options;
			
			for (idx = 0; idx < assignedSchedules.length; idx++) 
			{
				var html = '<input type="hidden" name="schedules" value="' + assignedSchedules[idx].value + '">';
				form.insertAdjacentHTML("beforeEnd", html);
			}
			
			return validate(form);
		}
	
	</script>
</cti:standardPage>          
