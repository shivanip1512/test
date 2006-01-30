<%@ taglib uri="http://java.sun.com/jstl/core" prefix="c" %>
<%@ include file="../Consumer/include/StarsHeader.jsp" %>
<jsp:useBean id="wareAdmin" scope="page" class="com.cannontech.stars.web.bean.WarehouseAdminBean" />

<cti:standardPage title="Energy Services Operations Center" module="stars">
	<link rel="stylesheet" href="../../include/PurpleStyles.css" type="text/css">
	
	<div class="headerbar">
		<%@ include file="include/HeaderBar.jsp" %>
	</div>
 	<br clear="all"> 
 	
	<div class="standardpurplesidebox"> </div>

	<div class="standardcentralwhitebody">
		<br clear="all">
		<br clear="all">
		<div align="center"> <br>
            <span class="TitleHeader">ADMINISTRATION - WAREHOUSE</span><br>
            <c:if test="${errorMsg != null}">
				<span class="ErrorMsg"><c:out value="${errorMsg}"/></span><br>
			</c:if>
			<c:if test="${confirmMsg != null}">
				<span class="ConfirmMsg" ><c:out value="${confirmMsg}"/></span><br>
			</c:if>
		</div>
		
		<table width="600" border="1" cellspacing="0" cellpadding="0" align="center">
        	<tr> 
              	<td class="HeaderCell">Edit Warehouse Information</td>
            </tr>
        </table>
        <form name="form1" method="POST" action='<c:out value="${param.contextPath}"/>/servlet/StarsAdmin'>
	        <input type="hidden" name="action" value="UpdateWarehouse">
			<c:set target="${wareAdmin}" property="currentWarehouseID" value="${param.Warehouse}" />
			<input type="hidden" name="WarehouseID" value='<c:out value="${param.Warehouse}"/>'>
	        <table width="600" border="1" cellspacing="0" cellpadding="5" align="center">
	        	<tr>
		        	<td width="90" class="TableCell"> 
		            	<div align="right">Warehouse Name:</div>
		                </td>
		            <td width="210"> 
		                <input type="text" name="warehouseName" maxlength="30" size="24" value='<c:out value="${wareAdmin.currentWarehouse.warehouseName}"/>' onchange="setContentChanged(true)">
		            </td>
		        </tr>
	            <tr> 
	                <td width="90" class="TableCell"> 
	                  <div align="right">Address 1:</div>
	                </td>
	                <td width="210"> 
	                  <input type="text" name="addr1" maxlength="40" size="24" value='<c:out value="${wareAdmin.currentAddress.locationAddress1}"/>' onchange="setContentChanged(true)">
	                </td>
	          	</tr>
	          	<tr> 
	                <td width="90" class="TableCell"> 
	                  <div align="right">Address 2:</div>
	                </td>
	                <td width="210"> 
	                  <input type="text" name="addr2" maxlength="40" size="24" value='<c:out value="${wareAdmin.currentAddress.locationAddress2}"/>' onchange="setContentChanged(true)">
	                </td>
		     	</tr>
		       	<tr> 
	                <td width="90" class="TableCell"> 
	                  <div align="right">City:</div>
	                </td>
	                <td width="210"> 
	                  <input type="text" name="city" maxlength="30" size="24" value='<c:out value="${wareAdmin.currentAddress.cityName}"/>' onchange="setContentChanged(true)">
	                </td>
	           	</tr>
				<tr> 
	                <td width="90" class="TableCell"> 
	                  <div align="right">State:</div>
	                </td>
	                <td width="210"> 
	                  <input type="text" name="state" maxlength="2" size="14" value='<c:out value="${wareAdmin.currentAddress.stateCode}"/>' onchange="setContentChanged(true)">
	                </td>
	          	</tr>
	            <tr> 
	                <td width="90" class="TableCell"> 
	                  <div align="right">Zip:</div>
	                </td>
	                <td width="210"> 
	                  <input type="text" name="zip" maxlength="12" size="14" value='<c:out value="${wareAdmin.currentAddress.zipCode}"/>' onchange="setContentChanged(true)">
	                </td>
	         	</tr>
	         	<tr> 
	            	<td width="90" class="TableCell"> 
	              		<div align="right">Notes:</div>
	            	</td>
	            	<td width="210"> 
	              		<textarea name="notes" rows="3" wrap="soft" cols="28" class = "TableCell" onchange="setContentChanged(true)"><c:out value="${wareAdmin.currentWarehouse.notes}"/></textarea>
	            	</td>
	          	</tr>
			</table>
			
			<table width="600" border="0" cellspacing="0" cellpadding="5" align="center">
		        <tr>
		          <td width="290" align="right"> 
		            <input type="submit" name="Submit" value="Submit">
		          </td>
		          <td width="205"> 
		            <input type="reset" name="Reset" value="Reset" onclick="setContentChanged(false)">
		          </td>
		          <td width="75" align="right"> 
		            <input type="button" name="Back" value="Back" onclick="if (warnUnsavedChanges()) location.href='AdminTest.jsp'">
		          </td>
		        </tr>
		    </table>
        </form>
	</div>



</cti:standardPage>

