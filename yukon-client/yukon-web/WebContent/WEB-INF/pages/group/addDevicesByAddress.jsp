<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>

<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<cti:standardPage title="Add multiple devices to group" module="amr">
<cti:standardMenu menuSelection="devicegroups|commander"/>
   	
   	<c:url var="homeUrl" value="/spring/group/home">
		<c:param name="groupName" value="${group.fullName}" />
	</c:url>
   	
   	<cti:breadCrumbs>
	    <cti:crumbLink url="/operator/Operations.jsp" title="Operations Home" />
	    <cti:crumbLink url="${homeUrl}" title="Groups Home" />
	    &gt; Add Devices by Physical Address range
	</cti:breadCrumbs>
	
	<script type="text/javascript">
		
		function addDevicesByAddress(){
		
			var startAddress = $F('startRange');
			var endAddress = parseInt($F('endRange'));
		
			if(startAddress == null || startAddress == '' || (startAddress != parseInt(startAddress))) {
				alert('Please enter an integer Start of Range value');
				$('startRange').focus();
			} else if(endAddress == null || endAddress == '' || (endAddress != parseInt(endAddress))) {
				alert('Please enter an integer End of Range value');
				$('endRange').focus();
			} else if(endAddress <= startAddress) {
				alert('Please enter an End of Range value that is greater than the Start of Range value');
				$('endRange').focus();
			} else {
				return true;
			}
			
			return false;
		}
	
	</script>
	
	<h2>Group: <a href="${homeUrl}">${fn:escapeXml(group.fullName)}</a></h2>
	
	<c:if test="${not empty param.errorMessage}">
		<div style="color: red">
			${param.errorMessage}
		</div>
		<br/><br/>
	</c:if>

	<div style="width: 700px">
		
		<tags:boxContainer title="Add Multiple Devices by physical address range" hideEnabled="false">
			<form id="addByAddressForm" method="post" action="/spring/group/addDevicesByAddressRange">
				<input type="hidden" name="groupName" value="${fn:escapeXml(group.fullName)}" />
				Start of Range: <input type="text" id="startRange" name="startRange" />
				<br/><br/>
				End of Range: <input type="text" id="endRange" name="endRange" />
				<br/><br/>
				<input type="submit" name="submit" value="Add Devices" onclick="return addDevicesByAddress();" />
			</form>
		</tags:boxContainer>
	
	</div>
	
</cti:standardPage>