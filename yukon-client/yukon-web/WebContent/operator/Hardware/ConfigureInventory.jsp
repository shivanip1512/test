<%@ taglib uri="http://java.sun.com/jstl/core" prefix="c" %>
<%@ include file="../Consumer/include/StarsHeader.jsp" %>
<jsp:useBean id="inventoryBean" class="com.cannontech.stars.web.bean.InventoryBean" scope="session"/>
<jsp:useBean id="configBean" class="com.cannontech.stars.web.bean.ConfigBean" scope="page"/>

<cti:standardPage title="Energy Services Operations Center" module="stars" htmlLevel="quirks">
	
	<link rel="stylesheet" href="../../include/PurpleStyles.css" type="text/css">
	<div class="headerbar">
		<%@ include file="include/HeaderBar.jsp" %>
	</div>
 	<br clear="all"> 
 	
	<div class="standardpurplesidebox"> 
		<% String pageName = "ConfigureInventory.jsp"; %>
		<div align="right">
			<%@ include file="include/Nav.jsp" %>
		</div>
	</div>

	<div class="standardcentralwhitebody">
		<div align="center"> <br>
            <% String header = "DEVICE CONFIGURATION"; %>
            <%@ include file="include/SearchBar.jsp" %>
            <% if (errorMsg != null) out.write("<span class=\"ErrorMsg\">* " + errorMsg + "</span><br>"); %>
            <% if (confirmMsg != null) out.write("<span class=\"ConfirmMsg\">* " + confirmMsg + "</span><br>"); %>
    		<br clear="all">
    	</div>
    	
		<form name="MForm" method="post" action="<%=request.getContextPath()%>/servlet/InventoryManager">
	    	<input type="hidden" name="action" value="ConfigureResultSet">
	    	<input type="hidden" name="saveToBatch" value="false">
			<input type="hidden" name="<%= ServletUtils.CONFIRM_ON_MESSAGE_PAGE %>">				
	    	
	    	<div align="center">
				<span class="ErrorMsg">You have chosen to configure
					<c:out value="${inventoryBean.numberOfRecords}"/>
					devices.  Proceed?
				</span>
			</div>
	    	<table width="80%" border="2" cellspacing="0" cellpadding="0" align="center" class="TableCell">
			  	<tr>
					<c:choose>
						<c:when test="${configBean.hasStaticLoadGroupMapping}"> 
						 	<td> 
			                	<div align="center"> 
			                    	<input type="button" name="ScheduleConfig" value="Save To Batch" onclick="scheduleBatch(this.form)">
			                    </div>
		                    </td> 
	                    </c:when>
	                    <c:otherwise> 	
		                    <td> 
		                      <div align="right"> 
		                        <input type="submit" name="ConfigNow" value="Configure Now">
		                      </div>
		                    </td>
		                    <td> 
		                      <div align="left"> 
		                        <input type="button" name="ScheduleConfig" value="Save To Batch" onclick="scheduleBatch(this.form)">
		                      </div>
		                    </td>
			            </c:otherwise>
			    	</c:choose>
				</tr>
 			</table>
        	<br>
        	<br>
        	<br>
        	<div align="center">
				<span> </span>
			</div>
        	<table width="40%" border="2" cellspacing="0" cellpadding="0" align="center" class="TableCell">
			 	<tr>
					<td width> 
	                	<div align="center"> 
	                    	<input type="button" name="InvenReturn" value="Return to Inventory" onclick="returnToInven(this.form)">
	                    </div>
	                </td>
	           	</tr>
			</table>
        </form>
    </div>
    
    <script language="JavaScript">
		function init()
		{
		}
		
		function returnToFilters(form)
		{
		    form.REDIRECT.value += "/operator/Hardware/Filter.jsp";
		    form.submit();
		}
        
		function returnToInven(form)
		{
			form.REDIRECT.value += "/operator/Hardware/Inventory.jsp";
		    form.submit();
		}
		
		function scheduleBatch(form) 
		{
			form.saveToBatch.value = true;
			form.submit();
		}
	</script>
</cti:standardPage>          
