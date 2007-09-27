<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<cti:standardPage title="High Bill Complaint Device Selection" module="operations">
<cti:standardMenu/>
<cti:breadCrumbs>
    <cti:crumbLink url="/operator/Operations.jsp" title="Operations Home" />
    <cti:crumbLink url="/spring/csr/search" title="Device Selection" />
    <cti:crumbLink url="/spring/csr/home?deviceId=${deviceId}" title="Device Detail" />
    &gt; High Bill Complaint
</cti:breadCrumbs>

<script type="text/javascript">

	function createLPPoint(url){
		window.location = url;
	}

</script>

	<h2>Device Name: <cti:deviceName deviceId="${deviceId}"></cti:deviceName></h2>
	
	<c:choose>
		<c:when test="${lmPointExists}">
			
			<div style="width: 400px">
				<tags:widget height="65px" bean="meterInformationWidget" identify="false" deviceId="${deviceId}"/>
			</div>	
			<br/><br/>
			<div style="width: 700px">
				<tags:widget bean="profilePeakWidget" identify="false" deviceId="${deviceId}" collectLPVisible="true" highlight="usage,averageUsage" loadProfileRequestOrigin="HBC"/>
			</div>	
			<br/><br/><br/>
		
		</c:when>
		<c:otherwise>
			<c:url var="highBillUrl" value="/spring/csr/highBill">
				<c:param name="deviceId" value="${deviceId}" />
				<c:param name="createLPPoint" value="true" />
			</c:url>
			<cti:deviceName deviceId="${deviceId}"></cti:deviceName> is not configured for 
			High Bill Processing. <input type="button" value="Configure Now" onclick="javascript:createLPPoint('${highBillUrl}')"></input>
		</c:otherwise>
	
	</c:choose>
	
	
	
	
</cti:standardPage>