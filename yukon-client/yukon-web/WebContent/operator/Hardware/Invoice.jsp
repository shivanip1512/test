<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti" %>
<%@ include file="../Consumer/include/StarsHeader.jsp" %>
<jsp:useBean id="purchaseBean" class="com.cannontech.stars.web.bean.PurchaseBean" scope="session"/>

<cti:standardPage title="Energy Services Operations Center" module="stars" htmlLevel="quirks">
	
    <cti:msg key="yukon.common.calendarcontrol.months" var="months"/>
    <cti:msg key="yukon.common.calendarcontrol.days" var="days"/>
    <cti:msg key="yukon.common.calendarcontrol.clear" var="clear"/>
    <cti:msg key="yukon.common.calendarcontrol.close" var="close"/>

    <cti:includeScript link="/JavaScript/calendarControl.js"/>
    <cti:includeCss link="/WebConfig/yukon/styles/calendarControl.css"/>
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
	    	<input type="hidden" name="action" value="InvoiceChange"> 
	    	<div align="center">
	    		<span class="TitleHeader">INVOICE</span>
			</div>
			<br>
			<br>
			<table width="500" border="1" cellspacing="0" cellpadding="0" align="center">
        		<tr> 
              		<td class="HeaderCell">Invoice Details</td>
            	</tr>
        	</table>
			<table width="500" border="1" cellspacing="0" cellpadding="5" align="center">
	        	<tr>
		        	<td width="20%" class="TableCell"> 
		            	<div align="right">Invoice Designation:</div>
		                </td>
		            <td width="80%"> 
		                <input type="text" name="name" maxlength="30" size="24" value='<c:out value="${purchaseBean.currentInvoice.invoiceDesignation}"/>' onchange="setContentChanged(true)">
		            </td>
		        </tr>
	            <tr> 
	                <td width="20%" class="TableCell"> 
	                  	<div align="right">Date Submitted:</div>
	                </td>
	                <td width="80%"> 
	                  <input id="dateSubmitted" type="text" name="dateSubmitted" maxlength="40" size="24" value='<cti:formatDate value="${purchaseBean.currentInvoice.dateSubmitted}" type="DATE"/>' onchange="setContentChanged(true)">
	   				  	<a href="javascript:void(0);" onclick="javascript:showCalendarControl($('dateSubmitted'), '${months}', '${days}', '${clear}', '${close}');"
							onMouseOver="window.status='Date Submitted Calendar';return true;"
							onMouseOut="window.status='';return true;"> <img src="<%= request.getContextPath() %>/WebConfig/yukon/Icons/StartCalendar.png" width="20" height="15" align="absmiddle" border="0"> 
                        </a>
	                </td>
	          	</tr>
	            <tr>
		        	<td width="20%" class="TableCell"> 
		            	<div align="right">Authorized By:</div>
		                </td>
		            <td width="80%">
						<INPUT type="checkbox" name="authorized" value="Authorized" 
							<c:if test="${purchaseBean.isCurrentlyAuthorized}">
								checked="checked"
							</c:if>
						/>
						<input type="text" name="authorizedBy" maxlength="30" size="24" value='<c:out value="${purchaseBean.currentInvoice.authorizedBy}"/>' onchange="setContentChanged(true)">
		            </td>
		        </tr>
                <tr>
                    <td width="20%" class="TableCell"> 
                        <div align="right">Authorization Number:</div>
                        </td>
                    <td width="80%"> 
                        <input type="text" name="authorizedNum" maxlength="30" size="24" value='<c:out value="${purchaseBean.currentInvoice.authorizedNumber}"/>' onchange="setContentChanged(true)">
                    </td>
                </tr>
		      	<tr>
		        	<td width="20%" class="TableCell"> 
		            	<div align="right">Pay Status:</div>
		                </td>
		            <td width="80%">
						<INPUT type="checkbox" name="hasPaid" value="Has Paid" 
							<c:if test="${purchaseBean.hasCurrentlyPaid}">
								checked="checked"
							</c:if>
						/>
						<input id="datePaid" type="text" name="datePaid" maxlength="40" size="24" value='<cti:formatDate value="${purchaseBean.currentInvoice.datePaid}" type="DATE"/>' onchange="setContentChanged(true)">
	   				  	<a href="javascript:void(0);" onclick="javascript:showCalendarControl($('datePaid'), '${months}', '${days}', '${clear}', '${close}');"
							onMouseOver="window.status='Date Paid Calendar';return true;"
							onMouseOut="window.status='';return true;"> <img src="<%= request.getContextPath() %>/WebConfig/yukon/Icons/StartCalendar.png" width="20" height="15" align="absmiddle" border="0"> 
                        </a>
		            </td>
		        </tr>
		        <tr>
		        	<td width="20%" class="TableCell"> 
		            	<div align="right">Total Quantity:</div>
		                </td>
		            <td width="80%"> 
		                <input type="text" name="quantity" maxlength="30" size="24" value='<c:out value="${purchaseBean.currentInvoice.totalQuantity}"/>' onchange="setContentChanged(true)">
		            </td>
		        </tr>
	          	<tr> 
	                <td width="20%" class="TableCell"> 
	                  <div align="right">Filing:</div>
	                </td>
	                <td width="80%"> 
	                  <div align="left" class="fieldinfo">This invoice filed under plan <c:out value="${purchaseBean.currentPlan.planName}"/></div>
	                </td>
		     	</tr>
	        </table>
	        <br>
	        <br>
		    <div id="onlyavailableaftercreation">
		        <table width="500" border="1" cellspacing="0" cellpadding="0" align="center">
	        		<tr> 
	              		<td class="HeaderCell">Assigned Shipments</td>
	            	</tr>
	        	</table>
	        	<table width="500" border="1" cellspacing="0" cellpadding="5" align="center">
	        		<tr>
	        			<td width="40%" valign="top" class="TableCell"> Available Shipments:<br>
                            <div align="center">
                                <select id="allShipments" name="allShipments" size="5" style="width:165">
                                    <c:forEach var="ship" items="${purchaseBean.allUnassignedInvoiceShipments}">
                                        <option value='<c:out value="${ship.shipmentID}"/>'> <c:out value="${ship.shipmentNumber}"/>: #<c:out value="${ship.serialNumberStart}"/> to <c:out value="${ship.serialNumberEnd}"/> </option>
                                    </c:forEach>
                                </select>
                            </div>
                        </td>
	                    <td width="10%" align="center"> 
	                      	<input type="button" id="RemoveButton" name="Remove" value="<<" onclick="removeShipment(this.form)">
	                      	<br>
	                      	<input type="button" id="AddButton" name="Add" value=">>" onclick="addShipment(this.form)">
	                    </td>
	                    <td width="40%" valign="top" class="TableCell"> Assigned to this Invoice:<br>
                            <div align="center">
                                <select id="shipments" name="shipments" size="5" style="width:165">
                                    <c:forEach var="ship" items="${purchaseBean.assignedInvoiceShipments}">
                                        <option value='<c:out value="${ship.shipmentID}"/>'> <c:out value="${ship.shipmentNumber}"/>: #<c:out value="${ship.serialNumberStart}"/> to <c:out value="${ship.serialNumberEnd}"/> </option>
                                    </c:forEach>
                                </select>
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
	                  	<input type="button" name="Back" value="Back To Plan" onclick="back(this.form)">
	              	</td>
	          	</tr>
			</table> 
        </form>
    </div>
    
    <script language="JavaScript">
		function init() 
		{
			<c:if test="${purchaseBean.currentInvoice.invoiceID == null}">
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
		
		function loadShipment(form)
		{
			var assignList = document.getElementById("shipments");
									
			if (assignList.selectedIndex >= 0) 
			{
				form.action.value = "LoadShipmentFromInvoice";
				form.submit();
			}
		}
		
		function addShipment(form) 
		{
			var assignList = document.getElementById("shipments");
			var availList = document.getElementById("allShipments");
						
			if (availList.selectedIndex >= 0) 
			{
				var idx = availList.selectedIndex;
				var selectedText = availList.options[idx].text;
				var selectedID = availList.value;

				var oOption = document.createElement("OPTION");
				assignList.add(oOption);
				oOption.innerText = selectedText;
				oOption.value = selectedID;
				
				if (availList.value >= 0)
					availList.remove(idx);
				setContentChanged(true);
			}
		}

		function removeShipment(form) 
		{
			var assignList = document.getElementById("shipments");
			var availList = document.getElementById("allShipments");
						
			if (assignList.selectedIndex >= 0) 
			{
				var idx = assignList.selectedIndex;
				var selectedText = assignList.options[idx].text;
				var selectedID = assignList.value;

				var oOption = document.createElement("OPTION");
				availList.add(oOption);
				oOption.innerText = selectedText;
				oOption.value = selectedID;
				
				if (assignList.value > 0)
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
			var assignedShipments = document.getElementById("shipments").options;
			
			for (idx = 0; idx < assignedShipments.length; idx++) 
			{
				var html = '<input type="hidden" name="shipments" value="' + assignedShipments[idx].value + '">';
				form.insertAdjacentHTML("beforeEnd", html);
			}
			
			return validate(form);
		}
	
	</script>
</cti:standardPage>          
