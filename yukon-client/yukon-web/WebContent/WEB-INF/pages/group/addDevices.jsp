<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>

<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<cti:standardPage title="Add multiple devices to group" module="amr">
<cti:standardMenu menuSelection="devicegroups|commander"/>
   	
   	<cti:url var="homeUrl" value="/group/editor/home">
		<cti:param name="groupName" value="${group.fullName}" />
	</cti:url>
   	
   	<cti:breadCrumbs>
	    <cti:crumbLink url="/operator/Operations.jsp" title="Operations Home" />
	    <cti:crumbLink url="${homeUrl}" title="Groups Home" />
	    &gt; Add Devices
	</cti:breadCrumbs>
	
	
	<h2>Group: <a href="${homeUrl}">${fn:escapeXml(group.fullName)}</a></h2>
	<br><br>
  
	<c:if test="${not empty param.errorMessage}">
		<div style="color: red">
			${param.errorMessage}
		</div>
		<br/><br/>
	</c:if>

	<div style="width: 700px">
		
        <cti:msg key="yukon.common.device.group.addMultipleDevices" var="title"/>
		<tags:boxContainer title="${title}" hideEnabled="false">

            <tags:deviceSelection action="/group/editor/addDevicesByCollection" 
                groupDataJson="${groupDataJson}"
                groupName="${groupName}"
                pickerType="devicePicker"/>
		</tags:boxContainer>
	
	</div>
	
</cti:standardPage>