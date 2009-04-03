<%@ taglib tagdir="/WEB-INF/tags" prefix="tags" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti" %>


<c:choose>

	<c:when test="${msg != null}">
		${msg}
	</c:when>

	<c:otherwise>

		<table class="compactResultsTable">
		
		    <tr>
		        <th colspan="2" align="left">Customer Information:</th>
		    </tr>
		
		    <tr>
		        <td width="30%" class="label">Account Number:</td>
		        <td>${meterInfo.accountNumber}</td>
		    </tr>
		    
		    <tr>
		        <td width="30%" class="label">Name:</td>
		        <td>${meterInfo.name}</td>
		    </tr>
		    
		    <tr>
		        <td width="30%" class="label">Location Number:</td>
		        <td>${meterInfo.locationNumber}</td>
		    </tr>
		    
		    <tr>
		        <td width="30%" class="label">Service Address:</td>
		        <td><tags:address address="${address}" /></td>
		    </tr>
		    
		    <tr>
		        <td width="30%" class="label">Meter Serial Number:</td>
		        <td>${meterInfo.serialNumber}</td>
		    </tr>
		    
		    <tr>
		        <td width="30%" class="label">Phone Number:</td>
		        <td>${phoneInfo.phoneNumber}</td>
		    </tr>
		    
		    <tr>
		        <td width="30%" class="label">Map Number:</td>
		        <td></td>
		    </tr>
		    
		</table>
		
	</c:otherwise>
</c:choose>
    
