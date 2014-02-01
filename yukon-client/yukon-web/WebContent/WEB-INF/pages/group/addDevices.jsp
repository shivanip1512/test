<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>

<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<cti:standardPage module="tools" page="deviceGroups.addDevices">
       
    <c:if test="${not empty param.errorMessage}">
        <div class="error stacked">${param.errorMessage}</div>
    </c:if>
        
    <cti:msg key="yukon.common.device.group.addMultipleDevices" var="title"/>
    <tags:sectionContainer title="${title}">
        <tags:deviceSelection action="/group/editor/addDevicesByCollection" 
            groupDataJson="${groupDataJson}"
            groupName="${groupName}"
            pickerType="devicePicker"/>
    </tags:sectionContainer>
    
    <cti:url var="homeUrl" value="/group/editor/home"><cti:param name="groupName" value="${group.fullName}" /></cti:url>
    <div class="page-action-area"><cti:button nameKey="back" href="${homeUrl}"/></div>
    
</cti:standardPage>