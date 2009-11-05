<%@ taglib tagdir="/WEB-INF/tags" prefix="tags" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib tagdir="/WEB-INF/tags" prefix="ct"%>
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
		    	<td>
		    		<ct:nameValueContainer>
				    	<ct:nameValue name="Account Number" nameColumnWidth="30%">
				    		${meterInfo.accountNumber}
				    	</ct:nameValue>
				    	<ct:nameValueGap gapHeight="6px" />
		    		
				    	<ct:nameValue name="Name">
				    		${meterInfo.name}
				    	</ct:nameValue>
				    	<ct:nameValueGap gapHeight="6px" />
					    
					    <ct:nameValue name="Location Number">
				    		${meterInfo.locationNumber}
				    	</ct:nameValue>
				    	<ct:nameValueGap gapHeight="6px" />
					    
					    <ct:nameValue name="Service Address">
				    		<tags:address address="${address}" />
				    	</ct:nameValue>
				    	<ct:nameValueGap gapHeight="6px" />
					    
					    <ct:nameValue name="Meter Serial Number">
				    		${meterInfo.serialNumber}
				    	</ct:nameValue>
				    	<ct:nameValueGap gapHeight="6px" />
					    
					    <ct:nameValue name="Phone Number">
				    		<cti:formatPhoneNumber value="${phoneInfo.phoneNumber}" htmlEscape="true"/>
				    	</ct:nameValue>
				    	<ct:nameValueGap gapHeight="6px" />
					    
					    <ct:nameValue name="Map Number" nameColumnWidth="30%">
				    		${locationInfo.mapNumber}
				    	</ct:nameValue>
				    	<ct:nameValueGap gapHeight="6px" />
				    </ct:nameValueContainer>
		        </td>
		    </tr>
		    
		</table>
		
	</c:otherwise>
</c:choose>
    
