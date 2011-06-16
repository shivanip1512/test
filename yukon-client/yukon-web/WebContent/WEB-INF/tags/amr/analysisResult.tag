<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti" %>

<%@ attribute name="data" required="true" type="com.cannontech.common.bulk.model.DeviceArchiveData"%>
<%@ attribute name="width" required="true" description="The width of the bar in pixels"%>

<cti:uniqueIdentifier var="uniqueId" prefix="resultBar_${data.id.paoId}"/>

<c:set var="colorWidth" value="1"/>

<div id="${uniqueId}" style="width: ${width}px;" class="resultBar">
    
    <c:forEach var="readData" items="${data.timeline}">
        <cti:formatInterval var="title" value="${readData.range}" type="DATEHM"/>
        <div class="${readData.color}" style="width:${readData.width}px;" title="${title}"></div>
    </c:forEach>
    
</div>